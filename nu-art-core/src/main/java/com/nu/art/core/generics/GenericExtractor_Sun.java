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
import java.lang.reflect.Type;

import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;

/**
 * Created by TacB0sS on 05/04/2018.
 */

@SuppressWarnings("unchecked")
public class GenericExtractor_Sun
	implements IGenericParamExtractor {

	private final Field actualTypeField;
	private final Field rawField;

	GenericExtractor_Sun()
		throws NoSuchFieldException, ClassNotFoundException {
		Class<?> parametrizedType = Class.forName("sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl");

		actualTypeField = parametrizedType.getDeclaredField("actualTypeArguments");
		actualTypeField.setAccessible(true);

		rawField = parametrizedType.getDeclaredField("rawType");
		rawField.setAccessible(true);
	}

	@Override
	public Type[] getTypes(Type genericSuperclass)
		throws IllegalAccessException {
		return (Type[]) actualTypeField.get(genericSuperclass);
	}

	@Override
	public Type getRawType(Type genericInterface)
		throws IllegalAccessException {
		return (Type) rawField.get(genericInterface);
	}

	@Override
	public <K> Class<K> convertToClass(Type type) {
		if (type instanceof Class)
			return (Class<K>) type;

		if (type instanceof GenericArrayTypeImpl) {
			Class<?> c = convertToClass(((GenericArrayTypeImpl) type).getGenericComponentType());
			if (c != null)
				return (Class<K>) ArrayTools.getGenericArrayType(c, 1);
		}

		return null;
	}
}
