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
	private MmsQuoteHdrService serviceQuoteHdr = (MmsQuoteHdrService) SpringContextHelper.getBean("mmsquotehdr");
	private MmsQuoteDtlService serviceQuoteDtls = (MmsQuoteDtlService) SpringContextHelper.getBean("mmsquotedtl");
	private SmsTaxesService serviceTaxesSms = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private MmsEnqHdrService serviceEnqHdr = (MmsEnqHdrService) SpringContextHelper.getBean("MmsEnqHdr");
	private MmsEnqDtlService serviceMmsEnqDtl = (MmsEnqDtlService) SpringContextHelper.getBean("MmsEnqDtl");
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
	private TextField tfFreightPer, tfFreightValue, tfOtherPer, tfOtherValue, tfGrandtotal, tfUomTemp;
	private TextArea tapaymetTerms, taFreightTerms, taWarrentyTerms, taDelTerms;
	private TextArea taRemark;
	private PopupDateField dfQuoteDt, dfvalidDt;
	private CheckBox ckdutyexm, ckPdcRqu, ckCformRqu;
	private Button btnsavepurQuote = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlquoteDoc = new VerticalLayout();
	// QuoteDtl components
	private ComboBox cbMaterial, cbUom;
	private TextField tfQuoteQunt, tfUnitRate, tfBasicValue, tfReqdQty;
	private TextArea taQuoteRemark;
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private BeanItemContainer<MmsQuoteHdrDM> beanQuoteHdr = null;
	private BeanItemContainer<MmsQuoteDtlDM> beanQuoteDtl = null;
	// private BeanItemContainer<MmsQuoteHdrDM> beanSmsPurEnqHdrDM = null;
	private List<MmsQuoteDtlDM> listQuoteDtls = new ArrayList<MmsQuoteDtlDM>();
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
	private Long employeeId;
	private File file;
	private Long roleId;
	private Long branchId;
	private Long screenId;
	private Long moduleId;
	private Long quoteid;
	private MmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	public static boolean filevalue1 = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(MaterialQuote.class);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private String status;
	private ComboBox cbVendorname;
	
	// Constructor received the parameters from Login UI class
	public MaterialQuote() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
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
		dfQuoteDt.setWidth("130");
		dfvalidDt = new GERPPopupDateField("Valid Date");
		dfvalidDt.setInputPrompt("Select Date");
		dfvalidDt.setWidth("130");
		cbVendorname = new ComboBox("Vendor Name");
		cbVendorname.setItemCaptionPropertyId("vendorName");
		cbVendorname.setWidth("150");
		tfUomTemp = new TextField();
		tfUomTemp.setWidth("50");
		tfUomTemp.setHeight("23");
		tfUomTemp.setReadOnly(true);
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
		tfQuoteVersion.setWidth("145");
		tfBasictotal = new TextField("Basic total");
		tfBasictotal.setWidth("145");
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
		tapaymetTerms = new TextArea("Payment Terms");
		tapaymetTerms.setWidth("150");
		tapaymetTerms.setHeight("30");
		taFreightTerms = new TextArea("Freight Terms");
		taFreightTerms.setWidth("150");
		taFreightTerms.setHeight("30");
		taWarrentyTerms = new TextArea("Warrenty Terms");
		taWarrentyTerms.setWidth("150");
		taWarrentyTerms.setHeight("30");
		taDelTerms = new TextArea("Delivery Terms");
		taDelTerms.setWidth("150");
		taDelTerms.setHeight("30");
		ckdutyexm = new CheckBox("Duty Exempted");
		ckdutyexm.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				loadChkBoxDuty();
			}
		});
		ckCformRqu = new CheckBox("Cfrom Req");
		ckCformRqu.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				loadChkBoxCst();
			}
		});
		ckPdcRqu = new CheckBox("PDC Req");
		cbBranch = new ComboBox("Branch Name");
		cbBranch.setWidth("150");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		try {
			ApprovalSchemaDM obj = serviceQuoteHdr.getReviewerId(companyid, screenId, branchId, roleId).get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_MMS_QUOTE_HDR, BASEConstants.MMS_QUOTE_STATUS);
			} else {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_MMS_QUOTE_HDR, BASEConstants.MMS_QUOTE_STATUS_RV);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		cbStatus.setWidth("120");
		// Purchase QuoteDtl Comp
		cbMaterial = new ComboBox("Material");
		cbMaterial.setItemCaptionPropertyId("materialName");
		cbMaterial.setWidth("120");
		cbMaterial.setImmediate(true);
		cbMaterial.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbMaterial.getValue() != null) {
					tfQuoteQunt.setReadOnly(false);
					tfQuoteQunt.setValue(((MmsEnqDtlDM) cbMaterial.getValue()).getEnquiryQty() + "");
					tfQuoteQunt.setReadOnly(true);
					cbUom.setReadOnly(false);
					tfUomTemp.setReadOnly(false);
					cbUom.setValue(((MmsEnqDtlDM) cbMaterial.getValue()).getMatuom() + "");
					tfUomTemp.setValue(cbUom.getValue().toString());
					cbUom.setReadOnly(true);
					tfUomTemp.setReadOnly(true);
				}
			}
		});
		tfQuoteQunt = new TextField();
		tfQuoteQunt.setWidth("80");
		tfQuoteQunt.setValue("0");
		tfReqdQty = new TextField();
		tfReqdQty.setWidth("80");
		tfReqdQty.setValue("0");
		tfReqdQty.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				calculateBasicvalue();
			}
		});
		cbUom = new ComboBox();
		cbUom.setItemCaptionPropertyId("lookupname");
		cbUom.setWidth("50");
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
			tfBasicValue.setValue((new BigDecimal(tfReqdQty.getValue()))
					.multiply(new BigDecimal(tfUnitRate.getValue())).toString());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		flColumn1.addComponent(cbVendorname);
		flColumn1.addComponent(tfQuoteRef);
		flColumn1.addComponent(dfQuoteDt);
		flColumn1.addComponent(dfvalidDt);
		flColumn1.addComponent(taRemark);
		flColumn1.addComponent(tfQuoteVersion);
		flColumn2.addComponent(tfBasictotal);
		HorizontalLayout pp = new HorizontalLayout();
		pp.addComponent(tfpackingPer);
		pp.addComponent(tfPackingValue);
		pp.setCaption("Packing(%)");
		flColumn2.addComponent(pp);
		flColumn2.setComponentAlignment(pp, Alignment.TOP_LEFT);
		HorizontalLayout ed = new HorizontalLayout();
		ed.addComponent(tfEDPer);
		ed.addComponent(tfEDValue);
		ed.setCaption("ED");
		flColumn2.addComponent(ed);
		flColumn2.setComponentAlignment(ed, Alignment.TOP_LEFT);
		flColumn2.addComponent(tfSubTotal);
		HorizontalLayout vp = new HorizontalLayout();
		vp.addComponent(tfVatPer);
		vp.addComponent(tfVatValue);
		vp.setCaption("VAT");
		flColumn2.addComponent(vp);
		flColumn2.setComponentAlignment(vp, Alignment.TOP_LEFT);
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
		flColumn3.addComponent(frght);
		flColumn3.setComponentAlignment(frght, Alignment.TOP_LEFT);
		HorizontalLayout other = new HorizontalLayout();
		other.addComponent(tfOtherPer);
		other.addComponent(tfOtherValue);
		other.setCaption("Other(%)");
		flColumn3.addComponent(other);
		flColumn3.setComponentAlignment(other, Alignment.TOP_LEFT);
		flColumn3.addComponent(tfGrandtotal);
		flColumn3.addComponent(tapaymetTerms);
		flColumn3.addComponent(taFreightTerms);
		flColumn3.addComponent(taWarrentyTerms);
		flColumn3.addComponent(taDelTerms);
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
		flDtlColumn6 = new FormLayout();
		flDtlColumn7 = new FormLayout();
		flDtlColumn1.addComponent(cbMaterial);
		HorizontalLayout hluom = new HorizontalLayout();
		hluom.addComponent(tfQuoteQunt);
		hluom.addComponent(tfUomTemp);
		hluom.setCaption("Enquiry Qty");
		flDtlColumn2.addComponent(hluom);
		HorizontalLayout hlAvuom = new HorizontalLayout();
		hlAvuom.addComponent(tfReqdQty);
		hlAvuom.addComponent(cbUom);
		hlAvuom.setCaption("Quote Qty");
		flDtlColumn2.addComponent(hlAvuom);
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
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<MmsQuoteHdrDM> list = new ArrayList<MmsQuoteHdrDM>();
			String eno = null;
			if (cbEnqNo.getValue() != null) {
				eno = (((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
			}
			list = serviceQuoteHdr.getMmsQuoteHdrList(companyid, null, null, (Long) cbBranch.getValue(), eno,
					(String) tfQuoteRef.getValue(), (String) cbStatus.getValue(), "F");
			recordCnt = list.size();
			beanQuoteHdr = new BeanItemContainer<MmsQuoteHdrDM>(MmsQuoteHdrDM.class);
			beanQuoteHdr.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanQuoteHdr);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "quoteId", "branchName", "quoteRef", "enquiryNo",
					"status", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Quote Ref", "Enquiry No",
					"Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("quoteId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
			tblMstScrSrchRslt.setPageLength(12);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadMatDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMatQuDtl.removeAllItems();
			tblMatQuDtl.setPageLength(3);
			recordCnt = 0;
			recordCnt = listQuoteDtls.size();
			beanQuoteDtl = new BeanItemContainer<MmsQuoteDtlDM>(MmsQuoteDtlDM.class);
			beanQuoteDtl.addAll(listQuoteDtls);
			BigDecimal sum = new BigDecimal("0");
			for (MmsQuoteDtlDM obj : listQuoteDtls) {
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
			tblMatQuDtl.setVisibleColumns(new Object[] { "materialname", "quoteqty", "reqQty", "unitrate",
					"basicvalue", "lastupdateddt", "lastupdatedby" });
			tblMatQuDtl.setColumnHeaders(new String[] { "Material Name", "Quote Qty", "Required Qty", "UnitRate",
					"Basic Value", "Last Updated Date", "Last Updated By" });
			tblMatQuDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
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
	
	// Load Check Box Duty
	private void loadChkBoxDuty() {
		try {
			if (ckdutyexm.getValue() == true) {
				tfEDValue.setReadOnly(false);
				tfEDPer.setReadOnly(false);
				tfEDValue.setValue("0");
				tfEDPer.setValue("0");
				tfEDValue.setReadOnly(true);
				tfEDPer.setReadOnly(true);
			} else {
				tfEDValue.setReadOnly(false);
				tfEDPer.setReadOnly(false);
				tfEDValue.setValue("0");
				tfEDPer.setValue("0");
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Check Box CST
	private void loadChkBoxCst() {
		try {
			if (ckCformRqu.getValue() == true) {
				tfCstValue.setReadOnly(false);
				tfCstPer.setReadOnly(false);
				tfCstPer.setValue("2.0");
				tfCstValue.setReadOnly(true);
				tfCstPer.setReadOnly(true);
			} else {
				tfCstValue.setReadOnly(false);
				tfCstPer.setReadOnly(false);
				tfCstPer.setValue("0");
				tfCstValue.setValue("0");
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Uom quoteid;
	private void loadUomList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp
					.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active", "MM_UOM"));
			cbUom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadMatNameList() {
		try {
			Long enquid = ((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId();
			BeanItemContainer<MmsEnqDtlDM> beanMaterial = new BeanItemContainer<MmsEnqDtlDM>(MmsEnqDtlDM.class);
			beanMaterial.addAll(serviceMmsEnqDtl.getMmsEnqDtlList(null, enquid, null, null, null));
			cbMaterial.setContainerDataSource(beanMaterial);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadvendorlist() {
		try {
			BeanContainer<Long, MMSVendorDtlDM> beanvndrdtl = new BeanContainer<Long, MMSVendorDtlDM>(
					MMSVendorDtlDM.class);
			beanvndrdtl.setBeanIdProperty("vendorid");
			beanvndrdtl.addAll(servicevendorEnq.getmaterialvdrdtl(null,
					((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId(), null));
			cbVendorname.setContainerDataSource(beanvndrdtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadEnquiryNo() {
		try {
			BeanItemContainer<MmsEnqHdrDM> beanmmsPurEnqHdrDM = new BeanItemContainer<MmsEnqHdrDM>(MmsEnqHdrDM.class);
			beanmmsPurEnqHdrDM.addAll(serviceEnqHdr.getMmsEnqHdrList(companyid, null, null, null, null, "F"));
			cbEnqNo.setContainerDataSource(beanmmsPurEnqHdrDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editQuoteHdr() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlCmdBtnLayout.setVisible(false);
			hlUserInputLayout.setVisible(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected QuoteId -> "
					+ QuoteId);
			if (tblMstScrSrchRslt.getValue() != null) {
				MmsQuoteHdrDM quoteHdr = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
				QuoteId = quoteHdr.getQuoteId();
				cbBranch.setValue(quoteHdr.getBranchId());
				cbEnqNo.setValue(quoteHdr.getEnquiryNo());
				tfQuoteRef.setReadOnly(false);
				tfQuoteRef.setValue(quoteHdr.getQuoteRef());
				tfQuoteRef.setReadOnly(true);
				dfQuoteDt.setValue(quoteHdr.getQuoteDate());
				dfvalidDt.setValue(quoteHdr.getQuoteValDate());
				if (quoteHdr.getRemarks() != null) {
					taRemark.setValue(quoteHdr.getRemarks());
				}
				tfQuoteVersion.setReadOnly(false);
				tfQuoteVersion.setValue(quoteHdr.getQuoteVersion());
				tfQuoteVersion.setReadOnly(true);
				tfBasictotal.setReadOnly(false);
				tfBasictotal.setValue(quoteHdr.getBasicTotal().toString());
				tfBasictotal.setReadOnly(true);
				tfpackingPer.setValue(quoteHdr.getPackingPrcnt().toString());
				tfPackingValue.setReadOnly(false);
				tfPackingValue.setValue(quoteHdr.getPackingValue().toString());
				tfPackingValue.setReadOnly(true);
				tfSubTotal.setReadOnly(false);
				tfSubTotal.setValue(quoteHdr.getSubTotal().toString());
				tfSubTotal.setReadOnly(true);
				tfVatPer.setValue(quoteHdr.getVatPrcnt().toString());
				tfVatValue.setReadOnly(false);
				tfVatValue.setValue(quoteHdr.getVatValue().toString());
				tfVatValue.setReadOnly(true);
				tfEDPer.setValue(quoteHdr.getEdPrcnt().toString());
				tfEDValue.setReadOnly(false);
				tfEDValue.setValue(quoteHdr.getEdValue().toString());
				tfEDValue.setReadOnly(true);
				tfHEDPer.setValue(quoteHdr.getHedPrcnt().toString());
				tfHEDValue.setReadOnly(false);
				tfHEDValue.setValue(quoteHdr.getHedValue().toString());
				tfHEDValue.setReadOnly(true);
				tfCessPer.setValue(quoteHdr.getCessPrcnt().toString());
				tfCessValue.setReadOnly(false);
				tfCessValue.setValue(quoteHdr.getCessValue().toString());
				tfCessValue.setReadOnly(true);
				tfCstPer.setValue(quoteHdr.getCstPrcnt().toString());
				tfCstValue.setReadOnly(false);
				tfCstValue.setValue(quoteHdr.getCstValue().toString());
				tfCstValue.setReadOnly(true);
				tfSubTaxTotal.setReadOnly(false);
				tfSubTaxTotal.setValue(quoteHdr.getSubTaxTotal().toString());
				tfSubTaxTotal.setReadOnly(true);
				tfFreightPer.setValue(quoteHdr.getFreightPrcnt().toString());
				tfFreightValue.setReadOnly(false);
				tfFreightValue.setValue(quoteHdr.getFreightValue().toString());
				tfFreightValue.setReadOnly(true);
				tfOtherPer.setValue((quoteHdr.getOthersPrcnt().toString()));
				tfOtherValue.setReadOnly(false);
				tfOtherValue.setValue((quoteHdr.getOthersValue().toString()));
				tfOtherValue.setReadOnly(true);
				tfGrandtotal.setReadOnly(false);
				tfGrandtotal.setValue(quoteHdr.getGrandTotal().toString());
				tfGrandtotal.setReadOnly(true);
				if (quoteHdr.getPaymentTerms() != null) {
					tapaymetTerms.setValue(quoteHdr.getPaymentTerms().toString());
				}
				if (quoteHdr.getFreightTerms() != null) {
					taFreightTerms.setValue(quoteHdr.getFreightTerms());
				}
				if (quoteHdr.getWarrantyTerms() != null) {
					taWarrentyTerms.setValue(quoteHdr.getWarrantyTerms());
				}
				if (quoteHdr.getDeliveryTerms() != null) {
					taDelTerms.setValue(quoteHdr.getDeliveryTerms());
				}
				if (quoteHdr.getStatus() != null) {
					cbStatus.setValue(quoteHdr.getStatus().toString());
				}
				if (quoteHdr.getDutyExempted().equals("Y")) {
					ckdutyexm.setValue(true);
				} else {
					ckdutyexm.setValue(false);
				}
				if (quoteHdr.getCformReqd().equals("Y")) {
					ckCformRqu.setValue(true);
				} else {
					ckCformRqu.setValue(false);
				}
				if (quoteHdr.getPdcReqd().equals("Y")) {
					ckPdcRqu.setValue(true);
				} else {
					ckPdcRqu.setValue(false);
				}
				if (quoteHdr.getVendorid() != null) {
					cbVendorname.setValue(quoteHdr.getVendorid());
				}
				Long uom = quoteHdr.getEnquiryId();
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
				if (quoteHdr.getQuoteDoc() != null) {
					byte[] certificate = quoteHdr.getQuoteDoc();
					UploadDocumentUI test = new UploadDocumentUI(hlquoteDoc);
					test.displaycertificate(certificate);
				} else {
					new UploadDocumentUI(hlquoteDoc);
				}
				listQuoteDtls = serviceQuoteDtls.getmmsquotedtllist(null, QuoteId, null, null, null);
			}
			loadMatDtl();
			comments = new MmsComments(vlTableForm, null, companyid, null, null, QuoteId, null, null, null, null,
					status);
			comments.loadsrch(true, null, null, null, null, QuoteId, null, null, null, null);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editQuoteDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			if (tblMatQuDtl.getValue() != null) {
				MmsQuoteDtlDM quoteDtl = beanQuoteDtl.getItem(tblMatQuDtl.getValue()).getBean();
				Long uom = quoteDtl.getMaterialid();
				Collection<?> uomid = cbMaterial.getItemIds();
				for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbMaterial.getItem(itemId);
					// Get the actual bean and use the data
					MmsEnqDtlDM st = (MmsEnqDtlDM) item.getBean();
					if (uom != null && uom.equals(st.getMaterialid())) {
						cbMaterial.setValue(itemId);
						break;
					} else {
						cbMaterial.setValue(null);
					}
				}
				if (quoteDtl.getQuoteqty() != null) {
					tfQuoteQunt.setReadOnly(false);
					tfQuoteQunt.setValue(quoteDtl.getQuoteqty().toString());
				}
				if (quoteDtl.getQuoteqty() != null) {
					tfReqdQty.setReadOnly(false);
					tfReqdQty.setValue(quoteDtl.getReqQty().toString());
				}
				if (quoteDtl.getUnitrate() != null) {
					tfUnitRate.setValue(quoteDtl.getUnitrate().toString());
				}
				if (quoteDtl.getMatuom() != null) {
					cbUom.setReadOnly(false);
					cbUom.setValue(quoteDtl.getMatuom().toString());
					cbUom.setReadOnly(true);
				}
				if (quoteDtl.getBasicvalue() != null) {
					tfBasicValue.setReadOnly(false);
					tfBasicValue.setValue(quoteDtl.getBasicvalue().toString());
					tfBasicValue.setReadOnly(true);
				}
				if (quoteDtl.getRemarks() != null) {
					taQuoteRemark.setValue(quoteDtl.getRemarks());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		cbMaterial.setRequired(true);
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
			tfQuoteRef.setReadOnly(true);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfQuoteRef.setReadOnly(true);
			} else {
				tfQuoteRef.setReadOnly(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		cbMaterial.setRequired(true);
		lblNotification.setValue("");
		assembleInputUserLayout();
		resetFields();
		editQuoteDtl();
		editQuoteHdr();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		// try {
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
			cbMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean dtlValidation() {
		boolean isValid = true;
		if (cbMaterial.getValue() == null) {
			cbMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			isValid = false;
		} else {
			cbMaterial.setComponentError(null);
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
			MmsQuoteHdrDM quoteHdr = new MmsQuoteHdrDM();
			quoteHdr.setQuoteRef(tfQuoteRef.getValue());
			quoteHdr.setVendorid((Long) cbVendorname.getValue());
			quoteHdr.setBranchId((Long) cbBranch.getValue());
			quoteHdr.setCompanyId(companyid);
			quoteHdr.setEnquiryId(((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId());
			quoteHdr.setEnquiryNo(((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
			quoteHdr.setQuoteDate(dfQuoteDt.getValue());
			quoteHdr.setQuoteValDate(dfvalidDt.getValue());
			quoteHdr.setRemarks(taRemark.getValue());
			quoteHdr.setQuoteVersion(tfQuoteVersion.getValue());
			if (tfBasictotal.getValue() != null && tfBasictotal.getValue().trim().length() > 0) {
				quoteHdr.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			}
			quoteHdr.setPackingPrcnt((new BigDecimal(tfpackingPer.getValue())));
			if (tfPackingValue.getValue() != null && tfPackingValue.getValue().trim().length() > 0) {
				quoteHdr.setPackingValue(new BigDecimal(tfPackingValue.getValue()));
			}
			quoteHdr.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			quoteHdr.setVatPrcnt(((new BigDecimal(tfVatPer.getValue()))));
			if (tfVatValue.getValue() != null && tfVatValue.getValue().trim().length() > 0) {
				quoteHdr.setVatValue((new BigDecimal(tfVatValue.getValue())));
			}
			if (tfEDPer.getValue() != null && tfEDPer.getValue().trim().length() > 0) {
				quoteHdr.setEdPrcnt((new BigDecimal(tfEDPer.getValue())));
			}
			if (tfEDValue.getValue() != null && tfEDValue.getValue().trim().length() > 0) {
				quoteHdr.setEdValue(new BigDecimal(tfEDValue.getValue()));
			}
			quoteHdr.setHedValue(new BigDecimal(tfHEDValue.getValue()));
			quoteHdr.setHedPrcnt((new BigDecimal(tfHEDPer.getValue())));
			quoteHdr.setCessPrcnt((new BigDecimal(tfCessPer.getValue())));
			quoteHdr.setCessValue(new BigDecimal(tfCessValue.getValue()));
			quoteHdr.setCstPrcnt((new BigDecimal(tfCstPer.getValue())));
			if (tfCstValue.getValue() != null && tfCstValue.getValue().trim().length() > 0) {
				quoteHdr.setCstValue((new BigDecimal(tfCstValue.getValue())));
			}
			quoteHdr.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			quoteHdr.setFreightPrcnt(new BigDecimal(tfFreightPer.getValue()));
			quoteHdr.setFreightValue(new BigDecimal(tfFreightValue.getValue()));
			quoteHdr.setOthersPrcnt(new BigDecimal(tfOtherPer.getValue()));
			quoteHdr.setOthersValue(new BigDecimal(tfOtherValue.getValue()));
			quoteHdr.setGrandTotal(new BigDecimal(tfGrandtotal.getValue()));
			if (tapaymetTerms.getValue() != null) {
				quoteHdr.setPaymentTerms((tapaymetTerms.getValue().toString()));
			}
			if (taFreightTerms.getValue() != null) {
				quoteHdr.setFreightTerms(taFreightTerms.getValue().toString());
			}
			if (taWarrentyTerms.getValue() != null) {
				quoteHdr.setWarrantyTerms((taWarrentyTerms.getValue().toString()));
			}
			if (taDelTerms.getValue() != null) {
				quoteHdr.setDeliveryTerms(taDelTerms.getValue().toString());
			}
			if (ckdutyexm.getValue().equals(true)) {
				quoteHdr.setDutyExempted("Y");
			} else if (ckdutyexm.getValue().equals(false)) {
				quoteHdr.setDutyExempted("N");
			}
			if (ckCformRqu.getValue().equals(true)) {
				quoteHdr.setCformReqd("Y");
			} else if (ckCformRqu.getValue().equals(false)) {
				quoteHdr.setCformReqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				quoteHdr.setPdcReqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				quoteHdr.setPdcReqd("N");
			}
			if (cbStatus.getValue() != null) {
				quoteHdr.setStatus(cbStatus.getValue().toString());
			}
			quoteHdr.setPreparedBy(employeeId);
			quoteHdr.setReviewedBy(null);
			quoteHdr.setActionedBy(null);
			quoteHdr.setLastupdateddt(DateUtils.getcurrentdate());
			quoteHdr.setLastupdatedby(username);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			quoteHdr.setQuoteDoc(fileContents);
			serviceQuoteHdr.saveOrUpdateMmsQuoteHdr(quoteHdr);
			@SuppressWarnings("unchecked")
			Collection<MmsQuoteDtlDM> itemIds = (Collection<MmsQuoteDtlDM>) tblMatQuDtl.getVisibleItemIds();
			for (MmsQuoteDtlDM save : (Collection<MmsQuoteDtlDM>) itemIds) {
				save.setQuoteid(Long.valueOf(quoteHdr.getQuoteId().toString()));
				serviceQuoteDtls.saveOrUpdatemmsquotedtlDetails(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_QN").get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "MM_QN");
					}
				}
				catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
			comments.saveQuote(quoteHdr.getQuoteId(), quoteHdr.getStatus());
			comments.resetfields();
			resetDetailsFields();
			loadSrchRslt();
			quoteid = 0L;
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	protected void saveMaterialQuoteDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			int count = 0;
			for (MmsQuoteDtlDM mmsQuoteDtlDM : listQuoteDtls) {
				if (mmsQuoteDtlDM.getMaterialid() == ((MmsEnqDtlDM) cbMaterial.getValue()).getMaterialid()) {
					count++;
					break;
				}
			}
			if (tblMatQuDtl.getValue() != null) {
				count = 1;
			}
			if (count == 0) {
				MmsQuoteDtlDM quoteDtlDM = new MmsQuoteDtlDM();
				if (tblMatQuDtl.getValue() != null) {
					quoteDtlDM = beanQuoteDtl.getItem(tblMatQuDtl.getValue()).getBean();
					listQuoteDtls.remove(quoteDtlDM);
				}
				quoteDtlDM.setMaterialid(((MmsEnqDtlDM) cbMaterial.getValue()).getMaterialid());
				quoteDtlDM.setMaterialname(((MmsEnqDtlDM) cbMaterial.getValue()).getMaterialName());
				if (tfQuoteQunt.getValue() != null && tfQuoteQunt.getValue().trim().length() > 0) {
					quoteDtlDM.setQuoteqty(Long.valueOf(tfQuoteQunt.getValue()));
				}
				if (tfReqdQty.getValue() != null && tfReqdQty.getValue().trim().length() > 0) {
					quoteDtlDM.setReqQty(Long.valueOf(tfReqdQty.getValue()));
				}
				if (tfUnitRate.getValue() != null && tfUnitRate.getValue().trim().length() > 0) {
					quoteDtlDM.setUnitrate((Long.valueOf(tfUnitRate.getValue())));
				}
				quoteDtlDM.setMatuom(cbUom.getValue().toString());
				if (tfBasicValue.getValue() != null && tfBasicValue.getValue().trim().length() > 0) {
					quoteDtlDM.setBasicvalue(new BigDecimal(tfBasicValue.getValue()));
				}
				quoteDtlDM.setRemarks(taQuoteRemark.getValue());
				quoteDtlDM.setLastupdateddt(DateUtils.getcurrentdate());
				quoteDtlDM.setLastupdatedby(username);
				listQuoteDtls.add(quoteDtlDM);
				loadMatDtl();
				btnsavepurQuote.setCaption("Add");
				getCalculatedValues();
				resetDetailsFields();
			} else {
				cbMaterial.setComponentError(new UserError("Material Already Exist.."));
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		cbMaterial.setRequired(false);
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
		taDelTerms.setValue("");
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
		taWarrentyTerms.setValue("");
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
		tapaymetTerms.setValue("");
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
		taFreightTerms.setValue("");
		cbStatus.setValue(null);
		dfQuoteDt.setValue(new Date());
		dfvalidDt.setValue(null);
		taRemark.setValue("");
		cbBranch.setComponentError(null);
		cbEnqNo.setComponentError(null);
		dfvalidDt.setComponentError(null);
		taDelTerms.setValue("");
		tapaymetTerms.setValue("");
		taWarrentyTerms.setValue("");
		taFreightTerms.setValue("");
		listQuoteDtls = new ArrayList<MmsQuoteDtlDM>();
		tblMatQuDtl.removeAllItems();
		new UploadDocumentUI(hlquoteDoc);
		cbBranch.setValue(branchId);
		cbStatus.setValue(null);
		setPercentZero();
	}
	
	protected void resetDetailsFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMaterial.setValue(null);
		cbMaterial.setComponentError(null);
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
		tfReqdQty.setReadOnly(false);
		tfReqdQty.setValue("0");
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
		BigDecimal edValue = gerPercentageValue(new BigDecimal(tfEDPer.getValue()), subtotal);
		tfEDValue.setReadOnly(false);
		tfEDValue.setValue(edValue.toString());
		tfEDValue.setReadOnly(true);
		subtotal = edValue.add(subtotal);
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue(subtotal.toString());
		tfSubTotal.setReadOnly(true);
		BigDecimal vatvalue = gerPercentageValue(new BigDecimal(tfVatPer.getValue()), subtotal);
		tfVatValue.setReadOnly(false);
		tfVatValue.setValue(vatvalue.toString());
		tfVatValue.setReadOnly(true);
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
		BigDecimal csttotal = vatvalue.add(hedValue).add(cessval).add(cstval);
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
			MmsQuoteDtlDM save = new MmsQuoteDtlDM();
			if (tblMatQuDtl.getValue() != null) {
				save = beanQuoteDtl.getItem(tblMatQuDtl.getValue()).getBean();
				listQuoteDtls.remove(save);
				resetDetailsFields();
				loadMatDtl();
				btndelete.setEnabled(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private BigDecimal gerPercentageValue(BigDecimal percent, BigDecimal value) {
		try {
			return (percent.multiply(value).divide(new BigDecimal("100"))).setScale(2, RoundingMode.CEILING);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
			return new BigDecimal("0");
		}
	}
	
	// to set percentage fields zero
	private void setPercentZero() {
		tfpackingPer.setValue("0");
		tfEDPer.setValue("0");
		tfVatPer.setValue("0");
		tfHEDPer.setValue("0");
		tfCessPer.setValue("0");
		tfCstPer.setValue("0");
		tfFreightPer.setValue("0");
		tfOtherPer.setValue("0");
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
