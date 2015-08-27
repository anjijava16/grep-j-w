/**
 * File Name	:	Branch.java
 * Description	:	this class is used for add/edit Branch details. 
 * Author		:	Rajan Babu
 * Date			:	Mar 5, 2014
 * Modification 
 * Modified By  :   Rajan Babu
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 * Version          Date           Modified By             Remarks
 *   0.1           Mar 05 2014     Rajan Babu             develop the Branch screen.
 * This software is the confidential and proprietary information
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1          JUN 16 2014        	SUDAKAR	        Initial Version
 * 0.2			05-July-2014		MOHAMED			Code re-factoring
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CityDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.BranchService;
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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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

public class Branch extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8624818597477342436L;
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private CityService serviceCity = (CityService) SpringContextHelper.getBean("city");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfPhoneNo;
	private TextField tfBranchName, tfPostCode, tfEmailId, tfRegNo, tfTanNo, tfStNo;
	private TextArea taBranchAddress;
	private ComboBox cbCountryName, cbStateName, cbCityName, cbStatus;
	private BeanItemContainer<BranchDM> beanBranch = null;
	// local variables declaration
	private Long companyid;
	private String branchId;
	private int recordCnt = 0;
	private String username;
	private Logger logger = Logger.getLogger(Branch.class);
	
	// Constructor
	public Branch() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Department() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Branch UI");
		// Branch Name text field
		tfBranchName = new GERPTextField("Branch Name");
		tfBranchName.setMaxLength(25);
		tfBranchName.setWidth("200");
		// Branch Name text Area
		taBranchAddress = new GERPTextArea("Address");
		taBranchAddress.setHeight("118");
		taBranchAddress.setWidth("200");
		// Branch Name text field
		tfPhoneNo = new GERPTextField("Phone No.");
		tfPhoneNo.setMaxLength(11);
		// tfPhoneNo.addValidator(new PhoneNumberValidation("Enter valid no"));
		// Text field for Branch Email-ID
		tfEmailId = new GERPTextField("Email Id.");
		// Text field for Branch Post-code
		tfPostCode = new GERPTextField("Post Code.");
		tfPostCode.setMaxLength(20);
		// Text field for Branch Reg-no
		tfRegNo = new GERPTextField("Reg No.");
		tfRegNo.setMaxLength(50);
		// Text field for Branch st-no
		tfStNo = new GERPTextField("ST No.");
		// Text field for Branch TAN-no
		tfTanNo = new GERPTextField("TAN No.");
		tfTanNo.setMaxLength(40);
		// Branch status combo box
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Branch status combo box
		cbCountryName = new GERPComboBox("Country Name");
		cbCountryName.setWidth("148");
		cbCountryName.setItemCaptionPropertyId("countryName");
		loadCountryList();
		cbCountryName.addValueChangeListener(new ValueChangeListener() {
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
		cbStateName = new GERPComboBox("State Name");
		cbStateName.setWidth("148");
		cbStateName.setItemCaptionPropertyId("stateName");
		cbCityName = new GERPComboBox("City Name");
		cbCityName.setWidth("148");
		cbCityName.setItemCaptionPropertyId("cityname");
		loadCityList();
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
		flColumn1.addComponent(tfBranchName);
		flColumn2.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// add the form layouts into user input layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		// create form layouts to hold the input items
		hlUserInputLayout.removeAllComponents();
		flColumn1.addComponent(tfBranchName);
		flColumn1.addComponent(taBranchAddress);
		flColumn2.addComponent(cbCountryName);
		flColumn2.addComponent(cbStateName);
		flColumn2.addComponent(cbCityName);
		flColumn2.addComponent(tfPhoneNo);
		flColumn2.addComponent(tfEmailId);
		flColumn3.addComponent(tfPostCode);
		flColumn3.addComponent(tfRegNo);
		flColumn3.addComponent(tfStNo);
		flColumn3.addComponent(tfTanNo);
		flColumn3.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	// load the Country name list details for form
	private void loadCountryList() {
		try {
			BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
			beanCountry.setBeanIdProperty("countryID");
			beanCountry.addAll(serviceCountry.getCountryList(null, null, null, null, "Active", "F"));
			cbCountryName.setContainerDataSource(beanCountry);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.warn("Loading null values in loadCountryList() functions----->>>>>" + e);
		}
	}
	
	// load the State name list details for form
	private void loadStateList() {
		try {
			BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
			beanState.setBeanIdProperty("stateId");
			beanState
					.addAll(serviceState.getStateList(null, "Active", (Long) cbCountryName.getValue(), companyid, "P"));
			cbStateName.setContainerDataSource(beanState);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// load the City name list details for form
	private void loadCityList() {
		try {
			BeanContainer<Long, CityDM> beanCity = new BeanContainer<Long, CityDM>(CityDM.class);
			beanCity.setBeanIdProperty("cityid");
			beanCity.addAll(serviceCity.getCityList(null, null, (Long) cbStateName.getValue(), "Active", companyid, "P"));
			cbCityName.setContainerDataSource(beanCity);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<BranchDM> listBranch = new ArrayList<BranchDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfBranchName.getValue() + ",");
		listBranch = serviceBranch.getBranchList(null, tfBranchName.getValue(), null, (String) cbStatus.getValue()
				.toString(), companyid, "F");
		recordCnt = listBranch.size();
		beanBranch = new BeanItemContainer<BranchDM>(BranchDM.class);
		beanBranch.addAll(listBranch);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Branch. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanBranch);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "branchId", "branchName", "cityName", "stateName",
				"phoneNo", "status", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch", "City", "State", "Phone Number",
				"Status", "Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("branchId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnAlignment("phoneNo", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfBranchName.setValue("");
		tfBranchName.setComponentError(null);
		taBranchAddress.setComponentError(null);
		cbCountryName.setComponentError(null);
		cbStateName.setComponentError(null);
		cbCityName.setComponentError(null);
		taBranchAddress.setValue("");
		cbCountryName.setValue(null);
		cbStateName.setValue(null);
		cbCityName.setValue(null);
		tfPhoneNo.setValue("");
		tfEmailId.setValue("");
		tfPostCode.setValue("");
		tfRegNo.setValue("");
		tfStNo.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be loadSrchRslt into user input fields in the input form
	private void editBranch() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Branch. Id -> "
				+ branchId);
		if (tblMstScrSrchRslt.getValue() != null) {
			BranchDM branchDM = beanBranch.getItem(tblMstScrSrchRslt.getValue()).getBean();
			branchId = branchDM.getBranchId().toString();
			tfBranchName.setValue(branchDM.getBranchName());
			taBranchAddress.setValue(branchDM.getBranchAddress());
			cbCountryName.setValue(Long.valueOf(branchDM.getCountryId()));
			cbStateName.setValue(Long.valueOf(branchDM.getStateId()).toString());
			cbCityName.setValue(Long.valueOf(branchDM.getCityId()).toString());
			tfPhoneNo.setValue(branchDM.getPhoneNo());
			tfEmailId.setValue(branchDM.getEmailId());
			tfPostCode.setValue(branchDM.getPostCode());
			tfRegNo.setValue(branchDM.getRegNo());
			tfStNo.setValue(branchDM.getStNo());
			tfTanNo.setValue(branchDM.getTanNo());
			cbStatus.setValue(cbStatus.getValue());
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
		tfBranchName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		resetFields();
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfBranchName.setRequired(true);
		taBranchAddress.setRequired(true);
		cbCountryName.setRequired(true);
		cbStateName.setRequired(true);
		cbCityName.setRequired(true);
		tfPhoneNo.setRequired(true);
		tfEmailId.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Branch ID " + branchId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_BRANCH);
		UI.getCurrent().getSession().setAttribute("audittablepk", branchId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfBranchName.setRequired(false);
		tfPhoneNo.setRequired(false);
		tfEmailId.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		tfBranchName.setRequired(true);
		taBranchAddress.setRequired(true);
		cbCountryName.setRequired(true);
		cbStateName.setRequired(true);
		cbCityName.setRequired(true);
		tfPhoneNo.setRequired(true);
		tfEmailId.setRequired(true);
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// tfBranchName.setRequired(false);
		editBranch();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Boolean errorFlag = false;
		tfBranchName.setComponentError(null);
		taBranchAddress.setComponentError(null);
		cbCountryName.setComponentError(null);
		cbStateName.setComponentError(null);
		cbCityName.setComponentError(null);
		tfPhoneNo.setComponentError(null);
		tfEmailId.setComponentError(null);
		if ((tfBranchName.getValue() == null) || tfBranchName.getValue().trim().length() == 0) {
			tfBranchName.setComponentError(new UserError(GERPErrorCodes.NULL_BRANCH_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfBranchName.getValue());
			errorFlag = true;
		}
		if (taBranchAddress.getValue() == "" || taBranchAddress.getValue() == null
				|| taBranchAddress.getValue().trim().length() == 0) {
			taBranchAddress.setComponentError(new UserError(GERPErrorCodes.NULL_BADDR));
			errorFlag = true;
		}
		if (cbCountryName.getValue() == null) {
			cbCountryName.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_COUNTRY));
			errorFlag = true;
		}
		if (cbStateName.getValue() == null) {
			cbStateName.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_STATE));
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
		String emailSeq = tfEmailId.getValue().toString();
		if (!emailSeq.contains("@") || !emailSeq.contains(".")) {
			tfEmailId.setComponentError(new UserError(GERPErrorCodes.EMAIL_VALIDATION));
			errorFlag = true;
		}
		if (cbCityName.getValue() == null) {
			cbCityName.setComponentError(new UserError(GERPErrorCodes.NULL_CITY_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			BranchDM branchObj = new BranchDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				branchObj = beanBranch.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			branchObj.setBranchName(tfBranchName.getValue());
			branchObj.setBranchAddress(taBranchAddress.getValue());
			branchObj.setPhoneNo(tfPhoneNo.getValue());
			branchObj.setEmailId(tfEmailId.getValue());
			branchObj.setRegNo(tfRegNo.getValue());
			branchObj.setTanNo(tfTanNo.getValue());
			branchObj.setStNo(tfStNo.getValue());
			branchObj.setTanNo(tfTanNo.getValue());
			branchObj.setPostCode(tfPostCode.getValue());
			branchObj.setStatus((String) cbStatus.getValue());
			branchObj.setCompanyid(companyid);
			if (cbCountryName.getValue() != null) {
				branchObj.setCountryId((Long) cbCountryName.getValue());
			}
			if (cbStateName.getValue() != null) {
				branchObj.setStateId(Long.valueOf(cbStateName.getValue().toString()));
			}
			if (cbCityName.getValue() != null) {
				branchObj.setCityId(Long.valueOf(cbCityName.getValue().toString()));
			}
			if (cbStatus.getValue() != null) {
				branchObj.setStatus(cbStatus.getValue().toString());
			}
			branchObj.setLastUpdatedDt(DateUtils.getcurrentdate());
			branchObj.setLastUpdatedBy(username);
			logger.info(" saveOrUpdateBranch() > " + branchObj);
			serviceBranch.saveOrUpdateBranch(branchObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
