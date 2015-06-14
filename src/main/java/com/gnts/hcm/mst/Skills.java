/**
 * File Name 		: Skills.java 
 * Description 		: this class is used for add/edit Skills details. 
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
import com.gnts.hcm.domain.mst.JobClassificationDM;
import com.gnts.hcm.domain.mst.SkillsDM;
import com.gnts.hcm.service.mst.JobClassificationService;
import com.gnts.hcm.service.mst.SkillsService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Skills extends BaseUI {
	private SkillsService serviceSkills = (SkillsService) SpringContextHelper.getBean("Skills");
	private JobClassificationService serviceJobClassification = (JobClassificationService) SpringContextHelper
			.getBean("JobClassification");
	// form layout for input controls
	private FormLayout flSkillsName, flSkillsStatus, flJobclsFcn;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfSkillsName;
	private ComboBox cbJobClsFcn;
	private ComboBox cbSkillsStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<SkillsDM> beanSkills = null;
	private BeanContainer<Long, JobClassificationDM> beanJobClsfctnDM = null;
	// local variables declaration
	private Long companyid;
	private String SkillsId;
	private Boolean errorFlag = false;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(Skills.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Skills() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Skills() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Skills UI");
		// Skills Name text field
		tfSkillsName = new GERPTextField("Skills Name");
		tfSkillsName.setMaxLength(25);
		// Symbol Name text field
		cbJobClsFcn = new GERPComboBox("Job Classification Name");
		cbJobClsFcn.setItemCaptionPropertyId("clasficatnName");
		loadJobFrctnList();
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
		flSkillsName = new FormLayout();
		flSkillsStatus = new FormLayout();
		flJobclsFcn=new FormLayout();
		flSkillsName.addComponent(tfSkillsName);
		flJobclsFcn.addComponent(cbJobClsFcn);
		flSkillsStatus.addComponent(cbSkillsStatus);
		hlSearchLayout.addComponent(flSkillsName);
		hlSearchLayout.addComponent(flJobclsFcn);
		hlSearchLayout.addComponent(flSkillsStatus);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flSkillsName = new FormLayout();
		flJobclsFcn = new FormLayout();
		flSkillsStatus = new FormLayout();
		flSkillsName.addComponent(tfSkillsName);
		flJobclsFcn.addComponent(cbJobClsFcn);
		flSkillsStatus.addComponent(cbSkillsStatus);
		hlUserInputLayout.addComponent(flSkillsName);
		hlUserInputLayout.addComponent(flJobclsFcn);
		hlUserInputLayout.addComponent(flSkillsStatus);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<SkillsDM> SkillsList = new ArrayList<SkillsDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfSkillsName.getValue() + ", " + cbSkillsStatus.getValue());
		Long jobclsfcnId=null;
		if(cbJobClsFcn.getValue()!=null)
		{
			jobclsfcnId=Long.valueOf(cbJobClsFcn.getValue().toString());
		}
		SkillsList = serviceSkills.getSkillsList(null, tfSkillsName.getValue(), jobclsfcnId, companyid,
				(String) cbSkillsStatus.getValue(), "F");
		recordCnt = SkillsList.size();
		beanSkills = new BeanItemContainer<SkillsDM>(SkillsDM.class);
		beanSkills.addAll(SkillsList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Leave Type. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanSkills);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "skillId", "skillName", "clasficatnName","status", "lastUpdatedDate",
				"lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Skills Name","Job Classification", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("skillId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfSkillsName.setValue("");
		cbJobClsFcn.setValue(null);
		tfSkillsName.setComponentError(null);
		cbJobClsFcn.setComponentError(null);
		cbSkillsStatus.setValue(cbSkillsStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editSkills() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		SkillsDM editSkills = beanSkills.getItem(tblMstScrSrchRslt.getValue()).getBean();
		SkillsId = editSkills.getSkillId().toString();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Skills. Id -> "
				+ SkillsId);
		if (editSkills.getSkillName() != null) {
			tfSkillsName.setValue(itselect.getItemProperty("skillName").getValue().toString());
		}
		cbJobClsFcn.setValue(Long.valueOf(editSkills.getJobClasfcnId()));
		cbSkillsStatus.setValue(itselect.getItemProperty("status").getValue());
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
		cbSkillsStatus.setValue(cbSkillsStatus.getItemIds().iterator().next());
		tfSkillsName.setValue("");
		cbJobClsFcn.setValue(null);
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
		tfSkillsName.setRequired(true);
		cbJobClsFcn.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Skills. ID " + SkillsId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_LEAVE_TYPE);
		UI.getCurrent().getSession().setAttribute("audittablepk", SkillsId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfSkillsName.setRequired(false);
		cbJobClsFcn.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfSkillsName.setRequired(true);
		cbJobClsFcn.setRequired(false);
		editSkills();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfSkillsName.setComponentError(null);
		cbJobClsFcn.setComponentError(null);
		errorFlag = false;
		if ((tfSkillsName.getValue() == null) || tfSkillsName.getValue().trim().length() == 0) {
			tfSkillsName.setComponentError(new UserError(GERPErrorCodes.NULL_SKILL_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfSkillsName.getValue());
			errorFlag = true;
		}
		if (cbJobClsFcn.getValue() == null) {
			cbJobClsFcn.setComponentError(new UserError(GERPErrorCodes.NULL_JOBCLASSIFICATION));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbJobClsFcn.getValue());
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
			SkillsDM SkillsObj = new SkillsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				SkillsObj = beanSkills.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			SkillsObj.setCmpId(companyid);
			if (tfSkillsName.getValue() != null && tfSkillsName.getValue().trim().length() > 0) {
				SkillsObj.setSkillName(tfSkillsName.getValue());
			}
			if (cbJobClsFcn.getValue() != null) {
				SkillsObj.setJobClasfcnId(Long.valueOf(cbJobClsFcn.getValue().toString()));
			}
			if (cbSkillsStatus.getValue() != null) {
				SkillsObj.setStatus((String) cbSkillsStatus.getValue());
			}
			SkillsObj.setLastUpdatedDate(DateUtils.getcurrentdate());
			SkillsObj.setLastUpdatedBy(username);
			serviceSkills.saveAndUpdate(SkillsObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadJobFrctnList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<JobClassificationDM> branchlist = serviceJobClassification.getJobClassificationList(null, null, companyid,
				"Active", "P");
		beanJobClsfctnDM = new BeanContainer<Long, JobClassificationDM>(JobClassificationDM.class);
		beanJobClsfctnDM.setBeanIdProperty("jobClasfnId");
		beanJobClsfctnDM.addAll(branchlist);
		cbJobClsFcn.setContainerDataSource(beanJobClsfctnDM);
	}
}
