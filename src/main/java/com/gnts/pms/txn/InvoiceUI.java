package com.gnts.pms.txn;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;
import com.gnts.base.domain.mst.CurrencyDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.domain.txn.ClientsContactsDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.crm.service.txn.ClientContactsService;
import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateUtils;


import com.gnts.pms.domain.mst.ProjectBillDM;
import com.gnts.pms.domain.mst.ProjectDM;
import com.gnts.pms.domain.mst.ProjectPhaseDM;
import com.gnts.pms.domain.mst.ProjectPhaseRevnDM;
import com.gnts.pms.domain.mst.ProjectTeamDM;
import com.gnts.pms.domain.txn.InvoiceDtlDM;
import com.gnts.pms.domain.txn.InvoiceHdrDM;
import com.gnts.pms.service.mst.ProjectBillService;
import com.gnts.pms.service.mst.ProjectService;
import com.gnts.pms.service.mst.ProjectTeamService;
import com.gnts.pms.service.txn.InvoiceDtlService;
import com.gnts.pms.service.txn.InvoiceHdrService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class InvoiceUI implements ClickListener 
     {
	private static final long serialVersionUID = 1L;
	private VerticalLayout vlSearchPanel=new VerticalLayout();
	private	VerticalLayout vlTablePanel=new VerticalLayout();
	private	VerticalLayout vlMainPanel=new VerticalLayout();
	
	private FormLayout flForm1,flForm2,flForm3,flForm4;
	private FormLayout flDtlForm1,flDtlForm2,flDtlForm3;
	private PopupDateField dfInvoiceDt,dfInvoiceDtlDate;
	private HorizontalLayout hlSaveBtn,hlAddEditBtn;
	private TextArea taRemarks,taItemDesc;
	private TabSheet tabsheet;
	private TextField tfInvoiceAmount,tfDiscountValue,tfST,tfSTValue,tfOther,tfOtherValue,tfInvoiceTotal;
	private TextField tfBillAmt,tfBillValue,tfItemQty;
	private ComboBox cbSearchProject,cbClientName,cbClientContacts,cbProjectName,cbSearchStatus,cbProjectTeam,cbProjectBill,cbStatus;
	private	Button btnSearch,btnReset,btnAdd,btnEdit,btnSave,btnCancel,btnAddDtl,btnEditDtl;
	private	Table tblInvoiceHdr,tblInvoiceDtl;
	private	Label lblNoofRecords,lblFormTitle,lblNotificationIcon,lblNotification;
	private int total = 0;
	private BeanItemContainer<InvoiceDtlDM> beanInvoiceDtl=null;
	private BeanItemContainer<InvoiceHdrDM> beanInvoicehdr=null;
	private BeanItemContainer<ProjectTeamDM> beanProjectTeam =null;
	private BeanItemContainer<ProjectPhaseRevnDM> beanPhaseRevn =null;
	private BeanItemContainer<ProjectPhaseDM> beanPhase  = null;
	private BeanItemContainer<ProjectDM> beanProject =null;
	private BeanItemContainer<EmployeeDM> beanEmployee =null;
	private BeanItemContainer<StatusDM> beanStatus =null;
	private BeanItemContainer<CurrencyDM> beanCurrency=null;
	private BeanItemContainer<ClientDM> beanClient=null;
	private BeanItemContainer<ProjectBillDM> beanProjBill = null;
	private BeanItemContainer<ClientsContactsDM> beanContacts = null;
	
	
	private ClientContactsService serviceClntContact=(ClientContactsService)SpringContextHelper.getBean("clientContact"); 
	private ProjectTeamService servProjTeamBean = (ProjectTeamService) SpringContextHelper.getBean("MProjectTeam");
	private ProjectService servProjectsBean=(ProjectService) SpringContextHelper.getBean("MProjects");
	private InvoiceHdrService servInvoiceheaderBean=(InvoiceHdrService) SpringContextHelper.getBean("TPmsInvoiceheader");
	private InvoiceDtlService servInvoiceDetailBean=(InvoiceDtlService) SpringContextHelper.getBean("TPmsInvoiceDetail");
	private ProjectBillService servProjbillBean=(ProjectBillService) SpringContextHelper.getBean("MProjectBill");
	private ClientService servClientBean=(ClientService) SpringContextHelper.getBean("clients");
	private ProjectDM selectedProject;
	private ClientDM selectedClient;
	private ClientsContactsDM selectedContacts;
	private ProjectBillDM selectedProjBill;
	private ProjectTeamDM selectedProjTeam;
	private String userName,screenName;
	private VerticalLayout vlTableForm,vlInvoiceDetail,vlInvoiceDetailForm;
	private Long projectId,projectTeamId,invoiceDtlId,clientId,invoiceHdrId,searchProjectId,projectBillId,companyId;
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	List<InvoiceDtlDM> tempSaveDetail = new ArrayList<InvoiceDtlDM>();
	private Logger logger = Logger.getLogger(InvoiceUI.class);

	public InvoiceUI() {
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
    	tblInvoiceHdr=new Table();
    	tblInvoiceHdr.setPageLength(12);
    	tblInvoiceHdr.setSizeFull();
    	tblInvoiceHdr.setImmediate(true);
    	tblInvoiceHdr.setFooterVisible(true);
		
		vlMainPanel.setVisible(false);
		vlMainPanel.setMargin(true);
		
		 btnSearch=new Button("Search",this);
		 btnReset=new Button("Reset",this);
		 btnAdd=new Button("Add",this);
		 btnEdit=new Button("Edit",this);
		 btnEdit.setEnabled(false);
		 btnSave=new Button("Save",this);
		 btnCancel=new Button("Cancel",this);
		
		btnSearch.setDescription("Search Invoice");
		btnReset.setDescription("Reset Invoice");
		btnAdd.setDescription("Add Invoice");
		btnEdit.setDescription("Edit Invoice");
		btnSave.setDescription("Save Invoice");
		btnCancel.setDescription("Return to Search");
		
		
		btnAdd.addStyleName("add");
		btnEdit.addStyleName("editbt");
		 btnSearch.addStyleName("searchbt");
		 btnReset.addStyleName("resetbt");
		 btnSave.addStyleName("savebt");
		 btnCancel.addStyleName("cancelbt");
		 
		 lblFormTitle = new Label();
		 lblFormTitle.setContentMode(ContentMode.HTML);
		 lblFormTitle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Search");
			
			lblNotificationIcon = new Label();
			lblNotification = new Label();
			lblNotification.setContentMode(ContentMode.HTML);
		
		 cbSearchStatus=new ComboBox("Status");
		 cbSearchStatus.setInputPrompt(Common.SELECT_PROMPT);
		 cbSearchStatus.setWidth("140");
		 cbSearchStatus.setNullSelectionAllowed(false);
		 cbSearchStatus.setImmediate(true);
		 cbSearchStatus.setItemCaptionPropertyId("desc");
		 beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
		 beanStatus.addAll(Common.listStatus);
	cbSearchStatus.setContainerDataSource(beanStatus);
	
	tfInvoiceAmount = new TextField("Basic Value");
	tfInvoiceAmount.setReadOnly(true);
	tfInvoiceAmount.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;
		public void blur(BlurEvent event) {
			getCalcInvoice();
		}
	});	
	dfInvoiceDt = new PopupDateField("Invoice Date");
	dfInvoiceDt.setInputPrompt(Common.SELECT_PROMPT);
	dfInvoiceDt.setRequired(true);
	taRemarks  = new TextArea("Remarks");
	taRemarks.setInputPrompt("Enter Remarks");
	taRemarks.setMaxLength(100);
	taRemarks.setHeight("70");
	taRemarks.setWidth("170");
	tfInvoiceTotal = new TextField("Invoice Total");
	tfInvoiceTotal.setReadOnly(true);
	
	tfDiscountValue=new TextField("Discount Value");
	tfDiscountValue.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;
		public void blur(BlurEvent event) {
			getCalcInvoice();
		}
	});	
	tfST = new TextField("ST");
	tfST.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;
		public void blur(BlurEvent event) {
			
			getCalcInvoice();
		}
	});	
	tfSTValue = new TextField("ST Value");
	tfOther = new TextField("Others");
	tfOther.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;
		public void blur(BlurEvent event) {
			getCalcInvoice();	
		}
	});	
	tfOtherValue = new TextField("Others Value");
	cbClientName = new ComboBox("Client Name");
	cbClientName.setInputPrompt(Common.SELECT_PROMPT);
	cbClientName.setRequired(true);
	cbClientName.setImmediate(true);
	cbClientName.setWidth("150");
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
				clientId = selectedClient.getClientId();
				
				loadClientContactList(clientId);
			}
		}
	});
	
	cbClientContacts = new ComboBox("Contact Name");
	cbClientContacts.setInputPrompt(Common.SELECT_PROMPT);
	cbClientContacts.setImmediate(true);
	cbClientContacts.setWidth("150");
	cbClientContacts.setNullSelectionAllowed(false);
	cbClientContacts.setItemCaptionPropertyId("contactName");
	cbClientContacts.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				BeanItem<?> item = (BeanItem<?>) cbClientContacts.getItem(itemid);
				selectedContacts = (ClientsContactsDM) item.getBean();
			}
		}
	});
	
	cbProjectName=new ComboBox("Project Name");
	cbProjectName.setInputPrompt(Common.SELECT_PROMPT);
	cbProjectName.setImmediate(true);
	cbProjectName.setRequired(true);
	cbProjectName.setWidth("150");
	cbProjectName.setNullSelectionAllowed(false);
	cbProjectName.setItemCaptionPropertyId("projectName");
	loadProjectList();
	cbProjectName.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				projectTeamId = null;
				BeanItem<?> item = (BeanItem<?>) cbProjectName.getItem(itemid);
				selectedProject = (ProjectDM) item.getBean();
				projectId = selectedProject.getProjectId();
				
				loadProjectBillList(projectId);
			}
		}
	});
	
	cbStatus=new ComboBox("Status");
	cbStatus.setInputPrompt(Common.SELECT_PROMPT);
	cbStatus.setWidth("140");
	cbStatus.setImmediate(true);
	cbStatus.setNullSelectionAllowed(false);
	cbStatus.setItemCaptionPropertyId("desc");
	beanStatus=new BeanItemContainer<StatusDM>(StatusDM.class);
	beanStatus.addAll(Common.listStatus);
	cbStatus.setContainerDataSource(beanStatus);
	cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
	 
	cbProjectBill=new ComboBox("Project Billing");
	cbProjectBill.setInputPrompt(Common.SELECT_PROMPT);
	cbProjectBill.setImmediate(true);
	cbProjectBill.setWidth("150");
	cbProjectBill.setNullSelectionAllowed(false);
	cbProjectBill.setItemCaptionPropertyId("billingDesc");
	cbProjectBill.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			// TODO Auto-generated method stub
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				projectTeamId = null;
				BeanItem<?> item = (BeanItem<?>) cbProjectBill.getItem(itemid);
				selectedProjBill = (ProjectBillDM) item.getBean();
				projectBillId = selectedProjBill.getProjectBillId();
			}
		}
	});
	cbProjectTeam=new ComboBox("Project Role");
	cbProjectTeam.setInputPrompt(Common.SELECT_PROMPT);
	cbProjectTeam.setImmediate(true);
	cbProjectTeam.setNullSelectionAllowed(false);
	cbProjectTeam.setWidth("150");
	cbProjectTeam.setItemCaptionPropertyId("roleDesc");
	cbProjectTeam.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			// TODO Auto-generated method stub
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				BeanItem<?> item = (BeanItem<?>) cbProjectTeam.getItem(itemid);
				selectedProjTeam= (ProjectTeamDM) item.getBean();
				projectTeamId = selectedProjTeam.getProjectTeamId();
			}
		}
	});
	
	flForm1=new FormLayout();
	flForm1.setSpacing(true);
	flForm1.addComponent(cbProjectName);
	flForm1.addComponent(cbProjectBill);
	flForm1.addComponent(cbClientName);
	flForm1.addComponent(cbClientContacts);
	flForm2=new FormLayout();
	flForm2.setSpacing(true);
	flForm2.addComponent(dfInvoiceDt);
	flForm2.addComponent(tfInvoiceAmount);
	flForm2.addComponent(tfST);
	flForm2.addComponent(tfSTValue);
	flForm3=new FormLayout();
	flForm3.setSpacing(true);
	flForm3.addComponent(tfOther);
	flForm3.addComponent(tfOtherValue);
	flForm3.addComponent(tfDiscountValue);
	flForm3.addComponent(tfInvoiceTotal);

	flForm4=new FormLayout();
	flForm4.setSpacing(true);
	flForm4.addComponent(cbStatus);
	flForm4.addComponent(taRemarks);

	HorizontalLayout hlMainform = new HorizontalLayout();
	hlMainform.setSpacing(true);
	hlMainform.addComponent(flForm1);
	hlMainform.addComponent(flForm2);
	hlMainform.addComponent(flForm3);
	hlMainform.addComponent(flForm4);
	
	hlSaveBtn = new HorizontalLayout();
	hlSaveBtn.addComponent(btnSave);
	hlSaveBtn.addComponent(btnCancel);
	hlSaveBtn.setVisible(false);
	 
	GridLayout glMainPanel = new GridLayout(1,1);
	glMainPanel.setSpacing(true);
	glMainPanel.setMargin(true);
	glMainPanel.setSizeFull();
	glMainPanel.addComponent(hlMainform);
	
	tfItemQty = new TextField("Item Quantity");
	tfBillAmt = new TextField("Bill Amount");
	tfBillAmt.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;
		public void blur(BlurEvent event) {
			try {
			BigDecimal unitprice = new BigDecimal(tfItemQty.getValue().toString());
			BigDecimal billamt = new BigDecimal(tfBillAmt.getValue().toString());
			tfBillValue.setReadOnly(false);
			tfBillValue.setValue(unitprice.multiply(billamt).toString());
			tfBillValue.setReadOnly(true);
			
			} catch(Exception e) {
				logger.info("bill Amount calcuation wrong------>>>>"+e);
			}
		}
	});	
	tfBillValue = new TextField("Bill Value");
	tfBillValue.setReadOnly(true);
	dfInvoiceDtlDate = new PopupDateField("Invoice Date");
	dfInvoiceDtlDate.setInputPrompt(Common.SELECT_PROMPT);
	dfInvoiceDtlDate.setRequired(true);
	taItemDesc = new TextArea("Item Description");
	taItemDesc.setInputPrompt("Enter Item Description");
	taItemDesc.setRequired(true);
	taItemDesc.setHeight("60");
	
	flDtlForm1 = new FormLayout();
	flDtlForm1.setSpacing(true);
	flDtlForm1.addComponent(dfInvoiceDtlDate);
	flDtlForm1.addComponent(tfItemQty);
	
	flDtlForm2 = new FormLayout();
	flDtlForm2.setSpacing(true);
	flDtlForm2.addComponent(tfBillAmt);
	flDtlForm2.addComponent(tfBillValue);
	
	flDtlForm3 = new FormLayout();
	flDtlForm3.setSpacing(true);
	flDtlForm3.addComponent(taItemDesc);
	
	HorizontalLayout hlDetailform = new HorizontalLayout();
	hlDetailform.setSpacing(true);
	hlDetailform.addComponent(flDtlForm1);
	hlDetailform.addComponent(flDtlForm2);
	hlDetailform.addComponent(flDtlForm3);
	 
	GridLayout glDetailPanel = new GridLayout(1,1);
	glDetailPanel.setSpacing(true);
	glDetailPanel.setMargin(true);
	glDetailPanel.setSizeFull();
	glDetailPanel.addComponent(hlDetailform);
	
	btnAddDtl = new Button("Add",this);
	btnEditDtl = new Button("Edit",this);
	btnEditDtl.setEnabled(false);
	btnAddDtl.addStyleName("add");
	btnEditDtl.addStyleName("editbt");
	btnAddDtl.setDescription("Add Invoice Detail");
	btnEditDtl.setDescription("Edit Invoice Detail");
	
	HorizontalLayout hlDtlBtnform = new HorizontalLayout();
	hlDtlBtnform.setSpacing(true);
	hlDtlBtnform.addComponent(btnAddDtl);
	hlDtlBtnform.addComponent(btnEditDtl);
	
  HorizontalLayout  hlAddEditBtnDtl = new HorizontalLayout();
  hlAddEditBtnDtl.addStyleName("topbarthree");
  hlAddEditBtnDtl.setWidth("100%");
  hlAddEditBtnDtl.addComponent(hlDtlBtnform);
  hlAddEditBtnDtl.setHeight("28px");
	
	tblInvoiceDtl=new Table();
	tblInvoiceDtl.setPageLength(3);
	tblInvoiceDtl.setSizeFull();
	tblInvoiceDtl.setImmediate(true);
	tblInvoiceDtl.setFooterVisible(true);
	
	vlInvoiceDetailForm = new VerticalLayout();
	vlInvoiceDetailForm.setSpacing(false);
	vlInvoiceDetailForm.addComponent(hlAddEditBtnDtl);
	vlInvoiceDetailForm.addComponent(tblInvoiceDtl);
	
	vlInvoiceDetail = new VerticalLayout();
	vlInvoiceDetail.setMargin(true);
	vlInvoiceDetail.setSpacing(true);
	vlInvoiceDetail.addComponent(PanelGenerator.createPanel(glDetailPanel));
	vlInvoiceDetail.addComponent(vlInvoiceDetailForm);
	
	
	tabsheet = new TabSheet();
	tabsheet.setSizeFull();
	tabsheet.setImmediate(true);
	tabsheet.addTab(vlInvoiceDetail,"Invoice Detail");
	
	cbSearchProject = new ComboBox("Project Name");
	cbSearchProject.setInputPrompt(Common.SELECT_PROMPT);
	cbSearchProject.setImmediate(true);
	cbSearchProject.setWidth("150");
	cbSearchProject.setNullSelectionAllowed(false);
	cbSearchProject.setItemCaptionPropertyId("projectName");
	loadSearchProjectList();
	cbSearchProject.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				BeanItem<?> item = (BeanItem<?>) cbSearchProject.getItem(itemid);
				selectedProject = (ProjectDM) item.getBean();
				searchProjectId = selectedProject.getProjectId();
				
			}
		}
	});
		
	    FormLayout flSearchEmployee=new FormLayout();
	    flSearchEmployee.addComponent(cbSearchProject);
		FormLayout flSearchStatus=new FormLayout();
		flSearchStatus.addComponent(cbSearchStatus);
		
    HorizontalLayout hlSearchform=new HorizontalLayout(); 
    hlSearchform.setSpacing(true);
    hlSearchform.setMargin(true);
    hlSearchform.addComponent(flSearchEmployee);
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
	
	vlSearchPanel.addComponent(PanelGenerator.createPanel(hlSearchComponent));
	vlSearchPanel.setMargin(true);
    
	HorizontalLayout hlTableTitle = new HorizontalLayout();
	hlTableTitle.addComponent(btnAdd);
	hlTableTitle.addComponent(btnEdit);
	
    hlAddEditBtn = new HorizontalLayout();
	hlAddEditBtn.addStyleName("topbarthree");
	hlAddEditBtn.setWidth("100%");
	hlAddEditBtn.addComponent(hlTableTitle);
	hlAddEditBtn.setHeight("28px");
	
	 vlTableForm = new VerticalLayout();
	 vlTableForm.setSizeFull();
	 vlTableForm.setMargin(true);
	 vlTableForm.addComponent(hlAddEditBtn);
	 vlTableForm.addComponent(tblInvoiceHdr);
	
    lblNoofRecords=new Label(" ",ContentMode.HTML);
    lblNoofRecords.addStyleName("lblfooter");
	
	vlMainPanel.addComponent(PanelGenerator.createPanel(glMainPanel));
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
	
    populateAndConfigureTable(false);	
    
    excelexporter.setTableToBeExported(tblInvoiceHdr);
	csvexporter.setTableToBeExported(tblInvoiceHdr);
	pdfexporter.setTableToBeExported(tblInvoiceHdr);
	excelexporter.setCaption("Microsoft Excel (XLS)");
	excelexporter.setStyleName("borderless");
	csvexporter.setCaption("Comma Dilimited (CSV)");
	csvexporter.setStyleName("borderless");
	pdfexporter.setCaption("Acrobat Document (PDF)");
	pdfexporter.setStyleName("borderless");
}
    /*
     * buildNotifications()-->this function is used for popupview for Download
     * components
     */
    
    /*
     * populateAndConfigureTable()-->this function used to load the list to the table.
     * 
     * @param boolean search if(search==true)--> it performs search operation
     * else it loads all values
     */
private void populateAndConfigureTable(boolean search) {
	try {
		tblInvoiceHdr.removeAllItems();
    	 List<InvoiceHdrDM> invoiceHdrList=new ArrayList<InvoiceHdrDM>();
    	if(search)
    	{
    		invoiceHdrList = new ArrayList<InvoiceHdrDM>();
    		String statusArg=null;
    		
    		StatusDM sts= (StatusDM) cbSearchStatus.getValue();
    		try {
    			statusArg = sts.getCode();
			} catch (Exception e) {
				logger.info("status is empty on search");
			}
    		if (statusArg!=null || searchProjectId!=null) {
    			invoiceHdrList = servInvoiceheaderBean.getInvoiceHeaderList(null,statusArg,searchProjectId,companyId);
			total = invoiceHdrList.size();
			}
    		if(total == 0) {
    			lblNotification.setValue("No Records Found");
    		}
			 
		}
    	else {
    		invoiceHdrList = servInvoiceheaderBean.getInvoiceHeaderList(null,null,null,companyId);
			total = invoiceHdrList.size();
		}
    	lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
    	  beanInvoicehdr = new BeanItemContainer<InvoiceHdrDM>(InvoiceHdrDM.class);
    	  beanInvoicehdr.addAll(invoiceHdrList);
    	 tblInvoiceHdr.setContainerDataSource(beanInvoicehdr);
    	 tblInvoiceHdr.setSelectable(true);
    	 tblInvoiceHdr.setColumnFooter("lastUpdatedBy","No. of Records:"+total);
    	 tblInvoiceHdr.setColumnAlignment("invoiceNo",Align.RIGHT);
    	 tblInvoiceHdr.setColumnAlignment("invoiceAmount",Align.RIGHT);
    	 tblInvoiceHdr.setVisibleColumns(new Object[] {"invoiceNo","invoiceDate","projectName","clientName","invoiceAmount","status","lastUpdtDate","lastUpdatedBy"});
    	 tblInvoiceHdr.setColumnHeaders(new String[] {"Ref.Id","Invoice Date","Project Name","Client Name","Invoice Amount","Status","Last Updated Date","Last Updated By"});	 
    	
    	 tblInvoiceHdr.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			   @Override
				public void itemClick(ItemClickEvent event) {
    				// TODO Auto-generated method stub
    				if (tblInvoiceHdr.isSelected(event.getItemId())) {
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
		logger.error("error during populate values on the table, The Error is ----->"+e);
	}
    }
private void populateAndConfigureInvoiceDetailTable(Long invoiceids) {
	try {
		tblInvoiceDtl.removeAllItems();
    	 if(invoiceids!=null) {
    	 tempSaveDetail = servInvoiceDetailBean.getInvoiceDetailList(invoiceids);
			total = tempSaveDetail.size();
    	 }
    	lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
    	beanInvoiceDtl = new BeanItemContainer<InvoiceDtlDM>(InvoiceDtlDM.class);
    	beanInvoiceDtl.addAll(tempSaveDetail);
    	  tblInvoiceDtl.setContainerDataSource(beanInvoiceDtl);
    	  tblInvoiceDtl.setSelectable(true);
    	  tblInvoiceDtl.setColumnAlignment("invoiceDetailNo",Align.RIGHT);
    	  tblInvoiceDtl.setColumnAlignment("basicValue",Align.RIGHT);
    	  tblInvoiceDtl.setColumnAlignment("itemQuantity",Align.RIGHT);
    	  tblInvoiceDtl.setColumnFooter("lastUpdatedBy","No. of Records:"+total);
    	  tblInvoiceDtl.setVisibleColumns(new Object[] {"invoiceDetailNo","invoiceDate","basicValue","itemQuantity","itemDesc"});
    	  tblInvoiceDtl.setColumnHeaders(new String[] {"Ref.Id","Invoice Detail Date","Basic Value","Item Quantity","Item Description"});	 
    	
    	  
    	  BigDecimal sum=new BigDecimal("0");
    		@SuppressWarnings("unchecked")
			Collection<InvoiceDtlDM>  itemIds= (Collection<InvoiceDtlDM>) tblInvoiceDtl.getVisibleItemIds();
    		
    		for(InvoiceDtlDM poacc : (Collection<InvoiceDtlDM>)itemIds)
    		{
    			sum=sum.add(poacc.getBasicValue());
    			
    		}
    		tfInvoiceAmount.setReadOnly(false);
    		tfInvoiceAmount.setValue(sum+"");
    		tfInvoiceAmount.setReadOnly(true);
    	  tblInvoiceDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			   @Override
				public void itemClick(ItemClickEvent event) {
    				// TODO Auto-generated method stub
    				if (tblInvoiceDtl.isSelected(event.getItemId())) {
    					btnAddDtl.setEnabled(true);
    					btnEditDtl.setEnabled(false);
    				} else {
    					btnAddDtl.setEnabled(false);
    					btnEditDtl.setEnabled(true);	
    				}
    		resetFieldsDetail();
    			}
    		});
	} catch(Exception e) {
		logger.error("error during populate values on the table, The Error is ----->"+e);
	}
    }
/*
 * resetFields()->this function is used for reset the UI components
 */
     private void resetFields() {
    	 tfInvoiceAmount.setReadOnly(false);
    	 tfInvoiceAmount.setValue("0.0"); 
    	 tfInvoiceAmount.setReadOnly(true);
    	 tfDiscountValue.setValue("0.0");
    	 tfST.setValue("0.0");
    	 tfSTValue.setValue("0.0");
    	 tfOther.setValue("0.0");
    	 tfOtherValue.setValue("0.0");
    	 tfInvoiceTotal.setReadOnly(false);
    	 tfInvoiceTotal.setValue("0.0");
    	 tfInvoiceTotal.setReadOnly(true);
    	 cbProjectBill.setValue(null);
    	 cbProjectTeam.setValue(null);
    	 dfInvoiceDt.setValue(null);
    	 cbClientContacts.setValue(null);
    	 taRemarks.setValue("");
    	 dfInvoiceDt.setComponentError(null);
    	 cbProjectName.setComponentError(null);
    	 cbClientName.setComponentError(null);
    	 cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
    	 cbProjectName.setValue(null);
    	 cbClientName.setValue(null);
    	 btnSave.setComponentError(null);
    	btnSave.setCaption("Save"); 
    	lblFormTitle.setValue("&nbsp;&nbsp;<b>"+ screenName+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Search</font> &nbsp;>&nbsp;Add New");
    	lblNotificationIcon.setIcon(null);
    	lblNotification.setValue("");
    	invoiceHdrId=null;
     }
     private void resetFieldsDetail()
     {
    	 tfBillAmt.setValue("0.0");
    	 tfBillValue.setReadOnly(false);
    	 tfBillValue.setValue("0.0");
    	 tfBillValue.setReadOnly(true);
    	 tfItemQty.setValue("0.0");
    	 taItemDesc.setValue("");
    	 dfInvoiceDtlDate.setValue(null);
    	 dfInvoiceDtlDate.setComponentError(null);
    	 taItemDesc.setComponentError(null);
    	 btnAddDtl.setComponentError(null);
    	 btnAddDtl.setCaption("Add");
     }
     /*
 	 * editState()-->this function used for edit the existing details
 	 */
    
     /*
 	 * this function handles load country list to country name component.
 	 */
     private void loadProjectList() {
    	 try {
    	 List <ProjectDM> getProjectlist = servProjectsBean.getProjectList(null,null,null,Common.ACTIVE_DESC);
    	  beanProject = new BeanItemContainer<ProjectDM>(ProjectDM.class);
    	  beanProject.addAll(getProjectlist);
    	  cbProjectName.setContainerDataSource(beanProject);
    	 } catch(Exception e) {
    		 logger.warn("Loading null values in loadProjectList() function------->>>>>>>"+e);
    	 }
     }
     /*
 	 * 
 	 * this function handles load state list to state name component.
 	 * 
 	 */
     private void loadProjectBillList(Long projecId) { 
    	 try {
    	 List <ProjectBillDM> getBilllist=servProjbillBean.getProjectBillList(null,Common.ACTIVE_DESC, projecId);
    	 beanProjBill = new  BeanItemContainer<ProjectBillDM>(ProjectBillDM.class);
    	 beanProjBill.addAll(getBilllist);
    	 cbProjectBill.setContainerDataSource(beanProjBill);
    	 } catch(Exception e) {
    		 logger.warn("Loading null values in loadStateList() function -------->>>>>>"+e);
    	 }
     }
     /*
 	 * 
 	 * this function handles load city list to city name component.
 	 * 
 	 */
     private void loadProjectTeamList(Long projecctphaseid) {
    	 try {
    	 List <ProjectTeamDM> getProjectTeamlist = servProjTeamBean.getProjectTeamList(null,projecctphaseid);
    	 beanProjectTeam = new BeanItemContainer<ProjectTeamDM>(ProjectTeamDM.class);
    	 beanProjectTeam.addAll(getProjectTeamlist);
    	 cbProjectTeam.setContainerDataSource(beanProjectTeam);
    	 } catch(Exception e) {
    		 logger.warn("Loaing null values in loadCityList() function----->>>>>>>"+e);
    	 }
     }
     /*
  	 * 
  	 * this function handles load Employee list to Employee name component.
  	 * 
  	 */
      private void loadClientList() {
     	 try {
     	 List <ClientDM> getClientlist = servClientBean.getClientDetails(null,null,null,null,null,null,null,Common.ACTIVE_DESC);
        beanClient = new  BeanItemContainer<ClientDM>(ClientDM.class);
        beanClient.addAll(getClientlist);
     	cbClientName.setContainerDataSource(beanClient);
     	 } catch(Exception e) {
     		 logger.warn("Loaing null values in loadEmployeeList() function----->>>>>>>"+e);
     	 }
      }
      private void loadClientContactList(Long clientids) {
      	 try {
      	 List <ClientsContactsDM> getClientlist = serviceClntContact.getClientContactsDetails(null, clientids,null,Common.ACTIVE_DESC);
      	  beanContacts = new BeanItemContainer<ClientsContactsDM>(ClientsContactsDM.class);
      	beanContacts.addAll(getClientlist);
      	cbClientContacts.setContainerDataSource(beanContacts);
      	 } catch(Exception e) {
      		 logger.warn("Loaing null values in loadEmployeeList() function----->>>>>>>"+e);
      	 }
       }
      /*
    	 * 
    	 * this function handles load Employee list to Search Employee name component.
    	 * 
    	 */
        private void loadSearchProjectList() {
       	 try {
       		List <ProjectDM> getProjectlist = servProjectsBean.getProjectList(null,null,null,Common.ACTIVE_DESC);
      	  beanProject = new BeanItemContainer<ProjectDM>(ProjectDM.class);
      	  beanProject.addAll(getProjectlist);
      	  cbSearchProject.setContainerDataSource(beanProject);
       	 } catch(Exception e) {
       		 logger.warn("Loaing null values in loadEmployeeList() function----->>>>>>>"+e);
       	 }
        }
     /*
 	 * saveDetails()-->this function is used for save/update the records
 	 */	
 
 private void saveInvoiceHeader()
 {
	
	 btnSave.setComponentError(null);
	 if(tblInvoiceHdr.getValue()!=null)
	 {
		 InvoiceHdrDM updateInvoiceHdr = beanInvoicehdr.getItem(tblInvoiceHdr.getValue()).getBean();
		 updateInvoiceHdr.setInvoiceNo(invoiceHdrId);
		 updateInvoiceHdr.setInvoiceDate(dfInvoiceDt.getValue());
		 updateInvoiceHdr.setClientId(selectedClient);
		 updateInvoiceHdr.setContactId(selectedContacts);
		 updateInvoiceHdr.setDiscountValue(new BigDecimal(tfDiscountValue.getValue().toString()));
		 updateInvoiceHdr.setInvoiceAmount(new BigDecimal(tfInvoiceAmount.getValue().toString()));
		 updateInvoiceHdr.setInvoiceTotal(new BigDecimal(tfInvoiceTotal.getValue().toString()));
		 updateInvoiceHdr.setLastUpdatedBy(userName);
		 updateInvoiceHdr.setLastUpdtDate(DateUtils.getcurrentdate());
		 updateInvoiceHdr.setOther(new BigDecimal(tfOther.getValue().toString()));
		 updateInvoiceHdr.setOtherValue(new BigDecimal(tfOtherValue.getValue().toString()));
		 updateInvoiceHdr.setProjectBillId(selectedProjBill);
		 updateInvoiceHdr.setProjectId(selectedProject);
		 updateInvoiceHdr.setRemarks(taRemarks.getValue().toString());
		 updateInvoiceHdr.setSt(new BigDecimal(tfST.getValue().toString()));
		 updateInvoiceHdr.setStValue(new BigDecimal(tfSTValue.getValue().toString()));
		 StatusDM statusupdt = (StatusDM) cbStatus.getValue();
		 updateInvoiceHdr.setStatus(statusupdt.getCode());
		 if(cbClientName.isValid() && dfInvoiceDt.isValid() && tfInvoiceAmount.isValid() && tfInvoiceTotal.isValid() && cbProjectName.isValid()) {
	         servInvoiceheaderBean.saveOrUpdateInvoiceHeader(updateInvoiceHdr);
			 populateAndConfigureTable(false);
			 invoiceHdrId = updateInvoiceHdr.getInvoiceNo();
			 btnSave.setCaption("Save");
			 lblNotification.setValue("Successfully Updated");
			 lblNotificationIcon.setIcon(new ThemeResource("img/success_small.png"));
		 }
		 else {
			 btnSave.setComponentError(new UserError("Form is not valid"));
			 lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
			 lblNotification.setValue("Save failed, please check the data and try again ");
		 }		 
}
	 else {
		 
		 InvoiceHdrDM saveInvoiceHdr = new InvoiceHdrDM();
		 saveInvoiceHdr.setInvoiceNo(invoiceHdrId);
		 saveInvoiceHdr.setInvoiceDate(dfInvoiceDt.getValue());
		 saveInvoiceHdr.setClientId(selectedClient);
		 saveInvoiceHdr.setContactId(selectedContacts);
		 saveInvoiceHdr.setDiscountValue(new BigDecimal(tfDiscountValue.getValue().toString()));
		 saveInvoiceHdr.setInvoiceAmount(new BigDecimal(tfInvoiceAmount.getValue().toString()));
		 saveInvoiceHdr.setInvoiceTotal(new BigDecimal(tfInvoiceTotal.getValue().toString()));
		 saveInvoiceHdr.setLastUpdatedBy(userName);
		 saveInvoiceHdr.setLastUpdtDate(DateUtils.getcurrentdate());
		 saveInvoiceHdr.setOther(new BigDecimal(tfOther.getValue().toString()));
		 saveInvoiceHdr.setOtherValue(new BigDecimal(tfOtherValue.getValue().toString()));
		 saveInvoiceHdr.setProjectBillId(selectedProjBill);
		 saveInvoiceHdr.setProjectId(selectedProject);
		 saveInvoiceHdr.setRemarks(taRemarks.getValue().toString());
		 saveInvoiceHdr.setSt(new BigDecimal(tfST.getValue().toString()));
		 saveInvoiceHdr.setStValue(new BigDecimal(tfSTValue.getValue().toString()));
		 StatusDM statusupdt = (StatusDM) cbStatus.getValue();
		 saveInvoiceHdr.setStatus(statusupdt.getCode());
		 
		 if(cbProjectName.isValid() && dfInvoiceDt.isValid() && cbClientName.isValid() && tfInvoiceAmount.isValid() && tfInvoiceTotal.isValid()) {
			 servInvoiceheaderBean.saveOrUpdateInvoiceHeader(saveInvoiceHdr);
			 populateAndConfigureTable(false);
			 //invoiceHdrId = saveInvoiceHdr.getInvoiceNo();
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
 private void editInvoiceHeader()
 {
	 Item selectHeader = tblInvoiceHdr.getItem(tblInvoiceHdr.getValue());
	 if (selectHeader != null)
	 {
		 InvoiceHdrDM editInvoiceHeaderlist = beanInvoicehdr.getItem(tblInvoiceHdr.getValue()).getBean();
		 if(editInvoiceHeaderlist.getDiscountValue()!=null) {
		 tfDiscountValue.setValue(selectHeader.getItemProperty("discountValue").getValue().toString());	
		 }
		 if(editInvoiceHeaderlist.getInvoiceAmount()!=null) {
			 tfInvoiceAmount.setReadOnly(false);
			 tfInvoiceAmount.setValue(selectHeader.getItemProperty("invoiceAmount").getValue().toString());	
			 tfInvoiceAmount.setReadOnly(true);	 
		 }
		 if(editInvoiceHeaderlist.getInvoiceDate()!=null) {
			 dfInvoiceDt.setValue((Date)selectHeader.getItemProperty("invoiceDate").getValue());	
			 }
		 if(editInvoiceHeaderlist.getInvoiceTotal()!=null) {
			 tfInvoiceTotal.setReadOnly(false);
			 tfInvoiceTotal.setValue(selectHeader.getItemProperty("invoiceTotal").getValue().toString());
			 tfInvoiceTotal.setReadOnly(true);
			 }
		 if(editInvoiceHeaderlist.getOther()!=null) {
			 tfOther.setValue(selectHeader.getItemProperty("other").getValue().toString());	
			 }
		 if(editInvoiceHeaderlist.getOtherValue()!=null) {
			 tfOtherValue.setValue(selectHeader.getItemProperty("otherValue").getValue().toString());	
			 }
		 if(editInvoiceHeaderlist.getRemarks()!=null) {
			 taRemarks.setValue(selectHeader.getItemProperty("remarks").getValue().toString());	
			 }
		 if(editInvoiceHeaderlist.getSt()!=null) {
			 tfST.setValue(selectHeader.getItemProperty("st").getValue().toString());	
			 }
		 if(editInvoiceHeaderlist.getStValue()!=null) {
			 tfSTValue.setValue(selectHeader.getItemProperty("stValue").getValue().toString());	
			 }
		 String gtCode = selectHeader.getItemProperty("status").getValue().toString();
			cbStatus.setValue(Common.getStatus(gtCode));
			
			ClientDM editGetClient = editInvoiceHeaderlist.getClientId();
			Collection<?> collClient = cbClientName.getItemIds();
			for (Iterator<?> iterator = collClient.iterator(); iterator.hasNext();) {
				Object itemid = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbClientName.getItem(itemid);
				ClientDM editClientBean = (ClientDM) item.getBean();
				if (editGetClient != null && editGetClient.getClientId().equals(editClientBean.getClientId())) {
					cbClientName.setValue(itemid);
					break;
				} else {
					cbClientName.setValue(null);
				}
			}
			
			ClientsContactsDM editGetContact = editInvoiceHeaderlist.getContactId();
			Collection<?> collContact = cbClientContacts.getItemIds();
			for (Iterator<?> iterator = collContact.iterator(); iterator.hasNext();) {
				Object itemid = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbClientContacts.getItem(itemid);
				ClientsContactsDM editContactBean = (ClientsContactsDM) item.getBean();
				if (editGetContact != null && editGetContact.getContactId().equals(editContactBean.getContactId())) {
					cbClientContacts.setValue(itemid);
					break;
				} else {
					cbClientContacts.setValue(null);
				}
			}
			
			ProjectDM editGetProject = editInvoiceHeaderlist.getProjectId();
			Collection<?> collProject = cbProjectName.getItemIds();
			for (Iterator<?> iterator = collProject.iterator(); iterator.hasNext();) {
				Object itemid = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProjectName.getItem(itemid);
				ProjectDM editProjectBean = (ProjectDM) item.getBean();
				if (editGetProject != null && editGetProject.getProjectId().equals(editProjectBean.getProjectId())) {
					cbProjectName.setValue(itemid);
					break;
				} else {
					cbProjectName.setValue(null);
				}
			}
			
			ProjectBillDM editGetBill = editInvoiceHeaderlist.getProjectBillId();
			Collection<?> collBill = cbProjectBill.getItemIds();
			for (Iterator<?> iterator = collBill.iterator(); iterator.hasNext();) {
				Object itemid = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProjectBill.getItem(itemid);
				ProjectBillDM editBillBean = (ProjectBillDM) item.getBean();
				if (editGetBill != null && editGetBill.getProjectBillId().equals(editBillBean.getProjectBillId())) {
					cbProjectBill.setValue(itemid);
					break;
				} else {
					cbProjectBill.setValue(null);
				}
			}
			 invoiceHdrId = new Long(selectHeader.getItemProperty("invoiceNo").getValue().toString()); 
			populateAndConfigureInvoiceDetailTable(invoiceHdrId);
		 
	 }
 }
 private void loadTempInvoiceDetail()
 {
	 try
	 {
	 btnAddDtl.setComponentError(null);
	 invoiceDtlId = servInvoiceDetailBean.getNextSequence();
	 if(tblInvoiceDtl.getValue()!=null)
	 {
		 InvoiceDtlDM updateInvoiceDtl = beanInvoiceDtl.getItem(tblInvoiceDtl.getValue()).getBean();
		 updateInvoiceDtl.setInvoiceDetailNo(updateInvoiceDtl.getInvoiceDetailNo());
		 updateInvoiceDtl.setProjectBillId(projectBillId);
		 updateInvoiceDtl.setBasicValue(new BigDecimal(tfBillValue.getValue().toString()));
		 updateInvoiceDtl.setBillAmount(new BigDecimal(tfBillAmt.getValue().toString()));
		 updateInvoiceDtl.setInvoiceDate(dfInvoiceDtlDate.getValue());
		 updateInvoiceDtl.setItemDesc(taItemDesc.getValue().toString());
		 updateInvoiceDtl.setItemQuantity(new BigDecimal(tfItemQty.getValue().toString()));
		 if(dfInvoiceDtlDate.isValid() && tfItemQty.isValid() && tfBillValue.isValid() && taItemDesc.isValid()) {
			 tempSaveDetail.add(updateInvoiceDtl);
			 populateAndConfigureInvoiceDetailTable(null);
			 resetFieldsDetail();
			 btnEditDtl.setEnabled(false);
			 btnAddDtl.setCaption("Add");
		 }
		 else {
			 btnAddDtl.setComponentError(new UserError("Form is not valid"));
		 }		 
}
	 else {
		 
		 InvoiceDtlDM saveInvoiceDtl = new InvoiceDtlDM();
		 saveInvoiceDtl.setInvoiceDetailNo(invoiceDtlId);
		 saveInvoiceDtl.setProjectBillId(projectBillId);
		 saveInvoiceDtl.setBasicValue(new BigDecimal(tfBillValue.getValue().toString()));
		 saveInvoiceDtl.setBillAmount(new BigDecimal(tfBillAmt.getValue().toString()));
		 saveInvoiceDtl.setInvoiceDate(dfInvoiceDtlDate.getValue());
		 saveInvoiceDtl.setItemDesc(taItemDesc.getValue().toString());
		 saveInvoiceDtl.setItemQuantity(new BigDecimal(tfItemQty.getValue().toString()));
		 if(dfInvoiceDtlDate.isValid() && tfItemQty.isValid() && tfBillValue.isValid() && taItemDesc.isValid()) {
			 tempSaveDetail.add(saveInvoiceDtl);
				
			 populateAndConfigureInvoiceDetailTable(null);
			 resetFieldsDetail();
			 btnAddDtl.setCaption("Add");
		 }
		 else {
			 btnAddDtl.setComponentError(new UserError("Form is not valid"));
		 }
	 }
	 } catch(Exception e) {
		 e.printStackTrace();
	 }
 }
 private void editInvoiceDetails() {  
	 Item selectDetail=tblInvoiceDtl.getItem(tblInvoiceDtl.getValue());
	 if (selectDetail != null)
	 {
		 InvoiceDtlDM editInvoiceDetaillist = beanInvoiceDtl.getItem(tblInvoiceDtl.getValue()).getBean();
		 if(editInvoiceDetaillist.getBasicValue()!=null) {
		 tfBillValue.setReadOnly(false);	 
		 tfBillValue.setValue(selectDetail.getItemProperty("basicValue").getValue().toString());
		 tfBillValue.setReadOnly(true);
		 }
		 if(editInvoiceDetaillist.getBillAmount()!=null) {
			 tfBillAmt.setValue(selectDetail.getItemProperty("billAmount").getValue().toString());	
			 }
		 if(editInvoiceDetaillist.getInvoiceDate()!=null) {
			 dfInvoiceDtlDate.setValue((Date)selectDetail.getItemProperty("invoiceDate").getValue());	
			 }
		 if(editInvoiceDetaillist.getItemDesc()!=null) {
			 taItemDesc.setValue(selectDetail.getItemProperty("itemDesc").getValue().toString());	
			 }
		 if(editInvoiceDetaillist.getItemQuantity()!=null) {
			 tfItemQty.setValue(selectDetail.getItemProperty("itemQuantity").getValue().toString());	
			 }
	 }
	 
 } 
 @SuppressWarnings("unchecked")
private void saveTempProjectBillDetails()
 {
	try {
	
		Collection<InvoiceDtlDM> detailList = (Collection<InvoiceDtlDM>) tblInvoiceDtl.getVisibleItemIds();
		 
	 for(InvoiceDtlDM savedtl : (Collection<InvoiceDtlDM>)detailList) {
		 
		 InvoiceDtlDM billDetail = new InvoiceDtlDM();
		 billDetail.setInvoiceDetailNo(savedtl.getInvoiceDetailNo());
		 billDetail.setInvoiceNo(invoiceHdrId);
		 billDetail.setProjectBillId(savedtl.getProjectBillId());
		 billDetail.setBasicValue(savedtl.getBasicValue());
		 billDetail.setBillAmount(savedtl.getBillAmount());
		 billDetail.setInvoiceDate(savedtl.getInvoiceDate());
		 billDetail.setItemDesc(savedtl.getItemDesc());
		 billDetail.setItemQuantity(savedtl.getItemQuantity());
		
		 if(invoiceHdrId!=null) {
             servInvoiceDetailBean.saveOrUpdateInvoicedetail(billDetail);
			 populateAndConfigureInvoiceDetailTable(null);
			 resetFieldsDetail();
			 btnAddDtl.setCaption("Add");
		 }
		
	 }
	} catch(Exception e) {
		e.printStackTrace();
	}
 }
 
 private void getCalcInvoice()
 {
	 try
	 {
	 
	 BigDecimal basicTot = new BigDecimal(tfInvoiceAmount.getValue().toString());
	 
	 BigDecimal st = new BigDecimal(tfST.getValue().toString());
	 BigDecimal stValCalc = basicTot.multiply(st.divide(new BigDecimal(100)));
		tfSTValue.setValue(stValCalc.toString());
		
		
		 BigDecimal other = new BigDecimal(tfOther.getValue().toString());
		 BigDecimal otherValCalc = basicTot.multiply(other.divide(new BigDecimal(100)));
		 tfOtherValue.setValue(otherValCalc.toString());
		 
		 BigDecimal discount = new BigDecimal(tfDiscountValue.getValue().toString());
		 
		 BigDecimal invtotal = basicTot.add(stValCalc).add(otherValCalc).subtract(discount);
		 tfInvoiceTotal.setReadOnly(false);
		 tfInvoiceTotal.setValue(invtotal.toString());
		 tfInvoiceTotal.setReadOnly(true);
	 
	 
	 } catch(Exception e) {
		 
	 }
	
 }
    /*
	 * 
	 * this function handles component error when Project Name,Client Name,Invoice Date did not selects.
	 * 
	 */
 private void saveComponenterror() {
	 cbProjectName.setComponentError(null);
	 cbClientName.setComponentError(null);
	 dfInvoiceDt.setComponentError(null);
		if( cbProjectName.getValue()==null || cbProjectName.getValue().toString().trim().length()==0)
		{
			cbProjectName.setComponentError(new UserError("Select Project Name "));
		}
		if( cbClientName.getValue()==null || cbClientName.getValue().toString().trim().length()==0)
		{
			cbClientName.setComponentError(new UserError("Select Client Name "));
		}		
		if( dfInvoiceDt.getValue()==null || dfInvoiceDt.getValue().toString().trim().length()==0)
		{
			dfInvoiceDt.setComponentError(new UserError("Select Invoice Date "));
		}		
	}
 /*
	 * 
	 * this function handles component error when InvoiceDetail Date,Item Description did not selects.
	 * 
	 */
private void saveComponentErrorDetail() {
	 dfInvoiceDtlDate.setComponentError(null);
	 taItemDesc.setComponentError(null);
		if( dfInvoiceDtlDate.getValue()==null || dfInvoiceDtlDate.getValue().toString().trim().length()==0)
		{
			dfInvoiceDtlDate.setComponentError(new UserError("Select InvoiceDetail Date "));
		}
		if( taItemDesc.getValue()==null || taItemDesc.getValue().toString().trim().length()==0)
		{
			taItemDesc.setComponentError(new UserError("Enter Item Description "));
		}		
	}
    /*
	 * 
	 * this function handles button click event
	 * 
	 * @param ClickEvent event
	 */
    @Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if(btnAdd==event.getButton()) {
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			vlTablePanel.setVisible(false);
			btnAdd.setEnabled(false);
			hlSaveBtn.setVisible(true);
			tempSaveDetail.removeAll(tempSaveDetail);
			resetFields();
			resetFieldsDetail();
			invoiceHdrId=servInvoiceheaderBean.getNextSequence();
			populateAndConfigureInvoiceDetailTable(null);
		}
		else if(btnEdit==event.getButton()) {
			btnSave.setCaption("Update");
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			vlTablePanel.setVisible(false);
			hlSaveBtn.setVisible(true);
			lblFormTitle.setValue("&nbsp;&nbsp;<b>"+ screenName+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Search</font> &nbsp;>&nbsp;Modify");
			try {
				editInvoiceHeader();
			} catch(Exception e) {
				e.printStackTrace();
				logger.error("Error thorws in editBranchDetails() function--->" + e);
			}
		}
		else if(btnSave==event.getButton()) {
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			vlTablePanel.setVisible(false);
			saveComponenterror();
			try {
				saveInvoiceHeader();
				saveTempProjectBillDetails();
			} catch(Exception e) {
				e.printStackTrace();
				logger.info("check  saveTimeSheetEntryDetails() function. Time Sheet datas did not saved properly--->" + e);
			}
		}
		else if(btnCancel==event.getButton()) {
			vlMainPanel.setVisible(false);
			vlSearchPanel.setVisible(true);
			vlTablePanel.setVisible(true);
			populateAndConfigureTable(false);
			 resetFields();
			 resetFieldsDetail();
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			
		btnSave.setCaption("Save");
		lblFormTitle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Search");
		hlSaveBtn.setVisible(false);
		}
		else if(btnSearch==event.getButton()) {
			populateAndConfigureTable(true);
			resetFields();
			 lblFormTitle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Search");
		}
		else if(btnReset==event.getButton()) {		
			cbSearchStatus.setValue(null);
			cbSearchProject.setValue(null);
			searchProjectId = null;
			populateAndConfigureTable(false);
			 resetFields();
			 btnAdd.setEnabled(true);
			 btnEdit.setEnabled(false);
		}
		
		else if(btnAddDtl == event.getButton()) {
			saveComponentErrorDetail();
			try {
			loadTempInvoiceDetail();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		else if(btnEditDtl == event.getButton()) {
			try {
			editInvoiceDetails();
			} catch(Exception e) {
				e.printStackTrace();
			}
			btnAddDtl.setEnabled(true);
			btnAddDtl.setCaption("Update");
		}
	}
						

    
    
}


