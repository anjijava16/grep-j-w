/**
 * File Name 		: 	MaterialVendorGroup.java 
 * Description 		:	This class is used for add/edit MaterialVendorGroup details. 
 * Author 			: 	SUNDAR 
 * Date 			: 	17-October-2014	
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * Version      	 Date           	Modified By            Remarks
 * 0.1       	17-October-2014			SUNDAR              Initial Version
 * 
 */
package com.gnts.mms.txn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.service.mst.VendorService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.MaterialVendorGrpDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.MaterialVendorGrpService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;

public class MaterialVendorGroup extends BaseUI {
	// Bean Creation
	private MaterialVendorGrpService servicegrpmaterialvendor = (MaterialVendorGrpService) SpringContextHelper
			.getBean("materialvendorgrp");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	// Form layout for input controls
	private FormLayout form1, form2, form3;
	// Parent layout for all the input controls
	private HorizontalLayout hlsearchlayout = new HorizontalLayout();
	private HorizontalLayout hluserInputlayout = new HorizontalLayout();
	private BeanItemContainer<MaterialVendorGrpDM> beanMaterialVendorGrpDM = null;
	private ComboBox cbMaterial, cbStatus;
	private ListSelect cbVendor;
	private Long materialVendorGrpId;
	private String username;
	private Boolean errorFlag = false;
	private Long companyid;
	private int recordCnt = 0;
	// Initialize logger
	private Logger logger = Logger.getLogger(MaterialVendorGroup.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor received the parameters from Login UI class
	public MaterialVendorGroup() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside MaterialVendorGrp() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting MaterialVendorGroup UI");
		// Material Name ComboBox
		cbMaterial = new GERPComboBox("Material Name");
		cbMaterial.setItemCaptionPropertyId("materialName");
		loadMaterialNameList();
		// Vendor Name ComboBox
		cbVendor = new ListSelect("Vendor Name");
		cbVendor.setItemCaptionPropertyId("vendorName");
		cbVendor.setNullSelectionAllowed(false);
		cbVendor.setMultiSelect(true);
		cbVendor.setImmediate(true);
		cbVendor.setWidth("150px");
		cbVendor.setHeight("110px");
		loadVendorNameList();
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// build search layout
		hlsearchlayout = new GERPAddEditHLayout();
		assemblesearchlayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assemblesearchlayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlsearchlayout.removeAllComponents();
		form1 = new GERPFormLayout();
		form2 = new GERPFormLayout();
		form3 = new GERPFormLayout();
		form1.addComponent(cbMaterial);
		form3.addComponent(cbStatus);
		hlsearchlayout.addComponent(form1);
		hlsearchlayout.addComponent(form3);
		hlsearchlayout.setSizeUndefined();
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		hluserInputlayout.removeAllComponents();
		form1 = new GERPFormLayout();
		form2 = new GERPFormLayout();
		form3 = new GERPFormLayout();
		form1.addComponent(cbMaterial);
		cbMaterial.setRequired(true);
		form2.addComponent(cbVendor);
		cbVendor.setRequired(true);
		form3.addComponent(cbStatus);
		hluserInputlayout.addComponent(form1);
		hluserInputlayout.addComponent(form2);
		hluserInputlayout.addComponent(form3);
		hluserInputlayout.setSpacing(true);
		hluserInputlayout.setMargin(true);
	}
	
	/*
	 * loadSrchRslt()-->this function is used for load the search result to table
	 */
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<MaterialVendorGrpDM> list = new ArrayList<MaterialVendorGrpDM>();
		list = servicegrpmaterialvendor.getMaterialVendorGrpList(null, (Long) cbMaterial.getValue(), null,
				(String) cbStatus.getValue(), "F");
		recordCnt = list.size();
		beanMaterialVendorGrpDM = new BeanItemContainer<MaterialVendorGrpDM>(MaterialVendorGrpDM.class);
		beanMaterialVendorGrpDM.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the vendorGrpList. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanMaterialVendorGrpDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "materialVendorGrpId", "materialName", "vendorName",
				"vendorGrpStatus", "lastUpdtDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Material Name", "Vendor Name", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("materialVendorGrpId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMaterial.setReadOnly(false);
		cbMaterial.setValue(null);
		cbVendor.setValue(null);
		cbMaterial.setComponentError(null);
		cbVendor.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	private void loadMaterialNameList() {
		try {
			BeanContainer<Long, MaterialDM> beanCtgry = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
			beanCtgry.setBeanIdProperty("materialId");
			beanCtgry.addAll(serviceMaterial.getMaterialList(null, companyid, null, null, null, null, null, null,
					"Active", "P"));
			cbMaterial.setContainerDataSource(beanCtgry);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadVendorNameList()-->this function is used for load the vendor name list
	 */
	private void loadVendorNameList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading VendorNameList");
			BeanContainer<Long, VendorDM> beanVendor = new BeanContainer<Long, VendorDM>(VendorDM.class);
			beanVendor.setBeanIdProperty("vendorId");
			beanVendor.addAll(serviceVendor.getVendorList(null, null, companyid, null, null, null, null, null,
					"Active", null, "P"));
			cbVendor.setContainerDataSource(beanVendor);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editMaterialVendorGrpDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hluserInputlayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				MaterialVendorGrpDM materialVendorGrpDM = beanMaterialVendorGrpDM.getItem(tblMstScrSrchRslt.getValue())
						.getBean();
				materialVendorGrpId = materialVendorGrpDM.getMaterialVendorGrpId();
				cbMaterial.setValue(materialVendorGrpDM.getMaterialId());
				cbVendor.setValue(null);
				Long vendorId = Long.valueOf(materialVendorGrpDM.getVendorId());
				Collection<?> vendorIds = cbVendor.getItemIds();
				for (Iterator<?> iteratorclient = vendorIds.iterator(); iteratorclient.hasNext();) {
					Object itemIdClient = (Object) iteratorclient.next();
					BeanItem<?> itemclient = (BeanItem<?>) cbVendor.getItem(itemIdClient);
					// Get the actual bean and use the data
					VendorDM matObj = (VendorDM) itemclient.getBean();
					if (vendorId != null && vendorId.equals(Long.valueOf(matObj.getVendorId()))) {
						cbVendor.select(itemIdClient);
					}
				}
				if (materialVendorGrpDM.getVendorGrpStatus() != null) {
					cbStatus.setValue(materialVendorGrpDM.getVendorGrpStatus());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
			hlUserIPContainer.removeAllComponents();
			hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
			resetFields();
			assembleUserInputLayout();
			cbMaterial.setRequired(true);
			cbVendor.setRequired(true);
			editMaterialVendorGrpDetails();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
			assemblesearchlayout();
		}
	}
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMaterial.setValue(null);
		cbVendor.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		resetFields();
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for materialVendorGrp.Id " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MMS_MATERIAL_VNDRGRP);
		UI.getCurrent().getSession().setAttribute("audittablepk", materialVendorGrpId);
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbMaterial.setComponentError(null);
		cbVendor.setComponentError(null);
		errorFlag = false;
		if (cbMaterial.getValue() == null) {
			cbMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbMaterial.getValue());
			errorFlag = true;
		}
		if (cbVendor.getValue() == null || cbVendor.getValue().toString() == "[]") {
			cbVendor.setComponentError(new UserError(GERPErrorCodes.NULL_VENDOR_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbVendor.getValue());
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() throws SaveException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			String[] split = cbVendor.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
			for (String obj : split) {
				if (obj.trim().length() > 0) {
					MaterialVendorGrpDM materialvendorgrpObj = new MaterialVendorGrpDM();
					if (tblMstScrSrchRslt.getValue() != null) {
						materialvendorgrpObj = beanMaterialVendorGrpDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
					}
					materialvendorgrpObj.setMaterialId((Long) cbMaterial.getValue());
					if (cbVendor.getValue() != null) {
						materialvendorgrpObj.setVendorId(Long.valueOf(obj.trim()));
						materialvendorgrpObj.setVendorName(serviceVendor
								.getVendorList(null, Long.valueOf(obj.trim()), null, null, null, null, null, null,
										null, null, "P").get(0).getVendorName());
					}
					if (cbStatus.getValue() != null) {
						materialvendorgrpObj.setVendorGrpStatus((String) cbStatus.getValue());
					}
					materialvendorgrpObj.setLastUpdtDate(DateUtils.getcurrentdate());
					materialvendorgrpObj.setLastUpdatedBy(username);
					servicegrpmaterialvendor.saveAndUpdate(materialvendorgrpObj);
				}
			}
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// BaseUI cancelDetails() implementation to get return back to the home page
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assemblesearchlayout();
		cbMaterial.setRequired(false);
		cbVendor.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
}