/**
 * File Name	:	BillPayment.java
 * Description	:	This Screen used for T_PEM_CM_BANK_PYMT_DTLS 
 * Author		:	Prakash.s
 * Date			:	mar 29, 2014
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jca.context.SpringContextResourceAdapter;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.CurrencyDM;
import com.gnts.base.mst.CompanyLookup;
import com.gnts.base.service.mst.CurrencyService;
import com.gnts.base.service.mst.LookupService;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.CurrencyColumnGenerator;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateValidation;
import com.gnts.erputil.util.DateUtils;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.rpt.TPemCmBillDtls;
import com.gnts.pem.domain.txn.CmPymtBillsDtls;
import com.gnts.pem.domain.txn.TPemCmBankPymtDtls;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.rpt.CmBillDtlsService;
import com.gnts.pem.service.txn.CmBillPymtDtlsService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
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

public class BillPayment implements ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CmBillPymtDtlsService beanBillpayments = (CmBillPymtDtlsService) SpringContextHelper
			.getBean("billPymntDtls");
	private CmBankService beanbank = (CmBankService) SpringContextHelper
			.getBean("bank");
	private LookupService lookUpBean = (LookupService) SpringContextHelper
			.getBean("lookup");
	private CmBillDtlsService beanBillDetails = (CmBillDtlsService) SpringContextHelper
			.getBean("billDtls");
	private CurrencyService beanCurrency = (CurrencyService) SpringContextHelper 
			.getBean("currency");

	private Table tblPymtDetails, tblBillDetails;
	private Long companyId,currencyId;
	private String screenName;
	private String userName;

	// Buttons
	private Button btnadd, btnAddnew, btndelete,btnview;
	private Button btnCancel;
	private Button btnSearch, btnReset;
	private Button btnsave;
	private Button btnBack;

	private Button btnDownload;
	  HorizontalLayout hlFileDownloadLayout;
	//Declaration for Exporter
	private Window notifications=new Window();
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	// components Search

	private ComboBox cbSearchBankName = new ComboBox("Bank Name");
	private PopupDateField dfserachstartdate = new PopupDateField("Start Date");
	private PopupDateField dfserachEnddate = new PopupDateField("End Date");
	private ComboBox cbSearchPymntType = new ComboBox("Payment Type");

	// header
	private TextField tfcheqeNo, tfpymntamount;
	private TextField tfbankDetails = new TextField("Bank Details");
	private TextField tfRemarks = new TextField("Remarks");
	private PopupDateField dfpymntDate = new PopupDateField("Payment Date");
	private PopupDateField dfCheqeDate = new PopupDateField("Cheque Date");
	private ComboBox cbpaymentType = new ComboBox("Payment Type");
	private ComboBox cbpaymentMode = new ComboBox("Payment Mode");
	private ComboBox cbBankname = new ComboBox("Bank Name");
	private ComboBox cbCcy;
	// bill payments
	private TextField tfbillAmount, tfPaymentAmount, tfCcyCode, tfBalanceAmount;
	private ComboBox cbbillNo = new ComboBox("Bill No");

	// containers And Entities
	private MPemCmBank selectBank, selectbankName;
	private BeanItemContainer<TPemCmBankPymtDtls> beans = null;
	private BeanItemContainer<CmPymtBillsDtls> beansforBills = null;
	private BeanItemContainer<MPemCmBank>  prodList=null;
	private CompanyLookupDM selectLookUppymntmode, selectLookUppymnttype;
	private Long bankId, billpaymentHeaderId;
	private List<TPemCmBillDtls> billList = new ArrayList<TPemCmBillDtls>();
	private TPemCmBillDtls SelectBillNo;
	private List<CmPymtBillsDtls> evalList = new ArrayList<CmPymtBillsDtls>();
	private List<Long> billIds = new ArrayList<Long>();

	private BigDecimal totalamount = new BigDecimal(0.00);
	private GridLayout glGridLayout2;
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

	private Logger logger = Logger.getLogger(Approvals.class);

	public BillPayment() {

		companyId = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		if(UI.getCurrent().getSession().getAttribute("currencyId")!=null)
		{
		currencyId = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("currenyId").toString());
		}
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();

		VerticalLayout clArgumentLayout = (VerticalLayout) UI.getCurrent().getSession()
				.getAttribute("clLayout");
		userName = UI.getCurrent().getSession().getAttribute("loginUserName")
				.toString();
		HorizontalLayout hlHeaderLayout = (HorizontalLayout) UI.getCurrent()
				.getSession().getAttribute("hlLayout");

		buildview(clArgumentLayout, hlHeaderLayout);
	}

	private void buildview(VerticalLayout clArgumentLayout,
			HorizontalLayout hlHeaderLayout) {
		clArgumentLayout.setStyleName(Runo.PANEL_LIGHT);
		hlHeaderLayout.removeAllComponents();

		btnCancel = new Button("Cancel", this);
		btnCancel.setVisible(true);
		btnSearch = new Button("Search", this);
		btnsave = new Button("Save", this);
		btnsave.setStyleName("styles.css/buttonrefresh");
		btnAddnew = new Button("Add Bill", this);
		btndelete = new Button("Delete", this);
		btnadd = new Button("Add", this);
		btnview = new Button("View",this);
		btnReset = new Button("Reset", this);
		btnBack = new Button("Home", this);
		btnBack.setStyleName("link");
		btnsave.setStyleName("savebt");
		btnCancel.addStyleName("cancelbt");
		btnAddnew.addStyleName("add");
		btndelete.addStyleName("delete");
		btnadd.addStyleName("add");
		btnview.addStyleName("editbt");
		btnReset.addStyleName("resetbt");
		btnSearch.setStyleName("searchbt");
		btnview.setEnabled(false);
		

		tblPymtDetails = new Table();
		tblPymtDetails.setStyleName(Runo.TABLE_SMALL);
		tblPymtDetails.setPageLength(14);
		tblPymtDetails.setSizeFull();
		tblPymtDetails.setFooterVisible(true);
		tblPymtDetails.setSelectable(true);
		tblPymtDetails.setImmediate(true);
		tblPymtDetails.setColumnCollapsingAllowed(true);

		tblBillDetails = new Table();
		tblBillDetails.setStyleName(Runo.TABLE_SMALL);
		tblBillDetails.setPageLength(6);
		tblBillDetails.setSizeFull();
		tblBillDetails.setFooterVisible(true);
		tblBillDetails.setSelectable(true);
		tblBillDetails.setImmediate(true);
		tblBillDetails.setColumnCollapsingAllowed(true);

		

		tfcheqeNo = new TextField("Cheque No");
		tfcheqeNo.setWidth("200");
		//tfcheqeNo.setInputPrompt("Cheque No");
		tfpymntamount = new TextField();
		tfpymntamount.setWidth("133");
		
		tfpymntamount.setRequired(true);
		cbCcy = new ComboBox();
		cbCcy.setWidth("60");
		//tfpymntamount.setInputPrompt("Payment Type");
		tfRemarks.setWidth("200");
		tfbankDetails.setWidth("200");
		//tfRemarks.setInputPrompt("Remarks");
		tfRemarks.setMaxLength(50);
		tfbankDetails.setMaxLength(25);
		//tfbankDetails.setInputPrompt("Bank Details");

		tfbillAmount = new TextField("Bill Amount");
		tfbillAmount.setWidth("100");
		//tfbillAmount.setInputPrompt("Bill Amount");
		tfCcyCode = new TextField();
		tfCcyCode.setWidth("60");

		tfPaymentAmount = new TextField("Bill Pymt Amount");
		tfPaymentAmount.setWidth("140");
		tfPaymentAmount.setRequired(true);
		//tfPaymentAmount.setInputPrompt("Payment Amount");

		tfBalanceAmount = new TextField("Balance Amount");
		tfBalanceAmount.setWidth("140");
		//tfBalanceAmount.setInputPrompt("Balance Amount");

		dfpymntDate.setDateFormat("dd-MMM-yyyy");
		//dfpymntDate.setInputPrompt("Select Date");
		dfpymntDate.setWidth("180");
		dfCheqeDate.setDateFormat("dd-MMM-yyyy");
		//dfCheqeDate.setInputPrompt("Select Date");
		dfCheqeDate.setWidth("180");
		
		dfCheqeDate.addValidator(new DateValidation("Invalid date entered"));
		dfCheqeDate.setImmediate(true);
		
		dfpymntDate.addValidator(new DateValidation("Invalid date entered"));
		dfpymntDate.setImmediate(true);
		
		dfpymntDate.setDateFormat("dd-MMM-yyyy");
		//dfpymntDate.setInputPrompt("Select Date");
		dfpymntDate.setWidth("180");
		
		dfserachEnddate.setDateFormat("dd-MMM-yyyy");
		//dfserachEnddate.setInputPrompt("Select Date");
		
		dfserachstartdate.setDateFormat("dd-MMM-yyyy");
		//dfserachstartdate.setInputPrompt("Select Date");
	
		cbSearchBankName.setWidth("150");
		cbSearchPymntType.setWidth("150");

		cbBankname.setWidth("200");
		cbpaymentMode.setWidth("200");
		cbpaymentType.setWidth("200");
		cbbillNo.setWidth("160");
		cbbillNo.setRequired(true);
		
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
							selectBank = (MPemCmBank) item.getBean();
							bankId = selectBank.getBankId();
						}
					}
				});
		cbSearchBankName.setImmediate(true);
		cbSearchBankName.setNullSelectionAllowed(false);
		cbSearchBankName.setWidth("200px");

		cbBankname.setInputPrompt(ApplicationConstants.selectDefault);
		cbBankname.setItemCaptionPropertyId("bankName");
		loadBankListforPayments();
		cbBankname.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				final Object itemId = event.getProperty().getValue();
				if (itemId != null) {
					final BeanItem<?> item = (BeanItem<?>) cbBankname
							.getItem(itemId);

					selectbankName = (MPemCmBank) item.getBean();
					loadBillDetails(selectbankName.getBankId());
				}
			}
		});
		cbBankname.setImmediate(true);
		cbBankname.setNullSelectionAllowed(false);
		cbBankname.setRequired(true);
	/*	List<MBaseCurrency>	list=beanCurrency.getCurrencyList(currencyId,null,null,null);
		for(MBaseCurrency obj:list){*/
		//}
		cbCcy.setItemCaptionPropertyId("ccycode");
		loadCcyList();
		cbCcy.setNullSelectionAllowed(false);
		System.out.println("Currency id--->"+currencyId);
		 
		/*Collection<?> coll = cbCcy.getItemIds();
		for (Iterator<?> iterator = coll.iterator(); iterator.hasNext();) {
			Object itemid = (Object) iterator.next();
			BeanItem<?> item = (BeanItem<?>) cbCcy.getItem(itemid);
			MBaseCurrency editCurny = (MBaseCurrency) item.getBean();
			if (currencyId != null && currencyId.equals(editCurny.getCcyid())) {
				cbCcy.setValue(itemid);
				break;
			} else {
				cbCcy.setValue(null);
			}
		}
		*/
		cbpaymentMode.setRequired(true);
		cbpaymentMode.setInputPrompt(ApplicationConstants.selectDefault);
		cbpaymentMode.setItemCaptionPropertyId("lookupname");
		loadlaoPaymenModfromLookup();
		cbpaymentMode
				.addValueChangeListener(new Property.ValueChangeListener() {

					public void valueChange(ValueChangeEvent event) {

						final Object itemId = event.getProperty().getValue();
						if (itemId != null) {
							final BeanItem<?> item = (BeanItem<?>) cbpaymentMode
									.getItem(itemId);
							selectLookUppymntmode = (CompanyLookupDM) item
									.getBean();

						}
					}
				});
		cbpaymentMode.setImmediate(true);
		cbpaymentMode.setNullSelectionAllowed(false);
		cbpaymentType.setRequired(true);
		cbpaymentType.setInputPrompt(ApplicationConstants.selectDefault);
		cbpaymentType.setItemCaptionPropertyId("lookupname");
		loadlaoPaymenttypefromLookup();
		cbpaymentType
				.addValueChangeListener(new Property.ValueChangeListener() {

					public void valueChange(ValueChangeEvent event) {

						final Object itemId = event.getProperty().getValue();
						if (itemId != null) {
							final BeanItem<?> item = (BeanItem<?>) cbpaymentType
									.getItem(itemId);
							selectLookUppymnttype = (CompanyLookupDM) item
									.getBean();
						}
					}
				});
		cbpaymentType.setImmediate(true);
		cbpaymentType.setNullSelectionAllowed(false);

//
		cbSearchPymntType.setInputPrompt(ApplicationConstants.selectDefault);
		cbSearchPymntType.setItemCaptionPropertyId("lookupname");
		loadlaoPaymenttypefromLookup();
		cbSearchPymntType
				.addValueChangeListener(new Property.ValueChangeListener() {

					public void valueChange(ValueChangeEvent event) {

						final Object itemId = event.getProperty().getValue();
						if (itemId != null) {
							final BeanItem<?> item = (BeanItem<?>) cbSearchPymntType
									.getItem(itemId);
							selectLookUppymnttype = (CompanyLookupDM) item
									.getBean();
							System.out.println("selectLookUppymnttype0000000000000000"+selectLookUppymnttype);
						}
					}
				});
		cbSearchPymntType.setImmediate(true);
		cbSearchPymntType.setNullSelectionAllowed(false);
		
		cbbillNo.setInputPrompt(ApplicationConstants.selectDefault);
		cbbillNo.setItemCaptionPropertyId("billNo");
		cbbillNo.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				final Object itemId = event.getProperty().getValue();
				if (itemId != null) {
					final BeanItem<?> item = (BeanItem<?>) cbbillNo
							.getItem(itemId);
					SelectBillNo = (TPemCmBillDtls) item.getBean();
					tfbillAmount.setReadOnly(false);
					tfbillAmount.setValue(SelectBillNo.getBalanceAmount()
							.toString());
					tfbillAmount.setReadOnly(true);
					tfCcyCode.setReadOnly(false);
				List<CurrencyDM>	list=beanCurrency.getCurrencyList(SelectBillNo.getCcyId(),null,null,null);
				for(CurrencyDM obj:list){
					tfCcyCode.setValue(obj.getCcycode());
				}
					tfCcyCode.setReadOnly(true);
					
				}
			}
		});
		cbbillNo.setImmediate(true);
		cbbillNo.setNullSelectionAllowed(false);
		tfPaymentAmount.setValue("0.0");
		tfPaymentAmount.addValidator(new DoubleValidator("Enter number only"));
		tfPaymentAmount.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;

			public void blur(BlurEvent event) {
				try {
					BigDecimal billamt = new BigDecimal(tfbillAmount.getValue());
					BigDecimal pymtAmt = new BigDecimal(tfPaymentAmount
							.getValue());

					if (billamt.compareTo(pymtAmt) >= 0) {
						BigDecimal balanceAmt = billamt.subtract(pymtAmt);
						lblSaveNotification.setValue("");
						lblNotificationIcon.setIcon(null);
						tfBalanceAmount.setReadOnly(false);
						tfBalanceAmount.setValue(balanceAmt + "");
						tfBalanceAmount.setReadOnly(true);
						validAmount();
						tfPaymentAmount.setComponentError(null);
					} else {
						tfBalanceAmount.setReadOnly(false);
						tfBalanceAmount.setValue("");
						tfBalanceAmount.setReadOnly(true);
						tfPaymentAmount.setComponentError(new UserError("Payment amount Shouldn't Exceed bill amount"));
					}

				} catch (Exception e) {
				}
			}
		});

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
		hlTableTittleLayout.addComponent(btnadd);
		hlTableTittleLayout.addComponent(btnview);
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
		vlTableForm.addComponent(tblPymtDetails);
		
		vlTableLayout.setStyleName(Runo.PANEL_LIGHT);
		vlTableLayout.addComponent(vlTableForm);

		// search panel
		FormLayout flSearchForm1 = new FormLayout();
		flSearchForm1.addComponent(cbSearchBankName);
		FormLayout flSearchForm2 = new FormLayout();
		flSearchForm2.addComponent(dfserachstartdate);
		FormLayout flSearchForm3 = new FormLayout();
		flSearchForm3.addComponent(dfserachEnddate);
		FormLayout flSearchForm4=new FormLayout();
		flSearchForm4.addComponent(cbSearchPymntType);
		

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

/*		GridLayout glSearchPanel = new GridLayout();
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
		Label gap = new Label();
		gap.setWidth("0.2em");
		FormLayout flMainform1 = new FormLayout();
		FormLayout flMainform2 = new FormLayout();
		FormLayout flMainform3 = new FormLayout();
		FormLayout flMainform4 = new FormLayout();
		HorizontalLayout hlpymntccy=new HorizontalLayout();
		hlpymntccy.setCaption("Bank Pymt Amount");
		//hlpymntccy.addComponent(gap);
		hlpymntccy.addComponent(tfpymntamount);
		
		hlpymntccy.addComponent(gap);
		hlpymntccy.addComponent(cbCcy);
		/*
		 * 	FormLayout flMainform5 = new FormLayout();
		FormLayout flMainform6 = new FormLayout();
		flMainform5.setComponentAlignment(tfpymntamount, Alignment.TOP_RIGHT);
		flMainform6.setComponentAlignment(cbCcy, Alignment.TOP_RIGHT);
		hlpymntccy.addComponent(flMainform5);
		hlpymntccy.addComponent(flMainform6);
		hlpymntccy.setComponentAlignment(flMainform5, Alignment.TOP_RIGHT);
		hlpymntccy.setComponentAlignment(flMainform6, Alignment.TOP_RIGHT);
		hlpymntccy.setWidth("280px");*/
		hlpymntccy.setSpacing(false);
		flMainform1.setSpacing(true);
		flMainform2.setSpacing(true);
		flMainform3.setSpacing(true);
		flMainform4.setSpacing(true);
		flMainform1.addComponent(dfpymntDate);
		flMainform1.addComponent(cbBankname);
		flMainform1.addComponent(hlpymntccy);
		
	//	flMainform1.addComponent(cbCcy);
		flMainform2.addComponent(cbpaymentType);
		flMainform2.addComponent(cbpaymentMode);
		flMainform2.addComponent(tfcheqeNo);
		flMainform3.addComponent(dfCheqeDate);
		flMainform3.addComponent(tfbankDetails);
		flMainform3.addComponent(tfRemarks);

		HorizontalLayout hlForm = new HorizontalLayout();
		hlForm.setSpacing(true);
		hlForm.addComponent(flMainform1);
		hlForm.addComponent(flMainform2);
		hlForm.addComponent(flMainform3);
		hlForm.setSpacing(true);
		
		
		FormLayout flMainformnew1 = new FormLayout();
		FormLayout flMainformnew2 = new FormLayout();
		FormLayout flMainformnew3 = new FormLayout();
		FormLayout flMainformnew4 = new FormLayout();
		FormLayout flMainformnew5 = new FormLayout();
		flMainformnew1.addComponent(cbbillNo);
		flMainformnew2.addComponent(tfbillAmount);
		flMainformnew3.addComponent(tfPaymentAmount);
		flMainformnew4.addComponent(tfBalanceAmount);
		flMainformnew5.addComponent(tfCcyCode);
//		HorizontalLayout hlForm2 = new HorizontalLayout();
//		hlForm2.addComponent(btnAddnew);
//		hlForm2.addComponent(btndelete);

		VerticalLayout hlForm2 = new VerticalLayout();
		hlForm2.setSpacing(true);
		hlForm2.addComponent(btnAddnew);
		hlForm2.addComponent(btndelete);
		hlForm2.setWidth("100");
		hlForm2.addStyleName("topbarthree");
		hlForm2.setMargin(false);

		HorizontalLayout hlForm1 = new HorizontalLayout();
		HorizontalLayout hlbillccy = new HorizontalLayout();
		hlbillccy.addComponent(flMainformnew2);
		hlbillccy.addComponent(flMainformnew5);
		hlForm1.setSpacing(true);
		hlForm1.addComponent(flMainformnew1);
		hlForm1.addComponent(hlbillccy);
		hlForm1.addComponent(flMainformnew3);
	//	hlForm1.addComponent(flMainformnew3);
		hlForm1.addComponent(flMainformnew4);
		hlForm1.addComponent(hlForm2);
		hlForm1.setComponentAlignment(hlForm2, Alignment.MIDDLE_CENTER);
		hlForm.setSpacing(true);
		hlbillccy.setSpacing(false);
		final GridLayout glGridLayout1 = new GridLayout(1, 2);
		glGridLayout1.setSizeFull();
		glGridLayout1.setSpacing(true);
		glGridLayout1.setMargin(true);
		glGridLayout1.addComponent(hlForm);

		glGridLayout2 = new GridLayout(1, 2);
		glGridLayout2.setSizeFull();
		glGridLayout2.setSpacing(true);
		glGridLayout2.setMargin(true);
		glGridLayout2.addComponent(hlForm1);
	//	glGridLayout2.setMargin(true);
		vlMainLayout = new VerticalLayout();
		vlMainLayout.addComponent(PanelGenerator.createPanel(glGridLayout1));
		vlMainLayout.addComponent(PanelGenerator.createPanel(glGridLayout2));
		vlMainLayout.addComponent(tblBillDetails);
		vlMainLayout.setMargin(true);
		vlMainLayout.setVisible(false);
		vlMainLayout.setSpacing(true);
		// css mainLaout
		clArgumentLayout.addComponent(vlMainLayout);
		clArgumentLayout.addComponent(vlSearchLayout);
		clArgumentLayout.addComponent(vlTableLayout);

		// HeaderPanel
		hlButtonLayout1 = new HorizontalLayout();
		hlButtonLayout1.addComponent(btnsave);
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
		populateandConfigBillDetails();
		setTableProperties();

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
	
public void setBillComponentError(){
	cbbillNo.setComponentError(null);
	tfPaymentAmount.setComponentError(null);
	if(cbbillNo.getValue()==null){
		cbbillNo.setComponentError(new UserError("Select Bill Number"));
	}
	if(tfPaymentAmount.getValue()==null){
		tfPaymentAmount.setComponentError(new UserError("Enter Payment Amount"));
	}
}
public void setComponentError(){
	cbpaymentMode.setComponentError(null);
	cbpaymentType.setComponentError(null);
	if(cbpaymentType.getValue()==null){
		cbpaymentType.setComponentError(new UserError("Select Payment Type"));
	}
	if(cbpaymentMode.getValue()==null){
		cbpaymentMode.setComponentError(new UserError("Select Payment Mode"));
	}
}
	// Show the expected value in Grid table.
	public void setTableProperties() {

		tblPymtDetails.addGeneratedColumn("pymtAmnt", new CurrencyColumnGenerator());
		tblPymtDetails.setColumnAlignment("pymtAmnt", Align.RIGHT);
		tblPymtDetails.setColumnAlignment("pymtId", Align.RIGHT);
		
			}

	public void setBillTableProperties() {

	tblBillDetails.addGeneratedColumn("pymtAmnt", new CurrencyColumnGenerator());
	tblBillDetails.addGeneratedColumn("befBalAmount", new CurrencyColumnGenerator());
	tblBillDetails.addGeneratedColumn("balAmnt", new CurrencyColumnGenerator());
		
		tblBillDetails.setColumnAlignment("billNo", Align.RIGHT);
		tblBillDetails.setColumnAlignment("befBalAmount", Align.RIGHT);
		tblBillDetails.setColumnAlignment("pymtAmnt", Align.RIGHT);
		tblBillDetails.setColumnAlignment("balAmnt", Align.RIGHT);
	}

	//	tblCmBillDetails.addGeneratedColumn("bankname",new BankNameColumnGenerator());
		
		/*List<MBaseCurrency> list=beanCurrency.getCurrencyList(currencyId,null, null,null);
		for(MBaseCurrency obj:list){
			 ccySymbol = obj.getCcysymbol();
			}*/
		
			
	/*
	 * populatedAndConfig()-->this function is used for populationg the records
	 * to Grid table
	 */
	public void populatedAndConfig(boolean search) {

		try {
			tblPymtDetails.removeAllItems();
			List<TPemCmBankPymtDtls> evalList = null;
			evalList = new ArrayList<TPemCmBankPymtDtls>();
			if (search) {
				String stdate = null;
				String stendate = null;
				String lookupname=null;
				CompanyLookupDM pymntType=(CompanyLookupDM) cbSearchPymntType.getValue();
				
				if(pymntType!=null){
					lookupname=pymntType.getLookupName();
				}
				System.out.println("pymntType==============="+pymntType);
				try{
					if(dfserachstartdate.getValue()!=null){
						 stdate=DateUtils.datetostringsimple(dfserachstartdate.getValue());
					}
					if(dfserachEnddate.getValue()!=null){
						stendate=DateUtils.datetostringsimple(dfserachEnddate.getValue());
					}
				
				}catch(Exception e){
					e.printStackTrace();
				}
				  if ( bankId != null || companyId != null ||
						  stdate != null||stendate!=null||pymntType!=null) {
					  evalList = beanBillpayments.getBillPymtList(null, companyId,bankId,stdate,stendate,lookupname);
					  total = evalList.size();
				 
				  }
			} else {

				evalList = beanBillpayments.getBillPymtList(null, companyId,null,null,null,null);
				total = evalList.size();

			}

			beans = new BeanItemContainer<TPemCmBankPymtDtls>(
					TPemCmBankPymtDtls.class);
			beans.addAll(evalList);

			tblPymtDetails.setContainerDataSource(beans);
			tblPymtDetails.setSelectable(true);
			tblPymtDetails.setVisibleColumns(new Object[] { "pymtId",
					"bankName", "pymtAmnt", "pymtDt", "pymtType", "pymtMode",
					"bankDetails", "lastUpdatedBy", "lastUpdatedDt" });
			tblPymtDetails.setColumnHeaders(new String[] { "Ref.Id",
					"Bank Name", "Payment Amount", "Payment Date",
					"Payment Type", "Payment Mode", "Bank Details",
					"Last Updated By", "Last Updated Date" });
			tblPymtDetails.setColumnFooter("lastUpdatedDt", "No.of Records : "
					+ total);
			tblPymtDetails.addItemClickListener(new ItemClickListener() {
				private static final long serialVersionUID = 1L;

				public void itemClick(ItemClickEvent event) {
					if (tblPymtDetails.isSelected(event.getItemId())) {

						btnadd.setEnabled(true);
						btnview.setEnabled(false);

					} else {

						btnadd.setEnabled(false);
						btnview.setEnabled(true);
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
	 * populateandConfigBillDetails()-->this function is used for populationg
	 * the records to Grid table for Bill Details
	 */
	private void populateandConfigBillDetails() {
		try {
			tblBillDetails.removeAllItems();

			beansforBills = new BeanItemContainer<CmPymtBillsDtls>(
					CmPymtBillsDtls.class);
			
			beansforBills.addAll(evalList);

			tblBillDetails.setContainerDataSource(beansforBills);
			tblBillDetails.setSelectable(true);
			tblBillDetails.setVisibleColumns(new Object[] { "billNo",
					"befBalAmount", "pymtAmnt", "balAmnt" });
			tblBillDetails.setColumnHeaders(new String[] { "Bill No",
					"Bill Amount", "Payment Amount", "Balance Amount" });
			
			BigDecimal sumofpayment=new BigDecimal("0");
			
			for(CmPymtBillsDtls obj:evalList){
				sumofpayment=sumofpayment.add(obj.getPymtAmnt());
			}
			
			tblBillDetails.setColumnFooter("pymtAmnt", "Amount : "
					+ sumofpayment);
			setBillTableProperties();
			tblBillDetails.addItemClickListener(new ItemClickListener() {
				private static final long serialVersionUID = 1L;

				public void itemClick(ItemClickEvent event) {
					if (tblBillDetails.isSelected(event.getItemId())) {

						btnadd.setEnabled(true);
						btnview.setEnabled(false);

					} else {

						btnadd.setEnabled(false);
						btnview.setEnabled(true);
					}

				}
			});
		} catch (Exception e) {

			logger.error("error during populateandConfigBillDetails() on the table, The Error is ----->"
					+ e);
			e.printStackTrace();
		}
	}

	private void savePaymentDetails() {
		try {
			boolean valid = false;
			TPemCmBankPymtDtls pojo = new TPemCmBankPymtDtls();
			pojo.setPymtDt(dfpymntDate.getValue());
			if (selectbankName != null) {
				pojo.setBankId(selectbankName);
			} else {
				cbBankname.setComponentError(new UserError("Select Bank !.."));
			}
			if (tfpymntamount.getValue() != null
					&& tfpymntamount.getValue().trim().length() > 0) {
				pojo.setPymtAmnt(new BigDecimal(tfpymntamount.getValue()));
			} else {
				tfpymntamount.setComponentError(new UserError(
						"Enter Payment Amount !.."));
			}

			pojo.setPymtType(selectLookUppymnttype.getLookupname());
			pojo.setPymtMode(selectLookUppymntmode.getLookupname());
			pojo.setChequeNo(tfcheqeNo.getValue());
			pojo.setChequeDt(dfCheqeDate.getValue());
			pojo.setBankDetails(tfbankDetails.getValue());
			pojo.setPymtRemarks(tfRemarks.getValue());
			pojo.setCompanyId(companyId);
			pojo.setLastUpdatedDt(DateUtils.getcurrentdate());
			pojo.setLastUpdatedBy(userName);
			if(evalList.size()==0){
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/msg_info.png"));
				lblSaveNotification.setValue("Please Add the Bill or Cancel");
			}
			if (cbBankname.isValid() && tfpymntamount.isValid()&& evalList.size()>0) {
				beanBillpayments.saveorUpdateBillpymtDtlsDAO(pojo);
				billpaymentHeaderId = pojo.getPymtId();
				saveBillsDetails();
				valid = true;
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblSaveNotification.setValue("Successfully Saved");
			}
			if (valid) {
				reSetAllFilds();
			} else {
				if(evalList.size()==0){
					lblNotificationIcon.setIcon(new ThemeResource(
							"img/msg_info.png"));
					lblSaveNotification.setValue("Please Add the Bill or Cancel");
				}
				else{
				lblNotificationIcon
						.setIcon(new ThemeResource("img/failure.png"));
				lblSaveNotification
						.setValue("Saved failed, please check the data and try again ");
				}

			}
		} catch (Exception e) {
					e.printStackTrace();
			logger.error("error during savePaymentDetails(), The Error is ----->"
					+ e);
		}
	}
	
public void viewPaymentDetails(){
try{
	cbpaymentMode.setComponentError(null);
	cbpaymentType.setComponentError(null);
	System.out.println("Inside view Payment");
	Item itselect = tblPymtDetails.getItem(tblPymtDetails.getValue());
	if (itselect != null) {
		TPemCmBankPymtDtls edit = beans.getItem(tblPymtDetails.getValue()).getBean();
		// edit evaluation details
		beans.getItem(tblPymtDetails.getValue()).getBean();
		//headerid = (Long) itselect.getItemProperty("docId").getValue();
		dfpymntDate.setReadOnly(false);
		if(edit.getPymtDt()!=null && edit.getPymtDt().trim().length()>0 )
		{
			dfpymntDate.setValue(new Date(edit.getPymtDt()));
		}else{
			dfpymntDate.setValue(null);
			
		}
		dfpymntDate.setReadOnly(true);
		
		cbBankname.setReadOnly(false);
		MPemCmBank editGetBank= edit.getBankId();
		Collection<?> coll = cbBankname.getItemIds();
		for (Iterator<?> iterator = coll.iterator(); iterator.hasNext();) {
			Object itemid = (Object) iterator.next();
			BeanItem<?> item = (BeanItem<?>) cbBankname.getItem(itemid);
			MPemCmBank editBankBean = (MPemCmBank) item.getBean();
			if (editGetBank != null && editGetBank.getBankId().equals(editBankBean.getBankId())) {
				cbBankname.setValue(itemid);
				break;
			} else {
				cbBankname.setValue(null);
			}
		}
		cbBankname.setReadOnly(true);
		
		tfpymntamount.setReadOnly(false);
		tfpymntamount.setValue(itselect.getItemProperty(
				"pymtAmnt").getValue()+"");
		tfpymntamount.setReadOnly(true);
		
		cbpaymentType.setReadOnly(false);
	//	cbpaymentType.setValue(edit.getPymtType());
		
		String editGetlook= edit.getPymtType();
		Collection<?> colt = cbpaymentType.getItemIds();
		for (Iterator<?> iterator = colt.iterator(); iterator.hasNext();) {
			Object itemid = (Object) iterator.next();
			BeanItem<?> item = (BeanItem<?>) cbpaymentType.getItem(itemid);
			CompanyLookupDM editlookBean = (CompanyLookupDM) item.getBean();
			if (editGetBank != null && editGetlook.equals(editlookBean.getLookupname())) {
				cbpaymentType.setValue(itemid);
				break;
			} else {
				cbpaymentType.setValue(null);
			}
		}
		cbpaymentType.setReadOnly(true);
		
		cbpaymentMode.setReadOnly(false);

		String editlook= edit.getPymtMode();
		Collection<?> col = cbpaymentMode.getItemIds();
		for (Iterator<?> iterator = col.iterator(); iterator.hasNext();) {
			Object itemid = (Object) iterator.next();
			BeanItem<?> item = (BeanItem<?>) cbpaymentMode.getItem(itemid);
			CompanyLookupDM editlookBean = (CompanyLookupDM) item.getBean();
			if (editGetBank != null && editlook.equals(editlookBean.getLookupname())) {
				cbpaymentMode.setValue(itemid);
				break;
			} else {
				cbpaymentMode.setValue(null);
			}
		}
		cbpaymentMode.setReadOnly(true);
		
		tfcheqeNo.setReadOnly(false);
		tfcheqeNo.setValue((String) itselect.getItemProperty(
				"chequeNo").getValue());
		tfcheqeNo.setReadOnly(true);
		
		dfCheqeDate.setReadOnly(false);
		if(edit.getChequeDt()!=null && edit.getChequeDt().trim().length()>0 )
		{
			dfCheqeDate.setValue(new Date(edit.getChequeDt()));
		}else{
			dfCheqeDate.setValue(null);
			
		}
		dfCheqeDate.setReadOnly(true);
		
		tfbankDetails.setReadOnly(false);
		tfbankDetails.setValue((String) itselect.getItemProperty(
				"bankDetails").getValue());
		tfbankDetails.setReadOnly(true);
		
		tfRemarks.setReadOnly(false);
		tfRemarks.setValue((String) itselect.getItemProperty(
				"pymtRemarks").getValue());
		tfRemarks.setReadOnly(true);
		
		cbCcy.setReadOnly(false);
		Collection<?> collec = cbCcy.getItemIds();
		for (Iterator<?> iterator = collec.iterator(); iterator.hasNext();) {
			Object itemid = (Object) iterator.next();
			BeanItem<?> item = (BeanItem<?>) cbCcy.getItem(itemid);
			CurrencyDM editCurny = (CurrencyDM) item.getBean();
			if (currencyId != null && currencyId.equals(editCurny.getCcyid())) {
				cbCcy.setValue(itemid);
				break;
			} else {
				cbCcy.setValue(null);
			}
		}
		cbCcy.setReadOnly(true);
		
		evalList=new ArrayList<CmPymtBillsDtls>();
		System.out.println("payment header id-->"+edit.getPymtId());
		evalList=beanBillpayments.getPymtBillDtlsList(edit.getPymtId(), companyId, bankId);
		System.out.println("Details list size--->"+evalList.size());
		populateandConfigBillDetails();
}
}catch(Exception e){
	e.printStackTrace();
}
}

	private void saveBillsDetails() {
		try {
			System.out.println("Inside save Bill Details=========");
			for (CmPymtBillsDtls pojo : evalList) {
				TPemCmBankPymtDtls pojohead = new TPemCmBankPymtDtls();
				pojohead.setPymtId(billpaymentHeaderId);
				pojo.setPymtId(pojohead);
				Double doubleva=Double.valueOf(pojo.getBalAmnt().toString());
				if (doubleva<=0) {
					beanBillDetails.upadateBalanceAmunt(pojo.getBillDtlId()
							.getBilldtlId(), pojo.getBalAmnt(),"Paid");
				}else{
					beanBillDetails.upadateBalanceAmunt(pojo.getBillDtlId()
							.getBilldtlId(), pojo.getBalAmnt(),"Pending");
	
				}
					beanBillpayments.saveOrUpdatepayBilsDetails(pojo);
				

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error during saveBillsDetails(), The Error is ----->"
					+ e);
		}
	}

	/*
	 * saveBillDetails()-->this function is used for Save Bill Details
	 * Components
	 */
	private void saveBillDetails() {

		try {
			CmPymtBillsDtls billdtls = new CmPymtBillsDtls();
			billdtls.setBillDtlId(SelectBillNo);
			billdtls.setBefBalAmount(tfbillAmount.getValue());
			billdtls.setPymtAmnt(new BigDecimal(tfPaymentAmount.getValue()));
			totalamount = totalamount.add(new BigDecimal(tfPaymentAmount
					.getValue()));
			billdtls.setDocId(SelectBillNo.getDocId().getDocId());
			billdtls.setBalAmnt(new BigDecimal(tfBalanceAmount.getValue()));
			billIds.add(SelectBillNo.getBilldtlId());
			evalList.add(billdtls);
			populateandConfigBillDetails();
			reSetBillDetailsFields();
		} catch (Exception e) {
			e.printStackTrace();

			logger.error("error during saveBillDetails() on the table, The Error is ----->"
					+ e);
		}
	}

	/*
	 * deleteRecords()-->this function is used for Bill Details list
	 */
	private void deleteRecords() {
		try {
			CmPymtBillsDtls edit = beansforBills.getItem(
					tblBillDetails.getValue()).getBean();
			
			evalList.remove(edit);
			billIds.remove(edit.getBillDtlId().getBilldtlId());
			totalamount = totalamount.subtract(new BigDecimal(edit.getPymtAmnt().toString()));
			populateandConfigBillDetails();
			loadBillDetails();
		} catch (Exception e) {

			logger.error("error during saveBillDetails() on the table, The Error is ----->"
					+ e);
		}
	}

	/*
	 * loadCcyList()-->this function is used for load bank list
	 */
	private void loadCcyList() {
		try {
			List<CurrencyDM> list = beanCurrency.getCurrencyList(null, null, null, null);

			BeanItemContainer<CurrencyDM> ccyList = new BeanItemContainer<CurrencyDM>(
					CurrencyDM.class);

			ccyList.addAll(list);
			cbCcy.setContainerDataSource(ccyList);
		} catch (Exception e) {

			logger.error("fn_loadBankList_Exception Caught->" + e);

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
			 prodList = new BeanItemContainer<MPemCmBank>(MPemCmBank.class);
			prodList.addAll(allList);
			cbSearchBankName.setContainerDataSource(prodList);
			 cbSearchBankName.setValue(all);
			 } catch (Exception e) {
				 e.printStackTrace();
			logger.error("fn_loadBankList_Exception Caught->" + e);

		}
	}

	/*
	 * loadBankListforPayments()-->this function is used for load bank list to
	 * mainpanel
	 */
	private void loadBankListforPayments() {
		try {
			List<MPemCmBank> list = beanbank.getBankDtlsList(companyId,
					"Active",null);

			BeanItemContainer<MPemCmBank> prodList = new BeanItemContainer<MPemCmBank>(
					MPemCmBank.class);

			prodList.addAll(list);
			cbBankname.setContainerDataSource(prodList);
		} catch (Exception e) {

			logger.error("fn_loadBankList_Exception Caught->" + e);

		}
	}

	/*
	 * loadBillDetails()-->this function is used for Bill Details List
	 */
	private void loadBillDetails(Long BankId) {
		try {
			billList = beanBillDetails.getBillDtlsList(companyId,null, BankId,null,null,null,null);
			loadBillDetails();
		} catch (Exception e) {
			logger.error("loadBillDetails Caught->" + e);
		}
	}

	private void loadBillDetails() {
		try {
			List<TPemCmBillDtls> allList = new ArrayList<TPemCmBillDtls>();
			for (TPemCmBillDtls obj : billList) {
				if (!billIds.contains(obj.getBilldtlId()))
					allList.add(obj);
			}

			BeanItemContainer<TPemCmBillDtls> prodList = new BeanItemContainer<TPemCmBillDtls>(
					TPemCmBillDtls.class);

			prodList.addAll(allList);

			cbbillNo.setContainerDataSource(prodList);

		} catch (Exception e) {

			logger.error("fn_loadBankList_Exception Caught->" + e);

		}
	}

	/*
	 * loadlaoPaymenModfromLookup()-->this function is used for load LookUp
	 * paymneModeList
	 */
	private void loadlaoPaymenModfromLookup() {
		try {
			/*List<MBaseLookup> list = lookUpBean.getMBaseLookupList("PYMT_MODE",
					null, "Active");*/
			
			List<CompanyLookupDM> list = lookUpBean.getCompanyLookupList(companyId, null, "PYMT_MODE", null, null,  "Active");

			BeanItemContainer<CompanyLookupDM> prodList = new BeanItemContainer<CompanyLookupDM>(
					CompanyLookupDM.class);

			prodList.addAll(list);
			cbpaymentMode.setContainerDataSource(prodList);
		} catch (Exception e) {

			logger.error("fn_loadlaoPaymenModfromLookup()_Exception Caught->"
					+ e);

		}
	}

	/*
	 * loadlaoPaymenttypefromLookup()-->this function is used for load LookUp
	 * paymneTypeList
	 */
	private void loadlaoPaymenttypefromLookup() {
		try {
			/*List<MBaseLookup> list = lookUpBean.getMBaseLookupList("PYMT_TYPE",
					null, "Active");*/
			List<CompanyLookupDM> list = lookUpBean.getCompanyLookupList(companyId, null, "PYMT_TYPE", null, null,  "Active");

			BeanItemContainer<CompanyLookupDM> prodList = new BeanItemContainer<CompanyLookupDM>(
					CompanyLookupDM.class);

			prodList.addAll(list);
			cbpaymentType.setContainerDataSource(prodList);
			cbSearchPymntType.setContainerDataSource(prodList);
		} catch (Exception e) {

			logger.error("fn_loadlaoPaymenttypefromLookup()_Exception Caught->"
					+ e);

		}
	}

	/*
	 * resetFields()-->this function is used for resetmaindComponents fileds
	 */
	private void resetFields() {

		lblNotificationIcon.setIcon(null);
		lblSaveNotification.setValue("");
		hlBreadCrumbs.setVisible(false);
		lblFormTittle.setVisible(true);

	}

	private void reSetAllFilds() {
		cbBankname.setComponentError(null);
		tfpymntamount.setComponentError(null);
		tfPaymentAmount.setComponentError(null);
		tfpymntamount.setReadOnly(false);
		tfpymntamount.setValue("");
		tfPaymentAmount.setValue("0.0");
		dfpymntDate.setReadOnly(false);
		dfpymntDate.setValue(null);
		cbBankname.setReadOnly(false);
		cbBankname.setValue(null);
		tfpymntamount.setValue("");
		cbpaymentType.setReadOnly(false);
		cbpaymentType.setValue(null);
		cbpaymentMode.setReadOnly(false);
		cbpaymentMode.setValue(null);
		tfcheqeNo.setReadOnly(false);
		tfcheqeNo.setValue("");
		dfCheqeDate.setReadOnly(false);
		dfCheqeDate.setValue(null);
		tfbankDetails.setReadOnly(false);
		tfbankDetails.setValue("");
		tfRemarks.setReadOnly(false);
		tfRemarks.setValue("");
		cbCcy.setReadOnly(false);
		cbCcy.setValue("");
		resetwhenbankChange();
	}

	/*
	 * reSetBillDetailsFields()-->this function is used for resetmaindComponents
	 * for bill Details
	 */
	private void reSetBillDetailsFields() {
		cbbillNo.setComponentError(null);
		tfPaymentAmount.setComponentError(null);
		tfbillAmount.setReadOnly(false);
		tfbillAmount.setValue("");
		tfbillAmount.setReadOnly(true);
		tfPaymentAmount.setValue("0.0");
		tfBalanceAmount.setReadOnly(false);
		tfBalanceAmount.setValue("");
		tfBalanceAmount.setReadOnly(true);
		tfCcyCode.setReadOnly(true);
		tfCcyCode.setValue("");
		cbbillNo.setValue(null);
		loadBillDetails();
	}

	/*
	 * resetwhenbankChange()-->this function is used for resetmaindComponents
	 * for final bank set to null
	 */
	private void resetwhenbankChange() {
		billList.removeAll(billList);
		evalList.removeAll(evalList);
		billIds.removeAll(billIds);
		totalamount = new BigDecimal("0.00");
		tfbillAmount.setReadOnly(false);
		tfbillAmount.setValue("");
		tfbillAmount.setReadOnly(true);
		tfPaymentAmount.setValue("0.0");
		tfBalanceAmount.setReadOnly(false);
		tfBalanceAmount.setValue("");
		tfBalanceAmount.setReadOnly(true);
		cbbillNo.setValue(null);
		populateandConfigBillDetails();

	}

	/*
	 * validAmount()-->this function is used for validat Payment amount with
	 * actual Payment Amount
	 */
	private void validAmount() {
		BigDecimal pymntamount = new BigDecimal(tfpymntamount.getValue());
		BigDecimal totalAmt = totalamount.add(new BigDecimal(tfPaymentAmount
				.getValue()));
		if (pymntamount.compareTo(totalAmt) < 0) {
			btnAddnew.setEnabled(false);
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
			lblSaveNotification

			.setValue(" Total Amount Shouldn't Exceed  Payment amount");
		} else {
			btnAddnew.setEnabled(true);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
		}

	}

	private void getExportTableDetails()
	{
		excelexporter.setTableToBeExported(tblPymtDetails);
		csvexporter.setTableToBeExported(tblPymtDetails);
		pdfexporter.setTableToBeExported(tblPymtDetails);
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
	@Override
	public void buttonClick(ClickEvent event) {
		notifications.close();
		if (btnadd == event.getButton()) {
			Collection<?> coll = cbCcy.getItemIds();
			for (Iterator<?> iterator = coll.iterator(); iterator.hasNext();) {
				Object itemid = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbCcy.getItem(itemid);
				CurrencyDM editCurny = (CurrencyDM) item.getBean();
				if (currencyId != null && currencyId.equals(editCurny.getCcyid())) {
					cbCcy.setValue(itemid);
					break;
				} else {
					cbCcy.setReadOnly(false);
					cbCcy.setValue(null);
				}
			}
			
			vlMainLayout.setVisible(true);
			glGridLayout2.setVisible(true);
			vlSearchLayout.setVisible(false);
			vlTableLayout.setVisible(false);
			tblPymtDetails.setPageLength(10);
			hlButtonLayout1.setVisible(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Add new");
			lblAddEdit.setVisible(true);
			btnsave.setVisible(true);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
		} 
		if (btnview == event.getButton()) {
			vlMainLayout.setVisible(true);
			glGridLayout2.setVisible(false);
			vlSearchLayout.setVisible(false);
			vlTableLayout.setVisible(false);
			viewPaymentDetails();
			tblBillDetails.setPageLength(6);	
			populateandConfigBillDetails();
			hlButtonLayout1.setVisible(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;View");
			lblAddEdit.setVisible(true);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			btnCancel.setVisible(true);
			btnsave.setVisible(false);
		} 
		else if (btnCancel == event.getButton()) {
			resetFields();
			reSetAllFilds();
			vlMainLayout.setVisible(false);
			vlSearchLayout.setVisible(true);
			vlTableLayout.setVisible(true);
			tblPymtDetails.setValue(null);
			hlButtonLayout1.setVisible(false);
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Search");
			lblAddEdit.setVisible(false);
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			tblPymtDetails.setPageLength(15);
			populatedAndConfig(false);
			btnadd.setEnabled(true);
			hlFileDownloadLayout.removeAllComponents();
			hlFileDownloadLayout.addComponent(btnDownload);
			getExportTableDetails();

		} else if (btnsave == event.getButton()) {
			try {
				setComponentError();
				savePaymentDetails();
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("Error on SaveApproveReject Status function--->"
						+ e);
				//
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
			cbSearchBankName.setValue(null);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
			dfserachEnddate.setValue(null);
			dfserachstartdate.setValue(null);
			cbSearchPymntType.setValue(null);
			bankId = null;
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
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
		} else if (btnAddnew == event.getButton()) {
			setBillComponentError();
			try {
				saveBillDetails();
			
				tfPaymentAmount.setValue("0.0");
				tfPaymentAmount.setComponentError(null);
			} catch (Exception e) {

			}

		} else if (btndelete == event.getButton()) {
			try {
				
				deleteRecords();
				tfPaymentAmount.setValue("0.0");
				tfPaymentAmount.setComponentError(null);
			} catch (Exception e) {

			}

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
