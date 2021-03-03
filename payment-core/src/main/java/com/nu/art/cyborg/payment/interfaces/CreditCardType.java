

package com.nu.art.cyborg.payment.interfaces;

/**
 * Created by TacB0sS on 28-Aug 2016.
 */
public interface CreditCardType {

	String getRegex();

	int getCvvLength();

	int getCardLogoId();
}
