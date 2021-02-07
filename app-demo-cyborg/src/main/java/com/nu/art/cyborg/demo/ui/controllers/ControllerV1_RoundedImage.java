

package com.nu.art.cyborg.demo.ui.controllers;

import com.nu.art.android.views.RoundedImageView;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class ControllerV1_RoundedImage
	extends CyborgController {

	@ViewIdentifier(viewId = R.id.IV_RoundedImageView)
	private RoundedImageView imageView;

	public ControllerV1_RoundedImage() {
		super(R.layout.controller__rounded_image_view);
	}
}
