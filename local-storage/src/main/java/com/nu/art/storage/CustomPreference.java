/*
 * cyborg-core is an extendable  module based framework for Android.
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.storage;

import com.nu.art.core.interfaces.Serializer;
import com.nu.art.storage.PreferencesModule.StorageImpl;

import java.lang.reflect.Type;

import static com.nu.art.storage.PreferencesModule.EXPIRES_POSTFIX;
import static com.nu.art.storage.PreferencesModule.JsonSerializer._Serializer;

@SuppressWarnings("UnusedReturnValue")
public final class CustomPreference<ItemType>
	extends PreferenceKey<CustomPreference<ItemType>, ItemType> {

	private ItemType cache;
	private Type itemType;
	private Serializer<Object, String> serializer;

	public CustomPreference() {}

	public CustomPreference(String key, Type itemType, ItemType defaultValue) {
		this(key, itemType, _Serializer, defaultValue);
	}

	public CustomPreference(String key, Type itemType, Serializer<Object, String> serializer, ItemType defaultValue) {
		super(key, defaultValue);
		this.itemType = itemType;
		this.serializer = serializer;
	}

	public CustomPreference<ItemType> setItemType(Class<ItemType> itemType, Serializer<Object, String> serializer) {
		this.itemType = itemType;
		this.serializer = serializer;
		return this;
	}

	public CustomPreference<ItemType> setSerializer(Serializer<Object, String> serializer) {
		this.serializer = serializer;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected ItemType _get(StorageImpl preferences, String key, ItemType defaultValue) {
		if (cache != null)
			return cache;

		String value = preferences.get(key, null);
		if (value != null)
			try {
				return setCache((ItemType) serializer.deserialize(value, itemType));
			} catch (Exception e) {
				logError("Error while deserializing item type: " + itemType, e);
			}

		return setCache((ItemType) serializer.deserialize(serializer.serialize(defaultValue), itemType));
	}

	private ItemType setCache(ItemType cache) {
		logDebug("set cache: " + cache);
		return this.cache = cache;
	}

	public void clearMemCache() {
		setCache(null);
	}

	@Override
	protected boolean areEquals(ItemType s1, ItemType s2) {
		return false;
	}

	public void set(final String value) {
		set(value, false);
	}

	public void set(final String value, boolean printToLog) {
		final StorageImpl preferences = getPreferences();
		String savedValue = preferences.get(key, null);

		if (savedValue != null && savedValue.equals(value))
			return;

		if (printToLog)
			logInfo("+----+ SET: " + key + ": " + value);

		setCache(null);
		preferences.put(key, value);
		if (expires != -1)
			preferences.put(key + EXPIRES_POSTFIX, System.currentTimeMillis());
	}

	@Override
	protected void _set(StorageImpl preferences, String key, ItemType value) {
		String valueAsString = value == null ? null : serializer.serialize(value);
		preferences.put(key, valueAsString);
		cache = value;
	}

	public void delete() {
		super.delete();
		setCache(null);
	}
}
