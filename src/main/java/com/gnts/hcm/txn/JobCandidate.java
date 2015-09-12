/**
 * File Name 		: JobCandidate .java 
 * Description 		: this class is used for add/edit JobCandidate  details. 
 * Author 			:  KAVITHA V M 
 * Date 			: 14-Aug-2014	
 *
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. 
 * All rights reserved.
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 *
 * Version      Date           	Modified By 	 Remarks
 * 0.1          14-Aug-2014	    KAVITHA V M	     Initial Version
 * 
 */
package com.gnts.hcm.txn;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
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
import com.gnts.hcm.domain.txn.JobCandidateDM;
import com.gnts.hcm.domain.txn.JobVaccancyDM;
import com.gnts.hcm.service.txn.JobCandidateService;
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

public class JobCandidate extends BaseUI {
	// Bean creation
	private JobCandidateService serviceJobCandidate = (JobCandidateService) SpringContextHelper.getBean("JobCandidate");
	private JobVaccancyService serviceJobVaccancy = (JobVaccancyService) SpringContextHelper.getBean("JobVaccancy");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfFirstname, tfLastname, tfEMailid, tfContactno;
	private PopupDateField dfDOA;
	private TextArea taResumkywrd;
	private ComboBox cbStatus, cbJobtitle;
	private VerticalLayout vlresumdoc = new VerticalLayout();
	// BeanItemContainer
	private BeanItemContainer<JobCandidateDM> beanJobCandidateDM = null;
	// local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	private GERPComboBox cbWrkExp;
	private GERPTextField tfWrkExpYr, tfWrkExpDesc;
	// Initialize logger
	private Logger logger = Logger.getLogger(JobCandidate.class);
	private String jobCandidateId;
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public JobCandidate() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		logger.info("username >>>>>>>>>>>> " + username);
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside JobCandidate() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Printing JobCandidate UI");
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
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("150");
		// Job Candidate Description text field
		tfFirstname = new GERPTextField("First Name");
		tfLastname = new GERPTextField("Last Name");
		tfEMailid = new GERPTextField("E-mail");
		tfEMailid.setMaxLength(30);
		tfContactno = new GERPTextField("Contact No.");
		tfContactno.setMaxLength(12);
		// Resume Keyword Description Area
		taResumkywrd = new GERPTextArea("Resume Keyword");
		taResumkywrd.setHeight("25");
		taResumkywrd.setWidth("150");
		dfDOA = new GERPPopupDateField("DOA");
		dfDOA.setWidth("130");
		// Job Title combobox
		cbJobtitle = new GERPComboBox("Job Title");
		cbJobtitle.setItemCaptionPropertyId("jobtitle");
		cbJobtitle.setWidth("150");
		loadJobVaccancy();
		// // build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
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
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbJobtitle);
		flColumn2.addComponent(tfFirstname);
		flColumn3.addComponent(tfContactno);
		flColumn4.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		tblMstScrSrchRslt.setPageLength(12);
		// Remove all components in Search Layout
		hlSearchLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbJobtitle);
		flColumn1.addComponent(tfFirstname);
		flColumn1.addComponent(tfLastname);
		flColumn1.addComponent(tfEMailid);
		flColumn2.addComponent(tfContactno);
		flColumn2.addComponent(dfDOA);
		flColumn2.addComponent(taResumkywrd);
		flColumn2.addComponent(cbWrkExp);
		flColumn3.addComponent(tfWrkExpYr);
		flColumn3.addComponent(tfWrkExpDesc);
		flColumn3.addComponent(cbStatus);
		flColumn4.addComponent(vlresumdoc);
		flColumn4.setMargin(true);
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
			List<JobCandidateDM> list = new ArrayList<JobCandidateDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfFirstname.getValue() + ", " + tfContactno.getValue()
					+ (String) cbStatus.getValue());
			list = serviceJobCandidate.getJobCandidateList(null, (Long) cbJobtitle.getValue(), tfFirstname.getValue(),
					tfContactno.getValue(), (String) cbStatus.getValue());
			recordCnt = list.size();
			logger.info("size" + list.size());
			beanJobCandidateDM = new BeanItemContainer<JobCandidateDM>(JobCandidateDM.class);
			beanJobCandidateDM.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the jobcandidatelist. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanJobCandidateDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "candidateId", "jobtitle", "firstName", "email",
					"contactNo", "status", "lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Job Title", "First Name", "E-mail",
					"Contact No.", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("candidateid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadJobVaccancy() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading JobVaccancy Search...");
			BeanContainer<Long, JobVaccancyDM> beanJobVaccancyDM = new BeanContainer<Long, JobVaccancyDM>(
					JobVaccancyDM.class);
			beanJobVaccancyDM.setBeanIdProperty("vaccancyId");
			beanJobVaccancyDM.addAll(serviceJobVaccancy.getJobVaccancyList(null, (String) cbJobtitle.getValue(), null,
					null, null, null, null, null));
			cbJobtitle.setContainerDataSource(beanJobVaccancyDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
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
	
	/*
	 * Laod Work Experince Type
	 */
	private void loadworkexp() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Relationship Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"HC_WRKEXP"));
			cbWrkExp.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * Work Experince must control flow.
	 */
	private void getWorkExper() {
		try {
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
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		cbJobtitle.setValue(null);
		tfFirstname.setValue("");
		tfContactno.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		new UploadDocumentUI(vlresumdoc);
		assembleUserInputLayout();
		resetFields();
		cbJobtitle.setRequired(true);
	}
	
	private void editCandidate() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				JobCandidateDM jobCandidateDM = beanJobCandidateDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				if (jobCandidateDM.getJobtitle() != null) {
					cbJobtitle.setValue(jobCandidateDM.getJobtitle());
				}
				if ((jobCandidateDM.getFirstName() != null)) {
					tfFirstname.setValue(jobCandidateDM.getFirstName().toString());
				}
				if ((jobCandidateDM.getLastName() != null)) {
					tfLastname.setValue(jobCandidateDM.getLastName());
				}
				if ((jobCandidateDM.getEmail() != null)) {
					tfEMailid.setValue(jobCandidateDM.getEmail().toString());
				}
				if ((jobCandidateDM.getContactNo() != null)) {
					tfContactno.setValue(jobCandidateDM.getContactNo());
				}
				if ((jobCandidateDM.getDoa() != null)) {
					dfDOA.setValue(jobCandidateDM.getDoa());
				}
				if ((jobCandidateDM.getResumKeywrds() != null)) {
					taResumkywrd.setValue(jobCandidateDM.getResumKeywrds());
				}
				if ((jobCandidateDM.getStatus() != null)) {
					cbStatus.setValue(jobCandidateDM.getStatus());
				}
				if (jobCandidateDM.getWorkExp() != null) {
					tfWrkExpDesc.setValue(jobCandidateDM.getWorkExp());
				}
				if (jobCandidateDM.getExpYear() != null) {
					tfWrkExpYr.setValue(jobCandidateDM.getExpYear());
				}
				if (jobCandidateDM.getExpDesc() != null) {
					cbWrkExp.setValue(jobCandidateDM.getExpDesc());
				}
				if (jobCandidateDM.getResume() != null) {
					byte[] certificate = jobCandidateDM.getResume();
					UploadDocumentUI test = new UploadDocumentUI(vlresumdoc);
					test.displaycertificate(certificate);
				} else {
					new UploadDocumentUI(vlresumdoc);
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		resetFields();
		cbJobtitle.setRequired(true);
		editCandidate();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		boolean errorFlag = false;
		cbJobtitle.setComponentError(null);
		//tfEMailid.setComponentError(null);
		if ((cbJobtitle.getValue() == null)) {
			cbJobtitle.setComponentError(new UserError(GERPErrorCodes.NULL_JOB_CANDIDATE));
			errorFlag = true;
		}
	/*	String emailSeq = tfEMailid.getValue().toString();
		if (!emailSeq.contains("@") || !emailSeq.contains(".")) {
			tfEMailid.setComponentError(new UserError(GERPErrorCodes.EMAIL_VALIDATION));
			errorFlag = true;
		}*/
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			JobCandidateDM jobcandidateobj = new JobCandidateDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				jobcandidateobj = beanJobCandidateDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			jobcandidateobj.setVaccancyid((Long) cbJobtitle.getValue());
			jobcandidateobj.setFirstName(tfFirstname.getValue().toString());
			jobcandidateobj.setLastName(tfLastname.getValue().toString());
			jobcandidateobj.setEmail(tfEMailid.getValue().toString());
			jobcandidateobj.setContactNo(tfContactno.getValue().toString());
			jobcandidateobj.setWorkExp(cbWrkExp.getValue().toString());
			jobcandidateobj.setExpYear(tfWrkExpYr.getValue());
			jobcandidateobj.setExpDesc(tfWrkExpDesc.getValue());
			jobcandidateobj.setResumKeywrds(taResumkywrd.getValue().toString());
			if (cbStatus.getValue() != null) {
				jobcandidateobj.setStatus((String) cbStatus.getValue());
			}
			if (dfDOA.getValue() != null) {
				jobcandidateobj.setDoa(dfDOA.getValue());
			}
			File file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];
			fio.read(fileContent);
			fio.close();
			jobcandidateobj.setResume(fileContent);
			jobcandidateobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			jobcandidateobj.setLastUpdatedBy(username);
			serviceJobCandidate.saveJobCandidateDetails(jobcandidateobj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for jobCandidateId. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_JOB_CANDIDATE);
		UI.getCurrent().getSession().setAttribute("audittablepk", jobCandidateId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tblMstScrSrchRslt.setValue(null);
		cbJobtitle.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		tfFirstname.setValue("");
		tfContactno.setValue("");
		tfContactno.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfFirstname.setComponentError(null);
		tfFirstname.setValue("");
		cbJobtitle.setComponentError(null);
		cbJobtitle.setValue(null);
		tfLastname.setComponentError(null);
		tfLastname.setValue("");
		tfEMailid.setValue("");
		dfDOA.setComponentError(null);
		dfDOA.setValue(null);
		taResumkywrd.setComponentError(null);
		taResumkywrd.setValue("");
		tfWrkExpYr.setValue(null);
		tfWrkExpDesc.setValue(null);
		new UploadDocumentUI(vlresumdoc);
	}
}
