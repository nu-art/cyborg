package com.nu.art.cyborg.stt;

import android.os.Handler;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.SpeechContext;
import com.google.cloud.speech.v1.SpeechContext.Builder;
import com.google.cloud.speech.v1.SpeechGrpc;
import com.google.cloud.speech.v1.SpeechGrpc.SpeechStub;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.cyborg.core.ActivityStack.ActivityStackAction;
import com.nu.art.cyborg.core.CyborgActivityBridge;
import com.nu.art.cyborg.core.modules.ThreadsModule;
import com.nu.art.cyborg.inProgress.audio.STT_Client;
import com.nu.art.cyborg.media.CyborgAudioRecorder;
import com.nu.art.cyborg.media.CyborgAudioRecorder.AudioBufferProcessor;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.StreamObserver;

import static com.nu.art.cyborg.stt.STT_Google.State.Idle;
import static com.nu.art.cyborg.stt.STT_Google.State.Preparing;
import static com.nu.art.cyborg.stt.STT_Google.State.Recording;

public class STT_Google
		extends STT_Client {

	private static final String HOSTNAME = "speech.googleapis.com";
	private static final int PORT = 443;
	private static final int SampleRate = 16000;
	private Builder speechContext;

	enum State {
		Idle,
		Preparing,
		Recording
	}

	private State state = Idle;

	private ManagedChannel channel;
	private SpeechStub speechClient;
	private boolean isRecording;
	private Handler handler;

	private Runnable initializeRecognizerClient = new Runnable() {
		@Override
		public void run() {
			initializeRecognizerImpl();
		}
	};
	private Runnable terminateRecognizerClient = new Runnable() {
		@Override
		public void run() {
			shutdownImpl();
		}
	};

	private StreamObserver<StreamingRecognizeRequest> requestObserver;
	private SpeechResponseObserver responseObserver;
	private AudioRecorderProcessor audioRecorderProcessor = new AudioRecorderProcessor();
	private Getter<InputStream> credentialsGetter;

	public void setCredentialsGetter(Getter<InputStream> credentialsGetter) {
		this.credentialsGetter = credentialsGetter;
	}

	public void setKeywords(String[] contextKeywords) {
		speechContext = SpeechContext.newBuilder().addAllPhrases(Arrays.asList(contextKeywords));
	}

	@Override
	protected void init() {
		responseObserver = new SpeechResponseObserver();
		handler = getModule(ThreadsModule.class).getDefaultHandler(getClass().getSimpleName());
		initializeRecognizer();
	}

	public void setState(State state) {
		if (this.state == state)
			return;

		logInfo("State: " + this.state + " => " + state);
		this.state = state;
	}

	private void initializeRecognizer() {
		handler.post(initializeRecognizerClient);
	}

	private void terminateRecognizer() {
		handler.post(terminateRecognizerClient);
	}

	private void initializeRecognizerImpl() {
		try {
			ProviderInstaller.installIfNeeded(getApplicationContext());
		} catch (final GooglePlayServicesRepairableException e) {
			logWarning("Google Play services is out of date -- Try to recover: ", e);
			postActivityAction(new ActivityStackAction() {
				@Override
				public void execute(CyborgActivityBridge activity) {
					GooglePlayServicesUtil.showErrorNotification(e.getConnectionStatusCode(), getApplicationContext());
				}
			});
			return;
		} catch (GooglePlayServicesNotAvailableException e) {
			toastDebug("No play service and cannot recover from this!");
			return;
		}

		try {
			logInfo("Initializing...");
			InputStream credentialsStream = credentialsGetter.get();
			GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
			credentialsStream.close();

			channel = ManagedChannelBuilder.forAddress(HOSTNAME, PORT).build();
			speechClient = SpeechGrpc.newStub(channel).withCallCredentials(MoreCallCredentials.from(credentials));
			logInfo("Initialized!");
		} catch (Throwable e) {
			logError("Failed to connect to Speech Server", e);
		}
	}

	private void shutdownImpl() {
		try {
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void startSTT() {
		isRecording = true;
		getModule(CyborgAudioRecorder.class).addListener(audioRecorderProcessor);
		logInfo("Start recognizer");
	}

	public void stopSTT() {
		isRecording = false;
		audioRecorderProcessor.dispose();
		getModule(CyborgAudioRecorder.class).removeListener(audioRecorderProcessor);
		logInfo("Stop recognizer");
	}

	public boolean isRecognizingSpeech() {
		return isRecording;
	}

	public void cancel() {
		responseObserver.cancel();
		//stop speech analyzing

		stopSTT();
	}

	private class SpeechResponseObserver
			implements StreamObserver<StreamingRecognizeResponse> {

		String transcript;
		boolean completed = false;
		boolean canceled = false;
		boolean started;

		@Override
		public void onNext(StreamingRecognizeResponse response) {
			if (response.getResultsCount() == 0)
				return;

			if (!response.getResults(0).getIsFinal() && !started) {
				started = true;
				dispatchStarted();
			} else if (response.getResults(0).getIsFinal() && started) {
				stopSTT();
				// stop speech analyzing
				// dispatch speech recognition.
				return;
			}

			if (response.getResultsCount() == 0)
				return;

			StreamingRecognitionResult results = response.getResults(0);
			String resultTranscript = results.getAlternatives(0).getTranscript();

			if (response.getResultsCount() == 1) {
				if (resultTranscript.length() > 0)
					setTranscript(resultTranscript);

				if (!results.getIsFinal())
					dispatchPartialResults(transcript);
				return;
			}

			StreamingRecognitionResult postfix = response.getResults(1);
			String postfixTranscript = postfix.getAlternatives(0).getTranscript();

			String partialResults = resultTranscript.trim() + " " + postfixTranscript.trim();

			setTranscript(partialResults);

			dispatchPartialResults(transcript);
		}

		private void setTranscript(String resultTranscript) {
			this.transcript = resultTranscript.trim().replace("  ", " ");
		}

		@Override
		public void onError(Throwable error) {
			Status status = Status.fromThrowable(error);
			logError("Recognition Error(" + status + ") : ", error);

			stopSTT();

			dispatchStopped();
		}

		@Override
		public void onCompleted() {
			if (completed)
				return;

			completed = true;

			if (canceled) {
				dispatchCancelled();
			} else if (transcript == null)
				dispatchRecognized("");
			else
				dispatchRecognized(transcript);

			dispatchStopped();
			logInfo("Recognition completed.");
		}

		public void cancel() {
			// ignore input if exists
			canceled = true;
		}

		private void prepare() {
			completed = false;
			canceled = false;
			transcript = null;
		}
	}

	private class AudioRecorderProcessor
			implements AudioBufferProcessor {

		private State state = Idle;

		private synchronized void setState(State state) {
			this.state = state;
		}

		private synchronized boolean isState(State state) {
			return this.state == state;
		}

		private void prepare() {
			handler.post(new Runnable() {

				@Override
				public void run() {
					try {
						setState(Preparing);
						logInfo("Preparing... 0");
						responseObserver.prepare();

						logInfo("Preparing... 1");
						long started = System.currentTimeMillis();
						requestObserver = speechClient.streamingRecognize(responseObserver);

						RecognitionConfig.Builder builder = RecognitionConfig.newBuilder();
						builder.setEncoding(AudioEncoding.LINEAR16);
						builder.setSampleRateHertz(SampleRate);
						builder.setMaxAlternatives(2);
						builder.setLanguageCode("en-US");
						if (speechContext != null)
							builder.setSpeechContexts(0, speechContext);
						StreamingRecognitionConfig streamingConfig = StreamingRecognitionConfig.newBuilder()
																																									 .setConfig(builder.build())
																																									 .setInterimResults(true)
																																									 .setSingleUtterance(true)
																																									 .build();

						logInfo("Preparing... 2");
						StreamingRecognizeRequest initial = StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamingConfig).build();
						requestObserver.onNext(initial);

						logInfo("Prepared!: " + (System.currentTimeMillis() - started) + "ms");
						setState(Recording);
						dispatchStarted();
					} catch (Exception e) {
						logError("Error preparing recognizer", e);
						state = Idle;
						dispose();
					}
				}
			});
		}

		@Override
		public void process(ArrayList<ByteBuffer> buffer, int byteRead, int sampleRate) {
			if (isState(Idle)) {
				prepare();
				return;
			}

			if (isState(Preparing))
				return;

			long processing = System.currentTimeMillis();

			for (ByteBuffer byteBuffer : buffer) {
				try {
					StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
																																			 .setAudioContent(ByteString.copyFrom(byteBuffer.array(), 0, byteRead))
																																			 .build();

					if (requestObserver != null)
						requestObserver.onNext(request);
				} catch (Exception e) {
					if (requestObserver != null)
						requestObserver.onError(e);
				}
				if (isDebug())
					logDebug("Check buffer... (" + (System.currentTimeMillis() - processing) + "ms)");
			}

			getModule(CyborgAudioRecorder.class).setBuffering(false);
		}

		public void dispose() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (isState(Idle))
						return;

					logInfo("Disposing... ");

					getModule(CyborgAudioRecorder.class).setBuffering(false);
					state = Idle;
					//					logWarning("", new WhoCalledThisException("dispose Google speech recognizer"));
					if (requestObserver == null)
						return;

					try {
						logInfo("requestObserver.onCompleted()");
						requestObserver.onCompleted();
					} catch (Throwable t) {
						logError("Error while calling onCompleted on the StreamObserver: ", t);
					}
				}
			});
		}
	}
}
