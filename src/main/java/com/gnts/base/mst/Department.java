/**
 * File Name 		: Department.java 
 * Description 		: this class is used for add/edit department details. 
 * Author 			: SOUNDAR C 
 * Date 			: Feb 25, 2014
 * Modification 	:
 * Modified By 		: SOUNDAR C 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          Mar 03 2014        	SOUNDAR C		        Initial Version
 * 0.2			14-Jun-2014			SK						Code re-factoring
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Department extends BaseUI {
	private DepartmentService beandepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	// form layout for input controls
	private FormLayout flDeptName, flDeptStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfDeptname;
	private ComboBox cbDeptStatus = new GERPComboBox("Status",BASEConstants.M_GENERIC_TABLE,BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<DepartmentDM> beanDepartment = null;
	// local variables declaration
	private Long companyid;
	private String departId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(Department.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Department() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Department() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting department UI");
		// Department Name text field
		tfDeptname = new GERPTextField("Department Name");
		tfDeptname.setMaxLength(25);
		// create form layouts to hold the input items
		flDeptName = new FormLayout();
		flDeptStatus = new FormLayout();
		// add the user input items into appropriate form layout
		flDeptName.addComponent(tfDeptname);
		flDeptStatus.addComponent(cbDeptStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flDeptName);
		hlUserInputLayout.addComponent(flDeptStatus);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<DepartmentDM> deptList = new ArrayList<DepartmentDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfDeptname.getValue() + ", " + cbDeptStatus.getValue());
		deptList = beandepartmant.getDepartmentList(companyid, tfDeptname.getValue(),(String) cbDeptStatus.getValue(), "F");
		recordCnt = deptList.size();
		beanDepartment = new BeanItemContainer<DepartmentDM>(DepartmentDM.class);
		beanDepartment.addAll(deptList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Dept. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanDepartment);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "deptid", "deptname", "deptstatus", "lastupdateddt",
				"lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Department", "Status", "Updated Date",
				"Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("deptid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfDeptname.setValue("");
		tfDeptname.setComponentError(null);
		cbDeptStatus.setValue(cbDeptStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editDepartment() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		departId = sltedRcd.getItemProperty("deptid").getValue().toString();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Dept. Id -> "
				+ departId);
		if (sltedRcd != null) {
			tfDeptname.setValue(sltedRcd.getItemProperty("deptname").getValue().toString());
			String stCode = sltedRcd.getItemProperty("deptstatus").getValue().toString();
			cbDeptStatus.setValue(stCode);
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbDeptStatus.setValue(cbDeptStatus.getItemIds().iterator().next());
		tfDeptname.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfDeptname.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Dept. ID " + departId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_DEPARTMENT);
		UI.getCurrent().getSession().setAttribute("audittablepk", departId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfDeptname.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfDeptname.setRequired(true);
		editDepartment();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfDeptname.setComponentError(null);
		if ((tfDeptname.getValue() == null) || tfDeptname.getValue().trim().length() == 0) {
			tfDeptname.setComponentError(new UserError(GERPErrorCodes.NULL_DEPT_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfDeptname.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		DepartmentDM depertmentobj = new DepartmentDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			depertmentobj = beanDepartment.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		depertmentobj.setCompanyid(companyid);
		depertmentobj.setDeptname(tfDeptname.getValue().toString());
		if (cbDeptStatus.getValue() != null) {
			depertmentobj.setDeptstatus((String)cbDeptStatus.getValue());
		}
		depertmentobj.setLastupdateddt(DateUtils.getcurrentdate());
		depertmentobj.setLastupdatedby(username);
		beandepartmant.saveDepartmentDetails(depertmentobj);
		UI.getCurrent().getSession().setAttribute("blIsEditMode", false);
		resetFields();
		loadSrchRslt();
	}
}
