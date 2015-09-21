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
import com.gnts.mms.txn.LetterofIntent;
import com.gnts.mms.txn.MaterialEnquiry;
import com.gnts.mms.txn.MaterialQuote;
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
	private Button btnLOI = new Button("17 Nos.", this);
	private Button btnReceiptsCount = new Button("16 Nos.", this);
	private Button btnAddMaterial = new Button("+  Add Material", this);
	private Button btnAddVendor = new Button("+ Add Vendor", this);
	private Button btnShowHide = new Button("Show/Hide", this);
	private MaterialStockService serviceMatStock = (MaterialStockService) SpringContextHelper.getBean("materialstock");
	private MmsEnqHdrService serviceMmsEnqHdr = (MmsEnqHdrService) SpringContextHelper.getBean("MmsEnqHdr");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private POHdrService servicePOHdr = (POHdrService) SpringContextHelper.getBean("pohdr");
	private Logger logger = Logger.getLogger(DashboardMMSView.class);
	private Table tblMaterialStock = new Table();
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
		btnLOI.setStyleName(Runo.BUTTON_LINK);
		btnReceiptsCount.setStyleName(Runo.BUTTON_LINK);
		btnAddMaterial.setStyleName(Runo.BUTTON_LINK);
		btnAddVendor.setStyleName(Runo.BUTTON_LINK);
		btnShowHide.setStyleName(Runo.BUTTON_LINK);
		btnAddMaterial.setHtmlContentAllowed(true);
		custom.addComponent(btnEnquiryCount, "enquiry");
		custom.addComponent(btnQuotationCount, "quotation");
		custom.addComponent(btnOrdersCount, "purchaseorder");
		custom.addComponent(btnReceiptsCount, "receipts");
		custom.addComponent(btnLOI, "loipur");
		custom.addComponent(tblMaterialStock, "stockDetails");
		custom.addComponent(tblEnquiry, "enquirytable");
		custom.addComponent(btnAddMaterial, "addmaterial");
		custom.addComponent(tblPaymentPending, "paymenttable");
		custom.addComponent(tblDeliveryPending, "deliverypending");
		custom.addComponent(btnAddVendor, "addVendor");
		custom.addComponent(btnShowHide, "showhide");
		tblMaterialStock.setHeight("300px");
		tblEnquiry.setHeight("250px");
		tblPaymentPending.setHeight("450px");
		tblPaymentPending.setWidth("510px");
		tblDeliveryPending.setWidth("510px");
		tblDeliveryPending.setHeight("450px");
		loadStockDetails();
		loadEnquiryList();
		loadPaymentPendingDetails();
		loadDeliveryDetails();
		tblPaymentPending.setVisible(false);
		tblDeliveryPending.setVisible(false);
	}
	
	private void loadStockDetails() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : > " + "Loading Search...");
			tblMaterialStock.removeAllItems();
			BeanItemContainer<MaterialStockDM> beanmaterialstock = new BeanItemContainer<MaterialStockDM>(
					MaterialStockDM.class);
			beanmaterialstock
					.addAll(serviceMatStock.getMaterialStockList(null, companyId, null, null, null, null, "F"));
			tblMaterialStock.setContainerDataSource(beanmaterialstock);
			tblMaterialStock.setVisibleColumns(new Object[] { "materialName", "stockType", "currentStock",
					"parkedStock", "effectiveStock" });
			tblMaterialStock.setColumnHeaders(new String[] { "Material", "Stock Type", "Curr. Stock", "Parked",
					"Eff. Stock" });
			tblMaterialStock.setColumnWidth("materialName", 150);
			tblMaterialStock.setColumnWidth("currentStock", 75);
			tblMaterialStock.setColumnWidth("effectiveStock", 70);
			tblMaterialStock.setColumnWidth("parkedStock", 70);
			tblMaterialStock.setColumnWidth("stockType", 70);
			tblMaterialStock.addGeneratedColumn("materialName", new ColumnGenerator() {
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
			logger.info(e.getMessage());
		}
	}
	
	// Load Purchase Header
	private void loadEnquiryList() {
		try {
			logger.info("Company ID : " + companyId + " | User Name :  > " + "Loading Search...");
			tblEnquiry.removeAllItems();
			BeanItemContainer<MmsEnqHdrDM> beanMmsEnqHdrDM = new BeanItemContainer<MmsEnqHdrDM>(MmsEnqHdrDM.class);
			beanMmsEnqHdrDM.addAll(serviceMmsEnqHdr.getMmsEnqHdrList(companyId, null, null, null, null, "P"));
			tblEnquiry.setContainerDataSource(beanMmsEnqHdrDM);
			tblEnquiry.setVisibleColumns(new Object[] { "enquiryNo", "enquiryStatus", "enqRemark" });
			tblEnquiry.setColumnHeaders(new String[] { "Enquiry No", "Status", "Remarks" });
			tblEnquiry.setColumnWidth("enquiryNo", 160);
			tblEnquiry.setColumnWidth("enquiryStatus", 120);
			tblEnquiry.setColumnWidth("enqRemark", 160);
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
			tblEnquiry.addGeneratedColumn("enquiryNo", new ColumnGenerator() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					@SuppressWarnings("unchecked")
					BeanItem<MmsEnqHdrDM> item = (BeanItem<MmsEnqHdrDM>) source.getItem(itemId);
					final MmsEnqHdrDM enqHdrDM = (MmsEnqHdrDM) item.getBean();
					HorizontalLayout hlLayout = new HorizontalLayout();
					Button btnView = new Button("View");
					btnView.addClickListener(new ClickListener() {
						private static final long serialVersionUID = 1L;
						
						@Override
						public void buttonClick(ClickEvent event) {
							// TODO Auto-generated method stub
							clMainLayout.removeAllComponents();
							hlHeader.removeAllComponents();
							UI.getCurrent().getSession().setAttribute("screenName", "Material Enquiry");
							UI.getCurrent().getSession().setAttribute("moduleId", 9L);
							new MaterialEnquiry(enqHdrDM.getEnquiryId());
						}
					});
					btnView.setStyleName("view");
					hlLayout.addComponent(btnView);
					if (enqHdrDM.getEnquiryNo() != null) {
						hlLayout.addComponent(new Label(enqHdrDM.getEnquiryNo()));
					} else {
						hlLayout.addComponent(new Label("------------"));
					}
					return hlLayout;
				}
			});
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadPaymentPendingDetails() {
		try {
			tblPaymentPending.removeAllItems();
			BeanItemContainer<POHdrDM> beanpohdr = new BeanItemContainer<POHdrDM>(POHdrDM.class);
			beanpohdr.addAll(servicePOHdr.getPOHdrList(companyId, null, null, null, null, null, null, "P"));
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
					if (emp.getBalancePayAmount() != null) {
						DecimalFormat df = new DecimalFormat("#.00", new DecimalFormatSymbols());
						if (emp.getBalancePayAmount().compareTo(new BigDecimal("5000")) > 0) {
							return new Label("<p style='color:#EC9E20;font-size:14px;align=right'>"
									+ df.format(emp.getBalancePayAmount().doubleValue()) + "</p>", ContentMode.HTML);
						} else {
							return new Label("<p style='color:#E26666;font-size:14px;align=right'>"
									+ df.format(emp.getBalancePayAmount().doubleValue()) + "</p>", ContentMode.HTML);
						}
					}
					return new Label("<p style='color:#EC9E20;font-size:14px;align=right'>0.00</p>", ContentMode.HTML);
				}
			});
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadDeliveryDetails() {
		try {
			tblDeliveryPending.removeAllItems();
			BeanItemContainer<POHdrDM> beanpohdr = new BeanItemContainer<POHdrDM>(POHdrDM.class);
			beanpohdr.addAll(servicePOHdr.getPOHdrList(companyId, null, null, null, null, null, null, "P"));
			tblDeliveryPending.setContainerDataSource(beanpohdr);
			tblDeliveryPending.setVisibleColumns(new Object[] { "pono", "vendorName", "expDate" });
			tblDeliveryPending.setColumnHeaders(new String[] { "PO Number", "Vendor Name", "Delivery Date" });
			tblDeliveryPending.setColumnWidth("pono", 135);
			tblDeliveryPending.setColumnWidth("vendorName", 135);
			tblDeliveryPending.addGeneratedColumn("expDate", new ColumnGenerator() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					@SuppressWarnings("unchecked")
					BeanItem<POHdrDM> item = (BeanItem<POHdrDM>) source.getItem(itemId);
					final POHdrDM poHdrDM = (POHdrDM) item.getBean();
					HorizontalLayout hlLayout = new HorizontalLayout();
					Button btnView = new Button("View");
					btnView.addClickListener(new ClickListener() {
						private static final long serialVersionUID = 1L;
						
						@Override
						public void buttonClick(ClickEvent event) {
							// TODO Auto-generated method stub
							clMainLayout.removeAllComponents();
							hlHeader.removeAllComponents();
							UI.getCurrent().getSession().setAttribute("screenName", "Material Purchase Orders");
							UI.getCurrent().getSession().setAttribute("moduleId", 9L);
							new MmsPurchaseOrder(poHdrDM.getPoId());
						}
					});
					btnView.setStyleName("view");
					if (poHdrDM.getExpDate1().after(new Date())) {
						hlLayout.addComponent(new Label("<p style='color:#6CD4BD;font-size:14px;align=right'>"
								+ poHdrDM.getExpDate() + "</p>", ContentMode.HTML));
					} else {
						hlLayout.addComponent(new Label("<p style='color:#E26666;font-size:14px;align=right'>"
								+ poHdrDM.getExpDate() + "</p>", ContentMode.HTML));
					}
					hlLayout.setSpacing(true);
					hlLayout.addComponent(btnView);
					hlLayout.setComponentAlignment(btnView, Alignment.MIDDLE_RIGHT);
					return hlLayout;
				}
			});
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnEnquiryCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Enquiry");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new MaterialEnquiry();
		} else if (event.getButton() == btnQuotationCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Quotation");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new MaterialQuote();
		} else if (event.getButton() == btnOrdersCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Purchase Orders");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new MmsPurchaseOrder();
		} else if (event.getButton() == btnLOI) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Letter of Intent");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new LetterofIntent();
		} else if (event.getButton() == btnReceiptsCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Receipts");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new POMMSReceipts();
		} else if (event.getButton() == btnAddMaterial) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new Material();
		} else if (event.getButton() == btnAddVendor) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Vendor");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new Vendor();
		} else if (event.getButton() == btnShowHide) {
			if (tblPaymentPending.isVisible()) {
				tblPaymentPending.setVisible(false);
				tblDeliveryPending.setVisible(false);
			} else {
				tblPaymentPending.setVisible(true);
				tblDeliveryPending.setVisible(true);
			}
		}
	}
}
