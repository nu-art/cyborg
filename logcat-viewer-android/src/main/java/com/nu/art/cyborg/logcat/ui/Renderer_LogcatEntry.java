package com.nu.art.cyborg.logcat.ui;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.logcat.LogcatEntry;
import com.nu.art.cyborg.logcat.Module_LogcatViewer;
import com.nu.art.cyborg.logcat.R;

public class Renderer_LogcatEntry
	extends ItemRenderer<LogcatEntry> {

	private TextView logMessage;
	private Module_LogcatViewer module;

	protected Renderer_LogcatEntry() {
		super(R.layout.renderer__logcat_item);
		setTag(getClass().getSimpleName());
	}

	@Override
	protected void extractMembers() {
		logMessage = getViewById(R.id.txt);
		logMessage.setTypeface(Typeface.MONOSPACE);
	}

	@Override
	protected void renderItem(LogcatEntry item) {
		logMessage.setTextSize(dimToPx(TypedValue.COMPLEX_UNIT_SP, module.getTextSize()));
		logMessage.setTextColor(module.getTextColor(item.getLogLevel()));

		String text = item.getLogMessage();
		logMessage.setText(text);
	}
}

