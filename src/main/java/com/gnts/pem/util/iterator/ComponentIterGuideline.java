package com.gnts.pem.util.iterator;

import java.math.BigDecimal;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class ComponentIterGuideline extends HorizontalLayout {

	
private GridLayout formLayout=new GridLayout();

private TextField tfDescription=new TextField("");
private TextField tfArea=new TextField("Area(sft)");
private TextField tfRate=new TextField("Rate(sft)");
private TextField tfAmount=new TextField("Amount");
private String strWidth="200px";

public String getDescription()
{
	return tfDescription.getValue();
}

public String getArea()
{
	return tfArea.getValue();
	}

public String getRate()
{
	
	return  tfRate.getValue();
}

public String getAmount()
{
	return tfAmount.getValue();
}

public ComponentIterGuideline(String description,String area,String rate,String amount)
{
	
	tfDescription.setValue(description);
	if(area==null||area.trim().length()==0){
		tfArea.setValue("0");
	}else{
		tfArea.setValue(area);
	}
	if(rate==null||rate.trim().length()==0){
		tfRate.setValue("0");
	}else{
		tfRate.setValue(rate);
	}
	if(amount==null||amount.trim().length()==0){
		tfAmount.setValue("0");
	}else{
		tfAmount.setValue(amount);
	}
	
	tfDescription.setWidth(strWidth);
	tfArea.setWidth(strWidth);
	tfRate.setWidth(strWidth);
	tfAmount.setWidth(strWidth);
	
	tfDescription.setNullRepresentation("");
	tfArea.setNullRepresentation("");
	tfRate.setNullRepresentation("");
	tfAmount.setNullRepresentation("");
	
	tfRate.setImmediate(true);
	tfRate.addBlurListener(new BlurListener() {
	private static final long serialVersionUID = 1L;

		@Override
		public void blur(BlurEvent event) {
			try {
				BigDecimal area = new BigDecimal(tfArea.getValue());
				BigDecimal rate = new BigDecimal(tfRate.getValue());
				tfAmount.setValue(area.multiply(rate).toString());
			} catch (Exception e) {
			}
		}
	});
	formLayout.setColumns(4);
	formLayout.setSpacing(true);
	formLayout.addComponent(tfDescription);
	formLayout.addComponent(tfArea);
	formLayout.addComponent(tfRate);
	formLayout.addComponent(tfAmount);
	

	addComponent(formLayout);
	
}
}
