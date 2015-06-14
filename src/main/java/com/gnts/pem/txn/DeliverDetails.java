
/**
 * File Name	:	DeliverDetails.java
 * Description	:	Used for approval process to T_PEM_CM_EVAL_DETAILS
 * Author		:	Prakash.s
 * Date			:	mar 28, 2014
 * Modification 
 * Modified By  :   prakash.s
 * Description	:
 *
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies pvt. ltd.
 * Version         Date           Modified By             Remarks
 * 
 */
package com.gnts.pem.txn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.LookupService;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateValidation;
import com.gnts.erputil.Common;
import com.gnts.erputil.util.DateUtils;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class DeliverDetails implements ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CmEvalDetailsService beanEvaluation = (CmEvalDetailsService) SpringContextHelper
			.getBean("evalDtls");
	private CmBankService beanbank = (CmBankService) SpringContextHelper
			.getBean("bank");
	private LookupService lookUpBean = (LookupService) SpringContextHelper.getBean("lookup");

	private Table tblEvalDetails;
	private Long companyId;
	private String screenName;
	private Long userId;

	// Buttons
	private Button btnedit, btnView;
	private Button btnCancel;
	private Button btnSearch, btnReset;
	private Button btnApp;
	private Button btnBack;
	
	private Button btnDownload;
	  HorizontalLayout hlFileDownloadLayout;
	  private FileDownloader filedownloader;
	//Declaration for Exporter
	private Window notifications=new Window();
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();

	// components
	private TextField tfSearchEvalNo, tfSearCustName;
	private ComboBox cbSearchBankName = new ComboBox("Bank Name");
	private TextField tfVeriFiedBy, tfevaluationNo, tfEvaluationDate,
			tfBankName, tfBranchName, tfCustomername, tfInspectionDate,
			tfinspectionBy, tfDelecompany, tfDeleBy;
	private ComboBox cbDelemode = new ComboBox("Delivery Mode");
	private PopupDateField dfDeleDate = new PopupDateField("Delivery Date");

	// containers And Entities
	private MPemCmBank selectBank;
	private CompanyLookupDM selectLookUp;
	private BeanItemContainer<TPemCmEvalDetails> beans = null;
	private Long bankId;
	

	// pagination
	private int total = 0;
	// for header layoute
	private Label lblTableTitle;
	private Label lblFormTittle, lblFormTitle1, lblAddEdit;
	private Label lblSaveNotification, lblNotificationIcon;
	

	private HorizontalLayout hlButtonLayout1;
	// layout Components
	private VerticalLayout vlMainLayout = new VerticalLayout();
	private VerticalLayout vlSearchLayout = new VerticalLayout();
	private VerticalLayout vlTableLayout = new VerticalLayout();
	private VerticalLayout vlTableForm;
	private HorizontalLayout hlAddEditLayout, hlBreadCrumbs;

	private Logger logger = Logger.getLogger(DeliverDetails.class);

	public DeliverDetails() {

		companyId = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		userId = (Long) UI.getCurrent().getSession().getAttribute("userId");
		VerticalLayout clArgumentLayout = (VerticalLayout) UI.getCurrent().getSession()
				.getAttribute("clLayout");
		HorizontalLayout hlHeaderLayout = (HorizontalLayout) UI.getCurrent()
				.getSession().getAttribute("hlLayout");

		buildview(clArgumentLayout, hlHeaderLayout);
	}

	private void buildview(VerticalLayout clArgumentLayout,
			HorizontalLayout hlHeaderLayout) {
		clArgumentLayout.setStyleName(Runo.PANEL_LIGHT);
		hlHeaderLayout.removeAllComponents();

		btnCancel = new Button("Cancel", this);
		btnSearch = new Button("Search", this);
		btnApp = new Button("Save", this);
		btnApp.setStyleName("styles.css/buttonrefresh");
		btnView = new Button("View Document", this);
		btnedit = new Button("Edit", this);
		btnReset = new Button("Reset", this);
		btnBack = new Button("Home", this);
		btnBack.setStyleName("link");
		btnApp.setStyleName("savebt");
		btnCancel.addStyleName("cancelbt");
		btnView.addStyleName("view");
		btnedit.addStyleName("editbt");
		btnReset.addStyleName("resetbt");
		btnSearch.setStyleName("searchbt");
		btnView.setEnabled(false);
		btnedit.setEnabled(false);

		
		tblEvalDetails = new Table();
		tblEvalDetails.setStyleName(Runo.TABLE_SMALL);
		tblEvalDetails.setPageLength(15);
		tblEvalDetails.setSizeFull();
		tblEvalDetails.setFooterVisible(true);
		tblEvalDetails.setSelectable(true);
		tblEvalDetails.setImmediate(true);
		tblEvalDetails.setColumnCollapsingAllowed(true);

		tfSearchEvalNo = new TextField("Evaluation No");
		tfSearchEvalNo.setInputPrompt("Enter Evaluation No");
		tfSearchEvalNo.setWidth("200");

		tfSearCustName = new TextField("Customer name");
		tfSearCustName.setInputPrompt("Customer name");
		tfSearCustName.setWidth("200");

		tfevaluationNo = new TextField("Evaluation No");
		tfevaluationNo.setWidth("200px");
		tfevaluationNo.setInputPrompt("Evaluation No");
		tfEvaluationDate = new TextField("Evaluation Date");
		tfEvaluationDate.setWidth("200px");
		tfEvaluationDate.setInputPrompt("Evaluation Date");
		tfBankName = new TextField("Bank Name");
		tfBankName.setWidth("200px");
		tfBankName.setInputPrompt("Bank Name");

		tfBranchName = new TextField("Branch Name");
		tfBranchName.setWidth("200px");
		tfBranchName.setInputPrompt("Branch Name");

		tfCustomername = new TextField("Customer Name");
		tfCustomername.setWidth("200px");
		tfCustomername.setInputPrompt("Customer Name");

		tfInspectionDate = new TextField("Inspection Date");
		tfInspectionDate.setWidth("200px");
		tfInspectionDate.setInputPrompt("Inspection Date");

		tfinspectionBy = new TextField("Inspectioned By");
		tfinspectionBy.setWidth("200px");
		tfinspectionBy.setInputPrompt("Inspectioned By");
		tfVeriFiedBy = new TextField("Valuated By");
		tfVeriFiedBy.setWidth("200px");
		tfVeriFiedBy.setInputPrompt("Valuated By");

		tfDelecompany = new TextField("Delivered Company");
		tfDelecompany.setWidth("200px");
		tfDelecompany.setInputPrompt("Delivered Company");

		tfDeleBy = new TextField("Delivered By");
		tfDeleBy.setWidth("200px");
		tfDeleBy.setInputPrompt("Delivered By");

		dfDeleDate.addValidator(new DateValidation("Invalid date entered"));
		dfDeleDate.setImmediate(true);
		
		dfDeleDate.setDateFormat("dd-MMM-yyyy");
		dfDeleDate.setInputPrompt("Select Date");
		dfDeleDate.setValue(DateUtils.getcurrentdate());
		dfDeleDate.setWidth("180px");

		lblTableTitle = new Label();
		lblSaveNotification = new Label();
		lblSaveNotification.setContentMode(ContentMode.HTML);
		lblNotificationIcon = new Label();
		lblTableTitle.setValue("<B>&nbsp;&nbsp;Action:</B>");
		lblTableTitle.setContentMode(ContentMode.HTML);
		lblFormTittle = new Label();
		lblFormTittle.setContentMode(ContentMode.HTML);
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;Home");
		lblFormTitle1 = new Label();
		lblFormTitle1.setContentMode(ContentMode.HTML);

		lblFormTitle1.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;");
		lblAddEdit = new Label();
		lblAddEdit.setContentMode(ContentMode.HTML);

		cbDelemode.setInputPrompt(ApplicationConstants.selectDefault);
		cbDelemode.setItemCaptionPropertyId("lookupname");
		loadlookupList();
		cbDelemode.setInputPrompt("Select");
		cbDelemode.setNullSelectionAllowed(false);
		cbDelemode.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				final Object itemId = event.getProperty().getValue();
				if (itemId != null) {
					final BeanItem<?> item = (BeanItem<?>) cbDelemode
							.getItem(itemId);
					selectLookUp = (CompanyLookupDM) item.getBean();
					
				}
			}
		});
		cbDelemode.setImmediate(true);
		cbDelemode.setWidth("200px");
		cbSearchBankName.setInputPrompt(ApplicationConstants.selectDefault);
		cbSearchBankName.setItemCaptionPropertyId("bankName");
		loadBankList();
		cbSearchBankName
				.addValueChangeListener(new Property.ValueChangeListener() {

					public void valueChange(ValueChangeEvent event) {
						StreamResource sr =getPDFStream();

		                if (sr != null) {

		                   if (filedownloader == null) {
		                	   filedownloader = new FileDownloader(getPDFStream());
		                	   filedownloader.extend(btnView);
		                  } else {
		                	  filedownloader.setFileDownloadResource(sr);
		                 }
		                } else {
		                	lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
		        			lblSaveNotification.setValue("No document is there");
		               //   notif.show(Page.getCurrent());
		                   if (filedownloader != null) {
		                	   filedownloader.setFileDownloadResource(null); // reset
		                   }                       
		                   
		                }
						
						final Object itemId = event.getProperty().getValue();
						if (itemId != null) {
							final BeanItem<?> item = (BeanItem<?>) cbSearchBankName
									.getItem(itemId);
							selectBank = (MPemCmBank) item.getBean();
							bankId = selectBank.getBankId();
						}
					}
				});
		cbSearchBankName.setImmediate(true);
		cbSearchBankName.setNullSelectionAllowed(false);
		cbSearchBankName.setWidth("200px");

		// table panel

		HorizontalLayout flTableCaption = new HorizontalLayout();
		flTableCaption.addComponent(lblTableTitle);
		flTableCaption.setComponentAlignment(lblTableTitle,
				Alignment.MIDDLE_CENTER);
		flTableCaption.addStyleName("lightgray");
		flTableCaption.setHeight("25px");
		flTableCaption.setWidth("60px");

		HorizontalLayout hlTableTittleLayout = new HorizontalLayout();
		hlTableTittleLayout.addComponent(flTableCaption);
		hlTableTittleLayout.addComponent(btnedit);
		hlTableTittleLayout.addComponent(btnView);
		hlTableTittleLayout.setHeight("25px");
		hlTableTittleLayout.setSpacing(true);
		
		//Initialization and properties for btnDownload		
				btnDownload=new Button("Download");
				//btnDownload.setDescription("Download");
				btnDownload.addStyleName("downloadbt");
				btnDownload.addClickListener(new ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		        //  UI.getCurrent()..clearDashboardButtonBadge();
		                event.getButton().removeStyleName("unread");
		               if (notifications != null && notifications.getUI() != null)
		                    notifications.close();
		                else {
		                    buildNotifications(event);
		                UI.getCurrent().addWindow(notifications);
		                    notifications.focus();
		                    ((VerticalLayout) UI.getCurrent().getContent())
		                            .addLayoutClickListener(new LayoutClickListener() {
		                                @Override
		                                public void layoutClick(LayoutClickEvent event) {
		                                    notifications.close();
		                                    ((VerticalLayout) UI.getCurrent().getContent())
		                                            .removeLayoutClickListener(this);
		                                }
		                            });
		                }

		            }
		        });
				

			
				hlFileDownloadLayout = new HorizontalLayout();
				hlFileDownloadLayout.setSpacing(true);
				hlFileDownloadLayout.addComponent(btnDownload);
				hlFileDownloadLayout.setComponentAlignment(btnDownload,Alignment.MIDDLE_CENTER);

		

		HorizontalLayout hlTableTitleandCaptionLayout = new HorizontalLayout();
		hlTableTitleandCaptionLayout.addStyleName("topbarthree");
		hlTableTitleandCaptionLayout.setWidth("100%");
		hlTableTitleandCaptionLayout.addComponent(hlTableTittleLayout);
		hlTableTitleandCaptionLayout.addComponent(hlFileDownloadLayout);
		hlTableTitleandCaptionLayout.setComponentAlignment(hlFileDownloadLayout,Alignment.MIDDLE_RIGHT);
		hlTableTitleandCaptionLayout.setHeight("28px");

		hlAddEditLayout = new HorizontalLayout();
		hlAddEditLayout.addStyleName("topbarthree");
		hlAddEditLayout.setWidth("100%");
		hlAddEditLayout.addComponent(hlTableTitleandCaptionLayout);
		hlAddEditLayout.setHeight("28px");

		vlTableForm = new VerticalLayout();
		vlTableForm.setSizeFull();
		vlTableForm.setMargin(true);
		vlTableForm.addComponent(hlAddEditLayout);
		vlTableForm.addComponent(tblEvalDetails);
		vlTableLayout.setStyleName(Runo.PANEL_LIGHT);
		vlTableLayout.addComponent(vlTableForm);

		// search panel
		FormLayout flSearchForm1 = new FormLayout();
		flSearchForm1.addComponent(tfSearchEvalNo);
		FormLayout flSearchForm2 = new FormLayout();
		flSearchForm2.addComponent(cbSearchBankName);
		FormLayout flSearchForm3 = new FormLayout();
		flSearchForm3.addComponent(tfSearCustName);

		HorizontalLayout hlSearch = new HorizontalLayout();
		hlSearch.addComponent(flSearchForm1);
		hlSearch.addComponent(flSearchForm2);
		hlSearch.addComponent(flSearchForm3);

		hlSearch.setSpacing(true);
		hlSearch.setMargin(true);

		
		//Initialization and properties for btnDownload		
		btnDownload=new Button("Download");
		//btnDownload.setDescription("Download");
		btnDownload.addStyleName("downloadbt");
		btnDownload.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
        //  UI.getCurrent()..clearDashboardButtonBadge();
                event.getButton().removeStyleName("unread");
               if (notifications != null && notifications.getUI() != null)
                    notifications.close();
                else {
                    buildNotifications(event);
                UI.getCurrent().addWindow(notifications);
                    notifications.focus();
                    ((VerticalLayout) UI.getCurrent().getContent())
                            .addLayoutClickListener(new LayoutClickListener() {
                                @Override
                                public void layoutClick(LayoutClickEvent event) {
                                    notifications.close();
                                    ((VerticalLayout) UI.getCurrent().getContent())
                                            .removeLayoutClickListener(this);
                                }
                            });
                }

            }
        });
		
		VerticalLayout hlSearchButtonLayout = new VerticalLayout();
		hlSearchButtonLayout.setSpacing(true);
		hlSearchButtonLayout.addComponent(btnSearch);
		hlSearchButtonLayout.addComponent(btnReset);
		hlSearchButtonLayout.setWidth("100");
		hlSearchButtonLayout.addStyleName("topbarthree");
		hlSearchButtonLayout.setMargin(true);

		HorizontalLayout hlSearchComponentandButtonLayout = new HorizontalLayout();
		hlSearchComponentandButtonLayout.setSizeFull();
		hlSearchComponentandButtonLayout.setSpacing(true);
		hlSearchComponentandButtonLayout.addComponent(hlSearch);
		hlSearchComponentandButtonLayout.setComponentAlignment(hlSearch,
				Alignment.MIDDLE_LEFT);
		hlSearchComponentandButtonLayout.addComponent(hlSearchButtonLayout);
		hlSearchComponentandButtonLayout.setComponentAlignment(
				hlSearchButtonLayout, Alignment.MIDDLE_RIGHT);
		hlSearchComponentandButtonLayout.setExpandRatio(hlSearchButtonLayout, 1);

		/*GridLayout glSearchPanel = new GridLayout();
		glSearchPanel.setSpacing(true);
		glSearchPanel.setColumns(2);
		glSearchPanel.addComponent(hlSearch);
		glSearchPanel.addComponent(hlSearchButtonLayout);
		glSearchPanel.setComponentAlignment(hlSearchButtonLayout, Alignment.MIDDLE_CENTER);
		glSearchPanel.setSizeFull();*/
		
		final VerticalLayout vlSearchPanel = new VerticalLayout();
		vlSearchPanel.setSpacing(true);
		vlSearchPanel.setSizeFull();
		vlSearchPanel.addComponent(hlSearchComponentandButtonLayout);
		vlSearchLayout = new VerticalLayout();
		vlSearchLayout.addComponent(PanelGenerator.createPanel(vlSearchPanel));
		vlSearchLayout.setMargin(true);
		vlTableLayout.setStyleName(Runo.PANEL_LIGHT);

		// main Panel
		FormLayout flMainform1 = new FormLayout();
		FormLayout flMainform2 = new FormLayout();
		FormLayout flMainform3 = new FormLayout();
		FormLayout flMainform4 = new FormLayout();

		flMainform1.setSpacing(true);
		flMainform2.setSpacing(true);
		flMainform3.setSpacing(true);
		flMainform4.setSpacing(true);
		flMainform1.addComponent(tfevaluationNo);
		flMainform1.addComponent(tfEvaluationDate);
		flMainform1.addComponent(tfBankName);
		flMainform1.addComponent(tfBranchName);
		flMainform2.addComponent(tfCustomername);
		flMainform2.addComponent(tfInspectionDate);
		flMainform2.addComponent(tfinspectionBy);
		flMainform2.addComponent(tfVeriFiedBy);

		flMainform3.addComponent(dfDeleDate);
		flMainform3.addComponent(cbDelemode);
		flMainform3.addComponent(tfDelecompany);
		flMainform3.addComponent(tfDeleBy);

		HorizontalLayout hlForm = new HorizontalLayout();
		hlForm.setSpacing(true);
		hlForm.addComponent(flMainform1);
		hlForm.addComponent(flMainform2);
		hlForm.addComponent(flMainform3);
		hlForm.setSpacing(true);

		final GridLayout glGridLayout1 = new GridLayout(1, 2);
		glGridLayout1.setSizeFull();
		glGridLayout1.setSpacing(true);
		glGridLayout1.setMargin(true);
		glGridLayout1.addComponent(hlForm);

		vlMainLayout = new VerticalLayout();
		vlMainLayout.addComponent(PanelGenerator.createPanel(glGridLayout1));
		vlMainLayout.setMargin(true);
		vlMainLayout.setVisible(false);
		// css mainLaout
		clArgumentLayout.addComponent(vlMainLayout);
		clArgumentLayout.addComponent(vlSearchLayout);
		clArgumentLayout.addComponent(vlTableLayout);

		// HeaderPanel
		hlButtonLayout1 = new HorizontalLayout();
		hlButtonLayout1.addComponent(btnApp);
		hlButtonLayout1.addComponent(btnCancel);
		hlButtonLayout1.setVisible(false);

		hlBreadCrumbs = new HorizontalLayout();
		hlBreadCrumbs.addComponent(lblFormTitle1);
		hlBreadCrumbs.addComponent(btnBack);
		hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
		hlBreadCrumbs.addComponent(lblAddEdit);
		hlBreadCrumbs
				.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER);
		hlBreadCrumbs.setVisible(false);

		HorizontalLayout hlNotificationLayout = new HorizontalLayout();
		hlNotificationLayout.addComponent(lblNotificationIcon);
		hlNotificationLayout.setComponentAlignment(lblNotificationIcon,
				Alignment.MIDDLE_CENTER);
		hlNotificationLayout.addComponent(lblSaveNotification);
		hlNotificationLayout.setComponentAlignment(lblSaveNotification,
				Alignment.MIDDLE_CENTER);
		hlHeaderLayout.addComponent(lblFormTittle);
		hlHeaderLayout.setComponentAlignment(lblFormTittle,
				Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlBreadCrumbs);
		hlHeaderLayout.setComponentAlignment(hlBreadCrumbs,
				Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlNotificationLayout);
		hlHeaderLayout.setComponentAlignment(hlNotificationLayout,
				Alignment.MIDDLE_CENTER);
		hlHeaderLayout.addComponent(hlButtonLayout1);
		hlHeaderLayout.setComponentAlignment(hlButtonLayout1,
				Alignment.MIDDLE_RIGHT);
		populatedAndConfig(false);

	}

	private void buildNotifications(ClickEvent event) {
		notifications = new Window();
		VerticalLayout l = new VerticalLayout();
		l.setMargin(true);
		l.setSpacing(true);
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
		//vlDownload.setSpacing(true);

		notifications.setContent(vlDownload);

	}
	


	/*
	 * populatedAndConfig()-->this function is used for populationg the records
	 * to Grid table
	 */
	public void populatedAndConfig(boolean search) {

		try {
			tblEvalDetails.removeAllItems();
			List<TPemCmEvalDetails> evalList = null;
			evalList = new ArrayList<TPemCmEvalDetails>();
			if (search) {
				String evalNo = tfSearchEvalNo.getValue();
				String custName = tfSearCustName.getValue();

				if (evalNo != null || bankId != null || companyId != null
						|| userId != null || custName != null) {

					evalList = beanEvaluation.getSearchEvalDetailnList(null,
							evalNo, null, custName, null, bankId,
							companyId, "Approved");
					total = evalList.size();

				}
			} else {

				evalList = beanEvaluation.getSearchEvalDetailnList(null, null,
						null, null, null, null, companyId, "Approved");
				total = evalList.size();

			}

			beans = new BeanItemContainer<TPemCmEvalDetails>(
					TPemCmEvalDetails.class);
			beans.addAll(evalList);

			tblEvalDetails.setContainerDataSource(beans);
			tblEvalDetails.setSelectable(true);
			tblEvalDetails.setVisibleColumns(new Object[] { "docId", "evalNo",
					"evalDate", "bankName", "custName", "docStatus",
					"lastUpdtedBy", "lastUpdateDt" });
			tblEvalDetails.setColumnHeaders(new String[] { "Ref.Id",
					"Evaluation Number", "Evaluation Date", "Bank Name",
					"Customer Name", "Status", "Last Updated By",
					"Last Updated Date" });
			tblEvalDetails.setColumnFooter("lastUpdateDt", "No.of Records : "
					+ total);
			tblEvalDetails.addItemClickListener(new ItemClickListener() {
				private static final long serialVersionUID = 1L;

				public void itemClick(ItemClickEvent event) {
					if (tblEvalDetails.isSelected(event.getItemId())) {

						btnView.setEnabled(false);
						btnedit.setEnabled(false);
					} else {

						btnView.setEnabled(true);
						btnedit.setEnabled(true);

					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->"
					+ e);
		}
		getExportTableDetails();
	}

	/*
	 * editEvaluationsDetails()-->this function is used for view components
	 */
	private void editEaluationDetails() {
		Item itselect = tblEvalDetails.getItem(tblEvalDetails.getValue());
		if (itselect != null) {
			TPemCmEvalDetails edit = beans.getItem(tblEvalDetails.getValue())
					.getBean();

			if (edit.getEvalNo() != null) {
				tfevaluationNo.setReadOnly(false);
				tfevaluationNo.setValue(edit.getEvalNo());
				tfevaluationNo.setReadOnly(true);
			} else {
				tfevaluationNo.setReadOnly(false);
				tfevaluationNo.setValue("");
				tfevaluationNo.setReadOnly(true);
			}
			if (edit.getEvalDate() != null) {
				tfEvaluationDate.setReadOnly(false);
				tfEvaluationDate.setValue(edit.getEvalDate());
				tfEvaluationDate.setReadOnly(true);
			} else {
				tfEvaluationDate.setReadOnly(false);
				tfEvaluationDate.setValue("");
				tfEvaluationDate.setReadOnly(true);
			}
			if (edit.getBankName() != null) {
				tfBankName.setReadOnly(false);
				tfBankName.setValue(edit.getBankName());
				tfBankName.setReadOnly(true);
			} else {
				tfBankName.setReadOnly(false);
				tfBankName.setValue("");
				tfBankName.setReadOnly(true);
			}
			if (edit.getBankBranch() != null) {
				tfBranchName.setReadOnly(false);
				tfBranchName.setValue(edit.getBankBranch());
				tfBranchName.setReadOnly(true);
			} else {
				tfBranchName.setReadOnly(false);
				tfBranchName.setValue("");
				tfBranchName.setReadOnly(true);
			}
			if (edit.getCustName() != null) {
				tfCustomername.setReadOnly(false);
				tfCustomername.setValue(edit.getCustName());
				tfCustomername.setReadOnly(true);
			} else {
				tfCustomername.setReadOnly(false);
				tfCustomername.setValue("");
				tfCustomername.setReadOnly(true);
			}
			if (edit.getInspectionDt() != null) {
				tfInspectionDate.setReadOnly(false);
				tfInspectionDate.setValue(edit.getInspectionDt() + "");
				tfInspectionDate.setReadOnly(true);
			} else {
				tfInspectionDate.setReadOnly(false);
				tfInspectionDate.setValue("");
				tfInspectionDate.setReadOnly(true);
			}
			if (edit.getInspectionBy() != null) {
				tfinspectionBy.setReadOnly(false);
				tfinspectionBy.setValue(edit.getInspectionBy());
				tfinspectionBy.setReadOnly(true);
			} else {
				tfinspectionBy.setReadOnly(false);
				tfinspectionBy.setValue("");
				tfinspectionBy.setReadOnly(true);
			}
			if (edit.getValuationBy() != null) {
				tfVeriFiedBy.setReadOnly(false);
				tfVeriFiedBy.setValue(edit.getValuationBy());
				tfVeriFiedBy.setReadOnly(true);
			} else {
				tfVeriFiedBy.setReadOnly(false);
				tfVeriFiedBy.setValue("");
				tfVeriFiedBy.setReadOnly(true);
			}

		}
	}

	/*
	 * loadBankList()-->this function is used for load bank list
	 */
	private void loadBankList() {
		try {
			List<MPemCmBank> allList=new ArrayList<MPemCmBank>();
			MPemCmBank all=new MPemCmBank();
			all.setBankName("All Banks");
			all.setBankId(0L);
			allList.add(all);
			
			List<MPemCmBank> bankList = beanbank.getBankDtlsList(companyId,"Active",null);
			for(MPemCmBank obj:bankList){
				allList.add(obj);
			}
			BeanItemContainer<MPemCmBank> prodList = new BeanItemContainer<MPemCmBank>(MPemCmBank.class);
			prodList.addAll(allList);
			cbSearchBankName.setContainerDataSource(prodList);
			 cbSearchBankName.setValue(all);
			 } catch (Exception e) {

			logger.error("fn_loadBankList_Exception Caught->" + e);

		}
	}
	/*
	 * loadBankList()-->this function is used for load bank list
	 */
	private void loadlookupList(){
		try {
			//List<MBaseLookup> list = lookUpBean.getMBaseLookupList("DELE_MODE", null, "Active");

			List<CompanyLookupDM> list = lookUpBean.getCompanyLookupList(companyId, null, "DELE_MODE", null, null,  "Active");

			BeanItemContainer<CompanyLookupDM> prodList = new BeanItemContainer<CompanyLookupDM>(
					CompanyLookupDM.class);

		
			prodList.addAll(list);
			cbDelemode.setContainerDataSource(prodList);
		} catch (Exception e) {

			logger.error("fn_loadlookupList_Exception Caught->" + e);

		}
	}

	private void resetDeliveryFields(){
		hlBreadCrumbs.setVisible(false);
		lblFormTittle.setVisible(true);
		tfDelecompany.setValue("");
		tfDeleBy.setValue("");
		dfDeleDate.setValue(null);
		cbDelemode.setValue(null);
		tfevaluationNo.setReadOnly(false);
			tfevaluationNo.setValue("");
			tfevaluationNo.setReadOnly(true);
			tfEvaluationDate.setReadOnly(false);
			tfEvaluationDate.setValue("");
			tfEvaluationDate.setReadOnly(true);
			tfBankName.setReadOnly(false);
			tfBankName.setValue("");
			tfBankName.setReadOnly(true);
			tfBranchName.setReadOnly(false);
			tfBranchName.setValue("");
			tfBranchName.setReadOnly(true);
			tfCustomername.setReadOnly(false);
			tfCustomername.setValue("");
			tfCustomername.setReadOnly(true);
			tfInspectionDate.setReadOnly(false);
			tfInspectionDate.setValue("");
			tfInspectionDate.setReadOnly(true);
			tfinspectionBy.setReadOnly(false);
			tfinspectionBy.setValue("");
			tfinspectionBy.setReadOnly(true);
			tfVeriFiedBy.setReadOnly(false);
			tfVeriFiedBy.setValue("");
			tfVeriFiedBy.setReadOnly(true);
		
	}
	
	/*
	 * resetFields()-->this function is used for resetmaindComponents fileds
	 */
	private void resetFields() {

		lblNotificationIcon.setIcon(null);
		lblSaveNotification.setValue("");
		hlBreadCrumbs.setVisible(false);
		lblFormTittle.setVisible(true);
		tfDelecompany.setValue("");
		tfDeleBy.setValue("");
		dfDeleDate.setValue(null);
		cbDelemode.setValue(null);
		tfevaluationNo.setReadOnly(false);
			tfevaluationNo.setValue("");
			tfevaluationNo.setReadOnly(true);
			tfEvaluationDate.setReadOnly(false);
			tfEvaluationDate.setValue("");
			tfEvaluationDate.setReadOnly(true);
			tfBankName.setReadOnly(false);
			tfBankName.setValue("");
			tfBankName.setReadOnly(true);
			tfBranchName.setReadOnly(false);
			tfBranchName.setValue("");
			tfBranchName.setReadOnly(true);
			tfCustomername.setReadOnly(false);
			tfCustomername.setValue("");
			tfCustomername.setReadOnly(true);
			tfInspectionDate.setReadOnly(false);
			tfInspectionDate.setValue("");
			tfInspectionDate.setReadOnly(true);
			tfinspectionBy.setReadOnly(false);
			tfinspectionBy.setValue("");
			tfinspectionBy.setReadOnly(true);
			tfVeriFiedBy.setReadOnly(false);
			tfVeriFiedBy.setValue("");
			tfVeriFiedBy.setReadOnly(true);
		
	}
	private void getExportTableDetails()
	{
		excelexporter.setTableToBeExported(tblEvalDetails);
		csvexporter.setTableToBeExported(tblEvalDetails);
		pdfexporter.setTableToBeExported(tblEvalDetails);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
		
	}


	/*
	 * Method for File Download
	 */
	private StreamResource getPDFStream() {
		try{
			StreamResource.StreamSource source = new StreamResource.StreamSource() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public InputStream getStream() {
					TPemCmEvalDetails edit = beans.getItem(
							tblEvalDetails.getValue()).getBean();
					if (edit.getEvalDoc() != null) {

						return new ByteArrayInputStream(edit.getEvalDoc());
					} else {
						return null;
					}

				}
			};
			TPemCmEvalDetails edit = beans.getItem(tblEvalDetails.getValue())
					.getBean();
			StreamResource resource = new StreamResource(source, edit.getEvalNo()+"_"+edit.getCustName()+"_"+edit.getDoctype()+
					 ".doc");
			return resource;
	}
	catch(Exception e){
		return null;
	}
		}

	/*
	 * Listener Function for button clickevents
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		notifications.close();
		if (btnedit == event.getButton()) {
			try {
				editEaluationDetails();
			} catch (Exception e) {
			
				logger.error("Error thorws in editEaluationDetails() function--->"
						+ e);
			}
			dfDeleDate.setValue(DateUtils.getcurrentdate());
			cbDelemode.setValue(null);
			tfDelecompany.setValue("");
			tfDeleBy.setValue("");
			vlMainLayout.setVisible(true);
			vlSearchLayout.setVisible(false);
			vlTableLayout.setVisible(true);
			tblEvalDetails.setPageLength(10);
			hlButtonLayout1.setVisible(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Modify");
			lblAddEdit.setVisible(true);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
		} else if (btnCancel == event.getButton()) {
			resetFields();
			vlMainLayout.setVisible(false);
			vlSearchLayout.setVisible(true);
			vlTableLayout.setVisible(true);
			tblEvalDetails.setValue(null);
			hlButtonLayout1.setVisible(false);
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Search");
			lblAddEdit.setVisible(false);
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			tblEvalDetails.setPageLength(15);
			populatedAndConfig(false);
			btnedit.setEnabled(false);
			btnView.setEnabled(false);
			 hlFileDownloadLayout.removeAllComponents();
				hlFileDownloadLayout.addComponent(btnDownload);
				getExportTableDetails();
		
		} else if (btnApp == event.getButton()) {
			try {
				TPemCmEvalDetails edit = beans.getItem(
						tblEvalDetails.getValue()).getBean();

				if (edit.getDocId() != null) {
					String deleDate;
					try{
					 deleDate =DateUtils.datetostring(dfDeleDate.getValue());
					}catch(Exception e){
						deleDate=null;
					}
					String delemode= selectLookUp.getLookupname();
					String deleCompany=tfDelecompany
							.getValue();
					String deleBy=tfDeleBy.getValue();
					
					//
					System.out.println("deleDate"+deleDate);
					System.out.println("deleDate"+deleDate);
					System.out.println("delemode"+delemode);
					System.out.println("deleCompany"+deleCompany);
					System.out.println("deleBy"+deleBy);
					beanEvaluation.updateDocStatus(edit.getDocId(),"Delivered", null,deleDate,delemode, deleCompany,deleBy);
				}
				
				resetDeliveryFields();
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblSaveNotification.setValue("Successfully Saved");
				populatedAndConfig(false);
			} catch (Exception e) {
				e.printStackTrace();
				lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
				lblSaveNotification
						.setValue("Saved failed, please check the data and try again ");
				logger.info("Error on saveDepartment() function--->" + e);
			}
		} else if (btnSearch == event.getButton()) {
			populatedAndConfig(true);
			if (total == 0) {
				lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
				lblSaveNotification.setValue("No Records found");
			} else {
				lblNotificationIcon.setIcon(null);
				lblSaveNotification.setValue("");
			}
			 hlFileDownloadLayout.removeAllComponents();
				hlFileDownloadLayout.addComponent(btnDownload);
				getExportTableDetails();
		
		} else if (btnReset == event.getButton()) {
			populatedAndConfig(false);
			tfSearchEvalNo.setValue("");
			cbSearchBankName.setValue(null);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
			tfSearCustName.setValue("");
			bankId = null;
		} else if (btnBack == event.getButton()) {
			resetFields();
			vlMainLayout.setVisible(false);
			vlSearchLayout.setVisible(true);
			vlTableLayout.setVisible(true);
			hlButtonLayout1.setVisible(false);
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Search");
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			lblAddEdit.setVisible(false);
		}
		else if (btnDownload == event.getButton()) {

			event.getButton().removeStyleName("unread");

			if (notifications != null && notifications.getUI() != null)
				notifications.close();
			else {
				buildNotifications(event);
				UI.getCurrent().addWindow(notifications);
				notifications.focus();
				((VerticalLayout) UI.getCurrent().getContent())
						.addLayoutClickListener(new LayoutClickListener() {
							@Override
							public void layoutClick(LayoutClickEvent event) {
								notifications.close();
								((VerticalLayout) UI.getCurrent().getContent())
										.removeLayoutClickListener(this);
							}
						});
			}
			
		}

	}

}
