package com.gnts.base.dashboard;

import java.util.ArrayList;
import java.util.List;
import com.gnts.base.mst.Product;
import com.gnts.base.service.mst.ProductService;
import com.gnts.crm.mst.Client;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.die.txn.DieRequest;
import com.gnts.dsn.stt.txn.DesignDocuments;
import com.gnts.dsn.stt.txn.ECNote;
import com.gnts.dsn.stt.txn.ECRequest;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.txn.SmsEnquiry;
import com.gnts.stt.dsn.service.txn.ECRequestService;
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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class DashbordDesignView implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long companyId, branchId;
	private Label lblDashboardTitle;
	private Button btnEnquiryCount = new Button("100", this);
	private Button btnEnquiryWorkflow = new Button("125", this);
	private Button btnECRequest = new Button("100", this);
	private Button btnECNote = new Button("55", this);
	private Button btnWOCount = new Button("7", this);
	private Button btnProductCount = new Button("17", this);
	private Button btnClientCount = new Button("22", this);
	private Button btnNotify;
	private Window notificationsWindow;
	private SmsEnqHdrService serviceEnqHdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private ECRequestService serviceECRequest = (ECRequestService) SpringContextHelper.getBean("ecRequest");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private VerticalLayout clMainLayout;
	private HorizontalLayout hlHeader;
	int countnotify = 0;
	
	public DashbordDesignView() {
		Long.valueOf(UI.getCurrent().getSession().getAttribute("branchId").toString());
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		countnotify = serviceEnqHdr.getSmsEnqHdrList(companyId, null, branchId, null, "Approved", "P", null, null)
				.size();
		btnNotify = new Button(countnotify + "");
		btnNotify.setHtmlContentAllowed(true);
		hlHeader.removeAllComponents();
		CustomLayout custom = new CustomLayout("dashdesign");
		btnEnquiryCount.setCaption(serviceEnqHdr.getSMSEnquiryListCount(null, null, null, null, "Approved", null, null,
				null).toString());
		btnClientCount.setCaption(serviceClients.getClientDetailscount(companyId, null, "Active", null).toString());
		btnProductCount.setCaption(serviceProduct.getProductscount(companyId, null, "Active", null).toString());
		btnECRequest.setCaption(serviceECRequest.getProductscount(null, null, null, null).toString());
		btnEnquiryCount.setStyleName("borderless-colored");
		btnEnquiryWorkflow.setStyleName("borderless-colored");
		btnECRequest.setStyleName("borderless-colored");
		btnECNote.setStyleName("borderless-colored");
		btnWOCount.setStyleName("borderless-colored");
		btnProductCount.setStyleName("borderless-coloredbig");
		btnClientCount.setStyleName("borderless-coloredbig");
		btnNotify.setIcon(new ThemeResource("img/download.png"));
		VerticalLayout root = new VerticalLayout();
		root.addComponent(buildHeader());
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Design Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		hlHeader.addComponent(btnNotify);
		hlHeader.setComponentAlignment(btnNotify, Alignment.TOP_RIGHT);
		clMainLayout.addComponent(custom);
		custom.addComponent(btnEnquiryCount, "enquirycount");
		custom.addComponent(btnEnquiryWorkflow, "quotationcount");
		custom.addComponent(btnECRequest, "pocount");
		custom.addComponent(btnECNote, "invoicecount");
		custom.addComponent(btnProductCount, "productCount");
		custom.addComponent(btnClientCount, "clientCount");
		custom.addComponent(btnWOCount, "workordercount");
		custom.addComponent(new CalendarMonthly("DESIGN_VIEW"), "designview");
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
			UI.getCurrent().getSession().setAttribute("IS_MARK_FRM", false);
			UI.getCurrent().getSession().setAttribute("IS_ENQ_WF", true);
			new SmsEnquiry();
		}
		if (event.getButton() == btnEnquiryWorkflow) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Design Documents");
			new DesignDocuments();
		}
		if (event.getButton() == btnECRequest) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "ECR");
			new ECRequest();
		}
		if (event.getButton() == btnECNote) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "ECN");
			new ECNote();
		}
		if (event.getButton() == btnWOCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Bill of Material");
			UI.getCurrent().getSession().setAttribute("IS_DESIGN_DR", true);
			UI.getCurrent().getSession().setAttribute("IS_DIE_ENQ", false);
			// UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new DieRequest();
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
	}
	
	private void openNotificationsPopup(final ClickEvent event) {
		VerticalLayout notificationsLayout = new VerticalLayout();
		notificationsLayout.setMargin(true);
		notificationsLayout.setSpacing(true);
		final Panel panel = new Panel("Notifications(<font color=red><font size=3>"+ countnotify+ "</font></font color>) ");
		notificationsLayout.addComponent(panel);
		List<SmsEnqHdrDM> smsEnqHdrList = new ArrayList<SmsEnqHdrDM>();
		SmsEnqHdrDM smspohdr = new SmsEnqHdrDM();
		smspohdr.getEnquiryStatus();
		smsEnqHdrList.add(smspohdr);
		smsEnqHdrList = serviceEnqHdr.getSmsEnqHdrList(null, null, null, null, "Approved", "F", null, null);
		FormLayout fmlayout = new FormLayout();
		VerticalLayout hrLayout = new VerticalLayout();
		for (SmsEnqHdrDM n : smsEnqHdrList) {
			hrLayout.addStyleName("notification-item");
			Label titleLabel = new Label("\n" + "<small>Status : </small><b><font color=blue><font size=4>"
					+ n.getEnquiryStatus() + "</font></b>", ContentMode.HTML);
			Label titleLabel1 = new Label("<small>Enquiry No: </small><font color=green>" + n.getEnquiryNo()
					+ "</font>", ContentMode.HTML);
			Label titleLabel2 = new Label("<small>Branch : </small><font color=green>" + n.getBranchName() + "</font>",
					ContentMode.HTML);
			Label titleLabel3 = new Label();
			if (n.getRemarks() != null) {
				titleLabel3 = new Label("<small>Remarks : </small><font color=red>" + n.getRemarks() + "</font>",
						ContentMode.HTML);
			} else {
				titleLabel3 = new Label("<small>Remarks : </small><font color=red> ------- </font>", ContentMode.HTML);
			}
			Label titleLabel4 = new Label("<HR size=3 color=red>", ContentMode.HTML);
			titleLabel.addStyleName("notification-title");
			fmlayout.addComponents(titleLabel);
			fmlayout.addComponents(titleLabel1);
			fmlayout.addComponents(titleLabel2);
			fmlayout.addComponent(titleLabel3);
			fmlayout.addComponent(titleLabel4);
			hrLayout.addComponent(fmlayout);
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
