

package com.nu.art.cyborg.demo.ui.controllers;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.hardware.Camera.CameraInfo;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.nu.art.core.tools.FileTools;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.PermissionModule;
import com.nu.art.cyborg.modules.PermissionModule.PermissionResultListener;
import com.nu.art.cyborg.modules.camera.CameraException;
import com.nu.art.cyborg.modules.camera.CameraLayer;
import com.nu.art.cyborg.modules.camera.CameraModule;

import java.io.IOException;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class ControllerV1_CameraView
	extends CyborgController
	implements PermissionResultListener {

	@ViewIdentifier(viewId = R.id.CV_CameraLayer,
	                listeners = ViewListener.OnClick)
	private CameraLayer cameraLayer;
	private CameraModule cameraModule;

	@ViewIdentifier(viewId = R.id.TV_ToggleRecording,
	                listeners = ViewListener.OnClick)
	private TextView toggleRecording;

	@ViewIdentifier(viewId = R.id.TV_ToggleCamera,
	                listeners = ViewListener.OnClick)
	private TextView toggleCamera;

	public ControllerV1_CameraView() {
		super(R.layout.controller__camera_view);
	}

	@Override
	public void onResume() {
		super.onResume();
		getModule(PermissionModule.class).requestPermission(100, permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE, permission.RECORD_AUDIO);
		renderUI();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.TV_ToggleCamera:
				cameraLayer.switchToCamera(cameraLayer.getCameraId() == CameraInfo.CAMERA_FACING_BACK ? CameraInfo.CAMERA_FACING_FRONT : CameraInfo.CAMERA_FACING_BACK);
				break;
			case R.id.TV_ToggleRecording:
				if (cameraModule.isRecording()) {
					cameraModule.stopRecording();
					break;
				}

				try {
					String outputFile = Environment.getExternalStorageDirectory() + "/recorded-video/" + System.currentTimeMillis() + ".mp4";
					FileTools.createNewFile(outputFile);
					cameraModule.startRecording(outputFile);
				} catch (CameraException e) {
					logError(e);
				} catch (IOException e) {
					logError(e);
				}
				break;
		}
		renderUI();
		super.onClick(v);
	}

	@Override
	protected void render() {
		toggleCamera.setText("Switch to " + (cameraLayer.getCameraId() == CameraInfo.CAMERA_FACING_BACK ? "Front" : "Back") + " Camera");
		toggleRecording.setText((cameraModule.isRecording() ? "STOP" : "START") + " RECORDING");
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
	@SuppressLint("MissingPermission")
	public void onAllPermissionsGranted(int requestCode) {
		try {
			cameraLayer.startPreview(CameraInfo.CAMERA_FACING_BACK, 500, 500);
		} catch (CameraException e) {
			logError("Error starting preview", e);
		}
	}

	@Override
	protected void onPause() {
		if (cameraModule.isRecording()) {
			cameraModule.stopRecording();
		}

		if (!cameraLayer.isPreviewActive())
			return;

		cameraLayer.stopPreview();
	}
}
