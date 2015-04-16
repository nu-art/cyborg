package com.nu.art.software.cyborg.demo.model;

import com.nu.art.software.cyborg.core.ModulesPack;

/**
 * Created by tacb0ss on 4/16/15.
 */
@SuppressWarnings("unchecked")
public class MyModulePack
		extends ModulesPack {


	private MyModulePack() {
		super(MyModule.class);
	}

	@Override
	protected void preBuildModules() {
		// Technically you can ask for any module and PRE-CONFIGURE it before it is initialized.
		getModule(MyModule.class).addString("0");
	}

	@Override
	protected void postBuildModules() {
		// Technically you can ask for any module and PRE-CONFIGURE it before it is initialized.
		getModule(MyModule.class).addString("4");
	}
}
