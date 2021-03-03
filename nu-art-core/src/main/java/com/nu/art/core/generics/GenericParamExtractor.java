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

import com.nu.art.core.exceptions.runtime.MUST_NeverHappenException;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.utils.SynchronizedObject;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class GenericParamExtractor {

	private static final Class<? extends IGenericParamExtractor>[] extractorTypes = new Class[]{
		GenericExtractor_Libcore.class,
		GenericExtractor_Sun.class,
		GenericExtractor_Apache.class,
		GenericExtractor_NotSupported.class
	};

	public static final GenericParamExtractor _GenericParamExtractor = new GenericParamExtractor();

	private IGenericParamExtractor extractor;

	private SynchronizedObject<HashMap<String, Type>> params = new SynchronizedObject<>(new Getter<HashMap<String, Type>>() {
		@Override
		public HashMap<String, Type> get() {
			return new HashMap<>();
		}
	});

	private GenericParamExtractor() {
		for (Class<? extends IGenericParamExtractor> extractorType : extractorTypes) {
			try {
				extractor = extractorType.newInstance();
				return;
			} catch (Throwable ignore) {}
		}

		if (extractor == null)
			throw new MUST_NeverHappenException("Error extracting processor generic parameter fields for runtime use");
	}

	@SuppressWarnings("unchecked")
	public <T, K> Class<K> extractGenericType(Class<T> type, T instance, int index) {
		try {
			Class<?> aClass = instance.getClass();
			HashMap<String, Type> genericParamsTypes = params.get();

			do {
				TypeVariable<? extends Class<?>>[] superTypes = aClass.getSuperclass().getTypeParameters();
				Type genericSuperclass = aClass.getGenericSuperclass();
				if (genericSuperclass != Object.class) {
					Type[] e = extractor.getTypes(genericSuperclass);

					for (int i = 0; i < superTypes.length; i++) {
						if (!(e[i] instanceof TypeVariable)) {
							genericParamsTypes.put(superTypes[i].getName(), e[i]);
							continue;
						}

						TypeVariable typeVariable = (TypeVariable) e[i];
						Type remove = genericParamsTypes.remove(typeVariable.getName());
						genericParamsTypes.put(superTypes[i].getName(), remove);
					}
				}

				if (type.isInterface()) {
					Type[] genericInterfaces = aClass.getGenericInterfaces();
					for (Type genericInterface : genericInterfaces) {
						if (extractor.getRawType(genericInterface) != type)
							continue;

						Type[] e1 = extractor.getTypes(genericInterface);
						Type type1 = e1[index];

						Class<K> toRet = extractor.convertToClass(type1);
						if (toRet != null)
							return toRet;

						return (Class<K>) genericParamsTypes.get(((TypeVariable) type1).getName());
					}
				}

				aClass = aClass.getSuperclass();
			} while (aClass != type && aClass != Object.class);

			return (Class<K>) genericParamsTypes.get(type.getTypeParameters()[index].getName());
		} catch (Throwable e) {
			throw new MUST_NeverHappenException("Error extracting processor generic parameter from processor type: " + instance.getClass(), e);
		}
	}
}
