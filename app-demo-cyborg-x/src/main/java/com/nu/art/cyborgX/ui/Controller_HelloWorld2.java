package com.nu.art.cyborgX.ui;

import android.view.View;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborgX.R;

/**
 * Created by TacB0sS on 12-May 2017.
 */

public class Controller_HelloWorld2
	extends CyborgController {

	private static int index;

	// Inject view and set an OnLongClickListener and an OnClickListener.
	@ViewIdentifier(viewId = R.id.HelloWorld,
	                listeners = {
		                ViewListener.OnLongClick,
		                ViewListener.OnClick
	                })
	TextView helloWorldTextView;

	public Controller_HelloWorld2() {
		super(R.layout.controller__hello_world2);
	}

	@Override
	protected void onCreate() {

		helloWorldTextView.setText("Hello World " + index++);
		super.onCreate();
	}

	@Override
	public void onClick(View v) {
		String text = helloWorldTextView.getText() + "-";
		helloWorldTextView.setText(text);
		super.onClick(v);
	}

	@Override
	public boolean onLongClick(View v) {
		String text = helloWorldTextView.getText() + "+";
		helloWorldTextView.setText(text);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
