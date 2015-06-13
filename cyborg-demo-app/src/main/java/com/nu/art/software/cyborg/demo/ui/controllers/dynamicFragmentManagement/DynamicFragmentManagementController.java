///*
// *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
// *  Restricted usage under Binpress license
// *
// *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
// */
//
//package com.nu.art.software.cyborg.demo.ui.controllers.dynamicFragmentManagement;
//
//import android.os.AsyncTask;
//import android.view.View;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.TextView;
//
//import com.nu.art.software.cyborg.annotations.Restorable;
//import com.nu.art.software.cyborg.annotations.ViewIdentifier;
//import com.nu.art.software.cyborg.common.consts.ViewListener;
//import com.nu.art.software.cyborg.core.CyborgFragmentActivity;
//import com.nu.art.software.cyborg.core.CyborgFragmentController;
//import com.nu.art.software.cyborg.core.CyborgViewController;
//import com.nu.art.software.cyborg.core.FragmentStack;
//import com.nu.art.software.cyborg.demo.R;
//
//@SuppressWarnings("unused")
//public class DynamicFragmentManagementController
//		extends CyborgViewController {
//
//	@ViewIdentifier(viewIds = {R.id.AddA, R.id.AddB, R.id.AddC}, listeners = ViewListener.OnClick)
//	private View[] views;
//
//	@ViewIdentifier(viewId = R.id.FragmentStackLabel)
//	private TextView fragmentStack;
//
//	@ViewIdentifier(viewId = R.id.AddToLeftStack, listeners = ViewListener.OnCheckChanged)
//	private CheckBox addToLeftStack;
//
//	@Restorable
//	private String toSave;
//
//	private String notToSave;
//
//	private FragmentStack parentStack_Left;
//
//	private FragmentStack parentStack_Right;
//
//	private FragmentStack currentStack;
//
//	public DynamicFragmentManagementController() {
//		super(R.layout.v1_controller__fragment_management_a);
//	}
//
//	class Sometask
//			extends AsyncTask<Object, Object, Object> {
//
//		public Sometask(CyborgFragmentActivity activity) {
//
//		}
//
//		@Override
//		protected Object doInBackground(Object... objects) {
//			return new Object();
//		}
//	}
//
//	@Override
//	protected void onCreate() {
//		parentStack_Left = new FragmentStack(getFragmentManager(), R.id.ParentLayoutId1);
//		parentStack_Right = new FragmentStack(getFragmentManager(), R.id.ParentLayoutId2);
//		currentStack = parentStack_Right;
//
//		postOnUI(30, new Runnable() {
//			@Override
//			public void run() {
//				CyborgFragmentController[] controllers = getActivity().getControllers();
//				String fragStack = "";
//				for (CyborgFragmentController controller : controllers) {
//					if (controller == DynamicFragmentManagementController.this)
//						continue;
//					fragStack += controller.getFragmentTag() + "(" + controller.getState() + ")\n";
//				}
//				fragmentStack.setText(fragStack);
//
//				if (isDestroyed())
//					return;
//				postOnUI(100, this);
//			}
//		});
//		Sometask task1 = new Sometask(getActivity());
//		task1.execute(new Object());
//		Sometask task2 = new Sometask(getActivity());
//		task2.execute(new Object());
//		Sometask task3 = new Sometask(getActivity());
//		task3.execute(new Object());
//		Sometask task4 = new Sometask(getActivity());
//		task4.execute(new Object());
//		Sometask task5 = new Sometask(getActivity());
//		task5.execute(new Object());
//	}
//
//	@Override
//	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//		currentStack = !isChecked ? parentStack_Right : parentStack_Left;
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//			case R.id.AddA:
//				currentStack.addFragment(DynamicAFragmentController.class, "TagA");
//				break;
//			case R.id.AddB:
//				currentStack.addFragment(DynamicBFragmentController.class, "TagB");
//				break;
//			case R.id.AddC:
//				currentStack.addFragment(DynamicCFragmentController.class, "TagC");
//				break;
//		}
//	}
//
//	@Override
//	public boolean onBackPressed() {
//		currentStack.pop();
//		return true;
//	}
//}
