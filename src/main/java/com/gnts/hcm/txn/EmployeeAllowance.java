/**
 * File Name 		: EmployeeAllowance.java 
 * Description 		: this class is used for add/edit EmployeeAllowance  details. 
 * Author 			: Madhu T
 * Date 			: sep-25-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks 
 * 0.1          sep-25-2014       	Madhu T	        Initial Version
 **/
package com.gnts.hcm.txn;

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
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.AllowanceDM;
import com.gnts.hcm.domain.mst.EmployeeDtlsDM;
import com.gnts.hcm.domain.mst.GradeAllowanceDM;
import com.gnts.hcm.domain.txn.EmployeeAllowanceDM;
import com.gnts.hcm.service.mst.AllowanceService;
import com.gnts.hcm.service.mst.EmployeeDtlsService;
import com.gnts.hcm.service.mst.GradeAllowanceService;
import com.gnts.hcm.service.txn.EmployeeAllowanceService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class EmployeeAllowance extends BaseUI {
	// Bean creation
	private GradeAllowanceService serviceGradeAllowance = (GradeAllowanceService) SpringContextHelper
			.getBean("GradeAllowance");
	private EmployeeAllowanceService serviceEmpAllowance = (EmployeeAllowanceService) SpringContextHelper
			.getBean("EmployeeAllowance");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private AllowanceService serviceAllowance = (AllowanceService) SpringContextHelper.getBean("Allowance");
	private EmployeeDtlsService serviceEmpdetails = (EmployeeDtlsService) SpringContextHelper.getBean("Employeedtls");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfAllownceAmt, tfAllBal, tfAlwncPercent;
	private CheckBox chArrearFlag, chAutoPay;
	private ComboBox cbStatus, cbEmpName, cbAlwncDesc, cbFlatPercent;
	private DateField dtEffectiveDt;
	// BeanItemContainer
	private BeanItemContainer<EmployeeAllowanceDM> beanEmpAllowanceDM = null;
	// local variables declaration
	private Long companyid;
	private String pkEmpAlwncId;
	private BigDecimal allPercent;
	private BigDecimal basictotal = new BigDecimal("0");
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeAllowance.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmployeeAllowance() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeAllowance() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting EmployeeAllowance UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Arrear Flag checkbox
		chArrearFlag = new CheckBox("Arrear Flag");
		// Is Flat percent combobox
		cbFlatPercent = new GERPComboBox("Flat/Percent", BASEConstants.M_HCM_GRADE_EARNING, BASEConstants.FLAT_PERCENT);
		cbFlatPercent.setImmediate(true);
		cbFlatPercent.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbFlatPercent.getValue() != null) {
					if (cbFlatPercent.getValue().equals("Flat")) {
						tfAlwncPercent.setReadOnly(false);
						tfAlwncPercent.setValue("0");
						tfAlwncPercent.setReadOnly(true);
						tfAllownceAmt.setReadOnly(false);
						tfAllBal.setReadOnly(false);
					} else if (cbFlatPercent.getValue().equals("Percent")) {
						tfAlwncPercent.setReadOnly(false);
						tfAlwncPercent.setValue("0");
						cbAlwncDesc.setReadOnly(false);
						tfAllownceAmt.setReadOnly(false);
						tfAllownceAmt.setValue("0");
						tfAllownceAmt.setReadOnly(true);
						tfAllBal.setReadOnly(false);
						tfAllBal.setValue("0");
						tfAllBal.setReadOnly(true);
					} else if (cbFlatPercent.getValue() == null) {
						tfAlwncPercent.setReadOnly(false);
						cbAlwncDesc.setReadOnly(false);
						tfAllownceAmt.setReadOnly(false);
						tfAllBal.setReadOnly(false);
					}
				}
			}
		});
		// Allowance Amount TextField
		tfAllownceAmt = new GERPTextField("Allowance Amount");
		tfAllownceAmt.setValue("0");
		// Employee Name Combo Box
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setItemCaptionPropertyId("fullname");
		loadEmpList();
		// Allowance Name ComboBox
		cbAlwncDesc = new GERPComboBox("Allowance Name");
		cbAlwncDesc.setItemCaptionPropertyId("alowncDesc");
		cbAlwncDesc.addValueChangeListener(new ValueChangeListener() {
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
					GradeAllowanceDM gradeAllowanceDM = serviceGradeAllowance.getGradeAllowanceList(null,
							employeeDtlsDM.getGradeid(), (Long) cbAlwncDesc.getValue(), null, "F").get(0);
					cbFlatPercent.setValue(gradeAllowanceDM.getIsFlatPer());
					if (gradeAllowanceDM.getIsFlatPer().equals("Flat")) {
						tfAllownceAmt.setValue(gradeAllowanceDM.getMinVal() + "");
						tfAlwncPercent.setValue("");
					} else {
						tfAllownceAmt.setValue("");
						tfAlwncPercent.setValue(gradeAllowanceDM.getMinPer() + "");
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		loadAllowanceList();
		// Effective Date field
		dtEffectiveDt = new GERPPopupDateField("Effective Date");
		// Auto Pay CheckBox
		chAutoPay = new CheckBox("Auto Pay");
		chAutoPay.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (chAutoPay.getValue().equals(true)) {
					chArrearFlag.setEnabled(true);
					tfAllBal.setEnabled(false);
				} else {
					chArrearFlag.setEnabled(false);
					tfAllBal.setEnabled(true);
				}
			}
		});
		chAutoPay.setImmediate(true);
		// Allowance Balance TextField
		tfAllBal = new GERPTextField("Allowance Balance");
		tfAllBal.setValue("0");
		cbEmpName.setImmediate(true);
		cbEmpName.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Long empId = null;
				if (cbEmpName.getValue() != null) {
					empId = ((Long.valueOf(cbEmpName.getValue().toString())));
				}
				List<EmployeeAllowanceDM> empAlwncObj = new ArrayList<EmployeeAllowanceDM>();
				empAlwncObj = serviceEmpAllowance.getEmpAlwncList(empId, "BASIC");
				for (EmployeeAllowanceDM obj : empAlwncObj) {
					basictotal = new BigDecimal(obj.getAllowamt().toString());
					getAllowanceAmount();
				}
			}
		});
		// All Percentage TextField
		tfAlwncPercent = new GERPTextField("Allowance Percentage");
		tfAlwncPercent.setImmediate(true);
		tfAlwncPercent.setValue("0");
		tfAlwncPercent.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Long empId = null;
				if (cbEmpName.getValue() != null) {
					empId = ((Long.valueOf(cbEmpName.getValue().toString())));
				}
				List<EmployeeAllowanceDM> empAlwncObj = new ArrayList<EmployeeAllowanceDM>();
				empAlwncObj = serviceEmpAllowance.getEmpAlwncList(empId, "BASIC");
				for (EmployeeAllowanceDM obj : empAlwncObj) {
					basictotal = new BigDecimal(obj.getAllowamt().toString());
					getAllowanceAmount();
				}
			}
		});
		btnAdd.setVisible(false);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void getAllowanceAmount() {
		allPercent = (new BigDecimal(tfAlwncPercent.getValue()));
		BigDecimal packingvalue = gerPercentageValue(allPercent, basictotal);
		tfAllownceAmt.setReadOnly(false);
		tfAllownceAmt.setValue("0");
		tfAllBal.setReadOnly(false);
		tfAllBal.setValue("0");
		tfAllownceAmt.setValue(packingvalue.toString());
		tfAllBal.setValue(packingvalue.toString());
		tfAllownceAmt.setReadOnly(true);
		tfAllBal.setReadOnly(true);
	}
	
	private BigDecimal gerPercentageValue(BigDecimal percent, BigDecimal value) {
		return percent.multiply(value).divide(new BigDecimal("100"));
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(cbEmpName);
		flColumn2.addComponent(cbAlwncDesc);
		flColumn3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		Label lbl = new Label();
		flColumn1.addComponent(cbEmpName);
		flColumn1.addComponent(cbAlwncDesc);
		flColumn1.addComponent(cbFlatPercent);
		flColumn2.addComponent(tfAlwncPercent);
		flColumn2.addComponent(tfAllownceAmt);
		flColumn3.addComponent(dtEffectiveDt);
		flColumn3.addComponent(tfAllBal);
		flColumn2.addComponent(chAutoPay);
		flColumn3.addComponent(chArrearFlag);
		flColumn4.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(lbl);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<EmployeeAllowanceDM> listEmpAllowance = new ArrayList<EmployeeAllowanceDM>();
		Long empAlwnId = null;
		if (cbEmpName.getValue() != null) {
			empAlwnId = ((Long.valueOf(cbEmpName.getValue().toString())));
		}
		Long alwncId = null;
		if (cbAlwncDesc.getValue() != null) {
			alwncId = ((Long.valueOf(cbAlwncDesc.getValue().toString())));
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfAllownceAmt.getValue() + ", " + dtEffectiveDt.getValue()
				+ (String) cbStatus.getValue() + ", " + empAlwnId + "," + alwncId);
		listEmpAllowance = serviceEmpAllowance.getempallowanceList(null, empAlwnId, alwncId,
				(String) cbStatus.getValue(), "F");
		recordCnt = listEmpAllowance.size();
		beanEmpAllowanceDM = new BeanItemContainer<EmployeeAllowanceDM>(EmployeeAllowanceDM.class);
		beanEmpAllowanceDM.addAll(listEmpAllowance);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the EmployeeAllowance. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanEmpAllowanceDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "empallwnid", "empName", "allownceDesc", "isflpt",
				"allowpt", "allowamt", "empawstatus", "lastupdt", "lastupby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Allowance Name", "Flat/Percent",
				"Allowance Percentage", "Allowance Amt.", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("empallwnid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupby", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbAlwncDesc.setValue(null);
		cbEmpName.setValue(null);
		cbFlatPercent.setValue(null);
		tfAlwncPercent.setReadOnly(false);
		tfAlwncPercent.setValue("0");
		chArrearFlag.setValue(false);
		tfAllownceAmt.setReadOnly(false);
		tfAllownceAmt.setValue("0");
		chAutoPay.setValue(false);
		tfAllBal.setReadOnly(false);
		tfAllBal.setValue("0");
		dtEffectiveDt.setValue(null);
		cbFlatPercent.setComponentError(null);
		cbEmpName.setComponentError(null);
		cbFlatPercent.setComponentError(null);
		cbAlwncDesc.setComponentError(null);
		dtEffectiveDt.setComponentError(null);
		tfAllBal.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editEmpAlownce() {
		if (tblMstScrSrchRslt.getValue() != null) {
			EmployeeAllowanceDM editEmpAlownce = beanEmpAllowanceDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			pkEmpAlwncId = editEmpAlownce.getEmpallwnid().toString();
			if (editEmpAlownce.getAllowamt() != null) {
				tfAllownceAmt.setValue(editEmpAlownce.getAllowamt().toString());
			}
			dtEffectiveDt.setValue(editEmpAlownce.getEffdt());
			if (editEmpAlownce.getAllowbal() != null) {
				tfAllBal.setValue(editEmpAlownce.getAllowbal().toString());
			}
			if (editEmpAlownce.getAutopay().equals("Y")) {
				chAutoPay.setValue(true);
			} else {
				chAutoPay.setValue(false);
			}
			if (editEmpAlownce.getArrglag() != null && editEmpAlownce.getArrglag().equals("Y")) {
				chArrearFlag.setValue(true);
			} else {
				chArrearFlag.setValue(false);
			}
			cbStatus.setValue(editEmpAlownce.getEmpawstatus());
			cbEmpName.setValue(editEmpAlownce.getEmpid());
			cbAlwncDesc.setValue(editEmpAlownce.getAllowid());
			cbFlatPercent.setValue(editEmpAlownce.getIsflpt());
			if (editEmpAlownce.getAllowpt() != null) {
				tfAlwncPercent.setValue(editEmpAlownce.getAllowpt().toString());
			}
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbAlwncDesc.setValue(null);
		cbEmpName.setValue(null);
		// reset the field valued to default
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		resetFields();
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbAlwncDesc.setRequired(true);
		cbEmpName.setRequired(true);
		cbFlatPercent.setRequired(true);
		tfAlwncPercent.setReadOnly(false);
		cbAlwncDesc.setReadOnly(false);
		tfAllownceAmt.setReadOnly(false);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for EmpAllowance. ID " + pkEmpAlwncId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_EMP_ALLOWANCE);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkEmpAlwncId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbEmpName.setComponentError(null);
		cbAlwncDesc.setComponentError(null);
		cbFlatPercent.setComponentError(null);
		cbAlwncDesc.setRequired(false);
		cbEmpName.setRequired(false);
		cbFlatPercent.setRequired(false);
		cbAlwncDesc.setReadOnly(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		cbAlwncDesc.setRequired(true);
		cbEmpName.setRequired(true);
		cbFlatPercent.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		resetFields();
		editEmpAlownce();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbEmpName.setComponentError(null);
		cbAlwncDesc.setComponentError(null);
		cbFlatPercent.setComponentError(null);
		dtEffectiveDt.setComponentError(null);
		tfAllBal.setComponentError(null);
		errorFlag = false;
		if (cbEmpName.getValue() == null) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbEmpName.getValue());
			errorFlag = true;
		}
		if (cbAlwncDesc.getValue() == null) {
			cbAlwncDesc.setComponentError(new UserError(GERPErrorCodes.NULL_ALLOWANCE_NAME));
			errorFlag = true;
		}
		if (cbFlatPercent.getValue() == null) {
			cbFlatPercent.setComponentError(new UserError(GERPErrorCodes.NULL_FLAT_PERCENT));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		EmployeeAllowanceDM empAllowncObj = new EmployeeAllowanceDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			empAllowncObj = beanEmpAllowanceDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if (tfAllownceAmt.getValue() != null && tfAllownceAmt.getValue().trim().length() > 0) {
			empAllowncObj.setAllowamt(new BigDecimal(tfAllownceAmt.getValue()));
		} else {
			empAllowncObj.setAllowamt(new BigDecimal("0"));
		}
		if (dtEffectiveDt.getValue() != null) {
			empAllowncObj.setEffdt((dtEffectiveDt.getValue()));
		}
		if (chAutoPay.getValue().equals(true)) {
			empAllowncObj.setAutopay("Y");
		} else {
			empAllowncObj.setAutopay("N");
		}
		if (chArrearFlag.getValue().equals(true)) {
			empAllowncObj.setArrglag("Y");
		} else {
			empAllowncObj.setArrglag("N");
		}
		if (tfAllBal.getValue() != null && tfAllBal.getValue().trim().length() > 0) {
			empAllowncObj.setAllowbal(new BigDecimal(tfAllBal.getValue()));
		} else {
			empAllowncObj.setAllowbal(new BigDecimal("0"));
		}
		if (cbStatus.getValue() != null) {
			empAllowncObj.setEmpawstatus((String) cbStatus.getValue());
		}
		if (cbFlatPercent.getValue() != null) {
			empAllowncObj.setIsflpt((String) cbFlatPercent.getValue());
		}
		if (tfAlwncPercent.getValue() != null) {
			empAllowncObj.setAllowpt(new BigDecimal(tfAlwncPercent.getValue()));
		}
		if (cbEmpName.getValue() != null) {
			empAllowncObj.setEmpid((Long.valueOf(cbEmpName.getValue().toString())));
		}
		if (cbAlwncDesc.getValue() != null) {
			empAllowncObj.setAllowid((Long.valueOf(cbAlwncDesc.getValue().toString())));
		}
		empAllowncObj.setLastupdt(DateUtils.getcurrentdate());
		empAllowncObj.setLastupby(username);
		serviceEmpAllowance.saveAndUpdate(empAllowncObj);
		resetFields();
		loadSrchRslt();
	}
	
	private void loadEmpList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading employee Search...");
			BeanContainer<Long, EmployeeDM> beanEmpDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmpDM.setBeanIdProperty("employeeid");
			beanEmpDM.addAll(serviceEmployee.getEmployeeList(null, null, null, null, companyid, null, null, null, null,
					"P"));
			cbEmpName.setContainerDataSource(beanEmpDM);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadAllowanceList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading allowance Search...");
		BeanContainer<Long, AllowanceDM> beanAlwncDM = new BeanContainer<Long, AllowanceDM>(AllowanceDM.class);
		beanAlwncDM.setBeanIdProperty("alowncId");
		beanAlwncDM.addAll(serviceAllowance.getalowanceList(null, null, companyid, (String) cbAlwncDesc.getValue(),
				"Active", "P"));
		cbAlwncDesc.setContainerDataSource(beanAlwncDM);
	}
}
