package com.nu.art.storage;

import com.nu.art.core.exceptions.runtime.BadImplementationException;

public class Test_Utils {

	public static void sleepFor(int interval) {
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static <T> void setAndValidate(PreferenceKey<?, T> pref, T value) {
		pref.set(value);
		validate(pref, value);
	}

	public static <T> void getAndValidate(PreferenceKey<?, T> pref, T value) {
		validate(pref, value);
	}

	public static <T> void deleteAndValidate(PreferenceKey<?, T> pref, T defaultValue) {
		pref.delete();
		validate(pref, defaultValue);
	}

	public static <T> void validate(PreferenceKey<?, T> pref, T value) {
		T got = pref.get(true);
		if (!got.equals(value))
			throw new BadImplementationException("didn't receive expected value: " + value + " - got: " + got);
	}
}
