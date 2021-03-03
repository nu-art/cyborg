package com.nu.art.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nu.art.belog.BeLogged;
import com.nu.art.belog.loggers.JavaLogger;
import com.nu.art.core.exceptions.runtime.ThisShouldNotHappenException;
import com.nu.art.core.interfaces.Serializer;
import com.nu.art.core.tools.FileTools;
import com.nu.art.modular.core.ModuleManager;
import com.nu.art.modular.core.ModuleManagerBuilder;
import com.nu.art.modular.core.ModulesPack;
import com.nu.art.modular.tests.ModuleManager_TestClass;

import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import static com.nu.art.modular.tests.ModuleManager_TestClass.getModule;
import static com.nu.art.storage.Test_Utils.sleepFor;

public class Test_Setup {

	public static final JsonSerializer _Serializer = new JsonSerializer();

	public static class JsonSerializer
		extends Serializer<Object, String> {

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

	private static final File storageFolder = new File("build/test/storage");

	static void cleanUp() {
		try {
			FileTools.delete(storageFolder);
			getModule(PreferencesModule.class).clear();
			sleepFor(300);
		} catch (IOException e) {
			throw new ThisShouldNotHappenException("Could not delete storage folder: " + storageFolder.getAbsolutePath(), e);
		}
	}

	static class Pack
		extends ModulesPack {

		Pack() {
			super(PreferencesModule.class);
		}

		@Override
		protected void init() {
			PreferencesModule preferencesModule = getModule(PreferencesModule.class);
			preferencesModule.setStorageFolder(storageFolder);
			preferencesModule.DebugFlag.enable();
		}
	}

	public static class PrefModel<T> {

		PreferenceKey<?, T> pref;
		T defaultValue;
		T value;

		public PrefModel(PreferenceKey<? extends PreferenceKey<?, T>, T> pref, String key, T defaultValue, T value) {
			this.pref = pref;
			this.defaultValue = defaultValue;
			this.value = value;

			pref.setKey(key, defaultValue).setDefaultValue(defaultValue);
		}
	}

	public static class Model {

		Model(String value) {
			this.value = value;
		}

		Model() {
		}

		String value;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;

			if (o == null || getClass() != o.getClass())
				return false;

			Model model = (Model) o;

			return value != null ? value.equals(model.value) : model.value == null;
		}

		@Override
		public int hashCode() {
			return value != null ? value.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "Model{" + "value='" + value + '\'' + '}';
		}
	}

	public enum TestEnum {
		DefaultValue,
		Value1,
		Value2,
		Value3
	}

	public static PrefModel<Integer> getIntegerModel() {
		return new PrefModel<>(new IntegerPreference(), "pref-integer", 42, 21);
	}

	public static PrefModel<Long> getLongModel() {
		return new PrefModel<>(new LongPreference(), "pref-long", 42L, 21L);
	}

	public static PrefModel<Float> getFloatModel() {
		return new PrefModel<>(new FloatPreference(), "pref-float", 42f, 21f);
	}

	public static PrefModel<Double> getDoubleModel() {
		return new PrefModel<>(new DoublePreference(), "pref-double", 42.0, 21.0);
	}

	public static PrefModel<String> getStringModel() {
		return new PrefModel<>(new StringPreference(), "pref-string", "PAH", "ashpa");
	}

	public static PrefModel<Model> getCustomModel() {
		return new PrefModel<>(new CustomPreference<Model>().setItemType(Model.class, _Serializer), "pref-custom", new Model("EMPTY"), new Model("ZEVEL"));
	}

	public static PrefModel<HashMap> getCustomModelStateful() {
		return new PrefModel<>(new CustomPreference<HashMap>().setItemType(HashMap.class, _Serializer), "pref-custom", new HashMap(), new HashMap());
	}

	public static PrefModel<TestEnum> getEnumModel() {
		return new PrefModel<>(new EnumPreference<TestEnum>().setEnumType(TestEnum.class), "pref-enum", TestEnum.DefaultValue, TestEnum.Value2);
	}
}
