/**
 * File Name	:	SyncLogger.java
 * Description	:	Used for view T_GCAT_SYNCLOGGER details
 * Author		:	Prakash.s
 * Date			:	mar 1, 2014
 * Modification 
 * Modified By  :   prakash.s
 * Description	:
 *
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies pvt. ltd.
 * Version         Date           Modified By             Remarks
 * 
 */
package com.gnts.gcat.rpt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.gcat.domain.mst.CompanyDevicesDM;
import com.gnts.gcat.domain.rpt.SyncLoggerDM;
import com.gnts.gcat.service.mst.CompanyDevicesService;
import com.gnts.gcat.service.rpt.SyncLoggerService;
import com.gnts.gcat.txn.RateSettings;
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

public class SyncLogger extends BaseUI {
	private SyncLoggerService servicesyncLogger = (SyncLoggerService) SpringContextHelper.getBean("syncLogger");
	private CompanyDevicesService servicecompanyDevice = (CompanyDevicesService) SpringContextHelper
			.getBean("gcatCompanyDevice");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tflicError, tfemailId, tflogMsg;
	private ComboBox cblogType, cbcode, cbstatus, cbemailStatus,cbSerDev,cbSearStatus,cbsearLogType;
	private BeanContainer<String, CompanyDevicesDM>codeList= null;
	private PopupDateField dflogDate;
	// Bean Container
	private BeanItemContainer<SyncLoggerDM> beanSyncLoggerDM = null;
	// Local variables
	private Long companyid;
	private String username;
	private int recordCnt = 0;
	private Long userId;
	// Initialize logger
	private Logger logger = Logger.getLogger(RateSettings.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public SyncLogger() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmailLogger() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Email logger UI");
		// Client text box
		btnAdd.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnEdit.setCaption("View");
		btnEdit.setStyleName("view");
		
		tfemailId = new TextField("Email Id");
		tfemailId.setReadOnly(false);
		// RAte Changing text box
		tflicError = new TextField("License Error");
		tflicError.setReadOnly(false);
		// product combo box
		tflogMsg = new TextField("Log Message");
		tflogMsg.setReadOnly(false);
		
		cbsearLogType = new ComboBox("Log Type");
		cbsearLogType.addItem("SYNC");
		cbsearLogType.addItem("ERR");
		cbsearLogType.addItem("EMAIL");
		
		// productcombo box for search
		cbSearStatus = new GERPComboBox("Sync Status");
		cbSearStatus.addItem("Success");
		cbSearStatus.addItem("Failed");
		
		cbstatus = new GERPComboBox("Sync Status");
		cbstatus.addItem("Success");
		cbstatus.addItem("Failed");
		cbstatus.setReadOnly(false);
		
		
		cblogType = new GERPComboBox("Log Type");
		cblogType.addItem("SYNC");
		cblogType.addItem("ERR");
		cblogType.addItem("EMAIL");
		
		cblogType.setReadOnly(false);
		// client TExt for Search
		
		cbcode = new GERPComboBox("Device Code");
		cbcode.setItemCaptionPropertyId("deviceCode");
		loaddevice();
		
		
		cbSerDev = new GERPComboBox("Device Code");
		cbSerDev.setItemCaptionPropertyId("deviceCode");
		loaddevice();
		
		cbemailStatus = new GERPComboBox("Email Status");
		cbemailStatus.addItem("Delivered");
		cbemailStatus.addItem("Failed");
		cbemailStatus.setReadOnly(false);
		// Change start date
		dflogDate = new GERPPopupDateField("Log Date");
		dflogDate.setInputPrompt("Select Date");
		dflogDate.setReadOnly(false);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		//assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		// Add components for Search Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn1.addComponent(cbsearLogType);
		flColumn2.addComponent(cbSerDev);
		flColumn3.addComponent(cbSearStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSizeUndefined();
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		btnSave.setVisible(false);
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setMargin(true);
		// Add components for User Input Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		flColumn1.addComponent(cblogType);
		flColumn1.addComponent(dflogDate);
		flColumn2.addComponent(cbcode);
		flColumn2.addComponent(tflicError);
		flColumn3.addComponent(tflogMsg);
		flColumn3.addComponent(tfemailId);
		flColumn4.addComponent(cbemailStatus);
		flColumn4.addComponent(cbstatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSpacing(true);
		
		
		
	}
	
	private void viewLogger() {
		Item rowSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (rowSelected != null) {
			SyncLoggerDM enqdtl = beanSyncLoggerDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			setReadOnlyFalseFields();
			cblogType.setValue(enqdtl.getLogType());
			//cblogType.setReadOnly(true);
			cbcode.setValue(enqdtl.getDeviceCode());
			
			dflogDate.setValue(enqdtl.getLogDate());
			tflicError.setValue(enqdtl.getLicenseError()); 
			tflogMsg.setValue(enqdtl.getLogMessage());
			tfemailId.setValue(enqdtl.getEmailId());
			cbemailStatus.setValue(enqdtl.getEmailStatus());
			cbstatus.setValue(enqdtl.getSyncStatus());
			
			setReadOnlyTrueFields();
		}
	}
	
	public void setReadOnlyFalseFields() {
		cbcode.setReadOnly(false);
		cblogType.setReadOnly(false);
		cbstatus.setReadOnly(false);
		tfemailId.setReadOnly(false);
		tflicError.setReadOnly(false);
		tflogMsg.setReadOnly(false);
		cbemailStatus.setReadOnly(false);
		dflogDate.setReadOnly(false); 
	}
	
	public void setReadOnlyTrueFields() {
		cbcode.setReadOnly(true);
		cblogType.setReadOnly(true);
		cbstatus.setReadOnly(true);
		tfemailId.setReadOnly(true);
		tflicError.setReadOnly(true);
		tflogMsg.setReadOnly(true);
		cbstatus.setReadOnly(true);
		cbemailStatus.setReadOnly(true);
		dflogDate.setReadOnly(true); 
	}
	
	// get the search result from DB based on the search SyngLogger
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		String logmsg = null;
		if (cbsearLogType.getValue() != null) {
			logmsg = ((String) cbsearLogType.getValue());
		}
		List<SyncLoggerDM> synchLoggerList = new ArrayList<SyncLoggerDM>();
		synchLoggerList = servicesyncLogger.getSyncLoggerList(null, logmsg,(String) cbSerDev.getValue(),(String) cbSearStatus.getValue(), "F");
		recordCnt = synchLoggerList.size();
		beanSyncLoggerDM = new BeanItemContainer<SyncLoggerDM>(SyncLoggerDM.class);
		beanSyncLoggerDM.addAll(synchLoggerList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Ratesetting. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanSyncLoggerDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "logId", "logType", "deviceCode", "licenseError", "syncStatus",
				"emailId", "emailStatus" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Log Type", "Device code", "License Error",
				"Sync Status", "Email Id", "Email Status" });
		tblMstScrSrchRslt.setColumnAlignment("logId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("logMessage", "No.of Records : " + recordCnt);
		System.out.println("Records"+recordCnt);
	}
	
	private void loaddevice() {
		try {
			List<CompanyDevicesDM> devicelist = servicecompanyDevice.getCompanyDevicesList(null, null, null, null,
					null, "Active", null);
			codeList = new BeanContainer<String, CompanyDevicesDM>(CompanyDevicesDM.class);
			codeList.setBeanIdProperty("deviceCode");
			codeList.addAll(devicelist);
			cbSerDev.setContainerDataSource(codeList);
			cbcode.setContainerDataSource(codeList);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("fn_loadProductList_Exception Caught->" + e);
		}
	}

	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		hlUserInputLayout.removeAllComponents();
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbsearLogType.setValue(null);
		cbsearLogType.setComponentError(null);
		cbSerDev.setValue(null);
		cbSerDev.setComponentError(null); 
		cbSearStatus.setValue(null);
		cbSearStatus.setComponentError(null); 
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		// reset the input controls to default value
		resetFields();
		assembleUserInputLayout();
		viewLogger();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		setReadOnlyFalseFields();
		tflogMsg.setValue("");
		dflogDate.setValue(null);
		tflicError.setValue("");
		tfemailId.setValue(null);
		cbcode.setValue(null);
	//	cbemailStatus.setValue(null);
		cblogType.setValue(null);
		cbstatus.setValue(null);
		setReadOnlyTrueFields();
	}
}

