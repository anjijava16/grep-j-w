/**
 * File Name	:	JobInterview.java
 * Description	:	this class is used for add/edit Job Candidate Interview details. 
 * Author		:	KAVITHA V M
 * Date			:	20-August-2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 *  Version         Date          Modified By             Remarks
 * 0.1         	20-August-2014    KAVITHA V M	  Initial Version       
 * 
 */
package com.gnts.hcm.txn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTimeField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.JobCandidateDM;
import com.gnts.hcm.domain.txn.JobInterviewDM;
import com.gnts.hcm.domain.txn.JobVaccancyDM;
import com.gnts.hcm.service.txn.JobCandidateService;
import com.gnts.hcm.service.txn.JobInterviewService;
import com.gnts.hcm.service.txn.JobVaccancyService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

public class JobInterview extends BaseUI {
	// Bean creation
	private JobInterviewService serviceJobInterview = (JobInterviewService) SpringContextHelper.getBean("JobInterview");
	private JobCandidateService serviceJobCandidate = (JobCandidateService) SpringContextHelper.getBean("JobCandidate");
	private JobVaccancyService serviceJobVaccancy = (JobVaccancyService) SpringContextHelper.getBean("JobVaccancy");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private ComboBox cbCandidateName, cbJobTitle, cbStatus, cbinterviewerid, cbIntrvwLevel;
	private PopupDateField dfIntrvwDate;
	private GERPTimeField tfIntrvwTime;
	private TextArea taIntrvwDesc;
	// BeanItemContain
	private BeanItemContainer<JobInterviewDM> beanJobInterviewDM = null;
	// local variables declaration
	private Long companyid, employeeId, moduleId;
	private int recordCnt = 0;
	private String username, jobInterviewId;
	// Initialize logger
	private Logger logger = Logger.getLogger(JobInterview.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public JobInterview() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside JobInterview() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Printing JobInterview UI");
		// Status ComboBox
		cbIntrvwLevel = new GERPComboBox("Interview Level");
		cbIntrvwLevel.setItemCaptionPropertyId("lookupname");
		loadIinterviewLevels();
		cbStatus = new GERPComboBox("Status");
		cbStatus.setItemCaptionPropertyId("lookupname");
		loadInterviewStatus();
		taIntrvwDesc = new GERPTextArea("Interview Description");
		taIntrvwDesc.setHeight("30");
		taIntrvwDesc.setWidth("150");
		tfIntrvwTime = new GERPTimeField("Interview Time");
		loadEmployeeList();
		cbCandidateName = new GERPComboBox("Candidate Name");
		cbCandidateName.setItemCaptionPropertyId("firstName");
		loadJobCandidate();
		cbJobTitle = new GERPComboBox("Vaccancy Title");
		cbJobTitle.setItemCaptionPropertyId("jobtitle");
		loadJobVaccancy();
		cbinterviewerid = new GERPComboBox("Interviewer Name");
		cbinterviewerid.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		dfIntrvwDate = new GERPPopupDateField("Interview Date");
		dfIntrvwDate.setWidth("150");
		// // build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	//
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		// Add components for Search Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(cbCandidateName);
		flColumn2.addComponent(cbJobTitle);
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
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbCandidateName);
		flColumn1.addComponent(cbJobTitle);
		flColumn2.addComponent(dfIntrvwDate);
		flColumn2.addComponent(taIntrvwDesc);
		flColumn3.addComponent(cbIntrvwLevel);
		flColumn3.addComponent(cbinterviewerid);
		flColumn4.addComponent(tfIntrvwTime);
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
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<JobInterviewDM> loadjobinterviewList = new ArrayList<JobInterviewDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + (Long) cbCandidateName.getValue() + ", " + (Long) cbJobTitle.getValue()
				+ (String) cbStatus.getValue());
		loadjobinterviewList = serviceJobInterview.getJobInterviewyList(null, (Long) cbCandidateName.getValue(),
				(Long) cbJobTitle.getValue(), null, (String) cbStatus.getValue());
		recordCnt = loadjobinterviewList.size();
		beanJobInterviewDM = new BeanItemContainer<JobInterviewDM>(JobInterviewDM.class);
		beanJobInterviewDM.addAll(loadjobinterviewList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the JobInterview. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanJobInterviewDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "interviewId", "candidateName", "jobtitle", "status",
				"lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Candidate Name", "Job Title", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("interviewId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadJobCandidate() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Candidate Search...");
		BeanContainer<Long, JobCandidateDM> beanJobCandidateDM = new BeanContainer<Long, JobCandidateDM>(
				JobCandidateDM.class);
		beanJobCandidateDM.setBeanIdProperty("candidateId");
		beanJobCandidateDM.addAll(serviceJobCandidate.getJobCandidateList(null, null,
				(String) cbCandidateName.getValue(), null, null));
		cbCandidateName.setContainerDataSource(beanJobCandidateDM);
	}
	
	private void loadJobVaccancy() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading JobVaccancy Search...");
		BeanContainer<Long, JobVaccancyDM> beanJobVaccancyDM = new BeanContainer<Long, JobVaccancyDM>(
				JobVaccancyDM.class);
		beanJobVaccancyDM.setBeanIdProperty("vaccancyId");
		beanJobVaccancyDM.addAll(serviceJobVaccancy.getJobVaccancyList(null, null, null, null, null, null, null, "F"));
		cbJobTitle.setContainerDataSource(beanJobVaccancyDM);
	}
	
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.setBeanIdProperty("employeeid");
			beanEmployeeDM.addAll(servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyid,
					employeeId, null, null, null, "P"));
			cbinterviewerid.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
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
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbCandidateName.setValue(null);
		cbJobTitle.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbCandidateName.setRequired(true);
		cbJobTitle.setRequired(true);
		cbinterviewerid.setRequired(true);
		cbIntrvwLevel.setRequired(true);
		cbJobTitle.setComponentError(null);
		cbCandidateName.setComponentError(null);
		cbinterviewerid.setComponentError(null);
		hlUserInputLayout.setSpacing(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		assembleUserInputLayout();
	}
	
	private void editJobIntervwdetails() {
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			JobInterviewDM editjbintrvwList = beanJobInterviewDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			if ((editjbintrvwList.getCandidateid() != null)) {
				cbCandidateName.setValue(editjbintrvwList.getCandidateid());
			}
			if ((editjbintrvwList.getVacancyid() != null)) {
				cbJobTitle.setValue(editjbintrvwList.getVacancyid());
			}
			if (editjbintrvwList.getInterviewDt() != null) {
				dfIntrvwDate.setValue(editjbintrvwList.getInterviewDt());
			}
			if ((editjbintrvwList.getIntervwdesc() != null)) {
				taIntrvwDesc.setValue(editjbintrvwList.getIntervwdesc().toString());
			}
			if ((editjbintrvwList.getIntervwlevel() != null)) {
				cbIntrvwLevel.setValue(editjbintrvwList.getIntervwlevel().toString());
			}
			tfIntrvwTime.setTime(editjbintrvwList.getIntervwtime());
			cbStatus.setValue(editjbintrvwList.getStatus());
			cbinterviewerid.setValue(editjbintrvwList.getInterviewerid());
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbCandidateName.setRequired(true);
		cbinterviewerid.setRequired(true);
		cbIntrvwLevel.setRequired(true);
		cbJobTitle.setRequired(true);
		assembleUserInputLayout();
		resetFields();
		editJobIntervwdetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + cbJobTitle.getValue());
		cbCandidateName.setComponentError(null);
		cbinterviewerid.setComponentError(null);
		cbJobTitle.setComponentError(null);
		cbIntrvwLevel.setComponentError(null);
		boolean errorFlag = false;
		if (cbCandidateName.getValue() == null) {
			cbCandidateName.setComponentError(new UserError(GERPErrorCodes.NULL_JOB_INTRVW));
			errorFlag = true;
		}
		if (cbJobTitle.getValue() == null) {
			cbJobTitle.setComponentError(new UserError(GERPErrorCodes.NULL_JOB_VANCY_TITLE));
			errorFlag = true;
		}
		if (cbinterviewerid.getValue() == null) {
			cbinterviewerid.setComponentError(new UserError(GERPErrorCodes.NULL_INTERVIEWER_ID));
			errorFlag = true;
		}
		if (cbIntrvwLevel.getValue() == null) {
			cbIntrvwLevel.setComponentError(new UserError(GERPErrorCodes.NULL_INTERVIEWER_LEVEL));
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
			JobInterviewDM jobInterviewobj = new JobInterviewDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				jobInterviewobj = beanJobInterviewDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			jobInterviewobj.setCandidateid((Long) cbCandidateName.getValue());
			jobInterviewobj.setVacancyid((Long) cbJobTitle.getValue());
			if (dfIntrvwDate.getValue() != null) {
				jobInterviewobj.setInterviewDt(dfIntrvwDate.getValue());
			}
			jobInterviewobj.setIntervwdesc(taIntrvwDesc.getValue().toString());
			jobInterviewobj.setIntervwlevel(cbIntrvwLevel.getValue().toString());
			jobInterviewobj.setInterviewerid((Long) cbinterviewerid.getValue());
			jobInterviewobj.setStatus(cbStatus.getValue().toString());
			if (tfIntrvwTime.getValue() != null) {
				jobInterviewobj.setIntervwtime(tfIntrvwTime.getHorsMunites());
			}
			jobInterviewobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			jobInterviewobj.setLastUpdatedBy(username);
			serviceJobInterview.saveOrUpdateJobInterview(jobInterviewobj);
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
				+ "Getting audit record for JobInterview. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_JOB_INTERVIEW);
		UI.getCurrent().getSession().setAttribute("audittablepk", jobInterviewId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		cbCandidateName.setRequired(false);
		cbJobTitle.setRequired(false);
		cbIntrvwLevel.setRequired(false);
		cbinterviewerid.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		assembleSearchLayout();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		cbCandidateName.setValue(null);
		cbCandidateName.setComponentError(null);
		cbJobTitle.setComponentError(null);
		cbJobTitle.setValue(null);
		dfIntrvwDate.setComponentError(null);
		dfIntrvwDate.setValue(new Date());
		taIntrvwDesc.setComponentError(null);
		taIntrvwDesc.setValue("");
		cbIntrvwLevel.setComponentError(null);
		cbIntrvwLevel.setValue(null);
		cbinterviewerid.setComponentError(null);
		cbinterviewerid.setValue(null);
		tfIntrvwTime.setComponentError(null);
		tfIntrvwTime.setValue(null);
		cbStatus.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	private void loadIinterviewLevels() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Interview Level Search...");
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
				"HC_INVWLVL"));
		cbIntrvwLevel.setContainerDataSource(beanCompanyLookUp);
	}
	
	private void loadInterviewStatus() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Interview Status Search...");
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
				"HC_INVWSTS"));
		cbStatus.setContainerDataSource(beanCompanyLookUp);
	}
}
