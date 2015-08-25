/**
 * File Name	:	EmployeeEarning.java
 * Description	:	this class is used for add/edit EmployeeEarning details. 
 * Author		:	KAVITHA V M
 * Date			:	10-September-2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version       Date                 Modified By       Remarks
 * 0.1         	 10-September-2014    KAVITHA V M	    Initial Version       
 * 
 */
package com.gnts.hcm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.hcm.domain.mst.EarningsDM;
import com.gnts.hcm.domain.mst.EmployeeDtlsDM;
import com.gnts.hcm.domain.mst.GradeEarningsDM;
import com.gnts.hcm.domain.txn.EmployeeEarningDM;
import com.gnts.hcm.service.mst.EarningsService;
import com.gnts.hcm.service.mst.EmployeeDtlsService;
import com.gnts.hcm.service.mst.GradeEarningService;
import com.gnts.hcm.service.txn.EmployeeEarningService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class EmployeeEarning extends BaseUI {
	private EmployeeEarningService serviceEmployeeEarning = (EmployeeEarningService) SpringContextHelper
			.getBean("EmployeeEarning");
	private EarningsService serviceEarnings = (EarningsService) SpringContextHelper.getBean("Earnings");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private EmployeeDtlsService serviceEmpdetails = (EmployeeDtlsService) SpringContextHelper.getBean("Employeedtls");
	private GradeEarningService serviceGradeEarning = (GradeEarningService) SpringContextHelper
			.getBean("GradeEarnings");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private ComboBox cbEmpName, cbSearchEmpName, cbEarnCode, cbSearchEarnCode, cbFlatPercnt, cbStatus;
	private PopupDateField dfEffDt, dfLastPaidDt, dfNextPayDt;
	private CheckBox ckFlag;
	private TextField tfEarnPerct, tfEarnAmt, tfPreAmt, tfPrePercnt;
	// BeanItemContainer
	private BeanItemContainer<EmployeeEarningDM> beanEmployeeEarn = null;
	// local variables declaration
	private Long companyId, earnId;
	private int recordCnt = 0;
	private String userName, empEarnId;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeEarningDM.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmployeeEarning() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside EmployeeEarning() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Printing EmployeeEarning UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setItemCaptionPropertyId("fullname");
		cbEmpName.setWidth("200");
		cbSearchEmpName = new GERPComboBox("Employee Name");
		cbSearchEmpName.setItemCaptionPropertyId("fullname");
		cbSearchEmpName.setWidth("200");
		loadEmployeeList();
		// Search Earn code combobox
		cbSearchEarnCode = new GERPComboBox("Earn Code");
		cbSearchEarnCode.setItemCaptionPropertyId("earnCode");
		cbSearchEarnCode.setWidth("180");
		cbSearchEarnCode.setImmediate(true);
		cbEarnCode = new GERPComboBox("Earn Code");
		cbEarnCode.setItemCaptionPropertyId("earnCode");
		cbEarnCode.setWidth("180");
		cbEarnCode.setImmediate(true);
		cbEarnCode.setReadOnly(false);
		cbEarnCode.addValueChangeListener(new ValueChangeListener() {
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
					GradeEarningsDM gradeEarningsDM = serviceGradeEarning.getGradeEarnList(null,
							employeeDtlsDM.getGradeid(), (Long) cbEarnCode.getValue(), "Active", "F").get(0);
					cbFlatPercnt.setReadOnly(false);
					cbFlatPercnt.setValue(gradeEarningsDM.getIsFlatPer());
					cbFlatPercnt.setReadOnly(true);
					if (gradeEarningsDM.getIsFlatPer().equals("Flat")) {
						tfEarnAmt.setValue(gradeEarningsDM.getMinVal() + "");
						tfEarnPerct.setReadOnly(false);
						tfEarnPerct.setValue(null);
						tfEarnPerct.setReadOnly(true);
					} else {
						tfEarnAmt.setValue("");
						tfEarnPerct.setReadOnly(false);
						tfEarnPerct.setValue(gradeEarningsDM.getEarnPercent() + "");
						tfEarnPerct.setReadOnly(true);
					}
				}
				catch (Exception e) {
				}
			}
		});
		loadEarnList();
		cbFlatPercnt = new GERPComboBox("Flat/Percent", BASEConstants.T_HCM_EMPLOYEE_EARNING,
				BASEConstants.FLAT_PERCENT);
		cbFlatPercnt.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbFlatPercnt.getValue() != null) {
					if (cbFlatPercnt.getValue().equals("Flat")) {
						tfEarnPerct.setComponentError(null);
						tfPrePercnt.setEnabled(false);
						tfEarnPerct.setEnabled(false);
						tfPrePercnt.setValue("0");
						tfEarnPerct.setValue(null);
						tfEarnAmt.setEnabled(true);
						tfPreAmt.setEnabled(false);
						tfEarnAmt.setValue(tfEarnAmt.getValue());
						tfPreAmt.setValue(tfPreAmt.getValue());
					} else if (cbFlatPercnt.getValue().equals("Percent")) {
						tfPreAmt.setComponentError(null);
						tfEarnAmt.setEnabled(false);
						tfPreAmt.setEnabled(false);
						tfEarnAmt.setValue("0");
						tfPreAmt.setValue("0");
						tfEarnPerct.setEnabled(true);
						tfPrePercnt.setEnabled(false);
						tfEarnPerct.setValue(tfEarnPerct.getValue());
						tfPrePercnt.setValue(tfPrePercnt.getValue());
					} else if (cbFlatPercnt.getValue() == null) {
						tfPreAmt.setEnabled(true);
						tfEarnAmt.setEnabled(true);
						tfEarnPerct.setEnabled(true);
						tfPrePercnt.setEnabled(true);
					}
				}
			}
		});
		tfEarnAmt = new GERPTextField("Earn Amount");
		tfEarnAmt.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				try {
					tfEarnAmt.setComponentError(null);
					if (tfEarnAmt.getValue() != null && tfEarnAmt.getValue().trim().length() > 0
							&& tfEarnAmt.getValue() != "0") {
						GradeEarningsDM gradeEarningsDM = serviceGradeEarning.getGradeEarnList(null, null,
								(Long) cbEarnCode.getValue(), "Active", "F").get(0);
						System.out.println("Min=" + gradeEarningsDM.getMinVal() + "\nMax="
								+ gradeEarningsDM.getMaxVal());
						if (((new BigDecimal(tfEarnAmt.getValue())).compareTo(gradeEarningsDM.getMinVal()) > 0)
								|| ((new BigDecimal(tfEarnAmt.getValue())).compareTo(gradeEarningsDM.getMaxVal())) < 0) {
							tfEarnAmt.setComponentError(new UserError("Enter earn amount between "
									+ gradeEarningsDM.getMinVal() + "\nto " + gradeEarningsDM.getMaxVal()));
						}
					}
				}
				catch (Exception e) {
				}
			}
		});
		tfEarnAmt.setValue("0");
		tfPreAmt = new GERPTextField("Prev. Amount");
		tfPreAmt.setValue("0");
		tfPreAmt.setEnabled(false);
		tfEarnPerct = new GERPTextField("Earn Percent");
		tfEarnPerct.setValue(null);
		tfPrePercnt = new GERPTextField("Prev. Percent");
		tfPrePercnt.setWidth("120");
		tfPrePercnt.setValue("0");
		tfPrePercnt.setEnabled(false);
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
			List<EmployeeDM> empList = serviceEmployee.getEmployeeList(null, null, null, null, companyId, null, null,
					null, null, "P");
			BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.setBeanIdProperty("employeeid");
			beanEmployeeDM.addAll(empList);
			cbEmpName.setContainerDataSource(beanEmployeeDM);
			BeanContainer<Long, EmployeeDM> beanEmployeeDM1 = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM1.setBeanIdProperty("employeeid");
			beanEmployeeDM1.addAll(empList);
			cbSearchEmpName.setContainerDataSource(beanEmployeeDM1);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadEarnList() {
		try {
			List<EarningsDM> earningList = serviceEarnings.getEarningList(earnId, null, null, companyId, null, "F");
			BeanContainer<Long, EarningsDM> beanEarning = new BeanContainer<Long, EarningsDM>(EarningsDM.class);
			beanEarning.setBeanIdProperty("earnId");
			beanEarning.addAll(earningList);
			cbEarnCode.setContainerDataSource(beanEarning);
			BeanContainer<Long, EarningsDM> beanEarning1 = new BeanContainer<Long, EarningsDM>(EarningsDM.class);
			beanEarning1.setBeanIdProperty("earnId");
			beanEarning1.addAll(earningList);
			cbSearchEarnCode.setContainerDataSource(beanEarning1);
		}
		catch (Exception ex) {
			logger.info("load Earnings Details" + ex);
		}
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<EmployeeEarningDM> listEmployeeEarning = new ArrayList<EmployeeEarningDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyId + ", " + (Long) cbEmpName.getValue() + ", " + (Long) cbEarnCode.getValue()
				+ (String) cbStatus.getValue());
		listEmployeeEarning = serviceEmployeeEarning.getempearningList(null, (Long) cbSearchEmpName.getValue(),
				(Long) cbSearchEarnCode.getValue(), (String) cbStatus.getValue(), "F");
		recordCnt = listEmployeeEarning.size();
		beanEmployeeEarn = new BeanItemContainer<EmployeeEarningDM>(EmployeeEarningDM.class);
		beanEmployeeEarn.addAll(listEmployeeEarning);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Got the EmployeeEarning. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanEmployeeEarn);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "empearnid", "employeeName", "earnCode", "isflatpercent",
				"earnpercent", "earnamt", "empearnstatus", "lastpdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Earn Code", "Flat/Percent",
				"Earn Percent", "Earn Amount", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("empearnid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
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
		flColumn1.addComponent(cbSearchEmpName);
		flColumn2.addComponent(cbSearchEarnCode);
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
		flColumn1.addComponent(cbEarnCode);
		flColumn1.addComponent(cbFlatPercnt);
		flColumn2.addComponent(tfEarnPerct);
		flColumn2.addComponent(tfEarnAmt);
		flColumn4.addComponent(dfEffDt);
		flColumn3.setMargin(true);
		flColumn2.addComponent(tfPreAmt);
		flColumn3.addComponent(tfPrePercnt);
		flColumn3.addComponent(dfLastPaidDt);
		flColumn3.addComponent(dfNextPayDt);
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
		cbSearchEmpName.setValue(null);
		cbSearchEarnCode.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		resetFields();
		editEmpEarning();
	}
	
	private void editEmpEarning() {
		if (tblMstScrSrchRslt.getValue() != null) {
			EmployeeEarningDM empEarning = beanEmployeeEarn.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cbEmpName.setReadOnly(false);
			cbEmpName.setValue(empEarning.getEmployeeid());
			cbEmpName.setReadOnly(true);
			cbFlatPercnt.setReadOnly(false);
			cbFlatPercnt.setValue(empEarning.getIsflatpercent());
			cbFlatPercnt.setReadOnly(true);
			cbEarnCode.setReadOnly(false);
			cbEarnCode.setValue(empEarning.getEarnid());
			cbEarnCode.setReadOnly(true);
			if (empEarning.getEarnpercent() != null) {
				tfEarnPerct.setReadOnly(false);
				tfEarnPerct.setValue(empEarning.getEarnpercent().toString());
			}
			if (empEarning.getEarnamt() != null) {
				tfEarnAmt.setValue(empEarning.getEarnamt().toString());
				if (empEarning.getEffdt() != null) {
					dfEffDt.setValue(empEarning.getEffdt());
				}
				if (empEarning.getPrevamt() != null) {
					tfPreAmt.setValue(empEarning.getPrevamt().toString());
				}
				if (empEarning.getPrevpercent() != null) {
					tfPrePercnt.setValue(empEarning.getPrevpercent().toString());
				}
				if (empEarning.getLastpaidt() != null) {
					dfLastPaidDt.setValue(empEarning.getLastpaidt());
				}
				if (empEarning.getNxtpytdt() != null) {
					dfNextPayDt.setValue(empEarning.getNxtpytdt());
				}
				if (empEarning.getArrearflag() != null) {
					if (empEarning.getArrearflag().equals("Y")) {
						ckFlag.setValue(true);
					} else {
						ckFlag.setValue(false);
					}
				}
				cbStatus.setValue(empEarning.getEmpearnstatus());
			}
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		tfEarnAmt.setComponentError(null);
		Boolean errorFlag = false;
		if (tfEarnAmt.getValue() == null && tfEarnAmt.getValue().trim().length() == 0) {
			tfEarnAmt.setComponentError(new UserError(""));
			errorFlag = true;
		} else {
			tfEarnAmt.setComponentError(null);
		}
		try {
			new BigDecimal(tfEarnAmt.getValue());
		}
		catch (Exception e) {
			tfEarnAmt.setComponentError(new UserError(""));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		EmployeeEarningDM employeeEarningDM = new EmployeeEarningDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			employeeEarningDM = beanEmployeeEarn.getItem(tblMstScrSrchRslt.getValue()).getBean();
			employeeEarningDM.setEarnamt(new BigDecimal(tfEarnAmt.getValue()));
			employeeEarningDM.setEffdt(dfEffDt.getValue());
			employeeEarningDM.setEmpearnstatus((String) cbStatus.getValue());
			employeeEarningDM.setLastupdatedby(userName);
			employeeEarningDM.setLastpdateddt(new Date());
			serviceEmployeeEarning.saveAndUpdate(employeeEarningDM);
			resetFields();
			loadSrchRslt();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for Empearn. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_EMP_EARNING);
		UI.getCurrent().getSession().setAttribute("audittablepk", empEarnId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		cbEmpName.setReadOnly(false);
		cbEarnCode.setReadOnly(false);
		assembleSearchLayout();
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		cbEmpName.setReadOnly(false);
		cbEmpName.setValue(null);
		cbEmpName.setReadOnly(true);
		cbEarnCode.setReadOnly(false);
		cbEarnCode.setValue(null);
		cbEarnCode.setReadOnly(true);
		cbFlatPercnt.setReadOnly(false);
		cbFlatPercnt.setValue(null);
		cbFlatPercnt.setReadOnly(true);
		tfEarnPerct.setReadOnly(false);
		tfEarnPerct.setValue(null);
		tfEarnPerct.setReadOnly(true);
		tfEarnAmt.setValue("0");
		tfEarnAmt.setComponentError(null);
		dfEffDt.setValue(null);
		tfPreAmt.setValue("0");
		tfPreAmt.setComponentError(null);
		tfPrePercnt.setValue("0");
		tfPrePercnt.setComponentError(null);
		dfLastPaidDt.setValue(null);
		dfNextPayDt.setValue(null);
		ckFlag.setValue(false);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
}
