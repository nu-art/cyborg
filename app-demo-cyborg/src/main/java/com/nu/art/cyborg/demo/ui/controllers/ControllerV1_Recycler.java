

package com.nu.art.cyborg.demo.ui.controllers;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nu.art.cyborg.annotations.Restorable;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgAdapter;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgRecycler;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.core.modules.ThreadsModule;
import com.nu.art.cyborg.demo.R;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

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
public class ControllerV1_Recycler
	extends CyborgController {

	@ViewIdentifier(viewId = R.id.RecyclerExample,
	                listeners = {ViewListener.OnRecyclerItemClicked})
	private CyborgRecycler recycler;

	@ViewIdentifier(viewId = {
		R.id.OrientationToggle,
		R.id.IncrementColumn,
		R.id.DecrementColumn
	},
	                listeners = ViewListener.OnClick)
	private View[] views;

	@ViewIdentifier(viewId = R.id.SaveRecyclerState)
	private CheckBox saveRecyclerStateCheckbox;

	@Restorable
	private boolean saveRecyclerState;

	private ThreadsModule threadsModule;

	private Handler handler;

	private ListDataModel<Number> dataModel;

	private ControllerV1_Recycler() {
		super(R.layout.controller__recycler_example);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate() {
		CyborgAdapter<Number> numbers = new CyborgAdapter<Number>(this, IntegerRenderer.class, DoubleRenderer.class, FloatRenderer.class);
		dataModel = new ListDataModel<Number>(Integer.class, Double.class, Float.class);
		for (int i = 0; i < 1000; i++) {
			if (i % 2 == 0)
				dataModel.add(i);
			else if (i % 5 == 0)
				dataModel.add((float) (i + 0.01 * i));
			else
				dataModel.add(i + 0.01 * i);
		}
		numbers.setDataModel(dataModel);
		recycler.setAdapter(numbers);
	}

	//
	@Override
	protected void onPreSaveState() {
		// <<< ADD A BREAKPOINT AT THE NEXT LINE
		saveRecyclerState = saveRecyclerStateCheckbox.isChecked();
	}

	@Override
	protected void onSaveComplexObjectState(Bundle outState) {
		// <<< ADD A BREAKPOINT AT THE NEXT LINE
		if (saveRecyclerState) {
			saveStatefulObject("recycler", outState, recycler);
		}
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
			//			recycler.invalidateLayoutManager();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
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

	private static class FloatRenderer
		extends ItemRenderer<Float> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		public FloatRenderer() {
			super(R.layout.renderer__example_float);
		}

		@Override
		protected void renderItem(Float item) {
			exampleLabel.setText("float: " + item);
		}
	}

	private static class DoubleRenderer
		extends ItemRenderer<Double> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		public DoubleRenderer() {
			super(R.layout.renderer__example_double);
		}

		@Override
		protected void renderItem(Double item) {
			exampleLabel.setText("" + item);
		}
	}

	private static class IntegerRenderer
		extends ItemRenderer<Integer> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		public IntegerRenderer() {
			super(R.layout.renderer__example_int);
		}

		@Override
		protected void renderItem(Integer item) {
			exampleLabel.setText("" + item);
		}
	}
}