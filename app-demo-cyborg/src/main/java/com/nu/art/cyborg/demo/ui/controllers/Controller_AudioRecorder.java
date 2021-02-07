package com.nu.art.cyborg.demo.ui.controllers;

import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.nu.art.core.interfaces.Getter;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgAdapter;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.dataModels.DataModel;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.media.CyborgAudioRecorder;
import com.nu.art.cyborg.media.CyborgAudioRecorder.AudioChannelType;
import com.nu.art.cyborg.media.CyborgAudioRecorder.AudioRecorderStateListener;
import com.nu.art.cyborg.media.CyborgAudioRecorder.AudioSourceType;
import com.nu.art.cyborg.media.CyborgAudioRecorder.EncodingType;
import com.nu.art.cyborg.media.CyborgAudioRecorder.SampleRateType;

import java.util.Arrays;

/**
 * Created by TacB0sS on 27/12/2017.
 */

public class Controller_AudioRecorder
	extends CyborgController
	implements AudioRecorderStateListener {

	@ViewIdentifier(viewId = R.id.SP_AudioSource,
	                listeners = ViewListener.OnItemSelected)
	private Spinner audioSource;

	@ViewIdentifier(viewId = R.id.SP_Channel,
	                listeners = ViewListener.OnItemSelected)
	private Spinner channel;

	@ViewIdentifier(viewId = R.id.SP_Encoding,
	                listeners = ViewListener.OnItemSelected)
	private Spinner encoding;

	@ViewIdentifier(viewId = R.id.SP_SampleRate,
	                listeners = ViewListener.OnItemSelected)
	private Spinner sampleRate;

	@ViewIdentifier(viewId = R.id.TV_Start,
	                listeners = ViewListener.OnClick)
	private TextView start;

	@ViewIdentifier(viewId = R.id.TV_Stop,
	                listeners = ViewListener.OnClick)
	private TextView stop;

	@ViewIdentifier(viewId = R.id.TV_State)
	private TextView state;

	private CyborgAudioRecorder audioRecorder;

	public Controller_AudioRecorder() {
		super(R.layout.controller__audio_recorder);
	}

	@Override
	protected void onCreate() {
		setAudioSpinner();
		setChannelSpinner();
		setEncodingSpinner();
		setSampleRateSpinner();
	}

	@Override
	public void onAudioRecorderStateChanged() {
		renderUI();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
			case R.id.TV_Start:
				audioRecorder.createBuilder() //
				             .setRecordingSource(getSource()) //
				             .setRecordingChannel(getChannel()) //
				             .setRecordingEncoding(getEncoding()) //
				             .setSampleRate(getSampleRate()).startRecording(); //

				break;
			case R.id.TV_Stop:
				audioRecorder.stopRecording();
				break;
		}
		renderUI();
	}

	@Override
	protected void render() {
		state.setText(audioRecorder.getState().name());
		//		start.setEnabled(audioRecorder.isState(AudioRecorderState.Idle));
		//		stop.setEnabled(!audioRecorder.isState(AudioRecorderState.Idle));
	}

	private void setAudioSpinner() {
		setSpinner(audioSource, Arrays.asList(AudioSourceType.values()).toArray());
	}

	private void setChannelSpinner() {
		setSpinner(channel, Arrays.asList(AudioChannelType.values()).toArray());
	}

	private void setEncodingSpinner() {
		setSpinner(encoding, Arrays.asList(EncodingType.values()).toArray());
	}

	private void setSampleRateSpinner() {
		setSpinner(sampleRate, Arrays.asList(SampleRateType.values()).toArray());
	}

	private void setSpinner(Spinner spinner, final Object[] values) {
		CyborgAdapter<Object> sourceAdapter = new CyborgAdapter<>(this, Renderer_SourceType.class);
		sourceAdapter.setResolver(new Getter<DataModel<Object>>() {
			ListDataModel<Object> model = new ListDataModel<>(Object.class);

			@Override
			public DataModel<Object> get() {
				model.clear();
				model.add(values);
				return model;
			}
		});

		spinner.setAdapter(sourceAdapter.getArrayAdapter());
	}

	private int getSampleRate() {
		return ((SampleRateType) sampleRate.getSelectedItem()).key;
	}

	private int getEncoding() {
		return ((EncodingType) encoding.getSelectedItem()).key;
	}

	private int getChannel() {
		return ((AudioChannelType) channel.getSelectedItem()).key;
	}

	private int getSource() {
		return ((AudioSourceType) audioSource.getSelectedItem()).key;
	}

	private class Renderer_SourceType
		extends ItemRenderer<Object> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView name;

		protected Renderer_SourceType() {
			super(R.layout.renderer__example);
		}

		@Override
		protected void renderItem(Object audioSourceType) {
			name.setText(audioSourceType.toString());
		}
	}
}
