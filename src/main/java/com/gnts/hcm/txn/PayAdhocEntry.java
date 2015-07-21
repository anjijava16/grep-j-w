/**
 * File Name 		: PayAdhocEntry.java 
 * Description 		: This UI screen  is used for Add PayAdhocEntry Details. 
 * Author 			: GOKUL M
 * Date 			: Oct 11, 2014
 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * Version       Date           	Modified By               Remarks
 *  0.1       Oct 11 2014             GOKUL M	           Initial Version
 **/
package com.gnts.hcm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.PayAdhocEntryDM;
import com.gnts.hcm.service.txn.PayAdhocEntryService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.UI;

public class PayAdhocEntry extends BaseUI {
	private Logger logger = Logger.getLogger(PayAdhocEntry.class);
	private static final long serialVersionUID = 1L;
	private PayAdhocEntryService servicepayadhocentry = (PayAdhocEntryService) SpringContextHelper.getBean("PayAdhoc");
	private EmployeeService serviceemployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private HorizontalLayout hluserInputlayout = new HorizontalLayout();
	// Search Horizontal Layout
	private HorizontalLayout hlsearchlayout = new HorizontalLayout();
	// Add Input fields
	private ComboBox cbEmployeeName, cbStatus;
	private CheckBox chkentry;
	private GERPTextField tfPayAmount;
	private PopupDateField pdfPayDate;
	private GERPTextArea taRemarks;
	// To add Bean Item Container bean
	private BeanItemContainer<PayAdhocEntryDM> beanPayAdhocEntryDM = null;
	private String username;
	private Long companyid, employeeid;
	private int recordCnt;
	private String empname;
	private FormLayout flColumn1, flColumn2, flColumn3, flcolumn4;
	
	// Initialize logger
	public PayAdhocEntry() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PayAdhocEntry constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting PayAdhoc Entry UI");
		cbEmployeeName = new GERPComboBox("Employee Name");
		cbEmployeeName.setItemCaptionPropertyId("firstlastname");
		loadEmployeeList();
		chkentry = new CheckBox();
		chkentry.setCaption("Entry Type");
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		tfPayAmount = new GERPTextField("Pay Amount");
		taRemarks = new GERPTextArea("Remark");
		pdfPayDate = new GERPPopupDateField("Pay Date");
		pdfPayDate.setWidth("90");
		hlsearchlayout = new GERPAddEditHLayout();
		assemblesearchdetail();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assemblesearchdetail() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search");
		hlsearchlayout.removeAllComponents();
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn1.addComponent(cbEmployeeName);
		flColumn2.addComponent(pdfPayDate);
		flColumn2.setSpacing(true);
		flColumn2.setMargin(true);
		flColumn3.addComponent(cbStatus);
		flColumn3.setSpacing(true);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		hlsearchlayout.addComponent(flColumn1);
		hlsearchlayout.addComponent(flColumn2);
		hlsearchlayout.setSpacing(true);
		hlsearchlayout.addComponent(flColumn3);
		hlsearchlayout.setSizeUndefined();
		hlsearchlayout.setSpacing(true);
		hlsearchlayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		hluserInputlayout.removeAllComponents();
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flcolumn4 = new GERPFormLayout();
		flColumn1.addComponent(cbEmployeeName);
		cbEmployeeName.setRequired(true);
		flColumn1.addComponent(tfPayAmount);
		flColumn2.addComponent(pdfPayDate);
		pdfPayDate.setWidth("90");
		flColumn2.addComponent(cbStatus);
		flColumn3.addComponent(taRemarks);
		taRemarks.setValue("");
		taRemarks.setHeight("50");
		flcolumn4.addComponent(chkentry);
		hluserInputlayout.addComponent(flColumn1);
		hluserInputlayout.addComponent(flColumn2);
		hluserInputlayout.addComponent(flColumn3);
		hluserInputlayout.addComponent(flcolumn4);
		hluserInputlayout.setSpacing(true);
		hluserInputlayout.setMargin(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search result...");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.removeAllItems();
		Long empid = null;
		if (cbEmployeeName.getValue() != null) {
			empid = ((Long) cbEmployeeName.getValue());
		}
		List<PayAdhocEntryDM> listPayAdhocEntry = new ArrayList<PayAdhocEntryDM>();
		Date payrolldt = (Date) pdfPayDate.getValue();
		listPayAdhocEntry = servicepayadhocentry.getPayAdhocEntry(null, empid, payrolldt, (String) cbStatus.getValue(),
				"F");
		recordCnt = listPayAdhocEntry.size();
		beanPayAdhocEntryDM = new BeanItemContainer<PayAdhocEntryDM>(PayAdhocEntryDM.class);
		beanPayAdhocEntryDM.addAll(listPayAdhocEntry);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the PayAdhocEntry result set");
		tblMstScrSrchRslt.setContainerDataSource(beanPayAdhocEntryDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "entIdry", "empfirstlast", "payrollDt", "paytollAmt",
				"status", "lastUpdatedDt", "lastUpdatedBy" });
		logger.info("Company ID : " + companyid + " | User Name : " + username + " >>>>>>>>>>>>>>>>> "
				+ "Loading Search...");
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Payroll Date", "Payroll Amount",
				"Status", "LastUpdated Date", "LastUpdated By" });
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading EmployeeList");
		BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmployee.setBeanIdProperty("employeeid");
		beanEmployee.addAll(serviceemployee.getEmployeeList(empname, null, null, "Active", companyid, employeeid, null,
				null, null, "P"));
		cbEmployeeName.setContainerDataSource(beanEmployee);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Searching detail...");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			assemblesearchdetail();
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data.....");
		cbEmployeeName.setComponentError(null);
		Boolean errorFlag = false;
		if (cbEmployeeName.getValue() == null) {
			cbEmployeeName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data.....");
			PayAdhocEntryDM payadhocobj = new PayAdhocEntryDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				payadhocobj = beanPayAdhocEntryDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (cbEmployeeName.getValue() != null) {
				payadhocobj.setEmpid((Long) cbEmployeeName.getValue());
			}
			if (chkentry.getValue().equals(true)) {
				payadhocobj.setEntryType("Y");
			} else {
				payadhocobj.setEntryType("N");
			}
			if (Long.valueOf(tfPayAmount.getValue()) != null) {
				payadhocobj.setPaytollAmt(Long.valueOf(tfPayAmount.getValue()));
			}
			if (pdfPayDate.getValue() != null) {
				payadhocobj.setPayrollDt(pdfPayDate.getValue());
			}
			if (cbStatus.getValue() != null) {
				payadhocobj.setStatus(cbStatus.getValue().toString());
			}
			payadhocobj.setEntryRemarks(taRemarks.getValue());
			payadhocobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			payadhocobj.setLastUpdatedBy(username);
			servicepayadhocentry.saveAndUpdate(payadhocobj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Cancel Details...");
		cbEmployeeName.setRequired(false);
		assemblesearchdetail();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "resetfields...");
		cbEmployeeName.setValue(null);
		cbEmployeeName.setComponentError(null);
		chkentry.setValue(false);
		tfPayAmount.setValue("0");
		pdfPayDate.setValue(null);
		pdfPayDate.setComponentError(null);
		taRemarks.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	@Override
	protected void resetSearchDetails() {
		cbEmployeeName.setValue(null);
		pdfPayDate.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		assembleUserInputLayout();
		btnCancel.setVisible(true);
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing new record...");
		hlUserIPContainer.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		editpayadoc();
	}
	
	private void editpayadoc() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing PayAdhocEntry.......");
		tblMstScrSrchRslt.setVisible(true);
		hlCmdBtnLayout.setVisible(true);
		hluserInputlayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			PayAdhocEntryDM payadhocentry = beanPayAdhocEntryDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cbEmployeeName.setValue(payadhocentry.getEmpid());
			tfPayAmount.setValue(payadhocentry.getPaytollAmt().toString());
			cbStatus.setValue(payadhocentry.getStatus());
			if (payadhocentry.getEntryType().equals("Y")) {
				chkentry.setValue(true);
			} else {
				chkentry.setValue(false);
			}
			if (payadhocentry.getPayrollDt() != null) {
				pdfPayDate.setValue(payadhocentry.getPayrollDt1());
			}
			if ((payadhocentry.getEntryRemarks() != null)) {
				taRemarks.setValue(payadhocentry.getEntryRemarks());
			}
		}
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
}