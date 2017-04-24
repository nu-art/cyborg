/*
 * Copyright (c) 2016 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art
 *
 * Restricted usage under specific license
 *
 */

package com.nu.art.http;

import com.nu.art.belogged.Logger;
import com.nu.art.software.core.generics.Processor;
import com.nu.art.software.core.interfaces.ILogger;
import com.nu.art.software.core.tools.ArrayTools;
import com.nu.art.software.core.utils.PoolQueue;
import com.nu.art.software.modular.core.Module;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@SuppressWarnings( {"unused", "WeakerAccess"
				   })
public final class HttpModule
		extends Module {
	//	private Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

	public static final HttpResponseListener EmptyResponseListener = new EmptyResponseListener();

	private static final int ThreadCount = 5;

	private int threadCount = ThreadCount;

	/**
	 * PoolQueue holding the requests to be executed by its thread pool
	 */
	private HttpPoolQueue httpAsyncQueue = new HttpPoolQueue();

	private HttpModule() { }

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	@Override
	protected void init() {
		httpAsyncQueue.createThreads("Http Thread Pool", threadCount);
	}

	public final void trustAllCertificates() {
		logWarning("Very bad idea... calling this is a debug feature ONLY!!!");
		try {
			// Create a trust manager that does not validate certificate chains
			final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
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

	@SuppressWarnings("unused")
	public abstract class Transaction
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
			return new HttpRequestIn();
		}

		protected final <Manager extends Module> Manager getModule(Class<Manager> moduleType) {
			return HttpModule.this.getModule(moduleType);
		}

		protected final <ListenerType> void dispatchModuleEvent(String message, Class<ListenerType> listenerType, Processor<ListenerType> processor) {
			HttpModule.this.dispatchModuleEvent(message, listenerType, processor);
		}
	}

	private class HttpTransaction {

		private HttpRequest request;

		private HttpResponse response;

		private HttpResponseListener responseListener;

		private HoopTiming hoop = new HoopTiming();

		private HttpTransaction(HttpRequest request, HttpResponseListener responseListener) {
			super();
			this.request = request;
			this.responseListener = responseListener;
		}

		@SuppressWarnings("unchecked")
		private void execute() {

			HttpURLConnection connection = null;
			try {
				connection = connect();
				request.printRequest(HttpModule.this);
				postBody(connection, request.inputStream);

				response = waitForResponse(connection);

				if (processRedirect()) {
					execute();
					return;
				}

				if (response.processFailure(connection)) {
					responseListener.onError(response);
					return;
				}

				response.processSuccess(connection);
				responseListener.onSuccess(response);
			} catch (Throwable e) {
				logError("+---- Error: ", e);
				responseListener.onError(e);
			} finally {
				response.printResponse(HttpModule.this);
				printTiming(HttpModule.this, hoop, "");
				logInfo("+-------------------------------------------------------------------------+");

				request.close();
				response.close();

				if (connection != null)
					connection.disconnect();
			}
		}

		final HttpResponse waitForResponse(HttpURLConnection connection)
				throws IOException {
			long start = System.currentTimeMillis();

			response.responseCode = connection.getResponseCode();
			response.headers = connection.getHeaderFields();
			String[] keys = ArrayTools.asArray(response.headers.keySet(), String.class);

			for (String key : keys) {
				List<String> value = response.headers.remove(key);
				response.headers.put(key.toLowerCase(), value);
			}

			hoop.waitForServerInterval = System.currentTimeMillis() - start;
			return null;
		}

		final boolean processRedirect()
				throws IOException {
			if (!request.autoRedirect)
				return false;

			if (response.responseCode < 300 || response.responseCode >= 400)
				return false;
			List<String> locations = response.getHeader("Location");
			if (locations.size() > 1)
				throw new IOException("redirect has ambiguous locations... cannot determine which!!");

			if (locations.size() == 0)
				return false;

			String location = locations.get(0);
			if (location.length() == 0)
				return false;

			HoopTiming originalHoop = hoop;
			hoop = new HoopTiming();
			hoop.redirectHoop = originalHoop;

			request.url = location;
			return true;
		}

		private void printTiming(ILogger logger, HoopTiming hoop, String indentation) {
			if (hoop.redirectHoop != null)
				printTiming(logger, hoop.redirectHoop, indentation + "-");

			logger.logDebug("+----" + indentation + " Timing, Url: " + hoop.finalUrl.toString());
			logger.logDebug("+----" + indentation + " Timing, Connection: " + hoop.connectionInterval);
			logger.logDebug("+----" + indentation + " Timing, Uploading: " + hoop.uploadInterval);
			logger.logDebug("+----" + indentation + " Timing, Waiting for response : " + hoop.waitForServerInterval);
			logger.logDebug("+----" + indentation + " Timing, Downloading: " + hoop.downloadingInterval);
			logger.logInfo("+----" + indentation + " Timing, Total Hoop: " + hoop.getTotalHoopTime());
		}

		final OutputStream postBody(HttpURLConnection connection, InputStream postStream)
				throws IOException {

			if (postStream == null)
				return null;

			long start = System.currentTimeMillis();
			byte[] buffer = new byte[1024];
			int length;
			long cached = 0;
			long uploaded = 0;

			OutputStream outputStream = connection.getOutputStream();
			while ((length = postStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
				cached += length;
				responseListener.onUploadProgress(uploaded, postStream.available());
				uploaded += length;
				if (cached < 1024 * 1024)
					continue;

				outputStream.flush();
				cached = 0;
			}

			outputStream.flush();
			postStream.close();
			hoop.uploadInterval = System.currentTimeMillis() - start;

			return outputStream;
		}

		private HttpURLConnection connect()
				throws IOException {
			long start = System.currentTimeMillis();
			HttpURLConnection connection = request.connect(hoop.finalUrl = request.composeURL());
			hoop.connectionInterval = System.currentTimeMillis() - start;
			return connection;
		}
	}

	private class HttpRequestIn
			extends HttpRequest {

		@Override
		public void execute(HttpResponseListener listener) {
			httpAsyncQueue.addItem(new HttpTransaction(this, listener));
		}
	}

	private class HttpPoolQueue
			extends PoolQueue<HttpTransaction> {

		@Override
		protected void onExecutionError(HttpTransaction item, Throwable e) {
			item.responseListener.onError(e);
		}

		@Override
		protected void executeAction(HttpTransaction transaction) {
			transaction.execute();
		}
	}

	private static class HoopTiming {

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
}
