/*
 * cyborg-core is an extendable  module based framework for Android.
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

package com.nu.art.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.exceptions.runtime.ImplementationMissingException;
import com.nu.art.core.file.Charsets;
import com.nu.art.core.generics.Processor;
import com.nu.art.core.interfaces.Serializer;
import com.nu.art.core.tools.ExceptionTools;
import com.nu.art.core.tools.FileTools;
import com.nu.art.core.utils.JavaHandler;
import com.nu.art.core.utils.ThreadMonitor.RunnableMonitor;
import com.nu.art.modular.core.Module;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

@SuppressWarnings( {
	                   "unused",
	                   "WeakerAccess"
                   })
public final class PreferencesModule
	extends Module {

	public interface Storage {

		/**
		 * Will clear the preference
		 */
		void clear();

		/**
		 * Will clear the mem cache preference
		 */
		void clearMemCache();

		/**
		 * Will save the preference - synchronously
		 */
		void save();
	}

	public interface StorageListener {

		void onSavingError(IOException e);

		void onLoadingError(IOException e);
	}

	final class StorageImpl
		implements Storage {

		private final HashMap<String, Object> data = new HashMap<>();
		private long lastModified;
		private String name;
		private File storageFile;

		private StorageImpl(String name) {
			this.name = name;
		}

		final StorageImpl setStorageFile(File storageFile) {
			this.storageFile = storageFile;
			return this;
		}

		boolean get(String key, boolean defaultValue) {
			Object value = get(key);
			if (value == null)
				return defaultValue;

			if (value instanceof Boolean)
				return (boolean) value;

			throw new BadImplementationException("Expected type boolean for key: '" + key + "', but was: " + value.getClass());
		}

		long get(String key, long defaultValue) {
			Object value = get(key);
			if (value == null)
				return defaultValue;

			if (value instanceof Number)
				return ((Number) value).longValue();

			throw new BadImplementationException("Expected type long for key: '" + key + "', but was: " + value.getClass());
		}

		int get(String key, int defaultValue) {
			Object value = get(key);
			if (value == null)
				return defaultValue;

			if (value instanceof Number)
				return ((Number) value).intValue();

			throw new BadImplementationException("Expected type int for key: '" + key + "', but was: " + value.getClass());
		}

		float get(String key, float defaultValue) {
			Object value = get(key);
			if (value == null)
				return defaultValue;

			if (value instanceof Number)
				return ((Number) value).floatValue();

			throw new BadImplementationException("Expected type float for key: '" + key + "', but was: " + value.getClass());
		}

		double get(String key, double defaultValue) {
			Object value = get(key);
			if (value == null)
				return defaultValue;

			if (value instanceof Number)
				return ((Number) value).doubleValue();

			throw new BadImplementationException("Expected type double for key: '" + key + "', but was: " + value.getClass());
		}

		final String get(String key, String defaultValue) {
			Object value = get(key);
			if (value == null)
				return defaultValue;

			if (value instanceof String)
				return (String) value;

			throw new BadImplementationException("Expected type String for key: '" + key + "', but was: " + value.getClass());
		}

		final Object get(String key) {
			synchronized (data) {
				return data.get(key);
			}
		}

		final void put(String key, Object value) {
			synchronized (data) {
				data.put(key, value);
			}
			_save();
		}

		final void remove(String key) {
			synchronized (data) {
				data.remove(key);
			}
			_save();
		}

		public final void clear() {
			clearMemCache();
			_save(0);
			savingHandler.post(new Runnable() {
				@Override
				public void run() {
					synchronized (data) {
						lastModified = 0;
					}
				}
			});
		}

		public final void clearMemCache() {
			synchronized (data) {
				if (DebugFlag.isEnabled())
					logInfo("Clearing mem cache for: '" + name + "'");

				data.clear();
			}
		}

		private Runnable save = new RunnableMonitor(new Runnable() {
			@Override
			public void run() {
				try {
					if (DebugFlag.isEnabled())
						logInfo("Saving: " + name);

					HashMap<String, Object> temp;
					synchronized (data) {
						temp = new HashMap<>(data);
					}

					File tempFile = getTempStorageFile();

					FileTools.writeToFile(gson.toJson(temp), tempFile, Charsets.UTF_8);
					FileTools.delete(storageFile);
					FileTools.renameFile(tempFile, storageFile);
					synchronized (data) {
						lastModified = storageFile.lastModified();
					}

					//					logInfo("Saved: " + name);
				} catch (final IOException e) {
					String exception = e.getMessage() + "\n" + ExceptionTools.getStackTrace(e);
					dispatchModuleEvent("Error saving shared preferences '" + name + "' to: " + storageFile.getAbsolutePath() + "\n" + exception, StorageListener.class, new Processor<StorageListener>() {
						@Override
						public void process(StorageListener listener) {
							listener.onSavingError(e);
						}
					});
				}
			}
		});

		private File getTempStorageFile() {
			return new File(storageFile.getParentFile(), storageFile.getName() + ".tmp");
		}

		@Override
		public void save() {
			save.run();
		}

		private void _save() {
			_save(100);
		}

		private void _save(int delay) {
			savingHandler.remove(save);
			savingHandler.post(delay, save);
		}

		@SuppressWarnings("unchecked")
		private void load() {

			synchronized (data) {
				if (!storageFile.exists() || storageFile.lastModified() <= lastModified)
					return;

				clearMemCache();
				lastModified = 0;
			}

			FileReader storageFileReader = null;
			try {
				if (!storageFile.exists()) {
					File tempFile = getTempStorageFile();
					if (!tempFile.exists()) {
						logInfo("No storage file to load");
						return;
					}

					logWarning("storage file did not exist, but could find the temp file... loading preference from temp file");
					FileTools.renameFile(tempFile, storageFile);
				}

				if (DebugFlag.isEnabled())
					logInfo("Loading: " + name);

				storageFileReader = new FileReader(storageFile);
				HashMap map = gson.fromJson(storageFileReader, HashMap.class);
				if (map != null) {
					logInfo("Loaded Storage: " + name + " from: " + storageFile);//, new WhoCalledThis("load storage"));
					synchronized (data) {
						data.putAll(map);
						lastModified = storageFile.lastModified();
					}
				}
			} catch (final IOException e) {
				String exception = e.getMessage() + "\n" + ExceptionTools.getStackTrace(e);
				dispatchModuleEvent("Error loading shared preferences '" + name + "' from: " + storageFile.getAbsolutePath() + "\n" + exception, StorageListener.class, new Processor<StorageListener>() {
					@Override
					public void process(StorageListener listener) {
						listener.onLoadingError(e);
					}
				});
			} finally {
				if (storageFileReader != null) {
					try {
						storageFileReader.close();
					} catch (IOException e) {
						logWarning("Error closing storage file reader", e);
					}
				}
			}
		}
	}

	public static final String DefaultStorageGroup = "DefaultStorage";

	static final String EXPIRES_POSTFIX = "-Expires";

	private Gson gson = new Gson();
	private final HashMap<String, StorageImpl> storageMap = new HashMap<>();
	private JavaHandler savingHandler;
	private File storageDefaultFolder;

	private PreferencesModule() {}

	@Override
	protected void init() {
		if (storageDefaultFolder == null)
			throw new ImplementationMissingException("MUST set storage root folder");

		if (!storageDefaultFolder.exists()) {
			try {
				FileTools.mkDir(storageDefaultFolder);
			} catch (IOException e) {
				throw new ImplementationMissingException("Unable to create root storage folder: " + storageDefaultFolder.getAbsolutePath());
			}
		}

		savingHandler = new JavaHandler().start("shared-preferences");
	}

	public final void defineGroup(String name, File pathToFile) {
		createStorageGroupImpl(name, pathToFile);
	}

	public final void setStorageFolder(String storageFolder) {
		setStorageFolder(new File(storageFolder));
	}

	public final void setStorageFolder(File storageFolder) {
		this.storageDefaultFolder = storageFolder;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public void clear() {
		synchronized (storageMap) {
			for (StorageImpl storage : storageMap.values()) {
				storage.clear();
			}
		}
	}

	public void clearMemCache() {
		synchronized (storageMap) {
			for (StorageImpl storage : storageMap.values()) {
				storage.clearMemCache();
			}
		}
	}

	private StorageImpl createStorageGroupImpl(String name, File pathToFile) {
		if (pathToFile.getParentFile() == null)
			throw new BadImplementationException("Path to storage file MUST contain a parent folder!!");

		StorageImpl prefs = new StorageImpl(name).setStorageFile(pathToFile);
		prefs.load();
		storageMap.put(name, prefs);
		return prefs;
	}

	public final Storage getStorage(String storageGroup) {
		StorageImpl preferences = storageMap.get(storageGroup);
		if (preferences == null) {
			File pathToFile = new File(storageDefaultFolder, storageGroup);
			storageMap.put(storageGroup, preferences = createStorageGroupImpl(storageGroup, pathToFile));
		} else
			preferences.load();

		return preferences;
	}

	public static class JsonSerializer
		extends Serializer<Object, String> {

		public static final JsonSerializer _Serializer = new JsonSerializer();

		public static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

		@Override
		public String serialize(Object o) {
			return gson.toJson(o);
		}

		@Override
		public Object deserialize(String from, Type toType) {
			return gson.fromJson(from, toType);
		}
	}
}
