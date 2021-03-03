package com.nu.art.storage;

import com.nu.art.storage.Test_Setup.PrefModel;

import static com.nu.art.storage.Test_Utils.deleteAndValidate;
import static com.nu.art.storage.Test_Utils.setAndValidate;
import static com.nu.art.storage.Test_Utils.sleepFor;
import static com.nu.art.storage.Test_Utils.validate;

public class Test_StoragePersistence
	extends Test_StorageCommon {

	@Override
	protected <T> void testModel(PrefModel<T> model) {
		Test_Setup.cleanUp();

		setAndValidate(model.pref, model.value);
		sleepFor(300);

		moduleManager.getModule(PreferencesModule.class).clearMemCache();
		validate(model.pref, model.value);

		deleteAndValidate(model.pref, model.defaultValue);
		sleepFor(300);

		moduleManager.getModule(PreferencesModule.class).clearMemCache();
		validate(model.pref, model.defaultValue);
		logInfo("---------------------------------------------");
	}
}
