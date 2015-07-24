/*
 *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *  Restricted usage under Binpress license
 *
 *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
 */

package com.nu.art.software.cyborg.demo.ui.controllers.dynamicStackExample;

import android.graphics.Typeface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.nu.art.software.core.exceptions.runtime.MUST_NeverHappenedException;
import com.nu.art.software.cyborg.annotations.Restorable;
import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgAdapter;
import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.core.ItemRenderer;
import com.nu.art.software.cyborg.core.dataModels.ListDataModel;
import com.nu.art.software.cyborg.core.interfaces.StackManagerEventListener;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.cyborg.ui.animations.AnimationTransition;
import com.nu.art.software.cyborg.ui.animations.PredefinedAnimations;
import com.nu.art.software.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class DynamicStackExampleController
		extends CyborgController {

	@ViewIdentifier(viewIds = {R.id.AddA, R.id.AddB, R.id.AddC}, listeners = ViewListener.OnClick)
	private View[] views;

	@ViewIdentifier(viewId = R.id.ControllerStackLabel)
	private TextView stackList;

	@ViewIdentifier(viewId = R.id.AddToLeftStack, listeners = ViewListener.OnCheckChanged)
	private CheckBox addToLeftStack;

	@ViewIdentifier(viewId = R.id.AddToRightStack, listeners = ViewListener.OnCheckChanged)
	private CheckBox addToRightStack;

	@ViewIdentifier(viewId = R.id.AnimationSelector, listeners = ViewListener.OnItemSelected)
	private Spinner animationSelector;

	@Restorable
	private String toSave;

	private String notToSave;

	private static final String RightPane = "RightPane";

	private static final String LeftPane = "LeftPane";

	private StackManagerEventListener stack;

	private int counter;

	private PredefinedAnimations selectedAnimation = PredefinedAnimations.values()[0];

	public DynamicStackExampleController() {
		super(R.layout.v1_controller__stack_example);
	}

	@Override
	public void onCreate() {
		stack = getController(StackManagerEventListener.class, RightPane);
		CyborgAdapter<PredefinedAnimations> adapter = new CyborgAdapter<PredefinedAnimations>(activityBridge, AnimationItemRenderer.class) {

			@Override
			@SuppressWarnings("unchecked")
			protected <RendererType extends ItemRenderer<? extends PredefinedAnimations>> RendererType instantiateItemRendererType(Class<RendererType> renderersType) {
				if (renderersType == AnimationItemRenderer.class)
					return (RendererType) new AnimationItemRenderer();
				throw new MUST_NeverHappenedException("");
			}
		};
		ListDataModel<PredefinedAnimations> dataModel = new ListDataModel<PredefinedAnimations>(PredefinedAnimations.class);
		dataModel.addItems(PredefinedAnimations.values());
		adapter.setDataModel(dataModel);
		animationSelector.setAdapter(adapter.getArrayAdapter());
		addToRightStack.setChecked(true);
		updateStackLabel();
	}

	private void updateStackLabel() {
		String stackAsString = stack.getStateTag() + "\n";
		for (String tag : stack.getStackListTags()) {
			stackAsString += tag + "\n";
		}
		stackList.setText(stackAsString);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		CheckBox toUncheck;
		String stackTag;
		switch (buttonView.getId()) {
			case R.id.AddToLeftStack:
				toUncheck = addToRightStack;
				stackTag = isChecked ? LeftPane : RightPane;
				break;
			case R.id.AddToRightStack:
				toUncheck = addToLeftStack;
				stackTag = isChecked ? RightPane : LeftPane;

				break;
			default:
				throw new MUST_NeverHappenedException("");
		}
		toUncheck.setChecked(!isChecked);
		stack = getController(StackManagerEventListener.class, stackTag);
	}

	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedView, int position, long id) {
		selectedAnimation = PredefinedAnimations.values()[position];
	}

	@Override
	public void onClick(View v) {
		counter++;
		switch (v.getId()) {
			case R.id.AddA:
				stack.push("TagA-" + counter, DynamicAFragmentController.class, new AnimationTransition(getActivity(), selectedAnimation), true);
				break;
			case R.id.AddB:
				stack.push("TagB-" + counter, R.layout.v1_controller__dynamic_b, true);
				break;
			case R.id.AddC:
				stack.push("TagC-" + counter, R.layout.v1_activity__recycler_example, true);
				break;
		}
		updateStackLabel();
	}

	@Override
	public boolean onBackPressed() {
		boolean b = stack.popLast();
		updateStackLabel();
		return b;
	}

	public class AnimationItemRenderer
			extends ItemRenderer<PredefinedAnimations> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		private AnimationItemRenderer() {
			super(R.layout.list_node__recycler_example_float);
		}

		@Override
		protected void renderItem(PredefinedAnimations item) {
			exampleLabel.setText(item.name());
			exampleLabel.setTypeface(null, item == selectedAnimation ? Typeface.BOLD : Typeface.NORMAL);
		}
	}
}
