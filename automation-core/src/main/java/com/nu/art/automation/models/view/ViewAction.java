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

package com.nu.art.automation.models.view;

import com.nu.art.automation.core.AutomationStep;

public class ViewAction
	extends AutomationStep {

	private String viewConstantName;

	public ViewAction(String key) {super(key);}

	public ViewAction(String key, String viewConstantName) {
		this(key);
		this.viewConstantName = viewConstantName;
	}

	public final String getViewConstantName() {
		return viewConstantName;
	}

	public final void setViewConstantName(String viewConstantName) {
		this.viewConstantName = viewConstantName;
	}
}
