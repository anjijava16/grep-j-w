
/**
 * File Name	:	DocApprovalScreenApp.java
 * Description	:	Used for approval process to T_PEM_CM_EVAL_DETAILS
 * Author		:	Prakash.s
 * Date			:	mar 27, 2014
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.base.domain.mst.AppScreensDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.domain.mst.UserDM;
import com.gnts.base.domain.mst.UserFavDM;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.base.service.mst.UserFavService;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.pem.domain.mst.CommissionSetupDM;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.rpt.TPemCmBillDtls;
import com.gnts.pem.domain.txn.BillDetails;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.domain.txn.common.TPemCmOwnerDetails;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.rpt.CmBillDtlsService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;
import com.gnts.pem.service.txn.common.CmOwnerDetailsService;
import com.gnts.pem.util.UIFlowData;
import com.gnts.pem.util.XMLUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class Approvals implements ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CmEvalDetailsService beanEvaluation=(CmEvalDetailsService)SpringContextHelper.getBean("evalDtls");
	private CmBankService beanbank=(CmBankService)SpringContextHelper.getBean("bank");
	private UserFavService beanFavor=(UserFavService) SpringContextHelper.getBean("userfavourites");
	private static CmBillDtlsService beanBill=(CmBillDtlsService) SpringContextHelper
			.getBean("billDtls");
	private CmOwnerDetailsService  beanOwner= (CmOwnerDetailsService) SpringContextHelper.getBean("ownerDtls");
	private static SlnoGenService beanSlno = (SlnoGenService) SpringContextHelper
			.getBean("slnogen");
	private Table tblEvalDetails;
	private Long companyId,currencyId;
	private String screenName,loginusername;
	private Long userId,screenId;
	private Long billid,selectedbankid;
	private String strBillXsl ="Bill.xsl";
	// Buttons
	private Button btnedit,btnView,btnBill;
	private Button btnCancel;
	private Button btnSearch, btnReset;
	private Button btnApp;
	private Button btnBack,btnFavor;

	private Button btnDownload;
	  HorizontalLayout hlFileDownloadLayout;
	//Declaration for Exporter
	private Window notifications=new Window();
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	// components
	private TextField tfSearchEvalNo,tfSearCustName;
	private ComboBox cbSearchBankName = new ComboBox("Bank Name");
	private ComboBox cbSearchStatus =  new ComboBox("Status");
	private TextField tfVeriFiedBy,tfevaluationNo,tfEvaluationDate,tfBankName,tfBranchName,tfCustomername,tfInspectionDate,tfinspectionBy;
	private TextArea tfrejectRemarks= new TextArea("Reject Remarks");
	private ComboBox cbstatus = new ComboBox("Status");
	private TextField tfReductionAmount = new TextField("Discount");
	
	// containers And Entities
	private MPemCmBank selectBank;
	private BeanItemContainer<TPemCmEvalDetails> beans = null;
	private BeanItemContainer<BillDetails> beansBillDetails = null;

	private Long bankId;
	
	//for report
	UIFlowData uiflowdata =new UIFlowData();

	// pagination
	private int total = 0;
	// for header layoute
	private Label lblTableTitle;
	private Label lblFormTittle,lblFormTitle1,lblAddEdit;
	private Label lblSaveNotification, lblNotificationIcon;
	private FileDownloader filedownloader;
	private FileDownloader billDownloader;
	private HorizontalLayout hlButtonLayout1;
	// layout Components
	private VerticalLayout vlMainLayout = new VerticalLayout();
	private VerticalLayout vlSearchLayout = new VerticalLayout();
	private VerticalLayout vlTableLayout = new VerticalLayout();
	private VerticalLayout vlTableForm;
	private HorizontalLayout hlAddEditLayout,hlBreadCrumbs;

	

	private Logger logger = Logger.getLogger(Approvals.class);

	public Approvals() {
		loginusername = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		
		if(UI.getCurrent().getSession()
				.getAttribute("currenyId")!=null){
		
		currencyId = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("currenyId").toString());
		}
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		userId = (Long) UI.getCurrent().getSession().getAttribute("userId");
		screenId=(Long)UI.getCurrent().getSession().getAttribute("appScreenId");
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
		
		
		tfrejectRemarks.setWidth("200");
		//tfrejectRemarks.setInputPrompt(ApplicationConstants.selectDefault);
		tfReductionAmount.setWidth("200");
		btnCancel = new Button("Cancel", this);
		btnSearch = new Button("Search", this);
		btnApp = new Button("Save", this);
		btnApp.setStyleName("styles.css/buttonrefresh");
		btnView = new Button("View Document", this);
		btnBill = new Button("View Bill",this);
		btnedit=new Button("Edit",this);
		btnReset = new Button("Reset", this);
		btnBack=new Button("Home",this);
		btnFavor =new Button("Favour",this);
		btnBack.setStyleName("link");
		btnApp.setStyleName("savebt");
		btnCancel.addStyleName("cancelbt");
		btnView.addStyleName("view");
		btnBill.setStyleName("view");
		btnedit.addStyleName("editbt");
		btnReset.addStyleName("resetbt");
		btnSearch.setStyleName("searchbt");
		btnFavor.setStyleName("addfavbt");
		btnView.setEnabled(false);
		btnedit.setEnabled(false);
		btnBill.setEnabled(false);
		

		tblEvalDetails = new Table();
		tblEvalDetails.setStyleName(Runo.TABLE_SMALL);
		tblEvalDetails.setPageLength(14);
		tblEvalDetails.setSizeFull();
		tblEvalDetails.setFooterVisible(true);
		tblEvalDetails.setSelectable(true);
		tblEvalDetails.setImmediate(true);
		tblEvalDetails.setColumnCollapsingAllowed(true);
		
		tfSearchEvalNo = new TextField("Evaluation No");
	//	tfSearchEvalNo.setInputPrompt("Enter Evaluation No");
		tfSearchEvalNo.setWidth("150");
		
		tfSearCustName = new TextField("Customer name");
	//	tfSearCustName.setInputPrompt("Customer name");
		tfSearCustName.setWidth("150");
		
		
		tfevaluationNo = new TextField("Evaluation No");
		tfevaluationNo.setWidth("170");
		tfevaluationNo.setInputPrompt("Evaluation No");
		tfEvaluationDate = new TextField("Evaluation Date");
		tfEvaluationDate.setWidth("170");
		tfEvaluationDate.setInputPrompt("Evaluation Date");
		tfBankName = new TextField("Bank Name");
		tfBankName.setWidth("170");
		tfBankName.setInputPrompt("Bank Name");
		
		tfBranchName = new TextField("Branch Name");
		tfBranchName.setWidth("170");
		tfBranchName.setInputPrompt("Branch Name");
		
		tfCustomername = new TextField("Customer Name");
		tfCustomername.setWidth("170");
		tfCustomername.setInputPrompt("Customer Name");
		
		tfInspectionDate = new TextField("Inspection Date");
		tfInspectionDate.setWidth("170");
		tfInspectionDate.setInputPrompt("Inspection Date");
		
		tfinspectionBy = new TextField("Inspectioned By");
		tfinspectionBy.setWidth("170");
		tfinspectionBy.setInputPrompt("Inspectioned By");
		tfVeriFiedBy = new TextField("Valuated By");
		tfVeriFiedBy.setWidth("170");
		tfVeriFiedBy.setInputPrompt("Valuated By");
		tfrejectRemarks.setInputPrompt("Remarks");
		tfReductionAmount.setInputPrompt("Discount");

	

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
		lblFormTitle1=new Label();
		lblFormTitle1.setContentMode(ContentMode.HTML);

		lblFormTitle1.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;");
		lblAddEdit=new Label();
		lblAddEdit.setContentMode(ContentMode.HTML);
		tfrejectRemarks.setVisible(false);
		tfReductionAmount.setValue("0.0");
		tfReductionAmount.setVisible(false);
		tfReductionAmount.setImmediate(true);
		tfReductionAmount.addValueChangeListener(new Property.ValueChangeListener() {

			@SuppressWarnings("deprecation")
			public void valueChange(ValueChangeEvent event) {
				try{
					if(tfReductionAmount.getValue()!=null && tfReductionAmount.getValue().trim().length()>0){
						tfReductionAmount.addValidator(new DoubleValidator("Enter number only"));
					}
					else{
					tfReductionAmount.setComponentError(null);
					}
				}catch(Exception e){
					
				}
			}
		});
//		cbstatus.addItem(RecordStatus.DOC_APPROVED);
//		cbstatus.addItem(RecordStatus.DOC_REJECTED);
		cbstatus.setInputPrompt("Select");
		cbstatus.setNullSelectionAllowed(false);
		cbstatus
		.addValueChangeListener(new Property.ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
		
				final String itemId = (String) event.getProperty().getValue();
				if(itemId==RecordStatus.DOC_APPROVED){
					tfrejectRemarks.setReadOnly(false);
					tfrejectRemarks.setValue("");
					tfrejectRemarks.setReadOnly(true);
					tfrejectRemarks.setVisible(false);
					
					tfReductionAmount.setVisible(true);
					tfReductionAmount.setReadOnly(false);
					tfReductionAmount.setValue("0.0");
				}
				else{
					tfReductionAmount.setReadOnly(false);
					tfReductionAmount.setValue("0.0");
					tfReductionAmount.setReadOnly(true);
					tfReductionAmount.setVisible(false);
					
					tfrejectRemarks.setVisible(true);
					tfrejectRemarks.setReadOnly(false);
					tfrejectRemarks.setValue("");
				}
			}
		});
		cbstatus.setImmediate(true);
		cbSearchBankName.setInputPrompt(ApplicationConstants.selectDefault);
		cbSearchBankName.setItemCaptionPropertyId("bankName");
		loadBankList();
		cbSearchBankName
				.addValueChangeListener(new Property.ValueChangeListener() {
	
					public void valueChange(ValueChangeEvent event) {

						final Object itemId = event.getProperty().getValue();
						if (itemId != null) {
							final BeanItem<?> item = (BeanItem<?>) cbSearchBankName
									.getItem(itemId);
							selectBank = (MPemCmBank) item
									.getBean();
							bankId=selectBank.getBankId();
						}
					}
				});
		cbSearchBankName.setImmediate(true);
		cbSearchBankName.setNullSelectionAllowed(false);
		cbSearchBankName.setWidth("170");
		
		cbSearchStatus = new ComboBox("Status");
		//cbSearchStatus.setInputPrompt(ApplicationConstants.selectDefault);
		cbSearchStatus.setItemCaptionPropertyId("desc");
		cbSearchStatus.setImmediate(true);
		cbSearchStatus.setNullSelectionAllowed(false);
		beansBillDetails = new BeanItemContainer<BillDetails>(BillDetails.class);
		//beansBillDetails.addAll(RecordStatus.listBillDtls);
	//	beansBillDetails.removeItem(new BillDetails(RecordStatus.BILL_DTLS_PAID_CODE,RecordStatus.BILL_DTLS_PAID_DESC));
		cbSearchStatus.setContainerDataSource(beansBillDetails);
		cbSearchStatus.setWidth("150");
		//cbSearchStatus.setValue(RecordStatus.getBillDetails(RecordStatus.BILL_DTLS_PEND_CODE));

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
		hlTableTittleLayout.addComponent(btnBill);
		hlTableTittleLayout.setHeight("25px");
		hlTableTittleLayout.setSpacing(true);
		
		
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
		FormLayout flSearchForm4 = new FormLayout();
		flSearchForm4.addComponent(cbSearchStatus);
		
		HorizontalLayout hlSearch = new HorizontalLayout();
		hlSearch.addComponent(flSearchForm1);
		hlSearch.addComponent(flSearchForm2);
		hlSearch.addComponent(flSearchForm3);
		hlSearch.addComponent(flSearchForm4);
		hlSearch.setSpacing(true);
		hlSearch.setMargin(true);

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

	/*	GridLayout glSearchPanel = new GridLayout();
		glSearchPanel.setSpacing(true);
		glSearchPanel.setColumns(2);
		glSearchPanel.addComponent(hlSearch);
		glSearchPanel.addComponent(hlSearchButtonLayout);
		glSearchPanel.setComponentAlignment(hlSearchButtonLayout, Alignment.MIDDLE_CENTER);
		glSearchPanel.setSizeFull();
		*/
		final VerticalLayout vlSearchPanel = new VerticalLayout();
		vlSearchPanel.setSpacing(true);
		vlSearchPanel.setSizeFull();
		vlSearchPanel.addComponent(hlSearchComponentandButtonLayout);
		vlSearchLayout = new VerticalLayout();
		//vlSearchLayout.addComponent(PanelGenerator.createPanel(vlSearchPanel));
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
		
		flMainform3.addComponent(cbstatus);
		flMainform3.addComponent(tfrejectRemarks);
		flMainform3.addComponent(tfReductionAmount);
		

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
		//vlMainLayout.addComponent(PanelGenerator.createPanel(glGridLayout1));
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
		hlButtonLayout1.addComponent(btnFavor);
		hlButtonLayout1.setVisible(false);
		
		hlBreadCrumbs=new HorizontalLayout();
		hlBreadCrumbs.addComponent(lblFormTitle1);
		hlBreadCrumbs.addComponent(btnBack);
		hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
		hlBreadCrumbs.addComponent(lblAddEdit);
		hlBreadCrumbs.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER);
		hlBreadCrumbs.setVisible(false);
		
		HorizontalLayout hlNotificationLayout = new HorizontalLayout();
		hlNotificationLayout.addComponent(lblNotificationIcon);
		hlNotificationLayout.setComponentAlignment(lblNotificationIcon,
				Alignment.MIDDLE_CENTER);
		hlNotificationLayout.addComponent(lblSaveNotification);
		hlNotificationLayout.setComponentAlignment(lblSaveNotification,
				Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(lblFormTittle);
		hlHeaderLayout.setComponentAlignment(lblFormTittle,
				Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlBreadCrumbs);
		hlHeaderLayout.setComponentAlignment(hlBreadCrumbs,
				Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlNotificationLayout);
		hlHeaderLayout.setComponentAlignment(hlNotificationLayout,
				Alignment.MIDDLE_LEFT);
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
	 * addFavourities()-->This function used to add the screen in Add to Favourities table
	 * 
	 */
public void addFavourities(){
try{	
	//List<UserFavDM> list=beanFavor.getUserFavouritesList(userId, screenId);
	//if(list.size()==0){
		UserFavDM favourObj=new UserFavDM();
	UserDM userid=new UserDM();
	userid.setUserid(userId);
	//favourObj.setUserId(userid);
	AppScreensDM screenid=new AppScreensDM();
	screenid.setScreenId(screenId);
	//favourObj.setScreenId(screenid);
	favourObj.setLastUpdatedBy(loginusername);
	favourObj.setLastUpdatedDt(new Date());
beanFavor.saveorUpdateUserFavouitiesDetails(favourObj);
}catch(Exception e){
	
}
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
				String custName=tfSearCustName.getValue();
				String strStatus = null;
				BillDetails status = (BillDetails) cbSearchStatus.getValue();
				try {
					strStatus = status.getCode();
					
				} catch (Exception e) {

				}
//				if(strStatus==null){
//					strStatus=RecordStatus.BILL_DTLS_PEND_CODE;
//				}
				if (evalNo != null || bankId != null || companyId != null
						|| userId != null||custName!=null || strStatus!=null) {
					evalList = beanEvaluation.getSearchEvalDetailnList(null, evalNo, null, custName, null, bankId, companyId,strStatus);
					total = evalList.size();

				}
				
//			} else {
//				cbSearchStatus.setValue(RecordStatus.BILL_DTLS_PEND_CODE);
//				evalList = beanEvaluation.getSearchEvalDetailnList(null, null, null, null, null, null, companyId,RecordStatus.BILL_DTLS_PEND_DESC);
//				total = evalList.size();
//
//			}
//			
			beans = new BeanItemContainer<TPemCmEvalDetails>(TPemCmEvalDetails.class);
			beans.addAll(evalList);
			
			tblEvalDetails.setContainerDataSource(beans);
			tblEvalDetails.setSelectable(true);
			tblEvalDetails.setVisibleColumns(new Object[] { "docId","evalNo",
					"evalDate", "bankName", "custName", "doctype","docStatus",
					"lastUpdtedBy", "lastUpdateDt" });
			tblEvalDetails.setColumnHeaders(new String[] { "Ref.Id","Evaluation Number",
					"Evaluation Date", "Bank Name", "Customer Name","Document Type",
					"Status", "Last Updated By", "Last Updated Date" });
			tblEvalDetails.setColumnFooter("lastUpdateDt",
					"No.of Records : " + total);
				tblEvalDetails.addItemClickListener(new ItemClickListener() {
					private static final long serialVersionUID = 1L;

					public void itemClick(ItemClickEvent event) {
						if (tblEvalDetails.isSelected(event.getItemId())) {
							
							btnView.setEnabled(false);
							btnedit.setEnabled(false);
							btnBill.setEnabled(false);
						} else {
							
							btnView.setEnabled(true);
							btnedit.setEnabled(true);
							btnBill.setEnabled(true);
						}

					}
				});
			tblEvalDetails
			.addValueChangeListener(new Property.ValueChangeListener() {
				/**
		 * 
		 */
				private static final long serialVersionUID = 3729824796823933688L;

				@Override
				public void valueChange(ValueChangeEvent event) {
try{
					StreamResource sr =getPDFStream();

	                if (sr != null) {

	                   if (filedownloader == null) {
	                	   filedownloader = new FileDownloader(getPDFStream());
	                	   filedownloader.extend(btnView);
	                  } else {
	                	  filedownloader.setFileDownloadResource(sr);
	                 }
	                } else {
	                
	               //   notif.show(Page.getCurrent());
	                   if (filedownloader != null) {
	                	   filedownloader.setFileDownloadResource(null); // reset
	                   }                       
	                   
	                }
	                
}catch(Exception e){
	
}
try{
	            	StreamResource sl =getBillStream();

	                if (sl != null) {

	                   if (billDownloader == null) {
	                	   billDownloader = new FileDownloader(getBillStream());
	                	   billDownloader.extend(btnBill);
	                  } else {
	                	  billDownloader.setFileDownloadResource(sl);
	                 }
	                } else {
	                	lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
	        			lblSaveNotification.setValue("No Bill Document is there");
	               //   notif.show(Page.getCurrent());
	                   if (billDownloader != null) {
	                	   billDownloader.setFileDownloadResource(null); // reset
	                   }                       
	                   
	                }
}catch(Exception e){
	
}
					TPemCmEvalDetails	syncList= (TPemCmEvalDetails) event.getProperty().getValue();
						if (syncList!= null) {				
					
//						if(syncList.getDocStatus().equals(RecordStatus.BILL_DTLS_PEND_DESC)||syncList.getDocStatus().equals(RecordStatus.BILL_DTLS_PAID_DESC)||syncList.getDocStatus().equals(RecordStatus.BILL_DTLS_APRVD_DESC)){
//							btnedit.setEnabled(true);
//							btnView.setEnabled(true);
//						}else{
//							btnedit.setEnabled(false);
//							btnView.setEnabled(false);
//							
//						}if(syncList.getDocStatus().equals(RecordStatus.BILL_DTLS_PEND_DESC)){
//							btnBill.setEnabled(false);
//						}
//						else{
//							btnBill.setEnabled(true);
//						}
	
					}catch (Exception e) {
						// TODO: handle exception
					}}
			}
				
		
		//	tblEvalDetails.setImmediate(true);
		 catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->"
					+ e);
		}
		getExportTableDetails();
	}
	
	/*
	 * editEvaluationsDetails()-->this function is used for view components
	 */
	private void editEaluationDetails(){
		Item itselect = tblEvalDetails.getItem(tblEvalDetails.getValue());
		if (itselect != null) {
			TPemCmEvalDetails edit=beans.getItem(tblEvalDetails.getValue()).getBean();
			
			if(edit.getEvalNo()!=null){
				tfevaluationNo.setReadOnly(false);
				tfevaluationNo.setValue(edit.getEvalNo());
				tfevaluationNo.setReadOnly(true);
			}
			else{
				tfevaluationNo.setReadOnly(false);
				tfevaluationNo.setValue("");
				tfevaluationNo.setReadOnly(true);
			}
			if(edit.getEvalDate()!=null){
				tfEvaluationDate.setReadOnly(false);
				String[] date=edit.getEvalDate().split(" ");
				tfEvaluationDate.setValue(date[0]);
				tfEvaluationDate.setReadOnly(true);
			}
			else{
				tfEvaluationDate.setReadOnly(false);
				tfEvaluationDate.setValue("");
				tfEvaluationDate.setReadOnly(true);
			}
			if(edit.getBankName()!=null){
				tfBankName.setReadOnly(false);
				tfBankName.setValue(edit.getBankName());
				tfBankName.setReadOnly(true);
			}
			else{
				tfBankName.setReadOnly(false);
				tfBankName.setValue("");
				tfBankName.setReadOnly(true);
			}
			if(edit.getBankBranch()!=null){
				tfBranchName.setReadOnly(false);
				tfBranchName.setValue(edit.getBankBranch());
				tfBranchName.setReadOnly(true);
			}
			else{
				tfBranchName.setReadOnly(false);
				tfBranchName.setValue("");
				tfBranchName.setReadOnly(true);
			}
			if(edit.getCustName()!=null){
				tfCustomername.setReadOnly(false);
				tfCustomername.setValue(edit.getCustName());
				tfCustomername.setReadOnly(true);
			}
			else{
				tfCustomername.setReadOnly(false);
				tfCustomername.setValue("");
				tfCustomername.setReadOnly(true);
			}
			if(edit.getInspectionDt()!=null){
				tfInspectionDate.setReadOnly(false);
				tfInspectionDate.setValue(edit.getInspectionDt());
				tfInspectionDate.setReadOnly(true);
			}
			else{
				tfInspectionDate.setReadOnly(false);
				tfInspectionDate.setValue("");
				tfInspectionDate.setReadOnly(true);
			}
			if(edit.getInspectionBy()!=null){
				tfinspectionBy.setReadOnly(false);
				tfinspectionBy.setValue(edit.getInspectionBy());
				tfinspectionBy.setReadOnly(true);
			}
			else{
				tfinspectionBy.setReadOnly(false);
				tfinspectionBy.setValue("");
				tfinspectionBy.setReadOnly(true);
			}
			if(edit.getValuationBy()!=null){
				tfVeriFiedBy.setReadOnly(false);
				tfVeriFiedBy.setValue(edit.getValuationBy());
				tfVeriFiedBy.setReadOnly(true);
			}
			else{
				tfVeriFiedBy.setReadOnly(false);
				tfVeriFiedBy.setValue("");
				tfVeriFiedBy.setReadOnly(true);
			}
//			if(edit.getDocStatus().equals(RecordStatus.DOC_APPROVED)){
//				cbstatus.setReadOnly(false);
//				cbstatus.setValue(edit.getDocStatus());
//				cbstatus.setReadOnly(true);
//				tfrejectRemarks.setVisible(false);
//				tfReductionAmount.setVisible(true);
//				tfReductionAmount.setValue("0.0");
//				tfReductionAmount.setReadOnly(false);
//
//			}
			//if(edit.getDocStatus().equals(RecordStatus.DOC_PENDING)){
			cbstatus.setReadOnly(false);
			tfrejectRemarks.setReadOnly(true);
			cbstatus.setValue(null);
			tfrejectRemarks.setVisible(false);
			tfReductionAmount.setVisible(true);
			tfReductionAmount.setValue("0.0");
			tfReductionAmount.setReadOnly(false);
				//cbstatus.setReadOnly(true);
			}
			
		}
	
	/*
	 * loadBankList()-->this function is used for load bank list
	 */
	private void loadBankList(){
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
	 * resetFields()-->this function is used for resetmaindComponents fileds
	 */
	private void resetFields() {
		//cbSearchStatus.setValue(RecordStatus.BILL_DTLS_PEND_CODE);
		tfReductionAmount.setComponentError(null);
		lblNotificationIcon.setIcon(null);
		lblSaveNotification.setValue("");
		hlBreadCrumbs.setVisible(false);
		lblFormTittle.setVisible(true);
		tfrejectRemarks.setReadOnly(false);
		tfrejectRemarks.setValue("");
		tfrejectRemarks.setReadOnly(true);
		tfReductionAmount.setReadOnly(false);
		tfReductionAmount.setValue("0.0");
		//tfReductionAmount.setReadOnly(true);
		cbstatus.setReadOnly(false);
		cbstatus.setValue(null);
		cbstatus.setReadOnly(true);
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
	 * Method for Documnet File Download
	 */
	private StreamResource getPDFStream() {
		
        StreamResource.StreamSource source = new StreamResource.StreamSource() {
            public InputStream getStream() {
            	TPemCmEvalDetails edit=beans.getItem(tblEvalDetails.getValue()).getBean();
    			if(edit.getEvalDoc()!=null){
    				
                  return new ByteArrayInputStream(edit.getEvalDoc());
    			}else{
				return null;
    			}

            }
        };
        TPemCmEvalDetails edit=beans.getItem(tblEvalDetails.getValue()).getBean();
      StreamResource resource = new StreamResource ( source, edit.getEvalNo()+".docx");
        return resource;
}
	
	/*
	 * Method for Bill Download
	 */
	private StreamResource getBillStream() {
		try{
        StreamResource.StreamSource source = new StreamResource.StreamSource() {
            public InputStream getStream() {
            	TPemCmEvalDetails edit=beans.getItem(tblEvalDetails.getValue()).getBean();
    			if(edit.getBillDoc()!=null){
    				
                  return new ByteArrayInputStream(edit.getBillDoc());
    			}else{
				return null;
    			}

            }
        };
        TPemCmEvalDetails edit=beans.getItem(tblEvalDetails.getValue()).getBean();
      StreamResource resource = new StreamResource ( source, edit.getEvalNo()+".doc");
        return resource;
		}catch(Exception e){
			return null;
		}
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
	 * Listener Function for button clickevents
	 */
//	@Override
//	public void buttonClick(ClickEvent event) {
//		notifications.close();
//		if (btnedit == event.getButton()) {
//			try {
//				editEaluationDetails();
//			} catch (Exception e) {
//				e.printStackTrace();
//				logger.error("Error thorws in editEaluationDetails() function--->"
//						+ e);
//			}
//			
//			vlMainLayout.setVisible(true);
//			vlSearchLayout.setVisible(false);
//			vlTableLayout.setVisible(true);
//			tblEvalDetails.setPageLength(10);
//			hlButtonLayout1.setVisible(true);
//			lblAddEdit.setValue("&nbsp;>&nbsp;Modify");
//			lblAddEdit.setVisible(true);
//			lblFormTittle.setVisible(false);
//			hlBreadCrumbs.setVisible(true);
//			lblNotificationIcon.setIcon(null);
//			lblSaveNotification.setValue("");
//		} 
//		else if (btnCancel == event.getButton()) {
//			resetFields();
//			vlMainLayout.setVisible(false);
//			vlSearchLayout.setVisible(true);
//			vlTableLayout.setVisible(true);
//			tblEvalDetails.setValue(null);
//			hlButtonLayout1.setVisible(false);
//			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
//					+ "</b>&nbsp;::&nbsp;Search");
//			lblAddEdit.setVisible(false);
//			lblFormTittle.setVisible(true);
//			hlBreadCrumbs.setVisible(false);
//			tblEvalDetails.setPageLength(15);
//			populatedAndConfig(false);
//			btnedit.setEnabled(false);
//			btnView.setEnabled(false);
//			btnBill.setEnabled(false);
//			hlFileDownloadLayout.removeAllComponents();
//			hlFileDownloadLayout.addComponent(btnDownload);
//			getExportTableDetails();
//		} else if (btnApp == event.getButton()) {
//			try {
//				uiflowdata =new UIFlowData();
//				TPemCmEvalDetails edit=beans.getItem(tblEvalDetails.getValue()).getBean();
//				List<TPemCmOwnerDetails> ownerobj= beanOwner.getOwnerDtlsList(edit.getDocId());
//				uiflowdata.setCustomer(ownerobj);
//				
//				
//				MPemCmBank editGetBank= edit.getBankId();
//				
//				if(edit.getDocId()!=null){
//					//commission setup
//					List<CommissionSetupDM> bill = BillGenerator.getEndValueDetails(edit.getPropertyValue(),edit.getDocId(),loginusername,editGetBank.getBankId(),companyId);
//					uiflowdata.setBillDtls(bill);
//					
//					//for bill generation
//					TPemCmBillDtls billDtls=BillGenerator.getBillDetails(edit.getPropertyValue(), Double.valueOf(tfReductionAmount.getValue().toString()),edit.getDocId(), loginusername,editGetBank.getBankId(), companyId,currencyId);
//					List<TPemCmBillDtls> billlist= beanBill.getBillList(edit.getDocId(),null);
//					System.out.println("billlist.size()++++++++++++++++++++"+billlist.size());
//					if(billlist.size()>0){
//					for(TPemCmBillDtls obj:billlist){
//						billDtls.setBillNo(obj.getBillNo());
//						billDtls.setBilldtlId(obj.getBilldtlId());
//					}
//					beanBill.saveBillDtls(billDtls);
//					}
//					else{
//						String ref="PEM_BILLNO";
//						billid = beanEvaluation.getNextSequnceId("seq_pem_billdtl_billdtlid");
//						List<SlnoGenDM> slnolist = beanSlno.generateSlnoGeneration(companyId,ref,null,null);
//						
//						for(SlnoGenDM slobj:slnolist){
//							String slno=slobj.getPrefixKey()+slobj.getPrefixCncat()+
//									slobj.getCurrSeqNo()+slobj.getSuffixCncat()+slobj.getSuffixKey();
//						billDtls.setBillNo(slno);
//						}
//						billDtls.setBilldtlId(billid);
//						beanBill.saveBillDtls(billDtls);
//						beanSlno.upadateSlnoGeneration(companyId, ref,null,null);
//						}
//					uiflowdata.setInspectionDate(edit.getInspectionDt());
//					uiflowdata.setBillDate(billDtls.getBillDate());
//					uiflowdata.setBill(billDtls);
//					uiflowdata.setEvalDtls(edit);
//					
//					//for save Bill document
//					String basepath = VaadinService.getCurrent()
//					          .getBaseDirectory().getAbsolutePath();
//					ByteArrayOutputStream recvstram = XMLUtil.doMarshall(uiflowdata);
//					XMLUtil.getWordDocument(recvstram, uiflowdata.getBill().getBillNo(),
//							strBillXsl);
//					
//					File billfile = new File(basepath+"/WEB-INF/PEM-DOCS/"+uiflowdata.getBill().getBillNo()+".doc");
//					FileInputStream billfin = new FileInputStream(billfile);
//				    byte fileContent1[] = new byte[(int)billfile.length()];
//				    billfin.read(fileContent1);
//				    billfin.close();
//					edit.setBillDoc(fileContent1);
//					beanEvaluation.saveorUpdateEvalDetails(edit);
//					uiflowdata.setEvalDtls(edit);
//					beanEvaluation.updateDocStatus(edit.getDocId(), (String) cbstatus.getValue(), tfrejectRemarks.getValue(), null, null, null, null);
//					
//					resetFields();
//					tfrejectRemarks.setReadOnly(true);
//					populatedAndConfig(false);
//					lblNotificationIcon.setIcon(new ThemeResource(
//							"img/success_small.png"));
//					lblSaveNotification.setValue("Successfully Saved");
//				}	
//				
//			} catch (Exception e) {
//				lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
//				lblSaveNotification
//						.setValue("Saved failed, please check the data and try again ");
//				e.printStackTrace();
//				logger.info("Error on SaveApproveReject Status function--->" + e);
//			
//			}
//		}
//		else if (btnSearch == event.getButton()) {
//			populatedAndConfig(true);
//			if (total == 0) {
//				lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
//				lblSaveNotification.setValue("No Records found");
//			} else {
//				lblNotificationIcon.setIcon(null);
//				lblSaveNotification.setValue("");
//			}
//			 hlFileDownloadLayout.removeAllComponents();
//				hlFileDownloadLayout.addComponent(btnDownload);
//				getExportTableDetails();
//		} else if (btnReset == event.getButton()) {
//			populatedAndConfig(false);
//			tfSearchEvalNo.setValue("");
//			cbSearchBankName.setValue(null);
//			tfSearCustName.setValue("");
//			cbSearchStatus.setValue(RecordStatus.getBillDetails(RecordStatus.BILL_DTLS_PEND_CODE));
//			lblNotificationIcon.setIcon(null);
//			lblSaveNotification.setValue("");
//			bankId = null;
//		}
//		else if(btnBack==event.getButton())
//		{
//			resetFields();
//			vlMainLayout.setVisible(false);
//			vlSearchLayout.setVisible(true);
//			vlTableLayout.setVisible(true);
//			hlButtonLayout1.setVisible(false);
//			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
//					+ "</b>&nbsp;::&nbsp;Search");
//			lblNotificationIcon.setIcon(null);
//			lblSaveNotification.setValue("");
//			lblFormTittle.setVisible(true);
//			hlBreadCrumbs.setVisible(false);
//			lblAddEdit.setVisible(false);
//			}
//	
//		
//		else if(btnFavor == event.getButton()){
//			addFavourities();
//		}
//		else if (btnDownload == event.getButton()) {
//
//			event.getButton().removeStyleName("unread");
//
//			if (notifications != null && notifications.getUI() != null)
//				notifications.close();
//			else {
//				buildNotifications(event);
//				UI.getCurrent().addWindow(notifications);
//				notifications.focus();
//				((VerticalLayout) UI.getCurrent().getContent())
//						.addLayoutClickListener(new LayoutClickListener() {
//							@Override
//							public void layoutClick(LayoutClickEvent event) {
//								notifications.close();
//								((VerticalLayout) UI.getCurrent().getContent())
//										.removeLayoutClickListener(this);
//							}
//						});
//			}
//			
//		}
//
//	}

}
