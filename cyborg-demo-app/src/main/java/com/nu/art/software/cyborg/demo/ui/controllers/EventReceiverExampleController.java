package com.nu.art.software.cyborg.demo.ui.controllers;

import android.widget.TextView;

import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.cyborg.demo.ui.controllers.EventDispatcherExampleController.TextEventListener;

/**
 * Created by TacB0sS on 16-Jun 2015.
 */
public class EventReceiverExampleController
		extends CyborgController
		implements TextEventListener {

	@Override
	public void onTextEvent(String text) {
		textContent.setText(text);
	}

	@ViewIdentifier(viewId = R.id.EventTextContent)
	private TextView textContent;

	public EventReceiverExampleController() {
		super(R.layout.v1_controller__event_receiver_example);
	}
}
