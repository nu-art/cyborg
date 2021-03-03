package com.nu.art.modular.core;

/**
 * While I Completely disagree with this hack I have been ask number of times about this capability.
 *
 * Using this is a result of not sticking to the module pattern, and trying mix and match a bunch of frameworks..
 * Do me a favor.. don't mix and match it's stupid.. use the module management as intended or use whatever other crappy framework you'd like!!!
 */
@Deprecated
public class HackApi {

	@Deprecated
	public static <_Module extends Module> void registerModuleType(Class<_Module> moduleType) {
		ModuleManager.ModuleManager.logDebug("Registering runtime Module Type: " + moduleType.getSimpleName());
		ModuleManager.ModuleManager.registerModuleType(moduleType);
	}

	@Deprecated
	public static <_Module extends Module> void registerModuleInstance(_Module module) {
		ModuleManager.ModuleManager.logDebug("Registering runtime Module instance: " + module);
		ModuleManager.ModuleManager.registerModuleInstance(module);
	}
}
