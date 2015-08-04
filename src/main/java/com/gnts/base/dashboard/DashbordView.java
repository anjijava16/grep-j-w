package com.gnts.base.dashboard;

import java.util.ArrayList;
import java.util.List;
import com.gnts.base.mst.Product;
import com.gnts.base.service.mst.ProductService;
import com.gnts.crm.mst.Client;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.die.txn.DieRequest;
import com.gnts.dsn.stt.txn.DesignDocuments;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.mfg.txn.WorkOrder;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsInvoiceHdrService;
import com.gnts.sms.service.txn.SmsPOHdrService;
import com.gnts.sms.service.txn.SmsQuoteHdrService;
import com.gnts.sms.txn.SalesPO;
import com.gnts.sms.txn.SalesQuote;
import com.gnts.sms.txn.SmsEnquiry;
import com.gnts.sms.txn.SmsInvoice;
import com.gnts.stt.dsn.domain.txn.ECRequestDM;
import com.gnts.stt.dsn.service.txn.ECRequestService;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class DashbordView implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long companyId;
	private Label lblDashboardTitle;
	private Button btnEnquiryCount = new Button("100", this);
	private Button btnQuotationCount = new Button("125", this);
	private Button btnPOCount = new Button("100", this);
	private Button btnInvoiceCount = new Button("55", this);
	private Button btnWOCount = new Button("7", this);
	private Button btnProductCount = new Button("17", this);
	private Button btnClientCount = new Button("22", this);
	private Button btnEnquiryDocs = new Button("Documents", this);
	private Button btnDieRequest = new Button("10", this);
	private Button btnNotify = new Button();
	private Window notificationsWindow;
	private SmsEnqHdrService serviceenqhdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private SmsQuoteHdrService servicesmsQuoteHdr = (SmsQuoteHdrService) SpringContextHelper.getBean("smsquotehdr");
	private SmsPOHdrService servicePurchaseOrd = (SmsPOHdrService) SpringContextHelper.getBean("smspohdr");
	private WorkOrderHdrService serviceWrkOrdHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private SmsInvoiceHdrService serviceInvoiceHdr = (SmsInvoiceHdrService) SpringContextHelper
			.getBean("smsInvoiceheader");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private ProductService ServiceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private ECRequestService ServiceEcrequest = (ECRequestService) SpringContextHelper.getBean("ecRequest");
	VerticalLayout clMainLayout;
	HorizontalLayout hlHeader;
	private Table tblWorkorderStatus = new Table();
	
	public DashbordView() {
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		btnNotify.setIcon(new ThemeResource("img/download.png"));
		hlHeader.removeAllComponents();
		CustomLayout custom = new CustomLayout("dashmarket");
		btnEnquiryCount.setCaption(serviceenqhdr.getSMSEnquiryListCount(null, null, null, null, null, null, null, null)
				.toString());
		btnQuotationCount.setCaption(servicesmsQuoteHdr.getSMSQuoteCount(null, null, null, null, null, null, null)
				.toString());
		btnPOCount.setCaption(servicePurchaseOrd.getSMSPOListCount(null, null, companyId, null, null, null, null, null)
				.toString());
		btnWOCount.setCaption(serviceWrkOrdHdr
				.getWorkOrderHDRcount(null, null, companyId, null, null, null, null, null).toString());
		btnInvoiceCount.setCaption(serviceInvoiceHdr.getSmsInvoiceHeadercount(null, null, companyId, null, null, null,
				null, null).toString());
		btnClientCount.setCaption(serviceClients.getClientDetailscount(companyId, null, "Active", null).toString());
		btnProductCount.setCaption(ServiceProduct.getProductscount(companyId, null, "Active", null).toString());
		// btnEnquiryCount.setStyleName(Runo.BUTTON_LINK);
		btnEnquiryCount.setStyleName("borderless-colored");
		btnQuotationCount.setStyleName("borderless-colored");
		btnPOCount.setStyleName("borderless-colored");
		btnInvoiceCount.setStyleName("borderless-colored");
		btnWOCount.setStyleName("borderless-colored");
		btnProductCount.setStyleName("borderless-coloredbig");
		btnClientCount.setStyleName("borderless-coloredbig");
		btnEnquiryDocs.setStyleName("borderless-coloredmed");
		btnDieRequest.setStyleName("borderless-coloredbig");
		VerticalLayout root = new VerticalLayout();
		root.addComponent(buildHeader());
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Marketing Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		hlHeader.addComponent(btnNotify);
		hlHeader.setComponentAlignment(btnNotify, Alignment.TOP_RIGHT);
		clMainLayout.addComponent(custom);
		custom.addComponent(btnEnquiryCount, "enquirycount");
		custom.addComponent(btnQuotationCount, "quotationcount");
		custom.addComponent(btnPOCount, "pocount");
		custom.addComponent(btnInvoiceCount, "invoicecount");
		custom.addComponent(btnWOCount, "workordercount");
		custom.addComponent(btnProductCount, "productCount");
		custom.addComponent(btnClientCount, "clientCount");
		custom.addComponent(btnEnquiryDocs, "enquirydocuments");
		custom.addComponent(btnDieRequest, "dieRequest");
		custom.addComponent(new CalendarMonthly("WO_SCHEDULE"), "marketcalender");
		loadWorkOrderStatus();
	}
	
	private Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.addStyleName("viewheader");
		header.setSpacing(true);
		btnNotify = buildNotificationsButton();
		HorizontalLayout tools = new HorizontalLayout(btnNotify);
		tools.setSpacing(true);
		tools.addStyleName("toolbar");
		return header;
	}
	
	private Button buildNotificationsButton() {
		btnNotify.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(final ClickEvent event) {
				openNotificationsPopup(event);
			}
		});
		return btnNotify;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (event.getButton() == btnEnquiryCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Sales Enquiry");
			UI.getCurrent().getSession().setAttribute("IS_ENQ_WF", false);
			UI.getCurrent().getSession().setAttribute("moduleId", 13L);
			new SmsEnquiry();
		}
		if (event.getButton() == btnQuotationCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Sales Quotation");
			UI.getCurrent().getSession().setAttribute("moduleId", 13L);
			new SalesQuote();
		}
		if (event.getButton() == btnPOCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Sales Order");
			UI.getCurrent().getSession().setAttribute("moduleId", 13L);
			new SalesPO();
		}
		if (event.getButton() == btnInvoiceCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Sales Invoice");
			UI.getCurrent().getSession().setAttribute("moduleId", 13L);
			new SmsInvoice();
		}
		if (event.getButton() == btnWOCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Work Order");
			UI.getCurrent().getSession().setAttribute("moduleId", 13L);
			new WorkOrder();
		}
		if (event.getButton() == btnProductCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Product");
			new Product();
		}
		if (event.getButton() == btnClientCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Client");
			new Client();
		}
		if (event.getButton() == btnEnquiryDocs) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Enquiry Docuemnts");
			new DesignDocuments();
		}
		if (event.getButton() == btnDieRequest) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("IS_DIE_ENQ", true);
			UI.getCurrent().getSession().setAttribute("screenName", "Die Request");
			new DieRequest();
		}
	}
	
	private void loadWorkOrderStatus() {
		try {
			tblWorkorderStatus.removeAllItems();
			BeanItemContainer<WorkOrderHdrDM> beanmaterialstock = new BeanItemContainer<WorkOrderHdrDM>(
					WorkOrderHdrDM.class);
			tblWorkorderStatus.setContainerDataSource(beanmaterialstock);
			tblWorkorderStatus.setVisibleColumns(new Object[] { "workOrdrNo", "workOrdrTyp", "workOrdrRmrks",
					"workOrdrSts", "effectiveStock" });
			tblWorkorderStatus.setColumnHeaders(new String[] { "Material", "Stock Type", "UOM", "Curr. Stock",
					"Eff. Stock" });
			tblWorkorderStatus.setColumnWidth("materialName", 160);
			tblWorkorderStatus.setColumnWidth("currentStock", 75);
			tblWorkorderStatus.setColumnWidth("effectiveStock", 75);
			tblWorkorderStatus.addGeneratedColumn("materialName", new ColumnGenerator() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					@SuppressWarnings("unchecked")
					BeanItem<WorkOrderHdrDM> item = (BeanItem<WorkOrderHdrDM>) source.getItem(itemId);
					return new Label((String) item.getItemProperty("workOrdrNo").getValue());
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openNotificationsPopup(final ClickEvent event) {
		VerticalLayout notificationsLayout = new VerticalLayout();
		notificationsLayout.setMargin(true);
		notificationsLayout.setSpacing(true);
		final Panel panel = new Panel("Notifications");
		notificationsLayout.addComponent(panel);
		List<ECRequestDM> ecrequestDm = new ArrayList<ECRequestDM>();
		ecrequestDm = ServiceEcrequest.getECRequestList(null, null, null, "Active");
		FormLayout fmlayout = new FormLayout();
		VerticalLayout hrLayout = new VerticalLayout();
		for (ECRequestDM n : ecrequestDm) {
			hrLayout.addStyleName("notification-item");
			if (n.getPartNumber() != null & n.getDrgNumber() != null) {
				Label titleLabel = new Label("\n"
						+ "<table style=width:100%><tr><td><small>Status : </small><font color=blue><font size=4>"
						+ n.getStatus() + "</font></font color></td><td><small>ECR No : </small><font color=green>"
						+ n.getEcrNumber() + "</font></td></tr></table>", ContentMode.HTML);
				Label titleLabel1 = new Label("<small>Enquiry No: </small><font color=green>" + n.getEnquiryNo()
						+ "</font>", ContentMode.HTML);
				Label titleLabel3 = new Label("<small>Drag No : </small><font color=red>" + n.getDrgNumber()
						+ "</font>", ContentMode.HTML);
				Label titleLabel4 = new Label("<small>Part No  : </small><font color=red>" + n.getPartNumber()
						+ "</font>", ContentMode.HTML);
				Label titleLabel5 = new Label("<HR size=3 color=red>", ContentMode.HTML);
				titleLabel.addStyleName("notification-title");
				fmlayout.addComponents(titleLabel);
				fmlayout.addComponents(titleLabel1);
				// fmlayout.addComponents(titleLabel2);
				fmlayout.addComponent(titleLabel3);
				fmlayout.addComponent(titleLabel4);
				fmlayout.addComponent(titleLabel5);
				hrLayout.addComponent(fmlayout);
			}
		}
		notificationsLayout.addComponent(hrLayout);
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth("100%");
		Button showAll = new Button("View All Notifications", new ClickListener() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(final ClickEvent event) {
				clMainLayout.removeAllComponents();
				hlHeader.removeAllComponents();
				new SmsEnquiry();
				notificationsWindow.close();
			}
		});
		showAll.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		showAll.addStyleName(ValoTheme.BUTTON_SMALL);
		footer.addComponent(showAll);
		footer.setComponentAlignment(showAll, Alignment.TOP_CENTER);
		notificationsLayout.addComponent(footer);
		if (notificationsWindow == null) {
			notificationsWindow = new Window();
			notificationsWindow.setWidthUndefined();
			notificationsWindow.addStyleName("notifications");
			notificationsWindow.setClosable(false);
			notificationsWindow.setResizable(false);
			notificationsWindow.setDraggable(true);
			notificationsWindow.setCloseShortcut(KeyCode.ESCAPE, null);
			notificationsWindow.setContent(notificationsLayout);
			notificationsWindow.setHeightUndefined();
		}
		if (!notificationsWindow.isAttached()) {
			notificationsWindow.setPositionX(event.getClientX() - 200);
			notificationsWindow.setPositionY(event.getClientY());
			notificationsWindow.setHeight("400");
			notificationsWindow.setWidth("300");
			UI.getCurrent().addWindow(notificationsWindow);
			notificationsWindow.focus();
		} else {
			notificationsWindow.close();
		}
	}
}