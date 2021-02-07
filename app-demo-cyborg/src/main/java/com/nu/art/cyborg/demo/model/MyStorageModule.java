

package com.nu.art.cyborg.demo.model;

import com.nu.art.cyborg.annotations.ModuleDescriptor;
import com.nu.art.cyborg.core.CyborgModule;
import com.nu.art.storage.IntegerPreference;
import com.nu.art.storage.LongPreference;
import com.nu.art.storage.StringPreference;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_Storage;

/**
 * In the past you needed to write so much code to save or load an entry to and from the shared preferences... today, one line!<br>
 * Check out the {@link ControllerV1_Storage}
 */
@ModuleDescriptor
public final class MyStorageModule
	extends CyborgModule {

	/**
	 * @author TacB0sS
	 */
	public enum CyborgDemoPreferences {
		Persistent("STAM"),
		Private("Harti"),
		OtherStuff("Barti"),
		;

		public final String storageGroup;

		CyborgDemoPreferences(String name) {
			this.storageGroup = name;
		}
	}

	/**
	 * The value set will expire 30 seconds after setting it.
	 */
	public StringPreference expiredString;

	/**
	 * This will be stored in the private shared preferences.
	 */
	public StringPreference privateString;

	/**
	 * This will be stored in the private shared preferences.
	 */
	public StringPreference uuidString = new StringPreference("uuid", "No Value Set").setExpires(30000)
	                                                                                 .setStorageGroup(CyborgDemoPreferences.Persistent.storageGroup);

	/**
	 * This will be a public integer.
	 */
	public IntegerPreference integerValue;

	/**
	 * Just another example to emphasis the type restriction this Architecture have.
	 */
	public LongPreference longValue;

	@Override
	protected void init() {
		expiredString = new StringPreference("Private - This will expire", "No Value Set").setExpires(30000)
		                                                                                  .setStorageGroup(CyborgDemoPreferences.Persistent.storageGroup);
		privateString = new StringPreference("Private - This Key Saves String", "If I would ever need a default value it would be here with the rest of the default values I would ever use in the entire app!!!");
		integerValue = new IntegerPreference("Public - This Key Saves an int", -111).setStorageGroup(CyborgDemoPreferences.OtherStuff.storageGroup);
		longValue = new LongPreference("Public - This Key Saves a long", -1L).setStorageGroup(CyborgDemoPreferences.OtherStuff.storageGroup);
	}
}
