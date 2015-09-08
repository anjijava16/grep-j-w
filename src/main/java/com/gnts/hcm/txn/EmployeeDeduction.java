/**
 * File Name	:	EmployeeDeduction.java
 * Description	:	this class is used for add/edit EmployeeDeduction details. 
 * Author		:	KAVITHA V M
 * Date			:	11-September-2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date                 Modified By             Remarks
 * 0.1         	   11-September-2014    KAVITHA V M	            Initial Version       
 * 
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
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.DeductionDM;
import com.gnts.hcm.domain.mst.EmployeeDtlsDM;
import com.gnts.hcm.domain.mst.GradeDeductionDM;
import com.gnts.hcm.domain.txn.EmployeeDeductionDM;
import com.gnts.hcm.service.mst.DeductionService;
import com.gnts.hcm.service.mst.EmployeeDtlsService;
import com.gnts.hcm.service.mst.GradeDeductionService;
import com.gnts.hcm.service.txn.EmployeeDeductionService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class EmployeeDeduction extends BaseUI {
	private EmployeeDeductionService serviceEmployeeDeduction = (EmployeeDeductionService) SpringContextHelper
			.getBean("EmployeeDeduction");
	private DeductionService serviceDeduction = (DeductionService) SpringContextHelper.getBean("Deduction");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private EmployeeDtlsService serviceEmpdetails = (EmployeeDtlsService) SpringContextHelper.getBean("Employeedtls");
	private GradeDeductionService serviceGradeDeduction = (GradeDeductionService) SpringContextHelper
			.getBean("GradeDeduction");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private ComboBox cbEmpName, cbDedcnCode, cbFlatPercnt, cbStatus;
	private PopupDateField dfEffDt, dfLastPaidDt, dfNextPayDt;
	private CheckBox ckFlag;
	private TextField tfDecnPerct, tfDecnAmt, tfPreAmt, tfPrePercnt;
	// BeanItemContainer
	private BeanItemContainer<EmployeeDeductionDM> beanEmployeeDecn = null;
	// local variables declaration
	private Long companyId;
	private int recordCnt = 0;
	private String userName, deductionId;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeDeductionDM.class);
	private BigDecimal prevsamt, prevspt;
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmployeeDeduction() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside EmployeeDeduction() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Printing EmployeeDeduction UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setItemCaptionPropertyId("fullname");
		cbEmpName.setWidth("180");
		loadEmployeeList();
		cbEmpName.setComponentError(null);
		cbDedcnCode = new GERPComboBox("Deduction Code");
		cbDedcnCode.setItemCaptionPropertyId("deductionCode");
		cbDedcnCode.setWidth("180");
		cbDedcnCode.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					EmployeeDtlsDM employeeDtlsDM = serviceEmpdetails.getEmployeeDtls(null,
							((Long) cbEmpName.getValue()), null, null, null, null, "F", null).get(0);
					GradeDeductionDM gradeDeductionDM = serviceGradeDeduction.getGradeEarnList(null,
							employeeDtlsDM.getGradeid(), (Long) cbDedcnCode.getValue(), "Active", "F").get(0);
					cbFlatPercnt.setValue(gradeDeductionDM.getIsFlatPer());
					if (gradeDeductionDM.getIsFlatPer().equals("Flat")) {
						tfDecnAmt.setValue(gradeDeductionDM.getMinVal() + "");
						tfDecnPerct.setValue("");
					} else {
						tfDecnAmt.setValue("");
						tfDecnPerct.setValue(gradeDeductionDM.getDednPercent() + "");
					}
				}
				catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
		});
		loadDedcnList();
		cbFlatPercnt = new GERPComboBox("Flat/Percent", BASEConstants.T_HCM_EMPLOYEE_DEDUCTION,
				BASEConstants.FLAT_PERCENT);
		cbFlatPercnt.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbFlatPercnt.getValue() != null) {
					if (cbFlatPercnt.getValue().equals("Flat")) {
						tfDecnPerct.setComponentError(null);
						tfPrePercnt.setEnabled(false);
						tfDecnPerct.setEnabled(false);
						tfDecnPerct.setValue("0");
						tfPrePercnt.setValue("0");
						tfDecnAmt.setEnabled(true);
						tfPreAmt.setEnabled(false);
						tfPreAmt.setValue(tfPreAmt.getValue());
					} else if (cbFlatPercnt.getValue().equals("Percent")) {
						tfPreAmt.setComponentError(null);
						tfDecnAmt.setEnabled(false);
						tfPreAmt.setEnabled(false);
						tfDecnAmt.setValue("0");
						tfPreAmt.setValue("0");
						tfDecnPerct.setEnabled(true);
						tfPrePercnt.setEnabled(false);
						tfDecnPerct.setValue(tfDecnPerct.getValue());
						tfPrePercnt.setValue(tfPrePercnt.getValue());
					} else if (cbFlatPercnt.getValue() == null) {
						tfPreAmt.setEnabled(false);
						tfDecnAmt.setEnabled(true);
						tfDecnPerct.setEnabled(true);
						tfPrePercnt.setEnabled(false);
					}
				}
			}
		});
		tfDecnAmt = new GERPTextField("Deduction Amount");
		tfDecnAmt.setValue("0");
		tfPreAmt = new GERPTextField("Prev. Amount");
		tfPreAmt.setValue("0");
		tfDecnPerct = new GERPTextField("Deduction Percent");
		tfDecnPerct.setValue("0");
		tfPrePercnt = new GERPTextField("Prev. Percent");
		tfPrePercnt.setValue("0");
		tfPrePercnt.setWidth("120");
		dfEffDt = new GERPPopupDateField("Effective Date");
		dfEffDt.setWidth("110");
		dfLastPaidDt = new GERPPopupDateField("LastPaid Date");
		dfLastPaidDt.setEnabled(false);
		dfNextPayDt = new GERPPopupDateField("NextPay Date");
		dfNextPayDt.setEnabled(false);
		ckFlag = new CheckBox();
		ckFlag.setCaption("Arrear Flag:Y/N");
		// // build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		btnAdd.setVisible(false);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.setBeanIdProperty("employeeid");
			beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, companyId, null, null, null, "P"));
			cbEmpName.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadDedcnList() {
		try {
			BeanContainer<Long, DeductionDM> beanDeduction = new BeanContainer<Long, DeductionDM>(DeductionDM.class);
			beanDeduction.setBeanIdProperty("deductionId");
			beanDeduction.addAll(serviceDeduction.getDuctionList(null, null, companyId, null, null, "F"));
			cbDedcnCode.setContainerDataSource(beanDeduction);
		}
		catch (Exception ex) {
			logger.info("load Deductions Details" + ex);
		}
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<EmployeeDeductionDM> loadEmpDedcnList = new ArrayList<EmployeeDeductionDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
					+ companyId + ", " + (Long) cbEmpName.getValue() + ", " + (Long) cbDedcnCode.getValue()
					+ (String) cbStatus.getValue());
			loadEmpDedcnList = serviceEmployeeDeduction.getempdeductionlist(null, (Long) cbEmpName.getValue(),
					(Long) cbDedcnCode.getValue(), (String) cbStatus.getValue(), "F");
			recordCnt = loadEmpDedcnList.size();
			beanEmployeeDecn = new BeanItemContainer<EmployeeDeductionDM>(EmployeeDeductionDM.class);
			beanEmployeeDecn.addAll(loadEmpDedcnList);
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Got the EmployeeDeduction. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanEmployeeDecn);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "empdednid", "empName", "dedcnCode", "isflatpt",
					"dednamt", "dednpt", "empdednstatus", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Deduction Code",
					"Flat/Percent", "Deduction Amount", "Deduction Percent", "Status", "Last Updated Date",
					"Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("empdednid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
		flColumn1.addComponent(cbEmpName);
		flColumn2.addComponent(cbDedcnCode);
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
		flColumn1.addComponent(cbEmpName);
		flColumn1.addComponent(cbDedcnCode);
		flColumn1.addComponent(cbFlatPercnt);
		flColumn2.addComponent(tfDecnPerct);
		flColumn2.addComponent(tfDecnAmt);
		flColumn2.addComponent(tfPreAmt);
		flColumn4.addComponent(dfEffDt);
		flColumn3.addComponent(tfPrePercnt);
		flColumn3.addComponent(dfLastPaidDt);
		flColumn3.addComponent(dfNextPayDt);
		flColumn3.setMargin(true);
		flColumn4.addComponent(ckFlag);
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
		cbEmpName.setValue(null);
		cbDedcnCode.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
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
		cbEmpName.setRequired(true);
		cbDedcnCode.setRequired(true);
		cbFlatPercnt.setRequired(true);
		tfPreAmt.setEnabled(false);
		tfDecnAmt.setEnabled(true);
		tfDecnPerct.setEnabled(true);
		tfPrePercnt.setEnabled(false);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		cbEmpName.setRequired(true);
		cbDedcnCode.setRequired(true);
		cbFlatPercnt.setRequired(true);
		resetFields();
		editEmpDeduction();
	}
	
	private void editEmpDeduction() {
		try {
			if (tblMstScrSrchRslt.getValue() != null) {
				EmployeeDeductionDM employeeDeduction = beanEmployeeDecn.getItem(tblMstScrSrchRslt.getValue())
						.getBean();
				cbEmpName.setValue(employeeDeduction.getEmployeeid());
				cbDedcnCode.setValue(employeeDeduction.getDednid());
				cbFlatPercnt.setValue(employeeDeduction.getIsflatpt());
				if (employeeDeduction.getDednpt() != null) {
					tfDecnPerct.setValue(employeeDeduction.getDednpt().toString());
				}
				prevspt = (new BigDecimal(tfDecnPerct.getValue()));
				if (employeeDeduction.getDednamt() != null) {
					tfDecnAmt.setValue(employeeDeduction.getDednamt().toString());
					prevsamt = (new BigDecimal(tfDecnAmt.getValue()));
					if (employeeDeduction.getEffdt() != null) {
						dfEffDt.setValue(employeeDeduction.getEffdt());
					}
					if (employeeDeduction.getPreamt() != null) {
						tfPreAmt.setValue(employeeDeduction.getPreamt().toString());
					}
					if (employeeDeduction.getPrevpt() != null) {
						tfPrePercnt.setValue(employeeDeduction.getPrevpt().toString());
					}
					if (employeeDeduction.getLastpaiddt() != null) {
						dfLastPaidDt.setValue(employeeDeduction.getLastpaiddt());
					}
					if (employeeDeduction.getNxtpymtdt() != null) {
						dfNextPayDt.setValue(employeeDeduction.getNxtpymtdt());
					}
					if (employeeDeduction.getArrearflag().equals("Y")) {
						ckFlag.setValue(true);
					} else {
						ckFlag.setValue(false);
					}
					cbStatus.setValue(employeeDeduction.getEmpdednstatus());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if (cbEmpName.getValue() == null) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMP_SHIFT));
			errorFlag = true;
		} else {
			cbEmpName.setComponentError(null);
		}
		if (cbDedcnCode.getValue() == null) {
			cbDedcnCode.setComponentError(new UserError(GERPErrorCodes.NULL_EMP_DEDCTNCOD));
			errorFlag = true;
		} else {
			cbDedcnCode.setComponentError(null);
		}
		if (cbFlatPercnt.getValue() == null) {
			cbFlatPercnt.setComponentError(new UserError(GERPErrorCodes.NULL_EMP_FLATPER));
			errorFlag = true;
		} else {
			cbFlatPercnt.setComponentError(null);
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
		EmployeeDeductionDM employeeDeduction = new EmployeeDeductionDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			employeeDeduction = beanEmployeeDecn.getItem(tblMstScrSrchRslt.getValue()).getBean();
			employeeDeduction.setPreamt(prevsamt);
			employeeDeduction.setPrevpt(prevspt);
		} else {
			employeeDeduction.setPreamt(new BigDecimal("0"));
			employeeDeduction.setPrevpt(new BigDecimal("0"));
		}
		employeeDeduction.setEmployeeid((Long) cbEmpName.getValue());
		employeeDeduction.setDednid((Long) cbDedcnCode.getValue());
		employeeDeduction.setIsflatpt(cbFlatPercnt.getValue().toString());
		employeeDeduction.setDednpt(new BigDecimal(tfDecnPerct.getValue()));
		employeeDeduction.setDednamt(new BigDecimal(tfDecnAmt.getValue()));
		if (dfEffDt.getValue() != null) {
			employeeDeduction.setEffdt(dfEffDt.getValue());
		}
		if (dfLastPaidDt.getValue() != null) {
			employeeDeduction.setLastpaiddt(dfLastPaidDt.getValue());
		}
		if (dfNextPayDt.getValue() != null) {
			employeeDeduction.setNxtpymtdt(dfNextPayDt.getValue());
		}
		if (ckFlag.getValue().equals(true)) {
			employeeDeduction.setArrearflag("Y");
		} else {
			employeeDeduction.setArrearflag("N");
		}
		if (ckFlag.getValue().equals(true)) {
			employeeDeduction.setArrearflag("Y");
		} else if (ckFlag.getValue().equals(false)) {
			employeeDeduction.setArrearflag("N");
		}
		if (cbStatus.getValue() != null) {
			employeeDeduction.setEmpdednstatus((String) cbStatus.getValue());
		}
		employeeDeduction.setLastupdateddt(DateUtils.getcurrentdate());
		employeeDeduction.setLastupdatedby(userName);
		serviceEmployeeDeduction.saveAndUpdate(employeeDeduction);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for deduction. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_EMPLOYEE_DEDUCTION);
		UI.getCurrent().getSession().setAttribute("audittablepk", deductionId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		cbEmpName.setRequired(false);
		cbDedcnCode.setRequired(false);
		cbFlatPercnt.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		cbEmpName.setValue(null);
		cbEmpName.setComponentError(null);
		cbDedcnCode.setValue(null);
		cbDedcnCode.setComponentError(null);
		cbFlatPercnt.setValue(null);
		cbFlatPercnt.setComponentError(null);
		tfDecnPerct.setValue("0");
		tfDecnPerct.setComponentError(null);
		tfDecnAmt.setValue("0");
		tfDecnAmt.setComponentError(null);
		dfEffDt.setValue(null);
		tfPreAmt.setValue("0");
		tfPreAmt.setComponentError(null);
		tfPrePercnt.setValue("0");
		tfPrePercnt.setComponentError(null);
		dfLastPaidDt.setValue(null);
		dfNextPayDt.setValue(null);
		ckFlag.setValue(false);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbEmpName.setComponentError(null);

	}
}
