package com.nu.art.cyborg.demo.ui.controllers;

import android.graphics.Typeface;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nu.art.core.interfaces.Getter;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.common.utils.Interpolators;
import com.nu.art.cyborg.core.CyborgAdapter;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgRecycler;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.media.CyborgMediaPlayer;
import com.nu.art.cyborg.media.CyborgMediaPlayer.MediaPlayerListenerImpl;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.demo.model.MyModule;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TacB0sS on 30/11/2017.
 */

public class ControllerV1_MediaTester
	extends CyborgController {

	private int index = 0;
	private HashMap<String, CyborgMediaPlayer> mediaPlayers = new HashMap<>();

	@ViewIdentifier(viewId = R.id.SP_Streams,
	                listeners = ViewListener.OnItemSelected)
	private Spinner streams;

	@ViewIdentifier(viewId = R.id.SP_Interpolators,
	                listeners = ViewListener.OnItemSelected)
	private Spinner interpolators;

	@ViewIdentifier(viewId = R.id.IV_PlayPause,
	                listeners = ViewListener.OnClick)
	ImageView button;

	@ViewIdentifier(viewId = R.id.IV_CrossFade,
	                listeners = ViewListener.OnClick)
	ImageView crossFade;

	@ViewIdentifier(viewId = R.id.RV_Tracks)
	CyborgRecycler mediaPlayerRecycler;

	private CyborgAdapter<Interpolator> interpolatorsAdapter;
	private CyborgAdapter<MediaStream> streamsAdapter;
	private CyborgAdapter<MediaModel> playersAdapter;
	private ArrayList<MediaModel> tracks = new ArrayList<>();

	private ArrayList<MediaModel> selectedItems = new ArrayList<MediaModel>() {
		@Override
		public void clear() {
			while (size() > 2)
				remove(0);
		}
	};

	public ControllerV1_MediaTester() {
		super(R.layout.controller__media_player_tester);
	}

	@Override
	protected void onCreate() {
		streamsAdapter = new CyborgAdapter<>(this, Renderer_MediaStream.class);
		streamsAdapter.setResolver(new StreamsResolver());
		streams.setAdapter(streamsAdapter.getArrayAdapter());

		interpolatorsAdapter = new CyborgAdapter<>(this, Renderer_Interpolator.class);
		interpolatorsAdapter.setResolver(new InterpolatorResolver());
		interpolators.setAdapter(interpolatorsAdapter.getArrayAdapter());

		playersAdapter = new CyborgAdapter<>(this, Renderer_MediaPlayer.class);
		playersAdapter.setResolver(new MediaModelResolver());
		mediaPlayerRecycler.setAdapter(playersAdapter);

		super.onCreate();
	}

	@Override
	public void onItemSelected(Object selected, AdapterView<?> parentView, View selectedView, int position, long id) {
		switch (parentView.getId()) {
			case R.id.SP_Streams:
				streamsAdapter.invalidateDataModel();
				break;

			case R.id.SP_Interpolators:
				interpolatorsAdapter.invalidateDataModel();
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.IV_PlayPause:
				MediaStream selectedItem = (MediaStream) streams.getSelectedItem();
				if (selectedItem == null)
					return;

				tracks.add(new MediaModel(selectedItem, "track-" + (index++)));
				playersAdapter.invalidateDataModel();
				break;

			case R.id.IV_CrossFade:
				if (selectedItems.size() < 2) {
					toastDebug("Select two items");
					return;
				}

				CyborgMediaPlayer fadeOut = mediaPlayers.get(selectedItems.get(0).key);
				CyborgMediaPlayer fadeIn = mediaPlayers.get(selectedItems.get(1).key);
				if (fadeOut == null || !fadeOut.isAlive()) {
					toastDebug(selectedItems.get(0).key + " player no alive");
					return;
				}

				if (fadeIn == null || !fadeIn.isAlive()) {
					toastDebug(selectedItems.get(1).key + " player no alive");
					return;
				}

				fadeOut.play();
				fadeIn.play();

				fadeOut.setMaxVolume(100);
				fadeOut.setVolume(100);
				fadeOut.fadeVolumeAndSetItAsMax(0, 5000);

				fadeIn.setVolume(0);
				fadeIn.fadeVolumeAndSetItAsMax(100, 5000);

				break;
		}
	}

	private static class MediaModel {

		MediaStream stream;
		String key;

		private MediaModel(MediaStream stream, String key) {
			this.stream = stream;
			this.key = key;
		}
	}

	private class Renderer_MediaPlayer
		extends ItemRenderer<MediaModel> {

		@ViewIdentifier(viewId = R.id.CB_Selecta,
		                listeners = ViewListener.OnClick)
		CheckBox selecta;

		@ViewIdentifier(viewId = R.id.SB_MediaProgress,
		                listeners = ViewListener.SeekBar)
		SeekBar progress;

		@ViewIdentifier(viewId = R.id.TV_Volume)
		TextView volume;

		@ViewIdentifier(viewId = R.id.TV_Name)
		TextView name;

		@ViewIdentifier(viewId = R.id.IV_PlayPause,
		                listeners = ViewListener.OnClick)
		ImageView button;
		private MediaPlayerListenerImpl listener = new MediaPlayerListenerImpl() {
			@Override
			public void onPrepared() {
				render();
			}

			@Override
			public void onPlaying() {
				render();
			}

			@Override
			public void onMediaProgress(int position) {
				render();
			}

			@Override
			public void onPaused() {
				render();
			}

			@Override
			public void onMediaCompleted() {
				render();
			}

			@Override
			public void onInterrupted() {
				render();
			}

			@Override
			public void onError() {
				render();
			}
		};

		Renderer_MediaPlayer() {
			super(R.layout.renderer__media_model);
		}

		@Override
		protected void renderItem(MediaModel item) {
			CyborgMediaPlayer player = getPlayer();
			getPlayer().setListener(listener);
			boolean contains = selectedItems.contains(item);
			selecta.setChecked(contains);
			selecta.setText(!contains ? "" : (selectedItems.indexOf(item) == 0 ? "out" : "in"));

			name.setText(item.stream.name);
			progress.setProgress((int) (player.getPositionRelative() * progress.getMax()));
			switch (player.getState()) {
				case Disposing:
				case Idle:
				case Preparing:
					button.setImageResource(R.drawable.media_loading);
					break;

				case Prepared:
					button.setImageResource(R.drawable.media_play);
					break;

				case Playing:
					button.setImageResource(R.drawable.media_pause);
					break;
			}
			int progress = (int) (player.getVolumeRelative() * 100);
			volume.setText(progress + "/" + player.getMaxVolume());
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.IV_PlayPause:
					CyborgMediaPlayer player = getPlayer();
					switch (player.getState()) {
						case Idle:
						case Preparing:
						case Disposing:
							break;

						case Prepared:
							player.play();
							break;

						case Playing:
							player.pause();
							break;
					}
					break;

				case R.id.CB_Selecta:
					selectedItems.add(getItem());
					selectedItems.clear();
					playersAdapter.invalidateDataModel();
					break;
			}

			super.onClick(v);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			if (seekBar.getId() == R.id.TV_Volume)
				return;

			getPlayer().pause();
		}

		private CyborgMediaPlayer getPlayer() {
			CyborgMediaPlayer mediaPlayer = mediaPlayers.get(getItem().key);
			if (mediaPlayer == null) {
				mediaPlayers.put(getItem().key, mediaPlayer = getModule(MyModule.class).createPlayer());
				mediaPlayer.createBuilder().setMediaId(getItem().key).setUri(getItem().stream.url).setListener(listener).prepare();
			}

			return mediaPlayer;
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (seekBar.getId() == R.id.TV_Volume)
				return;

			getPlayer().play();
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (!fromUser)
				return;

			switch (seekBar.getId()) {
				case R.id.TV_Volume:
					return;
				default:
					int newDuration = (int) ((1f * progress / seekBar.getMax()) * getPlayer().getDuration());
					getPlayer().setPosition(newDuration);
			}
		}
	}

	public static class MediaStream {

		private final String name;
		private final String url;

		public MediaStream(String name, String url) {
			this.name = name;
			this.url = url;
		}
	}

	private class Renderer_Interpolator
		extends ItemRenderer<Interpolator> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView name;

		protected Renderer_Interpolator() {
			super(R.layout.renderer__example);
		}

		@Override
		protected void renderItem(Interpolator item) {
			name.setText(item.getClass().getSimpleName());
			name.setTypeface(null, getItem() == interpolators.getSelectedItem() ? Typeface.BOLD : Typeface.NORMAL);
		}
	}

	private class Renderer_MediaStream
		extends ItemRenderer<MediaStream> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView name;

		protected Renderer_MediaStream() {
			super(R.layout.renderer__example);
		}

		@Override
		protected void renderItem(MediaStream item) {
			name.setText(item.name);
			name.setTypeface(null, getItem() == streams.getSelectedItem() ? Typeface.BOLD : Typeface.NORMAL);
		}
	}

	private class InterpolatorResolver
		implements Getter<ListDataModel<Interpolator>> {

		@SuppressWarnings("unchecked")
		ListDataModel<Interpolator> dataModel = new ListDataModel<>(Interpolator.class);

		@Override
		public ListDataModel<Interpolator> get() {
			dataModel.clear();
			dataModel.add(Interpolators.LinearInterpolator);
			dataModel.add(Interpolators.DecelerateInterpolator);
			dataModel.add(Interpolators.AccelerateInterpolator);
			dataModel.add(Interpolators.AccelerateDecelerateInterpolator);
			return dataModel;
		}
	}

	private class MediaModelResolver
		implements Getter<ListDataModel<MediaModel>> {

		@SuppressWarnings("unchecked")
		ListDataModel<MediaModel> dataModel = new ListDataModel<>(MediaModel.class);

		@Override
		public ListDataModel<MediaModel> get() {
			dataModel.clear();
			dataModel.addAll(tracks);
			return dataModel;
		}
	}

	private class StreamsResolver
		implements Getter<ListDataModel<MediaStream>> {

		@SuppressWarnings("unchecked")
		ListDataModel<MediaStream> dataModel = new ListDataModel<>(MediaStream.class);

		@Override
		public ListDataModel<MediaStream> get() {
			dataModel.clear();
			dataModel.addAll(getModule(MyModule.class).getMediaStreams());
			return dataModel;
		}
	}
}
