/**
 * File Name	:	ModuleControl.java
 * Description	:	entity class for M_BASE_MODULE_CTRL table
 * Author		:	Hohulnath.V
 * Date			:	Feb 19, 2014
 * Modification 
 * Modified By  :   Hohulnath.V
 * Description	:   ORM Class and Entity class for m_base_module_ctrl table
 *
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of  GNTS Technologies pvt. ltd.
 * Version       Date           	Modified By               	Remarks
 * 0.2          27-Jun-2014        	Abdullah	        		Code optimization
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ModuleControlDM;
import com.gnts.base.domain.mst.ModuleDM;
import com.gnts.base.service.mst.ModuleControlService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ModuleControl extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ModuleControlService servicemodulectrl = (ModuleControlService) SpringContextHelper
			.getBean("modulecontrol");
	// form layout for input controls
	private FormLayout flModulecode, flStatus, flLisenced, fldfLicenseendDt, fldfLicensestartDt;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User layout Components
	private TextField tfModuleCode, tfStatus, tflisence;
	private ComboBox cbStatus, cbModuleCode;
	private PopupDateField dfLicenseendDt, dfLicensestartDt;
	private BeanItemContainer<ModuleControlDM> beansModuleControlDM = null;
	// local variables declaration
	private String userName;
	private int recordCnt = 0;
	private Long companyId;
	// Initialize logger
	private Logger logger = Logger.getLogger(ModuleControl.class);
	
	// Constructor
	public ModuleControl() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		// Loading the UI
		buildView();
	}
	
	// Build the UI components
	private void buildView() {
		logger.info("Company ID :" + companyId + "| Login User Name : " + userName + " > "
				+ "Painting Module Control UI");
		// Module Control ComboBoxes
		cbModuleCode = new GERPComboBox("Module Name");
		cbModuleCode.setWidth("225");
		cbModuleCode.setItemCaptionPropertyId("moduleName");
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Module Control TextBoxes
		tfModuleCode = new GERPTextField("Module Name");
		tfModuleCode.setWidth("200");
		tfStatus = new GERPTextField("Status");
		tfStatus.setWidth("75");
		tflisence = new GERPTextField("Lisenced?");
		tflisence.setWidth("75");
		// create form layouts to hold the input items
		flModulecode = new GERPFormLayout();
		loadSearchModulelist();
		flStatus = new GERPFormLayout();
		flLisenced = new GERPFormLayout();
		fldfLicenseendDt = new GERPFormLayout();
		fldfLicensestartDt = new GERPFormLayout();
		// populate the status Pop Up Date Field
		dfLicenseendDt = new GERPPopupDateField("License End Date");
		dfLicensestartDt = new GERPPopupDateField("License Start Date");
		// Disable Button
		btnAdd.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnDownload.setVisible(false);
		btnSave.setVisible(false);
		// add the user input items into appropriate form layout
		flModulecode.addComponent(cbModuleCode);
		flStatus.addComponent(cbStatus);
		fldfLicenseendDt.addComponent(dfLicenseendDt);
		fldfLicensestartDt.addComponent(dfLicensestartDt);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		// Add Logger
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// Set the View button
		btnEdit.setCaption("View");
		btnEdit.setStyleName("view");
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flModulecode = new GERPFormLayout();
		flStatus = new GERPFormLayout();
		flModulecode.addComponent(cbModuleCode);
		flStatus.addComponent(cbStatus);
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(flModulecode);
		hlSearchLayout.addComponent(flStatus);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling User Input layout");
		flModulecode = new GERPFormLayout();
		flStatus = new GERPFormLayout();
		flLisenced = new GERPFormLayout();
		fldfLicensestartDt = new GERPFormLayout();
		fldfLicenseendDt = new GERPFormLayout();
		flModulecode.addComponent(tfModuleCode);
		flStatus.addComponent(tfStatus);
		flLisenced.addComponent(tflisence);
		fldfLicensestartDt.addComponent(dfLicensestartDt);
		fldfLicenseendDt.addComponent(dfLicenseendDt);
		hlUserInputLayout.addComponent(flModulecode);
		hlUserInputLayout.addComponent(flLisenced);
		hlUserInputLayout.addComponent(fldfLicensestartDt);
		hlUserInputLayout.addComponent(fldfLicenseendDt);
		hlUserInputLayout.addComponent(flStatus);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	private void loadSearchModulelist() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Load search mo");
		// Load the Active status screen only
		List<ModuleDM> moduleList = new ArrayList<ModuleDM>();
		moduleList.add(new ModuleDM(0L, "All Modules"));
		moduleList.addAll(servicemodulectrl.getModuleList());
		BeanContainer<Long, ModuleDM> moduleControlBean = new BeanContainer<Long, ModuleDM>(ModuleDM.class);
		moduleControlBean.setBeanIdProperty("moduleId");
		moduleControlBean.addAll(moduleList);
		cbModuleCode.setContainerDataSource(moduleControlBean);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		Long modulecodeid = null;
		List<ModuleControlDM> modulecontrolList = new ArrayList<ModuleControlDM>();
		if (cbModuleCode.getValue() != null) {
			modulecodeid = ((Long) cbModuleCode.getValue());
		}
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ modulecodeid + ", " + (String) cbStatus.getValue() + ", " + companyId);
		modulecontrolList = servicemodulectrl.getModuleControlList(modulecodeid, (String) cbStatus.getValue(),
				companyId);
		recordCnt = modulecontrolList.size();
		beansModuleControlDM = new BeanItemContainer<ModuleControlDM>(ModuleControlDM.class);
		beansModuleControlDM.addAll(modulecontrolList);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Got the Module control result set");
		tblMstScrSrchRslt.setContainerDataSource(beansModuleControlDM);
		tblMstScrSrchRslt.setColumnAlignment("modulecontrolid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No. of Records:" + recordCnt);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "modulecontrolid", "moduleName", "licensedyn", "ctrlstatus",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Module", "Licensed", "Status", "Updated Date",
				"Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("modulecontrolid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void viewModuleControl() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			ModuleControlDM moduleCntrl = beansModuleControlDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			if (itselect.getItemProperty("ctrlstatus").getValue().toString() != null
					&& !"null".equals(itselect.getItemProperty("ctrlstatus").getValue().toString())) {
				tfStatus.setReadOnly(false);
				tfStatus.setValue(moduleCntrl.getCtrlstatus());
				tfStatus.setReadOnly(true);
			}
			if (itselect.getItemProperty("licensedyn").getValue().toString() != null
					&& !"null".equals(itselect.getItemProperty("licensedyn").getValue().toString())) {
				tflisence.setReadOnly(false);
				tflisence.setValue(moduleCntrl.getLicensedyn());
				tflisence.setReadOnly(true);
			}
			if (itselect.getItemProperty("moduleCode").getValue().toString() != null
					&& !"null".equals(itselect.getItemProperty("moduleCode").getValue().toString())) {
				tfModuleCode.setReadOnly(false);
				tfModuleCode.setValue(moduleCntrl.getModuleName());
				tfModuleCode.setReadOnly(true);
			}
			if (itselect.getItemProperty("licensestartdt").getValue().toString() != null
					&& !"null".equals(itselect.getItemProperty("licensestartdt").getValue().toString())) {
				dfLicensestartDt.setReadOnly(false);
				dfLicensestartDt.setValue((Date) itselect.getItemProperty("licensestartdt").getValue());
				dfLicensestartDt.setReadOnly(true);
			}
			if (itselect.getItemProperty("licenseenddt").getValue().toString() != null
					&& !"null".equals(itselect.getItemProperty("licenseenddt").getValue().toString())) {
				dfLicenseendDt.setReadOnly(false);
				dfLicenseendDt.setValue((Date) itselect.getItemProperty("licenseenddt").getValue());
				dfLicenseendDt.setReadOnly(true);
			}
		}
	}
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbModuleCode.setReadOnly(false);
		cbModuleCode.setValue(0L);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		// No functionality is Here..!
	}
	@Override
	protected void editDetails() {
		btnSave.setVisible(false);
		dfLicensestartDt.setVisible(true);
		dfLicenseendDt.setVisible(true);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		viewModuleControl();
	}
	@Override
	protected void validateDetails() throws ValidationException {
		// No functionality is here...!
	}
	@Override
	protected void saveDetails() throws SaveException {
		// No functionality is here...!
	}
	
	@Override
	protected void showAuditDetails() {
		// No functionality is here...!
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Resetting UI controls ");
		cbModuleCode.setReadOnly(false);
		cbModuleCode.setValue(0L);
		cbStatus.setReadOnly(false);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfModuleCode.setReadOnly(false);
		tfModuleCode.setValue("");
		tfModuleCode.setReadOnly(true);
		tfStatus.setReadOnly(false);
		tfStatus.setValue("");
		tfStatus.setReadOnly(true);
		tflisence.setReadOnly(false);
		tflisence.setValue("");
		tflisence.setReadOnly(true);
		dfLicensestartDt.setReadOnly(false);
		dfLicensestartDt.setValue(null);
		dfLicensestartDt.setReadOnly(true);
		dfLicenseendDt.setReadOnly(false);
		dfLicenseendDt.setValue(null);
		dfLicenseendDt.setReadOnly(true);
	}
}
