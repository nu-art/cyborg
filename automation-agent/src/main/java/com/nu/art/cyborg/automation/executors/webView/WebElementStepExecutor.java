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

package com.nu.art.cyborg.automation.executors.webView;

import com.nu.art.automation.models.webView.WebElementAction;
import com.nu.art.cyborg.automation.executors.view.ViewActionExecutor;
import com.robotium.solo.By;

public abstract class WebElementStepExecutor<Action extends WebElementAction>
	extends ViewActionExecutor<Action> {

	protected final By getBy() {
		return super.getBy(step.getElementBy(), step.getCriteria());
	}

	//	protected final ArrayList<WebElement> getCurrentWebElements() {
	//		By by = getBy();
	//		logInfo("Fetching a WebElements List by " + step.getElementBy() + ": " + step.getCriteria());
	//		if (by == null)
	//			return solo.getCurrentWebElements();
	//		return solo.getCurrentWebElements(by);
	//	}
	//
	//	protected final WebElement getWebElement() {
	//		logInfo("Fetching a WebElement by " + step.getElementBy() + ": " + step.getCriteria() + ", index="
	//				+ step.getIndex());
	//
	//		return solo.getWebElement(getBy(), step.getIndex());
	//	}
}
