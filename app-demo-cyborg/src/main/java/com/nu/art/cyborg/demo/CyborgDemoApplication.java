

package com.nu.art.cyborg.demo;

import android.app.Application;

import com.nu.art.belog.BeConfig;
import com.nu.art.belog.BeLogged;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.tools.StreamTools;
import com.nu.art.cyborg.core.CyborgBuilder;
import com.nu.art.cyborg.core.CyborgBuilder.CyborgConfiguration;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgStackController;
import com.nu.art.cyborg.core.abs._DebugFlags;
import com.nu.art.cyborg.demo.model.MyModulePack;

import java.io.IOException;

public class CyborgDemoApplication
	extends Application {

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate() {
		super.onCreate();
		CyborgStackController.DebugFlag.enable();
		CyborgController.DebugFlag.enable();
		_DebugFlags.Debug_Performance.enable();

		Getter<BeConfig> logConfigLoader = new Getter<BeConfig>() {
			@Override
			public BeConfig get() {
				try {
					BeLogged beLogged = BeLogged.getInstance();
					beLogged.addConfigParam("appName", "cyborg-demo-app");
					String configAsString = StreamTools.readFullyAsString(getResources().openRawResource(R.raw.log_config_simple));
					return (BeConfig) beLogged.getSerializer().deserialize(configAsString, BeConfig.class);
				} catch (IOException e) {
					throw new BadImplementationException("Unable to deserialize log config");
				}
			}
		};

		CyborgBuilder.startCyborg(new CyborgConfiguration(this)
			                          .setLaunchConfiguration(R.layout.activity__examples_selection)
			                          .setModulesPacks(MyModulePack.class)
			                          .setLogConfig(logConfigLoader));
	}
}
