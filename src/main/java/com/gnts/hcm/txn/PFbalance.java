/**
 * File Name 		: PFbalance.java 
 * Description 		: this class is used for view pf balance  details. 
 * Author 			: Arun jeyaraj R 
 * Date 			: Sep 15,2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          Sep 15,2014        	Arun jeyaraj R		        Initial Version
 */
package com.gnts.hcm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
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
import com.gnts.hcm.domain.txn.PFbalanceDM;
import com.gnts.hcm.service.txn.PFbalanceService;
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

public class PFbalance extends BaseUI {
	private PFbalanceService servicepfbalance = (PFbalanceService) SpringContextHelper.getBean("pfbalance");
	private EmployeeService serviceemployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private FormLayout flfinyear, flempname;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tffinyear;
	private ComboBox cbempname;
	// Bean container
	private BeanItemContainer<PFbalanceDM> beanPFbalanceDM = null;
	// local variables declaration
	private Long companyid;
	private String userName;
	private int recordCnt = 0;
	// Initialize Logger
	private Logger logger = Logger.getLogger(PFbalance.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public PFbalance() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Inside PF Balance() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Painting PF Balance UI");
		// Employee Name Combo Box
		cbempname = new GERPComboBox("Employee Name");
		cbempname.setItemCaptionPropertyId("firstname");
		loadEmpList();
		// Financial Year text field
		tffinyear = new GERPTextField("Financial Year");
		tffinyear.setMaxLength(25);
		// create form layouts to hold the input items
		flempname = new FormLayout();
		flfinyear = new FormLayout();
		// add the user input items into appropriate form layout
		flempname.addComponent(cbempname);
		flfinyear.addComponent(tffinyear);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flempname);
		hlUserInputLayout.addComponent(flfinyear);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		tblMstScrSrchRslt.removeAllItems();
		resetFields();
		loadSrchRslt();
		tblMstScrSrchRslt.setVisible(true);
		tblMstScrSrchRslt.setReadOnly(true);
		btnAdd.setEnabled(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(true);
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<PFbalanceDM> PFbalanceList = new ArrayList<PFbalanceDM>();
		Long employeeid = null;
		if (cbempname.getValue() != null) {
			employeeid = ((Long) cbempname.getValue());
		}
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are "
				+ tffinyear.getValue());
		recordCnt = 0;
		if (cbempname.getValue() != null || tffinyear.getValue().trim().length() > 0) {
			PFbalanceList = servicepfbalance.getPfBalanceList(null, null, employeeid, null, null, tffinyear.getValue());
			recordCnt = PFbalanceList.size();
		}
		beanPFbalanceDM = new BeanItemContainer<PFbalanceDM>(PFbalanceDM.class);
		beanPFbalanceDM.addAll(PFbalanceList);
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Got the PF Balance result set");
		tblMstScrSrchRslt.setContainerDataSource(beanPFbalanceDM);
		tblMstScrSrchRslt.setColumnAlignment("pd_id", Align.RIGHT);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "pfid", "firstname", "pfdate", "employeecontrib",
				"employercontrib", "pfopening", "pfwithdrawal", "pfclosing", "islatest" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "PF Date",
				"Employee Contribution", "Employer Contribution", "PF Opening", "PF Withdrawal", "PF Closing",
				"Is Latest?" });
		tblMstScrSrchRslt.setColumnFooter("islatest", "No.of Records : " + recordCnt);
	}
	
	// load employee names
	private void loadEmpList() {
		BeanContainer<Long, EmployeeDM> beanLoadEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanLoadEmployee.setBeanIdProperty("employeeid");
		beanLoadEmployee.addAll(serviceemployee.getEmployeeList(null, null, null, null, companyid, null, null,
				null, null, "P"));
		cbempname.setContainerDataSource(beanLoadEmployee);
	}
	
	// Base class implementations
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Resetting the UI controls");
		tblMstScrSrchRslt.setVisible(true);
		loadSrchRslt();
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + " Invoking search");
		if (cbempname.getValue() == null && tffinyear.getValue() == "") {
			throw new ERPException.NoDataFoundException();
		} else {
			loadSrchRslt();
			tblMstScrSrchRslt.setReadOnly(true);
			tblMstScrSrchRslt.setVisible(true);
		}
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tffinyear.setValue("");
		cbempname.setValue(null);
		tblMstScrSrchRslt.setVisible(false);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		tblMstScrSrchRslt.setVisible(true);
		tblMstScrSrchRslt.setReadOnly(true);
		resetFields();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Validating Data ");
		tffinyear.setComponentError(null);
		Boolean errorFlag = false;
		if ((tffinyear.getValue() == null) || tffinyear.getValue().trim().length() == 0) {
			tffinyear.setComponentError(new UserError(GERPErrorCodes.NULL_FIN_YEAR));
			errorFlag = true;
		}
		if (cbempname.getValue() == null) {
			cbempname.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_FIRST_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void cancelDetails() {
	}
	
	@Override
	protected void addDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void editDetails() {
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
}
