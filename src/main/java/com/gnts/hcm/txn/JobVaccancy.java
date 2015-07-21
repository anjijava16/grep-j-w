/**
 * File Name	:	JobVaccancy.java
 * Description	:	this class is used for add/edit Job Vaccancy details. 
 * Author		:	KAVITHA V M
 * Date			:	09-August-2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 *  Version          Date              Modified By             Remarks
 *  0.1         	 09-August-2014    KAVITHA V M	           Initial Version       
 * 
 */
package com.gnts.hcm.txn;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.DesignationDM;
import com.gnts.hcm.domain.mst.JobClassificationDM;
import com.gnts.hcm.domain.txn.JobVaccancyDM;
import com.gnts.hcm.service.mst.DesignationService;
import com.gnts.hcm.service.mst.JobClassificationService;
import com.gnts.hcm.service.txn.JobVaccancyService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class JobVaccancy extends BaseUI {
	// Bean creation
	private JobVaccancyService serviceJobVaccancy = (JobVaccancyService) SpringContextHelper.getBean("JobVaccancy");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private JobClassificationService serviceJobClassification = (JobClassificationService) SpringContextHelper
			.getBean("JobClassification");
	private BranchService servicebeanBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private DesignationService serviceDesinatn = (DesignationService) SpringContextHelper.getBean("Designation");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private VerticalLayout vlappdoc = new VerticalLayout();
	// Add User Input Controls
	private TextField tfJobtitle;
	private ComboBox cbJbStatus, cbApStatus, cbHirgMgr, cbJobClsName, cbDesgntnName, cbBrnchName, cbReqstdName,
			cbApvdName;
	private PopupDateField dfApvdDate;
	private TextArea taJobDetails;
	private GERPComboBox cbWrkExp;
	private GERPTextField tfWrkExpYr, tfWrkExpDesc;
	// BeanItemContainer
	private BeanItemContainer<JobVaccancyDM> beanJobVaccancyDM = null;
	// local variables declaration
	private Long companyid, employeeId, vaccancyId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(JobVaccancy.class);
	private String jobvacancyId;
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public JobVaccancy() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside JobVaccancy() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Printing JobVaccancy UI");
		// Status ComboBox
		cbWrkExp = new GERPComboBox("Work Experience");
		cbWrkExp.setWidth("150");
		loadworkexp();
		cbWrkExp.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getWorkExper();
			}
		});
		tfWrkExpYr = new GERPTextField("No. of Years");
		tfWrkExpYr.setWidth("150");
		tfWrkExpDesc = new GERPTextField("Description");
		tfWrkExpDesc.setWidth("150");
		tfWrkExpDesc.setHeight("50");
		cbApStatus = new GERPComboBox("Approved Status", BASEConstants.T_HCM_JOB_VACCANCY, BASEConstants.JV_APRVDSTS);
		cbApStatus.setWidth("150");
		cbJbStatus = new GERPComboBox("Job Status", BASEConstants.T_HCM_JOB_VACCANCY, BASEConstants.JV_JOBSTS);
		cbJbStatus.setWidth("150");
		taJobDetails = new GERPTextArea("Job Details");
		taJobDetails.setHeight("30");
		taJobDetails.setWidth("150");
		// Job Title text field
		tfJobtitle = new GERPTextField("Job Title");
		// Job ClassName combobox
		cbJobClsName = new GERPComboBox("Job Classification");
		cbJobClsName.setItemCaptionPropertyId("clasficatnName");
		loadJobClassification();
		cbHirgMgr = new GERPComboBox("Hiring Manager");
		cbHirgMgr.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		cbDesgntnName = new GERPComboBox("Designation");
		cbDesgntnName.setItemCaptionPropertyId("designationName");
		loadDesignation();
		cbBrnchName = new GERPComboBox("Branch");
		cbBrnchName.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbReqstdName = new GERPComboBox("Requested By");
		cbReqstdName.setItemCaptionPropertyId("firstname");
		loadEmployeList();
		cbApvdName = new GERPComboBox("Approved By");
		cbApvdName.setItemCaptionPropertyId("firstname");
		loadEmpList();
		dfApvdDate = new GERPPopupDateField("Approved Date");
		dfApvdDate.setWidth("130");
		// // build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		VerticalLayout img = new VerticalLayout();
		new UploadDocumentUI(vlappdoc);
		img.addComponent(vlappdoc);
		img.setSpacing(true);
		img.setMargin(true);
		img.setSizeFull();
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		// Add components for Search Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(tfJobtitle);
		flColumn2.addComponent(cbJobClsName);
		flColumn3.addComponent(cbDesgntnName);
		flColumn4.addComponent(cbJbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn5 = new FormLayout();
		flColumn1.addComponent(tfJobtitle);
		flColumn1.addComponent(taJobDetails);
		flColumn1.addComponent(cbJobClsName);
		flColumn1.addComponent(cbDesgntnName);
		flColumn1.addComponent(cbBrnchName);
		flColumn2.addComponent(cbHirgMgr);
		flColumn2.addComponent(cbReqstdName);
		flColumn2.addComponent(cbApvdName);
		flColumn2.addComponent(dfApvdDate);
		flColumn2.addComponent(cbApStatus);
		flColumn3.addComponent(cbWrkExp);
		flColumn3.addComponent(tfWrkExpYr);
		flColumn3.addComponent(tfWrkExpDesc);
		flColumn3.addComponent(cbJbStatus);
		flColumn4.addComponent(vlappdoc);
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
		tblMstScrSrchRslt.setPageLength(13);
		List<JobVaccancyDM> listJobvacncy = new ArrayList<JobVaccancyDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + (String) tfJobtitle.getValue() + ", " + (Long) cbJobClsName.getValue()
				+ (String) cbJbStatus.getValue() + ", " + (Long) cbDesgntnName.getValue());
		listJobvacncy = serviceJobVaccancy.getJobVaccancyList(vaccancyId, (String) tfJobtitle.getValue(),
				(Long) cbJobClsName.getValue(), (Long) cbDesgntnName.getValue(), null, null,
				(String) cbJbStatus.getValue(), "F");
		recordCnt = listJobvacncy.size();
		beanJobVaccancyDM = new BeanItemContainer<JobVaccancyDM>(JobVaccancyDM.class);
		beanJobVaccancyDM.addAll(listJobvacncy);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the JobVaccancy. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanJobVaccancyDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "vaccancyId", "jobtitle", "jobClasfnName", "desgntnName",
				"branchName", "jobstatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Job Title", "Job Classification", "Designation",
				"Branch", "Job Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("vaccancyId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, BranchDM> beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranchDM.setBeanIdProperty("branchId");
		beanBranchDM.addAll(servicebeanBranch.getBranchList(null, (String) cbBrnchName.getValue(), null, "Active",
				companyid, "P"));
		cbBrnchName.setContainerDataSource(beanBranchDM);
	}
	
	private void loadJobClassification() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading JobClassification Search...");
		BeanContainer<Long, JobClassificationDM> beanClsFcnDM = new BeanContainer<Long, JobClassificationDM>(
				JobClassificationDM.class);
		beanClsFcnDM.setBeanIdProperty("jobClasfnId");
		beanClsFcnDM.addAll(serviceJobClassification.getJobClassificationList(null, null, companyid, "Active", "P"));
		cbJobClsName.setContainerDataSource(beanClsFcnDM);
	}
	
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.setBeanIdProperty("employeeid");
			beanEmployeeDM.addAll(servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyid,
					employeeId, null, null, null, "P"));
			cbHirgMgr.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	/*
	 * Laod Work Experince Type
	 */
	private void loadworkexp() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Relationship Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"HC_WRKEXP");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbWrkExp.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
		}
	}
	
	/*
	 * Work Experince must control flow.
	 */
	private void getWorkExper() {
		if (cbWrkExp != null) {
			if (cbWrkExp.getValue().toString().equals("Yes")) {
				tfWrkExpYr.setRequired(true);
				tfWrkExpDesc.setRequired(true);
			} else {
				tfWrkExpDesc.setRequired(false);
				tfWrkExpYr.setRequired(false);
			}
		}
	}
	
	private void loadEmployeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.setBeanIdProperty("employeeid");
			beanEmployeeDM.addAll(servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyid,
					employeeId, null, null, null, "P"));
			cbReqstdName.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadEmpList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.setBeanIdProperty("employeeid");
			beanEmployeeDM.addAll(servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyid,
					employeeId, null, null, null, "P"));
			cbApvdName.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadDesignation() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Designation Search...");
		BeanContainer<Long, DesignationDM> beanDesignationDM = new BeanContainer<Long, DesignationDM>(
				DesignationDM.class);
		beanDesignationDM.setBeanIdProperty("designationId");
		beanDesignationDM.addAll(serviceDesinatn.getDesignationList(null, null, null, null, companyid, "Active", "F"));
		cbDesgntnName.setContainerDataSource(beanDesignationDM);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
			hlUserInputLayout.removeAllComponents();
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
		catch (Exception ex) {
			logger.info("search details" + ex);
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tfJobtitle.setValue("");
		cbJobClsName.setValue(null);
		cbDesgntnName.setValue(null);
		cbJbStatus.setValue(cbJbStatus.getItemIds().iterator().next());
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfJobtitle.setRequired(true);
		cbJobClsName.setRequired(true);
		cbHirgMgr.setRequired(true);
		cbJobClsName.setRequired(true);
		cbDesgntnName.setRequired(true);
		cbBrnchName.setRequired(true);
		cbReqstdName.setRequired(true);
		cbApvdName.setRequired(true);
		cbApStatus.setRequired(true);
		cbJbStatus.setRequired(true);
		tfJobtitle.setComponentError(null);
		cbJobClsName.setComponentError(null);
		cbHirgMgr.setComponentError(null);
		cbJobClsName.setComponentError(null);
		cbDesgntnName.setComponentError(null);
		cbBrnchName.setComponentError(null);
		cbReqstdName.setComponentError(null);
		cbApvdName.setComponentError(null);
		cbApStatus.setComponentError(null);
		cbJbStatus.setComponentError(null);
		hlUserInputLayout.setSpacing(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		new UploadDocumentUI(vlappdoc);
		assembleUserInputLayout();
	}
	
	private void editJobvacncyDetails() {
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			JobVaccancyDM jobVaccancyDM = beanJobVaccancyDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			vaccancyId = jobVaccancyDM.getVaccancyId();
			if ((jobVaccancyDM.getJobtitle() != null)) {
				tfJobtitle.setValue(jobVaccancyDM.getJobtitle());
			}
			if ((jobVaccancyDM.getHiringmgr() != null)) {
				cbHirgMgr.setValue(jobVaccancyDM.getHiringmgr());
			}
			cbJobClsName.setValue(jobVaccancyDM.getJobclasfnid());
			cbBrnchName.setValue(jobVaccancyDM.getBranchid());
			cbDesgntnName.setValue(jobVaccancyDM.getDesignatnid());
			if ((jobVaccancyDM.getJobdetails() != null)) {
				taJobDetails.setValue(jobVaccancyDM.getJobdetails().toString());
			}
			if ((jobVaccancyDM.getRequestdby() != null)) {
				cbReqstdName.setValue(jobVaccancyDM.getRequestdby());
			}
			if ((jobVaccancyDM.getApprovedby() != null)) {
				cbApvdName.setValue(jobVaccancyDM.getApprovedby());
			}
			if (jobVaccancyDM.getApprovedDt() != null) {
				dfApvdDate.setValue(jobVaccancyDM.getApprovedDt());
			}
			if (jobVaccancyDM.getApprovedstatus() != null) {
				cbApStatus.setValue(jobVaccancyDM.getApprovedstatus());
			}
			if (jobVaccancyDM.getJobstatus() != null) {
				cbJbStatus.setValue(jobVaccancyDM.getJobstatus());
			}
			if (jobVaccancyDM.getWorkExp() != null) {
				tfWrkExpDesc.setValue(jobVaccancyDM.getWorkExp());
			}
			if (jobVaccancyDM.getExpYear() != null) {
				tfWrkExpYr.setValue(jobVaccancyDM.getExpYear());
			}
			if (jobVaccancyDM.getExpDesc() != null) {
				cbWrkExp.setValue(jobVaccancyDM.getExpDesc());
			}
			if (jobVaccancyDM.getApplctnform() != null) {
				byte[] certificate = jobVaccancyDM.getApplctnform();
				UploadDocumentUI test = new UploadDocumentUI(vlappdoc);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(vlappdoc);
			}
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfJobtitle.setRequired(true);
		cbJobClsName.setRequired(true);
		cbHirgMgr.setRequired(true);
		cbJobClsName.setRequired(true);
		cbDesgntnName.setRequired(true);
		cbBrnchName.setRequired(true);
		cbReqstdName.setRequired(true);
		cbApvdName.setRequired(true);
		cbApStatus.setRequired(true);
		cbJbStatus.setRequired(true);
		cbJbStatus.setComponentError(null);
		cbApStatus.setComponentError(null);
		assembleUserInputLayout();
		resetFields();
		editJobvacncyDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfJobtitle.setComponentError(null);
		cbJobClsName.setComponentError(null);
		cbHirgMgr.setComponentError(null);
		cbJobClsName.setComponentError(null);
		cbDesgntnName.setComponentError(null);
		cbBrnchName.setComponentError(null);
		cbReqstdName.setComponentError(null);
		cbApvdName.setComponentError(null);
		cbApStatus.setComponentError(null);
		cbJbStatus.setComponentError(null);
		hlUserInputLayout.setSpacing(true);
		tblMstScrSrchRslt.setVisible(true);
		boolean errorFlag = false;
		if ((tfJobtitle.getValue() == null) || tfJobtitle.getValue().trim().length() == 0) {
			tfJobtitle.setComponentError(new UserError(GERPErrorCodes.NULL_JOB_VNCY_TITLE));
			errorFlag = true;
		}
		if (cbJobClsName.getValue() == null) {
			cbJobClsName.setComponentError(new UserError(GERPErrorCodes.NULL_JOB_VNCY_CLSFN));
			errorFlag = true;
		}
		if (cbHirgMgr.getValue() == null) {
			cbHirgMgr.setComponentError(new UserError(GERPErrorCodes.NULL_HIRING_MNGR));
			errorFlag = true;
		}
		if (cbDesgntnName.getValue() == null) {
			cbDesgntnName.setComponentError(new UserError(GERPErrorCodes.NULL_DESIGNATION));
			errorFlag = true;
		}
		if (cbBrnchName.getValue() == null) {
			cbBrnchName.setComponentError(new UserError(GERPErrorCodes.NULL_BRANCH_NAME));
			errorFlag = true;
		}
		if (cbReqstdName.getValue() == null) {
			cbReqstdName.setComponentError(new UserError(GERPErrorCodes.NULL_REQUESTED_NAME));
			errorFlag = true;
		}
		if (cbApStatus.getValue() == null) {
			cbApStatus.setComponentError(new UserError(GERPErrorCodes.NULL_APPROVED_STATUS));
			errorFlag = true;
		}
		if (cbApvdName.getValue() == null) {
			cbApvdName.setComponentError(new UserError(GERPErrorCodes.NULL_APPROVED_NAME));
			errorFlag = true;
		}
		if (cbJbStatus.getValue() == null) {
			cbJbStatus.setComponentError(new UserError(GERPErrorCodes.NULL_JOB_STATUS));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			JobVaccancyDM jobVacancyobj = new JobVaccancyDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				jobVacancyobj = beanJobVaccancyDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			jobVacancyobj.setJobtitle(tfJobtitle.getValue().toString());
			jobVacancyobj.setHiringmgr((Long) cbHirgMgr.getValue());
			jobVacancyobj.setJobclasfnid((Long) cbJobClsName.getValue());
			jobVacancyobj.setDesignatnid((Long) cbDesgntnName.getValue());
			jobVacancyobj.setBranchid((Long) cbBrnchName.getValue());
			jobVacancyobj.setJobdetails(taJobDetails.getValue().toString());
			jobVacancyobj.setRequestdby((Long) cbReqstdName.getValue());
			jobVacancyobj.setApprovedby((Long) cbApvdName.getValue());
			jobVacancyobj.setWorkExp(cbWrkExp.getValue().toString());
			jobVacancyobj.setExpYear(tfWrkExpYr.getValue());
			jobVacancyobj.setExpDesc(tfWrkExpDesc.getValue());
			if (cbJbStatus.getValue() != null) {
				jobVacancyobj.setJobstatus((String) cbJbStatus.getValue());
			}
			if (cbApStatus.getValue() != null) {
				jobVacancyobj.setApprovedstatus((String) cbApStatus.getValue());
			}
			if (dfApvdDate.getValue() != null) {
				jobVacancyobj.setApprovedDt(dfApvdDate.getValue());
			}
			File file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];
			fio.read(fileContent);
			fio.close();
			jobVacancyobj.setApplctnform(fileContent);
			jobVacancyobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			jobVacancyobj.setLastUpdatedBy(username);
			serviceJobVaccancy.saveOrUpdateJobVaccancy(jobVacancyobj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for TestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_JOB_VACCANCY);
		UI.getCurrent().getSession().setAttribute("audittablepk", jobvacancyId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbJbStatus.setComponentError(null);
		tfJobtitle.setRequired(false);
		cbJobClsName.setRequired(false);
		cbHirgMgr.setRequired(false);
		cbJobClsName.setRequired(false);
		cbDesgntnName.setRequired(false);
		cbBrnchName.setRequired(false);
		cbReqstdName.setRequired(false);
		cbApvdName.setRequired(false);
		cbApStatus.setRequired(false);
		cbJbStatus.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		tfJobtitle.setValue("");
		tfJobtitle.setComponentError(null);
		cbHirgMgr.setComponentError(null);
		cbHirgMgr.setValue(null);
		cbJobClsName.setComponentError(null);
		cbJobClsName.setValue(null);
		cbJobClsName.setComponentError(null);
		cbJobClsName.setValue(null);
		cbDesgntnName.setComponentError(null);
		cbDesgntnName.setValue(null);
		cbBrnchName.setComponentError(null);
		cbBrnchName.setValue(null);
		taJobDetails.setComponentError(null);
		taJobDetails.setValue("");
		cbReqstdName.setComponentError(null);
		cbReqstdName.setValue(null);
		cbApvdName.setComponentError(null);
		cbApvdName.setValue(null);
		dfApvdDate.setComponentError(null);
		dfApvdDate.setValue(null);
		new UploadDocumentUI(vlappdoc);
		cbApStatus.setValue(null);
		tfWrkExpYr.setValue(null);
		tfWrkExpDesc.setValue(null);
		cbJbStatus.setValue(cbJbStatus.getItemIds().iterator().next());
	}
}