package com.gnts.pem.util.iterator;

import java.util.List;

import com.gnts.erputil.Common;


import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ComponentIterMiscellaneous extends HorizontalLayout{
	private CmBankConstantService beanBankCnst=(CmBankConstantService)SpringContextHelper.getBean("bankConstant");
	private GridLayout formLayout=new GridLayout();
	
	private ComboBox slMiscellaneous=new ComboBox("Miscellaneous");
	private TextField tfMisAmount=new TextField("Amount");
	private String strWidth="200px";
	Long selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
			.getAttribute("loginCompanyId").toString());
	
	public String getMiscellaneous()
	{
		if(slMiscellaneous.getValue()!=null)
		{
			slMiscellaneous.setComponentError(null);
		return  (String) slMiscellaneous.getValue();
		
		}
		else
		{
			return null;
		}
	}
	
	public String getItemValue()
	{
		if(tfMisAmount.getValue()!=null)
		{
			tfMisAmount.setComponentError(null);
		return tfMisAmount.getValue();
		
		}
		else
		{
			return null;
		}
	}

	
	public ComponentIterMiscellaneous(String miscellItem,String itemAmount)
	{
		
		List<String> list = beanBankCnst.getBankConstantList("MISCELL",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(String.class);
		childAccounts.addAll(list);
		slMiscellaneous.setContainerDataSource(childAccounts);

		if(itemAmount==null||itemAmount.trim().length()==0){
			tfMisAmount.setValue("0");
		}else{
			tfMisAmount.setValue(itemAmount);
		}
		slMiscellaneous.setValue(miscellItem);

		slMiscellaneous.setWidth(strWidth);
		tfMisAmount.setWidth(strWidth);
		
		slMiscellaneous.setInputPrompt(Common.SELECT_PROMPT);
		tfMisAmount.setInputPrompt("Amount");
		slMiscellaneous.setNullSelectionAllowed(false);
		tfMisAmount.setNullRepresentation("");
		
		formLayout.setColumns(4);
		formLayout.setSpacing(true);
		formLayout.addComponent(slMiscellaneous);
		formLayout.addComponent(tfMisAmount);
		addComponent(formLayout);
		
	}


}
