/**
 * File Name 		: AccountPayables.java 
 * Description 		: this class is used for Account Payable details. 
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
package com.gnts.fms.txn;

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
import com.gnts.fms.domain.txn.AccountPayablesDM;
import com.gnts.fms.domain.txn.AccountsDM;
import com.gnts.fms.service.txn.AccountPayablesService;
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

public class AccountPayables extends BaseUI {
	private static final long serialVersionUID = 1L;
	/*
	 * Service Declaration
	 */
	private AccountPayablesService serviceAccountPayables = (AccountPayablesService) SpringContextHelper
			.getBean("accountPayables");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private AccountsService serviceAccounttype = (AccountsService) SpringContextHelper.getBean("accounts");
	// Input Fields
	private ComboBox cbBranchName = new GERPComboBox("Branch Name");
	private PopupDateField dfEntryDate = new GERPPopupDateField("Entry Date");
	private ComboBox cbAccountReference = new GERPComboBox("Account Ref.");
	private TextField tfBillNo = new GERPTextField("Bill Number");
	private PopupDateField dfBillDate = new GERPPopupDateField("Bill Date");
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
	private BeanItemContainer<AccountPayablesDM> beansAccountPayablesDM = null;
	private static Logger logger = Logger.getLogger(AccountPayables.class);
	
	// Constructor
	public AccountPayables() {
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
		flFormLayout1 = new FormLayout();
		flFormLayout2 = new FormLayout();
		flFormLayout1.addComponent(cbBranchName);
		cbBranchName.setRequired(false);
		cbBranchName.setComponentError(null);
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
		flFormLayout2.addComponent(tfBillNo);
		flFormLayout2.addComponent(dfBillDate);
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
		List<AccountPayablesDM> actpayablesList = new ArrayList<AccountPayablesDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Search Parameters are "
				+ companyId + ", " + (Long) cbBranchName.getValue() + " , " + tfBillNo.getValue() + ", "
				+ (String) cbStatus.getValue());
		actpayablesList = serviceAccountPayables.getAccountpayablesList(companyId, (Long) cbBranchName.getValue(),
				tfBillNo.getValue(), (String) cbStatus.getValue(), null);
		recordCnt = actpayablesList.size();
		beansAccountPayablesDM = new BeanItemContainer<AccountPayablesDM>(AccountPayablesDM.class);
		beansAccountPayablesDM.addAll(actpayablesList);
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Got the Account Payables List result set");
		tblMstScrSrchRslt.setContainerDataSource(beansAccountPayablesDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "accPayableId", "branchName", "payStatus", "lastUpdatedDt",
				"lastUpadatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("accPayableId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpadatedBy", "No.of Records:" + recordCnt);
	}
	
	// For Load Active Branch Details based on Company
	private void loadBranchList() {
		List<BranchDM> list = serviceBranch.getBranchList(null, null, null, (String) cbStatus.getValue(), companyId,
				"P");
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
	
	private void editActPayables() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			AccountPayablesDM actpayablesList = beansAccountPayablesDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			primaryid = (itselect.getItemProperty("accPayableId").getValue().toString());
			if (actpayablesList.getBranchId() != null) {
				cbBranchName.setValue(actpayablesList.getBranchId());
			}
			if (actpayablesList.getEntryDate() != null) {
				dfEntryDate.setValue(actpayablesList.getEntryDate());
			}
			// For select account number
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
			if (actpayablesList.getBillNo() != null) {
				tfBillNo.setValue(actpayablesList.getBillNo().toString());
			}
			if (actpayablesList.getBillDate() != null) {
				dfBillDate.setValue(actpayablesList.getBillDate());
			}
			if (actpayablesList.getInvoiceAmt() != null) {
				tfInvoiceAmt.setValue(actpayablesList.getInvoiceAmt().toString());
			}
			tfBalanceAmt.setReadOnly(false);
			if (actpayablesList.getBalanceAmt() != null) {
				tfBalanceAmt.setValue(actpayablesList.getBalanceAmt().toString());
			}
			tfBalanceAmt.setReadOnly(true);
			tfPaidAmt.setReadOnly(false);
			if (actpayablesList.getPaidAmt() != null) {
				tfPaidAmt.setValue(actpayablesList.getPaidAmt().toString());
			}
			tfPaidAmt.setReadOnly(true);
			if (actpayablesList.getRemarks() != null) {
				tfRemarks.setValue(actpayablesList.getRemarks());
			}
			cbStatus.setValue(actpayablesList.getPayStatus());
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
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbStatus.setValue(null);
		tfBillNo.setValue("");
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		cbAccountReference.setComponentError(null);
		// cbActionedBy.setComponentError(null);
		cbBranchName.setRequired(true);
		cbAccountReference.setRequired(true);
		// cbActionedBy.setRequired(true);
		// remove the components in the search layout and input controls in the same container
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		cbBranchName.setRequired(true);
		cbAccountReference.setRequired(true);
		// cbActionedBy.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editActPayables();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		cbBranchName.setComponentError(null);
		cbAccountReference.setComponentError(null);
		// cbActionedBy.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranchName.getValue() == null)) {
			cbBranchName.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if (cbAccountReference.getValue() == null) {
			cbAccountReference.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_ACCOUNT_REF));
			errorFlag = true;
		}
		// if (tfInvoiceAmt.getValue() == "0") {
		// tfInvoiceAmt.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_INVOICEAMT));
		// errorFlag = true;
		// } else {
		// tfInvoiceAmt.setComponentError(null);
		// }
		// if (cbActionedBy.getValue() == null){
		// cbActionedBy.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_ACTIONEDBY));
		// errorFlag = true;
		// }
		//
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Saving Data... ");
		try {
			AccountPayablesDM actPayablesobj = new AccountPayablesDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				actPayablesobj = beansAccountPayablesDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			actPayablesobj.setCompanyId(companyId);
			actPayablesobj.setBranchId((Long) cbBranchName.getValue());
			actPayablesobj.setEntryDate(dfEntryDate.getValue());
			actPayablesobj.setAccountId(((AccountsDM) cbAccountReference.getValue()).getAccountId());
			actPayablesobj.setBillNo(tfBillNo.getValue());
			actPayablesobj.setBillDate(dfBillDate.getValue());
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
			accountbalance = accountbalance.subtract(transamount);
			closebalance = accountbalance;
			actPayablesobj.setInvoiceAmt(transamount);
			actPayablesobj.setBalanceAmt(closebalance);
			actPayablesobj.setPaidAmt(transamount);
			actPayablesobj.setActionedBy(empId);
			if (cbStatus.getValue() != null) {
				actPayablesobj.setPayStatus((String) cbStatus.getValue());
			}
			actPayablesobj.setPreparedBy(empId);
			actPayablesobj.setRemarks(tfRemarks.getValue());
			actPayablesobj.setLastUpadatedBy(loginUserName);
			actPayablesobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			serviceAccountPayables.saveDetails(actPayablesobj);
			serviceAccountPayables.updateAccountBalance(((AccountsDM) cbAccountReference.getValue()).getAccountId(),
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
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_FMS_ACCOUNT_PAYABLES);
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
		dfEntryDate.setValue(new Date());
		cbAccountReference.setValue(null);
		tfBillNo.setValue("");
		dfBillDate.setValue(null);
		tfInvoiceAmt.setValue("0");
		tfPaidAmt.setReadOnly(false);
		tfPaidAmt.setValue("0");
		tfPaidAmt.setReadOnly(true);
		tfBalanceAmt.setReadOnly(false);
		tfBalanceAmt.setValue("0");
		tfBalanceAmt.setReadOnly(true);
		tfRemarks.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbStatus.setValue(null);
	}
}
