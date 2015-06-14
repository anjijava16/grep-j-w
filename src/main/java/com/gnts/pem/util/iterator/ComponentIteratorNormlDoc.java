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

public class ComponentIteratorNormlDoc extends HorizontalLayout 
{
	private static final long serialVersionUID = 1L;
	private CmBankConstantService beanBankCnst=(CmBankConstantService)SpringContextHelper.getBean("bankConstant");
	private GridLayout formLayout=new GridLayout();
	
	
	private ComboBox slTypeofDocument=new ComboBox("Document Type");
	private ComboBox slYesorNo=new ComboBox("Yes or No");
	private TextField tfNameofAuthority=new TextField("Name of Authority");
	private TextField tfApprovalNumber=new TextField("Approval Number");
	private String strWidth="200px";
	private String strYesNoWidth="100px";
	private String strWidth1="250px";
	
	
	public String getNameofDocument()
	{
		if(slTypeofDocument.getValue()!=null)
		{
			slTypeofDocument.setComponentError(null);
		return  (String) slTypeofDocument.getValue();
		
		}
		else
		{
			return null;
		}
	}
	
	public String getYesorNo()
	{
		if(slYesorNo.getValue()!=null)
		{
			slYesorNo.setComponentError(null);
		return  (String) slYesorNo.getValue();
		
		}
		else
		{
			return null;
		}
	}
	
	public String getNameofAuthority()
	{
		if(tfNameofAuthority.getValue()!=null)
		{
			tfNameofAuthority.setComponentError(null);
		return  (String) tfNameofAuthority.getValue();
		
		}
		else
		{
			return null;
		}
	}
	
	public String getApprovalNo()
	{
		if(tfApprovalNumber.getValue()!=null)
		{
			tfApprovalNumber.setComponentError(null);
		return  (String) tfApprovalNumber.getValue();
		
		}
		else
		{
			 
			return null;
		}
	}
	
	public ComponentIteratorNormlDoc(String docname,String yesno,String approveauth,String approvalno)
	{
		Long selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		List<String> list = beanBankCnst.getBankConstantList("DOCUMENT_TYPE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(String.class);
		childAccounts.addAll(list);
		slTypeofDocument.setContainerDataSource(childAccounts);
		
		slYesorNo.setNullSelectionAllowed(false);
		slYesorNo.addItem("Yes");
		slYesorNo.addItem("No");
		
		slTypeofDocument.setValue(docname);
		slYesorNo.setValue(yesno);
		tfNameofAuthority.setValue(approveauth);
		tfApprovalNumber.setValue(approvalno);
		
		slTypeofDocument.setWidth(strWidth);
		slYesorNo.setWidth(strYesNoWidth);
		tfNameofAuthority.setWidth(strWidth1);
		tfApprovalNumber.setWidth(strWidth1);
		
		slTypeofDocument.setInputPrompt("Document");
		slYesorNo.setInputPrompt(Common.SELECT_PROMPT);
		//tfNameofAuthority.setInputPrompt("Authority Name");
		//tfApprovalNumber.setInputPrompt("Approval No");
		
		slTypeofDocument.setNullSelectionAllowed(false);
		slYesorNo.setNullSelectionAllowed(false);
		tfNameofAuthority.setNullRepresentation("");
		tfApprovalNumber.setNullRepresentation("");
		
		slTypeofDocument.setInputPrompt(Common.SELECT_PROMPT);
		
		formLayout.setColumns(4);
		formLayout.setSpacing(true);
		formLayout.addComponent(slTypeofDocument);
		formLayout.addComponent(slYesorNo);
		formLayout.addComponent(tfNameofAuthority);
		formLayout.addComponent(tfApprovalNumber);
		addComponent(formLayout);
		
	}
}
