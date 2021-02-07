

package com.nu.art.cyborg.demo.ui.controllers;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nu.art.core.generics.Processor;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

/**
 * Created by TacB0sS on 16-Jun 2015.
 */
@ReflectiveInitialization
public class ControllerV1_EventDispatcher
	extends CyborgController {

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

	@ViewIdentifier(viewId = R.id.DispatchTextButton,
	                listeners = {ViewListener.OnClick})
	Button dispatchEventButton;

	private ControllerV1_EventDispatcher() {
		super(R.layout.controller__event_dispatcher_example);
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		if (isInEditMode())
			textToDispatch.setText("From Edit Mode");
	}

	@Override
	public void onClick(View v) {
		final String text = textToDispatch.getText().toString();
		dispatchEvent("Text has changed: " + text, TextEventListener.class, new Processor<TextEventListener>() {
			@Override
			public void process(TextEventListener toProcess) {
				toProcess.onTextEvent(text);
			}
		});
	}
}
