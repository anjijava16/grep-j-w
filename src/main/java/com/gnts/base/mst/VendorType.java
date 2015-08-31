/**
 * File Name	:	Vendor Type.java
 * Description	:	this class is used for add/edit Vendor details.
 * Author		:	Ganga
 * Date			:  30-Jul-2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 Version          Date           Modified By             		Remarks

 */
package com.gnts.base.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.VendorTypeDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.VendorTypeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
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

public class VendorType extends BaseUI {
	private VendorTypeService serviceVendorType = (VendorTypeService) SpringContextHelper.getBean("vendorType");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	// form layout for input controls
	private FormLayout flVendorTypeName, flBranchName, flStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfvendortypename;
	private ComboBox cbBranchname, cbStatus;
	// Bean container
	private BeanItemContainer<VendorTypeDM> beanVendortypeDM = null;
	// local variables declaration
	private Long companyId, branchId;
	private String userName, vendoreid;
	private int recordCnt = 0;
	// Initialize Logger
	private Logger logger = Logger.getLogger(VendorType.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public VendorType() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside VendorType() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Painting VendorType UI");
		// Vendor Name text field
		tfvendortypename = new GERPTextField("Vendor Type");
		tfvendortypename.setMaxLength(25);
		// Branch Name Combo Box
		cbBranchname = new GERPComboBox("Branch Name");
		cbBranchname.setItemCaptionPropertyId("branchName");
		cbBranchname.setVisible(false);
		loadBranchList();
		// VendorType Status Combo Box
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// create form layouts to hold the input items
		flVendorTypeName = new FormLayout();
		flBranchName = new FormLayout();
		flStatus = new FormLayout();
		// add the user input items into appropriate form layout
		flVendorTypeName.addComponent(tfvendortypename);
		flBranchName.addComponent(cbBranchname);
		flStatus.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flVendorTypeName);
		hlUserInputLayout.addComponent(flBranchName);
		hlUserInputLayout.addComponent(flStatus);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<VendorTypeDM> list = new ArrayList<VendorTypeDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
					+ tfvendortypename.getValue() + ", " + (String) cbStatus.getValue());
			list = serviceVendorType.getVendorTypeList(tfvendortypename.getValue(), (String) cbStatus.getValue(), null,
					companyId);
			recordCnt = list.size();
			beanVendortypeDM = new BeanItemContainer<VendorTypeDM>(VendorTypeDM.class);
			beanVendortypeDM.addAll(list);
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Got the VendorType result set");
			tblMstScrSrchRslt.setContainerDataSource(beanVendortypeDM);
			tblMstScrSrchRslt.setColumnAlignment("vendorid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No. of Records:" + recordCnt);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "vendorid", "vendortypename", "vendortypestatus",
					"lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Vendor Type ", "Status", "Updated Date",
					"Updated By" });
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editVendorTypeDetails() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Editing the selected record");
			if (tblMstScrSrchRslt.getValue() != null) {
				VendorTypeDM vendorType = beanVendortypeDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				if (vendorType.getVendortypename() != null) {
					tfvendortypename.setValue(vendorType.getVendortypename());
				}
				cbStatus.setValue(vendorType.getVendortypestatus());
				cbBranchname.setValue(vendorType.getBranchid());
				vendoreid = vendorType.getVendorid().toString();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadBranchList() {
		try {
			List<BranchDM> branchList = serviceBranch.getBranchList(null, null, null, "Active", companyId, "P");
			branchList.add(new BranchDM(0L, "All Branches"));
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(branchList);
			cbBranchname.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Base class implementations
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Resetting the UI controls");
		tfvendortypename.setValue("");
		cbBranchname.setValue(0L);
		cbStatus.setValue("");
		tfvendortypename.setComponentError(null);
		cbBranchname.setComponentError(null);
		cbBranchname.setValue(branchId);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
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
		tfvendortypename.setComponentError(null);
		tfvendortypename.setValue("");
		cbBranchname.setComponentError(null);
		cbBranchname.setValue(0L);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the
		// same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfvendortypename.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfvendortypename.setRequired(true);
		editVendorTypeDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		tfvendortypename.setComponentError(null);
		Boolean errorFlag = false;
		if ((tfvendortypename.getValue() == null) || tfvendortypename.getValue().trim().length() == 0) {
			tfvendortypename.setComponentError(new UserError(GERPErrorCodes.NULL_VENDORTYPE_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for VendorType ID " + vendoreid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_VENDOR_TYPE);
		UI.getCurrent().getSession().setAttribute("audittablepk", vendoreid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		tfvendortypename.setRequired(false);
		tfvendortypename.setComponentError(null);
		cbBranchname.setComponentError(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
		VendorTypeDM vendorTypeDM = new VendorTypeDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			vendorTypeDM = beanVendortypeDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		vendorTypeDM.setCompanyid(companyId);
		vendorTypeDM.setVendortypename(tfvendortypename.getValue().toString());
		vendorTypeDM.setBranchid((Long) cbBranchname.getValue());
		if (cbStatus.getValue() != null) {
			vendorTypeDM.setVendortypestatus((String) cbStatus.getValue());
		}
		vendorTypeDM.setLastUpdatedDt(DateUtils.getcurrentdate());
		vendorTypeDM.setLastUpdatedBy(userName);
		serviceVendorType.saveorUpdateVendorTypeDetails(vendorTypeDM);
		resetFields();
		loadSrchRslt();
	}
}
