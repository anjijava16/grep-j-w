package com.gnts.erputil.components;

import com.vaadin.ui.TextField;

public class GERPTextField extends TextField{
private static final long serialVersionUID = 1L;

	public GERPTextField(String caption){
		setWidth("150px");	
		setCaption(caption);
		setRequired(false);
		setNullRepresentation("");
	}
	
}
