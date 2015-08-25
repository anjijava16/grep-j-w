/**
 * File Name	:	OrgPolicies.java
 * Description	:	This Screen Purpose for Modify the Organization Policies. Add the Organization Policies process should be directly added in DB.
 * Author		:	Mahaboob Subahan J
 * Date			:	Sep 22, 2014
 * Modification :   
 * Modified By  :   
 * Description 	:
 *
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version      Date           	Modified By      		Remarks
 * 0.1          Sep 22, 2014   	Mahaboob Subahan J		Initial Version		
 * 
 */
package com.gnts.hcm.txn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.OrgPoliciesDM;
import com.gnts.hcm.service.txn.OrgPoliciesService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class OrgPolicies extends BaseUI {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(OrgPolicies.class);
	private OrgPoliciesService serviceOrgPolicies = (OrgPoliciesService) SpringContextHelper.getBean("orgpolicies");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private BeanItemContainer<OrgPoliciesDM> beanOrgPolicies = null;
	// OrgPolicies Component Declaration
	private TextField tfPolicyName;
	private TextArea taPolicyDesc;
	private ComboBox cbPolicyGroup;
	private ComboBox cbPolicyStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	// Input Control Layout
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlDocument;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private String basepath1, basepath;
	private String userName;
	private Long companyId, moduleId;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public OrgPolicies() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = Long.valueOf(UI.getCurrent().getSession().getAttribute("moduleId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside Material() constructor");
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Painting OrgPolicies UI");
		// OrgPolicies Components Definition
		basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		basepath1 = basepath + "/VAADIN/themes/gerp/img/Document.pdf";
		tfPolicyName = new GERPTextField("Policy Name");
		tfPolicyName.setWidth("200");
		cbPolicyGroup = new GERPComboBox("Policy Group");
		cbPolicyGroup.setWidth("200");
		cbPolicyGroup.setItemCaptionPropertyId("lookupname");
		taPolicyDesc = new GERPTextArea("Policy Description");
		taPolicyDesc.setWidth("300");
		taPolicyDesc.setHeight("50");
		// OrgPolicies Document Upload
		vlDocument = new VerticalLayout();
		VerticalLayout docOrgPolici = new VerticalLayout();
		new UploadDocumentUI(vlDocument);
		docOrgPolici.addComponent(vlDocument);
		docOrgPolici.setSpacing(true);
		docOrgPolici.setMargin(true);
		docOrgPolici.setSizeFull();
		loadOrgPolicisGroup();
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	/*
	 * loadSrchRslt()-->this function is used for load the search result to table
	 */
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.setPageLength(14);
		tblMstScrSrchRslt.setWidth("100%");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.removeAllItems();
		List<OrgPoliciesDM> listOrgPolicies = new ArrayList<OrgPoliciesDM>();
		String orgPolName = tfPolicyName.getValue().toString();
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Search Org Policies Parameters are " + orgPolName + "," + (String) cbPolicyStatus.getValue() + ","
				+ companyId);
		listOrgPolicies = serviceOrgPolicies.getOrgPoliciesList(null, companyId, null, orgPolName,
				(String) cbPolicyStatus.getValue(), "F");
		recordCnt = listOrgPolicies.size();
		beanOrgPolicies = new BeanItemContainer<OrgPoliciesDM>(OrgPoliciesDM.class);
		beanOrgPolicies.addAll(listOrgPolicies);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Got the OrgPolicies result set");
		tblMstScrSrchRslt.setContainerDataSource(beanOrgPolicies);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "policyid", "policyname", "policyGroupName", "policydesc",
				"policystatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Policy Name", "Policy Group",
				"Policy Description", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("policyid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnWidth("policydesc", 180);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			assembleSearchLayout();
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// Remove all components in User Search Layout
		hlSearchLayout.removeAllComponents();
		tblMstScrSrchRslt.setPageLength(14);
		tfPolicyName.setRequired(false);
		// Add components for Search Layout
		FormLayout flPoliciName = new FormLayout();
		FormLayout flPoliciStatus = new FormLayout();
		flPoliciName.addComponent(tfPolicyName);
		flPoliciStatus.addComponent(cbPolicyStatus);
		hlSearchLayout.addComponent(flPoliciName);
		hlSearchLayout.addComponent(flPoliciStatus);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling User Input layout");
		// Remove all components in User Input Layout
		hlUserInputLayout.removeAllComponents();
		tblMstScrSrchRslt.setPageLength(11);
		tfPolicyName.setRequired(true);
		cbPolicyGroup.setRequired(true);
		// Add components for Input Layout
		FormLayout flOrgPolici1 = new FormLayout();
		FormLayout flOrgPolici2 = new FormLayout();
		FormLayout flOrgPolici3 = new FormLayout();
		FormLayout flOrgPolici4 = new FormLayout();
		flOrgPolici1.addComponent(tfPolicyName);
		flOrgPolici1.addComponent(cbPolicyGroup);
		flOrgPolici2.addComponent(taPolicyDesc);
		flOrgPolici3.addComponent(cbPolicyStatus);
		flOrgPolici4.addComponent(vlDocument);
		HorizontalLayout hlOrgPolici = new HorizontalLayout();
		hlOrgPolici.addComponent(flOrgPolici1);
		hlOrgPolici.addComponent(flOrgPolici2);
		hlOrgPolici.addComponent(flOrgPolici3);
		hlOrgPolici.addComponent(flOrgPolici4);
		hlOrgPolici.setSpacing(true);
		hlOrgPolici.setMargin(true);
		hlUserInputLayout.addComponent(hlOrgPolici);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setWidth("100%");
	}
	
	/*
	 * loadOrgPolicisGroup()-->this function is used for load the material UOM type
	 */
	private void loadOrgPolicisGroup() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Loading OrgPolicis Group Search...");
			BeanContainer<Long, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<Long, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("cmplookupid");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyId, moduleId, "Active",
					"HC_PLCYGRP"));
			cbPolicyGroup.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		tfPolicyName.setValue("");
		cbPolicyStatus.setValue(cbPolicyStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		resetFields();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tblMstScrSrchRslt.setValue(null);
		assembleUserInputLayout();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Updating existing record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		resetFields();
		editOrgPolyci();
	}
	
	// Reset the selected row's data into OrgPolycies input components
	private void editOrgPolyci() {
		if (tblMstScrSrchRslt.getValue() != null) {
			OrgPoliciesDM orgPoliciesDM = beanOrgPolicies.getItem(tblMstScrSrchRslt.getValue()).getBean();
			if ((orgPoliciesDM.getPolicyname()) != null) {
				tfPolicyName.setValue(orgPoliciesDM.getPolicyname().toString());
			}
			if ((orgPoliciesDM.getPolicygroup()) != null) {
				cbPolicyGroup.setValue((String) orgPoliciesDM.getPolicygroup());
			}
			if ((orgPoliciesDM.getPolicydesc()) != null) {
				taPolicyDesc.setValue(orgPoliciesDM.getPolicydesc().toString());
			}
			if ((orgPoliciesDM.getPolicystatus()) != null) {
				cbPolicyStatus.setValue(orgPoliciesDM.getPolicystatus());
			}
			if (orgPoliciesDM.getPolicydoc() != null) {
				byte[] certificate = orgPoliciesDM.getPolicydoc();
				UploadDocumentUI test = new UploadDocumentUI(vlDocument);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(vlDocument);
			}
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		tfPolicyName.setComponentError(null);
		cbPolicyGroup.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((tfPolicyName.getValue() == null) || tfPolicyName.getValue().trim().length() == 0) {
			tfPolicyName.setComponentError(new UserError(GERPErrorCodes.NULL_ORG_POLICIES_NAME));
			errorFlag = true;
		}
		if (cbPolicyGroup.getValue() == null) {
			cbPolicyGroup.setComponentError(new UserError(GERPErrorCodes.NULL_ORG_POLICIES_GROUP));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Throwing ValidationException. User data is > " + tfPolicyName.getValue() + cbPolicyGroup.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
		OrgPoliciesDM orgPoliciesDM = new OrgPoliciesDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			orgPoliciesDM = beanOrgPolicies.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		orgPoliciesDM.setCompanyid(companyId);
		orgPoliciesDM.setPolicyname(tfPolicyName.getValue().toString());
		orgPoliciesDM.setPolicygroup((String) cbPolicyGroup.getValue());
		orgPoliciesDM.setPolicydesc(taPolicyDesc.getValue().toString());
		if (cbPolicyStatus.getValue() != null) {
			orgPoliciesDM.setPolicystatus((String) cbPolicyStatus.getValue());
		}
		File file = new File(basepath1);
		FileInputStream fin = new FileInputStream(file);
		byte fileContent[] = new byte[(int) file.length()];
		fin.read(fileContent);
		fin.close();
		orgPoliciesDM.setPolicydoc(fileContent);
		orgPoliciesDM.setLastupdateddt(DateUtils.getcurrentdate());
		orgPoliciesDM.setLastupdatedby(userName);
		serviceOrgPolicies.saveorUpdateOrgPoliciesDetails(orgPoliciesDM);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		tfPolicyName.setValue("");
		tfPolicyName.setComponentError(null);
		cbPolicyGroup.setValue(null);
		cbPolicyGroup.setComponentError(null);
		cbPolicyStatus.setComponentError(null);
		taPolicyDesc.setValue("");
		taPolicyDesc.setComponentError(null);
		cbPolicyStatus.setValue(cbPolicyStatus.getItemIds().iterator().next());
		new UploadDocumentUI(vlDocument);
	}
}
