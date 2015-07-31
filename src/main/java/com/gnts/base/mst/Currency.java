/**
 * File Name	:	Currency.java
 * Description	:	To Handle Currency Web page requests.
 * Author		:	Priyanga M
 * Date			:	Feb 28, 2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date         	Modified By            	 Remarks
 * 0.1 			Feb 28, 2014		Priyanga M				To Handle Currency Web page requests.
 * 0.2			jun 19, 2014		Abdullah H				Code re-factoring
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CurrencyDM;
import com.gnts.base.service.mst.CurrencyService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

//import com.vaadin.event.ShortcutAction.KeyCode;
public class Currency extends BaseUI {
	private CurrencyService serviceCurrency = (CurrencyService) SpringContextHelper.getBean("currency");
	// form layout for input controls
	private FormLayout flCurrencyCode, flCurrencyName, flCurrencyStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfCurrencyName, tfCurrencyCode;
	private ComboBox cbCurrencyStatus;
	// Bean container
	private BeanItemContainer<CurrencyDM> beanCurrencyDM = null;
	// local variables declaration
	private String strLoginUserName;
	private int recordCnt = 0;
	private Long companyId;
	// Initialize logger
	private Logger logger = Logger.getLogger(Currency.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Currency() {
		// Get the logged in user name from the session
		strLoginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Inside Currency() constructor");
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		// Loading the UI
		buildView();
	}
	
	// Build the UI components
	private void buildView() {
		logger.info("Company ID :" + companyId + "| Login User Name : " + strLoginUserName + " > "
				+ "Painting Currency UI");
		// Currency text fields
		tfCurrencyCode = new GERPTextField("Currency Code");
		tfCurrencyName = new GERPTextField("Currency Name");
		// Currency Combo Box
		cbCurrencyStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// This Button is not visible
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnAuditRecords.setVisible(false);
		// create form layouts to hold the input items
		flCurrencyCode = new FormLayout();
		flCurrencyName = new FormLayout();
		flCurrencyStatus = new FormLayout();
		// create form layouts to hold the input items
		flCurrencyCode.addComponent(tfCurrencyCode);
		flCurrencyName.addComponent(tfCurrencyName);
		flCurrencyStatus.addComponent(cbCurrencyStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(flCurrencyCode);
		hlUserInputLayout.addComponent(flCurrencyName);
		hlUserInputLayout.addComponent(flCurrencyStatus);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID :" + companyId + "| Login User Name : " + strLoginUserName + " > "
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
				+ "Loading Search...");
		List<CurrencyDM> currencyList = new ArrayList<CurrencyDM>();
		logger.info("Company ID :" + companyId + "| Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + "," + tfCurrencyCode.getValue() + ", " + tfCurrencyName.getValue() + ", "
				+ (String) cbCurrencyStatus.getValue() + ", F");
		currencyList = serviceCurrency.getCurrencyList(null, tfCurrencyCode.getValue(), tfCurrencyName.getValue(),
				(String) cbCurrencyStatus.getValue(), "F");
		recordCnt = currencyList.size();
		beanCurrencyDM = new BeanItemContainer<CurrencyDM>(CurrencyDM.class);
		beanCurrencyDM.addAll(currencyList);
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Got the Currency result set");
		tblMstScrSrchRslt.setContainerDataSource(beanCurrencyDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "ccyid", "ccycode", "ccyname", "ccysymbol", "ccystatus",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.ID", "Currency Code", "Currency Name", "Symbol",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("ccyid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID :" + companyId + "| Login User Name : " + strLoginUserName + " > "
				+ "Resetting the UI controls");
		tfCurrencyCode.setValue("");
		tfCurrencyName.setValue("");
		cbCurrencyStatus.setValue(cbCurrencyStatus.getItemIds().iterator().next());
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
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
				+ "Resetting the UI controls");
		// reset the field valued to default
		tfCurrencyCode.setValue("");
		tfCurrencyName.setValue("");
		cbCurrencyStatus.setValue(cbCurrencyStatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		// no functionality is here...!
	}
	
	@Override
	protected void editDetails() {
		// no functionality is here...!
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		// no functionality is here...!
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		// no functionality is here...!
	}
	
	@Override
	protected void showAuditDetails() {
		// no functionality is here...!
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Canceling action ");
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
}
