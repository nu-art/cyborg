/*
 *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *  Restricted usage under Binpress license
 *
 *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
 */

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
import com.nu.art.software.cyborg.demo.model.MyModule;
import com.nu.art.software.reflection.annotations.ReflectiveInitialization;

/**
 * This example gives you a small impression of what Cyborg can do for you in terms of minimizing code and utilizing events and callbacks...
 */
@Restorable
@ReflectiveInitialization
public class InjectionExampleController
		extends CyborgController {

	@ViewIdentifier(viewIds = {R.id.View1, R.id.View2, R.id.View3}, listeners = ViewListener.OnClick)
	private View[] views;

	/**
	 * Note the event reaching the <b>afterTextChanged</b> method
	 */
	@ViewIdentifier(viewId = R.id.InputText, listeners = ViewListener.OnTextChangedListener)
	private EditText inputText;

	/**
	 * Note the event reaching the <b>onClick</b> and <b>onLongClick</b> methods respectively
	 */
	@ViewIdentifier(viewId = R.id.ResultTextView, listeners = {ViewListener.OnLongClick, ViewListener.OnClick})
	private TextView resultTextView;

	/**
	 * Note the event reaching the <b>onClick</b> method
	 */
	@ViewIdentifier(viewId = R.id.UpdateTextButton, listeners = {ViewListener.OnClick})
	private Button updateTextButton;

	/**
	 * To see the {@link Restorable} feature in action you'll need to go to the developer options and check the "<i>Do not keep activities</i>" option
	 */
	@Restorable
	private String toSave;

	private MyModule module;

	private InjectionExampleController() {
		super(R.layout.v1_controller__injection_example);
	}

	@Override
	public void afterTextChanged(TextView view, Editable editableValue) {
		// <<< ADD A BREAKPOINT AT THE NEXT LINE
		if (view == inputText) {
			logInfo("Text Changed: " + inputText.getText().toString());
		}
	}

	@Override
	public void onClick(View v) {
		// <<< ADD A BREAKPOINT AT THE NEXT LINE
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
		// <<< ADD A BREAKPOINT AT THE NEXT LINE
		switch (v.getId()) {
			case R.id.ResultTextView:
				resultTextView.setText("onLongClick");
				return true;
		}
		return false;
	}

	@Override
	protected void onPreSaveState() {
		toSave = resultTextView.getText().toString();
	}

	@Override
	protected void onPostRestoredState() {
		inputText.setText(toSave);
	}
}
