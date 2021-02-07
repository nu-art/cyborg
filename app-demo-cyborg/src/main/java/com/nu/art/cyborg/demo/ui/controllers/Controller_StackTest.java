package com.nu.art.cyborg.demo.ui.controllers;

import android.view.View;
import android.widget.TextView;

import com.nu.art.core.generics.Processor;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgStackController;
import com.nu.art.cyborg.core.stackTransitions.StackTransitions;
import com.nu.art.cyborg.demo.R;

import java.util.Arrays;

import static com.nu.art.core.tools.RandomTools.nextRandom;

/**
 * Created by TacB0sS on 16/12/2017.
 */

public class Controller_StackTest
	extends CyborgController {

	private int layerIndex;
	private int colors[] = {
		R.color.Green,
		R.color.Red,
		R.color.Blue,
		R.color.Gray,
		R.color.Purple,
		R.color.Cyan,
		R.color.Yellow,
	};

	public static class Controller_StackItem
		extends CyborgController {

		@ViewIdentifier(viewId = R.id.label)
		private TextView label;

		public Controller_StackItem() {
			super(R.layout.controller__dynamic_a);
		}

		void setLabel(String label) {
			this.label.setText(label);
		}
	}

	@ViewIdentifier(viewId = R.id.StackTest)
	private CyborgStackController stack;

	@ViewIdentifier(
		viewId = {
			R.id.Bug1,
			R.id.Bug2,
			R.id.MultiTransitions,
			R.id.AddLayerDontKeepInStack,
			R.id.AddLayerKeepBackground,
			R.id.AddLayerKeepInStack
		},
		listeners = ViewListener.OnClick)
	private View[] views;

	public Controller_StackTest() {
		super(R.layout.controller__stack_test);
	}

	@Override
	public void onClick(View v) {
		boolean keepInStack = false;
		boolean keepBackground = false;
		switch (v.getId()) {
			case R.id.Bug1:
				bug1();
				return;
			case R.id.Bug2:
				bug2();
				return;
			case R.id.MultiTransitions:
				multiTransitions();
				return;

			case R.id.AddLayerDontKeepInStack:
				break;
			case R.id.AddLayerKeepBackground:
				keepBackground = true;
				keepInStack = true;
				break;
			case R.id.AddLayerKeepInStack:
				keepInStack = true;
				break;
		}

		final boolean finalKeepInStack = keepInStack;
		final boolean finalKeepBackground = keepBackground;
		for (int i = 0; i < 4; i++) {
			postOnUI(100 * i, new Runnable() {
				@Override
				public void run() {
					final int k = layerIndex++;
					stack.createLayerBuilder()
					     .setStateTag("Layer-" + k)
					     .setKeepBackground(finalKeepBackground)
					     .setKeepInStack(finalKeepInStack)
					     .setTransitions(StackTransitions.Slide)
					     .setControllerType(Controller_StackItem.class)
					     .setProcessor(new Processor<Controller_StackItem>() {
						     boolean keepInStack1 = k % 6 == 0 || finalKeepInStack;
						     int color = getColor(colors[k % colors.length]);

						     @Override
						     public void process(Controller_StackItem controller_stackItem) {
							     controller_stackItem.setKeepInStack(keepInStack1);
							     controller_stackItem.getRootView().setBackgroundColor(color);
							     controller_stackItem.label.setText(Arrays.toString(stack.getViewsTags()));
						     }
					     })
					     .push();
				}
			});
		}
	}

	private void bug2() {
		for (int i = 0; i < 4; i++) {
			final int k = layerIndex++;
			stack.createLayerBuilder()
			     .setStateTag("Layer-" + k)
			     .setKeepBackground(false)
			     .setKeepInStack(false)
			     .setTransitions(StackTransitions.Fade)
			     .setControllerType(Controller_StackItem.class)
			     .setProcessor(new Processor<Controller_StackItem>() {
				     int color = getColor(colors[k % colors.length]);

				     @Override
				     public void process(Controller_StackItem controller_stackItem) {
					     controller_stackItem.getRootView().setBackgroundColor(color);
					     controller_stackItem.label.setText(Arrays.toString(stack.getViewsTags()));
				     }
			     })
			     .push();
		}
	}

	private void bug1() {
		final StackTransitions stackTransitions = nextRandom(StackTransitions.values());
		stack.createLayerBuilder()
		     .setStateTag("Layer-" + layerIndex++)
		     .setKeepBackground(false)
		     .setKeepInStack(true)
		     .setTransitions(stackTransitions)
		     .setControllerType(Controller_StackItem.class)
		     .setProcessor(new Processor<Controller_StackItem>() {
			     int color = getColor(colors[layerIndex % colors.length]);

			     @Override
			     public void process(Controller_StackItem controller_stackItem) {
				     controller_stackItem.getRootView().setBackgroundColor(color);
				     controller_stackItem.label.setText(stackTransitions.name());
			     }
		     })
		     .push();
	}

	@Override
	protected void onResume() {
		CyborgStackController.DebugFlag.enable();
	}

	@Override
	protected void onDestroy() {
		CyborgStackController.DebugFlag.disable();
	}

	private void multiTransitions() {
		final StackTransitions stackTransitions1 = nextRandom(StackTransitions.values());
		final StackTransitions stackTransitions2 = nextRandom(StackTransitions.values(), stackTransitions1);
		stack.createLayerBuilder()
		     .setStateTag("Layer-" + layerIndex++)
		     .setKeepBackground(false)
		     .setKeepInStack(true)
		     .setTransitions(stackTransitions1, stackTransitions2)
		     .setControllerType(Controller_StackItem.class)
		     .setProcessor(new Processor<Controller_StackItem>() {
			     int color = getColor(colors[layerIndex % colors.length]);

			     @Override
			     public void process(Controller_StackItem controller_stackItem) {
				     controller_stackItem.getRootView().setBackgroundColor(color);
				     controller_stackItem.label.setText(stackTransitions1.name() + " && " + stackTransitions2);
			     }
		     })
		     .push();
	}
}
