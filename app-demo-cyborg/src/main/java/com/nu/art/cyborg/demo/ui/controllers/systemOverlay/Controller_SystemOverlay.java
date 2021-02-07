

package com.nu.art.cyborg.demo.ui.controllers.systemOverlay;

import android.view.View;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.modules.SystemOverlayModule;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.InternetConnectivityModule.InternetConnectivityListener;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class Controller_SystemOverlay
	extends CyborgController
	implements InternetConnectivityListener {

	@ViewIdentifier(viewId = R.id.TV_Overlay1,
	                listeners = ViewListener.OnClick)
	private TextView showOverlay1;

	@ViewIdentifier(viewId = R.id.TV_Overlay2,
	                listeners = ViewListener.OnClick)
	private TextView showOverlay2;

	public Controller_SystemOverlay() {
		super(R.layout.controller__system_overlay);
	}

	@Override
	protected void onDestroy() {
	}

	@Override
	public void onClick(View v) {
		SystemOverlayModule module = getModule(SystemOverlayModule.class);
		if (!module.requestDrawOverOtherAppsPermissionIfNeeded(getActivity()))
			return;

		switch (v.getId()) {
			case R.id.TV_Overlay1:
				module.showOverlay(Controller_Overlay1.class);
				break;

			case R.id.TV_Overlay2:
				break;
		}
		renderUI();
	}

	@Override
	protected void onResume() {
		renderUI();
	}

	@Override
	public void onInternetConnectivityChanged() {
		renderUI();
	}

	@Override
	protected void render() {
		super.render();
	}
}
