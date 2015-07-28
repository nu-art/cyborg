package com.nu.art.software.cyborg.demo.ui.controllers.matrixTester.Transformations;

import android.graphics.Camera;
import android.graphics.Matrix;

/**
 * Created by TacB0sS on 25-Jul 2015.
 */
public class MatrixSkewStep
		extends TransformationStep {

	private float skewX;

	private float skewY;

	private float pivotX;

	private float pivotY;

	@Override
	public void setValues(float... values) {
		super.setValues(values);
		int i = 0;
		this.skewX = values[i++];
		this.skewY = values[i++];
		this.pivotX = values[i++];
		this.pivotY = values[i];
	}

	@Override
	public void applyTransform(Matrix matrix) {
		matrix.postSkew(skewX, skewY, pivotX, pivotY);
	}

	@Override
	public void applyTransform(Camera camera) {

	}

	@Override
	public String toString() {
		return String.format("%s\n(%.5f, %.5f) <> p[%.5f, %.5f]", type, skewX, skewY, pivotX, pivotY);
	}
}
