/**
 * File Name 		: MaterialQuote.java 
 * Description 		: this class is used for add/edit MaterialQuote  details. 
 * Author 			: Arun Jeyaraj R
 * Date 			:  Oct 18, 2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version     Date           		Modified By             Remarks
 * 0.1         Oct 18, 2014         Arun Jeyaraj R          Initial Version 
 */
package com.gnts.mms.txn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.base.service.mst.VendorService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPNumberField;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
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
import com.gnts.sms.txn.SmsComments;
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
import com.vaadin.server.VaadinService;
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
	private static final long serialVersionUID = 1L;
	private MmsEnqHdrService serviceMmsEnqHdr = (MmsEnqHdrService) SpringContextHelper.getBean("MmsEnqHdr");
	private MmsEnqDtlService serviceMmsEnqDtl = (MmsEnqDtlService) SpringContextHelper.getBean("MmsEnqDtl");
	private MMSVendorDtlService servicevendorEnq = (MMSVendorDtlService) SpringContextHelper.getBean("mmsVendorDtl");
	private MmsQuoteHdrService serviceMmsQuoteHdrService = (MmsQuoteHdrService) SpringContextHelper
			.getBean("mmsquotehdr");
	private MmsQuoteDtlService serviceMmsQuoteDtlService = (MmsQuoteDtlService) SpringContextHelper
			.getBean("mmsquotedtl");
	private BeanItemContainer<MmsQuoteHdrDM> beanQuoteHdr = null;
	private BeanItemContainer<MmsQuoteDtlDM> beanQuoteDtl = null;
	private GERPTable tblMatQuDtl;
	private List<MmsQuoteDtlDM> listQuoteDetails = new ArrayList<MmsQuoteDtlDM>();
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private SmsTaxesService serviceTaxesSms = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	// form layout for input controls for Sales Quote Header
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5;
	// form layout for input controls for Sales Quote Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4;
	// User Input Components for Sales Quote Details
	private ComboBox cbBranch, cbStatus, cbEnqNo, cbvendorname;
	private TextField tfQuoteNumber, tfQuoteRef, tfQuoteVersion, tfBasictotal, tfpackingPer, tfPaclingValue;
	private TextField tfSubTotal, tfVatPer, tfVatValue, tfEDPer, tfEDValue, tfHEDPer, tfvendorCode;
	private TextField tfHEDValue, tfCessPer, tfCessValue, tfCstPer, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightPer, tfFreightValue, tfOtherPer, tfOtherValue, tfGrandtotal, tfDocumentCharges,
			tfPDCCharges;
	private ComboBox cbpaymetTerms, cbFreightTerms, cbWarrentyTerms, cbDelTerms;
	private GERPComboBox cbQuotationType = new GERPComboBox("Quote Type");
	private PopupDateField dfQuoteDt, dfvalidDt;
	private CheckBox chkDutyExe, ckPdcRqu, chkCformReq;
	private Button btnsavepurQuote = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlquoteDoc = new VerticalLayout();
	// Sales QuoteDtl components
	private ComboBox cbMaterial, cbUom, cbdtlstatus;
	private TextField tfQuoteQunt, tfUnitRate, tfBasicValue, tfcustprodcode;
	private TextArea tacustproddesc;
	// local variables declaration
	private String username;
	private Long companyid;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private int recordCnt;
	private Long quoteId;
	private Long employeeId;
	private File file;
	private Long roleId;
	private Long branchId;
	private Long screenId;
	private Long moduleId;
	private SmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	private String status;
	// Initialize logger
	private Logger logger = Logger.getLogger(MaterialQuote.class);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	
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
		// Loading the SalesQuote UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting MaterialQuote UI");
		// Initialization for SalesQuote Details user input components
		tfQuoteNumber = new TextField("Quote No");
		tfQuoteNumber.setWidth("150");
		tfQuoteRef = new TextField("Quote Ref");
		tfQuoteRef.setWidth("150");
		dfQuoteDt = new GERPPopupDateField("Quote Date");
		dfQuoteDt.setInputPrompt("Select Date");
		dfQuoteDt.setWidth("80");
		dfvalidDt = new GERPPopupDateField("Valid Date");
		dfvalidDt.setInputPrompt("Select Date");
		dfvalidDt.setWidth("110px");
		cbEnqNo = new ComboBox("Enquiry No");
		cbEnqNo.setItemCaptionPropertyId("enquiryNo");
		cbEnqNo.setWidth("150");
		cbEnqNo.setRequired(true);
		loadEnquiryNo();
		cbEnqNo.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				if (item != null) {
					loadProductList(false);
					loadvendorlist();
				}
			}
		});
		cbvendorname = new ComboBox("Vendor Name");
		cbvendorname.setItemCaptionPropertyId("vendorName");
		cbvendorname.setWidth("150");
		tfQuoteVersion = new TextField("Quote Version");
		tfQuoteVersion.setWidth("150");
		tfBasictotal = new GERPNumberField("Basic total");
		tfBasictotal.setWidth("126");
		tfSubTotal = new GERPNumberField("Sub Total");
		tfSubTotal.setWidth("126");
		tfpackingPer = new TextField();
		tfpackingPer.setWidth("30");
		tfPaclingValue = new GERPNumberField();
		tfPaclingValue.setWidth("100");
		tfPaclingValue.setImmediate(true);
		tfVatPer = new TextField();
		tfVatPer.setWidth("30");
		tfVatValue = new GERPNumberField();
		tfVatValue.setWidth("100");
		tfEDPer = new TextField();
		tfEDPer.setWidth("30");
		tfEDValue = new GERPNumberField();
		tfEDValue.setWidth("100");
		tfHEDPer = new TextField();
		tfHEDPer.setWidth("30");
		tfHEDValue = new GERPNumberField();
		tfHEDValue.setWidth("100");
		tfCessPer = new TextField();
		tfCessPer.setWidth("30");
		tfCessValue = new GERPNumberField();
		tfCessValue.setWidth("100");
		tfSubTaxTotal = new GERPNumberField("Sub Tax Total");
		tfCstValue = new GERPNumberField();
		tfCstValue.setWidth("120");
		tfSubTaxTotal.setWidth("125");
		tfCstPer = new TextField();
		tfCstPer.setWidth("30");
		tfCstValue.setImmediate(true);
		tfGrandtotal = new GERPNumberField("Grand Total");
		tfGrandtotal.setWidth("150");
		cbvendorname = new ComboBox("Vendor Name");
		cbvendorname.setWidth("150");
		cbvendorname.setItemCaptionPropertyId("vendorName");
		cbvendorname.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbvendorname.getValue() != null) {
					VendorDM vendordm = serviceVendor.getVendorList(null, (Long) cbvendorname.getValue(), null, null,
							null, null, null, null, null, null, "F").get(0);
					if (vendordm.getPackingPrnct().toString() == null) {
						tfpackingPer.setValue(vendordm.getPackingPrnct().toString());
					} else {
						tfpackingPer.setValue("0");
					}
					if (vendordm.getEdPrnct().toString() != null) {
						tfEDPer.setValue(vendordm.getEdPrnct().toString());
					} else {
						tfEDPer.setValue("0");
					}
					if (vendordm.getVatPrnct().toString() != null) {
						tfVatPer.setValue(vendordm.getVatPrnct().toString());
					} else {
						tfVatPer.setValue("0");
					}
					if (vendordm.getCstPrnct().toString() != null) {
						tfCstPer.setValue(vendordm.getCstPrnct().toString());
					} else {
						tfCstPer.setValue("0");
					}
					if (vendordm.getFreightPrnct().toString() != null) {
						tfFreightPer.setValue(vendordm.getFreightPrnct().toString());
					} else {
						tfFreightPer.setValue("0");
					}
				}
			}
		});
		loadVendor();
		tfvendorCode = new TextField("Vendor Code");
		tfvendorCode.setWidth("150");
		tfvendorCode.setReadOnly(true);
		tfDocumentCharges = new GERPNumberField("Doc. Charges");
		tfDocumentCharges.setWidth("150");
		tfDocumentCharges.setImmediate(true);
		tfDocumentCharges.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfEDValue.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfCstPer.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfpackingPer.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfPDCCharges = new GERPNumberField("PDC Charges");
		tfPDCCharges.setWidth("150");
		tfPDCCharges.setImmediate(true);
		tfPDCCharges.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfFreightPer = new TextField();
		tfFreightPer.setWidth("30");
		tfFreightValue = new GERPNumberField();
		tfFreightValue.setWidth("120");
		tfOtherPer = new TextField();
		tfOtherPer.setWidth("30");
		tfOtherValue = new GERPNumberField();
		tfOtherValue.setWidth("120");
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
		chkDutyExe = new CheckBox("Duty Exempted");
		chkDutyExe.setImmediate(true);
		chkDutyExe.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		chkCformReq = new CheckBox("Cform Req");
		chkCformReq.setImmediate(true);
		chkCformReq.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		ckPdcRqu = new CheckBox("PDC Req");
		ckPdcRqu.setImmediate(true);
		ckPdcRqu.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (ckPdcRqu.getValue()) {
					tfPDCCharges.setReadOnly(false);
				} else {
					tfPDCCharges.setReadOnly(false);
					tfPDCCharges.setValue("0");
					tfPDCCharges.setReadOnly(true);
					getCalculatedValues();
				}
			}
		});
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
		cbStatus.setWidth("150");
		// Sales QuoteDtl Comp
		cbMaterial = new GERPComboBox("Material Name");
		cbMaterial.setItemCaptionPropertyId("materialName");
		cbMaterial.setWidth("130");
		cbMaterial.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbMaterial.getValue() != null) {
					tfQuoteQunt.setReadOnly(false);
					if (((MmsEnqDtlDM) cbMaterial.getValue()).getEnquiryQty() != null) {
						tfQuoteQunt.setValue(((MmsEnqDtlDM) cbMaterial.getValue()).getEnquiryQty() + "");
						// tfQuoteQunt.setReadOnly(true);
					} else {
						tfQuoteQunt.setValue("0");
					}
					tfQuoteQunt.setCaption("Quote Qty(" + ((MmsEnqDtlDM) cbMaterial.getValue()).getMatuom() + ")");
					if (((MmsEnqDtlDM) cbMaterial.getValue()).getMaterialName() != null) {
						tfcustprodcode.setValue(((MmsEnqDtlDM) cbMaterial.getValue()).getMaterialName());
					} else {
						tfcustprodcode.setValue("");
					}
					if (((MmsEnqDtlDM) cbMaterial.getValue()).getRemarks() != null) {
						tacustproddesc.setValue(((MmsEnqDtlDM) cbMaterial.getValue()).getRemarks());
					} else {
						tfcustprodcode.setValue("");
					}
				}
			}
		});
		cbdtlstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbdtlstatus.setWidth("150");
		tfQuoteQunt = new TextField("Quote Qty");
		tfQuoteQunt.setValue("0");
		tfQuoteQunt.setWidth("130");
		cbUom = new ComboBox("Product Uom");
		cbUom.setItemCaptionPropertyId("lookupname");
		cbUom.setWidth("100");
		loadUomList();
		tfcustprodcode = new TextField("Product Code");
		tfcustprodcode.setWidth("150");
		tfcustprodcode.setValue("0");
		tfBasicValue = new TextField("Basic value");
		tfBasicValue.setWidth("150");
		tfBasicValue.setValue("0");
		tfUnitRate = new TextField("Unit Rate");
		tfUnitRate.setWidth("130");
		tfUnitRate.setValue("0");
		tfUnitRate.setImmediate(true);
		tfUnitRate.addBlurListener(new BlurListener() {
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
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
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
		tacustproddesc = new TextArea("Product Description");
		tacustproddesc.setWidth("150");
		tacustproddesc.setHeight("50");
		btnsavepurQuote.addClickListener(new ClickListener() {
			// Click Listener for Add and Up
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateQuoteDetails()) {
					saveSalesQuoteDetails();
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
		// Load Quote type values.
		cbQuotationType.setRequired(true);
		cbQuotationType.addItem("Enquiry");
		cbQuotationType.addItem("Budgetry");
		cbQuotationType.addItem("Estimated");
		cbQuotationType.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbQuotationType.getValue() != null && cbQuotationType.getValue() != "Enquiry") {
					loadProductList(true);
				} else {
					cbMaterial.setContainerDataSource(null);
				}
			}
		});
		tfFreightValue.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfOtherValue.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		loadQuotationDetailList();
		btnsavepurQuote.setStyleName("add");
	}
	
	private void deleteDetails() {
		MmsQuoteDtlDM save = new MmsQuoteDtlDM();
		if (tblMatQuDtl.getValue() != null) {
			save = beanQuoteDtl.getItem(tblMatQuDtl.getValue()).getBean();
			listQuoteDetails.remove(save);
			quoteDtlresetFields();
			loadQuotationDetailList();
			btndelete.setEnabled(false);
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
		flColumn3.addComponent(tfQuoteNumber);
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
		flColumn5 = new FormLayout();
		flColumn1.addComponent(cbBranch);
		flColumn1.addComponent(cbEnqNo);
		flColumn1.addComponent(cbvendorname);
		flColumn1.addComponent(tfvendorCode);
		flColumn1.addComponent(cbQuotationType);
		flColumn1.addComponent(tfQuoteNumber);
		flColumn1.addComponent(tfQuoteRef);
		flColumn1.addComponent(tfQuoteVersion);
		flColumn2.addComponent(dfQuoteDt);
		dfQuoteDt.setWidth("110px");
		flColumn2.addComponent(dfvalidDt);
		flColumn2.addComponent(tfBasictotal);
		HorizontalLayout pp = new HorizontalLayout();
		pp.addComponent(tfpackingPer);
		pp.addComponent(tfPaclingValue);
		pp.setCaption("Packing(%)");
		flColumn2.addComponent(pp);
		flColumn2.setComponentAlignment(pp, Alignment.TOP_LEFT);
		flColumn2.addComponent(tfSubTotal);
		HorizontalLayout ed = new HorizontalLayout();
		ed.addComponent(tfEDPer);
		ed.addComponent(tfEDValue);
		ed.setCaption("ED");
		flColumn2.addComponent(ed);
		flColumn2.setComponentAlignment(ed, Alignment.TOP_LEFT);
		flColumn2.addComponent(tfSubTaxTotal);
		HorizontalLayout vp = new HorizontalLayout();
		vp.addComponent(tfVatPer);
		vp.addComponent(tfVatValue);
		vp.setCaption("VAT");
		flColumn2.addComponent(vp);
		flColumn2.setComponentAlignment(vp, Alignment.TOP_LEFT);
		HorizontalLayout cst = new HorizontalLayout();
		cst.addComponent(tfCstPer);
		cst.addComponent(tfCstValue);
		cst.setCaption("CST");
		flColumn3.addComponent(cst);
		flColumn3.setComponentAlignment(cst, Alignment.TOP_LEFT);
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
		flColumn3.addComponent(tfDocumentCharges);
		flColumn3.addComponent(tfPDCCharges);
		flColumn3.addComponent(tfGrandtotal);
		flColumn3.addComponent(cbpaymetTerms);
		flColumn4.addComponent(cbFreightTerms);
		flColumn4.addComponent(cbWarrentyTerms);
		flColumn4.addComponent(cbDelTerms);
		flColumn4.addComponent(cbStatus);
		flColumn4.addComponent(chkDutyExe);
		flColumn4.addComponent(chkCformReq);
		flColumn4.addComponent(ckPdcRqu);
		// flColumn5.addComponent(hlquoteDoc);
		HorizontalLayout hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flColumn1);
		hlHdr.addComponent(flColumn2);
		hlHdr.addComponent(flColumn3);
		hlHdr.addComponent(flColumn4);
		hlHdr.addComponent(flColumn5);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding SmsQuoteDtl components
		// Add components for User Input Layout
		flDtlColumn1 = new FormLayout();
		flDtlColumn2 = new FormLayout();
		flDtlColumn3 = new FormLayout();
		flDtlColumn4 = new FormLayout();
		flDtlColumn1.addComponent(cbMaterial);
		flDtlColumn1.addComponent(tfQuoteQunt);
		flDtlColumn1.addComponent(tfUnitRate);
		flDtlColumn2.addComponent(tfBasicValue);
		flDtlColumn2.addComponent(tacustproddesc);
		flDtlColumn3.addComponent(cbdtlstatus);
		flDtlColumn4.addComponent(btnsavepurQuote);
		flDtlColumn4.addComponent(btndelete);
		HorizontalLayout hlSmsQuotDtl = new HorizontalLayout();
		hlSmsQuotDtl.addComponent(flDtlColumn1);
		hlSmsQuotDtl.addComponent(flDtlColumn2);
		hlSmsQuotDtl.addComponent(flDtlColumn3);
		hlSmsQuotDtl.addComponent(flDtlColumn4);
		hlSmsQuotDtl.setSpacing(true);
		hlSmsQuotDtl.setMargin(true);
		VerticalLayout vlSmsQuoteHDR = new VerticalLayout();
		vlSmsQuoteHDR = new VerticalLayout();
		vlSmsQuoteHDR.addComponent(hlSmsQuotDtl);
		vlSmsQuoteHDR.addComponent(tblMatQuDtl);
		vlSmsQuoteHDR.setSpacing(true);
		hlquoteDoc.setMargin(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlSmsQuoteHDR, "Sales Quote Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		dtlTab.addTab(GERPPanelGenerator.createPanel(hlquoteDoc), "Documents");
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
		List<MmsQuoteHdrDM> listMaterialQuote = new ArrayList<MmsQuoteHdrDM>();
		String eno = null;
		if (cbEnqNo.getValue() != null) {
			eno = (((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
		}
		listMaterialQuote = serviceMmsQuoteHdrService.getMmsQuoteHdrList(companyid, null, null,
				(Long) cbBranch.getValue(), eno, (String) tfQuoteRef.getValue(), (String) cbStatus.getValue(), "F");
		recordCnt = listMaterialQuote.size();
		beanQuoteHdr = new BeanItemContainer<MmsQuoteHdrDM>(MmsQuoteHdrDM.class);
		beanQuoteHdr.addAll(listMaterialQuote);
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
	
	private void loadQuotationDetailList() {
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Taxslap. result set");
		tblMatQuDtl.setContainerDataSource(beanQuoteDtl);
		tblMatQuDtl.setVisibleColumns(new Object[] { "materialname", "quoteqty", "unitrate", "basicvalue",
				"lastupdateddt", "lastupdatedby" });
		tblMatQuDtl.setColumnHeaders(new String[] { "Material Name", "Quote Qty", "UnitRate", "Basic Value",
				"Last Updated Date", "Last Updated By" });
		tblMatQuDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Load BranchList
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
	
	// Load Uom List
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
			e.printStackTrace();
		}
	}
	
	// Load EnquiryNo
	private void loadEnquiryNo() {
		BeanItemContainer<MmsEnqHdrDM> beanmmsPurEnqHdrDM = new BeanItemContainer<MmsEnqHdrDM>(MmsEnqHdrDM.class);
		beanmmsPurEnqHdrDM.addAll(serviceMmsEnqHdr.getMmsEnqHdrList(companyid, null, null, null, null, "F"));
		cbEnqNo.setContainerDataSource(beanmmsPurEnqHdrDM);
	}
	
	// Load PaymentTerms
	private void loadPaymentTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_PAYTRM"));
			cbpaymetTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Load Vendor Names
	 */
	private void loadVendor() {
		try {
			BeanContainer<Long, VendorDM> beanVendor = new BeanContainer<Long, VendorDM>(VendorDM.class);
			beanVendor.setBeanIdProperty("vendorId");
			beanVendor.addAll(serviceVendor.getVendorList(null, null, companyid, null, null, null, null, null, null,
					null, "P"));
			cbvendorname.setContainerDataSource(beanVendor);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load FreightTerms
	private void loadFreightTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_FRTTRM"));
			cbFreightTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load WarrentyTerms
	private void loadWarentyTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_WRNTRM"));
			cbWarrentyTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load DeliveryTerms
	private void loadDeliveryTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_DELTRM"));
			cbDelTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Reset the selected row's data into Sales Quote Hdr input components
	private void editQuoteHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			MmsQuoteHdrDM quoteHdrDM = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			quoteId = quoteHdrDM.getQuoteId();
			cbBranch.setValue(quoteHdrDM.getBranchId());
			logger.info("sms salaes enquiry no" + quoteHdrDM.getEnquiryNo());
			cbEnqNo.setValue(quoteHdrDM.getEnquiryNo());
			cbQuotationType.setValue(quoteHdrDM.getQuotationType());
			System.out.println("editsmsQuotlist.getQuoteNumber()-->" + quoteHdrDM.getQuoteNumber());
			tfQuoteNumber.setReadOnly(false);
			tfQuoteNumber.setValue(quoteHdrDM.getQuoteNumber());
			tfQuoteNumber.setReadOnly(true);
			tfQuoteRef.setValue(quoteHdrDM.getQuoteRef());
			dfQuoteDt.setValue(quoteHdrDM.getQuoteDate());
			dfvalidDt.setValue(quoteHdrDM.getQuoteValDate());
			tfQuoteVersion.setValue(quoteHdrDM.getQuoteVersion());
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(quoteHdrDM.getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfpackingPer.setValue(quoteHdrDM.getPackingPrcnt().toString());
			tfPaclingValue.setReadOnly(false);
			tfPaclingValue.setValue(quoteHdrDM.getPackingValue().toString());
			tfPaclingValue.setReadOnly(true);
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(quoteHdrDM.getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(quoteHdrDM.getVatPrcnt().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(quoteHdrDM.getVatValue().toString());
			tfVatValue.setReadOnly(true);
			tfEDPer.setValue(quoteHdrDM.getEd_Prcnt().toString());
			tfEDValue.setReadOnly(false);
			tfEDValue.setValue(quoteHdrDM.getEdValue().toString());
			tfEDValue.setReadOnly(true);
			tfHEDPer.setValue(quoteHdrDM.getHedPrcnt().toString());
			tfHEDValue.setReadOnly(false);
			tfHEDValue.setValue(quoteHdrDM.getHedValue().toString());
			tfHEDValue.setReadOnly(true);
			tfCessPer.setValue(quoteHdrDM.getCessPrcnt().toString());
			tfCessValue.setReadOnly(false);
			tfCessValue.setValue(quoteHdrDM.getCessValue().toString());
			tfCessValue.setReadOnly(true);
			tfCstPer.setValue(quoteHdrDM.getCessPrcnt().toString());
			tfCstValue.setReadOnly(false);
			tfCstValue.setValue(quoteHdrDM.getCessValue().toString());
			tfCstValue.setReadOnly(true);
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(quoteHdrDM.getSubTaxTotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			tfFreightPer.setValue(quoteHdrDM.getFreightPrcnt().toString());
			tfFreightValue.setValue(quoteHdrDM.getFreightValue().toString());
			tfOtherPer.setValue((quoteHdrDM.getOthersPrcnt().toString()));
			tfOtherValue.setValue((quoteHdrDM.getOthersValue().toString()));
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(quoteHdrDM.getGrandTotal().toString());
			tfGrandtotal.setReadOnly(true);
			tfDocumentCharges.setReadOnly(false);
			if (quoteHdrDM.getDocumentCharges() != null) {
				tfDocumentCharges.setValue(quoteHdrDM.getDocumentCharges().toString());
			} else {
				tfDocumentCharges.setValue("0");
			}
			if (quoteHdrDM.getPdcCharges() != null) {
				tfPDCCharges.setReadOnly(false);
				tfPDCCharges.setValue(quoteHdrDM.getPdcCharges().toString());
			} else {
				tfPDCCharges.setReadOnly(false);
				tfPDCCharges.setValue("0");
			}
			if (quoteHdrDM.getPaymentTerms() != null) {
				cbpaymetTerms.setValue(quoteHdrDM.getPaymentTerms().toString());
			}
			if (quoteHdrDM.getFreightTerms() != null) {
				cbFreightTerms.setValue(quoteHdrDM.getFreightTerms());
			}
			if (quoteHdrDM.getWarrantyTerms() != null) {
				cbWarrentyTerms.setValue(quoteHdrDM.getWarrantyTerms());
			}
			if (quoteHdrDM.getDeliveryTerms() != null) {
				cbDelTerms.setValue(quoteHdrDM.getDeliveryTerms());
			}
			if (quoteHdrDM.getStatus() != null) {
				cbStatus.setValue(quoteHdrDM.getStatus().toString());
			}
			if (quoteHdrDM.getDutyExempted().equals("Y")) {
				chkDutyExe.setValue(true);
			} else {
				chkDutyExe.setValue(false);
			}
			if (quoteHdrDM.getCformReqd().equals("Y")) {
				chkCformReq.setValue(true);
			} else {
				chkCformReq.setValue(false);
			}
			if (quoteHdrDM.getPdcReqd().equals("Y")) {
				ckPdcRqu.setValue(true);
			} else {
				ckPdcRqu.setValue(false);
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
		loadQuotationDetailList();
		comments = new SmsComments(vlTableForm, null, null, null, null, null, null, null, null, quoteId, null, null,
				status);
		comments.loadsrch(true, null, null, null, null, null, null, null, null, quoteId, null, null, null);
	}
	
	// Reset the selected row's data into Sales Quote Detail input components
	private void editQuoteDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		if (tblMatQuDtl.getValue() != null) {
			MmsQuoteDtlDM quoteDtlDM = beanQuoteDtl.getItem(tblMatQuDtl.getValue()).getBean();
			Long uom = quoteDtlDM.getMaterialid();
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
		}
	}
	
	protected void quoteDtlresetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMaterial.setValue(null);
		cbMaterial.setComponentError(null);
		cbUom.setValue(null);
		cbUom.setComponentError(null);
		tfBasicValue.setComponentError(null);
		tfBasicValue.setReadOnly(false);
		tfBasicValue.setValue("0");
		tfBasicValue.setReadOnly(true);
		tfQuoteQunt.setReadOnly(false);
		tfQuoteQunt.setValue("");
		tfQuoteQunt.setReadOnly(true);
		tacustproddesc.setValue("");
		tacustproddesc.setComponentError(null);
		tfcustprodcode.setValue("");
		tfcustprodcode.setComponentError(null);
		cbdtlstatus.setValue(cbdtlstatus.getItemIds().iterator().next());
		cbdtlstatus.setComponentError(null);
		tfUnitRate.setValue("");
		tfUnitRate.setComponentError(null);
		cbEnqNo.setRequired(true);
	}
	
	// Calculated Values for Sales Quote Hdr validation
	private void getCalculatedValues() {
		tfVatPer.setValue("0");
		if (chkDutyExe.getValue()) {
			tfEDPer.setValue("0");
			tfHEDPer.setValue("0");
			tfCessPer.setValue("0");
		}
		BigDecimal basictotal = new BigDecimal(tfBasictotal.getValue());
		BigDecimal packingvalue = gerPercentageValue(new BigDecimal(tfpackingPer.getValue()), basictotal);
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue(packingvalue.toString());
		tfPaclingValue.setReadOnly(true);
		BigDecimal pdcCharges = new BigDecimal("0");
		try {
			pdcCharges = new BigDecimal(tfPDCCharges.getValue());
		}
		catch (Exception e) {
		}
		BigDecimal subtotal = packingvalue.add(basictotal).add(pdcCharges);
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue(subtotal.toString());
		tfSubTotal.setReadOnly(true);
		BigDecimal edValue = gerPercentageValue(new BigDecimal(tfEDPer.getValue()), subtotal);
		tfEDValue.setReadOnly(false);
		tfEDValue.setValue(edValue.toString());
		tfEDValue.setReadOnly(true);
		BigDecimal subtaxTotal = subtotal.add(new BigDecimal(tfEDValue.getValue()));
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue(subtaxTotal.toString());
		tfSubTaxTotal.setReadOnly(true);
		System.out.println("subtaxTotal" + subtaxTotal);
		System.out.println("tfVatPer.getValue()" + tfVatPer.getValue());
		BigDecimal vatvalue = gerPercentageValue(new BigDecimal(tfVatPer.getValue()), subtaxTotal);
		tfVatValue.setReadOnly(false);
		tfVatValue.setValue(vatvalue.toString());
		tfVatValue.setReadOnly(true);
		BigDecimal cstval = gerPercentageValue(new BigDecimal(tfCstPer.getValue()), subtaxTotal);
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue(cstval.toString());
		tfCstValue.setReadOnly(true);
		BigDecimal csttotal = vatvalue;
		BigDecimal frgval = new BigDecimal(0);
		BigDecimal otherval = new BigDecimal(0);
		if (!tfFreightPer.getValue().equals("0")) {
			frgval = gerPercentageValue(new BigDecimal(tfFreightPer.getValue()), subtaxTotal);
			tfFreightValue.setValue(frgval.toString());
		} else {
			tfFreightValue.setValue("0");
			frgval = new BigDecimal(tfFreightValue.getValue());
		}
		if (!tfOtherPer.getValue().equals("0")) {
			otherval = gerPercentageValue(new BigDecimal(tfOtherPer.getValue()), subtaxTotal);
			tfOtherValue.setValue(otherval.toString());
		} else {
			tfOtherValue.setValue("0");
			otherval = new BigDecimal(tfOtherValue.getValue());
		}
		BigDecimal grand = frgval.add(otherval).add(cstval).add(csttotal);
		BigDecimal documentCharges = new BigDecimal(tfDocumentCharges.getValue());
		BigDecimal grandTotal = subtaxTotal.add(grand).add(documentCharges);
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue(grandTotal.toString());
		tfGrandtotal.setReadOnly(true);
	}
	
	private BigDecimal gerPercentageValue(BigDecimal percent, BigDecimal value) {
		return (percent.multiply(value).divide(new BigDecimal("100"))).setScale(2, RoundingMode.CEILING);
	}
	
	private void loadProductList(Boolean isFullList) {
		try {
			BeanItemContainer<MmsEnqDtlDM> beanMaterial = new BeanItemContainer<MmsEnqDtlDM>(MmsEnqDtlDM.class);
			beanMaterial.addAll(serviceMmsEnqDtl.getMmsEnqDtlList(null,
					((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId(), null, null, null));
			cbMaterial.setContainerDataSource(beanMaterial);
		}
		catch (Exception e) {
			e.printStackTrace();
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
		tfQuoteNumber.setValue("");
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
		cbdtlstatus.setValue(cbdtlstatus.getItemIds().iterator().next());
		cbMaterial.setRequired(true);
		cbUom.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		// cbEnqNo.setRequired(true);
		tfQuoteQunt.setRequired(true);
		cbUom.setRequired(true);
		tfUnitRate.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		cbBranch.setValue(branchId);
		tfBasictotal.setReadOnly(true);
		tfSubTotal.setReadOnly(true);
		tfSubTaxTotal.setReadOnly(true);
		tfGrandtotal.setReadOnly(true);
		tfVatValue.setReadOnly(true);
		tfEDValue.setReadOnly(true);
		tfHEDValue.setReadOnly(true);
		tfCessValue.setReadOnly(true);
		tfCstValue.setReadOnly(true);
		new UploadDocumentUI(hlquoteDoc);
		tblMatQuDtl.setVisible(true);
		assembleInputUserLayout();
		loadQuotationDetailList();
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "MM_QN").get(0);
			System.out.println(slnoObj);
			tfQuoteNumber.setReadOnly(false);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfQuoteNumber.setValue(slnoObj.getKeyDesc());
				tfQuoteNumber.setReadOnly(true);
			} else {
				tfQuoteNumber.setReadOnly(false);
			}
			btnsavepurQuote.setCaption("Add");
			lblNotification.setValue("");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, quoteId, null, null, null, null, null, null,
				null, null);
		cbEnqNo.setRequired(true);
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
		cbBranch.setValue(branchId);
		cbEnqNo.setRequired(true);
		tfQuoteQunt.setRequired(true);
		cbUom.setRequired(true);
		tfUnitRate.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		tfQuoteNumber.setReadOnly(false);
		cbMaterial.setRequired(true);
		cbUom.setRequired(true);
		lblNotification.setValue("");
		assembleInputUserLayout();
		resetFields();
		editQuoteDtl();
		editQuoteHdr();
		// loadPurDtl();
		comments.loadsrch(true, null, null, null, quoteId, null, null, null, null, null, null, null, null);
		comments.editcommentDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbBranch.setComponentError(null);
		cbEnqNo.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if (cbEnqNo.getValue() == null) {
			cbEnqNo.setComponentError(new UserError(GERPErrorCodes.ENQUIRY_NO));
			errorFlag = true;
		}
		if (tblMatQuDtl.size() == 0) {
			cbMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + cbBranch.getValue() + "," + cbEnqNo.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Validation for Sales Quote Details
	private boolean validateQuoteDetails() {
		boolean isValid = true;
		if (cbMaterial.getValue() == null) {
			cbMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			cbMaterial.setComponentError(null);
		}
		if (tfUnitRate.getValue() == "0") {
			tfUnitRate.setComponentError(new UserError(GERPErrorCodes.UNIT_RATE));
			isValid = false;
		} else {
			tfUnitRate.setComponentError(null);
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			MmsQuoteHdrDM salesQuoteHdrobj = new MmsQuoteHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				salesQuoteHdrobj = beanQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			salesQuoteHdrobj.setQuoteNumber(tfQuoteNumber.getValue());
			salesQuoteHdrobj.setQuoteRef(tfQuoteRef.getValue());
			salesQuoteHdrobj.setBranchId((Long) cbBranch.getValue());
			salesQuoteHdrobj.setQuotationType((String) cbQuotationType.getValue());
			salesQuoteHdrobj.setCompanyId(companyid);
			if (cbEnqNo.getValue() != null) {
				salesQuoteHdrobj.setEnquiryNo(((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
				salesQuoteHdrobj.setEnquiryId(((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId());
			}
			salesQuoteHdrobj.setQuoteDate(dfQuoteDt.getValue());
			salesQuoteHdrobj.setQuoteValDate(dfvalidDt.getValue());
			salesQuoteHdrobj.setQuoteVersion(tfQuoteVersion.getValue());
			if (tfBasictotal.getValue() != null && tfBasictotal.getValue().trim().length() > 0) {
				salesQuoteHdrobj.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			}
			salesQuoteHdrobj.setPackingPrcnt((new BigDecimal(tfpackingPer.getValue())));
			if (tfPaclingValue.getValue() != null && tfPaclingValue.getValue().trim().length() > 0) {
				salesQuoteHdrobj.setPackingValue((new BigDecimal(tfPaclingValue.getValue())));
			}
			salesQuoteHdrobj.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			salesQuoteHdrobj.setVatPrcnt(((new BigDecimal(tfVatPer.getValue()))));
			if (tfVatValue.getValue() != null && tfVatValue.getValue().trim().length() > 0) {
				salesQuoteHdrobj.setVatValue((new BigDecimal(tfVatValue.getValue())));
			}
			if (tfEDPer.getValue() != null && tfEDPer.getValue().trim().length() > 0) {
				salesQuoteHdrobj.setEd_Prcnt((new BigDecimal(tfEDPer.getValue())));
			}
			if (tfEDValue.getValue() != null && tfEDValue.getValue().trim().length() > 0) {
				salesQuoteHdrobj.setEdValue(new BigDecimal(tfEDValue.getValue()));
			}
			salesQuoteHdrobj.setHedValue(new BigDecimal(tfHEDValue.getValue()));
			salesQuoteHdrobj.setHedPrcnt((new BigDecimal(tfHEDPer.getValue())));
			salesQuoteHdrobj.setCessPrcnt((new BigDecimal(tfCessPer.getValue())));
			salesQuoteHdrobj.setCessValue(new BigDecimal(tfCessValue.getValue()));
			salesQuoteHdrobj.setCstPrcnt((new BigDecimal(tfCstPer.getValue())));
			if (tfCstValue.getValue() != null && tfCstValue.getValue().trim().length() > 0) {
				salesQuoteHdrobj.setCstValue((new BigDecimal(tfCstValue.getValue())));
			}
			salesQuoteHdrobj.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			salesQuoteHdrobj.setFreightPrcnt(new BigDecimal(tfFreightPer.getValue()));
			salesQuoteHdrobj.setFreightValue(new BigDecimal(tfFreightValue.getValue()));
			salesQuoteHdrobj.setOthersPrcnt(new BigDecimal(tfOtherPer.getValue()));
			salesQuoteHdrobj.setOthersValue(new BigDecimal(tfOtherValue.getValue()));
			salesQuoteHdrobj.setDocumentCharges(new BigDecimal(tfDocumentCharges.getValue()));
			salesQuoteHdrobj.setPdcCharges(new BigDecimal(tfPDCCharges.getValue()));
			salesQuoteHdrobj.setGrandTotal(new BigDecimal(tfGrandtotal.getValue()));
			if (cbpaymetTerms.getValue() != null) {
				salesQuoteHdrobj.setPaymentTerms((cbpaymetTerms.getValue().toString()));
			}
			if (cbFreightTerms.getValue() != null) {
				salesQuoteHdrobj.setFreightTerms(cbFreightTerms.getValue().toString());
			}
			if (cbWarrentyTerms.getValue() != null) {
				salesQuoteHdrobj.setWarrantyTerms((cbWarrentyTerms.getValue().toString()));
			}
			if (cbDelTerms.getValue() != null) {
				salesQuoteHdrobj.setDeliveryTerms(cbDelTerms.getValue().toString());
			}
			if (chkDutyExe.getValue().equals(true)) {
				salesQuoteHdrobj.setDutyExempted("Y");
			} else if (chkDutyExe.getValue().equals(false)) {
				salesQuoteHdrobj.setDutyExempted("N");
			}
			if (chkCformReq.getValue().equals(true)) {
				salesQuoteHdrobj.setCformReqd("Y");
			} else if (chkCformReq.getValue().equals(false)) {
				salesQuoteHdrobj.setCformReqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				salesQuoteHdrobj.setPdcReqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				salesQuoteHdrobj.setPdcReqd("N");
			}
			if (cbStatus.getValue() != null) {
				salesQuoteHdrobj.setStatus(cbStatus.getValue().toString());
			}
			salesQuoteHdrobj.setPreparedBy(employeeId);
			salesQuoteHdrobj.setReviewedBy(null);
			salesQuoteHdrobj.setActionedBy(null);
			salesQuoteHdrobj.setLastupdateddt(DateUtils.getcurrentdate());
			salesQuoteHdrobj.setLastupdatedby(username);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			salesQuoteHdrobj.setQuoteDoc(fileContents);
			validateQuoteDetails();
			serviceMmsQuoteHdrService.saveOrUpdateMmsQuoteHdr(salesQuoteHdrobj);
			@SuppressWarnings("unchecked")
			Collection<MmsQuoteDtlDM> itemIds = (Collection<MmsQuoteDtlDM>) tblMatQuDtl.getVisibleItemIds();
			for (MmsQuoteDtlDM save : (Collection<MmsQuoteDtlDM>) itemIds) {
				save.setQuoteid(Long.valueOf(salesQuoteHdrobj.getQuoteId().toString()));
				serviceMmsQuoteDtlService.saveOrUpdatemmsquotedtlDetails(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_QN");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "MM_QN");
					}
				}
			}
			quoteDtlresetFields();
			loadSrchRslt();
			quoteId = 0L;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// saveSalesQuoteDetails()-->this function is used for save the Sales Quote details for temporary
	protected void saveSalesQuoteDetails() {
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
				count = 0;
			}
			if (count == 0) {
				MmsQuoteDtlDM materialQuotDtlobj = new MmsQuoteDtlDM();
				if (tblMatQuDtl.getValue() != null) {
					materialQuotDtlobj = beanQuoteDtl.getItem(tblMatQuDtl.getValue()).getBean();
					listQuoteDetails.remove(materialQuotDtlobj);
				}
				materialQuotDtlobj.setMaterialid(((MmsEnqDtlDM) cbMaterial.getValue()).getMaterialid());
				materialQuotDtlobj.setMaterialname(((MmsEnqDtlDM) cbMaterial.getValue()).getMaterialName());
				if (tfQuoteQunt.getValue() != null && tfQuoteQunt.getValue().trim().length() > 0) {
					materialQuotDtlobj.setQuoteqty(Long.valueOf(tfQuoteQunt.getValue()));
				}
				if (tfUnitRate.getValue() != null && tfUnitRate.getValue().trim().length() > 0) {
					materialQuotDtlobj.setUnitrate((Long.valueOf(tfUnitRate.getValue())));
				}
				materialQuotDtlobj.setMatuom(((MmsEnqDtlDM) cbMaterial.getValue()).getMatuom());
				if (tfBasicValue.getValue() != null && tfBasicValue.getValue().trim().length() > 0) {
					materialQuotDtlobj.setBasicvalue(new BigDecimal(tfBasicValue.getValue()));
				}
				materialQuotDtlobj.setLastupdateddt(DateUtils.getcurrentdate());
				materialQuotDtlobj.setLastupdatedby(username);
				listQuoteDetails.add(materialQuotDtlobj);
				loadQuotationDetailList();
				resetDetailsFields();
				btnsavepurQuote.setCaption("Add");
				getCalculatedValues();
			} else {
				cbMaterial.setComponentError(new UserError("Material Already Exist.."));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		quoteDtlresetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for TestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_P_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(quoteId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		cbMaterial.setRequired(false);
		cbBranch.setValue(null);
		cbEnqNo.setRequired(false);
		tfQuoteQunt.setRequired(false);
		cbUom.setRequired(false);
		tfUnitRate.setRequired(false);
		resetFields();
		quoteDtlresetFields();
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		loadSrchRslt();
		cbEnqNo.setRequired(false);
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
		chkCformReq.setValue(false);
		try {
			tfCstPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CST", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCstPer.setValue("0");
		}
		cbEnqNo.setRequired(false);
		tfCessValue.setReadOnly(false);
		tfCessValue.setValue("0");
		tfCstPer.setValue("10");
		cbDelTerms.setValue(null);
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue("0");
		chkDutyExe.setValue(false);
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
		System.out.println("tfVatPer--->" + tfVatPer.getValue());
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue("0");
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue("0");
		tfQuoteVersion.setValue("1");
		tfQuoteNumber.setReadOnly(false);
		tfQuoteNumber.setValue("");
		tfQuoteRef.setValue("");
		ckPdcRqu.setValue(false);
		cbpaymetTerms.setValue(null);
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue("0");
		tfpackingPer.setValue("10");
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
		tfDocumentCharges.setValue("0");
		tfPDCCharges.setReadOnly(false);
		tfPDCCharges.setValue("0");
		tfPDCCharges.setReadOnly(true);
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue("0");
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
		cbBranch.setValue(null);
		cbQuotationType.setValue("Enquiry");
		dfQuoteDt.setValue(new Date());
		dfvalidDt.setValue(DateUtils.addDays(new Date(), 7));
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
		tfQuoteQunt.setReadOnly(false);
		tfQuoteQunt.setValue("0");
		tfQuoteQunt.setReadOnly(true);
		tfUnitRate.setValue("0");
		tfUnitRate.setComponentError(null);
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, Long> parameterMap = new HashMap<String, Long>();
			parameterMap.put("QTID", quoteId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/qutationReport"); // productlist is the name of my jasper
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				statement.close();
				Database.close(connection);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadvendorlist() {
		try {
			BeanContainer<Long, MMSVendorDtlDM> beanvndrdtl = new BeanContainer<Long, MMSVendorDtlDM>(
					MMSVendorDtlDM.class);
			beanvndrdtl.setBeanIdProperty("vendorid");
			beanvndrdtl.addAll(servicevendorEnq.getmaterialvdrdtl(null,
					((MmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId(), null));
			cbvendorname.setContainerDataSource(beanvndrdtl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
