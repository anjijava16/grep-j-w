/**
 * File Name 		: EmployeeAttendence.java 
 * Description 		: this class is used for add/edit EmployeeAttendence  details. 
 * Author 			: Madhu T
 * Date 			: sep-26-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1         sep-26-2014       Madhu T	        Initial Version
 **/
package com.gnts.hcm.txn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.ParameterService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.components.GERPTimeField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.AttendenceProcDM;
import com.gnts.hcm.domain.txn.EmpAttendenceDM;
import com.gnts.hcm.service.txn.AttendenceProcService;
import com.gnts.hcm.service.txn.EmpAttendenceService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class EmployeeAttendence extends BaseUI {
	// Bean creation
	private EmpAttendenceService serviceEmpAtten = (EmpAttendenceService) SpringContextHelper.getBean("EmpAttendence");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private AttendenceProcService serviceAttendenceProce = (AttendenceProcService) SpringContextHelper
			.getBean("AttendenceProc");
	private ParameterService serviceParameter = (ParameterService) SpringContextHelper.getBean("parameter");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3, flColumn5, flColumn6;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private ComboBox cbStatus, cbEmpName;
	private DateField dfAtndence, dfStartDt, dfEndDt;
	private TextField tfReturnCount, tfLWPHrs;
	private GERPTimeField tfPresentHr, tfOTHrs, tfLateHrs, tfAbsentHr, tfLeaveHr, tfOnDutyHr, tfPermisnHr;
	private Button btnRunAtten = new GERPButton("Save", "savebt");
	// BeanItemContainer
	private BeanItemContainer<EmpAttendenceDM> beanEmpAtndncDM = null;
	// local variables declaration
	private Long companyid, attnceProcId;
	private Long processId;
	private String alwncId, pkEmpAdvncId;
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeAttendence.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmployeeAttendence() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeAttendence() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting EmployeeAttendence UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("150px");
		// Employee Name Combo Box
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setWidth("200");
		cbEmpName.setItemCaptionPropertyId("fullname");
		loadEmpAprvrList();
		// Attendence Date field
		dfAtndence = new GERPPopupDateField("Attendence Date");
		// Start Date Field
		dfStartDt = new GERPPopupDateField("Start Date");
		// End Date Field
		dfEndDt = new GERPPopupDateField("End Date");
		// Return count Text field
		tfReturnCount = new GERPTextField("Return count");
		// Present Hour TimeField
		tfPresentHr = new GERPTimeField("Present Hour");
		// OT Hour TimeField
		tfOTHrs = new GERPTimeField("OT Hour");
		// Late hours TimeField
		tfLateHrs = new GERPTimeField("Late Hour");
		// Absent Hour TimeField
		tfAbsentHr = new GERPTimeField("Absent Hour");
		// Leave Hour TimeField
		tfLeaveHr = new GERPTimeField("Leave Hour");
		// OnDuty Hour TimeField
		tfOnDutyHr = new GERPTimeField("OnDuty Hour");
		// Permission Hour TimeField
		tfPermisnHr = new GERPTimeField("Permission Hour");
		// LWP Hour TimeField
		tfLWPHrs = new GERPTextField("LWP Hour");
		hlCmdBtnLayout.setVisible(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		staffAttendancePapulateAndConfig(true);
		btnRunAtten.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				runAttendenceProcess();
			}
		});
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn5 = new FormLayout();
		flColumn6 = new FormLayout();
		Label lbl = new Label();
		lbl.setWidth("5px");
		Label lbl1 = new Label();
		lbl1.setWidth("5px");
		flColumn1.addComponent(cbEmpName);
		flColumn2.addComponent(dfStartDt);
		flColumn3.addComponent(lbl);
		flColumn4.addComponent(dfEndDt);
		flColumn5.addComponent(lbl1);
		flColumn6.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.addComponent(flColumn5);
		hlSearchLayout.addComponent(flColumn6);
		hlSearchLayout.addComponent(btnRunAtten);
		hlSearchLayout.setComponentAlignment(btnRunAtten, Alignment.MIDDLE_RIGHT);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void runAttendenceProcess() {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.setTime(dfStartDt.getValue());
		cal.add(Calendar.DATE, -1);
		String workhours = "8";
		try {
			workhours = serviceParameter.getParameterValue("WORK_HOURS", null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		AttendenceProcDM attendenceProcDM = new AttendenceProcDM();
		attendenceProcDM.setEmpid((Long) cbEmpName.getValue());
		attendenceProcDM.setPayPeriodId(2L);
		attendenceProcDM.setAllStDt(dfStartDt.getValue());
		attendenceProcDM.setAllEndDt(dfEndDt.getValue());
		attendenceProcDM.setBranchId(198L);
		attendenceProcDM.setProcessedBy(username);
		attendenceProcDM.setProcessedDt(new Date());
		attendenceProcDM.setCompanyId(companyid);
		attendenceProcDM.setStatus("Pending");
		serviceAttendenceProce.saveAndUpdate(attendenceProcDM);
		try {
			EmpAttendenceDM empAttendenceDM = new EmpAttendenceDM();
			empAttendenceDM.setEmpId((Long) cbEmpName.getValue());
			empAttendenceDM.setAttProcId(attendenceProcDM.getAttProcId());
			empAttendenceDM.setAttDt(cal.getTime());
			empAttendenceDM.setPresentHr(workhours);
			empAttendenceDM.setOtHrs("0");
			empAttendenceDM.setLeaveHr("0");
			empAttendenceDM.setOndutyHr("0");
			empAttendenceDM.setAbsentHr("0");
			empAttendenceDM.setPermissionHr("0");
			empAttendenceDM.setLwpHr(0L);
			empAttendenceDM.setStatus("Pending");
			empAttendenceDM.setLastUpdatedBy(username);
			empAttendenceDM.setLastUpdatedDt(new Date());
			serviceEmpAtten.saveAndUpdate(empAttendenceDM);
		}
		catch (Exception ex) {
			logger.info(ex.getMessage());
		}
		staffAttendancePapulateAndConfig(true);
	}
	
	private void staffAttendancePapulateAndConfig(boolean search) {
		try {
			tblMstScrSrchRslt.removeAllItems();
			tblMstScrSrchRslt.setSelectable(true);
			List<EmpAttendenceDM> list = null;
			if (search) {
				list = new ArrayList<EmpAttendenceDM>();
				if ((Long) cbEmpName.getValue() != null) {
					list = serviceEmpAtten.getByStaffAttendenceList((Long) cbEmpName.getValue());
				}
			} else {
				list = serviceAttendenceProce.loadStaffAttendanceList(processId);
			}
			recordCnt = list.size();
			beanEmpAtndncDM = new BeanItemContainer<EmpAttendenceDM>(EmpAttendenceDM.class);
			beanEmpAtndncDM.addAll(list);
			tblMstScrSrchRslt.setContainerDataSource(beanEmpAtndncDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "attDt", "presentHr", "otHrs", "lateHr", "absentHr",
					"leaveHr", "ondutyHr", "permissionHr", "status", "lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Atten. Date", "Present(Hrs)", "OT(Hrs)", "Late(Hrs)",
					"Absent(Hrs)", "Leave(Hrs)", "OnDuty(Hrs)", "Permission(Hrs)", "Status", "Updated Date",
					"Updated By" });
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbEmpName.setValue(null);
		dfAtndence.setValue(null);
		tfPresentHr.setValue(null);
		tfOTHrs.setValue(null);
		tfLateHrs.setValue(null);
		tfAbsentHr.setValue(null);
		tfLeaveHr.setValue(null);
		tfOnDutyHr.setValue(null);
		tfPermisnHr.setValue(null);
		tfLWPHrs.setValue(null);
		tfReturnCount.setValue(null);
		cbEmpName.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editEmpAtndnc() {
		try {
			if (tblMstScrSrchRslt.getValue() != null) {
				EmpAttendenceDM empAttendence = beanEmpAtndncDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				pkEmpAdvncId = empAttendence.getAttendenceId().toString();
				cbEmpName.setValue(empAttendence.getEmpId());
				dfAtndence.setValue(empAttendence.getAttDt1());
				tfPresentHr.setTime(empAttendence.getPresentHr());
				tfOTHrs.setTime(empAttendence.getOtHrs());
				tfLateHrs.setTime(empAttendence.getLateHr());
				tfAbsentHr.setTime(empAttendence.getAbsentHr());
				tfLeaveHr.setTime(empAttendence.getLeaveHr());
				tfOnDutyHr.setTime(empAttendence.getOndutyHr());
				tfPermisnHr.setTime(empAttendence.getPermissionHr());
				if (empAttendence.getLwpHr() != null) {
					tfLWPHrs.setValue(empAttendence.getLwpHr().toString());
				}
				if (empAttendence.getReturnCount() != null) {
					tfReturnCount.setValue(empAttendence.getReturnCount().toString());
				}
				cbStatus.setValue(empAttendence.getStatus());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		// loadSrchRslt();
		staffAttendancePapulateAndConfig(true);
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
		cbEmpName.setValue(null);
		dfEndDt.setValue(null);
		dfStartDt.setValue(null);
		// reset the field valued to default
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		staffAttendancePapulateAndConfig(true);
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		resetFields();
		assembleSearchLayout();
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbEmpName.setRequired(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for EmployeeAttendence. ID " + alwncId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_EMPLOYEE_ADVANCE);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkEmpAdvncId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbEmpName.setComponentError(null);
		cbEmpName.setRequired(false);
		resetFields();
		staffAttendancePapulateAndConfig(true);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		cbEmpName.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editEmpAtndnc();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbEmpName.setComponentError(null);
		errorFlag = false;
		if (cbEmpName.getValue() == null) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbEmpName.getValue());
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		EmpAttendenceDM empAdvncObj = new EmpAttendenceDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			empAdvncObj = beanEmpAtndncDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if (cbEmpName.getValue() != null) {
			empAdvncObj.setEmpId((Long.valueOf(cbEmpName.getValue().toString())));
		}
		if (dfAtndence.getValue() != null) {
			empAdvncObj.setAttDt((dfAtndence.getValue()));
		}
		if (tfPresentHr.getValue() != null) {
			empAdvncObj.setPresentHr(tfPresentHr.getHorsMunites());
		}
		if (tfOTHrs.getValue() != null) {
			empAdvncObj.setOtHrs(tfOTHrs.getHorsMunites());
		}
		if (tfLateHrs.getValue() != null) {
			empAdvncObj.setLateHr(tfLateHrs.getHorsMunites());
		}
		if (tfAbsentHr.getValue() != null) {
			empAdvncObj.setAbsentHr(tfAbsentHr.getHorsMunites());
		}
		if (tfLeaveHr.getValue() != null) {
			empAdvncObj.setLeaveHr(tfLeaveHr.getHorsMunites());
		}
		if (tfOnDutyHr.getValue() != null) {
			empAdvncObj.setOndutyHr(tfOnDutyHr.getHorsMunites());
		}
		if (tfPermisnHr.getValue() != null) {
			empAdvncObj.setPermissionHr(tfPermisnHr.getHorsMunites());
		}
		if (tfLWPHrs.getValue() != null) {
			empAdvncObj.setLwpHr(Long.valueOf(tfLWPHrs.getValue()));
		}
		if (tfReturnCount.getValue() != null) {
			empAdvncObj.setReturnCount(Long.valueOf(tfReturnCount.getValue()));
		}
		if (cbStatus.getValue() != null) {
			empAdvncObj.setStatus((String) cbStatus.getValue());
		}
		empAdvncObj.setAttProcId(attnceProcId);
		empAdvncObj.setLastUpdatedDt(DateUtils.getcurrentdate());
		empAdvncObj.setLastUpdatedBy(username);
		serviceEmpAtten.saveAndUpdate(empAdvncObj);
		resetFields();
		staffAttendancePapulateAndConfig(true);
	}
	
	private void loadEmpAprvrList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Approver Search...");
			List<EmployeeDM> empList = serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null,
					null, null, null, "P");
			empList.add(new EmployeeDM(-1L, "All"));
			BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.setBeanIdProperty("employeeid");
			beanEmployeeDM.addAll(empList);
			cbEmpName.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
