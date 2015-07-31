/**
 * File Name 		: EmployeeSetting.java 
 * Description 		: this class is used for add/edit Employee details. 
 * Author 			: SOUNDAR C 
 * Date 			: Feb 25, 2014
 * Modified By 		: JOEL GLINDAN D  
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          Mar 03 2014        	SOUNDAR C		        Initial Version
 * 0.2			10-Jul-2014			JOEL GLINDAN D   		Code re-factoring
 */
package com.gnts.base.mst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPSaveNotification;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class EmployeeSetting extends BaseUI {
	// Bean creation
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private DepartmentService servicebeandepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	private BranchService servicebeanBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private HorizontalLayout hlimage = new HorizontalLayout();
	// Add User Input Controls
	private TextField tfFirstName, tfLastName, tfPhoneNo, tfEmailid;
	private PopupDateField dfDateofBirth;
	private ComboBox cbDepartment, cbBranch, cbCountry;
	private ComboBox cbEmpStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private Button btnEmpSave = new Button("Save");
	// local variables declaration
	private Long companyid, userId;
	private String username;
	private Long employeeId;
	// Initialize logger
	private Logger logger = Logger.getLogger(Department.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmployeeSetting() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		userId = Long.valueOf(UI.getCurrent().getSession().getAttribute("userId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Employee() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Employee UI");
		btnEmpSave.addStyleName("add");
		// First Name text field
		tfFirstName = new GERPTextField("First Name");
		// Last Name text field
		tfLastName = new GERPTextField("Last Name");
		// Phone Number text field
		tfPhoneNo = new GERPTextField("Phone Number");
		// Department ComboBox text field
		cbDepartment = new GERPComboBox("Department");
		cbDepartment.setItemCaptionPropertyId("deptname");
		cbDepartment.setImmediate(true);
		cbDepartment.setNullSelectionAllowed(false);
		loadDepartmentList();
		// Branch ComboBox text field
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setRequired(true);
		cbBranch.setImmediate(true);
		cbBranch.setNullSelectionAllowed(false);
		loadBranchList();
		// Country ComboBox text field
		cbCountry = new GERPComboBox("Country");
		cbCountry.setItemCaptionPropertyId("countryName");
		cbCountry.setImmediate(true);
		cbCountry.setNullSelectionAllowed(false);
		cbCountry.setRequired(true);
		loadCountryList();
		cbCountry.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadBranchList();
				}
			}
		});
		// Date Of Birth text field
		dfDateofBirth = new GERPPopupDateField("Date Of Birth");
		// Email-Id text field
		tfEmailid = new GERPTextField("Email-Id");
		btnEmpSave.addStyleName("savebt");
		hlPageHdrContainter.addComponent(btnEmpSave);
		hlPageHdrContainter.setComponentAlignment(btnEmpSave, Alignment.MIDDLE_RIGHT);
		// To set User logs button to Layout
		btnEmpSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					saveEmpDetails();
					new GERPSaveNotification();
				}
				catch (Exception e) {
					try {
						throw new ERPException.SaveException();
					}
					catch (SaveException e1) {
						logger.error("Company ID : "
								+ UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
								+ " | User Name : "
								+ UI.getCurrent().getSession().getAttribute("loginUserName").toString() + " > "
								+ "Exception " + e1.getMessage());
						e1.printStackTrace();
					}
				}
			}
		});
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		btnSearch.setVisible(false);
		btnReset.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		vlSrchRsltContainer.setVisible(false);
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		hlSearchLayout.setHeight("500");
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		// Add components for Search Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		// add image into fourth column in form layout4
		flColumn1.addComponent(hlimage);
		flColumn2.addComponent(tfFirstName);
		flColumn2.addComponent(tfLastName);
		flColumn2.addComponent(tfPhoneNo);
		flColumn2.addComponent(tfEmailid);
		flColumn2.addComponent(dfDateofBirth);
		flColumn2.addComponent(cbCountry);
		flColumn2.addComponent(cbBranch);
		flColumn2.addComponent(cbDepartment);
		flColumn2.addComponent(cbEmpStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSpacing(true);
		editDetails();
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		tfFirstName.setValue("");
		tfLastName.setValue("");
		tfPhoneNo.setValue("");
		tfEmailid.setValue("");
		dfDateofBirth.setValue(null);
		cbCountry.setValue(null);
		cbBranch.setValue(null);
		cbDepartment.setValue(null);
		UI.getCurrent().getSession().setAttribute("isFileUploaded", false);
	}
	
	// No searchDetails() cannot be implemented
	@Override
	protected void searchDetails() throws NoDataFoundException {
	}
	
	// No resetSearchDetails() cannot be implemented
	@Override
	protected void resetSearchDetails() {
	}
	
	// No addDetails() cannot be implemented
	@Override
	protected void addDetails() {
	}
	
	// No showAuditDetails() cannot be implemented
	@Override
	protected void showAuditDetails() {
	}
	
	// No cancelDetails() cannot be implemented
	@Override
	protected void cancelDetails() {
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	@Override
	protected void editDetails() {
		List<EmployeeDM> editEmp = serviceEmployee.getEmployeeList(null, null, null, null, companyid, employeeId,
				userId, null, null, "F");
		if (editEmp.size() != 0) {
			for (EmployeeDM empObj : editEmp) {
				tfEmailid.setValue(empObj.getPrimaryemail());
				tfFirstName.setValue(empObj.getFirstname());
				if (empObj.getLastname() != null) {
					tfLastName.setValue(empObj.getLastname());
				}
				if (empObj.getCountryid() != null) {
					cbCountry.setValue(empObj.getCountryid());
				}
				if (empObj.getPrimaryphone() != null) {
					tfPhoneNo.setValue(empObj.getPrimaryphone());
				}
				if (empObj.getDob() != null) {
					String dateInString = empObj.getDob();
					Date dateDob;
					try {
						dateDob = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateInString);
						dfDateofBirth.setValue(dateDob);
					}
					catch (ParseException e1) {
					}
				}
				cbDepartment.setValue((Long) empObj.getDeptid());
				cbBranch.setValue((Long) empObj.getBranchid());
				cbEmpStatus.setValue(empObj.getEmpstatus());
				if (empObj.getEmpphoto() != null) {
					hlimage.removeAllComponents();
					byte[] myimage = (byte[]) empObj.getEmpphoto();
					UploadUI test = new UploadUI(hlimage);
					test.dispayImage(myimage, empObj.getFirstname());
				} else {
					new UploadUI(hlimage);
				}
			}
		}
	}
	
	// No avalidateDetails() cannot be implemented
	@Override
	protected void validateDetails() throws ValidationException {
	}
	
	@Override
	protected void saveDetails() {
	}
	
	/**
	 * this method used to update the employee details
	 */
	private void saveEmpDetails() throws ERPException.SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			EmployeeDM empObj = new EmployeeDM();
			empObj.setEmployeeid(employeeId);
			empObj.setCompanyid(companyid);
			empObj.setUserId(userId);
			empObj.setFirstname(tfFirstName.getValue().toString());
			empObj.setLastname(tfLastName.getValue().toString());
			empObj.setPrimaryphone(tfPhoneNo.getValue().toString());
			empObj.setPrimaryemail(tfEmailid.getValue().toString());
			if (cbEmpStatus.getValue() != null) {
				empObj.setEmpstatus((String) cbEmpStatus.getValue());
			}
			if (cbCountry.getValue() != null) {
				empObj.setCountryid((Long) cbCountry.getValue());
			}
			if (dfDateofBirth.getValue() != null) {
				empObj.setDob(dfDateofBirth.getValue());
			}
			if (cbDepartment.getValue() != null) {
				empObj.setDeptid((Long) cbDepartment.getValue());
			}
			if (cbBranch.getValue() != null) {
				empObj.setBranchid((Long) cbBranch.getValue());
			}
			File file = new File(GERPConstants.IMAGE_PATH);
			FileInputStream fin = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];
			fin.read(fileContent);
			fin.close();
			empObj.setEmpphoto(fileContent);
			empObj.setLoginAccess("Y");
			empObj.setLastupdateddt(DateUtils.getcurrentdate());
			empObj.setLastupdatedby(username);
			servicebeanEmployee.updateEmployeedetails(empObj);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		resetFields();
	}
	
	/*
	 * loadCountryList()-->this function is used for load the Country list
	 */
	private void loadCountryList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Country Search...");
		BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
		beanCountry.setBeanIdProperty("countryID");
		beanCountry.addAll(serviceCountry.getCountryList(null, null, null, null, "Active", "P"));
		cbCountry.setContainerDataSource(beanCountry);
	}
	
	/*
	 * loadDepartmentList()-->this function is used for load the Department list
	 */
	private void loadDepartmentList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Department Search...");
		BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beanDepartment.setBeanIdProperty("deptid");
		beanDepartment.addAll(servicebeandepartmant.getDepartmentList(companyid, null, null, "P"));
		cbDepartment.setContainerDataSource(beanDepartment);
	}
	
	/*
	 * loadBranchList()-->this function is used for load the Branch list
	 */
	private void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, BranchDM> beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranch.setBeanIdProperty("branchId");
		beanBranch.addAll(servicebeanBranch.getBranchList(null, null, null, null, companyid, "P"));
		cbBranch.setContainerDataSource(beanBranch);
	}
}