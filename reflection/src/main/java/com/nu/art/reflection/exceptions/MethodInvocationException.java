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

package com.nu.art.reflection.exceptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.nu.art.reflection.tools.ReflectiveTools;

public class MethodInvocationException
	extends Exception {

	private static final long serialVersionUID = 1346980994902830057L;

	private static String getInvocationAsDetails(String indentation, Object instance, Object[] parameters) {
		String toRet = "";
		toRet += indentation + "Instance Details:\n";
		toRet += indentation + "Type: " + instance.getClass().getName() + "\n";
		toRet += indentation + "ToString: " + instance.toString() + "\n\n";
		toRet += "Invocation parameters details:\n";
		for (int i = 0; i < parameters.length; i++) {
			toRet += indentation + "Parameter #" + i + ": \n";
			toRet += indentation + "Type: " + parameters[i].getClass().getName() + "\n";
			toRet += indentation + "ToString: " + parameters + "\n";
			if (i < parameters.length - 1) {
				toRet += "\n";
			}
		}
		return toRet;
	}

	public MethodInvocationException(Method method, Object instance, Object[] parameters, Exception e) {
		super(generateMessage(method, instance, parameters, e), e);
	}

	private static String generateMessage(Method method, Object instance, Object[] parameters, Exception e) {
		if (e instanceof IllegalAccessException) {
			return "An Exception has accessing method: " + ReflectiveTools.getMethodAsString(method) + ".\n" + MethodInvocationException.getInvocationAsDetails("  ", instance, parameters);
		} else if (e instanceof IllegalArgumentException) {
			return "Wrong parameter/s was supplied to method invocation: " + ReflectiveTools.getMethodAsString(method) + ".\n" + MethodInvocationException.getInvocationAsDetails("  ", instance, parameters);
		} else if (e instanceof InvocationTargetException) {
			return "An Exception has occur during the invocation of the method: " + ReflectiveTools.getMethodAsString(method) + ".\n" + MethodInvocationException.getInvocationAsDetails("  ", instance, parameters);
		} else if (e instanceof WrongParameterType) {
			return "Wrong parameter/s was supplied to method invocation: " + ReflectiveTools.getMethodAsString(method) + ".\n" + MethodInvocationException.getInvocationAsDetails("  ", instance, parameters);
		}
		return e.getMessage();
	}
}
