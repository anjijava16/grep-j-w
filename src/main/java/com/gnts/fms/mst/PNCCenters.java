/**
 * File Name 		: PNCCenters.java 
 * Description 		: this class is used for add/edit PNC Center details. 
 * Author 			: SOUNDAR C 
 * Date 			: Feb 25, 2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          Mar 03 2014        	MADHU T			        Initial Version
 * 0.2			JUNE 20,2014		MADHU T					Code re-factoring
 * 0.2			16-Jul-2014			Abdullah.H				code re-factoring
 */
package com.gnts.fms.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.service.mst.DepartmentService;
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
import com.gnts.fms.domain.mst.PNCCentersDM;
import com.gnts.fms.domain.mst.PNCDeptMapDM;
import com.gnts.fms.service.mst.PNCCentersService;
import com.gnts.fms.service.mst.PNCDeptMapService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class PNCCenters extends BaseUI {
	private PNCDeptMapService servicePNCDeptMap = (PNCDeptMapService) SpringContextHelper.getBean("pncdeptmap");
	private PNCCentersService servicePNCCenter = (PNCCentersService) SpringContextHelper.getBean("pnccenter");
	private DepartmentService serviceDepartment = (DepartmentService) SpringContextHelper.getBean("department");
	// Form layout for input controls
	private FormLayout flPNCName, flStatus, flPNCDesc, flDeptName;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfPNCCode, tfPNCDesc;
	private ComboBox cbPNCStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	// BeanContainer
	private BeanItemContainer<PNCCentersDM> beanPNCCenter = null;
	private ListSelect lSDeptName = new ListSelect("Department Name");
	// Local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	private Long pkPNCId;
	private Long companyId;
	// Initialize logger
	private Logger logger = Logger.getLogger(PNCCenters.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public PNCCenters() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PNCCenters() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting PNCCenters UI");
		// PNCCenter Name Text Field
		tfPNCCode = new GERPTextField("PNC Code");
		tfPNCCode.setMaxLength(25);
		// PNCCenter Description Text Field
		tfPNCDesc = new GERPTextField("PNC Description");
		tfPNCDesc.setMaxLength(25);
		lSDeptName.setItemCaptionPropertyId("deptname");
		lSDeptName.setWidth("200");
		lSDeptName.setHeight("110");
		lSDeptName.setMultiSelect(true);
		loadDepartmentList();
		// create form layouts to hold the input items
		flPNCName = new FormLayout();
		flPNCDesc = new FormLayout();
		flStatus = new FormLayout();
		flDeptName = new FormLayout();
		// Add the user input items into appropriate form layout
		flPNCName.addComponent(tfPNCCode);
		flPNCDesc.addComponent(tfPNCDesc);
		flStatus.addComponent(cbPNCStatus);
		flDeptName.addComponent(lSDeptName);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		// Add components for Search Layout
		hlSearchLayout.addComponent(flPNCName);
		hlSearchLayout.addComponent(flStatus);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		hlUserInputLayout.addComponent(flPNCName);
		hlUserInputLayout.addComponent(flPNCDesc);
		hlUserInputLayout.addComponent(flStatus);
		hlUserInputLayout.addComponent(flDeptName);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<PNCCentersDM> pncCenterList = new ArrayList<PNCCentersDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfPNCCode.getValue() + ", " + (String) cbPNCStatus.getValue());
		pncCenterList = servicePNCCenter.getCenterTypeList(tfPNCCode.getValue(), companyId,
				(String) cbPNCStatus.getValue(), "F");
		recordCnt = pncCenterList.size();
		beanPNCCenter = new BeanItemContainer<PNCCentersDM>(PNCCentersDM.class);
		beanPNCCenter.addAll(pncCenterList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the PNCCenter. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanPNCCenter);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "pncid", "pnccode", "status", "lastupdateddt",
				"lastupdatedby", });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "PNC Code", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("pncid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfPNCCode.setValue("");
		tfPNCCode.setComponentError(null);
		tfPNCDesc.setValue("");
		cbPNCStatus.setValue(cbPNCStatus.getItemIds().iterator().next());
		lSDeptName.setValue(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editPNCCenter() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			PNCCentersDM pncCentersDM = beanPNCCenter.getItem(tblMstScrSrchRslt.getValue()).getBean();
			pkPNCId = pncCentersDM.getPncid();
			tfPNCCode.setValue(pncCentersDM.getPnccode());
			if (pncCentersDM.getPncdesc() != null) {
				tfPNCDesc.setValue(pncCentersDM.getPncdesc());
			}
			cbPNCStatus.setValue(pncCentersDM.getStatus());
		}
		lSDeptName.setValue(null);
		List<PNCDeptMapDM> listPncDept = servicePNCDeptMap.getDeptMapList(pkPNCId, null, companyid, "Active");
		for (PNCDeptMapDM accOwner : listPncDept) {
			lSDeptName.select(accOwner.getDeptid());
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
		cbPNCStatus.setValue(cbPNCStatus.getItemIds().iterator().next());
		tfPNCCode.setValue("");
		tfPNCDesc.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		lSDeptName.setValue(null);
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		assembleUserInputLayout();
		tfPNCCode.setVisible(true);
		// Add input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfPNCCode.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for PNC Center. ID " + pkPNCId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_FMS_PNC_CENTERS);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(pkPNCId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfPNCCode.setRequired(false);
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfPNCCode.setRequired(true);
		editPNCCenter();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfPNCCode.setComponentError(null);
		if ((tfPNCCode.getValue() == null) || tfPNCCode.getValue().trim().length() == 0) {
			tfPNCCode.setComponentError(new UserError(GERPErrorCodes.NULL_PNC_CODE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfPNCCode.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		PNCCentersDM pncCentersDM = new PNCCentersDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			pncCentersDM = beanPNCCenter.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		pncCentersDM.setCompanyid(companyid);
		pncCentersDM.setPnccode(tfPNCCode.getValue().toString());
		pncCentersDM.setPncdesc(tfPNCDesc.getValue().toString());
		if (cbPNCStatus.getValue() != null) {
			pncCentersDM.setStatus((String) cbPNCStatus.getValue());
		}
		pncCentersDM.setLastupdateddt(DateUtils.getcurrentdate());
		pncCentersDM.setLastupdatedby(username);
		servicePNCCenter.saveDetails(pncCentersDM);
		String[] split = lSDeptName.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
		servicePNCDeptMap.deletePncDeptmap(pkPNCId);
		for (String obj : split) {
			if (obj.trim().length() > 0) {
				PNCDeptMapDM pncDeptMapList = new PNCDeptMapDM();
				pncDeptMapList.setPncid(pncCentersDM.getPncid());
				pncDeptMapList.setCompanyid(companyid);
				pncDeptMapList.setDeptid(Long.valueOf(obj.trim()));
				pncDeptMapList.setStatus("Active");
				pncDeptMapList.setLastupdatedby(username);
				pncDeptMapList.setLastupdateddt(DateUtils.getcurrentdate());
				servicePNCDeptMap.saveDetails(pncDeptMapList);
			}
		}
		resetFields();
		loadSrchRslt();
	}
	
	/*
	 * For Load Active Account Type Details based on Company
	 */
	private void loadDepartmentList() {
		BeanContainer<Long, DepartmentDM> bean = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		bean.setBeanIdProperty("deptid");
		bean.addAll(serviceDepartment.getDepartmentList(companyId, null, "Active", "P"));
		lSDeptName.setContainerDataSource(bean);
	}
}
