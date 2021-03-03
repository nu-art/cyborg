package com.nu.art.http.headers;

import com.nu.art.http.HttpKeyValue;
import com.nu.art.http.interfaces.HeaderType;

/**
 * Created by TacB0sS on 08-Mar 2017.
 */
public enum EncodingType
	implements HeaderType {
	GZip("gzip"),
	//
	;

	static final String key = "content-encoding";

	public final HttpKeyValue header;

	EncodingType(String value) {
		this.header = new HttpKeyValue(key, value);
	}

	@Override
	public HttpKeyValue getHeader() {
		return header;
	}
}
