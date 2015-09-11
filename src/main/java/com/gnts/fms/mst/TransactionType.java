/**
 * File Name 		: TransactionType.java 
 * Description 		: this class is used for add/edit transaction type details. 
 * Author 			: SOUNDAR C 
 * Date 			: Mar 05, 2014
 * Modification 	:
 * Modified By 		: SOUNDAR C 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 05 2014        SOUNDAR C		          Initial Version
 * 0.2 			 10-Jul-2014		Abdullah.H				  Code Optimization
 */
package com.gnts.fms.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.fms.domain.mst.TransactionTypeDM;
import com.gnts.fms.service.mst.TransactionTypeService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class TransactionType extends BaseUI {
	private static final long serialVersionUID = 1L;
	private TransactionTypeService serviceTransType = (TransactionTypeService) SpringContextHelper.getBean("transtype");
	// form layout for input controls
	private FormLayout formLayout1, formLayout2, formLayout3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add Input fields
	private TextField tfTranstypeName;
	// private ComboBoxs
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private ComboBox cbCreditDebit = new GERPComboBox("Credit/Debit", BASEConstants.M_FMS_TRANS_TYPE,
			BASEConstants.M_FMS_CR_DE);
	// To add Bean Item Container
	private BeanItemContainer<TransactionTypeDM> beanTransactionTypeDM = null;
	private Long companyId;
	private String transtypeid;
	private int recordCnt = 0;
	private String strLoginUserName;
	// Initialize logger
	private Logger logger = Logger.getLogger(TransactionType.class);
	
	// Constructor
	public TransactionType() {
		logger.info("Company ID : " + companyId + " | User Name : " + strLoginUserName + " > "
				+ "Inside TransactionType() constructor");
		strLoginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildView();
	}
	
	private void buildView() {
		// add fields to panel
		tfTranstypeName = new GERPTextField("Transaction Type");
		tfTranstypeName.setMaxLength(30);
		// create form layouts to hold the input items
		formLayout1 = new FormLayout();
		formLayout2 = new FormLayout();
		formLayout3 = new FormLayout();
		// add the user input items into appropriate form layout
		formLayout1.addComponent(tfTranstypeName);
		formLayout2.addComponent(cbCreditDebit);
		formLayout3.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(formLayout1);
		hlUserInputLayout.addComponent(formLayout2);
		hlUserInputLayout.addComponent(formLayout3);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId + ",F"
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + "," + (String) cbStatus.getValue()
					+ ", " + companyId + "Loading Search...");
			List<TransactionTypeDM> list = new ArrayList<TransactionTypeDM>();
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + "," + (String) cbStatus.getValue()
					+ ", " + companyId);
			list = serviceTransType.getTransactionTypeList(companyId, tfTranstypeName.getValue(),
					(String) cbStatus.getValue(), (String) cbCreditDebit.getValue(), null);
			recordCnt = list.size();
			beanTransactionTypeDM = new BeanItemContainer<TransactionTypeDM>(TransactionTypeDM.class);
			beanTransactionTypeDM.addAll(list);
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
					+ "Got the Transaction Type result set");
			tblMstScrSrchRslt.setContainerDataSource(beanTransactionTypeDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "transtypeid", "transtypename", "crdr",
					"transtypestatus", "lastupdateddt", "lastupdatedby", });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Transaction Type", "Credit/Debit", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("transtypeid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editTransactionType() {
		try {
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			if (sltedRcd != null) {
				transtypeid = sltedRcd.getItemProperty("transtypeid").getValue().toString();
				logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
						+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
						+ "Selected Transaction Type Id -> " + transtypeid);
				tfTranstypeName.setValue(sltedRcd.getItemProperty("transtypename").getValue().toString());
				String credr = sltedRcd.getItemProperty("crdr").getValue().toString();
				if (credr.equals("Credit")) {
					credr = "C";
				} else {
					credr = "D";
				}
				cbCreditDebit.setValue(credr);
				cbStatus.setValue(sltedRcd.getItemProperty("transtypestatus").getValue().toString());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
				+ " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfTranstypeName.setValue("");
		cbCreditDebit.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
				+ "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfTranstypeName.setRequired(true);
		cbCreditDebit.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
				+ "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfTranstypeName.setRequired(true);
		cbCreditDebit.setRequired(true);
		editTransactionType();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
				+ "Validating Data ");
		cbCreditDebit.setComponentError(null);
		tfTranstypeName.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + strLoginUserName + " > " + "Validating Data ");
		if (cbCreditDebit.getValue() == null) {
			cbCreditDebit.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_CREDIT_DEBIT));
			errorFlag = true;
		}
		if ((tfTranstypeName.getValue() == null) || tfTranstypeName.getValue().trim().length() == 0) {
			tfTranstypeName.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_TRANS_TYPE));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
				+ "Saving Data... ");
		TransactionTypeDM transactionType = new TransactionTypeDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			transactionType = beanTransactionTypeDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		transactionType.setCompanyid(companyId);
		transactionType.setTranstypename(tfTranstypeName.getValue().toString());
		if (cbStatus.getValue() != null) {
			transactionType.setTranstypestatus((String) cbStatus.getValue());
		}
		if (cbCreditDebit.getValue() != null) {
			transactionType.setCrdr((String) cbCreditDebit.getValue());
		}
		transactionType.setLastupdateddt(DateUtils.getcurrentdate());
		transactionType.setLastupdatedby(strLoginUserName);
		serviceTransType.saveDetails(transactionType);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
				+ "Getting audit record for Trantraction type ID " + transtypeid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_FMS_TRANS_TYPE);
		UI.getCurrent().getSession().setAttribute("audittablepk", transtypeid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
				+ "Canceling action ");
		assembleSearchLayout();
		tfTranstypeName.setRequired(false);
		cbCreditDebit.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfTranstypeName.getValue() + ",  Active ," + companyId
				+ "Resetting the UI controls");
		tfTranstypeName.setValue("");
		tfTranstypeName.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbCreditDebit.setValue(null);
	}
}
