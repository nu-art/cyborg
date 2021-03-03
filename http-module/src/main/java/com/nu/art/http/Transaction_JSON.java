package com.nu.art.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nu.art.http.HttpModule.BaseTransaction;

/**
 * Created by TacB0sS on 16-Sep 2017.
 */

public class Transaction_JSON
	extends BaseTransaction {

	public static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	@Override
	protected IHttpRequest createRequest() {
		return super.createRequest().addHeader("Content-Type", "application/json");
	}

	protected IHttpRequest createPlainRequest() {
		return super.createRequest();
	}

	public static abstract class JsonHttpResponseListener<Type>
		extends HttpResponseListener<Type, String> {

		protected JsonHttpResponseListener() {
			super();
		}

		protected JsonHttpResponseListener(Class<Type> responseType) {
			super(responseType, String.class);
		}

		@Override
		protected <Type> Type deserialize(Class<Type> type, String responseAsString) {
			return gson.fromJson(responseAsString, type);
		}

		@Override
		public void onError(HttpResponse httpResponse, String errorAsString) {

		}
	}
}
