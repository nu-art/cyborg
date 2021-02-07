

package com.nu.art.cyborg.demo.ui.controllers;

import android.os.Environment;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.demo.model.MyModule;
import com.nu.art.cyborg.media.CyborgMediaPlayer;
import com.nu.art.cyborg.ui.views.VideoView;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class ControllerV1_VideoView
	extends CyborgController {

	@ViewIdentifier(viewId = R.id.VV_VideoView)
	private VideoView videoView;

	public ControllerV1_VideoView() {
		super(R.layout.controller__video_view);
	}

	@Override
	protected void onResume() {
		super.onResume();
		String outputFile = Environment.getExternalStorageDirectory() + "/recorded-video/1530654992676.mp4";
		CyborgMediaPlayer player = getModule(MyModule.class).createPlayer();
		player.createBuilder().setAutoPlay(true).setUri(outputFile).prepare();
		videoView.setMediaPlayer(player);
	}
}
