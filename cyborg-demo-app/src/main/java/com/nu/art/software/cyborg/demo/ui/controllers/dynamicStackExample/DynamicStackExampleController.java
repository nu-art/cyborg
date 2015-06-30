/*
 *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *  Restricted usage under Binpress license
 *
 *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
 */

package com.nu.art.software.cyborg.demo.ui.controllers.dynamicStackExample;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nu.art.software.cyborg.annotations.Restorable;
import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.core.interfaces.StackManagerEventListener;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class DynamicStackExampleController
		extends CyborgController {

	@ViewIdentifier(viewIds = {R.id.AddA, R.id.AddB, R.id.AddC}, listeners = ViewListener.OnClick)
	private View[] views;

	@ViewIdentifier(viewId = R.id.ControllerStackLabel)
	private TextView fragmentStack;

	@ViewIdentifier(viewId = R.id.AddToLeftStack, listeners = ViewListener.OnCheckChanged)
	private CheckBox addToLeftStack;

	@Restorable
	private String toSave;

	private String notToSave;

	private static final String RightPane = "RightPane";

	private static final String LeftPane = "LeftPane";

	private StackManagerEventListener stack;

	public DynamicStackExampleController() {
		super(R.layout.v1_controller__stack_example);
	}

	@Override
	public void onCreate() {
		stack = getController(StackManagerEventListener.class, RightPane);
		updateStackLabel();
	}

	private void updateStackLabel() {
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		stack = getController(StackManagerEventListener.class, !isChecked ? RightPane : LeftPane);
	}

	int counter;

	@Override
	public void onClick(View v) {
		counter++;
		switch (v.getId()) {
			case R.id.AddA:
				stack.push("TagA-" + counter, DynamicAFragmentController.class, true);
				break;
			case R.id.AddB:
				stack.push("TagB-" + counter, R.layout.v1_controller__dynamic_b, true);
				break;
			case R.id.AddC:
				stack.push("TagC-" + counter, R.layout.v1_activity__recycler_example, true);
				break;
		}
	}

	@Override
	public boolean onBackPressed() {
		return stack.popLast();
	}
}
