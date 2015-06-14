package com.gnts.erputil.validations;

import com.vaadin.data.Validator;

public class IntegerValidation implements Validator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4814993005469928071L;
	private String errMsg;
	
	public IntegerValidation(String errorMessage) {
		errMsg = errorMessage;
	}
	
	public boolean isValid(Object value) {
		if (value != null || !(value instanceof Integer)) {
			return false;
		} else {
			return ((String) value).matches("^[0-9]+$");
		}
	}
	
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException(errMsg);
		}
	}
}
