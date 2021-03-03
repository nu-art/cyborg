package com.nu.art.modular;

import com.nu.art.modular.core.ModulesPack;
import com.nu.art.modular.module.RealModule;
import com.nu.art.modular.module.MockModule;

import static org.mockito.Mockito.mock;

public class Test_Setup {

	static class TestPack
		extends ModulesPack {

		TestPack() {
			super(RealModule.class, MockModule.class);
		}

		@Override
		protected void init() {
			manager.registerMockModule(MockModule.class, mock(MockModule.class));
		}
	}
}
