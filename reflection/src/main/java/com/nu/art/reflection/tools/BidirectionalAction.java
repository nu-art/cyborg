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

package com.nu.art.reflection.tools;

import com.nu.art.reflection.utils.MethodInvocationUtility;

public abstract class BidirectionalAction<Type> {

	protected MethodInvocationUtility<Type> forwardInvocation;

	protected MethodInvocationUtility<Type> backwardInvocation;

	public BidirectionalAction(Class<Type> type, String redoMethodName, Object[] redoParameters, String undoMethodName, Object[] undoParameters)
		throws Exception {
		forwardInvocation = new MethodInvocationUtility<>(type, redoMethodName, ReflectiveTools.getInstancesTypes(redoParameters));
		backwardInvocation = new MethodInvocationUtility<>(type, undoMethodName, ReflectiveTools.getInstancesTypes(undoParameters));
	}

	public BidirectionalAction(Class<Type> type, String forwardMethod, String backwardMethod)
		throws Exception {
		this(type, forwardMethod, new Object[0], backwardMethod, new Object[0]);
	}
}
