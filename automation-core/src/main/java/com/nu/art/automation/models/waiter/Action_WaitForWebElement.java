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
package com.nu.art.automation.models.waiter;

import com.nu.art.automation.consts.GetWebElementBy;

import static com.nu.art.automation.consts.StepTypes.Type_WaitForWebElement;

public final class Action_WaitForWebElement
	extends WaitForConditionAction {

	private String webViewId;

	private GetWebElementBy elementBy;

	private String criteria;

	private int index;

	public Action_WaitForWebElement() {super(Type_WaitForWebElement);}

	public Action_WaitForWebElement(String webViewId, GetWebElementBy elementBy, String criteria, int index, int timeout) {
		super(Type_WaitForWebElement, timeout);
		this.webViewId = webViewId;
		this.elementBy = elementBy;
		this.criteria = criteria;
		this.index = index;
	}

	public final String getWebViewId() {
		return webViewId;
	}

	public final void setWebViewId(String webViewId) {
		this.webViewId = webViewId;
	}

	public final GetWebElementBy getElementBy() {
		return elementBy;
	}

	public final void setElementBy(GetWebElementBy elementBy) {
		this.elementBy = elementBy;
	}

	public final String getCriteria() {
		return criteria;
	}

	public final void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public final int getIndex() {
		return index;
	}

	public final void setIndex(int index) {
		this.index = index;
	}
}
