/**  
 * File Name	:	City.java  
 * Description	:	To Handle City Web page requests.     
 * Author		:	Priyanga M  
 * Date			:	March 01, 2014
 * Modification :   UI code optimization 
 * Modified By  :   Sudhakar S
 * Description	:   Optimizing the code for City  UI 
 *
 * Copyright (C) 2014 GNTS Technologies Pvt.ltd.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies Pvt.ltd.
 * 
 * Version         Date               Modified By              Remarks
 * 0.1			   March 01,2014      Priyanga M 		       UI code optimization   
 * 0.2             June  18,2014	  Sudhakar S		       code-Refractment
 * 0.2			   2-July-2014		  Abdullah.H			   Code-Optimization
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CityDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.RegionDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.domain.mst.TimeZoneDM;
import com.gnts.base.service.mst.CityService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.RegionService;
import com.gnts.base.service.mst.StateService;
import com.gnts.base.service.mst.TimeZoneService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class City extends BaseUI {
	private static final long serialVersionUID = 1L;
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	private RegionService serviceRegion = (RegionService) SpringContextHelper.getBean("mregion");
	private CityService serviceCity = (CityService) SpringContextHelper.getBean("city");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private TimeZoneService serviceTimezone = (TimeZoneService) SpringContextHelper.getBean("timezone");
	// form layout for input controls
	private FormLayout flcityname, flstatename, flstatus, fltier;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add Input fields
	private TextField tfcityname, tftier;
	private ComboBox cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private ComboBox cbCountry, cbTimezone, cbstate, cbregion;
	// To add Bean Item Container
	private BeanItemContainer<CityDM> citybean = null;
	// local variables declaration
	private Long companyid, countryid;
	private String cityid, username;
	private int recordCnt = 0;
	// Initialize logger
	private Logger logger = Logger.getLogger(City.class);
	
	// Constructor
	public City() {
		// Get the logged in user name and company id and country id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside City() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting City UI");
		// City Name text field
		tfcityname = new GERPTextField("City Name");
		tfcityname.setMaxLength(25);
		// Tier Name text field
		tftier = new GERPTextField("Tier");
		tftier.setMaxLength(25);
		// populate the state combo boxes
		cbstate = new GERPComboBox("State Name");
		cbstate.setItemCaptionPropertyId("stateName");
		cbstate.setWidth("150px");
		cbregion = new GERPComboBox("Region Name");
		cbregion.setItemCaptionPropertyId("regionName");
		cbregion.setWidth("150px");
		cbCountry = new GERPComboBox("Country Name");
		cbCountry.setItemCaptionPropertyId("countryName");
		cbCountry.setWidth("150px");
		loadCountry();
		cbTimezone = new GERPComboBox("Time Zone ");
		cbTimezone.setItemCaptionPropertyId("timezonedesc");
		cbTimezone.setWidth("150px");
		loadTimezoneCode();
		cbCountry.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					if (btnSearch.isVisible()) {
						loadstateListAll();
						cbstate.setValue("0");
					} else {
						loadstateList();
					}
					loadRegion();
					@SuppressWarnings("unchecked")
					BeanItem<CountryDM> cntry = (BeanItem<CountryDM>) cbCountry.getItem((Long) cbCountry.getValue());
					CountryDM cntryBean = cntry.getBean();
					cbTimezone.setValue(cntryBean.getTimeZoneId());
				}
			}
		});
		// create form layouts to hold the input items
		flcityname = new FormLayout();
		flstatename = new FormLayout();
		flstatus = new FormLayout();
		fltier = new FormLayout();
		// add the user input items into appropriate form layout
		flcityname.addComponent(tfcityname);
		flcityname.addComponent(cbregion);
		fltier.addComponent(tftier);
		fltier.addComponent(cbTimezone);
		flstatus.addComponent(cbstatus);
		flstatename.addComponent(cbCountry);
		flstatename.addComponent(cbstate);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flstatename);
		hlUserInputLayout.addComponent(flcityname);
		hlUserInputLayout.addComponent(fltier);
		hlUserInputLayout.addComponent(flstatus);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadstateList();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			cbCountry.setVisible(false);
			cbTimezone.setVisible(false);
			cbregion.setVisible(false);
			tftier.setVisible(false);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<CityDM> list = new ArrayList<CityDM>();
			Long stateid = null;
			if (cbstate.getValue() != null) {
				stateid = Long.valueOf(cbstate.getValue().toString());
			}
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfcityname.getValue() + ", " + cbstate.getValue() + ","
					+ (String) cbstatus.getValue());
			list = serviceCity.getCityList(null, tfcityname.getValue(), stateid, (String) cbstatus.getValue(),
					companyid, "F");
			recordCnt = list.size();
			citybean = new BeanItemContainer<CityDM>(CityDM.class);
			citybean.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the City. result set");
			tblMstScrSrchRslt.setContainerDataSource(citybean);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "cityid", "cityname", "statename", "status",
					"lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "City", "State", "Status", "UpdatedDate",
					"UpdatedBy" });
			tblMstScrSrchRslt.setColumnAlignment("cityid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editCity() {
		try {
			tfcityname.setRequired(true);
			cbCountry.setVisible(true);
			cbTimezone.setVisible(true);
			cbregion.setVisible(true);
			tftier.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				CityDM cityDM = citybean.getItem(tblMstScrSrchRslt.getValue()).getBean();
				cityid = cityDM.getCityid().toString();
				cbstatus.setValue(cityDM.getStatus());
				cbCountry.setValue(Long.valueOf(cityDM.getCountryid()));
				loadstateList();
				cbstate.removeItem("0");
				cbstate.setValue(Long.valueOf(cityDM.getStateId()).toString());
				tfcityname.setValue(cityDM.getCityname());
				cbregion.setValue(Long.valueOf(cityDM.getRegionId()).toString());
				tftier.setValue(cityDM.getTier());
				cbTimezone.setValue((Long) cityDM.getTimezoneid());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// load the lookupDetails List details for form without
	private void loadstateList() {
		List<StateDM> getStateList = new ArrayList<StateDM>();
		loadaddStatelist(getStateList);
	}
	
	private void loadstateListAll() {
		List<StateDM> getStateList = new ArrayList<StateDM>();
		getStateList.add(new StateDM(0L, "All States"));
		loadaddStatelist(getStateList);
	}
	
	// Load state list for panelmain's combo Box
	private void loadaddStatelist(List<StateDM> getStateList) {
		try {
			getStateList.addAll(serviceState.getStateList(null, (String) cbstatus.getValue(),
					(Long) cbCountry.getValue(), null, "P"));
			BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
			beanState.setBeanIdProperty("stateId");
			beanState.addAll(serviceState.getStateList(null, (String) cbstatus.getValue(), (Long) cbCountry.getValue(),
					null, "P"));
			cbstate.setContainerDataSource(beanState);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Country List
	private void loadCountry() {
		try {
			BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
			beanCountry.setBeanIdProperty("countryID");
			beanCountry
					.addAll(serviceCountry.getCountryList(null, null, null, null, (String) cbstatus.getValue(), "T"));
			cbCountry.setContainerDataSource(beanCountry);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Time Zone
	private void loadTimezoneCode() {
		try {
			BeanContainer<Long, TimeZoneDM> beanTime = new BeanContainer<Long, TimeZoneDM>(TimeZoneDM.class);
			beanTime.setBeanIdProperty("timezoneid");
			beanTime.addAll(serviceTimezone.getTimeZoneList(null, null, "P"));
			cbTimezone.setContainerDataSource(beanTime);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load region list for pnladdedit's combo Box
	private void loadRegion() {
		try {
			logger.info("Region  domain  --->" + (String) cbstatus.getValue());
			BeanContainer<Long, RegionDM> beanRegion = new BeanContainer<Long, RegionDM>(RegionDM.class);
			beanRegion.setBeanIdProperty("regionId");
			beanRegion.addAll(serviceRegion.getRegionList(null, (String) cbstatus.getValue(), null, companyid, "P"));
			cbregion.setContainerDataSource(beanRegion);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfcityname.setValue("");
		tfcityname.setComponentError(null);
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		cbstate.setComponentError(null);
		cbTimezone.setValue(null);
		cbregion.setValue(null);
		tftier.setValue(null);
		cbCountry.setValue(null);
		cbCountry.setValue(countryid);
		cbstate.setValue(null);
	}
	
	// BaseUI searchDetails() to the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		tfcityname.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		tftier.setValue("");
		cbstate.setValue(null);
		cbregion.setValue(null);
		cbCountry.setValue(countryid);
		cbstate.setValue("0");
		cbTimezone.setValue(null);
		tftier.setValue("");
		loadSrchRslt();
	}
	
	// BaseUI addDetails() to the field values to default values
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbregion.setVisible(true);
		tfcityname.setRequired(true);
		cbstate.setRequired(true);
		tftier.setVisible(true);
		cbCountry.setVisible(true);
		cbTimezone.setVisible(true);
		// reset the input controls to default value
		resetFields();
		cbstate.setContainerDataSource(null);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for City.ID " + companyid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_CITY);
		UI.getCurrent().getSession().setAttribute("audittablepk", cityid);
	}
	
	@Override
	protected void cancelDetails() {
		cbstate.setValue(Long.valueOf(0L).toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbCountry.setVisible(false);
		cbTimezone.setVisible(false);
		cbregion.setVisible(false);
		tfcityname.setRequired(false);
		cbstate.setRequired(false);
		tftier.setVisible(false);
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadstateList();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfcityname.setRequired(true);
		editCity();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfcityname.setComponentError(null);
		cbstate.setComponentError(null);
		Boolean errorFlag = false;
		if ((tfcityname.getValue() == null) || tfcityname.getValue().trim().length() == 0) {
			tfcityname.setComponentError(new UserError(GERPErrorCodes.NULL_CITY_NAME));
			errorFlag = true;
		}
		if ((cbstate.getValue() == null)) {
			cbstate.setComponentError(new UserError(GERPErrorCodes.NULL_STATE_NAMECB));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + tfcityname.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		CityDM cityDM = new CityDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			cityDM = citybean.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		cityDM.setCompanyid(companyid);
		cityDM.setCityname(tfcityname.getValue().toString());
		if (cbstatus.getValue() != null) {
			cityDM.setStatus((String) cbstatus.getValue());
		}
		if (tftier.getValue() != "") {
			cityDM.setTier(tftier.getValue());
		}
		cityDM.setStateId(Long.valueOf(cbstate.getValue().toString()));
		cityDM.setCountryid(Long.valueOf(cbCountry.getValue().toString()));
		cityDM.setTimezoneid(Long.valueOf(cbTimezone.getValue().toString()));
		if (cbregion.getValue() != null) {
			cityDM.setRegionId(Long.valueOf(cbregion.getValue().toString()));
		}
		cityDM.setLastupdateddt(DateUtils.getcurrentdate());
		cityDM.setLastupdatedby(username);
		serviceCity.saveAndUpdateCitydetails(cityDM);
		resetFields();
		loadSrchRslt();
	}
}
