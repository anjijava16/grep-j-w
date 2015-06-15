/**
 * File Name 		: MaterialQuote.java 
 * Description 		: this class is used for add/edit MaterialQuote  details. 
 * Author 			: Arun Jeyaraj R
 * Date 			:  Oct 18, 2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Oct 18, 2014          Arun Jeyaraj R          Initial Version 
 */
package com.gnts.mms.txn;

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
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.txn.MMSVendorDtlDM;
import com.gnts.mms.domain.txn.MmsEnqDtlDM;
import com.gnts.mms.domain.txn.MmsEnqHdrDM;
import com.gnts.mms.domain.txn.MmsQuoteDtlDM;
import com.gnts.mms.domain.txn.MmsQuoteHdrDM;
import com.gnts.mms.service.txn.MMSVendorDtlService;
import com.gnts.mms.service.txn.MmsEnqDtlService;
import com.gnts.mms.service.txn.MmsEnqHdrService;
import com.gnts.mms.service.txn.MmsQuoteDtlService;
import com.gnts.mms.service.txn.MmsQuoteHdrService;
import com.gnts.sms.service.mst.SmsTaxesService;
import com.vaadin.data.Container.Viewer;
import com.vaadin.data.Item;
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

public class MaterialQuote extends BaseTransUI {
	private MmsQuoteHdrService serviceMmsQuoteHdrService = (MmsQuoteHdrService) SpringContextHelper
			.getBean("mmsquotehdr");
	private MmsQuoteDtlService serviceMmsQuoteDtlService = (MmsQuoteDtlService) SpringContextHelper
			.getBean("mmsquotedtl");
	private SmsTaxesService serviceTaxesSms = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private MmsEnqHdrService ServiceMmsEnqHdr = (MmsEnqHdrService) SpringContextHelper.getBean("MmsEnqHdr");
	private MmsEnqDtlService ServiceMmsEnqDtl = (MmsEnqDtlService) SpringContextHelper.getBean("MmsEnqDtl");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private MMSVendorDtlService servicevendorEnq = (MMSVendorDtlService) SpringContextHelper.getBean("mmsVendorDtl");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	// form layout for input controls for Quote Header
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5, flDtlColumn6, flDtlColumn7;
	// form layout for input controls for Quote Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4, flDtlColumn5;
	// // User Input Components for Quote Details
	private ComboBox cbBranch, cbStatus, cbEnqNo;
	private TextField tfQuoteRef, tfQuoteVersion, tfBasictotal, tfpackingPer, tfPackingValue;
	private TextField tfSubTotal, tfVatPer, tfVatValue, tfEDPer, tfEDValue, tfHEDPer;
	private TextField tfHEDValue, tfCessPer, tfCessValue, tfCstPer, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightPer, tfFreightValue, tfOtherPer, tfOtherValue, tfGrandtotal;
	private ComboBox cbpaymetTerms, cbFreightTerms, cbWarrentyTerms, cbDelTerms;
	private TextArea taRemark;
	private PopupDateField dfQuoteDt, dfvalidDt;
	private CheckBox ckdutyexm, ckPdcRqu, ckCformRqu;
	private Button btnsavepurQuote = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlquoteDoc = new VerticalLayout();
	// QuoteDtl components
	private ComboBox cbmaterial, cbUom;
	private TextField tfQuoteQunt, tfUnitRate, tfBasicValue;
	private TextArea taQuoteRemark;
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private BeanItemContainer<MmsQuoteHdrDM> beanQuoteHdr = null;
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	private BeanItemContainer<MmsQuoteDtlDM> beanQuoteDtl = null;
	private BeanItemContainer<MmsEnqDtlDM> beanMaterial = null;
	private BeanContainer<Long, MmsQuoteHdrDM> beanquote = null;
	private BeanContainer<Long, MMSVendorDtlDM> beanvndrdtl = null;
	// private BeanItemContainer<MmsQuoteHdrDM> beanSmsPurEnqHdrDM = null;
	private List<MmsQuoteDtlDM> listQuoteDetails = new ArrayList<MmsQuoteDtlDM>();
	// local variables declaration
	private String username;
	private Long companyid;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private GERPTable tblMatQuDtl;
	private int recordCnt = 0;
	private Long QuoteId;
	private Long EmployeeId;
	private File file;
	private Long roleId;
	private Long branchId;
	private Long screenId;
	private Long moduleId;
	private Long quoteid;
	private MmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	// Initialize logger
	private Logger logger = Logger.getLogger(MaterialQuote.class);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private String status;
	private ComboBox cbvendorname;
	
	// Constructor received the parameters from Login UI class
	public MaterialQuote() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		EmployeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside MaterialQuote() constructor");
		// Loading the MaterialQuote UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax UI");
		// Initialization for MaterialQuote Details user input components
		tfQuoteRef = new TextField("Quote Ref");
		tfQuoteRef.setWidth("150");
		dfQuoteDt = new GERPPopupDateField("Quote Date");
		dfQuoteDt.setInputPrompt("Select Date");
		dfQuoteDt.setWidth("150");
		dfvalidDt = new GERPPopupDateField("Valid Date");
		dfvalidDt.setInputPrompt("Select Date");
		dfvalidDt.setWidth("150");
		cbvendorname = new ComboBox("Vendor Name");
		cbvendorname.setItemCaptionPropertyId("vendorName");
		cbvendorname.setWidth("150");
		/* modified */
		cbEnqNo = new ComboBox("Enquiry No");
		cbEnqNo.setItemCaptionPropertyId("enquiryNo");
		cbEnqNo.setWidth("150");
		cbEnqNo.setImmediate(true);
		loadEnquiryNo();
		cbEnqNo.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				if (item != null) {
					loadvendorlist();
				}
			}
		});
		cbEnqNo.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				if (item != null) {
					loadMatNameList();
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
		tfPackingValue = new TextField();
		tfPackingValue.setWidth("120");
		tfPackingValue.setImmediate(true);
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
		cbpaymetTerms = new ComboBox("Payment Terms");
		cbpaymetTerms.setItemCaptionPropertyId("lookupname");
		cbpaymetTerms.setWidth("150");
		loadPaymentTerms();
		cbFreightTerms = new ComboBox("Freight Terms");
		cbFreightTerms.setItemCaptionPropertyId("lookupname");
		cbFreightTerms.setWidth("150");
		loadFreightTerms();
		cbWarrentyTerms = new ComboBox("Warrenty Terms");
		cbWarrentyTerms.setItemCaptionPropertyId("lookupname");
		cbWarrentyTerms.setWidth("150");
		loadWarentyTerms();
		cbDelTerms = new ComboBox("Delivery Terms");
		cbDelTerms.setItemCaptionPropertyId("lookupname");
		cbDelTerms.setWidth("150");
		loadDeliveryTerms();
		ckdutyexm = new CheckBox("Duty Exempted");
		ckCformRqu = new CheckBox("Cfrom Req");
		ckPdcRqu = new CheckBox("PDC Req");
		cbBranch = new ComboBox("Branch Name");
		cbBranch.setWidth("150");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		try {
			ApprovalSchemaDM obj = serviceMmsQuoteHdrService.getReviewerId(companyid, screenId, branchId, roleId)
					.get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_MMS_QUOTE_HDR, BASEConstants.MMS_QUOTE_STATUS);
			} else {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_MMS_QUOTE_HDR, BASEConstants.MMS_QUOTE_STATUS_RV);
			}
		}
		catch (Exception e) {
		}
		cbStatus.setWidth("120");
		// Purchase QuoteDtl Comp
		cbmaterial = new ComboBox("Material Name");
		cbmaterial.setItemCaptionPropertyId("materialName");
		cbmaterial.setWidth("80");
		cbmaterial.setImmediate(true);
		cbmaterial.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbmaterial.getValue() != null) {
					tfQuoteQunt.setReadOnly(false);
					tfQuoteQunt.setValue(((MmsEnqDtlDM) cbmaterial.getValue()).getEnquiryQty() + "");
					tfQuoteQunt.setReadOnly(true);
					cbUom.setReadOnly(false);
					cbUom.setValue(((MmsEnqDtlDM) cbmaterial.getValue()).getMatuom() + "");
					cbUom.setReadOnly(true);
				}
			}
		});
		tfQuoteQunt = new TextField();
		tfQuoteQunt.setWidth("80");
		tfQuoteQunt.setValue("0");
		tfQuoteQunt.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				calculateBasicvalue();
			}
		});
		cbUom = new ComboBox();
		cbUom.setItemCaptionPropertyId("lookupname");
		cbUom.setWidth("77");
		cbUom.setHeight("23");
		loadUomList();
		tfUnitRate = new TextField("Unit Rate");
		tfUnitRate.setWidth("80");
		tfUnitRate.setValue("0");
		tfUnitRate.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				calculateBasicvalue();
			}
		});
		tfBasicValue = new TextField("Basic value");
		tfBasicValue.setWidth("80");
		tfBasicValue.setValue("0");
		taQuoteRemark = new TextArea("Remarks");
		taQuoteRemark.setWidth("130");
		taQuoteRemark.setHeight("24");
		btnsavepurQuote.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (dtlValidation()) {
					saveMaterialQuoteDetails();
				}
			}
		});
		btndelete.setEnabled(false);
		tblMatQuDtl = new GERPTable();
		tblMatQuDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMatQuDtl.isSelected(event.getItemId())) {
					tblMatQuDtl.setImmediate(true);
					btnsavepurQuote.setCaption("Add");
					btnsavepurQuote.setStyleName("savebt");
					btndelete.setEnabled(false);
					resetDetailsFields();
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
		loadMatDtl();
		btnsavepurQuote.setStyleName("add");
	}
	
	private void calculateBasicvalue() {
		// TODO Auto-generated method stub
		try {
			tfBasicValue.setReadOnly(false);
			tfBasicValue.setValue((new BigDecimal(tfQuoteQunt.getValue())).multiply(
					new BigDecimal(tfUnitRate.getValue())).toString());
			tfBasicValue.setReadOnly(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
		flColumn1.addComponent(cbvendorname);
		flColumn1.addComponent(tfQuoteRef);
		flColumn1.addComponent(dfQuoteDt);
		flColumn1.addComponent(dfvalidDt);
		flColumn1.addComponent(taRemark);
		flColumn1.addComponent(tfQuoteVersion);
		flColumn1.addComponent(tfBasictotal);
		HorizontalLayout pp = new HorizontalLayout();
		pp.addComponent(tfpackingPer);
		pp.addComponent(tfPackingValue);
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
		flColumn3.addComponent(cbpaymetTerms);
		flColumn3.addComponent(cbFreightTerms);
		flColumn3.addComponent(cbWarrentyTerms);
		flColumn3.addComponent(cbDelTerms);
		flColumn3.addComponent(cbStatus);
		flColumn3.addComponent(ckdutyexm);
		flColumn3.addComponent(ckCformRqu);
		flColumn3.addComponent(ckPdcRqu);
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
		flDtlColumn6 = new FormLayout();
		flDtlColumn7 = new FormLayout();
		flDtlColumn1.addComponent(cbmaterial);
		HorizontalLayout hluom = new HorizontalLayout();
		hluom.addComponent(tfQuoteQunt);
		hluom.addComponent(cbUom);
		hluom.setCaption("Quote Qty");
		flDtlColumn2.addComponent(hluom);
		flDtlColumn3.addComponent(tfUnitRate);
		flDtlColumn4.addComponent(tfBasicValue);
		flDtlColumn5.addComponent(taQuoteRemark);
		flDtlColumn6.addComponent(btnsavepurQuote);
		flDtlColumn6.addComponent(btndelete);
		HorizontalLayout hlMmsQuotDtl = new HorizontalLayout();
		hlMmsQuotDtl.addComponent(flDtlColumn1);
		hlMmsQuotDtl.addComponent(flDtlColumn2);
		hlMmsQuotDtl.addComponent(flDtlColumn3);
		hlMmsQuotDtl.addComponent(flDtlColumn4);
		hlMmsQuotDtl.addComponent(flDtlColumn5);
		hlMmsQuotDtl.addComponent(flDtlColumn6);
		hlMmsQuotDtl.addComponent(flDtlColumn7);
		hlMmsQuotDtl.setSpacing(true);
		hlMmsQuotDtl.setMargin(true);
		VerticalLayout vlMmsQuoteHDR = new VerticalLayout();
		vlMmsQuoteHDR = new VerticalLayout();
		vlMmsQuoteHDR.addComponent(hlMmsQuotDtl);
		vlMmsQuoteHDR.addComponent(tblMatQuDtl);
		vlMmsQuoteHDR.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlMmsQuoteHDR, "Material Quote Detail");
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
		List<MmsQuoteHdrDM> MatQuoteHdrList = new ArrayList<MmsQuoteHdrDM>();
		String eno = null;
		if (cbEnqNo.getValue() != null) {
			eno = (((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
		}
		MatQuoteHdrList = serviceMmsQuoteHdrService.getMmsQuoteHdrList(companyid, null, null,
				(Long) cbBranch.getValue(), eno, (String) tfQuoteRef.getValue(), (String) cbStatus.getValue(), "F");
		recordCnt = MatQuoteHdrList.size();
		beanQuoteHdr = new BeanItemContainer<MmsQuoteHdrDM>(MmsQuoteHdrDM.class);
		beanQuoteHdr.addAll(MatQuoteHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanQuoteHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "quoteId", "branchName", "quoteRef", "enquiryNo", "status",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Quote Ref", "Enquiry No", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("quoteId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		tblMstScrSrchRslt.setPageLength(12);
	}
	
	private void loadMatDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMatQuDtl.removeAllItems();
			tblMatQuDtl.setPageLength(3);
			recordCnt = 0;
			recordCnt = listQuoteDetails.size();
			beanQuoteDtl = new BeanItemContainer<MmsQuoteDtlDM>(MmsQuoteDtlDM.class);
			beanQuoteDtl.addAll(listQuoteDetails);
			BigDecimal sum = new BigDecimal("0");
			for (MmsQuoteDtlDM obj : listQuoteDetails) {
				if (obj.getBasicvalue() != null) {
					sum = sum.add(obj.getBasicvalue());
				}
			}
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(sum.toString());
			tfBasictotal.setReadOnly(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxslap. result set");
			tblMatQuDtl.setContainerDataSource(beanQuoteDtl);
			tblMatQuDtl.setVisibleColumns(new Object[] { "materialname", "quoteqty", "unitrate", "basicvalue",
					"lastupdateddt", "lastupdatedby" });
			tblMatQuDtl.setColumnHeaders(new String[] { "Material Name", "Quote Qty", "UnitRate", "Basic Value",
					"Last Updated Date", "Last Updated By" });
			tblMatQuDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
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
	
	// Load Uom quoteid;
	public void loadUomList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"MM_UOM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbUom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadMatNameList() {
		try {
			List<MmsEnqDtlDM> MatnameList = new ArrayList<MmsEnqDtlDM>();
			Long enquid = ((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId();
			MatnameList.addAll(ServiceMmsEnqDtl.getMmsEnqDtlList(null, enquid, null, null, null));
			beanMaterial = new BeanItemContainer<MmsEnqDtlDM>(MmsEnqDtlDM.class);
			beanMaterial.addAll(MatnameList);
			cbmaterial.setContainerDataSource(beanMaterial);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadvendorlist() {
		try {
			List<MMSVendorDtlDM> lookUpList = servicevendorEnq.getmaterialvdrdtl(null,
					((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId(), null);
			beanvndrdtl = new BeanContainer<Long, MMSVendorDtlDM>(MMSVendorDtlDM.class);
			beanvndrdtl.setBeanIdProperty("vendorid");
			beanvndrdtl.addAll(lookUpList);
			cbvendorname.setContainerDataSource(beanvndrdtl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* modified */
	public void loadquotelist() {
		try {
			List<MmsQuoteHdrDM> lookUpList = serviceMmsQuoteHdrService.getMmsQuoteHdrList(null, null, null, branchId,
					null, null, null, null);
			beanquote = new BeanContainer<Long, MmsQuoteHdrDM>(MmsQuoteHdrDM.class);
			beanquote.setBeanIdProperty("quoteId");
			beanquote.addAll(lookUpList);
			((Viewer) tfQuoteRef).setContainerDataSource(beanquote);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadEnquiryNo() {
		List<MmsEnqHdrDM> getEnNoHdr = new ArrayList<MmsEnqHdrDM>();
		getEnNoHdr.addAll(ServiceMmsEnqHdr.getMmsEnqHdrList(companyid, null, null, null, null, "F"));
		BeanItemContainer<MmsEnqHdrDM> beanmmsPurEnqHdrDM = new BeanItemContainer<MmsEnqHdrDM>(MmsEnqHdrDM.class);
		beanmmsPurEnqHdrDM.addAll(getEnNoHdr);
		cbEnqNo.setContainerDataSource(beanmmsPurEnqHdrDM);
	}
	
	public void loadPaymentTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_PAYTRM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbpaymetTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadFreightTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_FRTTRM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbFreightTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadWarentyTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_WRNTRM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbWarrentyTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadDeliveryTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_DELTRM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbDelTerms.setContainerDataSource(beanCompanyLookUp);
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
			MmsQuoteHdrDM editPurchaseQuotlist = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			QuoteId = editPurchaseQuotlist.getQuoteId();
			cbBranch.setValue(editPurchaseQuotlist.getBranchId());
			cbEnqNo.setValue(editPurchaseQuotlist.getEnquiryNo());
			tfQuoteRef.setReadOnly(false);
			tfQuoteRef.setValue(editPurchaseQuotlist.getQuoteRef());
			tfQuoteRef.setReadOnly(true);
			dfQuoteDt.setValue(editPurchaseQuotlist.getQuoteDate());
			dfvalidDt.setValue(editPurchaseQuotlist.getQuoteValDate());
			if (editPurchaseQuotlist.getRemarks() != null) {
				taRemark.setValue(editPurchaseQuotlist.getRemarks());
			}
			tfQuoteVersion.setReadOnly(false);
			tfQuoteVersion.setValue(editPurchaseQuotlist.getQuoteVersion());
			tfQuoteVersion.setReadOnly(true);
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(editPurchaseQuotlist.getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfpackingPer.setValue(editPurchaseQuotlist.getPackingPrcnt().toString());
			tfPackingValue.setReadOnly(false);
			tfPackingValue.setValue(editPurchaseQuotlist.getPackingValue().toString());
			tfPackingValue.setReadOnly(true);
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(editPurchaseQuotlist.getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(editPurchaseQuotlist.getVatPrcnt().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(editPurchaseQuotlist.getVatValue().toString());
			tfVatValue.setReadOnly(true);
			tfEDPer.setValue(editPurchaseQuotlist.getEd_Prcnt().toString());
			tfEDValue.setReadOnly(false);
			tfEDValue.setValue(editPurchaseQuotlist.getEdValue().toString());
			tfEDValue.setReadOnly(true);
			tfHEDPer.setValue(editPurchaseQuotlist.getHedPrcnt().toString());
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
			tfOtherPer.setValue((editPurchaseQuotlist.getOthersPrcnt().toString()));
			tfOtherValue.setReadOnly(false);
			tfOtherValue.setValue((editPurchaseQuotlist.getOthersValue().toString()));
			tfOtherValue.setReadOnly(true);
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(editPurchaseQuotlist.getGrandTotal().toString());
			tfGrandtotal.setReadOnly(true);
			if (editPurchaseQuotlist.getPaymentTerms() != null) {
				cbpaymetTerms.setValue(editPurchaseQuotlist.getPaymentTerms().toString());
			}
			if (editPurchaseQuotlist.getFreightTerms() != null) {
				cbFreightTerms.setValue(editPurchaseQuotlist.getFreightTerms());
			}
			if (editPurchaseQuotlist.getWarrantyTerms() != null) {
				cbWarrentyTerms.setValue(editPurchaseQuotlist.getWarrantyTerms());
			}
			if (editPurchaseQuotlist.getDeliveryTerms() != null) {
				cbDelTerms.setValue(editPurchaseQuotlist.getDeliveryTerms());
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
			if (editPurchaseQuotlist.getVendorid() != null) {
				cbvendorname.setValue(editPurchaseQuotlist.getVendorid());
			}
			Long uom = editPurchaseQuotlist.getEnquiryId();
			Collection<?> uomid = cbEnqNo.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				// Get the actual bean and use the data
				MmsEnqHdrDM st = (MmsEnqHdrDM) item.getBean();
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
			listQuoteDetails = serviceMmsQuoteDtlService.getmmsquotedtllist(null, QuoteId, null, null, null);
		}
		loadMatDtl();
		comments = new MmsComments(vlTableForm, null, companyid, null, null, QuoteId, null, null, null, null, status);
		comments.loadsrch(true, null, null, null, null, QuoteId, null, null, null, null);
	}
	
	private void editQuoteDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		Item sltedRcd = tblMatQuDtl.getItem(tblMatQuDtl.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected QuoteId -> "
				+ QuoteId);
		if (sltedRcd != null) {
			MmsQuoteDtlDM editMaterialQuotDtllist = beanQuoteDtl.getItem(tblMatQuDtl.getValue()).getBean();
			Long uom = editMaterialQuotDtllist.getMaterialid();
			Collection<?> uomid = cbmaterial.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbmaterial.getItem(itemId);
				// Get the actual bean and use the data
				MmsEnqDtlDM st = (MmsEnqDtlDM) item.getBean();
				if (uom != null && uom.equals(st.getMaterialid())) {
					cbmaterial.setValue(itemId);
					break;
				} else {
					cbmaterial.setValue(null);
				}
			}
			if (editMaterialQuotDtllist.getQuoteqty() != null) {
				tfQuoteQunt.setReadOnly(false);
				tfQuoteQunt.setValue(editMaterialQuotDtllist.getQuoteqty().toString());
			}
			if (editMaterialQuotDtllist.getUnitrate() != null) {
				tfUnitRate.setValue(editMaterialQuotDtllist.getUnitrate().toString());
			}
			if (editMaterialQuotDtllist.getMatuom() != null) {
				cbUom.setReadOnly(false);
				cbUom.setValue(editMaterialQuotDtllist.getMatuom().toString());
				cbUom.setReadOnly(true);
			}
			if (editMaterialQuotDtllist.getBasicvalue() != null) {
				tfBasicValue.setReadOnly(false);
				tfBasicValue.setValue(editMaterialQuotDtllist.getBasicvalue().toString());
				tfBasicValue.setReadOnly(true);
			}
			if (editMaterialQuotDtllist.getRemarks() != null) {
				taQuoteRemark.setValue(editMaterialQuotDtllist.getRemarks());
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
		cbBranch.setValue(branchId);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		cbmaterial.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		cbBranch.setRequired(true);
		cbEnqNo.setRequired(true);
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
		loadMatDtl();
		assembleInputUserLayout();
		new UploadDocumentUI(hlquoteDoc);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_QN ").get(0);
			tfQuoteRef.setReadOnly(false);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfQuoteRef.setValue(slnoObj.getKeyDesc());
				tfQuoteRef.setReadOnly(true);
			} else {
				tfQuoteRef.setReadOnly(false);
			}
		}
		catch (Exception e) {
		}
		btnsavepurQuote.setCaption("Add");
		tblMatQuDtl.setVisible(true);
		lblNotification.setValue("");
		comments = new MmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null);
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
		cbEnqNo.setRequired(true);
		tfUnitRate.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		cbmaterial.setRequired(true);
		lblNotification.setValue("");
		assembleInputUserLayout();
		resetFields();
		editQuoteDtl();
		editQuoteHdr();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		cbEnqNo.setComponentError(null);
		dfvalidDt.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if (cbEnqNo.getValue() == null) {
			cbEnqNo.setComponentError(new UserError(GERPErrorCodes.ENQUIRY_NO));
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
		if (tblMatQuDtl.size() == 0) {
			cbmaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean dtlValidation() {
		boolean isValid = true;
		if (cbmaterial.getValue() == null) {
			cbmaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			isValid = false;
		} else {
			cbmaterial.setComponentError(null);
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
			MmsQuoteHdrDM mmsQuoteHdrDM = new MmsQuoteHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				mmsQuoteHdrDM = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			mmsQuoteHdrDM.setQuoteRef(tfQuoteRef.getValue());
			mmsQuoteHdrDM.setVendorid((Long) cbvendorname.getValue());
			mmsQuoteHdrDM.setBranchId((Long) cbBranch.getValue());
			mmsQuoteHdrDM.setCompanyId(companyid);
			mmsQuoteHdrDM.setEnquiryId(((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId());
			mmsQuoteHdrDM.setEnquiryNo(((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
			mmsQuoteHdrDM.setQuoteDate(dfQuoteDt.getValue());
			mmsQuoteHdrDM.setQuoteValDate(dfvalidDt.getValue());
			mmsQuoteHdrDM.setRemarks(taRemark.getValue());
			mmsQuoteHdrDM.setQuoteVersion(tfQuoteVersion.getValue());
			if (tfBasictotal.getValue() != null && tfBasictotal.getValue().trim().length() > 0) {
				mmsQuoteHdrDM.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			}
			mmsQuoteHdrDM.setPackingPrcnt((new BigDecimal(tfpackingPer.getValue())));
			if (tfPackingValue.getValue() != null && tfPackingValue.getValue().trim().length() > 0) {
				mmsQuoteHdrDM.setPackingValue(new BigDecimal(tfPackingValue.getValue()));
			}
			mmsQuoteHdrDM.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			mmsQuoteHdrDM.setVatPrcnt(((new BigDecimal(tfVatPer.getValue()))));
			if (tfVatValue.getValue() != null && tfVatValue.getValue().trim().length() > 0) {
				mmsQuoteHdrDM.setVatValue((new BigDecimal(tfVatValue.getValue())));
			}
			if (tfEDPer.getValue() != null && tfEDPer.getValue().trim().length() > 0) {
				mmsQuoteHdrDM.setEd_Prcnt((new BigDecimal(tfEDPer.getValue())));
			}
			if (tfEDValue.getValue() != null && tfEDValue.getValue().trim().length() > 0) {
				mmsQuoteHdrDM.setEdValue(new BigDecimal(tfEDValue.getValue()));
			}
			mmsQuoteHdrDM.setHedValue(new BigDecimal(tfHEDValue.getValue()));
			mmsQuoteHdrDM.setHedPrcnt((new BigDecimal(tfHEDPer.getValue())));
			mmsQuoteHdrDM.setCessPrcnt((new BigDecimal(tfCessPer.getValue())));
			mmsQuoteHdrDM.setCessValue(new BigDecimal(tfCessValue.getValue()));
			mmsQuoteHdrDM.setCstPrcnt((new BigDecimal(tfCstPer.getValue())));
			if (tfCstValue.getValue() != null && tfCstValue.getValue().trim().length() > 0) {
				mmsQuoteHdrDM.setCstValue((new BigDecimal(tfCstValue.getValue())));
			}
			mmsQuoteHdrDM.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			mmsQuoteHdrDM.setFreightPrcnt(new BigDecimal(tfFreightPer.getValue()));
			mmsQuoteHdrDM.setFreightValue(new BigDecimal(tfFreightValue.getValue()));
			mmsQuoteHdrDM.setOthersPrcnt(new BigDecimal(tfOtherPer.getValue()));
			mmsQuoteHdrDM.setOthersValue(new BigDecimal(tfOtherValue.getValue()));
			mmsQuoteHdrDM.setGrandTotal(new BigDecimal(tfGrandtotal.getValue()));
			if (cbpaymetTerms.getValue() != null) {
				mmsQuoteHdrDM.setPaymentTerms((cbpaymetTerms.getValue().toString()));
			}
			if (cbFreightTerms.getValue() != null) {
				mmsQuoteHdrDM.setFreightTerms(cbFreightTerms.getValue().toString());
			}
			if (cbWarrentyTerms.getValue() != null) {
				mmsQuoteHdrDM.setWarrantyTerms((cbWarrentyTerms.getValue().toString()));
			}
			if (cbDelTerms.getValue() != null) {
				mmsQuoteHdrDM.setDeliveryTerms(cbDelTerms.getValue().toString());
			}
			if (ckdutyexm.getValue().equals(true)) {
				mmsQuoteHdrDM.setDutyExempted("Y");
			} else if (ckdutyexm.getValue().equals(false)) {
				mmsQuoteHdrDM.setDutyExempted("N");
			}
			if (ckCformRqu.getValue().equals(true)) {
				mmsQuoteHdrDM.setCformReqd("Y");
			} else if (ckCformRqu.getValue().equals(false)) {
				mmsQuoteHdrDM.setCformReqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				mmsQuoteHdrDM.setPdcReqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				mmsQuoteHdrDM.setPdcReqd("N");
			}
			if (cbStatus.getValue() != null) {
				mmsQuoteHdrDM.setStatus(cbStatus.getValue().toString());
			}
			mmsQuoteHdrDM.setPreparedBy(EmployeeId);
			mmsQuoteHdrDM.setReviewedBy(null);
			mmsQuoteHdrDM.setActionedBy(null);
			mmsQuoteHdrDM.setLastupdateddt(DateUtils.getcurrentdate());
			mmsQuoteHdrDM.setLastupdatedby(username);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			mmsQuoteHdrDM.setQuoteDoc(fileContents);
			serviceMmsQuoteHdrService.saveOrUpdateMmsQuoteHdr(mmsQuoteHdrDM);
			@SuppressWarnings("unchecked")
			Collection<MmsQuoteDtlDM> itemIds = (Collection<MmsQuoteDtlDM>) tblMatQuDtl.getVisibleItemIds();
			for (MmsQuoteDtlDM save : (Collection<MmsQuoteDtlDM>) itemIds) {
				save.setQuoteid(Long.valueOf(mmsQuoteHdrDM.getQuoteId().toString()));
				serviceMmsQuoteDtlService.saveOrUpdatemmsquotedtlDetails(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_QN");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "MM_QN");
						System.out.println("Serial no=>" + companyid + "," + moduleId + "," + branchId);
					}
				}
			}
			comments.saveQuote(mmsQuoteHdrDM.getQuoteId(), mmsQuoteHdrDM.getStatus());
			comments.resetfields();
			resetDetailsFields();
			loadSrchRslt();
			quoteid = 0L;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void saveMaterialQuoteDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			int count = 0;
			for (MmsQuoteDtlDM mmsQuoteDtlDM : listQuoteDetails) {
				if (mmsQuoteDtlDM.getMaterialid() == ((MmsEnqDtlDM) cbmaterial.getValue()).getMaterialid()) {
					count++;
					break;
				}
			}
			if (tblMatQuDtl.getValue() != null) {
				count = 0;
			}
			if (count == 0) {
				MmsQuoteDtlDM MaterialQuotDtlobj = new MmsQuoteDtlDM();
				if (tblMatQuDtl.getValue() != null) {
					MaterialQuotDtlobj = beanQuoteDtl.getItem(tblMatQuDtl.getValue()).getBean();
					listQuoteDetails.remove(MaterialQuotDtlobj);
				}
				MaterialQuotDtlobj.setMaterialid(((MmsEnqDtlDM) cbmaterial.getValue()).getMaterialid());
				MaterialQuotDtlobj.setMaterialname(((MmsEnqDtlDM) cbmaterial.getValue()).getMaterialName());
				if (tfQuoteQunt.getValue() != null && tfQuoteQunt.getValue().trim().length() > 0) {
					MaterialQuotDtlobj.setQuoteqty(Long.valueOf(tfQuoteQunt.getValue()));
				}
				if (tfUnitRate.getValue() != null && tfUnitRate.getValue().trim().length() > 0) {
					MaterialQuotDtlobj.setUnitrate((Long.valueOf(tfUnitRate.getValue())));
				}
				MaterialQuotDtlobj.setMatuom(cbUom.getValue().toString());
				if (tfBasicValue.getValue() != null && tfBasicValue.getValue().trim().length() > 0) {
					MaterialQuotDtlobj.setBasicvalue(new BigDecimal(tfBasicValue.getValue()));
				}
				MaterialQuotDtlobj.setRemarks(taQuoteRemark.getValue());
				MaterialQuotDtlobj.setLastupdateddt(DateUtils.getcurrentdate());
				MaterialQuotDtlobj.setLastupdatedby(username);
				listQuoteDetails.add(MaterialQuotDtlobj);
				loadMatDtl();
				btnsavepurQuote.setCaption("Add");
				getCalculatedValues();
				resetDetailsFields();
			} else {
				cbmaterial.setComponentError(new UserError("Material Already Exist.."));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
		cbmaterial.setRequired(false);
		cbBranch.setRequired(false);
		cbEnqNo.setRequired(false);
		tfUnitRate.setRequired(false);
		resetFields();
		resetDetailsFields();
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
		cbDelTerms.setValue(null);
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
		cbWarrentyTerms.setValue(null);
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
		tfQuoteRef.setReadOnly(false);
		tfQuoteRef.setValue("");
		ckPdcRqu.setValue(false);
		cbpaymetTerms.setValue(null);
		tfPackingValue.setReadOnly(false);
		tfPackingValue.setValue("0");
		try {
			tfpackingPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "Packing", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfpackingPer.setValue("0");
		}
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
			tfHEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "OTHER", "Active", "F").get(0)
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
		cbFreightTerms.setValue(null);
		cbStatus.setValue(null);
		dfQuoteDt.setValue(new Date());
		dfvalidDt.setValue(DateUtils.addDays(new Date(), 7));
		taRemark.setValue("");
		cbBranch.setComponentError(null);
		cbEnqNo.setComponentError(null);
		dfvalidDt.setComponentError(null);
		cbDelTerms.setValue(null);
		cbpaymetTerms.setValue(null);
		cbWarrentyTerms.setValue(null);
		cbFreightTerms.setValue(null);
		listQuoteDetails = new ArrayList<MmsQuoteDtlDM>();
		tblMatQuDtl.removeAllItems();
		new UploadDocumentUI(hlquoteDoc);
		cbBranch.setValue(branchId);
		cbStatus.setValue(null);
	}
	
	protected void resetDetailsFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbmaterial.setValue(null);
		cbmaterial.setComponentError(null);
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
	}
	
	private void getCalculatedValues() {
		BigDecimal basictotal = new BigDecimal(tfBasictotal.getValue());
		BigDecimal packingvalue = gerPercentageValue(new BigDecimal(tfpackingPer.getValue()), basictotal);
		tfPackingValue.setReadOnly(false);
		tfPackingValue.setValue(packingvalue.toString());
		tfPackingValue.setReadOnly(true);
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
		tfOtherValue.setReadOnly(true);
		BigDecimal Grand = frgval.add(otherval);
		BigDecimal GranTotal = subtaxTotal.add(Grand);
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue(GranTotal.toString());
		tfGrandtotal.setReadOnly(true);
	}
	
	private void deleteDetails() {
		MmsQuoteDtlDM save = new MmsQuoteDtlDM();
		if (tblMatQuDtl.getValue() != null) {
			save = beanQuoteDtl.getItem(tblMatQuDtl.getValue()).getBean();
			listQuoteDetails.remove(save);
			resetDetailsFields();
			loadMatDtl();
			btndelete.setEnabled(false);
		}
	}
	
	private BigDecimal gerPercentageValue(BigDecimal percent, BigDecimal value) {
		return (percent.multiply(value).divide(new BigDecimal("100"))).setScale(2, RoundingMode.CEILING);
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
