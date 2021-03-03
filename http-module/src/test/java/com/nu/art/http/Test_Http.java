package com.nu.art.http;

import com.nu.art.belog.BeLogged;
import com.nu.art.belog.loggers.JavaLogger;
import com.nu.art.http.Transaction_JSON.JsonHttpResponseListener;
import com.nu.art.modular.core.ModuleManagerBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static com.nu.art.belog.loggers.JavaLogger.Config_FastJavaLogger;
import static com.nu.art.http.consts.HttpMethod.Get;

public class Test_Http {

	public static class ResponseObj {

		public String ip;
	}

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		BeLogged.getInstance().setConfig(Config_FastJavaLogger);

		new ModuleManagerBuilder().addModules(HttpModule.class).build();
	}

	class Transaction_GetIP
		extends Transaction_JSON {

		void getIp(HttpResponseListener listener) {
			createRequest().setConnectTimeout(3000).setUrl(" http://ip.jsontest.com/").setMethod(Get).execute(listener);
		}
	}

	//	@Test
	public void test_httpGetRequest() {
		final AtomicReference<ResponseObj> ref = new AtomicReference<>();
		new Transaction_GetIP().getIp(new JsonHttpResponseListener<ResponseObj>() {
			@Override
			public void onSuccess(HttpResponse httpResponse, final ResponseObj responseObj) {
				ref.set(responseObj);
				synchronized (ref) {
					ref.notify();
				}
			}
		});
		synchronized (ref) {
			try {
				ref.wait(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ResponseObj responseObj = ref.get();
		if (responseObj == null)
			throw new RuntimeException("did not receive response");

		if (!responseObj.ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"))
			throw new RuntimeException("received corrupted ip: " + responseObj.ip);
	}
}
