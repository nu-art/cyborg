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
package com.nu.art.cyborg.automation.executors.view;

import com.nu.art.automation.models.view.Action_InputTextToEditText;
import com.nu.art.cyborg.automation.core.AutomationStepExecutor;

public final class Executor_InputTextToEditText
	extends AutomationStepExecutor<Action_InputTextToEditText> {

	@Override
	protected void execute() {
		//		logInfo("Insert text '" + step.getText() + "' to view with id: " + step.getViewConstantName());
		//		EditText editText = (EditText) solo.getView(step.getViewConstantName());
		//		solo.typeText(editText, step.getText());
	}
}
