package com.nu.art.cyborg.stt;

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
import com.nu.art.core.utils.DebugFlags;
import com.nu.art.core.utils.DebugFlags.DebugFlag;
import com.nu.art.core.utils.JavaHandler;
import com.nu.art.modules.STT_Client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.StreamObserver;

import static com.nu.art.modules.STT_Client.STTState.Cancelled;
import static com.nu.art.modules.STT_Client.STTState.Idle;
import static com.nu.art.modules.STT_Client.STTState.Initialized;
import static com.nu.art.modules.STT_Client.STTState.Initializing;
import static com.nu.art.modules.STT_Client.STTState.Prepared;
import static com.nu.art.modules.STT_Client.STTState.Preparing;
import static com.nu.art.modules.STT_Client.STTState.Recognized;
import static com.nu.art.modules.STT_Client.STTState.Recognizing;

public class STT_Google
	extends STT_Client {

	public interface AudioStreamer {

		void startStreaming();

		void stopStreaming();

		void setBuffering(boolean enabled);
	}

	private static final String HOSTNAME = "speech.googleapis.com";
	private static final int PORT = 443;
	private static final int SampleRate = 16000;
	private Builder speechContext;

	private ManagedChannel channel;
	private SpeechStub speechClient;
	private boolean isRecording;
	private JavaHandler handler;

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
	private Getter<GoogleCredentials> credentialsGetter;
	private String locale = "en-US";
	private AudioStreamer streamer;

	public void setCredentialsGetter(Getter<GoogleCredentials> credentialsGetter) {
		this.credentialsGetter = credentialsGetter;
	}

	public void setKeywords(String[] contextKeywords) {
		speechContext = SpeechContext.newBuilder().addAllPhrases(Arrays.asList(contextKeywords));
	}

	public void setSTTLocale(String locale) {
		this.locale = locale;
	}

	public String getSTTLocale() {
		return locale;
	}

	public void setStreamer(AudioStreamer streamer) {
		this.streamer = streamer;
	}

	@Override
	protected void init() {
		responseObserver = new SpeechResponseObserver();
		handler = new JavaHandler().start(getClass().getSimpleName());
		initializeRecognizer();
	}

	private void initializeRecognizer() {
		handler.post(initializeRecognizerClient);
	}

	private void terminateRecognizer() {
		handler.post(terminateRecognizerClient);
	}

	private void initializeRecognizerImpl() {
		setState(Initializing);

		try {
			logInfo("Initializing...");

			GoogleCredentials credentials = credentialsGetter.get();

			channel = ManagedChannelBuilder.forAddress(HOSTNAME, PORT).build();
			speechClient = SpeechGrpc.newStub(channel).withCallCredentials(MoreCallCredentials.from(credentials));
			logInfo("Initialized!");
			setState(Initialized);
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
		setState(Initialized);
		streamer.startStreaming();
		logInfo("Start recognizer");
	}

	public void stopSTT() {
		isRecording = false;
		if (isState(Recognizing))
			responseObserver.cancel();

		audioRecorderProcessor.dispose();
		streamer.stopStreaming();
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
			int resultsCount = response.getResultsCount();
			if (resultsCount == 0)
				return;

			if (isState(Cancelled))
				return;

			StreamingRecognitionResult results = response.getResults(0);
			boolean isFinal = results.getIsFinal();
			String resultTranscript = results.getAlternatives(0).getTranscript();

			if (DebugFlag.isEnabled())
				logDebug("started: " + started + ", isFinal: " + isFinal + ", resultsCount: " + resultsCount + ", resultTranscript: " + resultTranscript);

			if (!isFinal && !started) {
				started = true;
				dispatchPrepared();
			} else if (isFinal && started) {
				setState(Recognized);
				stopSTT();
				return;
			}

			if (resultsCount == 1) {
				if (resultTranscript.length() > 0)
					setTranscript(resultTranscript);

				if (!isFinal)
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
				setState(Cancelled);
				dispatchCancelled();
			} else if (transcript == null)
				dispatchRecognized("");
			else {
				dispatchRecognized(transcript);
			}

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

	public void processAudio(ArrayList<ByteBuffer> buffer, int byteRead) {
		audioRecorderProcessor.process(buffer, byteRead);
	}

	private class AudioRecorderProcessor {

		private void prepare() {
			handler.post(new Runnable() {

				@Override
				public void run() {
					try {
						long started = System.currentTimeMillis();
						setState(Preparing);
						logInfo("Preparing...");
						responseObserver.prepare();

						requestObserver = speechClient.streamingRecognize(responseObserver);

						RecognitionConfig.Builder builder = RecognitionConfig.newBuilder();
						builder.setEncoding(AudioEncoding.LINEAR16);
						builder.setSampleRateHertz(SampleRate);
						builder.setMaxAlternatives(2);
						builder.setLanguageCode(locale);
						if (speechContext != null)
							builder.setSpeechContexts(0, speechContext);

						StreamingRecognitionConfig streamingConfig = StreamingRecognitionConfig.newBuilder()
						                                                                       .setConfig(builder.build())
						                                                                       .setInterimResults(true)
						                                                                       .setSingleUtterance(true)
						                                                                       .build();

						StreamingRecognizeRequest initial = StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamingConfig).build();
						requestObserver.onNext(initial);

						logInfo("Prepared!: " + (System.currentTimeMillis() - started) + "ms");
						setState(Prepared);
						dispatchPrepared();
					} catch (Exception e) {
						logError("Error preparing recognizer", e);
						dispose();
					}
				}
			});
		}

		public void process(ArrayList<ByteBuffer> buffer, int byteRead) {
			if (isState(Initialized)) {
				prepare();
				return;
			}

			if (isState(Preparing))
				return;

			if (isState(Cancelled))
				return;

			if (!isState(Recognizing))
				setState(Recognizing);

			long processing = System.currentTimeMillis();

			for (ByteBuffer byteBuffer : buffer) {
				try {
					ByteString audioContent = ByteString.copyFrom(byteBuffer.array(), 0, byteRead);
					StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder().setAudioContent(audioContent).build();

					if (requestObserver != null)
						requestObserver.onNext(request);
				} catch (Exception e) {
					if (requestObserver != null)
						requestObserver.onError(e);
				}

				if (DebugFlag.isEnabled())
					logDebug("Check buffer... (" + (System.currentTimeMillis() - processing) + "ms)");
			}

			streamer.setBuffering(false);
		}

		public void dispose() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (isState(Idle))
						return;

					logInfo("Disposing... ");

					streamer.setBuffering(false);
					if (responseObserver.canceled)
						setState(Cancelled);
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
