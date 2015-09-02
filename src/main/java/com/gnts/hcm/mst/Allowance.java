/**
 * File Name 		: Allowance.java 
 * Description 		: this class is used for add/edit Allowance details. 
 * Author 			: MADHU T 
 * Date 			: 22-July-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         22-July-2014        	MADHU T		        Initial Version
 * 
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
import com.gnts.hcm.domain.mst.AllowanceDM;
import com.gnts.hcm.service.mst.AllowanceService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Allowance extends BaseUI {
	// Bean creation
	private AllowanceService serviceAllowance = (AllowanceService) SpringContextHelper.getBean("Allowance");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfAlowncDesc, tfAlownceCode;
	private ComboBox cbStatus;
	private CheckBox chkAddToGros;
	// BeanItemContainer
	private BeanItemContainer<AllowanceDM> beanAllowanceDM = null;
	// local variables declaration
	private Long companyid;
	private String pkAllowanceId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(Allowance.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Allowance() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Allowance() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Allowance UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Allowance Description text field
		tfAlowncDesc = new GERPTextField("Allowance Description");
		// Allowance Code text field
		tfAlownceCode = new GERPTextField("Allowance Code");
		tfAlownceCode.setWidth("50");
		// ApplyAllGrade CheckBox
		chkAddToGros = new CheckBox();
		chkAddToGros.setCaption("Add to gross?");
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
		flColumn1.addComponent(tfAlownceCode);
		flColumn2.addComponent(tfAlowncDesc);
		flColumn3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn1);
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
		flColumn1.addComponent(tfAlowncDesc);
		flColumn2.addComponent(tfAlownceCode);
		flColumn3.addComponent(chkAddToGros);
		flColumn4.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<AllowanceDM> listAllowance = new ArrayList<AllowanceDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfAlowncDesc.getValue() + ", " + tfAlownceCode.getValue()
					+ (String) cbStatus.getValue());
			listAllowance = serviceAllowance.getalowanceList(null, (String) tfAlownceCode.getValue(), companyid,
					(String) tfAlowncDesc.getValue(), (String) cbStatus.getValue(), "F");
			recordCnt = listAllowance.size();
			beanAllowanceDM = new BeanItemContainer<AllowanceDM>(AllowanceDM.class);
			beanAllowanceDM.addAll(listAllowance);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Allowance. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanAllowanceDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "alowncId", "alowncDesc", "alowncCode", "status",
					"lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Allowance Desc", "Allowance Code", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("alowncId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfAlowncDesc.setValue("");
		tfAlownceCode.setValue("");
		chkAddToGros.setValue(false);
		tfAlowncDesc.setComponentError(null);
		tfAlownceCode.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editAllowance() {
		try {
			AllowanceDM allowance = beanAllowanceDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			pkAllowanceId = allowance.getAlowncId().toString();
			if (allowance.getAlowncDesc() != null) {
				tfAlowncDesc.setValue(allowance.getAlowncDesc());
			}
			if (allowance.getAlowncCode() != null) {
				tfAlownceCode.setValue(allowance.getAlowncCode());
			}
			if (allowance.getAddToGross().equals("Y")) {
				chkAddToGros.setValue(true);
			} else {
				chkAddToGros.setValue(false);
			}
			cbStatus.setValue(allowance.getStatus());
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		tfAlowncDesc.setValue("");
		tfAlownceCode.setValue("");
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
		tfAlowncDesc.setRequired(true);
		tfAlownceCode.setRequired(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Allowance. ID " + pkAllowanceId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_ALLOWANCE);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkAllowanceId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfAlowncDesc.setRequired(false);
		tfAlownceCode.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		tfAlowncDesc.setRequired(true);
		tfAlownceCode.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editAllowance();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfAlowncDesc.setComponentError(null);
		tfAlownceCode.setComponentError(null);
		Boolean errorFlag = false;
		if ((tfAlowncDesc.getValue() == null) || tfAlowncDesc.getValue().trim().length() == 0) {
			tfAlowncDesc.setComponentError(new UserError(GERPErrorCodes.NULL_ALLOWANCE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfAlowncDesc.getValue());
			errorFlag = true;
		}
		if ((tfAlownceCode.getValue() == null) || tfAlownceCode.getValue().trim().length() == 0) {
			tfAlownceCode.setComponentError(new UserError(GERPErrorCodes.NULL_DEDUCTION_CODE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfAlownceCode.getValue());
			errorFlag = true;
		} else if (tblMstScrSrchRslt == null) {
			if (serviceAllowance.getalowanceList(null, tfAlownceCode.getValue(), companyid, null, "Active", "P").size() > 0) {
				tfAlownceCode.setComponentError(new UserError("Allowance Code already Exist"));
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
		AllowanceDM allowanceDM = new AllowanceDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			allowanceDM = beanAllowanceDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if (tfAlowncDesc.getValue() != null) {
			allowanceDM.setAlowncDesc(tfAlowncDesc.getValue().toString());
		}
		if (tfAlownceCode.getValue() != null) {
			allowanceDM.setAlowncCode(tfAlownceCode.getValue().toString());
		}
		if (chkAddToGros.getValue().equals(true)) {
			allowanceDM.setAddToGross("Y");
		} else if (chkAddToGros.getValue().equals(false)) {
			allowanceDM.setAddToGross("N");
		}
		allowanceDM.setCmpId(companyid);
		if (cbStatus.getValue() != null) {
			allowanceDM.setStatus((String) cbStatus.getValue());
		}
		allowanceDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		allowanceDM.setLastUpdatedBy(username);
		serviceAllowance.saveAndUpdate(allowanceDM);
		resetFields();
		loadSrchRslt();
	}
}
