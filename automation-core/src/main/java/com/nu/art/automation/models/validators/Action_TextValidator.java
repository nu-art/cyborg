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

package com.nu.art.automation.models.validators;

import com.nu.art.automation.models.view.ViewAction;
import com.nu.art.automation.consts.Comparator;

import static com.nu.art.automation.consts.StepTypes.Type_TextValidator;

public class Action_TextValidator
	extends ViewAction {

	private Comparator comparator;

	private String value;

	public Action_TextValidator() {super(Type_TextValidator);}

	public Action_TextValidator(String viewId, Comparator comparator, String value) {
		super(Type_TextValidator, viewId);
		this.comparator = comparator;
		this.value = value;
	}

	public final Comparator getComparator() {
		return comparator;
	}

	public final void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	public final String getExpectedValue() {
		return value;
	}

	public final void setValue(String value) {
		this.value = value;
	}
}
