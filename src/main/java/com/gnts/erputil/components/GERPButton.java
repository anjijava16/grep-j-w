package com.gnts.erputil.components;

import com.vaadin.ui.Button;

public class GERPButton extends Button{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GERPButton(String caption,String stylename,ClickListener listener){
		setCaption(caption);
		addStyleName(stylename);
		addClickListener(listener);
	}
	public GERPButton(String caption,String stylename){
		setCaption(caption);
		addStyleName(stylename);
	}
}
