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
package com.nu.art.automation.core;

import java.util.Collections;
import java.util.Vector;

import static com.nu.art.automation.consts.StepTypes.Type_Scenario;

public final class AutomationScenario
	extends AutomationStep {

	private static final int MaxRecordBufferSize = 1000;

	private String label;
	private int recordBufferSizeLimit = MaxRecordBufferSize;
	private int bufferSize;
	private Vector<AutomationStep> steps = new Vector<>();

	public AutomationScenario() {
		super(Type_Scenario);
	}

	public final void addStep(AutomationStep automationStep) {
		steps.add(0, automationStep);
		if (steps.size() > 1) {
			long thisAction = automationStep.getTimestamp();
			long lastAction = steps.get(1).getTimestamp();
			long delay = thisAction - lastAction;
			automationStep.setIntervalSinceLastAction(delay);
		}
		if (steps.size() > recordBufferSizeLimit) {
			steps.setSize(recordBufferSizeLimit);
		}
		bufferSize = steps.size();
	}

	public final void addStep_viaCode(AutomationStep automationStep) {
		steps.add(automationStep);
	}

	public final String getLabel() {
		return label;
	}

	public final void setLabel(String label) {
		this.label = label;
	}

	public final int getRecordBufferSizeLimit() {
		return recordBufferSizeLimit;
	}

	public final void setRecordBufferSizeLimit(int recordBufferSizeLimit) {
		this.recordBufferSizeLimit = recordBufferSizeLimit;
	}

	public final int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @return An array of the last amount of steps defined for this scenario. The steps are ordered from last to first.
	 */
	public AutomationStep[] getSteps() {
		return steps.toArray(new AutomationStep[steps.size()]);
	}

	public void reverse() {
		Collections.reverse(steps);
	}
}
