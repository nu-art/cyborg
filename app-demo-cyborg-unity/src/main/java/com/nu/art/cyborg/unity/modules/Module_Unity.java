package com.nu.art.cyborg.unity.modules;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.InputEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nu.art.core.generics.Processor;
import com.nu.art.cyborg.core.CyborgModule;
import com.unity3d.player.IUnityPlayerLifecycleEvents;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerForActivityOrService;

public class Module_Unity
	extends CyborgModule
	implements IUnityPlayerLifecycleEvents {

	public interface UnityPingListener {

		void onUnityPing(String payload);
	}

	private static volatile Module_Unity instance;

	private UnityPlayer player;

	private Module_Unity() {}

	public static Module_Unity getIfReady() {
		return instance;
	}

	@Override
	protected void init() {
		instance = this;
	}

	public void attachTo(FrameLayout container, Activity activity) {
		if (player == null)
			player = new UnityPlayerForActivityOrService(activity, this);

		View unityView = player.getView();
		if (unityView.getParent() instanceof ViewGroup)
			((ViewGroup) unityView.getParent()).removeView(unityView);

		container.removeAllViews();
		unityView.setClickable(true);
		unityView.setFocusable(true);
		container.addView(unityView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		player.windowFocusChanged(true);
		player.resume();
	}

	public void resume() {
		if (player != null)
			player.resume();
	}

	public void pause() {
		if (player != null)
			player.pause();
	}

	public void windowFocusChanged(boolean hasFocus) {
		if (player != null)
			player.windowFocusChanged(hasFocus);
	}

	public void configurationChanged(Configuration newConfig) {
		if (player != null)
			player.configurationChanged(newConfig);
	}

	public boolean injectEvent(InputEvent event) {
		return player != null && player.injectEvent(event);
	}

	public void tapUnity(float rawX, float rawY) {
		if (player == null)
			return;

		View unityView = player.getView();
		int[] loc = new int[2];
		unityView.getLocationOnScreen(loc);
		float x = rawX - loc[0];
		float y = rawY - loc[1];
		if (x < 0 || y < 0 || x > unityView.getWidth() || y > unityView.getHeight())
			return;

		UnityPlayer.UnitySendMessage("Cube", "TapFromAndroid", x + "," + y);
		logInfo("tap Unity " + x + "," + y);
	}

	public void pingUnity(String payload) {
		UnityPlayer.UnitySendMessage("Cube", "PingFromAndroid", payload == null ? "" : payload);
	}

	public void onNativePing(final String payload) {
		postOnUI(new Runnable() {
			@Override
			public void run() {
				logInfo("Unity ping: " + payload);
				dispatchEvent("unity ping", UnityPingListener.class, new Processor<UnityPingListener>() {
					@Override
					public void process(UnityPingListener listener) {
						listener.onUnityPing(payload);
					}
				});
			}
		});
	}

	@Override
	public void onUnityPlayerUnloaded() {
		logInfo("Unity player unloaded");
	}

	@Override
	public void onUnityPlayerQuitted() {
		logInfo("Unity player quitted");
		player = null;
	}
}
