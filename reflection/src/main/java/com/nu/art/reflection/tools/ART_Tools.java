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

import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.interfaces.Condition;
import com.nu.art.core.tools.ArrayTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Vector;

import static com.nu.art.core.interfaces.Condition.Condition_AlwaysTrue;

@SuppressWarnings( {
	                   "unused",
	                   "WeakerAccess"
                   })
public class ART_Tools {

	public static <Type> Field[] getFieldsWithAnnotationAndTypeFromClassHierarchy(Class<Type> child,
	                                                                              Class<? super Type> topParent,
	                                                                              Class<? extends Annotation> classAnnotationType,
	                                                                              Class<? extends Annotation> fieldAnnotationType,
	                                                                              Class<?>... fieldTypes) {
		Class<?>[] hierarchy = getClassHierarchy(child, topParent, classAnnotationType);
		return getAllFieldsWithAnnotationAndType(hierarchy, fieldTypes, fieldAnnotationType);
	}

	public static <Type> Class<?>[] getClassHierarchy(Class<Type> _class) {
		return getClassHierarchy(_class, Object.class);
	}

	@SuppressWarnings("unchecked")
	public static <Type> Class<?>[] getClassHierarchy(Class<Type> _class, Class<? super Type> superClass) {
		return getClassHierarchy(_class, superClass, (Condition<Class<?>>) Condition_AlwaysTrue);
	}

	public static <Type> Class<?>[] getClassHierarchy(Class<Type> _class, Class<? super Type> superClass, final Class<? extends Annotation> annotationType) {
		return getClassHierarchy(_class, superClass, new Condition<Class<?>>() {
			@Override
			public boolean checkCondition(Class<?> _class) {
				if (annotationType == null)
					return true;

				return _class.getAnnotation(annotationType) != null;
			}
		});
	}

	public static <Type> Class<?>[] getClassHierarchy(Class<Type> _class, Class<? super Type> superClass, Condition<Class<?>> filter) {
		if (!superClass.isAssignableFrom(_class))
			throw new BadImplementationException("The class type: '" + superClass.getName() + "' is not a superclass of: '" + _class.getName() + "'");

		ArrayList<Class<?>> typeHierarchy = new ArrayList<>();
		Class<?> __class = _class;

		while (true) {
			if (!filter.checkCondition(__class))
				continue;

			typeHierarchy.add(0, __class);

			__class = __class.getSuperclass();
			if (__class == superClass)
				break;
		}

		return ArrayTools.asArray(typeHierarchy, Class.class);
	}

	public static Field[] getAllFieldsFromClasses(Class<?> _class) {
		return getAllFieldsFromClasses(new Class[]{_class});
	}

	public static <T> Field[] getAllFieldsFromClasses(Class<?> _class, Class<?> superclass) {
		return getAllFieldsFromClasses(new Class[]{_class}, superclass);
	}

	@SuppressWarnings("unchecked")
	public static Field[] getAllFieldsInHierarchy(Class<?> _class) {
		return getAllFieldsInHierarchy(_class, (Condition<Field>) Condition_AlwaysTrue);
	}

	public static Field[] getAllFieldsInHierarchy(Class<?> _class, Condition<Field> filter) {
		Class<?>[] classHierarchy = getClassHierarchy(_class, Object.class);
		return getAllFieldsFromClasses(classHierarchy, filter);
	}

	@SuppressWarnings("unchecked")
	public static Field[] getAllFieldsFromClasses(Class<?>[] classes) {
		return getAllFieldsFromClasses(classes, (Condition<Field>) Condition_AlwaysTrue);
	}

	@SuppressWarnings("unchecked")
	public static Field[] getAllFieldsFromClasses(Class<?>[] classes, Class<?> superclass) {
		return getAllFieldsFromClasses(classes, (Condition<Field>) Condition_AlwaysTrue);
	}

	public static Field[] getAllFieldsFromClasses(Class<?>[] classes, Condition<Field> filter) {
		ArrayList<Field> fieldList = new ArrayList<>();
		for (Class<?> _class : classes) {
			Field[] fields = _class.getDeclaredFields();
			for (Field field : fields) {
				if (!filter.checkCondition(field))
					continue;

				fieldList.add(field);
			}
		}

		return ArrayTools.asArray(fieldList, Field.class);
	}

	public static Field[] getAllFieldsWithAnnotationAndType(Class<?> _class, Class<?>[] fieldTypes, Class<? extends Annotation> annotationType) {
		return getAllFieldsWithAnnotationAndType(new Class[]{_class}, fieldTypes, annotationType);
	}

	public static Field[] getAllFieldsWithAnnotationAndType(Class<?>[] classes, final Class<?>[] fieldTypes, final Class<? extends Annotation> annotationType) {
		return getAllFieldsFromClasses(classes, new Condition<Field>() {
			@Override
			public boolean checkCondition(Field field) {
				boolean match = false;

				for (Class<?> fieldType : fieldTypes) {
					if (fieldType.isAssignableFrom(field.getType())) {
						match = true;
						break;
					}
				}

				if (!match && fieldTypes.length > 0)
					return false;

				return annotationType == null || field.getAnnotation(annotationType) != null;
			}
		});
	}

	@SafeVarargs
	public static Field[] getAllFieldsWithAnnotation(Class<?> _class, Class<? extends Annotation>... annotationTypes) {
		Field[] fields = _class.getDeclaredFields();
		return getAllFieldsWithAnnotation(fields, annotationTypes);
	}

	public static Field[] getAllFieldsWithAnnotation(Field[] fields, Class<? extends Annotation>[] annotationTypes) {
		Vector<Field> fieldList = new Vector<>();
		for (Field field : fields) {
			for (Class<? extends Annotation> annotationType : annotationTypes) {
				if (field.getAnnotation(annotationType) == null)
					continue;

				fieldList.add(field);
				break;
			}
		}

		return ArrayTools.asArray(fieldList, Field.class);
	}

	public static Field getFirstFieldsWithAnnotation(Field[] fields, Class<? extends Annotation> annotationType) {
		for (Field field : fields) {
			if (field.getAnnotation(annotationType) != null)
				return field;
		}
		return null;
	}

	public static Method[] getAllMethodsWithAnnotation(Class<?> _class, Class<? extends Annotation> annotationType) {
		Method[] methods = _class.getDeclaredMethods();
		Vector<Method> methodList = new Vector<>();
		for (Method method : methods) {
			if (method.getAnnotation(annotationType) != null)
				methodList.add(method);
		}

		return ArrayTools.asArray(methodList, Method.class);
	}
}
