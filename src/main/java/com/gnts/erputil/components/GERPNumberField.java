package com.gnts.erputil.components;

import com.vaadin.ui.TextField;

public class GERPNumberField extends TextField {
	private static final long serialVersionUID = 1L;
	
	public GERPNumberField(String caption) {
		setWidth("150px");
		setCaption(caption);
		setRequired(false);
		setNullRepresentation("");
		setStyleName("textalignright");
	}
	public GERPNumberField() {
		setWidth("150px");
		setRequired(false);
		setNullRepresentation("");
		setStyleName("textalignright");
	}
}
