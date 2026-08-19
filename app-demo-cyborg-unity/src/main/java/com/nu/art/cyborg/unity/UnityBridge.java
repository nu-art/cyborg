package com.nu.art.cyborg.unity;

import com.nu.art.cyborg.unity.modules.Module_Unity;

/**
 * Called from Unity C# via AndroidJavaClass. Keep the FQCN stable.
 */
public final class UnityBridge {

	private UnityBridge() {}

	public static void onUnityPing(String payload) {
		Module_Unity module = Module_Unity.getIfReady();
		if (module == null)
			return;
		module.onNativePing(payload);
	}
}
