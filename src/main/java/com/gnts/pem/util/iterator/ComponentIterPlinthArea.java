package com.gnts.pem.util.iterator;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class ComponentIterPlinthArea extends HorizontalLayout{

	
private GridLayout formLayout=new GridLayout();

private TextField tfGroundfloor=new TextField("");
private TextField tfAsperplan=new TextField("As per Plan");
private TextField tfAsAtSite=new TextField("As at Site");
private String strWidth="200px";

public String getGroundFloor()
{
	return tfGroundfloor.getValue();
}

public String getAsPerPlan()
{
	
	return  tfAsperplan.getValue();
}

public String getAsatSite()
{
	return tfAsAtSite.getValue();
}

public ComponentIterPlinthArea(String floor,String asperplan,String asatsite)
{
	
	tfGroundfloor.setValue(floor);
	tfAsperplan.setValue(asperplan);
	tfAsAtSite.setValue(asatsite);
	
	tfGroundfloor.setWidth(strWidth);
	tfAsperplan.setWidth(strWidth);
	tfAsAtSite.setWidth(strWidth);
	
	tfGroundfloor.setInputPrompt("Floor");
	tfAsperplan.setInputPrompt("As per plan");
	tfAsAtSite.setInputPrompt("As at site");
	
	tfGroundfloor.setNullRepresentation("");
	tfAsperplan.setNullRepresentation("");
	tfAsAtSite.setNullRepresentation("");
	
	formLayout.setColumns(4);
	formLayout.setSpacing(true);
	formLayout.addComponent(tfGroundfloor);
	formLayout.addComponent(tfAsperplan);
	formLayout.addComponent(tfAsAtSite);
	addComponent(formLayout);
	
}
}



