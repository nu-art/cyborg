

package com.nu.art.cyborg.demo.ui.controllers;

import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.demo.ui.controllers.ControllerV1_EventDispatcher.TextEventListener;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

/**
 * Created by TacB0sS on 16-Jun 2015.
 */
@ReflectiveInitialization
public class ControllerV1_EventReceiver
	extends CyborgController
	implements TextEventListener {

	@Override
	public void onTextEvent(String text) {
		textContent.setText(text);
	}

	@ViewIdentifier(viewId = R.id.EventTextContent)
	private TextView textContent;

	public ControllerV1_EventReceiver() {
		super(R.layout.controller__event_receiver_example);
	}

	@Override
	protected void onCreate() {
		super.onCreate();
	}
}
