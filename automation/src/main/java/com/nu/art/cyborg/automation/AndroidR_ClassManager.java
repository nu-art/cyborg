/*
 * automation is the scenario automation testing framework allowing
 * the app to record last user actions, and in case of a crash serialize
 * the scenario into a file..
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

package com.nu.art.cyborg.automation;

import com.nu.art.reflection.tools.ReflectiveTools;

import java.util.ArrayList;
import java.util.HashMap;

public final class AndroidR_ClassManager {

	private static final class ValuesMap {

		private final Class<?> resourceClass;

		/**
		 * A constant to class field name mapping.
		 */
		private HashMap<Object, Object> resourcesValues = new HashMap<>();

		ValuesMap(Class<?> resourceClass) {
			this.resourceClass = resourceClass;
			resourcesValues = ReflectiveTools.getFieldsCrossMappings(resourceClass);
		}

		@SuppressWarnings("unused")
		public Class<?> getResourceClass() {
			return resourceClass;
		}

		String getName(int id) {
			return (String) resourcesValues.get(id);
		}

		Integer getId(String name) {
			return (Integer) resourcesValues.get(name);
		}
	}

	private static final class ResourcesMap {

		/**
		 * An inner resources class name to its values map.
		 */
		private HashMap<String, ValuesMap> resourcesValuesMap = new HashMap<>();

		private Class<?> rClass;

		ResourcesMap(Class<?> rClass) {
			this.rClass = rClass;
			Class<?>[] resourcesClasses = rClass.getDeclaredClasses();
			for (Class<?> resourceClass : resourcesClasses) {
				if (resourceClass.getSimpleName().equals("id")) {
					resourcesValuesMap.put(resourceClass.getSimpleName(), new ValuesMap(resourceClass));
				}
			}
		}

		@SuppressWarnings("unused")
		public Class<?> getR_Class() {
			return rClass;
		}

		String getName(ResourceType resourceType, int id) {
			return resourcesValuesMap.get(resourceType.getClassName()).getName(id);
		}

		Integer getId(ResourceType resourceType, String name) {
			return resourcesValuesMap.get(resourceType.getClassName()).getId(name);
		}
	}

	/**
	 * Maps the R class to its ResourcesMap
	 */
	private ResourcesMap[] resourcesMap;

	// TODO: Been a while since I wrote this code... might only need to app R file
	public AndroidR_ClassManager(Class<?>... rClasses) {
		super();
		ArrayList<ResourcesMap> resourcesMaps = new ArrayList<>();
		for (Class<?> rClass : rClasses) {
			resourcesMaps.add(new ResourcesMap(rClass));
		}

		this.resourcesMap = resourcesMaps.toArray(new ResourcesMap[resourcesMaps.size()]);
	}

	public synchronized final String getName(ResourceType resourceType, int id) {
		String name;
		for (ResourcesMap resourceMap : resourcesMap) {
			name = resourceMap.getName(resourceType, id);

			if (name != null)
				return name;
		}

		throw new IllegalArgumentException("Could not find field name for ResourceType '" + resourceType + "' && Id '" + id + "'");
	}

	public synchronized final int getId(ResourceType resourceType, String name) {
		Integer id;
		for (ResourcesMap resourceMap : resourcesMap) {
			id = resourceMap.getId(resourceType, name);

			if (id != null)
				return id;
		}

		throw new IllegalArgumentException("Could not find field Id for ResourceType '" + resourceType + "' && Field Name '" + name + "'");
	}
}
