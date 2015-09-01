package com.gnts.fms.txn;

/**
 * File Name 		: Transactions.java 
 * Description 		: this class is used for add/edit Transaction details. 
 * Author 			: SOUNDARC 
 * Date 			: Mar 11, 2014
 * Modification 	:
 * Modified By 		: SOUNDARC 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.

 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 11, 2014        SOUNDARC		          Initial Version
 * 0.2			 29-Aug-2014		 Abdullah.H				  Code Optimization
 */
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPSaveNotification;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.constants.SessionForModule;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.erputil.util.RandomNoGenerator;
import com.gnts.fms.domain.mst.TransactionTypeDM;
import com.gnts.fms.domain.txn.AccountsDM;
import com.gnts.fms.domain.txn.TransactionsDM;
import com.gnts.fms.service.mst.TransactionTypeService;
import com.gnts.fms.service.txn.AccountsService;
import com.gnts.fms.service.txn.TransactionsService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Transactions extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	/*
	 * Service Declaration
	 */
	private TransactionsService serviceTransactions = (TransactionsService) SpringContextHelper.getBean("transaction");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private AccountsService serviceAccounttype = (AccountsService) SpringContextHelper.getBean("accounts");
	private TransactionTypeService serviceTransType = (TransactionTypeService) SpringContextHelper.getBean("transtype");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private Button btnaddamt = new GERPButton("Add", "addbt", this);
	private PopupDateField dfTransactionDate = new GERPPopupDateField("Transaction Date");
	private ComboBox cbAccountReference = new GERPComboBox("Account Ref.");
	private ComboBox cbApproveManager = new GERPComboBox("Approve Name");
	private ComboBox cbTransactionType = new GERPComboBox("Transaction Type");
	private TextField tfTransactionAmount = new GERPTextField("Trans. Amount");
	private TextField tfChequeNumber = new GERPTextField("Cheque Number");
	private PopupDateField dfChequeDate = new GERPPopupDateField("Cheque Date");
	private ComboBox cbDepartmentName = new GERPComboBox("Department Name");
	private TextArea tfInstrumentRemarks = new GERPTextArea("Remarks");
	private ComboBox cbPaymentMode = new GERPComboBox("Payment Mode");
	private TextField tfVoucherNo = new GERPTextField("Voucher No.");
	private TextArea tfApproverRemarks = new GERPTextArea("Approver Remarks");
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.T_FMS_ACCOUNT_TXNS, BASEConstants.TXN_STATUS);
	private TextField tfCloseBalance = new GERPTextField("Closing Balance");
	private ComboBox cbProjectName = new GERPComboBox("Project Name");
	private TextField tfPaidAmt = new GERPTextField("Paid");
	private TextField tfBalAmt = new GERPTextField("Balance");
	private GERPTextField tfAccount = new GERPTextField("Account");
	private TextArea tfRefDetails = new TextArea("Ref. Details");
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Form layout for input controls
	private FormLayout flFormLayout1, flFormLayout2, flFormLayout3, flFormLayout4;
	private String loginUserName;
	private Long companyId, empId, transactionId;
	private int recordCnt;
	private BeanItemContainer<TransactionsDM> beanTransactionDM = null;
	private Logger logger = Logger.getLogger(Transactions.class);
	
	public Transactions() {
		// Get the logged in user name and company id and country id from the session
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Inside Transactions() constructor");
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		empId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		// Loading the UI
		buildView();
	}
	
	/*
	 * buildMainview()-->for screen UI design
	 * @param clArgumentLayout hlHeaderLayout
	 */
	private void buildView() {
		cbAccountReference.setWidth("150");
		cbApproveManager.setWidth("150");
		cbDepartmentName.setWidth("150");
		cbTransactionType.setWidth("150");
		tfApproverRemarks.setHeight("43px");
		tfInstrumentRemarks.setHeight("86px");
		tfRefDetails.setHeight("43px");
		tfRefDetails.setWidth("150");
		cbApproveManager.setItemCaptionPropertyId("firstname");
		tblMstScrSrchRslt.setPageLength(12);
		tblMstScrSrchRslt.setFooterVisible(true);
		loadEmployeeList();
		cbAccountReference.setItemCaptionPropertyId("accountname");
		loadAccountTypeList();
		cbDepartmentName.setItemCaptionPropertyId("ccyname");
		cbTransactionType.setItemCaptionPropertyId("transtypename");
		loadMTranstypeList();
		cbProjectName.setItemCaptionPropertyId("projectName");
		tfChequeNumber.setMaxLength(30);
		cbPaymentMode.setItemCaptionPropertyId("lookupname");
		loadPaymentmodeList();
		btnaddamt.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					new GERPSaveNotification();
				}
				catch (Exception e) {
					try {
						throw new ERPException.SaveException();
					}
					catch (SaveException e1) {
						logger.error("Company ID : "
								+ UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
								+ " | User Name : "
								+ UI.getCurrent().getSession().getAttribute("loginUserName").toString() + " > "
								+ "Exception " + e1.getMessage());
						e1.printStackTrace();
					}
				}
			}
		});
		cbAccountReference.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				tfVoucherNo.setReadOnly(false);
				tfVoucherNo.setValue("");
				if (cbAccountReference.getValue() != null) {
					if (((AccountsDM) cbAccountReference.getValue()).getGenerateVoucherYN() != null
							&& ((AccountsDM) cbAccountReference.getValue()).getGenerateVoucherYN().equals("Y")) {
						RandomNoGenerator randomGen = new RandomNoGenerator();
						tfVoucherNo.setValue(randomGen.generateRandomString());
						tfVoucherNo.setReadOnly(true);
					} else {
						tfVoucherNo.setReadOnly(false);
					}
				}
			}
		});
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	// For Load Active Employee Details based on Company
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> employeebeans = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			employeebeans.setBeanIdProperty("employeeid");
			employeebeans.addAll(servicebeanEmployee.getEmployeeList(null, null, null, (String) cbStatus.getValue(),
					companyId, null, null, null, null, "P"));
			cbApproveManager.setContainerDataSource(employeebeans);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// For Load Active Account Type Details based on Company
	private void loadAccountTypeList() {
		try {
			BeanItemContainer<AccountsDM> bean = new BeanItemContainer<AccountsDM>(AccountsDM.class);
			bean.addAll(serviceAccounttype.getAccountsList(companyId, null, null, "Active", null, null, null));
			cbAccountReference.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// For Load Active Transaction Type Details based on Company
	private void loadMTranstypeList() {
		try {
			BeanItemContainer<TransactionTypeDM> bean = new BeanItemContainer<TransactionTypeDM>(
					TransactionTypeDM.class);
			bean.addAll(serviceTransType.getTransactionTypeList(companyId, null,"Active", null,
					null));
			cbTransactionType.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// * For Load Active Payment mode Details based on Company
	private void loadPaymentmodeList() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Loading Gender Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyId,
					SessionForModule.getModuleId("FMS"), "Active", "FM_PAYMODE"));
			cbPaymentMode.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		cbAccountReference.setRequired(false);
		flFormLayout1 = new FormLayout();
		flFormLayout2 = new FormLayout();
		flFormLayout3 = new FormLayout();
		flFormLayout1.addComponent(cbAccountReference);
		flFormLayout2.addComponent(cbPaymentMode);
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
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		flFormLayout1 = new FormLayout();
		flFormLayout2 = new FormLayout();
		flFormLayout3 = new FormLayout();
		flFormLayout4 = new FormLayout();
		flFormLayout1.setSpacing(true);
		flFormLayout1.addComponent(dfTransactionDate);
		flFormLayout1.addComponent(cbAccountReference);
		flFormLayout1.addComponent(cbTransactionType);
		flFormLayout1.addComponent(tfTransactionAmount);
		flFormLayout2.addComponent(cbPaymentMode);
		flFormLayout2.addComponent(tfAccount);
		flFormLayout2.addComponent(tfRefDetails);
		flFormLayout3.addComponent(tfInstrumentRemarks);
		flFormLayout4.addComponent(tfApproverRemarks);
		flFormLayout4.addComponent(cbStatus);
		flFormLayout3.setSpacing(true);
		flFormLayout2.setSpacing(true);
		HorizontalLayout amtLayout = new HorizontalLayout();
		amtLayout.addComponent(tfPaidAmt);
		VerticalLayout vl = new VerticalLayout();
		vl.addComponent(tfBalAmt);
		vl.setSpacing(true);
		vl.addComponent(btnaddamt);
		flFormLayout4.setSpacing(true);
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
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<TransactionsDM> listTrans = new ArrayList<TransactionsDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Search Parameters are "
				+ companyId + ", " + cbAccountReference.getValue() + ", " + (String) cbStatus.getValue());
		listTrans = serviceTransactions.getTransactionDetails(companyId, null, null, (String) cbStatus.getValue(),
				null, (String) cbPaymentMode.getValue(), (Long) cbAccountReference.getValue());
		recordCnt = listTrans.size();
		beanTransactionDM = new BeanItemContainer<TransactionsDM>(TransactionsDM.class);
		beanTransactionDM.addAll(listTrans);
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Got the Account List result set");
		tblMstScrSrchRslt.setContainerDataSource(beanTransactionDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "accTxnId", "accName", "paymentmode", "accountRefName",
				"transamount", "txnStatus", "lastupdateddt", "lastupdatedby", });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Account Ref.", "Payment Mode", "Account",
				"Trans. Amount", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("accTxnId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnAlignment("transamount", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records:" + recordCnt);
	}
	
	private void editTransactions() {
		if (tblMstScrSrchRslt.getValue() != null) {
			TransactionsDM transactionsDM = beanTransactionDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			transactionId = transactionsDM.getAccTxnId();
			if (transactionsDM.getTransdt() != null) {
				try {
					dfTransactionDate.setValue(transactionsDM.getTransdt());
				}
				catch (Exception e) {
					dfTransactionDate.setValue(null);
				}
			} else {
				dfTransactionDate.setValue(null);
			}
			if (transactionsDM.getValueDate() == null) {
				transactionsDM.setValueDate(dfTransactionDate.getValue());
			}
			// For select account number
			if (transactionsDM.getAccountId() != null) {
				Long editaccount = transactionsDM.getAccountId();
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
			// For select Transaction type
			if (transactionsDM.getTranstypeid() != null) {
				Long edittranstype = transactionsDM.getTranstypeid();
				Collection<?> coll2 = cbTransactionType.getItemIds();
				for (Iterator<?> iterator = coll2.iterator(); iterator.hasNext();) {
					Object itemid = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbTransactionType.getItem(itemid);
					TransactionTypeDM edit = (TransactionTypeDM) item.getBean();
					if (edittranstype != null && edittranstype.equals(edit.getTranstypeid())) {
						cbTransactionType.setValue(itemid);
						break;
					} else {
						cbTransactionType.setValue(null);
					}
				}
			}
			if (transactionsDM.getTransamount() != null) {
				tfTransactionAmount.setValue(transactionsDM.getTransamount().toString());
			}
			if (transactionsDM.getChequedt() != null) {
				dfChequeDate.setValue(transactionsDM.getChequedt());
			}
			if (transactionsDM.getChequeNo() != null) {
				tfChequeNumber.setValue(transactionsDM.getChequeNo());
			}
			if (transactionsDM.getInstruremark() != null) {
				tfInstrumentRemarks.setValue(transactionsDM.getInstruremark());
			}
			if (transactionsDM.getPaymentmode() != null) {
				cbPaymentMode.setValue(transactionsDM.getPaymentmode());
			}
			if (transactionsDM.getAppremarks() != null) {
				tfApproverRemarks.setValue(transactionsDM.getAppremarks());
			}
			tfVoucherNo.setReadOnly(false);
			tfVoucherNo.setValue(transactionsDM.getVoucherNo());
			cbStatus.setValue(transactionsDM.getTxnStatus());
			tfAccount.setValue(transactionsDM.getAccountRefName());
			tfRefDetails.setValue(transactionsDM.getRefDetails());
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
		cbTransactionType.setValue(null);
		cbPaymentMode.setValue(null);
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		cbAccountReference.setRequired(true);
		cbTransactionType.setRequired(true);
		tfTransactionAmount.setRequired(true);
		tfVoucherNo.setRequired(false);
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		cbAccountReference.setRequired(true);
		cbTransactionType.setRequired(true);
		tfTransactionAmount.setRequired(true);
		tfVoucherNo.setRequired(false);
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editTransactions();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		cbAccountReference.setComponentError(null);
		tfTransactionAmount.setComponentError(null);
		// tfVoucherNo.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbAccountReference.getValue() == null)) {
			cbAccountReference.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_ACCOUNT_REF));
			errorFlag = true;
		}
		if ((cbTransactionType.getValue() == null)) {
			cbTransactionType.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_TRANSACTIONS_TYPE));
			errorFlag = true;
		}
		if (tfTransactionAmount.getValue() == "0") {
			tfTransactionAmount.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_TRANSAMT));
			errorFlag = true;
		} else {
			tfTransactionAmount.setComponentError(null);
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Saving Data... ");
			TransactionsDM tranObj = new TransactionsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				tranObj = beanTransactionDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			tranObj.setPreparedBy(empId);
			tranObj.setCompanyid(companyId);
			tranObj.setTransdt((Date) dfTransactionDate.getValue());
			tranObj.setAccountId(((AccountsDM) cbAccountReference.getValue()).getAccountId());
			if (tranObj.getValueDate() == null) {
				tranObj.setValueDate(dfTransactionDate.getValue());
			}
			tranObj.setChequedt(dfChequeDate.getValue());
			tranObj.setChequeNo(tfChequeNumber.getValue());
			tranObj.setInstruremark(tfInstrumentRemarks.getValue());
			tranObj.setAccountRefName(tfAccount.getValue());
			tranObj.setRefDetails(tfRefDetails.getValue());
			tranObj.setPaymentmode((String) cbPaymentMode.getValue());
			if (((AccountsDM) cbAccountReference.getValue()).getEmployeeId() != null) {
				tranObj.setEmployeeid(((AccountsDM) cbAccountReference.getValue()).getEmployeeId());
			}
			tranObj.setTranstypeid(((TransactionTypeDM) cbTransactionType.getValue()).getTranstypeid());
			BigDecimal parkedamount = new BigDecimal("0");
			BigDecimal openbalance = new BigDecimal("0");
			BigDecimal transamount = new BigDecimal("0");
			BigDecimal closebalance = new BigDecimal("0");
			BigDecimal accountbalance = new BigDecimal("0");
			if (tfTransactionAmount.getValue() != null) {
				try {
					transamount = new BigDecimal(tfTransactionAmount.getValue());
					accountbalance = ((AccountsDM) cbAccountReference.getValue()).getCurrentBalance();
					openbalance = accountbalance;
				}
				catch (Exception e) {
				}
			}
			if (((TransactionTypeDM) cbTransactionType.getValue()).getCrdr().toUpperCase().equals("C")
					|| ((TransactionTypeDM) cbTransactionType.getValue()).getCrdr().toUpperCase().equals("CREDIT")) {
				accountbalance = accountbalance.add(transamount);
				closebalance = accountbalance;
			} else {
				accountbalance = accountbalance.subtract(transamount);
				closebalance = accountbalance;
			}
			tranObj.setTransamount(transamount);
			tranObj.setOpenbalance(openbalance);
			tranObj.setCloseamount(closebalance);
			if (cbStatus.getValue() != null) {
				tranObj.setTxnStatus((String) cbStatus.getValue());
			}
			tranObj.setAppremarks(tfApproverRemarks.getValue());
			tranObj.setLastupdateddt(DateUtils.getcurrentdate());
			tranObj.setLastupdatedby(loginUserName);
			tranObj.setVoucherNo(tfVoucherNo.getValue());
			serviceTransactions.saveTransactionDetails(tranObj);
			transactionId = tranObj.getAccTxnId();
			serviceTransactions.updateAccountBalance(((AccountsDM) cbAccountReference.getValue()).getAccountId(),
					closebalance, parkedamount);
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
				+ "Getting audit record for PNC Dept. ID " + transactionId.toString());
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_FMS_FINANCE_PLAN);
		UI.getCurrent().getSession().setAttribute("audittablepk", transactionId.toString());
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
		tfTransactionAmount.setComponentError(null);
		dfTransactionDate.setComponentError(null);
		cbTransactionType.setComponentError(null);
		cbAccountReference.setComponentError(null);
		tfVoucherNo.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbStatus.setValue(null);
		tfChequeNumber.setValue("");
		tfCloseBalance.setValue("0");
		dfTransactionDate.setValue(new Date());
		cbAccountReference.setValue(null);
		cbApproveManager.setValue(null);
		cbTransactionType.setValue(null);
		cbDepartmentName.setValue(null);
		tfInstrumentRemarks.setValue("");
		tfTransactionAmount.setValue("0");
		tfApproverRemarks.setValue("");
		tfInstrumentRemarks.setValue("");
		tfVoucherNo.setReadOnly(false);
		tfVoucherNo.setValue("");
		cbPaymentMode.setValue(null);
		dfChequeDate.setValue(null);
		cbProjectName.setValue(null);
		tfRefDetails.setValue("");
		tfAccount.setValue("");
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, Long> parameterMap = new HashMap<String, Long>();
			parameterMap.put("ATXNID", transactionId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/cashvoucher"); // pif is the name of my jasper
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				statement.close();
				Database.close(connection);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
