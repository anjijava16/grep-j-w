/**
 * File Name	:	EmployeeShift.java
 * Description	:	this class is used for add/edit EmployeeShift details. 
 * Author		:	KAVITHA V M
 * Date			:	09-September-2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 *  Version         Date          		Modified By             Remarks
 *  0.1             09-September-2014   KAVITHA V M	  			Initial Version       
 * 
 */
package com.gnts.hcm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.ShiftDM;
import com.gnts.hcm.domain.txn.EmployeeShiftDM;
import com.gnts.hcm.service.mst.ShiftService;
import com.gnts.hcm.service.txn.EmployeeShiftService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;

public class EmployeeShift extends BaseUI {
	private EmployeeShiftService serviceEmployeeShift = (EmployeeShiftService) SpringContextHelper
			.getBean("EmployeeShift");
	private ShiftService serviceShift = (ShiftService) SpringContextHelper.getBean("Shift");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5, flColumn6, flColumn7;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private ComboBox cbEmployeeName, cbShiftName, cbStatus;
	private PopupDateField dfShiftStart, dfShiftEnd;
	// BeanItemContainer
	private BeanItemContainer<EmployeeShiftDM> beanEmployeeShift = null;
	// local variables declaration
	private Long companyId, shiftId, employeeId;
	private int recordCnt = 0;
	private String userName, empShiftId;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeShiftDM.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmployeeShift() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside EmployeeShift() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Printing EmployeeShift UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbEmployeeName = new GERPComboBox("Employee Name");
		cbEmployeeName.setItemCaptionPropertyId("fullname");
		cbEmployeeName.setWidth("200");
		loadEmployeeList();
		cbShiftName = new GERPComboBox("Shift");
		cbShiftName.setItemCaptionPropertyId("shiftName");
		cbShiftName.setWidth("140");
		loadShiftList();
		dfShiftStart = new PopupDateField("From date");
		dfShiftStart.setDateFormat("dd-MMM-yyyy");
		dfShiftStart.setInputPrompt("Select Date");
		dfShiftStart.setWidth("100");
		dfShiftStart.setVisible(true);
		dfShiftStart.setRequired(true);
		dfShiftStart.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				try {
					Date start = (Date) dfShiftStart.getValue();
					Date end = (Date) dfShiftEnd.getValue();
					if (start.before(end) || start.equals(end)) {
						dfShiftEnd.setComponentError(null);
					}
				}
				catch (Exception e) {
					logger.info("select start date  exception" + e);
				}
			}
		});
		dfShiftEnd = new PopupDateField("To date");
		dfShiftEnd.setDateFormat("dd-MMM-yyyy");
		dfShiftEnd.setInputPrompt("Select Date");
		dfShiftEnd.setRequired(true);
		dfShiftEnd.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				try {
					Date start = (Date) dfShiftStart.getValue();
					Date end = (Date) dfShiftEnd.getValue();
					if (start.before(end) || start.equals(end)) {
						dfShiftEnd.setComponentError(null);
					}
				}
				catch (Exception e) {
					logger.info("select end exception" + e);
				}
			}
		});
		dfShiftEnd.setWidth("100");
		dfShiftEnd.setVisible(true);
		// // build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.setBeanIdProperty("employeeid");
			beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyId, employeeId,
					null, null, null, "P"));
			cbEmployeeName.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadShiftList() {
		try {
			BeanContainer<Long, ShiftDM> beanShift = new BeanContainer<Long, ShiftDM>(ShiftDM.class);
			beanShift.setBeanIdProperty("shiftId");
			beanShift.addAll(serviceShift.getShiftList(shiftId, null, companyId, null, "F"));
			cbShiftName.setContainerDataSource(beanShift);
		}
		catch (Exception e) {
			logger.info("load Shift details" + e);
		}
	}
	
	private void loadSrchRslt() {
		try {
			tblMstScrSrchRslt.removeAllItems();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<EmployeeShiftDM> listEmployeeShift = new ArrayList<EmployeeShiftDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
					+ companyId + ", " + (Long) cbShiftName.getValue() + ", " + (Long) cbEmployeeName.getValue()
					+ (String) cbStatus.getValue());
			listEmployeeShift = serviceEmployeeShift.getempshiftlist(null, (Long) cbEmployeeName.getValue(),
					(Long) cbShiftName.getValue(), (String) cbStatus.getValue(), "F");
			recordCnt = listEmployeeShift.size();
			beanEmployeeShift = new BeanItemContainer<EmployeeShiftDM>(EmployeeShiftDM.class);
			beanEmployeeShift.addAll(listEmployeeShift);
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Got the EmployeeShift. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanEmployeeShift);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "empshiftid", "employeeName", "shiftdt", "shiftname",
					"shiftstatus", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Date", "Shift", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("empshiftid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		// Add components for Search Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(cbEmployeeName);
		flColumn2.addComponent(cbShiftName);
		flColumn3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		Label lbl1, lbl2;
		lbl1 = new Label();
		lbl2 = new Label();
		lbl1.setWidth("5");
		lbl2.setWidth("5");
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn5 = new FormLayout();
		flColumn6 = new FormLayout();
		flColumn7 = new FormLayout();
		flColumn1.addComponent(cbEmployeeName);
		flColumn2.addComponent(cbShiftName);
		flColumn3.addComponent(dfShiftStart);
		flColumn4.addComponent(lbl1);
		flColumn5.addComponent(dfShiftEnd);
		flColumn6.addComponent(lbl2);
		flColumn7.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.addComponent(flColumn5);
		hlUserInputLayout.addComponent(flColumn6);
		hlUserInputLayout.addComponent(flColumn7);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbEmployeeName.setValue(null);
		cbShiftName.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		resetFields();
		cbEmployeeName.setRequired(true);
		cbShiftName.setRequired(true);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		cbEmployeeName.setRequired(true);
		cbShiftName.setRequired(true);
		resetFields();
		editEmpShift();
	}
	
	private void editEmpShift() {
		try {
			if (tblMstScrSrchRslt.getValue() != null) {
				EmployeeShiftDM empShift = beanEmployeeShift.getItem(tblMstScrSrchRslt.getValue()).getBean();
				empShift.getEmpshiftid();
				cbEmployeeName.setValue(empShift.getEmployeeid());
				cbShiftName.setValue(empShift.getShiftid());
				if (empShift.getShiftdt() != null) {
					dfShiftEnd.setValue(empShift.getShiftdt());
					dfShiftStart.setValue(empShift.getShiftdt());
				}
				cbStatus.setValue(empShift.getShiftstatus());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if (cbEmployeeName.getValue() == null) {
			cbEmployeeName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			errorFlag = true;
		} else {
			cbEmployeeName.setComponentError(null);
		}
		if (cbShiftName.getValue() == null) {
			cbShiftName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPL_SHIFT));
			errorFlag = true;
		} else {
			cbShiftName.setComponentError(null);
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
		Calendar cal = Calendar.getInstance();
		cal.setTime(dfShiftStart.getValue());
		cal.add(Calendar.DATE, -1);
		while (cal.getTime().before(dfShiftEnd.getValue())) {
			EmployeeShiftDM employeeShift = new EmployeeShiftDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				employeeShift = beanEmployeeShift.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			employeeShift.setEmployeeid((Long) cbEmployeeName.getValue());
			employeeShift.setShiftid((Long) cbShiftName.getValue());
			if (cbStatus.getValue() != null) {
				employeeShift.setShiftstatus((String) cbStatus.getValue());
			}
			employeeShift.setLastupdateddt(DateUtils.getcurrentdate());
			employeeShift.setLastupdatedby(userName);
			// Date calculation
			cal.add(Calendar.DATE, 1);
			employeeShift.setShiftdt(cal.getTime());
			serviceEmployeeShift.saveAndUpdate(employeeShift);
		}
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for Employeeshift. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_EMP_PROXIMITY);
		UI.getCurrent().getSession().setAttribute("audittablepk", empShiftId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		cbEmployeeName.setRequired(false);
		cbShiftName.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		cbEmployeeName.setValue(null);
		cbEmployeeName.setComponentError(null);
		cbShiftName.setValue(null);
		cbShiftName.setComponentError(null);
		dfShiftStart.setValue(null);
		dfShiftEnd.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
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
		}
	}
}
