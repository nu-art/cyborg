package com.nu.art.software.cyborg.demo.ui.controllers.examplesSelection;

import com.nu.art.software.cyborg.demo.R;

/**
 * A list of examples and their layouts provided in this demo project.
 */
public enum Example {
	Injection(R.string.ExampleLabel_Injection, R.layout.v1_activity__injection_example), //
	Storage(R.string.ExampleLabel_Storage, R.layout.v1_activity__storage_example), //
	Recycler(R.string.ExampleLabel_Recycler, R.layout.v1_activity__recycler_example), //
	EventDispatching(R.string.ExampleLabel_EventDispatching, R.layout.v1_activity__event_dispatching_example), //
	Stack(R.string.ExampleLabel_Stack, R.layout.v1_activity__dynamic_controllers_stack_example), //
	Animations(R.string.ExampleLabel_Animations, R.layout.v1_activity__animations_example), //
	;

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
