/*
 *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *  Restricted usage under Binpress license
 *
 *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
 */

package com.nu.art.software.cyborg.demo.ui.controllers;

import android.view.View;
import android.widget.TextView;

import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgAdapter;
import com.nu.art.software.cyborg.core.CyborgRecycler;
import com.nu.art.software.cyborg.core.CyborgViewController;
import com.nu.art.software.cyborg.core.ItemRenderer;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class RecyclerExampleController
		extends CyborgViewController {

	@ViewIdentifier(viewId = R.id.ExampleSelectionList, listeners = {ViewListener.OnRecyclerItemClicked})
	private CyborgRecycler recycler;

	@ViewIdentifier(viewIds = {R.id.OrientationToggle, R.id.IncrementColumn, R.id.DecrementColumn}, listeners = ViewListener.OnClick)
	private View[] views;

	private CyborgAdapter<Integer, IntegerRenderer> numbers;

	private RecyclerExampleController() {
		super(R.layout.v1_controller__recycler_example);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.OrientationToggle:
				orie
				break;
			case R.id.IncrementColumn:
				break;
			case R.id.DecrementColumn:
		}
	}

	@Override
	protected void onPreSaveState() {
	}

	@Override
	protected void onPostRestoredState() {
	}

	private static class IntegerRenderer
			extends ItemRenderer<Integer> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		public IntegerRenderer() {
			super(R.layout.list_node__example);
		}

		@Override
		protected void renderItem(Integer item) {

		}
	}
}
