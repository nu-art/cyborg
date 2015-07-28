package com.nu.art.software.cyborg.demo.ui.controllers.transitionAnimation;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.core.interfaces.StackManagerEventListener;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.cyborg.ui.animations.viewBasedAnimations.LeadingViewAnimationBuilder;
import com.nu.art.software.cyborg.ui.animations.viewBasedAnimations.LeadingViewAnimationBuilder.ViewGenerator;
import com.nu.art.software.cyborg.ui.animations.viewBasedAnimations.LeadingViewAnimationBuilder.ViewResolver;

/**
 * Created by TacB0sS on 14-Jul 2015.
 */
public class TransitionAnimationBeginExampleController
		extends CyborgController {

	@ViewIdentifier(viewId = R.id.imageView, listeners = ViewListener.OnClick)
	private ImageView image;

	@ViewIdentifier(viewId = R.id.textView4, listeners = ViewListener.OnClick)
	private TextView text;

	@ViewIdentifier(viewId = R.id.ratingBar, listeners = ViewListener.OnClick)
	private RatingBar ratingBar;

	private StackManagerEventListener stack;

	private static final String AnimationTransitionStack = "AnimationTransition";

	public TransitionAnimationBeginExampleController() {
		super(R.layout.v1_controller__transition_animation_begin_example);
	}

	@Override
	public void onCreate() {
		stack = getController(StackManagerEventListener.class, AnimationTransitionStack);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onClick(View v) {
		LeadingViewAnimationBuilder leadingViewAnimation;
		switch (v.getId()) {
			case R.id.imageView:
				leadingViewAnimation = new LeadingViewAnimationBuilder<ImageView>(getActivity());
				leadingViewAnimation
						.setViewResolver(new ViewResolver<ImageView, TransitionAnimationBeginExampleController, TransitionAnimationEndExampleController>() {
							@Override
							public ImageView resolveOriginView(TransitionAnimationBeginExampleController transitionAnimationBeginExampleController) {
								return transitionAnimationBeginExampleController.getImage();
							}

							@Override
							public ImageView resolveTargetView(TransitionAnimationEndExampleController transitionAnimationEndExampleController) {
								return transitionAnimationEndExampleController.getImage();
							}
						});
				leadingViewAnimation.setViewGenerator(new ViewGenerator<ImageView>() {
					@Override
					public ImageView generate(ImageView origin, ImageView target) {
						ImageView view = new ImageView(origin.getContext());
						view.setImageDrawable(origin.getDrawable());
						return view;
					}
				});
				stack.push("AnimationEnd", TransitionAnimationEndExampleController.class, true);
				break;
			case R.id.textView:
				break;
			case R.id.ratingBar:
				break;
		}
	}

	public RatingBar getRatingBar() {
		return ratingBar;
	}

	public TextView getText() {
		return text;
	}

	public ImageView getImage() {
		return image;
	}

}
