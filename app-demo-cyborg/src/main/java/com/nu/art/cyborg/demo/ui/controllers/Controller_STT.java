

package com.nu.art.cyborg.demo.ui.controllers;

import android.Manifest.permission;
import android.view.View;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.media.CyborgAudioRecorder;
import com.nu.art.cyborg.media.CyborgAudioRecorder.AudioBufferProcessor;
import com.nu.art.cyborg.modules.PermissionModule;
import com.nu.art.cyborg.modules.PermissionModule.PermissionResultListener;
import com.nu.art.cyborg.stt.STT_Google;
import com.nu.art.cyborg.stt.STT_Google.AudioStreamer;
import com.nu.art.modules.STT_Client.STT_Listener;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class Controller_STT
	extends CyborgController
	implements STT_Listener, PermissionResultListener {

	STT_Google stt;
	CyborgAudioRecorder recorder;

	@ViewIdentifier(viewId = R.id.TV_Start,
	                listeners = ViewListener.OnClick)
	private TextView start;

	@ViewIdentifier(viewId = R.id.TV_Stop,
	                listeners = ViewListener.OnClick)
	private TextView stop;

	@ViewIdentifier(viewId = R.id.TV_PartialResults,
	                listeners = ViewListener.OnClick)
	private TextView results;

	public Controller_STT() {
		super(R.layout.controller__stt);
	}

	private AudioBufferProcessor listener = new AudioBufferProcessor() {
		@Override
		public void process(ArrayList<ByteBuffer> buffer, int byteRead, int sampleRate) {
			stt.processAudio(buffer, byteRead);
		}
	};

	@Override
	protected void onCreate() {
		stt.setStreamer(new AudioStreamer() {
			@Override
			public void startStreaming() {
				getModule(CyborgAudioRecorder.class).addListener(listener);
			}

			@Override
			public void stopStreaming() {
				getModule(CyborgAudioRecorder.class).removeListener(listener);
			}

			@Override
			public void setBuffering(boolean enabled) {
				getModule(CyborgAudioRecorder.class).setBuffering(false);
			}
		});
	}

	@Override
	protected void render() {
		super.render();
		start.setEnabled(!stt.isRecognizingSpeech());
		stop.setEnabled(stt.isRecognizingSpeech());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.TV_Start:
				getModule(PermissionModule.class).requestPermission(500, permission.RECORD_AUDIO);
				break;
			case R.id.TV_Stop:
				recorder.stopRecording();
				stt.stopSTT();
				break;
		}
	}

	@Override
	public void onPermissionsRejected(int requestCode, String[] rejected) {
		toastDebug("No All Permissions accepted");
	}

	@Override
	public void onAllPermissionsGranted(int requestCode) {
		recorder.createBuilder().startRecording();
		stt.startSTT();
	}

	@Override
	public void onPrepared() {
		renderUI();
	}

	@Override
	public void onStopped() {
		renderUI();
	}

	@Override
	public void onRecognized(String message) {
		results.setText(message);
		recorder.stopRecording();
		renderUI();
	}

	@Override
	public void onPartialResults(String partialResults) {
		results.setText(partialResults);
		renderUI();
	}

	@Override
	public void onCancelled() {
		renderUI();
	}
}
