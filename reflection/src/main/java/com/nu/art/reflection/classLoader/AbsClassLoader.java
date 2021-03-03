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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class AbsClassLoader<HolderType>
	extends ClassLoader {

	protected static boolean debug = false;

	protected HolderType resourceHandler;

	protected String resourceUrl;

	private ClassesLoader manager;

	@Override
	protected URL findResource(String resourceRelativePath) {
		return manager.findResource(resourceRelativePath);
	}

	protected abstract InputStream getResourceAsStream(HolderType holder, String resourceName);

	protected abstract void releaseHolder(HolderType holder);

	final void setManager(ClassesLoader manager) {
		this.manager = manager;
	}

	@Override
	public final InputStream getResourceAsStream(String name) {
		InputStream is = getResourceAsStream(resourceHandler, name);
		releaseHolder(resourceHandler);
		return is;
	}

	@Override
	public final Class<?> loadClass(String className)
		throws ClassNotFoundException {
		return manager.loadClass(className);
	}

	public void setResourceHandler(String resourceUrl, HolderType resourceHandler) {
		this.resourceUrl = resourceUrl;
		this.resourceHandler = resourceHandler;
	}

	@Override
	public String toString() {
		return resourceUrl;
	}

	Class<?> loadClassFromThisLoader(String className) {
		if (AbsClassLoader.debug) {
			System.out.println("Loading class '" + className + "', from: " + resourceHandler);
		}

		Class<?> _class = findLoadedClass(className);

		if (_class != null) {
			return _class;
		}

		InputStream is = getResourceAsStream(resourceHandler, className.replace(".", "/") + ".class");
		if (is == null) {
			return null;
		}

		try {
			_class = loadClassFromStream(is);
			is.close();
			releaseHolder(resourceHandler);
			return _class;
		} catch (IOException e) {
			return null;
		}
	}

	protected Class<?> loadClassFromStream(InputStream inputStream)
		throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(inputStream.available());
		BufferedOutputStream out = new BufferedOutputStream(bos, 1024);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			out.write(buffer, 0, length);
		}
		out.flush();
		inputStream.close();
		return defineClass(null, bos.toByteArray(), 0, bos.size());
	}

	public String getResourceUrl() {
		return resourceUrl;
	}
}
