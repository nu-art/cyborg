

package com.nu.art.cyborg.demo.ui.controllers.liveModule;

import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.modular.core.HackApi;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class Controller_LiveModule
	extends CyborgController {

	static LiveModule module = new LiveModule();

	public Controller_LiveModule() {
		super(R.layout.controller__dynamic_a);
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		getRootView().setBackgroundColor(getColor(R.color.white));
		HackApi.registerModuleInstance(module);
	}
}
