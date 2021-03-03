

package com.nu.art.cyborg.payment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nu.art.cyborg.common.implementors.TextWatcherImpl;
import com.nu.art.cyborg.payment.interfaces.CreditCardType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by TacB0sS on 22-Aug 2016.
 */

public class CreditCardView
	extends LinearLayout {

	private final InputFilter[] noFilters = {};

	private CreditCardType creditCard = CreditCard.Unknown;

	private EditText cardDateMonth;

	private EditText cardDateYear;

	private EditText cardNumber;

	private EditText cardCVV;

	private EditText cardOwnerName;

	private ImageView cardTypeLogo;

	private ArrayList<CreditCardType> cardTypes = new ArrayList<>();

	public CreditCardView(Context context) {
		super(context);
		init();
	}

	public CreditCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CreditCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(VERSION_CODES.LOLLIPOP)
	public CreditCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void render() {
		cardTypeLogo.setImageResource(creditCard.getCardLogoId());
	}

	private void init() {
		cardTypes.addAll(Arrays.asList(CreditCard.values()));
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.custom_v1__credit_card, this, true);
		cardNumber = (EditText) findViewById(R.id.Input_CreditCardNumber);
		cardDateMonth = (EditText) findViewById(R.id.Input_CreditCardDateMonth);
		cardDateYear = (EditText) findViewById(R.id.Input_CreditCardDateYear);
		cardOwnerName = (EditText) findViewById(R.id.Input_CardOwnerName);
		cardTypeLogo = (ImageView) findViewById(R.id.Image_CreditCardLogo);
		cardCVV = (EditText) findViewById(R.id.Input_CreditCardCVV);
		cardNumber.addTextChangedListener(new CreditCardNumberWatcher());
		cardCVV.addTextChangedListener(new CreditCardCVVWatcher());
		cardDateMonth.addTextChangedListener(new CreditCardDateMonthWatcher());
		cardDateYear.addTextChangedListener(new CreditCardDateYearWatcher());
		render();
	}

	private String getCreditCardNumber() {
		return null;
	}

	private boolean verifyCreditCard(String creditCardNumber) {
		char[] chars = new StringBuilder(creditCardNumber).reverse().toString().toCharArray();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < chars.length; i++) {
			sb.append(i % 2 == 0 ? chars[i] - 48 : chars[i] * 2);
		}
		chars = sb.toString().toCharArray();

		int sum = 0;
		for (char aChar : chars) {
			sum += aChar - 48;
		}

		return sum % 10 == 0;
	}

	private class CreditCardNumberWatcher
		implements TextWatcher {

		private int cursorIndex;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			Log.d(getClass().getSimpleName(), "beforeTextChanged:'" + s.toString() + "' " + "[" + start + " - " + count + " - " + after + "] ");
		}

		StringBuilder sb = new StringBuilder();

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String temp = s.toString();
			//			logger.logDebug("onTextChanged: '" + temp + "' " + "[" + start + "-" + count + "-" + before + "]");
			cursorIndex = start + count;

			int index = 0;

			while ((index = temp.indexOf("  ", index + 1)) != -1 && index < start) {
				cursorIndex -= 2;
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			sb.setLength(0);

			String originalText = s.toString();
			int cursorIndex = this.cursorIndex;

			String tempCardNumber = originalText.replaceAll(" ", "");

			creditCard = getCardType(tempCardNumber);

			render();

			if (tempCardNumber.length() > 16)
				tempCardNumber = tempCardNumber.substring(0, 16);

			int index = 0;
			while (index < tempCardNumber.length()) {
				int nextIndex = tempCardNumber.length() >= index + 4 ? index + 4 : tempCardNumber.length();
				sb.append(tempCardNumber.substring(index, nextIndex));

				if (nextIndex - index == 4 && nextIndex < tempCardNumber.length()) {
					sb.append("  ");
					if (index < cursorIndex && this.cursorIndex - index > 4)
						cursorIndex += 2;
				}

				index = nextIndex;
			}
			//			logger.logDebug("afterTextChanged: '" + originalText + "' - cursorIndex: " + cursorIndex);

			final String finalValue = sb.toString();
			if (cursorIndex > finalValue.length())
				cursorIndex = finalValue.length();

			final int finalCursorIndex = cursorIndex;

			setValue(cardNumber, this, s, finalValue);
			cardNumber.setSelection(finalCursorIndex);

			if (tempCardNumber.length() == 16)
				cardCVV.requestFocus();
		}
	}

	private CreditCardType getCardType(String creditCardNumber) {
		for (CreditCardType creditCard : cardTypes) {
			if (!creditCardNumber.matches(creditCard.getRegex()))
				continue;

			return creditCard;
		}
		return CreditCard.Unknown;
	}

	private class CreditCardCVVWatcher
		extends TextWatcherImpl {

		@Override
		public void afterTextChanged(Editable s) {
			String cvv = s.toString();
			if (cvv.length() > creditCard.getCvvLength())
				setValue(cardCVV, this, s, cvv.substring(0, creditCard.getCvvLength()));

			if (s.length() == creditCard.getCvvLength())
				cardDateMonth.requestFocus();
		}
	}

	private class CreditCardDateMonthWatcher
		extends TextWatcherImpl {

		@Override
		public void afterTextChanged(Editable s) {
			String dateAsString = s.toString();
			if (dateAsString.length() == 0)
				return;

			dateAsString = dateAsString.replace("/", "");
			if (dateAsString.length() > 2)
				dateAsString = dateAsString.substring(0, 2);

			char[] chars = dateAsString.toCharArray();
			String month = null;
			if (chars.length == 1) {
				int num1 = chars[0] - 48;
				if (num1 > 1)
					month = "0" + num1;
			} else if (chars.length == 2) {
				int num1 = chars[0] - 48;
				int num2 = chars[1] - 48;
				if (num1 > 1)
					month = "0" + num1;
				else if (num1 == 1 && num2 <= 2)
					month = "1" + num2;
				else
					month = "0" + num2;
			}

			if (month == null)
				return;

			setValue(cardDateMonth, this, s, month);
			if (month.length() == 2)
				cardDateYear.requestFocus();
		}
	}

	private class CreditCardDateYearWatcher
		extends TextWatcherImpl {

		@Override
		public void afterTextChanged(Editable s) {
			String year = s.toString();
			if (year.length() == 0)
				return;

			if (year.length() > 2)
				year = year.substring(0, 2);

			setValue(cardDateYear, this, s, year);
			if (year.length() == 2)
				cardOwnerName.requestFocus();
		}
	}

	private void setValue(EditText editText, TextWatcher textWatcher, Editable s, String toSet) {
		editText.removeTextChangedListener(textWatcher);
		InputFilter[] filters = s.getFilters(); // save filters
		s.clear();
		s.setFilters(noFilters);     // clear filters
		s.append(toSet);
		s.setFilters(filters);                  // restore filters
		editText.addTextChangedListener(textWatcher);
	}
}
