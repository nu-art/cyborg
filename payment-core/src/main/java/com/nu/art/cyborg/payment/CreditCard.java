

package com.nu.art.cyborg.payment;

import com.nu.art.cyborg.payment.interfaces.CreditCardType;

/**
 * Created by TacB0sS on 23-Aug 2016.
 *
 * http://stackoverflow.com/questions/72768/how-do-you-detect-credit-card-type-based-on-number
 */

public enum CreditCard
	implements CreditCardType {
	AmericanExpress(R.drawable.credit_card_logo__american_express, "^3[47][0-9]{0,13}$", 4),
	Diners(R.drawable.credit_card_logo__diners, "^3(?:0[0-5]|[68][0-9])[0-9]{0,11}$"),
	Discover(R.drawable.credit_card_logo__discover, "^6(?:011|5[0-9]{2})[0-9]{0,12}$"),
	Maestro(R.drawable.credit_card_logo__maestro, "^(5018|5020|5038|5612|5893|6304|6759|6761|6762|6763|0604|6390)\\d+$"),
	Mastercard(R.drawable.credit_card_logo__mastercard, "^5[1-5][0-9]{0,14}$"),
	//	Dankort("^(5019)\\d+$"),
	//	Interpayment("^(636)\\d+$"),
	//	Cirrus(R.drawable.credit_card_logo__cirrus, ".*?$"),
	//	Delta(R.drawable.credit_card_logo__delta, "^.*?$"),
	//	Solo(R.drawable.credit_card_logo__solo, "^.*?$"),
	WesternUnion(R.drawable.credit_card_logo__western_union, "^(62|88)\\d+$"),
	Visa(R.drawable.credit_card_logo__visa, "^4[0-9]{0,15}$"),
	VisaElectron(R.drawable.credit_card_logo__visa_electron, "^(4026|417500|4405|4508|4844|4913|4917)\\d+$"),
	Jcb(R.drawable.credit_card_logo__jcb, "^(?:2131|1800|35\\d{3})\\d{0,11}$"),
	Unknown(R.drawable.credit_card__icc, ""),
	;

	private final String regex;

	private final int logoId;

	private final int cvvLength;

	CreditCard(int logoId, String regex) {
		this(logoId, regex, 3);
	}

	CreditCard(int logoId, String regex, int cvvLength) {
		this.logoId = logoId;
		this.regex = regex;
		this.cvvLength = cvvLength;
	}

	@Override
	public int getCardLogoId() {
		return logoId;
	}

	@Override
	public String getRegex() {
		return regex;
	}

	@Override
	public int getCvvLength() {
		return cvvLength;
	}
}
