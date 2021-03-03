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
package com.nu.art.cyborg.automation.core;

import android.os.Handler;
import android.os.Looper;

import com.nu.art.automation.core.AutomationScenario;
import com.nu.art.automation.core.AutomationStep;
import com.nu.art.automation.models.Action_AppCrashedHere;
import com.nu.art.automation.models.Action_Delay;
import com.nu.art.automation.models.Action_OnKey;
import com.nu.art.automation.models.Action_StartActivity;
import com.nu.art.automation.models.device.Action_PressHardButton;
import com.nu.art.automation.models.general.Action_PrintLog;
import com.nu.art.automation.models.validators.Action_TextValidator;
import com.nu.art.automation.models.view.Action_ClearTextInEditText;
import com.nu.art.automation.models.view.Action_ClickOnRecyclerItem;
import com.nu.art.automation.models.view.Action_ClickOnView;
import com.nu.art.automation.models.view.Action_InputTextToEditText;
import com.nu.art.automation.models.view.Action_SelectTextInEditText;
import com.nu.art.automation.models.waiter.Action_WaitForView;
import com.nu.art.automation.models.waiter.Action_WaitForWebElement;
import com.nu.art.automation.models.webView.Action_ClickOnWebElement;
import com.nu.art.automation.models.webView.Action_InputTextToWebElement;
import com.nu.art.belog.Logger;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.cyborg.automation.AndroidR_ClassManager;
import com.nu.art.cyborg.automation.AutomationModule;
import com.nu.art.cyborg.automation.ResourceType;
import com.nu.art.cyborg.automation.exceptions.CyborgAutomationException;
import com.nu.art.cyborg.automation.executors.Executor_Delay;
import com.nu.art.cyborg.automation.executors.Executor_OnCrashHere;
import com.nu.art.cyborg.automation.executors.Executor_OnKeyClick;
import com.nu.art.cyborg.automation.executors.Executor_StartActivity;
import com.nu.art.cyborg.automation.executors.device.Executor_PressHardButton;
import com.nu.art.cyborg.automation.executors.general.BaseAction;
import com.nu.art.cyborg.automation.executors.general.Executor_AbstractStep;
import com.nu.art.cyborg.automation.executors.general.Executor_PrintLog;
import com.nu.art.cyborg.automation.executors.validators.Executor_TextValidator;
import com.nu.art.cyborg.automation.executors.view.Executor_ClearTextInEditText;
import com.nu.art.cyborg.automation.executors.view.Executor_InputTextToEditText;
import com.nu.art.cyborg.automation.executors.view.Executor_OnClick;
import com.nu.art.cyborg.automation.executors.view.Executor_OnRecyclerItemClick;
import com.nu.art.cyborg.automation.executors.view.Executor_SelectTextInEditText;
import com.nu.art.cyborg.automation.executors.waiters.Executor_WaitForView;
import com.nu.art.cyborg.automation.executors.waiters.Executor_WaitForWebElement;
import com.nu.art.cyborg.automation.executors.webView.Executor_ClickOnWebElement;
import com.nu.art.cyborg.automation.executors.webView.Executor_InputTextToWebElement;
import com.nu.art.cyborg.automation.test.R;
import com.nu.art.cyborg.core.CyborgBuilder;
import com.nu.art.cyborg.core.abs.Cyborg;
import com.nu.art.reflection.tools.ReflectiveTools;
import com.nu.art.storage.PreferencesModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AutomationManager
	extends Logger {

	public static final class Item<Type> {

		private final String key;

		public Item(String key) {
			this.key = key;
		}
	}

	private static final int DefaultSleepBetweenExecutions = 100;

	private AndroidR_ClassManager rClassManager;

	private HashMap<Class<? extends AutomationStep>, Class<? extends AutomationStepExecutor<?>>> executionMap = new HashMap<Class<? extends AutomationStep>, Class<? extends AutomationStepExecutor<?>>>();

	private Cyborg cyborg;

	/**
	 * Target application Configuration.
	 */
	private PreferencesModule targetConfiguration;

	private long lastStepExecuted;

	private String runningTag;

	private Handler uiHandler = new Handler(Looper.getMainLooper());

	private HashMap<String, Object> repository = new HashMap<String, Object>();

	private String section;

	final <StepType extends AutomationStep, ExecutorType extends AutomationStepExecutor<StepType>> void registerExecutor(Class<StepType> stepType,
	                                                                                                                     Class<ExecutorType> executerType) {
		executionMap.put(stepType, executerType);
	}

	@SuppressWarnings("unchecked")
	public void init() {
		Thread.currentThread().setName("scenario-executor");
		/*
		 * ToImplement add annotation to the executor, to determine the type of action it handles.
		 */
		registerExecutor(Action_ClickOnView.class, Executor_OnClick.class);
		registerExecutor(Action_WaitForView.class, Executor_WaitForView.class);
		registerExecutor(Action_WaitForWebElement.class, Executor_WaitForWebElement.class);
		registerExecutor(Action_ClickOnRecyclerItem.class, Executor_OnRecyclerItemClick.class);
		registerExecutor(Action_InputTextToWebElement.class, Executor_InputTextToWebElement.class);
		registerExecutor(Action_ClickOnWebElement.class, Executor_ClickOnWebElement.class);
		registerExecutor(Action_PressHardButton.class, Executor_PressHardButton.class);
		registerExecutor(Action_InputTextToEditText.class, Executor_InputTextToEditText.class);
		registerExecutor(Action_ClearTextInEditText.class, Executor_ClearTextInEditText.class);
		registerExecutor(Action_SelectTextInEditText.class, Executor_SelectTextInEditText.class);
		registerExecutor(Action_PrintLog.class, Executor_PrintLog.class);
		registerExecutor(BaseAction.class, Executor_AbstractStep.class);
		registerExecutor(Action_TextValidator.class, Executor_TextValidator.class);

		registerExecutor(Action_OnKey.class, Executor_OnKeyClick.class);
		registerExecutor(Action_AppCrashedHere.class, Executor_OnCrashHere.class);
		registerExecutor(Action_Delay.class, Executor_Delay.class);
		registerExecutor(Action_StartActivity.class, Executor_StartActivity.class);

		rClassManager = new AndroidR_ClassManager(R.class);

		cyborg = CyborgBuilder.getInstance();
		targetConfiguration = cyborg.getModule(PreferencesModule.class);
		AutomationModule targetAutomationModule = cyborg.getModule(AutomationModule.class);
		if (targetAutomationModule == null) {
			throw new BadImplementationException("Cannot connect to an application which does not use the " + AutomationModule.class.getName());
		}
		targetAutomationModule.setAutomated(true);
	}

	@SuppressWarnings("ForLoopReplaceableByForEach")
	public final void executeScenario(AutomationScenario scenario)
		throws CyborgAutomationException {
		AutomationStep[] steps = scenario.getSteps();
		try {
			for (int i = 0; i < steps.length; i++) {
				executeStep(steps[i]);
			}
		} catch (Throwable e) {
			CyborgAutomationException e1 = new CyborgAutomationException("Error in section: " + section, e);
			logError(e1);
			throw e1;
		}
	}

	final Object resolveAsString(String toResolve) {
		String[] keys = getKeys(toResolve);
		for (int i = 0; i < keys.length; i++) {
			toResolve = toResolve.replaceAll("${" + keys[i] + "}", repository.get(keys[i]).toString());
		}
		return toResolve;
	}

	final Object resolveToObject(String toResolve) {
		String[] keys = getKeys(toResolve);
		return repository.get(keys[0]);
	}

	final Object[] resolveToArray(String toResolve) {
		String[] keys = getKeys(toResolve);
		ArrayList<Object> retVal = new ArrayList<Object>();
		for (int i = 0; i < keys.length; i++) {
			retVal.add(repository.get(keys[i]));
		}
		return retVal.toArray(new Object[retVal.size()]);
	}

	@SuppressWarnings("unchecked")
	private <StepType extends AutomationStep> void executeStep(final StepType automationStep)
		throws CyborgAutomationException {
		Class<? extends AutomationStepExecutor<?>> executorType = executionMap.get(automationStep.getClass());

		if (automationStep instanceof BaseAction)
			executorType = Executor_AbstractStep.class;

		if (executorType == null)
			throw new RuntimeException("No executor for " + AutomationStep.class.getSimpleName() + " type '" + automationStep.getClass() + "'!!");

		final AutomationStepExecutor<StepType> executor;

		executor = (AutomationStepExecutor<StepType>) ReflectiveTools.newInstance(executorType);
		executor.setTag(runningTag);
		while (automationStep.getIntervalSinceLastAction() - (System.currentTimeMillis() - lastStepExecuted) > 0) {

			try {
				Thread.sleep(DefaultSleepBetweenExecutions);
			} catch (InterruptedException e) {
				logError(e);
			}
		}

		executor.setUI_Handler(uiHandler);
		executor.setManager(this);
		executor.setUserStep(automationStep);
		lastStepExecuted = System.currentTimeMillis();
		executor.execute();
	}

	public int getId(ResourceType resourceType, String viewConstantName) {
		return rClassManager.getId(resourceType, viewConstantName);
	}

	public void toastOnApplication(final String string) {
		cyborg.toastDebug(string);
	}

	@SuppressWarnings("unchecked")
	final <Type> Type getItem(Item<Type> key) {
		return (Type) repository.get(key.key);
	}

	@SuppressWarnings("unchecked")
	final <Type> void putItem(Item<Type> key, Type value) {
		repository.put(key.key, value);
	}

	private String[] getKeys(String toResolve) {
		Pattern pattern = Pattern.compile("\\$\\{.*?\\}");
		Matcher matcher = pattern.matcher(toResolve);
		ArrayList<String> retVal = new ArrayList<String>();
		while (matcher.find()) {
			String value = matcher.group();
			retVal.add(value);
		}
		return retVal.toArray(new String[retVal.size()]);
	}

	public String getName(ResourceType resourceType, int id) {
		return rClassManager.getName(resourceType, id);
	}

	public String getRunningLogTag() {
		return runningTag;
	}

	public void setRunningLogTag(String runningTag) {
		this.runningTag = runningTag;
	}

	public void setSection(String section) {
		this.section = section;
	}
}
