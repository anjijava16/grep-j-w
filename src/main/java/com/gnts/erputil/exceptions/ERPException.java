/**
 * File Name 		: ERPException.java 
 * Description 		: This class is used to handle different types of exceptions
 * Author 			: SK
 * Date 			: Jun 15, 2014
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          15-Jun-2014        	SK		        		Initial Version
 */

package com.gnts.erputil.exceptions;

import com.gnts.erputil.constants.GERPExceptionCodes;

public class ERPException extends ERPBaseException {

	private static final long serialVersionUID = 1L;

	// Constructor to unknown exception
	public ERPException() {
		super(GERPExceptionCodes.UNKN_ERROR);
	}
	
	// Subclass to handle unknown database exception
    public static class DBGenericException extends ERPBaseException
    {
        private static final long serialVersionUID = 3555714415375055302L;
        public DBGenericException() {
            super(GERPExceptionCodes.DB_ERROR);
        }
    }

	// Subclass to handle generic validation exception
    public static class ValidationException extends ERPBaseException
    {
        private static final long serialVersionUID = 8777415230393628334L;
        public ValidationException() {
            super(GERPExceptionCodes.INVALID_DATA);
        }
    }
    
    // Subclass to handle generic Save exception
    public static class SaveException extends ERPBaseException
    {
        private static final long serialVersionUID = -3987707665150073980L;
        public SaveException() {
            super(GERPExceptionCodes.SAVE_FAILED);
        }
    }
    
    // Subclass to handle no data found error
    public static class NoDataFoundException extends ERPBaseException
    {
        private static final long serialVersionUID = 4235225697094262603L;
        public NoDataFoundException() {
            super(GERPExceptionCodes.NO_DATA_FOUND);
        }
    }
}
