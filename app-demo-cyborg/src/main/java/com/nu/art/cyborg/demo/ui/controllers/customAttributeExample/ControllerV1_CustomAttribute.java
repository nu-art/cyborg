

package com.nu.art.cyborg.demo.ui.controllers.customAttributeExample;

import android.widget.TextView;

import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;
import com.nu.art.reflection.annotations.ReflectiveInitialization;

@ReflectiveInitialization
public class ControllerV1_CustomAttribute
	extends CyborgController
	implements AttributeSetterV1_CustomValueInterface {

	@ViewIdentifier(viewId = R.id.AttributeValue)
	TextView attributeValue;

	ControllerV1_CustomAttribute() {
		super(R.layout.controller__custom_attribute);
	}

	@Override
	public final void setAttributeValue(String value) {
		attributeValue.setText(value);
	}
}
