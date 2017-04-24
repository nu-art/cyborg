package com.nu.art.http;

import java.io.IOException;

/**
 * Created by TacB0sS on 02-Mar 2017.
 */
public class HttpException
		extends IOException {

	public HttpException() {
	}

	public HttpException(String s) {
		super(s);
	}

	public HttpException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public HttpException(Throwable throwable) {
		super(throwable);
	}
}
