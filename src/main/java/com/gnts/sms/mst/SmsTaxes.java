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
	private SmsTaxesService serviceTaxes = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
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
	private ComboBox cbState, cbCountry, cbStatus, cbTaxCode;
	private PopupDateField dfStartDate, dfEndDate;
	// Bean container
	private BeanItemContainer<SmsTaxesDM> beanTaxDM = null;
	// local variables declaration
	private Long companyid;
	private String taxid;
	private int recordCnt = 0;
	private String username;
	// for initialize logger
	private Logger logger = Logger.getLogger(SmsTaxes.class);
	private String userName;
	private Long moduleId, countryID;
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public SmsTaxes() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		if (countryID != null) {
			countryID = Long.valueOf(UI.getCurrent().getSession().getAttribute("countryid").toString());
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside SmsTaxes() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting SmsTaxes  UI");
		// Tax Code text field
		cbTaxCode = new GERPComboBox("Tax Code");
		cbTaxCode.setItemCaptionPropertyId("lookupname");
		cbTaxCode.setImmediate(true);
		cbTaxCode.setNullSelectionAllowed(false);
		cbTaxCode.setWidth("150");
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
		dfStartDate = new GERPPopupDateField("Start Date");
		dfStartDate.setInputPrompt("Select Date");
		// End Date
		dfEndDate = new GERPPopupDateField("End Date");
		dfEndDate.setInputPrompt("Select Date");
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
		flColumn1.addComponent(cbTaxCode);
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
		flColumn1.addComponent(cbTaxCode);
		flColumn1.addComponent(tfTaxPer);
		flColumn2.addComponent(cbCountry);
		flColumn2.addComponent(cbState);
		flColumn3.addComponent(dfStartDate);
		flColumn3.addComponent(dfEndDate);
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
	private void loadSrchRslt() {
		logger.info("SmsTaxes Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<SmsTaxesDM> listTaxes = new ArrayList<SmsTaxesDM>();
		logger.info("" + "SmsTaxes : Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + cbTaxCode.getValue());
		listTaxes = serviceTaxes.getTaxesSmsList(companyid, null, (String) cbTaxCode.getValue(),
				(String) cbStatus.getValue(), null);
		recordCnt = listTaxes.size();
		beanTaxDM = new BeanItemContainer<SmsTaxesDM>(SmsTaxesDM.class);
		beanTaxDM.addAll(listTaxes);
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
	
	private void loadTaxCode() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
			BeanContainer<Long, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<Long, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
					"SM_TAXCD"));
			cbTaxCode.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
		}
	}
	
	// load the State name list details for form
	private void loadStateList() {
		try {
			BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
			beanState.setBeanIdProperty("stateId");
			beanState.addAll(serviceState.getStateList(null, "Active", (Long) cbCountry.getValue(), null, "P"));
			cbState.setContainerDataSource(beanState);
		}
		catch (Exception e) {
		}
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editSmsTax() {
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			SmsTaxesDM taxesDM = beanTaxDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cbTaxCode.setValue(taxesDM.getTaxcode());
			cbStatus.setValue(taxesDM.getTaxstatus());
			cbCountry.setValue(Long.valueOf(taxesDM.getCountryid()));
			cbState.setValue(Long.valueOf(taxesDM.getStateid()).toString());
			if (taxesDM.getTaxdesc() != null) {
				taDesc.setValue(taxesDM.getTaxdesc());
			}
			if (taxesDM.getTaxprnct() != null) {
				tfTaxPer.setValue(taxesDM.getTaxprnct().toString());
			}
			dfStartDate.setValue(taxesDM.getStartdt());
			dfEndDate.setValue(taxesDM.getEnddt());
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
		cbTaxCode.setValue(null);
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbTaxCode.setRequired(true);
		cbCountry.setRequired(true);
		cbState.setRequired(true);
		tfTaxPer.setRequired(true);
		dfEndDate.setRequired(true);
		dfStartDate.setRequired(true);
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
		cbTaxCode.setRequired(true);
		cbCountry.setRequired(true);
		cbState.setRequired(true);
		tfTaxPer.setRequired(true);
		dfEndDate.setRequired(true);
		dfStartDate.setRequired(true);
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
		if ((cbTaxCode.getValue() == null)) {
			cbTaxCode.setComponentError(new UserError(GERPErrorCodes.NULL_TAX_CODE));
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
		if (dfStartDate.getValue() == null) {
			dfStartDate.setComponentError(new UserError(GERPErrorCodes.START_DATE));
			errorFlag = true;
		}
		if (dfEndDate.getValue() == null) {
			dfEndDate.setComponentError(new UserError(GERPErrorCodes.END_DATE));
			errorFlag = true;
		}
		if ((dfStartDate.getValue() != null) || (dfEndDate.getValue() != null)) {
			if (dfStartDate.getValue().after(dfEndDate.getValue())) {
				dfEndDate.setComponentError(new UserError(GERPErrorCodes.DATE_OUTOFRANGE));
				logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Throwing ValidationException. User data is > " + cbTaxCode.getValue());
				throw new ERPException.ValidationException();
			}
		}
		cbTaxCode.setComponentError(null);
		if (tblMstScrSrchRslt.getValue() == null) {
			if (serviceTaxes.getTaxesSmsListSize(cbTaxCode.getValue().toString(), (Long) cbCountry.getValue(),
					Long.valueOf(cbState.getValue().toString()), dfStartDate.getValue(), dfEndDate.getValue()).size() > 0) {
				cbTaxCode.setComponentError(new UserError("This tax code is already available"));
				errorFlag = true;
			}
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			SmsTaxesDM taxesDM = new SmsTaxesDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				taxesDM = beanTaxDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			taxesDM.setCompanyid(companyid);
			taxesDM.setTaxcode(cbTaxCode.getValue().toString());
			taxesDM.setCountryid((Long) cbCountry.getValue());
			taxesDM.setStateid(Long.valueOf(cbState.getValue().toString()));
			taxesDM.setTaxdesc(taDesc.getValue());
			if (tfTaxPer.getValue() != null && tfTaxPer.getValue().trim().length() > 0) {
				taxesDM.setTaxprnct((Double.valueOf(tfTaxPer.getValue())));
			}
			taxesDM.setStartdt(dfStartDate.getValue());
			taxesDM.setEnddt(dfEndDate.getValue());
			taxesDM.setTaxstatus((String) cbStatus.getValue());
			taxesDM.setLastupdateddt(DateUtils.getcurrentdate());
			taxesDM.setLastupdatedby(username);
			serviceTaxes.saveTaxesSmsDetails(taxesDM);
			btnAdd.setCaption("add");
			resetFields();
			loadSrchRslt();
			btnAdd.setCaption("add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
		cbTaxCode.setRequired(false);
		cbCountry.setRequired(false);
		cbState.setRequired(false);
		tfTaxPer.setRequired(false);
		dfEndDate.setRequired(false);
		dfStartDate.setRequired(false);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbTaxCode.setValue(null);
		cbTaxCode.setComponentError(null);
		tfTaxPer.setValue("0");
		tfTaxPer.setComponentError(null);
		taDesc.setValue("");
		cbStatus.setValue(null);
		cbState.setValue(null);
		cbState.setComponentError(null);
		cbCountry.setValue(null);
		cbCountry.setComponentError(null);
		dfStartDate.setValue(null);
		dfStartDate.setComponentError(null);
		dfEndDate.setValue(null);
		cbTaxCode.setComponentError(null);
		dfEndDate.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbCountry.setValue(cbCountry.getItemIds().iterator().next());
	}
}
