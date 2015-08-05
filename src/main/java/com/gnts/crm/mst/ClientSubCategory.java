/**
 * File Name 		: ClientSubCategory.java 
 * Description 		: this class is used for add/edit Client sub Category details. 
 * Author 			: P Sekhar
 * Date 			: Mar 05, 2014
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * * Version       Date           	Modified By               Remarks
 *  * 0.1	     17-Jun-2014		  Ganga		          optimizing code for Client subcategory
 * 
 *  *0.3		 10-JUL-2014		MOHAMED					code-re-factor
 */
package com.gnts.crm.mst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.crm.domain.mst.ClientCategoryDM;
import com.gnts.crm.domain.mst.ClientSubCategoryDM;
import com.gnts.crm.service.mst.ClientCategoryService;
import com.gnts.crm.service.mst.ClientSubCategoryService;
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

public class ClientSubCategory extends BaseUI {
	private ClientSubCategoryService serviceClientSubCategory = (ClientSubCategoryService) SpringContextHelper
			.getBean("clientSubCategory");
	private ClientCategoryService serviceClientCat = (ClientCategoryService) SpringContextHelper.getBean("clientCategory");
	// form layout for input controls
	private FormLayout flSubClntcatName, flClntcatName, flClntSubcatStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfSubCateName;
	private ComboBox cbClntSubcatStatus;
	private ComboBox cbClientCat;
	// Bean Container
	private BeanItemContainer<ClientSubCategoryDM> beanSubCategoryDM = null;
	// local variables declaration
	private Long companyid;
	private Object clientCategoryId;
	private int recordCnt = 0;
	private String username;
	// Initialize the logger
	private Logger logger = Logger.getLogger(ClientSubCategory.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public ClientSubCategory() {
		// Get the logged in user name and company id from the session
		username = (String) UI.getCurrent().getSession().getAttribute("loginUserName");
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ClientSub() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting ClientSubCategory UI");
		// SubCategory Name text field
		tfSubCateName = new GERPTextField("Client Subcategory");
		tfSubCateName.setMaxLength(25);
		// SubCategory status combo box
		cbClntSubcatStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// get the list of statuses for SubCategory status field
		cbClientCat = new GERPComboBox("Client Category");
		cbClientCat.setItemCaptionPropertyId("clientCatName");
		cbClientCat.setNullSelectionAllowed(false);
		cbClientCat.setWidth("160px");
		loadClientCategoryList();
		// create form layouts to hold the input items
		flSubClntcatName = new FormLayout();
		flClntcatName = new FormLayout();
		flClntSubcatStatus = new FormLayout();
		// add the user input items into appropriate form layout
		flSubClntcatName.addComponent(tfSubCateName);
		flClntcatName.addComponent(cbClientCat);
		flClntSubcatStatus.addComponent(cbClntSubcatStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flSubClntcatName);
		hlUserInputLayout.addComponent(flClntcatName);
		hlUserInputLayout.addComponent(flClntSubcatStatus);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		loadClientCategoryList();
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
	
	private void loadClientCategoryList() {
		try {
			BeanContainer<Long, ClientCategoryDM> beanClientCategoryDM = new BeanContainer<Long, ClientCategoryDM>(
					ClientCategoryDM.class);
			beanClientCategoryDM.setBeanIdProperty("clientCategoryId");
			beanClientCategoryDM.addAll(serviceClientCat.getCrmClientCategoryList(companyid, null,
					(String) cbClntSubcatStatus.getValue(), "F"));
			cbClientCat.setContainerDataSource(beanClientCategoryDM);
		}
		catch (Exception e) {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Exception "
					+ e.getMessage());
		}
	}
	
	// get the search result from DB based on the search parameter
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<ClientSubCategoryDM> subCatList = new ArrayList<ClientSubCategoryDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfSubCateName.getValue() + ", " + (String) cbClntSubcatStatus.getValue());
		subCatList = serviceClientSubCategory.getClientSubCategoryList(companyid, null, tfSubCateName.getValue(),
				(String) cbClntSubcatStatus.getValue(), (Long) cbClientCat.getValue(), "P");
		recordCnt = subCatList.size();
		beanSubCategoryDM = new BeanItemContainer<ClientSubCategoryDM>(ClientSubCategoryDM.class);
		beanSubCategoryDM.addAll(subCatList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the ClientSubCategory. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanSubCategoryDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "clientSubCatId", "clientSubCatName", "clientCatName",
				"clientSubCatStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Client SubCategory", "Client Category", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("clientSubCatId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfSubCateName.setValue("");
		tfSubCateName.setComponentError(null);
		cbClientCat.setValue(null);
		cbClntSubcatStatus.setValue(cbClntSubcatStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editSubcategory() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Selected clientCategoryID. Id -> " + clientCategoryId);
		if (tblMstScrSrchRslt.getValue() != null) {
			ClientSubCategoryDM clientSubCategoryDM = beanSubCategoryDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			clientCategoryId = clientSubCategoryDM.getClientSubCatId();
			tfSubCateName.setValue(clientSubCategoryDM.getClientSubCatName());
			cbClntSubcatStatus.setValue(clientSubCategoryDM.getClientSubCatStatus());
			cbClientCat.setValue(clientSubCategoryDM.getClientCategoryId());
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
		tfSubCateName.setValue("");
		cbClientCat.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		cbClntSubcatStatus.setValue(cbClntSubcatStatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the
		// same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfSubCateName.setRequired(true);
		cbClientCat.setRequired(true);
		// reset the input controls to default value
		loadClientCategoryList();
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for ClientSubCategory. ID " + clientCategoryId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_CRM_CLIENT_SUBCAT);
		UI.getCurrent().getSession().setAttribute("audittablepk", clientCategoryId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfSubCateName.setRequired(false);
		cbClientCat.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfSubCateName.setRequired(true);
		editSubcategory();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfSubCateName.setComponentError(null);
		if ((tfSubCateName.getValue() == null) || tfSubCateName.getValue().trim().length() == 0) {
			tfSubCateName.setComponentError(new UserError(GERPErrorCodes.NULL_CLNT_SCATGRY_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfSubCateName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			ClientSubCategoryDM subcategoryobj = new ClientSubCategoryDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				subcategoryobj = beanSubCategoryDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (tfSubCateName.getValue() != null) {
				subcategoryobj.setClientSubCatName(tfSubCateName.getValue());
			}
			if (cbClientCat.getValue() != null) {
				subcategoryobj.setClientCategoryId((Long) cbClientCat.getValue());
			}
			if (cbClntSubcatStatus.getValue() != null) {
				subcategoryobj.setClientSubCatStatus((String) cbClntSubcatStatus.getValue());
			}
			subcategoryobj.setLastUpdatedDt(new Date());
			subcategoryobj.setLastUpdatedBy(username);
			serviceClientSubCategory.saveOrUpdateClientSubCategory(subcategoryobj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}