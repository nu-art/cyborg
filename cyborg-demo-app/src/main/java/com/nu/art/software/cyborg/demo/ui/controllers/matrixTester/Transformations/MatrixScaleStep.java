package com.nu.art.software.cyborg.demo.ui.controllers.matrixTester.Transformations;

import android.graphics.Camera;
import android.graphics.Matrix;

/**
 * Created by TacB0sS on 25-Jul 2015.
 */
public class MatrixScaleStep
		extends TransformationStep {

	private float scaleX;

	private float scaleY;

	private float pivotX;

	private float pivotY;

	@Override
	public void setValues(float... values) {
		super.setValues(values);
		int i = 0;
		this.scaleX = values[i++];
		this.scaleY = values[i++];
		this.pivotX = values[i++];
		this.pivotY = values[i];
	}

	@Override
	public void applyTransform(Matrix matrix) {
		matrix.postScale(scaleX, scaleY, pivotX, pivotY);
	}

	@Override
	public void applyTransform(Camera camera) {

	}

	@Override
	public String toString() {
		return String.format("%s\n(%.5f, %.5f) <> p[%.5f, %.5f]", type, scaleX, scaleY, pivotX, pivotY);
	}
}
