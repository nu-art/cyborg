package com.nu.art.software.cyborg.demo.ui.controllers;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nu.art.software.core.generics.Processor;
import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgViewController;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.reflection.annotations.ReflectiveInitialization;

/**
 * Created by TacB0sS on 16-Jun 2015.
 */
@ReflectiveInitialization
public class EventDispatcherExampleController
		extends CyborgViewController {

	/**
	 * All event listener should be declared in the vicinity of the invoking class type... for example, this class dispatches the Text event, then it is the
	 * one
	 * to declare it
	 */
	public interface TextEventListener {

		void onTextEvent(String text);
	}

	@ViewIdentifier(viewId = R.id.TextToDispatch)
	EditText textToDispatch;

	@ViewIdentifier(viewId = R.id.DispatchTextButton, listeners = {ViewListener.OnClick})
	Button dispatchEventButton;

	private EventDispatcherExampleController() {
		super(R.layout.v1_controller__event_dispatcher_example);
	}

	@Override
	public void onClick(View v) {
		dispatchEvent(TextEventListener.class, new Processor<TextEventListener>() {
			@Override
			public void process(TextEventListener toProcess) {
				toProcess.onTextEvent(textToDispatch.getText().toString());
			}
		});
	}
}
