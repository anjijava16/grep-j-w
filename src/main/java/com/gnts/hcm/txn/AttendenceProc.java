/**
 * File Name	:	AttendenceProc.java
 * Description	:	This Screen Purpose for Modify the Attendence Process Details.Add the Attendence process should be directly added in DB.
 * Author		:	Mahaboob Subahan J
 * Date			:	Oct 09, 2014
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
 * 0.1          Oct 09, 2014   	Mahaboob Subahan J		Initial Version		
 * 
 */
package com.gnts.hcm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.PayPeriodDM;
import com.gnts.hcm.domain.txn.AttendenceProcDM;
import com.gnts.hcm.service.mst.PayPeriodService;
import com.gnts.hcm.service.txn.AttendenceProcService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AttendenceProc extends BaseUI {
	private static final long serialVersionUID = 1118829691766246544L;
	private Logger logger = Logger.getLogger(AttendenceProc.class);
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private PayPeriodService servicePayPeriod = (PayPeriodService) SpringContextHelper.getBean("PayPeriod");
	private AttendenceProcService serviceAttendanceProcess = (AttendenceProcService) SpringContextHelper
			.getBean("AttendenceProc");
	private Button btnsaveAttenProc = new GERPButton("Add", "add", this);
	private BeanItemContainer<AttendenceProcDM> beanAttendenceProcDM = null;
	private List<AttendenceProcDM> attendProcList = new ArrayList<AttendenceProcDM>();
	private Long attProcId;
	// Attendance Process Component Declaration
	private ComboBox cbPayPeried, cbBranch, cbEmployeeName;
	private TextField tfProcessPeriod;
	private Button btnSearchStaff, btnAttendanceProc;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private PayPeriodDM payPeriodList;
	private String userName;
	private Long companyId, payPeriodId = null;
	private CallableStatement statement = null;
	private String startDate = null;
	private String endDate = null;
	private String funationStatus;
	private String errorMsg;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public AttendenceProc() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside Material() constructor");
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Painting Attendance Process UI");
		// Material Components Definition
		cbPayPeried = new GERPComboBox("Pay Period");
		cbPayPeried.setImmediate(true);
		cbPayPeried.setNullSelectionAllowed(false);
		cbPayPeried.setWidth("130");
		cbPayPeried.setItemCaptionPropertyId("periodName");
		cbPayPeried.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				if (itemId != null) {
					BeanItem<?> item = (BeanItem<?>) cbPayPeried.getItem(itemId);
					payPeriodList = (PayPeriodDM) item.getBean();
					payPeriodId = payPeriodList.getPayPeriodId();
					loadStartandEndDates();
					// loadEmployeeList();
					// loadPayPeriod();
				}
			}
		});
		btnsaveAttenProc.setCaption("Save");
		tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
					tblMstScrSrchRslt.setImmediate(true);
					btnsaveAttenProc.setStyleName("savebt");
					resetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnsaveAttenProc.setStyleName("savebt");
					readonlyfalse();
					editClient();
					readonlytrue();
				}
			}
		});
		hlPageHdrContainter.addComponent(btnsaveAttenProc);
		hlPageHdrContainter.setComponentAlignment(btnsaveAttenProc, Alignment.MIDDLE_RIGHT);
		tfProcessPeriod = new GERPTextField("Process Period");
		tfProcessPeriod.setWidth("170");
		tfProcessPeriod.setReadOnly(true);
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setWidth("170");
		cbBranch.setNullSelectionAllowed(false);
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setImmediate(true);
		cbBranch.setValue(0L);
		cbBranch.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				if (itemId != null) {
					loadEmployeeList();
				}
			}
		});
		btnsaveAttenProc.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				saveattapprove();
			}
		});
		cbEmployeeName = new GERPComboBox("Employee Name");
		cbEmployeeName.setItemCaptionPropertyId("firstname");
		cbEmployeeName.setWidth("200");
		cbEmployeeName.setImmediate(true);
		cbEmployeeName.setNullSelectionAllowed(false);
		loadEmployeeList();
		btnSearchStaff = new GERPButton("Search Employee", "searchbt", this);
		btnAttendanceProc = new GERPButton("Run Attendance Process", "savebt", this);
		btnAttendanceProc.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				loadAttendenceProcess();
			}
		});
		btnSearch.setVisible(false);
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadAttendanceProcessBranchList();
		loadPayPeriod();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// Remove all components in User Search Layout
		hlSearchLayout.removeAllComponents();
		cbPayPeried.setRequired(true);
		cbBranch.setRequired(true);
		vlSrchRsltContainer.setVisible(true);
		hlCmdBtnLayout.setVisible(false);
		// Add components for Search Layout
		FormLayout formLayout1 = new FormLayout();
		FormLayout formLayout2 = new FormLayout();
		FormLayout formLayout3 = new FormLayout();
		FormLayout formLayout4 = new FormLayout();
		formLayout1.addComponent(cbPayPeried);
		formLayout1.setSpacing(true);
		formLayout2.addComponent(tfProcessPeriod);
		formLayout2.setSpacing(true);
		formLayout3.addComponent(cbBranch);
		formLayout3.setSpacing(true);
		formLayout4.addComponent(cbEmployeeName);
		formLayout4.setSpacing(true);
		HorizontalLayout hlAttendanceProc1 = new HorizontalLayout();
		hlAttendanceProc1.addComponent(formLayout1);
		hlAttendanceProc1.addComponent(formLayout2);
		hlAttendanceProc1.addComponent(formLayout3);
		hlAttendanceProc1.addComponent(formLayout4);
		hlAttendanceProc1.addComponent(btnSearchStaff);
		hlAttendanceProc1.setComponentAlignment(btnSearchStaff, Alignment.MIDDLE_RIGHT);
		hlAttendanceProc1.setSpacing(true);
		HorizontalLayout hlAttendanceProc2 = new HorizontalLayout();
		hlAttendanceProc2.addComponent(btnAttendanceProc);
		hlAttendanceProc2.setSizeFull();
		hlAttendanceProc2.setComponentAlignment(btnAttendanceProc, Alignment.MIDDLE_RIGHT);
		VerticalLayout vlAttendanceProc1 = new VerticalLayout();
		vlAttendanceProc1.addComponent(hlAttendanceProc1);
		vlAttendanceProc1.addComponent(hlAttendanceProc2);
		hlSearchLayout.addComponent(vlAttendanceProc1);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
		btnSearch.setVisible(false);
	}
	
	/*
	 * loadAttendanceProcessBranchList()-->this function is used for load the branch list to branch combo box
	 */
	private void loadAttendanceProcessBranchList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Branch Search...");
		List<BranchDM> branchList = serviceBranch.getBranchList(null, null, null, "Active", companyId, "P");
		branchList.add(new BranchDM(0L, "All"));
		BeanContainer<Long, BranchDM> beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranch.setBeanIdProperty("branchId");
		beanBranch.addAll(branchList);
		cbBranch.setContainerDataSource(beanBranch);
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee list
	 */
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		List<EmployeeDM> empList = new ArrayList<EmployeeDM>();
		empList.add(new EmployeeDM(-1L, "All"));
		empList.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyId, null, null, null, null,
				"P"));
		BeanContainer<Long, EmployeeDM> beanLoadEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanLoadEmployee.setBeanIdProperty("employeeid");
		beanLoadEmployee.addAll(empList);
		cbEmployeeName.setContainerDataSource(beanLoadEmployee);
	}
	
	/*
	 * loadPayPeriodDM()-->this function is used for load the pay period list
	 */
	private void loadPayPeriod() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			BeanContainer<Long, PayPeriodDM> beanPayPeriod = new BeanContainer<Long, PayPeriodDM>(PayPeriodDM.class);
			beanPayPeriod.setBeanIdProperty("payPeriodId");
			beanPayPeriod.addAll(servicePayPeriod.getPayList(null, null, null, null, companyId, "Active", "P"));
			cbPayPeried.setContainerDataSource(beanPayPeriod);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	/*
	 * loadStartandEndDates()-->this function is used for load the start and end date
	 */
	void loadStartandEndDates() {
		try {
			SessionFactory sf = serviceAttendanceProcess.getConnection();
			Session session = sf.openSession();
			session.beginTransaction();
			session.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					// TODO Auto-generated method stub
					statement = connection.prepareCall("{ call PROC_DATE_CALC (?,?,?) }");
					statement.setLong(1, payPeriodId);
					statement.registerOutParameter(2, Types.VARCHAR);
					statement.registerOutParameter(3, Types.VARCHAR);
					statement.execute();
					startDate = statement.getString(2);
					endDate = statement.getString(3);
					connection.close();
				}
			});
			tfProcessPeriod.setReadOnly(false);
			tfProcessPeriod.setValue(" " + startDate + " to " + endDate);
			tfProcessPeriod.setReadOnly(true);
		}
		catch (Exception e) {
			logger.info("Load Start and end Date Using Procedure" + e);
		}
		finally {
			try {
				statement.close();
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("Load Start and end Date Using Procedure" + e);
			}
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void resetSearchDetails() {
		readonlyfalse();
		cbPayPeried.setComponentError(null);
		cbPayPeried.setValue(null);
		tfProcessPeriod.setComponentError(null);
		tfProcessPeriod.setValue("");
		cbBranch.setComponentError(null);
		cbBranch.setValue(null);
		cbEmployeeName.setComponentError(null);
		cbEmployeeName.setValue(null);
		btnSearch.setVisible(false);
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
	protected void validateDetails() throws ValidationException {
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
	
	@Override
	protected void resetFields() {
		readonlyfalse();
		tfProcessPeriod.setValue("");
		cbBranch.setValue(null);
		cbEmployeeName.setValue(null);
		cbPayPeried.setValue(null);
		tfProcessPeriod.setReadOnly(true);
	}
	
	private void loadAttendenceProcess() {
		try {
			SessionFactory sf = serviceAttendanceProcess.getConnection();
			Session session = sf.openSession();
			session.beginTransaction();
			session.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					// TODO Auto-generated method stub
					System.out.println("payPeriodId=" + payPeriodId + "\n(Long) cbBranch.getValue()="
							+ (Long) cbBranch.getValue() + "\nstartDate=" + startDate + "\nendDate=" + endDate
							+ "\ncompanyId=" + companyId + "\nuserName=" + userName);
					statement = connection
							.prepareCall("{ ? = call pkg_hcm_core.fn_calc_staff_attend (?,?,?,?,?,?,?,?) }");
					statement.registerOutParameter(1, Types.VARCHAR);
					statement.setLong(2, payPeriodId);
					statement.setLong(3, (Long) cbEmployeeName.getValue());
					statement.setLong(4, (Long) cbBranch.getValue());
					statement.setString(5, startDate);
					statement.setString(6, endDate);
					statement.setLong(7, companyId);
					statement.setString(8, userName);
					statement.registerOutParameter(9, Types.VARCHAR);
					statement.execute();
					funationStatus = statement.getString(1);
					errorMsg = statement.getString(9);
					System.out.println("funationStatus-->" + funationStatus);
					System.out.println("errorMsg-->" + errorMsg);
					connection.close();
				}
			});
			cbPayPeried.setComponentError(null);
			cbBranch.setComponentError(null);
		}
		catch (Exception e) {
			System.out.println(e);
			if (payPeriodId == null) {
				cbPayPeried.setComponentError(new UserError("Please Select Pay Period"));
			}
			if (cbBranch.getValue() == null) {
				cbBranch.setComponentError(new UserError("Please Select Branch "));
			}
			logger.info("Employee Attendance Process Function call" + e);
		}
		finally {
			try {
				statement.close();
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.info("Employee Attendance Process Function call" + e);
			}
		}
	}
	
	public void loadSrchRslt() {
		try {
			tblMstScrSrchRslt.removeAllItems();
			attendProcList = serviceAttendanceProcess.getAttendenceProc(null, null, null, null, "Pending", "F");
			recordCnt = attendProcList.size();
			System.out.println("LISYYYYY" + recordCnt);
			beanAttendenceProcDM = new BeanItemContainer<AttendenceProcDM>(AttendenceProcDM.class);
			beanAttendenceProcDM.addAll(attendProcList);
			tblMstScrSrchRslt.setContainerDataSource(beanAttendenceProcDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "attProcId", "payperiodName", "allStDt", "allEndDt",
					"status" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Pay Period Name", "Start Dt", "End Dt",
					"Status" });
			tblMstScrSrchRslt.setColumnAlignment("attProcId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("processedDt", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void editClient() {
		hlCmdBtnLayout.setVisible(false);
		if (tblMstScrSrchRslt.getValue() != null) {
			AttendenceProcDM editClientlist = beanAttendenceProcDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			if (editClientlist.getPayPeriodId() != null) {
				cbPayPeried.setValue(editClientlist.getPayPeriodId());
			}
			attProcId = editClientlist.getAttProcId();
			String startsdt = DateUtils.datetostring(editClientlist.getAllStDt());
			String enddst = DateUtils.datetostring(editClientlist.getAllEndDt());
			tfProcessPeriod.setReadOnly(false);
			tfProcessPeriod.setValue(startsdt + " to " + enddst);
			tfProcessPeriod.setReadOnly(true);
			if (editClientlist.getBranchId() != null) {
				cbBranch.setValue(editClientlist.getBranchId());
			}
			cbEmployeeName.setValue(editClientlist.getEmpid());
		}
	}
	
	protected void saveattapprove() {
		serviceAttendanceProcess.updateapproveAtt_proc(attProcId, "Approved", null, userName, "ATT_PROC");
		serviceAttendanceProcess.updateapproveAtt_proc(attProcId, "Approved", null, userName, "ATT_ATTEN");
		serviceAttendanceProcess.procAttendenceApprove(companyId, (String) attProcId.toString(), userName);
		loadSrchRslt();
	}
	
	private void readonlytrue() {
		cbBranch.setReadOnly(true);
		cbEmployeeName.setReadOnly(true);
		cbPayPeried.setReadOnly(true);
		tfProcessPeriod.setReadOnly(true);
	}
	
	private void readonlyfalse() {
		tfProcessPeriod.setReadOnly(false);
		cbBranch.setReadOnly(false);
		cbEmployeeName.setReadOnly(false);
		cbPayPeried.setReadOnly(false);
	}
}
