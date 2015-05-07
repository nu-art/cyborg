/*
 *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *  Restricted usage under Binpress license
 *
 *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
 */

package com.nu.art.software.cyborg.demo.ui.controllers.dynamicFragmentManagement;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nu.art.software.cyborg.annotations.Restorable;
import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.core.FragmentStack;
import com.nu.art.software.cyborg.demo.R;

@SuppressWarnings("unused")
public class DynamicFragmentManagementController
		extends CyborgController {


	@ViewIdentifier(viewIds = {R.id.AddA, R.id.AddB, R.id.AddC}, listeners = ViewListener.OnClick)
	private View[] views;

	@ViewIdentifier(viewId = R.id.FragmentStackLabel)
	private TextView fragmentStack;


	@ViewIdentifier(viewId = R.id.AddToLeftStack, listeners = ViewListener.OnCheckChanged)
	private CheckBox addToLeftStack;

	@Restorable
	private String toSave;

	private String notToSave;

	private FragmentStack parentStack_Left;

	private FragmentStack parentStack_Right;

	private FragmentStack currentStack;

	public DynamicFragmentManagementController() {
		super(R.layout.v1_controller__fragment_management_a);
	}

	@Override
	protected void onCreate() {
		parentStack_Left = new FragmentStack(getFragmentManager(), R.id.ParentLayoutId1);
		parentStack_Right = new FragmentStack(getFragmentManager(), R.id.ParentLayoutId2);
		currentStack = parentStack_Right;

		postOnUI(30, new Runnable() {
			@Override
			public void run() {
				CyborgController[] controllers = getActivity().getControllers();
				String fragStack = "";
				for (CyborgController controller : controllers) {
					if (controller == DynamicFragmentManagementController.this)
						continue;
					fragStack += controller.getFragmentTag() + "(" + controller.getState() + ")\n";
				}
				fragmentStack.setText(fragStack);

				if (isDestroyed())
					return;
				postOnUI(100, this);
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		currentStack = !isChecked ? parentStack_Right : parentStack_Left;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.AddA:
				currentStack.addFragment(DynamicAController.class, "TagA");
				break;
			case R.id.AddB:
				currentStack.addFragment(DynamicBController.class, "TagB");
				break;
			case R.id.AddC:
				currentStack.addFragment(DynamicCController.class, "TagC");
				break;
		}
	}

	@Override
	public boolean onBackPressed() {
		currentStack.pop();
		return true;
	}
}
