
package com.nu.art.cyborg.demo.ui.controllers.customAttributeExample;

import android.content.res.TypedArray;

import com.nu.art.cyborg.demo.R;
import com.nu.art.cyborg.modules.AttributeModule.AttributesSetter;

/**
 * Created by TacB0sS on 10-Aug 2015.
 *
 * @author TacB0sS
 */
public class AttributeSetterV1_CustomValue
	extends AttributesSetter<AttributeSetterV1_CustomValueInterface> {

	private static int[] ids = new int[]{R.styleable.CustomAttribute_example};

	public AttributeSetterV1_CustomValue() {
		super(AttributeSetterV1_CustomValueInterface.class, R.styleable.CustomAttribute, ids);
	}

	@Override
	protected void setAttribute(AttributeSetterV1_CustomValueInterface instance, TypedArray a, int attr) {
		if (attr == R.styleable.CustomAttribute_example) {
			String customValue = a.getString(attr);
			instance.setAttributeValue(customValue == null ? "No Value Set" : customValue);
		}
	}
}
