/*
 *  Copyright (c) 2015 to Adam van der Kruk (Zehavi) AKA TacB0sS - Nu-Art Software
 *  Restricted usage under Binpress license
 *
 *  For more details go to: http://cyborg.binpress.com/product/cyborg/2052
 */

package com.nu.art.software.cyborg.demo.ui.controllers.dynamicFragmentManagement;

import android.view.View;

import com.nu.art.software.cyborg.annotations.Restorable;
import com.nu.art.software.cyborg.annotations.ViewIdentifier;
import com.nu.art.software.cyborg.common.consts.ViewListener;
import com.nu.art.software.cyborg.core.CyborgController;
import com.nu.art.software.cyborg.core.FragmentTransactionConfig;
import com.nu.art.software.cyborg.core.FragmentTransactionConfig.TransactionType;
import com.nu.art.software.cyborg.demo.R;

@SuppressWarnings("unused")
public class DynamicFragmentManagementController
		extends CyborgController {


	@ViewIdentifier(viewIds = {R.id.AddA, R.id.AddB, R.id.AddC, R.id.RemoveA, R.id.RemoveB, R.id.RemoveC, R.id.ReplaceA2B, R.id.ReplaceA2C, R.id.ReplaceB2C,
			R.id.ReplaceB2A, R.id.ReplaceC2A, R.id.ReplaceC2B}, listeners = ViewListener.OnClick)
	private View[] views;

	@Restorable
	private String toSave;

	private String notToSave;

	FragmentTransactionConfig transactionConfig = new FragmentTransactionConfig();

	public DynamicFragmentManagementController() {
		super(R.layout.v1_controller__fragment_management_a);
		transactionConfig.setParentId(R.id.ParentLayoutId);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.AddA:
				transactionConfig.setControllerType(DynamicAController.class);
				transactionConfig.setType(TransactionType.Add);
				transactionConfig.setTag("TagA");
				break;
			case R.id.AddB:
				transactionConfig.setControllerType(DynamicBController.class);
				transactionConfig.setType(TransactionType.Add);
				transactionConfig.setTag("TagB");
				break;
			case R.id.AddC:
				transactionConfig.setControllerType(DynamicCController.class);
				transactionConfig.setType(TransactionType.Add);
				transactionConfig.setTag("TagC");
				break;
			case R.id.RemoveA:
				transactionConfig.setControllerType(DynamicAController.class);
				transactionConfig.setType(TransactionType.Remove);
				transactionConfig.setTag("TagA");
				break;
			case R.id.RemoveB:
				transactionConfig.setControllerType(DynamicBController.class);
				transactionConfig.setType(TransactionType.Remove);
				transactionConfig.setTag("TagB");
				break;
			case R.id.RemoveC:
				transactionConfig.setControllerType(DynamicCController.class);
				transactionConfig.setType(TransactionType.Remove);
				transactionConfig.setTag("TagC");
				break;
			case R.id.ReplaceA2B:
				break;
			case R.id.ReplaceA2C:
				break;
			case R.id.ReplaceB2C:
				break;
			case R.id.ReplaceB2A:
				break;
			case R.id.ReplaceC2A:
				break;
			case R.id.ReplaceC2B:
				break;
		}
		executeFragmentConfig(transactionConfig);
	}
}
