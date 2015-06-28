package com.nu.art.software.cyborg.demo.ui.controllers.dynamicStackExample;

import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class DynamicBFragmentController
		extends CyborgController {

	public DynamicBFragmentController() {
		super(R.layout.v1_controller__dynamic_b);
	}
}
