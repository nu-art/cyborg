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
import com.nu.art.core.exceptions.runtime.ClassInstantiationRuntimeException;
import com.nu.art.core.exceptions.runtime.ImplementationMissingException;
import com.nu.art.core.exceptions.runtime.MUST_NeverHappenException;
import com.nu.art.core.exceptions.runtime.ThisShouldNotHappenException;
import com.nu.art.core.interfaces.Condition;
import com.nu.art.core.tools.ArrayTools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings( {
	                   "WeakerAccess",
	                   "unused"
                   })
public class ReflectiveTools {

	public static Object newInstance(Type type) {
		if (!(type instanceof Class))
			throw new ImplementationMissingException("Need to create an instance from Type");

		return newInstance((Class) type);
	}

	@SuppressWarnings("unchecked")
	public static <ItemType> ItemType newInstance(String classFQN)
		throws ClassNotFoundException {
		return (ItemType) newInstance(Class.forName(classFQN));
	}

	/**
	 * Creates a new instance of the supplied class type, via the default constructor.
	 *
	 * @param itemType   The instance type to instantiate.
	 * @param <ItemType> The class type to instantiate
	 *
	 * @return An instance of the supplied item type.
	 */
	public static <ItemType> ItemType newInstance(Class<ItemType> itemType) {
		try {
			Constructor<ItemType> constructor = itemType.getDeclaredConstructor();
			constructor.setAccessible(true);
			ItemType item = constructor.newInstance();
			constructor.setAccessible(false);
			return item;
		} catch (Exception e) {
			throw new ClassInstantiationRuntimeException(itemType, e);
		}
	}

	public static <ItemType> ItemType newInstance(Class<ItemType> itemType, Class<?> enclosingType, Object instance) {
		try {
			Constructor<ItemType> constructor = itemType.getDeclaredConstructor(enclosingType);
			constructor.setAccessible(true);
			ItemType item = constructor.newInstance(instance);
			constructor.setAccessible(false);
			return item;
		} catch (Exception e) {
			throw new ClassInstantiationRuntimeException(itemType, e);
		}
	}

	public static List<Class<?>> getAllInterfaces(Class<?> _class) {
		if (_class == null) {
			throw new BadImplementationException("");
		}

		List<Class<?>> interfacesFound = new ArrayList<>();
		getAllInterfaces(_class, interfacesFound);
		return interfacesFound;
	}

	private static void getAllInterfaces(Class<?> _class, List<Class<?>> interfacesFound) {
		while (_class != null) {
			Class<?>[] interfaces = _class.getInterfaces();

			for (Class<?> anInterface : interfaces) {
				if (!interfacesFound.contains(anInterface)) {
					interfacesFound.add(anInterface);
					getAllInterfaces(anInterface, interfacesFound);
				}
			}
			_class = _class.getSuperclass();
		}
	}

	public static Class<?>[] getInstancesTypes(Object[] instances) {
		Class<?> parameterTypes[] = new Class<?>[instances.length];
		int i = 0;
		for (Object parameter : instances) {
			parameterTypes[i++] = parameter.getClass();
		}
		return parameterTypes;
	}

	public static boolean isPrimitiveOrBoxed(Class<?> type) {
		return isPrimitive(type) || isBoxed(type);
	}

	/* Primitives */
	public static boolean isPrimitive(Class<?> type) {
		return isNumericPrimitive(type) || type == boolean.class;
	}

	public static boolean isNumericPrimitive(Class<?> type) {
		return isNaturalPrimitive(type) || isFloatingPrimitive(type);
	}

	public static boolean isNaturalPrimitive(Class<?> type) {
		return type == int.class || type == short.class || type == char.class || type == long.class || type == byte.class;
	}

	public static boolean isFloatingPrimitive(Class<?> type) {
		return type == float.class || type == double.class;
	}

	/* Boxed Primitives */
	public static boolean isBoxed(Class<?> type) {
		return isNumericBoxed(type) || type == Boolean.class || type == Character.class;
	}

	public static boolean isNumericBoxed(Class<?> type) {
		return isNaturalBoxed(type) || isFloatingBoxed(type);
	}

	public static boolean isNaturalBoxed(Class<?> type) {
		return type == Integer.class || type == Short.class || type == Long.class || type == Byte.class;
	}

	public static boolean isFloatingBoxed(Class<?> type) {
		return type == Float.class || type == Double.class;
	}

	public static String parseInstancesParameterType(Object[] parameters) {
		StringBuilder toRet = new StringBuilder();
		for (int i = 0; i < parameters.length; i++) {
			toRet.append(parameters[i].getClass().getSimpleName());
			if (i != parameters.length - 1) {
				toRet.append(", ");
			}
		}
		return toRet.toString();
	}

	public static String parseParametersType(Class<?>[] parameters) {
		return parseParametersType(parameters, false);
	}

	public static String getMethodAsString(Method method) {
		return getMethodAsString(method, false);
	}

	public static String getMethodAsString(Method method, boolean fullName) {
		String parameters = parseParametersType(method.getParameterTypes(), fullName);
		return method.getName() + "(" + parameters + ")";
	}

	public static String parseParametersType(Class<?>[] parameterTypes, boolean fullName) {
		StringBuilder toRet = new StringBuilder();
		for (int i = 0; i < parameterTypes.length; i++) {
			if (fullName) {
				toRet.append(parameterTypes[i].getName());
			} else {
				toRet.append(parameterTypes[i].getSimpleName());
			}
			if (i != parameterTypes.length - 1) {
				toRet.append(", ");
			}
		}
		return toRet.toString();
	}

	public static <EnumType extends Enum<?>> EnumType findMatchingEnumItem(Class<EnumType> type, Condition<EnumType> condition) {
		EnumType[] values = getEnumValues(type);
		for (EnumType value : values) {
			if (!condition.checkCondition(value))
				continue;

			return value;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <EnumType extends Enum<?>> EnumType[] getEnumValues(Class<EnumType> type) {
		Method enumGetValues;
		try {
			enumGetValues = type.getDeclaredMethod("values");
		} catch (Exception e) {
			throw new MUST_NeverHappenException("MUST NEVER HAPPENED: no values method for Enum!", e);
		}
		boolean wasAccessible = enumGetValues.isAccessible();
		if (!wasAccessible) {
			enumGetValues.setAccessible(true);
		}
		EnumType[] enumValues;
		try {
			enumValues = (EnumType[]) enumGetValues.invoke(null);
		} catch (Exception e) {
			throw new MUST_NeverHappenException("MUST NEVER HAPPENED: no values method invocation error!", e);
		} finally {
			if (!wasAccessible) {
				enumGetValues.setAccessible(false);
			}
		}
		return enumValues;
	}

	@SuppressWarnings("unchecked")
	public static <EnumType extends Enum<?>> EnumType[] getEnumFromValue(Class<EnumType> type, String... values) {
		EnumType[] enumValues = getEnumValues(type);
		EnumType[] toRet = (EnumType[]) ArrayTools.getGenericArrayType(type, values.length);

		for (int i = 0; i < values.length; i++) {
			String enumAsString = values[i];

			for (EnumType enumType : enumValues) {
				if (enumType.name().equalsIgnoreCase(enumAsString)) {
					toRet[i] = enumType;
				}
			}

			if (toRet[i] == null)
				throw new EnumConstantNotPresentException(type, enumAsString);
		}

		return toRet;
	}

	public static <EnumType extends Enum<?>> EnumType getEnumFromValue(Class<EnumType> type, String value) {
		EnumType[] enumValues = getEnumValues(type);
		for (EnumType enumType : enumValues) {
			if (enumType.name().equalsIgnoreCase(value)) {
				return enumType;
			}
		}
		throw new EnumConstantNotPresentException(type, value);
	}

	public static Class<?> getBoxedType(Class<?> type) {
		if (type == Integer.class || type == int.class) {
			return Integer.class;
		}
		if (type == Long.class || type == long.class) {
			return Long.class;
		}
		if (type == Float.class || type == float.class) {
			return Float.class;
		}
		if (type == Short.class || type == short.class) {
			return Short.class;
		}
		if (type == Boolean.class || type == boolean.class) {
			return Boolean.class;
		}
		if (type == Double.class || type == double.class) {
			return Double.class;
		}
		if (type == Byte.class || type == byte.class) {
			return Byte.class;
		}
		if (type == Character.class || type == char.class) {
			return Character.class;
		}
		return type;
	}

	public static Class<?> getunboxedtype(Class<?> type) {
		if (type == Integer.class || type == int.class) {
			return int.class;
		}
		if (type == Long.class || type == long.class) {
			return long.class;
		}
		if (type == Float.class || type == float.class) {
			return float.class;
		}
		if (type == Short.class || type == short.class) {
			return short.class;
		}
		if (type == Boolean.class || type == boolean.class) {
			return boolean.class;
		}
		if (type == Double.class || type == double.class) {
			return double.class;
		}
		if (type == Byte.class || type == byte.class) {
			return byte.class;
		}
		if (type == Character.class || type == char.class) {
			return char.class;
		}
		return type;
	}

	public static Object getDefaultValue(Class<?> type) {
		if (type == String.class) {
			return "";
		}
		if (type == Integer.class || type == int.class) {
			return 0;
		}
		if (type == Long.class || type == long.class) {
			return 0;
		}
		if (type == Float.class || type == float.class) {
			return 0;
		}
		if (type == Short.class || type == short.class) {
			return 0;
		}
		if (type == Boolean.class || type == boolean.class) {
			return false;
		}
		if (type == Double.class || type == double.class) {
			return 0;
		}
		if (type == Byte.class || type == byte.class) {
			return 0;
		}
		if (type == Character.class || type == char.class) {
			return 0;
		}
		return null;
	}

	public static Object getPrimitiveTypeInstance(Class<?> type, String value) {
		if (value.length() == 0) {
			return getDefaultValue(type);
		}

		if (type == String.class) {
			return value;
		}
		if (type == Integer.class || type == int.class) {
			return Integer.valueOf(value);
		}
		if (type == Long.class || type == long.class) {
			return Long.valueOf(value);
		}
		if (type == Float.class || type == float.class) {
			return Float.valueOf(value);
		}
		if (type == Short.class || type == short.class) {
			return Short.valueOf(value);
		}
		if (type == Boolean.class || type == boolean.class) {
			return Boolean.valueOf(value);
		}
		if (type == Double.class || type == double.class) {
			return Double.valueOf(value);
		}
		if (type == Byte.class || type == byte.class) {
			return Byte.valueOf(value);
		}
		throw new BadImplementationException("This method can only handle: int,long,float,short,boolean,double," + "byte. you have supplied: " + type.getName());
	}

	public static HashMap<Object, Object> getFieldsCrossMappings(Class<?> resourceClass) {
		return getFieldsCrossMappings(resourceClass, false);
	}

	public static HashMap<Object, Object> getFieldsCrossMappings(Class<?> resourceClass, boolean withParents) {
		HashMap<Object, Object> resourceMap = new HashMap<>();
		Class<?>[] interfaces = resourceClass.getInterfaces();
		if (withParents)
			for (Class<?> _interface : interfaces) {
				resourceMap.putAll(getFieldsCrossMappings(_interface, withParents));
			}
		Field[] fields = resourceClass.getDeclaredFields();
		for (Field field : fields) {
			try {
				boolean accessible = field.isAccessible();
				if (!accessible) {
					field.setAccessible(true);
				}

				Object value = field.get(null);
				String name = (withParents ? resourceClass.getSimpleName() + "-" : "") + field.getName();

				if (!accessible) {
					field.setAccessible(false);
				}

				resourceMap.put(value, name);
				resourceMap.put(name, value);
			} catch (Exception e) {
				throw new ThisShouldNotHappenException("This should not happend", e);
			}
		}
		return resourceMap;
	}

	public static Class<?> getPrimitiveClass(String type) {
		if (type.equals("long"))
			return long.class;

		if (type.equals("int"))
			return int.class;

		if (type.equals("short"))
			return short.class;

		if (type.equals("byte"))
			return byte.class;

		if (type.equals("float"))
			return float.class;

		if (type.equals("double"))
			return double.class;

		if (type.equals("boolean"))
			return boolean.class;

		return null;
	}
}
