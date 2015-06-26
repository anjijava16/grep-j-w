/**
 * File Name 		: Earnings.java 
 * Description 		: this class is used for add/edit Earnings details. 
 * Author 			: MADHU T 
 * Date 			: 18-July-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         18-July-2014        	MADHU T		        Initial Version
 * 
 */
package com.gnts.hcm.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.EarningsDM;
import com.gnts.hcm.service.mst.EarningsService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Earnings extends BaseUI {
	// Bean creation
	private EarningsService serviceEarnings = (EarningsService) SpringContextHelper.getBean("Earnings");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfEarnDesc, tfEarnCode;
	private ComboBox cbStatus, cbEarnType;
	private CheckBox chkIsTax;
	// BeanItemContainer
	private BeanItemContainer<EarningsDM> beanEarningsDM = null;
	// local variables declaration
	private Long companyid, moduleId;
	private String pkEarningsId;
	private int recordCnt = 0;
	private Boolean errorFlag = false;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(Earnings.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Earnings() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Earnings() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Earnings UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Earnings Description text field
		tfEarnDesc = new GERPTextField("Earnings Description");
		// Earnings Type ComboBox
		cbEarnType = new GERPComboBox("Earning Type");
		cbEarnType.setItemCaptionPropertyId("lookupname");
		loadCmpLkup();
		// Earn Code Textfield
		tfEarnCode = new GERPTextField("Earn Code");
		// Is Taxable CheckBox
		chkIsTax = new CheckBox("Is Taxable?");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(tfEarnCode);
		flColumn2.addComponent(tfEarnDesc);
		flColumn3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlSearchLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn5 = new FormLayout();
		flColumn1.addComponent(tfEarnCode);
		flColumn2.addComponent(tfEarnDesc);
		flColumn3.addComponent(cbEarnType);
		flColumn4.addComponent(chkIsTax);
		flColumn5.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.addComponent(flColumn5);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<EarningsDM> EarningsList = new ArrayList<EarningsDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfEarnDesc.getValue() + ", " + tfEarnDesc.getValue()
				+ (String) cbStatus.getValue());
		EarningsList = serviceEarnings.getEarningList(null, tfEarnCode.getValue(), tfEarnDesc.getValue(), null,
				(String) cbStatus.getValue(), "F");
		recordCnt = EarningsList.size();
		beanEarningsDM = new BeanItemContainer<EarningsDM>(EarningsDM.class);
		beanEarningsDM.addAll(EarningsList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Earnings. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanEarningsDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "earnId", "earnCode", "earnDESCR", "earnType", "status",
				"lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Earn Code", "Earn Description", "Earning Type",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("earnId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfEarnDesc.setValue("");
		tfEarnCode.setValue("");
		cbEarnType.setValue(null);
		chkIsTax.setValue(false);
		tfEarnDesc.setComponentError(null);
		tfEarnCode.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editEarnings() {
		EarningsDM editEarnings = beanEarningsDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		pkEarningsId = editEarnings.getEarnId().toString();
		if (editEarnings.getEarnCode() != null) {
			tfEarnCode.setValue(editEarnings.getEarnCode());
		}
		if (editEarnings.getEarnDESCR() != null) {
			tfEarnDesc.setValue(editEarnings.getEarnDESCR());
		}
		if (editEarnings.getEarnType() != null) {
			cbEarnType.setValue(editEarnings.getEarnType());
		}
		if (editEarnings.getIsTax().equals("Y")) {
			chkIsTax.setValue(true);
		} else {
			chkIsTax.setValue(false);
		}
		cbStatus.setValue(editEarnings.getStatus());
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		tfEarnDesc.setValue("");
		tfEarnCode.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		// reset the field valued to default
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		resetFields();
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfEarnDesc.setRequired(true);
		tfEarnCode.setRequired(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Earn. ID " + pkEarningsId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_EARNINGS);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkEarningsId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfEarnDesc.setRequired(false);
		tfEarnCode.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		tfEarnDesc.setRequired(true);
		tfEarnCode.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editEarnings();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfEarnDesc.setComponentError(null);
		tfEarnCode.setComponentError(null);
		errorFlag = false;
		if ((tfEarnDesc.getValue() == null) || tfEarnDesc.getValue().trim().length() == 0) {
			tfEarnDesc.setComponentError(new UserError(GERPErrorCodes.NULL_EARNING_DESC));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfEarnDesc.getValue());
			errorFlag = true;
		}
		if ((tfEarnCode.getValue() == "") || tfEarnCode.getValue().trim().length() == 0) {
			tfEarnCode.setComponentError(new UserError(GERPErrorCodes.NULL_EARNING_CODE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfEarnCode.getValue());
			errorFlag = true;
		}
		EarningsDM earningObj = new EarningsDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			earningObj = beanEarningsDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if ((tfEarnCode.getValue() != null) && earningObj.getEarnId() == null) {
			if (serviceEarnings.getEarningList(null, tfEarnCode.getValue(), null, companyid, "Active", "P").size() > 0) {
				tfEarnCode.setComponentError(new UserError("Earning Code already Exist"));
				errorFlag = true;
			}
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		EarningsDM earningObj = new EarningsDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			earningObj = beanEarningsDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if (tfEarnDesc.getValue() != null) {
			earningObj.setEarnDESCR(tfEarnDesc.getValue().toString());
		}
		if (cbEarnType.getValue() != null) {
			earningObj.setEarnType(cbEarnType.getValue().toString());
		}
		if (tfEarnCode.getValue() != null) {
			earningObj.setEarnCode(tfEarnCode.getValue().toString());
		}
		if (chkIsTax.getValue().equals(true)) {
			earningObj.setIsTax("Y");
		} else if (chkIsTax.getValue().equals(false)) {
			earningObj.setIsTax("N");
		}
		earningObj.setCmpId(companyid);
		if (cbStatus.getValue() != null) {
			earningObj.setStatus((String) cbStatus.getValue());
		}
		earningObj.setLastUpdatedDate(DateUtils.getcurrentdate());
		earningObj.setLastUpdatedBy(username);
		serviceEarnings.saveAndUpdate(earningObj);
		resetFields();
		loadSrchRslt();
	}
	
	public void loadCmpLkup() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
				"HC_ERNTYPE"));
		cbEarnType.setContainerDataSource(beanCompanyLookUp);
	}
}
