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

package com.nu.art.belog.loggers;

import com.nu.art.belog.BeConfig;
import com.nu.art.belog.BeConfig.LoggerConfig;
import com.nu.art.belog.BeConfig.Rule;
import com.nu.art.belog.LoggerClient;
import com.nu.art.belog.LoggerDescriptor;
import com.nu.art.belog.consts.LogLevel;
import com.nu.art.belog.loggers.JavaLogger.Config_JavaLogger;

/**
 * Created by TacB0sS on 28-Feb 2017.
 */

public class JavaLogger
	extends LoggerClient<Config_JavaLogger> {

	public static final Rule Rule_AllToJavaLogger = new Rule().setLoggerKeys(Config_JavaLogger.KEY);
	public static final LoggerConfig LogConfig_JavaLogger = new Config_JavaLogger().setKey(Config_JavaLogger.KEY);
	public static final BeConfig Config_FastJavaLogger = new BeConfig().setRules(Rule_AllToJavaLogger).setLoggersConfig(LogConfig_JavaLogger);

	@Override
	protected void log(long timestamp, LogLevel level, Thread thread, String tag, String message, Throwable t) {
		String s = composer.composeEntry(timestamp, level, thread, tag, message, t);
		switch (level) {

			case Verbose:
			case Debug:
			case Info:
				System.out.print(s);
				break;
			case Warning:
			case Error:
			case Assert:
				System.err.print(s);
		}
	}

	public static class JavaLoggerDescriptor
		extends LoggerDescriptor<Config_JavaLogger, JavaLogger> {

		public JavaLoggerDescriptor() {
			super(Config_JavaLogger.KEY, Config_JavaLogger.class, JavaLogger.class);
		}
	}

	public static class Config_JavaLogger
		extends LoggerConfig {

		public static final String KEY = JavaLogger.class.getSimpleName();

		public Config_JavaLogger() {
			super(KEY);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;

			return o != null && getClass() == o.getClass();
		}

		@Override
		public int hashCode() {
			return KEY.hashCode();
		}
	}
}
