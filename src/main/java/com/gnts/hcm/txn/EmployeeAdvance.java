/**
 * File Name 		: EmployeeAdvance.java 
 * Description 		: this class is used for add/edit EmployeeAdvance details. 
 * Author 			: Madhu T
 * Date 			: sep-25-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks 
 * 0.1          sep-25-2014       	Madhu T	        Initial Version
 **/
package com.gnts.hcm.txn;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.DeductionDM;
import com.gnts.hcm.domain.txn.EmployeeAdvanceDM;
import com.gnts.hcm.service.mst.DeductionService;
import com.gnts.hcm.service.txn.EmployeeAdvanceService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Table.Align;

public class EmployeeAdvance extends BaseUI {
	// Bean creation
	private EmployeeAdvanceService serviceEmpAdvance = (EmployeeAdvanceService) SpringContextHelper
			.getBean("EmployeeAdvanceService");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private DeductionService serviceDeduction = (DeductionService) SpringContextHelper.getBean("Deduction");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfAdvncAmt, tfAdvncInterest, tfNoOfRepay, tfEMIAmt;
	private TextArea tfAdvanceReason;
	private ComboBox cbStatus, cbEmpName, cbDeductionName, cbAprveMngr;
	private DateField dfEffective, dfDeductnStDt;
	// BeanItemContainer
	private BeanItemContainer<EmployeeAdvanceDM> beanAdvanceDM = null;
	private BeanContainer<Long, EmployeeDM> beanEmpDM = null;
	private BeanContainer<Long, EmployeeDM> beanEmployeeDM = null;
	private BeanContainer<Long, DeductionDM> beanDeductnDM = null;
	// local variables declaration
	private Long companyid;
	private String pkEmpAdvncId;
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeAdvance.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmployeeAdvance() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeAdvance() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting EmployeeAdvance UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("150px");
		// Advance Reason Textfield
		tfAdvanceReason = new GERPTextArea("Advance Reason");
		tfAdvanceReason.setNullRepresentation("");
		tfAdvanceReason.setHeight("50px");
		// Approve Manager ComboBox
		cbAprveMngr = new GERPComboBox("Approve Manager");
		cbAprveMngr.setWidth("150px");
		cbAprveMngr.setItemCaptionPropertyId("firstname");
		loadEmpAprvrList();
		// No Of Repayment text field
		tfNoOfRepay = new GERPTextField("No of Repayment");
		// EMIAmt text field
		tfEMIAmt = new GERPTextField("EMI Amount");
		// Employee Name Combo Box
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setWidth("140px");
		cbEmpName.setItemCaptionPropertyId("fullname");
		loadEmpList();
		// Deduction Name ComboBox
		cbDeductionName = new GERPComboBox("Deduction Name");
		cbDeductionName.setWidth("140px");
		cbDeductionName.setItemCaptionPropertyId("deducnDesc");
		loadDeductionList();
		// Advance Amount Textfield
		tfAdvncAmt = new GERPTextField("Advance Amount");
		tfAdvncAmt.setValue("0");
		// Effective Date field
		dfEffective = new GERPPopupDateField("Effective Date");
		// Deduction Start date field
		dfDeductnStDt = new GERPPopupDateField("Deduction Start Date");
		dfDeductnStDt.setWidth("130px");
		// Advance Interest TextField
		tfAdvncInterest = new GERPTextField("Advance Interest");
		tfAdvncInterest.setValue("0");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(cbEmpName);
		flColumn2.addComponent(cbDeductionName);
		flColumn3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		Label lbl = new Label();
		flColumn1.addComponent(cbEmpName);
		flColumn1.addComponent(cbDeductionName);
		flColumn1.addComponent(dfEffective);
		flColumn2.addComponent(tfAdvncAmt);
		flColumn2.addComponent(tfAdvncInterest);
		flColumn2.addComponent(cbAprveMngr);
		flColumn3.addComponent(tfAdvanceReason);
		flColumn3.addComponent(tfNoOfRepay);
		flColumn4.addComponent(tfEMIAmt);
		flColumn4.addComponent(dfDeductnStDt);
		flColumn4.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(lbl);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<EmployeeAdvanceDM> EmployeeAdvanceList = new ArrayList<EmployeeAdvanceDM>();
		Long empId = null;
		if (cbEmpName.getValue() != null) {
			empId = ((Long.valueOf(cbEmpName.getValue().toString())));
		}
		Long deductionId = null;
		if (cbDeductionName.getValue() != null) {
			deductionId = ((Long.valueOf(cbDeductionName.getValue().toString())));
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfAdvncAmt.getValue() + ", " + dfEffective.getValue()
				+ (String) cbStatus.getValue() + ", " + empId + "," + deductionId);
		EmployeeAdvanceList = serviceEmpAdvance.getempadvancelist(null, empId, deductionId, null, null,
				(String) cbStatus.getValue(), "F");
		recordCnt = EmployeeAdvanceList.size();
		beanAdvanceDM = new BeanItemContainer<EmployeeAdvanceDM>(EmployeeAdvanceDM.class);
		beanAdvanceDM.addAll(EmployeeAdvanceList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the EmployeeAdvance. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanAdvanceDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "empadvanceid", "empName", "deductnName", "advnceamt",
				"aprvMngr", "advstatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Deduction Name",
				"Advance Amount", "Approve Manager", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("empadvanceid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbDeductionName.setValue(null);
		cbEmpName.setValue(null);
		cbAprveMngr.setValue(null);
		tfAdvanceReason.setValue("");
		tfAdvncAmt.setValue("0");
		tfAdvncInterest.setValue("0");
		dfEffective.setValue(new Date());
		tfNoOfRepay.setValue("0");
		tfEMIAmt.setValue("0");
		dfDeductnStDt.setValue(null);
		cbEmpName.setComponentError(null);
		cbDeductionName.setComponentError(null);
		cbAprveMngr.setComponentError(null);
		dfEffective.setComponentError(null);
		tfAdvncInterest.setComponentError(null);
		dfDeductnStDt.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editEmpAdvance() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			EmployeeAdvanceDM editEmpAdvance = beanAdvanceDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			pkEmpAdvncId = editEmpAdvance.getEmpadvanceid().toString();
			cbEmpName.setValue(editEmpAdvance.getEmpid());
			cbAprveMngr.setValue(editEmpAdvance.getApprovemgr());
			cbDeductionName.setValue(editEmpAdvance.getDednid());
			dfEffective.setValue((Date) itselect.getItemProperty("advncedt").getValue());
			tfAdvncAmt.setValue(itselect.getItemProperty("advnceamt").getValue().toString());
			tfAdvncInterest.setValue(itselect.getItemProperty("advnceiterst").getValue().toString());
			tfNoOfRepay.setValue(itselect.getItemProperty("noofpayment").getValue().toString());
			tfEMIAmt.setValue(itselect.getItemProperty("emiamount").getValue().toString());
			tfAdvanceReason.setValue((String) itselect.getItemProperty("advanceReason").getValue());
			dfDeductnStDt.setValue((Date) itselect.getItemProperty("dednstartdt").getValue());
			cbStatus.setValue(itselect.getItemProperty("advstatus").getValue());
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbDeductionName.setValue(null);
		cbEmpName.setValue(null);
		// reset the field valued to default
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		resetFields();
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbDeductionName.setRequired(true);
		cbEmpName.setRequired(true);
		cbAprveMngr.setRequired(true);
		tfAdvanceReason.setEnabled(true);
		cbAprveMngr.setEnabled(true);
		tfAdvncAmt.setEnabled(true);
		dfEffective.setEnabled(true);
		tfAdvncInterest.setEnabled(true);
		dfDeductnStDt.setRequired(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for EmployeeAdvance. ID " + pkEmpAdvncId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_EMPLOYEE_ADVANCE);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkEmpAdvncId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbEmpName.setComponentError(null);
		cbDeductionName.setComponentError(null);
		cbAprveMngr.setComponentError(null);
		cbDeductionName.setRequired(false);
		cbEmpName.setRequired(false);
		dfDeductnStDt.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		cbDeductionName.setRequired(true);
		cbEmpName.setRequired(true);
		cbAprveMngr.setRequired(true);
		dfDeductnStDt.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editEmpAdvance();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbEmpName.setComponentError(null);
		cbDeductionName.setComponentError(null);
		cbAprveMngr.setComponentError(null);
		tfAdvncInterest.setComponentError(null);
		tfAdvncAmt.setComponentError(null);
		tfEMIAmt.setComponentError(null);
		dfDeductnStDt.setComponentError(null);
		errorFlag = false;
		if (cbEmpName.getValue() == null) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbEmpName.getValue());
			errorFlag = true;
		}
		if (cbDeductionName.getValue() == null) {
			cbDeductionName.setComponentError(new UserError(GERPErrorCodes.NULL_DEDCTION_NAME));
			errorFlag = true;
		}
		if (cbAprveMngr.getValue() == null) {
			cbAprveMngr.setComponentError(new UserError(GERPErrorCodes.NULL_APPROVE_MANAGER));
			errorFlag = true;
		}
		if (dfDeductnStDt.getValue() == null) {
			dfDeductnStDt.setComponentError(new UserError("Deduction Start Date"));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			EmployeeAdvanceDM empAdvncObj = new EmployeeAdvanceDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				empAdvncObj = beanAdvanceDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (cbEmpName.getValue() != null) {
				empAdvncObj.setEmpid((Long.valueOf(cbEmpName.getValue().toString())));
			}
			if (cbDeductionName.getValue() != null) {
				empAdvncObj.setDednid((Long.valueOf(cbDeductionName.getValue().toString())));
			}
			if (dfEffective.getValue() != null) {
				empAdvncObj.setAdvncedt((dfEffective.getValue()));
			}
			if (tfAdvncAmt.getValue() != null && tfAdvncAmt.getValue().trim().length() > 0) {
				empAdvncObj.setAdvnceamt(Long.valueOf(tfAdvncAmt.getValue()));
			} else {
				empAdvncObj.setAdvnceamt(new Long("0"));
			}
			if (tfAdvncInterest.getValue() != null && tfAdvncInterest.getValue().trim().length() > 0) {
				empAdvncObj.setAdvnceiterst(Long.valueOf(tfAdvncInterest.getValue()));
			} else {
				empAdvncObj.setAdvnceiterst(new Long("0"));
			}
			if (cbAprveMngr.getValue() != null) {
				empAdvncObj.setApprovemgr((Long.valueOf(cbAprveMngr.getValue().toString())));
			}
			if (tfAdvanceReason.getValue() != null) {
				empAdvncObj.setAdvanceReason(tfAdvanceReason.getValue());
			}
			if (tfNoOfRepay.getValue() != null) {
				empAdvncObj.setNoofpayment(Long.valueOf((tfNoOfRepay.getValue())));
			}
			if (tfEMIAmt.getValue() != null) {
				empAdvncObj.setEmiamount((Long.valueOf((tfEMIAmt.getValue()))));
			}
			if (dfDeductnStDt.getValue() != null) {
				empAdvncObj.setDednstartdt(dfDeductnStDt.getValue());
			}
			if (cbStatus.getValue() != null) {
				empAdvncObj.setAdvstatus((String) cbStatus.getValue());
			}
			empAdvncObj.setLastupdateddt(DateUtils.getcurrentdate());
			empAdvncObj.setLastupdatedby(username);
			serviceEmpAdvance.saveAndUpdate(empAdvncObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadEmpList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Employee Search...");
		List<EmployeeDM> empList = serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null,
				null, null, "F");
		beanEmpDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmpDM.setBeanIdProperty("employeeid");
		beanEmpDM.addAll(empList);
		cbEmpName.setContainerDataSource(beanEmpDM);
	}
	
	public void loadEmpAprvrList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Approver Search...");
		List<EmployeeDM> empList = serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null,
				null, null, "F");
		beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmployeeDM.setBeanIdProperty("employeeid");
		beanEmployeeDM.addAll(empList);
		cbAprveMngr.setContainerDataSource(beanEmployeeDM);
	}
	
	public void loadDeductionList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading deduction Search...");
		List<DeductionDM> alwncList = serviceDeduction.getDuctionList(null, null, companyid, null,
				(String) cbStatus.getValue(), "F");
		beanDeductnDM = new BeanContainer<Long, DeductionDM>(DeductionDM.class);
		beanDeductnDM.setBeanIdProperty("deductionId");
		beanDeductnDM.addAll(alwncList);
		cbDeductionName.setContainerDataSource(beanDeductnDM);
	}
	
	private Date addDays(Date d, int days) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = sdf.format(d);
		Date parsedDate = null;
		try {
			parsedDate = sdf.parse(strDate);
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.warn("calculate days" + e);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, days);
		return now.getTime();
	}
}
