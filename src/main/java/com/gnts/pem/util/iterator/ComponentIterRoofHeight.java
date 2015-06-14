package com.gnts.pem.util.iterator;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class ComponentIterRoofHeight extends HorizontalLayout {

	
private GridLayout formLayout=new GridLayout();

private TextField tfGroundfloor=new TextField("Floor");
private TextField tfRoofHt=new TextField("Roof Height");
private String strWidth="200px";

public String getGroundFloor()
{
	return tfGroundfloor.getValue();
}

public String getRoofHeight()
{
	return tfRoofHt.getValue();
	}
public ComponentIterRoofHeight(String floor,String roofHeight)
{
	
	tfGroundfloor.setValue(floor);
	tfRoofHt.setValue(roofHeight);
	
	
	tfGroundfloor.setWidth(strWidth);
	tfRoofHt.setWidth(strWidth);


	tfGroundfloor.setNullRepresentation("");
	tfRoofHt.setNullRepresentation("");
	
	tfGroundfloor.setInputPrompt("Floor");
	tfRoofHt.setInputPrompt("Roof height");
	formLayout.setColumns(2);
	formLayout.setSpacing(true);
	formLayout.addComponent(tfGroundfloor);
	formLayout.addComponent(tfRoofHt);
	

	addComponent(formLayout);
	
}
}
