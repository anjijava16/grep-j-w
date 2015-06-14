/**
 * File Name 		: EmploymentType.java 
 * Description 		: this class is used for add/edit EmploymentType details. 
 * Author 			: MADHU T
 * Date 			: JULY 11, 2014
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version      	 Date           	Modified By               Remarks
 * 0.1             JULY 11, 2014   	 	MADHU T 		        Intial Version
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
import com.gnts.hcm.domain.mst.EmploymentTypeDM;
import com.gnts.hcm.service.mst.EmploymentTypeService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Table.Align;

public class EmploymentType extends BaseUI {
	// Bean Creation
	private EmploymentTypeService serviceEmpType = (EmploymentTypeService) SpringContextHelper.getBean("EmploymentType");
	// form layout for input controls
	private FormLayout flEmpName, flEmpStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfEmpName;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<EmploymentTypeDM> beanEmploymentTypeDM = null;
	// local variables declaration
	private Long companyid;
	private String empTypeId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmploymentType.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmploymentType() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmploymentType() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting EmploymentType UI");
		// EmploymentType Name text field
		tfEmpName = new GERPTextField("Employment Type");
		tfEmpName.setMaxLength(25);
		// create form layouts to hold the input items
		flEmpName = new FormLayout();
		flEmpStatus = new FormLayout();
		// add the user input items into appropriate form layout
		flEmpName.addComponent(tfEmpName);
		flEmpStatus.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flEmpName);
		hlUserInputLayout.addComponent(flEmpStatus);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Add User Input Layout
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<EmploymentTypeDM> empTypeList = new ArrayList<EmploymentTypeDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfEmpName.getValue() + ", " + cbStatus.getValue());
		empTypeList = serviceEmpType.getEmpTypeList(null, tfEmpName.getValue(), companyid,
				(String) cbStatus.getValue(), "F");
		recordCnt = empTypeList.size();
		beanEmploymentTypeDM = new BeanItemContainer<EmploymentTypeDM>(EmploymentTypeDM.class);
		beanEmploymentTypeDM.addAll(empTypeList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the EmploymentType. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanEmploymentTypeDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "empTypeId", "empTypeName", "empStatus", "lastUpdatedDate",
				"lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employment Type", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("empTypeId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfEmpName.setValue("");
		tfEmpName.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editEmploymentType() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		empTypeId = sltedRcd.getItemProperty("empTypeId").getValue().toString();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Selected EmploymentType. Id -> " + empTypeId);
		if (sltedRcd != null) {
			tfEmpName.setValue(sltedRcd.getItemProperty("empTypeName").getValue().toString());
			String stCode = sltedRcd.getItemProperty("empStatus").getValue().toString();
			cbStatus.setValue(stCode);
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
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfEmpName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfEmpName.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for EmploymentType. ID " + empTypeId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_EMPLOYEE_TYPE);
		UI.getCurrent().getSession().setAttribute("audittablepk", empTypeId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfEmpName.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfEmpName.setRequired(true);
		editEmploymentType();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfEmpName.setComponentError(null);
		if ((tfEmpName.getValue() == null) || tfEmpName.getValue().trim().length() == 0) {
			tfEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYMENTTYPE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfEmpName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		EmploymentTypeDM empTypeObj = new EmploymentTypeDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			empTypeObj = beanEmploymentTypeDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		empTypeObj.setCmpId(companyid);
		empTypeObj.setEmpTypeName(tfEmpName.getValue().toString());
		if (cbStatus.getValue() != null) {
			empTypeObj.setEmpStatus((String) cbStatus.getValue());
		}
		empTypeObj.setLastUpdatedDate(DateUtils.getcurrentdate());
		empTypeObj.setLastUpdatedBy(username);
		serviceEmpType.saveAndUpdate(empTypeObj);
		resetFields();
		loadSrchRslt();
	}
}
