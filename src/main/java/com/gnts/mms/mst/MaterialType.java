/**
 * File Name	:	MaterialType.java
 * Description	:	This Screen Purpose for Modify the Material Type Details.Add the Material Type details process should be directly added in DB.
 * Author		:	Mahaboob Subahan J
 * Date			:	Jul 11, 2014
 * Modification :   
 * Modified By  :   
 * Description 	:
 *
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version      Date           	Modified By      		Remarks
 * 0.1          Jul 11, 2014   	Mahaboob Subahan J		Initial Version		
 * 
 */
package com.gnts.mms.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
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
import com.gnts.mms.domain.mst.MaterialTypeDM;
import com.gnts.mms.service.mst.MaterialTypeService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class MaterialType extends BaseUI {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(MaterialType.class);
	private MaterialTypeService serviceMaterialType = (MaterialTypeService) SpringContextHelper.getBean("materialType");
	private TextField tfMatTypeName;
	private ComboBox cbMatTypeStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private BeanItemContainer<MaterialTypeDM> beanMaterialType = null;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// form layout for input controls
	private FormLayout flMatTypeName, flMatStatus;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private String userName;
	private Long companyId;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public MaterialType() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside MaterialType() constructor");
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Painting Material Type UI");
		tfMatTypeName = new GERPTextField("Material Type Name");
		tfMatTypeName.setWidth("225");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	/*
	 * loadSrchRslt()-->this function is used for load the search result to table
	 */
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.setPageLength(15);
		tblMstScrSrchRslt.removeAllItems();
		List<MaterialTypeDM> materialTypeList = new ArrayList<MaterialTypeDM>();
		String matTypeName = tfMatTypeName.getValue().toString();
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ matTypeName + "," + cbMatTypeStatus.getValue() + "," + companyId);
		materialTypeList = serviceMaterialType.getMaterialTypeList(null, matTypeName,
				(String) cbMatTypeStatus.getValue(), "F");
		recordCnt = materialTypeList.size();
		beanMaterialType = new BeanItemContainer<MaterialTypeDM>(MaterialTypeDM.class);
		beanMaterialType.addAll(materialTypeList);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Got the Material Type result set");
		tblMstScrSrchRslt.setContainerDataSource(beanMaterialType);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "materialTypeId", "materialTypeName", "materialTypeStatus",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Material Type Name", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("materialTypeId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Assembling User Search layout");
		tfMatTypeName.setRequired(false);
		// Remove all components in Search Layout
		hlSearchLayout.removeAllComponents();
		// Add components for Search Layout
		flMatTypeName = new FormLayout();
		flMatStatus = new FormLayout();
		flMatTypeName.addComponent(tfMatTypeName);
		flMatStatus.addComponent(cbMatTypeStatus);
		hlSearchLayout.addComponent(flMatTypeName);
		hlSearchLayout.addComponent(flMatStatus);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling User Input layout");
		hlUserInputLayout.removeAllComponents();
		tfMatTypeName.setRequired(true);
		// Add components for User Input Layout
		FormLayout fl1 = new FormLayout();
		FormLayout fl2 = new FormLayout();
		fl1.addComponent(tfMatTypeName);
		fl2.addComponent(cbMatTypeStatus);
		// Add components for User Input Layout
		hlUserInputLayout.addComponent(fl1);
		hlUserInputLayout.addComponent(fl2);
		hlUserInputLayout.setSizeFull();
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSizeUndefined();
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		tfMatTypeName.setValue("");
		cbMatTypeStatus.setValue(cbMatTypeStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editMaterialTypeName() {
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		MaterialTypeDM editMatTypeNamelist = beanMaterialType.getItem(tblMstScrSrchRslt.getValue()).getBean();
		if (editMatTypeNamelist.getMaterialTypeName() != null) {
			logger.info("editMaterialTypeName : " + tfMatTypeName.getValue() + "," + cbMatTypeStatus.getValue());
			tfMatTypeName.setValue(sltedRcd.getItemProperty("materialTypeName").getValue().toString());
		}
		String stCode = sltedRcd.getItemProperty("materialTypeStatus").getValue().toString();
		cbMatTypeStatus.setValue(stCode);
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		resetFields();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tblMstScrSrchRslt.setValue(null);
		assembleUserInputLayout();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Updating existing record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		resetFields();
		editMaterialTypeName();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		tfMatTypeName.setComponentError(null);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((tfMatTypeName.getValue() == null) || tfMatTypeName.getValue().trim().length() == 0) {
			tfMatTypeName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_TYPE_NAME));
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + tfMatTypeName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	/*
	 * saveDetails()-->this function is used for save/update the records
	 */
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
		MaterialTypeDM materialTypeObj = new MaterialTypeDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			materialTypeObj = beanMaterialType.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		materialTypeObj.setCompanyId(companyId);
		materialTypeObj.setMaterialTypeName(tfMatTypeName.getValue().toString());
		if (cbMatTypeStatus.getValue() != null) {
			materialTypeObj.setMaterialTypeStatus((String) cbMatTypeStatus.getValue());
		}
		materialTypeObj.setLastupdateddt(DateUtils.getcurrentdate());
		materialTypeObj.setLastupdatedby(userName);
		serviceMaterialType.saveOrUpdateMaterialType(materialTypeObj);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		tfMatTypeName.setValue("");
		tfMatTypeName.setComponentError(null);
		cbMatTypeStatus.setValue(cbMatTypeStatus.getItemIds().iterator().next());
	}
}
