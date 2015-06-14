/**
 * File Name 		: Leads.java 
 * Description 		: this class is used for add/edit Client Leads details. 
 * Author 			: P Sekhar
 * Date 			: Mar 19, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 of  GNTS Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1          JUN 16 2014        	MOHAMED	        Initial Version
 * 0.2			07-JULY-2014		MOHAMED			Code re-factoring
 * 0.3	        
 */
package com.gnts.crm.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CityDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.CurrencyDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.CityService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.CurrencyService;
import com.gnts.base.service.mst.StateService;
import com.gnts.crm.domain.mst.ClientCategoryDM;
import com.gnts.crm.domain.txn.CampaignDM;
import com.gnts.crm.domain.txn.LeadsDM;
import com.gnts.crm.service.mst.ClientCategoryService;
import com.gnts.crm.service.txn.CampaignService;
import com.gnts.crm.service.txn.LeadsService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
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
import com.gnts.erputil.validations.PhoneNumberValidation;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Leads extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CampaignService serviceCampaign = (CampaignService) SpringContextHelper.getBean("clientCampaign");
	private LeadsService serviceLead = (LeadsService) SpringContextHelper.getBean("clientLeads");
	private ClientCategoryService serviceClientCat = (ClientCategoryService) SpringContextHelper
			.getBean("clientCategory");
	private CompanyLookupService serviceCompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private CurrencyService serviceCurrencey = (CurrencyService) SpringContextHelper.getBean("currency");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	private CityService serviceCity = (CityService) SpringContextHelper.getBean("city");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private VerticalLayout vlCommetTblLayout = new VerticalLayout();
	private VerticalLayout vlDocumentLayout = new VerticalLayout();
	private BeanContainer<String, CompanyLookupDM> beanCompanylookup = null;
	/**
	 * UI Components
	 */
	private TextField tfFirstName, tfLastName, tfDesignation, tfCompanyName, tfNoOfEmp, tfPostalCode, tfWebsite,
			tfEmailId, tfPhoneNo, tfRevenue;
	private TextArea taAddress, taremarks;
	private ComboBox cbCampaign, cbClientCat, cbLeadStatus, cbCity, cbState, cbCountry, cbcurrency, cblead;
	private int recordCnt = 0;
	private BeanContainer<Long, CampaignDM> beanCampaign = null;
	private BeanItemContainer<LeadsDM> beanLead = null;
	private BeanContainer<Long, ClientCategoryDM> beanClientCat = null;
	private BeanContainer<Long, CurrencyDM> beancurency = null;
	// Declare local variables
	private Long employeeId;
	private String username, strWidth = "150px";
	private Long companyid, leadId,moduleId;
	private Long clntLeadId, countryid;
	// intialize the logger
	private Logger logger = Logger.getLogger(LeadsDM.class);
	Comments comment;
	Documents document;
	
	// Constructor
	public Leads() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		//countryid = Long.valueOf(UI.getCurrent().getSession().getAttribute("countryid").toString());
		countryid = (Long) UI.getCurrent().getSession().getAttribute("countryid");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Lead() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Lead UI");
		cbCampaign = new GERPComboBox("Campaign");
		cbCampaign.setItemCaptionPropertyId("campaignname");
		cbCampaign.setWidth(strWidth);
		loadClientCampaigns();
		// FirstName Name text field
		tfFirstName = new GERPTextField("First Name");
		tfFirstName.setMaxLength(25);
		tfFirstName.setRequired(true);
		tfFirstName.addBlurListener(new BlurListener() {
			@Override
			public void blur(BlurEvent event) {
				tfFirstName.setComponentError(null);
				if (tfFirstName.getValue() != null) {
					tfFirstName.setComponentError(null);
				}
			}
		});
		tfLastName = new GERPTextField("Last Name");
		tfLastName.setValue("");
		cblead = new GERPComboBox("LeadSource");
		loadLookUpList();
		// Remarks textArea
		taremarks = new GERPTextArea("Remarks");
		taremarks.setMaxLength(100);
		// Designation text field
		tfDesignation = new GERPTextField("Designation");
		tfDesignation.setMaxLength(30);
		// CompanyName TextField
		tfCompanyName = new GERPTextField("Company Name");
		tfCompanyName.setMaxLength(30);
		tfCompanyName.addBlurListener(new BlurListener() {
			@Override
			public void blur(BlurEvent event) {
				tfCompanyName.setComponentError(null);
				if (tfCompanyName.getValue() != null) {
					tfCompanyName.setComponentError(null);
				}
			}
		});
		tfNoOfEmp = new GERPTextField("No.of.Employess");
		tfNoOfEmp.setMaxLength(8);
		tfNoOfEmp.setWidth(strWidth);
		// Website textField
		tfWebsite = new GERPTextField("Website");
		tfWebsite.setWidth(strWidth);
		// EmailTextField
		tfEmailId = new GERPTextField("Email");
		tfEmailId.setRequired(true);
		tfEmailId.setMaxLength(30);
		tfEmailId.setWidth(strWidth);
		tfEmailId.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfEmailId.setComponentError(null);
				if (tfEmailId.getValue() != null) {
					tfEmailId.setComponentError(null);
				}
			}
		});
		// PhoneNumber TextField
		tfPhoneNo = new GERPTextField("Phone No.");
		tfPhoneNo.setRequired(true);
		tfPhoneNo.setWidth(strWidth);
		tfPhoneNo.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfPhoneNo.setComponentError(null);
				if (tfPhoneNo.getValue() != null) {
					tfPhoneNo.addValidator(new PhoneNumberValidation());
				} else {
					tfPhoneNo.setComponentError(null);
				}
			}
		});
		tfPostalCode = new TextField("Postal Code");
		tfPostalCode.setWidth(strWidth);
		tfRevenue = new TextField("Revenue");
		tfRevenue.setWidth(strWidth);
		// tfRevenue.addValidator(new IntegerValidator("Enter numbers only"));
		taAddress = new GERPTextArea("Address");
		taAddress.setWidth(strWidth);
		taAddress.setHeight("70px");
		taAddress.setRequired(true);
		taAddress.addBlurListener(new BlurListener() {
			@Override
			public void blur(BlurEvent event) {
				taAddress.setComponentError(null);
				if (taAddress.getValue() != null) {
					taAddress.setComponentError(null);
				}
			}
		});
		// Initialization and properties cbLeadStatus
		cbLeadStatus = new GERPComboBox("Status");
		loadLookUpListSts();
		cbLeadStatus.setWidth(strWidth);
		// populate the ClientCategory combo box
		cbClientCat = new GERPComboBox("Client Category");
		cbClientCat.setItemCaptionPropertyId("clientCatName");
		cbClientCat.setWidth(strWidth);
		cbClientCat.setNullSelectionAllowed(false);
		loadClientCategoryList();
		cbCountry = new GERPComboBox("Country");
		cbCountry.setItemCaptionPropertyId("countryName");
		cbCountry.setNullSelectionAllowed(false);
		cbCountry.setWidth(strWidth);
		cbCountry.setImmediate(true);
		loadCountryList();
		cbCountry.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadStateList();
				}
			}
		});
		cbState = new GERPComboBox("State");
		cbState.setItemCaptionPropertyId("stateName");
		cbState.setWidth(strWidth);
		cbState.setNullSelectionAllowed(false);
		cbState.setImmediate(true);
		loadStateList();
		cbCity = new GERPComboBox("City");
		cbCity.setItemCaptionPropertyId("cityname");
		cbCity.setNullSelectionAllowed(false);
		cbCity.setWidth(strWidth);
		loadCityList();
		cbcurrency = new GERPComboBox("Currency Name");
		cbcurrency.setItemCaptionPropertyId("ccyname");
		cbcurrency.setWidth(strWidth);
		loadCurrencyList();
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(tfCompanyName);
		flColumn2.addComponent(cbClientCat);
		flColumn3.addComponent(cbLeadStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlUserInputLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbCampaign);
		flColumn1.addComponent(tfFirstName);
		flColumn1.addComponent(tfLastName);
		flColumn1.addComponent(cbClientCat);
		flColumn1.addComponent(tfDesignation);
		flColumn1.addComponent(tfCompanyName);
		flColumn1.addComponent(tfNoOfEmp);
		flColumn2.addComponent(taAddress);
		flColumn2.addComponent(tfWebsite);
		flColumn2.addComponent(tfEmailId);
		flColumn2.addComponent(tfPhoneNo);
		flColumn3.addComponent(cbCountry);
		flColumn3.addComponent(cbState);
		flColumn3.addComponent(cbCity);
		flColumn3.addComponent(tfPostalCode);
		flColumn3.addComponent(cblead);
		flColumn3.addComponent(tfRevenue);
		flColumn4.addComponent(cbcurrency);
		flColumn4.addComponent(cbClientCat);
		flColumn4.addComponent(taremarks);
		flColumn4.addComponent(cbLeadStatus);
		HorizontalLayout hlInput = new HorizontalLayout();
		hlInput.setMargin(true);
		VerticalLayout hlUserInput = new VerticalLayout();
		hlInput.setWidth("1170");
		hlInput.addComponent(flColumn1);
		hlInput.addComponent(flColumn2);
		hlInput.addComponent(flColumn3);
		hlInput.addComponent(flColumn4);
		hlUserInput.addComponent(GERPPanelGenerator.createPanel(hlInput));
		TabSheet test3 = new TabSheet();
		test3.addTab(vlCommetTblLayout, "Comments");
		test3.addTab(vlDocumentLayout, "Documents");
		test3.setWidth("1195");
		hlUserInput.addComponent(test3);
		hlUserInputLayout.addComponent(hlUserInput);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	/**
	 * this method used to load the Client Campaigns list based on company id
	 */
	private void loadClientCampaigns() {
		try {
			List<CampaignDM> campaignlist = serviceCampaign.getCampaignDetailList(companyid, null, null, null, null,
					null, null, null, "P");
			beanCampaign = new BeanContainer<Long, CampaignDM>(CampaignDM.class);
			beanCampaign.setBeanIdProperty("campaingnId");
			beanCampaign.addAll(campaignlist);
			cbCampaign.setContainerDataSource(beanCampaign);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load client Campaign List" + e);
		}
	}
	
	private void loadCurrencyList() {
		try {
			List<CurrencyDM> getCurrencylist = serviceCurrencey.getCurrencyList(null, null, null, "Active", "P");
			beancurency = new BeanContainer<Long, CurrencyDM>(CurrencyDM.class);
			beancurency.setBeanIdProperty("ccyid");
			beancurency.addAll(getCurrencylist);
			cbcurrency.setContainerDataSource(beancurency);
		}
		catch (Exception e) {
			logger.info("Loading null values in loadCurrencyList() function------>>>>" + e);
		}
	}
	
	private void loadLookUpList() {
		try {
			List<CompanyLookupDM> compLookUpList = serviceCompany.getCompanyLookUpByLookUp(companyid, moduleId,
					"Active", "CM_LEADSRC");
			beanCompanylookup = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanylookup.setBeanIdProperty("lookupname");
			beanCompanylookup.addAll(compLookUpList);
			cblead.setContainerDataSource(beanCompanylookup);
		}
		catch (Exception e) {
			logger.info("load company look up details" + e);
		}
	}
	private void loadLookUpListSts() {
		try {
			List<CompanyLookupDM> compLookUpList = serviceCompany.getCompanyLookUpByLookUp(companyid, moduleId,
					"Active", "CM_LEADSTS");
			beanCompanylookup = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanylookup.setBeanIdProperty("lookupname");
			beanCompanylookup.addAll(compLookUpList);
			cbLeadStatus.setContainerDataSource(beanCompanylookup);
		}
		catch (Exception e) {
			logger.info("load company look up details" + e);
		}
	}
	private void loadClientCategoryList() {
		List<ClientCategoryDM> clntCatList = serviceClientCat.getCrmClientCategoryList(companyid, null, "Active", "p");
		beanClientCat = new BeanContainer<Long, ClientCategoryDM>(ClientCategoryDM.class);
		beanClientCat.setBeanIdProperty("clientCategoryId");
		beanClientCat.addAll(clntCatList);
		cbClientCat.setContainerDataSource(beanClientCat);
	}
	
	private void loadCountryList() {
		try {
			List<CountryDM> getCountrylist = serviceCountry.getCountryList(countryid, null, null, null, "Active", "P");
			getCountrylist.add(new CountryDM(0L, "All Countries", null));
			BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
			beanCountry.setBeanIdProperty("countryID");
			beanCountry.addAll(getCountrylist);
			cbCountry.setContainerDataSource(beanCountry);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.warn("Loading null values in loadCountryList() functions----->>>>>" + e);
		}
	}
	
	// load the state name list details for form
	private void loadStateList() {
		try {
			List<StateDM> getStateList = serviceState.getStateList(null, "Active", (Long) cbCountry.getValue(), null,
					"P");
			BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
			beanState.setBeanIdProperty("stateId");
			beanState.addAll(getStateList);
			cbState.setContainerDataSource(beanState);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	// load the City name list details for form
	private void loadCityList() {
		List<CityDM> getcityList = serviceCity.getCityList(null, null, (Long) cbState.getValue(), "Active", companyid,
				"P");
		BeanContainer<Long, CityDM> beanCity = new BeanContainer<Long, CityDM>(CityDM.class);
		beanCity.setBeanIdProperty("cityid");
		beanCity.addAll(getcityList);
		cbCity.setContainerDataSource(beanCity);
	}
	
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<LeadsDM> LeadList = new ArrayList<LeadsDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfCompanyName.getValue() + ", " + (String) cbLeadStatus.getValue());
		LeadList = serviceLead.getLeadsDetailsList(companyid, null, tfCompanyName.getValue().toString(),
				(String) cbLeadStatus.getValue(), (Long) cbClientCat.getValue(), "F");
		recordCnt = LeadList.size();
		logger.info("Size undefined" + LeadList.size());
		beanLead = new BeanItemContainer<LeadsDM>(LeadsDM.class);
		beanLead.addAll(LeadList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the orgNews. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanLead);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "leadId", "firstName", "companyName", "clientCatname",
				"emailId", "cityName", "phoneNo", "leadStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "First Name", "Company Name", "Client Category",
				"Email Id", "City", "Phone No.", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("leadId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void editLead() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		clntLeadId = (Long) sltedRcd.getItemProperty("leadId").getValue();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected news. Id -> "
				+ leadId);
		if (sltedRcd != null) {
			LeadsDM editleadlist = beanLead.getItem(tblMstScrSrchRslt.getValue()).getBean();
			tfCompanyName.setValue(editleadlist.getCompanyName());
			tfFirstName.setValue(editleadlist.getFirstName());
			taAddress.setValue(editleadlist.getAddress());
			taremarks.setValue(editleadlist.getRemarks());
			tfLastName.setValue(editleadlist.getLastName());
			tfDesignation.setValue(editleadlist.getDesignation());
			if (editleadlist.getNoOfEmployees() != null && !"null".equals(editleadlist.getNoOfEmployees())) {
				tfNoOfEmp.setValue(editleadlist.getNoOfEmployees().toString());
				;
			}
			tfPhoneNo.setValue(editleadlist.getPhoneNo());
			tfPostalCode.setValue(editleadlist.getPostalCode());
			cblead.setValue(editleadlist.getLeadSource());
			tfEmailId.setValue(editleadlist.getEmailId());
			if (editleadlist.getRevenue() != null) {
				tfRevenue.setValue(editleadlist.getRevenue().toString());
			}
			tfWebsite.setValue(editleadlist.getWebsite());
			cbcurrency.setValue(editleadlist.getCcyId());
			cbClientCat.setValue(editleadlist.getClientCatId());
			cbCampaign.setValue(editleadlist.getCompaignId());
			cbCountry.setValue((editleadlist.getCountryId()));
			cbState.setValue(Long.valueOf(editleadlist.getStateId()).toString());
			cbCity.setValue(Long.valueOf(editleadlist.getCityId()).toString());
		}
		comment.loadsrch(true, null, null, null, clntLeadId, null, null);
		document.loadsrcrslt(true, null, null, null, clntLeadId, null, null);
	}
	
	@Override
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
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tfCompanyName.setValue("");
		cbClientCat.setValue(null);
		cbLeadStatus.setValue(null);
		hlCmdBtnLayout.setVisible(true);
		vlSrchRsltContainer.setVisible(true);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tfCompanyName.setValue("");
		comment = new Comments(vlCommetTblLayout, employeeId, null, clntLeadId, null, null, null, null);
		document = new Documents(vlDocumentLayout, null, clntLeadId, null, null, null, null);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent((hlUserInputLayout));
		tblMstScrSrchRslt.setVisible(false);
		tfCompanyName.setRequired(true);
		hlCmdBtnLayout.setVisible(false);
		comment = new Comments(vlCommetTblLayout, employeeId, null, clntLeadId, null, null, null, null);
		document = new Documents(vlDocumentLayout, null, clntLeadId, null, null, null, null);
		editLead();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean erroflag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfCompanyName.setComponentError(null);
		if ((tfCompanyName.getValue() == null) || tfCompanyName.getValue().trim().length() == 0) {
			tfCompanyName.setComponentError(new UserError(GERPErrorCodes.NULL_LEAD_NAME));
			erroflag = true;
		}
		if ((tfEmailId.getValue() == null) || tfEmailId.getValue().trim().length() == 0) {
			tfEmailId.setComponentError(new UserError(GERPErrorCodes.EMAIL_VALIDATION));
			erroflag = true;
		}
		if ((tfFirstName.getValue() == null) || tfFirstName.getValue().trim().length() == 0) {
			tfFirstName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_FIRST_NAME));
			erroflag = true;
		}
		if ((taAddress.getValue() == null) || taAddress.getValue().trim().length() == 0) {
			taAddress.setComponentError(new UserError(GERPErrorCodes.EMPLOYEE_ADDRESS));
			erroflag = true;
		}
		if ((tfPhoneNo.getValue() == null) || tfPhoneNo.getValue().trim().length() == 0) {
			tfPhoneNo.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
			erroflag = true;
		}
		if (erroflag) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfCompanyName.getValue() + tfEmailId.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			LeadsDM Leadobj = new LeadsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				Leadobj = beanLead.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			Leadobj.setCompanyId(companyid);
			Leadobj.setCompaignId((Long) cbCampaign.getValue());
			Leadobj.setFirstName(tfFirstName.getValue());
			Leadobj.setLastName(tfLastName.getValue());
			Leadobj.setDesignation(tfDesignation.getValue());
			Leadobj.setCompanyName(tfCompanyName.getValue());
			Leadobj.setEmailId(tfEmailId.getValue());
			Leadobj.setAddress(Leadobj.getAddress());
			Leadobj.setWebsite(tfWebsite.getValue());
			if (tfNoOfEmp.getValue().toString().trim().length() > 0) {
				Leadobj.setNoOfEmployees(Long.valueOf(tfNoOfEmp.getValue()));
			}
			Leadobj.setRemarks(taremarks.getValue());
			Leadobj.setAddress(taAddress.getValue());
			Leadobj.setPhoneNo(tfPhoneNo.getValue());
			Leadobj.setPostalCode(tfPostalCode.getValue());
			Leadobj.setLeadSource((String)cblead.getValue());
			Leadobj.setPhoneNo(Leadobj.getPhoneNo());
			if (tfRevenue.getValue() != null) {
				Leadobj.setRevenue(Long.valueOf(tfRevenue.getValue()));
			}
			if (cbClientCat.getValue() != null) {
				Leadobj.setClientCatId((Long) (cbClientCat.getValue()));
			}
			if (cbCountry.getValue() != null) {
				Leadobj.setCountryId((Long) cbCountry.getValue());
			}
			if (cbcurrency.getValue() != null) {
				Leadobj.setCcyId((Long) cbcurrency.getValue());
			}
			logger.info(" (Long) cbCountryName.getValue() is > " + cbCountry.getValue());
			if (cbState.getValue() != null) {
				Leadobj.setStateId(Long.valueOf(cbState.getValue().toString()));
			}
			logger.info(" (Long) cbState.getValue() is > " + cbState.getValue());
			if (cbCity.getValue() != null) {
				Leadobj.setCityId(Long.valueOf(cbCity.getValue().toString()));
			}
			if (cbLeadStatus.getValue() != null) {
				Leadobj.setLeadStatus((String) cbLeadStatus.getValue());
			}
			Leadobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			Leadobj.setLastUpdatedBy(username);
			logger.info(" saveOrUpdateLeads() > " + Leadobj);
			serviceLead.saveOrUpdateLeads(Leadobj);
			System.out.println("LEADS->>" + Leadobj.getLeadId());
			comment.saveLeads(Leadobj.getLeadId());
			comment.resetfields();
			document.saveLeads(Leadobj.getLeadId());
			document.ResetFields();
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for client cat. ID " + leadId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_CRM_LEADS);
		UI.getCurrent().getSession().setAttribute("audittablepk", leadId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlCmdBtnLayout.setVisible(true);
		// hlUserIPContainer.removeAllComponents();
		tfCompanyName.setRequired(false);
		assembleSearchLayout();
		resetFields();
		tblMstScrSrchRslt.setVisible(true);
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
		tfFirstName.setValue("");
		tfFirstName.setComponentError(null);
		tfCompanyName.setValue("");
		tfCompanyName.setComponentError(null);
		cblead.setValue(null);
		taremarks.setValue("");
		tfLastName.setValue("");
		tfRevenue.setValue("");
		tfDesignation.setValue("");
		tfDesignation.setComponentError(null);
		tfWebsite.setValue(null);
		tfEmailId.setValue("");
		tfEmailId.setComponentError(null);
		tfNoOfEmp.setValue("");
		tfCompanyName.setValue("");
		taAddress.setValue("");
		taAddress.setComponentError(null);
		tfPhoneNo.setValue("");
		tfPhoneNo.setComponentError(null);
		tfPostalCode.setValue("");
		cbCountry.setValue(null);
		cbState.setValue(null);
		cbCity.setValue(null);
		cbCampaign.setValue(null);
		cbClientCat.setValue(null);
		cbcurrency.setValue(null);
		cbLeadStatus.setValue(null);
		cbCountry.setValue(cbCountry.getItemIds().iterator().next());
	}
}
