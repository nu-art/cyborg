

package com.nu.art.cyborgX.core;

import com.nu.art.cyborgX.modules.Module_HelloWorld;
import com.nu.art.modular.core.ModulesPack;

@SuppressWarnings("unchecked")
public class ModulePack_HelloWorld
	extends ModulesPack {

	private static final Class[] Modules = {
		Module_HelloWorld.class,
	};

	private ModulePack_HelloWorld() {
		super(Modules);
	}

	@Override
	protected void init() {
		// You can get any module declared in the constructor and PRE-CONFIGURE it before it is initialized.
	}

	@Override
	protected void postInit() {
		// You can get any module declared in the constructor and POST-CONFIGURE after it has initialized.
	}
}
