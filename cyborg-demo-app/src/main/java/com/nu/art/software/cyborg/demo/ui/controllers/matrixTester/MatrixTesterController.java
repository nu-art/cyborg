package com.nu.art.software.cyborg.demo.ui.controllers.matrixTester;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgAdapter;
import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.core.CyborgRecycler;
import com.nu.art.software.cyborg.core.ItemRenderer;
import com.nu.art.software.cyborg.core.dataModels.ListDataModel;
import com.nu.art.software.cyborg.demo.R;
import com.nu.art.software.cyborg.demo.ui.controllers.matrixTester.Transformations.MatrixTransformationType;
import com.nu.art.software.cyborg.demo.ui.controllers.matrixTester.Transformations.TransformationStep;
import com.nu.art.software.cyborg.ui.views.valueChanger.ValueChanger;
import com.nu.art.software.cyborg.ui.views.valueChanger.ValueChanger.OnValueChangedListener;

/**
 * Created by TacB0sS on 25-Jul 2015.
 */
public class MatrixTesterController
		extends CyborgController
		implements OnValueChangedListener {

	@ViewIdentifier(viewId = R.id.MatrixData)
	TextView matrixData;

	@ViewIdentifier(viewId = R.id.sampleView)
	View sampleView;

	@ViewIdentifier(viewId = R.id.RemoveTransform, listeners = {ViewListener.OnClick})
	View removeStep;

	@ViewIdentifier(viewId = R.id.TypeSpinner, listeners = {ViewListener.OnItemSelected})
	Spinner typeSpinner;

	@ViewIdentifier(viewIds = {R.id.Value0, R.id.Value1, R.id.Value2, R.id.Value3})
	ValueChanger[] valueChangers;

	@ViewIdentifier(viewId = R.id.TransfromList, listeners = {ViewListener.OnRecyclerItemClicked})
	CyborgRecycler transformSteps;

	private ListDataModel<MatrixTransformationType> transformationTypesDataModel;

	private ListDataModel<TransformationStep> transformationStepsDataModel;

	private MatrixTransformationType selectedTransformationType;

	private TransformationStep selectedTransformationStep;

	private boolean ignoreSelection;

	private Runnable render = new TransformationRenderer();

	public MatrixTesterController() {
		super(R.layout.v1_controller__matrix_tester);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		for (ValueChanger valueChanger : valueChangers) {
			valueChanger.setValueChangedListener(this);
		}

		CyborgAdapter<TransformationStep> transformationStepsAdapter = new CyborgAdapter<TransformationStep>(activityBridge, TransformationStepRenderer.class) {
			@Override
			@SuppressWarnings("unchecked")
			protected <RendererType extends ItemRenderer<? extends TransformationStep>> RendererType instantiateItemRendererType(Class<RendererType> renderersType) {
				return (RendererType) new TransformationStepRenderer();
			}
		};
		transformationStepsDataModel = new ListDataModel<TransformationStep>(TransformationStep.class);
		transformationStepsAdapter.setDataModel(transformationStepsDataModel);
		transformSteps.setAdapter(transformationStepsAdapter.getRecyclerAdapter(transformSteps));

		CyborgAdapter<MatrixTransformationType> transformTypeAdapter = new CyborgAdapter<MatrixTransformationType>(activityBridge, TransformationTypeRenderer.class) {
			@Override
			@SuppressWarnings("unchecked")
			protected <RendererType extends ItemRenderer<? extends MatrixTransformationType>> RendererType instantiateItemRendererType(Class<RendererType> renderersType) {
				return (RendererType) new TransformationTypeRenderer();
			}
		};
		transformationTypesDataModel = new ListDataModel<MatrixTransformationType>(MatrixTransformationType.class);
		transformationTypesDataModel.addItems(MatrixTransformationType.values());
		transformTypeAdapter.setDataModel(transformationTypesDataModel);
		typeSpinner.setAdapter(transformTypeAdapter.getArrayAdapter());
	}

	float[] uiValues = new float[4];

	@Override
	public void onClick(View v) {
		transformationStepsDataModel.removeItems(selectedTransformationStep);
		TransformationStep toSelect = transformationStepsDataModel.getItemForPosition(transformationStepsDataModel.getItemsCount() - 1);
		onTransformStepSelected(toSelect);
	}

	@Override
	public void onValueChanged() {
		for (int i = 0; i < valueChangers.length; i++) {
			uiValues[i] = valueChangers[i].getValue();
		}
		selectedTransformationStep.setValues(uiValues);
		transformationStepsDataModel.renderItem(selectedTransformationStep);
		removeAndPostOnUI(100, render);
	}

	float[] matrixValues = new float[9];

	@Override
	public void onRecyclerItemClicked(RecyclerView parentView, View view, int position) {
		onTransformStepSelected(transformationStepsDataModel.getItemForPosition(position));
	}

	private void onTransformStepSelected(TransformationStep transformationStep) {
		selectedTransformationStep = transformationStep;
		MatrixTransformationType type = selectedTransformationStep.getType();
		if (type != selectedTransformationType)
			ignoreSelection = true;
		logInfo("Transformation Selected: " + selectedTransformationStep.toString().replaceAll("\n", " "));
		setupTransformType(type);
		setupTransformStep(selectedTransformationStep);
		typeSpinner.setSelection(type.ordinal());
	}

	private void setupTransformStep(TransformationStep step) {
		float[] values = step.getValues();
		if (values == null)
			return;

		for (int i = 0; i < values.length; i++) {
			valueChangers[i].setValue(values[i]);
		}
	}

	private void setupTransformType(MatrixTransformationType selectedItem) {
		for (int i = 0; i < 4; i++) {
			valueChangers[i].setVisibility(i < selectedItem.getParameterCount() ? View.VISIBLE : View.GONE);
			valueChangers[i].setValue(0);
			if (i >= selectedItem.getParameterCount())
				continue;
			valueChangers[i].setLabel(selectedItem.getLabelValue(i));
			valueChangers[i].setDeltaValue(selectedItem.getDeltaValue(i));
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedView, int position, long id) {
		MatrixTransformationType selectedItem = transformationTypesDataModel.getItemForPosition(position);
		selectedTransformationType = selectedItem;

		if (!ignoreSelection) {
			selectedTransformationStep = selectedItem.getTransformation();
			transformationStepsDataModel.addItems(selectedTransformationStep);
			logInfo("new Transformation Added: " + selectedTransformationStep.toString().replaceAll("\n", " "));
		} else
			logInfo("on Transformation Matched Step: " + selectedItem);

		ignoreSelection = false;
		onTransformStepSelected(selectedTransformationStep);
		transformationStepsDataModel.notifyDataSetChanged();
	}

	private String getMatrixAsString(Matrix matrix) {
		matrix.getValues(matrixValues);
		String value = "";
		for (int i = 0; i < 3; i++) {
			value += String.format("[%.5f, %.5f, %.5f]\n", matrixValues[i * 3], matrixValues[i * 3 + 1], matrixValues[i * 3 + 2]);
		}
		return value.substring(0, value.length() - 1);
	}

	private class TransformationTypeRenderer
			extends ItemRenderer<MatrixTransformationType> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		private TextView label;

		TransformationTypeRenderer() {
			super(R.layout.list_node__recycler_example_double);
		}

		@Override
		protected void renderItem(MatrixTransformationType item) {
			label.setTypeface(null, selectedTransformationType == item ? Typeface.BOLD : Typeface.NORMAL);
			label.setText(item.name());
		}
	}

	private class TransformationStepRenderer
			extends ItemRenderer<TransformationStep> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		private TextView label;

		TransformationStepRenderer() {
			super(R.layout.list_node__recycler_transfrom_step);
		}

		@Override
		protected void renderItem(TransformationStep item) {
			label.setTypeface(null, selectedTransformationStep == item ? Typeface.BOLD : Typeface.NORMAL);
			label.setText(item.toString());
		}
	}

	private class TransformationRenderer
			implements Runnable {

		@Override
		public void run() {
			final Matrix matrix = new Matrix();
			final Camera camera = new Camera();
			for (int i = 0; i < transformationStepsDataModel.getItemsCount(); i++) {
				transformationStepsDataModel.getItemForPosition(i).applyTransform(camera);
			}
			camera.getMatrix(matrix);
			for (int i = 0; i < transformationStepsDataModel.getItemsCount(); i++) {
				transformationStepsDataModel.getItemForPosition(i).applyTransform(matrix);
			}
			Animation animation = new Animation(getActivity(), null) {
				@Override
				protected void applyTransformation(float interpolatedTime, Transformation t) {
					super.applyTransformation(interpolatedTime, t);
					t.getMatrix().set(matrix);
				}
			};

			animation.setDuration(0);
			animation.setFillAfter(true);
			sampleView.startAnimation(animation);
			matrixData.setText(getMatrixAsString(matrix));
		}
	}
}
