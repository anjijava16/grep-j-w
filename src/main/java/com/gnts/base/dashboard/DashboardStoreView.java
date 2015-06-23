package com.gnts.base.dashboard;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.IndentHdrDM;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.domain.txn.POHdrDM;
import com.gnts.mms.mst.Material;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.IndentHdrService;
import com.gnts.mms.service.txn.MaterialLedgerService;
import com.gnts.mms.service.txn.MaterialStockService;
import com.gnts.mms.service.txn.POHdrService;
import com.gnts.mms.txn.Indent;
import com.gnts.mms.txn.IndentIssue;
import com.gnts.mms.txn.IndentIssueReturn;
import com.gnts.mms.txn.MaterialLedger;
import com.gnts.mms.txn.MaterialStock;
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

public class DashboardStoreView implements ClickListener {
	private static final long serialVersionUID = 1L;
	private Label lblDashboardTitle;
	private VerticalLayout clMainLayout;
	private HorizontalLayout hlHeader;
	private Button btnIntentCount = new Button("11 Nos.", this);
	private Button btnIntentIssueCount = new Button("15 Nos.", this);
	private Button btnIntentReturnCount = new Button("13 Nos.", this);
	private Button btnLedgerCount = new Button("17 Nos.", this);
	private Button btnStockCount = new Button("16 Nos.", this);
	private Button btnAddMaterial = new Button("+  Add Material", this);
	private IndentHdrService serviceIndentHdr = (IndentHdrService) SpringContextHelper.getBean("IndentHdr");
	private MaterialStockService servicematerialstock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private MaterialLedgerService serviceledger = (MaterialLedgerService) SpringContextHelper.getBean("materialledger");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private POHdrService servicepohdr = (POHdrService) SpringContextHelper.getBean("pohdr");
	private Logger logger = Logger.getLogger(DashboardStoreView.class);
	private Table tblMstScrSrchRslt = new Table();
	private Table tblMaterialInward = new Table();
	private Table tblMaterialOutward = new Table();
	private Table tblIndent = new Table();
	private Long companyId;
	
	public DashboardStoreView() {
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
		CustomLayout custom = new CustomLayout("storedashboard");
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Inventory Management Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		clMainLayout.addComponent(custom);
		btnIntentCount.setStyleName(Runo.BUTTON_LINK);
		btnIntentIssueCount.setStyleName(Runo.BUTTON_LINK);
		btnIntentReturnCount.setStyleName(Runo.BUTTON_LINK);
		btnLedgerCount.setStyleName(Runo.BUTTON_LINK);
		btnStockCount.setStyleName(Runo.BUTTON_LINK);
		btnAddMaterial.setStyleName(Runo.BUTTON_LINK);
		btnAddMaterial.setHtmlContentAllowed(true);
		custom.addComponent(btnIntentCount, "enquiry");
		custom.addComponent(btnIntentIssueCount, "quotation");
		custom.addComponent(btnIntentReturnCount, "purchaseorder");
		custom.addComponent(btnStockCount, "stock");
		custom.addComponent(btnLedgerCount, "ledger");
		custom.addComponent(tblMstScrSrchRslt, "stockDetails");
		custom.addComponent(tblIndent, "enquirytable");
		custom.addComponent(btnAddMaterial, "addmaterial");
		custom.addComponent(tblMaterialInward, "paymenttable");
		custom.addComponent(tblMaterialOutward, "deliverypending");
		tblMstScrSrchRslt.setHeight("300px");
		tblIndent.setHeight("250px");
		tblMaterialInward.setHeight("450px");
		tblMaterialInward.setWidth("510px");
		tblMaterialOutward.setWidth("510px");
		tblMaterialOutward.setHeight("450px");
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
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "materialName", "stockType", "materialUOM",
					"currentStock", "effectiveStock" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Material", "Stock Type", "UOM", "Curr. Stock",
					"Eff. Stock" });
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
		tblIndent.removeAllItems();
		List<IndentHdrDM> indentHdrList = new ArrayList<IndentHdrDM>();
		indentHdrList = serviceIndentHdr.getMmsIndentHdrList(null, null, null, null, null, null, null, null, "F");
		BeanItemContainer<IndentHdrDM> beanIndentHdrDM = new BeanItemContainer<IndentHdrDM>(IndentHdrDM.class);
		beanIndentHdrDM.addAll(indentHdrList);
		tblIndent.setContainerDataSource(beanIndentHdrDM);
		tblIndent
				.setVisibleColumns(new Object[] { "indentNo", "indentDate", "indentStatus", "empName", "indentRemarks" });
		tblIndent.setColumnHeaders(new String[] { "Indent No", "Date", "Status", "Raised by", "Purpose" });
		tblIndent.setColumnAlignment("indentNo", Align.RIGHT);
		tblIndent.setColumnWidth("indentRemarks", 175);
		tblIndent.addGeneratedColumn("indentStatus", new ColumnGenerator() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				@SuppressWarnings("unchecked")
				BeanItem<IndentHdrDM> item = (BeanItem<IndentHdrDM>) source.getItem(itemId);
				IndentHdrDM emp = (IndentHdrDM) item.getBean();
				if (emp.getIndentStatus().equalsIgnoreCase("Pending")||emp.getIndentStatus().equalsIgnoreCase("Cancelled")) {
					return new Label(
							"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#E26666;font-size:12px'>"
									+ emp.getIndentStatus() + "</h1>", ContentMode.HTML);
				}else if (emp.getIndentStatus().equalsIgnoreCase("Approved")) {
					return new Label(
							"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#6CD4BD;font-size:12px'>"
									+ emp.getIndentStatus() + "</h1>", ContentMode.HTML);
				}
				else{
					return new Label(
							"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#EC9E20;font-size:12px'>"
									+ emp.getIndentStatus() + "</h1>", ContentMode.HTML);
				}
			}
		});
	}
	
	private void loadPaymentPendingDetails() {
		tblMaterialInward.removeAllItems();
		BeanItemContainer<POHdrDM> beanpohdr = new BeanItemContainer<POHdrDM>(POHdrDM.class);
		beanpohdr.addAll(servicepohdr.getPOHdrList(companyId, null, null, null, null, null, "P"));
		tblMaterialInward.setContainerDataSource(beanpohdr);
		tblMaterialInward.setVisibleColumns(new Object[] { "pono", "vendorName", "balancePayAmount" });
		tblMaterialInward.setColumnHeaders(new String[] { "PO Number", "Vendor Name", "Balance Amount(Rs.)" });
		tblMaterialInward.setColumnWidth("pono", 150);
		tblMaterialInward.setColumnWidth("vendorName", 150);
		tblMaterialInward.setColumnAlignment("balancePayAmount", Align.RIGHT);
		tblMaterialInward.addGeneratedColumn("balancePayAmount", new ColumnGenerator() {
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
		tblMaterialOutward.removeAllItems();
		BeanItemContainer<POHdrDM> beanpohdr = new BeanItemContainer<POHdrDM>(POHdrDM.class);
		beanpohdr.addAll(servicepohdr.getPOHdrList(companyId, null, null, null, null, null, "P"));
		tblMaterialOutward.setContainerDataSource(beanpohdr);
		tblMaterialOutward.setVisibleColumns(new Object[] { "pono", "vendorName", "balancePayAmount" });
		tblMaterialOutward.setColumnHeaders(new String[] { "PO Number", "Vendor Name", "Balance Amount(Rs.)" });
		tblMaterialOutward.setColumnWidth("pono", 150);
		tblMaterialOutward.setColumnWidth("vendorName", 150);
		tblMaterialOutward.setColumnAlignment("balancePayAmount", Align.RIGHT);
		tblMaterialOutward.addGeneratedColumn("balancePayAmount", new ColumnGenerator() {
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
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnIntentCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Indent");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new Indent();
		}
		if (event.getButton() == btnIntentIssueCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Indent Issue");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new IndentIssue();
		}
		if (event.getButton() == btnIntentReturnCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Indent Issue Return");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new IndentIssueReturn();
		}
		if (event.getButton() == btnLedgerCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Ledger");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new MaterialLedger();
		}
		if (event.getButton() == btnStockCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material Stock");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new MaterialStock();
		}
		if (event.getButton() == btnAddMaterial) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Material");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new Material();
		}
	}
}
