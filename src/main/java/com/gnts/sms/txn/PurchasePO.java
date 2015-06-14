/**
 * File Name 		: PurchasePO.java 
 * Description 		:This Screen Purpose for Modify the PurchasePO Details.
 * 					Add the PurchasePO details process should be directly added in DB.
 * Author 			: Ganga 
 * Date 			: Sep 15, 2014

 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          Sep 15, 2014        GANGA                  Initial  Version
 */
package com.gnts.sms.txn;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.base.service.mst.VendorService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.sms.domain.txn.PurchasePODtlDM;
import com.gnts.sms.domain.txn.PurchasePOHdrDM;
import com.gnts.sms.domain.txn.PurchaseQuotDtlDM;
import com.gnts.sms.domain.txn.PurchaseQuotHdrDM;
import com.gnts.sms.service.txn.PurchasePODtlService;
import com.gnts.sms.service.txn.PurchasePOHdrService;
import com.gnts.sms.service.txn.PurchaseQuotHdrService;
import com.gnts.sms.service.txn.PurchaseQuoteDtlService;
import com.gnts.sms.service.txn.SmsCommentsService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class PurchasePO extends BaseUI {
	private SmsCommentsService serviceComment = (SmsCommentsService) SpringContextHelper.getBean("smsComments");
	private PurchasePOHdrService servicepurchaePOHdr = (PurchasePOHdrService) SpringContextHelper
			.getBean("PurchasePOhdr");
	private PurchaseQuoteDtlService servicePurchaseQuoteDtl = (PurchaseQuoteDtlService) SpringContextHelper
			.getBean("PurchaseQuotDtl");
	private PurchasePODtlService servicePurchasePODtl = (PurchasePODtlService) SpringContextHelper
			.getBean("PurchasePODtl");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private PurchaseQuotHdrService servicepurchaeQuoteHdr = (PurchaseQuotHdrService) SpringContextHelper
			.getBean("PurchaseQuot");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	// form layout for input controls for PO Header
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// form layout for input controls for PO Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4, flDtlColumn5;
	// // User Input Components for PO Details
	private ComboBox cbBranch, cbStatus, cbpoType, cbquoteNo;
	private TextField tfpaymetTerms, tfFreightTerms, tfWarrentyTerms, tfDelTerms;
	private TextField tfversionNo, tfBasictotal, tfpackingPer, tfPaclingValue, tfPONo, tfvendor;
	private TextField tfSubTotal, tfVatPer, tfVatValue, tfEDPer, tfEDValue, tfHEDPer;
	private TextField tfHEDValue, tfCessPer, tfCessValue, tfCstPer, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightPer, tfFreightValue, tfOtherPer, tfOtherValue, tfGrandtotal;
	private TextArea taRemark, taInvoiceOrd, taShpnAddr;
	private CheckBox ckdutyexm, ckPdcRqu, ckCformRqu, ckcasePO;
	private PopupDateField dfPODt;
	private Button btnsavepurQuote = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlPODoc = new VerticalLayout();
	// PODtl components
	private ComboBox cbproduct, cbUom, cbPODtlStatus;
	private TextField tfPOQnty, tfUnitRate, tfBasicValue;
	private TextArea taPODtlRemark;
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private BeanItemContainer<PurchasePOHdrDM> beanPurchasePOHdr = null;
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	private BeanItemContainer<PurchasePODtlDM> beanPurchasePODtl = null;
	List<PurchasePODtlDM> PurchasePODtllList = new ArrayList<PurchasePODtlDM>();
	// local variables declaration
	private String username;
	private Long companyid;
	private int recordCnt;
	private Long QuoteId;
	private Long EmployeeId;
	private File file;
	private Long roleId;
	private Long branchId;
	private Long moduleId;
	private Long appScreenId;
	private Long branchID;
	private Long poId;
	private String poid;
	private SmsComments comments;
	VerticalLayout vlTableForm = new VerticalLayout();
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private GERPTable tblPurPO;
	private Long pODtlId;
	private String status;
	public static boolean filevalue1 = false;
	// Initialize logger
	private static Logger logger = Logger.getLogger(PurchasePO.class);
	
	// Constructor received the parameters from Login UI class
	public PurchasePO() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		EmployeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PurchasePO() constructor");
		// Loading the PO UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax UI");
		// Initialization for PurchasePO Details user input components
		cbquoteNo = new ComboBox("Quote No");
		cbquoteNo.setItemCaptionPropertyId("quoteRef");
		cbquoteNo.setWidth("150");
		cbquoteNo.setRequired(true);
		loadQuoteNoList();
		tfvendor = new TextField("Vendor Name");
		// cbVendor.setItemCaptionPropertyId("vendorName");
		tfvendor.setWidth("150");
		// loadVendor();
		cbquoteNo.setImmediate(true);
		cbquoteNo.addValueChangeListener(new Property.ValueChangeListener() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbquoteNo.getItem(itemId);
				if (item != null) {
					tfvendor.setReadOnly(false);
					tfvendor.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getVendorName());
					tfvendor.setReadOnly(true);
				}
			}
		});
		cbquoteNo.addValueChangeListener(new Property.ValueChangeListener() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbquoteNo.getItem(itemId);
				if (item != null) {
					loadProduct();
					loadQuoteDetails();
				}
			}
		});
		cbquoteNo.addValueChangeListener(new Property.ValueChangeListener() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbquoteNo.getItem(itemId);
				if (item != null) {
				}
			}
		});
		taInvoiceOrd = new TextArea("Invoice Addr");
		taInvoiceOrd.setHeight("35");
		taInvoiceOrd.setWidth("150");
		taShpnAddr = new TextArea("Shipping Addr");
		taShpnAddr.setHeight("35");
		taShpnAddr.setWidth("150");
		dfPODt = new GERPPopupDateField("Purchase Ord Date");
		dfPODt.setInputPrompt("Select Date");
		dfPODt.setWidth("150");
		tfPONo = new TextField("PO.No");
		tfPONo.setWidth("150");
		taRemark = new TextArea("Remarks");
		taRemark.setHeight("50");
		taRemark.setWidth("150");
		tfversionNo = new TextField(" Version No");
		tfversionNo.setWidth("150");
		tfBasictotal = new TextField("Basic total");
		tfBasictotal.setWidth("150");
		tfpackingPer = new TextField();
		tfpackingPer.setWidth("30");
		tfPaclingValue = new TextField();
		tfPaclingValue.setWidth("120");
		tfSubTotal = new TextField("Sub Total");
		tfSubTotal.setWidth("150");
		tfVatPer = new TextField();
		tfVatPer.setWidth("30");
		tfVatValue = new TextField();
		tfVatValue.setWidth("120");
		tfEDPer = new TextField();
		tfEDPer.setWidth("30");
		tfEDValue = new TextField();
		tfEDValue.setWidth("120");
		tfHEDPer = new TextField();
		tfHEDPer.setWidth("30");
		tfHEDValue = new TextField();
		tfHEDValue.setWidth("120");
		tfCessPer = new TextField();
		tfCessPer.setWidth("30");
		tfCessValue = new TextField();
		tfCessValue.setWidth("120");
		tfCstPer = new TextField();
		tfCstPer.setWidth("30");
		tfCstValue = new TextField();
		tfCstValue.setWidth("120");
		tfSubTaxTotal = new TextField("Sub Tax Total");
		tfSubTaxTotal.setWidth("150");
		tfFreightPer = new TextField();
		tfFreightPer.setWidth("30");
		tfFreightValue = new TextField();
		tfFreightValue.setWidth("120");
		tfOtherValue = new TextField();
		tfOtherValue.setWidth("120");
		tfGrandtotal = new TextField("Grand Total");
		tfGrandtotal.setWidth("150");
		tfOtherPer = new TextField();
		tfOtherPer.setWidth("30");
		tfpaymetTerms = new TextField("Payment Terms");
		tfFreightTerms = new TextField("Freight Terms");
		tfWarrentyTerms = new TextField("Warrenty Terms");
		tfDelTerms = new TextField("Delivery Terms");
		ckdutyexm = new CheckBox("Duty Exempted");
		ckCformRqu = new CheckBox("Cfrom Req");
		ckPdcRqu = new CheckBox("PDC Req");
		ckcasePO = new CheckBox("Cash PO");
		cbBranch = new ComboBox("Branch Name");
		cbBranch.setWidth("150");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbpoType = new ComboBox("Order Type");
		cbpoType.setItemCaptionPropertyId("lookupname");
		cbpoType.setWidth("150");
		loadPOTypet();
		List<ApprovalSchemaDM> list = servicepurchaePOHdr.getReviewerId(companyid, appScreenId, branchID, roleId);
		for (ApprovalSchemaDM obj : list) {
			if (obj.getApprLevel().equals("Reviewer")) {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR, BASEConstants.WO_RV_STATUS);
			} else {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR, BASEConstants.WO_AP_STATUS);
			}
		}
		cbStatus.setWidth("155");
		// PurchaseOrder Detail Comp
		cbproduct = new ComboBox("Product Name");
		cbproduct.setItemCaptionPropertyId("productName");
		cbproduct.setWidth("130");
		cbproduct.setImmediate(true);
		cbproduct.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbproduct.getValue() != null) {
					tfPOQnty.setReadOnly(false);
					tfPOQnty.setValue(((PurchaseQuotDtlDM) cbproduct.getValue()).getEnquiryQty() + "");
					tfPOQnty.setReadOnly(true);
					cbUom.setReadOnly(false);
					cbUom.setValue(((PurchaseQuotDtlDM) cbproduct.getValue()).getUom() + "");
					cbUom.setReadOnly(true);
				}
			}
		});
		tfPOQnty = new TextField();
		tfPOQnty.setValue("0");
		tfPOQnty.setWidth("60");
		cbUom = new ComboBox();
		cbUom.setItemCaptionPropertyId("lookupname");
		cbUom.setWidth("77");
		cbUom.setHeight("23");
		loadUomList();
		tfUnitRate = new TextField("Unit Rate");
		tfUnitRate.setWidth("130");
		tfUnitRate.setValue("0");
		tfBasicValue = new TextField("Basic value");
		tfBasicValue.setWidth("130");
		tfBasicValue.setValue("0");
		taPODtlRemark = new TextArea("Remak");
		taPODtlRemark.setWidth("130");
		taPODtlRemark.setHeight("50");
		cbPODtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbPODtlStatus.setWidth("130");
		btnsavepurQuote.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (DtlValidation()) {
					savePurchaseQuoteDetails();
				}
			}
		});
		tblPurPO = new GERPTable();
		tblPurPO.setPageLength(10);
		tblPurPO.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblPurPO.isSelected(event.getItemId())) {
					tblPurPO.setImmediate(true);
					btnsavepurQuote.setCaption("Add");
					btnsavepurQuote.setStyleName("savebt");
					PoDtlresetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnsavepurQuote.setCaption("Update");
					btnsavepurQuote.setStyleName("savebt");
					editPODtl();
				}
			}
		});
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		loadPurDtl();
		btnsavepurQuote.setStyleName("add");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for TestType UI search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		// Adding components into form layouts for TestType UI search layout
		flColumn1.addComponent(cbBranch);
		flColumn2.addComponent(tfPONo);
		flColumn3.addComponent(cbpoType);
		flColumn4.addComponent(cbStatus);
		// Adding form layouts into search layout for TestType UI search mode
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ " assembleInputUserLayout layout");
		/*
		 * Adding user input layout to the input layout as all the fields in the user input are available in the edit
		 * block. hence the same layout used as is
		 */
		// Set required fields
		// Removing components from search layout and re-initializing form layouts
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbBranch);
		flColumn1.addComponent(cbquoteNo);
		flColumn1.addComponent(tfvendor);
		flColumn1.addComponent(cbpoType);
		flColumn1.addComponent(ckcasePO);
		flColumn1.addComponent(tfPONo);
		flColumn1.addComponent(dfPODt);
		flColumn1.addComponent(tfversionNo);
		flColumn1.addComponent(taInvoiceOrd);
		flColumn2.addComponent(taShpnAddr);
		flColumn2.addComponent(taRemark);
		flColumn2.addComponent(tfBasictotal);
		HorizontalLayout pv = new HorizontalLayout();
		pv.addComponent(tfpackingPer);
		pv.addComponent(tfPaclingValue);
		pv.setCaption("Packing");
		flColumn2.addComponent(pv);
		flColumn2.setComponentAlignment(pv, Alignment.TOP_LEFT);
		flColumn2.addComponent(tfSubTotal);
		HorizontalLayout vp = new HorizontalLayout();
		vp.addComponent(tfVatPer);
		vp.addComponent(tfVatValue);
		vp.setCaption("VAT");
		flColumn2.addComponent(vp);
		flColumn2.setComponentAlignment(vp, Alignment.TOP_LEFT);
		HorizontalLayout ed = new HorizontalLayout();
		ed.addComponent(tfEDPer);
		ed.addComponent(tfEDValue);
		ed.setCaption("ED");
		flColumn2.addComponent(ed);
		flColumn2.setComponentAlignment(ed, Alignment.TOP_LEFT);
		HorizontalLayout hed = new HorizontalLayout();
		hed.addComponent(tfHEDPer);
		hed.addComponent(tfHEDValue);
		hed.setCaption("HED");
		flColumn2.addComponent(hed);
		flColumn2.setComponentAlignment(hed, Alignment.TOP_LEFT);
		flColumn2.addComponent(hed);
		HorizontalLayout cess = new HorizontalLayout();
		cess.addComponent(tfCessPer);
		cess.addComponent(tfCessValue);
		cess.setCaption("CESS");
		flColumn3.addComponent(cess);
		flColumn3.setComponentAlignment(cess, Alignment.TOP_LEFT);
		HorizontalLayout cst = new HorizontalLayout();
		cst.addComponent(tfCstPer);
		cst.addComponent(tfCstValue);
		cst.setCaption("CST");
		flColumn3.addComponent(cst);
		flColumn3.setComponentAlignment(cst, Alignment.TOP_LEFT);
		flColumn3.addComponent(tfSubTaxTotal);
		HorizontalLayout frgt = new HorizontalLayout();
		frgt.addComponent(tfFreightPer);
		frgt.addComponent(tfFreightValue);
		frgt.setCaption("Freight");
		flColumn3.addComponent(frgt);
		flColumn3.setComponentAlignment(frgt, Alignment.TOP_LEFT);
		HorizontalLayout other = new HorizontalLayout();
		other.addComponent(tfOtherPer);
		other.addComponent(tfOtherValue);
		other.setCaption("Other(%)");
		flColumn3.addComponent(other);
		flColumn3.setComponentAlignment(other, Alignment.TOP_LEFT);
		flColumn3.addComponent(tfGrandtotal);
		flColumn3.addComponent(tfpaymetTerms);
		flColumn3.addComponent(tfFreightTerms);
		flColumn3.addComponent(tfWarrentyTerms);
		flColumn4.addComponent(tfDelTerms);
		flColumn4.addComponent(cbStatus);
		flColumn4.addComponent(ckdutyexm);
		flColumn4.addComponent(ckCformRqu);
		flColumn4.addComponent(ckPdcRqu);
		flColumn4.addComponent(hlPODoc);
		HorizontalLayout hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flColumn1);
		hlHdr.addComponent(flColumn2);
		hlHdr.addComponent(flColumn3);
		hlHdr.addComponent(flColumn4);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding PODtl components
		// Add components for User Input Layout
		flDtlColumn1 = new FormLayout();
		flDtlColumn2 = new FormLayout();
		flDtlColumn3 = new FormLayout();
		flDtlColumn4 = new FormLayout();
		flDtlColumn5 = new FormLayout();
		flDtlColumn1.addComponent(cbproduct);
//		flDtlColumn2.addComponent(cbUom);
//		flDtlColumn2.addComponent(tfPOQnty);
		
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfPOQnty);
		//tfEnqQty.setRequired(true);
		hlQtyUom.addComponent(cbUom);
		hlQtyUom.setCaption("PO Qty");
		flDtlColumn2.addComponent(hlQtyUom);
		flDtlColumn2.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		
		
		flDtlColumn2.addComponent(tfUnitRate);
		flDtlColumn3.addComponent(tfBasicValue);
		flDtlColumn3.addComponent(cbPODtlStatus);
		flDtlColumn4.addComponent(taPODtlRemark);
		flDtlColumn5.addComponent(btnsavepurQuote);
		HorizontalLayout hlSmsQuotDtl = new HorizontalLayout();
		hlSmsQuotDtl.addComponent(flDtlColumn1);
		hlSmsQuotDtl.addComponent(flDtlColumn2);
		hlSmsQuotDtl.addComponent(flDtlColumn3);
		hlSmsQuotDtl.addComponent(flDtlColumn4);
		hlSmsQuotDtl.addComponent(flDtlColumn5);
		hlSmsQuotDtl.setSpacing(true);
		hlSmsQuotDtl.setMargin(true);
		VerticalLayout vlSmsQuoteHDR = new VerticalLayout();
		vlSmsQuoteHDR = new VerticalLayout();
		vlSmsQuoteHDR.addComponent(hlSmsQuotDtl);
		vlSmsQuoteHDR.addComponent(tblPurPO);
		vlSmsQuoteHDR.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlSmsQuoteHDR, "Purchase Order Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		VerticalLayout vlQuoteHdrDtl = new VerticalLayout();
		vlQuoteHdrDtl = new VerticalLayout();
		vlQuoteHdrDtl.addComponent(GERPPanelGenerator.createPanel(hlHdr));
		vlQuoteHdrDtl.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		vlQuoteHdrDtl.setSpacing(true);
		vlQuoteHdrDtl.setWidth("100%");
		hlUserInputLayout.addComponent(vlQuoteHdrDtl);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		btnsavepurQuote.setStyleName("add");
	}
	
	private void loadQuoteDetails() {
		// TODO Auto-generated method stub
		tfBasictotal.setReadOnly(false);
		tfBasictotal.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getBasicTotal().toString());
		tfBasictotal.setReadOnly(true);
		tfversionNo.setReadOnly(false);
		tfversionNo.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getQuoteVersion().toString());
		tfversionNo.setReadOnly(true);
		taRemark.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getRemarks().toString());
		tfpackingPer.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getPackingPrcnt().toString());
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getPackinValue().toString());
		tfPaclingValue.setReadOnly(true);
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getSubTotal().toString());
		tfSubTotal.setReadOnly(true);
		tfVatPer.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getVatPrcnt().toString());
		tfVatValue.setReadOnly(false);
		tfVatValue.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getVatValue().toString());
		tfVatValue.setReadOnly(true);
		tfEDPer.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getEdPrcnt().toString());
		tfEDValue.setReadOnly(false);
		tfEDValue.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getEdValue().toString());
		tfEDValue.setReadOnly(true);
		tfHEDPer.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getHeadPrcnt().toString());
		tfHEDValue.setReadOnly(false);
		tfHEDValue.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getHedValue().toString());
		tfHEDValue.setReadOnly(true);
		tfCessPer.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getCessPrcnt().toString());
		tfCessValue.setReadOnly(false);
		tfCessValue.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getCessValue().toString());
		tfCessValue.setReadOnly(true);
		tfCstPer.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getCstPrcnt().toString());
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getCstValue().toString());
		tfCstValue.setReadOnly(true);
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getSubTaxTotal().toString());
		tfSubTaxTotal.setReadOnly(true);
		tfFreightPer.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getFreightPrcnt().toString());
		tfFreightValue.setReadOnly(false);
		tfFreightValue.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getFreightValue().toString());
		tfFreightValue.setReadOnly(true);
		tfOtherPer.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getOtherPrcnt().toString());
		tfOtherValue.setReadOnly(false);
		tfOtherValue.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getOtherValue().toString());
		tfOtherValue.setReadOnly(true);
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getGrandTotal().toString());
		tfGrandtotal.setReadOnly(true);
		tfpaymetTerms.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getPaymentTerms().toString());
		tfFreightTerms.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getFreightTerms().toString());
		tfWarrentyTerms.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getWarrentyTerms().toString());
		tfDelTerms.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getDeliveryTerms().toString());
		// cbStatus.setValue(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getStatus().toString());
		// load quote details
		PurchasePODtllList = new ArrayList<PurchasePODtlDM>();
		for (PurchaseQuotDtlDM purchaseQuotDtlDM : servicePurchaseQuoteDtl.getPurchaseQuotDtlList(null,
				((PurchaseQuotHdrDM) cbquoteNo.getValue()).getQuoteId(), null)) {
			PurchasePODtlDM purchasePODtlDM = new PurchasePODtlDM();
			purchasePODtlDM.setProductId(purchaseQuotDtlDM.getProductId());
			purchasePODtlDM.setProductName(purchaseQuotDtlDM.getProductName());
			purchasePODtlDM.setUnitRate(purchaseQuotDtlDM.getUnitRate());
			purchasePODtlDM.setPoQty(purchaseQuotDtlDM.getEnquiryQty());
			purchasePODtlDM.setMaterialUom(purchaseQuotDtlDM.getUom());
			purchasePODtlDM.setBasicValue(purchaseQuotDtlDM.getBasicValue());
			purchasePODtlDM.setStatus("Active");
			purchasePODtlDM.setLastupdatedby(username);
			purchasePODtlDM.setLastupdateddt(DateUtils.getcurrentdate());
			PurchasePODtllList.add(purchasePODtlDM);
		}
		loadPurDtl();
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<PurchasePOHdrDM> purchasePOHdrList = new ArrayList<PurchasePOHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbBranch.getValue() + ", " + cbStatus.getValue());
		String poType = null;
		if (cbpoType.getValue() != null) {
			poType = cbpoType.getValue().toString();
		}
		purchasePOHdrList = servicepurchaePOHdr.getPurchaseOrdHdrList(companyid, (Long) cbBranch.getValue(), poType,
				(String) cbStatus.getValue(), tfPONo.getValue());
		recordCnt = purchasePOHdrList.size();
		System.out.println("ize===>"+recordCnt);
		beanPurchasePOHdr = new BeanItemContainer<PurchasePOHdrDM>(PurchasePOHdrDM.class);
		beanPurchasePOHdr.addAll(purchasePOHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanPurchasePOHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "poId", "branchName", "pono", "poType", "pOStatus",
				"lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "PO.No ", "Order Type", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("poId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadPurDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordCnt = PurchasePODtllList.size();
			tblPurPO.setPageLength(3);
			beanPurchasePODtl = new BeanItemContainer<PurchasePODtlDM>(PurchasePODtlDM.class);
			beanPurchasePODtl.addAll(PurchasePODtllList);
			BigDecimal sum = new BigDecimal("0");
			for (PurchasePODtlDM obj : PurchasePODtllList) {
				if (obj.getBasicValue() != null) {
					sum = sum.add(obj.getBasicValue());
				}
			}
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(sum.toString());
			tfBasictotal.setReadOnly(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxslap. result set");
			tblPurPO.setContainerDataSource(beanPurchasePODtl);
			tblPurPO.setVisibleColumns(new Object[] { "productName", "unitRate", "basicValue", "poQty",
					"lastupdateddt", "lastupdatedby" });
			tblPurPO.setColumnHeaders(new String[] { "Product Name", "Unit Rate", "Basic Value", "PO.Qty",
					"Last Updated Date", "Last Updated By" });
			tblPurPO.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadBranchList() {
		try {
			List<BranchDM> branchList = serviceBranch.getBranchList(null, null, null, "Active", companyid, "P");
			branchList.add(new BranchDM(0L, "All Branches"));
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(branchList);
			cbBranch.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Uom
	public void loadUomList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_UOM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbUom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadProduct() {
		try {
			List<PurchaseQuotDtlDM> getQuoteDtl = new ArrayList<PurchaseQuotDtlDM>();
			Long QuoteId = ((PurchaseQuotHdrDM) cbquoteNo.getValue()).getQuoteId();
			getQuoteDtl.addAll(servicePurchaseQuoteDtl.getPurchaseQuotDtlList(null, QuoteId, null));
			BeanItemContainer<PurchaseQuotDtlDM> beanPlnDtl = new BeanItemContainer<PurchaseQuotDtlDM>(
					PurchaseQuotDtlDM.class);
			beanPlnDtl.addAll(getQuoteDtl);
			cbproduct.setContainerDataSource(beanPlnDtl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * public void loadVendor() { try { List<VendorDM> vendorList = serviceVendor.getVendorList(null, null, companyid,
	 * null, null, null, null, null, "progress", null, "P"); BeanContainer<Long, VendorDM> beanVendor = new
	 * BeanContainer<Long, VendorDM>(VendorDM.class); beanVendor.setBeanIdProperty("vendorId");
	 * beanVendor.addAll(vendorList); cbVendor.setContainerDataSource(beanVendor); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */
	public void loadPOTypet() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_POTYPE");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbpoType.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public void loadPaymentTerms() {
//		try {
//			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
//			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
//					"SM_PAYTRM");
//			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
//			beanCompanyLookUp.setBeanIdProperty("lookupname");
//			beanCompanyLookUp.addAll(lookUpList);
//			tfpaymetTerms.setContainerDataSource(beanCompanyLookUp);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void loadFreightTerms() {
//		try {
//			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
//			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
//					"SM_FRTTRM");
//			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
//			beanCompanyLookUp.setBeanIdProperty("lookupname");
//			beanCompanyLookUp.addAll(lookUpList);
//			tfFreightTerms.setContainerDataSource(beanCompanyLookUp);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void loadWarentyTerms() {
//		try {
//			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
//			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
//					"SM_WRNTRM");
//			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
//			beanCompanyLookUp.setBeanIdProperty("lookupname");
//			beanCompanyLookUp.addAll(lookUpList);
//			tfWarrentyTerms.setContainerDataSource(beanCompanyLookUp);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void loadDeliveryTerms() {
//		try {
//			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
//			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
//					"SM_DELTRM");
//			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
//			beanCompanyLookUp.setBeanIdProperty("lookupname");
//			beanCompanyLookUp.addAll(lookUpList);
//			tfDelTerms.setContainerDataSource(beanCompanyLookUp);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
	private void loadQuoteNoList() {
		List<PurchaseQuotHdrDM> getQuoteNo = new ArrayList<PurchaseQuotHdrDM>();
		getQuoteNo.addAll(servicepurchaeQuoteHdr.getPurchaseQuotHdrList(null, companyid, null, null, "Progress", null,
				null, null, "f"));
		BeanItemContainer<PurchaseQuotHdrDM> beanQuote = new BeanItemContainer<PurchaseQuotHdrDM>(
				PurchaseQuotHdrDM.class);
		beanQuote.addAll(getQuoteNo);
		cbquoteNo.setContainerDataSource(beanQuote);
	}
	
	private void editPOHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected QuoteId -> "
				+ QuoteId);
		if (sltedRcd != null) {
			PurchasePOHdrDM editPurchaseOrdlist = beanPurchasePOHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			poId = editPurchaseOrdlist.getPoId();
			cbBranch.setValue(editPurchaseOrdlist.getBranchId());
			tfPONo.setReadOnly(false);
			tfPONo.setValue(editPurchaseOrdlist.getPono());
			tfPONo.setReadOnly(true);
			dfPODt.setValue(editPurchaseOrdlist.getPurchaseDate());
			taRemark.setValue(editPurchaseOrdlist.getPoRemark());
			tfversionNo.setValue(editPurchaseOrdlist.getVersionNo().toString());
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(editPurchaseOrdlist.getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfpackingPer.setValue(editPurchaseOrdlist.getPackingPrcnt().toString());
			tfPaclingValue.setReadOnly(false);
			tfPaclingValue.setValue(editPurchaseOrdlist.getPackingPrcnt().toString());
			tfPaclingValue.setReadOnly(true);
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(editPurchaseOrdlist.getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(editPurchaseOrdlist.getVatPrcnt().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(editPurchaseOrdlist.getVatValue().toString());
			tfVatValue.setReadOnly(true);
			tfEDPer.setValue(editPurchaseOrdlist.getEdPrcnt().toString());
			tfEDValue.setReadOnly(false);
			tfEDValue.setValue(editPurchaseOrdlist.getEdValue().toString());
			tfEDValue.setReadOnly(true);
			tfHEDPer.setValue(editPurchaseOrdlist.getHedPrcnt().toString());
			tfHEDValue.setReadOnly(false);
			tfHEDValue.setValue(editPurchaseOrdlist.getHedValue().toString());
			tfHEDValue.setReadOnly(true);
			tfCessPer.setValue(editPurchaseOrdlist.getCessPrcnt().toString());
			tfCessValue.setReadOnly(false);
			tfCessValue.setValue(editPurchaseOrdlist.getCessValue().toString());
			tfCessValue.setReadOnly(true);
			tfCstPer.setValue(editPurchaseOrdlist.getCstPrcnt().toString());
			tfCstValue.setReadOnly(false);
			tfCstValue.setValue(editPurchaseOrdlist.getCstValue().toString());
			tfCstValue.setReadOnly(true);
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(editPurchaseOrdlist.getSubTaxTotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			tfFreightPer.setValue(editPurchaseOrdlist.getFrgtPrcnt().toString());
			tfFreightValue.setReadOnly(false);
			tfFreightValue.setValue(editPurchaseOrdlist.getFrgtValue().toString());
			tfFreightValue.setReadOnly(true);
			tfOtherPer.setValue((editPurchaseOrdlist.getOthersPrcnt().toString()));
			tfOtherValue.setReadOnly(false);
			tfOtherValue.setValue((editPurchaseOrdlist.getOthersValue().toString()));
			tfOtherValue.setReadOnly(true);
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(editPurchaseOrdlist.getGrandTotal().toString());
			tfGrandtotal.setReadOnly(true);
			if (editPurchaseOrdlist.getPaymentTerms() != null) {
				tfpaymetTerms.setValue(editPurchaseOrdlist.getPaymentTerms().toString());
			}
			if (editPurchaseOrdlist.getFrnghtTerms() != null) {
				tfFreightTerms.setValue(editPurchaseOrdlist.getFrnghtTerms());
			}
			if (editPurchaseOrdlist.getWrntyTerms() != null) {
				tfWarrentyTerms.setValue(editPurchaseOrdlist.getWrntyTerms());
			}
			if ((editPurchaseOrdlist.getDlvryTerms() != null)) {
				tfDelTerms.setValue(editPurchaseOrdlist.getDlvryTerms());
			}
			if (editPurchaseOrdlist.getpoType() != null) {
				cbpoType.setValue(editPurchaseOrdlist.getpoType());
			}
			if (editPurchaseOrdlist.getShippingAddr() != null) {
				taShpnAddr.setValue(editPurchaseOrdlist.getShippingAddr());
			}
			if (editPurchaseOrdlist.getInvoiceAddress() != null) {
				taInvoiceOrd.setValue(editPurchaseOrdlist.getInvoiceAddress());
			}
			if (editPurchaseOrdlist.getVendorId() != null) {
				tfvendor.setReadOnly(false);
				tfvendor.setValue(editPurchaseOrdlist.getVendorName());
				tfvendor.setReadOnly(true);
			}
			// if (hlPODoc != null) {
			// if (sltedRcd.getItemProperty("quoteDoc").getValue() != null) {
			// byte[] certificate = (byte[]) sltedRcd.getItemProperty("quoteDoc").getValue();
			// UploadDocumentUI test = new UploadDocumentUI(hlPODoc);
			// test.displaycertificate(certificate);
			// } else {
			// new UploadDocumentUI(hlPODoc);
			// }
			// }
			Long uom = editPurchaseOrdlist.getQuoteId();
			Collection<?> uomid = cbquoteNo.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbquoteNo.getItem(itemId);
				// Get the actual bean and use the data
				PurchaseQuotHdrDM st = (PurchaseQuotHdrDM) item.getBean();
				if (uom != null && uom.equals(st.getQuoteId())) {
					cbquoteNo.setValue(itemId);
				}
			}
			if (editPurchaseOrdlist.getDutyExempt().equals("Y")) {
				ckdutyexm.setValue(true);
			} else {
				ckdutyexm.setValue(false);
			}
			if (editPurchaseOrdlist.getCformReqd().equals("Y")) {
				ckCformRqu.setValue(true);
			} else {
				ckCformRqu.setValue(false);
			}
			if (editPurchaseOrdlist.getpDCReqd().equals("Y")) {
				ckPdcRqu.setValue(true);
			} else {
				ckPdcRqu.setValue(false);
			}
			if (editPurchaseOrdlist.getCasePoYn().equals("Y")) {
				ckcasePO.setValue(true);
			} else {
				ckcasePO.setValue(false);
			}
			cbStatus.setValue(editPurchaseOrdlist.getpOStatus());
			PurchasePODtllList = servicePurchasePODtl.getPurchaseOrdDtlList(null, poId, null, null);
		}
		loadPurDtl();
		comments = new SmsComments(vlTableForm, null, companyid, null, null, poId, null, null, null, null, null, null,
				status);
		comments.loadsrch(true, null, null, null, null, poId, null, null, null, null, null, null, null);
		comments.commentList = serviceComment.getSmsCommentsList(null, null, null, null, poId, null, null, null, null,
				null, null, null);
		System.out.println("PoID=>" + poId);
	}
	
	private void editPODtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		Item sltedRcd = tblPurPO.getItem(tblPurPO.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected QuoteId -> "
				+ QuoteId);
		if (sltedRcd != null) {
			PurchasePODtlDM editPurchaseOrdDtllist = beanPurchasePODtl.getItem(tblPurPO.getValue()).getBean();
			pODtlId = editPurchaseOrdDtllist.getPoDTlID();
			Long uom = editPurchaseOrdDtllist.getProductId();
			Collection<?> uomid = cbproduct.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbproduct.getItem(itemId);
				// Get the actual bean and use the data
				PurchaseQuotDtlDM st = (PurchaseQuotDtlDM) item.getBean();
				if (uom != null && uom.equals(st.getProductId())) {
					cbproduct.setValue(itemId);
				}
			}
			if (editPurchaseOrdDtllist.getPoQty() != null) {
				tfPOQnty.setReadOnly(false);
				tfPOQnty.setValue(editPurchaseOrdDtllist.getPoQty().toString());
				tfPOQnty.setReadOnly(true);
			}
			tfUnitRate.setReadOnly(false);
			tfUnitRate.setValue(editPurchaseOrdDtllist.getUnitRate().toString());
			tfUnitRate.setReadOnly(true);
			cbUom.setValue(editPurchaseOrdDtllist.getMaterialUom());
			tfBasicValue.setReadOnly(false);
			tfBasicValue.setValue(editPurchaseOrdDtllist.getBasicValue().toString());
			tfBasicValue.setReadOnly(true);
			taPODtlRemark.setValue(editPurchaseOrdDtllist.getRemarks());
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(null);
		cbpoType.setValue(null);
		tfPONo.setValue("");
		cbBranch.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		cbproduct.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		cbBranch.setRequired(true);
		cbpoType.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		tfBasictotal.setReadOnly(true);
		tfSubTotal.setReadOnly(true);
		tfSubTaxTotal.setReadOnly(true);
		tfGrandtotal.setReadOnly(true);
		tfPaclingValue.setReadOnly(true);
		tfVatValue.setReadOnly(true);
		tfEDValue.setReadOnly(true);
		tfHEDValue.setReadOnly(true);
		tfCessValue.setReadOnly(true);
		tfCstValue.setReadOnly(true);
		tfFreightValue.setReadOnly(true);
		tfOtherValue.setReadOnly(true);
		loadPurDtl();
		assembleInputUserLayout();
		new UploadDocumentUI(hlPODoc);
		tfPONo.setReadOnly(true);
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_NPONO ");
		tfPONo.setReadOnly(true);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfPONo.setReadOnly(true);
			} else {
				tfPONo.setReadOnly(false);
			}
			resetFields();
			btnsavepurQuote.setCaption("Add");
			tblPurPO.setVisible(true);
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		cbproduct.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		cbBranch.setRequired(true);
		tfPONo.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		resetFields();
		editPODtl();
		editPOHdr();
		comments.loadsrch(true, null, null, null, null, poId, null, null, null, null, null, null, null);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		cbpoType.setComponentError(null);
		cbquoteNo.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if (cbpoType.getValue() == null) {
			cbpoType.setComponentError(new UserError(GERPErrorCodes.ORDER_TYPE));
			errorFlag = true;
		}
		if ((cbquoteNo.getValue() == null)) {
			cbquoteNo.setComponentError(new UserError(GERPErrorCodes.QUOTE_NO));
			errorFlag = true;
		}
		// DtlValidation();
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean DtlValidation() {
		boolean isValid = true;
		if (cbproduct.getValue() == null) {
			cbproduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			cbproduct.setComponentError(null);
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			PurchasePOHdrDM PurchaseHdrobj = new PurchasePOHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				PurchaseHdrobj = beanPurchasePOHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
				if (tfPONo.getValue() != null) {
					PurchaseHdrobj.setPono(tfPONo.getValue());
				}
			} else {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_NPONO");
				logger.info("Serial No Generation  Data...===> " + companyid + "," + branchId + "," + moduleId);
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						PurchaseHdrobj.setPono(slnoObj.getKeyDesc());
					}
				}
			}
			PurchaseHdrobj.setBranchId((Long) cbBranch.getValue());
			PurchaseHdrobj.setCompanyId(companyid);
			PurchaseHdrobj.setPurchaseDate(dfPODt.getValue());
			PurchaseHdrobj.setPoRemark(taRemark.getValue());
			PurchaseHdrobj.setpoType(cbpoType.getValue().toString());
			PurchaseHdrobj.setVendorName(tfvendor.getValue());
			PurchaseHdrobj.setVersionNo((Long.valueOf(tfversionNo.getValue())));
			PurchaseHdrobj.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			PurchaseHdrobj.setPackingPrcnt((new BigDecimal(tfpackingPer.getValue())));
			PurchaseHdrobj.setPackingVL(new BigDecimal(tfPaclingValue.getValue()));
			PurchaseHdrobj.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			PurchaseHdrobj.setVatPrcnt(((new BigDecimal(tfVatPer.getValue()))));
			PurchaseHdrobj.setVatValue(new BigDecimal(tfVatValue.getValue()));
			PurchaseHdrobj.setEdPrcnt((new BigDecimal(tfEDPer.getValue())));
			PurchaseHdrobj.setEdValue((new BigDecimal(tfEDValue.getValue())));
			PurchaseHdrobj.setHedValue(new BigDecimal(tfHEDValue.getValue()));
			PurchaseHdrobj.setHedPrcnt((new BigDecimal(tfHEDPer.getValue())));
			PurchaseHdrobj.setCessPrcnt(new BigDecimal(tfCessPer.getValue()));
			PurchaseHdrobj.setCessValue(new BigDecimal(tfCessValue.getValue()));
			PurchaseHdrobj.setCstPrcnt((new BigDecimal(tfCstPer.getValue())));
			PurchaseHdrobj.setCstValue(new BigDecimal(tfCstValue.getValue()));
			PurchaseHdrobj.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			PurchaseHdrobj.setFrgtPrcnt(new BigDecimal(tfFreightPer.getValue()));
			PurchaseHdrobj.setFrgtValue(new BigDecimal(tfFreightValue.getValue()));
			PurchaseHdrobj.setOthersPrcnt(new BigDecimal(tfOtherPer.getValue()));
			PurchaseHdrobj.setOthersValue(new BigDecimal(tfOtherValue.getValue()));
			PurchaseHdrobj.setGrandTotal(new BigDecimal(tfGrandtotal.getValue()));
			if (tfpaymetTerms.getValue() != null) {
				PurchaseHdrobj.setPaymentTerms((tfpaymetTerms.getValue().toString()));
			}
			if (tfFreightTerms.getValue() != null) {
				PurchaseHdrobj.setFrnghtTerms(tfFreightTerms.getValue().toString());
			}
			if (tfWarrentyTerms.getValue() != null) {
				PurchaseHdrobj.setWrntyTerms((tfWarrentyTerms.getValue().toString()));
			}
			if (tfDelTerms.getValue() != null) {
				PurchaseHdrobj.setDlvryTerms(tfDelTerms.getValue().toString());
			}
			if (ckdutyexm.getValue().equals(true)) {
				PurchaseHdrobj.setDutyExempt("Y");
			} else if (ckdutyexm.getValue().equals(false)) {
				PurchaseHdrobj.setDutyExempt("N");
			}
			if (ckCformRqu.getValue().equals(true)) {
				PurchaseHdrobj.setCformReqd("Y");
			} else if (ckCformRqu.getValue().equals(false)) {
				PurchaseHdrobj.setCformReqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				PurchaseHdrobj.setpDCReqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				PurchaseHdrobj.setpDCReqd("N");
			}
			if (ckcasePO.getValue().equals(true)) {
				PurchaseHdrobj.setCasePoYn("Y");
			} else if (ckcasePO.getValue().equals(false)) {
				PurchaseHdrobj.setCasePoYn("N");
			}
			if (cbquoteNo.getValue() != null) {
				PurchaseHdrobj.setQuoteId(((PurchaseQuotHdrDM) cbquoteNo.getValue()).getQuoteId());
			}
			PurchaseHdrobj.setShippingAddr(taShpnAddr.getValue());
			PurchaseHdrobj.setInvoiceAddress(taInvoiceOrd.getValue());
			if (cbStatus.getValue().toString() != null) {
				PurchaseHdrobj.setpOStatus(cbStatus.getValue().toString());
			}
			PurchaseHdrobj.setPreparedBy(EmployeeId);
			PurchaseHdrobj.setReviewedBy(null);
			PurchaseHdrobj.setActionedBY(null);
			PurchaseHdrobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			PurchaseHdrobj.setLastUpdatedBy(username);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			PurchaseHdrobj.setPoDoc(fileContents);
			servicepurchaePOHdr.saveorUpdatePurchaseOrdHdrDetails(PurchaseHdrobj);
			@SuppressWarnings("unchecked")
			Collection<PurchasePODtlDM> itemIds = (Collection<PurchasePODtlDM>) tblPurPO.getVisibleItemIds();
			for (PurchasePODtlDM save : (Collection<PurchasePODtlDM>) itemIds) {
				save.setPoId(Long.valueOf(PurchaseHdrobj.getPoId().toString()));
				servicePurchasePODtl.saveorUpdatePurchaseOrdDtlDetails(save);
			}
			comments.savePurchaseOrder(PurchaseHdrobj.getPoId(), PurchaseHdrobj.getpOStatus());
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_NPONO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_NPONO");
						System.out.println("Serial no=>" + companyid + "," + moduleId + "," + branchId);
					}
				}
			}
			PoDtlresetFields();
			resetFields();
			loadSrchRslt();
			poId = 0L;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void savePurchaseQuoteDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			int count = 0;
			for (PurchasePODtlDM purchasePODtlDM : PurchasePODtllList) {
				if (purchasePODtlDM.getProductId() == ((PurchaseQuotDtlDM) cbproduct.getValue()).getProductId()) {
					count++;
					break;
				}
			}
			System.out.println("count--->" + count);
			if (count == 0) {
				PurchasePODtlDM purchaseOrderDtlobj = new PurchasePODtlDM();
				if (tblPurPO.getValue() != null) {
					purchaseOrderDtlobj = beanPurchasePODtl.getItem(tblPurPO.getValue()).getBean();
					PurchasePODtllList.remove(purchaseOrderDtlobj);
				}
				purchaseOrderDtlobj.setProductId(((PurchaseQuotDtlDM) cbproduct.getValue()).getProductId());
				purchaseOrderDtlobj.setProductName(((PurchaseQuotDtlDM) cbproduct.getValue()).getProductName());
				purchaseOrderDtlobj.setPoQty((Long.valueOf(tfPOQnty.getValue())));
				purchaseOrderDtlobj.setUnitRate((Long.valueOf(tfUnitRate.getValue())));
				purchaseOrderDtlobj.setMaterialUom(cbUom.getValue().toString());
				purchaseOrderDtlobj.setBasicValue((new BigDecimal(tfBasicValue.getValue())));
				purchaseOrderDtlobj.setRemarks(taPODtlRemark.getValue());
				purchaseOrderDtlobj.setLastupdateddt(DateUtils.getcurrentdate());
				purchaseOrderDtlobj.setLastupdatedby(username);
				PurchasePODtllList.add(purchaseOrderDtlobj);
				loadPurDtl();
				getCalculatedValues();
			} else {
				cbproduct.setComponentError(new UserError("Product Already Exist.."));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		PoDtlresetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for TestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_P_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", poid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		cbproduct.setRequired(false);
		cbBranch.setRequired(false);
		cbpoType.setRequired(false);
		tfPONo.setRequired(false);
		resetFields();
		PoDtlresetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfPONo.setReadOnly(false);
		tfPONo.setValue("");
		tfBasictotal.setReadOnly(false);
		tfBasictotal.setValue("0");
		// tfCessPer.setValue("10");
		tfCessValue.setReadOnly(false);
		tfCessValue.setValue("0");
		ckCformRqu.setValue(false);
		// tfCstPer.setValue("10");
		tfDelTerms.setValue("");
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue("0");
		ckdutyexm.setValue(false);
		// tfEDPer.setValue("10");
		tfEDValue.setReadOnly(false);
		tfEDValue.setValue("0");
		tfWarrentyTerms.setValue("");
		tfVatValue.setReadOnly(false);
		tfVatValue.setValue("0");
		// tfVatPer.setValue("10");
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue("0");
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue("0");
		tfversionNo.setReadOnly(false);
		tfversionNo.setValue("0");
		cbquoteNo.setValue(null);
		tfpaymetTerms.setValue("");
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue("0");
		// tfpackingPer.setValue("10");
		tfOtherValue.setReadOnly(false);
		tfOtherValue.setValue("0");
		// tfOtherPer.setValue("10");
		tfHEDValue.setReadOnly(false);
		tfHEDValue.setValue("0");
		// tfHEDPer.setValue("10");
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue("");
		tfFreightValue.setReadOnly(false);
		tfFreightValue.setValue("0");
		// tfFreightPer.setValue("10");
		tfFreightTerms.setValue("");
		cbproduct.setValue(null);
		cbStatus.setValue(null);
		cbBranch.setValue(branchId);
		dfPODt.setValue(new Date());
		taRemark.setValue("");
		cbBranch.setComponentError(null);
		ckPdcRqu.setValue(false);
		PurchasePODtllList = new ArrayList<PurchasePODtlDM>();
		tblPurPO.removeAllItems();
		new UploadDocumentUI(hlPODoc);
		cbpoType.setValue(null);
		tfvendor.setReadOnly(false);
		tfvendor.setValue("");
		tfvendor.setReadOnly(true);
		taInvoiceOrd.setValue("");
		taShpnAddr.setValue("");
		ckcasePO.setValue(false);
		cbpoType.setComponentError(null);
		cbquoteNo.setComponentError(null);
		cbproduct.setContainerDataSource(null);
	}
	
	protected void PoDtlresetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbproduct.setValue(null);
		tfPOQnty.setReadOnly(false);
		tfPOQnty.setValue("0");
		tfPOQnty.setReadOnly(true);
		tfUnitRate.setReadOnly(false);
		tfUnitRate.setValue("0");
		tfUnitRate.setReadOnly(true);
		cbUom.setReadOnly(false);
		cbUom.setValue(null);
		cbUom.setReadOnly(true);
		tfBasicValue.setReadOnly(false);
		tfBasicValue.setValue("0");
		tfBasicValue.setReadOnly(true);
		taPODtlRemark.setValue("");
		cbPODtlStatus.setValue(cbPODtlStatus.getItemIds().iterator().next());
		btnsavepurQuote.setCaption("Add");
	}
	
	private void getCalculatedValues() {
		BigDecimal basictotal = new BigDecimal(tfBasictotal.getValue());
		BigDecimal packingvalue = gerPercentageValue(new BigDecimal(tfpackingPer.getValue()), basictotal);
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue(packingvalue.toString());
		tfPaclingValue.setReadOnly(true);
		BigDecimal subtotal = packingvalue.add(basictotal);
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue(subtotal.toString());
		tfSubTotal.setReadOnly(true);
		BigDecimal vatvalue = gerPercentageValue(new BigDecimal(tfVatPer.getValue()), subtotal);
		tfVatValue.setReadOnly(false);
		tfVatValue.setValue(vatvalue.toString());
		tfVatValue.setReadOnly(true);
		BigDecimal edValue = gerPercentageValue(new BigDecimal(tfEDPer.getValue()), subtotal);
		tfEDValue.setReadOnly(false);
		tfEDValue.setValue(edValue.toString());
		tfEDValue.setReadOnly(true);
		BigDecimal hedValue = gerPercentageValue(new BigDecimal(tfHEDPer.getValue()), subtotal);
		tfHEDValue.setReadOnly(false);
		tfHEDValue.setValue(hedValue.toString());
		tfHEDValue.setReadOnly(true);
		BigDecimal cessval = gerPercentageValue(new BigDecimal(tfCessPer.getValue()), subtotal);
		tfCessValue.setReadOnly(false);
		tfCessValue.setValue(cessval.toString());
		tfCessValue.setReadOnly(true);
		BigDecimal cstval = gerPercentageValue(new BigDecimal(tfCstPer.getValue()), subtotal);
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue(cstval.toString());
		tfCstValue.setReadOnly(true);
		BigDecimal csttotal = vatvalue.add(edValue).add(hedValue).add(cessval).add(cstval);
		BigDecimal subtaxTotal = subtotal.add(csttotal);
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue(subtaxTotal.toString());
		tfSubTaxTotal.setReadOnly(true);
		BigDecimal frgval = gerPercentageValue(new BigDecimal(tfFreightPer.getValue()), subtaxTotal);
		tfFreightValue.setReadOnly(false);
		tfFreightValue.setValue(frgval.toString());
		tfFreightValue.setReadOnly(true);
		BigDecimal otherval = gerPercentageValue(new BigDecimal(tfOtherPer.getValue()), subtaxTotal);
		tfOtherValue.setReadOnly(false);
		tfOtherValue.setValue(otherval.toString());
		tfOtherValue.setReadOnly(true);
		BigDecimal Grand = frgval.add(otherval);
		BigDecimal GranTotal = subtaxTotal.add(Grand);
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue(GranTotal.toString());
		tfGrandtotal.setReadOnly(true);
	}
	
	private BigDecimal gerPercentageValue(BigDecimal percent, BigDecimal value) {
		return (percent.multiply(value).divide(new BigDecimal("100"))).setScale(2, RoundingMode.CEILING);
	}
}