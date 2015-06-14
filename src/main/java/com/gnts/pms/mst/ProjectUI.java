/**
 * File Name	:	MPmsProjectApp.java
 * Description	:	this class is used for add/edit projects details.
 * Author		:	Rajan Babu
 * Date			:	Mar 5, 2014
 * Modification 
 * Modified By  :   Rajan Babu
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version          Date           Modified By             Remarks
 *   0.1           Mar 05 2014     Rajan Babu             develop the Projects screen.
 *           
 */

package com.gnts.pms.mst;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;


import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.components.GERPPanelGenerator;


import com.gnts.base.domain.mst.CurrencyDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CurrencyService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.LookupService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;

import com.gnts.base.domain.mst.EmployeeDM;

import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.ui.AuditRecordsApp;







import com.gnts.pms.domain.mst.ProjectBillDM;
import com.gnts.pms.domain.mst.ProjectDM;
import com.gnts.pms.domain.mst.ProjectPhaseDM;
import com.gnts.pms.domain.mst.ProjectPhaseRevnDM;
import com.gnts.pms.domain.mst.ProjectTeamDM;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import  com.gnts.pms.domain.mst.ProjectTeamDM;


import com.gnts.pms.service.mst.ProjectBillService;
import com.gnts.pms.service.mst.ProjectPhaseRevnService;
import com.gnts.pms.service.mst.ProjectPhaseService;
import com.gnts.pms.service.mst.ProjectService;
import com.gnts.pms.service.mst.ProjectTeamService;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ProjectUI implements ClickListener{
	
	private static final long serialVersionUID = 1L;
	private VerticalLayout vlSearchPanel=new VerticalLayout();
	private	VerticalLayout vlTablePanel=new VerticalLayout();
	private	VerticalLayout vlMainPanel=new VerticalLayout();
	private FormLayout flForm1,flForm2,flForm3,flForm4,flForm5;
	private FormLayout flBillForm1,flBillForm2,flBillForm3,flBillForm5;
	private FormLayout flBillForm4;
	private FormLayout flPhaseForm1,flPhaseForm2,flPhaseForm3,flPhaseForm4;
	private FormLayout flProjectTeamForm1,flProjectTeamForm2,flProjectTeamForm3;
	private FormLayout flPhaseRevnForm1,flPhaseRevnForm2,flPhaseRevnForm3,flPhaseRevnForm4;
	private HorizontalLayout hlSaveBtn,hlAddEditBtn,hlBillingForm,hlAddEditBtnBill,hlPhaseForm,hlAddEditBtnPhase,hlAddEditBtnTeam,hlAddEditBtnRevn;
	private PopupDateField dfBroughtDt,dfStartDt,dfEndDt;
	private PopupDateField dfPlanStartDate,dfPlanEndDt,dfActStartDate,dfActEndDate;
	private PopupDateField dfdueDate,dfInvoiceDt,dfPaidDate,dfRevnStartdt,dfRevnEnddt;
	private TextArea taBillRemarks,taPaymentRemarks,taPhaseRemarks,taTeamRemarks,taClientRemarks,taJustification;
	private TextField tfSearchProject,tfProjectName,tfProjectCost,tfProjectEffort,tfBroughtBy;
	private TextField tfBillDesc,tfBillAmount,tfInvoiceNo,tfPaidAmount,tfBalAmount;
	private TextField tfProjPhase,tfPlanEffort,tfActEffort,tfVersionNo;
	private TextField tfRoleDesc,tfTeamPlanEffort,tfBillRate,tfLoading;
	private TextField tfRevnVersion,tfRevnPenalty,tfRevnEffort;
	private ComboBox cbClientName,cbSearchStatus,cbCurrencyName,cbEmployeName,cbStatus,cbProjectType,cbProjectSubType,cbBroughtby;
	private ComboBox cbBillStatus,cbReceivedBy,cbPhaseStatus,cbTeamEmployee;
	private	Button btnSearch,btnReset,btnAdd,btnEdit,btnSave,btnCancel,btnDownload,btnHome,btnAudit;
	private	Button btnAddBill,btnEditBill,btnDownloadBill,btnHomeBill,btnAuditBill;
	private	Button btnAddPhase,btnEditPhase,btnDownloadPhase,btnHomePhase,btnAuditPhase,btnPhaseRevn;
	private	Button btnAddTeam,btnEditTeam,btnDownloadTeam,btnHomeTeam,btnAuditTeam;
	private	Button btnAddRevision,btnDownloadRevn;
	private	Table tblProject,tblBill,tblPhase,tblTeam,tblPhaseRevn;
	private CheckBox chTrack,chBaseLined,chClientAgreed;
	private TabSheet tabsheet;
	private	Label lblNoofRecords,lblFormTitle,lblNotificationIcon,lblNotification;
	private int total = 0;
	private BeanItemContainer<ProjectTeamDM> beanProjectTeam =null;
	private BeanItemContainer<ProjectPhaseRevnDM> beanPhaseRevn =null;
	private BeanItemContainer<ProjectPhaseDM> beanPhase =null;
	private BeanItemContainer<ProjectDM> beanProject =null;
	private BeanItemContainer<EmployeeDM> beanEmployee =null;
	private BeanItemContainer<StatusDM> beanStatus =null;
	private BeanItemContainer<CurrencyDM> beanCurrency=null;
	private BeanItemContainer<ClientDM> beanClient=null;
	private BeanItemContainer<ProjectBillDM> beanProjBill =null;
	private LookupService servbeanLookup = (LookupService) SpringContextHelper.getBean("lookup");
	
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private CurrencyService servicebeanCurrency = (CurrencyService) SpringContextHelper.getBean("currency");
	private ProjectService servProjectsBean=(ProjectService) SpringContextHelper.getBean("MProjects");
	private ProjectBillService servProjbillBean=(ProjectBillService) SpringContextHelper.getBean("MProjectBill");
	private ClientService servClientBean=(ClientService) SpringContextHelper.getBean("clients");
	private ProjectPhaseService servProjPhaseBean=(ProjectPhaseService) SpringContextHelper.getBean("MProjectPhase");
	private ProjectTeamService servProjTeamBean = (ProjectTeamService) SpringContextHelper.getBean("MProjectTeam");
	private ProjectPhaseRevnService servProjPhaseRevnBean = (ProjectPhaseRevnService) SpringContextHelper.getBean("MPhaseRevision");
	private ClientDM selectedClient;
	private EmployeeDM selectedEmployee,selectedBroughtby;
	private String userName,screenName,projectidAudit,projectBillIdAudit,projPhaseidAudit,projTeamidAudit;
	private CurrencyDM selectedCurrency;
	private VerticalLayout vlTableForm,vlAudit,vlProjectBill,vlProjectPhase,vlProjectTeam,vlProjectPhaseRevision;
	private VerticalLayout vlAuditBill,vlAuditPhase,vlAuditTeam,vlBillTableForm,vlPhaseTableForm,vlTeamTableForm;
	private Long companyId,projectId,projectPhaseid,billId,phaseId,teamId,revisionId,phaseid;
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	private ExcelExporter excelexporterBill,excelexporterPhase,excelexporterTeam;
	private CSVExporter csvexporterBill,csvexporterPhase,csvexporterTeam;
	private PdfExporter pdfexporterBill,pdfexporterPhase,pdfexporterTeam;
	private Window notifications,notificationBill,notificationPhase,notificationTeam;
	List<ProjectBillDM> tempSaveBill = new ArrayList<ProjectBillDM>();
	List<ProjectPhaseDM> tempSavePhase = new ArrayList<ProjectPhaseDM>();
	List<ProjectTeamDM> tempSaveTeam = new ArrayList<ProjectTeamDM>();
	List<ProjectPhaseRevnDM> tempSaveRevn = new ArrayList<ProjectPhaseRevnDM>();
	private Logger logger = Logger.getLogger(ProjectUI.class);
	private Long comapnyId;

	public ProjectUI() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		companyId=Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		VerticalLayout clMainLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		HorizontalLayout hlScreenNameLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");

		buildMainview(clMainLayout,hlScreenNameLayout);
	}
	/*
	 * buildMainview()-->for screen UI design
	 * 
	 * @param clMainLayout, hlScreenNameLayout
	 */
    private	void buildMainview(VerticalLayout clMainLayout,HorizontalLayout hlScreenNameLayout)
	{
    	
    	hlScreenNameLayout.removeAllComponents();
    	tblProject=new Table();
    	tblProject.setPageLength(12);
    	tblProject.setSizeFull();
    	tblProject.setImmediate(true);
    	tblProject.setFooterVisible(true);
		
    	
		vlMainPanel.setVisible(false);
		vlMainPanel.setMargin(true);
		
		 btnSearch=new Button("Search",this);  
		 
		 btnReset=new Button("Reset",this);
		 
		 btnAdd=new Button("Add",this);
		 btnAdd.setEnabled(true);
		 btnAdd.addStyleName("add");

		 
		 btnEdit=new Button("Edit",this);
		 btnEdit.setEnabled(false);
		 
		 btnSave=new Button("Save",this);
		 btnCancel=new Button("Cancel",this);
		 
		 btnDownload = new Button("Download",this);
		 
		 btnHome=new Button("Home",this);		 
		btnHome.setStyleName("homebtn");
		 btnHome.setEnabled(false);
		 
		 
		 btnAudit=new Button("Audit History",this);
		 btnAudit.setStyleName("hostorybtn");
		 //btnAudit.setEnabled(false);
			
		
		 btnSearch.setDescription("Search Projects");
		 btnReset.setDescription("Reset Projects");
		 btnAdd.setDescription("Add Projects");
		 btnEdit.setDescription("Edit Projects");
		 btnSave.setDescription("Save Projects");
		 btnCancel.setDescription("Return to Search");
		 btnDownload.setDescription("Download");
		 
		 
		btnDownload.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
		
			public void buttonClick(ClickEvent event) {
				event.getButton().removeStyleName("unread");
				if (notifications != null && notifications.getUI() != null)
					notifications.close();
				else {
					buildNotifications(event);
					UI.getCurrent().addWindow(notifications);
					notifications.focus();
					((VerticalLayout) UI.getCurrent().getContent())
							.addLayoutClickListener(new LayoutClickListener() {
								
								private static final long serialVersionUID = 1L;

								@Override
								public void layoutClick(LayoutClickEvent event) {
									notifications.close();
									((VerticalLayout) UI.getCurrent().getContent())
											.removeLayoutClickListener(this);
								}
							});
				}
			}
		});
		
		btnAdd.addStyleName("add");
		btnEdit.addStyleName("editbt");
		 btnSearch.addStyleName("searchbt");
		 btnReset.addStyleName("resetbt");
		 btnSave.addStyleName("savebt");
		 btnCancel.addStyleName("cancelbt");
		 btnDownload.addStyleName("downloadbt");
		 
		 lblFormTitle = new Label();
		 lblFormTitle.setContentMode(ContentMode.HTML);
		 lblFormTitle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Home");
			
			lblNotificationIcon = new Label();
			lblNotification = new Label();
			lblNotification.setContentMode(ContentMode.HTML);
			tfSearchProject=new TextField("Project Name");	
			//tfSearchProject.setInputPrompt("Project Name");
		
		 cbSearchStatus=new ComboBox("Status");
		 //cbSearchStatus.setInputPrompt(Common.SELECT_PROMPT);
		 cbSearchStatus.setWidth("140");
		 cbSearchStatus.setNullSelectionAllowed(false);
		 cbSearchStatus.setImmediate(true);
		 cbSearchStatus.setItemCaptionPropertyId("desc");
 		 beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
//		 beanStatus.addAll(Common.listStatus);
		 cbSearchStatus.setContainerDataSource(beanStatus);
//		 cbSearchStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));

	
	tfProjectName=new TextField("Project Name");
	//tfProjectName.setInputPrompt("Enter Project Name");
	tfProjectName.setRequired(true);
	tfProjectName.setMaxLength(30);
	tfProjectName.setWidth("140");
//	tfProjectName.addBlurListener(new BlurListener()
	//{
//		private static final long serialVersionUID = 1L;
//			public void blur(BlurEvent event) {
//				// TODO Auto-generated method stub
//			//	tfProjectName.setComponentError(null);
//				 String charseq=tfProjectName.getValue().toString();
//				 if(charseq.matches("^[a-zA-Z ]+")) {
//					 tfProjectName.setComponentError(null); 
//				 } else {
//					 tfProjectName.setComponentError(new UserError("Projects name should be characters"));
//				 }
//			}
//	 });	
	
	cbProjectType = new ComboBox("Project Type");
	//cbProjectType.setInputPrompt(Common.SELECT_PROMPT);
	cbProjectType.setImmediate(true);
	cbProjectType.setWidth("140");
	cbProjectType.setNullSelectionAllowed(false);
//	List<String> projType=servbeanLookup.getLookupNameForProjectType();
//	for(Object obj:projType) {
//	cbProjectType.addItem(obj);
//	}
	cbProjectSubType = new ComboBox("Project SubType");
	//cbProjectSubType.setInputPrompt(Common.SELECT_PROMPT);
	cbProjectSubType.setImmediate(true);
	cbProjectSubType.setWidth("140");
	cbProjectSubType.setNullSelectionAllowed(false);
	/*List<String> projsubtype = servbeanLookup.getLookupNameForProjectSubType();
	Iterator<String> iter = projsubtype.iterator();
	while(iter.hasNext()) {
	cbProjectSubType.addItem(iter.next());
	}*/
	
	cbClientName=new ComboBox("Client Name");
	//cbClientName.setInputPrompt(Common.SELECT_PROMPT);
	cbClientName.setRequired(true);
	cbClientName.setImmediate(true);
	cbClientName.setWidth("140");
	cbClientName.setNullSelectionAllowed(false);
	cbClientName.setItemCaptionPropertyId("clientName");
	loadClientList();
	cbClientName.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				BeanItem<?> item = (BeanItem<?>) cbClientName.getItem(itemid);
				selectedClient = (ClientDM) item.getBean();
			}
		}
	});
	
	cbStatus=new ComboBox("Status");
	//cbStatus.setInputPrompt(Common.SELECT_PROMPT);
	cbStatus.setWidth("140");
	cbStatus.setImmediate(true);
	cbStatus.setNullSelectionAllowed(false);
	cbStatus.setItemCaptionPropertyId("desc");
	beanStatus=new BeanItemContainer<StatusDM>(StatusDM.class);
//	beanStatus.addAll(Common.listStatus);
	cbStatus.setContainerDataSource(beanStatus);
//	cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
	 
	cbEmployeName=new ComboBox("Project Owner");
	//cbEmployeName.setInputPrompt(Common.SELECT_PROMPT);
	cbEmployeName.setRequired(true);
	cbEmployeName.setImmediate(true);
	cbEmployeName.setWidth("140");
	cbEmployeName.setNullSelectionAllowed(false);
	cbEmployeName.setItemCaptionPropertyId("firstname");
	loadEmployeeList();
	cbEmployeName.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			// TODO Auto-generated method stub
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				BeanItem<?> item = (BeanItem<?>) cbEmployeName.getItem(itemid);
				selectedEmployee = (EmployeeDM) item.getBean();
			}
		}
	});
	cbCurrencyName=new ComboBox("Currency ");
	//cbCurrencyName.setInputPrompt(Common.SELECT_PROMPT);
	cbCurrencyName.setImmediate(true);
	cbCurrencyName.setNullSelectionAllowed(false);
	cbCurrencyName.setWidth("140");
	cbCurrencyName.setItemCaptionPropertyId("ccyname");
	loadCurrencyList();
	cbCurrencyName.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			// TODO Auto-generated method stub
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				BeanItem<?> item = (BeanItem<?>) cbCurrencyName.getItem(itemid);
				selectedCurrency= (CurrencyDM) item.getBean();
			}
		}
	});
	
	 tfProjectCost=new TextField("Project Cost");
	// tfProjectCost.setInputPrompt("Enter Project Cost");
	 tfProjectCost.setWidth("140");
	 
	 tfProjectEffort=new TextField("Project Effort");
	// tfProjectEffort.setInputPrompt("Enter Project Effort");
	 tfProjectEffort.setWidth("140");
	 tfBroughtBy=new TextField("Brought By");
	 //tfBroughtBy.setInputPrompt("Enter Brought By");
	 cbBroughtby = new ComboBox("Brought By");
	 //cbBroughtby.setInputPrompt(Common.SELECT_PROMPT);
	 cbBroughtby.setImmediate(true);
	 cbBroughtby.setWidth("140");
	 cbBroughtby.setNullSelectionAllowed(false);
	 cbBroughtby.setItemCaptionPropertyId("firstname");
		loadBroughtByList();
		cbBroughtby.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					BeanItem<?> item = (BeanItem<?>) cbBroughtby.getItem(itemid);
					selectedBroughtby = (EmployeeDM) item.getBean();
				}
			}
		});
	 
	 dfBroughtDt =new PopupDateField("Brought Date");
	 //dfBroughtDt.setInputPrompt(Common.SELECT_PROMPT);
	 dfBroughtDt.setWidth("140");
	 
	/// dfBroughtDt.setDateFormat(stringdate());
	 dfStartDt =new PopupDateField("Start Date");
	// dfStartDt.setInputPrompt(Common.SELECT_PROMPT);
	 dfStartDt.setRequired(true);
	 dfStartDt.setWidth("140");
	 dfStartDt.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;

			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				dateValidate();	
			} 
       });
       
	 dfEndDt =new PopupDateField("End Date");
	 //dfEndDt.setInputPrompt(Common.SELECT_PROMPT);
	 dfEndDt.setRequired(true);
	 dfEndDt.setWidth("140");
	 dfEndDt.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;

			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				dateValidate();	
			} 
    });
	 chTrack=new CheckBox("Track");
	
	flForm1=new FormLayout();
	flForm1.setSpacing(true);
	flForm1.addComponent(tfProjectName);
	flForm1.addComponent(cbProjectType);
	flForm1.addComponent(cbProjectSubType);
	
		
	flForm2=new FormLayout();
	flForm2.setSpacing(true);
	flForm2.addComponent(cbClientName);
	flForm2.addComponent(cbEmployeName);
	flForm2.addComponent(cbCurrencyName);
	
	flForm3=new FormLayout();
	flForm3.setSpacing(true);
	flForm3.addComponent(tfProjectCost);
	flForm3.addComponent(tfProjectEffort);
	flForm3.addComponent(cbBroughtby);
	
	flForm4=new FormLayout();
	flForm4.setSpacing(true);
	flForm4.addComponent(dfBroughtDt);
	flForm4.addComponent(dfStartDt);
	flForm4.addComponent(dfEndDt);
	flForm4.addComponent(cbStatus);
	
	flForm5=new FormLayout();
	
	
	flForm5.addComponent(chTrack);
	
	
	HorizontalLayout hlMainform = new HorizontalLayout();
	hlMainform.setSpacing(true);
	hlMainform.addComponent(flForm1);
	hlMainform.addComponent(flForm2);
	hlMainform.addComponent(flForm3);
	hlMainform.addComponent(flForm4);
	hlMainform.addComponent(flForm5);
	
	hlSaveBtn = new HorizontalLayout();
	hlSaveBtn.addComponent(btnSave);
	hlSaveBtn.addComponent(btnCancel);
	hlSaveBtn.setVisible(false);
	 
	GridLayout glMainPanel = new GridLayout(1,1);
	glMainPanel.setSpacing(true);
	glMainPanel.setMargin(true);
	glMainPanel.setSizeFull();
	glMainPanel.addComponent(hlMainform);
	
	btnAddBill = new Button("Add",this);
	btnEditBill = new Button("Edit",this);
	btnEditBill.setEnabled(false);
	btnDownloadBill = new Button("Download",this);
	btnHomeBill = new Button("Home",this);
	btnHomeBill.setStyleName("homebtn");
	btnHomeBill.setEnabled(false);
	btnAuditBill =new Button("Audit History",this);
	btnAuditBill.setStyleName("hostorybtn");
	btnAddBill.addStyleName("add");
	btnEditBill.addStyleName("editbt");
	 btnDownloadBill.addStyleName("downloadbt");
	 btnDownloadBill.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				event.getButton().removeStyleName("unread");
				if (notificationBill != null && notificationBill.getUI() != null)
					notificationBill.close();
				else {
					buildNotificationsBill(event);
					UI.getCurrent().addWindow(notificationBill);
					notificationBill.focus();
					((VerticalLayout) UI.getCurrent().getContent())
							.addLayoutClickListener(new LayoutClickListener() {

								private static final long serialVersionUID = 1L;
								public void layoutClick(LayoutClickEvent event) {
									notificationBill.close();
									((VerticalLayout) UI.getCurrent().getContent())
											.removeLayoutClickListener(this);
								}
							});
				}
			}
		});
	 
	 btnEditBill.setDescription("Edit Project Bill");
	 btnAddBill.setDescription("Save Project Bill");
	 btnDownloadBill.setDescription("Download");
	
	tfBillDesc = new TextField("Billing Description");
	//tfBillDesc.setInputPrompt("enter Bill Desc.");
	tfBillDesc.setRequired(true);
	tfBillDesc.setWidth("140");
	tfBillAmount = new TextField("Billing Amount");
	//tfBillAmount.setInputPrompt("Enter Bill Amt.");
	tfBillAmount.setWidth("140");
	tfInvoiceNo =new TextField("Invoice No.");
	//tfInvoiceNo.setInputPrompt("Enter Invoice No.");
	tfInvoiceNo.setWidth("100");
	dfdueDate = new PopupDateField("Due Date");
	//dfdueDate.setInputPrompt(Common.SELECT_PROMPT);
	dfdueDate.setRequired(true);
	dfdueDate.setWidth("100");
	dfInvoiceDt = new PopupDateField("Invoice Date");
	//dfInvoiceDt.setInputPrompt(Common.SELECT_PROMPT);
	dfInvoiceDt.setWidth("100");
	dfPaidDate = new PopupDateField("Paid Date");
	//dfPaidDate.setInputPrompt(Common.SELECT_PROMPT);
	dfPaidDate.setRequired(true);
	dfPaidDate.setWidth("100");
	tfPaidAmount = new TextField("Paid Amount");
	//tfPaidAmount.setInputPrompt("Enter Paid Amt.");
	tfPaidAmount.setWidth("100");
	tfBalAmount = new TextField("Bal Amount");
	tfBalAmount.setWidth("100");
	
	cbBillStatus = new ComboBox("Status");
	//cbBillStatus.setInputPrompt(Common.SELECT_PROMPT);
	cbBillStatus.setWidth("100");
	cbBillStatus.setNullSelectionAllowed(false);
	cbBillStatus.setImmediate(true);
	cbBillStatus.setItemCaptionPropertyId("desc");
	beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
//	 beanStatus.addAll(Common.listStatus);
	 cbBillStatus.setContainerDataSource(beanStatus);
//	 cbBillStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
	 
	 taBillRemarks = new TextArea("Billing Remarks");
	 //taBillRemarks.setInputPrompt("Enter Bill Remarks");
	 taBillRemarks.setWidth("105");
	 taPaymentRemarks = new TextArea("Payment Remarks");
	 taPaymentRemarks.setWidth("105");
//	 taPaymentRemarks.setInputPrompt("Enter Payment Remarks");
	 cbReceivedBy=new ComboBox("Received By");
//	 cbReceivedBy.setInputPrompt(Common.SELECT_PROMPT);
	 cbReceivedBy.setRequired(false);
	 cbReceivedBy.setImmediate(true);
	 cbReceivedBy.setWidth("140");
	 cbReceivedBy.setNullSelectionAllowed(false);
	 cbReceivedBy.setItemCaptionPropertyId("firstname");
	 loadReceivedbyList();
		cbReceivedBy.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					BeanItem<?> item = (BeanItem<?>) cbReceivedBy.getItem(itemid);
					selectedEmployee = (EmployeeDM) item.getBean();
				}
			}
		});
	
	flBillForm1 = new FormLayout();
	flBillForm1.setSpacing(true);
	flBillForm1.addComponent(tfBillDesc);
	flBillForm1.addComponent(tfBillAmount);
	flBillForm1.addComponent(cbReceivedBy);
	
	flBillForm2 = new FormLayout();
	flBillForm2.setSpacing(true);
	flBillForm2.addComponent(dfdueDate);
	flBillForm2.addComponent(tfInvoiceNo);
	flBillForm2.addComponent(dfInvoiceDt);
	
	flBillForm3 = new FormLayout();
	flBillForm3.setSpacing(true);
	flBillForm3.addComponent(dfPaidDate);
	flBillForm3.addComponent(tfPaidAmount);
	flBillForm3.addComponent(tfBalAmount);

	flBillForm3.addComponent(cbBillStatus);
	
	flBillForm4 = new FormLayout();
	flBillForm4.setSpacing(true);
	flBillForm4.addComponent(taBillRemarks);
	flBillForm4.setHeight("18");
	flBillForm4.setMargin(true);
	
	flBillForm5 = new FormLayout();
	flBillForm5.setSpacing(true);
	flBillForm5.addComponent(taPaymentRemarks);
	
	
	hlBillingForm = new HorizontalLayout();
	hlBillingForm.setSpacing(true);
	hlBillingForm.addComponent(flBillForm1);
	hlBillingForm.addComponent(flBillForm2);
	hlBillingForm.addComponent(flBillForm3);
	hlBillingForm.addComponent(flBillForm4);
	hlBillingForm.addComponent(flBillForm5);
	
	GridLayout glBillform = new GridLayout(1,1);
	glBillform.setSizeFull();
	glBillform.setSpacing(true);
	glBillform.setMargin(false);
	glBillform.addComponent(hlBillingForm);
	
	tblBill = new Table();
	tblBill.setImmediate(true);
	tblBill.setSizeFull();
	tblBill.setPageLength(4); 
	
	HorizontalLayout hlFiledownloadBill = new HorizontalLayout();
	hlFiledownloadBill.setSpacing(true);
	hlFiledownloadBill.addComponent(btnDownloadBill);
	hlFiledownloadBill.setComponentAlignment(btnDownloadBill,Alignment.MIDDLE_CENTER);
    
    HorizontalLayout hlTabelCaptionBill = new HorizontalLayout();
    hlTabelCaptionBill.addComponent(btnHomeBill);
    hlTabelCaptionBill.setComponentAlignment(btnHomeBill, Alignment.MIDDLE_RIGHT);
    hlTabelCaptionBill.addStyleName("lightgray");
    hlTabelCaptionBill.setHeight("25px");
    hlTabelCaptionBill.setWidth("90px");
	
	HorizontalLayout hlTableTitleBill = new HorizontalLayout(); 
	hlTableTitleBill.addComponent(hlTabelCaptionBill);
	hlTableTitleBill.addComponent(btnAddBill);
	hlTableTitleBill.addComponent(btnEditBill);
	hlTableTitleBill.addComponent(btnAuditBill);
	
    hlAddEditBtnBill = new HorizontalLayout();
    hlAddEditBtnBill.addStyleName("topbarthree");
    hlAddEditBtnBill.setWidth("100%");
    hlAddEditBtnBill.addComponent(hlTableTitleBill);
    hlAddEditBtnBill.addComponent(hlFiledownloadBill);
    hlAddEditBtnBill.setComponentAlignment(hlFiledownloadBill,Alignment.MIDDLE_RIGHT);
    hlAddEditBtnBill.setHeight("28px");
	
    vlBillTableForm = new VerticalLayout();
    vlBillTableForm.setSpacing(false);
    vlBillTableForm.addComponent(hlAddEditBtnBill);
    vlBillTableForm.addComponent(tblBill);
    
	vlProjectBill = new VerticalLayout();
	vlProjectBill.setSpacing(true);
	vlProjectBill.setMargin(true);
	vlProjectBill.addComponent(GERPPanelGenerator.createPanel(glBillform));
	vlProjectBill.addComponent(vlBillTableForm);
	
	tfProjPhase = new TextField("Project Phase");
	tfProjPhase.setRequired(true);
	tfProjPhase.setReadOnly(false);
	//tfProjPhase.setInputPrompt("enter Project Phase");
	tfPlanEffort = new TextField("Plan Effort");
	tfPlanEffort.setReadOnly(false);
	//tfPlanEffort.setInputPrompt("Enter Plan Effort");
	tfActEffort =new TextField("Act Effort");
	tfActEffort.setReadOnly(false);
	//tfActEffort.setInputPrompt("Enter Act Effort");
	tfVersionNo =new TextField("Version No.");
	tfVersionNo.setReadOnly(false);
	dfPlanStartDate = new PopupDateField("Plan StartDate");
	dfPlanStartDate.setReadOnly(false);
	dfPlanStartDate.setRequired(true);
	//dfPlanStartDate.setInputPrompt(Common.SELECT_PROMPT);
	dfPlanStartDate.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;

			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				PlanDateValidate();	
			} 
       });
	dfPlanEndDt = new PopupDateField("Plan EndDate");
	dfPlanEndDt.setReadOnly(false);
	//dfPlanEndDt.setInputPrompt(Common.SELECT_PROMPT);
	dfPlanEndDt.setRequired(true);
	dfPlanEndDt.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;

			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				PlanDateValidate();	
			} 
       });
	dfActStartDate = new PopupDateField("Act StartDate");
	dfActStartDate.setReadOnly(false);
	//dfActStartDate.setInputPrompt(Common.SELECT_PROMPT);
	dfActStartDate.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;

			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				ActDateValidate();
			} 
       });
	dfActEndDate = new PopupDateField("Act EndDate");
	dfActEndDate.setReadOnly(false);
	//dfActEndDate.setInputPrompt(Common.SELECT_PROMPT);
	dfActEndDate.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;

			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				ActDateValidate();
			} 
       });
	cbPhaseStatus = new ComboBox("Status");
	cbPhaseStatus.setReadOnly(false);
	//cbPhaseStatus.setInputPrompt(Common.SELECT_PROMPT);
	cbPhaseStatus.setWidth("150");
	cbPhaseStatus.setNullSelectionAllowed(false);
	cbPhaseStatus.setImmediate(true);
	cbPhaseStatus.setItemCaptionPropertyId("desc");
	beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
//	 beanStatus.addAll(Common.listStatus);
	 cbPhaseStatus.setContainerDataSource(beanStatus);
//	 cbPhaseStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
	 taPhaseRemarks = new TextArea("Remarks");
	 taPhaseRemarks.setReadOnly(false);
	
	// taPhaseRemarks.setInputPrompt("Enter Remarks");
	 taPhaseRemarks.setHeight("60");
	 chBaseLined = new CheckBox("Base Lined");
	 chBaseLined.setReadOnly(false);
	 
	 flPhaseForm1 = new FormLayout();
	 flPhaseForm1.setSpacing(true);
	 flPhaseForm1.addComponent(tfProjPhase);
	 flPhaseForm1.addComponent(dfPlanStartDate);
	 flPhaseForm1.addComponent(dfPlanEndDt);
	 flPhaseForm1.addComponent(tfPlanEffort);
	 
	 flPhaseForm2 = new FormLayout();
	 flPhaseForm2.setSpacing(true);
	 flPhaseForm2.addComponent(dfActStartDate);
	 flPhaseForm2.addComponent(dfActEndDate);
	 flPhaseForm2.addComponent(tfActEffort);
	 flPhaseForm2.addComponent(tfVersionNo);
		
	 flPhaseForm3 = new FormLayout();
	 flPhaseForm3.setSpacing(true);
	 flPhaseForm3.addComponent(cbPhaseStatus);
	 flPhaseForm3.addComponent(chBaseLined);
	 flPhaseForm3.addComponent(taPhaseRemarks);
		
		
	 flPhaseForm4 = new FormLayout();
	 flPhaseForm4.setSpacing(true);
	 
		
		hlPhaseForm = new HorizontalLayout();
		hlPhaseForm.setSpacing(true);
		hlPhaseForm.addComponent(flPhaseForm1);
		hlPhaseForm.addComponent(flPhaseForm2);
		hlPhaseForm.addComponent(flPhaseForm3);
		hlPhaseForm.addComponent(flPhaseForm4);
		
		GridLayout glPhaseform = new GridLayout(1,1);
		glPhaseform.setSizeFull();
		glPhaseform.setSpacing(true);
		glPhaseform.setMargin(false);
		glPhaseform.addComponent(hlPhaseForm);
		
		tblPhase = new Table();
		tblPhase.setImmediate(true);
		tblPhase.setSizeFull();
		tblPhase.setPageLength(3);
		
		btnAddPhase = new Button("Add",this);
		btnAddPhase.addStyleName("add");

		btnEditPhase = new Button("Edit",this);
		btnEditPhase.setEnabled(false);
		btnDownloadPhase = new Button("Download",this);
		btnHomePhase = new Button("Home",this);
		btnHomePhase.setStyleName("homebtn");
		btnAuditPhase =new Button("Audit History",this);
		btnAuditPhase.setStyleName("hostorybtn");
		//btnHomePhase.setWidth("7px");
		btnEditPhase.addStyleName("editbt");
		btnDownloadPhase.addStyleName("downloadbt");
		btnDownloadPhase.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				event.getButton().removeStyleName("unread");
				if (notificationPhase != null && notificationPhase.getUI() != null)
					notificationPhase.close();
				else {
					buildNotificationsPhase(event);
					UI.getCurrent().addWindow(notificationPhase);
					notificationPhase.focus();
					((VerticalLayout) UI.getCurrent().getContent())
							.addLayoutClickListener(new LayoutClickListener() {

								private static final long serialVersionUID = 1L;
								public void layoutClick(LayoutClickEvent event) {
									notificationPhase.close();
									((VerticalLayout) UI.getCurrent().getContent())
											.removeLayoutClickListener(this);
								}
							});
				}
			}
		});
		btnPhaseRevn = new Button("Revise",this);
		//btnPhaseRevn.setVisible(true);
		btnPhaseRevn.setEnabled(false);
		btnPhaseRevn.addStyleName("editbt");
		 
		 btnPhaseRevn.setDescription("Project Phase Revise");
		 btnEditPhase.setDescription("Edit Project Phase");
		 btnAddPhase.setDescription("Save Project Phase");
		 btnDownloadPhase.setDescription("Download");
		
		HorizontalLayout hlFiledownloadPhase = new HorizontalLayout();
		hlFiledownloadPhase.setSpacing(true);
		hlFiledownloadPhase.addComponent(btnDownloadPhase);
		hlFiledownloadPhase.setComponentAlignment(btnDownloadPhase,Alignment.MIDDLE_CENTER);
	    
	    HorizontalLayout hlTabelCaptionPhase = new HorizontalLayout();
	    hlTabelCaptionPhase.addComponent(btnHomePhase);
	    hlTabelCaptionPhase.setComponentAlignment(btnHomePhase, Alignment.MIDDLE_RIGHT);
	    hlTabelCaptionPhase.addStyleName("lightgray");
	    hlTabelCaptionPhase.setHeight("25px");
	    hlTabelCaptionPhase.setWidth("90px");
		
		HorizontalLayout hlTableTitlePhase = new HorizontalLayout(); 
		hlTableTitlePhase.addComponent(hlTabelCaptionPhase);
		hlTableTitlePhase.addComponent(btnAddPhase);
		hlTableTitlePhase.addComponent(btnEditPhase);
		hlTableTitlePhase.addComponent(btnPhaseRevn);
		hlTableTitlePhase.addComponent(btnAuditPhase);
		
		hlAddEditBtnPhase = new HorizontalLayout();
		hlAddEditBtnPhase.addStyleName("topbarthree");
		hlAddEditBtnPhase.setWidth("100%");
		hlAddEditBtnPhase.addComponent(hlTableTitlePhase);
		hlAddEditBtnPhase.addComponent(hlFiledownloadPhase);
		hlAddEditBtnPhase.setComponentAlignment(hlFiledownloadPhase,Alignment.MIDDLE_RIGHT);
		hlAddEditBtnPhase.setHeight("28px");
	
		vlPhaseTableForm = new VerticalLayout();
		vlPhaseTableForm.setSpacing(false);
		vlPhaseTableForm.addComponent(hlAddEditBtnPhase);
		vlPhaseTableForm.addComponent(tblPhase);
		 
	vlProjectPhase = new VerticalLayout();
	vlProjectPhase.setSpacing(true);
	vlProjectPhase.setMargin(true);
	vlProjectPhase.addComponent(GERPPanelGenerator.createPanel(glPhaseform));
	vlProjectPhase.addComponent(vlPhaseTableForm);
	
	tfRoleDesc = new TextField("Role Description");
	//tfRoleDesc.setInputPrompt("Enter Role Desc.");
	tfRoleDesc.setRequired(true);
	tfTeamPlanEffort = new TextField("Plan Effort");
	//tfTeamPlanEffort.setInputPrompt("Enter Plan Effort");
	tfBillRate = new TextField("Bill Rate");
	//tfBillRate.setInputPrompt("Enter Bill Rate");
	tfLoading = new TextField("Loading PRCNT");
	//tfLoading.setInputPrompt("Enter Loading PRCNT");
	taTeamRemarks = new TextArea("Remarks");
	//taTeamRemarks.setInputPrompt("Enter Remarks");
	cbTeamEmployee = new ComboBox("Employee Name");
	//cbTeamEmployee.setInputPrompt(Common.SELECT_PROMPT);
	cbTeamEmployee.setRequired(true);
	cbTeamEmployee.setImmediate(true);
	cbTeamEmployee.setWidth("150");
	cbTeamEmployee.setNullSelectionAllowed(false);
	cbTeamEmployee.setItemCaptionPropertyId("firstname");
	loadTeamEmployeeList();
	cbTeamEmployee.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					BeanItem<?> item = (BeanItem<?>) cbTeamEmployee.getItem(itemid);
					selectedEmployee = (EmployeeDM) item.getBean();
				}
			}
		});
	
	flProjectTeamForm1 = new FormLayout();
	flProjectTeamForm1.setSpacing(true);
	flProjectTeamForm1.addComponent(tfRoleDesc);
	flProjectTeamForm1.addComponent(tfTeamPlanEffort);
	flProjectTeamForm1.addComponent(tfBillRate);	
	
	flProjectTeamForm2 = new FormLayout();
	flProjectTeamForm2.setSpacing(true);
	flProjectTeamForm2.addComponent(tfLoading);
	flProjectTeamForm2.addComponent(cbTeamEmployee);
	
	flProjectTeamForm3 = new FormLayout();
	flProjectTeamForm3.addComponent(taTeamRemarks);

	HorizontalLayout hlTeamForm = new HorizontalLayout();
	hlTeamForm.setSpacing(true);
	hlTeamForm.addComponent(flProjectTeamForm1);
	hlTeamForm.addComponent(flProjectTeamForm2);
	hlTeamForm.addComponent(flProjectTeamForm3);
	
		GridLayout glTeamform = new GridLayout(1,1);
		glTeamform.setSizeFull();
		glTeamform.setSpacing(true);
		glTeamform.setMargin(false);
		glTeamform.addComponent(hlTeamForm);
		
		tblTeam = new Table();
		tblTeam.setImmediate(true);
		tblTeam.setSizeFull();
		tblTeam.setPageLength(5);
		
		btnAddTeam = new Button("Add",this);
		btnAddTeam.addStyleName("add");

		btnEditTeam = new Button("Edit",this);
		btnEditTeam.setEnabled(false);
		
		btnDownloadTeam = new Button("Download",this);
		
		btnHomeTeam = new Button("Home",this);
		btnHomeTeam.setStyleName("homebtn");
		
		btnAuditTeam =new Button("Audit History",this);
		btnAuditTeam.setStyleName("hostorybtn");
		//btnHomeTeam.setWidth("7px");
		

		
		btnEditTeam.addStyleName("editbt");
		btnDownloadTeam.addStyleName("downloadbt");
		btnDownloadTeam.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				event.getButton().removeStyleName("unread");
				if (notificationTeam != null && notificationTeam.getUI() != null)
					notificationTeam.close();
				else {
					buildNotificationsTeam(event);
					UI.getCurrent().addWindow(notificationTeam);
					notificationTeam.focus();
					((VerticalLayout) UI.getCurrent().getContent())
							.addLayoutClickListener(new LayoutClickListener() {

								private static final long serialVersionUID = 1L;
								public void layoutClick(LayoutClickEvent event) {
									notificationTeam.close();
									((VerticalLayout) UI.getCurrent().getContent())
											.removeLayoutClickListener(this);
								}
							});
				}
			}
		});
		 
		btnEditTeam.setDescription("Edit Project Phase");
		 btnAddTeam.setDescription("Save Project Team");
		 btnDownloadTeam.setDescription("Download");
		 
		 HorizontalLayout hlFiledownloadTeam = new HorizontalLayout();
		 hlFiledownloadTeam.setSpacing(true);
		 hlFiledownloadTeam.addComponent(btnDownloadTeam);
		 hlFiledownloadTeam.setComponentAlignment(btnDownloadTeam,Alignment.MIDDLE_CENTER);
		    
		    HorizontalLayout hlTabelCaptionTeam = new HorizontalLayout();
		    hlTabelCaptionTeam.addComponent(btnHomeTeam);
		    hlTabelCaptionTeam.setComponentAlignment(btnHomeTeam, Alignment.MIDDLE_RIGHT);
		    hlTabelCaptionTeam.addStyleName("lightgray");
		    hlTabelCaptionTeam.setHeight("25px");
		    hlTabelCaptionTeam.setWidth("90px");
			
			HorizontalLayout hlTableTitleTeam = new HorizontalLayout(); 
			hlTableTitleTeam.addComponent(hlTabelCaptionTeam);
			hlTableTitleTeam.addComponent(btnAddTeam);
			hlTableTitleTeam.addComponent(btnEditTeam);
			hlTableTitleTeam.addComponent(btnAuditTeam);
			
			hlAddEditBtnTeam = new HorizontalLayout();
			hlAddEditBtnTeam.addStyleName("topbarthree");
			hlAddEditBtnTeam.setWidth("100%");
			hlAddEditBtnTeam.addComponent(hlTableTitleTeam);
			hlAddEditBtnTeam.addComponent(hlFiledownloadTeam);
			hlAddEditBtnTeam.setComponentAlignment(hlFiledownloadTeam,Alignment.MIDDLE_RIGHT);
			hlAddEditBtnTeam.setHeight("28px"); 
	
	vlTeamTableForm = new VerticalLayout();
	vlTeamTableForm.setSpacing(false);
	vlTeamTableForm.addComponent(hlAddEditBtnTeam);
	vlTeamTableForm.addComponent(tblTeam);		
	
	vlProjectTeam = new VerticalLayout();
	vlProjectTeam.setSpacing(true);
	vlProjectTeam.setMargin(true);
	vlProjectTeam.addComponent(GERPPanelGenerator.createPanel(glTeamform));
	vlProjectTeam.addComponent(hlAddEditBtnTeam);
	vlProjectTeam.addComponent(vlTeamTableForm);
	
	tfRevnEffort = new TextField("Effot");
	//tfRevnEffort.setInputPrompt("Enter Effort");
	tfRevnPenalty = new TextField("Penalty Amount");
	//tfRevnPenalty.setInputPrompt("Enter Penalty Amt.");
	tfRevnVersion = new TextField("Version No.");
	dfRevnStartdt = new PopupDateField("Start Date");
	//dfRevnStartdt.setInputPrompt(Common.SELECT_PROMPT);
	dfRevnStartdt.setRequired(true);
	dfRevnEnddt = new PopupDateField("End Date");
	//dfRevnEnddt.setInputPrompt(Common.SELECT_PROMPT);
	dfRevnEnddt.setRequired(true);
	taJustification = new TextArea("Justification");
	//taJustification.setInputPrompt("Enter Justification");
	taClientRemarks = new TextArea("Client Remarks");
	//taClientRemarks.setInputPrompt("Enter Client Remarks");
	chClientAgreed = new CheckBox("Client Agreed");
	
	flPhaseRevnForm1 = new FormLayout();
	flPhaseRevnForm1.setSpacing(true);
	flPhaseRevnForm1.addComponent(dfRevnStartdt);
	flPhaseRevnForm1.addComponent(dfRevnEnddt);
	flPhaseRevnForm1.addComponent(tfRevnEffort);
	
	flPhaseRevnForm2 = new FormLayout();
	flPhaseRevnForm2.setSpacing(true);
	flPhaseRevnForm2.addComponent(tfRevnPenalty);
	flPhaseRevnForm2.addComponent(tfRevnVersion);
	flPhaseRevnForm2.addComponent(chClientAgreed);
	
	flPhaseRevnForm3 = new FormLayout();
	flPhaseRevnForm3.setSpacing(true);
	flPhaseRevnForm3.addComponent(taJustification);
	
	flPhaseRevnForm4 = new FormLayout();
	flPhaseRevnForm4.setSpacing(true);
	flPhaseRevnForm4.addComponent(taClientRemarks);
	
	HorizontalLayout hlRevisionForm = new HorizontalLayout();
	hlRevisionForm.setSpacing(true);
	hlRevisionForm.addComponent(flPhaseRevnForm1);
	hlRevisionForm.addComponent(flPhaseRevnForm2);
	hlRevisionForm.addComponent(flPhaseRevnForm3);
	hlRevisionForm.addComponent(flPhaseRevnForm4);
	
		GridLayout glRevisionform = new GridLayout(1,1);
		glRevisionform.setSizeFull();
		glRevisionform.setSpacing(true);
		glRevisionform.setMargin(false);
		glRevisionform.addComponent(hlRevisionForm);
		
		btnAddRevision = new Button("Add",this);
		btnAddRevision.addStyleName("add");

		btnDownloadRevn = new Button("Download");
		btnDownloadRevn.addStyleName("downloadbt");
		
		HorizontalLayout hlFiledownloadRevn = new HorizontalLayout();
		hlFiledownloadRevn.setSpacing(true);
		hlFiledownloadRevn.addComponent(btnDownloadRevn);
		hlFiledownloadRevn.setComponentAlignment(btnDownloadRevn,Alignment.MIDDLE_CENTER);
		    
		    HorizontalLayout hlTabelCaptionRevn = new HorizontalLayout();
		    hlTabelCaptionRevn.addStyleName("lightgray");
		    hlTabelCaptionRevn.setHeight("25px");
		    hlTabelCaptionRevn.setWidth("90px");
		
		    HorizontalLayout hlTableTitleRevn = new HorizontalLayout(); 
		    hlTableTitleRevn.addComponent(hlTabelCaptionRevn);
		    hlTableTitleRevn.addComponent(btnAddRevision);
			
			hlAddEditBtnRevn = new HorizontalLayout();
			hlAddEditBtnRevn.addStyleName("topbarthree");
			hlAddEditBtnRevn.setWidth("100%");
			hlAddEditBtnRevn.addComponent(hlTableTitleRevn);
			hlAddEditBtnRevn.addComponent(hlFiledownloadRevn);
			hlAddEditBtnRevn.setComponentAlignment(hlFiledownloadRevn,Alignment.MIDDLE_RIGHT);
			hlAddEditBtnRevn.setHeight("28px"); 	    
	
	tblPhaseRevn = new Table();
	tblPhaseRevn.setImmediate(true);
	tblPhaseRevn.setSizeFull();
	tblPhaseRevn.setPageLength(5);
	
	vlProjectPhaseRevision = new VerticalLayout();
	vlProjectPhaseRevision.setSpacing(true);
	vlProjectPhaseRevision.setMargin(true);
	vlProjectPhaseRevision.addComponent(GERPPanelGenerator.createPanel(glRevisionform));
	vlProjectPhaseRevision.addComponent(hlAddEditBtnRevn);
	vlProjectPhaseRevision.addComponent(tblPhaseRevn);
	
	tabsheet = new TabSheet();
	tabsheet.setSizeFull();
	tabsheet.setImmediate(true);
	tabsheet.addTab(vlProjectBill,"Project Billing");
	tabsheet.addTab(vlProjectPhase,"Project Phase");
	tabsheet.addTab(vlProjectPhaseRevision,"Project Phase Revise");
	tabsheet.addTab(vlProjectTeam,"Project Team");
	tabsheet.getTab(vlProjectBill).setEnabled(true);
	tabsheet.getTab(vlProjectPhase).setEnabled(true);
	tabsheet.getTab(vlProjectTeam).setEnabled(false);
	tabsheet.getTab(vlProjectPhaseRevision).setEnabled(false);
		
	    FormLayout flSearchProject=new FormLayout();
	    flSearchProject.addComponent(tfSearchProject);
	    FormLayout flSearchStatus=new FormLayout();
		flSearchStatus.addComponent(cbSearchStatus);
		
    HorizontalLayout hlSearchform=new HorizontalLayout(); 
    hlSearchform.setSpacing(true);
    hlSearchform.setMargin(true);
    hlSearchform.addComponent(flSearchProject);
    hlSearchform.addComponent(flSearchStatus);
    
    VerticalLayout vlSearchBtn = new VerticalLayout();
    vlSearchBtn.setSpacing(true);
    vlSearchBtn.addComponent(btnSearch);
    vlSearchBtn.addComponent(btnReset);
    vlSearchBtn.setWidth("17%");
    vlSearchBtn.addStyleName("topbarthree");
    vlSearchBtn.setMargin(true);
	
	HorizontalLayout hlSearchComponent = new HorizontalLayout();
	hlSearchComponent.setSizeFull();
	hlSearchComponent.setSpacing(true);
	hlSearchComponent.addComponent(hlSearchform);
	hlSearchComponent.setComponentAlignment(hlSearchform, Alignment.MIDDLE_LEFT);
	hlSearchComponent.addComponent(vlSearchBtn);
	hlSearchComponent.setComponentAlignment(vlSearchBtn,Alignment.MIDDLE_RIGHT);
	
	vlSearchPanel.addComponent(GERPPanelGenerator.createPanel(hlSearchComponent));
	vlSearchPanel.setMargin(true);
    
    HorizontalLayout hlFiledownload = new HorizontalLayout();
    hlFiledownload.setSpacing(true);
    hlFiledownload.addComponent(btnDownload);
    hlFiledownload.setComponentAlignment(btnDownload,Alignment.MIDDLE_CENTER);
    
    HorizontalLayout hlTabelCaption = new HorizontalLayout();
    hlTabelCaption.addComponent(btnHome);
	hlTabelCaption.setComponentAlignment(btnHome, Alignment.MIDDLE_RIGHT);
    hlTabelCaption.addStyleName("lightgray");
    hlTabelCaption.setHeight("25px");
    hlTabelCaption.setWidth("90px");
    
	HorizontalLayout hlTableTitle = new HorizontalLayout(); 
	hlTableTitle.addComponent(hlTabelCaption);
	hlTableTitle.addComponent(btnAdd);
	hlTableTitle.addComponent(btnEdit);
	hlTableTitle.addComponent(btnAudit);
	
    hlAddEditBtn = new HorizontalLayout();
	hlAddEditBtn.addStyleName("topbarthree");
	hlAddEditBtn.setWidth("100%");
	hlAddEditBtn.addComponent(hlTableTitle);
	hlAddEditBtn.addComponent(hlFiledownload);
	hlAddEditBtn.setComponentAlignment(hlFiledownload,Alignment.MIDDLE_RIGHT);
	hlAddEditBtn.setHeight("28px");
	
	 vlTableForm = new VerticalLayout();
	 vlTableForm.setSizeFull();
	 vlTableForm.setMargin(true);
	 vlTableForm.addComponent(hlAddEditBtn);
	 vlTableForm.addComponent(tblProject);
	
    lblNoofRecords=new Label(" ",ContentMode.HTML);
    lblNoofRecords.addStyleName("lblfooter");
	
	vlMainPanel.addComponent(GERPPanelGenerator.createPanel(glMainPanel));
	vlMainPanel.addComponent(tabsheet);
	vlMainPanel.setSpacing(true);
	vlTablePanel.addComponent(vlTableForm);
	vlTablePanel.setMargin(false);
	
	clMainLayout.addComponent(vlMainPanel);
	clMainLayout.addComponent(vlSearchPanel);
	clMainLayout.addComponent(vlTablePanel);
	
	HorizontalLayout hlNotification = new HorizontalLayout();
	hlNotification.addComponent(lblNotificationIcon);
	hlNotification.setComponentAlignment(lblNotificationIcon,Alignment.MIDDLE_CENTER);
	hlNotification.addComponent(lblNotification);
	hlNotification.setComponentAlignment(lblNotification,Alignment.MIDDLE_CENTER);
	hlScreenNameLayout.addComponent(lblFormTitle);
	hlScreenNameLayout.setComponentAlignment(lblFormTitle, Alignment.MIDDLE_LEFT);
	hlScreenNameLayout.addComponent(hlNotification);
	hlScreenNameLayout.setComponentAlignment(hlNotification, Alignment.MIDDLE_CENTER);
	hlScreenNameLayout.addComponent(hlSaveBtn);
	hlScreenNameLayout.setComponentAlignment(hlSaveBtn, Alignment.MIDDLE_RIGHT);
	
	setTableProperties();
    populateAndConfigureTable(false);	
    
    excelexporter.setTableToBeExported(tblProject);
	csvexporter.setTableToBeExported(tblProject);
	pdfexporter.setTableToBeExported(tblProject);
	excelexporter.setCaption("Microsoft Excel (XLS)");
	excelexporter.setStyleName("borderless");
	csvexporter.setCaption("Comma Dilimited (CSV)");
	csvexporter.setStyleName("borderless");
	pdfexporter.setCaption("Acrobat Document (PDF)");
	pdfexporter.setStyleName("borderless");
	
	 excelexporterBill = new ExcelExporter();
	  csvexporterBill = new CSVExporter();
      pdfexporterBill = new PdfExporter();
	 excelexporterBill.setTableToBeExported(tblBill);
	 csvexporterBill.setTableToBeExported(tblBill);
	 pdfexporterBill.setTableToBeExported(tblBill);
	 excelexporterBill.setCaption("Microsoft Excel (XLS)");
	 excelexporterBill.setStyleName("borderless");
	 csvexporterBill.setCaption("Comma Dilimited (CSV)");
	 csvexporterBill.setStyleName("borderless");
	 pdfexporterBill.setCaption("Acrobat Document (PDF)");
	 pdfexporterBill.setStyleName("borderless");
	 
	 excelexporterPhase = new ExcelExporter();
	  csvexporterPhase = new CSVExporter();
     pdfexporterPhase = new PdfExporter();
     excelexporterPhase.setTableToBeExported(tblPhase);
     csvexporterPhase.setTableToBeExported(tblPhase);
     pdfexporterPhase.setTableToBeExported(tblPhase);
	 excelexporterPhase.setCaption("Microsoft Excel (XLS)");
	 excelexporterPhase.setStyleName("borderless");
	 csvexporterPhase.setCaption("Comma Dilimited (CSV)");
	 csvexporterPhase.setStyleName("borderless");
	 pdfexporterPhase.setCaption("Acrobat Document (PDF)");
	 pdfexporterPhase.setStyleName("borderless");
	 
	 excelexporterTeam = new ExcelExporter();
	 csvexporterTeam = new CSVExporter();
    pdfexporterTeam = new PdfExporter();
    excelexporterTeam.setTableToBeExported(tblTeam);
    csvexporterTeam.setTableToBeExported(tblTeam);
    pdfexporterTeam.setTableToBeExported(tblTeam);
    excelexporterTeam.setCaption("Microsoft Excel (XLS)");
    excelexporterTeam.setStyleName("borderless");
    csvexporterTeam.setCaption("Comma Dilimited (CSV)");
    csvexporterTeam.setStyleName("borderless");
    pdfexporterTeam.setCaption("Acrobat Document (PDF)");
    pdfexporterTeam.setStyleName("borderless");
}
    private String stringdate() {
    	DateFormat formatter ; 
		formatter = new SimpleDateFormat("dd-MMM-yyyy");
	 return formatter.format(dfBroughtDt);
		// TODO Auto-generated method stub
		
	}
	/*
     * buildNotifications()-->this function is used for poppupview for Download
     * components
     */
    private void buildNotifications(ClickEvent event) {
    	notifications = new Window();
    	VerticalLayout vlNotifyWindow = new VerticalLayout();
    	vlNotifyWindow.setMargin(true);
    	vlNotifyWindow.setSpacing(true);
    	notifications.setWidth("165px");
    	notifications.addStyleName("notifications");
    	notifications.setClosable(false);
    	notifications.setResizable(false);
    	notifications.setDraggable(false);
    	notifications.setPositionX(event.getClientX() - event.getRelativeX());
    	notifications.setPositionY(event.getClientY() - event.getRelativeY());
    	notifications.setCloseShortcut(KeyCode.ESCAPE, null);

    	VerticalLayout vlDownload = new VerticalLayout();
    	vlDownload.addComponent(excelexporter);
    	vlDownload.addComponent(csvexporter);
    	vlDownload.addComponent(pdfexporter);
    	vlDownload.setSpacing(true);

    	notifications.setContent(vlDownload);

    }   
    private void buildNotificationsBill(ClickEvent event) {
    	notificationBill = new Window();
    	VerticalLayout vlNotifyWindow = new VerticalLayout();
    	vlNotifyWindow.setMargin(true);
    	vlNotifyWindow.setSpacing(true);
    	notificationBill.setWidth("165px");
    	notificationBill.addStyleName("notifications");
    	notificationBill.setClosable(false);
    	notificationBill.setResizable(false);
    	notificationBill.setDraggable(false);
    	notificationBill.setPositionX(event.getClientX() - event.getRelativeX());
    	notificationBill.setPositionY(event.getClientY() - event.getRelativeY());
    	notificationBill.setCloseShortcut(KeyCode.ESCAPE, null);

    	VerticalLayout vlDownload = new VerticalLayout();
    	vlDownload.addComponent(excelexporterBill);
    	vlDownload.addComponent(csvexporterBill);
    	vlDownload.addComponent(pdfexporterBill);
    	vlDownload.setSpacing(true);

    	notificationBill.setContent(vlDownload);

    }   
    private void buildNotificationsPhase(ClickEvent event) {
    	notificationPhase = new Window();
    	VerticalLayout vlNotifyWindow = new VerticalLayout();
    	vlNotifyWindow.setMargin(true);
    	vlNotifyWindow.setSpacing(true);
    	notificationPhase.setWidth("165px");
    	notificationPhase.addStyleName("notifications");
    	notificationPhase.setClosable(false);
    	notificationPhase.setResizable(false);
    	notificationPhase.setDraggable(false);
    	notificationPhase.setPositionX(event.getClientX() - event.getRelativeX());
    	notificationPhase.setPositionY(event.getClientY() - event.getRelativeY());
    	notificationPhase.setCloseShortcut(KeyCode.ESCAPE, null);

    	VerticalLayout vlDownloadPhase = new VerticalLayout();
    	vlDownloadPhase.addComponent(excelexporterPhase);
    	vlDownloadPhase.addComponent(csvexporterPhase);
    	vlDownloadPhase.addComponent(pdfexporterPhase);
    	vlDownloadPhase.setSpacing(true);

    	notificationPhase.setContent(vlDownloadPhase);

    }   
    private void buildNotificationsTeam(ClickEvent event) {
    	notificationTeam = new Window();
    	VerticalLayout vlNotifyWindow = new VerticalLayout();
    	vlNotifyWindow.setMargin(true);
    	vlNotifyWindow.setSpacing(true);
    	notificationTeam.setWidth("165px");
    	notificationTeam.addStyleName("notifications");
    	notificationTeam.setClosable(false);
    	notificationTeam.setResizable(false);
    	notificationTeam.setDraggable(false);
    	notificationTeam.setPositionX(event.getClientX() - event.getRelativeX());
    	notificationTeam.setPositionY(event.getClientY() - event.getRelativeY());
    	notificationTeam.setCloseShortcut(KeyCode.ESCAPE, null);

    	VerticalLayout vlDownloadTeam = new VerticalLayout();
    	vlDownloadTeam.addComponent(excelexporterTeam);
    	vlDownloadTeam.addComponent(csvexporterTeam);
    	vlDownloadTeam.addComponent(pdfexporterTeam);
    	vlDownloadTeam.setSpacing(true);

    	notificationTeam.setContent(vlDownloadTeam);

    }   
    /*
     * populateAndConfigureTable()-->this function used to load the list to the table.
     * 
     * @param boolean search if(search==true)--> it performs search operation
     * else it loads all values
     */
private void populateAndConfigureTable(boolean search) {
	try {
		tblProject.removeAllItems();
    	 List<ProjectDM> projectList=new ArrayList<ProjectDM>();
    	if(search)
    	{
    		projectList=new ArrayList<ProjectDM>();
    		String statusArg=null;
    		String projectNameArg=tfSearchProject.getValue().toString();
    		StatusDM sts= (StatusDM) cbSearchStatus.getValue();
    		try {
    			statusArg = sts.getCode();
			} catch (Exception e) {
				logger.info("status is empty on search");
			}
    		if (projectNameArg != null || statusArg!=null ) {
//    			projectList =servProjectsBean.getProjectList(projectNameArg,companyId,null, statusArg);
			total = projectList.size();
			}
			if (total == 0) {
				Notification.show("NO RECORDS FOUND");/*BALA*/
				lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
   			    lblNotification.setValue("Update failed, please check the data and try again ");
				
			}
			 
		}
    	else {
//    		projectList = servProjectsBean.getProjectList(null,companyId,null,null);
			total = projectList.size();
		}
    //	lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
    	 beanProject = new BeanItemContainer<ProjectDM>(ProjectDM.class);
    	 beanProject.addAll(projectList);
    	tblProject.setContainerDataSource(beanProject);
    	tblProject.setSelectable(true);
    	tblProject.setColumnAlignment("projectId", Align.RIGHT);
    	tblProject.setColumnFooter("lastUpdatedBy","No. of Records:"+total);
    	tblProject.setHeight("380");
    	tblProject.setVisibleColumns(new Object[] {"projectId","projectName","projectType","employeeName","projectStatus","lastUpdtDate","lastUpdatedBy"});
    	tblProject.setColumnHeaders(new String[] {"Ref.Id","Project Name","Project Type","Employee Name","Status","Last Updated Date","Last Updated By"});	 
    	
    	tblProject.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
				public void itemClick(ItemClickEvent event) {
    				// TODO Auto-generated method stub
    				if (tblProject.isSelected(event.getItemId())) {
    					btnAdd.setEnabled(true);
    					btnEdit.setEnabled(false);	
    				} else {
    					btnAdd.setEnabled(false);
    					btnEdit.setEnabled(true);	
    				}
    				resetFields();			
    			}
    		});
	} catch(Exception e) {
		e.printStackTrace();
		logger.error("error during populate values on the table, The Error is ----->"+e);
	}
    }
/*
 * populateAndConfigureBillingTable()-->this function used to load the list to the project Bill table.
 * 
 */
private void populateAndConfigureBillingTable(Long projectid) {
	try {
		tblBill.removeAllItems();
    	if(projectid!=null) {
    		tempSaveBill = servProjbillBean.getProjectBillList(null,null, null,projectid);
    	}
    	lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
    	 beanProjBill = new BeanItemContainer<ProjectBillDM>(ProjectBillDM.class);
    	 beanProjBill.addAll(tempSaveBill);
    	 tblBill.setContainerDataSource(beanProjBill);
    	 tblBill.setSelectable(true);
    	 tblBill.setColumnAlignment("projectBillId", Align.RIGHT);
    	 tblBill.setColumnAlignment("paidAmount", Align.RIGHT);
    	 tblBill.setColumnAlignment("billAmount", Align.RIGHT);
    	 tblBill.setColumnFooter("lastUpdatedBy","No. of Records:"+total);
    	 tblBill.setVisibleColumns(new Object[] {"projectBillId","billingDesc","billAmount","invoiceNumber","paidAmount","billStatus","lastUpdtDate","lastUpdatedBy"});
    	 tblBill.setColumnHeaders(new String[] {"Ref.Id","Billing Description","Bill Amount","Invoice Number","Paid Amount","Status","Last Updated Date","Last Updated By"});	 
    	
    	 tblBill.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
				public void itemClick(ItemClickEvent event) {
    				// TODO Auto-generated method stub
    				if (tblBill.isSelected(event.getItemId())) {
    					btnAddBill.setEnabled(true);
    					btnEditBill.setEnabled(false);	
    				} else {
    					btnAddBill.setEnabled(false);
    					btnEditBill.setEnabled(true);	
    				}
		resetFieldsBilling();
    			}
    		});
	} catch(Exception e) {
		e.printStackTrace();
		logger.error("error during populate values on the table, The Error is ----->"+e);
	}
    }
/*
 * populateAndConfigurePhaseTable()-->this function used to load the list to the project Phase table.
 * 
 */
private void populateAndConfigurePhaseTable(Long projectids) {
	
	try {
		tblPhase.removeAllItems();
		String s = null;
		if(projectids!=null) {
			tempSavePhase = servProjPhaseBean.getProjectPhaseList(null,null,null, projectids);
			tfVersionNo.setValue(tempSavePhase.size()+1+"");
		}else{
			s = (tempSavePhase.size()==0)?"1":tempSavePhase.size()+1+"";
			tfVersionNo.setValue(s);
		}
		
		
    	lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
        beanPhase = new BeanItemContainer<ProjectPhaseDM>(ProjectPhaseDM.class);
        beanPhase.addAll(tempSavePhase);
    	 tblPhase.setContainerDataSource(beanPhase);
    	 tblPhase.setSelectable(true);
    	 tblPhase.setColumnAlignment("projectPhaseId", Align.RIGHT);
    	 tblPhase.setColumnFooter("lastUpdatedBy","No. of Records:"+total);
    	 tblPhase.setVisibleColumns(new Object[] {"projectPhaseId","projectPhase","planStartDate","planEndDate","phaseStatus","lastUpdtDate","lastUpdatedBy"});
    	 tblPhase.setColumnHeaders(new String[] {"Ref.Id","Project Phase","Plan Start Date","Plan End Date","Status","Last Updated Date","Last Updated By"});	 
    	
    	 tblPhase.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
				public void itemClick(ItemClickEvent event) {
    				// TODO Auto-generated method stub
    				if (tblPhase.isSelected(event.getItemId())) {
    					btnAddPhase.setEnabled(true);
    					btnEditPhase.setEnabled(false);	
    					btnPhaseRevn.setEnabled(false);	
    				} else {
    					btnAddPhase.setEnabled(false);
    					btnEditPhase.setEnabled(true);	
    					btnPhaseRevn.setEnabled(true);	
    				}
		resetFieldsPhase();
    			}
    		});
	} catch(Exception e) {
		e.printStackTrace();
		logger.error("error during populate values on the table, The Error is ----->"+e);
	}
    }
private void populateAndConfigurePhaseRevisionTable(Long phaseids) {
	try {
		tblPhaseRevn.removeAllItems();
		if(phaseids!=null) {
			System.out.println("populate and config for revision -------------->>>>>>"+phaseids);
//			tempSaveRevn = servProjPhaseRevnBean.getProjectPhaseRevisionList(phaseids);
		}
		System.out.println("revison table populate---------->>>>>>>>>");
    	lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
        beanPhaseRevn = new BeanItemContainer<ProjectPhaseRevnDM>(ProjectPhaseRevnDM.class);
        beanPhaseRevn.addAll(tempSaveRevn);
        tblPhaseRevn.setContainerDataSource(beanPhaseRevn);
        tblPhaseRevn.setSelectable(true);
        tblPhaseRevn.setColumnAlignment("projectRevisionId", Align.RIGHT);
        tblPhaseRevn.setColumnAlignment("penaltyAmount", Align.RIGHT);
        tblPhaseRevn.setColumnFooter("lastUpdatedBy","No. of Records:"+total);
        tblPhaseRevn.setVisibleColumns(new Object[] {"projectRevisionId","newstartDate","newendDate","penaltyAmount","lastUpdtDate","lastUpdatedBy"});
        tblPhaseRevn.setColumnHeaders(new String[] {"Ref.Id","Start Date","End Date","Penalty Amount","Last Updated Date","Last Updated By"});	 
	
} catch(Exception e) {
	e.printStackTrace();
}
}
/*
 * populateAndConfigureTeamTable()-->this function used to load the list to the project Team table.
 * 
 */
private void populateAndConfigureTeamTable(Long projPhaseId) {
	try {
		tblTeam.removeAllItems();
    	if(projPhaseId!=null) {
    		System.out.println("get project phase id for project team------------>>>>>>>>"+projPhaseId);
    		tempSaveTeam = servProjTeamBean.getProjectTeamList(null,null,null, projPhaseId);
    	}
    	lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
    	 beanProjectTeam = new BeanItemContainer<ProjectTeamDM>(ProjectTeamDM.class);
    	 beanProjectTeam.addAll(tempSaveTeam);
        tblTeam.setContainerDataSource(beanProjectTeam);
        tblTeam.setSelectable(true);
        tblTeam.setColumnAlignment("projectTeamId", Align.RIGHT);
        tblTeam.setColumnAlignment("planEffort", Align.RIGHT);
        tblTeam.setColumnAlignment("billRate", Align.RIGHT);
        tblTeam.setColumnFooter("lastUpdatedBy","No. of Records:"+total);
        tblTeam.setVisibleColumns(new Object[] {"projectTeamId","roleDesc","planEffort","billRate","lastUpdtDate","lastUpdatedBy"});
        tblTeam.setColumnHeaders(new String[] {"Ref.Id","Role Description","Plan Effort","Bill Rate","Last Updated Date","Last Updated By"});	 
    	
        tblTeam.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
				public void itemClick(ItemClickEvent event) {
    				// TODO Auto-generated method stub
    				if (tblBill.isSelected(event.getItemId())) {
    					btnAddTeam.setEnabled(true);
    					btnEditTeam.setEnabled(false);	
    				} else {
    					btnAddTeam.setEnabled(false);
    					btnEditTeam.setEnabled(true);	
    				}
		resetFieldsTeam();
    			}
    		});
	} catch(Exception e) {
		e.printStackTrace();
		logger.error("error during populate values on the table, The Error is ----->"+e);
	}
    }
/*
 * resetFields()->this function is used for reset the UI components
 */
     private void resetFields() {
    	 projectidAudit = null;
    	 tfProjectName.setValue(""); 
    	 tfProjectName.setComponentError(null);
    	 cbEmployeName.setValue(null);
    	 cbEmployeName.setComponentError(null);
    	 cbCurrencyName.setValue(null);
    	 cbProjectType.setValue(null);
    	 cbProjectSubType.setValue(null);
    	 tfBroughtBy.setValue("0");
    	 cbBroughtby.setValue(null);
    	 tfProjectEffort.setValue("0");
    	 chTrack.setValue(null);
    	 tfProjectCost.setValue("0");
//    	 cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
    	 cbClientName.setValue(null);
    	 cbClientName.setComponentError(null);
    	 dfBroughtDt.setValue(null);
    	 dfStartDt.setValue(null);
    	 dfStartDt.setComponentError(null);
    	 dfEndDt.setValue(null);
    	 dfEndDt.setComponentError(null);
    	 btnSave.setComponentError(null);
    	 tfProjectName.setComponentError(null);
    	btnSave.setCaption("Save"); 
    	lblFormTitle.setValue("&nbsp;&nbsp;<b>"+ screenName+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Home</font> &nbsp;>&nbsp;Add New");
    	lblNotificationIcon.setIcon(null);
    	lblNotification.setValue("");
    	projectId = null;
     }
     /*
      * resetFieldsBilling()->this function is used for reset the UI components
      */
     private void resetFieldsBilling() {
    	 tfBillDesc.setValue(""); 
    	 tfBillDesc.setComponentError(null);
    	 tfBillAmount.setValue("0");
    	 cbReceivedBy.setValue(null);
    	 cbReceivedBy.setComponentError(null);
    	 dfdueDate.setValue(null);
    	 dfdueDate.setComponentError(null);
    	 tfInvoiceNo.setValue("0");
    	 dfInvoiceDt.setValue(null);
    	 dfPaidDate.setValue(null);
    	 dfPaidDate.setComponentError(null);
    	 tfPaidAmount.setValue("0");
//    	 cbBillStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
    	 taBillRemarks.setValue("");
    	 taPaymentRemarks.setValue("");
    	 btnAddBill.setComponentError(null);
    	 btnAddBill.setCaption("Add");
    	
     }
     /*
      * resetFieldsPhase()->this function is used for reset the UI components
      */
     private void resetFieldsPhase() {
    	 tfProjPhase.setReadOnly(false);
    	 tfProjPhase.setValue(""); 
    	 tfProjPhase.setComponentError(null);
    	 tfVersionNo.setReadOnly(false);
    	 tfVersionNo.setValue("0");
    	 dfActEndDate.setReadOnly(false);
    	 dfActEndDate.setValue(null);
    	 dfActStartDate.setReadOnly(false);
    	 dfActStartDate.setValue(null);
    	 tfActEffort.setReadOnly(false);
    	 tfActEffort.setValue("0");
    	 dfPlanStartDate.setReadOnly(false);
    	 dfPlanStartDate.setValue(null);
    	 dfPlanStartDate.setComponentError(null);
    	 dfPlanEndDt.setReadOnly(false);
    	 dfPlanEndDt.setValue(null);
    	 dfPlanEndDt.setComponentError(null);
    	 tfPlanEffort.setReadOnly(false);
    	 tfPlanEffort.setValue("0");
    	 cbPhaseStatus.setReadOnly(false);
//    	 cbPhaseStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
    	 taPhaseRemarks.setReadOnly(false);
    	 taPhaseRemarks.setValue("");
    	 chBaseLined.setReadOnly(false);
    	 chBaseLined.setValue(null);
    	 btnAddPhase.setComponentError(null);
    	 btnAddPhase.setCaption("Add");
     }
   private void resetFieldsRevision() {
    	 dfRevnStartdt.setValue(null);
    	 dfRevnStartdt.setComponentError(null);
    	 dfRevnEnddt.setValue(null);
    	 dfRevnEnddt.setComponentError(null);
    	 tfRevnEffort.setValue("0");
    	 tfRevnPenalty.setValue("0");
    	 tfRevnVersion.setValue("0");
    	 btnAddRevision.setComponentError(null);
    	 taClientRemarks.setValue("");
    	 taJustification.setValue("");
    	 chClientAgreed.setValue(null);
    	 
     }
     /*
      * resetFieldsTeam()->this function is used for reset the UI components
      */
     private void resetFieldsTeam() {
    	 tfRoleDesc.setValue(""); 
    	 tfRoleDesc.setComponentError(null);
    	 tfLoading.setValue("0");
    	 tfBillRate.setValue("0");
    	 cbTeamEmployee.setValue(null);
    	 cbTeamEmployee.setComponentError(null);
    	 tfTeamPlanEffort.setValue("0");
    	 taTeamRemarks.setValue("");
    	 btnAddTeam.setComponentError(null);
    	 btnAddTeam.setCaption("Add");
     }
     /*
 	 * editState()-->this function used for edit the existing details
 	 */
     private void editProjectsDetails(){
    	 Item select=tblProject.getItem(tblProject.getValue());
    	 if (select != null)
    	 {
    		 ProjectDM editProjectlist=beanProject.getItem(tblProject.getValue()).getBean();
    		 if(editProjectlist.getProjectName()!=null) {
    		 tfProjectName.setValue(select.getItemProperty("projectName").getValue().toString());	
    		 }
    		
    		if(editProjectlist.getProjectCost()!=null) {
    			tfProjectCost.setValue(select.getItemProperty("projectCost").getValue().toString());	
    		}
    		if(editProjectlist.getProjectEffort()!=null) {
    			tfProjectEffort.setValue(select.getItemProperty("projectEffort").getValue().toString());	
    		}
    		if(editProjectlist.getProjectType()!=null) {
    			cbProjectType.setValue(select.getItemProperty("projectType").getValue().toString());	
    		}
    		if(editProjectlist.getProjectSubType()!=null) {
    			cbProjectSubType.setValue(select.getItemProperty("projectSubType").getValue().toString());	
    		}
    		
    		if(editProjectlist.getBroughtDate()!=null) {
    			dfBroughtDt.setValue((Date)select.getItemProperty("broughtDate").getValue());	
    		}
    		if(editProjectlist.getStartDate()!=null) {
    			dfStartDt.setValue((Date)select.getItemProperty("startDate").getValue());	
    		}
    		if(editProjectlist.getEndDate()!=null) {
    			dfEndDt.setValue((Date)select.getItemProperty("endDate").getValue());	
    		}
    	 String gtCode = select.getItemProperty("projectStatus").getValue().toString();
//    	 cbStatus.setValue(Common.getStatus(gtCode)); 
    	 
    	 String str=select.getItemProperty("trackKPA").getValue().toString();
			if(str.equals("Y"))
			{
				chTrack.setValue(true);		
			}
			else {
				chTrack.setValue(false);
			}
    	 
//			CurrencyDM editGetCurrency = editProjectlist.getCurrencyId();
 			Collection<?> collcurr=cbCurrencyName.getItemIds();
 			for(Iterator<?> iterator=collcurr.iterator(); iterator.hasNext();) {
 				Object itemid=(Object)iterator.next();
 				BeanItem<?> item=(BeanItem<?>) cbCurrencyName.getItem(itemid);
 				CurrencyDM editCurrBean=(CurrencyDM) item.getBean();
// 				if (editGetCurrency != null && editGetCurrency.getCcyid().equals(editCurrBean.getCcyid())){
 					cbCurrencyName.setValue(itemid);
					break;
// 				} else {
// 					cbCurrencyName.setValue(null);
// 				}
 			}
// 			EmployeeDM  editGetEmp = editProjectlist.getEmployeeId();
 			Collection<?> collemp=cbEmployeName.getItemIds();
 			for(Iterator<?> iterator=collemp.iterator(); iterator.hasNext();) {
 				Object itemid=(Object)iterator.next();
 				BeanItem<?> item=(BeanItem<?>) cbEmployeName.getItem(itemid);
 				EmployeeDM editEmpBean=(EmployeeDM) item.getBean();
// 				if (editGetEmp != null && editGetEmp.getEmployeeid().equals(editEmpBean.getEmployeeid())){
 					cbEmployeName.setValue(itemid);
//					break;
// 				} else {
// 					cbEmployeName.setValue(null);
// 				}
 			}	
// 			EmployeeDM editGetBrought = editProjectlist.getBroughtBy();
 			Collection<?> collBrought=cbBroughtby.getItemIds();
 			for(Iterator<?> iterator=collBrought.iterator(); iterator.hasNext();) {
 				Object itemid=(Object)iterator.next();
 				BeanItem<?> item=(BeanItem<?>) cbBroughtby.getItem(itemid);
 				EmployeeDM editBroughtBean=(EmployeeDM) item.getBean();
// 				if (editGetBrought != null && editGetBrought.getEmployeeid().equals(editBroughtBean.getEmployeeid())){
 					cbBroughtby.setValue(itemid);
//					break;
// 				} else {
// 					cbBroughtby.setValue(null);
// 				}
 			}	
// 			ClientDM editGetClient = editProjectlist.getClientId();
 			Collection<?> collClient=cbClientName.getItemIds();
 			for(Iterator<?> iterator=collClient.iterator(); iterator.hasNext();) {
 				Object itemid=(Object)iterator.next();
 				BeanItem<?> item=(BeanItem<?>) cbClientName.getItem(itemid);
 				ClientDM editClientBean=(ClientDM) item.getBean();
// 				if (editGetClient != null && editGetClient.getClientId().equals(editClientBean.getClientId())){
 					cbClientName.setValue(itemid);
//					break;
// 				} else {
// 					cbClientName.setValue(null);
// 				}
 			}		
 			
 			if(editProjectlist.getProjectId()!=null) { 
    			 projectId = (Long)select.getItemProperty("projectId").getValue();	
    			 System.out.println("project id----------------->>>>>>>>>>>>>>>>>"+projectId);
    		}
 			
 			populateAndConfigureBillingTable(projectId);
    		 populateAndConfigurePhaseTable(projectId);
    	 }
     }
     private void editProjectBillingDetails() {  
    	 Item selectBill=tblBill.getItem(tblBill.getValue());
    	 if (selectBill != null)
    	 {
    		 ProjectBillDM editProjectBilllist = beanProjBill.getItem(tblBill.getValue()).getBean();
    		 if(editProjectBilllist.getBillingDesc()!=null) {
    		 tfBillDesc.setValue(selectBill.getItemProperty("billingDesc").getValue().toString());	
    		 }
    		
    		if(editProjectBilllist.getBillAmount()!=null) {
    			tfBillAmount.setValue(selectBill.getItemProperty("billAmount").getValue().toString());	
    		}
    		if(editProjectBilllist.getBillRemarks()!=null) {
    			taBillRemarks.setValue(selectBill.getItemProperty("billRemarks").getValue().toString());	
    		}
    		if(editProjectBilllist.getDueDate()!=null) {
    			dfdueDate.setValue((Date)selectBill.getItemProperty("dueDate").getValue());	
    		}
    		if(editProjectBilllist.getInvoiceNumber()!=null) {
    			tfInvoiceNo.setValue(selectBill.getItemProperty("invoiceNumber").getValue().toString());	
    		}
    		if(editProjectBilllist.getInvoiveDate()!=null) {
    			dfInvoiceDt.setValue((Date)selectBill.getItemProperty("invoiveDate").getValue());	
    		}
    		if(editProjectBilllist.getPaidAmount()!=null) {
    			tfPaidAmount.setValue(selectBill.getItemProperty("paidAmount").getValue().toString());	
    		}
    		if(editProjectBilllist.getPaidDate()!=null) {
    			dfPaidDate.setValue((Date)selectBill.getItemProperty("paidDate").getValue());	
    		}
    		if(editProjectBilllist.getPaymentRemarks()!=null) {
    			taPaymentRemarks.setValue(selectBill.getItemProperty("paymentRemarks").getValue().toString());	
    		}
    	 String gtCode = selectBill.getItemProperty("billStatus").getValue().toString();
//    	 cbBillStatus.setValue(Common.getStatus(gtCode)); 
    	 
// 			EmployeeDM editGetRecd = editProjectBilllist.getReceivedBy();
 			Collection<?> collrecv=cbReceivedBy.getItemIds();
 			for(Iterator<?> iterator=collrecv.iterator(); iterator.hasNext();) {
 				Object itemid=(Object)iterator.next();
 				BeanItem<?> item=(BeanItem<?>) cbReceivedBy.getItem(itemid);
 				EmployeeDM editRecvBean=(EmployeeDM) item.getBean();
// 				if (editGetRecd != null && editGetRecd.getEmployeeid().equals(editRecvBean.getEmployeeid())){
 					cbReceivedBy.setValue(itemid);
//					break;
// 				} else {
// 					cbReceivedBy.setValue(null);
// 				}
 			}	
 
    	 }
     }
     private void editProjectPhaseDetails() {  
    	 Item selectPhase=tblPhase.getItem(tblPhase.getValue());
    	 if (selectPhase != null)
    	 {
    		 ProjectPhaseDM editProjectPhaselist = beanPhase.getItem(tblPhase.getValue()).getBean();
            if(editProjectPhaselist.getBaseLined().equals("Y")) {
            	
            	if(editProjectPhaselist.getProjectPhase()!=null) {
            	tfProjPhase.setReadOnly(false);
           		tfProjPhase.setValue(selectPhase.getItemProperty("projectPhase").getValue().toString());
           		tfProjPhase.setReadOnly(true);
           		 }
           		if(editProjectPhaselist.getPlanStartDate()!=null) {
           			dfPlanStartDate.setReadOnly(false);
           			dfPlanStartDate.setValue((Date)selectPhase.getItemProperty("planStartDate").getValue());
           			dfPlanStartDate.setReadOnly(true);
           		}
           		if(editProjectPhaselist.getPlanEndDate()!=null) {
           			dfPlanEndDt.setReadOnly(false);
           			dfPlanEndDt.setValue((Date)selectPhase.getItemProperty("planEndDate").getValue());
           			dfPlanEndDt.setReadOnly(true);
           		}
           		if(editProjectPhaselist.getActStartDate()!=null) {
           			dfActStartDate.setReadOnly(false);
           			dfActStartDate.setValue((Date)selectPhase.getItemProperty("actStartDate").getValue());
    //       			dfActStartDate.setprivate static final long serialVersionUID = 1L;
//        			public void blur(BlurEvent event) {
//    				// TODO Auto-generated method stub
//    			//	tfProjectName.setComponentError(null);
//    				 String charseq=tfProjectName.getValue().toString();
//    				 if(charseq.matches("^[a-zA-Z ]+")) {
//    					 tfProjectName.setComponentError(null); 
//    				 } else {
//    					 tfProjectName.setComponentError(new UserError("Projects name should be characters"));
//    				 }
//    			}
//    	 });	ReadOnly(true);
           		}
           		if(editProjectPhaselist.getActEndDate()!=null) {
           			dfActEndDate.setReadOnly(false);
           			dfActEndDate.setValue((Date)selectPhase.getItemProperty("actEndDate").getValue());
           			dfActEndDate.setReadOnly(true);
           		}
           		if(editProjectPhaselist.getActEffort()!=null) {
           			tfActEffort.setReadOnly(false);
              		 tfActEffort.setValue(selectPhase.getItemProperty("actEffort").getValue().toString());
              		tfActEffort.setReadOnly(true);
              		 }
           		if(editProjectPhaselist.getPlanEffort()!=null) {
           			tfPlanEffort.setReadOnly(false);
              		tfPlanEffort.setValue(selectPhase.getItemProperty("planEffort").getValue().toString());
              		tfPlanEffort.setReadOnly(true);
              		 }
           		if(editProjectPhaselist.getRemarks()!=null) {
           			taPhaseRemarks.setReadOnly(false);
                 	taPhaseRemarks.setValue(selectPhase.getItemProperty("remarks").getValue().toString());	
                 	taPhaseRemarks.setReadOnly(true);
                 		 }
           		if(editProjectPhaselist.getVersionNo()!=null) {
           			     tfVersionNo.setReadOnly(false);
                 		 tfVersionNo.setValue(selectPhase.getItemProperty("versionNo").getValue().toString());	
                 		 tfVersionNo.setReadOnly(true); 		
           		}
           		
              	String stringBase=selectPhase.getItemProperty("baseLined").getValue().toString();
       		if(stringBase.equals("Y"))
       		{
       			chBaseLined.setReadOnly(false);
       			chBaseLined.setValue(true);	
       			chBaseLined.setReadOnly(true);
       		}
       		else {
       			chBaseLined.setReadOnly(false);
       			chBaseLined.setValue(false);
       			chBaseLined.setReadOnly(true);
       		}
       		String gtCodes = selectPhase.getItemProperty("phaseStatus").getValue().toString();
       		     cbPhaseStatus.setReadOnly(false);
//             	 cbPhaseStatus.setValue(Common.getStatus(gtCodes)); 
             	 cbPhaseStatus.setReadOnly(true);
    		 }
            
            else {
            	
            
    		 if(editProjectPhaselist.getProjectPhase()!=null) {
    		 tfProjPhase.setValue(selectPhase.getItemProperty("projectPhase").getValue().toString());	
    		 }
    		if(editProjectPhaselist.getPlanStartDate()!=null) {
    			dfPlanStartDate.setValue((Date)selectPhase.getItemProperty("planStartDate").getValue());	
    		}
    		if(editProjectPhaselist.getPlanEndDate()!=null) {
    			dfPlanEndDt.setValue((Date)selectPhase.getItemProperty("planEndDate").getValue());	
    		}
    		if(editProjectPhaselist.getActStartDate()!=null) {
    			dfActStartDate.setValue((Date)selectPhase.getItemProperty("actStartDate").getValue());	
    		}
    		if(editProjectPhaselist.getActEndDate()!=null) {
    			dfActEndDate.setValue((Date)selectPhase.getItemProperty("actEndDate").getValue());	
    		}
    		if(editProjectPhaselist.getActEffort()!=null) {
       		 tfActEffort.setValue(selectPhase.getItemProperty("actEffort").getValue().toString());	
       		 }
    		if(editProjectPhaselist.getPlanEffort()!=null) {
       		 tfPlanEffort.setValue(selectPhase.getItemProperty("planEffort").getValue().toString());	
       		 }
    		if(editProjectPhaselist.getRemarks()!=null) {
          		 taPhaseRemarks.setValue(selectPhase.getItemProperty("remarks").getValue().toString());	
          		 }
    		if(editProjectPhaselist.getVersionNo()!=null) {
          		 tfVersionNo.setValue(selectPhase.getItemProperty("versionNo").getValue().toString());	
          		 }
    		
       	String stringBase=selectPhase.getItemProperty("baseLined").getValue().toString();
		if(stringBase.equals("Y"))
		{
			chBaseLined.setValue(true);		
		}
		else {
			chBaseLined.setValue(false);
		}
		String gtCodes = selectPhase.getItemProperty("phaseStatus").getValue().toString();
//      	 cbPhaseStatus.setValue(Common.getStatus(gtCodes)); 
            }
            
      	  projectPhaseid = (Long)selectPhase.getItemProperty("projectPhaseId").getValue();
      	 System.out.println("project phase Id------------>>>>>>>"+projectPhaseid);
		populateAndConfigureTeamTable(projectPhaseid);
    	 
    	 }
     }
     private void editProjectTeamDetails() {  
    	 Item selectTeam=tblTeam.getItem(tblTeam.getValue());
    	 if (selectTeam != null)
    	 {
    		 ProjectTeamDM editProjectTeamlist = beanProjectTeam.getItem(tblTeam.getValue()).getBean();
    		 if(editProjectTeamlist.getRoleDesc()!=null) {
    		 tfRoleDesc.setValue(selectTeam.getItemProperty("roleDesc").getValue().toString());	
    		 }
    		if(editProjectTeamlist.getBillRate()!=null) {
    			tfBillRate.setValue(selectTeam.getItemProperty("billRate").getValue().toString());	
    		}
    		if(editProjectTeamlist.getLoadingPrcnt()!=null) {
    			tfLoading.setValue(selectTeam.getItemProperty("loadingPrcnt").getValue().toString());	
    		}
    		if(editProjectTeamlist.getPlanEffort()!=null) {
    			tfTeamPlanEffort.setValue(selectTeam.getItemProperty("planEffort").getValue().toString());	
    		}
    		if(editProjectTeamlist.getRemarks()!=null) {
    			taTeamRemarks.setValue(selectTeam.getItemProperty("remarks").getValue().toString());	
    		}
//    		EmployeeDM editEmployee = editProjectTeamlist.getEmployeeId();
 			Collection<?> collEmp=cbTeamEmployee.getItemIds();
 			for(Iterator<?> iterator=collEmp.iterator(); iterator.hasNext();) {
 				Object itemid=(Object)iterator.next();
 				BeanItem<?> item=(BeanItem<?>) cbTeamEmployee.getItem(itemid);
 				EmployeeDM editEmpBean=(EmployeeDM) item.getBean();
// 				if (editEmployee != null && editEmployee.getEmployeeid().equals(editEmpBean.getEmployeeid())){
 					cbTeamEmployee.setValue(itemid);
//					break;
// 				} else {
// 					cbTeamEmployee.setValue(null);
// 				}
 			}	
    	 }
     }
     private void setTableProperties() {
    	 beanProject = new BeanItemContainer<ProjectDM>(ProjectDM.class);

 	}
     /*
 	 * this function handles load client list to client name component.
 	 */
     private void loadClientList() {
    	 try {
//    	 List <ClientDM> getClientlist=servClientBean.getClientDetails(comapnyId, null, null, null, null, null, null, Common.ACTIVE_DESC);
    			// getClientDetails(companyId, null, null, null, null, null, null,null, Common.ACTIVE_DESC);
    	  beanClient=new BeanItemContainer<ClientDM>(ClientDM.class);
//    	  beanClient.addAll(getClientlist);
    	 cbClientName.setContainerDataSource(beanClient);
    	 } catch(Exception e) {
    		 e.printStackTrace();
    		logger.warn("Loading null values in loadClientList() functions"+e); 
    	 }
     }
     /*
 	 * 
 	 * this function handles load Employee list to Employee name component.
 	 * 
 	 */
     private void loadEmployeeList() {
    	 try {
//    	 List <EmployeeDM> getEmplist=servicebeanEmployee.getEmployeeList(null,null,null,"Active", companyId,null,null,null);
    	 beanEmployee =new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
//    	 beanEmployee.addAll(getEmplist);
    	 cbEmployeName.setContainerDataSource(beanEmployee);
    	 } catch(Exception e) {
    		 logger.warn("Loading null values in loadEmployeeList() functions------>>>>>"+e);
    	 }
     }
     private void loadBroughtByList() {
    	 try {
//    	 List <EmployeeDM> getBroughtlist=servicebeanEmployee.getEmployeeList(null,null,null,"Active", companyId,null,null,null);
    	 beanEmployee =new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
//    	 beanEmployee.addAll(getBroughtlist);
    	 cbBroughtby.setContainerDataSource(beanEmployee);
    	 } catch(Exception e) {
    		 logger.warn("Loading null values in loadBroughtByList() functions------>>>>>"+e);
    	 }
     }
     /*
  	 * 
  	 * this function handles load Employee list to Received By component.
  	 * 
  	 */
     private void loadReceivedbyList() {
    	 try {
//    	 List <EmployeeDM> getRecdbylist=servicebeanEmployee.getEmployeeList(null,null,null,"Active", companyId,null,null,null);
    	 beanEmployee =new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
//    	 beanEmployee.addAll(getRecdbylist);
    	 cbReceivedBy.setContainerDataSource(beanEmployee);
    	 } catch(Exception e) {
    		 logger.warn("Loading null values in loadReceivedbyList() functions------>>>>>"+e);
    	 }
     }
     /*
   	 * 
   	 * this function handles load Employee list to  Employee component.
   	 * 
   	 */
      private void loadTeamEmployeeList() {
     	 try {
//     	 List <EmployeeDM> getTeamEmplist=servicebeanEmployee.getEmployeeList(null,null,null,"Active", companyId,null,null,null);
     	 beanEmployee =new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
//     	 beanEmployee.addAll(getTeamEmplist);
     	 cbTeamEmployee.setContainerDataSource(beanEmployee);
     	 } catch(Exception e) {
     		 logger.warn("Loading null values in loadTeamEmployeeList() functions------>>>>>"+e);
     	 }
      }
     /*
 	 * 
 	 * this function handles load currency list to currency name component.
 	 * 
 	 */
     private void loadCurrencyList() {
    	 try {
//    	 List <CurrencyDM> getCurrencylist=servicebeanCurrency.getCurrencyList(null,null,null,"Active");
    	  beanCurrency=new BeanItemContainer<CurrencyDM>(CurrencyDM.class);
//    	  beanCurrency.addAll(getCurrencylist);
    	 cbCurrencyName.setContainerDataSource(beanCurrency);
    	 } catch(Exception e) {
    		logger.info("Loading null values in loadCurrencyList() function------>>>>"+e); 
    	 }
     }
     /*
 	 * saveProjectsDetails()-->this function is used for save/update the records
 	 */	
 private  void saveProjectsDetails() {
	 btnSave.setComponentError(null);
    	 if(tblProject.getValue()!=null)
    	 {
    		 ProjectDM updateProjects=beanProject.getItem(tblProject.getValue()).getBean();
    		 updateProjects.setProjectId(projectId);
//    		 if(tfProjectName!=null)
//    		 {
    		 updateProjects.setProjectName(tfProjectName.getValue().toString());
//    		 }
//    		 else
//    		 {
//    			 tfProjectName.setComponentError(new UserError("Projects name should be characters"));
//
//    		 }
//    		 
    		 try {
    		 updateProjects.setProjectType(cbProjectType.getValue().toString());
    		 } catch(Exception e) {logger.info("update project type------>>>>>>"+e);}
    		 try {
    		 updateProjects.setProjectSubType(cbProjectSubType.getValue().toString());
    		 } catch(Exception e) {logger.info("update Project SubType----->>>>>>"+e);}
    		 updateProjects.setProjectCost(new Long(tfProjectCost.getValue().toString()));
    		 updateProjects.setProjectEffort(new Long(tfProjectEffort.getValue().toString()));
//    		 updateProjects.setBroughtBy(selectedBroughtby);
    		 StatusDM sts = (StatusDM) cbStatus.getValue();
    		 if(chTrack.getValue()!=null) {
    			 if(chTrack.getValue().equals(true)) {
    				 updateProjects.setTrackKPA("Y");
    			 }else {
    				 updateProjects.setTrackKPA("N");
    			 } }
    		 else {
    			 updateProjects.setTrackKPA("N");
    		 }
//    		 updateProjects.setCurrencyId(selectedCurrency);
    		 updateProjects.setProjectStatus(sts.getCode());
    		 updateProjects.setBroughtDate(dfBroughtDt.getValue());
    		 updateProjects.setStartDate(dfStartDt.getValue());
    		 updateProjects.setEndDate(dfEndDt.getValue());
//    		 updateProjects.setEmployeeId(selectedEmployee);
//    		 updateProjects.setClientId(selectedClient);
    		 updateProjects.setCompanyid(companyId);
    		 updateProjects.setLastUpdatedBy(userName);
//    		 updateProjects.setLastUpdtDate(DateUtils.getcurrentdate());
    	
   
    		 if(tfProjectName.isValid() && cbClientName.isValid() && cbEmployeName.isValid() && dfStartDt.isValid() && dfEndDt.isValid()){
    			 servProjectsBean.saveOrUpdateProject(updateProjects);
    			 
    			 projectId = updateProjects.getProjectId();
        		 System.out.println("Updated Project Id--------------------->>>>>>>>>>"+projectId);
        		 
    			 populateAndConfigureTable(false);
    			 btnEdit.setEnabled(false);
    			 btnSave.setCaption("Save");
    			 lblNotification.setValue("Successfully Updated");
    			 lblNotificationIcon.setIcon(new ThemeResource("img/success_small.png"));
    		 }
    		 else {
    			 btnSave.setComponentError(new UserError("Form is not valid"));
    			 lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
    			 lblNotification.setValue("Update failed, please check the data and try again ");
    		 }		 
    }
    	 else{
    		 
    		 ProjectDM saveProjects=new ProjectDM();
    		 saveProjects.setProjectId(projectId);
    		 saveProjects.setProjectName(tfProjectName.getValue().toString());
    		 try {
    		 saveProjects.setProjectType(cbProjectType.getValue().toString());
    		 } catch(Exception e) {logger.info("select Project Type-------->>>>>>>>"+e);}
    		 try {
    		 saveProjects.setProjectSubType(cbProjectSubType.getValue().toString());
    		 } catch(Exception e) {logger.info("selecct Project SubType------->>>>>"+e);}
    		 saveProjects.setProjectCost(new Long(tfProjectCost.getValue().toString()));
    		 saveProjects.setProjectEffort(new Long(tfProjectEffort.getValue().toString()));
//    		 saveProjects.setBroughtBy(selectedBroughtby);
    		 StatusDM sts = (StatusDM) cbStatus.getValue();
    		 saveProjects.setProjectStatus(sts.getCode());
    		 if(chTrack.getValue()!=null) {
    			 if(chTrack.getValue().equals(true)) {
    				 saveProjects.setTrackKPA("Y");
    			 }else {
    				 saveProjects.setTrackKPA("N");
    			 } }
    		 else {
    			 saveProjects.setTrackKPA("N");
    		 }
//    		 saveProjects.setCurrencyId(selectedCurrency);
    		 saveProjects.setBroughtDate(dfBroughtDt.getValue());
    		 saveProjects.setStartDate(dfStartDt.getValue());
    		 saveProjects.setEndDate(dfEndDt.getValue());
//    		 saveProjects.setEmployeeId(selectedEmployee);
//    		 saveProjects.setClientId(selectedClient);
    		 saveProjects.setCompanyid(companyId);
    		 saveProjects.setLastUpdatedBy(userName);
//    		 saveProjects.setLastUpdtDate(DateUtils.getcurrentdate());
    		 
    		 if(tfProjectName.isValid()  && cbClientName.isValid() && cbEmployeName.isValid() && dfStartDt.isValid() && dfEndDt.isValid()) {
    			 servProjectsBean.saveOrUpdateProject(saveProjects);
    			 
    			 projectId = saveProjects.getProjectId();
        		 System.out.println("Save Project Id--------------------->>>>>>>>>>"+projectId);
    			 populateAndConfigureTable(false);
    			 btnSave.setCaption("Save");
    			 lblNotification.setValue("Successfully Saved");
    			 lblNotificationIcon.setIcon(new ThemeResource("img/success_small.png"));
    		 }
    		 else {
    			 btnSave.setComponentError(new UserError("Form is not valid"));
    			 lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
    			 lblNotification.setValue("Save failed, please check the data and try again ");
    		 }
    	 }
 }

private void loadTempBillDetails()
{
		btnAddBill.setComponentError(null);
		billId = servProjbillBean.getNextSequence();
   	 if(tblBill.getValue()!=null)
   	 {
   		ProjectBillDM Billupdate=beanProjBill.getItem(tblBill.getValue()).getBean();
   		Billupdate.setProjectBillId(Billupdate.getProjectBillId());
   		Billupdate.setBillingDesc(tfBillDesc.getValue().toString());
//   		Billupdate.setBillAmount(new Long(tfBillAmount.getValue().toString()));
   		Billupdate.setBillRemarks(taBillRemarks.getValue().toString());
   		Billupdate.setDueDate(dfdueDate.getValue());
   		Billupdate.setInvoiceNumber(new Long(tfInvoiceNo.getValue().toString()));
   		StatusDM stsbill = (StatusDM) cbBillStatus.getValue();
   		Billupdate.setBillStatus(stsbill.getCode());
   		Billupdate.setInvoiveDate(dfInvoiceDt.getValue());
//   		Billupdate.setPaidAmount(new Long(tfPaidAmount.getValue().toString()));
   		Billupdate.setPaidDate(dfPaidDate.getValue());
   		Billupdate.setPaymentRemarks(taPaymentRemarks.getValue().toString());
//   		Billupdate.setReceivedBy(selectedEmployee);
   		Billupdate.setLastUpdatedBy(userName);
//   		Billupdate.setLastUpdtDate(DateUtils.getcurrentdate());
   		if(cbReceivedBy.isValid() && dfdueDate.isValid() && dfPaidDate.isValid() && tfBillDesc.isValid()) {
   		tempSaveBill.add(Billupdate);
		populateAndConfigureBillingTable(null);
		resetFieldsBilling();
   		}
   		else {
   			btnAddBill.setComponentError(new UserError("Form is not valid"));
   		}
	}
	else {
	ProjectBillDM billSave=new ProjectBillDM();
	billSave.setProjectBillId(billId);
	billSave.setBillingDesc(tfBillDesc.getValue().toString());
//	billSave.setBillAmount(new Long(tfBillAmount.getValue().toString()));
	billSave.setBillRemarks(taBillRemarks.getValue().toString());
	billSave.setDueDate(dfdueDate.getValue());
	billSave.setInvoiceNumber(new Long(tfInvoiceNo.getValue().toString()));
	 StatusDM stsbill = (StatusDM) cbBillStatus.getValue();
	 billSave.setBillStatus(stsbill.getCode());
	 billSave.setInvoiveDate(dfInvoiceDt.getValue());
//	 billSave.setPaidAmount(new Long(tfPaidAmount.getValue().toString()));
	 billSave.setPaidDate(dfPaidDate.getValue());
	 billSave.setPaymentRemarks(taPaymentRemarks.getValue().toString());
//	 billSave.setReceivedBy(selectedEmployee);
	 billSave.setLastUpdatedBy(userName);
//	 billSave.setLastUpdtDate(DateUtils.getcurrentdate());
	 if(cbReceivedBy.isValid() && dfdueDate.isValid() && dfPaidDate.isValid() && tfBillDesc.isValid()) {
		tempSaveBill.add(billSave);
		populateAndConfigureBillingTable(null);
		resetFieldsBilling();
	 }
	 else {
	 btnAddBill.setComponentError(new UserError("Form is not valid"));
	 }
	}
   	 
}


 @SuppressWarnings("unchecked")
private void saveTempProjectBillDetails()
 {
	try {
	
		Collection<ProjectBillDM> billList = (Collection<ProjectBillDM>) tblBill.getVisibleItemIds();
		 
	 for(ProjectBillDM savedtl : (Collection<ProjectBillDM>)billList) {
		 
		 ProjectBillDM billDetail = new ProjectBillDM();
		 
		 billDetail.setProjectId(projectId);
		 billDetail.setProjectBillId(savedtl.getProjectBillId());
		 billDetail.setBillingDesc(savedtl.getBillingDesc());
//		 billDetail.setBillAmount(new Long(savedtl.getBillAmount()));
		 billDetail.setBillRemarks(savedtl.getBillRemarks());
		 billDetail.setDueDate(savedtl.getDueDate());
		 billDetail.setInvoiceNumber(new Long(savedtl.getInvoiceNumber()));
		 billDetail.setBillStatus(savedtl.getBillStatus());
		 billDetail.setInvoiveDate(savedtl.getInvoiveDate());
//		 billDetail.setPaidAmount(new Long(savedtl.getPaidAmount()));
		 billDetail.setPaidDate(savedtl.getPaidDate());
		 billDetail.setPaymentRemarks(savedtl.getPaymentRemarks());
		 billDetail.setReceivedBy(savedtl.getReceivedBy());
		 billDetail.setLastUpdatedBy(savedtl.getLastUpdatedBy());
		 billDetail.setLastUpdtDate(savedtl.getLastUpdtDate());
     if(projectId!=null) {
		 servProjbillBean.saveOrUpdateProjectBill(billDetail);
			 populateAndConfigureBillingTable(projectId);
			 resetFieldsBilling();
			 btnAddBill.setCaption("Add");
      }
		
	 }
	} catch(Exception e) {
		e.printStackTrace();
	}
 }
 
 /*
	 * saveProjectPhaseDetails()-->this function is used for save/update the records
	 */	
 private void loadTempProjectPhase()
 {
		 btnAddPhase.setComponentError(null);
		 phaseId = servProjPhaseBean.getNextSequenceforPhase();
    	 if(tblPhase.getValue()!=null)
    	 {
    		 ProjectPhaseDM phaseUpdate = beanPhase.getItem(tblPhase.getValue()).getBean();
    		 phaseUpdate.setProjectPhaseId(phaseUpdate.getProjectPhaseId());
    		 phaseUpdate.setProjectPhase(tfProjPhase.getValue().toString());
    		 phaseUpdate.setPlanStartDate(dfPlanStartDate.getValue());
    		 phaseUpdate.setPlanEndDate(dfPlanEndDt.getValue());
    		 phaseUpdate.setActStartDate(dfActStartDate.getValue());
    		 phaseUpdate.setActEndDate(dfActEndDate.getValue());
    		 phaseUpdate.setPlanEffort(new Long(tfPlanEffort.getValue().toString()));
    		 phaseUpdate.setActEffort(new Long(tfActEffort.getValue().toString()));
    		 StatusDM stsphase = (StatusDM) cbPhaseStatus.getValue();
    		 phaseUpdate.setPhaseStatus(stsphase.getCode());
    		 phaseUpdate.setVersionNo(new Long(tfVersionNo.getValue().toString()));
    		 phaseUpdate.setRemarks(taPhaseRemarks.getValue().toString());
    		 if(chBaseLined.getValue()!=null) {
    			 if(chBaseLined.getValue().equals(true)) {
    				 phaseUpdate.setBaseLined("Y");
    			 }else {
    				 phaseUpdate.setBaseLined("N");
    			 } }
    		 else {
    			 phaseUpdate.setBaseLined("N");
    		 }
    		 phaseUpdate.setLastUpdatedBy(userName);
//    		 phaseUpdate.setLastUpdtDate(DateUtils.getcurrentdate());
    		 if(tfProjPhase.isValid() && dfPlanStartDate.isValid() && dfPlanEndDt.isValid()) {
    		 tempSavePhase.add(phaseUpdate);
    		 resetFieldsPhase();
    		 populateAndConfigurePhaseTable(null);
    		 btnAddPhase.setEnabled(true);
    		 btnEditPhase.setEnabled(false);
    		 btnPhaseRevn.setEnabled(false);
    		 phaseId = phaseUpdate.getProjectPhaseId();
    		 populateAndConfigureTeamTable(phaseId);
    		 tabsheet.setSelectedTab(vlProjectPhase);
    			tabsheet.getTab(vlProjectTeam).setEnabled(true);
    			resetFieldsTeam();
    		 } else {
    			 btnAddBill.setComponentError(new UserError("Form is not valid"));
    		 }
    	 
    	 }
	 else 
	 {		 
		 ProjectPhaseDM saveProjectPhase = new ProjectPhaseDM();
	 saveProjectPhase.setProjectPhaseId(phaseId);
	 saveProjectPhase.setProjectPhase(tfProjPhase.getValue().toString());
	 saveProjectPhase.setPlanStartDate(dfPlanStartDate.getValue());
	 saveProjectPhase.setPlanEndDate(dfPlanEndDt.getValue());
	 saveProjectPhase.setActStartDate(dfActStartDate.getValue());
	 saveProjectPhase.setActEndDate(dfActEndDate.getValue());
	 saveProjectPhase.setPlanEffort(new Long(tfPlanEffort.getValue().toString()));
	 saveProjectPhase.setActEffort(new Long(tfActEffort.getValue().toString()));
	 StatusDM stsphase = (StatusDM) cbPhaseStatus.getValue();
	 saveProjectPhase.setPhaseStatus(stsphase.getCode());
	 saveProjectPhase.setVersionNo(new Long(tfVersionNo.getValue().toString()));
	 saveProjectPhase.setRemarks(taPhaseRemarks.getValue().toString());
	 if(chBaseLined.getValue()!=null) {
		 if(chBaseLined.getValue().equals(true)) {
			 saveProjectPhase.setBaseLined("Y");
		 }else {
			 saveProjectPhase.setBaseLined("N");
		 } }
	 else {
		 saveProjectPhase.setBaseLined("N");
	 }
	 saveProjectPhase.setLastUpdatedBy(userName);
//	 saveProjectPhase.setLastUpdtDate(DateUtils.getcurrentdate());
	 if(tfProjPhase.isValid() && dfPlanStartDate.isValid() && dfPlanEndDt.isValid()) {
	 tempSavePhase.add(saveProjectPhase);
	 resetFieldsPhase();
	 populateAndConfigurePhaseTable(null);
	 btnAddPhase.setEnabled(true);
	 btnEditPhase.setEnabled(false);
	 btnPhaseRevn.setEnabled(false);
	 phaseId = saveProjectPhase.getProjectPhaseId();
	 populateAndConfigureTeamTable(phaseId);
	 tabsheet.setSelectedTab(vlProjectPhase);
	 tabsheet.getTab(vlProjectTeam).setEnabled(true);
	 resetFieldsTeam();
	 
	 } else {
		 btnAddBill.setComponentError(new UserError("Form is not valid"));
	 }
	 
	 }
 }
 @SuppressWarnings("unchecked")
private  void saveProjectPhaseDetails() {
	 Collection<ProjectPhaseDM> billList = (Collection<ProjectPhaseDM>) tblPhase.getVisibleItemIds();
	 
	 for(ProjectPhaseDM savedtl : (Collection<ProjectPhaseDM>)billList) {
		 ProjectPhaseDM saveProjectPhase = new ProjectPhaseDM();
    		 saveProjectPhase.setProjectId(projectId);
    		 saveProjectPhase.setProjectPhaseId(savedtl.getProjectPhaseId());
    		 saveProjectPhase.setProjectPhase(savedtl.getProjectPhase());
    		 saveProjectPhase.setPlanStartDate(savedtl.getPlanStartDate());
    		 saveProjectPhase.setPlanEndDate(savedtl.getPlanEndDate());
    		 saveProjectPhase.setActStartDate(savedtl.getActStartDate());
    		 saveProjectPhase.setActEndDate(savedtl.getActEndDate());
    		 saveProjectPhase.setPlanEffort(savedtl.getPlanEffort());
    		 saveProjectPhase.setActEffort(savedtl.getActEffort());
    		 saveProjectPhase.setPhaseStatus(savedtl.getPhaseStatus());
    		 saveProjectPhase.setVersionNo(savedtl.getVersionNo());
    		 saveProjectPhase.setRemarks(savedtl.getRemarks());
    		 saveProjectPhase.setBaseLined(savedtl.getBaseLined());
    		 saveProjectPhase.setLastUpdatedBy(savedtl.getLastUpdatedBy());
    		 saveProjectPhase.setLastUpdtDate(savedtl.getLastUpdtDate());
    		 
    		 if(projectId!=null) {
    			 servProjPhaseBean.saveOrUpdateProjectPhase(saveProjectPhase);
    			 
    			 projectPhaseid = saveProjectPhase.getProjectPhaseId();
    			 System.out.println("Save Project Id of Phase Screen------>>>>>>>"+projectId+"  Save Project Phase Id------->>>>>>"+projectPhaseid);
    			 populateAndConfigurePhaseTable(projectId);
    	//		 populateAndConfigureTeamTable(projectPhaseid);
    			 resetFieldsPhase();
    			 btnAddPhase.setCaption("Add");
    			
    		 }
	 }
    		 
    	 
     }
 private void loadTempPhaseRevision()  
 {
	 try {
	 btnAddRevision.setComponentError(null);
	 revisionId = servProjPhaseRevnBean.generateNextSequence();
	 
	 Item selectPhase=tblPhase.getItem(tblPhase.getValue());
	 if (selectPhase != null)
	 {
		 ProjectPhaseDM editProjectPhaselist = beanPhase.getItem(tblPhase.getValue()).getBean();
		 phaseid = editProjectPhaselist.getProjectPhaseId();
	 System.out.println("Phase Id for Revision--------->>>>>>>>>>>>>>>"+phaseid);
	 
	 ProjectPhaseRevnDM saveRevision = new ProjectPhaseRevnDM();
	 saveRevision.setProjectRevisionId(revisionId);
	 saveRevision.setProjectPhaseId(phaseid);
	 saveRevision.setNewstartDate(dfRevnStartdt.getValue());
	 saveRevision.setNewendDate(dfRevnEnddt.getValue());
	 saveRevision.setNewEffort(new Long(tfRevnEffort.getValue().toString()));
	 saveRevision.setVersionNo(new Long(tfRevnVersion.getValue().toString()));
	 saveRevision.setPenaltyAmount(new Long(tfRevnPenalty.getValue().toString()));
	 saveRevision.setClientRemarks(taClientRemarks.getValue().toString());
	 saveRevision.setJustification(taJustification.getValue().toString());
	
	 if(chClientAgreed.getValue()!=null) {
		 if(chClientAgreed.getValue().equals(true)) {
			 saveRevision.setClientAgreed("Y");
		 }else {
			 saveRevision.setClientAgreed("N");
		 } }
	 else {
		 saveRevision.setClientAgreed("N");
	 }
	 saveRevision.setLastUpdatedBy(userName);
//	 saveRevision.setLastUpdtDate(DateUtils.getcurrentdate());
	 if(phaseid!=null && dfRevnStartdt.isValid() && dfRevnEnddt.isValid()) {
		 tempSaveRevn.add(saveRevision);
		 populateAndConfigurePhaseRevisionTable(null);
		 resetFieldsRevision();
		 
	 } else {
		 btnAddRevision.setComponentError(new UserError("Form is not valid"));
	 }
	 }
	 } catch(Exception e) {
		 e.printStackTrace();
	 }
	 
 }
 @SuppressWarnings("unchecked")
 private  void saveProjectPhaseRevision() {
  
 Collection<ProjectPhaseRevnDM> RevisionList = (Collection<ProjectPhaseRevnDM>) tblPhaseRevn.getVisibleItemIds();
 	 
 	 for(ProjectPhaseRevnDM savedtl : (Collection<ProjectPhaseRevnDM>)RevisionList) {
     		 
 		ProjectPhaseRevnDM savePhaseRevision = new ProjectPhaseRevnDM();
 		savePhaseRevision.setProjectRevisionId(savedtl.getProjectRevisionId());
 		savePhaseRevision.setProjectPhaseId(savedtl.getProjectPhaseId());
 		savePhaseRevision.setNewstartDate(savedtl.getNewstartDate());
 		savePhaseRevision.setNewendDate(savedtl.getNewendDate());
 		savePhaseRevision.setNewEffort(savedtl.getNewEffort());
 		savePhaseRevision.setVersionNo(savedtl.getVersionNo());
 		savePhaseRevision.setPenaltyAmount(savedtl.getPenaltyAmount());
 		savePhaseRevision.setClientRemarks(savedtl.getClientRemarks());
 		savePhaseRevision.setJustification(savedtl.getJustification());
 		savePhaseRevision.setLastUpdatedBy(savedtl.getLastUpdatedBy());
 		savePhaseRevision.setLastUpdtDate(savedtl.getLastUpdtDate());
 		
 		servProjPhaseRevnBean.saveOrUpdateProjectPhaseRevision(savePhaseRevision);
 		Long phaseids = savedtl.getProjectPhaseId();
 		Date startdt = savedtl.getNewstartDate();
 		Date enddt = savedtl.getNewendDate();
 		servProjPhaseBean.updateProjectPhaseDates(startdt, enddt, phaseids);
 		
 	 }
 } 
 /*
	 * saveProjectTeamDetails()-->this function is used for save/update the records
	 */	
 private void loadTempProjectTeam()
 {
	 btnAddTeam.setComponentError(null);
	 teamId = servProjTeamBean.getNextSequenceforTeam();
	 if(tblTeam.getValue()!=null)
	 {
		 ProjectTeamDM updateTeam = beanProjectTeam.getItem(tblTeam.getValue()).getBean();
		 updateTeam.setProjectTeamId(updateTeam.getProjectTeamId());
		 updateTeam.setProjectPhaseId(updateTeam.getProjectPhaseId());
		 updateTeam.setRoleDesc(tfRoleDesc.getValue().toString());
		 updateTeam.setPlanEffort(new Long(tfTeamPlanEffort.getValue().toString()));
		 updateTeam.setBillRate(new Long(tfBillRate.getValue().toString()));
		 updateTeam.setLoadingPrcnt(new Long(tfLoading.getValue().toString()));
		 updateTeam.setRemarks(taTeamRemarks.getValue().toString());
//		 updateTeam.setEmployeeId(selectedEmployee);
		 updateTeam.setLastUpdatedBy(userName);
//		 updateTeam.setLastUpdtDate(DateUtils.getcurrentdate());

		 if(phaseId!=null && tfRoleDesc.isValid() && cbTeamEmployee.isValid() ){
			 tempSaveTeam.add(updateTeam);
			 System.out.println("update project phase Id of Project Team Screen------>>>>>"+phaseId);
			 populateAndConfigureTeamTable(null);
			 resetFieldsTeam();
			 btnEditTeam.setEnabled(false);
			 btnAddTeam.setCaption("Add");
		 }
		 else {
			 btnAddTeam.setComponentError(new UserError("Form is not valid"));
		 }		 
}
	 else{
		 
		 ProjectTeamDM saveTeam = new ProjectTeamDM();
		 saveTeam.setProjectTeamId(teamId);
		 saveTeam.setProjectPhaseId(phaseId);
		 saveTeam.setRoleDesc(tfRoleDesc.getValue().toString());
		 saveTeam.setPlanEffort(new Long(tfTeamPlanEffort.getValue().toString()));
		 saveTeam.setBillRate(new Long(tfBillRate.getValue().toString()));
		 saveTeam.setLoadingPrcnt(new Long(tfLoading.getValue().toString()));
		 saveTeam.setRemarks(taTeamRemarks.getValue().toString());
//		 saveTeam.setEmployeeId(selectedEmployee);
		 saveTeam.setLastUpdatedBy(userName);
//		 saveTeam.setLastUpdtDate(DateUtils.getcurrentdate());
		 System.out.println("Testing testing tessting team-------->>>>>>>>>>");
		 if(phaseId!=null && tfRoleDesc.isValid() && cbTeamEmployee.isValid()) {
			 System.out.println("Testing testing tesing testing testing 1342--------->>>>>>>>>>>");
			 tempSaveTeam.add(saveTeam);
			 populateAndConfigureTeamTable(null);
			 resetFieldsTeam();
			 btnAddTeam.setCaption("Add");
		 }
		 else {
			 btnAddTeam.setComponentError(new UserError("Form is not valid"));
		 }
	 }
 }
 @SuppressWarnings("unchecked")
private  void saveProjectTeamDetails() {
	 System.out.println("project team save function is working---------->>>>>>>>>>>>");
Collection<ProjectTeamDM> teamList = (Collection<ProjectTeamDM>) tblTeam.getVisibleItemIds();
	 
	 for(ProjectTeamDM savedtl : (Collection<ProjectTeamDM>)teamList) {
    		 
		 ProjectTeamDM saveProjectTeam = new ProjectTeamDM();
    		 saveProjectTeam.setProjectTeamId(savedtl.getProjectTeamId());
    		 saveProjectTeam.setProjectPhaseId(savedtl.getProjectPhaseId());
    		 saveProjectTeam.setRoleDesc(savedtl.getRoleDesc());
    		 saveProjectTeam.setPlanEffort(savedtl.getPlanEffort());
    		 saveProjectTeam.setBillRate(savedtl.getBillRate());
    		 saveProjectTeam.setLoadingPrcnt(savedtl.getLoadingPrcnt());
    		 saveProjectTeam.setRemarks(savedtl.getRemarks());
//    		 saveProjectTeam.setEmployeeId(savedtl.getEmployeeId());
    		 saveProjectTeam.setLastUpdatedBy(savedtl.getLastUpdatedBy());
    		 saveProjectTeam.setLastUpdtDate(savedtl.getLastUpdtDate());
    		
    			 servProjTeamBean.saveOrUpdateProjectTeam(saveProjectTeam);
    			 populateAndConfigureTeamTable(projectPhaseid);
    			 resetFieldsTeam();
    			 btnAddTeam.setCaption("Add");
    		 
	 }
    	 
     }
 /*
	 * 
	 * this function handles component error when Project Name did not enters.
	 * 
	 */
 private void saveComponenterror() {
	 tfProjectName.setComponentError(null);
	 cbClientName.setComponentError(null);
	 cbEmployeName.setComponentError(null);
	 dfStartDt.setComponentError(null);
	 dfEndDt.setComponentError(null);
		if( tfProjectName.getValue()==null || tfProjectName.getValue().toString().trim().length()==0)
		{
			tfProjectName.setComponentError(new UserError("Enter Projects Name "));
		}
		if( cbClientName.getValue()==null || cbClientName.getValue().toString().trim().length()==0)
		{
			cbClientName.setComponentError(new UserError("Select Project Type "));
		}
		if( cbEmployeName.getValue()==null || cbEmployeName.getValue().toString().trim().length()==0)
			{
				cbEmployeName.setComponentError(new UserError("Select Employee Name "));
			}
			if( dfStartDt.getValue()==null || dfStartDt.getValue().toString().trim().length()==0)
			{
				dfStartDt.setComponentError(new UserError("Select Start Date "));
			}		
			if( dfEndDt.getValue()==null || dfEndDt.getValue().toString().trim().length()==0)
			{
				dfEndDt.setComponentError(new UserError("Select End Date "));
			}		
	}
 /*
	 * 
	 * this function handles component error when end date select less than start date.
	 * 
	 */
 private void dateValidate()
	{
		Date startdt=(Date)dfStartDt.getValue();
		Date enddt=(Date)dfEndDt.getValue();
		try{
		if(startdt.before(enddt)|| startdt.equals(enddt))
		{
			dfEndDt.setComponentError(null);
		}
		else{
			dfEndDt.setComponentError(new UserError("End Date  should be greater than Start Date"));
		}
		}catch(NullPointerException e) {
			logger.info("date validator"+e);	
			}
			
		}
 /*
	 * 
	 * this function handles component error when plan end date select less than plan start date.
	 * 
	 */
private void PlanDateValidate()
	{
		Date planStartdt=(Date)dfPlanStartDate.getValue();
		Date planEnddt=(Date)dfPlanEndDt.getValue();
		try{
		if(planStartdt.before(planEnddt)|| planStartdt.equals(planEnddt))
		{
			dfPlanEndDt.setComponentError(null);
		}
		else{
			dfPlanEndDt.setComponentError(new UserError("End Date  should be greater than Start Date"));
		}
		}catch(NullPointerException e) {
			logger.info("Plan Date validator"+e);	
			}
			
		}

/*
 * 
 * this function handles component error when Act end date select less than Act start date.
 * 
 */
private void ActDateValidate()
{
	Date actStartdt=(Date)dfActStartDate.getValue();
	Date actEnddt=(Date)dfActEndDate.getValue();
	try{
	if(actStartdt.before(actEnddt)|| actStartdt.equals(actEnddt))
	{
		dfActEndDate.setComponentError(null);
	}
	else{
		dfActEndDate.setComponentError(new UserError("End Date  should be greater than Start Date"));
	}
	}catch(NullPointerException e) {
		logger.info("Act Date validator"+e);	
		}
		
	}
/*
 * 
 * this function handles Set Component Error when given fields did not enter.
 * 
 */
private void saveComponentErrorProjectBill()
{
	tfBillDesc.setComponentError(null);
	 cbReceivedBy.setComponentError(null);
	 dfdueDate.setComponentError(null);
		if( tfBillDesc.getValue()==null || tfBillDesc.getValue().toString().trim().length()==0)
		{
			tfBillDesc.setComponentError(new UserError("Enter Billing Description "));
		}
		if( cbReceivedBy.getValue()==null || cbReceivedBy.getValue().toString().trim().length()==0)
		{
			cbReceivedBy.setComponentError(new UserError("Select Received By"));
		}		
		if( dfdueDate.getValue()==null || dfdueDate.getValue().toString().trim().length()==0)
		{
			dfdueDate.setComponentError(new UserError("Select Due Date "));
		}	
		if( dfPaidDate.getValue()==null || dfPaidDate.getValue().toString().trim().length()==0)
		{
			dfPaidDate.setComponentError(new UserError("Select Paid Date "));
		}	
}
private void saveComponentErrorProjectPhase()
{
	tfProjPhase.setComponentError(null);
	dfPlanStartDate.setComponentError(null);
	dfPlanEndDt.setComponentError(null);
		if( tfProjPhase.getValue()==null || tfProjPhase.getValue().toString().trim().length()==0)
		{
			tfProjPhase.setComponentError(new UserError("Enter Project Phase "));
		}
		if( dfPlanStartDate.getValue()==null || dfPlanStartDate.getValue().toString().trim().length()==0)
		{
			dfPlanStartDate.setComponentError(new UserError("Select Plan Start Date"));
		}		
		if( dfPlanEndDt.getValue()==null || dfPlanEndDt.getValue().toString().trim().length()==0)
		{
			dfPlanEndDt.setComponentError(new UserError("Select Plan End Date "));
		}			
}
private void saveComponentErrorTeam()
{
	tfRoleDesc.setComponentError(null);
	cbTeamEmployee.setComponentError(null);
		if( tfRoleDesc.getValue()==null || tfRoleDesc.getValue().toString().trim().length()==0)
		{
			tfRoleDesc.setComponentError(new UserError("Enter Role Description "));
		}
		if( cbTeamEmployee.getValue()==null || cbTeamEmployee.getValue().toString().trim().length()==0)
		{
			cbTeamEmployee.setComponentError(new UserError("Select Employee Name"));
		}		
}

private void saveComponentErrorPhaseRevision()
{
	dfRevnStartdt.setComponentError(null);
	dfRevnEnddt.setComponentError(null);
		if( dfRevnStartdt.getValue()==null || dfRevnStartdt.getValue().toString().trim().length()==0)
		{
			dfRevnStartdt.setComponentError(new UserError("Select Start Date "));
		}
		if( dfRevnEnddt.getValue()==null || dfRevnEnddt.getValue().toString().trim().length()==0)
		{
			dfRevnEnddt.setComponentError(new UserError("Select End Date "));
		}		
}

 /*
	 * 
	 * this function handles button click event
	 * 
	 * @param ClickEvent event
	 */
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		tabsheet.getTab(vlProjectPhaseRevision).setEnabled(true);
		if(btnAdd==event.getButton()) {
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			vlTablePanel.setVisible(false);
			hlSaveBtn.setVisible(true);
			tempSaveBill.removeAll(tempSaveBill);
			tempSavePhase.removeAll(tempSavePhase);
			resetFields();
			resetFieldsBilling();
			resetFieldsPhase();
			populateAndConfigureBillingTable(null);
			populateAndConfigurePhaseTable(null);
			projectId = servProjectsBean.getNextSequence();
			tabsheet.setSelectedTab(vlProjectBill);
			tabsheet.getTab(vlProjectPhase).setEnabled(true);
			btnHome.setEnabled(false);
			btnEdit.setEnabled(false);
			btnAudit.setEnabled(false);
		
		}
		else if(btnEdit==event.getButton()) {
			btnSave.setCaption("Update");
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			vlTablePanel.setVisible(false);	
			hlSaveBtn.setVisible(true);
			btnHome.setEnabled(true);
			btnAudit.setEnabled(true);
			btnAdd.setEnabled(false);
			
			lblFormTitle.setValue("&nbsp;&nbsp;<b>"+ screenName+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Home</font> &nbsp;>&nbsp;Modify");
			try {
				editProjectsDetails();
			} catch(Exception e) {
				e.printStackTrace();
				logger.error("Error thorws in editProjectsDetails() function--->" + e);
			}
	
		}
		else if(btnSave==event.getButton()) {
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			vlTablePanel.setVisible(false);
			saveComponenterror();
			try {
				saveProjectsDetails();
				saveTempProjectBillDetails();
				 saveProjectPhaseDetails();
				 saveProjectTeamDetails();
				 saveProjectPhaseRevision();
			} catch(Exception e) {
				e.printStackTrace();
				logger.info("check  saveProjectsDetails() function. Projects datas did not saved properly--->" + e);
			}
		}
		else if(btnCancel==event.getButton()) {
			vlMainPanel.setVisible(false);
			vlSearchPanel.setVisible(true);
			vlTablePanel.setVisible(true);
			populateAndConfigureTable(false);
			 resetFields();
			 resetFieldsBilling();
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			btnHome.setEnabled(false);
		btnSave.setCaption("Save");
		lblFormTitle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Home");
		hlSaveBtn.setVisible(false);
		}
		else if(btnSearch==event.getButton()) {
			populateAndConfigureTable(true);
			 resetFields();
			 lblFormTitle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Home");
		}
		else if(btnReset==event.getButton()) {		
			tfSearchProject.setValue("");
			cbSearchStatus.setValue(null);
			populateAndConfigureTable(false);
			 resetFields();
			 btnAdd.setEnabled(true);
			 btnEdit.setEnabled(false);
			 btnHome.setEnabled(false);
			 btnAudit.setEnabled(true);
		}
		else if(btnHome==event.getButton())
		{
			vlTableForm.removeAllComponents();
			vlTableForm.addComponent(hlAddEditBtn);
			vlTableForm.addComponent(tblProject);
		
			btnAdd.setEnabled(true);
			btnAudit.setEnabled(true);
			btnEdit.setEnabled(false);
			btnHome.setEnabled(false);
			lblFormTitle.setVisible(true);
			lblFormTitle.setEnabled(true);
			populateAndConfigureTable(false);
		}
		else if(btnAudit==event.getButton())
		{
		vlAudit=new VerticalLayout();
		vlAudit.removeAllComponents();
//		new AuditRecordsApp(vlAudit,Common.M_PMS_PROJECTS,projectidAudit);
		vlTableForm.removeAllComponents();
		vlTableForm.addComponent(hlAddEditBtn);
		vlTableForm.addComponent(vlAudit);
		btnAdd.setEnabled(false);
		btnAudit.setEnabled(false);
		btnEdit.setEnabled(false);
		btnHome.setEnabled(true);
		
		}
		else if(btnAddBill==event.getButton()) {
			saveComponentErrorProjectBill();
			try {
				loadTempBillDetails();
			} catch(Exception e) {
				e.printStackTrace();
				logger.info("check  saveProjectBillingDetails() function. Project Bill datas did not saved properly--->" + e);
			}
			tabsheet.setSelectedTab(vlProjectBill);
			tabsheet.getTab(vlProjectPhase).setEnabled(true);
			
		}
		else if(btnEditBill == event.getButton()) {
			try {
			editProjectBillingDetails();
			} catch(Exception e) {
				e.printStackTrace();
				logger.error("Error thorws in editProjectBillingDetails() function--->" + e);
			}
			btnAddBill.setCaption("Update");
			btnAddBill.setEnabled(true);
		}
      else if(btnAuditBill == event.getButton()) {
    	  vlAuditBill = new VerticalLayout();
    	  vlAuditBill.removeAllComponents();
//  		new AuditRecordsApp(vlAuditBill,Common.M_PMS_PROJECT_BILL,projectBillIdAudit);
  		vlBillTableForm.removeAllComponents();
  		vlBillTableForm.addComponent(hlAddEditBtnBill);
  		vlBillTableForm.addComponent(vlAuditBill);
		}
      else if(btnHomeBill == event.getButton()) {
    	  vlBillTableForm.removeAllComponents();
    	  vlBillTableForm.addComponent(hlAddEditBtnBill);
    	  vlBillTableForm.addComponent(tblBill);
    	   //btnHome.setEnabled(false);
    	   //btnHome.setVisible(true);
    		populateAndConfigureBillingTable(null);

		}
      else if(btnAddPhase == event.getButton()) {
    	  saveComponentErrorProjectPhase();
    	  try {
    		  loadTempProjectPhase();
    	  } catch(Exception e) {
    		  e.printStackTrace();
    		  logger.info("check  saveProjectPhaseDetails() function. Project Phase datas did not saved properly--->" + e);
    	  }
    	 
		}
      else if(btnEditPhase == event.getButton()) {
    	  try {
    	  editProjectPhaseDetails();
    	  } catch(Exception e) {
    		  e.printStackTrace();
    		  logger.error("Error thorws in editProjectPhaseDetails() function--->" + e);
    	  }
    	  btnAddPhase.setCaption("Update");
			btnAddPhase.setEnabled(true);
		}
      else if(btnPhaseRevn == event.getButton()) {
    	  Item selectPhase=tblPhase.getItem(tblPhase.getValue());
     	 if (selectPhase != null)
     	 {
     		 ProjectPhaseDM editProjectPhaselist = beanPhase.getItem(tblPhase.getValue()).getBean();
             if(editProjectPhaselist.getBaseLined().equals("Y")) {
            	 tabsheet.getTab(vlProjectPhaseRevision).setEnabled(true);
     			populateAndConfigurePhaseRevisionTable(null);
             }
             else {
            	 tabsheet.getTab(vlProjectPhaseRevision).setEnabled(false);
     			populateAndConfigurePhaseRevisionTable(null);
             }  
     	 }
    	  
		}
      else if(btnAddRevision == event.getButton()) {
    	  saveComponentErrorPhaseRevision();
    	  try {
    	  loadTempPhaseRevision();
    	  } catch(Exception e) {
    		  e.printStackTrace();
    	  }
		}
      else if(btnHomePhase == event.getButton()) {
    	  vlPhaseTableForm.removeAllComponents();
    		vlPhaseTableForm.addComponent(hlAddEditBtnPhase);
  		   vlPhaseTableForm.addComponent(tblPhase);
  		   populateAndConfigurePhaseTable(null);
		}
      else if(btnAuditPhase == event.getButton()) {
    	  vlAuditPhase = new VerticalLayout();
    	  vlAuditPhase.removeAllComponents();
//  		new AuditRecordsApp(vlAuditPhase,Common.M_PMS_PROJECTS_PHASE,projPhaseidAudit);
  		vlPhaseTableForm.removeAllComponents();
  		vlPhaseTableForm.addComponent(hlAddEditBtnPhase);
		vlPhaseTableForm.addComponent(vlAuditPhase);
		}
      else if(btnAddTeam == event.getButton()) {
    	  saveComponentErrorTeam();
    	  try {
    		  loadTempProjectTeam();
    	  } catch(Exception e) {
    		  e.printStackTrace();
    		  logger.info("check  saveProjectTeamDetails() function. Project Team datas did not saved properly--->" + e);
    	  }
		}
      else if(btnEditTeam == event.getButton()) {
    	  try{
			editProjectTeamDetails();
    	  } catch(Exception e) {
    		  e.printStackTrace();
    		  logger.error("Error throws in editProjectTeamDetails() function--->" + e);
    	  }
    	  btnAddTeam.setCaption("Update");
		  btnAddTeam.setEnabled(true);
		}
      else if(btnAuditTeam == event.getButton()) {
    	  vlAuditTeam = new VerticalLayout();
    	  vlAuditTeam.removeAllComponents();
//  		new AuditRecordsApp(vlAuditTeam,Common.M_PMS_PROJECTS_TEAM,projTeamidAudit);
  		vlTeamTableForm.removeAllComponents();
  		 vlTeamTableForm.addComponent(hlAddEditBtnTeam);
   	  vlTeamTableForm.addComponent(vlAuditTeam);
      }
      else if(btnHomeTeam == event.getButton()) {
    	  vlTeamTableForm.removeAllComponents();
    	  vlTeamTableForm.addComponent(hlAddEditBtnTeam);
    	  vlTeamTableForm.addComponent(tblTeam);	
    	  populateAndConfigureTeamTable(null);
		}
      
	}
						


}