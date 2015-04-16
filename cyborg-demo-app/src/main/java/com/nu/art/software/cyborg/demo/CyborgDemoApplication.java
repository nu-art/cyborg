package com.nu.art.software.cyborg.demo;

import android.app.Application;

import com.nu.art.software.cyborg.core.CyborgBuilder;
import com.nu.art.software.cyborg.core.CyborgBuilder.CyborgConfiguration;
import com.nu.art.software.cyborg.demo.model.MyModulePack;

public class CyborgDemoApplication
		extends Application {


	@Override
	@SuppressWarnings("unchecked")
	public void onCreate() {
		super.onCreate();
//		CyborgBuilder.startCyborg(new CyborgConfiguration(this, R.layout.v1_activity__injection_example));
		CyborgBuilder.startCyborg(new CyborgConfiguration(this, R.layout.v1_activity__injection_example, MyModulePack.class));
	}
}
