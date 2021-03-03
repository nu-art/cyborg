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

package com.nu.art.cyborg.automation.executors.validators;

import com.nu.art.automation.consts.Comparator;
import com.nu.art.automation.models.validators.Action_TextValidator;
import com.nu.art.cyborg.automation.exceptions.CyborgAutomationException;
import com.nu.art.cyborg.automation.executors.view.ViewActionExecutor;

public class Executor_TextValidator
	extends ViewActionExecutor<Action_TextValidator> {

	@Override
	protected void execute()
		throws CyborgAutomationException {
		//		View view = solo.getView(step.getViewConstantName());
		//		String extractedText;
		//		if (view instanceof TextView) {
		//			extractedText = (String) ((TextView) view).getText();
		//		} else if (view instanceof EditText) {
		//			extractedText = ((EditText) view).getText().toString();
		//		} else
		//			throw new CyborgAutomationException("Cannot extract text out of a " + view.getClass().getSimpleName());
		//
		//		boolean valid = false;
		//		switch (step.getComparator()) {
		//			case Equals :
		//			case EqualsOrGreaterThan :
		//			case GreaterThan :
		//			case LesserThan :
		//			case EqualsOrLesserThan :
		//				double extractedValue;
		//				try {
		//					extractedValue = Double.parseDouble(extractedText);
		//				} catch (NumberFormatException e) {
		//					throw new CyborgAutomationException("Error parsing extracted value", e);
		//				}
		//				double expectedValue;
		//				try {
		//					expectedValue = Double.parseDouble(step.getExpectedValue());
		//				} catch (NumberFormatException e) {
		//					throw new CyborgAutomationException("Error parsing expected value", e);
		//				}
		//				valid = compareValue(step.getComparator(), extractedValue, expectedValue);
		//				break;
		//
		//			case Contains :
		//				valid = extractedText.contains(extractedText);
		//				break;
		//
		//			case Matches :
		//				valid = extractedText.equals(extractedText);
		//				break;
		//		}
		//
		//		if (!valid)
		//			throw new CyborgAutomationException("Unable to validate: " + extractedText + " !" + step.getComparator() + " "
		//					+ step.getExpectedValue());
	}

	private boolean compareValue(Comparator comparator, double extractedValue, double expectedValue) {
		switch (comparator) {
			case Equals:
				return extractedValue == expectedValue;
			case EqualsOrGreaterThan:
				return extractedValue >= expectedValue;
			case GreaterThan:
				return extractedValue > expectedValue;
			case LesserThan:
				return extractedValue < expectedValue;
			case EqualsOrLesserThan:
				return extractedValue <= expectedValue;
			default:
				return false;
		}
	}
}
