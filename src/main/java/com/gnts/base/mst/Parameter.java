/**
 * File Name 		: Parameter.java 
 * Description 		: this class is used for save parameter details. 
 * Author 			: P Sekhar
 * Date 			: Feb 21, 2014
 * Modification 	:
 * Modified By 		: P Sekhar 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 04 2014         Hema		          Intial Version
 * 0.2           23-Jun-2014         Ganga              Code Optimizing&code re-factoring
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ParameterDM;
import com.gnts.base.service.mst.ParameterService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Parameter extends BaseUI {
	private ParameterService serviceParameter = (ParameterService) SpringContextHelper.getBean("parameter");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfParameterRef, tfModulcode, tfParamValue, tfParamDesc;
	private PopupDateField dfParamStartdate, dfParamEndDate;
	private ComboBox cbparameterstatus;
	// Bean Container
	private BeanItemContainer<ParameterDM> beanparameterDM = null;
	// local variables declaration
	private Long companyid;
	private String parameterId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(Parameter.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Parameter() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Parameter() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Parameter UI");
		btnAdd.setVisible(false);
		// Parameter ref text field
		tfParameterRef = new GERPTextField("Parameter Ref");
		// Parameter Module code text field
		tfModulcode = new GERPTextField("Module Code");
		// Parameter Value text field
		tfParamValue = new GERPTextField("Param Value");
		tfParamValue.setMaxLength(25);
		// Parameter Desc text field
		tfParamDesc = new GERPTextField("Param Description");
		// Parameter Start Date text field
		dfParamStartdate = new GERPPopupDateField("Param. Start Date");
		dfParamStartdate.setInputPrompt("Select Date");
		// Parameter End Date text field
		dfParamEndDate = new GERPPopupDateField("Param. End Date");
		dfParamEndDate.setInputPrompt("Select Date");
		// Parameter status combo box
		cbparameterstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
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
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn1.addComponent(tfParameterRef);
		flColumn2.addComponent(cbparameterstatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setMargin(true);
		// Add components for User Input Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		flColumn1.addComponent(tfModulcode);
		flColumn1.addComponent(tfParameterRef);
		flColumn2.addComponent(tfParamDesc);
		flColumn2.addComponent(tfParamValue);
		flColumn3.addComponent(dfParamStartdate);
		flColumn3.addComponent(dfParamEndDate);
		flColumn4.addComponent(cbparameterstatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			List<ParameterDM> list = new ArrayList<ParameterDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfParameterRef.getValue() + ", " + (String) cbparameterstatus.getValue());
			list = serviceParameter.getParameterList(null, null, tfParameterRef.getValue(),
					(String) cbparameterstatus.getValue(), companyid);
			recordCnt = list.size();
			beanparameterDM = new BeanItemContainer<ParameterDM>(ParameterDM.class);
			beanparameterDM.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Parameter. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanparameterDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "paramId", "moduleCode", "paramRef", "paramDesc",
					"paramValue", "paramStatus", "lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Module Code", "Reference", "Description",
					"Value", "Status", "Updated Date", "Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("parameterId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		setReadOnlyFalseFields();
		dfParamStartdate.setValue(null);
		tfModulcode.setValue("");
		tfParameterRef.setValue("");
		tfParamValue.setValue("");
		tfParamDesc.setValue("");
		dfParamEndDate.setValue(null);
		cbparameterstatus.setValue(cbparameterstatus.getItemIds().iterator().next());
		setReadOnlyFalseFields();
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editParameter() {
		try {
			hlUserInputLayout.setVisible(true);
			Item rowSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			if (rowSelected != null) {
				String editableMode = rowSelected.getItemProperty("editYn").getValue().toString();
				parameterId = rowSelected.getItemProperty("paramId").getValue().toString();
				setReadOnlyFalseFields();
				tfParameterRef.setValue(rowSelected.getItemProperty("paramRef").getValue().toString());
				tfParamDesc.setValue(rowSelected.getItemProperty("paramDesc").getValue().toString());
				tfParamValue.setValue(rowSelected.getItemProperty("paramValue").getValue().toString());
				tfModulcode.setValue(rowSelected.getItemProperty("moduleCode").getValue().toString());
				dfParamStartdate.setValue((Date) rowSelected.getItemProperty("paramStDate").getValue());
				dfParamEndDate.setValue((Date) rowSelected.getItemProperty("paramEndDate").getValue());
				cbparameterstatus.setValue(rowSelected.getItemProperty("paramStatus").getValue());
				if (editableMode.equals("N")) {
					setReadOnlyTrueFields();
				} else {
					tfParamDesc.setReadOnly(true);
					tfModulcode.setReadOnly(true);
					cbparameterstatus.setReadOnly(true);
					tfParameterRef.setReadOnly(true);
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void setReadOnlyFalseFields() {
		tfParameterRef.setReadOnly(false);
		tfParamValue.setReadOnly(false);
		dfParamEndDate.setReadOnly(false);
		dfParamStartdate.setReadOnly(false);
		tfParamDesc.setReadOnly(false);
		tfModulcode.setReadOnly(false);
		cbparameterstatus.setReadOnly(false);
	}
	
	private void setReadOnlyTrueFields() {
		tfParameterRef.setReadOnly(true);
		tfParamValue.setReadOnly(true);
		dfParamEndDate.setReadOnly(true);
		dfParamStartdate.setReadOnly(true);
		tfParamDesc.setReadOnly(true);
		tfModulcode.setReadOnly(true);
		cbparameterstatus.setReadOnly(true);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		hlUserInputLayout.removeAllComponents();
		setReadOnlyFalseFields();
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		hlUserInputLayout.removeAllComponents();
		// reset the field valued to default
		cbparameterstatus.setValue(cbparameterstatus.getItemIds().iterator().next());
		tfParameterRef.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		// no functionality to implement
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		// reset the input controls to default value
		resetFields();
		assembleUserInputLayout();
		editParameter();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfParamDesc.setComponentError(null);
		if ((tfParamValue.getValue() == null) || tfParamValue.getValue().trim().length() == 0) {
			tfParamValue.setComponentError(new UserError(GERPErrorCodes.NULL_PARAMETER_VALUE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfParamValue.getValue());
			throw new ERPException.ValidationException();
		}
		if ((dfParamStartdate.getValue() != null) || (dfParamEndDate.getValue() != null)) {
			if (dfParamStartdate.getValue().after(dfParamEndDate.getValue())) {
				dfParamEndDate.setComponentError(new UserError(GERPErrorCodes.DATE_OUTOFRANGE));
				logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Throwing ValidationException. User data is > " + tfParamValue.getValue());
				throw new ERPException.ValidationException();
			}
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Parameter. ID " + parameterId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_PARAMETER);
		UI.getCurrent().getSession().setAttribute("audittablepk", parameterId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		setReadOnlyFalseFields();
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		if (tblMstScrSrchRslt.getValue() != null) {
			ParameterDM paramobj = beanparameterDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			paramobj.setCompanyId(companyid);
			paramobj.setModuleCode(tfModulcode.getValue());
			paramobj.setParamDesc(tfParamDesc.getValue());
			paramobj.setParamRef(tfParameterRef.getValue());
			paramobj.setParamValue(tfParamValue.getValue());
			paramobj.setParamEndDate((Date) dfParamEndDate.getValue());
			paramobj.setParamStDate((Date) dfParamStartdate.getValue());
			paramobj.setLastUpdatedBy(username);
			paramobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			paramobj.setParamStatus((String) cbparameterstatus.getValue());
			serviceParameter.updateDetails(paramobj);
			resetFields();
			loadSrchRslt();
		}
	}
}