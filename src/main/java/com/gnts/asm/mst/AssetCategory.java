/**
 * File Name	:	AssetCategory.java
 * Description	:	To Handle asset category details for assets.
 * Author		:	Priyanga M
 * Date			:	March 07, 2014
 * Modification :   UI code optimization
 * Modified By  :   Nandhakumar.S
 * Description	:   Optimizing the code for asset Category UI 
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1			07-Mar-2014		Priyanga M				Initial version
 * 0.1			17-Jun-2014		Nandhkumar.S			code re-fragment
 * 0.2			31-JULY-2014	MOHAMED					code-Modify
 */
package com.gnts.asm.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.mst.AssetCategoryDM;
import com.gnts.asm.service.mst.AssetCategoryService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPSaveNotification;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class AssetCategory extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AssetCategoryService assetCatgryService = (AssetCategoryService) SpringContextHelper.getBean("assetCategory");
	// form layout for input controls
	private FormLayout flCategoryName, flCategoryStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfCategoryName;
	private ComboBox cbCatgryStatus;
	private BeanItemContainer<AssetCategoryDM> beanAssetCatgry = null;
	// local variables declaration
	private Long companyid;
	private String assetCatgryId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(AssetCategory.class);
	
	// Constructor
	public AssetCategory() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside AssetCategory() constructor");
		// Loading the UI
		buildView();
	}
	
	// Build the UI components
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting AssetBrand UI");
		// Asset BrandName text field
		tfCategoryName = new GERPTextField("Category Name");
		tfCategoryName.setMaxLength(25);
		cbCatgryStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// create form layouts to hold the input items
		flCategoryName = new FormLayout();
		flCategoryStatus = new FormLayout();
		// add the form layouts into user input layout
		flCategoryName.addComponent(tfCategoryName);
		flCategoryStatus.addComponent(cbCatgryStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(flCategoryName);
		hlUserInputLayout.addComponent(flCategoryStatus);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
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
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<AssetCategoryDM> assetCtgryList = new ArrayList<AssetCategoryDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfCategoryName.getValue() + ", " + (String) cbCatgryStatus.getValue());
		assetCtgryList = assetCatgryService.getAssetCategoryList(null,tfCategoryName.getValue(), (String)cbCatgryStatus.getValue(), "F");
		recordCnt = assetCtgryList.size();
		beanAssetCatgry = new BeanItemContainer<AssetCategoryDM>(AssetCategoryDM.class);
		beanAssetCatgry.addAll(assetCtgryList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Dept. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanAssetCatgry);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "catgryId", "catgryName", "catgryStatus", "lastUpdatedDate",
				"lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Category Name", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("catgryId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfCategoryName.setValue("");
		tfCategoryName.setComponentError(null);
		cbCatgryStatus.setValue(cbCatgryStatus.getItemIds().iterator().next());
		
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editAssetCatgry() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			AssetCategoryDM enqdtl = beanAssetCatgry.getItem(tblMstScrSrchRslt.getValue()).getBean();
			tfCategoryName.setValue(enqdtl.getCatgryName());
			cbCatgryStatus.setValue((String) cbCatgryStatus.getValue());
			assetCatgryId = enqdtl.getCatgryId();
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
	
	// BaseUI searchDetails() to the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		// reset the field valued to default
		cbCatgryStatus.setValue(cbCatgryStatus.getItemIds().iterator().next());
		tfCategoryName.setValue("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	// BaseUI addDetails() implementation to add the required components for add
	// mode into the hlUserIPContainer
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// Adding user input fields into userIPContainer
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfCategoryName.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	// BaseUI showAuditDetails() implementation to show audit details which
	// value has been modified already.
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for AssetCatgry. ID " + assetCatgryId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_AMS_ASSET_CATEGORY);
		UI.getCurrent().getSession().setAttribute("audittablepk", assetCatgryId);
	}
	
	// BaseUI cancelDetails() implementation to get return back to the home page
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfCategoryName.setRequired(false);
		// tblMstScrSrchRslt.setValue(null);
		resetFields();
	}
	
	// BaseUI editDetails() implementation to add the required components for
	// edit mode into the hlUserIPContainer
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfCategoryName.setRequired(true);
		editAssetCatgry();
	}
	
	// BaseUI validateDetails() implementation to validate the input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		flCategoryName.setComponentError(null);
		if ((tfCategoryName.getValue() == null) || tfCategoryName.getValue().trim().length() == 0) {
			tfCategoryName.setComponentError(new UserError(GERPErrorCodes.NULL_ASST_CATGRY_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfCategoryName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	// Insert and update values into the table
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		AssetCategoryDM assetCtgryObj = new AssetCategoryDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			assetCtgryObj = beanAssetCatgry.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		assetCtgryObj.setCompanyId(companyid);
		assetCtgryObj.setCatgryName(tfCategoryName.getValue().toString());
		if (cbCatgryStatus.getValue() != null) {
			assetCtgryObj.setCatgryStatus((String) cbCatgryStatus.getValue());
		}
		assetCtgryObj.setLastUpdatedDate(DateUtils.getcurrentdate());
		assetCtgryObj.setLastUpdatedBy(username);
		assetCatgryService.saveAndUpdateAssetCategory(assetCtgryObj);
		// Display successful save message
		new GERPSaveNotification();
		resetFields();
		loadSrchRslt();
	}
}
