package com.nu.art.storage;

import com.nu.art.storage.Test_Setup.PrefModel;

import org.junit.Test;

public abstract class Test_StorageCommon
	extends Test_StorageBase {

	@Test
	public void test_CustomPrefsMem() {
		testModel(Test_Setup.getCustomModel());
	}

	@Test
	public void test_EnumPrefsMem() {
		testModel(Test_Setup.getEnumModel());
	}

	@Test
	public void test_LongPrefsMem() {
		testModel(Test_Setup.getLongModel());
	}

	@Test
	public void test_FloatPrefsMem() {
		testModel(Test_Setup.getFloatModel());
	}

	@Test
	public void test_DoublePrefsMem() {
		testModel(Test_Setup.getDoubleModel());
	}

	@Test
	public void test_StringPrefsMem() {
		testModel(Test_Setup.getStringModel());
	}

	@Test
	public void test_IntegerPrefsMem() {
		testModel(Test_Setup.getIntegerModel());
	}

	protected abstract <T> void testModel(PrefModel<T> model);
}
