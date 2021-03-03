package com.nu.art.storage;

import com.nu.art.storage.Test_Setup.PrefModel;

import org.junit.BeforeClass;

import static com.nu.art.storage.Test_Utils.deleteAndValidate;
import static com.nu.art.storage.Test_Utils.getAndValidate;
import static com.nu.art.storage.Test_Utils.setAndValidate;

public class Test_Storage
	extends Test_StorageCommon {

	protected <T> void testModel(PrefModel<T> model) {
		for (int i = 0; i < 5; i++) {
			Test_Setup.cleanUp();
			getAndValidate(model.pref, model.defaultValue);
			setAndValidate(model.pref, model.value);
			deleteAndValidate(model.pref, model.defaultValue);

			logInfo("---------------------------------------------");
		}
	}
}
