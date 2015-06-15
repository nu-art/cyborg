/*
 *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *  Restricted usage under Binpress license
 *  
 *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
 */

package com.nu.art.software.cyborg.demo.model;

import android.app.Application;
import android.content.SharedPreferences;

import com.nu.art.software.cyborg.annotations.ModuleDescriptor;
import com.nu.art.software.cyborg.core.CyborgModule;
import com.nu.art.software.cyborg.core.modules.PreferencesModule;
import com.nu.art.software.cyborg.core.modules.PreferencesModule.PreferenceKey;
import com.nu.art.software.cyborg.core.modules.PreferencesModule.PreferencesStorage;
import com.nu.art.software.cyborg.demo.ui.controllers.StorageExampleController;

/**
 * In the past you needed to write so much code to save or load an entry to and from the shared preferences... today, one line!<br>
 * Check out the {@link StorageExampleController}
 */
@ModuleDescriptor
public final class MyStorageModule
		extends CyborgModule {

	/**
	 * These represent the {@link SharedPreferences} details for the {@link PreferencesStorage} to use!
	 *
	 * @author TacB0sS
	 */
	public enum CyborgDemoPreferences
			implements PreferencesStorage {
		Persistent("Persistent", Application.MODE_PRIVATE),
		Private("Default", Application.MODE_PRIVATE),
		OtherStuff("OtherStuff", Application.MODE_PRIVATE),;

		private String preferencesName;

		private int mode;

		CyborgDemoPreferences(String name, int mode) {
			this.preferencesName = name;
			this.mode = mode;
		}

		@Override
		public String getPreferencesName() {
			return preferencesName;
		}

		@Override
		public int getMode() {
			return mode;
		}
	}

	/**
	 * The value set will expire 30 seconds after setting it.
	 */
	public PreferenceKey<String> ExpiredString;

	/**
	 * This will be stored in the private shared preferences.
	 */
	public PreferenceKey<String> PrivateString;

	/**
	 * This will be a public integer.
	 */
	public PreferenceKey<Integer> IntegerValue;

	/**
	 * Just another example to emphasis the type restriction this Architecture have.
	 */
	public PreferenceKey<Long> LongValue;

	@Override
	protected void init() {
		PreferencesModule preferences = getModule(PreferencesModule.class);
		ExpiredString = preferences.new StringPreference("Private - This will expire", "No Value Set", 30000, CyborgDemoPreferences.Persistent);
		PrivateString = preferences.new StringPreference("Private - This Key Saves String", "If I would ever need a default value it would be here with the rest of the default values I would ever use in the entire app!!!");
		IntegerValue = preferences.new IntegerPreference("Public - This Key Saves an int", -111, CyborgDemoPreferences.OtherStuff);
		LongValue = preferences.new LongPreference("Public - This Key Saves a long", -1L, CyborgDemoPreferences.OtherStuff);
	}
}
