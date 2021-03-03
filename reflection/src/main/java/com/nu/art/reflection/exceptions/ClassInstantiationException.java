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

import com.nu.art.reflection.utils.ClassInstantiationUtility;

import java.lang.reflect.InvocationTargetException;

public class ClassInstantiationException
	extends Exception {

	private static final long serialVersionUID = 8007508391207595997L;

	private static String getInvocationAsDetails(String indentation, Object[] parameters) {
		String toRet = "";
		toRet += "Instantiation parameters details:\n";
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

	private static String generateMessage(ClassInstantiationUtility<?> ciu, Object[] parameters, Exception e) {
		if (e instanceof IllegalAccessException) {
			return "Error accessing constructor: " + ciu.getConstructorAsString() + ".\n" + getInvocationAsDetails("  ", parameters);
		} else if (e instanceof IllegalArgumentException) {
			return "Wrong parameter/s was supplied to constructor invocation: " + ciu.getConstructorAsString() + ".\n" + getInvocationAsDetails("  ", parameters);
		} else if (e instanceof InstantiationException) {
			return "An Exception has occur during the instantiation of the item type: " + ciu.getConstructorAsString() + ".\n" + getInvocationAsDetails("  ", parameters);
		} else if (e instanceof InvocationTargetException) {
			return "An Exception has occur during the invocation of the constructor: " + ciu.getConstructorAsString() + ".\n" + getInvocationAsDetails("  ", parameters);
		} else if (e instanceof WrongParameterType) {
			return "Wrong parameter/s was supplied for constructor invocation: " + ciu.getConstructorAsString() + ".\n" + getInvocationAsDetails("  ", parameters);
		}
		return "Exception while instantiating constructor invocation: " + ciu.getConstructorAsString();
	}

	public ClassInstantiationException(ClassInstantiationUtility<?> ciu, Object[] parameters, Exception e) {
		super("Error instantiating Class type '" + ciu.getInstanceType() + "'\n" + generateMessage(ciu, parameters, e), e);
	}
}
