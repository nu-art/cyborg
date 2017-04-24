package com.nu.art.http;

import com.nu.art.http.headers.ContentType;
import com.nu.art.http.headers.EncodingType;
import com.nu.art.http.interfaces.HeaderType;
import com.nu.art.software.core.interfaces.ILogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by TacB0sS on 08-Mar 2017.
 */

public class HttpResponse {

	int responseCode;

	Throwable exception;

	Map<String, List<String>> headers;

	InputStream inputStream;

	String responseAsString;

	public Map<String, List<String>> getHeaders() {
		if (headers == null)
			headers = new HashMap<>();

		return headers;
	}

	List<String> getHeader(String key) {
		return headers.get(key);
	}

	private boolean hasFailed() {
		return responseCode >= 300;
	}

	final boolean processFailure(HttpURLConnection connection)
			throws IOException {
		if (!hasFailed())
			return false;

		InputStream responseStream = connection.getErrorStream();
		if (responseStream == null) {
			throw new HttpException("Response code: " + responseCode + ", Got an error response without Error Body");
		}

		inputStream = connection.getErrorStream();

		if (hasEncodingType(EncodingType.GZip))
			inputStream = new GZIPInputStream(inputStream);

		return true;
	}

	@SuppressWarnings("unchecked")
	final InputStream processSuccess(HttpURLConnection connection)
			throws IOException {
		inputStream = connection.getInputStream();

		if (inputStream == null)
			return null;

		if (hasEncodingType(EncodingType.GZip))
			inputStream = new GZIPInputStream(inputStream);

		return inputStream;
	}

	final void printResponse(ILogger logger) {
		try {
			logger.logVerbose("+---- Response Headers: ");
			for (String key : headers.keySet()) {
				for (String value : headers.get(key)) {
					logger.logVerbose("+------- " + key + ": " + value);
				}
			}

			logger.logDebug("+---- Response Code: " + responseCode);
			if (hasFailed()) {
				if (responseAsString != null)
					logger.logError("+---- Response: " + responseAsString);
				else if (inputStream != null && inputStream.available() > 0)
					logger.logError("+---- Error Response Length: " + inputStream.available());
			} else {
				if (responseAsString != null)
					logger.logVerbose("+---- Response: " + responseAsString);
				else if (inputStream != null && inputStream.available() > 0)
					logger.logVerbose("+---- Error Response Length: " + inputStream.available());
			}
		} catch (IOException e) {
			logger.logError("Error printing response", e);
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
