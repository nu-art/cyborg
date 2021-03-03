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

package com.nu.art.reflection.classLoader;

import com.nu.art.reflection.exceptions.ClassLoaderCyclicException;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

public final class ClassesLoader
	extends ClassLoader {

	protected static boolean debug = false;

	private HashMap<Object, AbsClassLoader<?>> classLoaders = new HashMap<>();

	private String label;

	public void addClassLoader(Object key, AbsClassLoader<?> classLoader) {
		AbsClassLoader<?> _classLoader = classLoaders.get(classLoader.resourceUrl);
		if (_classLoader != null) {
			throw new ClassLoaderCyclicException("Cyclic dependency was found between: " + _classLoader + " and " + classLoader);
		}
		classLoader.setManager(this);
		classLoaders.put(key, classLoader);
	}

	public boolean containsClassLoader(Object key) {
		return getClassLoader(key) != null;
	}

	@Override
	protected URL findResource(String resourceRelativePath) {
		Collection<AbsClassLoader<?>> loaders = classLoaders.values();
		for (AbsClassLoader<?> classLoader : loaders) {
			try {
				return new URL("file://" + classLoader.getResourceUrl() + "/" + resourceRelativePath);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public AbsClassLoader<?> getClassLoader(Object key) {
		return classLoaders.get(key);
	}

	@Override
	public final InputStream getResourceAsStream(String name) {
		InputStream is = null;
		for (AbsClassLoader<?> classLoader : classLoaders.values()) {
			is = classLoader.getResourceAsStream(name);
			if (is != null) {
				return is;
			}
		}
		return is;
	}

	@Override
	public final Class<?> loadClass(String className)
		throws ClassNotFoundException {
		Class<?> _class;
		try {
			_class = getParent().loadClass(className);
			return _class;
		} catch (ClassNotFoundException ignored) {}
		try {
			_class = getClass().getClassLoader().loadClass(className);
			return _class;
		} catch (ClassNotFoundException ignored) {}
		try {
			_class = ClassLoader.getSystemClassLoader().loadClass(className);
			return _class;
		} catch (ClassNotFoundException ignored) {}

		Collection<AbsClassLoader<?>> loaders = classLoaders.values();
		for (AbsClassLoader<?> classLoader : loaders) {
			_class = classLoader.loadClassFromThisLoader(className);
			if (_class != null) {
				return _class;
			}
		}
		throw new ClassNotFoundException("Class: " + className + ", was not found in ClassesLoader: " + label);
	}

	public final String getLabel() {
		return label;
	}

	public final void setLabel(String label) {
		this.label = label;
	}

	public boolean contains(Object key) {
		ClassLoader loader = classLoaders.get(key);
		return loader != null;
	}

	public ClassLoader getHandler(Object key) {
		return classLoaders.get(key);
	}
}
