/*
 * Copyright (c) 2016 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art
 *
 * Restricted usage under specific license
 *
 */

package com.nu.art.http.consts;

public enum HttpMethod {
	Get("GET", false),
	Post("POST", true),
	Put("PUT", true),
	Patch("PATCH", true),
	Delete("DELETE", false),
	;

	public final String method;

	public final boolean hasBody;

	HttpMethod(String method, boolean hasBody) {
		this.method = method;
		this.hasBody = hasBody;
	}

}
