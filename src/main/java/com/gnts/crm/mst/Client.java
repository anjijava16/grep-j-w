/**
 * File Name 		: Client.java 
 * Description 		: this class is used for add/edit Client  details. 
 * Author 			: P Sekhar
 * Date 			: Mar 12, 2014
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
 * 0.2			18-Jun-2014			MOHAMED			Code re-factoring
 * 0.3			16-July-2014		MOHAMED			calling Constructor of comments and document UI
 **/
package com.gnts.crm.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CityDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.CityService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.StateService;
import com.gnts.crm.domain.mst.ClientCategoryDM;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.domain.mst.ClientSubCategoryDM;
import com.gnts.crm.domain.txn.CampaignDM;
import com.gnts.crm.domain.txn.LeadsDM;
import com.gnts.crm.service.mst.ClientCategoryService;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.crm.service.mst.ClientSubCategoryService;
import com.gnts.crm.service.txn.CampaignService;
import com.gnts.crm.service.txn.LeadsService;
import com.gnts.crm.txn.Comments;
import com.gnts.crm.txn.Documents;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.txn.TestingDocuments;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Client extends BaseUI {
	private CityService serviceCity = (CityService) SpringContextHelper.getBean("city");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private LeadsService serviceLead = (LeadsService) SpringContextHelper.getBean("clientLeads");
	private ClientCategoryService serviceClientCat = (ClientCategoryService) SpringContextHelper
			.getBean("clientCategory");
	private ClientSubCategoryService serviceClientSubCat = (ClientSubCategoryService) SpringContextHelper
			.getBean("clientSubCategory");
	private CompanyLookupService serviceCompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private CampaignService serviceCampaign = (CampaignService) SpringContextHelper.getBean("clientCampaign");
	// form layout for input controls
	private FormLayout formLayout1, formLayout2, formLayout3, formLayout4;
	private TabSheet maintab = new TabSheet();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlCommetTblLayout = new VerticalLayout();
	private VerticalLayout vlinformTblLayout = new VerticalLayout();
	private HorizontalLayout hlInput;
	private VerticalLayout hlUserInput;
	// User Input Components
	private TextField tfClntName, tfRevenue, tfPostCode, tfPhone, tfFax, tfEmail, tfWebsite, tfClntcode;
	private TextArea taClntAddrss, tfotherDetails;
	private ComboBox cbClntCategory, cbClntSubCategory, cbCampaign, cbLeads, cbAssignedto, cbClientrate, cbCountry,
			cbState, cbCity, cbclntindustry, cbClntStatus;
	// Bean Container
	private BeanItemContainer<ClientDM> beanClnt = null;
	// local variables declaration
	private Long companyid;
	private Long countryid;
	private Long employeeid, clientId, moduleid;
	private int recordCnt = 0;
	private String username;
	private Comments comment;
	private Documents document;
	private ClientInformation inform;
	// Initialize the logger
	private Logger logger = Logger.getLogger(Client.class);
	private static final long serialVersionUID = 1L;
	// for test documents
	private VerticalLayout hlDocumentLayout = new VerticalLayout();
	
	// Constructor
	public Client() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeid = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		countryid = (Long) UI.getCurrent().getSession().getAttribute("countryid");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ClientCategory() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Clients UI");
		// Client Name text field
		tfClntName = new GERPTextField("Client Name");
		tfClntName.setRequired(false);
		tfClntName.setMaxLength(100);
		tfClntName.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfClntName.setComponentError(null);
				if (tfClntName.getValue() != null) {
					tfClntName.setComponentError(null);
				}
			}
		});
		// ClientAddress Name text Area
		taClntAddrss = new GERPTextArea("Client Address");
		taClntAddrss.setHeight("75px");
		taClntAddrss.setMaxLength(100);
		tfClntcode = new GERPTextField("Client Code");
		tfClntcode.setRequired(true);
		// Revenue text Field
		tfRevenue = new GERPTextField("Revenue");
		tfRevenue.setRequired(false);
		tfRevenue.setValue("0");
		tfRevenue.setWidth("150");
		tfRevenue.setMaxLength(24);
		// post code text Field
		tfPostCode = new GERPTextField("Post Code");
		tfPostCode.setRequired(false);
		tfPostCode.setMaxLength(25);
		// phone number text Field
		tfPhone = new GERPTextField("Phone Number");
		tfPhone.setRequired(true);
		tfPhone.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfPhone.setComponentError(null);
				if (tfPhone.getValue() != null) {
					if (!tfPhone.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
						tfPhone.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
					} else {
						tfPhone.setComponentError(null);
					}
				}
			}
		});
		tfPhone.setMaxLength(25);
		// fax number text Field
		tfFax = new GERPTextField("Fax Number");
		tfFax.setRequired(false);
		tfFax.setMaxLength(25);
		// Email text Field
		tfEmail = new GERPTextField("Email");
		tfEmail.setMaxLength(25);
		tfEmail.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfEmail.setComponentError(null);
				if (tfEmail.getValue() != null) {
					tfEmail.setComponentError(null);
				}
			}
		});
		// Website text Field
		tfWebsite = new GERPTextField("Website");
		tfWebsite.setRequired(false);
		tfWebsite.setMaxLength(25);
		tfotherDetails = new GERPTextArea("Other Details");
		tfotherDetails.setRequired(false);
		tfotherDetails.setMaxLength(24);
		cbClntSubCategory = new GERPComboBox("Sub Category");
		cbClntSubCategory.setItemCaptionPropertyId("clientSubCatName");
		cbClntSubCategory.setWidth("150");
		// ClientCategory combo box
		cbClntCategory = new GERPComboBox("Client Category");
		cbClntCategory.setItemCaptionPropertyId("clientCatName");
		cbClntCategory.setWidth("150");
		loadClientCategoryList();
		cbClntCategory.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbClntCategory.getValue() != null) {
					loadClientSubCategoryList();
				}
			}
		});
		cbCampaign = new GERPComboBox("Campaign");
		cbCampaign.setItemCaptionPropertyId("campaignname");
		cbCampaign.setWidth("150");
		loadClientCampaigns();
		// Leads comboBox
		cbLeads = new GERPComboBox("Leads");
		cbLeads.setItemCaptionPropertyId("firstName");
		cbLeads.setWidth("150");
		loadLeadsDetails();
		// Assigned comboBox
		cbAssignedto = new GERPComboBox("Assigned To");
		cbAssignedto.setItemCaptionPropertyId("firstname");
		cbAssignedto.setWidth("150");
		loadEmployeeList();
		// Client Rating combobox
		cbClientrate = new GERPComboBox("Client Rating");
		cbClientrate.setItemCaptionPropertyId("lookupname");
		cbClientrate.setWidth("150");
		loadcompanyUpList();
		// Country combobox
		cbCountry = new GERPComboBox("Country");
		cbCountry.setItemCaptionPropertyId("countryName");
		cbCountry.setWidth("150");
		loadCountryList();
		cbCountry.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadStateList();
				}
			}
		});
		// State combobox
		cbState = new GERPComboBox("State");
		cbState.setItemCaptionPropertyId("stateName");
		cbState.setWidth("150");
		cbState.setImmediate(true);
		cbState.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadCityList();
				}
			}
		});
		// city combobox
		cbCity = new GERPComboBox("City");
		cbCity.setItemCaptionPropertyId("cityname");
		cbCity.setWidth("150");
		// Client status combo box
		cbClntStatus = new GERPComboBox("Status", BASEConstants.M_CRM_CLIENTS, BASEConstants.CLIENTS);
		cbClntStatus.setItemCaptionPropertyId("desc");
		cbClntStatus.setWidth("150");
		cbclntindustry = new GERPComboBox("Client Industry");
		cbclntindustry.setItemCaptionPropertyId("lookupname");
		cbclntindustry.setWidth("150");
		loadLookUpList();
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		hlDocumentLayout.setEnabled(false);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		formLayout1 = new FormLayout();
		formLayout2 = new FormLayout();
		formLayout3 = new FormLayout();
		formLayout4 = new FormLayout();
		formLayout1.addComponent(tfClntName);
		formLayout2.addComponent(cbClntCategory);
		formLayout3.addComponent(cbClntSubCategory);
		formLayout4.addComponent(cbClntStatus);
		hlSearchLayout.addComponent(formLayout1);
		hlSearchLayout.addComponent(formLayout2);
		hlSearchLayout.addComponent(formLayout3);
		hlSearchLayout.addComponent(formLayout4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// add the form layouts into user input layout
		hlUserInputLayout.removeAllComponents();
		maintab.removeAllComponents();
		formLayout1 = new FormLayout();
		formLayout2 = new FormLayout();
		formLayout3 = new FormLayout();
		formLayout4 = new FormLayout();
		formLayout1.addComponent(tfClntcode);
		formLayout1.addComponent(tfClntName);
		tfClntName.setRequired(true);
		formLayout1.addComponent(taClntAddrss);
		formLayout2.addComponent(cbClntCategory);
		cbClntCategory.setRequired(true);
		formLayout2.addComponent(cbClntSubCategory);
		formLayout2.addComponent(cbCountry);
		formLayout2.addComponent(cbState);
		formLayout2.addComponent(cbCity);
		formLayout3.addComponent(tfPostCode);
		formLayout3.addComponent(tfPhone);
		formLayout3.addComponent(tfEmail);
		cbCountry.setRequired(true);
		formLayout3.addComponent(tfFax);
		formLayout3.addComponent(tfWebsite);
		formLayout4.addComponent(tfotherDetails);
		formLayout4.addComponent(cbClntStatus);
		hlInput = new HorizontalLayout();
		hlInput.setMargin(true);
		hlInput.setSpacing(true);
		hlInput.setWidth("1100");
		hlInput.addComponent(formLayout1);
		hlInput.addComponent(formLayout2);
		hlInput.addComponent(formLayout3);
		hlInput.addComponent(formLayout4);
		hlUserInput = new VerticalLayout();
		hlUserInput.addComponent(GERPPanelGenerator.createPanel(hlInput));
		TabSheet test3 = new TabSheet();
		test3.addTab(vlinformTblLayout, " Client Information");
		test3.addTab(vlCommetTblLayout, "Comments");
		test3.addTab(hlDocumentLayout, "Documents");
		test3.setWidth("1370");
		hlUserInputLayout.addComponent(hlUserInput);
		hlUserInput.addComponent(test3);
		// build search layout
	}
	
	private void loadCountryList() {
		try {
			BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
			beanCountry.setBeanIdProperty("countryID");
			beanCountry.addAll(serviceCountry.getCountryList(null, null, null, null, "Active", "P"));
			cbCountry.setContainerDataSource(beanCountry);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	private void loadStateList() {
		try {
			BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
			beanState.setBeanIdProperty("stateId");
			beanState.addAll(serviceState.getStateList(null, "Active", (Long) cbCountry.getValue(), null, "P"));
			cbState.setContainerDataSource(beanState);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	private void loadCityList() {
		try {
			BeanContainer<Long, CityDM> beanCity = new BeanContainer<Long, CityDM>(CityDM.class);
			beanCity.setBeanIdProperty("cityid");
			beanCity.addAll(serviceCity.getCityList(null, null, Long.valueOf(cbState.getValue().toString()), "Active",
					companyid, "P"));
			cbCity.setContainerDataSource(beanCity);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanemploye = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanemploye.setBeanIdProperty("employeeid");
			beanemploye.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, employeeid, null,
					null, null, "P"));
			cbAssignedto.setContainerDataSource(beanemploye);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadClientCategoryList() {
		try {
			BeanContainer<Long, ClientCategoryDM> beanCat = new BeanContainer<Long, ClientCategoryDM>(
					ClientCategoryDM.class);
			beanCat.setBeanIdProperty("clientCategoryId");
			beanCat.addAll(serviceClientCat.getCrmClientCategoryList(companyid, null, "Active", "P"));
			cbClntCategory.setContainerDataSource(beanCat);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load client category List" + e);
		}
	}
	
	private void loadClientSubCategoryList() {
		try {
			BeanContainer<Long, ClientSubCategoryDM> beanSubCat = new BeanContainer<Long, ClientSubCategoryDM>(
					ClientSubCategoryDM.class);
			beanSubCat.setBeanIdProperty("clientSubCatId");
			beanSubCat.addAll(serviceClientSubCat.getClientSubCategoryList(companyid, null, null, "Active",
					(Long) cbClntCategory.getValue(), "F"));
			cbClntSubCategory.setContainerDataSource(beanSubCat);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load client sub category List" + e);
		}
	}
	
	private void loadClientCampaigns() {
		try {
			BeanContainer<Long, CampaignDM> beancampaign = new BeanContainer<Long, CampaignDM>(CampaignDM.class);
			beancampaign.setBeanIdProperty("campaingnId");
			beancampaign.addAll(serviceCampaign.getCampaignDetailList(companyid, null, null, null, null, null, null,
					null, "P"));
			cbCampaign.setContainerDataSource(beancampaign);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load client Campaign List" + e);
		}
	}
	
	private void loadLeadsDetails() {
		try {
			BeanContainer<Long, LeadsDM> beanLead = new BeanContainer<Long, LeadsDM>(LeadsDM.class);
			beanLead.setBeanIdProperty("leadId");
			beanLead.addAll(serviceLead.getLeadsDetailsList(companyid, null, null, "Active", null, "P"));
			cbLeads.setContainerDataSource(beanLead);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load leads details" + e);
		}
	}
	
	private void loadcompanyUpList() {
		try {
			BeanContainer<String, CompanyLookupDM> beanlook = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanlook.setBeanIdProperty("lookupname");
			beanlook.addAll(serviceCompany.getCompanyLookUpByLookUp(companyid, moduleid, "Active", "CM_CLNTRTG"));
			cbClientrate.setContainerDataSource(beanlook);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load company look up details" + e);
		}
	}
	
	private void loadLookUpList() {
		try {
			BeanContainer<String, CompanyLookupDM> beanlook = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanlook.setBeanIdProperty("lookupname");
			beanlook.addAll(serviceCompany.getCompanyLookUpByLookUp(companyid, moduleid, "Active", "CM_CLNTIND"));
			cbclntindustry.setContainerDataSource(beanlook);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load company look up details" + e);
		}
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<ClientDM> listClient = new ArrayList<ClientDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfClntName.getValue() + ", " + (String) cbClntStatus.getValue());
			listClient = serviceClients.getClientDetails(companyid, null, (Long) cbClntCategory.getValue(),
					(Long) cbClntSubCategory.getValue(), null, null, null, (String) tfClntName.getValue(),
					(String) cbClntStatus.getValue(), "F");
			recordCnt = listClient.size();
			beanClnt = new BeanItemContainer<ClientDM>(ClientDM.class);
			beanClnt.addAll(listClient);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Client. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanClnt);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "clientId", "clientcatname", "clientCode", "clientName",
					"cityName", "clientSttus", "lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Category", "Client Code", "Client Name",
					"City", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("clientId", Align.RIGHT);
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
		tfClntName.setValue("");
		tfClntName.setComponentError(null);
		tfClntcode.setComponentError(null);
		tfClntcode.setValue("");
		taClntAddrss.setValue("");
		tfEmail.setValue("");
		tfEmail.setComponentError(null);
		tfFax.setValue("");
		tfotherDetails.setValue("");
		tfPostCode.setValue("");
		tfPhone.setValue("");
		tfPhone.setComponentError(null);
		tfRevenue.setValue("0");
		tfWebsite.setValue("");
		cbLeads.setValue(null);
		cbAssignedto.setValue(null);
		cbCampaign.setValue(null);
		cbCity.setValue(null);
		cbState.setValue(null);
		cbCountry.setValue(countryid);
		cbCountry.setRequired(false);
		cbState.setRequired(false);
		cbCity.setRequired(false);
		cbCountry.setComponentError(null);
		cbClntCategory.setValue(null);
		cbClntCategory.setComponentError(null);
		cbClntSubCategory.setValue(null);
		cbClientrate.setValue(null);
		cbclntindustry.setValue(null);
		cbClntStatus.setValue(cbClntStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editClient() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlCmdBtnLayout.setVisible(false);
			hlUserInputLayout.setVisible(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Dept. Id -> "
					+ clientId);
			if (tblMstScrSrchRslt.getValue() != null) {
				ClientDM clientDM = beanClnt.getItem(tblMstScrSrchRslt.getValue()).getBean();
				clientId = clientDM.getClientId();
				tfClntName.setValue(clientDM.getClientName());
				taClntAddrss.setValue(clientDM.getClientAddress());
				cbClntCategory.setValue(clientDM.getClientCatId());
				cbClntSubCategory.setValue(clientDM.getClientSubCatId());
				cbCampaign.setValue(clientDM.getCampaignId());
				cbAssignedto.setValue(clientDM.getAssignedTo());
				cbClientrate.setValue(clientDM.getClinetRating());
				if (clientDM.getRevenue() != null && !equals(clientDM.getRevenue())) {
					tfRevenue.setValue(clientDM.getRevenue().toString());
				}
				cbclntindustry.setValue(clientDM.getClientIndustry());
				if (clientDM.getClientCode() != null) {
					tfClntcode.setValue(clientDM.getClientCode());
				}
				cbLeads.setValue(clientDM.getLeadId());
				tfEmail.setValue(clientDM.getEmailId());
				tfFax.setValue(clientDM.getFaxNo());
				tfPhone.setValue(clientDM.getPhoneNo());
				tfPostCode.setValue(clientDM.getPostalCode());
				tfWebsite.setValue(clientDM.getWebsite());
				tfotherDetails.setValue(clientDM.getOtherDetails());
				cbCountry.setValue((clientDM.getCountryId()));
				cbState.setValue(clientDM.getStateId().toString());
				cbCity.setValue(clientDM.getCityId().toString());
			}
			comment = new Comments(vlCommetTblLayout, employeeid, null, null, clientId, null, null, null);
			new TestingDocuments(hlDocumentLayout, clientId.toString(), "CLIENT");
			hlDocumentLayout.setEnabled(true);
			inform = new ClientInformation(vlinformTblLayout, clientId);
			comment.loadsrch(true, clientId, null, null, null, null, null);
			document.loadsrcrslt(true, clientId, null, null, null, null, null);
			inform.loadsrch(true, clientId);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
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
		tfClntName.setValue("");
		cbClntCategory.setValue(null);
		cbClntSubCategory.setValue(null);
		cbClntStatus.setValue(cbClntStatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the
		// same container
		hlUserInputLayout.removeAllComponents();
		hlCmdBtnLayout.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tfClntName.setRequired(true);
		tblMstScrSrchRslt.setValue(null);
		tblMstScrSrchRslt.setVisible(false);
		inform = new ClientInformation(vlinformTblLayout, clientId);
		comment = new Comments(vlCommetTblLayout, employeeid, null, null, clientId, null, null, null);
		// reset the input controls to default value
		resetFields();
		cbCountry.setRequired(true);
		cbState.setRequired(true);
		cbCity.setRequired(true);
		hlDocumentLayout.removeAllComponents();
		hlDocumentLayout.setEnabled(false);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for client cat. ID " + clientId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_CRM_CLIENTS);
		UI.getCurrent().getSession().setAttribute("audittablepk", clientId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		cbClntCategory.setRequired(false);
		tfClntName.setRequired(false);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		// reset the input controls to default value
		hlCmdBtnLayout.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tfClntName.setRequired(true);
		tblMstScrSrchRslt.setVisible(false);
		editClient();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfClntName.setComponentError(null);
		tfClntcode.setComponentError(null);
		cbClntCategory.setComponentError(null);
		tfPhone.setComponentError(null);
		cbCountry.setComponentError(null);
		cbState.setComponentError(null);
		cbCity.setComponentError(null);
		if ((tfClntName.getValue() == null) || tfClntName.getValue().trim().length() == 0) {
			tfClntName.setComponentError(new UserError(GERPErrorCodes.NULL_CLNT_NAME));
			errorFlag = true;
		}
		if ((tfClntcode.getValue() == null) || tfClntcode.getValue().trim().length() == 0) {
			tfClntcode.setComponentError(new UserError(GERPErrorCodes.NULL_CLNT_CODE));
			errorFlag = true;
		}
		if (cbClntCategory.getValue() == null) {
			cbClntCategory.setComponentError(new UserError(GERPErrorCodes.NULL_CLNT_CATGRY_NAME));
			errorFlag = true;
		}
		if ((tfPhone.getValue() == null) || tfPhone.getValue().trim().length() == 0) {
			tfPhone.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
			errorFlag = true;
		}
		if (cbState.getValue() == null) {
			cbState.setComponentError(new UserError(GERPErrorCodes.NULL_STATE_NAME));
			errorFlag = true;
		}
		if (cbCity.getValue() == null) {
			cbCity.setComponentError(new UserError(GERPErrorCodes.NULL_CITY_NAME));
			errorFlag = true;
		}
		if (cbCountry.getValue() == null) {
			cbCountry.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_COUNTRY));
			errorFlag = true;
		}
		if (errorFlag) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfClntName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			ClientDM clientDM = new ClientDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				clientDM = beanClnt.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			clientDM.setClientCode(tfClntcode.getValue());
			clientDM.setCompanyId(companyid);
			clientDM.setClientName(tfClntName.getValue().toString());
			clientDM.setClientAddress(taClntAddrss.getValue());
			clientDM.setClientCatId((Long) cbClntCategory.getValue());
			clientDM.setClientSubCatId((Long) cbClntSubCategory.getValue());
			clientDM.setCampaignId((Long) cbCampaign.getValue());
			clientDM.setLeadId((Long) cbLeads.getValue());
			if (tfRevenue.getValue() != "" && tfRevenue.getValue().toString().trim().length() > 0) {
				clientDM.setRevenue(Long.valueOf(tfRevenue.getValue()));
			}
			clientDM.setAssignedTo((Long) cbAssignedto.getValue());
			clientDM.setPostalCode(tfPostCode.getValue());
			clientDM.setPhoneNo(tfPhone.getValue());
			clientDM.setFaxNo(tfFax.getValue());
			clientDM.setEmailId(tfEmail.getValue());
			clientDM.setWebsite(tfWebsite.getValue());
			clientDM.setOtherDetails(tfotherDetails.getValue());
			if (cbClientrate.getValue() != null) {
				clientDM.setClinetRating(cbClientrate.getValue().toString());
			}
			if (cbclntindustry.getValue() != null) {
				clientDM.setClientIndustry(cbclntindustry.getValue().toString());
			}
			if (cbCountry.getValue() != null) {
				clientDM.setCountryId((Long) cbCountry.getValue());
			}
			if (cbState.getValue() != null) {
				clientDM.setStateId((Long.valueOf(cbState.getValue().toString())));
			}
			if (cbCity.getValue() != null) {
				clientDM.setCityId((Long.valueOf(cbCity.getValue().toString())));
			}
			if (cbClntStatus.getValue() != null) {
				clientDM.setClientSttus(cbClntStatus.getValue().toString());
			}
			clientDM.setLastUpdatedDt(DateUtils.getcurrentdate());
			clientDM.setLastUpdatedBy(username);
			serviceClients.saveOrUpdateClientsDetails(clientDM);
			clientId = clientDM.getClientId();
			new TestingDocuments(hlDocumentLayout, clientId.toString(), "CLIENT");
			hlDocumentLayout.setEnabled(true);
			inform.saveinformation(clientDM.getClientId());
			inform.resetfields();
			comment.save(clientDM.getClientId());
			comment.resetfields();
			document.documentsave(clientDM.getClientId());
			document.ResetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
