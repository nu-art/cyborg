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

import java.util.HashMap;

@SuppressWarnings( {
	                   "rawtypes",
	                   "unchecked"
                   })
public final class SingleToneMap {

	protected final HashMap<Class<?>, HashMap> maps = new HashMap<>();

	public final <MapType> HashMap<Class<?>, MapType> getMap(Class<MapType> mapClassType) {
		HashMap map = maps.get(mapClassType);
		if (map == null) {
			map = new HashMap<>();
			maps.put(mapClassType, map);
		}
		return map;
	}

	/**
	 * Disposes of the stored parsers in this reflective analyzer instance.
	 */
	public final void dispose() {
		maps.clear();
	}

	public <MapType, Type> MapType getObject(Class<MapType> mapKey, Class<Type> aClass) {
		HashMap<Class<?>, MapType> map = getMap(mapKey);
		return map.get(aClass);
	}

	public <MapType, Type> void putObject(Class<MapType> mapKey, Class<Type> aClass, MapType instance) {
		HashMap<Class<?>, MapType> map = getMap(mapKey);
		map.put(aClass, instance);
	}
}
