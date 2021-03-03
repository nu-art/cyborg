/*
 * cyborg-core is an extendable  module based framework for Android.
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.modules;

import com.nu.art.core.generics.Processor;
import com.nu.art.modular.core.Module;

import static com.nu.art.modules.STT_Client.STTState.Idle;

public abstract class STT_Client
	extends Module {

	public enum STTState {
		Idle,
		Initializing,
		Initialized,
		Preparing,
		Prepared,
		Recognizing,
		Recognized,
		Cancelled,
		Terminating
	}

	public interface STT_Listener {

		void onPrepared();

		void onStopped();

		void onRecognized(String message);

		void onPartialResults(String partialResults);

		void onCancelled();
	}

	private STTState state = Idle;

	public STT_Client() {
		addKey(STT_Client.class);
	}

	@Override
	protected void init() {
	}

	public void setState(STTState state) {
		if (this.state == state)
			return;

		logInfo("State: " + this.state + " => " + state);
		this.state = state;
	}

	public final boolean isState(STTState state) {
		return this.state == state;
	}

	public abstract void startSTT();

	public abstract void stopSTT();

	public abstract boolean isRecognizingSpeech();

	public abstract void cancel();

	public abstract void setKeywords(String[] keywords);

	protected final void dispatchStopped() {
		dispatchModuleEvent("STT Stopped", STT_Listener.class, new Processor<STT_Listener>() {
			@Override
			public void process(STT_Listener listener) {
				listener.onStopped();
			}
		});
	}

	protected final void dispatchRecognized(final String finalText) {
		dispatchModuleEvent("STT Recognized: " + finalText, STT_Listener.class, new Processor<STT_Listener>() {
			@Override
			public void process(STT_Listener listener) {
				listener.onRecognized(finalText);
			}
		});
	}

	protected final void dispatchCancelled() {
		dispatchModuleEvent("STT Cancelled", STT_Listener.class, new Processor<STT_Listener>() {
			@Override
			public void process(STT_Listener listener) {
				listener.onCancelled();
			}
		});
	}

	protected final void dispatchPartialResults(final String partialResults) {
		dispatchModuleEvent("STT Partial Results: " + partialResults, STT_Listener.class, new Processor<STT_Listener>() {
			@Override
			public void process(STT_Listener listener) {
				listener.onPartialResults(partialResults);
			}
		});
	}

	protected final void dispatchPrepared() {
		dispatchModuleEvent("STT Prepared", STT_Listener.class, new Processor<STT_Listener>() {
			@Override
			public void process(STT_Listener listener) {
				listener.onPrepared();
			}
		});
	}
}