package com.nu.art.cyborg.unity.core;

import android.app.Application;

import com.nu.art.cyborg.core.CyborgBuilder;
import com.nu.art.cyborg.core.CyborgBuilder.CyborgConfiguration;
import com.nu.art.cyborg.core.CyborgBuilder.LaunchConfiguration;
import com.nu.art.cyborg.unity.R;
import com.nu.art.cyborg.unity.ui.CyborgActivity_Unity;

public class MyApplication
	extends Application {

	@SuppressWarnings("unchecked")
	private final Class<ModulePack_Unity>[] Modules = new Class[]{
		ModulePack_Unity.class
	};

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate() {
		super.onCreate();
		CyborgBuilder.startCyborg(new CyborgConfiguration(this).setLaunchConfiguration(new LaunchConfiguration().setLayoutId(R.layout.cyborgview__unity_host)
		                                                                                                      .setActivityType(CyborgActivity_Unity.class)
		                                                                                                      .setScreenName("Unity"))
		                                                       .setModulesPacks(Modules));
	}
}
