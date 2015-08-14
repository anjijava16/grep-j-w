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
	private TextField tffirstname, tflastname, tfemailid, tfcontactno;
	private PopupDateField dfDOA;
	private TextArea taresumkywrd;
	private ComboBox cbStatus, cbjobtitle;
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
		tffirstname = new GERPTextField("First Name");
		tflastname = new GERPTextField("Last Name");
		tfemailid = new GERPTextField("E-mail");
		tfemailid.setMaxLength(30);
		tfcontactno = new GERPTextField("Contact No.");
		tfcontactno.setMaxLength(12);
		// Resume Keyword Description Area
		taresumkywrd = new GERPTextArea("Resume Keyword");
		taresumkywrd.setHeight("25");
		taresumkywrd.setWidth("150");
		dfDOA = new GERPPopupDateField("DOA");
		dfDOA.setWidth("130");
		// Job Title combobox
		cbjobtitle = new GERPComboBox("Job Title");
		cbjobtitle.setItemCaptionPropertyId("jobtitle");
		cbjobtitle.setWidth("150");
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
		flColumn1.addComponent(cbjobtitle);
		flColumn2.addComponent(tffirstname);
		flColumn3.addComponent(tfcontactno);
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
		tfemailid.setRequired(true);
		tblMstScrSrchRslt.setPageLength(12);
		// Remove all components in Search Layout
		hlSearchLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbjobtitle);
		flColumn1.addComponent(tffirstname);
		flColumn1.addComponent(tflastname);
		flColumn1.addComponent(tfemailid);
		flColumn2.addComponent(tfcontactno);
		flColumn2.addComponent(dfDOA);
		flColumn2.addComponent(taresumkywrd);
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
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<JobCandidateDM> jobcandidatelist = new ArrayList<JobCandidateDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tffirstname.getValue() + ", " + tfcontactno.getValue()
				+ (String) cbStatus.getValue());
		jobcandidatelist = serviceJobCandidate.getJobCandidateList(null, (Long) cbjobtitle.getValue(),
				tffirstname.getValue(), tfcontactno.getValue(), (String) cbStatus.getValue());
		recordCnt = jobcandidatelist.size();
		logger.info("size" + jobcandidatelist.size());
		beanJobCandidateDM = new BeanItemContainer<JobCandidateDM>(JobCandidateDM.class);
		beanJobCandidateDM.addAll(jobcandidatelist);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the jobcandidatelist. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanJobCandidateDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "candidateId", "jobtitle", "firstName", "email",
				"contactNo", "status", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Job Title", "First Name", "E-mail", "Contact No.",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("candidateid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadJobVaccancy() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading JobVaccancy Search...");
			BeanContainer<Long, JobVaccancyDM> beanJobVaccancyDM = new BeanContainer<Long, JobVaccancyDM>(
					JobVaccancyDM.class);
			beanJobVaccancyDM.setBeanIdProperty("vaccancyId");
			beanJobVaccancyDM.addAll(serviceJobVaccancy.getJobVaccancyList(null, (String) cbjobtitle.getValue(), null,
					null, null, null, null, null));
			cbjobtitle.setContainerDataSource(beanJobVaccancyDM);
		}
		catch (Exception e) {
			e.printStackTrace();
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
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		cbjobtitle.setValue(null);
		tffirstname.setValue("");
		tfcontactno.setValue("");
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
		cbjobtitle.setRequired(true);
		tfemailid.setRequired(true);
	}
	
	public void editCandidate() {
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			JobCandidateDM jobCandidateDM = beanJobCandidateDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			if (jobCandidateDM.getJobtitle() != null) {
				cbjobtitle.setValue(jobCandidateDM.getJobtitle());
			}
			if ((jobCandidateDM.getFirstName() != null)) {
				tffirstname.setValue(jobCandidateDM.getFirstName().toString());
			}
			if ((jobCandidateDM.getLastName() != null)) {
				tflastname.setValue(jobCandidateDM.getLastName());
			}
			if ((jobCandidateDM.getEmail() != null)) {
				tfemailid.setValue(jobCandidateDM.getEmail().toString());
			}
			if ((jobCandidateDM.getContactNo() != null)) {
				tfcontactno.setValue(jobCandidateDM.getContactNo());
			}
			if ((jobCandidateDM.getDoa() != null)) {
				dfDOA.setValue(jobCandidateDM.getDoa());
			}
			if ((jobCandidateDM.getResumKeywrds() != null)) {
				taresumkywrd.setValue(jobCandidateDM.getResumKeywrds());
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
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		resetFields();
		cbjobtitle.setRequired(true);
		tfemailid.setRequired(true);
		editCandidate();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		boolean errorFlag = false;
		cbjobtitle.setComponentError(null);
		tfemailid.setComponentError(null);
		if ((cbjobtitle.getValue() == null)) {
			cbjobtitle.setComponentError(new UserError(GERPErrorCodes.NULL_JOB_CANDIDATE));
			errorFlag = true;
		}
		String emailSeq = tfemailid.getValue().toString();
		if (!emailSeq.contains("@") || !emailSeq.contains(".")) {
			tfemailid.setComponentError(new UserError(GERPErrorCodes.EMAIL_VALIDATION));
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
			JobCandidateDM jobcandidateobj = new JobCandidateDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				jobcandidateobj = beanJobCandidateDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			jobcandidateobj.setVaccancyid((Long) cbjobtitle.getValue());
			jobcandidateobj.setFirstName(tffirstname.getValue().toString());
			jobcandidateobj.setLastName(tflastname.getValue().toString());
			jobcandidateobj.setEmail(tfemailid.getValue().toString());
			jobcandidateobj.setContactNo(tfcontactno.getValue().toString());
			jobcandidateobj.setWorkExp(cbWrkExp.getValue().toString());
			jobcandidateobj.setExpYear(tfWrkExpYr.getValue());
			jobcandidateobj.setExpDesc(tfWrkExpDesc.getValue());
			jobcandidateobj.setResumKeywrds(taresumkywrd.getValue().toString());
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
			e.printStackTrace();
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
		cbjobtitle.setRequired(false);
		tfemailid.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		tffirstname.setValue("");
		tfcontactno.setValue("");
		tfcontactno.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tffirstname.setComponentError(null);
		tffirstname.setValue("");
		cbjobtitle.setComponentError(null);
		cbjobtitle.setValue(null);
		tflastname.setComponentError(null);
		tflastname.setValue("");
		tfemailid.setComponentError(null);
		tfemailid.setValue("");
		dfDOA.setComponentError(null);
		dfDOA.setValue(null);
		taresumkywrd.setComponentError(null);
		taresumkywrd.setValue("");
		tfWrkExpYr.setValue(null);
		tfWrkExpDesc.setValue(null);
		new UploadDocumentUI(vlresumdoc);
	}
}
