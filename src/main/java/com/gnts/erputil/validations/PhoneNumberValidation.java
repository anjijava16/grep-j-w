package com.gnts.erputil.validations;

import com.gnts.erputil.constants.GERPErrorCodes;
import com.vaadin.data.Validator;

public class PhoneNumberValidation implements Validator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7820113209799601241L;
	private String errMsg;
	
	public PhoneNumberValidation() {
		errMsg = GERPErrorCodes.PHONE_NUMBER_VALIDATION;
	}
	
	public boolean isValid(Object value) {
		if (value == null || !(value instanceof String)) {
			return false;
		}
		return ((String) value).matches("^\\+?[0-9. ()-]{10,25}$");
	}
	
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException(errMsg);
		}
	}
}
