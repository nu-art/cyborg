

package com.nu.art.cyborg.demo.ui.controllers.dynamicStackExample;

import android.graphics.Typeface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.nu.art.core.exceptions.runtime.MUST_NeverHappenException;
import com.nu.art.core.generics.Processor;
import com.nu.art.cyborg.annotations.ItemType;
import com.nu.art.cyborg.annotations.Restorable;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgStackController;
import com.nu.art.cyborg.core.CyborgStackController.StackLayerBuilder;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.stackTransitions.Transition;
import com.nu.art.cyborg.core.stackTransitions.StackTransitions;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.demo.R;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class Controller_DynamicStack
	extends CyborgController {

	@ViewIdentifier(viewId = {
		R.id.AddA,
		R.id.AddB,
		R.id.AddC
	},
	                listeners = ViewListener.OnClick)
	private View[] views;

	@ViewIdentifier(viewId = R.id.ControllerStackLabel)
	private TextView stackList;

	@ViewIdentifier(viewId = R.id.AddToLeftStack,
	                listeners = ViewListener.OnClick)
	private CheckBox addToLeftStack;

	@ViewIdentifier(viewId = R.id.AddToRightStack,
	                listeners = ViewListener.OnClick)
	private CheckBox addToRightStack;

	@ViewIdentifier(viewId = R.id.ReverseCheckBox,
	                listeners = ViewListener.OnClick)
	private CheckBox reverseCheckBox;

	@ViewIdentifier(viewId = R.id.AnimationSelector,
	                listeners = ViewListener.OnItemSelected)
	private Spinner animationSelector;

	@Restorable
	private String toSave;

	private String notToSave;

	private CyborgStackController stack;

	private int counter;

	private boolean reverse;

	private Transition selectedAnimation;

	public Controller_DynamicStack() {
		super(R.layout.controller__stack_example);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate() {
		stack = getControllerById(R.id.RightStack);
		CyborgStackController leftStack = getControllerById(R.id.LeftStack);
		leftStack.setFocused(false);

		setupSpinner(animationSelector, new Processor<ListDataModel<Transition>>() {
			@Override
			public void process(ListDataModel<Transition> listDataModel) {
				listDataModel.add(StackTransitions.values());
			}
		}, AnimationItemRenderer.class);
		addToRightStack.setChecked(true);
	}

	public void onClick1(View buttonView) {
		CheckBox toUncheck;
		int stackId;
		switch (buttonView.getId()) {
			case R.id.ReverseCheckBox:
				reverse = reverseCheckBox.isChecked();
				return;

			case R.id.AddToLeftStack:
				toUncheck = addToRightStack;
				stackId = addToLeftStack.isChecked() ? R.id.LeftStack : R.id.RightStack;
				break;

			case R.id.AddToRightStack:
				toUncheck = addToLeftStack;
				stackId = addToRightStack.isChecked() ? R.id.RightStack : R.id.LeftStack;
				break;

			default:
				throw new MUST_NeverHappenException("");
		}

		toUncheck.setChecked(!toUncheck.isChecked());
		if (stack != null)
			stack.setFocused(false);

		stack = getControllerById(stackId);
		stack.setFocused(true);
	}

	@Override
	public void onItemSelected(Object selectedItem, AdapterView<?> parentView, View selectedView, int position, long id) {
		switch (parentView.getId()) {
			case R.id.AnimationSelector:
				selectedAnimation = (Transition) selectedItem;
				break;
		}
	}

	@Override
	public void onClick(View v) {
		counter++;
		//		StackTransitionAnimator stackTransitionAnimator = new PredefinedStackTransitionAnimator(getActivity(), selectedAnimation, selectedOrientation, reverse);
		//		if (selectedAnimation == PredefinedTransitions.None)
		//			stackTransitionAnimator = null;

		StackLayerBuilder layerBuilder = stack.createLayerBuilder();
		layerBuilder.setTransitions(selectedAnimation);
		switch (v.getId()) {
			case R.id.AddA:
				layerBuilder.setControllerType(Controller_A.class);
				break;

			case R.id.AddB:
				layerBuilder.setControllerType(Controller_B.class);
				//				layerBuilder.setLayoutId(R.layout.v1_controller__dynamic_b);
				break;

			case R.id.AddC:
				layerBuilder.setStateTag("TagC-" + counter);
				//				layerBuilder.setLayoutId(R.layout.v1_activity__recycler_example);
				//				layerBuilder.setDuration(5000);
				break;

			default:
				onClick1(v);
				return;
		}
		layerBuilder.push();
	}

	@ItemType(type = Transition.class)
	public class AnimationItemRenderer
		extends ItemRenderer<Transition> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		private AnimationItemRenderer() {
			super(R.layout.renderer__example_double);
		}

		@Override
		protected void renderItem(Transition item) {
			exampleLabel.setText(item.toString());
			exampleLabel.setTypeface(null, item == selectedAnimation ? Typeface.BOLD : Typeface.NORMAL);
		}
	}
}
