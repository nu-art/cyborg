package com.nu.art.cyborg.logcat.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.logcat.LogcatViewerFilter;
import com.nu.art.cyborg.logcat.Module_LogcatViewer;
import com.nu.art.cyborg.logcat.R;

public class Renderer_LogTag
	extends ItemRenderer<String> {

	private CheckBox checkBox;
	private Module_LogcatViewer module;
	private LogcatViewerFilter filter;

	public Renderer_LogTag() {
		super(R.layout.renderer__selectable_item);
	}

	@Override
	protected void extractMembers() {
		checkBox = getViewById(R.id.CB_Item);
		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				filter.toggleTagSelection(getItem());
				module.onFilterUpdated();
			}
		});
	}

	@Override
	protected void onCreate() {
		filter = module.getFilter();
	}

	protected void renderItem(String tag) {
		checkBox.setChecked(filter.isTagSelected(tag));
		checkBox.setText(tag);
	}
}