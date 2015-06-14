package com.gnts.pem.util.iterator;

import java.math.BigDecimal;

import com.gnts.pem.util.list.ValuationDetailsList;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class ComponenetIterValuationDetails extends HorizontalLayout{
private static final long serialVersionUID = 1L;

	private GridLayout formLayout=new GridLayout();
	
	private TextField tfFloor=new TextField();
	private TextField tfPlinthArea=new TextField();
	private TextField tfRoofHt=new TextField();
	private TextField tfBuildAge=new TextField();
	private TextField tfRate=new TextField();
	private TextField tfReplaceCost=new TextField();
	private TextField tfDepreciation=new TextField();
	private TextField tfNetValue=new TextField();
	private String strWidth="100px";
	private String strLblWidth="75px";
	
	public ValuationDetailsList getValuationDtlsList(){
		ValuationDetailsList obj=new ValuationDetailsList();
		obj.setFloorDtlsLabel(tfFloor.getValue());
		obj.setPlinthAreaLabel(tfPlinthArea.getValue());
		obj.setRoofHtLabel(tfRoofHt.getValue());
		obj.setBuildAgeLabel(tfBuildAge.getValue());
		obj.setRateLabel(tfRate.getValue());
		obj.setReplaceLabel(tfReplaceCost.getValue());
		obj.setDepreciationLabel(tfDepreciation.getValue());
		obj.setNetValueLabel(tfNetValue.getValue());
		return obj;
	}
	
	public ComponenetIterValuationDetails(ValuationDetailsList obj)
	{
		if(obj!=null){
		if(obj.getFloorDtlsLabel()!=null){
			tfFloor.setValue(obj.getFloorDtlsLabel());
		}
		else{
			tfFloor.setValue("");
		}
		if(obj.getPlinthAreaLabel()!=null){
	tfPlinthArea.setValue(obj.getPlinthAreaLabel());
		}else{
			tfPlinthArea.setValue("");
		}
		if(obj.getRoofHtLabel()!=null){
	tfRoofHt.setValue(obj.getRoofHtLabel());
		}else{
			tfRoofHt.setValue("");
		}
		if(obj.getBuildAgeLabel()!=null){
	tfBuildAge.setValue(obj.getBuildAgeLabel());
	}
		else{
			tfBuildAge.setValue("");
		}
		if(obj.getRateLabel()!=null){
	tfRate.setValue(obj.getRateLabel());
		}else{
			tfRate.setValue("");
		}
		if(obj.getReplaceLabel()!=null){
	tfReplaceCost.setValue(obj.getReplaceLabel());
		}else{
			tfReplaceCost.setValue("");
		}
		if(obj.getDepreciationLabel()!=null){
	tfDepreciation.setValue(obj.getDepreciationLabel());
		}else{
			tfDepreciation.setValue("");
		}
		if(obj.getNetValueLabel()!=null){
	tfNetValue.setValue(obj.getNetValueLabel());
		}else{
			tfNetValue.setValue("");
		}
		}
	tfFloor.setWidth(strWidth);
	tfPlinthArea.setWidth(strLblWidth);
	tfRoofHt.setWidth(strLblWidth);
	tfBuildAge.setWidth(strWidth);
	tfRate.setWidth(strWidth);
	tfReplaceCost.setWidth(strWidth);
	tfDepreciation.setWidth(strWidth);
	tfNetValue.setWidth(strWidth);
	
	tfFloor.setInputPrompt("Floor");
	tfPlinthArea.setInputPrompt("Plinth area");
	tfRoofHt.setInputPrompt("Roof height");
	tfBuildAge.setInputPrompt("Build Age");
	tfRate.setInputPrompt("Rate");
	tfReplaceCost.setInputPrompt("Replace cost");
	tfDepreciation.setInputPrompt("");
	tfNetValue.setInputPrompt("Net value");
	
	tfFloor.setNullRepresentation("");
	tfPlinthArea.setNullRepresentation("");
	tfRoofHt.setNullRepresentation("");
	tfBuildAge.setNullRepresentation("");
	tfRate.setNullRepresentation("");
	tfReplaceCost.setNullRepresentation("");
	tfDepreciation.setNullRepresentation("");
	tfNetValue.setNullRepresentation("");
		
	tfDepreciation.setImmediate(true);
	tfDepreciation.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;

			@Override
			public void blur(BlurEvent event) {
				try {
					
					BigDecimal netvalue=new BigDecimal(tfReplaceCost.getValue());
					netvalue=netvalue.subtract(new BigDecimal(tfDepreciation.getValue()));
					tfNetValue.setValue(netvalue.toString());
					
				} catch (Exception e) {
				}
			}
		});
		formLayout.setColumns(8);
		formLayout.setSpacing(true);
		formLayout.addComponent(tfFloor);
		formLayout.addComponent(tfPlinthArea);
		formLayout.addComponent(tfRoofHt);
		formLayout.addComponent(tfBuildAge);
		formLayout.addComponent(tfRate);
		formLayout.addComponent(tfReplaceCost);
		formLayout.addComponent(tfDepreciation);
		formLayout.addComponent(tfNetValue);
		

		addComponent(formLayout);
		
	}
}
