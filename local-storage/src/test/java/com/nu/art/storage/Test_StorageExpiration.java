package com.nu.art.storage;

import com.nu.art.storage.Test_Setup.PrefModel;

import static com.nu.art.storage.Test_Utils.setAndValidate;
import static com.nu.art.storage.Test_Utils.sleepFor;
import static com.nu.art.storage.Test_Utils.validate;

public class Test_StorageExpiration
	extends Test_StorageCommon {

	@Override
	protected <T> void testModel(PrefModel<T> model) {
		Test_Setup.cleanUp();

		model.pref.setExpires(1000);
		setAndValidate(model.pref, model.value);
		sleepFor(300);

		validate(model.pref, model.value);
		sleepFor(300);
		moduleManager.getModule(PreferencesModule.class).clearMemCache();

		validate(model.pref, model.value);
		sleepFor(500);

		validate(model.pref, model.defaultValue);
	}
}
