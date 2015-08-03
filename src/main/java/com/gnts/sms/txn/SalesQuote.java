/**
 * File Name 		:	SalesQuote.java 
 * Description 		:	This class is used for add/edit SalesQuote  details. 
 * Author 			:	Sudhakar
 * Date 			:	Oct 8, 2014
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * Version  Date       	  Modified By   Remarks
 * 0.1      Oct 8, 2014   Sudhakar      Initial Version 
 */
package com.gnts.sms.txn;

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
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.txn.ClientsContactsDM;
import com.gnts.crm.service.txn.ClientContactsService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPNumberField;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextField;
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
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsEnquiryDtlDM;
import com.gnts.sms.domain.txn.SmsQuoteDtlDM;
import com.gnts.sms.domain.txn.SmsQuoteHdrDM;
import com.gnts.sms.service.mst.SmsTaxesService;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsEnquiryDtlService;
import com.gnts.sms.service.txn.SmsQuoteDtlService;
import com.gnts.sms.service.txn.SmsQuoteHdrService;
import com.gnts.stt.mfg.domain.txn.RotoPlanHdrDM;
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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SalesQuote extends BaseTransUI {
	private SmsQuoteHdrService servicesmsQuoteHdr = (SmsQuoteHdrService) SpringContextHelper.getBean("smsquotehdr");
	private ClientContactsService serviceClntContact = (ClientContactsService) SpringContextHelper
			.getBean("clientContact");
	private SmsQuoteDtlService servicesmseQuoteDtl = (SmsQuoteDtlService) SpringContextHelper.getBean("SmsQuoteDtl");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private SmsEnquiryDtlService serviceEnqDetail = (SmsEnquiryDtlService) SpringContextHelper.getBean("SmsEnquiryDtl");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private SmsTaxesService serviceTaxesSms = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private BeanContainer<Long, ClientsContactsDM> beanclientcontact = null;
	// form layout for input controls for Sales Quote Header
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5;
	// form layout for input controls for Sales Quote Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4;
	// User Input Components for Sales Quote Details
	private ComboBox cbBranch, cbStatus, cbEnqNo, cbwindTechPers, cbwindcommPerson;
	private TextField tfQuoteNumber, tfQuoteRef, tfQuoteVersion, tfBasictotal, tfpackingPer, tfPaclingValue;
	private TextField tfSubTotal, tfVatPer, tfVatValue, tfEDPer, tfEDValue, tfHEDPer;
	private TextField tfHEDValue, tfCessPer, tfCessValue, tfCstPer, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightValue, tfOtherValue, tfGrandtotal, tfDocumentCharges, tfPDCCharges;
	private ComboBox cbpaymetTerms, cbFreightTerms, cbWarrentyTerms, cbDelTerms;
	private TextArea tfLiquidatedDamage;
	private GERPComboBox cbQuotationType = new GERPComboBox("Quote Type");
	private PopupDateField dfQuoteDt, dfvalidDt;
	private CheckBox chkDutyExe, ckPdcRqu, chkCformReq;
	private Button btnsavepurQuote = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlquoteDoc = new VerticalLayout();
	// Sales QuoteDtl components
	private ComboBox cbproduct, cbUom, cbdtlstatus;
	private TextField tfQuoteQunt, tfUnitRate, tfBasicValue, tfcustprodcode;
	private TextArea tacustproddesc;
	private GERPTextField tfCustomField1 = new GERPTextField("Part Number");
	private GERPTextField tfCustomField2 = new GERPTextField("Drawing Number");
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private BeanItemContainer<SmsQuoteHdrDM> beansmsQuoteHdr = null;
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	private BeanItemContainer<SmsQuoteDtlDM> beansmsQuoteDtl = null;
	List<SmsQuoteDtlDM> smsQuoteDtlList = new ArrayList<SmsQuoteDtlDM>();
	// local variables declaration
	private String username;
	private Long companyid;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private GERPTable tblsmsQuoteDtl;
	private int recordCnt;
	private Long quoteId;
	private Long EmployeeId;
	private File file;
	private Long roleId;
	private Long branchId;
	private Long screenId;
	private Long moduleId;
	public static boolean filevalue1 = false;
	private SmsComments comments;
	VerticalLayout vlTableForm = new VerticalLayout();
	private String status;
	// Initialize logger
	private Logger logger = Logger.getLogger(PurchaseQuote.class);
	public Button btndelete = new GERPButton("Delete", "delete", this);
	
	// Constructor received the parameters from Login UI class
	public SalesQuote() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		EmployeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PurchaseQuote() constructor");
		// Loading the SalesQuote UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax UI");
		// Initialization for SalesQuote Details user input components
		cbwindTechPers = new GERPComboBox("Tech.Person");
		cbwindTechPers.setWidth("150");
		cbwindcommPerson = new GERPComboBox("Commer.Person");
		cbwindcommPerson.setWidth("150");
		tfQuoteNumber = new TextField("Quote No");
		tfQuoteNumber.setWidth("150");
		tfQuoteRef = new TextField("Quote Ref");
		tfQuoteRef.setWidth("150");
		dfQuoteDt = new GERPPopupDateField("Quote Date");
		dfQuoteDt.setInputPrompt("Select Date");
		dfQuoteDt.setWidth("110px");
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
					loadclientCommCont();
					loadclienTecCont();
				}
			}
		});
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
		tfFreightValue = new GERPNumberField("Freight Value");
		tfFreightValue.setWidth("150");
		tfOtherValue = new GERPNumberField("Other Value");
		tfOtherValue.setWidth("150");
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
		tfLiquidatedDamage = new TextArea("Liquidated Damage");
		tfLiquidatedDamage.setWidth("150");
		tfLiquidatedDamage.setHeight("50");
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
			ApprovalSchemaDM obj = servicesmsQuoteHdr.getReviewerId(companyid, screenId, branchId, roleId).get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_QUOTE_HDR, BASEConstants.QUOTE_STATUS_RV);
			} else {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_QUOTE_HDR, BASEConstants.QUOTE_STATUS);
			}
		}
		catch (Exception e) {
		}
		cbStatus.setWidth("150");
		// Sales QuoteDtl Comp
		cbproduct = new GERPComboBox("Product Name");
		cbproduct.setItemCaptionPropertyId("prodname");
		cbproduct.setWidth("130");
		cbproduct.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbproduct.getValue() != null) {
					tfQuoteQunt.setReadOnly(false);
					if (((SmsEnquiryDtlDM) cbproduct.getValue()).getEnquiryqty() != null) {
						tfQuoteQunt.setValue(((SmsEnquiryDtlDM) cbproduct.getValue()).getEnquiryqty() + "");
						// tfQuoteQunt.setReadOnly(true);
					} else {
						tfQuoteQunt.setValue("0");
					}
					tfQuoteQunt.setCaption("Quote Qty(" + ((SmsEnquiryDtlDM) cbproduct.getValue()).getUom() + ")");
					if (((SmsEnquiryDtlDM) cbproduct.getValue()).getCustprodcode() != null) {
						tfcustprodcode.setValue(((SmsEnquiryDtlDM) cbproduct.getValue()).getCustprodcode());
					} else {
						tfcustprodcode.setValue("");
					}
					if (((SmsEnquiryDtlDM) cbproduct.getValue()).getCustproddesc() != null) {
						tacustproddesc.setValue(((SmsEnquiryDtlDM) cbproduct.getValue()).getCustproddesc());
					} else {
						tfcustprodcode.setValue("");
					}
					if (((SmsEnquiryDtlDM) cbproduct.getValue()).getCustomField1() != null) {
						tfCustomField1.setValue(((SmsEnquiryDtlDM) cbproduct.getValue()).getCustomField1());
					} else {
						tfCustomField1.setValue("");
					}
					if (((SmsEnquiryDtlDM) cbproduct.getValue()).getCustomField2() != null) {
						tfCustomField2.setValue(((SmsEnquiryDtlDM) cbproduct.getValue()).getCustomField2());
					} else {
						tfCustomField2.setValue("");
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
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (DtlValidation()) {
					saveSalesQuoteDetails();
				}
			}
		});
		btndelete.setEnabled(false);
		tblsmsQuoteDtl = new GERPTable();
		tblsmsQuoteDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblsmsQuoteDtl.isSelected(event.getItemId())) {
					tblsmsQuoteDtl.setImmediate(true);
					btnsavepurQuote.setCaption("Add");
					btnsavepurQuote.setStyleName("savebt");
					btndelete.setEnabled(false);
					quoteDtlresetFields();
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
					cbproduct.setContainerDataSource(null);
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
		SmsQuoteDtlDM save = new SmsQuoteDtlDM();
		if (tblsmsQuoteDtl.getValue() != null) {
			save = beansmsQuoteDtl.getItem(tblsmsQuoteDtl.getValue()).getBean();
			smsQuoteDtlList.remove(save);
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
		flColumn1.addComponent(cbQuotationType);
		flColumn1.addComponent(tfQuoteNumber);
		flColumn1.addComponent(tfQuoteRef);
		flColumn1.addComponent(cbwindTechPers);
		flColumn1.addComponent(cbwindcommPerson);
		flColumn1.addComponent(tfQuoteVersion);
		flColumn2.addComponent(dfQuoteDt);
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
		/*
		 * HorizontalLayout hed = new HorizontalLayout(); hed.addComponent(tfHEDPer); hed.addComponent(tfHEDValue);
		 * hed.setCaption("HED"); flColumn2.addComponent(hed); flColumn2.setComponentAlignment(hed, Alignment.TOP_LEFT);
		 * HorizontalLayout cess = new HorizontalLayout(); cess.addComponent(tfCessPer); cess.addComponent(tfCessValue);
		 * cess.setCaption("CESS"); flColumn2.addComponent(cess); flColumn2.setComponentAlignment(cess,
		 * Alignment.TOP_LEFT);
		 */
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
		flColumn3.addComponent(tfFreightValue);
		flColumn3.addComponent(tfOtherValue);
		flColumn3.addComponent(tfDocumentCharges);
		flColumn3.addComponent(tfPDCCharges);
		flColumn3.addComponent(tfGrandtotal);
		flColumn3.addComponent(cbpaymetTerms);
		flColumn3.addComponent(cbFreightTerms);
		flColumn4.addComponent(cbWarrentyTerms);
		flColumn4.addComponent(cbDelTerms);
		flColumn4.addComponent(tfLiquidatedDamage);
		flColumn4.addComponent(cbStatus);
		flColumn4.addComponent(chkDutyExe);
		flColumn4.addComponent(chkCformReq);
		flColumn4.addComponent(ckPdcRqu);
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
		flDtlColumn1.addComponent(cbproduct);
		flDtlColumn1.addComponent(tfQuoteQunt);
		flDtlColumn1.addComponent(tfUnitRate);
		flDtlColumn2.addComponent(tfBasicValue);
		flDtlColumn2.addComponent(tfCustomField1);
		flDtlColumn2.addComponent(tfCustomField2);
		flDtlColumn3.addComponent(tacustproddesc);
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
		vlSmsQuoteHDR.addComponent(tblsmsQuoteDtl);
		vlSmsQuoteHDR.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlSmsQuoteHDR, "Sales Quote Detail");
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
		List<SmsQuoteHdrDM> smsQuoteHdrList = new ArrayList<SmsQuoteHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbBranch.getValue() + ", " + cbStatus.getValue());
		String eno = null;
		if (cbEnqNo.getValue() != null) {
			eno = (((SmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
		}
		smsQuoteHdrList = servicesmsQuoteHdr.getSmsQuoteHdrList(companyid, null, (Long) cbBranch.getValue(),
				(String) cbStatus.getValue(), (String) tfQuoteNumber.getValue(), eno, "F", null);
		recordCnt = smsQuoteHdrList.size();
		beansmsQuoteHdr = new BeanItemContainer<SmsQuoteHdrDM>(SmsQuoteHdrDM.class);
		beansmsQuoteHdr.addAll(smsQuoteHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
		tblMstScrSrchRslt.setContainerDataSource(beansmsQuoteHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "quoteId", "branchName", "quoteNumber", "enquiryNo",
				"status", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Quote No", "Enquiry No", "Status",
				"Last Updated Date", "Last Updated By" });
		// tblMstScrSrchRslt.setColumnAlignment("quoteId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadQuotationDetailList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblsmsQuoteDtl.setPageLength(3);
			recordCnt = smsQuoteDtlList.size();
			beansmsQuoteDtl = new BeanItemContainer<SmsQuoteDtlDM>(SmsQuoteDtlDM.class);
			beansmsQuoteDtl.addAll(smsQuoteDtlList);
			BigDecimal sum = new BigDecimal("0");
			for (SmsQuoteDtlDM obj : smsQuoteDtlList) {
				if (obj.getBasicvalue() != null) {
					sum = sum.add(obj.getBasicvalue());
				}
			}
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(sum.toString());
			tfBasictotal.setReadOnly(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxslap. result set");
			tblsmsQuoteDtl.setContainerDataSource(beansmsQuoteDtl);
			tblsmsQuoteDtl.setVisibleColumns(new Object[] { "prodname", "custproddesc","quoteqty",
					"unitrate", "basicvalue", "customField1", "quodtlstatus", "lastupdateddt", "lastupdatedby" });
			tblsmsQuoteDtl.setColumnHeaders(new String[] { "Product Name", "Description","Quote Qty",
					"Unit Rate", "Basic Value", "Part No.", "Status", "Last Updated Date", "Last Updated By" });
			tblsmsQuoteDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load BranchList
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
	
	// Load Uom List
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
	
	// Load EnquiryNo
	private void loadEnquiryNo() {
		List<SmsEnqHdrDM> getsmsEnqNoHdr = new ArrayList<SmsEnqHdrDM>();
		getsmsEnqNoHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, null, null, null, null, "F", null, null));
		BeanItemContainer<SmsEnqHdrDM> beansmsenqHdr = new BeanItemContainer<SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.addAll(getsmsEnqNoHdr);
		cbEnqNo.setContainerDataSource(beansmsenqHdr);
	}
	
	// Load PaymentTerms
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
	
	// Load FreightTerms
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
	
	// Load WarrentyTerms
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
	
	// Load DeliveryTerms
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
	
	// Reset the selected row's data into Sales Quote Hdr input components
	private void editQuoteHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected QuoteId -> "
				+ quoteId);
		if (sltedRcd != null) {
			SmsQuoteHdrDM editsmsQuotlist = beansmsQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			quoteId = editsmsQuotlist.getQuoteId();
			cbBranch.setValue(editsmsQuotlist.getBranchId());
			logger.info("sms salaes enquiry no" + editsmsQuotlist.getEnquiryNo());
			cbEnqNo.setValue(editsmsQuotlist.getEnquiryNo());
			cbQuotationType.setValue(editsmsQuotlist.getQuotationType());
			tfQuoteNumber.setReadOnly(false);
			tfQuoteNumber.setValue(editsmsQuotlist.getQuoteNumber());
			tfQuoteNumber.setReadOnly(true);
			tfQuoteRef.setValue(editsmsQuotlist.getQuoteRef());
			dfQuoteDt.setValue(editsmsQuotlist.getQuoteDate());
			dfvalidDt.setValue(editsmsQuotlist.getQuoteValDate());
			tfQuoteVersion.setValue(editsmsQuotlist.getQuoteVersion());
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(editsmsQuotlist.getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfpackingPer.setValue(editsmsQuotlist.getPackingPrcnt().toString());
			tfPaclingValue.setReadOnly(false);
			tfPaclingValue.setValue(editsmsQuotlist.getPackingValue().toString());
			tfPaclingValue.setReadOnly(true);
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(editsmsQuotlist.getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(editsmsQuotlist.getVatPrcnt().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(editsmsQuotlist.getVatValue().toString());
			tfVatValue.setReadOnly(true);
			tfEDPer.setValue(editsmsQuotlist.getEd_Prcnt().toString());
			tfEDValue.setReadOnly(false);
			tfEDValue.setValue(editsmsQuotlist.getEdValue().toString());
			tfEDValue.setReadOnly(true);
			tfHEDPer.setValue(editsmsQuotlist.getHedPrcnt().toString());
			tfHEDValue.setReadOnly(false);
			tfHEDValue.setValue(editsmsQuotlist.getHedValue().toString());
			tfHEDValue.setReadOnly(true);
			tfCessPer.setValue(editsmsQuotlist.getCessPrcnt().toString());
			tfCessValue.setReadOnly(false);
			tfCessValue.setValue(editsmsQuotlist.getCessValue().toString());
			tfCessValue.setReadOnly(true);
			tfCstPer.setValue(editsmsQuotlist.getCessPrcnt().toString());
			tfCstValue.setReadOnly(false);
			tfCstValue.setValue(editsmsQuotlist.getCessValue().toString());
			tfCstValue.setReadOnly(true);
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(editsmsQuotlist.getSubTaxTotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			tfFreightValue.setValue(editsmsQuotlist.getFreightValue().toString());
			tfOtherValue.setValue((editsmsQuotlist.getOthersValue().toString()));
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(editsmsQuotlist.getGrandTotal().toString());
			tfGrandtotal.setReadOnly(true);
			tfDocumentCharges.setReadOnly(false);
			if (editsmsQuotlist.getDocumentCharges() != null) {
				tfDocumentCharges.setValue(editsmsQuotlist.getDocumentCharges().toString());
			} else {
				tfDocumentCharges.setValue("0");
			}
			if (editsmsQuotlist.getPdcCharges() != null) {
				tfPDCCharges.setReadOnly(false);
				tfPDCCharges.setValue(editsmsQuotlist.getPdcCharges().toString());
			} else {
				tfPDCCharges.setReadOnly(false);
				tfPDCCharges.setValue("0");
			}
			if (editsmsQuotlist.getPaymentTerms() != null) {
				cbpaymetTerms.setValue(editsmsQuotlist.getPaymentTerms().toString());
			}
			if (editsmsQuotlist.getFreightTerms() != null) {
				cbFreightTerms.setValue(editsmsQuotlist.getFreightTerms());
			}
			if (editsmsQuotlist.getWarrantyTerms() != null) {
				cbWarrentyTerms.setValue(editsmsQuotlist.getWarrantyTerms());
			}
			if (editsmsQuotlist.getDeliveryTerms() != null) {
				cbDelTerms.setValue(editsmsQuotlist.getDeliveryTerms());
			}
			if (editsmsQuotlist.getLiqdatedDamage() != null) {
				tfLiquidatedDamage.setValue(editsmsQuotlist.getLiqdatedDamage());
			}
			if (editsmsQuotlist.getStatus() != null) {
				cbStatus.setValue(editsmsQuotlist.getStatus().toString());
			}
			if (editsmsQuotlist.getDutyExempted().equals("Y")) {
				chkDutyExe.setValue(true);
			} else {
				chkDutyExe.setValue(false);
			}
			if (editsmsQuotlist.getCformReqd().equals("Y")) {
				chkCformReq.setValue(true);
			} else {
				chkCformReq.setValue(false);
			}
			if (editsmsQuotlist.getPdcReqd().equals("Y")) {
				ckPdcRqu.setValue(true);
			} else {
				ckPdcRqu.setValue(false);
			}
			Long uom = editsmsQuotlist.getEnquiryId();
			Collection<?> uomid = cbEnqNo.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				// Get the actual bean and use the data
				SmsEnqHdrDM st = (SmsEnqHdrDM) item.getBean();
				if (uom != null && uom.equals(st.getEnquiryId())) {
					cbEnqNo.setValue(itemId);
				}
			}
			if (editsmsQuotlist.getCpmPerson() != null) {
				cbwindcommPerson.setValue(editsmsQuotlist.getCpmPerson());
			}
			if (editsmsQuotlist.getTecPerson() != null) {
				cbwindTechPers.setValue(editsmsQuotlist.getTecPerson());
			}
			if (sltedRcd.getItemProperty("quoteDoc").getValue() != null) {
				byte[] certificate = (byte[]) sltedRcd.getItemProperty("quoteDoc").getValue();
				UploadDocumentUI test = new UploadDocumentUI(hlquoteDoc);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(hlquoteDoc);
			}
			smsQuoteDtlList = servicesmseQuoteDtl.getsmsquotedtllist(null, quoteId, null, null);
		}
		loadQuotationDetailList();
		comments = new SmsComments(vlTableForm, null, null, null, null, null, null, null, null, quoteId, null, null,
				status);
		comments.loadsrch(true, null, null, null, null, null, null, null, null, quoteId, null, null, null);
		/*
		 * comments.commentList = serviceComment.getSmsCommentsList(null, null, null, QuoteId, null, null, null, null,
		 * null, null, null, null);
		 */
		System.out.println("QuoteId===>" + quoteId);
	}
	
	// Reset the selected row's data into Sales Quote Detail input components
	private void editQuoteDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		Item sltedRcd = tblsmsQuoteDtl.getItem(tblsmsQuoteDtl.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected QuoteId -> "
				+ quoteId);
		if (sltedRcd != null) {
			SmsQuoteDtlDM editsmsQuoteDtllist = beansmsQuoteDtl.getItem(tblsmsQuoteDtl.getValue()).getBean();
			// QuoteId = editsmsQuoteDtllist.getQuoteid();
			Long uom = editsmsQuoteDtllist.getProductid();
			Collection<?> uomid = cbproduct.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbproduct.getItem(itemId);
				// Get the actual bean and use the data
				// ProductDM st = (ProductDM) item.getBean();
				SmsEnquiryDtlDM st = (SmsEnquiryDtlDM) item.getBean();
				if (uom != null && uom.equals(st.getProductid())) {
					cbproduct.setValue(itemId);
				}
			}
			if (editsmsQuoteDtllist.getQuoteqty() != null) {
				tfQuoteQunt.setReadOnly(false);
				tfQuoteQunt.setValue(editsmsQuoteDtllist.getQuoteqty().toString());
				tfQuoteQunt.setReadOnly(true);
			}
			if (editsmsQuoteDtllist.getUnitrate() != null) {
				tfUnitRate.setValue(editsmsQuoteDtllist.getUnitrate().toString());
			}
			if (editsmsQuoteDtllist.getProduom() != null) {
				cbUom.setValue(editsmsQuoteDtllist.getProduom());
			}
			if (editsmsQuoteDtllist.getQuodtlstatus() != null) {
				cbdtlstatus.setValue(editsmsQuoteDtllist.getQuodtlstatus().toString());
			}
			if (editsmsQuoteDtllist.getBasicvalue() != null) {
				tfBasicValue.setReadOnly(false);
				tfBasicValue.setValue(editsmsQuoteDtllist.getBasicvalue().toString());
				tfBasicValue.setReadOnly(true);
			}
			if (editsmsQuoteDtllist.getCustprodcode() != null) {
				tfcustprodcode.setValue(editsmsQuoteDtllist.getCustprodcode().toString());
			}
			if (editsmsQuoteDtllist.getCustproddesc() != null) {
				tacustproddesc.setValue(editsmsQuoteDtllist.getCustproddesc());
			}
			if (editsmsQuoteDtllist.getCustomField1() != null) {
				tfCustomField1.setValue(editsmsQuoteDtllist.getCustomField1());
			}
			if (editsmsQuoteDtllist.getCustomField2() != null) {
				tfCustomField2.setValue(editsmsQuoteDtllist.getCustomField2());
			}
		}
	}
	
	protected void quoteDtlresetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbproduct.setValue(null);
		cbproduct.setComponentError(null);
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
		tfCustomField1.setValue("");
		tfCustomField2.setValue("");
		cbdtlstatus.setValue(cbdtlstatus.getItemIds().iterator().next());
		cbdtlstatus.setComponentError(null);
		tfUnitRate.setValue("");
		tfUnitRate.setComponentError(null);
		cbEnqNo.setRequired(true);
	}
	
	// Calculated Values for Sales Quote Hdr validation
	private void getCalculatedValues() {
		if (chkCformReq.getValue()) {
			tfVatPer.setValue("0");
			try {
				/*
				 * tfCstPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CST", "Active", "F").get(0)
				 * .getTaxprnct().toString());
				 */
			}
			catch (Exception e) {
				e.printStackTrace();
				tfCstPer.setValue("0");
			}
		} else {
			tfCstPer.setValue("0");
			try {
				/*
				 * tfVatPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "VAT", "Active", "F").get(0)
				 * .getTaxprnct().toString());
				 */
			}
			catch (Exception e) {
				e.printStackTrace();
				tfVatPer.setValue("0");
			}
		}
		if (chkDutyExe.getValue()) {
			tfEDPer.setValue("0");
			tfHEDPer.setValue("0");
			tfCessPer.setValue("0");
		} else {
			try {
				/*
				 * tfHEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "HED", "Active", "F").get(0)
				 * .getTaxprnct().toString());
				 */
			}
			catch (Exception e) {
				tfHEDPer.setValue("0");
			}
			try {
				/*
				 * tfEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "ED", "Active", "F").get(0)
				 * .getTaxprnct().toString());
				 */
			}
			catch (Exception e) {
				tfEDPer.setValue("0");
			}
			try {
				/*
				 * tfCessPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CESS", "Active", "F").get(0)
				 * .getTaxprnct().toString());
				 */
			}
			catch (Exception e) {
				tfCessPer.setValue("0");
			}
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
			List<SmsEnquiryDtlDM> getQuoteDtl = new ArrayList<SmsEnquiryDtlDM>();
		
				Long enquid = ((SmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId();
				getQuoteDtl.addAll(serviceEnqDetail.getsmsenquirydtllist(null, enquid, null, null, null, null));
			
			BeanItemContainer<SmsEnquiryDtlDM> beanPlnDtl = new BeanItemContainer<SmsEnquiryDtlDM>(
					SmsEnquiryDtlDM.class);
			beanPlnDtl.addAll(getQuoteDtl);
			cbproduct.setContainerDataSource(beanPlnDtl);
		}
		catch (Exception e) {
		}
		// cbproduct.setItemCaptionPropertyId("pordName");
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
		cbproduct.setRequired(true);
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
		tblsmsQuoteDtl.setVisible(true);
		assembleInputUserLayout();
		loadQuotationDetailList();
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, moduleId, "SM_QUOTENO").get(0);
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
		cbproduct.setRequired(true);
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
		if (tblsmsQuoteDtl.size() == 0) {
			cbproduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + cbBranch.getValue() + "," + cbEnqNo.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Validation for Sales Quote Details
	private boolean DtlValidation() {
		boolean isValid = true;
		if (cbproduct.getValue() == null) {
			cbproduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			cbproduct.setComponentError(null);
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
			SmsQuoteHdrDM salesQuoteHdrobj = new SmsQuoteHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				salesQuoteHdrobj = beansmsQuoteHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			salesQuoteHdrobj.setQuoteNumber(tfQuoteNumber.getValue());
			salesQuoteHdrobj.setQuoteRef(tfQuoteRef.getValue());
			salesQuoteHdrobj.setBranchId((Long) cbBranch.getValue());
			salesQuoteHdrobj.setQuotationType((String) cbQuotationType.getValue());
			salesQuoteHdrobj.setCompanyId(companyid);
			if (cbEnqNo.getValue() != null) {
				salesQuoteHdrobj.setEnquiryNo(((SmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryNo());
				salesQuoteHdrobj.setEnquiryId(((SmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId());
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
			salesQuoteHdrobj.setFreightValue(new BigDecimal(tfFreightValue.getValue()));
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
			if (tfLiquidatedDamage.getValue() != null) {
				salesQuoteHdrobj.setLiqdatedDamage(tfLiquidatedDamage.getValue());
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
			salesQuoteHdrobj.setPreparedBy(EmployeeId);
			salesQuoteHdrobj.setReviewedBy(null);
			salesQuoteHdrobj.setActionedBy(null);
			salesQuoteHdrobj.setLastupdateddt(DateUtils.getcurrentdate());
			salesQuoteHdrobj.setLastupdatedby(username);
			if (cbwindTechPers.getValue() != null) {
				salesQuoteHdrobj.setTecPerson(cbwindTechPers.getValue().toString());
			}
			if (cbwindcommPerson.getValue() != null) {
				salesQuoteHdrobj.setCpmPerson(cbwindcommPerson.getValue().toString());
			}
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			salesQuoteHdrobj.setQuoteDoc(fileContents);
			DtlValidation();
			servicesmsQuoteHdr.saveOrUpdateSmsQuoteHdr(salesQuoteHdrobj);
			@SuppressWarnings("unchecked")
			Collection<SmsQuoteDtlDM> itemIds = (Collection<SmsQuoteDtlDM>) tblsmsQuoteDtl.getVisibleItemIds();
			for (SmsQuoteDtlDM save : (Collection<SmsQuoteDtlDM>) itemIds) {
				save.setQuoteid(Long.valueOf(salesQuoteHdrobj.getQuoteId()));
				servicesmseQuoteDtl.saveOrUpdatesmsquotedtlDetails(save);
			}
			tfQuoteNumber.setReadOnly(false);
			tfQuoteNumber.setValue(salesQuoteHdrobj.getQuoteNumber());
			tfQuoteNumber.setReadOnly(true);
			comments.saveQuote(salesQuoteHdrobj.getQuoteId(), salesQuoteHdrobj.getStatus());
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, moduleId, "SM_QUOTENO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_QUOTENO");
						System.out.println("Serial no=>" + companyid + "," + moduleId + "," + branchId);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			// comments.saveSaleQuote(salesQuoteHdrobj.getQuoteId(),salesQuoteHdrobj.getStatus());
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
			SmsQuoteDtlDM smsQuotDtlobj = new SmsQuoteDtlDM();
			if (tblsmsQuoteDtl.getValue() != null) {
				smsQuotDtlobj = beansmsQuoteDtl.getItem(tblsmsQuoteDtl.getValue()).getBean();
				smsQuoteDtlList.remove(smsQuotDtlobj);
			} /*
			 * else { int count = 0; for (SmsQuoteDtlDM SmsQuoteDtlDM : smsQuoteDtlList) { if
			 * (SmsQuoteDtlDM.getProductid() == ((SmsEnquiryDtlDM) cbproduct.getValue()).getProductid()) { count++;
			 * break; } } System.out.println("count--->" + count); if (count == 0) { } else {
			 * cbproduct.setComponentError(new UserError("Product Already Exist..")); } }
			 */
			smsQuotDtlobj.setProductid(((SmsEnquiryDtlDM) cbproduct.getValue()).getProductid());
			smsQuotDtlobj.setProdname(((SmsEnquiryDtlDM) cbproduct.getValue()).getProdname());
			if (tfQuoteQunt.getValue() != null && tfQuoteQunt.getValue().trim().length() > 0) {
				tfQuoteQunt.setReadOnly(false);
				smsQuotDtlobj.setQuoteqty(Long.valueOf(tfQuoteQunt.getValue()));
				tfQuoteQunt.setReadOnly(true);
			}
			if (tfUnitRate.getValue() != null && tfUnitRate.getValue().trim().length() > 0) {
				smsQuotDtlobj.setUnitrate(new BigDecimal(tfUnitRate.getValue()));
			}
			// smsQuotDtlobj.setProduom(cbUom.getValue().toString());
			if (tfcustprodcode.getValue() != null && tfcustprodcode.getValue().trim().length() > 0) {
				smsQuotDtlobj.setCustprodcode((String.valueOf(tfcustprodcode.getValue())));
			}
			smsQuotDtlobj.setQuodtlstatus(cbdtlstatus.getValue().toString());
			// smsQuotDtlobj.setProduom(cbUom.getValue().toString());
			if (tfBasicValue.getValue() != null && tfBasicValue.getValue().trim().length() > 0) {
				smsQuotDtlobj.setBasicvalue(new BigDecimal(tfBasicValue.getValue()));
			}
			smsQuotDtlobj.setCustproddesc(tacustproddesc.getValue());
			smsQuotDtlobj.setCustomField1(tfCustomField1.getValue());
			smsQuotDtlobj.setCustomField2(tfCustomField2.getValue());
			smsQuotDtlobj.setLastupdateddt(DateUtils.getcurrentdate());
			smsQuotDtlobj.setLastupdatedby(username);
			smsQuoteDtlList.add(smsQuotDtlobj);
			loadQuotationDetailList();
			getCalculatedValues();
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
		cbproduct.setRequired(false);
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
		cbwindcommPerson.setValue(null);
		cbwindTechPers.setValue(null);
		chkCformReq.setValue(false);
		try {
			tfCstPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CST", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCstPer.setValue("0");
		}
		cbwindcommPerson.setContainerDataSource(null);
		cbwindTechPers.setContainerDataSource(null);
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
		cbFreightTerms.setValue(null);
		cbStatus.setValue(null);
		cbBranch.setValue(null);
		cbQuotationType.setValue("Enquiry");
		dfQuoteDt.setValue(new Date());
		dfvalidDt.setValue(DateUtils.addDays(new Date(), 30));
		cbBranch.setComponentError(null);
		cbEnqNo.setComponentError(null);
		dfvalidDt.setComponentError(null);
		tfLiquidatedDamage.setValue("");
		cbDelTerms.setValue(null);
		cbpaymetTerms.setValue(null);
		cbWarrentyTerms.setValue(null);
		cbFreightTerms.setValue(null);
		smsQuoteDtlList = new ArrayList<SmsQuoteDtlDM>();
		tblsmsQuoteDtl.removeAllItems();
		new UploadDocumentUI(hlquoteDoc);
		cbStatus.setValue(null);
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
			// file.
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
	
	private void loadclienTecCont() {
		try {
			Long enquid = ((SmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId();
			Long clientId = serviceEnqHeader.getSmsEnqHdrList(companyid, enquid, null, null, null, "F", null, null)
					.get(0).getClientId();
			List<ClientsContactsDM> listclientconDtls = serviceClntContact.getClientContactsDetails(companyid, null,
					clientId, null, "Active", "Technical Person");
			beanclientcontact = new BeanContainer<Long, ClientsContactsDM>(ClientsContactsDM.class);
			beanclientcontact.setBeanIdProperty("contactName");
			beanclientcontact.addAll(listclientconDtls);
			cbwindTechPers.setContainerDataSource(beanclientcontact);
			cbwindTechPers.setItemCaptionPropertyId("contactName");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadclientCommCont() {
		try {
			Long enquid = ((SmsEnqHdrDM) cbEnqNo.getValue()).getEnquiryId();
			Long clientId = serviceEnqHeader.getSmsEnqHdrList(companyid, enquid, null, null, null, "F", null, null)
					.get(0).getClientId();
			List<ClientsContactsDM> listclientconDtls = serviceClntContact.getClientContactsDetails(companyid, null,
					clientId, null, "Active", "Contact Person");
			beanclientcontact = new BeanContainer<Long, ClientsContactsDM>(ClientsContactsDM.class);
			beanclientcontact.setBeanIdProperty("contactName");
			beanclientcontact.addAll(listclientconDtls);
			cbwindcommPerson.setContainerDataSource(beanclientcontact);
			cbwindcommPerson.setItemCaptionPropertyId("contactName");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
