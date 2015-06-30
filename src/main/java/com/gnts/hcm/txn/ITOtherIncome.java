/**
 * File Name 		: ITOtherIncome.java 
 * Description 		: this class is used for IT Other Income details. 
 * Author 			: Abdullah.H
 * Date 			: 23-Sep-2014
 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.

 * Version       Date           	 Modified By               Remarks
 * 0.1          23-Sep-2014    		 Abdullah.H		          Intial Version
 * 0.2			15-Oct-2014			 sudhakar				 Code Refctoring
 */
package com.gnts.hcm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.ParameterService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.ITOtherIncomeDM;
import com.gnts.hcm.service.txn.ITOtherIncomeService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ITOtherIncome extends BaseUI {
	private ITOtherIncomeService serviceITOtherIncomeService = (ITOtherIncomeService) SpringContextHelper
			.getBean("ITOtherIncome");
	private ParameterService serviceParameter = (ParameterService) SpringContextHelper.getBean("parameter");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfFinYear, tfIncomeAmt, tfApprovedAmt, tfIncomeDesc;
	private ComboBox cbEmpName, cbStatus;
	// Bean container
	private BeanItemContainer<ITOtherIncomeDM> beanITOtherIncomeDM = null;
	private Long companyId;
	private String loginUserName;
	private Long incomeamt = 0L;
	private Long approvedamount = 0L;
	private int recordCnt = 0;
	private Boolean errorFlag = false;
	private String primaryid;
	// Initialize Logger
	private Logger logger = Logger.getLogger(ITOtherIncome.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public ITOtherIncome() {
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Inside ITOtherIncome() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Painting ITOtherIncome UI");
		// text fields
		tfFinYear = new GERPTextField("Finance Year");
		tfFinYear.setWidth("200px");
		tfIncomeAmt = new GERPTextField("Income Amount");
		tfApprovedAmt = new GERPTextField("Approved Amount");
		tfIncomeDesc = new GERPTextField("Income Desc.");
		tfIncomeDesc.setHeight("43");
		// Combo Boxes
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setWidth("200px");
		tfFinYear.setReadOnly(false);
		tfFinYear.setValue(serviceParameter.getParameterValue("FM_FINYEAR", companyId, null));
		tfFinYear.setReadOnly(true);
		loadEmployee();
		cbEmpName.setItemCaptionPropertyId("fullname");
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// create form layouts to hold the input items
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		// add the user input items into appropriate form layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn1.addComponent(cbEmpName);
		flColumn2.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		hlUserInputLayout.removeAllComponents();
		// Remove all components in Search Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbEmpName);
		flColumn1.addComponent(tfFinYear);
		flColumn2.addComponent(tfIncomeAmt);
		flColumn2.addComponent(tfApprovedAmt);
		flColumn3.addComponent(tfIncomeDesc);
		flColumn4.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadEmployee() {
		BeanContainer<Long, EmployeeDM> bean = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		bean.setBeanIdProperty("employeeid");
		bean.addAll(servicebeanEmployee.getEmployeeList((String) cbEmpName.getValue(), null, null, null,
				null, null, null, null, null, "P"));
		cbEmpName.setContainerDataSource(bean);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<ITOtherIncomeDM> itOtherComeList = new ArrayList<ITOtherIncomeDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Search Parameters are "
				+ companyId + ", " + (Long) cbEmpName.getValue() + ", " + (String) cbStatus.getValue());
		itOtherComeList = serviceITOtherIncomeService.getItOtherIncomeList(null, (Long) cbEmpName.getValue(), null,
				(String) cbStatus.getValue(), "F");
		recordCnt = itOtherComeList.size();
		beanITOtherIncomeDM = new BeanItemContainer<ITOtherIncomeDM>(ITOtherIncomeDM.class);
		beanITOtherIncomeDM.addAll(itOtherComeList);
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Got the IT Other Income List result set");
		tblMstScrSrchRslt.setContainerDataSource(beanITOtherIncomeDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "incDeclId", "empName", "incomeAmt", "appAmt", "status",
				"verifiedDt", "verifiedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Income Amount",
				"Approved Amount", "Status", "Verified Date", "Verified By" });
		tblMstScrSrchRslt.setColumnAlignment("incDeclId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("verifiedBy", "No.of Records:" + recordCnt);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbEmpName.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		cbEmpName.setRequired(true);
		tfApprovedAmt.setRequired(true);
		tfIncomeAmt.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	private void editItOtherIncome() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Editing the selected record");
		if (tblMstScrSrchRslt.getValue() != null) {
			ITOtherIncomeDM editItInComeObj = beanITOtherIncomeDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			if (editItInComeObj.getEmpId() != null) {
				cbEmpName.setValue(editItInComeObj.getEmpId());
			}
			if (editItInComeObj.getFinYear() != null) {
				tfFinYear.setReadOnly(false);
				tfFinYear.setValue(editItInComeObj.getFinYear());
			}
			if (editItInComeObj.getIncomeAmt() != null) {
				tfIncomeAmt.setValue(editItInComeObj.getIncomeAmt().toString());
			}
			if (editItInComeObj.getAppAmt() != null) {
				tfApprovedAmt.setValue(editItInComeObj.getAppAmt().toString());
			}
			if (editItInComeObj.getIncomeDesc() != null) {
				tfIncomeDesc.setValue(editItInComeObj.getIncomeDesc());
			}
			if (editItInComeObj.getStatus() != null) {
				cbStatus.setValue(editItInComeObj.getStatus());
			}
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		cbEmpName.setRequired(true);
		tfApprovedAmt.setRequired(true);
		tfIncomeAmt.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editItOtherIncome();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		tfIncomeAmt.setComponentError(null);
		tfApprovedAmt.setComponentError(null);
		cbEmpName.setComponentError(null);
		errorFlag = false;
		if (cbEmpName.getValue() == null) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			errorFlag = true;
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfIncomeAmt.getValue());
			if (achievedQty < 0) {
				tfIncomeAmt.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorFlag = true;
			}
		}
		catch (Exception e) {
			if ((tfIncomeAmt.getValue() == null) || tfIncomeAmt.getValue().trim().length() == 0) {
				tfIncomeAmt.setComponentError(new UserError(GERPErrorCodes.NULL_INCOME_SALARY));
				errorFlag = true;
			} else {
				tfIncomeAmt.setComponentError(new UserError("Enter Income Salary"));
				errorFlag = true;
			}
		}
		Long achievedQty1;
		try {
			achievedQty1 = Long.valueOf(tfApprovedAmt.getValue());
			if (achievedQty1 < 0) {
				tfApprovedAmt.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorFlag = true;
			}
		}
		catch (Exception e) {
			if ((tfApprovedAmt.getValue() == null) || tfApprovedAmt.getValue().trim().length() == 0) {
				tfApprovedAmt.setComponentError(new UserError(GERPErrorCodes.NULL_APPROVED_AMOUNT));
				errorFlag = true;
			} else {
				tfApprovedAmt.setComponentError(new UserError(GERPErrorCodes.NULL_APPROVED_AMOUNT));
				errorFlag = true;
			}
		}
		approvedamount = Long.valueOf(tfApprovedAmt.getValue().toString());
		incomeamt = Long.valueOf(tfIncomeAmt.getValue().toString());
		if (approvedamount > incomeamt) {
			tfIncomeAmt.setComponentError(new UserError(GERPErrorCodes.NULL_APPROVED_AMOUNT_COMPARE));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Saving Data... ");
			ITOtherIncomeDM itOtherIncomeObj = new ITOtherIncomeDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				itOtherIncomeObj = beanITOtherIncomeDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			itOtherIncomeObj.setEmpId((Long) cbEmpName.getValue());
			itOtherIncomeObj.setFinYear(tfFinYear.getValue());
			itOtherIncomeObj.setAppAmt(new BigDecimal(tfApprovedAmt.getValue()));
			itOtherIncomeObj.setIncomeAmt(new BigDecimal(tfIncomeAmt.getValue()));
			itOtherIncomeObj.setIncomeDesc(tfIncomeDesc.getValue());
			if (cbStatus.getValue() != null) {
				itOtherIncomeObj.setStatus((String) cbStatus.getValue());
			}
			itOtherIncomeObj.setVerifiedDt(DateUtils.getcurrentdate());
			itOtherIncomeObj.setVerifiedBy(loginUserName);
			serviceITOtherIncomeService.saveItInvest(itOtherIncomeObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Getting audit record for IT Other Income Group ID " + primaryid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_IT_OTHER_INCOME);
		UI.getCurrent().getSession().setAttribute("audittablepk", primaryid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Canceling action ");
		assembleSearchLayout();
		cbEmpName.setRequired(false);
		tfApprovedAmt.setRequired(false);
		tfIncomeAmt.setRequired(false);
		tfIncomeDesc.setRequired(false);
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		cbEmpName.setValue(null);
		tfApprovedAmt.setValue(null);
		tfIncomeAmt.setValue(null);
		tfIncomeDesc.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbEmpName.setComponentError(null);
		tfIncomeAmt.setComponentError(null);
		tfIncomeDesc.setComponentError(null);
		tfApprovedAmt.setComponentError(null);
	}
}
