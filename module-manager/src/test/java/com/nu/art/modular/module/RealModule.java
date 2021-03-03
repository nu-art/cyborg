package com.nu.art.modular.module;

import com.nu.art.modular.core.Module;

public class RealModule
	extends Module {

	MockModule module;

	@Override
	protected void init() {

	}

	public int getValue(int input) {
		return input * 2 + 10;
	}

	public String getString() {
		return module.concatString("a", "b");
	}
}
