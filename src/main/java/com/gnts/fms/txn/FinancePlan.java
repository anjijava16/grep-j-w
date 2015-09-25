/**
 * File Name 		: FinancePlan.java 
 * Description 		: this class is used for add/edit finance plan details. 
 * Author 			: SOUNDAR C 
 * Date 			: Mar 17, 2014
 * Modification 	:
 * Modified By 		: SOUNDAR C 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 17 2014        SOUNDAR C		          Intial Version
 * 0.2			 26-Jul-2014		Abdullah.H				  Code Optimization
 */
package com.gnts.fms.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.CurrencyDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.CurrencyService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.constants.SessionForModule;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.fms.domain.mst.TransactionTypeDM;
import com.gnts.fms.domain.txn.FinancePlanDM;
import com.gnts.fms.service.mst.TransactionTypeService;
import com.gnts.fms.service.txn.FinancePlanService;
import com.gnts.pms.domain.mst.ProjectDM;
import com.gnts.pms.service.mst.ProjectService;
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

public class FinancePlan extends BaseUI {
	private static final long serialVersionUID = 1L;
	/*
	 * Service Declaration
	 */
	private FinancePlanService serviceFinancePlan = (FinancePlanService) SpringContextHelper.getBean("finance");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private ProjectService serviceProjects = (ProjectService) SpringContextHelper.getBean("MProjects");
	private CurrencyService serviceCurrency = (CurrencyService) SpringContextHelper.getBean("currency");
	private BranchService serviceBankBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private DepartmentService serviceDepartment = (DepartmentService) SpringContextHelper.getBean("department");
	private TransactionTypeService serviceTransType = (TransactionTypeService) SpringContextHelper.getBean("transtype");
	private TextField tfFpDescription = new GERPTextField("Fin. Plan Description");
	private ComboBox cbBranchName = new GERPComboBox("Branch");
	private ComboBox cbFpCategory = new GERPComboBox("FP Category");
	private TextField tfFpAmount = new GERPTextField("Open Balance");
	private ComboBox cbCurrency = new GERPComboBox("Currency");
	private PopupDateField dfLastExpenseDate = new GERPPopupDateField("Last Expense Date");
	private TextField tfRegularDuration = new GERPTextField("Regular Duration");
	private PopupDateField dfNextExpenseDate = new GERPPopupDateField("Next Expense Date");
	private ComboBox cbDepartmentName = new GERPComboBox("Department Name");
	private ComboBox cbOwnerName = new GERPComboBox("Owner Name");
	private ComboBox cbTransactionType = new GERPComboBox("Transaction Type");
	private ComboBox cbProjectName = new GERPComboBox("Project Name");
	private TextArea tfRemarks = new GERPTextArea("Remarks");
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.T_FMS_FINANCE_PLAN, BASEConstants.FMS_STATUS);
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Form layout for input controls
	private FormLayout flFormLayout1, flFormLayout2, flFormLayout3, flFormLayout4;
	private String loginUserName;
	private Long companyId;
	private int recordCnt;
	private String primaryid;
	private Long empId;
	private BeanItemContainer<FinancePlanDM> beansFinancePlanDM = null;
	private Logger logger = Logger.getLogger(FinancePlan.class);
	
	// Constructor
	public FinancePlan() {
		// Get the logged in user name and company id and country id from the session
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Inside FinancePlan() constructor");
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		// Loading the UI
		empId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		buildView();
	}
	
	/*
	 * buildMainview()-->for screen UI design
	 * @param clArgumentLayout hlHeaderLayout
	 */
	private void buildView() {
		cbBranchName.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbFpCategory.setItemCaptionPropertyId("lookupname");
		loadFbCategoryList();
		cbCurrency.setItemCaptionPropertyId("ccyname");
		loadCurrencyList();
		cbDepartmentName.setItemCaptionPropertyId("deptname");
		loadDepartmentList();
		cbOwnerName.setItemCaptionPropertyId("fullname");
		loadOwnerList();
		cbTransactionType.setItemCaptionPropertyId("transtypename");
		loadMTranstypeList();
		cbProjectName.setItemCaptionPropertyId("projectName");
		loadProjectList();
		tfFpDescription.setRequired(true);
		tfFpDescription.setMaxLength(30);
		cbStatus.setWidth("120");
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
		tfFpDescription.setRequired(false);
		flFormLayout1 = new FormLayout();
		flFormLayout2 = new FormLayout();
		flFormLayout3 = new FormLayout();
		flFormLayout1.addComponent(tfFpDescription);
		tfFpDescription.setRequired(false);
		tfFpDescription.setComponentError(null);
		flFormLayout2.addComponent(cbDepartmentName);
		cbDepartmentName.setRequired(false);
		cbDepartmentName.setComponentError(null);
		flFormLayout3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flFormLayout1);
		hlSearchLayout.addComponent(flFormLayout2);
		hlSearchLayout.addComponent(flFormLayout3);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling User Input layout");
		hlUserInputLayout.removeAllComponents();
		// Remove all components in Search Layout
		flFormLayout1 = new FormLayout();
		flFormLayout2 = new FormLayout();
		flFormLayout3 = new FormLayout();
		flFormLayout4 = new FormLayout();
		flFormLayout1.setSpacing(true);
		flFormLayout1.addComponent(tfFpDescription);
		flFormLayout1.addComponent(cbBranchName);
		flFormLayout1.addComponent(cbFpCategory);
		flFormLayout1.addComponent(tfFpAmount);
		flFormLayout2 = new FormLayout();
		flFormLayout2.setSpacing(true);
		flFormLayout2.addComponent(cbCurrency);
		flFormLayout2.addComponent(dfLastExpenseDate);
		flFormLayout2.addComponent(tfRegularDuration);
		flFormLayout2.addComponent(dfNextExpenseDate);
		flFormLayout3 = new FormLayout();
		flFormLayout3.setSpacing(true);
		flFormLayout3.addComponent(cbDepartmentName);
		flFormLayout3.addComponent(cbOwnerName);
		flFormLayout3.addComponent(cbTransactionType);
		flFormLayout3.addComponent(cbProjectName);
		flFormLayout4 = new FormLayout();
		flFormLayout4.setSpacing(true);
		flFormLayout4.addComponent(tfRemarks);
		tfRemarks.setWidth("160");
		flFormLayout4.addComponent(cbStatus);
		cbStatus.setWidth("160");
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flFormLayout1);
		hlUserInputLayout.addComponent(flFormLayout2);
		hlUserInputLayout.addComponent(flFormLayout3);
		hlUserInputLayout.addComponent(flFormLayout4);
		hlSearchLayout.removeAllComponents();
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<FinancePlanDM> listFinPlan = new ArrayList<FinancePlanDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Search Parameters are " + companyId + ", " + tfFpDescription.getValue() + ", "
					+ (String) cbStatus.getValue());
			listFinPlan = serviceFinancePlan.getFinancePlanList(null, companyId, tfFpDescription.getValue(), null,
					(Long) cbDepartmentName.getValue(), null, (String) cbStatus.getValue());
			recordCnt = listFinPlan.size();
			beansFinancePlanDM = new BeanItemContainer<FinancePlanDM>(FinancePlanDM.class);
			beansFinancePlanDM.addAll(listFinPlan);
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Got the Account List result set");
			tblMstScrSrchRslt.setContainerDataSource(beansFinancePlanDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "fpid", "fpdesc", "deptname", "fpstatus",
					"lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Fin.Plan Description", "Department Name",
					"Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("fpid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records:" + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// For Load Active Payment mode Details based on Company
	private void loadFbCategoryList() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyId,
					SessionForModule.getModuleId("FMS"), "Active", "FM_FPCTGRY"));
			cbFpCategory.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// For Load Active Account Type Details based on Company
	private void loadDepartmentList() {
		try {
			BeanContainer<Long, DepartmentDM> bean = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
			bean.setBeanIdProperty("deptid");
			bean.addAll(serviceDepartment.getDepartmentList(companyId, null, (String) cbStatus.getValue(), "P"));
			cbDepartmentName.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// For Load Active Employee Details based on Company
	private void loadOwnerList() {
		try {
			BeanContainer<Long, EmployeeDM> employeebeans = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			employeebeans.setBeanIdProperty("employeeid");
			employeebeans.addAll(serviceEmployee.getEmployeeList(null, null, null, (String) cbStatus.getValue(),
					companyId, null, null, null, null, "P"));
			cbOwnerName.setContainerDataSource(employeebeans);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// For Load Active Transaction Type Details based on Company
	private void loadMTranstypeList() {
		try {
			BeanContainer<Long, TransactionTypeDM> bean = new BeanContainer<Long, TransactionTypeDM>(
					TransactionTypeDM.class);
			bean.setBeanIdProperty("transtypeid");
			bean.addAll(serviceTransType.getTransactionTypeList(companyId, null, (String) cbStatus.getValue(), null,
					null));
			cbTransactionType.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// For Load Active Project Details based on Company
	private void loadProjectList() {
		try {
			BeanContainer<Long, ProjectDM> bean = new BeanContainer<Long, ProjectDM>(ProjectDM.class);
			bean.setBeanIdProperty("projectId");
			bean.addAll(serviceProjects.getProjectList(null, null, companyId, null, (String) cbStatus.getValue()));
			cbProjectName.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// For Load Active Account Type Details based on Company
	private void loadCurrencyList() {
		try {
			BeanContainer<Long, CurrencyDM> bean = new BeanContainer<Long, CurrencyDM>(CurrencyDM.class);
			bean.setBeanIdProperty("ccyid");
			bean.addAll(serviceCurrency.getCurrencyList(null, null, null, (String) cbStatus.getValue(), "T"));
			cbCurrency.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// For Load Active Branch Details based on Company
	private void loadBranchList() {
		try {
			BeanContainer<Long, BranchDM> bean = new BeanContainer<Long, BranchDM>(BranchDM.class);
			bean.setBeanIdProperty("branchId");
			bean.addAll(serviceBankBranch.getBranchList(null, null, null, (String) cbStatus.getValue(), companyId, "P"));
			cbBranchName.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editFinancePlan() {
		try {
			if (tblMstScrSrchRslt.getValue() != null) {
				FinancePlanDM financePlanDM = beansFinancePlanDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				primaryid = financePlanDM.getFpid().toString();
				if (financePlanDM.getFpdesc() != null) {
					tfFpDescription.setValue(financePlanDM.getFpdesc());
				}
				if (financePlanDM.getBranchid() != null) {
					cbBranchName.setValue(financePlanDM.getBranchid());
				}
				if (financePlanDM.getFpcategory() != null) {
					cbFpCategory.setValue(financePlanDM.getFpcategory());
				}
				if (financePlanDM.getFpamount() != null) {
					tfFpAmount.setValue(financePlanDM.getFpamount().toString());
				}
				if (financePlanDM.getCcyid() != null) {
					cbCurrency.setValue(financePlanDM.getCcyid());
				}
				if (financePlanDM.getLastexpensedt() != null) {
					dfLastExpenseDate.setValue(financePlanDM.getLastexpensedt());
				}
				if (financePlanDM.getRecurduration() != null) {
					tfRegularDuration.setValue(financePlanDM.getRecurduration());
				}
				if (financePlanDM.getNextexpensedt() != null) {
					dfNextExpenseDate.setValue(financePlanDM.getNextexpensedt());
				}
				if (financePlanDM.getDeptid() != null) {
					cbDepartmentName.setValue(financePlanDM.getDeptid());
				}
				if (financePlanDM.getOwnerid() != null) {
					cbOwnerName.setValue(financePlanDM.getOwnerid());
				}
				if (financePlanDM.getTranstypeid() != null) {
					cbTransactionType.setValue(financePlanDM.getTranstypeid());
				}
				if (financePlanDM.getProjid() != null) {
					cbProjectName.setValue(financePlanDM.getProjid());
				}
				if (financePlanDM.getRemarks() != null) {
					tfRemarks.setValue(financePlanDM.getRemarks());
				}
				financePlanDM.setFpstatus(((String) cbStatus.getValue()));
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbStatus.setValue(null);
		tfFpDescription.setValue("");
		cbDepartmentName.setValue(null);
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		tfFpDescription.setRequired(true);
		cbBranchName.setRequired(true);
		cbFpCategory.setRequired(true);
		cbDepartmentName.setRequired(true);
		cbOwnerName.setRequired(true);
		cbCurrency.setRequired(true);
		cbTransactionType.setRequired(true);
		cbProjectName.setRequired(true);
		tfFpDescription.setComponentError(null);
		cbBranchName.setComponentError(null);
		cbFpCategory.setComponentError(null);
		cbDepartmentName.setComponentError(null);
		cbOwnerName.setComponentError(null);
		cbProjectName.setComponentError(null);
		cbCurrency.setComponentError(null);
		cbTransactionType.setComponentError(null);
		// remove the components in the search layout and input controls in the same container
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		tfFpDescription.setRequired(true);
		cbBranchName.setRequired(true);
		cbFpCategory.setRequired(true);
		cbDepartmentName.setRequired(true);
		cbOwnerName.setRequired(true);
		cbCurrency.setRequired(true);
		cbTransactionType.setRequired(true);
		cbProjectName.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editFinancePlan();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		tfFpDescription.setComponentError(null);
		cbProjectName.setComponentError(null);
		cbCurrency.setComponentError(null);
		cbBranchName.setComponentError(null);
		cbFpCategory.setComponentError(null);
		cbDepartmentName.setComponentError(null);
		cbOwnerName.setComponentError(null);
		cbTransactionType.setComponentError(null);
		Boolean errorFlag = false;
		if ((tfFpDescription.getValue() == null) || tfFpDescription.getValue().trim().length() == 0) {
			tfFpDescription.setComponentError(new UserError(GERPErrorCodes.NULL_FPDESCRIPTION));
			errorFlag = true;
		}
		if ((cbCurrency.getValue() == null)) {
			cbCurrency.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_CURRENCY));
			errorFlag = true;
		}
		if ((cbProjectName.getValue() == null)) {
			cbProjectName.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_PROJECTNAME));
			errorFlag = true;
		}
		if ((cbBranchName.getValue() == null)) {
			cbBranchName.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			errorFlag = true;
		}
		if ((cbFpCategory.getValue() == null)) {
			cbFpCategory.setComponentError(new UserError(GERPErrorCodes.NULL_FPCATEGORY));
			errorFlag = true;
		}
		if ((cbOwnerName.getValue() == null)) {
			cbOwnerName.setComponentError(new UserError(GERPErrorCodes.NULL_OWNERNAME));
			errorFlag = true;
		}
		if ((cbDepartmentName.getValue() == null)) {
			cbDepartmentName.setComponentError(new UserError(GERPErrorCodes.NULL_DEPT_NAME));
			errorFlag = true;
		}
		if ((cbTransactionType.getValue() == null)) {
			cbTransactionType.setComponentError(new UserError(GERPErrorCodes.NULL_DEPT_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Saving Data... ");
		try {
			FinancePlanDM finobj = new FinancePlanDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				finobj = beansFinancePlanDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			finobj.setCompanyid(companyId);
			finobj.setFpdesc(tfFpDescription.getValue());
			finobj.setBranchid((Long) cbBranchName.getValue());
			finobj.setFpcategory((String) cbFpCategory.getValue());
			if (tfFpAmount.getValue() != null && tfFpAmount.getValue().trim().length() > 0) {
				finobj.setFpamount(new BigDecimal(tfFpAmount.getValue()));
			} else {
				finobj.setFpamount(new BigDecimal("0"));
			}
			finobj.setCcyid((Long.valueOf(cbCurrency.getValue().toString())));
			finobj.setLastexpensedt(dfLastExpenseDate.getValue());
			finobj.setNextexpensedt(dfNextExpenseDate.getValue());
			finobj.setRecurduration(tfRegularDuration.getValue());
			finobj.setDeptid((Long) cbDepartmentName.getValue());
			finobj.setOwnerid((Long) cbOwnerName.getValue());
			if (cbTransactionType.getValue() != null) {
				finobj.setTranstypeid((Long) cbTransactionType.getValue());
			}
			finobj.setProjid((Long) cbProjectName.getValue());
			if (cbStatus.getValue() != null) {
				finobj.setFpstatus((String) cbStatus.getValue());
			}
			finobj.setActionedby(empId);
			finobj.setRemarks(tfRemarks.getValue());
			finobj.setLastupdatedby(loginUserName);
			finobj.setLastupdateddt(DateUtils.getcurrentdate());
			serviceFinancePlan.saveDetails(finobj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Getting audit record for PNC Dept. ID " + primaryid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_FMS_FINANCE_PLAN);
		UI.getCurrent().getSession().setAttribute("audittablepk", primaryid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Canceling action ");
		assembleSearchLayout();
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tfFpDescription.setValue("");
		tfRegularDuration.setValue("");
		dfLastExpenseDate.setValue(null);
		dfNextExpenseDate.setValue(null);
		cbProjectName.setValue(null);
		cbFpCategory.setValue(null);
		cbBranchName.setValue(null);
		cbCurrency.setValue(null);
		tfFpAmount.setValue("0");
		tfRemarks.setValue("");
		cbDepartmentName.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbStatus.setValue(null);
		tfFpDescription.setComponentError(null);
		cbOwnerName.setValue(null);
		cbTransactionType.setValue(null);
	}
}