

package com.nu.art.cyborg.demo.ui.controllers;

import android.view.View;
import android.widget.TextView;

import com.nu.art.core.utils.TimeProxy;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.AudioOptionsModule;
import com.nu.art.cyborg.modules.AudioOptionsModule.AudioStreamType;
import com.nu.art.cyborg.modules.InternetConnectivityModule;
import com.nu.art.cyborg.modules.InternetConnectivityModule.InternetConnectivityListener;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class Controller_SystemVolume
	extends CyborgController
	implements InternetConnectivityListener {

	@ViewIdentifier(viewId = R.id.TV_ActionMain,
	                listeners = ViewListener.OnClick)
	private TextView actionMain;

	@ViewIdentifier(viewId = R.id.TV_ActionOther,
	                listeners = ViewListener.OnClick)
	private TextView actionOther;

	private Runnable main;
	private Runnable background;

	public Controller_SystemVolume() {
		super(R.layout.controller__system_volume);
	}

	@Override
	protected void onDestroy() {
		background = null;
		main = null;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.TV_ActionMain:
				if (main != null) {
					main = null;
					break;
				}
				postOnUI(main = new Runnable() {

					@Override
					public void run() {
						updateVolume(4);
						postOnUI(1000, new Runnable() {

							@Override
							public void run() {
								updateALLVolume(0);
								if (main == null)
									// break the loop
									return;

								postOnUI(1000, main);
							}
						});
					}
				});
				break;

			case R.id.TV_ActionOther:
				if (background != null) {
					background = null;
					break;
				}

				postOnUI(background = new Runnable() {

					@Override
					public void run() {
						updateVolume(4);
						postOnUI(1000, new Runnable() {

							@Override
							public void run() {
								updateALLVolume(0);
								if (background == null)
									// break the loop
									return;

								postOnUI(1000, background);
							}
						});
					}
				});

				break;
		}
		renderUI();
	}

	private void updateALLVolume(int i) {
		long started = TimeProxy.currentTimeMillis();
		logDebug("Setting volume to: " + i);
		getModule(AudioOptionsModule.class).setStreamVolume(AudioStreamType.Music, i);
		getModule(AudioOptionsModule.class).setStreamVolume(AudioStreamType.Notification, i);
		getModule(AudioOptionsModule.class).setStreamVolume(AudioStreamType.Alarm, i);
		getModule(AudioOptionsModule.class).setStreamVolume(AudioStreamType.Ring, i);
		getModule(AudioOptionsModule.class).setStreamVolume(AudioStreamType.System, i);
		logDebug("DONE!!" + (TimeProxy.currentTimeMillis() - started));
	}

	private void updateVolume(int i) {
		long started = TimeProxy.currentTimeMillis();
		logDebug("Setting volume to: " + i);
		getModule(AudioOptionsModule.class).setStreamVolume(AudioStreamType.Music, i);
		logDebug("DONE!!" + (TimeProxy.currentTimeMillis() - started));
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
		actionMain.setBackgroundResource(main == null ? R.drawable.background__24radius_red : R.drawable.background__24radius_green);
		actionMain.setText(main == null ? "Start" : "Stop");
		actionOther.setBackgroundResource(background == null ? R.drawable.background__24radius_red : R.drawable.background__24radius_green);
		actionOther.setText(background == null ? "Start" : "Stop");
	}
}
