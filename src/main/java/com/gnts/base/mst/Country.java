/**
 * File Name	:	Country.java
 * Description	:	To Handle Country Web page requests.
 * Author		:	Priyanga M
 * Date			:	Feb 27, 2014
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1           27-Feb-2014      Priyanga.M		  Initial version
 * 0.2         	 17-Jun-2014      Nandhakumar.S		  Updated Version  
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.service.mst.CountryService;
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

public class Country extends BaseUI {
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	// form layout for input controls
	private FormLayout flCountryname, flCountrycode, flCountrystatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfCountryname, tfCountrycode;
	private ComboBox cbCountrystatus = new GERPComboBox("Status",BASEConstants.M_GENERIC_TABLE,BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<CountryDM> beanCountryDM = null;
	// local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private static Logger logger = Logger.getLogger(Country.class);
	
	public Country() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Country() constructor");
		// Loading the Country UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Country UI");
		// Country text fields
		tfCountryname = new GERPTextField("Country Name");
		tfCountryname.setMaxLength(25);
		tfCountrycode = new GERPTextField("Country Code");
		tfCountrycode.setMaxLength(4);
		// Base UI add, edit, Audit Records buttons to be invisible
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnAuditRecords.setVisible(false);
		// get the list of statuses for Country status field
		flCountryname = new FormLayout();
		flCountrycode = new FormLayout();
		flCountrystatus = new FormLayout();
		// add the form layouts into user input layout
		flCountryname.addComponent(tfCountryname);
		flCountrycode.addComponent(tfCountrycode);
		flCountrystatus.addComponent(cbCountrystatus);
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(flCountryname);
		hlUserInputLayout.addComponent(flCountrycode);
		hlUserInputLayout.addComponent(flCountrystatus);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	protected void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	protected void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<CountryDM> countryList = new ArrayList<CountryDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " for search parameters are "
				+ companyid + ", " + tfCountryname.getValue() + "," + tfCountrycode + "," + (String) cbCountrystatus.getValue());
		countryList = serviceCountry.getCountryList(null,null, tfCountrycode.getValue(), tfCountryname.getValue(), (String) cbCountrystatus.getValue(),
				"F");
		recordCnt = countryList.size();
		beanCountryDM = new BeanItemContainer<CountryDM>(CountryDM.class);
		beanCountryDM.addAll(countryList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Country. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanCountryDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "countryID", "countryName", "countryCode", "isdcode",
				"primaryLang", "timeZoneDesc", "clockAdjust", "countyStats", "lastupdateddt", "lastUpdateBy" });
		
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Country Name", "Country Code", "ISD Code",
				"Primary Lang.", "Time Zone Desc.", "Clock Adjust", "Status", "Last Updated Date", "Last Updated By" });
		
		tblMstScrSrchRslt.setColumnAlignment("countryID", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdateBy", "No.of Records : " + recordCnt);
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
		}
	}
	
	// BaseUI searchDetails() to the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		// reset the field valued to default
		tfCountryname.setValue("");
		tfCountrycode.setValue("");
		cbCountrystatus.setValue(cbCountrystatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	

	@Override
	protected void addDetails() {
		// No functionality to implement
	}
	
	@Override
	protected void editDetails() {
		// No functionality to implement
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		// No functionality to implement
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		// No functionality to implement
	}
	
	@Override
	protected void showAuditDetails() {
		// No functionality to implement
	}
	
	@Override
	protected void cancelDetails() {
		// No functionality to implement
	}
	
	@Override
	protected void resetFields() {
		// No functionality to implement
		cbCountrystatus.setValue(cbCountrystatus.getItemIds().iterator().next());
	}
}
