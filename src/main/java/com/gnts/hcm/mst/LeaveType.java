/**
 * File Name 		: LeaveType.java 
 * Description 		: this class is used for add/edit LeaveType details. 
 * Author 			: MADHU T 
 * Date 			: 10-July-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1			10-July-2014			Madhu T						Initial Version
 */
package com.gnts.hcm.mst;

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
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.LeaveTypeDM;
import com.gnts.hcm.service.mst.LeaveTypeService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class LeaveType extends BaseUI {
	private LeaveTypeService serviceLeaveType = (LeaveTypeService) SpringContextHelper.getBean("LeaveType");
	// form layout for input controls
	private FormLayout flLeaveTypeName, flLeaveTypeStatus, flSymbol, flCarryFrwd;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfLeaveTypeName, tfSymbol;
	private ComboBox cbLeaveTypeStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private CheckBox ckCarryFrwd;
	private Boolean errorFlag = false;
	private BeanItemContainer<LeaveTypeDM> beanLeaveType = null;
	// local variables declaration
	private Long companyid;
	private String leaveTypeId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(LeaveType.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public LeaveType() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside LeaveType() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting LeaveType UI");
		// LeaveType Name text field
		tfLeaveTypeName = new GERPTextField("Leave Type Name");
		tfLeaveTypeName.setMaxLength(25);
		// Symbol Name text field
		tfSymbol = new GERPTextField("Symbol");
		tfSymbol.setMaxLength(1);
		// CarryFrwd CheckBox
		ckCarryFrwd = new CheckBox();
		ckCarryFrwd.setCaption("Carry Forward?");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flLeaveTypeName = new FormLayout();
		flLeaveTypeStatus = new FormLayout();
		flSymbol = new FormLayout();
		flLeaveTypeName.addComponent(tfLeaveTypeName);
		flSymbol.addComponent(tfSymbol);
		flLeaveTypeStatus.addComponent(cbLeaveTypeStatus);
		hlSearchLayout.addComponent(flLeaveTypeName);
		hlSearchLayout.addComponent(flSymbol);
		hlSearchLayout.addComponent(flLeaveTypeStatus);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flLeaveTypeName = new FormLayout();
		flSymbol = new FormLayout();
		flCarryFrwd = new FormLayout();
		flLeaveTypeStatus = new FormLayout();
		flLeaveTypeName.addComponent(tfLeaveTypeName);
		flSymbol.addComponent(tfSymbol);
		flCarryFrwd.addComponent(ckCarryFrwd);
		flLeaveTypeStatus.addComponent(cbLeaveTypeStatus);
		hlUserInputLayout.addComponent(flLeaveTypeName);
		hlUserInputLayout.addComponent(flSymbol);
		hlUserInputLayout.addComponent(flCarryFrwd);
		hlUserInputLayout.addComponent(flLeaveTypeStatus);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<LeaveTypeDM> listLeaveType = new ArrayList<LeaveTypeDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfLeaveTypeName.getValue() + ", " + cbLeaveTypeStatus.getValue());
		listLeaveType = serviceLeaveType.getLeaveTypeList(null, tfLeaveTypeName.getValue(), companyid,
				tfSymbol.getValue(), null, (String) cbLeaveTypeStatus.getValue(), "F");
		recordCnt = listLeaveType.size();
		beanLeaveType = new BeanItemContainer<LeaveTypeDM>(LeaveTypeDM.class);
		beanLeaveType.addAll(listLeaveType);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Leave Type. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanLeaveType);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "leaveTypeId", "leaveTypeName", "leaveTypeSymbl",
				"leaveTypeStatus", "lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Leave Type Name", "Symbol", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("leaveTypeId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfLeaveTypeName.setValue("");
		tfSymbol.setValue("");
		ckCarryFrwd.setValue(false);
		tfLeaveTypeName.setComponentError(null);
		tfSymbol.setComponentError(null);
		cbLeaveTypeStatus.setValue(cbLeaveTypeStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editLeaveType() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		LeaveTypeDM leaveTypeDM = beanLeaveType.getItem(tblMstScrSrchRslt.getValue()).getBean();
		leaveTypeId = leaveTypeDM.getLeaveTypeId().toString();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected LeaveType. Id -> "
				+ leaveTypeId);
		if (leaveTypeDM.getLeaveTypeName() != null) {
			tfLeaveTypeName.setValue(leaveTypeDM.getLeaveTypeName());
		}
		if (leaveTypeDM.getLeaveTypeSymbl() != null) {
			tfSymbol.setValue(leaveTypeDM.getLeaveTypeSymbl());
		}
		if (leaveTypeDM.getLeaveTypeCarryFrwd().equals("Y")) {
			ckCarryFrwd.setValue(true);
		} else {
			ckCarryFrwd.setValue(false);
		}
		cbLeaveTypeStatus.setValue(leaveTypeDM.getLeaveTypeStatus());
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
		cbLeaveTypeStatus.setValue(cbLeaveTypeStatus.getItemIds().iterator().next());
		tfLeaveTypeName.setValue("");
		tfSymbol.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		assembleUserInputLayout();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfLeaveTypeName.setRequired(true);
		tfSymbol.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for LeaveType. ID " + leaveTypeId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_LEAVE_TYPE);
		UI.getCurrent().getSession().setAttribute("audittablepk", leaveTypeId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfLeaveTypeName.setRequired(false);
		tfSymbol.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfLeaveTypeName.setRequired(true);
		tfSymbol.setRequired(true);
		editLeaveType();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfLeaveTypeName.setComponentError(null);
		tfSymbol.setComponentError(null);
		errorFlag = false;
		if ((tfLeaveTypeName.getValue() == null) || tfLeaveTypeName.getValue().trim().length() == 0) {
			tfLeaveTypeName.setComponentError(new UserError(GERPErrorCodes.NULL_LEAVE_TYPE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfLeaveTypeName.getValue());
			errorFlag = true;
		}
		if (tfSymbol.getValue() == "") {
			tfSymbol.setComponentError(new UserError(GERPErrorCodes.NULL_LEAVE_TYPE_SYMBOL));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfSymbol.getValue());
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			LeaveTypeDM leaveTypeDM = new LeaveTypeDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				leaveTypeDM = beanLeaveType.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			leaveTypeDM.setCmpId(companyid);
			if (tfLeaveTypeName.getValue() != null && tfLeaveTypeName.getValue().trim().length() > 0) {
				leaveTypeDM.setLeaveTypeName(tfLeaveTypeName.getValue());
			}
			if (tfSymbol.getValue() != null) {
				leaveTypeDM.setLeaveTypeSymbl(tfSymbol.getValue());
			}
			if (ckCarryFrwd.getValue().equals(true)) {
				leaveTypeDM.setLeaveTypeCarryFrwd("Y");
			} else {
				leaveTypeDM.setLeaveTypeCarryFrwd("N");
			}
			if (ckCarryFrwd.getValue().equals(true)) {
				leaveTypeDM.setLeaveTypeCarryFrwd("Y");
			} else if (ckCarryFrwd.getValue().equals(false)) {
				leaveTypeDM.setLeaveTypeCarryFrwd("N");
			}
			if (cbLeaveTypeStatus.getValue() != null) {
				leaveTypeDM.setLeaveTypeStatus((String) cbLeaveTypeStatus.getValue());
			}
			leaveTypeDM.setLastUpdatedDate(DateUtils.getcurrentdate());
			leaveTypeDM.setLastUpdatedBy(username);
			serviceLeaveType.saveAndUpdate(leaveTypeDM);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
