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

import com.nu.art.belog.consts.LogLevel;
import com.nu.art.core.interfaces.ILogger;
import com.nu.art.core.tools.ArrayTools;

import static com.nu.art.belog.consts.LogLevel.Debug;
import static com.nu.art.belog.consts.LogLevel.Error;
import static com.nu.art.belog.consts.LogLevel.Info;
import static com.nu.art.belog.consts.LogLevel.Verbose;
import static com.nu.art.belog.consts.LogLevel.Warning;

/**
 * Created by TacB0sS on 27-Feb 2017.
 */

public class Logger
	implements ILogger {

	private transient BeLogged beLogged;

	private String tag = "NotSet";

	private boolean enable = true;

	{
		String fqn = getClass().getName();
		tag = fqn.substring(fqn.lastIndexOf(".") + 1);
	}

	private LogLevel minLogLevel = Verbose;

	protected Logger() {
		beLogged = BeLogged.getInstance();
	}

	public Logger setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public String getTag() {
		return tag;
	}

	public void setMinLogLevel(LogLevel minLogLevel) {
		this.minLogLevel = minLogLevel;
	}

	private boolean canLog(LogLevel logLevelToLog) {
		return logLevelToLog.ordinal() >= minLogLevel.ordinal() && isLoggerEnabled();
	}

	public void setLoggerEnable(boolean enable) {
		this.enable = enable;
	}

	protected boolean isLoggerEnabled() {
		return enable;
	}

	public void log(LogLevel level, String message) {
		finalLog(level, message);
	}

	public void log(LogLevel level, Throwable e) {
		finalLog(level, null, e);
	}

	public void log(LogLevel level, String message, Throwable e) {
		finalLog(level, message, e);
	}

	public void log(LogLevel level, String message, Object... params) {
		finalLog(level, message, params);
	}

	private void finalLog(LogLevel level, String message, Object... params) {
		if (!canLog(level))
			return;

		Throwable t = null;
		Object lastParam = null;
		if (params.length > 0) {
			lastParam = params[params.length - 1];
			if (lastParam instanceof Throwable)
				t = (Throwable) lastParam;
		}

		try {
			beLogged.log(level, tag, message, params, t);
		} catch (Exception e) {
			if (lastParam == t && t != null)
				try {
					beLogged.log(level, tag, message, ArrayTools.removeElement(params, t), t);
					e = null;
				} catch (Exception e1) {
					e = e1;
				}

			if (e != null)
				beLogged.log(Error, tag, "Error formatting string: " + message + ", with params: " + ArrayTools.printGenericArray("", -1, params), null, e);
		}
	}

	public boolean runtimeDebuggingLog(String log) {
		logDebug(log);
		return false;
	}

	/*
	 * VERBOSE
	 */
	@Override
	public void logVerbose(String verbose) {
		log(Verbose, verbose);
	}

	@Override
	public void logVerbose(String verbose, Object... params) {
		log(Verbose, verbose, params);
	}

	@Override
	public void logVerbose(Throwable e) {
		log(Verbose, e);
	}

	@Override
	public void logVerbose(String verbose, Throwable e) {
		log(Verbose, verbose, e);
	}

	/*
	 * DEBUG
	 */
	@Override
	public void logDebug(String debug) {
		log(Debug, debug);
	}

	@Override
	public void logDebug(String debug, Object... params) {
		log(Debug, debug, params);
	}

	@Override
	public void logDebug(Throwable e) {
		log(Debug, e);
	}

	@Override
	public void logDebug(String debug, Throwable e) {
		log(Debug, debug, e);
	}

	/*
	 * INFO
	 */
	@Override
	public void logInfo(String info) {
		log(Info, info);
	}

	@Override
	public void logInfo(String info, Object... params) {
		log(Info, info, params);
	}

	@Override
	public void logInfo(Throwable e) {
		log(Info, e);
	}

	@Override
	public void logInfo(String info, Throwable e) {
		log(Info, info, e);
	}

	/*
	 * WARNING
	 */
	@Override
	public void logWarning(String warning) {
		log(Warning, warning);
	}

	@Override
	public void logWarning(String warning, Object... params) {
		log(Warning, warning, params);
	}

	@Override
	public void logWarning(Throwable e) {
		log(Warning, e);
	}

	@Override
	public void logWarning(String warning, Throwable e) {
		log(Warning, warning, e);
	}

	/*
	 * ERROR
	 */
	@Override
	public void logError(String error) {
		log(Error, error);
	}

	@Override
	public void logError(String error, Object... params) {
		log(Error, error, params);
	}

	@Override
	public void logError(Throwable e) {
		log(Error, e);
	}

	@Override
	public void logError(String error, Throwable e) {
		log(Error, error, e);
	}
}
