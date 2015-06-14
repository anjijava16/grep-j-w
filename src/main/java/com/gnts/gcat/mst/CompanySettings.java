/**
 * File Name	:	CompanySettingsApp.java
 * Description	:	Used for view m_gcat_company_settings details
 * Author		:	Prakash.s
 * Date			:	mar 7, 2014
 * Modification 
 * Modified By  :   prakash.s
 * Description	:
 *
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies pvt. ltd.
 * Version         Date           Modified By             Remarks
 * * 0.1           18-Jul-2014         Ganga              Code Optimizing&code re-factoring
 */
 
package com.gnts.gcat.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.gcat.domain.mst.CompanySettingsDM;
import com.gnts.gcat.service.mst.CompanySettingsService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class CompanySettings extends BaseUI {
	CompanySettingsService serviceCompanySettings = (CompanySettingsService) SpringContextHelper
			.getBean("companySettings");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfsettingcode, tfsettingValue;
	private ComboBox cbcompanyStngstatus;
	// Bean Container
	private BeanItemContainer<CompanySettingsDM> beancompanyStngDM = null;
	// Local Variable
	private String username;
	private Long companyid;
	private String compSettingId;
	// Initialize logger
	private Logger logger = Logger.getLogger(CompanySettings.class);
	private int recordCnt;
	//private int recordCn;
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public CompanySettings() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside CompanySettings() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting CompanySettings UI");
		btnAdd.setVisible(false);
		tfsettingcode = new GERPTextField("Setting Code");
		tfsettingValue = new GERPTextField("Setting Value");
		cbcompanyStngstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
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
		flColumn1.addComponent(tfsettingcode);
		flColumn2.addComponent(cbcompanyStngstatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setSizeUndefined();
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setMargin(true);
		// Add components for User Input Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn1.addComponent(tfsettingcode);
		flColumn2.addComponent(tfsettingValue);
		flColumn3.addComponent(cbcompanyStngstatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.setSpacing(true);
	}
	
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<CompanySettingsDM> companyStngList = new ArrayList<CompanySettingsDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search CompanySettings are "
				+ companyid + ", " + tfsettingcode.getValue() + ", " + (String) cbcompanyStngstatus.getValue());
		companyStngList = serviceCompanySettings.getCompanySettingsList(companyid, tfsettingcode.getValue(),
				(String) cbcompanyStngstatus.getValue());
		recordCnt = companyStngList.size();
		beancompanyStngDM = new BeanItemContainer<CompanySettingsDM>(CompanySettingsDM.class);
		beancompanyStngDM.addAll(companyStngList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the CompanySettings. result set");
		tblMstScrSrchRslt.setContainerDataSource(beancompanyStngDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "settinfID", "settingCode", "settingValue", "settingStatus",
				"lastUpdatedDate", "lastUpdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Setting Code", "Setting Value", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("compSettingId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void editCompanyStng() {
		hlUserInputLayout.setVisible(true);
		Item rowSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		// compSettingId = rowSelected.getItemProperty("rowSelected").getValue().toString();
		if (rowSelected != null) {
			CompanySettingsDM editCompStnglist = beancompanyStngDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			tfsettingcode.setValue(editCompStnglist.getSettingCode());
			tfsettingValue.setValue(editCompStnglist.getSettingValue());
			cbcompanyStngstatus.setValue(rowSelected.getItemProperty("settingStatus").getValue());
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfsettingcode.setValue("");
		tfsettingValue.setValue("");
		cbcompanyStngstatus.setValue(cbcompanyStngstatus.getItemIds().iterator().next());
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		hlUserInputLayout.removeAllComponents();
		// reset the field valued to default
		cbcompanyStngstatus.setValue(cbcompanyStngstatus.getItemIds().iterator().next());
		tfsettingcode.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		// TODO Auto-generated method stub
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
		editCompanyStng();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfsettingcode.setComponentError(null);
		if ((tfsettingcode.getValue() == null) || tfsettingcode.getValue().trim().length() == 0) {
			tfsettingcode.setComponentError(new UserError(GERPErrorCodes.NULL_SETTING_CODE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfsettingcode.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		CompanySettingsDM compStngobj = new CompanySettingsDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			compStngobj = beancompanyStngDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		compStngobj.setCompanyId(companyid);
		compStngobj.setSettingCode(tfsettingcode.getValue());
		compStngobj.setSettingValue(tfsettingValue.getValue());
		compStngobj.setSettingStatus((String) cbcompanyStngstatus.getValue());
		compStngobj.setLastUpdatedby(username);
		compStngobj.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceCompanySettings.updateCompanySettings(compStngobj);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for CompanySetting. ID " + compSettingId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_GCAT__COMPANYSETTINGS);
		UI.getCurrent().getSession().setAttribute("audittablepk", compSettingId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
}
