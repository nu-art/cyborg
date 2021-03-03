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

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClassLoader
	extends AbsClassLoader<JarFile> {

	@Override
	protected InputStream getResourceAsStream(JarFile oldJarFile, String resourceName) {
		JarFile jarFile;
		try {
			jarFile = new JarFile(oldJarFile.getName());
			JarEntry entry = jarFile.getJarEntry(resourceName);
			if (entry == null) {
				return null;
			}
			return jarFile.getInputStream(entry);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void releaseHolder(JarFile jarFile) {
		try {
			jarFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
