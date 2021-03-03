package com.nu.art.core.replacer;

import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.exceptions.runtime.ImplementationMissingException;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.utils.RegexAnalyzer;
import com.nu.art.core.utils.SynchronizedObject;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Replacer {

	public static final Replacer Replacer = new Replacer();

	private SynchronizedObject<HashSet<String>> syncSet = new SynchronizedObject<>(new Getter<HashSet<String>>() {
		@Override
		public HashSet<String> get() {
			return new HashSet<>();
		}
	});

	private RegexAnalyzer regexAnalyzer = new RegexAnalyzer("\\$\\{(.*?)\\}");

	private Replacer() {}

	@SuppressWarnings("RegExpRedundantEscape")
	public String replace(String original, Object repo) {
		if (repo == null)
			repo = new HashMap<>();

		String[] parameters = regexAnalyzer.instances(original);
		if (parameters.length == 0)
			return original;

		HashSet<String> collection = syncSet.get();
		collection.clear();
		Collections.addAll(collection, parameters);

		for (String fqParameter : parameters) {
			String paramName = regexAnalyzer.findRegex(0, 1, fqParameter);
			if (paramName == null)
				throw new BadImplementationException("Could not extract parameter name from: " + fqParameter);

			String[] parameter = paramName.split("\\.");
			Object paramValue = extractParamFromRepo(parameter, 0, repo);
			if (paramValue == null)
				throw new ImplementationMissingException("No value found for param '" + paramName + "'");

			original = original.replaceAll("\\$\\{" + paramName + "\\}", paramValue.toString());
		}

		return original;
	}

	private Object extractParamFromRepo(String[] parameter, int index, Object object) {
		if (parameter.length <= index)
			return object;

		String part = parameter[index];
		Object value = resolveValue(object, part);

		return extractParamFromRepo(parameter, index + 1, value);
	}

	@SuppressWarnings("unchecked")
	private Object resolveValue(Object object, String part) {
		Object value;
		if (object instanceof Map)
			value = getValueFromMap((Map<String, Object>) object, part);
		else
			value = getValueFromObject(object, part);
		return value;
	}

	private Object getValueFromObject(Object object, String part) {
		Object value;
		Field field;
		try {
			field = object.getClass().getDeclaredField(part);
		} catch (NoSuchFieldException e) {
			throw new BadImplementationException("No such field '" + part + "' in object of type '" + object.getClass() + "'", e);
		}

		try {
			boolean accessible = field.isAccessible();
			if (!accessible)
				field.setAccessible(true);

			value = field.get(object);

			if (!accessible)
				field.setAccessible(false);
		} catch (IllegalAccessException e) {
			throw new BadImplementationException("Error extracting value from field '" + part + "' in object of type '" + object.getClass() + "'", e);
		}
		return value;
	}

	private Object getValueFromMap(Map<String, Object> map, String part) {
		return map.get(part);
	}
}
