/*
 * automation is the scenario automation testing framework allowing
 * the app to record last user actions, and in case of a crash serialize
 * the scenario into a file..
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
package com.nu.art.automation.models.general;

import com.nu.art.automation.core.AutomationStep;

import static com.nu.art.automation.consts.StepTypes.Type_PrintLog;

public final class Action_PrintLog
	extends AutomationStep {

	private int logLevel;

	private String log;

	private String tag;

	public Action_PrintLog() {
		super(Type_PrintLog);
	}

	public Action_PrintLog(int logLevel, String tag, String log) {
		this();
		this.logLevel = logLevel;
		this.log = log;
		this.tag = tag;
	}

	public final int getLogLevel() {
		return logLevel;
	}

	public final void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public final String getLog() {
		return log;
	}

	public final void setLog(String log) {
		this.log = log;
	}

	public final String getTag() {
		return tag;
	}

	public final void setTag(String tag) {
		this.tag = tag;
	}
}
