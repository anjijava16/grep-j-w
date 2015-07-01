/**
 * File Name	:	State.java
 * Description	:	this class is used for add/edit State details.
 * Author		:	Rajan Babu
 * Date			:	Mar 5, 2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 * Version          Date           Modified By             		Remarks
 *   0.1          	Mar 5, 2014    	Rajan Babu           	develop the State screen.
 *   0.2			16-Jun-2014		Abdullah H				Optimizing the code for State UI 
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.StateService;
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
import com.gnts.erputil.validations.StringWithSpaceValidation;
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

public class State extends BaseUI {
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	// form layout for input controls
	private FormLayout flStateName, flStateCode, flCountryName, flStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfStateName, tfStateCode;
	private ComboBox cbCountryName, cbStatus;
	// Bean container
	private BeanItemContainer<StateDM> beanStateDM = null;
	// local variables declaration
	private Long companyId;
	private String userName, stateid;
	private int recordCnt = 0;
	// Initialize Logger
	private Logger logger = Logger.getLogger(State.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public State() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Inside State() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Painting State UI");
		// State Name text field
		tfStateName = new GERPTextField("State Name");
		tfStateName.setMaxLength(25);
		tfStateName.addValidator(new StringWithSpaceValidation("Enter characters only"));
		tfStateCode = new TextField("State Code");
		tfStateCode.setVisible(false);
		// Only Four Integer only accept it
		tfStateCode.setMaxLength(4);
		// Country Name Combo Box
		cbCountryName = new GERPComboBox("Country Name");
		cbCountryName.setWidth("215");
		cbCountryName.setItemCaptionPropertyId("countryName");
		loadCountryList();
		cbCountryName.setValue(0L);
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// create form layouts to hold the input items
		flStateName = new FormLayout();
		flStateCode = new FormLayout();
		flCountryName = new FormLayout();
		flStatus = new FormLayout();
		// add the user input items into appropriate form layout
		flStateName.addComponent(tfStateName);
		flStateCode.addComponent(tfStateCode);
		flCountryName.addComponent(cbCountryName);
		flStatus.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flStateName);
		hlUserInputLayout.addComponent(flStateCode);
		hlUserInputLayout.addComponent(flCountryName);
		hlUserInputLayout.addComponent(flStatus);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<StateDM> stateList = new ArrayList<StateDM>();
		Long countryid = null;
		if (cbCountryName.getValue() != null) {
			countryid = ((Long) cbCountryName.getValue());
		}
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyId + ", " + tfStateName.getValue() + ", " + (String) cbStatus.getValue());
		stateList = serviceState.getStateList(tfStateName.getValue(), (String) cbStatus.getValue(), countryid,
				companyId, "F");
		recordCnt = stateList.size();
		beanStateDM = new BeanItemContainer<StateDM>(StateDM.class);
		beanStateDM.addAll(stateList);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Got the State result set");
		tblMstScrSrchRslt.setContainerDataSource(beanStateDM);
		tblMstScrSrchRslt.setColumnAlignment("stateId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No. of Records:" + recordCnt);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "stateId", "stateName", "stateCode", "countryName",
				"stateStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "State", "Code", "Country",
				"Status", "Updated Date", "Updated By" });
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editStateDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Editing the selected record");
		tfStateCode.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			StateDM editstatelist = beanStateDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			if (editstatelist.getStateName() != null) {
				tfStateName.setValue(editstatelist.getStateName());
			}
			if (editstatelist.getStateCode() != null) {
				tfStateCode.setValue(editstatelist.getStateCode());
			}
			cbStatus.setValue(editstatelist.getStateStatus());
			cbCountryName.setValue(editstatelist.getCountryId());
			stateid = editstatelist.getStateId();
		}
	}
	
	private void loadCountryList() {
		try {
			List<CountryDM> getCountrylist = serviceCountry.getCountryList(null,null, null, null, "Active", "P");
			getCountrylist.add(new CountryDM(0L, "All Countries", null));
			BeanContainer<Long, CountryDM> beanCountryDM = new BeanContainer<Long, CountryDM>(CountryDM.class);
			beanCountryDM.setBeanIdProperty("countryID");
			beanCountryDM.addAll(getCountrylist);
			cbCountryName.setContainerDataSource(beanCountryDM);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " Country List is Null");
		}
	}
	
	// Base class implementations
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Resetting the UI controls");
		tfStateName.setValue("");
		tfStateCode.setValue("");
		tfStateName.setComponentError(null);
		tfStateCode.setComponentError(null);
		cbCountryName.setComponentError(null);
		cbCountryName.setValue(0L);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// BaseUI searchDetails() implementation
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfStateName.setComponentError(null);
		tfStateName.setValue("");
		cbCountryName.setComponentError(null);
		
		cbCountryName.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		tfStateCode.setVisible(true);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the
		// same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfStateName.setRequired(true);
		cbCountryName.setRequired(true);
		// reset the input controls to default value
		cbCountryName.removeItem(0L);
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for State ID " + stateid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_STATE);
		UI.getCurrent().getSession().setAttribute("audittablepk", stateid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		tfStateCode.setVisible(false);
		tfStateName.setRequired(false);
		cbCountryName.setRequired(false);
		cbCountryName.removeItem(0L);
		loadCountryList();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		tfStateCode.setVisible(true);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfStateName.setRequired(true);
		cbCountryName.setRequired(true);
		cbCountryName.removeItem(0L);
		editStateDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		tfStateName.setComponentError(null);
		cbCountryName.setComponentError(null);
		if ((tfStateName.getValue() == null) || tfStateName.getValue().trim().length() == 0) {
			tfStateName.setComponentError(new UserError(GERPErrorCodes.NULL_STATE_NAME));
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + tfStateName.getValue());
		}
		if (cbCountryName.getValue()==null || (Long)cbCountryName.getValue()==0L ) {
			cbCountryName.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_COUNTRY));
			errorFlag = true;
			logger.warn("Company ID : " + cbCountryName + " | User Name : " + cbCountryName + " > "
					+ "Throwing ValidationException. Holiday Name is > " + cbCountryName.getValue());
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	
	/*
	 * saveDetails()-->this function is used for save/update the records
	 */
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
		StateDM stateobj = new StateDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			stateobj = beanStateDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		stateobj.setCompanyid(companyId);
		stateobj.setStateName(tfStateName.getValue().toString());
		stateobj.setStateCode(tfStateCode.getValue().toString());
		stateobj.setCountryId((Long) cbCountryName.getValue());
		if (cbStatus.getValue() != null) {
			stateobj.setStateStatus((String) cbStatus.getValue());
		}
		stateobj.setLastUpdatedDt(DateUtils.getcurrentdate());
		stateobj.setLastUpdatedBy(userName);
		serviceState.saveOrUpdateState(stateobj);
		resetFields();
		loadSrchRslt();
	}
}
