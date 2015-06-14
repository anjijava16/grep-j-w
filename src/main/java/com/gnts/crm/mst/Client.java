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
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.CityService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.base.service.mst.StateService;
import com.gnts.crm.domain.mst.ClientCategoryDM;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.domain.mst.ClientSubCategoryDM;
import com.gnts.crm.domain.txn.CampaignDM;
import com.gnts.crm.domain.txn.CommentsDM;
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
import com.gnts.erputil.components.GERPButton;
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
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
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
	CityService servicecity = (CityService) SpringContextHelper.getBean("city");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	private CountryService servicecountry = (CountryService) SpringContextHelper.getBean("country");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private LeadsService serviceLead = (LeadsService) SpringContextHelper.getBean("clientLeads");
	private ClientCategoryService serviceClientCat = (ClientCategoryService) SpringContextHelper
			.getBean("clientCategory");
	private ClientSubCategoryService serviceClientSubCat = (ClientSubCategoryService) SpringContextHelper
			.getBean("clientSubCategory");
	private CompanyLookupService servicecompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private CampaignService serviceCampaign = (CampaignService) SpringContextHelper.getBean("clientCampaign");
	// form layout for input controls
	private FormLayout formLayout1, formLayout2, formLayout3, formLayout4;
	private TabSheet maintab = new TabSheet();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlCommetTblLayout = new VerticalLayout();
	private VerticalLayout vlDocumentLayout = new VerticalLayout();
	private VerticalLayout vlinformTblLayout = new VerticalLayout();
	private HorizontalLayout hlInput;
	private VerticalLayout hlUserInput;
	// User Input Components
	private TextField tfClntName, tfRevenue, tfpostcd, tfphnno, tffaxno, tfEmail, tfWebsite, tfotherDetails,
			tfclntcode;
	private TextArea taClntAddrss;
	private ComboBox cbClntCategory, cbClntSubCategory, cbCampaign, cbLeads, cbAssignedto, cbClientrate, cbCountry,
			cbState, cbcity, cbclntindustry, cbClntStatus;
	// Bean Container
	private BeanContainer<Long, CountryDM> beanCountry = null;
	private BeanContainer<Long, CityDM> beanCity = null;
	private BeanItemContainer<ClientDM> beanClnt = null;
	private BeanContainer<Long, ClientCategoryDM> beanCat;
	private BeanContainer<Long, ClientSubCategoryDM> beanSubCat = null;
	private BeanContainer<Long, EmployeeDM> beanemploye = null;
	private BeanContainer<Long, CampaignDM> beancampaign = null;
	BeanItemContainer<CommentsDM> beanCmmnt;
	private BeanContainer<String, CompanyLookupDM> beanlook = null;
	private BeanContainer<Long, LeadsDM> beanLead = null;
	// local variables declaration
	private Long companyid;
	Long countryid, clientcatid;
	Long stateid, lookupid;
	Long cityid, clientsucatid;
	private Long employeeid, clientId, moduleid, branchid;
	private int recordCnt = 0;
	private String username;
	Comments comment;
	Documents document;
	public Button btndelete = new GERPButton("Delete", "delete", this);
	ClientInformation inform;
	// Initialize the logger
	private Logger logger = Logger.getLogger(Client.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Client() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeid = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		// countryid = Long.valueOf(UI.getCurrent().getSession().getAttribute("countryid").toString());
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
		tfclntcode = new GERPTextField("Client Code");
		tfclntcode.setRequired(true);
		// Revenue text Field
		tfRevenue = new GERPTextField("Revenue");
		tfRevenue.setRequired(false);
		tfRevenue.setValue("0");
		tfRevenue.setWidth("150");
		tfRevenue.setMaxLength(24);
		// post code text Field
		tfpostcd = new GERPTextField("Post Code");
		tfpostcd.setRequired(false);
		tfpostcd.setMaxLength(25);
		// phone number text Field
		tfphnno = new GERPTextField("Phone Number");
		tfphnno.setRequired(true);
		tfphnno.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfphnno.setComponentError(null);
				if (tfphnno.getValue() != null) {
					if (!tfphnno.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
						tfphnno.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
					} else {
						tfphnno.setComponentError(null);
					}
				}
			}
		});
		// tfphnno.addValidator(new PhoneNumberValidation("Enter Correct phone no"));
		tfphnno.setMaxLength(25);
		// fax number text Field
		tffaxno = new GERPTextField("Fax Number");
		tffaxno.setRequired(false);
		tffaxno.setMaxLength(25);
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
		tfotherDetails = new GERPTextField("Other Details");
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
		cbcity = new GERPComboBox("City");
		cbcity.setItemCaptionPropertyId("cityname");
		cbcity.setWidth("150");
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
		formLayout1.addComponent(tfclntcode);
		formLayout1.addComponent(tfClntName);
		tfClntName.setRequired(true);
		formLayout1.addComponent(taClntAddrss);
		formLayout1.addComponent(cbClntCategory);
		cbClntCategory.setRequired(true);
		formLayout2.addComponent(cbClntSubCategory);
		formLayout2.addComponent(cbCountry);
		formLayout2.addComponent(cbState);
		formLayout2.addComponent(cbcity);
		formLayout2.addComponent(tfpostcd);
		formLayout2.addComponent(tfphnno);
		formLayout3.addComponent(tfEmail);
		cbCountry.setRequired(true);
		formLayout4.addComponent(tffaxno);
		formLayout4.addComponent(tfWebsite);
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
		// maintab.addTab(hlInput, "Clients");
		// maintab.addTab(");
		TabSheet test3 = new TabSheet();
		test3.addTab(vlinformTblLayout, " Client Information");
		test3.addTab(vlCommetTblLayout, "Comments");
		test3.addTab(vlDocumentLayout, "Documents");
		test3.setWidth("1370");
		// hlUserInput.addComponent(maintab);
		hlUserInputLayout.addComponent(hlUserInput);
		hlUserInput.addComponent(test3);
		// build search layout
	}
	
	private void loadCountryList() {
		try {
			List<CountryDM> getCountrylist = servicecountry.getCountryList(null, null, null, null, "Active", "F");
			beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
			beanCountry.setBeanIdProperty("countryID");
			beanCountry.addAll(getCountrylist);
			cbCountry.setContainerDataSource(beanCountry);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	private void loadStateList() {
		try {
			List<StateDM> getStateList = serviceState.getStateList(null, "Active", (Long) cbCountry.getValue(), null,
					"F");
			BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
			beanState.setBeanIdProperty("stateId");
			beanState.addAll(getStateList);
			cbState.setContainerDataSource(beanState);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	private void loadCityList() {
		try {
			List<CityDM> getCitylist = servicecity.getCityList(null, null, Long.valueOf(cbState.getValue().toString()),
					"Active", companyid, "F");
			beanCity = new BeanContainer<Long, CityDM>(CityDM.class);
			beanCity.setBeanIdProperty("cityid");
			beanCity.addAll(getCitylist);
			cbcity.setContainerDataSource(beanCity);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	private void loadEmployeeList() {
		try {
			List<EmployeeDM> empList = serviceEmployee.getEmployeeList(null, null, null, "Active", companyid,
					employeeid, null, null, null, "F");
			beanemploye = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanemploye.setBeanIdProperty("employeeid");
			beanemploye.addAll(empList);
			cbAssignedto.setContainerDataSource(beanemploye);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadClientCategoryList() {
		try {
			List<ClientCategoryDM> clntCatList = serviceClientCat.getCrmClientCategoryList(companyid, null, "Active",
					"F");
			beanCat = new BeanContainer<Long, ClientCategoryDM>(ClientCategoryDM.class);
			beanCat.setBeanIdProperty("clientCategoryId");
			beanCat.addAll(clntCatList);
			cbClntCategory.setContainerDataSource(beanCat);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load client category List" + e);
		}
	}
	
	private void loadClientSubCategoryList() {
		try {
			System.out.println("Client category id " + cbClntSubCategory.getValue());
			List<ClientSubCategoryDM> getsubcatList = serviceClientSubCat.getClientSubCategoryList(companyid, null,
					null, "Active", (Long) cbClntCategory.getValue(), "F");
			beanSubCat = new BeanContainer<Long, ClientSubCategoryDM>(ClientSubCategoryDM.class);
			beanSubCat.setBeanIdProperty("clientSubCatId");
			beanSubCat.addAll(getsubcatList);
			cbClntSubCategory.setContainerDataSource(beanSubCat);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load client sub category List" + e);
		}
	}
	
	private void loadClientCampaigns() {
		try {
			List<CampaignDM> campaignlist = serviceCampaign.getCampaignDetailList(companyid, null, null, null, null,
					null, null, null, "F");
			beancampaign = new BeanContainer<Long, CampaignDM>(CampaignDM.class);
			beancampaign.setBeanIdProperty("campaingnId");
			beancampaign.addAll(campaignlist);
			cbCampaign.setContainerDataSource(beancampaign);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load client Campaign List" + e);
		}
	}
	
	private void loadLeadsDetails() {
		try {
			List<LeadsDM> leadList = serviceLead.getLeadsDetailsList(companyid, null, null, "Active", null, "P");
			beanLead = new BeanContainer<Long, LeadsDM>(LeadsDM.class);
			beanLead.setBeanIdProperty("leadId");
			beanLead.addAll(leadList);
			cbLeads.setContainerDataSource(beanLead);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load leads details" + e);
		}
	}
	
	private void loadcompanyUpList() {
		try {
			List<CompanyLookupDM> LookUpList = servicecompany.getCompanyLookUpByLookUp(companyid, moduleid, "Active",
					"CM_CLNTRTG");
			beanlook = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanlook.setBeanIdProperty("lookupname");
			beanlook.addAll(LookUpList);
			cbClientrate.setContainerDataSource(beanlook);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load company look up details" + e);
		}
	}
	
	private void loadLookUpList() {
		try {
			List<CompanyLookupDM> LookList = servicecompany.getCompanyLookUpByLookUp(companyid, moduleid, "Active",
					"CM_CLNTIND");
			beanlook = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanlook.setBeanIdProperty("lookupname");
			beanlook.addAll(LookList);
			cbclntindustry.setContainerDataSource(beanlook);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load company look up details" + e);
		}
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<ClientDM> ClntList = new ArrayList<ClientDM>();
			/*
			 * if (cbCountry.getValue() != null) { countryid = ((Long) cbCountry.getValue()); } if
			 * (cbClntSubCategory.getValue() != null) { clientsucatid = ((Long) cbClntSubCategory.getValue()); }
			 */
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfClntName.getValue() + ", " + (String) cbClntStatus.getValue());
			ClntList = serviceClients.getClientDetails(companyid, null, (Long) cbClntCategory.getValue(),
					(Long) cbClntSubCategory.getValue(), null, null, null, (String) tfClntName.getValue(),
					(String) cbClntStatus.getValue(), "F");
			recordCnt = ClntList.size();
			System.out.println("LISYYYYY" + recordCnt);
			beanClnt = new BeanItemContainer<ClientDM>(ClientDM.class);
			beanClnt.addAll(ClntList);
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
			e.printStackTrace();
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfClntName.setValue("");
		tfClntName.setComponentError(null);
		tfclntcode.setComponentError(null);
		tfclntcode.setValue("");
		taClntAddrss.setValue("");
		tfEmail.setValue("");
		tfEmail.setComponentError(null);
		tffaxno.setValue("");
		tfotherDetails.setValue("");
		tfpostcd.setValue("");
		tfphnno.setValue("");
		tfphnno.setComponentError(null);
		tfRevenue.setValue("0");
		tfWebsite.setValue("");
		cbLeads.setValue(null);
		cbAssignedto.setValue(null);
		cbCampaign.setValue(null);
		cbcity.setValue(null);
		cbState.setValue(null);
		cbCountry.setValue(countryid);
		cbCountry.setRequired(false);
		cbState.setRequired(false);
		cbcity.setRequired(false);
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		clientId = (Long) sltedRcd.getItemProperty("clientId").getValue();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Dept. Id -> "
				+ clientId);
		beanCmmnt = new BeanItemContainer<CommentsDM>(CommentsDM.class);
		if (sltedRcd != null) {
			ClientDM editClientlist = beanClnt.getItem(tblMstScrSrchRslt.getValue()).getBean();
			tfClntName.setValue(editClientlist.getClientName());
			taClntAddrss.setValue(editClientlist.getClientAddress());
			cbClntCategory.setValue(editClientlist.getClientCatId());
			cbClntSubCategory.setValue(editClientlist.getClientSubCatId());
			cbCampaign.setValue(editClientlist.getCampaignId());
			cbAssignedto.setValue(editClientlist.getAssignedTo());
			cbClientrate.setValue(editClientlist.getClinetRating());
			if (editClientlist.getRevenue() != null && !equals(editClientlist.getRevenue())) {
				tfRevenue.setValue(editClientlist.getRevenue().toString());
			}
			cbclntindustry.setValue(editClientlist.getClientIndustry());
			if (editClientlist.getClientCode() != null) {
				tfclntcode.setValue(editClientlist.getClientCode());
			}
			System.out.println("tfclntcode-->>>" + editClientlist.getClientCode());
			cbLeads.setValue(editClientlist.getLeadId());
			tfEmail.setValue(editClientlist.getEmailId());
			tffaxno.setValue(editClientlist.getFaxNo());
			tfphnno.setValue(editClientlist.getPhoneNo());
			tfpostcd.setValue(editClientlist.getPostalCode());
			tfWebsite.setValue(editClientlist.getWebsite());
			tfotherDetails.setValue(editClientlist.getOtherDetails());
			cbCountry.setValue((editClientlist.getCountryId()));
			cbState.setValue(editClientlist.getStateId().toString());
			cbcity.setValue(editClientlist.getCityId().toString());
		}
		comment = new Comments(vlCommetTblLayout, employeeid, null, null, clientId, null, null, null);
		document = new Documents(vlDocumentLayout, null, null, clientId, null, null, null);
		inform = new ClientInformation(vlinformTblLayout, clientId);
		comment.loadsrch(true, clientId, null, null, null, null, null);
		document.loadsrcrslt(true, clientId, null, null, null, null, null);
		inform.loadsrch(true, clientId);
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
		// hlUserInput.removeAllComponents();
		hlCmdBtnLayout.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tfClntName.setRequired(true);
		tblMstScrSrchRslt.setValue(null);
		tblMstScrSrchRslt.setVisible(false);
		inform = new ClientInformation(vlinformTblLayout, clientId);
		comment = new Comments(vlCommetTblLayout, employeeid, null, null, clientId, null, null, null);
		document = new Documents(vlDocumentLayout, null, null, clientId, null, null, null);
		// reset the input controls to default value
		resetFields();
		cbCountry.setRequired(true);
		cbState.setRequired(true);
		cbcity.setRequired(true);
		/*
		 * List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid, "CM_CLNTCD "); for
		 * (SlnoGenDM slnoObj : slnoList) { if (slnoObj.getAutoGenYN().equals("Y")) { tfclntcode.setReadOnly(true); }
		 * else { tfclntcode.setReadOnly(false); } }
		 */
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
		/*
		 * List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid, "CM_CLNTCD "); for
		 * (SlnoGenDM slnoObj : slnoList) { if (slnoObj.getAutoGenYN().equals("Y")) { tfclntcode.setReadOnly(true); } }
		 * if (tfclntcode.getValue() == null || tfclntcode.getValue().trim().length() == 0) {
		 * tfclntcode.setReadOnly(false); }
		 */
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
		tfclntcode.setComponentError(null);
		cbClntCategory.setComponentError(null);
		tfphnno.setComponentError(null);
		cbCountry.setComponentError(null);
		cbState.setComponentError(null);
		cbcity.setComponentError(null);
		if ((tfClntName.getValue() == null) || tfClntName.getValue().trim().length() == 0) {
			tfClntName.setComponentError(new UserError(GERPErrorCodes.NULL_CLNT_NAME));
			errorFlag = true;
		}
		if ((tfclntcode.getValue() == null) || tfclntcode.getValue().trim().length() == 0) {
			tfclntcode.setComponentError(new UserError(GERPErrorCodes.NULL_CLNT_CODE));
			errorFlag = true;
		}
		if (cbClntCategory.getValue() == null) {
			cbClntCategory.setComponentError(new UserError(GERPErrorCodes.NULL_CLNT_CATGRY_NAME));
			errorFlag = true;
		}
		if ((tfphnno.getValue() == null) || tfphnno.getValue().trim().length() == 0) {
			tfphnno.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
			errorFlag = true;
		}
		if (cbState.getValue() == null) {
			cbState.setComponentError(new UserError(GERPErrorCodes.NULL_STATE_NAME));
			errorFlag = true;
		}
		if (cbcity.getValue() == null) {
			cbcity.setComponentError(new UserError(GERPErrorCodes.NULL_CITY_NAME));
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
			ClientDM Clntobj = new ClientDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				Clntobj = beanClnt.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			Clntobj.setClientCode(tfclntcode.getValue());
			Clntobj.setCompanyId(companyid);
			Clntobj.setClientName(tfClntName.getValue().toString());
			Clntobj.setClientAddress(taClntAddrss.getValue());
			Clntobj.setClientCatId((Long) cbClntCategory.getValue());
			Clntobj.setClientSubCatId((Long) cbClntSubCategory.getValue());
			Clntobj.setCampaignId((Long) cbCampaign.getValue());
			Clntobj.setLeadId((Long) cbLeads.getValue());
			System.out.println("LeadIDloader" + cbLeads.getValue());
			if (tfRevenue.getValue() != "" && tfRevenue.getValue().toString().trim().length() > 0) {
				Clntobj.setRevenue(Long.valueOf(tfRevenue.getValue()));
			}
			Clntobj.setAssignedTo((Long) cbAssignedto.getValue());
			Clntobj.setPostalCode(tfpostcd.getValue());
			Clntobj.setPhoneNo(tfphnno.getValue());
			Clntobj.setFaxNo(tffaxno.getValue());
			Clntobj.setEmailId(tfEmail.getValue());
			Clntobj.setWebsite(tfWebsite.getValue());
			Clntobj.setOtherDetails(tfotherDetails.getValue());
			if (cbClientrate.getValue() != null) {
				Clntobj.setClinetRating(cbClientrate.getValue().toString());
			}
			if (cbclntindustry.getValue() != null) {
				Clntobj.setClientIndustry(cbclntindustry.getValue().toString());
			}
			if (cbCountry.getValue() != null) {
				Clntobj.setCountryId((Long) cbCountry.getValue());
			}
			logger.info(" (Long) cbCountryName.getValue() is > " + cbCountry.getValue());
			if (cbState.getValue() != null) {
				Clntobj.setStateId((Long.valueOf(cbState.getValue().toString())));
			}
			logger.info(" (Long) cbState.getValue() is > " + cbState.getValue());
			if (cbcity.getValue() != null) {
				Clntobj.setCityId((Long.valueOf(cbcity.getValue().toString())));
			}
			if (cbClntStatus.getValue() != null) {
				Clntobj.setClientSttus(cbClntStatus.getValue().toString());
			}
			Clntobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			Clntobj.setLastUpdatedBy(username);
			serviceClients.saveOrUpdateClientsDetails(Clntobj);
			inform.saveinformation(Clntobj.getClientId());
			System.out.println("saveinformation..>>>" + Clntobj.getClientId());
			inform.resetfields();
			comment.save(Clntobj.getClientId());
			comment.resetfields();
			document.documentsave(Clntobj.getClientId());
			document.ResetFields();
			/*
			 * if (tblMstScrSrchRslt.getValue() == null) { List<SlnoGenDM> slnoList =
			 * serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid, "CM_CLNTCD");
			 * System.out.println("iiiid===>" + companyid + "," + branchid + "," + moduleid); for (SlnoGenDM slnoObj :
			 * slnoList) { if (slnoObj.getAutoGenYN().equals("Y")) { serviceSlnogen.updateNextSequenceNumber(companyid,
			 * branchid, moduleid, "CM_CLNTCD"); } } }
			 */
			System.out.println("CLIENTS->>" + Clntobj.getClientId());
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
