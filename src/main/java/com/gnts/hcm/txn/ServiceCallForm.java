/**
 * File Name 	 :   ServiceCallForm.java 
 * Description 	 :   This Screen Purpose for Modify the ServiceCallForm Details.Add the ServiceCallForm details and Specification process should be directly added in DB.
 * Author 		 :   Arun Jeyaraj R
 * Date 		 :   18-Aug-2015
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS Technologies pvt. ltd.
 * 
 * Version  Date           Modified By        Remarks
 * 0.1      18-Aug-2015   Arun Jeyaraj R    Initial Version
 */
package com.gnts.hcm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.sms.domain.txn.ServiceCallFormDM;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsEnquiryDtlDM;
import com.gnts.sms.domain.txn.SmsPOHdrDM;
import com.gnts.sms.service.txn.ServiceCallFormService;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsEnquiryDtlService;
import com.gnts.sms.service.txn.SmsPOHdrService;
import com.gnts.sms.txn.SmsEnquiry;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ServiceCallForm extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private ServiceCallFormService serviveCallFormService = (ServiceCallFormService) SpringContextHelper
			.getBean("sercallForm");
	private WorkOrderHdrService serviceWorkOrderHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private SmsPOHdrService servicePurchaseOrdHdr = (SmsPOHdrService) SpringContextHelper.getBean("smspohdr");
	private SmsEnquiryDtlService serviceEnqDtls = (SmsEnquiryDtlService) SpringContextHelper.getBean("SmsEnquiryDtl");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private SmsEnqHdrService serviceEnqHdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	// Initialize the logger
	private Logger logger = Logger.getLogger(SmsEnquiry.class);
	// User Input Fields for Sales Enquiry Header
	private TextField tfslno, tfclientname, tfClientCity, tfAlloDays, tfpersonsAllo, tftravelmode, tfExpanses,
			tfnoofdaysWrkd;
	private GERPTextArea tfserdesc;
	private TextArea taCustomerDetails, taserviceProblmRep, tarootcauseanl, tacorrectiveaction, taprevaction;
	private PopupDateField dfdate, dfenddate, dfstartdt;
	private ComboBox cbBranch, cbtype, cbClient, cbWorkOrderNo, cbmarkstatus, cbprodstatus, cbqcstatus,
			cbEnquiryNumber, cbPONumber, cbinfnRecBy, cbDragNo;
	private BeanItemContainer<ServiceCallFormDM> beanServiceCallForm = null;
	private GERPTextField tfClntName = new GERPTextField("Client Name");
	private GERPTextField tfClientCode = new GERPTextField("Client Code");
	// User Input Fields for Sales Enquiry Specification
	private TextArea taMatReq;
	// form layout for input controls Sales Enquiry Header
	private FormLayout flcol1, flcol2, flcol3, flcol4, flcol5;
	// form layout for input controls Sales Enquiry Deatil
	private FormLayout fldtl1;
	// form layout for input controls Sales Enquiry Specification
	private FormLayout flspec1;
	private FormLayout fldoc1, fldoc2;
	private FormLayout flqccont;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls Sales Enquiry Header
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// Parent layout for all the input controls Sales Enquiry Detail
	private HorizontalLayout hldtllayout = new HorizontalLayout();
	private HorizontalLayout hlQcdtl = new HorizontalLayout();
	private VerticalLayout vldtl = new VerticalLayout();
	private VerticalLayout vlQcdetl = new VerticalLayout();
	// Parent layout for all the input controls Sales Enquiry Specification
	private HorizontalLayout hlspecadd = new HorizontalLayout();
	private HorizontalLayout hlspecadd1 = new HorizontalLayout();
	private HorizontalLayout hlqccontrol = new HorizontalLayout();
	private HorizontalLayout hlspec = new HorizontalLayout();
	private HorizontalLayout hldoc = new HorizontalLayout();
	private VerticalLayout vlspec = new VerticalLayout();
	private VerticalLayout vldoc = new VerticalLayout();
	// Parent layout for all the input controls Sms Comments
	// Document Layout
	private VerticalLayout hlDocumentLayout = new VerticalLayout();
	// local variables declaration
	private Long serCallFormId;
	private String username;
	private Long companyid, moduleId;
	private int recordCnt = 0;
	private Long branchId;
	private TabSheet dtlTab;
	
	// Constructor received the parameters from Login UI class
	public ServiceCallForm() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside SmsEnquiry() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting Sales Enquiry UI");
		// Sales Enquiry Header Components Definition
		tfClntName.setRequired(true);
		tfClientCode.setRequired(true);
		// tfEmail.setRequired(true);
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setImmediate(true);
		cbBranch.setNullSelectionAllowed(false);
		cbBranch.setWidth("130");
		loadBranchList();
		tfslno = new GERPTextField("Serial No");
		tfslno.setWidth("130");
		tfslno.setReadOnly(false);
		cbClient = new GERPComboBox("Client Code");
		cbClient.setItemCaptionPropertyId("clientCode");
		cbClient.setImmediate(true);
		cbClient.setNullSelectionAllowed(false);
		cbClient.setWidth("150");
		cbmarkstatus = new GERPComboBox("Status", BASEConstants.T_SMS_SER_CALL_FORM, BASEConstants.T_SMS_SER_CALL_FORM);
		cbmarkstatus.setWidth("130");
		cbprodstatus = new GERPComboBox("Status", BASEConstants.T_SMS_SER_CALL_FORM1,
				BASEConstants.T_SMS_SER_CALL_FORM1);
		cbprodstatus.setWidth("130");
		cbqcstatus = new GERPComboBox("Status", BASEConstants.T_SMS_SER_CALL_FORM1, BASEConstants.T_SMS_SER_CALL_FORM1);
		cbqcstatus.setWidth("130");
		cbinfnRecBy = new GERPComboBox("Information Rec.BY");
		cbinfnRecBy.setItemCaptionPropertyId("lookupname");
		cbinfnRecBy.setImmediate(true);
		cbinfnRecBy.setWidth("120");
		loadInfrnRecBy();
		cbDragNo = new GERPComboBox("Drawing No");
		cbDragNo.setItemCaptionPropertyId("customField2");
		cbDragNo.setImmediate(true);
		cbDragNo.setNullSelectionAllowed(false);
		cbDragNo.setWidth("120");
		cbPONumber = new GERPComboBox("Purchase Order No.");
		cbPONumber.setWidth("140");
		cbPONumber.setItemCaptionPropertyId("pono");
		cbPONumber.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadWorkOrderNo();
				}
			}
		});
		cbEnquiryNumber = new ComboBox("Enquiry No.");
		cbEnquiryNumber = new ComboBox("Enquiry No");
		cbEnquiryNumber.setItemCaptionPropertyId("enquiryNo");
		cbEnquiryNumber.setWidth("150");
		loadEnquiryNo();
		cbEnquiryNumber.setImmediate(true);
		cbEnquiryNumber.setImmediate(true);
		cbEnquiryNumber.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnquiryNumber.getItem(itemId);
				if (item != null) {
					loaddrawingNo();
					loadClientList();
					loadPurchaseOrdNo();
				}
			}
		});
		cbWorkOrderNo = new GERPComboBox("WO.No");
		cbWorkOrderNo.setItemCaptionPropertyId("workOrdrNo");
		cbWorkOrderNo.setWidth("140");
		tfclientname = new GERPTextField("Client Name");
		tfclientname.setReadOnly(true);
		tfClientCity = new GERPTextField("Client City");
		tfClientCity.setReadOnly(true);
		tfAlloDays = new GERPTextField("Alloted Days for Rework");
		tfpersonsAllo = new GERPTextField("Name of Persons alloted for Rework");
		tftravelmode = new GERPTextField("Travelling Mode/Days");
		tfExpanses = new GERPTextField("Service Expenses");
		tfnoofdaysWrkd = new GERPTextField("Number of Days Worked");
		tfserdesc = new GERPTextArea("Note");
		tfserdesc.setWidth("300");
		tfserdesc.setHeight("100");
		cbClient.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbClient.getValue() != null) {
					tfclientname.setReadOnly(false);
					tfClientCity.setReadOnly(false);
					try {
						tfclientname.setValue(serviceClients
								.getClientDetails(companyid, Long.valueOf(cbClient.getValue().toString()), null, null,
										null, null, null, null, "Active", "P").get(0).getClientName());
						tfClientCity.setValue(serviceClients
								.getClientDetails(companyid, Long.valueOf(cbClient.getValue().toString()), null, null,
										null, null, null, null, "Active", "F").get(0).getCityName());
					}
					catch (Exception e) {
					}
					tfclientname.setReadOnly(true);
					tfClientCity.setReadOnly(true);
				}
			}
		});
		tfclientname.setImmediate(true);
		tfclientname.setWidth("150");
		tfClientCity.setImmediate(true);
		tfClientCity.setWidth("150");
		cbtype = new GERPComboBox("Type");
		cbtype.setItemCaptionPropertyId("lookupname");
		cbtype.setImmediate(true);
		cbtype.setNullSelectionAllowed(false);
		cbtype.setWidth("120");
		loadtype();
		dfdate = new GERPPopupDateField("Date");
		dfdate.setDateFormat("dd-MMM-yyyy");
		dfdate.setInputPrompt("Select Date");
		dfdate.setWidth("100px");
		dfstartdt = new GERPPopupDateField("Start Date");
		dfstartdt.setDateFormat("dd-MMM-yyyy");
		dfstartdt.setInputPrompt("Select Date");
		dfenddate = new GERPPopupDateField("End Date");
		dfenddate.setDateFormat("dd-MMM-yyyy");
		dfenddate.setInputPrompt("Select Date");
		taCustomerDetails = new GERPTextArea("Customer Details");
		taCustomerDetails.setWidth("920");
		taCustomerDetails.setHeight("100");
		taserviceProblmRep = new GERPTextArea("Service Problem Reported");
		taserviceProblmRep.setWidth("920");
		taserviceProblmRep.setHeight("100");
		taMatReq = new GERPTextArea("Material Requirement");
		taMatReq.setWidth("700");
		taMatReq.setHeight("100");
		tarootcauseanl = new GERPTextArea("Root Cause Analysis");
		tarootcauseanl.setWidth("920");
		tarootcauseanl.setHeight("100");
		tacorrectiveaction = new GERPTextArea("Corrective Action");
		tacorrectiveaction.setWidth("920");
		tacorrectiveaction.setHeight("100");
		taprevaction = new GERPTextArea("Preventive Action");
		taprevaction.setWidth("920");
		taprevaction.setHeight("100");
		// Sales Enquiry Detail Components Definition
		hlsearchlayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
		// Document Components
		hlDocumentLayout.addLayoutClickListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void layoutClick(LayoutClickEvent event) {
				try {
					tfslno.setReadOnly(false);
					if (tfslno.getValue() != null) {
					}
					tfslno.setReadOnly(true);
				}
				catch (Exception e) {
				}
			}
		});
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlsearchlayout.removeAllComponents();
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol1.addComponent(cbEnquiryNumber);
		flcol2.addComponent(cbtype);
		flcol3.addComponent(dfdate);
		hlsearchlayout.addComponent(flcol1);
		hlsearchlayout.addComponent(flcol2);
		hlsearchlayout.addComponent(flcol3);
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setSizeUndefined();
	}
	
	private void assembleinputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol5 = new FormLayout();
		flcol1.addComponent(tfslno);
		tfslno.setReadOnly(true);
		flcol1.addComponent(cbBranch);
		flcol1.addComponent(dfdate);

		flcol2.addComponent(cbEnquiryNumber);
		flcol2.addComponent(cbDragNo);
		flcol2.addComponent(cbClient);
		flcol3.addComponent(tfclientname);
		flcol3.addComponent(tfClientCity);
		flcol3.addComponent(cbtype);
		flcol4.addComponent(cbPONumber);
		flcol4.addComponent(cbWorkOrderNo);
		hllayout.setMargin(true);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol4);
		hllayout.addComponent(flcol4);
		hllayout.addComponent(flcol5);
		hllayout.setMargin(true);
		hllayout.setSpacing(true);
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hllayout));
		// Add components for User Input Layout
		fldtl1 = new FormLayout();
		fldtl1.addComponent(taCustomerDetails);
		fldtl1.addComponent(taserviceProblmRep);
		fldtl1.addComponent(cbinfnRecBy);
		fldtl1.addComponent(cbmarkstatus);
		hldtllayout.setMargin(true);
		hldtllayout.setSpacing(true);
		hldtllayout.addComponent(fldtl1);
		vldtl.addComponent(hldtllayout);
		hlspecadd.addComponent(vldtl);
		hlspecadd.setSpacing(true);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		// Add components for User Input Layout
		flspec1 = new FormLayout();
		flspec1.addComponent(taMatReq);
		flspec1.addComponent(tfAlloDays);
		flspec1.addComponent(tfpersonsAllo);
		flspec1.addComponent(tftravelmode);
		flspec1.addComponent(tfExpanses);
		flspec1.addComponent(cbprodstatus);
		hlspec.setMargin(true);
		hlspec.addComponent(flspec1);
		hlspec.setMargin(true);
		hlspec.setSpacing(true);
		vlspec.addComponent(hlspec);
		hlspecadd1.addComponent(vlspec);
		hlspecadd1.setSpacing(true);
		// Document Specification
		fldoc1 = new FormLayout();
		fldoc2 = new FormLayout();
		fldoc1.addComponent(dfstartdt);
		fldoc2.addComponent(dfenddate);
		fldoc1.addComponent(tfnoofdaysWrkd);
		fldoc1.addComponent(tfserdesc);
		fldoc1.addComponent(cbqcstatus);
		hldoc.setMargin(true);
		hldoc.addComponent(fldoc1);
		hldoc.addComponent(fldoc2);
		hldoc.setMargin(true);
		hldoc.setSpacing(true);
		vldoc.addComponent(GERPPanelGenerator.createPanel(hldoc));
		hlDocumentLayout.addComponent(vldoc);
		hlDocumentLayout.setSpacing(true);
		flqccont = new FormLayout();
		flqccont.addComponent(tarootcauseanl);
		flqccont.addComponent(tacorrectiveaction);
		flqccont.addComponent(taprevaction);
		hlQcdtl.setSpacing(true);
		hlQcdtl.addComponent(flqccont);
		vlQcdetl.addComponent(GERPPanelGenerator.createPanel(hlQcdtl));
		vlQcdetl.setWidth("100%");
		hlqccontrol.addComponent(vlQcdetl);
		dtlTab = new TabSheet();
		dtlTab.addTab(hlspecadd, "Marketing");
		dtlTab.addTab(hlspecadd1, "Production");
		dtlTab.addTab(hlDocumentLayout, "Customer Ack for Service");
		dtlTab.addTab(hlqccontrol, "Quality Control");
		vlSrchRsltContainer.addComponent(GERPPanelGenerator.createPanel(dtlTab));
	}
	
	// Load Sales Enquiry Header
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<ServiceCallFormDM> hdrlist = new ArrayList<ServiceCallFormDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbBranch.getValue() + "," + tfslno.getValue() + ", "
				+ (Long) cbEnquiryNumber.getValue());
		if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_MARK_FRM") != null
				&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_MARK_FRM")) {
			Notification.show("Arun");
			hdrlist = serviveCallFormService.getServicecallFormList(null, (Long) cbEnquiryNumber.getValue(), null,
					dfdate.getValue(), (String) cbtype.getValue(), null, null, "F", null, null, null);
		} else {
			Notification.show("Jeyaraj");

			hdrlist = serviveCallFormService.getServicecallFormList(null, (Long) cbEnquiryNumber.getValue(), null,
					dfdate.getValue(), (String) cbtype.getValue(), null, null, "F", "Approved", null, null);
		}
		recordCnt = hdrlist.size();
		beanServiceCallForm = new BeanItemContainer<ServiceCallFormDM>(ServiceCallFormDM.class);
		beanServiceCallForm.addAll(hdrlist);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the SMSENQUIRY. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanServiceCallForm);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "slNo", "refDate", "enquiryNo", "clientName", "cityname","type" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Ref.Date", "Enquiry No", "Client Name","City Name", "Type" });
		tblMstScrSrchRslt.setColumnAlignment("slNo", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("", "No.of Records : " + recordCnt);
	}
	
	// Load Branch List
	private void loadBranchList() {
		try {
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(serviceBranch.getBranchList(branchId, null, null, "Active", companyid, "P"));
			cbBranch.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Client List
	private void loadClientList() {
		try {
			Long clientid = serviceEnqHdr
					.getSmsEnqHdrList(null, Long.valueOf(cbEnquiryNumber.getValue().toString()), null, null, null, "F",
							null, null).get(0).getClientId();
			BeanContainer<Long, ClientDM> beanClient = new BeanContainer<Long, ClientDM>(ClientDM.class);
			beanClient.setBeanIdProperty("clientId");
			beanClient.addAll(serviceClients.getClientDetails(companyid, clientid, null, null, null, null, null, null,
					"Active", "P"));
			cbClient.setContainerDataSource(beanClient);
			cbClient.setValue(clientid);
		}
		catch (Exception e) {
		}
	}
	
	// Load Mod_of_Enquiry List
	private void loadtype() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup
					.getCompanyLookUpByLookUp(companyid, null, "Active", "SM_TYPE"));
			cbtype.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadInfrnRecBy() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_INF_REC_BY"));
			cbinfnRecBy.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editSmsEnquiry() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			ServiceCallFormDM serviceCallForm = beanServiceCallForm.getItem(tblMstScrSrchRslt.getValue()).getBean();
			serCallFormId = serviceCallForm.getSerCallFormId();
			cbEnquiryNumber.setValue(serviceCallForm.getEnquiryId());
			cbBranch.setValue(serviceCallForm.getBranchId());
			tfslno.setReadOnly(false);
			tfslno.setValue(serviceCallForm.getSlNo().toString());
			tfslno.setReadOnly(true);
			cbClient.setValue(serviceCallForm.getClientId());
			dfdate.setValue(serviceCallForm.getRefDate());
			cbEnquiryNumber.setValue(serviceCallForm.getEnquiryId());
			cbPONumber.setValue(serviceCallForm.getPoId());
			cbWorkOrderNo.setValue(serviceCallForm.getWoId());
			cbtype.setValue(serviceCallForm.getType());
			taCustomerDetails.setValue(serviceCallForm.getCustomerDetails());
			taserviceProblmRep.setValue(serviceCallForm.getProblemDesc());
			cbinfnRecBy.setValue(serviceCallForm.getComnMode());
			taMatReq.setValue(serviceCallForm.getMatRequirement());
			tfAlloDays.setValue(serviceCallForm.getAllotDays());
			tfpersonsAllo.setValue(serviceCallForm.getPersonsAllot());
			tftravelmode.setValue(serviceCallForm.getTravelMode());
			cbmarkstatus.setValue(serviceCallForm.getMarkStatus());
			cbprodstatus.setValue(serviceCallForm.getProdStatus());
			cbqcstatus.setValue(serviceCallForm.getQcStatus());
			if (serviceCallForm.getServiceExpenses() != null && serviceCallForm.getServiceExpenses().equals("")) tfExpanses
					.setValue(Long.valueOf(serviceCallForm.getServiceExpenses()).toString());
			dfstartdt.setValue(serviceCallForm.getSerStartDt());
			dfenddate.setValue(serviceCallForm.getSerEndDt());
			if (serviceCallForm.getNoDaysWorked() != null && serviceCallForm.getNoDaysWorked().equals("")) {
				tfnoofdaysWrkd.setValue(Long.valueOf(serviceCallForm.getNoDaysWorked()).toString());
			}
			tfserdesc.setValue(serviceCallForm.getServDesc());
			tarootcauseanl.setValue(serviceCallForm.getRootCauseAnal());
			tacorrectiveaction.setValue(serviceCallForm.getCorrectAction());
			taprevaction.setValue(serviceCallForm.getPrevAction());
			cbDragNo.setValue(serviceCallForm.getProduct_id());
		}
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Detail
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			ServiceCallFormDM serviceCallFormDM = new ServiceCallFormDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				serviceCallFormDM = beanServiceCallForm.getItem(tblMstScrSrchRslt.getValue()).getBean();
				serviceCallFormDM.setSlNo(Long.valueOf(tfslno.getValue()));
			} else {
				serviceCallFormDM.setSlNo(Long.valueOf(tfslno.getValue()));
			}
			serviceCallFormDM.setCompanyId(companyid);
			serviceCallFormDM.setRefDate(dfdate.getValue());
			if (cbtype.getValue() != null) {
				serviceCallFormDM.setType(cbtype.getValue().toString());
			}
			serviceCallFormDM.setEnquiryId((Long) cbEnquiryNumber.getValue());
			serviceCallFormDM.setProduct_id((Long) cbDragNo.getValue());
			serviceCallFormDM.setBranchId((Long) cbBranch.getValue());
			serviceCallFormDM.setClientId((Long) cbClient.getValue());
			serviceCallFormDM.setPoId((Long) cbPONumber.getValue());
			serviceCallFormDM.setWoId((Long) cbWorkOrderNo.getValue());
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_MARK_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_MARK_FRM")) {
				serviceCallFormDM.setCustomerDetails(taCustomerDetails.getValue());
				serviceCallFormDM.setProblemDesc(taserviceProblmRep.getValue());
				serviceCallFormDM.setComnMode((String) cbinfnRecBy.getValue());
				serviceCallFormDM.setMarkStatus((String) cbmarkstatus.getValue());
			}
			if (UI.getCurrent().getSession().getAttribute("IS_PROD_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_PROD_FRM")) {
				serviceCallFormDM.setMatRequirement(taMatReq.getValue());
				serviceCallFormDM.setAllotDays(tfAlloDays.getValue());
				serviceCallFormDM.setTravelMode(tftravelmode.getValue());
				if (tfExpanses.getValue() != "") {
					serviceCallFormDM.setServiceExpenses(Long.valueOf(tfExpanses.getValue()));
				}
				if (tfpersonsAllo.getValue() != "") {
					serviceCallFormDM.setPersonsAllot(tfpersonsAllo.getValue());
				}
				serviceCallFormDM.setProdStatus((String) cbprodstatus.getValue());
			}
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_QC_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_QC_FRM")) {
				serviceCallFormDM.setSerEndDt(dfenddate.getValue());
				serviceCallFormDM.setSerStartDt(dfstartdt.getValue());
				serviceCallFormDM.setServDesc(tfserdesc.getValue());
				if (tfnoofdaysWrkd.getValue() != "") {
					serviceCallFormDM.setNoDaysWorked(Long.valueOf(tfnoofdaysWrkd.getValue()));
				}
				serviceCallFormDM.setQcStatus((String) cbqcstatus.getValue());
			}
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_CUST_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_CUST_FRM")) {
				serviceCallFormDM.setRootCauseAnal(tarootcauseanl.getValue());
				serviceCallFormDM.setCorrectAction(tacorrectiveaction.getValue());
				serviceCallFormDM.setPrevAction(taprevaction.getValue());
			}
			serviveCallFormService.saveorupdateServCallForm(serviceCallFormDM);
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_CALLFORM")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_CALLFORM");
					}
				}
				catch (Exception e) {
				}
			}
			tfslno.setReadOnly(false);
			tfslno.setValue(serviceCallFormDM.getSlNo().toString());
			tfslno.setReadOnly(true);
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbBranch.setValue(branchId);
		cbEnquiryNumber.setValue(null);
		cbtype.setValue(null);
		dfdate.setValue(null);
		tfslno.setValue("");
		tfslno.setReadOnly(false);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// cbclient.setRequired(true);
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		tfslno.setReadOnly(true);
		cbBranch.setValue(branchId);
		hllayout.removeAllComponents();
		hldtllayout.removeAllComponents();
		hlspec.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		cbBranch.setRequired(true);
		resetFields();
		dfdate.setValue(new Date());
		cbEnquiryNumber.setRequired(true);
		cbBranch.setValue(branchId);
		tfslno.setReadOnly(true);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, moduleId, "SM_CALLFORM").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfslno.setReadOnly(false);
				tfslno.setValue(slnoObj.getKeyDesc());
				tfslno.setReadOnly(true);
			} else {
				tfslno.setReadOnly(false);
			}
		}
		catch (Exception e) {
		}
		try {
			btnAdd.setVisible(true);
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_MARK_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_MARK_FRM")) {
				dtlTab.setSelectedTab(hlspecadd);
				hlDocumentLayout.setEnabled(false);
				hlspecadd1.setEnabled(false);
				hlqccontrol.setEnabled(false);
			}
		}
		catch (Exception e) {
		}
		try {
			btnAdd.setVisible(true);
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_CUST_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_CUST_FRM")) {
				dtlTab.setSelectedTab(hlDocumentLayout);
				hlspecadd.setEnabled(false);
				hlspecadd1.setEnabled(false);
				hlqccontrol.setEnabled(false);
			}
		}
		catch (Exception e) {
		}
		try {
			btnAdd.setVisible(true);
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_QC_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_QC_FRM")) {
				dtlTab.setSelectedTab(hlqccontrol);
				hlDocumentLayout.setEnabled(false);
				hlspecadd1.setEnabled(false);
				hlspecadd.setEnabled(false);
			}
		}
		catch (Exception e) {
		}
		try {
			btnAdd.setVisible(true);
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_PROD_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_PROD_FRM")) {
				dtlTab.setSelectedTab(hlspecadd1);
				hlspecadd.setEnabled(false);
				hlDocumentLayout.setEnabled(false);
				hlqccontrol.setEnabled(false);
			}
		}
		catch (Exception e) {
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbBranch.setValue(branchId);
		hllayout.removeAllComponents();
		hldtllayout.removeAllComponents();
		hlspec.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		if (tfslno.getValue() == null || tfslno.getValue().trim().length() == 0) {
			tfslno.setReadOnly(false);
		}
		assembleinputLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		resetFields();
		editSmsEnquiry();
		cbBranch.setRequired(true);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbBranch.setComponentError(null);
		cbClient.setComponentError(null);
		Boolean errorFlag = false;
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
			if (cbBranch.getValue() == null) {
				cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
				errorFlag = true;
			}
			if (cbClient.getValue() == null) {
				cbClient.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
				errorFlag = true;
			}
			if (cbEnquiryNumber.getValue() == null) {
				cbEnquiryNumber.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
				errorFlag = true;
			}
			if ((tfslno.getValue() == null) || tfslno.getValue().trim().length() == 0) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_CALLFORM")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("N")) {
						tfslno.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_CODE));
						errorFlag = true;
					}
				}
				catch (Exception e) {
				}
			} else {
				tfslno.setComponentError(null);
				errorFlag = false;
			}
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_MARK_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_MARK_FRM")) {
				if (taserviceProblmRep.getValue() == "" && taserviceProblmRep.getValue() == null) {
					taserviceProblmRep.setComponentError(new UserError("Please Enter Service Problem Description"));
					errorFlag = true;
				}
				if (cbinfnRecBy.getValue() == null) {
					cbinfnRecBy.setComponentError(new UserError("Please select Information Rec.By"));
					errorFlag = true;
				}
			}
			if (UI.getCurrent().getSession().getAttribute("IS_PROD_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_PROD_FRM")) {
				if (taMatReq.getValue() == "" || taMatReq.getValue() == null) {
					taMatReq.setComponentError(new UserError("Please Enter Material Requirement"));
					errorFlag = true;
				}
				if (tfAlloDays.getValue() == "" || tfAlloDays.getValue() == null) {
					taserviceProblmRep.setComponentError(new UserError("Please Enter Alloted Days for Rework"));
					errorFlag = true;
				}
				if (tftravelmode.getValue() == "" || tftravelmode.getValue() == null) {
					tftravelmode.setComponentError(new UserError("Please Enter Travelling Mode"));
					errorFlag = true;
				}
			}
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_QC_FRM") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_QC_FRM")) {
				if (tarootcauseanl.getValue() == "" || tarootcauseanl.getValue() == null) {
					tarootcauseanl.setComponentError(new UserError("Please Enter Root Cause Analysis"));
					errorFlag = true;
				}
				if (tacorrectiveaction.getValue() == "" || tacorrectiveaction.getValue() == null) {
					tacorrectiveaction.setComponentError(new UserError("Please Enter Corrective Action"));
					errorFlag = true;
				}
				if (taprevaction.getValue() == "" || taprevaction.getValue() == null) {
					taprevaction.setComponentError(new UserError("Please Enter Preventive Action"));
					errorFlag = true;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + cbBranch.getValue() + "," + cbClient.getValue()
				+ "," + "," + cbtype.getValue() + "," + dfdate.getValue() + "," + dfenddate.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for enquiryId " + serCallFormId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(serCallFormId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hllayout1.removeAllComponents();
		tblMstScrSrchRslt.setValue(null);
		cbtype.setRequired(false);
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
		cbBranch.setRequired(false);
		tfslno.setReadOnly(false);
		cbEnquiryNumber.setRequired(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfslno.setReadOnly(false);
		tfslno.setValue("");
		cbEnquiryNumber.setValue(null);
		tfclientname.setReadOnly(false);
		tfclientname.setValue("");
		tfclientname.setReadOnly(true);
		tfClientCity.setReadOnly(false);
		tfClientCity.setValue("");
		tfClientCity.setReadOnly(true);
		tfslno.setComponentError(null);
		cbBranch.setValue(null);
		cbBranch.setComponentError(null);
		cbClient.setComponentError(null);
		cbClient.setValue(null);
		dfdate.setValue(null);
		dfenddate.setValue(null);
		cbtype.setValue(null);
		taCustomerDetails.setValue("");
		cbtype.setValue(null);
		dfenddate.setValue(null);
		cbClient.setRequired(true);
		cbinfnRecBy.setRequired(true);
		taMatReq.setRequired(true);
		taMatReq.setValue("");
		tfAlloDays.setRequired(true);
		tftravelmode.setRequired(true);
		tarootcauseanl.setRequired(true);
		tacorrectiveaction.setRequired(true);
		taprevaction.setRequired(true);
		taserviceProblmRep.setRequired(true);
		cbDragNo.setValue(null);
		cbPONumber.setValue(null);
		cbWorkOrderNo.setValue(null);
		cbEnquiryNumber.setComponentError(null);
		cbmarkstatus.setValue(cbmarkstatus.getItemIds().iterator().next());
		cbprodstatus.setValue(cbprodstatus.getItemIds().iterator().next());
		cbqcstatus.setValue(cbqcstatus.getItemIds().iterator().next());
		tfserdesc.setValue("");
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
			assembleSearchLayout();
			tfslno.setReadOnly(false);
		}
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, Long> parameterMap = new HashMap<String, Long>();
			parameterMap.put("SER_CALL_FORM_ID", serCallFormId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "//WEB-INF//reports//servicecallform"); // productlist is the name of my jasper
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				statement.close();
				Database.close(connection);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Load EnquiryNo
	private void loadEnquiryNo() {
		BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("enquiryId");
		beansmsenqHdr.addAll(serviceEnqHdr.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
		cbEnquiryNumber.setContainerDataSource(beansmsenqHdr);
	}
	
	private void loadPurchaseOrdNo() {
		BeanContainer<Long, SmsPOHdrDM> beanPurchaseOrdHdr = new BeanContainer<Long, SmsPOHdrDM>(SmsPOHdrDM.class);
		beanPurchaseOrdHdr.setBeanIdProperty("poid");
		beanPurchaseOrdHdr.addAll(servicePurchaseOrdHdr.getSmspohdrList(null, null, companyid, null, null, null, null,
				"F", (Long) cbEnquiryNumber.getValue()));
		cbPONumber.setContainerDataSource(beanPurchaseOrdHdr);
	}
	
	private void loadWorkOrderNo() {
		BeanContainer<Long, WorkOrderHdrDM> beanWrkOrdHdr = new BeanContainer<Long, WorkOrderHdrDM>(
				WorkOrderHdrDM.class);
		beanWrkOrdHdr.setBeanIdProperty("workOrdrId");
		beanWrkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, null, null, null, null, "F",
				null, null, null, null, (Long) cbPONumber.getValue()));
		cbWorkOrderNo.setContainerDataSource(beanWrkOrdHdr);
	}
	
	private void loaddrawingNo() {
		BeanContainer<Long, SmsEnquiryDtlDM> beandrgNo = new BeanContainer<Long, SmsEnquiryDtlDM>(SmsEnquiryDtlDM.class);
		beandrgNo.setBeanIdProperty("productid");
		beandrgNo.addAll(serviceEnqDtls.getsmsenquirydtllist(null, (Long) cbEnquiryNumber.getValue(), null, null, null,
				null));
		cbDragNo.setContainerDataSource(beandrgNo);
	}
}