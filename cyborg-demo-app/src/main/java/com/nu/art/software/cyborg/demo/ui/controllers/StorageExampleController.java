/*
 *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *  Restricted usage under Binpress license
 *
 *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
 */

package com.nu.art.software.cyborg.demo.ui.controllers;

import android.os.Handler;
import android.util.Log;

import com.nu.art.software.cyborg.core.CyborgViewController;
import com.nu.art.software.cyborg.core.modules.PreferencesModule;
import com.nu.art.software.cyborg.core.modules.PreferencesModule.PreferenceEnumKey;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.cyborg.demo.model.MyStorageModule;
import com.nu.art.software.cyborg.demo.model.MyStorageModule.CyborgDemoPreferences;
import com.nu.art.software.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class StorageExampleController
		extends CyborgViewController {

	public enum MyEnum {
		Value1,
		Value2,
		Value3,
	}

	PreferenceEnumKey<MyEnum> dynamicValue;

	PreferencesModule preferences;

	MyStorageModule myStorage;

	private StorageExampleController() {
		super(R.layout.v1_controller__storage_example);
	}

	@Override
	public void onCreate() {
		preferences.dropPreferences(CyborgDemoPreferences.Private);
		preferences.dropPreferences(CyborgDemoPreferences.OtherStuff);
		dynamicValue = preferences.new PreferenceEnumKey<MyEnum>("Dynamic Enum", MyEnum.Value1);

		MyEnum defaultEnum = dynamicValue.get();
		Log.i("GenericPreferences", "defaultEnum: " + defaultEnum);

		myStorage.PrivateString.set("My little secret string...");
		myStorage.IntegerValue.set(453453);
		myStorage.LongValue.set(123L);
		myStorage.ExpiredString.set("WE HAVE A VALUE!!!!");
		final long setAt = System.currentTimeMillis();
		dynamicValue.set(MyEnum.Value2);

		String privateString = myStorage.PrivateString.get();
		int publicInt = myStorage.IntegerValue.get();
		long otherLong = myStorage.LongValue.get();
		MyEnum enumValue = dynamicValue.get();

		Log.i("GenericPreferences", "privateString: " + privateString);
		Log.i("GenericPreferences", "--- publicInt: " + publicInt);
		Log.i("GenericPreferences", "--- otherLong: " + otherLong);
		Log.i("GenericPreferences", "--- enumValue: " + enumValue);

		final Handler handler = new Handler();
		final String valueThatWillExpire = myStorage.ExpiredString.get();
		Runnable printValue = new Runnable() {

			@Override
			public void run() {
				String value = myStorage.ExpiredString.get();
				Log.i("GenericPreferences", "time passed: " + ((System.currentTimeMillis() - setAt) / 1000 + "sec, --- Value: " + value));
				if (valueThatWillExpire.equals(value))
					handler.postDelayed(this, 1000);
			}
		};
		handler.postDelayed(printValue, 1000);
	}
}
