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

import com.nu.art.reflection.exceptions.MethodInvocationException;
import com.nu.art.reflection.exceptions.MethodNotFoundException;
import com.nu.art.reflection.tools.ReflectiveTools;

import java.lang.reflect.Method;

public class MethodInvocationUtility<InstanceType> {

	/**
	 * The class type parameters as would be supplied to the invocation of the method.
	 */
	private Class<?>[] parameterTypes;

	/**
	 * The method instance which would be invoked.
	 */
	private Method method;

	public MethodInvocationUtility(Class<InstanceType> instanceType, String methodName, Class<?>... parameterTypes)
		throws MethodNotFoundException {
		this.parameterTypes = parameterTypes;
		method = findAMethod(instanceType, methodName, parameterTypes);
		method.setAccessible(true);
	}

	public MethodInvocationUtility(Method method) {
		this.parameterTypes = method.getParameterTypes();
		this.method = method;
		method.setAccessible(true);
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

	public final Method getMethod() {
		return method;
	}

	/**
	 * Checks if the supplied parameters types are assignable from the method parameters types.
	 *
	 * @param suppliedParameterTypes the supplied parameters.
	 * @param methodParameterTypes   The method parameters.
	 *
	 * @return true, if the parameters match, false otherwise.
	 */
	private boolean compareMethodParametersTypes(Class<?>[] suppliedParameterTypes, Class<?>[] methodParameterTypes) {
		if (suppliedParameterTypes.length != methodParameterTypes.length) {
			return false;
		}
		for (int i = 0; i < suppliedParameterTypes.length; i++) {
			if (!methodParameterTypes[i].isAssignableFrom(suppliedParameterTypes[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Searches for the method with the specified parameters in the supplied class object.
	 *
	 * @param _class         The class which contains the method with the specified parameters.
	 * @param methodName     The method name to search for.
	 * @param parameterTypes The method parameters types.
	 *
	 * @return The method instance of the supplied class.
	 *
	 * @throws MethodNotFoundException if a method with the supplied specifications was not found.
	 */
	private Method findAMethod(Class<InstanceType> _class, String methodName, Class<?>[] parameterTypes)
		throws MethodNotFoundException {
		Method[] methods = _class.getDeclaredMethods();
		for (Method method : methods) {
			if (!method.getName().equals(methodName)) {
				continue;
			}
			if (compareMethodParametersTypes(parameterTypes, method.getParameterTypes())) {
				return method;
			}
		}
		throw new MethodNotFoundException("There was no match for method: \n  " + methodName + "(" + ReflectiveTools.parseParametersType(parameterTypes) + "); \n  In the specified class object: " + _class
			.getName());
	}

	public String getMethodName() {
		return method.getName();
	}

	public final Object invokeMethod(InstanceType instance, Object... parameters)
		throws MethodInvocationException {
		try {
			checkParameters(parameters);
			return method.invoke(instance, parameters);
		} catch (Exception e) {
			throw new MethodInvocationException(method, instance, parameters, e);
		}
	}
}
