package com.gnts.pem.util.iterator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.validations.DateUtils;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ComponentIteratorLegalDoc extends HorizontalLayout {
	private static final long serialVersionUID = 1L;
	private CmBankConstantService beanBankCnst=(CmBankConstantService)SpringContextHelper.getBean("bankConstant");
	private GridLayout formLayout = new GridLayout();

	private ComboBox tfDocumentName = new ComboBox("Document Name");
	private TextField tfReferenceNumber = new TextField("Document Number");
	private PopupDateField dfApprovalDate = new PopupDateField("Approval Date");

	private String strWidth = "200px";

	public String getNameofDocument() {
		
		if (tfDocumentName.getValue() != null) {
			tfDocumentName.setComponentError(null);
			return (String) tfDocumentName.getValue();

		} else {
				return null;
		}
	}

	public String getReferenceNumber() {
		if (tfReferenceNumber.getValue() != null) {
			tfReferenceNumber.setComponentError(null);
			return (String) tfReferenceNumber.getValue();

		} else {
				return null;
		}
	}

	public String getApprovalDate() {
		if (dfApprovalDate.getValue() != null) {
			dfApprovalDate.setComponentError(null);
			return DateUtils.datetostring(dfApprovalDate.getValue());

		} else {
				return null;
		}
	}

	public ComponentIteratorLegalDoc(String docname, String referanceno,
			String date) {
		Long selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		List<String> list = beanBankCnst.getBankConstantList("LEGAL",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfDocumentName.setContainerDataSource(childAccounts);
		
		tfDocumentName.setNullSelectionAllowed(false);
		tfReferenceNumber.setNullRepresentation("");
		
		System.out.println("DocumentName-->"+tfDocumentName.getValue());
		
		tfDocumentName.setValue(docname);
		tfReferenceNumber.setValue(referanceno);
		if (date != null) {
			try {
				DateFormat formatter = new SimpleDateFormat(
						"yyyy-mm-dd HH:mm:ss.SSSSSS");
				Date date1 = (Date) formatter.parse(date);
				dfApprovalDate.setValue(date1);
			} catch (Exception e) {
				
			}
		}
		dfApprovalDate.setDateFormat("dd-MMM-yyy");
		tfDocumentName.setInputPrompt(Common.SELECT_PROMPT);
		//tfReferenceNumber.setInputPrompt("Reference number");
		//dfApprovalDate.setInputPrompt("Approval Date");
		
		tfDocumentName.setWidth(strWidth);
		tfReferenceNumber.setWidth(strWidth);
		dfApprovalDate.setWidth(strWidth);

		formLayout.setColumns(4);
		formLayout.setSpacing(true);
		formLayout.addComponent(tfDocumentName);
		formLayout.addComponent(tfReferenceNumber);
		formLayout.addComponent(dfApprovalDate);
		addComponent(formLayout);
	}

}
