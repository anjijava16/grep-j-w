package com.gnts.erputil.validations;

import java.util.Date;
import com.vaadin.data.Validator;

public class DateValidation implements Validator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6557253545928133196L;
	private String errMsg;
	
	public DateValidation(String errorMessage) {
		errMsg = errorMessage;
	}
	
	public void validate(Object value) throws InvalidValueException {
		if (value != null && !(value instanceof Date)) {
			throw new InvalidValueException(errMsg);
		}
	}
}
