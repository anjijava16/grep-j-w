/**
 * File Name 		: AccountType.java 
 * Description 		: this class is used for add/edit account type details. 
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
 * 0.1           Mar 05 2014        SOUNDAR C		          Intial Version
 * 0.2 			 0-Jul-2014			Abdullah				  Code Optimization
 */
package com.gnts.fms.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.fms.domain.mst.AccountTypeDM;
import com.gnts.fms.service.mst.AccountTypeService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class AccountType extends BaseUI {
	private static final long serialVersionUID = 1L;
	private AccountTypeService serviceAccountType = (AccountTypeService) SpringContextHelper.getBean("accounttype");
	// form layout for input controls
	private FormLayout formLayout1, formLayout2;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add Input fields
	private TextField tfAccountType;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	// To add Bean Item Container
	private BeanItemContainer<AccountTypeDM> beanAcountTypeDM = null;
	// local variables declaration
	private Long companyId;
	private String accttypeid;
	private int recordCnt = 0;
	private String strLoginUserName;
	// Initialize logger
	private Logger logger = Logger.getLogger(AccountType.class);
	
	// Constructor
	public AccountType() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Inside AccountType() constructor");
		strLoginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Inside Build View");
		// add fields to panel
		tfAccountType = new GERPTextField("Account Type");
		
		tfAccountType.setMaxLength(30);
		formLayout1 = new FormLayout();
		formLayout2 = new FormLayout();
		// add the user input items into appropriate form layout
		formLayout1.addComponent(tfAccountType);
		formLayout2.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(formLayout1);
		hlUserInputLayout.addComponent(formLayout2);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + strLoginUserName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + "," + (String) cbStatus.getValue() + ", "
				+ companyId + ",F" + "Loading Search...");
		List<AccountTypeDM> listAccountype = new ArrayList<AccountTypeDM>();
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + "," + (String) cbStatus.getValue() + ", "
				+ companyId + ",F");
		listAccountype = serviceAccountType.getAccountTypeList(companyId, tfAccountType.getValue(),
				(String) cbStatus.getValue());
		recordCnt = listAccountype.size();
		beanAcountTypeDM = new BeanItemContainer<AccountTypeDM>(AccountTypeDM.class);
		beanAcountTypeDM.addAll(listAccountype);
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ "Got the AccountType result set");
		tblMstScrSrchRslt.setContainerDataSource(beanAcountTypeDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "accttypeid", "accttype", "accttypestatus", "lastupdateddt",
				"lastupdatedby", });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Account Type", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("accttypeid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editAccountType() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (sltedRcd != null) {
			accttypeid = sltedRcd.getItemProperty("accttypeid").getValue().toString();
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
					+ "Selected Account Type Id -> " + accttypeid);
			tfAccountType.setValue(sltedRcd.getItemProperty("accttype").getValue().toString());
			cbStatus.setValue(sltedRcd.getItemProperty("accttypestatus").getValue().toString());
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfAccountType.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfAccountType.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfAccountType.setRequired(true);
		editAccountType();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ "Validating Data ");
		tfAccountType.setComponentError(null);
		if ((tfAccountType.getValue() == null) || tfAccountType.getValue().trim().length() == 0) {
			tfAccountType.setComponentError(new UserError(GERPErrorCodes.NULL_FMS_ACCOUNT_TYPE));
			logger.warn("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
					+ "Throwing ValidationException.");
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ "Saving Data... ");
		AccountTypeDM accType = new AccountTypeDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			accType = beanAcountTypeDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		accType.setCompanyid(companyId);
		accType.setAccttype(tfAccountType.getValue().toString());
		if (cbStatus.getValue() != null) {
			accType.setAccttypestatus((String) cbStatus.getValue());
		}
		accType.setLastupdateddt(DateUtils.getcurrentdate());
		accType.setLastupdatedby(strLoginUserName);
		serviceAccountType.saveDetails(accType);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ "Getting audit record for Account type ID " + accttypeid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_FMS_ACCOUNT_TYPE);
		UI.getCurrent().getSession().setAttribute("audittablepk", accttypeid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ "Canceling action ");
		assembleSearchLayout();
		tfAccountType.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfAccountType.getValue() + ",  Active ," + companyId + ",F"
				+ "Resetting the UI controls");
		tfAccountType.setValue("");
		tfAccountType.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
}
