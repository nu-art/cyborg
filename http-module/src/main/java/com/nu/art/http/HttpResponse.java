package com.nu.art.http;

import com.nu.art.http.headers.ContentType;
import com.nu.art.http.headers.EncodingType;
import com.nu.art.http.interfaces.HeaderType;
import com.nu.art.core.interfaces.ILogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by TacB0sS on 08-Mar 2017.
 */

public class HttpResponse {

	public int responseCode;

	public int responseSize;

	public Throwable exception;

	public InputStream inputStream;

	Map<String, List<String>> headers;

	String responseAsString;

	public Map<String, List<String>> getHeaders() {
		if (headers == null)
			headers = new HashMap<>();

		return headers;
	}

	public List<String> getHeader(String key) {
		List<String> headers = getHeaders().get(key);
		if (headers == null)
			this.headers.put(key, headers = new ArrayList<>());

		return headers;
	}

	private boolean hasFailed() {
		return responseCode >= 300;
	}

	final void assertFailure(HttpURLConnection connection)
		throws IOException {
		if (!hasFailed())
			return;

		InputStream responseStream = connection.getErrorStream();
		if (responseStream != null) {
			inputStream = connection.getErrorStream();

			if (hasEncodingType(EncodingType.GZip))
				inputStream = new GZIPInputStream(inputStream);
		}

		throw new HttpException(this, "Got an error code (" + responseCode + ")");
	}

	@SuppressWarnings("unchecked")
	final void processSuccess(HttpURLConnection connection)
		throws IOException {
		inputStream = connection.getInputStream();

		if (inputStream == null)
			return;

		if (hasEncodingType(EncodingType.GZip))
			inputStream = new GZIPInputStream(inputStream);

		List<String> header = getHeader("content-length");
		if (header != null && header.size() == 1)
			responseSize = Integer.valueOf(header.get(0));
		else if (inputStream != null)
			responseSize = inputStream.available();
	}

	final void printResponse(ILogger logger) {
		logger.logInfo("+-- Response Code: " + responseCode);
		if (hasFailed()) {
			if (responseAsString != null)
				logger.logError("+-- Response: " + responseAsString);
			else if (responseSize > 0)
				logger.logVerbose("+-- Error Response Length: " + responseSize);
		} else {
			if (responseAsString != null)
				logger.logDebug("+-- Response: " + responseAsString);
			else if (responseSize > 0)
				logger.logVerbose("+-- Response Length: " + responseSize);
		}

		Map<String, List<String>> headers = getHeaders();
		if (headers.size() > 0)
			logger.logVerbose("+-- Response Headers: ");
		for (String key : headers.keySet()) {
			for (String value : headers.get(key)) {
				logger.logVerbose("+---- " + key + ": " + value);
			}
		}
	}

	/*
	 * Utility functions
	 */
	private boolean hasEncodingType(EncodingType encodingType) {
		return hasMatchingHeader(encodingType);
	}

	protected final boolean isContentType(ContentType header) {
		return hasMatchingHeader(header);
	}

	protected final boolean hasMatchingHeader(HeaderType header) {
		List<String> contentTypes = headers.get(header.getHeader().key);
		if (contentTypes == null || contentTypes.size() == 0)
			return false;

		for (String contentType : contentTypes) {
			if (!contentType.contains(header.getHeader().value))
				continue;

			return true;
		}

		return false;
	}

	final void close() {
		try {
			if (inputStream != null)
				inputStream.close();
		} catch (IOException ignore) {
		}
	}
}
