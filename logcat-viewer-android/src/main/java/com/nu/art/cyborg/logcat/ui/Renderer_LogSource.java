package com.nu.art.cyborg.logcat.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.logcat.LogcatSource;
import com.nu.art.cyborg.logcat.Module_LogcatViewer;
import com.nu.art.cyborg.logcat.R;

public class Renderer_LogSource
	extends ItemRenderer<LogcatSource> {

	private TextView txt;
	private Module_LogcatViewer module;

	public Renderer_LogSource() {
		super(R.layout.renderer__option);
	}

	@Override
	protected void extractMembers() {
		txt = getViewById(R.id.tv_ItemName);
		txt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				module.replaceSource(getItem());
			}
		});
	}

	@Override
	protected void renderItem(LogcatSource logcatSource) {
		txt.setText(logcatSource.getName());
	}
}