/**
 * File Name	:	TestGroup.java
 * Description	:	This Screen Purpose for Modify the TestGroup Details.
 * 					Add the TestGroup details process should be directly added in DB.
 * Author		:	Nandhakumar.S
 * 
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1          10-Jul-2014		  Nandhakumar.S		   Initial version
 * 
 */
package com.gnts.mfg.mst;

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
import com.gnts.mfg.domain.mst.TestGroupDM;
import com.gnts.mfg.service.mst.TestGroupService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class TestGroup extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TestGroupService serviceTestGroup = (TestGroupService) SpringContextHelper.getBean("testGroup");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2;
	// User Input Components
	private TextField tfTestGroup;
	private ComboBox cbStatus;
	// BeanItem container of TestGroupDM
	private BeanItemContainer<TestGroupDM> beanTestGroup = null;
	// local variables declaration
	private Long testGpID;
	private String username;
	private Long companyid;
	private int recordCnt;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	// Initialize logger
	private Logger logger = Logger.getLogger(TestGroup.class);
	
	// Constructor received the parameters from Login UI class
	public TestGroup() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Company() constructor");
		// Loading the TestGroup UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting TestGroup UI");
		// Text field for MFG.Test group Name
		tfTestGroup = new GERPTextField("Test Group");
		// Text field for MFG.Test group Status
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Initializing to form layouts for TestGroup UI search layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		// Adding components into form layouts for TestGroup UI search layout
		flColumn1.addComponent(tfTestGroup);
		flColumn2.addComponent(cbStatus);
		// Adding form layouts into search layout for TestGroup UI search mode
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			List<TestGroupDM> list = new ArrayList<TestGroupDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid);
			list = serviceTestGroup.getTestGpDetails(companyid, tfTestGroup.getValue().toString(),
					(String) cbStatus.getValue(), "F");
			recordCnt = list.size();
			beanTestGroup = new BeanItemContainer<TestGroupDM>(TestGroupDM.class);
			beanTestGroup.addAll(list);
			tblMstScrSrchRslt.setContainerDataSource(beanTestGroup);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "qaTestGpID", "testGroup", "tgroupStatus",
					"lastUpdatedDt", "lastUpdateBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Test Group", "Status", "Last Updated Date",
					"Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("qaTestGpID", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdateBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfTestGroup.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	protected void editMFGQATestGroupDetails() {
		if (tblMstScrSrchRslt.getValue() != null) {
			TestGroupDM testGroupDM = beanTestGroup.getItem(tblMstScrSrchRslt.getValue()).getBean();
			tfTestGroup.setValue(testGroupDM.getTestGroup());
			cbStatus.setValue(testGroupDM.gettgroupStatus());
			testGpID = Long.valueOf(testGroupDM.getQaTestGpID());
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
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tfTestGroup.setValue("");
		tfTestGroup.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// Adding user input fields into userIPContainer
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfTestGroup.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for TestGroup. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_MFG_QA_TEST_GROUP);
		UI.getCurrent().getSession().setAttribute("audittablepk", testGpID.toString());
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfTestGroup.setComponentError(null);
		tfTestGroup.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfTestGroup.setRequired(true);
		editMFGQATestGroupDetails();
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Boolean errorFlag = false;
		tfTestGroup.setComponentError(null);
		if (tfTestGroup.getValue() == null || tfTestGroup.getValue().trim().length() == 0) {
			tfTestGroup.setComponentError(new UserError(GERPErrorCodes.NULL_TEST_GROUP_NAME));
			errorFlag = true;
		} else if (tblMstScrSrchRslt.getValue() == null) {
			if (serviceTestGroup.getTestGpDetails(companyid, tfTestGroup.getValue(), "Active", "F").size() > 0) {
				tfTestGroup.setComponentError(new UserError(GERPErrorCodes.EXIST_TEST_GROUP_NAME));
				errorFlag = true;
			}
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Method to save TestGroup details into database
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			TestGroupDM testGroup = new TestGroupDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				testGroup = beanTestGroup.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			testGroup.setCompanyId(companyid);
			testGroup.setTestGroup(tfTestGroup.getValue());
			testGroup.settgroupStatus((String) cbStatus.getValue());
			testGroup.setLastUpdatedDt(DateUtils.getcurrentdate());
			testGroup.setLastUpdateBy(username);
			serviceTestGroup.saveTestGpDetails(testGroup);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
