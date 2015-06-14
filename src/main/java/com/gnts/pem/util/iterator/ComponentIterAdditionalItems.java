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

public class ComponentIterAdditionalItems extends HorizontalLayout{
	private CmBankConstantService beanBankCnst=(CmBankConstantService)SpringContextHelper.getBean("bankConstant");
	private GridLayout formLayout=new GridLayout();
	
	
	private ComboBox slAdditionalItem=new ComboBox("Additional Item");
	private TextField tfAddAmount=new TextField("Amount");
	private String strWidth="200px";
	
	public String getAdditionalItem()
	{
		if(slAdditionalItem.getValue()!=null)
		{
			slAdditionalItem.setComponentError(null);
		return  (String) slAdditionalItem.getValue();
		
		}
		else
		{
			return null;
		}
	}
	
	public String getItemValue()
	{
		if(tfAddAmount.getValue()!=null)
		{
			tfAddAmount.setComponentError(null);
		return tfAddAmount.getValue();
		
		}
		else
		{
			return null;
		}
	}

	
	public ComponentIterAdditionalItems(String AddItem,String itemAmount)
	{
		Long selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		
		List<String> list = beanBankCnst.getBankConstantList("AI",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(String.class);
		childAccounts.addAll(list);
		slAdditionalItem.setContainerDataSource(childAccounts);

		if(itemAmount==null||itemAmount.trim().length()==0){
			tfAddAmount.setValue("0");
		}else{
			tfAddAmount.setValue(itemAmount);
		}
		slAdditionalItem.setValue(AddItem);
		
		slAdditionalItem.setWidth(strWidth);
		tfAddAmount.setWidth(strWidth);
		
		slAdditionalItem.setInputPrompt(Common.SELECT_PROMPT);
		slAdditionalItem.setNullSelectionAllowed(false);
		tfAddAmount.setNullRepresentation("");
		
		formLayout.setColumns(4);
		formLayout.setSpacing(true);
		formLayout.addComponent(slAdditionalItem);
		formLayout.addComponent(tfAddAmount);
		addComponent(formLayout);
		
	}


}
