

package com.nu.art.cyborg.demo.ui.controllers;

import android.view.View;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class ControllerV1_DialogContent
	extends CyborgController {

	@ViewIdentifier(viewId = R.id.BTN_ShowDialog,
	                listeners = ViewListener.OnClick)
	View showDialog;

	public ControllerV1_DialogContent() {
		super(R.layout.controller__dialog_content);
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.BTN_ShowDialog:
				createLayerBuilder().setControllerType(ControllerV1_Dialog.class).setKeepBackground(true).push();
				break;
		}
	}
}