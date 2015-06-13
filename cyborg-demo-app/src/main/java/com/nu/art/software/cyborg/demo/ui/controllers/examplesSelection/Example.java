package com.nu.art.software.cyborg.demo.ui.controllers.examplesSelection;

import com.nu.art.software.cyborg.demo.R;

/**
 * Created by TacB0sS on 13-Jun 2015.
 */
public enum Example {
	Injection(R.string.EampleLabel_Injection, R.layout.v1_activity__injection_example),
	Storage(R.string.EampleLabel_Storage, R.layout.v1_activity__storage_example);

	private final int labelId;

	private final int layoutId;

	Example(int labelId, int layoutId) {
		this.labelId = labelId;
		this.layoutId = layoutId;
	}

	public int getLabelId() {
		return labelId;
	}

	public int getLayoutId() {
		return layoutId;
	}
}
