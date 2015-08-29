/**
 * File Name 		: BankBranch.java 
 * Description 		: this class is used for add/edit Account details. 
 * Author 			: SOUNDAR C 
 * Date 			: Mar 05, 2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 07 2014        SOUNDAR C		        Intial Version
 * 0.2			June 18 2014		Madhu T					code re-factoring
 * 0.2			21-Jul-2014			Abdullah.H				Code Optimization
 */
package com.gnts.fms.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CityDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.CityService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.StateService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.fms.domain.mst.BankBranchDM;
import com.gnts.fms.domain.mst.BankDM;
import com.gnts.fms.service.mst.BankBranchService;
import com.gnts.fms.service.mst.BankService;
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
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class BankBranch extends BaseUI {
	// Bean creation
	private BankBranchService serviceBankBranch = (BankBranchService) SpringContextHelper.getBean("bankbranch");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private CityService serviceCity = (CityService) SpringContextHelper.getBean("city");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	private BankService serviceBank = (BankService) SpringContextHelper.getBean("fmsbank");
	// Form layout for input controls
	private FormLayout formlayout1, formlayout2, formlayout4, formlayout3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfIFSCCode, tfMICRCode, tfAddress1, tfPhoneno, tfEmail;
	private GERPTextArea tfAddress2;
	private ComboBox cbCountry, cbState, cbCity, cbBankName;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	// BeanContainer
	private BeanItemContainer<BankBranchDM> beans = null;
	// Local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	private String bankbrnchid;
	// Initialize logger
	private Logger logger = Logger.getLogger(BankBranch.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public BankBranch() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside BankBranch() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting BankBranch UI");
		// Bank Name TextField
		cbBankName = new GERPComboBox("Bank name");
		cbBankName.setItemCaptionPropertyId("bankname");
		cbBankName.setWidth("160");
		loadBankList();
		// Bank IFSC Code TextField
		tfIFSCCode = new GERPTextField("IFSC Code");
		tfIFSCCode.setWidth("160");
		// Bank MICR Code TextField
		tfMICRCode = new GERPTextField("MICR Code");
		tfMICRCode.setWidth("160");
		// Address TextField
		tfAddress1 = new GERPTextField("Address1");
		tfAddress1.setWidth("160");
		// Address TextField
		tfAddress2 = new GERPTextArea("Address2");
		tfAddress2.setWidth("160");
		tfAddress2.setHeight("50");
		// Country Combobox
		cbCountry = new GERPComboBox("Country Name");
		cbCountry.setWidth("160");
		cbCountry.setItemCaptionPropertyId("countryName");
		loadCountryList();
		cbCountry.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					if (btnSearch.isVisible()) {
						loadStateList();
						cbState.setValue("0");
					} else {
						loadStateList();
					}
				}
			}
		});
		// State Combobox
		cbState = new GERPComboBox("State Name");
		cbState.setWidth("160");
		cbState.setItemCaptionPropertyId("stateName");
		cbState.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadCityList();
				}
			}
		});
		// City Combobox
		cbCity = new GERPComboBox("City Name");
		cbCity.setWidth("160");
		cbCity.setImmediate(true);
		cbCity.setNullSelectionAllowed(false);
		cbCity.setItemCaptionPropertyId("cityname");
		loadCityList();
		// Phone No. TextField
		tfPhoneno = new GERPTextField("Phone No.");
		tfPhoneno.setWidth("160");
		tfPhoneno.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfPhoneno.setComponentError(null);
				if (tfPhoneno.getValue() != null) {
					if (!tfPhoneno.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
						tfPhoneno.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
					} else {
						tfPhoneno.setComponentError(null);
					}
				}
			}
		});
		// EmailId TextField
		tfEmail = new GERPTextField("Email Id");
		tfEmail.setWidth("160");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlSearchLayout.removeAllComponents();
		// Add components for Search Layout
		formlayout1 = new FormLayout();
		formlayout2 = new FormLayout();
		formlayout3 = new FormLayout();
		formlayout1.addComponent(tfIFSCCode);
		formlayout2.addComponent(tfMICRCode);
		formlayout3.addComponent(cbStatus);
		hlSearchLayout.addComponent(formlayout1);
		hlSearchLayout.addComponent(formlayout2);
		hlSearchLayout.addComponent(formlayout3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlSearchLayout.removeAllComponents();
		// Add components for User Input Layout
		formlayout1 = new FormLayout();
		formlayout2 = new FormLayout();
		formlayout3 = new FormLayout();
		formlayout4 = new FormLayout();
		formlayout1.addComponent(cbBankName);
		formlayout1.addComponent(tfIFSCCode);
		formlayout1.addComponent(tfMICRCode);
		formlayout2.addComponent(tfAddress1);
		formlayout2.addComponent(tfAddress2);
		formlayout3.addComponent(cbCountry);
		formlayout3.addComponent(cbState);
		formlayout3.addComponent(cbCity);
		formlayout4.addComponent(tfPhoneno);
		formlayout4.addComponent(tfEmail);
		formlayout4.addComponent(cbStatus);
		hlUserInputLayout.addComponent(formlayout1);
		hlUserInputLayout.addComponent(formlayout2);
		hlUserInputLayout.addComponent(formlayout3);
		hlUserInputLayout.addComponent(formlayout4);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			List<BankBranchDM> listBankBranch = new ArrayList<BankBranchDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfIFSCCode.getValue() + ", " + tfMICRCode.getValue() + ","
					+ (String) cbStatus.getValue());
			listBankBranch = serviceBankBranch.getBankBranchlist(null, tfIFSCCode.getValue(), tfMICRCode.getValue(),
					companyid, (String) cbStatus.getValue(), null, "F");
			recordCnt = listBankBranch.size();
			beans = new BeanItemContainer<BankBranchDM>(BankBranchDM.class);
			beans.addAll(listBankBranch);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the BankBranch. result set");
			tblMstScrSrchRslt.setContainerDataSource(beans);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "bankbrnchid", "ifsccode", "micrcode", "branchstatus",
					"lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "IFSC Code", "MICR Code", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("bankbrnchid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbBankName.setValue(null);
		tfIFSCCode.setValue("");
		tfMICRCode.setValue("");
		tfAddress1.setValue("");
		tfAddress2.setValue("");
		cbCountry.setValue(null);
		cbState.setValue(null);
		cbCity.setValue(null);
		tfEmail.setComponentError(null);
		tfEmail.setValue("");
		tfPhoneno.setComponentError(null);
		tfPhoneno.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfIFSCCode.setComponentError(null);
		tfMICRCode.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editBankBranch() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected BankBranch. Id -> "
				+ bankbrnchid);
		if (tblMstScrSrchRslt.getValue() != null) {
			BankBranchDM bankBranchDM = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
			bankbrnchid = bankBranchDM.getBankbrnchid().toString();
			cbBankName.setValue(bankBranchDM.getBankid());
			tfIFSCCode.setValue(bankBranchDM.getIfsccode());
			tfMICRCode.setValue(bankBranchDM.getMicrcode());
			tfAddress1.setValue(bankBranchDM.getAddress1());
			tfAddress2.setValue(bankBranchDM.getAddress2());
			cbCountry.setValue(bankBranchDM.getCountryid());
			cbState.setValue(bankBranchDM.getStateid().toString());
			cbCity.setValue(bankBranchDM.getCityid().toString());
			tfPhoneno.setValue(bankBranchDM.getPhno().toString());
			tfEmail.setValue(bankBranchDM.getEmailid());
			cbStatus.setValue(bankBranchDM.getBranchstatus());
			cbCountry.setValue(bankBranchDM.getCountryid());
			cbState.setValue(bankBranchDM.getStateid());
			cbCity.setValue(bankBranchDM.getCityid());
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
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbState.setValue(null);
		cbState.setValue("0");
		cbCity.setValue(null);
		cbCity.setValue("0");
		tfIFSCCode.setValue("");
		tfMICRCode.setValue("");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbBankName.setRequired(true);
		cbState.setRequired(true);
		cbCountry.setRequired(true);
		cbCity.setRequired(true);
		tfPhoneno.setRequired(true);
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		tfIFSCCode.setRequired(true);
		tfMICRCode.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for BankBranch. ID " + bankbrnchid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_FMS_BANK_BRANCH);
		UI.getCurrent().getSession().setAttribute("audittablepk", bankbrnchid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfIFSCCode.setRequired(false);
		tfMICRCode.setRequired(false);
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		cbBankName.setRequired(true);
		cbState.setRequired(true);
		cbCountry.setRequired(true);
		cbCity.setRequired(true);
		tfPhoneno.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfIFSCCode.setRequired(true);
		tfMICRCode.setRequired(true);
		editBankBranch();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfIFSCCode.setComponentError(null);
		tfMICRCode.setComponentError(null);
		cbCity.setComponentError(null);
		cbState.setComponentError(null);
		cbCountry.setComponentError(null);
		cbBankName.setComponentError(null);
		tfPhoneno.setComponentError(null);
		Boolean errorFlag = false;
		if (cbBankName.getValue() == null) {
			cbBankName.setComponentError(new UserError(GERPErrorCodes.NULL_BANK_NAME));
			errorFlag = true;
		}
		if ((tfIFSCCode.getValue() == null) || tfIFSCCode.getValue().trim().length() == 0) {
			tfIFSCCode.setComponentError(new UserError(GERPErrorCodes.NULL_BANK_IFSC_CODE));
			errorFlag = true;
		}
		if ((tfPhoneno.getValue() == null) || tfPhoneno.getValue().trim().length() == 0) {
			tfPhoneno.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
			errorFlag = true;
		}
		if ((tfMICRCode.getValue() == null) || tfMICRCode.getValue().trim().length() == 0) {
			tfMICRCode.setComponentError(new UserError(GERPErrorCodes.NULL_BANK_MICR_CODE));
			errorFlag = true;
		}
		if ((cbCity.getValue() == null)) {
			cbCity.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_CITY));
			errorFlag = true;
		}
		if ((cbState.getValue() == null)) {
			cbState.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_STATE));
			errorFlag = true;
		}
		if ((cbCountry.getValue() == null)) {
			cbCountry.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_COUNTRY));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			BankBranchDM bankBranchObj = new BankBranchDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				bankBranchObj = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			bankBranchObj.setBankid(Long.valueOf(cbBankName.getValue().toString()));
			bankBranchObj.setIfsccode(tfIFSCCode.getValue());
			bankBranchObj.setMicrcode(tfMICRCode.getValue());
			bankBranchObj.setAddress1(tfAddress1.getValue());
			bankBranchObj.setAddress2(tfAddress2.getValue());
			bankBranchObj.setCountryid(Long.valueOf(cbCountry.getValue().toString()));
			bankBranchObj.setStateid(Long.valueOf(cbState.getValue().toString()));
			bankBranchObj.setCityid(Long.valueOf(cbCity.getValue().toString()));
			bankBranchObj.setPhno(Long.valueOf(tfPhoneno.getValue()));
			bankBranchObj.setEmailid(tfEmail.getValue());
			bankBranchObj.setCompanyid(companyid);
			if (cbStatus.getValue() != null) {
				bankBranchObj.setBranchstatus((String) cbStatus.getValue());
			}
			bankBranchObj.setLastupdateddt(DateUtils.getcurrentdate());
			bankBranchObj.setLastupdatedby(username);
			serviceBankBranch.saveDetails(bankBranchObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadBankList() {
		try {
			BeanContainer<Long, BankDM> bean = new BeanContainer<Long, BankDM>(BankDM.class);
			bean.setBeanIdProperty("bankid");
			bean.addAll(serviceBank.getBanklist(null, null, null, "Active", "P"));
			cbBankName.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadCountryList() {
		try {
			BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
			beanCountry.setBeanIdProperty("countryID");
			beanCountry.addAll(serviceCountry.getCountryList(null, null, null, null, "Active", "P"));
			cbCountry.setContainerDataSource(beanCountry);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadStateList() {
		try {
			BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
			beanState.setBeanIdProperty("stateId");
			beanState.addAll(serviceState.getStateList(null, "Active", null, companyid, "P"));
			cbState.setContainerDataSource(beanState);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadCityList() {
		try {
			BeanContainer<Long, CityDM> beanCity = new BeanContainer<Long, CityDM>(CityDM.class);
			beanCity.setBeanIdProperty("cityid");
			beanCity.addAll(serviceCity.getCityList(null, null, null, "Active", companyid, "P"));
			cbCity.setContainerDataSource(beanCity);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
