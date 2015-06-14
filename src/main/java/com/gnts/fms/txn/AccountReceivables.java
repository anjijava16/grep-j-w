package com.gnts.fms.txn;

/**
 * File Name 		: AccountReceivables.java 
 * Description 		: this class is used for Account Receivable details. 
 * Author 			: Abdullah.H
 * Date 			: 5-Aug-2014

 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.

 * Version       Date           	 Modified By               Remarks
 * 0.2          5-Aug-2014    		 Abdullah.H		          Intial Version
 * 
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.service.mst.BranchService;
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
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.fms.domain.txn.AccountReceivablesDM;
import com.gnts.fms.domain.txn.AccountsDM;
import com.gnts.fms.service.txn.AccountReceivablesService;
import com.gnts.fms.service.txn.AccountsService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
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

public class AccountReceivables extends BaseUI {
	private static final long serialVersionUID = 1L;
	/*
	 * Service Declaration
	 */
	private AccountReceivablesService serviceAccountReceivables = (AccountReceivablesService) SpringContextHelper
			.getBean("accountReceivables");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private AccountsService serviceAccounttype = (AccountsService) SpringContextHelper.getBean("accounts");
	// Input Fields
	private ComboBox cbBranchName = new GERPComboBox("Branch Name");
	private PopupDateField dfEntryDate = new GERPPopupDateField("Entry Date");
	private ComboBox cbAccountReference = new GERPComboBox("Account Ref.");
	private TextField tfInvoiceNo = new GERPTextField("Invoice Number");
	private PopupDateField dfInvoiceDate = new GERPPopupDateField("Invoice Date");
	private TextField tfInvoiceAmt = new GERPTextField("Invoice Amt.");
	private TextField tfPaidAmt = new GERPTextField("Paid Amount");
	private TextField tfBalanceAmt = new GERPTextField("Balance Amount");
	private TextArea tfRemarks = new GERPTextArea("Remarks");
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.T_FMS_ACCOUNT_PAYABLES,
			BASEConstants.PAY_STATUS);
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Form layout for input controls
	private FormLayout flFormLayout1, flFormLayout2, flFormLayout3, flFormLayout4;
	private String loginUserName;
	private Long companyId, empId;
	private int recordCnt;
	String primaryid;
	private BeanItemContainer<AccountReceivablesDM> beansAccountReceivablesDM = null;
	private static Logger logger = Logger.getLogger(AccountPayables.class);
	
	// Constructor
	public AccountReceivables() {
		// Get the logged in user name and company id and country id from the session
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Inside AccountPayables() constructor");
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		empId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		cbBranchName.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbAccountReference.setItemCaptionPropertyId("accountname");
		loadAccountTypeList();
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
		// tfAccountNumber.setRequired(false);
		flFormLayout1 = new FormLayout();
		flFormLayout2 = new FormLayout();
		flFormLayout1.addComponent(cbBranchName);
		flFormLayout2.addComponent(cbStatus);
		hlSearchLayout.addComponent(flFormLayout1);
		hlSearchLayout.addComponent(flFormLayout2);
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
		flFormLayout1.addComponent(cbBranchName);
		flFormLayout1.addComponent(dfEntryDate);
		flFormLayout1.addComponent(cbAccountReference);
		flFormLayout2 = new FormLayout();
		flFormLayout2.setSpacing(true);
		flFormLayout2.addComponent(tfInvoiceNo);
		flFormLayout2.addComponent(dfInvoiceDate);
		flFormLayout2.addComponent(tfInvoiceAmt);
		flFormLayout3 = new FormLayout();
		flFormLayout3.setSpacing(true);
		flFormLayout3.addComponent(tfPaidAmt);
		flFormLayout3.addComponent(tfBalanceAmt);
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
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<AccountReceivablesDM> actReceivablesList = new ArrayList<AccountReceivablesDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Search Parameters are "
				+ companyId + ", " + (Long) cbBranchName.getValue() + " , " + tfInvoiceNo.getValue() + ", "
				+ (String) cbStatus.getValue());
		actReceivablesList = serviceAccountReceivables.getAccountsReceivablesList(companyId, tfInvoiceNo.getValue(),
				(String) cbStatus.getValue(), (Long) cbBranchName.getValue(), null);
		recordCnt = actReceivablesList.size();
		beansAccountReceivablesDM = new BeanItemContainer<AccountReceivablesDM>(AccountReceivablesDM.class);
		beansAccountReceivablesDM.addAll(actReceivablesList);
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Got the Account Payables List result set");
		tblMstScrSrchRslt.setContainerDataSource(beansAccountReceivablesDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "accrcbleId", "branchName", "recbleStatus",
				"lastUpadatedDt", "lastUpadatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("accrcbleId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpadatedBy", "No.of Records:" + recordCnt);
	}
	
	// For Load Active Branch Details based on Company
	private void loadBranchList() {
		List<BranchDM> list = serviceBranch.getBranchList(null, null, null, (String) cbStatus.getValue(), companyId,
				"F");
		BeanContainer<Long, BranchDM> bean = new BeanContainer<Long, BranchDM>(BranchDM.class);
		bean.setBeanIdProperty("branchId");
		bean.addAll(list);
		cbBranchName.setContainerDataSource(bean);
	}
	
	// For Load Active Account Type Details based on Company
	private void loadAccountTypeList() {
		List<AccountsDM> list = serviceAccounttype.getAccountsList(companyId, null, null, "Active", null, null, null);
		BeanItemContainer<AccountsDM> bean = new BeanItemContainer<AccountsDM>(AccountsDM.class);
		bean.addAll(list);
		cbAccountReference.setContainerDataSource(bean);
	}
	
	private void editActReceivables() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			AccountReceivablesDM actpayablesList = beansAccountReceivablesDM.getItem(tblMstScrSrchRslt.getValue())
					.getBean();
			primaryid = (itselect.getItemProperty("accrcbleId").getValue().toString());
			if (actpayablesList.getBranchId() != null) {
				cbBranchName.setValue(actpayablesList.getBranchId());
			}
			if (actpayablesList.getEntryDate() != null) {
				dfEntryDate.setValue(actpayablesList.getEntryDate());
			}
			if (actpayablesList.getAccountId() != null) {
				Long editaccount = actpayablesList.getAccountId();
				Collection<?> coll1 = cbAccountReference.getItemIds();
				for (Iterator<?> iterator = coll1.iterator(); iterator.hasNext();) {
					Object itemid = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbAccountReference.getItem(itemid);
					AccountsDM edit = (AccountsDM) item.getBean();
					if (editaccount != null && editaccount.equals(edit.getAccountId())) {
						cbAccountReference.setValue(itemid);
						break;
					} else {
						cbAccountReference.setValue(null);
					}
				}
			}
			if (actpayablesList.getInvoiceNo() != null) {
				tfInvoiceNo.setValue(actpayablesList.getInvoiceNo().toString());
			}
			if (actpayablesList.getInvoiceDate() != null) {
				dfInvoiceDate.setValue(actpayablesList.getInvoiceDate());
			}
			if (actpayablesList.getInvoiceAmt() != null) {
				tfInvoiceAmt.setValue(actpayablesList.getInvoiceAmt().toString());
			}
			if (actpayablesList.getBalanceAmt() != null) {
				tfBalanceAmt.setValue(actpayablesList.getBalanceAmt().toString());
			}
			if (actpayablesList.getPaidAmt() != null) {
				tfPaidAmt.setValue(actpayablesList.getPaidAmt().toString());
			}
			if (actpayablesList.getRemarks() != null) {
				tfRemarks.setValue(actpayablesList.getRemarks());
			}
		}
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
		cbStatus.setValue(null);
		tfInvoiceNo.setValue("");
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		cbAccountReference.setComponentError(null);
		tfInvoiceAmt.setComponentError(null);
		tfPaidAmt.setComponentError(null);
		// remove the components in the search layout and input controls in the same container
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editActReceivables();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		cbBranchName.setComponentError(null);
		cbAccountReference.setComponentError(null);
		tfInvoiceNo.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranchName.getValue() == null)) {
			cbBranchName.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if ((tfInvoiceNo.getValue() == null) || tfInvoiceNo.getValue().trim().length() == 0) {
			tfInvoiceNo.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_INVOICENO));
			errorFlag = true;
		}
		if (cbAccountReference.getValue() == null) {
			cbAccountReference.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_ACCOUNT_REF));
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
			AccountReceivablesDM actRecevablesobj = new AccountReceivablesDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				actRecevablesobj = beansAccountReceivablesDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			actRecevablesobj.setCompanyId(companyId);
			actRecevablesobj.setBranchId((Long) cbBranchName.getValue());
			actRecevablesobj.setEntryDate(dfEntryDate.getValue());
			actRecevablesobj.setAccountId(((AccountsDM) cbAccountReference.getValue()).getAccountId());
			actRecevablesobj.setInvoiceNo(tfInvoiceNo.getValue());
			actRecevablesobj.setInvoiceDate(dfInvoiceDate.getValue());
			BigDecimal transamount = new BigDecimal("0");
			BigDecimal closebalance = new BigDecimal("0");
			BigDecimal accountbalance = new BigDecimal("0");
			if (tfInvoiceAmt.getValue() != null) {
				try {
					transamount = new BigDecimal(tfInvoiceAmt.getValue());
					accountbalance = ((AccountsDM) cbAccountReference.getValue()).getCurrentBalance();
				}
				catch (Exception e) {
				}
			}
			accountbalance = accountbalance.add(transamount);
			closebalance = accountbalance;
			actRecevablesobj.setInvoiceAmt(transamount);
			actRecevablesobj.setBalanceAmt(closebalance);
			actRecevablesobj.setPaidAmt(transamount);
			actRecevablesobj.setActionedBy(empId);
			if (cbStatus.getValue() != null) {
				actRecevablesobj.setRecbleStatus((String) cbStatus.getValue());
			}
			actRecevablesobj.setPreparedBy(empId);
			actRecevablesobj.setRemarks(tfRemarks.getValue());
			actRecevablesobj.setLastUpadatedBy(loginUserName);
			actRecevablesobj.setLastUpadatedDt(DateUtils.getcurrentdate());
			serviceAccountReceivables.saveDetails(actRecevablesobj);
			serviceAccountReceivables.updateAccountBalance(((AccountsDM) cbAccountReference.getValue()).getAccountId(),
					closebalance, null);
			resetSearchDetails();
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info("Save Method------------------------->" + e);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Getting audit record for PNC Dept. ID " + primaryid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_FMS_ACCOUNT_RECEIVABLES);
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
		cbBranchName.setValue(null);
		cbBranchName.setComponentError(null);
		dfEntryDate.setValue(new Date());
		cbAccountReference.setValue(null);
		tfInvoiceNo.setValue("");
		dfInvoiceDate.setValue(null);
		tfInvoiceAmt.setValue("0");
		tfPaidAmt.setValue("0");
		tfBalanceAmt.setValue("0");
		tfRemarks.setValue("");
		// cbActionedBy.setValue(cbActionedBy.getItemIds().iterator().next());
		// cbActionedBy.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbStatus.setValue(null);
	}
}
