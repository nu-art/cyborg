

package com.nu.art.cyborg.demo.ui.controllers;

import android.hardware.fingerprint.FingerprintManager.AuthenticationResult;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.FingerprintModule;
import com.nu.art.cyborg.modules.FingerprintModule.FingerprintAuthenticationListener;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class ControllerV1_Fingerprint
	extends CyborgController
	implements FingerprintAuthenticationListener {

	@ViewIdentifier(viewId = R.id.TV_NoSupport)
	private TextView noSupport;

	@ViewIdentifier(viewId = R.id.RL_HasSupport)
	private RelativeLayout hasSupport;

	@ViewIdentifier(viewId = R.id.TV_Start,
	                listeners = ViewListener.OnClick)
	private TextView start;

	@ViewIdentifier(viewId = R.id.TV_Stop,
	                listeners = ViewListener.OnClick)
	private TextView stop;

	@ViewIdentifier(viewId = R.id.TV_Result)
	private TextView results;

	@ViewIdentifier(viewId = R.id.IV_Fingerprint)
	private ImageView fingerprintIcon;

	private FingerprintModule fingerprintModule;

	public ControllerV1_Fingerprint() {
		super(R.layout.controller__fingerprint);
	}

	@Override
	protected void onCreate() {
		boolean enabled = fingerprintModule.isEnabled();
		noSupport.setVisibility(!enabled ? View.VISIBLE : View.INVISIBLE);
		hasSupport.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		renderUI();
	}

	@Override
	@RequiresApi(api = VERSION_CODES.M)
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.TV_Start:
				try {
					fingerprintModule.startListening("Test");
				} catch (Exception e) {
					logError("error listening", e);
				}
				break;
			case R.id.TV_Stop:
				fingerprintModule.stopListening();
				break;
		}
		renderUI();
	}

	@Override
	protected void render() {
		fingerprintIcon.setVisibility(fingerprintModule.isListening() ? View.VISIBLE : View.INVISIBLE);
		results.setText(fingerprintModule.isListening() ? "Listening" : "");
	}

	@Override
	public void onAuthenticationError(int errorCode, CharSequence errString) {
		renderUI();
		results.setText("help: " + errString + "(" + errorCode + ")");
	}

	@Override
	public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
		renderUI();
		results.setText("help: " + helpString + "(" + helpCode + ")");
	}

	@Override
	public void onAuthenticationSucceeded(AuthenticationResult result) {
		renderUI();
		results.setText("Success");
	}

	@Override
	public void onAuthenticationFailed() {
		renderUI();
		results.setText("Failed");
	}
}
