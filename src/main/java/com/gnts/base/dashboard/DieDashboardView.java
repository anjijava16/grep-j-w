package com.gnts.base.dashboard;

import org.apache.log4j.Logger;
import com.gnts.asm.domain.txn.AssetMaintDetailDM;
import com.gnts.asm.domain.txn.GeneratorDM;
import com.gnts.asm.service.txn.AssetMaintDetailService;
import com.gnts.asm.service.txn.GeneratorService;
import com.gnts.die.txn.DieRequest;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.mms.domain.txn.MmsEnqHdrDM;
import com.gnts.mms.service.txn.MmsEnqHdrService;
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

/**
 * @author soundar
 *
 */
public class DieDashboardView implements ClickListener {
	private static final long serialVersionUID = 1L;
	private Label lblDashboardTitle;
	private VerticalLayout clMainLayout;
	private HorizontalLayout hlHeader;
	private Button btnDieRequest = new Button("11 Nos.", this);
	private Button btnDieSection = new Button("15 Nos.", this);
	private Button btnMoldTrialReq = new Button("13 Nos.", this);
	private Button btnDieCompletion = new Button("17 Nos.", this);
	private Button btnBOM = new Button("16 Nos.", this);
	private AssetMaintDetailService serviceAssetMaintDetails = (AssetMaintDetailService) SpringContextHelper
			.getBean("assetMaintDetails");
	private MmsEnqHdrService serviceMmsEnqHdr = (MmsEnqHdrService) SpringContextHelper.getBean("MmsEnqHdr");
	private GeneratorService serviceGenerator = (GeneratorService) SpringContextHelper.getBean("generator");
	private Logger logger = Logger.getLogger(DashboardMMSView.class);
	private Table tblAssetMaint = new Table();
	private Table tblEnquiry = new Table();
	private Long companyId;
	private VerticalLayout vlGensetOilStatus = new VerticalLayout();
	
	public DieDashboardView() {
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
		CustomLayout custom = new CustomLayout("diedashboard");
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Die Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		clMainLayout.addComponent(custom);
		btnDieRequest.setStyleName(Runo.BUTTON_LINK);
		btnDieSection.setStyleName(Runo.BUTTON_LINK);
		btnMoldTrialReq.setStyleName(Runo.BUTTON_LINK);
		btnDieCompletion.setStyleName(Runo.BUTTON_LINK);
		btnBOM.setStyleName(Runo.BUTTON_LINK);
		custom.addComponent(btnDieRequest, "enquiry");
		custom.addComponent(btnDieSection, "quotation");
		custom.addComponent(btnMoldTrialReq, "purchaseorder");
		custom.addComponent(btnBOM, "receipts");
		custom.addComponent(btnDieCompletion, "vendorbills");
		custom.addComponent(tblAssetMaint, "stockDetails");
		custom.addComponent(vlGensetOilStatus, "gensetoilchk");
		custom.addComponent(new CalendarMonthly("DIE_SCHEDULE"), "maintaincedtls");
		tblAssetMaint.setHeight("300px");
		tblAssetMaint.setWidth("99%");
		tblEnquiry.setHeight("250px");
		loadMaintainceDetails();
		loadEnquiryList();
		loadGensetDetails();
	}
	
	private void loadMaintainceDetails() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : > " + "Loading Search...");
			tblAssetMaint.removeAllItems();
			BeanItemContainer<AssetMaintDetailDM> beanAssetMaint = new BeanItemContainer<AssetMaintDetailDM>(
					AssetMaintDetailDM.class);
			beanAssetMaint.addAll(serviceAssetMaintDetails.getAssetMaintDetailList(null, null, null, null, "Pending"));
			tblAssetMaint.setContainerDataSource(beanAssetMaint);
			tblAssetMaint.setVisibleColumns(new Object[] { "assetName", "maintenanceType", "problemDescription",
					"lastUpdatedDt", "lastUpdatedBy" });
			tblAssetMaint.setColumnHeaders(new String[] { "Asset Name", "Type", "Problem", "Raised on", "Raised by" });
			tblAssetMaint.setColumnWidth("problemDescription", 400);
			tblAssetMaint.addGeneratedColumn("problemDescription", new ColumnGenerator() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					@SuppressWarnings("unchecked")
					BeanItem<AssetMaintDetailDM> item = (BeanItem<AssetMaintDetailDM>) source.getItem(itemId);
					AssetMaintDetailDM emp = (AssetMaintDetailDM) item.getBean();
					return new Label(
							"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#E26666;font-size:12px'>"
									+ emp.getProblemDescription() + "</h1>", ContentMode.HTML);
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
	
	private void loadGensetDetails() {
		vlGensetOilStatus.setSpacing(true);
		for (GeneratorDM generatorDM : serviceGenerator.getGeneratorDetailList(null, null, null, null, "Y", null)) {
			Label lbl = new Label(generatorDM.getAssetName()
					+ " disel closing balance is   <span style='color:red;font-size:15px'>"
					+ generatorDM.getDiselCloseBalance() + " Ltrs.</span>", ContentMode.HTML);
			lbl.setStyleName("innerPanel");
			vlGensetOilStatus.addComponent(lbl);
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnDieRequest) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Die Request");
			UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new DieRequest();
		}
		if (event.getButton() == btnDieSection) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Die Section");
			UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new DieRequest();
		}
		if (event.getButton() == btnMoldTrialReq) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Mold Trial Request");
			UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new DieRequest();
		}
		if (event.getButton() == btnDieCompletion) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Die Completion");
			UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new DieRequest();
		}
		if (event.getButton() == btnBOM) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Bill of Material");
			UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new DieRequest();
		}
	}
}
