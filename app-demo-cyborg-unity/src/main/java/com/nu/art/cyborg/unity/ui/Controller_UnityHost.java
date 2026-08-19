package com.nu.art.cyborg.unity.ui;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.unity.R;
import com.nu.art.cyborg.unity.modules.Module_Unity;
import com.nu.art.cyborg.unity.modules.Module_Unity.UnityPingListener;

public class Controller_UnityHost
	extends CyborgController
	implements UnityPingListener {

	@ViewIdentifier(viewId = R.id.unity_container)
	private FrameLayout unityContainer;

	@ViewIdentifier(viewId = R.id.BTN_PingUnity, listeners = ViewListener.OnClick)
	private Button pingButton;

	@ViewIdentifier(viewId = R.id.TV_FromUnity)
	private TextView fromUnity;

	@ViewIdentifier(viewId = R.id.color_chip)
	private View colorChip;

	private Module_Unity unity;

	public Controller_UnityHost() {
		super(R.layout.controller__unity_host);
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		unity.attachTo(unityContainer, getActivity());
	}

	@Override
	protected void onResume() {
		super.onResume();
		unity.resume();
	}

	@Override
	protected void onPause() {
		unity.pause();
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		unity.pingUnity("cyborg-overlay");
		super.onClick(v);
	}

	@Override
	public void onUnityPing(String payload) {
		fromUnity.setText("cube color " + payload);
		if (payload != null && payload.startsWith("#") && payload.length() == 7) {
			try {
				colorChip.setBackgroundColor(Color.parseColor(payload));
			} catch (IllegalArgumentException ignore) {}
		}
	}
}
