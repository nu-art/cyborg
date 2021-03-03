/*
 * The core of the core of all my projects!
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

package com.nu.art.core.utils;

import com.nu.art.core.tools.ArrayTools;

import java.util.HashMap;

@SuppressWarnings( {
	                   "unused",
	                   "WeakerAccess"
                   })
public class TimeProxy {

	private static TimeProxy instance;
	private static final String DefaultTimeProvider = "System";

	public static TimeProxy getInstance() {
		if (instance == null) {
			instance = new TimeProxy();
			addTimeProvider(new TimeProvider() {
				@Override
				public String getName() {
					return DefaultTimeProvider;
				}

				@Override
				public long currentTimeMillis() {
					return System.currentTimeMillis();
				}
			});
		}

		return instance;
	}

	private final HashMap<String, TimeProvider> allTimeProviders = new HashMap<>();

	private TimeProxy() {
	}

	public static TimeProvider[] getAllProviders() {
		return ArrayTools.asArray(getInstance().allTimeProviders.values(), TimeProvider.class);
	}

	public static void addTimeProvider(TimeProvider timeProvider) {
		getInstance()._addTimeProvider(timeProvider);
	}

	public static TimeProvider getTimeProvider(String providerKey) {
		return getInstance()._getTimeProvider(providerKey);
	}

	public static long currentTimeMillis() {
		return currentTimeMillis(DefaultTimeProvider);
	}

	public static long currentTimeMillis(String key) {
		return getTimeProvider(key).currentTimeMillis();
	}

	private void _addTimeProvider(TimeProvider timeProvider) {
		allTimeProviders.put(timeProvider.getName(), timeProvider);
	}

	private TimeProvider _getTimeProvider(String providerKey) {
		return getInstance().allTimeProviders.get(providerKey);
	}

	public interface TimeProvider {

		String getName();

		long currentTimeMillis();
	}
}
