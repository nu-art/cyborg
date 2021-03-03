package com.nu.art.modular;

import com.nu.art.modular.Test_Setup.TestPack;
import com.nu.art.modular.module.MockModule;
import com.nu.art.modular.module.RealModule;
import com.nu.art.modular.tests.ModuleManager_TestClass;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class Test_Modules
	extends ModuleManager_TestClass {

	private MockModule mockModule;
	private RealModule module;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUp() {
		initWithPacks(TestPack.class);
	}

	@Test
	public void test() {

		when(mockModule.concatString(anyString(), anyString())).thenReturn("nothing to see here");
		logDebug("value: " + module.getString());
	}
}
