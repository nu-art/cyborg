/*
 * Copyright (c) 2016 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art
 *
 * Restricted usage under specific license
 *
 */

package com.nu.art.http;

import com.nu.art.belog.consts.LogLevel;
import com.nu.art.core.generics.Processor;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.http.HttpModule.ExecutionPool;
import com.nu.art.http.consts.HttpMethod;

import java.io.InputStream;

import javax.net.ssl.SSLContext;

@SuppressWarnings("WeakerAccess")
public interface IHttpRequest {

	IHttpRequest setPreExecutionProcessor(Processor<HttpRequest> preExecutionProcessor);

	IHttpRequest setLogLevel(LogLevel logLevel);

	IHttpRequest setExecutionPool(ExecutionPool executionPool);

	IHttpRequest setUrl(String url);

	IHttpRequest addUrlPath(String path);

	IHttpRequest addHeader(String key, String value);

	IHttpRequest addParameter(String key, String value);

	IHttpRequest setMethod(HttpMethod method);

	IHttpRequest setBody(String body);

	IHttpRequest setConnectTimeout(int connectedTimeout);

	IHttpRequest setSSLContext(SSLContext sslContext);

	IHttpRequest setReadTimeout(int readTimeout);

	IHttpRequest setBody(Getter<InputStream> bodyAsInputStream);

	IHttpRequest followRedirect(boolean followRedirect);

	void execute(HttpResponseListener listener);

	InputStream executeSync()
		throws Throwable;
}
