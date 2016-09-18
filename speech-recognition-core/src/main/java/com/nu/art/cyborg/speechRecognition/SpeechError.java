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

import android.speech.SpeechRecognizer;

public enum SpeechError {
	AudioError(SpeechRecognizer.ERROR_AUDIO),
	ClientError(SpeechRecognizer.ERROR_CLIENT),
	MissingPermissions(SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS),
	NetworkError(SpeechRecognizer.ERROR_NETWORK),
	MissingTimeout(SpeechRecognizer.ERROR_NETWORK_TIMEOUT),
	NoMatch(SpeechRecognizer.ERROR_NO_MATCH),
	Busy(SpeechRecognizer.ERROR_RECOGNIZER_BUSY),
	Server(SpeechRecognizer.ERROR_SERVER),
	SpeechTimeout(SpeechRecognizer.ERROR_SPEECH_TIMEOUT),
	Unknown(-1),;

	private final int errorCode;

	SpeechError(int errorCode) {
		this.errorCode = errorCode;
	}

	public static SpeechError getErrorByCode(int code) {
		for (SpeechError error : values()) {
			if (error.errorCode == code)
				return error;
		}
		return Unknown;
	}
}
