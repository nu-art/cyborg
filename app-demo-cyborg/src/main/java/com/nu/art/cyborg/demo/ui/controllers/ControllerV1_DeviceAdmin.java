

package com.nu.art.cyborg.demo.ui.controllers;

import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.nu.art.core.tools.ArrayTools;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.deviceAdmin.DeviceAdminModule;

import static com.nu.art.storage.PreferencesModule.JsonSerializer.gson;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class ControllerV1_DeviceAdmin
	extends CyborgController {

	DeviceAdminModule module;

	@ViewIdentifier(viewId = {
		R.id.TV_DevicePolicy_Button1A,
		R.id.TV_DevicePolicy_Button1B,
		R.id.TV_DevicePolicy_Button2A,
		R.id.TV_DevicePolicy_Button2B,
		R.id.TV_DevicePolicy_Button3A,
		R.id.TV_DevicePolicy_Button3B,
		R.id.TV_DevicePolicy_Button4A,
		R.id.TV_DevicePolicy_Button4B,
		R.id.TV_DevicePolicy_Button4C,
	},
	                listeners = ViewListener.OnClick)
	View[] button1;

	@ViewIdentifier(viewId = R.id.TV_DevicePolicy_Output,
	                listeners = ViewListener.OnClick)
	TextView output;

	public ControllerV1_DeviceAdmin() {
		super(R.layout.controller__device_admin);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.TV_DevicePolicy_Button1A:
				module.enableDeviceAdmin(this.getActivity(), "Allow Admin?");
				break;

			case R.id.TV_DevicePolicy_Button1B:
				module.disableDeviceAdmin();
				break;

			case R.id.TV_DevicePolicy_Button2A:
				try {
					module.setLockTaskPackages(getPackageName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.TV_DevicePolicy_Button2B:
				try {
					module.setLockTaskPackages(ArrayTools.newInstance(String.class, 0));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case R.id.TV_DevicePolicy_Button3A:
				getActivity().startLockTask();
				break;

			case R.id.TV_DevicePolicy_Button3B:
				getActivity().stopLockTask();
				break;

			case R.id.TV_DevicePolicy_Button4A:
				module.disableDeviceOwner();
				break;

			case R.id.TV_DevicePolicy_Button4B:
				getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				break;

			case R.id.TV_DevicePolicy_Button4C:
				View decorView = getActivity().getWindow().getDecorView();
				decorView.setSystemUiVisibility(getFlags());
				break;
		}

		StringBuffer buffer = new StringBuffer()
			.append("isDeviceAdmin: ").append(module.isDeviceAdmin()).append("\n")
			.append("isDeviceOwner: ").append(module.isDeviceOwner()).append("\n")
			.append("isProfileOwner: ").append(module.isProfileOwner()).append("\n")
			.append("ActiveAdmins: ").append(module.getActiveAdmins()).append("\n")
			.append("isLockTaskPermitted: ").append(module.isLockTaskPermitted()).append("\n");
		try {
			buffer.append("packages that are allowed to lock: ").append(gson.toJson(module.getLockTaskPackages()));
		} catch (Exception e) {
			buffer.append("lockedPackages: ").append(e.getMessage());
		}
		this.output.setText(buffer.toString());
	}


	private int getFlags() {
		return
			View.SYSTEM_UI_FLAG_LAYOUT_STABLE | // Uncertain if necessary.
				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |// Ask to be drawn as if there is no nav-bar
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |// Ask to be drawn as if there is no status-bar
				View.SYSTEM_UI_FLAG_LOW_PROFILE |// "Status bar and/or nav icons may dim", to be used in immersive mode apps.
				//// ___The following 3 flags go together___
				//// FLAG_HIDE_NAVIGATION + FLAG_FULLSCREEN Are usually force-cleared by the system on user interaction.
				View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |// won't be force cleared, because of SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				View.SYSTEM_UI_FLAG_FULLSCREEN // won't be force cleared, because of SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			////
			;
	}
}
