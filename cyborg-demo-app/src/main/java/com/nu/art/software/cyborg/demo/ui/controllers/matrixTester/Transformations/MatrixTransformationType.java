package com.nu.art.software.cyborg.demo.ui.controllers.matrixTester.Transformations;

import com.nu.art.software.reflection.tools.ReflectiveTools;

/**
 * Created by TacB0sS on 25-Jul 2015.
 */
public enum MatrixTransformationType {
	MatrixTranslate(2, MatrixTranslateStep.class, new float[]{1f, 1f}, new float[]{0f, 0f}, new String[]{"Translate X", "Translate Y"}),//
	MatrixScale(4, MatrixScaleStep.class, new float[]{0.1f, 0.1f, 1f, 1f}, new float[]{1f, 1f, 0f, 0f}, new String[]{"Scale X", "Scale Y", "Pivot X",
			"Pivot Y"}),//
	MatrixSkew(4, MatrixSkewStep.class, new float[]{0.1f, 0.1f, 1f, 1f}, new float[]{0f, 0f, 0f, 0f}, new String[]{"Skew X", "Skew Y", "Pivot X", "Pivot Y"}),//
	MatrixRotate(3, MatrixRotateStep.class, new float[]{0.1f, 1f, 1f}, new float[]{0f, 0f, 0f}, new String[]{"Rotate", "Pivot X", "Pivot Y"}),//
	CameraRotate(3, CameraRotateStep.class, new float[]{0.1f, 0.1f, 0.1f}, new float[]{0f, 0f, 0f}, new String[]{"Rotate X", "Rotate Y", "Rotate Z"}),//
	CameraLocation(3, CameraLocationStep.class, new float[]{1f, 1f, 1f}, new float[]{0f, 0f, 0f}, new String[]{"Location X", "Location Y", "Location Z"}),//
	CameraTranslate(3, CameraTranslateStep.class, new float[]{1f, 1f, 1f}, new float[]{0f, 0f, 0f}, new String[]{"Translate X", "Translate Y",
			"Translate Z"}),//
	;

	private final int parameterCount;

	private final String[] labels;

	private final float[] deltas;

	private final float[] defaults;

	private final Class<? extends TransformationStep> stepType;

	MatrixTransformationType(int parameterCount, Class<? extends TransformationStep> stepType, float[] deltas, float[] defaults, String[] labels) {
		this.parameterCount = parameterCount;
		this.deltas = deltas;
		this.defaults = defaults;
		this.labels = labels;
		this.stepType = stepType;
	}

	public final TransformationStep getTransformation() {
		TransformationStep transformationStep = ReflectiveTools.newInstance(stepType);
		transformationStep.setValues(defaults);
		transformationStep.setType(this);
		return transformationStep;
	}

	public int getParameterCount() {
		return parameterCount;
	}

	public float getDeltaValue(int index) {
		return deltas[index];
	}

	public String getLabelValue(int index) {
		return labels[index];
	}
}
