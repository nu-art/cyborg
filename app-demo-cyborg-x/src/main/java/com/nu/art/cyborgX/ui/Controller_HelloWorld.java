package com.nu.art.cyborgX.ui;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborgX.R;

/**
 * Created by TacB0sS on 12-May 2017.
 */

public class Controller_HelloWorld
	extends CyborgController {

	private static int index;
	private static Integer[] colors = new Integer[]{
		Color.RED,
		Color.BLUE,
		Color.GREEN
	};

	// Inject view and set an OnLongClickListener and an OnClickListener.
	@ViewIdentifier(viewId = R.id.HelloWorld,
	                listeners = {
		                ViewListener.OnLongClick,
		                ViewListener.OnClick
	                })
	TextView helloWorldTextView;

	public Controller_HelloWorld() {
		super(R.layout.controller__hello_world);
	}

	@Override
	protected void onCreate() {

		helloWorldTextView.setText("Hello World " + index++);
		getRootView().setBackgroundColor(colors[UtilsRandom.nextInt(colors.length)]);
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
