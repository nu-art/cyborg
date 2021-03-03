package com.nu.art.modular.module;

import com.nu.art.modular.core.Module;

public class MockModule
	extends Module {

	@Override
	protected void init() { }

	public String concatString(String str1, String str2) {
		return str1 + str2;
	}
}
