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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.fms.domain.txn.AccountsDM;
import com.gnts.fms.domain.txn.FundRequestDM;
import com.gnts.fms.service.txn.AccountsService;
import com.gnts.fms.service.txn.FundRequestService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class FundRequest extends BaseUI {
	private static final long serialVersionUID = 1L;
	/*
	 * Service Declaration
	 */
	private FundRequestService serviceFundRequest = (FundRequestService) SpringContextHelper.getBean("fundeRequest");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private AccountsService serviceAccounttype = (AccountsService) SpringContextHelper.getBean("accounts");
	// Input Fields
	private PopupDateField dfFundReqDt = new GERPPopupDateField("Fund Req. Date");
	private ComboBox cbAccountReference = new GERPComboBox("Account Ref.");
	private ComboBox cbBranchName = new GERPComboBox("Branch Name");
	private TextField tfReqAmt = new GERPTextField("Request Amt.");
	private TextField tfReqDtl = new GERPTextField("Request Dtls.");
	private TextField tfApprvAmt = new GERPTextField("Approve Amt.");
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.T_FMS_FUND_REQUEST, BASEConstants.FUND_STATUS);
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Form layout for input controls
	private FormLayout flFormLayout1, flFormLayout2, flFormLayout3, flFormLayout4;
	private String loginUserName;
	private Long companyId, empId;
	private int recordCnt;
	private String primaryid;
	private BeanItemContainer<FundRequestDM> beansFundRequestDM = null;
	private Logger logger = Logger.getLogger(FundRequest.class);
	
	// Constructor
	public FundRequest() {
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
		flFormLayout1.addComponent(dfFundReqDt);
		flFormLayout1.addComponent(cbBranchName);
		flFormLayout2 = new FormLayout();
		flFormLayout2.setSpacing(true);
		flFormLayout2.addComponent(cbAccountReference);
		flFormLayout2.addComponent(tfReqAmt);
		flFormLayout3 = new FormLayout();
		flFormLayout3.addComponent(tfReqDtl);
		flFormLayout3.addComponent(tfApprvAmt);
		flFormLayout3.setSpacing(true);
		flFormLayout4 = new FormLayout();
		flFormLayout4.setSpacing(true);
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
			List<FundRequestDM> listFundReqest = new ArrayList<FundRequestDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Search Parameters are " + companyId + ", " + (Long) cbBranchName.getValue() + " , "
					+ (String) cbStatus.getValue());
			listFundReqest = serviceFundRequest.getFundRequestList(companyId, (Long) cbBranchName.getValue(),
					(String) cbStatus.getValue());
			recordCnt = listFundReqest.size();
			beansFundRequestDM = new BeanItemContainer<FundRequestDM>(FundRequestDM.class);
			beansFundRequestDM.addAll(listFundReqest);
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Got the Account Payables List result set");
			tblMstScrSrchRslt.setContainerDataSource(beansFundRequestDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "fundrqstId", "branchName", "rqstStatus",
					"lastUpdateDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Status", "Last Updated Date",
					"Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("fundrqstId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records:" + recordCnt);
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
			bean.addAll(serviceBranch.getBranchList(null, null, null, (String) cbStatus.getValue(), companyId, "P"));
			cbBranchName.setContainerDataSource(bean);
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
	
	private void editFundReq() {
		if (tblMstScrSrchRslt.getValue() != null) {
			FundRequestDM fundRequestDM = beansFundRequestDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			primaryid = fundRequestDM.getFundrqstId().toString();
			if (fundRequestDM.getFundrqsrDt() != null) {
				dfFundReqDt.setValue(fundRequestDM.getFundrqsrDt());
			}
			Long accid = fundRequestDM.getAccountId();
			Collection<?> accids = cbAccountReference.getItemIds();
			for (Iterator<?> iterator = accids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbAccountReference.getItem(itemId);
				// Get the actual bean and use the data
				AccountsDM st = (AccountsDM) item.getBean();
				if (accid != null && accid.equals(st.getAccountId())) {
					cbAccountReference.setValue(itemId);
				}
			}
			if (fundRequestDM.getBranchId() != null) {
				cbBranchName.setValue(fundRequestDM.getBranchId());
			}
			if (fundRequestDM.getReqdAmt() != null) {
				tfReqAmt.setValue(fundRequestDM.getReqdAmt().toString());
			}
			if (fundRequestDM.getReqDtl() != null) {
				tfReqDtl.setValue(fundRequestDM.getReqDtl());
			}
			if (fundRequestDM.getApprvAmt() != null) {
				tfApprvAmt.setValue(fundRequestDM.getApprvAmt().toString());
			}
			cbStatus.setValue(fundRequestDM.getRqstStatus());
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
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		cbAccountReference.setRequired(true);
		cbAccountReference.setComponentError(null);
		cbBranchName.setRequired(true);
		// remove the components in the search layout and input controls in the same container
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		cbAccountReference.setRequired(true);
		cbBranchName.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editFundReq();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		cbBranchName.setComponentError(null);
		cbAccountReference.setComponentError(null);
		Boolean errorFlag = false;
		if (cbBranchName.getValue() == null) {
			cbBranchName.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
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
			FundRequestDM fundReqobj = new FundRequestDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				fundReqobj = beansFundRequestDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			fundReqobj.setFundrqsrDt(dfFundReqDt.getValue());
			if (cbAccountReference.getValue() != null) {
				fundReqobj.setAccountId(((AccountsDM) cbAccountReference.getValue()).getAccountId());
			}
			fundReqobj.setCompanyId(companyId);
			fundReqobj.setBranchId((Long) cbBranchName.getValue());
			if (tfReqAmt.getValue() != null && tfReqAmt.getValue().trim().length() > 0) {
				fundReqobj.setReqdAmt(new BigDecimal(tfReqAmt.getValue()));
			} else {
				fundReqobj.setReqdAmt(new BigDecimal("0"));
			}
			fundReqobj.setReqDtl(tfReqDtl.getValue());
			if (tfApprvAmt.getValue() != null && tfApprvAmt.getValue().trim().length() > 0) {
				fundReqobj.setApprvAmt(new BigDecimal(tfApprvAmt.getValue()));
			} else {
				fundReqobj.setApprvAmt(new BigDecimal("0"));
			}
			fundReqobj.setPreparedBy(empId);
			fundReqobj.setReviewdBy(null);
			fundReqobj.setActionedBy(null);
			fundReqobj.setLastUpdatedBy(loginUserName);
			fundReqobj.setLastUpdateDt(DateUtils.getcurrentdate());
			fundReqobj.setRqstStatus((String) cbStatus.getValue());
			serviceFundRequest.saveDetails(fundReqobj);
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
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_FMS_FUND_REQUEST);
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
		dfFundReqDt.setValue(null);
		cbAccountReference.setValue(null);
		cbBranchName.setValue(null);
		cbBranchName.setComponentError(null);
		tfReqAmt.setValue("");
		tfReqDtl.setValue("");
		tfApprvAmt.setValue("0");
		cbStatus.setValue(null);
	}
}
