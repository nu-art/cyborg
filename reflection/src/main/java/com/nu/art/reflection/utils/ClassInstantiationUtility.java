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

import java.lang.reflect.Constructor;

public final class ClassInstantiationUtility<InstanceType> {

	/**
	 * The class type parameters as would be supplied to the invocation of the constructor.
	 */
	private Class<?>[] parameterTypes;

	/**
	 * The constructor instance which would be invoked.
	 */
	private final Constructor<InstanceType> constructor;

	/**
	 * The instance type to instantiate.
	 */
	private final Class<InstanceType> instanceType;

	public ClassInstantiationUtility(Class<InstanceType> instanceType, Class<?>... parameterTypes)
		throws com.nu.art.reflection.exceptions.ConstructorNotFoundException {
		this.instanceType = instanceType;
		this.parameterTypes = parameterTypes;
		constructor = findAConstructor(instanceType, parameterTypes);
		constructor.setAccessible(true);
	}

	public ClassInstantiationUtility(Constructor<InstanceType> constructor) {
		this.parameterTypes = constructor.getParameterTypes();
		this.instanceType = constructor.getDeclaringClass();
		this.constructor = constructor;
		constructor.setAccessible(true);
	}

	private boolean checkParameters(Object... parameters)
		throws com.nu.art.reflection.exceptions.WrongParameterType {
		if (parameters.length != parameterTypes.length) {
			return false;
		}
		for (int i = 0; i < parameters.length; i++) {
			if (!parameters[i].getClass().isAssignableFrom(parameterTypes[i])) {
				throw new com.nu.art.reflection.exceptions.WrongParameterType(i, parameters[i], parameterTypes[i]);
			}
		}
		return true;
	}

	public final Class<InstanceType> getInstanceType() {
		return instanceType;
	}

	/**
	 * Checks if the supplied parameters types are assignable from the constructor parameters types.
	 *
	 * @param suppliedParameterTypes    the supplied parameters.
	 * @param constructorParameterTypes The constructor parameters.
	 *
	 * @return true, if the parameters match, false otherwise.
	 */
	private boolean compareConstructorParametersTypes(Class<?>[] suppliedParameterTypes, Class<?>[] constructorParameterTypes) {
		if (suppliedParameterTypes.length != constructorParameterTypes.length) {
			return false;
		}
		for (int i = 0; i < suppliedParameterTypes.length; i++) {
			if (!constructorParameterTypes[i].isAssignableFrom(suppliedParameterTypes[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Searches for the constructor with the specified parameters in the supplied class object.
	 *
	 * @param _class         The class which contains the constructor with the specified parameters.
	 * @param parameterTypes The constructor parameters types.
	 *
	 * @return The constructor instance of the supplied class.
	 *
	 * @throws com.nu.art.reflection.exceptions.ConstructorNotFoundException if a constructor with the supplied specifications was not found.
	 */
	@SuppressWarnings("unchecked")
	private Constructor<InstanceType> findAConstructor(Class<InstanceType> _class, Class<?>[] parameterTypes)
		throws com.nu.art.reflection.exceptions.ConstructorNotFoundException {
		Constructor<?>[] constructors = _class.getConstructors();
		for (Constructor<?> constructor2 : constructors) {
			if (compareConstructorParametersTypes(parameterTypes, constructor2.getParameterTypes())) {
				return (Constructor<InstanceType>) constructor2;
			}
		}
		throw new com.nu.art.reflection.exceptions.ConstructorNotFoundException("There was no match for Constructor: \n  " + _class.getSimpleName() + "(" + ReflectiveTools
			.parseParametersType(parameterTypes) + "); \n  In the specified class object: " + _class.getName());
	}

	public String getConstructorAsString() {
		return constructor.getName() + "(" + ReflectiveTools.parseParametersType(parameterTypes) + ")";
	}

	public final InstanceType newInstance(Object... parameters)
		throws com.nu.art.reflection.exceptions.ClassInstantiationException {
		try {
			checkParameters(parameters);
			return constructor.newInstance(parameters);
		} catch (Exception e) {
			throw new com.nu.art.reflection.exceptions.ClassInstantiationException(this, parameters, e);
		}
	}
}
