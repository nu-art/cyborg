package com.nu.art.http;

import java.io.InputStream;

final class EmptyResponseListener
		extends HttpResponseListener<InputStream, InputStream> {

	EmptyResponseListener() {
		super(InputStream.class, InputStream.class);
	}

	@Override
	public void onSuccess(HttpResponse httpResponse, InputStream responseBody) {

	}

	@Override
	public void onError(HttpResponse httpResponse, InputStream errorBody) {

	}
}
