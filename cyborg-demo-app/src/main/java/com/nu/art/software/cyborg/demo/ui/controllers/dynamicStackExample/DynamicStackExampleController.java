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
import com.nu.art.software.cyborg.ui.animations.animationTransition.AnimationTransition;
import com.nu.art.software.cyborg.ui.animations.animationTransition.AnimationTransitionType;
import com.nu.art.software.cyborg.ui.animations.animationTransition.PredefinedAnimations;
import com.nu.art.software.cyborg.ui.animations.viewBasedAnimations.ViewBasedAnimation;
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

	@ViewIdentifier(viewId = R.id.ReverseCheckBox, listeners = ViewListener.OnCheckChanged)
	private CheckBox reverseCheckBox;

	@ViewIdentifier(viewId = R.id.AnimationSelector, listeners = ViewListener.OnItemSelected)
	private Spinner animationSelector;

	@ViewIdentifier(viewId = R.id.OrientationSelector, listeners = ViewListener.OnItemSelected)
	private Spinner orientationSelector;

	@Restorable
	private String toSave;

	private String notToSave;

	private static final String RightPane = "RightPane";

	private static final String LeftPane = "LeftPane";

	private StackManagerEventListener stack;

	private int counter;

	private AnimationTransitionType selectedAnimation = PredefinedAnimations.values()[0];

	private ListDataModel<AnimationTransitionType> animationsDataModel;

	private int selectedOrientation;

	private ListDataModel<Integer> orientationDataModel;

	private boolean reverse;

	public DynamicStackExampleController() {
		super(R.layout.v1_controller__stack_example);
	}

	@Override
	public void onCreate() {
		stack = getController(StackManagerEventListener.class, RightPane);

		setupAnimationsSpinner();
		setupOrientationSpinner();
		addToRightStack.setChecked(true);
		updateStackLabel();
	}

	private void setupOrientationSpinner() {
		CyborgAdapter<Integer> adapter = new CyborgAdapter<Integer>(activityBridge, OrientationItemRenderer.class) {

			@Override
			@SuppressWarnings("unchecked")
			protected <RendererType extends ItemRenderer<? extends Integer>> RendererType instantiateItemRendererType(Class<RendererType> renderersType) {
				if (renderersType == OrientationItemRenderer.class)
					return (RendererType) new OrientationItemRenderer();
				throw new MUST_NeverHappenedException("");
			}
		};
		orientationDataModel = new ListDataModel<Integer>(Integer.class);
		orientationDataModel.addItems(ViewBasedAnimation.ORIENTATION_HORIZONTAL, ViewBasedAnimation.ORIENTATION_VERTICAL);
		adapter.setDataModel(orientationDataModel);
		orientationSelector.setAdapter(adapter.getArrayAdapter());
	}

	private void setupAnimationsSpinner() {
		CyborgAdapter<AnimationTransitionType> adapter = new CyborgAdapter<AnimationTransitionType>(activityBridge, AnimationItemRenderer.class) {

			@Override
			@SuppressWarnings("unchecked")
			protected <RendererType extends ItemRenderer<? extends AnimationTransitionType>> RendererType instantiateItemRendererType(Class<RendererType> renderersType) {
				if (renderersType == AnimationItemRenderer.class)
					return (RendererType) new AnimationItemRenderer();
				throw new MUST_NeverHappenedException("");
			}
		};
		animationsDataModel = new ListDataModel<AnimationTransitionType>(AnimationTransitionType.class);
		animationsDataModel.addItems(PredefinedAnimations.values());
		adapter.setDataModel(animationsDataModel);
		animationSelector.setAdapter(adapter.getArrayAdapter());
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
			case R.id.ReverseCheckBox:
				reverse = isChecked;
				return;
			default:
				throw new MUST_NeverHappenedException("");
		}
		toUncheck.setChecked(!isChecked);
		stack = getController(StackManagerEventListener.class, stackTag);
	}

	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedView, int position, long id) {
		switch (parentView.getId()) {
			case R.id.AnimationSelector:
				selectedAnimation = animationsDataModel.getItemForPosition(position);
				break;
			case R.id.OrientationSelector:
				selectedOrientation = orientationDataModel.getItemForPosition(position);
				break;
		}
	}

	@Override
	public void onClick(View v) {
		counter++;
		switch (v.getId()) {
			case R.id.AddA:
				stack.push("TagA-" + counter, DynamicAFragmentController.class, new AnimationTransition(getActivity(), selectedAnimation, selectedOrientation, reverse), true);
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

	public class OrientationItemRenderer
			extends ItemRenderer<Integer> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		private OrientationItemRenderer() {
			super(R.layout.list_node__recycler_example_float);
		}

		@Override
		protected void renderItem(Integer item) {
			exampleLabel.setText(item == ViewBasedAnimation.ORIENTATION_HORIZONTAL ? "Horizontal" : "Vertical");

			exampleLabel.setTypeface(null, item == selectedOrientation ? Typeface.BOLD : Typeface.NORMAL);
		}
	}

	public class AnimationItemRenderer
			extends ItemRenderer<AnimationTransitionType> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		private AnimationItemRenderer() {
			super(R.layout.list_node__recycler_example_double);
		}

		@Override
		protected void renderItem(AnimationTransitionType item) {
			exampleLabel.setText(item.toString());
			exampleLabel.setTypeface(null, item == selectedAnimation ? Typeface.BOLD : Typeface.NORMAL);
		}
	}
}
