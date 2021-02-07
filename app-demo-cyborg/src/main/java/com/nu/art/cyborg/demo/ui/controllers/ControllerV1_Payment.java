

package com.nu.art.cyborg.demo.ui.controllers;

import com.nu.art.cyborg.payment.CreditCardView;
import com.nu.art.cyborg.annotations.ViewIdentifier;
import com.nu.art.cyborg.core.CyborgController;
import com.nu.art.cyborg.demo.R;

/**
 * Created by TacB0sS on 24-Aug 2016.
 */

public class ControllerV1_Payment
	extends CyborgController {

	@ViewIdentifier(viewId = R.id.CreditCardView)
	private CreditCardView creditCardView;

	public ControllerV1_Payment() {
		super(R.layout.controller__payment);
	}
}
