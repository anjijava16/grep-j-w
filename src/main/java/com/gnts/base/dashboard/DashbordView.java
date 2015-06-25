package com.gnts.base.dashboard;

import com.gnts.base.mst.Product;
import com.gnts.base.rpt.MultipleAxes;
import com.gnts.base.service.mst.ProductService;
import com.gnts.crm.mst.Client;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.dsn.stt.txn.DesignDocuments;
import com.gnts.erputil.helper.SpringContextHelper;
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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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
	private SmsEnqHdrService serviceenqhdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private SmsQuoteHdrService servicesmsQuoteHdr = (SmsQuoteHdrService) SpringContextHelper.getBean("smsquotehdr");
	private SmsPOHdrService servicePurchaseOrd = (SmsPOHdrService) SpringContextHelper.getBean("smspohdr");
	private WorkOrderHdrService serviceWrkOrdHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private SmsInvoiceHdrService serviceInvoiceHdr = (SmsInvoiceHdrService) SpringContextHelper
			.getBean("smsInvoiceheader");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private ProductService ServiceProduct = (ProductService) SpringContextHelper.getBean("Product");
	VerticalLayout clMainLayout;
	HorizontalLayout hlHeader;
	
	public DashbordView() {
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
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
		btnEnquiryDocs.setStyleName("borderless-colored");
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Marketing Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		clMainLayout.addComponent(custom);
		MultipleAxes multipleAxes = new MultipleAxes();
		custom.addComponent(multipleAxes.getChart(), "marketchart");
		custom.addComponent(btnEnquiryCount, "enquirycount");
		custom.addComponent(btnQuotationCount, "quotationcount");
		custom.addComponent(btnPOCount, "pocount");
		custom.addComponent(btnInvoiceCount, "invoicecount");
		custom.addComponent(btnWOCount, "workordercount");
		custom.addComponent(btnProductCount, "productCount");
		custom.addComponent(btnClientCount, "clientCount");
		custom.addComponent(btnEnquiryDocs, "enquirydocuments");
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
	}
}
