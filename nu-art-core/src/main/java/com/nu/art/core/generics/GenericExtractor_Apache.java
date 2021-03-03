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

package com.nu.art.core.generics;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by TacB0sS on 05/04/2018.
 */

public class GenericExtractor_Apache
	implements IGenericParamExtractor {

	private final Field rawField;
	private Field argsField;

	private Field resolvedTypesField;

	public GenericExtractor_Apache()
		throws NoSuchFieldException, ClassNotFoundException {
		Class<?> parametrizedType = Class.forName("org.apache.harmony.luni.lang.reflect.ImplForType");

		argsField = parametrizedType.getDeclaredField("args");
		argsField.setAccessible(true);

		rawField = parametrizedType.getDeclaredField("rawTypeName");
		rawField.setAccessible(true);

		Class<?> listOfTypes = Class.forName("org.apache.harmony.luni.lang.reflect.ListOfTypes");
		resolvedTypesField = listOfTypes.getDeclaredField("resolvedTypes");
		resolvedTypesField.setAccessible(true);
	}

	@Override
	public Type[] getTypes(Type genericSuperclass)
		throws IllegalAccessException {
		return (Type[]) resolvedTypesField.get(argsField.get(genericSuperclass));
	}

	@Override
	public Type getRawType(Type genericInterface)
		throws IllegalAccessException, ClassNotFoundException {
		return Class.forName((String) rawField.get(genericInterface));
	}

	@Override
	public <K> Class<K> convertToClass(Type type) {
		if (type instanceof Class)
			return (Class<K>) type;

		return null;
	}
}
