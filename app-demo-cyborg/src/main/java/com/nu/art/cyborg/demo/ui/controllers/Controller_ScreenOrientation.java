

package com.nu.art.cyborg.demo.ui.controllers;

import android.view.View;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.InternetConnectivityModule.InternetConnectivityListener;
import com.nu.art.cyborg.modules.ScreenOptionsModule;
import com.nu.art.cyborg.modules.ScreenOptionsModule.ScreenOrientation;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class Controller_ScreenOrientation
	extends CyborgController
	implements InternetConnectivityListener {

	@ViewIdentifier(viewId = R.id.TV_ActionLandscape,
	                listeners = ViewListener.OnClick)
	private TextView actionLandscape;

	@ViewIdentifier(viewId = R.id.TV_ActionPortrait,
	                listeners = ViewListener.OnClick)
	private TextView actionPortrait;
	private ScreenOptionsModule screenModule;

	public Controller_ScreenOrientation() {
		super(R.layout.controller__screen_orientation);
	}

	@Override
	protected void onCreate() {
		screenModule.setActivity(getActivity());
	}

	@Override
	protected void onDestroy() {
		screenModule.setActivity(null);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.TV_ActionLandscape:
				screenModule.setRequestScreenOrientation(ScreenOrientation.SENSOR_LANDSCAPE);
				break;

			case R.id.TV_ActionPortrait:
				screenModule.setRequestScreenOrientation(ScreenOrientation.PORTRAIT);
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
		ScreenOrientation screenOrientation = screenModule.getRequestedScreenOrientation();
		actionLandscape.setBackgroundResource(screenOrientation != ScreenOrientation.LANDSCAPE ? R.drawable.background__24radius_red
		                                                                                       : R.drawable.background__24radius_green);
		actionPortrait.setBackgroundResource(
			screenOrientation != ScreenOrientation.PORTRAIT ? R.drawable.background__24radius_red : R.drawable.background__24radius_green);
	}
}
