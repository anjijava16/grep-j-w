/**
 * File Name 		: Bank.java 
 * Description 		: this class is used for add/edit Bank details. 
 * Author 			: SOUNDAR C 
 * Date 			: Feb 25, 2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          Mar 03 2014        	MADHU T			        Initial Version
 * 0.2			JUNE 18,2014		MADHU T					Code re-factoring
 * 0.2			18-Jul-2014			Abdullah				Code Optimization
 */
package com.gnts.fms.mst;

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
import com.gnts.fms.domain.mst.BankDM;
import com.gnts.fms.service.mst.BankService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Bank extends BaseUI {
	private BankService serviceBank = (BankService) SpringContextHelper.getBean("fmsbank");
	// form layout for input controls
	private FormLayout flBankName, flStatus, flShortName;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfBankname, tfShortName;
	private ComboBox cbBankStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	// BeanContainer
	private BeanItemContainer<BankDM> beanBankDM = null;
	// Local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	private String pkbankId;
	private Long companyId;
	// Initialize logger
	private Logger logger = Logger.getLogger(Bank.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Bank() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Bank() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Bank UI");
		// Bank Name text field
		tfBankname = new GERPTextField("Bank Name");
		tfBankname.setMaxLength(25);
		// Bank Short Name Text Field
		tfShortName = new GERPTextField("Bank Short Name");
		tfShortName.setMaxLength(25);
		// create form layouts to hold the input items
		flBankName = new FormLayout();
		flShortName = new FormLayout();
		flStatus = new FormLayout();
		// add the user input items into appropriate form layout
		flBankName.addComponent(tfBankname);
		flShortName.addComponent(tfShortName);
		flStatus.addComponent(cbBankStatus);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in User Input Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for Search Layout
		hlSearchLayout.addComponent(flBankName);
		tfBankname.setComponentError(null);
		hlSearchLayout.addComponent(flStatus);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlSearchLayout.removeAllComponents();
		// Add components for User Input Layout
		hlUserInputLayout.addComponent(flBankName);
		hlUserInputLayout.addComponent(flShortName);
		hlUserInputLayout.addComponent(flStatus);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<BankDM> bankList = new ArrayList<BankDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfBankname.getValue() + ", " + (String) cbBankStatus.getValue());
		bankList = serviceBank.getBanklist(null, tfBankname.getValue(), companyId, (String) cbBankStatus.getValue(),
				"F");
		recordCnt = bankList.size();
		beanBankDM = new BeanItemContainer<BankDM>(BankDM.class);
		beanBankDM.addAll(bankList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Bank. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanBankDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "bankid", "bankname", "bankStatus", "lastupdateddt",
				"lastupdatedby", });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", " Bank Name", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("bankid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfBankname.setValue("");
		tfShortName.setValue("");
		cbBankStatus.setValue(cbBankStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editBank() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			BankDM editBank = beanBankDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			pkbankId = editBank.getBankid().toString();
			tfBankname.setValue(editBank.getBankname());
			if (editBank.getShortname() != null) {
				tfShortName.setValue(editBank.getShortname());
			}
			cbBankStatus.setValue(editBank.getBankStatus());
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbBankStatus.setValue(cbBankStatus.getItemIds().iterator().next());
		tfBankname.setValue("");
		tfShortName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		assembleUserInputLayout();
		tfBankname.setVisible(true);
		// Add input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfBankname.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Bank. ID " + pkbankId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_FMS_BANK);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkbankId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfBankname.setRequired(false);
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfBankname.setRequired(true);
		editBank();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfBankname.setComponentError(null);
		if ((tfBankname.getValue() == null) || tfBankname.getValue().trim().length() == 0) {
			tfBankname.setComponentError(new UserError(GERPErrorCodes.NULL_BANK_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfBankname.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			BankDM bankObj = new BankDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				bankObj = beanBankDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			bankObj.setCompanyid(companyid);
			bankObj.setBankname(tfBankname.getValue().toString());
			bankObj.setShortname(tfShortName.getValue().toString());
			if (cbBankStatus.getValue() != null) {
				bankObj.setBankStatus((String) cbBankStatus.getValue());
			}
			bankObj.setLastupdateddt(DateUtils.getcurrentdate());
			bankObj.setLastupdatedby(username);
			serviceBank.saveDetails(bankObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			try {
				throw new ERPException.SaveException();
			}
			catch (SaveException e1) {
				logger.error("Company ID : " + companyid + " | User Name : " + username + " > " + "Exception "
						+ e1.getMessage());
				e1.printStackTrace();
			}
		}
	}
}
