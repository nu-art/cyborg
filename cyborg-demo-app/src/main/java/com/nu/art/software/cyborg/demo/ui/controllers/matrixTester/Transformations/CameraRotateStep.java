package com.nu.art.software.cyborg.demo.ui.controllers.matrixTester.Transformations;

import android.graphics.Camera;
import android.graphics.Matrix;

/**
 * Created by TacB0sS on 25-Jul 2015.
 */
public class CameraRotateStep
		extends TransformationStep {

	private float rotateX;

	private float rotateY;

	private float rotateZ;

	@Override
	public void setValues(float... values) {
		super.setValues(values);
		int i = 0;
		this.rotateX = values[i++];
		this.rotateY = values[i++];
		this.rotateZ = values[i];
	}

	@Override
	public void applyTransform(Matrix matrix) {

	}

	@Override
	public void applyTransform(Camera camera) {
		camera.rotate(rotateX, rotateY, rotateZ);
	}

	@Override
	public String toString() {
		return String.format("%s\n(%.5f, %.5f, %.5f)", type, rotateX, rotateY, rotateZ);
	}
}
