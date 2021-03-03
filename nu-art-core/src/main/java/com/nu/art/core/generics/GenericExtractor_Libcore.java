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

import com.nu.art.core.tools.ArrayTools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by TacB0sS on 05/04/2018.
 */

public class GenericExtractor_Libcore
	implements IGenericParamExtractor {

	private final Field rawField;
	private final Method getGenericComponentType;
	private Field argsField;

	private Field resolvedTypesField;

	public GenericExtractor_Libcore()
		throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
		Class<?> parametrizedType = Class.forName("libcore.reflect.ParameterizedTypeImpl");
		argsField = parametrizedType.getDeclaredField("args");
		argsField.setAccessible(true);

		rawField = parametrizedType.getDeclaredField("rawTypeName");
		rawField.setAccessible(true);

		Class<?> listOfTypes = Class.forName("libcore.reflect.ListOfTypes");
		resolvedTypesField = listOfTypes.getDeclaredField("resolvedTypes");
		resolvedTypesField.setAccessible(true);

		getGenericComponentType = Class.forName("libcore.reflect.GenericArrayTypeImpl").getMethod("getGenericComponentType");

		//(() type1).getGenericComponentType()
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
	public <K> Class<K> convertToClass(Type type)
		throws InvocationTargetException, IllegalAccessException {
		if (type instanceof Class)
			return (Class<K>) type;

		if (type.getClass().getName().equals("libcore.reflect.GenericArrayTypeImpl")) {
			Class<?> c = convertToClass((Type) getGenericComponentType.invoke(type));
			if (c != null)
				return (Class<K>) ArrayTools.getGenericArrayType(c, 1);
		}

		return null;
	}
}
