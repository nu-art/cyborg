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

package com.nu.art.reflection.injector;

import com.nu.art.core.exceptions.runtime.BadImplementationException;

import java.lang.reflect.Field;

/**
 * The injectee type that this interface supports.
 *
 * @author TacB0sS
 */
public abstract class Injector<BaseType, InjecteeBaseType> {

	protected final void injectInstanceToField(Object instance, Field field, Object fieldValue) {
		try {
			boolean accessible = field.isAccessible();
			if (!accessible)
				field.setAccessible(true);

			field.set(instance, fieldValue);

			if (!accessible)
				field.setAccessible(false);
		} catch (Exception e) {
			throw new BadImplementationException("Expected value for field: '" + field + "', actual value type: '" + fieldValue.getClass().getName() + "'", e);
		}
	}

	protected final Object extractValueFromField(Object instance, Field field) {
		try {
			boolean accessible = field.isAccessible();
			if (!accessible)
				field.setAccessible(true);

			Object fieldValue = field.get(instance);

			if (!accessible)
				field.setAccessible(false);

			return fieldValue;
		} catch (Exception e) {
			throw new BadImplementationException("Error extracting value from field: " + field, e);
		}
	}

	protected abstract Field[] extractFieldsFromInstance(Class<? extends InjecteeBaseType> injecteeType);

	@SuppressWarnings("unchecked")
	public final <InjecteeType extends InjecteeBaseType> void injectToInstance(InjecteeType instance) {
		Field[] validFieldsForInjection = extractFieldsFromInstance((Class<? extends InjecteeBaseType>) instance.getClass());
		for (Field field : validFieldsForInjection) {
			Object fieldValue = getValueForField(instance, field);
			injectInstanceToField(instance, field, fieldValue);
		}
	}

	/**
	 * @param field The field for which to get the value for.
	 *
	 * @return a value for the field
	 */
	protected abstract Object getValueForField(Object instance, Field field);
}
