/**
 * File Name 		: Designation.java 
 * Description 		: this class is used for add/edit Designation details. 
 * Author 			: MADHU T 
 * Date 			: 15-July-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         15-July-2014        	MADHU T		        Initial Version
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
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.DesignationDM;
import com.gnts.hcm.domain.mst.GradeDM;
import com.gnts.hcm.domain.mst.JobClassificationDM;
import com.gnts.hcm.service.mst.DesignationService;
import com.gnts.hcm.service.mst.GradeService;
import com.gnts.hcm.service.mst.JobClassificationService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Designation extends BaseUI {
	// Bean creation
	private DesignationService serviceDesignation = (DesignationService) SpringContextHelper.getBean("Designation");
	private GradeService serviceGrade = (GradeService) SpringContextHelper.getBean("Grade");
	private JobClassificationService serviceJobclsFcn = (JobClassificationService) SpringContextHelper
			.getBean("JobClassification");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private HorizontalLayout hlimage = new HorizontalLayout();
	// Add User Input Controls
	private TextField tfDescName;
	private ComboBox cbStatus, cbGRDDesc, cbJobClsName;
	// BeanItemContainer
	private BeanItemContainer<DesignationDM> beanDesignationDM = null;
	// local variables declaration
	private Long companyid;
	private String departId, pkDesigId;
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(Grade.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Designation() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Designation() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Designation UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("150");
		// Designation Name text field
		tfDescName = new GERPTextField("Designation Name");
		// Grade Description combobox
		cbGRDDesc = new GERPComboBox("Grade Desc.");
		cbGRDDesc.setItemCaptionPropertyId("gradeDESC");
		loadGRDLvl();
		// Job Spec Image
		hlimage.setCaption("Job Specification Image");
		// Job ClassName combobox
		cbJobClsName = new GERPComboBox("Job Classification Name");
		cbJobClsName.setItemCaptionPropertyId("clasficatnName");
		loadJobClassification();
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
		flColumn4 = new FormLayout();
		flColumn1.addComponent(tfDescName);
		flColumn2.addComponent(cbGRDDesc);
		flColumn3.addComponent(cbJobClsName);
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
		// Remove all components in Search Layout
		hlSearchLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(tfDescName);
		flColumn1.addComponent(cbGRDDesc);
		flColumn2.addComponent(cbJobClsName);
		flColumn2.addComponent(cbStatus);
		flColumn3.addComponent(hlimage);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<DesignationDM> listDesignation = new ArrayList<DesignationDM>();
		Long clsfcnId = null;
		if (cbJobClsName.getValue() != null) {
			clsfcnId = (Long.valueOf(cbJobClsName.getValue().toString()));
		}
		Long gradeId = null;
		if (cbGRDDesc.getValue() != null) {
			gradeId = (Long.valueOf(cbGRDDesc.getValue().toString()));
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfDescName.getValue() + ", " + tfDescName.getValue()
				+ (String) cbStatus.getValue() + ", " + clsfcnId);
		listDesignation = serviceDesignation.getDesignationList(null, gradeId, tfDescName.getValue(), clsfcnId,
				companyid, (String) cbStatus.getValue(), "F");
		recordCnt = listDesignation.size();
		beanDesignationDM = new BeanItemContainer<DesignationDM>(DesignationDM.class);
		beanDesignationDM.addAll(listDesignation);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Grade. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanDesignationDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "designationId", "designationName", "gradeDesc",
				"jobClasfnName", "status", "lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Designation Name", "Grade Desc.",
				"Job classification Name", "Status", "Last Updated Date", "Last Updated By", });
		tblMstScrSrchRslt.setColumnAlignment("designationId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfDescName.setValue("");
		cbGRDDesc.setValue(null);
		cbJobClsName.setValue(null);
		tfDescName.setComponentError(null);
		cbGRDDesc.setComponentError(null);
		cbJobClsName.setComponentError(null);
		new UploadUI(hlimage);
		UI.getCurrent().getSession().setAttribute("isFileUploaded", false);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editDesignation() {
		DesignationDM editDesignation = beanDesignationDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		pkDesigId = editDesignation.getDesignationId().toString();
		if (editDesignation.getDesignationName() != null) {
			tfDescName.setValue(editDesignation.getDesignationName());
		}
		if (editDesignation.getJobSpec() != null) {
			hlimage.removeAllComponents();
			byte[] myimage = (byte[]) editDesignation.getJobSpec();
			UploadUI uploadObject = new UploadUI(hlimage);
			uploadObject.dispayImage(myimage, editDesignation.getDesignationName());
		} else {
			try {
				new UploadUI(hlimage);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		cbStatus.setValue(editDesignation.getStatus());
		cbGRDDesc.setValue(Long.valueOf(editDesignation.getGradeId()));
		cbJobClsName.setValue(Long.valueOf(editDesignation.getJobClasfnId().toString()));
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
		tfDescName.setValue("");
		cbGRDDesc.setValue(null);
		cbJobClsName.setValue(null);
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
		tfDescName.setRequired(true);
		cbGRDDesc.setRequired(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Dept. ID " + departId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_DESIGNATION);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkDesigId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfDescName.setRequired(false);
		cbGRDDesc.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		tfDescName.setRequired(true);
		cbGRDDesc.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editDesignation();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfDescName.setComponentError(null);
		cbGRDDesc.setComponentError(null);
		cbJobClsName.setComponentError(null);
		errorFlag = false;
		if ((tfDescName.getValue() == null) || tfDescName.getValue().trim().length() == 0) {
			tfDescName.setComponentError(new UserError(GERPErrorCodes.NULL_DESIGNATION));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfDescName.getValue());
			errorFlag = true;
		}
		if (cbGRDDesc.getValue() == null) {
			cbGRDDesc.setComponentError(new UserError(GERPErrorCodes.NULL_GRADE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbGRDDesc.getValue());
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
			DesignationDM designationObj = new DesignationDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				designationObj = beanDesignationDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (tfDescName.getValue() != null) {
				designationObj.setDesignationName(tfDescName.getValue().toString());
			}
			if (cbGRDDesc.getValue() != null) {
				designationObj.setGradeId((Long) cbGRDDesc.getValue());
			}
			if (cbJobClsName.getValue() != null) {
				designationObj.setJobClasfnId((Long) cbJobClsName.getValue());
			}
			designationObj.setCmpId(companyid);
			if (cbStatus.getValue() != null) {
				designationObj.setStatus((String) cbStatus.getValue());
			}
			if ((Boolean) UI.getCurrent().getSession().getAttribute("isFileUploaded")) {
				try {
					designationObj.setJobSpec((byte[]) UI.getCurrent().getSession().getAttribute("imagebyte"));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				designationObj.setJobSpec(null);
			}
			designationObj.setLastUpdatedDate(DateUtils.getcurrentdate());
			designationObj.setLastUpdatedBy(username);
			serviceDesignation.saveAndUpdate(designationObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadGRDLvl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
			BeanContainer<Long, GradeDM> beanGradeDM = new BeanContainer<Long, GradeDM>(GradeDM.class);
			beanGradeDM.setBeanIdProperty("gradeId");
			beanGradeDM.addAll(serviceGrade.getGradeList(null, null, null, companyid, "Active", "P"));
			cbGRDDesc.setContainerDataSource(beanGradeDM);
		}
		catch (Exception e) {
		}
	}
	
	private void loadJobClassification() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
			BeanContainer<Long, JobClassificationDM> beanClsFcnDM = new BeanContainer<Long, JobClassificationDM>(
					JobClassificationDM.class);
			beanClsFcnDM.setBeanIdProperty("jobClasfnId");
			beanClsFcnDM.addAll(serviceJobclsFcn.getJobClassificationList(null, null, companyid, "Active", "P"));
			cbJobClsName.setContainerDataSource(beanClsFcnDM);
		}
		catch (Exception e) {
		}
	}
}
