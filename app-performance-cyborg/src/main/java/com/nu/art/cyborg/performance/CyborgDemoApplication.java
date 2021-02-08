

package com.nu.art.cyborg.performance;

import android.app.Application;

import com.nu.art.cyborg.core.CyborgBuilder;
import com.nu.art.cyborg.core.CyborgBuilder.CyborgConfiguration;
import com.nu.art.cyborg.core.CyborgStackController;

public class CyborgDemoApplication
	extends Application {

	private final String TAG = getClass().getSimpleName();

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate() {
		super.onCreate();
		CyborgStackController.DebugFlag.enable();
		CyborgBuilder.startCyborg(new CyborgConfiguration(this).setLaunchConfiguration(R.layout.activity__performance).setModulesPacks(MyModulePack.class));
	}
}
