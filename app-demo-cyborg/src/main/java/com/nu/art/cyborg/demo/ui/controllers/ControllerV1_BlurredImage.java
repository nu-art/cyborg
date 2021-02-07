

package com.nu.art.cyborg.demo.ui.controllers;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nu.art.core.generics.Processor;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.common.consts.ViewListener;
import com.nu.art.cyborg.core.CyborgAdapter;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgRecycler;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.dataModels.DataModel;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.BlurModule;
import com.nu.art.cyborg.modules.ImageUtilsModule;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class ControllerV1_BlurredImage
	extends CyborgController {

	@ViewIdentifier(viewId = R.id.RV_Images,
	                listeners = {ViewListener.OnRecyclerItemClicked})
	private CyborgRecycler recycler;

	enum Images {
		Hammer(R.drawable.hammer),
		Fingerprint(R.drawable.icon_fingerprint),
		Arrow(R.drawable.arrow),
		Load(R.drawable.media_loading),
		Pause(R.drawable.media_pause),
		Play(R.drawable.media_play),
		//
		;

		final int id;

		Images(int id) {
			this.id = id;
		}
	}

	public ControllerV1_BlurredImage() {
		super(R.layout.controller__blur_image);
	}

	@Override
	protected void onCreate() {
		CyborgAdapter<Images> examplesAdapter = new CyborgAdapter<>(this, Renderer_Blur.class);
		examplesAdapter.setResolver(new Getter<DataModel<Images>>() {

			private ListDataModel<Images> _dataModel = new ListDataModel<>(Images.class);

			@Override
			public DataModel<Images> get() {
				_dataModel.clear();
				_dataModel.add(Images.values());
				return _dataModel;
			}
		});
		recycler.setAdapter(examplesAdapter);
	}

	public static class Renderer_Blur
		extends ItemRenderer<Images> {

		@ViewIdentifier(viewId = R.id.IV_OriginImageView)
		private ImageView originImageView;

		@ViewIdentifier(viewId = R.id.IV_BlurredImageView)
		private ImageView blurredImageView;

		public Renderer_Blur() {
			super(R.layout.renderer__blur_image);
		}

		@Override
		protected void renderItem(Images item) {
			final int drawableId = item.id;
			originImageView.setImageResource(drawableId);
			getModule(BlurModule.class).createBlur().setBitmapResolver(new Getter<Bitmap>() {
				@Override
				public Bitmap get() {
					Bitmap bitmap = ImageUtilsModule.drawableToBitmap(getDrawable(drawableId));
					return bitmap;
				}
			}).setName("test").setOnSuccess(new Processor<Bitmap>() {
				@Override
				public void process(final Bitmap bitmap) {
					postOnUI(new Runnable() {
						@Override
						public void run() {
							blurredImageView.setImageBitmap(bitmap);
						}
					});
				}
			}).blur();
		}
	}
}
