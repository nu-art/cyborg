package com.nu.art.http;

import java.io.IOException;

/**
 * Created by TacB0sS on 02-Mar 2017.
 */
public class HttpException
	extends IOException {

	public final HttpResponse response;

	public final Object error;

	public HttpException(HttpResponse response, Object error) {
		this.response = response;
		this.error = error;
	}

	@Override
	public String getMessage() {
		if (error instanceof String)
			return (String) error;

		return error.toString();
	}
}
