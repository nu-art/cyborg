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
package com.nu.art.cyborg.automation.executors.view;

import com.nu.art.automation.models.view.Action_ClickOnRecyclerItem;
import com.nu.art.cyborg.automation.core.AutomationStepExecutor;
import com.nu.art.cyborg.automation.exceptions.CyborgAutomationException;

public final class Executor_OnRecyclerItemClick
	extends AutomationStepExecutor<Action_ClickOnRecyclerItem> {

	@Override
	protected final void execute()
		throws CyborgAutomationException {
		//		final int wantedPosition = step.getPosition();
		//		logInfo("Fetching AdapterView with ID: " + step.getViewConstantName());
		//		final AdapterView<?> adapterView = (AdapterView<?>) solo.getView(step.getViewConstantName());
		//		int headerViewsCount = 0;
		//		if (adapterView instanceof ListView)
		//			headerViewsCount = ((ListView) adapterView).getHeaderViewsCount();
		//
		//		waitForMainThreadAction(new Runnable() {
		//
		//			@Override
		//			public void run() {
		//				logInfo("Scrolling to index:" + wantedPosition);
		//
		//				if (adapterView instanceof ListView)
		//					((ListView) adapterView).setSelection(wantedPosition);
		//				else if (adapterView instanceof GridView)
		//					((GridView) adapterView).setSelection(wantedPosition);
		//			}
		//		});
		//
		//		int firstPosition = adapterView.getFirstVisiblePosition() - headerViewsCount; // This is the same as child #0
		//		int wantedChild = wantedPosition - firstPosition;
		//
		//		if (wantedChild < 0 || wantedChild >= adapterView.getChildCount()) {
		//			// Could also check if wantedPosition is between listView.getFirstVisiblePosition() and
		//			// listView.getLastVisiblePosition() instead.
		//			throw new CyborgAutomationException("Failed to find position: " + wantedPosition + ", in " + adapterView.getAdapter().getCount()
		//					+ " elements, with in a " + adapterView.getClass().getSimpleName() + " with id: " + step.getViewConstantName());
		//		}
		//
		//		View wantedView = adapterView.getChildAt(wantedChild);
		//		switch (step.getClickType()) {
		//			case DoubleClick :
		//				logInfo("Double Click on index:" + wantedPosition);
		//				solo.clickOnView(wantedView);
		//				try {
		//					Thread.sleep(50);
		//				} catch (InterruptedException e) {}
		//
		//			case Click :
		//				logInfo("Click on index:" + wantedPosition);
		//				solo.clickOnView(wantedView);
		//				break;
		//
		//			case LongClick :
		//				logInfo("Long Click on index:" + wantedPosition);
		//				solo.clickLongOnView(wantedView);
		//				break;
		//
		//			default :
		//				break;
		//		}
	}
}
