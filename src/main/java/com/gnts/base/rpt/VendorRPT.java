/**
 * File Name	:	VendorRPT.java
 * Description	:	entity class for M_BASE_VENDOR table
 * Author		:	JOEL GLINDAN D
 * Date			:	JULY 11 , 2014
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of  GNTS Technologies pvt. ltd.
 * Version       Date           	Modified By              Remarks
 * 0.1           JULY 11 , 2014     JOEL GLINDAN D		     Initial Version
 */
package com.gnts.base.rpt;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CityDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.domain.mst.VendorTypeDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CityService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.StateService;
import com.gnts.base.service.mst.VendorService;
import com.gnts.base.service.mst.VendorTypeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class VendorRPT extends BaseUI {
	// Bean creation
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private VendorTypeService serviceVendorType = (VendorTypeService) SpringContextHelper.getBean("vendorType");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private CityService serviceCity = (CityService) SpringContextHelper.getBean("city");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4,flColumn5;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfVendorName, tfContactName, tfcontactno,
			tfEmail;
	
	private ComboBox cbStatus, cbVendorTypeName, cbBranch, cbCountry, cbState, cbCity;
	// BeanItemContainer
	private BeanItemContainer<VendorDM> beanVendorDM = null;
	// local variables declaration
	private Long companyid;
	private String departId;
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	//private Button btnPrint;
	// Initialize logger
	private Logger logger = Logger.getLogger(VendorRPT.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public VendorRPT() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Vendor() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Vendor UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Vendor Name text field
		tfVendorName = new TextField("Vendor");
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnAuditRecords.setVisible(false);
		cbVendorTypeName = new ComboBox("Vendor Type");
		cbVendorTypeName.setWidth("150");
		cbVendorTypeName.setItemCaptionPropertyId("vendortypename");
		loadVendorTypeList();
		// ContactName text field
		tfContactName = new TextField("Contact Name");
		
		cbBranch = new ComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setNullSelectionAllowed(false);
		loadBranchList();
		// Country text field
		cbCountry = new ComboBox("Country");
		cbCountry.setItemCaptionPropertyId("countryName");
		cbCountry.setNullSelectionAllowed(false);
		loadCountryList();
		// State text field
		cbState = new ComboBox("State");
		cbState.setItemCaptionPropertyId("stateName");
		cbState.setNullSelectionAllowed(false);
		loadStateList();
		// City text field
		cbCity = new ComboBox("City");
		cbCity.setItemCaptionPropertyId("cityname");
		cbCity.setNullSelectionAllowed(false);
		loadCityList();
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		hlCmdBtnLayout.setSpacing(true);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn5 = new FormLayout();	
		flColumn1.addComponent(tfVendorName);
		flColumn2.addComponent(cbBranch);
		flColumn3.addComponent(cbState);
		flColumn4.addComponent(cbCountry);
		flColumn5.addComponent(cbStatus);

		//flColumn5
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.addComponent(flColumn5);
		
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<VendorDM> vendorList = new ArrayList<VendorDM>();
		Long cityId = null;
		Long VendorId = null;
		Long branchId = null;
		Long countryID = null;
		Long stateId = null;
		Long vendorTypeId = null;
		if (cbCity.getValue() != null) {
			cityId = ((Long.valueOf(cbCity.getValue().toString())));
		}
		if (cbVendorTypeName.getValue() != null) {
			vendorTypeId = ((Long.valueOf(cbVendorTypeName.getValue().toString())));
		}
		if (cbCountry.getValue() != null) {
			countryID = ((Long.valueOf(cbCountry.getValue().toString())));
			
		}
		if (cbBranch.getValue() != null) {
			branchId = ((Long.valueOf(cbBranch.getValue().toString())));
		}
		if (cbState.getValue() != null) {
			stateId = ((Long.valueOf(cbState.getValue().toString())));
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfVendorName.getValue() + ", " + tfContactName.getValue()
				+ (String) cbStatus.getValue() + ", " + cityId);
		vendorList = serviceVendor.getVendorList(branchId, VendorId, companyid, tfVendorName.getValue(), vendorTypeId,
				countryID, stateId, tfContactName.getValue(), (String) cbStatus.getValue(), cityId, "F");
		recordCnt = vendorList.size();
		beanVendorDM = new BeanItemContainer<VendorDM>(VendorDM.class);
		beanVendorDM.addAll(vendorList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Vendor. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanVendorDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "vendorId", "vendorName", "branchName","stateName","countryName",
				"vendorstatus","lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Vendor Name", "Branch","State","Country", "Status",
				"Last Updated Date","Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("vendorId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbBranch.setValue(0L);
		cbCountry.setValue(0L);
		cbState.setValue("0");
		cbCity.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		try {
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
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		cbVendorTypeName.setValue(null);
		tfVendorName.setValue("");
		cbBranch.setValue(0L);
		cbCountry.setValue(0L);
		cbState.setValue("0");
		cbCity.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		// reset the field valued to default
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
	}
	
	@Override
	protected void showAuditDetails() {
	}
	
	@Override
	protected void cancelDetails() {
	}
	
	@Override
	protected void editDetails() {
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfVendorName.setComponentError(null);
		tfcontactno.setComponentError(null);
		errorFlag = false;
		if (tfcontactno.getValue().toString() == null) {
			tfcontactno.setComponentError(new UserError(GERPErrorCodes.NULL_PHONE_NUMBER));
			errorFlag = true;
		}
		if ((tfVendorName.getValue() == null) || tfVendorName.getValue().trim().length() == 0) {
			tfVendorName.setComponentError(new UserError(GERPErrorCodes.NULL_VENDOR_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfVendorName.getValue());
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
	
	@Override
	protected void saveDetails() {
	}
	
	private void loadCountryList() {
		List<CountryDM> countryList = new ArrayList<CountryDM>();
		countryList.add(new CountryDM(0L, "All Countries", departId));
		countryList.addAll(serviceCountry.getCountryList(null,null, null, null, "Active", "P"));
		BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
		beanCountry.setBeanIdProperty("countryID");
		beanCountry.addAll(countryList);
		cbCountry.setContainerDataSource(beanCountry);
	}
	
	private void loadStateList() {
		List<StateDM> getStateList = new ArrayList<StateDM>();
		getStateList.add(new StateDM(0L, "All states"));
		getStateList.addAll(serviceState.getStateList(null, null,"Active", null, companyid, "P"));
		BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
		beanState.setBeanIdProperty("stateId");
		beanState.addAll(getStateList);
		cbState.setContainerDataSource(beanState);
	}
	
	private void loadCityList() {
		List<CityDM> list = new ArrayList<CityDM>();
		list.add(new CityDM(0L, "All Cities", departId));
		list.addAll(serviceCity.getCityList(null, null, null, "Active", companyid, "P"));
		BeanContainer<Long, CityDM> beanCity = new BeanContainer<Long, CityDM>(CityDM.class);
		beanCity.setBeanIdProperty("cityid");
		beanCity.addAll(list);
		cbCity.setContainerDataSource(beanCity);
	}
	
	public void loadBranchList() {
		List<BranchDM> list = new ArrayList<BranchDM>();
		list.add(new BranchDM(0L, "All Branches"));
		list.addAll(serviceBranch.getBranchList(null, null, null, null, companyid, "P"));
		BeanContainer<Long, BranchDM> beansbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beansbranch.setBeanIdProperty("branchId");
		beansbranch.addAll(list);
		cbBranch.setContainerDataSource(beansbranch);
	}
	
	public void loadVendorTypeList() {
		List<VendorTypeDM> list = new ArrayList<VendorTypeDM>();
		list.addAll(serviceVendorType.getVendorTypeList(null,null, null, null, companyid));
		BeanContainer<Long, VendorTypeDM> beanVendorType = new BeanContainer<Long, VendorTypeDM>(VendorTypeDM.class);
		beanVendorType.setBeanIdProperty("vendorid");
		beanVendorType.addAll(list);
		cbVendorTypeName.setContainerDataSource(beanVendorType);
	}
}
