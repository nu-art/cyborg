/*
 * belog is an extendable infrastructure to manage and customize
 * your application output.
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nu.art.belog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.nu.art.belog.BeConfig.LoggerConfig;
import com.nu.art.belog.BeConfig.Rule;
import com.nu.art.belog.consts.LogLevel;
import com.nu.art.belog.loggers.FileLogger.FileLoggerDescriptor;
import com.nu.art.belog.loggers.JavaLogger.JavaLoggerDescriptor;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.exceptions.runtime.ImplementationMissingException;
import com.nu.art.core.interfaces.Condition;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.interfaces.Serializer;
import com.nu.art.core.replacer.Replacer;
import com.nu.art.core.tools.ArrayTools;
import com.nu.art.core.tools.StreamTools;
import com.nu.art.core.utils.SynchronizedObject;
import com.nu.art.reflection.tools.ART_Tools;
import com.nu.art.reflection.tools.ReflectiveTools;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class BeLogged {

	private static BeLogged INSTANCE;

	public static synchronized BeLogged getInstance() {
		if (INSTANCE == null)
			INSTANCE = new BeLogged();

		return INSTANCE;
	}

	private LogLevel minLogLevel = LogLevel.Verbose;
	private LogLevel maxLogLevel = LogLevel.Assert;
	private SynchronizedObject<HashSet<String>> syncLoggerSet = new SynchronizedObject<>(new Getter<HashSet<String>>() {
		@Override
		public HashSet<String> get() {
			return new HashSet<>();
		}
	});

	private final Map<String, LoggerDescriptor<?, ? extends LoggerClient<? extends LoggerConfig>>> descriptors = new HashMap<>();
	private final HashMap<String, String> configParams = new HashMap<>();
	private final Map<String, LoggerClient> logClients = new ConcurrentHashMap<>();
	private Rule[] rules = {};
	private Serializer<Object, String> serializer = new Serializer<Object, String>() {

		private Gson gson = new GsonBuilder().registerTypeAdapter(LoggerConfig.class, new JsonDeserializer<LoggerConfig>() {
			@Override
			public LoggerConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
				JsonElement typeElement = json.getAsJsonObject().get("type");
				String type = typeElement.getAsString();
				LoggerDescriptor<?, ? extends LoggerClient<? extends LoggerConfig>> loggerDescriptor = descriptors.get(type);
				if (loggerDescriptor == null)
					throw new ImplementationMissingException("No descriptor was defined for type: " + type);

				LoggerConfig config = context.deserialize(json, loggerDescriptor.configType);

				Field[] fields = ART_Tools.getAllFieldsInHierarchy(config.getClass(), new Condition<Field>() {
					@Override
					public boolean checkCondition(Field field) {
						return !Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers());
					}
				});

				for (Field field : fields) {
					if (field.getType() != String.class)
						continue;

					try {
						boolean accessible = field.isAccessible();
						if (!accessible)
							field.setAccessible(true);

						String value = (String) field.get(config);
						value = Replacer.Replacer.replace(value, configParams);
						field.set(config, value);

						if (!accessible)
							field.setAccessible(false);
					} catch (IllegalAccessException e) {
						throw new BadImplementationException("Error updating field value '" + field + "' in config type '" + config.getClass() + "'", e);
					}
				}

				return config;
			}
		}).create();

		@Override
		public String serialize(Object o) {
			return gson.toJson(o);
		}

		@Override
		public Object deserialize(String s, Type toType) {
			return gson.fromJson(s, toType);
		}
	};

	private BeLogged() {
		registerDescriptor(new FileLoggerDescriptor());
		registerDescriptor(new JavaLoggerDescriptor());
	}

	public void setMinLogLevel(LogLevel minLogLevel) {
		this.minLogLevel = minLogLevel;
	}

	public void setMaxLogLevel(LogLevel maxLogLevel) {
		this.maxLogLevel = maxLogLevel;
	}

	public final Serializer<Object, String> getSerializer() {
		return serializer;
	}

	public final void addConfigParam(String key, String value) {
		configParams.put(key, value);
	}

	public final <Config extends LoggerConfig> void registerDescriptor(LoggerDescriptor<Config, ? extends LoggerClient<Config>> descriptor) {
		descriptors.put(descriptor.key, descriptor);
	}

	public final void setConfig(InputStream configAsStream)
		throws IOException {
		setConfig(StreamTools.readFullyAsString(configAsStream));
	}

	public final void setConfig(String configAsString) {
		setConfig((BeConfig) serializer.deserialize(configAsString, BeConfig.class));
	}

	public final void setConfig(BeConfig _config) {
		if (_config.rules == null)
			throw new BadImplementationException("what is the point in having no rules??");

		for (LoggerConfig config : _config.configs) {
			if (config.key == null)
				throw new BadImplementationException("logger MUST have a key!!");

			for (LoggerConfig otherConfig : _config.configs) {
				if (otherConfig == config)
					continue;

				if (config.key.equals(otherConfig.key))
					throw new BadImplementationException("Logger key MUST be unique!! Found two entries with key: " + config.key);
			}
		}

		/*
			if a log config already in use in the runtime belogged instance, there is no point in disposing and re-initializing it
		 */
		ArrayList<LoggerConfig> configsToAdd = new ArrayList<>(Arrays.asList(_config.configs));
		Map<String, LoggerClient> logClients = this.logClients;
		for (String s : logClients.keySet()) {
			LoggerClient loggerClient = logClients.get(s);
			if (loggerClient == null)
				continue;

			int configIndex = configsToAdd.indexOf(loggerClient.config);
			if (configIndex > -1) {
				loggerClient.config = configsToAdd.get(configIndex);
				configsToAdd.remove(configIndex);
				continue;
			}

			loggerClient = logClients.remove(s);
			loggerClient.dispose();
		}

		ArrayList<String> _defaultLoggers = new ArrayList<>();
		for (LoggerConfig config : configsToAdd) {
			LoggerClient loggerClient = createLoggerFromConfig(config);
			this.logClients.put(config.key, loggerClient);

			if (config.isDefault)
				_defaultLoggers.add(config.key);
		}

		String[] defaultLoggers = ArrayTools.asArray(_defaultLoggers, String.class);
		for (Rule rule : _config.rules) {
			if (rule.thread != null)
				rule._thread = Pattern.compile(rule.thread, Pattern.CASE_INSENSITIVE);

			if (rule.tag != null)
				rule._tag = Pattern.compile(rule.tag, Pattern.CASE_INSENSITIVE);

			if (rule.loggerKeys != null && rule.loggerKeys.length > 0)
				continue;

			rule.loggerKeys = defaultLoggers;
		}

		this.rules = _config.rules;
	}

	@SuppressWarnings("unchecked")
	public <Config extends LoggerConfig> LoggerClient<Config> createLoggerFromConfig(Config config) {
		LoggerDescriptor<Config, LoggerClient<Config>> validator = (LoggerDescriptor<Config, LoggerClient<Config>>) descriptors.get(config.type);
		if (validator == null)
			throw new ImplementationMissingException("Could not find descriptor for config of type: " + config.type);

		validator.validateConfig(config);

		LoggerClient<Config> logger = ReflectiveTools.newInstance(validator.loggerType);
		logger.setConfig(config);
		logger.init();
		return logger;
	}

	final void log(final LogLevel level, final String tag, final String message, final Object[] params, final Throwable t) {
		if (!(level.ordinal() >= minLogLevel.ordinal() && level.ordinal() <= maxLogLevel.ordinal()))
			return;

		Thread thread = Thread.currentThread();
		String formattedMessage = params == null || params.length == 0 ? message : null;

		HashSet<String> used = syncLoggerSet.get();
		for (Rule rule : rules) {
			if (!(level.ordinal() >= rule.minLevel.ordinal() && level.ordinal() <= rule.maxLevel.ordinal()))
				continue;

			if (rule._thread != null && !rule._thread.matcher(thread.getName()).matches())
				continue;

			if (rule._tag != null && !rule._tag.matcher(tag).matches())
				continue;

			if (formattedMessage == null && message != null)
				formattedMessage = String.format(message, params);

			long timestamp = System.currentTimeMillis();
			for (String loggerKey : rule.loggerKeys) {
				if (used.contains(loggerKey))
					continue;

				LoggerClient client = logClients.get(loggerKey);
				if (client == null)
					continue;

				client._log(timestamp, level, thread, tag, formattedMessage, t);
				used.add(loggerKey);
			}
		}
		used.clear();
	}

	public final Logger getLogger(Object objectForTag) {
		String tag;
		if (objectForTag instanceof String)
			tag = (String) objectForTag;
		else if (objectForTag instanceof Class)
			tag = ((Class) objectForTag).getSimpleName();
		else
			tag = objectForTag.getClass().getSimpleName();
		return new Logger().setTag(tag);
	}

	public LoggerClient getClient(String loggerKey) {
		return logClients.get(loggerKey);
	}

	public LoggerClient[] getClients() {
		return ArrayTools.asArray(logClients.values(), LoggerClient.class);
	}
}
