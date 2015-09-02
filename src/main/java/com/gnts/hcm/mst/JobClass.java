/**
 * File Name 		: JobClass.java 
 * Description 		: this class is used for add/edit JobClass details. 
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
import com.gnts.hcm.domain.mst.JobClassificationDM;
import com.gnts.hcm.service.mst.JobClassificationService;
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

public class JobClass extends BaseUI {
	// Bean Creation
	private JobClassificationService serviceJobClassification = (JobClassificationService) SpringContextHelper
			.getBean("JobClassification");
	// form layout for input controls
	private FormLayout flClsFctnName, flClsFctnStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfClsFctnName;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<JobClassificationDM> beanJobClassificationDM = null;
	// local variables declaration
	private Long companyid;
	private String jobClsId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(JobClass.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public JobClass() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside JobClassification() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting JobClassification UI");
		// JobClassification Name text field
		tfClsFctnName = new GERPTextField("Job Classification Name");
		tfClsFctnName.setMaxLength(25);
		// create form layouts to hold the input items
		flClsFctnName = new FormLayout();
		flClsFctnStatus = new FormLayout();
		// add the user input items into appropriate form layout
		flClsFctnName.addComponent(tfClsFctnName);
		flClsFctnStatus.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flClsFctnName);
		hlUserInputLayout.addComponent(flClsFctnStatus);
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
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<JobClassificationDM> jobClsList = new ArrayList<JobClassificationDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfClsFctnName.getValue() + ", " + cbStatus.getValue());
			jobClsList = serviceJobClassification.getJobClassificationList(null, tfClsFctnName.getValue(), companyid,
					(String) cbStatus.getValue(), "F");
			recordCnt = jobClsList.size();
			beanJobClassificationDM = new BeanItemContainer<JobClassificationDM>(JobClassificationDM.class);
			beanJobClassificationDM.addAll(jobClsList);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the JobClassification. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanJobClassificationDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "jobClasfnId", "clasficatnName", "status",
					"lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Job Classification Name", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("jobClsId", Align.RIGHT);
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
		tfClsFctnName.setValue("");
		tfClsFctnName.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editJobClsfctn() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			jobClsId = sltedRcd.getItemProperty("jobClasfnId").getValue().toString();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected JobClassification. Id -> " + jobClsId);
			if (sltedRcd != null) {
				tfClsFctnName.setValue(sltedRcd.getItemProperty("clasficatnName").getValue().toString());
				String stCode = sltedRcd.getItemProperty("status").getValue().toString();
				cbStatus.setValue(stCode);
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfClsFctnName.setValue("");
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
		tfClsFctnName.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for JobClassification. ID " + jobClsId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_JOBCLASSIFICATION);
		UI.getCurrent().getSession().setAttribute("audittablepk", jobClsId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfClsFctnName.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfClsFctnName.setRequired(true);
		editJobClsfctn();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfClsFctnName.setComponentError(null);
		if ((tfClsFctnName.getValue() == null) || tfClsFctnName.getValue().trim().length() == 0) {
			tfClsFctnName.setComponentError(new UserError(GERPErrorCodes.NULL_JOBCLASSIFICATION));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfClsFctnName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		JobClassificationDM jobClassification = new JobClassificationDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			jobClassification = beanJobClassificationDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		jobClassification.setCmpId(companyid);
		jobClassification.setClasficatnName(tfClsFctnName.getValue().toString());
		if (cbStatus.getValue() != null) {
			jobClassification.setStatus((String) cbStatus.getValue());
		}
		jobClassification.setLastUpdatedDate(DateUtils.getcurrentdate());
		jobClassification.setLastUpdatedBy(username);
		serviceJobClassification.saveAndUpdate(jobClassification);
		resetFields();
		loadSrchRslt();
	}
}
