package com.gnts.pem.util.iterator;

import java.util.List;

import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ComponentIerServices extends HorizontalLayout{
private static final long serialVersionUID = 1L;
private CmBankConstantService beanBankCnst=(CmBankConstantService)SpringContextHelper.getBean("bankConstant");
	private GridLayout formLayout=new GridLayout();
	
	
	private ComboBox slServices=new ComboBox("Service");
	private TextField tfServiceAmount=new TextField("Amount");
	private String strWidth="200px";
	
	public String getService()
	{
		if(slServices.getValue()!=null)
		{
			slServices.setComponentError(null);
		return  (String) slServices.getValue();
		
		}
		else
		{
			return null;
		}
	}
	
	public String getServiceValue()
	{
		if(tfServiceAmount.getValue()!=null)
		{
			tfServiceAmount.setComponentError(null);
		return tfServiceAmount.getValue();
		
		}
		else
		{
			return null;
		}
	}

	
	public ComponentIerServices(String service,String itemAmount)
	{
		Long selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		
		List<String> list = beanBankCnst.getBankConstantList("SERVICE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(String.class);
		childAccounts.addAll(list);
		slServices.setContainerDataSource(childAccounts);
	
		if(itemAmount==null||itemAmount.trim().length()==0){
			tfServiceAmount.setValue("0");
		}else{
			tfServiceAmount.setValue(itemAmount);
		}

		slServices.setValue(service);
		slServices.setInputPrompt(Common.SELECT_PROMPT);
		slServices.setWidth(strWidth);
		tfServiceAmount.setWidth(strWidth);
		tfServiceAmount.setNullRepresentation("");
		slServices.setNullSelectionAllowed(false);
		
		formLayout.setColumns(4);
		formLayout.setSpacing(true);
		formLayout.addComponent(slServices);
		formLayout.addComponent(tfServiceAmount);
		addComponent(formLayout);
		
	}


}
