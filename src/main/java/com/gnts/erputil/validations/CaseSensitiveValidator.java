package com.gnts.erputil.validations;

import com.vaadin.data.Validator;

public class CaseSensitiveValidator implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3066587487487835808L;
	private String errMsg;
	
	public CaseSensitiveValidator(String errorMessage) {
		errMsg = errorMessage;
	}
	
	public boolean isValid(Object value) {
		if (value == null || !(value instanceof String)) {
			return false;
		}
		return ((String) value).matches("[a-zA-Z]{9}");
	}
	
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException(errMsg);
		}
	}
}
