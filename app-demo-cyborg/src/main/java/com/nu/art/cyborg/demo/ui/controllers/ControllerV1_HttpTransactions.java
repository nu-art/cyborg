

package com.nu.art.cyborg.demo.ui.controllers;

import android.view.View;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.http.HttpResponse;
import com.nu.art.http.Transaction_JSON;

import static com.nu.art.http.consts.HttpMethod.Get;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class ControllerV1_HttpTransactions
	extends CyborgController {

	@ViewIdentifier(viewId = R.id.TV_Start,
	                listeners = ViewListener.OnClick)
	private TextView start;

	@ViewIdentifier(viewId = R.id.TV_Result)
	private TextView results;

	public static class ResponseObj {

		public String ip;
	}

	class Transaction_GetIP
		extends Transaction_JSON {

		void getIp() {
			createRequest().setUrl(" http://ip.jsontest.com/").setMethod(Get).execute(new JsonHttpResponseListener<ResponseObj>() {
				@Override
				public void onSuccess(HttpResponse httpResponse, final ResponseObj responseBody) {
					postOnUI(new Runnable() {
						@Override
						public void run() {
							results.setText(gson.toJson(responseBody));
						}
					});
				}
			});
		}
	}

	private Transaction_GetIP getIp = new Transaction_GetIP();

	public ControllerV1_HttpTransactions() {
		super(R.layout.controller__http_transactions);
	}

	@Override
	protected void onCreate() {
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.TV_Start:
				getIp.getIp();
				break;
		}
	}
}
