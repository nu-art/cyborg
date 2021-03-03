package com.nu.art.storage;

import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.modular.tests.ModuleManager_TestClass;
import com.nu.art.storage.Test_Setup.JsonSerializer;
import com.nu.art.storage.Test_Setup.PrefModel;

import org.junit.Test;

import java.util.HashMap;

import static com.nu.art.storage.Test_Utils.sleepFor;

public class Test_CustomPref
	extends Test_StorageBase {

	@Test
	public void test_CustomPrefsStateful() {
		for (int i = 0; i < 10; i++) {
			Test_Setup.cleanUp();

			PrefModel<HashMap> model = Test_Setup.getCustomModelStateful();
			HashMap hashMap = model.pref.get();
			hashMap.put("pah", "zevel");
			model.pref.set(hashMap);

			sleepFor(300);

			getModule(PreferencesModule.class).clearMemCache();
			Object value = model.pref.get().get("pah");
			if (value == null)
				throw new BadImplementationException("did not save map correctly");

			if (!"zevel".equals(value))
				throw new BadImplementationException("Wrong value from map");

			model.pref.delete();

			value = model.pref.get().get("pah");
			if (value != null)
				throw new BadImplementationException("expected empty map.. but found value");
		}
	}

	@Test
	public void test_CustomPrefsAsString() {

		for (int i = 0; i < 10; i++) {
			PrefModel<HashMap> model = Test_Setup.getCustomModelStateful();
			HashMap hashMap = model.pref.get(true);
			//			hashMap.put("pah", "zevel");
			//			model.pref.set(hashMap);

			String key = "key1";
			String storedValue = "value12";
			hashMap = new HashMap();
			hashMap.put(key, storedValue);

			((CustomPreference) model.pref).set(JsonSerializer.gson.toJson(hashMap), true);

			sleepFor(300);

			getModule(PreferencesModule.class).clearMemCache();
			Object value = model.pref.get(true).get(key);
			if (value == null)
				throw new BadImplementationException("did not save map correctly");

			if (!storedValue.equals(value))
				throw new BadImplementationException("Wrong value from map");

			model.pref.delete();

			value = model.pref.get(true).get(key);
			if (value != null)
				throw new BadImplementationException("expected empty map.. but found value");

			logInfo("+---------------------------------------------+");
		}
	}
}
