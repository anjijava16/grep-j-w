/**
 * File Name	:	AssetBrand.java
 * Description	:	To Handle asset brand details for assets.
 * Author		:	Priyanga M
 * Date			:	March 06, 2014
 * Modification :   UI code optimization
 * Modified By  :   Nandhakumar.S
 * Description	:   Optimizing the code for asset brand UI 
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1			16-Jun-2014		Nandhakumar.S			code refractment
 * 0.2          31-JULY-2014    MOHAMED					Code Modify
 */
package com.gnts.asm.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.mst.AssetBrandDM;
import com.gnts.asm.service.mst.AssetBrandService;
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
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class AssetBrand extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AssetBrandService assetBrandService = (AssetBrandService) SpringContextHelper.getBean("assetBrand");
	// form layout for input controls
	private FormLayout flBrandName, flBrandStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfBrandName;
	private ComboBox cbBrandStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<AssetBrandDM> beanAssetbrand = null;
	// local variables declaration
	private Long companyid;
	private String assetId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(AssetBrand.class);
	
	// Constructor
	public AssetBrand() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside AssetBrand() constructor");
		// Loading the AssetBrand UI
		buildView();
	}
	
	// Build the UI components
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting AssetBrand UI");
		// Asset BrandName text field
		tfBrandName = new GERPTextField("Brand Name");
		tfBrandName.setMaxLength(25);
		tfBrandName.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfBrandName.setComponentError(null);
				if (tfBrandName.getValue() != null) {
					tfBrandName.setComponentError(null);
				}
			}
		});
		// create form layouts to hold the input items
		flBrandName = new FormLayout();
		flBrandStatus = new FormLayout();
		// add the form layouts into user input layout
		flBrandName.addComponent(tfBrandName);
		flBrandStatus.addComponent(cbBrandStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(flBrandName);
		hlUserInputLayout.addComponent(flBrandStatus);
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
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<AssetBrandDM> assetList = new ArrayList<AssetBrandDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfBrandName.getValue() + ", " + (String) cbBrandStatus.getValue());
		assetList = assetBrandService.getAssetBrandList(null, tfBrandName.getValue(),
				(String) cbBrandStatus.getValue(), "F");
		recordCnt = assetList.size();
		beanAssetbrand = new BeanItemContainer<AssetBrandDM>(AssetBrandDM.class);
		beanAssetbrand.addAll(assetList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the AssetBrand. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanAssetbrand);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "brandid", "brandname", "brandstatus", "lastupdateddate",
				"lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Brand Name", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("brandid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfBrandName.setValue("");
		tfBrandName.setComponentError(null);
		cbBrandStatus.setValue(cbBrandStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	protected void editAssetBrand() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			AssetBrandDM assetBrandDM = beanAssetbrand.getItem(tblMstScrSrchRslt.getValue()).getBean();
			tfBrandName.setValue(assetBrandDM.getBrandname());
			cbBrandStatus.setValue((String) assetBrandDM.getBrandstatus());
			assetId = assetBrandDM.getBrandid().toString();
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
		cbBrandStatus.setValue(cbBrandStatus.getItemIds().iterator().next());
		tfBrandName.setValue("");
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
		tfBrandName.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	// BaseUI showAuditDetails() implementation to show audit details which
	// value has been modified already.
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for AssetBrand. ID " + assetId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_AMS_ASSET_BRAND);
		UI.getCurrent().getSession().setAttribute("audittablepk", assetId);
	}
	
	// BaseUI cancelDetails() implementation to get return back to the home page
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfBrandName.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	// BaseUI editDetails() implementation to add the required components for
	// edit mode into the hlUserIPContainer
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfBrandName.setRequired(true);
		editAssetBrand();
	}
	
	// BaseUI validateDetails() implementation to validate the input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfBrandName.setComponentError(null);
		if ((tfBrandName.getValue() == null) || tfBrandName.getValue().trim().length() == 0) {
			tfBrandName.setComponentError(new UserError(GERPErrorCodes.NULL_ASST_BRAND_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfBrandName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	// Insert and update values into the table
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		AssetBrandDM assetBrandObj = new AssetBrandDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			assetBrandObj = beanAssetbrand.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		assetBrandObj.setCompanyid(companyid);
		assetBrandObj.setBrandname(tfBrandName.getValue().toString());
		if (cbBrandStatus.getValue() != null) {
			assetBrandObj.setBrandstatus(cbBrandStatus.getValue().toString());
		}
		assetBrandObj.setLastupdateddate(DateUtils.getcurrentdate());
		assetBrandObj.setLastupdatedby(username);
		assetBrandService.saveOrUpdate(assetBrandObj);
		// Display successful save message
		new GERPSaveNotification();
		resetFields();
		loadSrchRslt();
	}
}
