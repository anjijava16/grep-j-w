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
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
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
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsPurEnqDtlDM;
import com.gnts.sms.domain.txn.SmsPurEnqHdrDM;
import com.gnts.sms.service.mst.SmsTaxesService;
import com.gnts.sms.service.txn.EnquiryVendorDtlService;
import com.gnts.sms.service.txn.PurchaseQuotHdrService;
import com.gnts.sms.service.txn.PurchaseQuoteDtlService;
import com.gnts.sms.service.txn.SmsCommentsService;
import com.gnts.sms.service.txn.SmsPurEnqDtlService;
import com.gnts.sms.service.txn.SmsPurEnqHdrService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
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
	private PurchaseQuotHdrService servicepurchaeQuoteHdr = (PurchaseQuotHdrService) SpringContextHelper
			.getBean("PurchaseQuot");
	private PurchaseQuoteDtlService servicePurchaseQuoteDtl = (PurchaseQuoteDtlService) SpringContextHelper
			.getBean("PurchaseQuotDtl");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private SmsPurEnqHdrService serviceSmsPurEnqHdr = (SmsPurEnqHdrService) SpringContextHelper.getBean("SmsPurEnqHdr");
	private SmsPurEnqDtlService serviceSmsPurEnqDtl = (SmsPurEnqDtlService) SpringContextHelper.getBean("SmsPurEnqDtl");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private SmsTaxesService serviceTaxesSms = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	// form layout for input controls for Quote Header
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5;
	// form layout for input controls for Quote Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4, flDtlColumn5;
	// // User Input Components for Quote Details
	private ComboBox cbBranch, cbStatus, cbEnqNo,cbVendorName;
	private TextField tfQuoteRef, tfQuoteVersion, tfBasictotal, tfpackingPer, tfPaclingValue, tfcityName, tfvendorName;
	private TextField tfSubTotal, tfVatPer, tfVatValue, tfEDPer, tfEDValue, tfHEDPer;
	private TextField tfHEDValue, tfCessPer, tfCessValue, tfCstPer, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightPer, tfFreightValue, tfOtherPer, tfOtherValue, tfGrandtotal;
	private TextField tfpaymetTerms, tfFreightTerms, tfWarrentyTerms, tfDelTerms;
	private TextArea taRemark, tavendorAdd;
	private PopupDateField dfQuoteDt, dfvalidDt;
	private CheckBox ckdutyexm, ckPdcRqu, ckCformRqu;
	private Button btnsavepurQuote = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlquoteDoc = new VerticalLayout();
	// QuoteDtl components
	private ComboBox cbproduct, cbUom;
	private TextField tfQuoteQunt, tfUnitRate, tfBasicValue;
	private TextArea taQuoteRemark;
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private BeanItemContainer<PurchaseQuotHdrDM> beanQuoteHdr = null;
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	private BeanItemContainer<PurchaseQuotDtlDM> beanQuoteDtl = null;
	// private BeanItemContainer<SmsPurEnqHdrDM> beanSmsPurEnqHdrDM = null;
	List<PurchaseQuotDtlDM> QuoteDtllList = new ArrayList<PurchaseQuotDtlDM>();
	// local variables declaration
	private String username;
	private Long companyid;
	private SmsComments comments;
	VerticalLayout vlTableForm = new VerticalLayout();
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private GERPTable tblPurQuDtl;
	private int recordCnt;
	private Long QuoteId;
	private Long EmployeeId;
	private File file;
	private Long roleId;
	private Long branchId;
	private Long screenId;
	private Long moduleId;
	private String quoteid;
	private Long QuotedtlId;
	private Long appScreenId;
	private String strZero = "0.00";
	private Long enquiryId;
	public static boolean filevalue1 = false;
	// Initialize logger
	private static Logger logger = Logger.getLogger(PurchaseQuote.class);
	public Button btndelete = new GERPButton("Delete", "delete", this);
	private String status;
	
	// Constructor received the parameters from Login UI class
	public PurchaseQuote() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		EmployeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
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
		dfvalidDt = new GERPPopupDateField("Valid Date");
		dfvalidDt.setInputPrompt("Select Date");
		dfvalidDt.setWidth("130");
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
		tfpaymetTerms = new TextField("Payment Terms");
		tfpaymetTerms.setWidth("150");
		tfFreightTerms = new TextField("Freight Terms");
		tfFreightTerms.setWidth("150");
		tfWarrentyTerms = new TextField("Warrenty Terms");
		tfWarrentyTerms.setWidth("150");
		tfDelTerms = new TextField("Delivery Terms");
		tfDelTerms.setWidth("150");
		ckdutyexm = new CheckBox("Duty Exempted");
		ckCformRqu = new CheckBox("Cfrom Req");
		ckPdcRqu = new CheckBox("PDC Req");
		cbBranch = new ComboBox("Branch Name");
		cbBranch.setWidth("150");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		List<ApprovalSchemaDM> list = servicepurchaeQuoteHdr.getReviewerId(companyid, screenId, branchId, roleId);
		for (ApprovalSchemaDM obj : list) {
			if (obj.getApprLevel().equals("Reviewer")) {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_QUOTE_HDR, BASEConstants.QUOTE_STATUS_RV);
			} else {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_QUOTE_HDR, BASEConstants.QUOTE_STATUS);
			}
		}
		cbStatus.setWidth("120");
		tfcityName = new TextField("City Name");
		tfcityName.setWidth("150");
		tavendorAdd = new TextArea("Vendor Add");
		tavendorAdd.setWidth("150");
		tavendorAdd.setHeight("50");
		// Purchase QuoteDtl Comp
		cbproduct = new ComboBox("Product Name");
		cbproduct.setItemCaptionPropertyId("pordName");
		cbproduct.setWidth("130");
		cbproduct.setImmediate(true);
		cbproduct.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbproduct.getValue() != null) {
					tfQuoteQunt.setReadOnly(false);
					tfQuoteQunt.setValue(((SmsPurEnqDtlDM) cbproduct.getValue()).getEnquiryQty() + "");
					tfQuoteQunt.setReadOnly(true);
					cbUom.setReadOnly(false);
					cbUom.setValue(((SmsPurEnqDtlDM) cbproduct.getValue()).getProductUom() + "");
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
		btndelete.setEnabled(false);
		tblPurQuDtl = new GERPTable();
		tblPurQuDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblPurQuDtl.isSelected(event.getItemId())) {
					tblPurQuDtl.setImmediate(true);
					btnsavepurQuote.setCaption("Add");
					btnsavepurQuote.setStyleName("savebt");
					btndelete.setEnabled(false);
					QuoteDtlresetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnsavepurQuote.setCaption("Update");
					btnsavepurQuote.setStyleName("savebt");
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
		flColumn1.addComponent(dfvalidDt);
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
		flColumn3.addComponent(tfpaymetTerms);
		flColumn3.addComponent(tfFreightTerms);
		flColumn3.addComponent(tfWarrentyTerms);
		flColumn3.addComponent(tfDelTerms);
		flColumn3.addComponent(tfvendorName);
		flColumn3.addComponent(tavendorAdd);
		flColumn3.addComponent(tfcityName);
		flColumn4.addComponent(cbStatus);
		flColumn4.addComponent(ckdutyexm);
		flColumn4.addComponent(ckCformRqu);
		flColumn4.addComponent(ckPdcRqu);
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
		flDtlColumn1.addComponent(cbproduct);
//		flDtlColumn2.addComponent(tfQuoteQunt);
//		flDtlColumn2.addComponent(cbUom);
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfQuoteQunt);
		hlQtyUom.addComponent(cbUom);
		hlQtyUom.setCaption("Quote Qty");
		flDtlColumn2.addComponent(hlQtyUom);
		flDtlColumn2.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		

		flDtlColumn3.addComponent(tfUnitRate);
		flDtlColumn3.addComponent(tfBasicValue);
		flDtlColumn4.addComponent(taQuoteRemark);
		flDtlColumn5.addComponent(btnsavepurQuote);
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
		btnsavepurQuote.setStyleName("add");
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<PurchaseQuotHdrDM> PurQuoteHdrList = new ArrayList<PurchaseQuotHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbBranch.getValue() + ", " + cbStatus.getValue());
		String eno = null;
		if (cbEnqNo.getValue() != null) {
			eno = (((SmsPurEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
		}
		PurQuoteHdrList = servicepurchaeQuoteHdr.getPurchaseQuotHdrList(null, companyid, (Long) cbBranch.getValue(),
				eno, (String) cbStatus.getValue(), (String) tfvendorName.getValue(), (String) tfQuoteRef.getValue(),
				null, "F");
		recordCnt = PurQuoteHdrList.size();
		beanQuoteHdr = new BeanItemContainer<PurchaseQuotHdrDM>(PurchaseQuotHdrDM.class);
		beanQuoteHdr.addAll(PurQuoteHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanQuoteHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "quoteId", "branchName", "vendorName", "quoteRef",
				"enquiryNo", "status", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Vendor Name", "Quote Ref",
				"Enquiry No", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("quoteId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadQuoteDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblPurQuDtl.setPageLength(3);
			recordCnt = QuoteDtllList.size();
			beanQuoteDtl = new BeanItemContainer<PurchaseQuotDtlDM>(PurchaseQuotDtlDM.class);
			beanQuoteDtl.addAll(QuoteDtllList);
			BigDecimal sum = new BigDecimal("0");
			for (PurchaseQuotDtlDM obj : QuoteDtllList) {
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
			e.printStackTrace();
		}
	}
	
	public void loadBranchList() {
		try {
			List<BranchDM> branchList = serviceBranch.getBranchList(null, null, null, "Active", companyid, "P");
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
	
	// public void loadProduct() {
	// try {
	// List<ProductDM> ProductList = serviceProduct.getProductList(companyid, null, null, null, "Active", null,
	// "P");
	// BeanItemContainer<ProductDM> beanProduct = new BeanItemContainer<ProductDM>(ProductDM.class);
	// beanProduct.addAll(ProductList);
	// cbproduct.setContainerDataSource(beanProduct);
	// }
	// catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	private void loadProductList() {
		List<SmsPurEnqDtlDM> getQuoteDtl = new ArrayList<SmsPurEnqDtlDM>();
		Long enquid = ((SmsPurEnqHdrDM) cbEnqNo.getValue()).getEnquiryId();
		getQuoteDtl.addAll(serviceSmsPurEnqDtl.getSmsPurEnqDtlList(null, enquid, null, null));
		BeanItemContainer<SmsPurEnqDtlDM> beanPlnDtl = new BeanItemContainer<SmsPurEnqDtlDM>(SmsPurEnqDtlDM.class);
		beanPlnDtl.addAll(getQuoteDtl);
		cbproduct.setContainerDataSource(beanPlnDtl);
		cbproduct.setItemCaptionPropertyId("pordName");
	}
	
	// Load Product List
	public void loadProduct() {
		try {
			List<ProductDM> ProductList = serviceProduct.getProductList(companyid, null, null, null, "Active", null,
					null, "P");
			BeanItemContainer<ProductDM> beanProduct = new BeanItemContainer<ProductDM>(ProductDM.class);
			beanProduct.addAll(ProductList);
			cbproduct.setContainerDataSource(beanProduct);
			cbproduct.setItemCaptionPropertyId("prodname");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadEnquiryNo() {
		List<SmsPurEnqHdrDM> getEnNoHdr = new ArrayList<SmsPurEnqHdrDM>();
		getEnNoHdr.addAll(serviceSmsPurEnqHdr.getSmsPurEnqHdrList(companyid, null, null, null, "Approved", "f"));
		BeanItemContainer<SmsPurEnqHdrDM> beanSmsPurEnqHdrDM = new BeanItemContainer<SmsPurEnqHdrDM>(
				SmsPurEnqHdrDM.class);
		beanSmsPurEnqHdrDM.addAll(getEnNoHdr);
		cbEnqNo.setContainerDataSource(beanSmsPurEnqHdrDM);
	}
	// Load Vendor List
	public void loadVendorList() {
		try {
			List<EnquiryVendorDtlDM> vendorList = serviceEnquiryVendorDtl.getpurchasevdrdtl(null, ((SmsPurEnqHdrDM)cbEnqNo.getValue()).getEnquiryId(), null);
			BeanContainer<Long, EnquiryVendorDtlDM> beanVendor = new BeanContainer<Long, EnquiryVendorDtlDM>(EnquiryVendorDtlDM.class);
			beanVendor.setBeanIdProperty("vendorid");
			beanVendor.addAll(vendorList);
			cbVendorName.setContainerDataSource(beanVendor);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void editQuoteHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected QuoteId -> "
				+ QuoteId);
		if (sltedRcd != null) {
			PurchaseQuotHdrDM editPurchaseQuotlist = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			QuoteId = editPurchaseQuotlist.getQuoteId();
			cbBranch.setValue(editPurchaseQuotlist.getBranchId());
			cbEnqNo.setValue(editPurchaseQuotlist.getEnquiryNo());
			tfQuoteRef.setValue(editPurchaseQuotlist.getQuoteRef());
			dfQuoteDt.setValue(editPurchaseQuotlist.getQuoteDate());
			dfvalidDt.setValue(editPurchaseQuotlist.getQuotValDate());
			taRemark.setValue(editPurchaseQuotlist.getRemarks());
			tfQuoteVersion.setValue(editPurchaseQuotlist.getQuoteVersion());
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(editPurchaseQuotlist.getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfpackingPer.setValue(editPurchaseQuotlist.getPackingPrcnt().toString());
			tfPaclingValue.setReadOnly(false);
			tfPaclingValue.setValue(editPurchaseQuotlist.getPackinValue().toString());
			tfPaclingValue.setReadOnly(true);
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(editPurchaseQuotlist.getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(editPurchaseQuotlist.getVatPrcnt().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(editPurchaseQuotlist.getVatValue().toString());
			tfVatValue.setReadOnly(true);
			tfEDPer.setValue(editPurchaseQuotlist.getEdPrcnt().toString());
			tfEDValue.setReadOnly(false);
			tfEDValue.setValue(editPurchaseQuotlist.getEdValue().toString());
			tfEDValue.setReadOnly(true);
			tfHEDPer.setValue(editPurchaseQuotlist.getHeadPrcnt().toString());
			tfHEDValue.setReadOnly(false);
			tfHEDValue.setValue(editPurchaseQuotlist.getHedValue().toString());
			tfHEDValue.setReadOnly(true);
			tfCessPer.setValue(editPurchaseQuotlist.getCessPrcnt().toString());
			tfCessValue.setReadOnly(false);
			tfCessValue.setValue(editPurchaseQuotlist.getCessValue().toString());
			tfCessValue.setReadOnly(true);
			tfCstPer.setValue(editPurchaseQuotlist.getCstPrcnt().toString());
			tfCstValue.setReadOnly(false);
			tfCstValue.setValue(editPurchaseQuotlist.getCstValue().toString());
			tfCstValue.setReadOnly(true);
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(editPurchaseQuotlist.getSubTaxTotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			tfFreightPer.setValue(editPurchaseQuotlist.getFreightPrcnt().toString());
			tfFreightValue.setReadOnly(false);
			tfFreightValue.setValue(editPurchaseQuotlist.getFreightValue().toString());
			tfFreightValue.setReadOnly(true);
			tfOtherPer.setValue((editPurchaseQuotlist.getOtherPrcnt().toString()));
			tfOtherValue.setReadOnly(false);
			tfOtherValue.setValue((editPurchaseQuotlist.getOtherValue().toString()));
			tfOtherValue.setReadOnly(true);
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(editPurchaseQuotlist.getGrandTotal().toString());
			tfGrandtotal.setReadOnly(true);
			if (editPurchaseQuotlist.getPaymentTerms() != null) {
				tfpaymetTerms.setValue(editPurchaseQuotlist.getPaymentTerms().toString());
			}
			if (editPurchaseQuotlist.getFreightTerms() != null) {
				tfFreightTerms.setValue(editPurchaseQuotlist.getFreightTerms());
			}
			if (editPurchaseQuotlist.getWarrentyTerms() != null) {
				tfWarrentyTerms.setValue(editPurchaseQuotlist.getWarrentyTerms());
			}
			if (editPurchaseQuotlist.getDeliveryTerms() != null) {
				tfDelTerms.setValue(editPurchaseQuotlist.getDeliveryTerms());
			}
			if (editPurchaseQuotlist.getVendorAdd() != null) {
				tavendorAdd.setValue(editPurchaseQuotlist.getVendorAdd().toString());
			}
			if (editPurchaseQuotlist.getVendorName() != null) {
				tfvendorName.setReadOnly(false);
				tfvendorName.setValue(editPurchaseQuotlist.getVendorName());
				tfvendorName.setReadOnly(true);
			}
			if (editPurchaseQuotlist.getCityName() != null) {
				tfcityName.setValue(editPurchaseQuotlist.getCityName());
			}
			if (editPurchaseQuotlist.getStatus() != null) {
				cbStatus.setValue(editPurchaseQuotlist.getStatus().toString());
			}
			if (editPurchaseQuotlist.getDutyExempted().equals("Y")) {
				ckdutyexm.setValue(true);
			} else {
				ckdutyexm.setValue(false);
			}
			if (editPurchaseQuotlist.getCformReqd().equals("Y")) {
				ckCformRqu.setValue(true);
			} else {
				ckCformRqu.setValue(false);
			}
			if (editPurchaseQuotlist.getPdcReqd().equals("Y")) {
				ckPdcRqu.setValue(true);
			} else {
				ckPdcRqu.setValue(false);
			}
			
			cbVendorName.setValue(editPurchaseQuotlist.getVendorId());
			
			Long uom = editPurchaseQuotlist.getEnquiryId();
			Collection<?> uomid = cbEnqNo.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				// Get the actual bean and use the data
				SmsPurEnqHdrDM st = (SmsPurEnqHdrDM) item.getBean();
				if (uom != null && uom.equals(st.getEnquiryId())) {
					cbEnqNo.setValue(itemId);
				}
			}
			if (sltedRcd.getItemProperty("quoteDoc").getValue() != null) {
				byte[] certificate = (byte[]) sltedRcd.getItemProperty("quoteDoc").getValue();
				UploadDocumentUI test = new UploadDocumentUI(hlquoteDoc);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(hlquoteDoc);
			}
			QuoteDtllList = servicePurchaseQuoteDtl.getPurchaseQuotDtlList(null, QuoteId, null);
		}
		loadQuoteDtl();
		comments = new SmsComments(vlTableForm, null, companyid, null, QuoteId, null, null, null, null, null, null,
				null, status);
		comments.loadsrch(true, null, null, null, null, QuoteId, null, null, null, null, null, null, null);
		comments.commentList = serviceComment.getSmsCommentsList(null, null, null, QuoteId, null, null, null, null,
				null, null, null, null);
		System.out.println("QuoteId===>" + QuoteId);
	}
	
	private void editQuoteDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		Item sltedRcd = tblPurQuDtl.getItem(tblPurQuDtl.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected QuoteId -> "
				+ QuoteId);
		if (sltedRcd != null) {
			PurchaseQuotDtlDM editPurchaseQuotDtllist = beanQuoteDtl.getItem(tblPurQuDtl.getValue()).getBean();
			QuotedtlId = editPurchaseQuotDtllist.getQuoteDtlId();
			Long uom = editPurchaseQuotDtllist.getProductId();
			Collection<?> uomid = cbproduct.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbproduct.getItem(itemId);
				// Get the actual bean and use the data
				SmsPurEnqDtlDM st = (SmsPurEnqDtlDM) item.getBean();
				if (uom != null && uom.equals(st.getProductId())) {
					cbproduct.setValue(itemId);
				}
			}
			if (editPurchaseQuotDtllist.getEnquiryQty() != null) {
				tfQuoteQunt.setReadOnly(false);
				tfQuoteQunt.setValue(editPurchaseQuotDtllist.getEnquiryQty().toString());
				tfQuoteQunt.setReadOnly(true);
			}
			if (editPurchaseQuotDtllist.getUnitRate() != null) {
				tfUnitRate.setValue(editPurchaseQuotDtllist.getUnitRate().toString());
			}
			if (editPurchaseQuotDtllist.getUom() != null) {
				cbUom.setValue(editPurchaseQuotDtllist.getUom().toString());
			}
			if (editPurchaseQuotDtllist.getBasicValue() != null) {
				tfBasicValue.setReadOnly(false);
				tfBasicValue.setValue(editPurchaseQuotDtllist.getBasicValue().toString());
				tfBasicValue.setReadOnly(true);
			}
			if (editPurchaseQuotDtllist.getDtlRemarks() != null) {
				taQuoteRemark.setValue(editPurchaseQuotDtllist.getDtlRemarks());
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
		cbproduct.setRequired(true);
		//cbUom.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		cbBranch.setRequired(true);
		//cbEnqNo.setRequired(true);
		//tfQuoteQunt.setRequired(true);
		tfBasicValue.setRequired(true);
		//cbUom.setRequired(true);
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
			btnsavepurQuote.setCaption("Add");
			tblPurQuDtl.setVisible(true);
			lblNotification.setValue("");
		comments = new SmsComments(vlTableForm, null, companyid, null, QuoteId, null, null, null, null, null, null,
				null, null);
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
		//cbEnqNo.setRequired(true);
		//tfQuoteQunt.setRequired(true);
		tfBasicValue.setRequired(true);
		//cbUom.setRequired(true);
		tfUnitRate.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		cbproduct.setRequired(true);
		//cbUom.setRequired(true);
		lblNotification.setValue("");
		assembleInputUserLayout();
		resetFields();
		editQuoteDtl();
		editQuoteHdr();
		comments.loadsrch(true, null, null, null, QuoteId, null, null, null, null, null, null, null, null);
		comments.editcommentDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		dfvalidDt.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if ((dfQuoteDt.getValue() != null) || (dfvalidDt.getValue() != null)) {
			if (dfQuoteDt.getValue().after(dfvalidDt.getValue())) {
				dfvalidDt.setComponentError(new UserError(GERPErrorCodes.SMS_DATE_OUTOFRANGE));
				logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Throwing ValidationException. User data is > " + dfQuoteDt.getValue());
				errorFlag = true;
			}
		}
		 DtlValidation();
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean DtlValidation() {
		boolean isValid = true;
		cbproduct.setComponentError(null);
		tfUnitRate.setComponentError(null);
		if (cbproduct.getValue() == null) {
			cbproduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			cbproduct.setComponentError(null);
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
			 DtlValidation();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			PurchaseQuotHdrDM PurchaseQuotHdrobj = new PurchaseQuotHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				PurchaseQuotHdrobj = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
//				if (tfQuoteRef.getValue() != null) {
//					PurchaseQuotHdrobj.setQuoteRef(tfQuoteRef.getValue());
//				}
			} 
//			else {
//				List<SlnoGenDM> slnoList = serviceSlnogen
//						.getSequenceNumber(companyid, branchId, moduleId, "SM_QUOTENO");
//				logger.info("Serial No Generation  Data...===> " + companyid + "," + branchId + "," + moduleId);
//				for (SlnoGenDM slnoObj : slnoList) {
//					if (slnoObj.getAutoGenYN().equals("Y")) {
//						PurchaseQuotHdrobj.setQuoteRef(slnoObj.getKeyDesc());
//					}
//				}
//			}
			PurchaseQuotHdrobj.setBranchId((Long) cbBranch.getValue());
			PurchaseQuotHdrobj.setCompanyId(companyid);
			if (cbEnqNo.getValue() != null) {
				PurchaseQuotHdrobj.setEnquiryNo(((SmsPurEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
				PurchaseQuotHdrobj.setEnquiryId(((SmsPurEnqHdrDM) cbEnqNo.getValue()).getEnquiryId());
			}
			PurchaseQuotHdrobj.setVendorId((Long)cbVendorName.getValue());
			PurchaseQuotHdrobj.setQuoteDate(dfQuoteDt.getValue());
			PurchaseQuotHdrobj.setQuotValDate(dfvalidDt.getValue());
			PurchaseQuotHdrobj.setRemarks(taRemark.getValue());
			PurchaseQuotHdrobj.setQuoteVersion(tfQuoteVersion.getValue());
			PurchaseQuotHdrobj.setQuoteRef(tfQuoteRef.getValue());
			if (tfBasictotal.getValue() != null && tfBasictotal.getValue().trim().length() > 0) {
				PurchaseQuotHdrobj.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			}
			PurchaseQuotHdrobj.setPackingPrcnt((new BigDecimal(tfpackingPer.getValue())));
			if (tfPaclingValue.getValue() != null && tfPaclingValue.getValue().trim().length() > 0) {
				PurchaseQuotHdrobj.setPackinValue(new BigDecimal(tfPaclingValue.getValue()));
			}
			PurchaseQuotHdrobj.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			PurchaseQuotHdrobj.setVatPrcnt(((new BigDecimal(tfVatPer.getValue()))));
			if (tfVatValue.getValue() != null && tfVatValue.getValue().trim().length() > 0) {
				PurchaseQuotHdrobj.setVatValue((new BigDecimal(tfVatValue.getValue())));
			}
			if (tfEDPer.getValue() != null && tfEDPer.getValue().trim().length() > 0) {
				PurchaseQuotHdrobj.setEdPrcnt((new BigDecimal(tfEDPer.getValue())));
			}
			if (tfEDValue.getValue() != null && tfEDValue.getValue().trim().length() > 0) {
				PurchaseQuotHdrobj.setEdValue(new BigDecimal(tfEDValue.getValue()));
			}
			PurchaseQuotHdrobj.setHedValue(new BigDecimal(tfHEDValue.getValue()));
			PurchaseQuotHdrobj.setHeadPrcnt((new BigDecimal(tfHEDPer.getValue())));
			PurchaseQuotHdrobj.setCessPrcnt((new BigDecimal(tfCessPer.getValue())));
			PurchaseQuotHdrobj.setCessValue(new BigDecimal(tfCessValue.getValue()));
			PurchaseQuotHdrobj.setCstPrcnt((new BigDecimal(tfCstPer.getValue())));
			if (tfCstValue.getValue() != null && tfCstValue.getValue().trim().length() > 0) {
				PurchaseQuotHdrobj.setCstValue((new BigDecimal(tfCstValue.getValue())));
			}
			PurchaseQuotHdrobj.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			PurchaseQuotHdrobj.setFreightPrcnt(new BigDecimal(tfFreightPer.getValue()));
			PurchaseQuotHdrobj.setFreightValue(new BigDecimal(tfFreightValue.getValue()));
			PurchaseQuotHdrobj.setOtherPrcnt(new BigDecimal(tfOtherPer.getValue()));
			PurchaseQuotHdrobj.setOtherValue(new BigDecimal(tfOtherValue.getValue()));
			PurchaseQuotHdrobj.setGrandTotal(new BigDecimal(tfGrandtotal.getValue()));
			if (tfpaymetTerms.getValue() != null) {
				PurchaseQuotHdrobj.setPaymentTerms((tfpaymetTerms.getValue().toString()));
			}
			if (tfFreightTerms.getValue() != null) {
				PurchaseQuotHdrobj.setFreightTerms(tfFreightTerms.getValue().toString());
			}
			if (tfWarrentyTerms.getValue() != null) {
				PurchaseQuotHdrobj.setWarrentyTerms((tfWarrentyTerms.getValue().toString()));
			}
			if (tfDelTerms.getValue() != null) {
				PurchaseQuotHdrobj.setDeliveryTerms(tfDelTerms.getValue().toString());
			}
			PurchaseQuotHdrobj.setVendorAdd(tavendorAdd.getValue());
			tfvendorName.setReadOnly(false);
			PurchaseQuotHdrobj.setVendorName(tfvendorName.getValue());
			tfvendorName.setReadOnly(true);
			PurchaseQuotHdrobj.setCityName(tfcityName.getValue());
			if (ckdutyexm.getValue().equals(true)) {
				PurchaseQuotHdrobj.setDutyExempted("Y");
			} else if (ckdutyexm.getValue().equals(false)) {
				PurchaseQuotHdrobj.setDutyExempted("N");
			}
			if (ckCformRqu.getValue().equals(true)) {
				PurchaseQuotHdrobj.setCformReqd("Y");
			} else if (ckCformRqu.getValue().equals(false)) {
				PurchaseQuotHdrobj.setCformReqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				PurchaseQuotHdrobj.setPdcReqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				PurchaseQuotHdrobj.setPdcReqd("N");
			}
			if (cbStatus.getValue() != null) {
				PurchaseQuotHdrobj.setStatus(cbStatus.getValue().toString());
			}
			PurchaseQuotHdrobj.setPreparedBy(EmployeeId);
			PurchaseQuotHdrobj.setReviewedBy(null);
			PurchaseQuotHdrobj.setActionedBy(null);
			PurchaseQuotHdrobj.setLastupdateddt(DateUtils.getcurrentdate());
			PurchaseQuotHdrobj.setLastupdatedby(username);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			PurchaseQuotHdrobj.setQuoteDoc(fileContents);
			servicepurchaeQuoteHdr.saveorUpdatePurchaseQuotHdrDetails(PurchaseQuotHdrobj);
			@SuppressWarnings("unchecked")
			Collection<PurchaseQuotDtlDM> itemIds = (Collection<PurchaseQuotDtlDM>) tblPurQuDtl.getVisibleItemIds();
			for (PurchaseQuotDtlDM save : (Collection<PurchaseQuotDtlDM>) itemIds) {
				save.setQuoteId(Long.valueOf(PurchaseQuotHdrobj.getQuoteId().toString()));
				servicePurchaseQuoteDtl.saveorUpdatePurchaseQuotDtlDetails(save);
			}
			comments.saveQuote(PurchaseQuotHdrobj.getQuoteId(), PurchaseQuotHdrobj.getStatus());
//			if (tblMstScrSrchRslt.getValue() == null) {
//				List<SlnoGenDM> slnoList = serviceSlnogen
//						.getSequenceNumber(companyid, branchId, moduleId, "SM_QUOTENO");
//				for (SlnoGenDM slnoObj : slnoList) {
//					if (slnoObj.getAutoGenYN().equals("Y")) {
//						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_QUOTENO");
//						System.out.println("Serial no=>" + companyid + "," + moduleId + "," + branchId);
//					}
//				}
//			}
			QuoteDtlresetFields();
			loadSrchRslt();
			QuoteId = 0L;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void savePurchaseQuoteDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			int count = 0;
			for (PurchaseQuotDtlDM purchaseQuotDtlDM : QuoteDtllList) {
				if (purchaseQuotDtlDM.getProductId() == ((SmsPurEnqDtlDM) cbproduct.getValue()).getProductId()) {
					count++;
					break;
				}
			}
			System.out.println("count--->" + count);
			if (count == 0) {
				PurchaseQuotDtlDM purchaseQuotDtlobj = new PurchaseQuotDtlDM();
				if (tblPurQuDtl.getValue() != null) {
					purchaseQuotDtlobj = beanQuoteDtl.getItem(tblPurQuDtl.getValue()).getBean();
					QuoteDtllList.remove(purchaseQuotDtlobj);
				}
				purchaseQuotDtlobj.setProductId(((SmsPurEnqDtlDM) cbproduct.getValue()).getProductId());
				purchaseQuotDtlobj.setProductName(((SmsPurEnqDtlDM) cbproduct.getValue()).getPordName());
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
				QuoteDtllList.add(purchaseQuotDtlobj);
				loadQuoteDtl();
				getCalculatedValues();
			} else {
				cbproduct.setComponentError(new UserError("Product Already Exist.."));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		QuoteDtlresetFields();
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
		cbproduct.setRequired(false);
		cbBranch.setRequired(false);
		cbEnqNo.setRequired(false);
		tfQuoteQunt.setRequired(false);
		tfBasicValue.setRequired(false);
		//cbUom.setRequired(false);
		tfUnitRate.setRequired(false);
		// tfvendorName.setReadOnly(false);
		resetFields();
		QuoteDtlresetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEnqNo.setValue(null);
		tfBasictotal.setReadOnly(false);
		tfBasictotal.setValue("0");
		try {
			tfCessPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CESS", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCessPer.setValue("0");
		}
		ckCformRqu.setValue(false);
		try {
			tfCstPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CST", "Active", "F").get(0)
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
		ckdutyexm.setValue(false);
		try {
			tfEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "ED", "Active", "F").get(0).getTaxprnct()
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
			tfVatPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "VAT", "Active", "F").get(0)
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
		ckPdcRqu.setValue(false);
		tfpaymetTerms.setValue("");
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue("0");
		tfpackingPer.setValue("10");
		tfOtherValue.setReadOnly(false);
		tfOtherValue.setValue("0");
		try {
			tfOtherPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "OTHER", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfOtherPer.setValue("0");
		}
		tfHEDValue.setReadOnly(false);
		tfHEDValue.setValue("0");
		try {
			tfHEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "HED", "Active", "F").get(0)
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
			tfFreightPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "FREIGHT", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfFreightPer.setValue("0");
		}
		tfFreightTerms.setValue("");
	
		cbBranch.setValue(branchId);
		dfQuoteDt.setValue(new Date());
		dfvalidDt.setValue(null);
		taRemark.setValue("");
		cbBranch.setComponentError(null);
		cbEnqNo.setComponentError(null);
		dfvalidDt.setComponentError(null);
		QuoteDtllList = new ArrayList<PurchaseQuotDtlDM>();
		tblPurQuDtl.removeAllItems();
		new UploadDocumentUI(hlquoteDoc);
		tfvendorName.setReadOnly(false);
		tfvendorName.setValue("");
		tavendorAdd.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfcityName.setValue("");
		loadProduct();
		dfvalidDt.setValue(DateUtils.addDays(new Date(), 7));
	}
	
	protected void QuoteDtlresetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbproduct.setValue(null);
		cbproduct.setComponentError(null);
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
	
	private void deleteDetails() {
		PurchaseQuotDtlDM save = new PurchaseQuotDtlDM();
		if (tblPurQuDtl.getValue() != null) {
			save = beanQuoteDtl.getItem(tblPurQuDtl.getValue()).getBean();
			QuoteDtllList.remove(save);
			QuoteDtlresetFields();
			loadQuoteDtl();
			btndelete.setEnabled(false);
		}
	}
	
	private BigDecimal gerPercentageValue(BigDecimal percent, BigDecimal value) {
		return (percent.multiply(value).divide(new BigDecimal("100"))).setScale(2, RoundingMode.CEILING);
	}
}