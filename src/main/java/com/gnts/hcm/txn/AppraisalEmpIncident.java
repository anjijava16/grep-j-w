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
import com.vaadin.data.Item;
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
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	private BeanItemContainer<AppraisalEmpIncidentDM> beanAppraisalEmpIncidentDM = null;
	// User Input Components
	private ComboBox cbempname, cbincidenttype, cbkpigrpname, cbincidentstatus, cbindsrty;
	private TextField tfncidenttitle, tfbuinessvalue, tfempresponse, tfempagreed;
	private TextArea taincidentdesc, tarevcomts;
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
		tfbuinessvalue = new GERPTextField("Business Value");
		tfbuinessvalue.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfbuinessvalue.setComponentError(null);
				if (tfbuinessvalue.getValue() != null) {
					if (!tfbuinessvalue.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
						tfbuinessvalue.setComponentError(new UserError(GERPErrorCodes.NULL_BUSINESSVALUE));
					} else {
						tfbuinessvalue.setComponentError(null);
					}
				}
			}
		});
		// Combo Boxes
		cbempname = new GERPComboBox("Employee Name");
		cbempname.setWidth("200px");
		cbempname.setItemCaptionPropertyId("fullname");
		loadEmployee();
		cbkpigrpname = new GERPComboBox("KPI Group Name");
		cbkpigrpname.setWidth("200px");
		cbkpigrpname.setItemCaptionPropertyId("kpigroupname");
		loadKpiGroupNameList();
		cbincidentstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbincidenttype = new GERPComboBox("Incident Type", BASEConstants.T_HCM_APPRAISAL_EMP_INCIDENT,
				BASEConstants.APP_EMPINCIDENT);
		cbincidenttype.setWidth("200px");
		cbindsrty = new GERPComboBox("Incident Severity");
		cbindsrty.setWidth("200px");
		cbindsrty.setItemCaptionPropertyId("lookupname");
		loadincidentseverity();
		// TextFields
		tfncidenttitle = new GERPTextField("Incident Title");
		tfncidenttitle.setWidth("200");
		tfncidenttitle.setMaxLength(10);
		tfbuinessvalue = new GERPTextField("Business Value");
		tfbuinessvalue.setWidth("200");
		tfbuinessvalue.setMaxLength(10);
		tfempresponse = new GERPTextField("Employee Response");
		tfempresponse.setWidth("200");
		tfempresponse.setMaxLength(10);
		tfempagreed = new GERPTextField("Employee Agreed");
		tfempagreed.setWidth("200");
		tfempagreed.setMaxLength(10);
		// TextAreas
		taincidentdesc = new GERPTextArea("Incident Desc");
		taincidentdesc.setWidth("150");
		taincidentdesc.setHeight("50");
		tarevcomts = new GERPTextArea("Reviewer Comments");
		tarevcomts.setWidth("150");
		tarevcomts.setHeight("50");
		// Create form layouts to hold the input items
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		loadSrchRslt();
	}
	
	// Load Employee List
	private void loadEmployee() {
		List<EmployeeDM> list = servicebeanEmployee.getEmployeeList((String) cbempname.getValue(), null, null, null,
				null, null, null, null, null, "F");
		BeanContainer<Long, EmployeeDM> bean = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		bean.setBeanIdProperty("employeeid");
		bean.addAll(list);
		cbempname.setContainerDataSource(bean);
	}
	
	// Load Load Kpigroup Name List
	private void loadKpiGroupNameList() {
		List<KpiGroupDM> kpigrouplist = serviceKpiGroup.getkpigrouplist(null,null, companyId, null,"Active", "F");
		BeanContainer<Long, KpiGroupDM> beanEmployee = new BeanContainer<Long, KpiGroupDM>(KpiGroupDM.class);
		beanEmployee.setBeanIdProperty("kpigrpid");
		beanEmployee.addAll(kpigrouplist);
		cbkpigrpname.setContainerDataSource(beanEmployee);
	}
	/*private void loadKpiGroupNameList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading KpiGroupNameList");
		List<KpiGroupDM> kpigrouplist = servicegroupkpi.getkpigrouplist(null, null, companyid, null, "Active", "P");
		BeanContainer<Long, KpiGroupDM> beankpigroup = new BeanContainer<Long, KpiGroupDM>(KpiGroupDM.class);
		beankpigroup.setBeanIdProperty("kpigrpid");
		beankpigroup.addAll(kpigrouplist);
		cbkpigpname.setContainerDataSource(beankpigroup);
	}*/
	
	// Load Incident Severity List
	public void loadincidentseverity() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Loading Uom Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyId, null, "Active",
					"HC_INCISEV");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbindsrty.setContainerDataSource(beanCompanyLookUp);
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
		flcol1.addComponent(cbempname);
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
		flcol1.addComponent(cbempname);
		flcol1.addComponent(cbkpigrpname);
		flcol1.addComponent(cbindsrty);
		flcol1.addComponent(tfempagreed);
		flcol2.addComponent(tfncidenttitle);
		flcol2.addComponent(cbincidenttype);
		flcol2.addComponent(tfbuinessvalue);
		flcol2.addComponent(tfempresponse);
		flcol3.addComponent(taincidentdesc);
		flcol3.addComponent(tarevcomts);
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
				+ companyId + ", " + (Long) cbempname.getValue() + ", " + (String) cbincidentstatus.getValue());
		appempincidentList = serviceappempincident.getAppEmpIncidentList(null, (Long) cbempname.getValue(), null, null,
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
		cbempname.setValue(null);
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		cbempname.setRequired(true);
		cbkpigrpname.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		cbempname.setRequired(true);
		cbkpigrpname.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editappempincident();
	}
	
	private void editappempincident() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Editing the selected record");
		Item select = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (select != null) {
			AppraisalEmpIncidentDM editappempinObj = beanAppraisalEmpIncidentDM.getItem(tblMstScrSrchRslt.getValue())
					.getBean();
			if (editappempinObj.getEmpid() != null) {
				cbempname.setValue(editappempinObj.getEmpid());
			}
			if (editappempinObj.getKpiGrpId() != null) {
				cbkpigrpname.setValue(editappempinObj.getKpiGrpId());
			}
			if (editappempinObj.getStatus() != null) {
				cbincidentstatus.setValue(editappempinObj.getStatus());
			}
			if (editappempinObj.getIncidentSeverity() != null) {
				cbindsrty.setValue(editappempinObj.getIncidentSeverity());
			}
			if (editappempinObj.getIncidentType() != null) {
				cbincidenttype.setValue(editappempinObj.getIncidentType());
			}
			if (editappempinObj.getBusValue() != null) {
				tfbuinessvalue.setValue(editappempinObj.getBusValue().toString());
			}
			if (editappempinObj.getEmpAgreed() != null) {
				tfempagreed.setValue(editappempinObj.getEmpAgreed().toString());
			}
			if (editappempinObj.getEmpResponse() != null) {
				tfempresponse.setValue(editappempinObj.getEmpResponse());
			}
			if (editappempinObj.getIncidentTitle() != null) {
				tfncidenttitle.setValue(editappempinObj.getIncidentTitle());
			}
			if (editappempinObj.getIncidentDes() != null) {
				taincidentdesc.setValue(editappempinObj.getIncidentDes().toString());
			}
			if (editappempinObj.getRevCmnts() != null) {
				tarevcomts.setValue(editappempinObj.getRevCmnts().toString());
			}
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbempname.setComponentError(null);
		cbincidentstatus.setComponentError(null);
		cbincidenttype.setComponentError(null);
		cbindsrty.setComponentError(null);
		cbkpigrpname.setComponentError(null);
		tfbuinessvalue.setComponentError(null);
		tfempagreed.setComponentError(null);
		tfempresponse.setComponentError(null);
		tfncidenttitle.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		if (cbempname.getValue() == null) {
			cbempname.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			errorFlag = true;
		}
		if (cbkpigrpname.getValue() == null) {
			cbkpigrpname.setComponentError(new UserError(GERPErrorCodes.NULL_KPIGROUPNAME));
			errorFlag = true;
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfbuinessvalue.getValue());
			if (achievedQty < 0) {
				tfbuinessvalue.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorFlag = true;
			}
		}
		catch (Exception e) {
			tfbuinessvalue.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATIONHCM));
			errorFlag = false;
		}
		// if ((tfbuinessvalue.getValue() == null) || tfbuinessvalue.getValue().trim().length() == 0) {
		// tfbuinessvalue.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
		// errorFlag = true;
		// }
		logger.warn("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Throwing ValidationException. User data is > " + tfbuinessvalue.getValue() + ","
				+ tfempagreed.getValue() + "," + cbincidenttype.getValue() + "," + cbempname.getValue() + ","
				+ cbkpigrpname.getValue() + "," + cbincidentstatus.getValue() + "," + cbindsrty.getValue() + ","
				+ tfempresponse.getValue() + "," + tfncidenttitle.getValue());
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
		appempincidentobj.setEmpid((Long) cbempname.getValue());
		appempincidentobj.setRecBy(null);
		appempincidentobj.setReviewedBy(null);
		appempincidentobj.setKpiGrpId((Long) cbkpigrpname.getValue());
		if (cbincidentstatus.getValue() != null) {
			appempincidentobj.setStatus((String) cbincidentstatus.getValue());
		}
		if (cbincidenttype.getValue() != null) {
			appempincidentobj.setIncidentType((String) cbincidenttype.getValue());
		}
		if (cbindsrty.getValue() != null) {
			appempincidentobj.setIncidentSeverity((String) cbindsrty.getValue());
		}
		// if (tfbuinessvalue.getValue() != null && tfbuinessvalue.getValue().trim().length() > 0) {
		// appempincidentobj.setBusValue(Long.valueOf(tfbuinessvalue.getValue()));
		// }
		appempincidentobj.setBusValue(Long.valueOf(tfbuinessvalue.getValue()));
		appempincidentobj.setEmpAgreed(tfempagreed.getValue().toString());
		appempincidentobj.setEmpResponse(tfempresponse.getValue().toString());
		appempincidentobj.setIncidentTitle(tfncidenttitle.getValue().toString());
		appempincidentobj.setIncidentDes(taincidentdesc.getValue().toString());
		appempincidentobj.setRevCmnts(tarevcomts.getValue().toString());
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
		cbempname.setRequired(false);
		assembleSearchLayout();
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		cbempname.setValue(null);
		cbempname.setComponentError(null);
		cbincidenttype.setValue(cbincidenttype.getItemIds().iterator().next());
		cbincidenttype.setValue(null);
		cbincidenttype.setComponentError(null);
		cbincidentstatus.setValue(cbincidentstatus.getItemIds().iterator().next());
		cbkpigrpname.setValue(null);
		cbkpigrpname.setComponentError(null);
		cbindsrty.setValue(null);
		cbindsrty.setComponentError(null);
		tfbuinessvalue.setValue("");
		tfempagreed.setValue("");
		tfempresponse.setValue("");
		tfncidenttitle.setValue("");
		taincidentdesc.setValue("");
		tarevcomts.setValue("");
	}
}
