/**
 * File Name 		: Accounts.java 
 * Description 		: this class is used for add/edit Account details. 
 * Author 			: SOUNDAR C 
 * Date 			: Mar 05, 2014
 * Modification 	:
 * Modified By 		: SOUNDAR C 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 07 2014        SOUNDAR C		          Intial Version
 * 
 */
package com.gnts.fms.txn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CurrencyDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.service.mst.CurrencyService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.ParameterService;
import com.gnts.base.service.mst.VendorService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
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
import com.gnts.fms.domain.mst.AccountTypeDM;
import com.gnts.fms.domain.mst.BankBranchDM;
import com.gnts.fms.domain.mst.BankDM;
import com.gnts.fms.domain.txn.AccountOwnersDM;
import com.gnts.fms.domain.txn.AccountsDM;
import com.gnts.fms.service.mst.AccountTypeService;
import com.gnts.fms.service.mst.BankBranchService;
import com.gnts.fms.service.mst.BankService;
import com.gnts.fms.service.txn.AccountOwnersService;
import com.gnts.fms.service.txn.AccountsService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Accounts extends BaseUI {
	private static final long serialVersionUID = 1L;
	// Bean creation
	private AccountsService serviceAccounts = (AccountsService) SpringContextHelper.getBean("accounts");
	private BankService serviceBank = (BankService) SpringContextHelper.getBean("fmsbank");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private AccountTypeService serviceAccounttype = (AccountTypeService) SpringContextHelper.getBean("accounttype");
	private CurrencyService serviceCurrency = (CurrencyService) SpringContextHelper.getBean("currency");
	private BankBranchService serviceBankBranch = (BankBranchService) SpringContextHelper.getBean("bankbranch");
	private ClientService serviceClient = (ClientService) SpringContextHelper.getBean("clients");
	private ParameterService serviceParameter = (ParameterService) SpringContextHelper.getBean("parameter");
	private AccountOwnersService serviceAccountOwner = (AccountOwnersService) SpringContextHelper
			.getBean("accountowner");
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Form layout for input controls
	private FormLayout flFormLayout1, flFormLayout2, flFormLayout3, flFormLayout4;
	// Add User Input Controls
	private ComboBox cbAccountGrp = new GERPComboBox("Account Group", BASEConstants.T_FMS_ACCOUNTS,
			BASEConstants.T_FMS_ACCOUNT_GROUP);
	private TextField tfAccountNumber = new GERPTextField("Account Number");
	private TextField tfAccountName = new GERPTextField("Account Name");
	private PopupDateField dfAccountDate = new GERPPopupDateField("Account Date");
	private TextField tffinanceYr = new GERPTextField("Finance Year");
	private CheckBox ckGenerateVoucheryn = new CheckBox("Generate Voucher");
	private ComboBox cbOwnerName = new GERPComboBox("Account owner");
	private ComboBox cbBankName = new GERPComboBox("Bank Name");
	private ComboBox cbBankBranch = new GERPComboBox("Bank Branch");
	private ComboBox cbVendorName = new GERPComboBox("Vendor Name");
	private ComboBox cbClientName = new GERPComboBox("Client Name");
	private ComboBox cbAccountType = new GERPComboBox("Account Type");
	private ComboBox cbCurrency = new GERPComboBox("Currency");
	private TextField tfOpenBalance = new GERPTextField("Open Balance");
	private TextField tfParkedAmt = new GERPTextField("Parked Amount");
	private TextField tfCurrentBalance = new GERPTextField("Current Balance");
	private ComboBox cbApprovelAuth = new GERPComboBox("Approvel Auth.", BASEConstants.T_FMS_ACCOUNTS,
			BASEConstants.T_FMS_APPROVEAUTH_RM_AM);
	private ComboBox cbApproveManager = new GERPComboBox("Approvel Manager");
	private ComboBox cbParentAccId = new GERPComboBox("Parent Account ID");
	private CheckBox ckSelfApprovel = new CheckBox("Self Approvel Account?");
	private TextArea tfRemarks = new GERPTextArea("Remarks");
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private ListSelect lsAccountOwners = new ListSelect("Account Owners");
	public Long accId;
	// BeanItemContainer
	private BeanItemContainer<AccountsDM> beanAccountsDM = null;
	// private BeanItemContainer<AccountOwnersDM> beanAccountOwnersDM = null;
	// Local variables declaration
	private Long companyId;
	private int recordCnt = 0;
	private String username, accountId;
	// Initialize logger
	private Logger logger = Logger.getLogger(Accounts.class);
	
	// Constructor
	public Accounts() {
		// Get the logged in user name and company id and country id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		cbAccountGrp.setRequired(true);
		cbAccountGrp.setWidth("150");
		cbAccountGrp.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadParentAccountList();
				}
			}
		});
		cbStatus.setWidth("150");
		cbApprovelAuth.setWidth("150");
		tffinanceYr.setValue(serviceParameter.getParameterValue("FM_FINYEAR", companyId, null));
		tffinanceYr.setReadOnly(true);
		tfAccountNumber.setMaxLength(30);
		tfAccountName.setRequired(true);
		loadEmployeeList();
		cbOwnerName.setItemCaptionPropertyId("fullname");
		cbApproveManager.setItemCaptionPropertyId("fullname");
		loadAccountTypeList();
		cbAccountType.setItemCaptionPropertyId("accttype");
		cbCurrency.setItemCaptionPropertyId("ccyname");
		loadCurrencyList();
		cbBankName.setItemCaptionPropertyId("bankname");
		loadBankList();
		cbVendorName.setItemCaptionPropertyId("vendorName");
		loadVendorList();
		cbBankBranch.setItemCaptionPropertyId("ifsccode");
		loadBankBranchList();
		cbClientName.setItemCaptionPropertyId("clientName");
		loadClientList();
		lsAccountOwners.setItemCaptionPropertyId("fullname");
		lsAccountOwners.setWidth("200");
		lsAccountOwners.setHeight("140");
		lsAccountOwners.setMultiSelect(true);
		cbParentAccId.setItemCaptionPropertyId("accountname");
		flFormLayout1 = new FormLayout();
		flFormLayout2 = new FormLayout();
		flFormLayout3 = new FormLayout();
		flFormLayout4 = new FormLayout();
		tfAccountNumber.setReadOnly(false);
		tfAccountNumber.setValue("");
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		tfAccountName.setRequired(false);
		tfAccountNumber.setRequired(false);
		flFormLayout1 = new FormLayout();
		flFormLayout2 = new FormLayout();
		flFormLayout3 = new FormLayout();
		flFormLayout1.addComponent(tfAccountNumber);
		// tfAccountNumber.setReadOnly(false);
		// tfAccountNumber.setValue("");
		flFormLayout2.addComponent(tfAccountName);
		flFormLayout3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flFormLayout1);
		hlSearchLayout.addComponent(flFormLayout2);
		hlSearchLayout.addComponent(flFormLayout3);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		tfAccountName.setRequired(true);
		tfAccountNumber.setRequired(true);
		tfCurrentBalance.setReadOnly(true);
		tfOpenBalance.setReadOnly(true);
		tfParkedAmt.setReadOnly(true);
		hlSearchLayout.removeAllComponents();
		flFormLayout1 = new FormLayout();
		flFormLayout2 = new FormLayout();
		flFormLayout3 = new FormLayout();
		flFormLayout4 = new FormLayout();
		flFormLayout1.setSpacing(true);
		flFormLayout1.addComponent(cbAccountGrp);
		flFormLayout1.addComponent(tfAccountNumber);
		flFormLayout1.addComponent(tfAccountName);
		flFormLayout1.addComponent(dfAccountDate);
		flFormLayout1.addComponent(tffinanceYr);
		flFormLayout1.addComponent(ckGenerateVoucheryn);
		flFormLayout1.addComponent(cbOwnerName);
		flFormLayout1.addComponent(cbBankName);
		flFormLayout1.setSpacing(true);
		flFormLayout2.addComponent(cbBankBranch);
		flFormLayout2.addComponent(cbVendorName);
		flFormLayout2.addComponent(cbClientName);
		flFormLayout2.addComponent(cbAccountType);
		flFormLayout2.addComponent(cbCurrency);
		flFormLayout2.addComponent(tfOpenBalance);
		flFormLayout2.addComponent(tfParkedAmt);
		flFormLayout2.addComponent(tfCurrentBalance);
		flFormLayout2.setSpacing(true);
		flFormLayout3.addComponent(cbApprovelAuth);
		flFormLayout3.addComponent(cbApproveManager);
		flFormLayout3.addComponent(cbParentAccId);
		// flFormLayout3.addComponent(ckSelfApprovel);
		flFormLayout3.addComponent(tfRemarks);
		flFormLayout3.addComponent(cbStatus);
		flFormLayout3.setSpacing(true);
		flFormLayout4.setSpacing(true);
		flFormLayout4.addComponent(lsAccountOwners);
		flFormLayout4.setSpacing(true);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flFormLayout1);
		hlUserInputLayout.addComponent(flFormLayout2);
		hlUserInputLayout.addComponent(flFormLayout3);
		hlUserInputLayout.addComponent(flFormLayout4);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	/*
	 * For Load Active Employee Details based on Company
	 */
	private void loadEmployeeList() {
		List<EmployeeDM> list = servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyId, null, null,
				null, null, "T");
		BeanContainer<Long, EmployeeDM> employeebeans = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		employeebeans.setBeanIdProperty("employeeid");
		employeebeans.addAll(list);
		cbOwnerName.setContainerDataSource(employeebeans);
		BeanContainer<Long, EmployeeDM> employeebeans1 = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		employeebeans1.setBeanIdProperty("employeeid");
		employeebeans1.addAll(list);
		cbApproveManager.setContainerDataSource(employeebeans1);
		BeanContainer<Long, EmployeeDM> employeebeans2 = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		employeebeans2.setBeanIdProperty("employeeid");
		employeebeans2.addAll(list);
		lsAccountOwners.setContainerDataSource(employeebeans2);
	}
	
	/*
	 * For Load Active Account Type Details based on Company
	 */
	private void loadAccountTypeList() {
		List<AccountTypeDM> list = serviceAccounttype.getAccountTypeList(companyId, null, "Active");
		BeanContainer<Long, AccountTypeDM> bean = new BeanContainer<Long, AccountTypeDM>(AccountTypeDM.class);
		bean.setBeanIdProperty("accttypeid");
		bean.addAll(list);
		cbAccountType.setContainerDataSource(bean);
	}
	
	/*
	 * For Load Active Account Type Details based on Company
	 */
	private void loadCurrencyList() {
		List<CurrencyDM> list = serviceCurrency.getCurrencyList(null, null, null, "Active", "T");
		BeanContainer<Long, CurrencyDM> bean = new BeanContainer<Long, CurrencyDM>(CurrencyDM.class);
		bean.setBeanIdProperty("ccyid");
		bean.addAll(list);
		cbCurrency.setContainerDataSource(bean);
	}
	
	/*
	 * For Load Active Bank Branch Details based on Company
	 */
	private void loadBankBranchList() {
		List<BankBranchDM> list = serviceBankBranch.getBankBranchlist(null, null, null, companyId, "Active", "T");
		BeanContainer<Long, BankBranchDM> bean = new BeanContainer<Long, BankBranchDM>(BankBranchDM.class);
		bean.setBeanIdProperty("bankbrnchid");
		bean.addAll(list);
		cbBankBranch.setContainerDataSource(bean);
	}
	
	/*
	 * For Load Active Bank Details based on Company
	 */
	private void loadBankList() {
		List<BankDM> list = serviceBank.getBanklist(null, null, companyId, "Active", "T");
		BeanContainer<Long, BankDM> beanBank = new BeanContainer<Long, BankDM>(BankDM.class);
		beanBank.setBeanIdProperty("bankid");
		beanBank.addAll(list);
		cbBankName.setContainerDataSource(beanBank);
	}
	
	/*
	 * For Load Active Vendor Details based on Company
	 */
	private void loadVendorList() {
		List<VendorDM> list = serviceVendor.getVendorList(null, null, companyId, null, null, null, null, null,
				"Active", null, "P");
		BeanContainer<Long, VendorDM> beanBank = new BeanContainer<Long, VendorDM>(VendorDM.class);
		beanBank.setBeanIdProperty("vendorId");
		beanBank.addAll(list);
		cbVendorName.setContainerDataSource(beanBank);
	}
	
	/*
	 * For Load Active Client Details based on Company
	 */
	private void loadClientList() {
		List<ClientDM> list = serviceClient.getClientDetails(companyId, null, null, null, null, null, null, null,
				"Active", "T");
		// getClientDetails(companyId, null, null, null, null, null, null, "Active",
		// "T");
		BeanContainer<Long, ClientDM> bean = new BeanContainer<Long, ClientDM>(ClientDM.class);
		bean.setBeanIdProperty("clientId");
		bean.addAll(list);
		cbClientName.setContainerDataSource(bean);
	}
	
	/*
	 * For Load Active Parent Account Details based on Company
	 */
	private void loadParentAccountList() {
		List<AccountsDM> list = serviceAccounts.getAccountsList(companyId, accId, null, "Active", null, null,
				cbAccountGrp.getValue().toString());
		BeanContainer<Long, AccountsDM> bean = new BeanContainer<Long, AccountsDM>(AccountsDM.class);
		bean.setBeanIdProperty("accountId");
		bean.addAll(list);
		cbParentAccId.setContainerDataSource(bean);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<AccountsDM> accountList = new ArrayList<AccountsDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyId + ", " + tfAccountNumber.getValue() + ", " + "Active");
		accountList = serviceAccounts.getAccountsList(companyId, null, tfAccountNumber.getValue(),
				(String) cbStatus.getValue(), tfAccountName.getValue(), null, null);
		recordCnt = accountList.size();
		beanAccountsDM = new BeanItemContainer<AccountsDM>(AccountsDM.class);
		beanAccountsDM.addAll(accountList);
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > "
				+ "Got the Account List result set");
		tblMstScrSrchRslt.setContainerDataSource(beanAccountsDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "accountId", "accountno", "accountname", "currentBalance",
				"acctstatus", "lastupdateddt", "lastupdatedby", });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Account Number", "Account Name",
				"Account Balance", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("currentBalance", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records:" + recordCnt);
	}
	
	private void editAccounts() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			AccountsDM accountsList = beanAccountsDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			accId = ((Long) itselect.getItemProperty("accountId").getValue());
			if (accountsList.getAccGroup() != null) {
				cbAccountGrp.setValue(accountsList.getAccGroup());
			}
			if (accountsList.getAccountname() != null) {
				tfAccountName.setValue(accountsList.getAccountname());
			}
			if (accountsList.getAccountdt() != null) {
				dfAccountDate.setValue(accountsList.getAccountdt());
			}
			if (accountsList.getFinanceyear() != null) {
				tffinanceYr.setValue(accountsList.getFinanceyear());
			}
			if (accountsList.getGenerateVoucherYN().equals("Y")) {
				ckGenerateVoucheryn.setValue(true);
			} else {
				ckGenerateVoucheryn.setValue(false);
			}
			if (accountsList.getEmployeeId() != null) {
				cbOwnerName.setValue(accountsList.getEmployeeId());
			}
			if (accountsList.getBankId() != null) {
				cbBankName.setValue(accountsList.getBankId());
			}
			if (accountsList.getBankbranchId() != null) {
				cbBankBranch.setValue(accountsList.getBankbranchId());
			}
			if (accountsList.getVendorId() != null) {
				cbVendorName.setValue(accountsList.getVendorId());
			}
			if (accountsList.getClientId() != null) {
				cbClientName.setValue(accountsList.getClientId());
			}
			if (accountsList.getAccttypeid() != null) {
				cbAccountType.setValue(accountsList.getAccttypeid().toString());
			}
			if (accountsList.getCcyid() != null) {
				cbCurrency.setValue(accountsList.getCcyid());
			}
			if (accountsList.getOpenbalance() != null) {
				tfOpenBalance.setReadOnly(false);
				tfOpenBalance.setValue(accountsList.getOpenbalance().toString());
				tfOpenBalance.setReadOnly(true);
			}
			if (accountsList.getParkedAmt() != null) {
				tfParkedAmt.setReadOnly(false);
				tfParkedAmt.setValue(accountsList.getParkedAmt().toString());
				tfParkedAmt.setReadOnly(true);
			}
			if (accountsList.getCurrentBalance() != null) {
				tfCurrentBalance.setReadOnly(false);
				tfCurrentBalance.setValue(accountsList.getCurrentBalance().toString());
				tfCurrentBalance.setReadOnly(true);
			}
			String amrm = itselect.getItemProperty("approveauthRMAM").getValue().toString();
			if (amrm.equals("Account Mgr.")) {
				amrm = "AM";
			} else {
				amrm = "RM";
			}
			cbApprovelAuth.setValue(amrm);
			if (accountsList.getSelfapproveyn().equals("Y")) {
				ckSelfApprovel.setValue(true);
			} else {
				ckSelfApprovel.setValue(false);
			}
			if (accountsList.getApprovemanager() != null) {
				cbApproveManager.setValue(accountsList.getApprovemanager());
			}
			if (accountsList.getRemarks() != null) {
				tfRemarks.setValue(accountsList.getRemarks());
			}
			if (accountsList.getParentAccId() != null) {
				cbParentAccId.setValue(accountsList.getParentAccId());
			}
			if (accountsList.getSelfapproveyn().equals("Y")) {
				ckSelfApprovel.setValue(true);
			} else {
				ckSelfApprovel.setValue(false);
			}
			tfAccountNumber.setReadOnly(false);
			tfAccountNumber.setValue(accountsList.getAccountno());
			tfAccountNumber.setReadOnly(true);
			String stCode = itselect.getItemProperty("acctstatus").getValue().toString();
			cbStatus.setValue(stCode);
			lsAccountOwners.setValue(null);
			List<AccountOwnersDM> listAccOwner = serviceAccountOwner.getAccountOwnerList(null, accId, null, "Active");
			for (AccountOwnersDM accOwner : listAccOwner) {
				lsAccountOwners.select(accOwner.getEmpid());
			}
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + username + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tfAccountNumber.setReadOnly(false);
		tfAccountNumber.setValue("");
		tfAccountName.setValue("");
		dfAccountDate.setValue(null);
		cbAccountType.setReadOnly(false);
		cbAccountType.setValue(null);
		// cbAccountType.setReadOnly(true);
		cbOwnerName.setValue(null);
		cbBankBranch.setValue(null);
		cbCurrency.setReadOnly(false);
		cbCurrency.setValue(null);
		// cbCurrency.setReadOnly(true);
		tfOpenBalance.setReadOnly(false);
		tfOpenBalance.setValue("0");
		tfOpenBalance.setReadOnly(true);
		tffinanceYr.setReadOnly(false);
		tffinanceYr.setValue(serviceParameter.getParameterValue("FM_FINYEAR", companyId, null));
		tffinanceYr.setReadOnly(true);
		ckGenerateVoucheryn.setValue(false);
		cbBankName.setValue(null);
		cbVendorName.setValue(null);
		cbClientName.setValue(null);
		tfRemarks.setValue("");
		tfCurrentBalance.setReadOnly(false);
		tfCurrentBalance.setValue("0");
		tfCurrentBalance.setReadOnly(true);
		cbApprovelAuth.setValue(null);
		ckSelfApprovel.setValue(false);
		tfParkedAmt.setReadOnly(false);
		tfParkedAmt.setValue("0");
		tfParkedAmt.setReadOnly(true);
		cbCurrency.setValue(null);
		cbApprovelAuth.setValue(null);
		cbApproveManager.setValue(null);
		cbParentAccId.setValue(null);
		tfRemarks.setValue("");
		tfAccountNumber.setComponentError(null);
		tfAccountName.setComponentError(null);
		cbApproveManager.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		lsAccountOwners.setValue(null);
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + username + " > " + "Adding new record...");
		cbAccountGrp.setReadOnly(false);
		tfAccountNumber.setReadOnly(false);
		cbAccountType.setReadOnly(false);
		cbCurrency.setReadOnly(false);
		cbAccountGrp.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadParentAccountList();
				}
			}
		});
		cbAccountGrp.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				try {
					if (cbAccountGrp.getValue() != null) {
						if (cbAccountGrp.getValue().equals("Bank")) {
							cbBankName.setVisible(false);
							cbBankName.setValue(null);
							cbBankBranch.setVisible(false);
							cbBankBranch.setValue(null);
							tfAccountNumber.setReadOnly(false);
							tfAccountNumber.setValue("");
							cbClientName.setVisible(true);
							cbVendorName.setVisible(true);
						} else if (cbAccountGrp.getValue().equals("Pettycash")) {
							tfAccountNumber.setReadOnly(false);
							autogen();
						} else if (cbAccountGrp.getValue().equals("Customer")) {
							tfAccountNumber.setReadOnly(false);
							autogen();
							cbVendorName.setVisible(false);
							cbClientName.setVisible(true);
						} else if (cbAccountGrp.getValue().equals("Vendor")) {
							tfAccountNumber.setReadOnly(false);
							autogen();
							cbClientName.setVisible(false);
							cbVendorName.setVisible(true);
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetSearchDetails();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		tfAccountNumber.setReadOnly(false);
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editAccounts();
		cbAccountGrp.setReadOnly(true);
		tfAccountNumber.setReadOnly(true);
		cbAccountType.setReadOnly(true);
		cbCurrency.setReadOnly(true);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Validating Data ");
		tfAccountNumber.setComponentError(null);
		tfAccountName.setComponentError(null);
		cbAccountGrp.setComponentError(null);
		cbOwnerName.setComponentError(null);
		cbVendorName.setComponentError(null);
		Boolean errorFlag = false;
		if ((tfAccountNumber.getValue() == null) || tfAccountNumber.getValue().trim().length() == 0) {
			tfAccountNumber.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_ACCOUNTS_NUMBER));
			errorFlag = true;
		}
		if ((tfAccountName.getValue() == null) || tfAccountName.getValue().trim().length() == 0) {
			tfAccountName.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_ACCOUNTS_NAME));
			errorFlag = true;
		}
		if ((cbAccountGrp.getValue() == null)) {
			cbAccountGrp.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_TRANSACTIONS_TYPE));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			AccountsDM accountsobj = new AccountsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				accountsobj = beanAccountsDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			accountsobj.setAccGroup((String) cbAccountGrp.getValue());
			accountsobj.setCompanyId(companyId);
			accountsobj.setAccountno(tfAccountNumber.getValue());
			accountsobj.setAccountname(tfAccountName.getValue());
			if (dfAccountDate.getValue() != null) {
				accountsobj.setAccountdt(dfAccountDate.getValue());
			}
			accountsobj.setFinanceyear(tffinanceYr.getValue().toString());
			if (ckGenerateVoucheryn.getValue().equals(true)) {
				accountsobj.setGenerateVoucherYN("Y");
			} else {
				accountsobj.setGenerateVoucherYN("N");
			}
			if (cbOwnerName.getValue() != null) {
				accountsobj.setEmployeeId((Long.valueOf(cbOwnerName.getValue().toString())));
			}
			if (cbBankName.getValue() != null) {
				accountsobj.setBankId((Long.valueOf(cbBankName.getValue().toString())));
			}
			if (dfAccountDate.getValue() != null) {
				accountsobj.setAccountdt(dfAccountDate.getValue());
			}
			if (cbAccountType.getValue() != null) {
				accountsobj.setAccttypeid(Long.valueOf(cbAccountType.getValue().toString()));
			}
			if (cbApprovelAuth.getValue() != null) {
				accountsobj.setApproveauthRMAM((String) cbApprovelAuth.getValue());
			}
			if (cbVendorName.getValue() != null) {
				accountsobj.setVendorId(Long.valueOf(cbVendorName.getValue().toString()));
			}
			if (cbClientName.getValue() != null) {
				accountsobj.setClientId(Long.valueOf(cbClientName.getValue().toString()));
			}
			if (cbBankBranch.getValue() != null) {
				accountsobj.setBankbranchId((Long.valueOf(cbBankBranch.getValue().toString())));
			}
			try {
				if (tfOpenBalance.getValue() != null && tfOpenBalance.getValue().trim().length() > 0) {
					accountsobj.setOpenbalance(new BigDecimal(tfOpenBalance.getValue()));
				} else {
					accountsobj.setOpenbalance(new BigDecimal("0"));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				accountsobj.setOpenbalance(new BigDecimal("0"));
			}
			if (cbStatus.getValue() != null) {
				accountsobj.setAcctstatus((String) cbStatus.getValue());
			}
			try {
				if (ckSelfApprovel.getValue().equals(true)) {
					accountsobj.setSelfapproveyn("Y");
				} else {
					accountsobj.setSelfapproveyn("N");
				}
				if (cbApproveManager.getValue() != null) {
					accountsobj.setApprovemanager((Long) cbApproveManager.getValue());
				}
				if (cbCurrency.getValue() != null) {
					accountsobj.setCcyid((Long) cbCurrency.getValue());
				}
				if (tfCurrentBalance.getValue() != null && tfCurrentBalance.getValue().trim().length() > 0) {
					accountsobj.setCurrentBalance(new BigDecimal(tfCurrentBalance.getValue()));
				} else {
					accountsobj.setCurrentBalance(new BigDecimal("0"));
				}
				if (tfParkedAmt.getValue() != null && tfParkedAmt.getValue().trim().length() > 0) {
					accountsobj.setParkedAmt(new BigDecimal(tfParkedAmt.getValue()));
				} else {
					accountsobj.setParkedAmt(new BigDecimal("0"));
				}
				if (cbParentAccId.getValue() != null) {
					accountsobj.setParentAccId((Long) cbParentAccId.getValue());
				}
				accountsobj.setRemarks(tfRemarks.getValue());
				accountsobj.setLastupdateddt(DateUtils.getcurrentdate());
				accountsobj.setLastupdatedby(username);
				serviceAccounts.saveDetails(accountsobj);
				// for save Account Owner details
				logger.info("User Account Owner> split------------" + lsAccountOwners.getValue().toString());
				String[] split = lsAccountOwners.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "")
						.split(",");
				serviceAccountOwner.deleteAccOwner(accId);
				for (String obj : split) {
					logger.info("User Account Owner> split------------" + obj);
					if (obj.trim().length() > 0) {
						AccountOwnersDM accountOwnerList = new AccountOwnersDM();
						accountOwnerList.setAccRef(accountsobj.getAccountId());
						accountOwnerList.setEmpid(Long.valueOf(obj.trim()));
						accountOwnerList.setAcctOwnerstatus("Active");
						accountOwnerList.setLastupdatedby(username);
						accountOwnerList.setLastupdateddt(DateUtils.getcurrentdate());
						serviceAccountOwner.saveDetails(accountOwnerList);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			resetSearchDetails();
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > "
				+ "Getting audit record for Account ID " + accountId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_FMS_ACCOUNTS);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(accountId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		hlUserInputLayout.removeAllComponents();
		assembleSearchLayout();
		tfAccountName.setComponentError(null);
		tfAccountNumber.setComponentError(null);
		tfAccountNumber.setReadOnly(false);
		tfAccountNumber.setValue("");
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbAccountGrp.setReadOnly(false);
		cbAccountGrp.setValue(null);
		tfAccountNumber.setReadOnly(false);
		tfAccountNumber.setValue("");
		tfAccountName.setValue("");
		dfAccountDate.setValue(null);
		cbAccountType.setReadOnly(false);
		cbAccountType.setValue(null);
		// cbAccountType.setReadOnly(true);
		cbOwnerName.setValue(null);
		cbBankName.setValue(null);
		cbBankBranch.setValue(null);
		cbCurrency.setReadOnly(false);
		cbCurrency.setValue(null);
		// tfOpenBalance.setValue("0");
		// tffinanceYr.setValue(serviceParameter.getParameterValue("FM_FINYEAR", companyId, null));
		// tffinanceYr.setReadOnly(true);
		ckGenerateVoucheryn.setValue(false);
		// cbBankName.setValue(null);
		cbVendorName.setValue(null);
		cbClientName.setValue(null);
		tfRemarks.setValue("");
		// tfCurrentBalance.setValue("0");
		cbApprovelAuth.setValue(null);
		ckSelfApprovel.setValue(false);
		// tfParkedAmt.setValue("0");
		cbCurrency.setValue(null);
		cbApprovelAuth.setValue(null);
		cbApproveManager.setValue(null);
		cbParentAccId.setValue(null);
		tfRemarks.setValue("");
		tfAccountNumber.setComponentError(null);
		tfAccountName.setComponentError(null);
		cbApproveManager.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		lsAccountOwners.setValue(null);
	}
	
	private void autogen() {
		cbBankName.setVisible(true);
		cbBankBranch.setVisible(true);
	}
}
