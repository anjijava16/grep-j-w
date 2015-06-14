/**
 * File Name 		: ClientAppointments.java 
 * Description 		: this class is used for add/edit Client sub Category details. 
 * Author 			: P Sekhar
 * Date 			: Mar 15, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 Version       Date           	Modified By               Remarks
 * 0.1          JUN 16 2014        	MOHAMED	        Initial Version
 * 0.2			22-July-2014			MOHAMED			Code re-factoring
 */
package com.gnts.crm.txn;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.domain.txn.CampaignDM;
import com.gnts.crm.domain.txn.ClientAppointmentsDM;
import com.gnts.crm.domain.txn.ClientCasesDM;
import com.gnts.crm.domain.txn.ClientsContactsDM;
import com.gnts.crm.domain.txn.LeadsDM;
import com.gnts.crm.domain.txn.OppertunitiesDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.crm.service.txn.CampaignService;
import com.gnts.crm.service.txn.ClientAppointmentsService;
import com.gnts.crm.service.txn.ClientCasesService;
import com.gnts.crm.service.txn.ClientContactsService;
import com.gnts.crm.service.txn.LeadsService;
import com.gnts.crm.service.txn.OppertunityService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.erputil.validations.DateValidation;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.calendar.event.BasicEvent;

public class ClientAppointments extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private LeadsService serviceLead = (LeadsService) SpringContextHelper.getBean("clientLeads");
	private CampaignService serviceCampaign = (CampaignService) SpringContextHelper.getBean("clientCampaign");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private CompanyLookupService serviceCompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private ClientAppointmentsService serviceAppointment = (ClientAppointmentsService) SpringContextHelper
			.getBean("clientAppointment");
	private ClientContactsService serviceClntContact = (ClientContactsService) SpringContextHelper
			.getBean("clientContact");
	private OppertunityService serviceOppertunity = (OppertunityService) SpringContextHelper.getBean("clntOppertunity");
	private ClientCasesService serviceCase = (ClientCasesService) SpringContextHelper.getBean("clientCase");
	private Long companyId, caseid;
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	private HorizontalLayout hlSearchLayout;
	private VerticalLayout vlCalendar, vlFeildsLayout;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	HorizontalLayout hlInput = new HorizontalLayout();
	private String userName, appointId, strWidth = "160px";
	/**
	 * UI Components
	 */
	private TextArea taObjectives, taMeetRemarks;
	private ComboBox cbValues, cbLead, cbClient, cbCampaign, cbCase, cbEmployee, cbContact, cbMeetingType, cbPriority,
			cbMeetingStatus, cbPreviewAppoint, cbOppertunity;
	private TabSheet tabAppointment;
	private PopupDateField dueDate;
	private PopupDateField scheduleDt;
	private EmployeeDM employee;
	private CompanyLookupDM typeLookUp;
	private BeanContainer<Long, LeadsDM> beanLead = null;
	private BeanContainer<Long, CampaignDM> beanCampaign = null;
	private BeanContainer<Long, ClientDM> beanClients = null;
	private BeanContainer<Long, EmployeeDM> beanEmployee = null;
	private BeanContainer<Long, ClientCasesDM> beanClntCase = null;
	private BeanContainer<Long, ClientsContactsDM> beanClntContact = null;
	private BeanContainer<String, CompanyLookupDM> beanCompLookUp = null;
	private BeanItemContainer<ClientAppointmentsDM> beanAppointment = null;
	private BeanContainer<Long, OppertunitiesDM> beanClntOppertunity = null;
	private Long clientId, moduleId;
	private int recordCnt = 0;
	private Logger logger = Logger.getLogger(ClientAppointments.class);
	GregorianCalendar daystart;
	BasicEvent dayEvent;
	Calendar calendar;
	
	public ClientAppointments() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildview();
	}
	
	/**
	 * buildMainview()-->for screen UI design
	 * 
	 * @param clArgumentLayout
	 * @param hlHeaderLayout
	 */
	private void buildview() {
		/**
		 * Declaration add/edit text field and combo box fields
		 */
		cbValues = new GERPComboBox("Type of Contact");
		cbValues.addItem("Client Name");
		cbValues.addItem("Campaign");
		cbValues.addItem("Lead Name");
		cbValues.addItem("Contact Name");
		cbValues.addItem("Client Case");
		cbValues.addItem("Opportunity Provider");
		cbValues.setImmediate(true);
		cbValues.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbValues.getValue() != null) {
					if (cbValues.getValue().equals("Client Name")) {
						cbLead.setVisible(false);
						cbCase.setVisible(false);
						cbContact.setVisible(false);
						cbOppertunity.setVisible(false);
						cbCampaign.setVisible(false);
						cbClient.setVisible(true);
					} else if (cbValues.getValue().equals("Campaign")) {
						cbVisibleFalse();
						cbLead.setVisible(false);
						cbCase.setVisible(false);
						cbContact.setVisible(false);
						cbOppertunity.setVisible(false);
						cbClient.setVisible(false);
						cbCampaign.setVisible(true);
					} else if (cbValues.getValue().equals("Lead Name")) {
						cbCampaign.setVisible(false);
						cbCase.setVisible(false);
						cbContact.setVisible(false);
						cbOppertunity.setVisible(false);
						cbClient.setVisible(false);
						cbLead.setVisible(true);
					} else if (cbValues.getValue().equals("Opportunity Provider")) {
						cbCampaign.setVisible(false);
						cbLead.setVisible(false);
						cbCase.setVisible(false);
						cbContact.setVisible(false);
						cbClient.setVisible(false);
						cbOppertunity.setVisible(true);
					} else if (cbValues.getValue().equals("Client Case")) {
						cbCampaign.setVisible(false);
						cbLead.setVisible(false);
						cbContact.setVisible(false);
						cbOppertunity.setVisible(false);
						cbContact.setVisible(false);
						cbCase.setVisible(true);
					} else {
						cbCampaign.setVisible(false);
						cbLead.setVisible(false);
						cbCase.setVisible(false);
						cbOppertunity.setVisible(false);
						cbClient.setVisible(false);
						cbContact.setVisible(true);
					}
				}
			}
		});
		cbValues.addItem("Client Case");
		cbClient = new GERPComboBox("Client Name");
		cbClient.setItemCaptionPropertyId("clientName");
		cbClient.setNullSelectionAllowed(true);
		cbClient.setWidth(strWidth);
		loadClientsDetails();
		cbCampaign = new GERPComboBox("Campaign");
		cbCampaign.setItemCaptionPropertyId("campaignname");
		cbCampaign.setWidth(strWidth);
		loadClientCampaigns();
		cbCampaign.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbCampaign.setComponentError(null);
				if (cbCampaign.getValue() != null) {
					cbCampaign.setComponentError(null);
				}
			}
		});
		cbLead = new GERPComboBox("Lead Name");
		cbLead.setItemCaptionPropertyId("firstName");
		cbLead.setWidth(strWidth);
		cbLead.setNullSelectionAllowed(true);
		cbLead.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbLead.setComponentError(null);
				if (cbLead.getValue() != null) {
					cbLead.setComponentError(null);
				}
			}
		});
		loadLeadsDetails();
		cbOppertunity = new ComboBox("Opportunity Provider");
		cbOppertunity.setItemCaptionPropertyId("oppertunityName");
		cbOppertunity.setWidth(strWidth);
		loadOpportunityDetails();
		cbCase = new GERPComboBox("Client Case");
		cbCase.setItemCaptionPropertyId("caseTitle");
		cbCase.setImmediate(true);
		cbCase.setWidth(strWidth);
		cbCase.setNullSelectionAllowed(true);
		loadClientCasesList();
		cbContact = new GERPComboBox("Contact Name");
		cbContact.setItemCaptionPropertyId("contactName");
		cbContact.setImmediate(true);
		cbContact.setWidth(strWidth);
		cbContact.setNullSelectionAllowed(true);
		loadClientContactList();
		cbEmployee = new GERPComboBox("Assigned To");
		cbEmployee.setItemCaptionPropertyId("firstname");
		cbEmployee.setWidth(strWidth);
		cbEmployee.setNullSelectionAllowed(false);
		loadEmployeeList();
		cbPreviewAppoint = new GERPComboBox("Previous Appointment");
		cbPreviewAppoint.setItemCaptionPropertyId("scheduleDt");
		cbPreviewAppoint.setImmediate(true);
		cbPreviewAppoint.setWidth(strWidth);
		loadSchedule();
		calendar = new Calendar();
		calendar.setWidth("800px");
		calendar.setHeight("350px");
		// Use US English for date/time representation
		calendar.setLocale(new Locale("en", "US"));
		// Set start date to first date in this month
		GregorianCalendar startDate = new GregorianCalendar();
		startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
		startDate.set(java.util.Calendar.DATE, 1);
		calendar.setStartDate(startDate.getTime());
		// Set end date to last day of this month
		GregorianCalendar endDate = new GregorianCalendar();
		endDate.set(java.util.Calendar.DATE, 1);
		endDate.roll(java.util.Calendar.DATE, -1);
		calendar.setEndDate(endDate.getTime());
		vlCalendar = new VerticalLayout();
		vlCalendar.addComponent(calendar);
		vlCalendar.setComponentAlignment(calendar, Alignment.MIDDLE_CENTER);
		vlCalendar.setSizeFull();
		tabAppointment = new TabSheet();
		tabAppointment.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				// TODO Auto-generated method stub
				if (tabAppointment.getSelectedTab().equals(vlFeildsLayout)) {
				} else if (tabAppointment.getSelectedTab().equals(vlCalendar)) {
				}
			}
		});
		cbClient.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbClient.getItem(itemId);
				if (item != null) {
					cbCampaign.setValue(null);
					cbLead.setValue(null);
					cbCase.setValue(null);
					cbContact.setValue(null);
					cbOppertunity.setValue(null);
				}
			}
		});
		cbCampaign.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbCampaign.getItem(itemId);
				if (item != null) {
					cbClient.setValue(null);
					cbLead.setValue(null);
					cbCase.setValue(null);
					cbContact.setValue(null);
					cbOppertunity.setValue(null);
				}
			}
		});
		cbLead.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbLead.getItem(itemId);
				if (item != null) {
					cbCampaign.setValue(null);
					cbClient.setValue(null);
					cbCase.setValue(null);
					cbContact.setValue(null);
					cbOppertunity.setValue(null);
				}
			}
		});
		cbCase.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbCase.getItem(itemId);
				if (item != null) {
					cbCampaign.setValue(null);
					cbClient.setValue(null);
					cbLead.setValue(null);
					cbContact.setValue(null);
					cbOppertunity.setValue(null);
				}
			}
		});
		cbContact.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbContact.getItem(itemId);
				if (item != null) {
					cbCampaign.setValue(null);
					cbClient.setValue(null);
					cbLead.setValue(null);
					cbCase.setValue(null);
					cbOppertunity.setValue(null);
				}
			}
		});
		cbOppertunity.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbOppertunity.getItem(itemId);
				if (item != null) {
					cbCampaign.setValue(null);
					cbClient.setValue(null);
					cbLead.setValue(null);
					cbCase.setValue(null);
					cbContact.setValue(null);
				}
			}
		});
		taObjectives = new GERPTextArea("Objective");
		taObjectives.setWidth(strWidth);
		taObjectives.setHeight("50px");
		taMeetRemarks = new GERPTextArea("Remarks");
		taMeetRemarks.setWidth("145");
		taMeetRemarks.setHeight("50px");
		scheduleDt = new GERPPopupDateField("Schedule Date");
		scheduleDt.setDateFormat("dd-MMM-yyyy");
		scheduleDt.setLenient(true);
		scheduleDt.setResolution(Resolution.MINUTE);
		scheduleDt.addValidator(new DateValidation("Invalid date entered"));
		scheduleDt.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				scheduleDt.setComponentError(null);
				if (scheduleDt.getValue() != null) {
					scheduleDt.setComponentError(null);
				}
			}
		});
		dueDate = new GERPPopupDateField("Due Date");
		dueDate.setDateFormat("dd-MMM-yyyy");
		// dueDate.addValidator(new DateValidation("Invalid date entered"));
		dueDate.setImmediate(true);
		cbMeetingType = new GERPComboBox("Meeting Type");
		cbMeetingType.setItemCaptionPropertyId("lookupname");
		cbMeetingType.setNullSelectionAllowed(false);
		cbMeetingType.setWidth(strWidth);
		cbMeetingType.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbMeetingType.setComponentError(null);
				if (cbMeetingType.getValue() != null) {
					cbMeetingType.setComponentError(null);
				}
			}
		});
		loadMeetingTypeByLookUpList();
		cbPriority = new GERPComboBox("Priority");
		cbPriority.setItemCaptionPropertyId("lookupname");
		cbPriority.setNullSelectionAllowed(false);
		cbPriority.setWidth("145");
		loadPriorityByLookUpList();
		cbMeetingStatus = new GERPComboBox("Status", BASEConstants.T_CRM_APPOINTMENT, BASEConstants.MEETING_STATUS);
		cbMeetingStatus.setWidth(strWidth);
		cbMeetingStatus.setWidth("145");
		tblMstScrSrchRslt.setSelectable(true);
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		/**
		 * Exporter
		 */
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		hlSearchLayout.removeAllComponents();
		cbClient.setVisible(true);
		cbLead.setVisible(true);
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbClient);
		flColumn2.addComponent(cbLead);
		flColumn3.addComponent(scheduleDt);
		flColumn2.setSpacing(true);
		flColumn2.setMargin(true);
		flColumn4.addComponent(cbMeetingStatus);
		flColumn4.setMargin(true);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		cbLead.setVisible(false);
		cbCase.setVisible(false);
		cbContact.setVisible(false);
		cbOppertunity.setVisible(false);
		cbCampaign.setVisible(false);
		cbClient.setVisible(false);
		hlUserInputLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbValues);
		flColumn1.addComponent(cbClient);
		flColumn1.addComponent(cbCampaign);
		flColumn1.addComponent(cbLead);
		flColumn1.setMargin(true);
		flColumn1.addComponent(cbContact);
		flColumn1.addComponent(cbCase);
		flColumn1.addComponent(cbOppertunity);
		flColumn2.addComponent(cbEmployee);
		flColumn2.addComponent(scheduleDt);
		flColumn2.addComponent(dueDate);
		flColumn2.setSpacing(true);
		flColumn2.setMargin(true);
		flColumn3.addComponent(taObjectives);
		flColumn3.addComponent(cbPreviewAppoint);
		flColumn3.addComponent(cbMeetingType);
		flColumn3.setSpacing(true);
		flColumn3.setMargin(true);
		flColumn4.addComponent(cbPriority);
		flColumn4.addComponent(taMeetRemarks);
		flColumn4.addComponent(cbMeetingStatus);
		hlInput.addComponent(flColumn1);
		hlInput.addComponent(flColumn2);
		hlInput.addComponent(flColumn3);
		hlInput.addComponent(flColumn4);
		tabAppointment.addTab(hlInput, "Appointment");
		tabAppointment.addTab(vlCalendar, "Calendar");
		hlUserInputLayout.setWidth("1200");
		hlUserInputLayout.addComponent(tabAppointment);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	/**
	 * populatedAndConfig()-->this function used to load the list to the table if(search==true)--> it performs search
	 * operation else it loads all values
	 * 
	 * @param search
	 */
	public void loadSrchRslt() {
		try {
			tblMstScrSrchRslt.removeAllItems();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<ClientAppointmentsDM> appointList = new ArrayList<ClientAppointmentsDM>();
			Date Scheduledt = scheduleDt.getValue();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
					+ companyId + ", " + ",");
			appointList = serviceAppointment.getAppointmentDetailList(companyId, null, (Long) cbLead.getValue(),
					(Long) cbClient.getValue(), null, null, null, Scheduledt, (String) cbMeetingStatus.getValue(), "F");
			recordCnt = appointList.size();
			beanAppointment = new BeanItemContainer<ClientAppointmentsDM>(ClientAppointmentsDM.class);
			beanAppointment.addAll(appointList);
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.setContainerDataSource(beanAppointment);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "appointId", "scheduleDt",
					 "meetingType", "meetingStatus", "lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Schedule Date",
					"Meeting Type", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	private void editClientAppointmentDetails() {
		cbVisibleFalse();
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Selected Appointement. Id -> " + appointId);
		if (sltedRcd != null) {
			ClientAppointmentsDM editappointmentlist = beanAppointment.getItem(tblMstScrSrchRslt.getValue()).getBean();
			appointId = editappointmentlist.getAppointId().toString();
			taMeetRemarks.setValue(editappointmentlist.getMeetingRemakrs());
			if (editappointmentlist.getCompaignId() != null) {
				cbValues.setValue("Campaign");
				cbCampaign.setValue(editappointmentlist.getCompaignId());
			}
			if (editappointmentlist.getClientId() != null) {
				cbValues.setValue("Client Name");
				cbClient.setValue(editappointmentlist.getClientId());
			}
			if (editappointmentlist.getLeadId() != null) {
				cbValues.setValue("Lead Name");
				cbLead.setValue(editappointmentlist.getLeadId());
			}
			if (editappointmentlist.getContactId() != null) {
				cbValues.setValue("Contact Name");
				cbContact.setValue(editappointmentlist.getContactId());
			}
			if (editappointmentlist.getClientCaseId() != null) {
				cbValues.setValue("Client Case");
				cbCase.setValue(editappointmentlist.getClientCaseId());
			}
			if (editappointmentlist.getOppertunityid() != null) {
				cbValues.setValue("Opportunity Provider");
				cbOppertunity.setValue(editappointmentlist.getOppertunityid());
			}
			taObjectives.setValue(editappointmentlist.getObjective());
			cbMeetingType.setValue(editappointmentlist.getMeetingType());
			cbPriority.setValue(editappointmentlist.getPriority());
			cbPreviewAppoint.setValue(editappointmentlist.getPrevAppointmentId());
			cbEmployee.setValue(editappointmentlist.getOwnerId());
			scheduleDt.setValue(editappointmentlist.getScheduleDtInt());
			dueDate.setValue(editappointmentlist.getDueDateDt());
			cbMeetingStatus.setValue(editappointmentlist.getMeetingStatus());
		}
	}
	
	/**
	 * load client details based on company id and status
	 */
	private void loadClientsDetails() {
		try {
			List<ClientDM> clientList = serviceClients.getClientDetails(companyId, null, null, null, null, null, null,
					null, "Active", "P");
			beanClients = new BeanContainer<Long, ClientDM>(ClientDM.class);
			beanClients.setBeanIdProperty("clientId");
			beanClients.addAll(clientList);
			cbClient.setContainerDataSource(beanClients);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load Clients Details " + e);
		}
	}
	
	private void loadClientCampaigns() {
		try {
			List<CampaignDM> campaignlist = serviceCampaign.getCampaignDetailList(companyId, null, null, null, null,
					null, null, null, "P");
			beanCampaign = new BeanContainer<Long, CampaignDM>(CampaignDM.class);
			beanCampaign.setBeanIdProperty("campaingnId");
			beanCampaign.addAll(campaignlist);
			cbCampaign.setContainerDataSource(beanCampaign);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load client Campaign List" + e);
		}
	}
	
	private void loadLeadsDetails() {
		try {
			List<LeadsDM> leadList = serviceLead.getLeadsDetailsList(companyId, null, null, "Active", null, "P");
			beanLead = new BeanContainer<Long, LeadsDM>(LeadsDM.class);
			beanLead.setBeanIdProperty("leadId");
			beanLead.addAll(leadList);
			cbLead.setContainerDataSource(beanLead);
		}
		catch (Exception e) {
			logger.info("load leads details" + e);
		}
	}
	
	private void loadOpportunityDetails() {
		List<OppertunitiesDM> clntOppertunity = serviceOppertunity.getClientOppertunityDetails(companyId, null, null,
				null, null, null);
		beanClntOppertunity = new BeanContainer<Long, OppertunitiesDM>(OppertunitiesDM.class);
		beanClntOppertunity.setBeanIdProperty("oppertunityId");
		beanClntOppertunity.addAll(clntOppertunity);
		cbOppertunity.setContainerDataSource(beanClntOppertunity);
	}
	
	private void loadEmployeeList() {
		try {
			List<EmployeeDM> empList = serviceEmployee.getEmployeeList(null, null, null, "Active", companyId, null,
					null, null, null, "F");
			beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(empList);
			cbEmployee.setContainerDataSource(beanEmployee);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadClientContactList() {
		try {
			List<ClientsContactsDM> clientList = serviceClntContact.getClientContactsDetails(companyId, null, clientId,
					null, "Active",null);
			beanClntContact = new BeanContainer<Long, ClientsContactsDM>(ClientsContactsDM.class);
			beanClntContact.setBeanIdProperty("contactId");
			beanClntContact.addAll(clientList);
			cbContact.setContainerDataSource(beanClntContact);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load Clients Contacts Details " + e);
		}
	}
	
	private void loadClientCasesList() {
		try {
			List<ClientCasesDM> caseList = serviceCase.getClientCaseDetails(companyId, caseid, null, null, null,
					"Active", "P");
			beanClntCase = new BeanContainer<Long, ClientCasesDM>(ClientCasesDM.class);
			beanClntCase.setBeanIdProperty("clientCaseId");
			beanClntCase.addAll(caseList);
			cbCase.setContainerDataSource(beanClntCase);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load Clients Case Details " + e);
		}
	}
	
	private void loadMeetingTypeByLookUpList() {
		try {
			List<CompanyLookupDM> compLookUpList = serviceCompany.getCompanyLookUpByLookUp(companyId, moduleId,
					"Active", "CM_MTNGTYP");
			beanCompLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompLookUp.setBeanIdProperty("lookupname");
			beanCompLookUp.addAll(compLookUpList);
			cbMeetingType.setContainerDataSource(beanCompLookUp);
		}
		catch (Exception e) {
			logger.info("load company look up details" + e);
		}
	}
	
	private void loadSchedule() {
		List<ClientAppointmentsDM> scheduledtList = serviceAppointment.getAppointmentDetailList(companyId, null, null,
				null, null, null, null, null, null, "F");
		BeanContainer<Long, ClientAppointmentsDM> beanAppointment = new BeanContainer<Long, ClientAppointmentsDM>(
				ClientAppointmentsDM.class);
		beanAppointment.setBeanIdProperty("appointId");
		beanAppointment.addAll(scheduledtList);
		cbPreviewAppoint.setContainerDataSource(beanAppointment);
	}
	
	private void loadPriorityByLookUpList() {
		try {
			List<CompanyLookupDM> priorityLookUpList = serviceCompany.getCompanyLookUpByLookUp(companyId, moduleId,
					"Active", "CM_MTNGPRY");
			beanCompLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompLookUp.setBeanIdProperty("lookupname");
			beanCompLookUp.addAll(priorityLookUpList);
			cbPriority.setContainerDataSource(beanCompLookUp);
		}
		catch (Exception e) {
			logger.info("load company look up details" + e);
		}
	}
	
	private void laodCalendarviewList() {
		try {
			List<ClientAppointmentsDM> appointList = serviceAppointment.getAppointmentDetailList(companyId, null, null,
					clientId, null, null, null, null, "Active", null);
			beanAppointment = new BeanItemContainer<ClientAppointmentsDM>(ClientAppointmentsDM.class);
			beanAppointment.addAll(appointList);
			cbPreviewAppoint.setContainerDataSource(beanAppointment);
			for (ClientAppointmentsDM object : appointList) {
				java.util.Calendar cal = GregorianCalendar.getInstance();
				cal.setTime(new Date(object.getScheduleDt()));
				dayEvent = new BasicEvent("*", "This is the Day", cal.getTime(), cal.getTime());
				System.out.println("Date Utils" + cal.getTime());
				dayEvent.setAllDay(true);
				calendar.addEvent(dayEvent);
			}
		}
		catch (Exception e) {
			logger.info("load Calendar view Details " + e);
		}
	}
	
	/*
	 * buildNotifications()-->this method is used for popup view for Download components
	 * @param event
	 */
	/**
	 * this method used to download table grid view data
	 */
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
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		scheduleDt.setRequired(true);
		taObjectives.setRequired(true);
		cbMeetingStatus.setRequired(true);
		cbMeetingType.setRequired(true);
		cbValues.setRequired(true);
		hlCmdBtnLayout.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tblMstScrSrchRslt.setVisible(false);
	}
	
	@Override
	protected void editDetails() {
		scheduleDt.setRequired(true);
		taObjectives.setRequired(true);
		cbMeetingStatus.setRequired(true);
		cbMeetingType.setRequired(true);
		cbValues.setRequired(true);
		hlCmdBtnLayout.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		editClientAppointmentDetails();
		tblMstScrSrchRslt.setVisible(false);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean errorflag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if (taObjectives.getValue() == "") {
			taObjectives.setComponentError(new UserError(GERPErrorCodes.NULL_OBJECTIVE));
			errorflag = true;
		} else {
			taObjectives.setComponentError(null);
		}
		if (cbMeetingType.getValue() == null) {
			cbMeetingType.setComponentError(new UserError(GERPErrorCodes.NULL_MEETING_TYPE));
			errorflag = true;
		} else {
			cbMeetingType.setComponentError(null);
		}
		if (cbMeetingStatus.getValue() == null) {
			cbMeetingStatus.setComponentError(new UserError(GERPErrorCodes.NULL_MEETING_STATUS));
			errorflag = true;
		} else {
			cbMeetingStatus.setComponentError(null);
		}
		if (scheduleDt.getValue() == null) {
			scheduleDt.setComponentError(new UserError(GERPErrorCodes.NULL_MEETING_SCHEDULE_DT));
			errorflag = true;
		} else {
			cbMeetingStatus.setComponentError(null);
		}
		if (cbValues.getValue() == null) {
			cbValues.setComponentError(new UserError(GERPErrorCodes.CB_VALUES));
			errorflag = true;
		} else {
			cbValues.setComponentError(null);
		}
		if ((scheduleDt.getValue() != null) || (dueDate.getValue() != null)) {
			if (scheduleDt.getValue().after(dueDate.getValue())) {
				dueDate.setComponentError(new UserError(GERPErrorCodes.CRM_DATE_OUTOFRANGE));
				logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
						+ "Throwing ValidationException. User data is > " + scheduleDt.getValue());
				errorflag = true;
			} else {
				dueDate.setComponentError(null);
			}
		}
		if (errorflag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
			ClientAppointmentsDM saveAppointment = new ClientAppointmentsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				saveAppointment = beanAppointment.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (cbClient.getValue() != null) {
				saveAppointment.setClientId(Long.valueOf(cbClient.getValue().toString()));
			}
			if (cbCampaign.getValue() != null) {
				saveAppointment.setCompaignId((Long) cbCampaign.getValue());
			}
			if (cbLead.getValue() != null) {
				saveAppointment.setLeadId(Long.valueOf(cbLead.getValue().toString()));
			}
			if (cbContact.getValue() != null) {
				saveAppointment.setContactId(Long.valueOf(cbContact.getValue().toString()));
			}
			saveAppointment.setClientCaseId((Long) cbCase.getValue());
			saveAppointment.setOppertunityid((Long) cbOppertunity.getValue());
			if (cbEmployee.getValue() != null) {
				saveAppointment.setOwnerId((Long) cbEmployee.getValue());
			}
			saveAppointment.setScheduleDt(scheduleDt.getValue());
			saveAppointment.setDueDate(dueDate.getValue());
			if (saveAppointment.getPrevAppointmentId() != null) {
				saveAppointment.setPrevAppointmentId((Long) cbPreviewAppoint.getValue());
			}
			if (cbMeetingType.getValue() != null) {
				saveAppointment.setMeetingType((String) cbMeetingType.getValue());
			}
			saveAppointment.setLastUpdatedBy(userName);
			saveAppointment.setLastUpdatedDt(DateUtils.getcurrentdate());
			saveAppointment.setMeetingRemakrs(taMeetRemarks.getValue());
			saveAppointment.setMeetingStatus((String) cbMeetingStatus.getValue());
			if (typeLookUp != null) {
				saveAppointment.setMeetingType(typeLookUp.getLookupname());
			}
			saveAppointment.setObjective(taObjectives.getValue());
			if (employee != null) {
				saveAppointment.setOwnerId((Long) cbEmployee.getValue());
			}
			if (cbPriority.getValue() != null) {
				saveAppointment.setPriority(cbPriority.getValue().toString());
			}
			saveAppointment.setCompanyId(companyId);
			saveAppointment.setPrevAppointmentId((Long) cbPreviewAppoint.getValue());
			serviceAppointment.saveOrUpdateAppointmentdetails(saveAppointment);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info("saveOrUpdateAppointmentdetails" + e);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for client appointement. ID " + appointId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_CRM_APPOINTMENT);
		UI.getCurrent().getSession().setAttribute("audittablepk", appointId);
	}
	
	@Override
	protected void cancelDetails() {
		scheduleDt.setRequired(false);
		taObjectives.setRequired(false);
		cbMeetingStatus.setRequired(false);
		cbMeetingType.setRequired(false);
		cbValues.setRequired(false);
		cbMeetingStatus.setComponentError(null);
		scheduleDt.setComponentError(null);
		taObjectives.setComponentError(null);
		cbMeetingType.setComponentError(null);
		cbValues.setComponentError(null);
		hlCmdBtnLayout.setVisible(true);
		resetFields();
		assembleSearchLayout();
		tblMstScrSrchRslt.setVisible(true);
	}
	
	/**
	 * resetFields()->this method is used for reset the add/edit UI components
	 */
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
		taMeetRemarks.setValue("");
		taObjectives.setValue("");
		cbValues.setValue(null);
		cbClient.setValue(null);
		cbClient.setComponentError(null);
		cbCampaign.setValue(null);
		cbCampaign.setComponentError(null);
		cbCase.setValue(null);
		cbClient.setValue(null);
		cbContact.setValue(null);
		cbEmployee.setValue(null);
		cbEmployee.setComponentError(null);
		cbLead.setValue(null);
		cbLead.setComponentError(null);
		cbMeetingStatus.setValue(null);
		cbMeetingType.setValue(null);
		cbMeetingType.setComponentError(null);
		cbValues.setComponentError(null);
		cbPriority.setValue(null);
		cbPriority.setComponentError(null);
		scheduleDt.setValue(null);
		scheduleDt.setComponentError(null);
		dueDate.setValue(null);
		dueDate.setComponentError(null);
		cbPreviewAppoint.setValue(null);
		cbOppertunity.setValue(null);
		cbPriority.setValue(cbPriority.getItemIds().iterator().next());
	}
	
	public void cbVisibleFalse() {
		cbCampaign.setVisible(false);
		cbLead.setVisible(false);
		cbCase.setVisible(false);
		cbContact.setVisible(false);
		cbOppertunity.setVisible(false);
		cbClient.setVisible(false);
	}
	
	public void cbReset() {
		cbCampaign.setValue(null);
		cbLead.setValue(null);
		cbCase.setValue(null);
		cbContact.setValue(null);
		cbOppertunity.setValue(null);
		cbClient.setValue(null);
	}
}