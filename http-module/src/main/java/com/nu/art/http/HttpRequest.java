/*
 * Copyright (c) 2016 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art
 *
 * Restricted usage under specific license
 *
 */

package com.nu.art.http;

import com.nu.art.belog.consts.LogLevel;
import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.generics.Processor;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.interfaces.ILogger;
import com.nu.art.http.HttpModule.ExecutionPool;
import com.nu.art.http.HttpModule.HoopTiming;
import com.nu.art.http.consts.HttpMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public abstract class HttpRequest
	implements IHttpRequest {

	private String finalUrl;

	// Request
	private HttpMethod method = HttpMethod.Get;
	Processor<HttpRequest> preExecutionProcessor;
	ExecutionPool executionPool;
	String tag;
	String url;
	private String bodyAsString;
	private int connectionTimeout = 10000;
	private int readTimeout = 20000;
	private Vector<HttpKeyValue> urlParams = new Vector<>();
	boolean autoRedirect = true;
	Getter<InputStream> _inputStream;
	private Vector<HttpKeyValue> headers = new Vector<>();
	private int requestBodyLength;
	private SSLContext sslContext;
	LogLevel logLevel;

	HttpRequest() {
		addHeader("accept-encoding", "gzip");
	}

	public final IHttpRequest setExecutionPool(ExecutionPool executionPool) {
		this.executionPool = executionPool;
		return this;
	}

	public final IHttpRequest setPreExecutionProcessor(Processor<HttpRequest> preExecutionProcessor) {
		this.preExecutionProcessor = preExecutionProcessor;
		return this;
	}

	public final IHttpRequest setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
		return this;
	}

	public final IHttpRequest setUrl(String url) {
		this.url = url;
		return this;
	}

	public final IHttpRequest setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public final IHttpRequest addUrlPath(String path) {
		this.url += path;
		return this;
	}

	public final IHttpRequest addHeader(String key, String value) {
		HttpKeyValue header = new HttpKeyValue(key, value);
		headers.add(header);
		return this;
	}

	public final IHttpRequest addParameter(String key, String value) {
		HttpKeyValue parameter = new HttpKeyValue(key, value);
		if (urlParams.contains(parameter))
			throw new BadImplementationException("already have a parameter with key: " + key);

		urlParams.add(parameter);
		return this;
	}

	public final IHttpRequest setMethod(HttpMethod method) {
		this.method = method;
		return this;
	}

	public final IHttpRequest setBody(String body) {
		if (body == null)
			return this;

		this.bodyAsString = body;
		setBody(() -> new ByteArrayInputStream(body.getBytes()));
		return this;
	}

	@Override
	public IHttpRequest setSSLContext(SSLContext sslContext) {
		this.sslContext = sslContext;
		return this;
	}

	public final IHttpRequest setConnectTimeout(int connectedTimeout) {
		this.connectionTimeout = connectedTimeout;
		return this;
	}

	public final IHttpRequest setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public IHttpRequest setBody(Getter<InputStream> bodyAsInputStream) {
		this._inputStream = bodyAsInputStream;
		return this;
	}

	public IHttpRequest followRedirect(boolean followRedirect) {
		this.autoRedirect = followRedirect;
		return this;
	}

	/*
	 *
	 *
	 *
	 *
	 */

	public String getBodyAsString() {
		return bodyAsString;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	private HttpKeyValue[] getParameters() {
		Vector<HttpKeyValue> allParameters = new Vector<>();
		allParameters.addAll(urlParams);
		return allParameters.toArray(new HttpKeyValue[allParameters.size()]);
	}

	final String composeURL()
		throws IOException {
		String urlPath = url;
		HttpKeyValue[] parameters = getParameters();
		StringBuilder params = new StringBuilder();
		if (parameters.length > 0)
			params.append(urlPath.contains("?") ? "&" : "?");

		for (int i = 0; i < parameters.length; i++) {
			HttpKeyValue parameter = parameters[i];
			params.append(parameter.key).append("=").append(URLEncoder.encode(parameter.value, "utf-8"));
			if (i < parameters.length - 1)
				params.append("&");
		}
		urlPath += params;

		return finalUrl = urlPath;
	}

	final HttpURLConnection connect(URL url, InputStream inputStream)
		throws IOException {

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (sslContext != null && connection instanceof HttpsURLConnection)
			((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());

		connection.setRequestMethod(method.method);
		connection.setInstanceFollowRedirects(autoRedirect);
		connection.setConnectTimeout(connectionTimeout);
		connection.setReadTimeout(readTimeout);
		connection.setDoOutput(inputStream != null);
		connection.setUseCaches(false);

		if (inputStream != null)
			connection.setFixedLengthStreamingMode(requestBodyLength = inputStream.available());

		for (HttpKeyValue header : headers) {
			if (header.value == null)
				continue;

			connection.addRequestProperty(header.key, header.value);
		}

		connection.connect();
		return connection;
	}

	final void printRequest(ILogger logger, HoopTiming hoop) {
		logger.logDebug("+----------------------------- HTTP REQUEST ------------------------------+");
		logger.logInfo("+-- URL(" + hoop.hoopIndex + "): " + method + " - " + finalUrl);
		logger.logVerbose("+-- Connection-Timeout: " + connectionTimeout);

		if (sslContext != null)
			logger.logVerbose("+-- SSL-Context: " + sslContext);

		if (urlParams.size() > 0)
			logger.logDebug("+-- Request Params: ");
		for (HttpKeyValue param : urlParams) {
			logger.logDebug("+----  " + param.key + ": " + param.value);
		}

		if (headers.size() > 0)
			logger.logVerbose("+-- Request Headers: ");
		for (HttpKeyValue header : headers) {
			logger.logVerbose("+----  " + header.key + ": " + header.value);
		}

		if (bodyAsString != null) {
			logger.logVerbose("+-- Request Body (" + bodyAsString.getBytes().length + "): ");
			logger.logDebug("+-- Request Body: " + bodyAsString);
		} else if (requestBodyLength > 0)
			logger.logVerbose("+-- Body Length: " + requestBodyLength);
	}

	final void close(InputStream inputStream) {
		try {
			if (inputStream != null)
				inputStream.close();
		} catch (IOException ignore) {
		}
	}
}