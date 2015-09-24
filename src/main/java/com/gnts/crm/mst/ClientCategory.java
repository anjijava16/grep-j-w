/**
 * File Name 		:ClientCategory.java 
 * Description 		: this class is used for add/edit Client Category details. 
 * Author 			: P Sekhar
 * Date 			: Mar 05, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1          JUN 16 2014        	MOHAMED	        Initial Version
 * 0.2			14-Jun-2014			MOHAMED			Code re-factoring
 */
package com.gnts.crm.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.crm.domain.mst.ClientCategoryDM;
import com.gnts.crm.service.mst.ClientCategoryService;
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
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ClientCategory extends BaseUI {
	private ClientCategoryService serviceClientCat = (ClientCategoryService) SpringContextHelper
			.getBean("clientCategory");
	// form layout for input controls
	private FormLayout flClntcatName, flClntcatStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfCategryName;
	private ComboBox cbStatus;
	// Bean Container
	private BeanItemContainer<ClientCategoryDM> beanClentCat = null;
	// local variables declaration
	private Long companyid;
	private String clientCategoryId;
	private int recordCnt = 0;
	private String username;
	// intialize the logger
	private Logger logger = Logger.getLogger(ClientCategory.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public ClientCategory() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ClientCategory() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting ClientCategory UI");
		// ClientCategory Name text field
		tfCategryName = new GERPTextField("Category Name");
		tfCategryName.setRequired(false);
		tfCategryName.setMaxLength(25);
		// ClientCategory status combo box
		// populate the status combo box
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("100");
		// create form layouts to hold the input items
		flClntcatName = new FormLayout();
		flClntcatStatus = new FormLayout();
		// add the user input items into appropriate form layout
		flClntcatName.addComponent(tfCategryName);
		flClntcatStatus.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flClntcatName);
		hlUserInputLayout.addComponent(flClntcatStatus);
		hlUserInputLayout.setMargin(true);
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
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<ClientCategoryDM> listClientCate = new ArrayList<ClientCategoryDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfCategryName.getValue() + ", " + (String) cbStatus.getValue());
			listClientCate = serviceClientCat.getCrmClientCategoryList(companyid, tfCategryName.getValue(),
					(String) cbStatus.getValue(), "F");
			recordCnt = listClientCate.size();
			beanClentCat = new BeanItemContainer<ClientCategoryDM>(ClientCategoryDM.class);
			beanClentCat.addAll(listClientCate);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the ClientCategory. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanClentCat);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "clientCategoryId", "clientCatName", "clientCatStatus",
					"lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Client Category Name", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("clientCategoryId", Align.RIGHT);
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
		tfCategryName.setValue("");
		tfCategryName.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editClientCategory() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			clientCategoryId = sltedRcd.getItemProperty("clientCategoryId").getValue().toString();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected Clientcat. Id -> " + clientCategoryId);
			if (sltedRcd != null) {
				tfCategryName.setValue(sltedRcd.getItemProperty("clientCatName").getValue().toString());
				cbStatus.setValue(sltedRcd.getItemProperty("clientCatStatus").getValue().toString());
			}
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
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfCategryName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the
		// same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfCategryName.setRequired(true);
		tblMstScrSrchRslt.setValue(null);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for client cat. ID " + clientCategoryId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_CRM_CLIENT_CAT);
		UI.getCurrent().getSession().setAttribute("audittablepk", clientCategoryId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfCategryName.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfCategryName.setRequired(true);
		editClientCategory();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfCategryName.setComponentError(null);
		if ((tfCategryName.getValue() == null) || tfCategryName.getValue().trim().length() == 0) {
			tfCategryName.setComponentError(new UserError(GERPErrorCodes.NULL_CLNT_CATGRY_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfCategryName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		ClientCategoryDM clntcatobj = new ClientCategoryDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			clntcatobj = beanClentCat.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		clntcatobj.setCompanyId(companyid);
		clntcatobj.setClientCatName(tfCategryName.getValue().toString());
		if (cbStatus.getValue() != null) {
			clntcatobj.setClientCatStatus(cbStatus.getValue().toString());
		}
		clntcatobj.setLastUpdatedDt(DateUtils.getcurrentdate());
		clntcatobj.setLastUpdatedBy(username);
		serviceClientCat.saveOrUpdateCrmClientCategoryList(clntcatobj);
		resetFields();
		loadSrchRslt();
	}
}
