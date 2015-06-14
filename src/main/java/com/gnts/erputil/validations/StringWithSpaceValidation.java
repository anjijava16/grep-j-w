package com.gnts.erputil.validations;

import com.vaadin.data.Validator;

public class StringWithSpaceValidation implements Validator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 834538435618951895L;
	private String errMsg;
	
	public StringWithSpaceValidation(String errorMessage) {
		errMsg = errorMessage;
	}
	
	public boolean isValid(Object value) {
		if (value == null || !(value instanceof String)) {
			return false;
		}
		return ((String) value).matches("^[a-zA-Z /s]*$");
	}
	
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException(errMsg);
		}
	}
}
