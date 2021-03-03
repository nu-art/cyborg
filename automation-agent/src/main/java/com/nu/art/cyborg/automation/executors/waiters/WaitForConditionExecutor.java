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
package com.nu.art.cyborg.automation.executors.waiters;

import com.nu.art.cyborg.automation.core.AutomationStepExecutor;
import com.nu.art.cyborg.automation.exceptions.CyborgAutomationException;
import com.nu.art.automation.models.waiter.WaitForConditionAction;

public abstract class WaitForConditionExecutor<ConditionAction extends WaitForConditionAction>
	extends AutomationStepExecutor<ConditionAction> {

	@Override
	protected final void execute()
		throws CyborgAutomationException {
		long duration = System.currentTimeMillis();
		boolean conditionMeet = false;
		while (System.currentTimeMillis() - duration < step.getTimeout()) {
			if (checkCondition()) {
				conditionMeet = true;
				break;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!conditionMeet)
			throw new CyborgAutomationException("Could not validate condition: " + step);
	}

	protected abstract boolean checkCondition();
}
