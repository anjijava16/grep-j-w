/**
 * File Name 		: EmployeeLTAClaimDtl.java 
 * Description 		: this class is used for add/edit EmployeeLTAClaimDtl  details. 
 * Author 			: Madhu T
 * Date 			: Oct-7-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1          Oct-7-2014      	Madhu T	        Initial Version
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
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.AllowanceDM;
import com.gnts.hcm.domain.txn.EmpltaclaimdtlsDM;
import com.gnts.hcm.service.mst.AllowanceService;
import com.gnts.hcm.service.txn.EmpltaclaimdtlsService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Table.Align;

public class EmployeeLTAClaimDtl extends BaseUI {
	private static final long serialVersionUID = 1L;
	// Initialize Logger
	private Logger logger = Logger.getLogger(EmployeeLTAClaimDtl.class);
	private EmpltaclaimdtlsService EmployeeLTAClaimDtlervice = (EmpltaclaimdtlsService) SpringContextHelper
			.getBean("Empltaclaimdtls");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private AllowanceService serviceAllowance = (AllowanceService) SpringContextHelper.getBean("Allowance");
	// Bean container
	private BeanItemContainer<EmpltaclaimdtlsDM> beanEmpltaclaimdtlsDM = null;
	private BeanContainer<Long, EmployeeDM> beanEmpDM = null;
	private BeanContainer<Long, AllowanceDM> beanAlwncDM = null;
	private BeanContainer<Long, EmployeeDM> beanEmployeeDM = null;
	private TextField tfAlwncAmt, tfClaimAmt, tfAprvAmt, tfCurntBlkPeriod, tfClaimBlkPeriod, tfPaidPayroll;
	private PopupDateField dfClaimDt, dfPaidDt, dfAprvDt;
	private ComboBox cbEmpName, cbAllowanceName, cbApprovedBy, cbstatus, cbModeOfTravel;
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4;
	private HorizontalLayout hlsearchlayout;
	private HorizontalLayout hluserInputlayout = new HorizontalLayout();
	private String username;
	private Long companyid;
	private int recordCnt;
	public static boolean filevalue = false;
	private String pkEmpLtaClaimId;
	
	// Constructor
	public EmployeeLTAClaimDtl() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		// Loading the UI
		buidview();
	}
	
	public void buidview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "building EmployeeLTAClaimDtl UI");
		// Employee Name combobox
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setItemCaptionPropertyId("fullname");
		loadEmpList();
		// Allowance Name combobox
		cbAllowanceName = new GERPComboBox("Allowance Name");
		cbAllowanceName.setItemCaptionPropertyId("alowncDesc");
		loadAllowanceList();
		// Claim Date field
		dfClaimDt = new GERPPopupDateField("Claim Date");
		dfClaimDt.setWidth("110%");
		// Current Block Period text field
		tfCurntBlkPeriod = new GERPTextField("Current Block Period");
		// Claim Block Period text field
		tfClaimBlkPeriod = new GERPTextField("Claim Block Period");
		// Allowance Amount text field
		tfAlwncAmt = new GERPTextField("Allowance Amount");
		// Mode of travel combobox
		cbModeOfTravel = new GERPComboBox("Mode Of Travel");
		cbModeOfTravel.addItem("Roadways");
		cbModeOfTravel.addItem("Shipways");
		cbModeOfTravel.addItem("Airways");
		// claim amount text field
		tfClaimAmt = new GERPTextField("Claim Amount");
		// Paid Date field
		dfPaidDt = new GERPPopupDateField("Paid Date");
		// Paid payroll Id text field
		tfPaidPayroll = new GERPTextField("Paid Payroll Id");
		// Approved by combobox
		cbApprovedBy = new GERPComboBox("Approved By");
		cbApprovedBy.setItemCaptionPropertyId("firstname");
		loadApprovedByList();
		// Approve Date field
		dfAprvDt = new GERPPopupDateField("Approved Date");
		// Approve amount Textfield
		tfAprvAmt = new GERPTextField("Approved Amount");
		// Status combobox
		cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		hlsearchlayout = new GERPAddEditHLayout();
		assemblsearch();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
	}
	
	public void assemblsearch() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search");
		hlsearchlayout.removeAllComponents();
		flcolumn1 = new GERPFormLayout();
		flcolumn2 = new GERPFormLayout();
		flcolumn3 = new GERPFormLayout();
		flcolumn4 = new GERPFormLayout();
		flcolumn1.addComponent(cbEmpName);
		flcolumn2.addComponent(cbAllowanceName);
		flcolumn3.addComponent(cbstatus);
		hlsearchlayout.addComponent(flcolumn1);
		hlsearchlayout.addComponent(flcolumn2);
		hlsearchlayout.addComponent(flcolumn3);
		hlsearchlayout.setSizeUndefined();
		hlsearchlayout.setSpacing(true);
		hlsearchlayout.setMargin(true);
	}
	
	public void assemblUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		hluserInputlayout.removeAllComponents();
		flcolumn1 = new GERPFormLayout();
		flcolumn2 = new GERPFormLayout();
		flcolumn3 = new GERPFormLayout();
		flcolumn4 = new GERPFormLayout();
		flcolumn1.addComponent(cbEmpName);
		flcolumn1.addComponent(cbAllowanceName);
		flcolumn1.addComponent(dfClaimDt);
		flcolumn1.addComponent(tfCurntBlkPeriod);
		flcolumn2.addComponent(tfClaimBlkPeriod);
		flcolumn2.addComponent(tfAlwncAmt);
		flcolumn2.addComponent(cbModeOfTravel);
		flcolumn2.addComponent(tfClaimAmt);
		flcolumn3.addComponent(dfPaidDt);
		flcolumn3.addComponent(tfPaidPayroll);
		flcolumn3.addComponent(cbApprovedBy);
		flcolumn3.addComponent(dfAprvDt);
		flcolumn4.addComponent(tfAprvAmt);
		flcolumn4.addComponent(cbstatus);
		hluserInputlayout.addComponent(flcolumn1);
		hluserInputlayout.addComponent(flcolumn2);
		hluserInputlayout.addComponent(flcolumn3);
		hluserInputlayout.addComponent(flcolumn4);
		hluserInputlayout.setSpacing(true);
		hluserInputlayout.setMargin(true);
	}
	
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.removeAllItems();
		List<EmpltaclaimdtlsDM> claimDtlList = new ArrayList<EmpltaclaimdtlsDM>();
		Long empId = null;
		if (cbEmpName.getValue() != null) {
			empId = ((Long.valueOf(cbEmpName.getValue().toString())));
		}
		Long alwncId = null;
		if (cbAllowanceName.getValue() != null) {
			alwncId = ((Long.valueOf(cbAllowanceName.getValue().toString())));
		}
		claimDtlList = EmployeeLTAClaimDtlervice.getempltaclaimdtlsList(null, empId, alwncId, null, null, null,
				(String) cbstatus.getValue(), "F");
		recordCnt = claimDtlList.size();
		beanEmpltaclaimdtlsDM = new BeanItemContainer<EmpltaclaimdtlsDM>(EmpltaclaimdtlsDM.class);
		beanEmpltaclaimdtlsDM.addAll(claimDtlList);
		tblMstScrSrchRslt.setContainerDataSource(beanEmpltaclaimdtlsDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "empltaid", "empName", "alwncName", "claimblkperiod",
				"curblkperiod", "allwamt", "claimamt", "claimstatus", "lastupdtdate", "lastupdtby" });
		tblMstScrSrchRslt
				.setColumnHeaders(new String[] { "Ref.Id", "Emp.Name", "Allowance Name", "Claim Block Period",
						"Current Block Period", "Allowance Amt.", "Claim Amt.", "Status", "LastUpDated Date",
						"LastUpDated By" });
		tblMstScrSrchRslt.setColumnAlignment("empltaid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdtby", "No.of Records : " + recordCnt);
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
			assemblsearch();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbEmpName.setValue(null);
		cbAllowanceName.setValue(null);
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		assemblUserInputLayout();
		cbEmpName.setRequired(true);
		cbApprovedBy.setRequired(true);
		cbAllowanceName.setRequired(true);
		tfClaimAmt.setRequired(true);
		tfAlwncAmt.setRequired(true);
		tfAprvAmt.setRequired(true);
		btnCancel.setVisible(true);
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Updating existing record...");
		// remove the components in the search layout and input controls in the same container
		cbEmpName.setRequired(true);
		cbApprovedBy.setRequired(true);
		cbAllowanceName.setRequired(true);
		tfClaimAmt.setRequired(true);
		tfAlwncAmt.setRequired(true);
		tfAprvAmt.setRequired(true);
		hlUserIPContainer.removeAllComponents();
		assemblUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		editClaimDtls();
	}
	
	// Based on the selected record, the data would be included into user input fields in the input form
	private void editClaimDtls() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		tblMstScrSrchRslt.setVisible(true);
		hlCmdBtnLayout.setVisible(true);
		hluserInputlayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (sltedRcd != null) {
			EmpltaclaimdtlsDM editClaimDtlsList = beanEmpltaclaimdtlsDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			String stcode = sltedRcd.getItemProperty("claimstatus").getValue().toString();
			cbstatus.setValue(stcode);
			cbEmpName.setValue(editClaimDtlsList.getEmpid());
			cbAllowanceName.setValue(editClaimDtlsList.getAllowanceid());
			dfClaimDt.setValue((Date) sltedRcd.getItemProperty("claimdt").getValue());
			if (editClaimDtlsList.getCurblkperiod() != null) {
				tfCurntBlkPeriod.setValue(sltedRcd.getItemProperty("curblkperiod").getValue().toString());
			}
			if (editClaimDtlsList.getClaimblkperiod() != null) {
				tfClaimBlkPeriod.setValue(sltedRcd.getItemProperty("claimblkperiod").getValue().toString());
			}
			if (editClaimDtlsList.getAllwamt() != null) {
				tfAlwncAmt.setValue(sltedRcd.getItemProperty("allwamt").getValue().toString());
			}
			cbModeOfTravel.setValue(editClaimDtlsList.getModeoftravel());
			if (editClaimDtlsList.getClaimamt() != null) {
				tfClaimAmt.setValue(sltedRcd.getItemProperty("claimamt").getValue().toString());
			}
			if (editClaimDtlsList.getPaidpayrollid() != null) {
				tfPaidPayroll.setValue(sltedRcd.getItemProperty("paidpayrollid").getValue().toString());
			}
			cbstatus.setValue(sltedRcd.getItemProperty("claimstatus").getValue());
			cbApprovedBy.setValue(editClaimDtlsList.getApprby());
			dfPaidDt.setValue((Date) sltedRcd.getItemProperty("paiddt").getValue());
			dfAprvDt.setValue((Date) sltedRcd.getItemProperty("apprdt").getValue());
			if (editClaimDtlsList.getAppramt() != null) {
				tfAprvAmt.setValue(sltedRcd.getItemProperty("appramt").getValue().toString());
			}
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		boolean errorflag = false;
		cbEmpName.setComponentError(null);
		cbApprovedBy.setComponentError(null);
		cbAllowanceName.setComponentError(null);
		tfClaimAmt.setComponentError(null);
		tfAlwncAmt.setComponentError(null);
		tfAprvAmt.setComponentError(null);
		if (cbEmpName.getValue() == null) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			errorflag = true;
		}
		if (cbAllowanceName.getValue() == null) {
			cbAllowanceName.setComponentError(new UserError(GERPErrorCodes.NULL_ALLOWANCE_NAME));
			errorflag = true;
		}
		Long achievedQty1;
		try {
			achievedQty1 = Long.valueOf(tfClaimAmt.getValue());
			if (achievedQty1 < 0) {
				tfClaimAmt.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorflag = true;
			}
		}
		catch (Exception e) {
			if (Long.valueOf(tfClaimAmt.getValue()).equals("0")) {
				tfClaimAmt.setComponentError(new UserError(GERPErrorCodes.NULL_CLAIM_AMT));
				errorflag = true;
			} else {
				tfClaimAmt.setComponentError(new UserError(GERPErrorCodes.NULL_CLAIM_AMT));
				errorflag = true;
			}
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfAlwncAmt.getValue());
			if (achievedQty < 0) {
				tfAlwncAmt.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorflag = true;
			}
		}
		catch (Exception e) {
			if (Long.valueOf(tfAlwncAmt.getValue()).equals("0")) {
//				tfAlwncAmt.setComponentError(new UserError(GERPErrorCodes.NULL_ALLOW_AMT));
				tfAlwncAmt.setComponentError(new UserError("Please Enter Allowance Claim Amount"));
				errorflag = true;
			} else {
//				tfAlwncAmt.setComponentError(new UserError(GERPErrorCodes.Amount_CHAR_VALIDATION));
				tfAlwncAmt.setComponentError(new UserError("Please Enter Allowance Claim Amount"));
				errorflag = true;
			}
		}
		Long achievedQty2;
		try {
			achievedQty2 = Long.valueOf(tfAprvAmt.getValue());
			if (achievedQty2 < 0) {
				tfAprvAmt.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorflag = true;
			}
		}
		catch (Exception e) {
			if (Long.valueOf(tfAprvAmt.getValue()).equals("0")) {
				tfAprvAmt.setComponentError(new UserError(GERPErrorCodes.NULL_CLAIM_AMT));
				errorflag = true;
			} else {
//				tfAprvAmt.setComponentError(new UserError(GERPErrorCodes.Amount_CHAR_VALIDATION));
				tfAprvAmt.setComponentError(new UserError(GERPErrorCodes.NULL_CLAIM_AMT));
				errorflag = true;
			}
		}
		if (cbApprovedBy.getValue() == null) {
			cbApprovedBy.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			errorflag = true;
		}
		if ((dfPaidDt.getValue() != null) || (dfAprvDt.getValue() != null)) {
			if (dfPaidDt.getValue().after(dfAprvDt.getValue())) {
//				dfAprvDt.setComponentError(new UserError(GERPErrorCodes.LTA_DATE_OUTOFRANGE));
				dfAprvDt.setComponentError(new UserError("Approved Date Should be Lesser than Paid Date"));
				
				errorflag = true;
			} else {
				dfAprvDt.setComponentError(null);
			}
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + cbEmpName.getValue() + ",");
		if (errorflag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			EmpltaclaimdtlsDM empClaimDtlObj = new EmpltaclaimdtlsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				empClaimDtlObj = beanEmpltaclaimdtlsDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (cbEmpName.getValue() != null) {
				empClaimDtlObj.setEmpid(Long.valueOf(cbEmpName.getValue().toString()));
			}
			if (cbAllowanceName.getValue() != null) {
				empClaimDtlObj.setAllowanceid(Long.valueOf(cbAllowanceName.getValue().toString()));
			}
			if (dfClaimDt.getValue() != null) {
				empClaimDtlObj.setClaimdt(dfClaimDt.getValue());
			}
			if (tfCurntBlkPeriod.getValue() != null) {
				empClaimDtlObj.setCurblkperiod(tfCurntBlkPeriod.getValue());
			}
			if (tfClaimBlkPeriod.getValue() != null) {
				empClaimDtlObj.setClaimblkperiod(tfClaimBlkPeriod.getValue());
			}
			if (tfAlwncAmt.getValue() != null) {
				empClaimDtlObj.setAllwamt(Long.valueOf(tfAlwncAmt.getValue()));
			}
			if (cbModeOfTravel.getValue() != null) {
				empClaimDtlObj.setModeoftravel(cbModeOfTravel.getValue().toString());
			}
			if (tfClaimAmt.getValue() != null) {
				empClaimDtlObj.setClaimamt(Long.valueOf(tfClaimAmt.getValue()));
			}
			if (dfPaidDt.getValue() != null) {
				empClaimDtlObj.setPaiddt(dfPaidDt.getValue());
			}
			if (tfPaidPayroll.getValue() != null) {
				empClaimDtlObj.setPaidpayrollid(Long.valueOf(tfPaidPayroll.getValue().toString()));
			}
			if (cbstatus.getValue() != null) {
				empClaimDtlObj.setClaimstatus((String) cbstatus.getValue());
			}
			if (cbApprovedBy.getValue() != null) {
				empClaimDtlObj.setApprby(Long.valueOf(cbApprovedBy.getValue().toString()));
			}
			if (dfAprvDt.getValue() != null) {
				empClaimDtlObj.setApprdt(dfAprvDt.getValue());
			}
			if (tfAprvAmt.getValue() != null) {
				empClaimDtlObj.setAppramt(Long.valueOf(tfAprvAmt.getValue()));
			}
			empClaimDtlObj.setLastupdtdate(DateUtils.getcurrentdate());
			empClaimDtlObj.setLastupdtby(username);
			EmployeeLTAClaimDtlervice.saveAndUpdate(empClaimDtlObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for EmpLTAClaimDtl. ID " + pkEmpLtaClaimId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_EMP_CLAIM_DTLS);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkEmpLtaClaimId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assemblsearch();
		cbEmpName.setComponentError(null);
		cbAllowanceName.setComponentError(null);
		tfClaimAmt.setComponentError(null);
		tfAlwncAmt.setComponentError(null);
		tfAprvAmt.setComponentError(null);
		cbApprovedBy.setComponentError(null);
		cbAllowanceName.setRequired(false);
		cbEmpName.setRequired(false);
		tfClaimAmt.setRequired(false);
		tfAlwncAmt.setRequired(false);
		tfAprvAmt.setRequired(false);
		cbApprovedBy.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		cbAllowanceName.setValue(null);
		cbEmpName.setValue(null);
		dfClaimDt.setValue(new Date());
		tfCurntBlkPeriod.setValue("");
		tfClaimBlkPeriod.setValue("");
		tfAlwncAmt.setValue("0");
		tfAprvAmt.setValue("0");
		tfClaimAmt.setValue("0");
		dfPaidDt.setValue(null);
		tfPaidPayroll.setValue(null);
		cbModeOfTravel.setValue(null);
		cbApprovedBy.setValue(null);
		dfAprvDt.setValue(null);
		tfAprvAmt.setValue("0");
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		cbEmpName.setComponentError(null);
		cbAllowanceName.setComponentError(null);
		tfClaimAmt.setComponentError(null);
		tfAlwncAmt.setComponentError(null);
		tfAprvAmt.setComponentError(null);
		cbApprovedBy.setComponentError(null);
	}
	
	public void loadEmpList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading employee name list...");
		List<EmployeeDM> empList = serviceEmployee.getEmployeeList((String) cbEmpName.getValue(), null, null, "Active",
				null, null, null, null, null, "P");
		beanEmpDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmpDM.setBeanIdProperty("employeeid");
		beanEmpDM.addAll(empList);
		cbEmpName.setContainerDataSource(beanEmpDM);
	}
	
	public void loadApprovedByList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading approved by list...");
		List<EmployeeDM> empList = serviceEmployee.getEmployeeList((String) cbEmpName.getValue(), null, null, "Active",
				null, null, null, null, null, "F");
		beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmployeeDM.setBeanIdProperty("employeeid");
		beanEmployeeDM.addAll(empList);
		cbApprovedBy.setContainerDataSource(beanEmployeeDM);
	}
	
	public void loadAllowanceList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading allowance name list...");
		List<AllowanceDM> alwncList = serviceAllowance.getalowanceList(null, null, null,
				(String) cbAllowanceName.getValue(), "Active", "P");
		beanAlwncDM = new BeanContainer<Long, AllowanceDM>(AllowanceDM.class);
		beanAlwncDM.setBeanIdProperty("alowncId");
		beanAlwncDM.addAll(alwncList);
		cbAllowanceName.setContainerDataSource(beanAlwncDM);
	}
}
