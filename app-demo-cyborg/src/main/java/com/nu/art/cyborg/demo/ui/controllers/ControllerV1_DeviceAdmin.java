

package com.nu.art.cyborg.demo.ui.controllers;

import android.view.View;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.deviceAdmin.DeviceAdminModule;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class ControllerV1_DevicePolicy
	extends CyborgController {

	DeviceAdminModule module;

	@ViewIdentifier(viewId = R.id.TV_DevicePolicy_Button1,
	                listeners = ViewListener.OnClick)
	TextView button1;

	public ControllerV1_DevicePolicy() {
		super(R.layout.controller__material);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.TV_DevicePolicy_Button1: {

			}
		}
	}
}
