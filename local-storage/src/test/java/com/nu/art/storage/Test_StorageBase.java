package com.nu.art.storage;

import com.nu.art.modular.tests.ModuleManager_TestClass;
import com.nu.art.storage.Test_Setup.Pack;
import com.nu.art.storage.Test_Setup.PrefModel;

import org.junit.BeforeClass;
import org.junit.Test;

public abstract class Test_StorageBase
	extends ModuleManager_TestClass {

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUp() {
		initWithPacks(Pack.class);
	}
}
