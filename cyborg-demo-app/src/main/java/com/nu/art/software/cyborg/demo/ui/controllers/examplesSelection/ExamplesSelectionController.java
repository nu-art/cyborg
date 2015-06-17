package com.nu.art.software.cyborg.demo.ui.controllers.examplesSelection;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgActivity;
import com.nu.art.software.cyborg.core.CyborgAdapter;
import com.nu.art.software.cyborg.core.CyborgRecycler;
import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.core.ItemRenderer;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.reflection.annotations.ReflectiveInitialization;

/**
 * Created by TacB0sS on 13-Jun 2015.
 */
@ReflectiveInitialization
public class ExamplesSelectionController
		extends CyborgController {

	@ViewIdentifier(viewId = R.id.ExampleSelectionList, listeners = {ViewListener.OnRecyclerItemClicked})
	private CyborgRecycler recycler;

	private CyborgAdapter<Example, ExampleRenderer> examples;

	private ExamplesSelectionController() {
		super(R.layout.v1_controller__examples_selection);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		examples = new CyborgAdapter<Example, ExampleRenderer>(getActivity(), Example.class, ExampleRenderer.class);
		examples.addAll(Example.values());
		recycler.setAdapter(examples);
	}

	@Override
	public void onRecyclerItemClicked(RecyclerView parentView, View view, int position) {
		Example example = examples.getItem(position);
		Intent intent = CyborgActivity.composeIntent(example.name(), example.getLayoutId());
		startActivity(intent);
	}

	public static class ExampleRenderer
			extends ItemRenderer<Example> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		public ExampleRenderer() {
			super(R.layout.list_node__example);
		}

		@Override
		protected void renderItem(Example item) {
			exampleLabel.setText(item.getLabelId());
		}
	}
}

