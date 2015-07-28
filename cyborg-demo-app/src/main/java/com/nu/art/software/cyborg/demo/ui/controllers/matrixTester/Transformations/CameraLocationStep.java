package com.nu.art.software.cyborg.demo.ui.controllers.matrixTester.Transformations;

import android.graphics.Camera;
import android.graphics.Matrix;

/**
 * Created by TacB0sS on 25-Jul 2015.
 */
public class CameraLocationStep
		extends TransformationStep {

	private float locationX;

	private float locationY;

	private float locationZ;

	@Override
	public void setValues(float... values) {
		super.setValues(values);
		int i = 0;
		this.locationX = values[i++];
		this.locationY = values[i++];
		this.locationZ = values[i];
	}

	@Override
	public void applyTransform(Matrix matrix) {

	}

	@Override
	public void applyTransform(Camera camera) {
		camera.setLocation(locationX, locationY, locationZ);
	}

	@Override
	public String toString() {
		return String.format("%s\n(%.5f, %.5f, %.5f)", type, locationX, locationY, locationZ);
	}
}
