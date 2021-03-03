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
import com.nu.art.modular.core.ModuleManager.ModuleCreatedListener;
import com.nu.art.modular.core.ModuleManager.ModuleInitializedListener;
import com.nu.art.modular.interfaces.OnApplicationStartingListener;
import com.nu.art.reflection.tools.ReflectiveTools;

import java.util.ArrayList;
import java.util.Arrays;

public class ModuleManagerBuilder
	extends Logger
	implements OnApplicationStartingListener {

	private ArrayList<ModulesPack> modulePacks = new ArrayList<>();
	private ModuleInitializedListener moduleInitializedListener = (this instanceof ModuleInitializedListener ? (ModuleInitializedListener) this : null);
	private ModuleCreatedListener moduleCreatedListener = (this instanceof ModuleCreatedListener ? (ModuleCreatedListener) this : null);
	protected final ModuleManager manager = new ModuleManager();
	private OnApplicationStartingListener listener = this;

	public ModuleManagerBuilder() {
	}

	public ModuleManagerBuilder setOnApplicationStartingListener(OnApplicationStartingListener listener) {
		if (listener != null)
			this.listener = listener;
		return this;
	}

	public ModuleManagerBuilder setModuleInitializedListener(ModuleInitializedListener moduleInitializedListener) {
		this.moduleInitializedListener = moduleInitializedListener;
		return this;
	}

	public ModuleManagerBuilder setModuleCreatedListener(ModuleCreatedListener moduleCreatedListener) {
		this.moduleCreatedListener = moduleCreatedListener;
		return this;
	}

	@SuppressWarnings("unchecked")
	public final ModuleManagerBuilder addModulePacks(Class<? extends ModulesPack>... modulePacks) {
		for (Class<? extends ModulesPack> packType : modulePacks) {
			ModulesPack pack = ReflectiveTools.newInstance(packType);
			this.modulePacks.add(pack);
		}
		return this;
	}

	public final ModuleManagerBuilder addModulePacks(ModulesPack... modulePacks) {
		this.modulePacks.addAll(Arrays.asList(modulePacks));
		return this;
	}

	@SuppressWarnings("unchecked")
	public final ModuleManagerBuilder addModules(Class<? extends Module>... modules) {
		this.modulePacks.add(new ModulesPack(modules));
		return this;
	}

	public final ModuleManager build() {
		manager.setModuleCreatedListener(this.moduleCreatedListener);
		manager.setModuleInitializedListener(this.moduleInitializedListener);

		ArrayList<Class<? extends Module>> modulesTypes = new ArrayList<>();

		for (ModulesPack pack : modulePacks) {
			pack.setManager(manager);
			for (Class<? extends Module> moduleType : pack.moduleTypes) {
				if (modulesTypes.contains(moduleType))
					continue;

				modulesTypes.add(moduleType);
				manager.registerModule(moduleType);
			}
		}

		for (ModulesPack pack : modulePacks) {
			pack.init();
		}

		Module[] registeredModules = manager.getOrderedModules();
		validateModules(registeredModules);

		for (Module registeredModule : registeredModules) {
			manager.getInjector().injectToInstance(registeredModule);
		}

		logVerbose(" Application Starting...");
		logVerbose(" ");

		listener.onApplicationStarting();
		manager.init();
		for (Module module : registeredModules) {
			logInfo("----------- " + module.getClass().getSimpleName() + " ------------");
			module.printDetails();
			logInfo("-------- End of " + module.getClass().getSimpleName() + " --------");
		}

		manager.onBuildCompleted();
		return manager;
	}

	private void validateModules(Module[] allRegisteredModuleInstances) {
		ValidationResult result = new ValidationResult();

		for (Module module : allRegisteredModuleInstances)
			module.validateModule(result);

		if (!result.isEmpty())
			throw new com.nu.art.modular.exceptions.ModuleNotSupportedException("\n" + result.getErrorData());
	}

	protected void postInit(Module[] allRegisteredModuleInstances) {
	}

	public void onApplicationStarting() {
		logVerbose(" _______  _______  _______  _       _________ _______  _______ __________________ _______  _          _______ _________ _______  _______ _________ _______  ______  ");
		logVerbose("(  ___  )(  ____ )(  ____ )( \\      \\__   __/(  ____ \\(  ___  )\\__   __/\\__   __/(  ___  )( (    /|  (  ____ \\\\__   __/(  ___  )(  ____ )\\__   __/(  ____ \\(  __  \\ ");
		logVerbose("| (   ) || (    )|| (    )|| (         ) (   | (    \\/| (   ) |   ) (      ) (   | (   ) ||  \\  ( |  | (    \\/   ) (   | (   ) || (    )|   ) (   | (    \\/| (  \\  )");
		logVerbose("| (___) || (____)|| (____)|| |         | |   | |      | (___) |   | |      | |   | |   | ||   \\ | |  | (_____    | |   | (___) || (____)|   | |   | (__    | |   ) |");
		logVerbose("|  ___  ||  _____)|  _____)| |         | |   | |      |  ___  |   | |      | |   | |   | || (\\ \\) |  (_____  )   | |   |  ___  ||     __)   | |   |  __)   | |   | |");
		logVerbose("| (   ) || (      | (      | |         | |   | |      | (   ) |   | |      | |   | |   | || | \\   |        ) |   | |   | (   ) || (\\ (      | |   | (      | |   ) |");
		logVerbose("| )   ( || )      | )      | (____/\\___) (___| (____/\\| )   ( |   | |   ___) (___| (___) || )  \\  |  /\\____) |   | |   | )   ( || ) \\ \\__   | |   | (____/\\| (__/  )");
		logVerbose("|/     \\||/       |/       (_______/\\_______/(_______/|/     \\|   )_(   \\_______/(_______)|/    )_)  \\_______)   )_(   |/     \\||/   \\__/   )_(   (_______/(______/ ");
		logVerbose(" ");
	}
}


