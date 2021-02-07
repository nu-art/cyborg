package com.nu.art.cyborg.demo.ui.controllers.liveModule;

import com.nu.art.cyborg.modules.InternetConnectivityModule;
import com.nu.art.cyborg.modules.InternetConnectivityModule.InternetConnectivityListener;
import com.nu.art.modular.core.Module;

public class LiveModule
	extends Module
	implements InternetConnectivityListener {

	@Override
	protected void init() {

	}

	@Override
	public void onInternetConnectivityChanged() {
		logInfo("onInternetConnectivityChanged: " + getModule(InternetConnectivityModule.class).isConnected());
	}
}
