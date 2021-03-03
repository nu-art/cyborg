package com.nu.art.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nu.art.core.interfaces.Serializer;

import java.lang.reflect.Type;

public class JsonSerializer
	extends Serializer<Object, String> {

	public static final JsonSerializer Serializer = new JsonSerializer();

	public static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	@Override
	public String serialize(Object o) {
		return gson.toJson(o);
	}

	@Override
	public Object deserialize(String from, Type toType) {
		return gson.fromJson(from, toType);
	}
}
