package com.nu.art.http.headers;

import com.nu.art.http.HttpKeyValue;
import com.nu.art.http.interfaces.HeaderType;

/**
 * Created by TacB0sS on 08-Mar 2017.
 */
public enum ContentType
		implements HeaderType {
	JSON("application/json"),
	XML("application/xml"),
	TextPlain("text/plain"),
	TextHtml("text/html"),;

	static final String key = "content-type";

	public final HttpKeyValue header;

	ContentType(String value) {
		this.header = new HttpKeyValue(key, value);
	}

	@Override
	public HttpKeyValue getHeader() {
		return header;
	}
}
