/**
 * File Name	:	Company.java
 * Description	:	This Screen Purpose for Modify the Company Details.Add the company details process should be directly added in DB.
 * Author		:	Hohulnath.V
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1			18-Feb-2014		  HokulNath			initial version
 * 0.2          23-Jun-2014		  Nandhakumar.S		Code re-fragment and adding logger
 * 
 */
package com.gnts.base.mst;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CityDM;
import com.gnts.base.domain.mst.CompanyDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.CurrencyDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.CityService;
import com.gnts.base.service.mst.CompanyService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.CurrencyService;
import com.gnts.base.service.mst.StateService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Company extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CompanyService serviceCompany = (CompanyService) SpringContextHelper.getBean("companyBean");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private CityService serviceCity = (CityService) SpringContextHelper.getBean("city");
	private CurrencyService serviceCurrency = (CurrencyService) SpringContextHelper.getBean("currency");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// User Input Components
	private TextField tfCompanyName, tfCompanyCode, tfPostCode, tfPhoneNo, tfEmail, tfRegNo, tfTanNo, tfStNo, tfFaxNo,
			tfWebSite, tfServTaxNo, tfPanNo, tfEmpNo, tfEsiNo, tfPfNo, tfCstNo, tfEccNo, tfTinNo;
	private ComboBox cbStatus, cbCountry, cbState, cbCity, cbCurrency;
	private TextArea tfComapnyAddress;
	// BeanItem container of CompanyDM
	private BeanItemContainer<CompanyDM> beansCompany = null;
	// local variables declaration
	String strCompanyId;
	private String username;
	private Long companyid;
	private int recordCnt;
	// Image control layout
	private HorizontalLayout hlimage = new HorizontalLayout();
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	
	// Initialize logger
	private static Logger logger = Logger.getLogger(Company.class);
	public static boolean filevalue2 = false;
	
	// Constructor received the parameters from Login UI class
	public Company() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Company() constructor");
		// Loading the Company UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Company UI");
		// Text field for Company Name
		tfCompanyName = new GERPTextField("Company Name");
		tfCompanyName.setMaxLength(50);
		// Text field for company Code
		tfCompanyCode = new GERPTextField("Company Code");
		tfCompanyCode.setMaxLength(5);
		tfCompanyCode.setReadOnly(false);
		// Text field for company postal code
		tfPostCode = new GERPTextField("Postal Code");
		tfPostCode.setMaxLength(20);
		// Text Area for Company address
		tfComapnyAddress = new GERPTextArea("Address");
		tfComapnyAddress.setMaxLength(250);
		// Text field for Company phone number
		tfPhoneNo = new GERPTextField("Phone No.");
		// Text field for Company Email-ID
		tfEmail = new GERPTextField("E-mail Id");
		// Text field for Company Registration number
		tfRegNo = new GERPTextField("Reg.No.");
		tfRegNo.setMaxLength(50);
		// Text field for Company Tan number
		tfTanNo = new GERPTextField("TAN No.");
		tfTanNo.setMaxLength(40);
		// Text field for Company ST number
		tfStNo = new GERPTextField("ST No.");
		tfStNo.setMaxLength(100);
		// Text field for Company Fax number
		tfFaxNo = new GERPTextField("FAX No.");
		tfFaxNo.setMaxLength(100);
		// Text field for Company web site
		tfWebSite = new GERPTextField("Web Site");
		tfWebSite.setMaxLength(30);
		// Text field for Company Service tax number
		tfServTaxNo = new GERPTextField("Service Tax No.");
		tfServTaxNo.setMaxLength(30);
		// Text field for Company PAN number
		tfPanNo = new GERPTextField("PAN No.");
		tfPanNo.setMaxLength(30);
		// Text field for Company Employer number
		tfEmpNo = new GERPTextField("Employer No.");
		tfEmpNo.setMaxLength(50);
		// Text field for Company ESI number
		tfEsiNo = new GERPTextField("ESI No.");
		tfEsiNo.setMaxLength(50);
		// Text field for Company PF number
		tfPfNo = new GERPTextField("PF No.");
		tfPfNo.setMaxLength(50);
		// Text field for Company CST number
		tfCstNo = new GERPTextField("CST No.");
		tfCstNo.setMaxLength(100);
		// Text field for Company ECC number
		tfEccNo = new GERPTextField("ECC No.");
		tfEccNo.setMaxLength(30);
		// Text field for Company TIN number
		tfTinNo = new GERPTextField("TIN No.");
		tfTinNo.setMaxLength(30);
		// btnAdd button set to invisible
		btnAdd.setVisible(false);
		// load country name into the cbCountry
		cbCountry = new GERPComboBox("Country");
		cbCountry.setItemCaptionPropertyId("countryName");
		cbCountry.setWidth("160");
		loadCountryList();
		cbCountry.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadStateList();
					loadCurrencyList();
				}
			}
		});
		// Load state name into the cbState
		cbState = new GERPComboBox("State");
		cbState.setItemCaptionPropertyId("stateName");
		cbState.setWidth("150");
		cbState.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadCityList();
				}
			}
		});
		// Load the City Name into the cbCity
		cbCity = new GERPComboBox("City");
		cbCity.setItemCaptionPropertyId("cityname");
		cbCity.setWidth("150");
		// Load the Currency Name
		cbCurrency = new GERPComboBox("Currency");
		cbCurrency.setItemCaptionPropertyId("ccyname");
		cbCurrency.setWidth("150");
		// loadCurrencyList();
		// Load the Company Status
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		hlimage.setCaption("Company Logo");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for Company UI search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		// Adding components into form layouts for Company UI search layout
		flColumn1.addComponent(tfCompanyName);
		flColumn2.addComponent(cbStatus);
		// Adding form layouts into search layout for Company UI search mode
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ " assembleInputUserLayout layout");
		/*
		 * Adding user input layout to the input layout as all the fields in the user input are available in the edit
		 * block. hence the same layout used as is
		 */
		// Set required fields
		cbCountry.setRequired(true);
		cbState.setRequired(true);
		cbCity.setRequired(true);
		cbCurrency.setRequired(true);
		// Removing components from search layout and re-initializing form layouts
		hlSearchLayout.removeAllComponents();
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		// adding components into first column in form layout1
		flColumn1.addComponent(tfCompanyName);
		flColumn1.addComponent(tfCompanyCode);
		flColumn1.addComponent(tfComapnyAddress);
		flColumn1.addComponent(tfPostCode);
		flColumn1.addComponent(tfPhoneNo);
		flColumn1.addComponent(tfRegNo);
		flColumn1.addComponent(tfTanNo);
		// adding components into second column in form layout2
		flColumn2.addComponent(cbCountry);
		flColumn2.addComponent(cbState);
		flColumn2.addComponent(cbCity);
		flColumn2.addComponent(cbCurrency);
		flColumn2.addComponent(tfEmail);
		flColumn2.addComponent(tfEmpNo);
		flColumn2.addComponent(tfWebSite);
		flColumn2.addComponent(tfPfNo);
		flColumn2.addComponent(tfStNo);
		// adding components into third column in form layout3
		flColumn3.addComponent(tfEccNo);
		flColumn3.addComponent(tfCstNo);
		flColumn3.addComponent(tfTinNo);
		flColumn3.addComponent(tfEsiNo);
		flColumn3.addComponent(tfPanNo);
		flColumn3.addComponent(tfServTaxNo);
		flColumn3.addComponent(tfFaxNo);
		flColumn3.addComponent(cbStatus);
		// add image into fourth column in form layout4
		flColumn4.addComponent(hlimage);
		// adding form layouts into user input layouts
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<CompanyDM> companyList = new ArrayList<CompanyDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid);
		companyList = serviceCompany
				.getCompanyList(tfCompanyName.getValue(), cbStatus.getValue().toString(), companyid);
		recordCnt = companyList.size();
		beansCompany = new BeanItemContainer<CompanyDM>(CompanyDM.class);
		beansCompany.addAll(companyList);
		tblMstScrSrchRslt.setContainerDataSource(beansCompany);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "companyid", "companyname", "companycode", "companyaddress",
				"cityName", "stateName", "countryName", "phone","companystatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Company", "Code", "Address",
				"City", "State", "Country", "Phone Number","Status", "Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("companyid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnAlignment("phone", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		tblMstScrSrchRslt.setSelectable(true);
	}
	
	// load the Country name list details for form
	private void loadCountryList() {
		List<CountryDM> countryList = new ArrayList<CountryDM>();
		countryList.addAll(serviceCountry.getCountryList(null, null, null, null,"Active", "P"));
		BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
		beanCountry.setBeanIdProperty("countryID");
		beanCountry.addAll(countryList);
		cbCountry.setContainerDataSource(beanCountry);
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
	
	// load the City name list details for form
	private void loadCityList() {
		List<CityDM> getCityList = new ArrayList<CityDM>();
		getCityList.addAll(serviceCity.getCityList(null, null, Long.valueOf(cbState.getValue().toString()), "Active",
				null, "P"));
		BeanContainer<Long, CityDM> beanCity = new BeanContainer<Long, CityDM>(CityDM.class);
		beanCity.setBeanIdProperty("cityid");
		beanCity.addAll(getCityList);
		cbCity.setContainerDataSource(beanCity);
	}
	
	// load the Currency name list details for form
	private void loadCurrencyList() {
		List<CurrencyDM> getCurrencyList = new ArrayList<CurrencyDM>();
		getCurrencyList.addAll(serviceCurrency.getCurrencyList(null, null, null, "Active", "P"));
		BeanContainer<Long, CurrencyDM> beanCurrency = new BeanContainer<Long, CurrencyDM>(CurrencyDM.class);
		beanCurrency.setBeanIdProperty("ccyid");
		beanCurrency.addAll(getCurrencyList);
		cbCurrency.setContainerDataSource(beanCurrency);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfCompanyName.setReadOnly(false);
		tfCompanyName.setValue("");
		tfComapnyAddress.setValue("");
		tfPhoneNo.setValue("");
		tfEmail.setValue("");
		tfRegNo.setValue("");
		tfTanNo.setValue("");
		tfStNo.setValue("");
		tfFaxNo.setValue("");
		tfWebSite.setValue("");
		tfServTaxNo.setValue("");
		tfPanNo.setValue("");
		tfEmpNo.setValue("");
		tfEsiNo.setValue("");
		tfPfNo.setValue("");
		tfCstNo.setValue("");
		tfEccNo.setValue("");
		tfTinNo.setValue("");
		tfCompanyCode.setReadOnly(false);
		tfCompanyCode.setValue("");
		tfPostCode.setValue("");
		cbStatus.setReadOnly(false);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbCountry.setValue(null);
		cbState.setValue(null);
		cbCity.setValue(null);
		cbCurrency.setValue(null);
		cbCountry.setComponentError(null);
		cbState.setComponentError(null);
		cbCity.setComponentError(null);
		cbCurrency.setComponentError(null);
		tfPhoneNo.setComponentError(null);
		tfEmail.setComponentError(null);
		new UploadUI(hlimage);
		UI.getCurrent().getSession().setAttribute("isFileUploaded",false);

	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	protected void editCompanyDetails() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			CompanyDM editCompany = beansCompany.getItem(tblMstScrSrchRslt.getValue()).getBean();
			strCompanyId = editCompany.getCompanyid().toString();
			
			if(editCompany.getCompanylogo() !=null){
				hlimage.removeAllComponents();
				byte[] myimage = (byte[]) editCompany.getCompanylogo();
				UploadUI uploadObject = new UploadUI(hlimage);
				uploadObject.dispayImage(myimage, editCompany.getCompanyname());
			} else {
				try {
					new UploadUI(hlimage);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			if (itselect.getItemProperty("companyaddress") != null
					&& !"null".equals(itselect.getItemProperty("companyaddress"))) {
				tfComapnyAddress.setValue(itselect.getItemProperty("companyaddress").getValue().toString());
			}
			tfCompanyName.setReadOnly(false);
			tfCompanyName.setValue(itselect.getItemProperty("companyname").getValue().toString());
			tfCompanyName.setReadOnly(true);
			if (editCompany.getCompanycode() != null) {
				tfCompanyCode.setReadOnly(false);
				tfCompanyCode.setValue(itselect.getItemProperty("companycode").getValue().toString());
				tfCompanyCode.setReadOnly(true);
			}
			if (editCompany.getPostcode() != null) {
				tfPostCode.setValue(itselect.getItemProperty("postcode").getValue().toString());
			}
			if (editCompany.getCstno() != null) {
				tfCstNo.setValue(itselect.getItemProperty("cstno").getValue().toString());
			}
			if (editCompany.getEccno() != null) {
				tfEccNo.setValue(itselect.getItemProperty("eccno").getValue().toString());
			}
			if (editCompany.getEmailid() != null) {
				tfEmail.setValue(itselect.getItemProperty("emailid").getValue().toString());
			}
			if (editCompany.getEmployerno() != null) {
				tfEmpNo.setValue(itselect.getItemProperty("employerno").getValue().toString());
			}
			if (editCompany.getEsino() != null) {
				tfEsiNo.setValue(itselect.getItemProperty("esino").getValue().toString());
			}
			if (editCompany.getFaxno() != null) {
				tfFaxNo.setValue(itselect.getItemProperty("faxno").getValue().toString());
			}
			if (editCompany.getPanno() != null) {
				tfPanNo.setValue(itselect.getItemProperty("panno").getValue().toString());
			}
			if (editCompany.getPfno() != null) {
				tfPfNo.setValue(itselect.getItemProperty("pfno").getValue().toString());
			}
			if (editCompany.getPhone() != null) {
				tfPhoneNo.setValue(itselect.getItemProperty("phone").getValue().toString());
			}
			if (editCompany.getEmailid() != null) {
				tfEmail.setValue(itselect.getItemProperty("emailid").getValue().toString());
			}
			if (editCompany.getRegno() != null) {
				tfRegNo.setValue(itselect.getItemProperty("regno").getValue().toString());
			}
			if (editCompany.getServicetaxno() != null) {
				tfServTaxNo.setValue(itselect.getItemProperty("servicetaxno").getValue().toString());
			}
			if (editCompany.getStno() != null) {
				tfStNo.setValue(itselect.getItemProperty("stno").getValue().toString());
			}
			if (editCompany.getTanno() != null) {
				tfTanNo.setValue(itselect.getItemProperty("tanno").getValue().toString());
			}
			if (editCompany.getTinno() != null) {
				tfTinNo.setValue(itselect.getItemProperty("tinno").getValue().toString());
			}
			if (itselect.getItemProperty("website").getValue() != null
					&& !"null".equals(itselect.getItemProperty("website").getValue())) {
				tfWebSite.setValue(itselect.getItemProperty("website").getValue().toString());
			}
			String stCode = itselect.getItemProperty("companystatus").getValue().toString();
			cbStatus.setReadOnly(false);
			cbStatus.setValue(stCode);
			cbStatus.setReadOnly(true);
			cbCountry.setValue(Long.valueOf(editCompany.getCountryid()));
			cbState.setValue(Long.valueOf(editCompany.getStateid()).toString());
			cbCity.setValue(Long.valueOf(editCompany.getCityid()).toString());
			cbCurrency.setValue(Long.valueOf(editCompany.getCityid()));
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tfCompanyName.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Company. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_COMPANY);
		UI.getCurrent().getSession().setAttribute("audittablepk", companyid.toString());
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		hlimage.removeAllComponents();
		cbCountry.setComponentError(null);
		cbState.setComponentError(null);
		cbCity.setComponentError(null);
		cbCurrency.setComponentError(null);
		resetFields();
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editCompanyDetails();
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfCompanyName.setComponentError(null);
		Boolean errorFlag = false;
		if (cbCountry.getValue() == null) {
			cbCountry.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_COUNTRY));
			errorFlag = true;
		}
		if (cbState.getValue() == null) {
			cbState.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_STATE));
			errorFlag = true;
		}
		if (cbCity.getValue() == null) {
			cbCity.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_CITY));
			errorFlag = true;
		}
		if (tfPhoneNo.getValue().toString() == null) {
			tfPhoneNo.setComponentError(new UserError(GERPErrorCodes.NULL_PHONE_NUMBER));
			// errorFlag = true;
		} else if (tfPhoneNo.getValue() != null) {
			if (!tfPhoneNo.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
				tfPhoneNo.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
				errorFlag = true;
			}
		}
		if (cbCurrency.getValue() == null) {
			cbCurrency.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_CURRENCY));
			errorFlag = true;
		}
		String emailseq = tfEmail.getValue().toString();
		if (emailseq.contains("@") && emailseq.contains(".") || emailseq.equals("")) {
			tfEmail.setComponentError(null);
		} else {
			tfEmail.setComponentError(new UserError(GERPErrorCodes.EMAIL_VALIDATION));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Method to save Company details into database
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			CompanyDM companyObj = new CompanyDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				companyObj = beansCompany.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			companyObj.setCompanyname(tfCompanyName.getValue().toString());
			companyObj.setCompanycode(tfCompanyCode.getValue().toString());
			companyObj.setCompanyaddress(tfComapnyAddress.getValue());
			companyObj.setPhone(tfPhoneNo.getValue());
			companyObj.setEmailid(tfEmail.getValue());
			companyObj.setRegno(tfRegNo.getValue());
			companyObj.setTanno(tfTanNo.getValue());
			companyObj.setStno(tfStNo.getValue());
			companyObj.setFaxno(tfFaxNo.getValue());
			companyObj.setWebsite(tfWebSite.getValue());
			companyObj.setServicetaxno(tfServTaxNo.getValue());
			companyObj.setPanno(tfPanNo.getValue());
			companyObj.setEmployerno(tfEmpNo.getValue());
			companyObj.setEsino(tfEsiNo.getValue());
			companyObj.setPfno(tfPfNo.getValue());
			companyObj.setCstno(tfCstNo.getValue());
			companyObj.setEccno(tfEccNo.getValue());
			companyObj.setTinno(tfTinNo.getValue());
			companyObj.setPostcode(tfPostCode.getValue());
			companyObj.setCompanystatus(cbStatus.getValue().toString());
			if (cbCountry.getValue() != null) {
				companyObj.setCountryid((Long) cbCountry.getValue());
			}
			if (cbState.getValue() != null) {
				companyObj.setStateid(Long.valueOf(cbState.getValue().toString()));
			}
			if (cbCity.getValue() != null) {
				companyObj.setCityid(Long.valueOf(cbCity.getValue().toString()));
			}
			if (cbCurrency.getValue() != null) {
				companyObj.setCcyid((Long) (cbCurrency.getValue()));
			}
			if ((Boolean) UI.getCurrent().getSession().getAttribute("isFileUploaded")) {
				try {
					companyObj.setCompanylogo((byte[]) UI.getCurrent().getSession().getAttribute("imagebyte"));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				companyObj.setCompanylogo(null);
			}
			companyObj.setLastupdateddt(DateUtils.getcurrentdate());
			companyObj.setLastupdatedby(username);
			serviceCompany.saveorUpdateCompanyDetails(companyObj);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		resetFields();
		loadSrchRslt();
	}
}
