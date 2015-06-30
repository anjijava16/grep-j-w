package com.gnts.base.dashboard;

import java.util.Collection;
import com.gnts.base.mst.Product;
import com.gnts.base.service.mst.ProductService;
import com.gnts.crm.mst.Client;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.dsn.stt.txn.DesignDocuments;
import com.gnts.dsn.stt.txn.ECNote;
import com.gnts.dsn.stt.txn.ECRequest;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.txn.SmsEnquiry;
import com.gnts.stt.dsn.service.txn.ECRequestService;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

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
	private NotificationsButton btnNotify=new NotificationsButton();
	private Button btnTest=new Button("22222");
	private NotificationsButton notificationsButton;
	
   
    private Window notificationsWindow;
	private SmsEnqHdrService serviceenqhdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private ECRequestService serviceECRequest = (ECRequestService) SpringContextHelper.getBean("ecRequest");

	private ProductService ServiceProduct = (ProductService) SpringContextHelper.getBean("Product");
	VerticalLayout clMainLayout;
	HorizontalLayout hlHeader;
	
	public DashbordDesignView() {
		branchId = Long.valueOf(UI.getCurrent().getSession().getAttribute("branchId").toString());
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		notificationsButton = buildNotificationsButton();
//		HorizontalLayout tools = new HorizontalLayout(notificationsButton, btnTest);
		hlHeader.removeAllComponents();
		CustomLayout custom = new CustomLayout("dashdesign");
		btnEnquiryCount.setCaption(serviceenqhdr.getSMSEnquiryListCount(null, null, null, null, null, null, null, null)
				.toString());
		btnClientCount.setCaption(serviceClients.getClientDetailscount(companyId, null, "Active", null).toString());
		btnProductCount.setCaption(ServiceProduct.getProductscount(companyId, null, "Active", null).toString());
		btnECRequest.setCaption(serviceECRequest.getProductscount(null, null, null, null).toString());
		

		//btnTest.setStyleName(Runo.BUTTON_LINK);
		btnEnquiryCount.setStyleName("borderless-colored");
		btnEnquiryWorkflow.setStyleName("borderless-colored");
		btnECRequest.setStyleName("borderless-colored");
		btnECNote.setStyleName("borderless-colored");
		btnProductCount.setStyleName("borderless-coloredbig");
		btnClientCount.setStyleName("borderless-coloredbig");
		VerticalLayout root = new VerticalLayout();
		root.addComponent(buildHeader());
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Design Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		hlHeader.addComponent(btnTest);
		hlHeader.addComponent(notificationsButton);
		hlHeader.setComponentAlignment(btnTest, Alignment.MIDDLE_CENTER);
		hlHeader.setComponentAlignment(notificationsButton, Alignment.TOP_RIGHT);
		
		clMainLayout.addComponent(custom);
		// MultipleAxes multipleAxes = new MultipleAxes();
		// custom.addComponent(multipleAxes.getChart(), "marketchart");
		custom.addComponent(btnEnquiryCount, "enquirycount");
		custom.addComponent(btnEnquiryWorkflow, "quotationcount");
		custom.addComponent(btnECRequest, "pocount");
		custom.addComponent(btnECNote, "invoicecount");
		custom.addComponent(btnProductCount, "productCount");
		custom.addComponent(btnClientCount, "clientCount");
		
		
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
	  private NotificationsButton buildNotificationsButton() {
	        NotificationsButton result = new NotificationsButton();
	        result.addClickListener(new ClickListener() {
	            @Override
	            public void buttonClick(final ClickEvent event) {
	                openNotificationsPopup(event);
	            }
	        });
	        return result;
	    }
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (event.getButton() == btnEnquiryCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Sales Enquiry");
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

        Label title = new Label("Notifications");
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_SPINNER);
        notificationsLayout.addComponent(title);      
        
        if (notificationsWindow == null) {
            notificationsWindow = new Window();
            notificationsWindow.setWidth(300.0f, Unit.PIXELS);
            notificationsWindow.addStyleName("notifications");
            notificationsWindow.setClosable(false);
            notificationsWindow.setResizable(false);
            notificationsWindow.setDraggable(false);
            notificationsWindow.setCloseShortcut(KeyCode.ESCAPE, null);
            notificationsWindow.setContent(notificationsLayout);
            notificationsWindow.center();
        }

        if (!notificationsWindow.isAttached()) {
        	notificationsWindow.setPositionX(event.getClientX());
        	notificationsWindow.setPositionY(event.getClientY());
            UI.getCurrent().addWindow(notificationsWindow); 
            notificationsWindow.focus();
        } else {
            notificationsWindow.close();
        }
        System.out.println("---------------------------------<<<<<<<<<<<<<<<<<<<<<<<");      
       }
	}


