package com.nu.art.automation.models;

import android.content.ComponentName;
import android.content.Intent;

import com.nu.art.automation.core.AutomationStep;

import static com.nu.art.automation.consts.StepTypes.Type_StartActivity;

/**
 * Created by TacB0sS on 10/04/2018.
 */

public class Action_StartActivity
	extends AutomationStep {

	public String activityName;

	public Action_StartActivity() {
		super(Type_StartActivity);
	}

	public Action_StartActivity(Intent intent) {
		this();
		ComponentName component = intent.getComponent();
		if (component != null) {
			activityName = component.getPackageName() + "." + component.getClassName();
		}
	}

	public Action_StartActivity setActivityName(String activityName) {
		this.activityName = activityName;
		return this;
	}
}
