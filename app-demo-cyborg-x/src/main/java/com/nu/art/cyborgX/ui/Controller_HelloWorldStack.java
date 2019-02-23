package com.nu.art.cyborgX.ui;

import android.view.View;

import com.nu.art.core.exceptions.runtime.ImplementationMissingException;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgStackController;
import com.nu.art.cyborg.core.stackTransitions.StackTransitions;
import com.nu.art.cyborg.core.stackTransitions.Transition;
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
		               .setTransitionDuration(2000)
		               .setTransitions(StackTransitions.Fade)
		               .build();
		return true;
	}

	@Override
	public void onClick(View v) {
		Transition animation;
		Class<? extends CyborgController> controllerType;
		switch (v.getId()) {
			case R.id.TV_AddSecondLayer1:
				animation = StackTransitions.Cube;
				controllerType = Controller_HelloWorld.class;
				break;

			case R.id.TV_AddSecondLayer2:
				animation = StackTransitions.Slide;
				controllerType = Controller_HelloWorld2.class;
				break;

			default:
				throw new ImplementationMissingException("Unhandled view click event...");
		}

		CyborgStackController stackController = getControllerById(R.id.Tag_RootStack);
		stackController.createLayerBuilder().setControllerType(controllerType).setTransitions(animation).setTransitionDuration(600).build();
	}
}
