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
package com.nu.art.automation.models.device;

import com.nu.art.automation.core.AutomationStep;
import com.nu.art.automation.consts.HardButton;

import static com.nu.art.automation.consts.StepTypes.Type_HardButtonPressed;

public final class Action_PressHardButton
	extends AutomationStep {

	private HardButton button;

	public Action_PressHardButton() {super(Type_HardButtonPressed);}

	public Action_PressHardButton(HardButton button) {
		this();
		this.button = button;
	}

	public final HardButton getButton() {
		return button;
	}

	public final void setButton(HardButton button) {
		this.button = button;
	}
}
