/**
 * File Name	:	SerialNoGen.java
 * Description	:	this class is used for declare ClientsDAO class methods
 * Author		:	Priyanka
 * Date			:	Mar 07, 2014
 * Modification 
 * Modified By  :   
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of  GNTS Technologies pvt. ltd.
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1          JUN 16 2014        	MOHAMED	        Initial Version
 * 0.2			27-Jun-2014			MOHAMED			Code re-factoring
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.ModuleDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.ModuleService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class SerialNoGen extends BaseUI {
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private ModuleService serviceModule = (ModuleService) SpringContextHelper.getBean("module");
	BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private String username;
	private BeanItemContainer<SlnoGenDM> beanSlnoGen = null;
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private int recordCnt = 0;
	// Declaration for add and edit panel
	private TextField tfReferenceKey, tfKeyDescription, tfPrefixKey, tfPrefixConcat, tfSuffixKey, tfSuffixConcat,
			tfCurrentSeqNo, tfLastSeqNumber;
	private CheckBox ckAutoGeneration;
	private ComboBox cbBranchName, cbModuleName;
	private ComboBox cbSlnoGenLvl = new GERPComboBox("Slno. Gen. Level", BASEConstants.M_BASE_SLNO_GEN,
			BASEConstants.SLNOGEN_LEVEL);
	private Long companyid;
	private String slnoId;
	// intialize the logger
	private Logger logger = Logger.getLogger(SerialNoGen.class);
	private static final long serialVersionUID = 1L;
	
	public SerialNoGen() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside SerialNoGen() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting SerialNoGen UI");
		btnAdd.setVisible(false);
		// NewsDesc Name text field
		tfReferenceKey = new GERPTextField("Reference Key");
		tfReferenceKey.setMaxLength(25);
		tfKeyDescription = new GERPTextField("Key Description");
		tfKeyDescription.setWidth("225");
		tfPrefixKey = new GERPTextField("Prefix Key");
		tfPrefixKey.setMaxLength(10);
		tfPrefixConcat = new GERPTextField("Prefix Concat.");
		tfPrefixConcat.setMaxLength(1);
		tfSuffixKey = new GERPTextField("Suffix Key");
		tfSuffixKey.setMaxLength(10);
		tfSuffixConcat = new GERPTextField("Suffix Concat.");
		tfSuffixConcat.setMaxLength(1);
		tfCurrentSeqNo = new GERPTextField("Current Seq. No. ");
		tfLastSeqNumber = new GERPTextField("Last Seq. No.");
		ckAutoGeneration = new CheckBox("Auto Generation?");
		// populate the branch combo box
		cbBranchName = new GERPComboBox("Branch Name");
		cbBranchName.setWidth("150");
		cbBranchName.setItemCaptionPropertyId("branchName");
		cbBranchName.setNullSelectionAllowed(false);
		loadBranchDetails();
		cbModuleName = new GERPComboBox("Module Name");
		cbModuleName.setWidth("225");
		cbModuleName.setItemCaptionPropertyId("moduleName");
		loadModuleList();
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbModuleName);
		flColumn2.addComponent(tfReferenceKey);
		flColumn3.addComponent(cbSlnoGenLvl);
		flColumn4.addComponent(cbBranchName);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSpacing(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// add the form layouts into user input layout
		hlUserInputLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbModuleName);
		flColumn1.addComponent(tfReferenceKey);
		flColumn1.addComponent(tfKeyDescription);
		flColumn1.addComponent(cbSlnoGenLvl);
		flColumn2.addComponent(cbBranchName);
		flColumn2.addComponent(tfPrefixKey);
		flColumn2.addComponent(tfPrefixConcat);
		flColumn2.addComponent(tfSuffixKey);
		flColumn3.addComponent(tfSuffixConcat);
		flColumn3.addComponent(tfCurrentSeqNo);
		flColumn3.addComponent(tfLastSeqNumber);
		flColumn4.addComponent(ckAutoGeneration);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadModuleList() {
		List<ModuleDM> listt = new ArrayList<ModuleDM>();
		listt.add(new ModuleDM(0L, "All Modules"));
		listt.addAll(serviceModule.getModuleList(companyid));
		BeanContainer<Long, ModuleDM> beanModule = new BeanContainer<Long, ModuleDM>(ModuleDM.class);
		beanModule.setBeanIdProperty("moduleId");
		beanModule.addAll(listt);
		cbModuleName.setContainerDataSource(beanModule);
	}
	
	// Load Branch list for pnlmain's combo Box
	private void loadBranchDetails() {
		List<BranchDM> list = new ArrayList<BranchDM>();
		list.add(new BranchDM(0L, "All Branches"));
		list.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
		BeanContainer<Long, BranchDM> beansbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beansbranch.setBeanIdProperty("branchId");
		beansbranch.addAll(list);
		cbBranchName.setContainerDataSource(beansbranch);
	}
	
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<SlnoGenDM> slnoGList = new ArrayList<SlnoGenDM>();
		Long branchid = null, moduleId = null;
		if (cbBranchName.getValue() != null) {
			branchid = ((Long) cbBranchName.getValue());
		}
		if (cbModuleName.getValue() != null) {
			moduleId = ((Long) cbModuleName.getValue());
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfReferenceKey.getValue() + ", ");
		slnoGList = serviceSlnogen.getSlnoGenerationList(null, companyid, tfReferenceKey.getValue(), branchid,
				moduleId, (String) cbSlnoGenLvl.getValue(),"F");
		recordCnt = slnoGList.size();
		beanSlnoGen = new BeanItemContainer<SlnoGenDM>(SlnoGenDM.class);
		beanSlnoGen.addAll(slnoGList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the orgNews. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanSlnoGen);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "slnoId","moduleName", "refKey", "keyDesc", "slnogenLevel",
				"lastUpdatedDt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id","Module", "Reference Key", "Description", "Level",
				"Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("slnoId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void editSlnoGen() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		btnSave.setEnabled(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		slnoId = sltedRcd.getItemProperty("slnoId").getValue().toString();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected news. Id -> "
				+ slnoId);
		if (sltedRcd != null) {
			SlnoGenDM editSlnoGenlist = beanSlnoGen.getItem(tblMstScrSrchRslt.getValue()).getBean();
			setReadonlyFalse();
			tfReferenceKey.setValue(editSlnoGenlist.getRefKey());
			tfReferenceKey.setReadOnly(true);
			tfKeyDescription.setValue(editSlnoGenlist.getKeyDesc());
			tfKeyDescription.setReadOnly(true);
			tfPrefixKey.setValue(editSlnoGenlist.getPrefixKey());
			tfPrefixKey.setReadOnly(false);
			tfSuffixKey.setValue(editSlnoGenlist.getSuffixKey());
			tfSuffixKey.setReadOnly(false);
			tfPrefixConcat.setValue(editSlnoGenlist.getPrefixCncat());
			tfPrefixConcat.setReadOnly(false);
			tfSuffixConcat.setValue(editSlnoGenlist.getSuffixCncat());
			tfSuffixConcat.setReadOnly(false);
			tfCurrentSeqNo.setValue(editSlnoGenlist.getCurrSeqNo().toString());
			tfLastSeqNumber.setValue(editSlnoGenlist.getLastSeqNo());
			cbSlnoGenLvl.setValue(editSlnoGenlist.getSlnogenLevel());
			cbSlnoGenLvl.setReadOnly(true);
			cbBranchName.setValue(editSlnoGenlist.getBranchId());
			cbBranchName.setReadOnly(true);
			cbModuleName.setValue(editSlnoGenlist.getModuleId());
			cbModuleName.setReadOnly(true);
			if (editSlnoGenlist.getAutoGenYN().equals("Y")) {
				ckAutoGeneration.setValue(true);
			} else if (editSlnoGenlist.getAutoGenYN().equals("N")) {
				ckAutoGeneration.setValue(false);
			}
		}
	}
	
	void setReadonlyFalse() {
		tfReferenceKey.setReadOnly(false);
		tfKeyDescription.setReadOnly(false);
		tfPrefixKey.setReadOnly(false);
		tfSuffixKey.setReadOnly(false);
		tfPrefixConcat.setReadOnly(false);
		tfSuffixConcat.setReadOnly(false);
		tfCurrentSeqNo.setReadOnly(false);
		tfLastSeqNumber.setReadOnly(false);
		cbBranchName.setReadOnly(false);
		cbModuleName.setReadOnly(false);
		cbSlnoGenLvl.setReadOnly(false);
	}
	
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
		tfReferenceKey.setValue("");
		cbSlnoGenLvl.setValue(cbSlnoGenLvl.getItemIds().iterator().next());
		cbBranchName.setValue(0L);
		cbModuleName.setValue(0L);
		hlCmdBtnLayout.setVisible(true);
		vlSrchRsltContainer.setVisible(true);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfReferenceKey.setValue("");
		tfKeyDescription.setValue("");
		tfCurrentSeqNo.setValue("");
		tfLastSeqNumber.setValue("");
		tfPrefixConcat.setValue("");
		tfPrefixKey.setValue("");
		tfSuffixConcat.setValue("");
		tfSuffixKey.setValue("");
		ckAutoGeneration.setValue(false);
		cbSlnoGenLvl.setValue(cbSlnoGenLvl.getItemIds().iterator().next());
		cbBranchName.setValue(null);
		cbModuleName.setValue(null);
		// reset the input controls to default value
		resetFields();
		hlCmdBtnLayout.setVisible(false);
		vlSrchRsltContainer.setVisible(false);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for serial Gen Key. ID " + slnoId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_SLNO_GEN);
		UI.getCurrent().getSession().setAttribute("audittablepk", slnoId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		setReadonlyFalse();
		hlCmdBtnLayout.setVisible(true);
		vlSrchRsltContainer.setVisible(true);
		tfCurrentSeqNo.setRequired(false);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		setReadonlyFalse();
		tfReferenceKey.setValue("");
		tfCurrentSeqNo.setValue("");
		tfKeyDescription.setValue("");
		tfPrefixKey.setValue("");
		tfPrefixConcat.setValue("");
		tfSuffixKey.setValue("");
		tfSuffixConcat.setValue("");
		tfLastSeqNumber.setValue("");
		cbSlnoGenLvl.setValue(cbSlnoGenLvl.getItemIds().iterator().next());
		cbBranchName.setValue(0L);
		cbModuleName.setValue(0L);
		btnSave.setEnabled(false);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editSlnoGen();
		hlCmdBtnLayout.setVisible(true);
		vlSrchRsltContainer.setVisible(true);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfReferenceKey.setComponentError(null);
		if ((tfReferenceKey.getValue() == null) || tfReferenceKey.getValue().trim().length() == 0) {
			tfReferenceKey.setComponentError(new UserError(GERPErrorCodes.NULL_SLNO_GEN));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfReferenceKey.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		SlnoGenDM Slnoobj = new SlnoGenDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			Slnoobj = beanSlnoGen.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		Slnoobj.setCompanyId(companyid);
		Slnoobj.setBranchId((Long) cbBranchName.getValue());
		Slnoobj.setModuleId((Long) cbModuleName.getValue());
		Slnoobj.setSlnogenLevel((String) cbSlnoGenLvl.getValue());
		Slnoobj.setRefKey(tfReferenceKey.getValue());
		Slnoobj.setKeyDesc(tfKeyDescription.getValue());
		Slnoobj.setPrefixKey(tfPrefixKey.getValue());
		Slnoobj.setPrefixCncat(tfPrefixConcat.getValue());
		Slnoobj.setCurrSeqNo(Long.valueOf(tfCurrentSeqNo.getValue()));
		Slnoobj.setSuffixKey(tfSuffixKey.getValue());
		Slnoobj.setSuffixCncat(tfSuffixConcat.getValue());
		Slnoobj.setLastSeqNo(tfLastSeqNumber.getValue());
		Slnoobj.setLastUpdatedDt(DateUtils.getcurrentdate());
		Slnoobj.setLastupdatedby(username);
		if (ckAutoGeneration.getValue().equals(true)) {
			Slnoobj.setAutoGenYN("Y");
		} else if (ckAutoGeneration.getValue().equals(false)) {
			Slnoobj.setAutoGenYN("N");
		}
		serviceSlnogen.saveorupadateSlnoGeneration(Slnoobj);
		resetFields();
		loadSrchRslt();
	}
}