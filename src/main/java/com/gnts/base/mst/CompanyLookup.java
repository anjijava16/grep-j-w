/**
 * File Name	:	CompanyLookup.java
 * Description	:	To Handle CompanyLookup details .
 * Author		:	HokulNath
 * Date			:	March 06, 2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1			06-Mar-2014		  HokulNath			initial version
 * 0.2          20-Jun-2014		  Nandhakumar.S		Code re-fragment and adding logging
 * 
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.LookupDM;
import com.gnts.base.domain.mst.ModuleDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.LookupService;
import com.gnts.base.service.mst.ModuleService;
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
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class CompanyLookup extends BaseUI {
	private ModuleService servModuleBean = (ModuleService) SpringContextHelper.getBean("module");
	private LookupService serviceLookUp = (LookupService) SpringContextHelper.getBean("lookup");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// form layout for input controls
	private FormLayout flLookupName, flcbLookupCodeCode, flcbModuleCodeCode, flcbStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tflookupname;
	private ComboBox cbLookupCode;
	private ComboBox cbModuleCode;
	private ComboBox cbstatus;
	// Adding required domain classes into BeanItemContainer
	private BeanItemContainer<CompanyLookupDM> beansCompanyLookup = null;
	// local variables declaration
	private Long companyid;;
	private int recordCnt = 0;
	private String username;
	private String companyLookUpId;
	// Initialize logger
	private Logger logger = Logger.getLogger(Department.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public CompanyLookup() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside CompanyLookup() constructor");
		// Loading the CompanyLookup UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting CompanyLookup UI");
		// CompanyLookup Name text field
		tflookupname = new GERPTextField("Lookup Value");
		tflookupname.setMaxLength(100);
		// CompanyLookup status combo box
		cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// LookupCode combo box of CompanyLookup
		cbLookupCode = new GERPComboBox("Lookup Ref.");
		cbLookupCode.setItemCaptionPropertyId("lookupdesc");
		cbLookupCode.setWidth("190");
		// ModuleCode combo box of CompanyLookup
		cbModuleCode = new GERPComboBox("Module");
		cbModuleCode.setItemCaptionPropertyId("moduleName");
		cbModuleCode.setWidth("200");
		loadmodulelist();
		cbModuleCode.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadLookupList();
				}
			}
		});
		// loading ModuleCode list into cbModuleCode
		// build search layout
		flLookupName = new FormLayout();
		flcbLookupCodeCode = new FormLayout();
		flcbModuleCodeCode = new FormLayout();
		flcbStatus = new FormLayout();
		flcbModuleCodeCode.addComponent(cbModuleCode);
		flcbLookupCodeCode.addComponent(cbLookupCode);
		flLookupName.addComponent(tflookupname);
		flcbStatus.addComponent(cbstatus);
		hlUserInputLayout.addComponent(flcbModuleCodeCode);
		hlUserInputLayout.addComponent(flcbLookupCodeCode);
		hlUserInputLayout.addComponent(flLookupName);
		hlUserInputLayout.addComponent(flcbStatus);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadLookupListAll();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for CompanyLookUp UI search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		Long lookupcode = null;
		Long moduleId = null;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<CompanyLookupDM> lookuplist = new ArrayList<CompanyLookupDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tflookupname.getValue() + "," + lookupcode + "," + ((String) cbstatus.getValue()));
		if (cbLookupCode.getValue() != null) {
			lookupcode = ((Long) cbLookupCode.getValue());
		}
		if (cbModuleCode.getValue() != null) {
			moduleId = ((Long) cbModuleCode.getValue());
		}
		lookuplist = serviceCompanyLookup.getCompanyLookupList(companyid, tflookupname.getValue(), lookupcode,
				((String) cbstatus.getValue()), moduleId, "F");
		recordCnt = lookuplist.size();
		beansCompanyLookup = new BeanItemContainer<CompanyLookupDM>(CompanyLookupDM.class);
		beansCompanyLookup.addAll(lookuplist);
		tblMstScrSrchRslt.setContainerDataSource(beansCompanyLookup);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "cmplookupid", "moduleName", "lookupDesc", "lookupname",
				"lookupstatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Code", "Lookup Ref. ", "Value", "Status",
				"Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("cmplookupid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		tblMstScrSrchRslt.setSelectable(true);
	}
	
	// load the ModuleDeatils List details for form
	private void loadmodulelist() {
		List<ModuleDM> moduleList = new ArrayList<ModuleDM>();
		moduleList.add(new ModuleDM(0L, "All Modules"));
		moduleList.addAll(servModuleBean.getModuleList(companyid));
		BeanContainer<Long, ModuleDM> modulebean = new BeanContainer<Long, ModuleDM>(ModuleDM.class);
		modulebean.setBeanIdProperty("moduleId");
		modulebean.addAll(moduleList);
		cbModuleCode.setContainerDataSource(modulebean);
	}
	
	// load the lookupDetails List details for form without
	private void loadLookupList() {
		List<LookupDM> lookupList = new ArrayList<LookupDM>();
		loadLookupBean(lookupList);
	}
	
	private void loadLookupListAll() {
		List<LookupDM> lookupList = new ArrayList<LookupDM>();
		lookupList.add(new LookupDM(0L, "ALL", "All Lookup"));
		loadLookupBean(lookupList);
	}
	
	private void loadLookupBean(List<LookupDM> lookupList) {
		lookupList.addAll(serviceLookUp.getLookupList(((Long) cbModuleCode.getValue()), null, null, "Active", "D"));
		BeanContainer<Long, LookupDM> lookupbean = new BeanContainer<Long, LookupDM>(LookupDM.class);
		lookupbean.setBeanIdProperty("lookupid");
		lookupbean.addAll(lookupList);
		cbLookupCode.setContainerDataSource(lookupbean);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		loadLookupListAll();
		cbModuleCode.setValue(0L);
		cbLookupCode.setValue(0L);
		tflookupname.setValue("");
		tflookupname.setComponentError(null);
		cbLookupCode.setComponentError(null);
		cbModuleCode.setComponentError(null);
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editCompanyLookUp() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Selected CompanyLookup. Id -> ");
		if (tblMstScrSrchRslt.getValue() != null) {
			CompanyLookupDM companyLookupDM = beansCompanyLookup.getItem(tblMstScrSrchRslt.getValue()).getBean();
			companyLookUpId = companyLookupDM.getCmplookupid().toString();
			tflookupname.setValue(companyLookupDM.getLookupname());
			cbModuleCode.setValue((Long) companyLookupDM.getModuleid());
			cbLookupCode.setValue((Long) companyLookupDM.getLookupid());
			cbstatus.setValue(companyLookupDM.getLookupstatus());
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
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tflookupname.setValue("");
		cbModuleCode.setValue(0L);
		cbLookupCode.setValue(0L);
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbModuleCode.removeItem(0L);
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tflookupname.setRequired(true);
		cbLookupCode.setRequired(true);
		cbModuleCode.setRequired(true);
		// reset the input controls to default value
		resetFields();
		cbLookupCode.removeItem(0L);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for CompanyLookUp. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_COMPANY_LOOKUP);
		UI.getCurrent().getSession().setAttribute("audittablepk", companyLookUpId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		loadmodulelist();
		tflookupname.setRequired(false);
		tflookupname.setComponentError(null);
		cbLookupCode.setComponentError(null);
		cbModuleCode.setComponentError(null);
		cbLookupCode.setRequired(false);
		cbModuleCode.setRequired(false);
		resetFields();
		loadLookupListAll();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tflookupname.setRequired(false);
		cbModuleCode.removeItem(0L);
		cbLookupCode.removeItem(0L);
		editCompanyLookUp();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tflookupname.setComponentError(null);
		cbModuleCode.setComponentError(null);
		cbLookupCode.setComponentError(null);
		Boolean errorFlag = false;
		if ((tflookupname.getValue() == null) || tflookupname.getValue().trim().length() == 0) {
			tflookupname.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_LOOKUP));
			errorFlag = true;
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tflookupname.getValue());
		}
		if (cbModuleCode.getValue() == null) {
			cbModuleCode.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_LOOKUP_MODULE_CODE));
			errorFlag = true;
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbModuleCode.getValue());
		}
		if (cbLookupCode.getValue() == null) {
			cbLookupCode.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_LOOKUP_LOOKUP_CODE));
			errorFlag = true;
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbModuleCode.getValue());
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			CompanyLookupDM lookupobj = new CompanyLookupDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				lookupobj = beansCompanyLookup.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			lookupobj.setLookupname(tflookupname.getValue().toString());
			lookupobj.setCompanyid(companyid);
			if (cbLookupCode.getValue() != null) {
				lookupobj.setLookupid((Long) cbLookupCode.getValue());
			}
			if (cbModuleCode.getValue() != null) {
				lookupobj.setModuleid((Long) cbModuleCode.getValue());
			}
			if (cbstatus.getValue() != null) {
				lookupobj.setLookupstatus((String) cbstatus.getValue());
			}
			lookupobj.setLastupdateddt(DateUtils.getcurrentdate());
			lookupobj.setLastupdatedby(username);
			serviceCompanyLookup.saveorUpdateCompanyLookupDetails(lookupobj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
