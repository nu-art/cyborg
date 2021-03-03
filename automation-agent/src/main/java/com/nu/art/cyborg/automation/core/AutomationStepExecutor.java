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

import com.nu.art.automation.consts.GetWebElementBy;
import com.nu.art.automation.core.AutomationStep;
import com.nu.art.belog.Logger;
import com.nu.art.cyborg.automation.core.AutomationManager.Item;
import com.nu.art.cyborg.automation.exceptions.CyborgAutomationException;
import com.robotium.solo.By;

public abstract class AutomationStepExecutor<Step extends AutomationStep>
	extends Logger {

	private Exception errorInStep;

	protected AutomationManager manager;

	protected Step step;

	protected Handler uiHandler;

	final void setUserStep(Step userStep) {
		this.step = userStep;
	}

	final void setManager(AutomationManager manager) {
		this.manager = manager;
	}

	protected abstract void execute()
		throws CyborgAutomationException;

	public void setUI_Handler(Handler uiHandler) {
		this.uiHandler = uiHandler;
	}

	final <Type> Type getItem(Item<Type> key) {
		return manager.getItem(key);
	}

	final <Type> void putItem(Item<Type> key, Type value) {
		manager.putItem(key, value);
	}

	final Object resolveAsString(String toResolve) {
		return manager.resolveAsString(toResolve);
	}

	final Object resolveToObject(String toResolve) {
		return manager.resolveToObject(toResolve);
	}

	final Object[] resolveToArray(String toResolve) {
		return manager.resolveToArray(toResolve);
	}

	protected final void waitForMainThreadAction(final Runnable runnable)
		throws CyborgAutomationException {
		synchronized (runnable) {
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					try {
						runnable.run();
					} catch (Exception e) {
						errorInStep = e;
					}
					synchronized (runnable) {
						runnable.notify();
					}
				}
			});
			try {
				runnable.wait();
			} catch (InterruptedException e) {}
		}
		if (errorInStep != null)
			throw new CyborgAutomationException("Error in UI Thread action", errorInStep);
	}

	protected final By getBy(GetWebElementBy type, String criteria) {
		switch (type) {
			case ClassName:
				return By.className(criteria);
			case CSS_Selector:
				return By.cssSelector(criteria);
			case Id:
				return By.id(criteria);
			case Name:
				return By.name(criteria);
			case TagName:
				return By.tagName(criteria);
			case TextContent:
				return By.textContent(criteria);
			case XPath:
				return By.xpath(criteria);
			default:
				return null;
		}
	}
}
