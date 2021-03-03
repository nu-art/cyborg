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

package com.nu.art.reflection.extractor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * The injectee type that this interface supports.
 *
 * @author TacB0sS
 */
public abstract class AnnotatbleExtractor<KeyType extends Annotation, BaseType, InjecteeBaseType>
	extends Extractor<BaseType, InjecteeBaseType> {

	protected final Class<KeyType> annotationType;

	public AnnotatbleExtractor(Class<KeyType> annotationType) {
		super();
		this.annotationType = annotationType;
	}

	// protected abstract <RealType extends BaseType> RealType getInstance(Class<RealType> realType, KeyType key);
	//
	// protected abstract <RealType extends BaseType> RealType[] getArray(Class<RealType> realType, KeyType key);
	@Override
	protected void setValueForField(Field field, Object fieldValue) {
		setValueFromAnnotationAndField(field.getAnnotation(annotationType), field, fieldValue);
	}

	protected abstract void setValueFromAnnotationAndField(KeyType annotation, Field field, Object fieldValue);
}
