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
import com.nu.art.belog.loggers.FileLogger.Config_FileLogger;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.exceptions.runtime.BugSerachException;
import com.nu.art.core.tools.ArrayTools;
import com.nu.art.core.tools.FileTools;
import com.nu.art.core.tools.SizeTools;
import com.nu.art.core.utils.InstanceRecycler;
import com.nu.art.core.utils.InstanceRecycler.Instantiator;
import com.nu.art.core.utils.PoolQueue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FileLogger
	extends LoggerClient<Config_FileLogger> {

	public static final Rule Rule_AllToFileLogger = new Rule().setLoggerKeys(Config_FileLogger.KEY);
	public static final Config_FileLogger LogConfig_FileLogger = (Config_FileLogger) new Config_FileLogger().setKey(Config_FileLogger.KEY);
	public static final BeConfig Config_FastFileLogger = new BeConfig().setRules(Rule_AllToFileLogger).setLoggersConfig(LogConfig_FileLogger);

	private Throwable failure;
	private boolean enable = true;

	private volatile OutputStreamWriter logWriter;

	private long written;

	private PoolQueue<LogEntry> queue = new PoolQueue<LogEntry>() {
		@Override
		protected void onExecutionError(LogEntry item, Throwable e) {
			logError("Error writing log: " + item, e);
		}

		@Override
		protected void executeAction(LogEntry logEntry) {
			try {
				String logMessage = composer.composeEntry(logEntry.timestamp, logEntry.level, logEntry.thread, logEntry.tag, logEntry.message, logEntry.t);
				try {
					logWriter.write(logMessage);
					logWriter.flush();
				} catch (Exception e) {
					disable(new BugSerachException("Error writing log to file", e));
					return;
				}

				written += logMessage.getBytes().length;
				if (written >= config.size) {
					try {
						rotate();
					} catch (Exception e) {
						disable(new BugSerachException("Error rotating files", e));
					}
				}
			} finally {
				recycler.recycle(logEntry);
				if (queue.getItemsCount() == 0 && !enable)
					queue.kill();
			}
		}
	};

	private FileLoggerRotationListener postRotationListener;

	/**
	 * Use {@link #setConfig(LoggerConfig)} instead
	 */
	@Deprecated
	public FileLogger set(File logFolder, String fileNamePrefix, long maxFileSize, int filesCount) {
		Config_FileLogger config = new Config_FileLogger();
		config.count = filesCount;
		config.size = maxFileSize;
		config.fileName = fileNamePrefix;
		config.folder = logFolder.getAbsolutePath();
		this.setConfig(config);
		return this;
	}

	@Override
	protected void dispose() {
		enable = false;
	}

	private void disable(Throwable t) {
		logError("DISABLING FILE LOGGER");
		logError(t.getMessage());
		t.printStackTrace();
		failure = t;
		enable = false;
	}

	@Override
	protected void init() {
		try {
			FileTools.mkDir(config.folder);
		} catch (IOException e) {
			disable(e);
			return;
		}

		File logFile = getLogTextFile(0);
		try {
			createLogWriter(logFile);
		} catch (IOException e) {
			disable(new BugSerachException("Cannot create new logWriter for file: " + logFile.getAbsolutePath(), e));
		}

		logInfo("initializing");
		// Starting the queue after the setup is completed
		queue.createThreads("File logger", 1);
	}

	public void rotate()
		throws IOException {
		logInfo("rotating files");

		FileTools.delete(getLogZipFile(config.count - 1));

		for (int i = config.count - 2; i >= 0; i--) {
			rotateFile(i);
		}

		File file = getLogTextFile(0);
		FileTools.delete(file);
		createLogWriter(file);

		if (postRotationListener != null)
			postRotationListener.onLogFileRotated(this, getLogZipFile(1));
	}

	private void createLogWriter(File logFile)
		throws IOException {
		if (logFile.exists())
			FileTools.createNewFile(logFile);

		written = logFile.length();
		OutputStreamWriter oldLogWriter = this.logWriter;
		logWriter = new OutputStreamWriter(new FileOutputStream(logFile, true));

		if (oldLogWriter == null)
			return;

		try {
			oldLogWriter.flush();
			oldLogWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void rotateFile(int index) {
		File logTextFile = getLogTextFile(index);
		File logZipFile = getLogZipFile(index);
		File newLogZipFile = getLogZipFile(index + 1);
		//        logInfo("Rotating file from: " + index + " ==> " + (index + 1) + "\n");

		try {
			if (!logTextFile.exists() && !logZipFile.exists())
				return;

			if (logTextFile.exists())
				FileTools.archive(logZipFile, logTextFile);

			FileTools.renameFile(logZipFile, newLogZipFile);
		} catch (Exception e) {
			logError("Cannot rotate file from: " + logZipFile.getName() + " ==> " + newLogZipFile.getName() + "\n");
			disable(e);
		}
	}

	private File getLogTextFile(int i) {
		return getFile(i, "txt");
	}

	private File getLogZipFile(int i) {
		return getFile(i, "zip");
	}

	private File getFile(int i, String suffix) {
		return new File(config.folder, config.fileName + "-" + getIndexAsString(i) + "." + suffix);
	}

	private String getIndexAsString(int index) {
		int numDigits = (config.count + "").length();
		int missingZeros = numDigits - (index + "").length();

		String toRet = "";
		for (int i = 0; i < missingZeros; i++) {
			toRet += "0";
		}
		toRet += index;
		return toRet;
	}

	public final File[] getAllLogFiles() {
		List<File> filesToZip = new ArrayList<>();
		for (int i = 0; i < config.count; i++) {
			File file = getLogTextFile(i);
			if (file.exists())
				filesToZip.add(file);

			file = getLogZipFile(i);
			if (file.exists())
				filesToZip.add(file);
		}

		return ArrayTools.asArray(filesToZip, File.class);
	}

	private InstanceRecycler<LogEntry> recycler = new InstanceRecycler<>(new Instantiator<LogEntry>() {
		@Override
		public LogEntry create() {
			return new LogEntry();
		}
	});

	@Override
	protected void log(long timestamp, LogLevel level, Thread thread, String tag, String message, Throwable t) {
		if (!enable)
			return;

		LogEntry instance = recycler.getInstance().set(timestamp, level, thread, tag, message, t);
		queue.addItem(instance);
	}

	public static class FileLoggerDescriptor
		extends LoggerDescriptor<Config_FileLogger, FileLogger> {

		public FileLoggerDescriptor() {
			super(Config_FileLogger.KEY, Config_FileLogger.class, FileLogger.class);
		}

		public Class<Config_FileLogger> getConfigType() {
			return Config_FileLogger.class;
		}

		@Override
		protected void validateConfig(Config_FileLogger config) {
			if (config.folder == null)
				throw new BadImplementationException("No output folder specified of logger: " + config.key);

			if (config.size < 10 * SizeTools.KiloByte)
				throw new BadImplementationException("File size MUST be >= 10kb");

			if (config.count < 3)
				throw new BadImplementationException("Rotation count MUST be >= 3");

			if (config.fileName == null)
				config.fileName = "logger-" + config.key;
		}
	}

	public static class Config_FileLogger
		extends LoggerConfig {

		public static final String KEY = FileLogger.class.getSimpleName();

		String folder;
		String fileName;
		long size = 10 * SizeTools.MegaByte;
		int count = 10;

		public Config_FileLogger() {
			super(KEY);
		}

		public Config_FileLogger setFolder(String folder) {
			this.folder = folder;
			return this;
		}

		public Config_FileLogger setFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public Config_FileLogger setCount(int count) {
			this.count = count;
			return this;
		}

		public Config_FileLogger setSize(long size) {
			this.size = size;
			return this;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Config_FileLogger that = (Config_FileLogger) o;

			if (!key.equals(that.key))
				return false;

			if (folder != null ? !folder.equals(that.folder) : that.folder != null)
				return false;

			return fileName != null ? fileName.equals(that.fileName) : that.fileName == null;
		}

		@Override
		public int hashCode() {
			int result = folder != null ? folder.hashCode() : 0;
			result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
			return result;
		}

		@Override
		@SuppressWarnings("MethodDoesntCallSuperMethod")
		public Config_FileLogger clone() {
			return new Config_FileLogger().setFileName(fileName).setFolder(folder).setCount(count).setSize(size);
		}
	}

	public void logInfo(String log) {
		System.out.println("FileLogger '" + config.key + "' (" + Thread.currentThread().getName() + "): " + log);
	}

	public void logError(String log) {
		logError(log, null);
	}

	public void logError(String log, Throwable e) {
		System.err.println("FileLogger '" + config.key + "' (" + Thread.currentThread().getName() + "): " + log);
		if (e != null)
			e.printStackTrace();
	}

	public interface FileLoggerRotationListener {

		/**
		 * ***<b>Any work with the log file should be offloaded to a new thread, instead of the Belog thread.</b>***
		 *
		 * @param fileLogger  The FileLogger object that dispatched the rotation event.
		 * @param rotatedFile The file that just got filled up with log lines over the allowed size aka the "last fully baked" log file.
		 */
		void onLogFileRotated(FileLogger fileLogger, File rotatedFile);
	}

	public void setPostRotationListener(FileLoggerRotationListener postRotationListener) {
		this.postRotationListener = postRotationListener;
	}
}