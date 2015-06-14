/**
 * File Name 		: ERPBaseException.java 
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

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class ERPBaseException extends Exception{
	//	Declarations
	private static final long serialVersionUID = 1L;
	private String errorCd="SYSTEM_ERROR";
	
	// Default constructor
	public ERPBaseException(){
        super();
	}	
	
	// Constructor with error code passed. Based on the error code this display 
	// the description in the notification area
	
	public ERPBaseException(String errCode){
        this.errorCd = errCode;
		Label lblNotification = (Label) UI.getCurrent().getSession().getAttribute("lblNotification");
		if(lblNotification!=null){
			lblNotification.setIcon(new ThemeResource("img/failure.png"));
			lblNotification.setCaption(errCode);
		}	        
    }

	// Get method to get the error code
    public String getErrCode() {
        return errorCd;
    }
}
