

package com.nu.art.cyborg.demo.ui.controllers.injection;

import com.nu.art.cyborg.annotations.Restorable;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

/**
 * This example gives you a small impression of what Cyborg can do for you in terms of minimizing code and utilizing events and callbacks...
 */
@Restorable
@ReflectiveInitialization
public class Controller_InjectedController
	extends CyborgController {

	private Controller_InjectedController() {
		super(R.layout.controller__injected_controller);
	}
}
