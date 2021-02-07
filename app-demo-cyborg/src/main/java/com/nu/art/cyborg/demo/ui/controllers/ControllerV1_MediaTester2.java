package com.nu.art.cyborg.demo.ui.controllers;

import android.Manifest.permission;
import android.view.View;
import android.widget.ImageView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.demo.model.MyModule;
import com.nu.art.cyborg.media.CyborgMediaPlayer;
import com.nu.art.cyborg.modules.PermissionModule;
import com.nu.art.cyborg.modules.PermissionModule.PermissionResultListener;

/**
 * Created by TacB0sS on 30/11/2017.
 */

public class ControllerV1_MediaTester2
	extends CyborgController
	implements PermissionResultListener {

	@ViewIdentifier(viewId = R.id.IV_PlayPause,
	                listeners = ViewListener.OnClick)
	ImageView button;
	CyborgMediaPlayer mediaPlayer;

	public ControllerV1_MediaTester2() {
		super(R.layout.controller__media_player_tester);
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		mediaPlayer = getModule(MyModule.class).createPlayer();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getModule(PermissionModule.class).requestPermission(200, permission.READ_EXTERNAL_STORAGE);
	}

	@Override
	public void onPermissionsRejected(int requestCode, String[] rejected) {
		postOnUI(new Runnable() {
			@Override
			public void run() {
				getActivity().onBackPressed();
			}
		});
	}

	@Override
	public void onAllPermissionsGranted(int requestCode) {
		mediaPlayer.createBuilder().setUri("/sdcard/Download/songs/missions.mp3").prepare();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.IV_PlayPause:
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					mediaPlayer.setVolume(60);
				} else
					mediaPlayer.play();
				break;
		}
	}
}
