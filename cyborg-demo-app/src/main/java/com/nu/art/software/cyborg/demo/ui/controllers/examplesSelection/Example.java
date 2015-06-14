package com.nu.art.software.cyborg.demo.ui.controllers.examplesSelection;

import com.nu.art.software.cyborg.demo.R;

/**
 * A list of examples and their layouts provided in this demo project.
 */
public enum Example {
	Injection(R.string.ExampleLabel_Injection, R.layout.v1_activity__injection_example),
	Storage(R.string.ExampleLabel_Storage, R.layout.v1_activity__storage_example);

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
