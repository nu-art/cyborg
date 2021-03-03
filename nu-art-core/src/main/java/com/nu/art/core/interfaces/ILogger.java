/*
 * The core of the core of all my projects!
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

package com.nu.art.core.interfaces;

public interface ILogger {

	/*
	 * VERBOSE
	 */
	void logVerbose(String verbose);

	void logVerbose(String verbose, Object... params);

	void logVerbose(Throwable e);

	void logVerbose(String verbose, Throwable e);

	/*
	 * DEBUG
	 */
	void logDebug(String debug);

	void logDebug(String debug, Object... params);

	void logDebug(Throwable e);

	void logDebug(String debug, Throwable e);

	/*
	 * INFO
	 */
	void logInfo(String info);

	void logInfo(String info, Object... params);

	void logInfo(Throwable e);

	void logInfo(String info, Throwable e);

	/*
	 * WARNING
	 */
	void logWarning(String warning);

	void logWarning(String warning, Object... params);

	void logWarning(Throwable e);

	void logWarning(String warning, Throwable e);

	/*
	 * ERROR
	 */
	void logError(String error);

	void logError(String error, Object... params);

	void logError(Throwable e);

	void logError(String error, Throwable e);
}