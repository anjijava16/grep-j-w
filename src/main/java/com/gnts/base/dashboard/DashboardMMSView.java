package com.gnts.base.dashboard;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
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
	private MaterialStockService servicematerialstock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private MmsEnqHdrService serviceMmsEnqHdr = (MmsEnqHdrService) SpringContextHelper.getBean("MmsEnqHdr");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private POHdrService servicepohdr = (POHdrService) SpringContextHelper.getBean("pohdr");
	private Logger logger = Logger.getLogger(DashboardMMSView.class);
	private Table tblMstScrSrchRslt = new Table();
	private Table tblPaymentPending=new Table();
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
		btnAddMaterial.setHtmlContentAllowed(true);
		custom.addComponent(btnEnquiryCount, "enquiry");
		custom.addComponent(btnQuotationCount, "quotation");
		custom.addComponent(btnOrdersCount, "purchaseorder");
		custom.addComponent(btnReceiptsCount, "receipts");
		custom.addComponent(btnBillsCount, "vendorbills");
		custom.addComponent(tblMstScrSrchRslt, "stockDetails");
		custom.addComponent(tblEnquiry, "enquirytable");
		custom.addComponent(btnAddMaterial, "addmaterial");
		tblMstScrSrchRslt.setHeight("300px");
		tblEnquiry.setHeight("250px");
		tblPaymentPending.setHeight("300px");
		loadStockDetails();
		loadEnquiryList();
	}
	
	private void loadStockDetails() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<MaterialStockDM> materiallist = new ArrayList<MaterialStockDM>();
			materiallist = servicematerialstock.getMaterialStockList(null, companyId, null, null, null, null, "F");
			BeanItemContainer<MaterialStockDM> beanmaterialstock = new BeanItemContainer<MaterialStockDM>(
					MaterialStockDM.class);
			beanmaterialstock.addAll(materiallist);
			tblMstScrSrchRslt.setContainerDataSource(beanmaterialstock);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "materialName", "stockType", "materialUOM",
					"currentStock", "effectiveStock" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Material", "Stock Type", "UOM", "Curr. Stock",
					"Eff. Stock" });
			tblMstScrSrchRslt.setColumnFooter("effectiveStock", "No.of.Records :" + materiallist.size());
			tblMstScrSrchRslt.setColumnWidth("materialName", 160);
			tblMstScrSrchRslt.setColumnWidth("currentStock", 75);
			tblMstScrSrchRslt.setColumnWidth("effectiveStock", 75);
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
		List<MmsEnqHdrDM> mmsPurEnqHdrList = new ArrayList<MmsEnqHdrDM>();
		mmsPurEnqHdrList = serviceMmsEnqHdr.getMmsEnqHdrList(companyId, null, null, null, null, "P");
		BeanItemContainer<MmsEnqHdrDM> beanMmsEnqHdrDM = new BeanItemContainer<MmsEnqHdrDM>(MmsEnqHdrDM.class);
		beanMmsEnqHdrDM.addAll(mmsPurEnqHdrList);
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
		System.out.println("ddddddddd");
		List<POHdrDM> pohdrlist = new ArrayList<POHdrDM>();
		String poType = null;
		pohdrlist = servicepohdr.getPOHdrList(companyId, null, null, null,
				null, poType,"P");
		BeanItemContainer<POHdrDM> beanpohdr = new BeanItemContainer<POHdrDM>(POHdrDM.class);
		beanpohdr.addAll(pohdrlist);
		tblPaymentPending.setContainerDataSource(beanpohdr);
		tblPaymentPending.setVisibleColumns(new Object[] { "poId", "branchName", "pOType", "paymentTerms", "pOStatus",
				"lastUpdatedDt", "lastUpdatedBy" });
		tblPaymentPending.setColumnHeaders(new String[] { "Ref.Id", "Branch", "Po Type", "Payment Terms", "Status",
				"Last Updated Date", "Last Updated By" });
		tblPaymentPending.setColumnAlignment("poId", Align.RIGHT);
		tblPaymentPending.setColumnFooter("lastUpdatedBy", "No.of Records : " + pohdrlist.size());
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnEnquiryCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Enquiry");
			new MaterialEnquiry();
		}
		if (event.getButton() == btnQuotationCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Quotation");
			new MaterialQuote();
		}
		if (event.getButton() == btnOrdersCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Purchase Orders");
			new MmsPurchaseOrder();
		}
		if (event.getButton() == btnBillsCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Vendor Bills");
			new MaterialVendorBill();
		}
		if (event.getButton() == btnReceiptsCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Receipts");
			new POMMSReceipts();
		}
		if (event.getButton() == btnAddMaterial) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material");
			UI.getCurrent().getSession().setAttribute("moduleId",9L);
			new Material();
		}
	}
}
