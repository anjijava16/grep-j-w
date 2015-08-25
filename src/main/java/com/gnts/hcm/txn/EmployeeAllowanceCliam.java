/**
 * File Name 		: EmpAllowanceCliam.java 
 * Description 		: this class is used for add/edit EmpAllowanceCliam  details. 
 * Author 			: Madhu T
 * Date 			: sep-25-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version      	 Date           	Modified By               Remarks
 * 0.1          sep-25-2014     	  Madhu T	        Initial Version
 **/
package com.gnts.hcm.txn;

import java.util.ArrayList;
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
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.EmployeeAllowanceClaimDM;
import com.gnts.hcm.domain.txn.EmployeeAllowanceDM;
import com.gnts.hcm.service.txn.EmployeeAllowanceClaimService;
import com.gnts.hcm.service.txn.EmployeeAllowanceService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class EmployeeAllowanceCliam extends BaseUI {
	// Bean creation
	private EmployeeAllowanceClaimService serviceEmpAllowanceCliam = (EmployeeAllowanceClaimService) SpringContextHelper
			.getBean("EmployeeAllowanceClaim");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private EmployeeAllowanceService serviceEmpAllowance = (EmployeeAllowanceService) SpringContextHelper
			.getBean("EmployeeAllowance");
	private Label lbl = new Label();
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfPaidPayrollId, tfAlwncClaimAmt, tfClaimRemark;
	private ComboBox cbStatus, cbEmpName, cbEmpAlwncName;
	private DateField dtPaidDt, dfAlowncClaimDt;
	// BeanItemContainer
	private BeanItemContainer<EmployeeAllowanceClaimDM> beanEmpAllowanceDM = null;
	// local variables declaration
	private Long companyid;
	private String pkEmpAlwncClaim;
	private int recordCnt = 0;
	private String username;
	private Long gradeId;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeAllowanceCliam.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmployeeAllowanceCliam() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmpAllowanceCliam() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting EmpAllowanceCliam UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.T_HCM_PAYROLL_DTL, BASEConstants.PAYROLL_STATUS);
		cbStatus.setWidth("150px");
		// Claim Amount TextField
		tfAlwncClaimAmt = new GERPTextField("Claim Amount");
		tfAlwncClaimAmt.setValue("0");
		// Employee Name Combo Box
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setItemCaptionPropertyId("fullname");
		cbEmpName.setWidth("150px");
		loadEmpList();
		// Employee Allowance ComboBox
		cbEmpAlwncName = new GERPComboBox("Employee Allowance");
		cbEmpAlwncName.setWidth("150px");
		cbEmpAlwncName.setItemCaptionPropertyId("allownceDesc");
		loadAlwncList();
		// Allowance claim Date Field
		dfAlowncClaimDt = new GERPPopupDateField("Claim Date");
		dfAlowncClaimDt.setWidth("130px");
		// Paid date Field
		dtPaidDt = new GERPPopupDateField("Paid Date");
		dtPaidDt.setWidth("130px");
		// Cliam Remark TextField
		tfClaimRemark = new GERPTextField("Claim Remark");
		// Paid Payroll TextField
		tfPaidPayrollId = new GERPTextField("Paid Payroll");
		tfPaidPayrollId.setValue("0");
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
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbEmpName);
		flColumn2.addComponent(cbEmpAlwncName);
		flColumn3.addComponent(dfAlowncClaimDt);
		flColumn4.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(lbl);
		hlSearchLayout.addComponent(flColumn4);
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
		flColumn1.addComponent(cbEmpName);
		flColumn1.addComponent(cbEmpAlwncName);
		flColumn2.addComponent(dfAlowncClaimDt);
		flColumn2.addComponent(tfAlwncClaimAmt);
		flColumn3.addComponent(dtPaidDt);
		flColumn3.addComponent(tfPaidPayrollId);
		flColumn4.addComponent(tfClaimRemark);
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
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<EmployeeAllowanceClaimDM> listAllClaim = new ArrayList<EmployeeAllowanceClaimDM>();
		Long empId = null;
		if (cbEmpName.getValue() != null) {
			empId = ((Long.valueOf(cbEmpName.getValue().toString())));
		}
		Long alwncId = null;
		if (cbEmpAlwncName.getValue() != null) {
			alwncId = ((Long.valueOf(cbEmpAlwncName.getValue().toString())));
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + dtPaidDt.getValue() + (String) cbStatus.getValue() + ", " + empId + "," + alwncId);
		listAllClaim = serviceEmpAllowanceCliam.getempawclaimlist(null, (Long) cbEmpName.getValue(),
				(Date) dfAlowncClaimDt.getValue(), (Long) cbEmpAlwncName.getValue(), "Approved", "F");
		recordCnt = listAllClaim.size();
		beanEmpAllowanceDM = new BeanItemContainer<EmployeeAllowanceClaimDM>(EmployeeAllowanceClaimDM.class);
		beanEmpAllowanceDM.addAll(listAllClaim);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the EmpAllowanceCliam. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanEmpAllowanceDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "allwclaimid", "empName", "alwanceName", "awclmdt",
				"awclmamt", "allwclmstatus", "lastupdt", "lastupby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Employee Allowance",
				"Claim Date", "Claim Amount", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("allwclaimid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupby", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		dfAlowncClaimDt.setValue(null);
		cbEmpName.setValue(null);
		cbEmpAlwncName.setValue(null);
		tfAlwncClaimAmt.setValue("0");
		tfPaidPayrollId.setValue("0");
		dtPaidDt.setValue(null);
		tfClaimRemark.setValue("");
		cbEmpName.setComponentError(null);
		dfAlowncClaimDt.setComponentError(null);
		dtPaidDt.setComponentError(null);
		tfPaidPayrollId.setComponentError(null);
		dtPaidDt.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editEmpAlownce() {
		if (tblMstScrSrchRslt.getValue() != null) {
			EmployeeAllowanceClaimDM editEmpAlownce = beanEmpAllowanceDM.getItem(tblMstScrSrchRslt.getValue())
					.getBean();
			pkEmpAlwncClaim = editEmpAlownce.getEmpawid().toString();
			cbEmpName.setValue(editEmpAlownce.getEmpid());
			if (editEmpAlownce.getAwclmdt() != null) {
				dfAlowncClaimDt.setValue(editEmpAlownce.getAwclmdt1());
			}
			dtPaidDt.setValue(editEmpAlownce.getPaiddt());
			cbEmpAlwncName.setValue(editEmpAlownce.getEmpawid());
			tfAlwncClaimAmt.setValue(editEmpAlownce.getAwclmamt().toString());
			if (editEmpAlownce.getPaidpayrollid() != null) {
				tfPaidPayrollId.setValue(editEmpAlownce.getPaidpayrollid().toString());
			}
			cbStatus.setValue(editEmpAlownce.getAllwclmstatus());
			if (editEmpAlownce.getClaimremarks() != null) {
				tfClaimRemark.setValue(editEmpAlownce.getClaimremarks());
			}
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
		dfAlowncClaimDt.setValue(null);
		cbEmpName.setValue(null);
		cbEmpAlwncName.setValue(null);
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
		dfAlowncClaimDt.setRequired(true);
		cbEmpName.setRequired(true);
		cbEmpAlwncName.setRequired(true);
		tfAlwncClaimAmt.setEnabled(true);
		cbEmpAlwncName.setEnabled(true);
		dtPaidDt.setEnabled(true);
		dtPaidDt.setRequired(true);
		tfClaimRemark.setEnabled(true);
		tfPaidPayrollId.setEnabled(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for EmpAllowanceClaim. ID " + pkEmpAlwncClaim);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_EMPLOYEE_ALLOWANCE_CLAIM);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkEmpAlwncClaim);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbEmpName.setComponentError(null);
		cbEmpAlwncName.setComponentError(null);
		dfAlowncClaimDt.setComponentError(null);
		dfAlowncClaimDt.setRequired(false);
		cbEmpName.setRequired(false);
		cbEmpAlwncName.setRequired(false);
		dtPaidDt.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		dfAlowncClaimDt.setRequired(true);
		cbEmpName.setRequired(true);
		cbEmpAlwncName.setRequired(true);
		dtPaidDt.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editEmpAlownce();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbEmpName.setComponentError(null);
		dfAlowncClaimDt.setComponentError(null);
		cbEmpAlwncName.setComponentError(null);
		dtPaidDt.setComponentError(null);
		errorFlag = false;
		if (cbEmpName.getValue() == null) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbEmpName.getValue());
			errorFlag = true;
		}
		if (dfAlowncClaimDt.getValue() == null) {
			dfAlowncClaimDt.setComponentError(new UserError(GERPErrorCodes.NULL_ALLOWANCE_CLAIM_DATE));
			errorFlag = true;
		}
		if (cbEmpAlwncName.getValue() == null) {
			cbEmpAlwncName.setComponentError(new UserError(GERPErrorCodes.NULL_ALWNCE_NAME));
			errorFlag = true;
		}
		if (dtPaidDt.getValue() == null) {
			dtPaidDt.setComponentError(new UserError(GERPErrorCodes.NULL_PAID_DATE));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		EmployeeAllowanceClaimDM empAllowncObj = new EmployeeAllowanceClaimDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			empAllowncObj = beanEmpAllowanceDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if (dfAlowncClaimDt.getValue() != null) {
			empAllowncObj.setAwclmdt((dfAlowncClaimDt.getValue()));
		}
		if (dtPaidDt.getValue() != null) {
			empAllowncObj.setPaiddt((dtPaidDt.getValue()));
		}
		if (tfClaimRemark.getValue() != null) {
			empAllowncObj.setClaimremarks(tfClaimRemark.getValue());
		}
		if (tfPaidPayrollId.getValue() != null && tfPaidPayrollId.getValue().trim().length() > 0) {
			empAllowncObj.setPaidpayrollid(Long.valueOf(tfPaidPayrollId.getValue()));
		} else {
			empAllowncObj.setPaidpayrollid(new Long("0"));
		}
		if (cbStatus.getValue() != null) {
			empAllowncObj.setAllwclmstatus((String) cbStatus.getValue());
		}
		if (cbEmpAlwncName.getValue() != null) {
			empAllowncObj.setEmpawid((Long.valueOf(cbEmpAlwncName.getValue().toString())));
		}
		if (tfAlwncClaimAmt.getValue() != null) {
			empAllowncObj.setAwclmamt((Long.valueOf(tfAlwncClaimAmt.getValue().toString())));
		}
		if (cbEmpName.getValue() != null) {
			empAllowncObj.setEmpid((Long.valueOf(cbEmpName.getValue().toString())));
		}
		if (dfAlowncClaimDt.getValue() != null) {
			empAllowncObj.setAwclmdt(dfAlowncClaimDt.getValue());
		}
		empAllowncObj.setLastupdt(DateUtils.getcurrentdate());
		empAllowncObj.setLastupby(username);
		serviceEmpAllowanceCliam.saveAndUpdate(empAllowncObj);
		resetFields();
		loadSrchRslt();
	}
	
	private void loadEmpList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Employee Search...");
			BeanContainer<Long, EmployeeDM> beanEmpDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmpDM.setBeanIdProperty("employeeid");
			beanEmpDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null, null,
					null, "P"));
			cbEmpName.setContainerDataSource(beanEmpDM);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadAlwncList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Allowance Search...");
			BeanContainer<Long, EmployeeAllowanceDM> beanAlwncDM = new BeanContainer<Long, EmployeeAllowanceDM>(
					EmployeeAllowanceDM.class);
			beanAlwncDM.setBeanIdProperty("empallwnid");
			beanAlwncDM.addAll(serviceEmpAllowance.getempallowanceList(null, (Long) cbEmpName.getValue(), gradeId,
					"Active", "F"));
			cbEmpAlwncName.setContainerDataSource(beanAlwncDM);
		}
		catch (Exception e) {
		}
	}
}
