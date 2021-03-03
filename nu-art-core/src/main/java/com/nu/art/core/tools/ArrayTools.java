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

package com.nu.art.core.tools;

import com.nu.art.core.generics.Function;
import com.nu.art.core.interfaces.Condition;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings( {
	                   "unused",
	                   "unchecked"
                   })
public class ArrayTools {

	private static final Condition DefaultFilter = new Condition() {
		@Override
		public boolean checkCondition(Object item) {
			return true;
		}
	};

	public static <T> String join(String delimiter, T... elements) {
		if (elements == null)
			return "null";

		return join(new Function<Object, String>() {
			@Override
			public String map(Object s) {
				return s.toString();
			}
		}, delimiter, elements);
	}

	@SafeVarargs
	public static <T> String join(Function<T, String> toString, String delimiter, T... elements) {
		StringBuilder toRet = new StringBuilder();
		for (T item : elements) {
			toRet.append(toString.map(item));
			if (item != elements[elements.length - 1])
				toRet.append(delimiter);
		}
		return toRet.toString();
	}

	public static <From, To> To[] map(Class<To> toTypeArray, Function<From, To> mapper, From... source) {
		return map(toTypeArray, mapper, Arrays.asList(source));
	}

	public static <From, To> To[] map(Class<To> toTypeArray, Function<From, To> mapper, List<From> source) {
		int arrayLength = 0;
		if (source != null)
			arrayLength = source.size();

		To[] target = newInstance(toTypeArray, arrayLength);
		for (int i = 0; i < arrayLength; i++) {
			target[i] = mapper.map(source.get(i));
		}
		return target;
	}

	/**
	 * @param arr      original Array
	 * @param elements element to add
	 * @param <T>      The base type of the array
	 *
	 * @return A new array composed of the original and the new element
	 */
	public static <T> T[] appendElements(T[] arr, T... elements) {
		T[] toRet = Arrays.copyOf(arr, arr.length + elements.length);
		for (int i = 0; i < elements.length; i++) {
			toRet[arr.length + i] = elements[i];
		}
		return toRet;
	}

	public static <T> T[] appendElement(T[] arr, T element) {
		T[] toRet = Arrays.copyOf(arr, arr.length + 1);
		toRet[arr.length] = element;
		return toRet;
	}

	public static <T> T[] insertElement(T[] arr, T element, int index) {
		T[] toRet = Arrays.copyOf(arr, arr.length + 1);
		toRet[index] = element;
		System.arraycopy(arr, index + 1 - 1, toRet, index + 1, toRet.length - (index + 1));
		return toRet;
	}

	public static <T> T[] removeElement(T[] arr, T toRemoves) {
		return removeElements(arr, toRemoves);
	}

	public static <T> T[] removeElements(T[] arr, T... toRemoves) {
		return removeElements(arr, Arrays.asList(toRemoves));
	}

	public static <T> T[] removeElements(T[] arr, List<T> toRemoves) {
		ArrayList<T> temp = new ArrayList<>();
		temp.addAll(Arrays.asList(arr));
		temp.removeAll(toRemoves);
		return asArray(temp, (Class<T>) arr.getClass().getComponentType());
	}

	public static <T> T[] removeElement(T[] arr, int del) {
		return removeElement(arr, arr[del]);
	}

	public static <T> int indexOf(T[] arr, T element) {
		return Arrays.asList(arr).indexOf(element);
	}

	public static <T> T lastElement(T[] arr) {
		return arr[arr.length - 1];
	}

	public static <T> T firstElement(T[] arr) {
		return arr[0];
	}

	public static <T> boolean contains(T[] arr, T item) {
		return Arrays.asList(arr).contains(item);
	}

	public static <Type> Type[] asFilteredArray(Collection<?> list, Class<Type> arrayType) {
		return asFilteredArray(list, arrayType, (Condition<Type>) DefaultFilter);
	}

	@SuppressWarnings( {"SynchronizationOnLocalVariableOrMethodParameter"})
	public static <Type> Type[] asFilteredArray(Collection<?> list, Class<Type> arrayType, Condition<Type> filter) {
		ArrayList<Type> temp = new ArrayList<>();
		synchronized (list) {
			for (Object item : list) {
				if (arrayType.isAssignableFrom(item.getClass()) && filter.checkCondition((Type) item))
					temp.add((Type) item);
			}
		}
		return temp.toArray(newInstance(arrayType, temp.size()));
	}

	@SuppressWarnings( {"SynchronizationOnLocalVariableOrMethodParameter"})
	public static <SuperType, Type> SuperType[] asFilteredArray(Type[] arrayToFilter, Class<SuperType> arrayType) {
		ArrayList<SuperType> temp = new ArrayList<>();
		synchronized (arrayToFilter) {
			for (Type item : arrayToFilter) {
				if (arrayType.isAssignableFrom(item.getClass()))
					temp.add((SuperType) item);
			}
		}
		return temp.toArray(newInstance(arrayType, temp.size()));
	}

	public static <SuperType, ArrayType extends SuperType> SuperType[] asArray(Collection<ArrayType> collection, Class<SuperType> arrayType) {
		return collection.toArray(newInstance(arrayType, 0));
	}

	//	public static <ArrayType> ArrayType[] asArray(Set<ArrayType> set, Class<ArrayType> arrayType) {
	//		return set.toArray(newInstance(arrayType, set.size()));
	//	}
	//
	//	public static <ArrayType> ArrayType[] asArray(List<ArrayType> list, Class<ArrayType> arrayType) {
	//		return list.toArray(newInstance(arrayType, list.size()));
	//	}

	public static <ArrayType> ArrayType[] asArrayAnonymous(Iterator<?> keys, Class<ArrayType> arrayType) {
		return asArray(((Iterator<ArrayType>) keys), arrayType);
	}

	public static <ArrayType> ArrayType[] asArray(Iterator<ArrayType> keys, Class<ArrayType> arrayType) {
		ArrayList<ArrayType> items = new ArrayList<>();
		while (keys.hasNext()) {
			items.add(keys.next());
		}
		return asArray(items, arrayType);
	}

	public static <ArrayType> ArrayType[] asArray(Enumeration<ArrayType> enumeration, Class<ArrayType> arrayType) {
		return asArray(Collections.list(enumeration), arrayType);
	}

	public static <ArrayType> ArrayType[] newInstance(Class<ArrayType> arrayType, int size) {
		return (ArrayType[]) Array.newInstance(arrayType, size);
	}

	public static Object getGenericArrayType(Class<?> arrayType, int arrayDimensions) {
		int[] dimensions = new int[arrayDimensions];
		return getGenericArrayType(arrayType, dimensions);
	}

	public static Object getGenericArrayType(Class<?> arrayType, int... length) {
		return Array.newInstance(arrayType, length).getClass();
	}

	public static Class<?> getArrayType(Class<?> type) {
		while (type.isArray()) {
			type = type.getComponentType();
		}
		return type;
	}

	/**
	 * Returns a boxed array for the supplied array type. <br>
	 * <br>
	 * int[] ==&gt; Integer[]<Br>
	 * boolean[] ==&gt; Boolean[]<br>
	 * etc...
	 * <br>
	 *
	 * @param <ItemType> The return Boxed type
	 * @param array      An instance of a unboxed type array
	 *
	 * @return An instance of a boxed array, of the supplied array type.
	 */
	public static <ItemType> ItemType[] getBoxedArray(Object array) {
		if (array instanceof long[]) {
			long[] originalArray = (long[]) array;
			Long[] newArray = new Long[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return (ItemType[]) newArray;
		}

		if (array instanceof int[]) {
			int[] originalArray = (int[]) array;
			Integer[] newArray = new Integer[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return (ItemType[]) newArray;
		}

		if (array instanceof short[]) {
			short[] originalArray = (short[]) array;
			Short[] newArray = new Short[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return (ItemType[]) newArray;
		}

		if (array instanceof char[]) {
			char[] originalArray = (char[]) array;
			Character[] newArray = new Character[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return (ItemType[]) newArray;
		}

		if (array instanceof byte[]) {
			byte[] originalArray = (byte[]) array;
			Byte[] newArray = new Byte[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return (ItemType[]) newArray;
		}

		if (array instanceof double[]) {
			double[] originalArray = (double[]) array;
			Double[] newArray = new Double[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return (ItemType[]) newArray;
		}

		if (array instanceof float[]) {
			float[] originalArray = (float[]) array;
			Float[] newArray = new Float[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return (ItemType[]) newArray;
		}

		if (array instanceof boolean[]) {
			boolean[] originalArray = (boolean[]) array;
			Boolean[] newArray = new Boolean[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return (ItemType[]) newArray;
		}

		return (ItemType[]) array;
	}

	/**
	 * Given a specific primitive type, This method would return a <b>Boxed</b> empty array with the specified length. <br>
	 * <br>
	 * (int.class, x) ==&gt; Integer[]{ - x length- }<Br>
	 * (boolean.class, y) ==&gt; Boolean[]{ - y length- }<br>
	 * etc...<br>
	 *
	 * @param primitiveType The primitive type requested.
	 * @param length        The length of the array.
	 *
	 * @return An empty array of a primitive Boxed type.
	 */
	public static Object getEmptyBoxedArray(Class<?> primitiveType, int length) {
		if (primitiveType == long[].class) {
			return new Long[length];
		}
		if (primitiveType == int[].class) {
			return new Integer[length];
		}
		if (primitiveType == short[].class) {
			return new Short[length];
		}
		if (primitiveType == char[].class) {
			return new Character[length];
		}
		if (primitiveType == byte[].class) {
			return new Byte[length];
		}
		if (primitiveType == double[].class) {
			return new Double[length];
		}
		if (primitiveType == float[].class) {
			return new Float[length];
		}
		if (primitiveType == boolean[].class) {
			return new Boolean[length];
		}
		return Array.newInstance(primitiveType, length);
	}

	/**
	 * Given a specific array type, This method would return an empty array of the specified type with the specified length. <br>
	 * <br>
	 * (int[].class, x) ==&gt; int[]{ - x length- }<Br>
	 * (boolean[].class, y) ==&gt; boolean[]{ - y length- }<br>
	 * (Long[].class, z) ==&gt; Long[]{ - z length- }<br>
	 * etc...<br>
	 *
	 * @param arrayType The array type requested.
	 * @param length    The length of the array.
	 *
	 * @return An empty array of a primitive Boxed type.
	 */
	public static Object getArrayInstance(Class<?> arrayType, int length) {
		if (!arrayType.isArray()) {
			throw new IllegalArgumentException("Value supplied MUST be of an Array type: " + arrayType);
		}
		if (arrayType == long[].class) {
			return new long[length];
		}
		if (arrayType == int[].class) {
			return new int[length];
		}
		if (arrayType == short[].class) {
			return new short[length];
		}
		if (arrayType == char[].class) {
			return new char[length];
		}
		if (arrayType == byte[].class) {
			return new byte[length];
		}
		if (arrayType == double[].class) {
			return new double[length];
		}
		if (arrayType == float[].class) {
			return new float[length];
		}
		if (arrayType == boolean[].class) {
			return new boolean[length];
		}
		if (arrayType == Long[].class) {
			return new Long[length];
		}
		if (arrayType == Integer[].class) {
			return new Integer[length];
		}
		if (arrayType == Short[].class) {
			return new Short[length];
		}
		if (arrayType == Character[].class) {
			return new Character[length];
		}
		if (arrayType == Byte[].class) {
			return new Byte[length];
		}
		if (arrayType == Double[].class) {
			return new Double[length];
		}
		if (arrayType == Float[].class) {
			return new Float[length];
		}
		if (arrayType == Boolean[].class) {
			return new Boolean[length];
		}
		return Array.newInstance(arrayType.getComponentType(), length);
	}

	/**
	 * Given a primitive <b>Boxed</b> array, this method would return an array of primitives. <br>
	 * Integer[] ==&gt; int[] <Br>
	 * Boolean[] ==&gt; boolean[] <Br>
	 * etc...<br>
	 *
	 * @param array The Boxed array.
	 *
	 * @return A primitive array matching the Boxed type.
	 */
	public static Object getUnboxedArray(Object array) {
		if (array instanceof Long[]) {
			Long[] originalArray = (Long[]) array;
			long[] newArray = new long[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return newArray;
		}

		if (array instanceof Integer[]) {
			Integer[] originalArray = (Integer[]) array;
			int[] newArray = new int[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return newArray;
		}

		if (array instanceof Short[]) {
			Short[] originalArray = (Short[]) array;
			short[] newArray = new short[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return newArray;
		}

		if (array instanceof Character[]) {
			Character[] originalArray = (Character[]) array;
			char[] newArray = new char[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return newArray;
		}

		if (array instanceof Byte[]) {
			Byte[] originalArray = (Byte[]) array;
			byte[] newArray = new byte[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return newArray;
		}
		if (array instanceof Double[]) {
			Double[] originalArray = (Double[]) array;
			double[] newArray = new double[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return newArray;
		}
		if (array instanceof Float[]) {
			Float[] originalArray = (Float[]) array;
			float[] newArray = new float[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return newArray;
		}
		if (array instanceof Boolean[]) {
			Boolean[] originalArray = (Boolean[]) array;
			boolean[] newArray = new boolean[originalArray.length];
			for (int i = 0; i < originalArray.length; i++) {
				newArray[i] = originalArray[i];
			}
			return newArray;
		}
		return array;
	}

	public static String printArray(String indentation, int warpLineEvery, int[] indices) {
		String[] array = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			array[i] = "" + indices[i];
		}
		return ArrayTools.printArray(indentation, warpLineEvery, array);
	}

	public static String printArray(String indentation, int warpLineEvery, long[] indices) {
		String[] array = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			array[i] = "" + indices[i];
		}
		return ArrayTools.printArray(indentation, warpLineEvery, array);
	}

	public static String printArray(String indentation, int warpLineEvery, byte[] indices) {
		String[] array = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			array[i] = "" + indices[i];
		}
		return ArrayTools.printArray(indentation, warpLineEvery, array);
	}

	public static String printArray(String indentation, int warpLineEvery, short[] indices) {
		String[] array = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			array[i] = "" + indices[i];
		}
		return ArrayTools.printArray(indentation, warpLineEvery, array);
	}

	public static String printArray(String indentation, int warpLineEvery, float[] indices) {
		String[] array = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			array[i] = "" + indices[i];
		}
		return ArrayTools.printArray(indentation, warpLineEvery, array);
	}

	public static String printArray(String indentation, int warpLineEvery, double[] indices) {
		String[] array = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			array[i] = "" + indices[i];
		}
		return ArrayTools.printArray(indentation, warpLineEvery, array);
	}

	public static String printArray(String indentation, int warpLineEvery, String[] indices) {
		String toRet = indentation + "[";
		int newLineIndex = 0;
		for (int i = 0; i < indices.length; i++) {
			toRet += indices[i];
			if (warpLineEvery != -1) {
				newLineIndex++;
			}
			if (i < indices.length - 1) {
				if (newLineIndex == warpLineEvery) {
					toRet += "\n" + indentation + " ";
					newLineIndex = 0;
				} else {
					toRet += ", ";
				}
			}
		}
		toRet += "]";
		return toRet;
	}

	public static <ItemType> String printGenericArray(String indentation, int warpLineEvery, ItemType[] indices) {
		String[] array = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			array[i] = "" + indices[i];
		}
		return ArrayTools.printArray(indentation, warpLineEvery, array);
	}

	public static int[] reverseArray(int[] originalArray) {
		int[] toRet = new int[originalArray.length];
		int j = 0;
		for (int i = originalArray.length - 1; i >= 0; i--) {
			toRet[j++] = originalArray[i];
		}
		return toRet;
	}

	public static short[] reverseArray(short[] originalArray) {
		short[] toRet = new short[originalArray.length];
		int j = 0;
		for (int i = originalArray.length - 1; i >= 0; i--) {
			toRet[j++] = originalArray[i];
		}
		return toRet;
	}

	public static long[] reverseArray(long[] originalArray) {
		long[] toRet = new long[originalArray.length];
		int j = 0;
		for (int i = originalArray.length - 1; i >= 0; i--) {
			toRet[j++] = originalArray[i];
		}
		return toRet;
	}

	public static boolean[] reverseArray(boolean[] originalArray) {
		boolean[] toRet = new boolean[originalArray.length];
		int j = 0;
		for (int i = originalArray.length - 1; i >= 0; i--) {
			toRet[j++] = originalArray[i];
		}
		return toRet;
	}

	public static char[] reverseArray(char[] originalArray) {
		char[] toRet = new char[originalArray.length];
		int j = 0;
		for (int i = originalArray.length - 1; i >= 0; i--) {
			toRet[j++] = originalArray[i];
		}
		return toRet;
	}

	public static double[] reverseArray(double[] originalArray) {
		double[] toRet = new double[originalArray.length];
		int j = 0;
		for (int i = originalArray.length - 1; i >= 0; i--) {
			toRet[j++] = originalArray[i];
		}
		return toRet;
	}

	public static float[] reverseArray(float[] originalArray) {
		float[] toRet = new float[originalArray.length];
		int j = 0;
		for (int i = originalArray.length - 1; i >= 0; i--) {
			toRet[j++] = originalArray[i];
		}
		return toRet;
	}

	public static byte[] reverseArray(byte[] originalArray) {
		byte[] toRet = new byte[originalArray.length];
		int j = 0;
		for (int i = originalArray.length - 1; i >= 0; i--) {
			toRet[j++] = originalArray[i];
		}
		return toRet;
	}

	public static <ItemType> ItemType[] reverseGenericArray(ItemType[] originalArray) {
		return reverseGenericArray(originalArray, newInstance((Class<ItemType>) originalArray.getClass().getComponentType(), originalArray.length));
	}

	public static <ItemType> ItemType[] reverseGenericArray(ItemType[] originalArray, ItemType[] reversed) {
		int j = 0;
		for (int i = originalArray.length - 1; i >= 0; i--) {
			reversed[j++] = originalArray[i];
		}
		return reversed;
	}

	public static <T> T[] asFilteredArray(Class<T> type, Iterable<? super T> instancesToProcess, Condition<T> condition) {
		ArrayList<T> toRet = new ArrayList<>();
		for (Object obj : instancesToProcess) {
			if (!type.isAssignableFrom(obj.getClass()))
				continue;

			T instance = (T) obj;
			if (!condition.checkCondition(instance))
				continue;

			toRet.add(instance);
		}
		return asArray(toRet, type);
	}

	public static <T> List<T> asList(T[] array) {
		if (array == null)
			return Collections.emptyList();

		return Arrays.asList(array);
	}
}
