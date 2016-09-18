/*
 * Copyright (c) 2017 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *
 * This software code is not an 'Open Source'!
 * In order to use this code you MUST have a proper license.
 * In order to obtain a licence please contact me directly.
 *
 * Email: Adam.Zehavi@Nu-Art-Software.com
 */

package com.nu.art.cyborg.speechRecognition;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.nu.art.software.core.constants.language.Language;
import com.nu.art.software.core.generics.Function;
import com.nu.art.software.core.generics.Processor;
import com.nu.art.software.core.tools.ArrayTools;
import com.nu.art.software.core.tools.DateTimeTools;
import com.nu.art.software.core.tools.MathTools;
import com.nu.art.software.cyborg.core.CyborgModule;

import java.util.ArrayList;
import java.util.Arrays;

import static com.nu.art.cyborg.speechRecognition.SpeechRecognitionState.Idle;
import static com.nu.art.cyborg.speechRecognition.SpeechRecognitionState.Prepared;
import static com.nu.art.cyborg.speechRecognition.SpeechRecognitionState.Preparing;
import static com.nu.art.cyborg.speechRecognition.SpeechRecognitionState.Recording;

/**
 * Sensiya - iAm+
 * Created by Shahar on 11-Jul 2016.
 */
public class SpeechRecognitionModule
		extends CyborgModule {

	public static final int StartRMSArrayLength = 50;

	public static final int RMS_InitialValue = 10;

	private Intent mRecognizerIntent;

	private SpeechRecognizer mSpeechRecognizer;

	private RecordingProcessor mRecordingProcessor;

	private SpeechListener listener;

	private Language[] supportedLanguages = {};

	@Override
	protected void init() {
		mRecordingProcessor = new RecordingProcessor();
		listener = new SpeechListener();
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication());
		mSpeechRecognizer.setRecognitionListener(listener);
		mRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		String language = "en";
		Function<Language, String> mapper = new Function<Language, String>() {
			@Override
			public String map(Language language) {
				return language.getLocale().toString();
			}
		};
		ArrayList<String> supportedLanguages = new ArrayList<>(Arrays.asList(ArrayTools.map(this.supportedLanguages, String.class, mapper)));
		mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
		mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
		mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, supportedLanguages);
		mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
		mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplication().getPackageName());
		mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
	}

	public void setSupportedLanguages(Language... supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	public void startRecording() {
		logInfo("startRecording");
		try {
			mSpeechRecognizer.startListening(mRecognizerIntent);
		} catch (Exception e) {
			e.printStackTrace();
			listener.onError(1);
		}
	}

	public void stopRecording() {
		if (!isRecording()) {
			logWarning("stopRecording called, but not recording...");
			return;
		}

		logInfo("stopRecording");
		mSpeechRecognizer.stopListening();
	}

	private void dispatchNoSpeechRecognized() {
		dispatchOnSpeechRecognized("");
	}

	public boolean isRecording() {
		return isState(Recording);
	}

	private void setState(SpeechRecognitionState newState) {
		mRecordingProcessor.setState(newState);
	}

	private boolean isState(SpeechRecognitionState state) {
		return mRecordingProcessor.isState(state);
	}

	private class SpeechListener
			implements RecognitionListener {

		@Override
		public void onReadyForSpeech(Bundle params) {
			if (!isState(Preparing)) {
				logWarning("onReadyForSpeech - But not in Idle state... doing nothing");
				return;
			}

			logInfo("onReadyForSpeech");
			setState(Prepared);
			startRecording();
		}

		@Override
		public void onBeginningOfSpeech() {
			logInfo("onBeginningOfSpeech");
			mRecordingProcessor.onSpeechRecognitionStarted();
		}

		@Override
		public void onRmsChanged(final float rmsdB) {
			if (isState(Idle)) {
				logWarning("onRmsChanged - will do nothing");
				return;
			}

			mRecordingProcessor.recognizeSilence(rmsdB);
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			logVerbose("onBufferReceived");
		}

		@Override
		public void onEndOfSpeech() {
			mRecordingProcessor.onSpeechRecognitionEnded();
		}

		@Override
		public void onError(final int error) {
			final SpeechError speechError = SpeechError.getErrorByCode(error);
			logError("onError: " + speechError);
			dispatchOnRecognitionError(speechError);
		}

		@Override
		public void onResults(Bundle results) {
			mRecordingProcessor.onResults(results);
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			String partialResultsText = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
			logInfo("onRecognizedPartialResults: '" + partialResultsText + "'");
			if (partialResultsText.length() == 0)
				return;

			dispatchOnRecognizedPartialResults(partialResultsText);
		}

		@Override
		public void onEvent(int eventType, Bundle params) {
			logInfo("onEvent: " + eventType);
		}
	}

	private class RecordingProcessor {

		private float[] rms = new float[StartRMSArrayLength];

		private int index = 0;

		private float previousRms;

		private long recordingDuration;

		private boolean silenceMode;

		private SpeechRecognitionState state = Idle;

		private boolean isState(SpeechRecognitionState state) {return this.state == state;}

		private void setState(SpeechRecognitionState newState) {
			if (state == newState)
				return;

			logDebug("STATE: " + state + " ==> " + newState);
			state = newState;
		}

		private void onSpeechRecognitionStarted() {
			logInfo("onSpeechRecognitionStarted");
			silenceMode = false;
			setState(Recording);
			recordingDuration = System.currentTimeMillis();
			dispatchOnRecognitionStarted();
		}

		private void onSpeechRecognitionEnded() {
			recordingDuration = System.currentTimeMillis() - recordingDuration;
			logInfo("onSpeechRecognitionEnded: (" + DateTimeTools.getDurationAsString("ss:ms", recordingDuration) + ")");
			setState(Idle);
			dispatchOnRecognitionStopped();
		}

		private void resetRmsArrayForDuration(float duration) {
			int length = (int) (duration / 1000 * 20);
			if (rms.length != length)
				rms = new float[length];

			for (int i = 0; i < rms.length; i++) {
				rms[i] = RMS_InitialValue;
			}
			previousRms = RMS_InitialValue;
		}

		void recognizeSilence(float rmsdB) {
			rms[index >= rms.length ? index = 0 : index++] = rmsdB;
			float avgRms = MathTools.calcAverage(rms);
			if (avgRms - previousRms > 0.2 && !silenceMode) {
				silenceMode = true;
			}
			previousRms = avgRms;

			logVerbose("onRmsChanged: " + rmsdB + " -Avg(" + rms.length + "): " + avgRms);

			if (avgRms > 0)
				return;

			logInfo("onSilenceRecognized");
			stopRecording();
			onSpeechRecognitionEnded();
		}

		private void onResults(Bundle results) {
			ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			if (matches == null || matches.isEmpty()) {
				logWarning("So yeah --- onResult with no result!!");
				dispatchNoSpeechRecognized();
				return;
			}

			String text = matches.get(0);
			logInfo("onResults == > text received: " + text);
			dispatchOnSpeechRecognized(text);
		}

		private void logInfo(String log) {
			logInfo("State: " + state + " - " + log);
		}

		private void logWarning(String log) {
			logWarning("State: " + state + " - " + log);
		}

		private void logError(String log) {
			logError("State: " + state + " - " + log);
		}
	}

	private void dispatchOnSpeechRecognized(final String text) {
		dispatchEvent(SpeechRecognitionListener.class, new Processor<SpeechRecognitionListener>() {
			@Override
			public void process(SpeechRecognitionListener listener) {
				listener.onSpeechRecognized(text);
			}
		});
	}

	private void dispatchOnRecognizedPartialResults(final String text) {
		dispatchEvent(SpeechRecognitionListener.class, new Processor<SpeechRecognitionListener>() {
			@Override
			public void process(SpeechRecognitionListener listener) {
				listener.onRecognizedPartialResults(text);
			}
		});
	}

	private void dispatchOnRecognitionStarted() {
		dispatchEvent(SpeechRecognitionListener.class, new Processor<SpeechRecognitionListener>() {
			@Override
			public void process(SpeechRecognitionListener listener) {
				listener.onSpeechRecognitionStarted();
			}
		});
	}

	private void dispatchOnRecognitionStopped() {
		dispatchEvent(SpeechRecognitionListener.class, new Processor<SpeechRecognitionListener>() {
			@Override
			public void process(SpeechRecognitionListener listener) {
				listener.onSpeechRecognitionStopped();
			}
		});
	}

	private void dispatchOnRecognitionError(final SpeechError error) {
		dispatchEvent(SpeechRecognitionListener.class, new Processor<SpeechRecognitionListener>() {
			@Override
			public void process(SpeechRecognitionListener listener) {
				listener.onSpeechRecognitionError(error);
			}
		});
	}
}

