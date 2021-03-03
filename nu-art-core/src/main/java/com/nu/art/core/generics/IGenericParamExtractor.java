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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public interface IGenericParamExtractor {

	Type[] getTypes(Type genericSuperclass)
		throws IllegalAccessException;

	Type getRawType(Type genericInterface)
		throws IllegalAccessException, ClassNotFoundException;

	<K> Class<K> convertToClass(Type type)
		throws InvocationTargetException, IllegalAccessException;
}
