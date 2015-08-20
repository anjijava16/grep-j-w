/**
 * File Name 	 :   AppraisalEmpIncident.java 
 * Description 	 :   This Screen Purpose for Modify the Appraisal EmployeeIncident Details.Add the Appraisal EmployeeIncident  process should be directly added in DB.
 * Author 		 :   sudhakar 
 * Date 		 :   16-Oct-2014
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS Technologies pvt. ltd.
 * 
 * Version 	 Date          	 Modified By     Remarks
 * 0.1       16-Oct-2014     Sudhakar        Initial Version
 */
package com.gnts.hcm.txn;

import java.util.ArrayList;
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
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.AppraisalEmpIncidentDM;
import com.gnts.hcm.domain.txn.KpiGroupDM;
import com.gnts.hcm.service.txn.AppraisalEmpIncidentService;
import com.gnts.hcm.service.txn.KpiGroupService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class AppraisalEmpIncident extends BaseUI {
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private AppraisalEmpIncidentService serviceappempincident = (AppraisalEmpIncidentService) SpringContextHelper
			.getBean("AppraisalEmpIncident");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private KpiGroupService serviceKpiGroup = (KpiGroupService) SpringContextHelper.getBean("KpiGroup");
	// Form layout for input controls
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Bean container
	private BeanItemContainer<AppraisalEmpIncidentDM> beanAppraisalEmpIncidentDM = null;
	// User Input Components
	private ComboBox cbEmployee, cbincidenttype, cbKPIGroup, cbincidentstatus, cbSeverity;
	private TextField tfTitle, tfBusinessValue, tfResponse, tfempagreed;
	private TextArea taDescription, taComments;
	private String loginUserName;
	private Long companyId;
	private int recordCnt = 0;
	private Long attProcId;
	// Initialize Logger
	private Logger logger = Logger.getLogger(AppraisalEmpIncident.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public AppraisalEmpIncident() {
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Inside AppraisalEmpIncident() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Painting AppraisalEmpIncident UI");
		tfBusinessValue = new GERPTextField("Business Value");
		tfBusinessValue.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfBusinessValue.setComponentError(null);
				if (tfBusinessValue.getValue() != null) {
					if (!tfBusinessValue.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
						tfBusinessValue.setComponentError(new UserError(GERPErrorCodes.NULL_BUSINESSVALUE));
					} else {
						tfBusinessValue.setComponentError(null);
					}
				}
			}
		});
		// Combo Boxes
		cbEmployee = new GERPComboBox("Employee Name");
		cbEmployee.setWidth("200px");
		cbEmployee.setItemCaptionPropertyId("fullname");
		loadEmployee();
		cbKPIGroup = new GERPComboBox("KPI Group Name");
		cbKPIGroup.setWidth("200px");
		cbKPIGroup.setItemCaptionPropertyId("kpigroupname");
		loadKpiGroupNameList();
		cbincidentstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbincidenttype = new GERPComboBox("Incident Type", BASEConstants.T_HCM_APPRAISAL_EMP_INCIDENT,
				BASEConstants.APP_EMPINCIDENT);
		cbincidenttype.setWidth("200px");
		cbSeverity = new GERPComboBox("Incident Severity");
		cbSeverity.setWidth("200px");
		cbSeverity.setItemCaptionPropertyId("lookupname");
		loadincidentseverity();
		// TextFields
		tfTitle = new GERPTextField("Incident Title");
		tfTitle.setWidth("200");
		tfTitle.setMaxLength(10);
		tfBusinessValue = new GERPTextField("Business Value");
		tfBusinessValue.setWidth("200");
		tfBusinessValue.setMaxLength(10);
		tfResponse = new GERPTextField("Employee Response");
		tfResponse.setWidth("200");
		tfResponse.setMaxLength(10);
		tfempagreed = new GERPTextField("Employee Agreed");
		tfempagreed.setWidth("200");
		tfempagreed.setMaxLength(10);
		// TextAreas
		taDescription = new GERPTextArea("Incident Desc");
		taDescription.setWidth("150");
		taDescription.setHeight("50");
		taComments = new GERPTextArea("Reviewer Comments");
		taComments.setWidth("150");
		taComments.setHeight("50");
		// Create form layouts to hold the input items
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		loadSrchRslt();
	}
	
	// Load Employee List
	private void loadEmployee() {
		BeanContainer<Long, EmployeeDM> bean = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		bean.setBeanIdProperty("employeeid");
		bean.addAll(servicebeanEmployee.getEmployeeList((String) cbEmployee.getValue(), null, null, null, null, null,
				null, null, null, "P"));
		cbEmployee.setContainerDataSource(bean);
	}
	
	// Load Load Kpigroup Name List
	private void loadKpiGroupNameList() {
		BeanContainer<Long, KpiGroupDM> beanEmployee = new BeanContainer<Long, KpiGroupDM>(KpiGroupDM.class);
		beanEmployee.setBeanIdProperty("kpigrpid");
		beanEmployee.addAll(serviceKpiGroup.getkpigrouplist(null, null, companyId, null, "Active", "F"));
		cbKPIGroup.setContainerDataSource(beanEmployee);
	}
	
	// Load Incident Severity List
	private void loadincidentseverity() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyId, null, "Active",
					"HC_INCISEV"));
			cbSeverity.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		// add the user input items into appropriate form layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol1.addComponent(cbEmployee);
		flcol2.addComponent(cbincidentstatus);
		// add the form layouts into user input layout
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.addComponent(flcol1);
		hlSearchLayout.addComponent(flcol2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		hlUserInputLayout.removeAllComponents();
		// Remove all components in Search Layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol1.addComponent(cbEmployee);
		flcol1.addComponent(cbKPIGroup);
		flcol1.addComponent(cbSeverity);
		flcol1.addComponent(tfempagreed);
		flcol2.addComponent(tfTitle);
		flcol2.addComponent(cbincidenttype);
		flcol2.addComponent(tfBusinessValue);
		flcol2.addComponent(tfResponse);
		flcol3.addComponent(taDescription);
		flcol3.addComponent(taComments);
		flcol4.addComponent(cbincidentstatus);
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(flcol1);
		hlUserInputLayout.addComponent(flcol2);
		hlUserInputLayout.addComponent(flcol3);
		hlUserInputLayout.addComponent(flcol4);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		tblMstScrSrchRslt.setPageLength(15);
		List<AppraisalEmpIncidentDM> appempincidentList = new ArrayList<AppraisalEmpIncidentDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Search Parameters are "
				+ companyId + ", " + (Long) cbEmployee.getValue() + ", " + (String) cbincidentstatus.getValue());
		appempincidentList = serviceappempincident.getAppEmpIncidentList(null, (Long) cbEmployee.getValue(), null, null,
				null, null, null, (String) cbincidentstatus.getValue(), "F");
		recordCnt = appempincidentList.size();
		beanAppraisalEmpIncidentDM = new BeanItemContainer<AppraisalEmpIncidentDM>(AppraisalEmpIncidentDM.class);
		beanAppraisalEmpIncidentDM.addAll(appempincidentList);
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Got the IT Other Income List result set");
		tblMstScrSrchRslt.setContainerDataSource(beanAppraisalEmpIncidentDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "attProcId", "empName", "incidentType", "incidentSeverity",
				"incidentTitle", "status", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Incident Type",
				"Incident Severity", "Incident Title", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records:" + recordCnt);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		// cbincidentstatus.setValue(cbincidentstatus.getItemIds().iterator().next());
		cbEmployee.setValue(null);
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		cbEmployee.setRequired(true);
		cbKPIGroup.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		cbEmployee.setRequired(true);
		cbKPIGroup.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editappempincident();
	}
	
	private void editappempincident() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Editing the selected record");
		if (tblMstScrSrchRslt.getValue() != null) {
			AppraisalEmpIncidentDM editappempinObj = beanAppraisalEmpIncidentDM.getItem(tblMstScrSrchRslt.getValue())
					.getBean();
			if (editappempinObj.getEmpid() != null) {
				cbEmployee.setValue(editappempinObj.getEmpid());
			}
			if (editappempinObj.getKpiGrpId() != null) {
				cbKPIGroup.setValue(editappempinObj.getKpiGrpId());
			}
			if (editappempinObj.getStatus() != null) {
				cbincidentstatus.setValue(editappempinObj.getStatus());
			}
			if (editappempinObj.getIncidentSeverity() != null) {
				cbSeverity.setValue(editappempinObj.getIncidentSeverity());
			}
			if (editappempinObj.getIncidentType() != null) {
				cbincidenttype.setValue(editappempinObj.getIncidentType());
			}
			if (editappempinObj.getBusValue() != null) {
				tfBusinessValue.setValue(editappempinObj.getBusValue().toString());
			}
			if (editappempinObj.getEmpAgreed() != null) {
				tfempagreed.setValue(editappempinObj.getEmpAgreed().toString());
			}
			if (editappempinObj.getEmpResponse() != null) {
				tfResponse.setValue(editappempinObj.getEmpResponse());
			}
			if (editappempinObj.getIncidentTitle() != null) {
				tfTitle.setValue(editappempinObj.getIncidentTitle());
			}
			if (editappempinObj.getIncidentDes() != null) {
				taDescription.setValue(editappempinObj.getIncidentDes().toString());
			}
			if (editappempinObj.getRevCmnts() != null) {
				taComments.setValue(editappempinObj.getRevCmnts().toString());
			}
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbEmployee.setComponentError(null);
		cbincidentstatus.setComponentError(null);
		cbincidenttype.setComponentError(null);
		cbSeverity.setComponentError(null);
		cbKPIGroup.setComponentError(null);
		tfBusinessValue.setComponentError(null);
		tfempagreed.setComponentError(null);
		tfResponse.setComponentError(null);
		tfTitle.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		if (cbEmployee.getValue() == null) {
			cbEmployee.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			errorFlag = true;
		}
		if (cbKPIGroup.getValue() == null) {
			cbKPIGroup.setComponentError(new UserError(GERPErrorCodes.NULL_KPIGROUPNAME));
			errorFlag = true;
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfBusinessValue.getValue());
			if (achievedQty < 0) {
				tfBusinessValue.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorFlag = true;
			}
		}
		catch (Exception e) {
			tfBusinessValue.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATIONHCM));
			errorFlag = false;
		}
		logger.warn("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Throwing ValidationException. User data is > " + tfBusinessValue.getValue() + ","
				+ tfempagreed.getValue() + "," + cbincidenttype.getValue() + "," + cbEmployee.getValue() + ","
				+ cbKPIGroup.getValue() + "," + cbincidentstatus.getValue() + "," + cbSeverity.getValue() + ","
				+ tfResponse.getValue() + "," + tfTitle.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Saving Data... ");
		AppraisalEmpIncidentDM appempincidentobj = new AppraisalEmpIncidentDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			appempincidentobj = beanAppraisalEmpIncidentDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		appempincidentobj.setEmpid((Long) cbEmployee.getValue());
		appempincidentobj.setRecBy(null);
		appempincidentobj.setReviewedBy(null);
		appempincidentobj.setKpiGrpId((Long) cbKPIGroup.getValue());
		if (cbincidentstatus.getValue() != null) {
			appempincidentobj.setStatus((String) cbincidentstatus.getValue());
		}
		if (cbincidenttype.getValue() != null) {
			appempincidentobj.setIncidentType((String) cbincidenttype.getValue());
		}
		if (cbSeverity.getValue() != null) {
			appempincidentobj.setIncidentSeverity((String) cbSeverity.getValue());
		}
		appempincidentobj.setBusValue(Long.valueOf(tfBusinessValue.getValue()));
		appempincidentobj.setEmpAgreed(tfempagreed.getValue().toString());
		appempincidentobj.setEmpResponse(tfResponse.getValue().toString());
		appempincidentobj.setIncidentTitle(tfTitle.getValue().toString());
		appempincidentobj.setIncidentDes(taDescription.getValue().toString());
		appempincidentobj.setRevCmnts(taComments.getValue().toString());
		appempincidentobj.setLastUpdatedDt(DateUtils.getcurrentdate());
		appempincidentobj.setLastUpdatedBy(loginUserName);
		serviceappempincident.saveAndUpdate(appempincidentobj);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Getting audit record for Appraisal Employee Incident ID ");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_APPRAISAL_EMP_INCIDENT);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(attProcId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Canceling action ");
		cbEmployee.setRequired(false);
		assembleSearchLayout();
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		cbEmployee.setValue(null);
		cbEmployee.setComponentError(null);
		cbincidenttype.setValue(cbincidenttype.getItemIds().iterator().next());
		cbincidenttype.setValue(null);
		cbincidenttype.setComponentError(null);
		cbincidentstatus.setValue(cbincidentstatus.getItemIds().iterator().next());
		cbKPIGroup.setValue(null);
		cbKPIGroup.setComponentError(null);
		cbSeverity.setValue(null);
		cbSeverity.setComponentError(null);
		tfBusinessValue.setValue("");
		tfempagreed.setValue("");
		tfResponse.setValue("");
		tfTitle.setValue("");
		taDescription.setValue("");
		taComments.setValue("");
	}
}
