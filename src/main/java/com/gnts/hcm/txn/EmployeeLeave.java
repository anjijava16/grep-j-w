/**
 * File Name 		: EmployeeLeave.java 
 * Description 		: this class is used for add/update EmployeeLeave details. 
 * Author 			: Karthikeyan R
 * Date 			: Sep 22, 2014
 * Modification 	:
 * Modified By 		: Karthikeyan R 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Sep 22, 2014         Karthikeyan R	          Initial Version
 */
package com.gnts.hcm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPSaveNotification;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.LeaveTypeDM;
import com.gnts.hcm.domain.txn.EmployeeLeaveBalanceDM;
import com.gnts.hcm.domain.txn.EmployeeLeaveDM;
import com.gnts.hcm.service.mst.LeaveTypeService;
import com.gnts.hcm.service.txn.EmployeeLeaveBalanceService;
import com.gnts.hcm.service.txn.EmployeeLeaveService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class EmployeeLeave extends BaseUI {
	private WorkOrderHdrService serviceWrkOrdHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	// Add Input fields
	public FormLayout flcolumn1, flcolumn2;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlcol1, hlcol2;
	// Search Control Layout
	private VerticalLayout vlayout;
	private ComboBox cbEmployeeName, cbDepartmentName;
	private Table tblMstScrSrchRslt = new GERPTable();
	// Vertical Control Layout
	private PopupDateField dfdatefrom, dfdateto;
	private TextField tfnoofdays;
	private ComboBox cbappmanager, cbleavetype, cbempstatus;
	private TextArea taLeaveReason;
	private CheckBox cbhalfday;
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	private String username;
	private Button btnSubmit = new GERPButton("Save", "savebt");
	private Button btnCancel = new GERPButton("Cancel", "cancelbt");
	private Button btnadd;
	HorizontalLayout hlsavecancel = new HorizontalLayout();
	// To add Bean Item Container
	private List<EmployeeLeaveDM> usertable = new ArrayList<EmployeeLeaveDM>();
	private List<EmployeeLeaveBalanceDM> empLeaveBal = new ArrayList<EmployeeLeaveBalanceDM>();
	private BeanItemContainer<EmployeeLeaveDM> beanLeave = null;
	private BeanItemContainer<EmployeeLeaveBalanceDM> beanEmpLeaveBalance = null;
	private EmployeeLeaveService serviceleave = (EmployeeLeaveService) SpringContextHelper.getBean("EmployeeLeave");
	private EmployeeService serviceemployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private DepartmentService servicedepartment = (DepartmentService) SpringContextHelper.getBean("department");
	private LeaveTypeService serviceLeaveType = (LeaveTypeService) SpringContextHelper.getBean("LeaveType");
	private EmployeeLeaveBalanceService serviceLeaveBalance = (EmployeeLeaveBalanceService) SpringContextHelper
			.getBean("EmployeeLeaveBalance");
	private Long companyid, leavetypeid, leaveid, branchID, appScreenId, roleId;
	// private HorizontalLayout hlSearchLayout;
	private int total = 0;
	private Table tblLeaveBalnce;
	private Date dtfrm;
	private String name;
	private Double check;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeLeave.class);
	private static final long serialVersionUID = 1L;
	
	public EmployeeLeave() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeLeave() constructor");
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting EmployeeLeave UI");
		// Department Name Combo box
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
		// Initialization for cbempstatus
		cbEmployeeName = new GERPComboBox("Employee Name");
		cbEmployeeName.setItemCaptionPropertyId("fullname");
		cbEmployeeName.setRequired(true);
		cbEmployeeName.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object obj = event.getProperty().getValue();
				if (obj != null) {
					usertable = serviceleave.getempleaveList(leaveid, (Long) cbEmployeeName.getValue(), leavetypeid,
							(String) cbempstatus.getValue(), null, null, "F");
					loadSrchRslt();
					loadAppmgr();
					loadLeaveBalance();
				}
			}
		});
		try {
			ApprovalSchemaDM obj = serviceWrkOrdHdr.getReviewerId(companyid, appScreenId, branchID, roleId).get(0);
			name = obj.getApprLevel();
			if (name.equals("Approver")) {
				cbempstatus = new GERPComboBox("Status", BASEConstants.T_HCM_LEAVE_STATUS,
						BASEConstants.AP_LEAVE_STATUS);
			} else {
				cbempstatus = new GERPComboBox("Status", BASEConstants.T_HCM_LEAVE_STATUS,
						BASEConstants.RV_LEAVE_STATUS);
			}
			if (name.equals("Approver")) {
				btnAdd.setVisible(false);
			} else {
				btnAdd.setVisible(true);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		tblLeaveBalnce = new Table();
		tblLeaveBalnce.setPageLength(3);
		// create form layouts to hold the input items
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		hlcol1 = new HorizontalLayout();
		hlcol2 = new HorizontalLayout();
		Label lbl = new Label();
		lbl.setWidth("250px");
		// add the user input items into appropriate form layout
		flcolumn1.addComponent(cbDepartmentName);
		flcolumn2.addComponent(cbEmployeeName);
		hlcol1.addComponent(flcolumn1);
		hlcol1.addComponent(flcolumn2);
		hlcol2.addComponent(lbl);
		hlcol2.addComponent(tblLeaveBalnce);
		hlcol2.setComponentAlignment(tblLeaveBalnce, Alignment.MIDDLE_RIGHT);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(hlcol1);
		hlUserInputLayout.addComponent(hlcol2);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		hlCmdBtnLayout.setVisible(false);
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlPageHdrContainter.addComponent(btnSubmit);
		hlPageHdrContainter.setComponentAlignment(btnSubmit, Alignment.MIDDLE_RIGHT);
		hlPageHdrContainter.addComponent(btnCancel);
		hlPageHdrContainter.setComponentAlignment(btnCancel, Alignment.MIDDLE_RIGHT);
		btnSubmit.setVisible(true);
		btnSave.setVisible(false);
		btnCancel.setVisible(true);
		btnSearch.setVisible(false);
		btnReset.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnSubmit.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					leavesave((Long) cbEmployeeName.getValue());
					new GERPSaveNotification();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnCancel.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				resetField();
			}
		});
		// Initialization for dfdatefrom
		dfdatefrom = new GERPPopupDateField("Date From");
		dfdatefrom.setDateFormat("dd-MMM-yyyy");
		dfdatefrom.setRequired(true);
		dfdatefrom.setWidth("150");
		dfdatefrom.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				dtfrm = dfdatefrom.getValue();
				if (dfdatefrom.getValue().after(new Date()) || dfdatefrom.getValue().equals(new Date())) {
					dfdatefrom.setComponentError(new UserError(GERPErrorCodes.DATE_FROM));
				} else {
					dfdatefrom.setComponentError(null);
				}
			}
		});
		// Initialization for dfdatefrom
		dfdateto = new GERPPopupDateField("Date To");
		dfdateto.setDateFormat("dd-MMM-yyyy");
		dfdateto.setRequired(true);
		dfdateto.setWidth("150");
		dfdateto.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				if (dfdateto.getValue().before(dtfrm)) {
					dfdateto.setComponentError(new UserError(GERPErrorCodes.DATE_TO));
				} else {
					dfdateto.setComponentError(null);
				}
			}
		});
		dfdateto.setImmediate(true);
		dfdateto.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				diffdays();
				if (Double.valueOf(check.toString()) == 1.0) {
					cbhalfday.setEnabled(true);
				} else {
					cbhalfday.setValue(false);
					cbhalfday.setEnabled(false);
				}
			}
		});
		// Initialization for tfnoofdays
		tfnoofdays = new TextField("No of Days");
		tfnoofdays.setWidth("150");
		tfnoofdays.setRequired(true);
		vlSrchRsltContainer.removeAllComponents();
		// Initialization for cbappmanager
		cbappmanager = new GERPComboBox("Approve Manager");
		cbappmanager.setItemCaptionPropertyId("firstname");
		cbappmanager.setRequired(true);
		loadAppmgr();
		// Initialization for cbleavetype
		cbleavetype = new GERPComboBox("Leave Type");
		cbleavetype.setItemCaptionPropertyId("leaveTypeName");
		cbleavetype.setRequired(true);
		loadleavetype();
		// Initialization for taleavereason
		taLeaveReason = new GERPTextArea("Leave Reason");
		taLeaveReason.setInputPrompt("Enter Remarks");
		taLeaveReason.setHeight("75");
		taLeaveReason.setWidth("195");
		taLeaveReason.setReadOnly(false);
		// Initialization for cbhalfday
		cbhalfday = new CheckBox("Halfday");
		// Initialization for btnSave
		btnSave = new Button("Save", this);
		btnSave.setDescription("Save");
		btnSave.setStyleName("savebt");
		btnCancel = new Button("Cancel", this);
		btnCancel.setDescription("Cancel");
		btnCancel.setStyleName("cancelbt");
		hlsavecancel = new HorizontalLayout();
		hlsavecancel.addComponent(btnSave);
		hlsavecancel.addComponent(btnCancel);
		hlsavecancel.setVisible(false);
		// label,add,edit and download panel
		btnadd = new GERPButton("Add", "add", this);
		btnadd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee Leave
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDetail()) {
					saveleave();
				}
			}
		});
		HorizontalLayout hlTableCaptionLayout = new HorizontalLayout();
		// Initialization for table panel components
		tblMstScrSrchRslt.setSizeFull();
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.setColumnCollapsingAllowed(true);
		tblMstScrSrchRslt.setPageLength(8);
		tblMstScrSrchRslt.setStyleName(Runo.TABLE_SMALL);
		tblMstScrSrchRslt.setWidth("100%");
		tblMstScrSrchRslt.setImmediate(true);
		tblMstScrSrchRslt.setFooterVisible(true);
		VerticalLayout vltable = new VerticalLayout();
		VerticalLayout vlTablePanel = new VerticalLayout();
		vltable.setSizeFull();
		vltable.setMargin(true);
		vltable.addComponent(hlTableCaptionLayout);
		vltable.addComponent(tblMstScrSrchRslt);
		vlTablePanel.addComponent(vltable);
		// ClickListener for Employee Leave Tale
		tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
					tblMstScrSrchRslt.setImmediate(true);
					btnadd.setCaption("Add");
					btnadd.setStyleName("savebt");
					resetField();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnadd.setCaption("Update");
					btnadd.setStyleName("savebt");
					editleave();
				}
			}
		});
		// create form layouts to hold the input items
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		// add the user input items into appropriate form layout
		flColumn1.addComponent(cbleavetype);
		flColumn1.addComponent(dfdatefrom);
		flColumn1.addComponent(dfdateto);
		flColumn2.addComponent(tfnoofdays);
		flColumn2.addComponent(cbhalfday);
		flColumn2.addComponent(cbappmanager);
		flColumn3.addComponent(taLeaveReason);
		flColumn4.addComponent(cbempstatus);
		flColumn4.addComponent(btnadd);
		// add the form layouts into user input layout
		HorizontalLayout hlLeaveForm = new HorizontalLayout();
		hlLeaveForm.setSpacing(true);
		hlLeaveForm.addComponent(flColumn1);
		hlLeaveForm.addComponent(flColumn2);
		hlLeaveForm.addComponent(flColumn3);
		hlLeaveForm.addComponent(flColumn4);
		hlLeaveForm.setSizeFull();
		hlLeaveForm.setSpacing(true);
		hlLeaveForm.setMargin(true);
		vlayout = new VerticalLayout();
		vlayout.addComponent(hlLeaveForm);
		vlayout.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.addComponent(GERPPanelGenerator.createPanel(vlayout));
		btnSearch.setVisible(false);
		loadSrchRslt();
		loadLeaveBalance();
		btnadd.setStyleName("add");
		resetField();
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading Search Result....");
		tblMstScrSrchRslt.removeAllItems();
		total = 0;
		total = usertable.size();
		beanLeave = new BeanItemContainer<EmployeeLeaveDM>(EmployeeLeaveDM.class);
		beanLeave.addAll(usertable);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Employee Leave. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanLeave);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "datefrom", "dateto", "noofdays", "empleavestatus",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Date From", "Date To", "No of Days", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("leaveid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + total);
	}
	
	private void loadLeaveBalance() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading Search Result....");
		tblLeaveBalnce.removeAllItems();
		if (cbEmployeeName.getValue() != null) {
			empLeaveBal = serviceLeaveBalance.getemplveballist(null, (Long) cbEmployeeName.getValue(), null, null, "F");
		}
		beanEmpLeaveBalance = new BeanItemContainer<EmployeeLeaveBalanceDM>(EmployeeLeaveBalanceDM.class);
		beanEmpLeaveBalance.addAll(empLeaveBal);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Employee Leave. result set");
		tblLeaveBalnce.setContainerDataSource(beanEmpLeaveBalance);
		tblLeaveBalnce.setColumnAlignment("opnbal", Align.RIGHT);
		tblLeaveBalnce.setColumnAlignment("lvebal", Align.RIGHT);
		tblLeaveBalnce.setVisibleColumns(new Object[] { "leaveType", "opnbal", "lvebal" });
		tblLeaveBalnce.setColumnHeaders(new String[] { "Leave Type", "Total Leaves", "Leave Balance" });
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	public void loadDepartmentList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading DepartmentList");
		List<DepartmentDM> departmentlist = servicedepartment.getDepartmentList(companyid, null, "Active", "F");
		departmentlist.add(new DepartmentDM(0L, "All Departments"));
		BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beanDepartment.setBeanIdProperty("deptid");
		beanDepartment.addAll(departmentlist);
		cbDepartmentName.setContainerDataSource(beanDepartment);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading EmployeeList");
		BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmployee.setBeanIdProperty("employeeid");
		beanEmployee.addAll(serviceemployee.getEmployeeList(null, null, (Long) cbDepartmentName.getValue(), "Active",
				companyid, null, null, null, null, "P"));
		cbEmployeeName.setContainerDataSource(beanEmployee);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void loadAppmgr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading EmployeeList");
		BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmployee.setBeanIdProperty("employeeid");
		beanEmployee.addAll(serviceemployee.getEmployeeList(null, null, (Long) cbDepartmentName.getValue(), "Active",
				companyid, null, null, null, null, "P"));
		cbappmanager.setContainerDataSource(beanEmployee);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void loadleavetype() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading leave Type");
		BeanContainer<Long, LeaveTypeDM> beanLeave = new BeanContainer<Long, LeaveTypeDM>(LeaveTypeDM.class);
		beanLeave.setBeanIdProperty("leaveTypeId");
		beanLeave.addAll(serviceLeaveType.getLeaveTypeList(null, null, companyid, null, "Y", "Active", "F"));
		cbleavetype.setContainerDataSource(beanLeave);
	}
	
	// Method used to display selected row's values in desired text box and combo box for edit the values
	private void editleave() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing Leave details.......");
		if (tblMstScrSrchRslt.getValue() != null) {
			EmployeeLeaveDM leave = beanLeave.getItem(tblMstScrSrchRslt.getValue()).getBean();
			dfdatefrom.setValue(leave.getDatefrm());
			dfdateto.setValue(leave.getDatetoo());
			tfnoofdays.setReadOnly(false);
			tfnoofdays.setValue(leave.getNoofdays().toString());
			tfnoofdays.setReadOnly(true);
			cbappmanager.setValue(leave.getAppmgr());
			cbleavetype.setValue(leave.getLeavetypeid());
			if (leave.getHalfday().equals("Y")) {
				cbhalfday.setValue(true);
			} else {
				cbhalfday.setValue(false);
			}
			if (leave.getLeavereason() != null) {
				taLeaveReason.setValue(leave.getLeavereason());
			}
			cbempstatus.setValue(leave.getEmpleavestatus());
		}
	}
	
	private void saveleave() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Leave details......");
		try {
			EmployeeLeaveDM employeeLeaveDM = new EmployeeLeaveDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				employeeLeaveDM = beanLeave.getItem(tblMstScrSrchRslt.getValue()).getBean();
				usertable.remove(employeeLeaveDM);
			}
			if (dfdatefrom.getValue() != null) {
				employeeLeaveDM.setDatefrom(dfdatefrom.getValue());
			}
			if (dfdateto.getValue() != null) {
				employeeLeaveDM.setDateto(dfdateto.getValue());
			}
			if (tfnoofdays.getValue() != null) {
				employeeLeaveDM.setNoofdays((new BigDecimal(tfnoofdays.getValue())));
			}
			if (cbappmanager.getValue() != null) {
				employeeLeaveDM.setAppmgr(Long.valueOf(cbappmanager.getValue().toString()));
			}
			if (cbleavetype.getValue() != null) {
				employeeLeaveDM.setLeavetypeid(Long.valueOf(cbleavetype.getValue().toString()));
			}
			if (taLeaveReason.getValue() != null) {
				employeeLeaveDM.setLeavereason(taLeaveReason.getValue());
			}
			if (cbhalfday.getValue() == null || cbhalfday.getValue().equals(false)) {
				employeeLeaveDM.setHalfday("N");
			} else {
				employeeLeaveDM.setHalfday("Y");
			}
			if (cbempstatus.getValue() != null) {
				employeeLeaveDM.setEmpleavestatus(cbempstatus.getValue().toString());
			}
			employeeLeaveDM.setLastupdatedby(username);
			employeeLeaveDM.setLastupdateddt(DateUtils.getcurrentdate());
			usertable.add(employeeLeaveDM);
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		resetField();
	}
	
	public void leavesave(Long employeeid) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Employeeleave Save details......");
		@SuppressWarnings("unchecked")
		Collection<EmployeeLeaveDM> itemIds = (Collection<EmployeeLeaveDM>) tblMstScrSrchRslt.getVisibleItemIds();
		for (EmployeeLeaveDM saveleave : (Collection<EmployeeLeaveDM>) itemIds) {
			saveleave.setEmployeeid(employeeid);
			serviceleave.saveAndUpdate(saveleave);
		}
		loadSrchRslt();
		tblMstScrSrchRslt.removeAllItems();
	}
	
	public Boolean validateDetail() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validatating Data.....");
		Boolean errorFlag = true;
		cbEmployeeName.setComponentError(null);
		dfdatefrom.setComponentError(null);
		dfdateto.setComponentError(null);
		tfnoofdays.setComponentError(null);
		cbappmanager.setComponentError(null);
		cbleavetype.setComponentError(null);
		if (cbEmployeeName.getValue() == null) {
			cbEmployeeName.setComponentError(new UserError(GERPErrorCodes.NULL_LVE_EMPNAME));
			errorFlag = false;
		}
		if (dfdatefrom.getValue() == null) {
			dfdatefrom.setComponentError(new UserError(GERPErrorCodes.NULL_LVE_DATEFRM));
			errorFlag = false;
		}
		if (dfdateto.getValue() == null) {
			dfdateto.setComponentError(new UserError(GERPErrorCodes.NULL_LVE_DATETO));
			errorFlag = false;
		}
		if ((tfnoofdays.getValue() == "") || tfnoofdays.getValue().trim().length() == 0) {
			tfnoofdays.setComponentError(new UserError(GERPErrorCodes.NULL_LVE_NOOFDAYS));
			errorFlag = false;
		}
		if (cbappmanager.getValue() == null) {
			cbappmanager.setComponentError(new UserError(GERPErrorCodes.NULL_LVE_MANAGER));
			errorFlag = false;
		}
		if (cbleavetype.getValue() == null) {
			cbleavetype.setComponentError(new UserError(GERPErrorCodes.NULL_LVE_LVETYPE));
			errorFlag = false;
		}
		return errorFlag;
	}
	
	public void resetField() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbDepartmentName.setValue(0L);
		cbEmployeeName.setComponentError(null);
		dfdatefrom.setValue(null);
		dfdateto.setValue(null);
		tfnoofdays.setReadOnly(false);
		tfnoofdays.setValue("0");
		tfnoofdays.setReadOnly(true);
		cbappmanager.setValue(null);
		cbleavetype.setValue(null);
		taLeaveReason.setValue("");
		cbhalfday.setValue(null);
		cbempstatus.setValue(cbempstatus.getItemIds().iterator().next());
		btnadd.setCaption("Add");
		btnadd.setStyleName("add");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		dfdatefrom.setComponentError(null);
		dfdateto.setComponentError(null);
		tfnoofdays.setComponentError(null);
		cbappmanager.setComponentError(null);
		cbleavetype.setComponentError(null);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
	}
	
	@Override
	public void resetFields() {
	}
	
	@Override
	protected void resetSearchDetails() {
		// TODO Auto-generated method stub
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
	
	@Override
	protected void cancelDetails() {
		// TODO Auto-generated method stub
	}
	
	private void diffdays() {
		try {
			Date startDate2 = (Date) dfdatefrom.getValue();
			Date endDate2 = (Date) dfdateto.getValue();
			long diff = endDate2.getTime() - startDate2.getTime();
			int diffDays = (int) (diff / (24 * 1000 * 60 * 60));
			check = 0d;
			check = (double) (diffDays + 1);
			tfnoofdays.setReadOnly(false);
			tfnoofdays.setValue(check.toString());
			tfnoofdays.setReadOnly(true);
			dfdatefrom.setComponentError(null);
		}
		catch (Exception e) {
		}
	}
}
