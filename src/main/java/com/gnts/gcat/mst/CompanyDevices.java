/**
 * File Name	:	CompanyDevices.java
 * Description	:	Used for view M_GCAT_COMPANY_DEVICES details
 * Author		:	Prakash.s
 * Date			:	mar 1, 2014
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies pvt. ltd.
 * Version         Date           Modified By             Remarks
 * 0.1            05.08.2014       Ganga               Code Optimizing&code re-factoring
 */
package com.gnts.gcat.mst;

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
import com.gnts.gcat.service.mst.CompanyDevicesService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class CompanyDevices extends BaseUI {
	private CompanyDevicesService serviceCompanyDevice = (CompanyDevicesService) SpringContextHelper
			.getBean("gcatCompanyDevice");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfSlNo, tfDMake, tfDModel, tfDeviceCode, tfDEviceMake;
	private TextField tfSlNoSear, tfDMakeSear, tfDModelSearch;
	private ComboBox cbstatus, cbstatusSearch;
	private PopupDateField dfREnewedDate, dfVaildDate;
	// Bean Container
	private BeanItemContainer<CompanyDevicesDM> beanCompanyDeviceDM = null;
	// Local variables
	private Long companyid;
	private String username;
	private int recordCnt = 0;
	private Long userId;
	// Initialize logger
	private Logger logger = Logger.getLogger(CompanyDevices.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public CompanyDevices() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		userId = Long.valueOf(UI.getCurrent().getSession().getAttribute("userId").toString());
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
		// Sl.NO text feild
		tfSlNoSear = new TextField("Sl.No");
		// DMake text box
		tfDMakeSear = new TextField("Device Make");
		// TExtFeild device Model
		tfDModelSearch = new TextField("Device Model");
		// Statuscombo box for search
		cbstatusSearch = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// TextField Device Code
		tfDeviceCode = new TextField("Device Code");
		// textfield Serial no
		tfSlNo = new TextField("Sl.No");
		// Textfield Device Make
		tfDMake = new TextField("Device Make");
		// Textfield Device Make
		tfDEviceMake = new TextField("Device Make");
		// Textfield Device Model
		tfDModel = new TextField("Device Model");
		// Statuscombo box for search
		cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Renewed date
		dfREnewedDate = new GERPPopupDateField("Renewed Date");
		dfREnewedDate.setInputPrompt("Select Date");
		// VAild date
		dfVaildDate = new GERPPopupDateField("Vaild Date");
		dfVaildDate.setInputPrompt("Select Date");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		resetSearchDetails();
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
		flColumn4 = new GERPFormLayout();
		flColumn1.addComponent(tfSlNoSear);
		flColumn2.addComponent(tfDEviceMake);
		flColumn3.addComponent(tfDModelSearch);
		flColumn4.addComponent(cbstatusSearch);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
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
		flColumn1.addComponent(tfDeviceCode);
		flColumn1.addComponent(tfSlNo);
		flColumn2.addComponent(tfDMake);
		flColumn2.addComponent(tfDModel);
		flColumn3.addComponent(dfREnewedDate);
		flColumn3.addComponent(dfVaildDate);
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
			CompanyDevicesDM enqdtl = beanCompanyDeviceDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			setReadOnlyFalseFields();
			// cbProduct.setReadOnly(false);
			tfDeviceCode.setValue(enqdtl.getDeviceCode());
			tfSlNo.setValue(enqdtl.getDeviceSlno());
			tfDMake.setValue(enqdtl.getDeviceMake());
			tfDModel.setValue(enqdtl.getDevicemodel());
			dfREnewedDate.setValue(enqdtl.getLastRenewedv());
			dfVaildDate.setValue(enqdtl.getValidUntilv());
			cbstatus.setValue(enqdtl.getStatus());
			setReadOnlyTrueFields();
		}
	}
	
	public void setReadOnlyFalseFields() {
		tfDeviceCode.setReadOnly(false);
		tfSlNo.setReadOnly(false);
		tfDMake.setReadOnly(false);
		tfDModel.setReadOnly(false);
		dfREnewedDate.setReadOnly(false);
		dfVaildDate.setReadOnly(false);
		cbstatus.setReadOnly(false);
	}
	
	public void setReadOnlyTrueFields() {
		tfDeviceCode.setReadOnly(true);
		tfSlNo.setReadOnly(true);
		tfDMake.setReadOnly(true);
		tfDModel.setReadOnly(true);
		dfREnewedDate.setReadOnly(true);
		dfVaildDate.setReadOnly(true);
		cbstatus.setReadOnly(true);
	}
	
	// get the search result from DB based on the search Company Devices
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.removeAllItems();
		List<CompanyDevicesDM> companyDeviceList = new ArrayList<CompanyDevicesDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search CompanyDevice are "
				+ companyid + ", " + tfSlNoSear.getValue() + ", " + tfDEviceMake.getValue());
		
		String devmk=tfDEviceMake.getValue().toString();
		String devmdl=tfDModelSearch.getValue().toString();
		companyDeviceList = serviceCompanyDevice.getCompanyDevicesList(companyid, tfSlNoSear.getValue(),
				devmk, devmdl, null,(String) cbstatusSearch.getValue(),"F");
		recordCnt = companyDeviceList.size();
		beanCompanyDeviceDM = new BeanItemContainer<CompanyDevicesDM>(CompanyDevicesDM.class);
		beanCompanyDeviceDM.addAll(companyDeviceList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the CompanyDevice. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanCompanyDeviceDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "deviceCode", "deviceSlno", "deviceMake", "devicemodel",
				"lastRenewed", "validUntil", "status", "lastUpdateddate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Device Code", "Device Sl.No.", "Device Make",
				"Device Model", "Renewed Date", "Valid Date", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("deviceCode", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
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
		tfSlNoSear.setValue("");
		tfDEviceMake.setValue("");
		tfDModelSearch.setValue("");
		cbstatusSearch.setValue(cbstatusSearch.getItemIds().iterator().next());
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
		tfSlNo.setValue("");
		tfDeviceCode.setValue("");
		dfREnewedDate.setValue(null);
		dfVaildDate.setValue(null);
		tfDMake.setValue("");
		tfDModel.setValue("");
		cbstatus.setValue(null);
		setReadOnlyTrueFields();
	}
}
