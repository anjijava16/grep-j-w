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
import com.vaadin.data.Item;
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
	private PayrollHdrService servicepayrollhdr = (PayrollHdrService) SpringContextHelper.getBean("PayrollHdr");
	private PayrollDetailsService servicePayrollDetails = (PayrollDetailsService) SpringContextHelper
			.getBean("payrolldetails");
	private EmployeeService serviceemployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private DepartmentService servicedepartment = (DepartmentService) SpringContextHelper.getBean("department");
	private payrollEarningsService servicepayrollEarnings = (payrollEarningsService) SpringContextHelper
			.getBean("payrollearnings");
	private PayrollDeductionsService servicePayrollDeductions = (PayrollDeductionsService) SpringContextHelper
			.getBean("PayrollDeductions");
	// form layout for input controls
	FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4, flempname, fldeptname;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	VerticalLayout hlPageRootContainter1 = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
	VerticalLayout hlPageRootContainter2 = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
	private HorizontalLayout hlSearchLayout = new HorizontalLayout();
	HorizontalLayout hlUserIPContainer1 = new HorizontalLayout();
	public HorizontalLayout hlPageHdrContainter = (HorizontalLayout) UI.getCurrent().getSession()
			.getAttribute("hlLayout");
	HorizontalLayout hlpayED = new HorizontalLayout();
	HorizontalLayout hlpaydet = new HorizontalLayout();
	HorizontalLayout hldetails = new HorizontalLayout();
	VerticalLayout vlearn = new VerticalLayout();
	VerticalLayout vldeduct = new VerticalLayout();
	VerticalLayout vlpaydet = new VerticalLayout();
	VerticalLayout vlstaff = new VerticalLayout();
	Label lblspec4 = new Label();
	// User Input Components
	private TextField tfpayrollid;
	private TextArea tfremarks;
	private PopupDateField processDt;
	private ComboBox cbempname, cbdeptname, cbStatus;
	// lists
	List<payrollEarningsDM> payslipList = null;
	List<PayrollDeductionsDM> payslipList1 = null;
	List<PayrollDetailsDM> payrollhdrList22 = null;
	// Bean container
	private BeanItemContainer<PayrollHdrDM> beanPayrollHdrDM = null;
	private BeanItemContainer<payrollEarningsDM> beanpayrollEarningsDM = null;
	private BeanItemContainer<PayrollDeductionsDM> beanPayrollDeductionsDM = null;
	// button declaration
	public Button btnviewED = new GERPButton("View Earning/Deduction", "searchbt", this);
	public Button btnclose = new GERPButton("Close", "cancelbt", this);
	// local tables
	private Table tblHdrDtl = new GERPTable();
	public Table tblMstScrSrchRslt1 = new Table();
	public Table tblMstScrSrchRslt2 = new Table();
	private BeanItemContainer<PayrollDetailsDM> beanPayrollDetailsDM = null;
	List<PayrollDetailsDM> payrolldetailList = null;
	// local variables declaration
	private Long companyid, payrollid=0L, employeeid=0L;
	private String userName, fullname, processdate;
	private int recordCnt = 0;
	// Initialize Logger
	private Logger logger = Logger.getLogger(Payslip.class);
	private static final long serialVersionUID = 1L;
	private String currencysymbol;
	private String payrolId;
	
	// Constructor
	public Payslip() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		currencysymbol = (String) UI.getCurrent().getSession().getAttribute("currencysymbol");
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Inside Payslip() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Painting Payslip UI");
		// payrollhdr Name text field
		tfpayrollid = new GERPTextField("Process Id");
		processDt = new PopupDateField("Process Date");
		processDt.setDateFormat("dd-MMM-yyyy");
		// Text area
		tfremarks = new GERPTextArea("Remarks");
		tfremarks.setInputPrompt("Enter Remarks");
		tfremarks.setWidth("250");
		tfremarks.setHeight("45");
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
						tfremarks.setRequired(true);
						tfremarks.setComponentError(null);
					} else if (cbStatus.getValue().equals("Approved")) {
						tfremarks.setRequired(false);
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
		cbdeptname = new GERPComboBox("Department Name");
		cbdeptname.setItemCaptionPropertyId("deptname");
		loadDepartmentList();
		cbdeptname.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object obj = event.getProperty().getValue();
				if (obj != null) {
					loadEmpList();
				}
			}
		});
		// employee name Combo box
		cbempname = new GERPComboBox("Employee Name");
		cbempname.setItemCaptionPropertyId("fullname");
		cbempname.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object obj = event.getProperty().getValue();
				if (obj != null) {
					assembleInputUserLayout((Long) cbempname.getValue());
				}
			}
		});
		cbempname.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				loadpaydetail();
			}
		});
		tblHdrDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblHdrDtl.isSelected(event.getItemId())) {
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
		loadhdrrslt();
		btnclose.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				cbempname.setValue(null);
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
				tblHdrDtl.setVisible(false);
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
		flcolumn1.addComponent(tfpayrollid);
		flcolumn2.addComponent(processDt);
		flcolumn3.addComponent(cbStatus);
		flcolumn4.addComponent(tfremarks);
		hlSearchLayout.addComponent(flcolumn1);
		hlSearchLayout.addComponent(flcolumn2);
		hlSearchLayout.addComponent(flcolumn3);
		hlSearchLayout.addComponent(flcolumn4);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setSizeUndefined();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		hlSrchContainer.setSizeFull();
		hlSrchContainer.setSpacing(true);
		// hlSrchContainer.setMargin(true);
	}
	
	private void assembleInputUserLayout(Long employeeid) {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Assembling search layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		vlSrchRsltContainer.removeAllComponents();
		hlpaydet.removeAllComponents();
		vlpaydet.removeAllComponents();
		tblMstScrSrchRslt.setVisible(false);
		tblHdrDtl.setVisible(true);
		hlPageHdrContainter.addComponent(btnviewED);
		hlPageHdrContainter.setComponentAlignment(btnviewED, Alignment.MIDDLE_RIGHT);
		tblHdrDtl.setWidth("100%");
		cbempname.setWidth("100%");
		// Formlayout2 components
		fldeptname = new GERPFormLayout();
		fldeptname.addComponent(cbdeptname);
		cbdeptname.setWidth("100%");
		flempname = new GERPFormLayout();
		flempname.addComponent(cbempname);
		hlpaydet.addComponent(fldeptname);
		hlpaydet.addComponent(flempname);
		hlpaydet.setSpacing(true);
		hlpaydet.setMargin(true);
		hlpaydet.setSizeUndefined();
		hlUserInputLayout.addComponent(hlpaydet);
		vlpaydet.addComponent(hldetails);
		vlpaydet.addComponent(tblHdrDtl);
		vlSrchRsltContainer.addComponent(vlpaydet);
		btnSave.setVisible(false);
		btnCancel.setCaption("Close");
		btnCancel.setVisible(true);
		btnEdit.setVisible(false);
		btnviewED.setEnabled(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	public void loadhdrrslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<PayrollHdrDM> payrollhdrList = new ArrayList<PayrollHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are ");
		payrollhdrList = servicepayrollhdr.getpayrollhdrlist(null, companyid, null, null, null, null);
		tblMstScrSrchRslt.setPageLength(12);
		recordCnt = payrollhdrList.size();
		beanPayrollHdrDM = new BeanItemContainer<PayrollHdrDM>(PayrollHdrDM.class);
		beanPayrollHdrDM.addAll(payrollhdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Got the Payroll result set");
		tblMstScrSrchRslt.setContainerDataSource(beanPayrollHdrDM);
		tblMstScrSrchRslt.setColumnAlignment("payrollid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("verifiedby", "No. of Records:" + recordCnt);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "payrollid", "processeddt", "processedby", "payhdrstatus",
				"verifieddt", "verifiedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Payroll Id", "Process Date ", "Processed By", "Status",
				"Verified Date", "Verified By" });
		tblMstScrSrchRslt.setColumnFooter("verifiedby", "No.of Records : " + recordCnt);
	}
	
	private void loadpaydetail() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search.pay det..");
		tblHdrDtl.removeAllItems();
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are");
		payrollhdrList22 = servicePayrollDetails.getpayrolldetailsList(companyid, null, payrollid,
				(Long) cbempname.getValue(), null);
		tblHdrDtl.setPageLength(14);
		recordCnt = payrollhdrList22.size();
		beanPayrollDetailsDM = new BeanItemContainer<PayrollDetailsDM>(PayrollDetailsDM.class);
		beanPayrollDetailsDM.addAll(payrollhdrList22);
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Got the Taxslap. result set");
		tblHdrDtl.setContainerDataSource(beanPayrollDetailsDM);
		tblHdrDtl.setColumnAlignment("totalearn", Align.RIGHT);
		tblHdrDtl.setColumnAlignment("totaldedn", Align.RIGHT);
		tblHdrDtl.setColumnAlignment("netpay", Align.RIGHT);
		tblHdrDtl.setVisibleColumns(new Object[] { "fullname", "totalearn", "totaldedn", "netpay" });
		tblHdrDtl.setColumnHeaders(new String[] { "Employee Name", "Total Earnings(₹)",
				"Total Deductions(₹)", "Net Pay(₹)" });
		tblHdrDtl.setColumnFooter("netpay", "No.of Records : " + recordCnt);
	}
	
	// load the employee list
	public void loadEmpList() {
		List<EmployeeDM> employeelist = serviceemployee.getEmployeeList(null, null, (Long) cbdeptname.getValue(),
				"Active", companyid, null, null, null, null, "F");
		BeanContainer<Long, EmployeeDM> beanLoadEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanLoadEmployee.setBeanIdProperty("employeeid");
		beanLoadEmployee.addAll(employeelist);
		cbempname.setContainerDataSource(beanLoadEmployee);
	}
	
	// load the department list
	public void loadDepartmentList() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "loading DepartmentList");
		List<DepartmentDM> departmentlist = servicedepartment.getDepartmentList(companyid, null, "Active", "F");
		departmentlist.add(new DepartmentDM(0L, "All Departments"));
		BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beanDepartment.setBeanIdProperty("deptid");
		beanDepartment.addAll(departmentlist);
		cbdeptname.setContainerDataSource(beanDepartment);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Resetting the UI controls");
		tfpayrollid.setReadOnly(false);
		tfpayrollid.setValue("");
		tfpayrollid.setReadOnly(true);
		processDt.setReadOnly(false);
		processDt.setValue(null);
		processDt.setReadOnly(true);
		cbStatus.setValue(null);
		tfremarks.setValue("");
		btnSave.setEnabled(false);
		tfremarks.setRequired(false);
		cbdeptname.setValue(0L);
		cbempname.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + " Invoking search");
		tblMstScrSrchRslt.setVisible(false);
		tblHdrDtl.setVisible(true);
		loadpaydetail();
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
		loadhdrrslt();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Validating Data ");
		tfremarks.setComponentError(null);
		Boolean errorFlag = false;
		if (cbStatus.getValue() != null) {
			if (cbStatus.getValue().equals("Rejected")) {
				if ((tfremarks.getValue() == null) || tfremarks.getValue().trim().length() == 0) {
					tfremarks.setRequired(true);
					tfremarks.setComponentError(new UserError(GERPErrorCodes.NULL_PAYROLL_HDR));
					tfremarks.setEnabled(true);
					errorFlag = true;
				}
			} else if (cbStatus.getValue().equals("Approved")) {
				tfremarks.setRequired(false);
				tfremarks.setComponentError(null);
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
		loadpaydetail();
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
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Editing the selected record");
		Item select = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (select != null) {
			PayrollHdrDM editHdrList = beanPayrollHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			payrollid = editHdrList.getPayrollid();
			payrolldetailList = servicePayrollDetails.getpayrolldetailsList(companyid, null, payrollid, null, null);
			processdate = editHdrList.getProcessedd();
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
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editHdrDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Editing the selected record");
		Item select = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (select != null) {
			PayrollHdrDM editHdrList = beanPayrollHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			payrollid = editHdrList.getPayrollid();
			if (editHdrList.getPayrollid() != null) {
				tfpayrollid.setReadOnly(false);
				tfpayrollid.setValue(select.getItemProperty("payrollid").getValue().toString());
				tfpayrollid.setReadOnly(true);
			}
			if (editHdrList.getProcesseddt() != null) {
				processDt.setReadOnly(false);
				processDt.setValue(editHdrList.getProcesseddt12());
				processDt.setReadOnly(true);
			}
			if (editHdrList.getVerifyremarks() != null) {
				tfremarks.setValue(select.getItemProperty("verifyremarks").getValue().toString());
			}
			if (editHdrList.getPayhdrstatus() != null) {
				cbStatus.setValue(select.getItemProperty("payhdrstatus").getValue());
			}
		}
	}
	
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt2.removeAllItems();
		payslipList = new ArrayList<payrollEarningsDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are");
		payslipList = servicepayrollEarnings.getpayrollearningsList(companyid, null, payrollid, employeeid, null);
		recordCnt = payslipList.size();
		beanpayrollEarningsDM = new BeanItemContainer<payrollEarningsDM>(payrollEarningsDM.class);
		beanpayrollEarningsDM.addAll(payslipList);
		Long sum = 0L;
		for (payrollEarningsDM obj : payslipList) {
			if (obj.getEarnAmount() != null) {
				sum = sum + obj.getEarnAmount();
			}
		}
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Got the payslip result set");
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
	
	public void loadtable() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt1.removeAllItems();
		List<PayrollDeductionsDM> payslipList1 = new ArrayList<PayrollDeductionsDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are");
		payslipList1 = servicePayrollDeductions.getpayrolldeductionsList(companyid, null, payrollid, employeeid, null);
		recordCnt = payslipList1.size();
		beanPayrollDeductionsDM = new BeanItemContainer<PayrollDeductionsDM>(PayrollDeductionsDM.class);
		beanPayrollDeductionsDM.addAll(payslipList1);
		Long sum1 = 0L;
		for (PayrollDeductionsDM obj : payslipList1) {
			if (obj.getDedamount() != null) {
				sum1 = sum1 + obj.getDedamount();
			}
		}
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Got the payslip result set");
		tblMstScrSrchRslt1.setWidth("100%");
		tblMstScrSrchRslt1.setContainerDataSource(beanPayrollDeductionsDM);
		tblMstScrSrchRslt1.setColumnAlignment("dedamount", Align.RIGHT);
		tblMstScrSrchRslt1.setVisibleColumns(new Object[] { "deddesc", "dedamount" });
		tblMstScrSrchRslt1
				.setColumnHeaders(new String[] { "Deduction Type", "Deduction Amount(₹)" });
		tblMstScrSrchRslt1.setColumnFooter("dedamount", "No.of Records : " + recordCnt);
		tblMstScrSrchRslt1.setColumnFooter("deddesc", "Total");
		tblMstScrSrchRslt1.setColumnFooter("dedamount", " " + sum1);
		tblMstScrSrchRslt1.setColumnWidth("deddesc", 355);
		tblMstScrSrchRslt1.setColumnWidth("dedamount", 205);
		tblMstScrSrchRslt1.setSizeFull();
		tblMstScrSrchRslt1.setPageLength(8);
		tblMstScrSrchRslt1.setFooterVisible(true);
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
		if (tfremarks.getValue() != null) {
			payrollHdrobj.setVerifyremarks((String) tfremarks.getValue().toString());
		}
		if (cbStatus.getValue() != null) {
			payrollHdrobj.setPayhdrstatus((String) cbStatus.getValue());
		}
		payrollHdrobj.setVerifiedby(userName);
		payrollHdrobj.setVerifieddt(DateUtils.getcurrentdate());
		btnSave.setEnabled(false);
		servicepayrollhdr.saveAndUpdate(payrollHdrobj);
		resetFields();
		loadhdrrslt();
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
		loadhdrrslt();
		tblHdrDtl.setVisible(false);
		hlCmdBtnLayout.setVisible(true);
		resetFields();
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editpayED() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Editing the selected record");
		Item select = tblHdrDtl.getItem(tblHdrDtl.getValue());
		if (select != null) {
			PayrollDetailsDM editdetList = beanPayrollDetailsDM.getItem(tblHdrDtl.getValue()).getBean();
			payrollid = editdetList.getPayrollid();
			employeeid = editdetList.getEmployeeid();
			if (editdetList.getEmployeeid() != null) {
				cbempname.setValue(select.getItemProperty("employeeid").getValue().toString());
			}
			payslipList = servicepayrollEarnings.getpayrollearningsList(companyid, null, payrollid, employeeid, null);
			payslipList1 = servicePayrollDeductions.getpayrolldeductionsList(companyid, null, payrollid, employeeid,
					null);
		}
	}
	
	private void viewearded() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "view Earnings Deductions");
		vlSrchRsltContainer.removeAllComponents();
		vlpaydet.removeAllComponents();
		hlCmdBtnLayout.setVisible(false);
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Staff name>>>>");
		Item select = tblHdrDtl.getItem(tblHdrDtl.getValue());
		if (select != null) {
			PayrollDetailsDM editdetList = beanPayrollDetailsDM.getItem(tblHdrDtl.getValue()).getBean();
			fullname = editdetList.getFullname();
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
			loadtable();
			loadSrchRslt();
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
		loadpaydetail();
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
			System.out.println("processid-->"+payrollid);
			System.out.println("employeeid-->"+employeeid);
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