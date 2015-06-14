/**
 * File Name 		: AccountOwners.java 
 * Description 		: this class is used for add/edit Account Owner details. 
 * Author 			: SOUNDAR C 
 * Date 			: Mar 31, 2014
 * Modification 	:
 * Modified By 		: SOUNDAR C 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 31 2014        SOUNDAR C		          Intial Version
 * 
 */
package com.gnts.fms.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.mst.ProductCategory;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.fms.domain.txn.AccountOwnersDM;
import com.gnts.fms.domain.txn.AccountsDM;
import com.gnts.fms.service.txn.AccountOwnersService;
import com.gnts.fms.service.txn.AccountsService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;

public class AccountOwners extends BaseUI {
	private AccountOwnersService serviceAccountOwner = (AccountOwnersService) SpringContextHelper
			.getBean("accountowner");
	private AccountsService serviceAccount = (AccountsService) SpringContextHelper.getBean("accounts");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private ComboBox cbAccountNo, cbEmpName, cbStatus;
	// Bean container
	private BeanItemContainer<AccountOwnersDM> beanAccountOwnerDM = null;
	// local variables declaration
	private String AccountOwnerid;
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	// for initialize logger
	private Logger logger = Logger.getLogger(ProductCategory.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public AccountOwners() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ProductCategory() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Product Category UI");
		cbAccountNo = new GERPComboBox("Account Number");
		cbAccountNo.setItemCaptionPropertyId("accountname");
		loadAccountNumber();
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		// Add components for Search Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(cbAccountNo);
		flColumn2.addComponent(cbEmpName);
		flColumn3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSizeUndefined();
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(cbAccountNo);
		flColumn2.addComponent(cbEmpName);
		flColumn3.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Account Owners Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Search...");
		List<AccountOwnersDM> ownerList = new ArrayList<AccountOwnersDM>();
		logger.info("" + "Account Owners : Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + cbEmpName.getValue() + ", " + cbStatus.getValue());
		ownerList = serviceAccountOwner.getAccountOwnerList(null, (Long) cbAccountNo.getValue(),
				(Long) cbEmpName.getValue(), (String) cbStatus.getValue());
		recordCnt = ownerList.size();
		beanAccountOwnerDM = new BeanItemContainer<AccountOwnersDM>(AccountOwnersDM.class);
		beanAccountOwnerDM.addAll(ownerList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the ParentCategory. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanAccountOwnerDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "accOwnerId", "accountno", "employeename",
				"acctOwnerstatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Account Number", "Employee Name", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("accOwnerId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadEmployeeList() {
		try {
			List<EmployeeDM> empList = servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyid, null,
					null, null, null, "F");
			BeanContainer<Long, EmployeeDM> beanAccountNo = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanAccountNo.setBeanIdProperty("employeeid");
			beanAccountNo.addAll(empList);
			cbEmpName.setContainerDataSource(beanAccountNo);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadAccountNumber() {
		try {
			List<AccountsDM> empList = serviceAccount
					.getAccountsList(companyid, null, null, "Active", null, null, null);
			BeanContainer<Long, AccountsDM> beanAccountNo = new BeanContainer<Long, AccountsDM>(AccountsDM.class);
			beanAccountNo.setBeanIdProperty("accountId");
			beanAccountNo.addAll(empList);
			cbAccountNo.setContainerDataSource(beanAccountNo);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void editccountOwner() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected enquiry.Id -> "
				+ AccountOwnerid);
		if (sltedRcd != null) {
			AccountOwnersDM editAccountOwnerlist = new AccountOwnersDM();
			editAccountOwnerlist = beanAccountOwnerDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			String accno = editAccountOwnerlist.getAccountno();
			Collection<?> acccol = cbAccountNo.getItemIds();
			for (Iterator<?> iteratorclient = acccol.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbAccountNo.getItem(itemIdClient);
				// Get the actual bean and use the data
				AccountsDM matObj = (AccountsDM) itemclient.getBean();
				if (accno != null && accno.equals(matObj.getAccountno())) {
					cbAccountNo.setValue(itemIdClient);
				}
			}
			if (editAccountOwnerlist.getEmpid() != null) {
				cbEmpName.setValue(editAccountOwnerlist.getEmpid());
			}
			if (editAccountOwnerlist.getAcctOwnerstatus() != null) {
				cbStatus.setValue(editAccountOwnerlist.getAcctOwnerstatus());
			}
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		hlUserInputLayout.removeAllComponents();
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
		hlUserInputLayout.removeAllComponents();
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbEmpName.setValue(null);
		cbAccountNo.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbAccountNo.setRequired(true);
		cbEmpName.setRequired(true);
		hlUserInputLayout.setSpacing(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbEmpName.setRequired(true);
		cbAccountNo.setRequired(true);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		// reset the input controls to default value
		resetFields();
		assembleUserInputLayout();
		editccountOwner();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
			Boolean errorFlag = false;
			if (cbAccountNo.getValue() == null) {
				cbAccountNo.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_ACCOUNTS_NUMBER));
				errorFlag = true;
			} else {
				cbAccountNo.setComponentError(null);
				errorFlag = false;
			}
			if (cbEmpName.getValue() == null) {
				cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
				errorFlag = true;
			} else {
				cbEmpName.setComponentError(null);
				errorFlag = false;
			}
			if (errorFlag) {
				throw new ERPException.ValidationException();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		AccountOwnersDM accountOwnerobj = new AccountOwnersDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			accountOwnerobj = beanAccountOwnerDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		accountOwnerobj.setAccRef((Long) cbAccountNo.getValue());
		accountOwnerobj.setEmpid((Long.valueOf(cbEmpName.getValue().toString())));
		accountOwnerobj.setAcctOwnerstatus(cbStatus.getValue().toString());
		accountOwnerobj.setLastupdateddt(DateUtils.getcurrentdate());
		accountOwnerobj.setLastupdatedby(username);
		serviceAccountOwner.saveDetails(accountOwnerobj);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Producat. ID " + AccountOwnerid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_PRODUCT_CATEGORY);
		UI.getCurrent().getSession().setAttribute("audittablepk", AccountOwnerid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbAccountNo.setRequired(false);
		cbEmpName.setRequired(false);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbAccountNo.setValue(null);
		cbAccountNo.setComponentError(null);
		cbEmpName.setValue(null);
		cbEmpName.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
}
