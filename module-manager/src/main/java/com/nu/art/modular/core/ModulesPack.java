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

package com.nu.art.modular.core;

import com.nu.art.belog.Logger;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.tools.ArrayTools;

import java.util.HashMap;

/**
 * This is an important aspect of Cyborg, these packs are meant to encapsulate <b>YOUR</b> modules, and to allow you to add different packs to construct your
 * application or library.
 * <br>
 * Each ModulePack is defined by a list of {@link Module}s which are provided in the intialization of the application.
 */
public class ModulesPack
	extends Logger {

	final Class<? extends Module>[] moduleTypes;

	@SuppressWarnings("unchecked")
	public ModulesPack(Class<? extends Module>... moduleTypes) {
		super();
		this.moduleTypes = moduleTypes;
	}

	protected ModuleManager manager;

	final void setManager(ModuleManager manager) {
		this.manager = manager;
	}

	protected final <Type extends Module> Type getModule(Class<Type> moduleType) {
		if (ArrayTools.contains(moduleTypes, moduleType))
			return manager.getModule(moduleType);

		throw new BadImplementationException("Cannot access module(" + moduleType.getSimpleName() + ") that was not defined in this pack(" + getClass().getSimpleName() + ").");
	}

	protected void init() {}

	protected void postInit() {}
}

