/**
 * File Name	:	EmployeeEntry.java
 * Description	:	This Screen Purpose for Modify the Employee Entry.
 * Author		:	Karthikeyan R
 * Date			:	
 * Modification :   
 * Modified By  :   
 * Description 	:
 *
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version      Date           	Modified By      		Remarks
 * 0.1          Sep 14, 2014   	Karthikeyan R		Initial Version		
 * 
 */
package com.gnts.hcm.txn;

import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPSaveNotification;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class EmployeeEntry extends BaseUI {
	private EmployeeService serviceemployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private DepartmentService servicedepartment = (DepartmentService) SpringContextHelper.getBean("department");
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add Input fields
	private ComboBox cbDepartmentName, cbEmployeeName;
	private TabSheet tabSheet;
	private Button btnSubmit = new GERPButton("Save", "savebt");
	private Button btnCancel = new GERPButton("Cancel", "cancelbt");
	private String username;
	// To add Bean Item Container
	private Long companyid, employeeId;
	private FormLayout flColumn1, flColumn2;
	private EmployeeOnduty onduty;
	private EmployeePermission permission;
	private EmployeeOvertime overtime;
	private EmployeeAbsent absent;
	private EmployeeLate late;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeEntry.class);
	private static final long serialVersionUID = 1L;
	
	public EmployeeEntry() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeEntry() constructor");
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting EmployeeEntry UI");
		cbDepartmentName = new GERPComboBox("Department Name");
		cbDepartmentName.setItemCaptionPropertyId("deptname");
		cbDepartmentName.setWidth("150");
		loadDepartmentList();
		cbDepartmentName.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object obj = event.getProperty().getValue();
				if (obj != null) {
					loadEmployeeList();
				}
			}
		});
		cbEmployeeName = new GERPComboBox("Employee Name");
		cbEmployeeName.setItemCaptionPropertyId("fullname");
		cbEmployeeName.setWidth("150");
		cbEmployeeName.setRequired(true);
		cbEmployeeName.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object obj = event.getProperty().getValue();
				if (obj != null) {
					assemblesearchdetails((Long) cbEmployeeName.getValue());
					tabSheet.setEnabled(true);
				}
			}
		});
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		// Add master buttons to the Page header container
		hlPageHdrContainter.addComponent(btnSubmit);
		hlPageHdrContainter.setComponentAlignment(btnSubmit, Alignment.MIDDLE_RIGHT);
		hlPageHdrContainter.addComponent(btnCancel);
		hlPageHdrContainter.setComponentAlignment(btnCancel, Alignment.MIDDLE_RIGHT);
		btnSubmit.setVisible(false);
		btnSave.setVisible(false);
		btnCancel.setVisible(true);
		btnSearch.setVisible(false);
		btnReset.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		assemblesearchdetails(null);
		resetFields();
		btnSubmit.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validateDetails();
					onduty.ondutysave((Long) cbEmployeeName.getValue());
					permission.permissionsave((Long) cbEmployeeName.getValue());
					overtime.overtimesave((Long) cbEmployeeName.getValue());
					absent.saveAbsentDetails((Long) cbEmployeeName.getValue());
					late.latesave((Long) cbEmployeeName.getValue());
					new GERPSaveNotification();
					hlSearchLayout.setVisible(true);
				}
				catch (ValidationException e) {
					e.printStackTrace();
				}
			}
		});
		btnCancel.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				cancelDetails();
			}
		});
	}
	
	private void assemblesearchdetails(Long empid) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search details");
		hlUserInputLayout.removeAllComponents();
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn1.addComponent(cbDepartmentName);
		flColumn2.addComponent(cbEmployeeName);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
		VerticalLayout vlUserInput = new VerticalLayout();
		tabSheet = new TabSheet();
		tabSheet.setSizeFull();
		onduty = new EmployeeOnduty(empid);
		tabSheet.addTab(onduty, "Employee Onduty");
		permission = new EmployeePermission(empid);
		tabSheet.addTab(permission, "Employee Permission");
		overtime = new EmployeeOvertime(empid);
		tabSheet.addTab(overtime, "Employee OverTime");
		absent = new EmployeeAbsent(empid);
		tabSheet.addTab(absent, "Employee Absent");
		late = new EmployeeLate(empid);
		tabSheet.addTab(late, "Employee Late");
		vlUserInput.addComponent(tabSheet);
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		vlSrchRsltContainer.addComponent(vlUserInput);
		tabSheet.setEnabled(false);
	}
	
	@SuppressWarnings("unused")
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling UserInput layout");
		try {
			hlUserInputLayout.setSpacing(true);
			hlUserInputLayout.setMargin(true);
			tblMstScrSrchRslt.setVisible(false);
		}
		catch (Exception e) {
			logger.info("loadEmployeeEntries" + e);
			e.printStackTrace();
		}
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void loadEmployeeList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading EmployeeList");
			BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(serviceemployee.getEmployeeList(null, null, (Long) cbDepartmentName.getValue(),
					"Active", companyid, employeeId, null, null, null, "P"));
			cbEmployeeName.setContainerDataSource(beanEmployee);
		}
		catch (Exception e) {
		}
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void loadDepartmentList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading DepartmentList");
			List<DepartmentDM> listDepartment = servicedepartment.getDepartmentList(companyid, null, "Active", "F");
			listDepartment.add(new DepartmentDM(0L, "All Departments"));
			BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
			beanDepartment.setBeanIdProperty("deptid");
			beanDepartment.addAll(listDepartment);
			cbDepartmentName.setContainerDataSource(beanDepartment);
		}
		catch (Exception e) {
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validatating Data.....");
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
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data.....");
		hlSrchContainer.setVisible(true);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Cancel Details...");
		onduty.resetFields();
		permission.resetFields();
		overtime.resetFields();
		absent.resetFields();
		late.resetFields();
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		cbEmployeeName.setValue(null);
		cbEmployeeName.setComponentError(null);
		cbEmployeeName.setValue("");
		cbEmployeeName.setValue(0L);
		cbDepartmentName.setValue(null);
		cbDepartmentName.setComponentError(null);
		cbDepartmentName.setValue("");
		cbDepartmentName.setValue(0L);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		tabSheet.setEnabled(false);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
	}
	
	@Override
	protected void resetSearchDetails() {
	}
	
	@Override
	protected void addDetails() {
	}
	
	@Override
	protected void editDetails() {
	}
	
	@Override
	protected void showAuditDetails() {
	}
}
