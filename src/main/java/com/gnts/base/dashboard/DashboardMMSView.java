package com.gnts.base.dashboard;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import org.apache.log4j.Logger;
import com.gnts.base.mst.Vendor;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.domain.txn.MmsEnqHdrDM;
import com.gnts.mms.domain.txn.POHdrDM;
import com.gnts.mms.mst.Material;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.MaterialStockService;
import com.gnts.mms.service.txn.MmsEnqHdrService;
import com.gnts.mms.service.txn.POHdrService;
import com.gnts.mms.txn.MaterialEnquiry;
import com.gnts.mms.txn.MaterialQuote;
import com.gnts.mms.txn.MaterialVendorBill;
import com.gnts.mms.txn.MmsPurchaseOrder;
import com.gnts.mms.txn.POMMSReceipts;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class DashboardMMSView implements ClickListener {
	private static final long serialVersionUID = 1L;
	private Label lblDashboardTitle;
	private VerticalLayout clMainLayout;
	private HorizontalLayout hlHeader;
	private Button btnEnquiryCount = new Button("11 Nos.", this);
	private Button btnQuotationCount = new Button("15 Nos.", this);
	private Button btnOrdersCount = new Button("13 Nos.", this);
	private Button btnBillsCount = new Button("17 Nos.", this);
	private Button btnReceiptsCount = new Button("16 Nos.", this);
	private Button btnAddMaterial = new Button("+  Add Material", this);
	private Button btnAddVendor=new Button("+ Add Vendor",this);
	private MaterialStockService servicematerialstock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private MmsEnqHdrService serviceMmsEnqHdr = (MmsEnqHdrService) SpringContextHelper.getBean("MmsEnqHdr");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private POHdrService servicepohdr = (POHdrService) SpringContextHelper.getBean("pohdr");
	private Logger logger = Logger.getLogger(DashboardMMSView.class);
	private Table tblMstScrSrchRslt = new Table();
	private Table tblPaymentPending = new Table();
	private Table tblDeliveryPending = new Table();
	private Table tblEnquiry = new Table();
	private Long companyId;
	
	public DashboardMMSView() {
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		try {
			companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("logincompanyId").toString());
		}
		catch (Exception e) {
		}
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		hlHeader.removeAllComponents();
		CustomLayout custom = new CustomLayout("mmsdashboard");
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Material Management Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		clMainLayout.addComponent(custom);
		btnEnquiryCount.setStyleName(Runo.BUTTON_LINK);
		btnQuotationCount.setStyleName(Runo.BUTTON_LINK);
		btnOrdersCount.setStyleName(Runo.BUTTON_LINK);
		btnBillsCount.setStyleName(Runo.BUTTON_LINK);
		btnReceiptsCount.setStyleName(Runo.BUTTON_LINK);
		btnAddMaterial.setStyleName(Runo.BUTTON_LINK);
		btnAddVendor.setStyleName(Runo.BUTTON_LINK);
		btnAddMaterial.setHtmlContentAllowed(true);
		custom.addComponent(btnEnquiryCount, "enquiry");
		custom.addComponent(btnQuotationCount, "quotation");
		custom.addComponent(btnOrdersCount, "purchaseorder");
		custom.addComponent(btnReceiptsCount, "receipts");
		custom.addComponent(btnBillsCount, "vendorbills");
		custom.addComponent(tblMstScrSrchRslt, "stockDetails");
		custom.addComponent(tblEnquiry, "enquirytable");
		custom.addComponent(btnAddMaterial, "addmaterial");
		custom.addComponent(tblPaymentPending, "paymenttable");
		custom.addComponent(tblDeliveryPending, "deliverypending");
		custom.addComponent(btnAddVendor, "addVendor");
		
		tblMstScrSrchRslt.setHeight("300px");
		tblEnquiry.setHeight("250px");
		tblPaymentPending.setHeight("450px");
		tblPaymentPending.setWidth("510px");
		tblDeliveryPending.setWidth("510px");
		tblDeliveryPending.setHeight("450px");
		loadStockDetails();
		loadEnquiryList();
		loadPaymentPendingDetails();
		loadDeliveryDetails();
	}
	
	private void loadStockDetails() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			BeanItemContainer<MaterialStockDM> beanmaterialstock = new BeanItemContainer<MaterialStockDM>(
					MaterialStockDM.class);
			beanmaterialstock.addAll(servicematerialstock.getMaterialStockList(null, companyId, null, null, null, null,
					"F"));
			tblMstScrSrchRslt.setContainerDataSource(beanmaterialstock);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "materialName", "stockType",
					"currentStock","parkedStock", "effectiveStock" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Material", "Stock Type","Curr. Stock","Parked",
					"Eff. Stock" });
			tblMstScrSrchRslt.setColumnWidth("materialName", 150);
			tblMstScrSrchRslt.setColumnWidth("currentStock", 75);
			tblMstScrSrchRslt.setColumnWidth("effectiveStock", 70);
			tblMstScrSrchRslt.setColumnWidth("parkedStock", 70);
			tblMstScrSrchRslt.setColumnWidth("stockType", 70);
			tblMstScrSrchRslt.setHeightUndefined();
			tblMstScrSrchRslt.addGeneratedColumn("materialName", new ColumnGenerator() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					@SuppressWarnings("unchecked")
					BeanItem<MaterialStockDM> item = (BeanItem<MaterialStockDM>) source.getItem(itemId);
					MaterialStockDM emp = (MaterialStockDM) item.getBean();
					MaterialDM material = serviceMaterial.getMaterialList(emp.getMaterialId(), null, null, null, null,
							null, null, null, null, "P").get(0);
					System.out.println("material.getReorderLevel()--->" + material.getReorderLevel());
					if (material.getReorderLevel() == null || material.getReorderLevel() == emp.getEffectiveStock()) {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#EC9E20;font-size:12px'>"
										+ emp.getMaterialName() + "</h1>", ContentMode.HTML);
					} else if (material.getReorderLevel() > emp.getEffectiveStock()) {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#E26666;font-size:12px'>"
										+ emp.getMaterialName() + "</h1>", ContentMode.HTML);
					} else if (material.getReorderLevel() < emp.getEffectiveStock()) {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#6CD4BD;font-size:12px'>"
										+ emp.getMaterialName() + "</h1>", ContentMode.HTML);
					} else {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#E26666;font-size:12px'>"
										+ emp.getMaterialName() + "</h1>", ContentMode.HTML);
					}
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("loadSrchRslt-->" + e);
		}
	}
	
	// Load Purchase Header
	private void loadEnquiryList() {
		logger.info("Company ID : " + companyId + " | User Name :  > " + "Loading Search...");
		tblEnquiry.removeAllItems();
		BeanItemContainer<MmsEnqHdrDM> beanMmsEnqHdrDM = new BeanItemContainer<MmsEnqHdrDM>(MmsEnqHdrDM.class);
		beanMmsEnqHdrDM.addAll(serviceMmsEnqHdr.getMmsEnqHdrList(companyId, null, null, null, null, "P"));
		tblEnquiry.setContainerDataSource(beanMmsEnqHdrDM);
		tblEnquiry.setVisibleColumns(new Object[] { "enquiryNo", "enquiryStatus" });
		tblEnquiry.setColumnHeaders(new String[] { "Enquiry No", "Status" });
		tblEnquiry.setColumnWidth("enquiryNo", 160);
		tblEnquiry.addGeneratedColumn("enquiryStatus", new ColumnGenerator() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				@SuppressWarnings("unchecked")
				BeanItem<MmsEnqHdrDM> item = (BeanItem<MmsEnqHdrDM>) source.getItem(itemId);
				MmsEnqHdrDM emp = (MmsEnqHdrDM) item.getBean();
				System.out.println("emp.getEnquiryStatus()--->" + emp.getEnquiryStatus());
				if (emp.getEnquiryStatus() == null) {
					return new Label(
							"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#EC9E20;font-size:12px'>"
									+ "---" + "</h1>", ContentMode.HTML);
				} else if (emp.getEnquiryStatus().equals("Pending")) {
					return new Label(
							"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#E26666;font-size:12px'>"
									+ emp.getEnquiryStatus() + "</h1>", ContentMode.HTML);
				} else if (emp.getEnquiryStatus().equals("Approved")) {
					return new Label(
							"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#6CD4BD;font-size:12px'>"
									+ emp.getEnquiryStatus() + "</h1>", ContentMode.HTML);
				} else if (emp.getEnquiryStatus().equals("Progress")) {
					return new Label(
							"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#A8EDFF;font-size:12px'>"
									+ emp.getEnquiryStatus() + "</h1>", ContentMode.HTML);
				} else {
					return new Label(
							"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#E26666;font-size:12px'>"
									+ emp.getEnquiryStatus() + "</h1>", ContentMode.HTML);
				}
			}
		});
	}
	
	private void loadPaymentPendingDetails() {
		tblPaymentPending.removeAllItems();
		BeanItemContainer<POHdrDM> beanpohdr = new BeanItemContainer<POHdrDM>(POHdrDM.class);
		beanpohdr.addAll(servicepohdr.getPOHdrList(companyId, null, null, null, null, null, "P"));
		tblPaymentPending.setContainerDataSource(beanpohdr);
		tblPaymentPending.setVisibleColumns(new Object[] { "pono", "vendorName", "balancePayAmount" });
		tblPaymentPending.setColumnHeaders(new String[] { "PO Number", "Vendor Name", "Balance Amount(Rs.)" });
		tblPaymentPending.setColumnWidth("pono", 150);
		tblPaymentPending.setColumnWidth("vendorName", 150);
		tblPaymentPending.setColumnAlignment("balancePayAmount", Align.RIGHT);
		tblPaymentPending.addGeneratedColumn("balancePayAmount", new ColumnGenerator() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				@SuppressWarnings("unchecked")
				BeanItem<POHdrDM> item = (BeanItem<POHdrDM>) source.getItem(itemId);
				POHdrDM emp = (POHdrDM) item.getBean();
				System.out.println("emp.getBalancePayAmount()--->" + emp.getBalancePayAmount());
				DecimalFormat df = new DecimalFormat("#.00", new DecimalFormatSymbols());
				if (emp.getBalancePayAmount().compareTo(new BigDecimal("5000")) > 0) {
					return new Label("<p style='color:#EC9E20;font-size:14px;align=right'>"
							+ df.format(emp.getBalancePayAmount().doubleValue()) + "</p>", ContentMode.HTML);
				} else {
					return new Label("<p style='color:#E26666;font-size:14px;align=right'>"
							+ df.format(emp.getBalancePayAmount().doubleValue()) + "</p>", ContentMode.HTML);
				}
			}
		});
	}
	
	private void loadDeliveryDetails() {
		tblDeliveryPending.removeAllItems();
		BeanItemContainer<POHdrDM> beanpohdr = new BeanItemContainer<POHdrDM>(POHdrDM.class);
		beanpohdr.addAll(servicepohdr.getPOHdrList(companyId, null, null, null, null, null, "P"));
		tblDeliveryPending.setContainerDataSource(beanpohdr);
		tblDeliveryPending.setVisibleColumns(new Object[] { "pono", "vendorName", "expDate" });
		tblDeliveryPending.setColumnHeaders(new String[] { "PO Number", "Vendor Name", "Delivery Date" });
		tblDeliveryPending.setColumnWidth("pono", 150);
		tblDeliveryPending.setColumnWidth("vendorName", 150);
		tblDeliveryPending.addGeneratedColumn("expDate", new ColumnGenerator() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				@SuppressWarnings("unchecked")
				BeanItem<POHdrDM> item = (BeanItem<POHdrDM>) source.getItem(itemId);
				POHdrDM emp = (POHdrDM) item.getBean();
				if (emp.getExpDate1().after(new Date())) {
					return new Label(
							"<p style='color:#6CD4BD;font-size:14px;align=right'>" + emp.getExpDate() + "</p>",
							ContentMode.HTML);
				} else {
					return new Label(
							"<p style='color:#E26666;font-size:14px;align=right'>" + emp.getExpDate() + "</p>",
							ContentMode.HTML);
				}
			}
		});
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnEnquiryCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Enquiry");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new MaterialEnquiry();
		}
		if (event.getButton() == btnQuotationCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Quotation");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new MaterialQuote();
		}
		if (event.getButton() == btnOrdersCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Purchase Orders");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new MmsPurchaseOrder();
		}
		if (event.getButton() == btnBillsCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Vendor Bills");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new MaterialVendorBill();
		}
		if (event.getButton() == btnReceiptsCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Receipts");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new POMMSReceipts();
		}
		if (event.getButton() == btnAddMaterial) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new Material();
		}
		if (event.getButton() == btnAddVendor) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Vendor");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new Vendor();
		}
	}
}
