package com.gnts.pem.util.iterator;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ComponentIterOwnerDetails extends HorizontalLayout{

	private VerticalLayout formLayout=new VerticalLayout();
	private TextField tfOwnerName=new TextField("Owner Name");
	private TextArea tfOwnerAddr=new TextArea("Owner Address");
	private String strWidth="200px";
	
	
	public String getOwnerName(){
		return tfOwnerName.getValue();
	}
	
	public String getOwnerAddr(){
		return tfOwnerAddr.getValue();
	}
	public ComponentIterOwnerDetails(String name,String address){
//	
//		if(name==null||name.trim().length()==0){
//			tfOwnerName.setValue("Sri.");
//		}else{
//		tfOwnerName.setValue(name);
//		}
		tfOwnerName.setValue(name);
		tfOwnerAddr.setValue(address);
		
		tfOwnerName.setWidth(strWidth);
		tfOwnerAddr.setWidth(strWidth);
		tfOwnerAddr.setHeight("150px");
		
		//tfOwnerName.setInputPrompt("Owner Name");
		//tfOwnerAddr.setInputPrompt("");
		
		tfOwnerName.setNullRepresentation("");
		tfOwnerAddr.setNullRepresentation("");
		
		formLayout.setSpacing(true);
		formLayout.addComponent(tfOwnerName);
		formLayout.addComponent(tfOwnerAddr);
		addComponent(formLayout);	
	}
	
}
