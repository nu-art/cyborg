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

import com.nu.art.software.core.generics.Processor;
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

	private String currentStackTag;

	private static final String RightPane = "RightPane";

	private static final String LeftPane = "LeftPane";

	public DynamicStackExampleController() {
		super(R.layout.v1_controller__stack_example);
	}

	@Override
	public void onCreate() {
		currentStackTag = RightPane;
		updateStackLabel();
	}

	private void updateStackLabel() {
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		currentStackTag = !isChecked ? RightPane : LeftPane;
	}

	int counter;

	@Override
	public void onClick(View v) {
		counter++;
		switch (v.getId()) {
			case R.id.AddA:
				dispatchEvent(StackManagerEventListener.class, new Processor<StackManagerEventListener>() {
					@Override
					public void process(StackManagerEventListener toProcess) {
						toProcess.push(currentStackTag, "TagA-" + counter, DynamicAFragmentController.class, true);
					}
				});
				break;
			case R.id.AddB:
				dispatchEvent(StackManagerEventListener.class, new Processor<StackManagerEventListener>() {
					@Override
					public void process(StackManagerEventListener toProcess) {
						toProcess.push(currentStackTag, "TagB-" + counter, DynamicBFragmentController.class, true);
					}
				});
				break;
			case R.id.AddC:
				dispatchEvent(StackManagerEventListener.class, new Processor<StackManagerEventListener>() {
					@Override
					public void process(StackManagerEventListener toProcess) {
						toProcess.push(currentStackTag, "TagC-" + counter, DynamicCFragmentController.class, true);
					}
				});
				break;
		}
	}

	@Override
	public boolean onBackPressed() {
		dispatchEvent(StackManagerEventListener.class, new Processor<StackManagerEventListener>() {
			@Override
			public void process(StackManagerEventListener toProcess) {
				toProcess.popLast(currentStackTag);
			}
		});
		return true;
	}
}
