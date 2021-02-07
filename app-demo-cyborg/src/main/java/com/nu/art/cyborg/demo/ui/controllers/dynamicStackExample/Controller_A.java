

package com.nu.art.cyborg.demo.ui.controllers.dynamicStackExample;

import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class Controller_A
	extends CyborgController {

	public Controller_A() {
		super(R.layout.controller__dynamic_a);
	}
}
