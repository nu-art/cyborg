/*
 * Copyright (c) 2016 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art
 *
 * Restricted usage under specific license
 *
 */

package com.nu.art.http;

import com.nu.art.core.exceptions.runtime.ImplementationMissingException;
import com.nu.art.core.file.Charsets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.nu.art.core.generics.GenericParamExtractor._GenericParamExtractor;

@SuppressWarnings("WeakerAccess")
public abstract class HttpResponseListener<ResponseType, ErrorType> {

	protected final Class<ResponseType> responseType;

	protected final Class<ErrorType> errorType;

	protected HttpResponseListener() {
		responseType = _GenericParamExtractor.extractGenericType(HttpResponseListener.class, this, 0);
		errorType = _GenericParamExtractor.extractGenericType(HttpResponseListener.class, this, 1);
	}

	protected HttpResponseListener(Class<ResponseType> responseType, Class<ErrorType> errorType) {
		this.responseType = responseType;
		this.errorType = errorType;
	}

	@SuppressWarnings("unchecked")
	final <Type> Type convertToType(Class<Type> type, HttpResponse response)
		throws IOException {
		InputStream inputStream = response.inputStream;
		if (InputStream.class.isAssignableFrom(type))
			return (Type) inputStream;

		byte[] buffer = new byte[1024];
		int downloaded = 0;
		int length;
		int available;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		List<String> header = response.getHeader("Content-Length");
		if (header.size() > 0)
			available = Integer.parseInt(header.get(0));
		else
			available = inputStream.available();

		while ((length = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, length);
			downloaded += length;
			onDownloadProgress(downloaded, available);
		}

		response.responseAsString = bos.toString(Charsets.UTF_8.encoding);
		if (type == String.class)
			return (Type) (response.responseAsString);

		return deserialize(type, response.responseAsString);
	}

	protected <Type> Type deserialize(Class<Type> type, String responseAsString) {
		throw new ImplementationMissingException("if you got here, you probably meant to override this method!");
	}

	public abstract void onSuccess(HttpResponse httpResponse, ResponseType responseBody);

	public abstract void onError(HttpResponse httpResponse, ErrorType errorBody);

	final void onSuccess(HttpResponse httpResponse)
		throws IOException {
		ResponseType responseBody = convertToType(responseType, httpResponse);
		onSuccess(httpResponse, responseBody);
	}

	final void onError(HttpResponse httpResponse)
		throws IOException {

		ErrorType errorBody = null;
		if (httpResponse.inputStream != null)
			errorBody = convertToType(errorType, httpResponse);

		if (httpResponse.exception == null)
			httpResponse.exception = new HttpException(httpResponse, errorBody);

		onError(httpResponse, errorBody);
	}

	protected void onUploadProgress(long uploaded, long available) {

	}

	protected void onDownloadProgress(long downloaded, long available) {

	}
}