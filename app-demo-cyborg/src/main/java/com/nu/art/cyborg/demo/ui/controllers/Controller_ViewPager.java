package com.nu.art.cyborg.demo.ui.controllers;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.widget.TextView;

import com.nu.art.core.interfaces.Getter;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.core.CyborgAdapter;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgViewPager;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.dataModels.DataModel;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.demo.R;

/**
 * Created by TacB0sS on 20-May 2017.
 */

public class Controller_ViewPager
	extends CyborgController {

	private static final long _5_Sec = 5000;
	private static final long _20_Sec = 20000;

	@SuppressWarnings("unchecked")
	private Class<? extends ItemRenderer<? extends Number>>[] RendererTypes = new Class[]{
		Renderer_Integer.class,
	};

	@SuppressWarnings("unchecked")
	private Class<? extends Number>[] ItemTypes = new Class[]{
		Integer.class,
	};

	@ViewIdentifier(viewId = R.id.RV_Example)
	private CyborgViewPager viewPager;

	private DataModelGetter resolver;

	private CyborgAdapter<Number> adapter;
	private Runnable scrollToNextItem = new Runnable() {
		@Override
		public void run() {
			viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
			postOnUI(_5_Sec, scrollToNextItem);
		}
	};

	public Controller_ViewPager() {
		super(R.layout.controller__viewpager);
	}

	@Override
	protected void onCreate() {
		//		viewPager.setOffscreenPageLimit(3);
		adapter = new CyborgAdapter<>(this, RendererTypes);
		viewPager.setRotationY(180);
		viewPager.setPageTransformer(false, new PageTransformer() {
			@Override
			public void transformPage(@NonNull View page, float factor) {
				float translationX = factor * viewPager.getWidth();
				page.setTranslationX(translationX);
			}
		});
		resolver = new DataModelGetter();
		adapter.setResolver(resolver);
		viewPager.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		adapter.invalidateDataModel();
		postOnUI(_5_Sec, scrollToNextItem);
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeActionFromUI(scrollToNextItem);
		resolver.startingIndex += 1;
	}

	private class DataModelGetter
		implements Getter<DataModel<Number>> {

		int startingIndex = 0;

		private ListDataModel<Number> dataModel = new ListDataModel<Number>(ItemTypes);

		@Override
		public DataModel<Number> get() {
			dataModel.clear();

			for (int i = startingIndex; i < 100; i++) {
				dataModel.add(i);
			}
			return dataModel;
		}
	}

	private class Renderer_Integer
		extends ItemRenderer<Integer> {

		@ViewIdentifier(viewId = R.id.TV_Number,
		                listeners = {})
		private TextView number;

		Renderer_Integer() {
			super(R.layout.renderer__number);
		}

		@Override
		protected void onCreate() {
			getRootView().setRotationY(180);
			super.onCreate();
		}

		@Override
		protected void renderItem(Integer item) {
			number.setText("int: " + item);
		}
	}
}
