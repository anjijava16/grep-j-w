/**
 * File Name	:	Region.java
 * Description	:	this class is used for add/edit Region details. 
 * Author		:	Rajan Babu
 * Date			:	Mar 5, 2014
 * Modification 
 * Modified By  :   Mahaboob Subahan J
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version		Date			Modified By			Remarks
 * 0.1          Mar 05 2014     Rajan Babu          Initial Version.
 * 0.2			Jun 18 2014		Mahaboob Subahan J	Code re-factoring. 
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.RegionDM;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.RegionService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Region extends BaseUI {
	private RegionService serviceRegion = (RegionService) SpringContextHelper.getBean("mregion");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	// form layout for input controls
	private FormLayout flRegionName, flCountryame, flRegionStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfRegionName;
	private ComboBox cbCountry = new GERPComboBox("Country");
	private ComboBox cbRegionStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private BeanItemContainer<RegionDM> beanRegion = null;
	private BeanContainer<Long, CountryDM> beanCountry = null;
	private Long companyid,countryid;
	private String regId;
	private int recordCnt = 0;
	private String username;
	// Initialize the logger
	private Logger logger = Logger.getLogger(Region.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Region() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Region() constructor");
		// Loading the UI
		countryid=(Long)UI.getCurrent().getSession().getAttribute("countryid");
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Region UI");
		// Region Name text field
		tfRegionName = new GERPTextField("Region Name");
		tfRegionName.setMaxLength(25);
		// create form layouts to hold the input items
		flRegionName = new FormLayout();
		flCountryame = new FormLayout();
		flRegionStatus = new FormLayout();
		// add the user input items into appropriate form layout
		flRegionName.addComponent(tfRegionName);
		flCountryame.addComponent(cbCountry);
		flRegionStatus.addComponent(cbRegionStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flRegionName);
		hlUserInputLayout.addComponent(flCountryame);
		hlUserInputLayout.addComponent(flRegionStatus);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		cbCountry.setItemCaptionPropertyId("countryName");
		loadCountryname();
		resetFields();
		loadSrchRslt();
		
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Assembling search layout ");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// load country names
	private void loadCountryname() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<CountryDM> loadCountrylist = serviceCountry.getCountryList(null,null, null, null, "Active", "F");
		loadCountrylist.add(new CountryDM(0L, "All Countries", null));
		beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
		beanCountry.setBeanIdProperty("countryID");
		beanCountry.addAll(loadCountrylist);
		cbCountry.setContainerDataSource(beanCountry);
	}
	
	// load search result to table
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<RegionDM> regionList = new ArrayList<RegionDM>();
		regionList = serviceRegion.getRegionList(tfRegionName.getValue(), (String) cbRegionStatus.getValue(), (Long)cbCountry.getValue(),
				companyid, "F");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfRegionName.getValue() + ", " + cbRegionStatus.getValue() + "," );
		recordCnt = regionList.size();
		beanRegion = new BeanItemContainer<RegionDM>(RegionDM.class);
		beanRegion.addAll(regionList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Region result set");
		tblMstScrSrchRslt.setContainerDataSource(beanRegion);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "regionId", "regionName", "countryname", "status",
				"lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Region", "Country", "Status",
				"Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("regionId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		//tfRegionName.setRequired(false);
		//cbCountry.setRequired(false);
	}
	
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfRegionName.setValue("");
		tfRegionName.setComponentError(null);
		cbCountry.setComponentError(null);
		cbCountry.setValue(countryid);
		cbRegionStatus.setValue(cbRegionStatus.getItemIds().iterator().next());
		//loadSrchRslt();
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editRegion() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		RegionDM editRegionslist = beanRegion.getItem(tblMstScrSrchRslt.getValue()).getBean();
		regId = editRegionslist.getRegionId().toString();
		if (sltedRcd.getItemProperty("regionName") != null && !"null".equals(sltedRcd.getItemProperty("regionName"))) {
			tfRegionName.setValue(sltedRcd.getItemProperty("regionName").getValue().toString());
		}
		/*if (tfRegionName.getValue() != null) {
			tfRegionName.setValue(editRegionslist.getRegionName());
		}*/
		String stcodes = sltedRcd.getItemProperty("status").getValue().toString();
		cbRegionStatus.setValue(stcodes);
		cbCountry.setValue(editRegionslist.getCountryId());
		
	}
	
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
	
	// to show search details
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbRegionStatus.setValue(cbRegionStatus.getItemIds().iterator().next());
		tfRegionName.setValue("");
		cbCountry.setValue(countryid);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	// to add details
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfRegionName.setRequired(true);
		cbCountry.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	// to show audit details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for client cat. ID " + regId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_REGION);
		UI.getCurrent().getSession().setAttribute("audittablepk", regId);
	}
	
	// to cancel details
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfRegionName.setRequired(false);
		cbCountry.setRequired(false);
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();		
	}
	
	// to edit details
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfRegionName.setRequired(true);
		cbCountry.setRequired(true);
		editRegion();
	}
	
	// for validate details
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfRegionName.setComponentError(null);
		cbCountry.setComponentError(null);
		Boolean errorFlag = false;
		if ((tfRegionName.getValue() == null) || tfRegionName.getValue().trim().length() == 0) {
			tfRegionName.setComponentError(new UserError(GERPErrorCodes.NULL_REGION_NAME));
			errorFlag = true;
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfRegionName.getValue());
		}
		if ((Long)cbCountry.getValue()==0L) {
			cbCountry.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_COUNTRY));
			errorFlag = true;
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. Holiday Name is > " + cbCountry.getValue());
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		
		}
	}
	
	// save details
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		RegionDM regionObj = new RegionDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			regionObj = beanRegion.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		regionObj.setCompanyid(companyid);
		regionObj.setRegionName(tfRegionName.getValue().toString());
		if (cbCountry.getValue() != null) {
			regionObj.setCountryId((Long) cbCountry.getValue());
		}
		if (cbRegionStatus.getValue() != null) {
			regionObj.setStatus((String) cbRegionStatus.getValue());
		}
		regionObj.setLastUpdatedDt(DateUtils.getcurrentdate());
		regionObj.setLastUpdatedBy(username);
		serviceRegion.saveOrUpdateRegion(regionObj);
		resetFields();
		loadSrchRslt();
	}
}