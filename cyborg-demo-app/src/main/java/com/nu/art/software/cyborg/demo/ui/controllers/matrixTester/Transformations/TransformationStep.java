package com.nu.art.software.cyborg.demo.ui.controllers.matrixTester.Transformations;

import android.graphics.Camera;
import android.graphics.Matrix;

/**
 * Created by TacB0sS on 25-Jul 2015.
 */
public abstract class TransformationStep {

	protected MatrixTransformationType type;

	private float[] values;

	public void setValues(float... values) {
		this.values = values.clone();
	}

	public abstract void applyTransform(Matrix matrix);

	void setType(MatrixTransformationType type) {
		this.type = type;
	}

	public MatrixTransformationType getType() {
		return type;
	}

	public float[] getValues() {
		return values;
	}

	public abstract void applyTransform(Camera camera);
}
