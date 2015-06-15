/*
 *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *  Restricted usage under Binpress license
 *
 *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
 */

package com.nu.art.software.cyborg.demo.model;

import com.nu.art.software.cyborg.core.ModulesPack;

/**
 * Created by TacB0sS on 18-Apr 2015.
 */
@SuppressWarnings("unchecked")
public class MyModulePack
		extends ModulesPack {

	private MyModulePack() {
		super(MyModule.class, MyStorageModule.class);
	}

	@Override
	protected void preBuildModules() {
		// You can get any module declared in the constructor and PRE-CONFIGURE it before it is initialized.
		getModule(MyModule.class).addString("0");
	}

	@Override
	protected void postBuildModules() {
		// You can get any module declared in the constructor and POST-CONFIGURE after it has initialized.
		getModule(MyModule.class).addString("4");
	}
}
