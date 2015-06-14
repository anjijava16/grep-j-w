package com.gnts.erputil.validations;

import com.vaadin.data.Validator;

public class StringValidation implements Validator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8079701078436227922L;
	private String errMsg;
	
	public StringValidation(String errorMessage) {
		errMsg = errorMessage;
	}
	
	public boolean isValid(Object value) {
		if (value == null || !(value instanceof String)) {
			return false;
		}
		return ((String) value).matches("^[0-9a-zA-Z /s]*$");
	}
	
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException(errMsg);
		}
	}
}