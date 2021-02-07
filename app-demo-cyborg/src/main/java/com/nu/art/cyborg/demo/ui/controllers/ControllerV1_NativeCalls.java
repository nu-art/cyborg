

package com.nu.art.cyborg.demo.ui.controllers;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.widget.TextView;

import com.nu.art.core.interfaces.Getter;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.core.CyborgAdapter;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.core.CyborgRecycler;
import com.nu.art.cyborg.core.ItemRenderer;
import com.nu.art.cyborg.core.dataModels.DataModel;
import com.nu.art.cyborg.core.dataModels.ListDataModel;
import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.PermissionModule;
import com.nu.art.cyborg.modules.PermissionModule.PermissionResultListener;
import com.nu.art.cyborg.modules.calls.NativeCall;
import com.nu.art.cyborg.modules.calls.NativeCallsModule;
import com.nu.art.cyborg.modules.calls.NativeCallsModule.NativeCallsListener;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class ControllerV1_NativeCalls
	extends CyborgController
	implements PermissionResultListener, NativeCallsListener {

	@ViewIdentifier(viewId = R.id.RV_NativeCalls)
	private CyborgRecycler recycler;

	private NativeCallsModule callsModule;

	private ControllerV1_NativeCalls() {
		super(R.layout.controller__native_calls);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate() {
		CyborgAdapter<NativeCall> calls = new CyborgAdapter<>(this, NativeCallRenderer.class);
		calls.setResolver(new Getter<DataModel<NativeCall>>() {
			ListDataModel<NativeCall> model = new ListDataModel<>(NativeCall.class);

			@Override
			public DataModel<NativeCall> get() {
				model.clear();
				model.addAll(callsModule.getCalls());
				return model;
			}
		});
		recycler.setAdapter(calls);
	}

	@Override
	public void onResume() {
		super.onResume();
		getModule(PermissionModule.class).requestPermission(100, permission.PROCESS_OUTGOING_CALLS, permission.READ_PHONE_STATE);
		recycler.invalidateDataModel();
	}

	@Override
	public void onPermissionsRejected(int requestCode, String[] rejected) {
		postOnUI(new Runnable() {
			@Override
			public void run() {
				getActivity().onBackPressed();
			}
		});
	}

	@Override
	@SuppressLint("MissingPermission")
	public void onAllPermissionsGranted(int requestCode) {
		callsModule.enable();
	}

	@Override
	public void onCallsStateChanged() {
		recycler.invalidateDataModel();
	}

	private static class NativeCallRenderer
		extends ItemRenderer<NativeCall> {

		@ViewIdentifier(viewId = R.id.ExampleLabel)
		TextView exampleLabel;

		public NativeCallRenderer() {
			super(R.layout.renderer__example_double);
		}

		@Override
		protected void renderItem(NativeCall item) {
			exampleLabel.setText(item.toString());
		}
	}
}