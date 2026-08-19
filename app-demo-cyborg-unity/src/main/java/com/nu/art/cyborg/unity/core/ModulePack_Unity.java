package com.nu.art.cyborg.unity.core;

import com.nu.art.cyborg.unity.modules.Module_Unity;
import com.nu.art.modular.core.ModulesPack;

@SuppressWarnings("unchecked")
public class ModulePack_Unity
	extends ModulesPack {

	private static final Class[] Modules = {
		Module_Unity.class,
	};

	private ModulePack_Unity() {
		super(Modules);
	}
}
