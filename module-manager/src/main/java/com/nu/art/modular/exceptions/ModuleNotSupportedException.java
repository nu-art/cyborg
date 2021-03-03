/*
 * The module-manager project, is THE infrastructure that all my frameworks
 *  are based on, it allows encapsulation of logic where needed, and allow
 *  modules to converse without other design patterns limitations.
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

package com.nu.art.modular.exceptions;

public class ModuleNotSupportedException
	extends RuntimeException {

	private static final long serialVersionUID = -8217638105874287525L;

	public ModuleNotSupportedException(String reason, Throwable e) {
		super(reason, e);
	}

	public ModuleNotSupportedException(String reason) {
		super(reason);
	}

	public ModuleNotSupportedException(Class<? extends com.nu.art.modular.core.Module> moduleType, String reason, Throwable e) {
		super("Could not attach module: " + moduleType.getName() + ", reason: " + reason, e);
		e.printStackTrace();
	}

	public ModuleNotSupportedException(Class<? extends com.nu.art.modular.core.Module> moduleType, Throwable e) {
		super("Could not attach module: " + moduleType.getName(), e);
		System.err.println();
		e.printStackTrace();
	}

	public ModuleNotSupportedException(Class<? extends com.nu.art.modular.core.Module> moduleType, String reason) {
		super("Could not attach module: " + moduleType.getName() + ", reason: " + reason);
	}
}
