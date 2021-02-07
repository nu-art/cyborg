

package com.nu.art.cyborg.demo.ui.controllers.injection;

import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.Restorable;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.demo.model.MyModule;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

/**
 * This example gives you a small impression of what Cyborg can do for you in terms of minimizing code and utilizing events and callbacks...
 */
@Restorable
@ReflectiveInitialization
public class ControllerV1_Injection
	extends CyborgController {

	@ViewIdentifier(viewId = {R.id.View1},
	                listeners = ViewListener.OnClick)
	private TextView view1;

	@ViewIdentifier(listeners = ViewListener.OnClick)
	private TextView view2;

	@ViewIdentifier(viewId = R.id.View3,
	                listeners = {ViewListener.OnClick})
	private View view3;

	@ViewIdentifier(viewId = R.id.View4,
	                listeners = ViewListener.OnClick)
	private TextView view4;
	/**
	 * Note the event reaching the <b>afterTextChanged</b> method
	 */
	@ViewIdentifier(viewId = R.id.InputText,
	                listeners = ViewListener.OnTextChangedListener)
	private EditText inputText;

	/**
	 * Note the event reaching the <b>onClick</b> and <b>onLongClick</b> methods respectively
	 */
	@ViewIdentifier(viewId = R.id.ResultTextView,
	                listeners = {
		                ViewListener.OnLongClick,
		                ViewListener.OnClick
	                })
	private TextView resultTextView;

	/**
	 * Note the event reaching the <b>onClick</b> method
	 */
	@ViewIdentifier(viewId = R.id.UpdateTextButton,
	                listeners = {ViewListener.OnClick})
	private Button updateTextButton;

	@ViewIdentifier(viewId = R.id.InjectedController)
	private Controller_InjectedController injectedController;

	/**
	 * To see the {@link Restorable} feature in action you'll need to go to the developer options and check the "<i>Do not keep activities</i>" option
	 */
	@Restorable
	private String toSave;

	private MyModule module;

	private ControllerV1_Injection() {
		super(R.layout.controller__injection_example);
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		view2.setText(view1.getId() == R.id.View1 ? "Injected" : "Manually");
	}

	@Override
	protected void extractMembers() {
		super.extractMembers();
		view2 = getViewById(R.id.View2);
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
		String view = getViewId(v);
		resultTextView.setText(view + " - onClick");
	}

	@Override
	public boolean onLongClick(View v) {
		// <<< ADD A BREAKPOINT AT THE NEXT LINE
		String view = getViewId(v);
		resultTextView.setText(view + " - onLongClick");
		return false;
	}

	private String getViewId(View v) {
		String view;
		switch (v.getId()) {
			case R.id.InjectedController:
				view = "InjectedController";
				break;

			case R.id.View1:
				view = "view1";
				break;

			case R.id.View2:
				view = "view2";
				break;

			case R.id.View3:
				view = "view3";
				break;

			case R.id.View4:
				view = "view4";
				break;

			default:
				view = "???";
		}
		return view;
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
