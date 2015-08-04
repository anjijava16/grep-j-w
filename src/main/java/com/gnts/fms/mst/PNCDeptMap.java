/**
 * File Name 		: PNCDeptMap.java 
 * Description 		: this class is used for add/edit PNC Department Map details. 
 * Author 			: SOUNDAR C 
 * Date 			: Mar 15, 2014
 * Modification 	:
 * Modified By 		: SOUNDAR C 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 15 2014        SOUNDAR C		          Intial Version
 * 0.2			 17-Jul-2014		Abdullah.H				  Code Optimization
 */
package com.gnts.fms.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
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
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;

public class PNCDeptMap extends BaseUI {
	private static final long serialVersionUID = 1L;
	/*
	 * To Declare Service
	 */
	private PNCDeptMapService servicePNCDeptMap = (PNCDeptMapService) SpringContextHelper.getBean("pncdeptmap");
	private DepartmentService serviceDepartment = (DepartmentService) SpringContextHelper.getBean("department");
	private PNCCentersService servicepnccnter = (PNCCentersService) SpringContextHelper.getBean("pnccenter");
	// form layout for input controls
	private FormLayout formLayout1, formLayout2, formLayout3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add Input fields
	private ComboBox cbPNCCenters = new GERPComboBox("PNC Center");
	private ComboBox cbDepartmentName = new GERPComboBox("Department Name");
	private ComboBox cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private String loginUserName;
	private Long companyId;
	private int recordCnt = 0;
	private String pncDeptMap;
	private BeanItemContainer<PNCDeptMapDM> beanPNCDeptMapDM = null;
	private Logger logger = Logger.getLogger(PNCDeptMap.class);
	
	// Constructor
	public PNCDeptMap() {
		// Get the logged in user name and company id from the session
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		// Loading the UI
		buildView();
	}
	
	/*
	 * buildMainview()-->for screen UI design
	 * @param clArgumentLayout hlHeaderLayout
	 */
	private void buildView() {
		/*
		 * For Load Active PNC Center based on Company
		 */
		cbPNCCenters.setNullSelectionAllowed(false);
		cbPNCCenters.setItemCaptionPropertyId("pnccode");
		loadPNCCenterList();
		/*
		 * For Load Active Currency Details based on Company
		 */
		// cbDepartmentName.setInputPrompt(Common.SELECT_PROMPT);
		cbDepartmentName.setNullSelectionAllowed(true);
		cbDepartmentName.setItemCaptionPropertyId("deptname");
		loadDepartmentList();
		// create form layouts to hold the input items
		formLayout1 = new FormLayout();
		formLayout2 = new FormLayout();
		formLayout3 = new FormLayout();
		// Add the user input items into appropriate form layout
		formLayout1.addComponent(cbPNCCenters);
		formLayout2.addComponent(cbDepartmentName);
		formLayout3.addComponent(cbstatus);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		// Add components for Search Layout
		// hlSearchLayout.addComponent(formLayout1);
		hlSearchLayout.addComponent(formLayout2);
		hlSearchLayout.addComponent(formLayout3);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		hlUserInputLayout.addComponent(formLayout1);
		hlUserInputLayout.addComponent(formLayout2);
		hlUserInputLayout.addComponent(formLayout3);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Loading Search...");
			List<PNCDeptMapDM> pncDeptList = new ArrayList<PNCDeptMapDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Search Parameters are " + cbPNCCenters.getValue() + ", " + cbDepartmentName.getValue() + ","
					+ companyId + " , " + (String) cbstatus.getValue());
			pncDeptList = servicePNCDeptMap.getDeptMapList(null, (Long) cbDepartmentName.getValue(), companyId,
					(String) cbstatus.getValue());
			recordCnt = pncDeptList.size();
			beanPNCDeptMapDM = new BeanItemContainer<PNCDeptMapDM>(PNCDeptMapDM.class);
			beanPNCDeptMapDM.addAll(pncDeptList);
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Got the PNCCenter. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanPNCDeptMapDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "pncmapid", "departmentname", "status", "lastupdateddt",
					"lastupdatedby", });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Department Name", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("pncmapid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editPNCDeptMap() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			PNCDeptMapDM pncDeptMapDM = beanPNCDeptMapDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			pncDeptMap = pncDeptMapDM.getPncmapid().toString();
			if (pncDeptMapDM.getPncid() != null) {
				cbPNCCenters.setValue(pncDeptMapDM.getPncid());
			}
			if (pncDeptMapDM.getDeptid() != null) {
				cbDepartmentName.setValue(pncDeptMapDM.getDeptid());
			}
			cbstatus.setValue(pncDeptMapDM.getStatus());
		}
	}
	
	/*
	 * For Load Active Account Type Details based on Company
	 */
	private void loadDepartmentList() {
		BeanContainer<Long, DepartmentDM> bean = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		bean.setBeanIdProperty("deptid");
		bean.addAll(serviceDepartment.getDepartmentList(companyId, null, "Active", "P"));
		cbDepartmentName.setContainerDataSource(bean);
	}
	
	/*
	 * For Load Active Account Type Details based on Company
	 */
	private void loadPNCCenterList() {
		BeanContainer<Long, PNCCentersDM> bean = new BeanContainer<Long, PNCCentersDM>(PNCCentersDM.class);
		bean.setBeanIdProperty("pncid");
		bean.addAll(servicepnccnter.getCenterTypeList(null, companyId, (String) cbstatus.getValue(), "T"));
		cbPNCCenters.setContainerDataSource(bean);
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
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ " Invoking Reset search Detail");
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		assembleUserInputLayout();
		// Add input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbPNCCenters.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbPNCCenters.setRequired(true);
		editPNCDeptMap();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		cbPNCCenters.setComponentError(null);
		if (cbPNCCenters.getValue() == null) {
			cbPNCCenters.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_PNC_DEPT_MAP));
			logger.warn("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Throwing ValidationException. User data is > " + cbPNCCenters.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Saving Data... ");
		PNCDeptMapDM pncDeptObj = new PNCDeptMapDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			pncDeptObj = beanPNCDeptMapDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		pncDeptObj.setCompanyid(companyId);
		pncDeptObj.setPncid(Long.valueOf(cbPNCCenters.getValue().toString()));
		pncDeptObj.setDeptid(Long.valueOf(cbDepartmentName.getValue().toString()));
		if (cbstatus.getValue() != null) {
			pncDeptObj.setStatus((String) cbstatus.getValue());
		}
		pncDeptObj.setLastupdateddt(DateUtils.getcurrentdate());
		pncDeptObj.setLastupdatedby(loginUserName);
		servicePNCDeptMap.saveDetails(pncDeptObj);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Getting audit record for PNC Dept. ID " + pncDeptMap);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_FMS_PNC_CENTERS);
		UI.getCurrent().getSession().setAttribute("audittablepk", pncDeptMap);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Canceling action ");
		assembleSearchLayout();
		cbPNCCenters.setRequired(false);
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		cbPNCCenters.setValue(null);
		cbDepartmentName.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
	}
}
