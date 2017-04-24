/*
 * Copyright (c) 2016 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art
 *
 * Restricted usage under specific license
 *
 */

package com.nu.art.http;

import com.nu.art.http.consts.HttpMethod;

import java.io.InputStream;

@SuppressWarnings("WeakerAccess")
public interface IHttpRequest {

	IHttpRequest setUrl(String url);

	IHttpRequest addUrlPath(String path);

	IHttpRequest addHeader(String key, String value);

	IHttpRequest addParameter(String key, String value);

	IHttpRequest setMethod(HttpMethod method);

	IHttpRequest setBody(String body);

	IHttpRequest setConnectTimeout(int connectedTimeout);

	IHttpRequest setReadTimeout(int readTimeout);

	IHttpRequest setBody(InputStream bodyAsInputStream);

	IHttpRequest followRedirect(boolean followRedirect);

	void execute(HttpResponseListener listener);
}
