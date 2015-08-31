package com.gnts.base.dashboard;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.GatepassHdrDM;
import com.gnts.mms.domain.txn.IndentHdrDM;
import com.gnts.mms.domain.txn.MaterialLedgerDM;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.mst.Material;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.GatepassHdrService;
import com.gnts.mms.service.txn.IndentHdrService;
import com.gnts.mms.service.txn.MaterialLedgerService;
import com.gnts.mms.service.txn.MaterialStockService;
import com.gnts.mms.txn.DC;
import com.gnts.mms.txn.Indent;
import com.gnts.mms.txn.IndentIssue;
import com.gnts.mms.txn.IndentIssueReturn;
import com.gnts.mms.txn.MaterialGatepass;
import com.gnts.mms.txn.MaterialLedger;
import com.gnts.mms.txn.MaterialStock;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
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
	private Button btnStockReport = new Button("View Report", this);
	private Button btnLedgerInReport = new Button("Inward", this);
	private Button btnLedgerOutReport = new Button("Outward", this);
	private Button btnGatepass = new Button("11 Nos", this);
	private Button btnDC = new Button("10 Nos", this);
	private IndentHdrService serviceIndentHdr = (IndentHdrService) SpringContextHelper.getBean("IndentHdr");
	private MaterialStockService servicematerialstock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private MaterialLedgerService serviceledger = (MaterialLedgerService) SpringContextHelper.getBean("materialledger");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private GatepassHdrService serviceGatepass = (GatepassHdrService) SpringContextHelper.getBean("gatepasshdr");
	private Logger logger = Logger.getLogger(DashboardStoreView.class);
	private Table tblMaterialStock = new Table();
	private Table tblMaterialInward = new Table();
	private Table tblMaterialOutward = new Table();
	private Table tblIndent = new Table();
	private Table tblGatepass = new Table();
	private ComboBox cbMaterial;
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
		btnGatepass.setStyleName(Runo.BUTTON_LINK);
		btnDC.setStyleName(Runo.BUTTON_LINK);
		btnStockReport.setStyleName(Runo.BUTTON_LINK);
		btnLedgerInReport.setStyleName(Runo.BUTTON_LINK);
		btnLedgerOutReport.setStyleName(Runo.BUTTON_LINK);
		btnAddMaterial.setHtmlContentAllowed(true);
		custom.addComponent(btnIntentCount, "enquiry");
		custom.addComponent(btnIntentIssueCount, "quotation");
		custom.addComponent(btnIntentReturnCount, "purchaseorder");
		custom.addComponent(btnStockCount, "stock");
		custom.addComponent(btnLedgerCount, "ledger");
		custom.addComponent(tblMaterialStock, "stockDetails");
		custom.addComponent(tblIndent, "enquirytable");
		custom.addComponent(btnAddMaterial, "addmaterial");
		custom.addComponent(tblMaterialInward, "paymenttable");
		custom.addComponent(tblMaterialOutward, "deliverypending");
		custom.addComponent(btnGatepass, "gatepass");
		custom.addComponent(btnDC, "dc");
		custom.addComponent(btnStockReport, "stockreport");
		custom.addComponent(tblGatepass, "nonreturngoods");
		cbMaterial = new ComboBox();
		cbMaterial.setItemCaptionPropertyId("materialName");
		cbMaterial.setWidth("180");
		btnLedgerInReport.setWidth("90px");
		loadMaterialList();
		custom.addComponent(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(cbMaterial);
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(btnLedgerInReport);
						addComponent(btnLedgerOutReport);
					}
				});
			}
		}, "ledgerreports");
		tblMaterialStock.setPageLength(7);
		tblIndent.setHeight("250px");
		tblMaterialInward.setHeight("450px");
		tblMaterialInward.setWidth("510px");
		tblMaterialOutward.setWidth("510px");
		tblMaterialOutward.setHeight("450px");
		tblGatepass.setWidth("100%");
		tblGatepass.setHeight("450px");
		loadStockDetails();
		loadEnquiryList();
		loadMaterialInwardDetails();
		loadDeliveryDetails();
		loadMaterialGatepass();
	}
	
	private void loadStockDetails() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : > " + "Loading Search...");
			tblMaterialStock.removeAllItems();
			BeanItemContainer<MaterialStockDM> beanmaterialstock = new BeanItemContainer<MaterialStockDM>(
					MaterialStockDM.class);
			beanmaterialstock.addAll(servicematerialstock.getMaterialStockList(null, companyId, null, null, null, null,
					"F"));
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
			tblIndent.removeAllItems();
			List<IndentHdrDM> indentHdrList = new ArrayList<IndentHdrDM>();
			indentHdrList = serviceIndentHdr.getMmsIndentHdrList(null, null, null, null, null, null, null, null, null,
					"F");
			BeanItemContainer<IndentHdrDM> beanIndentHdrDM = new BeanItemContainer<IndentHdrDM>(IndentHdrDM.class);
			beanIndentHdrDM.addAll(indentHdrList);
			tblIndent.setContainerDataSource(beanIndentHdrDM);
			tblIndent.setVisibleColumns(new Object[] { "indentNo", "indentDate", "indentStatus", "empName",
					"indentRemarks" });
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
					if (emp.getIndentStatus().equalsIgnoreCase("Pending")
							|| emp.getIndentStatus().equalsIgnoreCase("Cancelled")) {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#E26666;font-size:12px'>"
										+ emp.getIndentStatus() + "</h1>", ContentMode.HTML);
					} else if (emp.getIndentStatus().equalsIgnoreCase("Approved")) {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#6CD4BD;font-size:12px'>"
										+ emp.getIndentStatus() + "</h1>", ContentMode.HTML);
					} else {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#EC9E20;font-size:12px'>"
										+ emp.getIndentStatus() + "</h1>", ContentMode.HTML);
					}
				}
			});
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadMaterialInwardDetails() {
		try {
			tblMaterialInward.removeAllItems();
			List<MaterialLedgerDM> materiallist = serviceledger.getMaterialLedgerList(null, null, null, null, null,
					"I", null, "F");
			BeanItemContainer<MaterialLedgerDM> beanmatrlledger = new BeanItemContainer<MaterialLedgerDM>(
					MaterialLedgerDM.class);
			beanmatrlledger.addAll(materiallist);
			tblMaterialInward.setContainerDataSource(beanmatrlledger);
			tblMaterialInward.setSelectable(true);
			tblMaterialInward.setVisibleColumns(new Object[] { "materialName", "stockledgeDate", "inoutFQty",
					"materialUOM", "referenceRemark" });
			tblMaterialInward.setColumnHeaders(new String[] { "Material", "Date", "Qty", "UOM", "Remarks" });
			tblMaterialInward.setColumnWidth("referenceRemark", 130);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadDeliveryDetails() {
		try {
			tblMaterialOutward.removeAllItems();
			List<MaterialLedgerDM> materiallist = serviceledger.getMaterialLedgerList(null, null, null, null, null,
					"O", null, "F");
			BeanItemContainer<MaterialLedgerDM> beanmatrlledger = new BeanItemContainer<MaterialLedgerDM>(
					MaterialLedgerDM.class);
			beanmatrlledger.addAll(materiallist);
			tblMaterialOutward.setContainerDataSource(beanmatrlledger);
			tblMaterialOutward.setSelectable(true);
			tblMaterialOutward.setVisibleColumns(new Object[] { "materialName", "stockledgeDate", "inoutFQty",
					"materialUOM", "referenceRemark" });
			tblMaterialOutward.setColumnHeaders(new String[] { "Material", "Date", "Qty", "UOM", "Remarks" });
			tblMaterialOutward.setColumnWidth("referenceRemark", 130);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadMaterialGatepass() {
		try {
			tblGatepass.removeAllItems();
			List<GatepassHdrDM> listGatepass = new ArrayList<GatepassHdrDM>();
			BeanItemContainer<GatepassHdrDM> beanGatePassHdr = new BeanItemContainer<GatepassHdrDM>(GatepassHdrDM.class);
			listGatepass = serviceGatepass.getGatepassHdrList(companyId, null, "Returnable", null, null, null,
					"Pending", null, "F");
			beanGatePassHdr.addAll(listGatepass);
			tblGatepass.setContainerDataSource(beanGatePassHdr);
			tblGatepass.setVisibleColumns(new Object[] { "gatepassId", "gatepassDt", "gatepassType", "returnDate",
					"gatepassStatus", "lastUpdatedDt", "lastUpdatedBy" });
			tblGatepass.setColumnHeaders(new String[] { "Ref.No", "Gate Pass Date ", "Gate Pass Type", "Return Date",
					"Status", "Updated Date", "Updated By" });
			tblGatepass.setPageLength(13);
			tblGatepass.setColumnAlignment("gatepassId", Align.RIGHT);
			tblGatepass.addGeneratedColumn("gatepassStatus", new ColumnGenerator() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					@SuppressWarnings("unchecked")
					BeanItem<GatepassHdrDM> item = (BeanItem<GatepassHdrDM>) source.getItem(itemId);
					GatepassHdrDM gatepassHdrDM = (GatepassHdrDM) item.getBean();
					if (gatepassHdrDM.getReturnDate() != null && gatepassHdrDM.getReturnDate().before(new Date())) {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#E26666;font-size:12px'>"
										+ gatepassHdrDM.getGatepassStatus() + "</h1>", ContentMode.HTML);
					} else if (gatepassHdrDM.getReturnDate() != null && gatepassHdrDM.getReturnDate().after(new Date())) {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#6CD4BD;font-size:12px'>"
										+ gatepassHdrDM.getGatepassStatus() + "</h1>", ContentMode.HTML);
					} else {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#EC9E20;font-size:12px'>"
										+ gatepassHdrDM.getGatepassStatus() + "</h1>", ContentMode.HTML);
					}
				}
			});
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Loading Material List
	private void loadMaterialList() {
		try {
			BeanContainer<Long, MaterialDM> beanmaterial = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
			beanmaterial.setBeanIdProperty("materialId");
			beanmaterial.addAll(serviceMaterial.getMaterialList(null, companyId, null, null, null, null, null, null,
					"Active", "P"));
			cbMaterial.setContainerDataSource(beanmaterial);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
		if (event.getButton() == btnGatepass) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Gatepass");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new MaterialGatepass();
		}
		if (event.getButton() == btnDC) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Delivery challan");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new DC();
		}
		if (event.getButton() == btnStockReport) {
			showStockReport();
		}
		if (event.getButton() == btnLedgerInReport) {
			showLedgerReport("I");
		}
		if (event.getButton() == btnLedgerOutReport) {
			showLedgerReport("O");
		}
	}
	
	private void showLedgerReport(String inout) {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put("INOUT_FLAG", inout);
			if (cbMaterial.getValue() != null) {
				parameterMap.put("item_id", ((Long) cbMaterial.getValue()).toString());
			} else {
				parameterMap.put("item_id", (String) cbMaterial.getValue());
			}
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/materialledger"); // materialledger is the name of my jasper
			// file.
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
	
	private void showStockReport() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, Long> parameterMap = new HashMap<String, Long>();
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/materialstock"); // materialstock is the name of my jasper
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
}
