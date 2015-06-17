/*
 *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *  Restricted usage under Binpress license
 *
 *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
 */

package com.nu.art.software.cyborg.demo.ui.controllers;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nu.art.software.cyborg.annotations.Restorable;
import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgAdapter;
import com.nu.art.software.cyborg.core.CyborgRecycler;
import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.core.ItemRenderer;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.reflection.annotations.ReflectiveInitialization;

/**
 * State-loss is one of the common things Android developer tend to ignore... it mostly happens when the Device is loaded and Android decides it going to
 * literally kill the your application process!
 * <br>
 * <br>
 * The ONLY way to handle this is by making sure you implement the state loss and state restored callbacks that Android so kindly invoke before your
 * application is killed, and after it is been revived
 * <br>
 * <br>
 * In order to test this behavior fully, goto the developers menu in the settings and enable the "<b>Do not keep activities</b>" be aware that many application
 * will start behaving weird when you <b>browse between applications</b>... then you can know these application did not handle state loss correctly
 * <br>
 * <br>
 * Cyborg wraps this state loss and restore callback and saves and restores all the members defined with {@link Restorable} annotation.<br>
 * Follow the breakpoint instructions in the code and debug away...
 */
@Restorable
@ReflectiveInitialization
public class RecyclerExampleController
		extends CyborgController {

	@ViewIdentifier(viewId = R.id.RecyclerExample, listeners = {ViewListener.OnRecyclerItemClicked})
	private CyborgRecycler recycler;

	@ViewIdentifier(viewIds = {R.id.OrientationToggle, R.id.IncrementColumn, R.id.DecrementColumn}, listeners = ViewListener.OnClick)
	private View[] views;

	@ViewIdentifier(viewId = R.id.SaveRecyclerState)
	private CheckBox saveRecyclerStateCheckbox;

	@Restorable
	private boolean saveRecyclerState;

	private RecyclerExampleController() {
		super(R.layout.v1_controller__recycler_example);
	}

	@Override
	public void onCreate() {
		CyborgAdapter<Integer, IntegerRenderer> numbers = new CyborgAdapter<Integer, IntegerRenderer>(getActivity(), Integer.class, IntegerRenderer.class);
		for (int i = 0; i < 100; i++) {
			numbers.add(i);
		}
		recycler.setAdapter(numbers);
	}

	@Override
	protected void onPreSaveState() {
		// <<< ADD A BREAKPOINT AT THE NEXT LINE
		saveRecyclerState = saveRecyclerStateCheckbox.isChecked();
	}

	@Override
	protected void onSaveComplexObjectState(Bundle outState) {
		// <<< ADD A BREAKPOINT AT THE NEXT LINE
		if (saveRecyclerState)
			saveStatefulObject("recycler", outState, recycler);
	}

	@Override
	protected void onPostRestoredState() {
		// <<< ADD A BREAKPOINT AT THE NEXT LINE
		saveRecyclerStateCheckbox.setChecked(saveRecyclerState);
	}

	@Override
	protected void onRestoreComplexObjectState(Bundle inState) {
		// <<< ADD A BREAKPOINT AT THE NEXT LINE
		if (saveRecyclerState) {
			restoreStatefulObject("recycler", inState, recycler);
			recycler.invalidateLayoutManager();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.OrientationToggle:
				int currentOrientation = recycler.getLayoutOrientation();
				if (currentOrientation == LinearLayoutManager.VERTICAL)
					recycler.setLayoutOrientation(LinearLayoutManager.HORIZONTAL);
				else
					recycler.setLayoutOrientation(LinearLayoutManager.VERTICAL);
				break;
			case R.id.IncrementColumn:
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
					recycler.setPortraitColumnsCount(recycler.getPortraitColumnsCount() + 1);
				else
					recycler.setLandscapeColumnsCount(recycler.getLandscapeColumnsCount() + 1);
				break;
			case R.id.DecrementColumn:
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
					recycler.setPortraitColumnsCount(recycler.getPortraitColumnsCount() - 1);
				else
					recycler.setLandscapeColumnsCount(recycler.getLandscapeColumnsCount() - 1);
				break;
		}
		recycler.invalidateLayoutManager();
	}

	private static class IntegerRenderer
			extends ItemRenderer<Integer> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		public IntegerRenderer() {
			super(R.layout.list_node__recycler_example);
		}

		@Override
		protected void renderItem(Integer item) {
			exampleLabel.setText("" + item);
		}
	}
}
