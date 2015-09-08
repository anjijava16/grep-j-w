/**
 * File Name 		: Payslip.java 
 * Description 		: this class is used for add/edit Payroll details. 
 * Author 			: Arun jeyaraj R
 * Date 			: sep15, 2014
 * Modification 	:
 * Modified By 		: Arun jeyaraj R 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           sep15      	  Arun jeyaraj R		          Intial Version
 */
package com.gnts.hcm.rpt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.PayrollDeductionsDM;
import com.gnts.hcm.domain.txn.PayrollDetailsDM;
import com.gnts.hcm.domain.txn.PayrollHdrDM;
import com.gnts.hcm.domain.txn.payrollEarningsDM;
import com.gnts.hcm.service.txn.PayrollDeductionsService;
import com.gnts.hcm.service.txn.PayrollDetailsService;
import com.gnts.hcm.service.txn.PayrollHdrService;
import com.gnts.hcm.service.txn.payrollEarningsService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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

public class Payslip extends BaseTransUI {
	private PayrollHdrService servicePayrollHdr = (PayrollHdrService) SpringContextHelper.getBean("PayrollHdr");
	private PayrollDetailsService servicePayrollDetails = (PayrollDetailsService) SpringContextHelper
			.getBean("payrolldetails");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private DepartmentService serviceDepartment = (DepartmentService) SpringContextHelper.getBean("department");
	private payrollEarningsService servicePayrollEarnings = (payrollEarningsService) SpringContextHelper
			.getBean("payrollearnings");
	private PayrollDeductionsService servicePayrollDeductions = (PayrollDeductionsService) SpringContextHelper
			.getBean("PayrollDeductions");
	// form layout for input controls
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4, flempname, fldeptname;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlSearchLayout = new HorizontalLayout();
	private HorizontalLayout hlPageHdrContainter = (HorizontalLayout) UI.getCurrent().getSession()
			.getAttribute("hlLayout");
	private HorizontalLayout hlpayED = new HorizontalLayout();
	private HorizontalLayout hlpaydet = new HorizontalLayout();
	private HorizontalLayout hldetails = new HorizontalLayout();
	private VerticalLayout vlearn = new VerticalLayout();
	private VerticalLayout vldeduct = new VerticalLayout();
	private VerticalLayout vlpaydet = new VerticalLayout();
	private VerticalLayout vlstaff = new VerticalLayout();
	private Label lblspec4 = new Label();
	// User Input Components
	private TextField tfPayrollId;
	private TextArea tfRemarks;
	private PopupDateField dfProcess;
	private ComboBox cbEmployee, cbDepartment, cbStatus;
	// lists
	private List<payrollEarningsDM> listPayEarnings = null;
	// Bean container
	private BeanItemContainer<PayrollHdrDM> beanPayrollHdrDM = null;
	// button declaration
	private Button btnviewED = new GERPButton("View Earning/Deduction", "searchbt", this);
	private Button btnclose = new GERPButton("Close", "cancelbt", this);
	// local tables
	private Table tblPayDetails = new GERPTable();
	private Table tblMstScrSrchRslt1 = new Table();
	private Table tblMstScrSrchRslt2 = new Table();
	private BeanItemContainer<PayrollDetailsDM> beanPayrollDetailsDM = null;
	// local variables declaration
	private Long companyid, payrollid = 0L, employeeid = 0L;
	private String userName, fullname, processdate;
	private int recordCnt = 0;
	// Initialize Logger
	private Logger logger = Logger.getLogger(Payslip.class);
	private static final long serialVersionUID = 1L;
	private String payrolId;
	
	// Constructor
	public Payslip() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Inside Payslip() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Painting Payslip UI");
		// payrollhdr Name text field
		tfPayrollId = new GERPTextField("Process Id");
		dfProcess = new PopupDateField("Process Date");
		dfProcess.setDateFormat("dd-MMM-yyyy");
		// Text area
		tfRemarks = new GERPTextArea("Remarks");
		tfRemarks.setInputPrompt("Enter Remarks");
		tfRemarks.setWidth("250");
		tfRemarks.setHeight("45");
		// payrollhdr status combo box
		cbStatus = new ComboBox("Status");
		cbStatus = new GERPComboBox("Status", BASEConstants.T_HCM_PAYROLL_DTL, BASEConstants.PAYROLL_STATUS);
		btnReset.setVisible(false);
		btnAdd.setEnabled(false);
		hlPageHdrContainter.addComponent(btnviewED);
		hlPageHdrContainter.setComponentAlignment(btnviewED, Alignment.MIDDLE_LEFT);
		btnSearch.setVisible(false);
		// addValueChangeListener
		cbStatus.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbStatus.getValue() != null) {
					if (cbStatus.getValue().equals("Rejected")) {
						tfRemarks.setRequired(true);
						tfRemarks.setComponentError(null);
					} else if (cbStatus.getValue().equals("Approved")) {
						tfRemarks.setRequired(false);
					}
				}
			}
		});
		// addItemClickListener
		tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
					btnAdd.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					PayrollHdrDM editHdrList = new PayrollHdrDM();
					editHdrList = beanPayrollHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
					if (editHdrList.getPayhdrstatus().equals("Approved")) {
						btnEdit.setEnabled(false);
						btnAdd.setEnabled(true);
					} else if (editHdrList.getPayhdrstatus().equals("Rejected")) {
						btnEdit.setEnabled(false);
						btnAdd.setEnabled(false);
					} else if (editHdrList.getPayhdrstatus().equals("Processing")) {
						btnEdit.setEnabled(true);
						btnAdd.setEnabled(true);
					}
				}
				resetFields();
			}
		});
		// department Combo box
		cbDepartment = new GERPComboBox("Department Name");
		cbDepartment.setItemCaptionPropertyId("deptname");
		loadDepartmentList();
		cbDepartment.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object obj = event.getProperty().getValue();
				if (obj != null) {
					loadEmpList();
				}
			}
		});
		// employee name Combo box
		cbEmployee = new GERPComboBox("Employee Name");
		cbEmployee.setItemCaptionPropertyId("fullname");
		cbEmployee.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object obj = event.getProperty().getValue();
				if (obj != null) {
					assembleInputUserLayout((Long) cbEmployee.getValue());
				}
			}
		});
		cbEmployee.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				loadPayrollDetail();
			}
		});
		tblPayDetails.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblPayDetails.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
					btnAdd.setEnabled(false);
					btnviewED.setEnabled(false);
				} else {
					btnEdit.setEnabled(true);
					btnAdd.setEnabled(true);
					btnviewED.setEnabled(true);
					btnviewED.setEnabled(true);
				}
			}
		});
		assembleSearchLayout();
		resetFields();
		loadPayrollHdr();
		btnclose.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				cbEmployee.setValue(null);
				hlCmdBtnLayout.setVisible(false);
				assembleInputUserLayout(employeeid);
				closeED();
			}
		});
		btnviewED.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				hlUserIPContainer.removeAllComponents();
				hlUserInputLayout.removeAllComponents();
				hlSrchContainer.setVisible(false);
				tblPayDetails.setVisible(false);
				tblMstScrSrchRslt1.setVisible(true);
				tblMstScrSrchRslt2.setVisible(true);
				viewearded();
				hlUserIPContainer.addComponent(hlUserInputLayout);
			}
		});
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Assembling search layout");
		hlSearchLayout.removeAllComponents();
		btnAdd.setCaption("View");
		btnAdd.setVisible(true);
		btnDownload.setVisible(true);
		btnSave.setVisible(true);
		btnSave.setEnabled(false);
		btnCancel.setVisible(false);
		btnviewED.setVisible(false);
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		// Add components for Assemble Searcch Layout
		// Formlayout1 components
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn4 = new FormLayout();
		flcolumn1.addComponent(tfPayrollId);
		flcolumn2.addComponent(dfProcess);
		flcolumn3.addComponent(cbStatus);
		flcolumn4.addComponent(tfRemarks);
		hlSearchLayout.addComponent(flcolumn1);
		hlSearchLayout.addComponent(flcolumn2);
		hlSearchLayout.addComponent(flcolumn3);
		hlSearchLayout.addComponent(flcolumn4);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setSizeUndefined();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		hlSrchContainer.setSizeFull();
		hlSrchContainer.setSpacing(true);
	}
	
	private void assembleInputUserLayout(Long employeeid) {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Assembling search layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		vlSrchRsltContainer.removeAllComponents();
		hlpaydet.removeAllComponents();
		vlpaydet.removeAllComponents();
		tblMstScrSrchRslt.setVisible(false);
		tblPayDetails.setVisible(true);
		hlPageHdrContainter.addComponent(btnviewED);
		hlPageHdrContainter.setComponentAlignment(btnviewED, Alignment.MIDDLE_RIGHT);
		tblPayDetails.setWidth("100%");
		cbEmployee.setWidth("100%");
		// Formlayout2 components
		fldeptname = new GERPFormLayout();
		fldeptname.addComponent(cbDepartment);
		cbDepartment.setWidth("100%");
		flempname = new GERPFormLayout();
		flempname.addComponent(cbEmployee);
		hlpaydet.addComponent(fldeptname);
		hlpaydet.addComponent(flempname);
		hlpaydet.setSpacing(true);
		hlpaydet.setMargin(true);
		hlpaydet.setSizeUndefined();
		hlUserInputLayout.addComponent(hlpaydet);
		vlpaydet.addComponent(hldetails);
		vlpaydet.addComponent(tblPayDetails);
		vlSrchRsltContainer.addComponent(vlpaydet);
		btnSave.setVisible(false);
		btnCancel.setCaption("Close");
		btnCancel.setVisible(true);
		btnEdit.setVisible(false);
		btnviewED.setEnabled(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	private void loadPayrollHdr() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<PayrollHdrDM> list = new ArrayList<PayrollHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are ");
			list = servicePayrollHdr.getpayrollhdrlist(null, companyid, null, null, null, null);
			tblMstScrSrchRslt.setPageLength(12);
			recordCnt = list.size();
			beanPayrollHdrDM = new BeanItemContainer<PayrollHdrDM>(PayrollHdrDM.class);
			beanPayrollHdrDM.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Got the Payroll result set");
			tblMstScrSrchRslt.setContainerDataSource(beanPayrollHdrDM);
			tblMstScrSrchRslt.setColumnAlignment("payrollid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("verifiedby", "No. of Records:" + recordCnt);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "payrollid", "processeddt", "processedby",
					"payhdrstatus", "verifieddt", "verifiedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Payroll Id", "Process Date ", "Processed By", "Status",
					"Verified Date", "Verified By" });
			tblMstScrSrchRslt.setColumnFooter("verifiedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadPayrollDetail() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search.pay det..");
			tblPayDetails.removeAllItems();
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are");
			List<PayrollDetailsDM> list = servicePayrollDetails.getpayrolldetailsList(companyid, null,
					payrollid, (Long) cbEmployee.getValue(), null);
			tblPayDetails.setPageLength(14);
			recordCnt = list.size();
			beanPayrollDetailsDM = new BeanItemContainer<PayrollDetailsDM>(PayrollDetailsDM.class);
			beanPayrollDetailsDM.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Got the Taxslap. result set");
			tblPayDetails.setContainerDataSource(beanPayrollDetailsDM);
			tblPayDetails.setColumnAlignment("totalearn", Align.RIGHT);
			tblPayDetails.setColumnAlignment("totaldedn", Align.RIGHT);
			tblPayDetails.setColumnAlignment("netpay", Align.RIGHT);
			tblPayDetails.setVisibleColumns(new Object[] { "fullname", "totalearn", "totaldedn", "netpay" });
			tblPayDetails.setColumnHeaders(new String[] { "Employee Name", "Total Earnings(₹)", "Total Deductions(₹)",
					"Net Pay(₹)" });
			tblPayDetails.setColumnFooter("netpay", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// load the employee list
	private void loadEmpList() {
		try {
			BeanContainer<Long, EmployeeDM> beanLoadEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanLoadEmployee.setBeanIdProperty("employeeid");
			beanLoadEmployee.addAll(serviceEmployee.getEmployeeList(null, null, (Long) cbDepartment.getValue(),
					"Active", companyid, null, null, null, null, "P"));
			cbEmployee.setContainerDataSource(beanLoadEmployee);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// load the department list
	private void loadDepartmentList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "loading DepartmentList");
			List<DepartmentDM> departmentlist = serviceDepartment.getDepartmentList(companyid, null, "Active", "F");
			departmentlist.add(new DepartmentDM(0L, "All Departments"));
			BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
			beanDepartment.setBeanIdProperty("deptid");
			beanDepartment.addAll(departmentlist);
			cbDepartment.setContainerDataSource(beanDepartment);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Resetting the UI controls");
		tfPayrollId.setReadOnly(false);
		tfPayrollId.setValue("");
		tfPayrollId.setReadOnly(true);
		dfProcess.setReadOnly(false);
		dfProcess.setValue(null);
		dfProcess.setReadOnly(true);
		cbStatus.setValue(null);
		tfRemarks.setValue("");
		btnSave.setEnabled(false);
		tfRemarks.setRequired(false);
		cbDepartment.setValue(0L);
		cbEmployee.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + " Invoking search");
		tblMstScrSrchRslt.setVisible(false);
		tblPayDetails.setVisible(true);
		loadPayrollDetail();
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
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		btnSearch.setVisible(false);
		resetFields();
		loadPayrollHdr();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Validating Data ");
		tfRemarks.setComponentError(null);
		Boolean errorFlag = false;
		if (cbStatus.getValue() != null) {
			if (cbStatus.getValue().equals("Rejected")) {
				if ((tfRemarks.getValue() == null) || tfRemarks.getValue().trim().length() == 0) {
					tfRemarks.setRequired(true);
					tfRemarks.setComponentError(new UserError(GERPErrorCodes.NULL_PAYROLL_HDR));
					tfRemarks.setEnabled(true);
					errorFlag = true;
				}
			} else if (cbStatus.getValue().equals("Approved")) {
				tfRemarks.setRequired(false);
				tfRemarks.setComponentError(null);
				errorFlag = false;
			}
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void addDetails() {
		// TODO Auto-generated method stub
		hlUserInputLayout.removeAllComponents();
		assembleInputUserLayout(employeeid);
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tblMstScrSrchRslt.setVisible(false);
		btnSave.setVisible(false);
		btnCancel.setVisible(true);
		btnEdit.setVisible(false);
		btnclose.setVisible(false);
		btnviewED.setVisible(true);
		hlCmdBtnLayout.setVisible(false);
		resetFields();
		editdtl();
		loadPayrollDetail();
	}
	
	@Override
	protected void editDetails() {
		// TODO Auto-generated method stub
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		btnCancel.setVisible(false);
		btnEdit.setVisible(true);
		btnAdd.setVisible(true);
		cbStatus.setRequired(false);
		btnSave.setEnabled(true);
		editHdrDetails();
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editdtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Editing the selected record");
			if (tblMstScrSrchRslt.getValue() != null) {
				PayrollHdrDM payrollHdr = beanPayrollHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				payrollid = payrollHdr.getPayrollid();
				processdate = payrollHdr.getProcessedd();
				Label lblspec2 = new Label("Payroll Details");
				lblspec2.setStyleName("h4");
				hldetails.addComponent(lblspec2);
				hldetails.addComponent(lblspec4);
				hldetails.setSpacing(true);
				hldetails.setWidth("100%");
				lblspec4.setValue("Processed Month :" + processdate);
				lblspec4.setStyleName("h4");
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editHdrDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Editing the selected record");
			if (tblMstScrSrchRslt.getValue() != null) {
				PayrollHdrDM payrollHdrDM = beanPayrollHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				payrollid = payrollHdrDM.getPayrollid();
				if (payrollHdrDM.getPayrollid() != null) {
					tfPayrollId.setReadOnly(false);
					tfPayrollId.setValue(payrollHdrDM.getPayrollid().toString());
					tfPayrollId.setReadOnly(true);
				}
				if (payrollHdrDM.getProcesseddt() != null) {
					dfProcess.setReadOnly(false);
					dfProcess.setValue(payrollHdrDM.getProcesseddt12());
					dfProcess.setReadOnly(true);
				}
				if (payrollHdrDM.getVerifyremarks() != null) {
					tfRemarks.setValue(payrollHdrDM.getVerifyremarks());
				}
				if (payrollHdrDM.getPayhdrstatus() != null) {
					cbStatus.setValue(payrollHdrDM.getPayhdrstatus());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt2.removeAllItems();
			listPayEarnings = new ArrayList<payrollEarningsDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are");
			listPayEarnings = servicePayrollEarnings.getpayrollearningsList(companyid, null, payrollid, employeeid,
					null);
			recordCnt = listPayEarnings.size();
			BeanItemContainer<payrollEarningsDM> beanpayrollEarningsDM = new BeanItemContainer<payrollEarningsDM>(
					payrollEarningsDM.class);
			beanpayrollEarningsDM.addAll(listPayEarnings);
			Long sum = 0L;
			for (payrollEarningsDM obj : listPayEarnings) {
				if (obj.getEarnAmount() != null) {
					sum = sum + obj.getEarnAmount();
				}
			}
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Got the payslip result set");
			tblMstScrSrchRslt2.setWidth("100%");
			tblMstScrSrchRslt2.setContainerDataSource(beanpayrollEarningsDM);
			tblMstScrSrchRslt2.setColumnAlignment("earnAmount", Align.RIGHT);
			tblMstScrSrchRslt2.setVisibleColumns(new Object[] { "earnDesc", "earnAmount" });
			tblMstScrSrchRslt2.setColumnHeaders(new String[] { "Earning Type", "Earning Amount(₹)" });
			tblMstScrSrchRslt2.setColumnFooter("earnDesc", "Total");
			tblMstScrSrchRslt2.setColumnFooter("earnAmount", "" + sum);
			tblMstScrSrchRslt2.setColumnWidth("earnDesc", 355);
			tblMstScrSrchRslt2.setFooterVisible(true);
			tblMstScrSrchRslt2.setColumnWidth("earnAmount", 205);
			tblMstScrSrchRslt2.setSizeFull();
			tblMstScrSrchRslt2.setPageLength(8);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadPayDeductions() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt1.removeAllItems();
			List<PayrollDeductionsDM> list = new ArrayList<PayrollDeductionsDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are");
			list = servicePayrollDeductions.getpayrolldeductionsList(companyid, null, payrollid, employeeid,
					null);
			recordCnt = list.size();
			BeanItemContainer<PayrollDeductionsDM> beanPayrollDeductionsDM = new BeanItemContainer<PayrollDeductionsDM>(
					PayrollDeductionsDM.class);
			beanPayrollDeductionsDM.addAll(list);
			Long sum1 = 0L;
			for (PayrollDeductionsDM obj : list) {
				if (obj.getDedamount() != null) {
					sum1 = sum1 + obj.getDedamount();
				}
			}
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Got the payslip result set");
			tblMstScrSrchRslt1.setWidth("100%");
			tblMstScrSrchRslt1.setContainerDataSource(beanPayrollDeductionsDM);
			tblMstScrSrchRslt1.setColumnAlignment("dedamount", Align.RIGHT);
			tblMstScrSrchRslt1.setVisibleColumns(new Object[] { "deddesc", "dedamount" });
			tblMstScrSrchRslt1.setColumnHeaders(new String[] { "Deduction Type", "Deduction Amount(₹)" });
			tblMstScrSrchRslt1.setColumnFooter("dedamount", "No.of Records : " + recordCnt);
			tblMstScrSrchRslt1.setColumnFooter("deddesc", "Total");
			tblMstScrSrchRslt1.setColumnFooter("dedamount", " " + sum1);
			tblMstScrSrchRslt1.setColumnWidth("deddesc", 355);
			tblMstScrSrchRslt1.setColumnWidth("dedamount", 205);
			tblMstScrSrchRslt1.setSizeFull();
			tblMstScrSrchRslt1.setPageLength(8);
			tblMstScrSrchRslt1.setFooterVisible(true);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Saving Data... ");
		PayrollHdrDM payrollHdrobj = new PayrollHdrDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			payrollHdrobj = beanPayrollHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		payrollHdrobj.setCompanyid(companyid);
		if (tfRemarks.getValue() != null) {
			payrollHdrobj.setVerifyremarks((String) tfRemarks.getValue().toString());
		}
		if (cbStatus.getValue() != null) {
			payrollHdrobj.setPayhdrstatus((String) cbStatus.getValue());
		}
		payrollHdrobj.setVerifiedby(userName);
		payrollHdrobj.setVerifieddt(DateUtils.getcurrentdate());
		btnSave.setEnabled(false);
		servicePayrollHdr.saveAndUpdate(payrollHdrobj);
		resetFields();
		loadPayrollHdr();
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Getting audit record for Payroll ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_PAYROLL_DTL);
		UI.getCurrent().getSession().setAttribute("audittablepk", payrolId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Canceling action ");
		hlUserInputLayout.removeAllComponents();
		hlSrchContainer.removeAllComponents();
		assembleSearchLayout();
		hldetails.removeAllComponents();
		vlpaydet.removeAllComponents();
		btnEdit.setVisible(true);
		btnAdd.setVisible(true);
		btnReset.setVisible(false);
		btnSearch.setVisible(false);
		btnAdd.setEnabled(false);
		btnSave.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		editdtl();
		loadPayrollHdr();
		tblPayDetails.setVisible(false);
		hlCmdBtnLayout.setVisible(true);
		resetFields();
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editpayED() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Editing the selected record");
			if (tblPayDetails.getValue() != null) {
				PayrollDetailsDM editdetList = beanPayrollDetailsDM.getItem(tblPayDetails.getValue()).getBean();
				payrollid = editdetList.getPayrollid();
				employeeid = editdetList.getEmployeeid();
				if (editdetList.getEmployeeid() != null) {
					cbEmployee.setValue(editdetList.getEmployeeid());
				}
				listPayEarnings = servicePayrollEarnings.getpayrollearningsList(companyid, null, payrollid, employeeid,
						null);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void viewearded() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "view Earnings Deductions");
			vlSrchRsltContainer.removeAllComponents();
			vlpaydet.removeAllComponents();
			hlCmdBtnLayout.setVisible(false);
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Staff name>>>>");
			if (tblPayDetails.getValue() != null) {
				PayrollDetailsDM payrollDetail = beanPayrollDetailsDM.getItem(tblPayDetails.getValue()).getBean();
				fullname = payrollDetail.getFullname();
				Label lblspec3 = new Label();
				lblspec3.setValue("Employee Name :" + fullname);
				lblspec3.addStyleName("h2");
				Label lblspec = new Label("Earnings Details");
				lblspec.setStyleName("h4");
				vlearn.addComponent(lblspec);
				vlearn.addComponent(tblMstScrSrchRslt2);
				Label lblspec1 = new Label("Deductions Details");
				lblspec1.setStyleName("h4");
				vldeduct.addComponent(lblspec1);
				vldeduct.addComponent(tblMstScrSrchRslt1);
				hlpayED.addComponent(vlearn);
				hlpayED.addComponent(vldeduct);
				hlpayED.setSpacing(true);
				vlstaff.addComponent(lblspec3);
				vlstaff.addComponent(hlpayED);
				vlstaff.setSpacing(true);
				hlUserInputLayout.addComponent(vlstaff);
				btnCancel.setVisible(false);
				btnviewED.setVisible(false);
				btnclose.setVisible(true);
				vlpaydet.removeAllComponents();
				hlPageHdrContainter.addComponent(btnclose);
				hlPageHdrContainter.setComponentAlignment(btnclose, Alignment.MIDDLE_RIGHT);
				editpayED();
				loadPayDeductions();
				loadSrchRslt();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void closeED() {
		hlpayED.removeAllComponents();
		hldetails.removeAllComponents();
		hlUserInputLayout.setVisible(true);
		vldeduct.removeAllComponents();
		vlearn.removeAllComponents();
		vlstaff.removeAllComponents();
		hlUserInputLayout.removeAllComponents();
		assembleInputUserLayout(employeeid);
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tblMstScrSrchRslt.setVisible(false);
		btnSave.setVisible(false);
		btnCancel.setVisible(true);
		btnEdit.setVisible(false);
		btnclose.setVisible(false);
		btnviewED.setVisible(true);
		hlPageHdrContainter.addComponent(btnviewED);
		hlPageHdrContainter.setComponentAlignment(btnviewED, Alignment.MIDDLE_LEFT);
		hlCmdBtnLayout.setVisible(false);
		resetFields();
		editdtl();
		loadPayrollDetail();
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, String> parameterMap = new HashMap<String, String>();
			System.out.println("processid-->" + payrollid);
			System.out.println("employeeid-->" + employeeid);
			parameterMap.put("processid", payrollid.toString());
			parameterMap.put("staffid", employeeid.toString());
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/payslip"); // payslip is the name of my jasper
			// file.
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				statement.close();
				Database.close(connection);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}