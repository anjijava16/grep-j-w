package com.gnts.erputil.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPSaveNotification;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

abstract public class BaseUI implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Root container for all the page components.
	VerticalLayout hlPageRootContainter = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
	// Header container which holds, screen name, notification and page master
	// buttons
	public HorizontalLayout hlPageHdrContainter = (HorizontalLayout) UI.getCurrent().getSession()
			.getAttribute("hlLayout");
	// Layout to display the page title
	public HorizontalLayout hlPageTitleLayout;
	// Notification layout to display information and errors
	HorizontalLayout hlNotificationLayout = new HorizontalLayout();
	// Container for all user input components, layouts
	public HorizontalLayout hlUserIPContainer = new HorizontalLayout();
	// Container for user search fields, buttons and layout
	public HorizontalLayout hlSrchContainer = new HorizontalLayout();
	// Container for user search fields, buttons and layout
	public VerticalLayout vlSrchRsltContainer = new VerticalLayout();
	// Layout for command buttons
	public HorizontalLayout hlCmdBtnLayout = new HorizontalLayout();
	// Common buttons used across all the screens
	public Button btnSave = new GERPButton("Save", "savebt", this);
	public Button btnCancel = new GERPButton("Cancel", "cancelbt", this);
	public Button btnAdd = new GERPButton("Add", "add", this);
	public Button btnEdit = new GERPButton("Edit", "editbt", this);
	public Button btnDownload = new GERPButton("Download", "downloadbt", this);
	public Button btnAuditRecords = new GERPButton("Audit", "hostorybtn", this);
	public Button btnSearch = new GERPButton("Search", "searchbt", this);
	public Button btnReset = new GERPButton("Reset", "resetbt", this);
	// Search result table
	public Table tblMstScrSrchRslt = new GERPTable();
	// Other local components and variables
	String screenName = "";
	public Label lblNotification;
	private Button btnScreenName;
	// CSVExprter declaration
	protected CSVExporter csvExporter = new CSVExporter();
	private Window wndReportPopup = new Window();
	private Logger logger = Logger.getLogger(BaseUI.class);
	
	public BaseUI() {
		if(UI.getCurrent().getSession().getAttribute("screenName")!=null){
			screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		}
		btnScreenName = new GERPButton(screenName, "link", this);
		// Build Page Header
		// Instantiate page title layout
		hlPageHdrContainter.removeAllComponents();
		hlPageTitleLayout = new HorizontalLayout();
		hlPageTitleLayout.setMargin(new MarginInfo(false, false, false, true));
		// Add screen name in the page title layout
		hlPageTitleLayout.addComponent(btnScreenName);
		hlPageTitleLayout.setComponentAlignment(btnScreenName, Alignment.MIDDLE_CENTER);
		// instantiate notification label
		lblNotification = new Label();
		lblNotification.setContentMode(ContentMode.HTML);
		// Add notification label in the notification layout
		hlNotificationLayout.addComponent(lblNotification);
		hlNotificationLayout.setComponentAlignment(lblNotification, Alignment.MIDDLE_CENTER);
		// Add page title to the Page header container
		hlPageHdrContainter.addComponent(hlPageTitleLayout);
		hlPageHdrContainter.setComponentAlignment(hlPageTitleLayout, Alignment.MIDDLE_LEFT);
		// Add notification to the Page header container
		hlPageHdrContainter.addComponent(hlNotificationLayout);
		hlPageHdrContainter.setComponentAlignment(hlNotificationLayout, Alignment.MIDDLE_CENTER);
		// Add master buttons to the Page header container
		hlPageHdrContainter.addComponent(btnSave);
		hlPageHdrContainter.setComponentAlignment(btnSave, Alignment.MIDDLE_RIGHT);
		hlPageHdrContainter.addComponent(btnCancel);
		hlPageHdrContainter.setComponentAlignment(btnCancel, Alignment.MIDDLE_RIGHT);
		hlPageHdrContainter.addComponent(btnSearch);
		hlPageHdrContainter.setComponentAlignment(btnSearch, Alignment.MIDDLE_RIGHT);
		hlPageHdrContainter.addComponent(btnReset);
		hlPageHdrContainter.setComponentAlignment(btnReset, Alignment.MIDDLE_RIGHT);
		hlPageHdrContainter.setExpandRatio(hlNotificationLayout, 1);
		// Format all the Container
		hlPageRootContainter.setSpacing(true);
		hlPageRootContainter.setWidth("100%");
		hlSrchContainer.setWidth("100%");
		vlSrchRsltContainer.setSizeFull();
		hlUserIPContainer.setWidth("100%");
		hlUserIPContainer.setVisible(false);
		hlCmdBtnLayout.addComponent(btnAdd);
		hlCmdBtnLayout.setComponentAlignment(btnAdd, Alignment.MIDDLE_LEFT);
		hlCmdBtnLayout.addComponent(btnEdit);
		hlCmdBtnLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);
		hlCmdBtnLayout.addComponent(btnAuditRecords);
		hlCmdBtnLayout.setComponentAlignment(btnAuditRecords, Alignment.MIDDLE_LEFT);
		hlCmdBtnLayout.addComponent(btnDownload);
		hlCmdBtnLayout.setComponentAlignment(btnDownload, Alignment.MIDDLE_RIGHT);
		hlCmdBtnLayout.setHeight("35px");
		hlCmdBtnLayout.setWidth("100%");
		hlCmdBtnLayout.setExpandRatio(btnAuditRecords, 1);
		hlCmdBtnLayout.addStyleName("topbarthree");
		hlCmdBtnLayout.setMargin(new MarginInfo(false, false, false, true));
		// Add child containers to the Root container
		hlPageRootContainter.addComponent(hlUserIPContainer);
		hlPageRootContainter.addComponent(hlSrchContainer);
		hlPageRootContainter.addComponent(GERPPanelGenerator.createPanel(hlCmdBtnLayout));
		hlPageRootContainter.addComponent(vlSrchRsltContainer);
		// Set the initial visibility property
		btnSave.setVisible(false);
		btnCancel.setVisible(false);
		btnEdit.setEnabled(false);
		btnAdd.setEnabled(true);
		UI.getCurrent().getSession().setAttribute("lblNotification", lblNotification);
		// Set Master screen search table properties and listener events
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
					btnAdd.setEnabled(true);
				} else {
					btnEdit.setEnabled(true);
					btnAdd.setEnabled(false);
				}
				resetFields();
			}
		});
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		// for report
		csvExporter.setCaption("Comma Delimited (CSV)");
		csvExporter.setStyleName("borderless");
	}
	
	// Abstract methods for user actions. Actual implementation will be done in
	// extended class
	protected abstract void searchDetails() throws ERPException.NoDataFoundException;
	
	protected abstract void resetSearchDetails();
	
	protected abstract void addDetails();
	
	protected abstract void editDetails();
	
	protected abstract void validateDetails() throws ERPException.ValidationException;
	
	protected abstract void saveDetails() throws ERPException.SaveException, FileNotFoundException, IOException;
	
	protected abstract void showAuditDetails();
	
	protected abstract void cancelDetails();
	
	public void downloadDetails() {
		csvExporter.setTableToBeExported(tblMstScrSrchRslt);
	}
	
	protected abstract void resetFields();
	
	/*
	 * viewPopupWindow()-->this function is used for view Popup Window for Download components
	 */
	private void viewPopupWindow(ClickEvent event) {
		wndReportPopup = new Window();
		wndReportPopup.setCaption(null);
		wndReportPopup.setWidth("172px");
		wndReportPopup.addStyleName("notifications");
		wndReportPopup.setClosable(false);
		wndReportPopup.setResizable(false);
		wndReportPopup.setDraggable(false);
		wndReportPopup.setPositionX(event.getClientX() - event.getRelativeX());
		wndReportPopup.setPositionY(event.getClientY() - event.getRelativeY());
		wndReportPopup.setCloseShortcut(KeyCode.ESCAPE, null);
		VerticalLayout vlDownload = new VerticalLayout();
		vlDownload.addComponent(csvExporter);
		vlDownload.setComponentAlignment(csvExporter, Alignment.TOP_CENTER);
		wndReportPopup.setContent(vlDownload);
	}
	
	// this method reset the visibility property of the button and layout based
	// on user actions
	@Override
	public void buttonClick(ClickEvent event) {
		wndReportPopup.close();
		if (btnAdd == event.getButton()) {
			hlUserIPContainer.setVisible(true);
			hlUserIPContainer.setEnabled(true);
			hlSrchContainer.setVisible(false);
			btnSave.setVisible(true);
			btnCancel.setVisible(true);
			btnSearch.setVisible(false);
			btnReset.setVisible(false);
			btnScreenName.setVisible(true);
			btnAuditRecords.setEnabled(false);
			btnAdd.setEnabled(false);
			hlUserIPContainer.removeAllComponents();
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			// Dummy implementation, actual will be implemented in extended
			// class
			addDetails();
		} else if (btnEdit == event.getButton()) {
			hlUserIPContainer.setVisible(true);
			hlUserIPContainer.setEnabled(true);
			hlSrchContainer.setVisible(false);
			btnSave.setVisible(true);
			btnCancel.setVisible(true);
			btnSearch.setVisible(false);
			btnEdit.setEnabled(false);
			btnAdd.setEnabled(false);
			btnReset.setVisible(false);
			btnScreenName.setVisible(true);
			btnAuditRecords.setEnabled(true);
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			hlUserIPContainer.removeAllComponents();
			// Dummy implementation, actual will be implemented in extended
			// class
			editDetails();
		} else if (btnSave == event.getButton()) {
			try {
				// Before save invoke the business validations.
				validateDetails();
				// After successful validation invokes the save method.
				saveDetails();
				// Display successful save message
				new GERPSaveNotification();
				hlSrchContainer.setVisible(false);
				btnAuditRecords.setEnabled(false);
			}
			catch (Exception e) {
				try {
					throw new ERPException.SaveException();
				}
				catch (SaveException e1) {
					logger.error("Company ID : "
							+ UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
							+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
							+ " > " + "Exception " + e1.getMessage());
					e1.printStackTrace();
				}
			}
		} else if (btnCancel == event.getButton()) {
			hlUserIPContainer.setVisible(false);
			hlSrchContainer.setVisible(true);
			btnSave.setVisible(false);
			btnCancel.setVisible(false);
			btnSearch.setVisible(true);
			btnReset.setVisible(true);
			btnEdit.setEnabled(false);
			btnAdd.setEnabled(true);
			btnAuditRecords.setEnabled(true);
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			hlSrchContainer.setEnabled(true);
			vlSrchRsltContainer.removeAllComponents();
			vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
			vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
			tblMstScrSrchRslt.setValue(null);
			// Dummy implementation, actual will be implemented in extended
			// class
			cancelDetails();
		} else if (btnDownload == event.getButton()) {
			// Dummy implementation, actual will be implemented in extended
			// class
			downloadDetails();
			event.getButton().removeStyleName("unread");
			if (wndReportPopup != null && wndReportPopup.getUI() != null) wndReportPopup.close();
			else {
				viewPopupWindow(event);
				UI.getCurrent().addWindow(wndReportPopup);
				wndReportPopup.focus();
				((VerticalLayout) UI.getCurrent().getContent()).addLayoutClickListener(new LayoutClickListener() {
					private static final long serialVersionUID = 1L;
					
					@Override
					public void layoutClick(LayoutClickEvent event) {
						wndReportPopup.close();
						((VerticalLayout) UI.getCurrent().getContent()).removeLayoutClickListener(this);
					}
				});
			}
		} else if (btnAuditRecords == event.getButton()) {
			btnScreenName.setVisible(true);
			btnAuditRecords.setEnabled(false);
			hlSrchContainer.setEnabled(false);
			if (tblMstScrSrchRslt.getValue() != null) {
			}
			// Dummy implementation, actual will be implemented in extended
			// class
			showAuditDetails();
			vlSrchRsltContainer.removeAllComponents();
			new AuditRecordsApp(vlSrchRsltContainer, (String) UI.getCurrent().getSession().getAttribute("audittable"),
					(String) UI.getCurrent().getSession().getAttribute("audittablepk"));
		} else if (btnScreenName == event.getButton()) {
			hlUserIPContainer.setVisible(false);
			hlUserIPContainer.setEnabled(true);
			hlSrchContainer.setVisible(true);
			btnSave.setVisible(false);
			btnCancel.setVisible(false);
			btnSearch.setVisible(true);
			btnReset.setVisible(true);
			btnEdit.setEnabled(false);
			btnAdd.setEnabled(true);
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			hlSrchContainer.setEnabled(true);
			btnAuditRecords.setEnabled(true);
			vlSrchRsltContainer.removeAllComponents();
			vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
			vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
			// Dummy implementation, actual will be implemented in extended
			// class
			cancelDetails();
		} else if (btnSearch == event.getButton()) {
			// Dummy implementation, actual will be implemented in extended
			// class
			tblMstScrSrchRslt.removeAllItems();
			try {
				searchDetails();
			}
			catch (Exception e) {
				logger.warn("btnSearch click -->", e);
			}
		} else if (btnReset == event.getButton()) {
			// Dummy implementation, actual will be implemented in extended
			// class
			hlUserIPContainer.setVisible(false);
			hlSrchContainer.setVisible(true);
			btnSave.setVisible(false);
			btnCancel.setVisible(false);
			btnSearch.setVisible(true);
			btnReset.setVisible(true);
			btnEdit.setEnabled(false);
			btnAdd.setEnabled(true);
			btnAuditRecords.setEnabled(true);
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			hlSrchContainer.setEnabled(true);
			vlSrchRsltContainer.removeAllComponents();
			vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
			vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
			tblMstScrSrchRslt.setValue(null);
			resetSearchDetails();
		}
	}
}
