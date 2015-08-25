/**
 * File Name	:	EmployeeProximity.java
 * Description	:	this class is used for add/edit EmployeeProximity details. 
 * Author		:	KAVITHA V M
 * Date			:	04-September-2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version       Date                Modified By       Remarks
 * 0.1         	04-September-2014    KAVITHA V M	   Initial Version       
 * 
 */
package com.gnts.hcm.txn;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.EmployeeProximityDM;
import com.gnts.hcm.service.txn.EmployeeProximityService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class EmployeeProximity extends BaseUI {
	// Bean creation
	private EmployeeProximityService serviceEmployeeProximity = (EmployeeProximityService) SpringContextHelper
			.getBean("EmployeeProximity");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfProximityNo;
	private ComboBox cbEmployeeName;
	private PopupDateField dfValidFrm;
	private PopupDateField dfValidUntil;
	private TextArea taRemarks;
	private ComboBox cbStatus;
	// BeanItemContainer
	private BeanItemContainer<EmployeeProximityDM> beanEmployeeProximityDM = null;
	// local variables declaration
	private Long companyId, employeeId;
	private int recordCnt = 0;
	private String userName;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeProximity.class);
	private String proximityId;
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmployeeProximity() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside EmployeeProximity() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Printing EmployeeProximity UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		tfProximityNo = new GERPTextField("Proximity No.");
		tfProximityNo.setWidth("150");
		tfProximityNo.setMaxLength(25);
		cbEmployeeName = new GERPComboBox("Employee Name");
		cbEmployeeName.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		dfValidFrm = new GERPPopupDateField("Valid From");
		dfValidFrm.setRequired(true);
		dfValidFrm.setWidth("150");
		dfValidUntil = new GERPPopupDateField("Valid Until");
		dfValidUntil.setRequired(true);
		dfValidUntil.setImmediate(true);
		dfValidUntil.setWidth("150");
		taRemarks = new GERPTextArea("Remarks");
		taRemarks.setHeight("50");
		taRemarks.setWidth("130");
		// // build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		tblMstScrSrchRslt.setPageLength(14);
		List<EmployeeProximityDM> listEmployeeProximity = new ArrayList<EmployeeProximityDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyId + ", " + (String) tfProximityNo.getValue() + ", " + (Long) cbEmployeeName.getValue()
				+ (String) cbStatus.getValue());
		listEmployeeProximity = serviceEmployeeProximity.getempproxmtyList(null, (String) tfProximityNo.getValue(),
				(Long) cbEmployeeName.getValue(), (String) cbStatus.getValue(), "F");
		recordCnt = listEmployeeProximity.size();
		beanEmployeeProximityDM = new BeanItemContainer<EmployeeProximityDM>(EmployeeProximityDM.class);
		beanEmployeeProximityDM.addAll(listEmployeeProximity);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Got the EmployeeProximity. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanEmployeeProximityDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "empproxmtyid", "proxmtyno", "employeename", "remarks",
				"proxmtystatus", "lastupdateddt", "lastupdatdby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Proximity No.", "Employee Name", "Remarks",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("empproxmtyid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatdby", "No.of Records : " + recordCnt);
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
		flColumn2.addComponent(cbEmployeeName);
		flColumn1.addComponent(tfProximityNo);
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
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(tfProximityNo);
		flColumn1.addComponent(cbEmployeeName);
		flColumn2.addComponent(dfValidFrm);
		flColumn2.addComponent(dfValidUntil);
		flColumn3.addComponent(taRemarks);
		flColumn3.setMargin(true);
		flColumn4.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
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
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbEmployeeName.setValue(null);
		tfProximityNo.setValue("");
		lblNotification.setIcon(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
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
		tfProximityNo.setRequired(true);
		cbEmployeeName.setRequired(true);
		dfValidFrm.setRequired(false);
		dfValidUntil.setRequired(false);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		resetFields();
		tfProximityNo.setRequired(true);
		cbEmployeeName.setRequired(true);
		dfValidFrm.setRequired(false);
		dfValidUntil.setRequired(false);
		editProximity();
	}
	
	private void editProximity() {
		if (tblMstScrSrchRslt.getValue() != null) {
			EmployeeProximityDM employeeProximityDM = beanEmployeeProximityDM.getItem(tblMstScrSrchRslt.getValue())
					.getBean();
			employeeProximityDM.getEmpproxmtyid();
			if (employeeProximityDM.getProxmtyno() != null) {
				tfProximityNo.setValue(employeeProximityDM.getProxmtyno().toString());
			}
			cbEmployeeName.setValue(employeeProximityDM.getEmployeeid());
			if (employeeProximityDM.getValidfrom() != null) {
				dfValidFrm.setValue(employeeProximityDM.getValidfrom());
			}
			if (employeeProximityDM.getValiduntil() != null) {
				dfValidUntil.setValue(employeeProximityDM.getValiduntil());
			}
			if (employeeProximityDM.getRemarks() != null) {
				taRemarks.setValue(employeeProximityDM.getRemarks().toString());
			}
			cbStatus.setValue(employeeProximityDM.getProxmtystatus());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		tfProximityNo.setComponentError(null);
		Boolean errorFlag = false;
		if ((tfProximityNo.getValue() == null) || tfProximityNo.getValue().trim().length() == 0) {
			tfProximityNo.setComponentError(new UserError(GERPErrorCodes.NULL_PROXIMITY_NO));
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + tfProximityNo.getValue());
		}
		if (cbEmployeeName.getValue() == null) {
			cbEmployeeName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
		if ((dfValidFrm.getValue() != null) || (dfValidUntil.getValue() != null)) {
			if (dfValidFrm.getValue().after(dfValidUntil.getValue())) {
				dfValidUntil.setComponentError(new UserError(GERPErrorCodes.DATE_OF_UNTIL_VALIDATION));
				logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
						+ "Throwing ValidationException. User data is > " + cbEmployeeName.getValue());
				throw new ERPException.ValidationException();
			}
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for proximity. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_EMP_PROXIMITY);
		UI.getCurrent().getSession().setAttribute("audittablepk", proximityId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		tblMstScrSrchRslt.setValue(null);
		cbEmployeeName.setRequired(false);
		tfProximityNo.setRequired(false);
		dfValidFrm.setRequired(false);
		dfValidUntil.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		tfProximityNo.setValue("");
		tfProximityNo.setComponentError(null);
		cbEmployeeName.setValue(null);
		cbEmployeeName.setComponentError(null);
		dfValidFrm.setValue(new Date());
		dfValidUntil.setValue(addDays(new Date(), 7));
		dfValidFrm.setComponentError(null);
		dfValidUntil.setComponentError(null);
		taRemarks.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.setBeanIdProperty("employeeid");
			beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyId,
					employeeId, null, null, null, "P"));
			cbEmployeeName.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	@Override
	protected void saveDetails() {
		
		try {
			validateDetails();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
			EmployeeProximityDM employeeProximity = new EmployeeProximityDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				employeeProximity = beanEmployeeProximityDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			employeeProximity.setProxmtyno(tfProximityNo.getValue().toString());
			employeeProximity.setEmployeeid((Long) cbEmployeeName.getValue());
			if (dfValidFrm.getValue() != null) {
				employeeProximity.setValidfrom(dfValidFrm.getValue());
			}
			if (dfValidUntil.getValue() != null) {
				employeeProximity.setValiduntil(dfValidUntil.getValue());
			}
			employeeProximity.setRemarks(taRemarks.getValue().toString());
			if (cbStatus.getValue() != null) {
				employeeProximity.setProxmtystatus((String) cbStatus.getValue());
			}
			employeeProximity.setLastupdateddt(DateUtils.getcurrentdate());
			employeeProximity.setLastupdatdby(userName);
			serviceEmployeeProximity.saveAndUpdate(employeeProximity);
		}
		catch (Exception e) {
			logger.info("SUCCESSS");
			e.printStackTrace();
		}
		resetFields();
		loadSrchRslt();
	}
	
	private Date addDays(Date d, int days) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = sdf.format(d);
		Date parsedDate = null;
		try {
			parsedDate = sdf.parse(strDate);
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.warn("calculate days" + e);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, days);
		return now.getTime();
	}
}
