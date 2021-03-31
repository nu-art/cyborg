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

package com.nu.art.automation.consts;

/**
 * Created by TacB0sS on 07/04/2018.
 */

public interface StepTypes {

	String Type_Scenario = "Scenario";

	String Type_Crash = "Crash";

	String Type_HardButtonPressed = "HardButtonPressed";

	String Type_OnKey = "OnKey";

	String Type_StartActivity = "Type_StartActivity";

	String Type_OnString = "OnString";

	String Type_PrintLog = "PrintLog";

	String Type_TextValidator = "TextValidator";

	String Type_ClearEditText = "ClearEditText";

	String Type_ClickOnRecyclerItem = "ClickOnRecyclerItem";

	String Type_OnItemSelected = "OnItemSelected";

	String Type_OnSeekBarChanged = "OnSeekBarChanged";

	String Type_ClickOnListItem = "ClickOnListItem";

	String Type_ClickOnView = "ClickOnView";

	String Type_TypeIntoEditText = "TypeIntoEditText";

	String Type_SelectInEditText = "SelectInEditText";

	String Type_WaitForView = "WaitForView";

	String Type_WaitForWebElement = "WaitForWebElement";
}
