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

/**
 * This is a core element of a group of tools which will be used to manage model changes in a UI action.
 *
 * @author TacB0sS
 */
public interface ProgressNotifier {

	/**
	 * Reports the ending of the progress.
	 */
	void onCopyEnded();

	/**
	 * @param e The exception thrown while in progress.
	 */
	void onCopyException(Throwable e);

	/**
	 * @param percentages The current progress percentage where: <b>0 &lt;= progress &lt;= 1</b>.
	 */
	void onProgressPercentage(double percentages);

	/**
	 * Reports the beginning of the progress.
	 */
	void onCopyStarted();

	/**
	 * @param report A meaningful message of the progress.
	 */
	void reportState(String report);
}
