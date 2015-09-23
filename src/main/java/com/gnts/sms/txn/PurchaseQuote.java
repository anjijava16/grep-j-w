/**
 * File Name 		: PurhaseQuot.java 
 * Description 		: this class is used for add/edit PurhaseQuot  details. 
 * Author 			: Ganga
 * Date 			: Aug 18, 2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Aug 18, 2014          Ganga                  Initial Version 
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
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.ProductService;
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
import com.gnts.sms.domain.txn.EnquiryVendorDtlDM;
import com.gnts.sms.domain.txn.PurchaseQuotDtlDM;
import com.gnts.sms.domain.txn.PurchaseQuotHdrDM;
import com.gnts.sms.domain.txn.SmsPurEnqDtlDM;
import com.gnts.sms.domain.txn.SmsPurEnqHdrDM;
import com.gnts.sms.service.mst.SmsTaxesService;
import com.gnts.sms.service.txn.EnquiryVendorDtlService;
import com.gnts.sms.service.txn.PurchaseQuotHdrService;
import com.gnts.sms.service.txn.PurchaseQuoteDtlService;
import com.gnts.sms.service.txn.SmsCommentsService;
import com.gnts.sms.service.txn.SmsPurEnqDtlService;
import com.gnts.sms.service.txn.SmsPurEnqHdrService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
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

public class PurchaseQuote extends BaseUI {
	private EnquiryVendorDtlService serviceEnquiryVendorDtl = (EnquiryVendorDtlService) SpringContextHelper
			.getBean("purvendor");
	private SmsCommentsService serviceComment = (SmsCommentsService) SpringContextHelper.getBean("smsComments");
	private PurchaseQuotHdrService servicePurQuoteHdr = (PurchaseQuotHdrService) SpringContextHelper
			.getBean("PurchaseQuot");
	private PurchaseQuoteDtlService servicePurchaseQuoteDtl = (PurchaseQuoteDtlService) SpringContextHelper
			.getBean("PurchaseQuotDtl");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private SmsPurEnqHdrService servicePurEnqHdr = (SmsPurEnqHdrService) SpringContextHelper.getBean("SmsPurEnqHdr");
	private SmsPurEnqDtlService servicePurEnqDtl = (SmsPurEnqDtlService) SpringContextHelper.getBean("SmsPurEnqDtl");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private SmsTaxesService serviceTaxes = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5;
	// form layout for input controls for Quote Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4, flDtlColumn5;
	// // User Input Components for Quote Details
	private ComboBox cbBranch, cbStatus, cbEnqNo, cbVendorName;
	private TextField tfQuoteRef, tfQuoteVersion, tfBasictotal, tfpackingPer, tfPaclingValue, tfCityName, tfvendorName;
	private TextField tfSubTotal, tfVatPer, tfVatValue, tfEDPer, tfEDValue, tfHEDPer;
	private TextField tfHEDValue, tfCessPer, tfCessValue, tfCstPer, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightPer, tfFreightValue, tfOtherPer, tfOtherValue, tfGrandtotal;
	private TextField tfPaymetTerms, tfFreightTerms, tfWarrentyTerms, tfDelTerms;
	private TextArea taRemark, taVendorAddr;
	private PopupDateField dfQuoteDt, dfValidDt;
	private CheckBox ckDutyexm, ckPDCRqu, ckCformRqu;
	private Button btnSavepurQuote = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlquoteDoc = new VerticalLayout();
	// QuoteDtl components
	private ComboBox cbProduct, cbUom;
	private TextField tfQuoteQunt, tfUnitRate, tfBasicValue;
	private TextArea taQuoteRemark;
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private BeanItemContainer<PurchaseQuotHdrDM> beanQuoteHdr = null;
	private BeanItemContainer<PurchaseQuotDtlDM> beanQuoteDtl = null;
	private List<PurchaseQuotDtlDM> listQuoteDetails = new ArrayList<PurchaseQuotDtlDM>();
	// local variables declaration
	private String username;
	private Long companyid;
	private SmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private GERPTable tblPurQuDtl;
	private int recordCnt;
	private Long quoteId;
	private Long employeeId;
	private File file;
	private Long roleId;
	private Long branchId;
	private Long screenId;
	private String quoteid;
	// Initialize logger
	private Logger logger = Logger.getLogger(PurchaseQuote.class);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private String status;
	
	// Constructor received the parameters from Login UI class
	public PurchaseQuote() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PurchaseQuote() constructor");
		// Loading the PurchaseQuote UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax UI");
		// Initialization for PurchaseQuote Details user input components
		cbVendorName = new ComboBox("Vendor Name");
		cbVendorName.setItemCaptionPropertyId("vendorName");
		cbVendorName.setWidth("150");
		tfQuoteRef = new TextField("Quote Ref");
		tfQuoteRef.setWidth("150");
		tfvendorName = new TextField("Vendor Name");
		tfvendorName.setWidth("150");
		tfvendorName.setImmediate(true);
		dfQuoteDt = new GERPPopupDateField("Quote Date");
		dfQuoteDt.setInputPrompt("Select Date");
		dfQuoteDt.setWidth("130");
		dfValidDt = new GERPPopupDateField("Valid Date");
		dfValidDt.setInputPrompt("Select Date");
		dfValidDt.setWidth("130");
		cbEnqNo = new ComboBox("Enquiry No");
		cbEnqNo.setItemCaptionPropertyId("enquiryNo");
		cbEnqNo.setWidth("150");
		cbEnqNo.setImmediate(true);
		loadEnquiryNo();
		cbEnqNo.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				if (item != null) {
					loadVendorList();
				}
			}
		});
		cbEnqNo.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				if (item != null) {
					loadProductList();
				}
			}
		});
		taRemark = new TextArea("Remarks");
		taRemark.setWidth("150");
		taRemark.setHeight("50");
		tfQuoteVersion = new TextField("Quote Version");
		tfBasictotal = new TextField("Basic total");
		tfSubTotal = new TextField("Sub Total");
		tfSubTotal.setWidth("145");
		tfpackingPer = new TextField();
		tfpackingPer.setWidth("30");
		tfPaclingValue = new TextField();
		tfPaclingValue.setWidth("120");
		tfPaclingValue.setImmediate(true);
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
		tfSubTaxTotal = new TextField("Sub Tax Total");
		tfCstValue = new TextField();
		tfCstValue.setWidth("120");
		tfSubTaxTotal.setWidth("145");
		tfCstPer = new TextField();
		tfCstPer.setWidth("30");
		tfCstValue.setImmediate(true);
		tfGrandtotal = new TextField("Grand Total");
		tfGrandtotal.setWidth("150");
		tfFreightPer = new TextField();
		tfFreightPer.setWidth("30");
		tfFreightValue = new TextField();
		tfFreightValue.setWidth("120");
		tfOtherPer = new TextField();
		tfOtherPer.setWidth("30");
		tfOtherValue = new TextField();
		tfOtherValue.setWidth("125");
		tfPaymetTerms = new TextField("Payment Terms");
		tfPaymetTerms.setWidth("150");
		tfFreightTerms = new TextField("Freight Terms");
		tfFreightTerms.setWidth("150");
		tfWarrentyTerms = new TextField("Warrenty Terms");
		tfWarrentyTerms.setWidth("150");
		tfDelTerms = new TextField("Delivery Terms");
		tfDelTerms.setWidth("150");
		ckDutyexm = new CheckBox("Duty Exempted");
		ckCformRqu = new CheckBox("Cfrom Req");
		ckPDCRqu = new CheckBox("PDC Req");
		cbBranch = new ComboBox("Branch Name");
		cbBranch.setWidth("150");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		try {
			ApprovalSchemaDM obj = servicePurQuoteHdr.getReviewerId(companyid, screenId, branchId, roleId).get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_QUOTE_HDR, BASEConstants.QUOTE_STATUS_RV);
			} else {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_QUOTE_HDR, BASEConstants.QUOTE_STATUS);
			}
		}
		catch (Exception e) {
		}
		cbStatus.setWidth("120");
		tfCityName = new TextField("City Name");
		tfCityName.setWidth("150");
		taVendorAddr = new TextArea("Vendor Add");
		taVendorAddr.setWidth("150");
		taVendorAddr.setHeight("50");
		// Purchase QuoteDtl Comp
		cbProduct = new ComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("pordName");
		cbProduct.setWidth("130");
		cbProduct.setImmediate(true);
		cbProduct.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbProduct.getValue() != null) {
					tfQuoteQunt.setReadOnly(false);
					tfQuoteQunt.setValue(((SmsPurEnqDtlDM) cbProduct.getValue()).getEnquiryQty() + "");
					tfQuoteQunt.setReadOnly(true);
					cbUom.setReadOnly(false);
					cbUom.setValue(((SmsPurEnqDtlDM) cbProduct.getValue()).getProductUom() + "");
					cbUom.setReadOnly(true);
				}
			}
		});
		loadProduct();
		tfQuoteQunt = new TextField();
		tfQuoteQunt.setWidth("90");
		tfQuoteQunt.setValue("0");
		tfQuoteQunt.setImmediate(true);
		cbUom = new ComboBox();
		cbUom.setItemCaptionPropertyId("lookupname");
		cbUom.setWidth("77");
		cbUom.setHeight("23");
		loadUomList();
		tfBasicValue = new TextField("Basic value");
		tfBasicValue.setWidth("130");
		tfBasicValue.setValue("0");
		tfBasicValue.setImmediate(true);
		tfUnitRate = new TextField("Unit Rate");
		tfUnitRate.setWidth("130");
		tfUnitRate.setValue("0");
		tfUnitRate.setImmediate(true);
		tfUnitRate.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				try {
					tfBasicValue.setReadOnly(false);
					tfBasicValue.setValue((new BigDecimal(tfUnitRate.getValue())).multiply(
							new BigDecimal(tfQuoteQunt.getValue())).toString());
					tfBasicValue.setReadOnly(true);
				}
				catch (Exception e) {
				}
			}
		});
		tfQuoteQunt.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				try {
					tfBasicValue.setReadOnly(false);
					tfBasicValue.setValue((new BigDecimal(tfUnitRate.getValue())).multiply(
							new BigDecimal(tfQuoteQunt.getValue())).toString());
					tfBasicValue.setReadOnly(true);
				}
				catch (Exception e) {
				}
			}
		});
		taQuoteRemark = new TextArea("Quote Remark");
		taQuoteRemark.setWidth("130");
		taQuoteRemark.setHeight("50");
		btnSavepurQuote.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (dtlValidation()) {
					savePurchaseQuoteDetails();
				}
			}
		});
		btndelete.setEnabled(false);
		tblPurQuDtl = new GERPTable();
		tblPurQuDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblPurQuDtl.isSelected(event.getItemId())) {
					tblPurQuDtl.setImmediate(true);
					btnSavepurQuote.setCaption("Add");
					btnSavepurQuote.setStyleName("savebt");
					btndelete.setEnabled(false);
					quoteDtlresetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnSavepurQuote.setCaption("Update");
					btnSavepurQuote.setStyleName("savebt");
					btndelete.setEnabled(true);
					editQuoteDtl();
				}
			}
		});
		btndelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndelete == event.getButton()) {
					deleteDetails();
				}
			}
		});
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		loadQuoteDtl();
		btnSavepurQuote.setStyleName("add");
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
		flColumn5 = new GERPFormLayout();
		// Adding components into form layouts for TestType UI search layout
		flColumn1.addComponent(cbBranch);
		flColumn2.addComponent(tfvendorName);
		flColumn3.addComponent(tfQuoteRef);
		flColumn4.addComponent(cbEnqNo);
		flColumn5.addComponent(cbStatus);
		// Adding form layouts into search layout for TestType UI search mode
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.addComponent(flColumn5);
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
		flColumn1.addComponent(cbEnqNo);
		flColumn1.addComponent(cbVendorName);
		flColumn1.addComponent(tfQuoteRef);
		flColumn1.addComponent(dfQuoteDt);
		flColumn1.addComponent(dfValidDt);
		flColumn1.addComponent(taRemark);
		flColumn1.addComponent(tfQuoteVersion);
		flColumn1.addComponent(tfBasictotal);
		HorizontalLayout pp = new HorizontalLayout();
		pp.addComponent(tfpackingPer);
		pp.addComponent(tfPaclingValue);
		pp.setCaption("Packing(%)");
		flColumn2.addComponent(pp);
		flColumn2.setComponentAlignment(pp, Alignment.TOP_LEFT);
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
		HorizontalLayout cess = new HorizontalLayout();
		cess.addComponent(tfCessPer);
		cess.addComponent(tfCessValue);
		cess.setCaption("CESS");
		flColumn2.addComponent(cess);
		flColumn2.setComponentAlignment(cess, Alignment.TOP_LEFT);
		HorizontalLayout cst = new HorizontalLayout();
		cst.addComponent(tfCstPer);
		cst.addComponent(tfCstValue);
		cst.setCaption("CST");
		flColumn2.addComponent(cst);
		flColumn2.setComponentAlignment(cst, Alignment.TOP_LEFT);
		flColumn2.addComponent(tfSubTaxTotal);
		HorizontalLayout frght = new HorizontalLayout();
		frght.addComponent(tfFreightPer);
		frght.addComponent(tfFreightValue);
		frght.setCaption("Freight");
		flColumn2.addComponent(frght);
		flColumn2.setComponentAlignment(frght, Alignment.TOP_LEFT);
		HorizontalLayout other = new HorizontalLayout();
		other.addComponent(tfOtherPer);
		other.addComponent(tfOtherValue);
		other.setCaption("Other(%)");
		flColumn3.addComponent(other);
		flColumn3.setComponentAlignment(other, Alignment.TOP_LEFT);
		flColumn3.addComponent(tfGrandtotal);
		flColumn3.addComponent(tfPaymetTerms);
		flColumn3.addComponent(tfFreightTerms);
		flColumn3.addComponent(tfWarrentyTerms);
		flColumn3.addComponent(tfDelTerms);
		flColumn3.addComponent(tfvendorName);
		flColumn3.addComponent(taVendorAddr);
		flColumn3.addComponent(tfCityName);
		flColumn4.addComponent(cbStatus);
		flColumn4.addComponent(ckDutyexm);
		flColumn4.addComponent(ckCformRqu);
		flColumn4.addComponent(ckPDCRqu);
		flColumn4.addComponent(hlquoteDoc);
		HorizontalLayout hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flColumn1);
		hlHdr.addComponent(flColumn2);
		hlHdr.addComponent(flColumn3);
		hlHdr.addComponent(flColumn4);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding SmsQuoteDtl components
		// Add components for User Input Layout
		flDtlColumn1 = new FormLayout();
		flDtlColumn2 = new FormLayout();
		flDtlColumn3 = new FormLayout();
		flDtlColumn4 = new FormLayout();
		flDtlColumn5 = new FormLayout();
		flDtlColumn1.addComponent(cbProduct);
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfQuoteQunt);
		hlQtyUom.addComponent(cbUom);
		hlQtyUom.setCaption("Quote Qty");
		flDtlColumn2.addComponent(hlQtyUom);
		flDtlColumn2.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		flDtlColumn3.addComponent(tfUnitRate);
		flDtlColumn3.addComponent(tfBasicValue);
		flDtlColumn4.addComponent(taQuoteRemark);
		flDtlColumn5.addComponent(btnSavepurQuote);
		flDtlColumn5.addComponent(btndelete);
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
		vlSmsQuoteHDR.addComponent(tblPurQuDtl);
		vlSmsQuoteHDR.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlSmsQuoteHDR, "Purchase Quote Detail");
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
		btnSavepurQuote.setStyleName("add");
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<PurchaseQuotHdrDM> listPurQuoteHdr = new ArrayList<PurchaseQuotHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + cbBranch.getValue() + ", " + cbStatus.getValue());
			String eno = null;
			if (cbEnqNo.getValue() != null) {
				eno = (((SmsPurEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
			}
			listPurQuoteHdr = servicePurQuoteHdr.getPurchaseQuotHdrList(null, companyid, (Long) cbBranch.getValue(),
					eno, (String) cbStatus.getValue(), (String) tfvendorName.getValue(),
					(String) tfQuoteRef.getValue(), null, "F");
			recordCnt = listPurQuoteHdr.size();
			beanQuoteHdr = new BeanItemContainer<PurchaseQuotHdrDM>(PurchaseQuotHdrDM.class);
			beanQuoteHdr.addAll(listPurQuoteHdr);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanQuoteHdr);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "quoteId", "branchName", "vendorName", "quoteRef",
					"enquiryNo", "status", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Vendor Name", "Quote Ref",
					"Enquiry No", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("quoteId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadQuoteDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblPurQuDtl.setPageLength(3);
			recordCnt = listQuoteDetails.size();
			beanQuoteDtl = new BeanItemContainer<PurchaseQuotDtlDM>(PurchaseQuotDtlDM.class);
			beanQuoteDtl.addAll(listQuoteDetails);
			BigDecimal sum = new BigDecimal("0");
			for (PurchaseQuotDtlDM obj : listQuoteDetails) {
				if (obj.getBasicValue() != null) {
					sum = sum.add(obj.getBasicValue());
				}
			}
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(sum.toString());
			tfBasictotal.setReadOnly(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxslap. result set");
			tblPurQuDtl.setContainerDataSource(beanQuoteDtl);
			tblPurQuDtl.setVisibleColumns(new Object[] { "productName", "enquiryQty", "unitRate", "basicValue",
					"lastupdateddt", "lastupdatedby" });
			tblPurQuDtl.setColumnHeaders(new String[] { "Product Name", "Quote Qty", "Unit Rate", "Basic Value",
					"Last Updated Date", "Last Updated By" });
			tblPurQuDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadBranchList() {
		try {
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
			cbBranch.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Uom
	private void loadUomList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp
					.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active", "SM_UOM"));
			cbUom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadProductList() {
		try {
			BeanItemContainer<SmsPurEnqDtlDM> beanPlnDtl = new BeanItemContainer<SmsPurEnqDtlDM>(SmsPurEnqDtlDM.class);
			beanPlnDtl.addAll(servicePurEnqDtl.getSmsPurEnqDtlList(null,
					((SmsPurEnqHdrDM) cbEnqNo.getValue()).getEnquiryId(), null, null));
			cbProduct.setContainerDataSource(beanPlnDtl);
			cbProduct.setItemCaptionPropertyId("pordName");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Product List
	private void loadProduct() {
		try {
			BeanItemContainer<ProductDM> beanProduct = new BeanItemContainer<ProductDM>(ProductDM.class);
			beanProduct.addAll(serviceProduct.getProductList(companyid, null, null, null, "Active", null, null, "P"));
			cbProduct.setContainerDataSource(beanProduct);
			cbProduct.setItemCaptionPropertyId("prodname");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadEnquiryNo() {
		try {
			BeanItemContainer<SmsPurEnqHdrDM> beanSmsPurEnqHdrDM = new BeanItemContainer<SmsPurEnqHdrDM>(
					SmsPurEnqHdrDM.class);
			beanSmsPurEnqHdrDM.addAll(servicePurEnqHdr.getSmsPurEnqHdrList(companyid, null, null, null, "Approved",
					"P"));
			cbEnqNo.setContainerDataSource(beanSmsPurEnqHdrDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Vendor List
	private void loadVendorList() {
		try {
			BeanContainer<Long, EnquiryVendorDtlDM> beanVendor = new BeanContainer<Long, EnquiryVendorDtlDM>(
					EnquiryVendorDtlDM.class);
			beanVendor.setBeanIdProperty("vendorid");
			beanVendor.addAll(serviceEnquiryVendorDtl.getpurchasevdrdtl(null,
					((SmsPurEnqHdrDM) cbEnqNo.getValue()).getEnquiryId(), null));
			cbVendorName.setContainerDataSource(beanVendor);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editQuoteHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			PurchaseQuotHdrDM quotHdrDM = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			quoteId = quotHdrDM.getQuoteId();
			cbBranch.setValue(quotHdrDM.getBranchId());
			cbEnqNo.setValue(quotHdrDM.getEnquiryNo());
			tfQuoteRef.setValue(quotHdrDM.getQuoteRef());
			dfQuoteDt.setValue(quotHdrDM.getQuoteDate());
			dfValidDt.setValue(quotHdrDM.getQuotValDate());
			taRemark.setValue(quotHdrDM.getRemarks());
			tfQuoteVersion.setValue(quotHdrDM.getQuoteVersion());
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(quotHdrDM.getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfpackingPer.setValue(quotHdrDM.getPackingPrcnt().toString());
			tfPaclingValue.setReadOnly(false);
			tfPaclingValue.setValue(quotHdrDM.getPackinValue().toString());
			tfPaclingValue.setReadOnly(true);
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(quotHdrDM.getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(quotHdrDM.getVatPrcnt().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(quotHdrDM.getVatValue().toString());
			tfVatValue.setReadOnly(true);
			tfEDPer.setValue(quotHdrDM.getEdPrcnt().toString());
			tfEDValue.setReadOnly(false);
			tfEDValue.setValue(quotHdrDM.getEdValue().toString());
			tfEDValue.setReadOnly(true);
			tfHEDPer.setValue(quotHdrDM.getHeadPrcnt().toString());
			tfHEDValue.setReadOnly(false);
			tfHEDValue.setValue(quotHdrDM.getHedValue().toString());
			tfHEDValue.setReadOnly(true);
			tfCessPer.setValue(quotHdrDM.getCessPrcnt().toString());
			tfCessValue.setReadOnly(false);
			tfCessValue.setValue(quotHdrDM.getCessValue().toString());
			tfCessValue.setReadOnly(true);
			tfCstPer.setValue(quotHdrDM.getCstPrcnt().toString());
			tfCstValue.setReadOnly(false);
			tfCstValue.setValue(quotHdrDM.getCstValue().toString());
			tfCstValue.setReadOnly(true);
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(quotHdrDM.getSubTaxTotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			tfFreightPer.setValue(quotHdrDM.getFreightPrcnt().toString());
			tfFreightValue.setReadOnly(false);
			tfFreightValue.setValue(quotHdrDM.getFreightValue().toString());
			tfFreightValue.setReadOnly(true);
			tfOtherPer.setValue((quotHdrDM.getOtherPrcnt().toString()));
			tfOtherValue.setReadOnly(false);
			tfOtherValue.setValue((quotHdrDM.getOtherValue().toString()));
			tfOtherValue.setReadOnly(true);
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(quotHdrDM.getGrandTotal().toString());
			tfGrandtotal.setReadOnly(true);
			if (quotHdrDM.getPaymentTerms() != null) {
				tfPaymetTerms.setValue(quotHdrDM.getPaymentTerms().toString());
			}
			if (quotHdrDM.getFreightTerms() != null) {
				tfFreightTerms.setValue(quotHdrDM.getFreightTerms());
			}
			if (quotHdrDM.getWarrentyTerms() != null) {
				tfWarrentyTerms.setValue(quotHdrDM.getWarrentyTerms());
			}
			if (quotHdrDM.getDeliveryTerms() != null) {
				tfDelTerms.setValue(quotHdrDM.getDeliveryTerms());
			}
			if (quotHdrDM.getVendorAdd() != null) {
				taVendorAddr.setValue(quotHdrDM.getVendorAdd().toString());
			}
			if (quotHdrDM.getVendorName() != null) {
				tfvendorName.setReadOnly(false);
				tfvendorName.setValue(quotHdrDM.getVendorName());
				tfvendorName.setReadOnly(true);
			}
			if (quotHdrDM.getCityName() != null) {
				tfCityName.setValue(quotHdrDM.getCityName());
			}
			if (quotHdrDM.getStatus() != null) {
				cbStatus.setValue(quotHdrDM.getStatus().toString());
			}
			if (quotHdrDM.getDutyExempted().equals("Y")) {
				ckDutyexm.setValue(true);
			} else {
				ckDutyexm.setValue(false);
			}
			if (quotHdrDM.getCformReqd().equals("Y")) {
				ckCformRqu.setValue(true);
			} else {
				ckCformRqu.setValue(false);
			}
			if (quotHdrDM.getPdcReqd().equals("Y")) {
				ckPDCRqu.setValue(true);
			} else {
				ckPDCRqu.setValue(false);
			}
			cbVendorName.setValue(quotHdrDM.getVendorId());
			Long enqid = quotHdrDM.getEnquiryId();
			Collection<?> enqids = cbEnqNo.getItemIds();
			for (Iterator<?> iterator = enqids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				// Get the actual bean and use the data
				SmsPurEnqHdrDM st = (SmsPurEnqHdrDM) item.getBean();
				if (enqid != null && enqid.equals(st.getEnquiryId())) {
					cbEnqNo.setValue(itemId);
				}
			}
			if (quotHdrDM.getQuoteDoc() != null) {
				byte[] certificate = quotHdrDM.getQuoteDoc();
				UploadDocumentUI test = new UploadDocumentUI(hlquoteDoc);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(hlquoteDoc);
			}
			listQuoteDetails = servicePurchaseQuoteDtl.getPurchaseQuotDtlList(null, quoteId, null);
		}
		loadQuoteDtl();
		comments = new SmsComments(vlTableForm, null, companyid, null, quoteId, null, null, null, null, null, null,
				null, status,null);
		comments.loadsrch(true, null, null, null, null, quoteId, null, null, null, null, null, null, null,null);
		comments.commentList = serviceComment.getSmsCommentsList(null, null, null, quoteId, null, null, null, null,
				null, null, null, null,null);
	}
	
	private void editQuoteDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		if (tblPurQuDtl.getValue() != null) {
			PurchaseQuotDtlDM purchaseQuotDtlDM = beanQuoteDtl.getItem(tblPurQuDtl.getValue()).getBean();
			Long poid = purchaseQuotDtlDM.getProductId();
			Collection<?> prodids = cbProduct.getItemIds();
			for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
				// Get the actual bean and use the data
				SmsPurEnqDtlDM st = (SmsPurEnqDtlDM) item.getBean();
				if (poid != null && poid.equals(st.getProductId())) {
					cbProduct.setValue(itemId);
				}
			}
			if (purchaseQuotDtlDM.getEnquiryQty() != null) {
				tfQuoteQunt.setReadOnly(false);
				tfQuoteQunt.setValue(purchaseQuotDtlDM.getEnquiryQty().toString());
				tfQuoteQunt.setReadOnly(true);
			}
			if (purchaseQuotDtlDM.getUnitRate() != null) {
				tfUnitRate.setValue(purchaseQuotDtlDM.getUnitRate().toString());
			}
			if (purchaseQuotDtlDM.getUom() != null) {
				cbUom.setValue(purchaseQuotDtlDM.getUom().toString());
			}
			if (purchaseQuotDtlDM.getBasicValue() != null) {
				tfBasicValue.setReadOnly(false);
				tfBasicValue.setValue(purchaseQuotDtlDM.getBasicValue().toString());
				tfBasicValue.setReadOnly(true);
			}
			if (purchaseQuotDtlDM.getDtlRemarks() != null) {
				taQuoteRemark.setValue(purchaseQuotDtlDM.getDtlRemarks());
			}
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
		cbEnqNo.setValue(null);
		tfQuoteRef.setValue("");
		cbBranch.setValue(null);
		tfvendorName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		cbProduct.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		cbBranch.setRequired(true);
		tfBasicValue.setRequired(true);
		tfUnitRate.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		tfBasictotal.setReadOnly(true);
		tfSubTotal.setReadOnly(true);
		tfSubTaxTotal.setReadOnly(true);
		tfGrandtotal.setReadOnly(true);
		tfVatValue.setReadOnly(true);
		tfEDValue.setReadOnly(true);
		tfHEDValue.setReadOnly(true);
		tfCessValue.setReadOnly(true);
		tfCstValue.setReadOnly(true);
		tfFreightValue.setReadOnly(true);
		tfOtherValue.setReadOnly(true);
		loadQuoteDtl();
		assembleInputUserLayout();
		new UploadDocumentUI(hlquoteDoc);
		btnSavepurQuote.setCaption("Add");
		tblPurQuDtl.setVisible(true);
		lblNotification.setValue("");
		comments = new SmsComments(vlTableForm, null, companyid, null, quoteId, null, null, null, null, null, null,
				null, null,null);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		cbBranch.setRequired(true);
		tfBasicValue.setRequired(true);
		tfUnitRate.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		cbProduct.setRequired(true);
		lblNotification.setValue("");
		assembleInputUserLayout();
		resetFields();
		editQuoteDtl();
		editQuoteHdr();
		comments.loadsrch(true, null, null, null, quoteId, null, null, null, null, null, null, null, null,null);
		comments.editcommentDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		dfValidDt.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if ((dfQuoteDt.getValue() != null) || (dfValidDt.getValue() != null)) {
			if (dfQuoteDt.getValue().after(dfValidDt.getValue())) {
				dfValidDt.setComponentError(new UserError(GERPErrorCodes.SMS_DATE_OUTOFRANGE));
				logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Throwing ValidationException. User data is > " + dfQuoteDt.getValue());
				errorFlag = true;
			}
		}
		dtlValidation();
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean dtlValidation() {
		boolean isValid = true;
		cbProduct.setComponentError(null);
		tfUnitRate.setComponentError(null);
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			cbProduct.setComponentError(null);
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfUnitRate.getValue());
			if (achievedQty < 0) {
				tfUnitRate.setComponentError(new UserError(GERPErrorCodes.NULL_GREATRETHANZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfUnitRate.setComponentError(new UserError(GERPErrorCodes.UNITRATE_NUMBER_VALIDATION));
			isValid = false;
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() {
		try {
			dtlValidation();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			PurchaseQuotHdrDM purchaseQuotHdrDM = new PurchaseQuotHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				purchaseQuotHdrDM = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			purchaseQuotHdrDM.setBranchId((Long) cbBranch.getValue());
			purchaseQuotHdrDM.setCompanyId(companyid);
			if (cbEnqNo.getValue() != null) {
				purchaseQuotHdrDM.setEnquiryNo(((SmsPurEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
				purchaseQuotHdrDM.setEnquiryId(((SmsPurEnqHdrDM) cbEnqNo.getValue()).getEnquiryId());
			}
			purchaseQuotHdrDM.setVendorId((Long) cbVendorName.getValue());
			purchaseQuotHdrDM.setQuoteDate(dfQuoteDt.getValue());
			purchaseQuotHdrDM.setQuotValDate(dfValidDt.getValue());
			purchaseQuotHdrDM.setRemarks(taRemark.getValue());
			purchaseQuotHdrDM.setQuoteVersion(tfQuoteVersion.getValue());
			purchaseQuotHdrDM.setQuoteRef(tfQuoteRef.getValue());
			if (tfBasictotal.getValue() != null && tfBasictotal.getValue().trim().length() > 0) {
				purchaseQuotHdrDM.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			}
			purchaseQuotHdrDM.setPackingPrcnt((new BigDecimal(tfpackingPer.getValue())));
			if (tfPaclingValue.getValue() != null && tfPaclingValue.getValue().trim().length() > 0) {
				purchaseQuotHdrDM.setPackinValue(new BigDecimal(tfPaclingValue.getValue()));
			}
			purchaseQuotHdrDM.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			purchaseQuotHdrDM.setVatPrcnt(((new BigDecimal(tfVatPer.getValue()))));
			if (tfVatValue.getValue() != null && tfVatValue.getValue().trim().length() > 0) {
				purchaseQuotHdrDM.setVatValue((new BigDecimal(tfVatValue.getValue())));
			}
			if (tfEDPer.getValue() != null && tfEDPer.getValue().trim().length() > 0) {
				purchaseQuotHdrDM.setEdPrcnt((new BigDecimal(tfEDPer.getValue())));
			}
			if (tfEDValue.getValue() != null && tfEDValue.getValue().trim().length() > 0) {
				purchaseQuotHdrDM.setEdValue(new BigDecimal(tfEDValue.getValue()));
			}
			purchaseQuotHdrDM.setHedValue(new BigDecimal(tfHEDValue.getValue()));
			purchaseQuotHdrDM.setHeadPrcnt((new BigDecimal(tfHEDPer.getValue())));
			purchaseQuotHdrDM.setCessPrcnt((new BigDecimal(tfCessPer.getValue())));
			purchaseQuotHdrDM.setCessValue(new BigDecimal(tfCessValue.getValue()));
			purchaseQuotHdrDM.setCstPrcnt((new BigDecimal(tfCstPer.getValue())));
			if (tfCstValue.getValue() != null && tfCstValue.getValue().trim().length() > 0) {
				purchaseQuotHdrDM.setCstValue((new BigDecimal(tfCstValue.getValue())));
			}
			purchaseQuotHdrDM.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			purchaseQuotHdrDM.setFreightPrcnt(new BigDecimal(tfFreightPer.getValue()));
			purchaseQuotHdrDM.setFreightValue(new BigDecimal(tfFreightValue.getValue()));
			purchaseQuotHdrDM.setOtherPrcnt(new BigDecimal(tfOtherPer.getValue()));
			purchaseQuotHdrDM.setOtherValue(new BigDecimal(tfOtherValue.getValue()));
			purchaseQuotHdrDM.setGrandTotal(new BigDecimal(tfGrandtotal.getValue()));
			if (tfPaymetTerms.getValue() != null) {
				purchaseQuotHdrDM.setPaymentTerms((tfPaymetTerms.getValue().toString()));
			}
			if (tfFreightTerms.getValue() != null) {
				purchaseQuotHdrDM.setFreightTerms(tfFreightTerms.getValue().toString());
			}
			if (tfWarrentyTerms.getValue() != null) {
				purchaseQuotHdrDM.setWarrentyTerms((tfWarrentyTerms.getValue().toString()));
			}
			if (tfDelTerms.getValue() != null) {
				purchaseQuotHdrDM.setDeliveryTerms(tfDelTerms.getValue().toString());
			}
			purchaseQuotHdrDM.setVendorAdd(taVendorAddr.getValue());
			tfvendorName.setReadOnly(false);
			purchaseQuotHdrDM.setVendorName(tfvendorName.getValue());
			tfvendorName.setReadOnly(true);
			purchaseQuotHdrDM.setCityName(tfCityName.getValue());
			if (ckDutyexm.getValue().equals(true)) {
				purchaseQuotHdrDM.setDutyExempted("Y");
			} else if (ckDutyexm.getValue().equals(false)) {
				purchaseQuotHdrDM.setDutyExempted("N");
			}
			if (ckCformRqu.getValue().equals(true)) {
				purchaseQuotHdrDM.setCformReqd("Y");
			} else if (ckCformRqu.getValue().equals(false)) {
				purchaseQuotHdrDM.setCformReqd("N");
			}
			if (ckPDCRqu.getValue().equals(true)) {
				purchaseQuotHdrDM.setPdcReqd("Y");
			} else if (ckPDCRqu.getValue().equals(false)) {
				purchaseQuotHdrDM.setPdcReqd("N");
			}
			if (cbStatus.getValue() != null) {
				purchaseQuotHdrDM.setStatus(cbStatus.getValue().toString());
			}
			purchaseQuotHdrDM.setPreparedBy(employeeId);
			purchaseQuotHdrDM.setReviewedBy(null);
			purchaseQuotHdrDM.setActionedBy(null);
			purchaseQuotHdrDM.setLastupdateddt(DateUtils.getcurrentdate());
			purchaseQuotHdrDM.setLastupdatedby(username);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			purchaseQuotHdrDM.setQuoteDoc(fileContents);
			servicePurQuoteHdr.saveorUpdatePurchaseQuotHdrDetails(purchaseQuotHdrDM);
			@SuppressWarnings("unchecked")
			Collection<PurchaseQuotDtlDM> itemIds = (Collection<PurchaseQuotDtlDM>) tblPurQuDtl.getVisibleItemIds();
			for (PurchaseQuotDtlDM save : (Collection<PurchaseQuotDtlDM>) itemIds) {
				save.setQuoteId(Long.valueOf(purchaseQuotHdrDM.getQuoteId().toString()));
				servicePurchaseQuoteDtl.saveorUpdatePurchaseQuotDtlDetails(save);
			}
			comments.saveQuote(purchaseQuotHdrDM.getQuoteId(), purchaseQuotHdrDM.getStatus());
			quoteDtlresetFields();
			loadSrchRslt();
			quoteId = 0L;
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void savePurchaseQuoteDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			int count = 0;
			for (PurchaseQuotDtlDM purchaseQuotDtlDM : listQuoteDetails) {
				if (purchaseQuotDtlDM.getProductId() == ((SmsPurEnqDtlDM) cbProduct.getValue()).getProductId()) {
					count++;
					break;
				}
			}
			if (count == 0) {
				PurchaseQuotDtlDM purchaseQuotDtlobj = new PurchaseQuotDtlDM();
				if (tblPurQuDtl.getValue() != null) {
					purchaseQuotDtlobj = beanQuoteDtl.getItem(tblPurQuDtl.getValue()).getBean();
					listQuoteDetails.remove(purchaseQuotDtlobj);
				}
				purchaseQuotDtlobj.setProductId(((SmsPurEnqDtlDM) cbProduct.getValue()).getProductId());
				purchaseQuotDtlobj.setProductName(((SmsPurEnqDtlDM) cbProduct.getValue()).getPordName());
				if (tfQuoteQunt.getValue() != null && tfQuoteQunt.getValue().trim().length() > 0) {
					tfQuoteQunt.setReadOnly(false);
					purchaseQuotDtlobj.setEnquiryQty(Long.valueOf(tfQuoteQunt.getValue()));
					tfQuoteQunt.setReadOnly(true);
				}
				if (tfUnitRate.getValue() != null && tfUnitRate.getValue().trim().length() > 0) {
					purchaseQuotDtlobj.setUnitRate((Long.valueOf(tfUnitRate.getValue())));
				}
				purchaseQuotDtlobj.setUom(cbUom.getValue().toString());
				if (tfBasicValue.getValue() != null && tfBasicValue.getValue().trim().length() > 0) {
					purchaseQuotDtlobj.setBasicValue(new BigDecimal(tfBasicValue.getValue()));
				}
				purchaseQuotDtlobj.setDtlRemarks(taQuoteRemark.getValue());
				purchaseQuotDtlobj.setLastupdateddt(DateUtils.getcurrentdate());
				purchaseQuotDtlobj.setLastupdatedby(username);
				listQuoteDetails.add(purchaseQuotDtlobj);
				loadQuoteDtl();
				getCalculatedValues();
			} else {
				cbProduct.setComponentError(new UserError("Product Already Exist.."));
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		quoteDtlresetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for TestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_P_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", quoteid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		cbProduct.setRequired(false);
		cbBranch.setRequired(false);
		cbEnqNo.setRequired(false);
		tfQuoteQunt.setRequired(false);
		tfBasicValue.setRequired(false);
		tfUnitRate.setRequired(false);
		resetFields();
		quoteDtlresetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEnqNo.setValue(null);
		tfBasictotal.setReadOnly(false);
		tfBasictotal.setValue("0");
		try {
			tfCessPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "CESS", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCessPer.setValue("0");
		}
		ckCformRqu.setValue(false);
		try {
			tfCstPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "CST", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCstPer.setValue("0");
		}
		tfCessValue.setReadOnly(false);
		tfCessValue.setValue("0");
		tfCstPer.setValue("10");
		tfDelTerms.setValue("");
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue("0");
		ckDutyexm.setValue(false);
		try {
			tfEDPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "ED", "Active", "F").get(0).getTaxprnct()
					.toString());
		}
		catch (Exception e) {
			tfEDPer.setValue("0");
		}
		tfEDValue.setReadOnly(false);
		tfEDValue.setValue("0");
		tfWarrentyTerms.setValue("");
		tfVatValue.setReadOnly(false);
		tfVatValue.setValue("0");
		try {
			tfVatPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "VAT", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfVatPer.setValue("0");
		}
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue("0");
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue("0");
		tfQuoteVersion.setReadOnly(false);
		tfQuoteVersion.setValue("1");
		tfQuoteVersion.setReadOnly(true);
		tfQuoteRef.setValue("");
		ckPDCRqu.setValue(false);
		tfPaymetTerms.setValue("");
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue("0");
		tfpackingPer.setValue("10");
		tfOtherValue.setReadOnly(false);
		tfOtherValue.setValue("0");
		try {
			tfOtherPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "OTHER", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfOtherPer.setValue("0");
		}
		tfHEDValue.setReadOnly(false);
		tfHEDValue.setValue("0");
		try {
			tfHEDPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "HED", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfHEDPer.setValue("0");
		}
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue("0");
		tfFreightValue.setReadOnly(false);
		tfFreightValue.setValue("0");
		try {
			tfFreightPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "FREIGHT", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfFreightPer.setValue("0");
		}
		tfFreightTerms.setValue("");
		cbBranch.setValue(branchId);
		dfQuoteDt.setValue(new Date());
		dfValidDt.setValue(null);
		taRemark.setValue("");
		cbBranch.setComponentError(null);
		cbEnqNo.setComponentError(null);
		dfValidDt.setComponentError(null);
		listQuoteDetails = new ArrayList<PurchaseQuotDtlDM>();
		tblPurQuDtl.removeAllItems();
		new UploadDocumentUI(hlquoteDoc);
		tfvendorName.setReadOnly(false);
		tfvendorName.setValue("");
		taVendorAddr.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfCityName.setValue("");
		loadProduct();
		dfValidDt.setValue(DateUtils.addDays(new Date(), 7));
	}
	
	protected void quoteDtlresetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbProduct.setValue(null);
		cbProduct.setComponentError(null);
		cbUom.setReadOnly(false);
		cbUom.setValue(null);
		cbUom.setReadOnly(true);
		tfBasicValue.setReadOnly(false);
		tfBasicValue.setValue("0");
		tfBasicValue.setReadOnly(true);
		taQuoteRemark.setValue("");
		taQuoteRemark.setComponentError(null);
		tfQuoteQunt.setReadOnly(false);
		tfQuoteQunt.setValue("0");
		tfQuoteQunt.setReadOnly(true);
		tfUnitRate.setValue("0");
		tfUnitRate.setComponentError(null);
		btnSavepurQuote.setCaption("Add");
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
	
	private void deleteDetails() {
		try {
			PurchaseQuotDtlDM save = new PurchaseQuotDtlDM();
			if (tblPurQuDtl.getValue() != null) {
				save = beanQuoteDtl.getItem(tblPurQuDtl.getValue()).getBean();
				listQuoteDetails.remove(save);
				quoteDtlresetFields();
				loadQuoteDtl();
				btndelete.setEnabled(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private BigDecimal gerPercentageValue(BigDecimal percent, BigDecimal value) {
		return (percent.multiply(value).divide(new BigDecimal("100"))).setScale(2, RoundingMode.CEILING);
	}
}