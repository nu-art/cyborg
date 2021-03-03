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

package com.nu.art.cyborg.automation;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nu.art.automation.consts.ClickType;
import com.nu.art.automation.consts.HardButton;
import com.nu.art.automation.core.AutomationScenario;
import com.nu.art.automation.core.AutomationStep;
import com.nu.art.automation.models.Action_AppCrashedHere;
import com.nu.art.automation.models.Action_StartActivity;
import com.nu.art.automation.models.device.Action_PressHardButton;
import com.nu.art.automation.models.view.Action_ClickOnListItem;
import com.nu.art.automation.models.view.Action_ClickOnRecyclerItem;
import com.nu.art.automation.models.view.Action_ClickOnView;
import com.nu.art.automation.models.view.Action_OnItemSelected;
import com.nu.art.automation.models.view.Action_OnSeekBarChanged;
import com.nu.art.core.exceptions.runtime.MUST_NeverHappenException;
import com.nu.art.core.file.Charsets;
import com.nu.art.core.tools.FileTools;
import com.nu.art.core.utils.DebugFlags;
import com.nu.art.cyborg.common.beans.ModelEvent;
import com.nu.art.cyborg.common.interfaces.ScenarioRecorder;
import com.nu.art.cyborg.common.interfaces.UserActionsDelegator;
import com.nu.art.cyborg.core.CyborgModule;
import com.nu.art.cyborg.core.modules.crashReport.ModuleStateCollector;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class AutomationModule
	extends CyborgModule
	implements ModuleStateCollector, UserActionsDelegator, ScenarioRecorder {

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static final String CrashingScenarioFileName = "crashing_scenario.xml";

	private File automationScenarioFile;

	private AndroidR_ClassManager rClassManager;
	private AutomationScenario scenario = new AutomationScenario();
	private boolean automated;

	@Override
	protected void init() {
		String rClassName = getApplicationContext().getPackageName() + ".R";
		try {
			Class<?> rClass = Class.forName(rClassName);
			rClassManager = new AndroidR_ClassManager(rClass);
		} catch (ClassNotFoundException e) {
			throw new MUST_NeverHappenException("Could not resolve R Class for app", e);
		}
		automationScenarioFile = new File(getApplicationContext().getCacheDir(), CrashingScenarioFileName);
	}

	public void setAutomated(boolean automated) {
		this.automated = automated;
	}

	public boolean isAutomated() {
		return automated;
	}

	public final AutomationScenario getScenario() {
		return scenario;
	}

	public final String getName(ResourceType resourceType, int id) {
		return rClassManager.getName(resourceType, id);
	}

	public final int getId(ResourceType resourceType, String name) {
		return rClassManager.getId(resourceType, name);
	}

	public final void addUserStep(AutomationStep step) {
		scenario.addStep(step);
	}

	@Override
	protected boolean isLoggerEnabled() {
		return DebugFlag.isEnabled();
	}

	public void collectModuleState(HashMap<String, Object> moduleCrashData)
		throws IOException {
		if (automated)
			return;

		addUserStep(new Action_AppCrashedHere());
		scenario.reverse();

		FileTools.writeToFile(gson.toJson(scenario), automationScenarioFile, Charsets.UTF_8);
	}

	@Override
	public void onActivityStarted(Intent intent) {
		logVerbose("onActivityStarted(" + intent + ") will probably have to model the intent");
		addUserStep(new Action_StartActivity(intent));
	}

	@Override
	public void onBackPressed() {
		logVerbose("onBackPressed()");
		addUserStep(new Action_PressHardButton(HardButton.Back));
	}

	@Override
	public void onClick(View v) {
		String viewName = getName(ResourceType.Id, v.getId());
		logVerbose("onClick(" + viewName + ")");
		addUserStep(new Action_ClickOnView(viewName, ClickType.Click));
	}

	@Override
	public boolean onLongClick(View v) {
		String viewName = getName(ResourceType.Id, v.getId());
		logVerbose("onLongClick(" + viewName + ")");
		addUserStep(new Action_ClickOnView(viewName, ClickType.LongClick));
		return false;
	}

	@Override
	public void onRecyclerItemClicked(RecyclerView parentView, View view, int position) {
		String viewName = getName(ResourceType.Id, parentView.getId());
		logVerbose("onRecyclerItemClicked(" + viewName + ", " + position + ")");
		addUserStep(new Action_ClickOnRecyclerItem(viewName, position, ClickType.Click));
	}

	@Override
	public boolean onRecyclerItemLongClicked(RecyclerView parentView, View view, int position) {
		String viewName = getName(ResourceType.Id, parentView.getId());
		logVerbose("onRecyclerItemLongClicked(" + viewName + ", " + position + ")");
		addUserStep(new Action_ClickOnRecyclerItem(viewName, position, ClickType.LongClick));
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		String viewName = getName(ResourceType.Id, parent.getId());
		logVerbose("onItemSelected(" + viewName + ", " + position + ")");
		addUserStep(new Action_OnItemSelected(viewName, position));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		String viewName = getName(ResourceType.Id, parent.getId());
		logVerbose("onNothingSelected(" + viewName + ")");
		addUserStep(new Action_OnItemSelected(viewName, -1));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		String viewName = getName(ResourceType.Id, v.getId());
		logVerbose("TODO - onNothingSelected(" + viewName + ")");
		return false;
	}

	@Override
	public void onModelEvent(ModelEvent event) {
		// TODO implement this

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// NO NEED TO IMPLEMENT WE ONLY CARE ABOUT: onStopTrackingTouch
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// NO NEED TO IMPLEMENT WE ONLY CARE ABOUT: onStopTrackingTouch
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		float relativeProgress = 1f * seekBar.getProgress() / seekBar.getMax();
		float relativeSecondaryProgress = 1f * seekBar.getSecondaryProgress() / seekBar.getMax();
		String viewName = getName(ResourceType.Id, seekBar.getId());
		logVerbose("TODO - onStopTrackingTouch(" + viewName + ", " + relativeProgress + ", " + relativeSecondaryProgress + ")");
		addUserStep(new Action_OnSeekBarChanged(viewName, relativeProgress, relativeSecondaryProgress));
	}

	@Override
	public boolean onKeyShortcut(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		logVerbose("TODO - onKeyDown(" + keyCode + ")");
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		logVerbose("TODO - onKeyUp(" + keyCode + ")");
		return false;
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		logVerbose("TODO - onKeyLongPress(" + keyCode + ")");
		return false;
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
		// TODO implement this

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// TODO implement this

	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO implement this

	}

	@Override
	public void onPageSelected(int position) {
		logVerbose("TODO - onPageSelected(" + position + ")");
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		logVerbose("TODO - onKey(" + getName(ResourceType.Id, v.getId()) + ", " + keyCode + ")");
		return false;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		logVerbose("TODO - onMenuItemClick(" + item + ")");
		return false;
	}

	@Override
	public void beforeTextChanged(TextView view, CharSequence string, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(TextView view, CharSequence string, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(TextView view, Editable editableValue) {
		logVerbose("TODO - afterTextChanged(" + getName(ResourceType.Id, view.getId()) + ", " + editableValue.toString() + ")");
	}
}
