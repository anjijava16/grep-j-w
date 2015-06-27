/**
 * File Name 		: ClientOppertunityApp.java 
 * Description 		: this class is used for add/edit Client  details. 
 * Author 			: P Sekhar
 * Date 			: Mar 27, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * 
 */
package com.gnts.crm.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.domain.txn.CampaignDM;
import com.gnts.crm.domain.txn.OppertunitiesDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.crm.service.txn.CampaignService;
import com.gnts.crm.service.txn.OppertunityService;
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
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Opportunity extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private CompanyLookupService serviceCompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private OppertunityService serviceOppertunity = (OppertunityService) SpringContextHelper.getBean("clntOppertunity");
	private CampaignService serviceCampaign = (CampaignService) SpringContextHelper.getBean("clientCampaign");
	private Long companyId;
	// form layout for input controls
	private FormLayout FormLayout1, FormLayout2, FormLayout3, FormLayout4;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlCommetTblLayout = new VerticalLayout();
	private VerticalLayout vlDocumentLayout = new VerticalLayout();
	private String userName, strWidth = "130px";
	/**
	 * UI Components
	 */
	private TextField tfOppertName, tfWinProb, tfBusinessValue;
	private ComboBox cbClient, cbEmployee, cbOppertType, cbCampaign, cbSearchOppertStatus;
	private TextArea taRemarks;
	private PopupDateField closingDt;
	private BeanItemContainer<OppertunitiesDM> beanClntOppertunity = new BeanItemContainer<OppertunitiesDM>(
			OppertunitiesDM.class);
	private Long moduleId, oppertunityId, employeeId;
	private Comments comment;
	private Documents document;
	private int total = 0;
	private Logger logger = Logger.getLogger(Opportunity.class);
	
	public Opportunity() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildview();
	}
	
	/**
	 * buildMainview()-->for screen UI design
	 * 
	 */
	private void buildview() {
		// TODO Auto-generated method stub
		tfOppertName = new GERPTextField("Opportunity Name");
		tfOppertName.setMaxLength(100);
		tfOppertName.setWidth(strWidth);
		tfOppertName.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfOppertName.setComponentError(null);
				if (tfOppertName.getValue() != null) {
					tfOppertName.setComponentError(null);
				}
			}
		});
		cbSearchOppertStatus = new GERPComboBox("Status");
		cbSearchOppertStatus.setItemCaptionPropertyId("lookupname");
		cbSearchOppertStatus.setWidth("150");
		loadoppertstatus();
		/**
		 * add fields to form Layout
		 */
		cbOppertType = new GERPComboBox("Opportunity Type");
		cbOppertType.setItemCaptionPropertyId("lookupname");
		cbOppertType.setWidth(strWidth);
		cbOppertType.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbOppertType.setComponentError(null);
				if (cbOppertType.getValue() != null) {
					cbOppertType.setComponentError(null);
				}
			}
		});
		loadOppertunityTypeByLookUpList();
		cbCampaign = new GERPComboBox("Campaign");
		cbCampaign.setItemCaptionPropertyId("campaignname");
		cbCampaign.setWidth(strWidth);
		loadClientCampaigns();
		cbClient = new GERPComboBox("Client");
		cbClient.setItemCaptionPropertyId("clientName");
		cbClient.setWidth(strWidth);
		loadClientsDetails();
		closingDt = new PopupDateField("Close Date");
		closingDt.setDateFormat("dd-MMM-yyyy");
		closingDt.setWidth(strWidth);
		cbEmployee = new GERPComboBox("Assigned To");
		cbEmployee.setItemCaptionPropertyId("firstname");
		cbEmployee.setWidth(strWidth);
		loadEmployeeList();
		tfWinProb = new GERPTextField("Win Probability");
		tfBusinessValue = new GERPTextField("Business Value");
		taRemarks = new GERPTextArea("Remarks");
		taRemarks.setWidth(strWidth);
		taRemarks.setHeight("50px");
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
		hlSearchLayout.removeAllComponents();
		FormLayout1 = new FormLayout();
		FormLayout2 = new FormLayout();
		FormLayout3 = new FormLayout();
		FormLayout1.addComponent(tfOppertName);
		FormLayout2.addComponent(cbClient);
		FormLayout3.addComponent(cbSearchOppertStatus);
		hlSearchLayout.addComponent(FormLayout1);
		hlSearchLayout.addComponent(FormLayout2);
		hlSearchLayout.addComponent(FormLayout3);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// add the form layouts into user input layout
		hlUserInputLayout.removeAllComponents();
		FormLayout1 = new FormLayout();
		FormLayout2 = new FormLayout();
		FormLayout3 = new FormLayout();
		FormLayout4 = new FormLayout();
		FormLayout1.addComponent(tfOppertName);
		tfOppertName.setRequired(true);
		FormLayout1.addComponent(cbOppertType);
		cbOppertType.setRequired(true);
		FormLayout1.addComponent(cbCampaign);
		FormLayout2.addComponent(cbClient);
		FormLayout2.addComponent(closingDt);
		FormLayout2.addComponent(cbEmployee);
		FormLayout3.addComponent(tfWinProb);
		FormLayout3.addComponent(tfBusinessValue);
		FormLayout3.addComponent(cbSearchOppertStatus);
		FormLayout4.addComponent(taRemarks);
		HorizontalLayout hlInput = new HorizontalLayout();
		hlInput.setMargin(true);
		VerticalLayout hlUserInput = new VerticalLayout();
		hlInput.setWidth("1180");
		hlInput.addComponent(FormLayout1);
		hlInput.addComponent(FormLayout2);
		hlInput.addComponent(FormLayout3);
		hlInput.addComponent(FormLayout4);
		hlUserInput.addComponent(GERPPanelGenerator.createPanel(hlInput));
		TabSheet test3 = new TabSheet();
		test3.addTab(vlCommetTblLayout, "Comments");
		test3.addTab(vlDocumentLayout, "Documents");
		test3.setWidth("1195");
		hlUserInput.addComponent(test3);
		hlInput.setMargin(true);
		hlInput.setSpacing(true);
		hlUserInputLayout.addComponent(hlUserInput);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
		// build search layout
	}
	
	public void loadSrchRslt() {
		tblMstScrSrchRslt.removeAllItems();
		List<OppertunitiesDM> clntOppertunityList = new ArrayList<OppertunitiesDM>();
		clntOppertunityList = serviceOppertunity.getClientOppertunityDetails(companyId, null,
				(Long) cbClient.getValue(), null, tfOppertName.getValue(), cbSearchOppertStatus.getValue().toString());
		System.out.println("STATUS" + cbSearchOppertStatus.getValue());
		total = clntOppertunityList.size();
		beanClntOppertunity.addAll(clntOppertunityList);
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.setContainerDataSource(beanClntOppertunity);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "oppertunityId", "oppertunityName", "oppertunityType",
				"closingDate", "oppertunityStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Opportunity Name", "Opportunity Type",
				"Close Date", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + total);
	}
	
	private void editClientOpportunityDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Selected opportunuity. Id -> " + oppertunityId);
		if (tblMstScrSrchRslt.getValue() != null) {
			OppertunitiesDM editopportunuitylist = beanClntOppertunity.getItem(tblMstScrSrchRslt.getValue()).getBean();
			oppertunityId = editopportunuitylist.getOppertunityId();
			if (editopportunuitylist.getBusinessValue() != null
					&& !"null".equals(editopportunuitylist.getBusinessValue())) {
				tfBusinessValue.setValue(editopportunuitylist.getBusinessValue().toString());
			} else {
				tfBusinessValue.setValue("0");
			}
			tfOppertName.setValue(editopportunuitylist.getOppertunityName());
			if (editopportunuitylist.getWinProbability() != null
					&& !"null".equals(editopportunuitylist.getWinProbability())) {
				tfWinProb.setValue(editopportunuitylist.getWinProbability().toString());
			} else {
				tfWinProb.setValue("0");
			}
			cbOppertType.setValue(editopportunuitylist.getOppertunityType());
			cbCampaign.setValue(editopportunuitylist.getCampaingnId());
			cbEmployee.setValue(editopportunuitylist.getAssignedTo());
			cbClient.setValue(editopportunuitylist.getClientId());
			if (editopportunuitylist.getRemarks() != null && !"null".equals(editopportunuitylist.getRemarks())) {
				taRemarks.setValue(editopportunuitylist.getRemarks());
			}
			try {
				closingDt.setValue(editopportunuitylist.getClosingDate1());
			}
			catch (Exception e) {
				logger.info("convert closing date to date" + e);
			}
		}
		comment = new Comments(vlCommetTblLayout, employeeId, oppertunityId, null, null, null, null, null);
		document = new Documents(vlDocumentLayout, oppertunityId, null, null, null, null, null);
		comment.loadsrch(true, null, null, null, null, oppertunityId, null);
		document.loadsrcrslt(true, null, null, null, null, oppertunityId, null);
	}
	
	/**
	 * this method used to load the opportunity type in company look up list based on status ,company id module id and
	 * look up code
	 */
	private void loadOppertunityTypeByLookUpList() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompanylookup = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanylookup.setBeanIdProperty("lookupname");
			beanCompanylookup.addAll(serviceCompany.getCompanyLookUpByLookUp(companyId, moduleId, "Active",
					"CM_OPRTYPE"));
			cbOppertType.setContainerDataSource(beanCompanylookup);
		}
		catch (Exception e) {
			logger.info("load company look up details" + e);
		}
	}
	
	private void loadClientCampaigns() {
		BeanContainer<Long, CampaignDM> beancampaign = new BeanContainer<Long, CampaignDM>(CampaignDM.class);
		beancampaign.setBeanIdProperty("campaingnId");
		beancampaign.addAll(serviceCampaign.getCampaignDetailList(companyId, null, null, null, null, null, null, null,
				"P"));
		cbCampaign.setContainerDataSource(beancampaign);
	}
	
	private void loadoppertstatus() {
		BeanContainer<String, CompanyLookupDM> beanlookup = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanlookup.setBeanIdProperty("lookupname");
		beanlookup.addAll(serviceCompany.getCompanyLookUpByLookUp(companyId, moduleId, "Active", "CM_OPRSTAT"));
		cbSearchOppertStatus.setContainerDataSource(beanlookup);
	}
	
	/**
	 * load client details based on company id and status
	 */
	private void loadClientsDetails() {
		try {
			BeanContainer<Long, ClientDM> beanClients = new BeanContainer<Long, ClientDM>(ClientDM.class);
			beanClients.setBeanIdProperty("clientId");
			beanClients.addAll(serviceClients.getClientDetails(companyId, null, null, null, null, null, null, null,
					"Active", "P"));
			cbClient.setContainerDataSource(beanClients);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load Clients Details " + e);
		}
	}
	
	/**
	 * this method used to load the employee list based on company id, department id and status
	 */
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyId, employeeId,
					null, null, null, "P"));
			cbEmployee.setContainerDataSource(beanEmployee);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		// TODO Auto-generated method stub
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		loadSrchRslt();
		if (total == 0) {
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
		// TODO Auto-generated method stub
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reload the search using the defaults
		tfOppertName.setValue("");
		cbSearchOppertStatus.setValue(cbSearchOppertStatus.getItemIds().iterator().next());
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		// TODO Auto-generated method stub
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the
		// same container
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent((hlUserInputLayout));
		tblMstScrSrchRslt.setVisible(false);
		comment = new Comments(vlCommetTblLayout, employeeId, oppertunityId, null, null, null, null, null);
		document = new Documents(vlDocumentLayout, oppertunityId, null, null, null, null, null);
		// reset the input controls to default value
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the
		// same container
		hlCmdBtnLayout.setVisible(false);
		hlUserIPContainer.addComponent((hlUserInputLayout));
		assembleUserInputLayout();
		tblMstScrSrchRslt.setVisible(false);
		editClientOpportunityDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean errorflag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if (tfOppertName.getValue() == null || tfOppertName.getValue().trim().length() == 0) {
			tfOppertName.setComponentError(new UserError(GERPErrorCodes.NULL_CLNT_OPPORTUNITY_NAME));
			errorflag = true;
		}
		if (cbOppertType.getValue() == null) {
			cbOppertType.setComponentError(new UserError(GERPErrorCodes.NULL_OPPORTUNITY_TYPE));
			errorflag = true;
		}
		if (errorflag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		try {
			OppertunitiesDM oppertunitiesDM = new OppertunitiesDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				oppertunitiesDM = beanClntOppertunity.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (cbEmployee.getValue() != null) {
				oppertunitiesDM.setAssignedTo((Long) cbEmployee.getValue());
			}
			if (tfBusinessValue.getValue().toString().trim().length() > 0) {
				oppertunitiesDM.setBusinessValue(Long.valueOf(tfBusinessValue.getValue()));
			}
			if (cbClient.getValue() != null) {
				oppertunitiesDM.setClientId(Long.valueOf(cbClient.getValue().toString()));
			}
			if (cbCampaign.getValue() != null) {
				oppertunitiesDM.setCampaingnId((Long) cbCampaign.getValue());
			}
			oppertunitiesDM.setCompanyId(companyId);
			if (tfOppertName.getValue().toString().trim().length() > 0) {
				oppertunitiesDM.setOppertunityName(tfOppertName.getValue());
			}
			if (cbOppertType.getValue() != null) {
				oppertunitiesDM.setOppertunityType((String) cbOppertType.getValue());
			}
			oppertunitiesDM.setRemarks(taRemarks.getValue());
			if (tfWinProb.getValue().toString().trim().length() > 0) {
				oppertunitiesDM.setWinProbability(Long.valueOf(tfWinProb.getValue()));
			}
			oppertunitiesDM.setOppertunityStatus(cbSearchOppertStatus.getValue().toString());
			if (cbSearchOppertStatus.getValue().equals("Won") || cbSearchOppertStatus.getValue().equals("Lost")) {
				closingDt.setValue(DateUtils.getcurrentdate());
			}
			oppertunitiesDM.setLastUpdatedBy(userName);
			oppertunitiesDM.setLastUpdatedDt(DateUtils.getcurrentdate());
			serviceOppertunity.saveOrUpdateClientOppertunityDetails(oppertunitiesDM);
			comment.saveclientoppertunuity(oppertunitiesDM.getOppertunityId());
			comment.resetfields();
			document.saveClientOppurtunuity(oppertunitiesDM.getOppertunityId());
			document.ResetFields();
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for Client Case ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_CRM_OPPERTUNITIES);
		UI.getCurrent().getSession().setAttribute("audittablepk", "");
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		tfOppertName.setRequired(false);
		cbOppertType.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
		tfBusinessValue.setValue("0");
		tfOppertName.setValue("");
		tfOppertName.setComponentError(null);
		tfWinProb.setValue("0");
		taRemarks.setValue("");
		cbEmployee.setValue(null);
		cbOppertType.setValue(null);
		cbOppertType.setComponentError(null);
		cbCampaign.setValue(null);
		cbClient.setValue(null);
		tfWinProb.setValue("");
		tfBusinessValue.setValue("");
		cbCampaign.setComponentError(null);
		cbSearchOppertStatus.setValue(cbSearchOppertStatus.getItemIds().iterator().next());
		closingDt.setValue(null);
	}
}
