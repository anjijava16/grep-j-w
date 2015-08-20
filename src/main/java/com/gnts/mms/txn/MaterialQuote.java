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
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private MmsEnqHdrService serviceMmsEnqHdr = (MmsEnqHdrService) SpringContextHelper.getBean("MmsEnqHdr");
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
	private TextField tfQuoteRef, tfQuoteVersion, tfBasictotal, tfPackingValue;
	private TextField tfSubTotal, tfVatValue, tfEDValue;
	private TextField tfHEDValue, tfCessValue, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightValue, tfOtherValue, tfGrandtotal;
	private TextArea tapaymetTerms, taFreightTerms, taWarrentyTerms, taDelTerms;
	private TextArea taRemark;
	private PopupDateField dfQuoteDt, dfvalidDt;
	private CheckBox ckdutyexm, ckPdcRqu, ckCformRqu;
	private Button btnsavepurQuote = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlquoteDoc = new VerticalLayout();
	// QuoteDtl components
	private ComboBox cbMaterial, cbUom;
	private TextField tfQuoteQunt, tfUnitRate, tfBasicValue;
	private TextArea taQuoteRemark;
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private BeanItemContainer<MmsQuoteHdrDM> beanQuoteHdr = null;
	private BeanItemContainer<MmsQuoteDtlDM> beanQuoteDtl = null;
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
	private Long quoteId;
	private Long employeeId;
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
	private ComboBox cbVendor;
	
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
		cbVendor = new ComboBox("Vendor Name");
		cbVendor.setItemCaptionPropertyId("vendorName");
		cbVendor.setWidth("150");
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
		tfQuoteVersion.setWidth("150");
		tfBasictotal = new TextField("Basic total");
		tfBasictotal.setWidth("150");
		tfSubTotal = new TextField("Sub Total");
		tfSubTotal.setWidth("150");
		tfPackingValue = new TextField();
		tfPackingValue.setWidth("150");
		tfPackingValue.setImmediate(true);
		tfVatValue = new TextField();
		tfVatValue.setWidth("150");
		tfEDValue = new TextField();
		tfEDValue.setWidth("150");
		tfHEDValue = new TextField();
		tfHEDValue.setWidth("150");
		tfCessValue = new TextField();
		tfCessValue.setWidth("150");
		tfSubTaxTotal = new TextField("Sub Tax Total");
		tfCstValue = new TextField();
		tfCstValue.setWidth("150");
		tfSubTaxTotal.setWidth("150");
		tfCstValue.setImmediate(true);
		tfGrandtotal = new TextField("Grand Total");
		tfGrandtotal.setWidth("150");
		tfFreightValue = new TextField();
		tfFreightValue.setWidth("150");
		tfOtherValue = new TextField();
		tfOtherValue.setWidth("150");
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
		tfGrandtotal.setReadOnly(true);
		tfSubTotal.setReadOnly(true);
		tfSubTaxTotal.setReadOnly(true);
		ckdutyexm = new CheckBox("Duty Exempted");
		ckdutyexm.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (ckdutyexm.getValue() == true) {
					tfEDValue.setReadOnly(false);
					tfEDValue.setValue("0");
					tfEDValue.setReadOnly(true);
				} else {
					tfEDValue.setReadOnly(false);
					tfEDValue.setValue("0");
				}
			}
		});
		ckCformRqu = new CheckBox("Cfrom Req");
		ckCformRqu.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (ckCformRqu.getValue() == true) {
					tfCstValue.setReadOnly(false);
					tfCstValue.setValue("2");
					tfCstValue.setReadOnly(true);
				} else {
					tfCstValue.setReadOnly(false);
					tfCstValue.setValue("0");
				}
			}
		});
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
		tfOtherValue.setCaption("Other(%)");
		tfFreightValue.setCaption("Freight");
		tfCstValue.setCaption("CST");
		tfCessValue.setCaption("CESS");
		tfHEDValue.setCaption("HED");
		tfPackingValue.setCaption("Packing");
		tfVatValue.setCaption("VAT");
		tfEDValue.setCaption("ED");
		cbMaterial = new ComboBox("Material Name");
		cbMaterial.setItemCaptionPropertyId("materialName");
		cbMaterial.setWidth("80");
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
					cbUom.setValue(((MmsEnqDtlDM) cbMaterial.getValue()).getMatuom() + "");
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
		flColumn1.addComponent(cbVendor);
		flColumn1.addComponent(tfQuoteRef);
		flColumn1.addComponent(dfQuoteDt);
		flColumn1.addComponent(dfvalidDt);
		flColumn1.addComponent(taRemark);
		flColumn1.addComponent(tfQuoteVersion);
		flColumn2.addComponent(tfBasictotal);
		flColumn2.addComponent(tfPackingValue);
		flColumn2.addComponent(tfSubTotal);
		flColumn2.addComponent(tfVatValue);
		flColumn2.addComponent(tfEDValue);
		flColumn2.addComponent(tfHEDValue);
		flColumn2.addComponent(tfCessValue);
		flColumn2.addComponent(tfCstValue);
		flColumn2.addComponent(tfSubTaxTotal);
		flColumn3.addComponent(tfFreightValue);
		flColumn3.addComponent(tfOtherValue);
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
		List<MmsQuoteHdrDM> listQuoteHdr = new ArrayList<MmsQuoteHdrDM>();
		String eno = null;
		if (cbEnqNo.getValue() != null) {
			eno = (((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
		}
		listQuoteHdr = serviceMmsQuoteHdrService.getMmsQuoteHdrList(companyid, null, null, (Long) cbBranch.getValue(),
				eno, (String) tfQuoteRef.getValue(), (String) cbStatus.getValue(), "F");
		recordCnt = listQuoteHdr.size();
		beanQuoteHdr = new BeanItemContainer<MmsQuoteHdrDM>(MmsQuoteHdrDM.class);
		beanQuoteHdr.addAll(listQuoteHdr);
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
	
	private void loadBranchList() {
		try {
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
			cbBranch.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}
	
	private void loadvendorlist() {
		try {
			BeanContainer<Long, MMSVendorDtlDM> beanvndrdtl = new BeanContainer<Long, MMSVendorDtlDM>(
					MMSVendorDtlDM.class);
			beanvndrdtl.setBeanIdProperty("vendorid");
			beanvndrdtl.addAll(servicevendorEnq.getmaterialvdrdtl(null,
					((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId(), null));
			cbVendor.setContainerDataSource(beanvndrdtl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadEnquiryNo() {
		BeanItemContainer<MmsEnqHdrDM> beanmmsPurEnqHdrDM = new BeanItemContainer<MmsEnqHdrDM>(MmsEnqHdrDM.class);
		beanmmsPurEnqHdrDM.addAll(serviceMmsEnqHdr.getMmsEnqHdrList(companyid, null, null, null, null, "F"));
		cbEnqNo.setContainerDataSource(beanmmsPurEnqHdrDM);
	}
	
	private void editQuoteHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			MmsQuoteHdrDM quoteHdrDM = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			quoteId = quoteHdrDM.getQuoteId();
			cbBranch.setValue(quoteHdrDM.getBranchId());
			cbEnqNo.setValue(quoteHdrDM.getEnquiryNo());
			tfQuoteRef.setReadOnly(false);
			tfQuoteRef.setValue(quoteHdrDM.getQuoteRef());
			tfQuoteRef.setReadOnly(true);
			dfQuoteDt.setValue(quoteHdrDM.getQuoteDate());
			dfvalidDt.setValue(quoteHdrDM.getQuoteValDate());
			if (quoteHdrDM.getRemarks() != null) {
				taRemark.setValue(quoteHdrDM.getRemarks());
			}
			tfQuoteVersion.setReadOnly(false);
			tfQuoteVersion.setValue(quoteHdrDM.getQuoteVersion());
			tfQuoteVersion.setReadOnly(true);
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(quoteHdrDM.getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfPackingValue.setValue(quoteHdrDM.getPackingValue().toString());
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(quoteHdrDM.getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatValue.setValue(quoteHdrDM.getVatValue().toString());
			tfEDValue.setValue(quoteHdrDM.getEdValue().toString());
			tfHEDValue.setValue(quoteHdrDM.getHedValue().toString());
			tfCessValue.setValue(quoteHdrDM.getCessValue().toString());
			tfCstValue.setValue(quoteHdrDM.getCstValue().toString());
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(quoteHdrDM.getSubTaxTotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			tfFreightValue.setValue(quoteHdrDM.getFreightValue().toString());
			tfOtherValue.setValue((quoteHdrDM.getOthersValue().toString()));
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(quoteHdrDM.getGrandTotal().toString());
			tfGrandtotal.setReadOnly(true);
			if (quoteHdrDM.getPaymentTerms() != null) {
				tapaymetTerms.setValue(quoteHdrDM.getPaymentTerms().toString());
			}
			if (quoteHdrDM.getFreightTerms() != null) {
				taFreightTerms.setValue(quoteHdrDM.getFreightTerms());
			}
			if (quoteHdrDM.getWarrantyTerms() != null) {
				taWarrentyTerms.setValue(quoteHdrDM.getWarrantyTerms());
			}
			if (quoteHdrDM.getDeliveryTerms() != null) {
				taDelTerms.setValue(quoteHdrDM.getDeliveryTerms());
			}
			if (quoteHdrDM.getStatus() != null) {
				cbStatus.setValue(quoteHdrDM.getStatus().toString());
			}
			if (quoteHdrDM.getDutyExempted().equals("Y")) {
				ckdutyexm.setValue(true);
			} else {
				ckdutyexm.setValue(false);
			}
			if (quoteHdrDM.getCformReqd().equals("Y")) {
				ckCformRqu.setValue(true);
			} else {
				ckCformRqu.setValue(false);
			}
			if (quoteHdrDM.getPdcReqd().equals("Y")) {
				ckPdcRqu.setValue(true);
			} else {
				ckPdcRqu.setValue(false);
			}
			if (quoteHdrDM.getVendorid() != null) {
				cbVendor.setValue(quoteHdrDM.getVendorid());
			}
			Long uom = quoteHdrDM.getEnquiryId();
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
			if (quoteHdrDM.getQuoteDoc() != null) {
				byte[] certificate = quoteHdrDM.getQuoteDoc();
				UploadDocumentUI test = new UploadDocumentUI(hlquoteDoc);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(hlquoteDoc);
			}
			listQuoteDetails = serviceMmsQuoteDtlService.getmmsquotedtllist(null, quoteId, null, null, null);
		}
		loadMatDtl();
		comments = new MmsComments(vlTableForm, null, companyid, null, null, quoteId, null, null, null, null, status);
		comments.loadsrch(true, null, null, null, null, quoteId, null, null, null, null);
	}
	
	private void editQuoteDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		if (tblMatQuDtl.getValue() != null) {
			MmsQuoteDtlDM quoteDtlDM = beanQuoteDtl.getItem(tblMatQuDtl.getValue()).getBean();
			Long matid = quoteDtlDM.getMaterialid();
			Collection<?> matids = cbMaterial.getItemIds();
			for (Iterator<?> iterator = matids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbMaterial.getItem(itemId);
				// Get the actual bean and use the data
				MmsEnqDtlDM st = (MmsEnqDtlDM) item.getBean();
				if (matid != null && matid.equals(st.getMaterialid())) {
					cbMaterial.setValue(itemId);
					break;
				} else {
					cbMaterial.setValue(null);
				}
			}
			if (quoteDtlDM.getQuoteqty() != null) {
				tfQuoteQunt.setReadOnly(false);
				tfQuoteQunt.setValue(quoteDtlDM.getQuoteqty().toString());
			}
			if (quoteDtlDM.getUnitrate() != null) {
				tfUnitRate.setValue(quoteDtlDM.getUnitrate().toString());
			}
			if (quoteDtlDM.getMatuom() != null) {
				cbUom.setReadOnly(false);
				cbUom.setValue(quoteDtlDM.getMatuom().toString());
				cbUom.setReadOnly(true);
			}
			if (quoteDtlDM.getBasicvalue() != null) {
				tfBasicValue.setReadOnly(false);
				tfBasicValue.setValue(quoteDtlDM.getBasicvalue().toString());
				tfBasicValue.setReadOnly(true);
			}
			if (quoteDtlDM.getRemarks() != null) {
				taQuoteRemark.setValue(quoteDtlDM.getRemarks());
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
		loadMatDtl();
		assembleInputUserLayout();
		new UploadDocumentUI(hlquoteDoc);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_QN ").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
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
		Boolean errorFlag = false;
		try {
			cbBranch.setComponentError(null);
			cbEnqNo.setComponentError(null);
			dfvalidDt.setComponentError(null);
			if ((cbBranch.getValue() == null)) {
				cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
				errorFlag = true;
			}
			if (cbEnqNo.getValue() == null) {
				cbEnqNo.setComponentError(new UserError(GERPErrorCodes.ENQUIRY_NO));
				errorFlag = true;
			}
			if ((dfQuoteDt.getValue() != null) || (dfvalidDt.getValue() != null)) {
				try {
					if (dfQuoteDt.getValue().after(dfvalidDt.getValue())) {
						dfvalidDt.setComponentError(new UserError(GERPErrorCodes.SMS_DATE_OUTOFRANGE));
						logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
								+ "Throwing ValidationException. User data is > " + dfQuoteDt.getValue());
						errorFlag = true;
					}
				}
				catch (Exception e) {
				}
			}
			if (tblMatQuDtl.size() == 0) {
				cbMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
				errorFlag = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
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
			MmsQuoteHdrDM quoteHdrDM = new MmsQuoteHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				quoteHdrDM = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
				if (tfQuoteRef.getValue() != null) {
					quoteHdrDM.setQuoteRef(tfQuoteRef.getValue());
				}
			} else {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_QN").get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						quoteHdrDM.setQuoteRef(slnoObj.getKeyDesc());
					}
				}
				catch (Exception e) {
				}
			}
			quoteHdrDM.setVendorid((Long) cbVendor.getValue());
			quoteHdrDM.setBranchId((Long) cbBranch.getValue());
			quoteHdrDM.setCompanyId(companyid);
			quoteHdrDM.setEnquiryId(((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId());
			quoteHdrDM.setEnquiryNo(((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
			quoteHdrDM.setQuoteDate(dfQuoteDt.getValue());
			quoteHdrDM.setQuoteValDate(dfvalidDt.getValue());
			quoteHdrDM.setRemarks(taRemark.getValue());
			quoteHdrDM.setQuoteVersion(tfQuoteVersion.getValue());
			if (tfBasictotal.getValue() != null && tfBasictotal.getValue().trim().length() > 0) {
				quoteHdrDM.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			}
			if (tfPackingValue.getValue() != null && tfPackingValue.getValue().trim().length() > 0) {
				quoteHdrDM.setPackingValue(new BigDecimal(tfPackingValue.getValue()));
			}
			quoteHdrDM.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			if (tfVatValue.getValue() != null && tfVatValue.getValue().trim().length() > 0) {
				quoteHdrDM.setVatValue((new BigDecimal(tfVatValue.getValue())));
			}
			if (tfEDValue.getValue() != null && tfEDValue.getValue().trim().length() > 0) {
				quoteHdrDM.setEdValue(new BigDecimal(tfEDValue.getValue()));
			}
			quoteHdrDM.setHedValue(new BigDecimal(tfHEDValue.getValue()));
			quoteHdrDM.setCessValue(new BigDecimal(tfCessValue.getValue()));
			if (tfCstValue.getValue() != null && tfCstValue.getValue().trim().length() > 0) {
				quoteHdrDM.setCstValue((new BigDecimal(tfCstValue.getValue())));
			}
			quoteHdrDM.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			quoteHdrDM.setFreightValue(new BigDecimal(tfFreightValue.getValue()));
			quoteHdrDM.setOthersValue(new BigDecimal(tfOtherValue.getValue()));
			quoteHdrDM.setGrandTotal(new BigDecimal(tfGrandtotal.getValue()));
			if (tapaymetTerms.getValue() != null) {
				quoteHdrDM.setPaymentTerms((tapaymetTerms.getValue().toString()));
			}
			if (taFreightTerms.getValue() != null) {
				quoteHdrDM.setFreightTerms(taFreightTerms.getValue().toString());
			}
			if (taWarrentyTerms.getValue() != null) {
				quoteHdrDM.setWarrantyTerms((taWarrentyTerms.getValue().toString()));
			}
			if (taDelTerms.getValue() != null) {
				quoteHdrDM.setDeliveryTerms(taDelTerms.getValue().toString());
			}
			if (ckdutyexm.getValue().equals(true)) {
				quoteHdrDM.setDutyExempted("Y");
			} else if (ckdutyexm.getValue().equals(false)) {
				quoteHdrDM.setDutyExempted("N");
			}
			if (ckCformRqu.getValue().equals(true)) {
				quoteHdrDM.setCformReqd("Y");
			} else if (ckCformRqu.getValue().equals(false)) {
				quoteHdrDM.setCformReqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				quoteHdrDM.setPdcReqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				quoteHdrDM.setPdcReqd("N");
			}
			if (cbStatus.getValue() != null) {
				quoteHdrDM.setStatus(cbStatus.getValue().toString());
			}
			quoteHdrDM.setPreparedBy(employeeId);
			quoteHdrDM.setReviewedBy(null);
			quoteHdrDM.setActionedBy(null);
			quoteHdrDM.setLastupdateddt(DateUtils.getcurrentdate());
			quoteHdrDM.setLastupdatedby(username);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			quoteHdrDM.setQuoteDoc(fileContents);
			serviceMmsQuoteHdrService.saveOrUpdateMmsQuoteHdr(quoteHdrDM);
			@SuppressWarnings("unchecked")
			Collection<MmsQuoteDtlDM> itemIds = (Collection<MmsQuoteDtlDM>) tblMatQuDtl.getVisibleItemIds();
			for (MmsQuoteDtlDM save : (Collection<MmsQuoteDtlDM>) itemIds) {
				save.setQuoteid(Long.valueOf(quoteHdrDM.getQuoteId().toString()));
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
			comments.saveQuote(quoteHdrDM.getQuoteId(), quoteHdrDM.getStatus());
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
					listQuoteDetails.remove(quoteDtlDM);
				}
				quoteDtlDM.setMaterialid(((MmsEnqDtlDM) cbMaterial.getValue()).getMaterialid());
				quoteDtlDM.setMaterialname(((MmsEnqDtlDM) cbMaterial.getValue()).getMaterialName());
				if (tfQuoteQunt.getValue() != null && tfQuoteQunt.getValue().trim().length() > 0) {
					quoteDtlDM.setQuoteqty(Long.valueOf(tfQuoteQunt.getValue()));
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
				listQuoteDetails.add(quoteDtlDM);
				loadMatDtl();
				btnsavepurQuote.setCaption("Add");
				getCalculatedValues();
				resetDetailsFields();
			} else {
				cbMaterial.setComponentError(new UserError("Material Already Exist.."));
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
		tfBasictotal.setReadOnly(true);
		tfCessValue.setReadOnly(false);
		tfCessValue.setValue("0");
		taDelTerms.setValue(null);
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue("0");
		ckdutyexm.setValue(false);
		tfEDValue.setReadOnly(false);
		tfEDValue.setValue("0");
		taWarrentyTerms.setValue(null);
		tfVatValue.setReadOnly(false);
		tfVatValue.setValue("0");
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue("0");
		tfSubTotal.setReadOnly(true);
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue("0");
		tfSubTaxTotal.setReadOnly(true);
		tfQuoteVersion.setReadOnly(false);
		tfQuoteVersion.setValue("1");
		tfQuoteVersion.setReadOnly(true);
		tfQuoteRef.setReadOnly(false);
		tfQuoteRef.setValue("");
		ckPdcRqu.setValue(false);
		tapaymetTerms.setValue(null);
		tfPackingValue.setReadOnly(false);
		tfPackingValue.setValue("0");
		tfOtherValue.setReadOnly(false);
		tfOtherValue.setValue("0");
		tfHEDValue.setReadOnly(false);
		tfHEDValue.setValue("0");
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue("0");
		tfGrandtotal.setReadOnly(true);
		tfFreightValue.setReadOnly(false);
		tfFreightValue.setValue("0");
		taFreightTerms.setValue(null);
		cbStatus.setValue(null);
		dfQuoteDt.setValue(new Date());
		taRemark.setValue("");
		cbBranch.setComponentError(null);
		cbEnqNo.setComponentError(null);
		dfvalidDt.setComponentError(null);
		tapaymetTerms.setValue("");
		taFreightTerms.setValue("");
		taWarrentyTerms.setValue("");
		taDelTerms.setValue("");
		listQuoteDetails = new ArrayList<MmsQuoteDtlDM>();
		tblMatQuDtl.removeAllItems();
		new UploadDocumentUI(hlquoteDoc);
		cbBranch.setValue(branchId);
		cbStatus.setValue(null);
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
		tfUnitRate.setValue("0");
		tfUnitRate.setComponentError(null);
	}
	
	private void getCalculatedValues() {
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue(new BigDecimal(tfPackingValue.getValue()).add(new BigDecimal(tfBasictotal.getValue()))
				.toString());
		tfSubTotal.setReadOnly(true);
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue((new BigDecimal(tfSubTotal.getValue())).add(new BigDecimal(tfVatValue.getValue()))
				.add(new BigDecimal(tfEDValue.getValue())).add(new BigDecimal(tfHEDValue.getValue()))
				.add(new BigDecimal(tfCessValue.getValue())).add(new BigDecimal(tfCstValue.getValue())).toString());
		tfSubTaxTotal.setReadOnly(true);
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue((new BigDecimal(tfSubTaxTotal.getValue())).add(new BigDecimal(tfFreightValue.getValue()))
				.add(new BigDecimal(tfOtherValue.getValue())).toString());
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
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
