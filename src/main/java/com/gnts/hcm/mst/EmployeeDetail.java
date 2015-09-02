/**
 * File Name	:	EmployeeDetail.java
 * Description	:	This Screen Purpose for Modify the Employee Details.Add the Employee  details process should be directly added in DB.
 * Author		:	JOEL GLINDAN D
 * Date			:	SEP 17, 2014
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
 * 0.1          SEP 17, 2014   	JOEL GLINDAN D		Initial Version		
 * 
 */
package com.gnts.hcm.mst;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.hcm.domain.mst.DesignationDM;
import com.gnts.hcm.domain.mst.EmployeeAddressDM;
import com.gnts.hcm.domain.mst.EmployeeDtlsDM;
import com.gnts.hcm.domain.mst.EmployeeEducationDM;
import com.gnts.hcm.domain.mst.EmploymentTypeDM;
import com.gnts.hcm.domain.mst.GradeDM;
import com.gnts.hcm.domain.mst.PayPeriodDM;
import com.gnts.hcm.domain.txn.EmployeeAbsentDM;
import com.gnts.hcm.domain.txn.EmployeeDeductionDM;
import com.gnts.hcm.domain.txn.EmployeeEarningDM;
import com.gnts.hcm.domain.txn.EmployeeLateDetailDM;
import com.gnts.hcm.domain.txn.EmployeeLeaveDM;
import com.gnts.hcm.domain.txn.EmployeeOndutyDM;
import com.gnts.hcm.domain.txn.EmployeePermissionDM;
import com.gnts.hcm.service.mst.DesignationService;
import com.gnts.hcm.service.mst.EmployeeAddressService;
import com.gnts.hcm.service.mst.EmployeeDtlsService;
import com.gnts.hcm.service.mst.EmployeeEducationService;
import com.gnts.hcm.service.mst.EmploymentTypeService;
import com.gnts.hcm.service.mst.GradeService;
import com.gnts.hcm.service.mst.PayPeriodService;
import com.gnts.hcm.service.txn.EmployeeAbsentService;
import com.gnts.hcm.service.txn.EmployeeDeductionService;
import com.gnts.hcm.service.txn.EmployeeEarningService;
import com.gnts.hcm.service.txn.EmployeeLateDetailService;
import com.gnts.hcm.service.txn.EmployeeLeaveService;
import com.gnts.hcm.service.txn.EmployeeOndutyService;
import com.gnts.hcm.service.txn.EmployeePermissionService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class EmployeeDetail implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VerticalLayout hlPageRootContainter = (VerticalLayout) UI.getCurrent().getSession()
			.getAttribute("clLayout");
	// Header container which holds, screen name, notification and page master
	// buttons
	private HorizontalLayout hlPageHdrContainter = (HorizontalLayout) UI.getCurrent().getSession()
			.getAttribute("hlLayout");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private DepartmentService servicebeandepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	private GradeService serviceGrade = (GradeService) SpringContextHelper.getBean("Grade");
	private DesignationService serviceDesignation = (DesignationService) SpringContextHelper.getBean("Designation");
	private PayPeriodService servicePayPeriodService = (PayPeriodService) SpringContextHelper.getBean("PayPeriod");
	private EmploymentTypeService serviceEmploymentType = (EmploymentTypeService) SpringContextHelper
			.getBean("EmploymentType");
	private EmployeeDtlsService servempdtls = (EmployeeDtlsService) SpringContextHelper.getBean("Employeedtls");
	private EmployeeOndutyService serviceOnduty = (EmployeeOndutyService) SpringContextHelper.getBean("EmployeeOnduty");
	private EmployeePermissionService servicePermission = (EmployeePermissionService) SpringContextHelper
			.getBean("EmployeePermission");
	private EmployeeAbsentService serviceAbsent = (EmployeeAbsentService) SpringContextHelper.getBean("EmployeeAbsent");
	private EmployeeLateDetailService serviceLate = (EmployeeLateDetailService) SpringContextHelper
			.getBean("EmployeeLateDetail");
	private EmployeeAddressService serviceAddress = (EmployeeAddressService) SpringContextHelper
			.getBean("EmployeeAddress");
	private EmployeeEducationService serviceLeave = (EmployeeEducationService) SpringContextHelper
			.getBean("EmployeeEducation");
	private EmployeeLeaveService serviceleave = (EmployeeLeaveService) SpringContextHelper.getBean("EmployeeLeave");
	private EmployeeDeductionService serviceEmployeeDeduction = (EmployeeDeductionService) SpringContextHelper
			.getBean("EmployeeDeduction");
	private EmployeeEarningService serviceEmployeeEarning = (EmployeeEarningService) SpringContextHelper
			.getBean("EmployeeEarning");
	private String screenName = "", username = "";
	private Button btnScreenName;
	private Long companyid;
	private GERPComboBox cbEmployee = new GERPComboBox();
	private Logger logger = Logger.getLogger(EmployeeDetail.class);
	// profile components
	private Image profilePic;
	private TextField firstNameField;
	private TextField lastNameField;
	private TextField sexField;
	private TextField emailField;
	private TextField phoneField;
	private TextField tfDob;
	private TextField tfDoj;
	private TextField tfEmployeeCode;
	private TextField tfMaritalStatus;
	private ComboBox cbBranch;
	private ComboBox cbDepartment;
	private ComboBox cbDesignation;
	private ComboBox cbEmpType;
	private ComboBox cbGrade;
	private ComboBox cbPayperiod;
	private Table tblOnduty = new Table("Onduty :");
	private Table tblAbsent = new Table("Absent :");
	private Table tblLate = new Table("Late :");
	private Table tblPermission = new Table("Permission :");
	private Table tblAddress = new Table();
	private Table tblEducation = new Table();
	private Table tblLeave = new Table("Leave :");
	private Table tblEarnings = new Table("Earnings");
	private Table tblDeduction = new Table("Deduction");
	
	public EmployeeDetail() {
		// TODO Auto-generated constructor stub
		if (UI.getCurrent().getSession().getAttribute("screenName") != null) {
			screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		}
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		btnScreenName = new GERPButton(screenName, "link", this);
		hlPageHdrContainter.removeAllComponents();
		hlPageHdrContainter.addComponent(btnScreenName);
		hlPageHdrContainter.setComponentAlignment(btnScreenName, Alignment.MIDDLE_LEFT);
		buildView();
	}
	
	private void buildView() {
		// TODO Auto-generated method stub
		cbEmployee.setWidth("200px");
		cbEmployee.setItemCaptionPropertyId("fullname");
		cbEmployee.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbEmployee.getValue() != null) {
					loadEmployeeOverView();
				}
			}
		});
		tblOnduty.setPageLength(5);
		tblAbsent.setPageLength(5);
		tblLate.setPageLength(5);
		tblPermission.setPageLength(5);
		tblAddress.setPageLength(5);
		tblEducation.setPageLength(5);
		tblLeave.setPageLength(5);
		tblOnduty.setFooterVisible(true);
		tblAbsent.setFooterVisible(true);
		tblLate.setFooterVisible(true);
		tblPermission.setFooterVisible(true);
		tblAddress.setFooterVisible(true);
		tblEducation.setFooterVisible(true);
		tblLeave.setFooterVisible(true);
		tblOnduty.setWidth("500px");
		tblLeave.setWidth("500px");
		tblAbsent.setWidth("600px");
		tblLate.setWidth("600px");
		tblPermission.setWidth("500px");
		tblAddress.setWidth("100%");
		tblEducation.setWidth("100%");
		tblEarnings.setWidth("100%");
		tblEarnings.setPageLength(5);
		tblDeduction.setWidth("100%");
		tblDeduction.setPageLength(5);
		hlPageRootContainter.setSpacing(true);
		hlPageRootContainter.addComponent(cbEmployee);
		// Create the Accordion.
		Accordion accordion = new Accordion();
		// Have it take all space available in the layout.
		accordion.setSizeFull();
		// Some components to put in the Accordion.
		HorizontalLayout l3 = new HorizontalLayout();
		l3.addComponent(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(tblLeave);
						addComponent(tblAbsent);
					}
				});
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(tblPermission);
						addComponent(tblLate);
					}
				});
			}
		});
		HorizontalLayout l2 = new HorizontalLayout();
		l2.addComponent(tblOnduty);
		VerticalLayout l4 = new VerticalLayout();
		l4.addComponent(tblEarnings);
		l4.addComponent(tblDeduction);
		l4.setSpacing(true);
		// Add the components as tabs in the Accordion.
		accordion.addTab(buildProfile(), "Personal Information", null);
		accordion.addTab(l3, "Time Off", null);
		accordion.addTab(l2, "Time On", null);
		accordion.addTab(tblAddress, "Address", null);
		accordion.addTab(tblEducation, "Education", null);
		accordion.addTab(l4, "Earnings & Deductions", null);
		hlPageRootContainter.addComponent(accordion);
		loadEmployeeList();
	}
	
	private Component buildProfile() {
		HorizontalLayout root = new HorizontalLayout();
		root.setWidth(100.0f, Unit.PERCENTAGE);
		root.setSpacing(true);
		root.setMargin(true);
		VerticalLayout pic = new VerticalLayout();
		pic.setSpacing(true);
		profilePic = new Image(null, new ThemeResource("img/profile-pic-300px.jpg"));
		profilePic.setWidth(170.0f, Unit.PIXELS);
		profilePic.setHeight(175.0f, Unit.PIXELS);
		pic.addComponent(profilePic);
		root.addComponent(pic);
		FormLayout details1 = new FormLayout();
		details1.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		root.addComponent(details1);
		tfEmployeeCode = new TextField("Employee Code");
		tfEmployeeCode.setWidth("150px");
		details1.addComponent(tfEmployeeCode);
		firstNameField = new TextField("First Name");
		firstNameField.setWidth("150px");
		details1.addComponent(firstNameField);
		lastNameField = new TextField("Last Name");
		lastNameField.setWidth("150px");
		details1.addComponent(lastNameField);
		sexField = new TextField("Sex");
		sexField.setWidth("150px");
		details1.addComponent(sexField);
		tfDob = new TextField("Date of birth");
		tfDob.setWidth("150px");
		details1.addComponent(tfDob);
		FormLayout details2 = new FormLayout();
		root.addComponent(details2);
		tfDoj = new TextField("Date of join");
		tfDoj.setWidth("150px");
		details2.addComponent(tfDoj);
		tfMaritalStatus = new TextField("Marital Status");
		tfMaritalStatus.setWidth("150px");
		details2.addComponent(tfMaritalStatus);
		emailField = new TextField("Email");
		emailField.setWidth("150px");
		emailField.setNullRepresentation("");
		details2.addComponent(emailField);
		phoneField = new TextField("Phone");
		phoneField.setWidth("150px");
		phoneField.setNullRepresentation("");
		details2.addComponent(phoneField);
		cbBranch = new ComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("150px");
		details2.addComponent(cbBranch);
		FormLayout details3 = new FormLayout();
		cbDepartment = new ComboBox("Department");
		cbDepartment.setItemCaptionPropertyId("deptname");
		cbDepartment.setWidth("150px");
		details3.addComponent(cbDepartment);
		cbDesignation = new ComboBox("Desigination");
		cbDesignation.setItemCaptionPropertyId("designationName");
		cbDesignation.setWidth("150px");
		details3.addComponent(cbDesignation);
		cbEmpType = new ComboBox("Employement Type");
		cbEmpType.setItemCaptionPropertyId("empTypeName");
		cbEmpType.setWidth("150px");
		details3.addComponent(cbEmpType);
		cbGrade = new ComboBox("Grade");
		cbGrade.setItemCaptionPropertyId("gradeDESC");
		cbGrade.setWidth("150px");
		details3.addComponent(cbGrade);
		cbPayperiod = new ComboBox("Pay period");
		cbPayperiod.setItemCaptionPropertyId("periodName");
		cbPayperiod.setWidth("150px");
		details3.addComponent(cbPayperiod);
		root.addComponent(details3);
		return root;
	}
	
	private void loadEmployeeOverView() {
		try {
			EmployeeDM employeeDM = (EmployeeDM) cbEmployee.getValue();
			try {
				profilePic.setSource(new FileResource(new File(viewImage(employeeDM.getEmpphoto(),
						employeeDM.getFirstname()))));
			}
			catch (Exception e) {
				profilePic.setSource(new ThemeResource("img/profile-pic-300px.jpg"));
			}
			if (employeeDM.getEmployeecode() != null) {
				tfEmployeeCode.setReadOnly(false);
				tfEmployeeCode.setValue(employeeDM.getEmployeecode());
				tfEmployeeCode.setReadOnly(true);
			}
			if (employeeDM.getFirstname() != null) {
				firstNameField.setReadOnly(false);
				firstNameField.setValue(employeeDM.getFirstname());
				firstNameField.setReadOnly(true);
			}
			if (employeeDM.getLastname() != null) {
				lastNameField.setReadOnly(false);
				lastNameField.setValue(employeeDM.getLastname());
				lastNameField.setReadOnly(true);
			}
			if (employeeDM.getGender() != null) {
				sexField.setReadOnly(false);
				sexField.setValue(employeeDM.getGender());
				sexField.setReadOnly(true);
			}
			if (employeeDM.getDob() != null) {
				tfDob.setReadOnly(false);
				tfDob.setValue(employeeDM.getDob());
				tfDob.setReadOnly(true);
			} else {
				tfDob.setReadOnly(false);
				tfDob.setValue("---");
				tfDob.setReadOnly(true);
			}
			if (employeeDM.getDoj() != null) {
				tfDoj.setReadOnly(false);
				tfDoj.setValue(employeeDM.getDoj());
				tfDoj.setReadOnly(true);
			} else {
				tfDoj.setReadOnly(false);
				tfDoj.setValue("---");
				tfDoj.setReadOnly(true);
			}
			if (employeeDM.getPrimaryemail() != null) {
				emailField.setReadOnly(false);
				emailField.setValue(employeeDM.getPrimaryemail());
				emailField.setReadOnly(true);
			} else {
				emailField.setReadOnly(false);
				emailField.setValue("---");
				emailField.setReadOnly(true);
			}
			if (employeeDM.getPrimaryphone() != null) {
				phoneField.setReadOnly(false);
				phoneField.setValue(employeeDM.getPrimaryphone());
				phoneField.setReadOnly(true);
			} else {
				phoneField.setReadOnly(false);
				phoneField.setValue("---");
				phoneField.setReadOnly(true);
			}
			if (employeeDM.getBranchid() != null) {
				cbBranch.setReadOnly(false);
				loadBranchList();
				cbBranch.setValue(employeeDM.getBranchid());
				cbBranch.setReadOnly(true);
			} else {
				cbBranch.setReadOnly(false);
				cbBranch.setValue(null);
				cbBranch.setReadOnly(true);
			}
			if (employeeDM.getDeptid() != null) {
				cbDepartment.setReadOnly(false);
				loadDepartmentList();
				cbDepartment.setValue(employeeDM.getDeptid());
				cbDepartment.setReadOnly(true);
			} else {
				cbDepartment.setReadOnly(false);
				cbDepartment.setValue(null);
				cbDepartment.setReadOnly(true);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			EmployeeDtlsDM employeeDtlsDM = new EmployeeDtlsDM();
			try {
				employeeDtlsDM = servempdtls.getEmployeeDtls(null,
						((EmployeeDM) cbEmployee.getValue()).getEmployeeid(), null, null, null, null, "F", null).get(0);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (employeeDtlsDM.getDesignationid() != null) {
				cbDesignation.setReadOnly(false);
				loadDesignationList(employeeDtlsDM.getDesignationid());
				cbDesignation.setValue(employeeDtlsDM.getDesignationid());
				cbDesignation.setReadOnly(true);
			} else {
				cbDesignation.setReadOnly(false);
				cbDesignation.setValue(employeeDtlsDM.getDesignationid());
				cbDesignation.setReadOnly(true);
			}
			if (employeeDtlsDM.getEmployeetypeid() != null) {
				cbEmpType.setReadOnly(false);
				loadEmploymentType(employeeDtlsDM.getEmployeetypeid());
				cbEmpType.setValue(employeeDtlsDM.getEmployeetypeid());
				cbEmpType.setReadOnly(true);
			} else {
				cbEmpType.setReadOnly(false);
				cbEmpType.setValue(null);
				cbEmpType.setReadOnly(true);
			}
			if (employeeDtlsDM.getMaritalstatus() != null) {
				tfMaritalStatus.setReadOnly(false);
				tfMaritalStatus.setValue(employeeDtlsDM.getMaritalstatus());
				tfMaritalStatus.setReadOnly(true);
			} else {
				tfMaritalStatus.setReadOnly(false);
				tfMaritalStatus.setValue("---");
				tfMaritalStatus.setReadOnly(true);
			}
			if (employeeDtlsDM.getGradeid() != null) {
				cbGrade.setReadOnly(false);
				loadGradeList(employeeDtlsDM.getGradeid());
				cbGrade.setValue(employeeDtlsDM.getGradeid());
				cbGrade.setReadOnly(true);
			} else {
				cbGrade.setReadOnly(false);
				cbGrade.setValue(null);
				cbGrade.setReadOnly(true);
			}
			if (employeeDtlsDM.getPayperiodid() != null) {
				cbPayperiod.setReadOnly(false);
				loadPayperiodList(employeeDtlsDM.getPayperiodid());
				cbPayperiod.setValue(employeeDtlsDM.getPayperiodid());
				cbPayperiod.setReadOnly(true);
			} else {
				cbPayperiod.setReadOnly(false);
				cbPayperiod.setValue(null);
				cbPayperiod.setReadOnly(true);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			loadTimeoffDetails();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			loadEmpAddress();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			loadEudcation();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadTimeoffDetails() {
		try {
			loadOndutyDetails();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			loadPermissionDetails();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			loadAbsentDetails();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			loadLateDetails();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			loadLeaveList();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			loadEarnings();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			loadDeductions();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadOndutyDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "loading SearchResult Details...");
			List<EmployeeOndutyDM> list = new ArrayList<EmployeeOndutyDM>();
			tblOnduty.removeAllItems();
			if (((EmployeeDM) cbEmployee.getValue()).getEmployeeid() != null) {
				list = serviceOnduty.getempondutylist(null, ((EmployeeDM) cbEmployee.getValue()).getEmployeeid(),
						"Active", "F");
			}
			BeanItemContainer<EmployeeOndutyDM> beans = new BeanItemContainer<EmployeeOndutyDM>(EmployeeOndutyDM.class);
			beans.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Employee Onduty. result set");
			tblOnduty.setContainerDataSource(beans);
			tblOnduty.setVisibleColumns(new Object[] { "datefrm", "dateto", "noofdays", "ondutyrks" });
			tblOnduty.setColumnHeaders(new String[] { "From Date", "To Date", "No of Days", "Remarks" });
			tblOnduty.setColumnFooter("ondutyrks", "No.of Records : " + list.size());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadPermissionDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "loading SearchResult Details...");
		List<EmployeePermissionDM> list = new ArrayList<EmployeePermissionDM>();
		tblPermission.removeAllItems();
		list = servicePermission.getemppermissionList(null, ((EmployeeDM) cbEmployee.getValue()).getEmployeeid(),
				"Active", "F");
		BeanItemContainer<EmployeePermissionDM> beans = new BeanItemContainer<EmployeePermissionDM>(
				EmployeePermissionDM.class);
		beans.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Employee Permission. result set");
		tblPermission.setContainerDataSource(beans);
		tblPermission.setVisibleColumns(new Object[] { "permissiondt", "intime", "permissionhrs", "remarks" });
		tblPermission.setColumnHeaders(new String[] { "Permission Date", "In Time", "Permission Hours", "Remarks" });
		tblPermission.setColumnFooter("remarks", "No.of Records : " + list.size());
	}
	
	private void loadAbsentDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "loading SearchResult Details...");
		List<EmployeeAbsentDM> list = new ArrayList<EmployeeAbsentDM>();
		list = serviceAbsent.getempabslist(null, ((EmployeeDM) cbEmployee.getValue()).getEmployeeid(), "Active", "F");
		BeanItemContainer<EmployeeAbsentDM> beans = new BeanItemContainer<EmployeeAbsentDM>(EmployeeAbsentDM.class);
		beans.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Employee Absent. result set");
		tblAbsent.setContainerDataSource(beans);
		tblAbsent.setVisibleColumns("absentdate", "starthours", "endhours", "totalhours", "absentremarks");
		tblAbsent.setColumnHeaders("Absent Date", "Start Hour", "End Hour", "Total Hours", "Remarks");
		tblAbsent.setColumnFooter("absentremarks", "No.of Records : " + list.size());
	}
	
	private void loadLateDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "loading SearchResult Details...");
		List<EmployeeLateDetailDM> list = new ArrayList<EmployeeLateDetailDM>();
		list = serviceLate.getemplatelist(null, ((EmployeeDM) cbEmployee.getValue()).getEmployeeid(), null, "Active",
				"F");
		BeanItemContainer<EmployeeLateDetailDM> beans = new BeanItemContainer<EmployeeLateDetailDM>(
				EmployeeLateDetailDM.class);
		beans.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Employee Late. result set");
		tblLate.setContainerDataSource(beans);
		tblLate.setVisibleColumns(new Object[] { "latedate", "intime", "latehrs", "laterks" });
		tblLate.setColumnHeaders(new String[] { "Late Date", "In Time", "Late Hours", "Remarks" });
		tblLate.setColumnFooter("laterks", "No.of Records : " + list.size());
	}
	
	private void loadLeaveList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading Search Result....");
		tblLeave.removeAllItems();
		List<EmployeeLeaveDM> list = serviceleave.getempleaveList(null,
				((EmployeeDM) cbEmployee.getValue()).getEmployeeid(), null, null, null, null, "F");
		BeanItemContainer<EmployeeLeaveDM> beanLeave = new BeanItemContainer<EmployeeLeaveDM>(EmployeeLeaveDM.class);
		beanLeave.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Employee Leave. result set");
		tblLeave.setContainerDataSource(beanLeave);
		tblLeave.setVisibleColumns(new Object[] { "datefrom", "dateto", "noofdays", "leavereason" });
		tblLeave.setColumnHeaders(new String[] { "Date From", "Date To", "No of Days", "Reason" });
		tblLeave.setColumnFooter("leavereason", "No.of Records : " + list.size());
	}
	
	/*
	 * loadSrchEmpAddress()-->this function is used for load the search Employee Address to table
	 */
	private void loadEmpAddress() {
		logger.info("Employee Address Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading employee Address Search...");
		List<EmployeeAddressDM> list = new ArrayList<EmployeeAddressDM>();
		tblAddress.removeAllItems();
		list.addAll(serviceAddress.getempaddresslist(null, ((EmployeeDM) cbEmployee.getValue()).getEmployeeid(), null,
				"Active", "F"));
		BeanItemContainer<EmployeeAddressDM> beanEmployeeAddress = new BeanItemContainer<EmployeeAddressDM>(
				EmployeeAddressDM.class);
		beanEmployeeAddress.addAll(list);
		tblAddress.setSelectable(true);
		tblAddress.setContainerDataSource(beanEmployeeAddress);
		tblAddress.setVisibleColumns(new Object[] { "addresstype", "addressln1", "addressln2", "cityname", "postcode",
				"addressstatus", "lastupdateddt", "lastupdatedby" });
		tblAddress.setColumnHeaders(new String[] { "Address Type", "Address Line1", "Address Line2", "City",
				"Post Code", "Status", "Last Updated Date", "Last Updated by" });
		tblAddress.setColumnAlignment("empaddressid", Align.RIGHT);
		tblAddress.setColumnFooter("lastupdatedby", "No.of Records : " + list.size());
	}
	
	/*
	 * loadEudcation()-->this function is used for load the search Employee Education to table
	 */
	private void loadEudcation() {
		logger.info("Employee Education Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Employee Education Search...");
		tblEducation.removeAllItems();
		List<EmployeeEducationDM> list = new ArrayList<EmployeeEducationDM>();
		list.addAll(serviceLeave.getempeducationList(null, ((EmployeeDM) cbEmployee.getValue()).getEmployeeid(), null,
				null, null, "Active", "F"));
		BeanItemContainer<EmployeeEducationDM> beanemployeeeducation = new BeanItemContainer<EmployeeEducationDM>(
				EmployeeEducationDM.class);
		beanemployeeeducation.addAll(list);
		tblEducation.setSelectable(true);
		tblEducation.setContainerDataSource(beanemployeeeducation);
		tblEducation.setVisibleColumns(new Object[] { "qualname", "subject", "durfrm", "durto", "insname",
				"gradeachcd", "empednstatus", "lastupdatddt", "lastupdatdby" });
		tblEducation.setColumnHeaders(new String[] { "Qualification", "Subject", "Duration From", "Duration To",
				"Institution Name", "Grade", "Status", "Last Updated Date", "Last Updated By" });
		tblEducation.setColumnFooter("lastupdatdby", "No.of Records :" + list.size());
	}
	
	/*
	 * loadDeductions()-->this function is used for load the search Employee Deductions to table
	 */
	private void loadDeductions() {
		tblDeduction.removeAllItems();
		List<EmployeeDeductionDM> list = new ArrayList<EmployeeDeductionDM>();
		list = serviceEmployeeDeduction.getempdeductionlist(null, ((EmployeeDM) cbEmployee.getValue()).getEmployeeid(),
				null, null, "F");
		BeanItemContainer<EmployeeDeductionDM> beanEmployeeDecn = new BeanItemContainer<EmployeeDeductionDM>(
				EmployeeDeductionDM.class);
		beanEmployeeDecn.addAll(list);
		tblDeduction.setContainerDataSource(beanEmployeeDecn);
		tblDeduction.setVisibleColumns(new Object[] { "empdednid", "empName", "dedcnCode", "isflatpt", "dednamt",
				"dednpt", "empdednstatus", "lastupdateddt", "lastupdatedby" });
		tblDeduction.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Deduction Code", "Flat/Percent",
				"Deduction Amount", "Deduction Percent", "Status", "Last Updated Date", "Last Updated By" });
		tblDeduction.setColumnAlignment("empdednid", Align.RIGHT);
	}
	
	/*
	 * loadEarnings()-->this function is used for load the search Employee Earnings to table
	 */
	private void loadEarnings() {
		tblEarnings.removeAllItems();
		List<EmployeeEarningDM> list = new ArrayList<EmployeeEarningDM>();
		list = serviceEmployeeEarning.getempearningList(null, ((EmployeeDM) cbEmployee.getValue()).getEmployeeid(),
				null, null, "F");
		BeanItemContainer<EmployeeEarningDM> beanEmployeeEarn = new BeanItemContainer<EmployeeEarningDM>(
				EmployeeEarningDM.class);
		beanEmployeeEarn.addAll(list);
		tblEarnings.setContainerDataSource(beanEmployeeEarn);
		tblEarnings.setVisibleColumns(new Object[] { "empearnid", "employeeName", "earnCode", "isflatpercent",
				"earnpercent", "earnamt", "empearnstatus", "lastpdateddt", "lastupdatedby" });
		tblEarnings.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Earn Code", "Flat/Percent",
				"Earn Percent", "Earn Amount", "Status", "Last Updated Date", "Last Updated By" });
		tblEarnings.setColumnAlignment("empearnid", Align.RIGHT);
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee list
	 */
	private void loadEmployeeList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name :  > " + "Loading  Employee Search...");
			BeanItemContainer<EmployeeDM> beanLoadEmployee = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
			beanLoadEmployee.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null,
					null, null, "F"));
			cbEmployee.setContainerDataSource(beanLoadEmployee);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadBranchList()-->this function is used for load the Branch list
	 */
	private void loadBranchList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
			BeanContainer<Long, BranchDM> beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanBranch.setBeanIdProperty("branchId");
			beanBranch.addAll(serviceBranch.getBranchList(((EmployeeDM) cbEmployee.getValue()).getBranchid(), null,
					null, "Active", companyid, "P"));
			cbBranch.setContainerDataSource(beanBranch);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadDepartmentList()-->this function is used for load the Department list
	 */
	private void loadDepartmentList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Department Search...");
			BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
			beanDepartment.setBeanIdProperty("deptid");
			beanDepartment.addAll(servicebeandepartmant.getDepartmentList(companyid, null, "Active", "P"));
			cbDepartment.setContainerDataSource(beanDepartment);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadEmploymentType()-->this function is used for load the employment type list
	 */
	private void loadEmploymentType(Long id) {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Employment Search...");
			BeanContainer<Long, EmploymentTypeDM> benemptype = new BeanContainer<Long, EmploymentTypeDM>(
					EmploymentTypeDM.class);
			benemptype.setBeanIdProperty("empTypeId");
			benemptype.addAll(serviceEmploymentType.getEmpTypeList(id, null, companyid, "Active", "F"));
			cbEmpType.setContainerDataSource(benemptype);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadempgradelist()-->this function is used for load the grade list
	 */
	private void loadGradeList(Long id) {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Grade Search...");
			BeanContainer<Long, GradeDM> beangrad = new BeanContainer<Long, GradeDM>(GradeDM.class);
			beangrad.setBeanIdProperty("gradeId");
			beangrad.addAll(serviceGrade.getGradeList(id, null, null, companyid, "Active", "F"));
			cbGrade.setContainerDataSource(beangrad);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadpayperiodlist()-->this function is used for load the payperiod list
	 */
	private void loadPayperiodList(Long id) {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Payperiod  Search...");
			BeanContainer<Long, PayPeriodDM> beanpayperiod = new BeanContainer<Long, PayPeriodDM>(PayPeriodDM.class);
			beanpayperiod.setBeanIdProperty("payPeriodId");
			beanpayperiod.addAll(servicePayPeriodService.getPayList(id, null, null, null, companyid, "Active", "F"));
			cbPayperiod.setContainerDataSource(beanpayperiod);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loaddesignationlist()-->this function is used for load the Designation List
	 */
	private void loadDesignationList(Long id) {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Designation Search...");
			BeanContainer<Long, DesignationDM> beandesignation = new BeanContainer<Long, DesignationDM>(
					DesignationDM.class);
			beandesignation.setBeanIdProperty("designationId");
			beandesignation.addAll(serviceDesignation
					.getDesignationList(id, null, null, null, companyid, "Active", "F"));
			cbDesignation.setContainerDataSource(beandesignation);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	public String viewImage(byte[] myimage, String name) {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/VAADIN/themes/gerp/img/"
				+ name + ".png";
		if (myimage != null && !"null".equals(myimage)) {
			InputStream in = new ByteArrayInputStream(myimage);
			BufferedImage bImageFromConvert = null;
			try {
				bImageFromConvert = ImageIO.read(in);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				ImageIO.write(bImageFromConvert, "png", new File(basepath));
			}
			catch (Exception e) {
			}
			try {
				ImageIO.write(bImageFromConvert, "jpg", new File(basepath));
			}
			catch (Exception e) {
			}
			try {
				return basepath;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath()
					+ "/VAADIN/themes/gerp/img/profile-pic-300px.jpg";
		}
		return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath()
				+ "/VAADIN/themes/gerp/img/profile-pic-300px.jpg";
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
	}
}
