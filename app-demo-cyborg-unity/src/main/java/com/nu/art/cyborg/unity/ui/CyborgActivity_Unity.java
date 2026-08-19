package com.nu.art.cyborg.unity.ui;

import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.nu.art.cyborg.core.CyborgActivity;
import com.nu.art.cyborg.unity.R;
import com.nu.art.cyborg.unity.modules.Module_Unity;

public class CyborgActivity_Unity
	extends CyborgActivity {

	public CyborgActivity_Unity() {
		super("Unity");
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		getModule(Module_Unity.class).windowFocusChanged(hasFocus);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		getModule(Module_Unity.class).configurationChanged(newConfig);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		Module_Unity unity = getModule(Module_Unity.class);
		if (event.getAction() == MotionEvent.ACTION_DOWN && !isOnOverlay(event))
			unity.tapUnity(event.getRawX(), event.getRawY());
		unity.injectEvent(event);
		return super.dispatchTouchEvent(event);
	}

	private boolean isOnOverlay(MotionEvent event) {
		View overlay = findViewById(R.id.unity_overlay);
		if (overlay == null)
			return false;

		int[] loc = new int[2];
		overlay.getLocationOnScreen(loc);
		float x = event.getRawX();
		float y = event.getRawY();
		return x >= loc[0] && x < loc[0] + overlay.getWidth()
			&& y >= loc[1] && y < loc[1] + overlay.getHeight();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() != KeyEvent.KEYCODE_BACK && getModule(Module_Unity.class).injectEvent(event))
			return true;
		return super.dispatchKeyEvent(event);
	}
}
