package com.gnts.base.dashboard;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.service.txn.MaterialStockService;
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
	private MaterialStockService servicematerialstock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private Logger logger = Logger.getLogger(DashboardMMSView.class);
	private Table tblMstScrSrchRslt = new GERPTable();
	private Long companyId;
	
	public DashboardMMSView() {
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
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
		custom.addComponent(btnEnquiryCount, "enquiry");
		custom.addComponent(btnQuotationCount, "quotation");
		custom.addComponent(btnOrdersCount, "purchaseorder");
		custom.addComponent(btnReceiptsCount, "receipts");
		custom.addComponent(btnBillsCount, "vendorbills");
		custom.addComponent(tblMstScrSrchRslt, "stockDetails");
		tblMstScrSrchRslt.setHeight("300px");
		loadStockDetails();
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
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "materialName", "stockType", "materialUOM",
					"currentStock", "effectiveStock" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Material", "Stock Type", "UOM", "Current Stock",
					"Effective Stock" });
			tblMstScrSrchRslt.setColumnFooter("effectiveStock", "No.of.Records :" + materiallist.size());
			tblMstScrSrchRslt.addGeneratedColumn("materialName", new ColumnGenerator() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					@SuppressWarnings("unchecked")
					BeanItem<MaterialStockDM> item = (BeanItem<MaterialStockDM>) source.getItem(itemId);
					MaterialStockDM emp = (MaterialStockDM) item.getBean();
					Label textLabel = new Label("<body><p style='backgrckckound-color:lightgrey;color:#3CAF9E;font-size:15px'>" + emp.getMaterialName()
							+ "</p></body>", ContentMode.HTML);
					return textLabel;
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("loadSrchRslt-->" + e);
		}
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
	}
}
