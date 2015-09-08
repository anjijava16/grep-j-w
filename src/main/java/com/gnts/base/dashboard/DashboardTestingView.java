package com.gnts.base.dashboard;

import org.apache.log4j.Logger;
import com.gnts.die.txn.DieRequest;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.hcm.txn.ServiceCallForm;
import com.gnts.mfg.txn.QATest;
import com.gnts.mfg.txn.QCTest;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.MaterialStockService;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class DashboardTestingView implements ClickListener {
	private static final long serialVersionUID = 1L;
	private Label lblDashboardTitle;
	private VerticalLayout clMainLayout;
	private HorizontalLayout hlHeader;
	private Button btnQCCount = new Button("11 Nos.", this);
	private Button btnQACount = new Button("15 Nos.", this);
	private Button btnserCallForm = new Button("13 Nos.", this);
	private Button btnDieReqCount = new Button("17 Nos.", this);
	private Button btnOthers = new Button("0 Nos.", this);
	private Table tblMaterialStock = new Table();
	private MaterialStockService servicematerialstock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private Logger logger = Logger.getLogger(DashboardTestingView.class);
	private Long companyId;
	
	public DashboardTestingView() {
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
		CustomLayout custom = new CustomLayout("testingdashboard");
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Testing Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		clMainLayout.addComponent(custom);
		btnQCCount.setStyleName(Runo.BUTTON_LINK);
		btnQACount.setStyleName(Runo.BUTTON_LINK);
		btnserCallForm.setStyleName(Runo.BUTTON_LINK);
		btnDieReqCount.setStyleName(Runo.BUTTON_LINK);
		btnOthers.setStyleName(Runo.BUTTON_LINK);
		custom.addComponent(btnQCCount, "qc");
		custom.addComponent(btnQACount, "qa");
		custom.addComponent(btnserCallForm, "scf");
		custom.addComponent(btnDieReqCount, "dierequest");
		custom.addComponent(btnOthers, "others");
		custom.addComponent(tblMaterialStock, "stockDetails");
		custom.addComponent(new CalendarMonthly("WO_SCHEDULE"), "testschedule");
		tblMaterialStock.setPageLength(11);
		loadStockDetails();
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
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnQCCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "QC Test");
			new QCTest();
		} else if (event.getButton() == btnQACount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "QA Test");
			new QATest();
		} else if (event.getButton() == btnserCallForm) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("IS_MARK_FRM", false);
			UI.getCurrent().getSession().setAttribute("IS_PROD_FRM", false);
			UI.getCurrent().getSession().setAttribute("IS_QC_FRM", true);
			UI.getCurrent().getSession().setAttribute("screenName", "Service Call Form");
			new ServiceCallForm();
		} else if (event.getButton() == btnDieReqCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Die Request");
			new DieRequest();
		} else if (event.getButton() == btnOthers) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Others");
		}
	}
}
