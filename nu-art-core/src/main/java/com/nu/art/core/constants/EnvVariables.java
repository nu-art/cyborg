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

package com.nu.art.core.constants;

public enum EnvVariables {
	Version("java.version"),
	Vendor("java.vendor"),
	JavaHome("java.home"),

	Classpath("java.class.path"),
	LibsPath("java.library.path"),
	Compiler("java.compiler"),
	ExternalLibs("java.ext.dirs"),

	SpecificationVersion("java.specification.version"),
	SpecificationVendor("java.specification.vendor"),
	SpecificationName("java.specification.name"),
	ClassesVersion("java.class.version"),

	VM_SpecificationVersion("java.vm.specification.version"),
	VM_SpecificationVendor("java.vm.specification.vendor"),
	VM_SpecificationName("java.vm.specification.name"),
	VM_Version("java.vm.version"),
	VM_Vendor("java.vm.vendor"),
	VM_Name("java.vm.name"),

	OS_Name("os.name"),
	OS_Version("os.version"),

	FileSeparator("file.separator"),
	PathSeparator("path.separator"),
	LineSeparator("line.separator"),

	CurrentUserName("user.name"),
	CurrentUserHomeDirectory("user.home"),
	CurrentUserWorkingDir("user.dir");

	private final String key;

	private EnvVariables(String key) {
		this.key = key;
	}

	public String getValue() {
		return System.getProperty(key);
	}

	@Override
	public String toString() {
		return key + "=" + getValue();
	}
}
