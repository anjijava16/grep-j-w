/**
 * File Name 		: Holiday.java 
 * Description 		: this class is used for add/edit Holiday details. 
 * Author 			: SOUNDAR C 
 * Date 			: MAR 06, 2014
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version      	 Date           	Modified By               Remarks
 * 0.1              MAR 06, 2014   	 	SOUNDAR C 		        Intial Version
 * 0.2				JUNE 17, 2014 		Madhu T 				code re-factoring
 */
package com.gnts.base.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.txn.HolidaysDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.txn.HolidayService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Holiday extends BaseUI {
	// Creating Bean
	private HolidayService serviceHoliday = (HolidayService) SpringContextHelper.getBean("holidays");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private Label lblspace;
	private TextField tfHolidayName;
	private ComboBox cbBranch, cbstatus;
	private ComboBox cbHolidayHrs;
	private PopupDateField dtHolidayDate;
	// Local variable declaration
	private String username;
	private Long companyid;
	private String holidayid;
	private int recordCnt = 0;
	// Bean Container
	private BeanItemContainer<HolidaysDM> beanHolidayDM = null;
	// Initialize logger
	private Logger logger = Logger.getLogger(Holiday.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Holiday() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Holiday() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI Components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting holiday UI");
		lblspace = new Label();
		// Holiday Hours Combo box
		cbHolidayHrs = new GERPComboBox("Holiday", BASEConstants.T_BASE_HOLIDAY, BASEConstants.HOLDY_SESSION);
		// Initializing and properties for holidayDate PopupDateField
		dtHolidayDate = new GERPPopupDateField("Holiday Date");
		dtHolidayDate.setVisible(false);
		// dtHolidayDate.setRequired(true);
		// Initializing and properties for Holiday Name Text field
		tfHolidayName = new GERPTextField("Holiday Name");
		tfHolidayName.setRequired(true);
		tfHolidayName.setMaxLength(50);
		// Initializing and properties for Branch Name Combobox
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("160");
		loadBranchlist();
		// Initializing and properties for Status Combobox
		// Status ComboBox
		cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
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
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		// Add components for Search Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn1.addComponent(tfHolidayName);
		flColumn2.addComponent(cbBranch);
		flColumn3.addComponent(cbstatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn1.addComponent(tfHolidayName);
		flColumn1.addComponent(cbBranch);
		flColumn2.addComponent(dtHolidayDate);
		flColumn2.addComponent(cbHolidayHrs);
		flColumn3.addComponent(cbstatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(lblspace);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			List<HolidaysDM> list = new ArrayList<HolidaysDM>();
			Long branchObjId = null;
			if (cbBranch.getValue() != null) {
				branchObjId = (Long) cbBranch.getValue();
			}
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfHolidayName.getValue() + ", " + cbstatus.getValue());
			list = serviceHoliday.getHolidaysList(null, tfHolidayName.getValue(), branchObjId,
					(String) cbstatus.getValue(), companyid, null, "F");
			recordCnt = list.size();
			beanHolidayDM = new BeanItemContainer<HolidaysDM>(HolidaysDM.class);
			beanHolidayDM.addAll(list);
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.setContainerDataSource(beanHolidayDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "holidayId", "holidayName", "branchName", "holidayDate",
					"holidayStatus", "lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref Id", "Holiday", "Branch", "Date", "Status",
					"Updated Date", "Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("holidayId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
			tfHolidayName.setRequired(false);
			cbBranch.setRequired(false);
			dtHolidayDate.setRequired(false);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		tfHolidayName.setValue("");
		tfHolidayName.setComponentError(null);
		dtHolidayDate.setComponentError(null);
		cbBranch.setComponentError(null);
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		cbHolidayHrs.setValue(null);
		cbBranch.setValue(0L);
		dtHolidayDate.setValue(null);
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editHoliday() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				HolidaysDM holidaysDM = beanHolidayDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				holidayid = holidaysDM.getHolidayId().toString();
				tfHolidayName.setValue(holidaysDM.getHolidayName());
				cbstatus.setValue(holidaysDM.getHolidayStatus());
				dtHolidayDate.setValue(holidaysDM.getHolidayDateInDt());
				cbHolidayHrs.setValue(holidaysDM.getHolidaySession());
				cbBranch.setValue(holidaysDM.getBranchId());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		tfHolidayName.setValue("");
		cbBranch.setValue(null);
		cbBranch.setValue(cbBranch.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		assembleUserInputLayout();
		// remove the components in the search layout and input controls in the
		// same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbBranch.setVisible(true);
		tfHolidayName.setRequired(true);
		dtHolidayDate.setRequired(true);
		cbBranch.setRequired(true);
		tfHolidayName.setVisible(true);
		dtHolidayDate.setVisible(true);
		cbstatus.setVisible(true);
		cbHolidayHrs.setVisible(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Holiday. ID " + holidayid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_BASE_HOLIDAY);
		UI.getCurrent().getSession().setAttribute("audittablepk", holidayid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfHolidayName.setRequired(false);
		dtHolidayDate.setRequired(false);
		cbBranch.setRequired(false);
		cbBranch.setVisible(true);
		tfHolidayName.setVisible(true);
		dtHolidayDate.setVisible(false);
		cbstatus.setVisible(true);
		cbHolidayHrs.setVisible(false);
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbBranch.setVisible(true);
		tfHolidayName.setVisible(true);
		dtHolidayDate.setVisible(true);
		cbstatus.setVisible(true);
		cbHolidayHrs.setVisible(true);
		hlUserInputLayout.setSpacing(true);
		tfHolidayName.setRequired(true);
		cbBranch.setRequired(true);
		dtHolidayDate.setRequired(true);
		editHoliday();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfHolidayName.setComponentError(null);
		dtHolidayDate.setComponentError(null);
		cbBranch.setComponentError(null);
		Boolean errorFlag = false;
		if (tfHolidayName.getValue() == null || tfHolidayName.getValue().trim().length() == 0) {
			tfHolidayName.setComponentError(new UserError(GERPErrorCodes.NULL_HOLIDAY_NAME));
			errorFlag = true;
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. Holiday Name is > " + tfHolidayName.getValue());
		}
		if (dtHolidayDate.getValue() == null) {
			dtHolidayDate.setComponentError(new UserError(GERPErrorCodes.NULL_HOLIDAY_DATE));
			errorFlag = true;
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. Holiday Date is > " + dtHolidayDate.getValue());
		}
		if ((Long) cbBranch.getValue() == 0L) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_BRANCH));
			errorFlag = true;
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. Holiday Name is > " + cbBranch.getValue());
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			HolidaysDM holidayObj = new HolidaysDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				holidayObj = beanHolidayDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			holidayObj.setHolidayName(tfHolidayName.getValue());
			holidayObj.setHolidayDate(dtHolidayDate.getValue());
			holidayObj.setCompanyid(companyid);
			if (cbstatus.getValue() != null) {
				holidayObj.setHolidayStatus((String) cbstatus.getValue());
			}
			if (cbHolidayHrs.getValue() != null) {
				holidayObj.setHolidaySession((String) cbHolidayHrs.getValue());
			}
			holidayObj.setLastUpdatedBy(username);
			holidayObj.setLastUpdatedDate(DateUtils.getcurrentdate());
			holidayObj.setBranchId((Long) cbBranch.getValue());
			serviceHoliday.saveAndUpdate(holidayObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Branch details
	private void loadBranchlist() {
		try {
			List<BranchDM> list = new ArrayList<BranchDM>();
			list.add(new BranchDM(0L, "All Branchces"));
			list.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
			BeanContainer<Long, BranchDM> beanState = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanState.setBeanIdProperty("branchId");
			beanState.addAll(list);
			cbBranch.setContainerDataSource(beanState);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
