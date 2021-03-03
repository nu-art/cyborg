/*
 * Copyright (c) 2016 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art
 *
 * Restricted usage under specific license
 *
 */

package com.nu.art.http;

import com.nu.art.belog.BeLogged;
import com.nu.art.belog.Logger;
import com.nu.art.belog.consts.LogLevel;
import com.nu.art.core.generics.Processor;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.interfaces.ILogger;
import com.nu.art.core.tools.ArrayTools;
import com.nu.art.core.tools.StreamTools;
import com.nu.art.core.utils.PoolQueue;
import com.nu.art.modular.core.Module;
import com.nu.art.modular.core.ModuleManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@SuppressWarnings( {
	                   "unused",
	                   "WeakerAccess"
                   })
public final class HttpModule
	extends Module {

	public interface OnRequestErrorListener {

		void onError(HttpTransaction item, Throwable e);
	}

	public static class ExecutionPool {

		public String key;
		public int numberOfThreads;
		private Processor<Thread> threadInitiator;

		public ExecutionPool(String key, int numberOfThreads) {
			this.key = key;
			this.numberOfThreads = numberOfThreads;
		}

		public ExecutionPool setThreadInitiator(Processor<Thread> threadInitiator) {
			this.threadInitiator = threadInitiator;
			return this;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;

			if (o == null || getClass() != o.getClass())
				return false;

			ExecutionPool that = (ExecutionPool) o;
			return Objects.equals(key, that.key);
		}

		@Override
		public int hashCode() {
			return Objects.hash(key);
		}
	}

	public ExecutionPool DefaultExecutionPool = new ExecutionPool("http-thread", 5);
	public static final HttpResponseListener EmptyResponseListener = new EmptyResponseListener();
	private LogLevel defaultLogLevel = LogLevel.Verbose;

	/**
	 * PoolQueue holding the requests to be executed by its thread pool
	 */
	private HashMap<String, HttpPoolQueue> queues = new HashMap<>();
	private OnRequestErrorListener generalErrorListener;

	private HttpModule() { }

	public void disposeExecutionQueue(ExecutionPool pool) {
		logInfo("disposing execution pool: " + pool.key);
		HttpPoolQueue poolQueue = getOrCreateQueue(pool);
		queues.remove(pool.key);
		poolQueue.kill();
	}

	private HttpPoolQueue getOrCreateQueue(ExecutionPool executionPool) {
		if (executionPool == null)
			executionPool = DefaultExecutionPool;

		HttpPoolQueue queue = queues.get(executionPool.key);
		if (queue == null) {
			queues.put(executionPool.key, queue = (HttpPoolQueue) new HttpPoolQueue().setThreadInitiator(executionPool.threadInitiator));
			queue.createThreads(executionPool.key, executionPool.numberOfThreads);
		}
		return queue;
	}

	public void setGeneralErrorListener(OnRequestErrorListener generalErrorListener) {
		this.generalErrorListener = generalErrorListener;
	}

	public void setDefaultLogLevel(LogLevel defaultLogLevel) {
		this.defaultLogLevel = defaultLogLevel;
	}

	public void setDefaultExecutionPoolThreadCount(int threadCount) {
		this.DefaultExecutionPool.numberOfThreads = threadCount;
	}

	@Override
	protected void init() {}

	public final void trustAllCertificates() {
		logWarning("Very bad idea... calling this is a debug feature ONLY!!!");
		try {
			// Create a trust manager that does not validate certificate chains
			final TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
						// Workaround to silence the lint error... This is a debug feature only!
						int i = 0;
					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
						// Workaround to silence the lint error... This is a debug feature only!
						int i = 0;
					}

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				}
			};

			HostnameVerifier hostnameVerifier = new HostnameVerifier() {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					// Workaround to silence the lint error... This is a debug feature only!
					return hostname != null;
				}
			};

			setHostnameVerifier(hostnameVerifier);

			// Install the all-trusting trust manager
			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			// Create an ssl socket factory with our all-trusting manager
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public final void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	}

	public static abstract class BaseTransaction
		extends Transaction {

		public BaseTransaction() {
			ModuleManager.ModuleManager.getModule(HttpModule.class).super();
		}
	}

	private HashMap<String, Getter<String>> defaultHeaders = new HashMap<>();

	public void addDefaultHeader(String key, Getter<String> value) {
		defaultHeaders.put(key, value);
	}

	public void clearDefaultHeaders() {
		defaultHeaders.clear();
	}

	@SuppressWarnings("unused")
	private abstract class Transaction
		extends Logger {

		private class HoopTiming {

			HoopTiming redirectHoop;

			URL finalUrl;

			long connectionInterval;

			long uploadInterval;

			long waitForServerInterval;

			long downloadingInterval;

			long getTotalTime() {
				return getTotalHoopTime() + (redirectHoop == null ? 0 : redirectHoop.getTotalTime());
			}

			long getTotalHoopTime() {
				return connectionInterval + uploadInterval + waitForServerInterval + downloadingInterval;
			}
		}

		private HoopTiming hoop = new HoopTiming();

		protected IHttpRequest createRequest() {
			HttpRequestIn httpRequest = new HttpRequestIn();

			for (String key : defaultHeaders.keySet()) // If exist, add default headers to all requests.
				httpRequest.addHeader(key, defaultHeaders.get(key).get());

			return httpRequest;
		}

		protected final <Manager extends Module> Manager getModule(Class<Manager> moduleType) {
			return HttpModule.this.getModule(moduleType);
		}

		protected final <ListenerType> void dispatchModuleEvent(String message, Class<ListenerType> listenerType, Processor<ListenerType> processor) {
			HttpModule.this.dispatchModuleEvent(message, listenerType, processor);
		}

		protected final Throwable createException(HttpResponse httpResponse, String errorBody) {
			if (httpResponse.exception != null)
				return httpResponse.exception;

			return new HttpException(httpResponse, errorBody);
		}
	}

	public class HttpTransaction {

		private Logger logger = BeLogged.getInstance().getLogger(HttpModule.this);

		private HoopTiming hoop;
		private HttpRequest request;
		private HttpResponse response;
		private HttpResponseListener responseListener;

		private HttpTransaction(HttpRequest request, HttpResponseListener responseListener) {
			super();
			logger.setMinLogLevel(request.logLevel != null ? request.logLevel : defaultLogLevel);
			this.request = request;
			this.responseListener = responseListener;
		}

		@SuppressWarnings("unchecked")
		private boolean execute()
			throws IOException {
			if (request.preExecutionProcessor != null)
				request.preExecutionProcessor.process(request);

			HoopTiming originalHoop = hoop;
			hoop = new HoopTiming(originalHoop);
			hoop.redirectHoop = originalHoop;

			HttpURLConnection connection = null;
			boolean redirect = false;
			response = new HttpResponse();
			InputStream inputStream = null;
			try {
				inputStream = request._inputStream != null ? request._inputStream.get() : null;
				connection = connect(inputStream);
				request.printRequest(logger, hoop);
				postBody(connection, inputStream);

				waitForResponse(response, connection);

				if (processRedirect())
					return redirect = true;

				response.assertFailure(connection);

				processSuccess(connection);
			} catch (Throwable e) {
				if (connection == null)
					request.printRequest(logger, hoop);

				logger.logError("+-- Error: ", e);
				throw e;
			} finally {
				response.printResponse(logger);
				printTiming(logger, hoop, "");
				if (!redirect)
					logger.logVerbose("+-------------------------------------------------------------------------+");

				request.close(inputStream);
				response.close();

				if (connection != null)
					connection.disconnect();
			}
			return false;
		}

		private void processSuccess(HttpURLConnection connection)
			throws IOException {
			long start = System.currentTimeMillis();

			response.processSuccess(connection);
			responseListener.onSuccess(response);

			hoop.downloadingAndProcessingInterval = System.currentTimeMillis() - start;
		}

		final void waitForResponse(HttpResponse response, HttpURLConnection connection)
			throws IOException {
			long start = System.currentTimeMillis();

			response.responseCode = connection.getResponseCode();
			response.headers = new HashMap<>(connection.getHeaderFields());
			String[] keys = ArrayTools.asArray(response.headers.keySet(), String.class);

			for (String key : keys) {
				if (key == null)
					continue;

				List<String> value = response.headers.remove(key);
				List<String> olderValue = response.headers.put(key.toLowerCase(), value);
				if (olderValue != null)
					logger.logWarning("POTENTIAL BUG... SAME HEADER NAME DIFFERENT CASING FOR KEY: " + key);
			}

			hoop.waitForServerInterval = System.currentTimeMillis() - start;
		}

		final boolean processRedirect()
			throws IOException {
			if (!request.autoRedirect)
				return false;

			if (response.responseCode < 300 || response.responseCode >= 400)
				return false;

			List<String> locations = response.getHeader("location");
			if (locations.size() > 1)
				throw new IOException("redirect has ambiguous locations... cannot determine which!!");

			if (locations.size() == 0)
				return false;

			String location = locations.get(0);
			if (location.length() == 0)
				return false;

			request.url = location;
			return true;
		}

		private void printTiming(ILogger logger, HoopTiming hoop, String indentation) {
			logger.logVerbose("+--" + indentation + " Timing, Url: " + hoop.finalUrl.toString());
			logger.logVerbose("+--" + indentation + " Timing, Connection: " + hoop.connectionInterval);
			logger.logVerbose("+--" + indentation + " Timing, Uploading: " + hoop.uploadInterval);
			logger.logVerbose("+--" + indentation + " Timing, Waiting for response : " + hoop.waitForServerInterval);
			logger.logVerbose("+--" + indentation + " Timing, Downloading & Processing: " + hoop.downloadingAndProcessingInterval);
			logger.logVerbose("+--" + indentation + " Timing, Total Hoop: " + hoop.getTotalHoopTime());
		}

		final void postBody(HttpURLConnection connection, InputStream postStream)
			throws IOException {

			if (postStream == null)
				return;

			long start = System.currentTimeMillis();
			byte[] buffer = new byte[1024];
			int length;
			long cached = 0;
			int uploaded = 0;

			OutputStream outputStream = connection.getOutputStream();

			while ((length = postStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
				cached += length;
				uploaded += length;
				responseListener.onUploadProgress(uploaded, postStream.available());
				if (cached < 1024 * 1024)
					continue;

				outputStream.flush();
				cached = 0;
			}

			outputStream.flush();
			postStream.close();
			hoop.uploadInterval = System.currentTimeMillis() - start;

			//			outputStream.close();
		}

		private HttpURLConnection connect(InputStream inputStream)
			throws IOException {
			long start = System.currentTimeMillis();

			String urlPath = request.composeURL();
			URL url;
			try {
				url = new URL(urlPath);
			} catch (MalformedURLException e) {
				throw new IOException("error parsing url: " + urlPath, e);
			}

			HttpURLConnection connection = request.connect(hoop.finalUrl = url, inputStream);
			hoop.connectionInterval = System.currentTimeMillis() - start;
			return connection;
		}
	}

	private class HttpRequestIn
		extends HttpRequest {

		@Override
		public void execute(HttpResponseListener listener) {
			HttpPoolQueue queue = getOrCreateQueue(executionPool);
			queue.addItem(new HttpTransaction(this, listener));
		}

		/**
		 * To call this method you might be using a bad utility OR your architecture is flawed OR you don't know what you are doing OR you don't have a choice OR
		 * you
		 * are smarter then I have anticipated...
		 *
		 * Regardless I think this is a bad way to use a rest api client!
		 *
		 * @return The response input stream, <b>be sure to close it when you are done</b>!
		 */
		private Throwable error;
		private InputStream response;

		public InputStream executeSync()
			throws Throwable {
			executeAction(new HttpTransaction(this, new HttpResponseListener<InputStream, String>(InputStream.class, String.class) {
				@Override
				public void onSuccess(HttpResponse httpResponse, InputStream responseBody) {
					try {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						StreamTools.copy(responseBody, baos);
						response = new ByteArrayInputStream(baos.toByteArray());
					} catch (IOException e) {
						error = e;
					}
				}

				@Override
				public void onError(HttpResponse httpResponse, String errorBody) {
					error = new IOException(errorBody, httpResponse.exception);
				}
			}));

			if (error != null)
				throw error;

			return response;
		}
	}

	protected void executeAction(HttpTransaction transaction)
		throws IOException {
		while (transaction.execute())
			;
	}

	private class HttpPoolQueue
		extends PoolQueue<HttpTransaction> {

		@Override
		protected void onExecutionError(HttpTransaction item, Throwable e) {
			HttpResponse httpResponse = new HttpResponse();
			httpResponse.exception = e;
			try {
				item.responseListener.onError(httpResponse);
			} catch (Throwable e1) {
				logError("ERROR WHILE HANDLING AN ERROR:\nNot really sure what to do here....?", e1);
			}

			try {
				if (HttpModule.this.generalErrorListener != null)
					HttpModule.this.generalErrorListener.onError(item, e);
			} catch (Throwable e1) {
				logError("ERROR WHILE HANDLING AN ERROR:\nNot really sure what to do here....?", e1);
			}
		}

		@Override
		protected void executeAction(HttpTransaction transaction)
			throws IOException {
			HttpModule.this.executeAction(transaction);
		}
	}

	static class HoopTiming {

		final int hoopIndex;

		HoopTiming redirectHoop;

		URL finalUrl;

		long connectionInterval;

		long uploadInterval;

		long waitForServerInterval;

		long downloadingAndProcessingInterval;

		public HoopTiming() {
			this(null);
		}

		public HoopTiming(HoopTiming originalHoop) {
			this.redirectHoop = originalHoop;
			if (originalHoop != null)
				hoopIndex = originalHoop.hoopIndex + 1;
			else
				hoopIndex = 0;
		}

		long getTotalTime() {
			return getTotalHoopTime() + (redirectHoop == null ? 0 : redirectHoop.getTotalTime());
		}

		long getTotalHoopTime() {
			return connectionInterval + uploadInterval + waitForServerInterval + downloadingAndProcessingInterval;
		}
	}
}
