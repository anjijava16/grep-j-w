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
import com.vaadin.data.Item;
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
	private MaterialService servicematerial = (MaterialService) SpringContextHelper.getBean("material");
	private VendorService servicevendor = (VendorService) SpringContextHelper.getBean("Vendor");
	// Form layout for input controls
	private FormLayout form1, form2, form3;
	// Parent layout for all the input controls
	private HorizontalLayout hlsearchlayout = new HorizontalLayout();
	private HorizontalLayout hluserInputlayout = new HorizontalLayout();
	private BeanItemContainer<MaterialVendorGrpDM> beanMaterialVendorGrpDM = null;
	private ComboBox cbmatname, cbvengrpstatus;
	private ListSelect cbvenname;
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
	public void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting MaterialVendorGroup UI");
		// Material Name ComboBox
		cbmatname = new GERPComboBox("Material Name");
		cbmatname.setItemCaptionPropertyId("materialName");
		loadMaterialNameList();
		// Vendor Name ComboBox
		cbvenname = new ListSelect("Vendor Name");
		cbvenname.setItemCaptionPropertyId("vendorName");
		cbvenname.setNullSelectionAllowed(false);
		cbvenname.setMultiSelect(true);
		cbvenname.setImmediate(true);
		cbvenname.setWidth("150px");
		cbvenname.setHeight("110px");
		loadVendorNameList();
		// Status ComboBox
		cbvengrpstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// build search layout
		hlsearchlayout = new GERPAddEditHLayout();
		assemblesearchlayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
	}
	
	public void assemblesearchlayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlsearchlayout.removeAllComponents();
		form1 = new GERPFormLayout();
		form2 = new GERPFormLayout();
		form3 = new GERPFormLayout();
		form1.addComponent(cbmatname);
		form3.addComponent(cbvengrpstatus);
		hlsearchlayout.addComponent(form1);
		hlsearchlayout.addComponent(form3);
		hlsearchlayout.setSizeUndefined();
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setMargin(true);
	}
	
	public void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		hluserInputlayout.removeAllComponents();
		form1 = new GERPFormLayout();
		form2 = new GERPFormLayout();
		form3 = new GERPFormLayout();
		form1.addComponent(cbmatname);
		cbmatname.setRequired(true);
		form2.addComponent(cbvenname);
		cbvenname.setRequired(true);
		form3.addComponent(cbvengrpstatus);
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
		List<MaterialVendorGrpDM> vendorGrpList = new ArrayList<MaterialVendorGrpDM>();
		vendorGrpList = servicegrpmaterialvendor.getMaterialVendorGrpList(null, (Long) cbmatname.getValue(), null,
				(String) cbvengrpstatus.getValue(), "F");
		recordCnt = vendorGrpList.size();
		beanMaterialVendorGrpDM = new BeanItemContainer<MaterialVendorGrpDM>(MaterialVendorGrpDM.class);
		beanMaterialVendorGrpDM.addAll(vendorGrpList);
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
		cbmatname.setReadOnly(false);
		cbmatname.setValue(null);
		cbvenname.setValue(null);
		cbmatname.setComponentError(null);
		cbvenname.setComponentError(null);
		cbvengrpstatus.setValue(cbvengrpstatus.getItemIds().iterator().next());
	}
	
	public void loadMaterialNameList() {
		List<MaterialDM> materiallist = servicematerial.getMaterialList(null, companyid, null, null, null, null, null,
				null, "Active", "F");
		BeanContainer<Long, MaterialDM> beanCtgry = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
		beanCtgry.setBeanIdProperty("materialId");
		beanCtgry.addAll(materiallist);
		cbmatname.setContainerDataSource(beanCtgry);
	}
	
	/*
	 * loadVendorNameList()-->this function is used for load the vendor name list
	 */
	public void loadVendorNameList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading VendorNameList");
		List<VendorDM> vendorlist = servicevendor.getVendorList(null, null, companyid, null, null, null, null, null,
				"Active", null, "P");
		BeanContainer<Long, VendorDM> beanVendor = new BeanContainer<Long, VendorDM>(VendorDM.class);
		beanVendor.setBeanIdProperty("vendorId");
		beanVendor.addAll(vendorlist);
		cbvenname.setContainerDataSource(beanVendor);
		System.out.println("cbvenname" + cbvenname);
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editMaterialVendorGrpDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hluserInputlayout.setVisible(true);
			Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			if (sltedRcd != null) {
				MaterialVendorGrpDM editMaterialVendorGrplist = beanMaterialVendorGrpDM.getItem(
						tblMstScrSrchRslt.getValue()).getBean();
				materialVendorGrpId = editMaterialVendorGrplist.getMaterialVendorGrpId();
				cbmatname.setValue(editMaterialVendorGrplist.getMaterialId());
				cbvenname.setValue(null);
				Long matId = Long.valueOf(editMaterialVendorGrplist.getVendorId());
				Collection<?> empColId = cbvenname.getItemIds();
				for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
					Object itemIdClient = (Object) iteratorclient.next();
					BeanItem<?> itemclient = (BeanItem<?>) cbvenname.getItem(itemIdClient);
					// Get the actual bean and use the data
					VendorDM matObj = (VendorDM) itemclient.getBean();
					if (matId != null && matId.equals(Long.valueOf(matObj.getVendorId()))) {
						cbvenname.select(itemIdClient);
					}
				}
				if (editMaterialVendorGrplist.getVendorGrpStatus() != null) {
					cbvengrpstatus.setValue(sltedRcd.getItemProperty("vendorGrpStatus").getValue());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
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
			cbmatname.setRequired(true);
			cbvenname.setRequired(true);
			editMaterialVendorGrpDetails();
		}
		catch (Exception e) {
			e.printStackTrace();
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
		cbmatname.setValue(null);
		cbvenname.setValue(null);
		cbvengrpstatus.setValue(cbvengrpstatus.getItemIds().iterator().next());
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
		cbmatname.setComponentError(null);
		cbvenname.setComponentError(null);
		errorFlag = false;
		if (cbmatname.getValue() == null) {
			cbmatname.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbmatname.getValue());
			errorFlag = true;
		}
		if (cbvenname.getValue() == null || cbvenname.getValue().toString() == "[]") {
			cbvenname.setComponentError(new UserError(GERPErrorCodes.NULL_VENDOR_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbvenname.getValue());
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
			String[] split = cbvenname.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
			for (String obj : split) {
				if (obj.trim().length() > 0) {
					MaterialVendorGrpDM materialvendorgrpObj = new MaterialVendorGrpDM();
					if (tblMstScrSrchRslt.getValue() != null) {
						materialvendorgrpObj = beanMaterialVendorGrpDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
					}
					materialvendorgrpObj.setMaterialId((Long) cbmatname.getValue());
					if (cbvenname.getValue() != null) {
						materialvendorgrpObj.setVendorId(Long.valueOf(obj.trim()));
						materialvendorgrpObj.setVendorName(servicevendor
								.getVendorList(null, Long.valueOf(obj.trim()), null, null, null, null, null, null,
										null, null, "P").get(0).getVendorName());
					}
					if (cbvengrpstatus.getValue() != null) {
						materialvendorgrpObj.setVendorGrpStatus((String) cbvengrpstatus.getValue());
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
		cbmatname.setRequired(false);
		cbvenname.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
}