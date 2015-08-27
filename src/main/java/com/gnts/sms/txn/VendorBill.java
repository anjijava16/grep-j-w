/**
 * File Name 		: VendorBill.java 
 * Description 		:This Screen Purpose for Modify the VendorBill Details.
 * 					Add the PurchasePO details process should be directly added in DB.
 * Author 			: Ganga 
 * Date 			: Oct 01, 2014

 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Oct 01, 2014       GANGA                  Initial  Version
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
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
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
import com.gnts.sms.domain.txn.PurchasePODtlDM;
import com.gnts.sms.domain.txn.PurchasePOHdrDM;
import com.gnts.sms.domain.txn.VendorBillDtlDM;
import com.gnts.sms.domain.txn.VendorBillHdrDM;
import com.gnts.sms.service.mst.SmsTaxesService;
import com.gnts.sms.service.txn.PurchasePODtlService;
import com.gnts.sms.service.txn.PurchasePOHdrService;
import com.gnts.sms.service.txn.VendorBillDtlService;
import com.gnts.sms.service.txn.VendorBillHdrService;
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

public class VendorBill extends BaseTransUI {
	private VendorBillHdrService servicevendorBillHdr = (VendorBillHdrService) SpringContextHelper
			.getBean("vendorbillheader");
	private VendorBillDtlService servicevendorBillDtl = (VendorBillDtlService) SpringContextHelper
			.getBean("vendorbillDtl");
	private PurchasePOHdrService servicepurchaePOHdr = (PurchasePOHdrService) SpringContextHelper
			.getBean("PurchasePOhdr");
	private PurchasePODtlService servicePurchasePODtl = (PurchasePODtlService) SpringContextHelper
			.getBean("PurchasePODtl");
	private SmsTaxesService serviceTaxesSms = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// form layout for input controls for Vendor Bill Header
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// form layout for input controls for Vendor Bill Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4, flDtlColumn5;
	// // User Input Components for Vendor Bill Details
	private ComboBox cbBranch, cbpoNo, cbStatus;
	private TextField tfFreightTerms, tfWarrentyTerms, tfDelTerms;
	private TextField tfPamentTerm, tfbillNo, tfBasictotal, tfpackingPer, tfPaclingValue;
	private TextField tfSubTotal, tfVatPer, tfVatValue, tfEDPer, tfEDValue, tfHEDPer;
	private TextField tfHEDValue, tfCessPer, tfCessValue, tfCstPer, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightPer, tfFreightValue, tfOtherPer, tfOtherValue, tfGrandtotal;
	private TextArea taRemark;
	private CheckBox ckdutyexm, ckPdcRqu, ckCformRqu;
	private PopupDateField dfbillDt;
	private Button btnAdd = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlPODoc = new VerticalLayout();
	private VerticalLayout hldtlDoc = new VerticalLayout();
	// VendorBillDtl components
	private ComboBox cbproduct, cbMatUom, cbDtlStatus;
	private TextField tfreceiptQnty, tfrejectQty, tfUnitRate, tfBasicValue, tfDebitValue;
	private TextArea tarejectReason;
	private CheckBox ckdebitNote;
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private BeanItemContainer<VendorBillHdrDM> beanVendorBillHdr = null;
	private BeanItemContainer<VendorBillDtlDM> beanVendorBillDtl = null;
	private List<VendorBillDtlDM> vendorDtlList = new ArrayList<VendorBillDtlDM>();
	// local variables declaration
	private String username;
	private Long companyid;
	private int recordCnt;
	private Long employeeId;
	private Long roleId;
	private Long branchId;
	private Long billId;
	private String poid;
	private SmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private GERPTable tblVendorBillDtl;
	private Long screenId;
	private String status;
	private Button btndelete = new GERPButton("Delete", "delete", this);
	// Initialize logger
	private Logger logger = Logger.getLogger(VendorBill.class);
	
	// Constructor received the parameters from Login UI class
	public VendorBill() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside VendorBill() constructor");
		// Loading the VendorBill UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax UI");
		// Initialization for VendorBill Details user input components
		cbpoNo = new GERPComboBox("PO No.");
		cbpoNo.setItemCaptionPropertyId("pono");
		cbpoNo.setImmediate(true);
		loadPoNo();
		cbpoNo.addValueChangeListener(new Property.ValueChangeListener() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbpoNo.getItem(itemId);
				if (item != null) {
					loadProduct();
					loadVendorDtl();
					tfPamentTerm.setValue(((PurchasePOHdrDM) cbpoNo.getValue()).getPaymentTerms());
					tfFreightTerms.setValue(((PurchasePOHdrDM) cbpoNo.getValue()).getFrnghtTerms());
					tfWarrentyTerms.setValue(((PurchasePOHdrDM) cbpoNo.getValue()).getWrntyTerms());
					tfDelTerms.setValue(((PurchasePOHdrDM) cbpoNo.getValue()).getDlvryTerms());
					if (((PurchasePOHdrDM) cbpoNo.getValue()).getDutyExempt().equals("Y")) {
						ckdutyexm.setValue(true);
					} else {
						ckdutyexm.setValue(false);
					}
					if (((PurchasePOHdrDM) cbpoNo.getValue()).getCformReqd().equals("Y")) {
						ckCformRqu.setValue(true);
					} else {
						ckCformRqu.setValue(false);
					}
					if (((PurchasePOHdrDM) cbpoNo.getValue()).getpDCReqd().equals("Y")) {
						ckPdcRqu.setValue(true);
					} else {
						ckPdcRqu.setValue(false);
					}
				}
			}
		});
		dfbillDt = new GERPPopupDateField("Bill Date");
		dfbillDt.setInputPrompt("Select Date");
		dfbillDt.setWidth("130");
		tfbillNo = new TextField("Bill No");
		tfbillNo.setWidth("150");
		taRemark = new TextArea("Remarks");
		taRemark.setHeight("30");
		taRemark.setWidth("150");
		tfBasictotal = new TextField("Basic total");
		tfBasictotal.setWidth("150");
		tfpackingPer = new TextField();
		tfpackingPer.setWidth("30");
		tfPaclingValue = new TextField();
		tfPaclingValue.addStyleName("rightalign");
		tfPaclingValue.setWidth("120");
		tfSubTotal = new TextField("Sub Total");
		tfSubTotal.setWidth("145");
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
		tfSubTaxTotal.setWidth("145");
		tfFreightPer = new TextField();
		tfFreightPer.setWidth("30");
		tfFreightValue = new TextField();
		tfFreightValue.setWidth("120");
		tfOtherValue = new TextField();
		tfOtherValue.setWidth("120");
		tfGrandtotal = new TextField("Grand Total");
		tfGrandtotal.setWidth("145");
		tfOtherPer = new TextField();
		tfOtherPer.setWidth("30");
		tfPamentTerm = new TextField("Payment Terms");
		tfPamentTerm.setWidth("150");
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
		try {
			ApprovalSchemaDM obj = servicevendorBillHdr.getReviewerId(companyid, screenId, branchId, roleId).get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_VENDOR_BILL_HDR, BASEConstants.VBR_STATUS);
			} else {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_VENDOR_BILL_HDR, BASEConstants.VB_STATUS);
			}
		}
		catch (Exception e) {
		}
		cbStatus.setWidth("120");
		// VendorBill Detail Comp
		cbproduct = new ComboBox("Product Name");
		cbproduct.setItemCaptionPropertyId("productName");
		cbproduct.setWidth("150");
		cbproduct.setImmediate(true);
		cbproduct.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbproduct.getValue() != null) {
					cbMatUom.setReadOnly(false);
					cbMatUom.setValue(((PurchasePODtlDM) cbproduct.getValue()).getMaterialUom());
					tfreceiptQnty.setReadOnly(false);
					tfreceiptQnty.setValue(((PurchasePODtlDM) cbproduct.getValue()).getPoQty() + "");
					tfUnitRate.setReadOnly(false);
					tfUnitRate.setValue(((PurchasePODtlDM) cbproduct.getValue()).getUnitRate() + "");
					tfBasicValue.setReadOnly(false);
					tfBasicValue.setValue(((PurchasePODtlDM) cbproduct.getValue()).getBasicValue() + "");
				}
			}
		});
		cbMatUom = new ComboBox("Material Uom");
		cbMatUom.setItemCaptionPropertyId("lookupname");
		cbMatUom.setWidth("150");
		loadUomList();
		tfreceiptQnty = new TextField("Receipt Qty");
		tfreceiptQnty.setValue("0");
		tfreceiptQnty.setWidth("150");
		tfrejectQty = new TextField("Reject Qty");
		tfrejectQty.setValue("0");
		tfrejectQty.setWidth("150");
		tfDebitValue = new TextField("Debit Value");
		tfUnitRate = new TextField("Unit Rate");
		tfUnitRate.setWidth("150");
		tfUnitRate.setValue("0");
		tfBasicValue = new TextField("Basic value");
		tfBasicValue.setWidth("150");
		tfBasicValue.setValue("0");
		tarejectReason = new TextArea("Reject Reason");
		tarejectReason.setWidth("150");
		tarejectReason.setHeight("30");
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_PO_RECEIPTS_DTL, BASEConstants.RP_STATUS);
		cbDtlStatus.setWidth("130");
		cbDtlStatus.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbDtlStatus.getValue() != null) {
					if (cbDtlStatus.getValue().equals("Rejected")) {
						tarejectReason.setEnabled(true);
						tarejectReason.setRequired(true);
					} else if (cbDtlStatus.getValue().equals("Accepted")) {
						tarejectReason.setEnabled(false);
					}
				}
			}
		});
		ckdebitNote = new CheckBox("Debit Note");
		btnAdd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateBillDetail()) {
					saveVendorBillDetails();
				}
			}
		});
		btndelete.setEnabled(false);
		tblVendorBillDtl = new GERPTable();
		tblVendorBillDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblVendorBillDtl.isSelected(event.getItemId())) {
					tblVendorBillDtl.setImmediate(true);
					btnAdd.setCaption("Add");
					btnAdd.setStyleName("savebt");
					btndelete.setEnabled(false);
					vendorBillResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAdd.setCaption("Update");
					btnAdd.setStyleName("savebt");
					btndelete.setEnabled(true);
					editVendorBillDtl();
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
		loadVendorDtl();
		btnAdd.setStyleName("add");
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
		flColumn2.addComponent(tfbillNo);
		flColumn3.addComponent(cbpoNo);
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
		// Formlayout1 components
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbBranch);
		flColumn1.addComponent(cbpoNo);
		flColumn1.addComponent(tfbillNo);
		flColumn1.addComponent(dfbillDt);
		flColumn1.addComponent(taRemark);
		flColumn1.addComponent(tfBasictotal);
		HorizontalLayout pv = new HorizontalLayout();
		pv.addComponent(tfpackingPer);
		pv.addComponent(tfPaclingValue);
		pv.setCaption("Packing");
		flColumn1.addComponent(pv);
		flColumn1.setComponentAlignment(pv, Alignment.TOP_LEFT);
		// Formlayout2 components
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
		flColumn2.addComponent(cess);
		flColumn2.setComponentAlignment(cess, Alignment.TOP_LEFT);
		HorizontalLayout cst = new HorizontalLayout();
		cst.addComponent(tfCstPer);
		cst.addComponent(tfCstValue);
		cst.setCaption("CST");
		flColumn2.addComponent(cst);
		flColumn2.setComponentAlignment(cst, Alignment.TOP_LEFT);
		flColumn2.addComponent(tfSubTaxTotal);
		HorizontalLayout frgt = new HorizontalLayout();
		frgt.addComponent(tfFreightPer);
		frgt.addComponent(tfFreightValue);
		frgt.setCaption("Freight");
		// Formlayout3 components
		flColumn3.addComponent(frgt);
		flColumn3.setComponentAlignment(frgt, Alignment.TOP_LEFT);
		HorizontalLayout other = new HorizontalLayout();
		other.addComponent(tfOtherPer);
		other.addComponent(tfOtherValue);
		other.setCaption("Other(%)");
		flColumn3.addComponent(other);
		flColumn3.setComponentAlignment(other, Alignment.TOP_LEFT);
		flColumn3.addComponent(tfGrandtotal);
		flColumn3.addComponent(tfPamentTerm);
		flColumn3.addComponent(tfFreightTerms);
		flColumn3.addComponent(tfWarrentyTerms);
		flColumn3.addComponent(tfDelTerms);
		// Formlayout4 components
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
		// Adding VendorBillDtl components
		// Add components for User Input Layout
		flDtlColumn1 = new FormLayout();
		flDtlColumn2 = new FormLayout();
		flDtlColumn3 = new FormLayout();
		flDtlColumn4 = new FormLayout();
		flDtlColumn5 = new FormLayout();
		flDtlColumn1.addComponent(cbproduct);
		flDtlColumn1.addComponent(cbMatUom);
		flDtlColumn1.addComponent(tfreceiptQnty);
		flDtlColumn2.addComponent(tfrejectQty);
		flDtlColumn2.addComponent(tfUnitRate);
		flDtlColumn2.addComponent(tfBasicValue);
		flDtlColumn3.addComponent(tfDebitValue);
		flDtlColumn3.addComponent(tarejectReason);
		flDtlColumn3.addComponent(ckdebitNote);
		flDtlColumn4.addComponent(cbDtlStatus);
		flDtlColumn4.addComponent(hldtlDoc);
		flDtlColumn5.addComponent(btnAdd);
		flDtlColumn5.addComponent(btndelete);
		HorizontalLayout hlVendorBillDtl = new HorizontalLayout();
		hlVendorBillDtl.addComponent(flDtlColumn1);
		hlVendorBillDtl.addComponent(flDtlColumn2);
		hlVendorBillDtl.addComponent(flDtlColumn3);
		hlVendorBillDtl.addComponent(flDtlColumn4);
		hlVendorBillDtl.addComponent(flDtlColumn5);
		hlVendorBillDtl.setSpacing(true);
		hlVendorBillDtl.setMargin(true);
		VerticalLayout vlVendorBillHdr = new VerticalLayout();
		vlVendorBillHdr = new VerticalLayout();
		vlVendorBillHdr.addComponent(hlVendorBillDtl);
		vlVendorBillHdr.addComponent(tblVendorBillDtl);
		vlVendorBillHdr.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlVendorBillHdr, "Vendor Detail");
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
		btnAdd.setStyleName("add");
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<VendorBillHdrDM> list = new ArrayList<VendorBillHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbBranch.getValue() + ", " + cbStatus.getValue());
		Long poNo = null;
		if (cbpoNo.getValue() != null) {
			poNo = (((PurchasePOHdrDM) cbpoNo.getValue()).getPoId());
		}
		list = servicevendorBillHdr.getVendorBillHdrList(companyid, null, poNo, (Long) cbBranch.getValue(),
				(String) cbStatus.getValue(), tfbillNo.getValue(), "f");
		recordCnt = list.size();
		beanVendorBillHdr = new BeanItemContainer<VendorBillHdrDM>(VendorBillHdrDM.class);
		beanVendorBillHdr.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanVendorBillHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "billId", "branchName", "billNo", "purchaseOrderNo",
				"status", "lastUpdtDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Bill No.", "PO No ", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("poId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadVendorDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordCnt = vendorDtlList.size();
			tblVendorBillDtl.setPageLength(5);
			beanVendorBillDtl = new BeanItemContainer<VendorBillDtlDM>(VendorBillDtlDM.class);
			beanVendorBillDtl.addAll(vendorDtlList);
			BigDecimal sum = new BigDecimal("0");
			for (VendorBillDtlDM obj : vendorDtlList) {
				if (obj.getBasicValue() != null) {
					sum = sum.add(obj.getBasicValue());
				}
			}
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(sum.toString());
			tfBasictotal.setReadOnly(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxslap. result set");
			tblVendorBillDtl.setContainerDataSource(beanVendorBillDtl);
			tblVendorBillDtl.setVisibleColumns(new Object[] { "productName", "basicValue", "receiptQty", "rejectQty",
					"unitRate", "productUom", "recpDtlStatus", "lastupdateddt", "lastupdatedby" });
			tblVendorBillDtl.setColumnHeaders(new String[] { "Product Name", "Basic Value", "Receipt Qty",
					"Reject Qty", "Unit Rate", "Product Uom", "Status", "Last Updated Date", "Last Updated By" });
			tblVendorBillDtl.setColumnFooter("lastupdateddt", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Branch List
	private void loadBranchList() {
		try {
			List<BranchDM> branchList = serviceBranch.getBranchList(null, null, null, "Active", companyid, "P");
			branchList.add(new BranchDM(0L, "All Branches"));
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(branchList);
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
			cbMatUom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Product
	private void loadProduct() {
		try {
			Long poId = ((PurchasePOHdrDM) cbpoNo.getValue()).getPoId();
			BeanItemContainer<PurchasePODtlDM> beanPlnDtl = new BeanItemContainer<PurchasePODtlDM>(
					PurchasePODtlDM.class);
			beanPlnDtl.addAll(servicePurchasePODtl.getPurchaseOrdDtlList(null, poId, null, null));
			cbproduct.setContainerDataSource(beanPlnDtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load PoNo
	private void loadPoNo() {
		try {
			BeanItemContainer<PurchasePOHdrDM> beanPurPoDM = new BeanItemContainer<PurchasePOHdrDM>(
					PurchasePOHdrDM.class);
			beanPurPoDM.addAll(servicepurchaePOHdr.getPurchaseOrdHdrList(companyid, null, null, null, null));
			cbpoNo.setContainerDataSource(beanPurPoDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editVendorBillHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			VendorBillHdrDM vendorBillHdrDM = beanVendorBillHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			billId = vendorBillHdrDM.getBillId();
			cbBranch.setValue(vendorBillHdrDM.getBranchId());
			dfbillDt.setValue(vendorBillHdrDM.getBillDate());
			tfbillNo.setValue(vendorBillHdrDM.getBillNo());
			taRemark.setValue(vendorBillHdrDM.getBillRemark());
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(vendorBillHdrDM.getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfpackingPer.setValue(vendorBillHdrDM.getPackingPrcnt().toString());
			tfPaclingValue.setReadOnly(false);
			tfPaclingValue.setValue(vendorBillHdrDM.getPackingPrcnt().toString());
			tfPaclingValue.setReadOnly(true);
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(vendorBillHdrDM.getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(vendorBillHdrDM.getVatPrcnt().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(vendorBillHdrDM.getVatValue().toString());
			tfVatValue.setReadOnly(true);
			tfEDPer.setValue(vendorBillHdrDM.getEdPrcnt().toString());
			tfEDValue.setReadOnly(false);
			tfEDValue.setValue(vendorBillHdrDM.getEdValue().toString());
			tfEDValue.setReadOnly(true);
			tfHEDPer.setValue(vendorBillHdrDM.getHedPrcnt().toString());
			tfHEDValue.setReadOnly(false);
			tfHEDValue.setValue(vendorBillHdrDM.getHedValue().toString());
			tfHEDValue.setReadOnly(true);
			tfCessPer.setValue(vendorBillHdrDM.getCessPrcnt().toString());
			tfCessValue.setReadOnly(false);
			tfCessValue.setValue(vendorBillHdrDM.getCessValue().toString());
			tfCessValue.setReadOnly(true);
			tfCstPer.setValue(vendorBillHdrDM.getCstPrcnt().toString());
			tfCstValue.setReadOnly(false);
			tfCstValue.setValue(vendorBillHdrDM.getCstValue().toString());
			tfCstValue.setReadOnly(true);
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(vendorBillHdrDM.getSubTaxTotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			tfFreightPer.setValue(vendorBillHdrDM.getFreightPrcnt().toString());
			tfFreightValue.setReadOnly(false);
			tfFreightValue.setValue(vendorBillHdrDM.getFreightValue().toString());
			tfFreightValue.setReadOnly(true);
			tfOtherPer.setValue((vendorBillHdrDM.getOtherPrcnt().toString()));
			tfOtherValue.setReadOnly(false);
			tfOtherValue.setValue((vendorBillHdrDM.getOtherValue().toString()));
			tfOtherValue.setReadOnly(true);
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(vendorBillHdrDM.getGrantTotal().toString());
			tfGrandtotal.setReadOnly(true);
			if (vendorBillHdrDM.getPaymentTerms() != null) {
				tfPamentTerm.setValue(vendorBillHdrDM.getPaymentTerms().toString());
			}
			if (vendorBillHdrDM.getFrightTerms() != null) {
				tfFreightTerms.setValue(vendorBillHdrDM.getFrightTerms());
			}
			if (vendorBillHdrDM.getWarrantyTerms() != null) {
				tfWarrentyTerms.setValue(vendorBillHdrDM.getWarrantyTerms());
			}
			if ((vendorBillHdrDM.getDeliveryTerms() != null)) {
				tfDelTerms.setValue(vendorBillHdrDM.getDeliveryTerms());
			}
			Long poid = vendorBillHdrDM.getPoId();
			Collection<?> poids = cbpoNo.getItemIds();
			for (Iterator<?> iterator = poids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbpoNo.getItem(itemId);
				// Get the actual bean and use the data
				PurchasePOHdrDM st = (PurchasePOHdrDM) item.getBean();
				if (poid != null && poid.equals(st.getPoId())) {
					cbpoNo.setValue(itemId);
				}
			}
			if (vendorBillHdrDM.getDutyExempted().equals("Y")) {
				ckdutyexm.setValue(true);
			} else {
				ckdutyexm.setValue(false);
			}
			if (vendorBillHdrDM.getCformReqd().equals("Y")) {
				ckCformRqu.setValue(true);
			} else {
				ckCformRqu.setValue(false);
			}
			if (vendorBillHdrDM.getPdcReqd().equals("Y")) {
				ckPdcRqu.setValue(true);
			} else {
				ckPdcRqu.setValue(false);
			}
			cbStatus.setValue(vendorBillHdrDM.getStatus());
			vendorDtlList = servicevendorBillDtl.getsaveVendorBillDtlList(null, billId, null, null);
		}
		loadVendorDtl();
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, billId, null, null, null,
				null, status);
	}
	
	private void editVendorBillDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		if (tblVendorBillDtl.getValue() != null) {
			VendorBillDtlDM vendorBillDtlDM = beanVendorBillDtl.getItem(tblVendorBillDtl.getValue()).getBean();
			Long prodid = vendorBillDtlDM.getProductId();
			Collection<?> prodids = cbproduct.getItemIds();
			for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbproduct.getItem(itemId);
				// Get the actual bean and use the data
				PurchasePODtlDM st = (PurchasePODtlDM) item.getBean();
				if (prodid != null && prodid.equals(st.getProductId())) {
					cbproduct.setValue(itemId);
				}
			}
			if (vendorBillDtlDM.getReceiptQty() != null) {
				tfreceiptQnty.setValue(vendorBillDtlDM.getReceiptQty().toString());
			}
			if (vendorBillDtlDM.getRejectQty() != null) {
				tfrejectQty.setValue(vendorBillDtlDM.getRejectQty().toString());
			}
			tfUnitRate.setValue(vendorBillDtlDM.getUnitRate().toString());
			tfBasicValue.setValue(vendorBillDtlDM.getBasicValue().toString());
			cbMatUom.setValue(vendorBillDtlDM.getProductUom());
			if (vendorBillDtlDM.getRecpDtlStatus() != null) {
				cbDtlStatus.setValue(vendorBillDtlDM.getRecpDtlStatus());
			}
			if (vendorBillDtlDM.getRejectReason() != null) {
				tarejectReason.setValue(vendorBillDtlDM.getRejectReason());
			}
			if (vendorBillDtlDM.getDebitNoteVal() != null) {
				tfDebitValue.setValue(vendorBillDtlDM.getDebitNoteVal().toString());
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
		cbpoNo.setValue(null);
		tfbillNo.setValue("");
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
		cbMatUom.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		cbBranch.setRequired(true);
		cbpoNo.setRequired(true);
		tfBasicValue.setRequired(true);
		cbMatUom.setRequired(true);
		tfUnitRate.setRequired(true);
		tfreceiptQnty.setRequired(true);
		tfrejectQty.setRequired(true);
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
		assembleInputUserLayout();
		new UploadDocumentUI(hlPODoc);
		new UploadDocumentUI(hldtlDoc);
		btnAdd.setCaption("Add");
		tblVendorBillDtl.setVisible(true);
		loadVendorDtl();
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		cbproduct.setRequired(true);
		cbMatUom.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		cbBranch.setRequired(true);
		tfBasicValue.setRequired(true);
		cbMatUom.setRequired(true);
		cbpoNo.setRequired(true);
		tfUnitRate.setRequired(true);
		tfreceiptQnty.setRequired(true);
		tfrejectQty.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		resetFields();
		editVendorBillDtl();
		editVendorBillHdr();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		cbpoNo.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if ((cbpoNo.getValue() == null)) {
			cbpoNo.setComponentError(new UserError(GERPErrorCodes.QUOTE_NO));
			errorFlag = true;
		}
		cbMatUom.setComponentError(null);
		tfreceiptQnty.setComponentError(null);
		tfrejectQty.setComponentError(null);
		tfUnitRate.setComponentError(null);
		tfBasicValue.setComponentError(null);
		cbproduct.setComponentError(null);
		if (tblVendorBillDtl.size() == 0) {
			cbMatUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			tfreceiptQnty.setComponentError(new UserError(GERPErrorCodes.RECEIPT_QTY));
			tfrejectQty.setComponentError(new UserError(GERPErrorCodes.REGECT_QTY));
			tfUnitRate.setComponentError(new UserError(GERPErrorCodes.UNIT_RATE));
			tfBasicValue.setComponentError(new UserError(GERPErrorCodes.BASIC_VALUE));
			cbproduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// VendorBill Validation
	private boolean validateBillDetail() {
		boolean isValid = true;
		if (cbMatUom.getValue() == null) {
			cbMatUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			isValid = false;
		} else {
			cbMatUom.setComponentError(null);
		}
		if (tfreceiptQnty.getValue() == "0") {
			tfreceiptQnty.setComponentError(new UserError(GERPErrorCodes.RECEIPT_QTY));
			isValid = false;
		} else {
			tfreceiptQnty.setComponentError(null);
		}
		if (tfrejectQty.getValue() == "0") {
			tfrejectQty.setComponentError(new UserError(GERPErrorCodes.REGECT_QTY));
			isValid = false;
		} else {
			tfrejectQty.setComponentError(null);
		}
		if (tfUnitRate.getValue() == "0") {
			tfUnitRate.setComponentError(new UserError(GERPErrorCodes.UNIT_RATE));
			isValid = false;
		} else {
			tfUnitRate.setComponentError(null);
		}
		if (tfBasicValue.getValue() == "0") {
			tfBasicValue.setComponentError(new UserError(GERPErrorCodes.BASIC_VALUE));
			isValid = false;
		} else {
			tfBasicValue.setComponentError(null);
		}
		if (tarejectReason.getValue() == null) {
			tarejectReason.setComponentError(new UserError(GERPErrorCodes.REGECT_REASON));
			isValid = false;
		} else {
			tarejectReason.setComponentError(null);
		}
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
			VendorBillHdrDM vendorBillHdrDM = new VendorBillHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				vendorBillHdrDM = beanVendorBillHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			vendorBillHdrDM.setBranchId((Long) cbBranch.getValue());
			vendorBillHdrDM.setCompanyId(companyid);
			vendorBillHdrDM.setBillDate(dfbillDt.getValue());
			vendorBillHdrDM.setBillRemark(taRemark.getValue());
			vendorBillHdrDM.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			vendorBillHdrDM.setPackingPrcnt((new BigDecimal(tfpackingPer.getValue())));
			vendorBillHdrDM.setPackingValue(new BigDecimal(tfPaclingValue.getValue()));
			vendorBillHdrDM.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			vendorBillHdrDM.setVatPrcnt(((new BigDecimal(tfVatPer.getValue()))));
			vendorBillHdrDM.setVatValue(new BigDecimal(tfVatValue.getValue()));
			vendorBillHdrDM.setEdPrcnt((new BigDecimal(tfEDPer.getValue())));
			vendorBillHdrDM.setEdValue((new BigDecimal(tfEDValue.getValue())));
			vendorBillHdrDM.setHedValue(new BigDecimal(tfHEDValue.getValue()));
			vendorBillHdrDM.setHedPrcnt((new BigDecimal(tfHEDPer.getValue())));
			vendorBillHdrDM.setCessPrcnt(new BigDecimal(tfCessPer.getValue()));
			vendorBillHdrDM.setCessValue(new BigDecimal(tfCessValue.getValue()));
			vendorBillHdrDM.setCstPrcnt((new BigDecimal(tfCstPer.getValue())));
			vendorBillHdrDM.setCstValue(new BigDecimal(tfCstValue.getValue()));
			vendorBillHdrDM.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			vendorBillHdrDM.setFreightPrcnt(new BigDecimal(tfFreightPer.getValue()));
			vendorBillHdrDM.setFreightValue(new BigDecimal(tfFreightValue.getValue()));
			vendorBillHdrDM.setOtherPrcnt(new BigDecimal(tfOtherPer.getValue()));
			vendorBillHdrDM.setOtherValue(new BigDecimal(tfOtherValue.getValue()));
			vendorBillHdrDM.setGrantTotal(new BigDecimal(tfGrandtotal.getValue()));
			vendorBillHdrDM.setBillNo(tfbillNo.getValue());
			if (tfPamentTerm.getValue() != null) {
				vendorBillHdrDM.setPaymentTerms((tfPamentTerm.getValue().toString()));
			}
			if (tfFreightTerms.getValue() != null) {
				vendorBillHdrDM.setFrightTerms(tfFreightTerms.getValue().toString());
			}
			if (tfWarrentyTerms.getValue() != null) {
				vendorBillHdrDM.setWarrantyTerms((tfWarrentyTerms.getValue().toString()));
			}
			if (tfDelTerms.getValue() != null) {
				vendorBillHdrDM.setDeliveryTerms(tfDelTerms.getValue().toString());
			}
			if (ckdutyexm.getValue().equals(true)) {
				vendorBillHdrDM.setDutyExempted("Y");
			} else if (ckdutyexm.getValue().equals(false)) {
				vendorBillHdrDM.setDutyExempted("N");
			}
			if (ckCformRqu.getValue().equals(true)) {
				vendorBillHdrDM.setCformReqd("Y");
			} else if (ckCformRqu.getValue().equals(false)) {
				vendorBillHdrDM.setCformReqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				vendorBillHdrDM.setPdcReqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				vendorBillHdrDM.setPdcReqd("N");
			}
			if (cbpoNo.getValue() != null) {
				vendorBillHdrDM.setPoId(((PurchasePOHdrDM) cbpoNo.getValue()).getPoId());
			}
			if (cbStatus.getValue().toString() != null) {
				vendorBillHdrDM.setStatus(cbStatus.getValue().toString());
			}
			File file = new File(GERPConstants.DOCUMENT_PATH);
			byte fileContent[] = new byte[(int) file.length()];
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContent);
			fio.close();
			vendorBillHdrDM.setVendorRefdoc(fileContents);
			vendorBillHdrDM.setPreparedBy(employeeId);
			vendorBillHdrDM.setReviewedBy(null);
			vendorBillHdrDM.setActionedBy(null);
			vendorBillHdrDM.setLastUpdtDate(DateUtils.getcurrentdate());
			vendorBillHdrDM.setLastUpdatedBy(username);
			servicevendorBillHdr.saveVendorBillHdrDetails(vendorBillHdrDM);
			@SuppressWarnings("unchecked")
			Collection<VendorBillDtlDM> itemIds = (Collection<VendorBillDtlDM>) tblVendorBillDtl.getVisibleItemIds();
			for (VendorBillDtlDM save : (Collection<VendorBillDtlDM>) itemIds) {
				save.setBillId(Long.valueOf(vendorBillHdrDM.getBillId().toString()));
				servicevendorBillDtl.saveVendorBillDtlDetails(save);
			}
			comments.saveVendorBill(vendorBillHdrDM.getBillId(), vendorBillHdrDM.getStatus());
			vendorBillResetFields();
			resetFields();
			loadSrchRslt();
			billId = 0L;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveVendorBillDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			int count = 0;
			for (VendorBillDtlDM vendorBillDtlDM : vendorDtlList) {
				if (vendorBillDtlDM.getProductId() == ((PurchasePODtlDM) cbproduct.getValue()).getProductId()) {
					count++;
					break;
				}
			}
			if (count == 0) {
				VendorBillDtlDM vendorDtlDtlobj = new VendorBillDtlDM();
				if (tblVendorBillDtl.getValue() != null) {
					vendorDtlDtlobj = beanVendorBillDtl.getItem(tblVendorBillDtl.getValue()).getBean();
					vendorDtlList.remove(vendorDtlDtlobj);
				}
				vendorDtlDtlobj.setProductId(((PurchasePODtlDM) cbproduct.getValue()).getProductId());
				vendorDtlDtlobj.setProductName(((PurchasePODtlDM) cbproduct.getValue()).getProductName());
				vendorDtlDtlobj.setReceiptQty((Long.valueOf(tfreceiptQnty.getValue())));
				vendorDtlDtlobj.setRejectQty((Long.valueOf(tfrejectQty.getValue())));
				vendorDtlDtlobj.setUnitRate((Long.valueOf(tfUnitRate.getValue())));
				vendorDtlDtlobj.setProductUom(cbMatUom.getValue().toString());
				vendorDtlDtlobj.setBasicValue((new BigDecimal(tfBasicValue.getValue())));
				vendorDtlDtlobj.setRejectReason(tarejectReason.getValue());
				if (cbDtlStatus.getValue() != null) {
					vendorDtlDtlobj.setRecpDtlStatus(cbDtlStatus.getValue().toString());
				}
				if (ckdebitNote.getValue() != null) {
					if (ckdebitNote.getValue().equals(true)) {
						vendorDtlDtlobj.setDebitNoteYn("Y");
					} else if (ckdebitNote.getValue().equals(false)) {
						vendorDtlDtlobj.setDebitNoteYn("N");
					}
				}
				File file = new File(GERPConstants.DOCUMENT_PATH);
				byte fileContent[] = new byte[(int) file.length()];
				FileInputStream fio = new FileInputStream(file);
				byte fileContents[] = new byte[(int) file.length()];
				fio.read(fileContent);
				fio.close();
				vendorDtlDtlobj.setReceiptEvd(fileContents);
				vendorDtlDtlobj.setDebitNoteVal((Long.valueOf(tfDebitValue.getValue())));
				vendorDtlDtlobj.setLastupdateddt(DateUtils.getcurrentdate());
				vendorDtlDtlobj.setLastupdatedby(username);
				vendorDtlList.add(vendorDtlDtlobj);
				loadVendorDtl();
				getCalculatedValues();
			} else {
				cbproduct.setComponentError(new UserError("Product Already Exist. ."));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		vendorBillResetFields();
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
		cbpoNo.setRequired(false);
		tfbillNo.setRequired(false);
		tfBasicValue.setRequired(false);
		cbMatUom.setRequired(false);
		tfUnitRate.setRequired(false);
		resetFields();
		vendorBillResetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfBasictotal.setReadOnly(false);
		tfBasictotal.setValue("0");
		try {
			tfCessPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CESS", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCessPer.setValue("0");
		}
		tfCessValue.setReadOnly(false);
		tfCessValue.setValue("0");
		ckCformRqu.setValue(false);
		try {
			tfCstPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CST", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCstPer.setValue("0");
		}
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
		cbpoNo.setValue(null);
		tfPamentTerm.setValue("");
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue("0");
		try {
			tfpackingPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "PACKING", "Active", "F").get(0)
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
			tfHEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "HED", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfHEDPer.setValue("0");
		}
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue("");
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
		cbproduct.setValue(null);
		cbStatus.setValue(null);
		dfbillDt.setValue(new Date());
		taRemark.setValue("");
		cbBranch.setComponentError(null);
		cbBranch.setValue(branchId);
		ckPdcRqu.setValue(false);
		vendorDtlList = new ArrayList<VendorBillDtlDM>();
		tblVendorBillDtl.removeAllItems();
		new UploadDocumentUI(hlPODoc);
		cbpoNo.setComponentError(null);
		tfbillNo.setValue("");
		cbproduct.setContainerDataSource(null);
	}
	
	private void vendorBillResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbproduct.setValue(null);
		tfUnitRate.setValue("0");
		tfUnitRate.setComponentError(null);
		cbMatUom.setReadOnly(false);
		cbMatUom.setValue(null);
		cbMatUom.setReadOnly(true);
		cbMatUom.setComponentError(null);
		tfBasicValue.setValue("0");
		tfBasicValue.setComponentError(null);
		tarejectReason.setValue("");
		cbDtlStatus.setValue(null);
		cbproduct.setComponentError(null);
		tfreceiptQnty.setValue("0");
		tfreceiptQnty.setComponentError(null);
		tfrejectQty.setValue("0");
		tfrejectQty.setComponentError(null);
		tfDebitValue.setValue("");
		ckdebitNote.setValue(null);
		new UploadDocumentUI(hldtlDoc);
		btnAdd.setCaption("Add");
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
		VendorBillDtlDM vendorBillDtlDM = new VendorBillDtlDM();
		if (tblVendorBillDtl.getValue() != null) {
			vendorBillDtlDM = beanVendorBillDtl.getItem(tblVendorBillDtl.getValue()).getBean();
			vendorDtlList.remove(vendorBillDtlDM);
			vendorBillResetFields();
			loadVendorDtl();
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
