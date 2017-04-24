/*
 * Copyright (c) 2016 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art
 *
 * Restricted usage under specific license
 *
 */

package com.nu.art.http;

public final class HttpKeyValue {

	String key;

	String value;

	public HttpKeyValue(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return key + ":" + value;
	}
}
