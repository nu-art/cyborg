

package com.nu.art.cyborg.demo.ui.controllers;

import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.modules.ABTestingModule;
import com.nu.art.cyborg.demo.R;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class ControllerV1_ABTesting
	extends CyborgController {

	@ViewIdentifier(viewId = R.id.TV_ConstAB)
	private TextView constTV;

	@ViewIdentifier(viewId = R.id.TV_ChangeAB)
	private TextView changeTV;

	public ControllerV1_ABTesting() {
		super(R.layout.controller__ab_testing_view);
	}

	private static Integer[] colors = {
		R.color.Green,
		R.color.Red,
		R.color.white
	};

	@Override
	protected void onCreate() {
		constTV.setTextColor(getColor(getModule(ABTestingModule.class).calcABTesting("Demo", colors)));
		changeTV.setTextColor(getColor(getModule(ABTestingModule.class).calcABTesting("Demo", false, colors)));
	}
}
