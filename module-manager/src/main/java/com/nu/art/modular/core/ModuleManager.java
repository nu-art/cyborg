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
import com.nu.art.core.exceptions.runtime.ImplementationMissingException;
import com.nu.art.core.generics.GenericParamExtractor;
import com.nu.art.core.generics.Processor;
import com.nu.art.core.interfaces.Condition;
import com.nu.art.core.interfaces.ILogger;
import com.nu.art.core.tools.ArrayTools;
import com.nu.art.modular.interfaces.ModuleManagerDelegator;
import com.nu.art.reflection.injector.Injector;
import com.nu.art.reflection.tools.ART_Tools;
import com.nu.art.reflection.tools.ReflectiveTools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author TacB0sS
 */
@SuppressWarnings("rawtypes")
public class ModuleManager
	extends Logger
	implements ModuleManagerDelegator {

	private boolean intialized;

	public interface ModuleInitializedListener {

		void onModuleInitialized(Module module);
	}

	public interface ModuleCreatedListener {

		void onModuleCreated(Module module);
	}

	public final class ModuleInjector
		extends Injector<Module, Object> {

		private ModuleInjector() {}

		@Override
		@SuppressWarnings("unchecked")
		protected Object getValueForField(Object instance, Field field) {
			if (field.getType() == Module.class)
				return null;

			Module module = getModule((Class<? extends Module>) field.getType(), false);
			if (module == null)
				throw new ImplementationMissingException("Cannot set module to field: " + field + "\n  MUST add the module of type: '" + field.getType() + "' to one of your ModulePacks");

			return module;
		}

		@Override
		protected Field[] extractFieldsFromInstance(Class<?> injecteeType) {
			return ART_Tools.getAllFieldsInHierarchy(injecteeType, new Condition<Field>() {
				@Override
				public boolean checkCondition(Field field) {
					return Module.class.isAssignableFrom(field.getType()) && !Modifier.isStatic(field.getModifiers());
				}
			});
		}
	}

	private final ModuleInjector moduleInjector = new ModuleInjector();

	private ModuleInitializedListener moduleInitializedListener;
	private ModuleCreatedListener moduleCreatedListener;

	/**
	 * Holds a references to all the module types which have registered to this main module,
	 */
	private HashMap<Class<? extends Module>, Module> registeredModules = new HashMap<>();

	private EventDispatcher eventDispatcher;

	private Module[] orderedModules = {};

	public static ModuleManager ModuleManager;

	public ModuleManager() {
		this(GenericParamExtractor._GenericParamExtractor);
	}

	protected ModuleManager(GenericParamExtractor paramExtractor) {
		if (ModuleManager != null)
			throw new BadImplementationException("THERE CAN ONLY BE ONE MODULE MANAGER IN A JVM!!");

		eventDispatcher = new EventDispatcher("ModulesEventDispatcher", paramExtractor);
		ModuleManager = this;
	}

	public final ModuleInjector getInjector() {
		return moduleInjector;
	}

	@SuppressWarnings("unchecked")
	public final <Type> Type[] getModulesAssignableFrom(Class<Type> classType) {
		ArrayList<Type> modules = new ArrayList<>();
		for (Module orderedModule : orderedModules) {
			if (!classType.isAssignableFrom(orderedModule.getClass()))
				continue;

			modules.add((Type) orderedModule);
		}
		return ArrayTools.asArray(modules, classType);
	}

	@Override
	public final <ModuleType extends Module> ModuleType getModule(Class<ModuleType> moduleType) {
		return getModule(moduleType, true);
	}

	protected final Module[] getOrderedModules() {
		return orderedModules;
	}

	public final void setModuleInitializedListener(ModuleInitializedListener moduleInitializedListener) {
		this.moduleInitializedListener = moduleInitializedListener;
	}

	public final void setModuleCreatedListener(ModuleCreatedListener moduleCreatedListener) {
		this.moduleCreatedListener = moduleCreatedListener;
	}

	@SuppressWarnings("unchecked")
	private <ModuleType extends Module> ModuleType getModule(Class<ModuleType> moduleType, boolean throwException) {
		ModuleType module = (ModuleType) registeredModules.get(moduleType);
		if (module == null && throwException) {
			throw new ImplementationMissingException("MUST add module of type: '" + moduleType.getName() + "' to one of your module packs");
		}
		return module;
	}

	public final synchronized void init() {
		if (intialized)
			throw new BadImplementationException("Module manager was already initialized!");

		intialized = true;
		for (Module module : orderedModules) {
			module.assignToDefaultInterface();
		}

		for (Module module : orderedModules) {
			module.init();

			if (moduleInitializedListener == null)
				continue;

			moduleInitializedListener.onModuleInitialized(module);
		}

		for (Module module : orderedModules) {
			logInfo("----------- " + module.getClass().getSimpleName() + " ------------");
			module.printDetails();
			logInfo("-------- End of " + module.getClass().getSimpleName() + " --------");
		}

		onBuildCompleted();
	}

	/**
	 * @param moduleType The module type to register with the Module Manager.
	 */
	@SuppressWarnings("unchecked")
	final <_Module extends Module> void registerModule(Class<_Module> moduleType) {
		_Module module = (_Module) registeredModules.get(moduleType);
		if (module != null)
			return;

		registerModuleType(moduleType);
	}

	public final <_Module extends Module> void registerMockModule(Class<_Module> moduleType, _Module module) {
		registeredModules.put(moduleType, module);
	}

	final <_Module extends Module> void registerModuleType(Class<_Module> moduleType) {
		_Module module;
		module = ReflectiveTools.newInstance(moduleType);
		registerModuleInstance(module);
	}

	final <_Module extends Module> void registerModuleInstance(_Module module) {
		module.setMainManager(this);

		for (Class<? extends Module> key : module.keys) {
			Module olderModule = registeredModules.put(key, module);
			if (olderModule != null)
				logWarning("Shared Module key " + key + " between modules: " + olderModule.getClass() + " and " + module.getClass());
		}

		eventDispatcher.addListener(module);
		this.orderedModules = ArrayTools.appendElement(this.orderedModules, module);

		if (moduleCreatedListener == null)
			return;

		moduleCreatedListener.onModuleCreated(module);
	}

	protected void onBuildCompleted() {}

	final void prepareModuleItem(ModuleItem moduleItem) {
		getInjector().injectToInstance(moduleItem);
		eventDispatcher.addListener(moduleItem);
	}

	final void disposeModuleItem(ModuleItem moduleItem) {
		eventDispatcher.removeListener(moduleItem);
	}

	@SuppressWarnings("unchecked")
	public <ListenerType> void dispatchModuleEvent(ILogger originator, String message, Class<ListenerType> listenerType, Processor<ListenerType> processor) {
		if (originator != null)
			originator.logInfo("Dispatching Module Event: " + message);
		eventDispatcher.dispatchEvent(null, listenerType, processor);
	}
}
