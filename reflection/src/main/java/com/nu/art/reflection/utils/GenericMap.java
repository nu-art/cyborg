/*
 * The reflection project, is collection of reflection tools I've picked up
 * along the way, use it wisely!
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

package com.nu.art.reflection.utils;

import com.nu.art.reflection.tools.ReflectiveTools;

import java.util.HashMap;

public class GenericMap<Type>
	extends HashMap<Class<? extends Type>, Type> {

	public final Type getOrCreate(Class<? extends Type> instanceType) {
		Type instance = get(instanceType);

		if (instance == null) {
			instance = ReflectiveTools.newInstance(instanceType);
			put(instanceType, instance);
		}

		return instance;
	}
}