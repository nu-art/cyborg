package com.nu.art.software.cyborg.demo.ui.controllers;

import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nu.art.software.cyborg.annotations.Restorable;
import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.demo.R;

@SuppressWarnings("unused")
public class InjectionExampleController
		extends CyborgController {


	/**
	 * Note the event reaching the <b>afterTextChanged</b> method
	 */
	@ViewIdentifier(viewId = R.id.InputText, listeners = ViewListener.OnTextChangedListener)
	EditText inputText;

	/**
	 * Note the event reaching the <b>onClick</b> and <b>onLongClick</b> methods respectively
	 */
	@ViewIdentifier(viewId = R.id.ResultTextView, listeners = {ViewListener.OnLongClick, ViewListener.OnClick})
	TextView resultTextView;

	/**
	 * Note the event reaching the <b>onClick</b> method
	 */
	@ViewIdentifier(viewId = R.id.UpdateTextButton, listeners = {ViewListener.OnClick})
	Button updateTextButton;

	/**
	 * To see the {@link Restorable} feature in action you'll need to go to the developer options and check the "<i>Do not keep activities</i>" option
	 */
	@Restorable
	String toSave;

	public InjectionExampleController() {
		super(R.layout.v1_controller__injection_example);
	}

	@Override
	public void afterTextChanged(TextView view, Editable editableValue) {
		if (view == inputText) {
			logInfo("Text Changed: " + inputText.getText().toString());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ResultTextView:
				resultTextView.setText("onClick");
				break;
			case R.id.UpdateTextButton:
				resultTextView.setText(toSave = inputText.getText().toString());
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
			case R.id.ResultTextView:
				resultTextView.setText("onLongClick");
				return true;
		}
		return false;
	}

	@Override
	protected void prepareToSaveState() {
		toSave = resultTextView.getText().toString();
	}

	@Override
	protected void onPostRestoredState() {
		inputText.setText(toSave);
	}
}
