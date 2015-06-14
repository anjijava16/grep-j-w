/**
 * File Name 		: SmsTaxes.java 
 * Description 		: this class is used for add/edit SmsTaxes details. 
 * Author 			: Ganga 
 * Date 			: Aug 06, 2014

 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 */
package com.gnts.sms.mst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.StateService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.sms.domain.mst.SmsTaxesDM;
import com.gnts.sms.service.mst.SmsTaxesService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class SmsTaxes extends BaseUI {
	private SmsTaxesService serviceTaxesSms = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfTaxPer;
	private TextArea taDesc;
	private ComboBox cbState, cbCountry, cbStatus, cbtaxCode;
	private PopupDateField dfstartDate, dfendDate;
	// Bean container
	private BeanItemContainer<SmsTaxesDM> beanTaxDM = null;
	private BeanContainer<Long, CompanyLookupDM> beanCompanyLookUp = null;
	// local variables declaration
	private Long companyid;
	private String taxid;
	private int recordCnt = 0;
	private String username;
	public static boolean filevalue1 = false;
	// for initialize logger
	private Logger logger = Logger.getLogger(SmsTaxes.class);
	private String userName;
	Long stateId;
	private Long moduleId, countryID;
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public SmsTaxes() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		if(countryID!=null){
		countryID = Long.valueOf(UI.getCurrent().getSession().getAttribute("countryid").toString());}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside SmsTaxes() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting SmsTaxes  UI");
		// Tax Code text field
		cbtaxCode = new GERPComboBox("Tax Code");
		cbtaxCode.setItemCaptionPropertyId("lookupname");
		cbtaxCode.setImmediate(true);
		cbtaxCode.setNullSelectionAllowed(false);
		cbtaxCode.setWidth("150");
		loadTaxCode();
		// Tax Percentage text field
		tfTaxPer = new GERPTextField("Tax Percentage");
		tfTaxPer.setValue("0");
		// ComboBox for Country
		cbCountry = new GERPComboBox("Country Name");
		cbCountry.setItemCaptionPropertyId("countryName");
		loadCountryList();
		cbCountry.setWidth("150");
		cbCountry.setValue(0L);
		cbCountry.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadStateList();
				}
			}
		});
		// ComboBox for State
		cbState = new GERPComboBox("State Name");
		cbState.setItemCaptionPropertyId("stateName");
		cbState.setWidth("150");
		loadStateList();
		cbState.setValue(0L);
		// Combo Box Status
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// TextArea for Description
		taDesc = new TextArea("Tax Description");
		taDesc.setHeight("40");
		taDesc.setWidth("150");
		// Start Date
		dfstartDate = new GERPPopupDateField("Start Date");
		dfstartDate.setInputPrompt("Select Date");
		// End Date
		dfendDate = new GERPPopupDateField("End Date");
		dfendDate.setInputPrompt("Select Date");
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
		flColumn1.addComponent(cbtaxCode);
		flColumn2.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn5 = new FormLayout();
		flColumn1.addComponent(cbtaxCode);
		flColumn1.addComponent(tfTaxPer);
		flColumn2.addComponent(cbCountry);
		flColumn2.addComponent(cbState);
		flColumn3.addComponent(dfstartDate);
		flColumn3.addComponent(dfendDate);
		Label lbl = new Label("");
		flColumn3.addComponent(lbl);
		flColumn4.addComponent(taDesc);
		flColumn5.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.addComponent(flColumn5);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("SmsTaxes Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<SmsTaxesDM> SmsTaxesList = new ArrayList<SmsTaxesDM>();
		logger.info("" + "SmsTaxes : Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + cbtaxCode.getValue());
		SmsTaxesList = serviceTaxesSms.getTaxesSmsList(companyid, null, (String) cbtaxCode.getValue(),
				(String) cbStatus.getValue(), null);
		recordCnt = SmsTaxesList.size();
		beanTaxDM = new BeanItemContainer<SmsTaxesDM>(SmsTaxesDM.class);
		beanTaxDM.addAll(SmsTaxesList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the SmsTaxes. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanTaxDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "taxid", "taxcode", "taxprnct", "taxstatus",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Tax Code", "Tax(%)", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("taxid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadCountryList() {
		try {
			List<CountryDM> getCountrylist = serviceCountry.getCountryList(countryID, null, null, null, "Active", "P");
			getCountrylist.add(new CountryDM(0L, "All Countries", null));
			BeanContainer<Long, CountryDM> beanCountryDM = new BeanContainer<Long, CountryDM>(CountryDM.class);
			beanCountryDM.setBeanIdProperty("countryID");
			beanCountryDM.addAll(getCountrylist);
			cbCountry.setContainerDataSource(beanCountryDM);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + userName + " Country List is Null");
		}
	}
	
	public void loadTaxCode() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
				"SM_TAXCD");
		beanCompanyLookUp = new BeanContainer<Long, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbtaxCode.setContainerDataSource(beanCompanyLookUp);
	}
	
	// load the State name list details for form
	private void loadStateList() {
		List<StateDM> getStateList = new ArrayList<StateDM>();
		getStateList.addAll(serviceState.getStateList(null, "Active", (Long) cbCountry.getValue(), null, "P"));
		BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
		beanState.setBeanIdProperty("stateId");
		beanState.addAll(getStateList);
		cbState.setContainerDataSource(beanState);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editSmsTax() {
		hlUserInputLayout.setVisible(true);
		Item rowSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (rowSelected != null) {
			SmsTaxesDM editSmsTaxList = beanTaxDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cbtaxCode.setValue(rowSelected.getItemProperty("taxcode").getValue().toString());
			String stCode = rowSelected.getItemProperty("taxstatus").getValue().toString();
			cbStatus.setValue(stCode);
			cbCountry.setValue(Long.valueOf(editSmsTaxList.getCountryid()));
			cbState.setValue(Long.valueOf(editSmsTaxList.getStateid()).toString());
			if ((rowSelected.getItemProperty("taxdesc").getValue() != null)) {
				taDesc.setValue(rowSelected.getItemProperty("taxdesc").getValue().toString());
			}
			if ((rowSelected.getItemProperty("taxprnct").getValue() != null)) {
				tfTaxPer.setValue(rowSelected.getItemProperty("taxprnct").getValue().toString());
			}
			dfstartDate.setValue((Date) rowSelected.getItemProperty("startdt").getValue());
			dfendDate.setValue((Date) rowSelected.getItemProperty("enddt").getValue());
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbtaxCode.setValue(null);
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbtaxCode.setRequired(true);
		cbCountry.setRequired(true);
		cbState.setRequired(true);
		tfTaxPer.setRequired(true);
		dfendDate.setRequired(true);
		dfstartDate.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbtaxCode.setRequired(true);
		cbCountry.setRequired(true);
		cbState.setRequired(true);
		tfTaxPer.setRequired(true);
		dfendDate.setRequired(true);
		dfstartDate.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		// reset the input controls to default value
		resetFields();
		assembleUserInputLayout();
		editSmsTax();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if ((cbtaxCode.getValue() == null)) {
			cbtaxCode.setComponentError(new UserError(GERPErrorCodes.NULL_TAX_CODE));
			errorFlag = true;
		}
		if ((cbCountry.getValue() == null)) {
			cbCountry.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_COUNTRY));
			errorFlag = true;
		}
		if (cbState.getValue() == null) {
			cbState.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_STATE));
			errorFlag = true;
		}
		
		if (tfTaxPer.getValue() == "0") {
			tfTaxPer.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_STATE));
			errorFlag = true;
		}
		if (dfstartDate.getValue() == null) {
			dfstartDate.setComponentError(new UserError(GERPErrorCodes.START_DATE));
			errorFlag = true;
		}
		
		if (dfendDate.getValue() == null) {
			dfendDate.setComponentError(new UserError(GERPErrorCodes.END_DATE));
			errorFlag = true;
		}
		
		if ((dfstartDate.getValue() != null) || (dfendDate.getValue() != null)) {
			if (dfstartDate.getValue().after(dfendDate.getValue())) {
				dfendDate.setComponentError(new UserError(GERPErrorCodes.DATE_OUTOFRANGE));
				logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Throwing ValidationException. User data is > " + cbtaxCode.getValue());
				throw new ERPException.ValidationException();
			}
		}
		cbtaxCode.setComponentError(null);
		if(tblMstScrSrchRslt.getValue()==null){
		if (serviceTaxesSms.getTaxesSmsListSize(cbtaxCode.getValue().toString(), (Long) cbCountry.getValue(),
				Long.valueOf(cbState.getValue().toString()), dfstartDate.getValue(), dfendDate.getValue()).size()> 0) {
			cbtaxCode.setComponentError(new UserError("This tax code is already available"));
			errorFlag = true;
		}
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	@Override
	protected void saveDetails() {
		/*
		 * List<SmsTaxesDM> siz=serviceTaxesSms.getTaxesSmsListSize(cbtaxCode.getValue().toString(),
		 * (Long)cbCountry.getValue(),Long.valueOf( cbState.getValue().toString()), dfstartDate.getValue(),
		 * dfendDate.getValue()); int val=siz.size(); if(val==0){
		 */
		try{
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		SmsTaxesDM smsTaxobj = new SmsTaxesDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			smsTaxobj = beanTaxDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		smsTaxobj.setCompanyid(companyid);
		smsTaxobj.setTaxcode(cbtaxCode.getValue().toString());
		smsTaxobj.setCountryid((Long) cbCountry.getValue());
		smsTaxobj.setStateid(Long.valueOf(cbState.getValue().toString()));
		smsTaxobj.setTaxdesc(taDesc.getValue());
		if (tfTaxPer.getValue() != null && tfTaxPer.getValue().trim().length() > 0) {
			smsTaxobj.setTaxprnct((Double.valueOf(tfTaxPer.getValue())));
		}
		smsTaxobj.setStartdt(dfstartDate.getValue());
		smsTaxobj.setEnddt(dfendDate.getValue());
		smsTaxobj.setTaxstatus((String) cbStatus.getValue());
		smsTaxobj.setLastupdateddt(DateUtils.getcurrentdate());
		smsTaxobj.setLastupdatedby(username);
		serviceTaxesSms.saveTaxesSmsDetails(smsTaxobj);
		btnAdd.setCaption("add");
		resetFields();
		loadSrchRslt();
		btnAdd.setCaption("add");
		}catch(Exception e){
			e.printStackTrace();
		}
		// }
		/*
		 * else{ cbtaxCode.setComponentError(new UserError("This tax code is already available")); }
		 */
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for SmsTaxes. ID " + taxid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_PRODUCT_CATEGORY);
		UI.getCurrent().getSession().setAttribute("audittablepk", taxid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbtaxCode.setRequired(false);
		cbCountry.setRequired(false);
		cbState.setRequired(false);
		tfTaxPer.setRequired(false);
		dfendDate.setRequired(false);
		dfstartDate.setRequired(false);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbtaxCode.setValue(null);
		cbtaxCode.setComponentError(null);
		tfTaxPer.setValue("0");
		tfTaxPer.setComponentError(null);
		taDesc.setValue("");
		cbStatus.setValue(null);
		cbState.setValue(null);
		cbState.setComponentError(null);
		cbCountry.setValue(null);
		cbCountry.setComponentError(null);
		dfstartDate.setValue(null);
		dfstartDate.setComponentError(null);
		dfendDate.setValue(null);
		cbtaxCode.setComponentError(null);
		dfendDate.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbCountry.setValue(cbCountry.getItemIds().iterator().next());
	}
}
