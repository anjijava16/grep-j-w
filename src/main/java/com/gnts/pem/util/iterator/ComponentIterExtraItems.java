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

public class ComponentIterExtraItems extends HorizontalLayout {
	private CmBankConstantService beanBankCnst=(CmBankConstantService)SpringContextHelper.getBean("bankConstant");
	private GridLayout formLayout=new GridLayout();
	
	
	private ComboBox slExtraItem=new ComboBox("Extra Item");
	private TextField tfItemAmount=new TextField("Amount");
	private String strWidth="200px";
	
	
	Long selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
			.getAttribute("loginCompanyId").toString());
	
	
	public String getExtraItem()
	{
		if(slExtraItem.getValue()!=null)
		{
			slExtraItem.setComponentError(null);
		return  (String) slExtraItem.getValue();
		
		}
		else
		{
			return null;
		}
	}
	
	public String getItemValue()
	{
		if(tfItemAmount.getValue()!=null)
		{
			tfItemAmount.setComponentError(null);
		return tfItemAmount.getValue();
		
		}
		else
		{
			return null;
		}
	}

	
	public ComponentIterExtraItems(String extraItem,String itemAmount)
	{
		
		List<String> list = beanBankCnst.getBankConstantList("EI",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(String.class);
		childAccounts.addAll(list);
		slExtraItem.setContainerDataSource(childAccounts);

		if(itemAmount==null||itemAmount.trim().length()==0){
			tfItemAmount.setValue("0");
		}else{
			tfItemAmount.setValue(itemAmount);
		}
		slExtraItem.setInputPrompt(Common.SELECT_PROMPT);
		slExtraItem.setValue(extraItem);
		
		slExtraItem.setWidth(strWidth);
		tfItemAmount.setWidth(strWidth);
		
		slExtraItem.setNullSelectionAllowed(false);
		tfItemAmount.setNullRepresentation("");
		
		formLayout.setColumns(4);
		formLayout.setSpacing(true);
		formLayout.addComponent(slExtraItem);
		formLayout.addComponent(tfItemAmount);
		addComponent(formLayout);
		
	}
}
