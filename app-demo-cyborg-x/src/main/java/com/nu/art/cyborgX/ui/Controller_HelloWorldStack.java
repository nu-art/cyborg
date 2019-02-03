package com.nu.art.cyborgX.ui;

import android.view.View;

import com.nu.art.core.exceptions.runtime.ImplementationMissingException;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgStackController;
import com.nu.art.cyborg.core.animations.PredefinedStackTransitionAnimator;
import com.nu.art.cyborg.core.animations.PredefinedTransitions;
import com.nu.art.cyborg.core.animations.transitions.BaseTransition;
import com.nu.art.cyborgX.R;

/**
 * Created by TacB0sS on 06-Jul 2017.
 */

public class Controller_HelloWorldStack
	extends CyborgController {

	//	@ViewIdentifier(viewId = R.id.TV_AddSecondLayer,
	//									listeners = {
	//											ViewListener.OnClick,
	//											ViewListener.OnLongClick
	//									})
	//	TextView helloWorldTextView;

	@ViewIdentifier(viewId = {
		R.id.TV_AddSecondLayer1,
		R.id.TV_AddSecondLayer2
	},
	                listeners = {
		                ViewListener.OnClick,
		                ViewListener.OnLongClick
	                })
	View[] clickableViews;

	public Controller_HelloWorldStack() {
		super(R.layout.controller__hello_world_stack);
	}

	@Override
	public boolean onLongClick(View v) {
		CyborgStackController stackController = getControllerById(R.id.Tag_RootStack);
		stackController.createLayerBuilder()
		               .setControllerType(Controller_HelloWorld.class)
		               .setDuration(2000)
		               .setStackTransitionAnimators(createLayerTransition(PredefinedTransitions.Fade))
		               .build();
		return true;
	}

	@Override
	public void onClick(View v) {
		PredefinedStackTransitionAnimator animation;
		Class<? extends CyborgController> controllerType;
		switch (v.getId()) {
			case R.id.TV_AddSecondLayer1:
				animation = new PredefinedStackTransitionAnimator(getActivity(), PredefinedTransitions.Cube, BaseTransition.ORIENTATION_HORIZONTAL);
				controllerType = Controller_HelloWorld.class;
				break;

			case R.id.TV_AddSecondLayer2:
				animation = new PredefinedStackTransitionAnimator(getActivity(), PredefinedTransitions.Slide, BaseTransition.ORIENTATION_VERTICAL);
				controllerType = Controller_HelloWorld2.class;
				break;

			default:
				throw new ImplementationMissingException("Unhandled view click event...");
		}

		CyborgStackController stackController = getControllerById(R.id.Tag_RootStack);
		stackController.createLayerBuilder().setControllerType(controllerType).setStackTransitionAnimators(animation).setDuration(600).build();
	}
}
