package com.nu.art.software.cyborg.demo.ui.controllers.matrixTester.Transformations;

import android.graphics.Camera;
import android.graphics.Matrix;

/**
 * Created by TacB0sS on 25-Jul 2015.
 */
public class CameraTranslateStep
		extends TransformationStep {

	private float translateX;

	private float translateY;

	private float translateZ;

	@Override
	public void setValues(float... values) {
		super.setValues(values);
		int i = 0;
		this.translateX = values[i++];
		this.translateY = values[i++];
		this.translateZ = values[i];
	}

	@Override
	public void applyTransform(Matrix matrix) {

	}

	@Override
	public void applyTransform(Camera camera) {
		camera.translate(translateX, translateY, translateZ);
	}

	@Override
	public String toString() {
		return String.format("%s\n(%.5f, %.5f, %.5f)", type, translateX, translateY, translateZ);
	}
}
