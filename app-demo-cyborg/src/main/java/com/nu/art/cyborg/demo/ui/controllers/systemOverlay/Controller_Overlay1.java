

package com.nu.art.cyborg.demo.ui.controllers.systemOverlay;

import android.view.View;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.InternetConnectivityModule;
import com.nu.art.cyborg.modules.InternetConnectivityModule.InternetConnectivityListener;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class Controller_Overlay1
	extends CyborgController
	implements InternetConnectivityListener {

	@ViewIdentifier(viewId = R.id.TV_Button,
	                listeners = ViewListener.OnClick)
	private TextView button;

	@ViewIdentifier(viewId = R.id.TV_Internet)
	private TextView internet;

	public Controller_Overlay1() {
		super(R.layout.controller__system_overlay_1);
	}

	@Override
	protected void onDestroy() {
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.TV_Button:
				toastDebug("pressed the button from overlay!!");
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
		String text = (getModule(InternetConnectivityModule.class).isConnected() ? "HAS" : "NO") + " INTERNET";
		internet.setText(text);
	}
}
