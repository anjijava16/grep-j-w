/**
 * File Name 		: TransactionApprovel.java 
 * Description 		: this class is used for Transaction Approval. 
 * Author 			: SOUNDAR C 
 * Date 			: Mar 18, 2014
 * Modification 	:
 * Modified By 		: SOUNDAR C 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 18 2014        SOUNDAR C		          Intial Version
 * 
 */
package com.gnts.fms.txn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.fms.domain.txn.TransactionsDM;
import com.gnts.fms.service.mst.TransactionTypeService;
import com.gnts.fms.service.txn.AccountsService;
import com.gnts.fms.service.txn.TransactionsService;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TransactionApprovel implements ClickListener {
	private static final long serialVersionUID = 1L;
	private Button btnCancel, btnSave, btnDownload;
	private Table tblTransactions = new Table();
	private CheckBox chkApproveAll = new CheckBox("Approve All");
	private VerticalLayout vlMainPanel = new VerticalLayout();
	private VerticalLayout vlSearchPanel = new VerticalLayout();
	private VerticalLayout vlTablePanel = new VerticalLayout();
	private Button btnHome, btnBack;
	private VerticalLayout vlTable = new VerticalLayout();
	private HorizontalLayout hlAddEdit = new HorizontalLayout();
	private String loginUserName, screenName;
	private Label lblFormTittle, lblFormTitle1, lblAddEdit;
	private Label lblNotification, lblNotificationIcon;
	private TransactionsService serviceTransactions = (TransactionsService) SpringContextHelper.getBean("transaction");
	private AccountsService serviceAccounts = (AccountsService) SpringContextHelper.getBean("accounts");
	private TransactionTypeService serviceTransType = (TransactionTypeService) SpringContextHelper.getBean("transtype");
	private Long companyId;
	private int total;
	private BeanItemContainer<TransactionsDM> beans = null;
	private HorizontalLayout hlButtonLayout1, hlBreadCrumbs;
	private Window notifications = new Window();
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter(tblTransactions);
	private Logger logger = Logger.getLogger(TransactionApprovel.class);
	
	public TransactionApprovel() {
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		VerticalLayout clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		HorizontalLayout hlHeaderLayout = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeaderLayout);
	}
	
	/*
	 * buildMainview()-->for screen UI design
	 * @param clArgumentLayout hlHeaderLayout
	 */
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeaderLayout) {
		hlHeaderLayout.removeAllComponents();
		lblNotificationIcon = new Label();
		lblNotification = new Label();
		lblNotification.setContentMode(ContentMode.HTML);
		// button declaration
		btnCancel = new Button("Cancel", this);
		btnSave = new Button("Save", this);
		btnDownload = new Button("Download", this);
		btnBack = new Button("Home", this);
		btnCancel.addStyleName("cancelbt");
		btnDownload.addStyleName("downloadbt");
		btnSave.addStyleName("savebt");
		btnBack.setStyleName("link");
		btnHome = new Button("Home", this);
		btnHome.setStyleName("homebtn");
		btnDownload.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			public void buttonClick(ClickEvent event) {
				event.getButton().removeStyleName("unread");
				if (notifications != null && notifications.getUI() != null) notifications.close();
				else {
					buildNotifications(event);
					UI.getCurrent().addWindow(notifications);
					notifications.focus();
					((VerticalLayout) UI.getCurrent().getContent()).addLayoutClickListener(new LayoutClickListener() {
						private static final long serialVersionUID = 1L;
						
						@Override
						public void layoutClick(LayoutClickEvent event) {
							notifications.close();
							((VerticalLayout) UI.getCurrent().getContent()).removeLayoutClickListener(this);
						}
					});
				}
			}
		});
		GridLayout vlMainPanelgrid = new GridLayout(1, 1);
		vlMainPanelgrid.setSpacing(true);
		vlMainPanelgrid.setMargin(true);
		vlMainPanelgrid.setSizeFull();
		lblFormTittle = new Label();
		lblFormTittle.setContentMode(ContentMode.HTML);
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName + "</b>&nbsp;::&nbsp;");
		lblFormTitle1 = new Label();
		lblFormTitle1.setContentMode(ContentMode.HTML);
		lblFormTitle1.setValue("&nbsp;&nbsp;<b>" + screenName + "</b>&nbsp;::&nbsp;");
		lblAddEdit = new Label();
		lblAddEdit.setContentMode(ContentMode.HTML);
		vlMainPanel = new VerticalLayout();
		vlMainPanel.addComponent(GERPPanelGenerator.createPanel(vlMainPanelgrid));
		vlMainPanel.setMargin(true);
		vlMainPanel.setVisible(false);
		// add save and cancel to to layout
		hlButtonLayout1 = new HorizontalLayout();
		hlButtonLayout1.addComponent(btnSave);
		hlButtonLayout1.addComponent(btnCancel);
		hlButtonLayout1.setVisible(true);
		// add add,edit and download buttons to panel
		HorizontalLayout hlFileDownload = new HorizontalLayout();
		hlFileDownload.setSpacing(true);
		hlFileDownload.addComponent(btnDownload);
		hlFileDownload.setComponentAlignment(btnDownload, Alignment.MIDDLE_CENTER);
		HorizontalLayout hlTableTittle = new HorizontalLayout();
		hlTableTittle.addComponent(btnHome);
		hlTableTittle.addComponent(chkApproveAll);
		hlTableTittle.setHeight("30px");
		hlAddEdit.addStyleName("topbarthree");
		hlAddEdit.setWidth("100%");
		hlAddEdit.addComponent(hlTableTittle);
		hlAddEdit.addComponent(hlFileDownload);
		hlAddEdit.setComponentAlignment(hlFileDownload, Alignment.MIDDLE_RIGHT);
		hlAddEdit.setHeight("28px");
		// table declaration
		tblTransactions.setSizeFull();
		tblTransactions.setSelectable(true);
		tblTransactions.setColumnCollapsingAllowed(true);
		// tblTransactions.setPageLength(11);
		vlTablePanel = new VerticalLayout();
		tblTransactions.setImmediate(true);
		tblTransactions.setFooterVisible(true);
		vlTable.setSizeFull();
		vlTable.setMargin(new MarginInfo(false, true, false, true));
		vlTable.addComponent(hlAddEdit);
		vlTable.setSpacing(true);
		vlTable.addComponent(tblTransactions);
		vlTable.setExpandRatio(tblTransactions, 1);
		vlTablePanel.addComponent(vlTable);
		getEditableTable();
		populateAndConfig(false);
		// add search,table and add fields layout to mainpanel
		clMainLayout.setSizeFull();
		clMainLayout.addComponent(vlTablePanel);
		hlBreadCrumbs = new HorizontalLayout();
		hlBreadCrumbs.addComponent(lblFormTitle1);
		hlBreadCrumbs.addComponent(btnBack);
		hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
		hlBreadCrumbs.addComponent(lblAddEdit);
		hlBreadCrumbs.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER);
		hlBreadCrumbs.setVisible(false);
		// add notification and title name to header layout
		HorizontalLayout hlNotification = new HorizontalLayout();
		hlNotification.addComponent(lblNotificationIcon);
		hlNotification.setComponentAlignment(lblNotificationIcon, Alignment.MIDDLE_LEFT);
		hlNotification.addComponent(lblNotification);
		hlNotification.setComponentAlignment(lblNotification, Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(lblFormTittle);
		hlHeaderLayout.setComponentAlignment(lblFormTittle, Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlBreadCrumbs);
		hlHeaderLayout.setComponentAlignment(hlBreadCrumbs, Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlNotification);
		hlHeaderLayout.setComponentAlignment(hlNotification, Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlButtonLayout1);
		hlHeaderLayout.setComponentAlignment(hlButtonLayout1, Alignment.MIDDLE_RIGHT);
		excelexporter.setTableToBeExported(tblTransactions);
		csvexporter.setTableToBeExported(tblTransactions);
		pdfexporter.setTableToBeExported(tblTransactions);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
	}
	
	/*
	 * buildNotifications()-->this function is used for poppupview for Download components
	 */
	private void buildNotifications(ClickEvent event) {
		notifications = new Window();
		notifications.setWidth("178px");
		notifications.addStyleName("notifications");
		notifications.setClosable(false);
		notifications.setResizable(false);
		notifications.setDraggable(false);
		notifications.setPositionX(event.getClientX() - event.getRelativeX());
		notifications.setPositionY(event.getClientY() - event.getRelativeY());
		notifications.setCloseShortcut(KeyCode.ESCAPE, null);
		VerticalLayout vlDownload = new VerticalLayout();
		vlDownload.addComponent(excelexporter);
		vlDownload.addComponent(csvexporter);
		vlDownload.addComponent(pdfexporter);
		vlDownload.setSpacing(false);
		notifications.setContent(vlDownload);
	}
	
	/*
	 * populatedAndConfig()-->this function used to load the list to the table
	 * @param search if(search==true)--> it performs search operation else it loads all values
	 */
	private void populateAndConfig(boolean search) {
		try {
			tblTransactions.removeAllItems();
			List<TransactionsDM> list = new ArrayList<TransactionsDM>();
			list = serviceTransactions.getTransactionDetails(companyId, null, null, "Pending", null, null, null);
			total = list.size();
			beans = new BeanItemContainer<TransactionsDM>(TransactionsDM.class);
			beans.addAll(list);
			tblTransactions.setContainerDataSource(beans);
			tblTransactions.setVisibleColumns(new Object[] { "accTxnId", "transdt", "transtypename", "transamount",
					"paymentmode", "txnStatus", "appremarks", });
			tblTransactions.setColumnHeaders(new String[] { "Ref.Id", "Trans. Date", "Trans. Type", "Trans. Amount",
					"Paymant Mode", "Status", "Remarks" });
			tblTransactions.setColumnFooter("appremarks", "No.of Records: " + total);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		getExportTableDetails();
	}
	
	private void getEditableTable() {
		try {
			tblTransactions.setEditable(true);
			tblTransactions.setTableFieldFactory(new TableFieldFactory() {
				private static final long serialVersionUID = 1L;
				
				public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
					if (propertyId.toString().equals("txnStatus")) {
						ComboBox approvStatus = new GERPComboBox(null, BASEConstants.T_FMS_ACCOUNT_TXNS,
								BASEConstants.TXN_STATUS);
						approvStatus.setNullSelectionAllowed(false);
						return approvStatus;
					}
					if (propertyId.toString().equals("appremarks")) {
						TextField tf = new TextField();
						tf.setWidth("250");
						tf.setNullRepresentation("");
						return tf;
					}
					return null;
				}
			});
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * saveApproveAll()-->this function is used for save/update the records
	 */
	private void saveApproveAll() {
		try {
			@SuppressWarnings("unchecked")
			Collection<TransactionsDM> itemIds = (Collection<TransactionsDM>) tblTransactions.getVisibleItemIds();
			for (TransactionsDM transOld : (Collection<TransactionsDM>) itemIds) {
				BigDecimal openbalance = new BigDecimal("0");
				BigDecimal transamount = new BigDecimal("0");
				BigDecimal closebalance = new BigDecimal("0");
				BigDecimal accountbalance = new BigDecimal("0");
				if (transOld.getAccountId() != null) {
					try {
						transamount = transOld.getTransamount();
						accountbalance = serviceAccounts
								.getAccountsList(null, transOld.getAccountId(), null, null, null, null, null).get(0)
								.getCurrentBalance();
						openbalance = accountbalance;
					}
					catch (Exception e) {
						logger.info(e.getMessage());
					}
				}
				if (serviceTransType.getTransactionTypeList(null, null, null, null, transOld.getTranstypeid()).get(0)
						.getCrdr().toUpperCase().equals("C")
						|| serviceTransType.getTransactionTypeList(null, null, null, null, transOld.getTranstypeid())
								.get(0).getCrdr().toUpperCase().equals("CREDIT")) {
					accountbalance = accountbalance.add(transamount);
					closebalance = accountbalance;
				} else {
					accountbalance = accountbalance.subtract(transamount);
					closebalance = accountbalance;
				}
				serviceTransactions.updateTrasactionDetails(transOld.getAccTxnId(), openbalance, closebalance, "",
						new Date(), loginUserName, transOld.getAppremarks(), null);
				serviceTransactions.updateAccountBalance(transOld.getAccountId(), closebalance, transamount);
			}
			chkApproveAll.setValue(false);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void saveDetails() {
		try {
			@SuppressWarnings("unchecked")
			Collection<TransactionsDM> itemIds = (Collection<TransactionsDM>) tblTransactions.getVisibleItemIds();
			for (TransactionsDM transOld : (Collection<TransactionsDM>) itemIds) {
				if (transOld.getTxnStatus().equals("Pending") || transOld.getTxnStatus().equals("Rejected")) {
					serviceTransactions.updateTrasactionDetails(transOld.getAccTxnId(), new BigDecimal("0"),
							new BigDecimal("0"), transOld.getTxnStatus(), new Date(), loginUserName,
							transOld.getAppremarks(), null);
				} else {
					BigDecimal openbalance = new BigDecimal("0");
					BigDecimal transamount = new BigDecimal("0");
					BigDecimal closebalance = new BigDecimal("0");
					BigDecimal accountbalance = new BigDecimal("0");
					if (transOld.getAccTxnId() != null) {
						try {
							transamount = transOld.getTransamount();
							accountbalance = serviceAccounts
									.getAccountsList(null, transOld.getAccountId(), null, null, null, null, null)
									.get(0).getCurrentBalance();
							openbalance = accountbalance;
						}
						catch (Exception e) {
						}
					}
					if (serviceTransType.getTransactionTypeList(null, null, null, null, transOld.getTranstypeid())
							.get(0).getCrdr().toUpperCase().equals("C")
							|| serviceTransType
									.getTransactionTypeList(null, null, null, null, transOld.getTranstypeid()).get(0)
									.getCrdr().toUpperCase().equals("CREDIT")) {
						accountbalance = accountbalance.add(transamount);
						closebalance = accountbalance;
					} else {
						accountbalance = accountbalance.subtract(transamount);
						closebalance = accountbalance;
					}
					serviceTransactions.updateTrasactionDetails(transOld.getAccTxnId(), openbalance, closebalance,
							transOld.getTxnStatus(), new Date(), loginUserName, transOld.getAppremarks(), null);
					serviceTransactions.updateAccountBalance(transOld.getAccTxnId(), closebalance, transamount);
				}
			}
			chkApproveAll.setValue(false);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void resetFields() {
		// TODO Auto-generated method stub
		btnSave.setComponentError(null);
		btnSave.setCaption("Save");
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName + "</b>&nbsp;::&nbsp;Home");
		lblNotificationIcon.setIcon(null);
		lblNotification.setValue("");
		lblFormTittle.setVisible(true);
		hlBreadCrumbs.setVisible(false);
	}
	
	/**
	 * this used to export the data
	 */
	private void getExportTableDetails() {
		excelexporter.setTableToBeExported(tblTransactions);
		csvexporter.setTableToBeExported(tblTransactions);
		pdfexporter.setTableToBeExported(tblTransactions);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
	}
	
	/*
	 * this function handles button click event
	 * @param event
	 */
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		notifications.close();
		if (btnSave == event.getButton()) {
			try {
				if (chkApproveAll.getValue().equals(true)) {
					saveApproveAll();
				} else {
					saveDetails();
				}
				populateAndConfig(false);
			}
			catch (Exception e) {
				e.printStackTrace();
				populateAndConfig(false);
			}
		} else if (btnCancel == event.getButton()) {
			vlTablePanel.setVisible(true);
			populateAndConfig(false);
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName + "</b>&nbsp;::&nbsp;");
			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			lblAddEdit.setValue("");
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			chkApproveAll.setValue(false);
		} else if (btnBack == event.getButton()) {
			resetFields();
			vlMainPanel.setVisible(false);
			vlSearchPanel.setVisible(true);
			vlSearchPanel.setEnabled(true);
			vlTablePanel.setVisible(true);
			hlButtonLayout1.setVisible(false);
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName + "</b>&nbsp;::&nbsp;");
			lblNotification.setCaption("");
			lblNotificationIcon.setIcon(null);
			lblAddEdit.setValue("");
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
		}
	}
}
