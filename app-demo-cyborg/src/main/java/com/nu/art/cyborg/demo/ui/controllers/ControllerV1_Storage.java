

package com.nu.art.cyborg.demo.ui.controllers;

import android.os.Handler;

import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.storage.EnumPreference;
import com.nu.art.storage.PreferencesModule;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.demo.model.MyStorageModule;
import com.nu.art.cyborg.demo.model.MyStorageModule.CyborgDemoPreferences;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

import java.util.UUID;

import static com.nu.art.cyborg.core.consts.LifecycleState.OnDestroy;

/**
 * Cyborg has a built-in TypeSafePreferences, inorder to use them follow the {@link MyStorageModule} and see how the preferences are defined...
 */
@ReflectiveInitialization
public class ControllerV1_Storage
	extends CyborgController {

	public enum MyEnum {
		Value1,
		Value2,
		Value3,
	}

	EnumPreference<MyEnum> dynamicValue;

	PreferencesModule preferences;

	MyStorageModule myStorage;

	private ControllerV1_Storage() {
		super(R.layout.controller__storage_example);
	}

	@Override
	public void onCreate() {
		preferences.getStorage(CyborgDemoPreferences.Private.storageGroup).clear();
		preferences.getStorage(CyborgDemoPreferences.OtherStuff.storageGroup).clear();
		dynamicValue = new EnumPreference<>("Dynamic Enum", MyEnum.class, MyEnum.Value1);

		MyEnum defaultEnum = dynamicValue.get();
		logInfo("defaultEnum: " + defaultEnum);

		myStorage.privateString.set("My little secret string...");
		myStorage.integerValue.set(453453);
		myStorage.longValue.set(123L);
		myStorage.expiredString.set("WE HAVE A VALUE!!!!");
		final long setAt = System.currentTimeMillis();
		dynamicValue.set(MyEnum.Value2);

		String privateString = myStorage.privateString.get();
		int publicInt = myStorage.integerValue.get();
		long otherLong = myStorage.longValue.get();
		MyEnum enumValue = dynamicValue.get();

		logInfo("privateString: " + privateString);
		logInfo("--- publicInt: " + publicInt);
		logInfo("--- otherLong: " + otherLong);
		logInfo("--- enumValue: " + enumValue);

		final Handler handler = new Handler();
		final String valueThatWillExpire = myStorage.expiredString.get();
		Runnable printValue = new Runnable() {

			@Override
			public void run() {
				if (isState(OnDestroy))
					return;

				String value = myStorage.expiredString.get();
				logInfo("time passed: " + ((System.currentTimeMillis() - setAt) / 1000 + "sec, --- Value: " + value));
				if (valueThatWillExpire.equals(value))
					handler.postDelayed(this, 1000);
			}
		};
		handler.postDelayed(printValue, 1000);

		String uuid = UUID.randomUUID().toString();
		logInfo("uuid: " + uuid);
		myStorage.uuidString.set(uuid);
		myStorage.uuidString.get();
		logInfo("GOT uuid: " + uuid);
		myStorage.uuidString.delete();
		myStorage.uuidString.get();
		logInfo("Deleted uuid: " + uuid);
	}
}
