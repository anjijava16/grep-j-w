/**
 * File Name	:	AuditConfig.java
 * Description	:	this class have UI functions 
 * Author		:	P Sekhar
 * Date			:	Feb 27, 2014
 * Modification 
 * Modified By  :   
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of  GNTS Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          Feb 27, 2014       	P Sekhar		        Initial Version
 * 0.2			5-Jul-2014			Abdullah.H				Code Optimization
 */
package com.gnts.base.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.txn.AuditConfigDM;
import com.gnts.base.service.txn.AuditConfigService;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.UI;

//import com.gnts.erputil.ui.PanelGenerator;
public class AuditConfig extends BaseUI {
	private AuditConfigService serviceAuditConfig = (AuditConfigService) SpringContextHelper.getBean("auditConfig");
	// form layout for input controls
	FormLayout fltableName;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private ComboBox cbTableName = new GERPComboBox("Table Name");
	private CheckBox ckBox;
	// local variables declaration
	private int recordCnt = 0;
	private String strLoginUserName;
	private Long companyId;
	// Initialize logger
	private Logger logger = Logger.getLogger(AuditConfig.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public AuditConfig() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Inside AuditConfig() constructor");
		strLoginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + cbTableName.getValue() + "Painting AuditConfig UI");
		// Not Visible Buttons
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnSearch.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnReset.setVisible(false);
		btnSave.setVisible(true);
		btnDownload.setVisible(true);
		// To load Table field
		loadsearchAuditConfigList();
		cbTableName.setItemCaptionPropertyId("screendesc");
		cbTableName.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbTableName.getItem(itemId);
				if (item != null) {
					// Get the actual bean and use the data
					loadSrchRslt();
					tblMstScrSrchRslt.setVisible(true);
				}
			}
		});
		
		cbTableName.setWidth("200px");
		// create form layouts to hold the input items
		fltableName = new FormLayout();
		// create form layouts to hold the input items
		fltableName.addComponent(cbTableName);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(fltableName);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + cbTableName.getValue() + ", Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
	}
	
	// load the lookup details for search
	private void loadsearchAuditConfigList() {
		BeanContainer<String, AuditConfigDM> auditConfigbean = new BeanContainer<String, AuditConfigDM>(
				AuditConfigDM.class);
		auditConfigbean.setBeanIdProperty("tableName");
		auditConfigbean.addAll(serviceAuditConfig.getColumnNameByTableName(null,(String)cbTableName.getValue(), companyId,null));
		cbTableName.setContainerDataSource(auditConfigbean);
	}
	
	public void loadSrchRslt() {
		btnCancel.setVisible(false);
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + cbTableName.getValue() + "Loading Search...");
		try {
			tblMstScrSrchRslt.removeAllItems();
			List<AuditConfigDM> auditConfigList = new ArrayList<AuditConfigDM>();
			auditConfigList = serviceAuditConfig.getColumnNameByTableName(null,(String) cbTableName.getValue(), companyId,null);
			recordCnt = auditConfigList.size();
			tblMstScrSrchRslt.setEditable(true);
			BeanItemContainer<AuditConfigDM> BeanAuditConfigDM = new BeanItemContainer<AuditConfigDM>(AuditConfigDM.class);
			for (AuditConfigDM auditList : auditConfigList) {
				if (auditList.getOnOff().equals("ON")) {
					auditList.setOnOff("true");
				} else {
					auditList.setOnOff("false");
				}
			}
			BeanAuditConfigDM.addAll(auditConfigList);
			tblMstScrSrchRslt.setTableFieldFactory(new TableFieldFactory() {
			private static final long serialVersionUID = 1L;
			public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
					if (propertyId.toString().equals("onOff")) {
						ckBox = new CheckBox();
						ckBox.setValue(true);
						return ckBox;
					}
					return null;
				}
			});
			tblMstScrSrchRslt.setContainerDataSource(BeanAuditConfigDM);
			tblMstScrSrchRslt.setColumnFooter("onOff", "No.of Records:" + recordCnt);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "configId", "screendesc", "scrFldName", "auditEvent",
					"onOff" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Table", "Column", "Audit Event",
					"On Off" });
			tblMstScrSrchRslt.setSelectable(true);
		}
		catch (Exception e) {
			logger.error("error on loadSrchRslt(), The Error is ----->" + e);
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		tblMstScrSrchRslt.setVisible(true);
	}
	
	@Override
	protected void resetSearchDetails() {
		// Do not have a code here...!
	}
	
	@Override
	protected void addDetails() {
		// Do not have a code here...!
	}
	
	@Override
	protected void editDetails() {
		// Do not have a code here...!
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		// Do not have a code here...!
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + cbTableName.getValue() + "Save Method");
		try {
			tblMstScrSrchRslt.setVisible(false);
			btnDownload.setVisible(false);
			btnSave.setComponentError(null);
			@SuppressWarnings("unchecked")
			Collection<AuditConfigDM> itemIds = (Collection<AuditConfigDM>) tblMstScrSrchRslt.getVisibleItemIds();
			for (AuditConfigDM pojo1 : (Collection<AuditConfigDM>) itemIds) {
				AuditConfigDM updateConfig = new AuditConfigDM();
				updateConfig.setColumnName(pojo1.getColumnName());
				updateConfig.setAuditEvent(pojo1.getAuditEvent());
				updateConfig.setCompanyId(pojo1.getCompanyId());
				updateConfig.setConfigId(pojo1.getConfigId());
				updateConfig.setTableName(pojo1.getTableName());
				if (pojo1.getOnOff().equals("true")) {
					updateConfig.setOnOff("ON");
				} else {
					updateConfig.setOnOff("OFF");
				}
				updateConfig.setScreenid(pojo1.getScreenid());
				updateConfig.setScrFldName(pojo1.getScrFldName());
				serviceAuditConfig.saveAndUpdateDetails(updateConfig);
				btnCancel.setVisible(true);
				resetFields();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		// Do not have a code here...!
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName);
		tblMstScrSrchRslt.setVisible(true);
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnSearch.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnReset.setVisible(false);
		btnSave.setVisible(true);
		btnCancel.setVisible(false);
		btnDownload.setVisible(true);
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName);
		cbTableName.setValue(null);
	}
}
