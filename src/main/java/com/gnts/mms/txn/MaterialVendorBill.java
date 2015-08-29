/**
 * File Name 		: MaterialVendorBill.java 
 * Description 		:This Screen Purpose for Modify the MaterialVendorBill Details.
 * 					 Add the material details process should be directly added in DB.
 * Author 			: Karthikeyan R
 * Date 			: Oct 24, 2014

 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1        Oct 24, 2014 		  Karthikeyan R                 Initial  Version
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
import com.gnts.mms.domain.txn.MmsPoDtlDM;
import com.gnts.mms.domain.txn.MmsVendorBillDtlDM;
import com.gnts.mms.domain.txn.MmsVendorBillHdrDM;
import com.gnts.mms.domain.txn.POHdrDM;
import com.gnts.mms.service.txn.MmsPoDtlService;
import com.gnts.mms.service.txn.MmsVendorBillDtlService;
import com.gnts.mms.service.txn.MmsVendorBillHdrService;
import com.gnts.mms.service.txn.POHdrService;
import com.gnts.sms.service.mst.SmsTaxesService;
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

public class MaterialVendorBill extends BaseTransUI {
	private MmsVendorBillHdrService serviceVendorBillHdr = (MmsVendorBillHdrService) SpringContextHelper
			.getBean("mmsvendorbillheader");
	private MmsVendorBillDtlService serviceVendorBillDtl = (MmsVendorBillDtlService) SpringContextHelper
			.getBean("mmsvendorbilldetail");
	private MmsPoDtlService servicepodtl = (MmsPoDtlService) SpringContextHelper.getBean("mmspoDtl");
	private POHdrService servicepoHdr = (POHdrService) SpringContextHelper.getBean("pohdr");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private SmsTaxesService serviceTaxesSms = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	// form layout for input controls for Vendor Bill Header
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// form layout for input controls for Vendor Bill Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4, flDtlColumn5;
	// // User Input Components for Vendor Bill Details
	private ComboBox cbBranch, cbpoNo, cbStatus;
	private TextField tfbillNo, tfBasictotal, tfpackingPer, tfPaclingValue, tfpaymetTerms, tfFreightTerms,
			tfWarrentyTerms, tfDelTerms;
	private TextField tfSubTotal, tfVatPer, tfVatValue, tfEDPer, tfEDValue, tfHEDPer;
	private TextField tfHEDValue, tfCessPer, tfCessValue, tfCstPer, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightPer, tfFreightValue, tfOtherPer, tfOtherValue, tfGrandtotal;
	private TextArea taRemark;
	private CheckBox ckDutyexm, ckPdcRqu, ckCformRqu;
	private PopupDateField dfbillDt;
	private Button btnAdd = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlPODoc = new VerticalLayout();
	private VerticalLayout hldtlDoc = new VerticalLayout();
	// VendorBillDtl components
	private ComboBox cbMaterial, cbMatUom, cbDtlStatus;
	private TextField tfreceiptQnty, tfrejectQty, tfUnitRate, tfBasicValue, tfDebitValue;
	private TextArea taRejectReason;
	private CheckBox ckdebitNote;
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private BeanItemContainer<MmsVendorBillHdrDM> beanVendorBillHdr = null;
	private BeanItemContainer<MmsVendorBillDtlDM> beanVendorBillDtl = null;
	private List<MmsVendorBillDtlDM> listVendorBillDtls = new ArrayList<MmsVendorBillDtlDM>();
	// local variables declaration
	private String username;
	private Long companyid;
	private int recordCnt;
	private Long employeeId;
	private Long branchId;
	private Long billId;
	private MmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private GERPTable tblVendorBillDtl;
	private Button btndelete = new GERPButton("Delete", "delete", this);
	// Initialize logger
	private Logger logger = Logger.getLogger(MaterialVendorBill.class);
	
	// Constructor received the parameters from Login UI class
	public MaterialVendorBill() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside VendorBill() constructor");
		// Loading the VendorBill UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax UI");
		// Initialization for VendorBill Details user input components
		btnPrint.setVisible(false);
		cbpoNo = new GERPComboBox("PO No");
		cbpoNo.setItemCaptionPropertyId("pono");
		loadPoNo();
		btndelete.setEnabled(false);
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
					loadMatNameList();
					loadMatDtl();
					tfpaymetTerms.setValue(((POHdrDM) cbpoNo.getValue()).getPaymentTerms());
					tfFreightTerms.setValue(((POHdrDM) cbpoNo.getValue()).getFrnghtTerms());
					tfWarrentyTerms.setValue(((POHdrDM) cbpoNo.getValue()).getWrntyTerms());
					tfDelTerms.setValue(((POHdrDM) cbpoNo.getValue()).getDlvryTerms());
					if (((POHdrDM) cbpoNo.getValue()).getDutyExempt().equals("Y")) {
						ckDutyexm.setValue(true);
					} else {
						ckDutyexm.setValue(false);
					}
					if (((POHdrDM) cbpoNo.getValue()).getCformReqd().equals("Y")) {
						ckCformRqu.setValue(true);
					} else {
						ckCformRqu.setValue(false);
					}
					if (((POHdrDM) cbpoNo.getValue()).getpDCReqd().equals("Y")) {
						ckPdcRqu.setValue(true);
					} else {
						ckPdcRqu.setValue(false);
					}
				}
			}
		});
		btndelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndelete == event.getButton()) {
					btnAdd.setCaption("Add");
					deleteDetails();
				}
			}
		});
		dfbillDt = new GERPPopupDateField("Bill Date");
		dfbillDt.setInputPrompt("Select Date");
		dfbillDt.setWidth("145");
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
		tfPaclingValue.setWidth("125");
		tfSubTotal = new TextField("Sub Total");
		tfSubTotal.setWidth("150");
		tfVatPer = new TextField();
		tfVatPer.setWidth("30");
		tfVatValue = new TextField();
		tfVatValue.setWidth("125");
		tfEDPer = new TextField();
		tfEDPer.setWidth("30");
		tfEDValue = new TextField();
		tfEDValue.setWidth("125");
		tfHEDPer = new TextField();
		tfHEDPer.setWidth("30");
		tfHEDValue = new TextField();
		tfHEDValue.setWidth("125");
		tfCessPer = new TextField();
		tfCessPer.setWidth("30");
		tfCessValue = new TextField();
		tfCessValue.setWidth("125");
		tfCstPer = new TextField();
		tfCstPer.setWidth("30");
		tfCstValue = new TextField();
		tfCstValue.setWidth("125");
		tfSubTaxTotal = new TextField("Sub Tax Total");
		tfSubTaxTotal.setWidth("150");
		tfFreightPer = new TextField();
		tfFreightPer.setWidth("30");
		tfFreightValue = new TextField();
		tfFreightValue.setWidth("125");
		tfOtherValue = new TextField();
		tfOtherValue.setWidth("125");
		tfGrandtotal = new TextField("Grand Total");
		tfGrandtotal.setWidth("150");
		tfOtherPer = new TextField();
		tfOtherPer.setWidth("30");
		tfpaymetTerms = new TextField("Payment Terms");
		tfpaymetTerms.setWidth("150");
		tfFreightTerms = new TextField("Freight Terms");
		tfFreightTerms.setWidth("150");
		tfWarrentyTerms = new TextField("Warrenty Terms");
		tfWarrentyTerms.setWidth("150");
		tfDelTerms = new TextField("Delivery Terms");
		tfDelTerms.setWidth("150");
		ckDutyexm = new CheckBox("Duty Exempted");
		ckCformRqu = new CheckBox("Cfrom Req");
		ckPdcRqu = new CheckBox("PDC Req");
		cbBranch = new ComboBox("Branch");
		cbBranch.setWidth("150");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbStatus = new GERPComboBox("Status", BASEConstants.T_MMS_VENDOR_BILL_HDR, BASEConstants.MMSVBR_STATUS);
		cbStatus.setWidth("150");
		// VendorBill Detail Comp
		cbMaterial = new GERPComboBox("Material");
		cbMaterial.setItemCaptionPropertyId("materialname");
		cbMaterial.setWidth("150");
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
					cbMatUom.setReadOnly(false);
					cbMatUom.setValue(((MmsPoDtlDM) cbMaterial.getValue()).getMaterialuom());
					cbMatUom.setReadOnly(true);
					tfreceiptQnty.setReadOnly(false);
					tfreceiptQnty.setValue(((MmsPoDtlDM) cbMaterial.getValue()).getPoqty() + "");
					tfreceiptQnty.setReadOnly(true);
					tfUnitRate.setReadOnly(false);
					tfUnitRate.setValue(((MmsPoDtlDM) cbMaterial.getValue()).getUnitrate() + "");
					tfUnitRate.setReadOnly(true);
					tfBasicValue.setReadOnly(false);
					tfBasicValue.setValue(((MmsPoDtlDM) cbMaterial.getValue()).getBasicvalue() + "");
					tfBasicValue.setReadOnly(true);
				}
			}
		});
		cbMatUom = new ComboBox("Uom");
		cbMatUom.setItemCaptionPropertyId("lookupname");
		cbMatUom.setWidth("150");
		loadUomList();
		tfreceiptQnty = new TextField("Receipt Qty");
		tfreceiptQnty.setWidth("150");
		tfrejectQty = new TextField("Reject Qty");
		tfrejectQty.setWidth("150");
		tfDebitValue = new TextField("Debit Value");
		tfDebitValue.setValue("0");
		tfDebitValue.setWidth("150");
		tfUnitRate = new TextField("Unit Rate");
		tfUnitRate.setWidth("150");
		tfUnitRate.setValue("0");
		tfBasicValue = new TextField("Basic value");
		tfBasicValue.setWidth("150");
		tfBasicValue.setValue("0");
		taRejectReason = new TextArea("Reason");
		taRejectReason.setWidth("150");
		taRejectReason.setHeight("30");
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.T_MMS_PO_RECEIPTS_DTL, BASEConstants.PORECDTL_STATUS);
		cbDtlStatus.setWidth("150");
		cbDtlStatus.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbDtlStatus.getValue() != null) {
					if (cbDtlStatus.getValue().equals("Rejected")) {
						taRejectReason.setEnabled(true);
						taRejectReason.setRequired(true);
					} else if (cbDtlStatus.getValue().equals("Accepted")) {
						taRejectReason.setEnabled(false);
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
				if (DtlValidation()) {
					saveBillDetails();
				}
			}
		});
		tblVendorBillDtl = new GERPTable();
		tblVendorBillDtl.setPageLength(10);
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
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		loadMatDtl();
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
		FormLayout flColumn5 = new FormLayout();
		flColumn1.addComponent(cbBranch);
		flColumn1.addComponent(cbpoNo);
		flColumn1.addComponent(tfbillNo);
		dfbillDt.setWidth("130");
		flColumn1.addComponent(dfbillDt);
		flColumn1.addComponent(taRemark);
		flColumn1.addComponent(tfBasictotal);
		HorizontalLayout pv = new HorizontalLayout();
		pv.addComponent(tfpackingPer);
		pv.addComponent(tfPaclingValue);
		pv.setCaption("Packing");
		flColumn2.addComponent(pv);
		flColumn2.setComponentAlignment(pv, Alignment.TOP_LEFT);
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
		flColumn3.addComponent(cst);
		flColumn3.setComponentAlignment(cst, Alignment.TOP_LEFT);
		flColumn3.addComponent(tfSubTaxTotal);
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
		flColumn3.addComponent(tfpaymetTerms);
		flColumn4.addComponent(tfFreightTerms);
		flColumn4.addComponent(tfWarrentyTerms);
		flColumn4.addComponent(tfDelTerms);
		// Formlayout4 components
		HorizontalLayout hlHdr1 = new HorizontalLayout();
		flColumn4.addComponent(ckDutyexm);
		// flColumn4.addComponent(ckCformRqu);
		flColumn4.addComponent(hlHdr1);
		hlHdr1.addComponent(ckCformRqu);
		hlHdr1.addComponent(ckPdcRqu);
		// flColumn4.addComponent(ckPdcRqu);
		flColumn4.addComponent(cbStatus);
		flColumn5.addComponent(hlPODoc);
		HorizontalLayout hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flColumn1);
		hlHdr.addComponent(flColumn2);
		hlHdr.addComponent(flColumn3);
		hlHdr.addComponent(flColumn4);
		hlHdr.addComponent(flColumn5);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding VendorBillDtl components
		// Add components for User Input Layout
		flDtlColumn1 = new FormLayout();
		flDtlColumn2 = new FormLayout();
		flDtlColumn3 = new FormLayout();
		flDtlColumn4 = new FormLayout();
		flDtlColumn5 = new FormLayout();
		flDtlColumn1.addComponent(cbMaterial);
		flDtlColumn1.addComponent(cbMatUom);
		flDtlColumn1.addComponent(tfreceiptQnty);
		flDtlColumn2.addComponent(tfUnitRate);
		flDtlColumn2.addComponent(tfBasicValue);
		flDtlColumn2.addComponent(tfrejectQty);
		flDtlColumn3.addComponent(cbDtlStatus);
		flDtlColumn3.addComponent(taRejectReason);
		flDtlColumn3.addComponent(ckdebitNote);
		flDtlColumn4.addComponent(tfDebitValue);
		flDtlColumn4.addComponent(btnAdd);
		flDtlColumn4.addComponent(btndelete);
		flDtlColumn5.addComponent(hldtlDoc);
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
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlVendorBillHdr, "Material VendorBill");
		dtlTab.addTab(vlTableForm, "Comments");
		vlVendorBillHdr.setSpacing(true);
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
		List<MmsVendorBillHdrDM> vendorBillHdrList = new ArrayList<MmsVendorBillHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbBranch.getValue() + ", " + cbStatus.getValue());
		Long purNo = null;
		if (cbpoNo.getValue() != null) {
			purNo = (((POHdrDM) cbpoNo.getValue()).getPoId());
		}
		vendorBillHdrList = serviceVendorBillHdr.getMmsVendorBillHdrList(companyid, null, purNo,
				(Long) cbBranch.getValue(), (String) tfbillNo.getValue(), (String) cbStatus.getValue(), null, "F");
		recordCnt = vendorBillHdrList.size();
		beanVendorBillHdr = new BeanItemContainer<MmsVendorBillHdrDM>(MmsVendorBillHdrDM.class);
		beanVendorBillHdr.addAll(vendorBillHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanVendorBillHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "billId", "branchName", "billNo", "purchaseOrderNo",
				"status", "lastUpdtDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch", "Bill No", "PO No", "Status",
				"Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("poId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		tblMstScrSrchRslt.setPageLength(15);
	}
	
	private void loadMatDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblVendorBillDtl.removeAllItems();
			recordCnt = listVendorBillDtls.size();
			tblVendorBillDtl.setPageLength(3);
			beanVendorBillDtl = new BeanItemContainer<MmsVendorBillDtlDM>(MmsVendorBillDtlDM.class);
			beanVendorBillDtl.addAll(listVendorBillDtls);
			BigDecimal sum = new BigDecimal("0");
			for (MmsVendorBillDtlDM obj : listVendorBillDtls) {
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
			tblVendorBillDtl.setVisibleColumns(new Object[] { "materialName", "materialUom", "receiptQty", "unitRate",
					"basicValue", "rejectQty", "recpDtlStatus", "lastupdateddt", "lastupdatedby" });
			tblVendorBillDtl.setColumnHeaders(new String[] { "Material", "Uom", "Receipt Qty", "Unit Rate",
					"Basic Value", "Reject Qty", "Status", "Updated Date", "Updated By" });
			tblVendorBillDtl.setColumnFooter("lastupdateddt", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadMatNameList() {
		try {
			Long poid = ((POHdrDM) cbpoNo.getValue()).getPoId();
			BeanItemContainer<MmsPoDtlDM> beanPlnDtl = new BeanItemContainer<MmsPoDtlDM>(MmsPoDtlDM.class);
			beanPlnDtl.addAll(servicepodtl.getpodtllist(poid, null, null, null, null, "F"));
			cbMaterial.setContainerDataSource(beanPlnDtl);
		}
		catch (Exception e) {
			e.printStackTrace();
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
					.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active", "MM_UOM"));
			cbMatUom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadPoNo() {
		try {
			BeanItemContainer<POHdrDM> beanPurPoDM = new BeanItemContainer<POHdrDM>(POHdrDM.class);
			beanPurPoDM.addAll(servicepoHdr.getPOHdrList(companyid, null, null, null, null, null, null, "F"));
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
			MmsVendorBillHdrDM vendorBillHdrDM = beanVendorBillHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			billId = vendorBillHdrDM.getBillId();
			cbBranch.setValue(vendorBillHdrDM.getBranchId());
			dfbillDt.setValue(vendorBillHdrDM.getBillDate());
			if (vendorBillHdrDM.getBillNo() != null) {
				tfbillNo.setValue(vendorBillHdrDM.getBillNo());
			}
			if (vendorBillHdrDM.getBillRemark() != null) {
				taRemark.setValue(vendorBillHdrDM.getBillRemark().toString());
			}
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
				tfpaymetTerms.setValue(vendorBillHdrDM.getPaymentTerms());
			}
			if (vendorBillHdrDM.getFrightTerms() != null) {
				tfFreightTerms.setValue(vendorBillHdrDM.getFrightTerms());
			}
			if (vendorBillHdrDM.getWarrantyTerms() != null) {
				tfWarrentyTerms.setValue(vendorBillHdrDM.getWarrantyTerms());
			}
			if (vendorBillHdrDM.getDeliveryTerms() != null) {
				tfDelTerms.setValue(vendorBillHdrDM.getDeliveryTerms());
			}
			Long uom = vendorBillHdrDM.getPoId();
			Collection<?> uomid = cbpoNo.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbpoNo.getItem(itemId);
				// Get the actual bean and use the data
				POHdrDM st = (POHdrDM) item.getBean();
				if (uom != null && uom.equals(st.getPoId())) {
					cbpoNo.setValue(itemId);
				}
			}
			if (vendorBillHdrDM.getDutyExempted().equals("Y")) {
				ckDutyexm.setValue(true);
			} else {
				ckDutyexm.setValue(false);
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
			if (vendorBillHdrDM.getStatus() != null) {
				cbStatus.setValue(vendorBillHdrDM.getStatus().toString());
			}
			listVendorBillDtls = serviceVendorBillDtl.getMmsVendorBillDtlList(null, billId, null, null);
		}
		loadMatDtl();
		comments = new MmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, billId, null);
		comments.loadsrch(true, null, null, null, null, null, null, null, null, billId);
	}
	
	private void editVendorBillDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		if (tblVendorBillDtl.getValue() != null) {
			MmsVendorBillDtlDM vendorBillDtlDM = beanVendorBillDtl.getItem(tblVendorBillDtl.getValue()).getBean();
			Long matid = vendorBillDtlDM.getMaterialid();
			Collection<?> matids = cbMaterial.getItemIds();
			for (Iterator<?> iterator = matids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbMaterial.getItem(itemId);
				// Get the actual bean and use the data
				MmsPoDtlDM st = (MmsPoDtlDM) item.getBean();
				if (matid != null && matid.equals(st.getMaterialid())) {
					cbMaterial.setValue(itemId);
				}
			}
			if (vendorBillDtlDM.getReceiptQty() != null) {
				tfreceiptQnty.setReadOnly(false);
				tfreceiptQnty.setValue(vendorBillDtlDM.getReceiptQty().toString());
				tfreceiptQnty.setReadOnly(true);
			}
			if (vendorBillDtlDM.getRejectQty() != null) {
				tfrejectQty.setValue(vendorBillDtlDM.getRejectQty().toString());
			}
			tfUnitRate.setReadOnly(false);
			tfUnitRate.setValue(vendorBillDtlDM.getUnitRate().toString());
			tfUnitRate.setReadOnly(true);
			tfBasicValue.setReadOnly(false);
			tfBasicValue.setValue(vendorBillDtlDM.getBasicValue().toString());
			tfBasicValue.setReadOnly(true);
			cbMatUom.setReadOnly(false);
			cbMatUom.setValue(vendorBillDtlDM.getMaterialUom());
			cbMatUom.setReadOnly(true);
			if (vendorBillDtlDM.getRecpDtlStatus() != null) {
				cbDtlStatus.setValue(vendorBillDtlDM.getRecpDtlStatus());
			}
			if (vendorBillDtlDM.getRejectReason() != null) {
				taRejectReason.setValue(vendorBillDtlDM.getRejectReason());
			}
			if (vendorBillDtlDM.getDebitNoteVal() != null) {
				tfDebitValue.setValue(vendorBillDtlDM.getDebitNoteVal().toString());
			}
			if (vendorBillDtlDM.getDebitNoteYn() != null) {
				if (vendorBillDtlDM.getDebitNoteYn().equals("Y")) {
					ckdebitNote.setValue(true);
				} else {
					ckdebitNote.setValue(false);
				}
			}
			if (hldtlDoc != null) {
				if (vendorBillDtlDM.getReceiptEvd() != null) {
					byte[] certificate = vendorBillDtlDM.getReceiptEvd();
					UploadDocumentUI test = new UploadDocumentUI(hldtlDoc);
					test.displaycertificate(certificate);
				} else {
					new UploadDocumentUI(hldtlDoc);
				}
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
		taRemark.setValue(null);
		cbBranch.setValue(branchId);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		cbMatUom.setRequired(true);
		tfBasicValue.setRequired(true);
		tfUnitRate.setRequired(true);
		tfreceiptQnty.setRequired(true);
		tfrejectQty.setRequired(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		cbMaterial.setRequired(true);
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
		comments = new MmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null);
	}
	
	@Override
	protected void editDetails() {
		cbMatUom.setRequired(true);
		tfBasicValue.setRequired(true);
		tfUnitRate.setRequired(true);
		tfreceiptQnty.setRequired(true);
		tfrejectQty.setRequired(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		cbMaterial.setRequired(true);
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
		cbMaterial.setComponentError(null);
		if (tblVendorBillDtl.size() == 0) {
			cbMatUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			tfreceiptQnty.setComponentError(new UserError(GERPErrorCodes.RECEIPT_QTY));
			tfrejectQty.setComponentError(new UserError(GERPErrorCodes.REGECT_QTY));
			tfUnitRate.setComponentError(new UserError(GERPErrorCodes.UNIT_RATE));
			tfBasicValue.setComponentError(new UserError(GERPErrorCodes.BASIC_VALUE));
			cbMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// VendorBill Validation
	private boolean DtlValidation() {
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
		if (taRejectReason.getValue() == null) {
			taRejectReason.setComponentError(new UserError(GERPErrorCodes.REGECT_REASON));
			isValid = false;
		} else {
			taRejectReason.setComponentError(null);
		}
		if (cbMaterial.getValue() == null) {
			cbMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			isValid = false;
		} else {
			cbMaterial.setComponentError(null);
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			MmsVendorBillHdrDM vendorBillHdr = new MmsVendorBillHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				vendorBillHdr = beanVendorBillHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			vendorBillHdr.setBranchId((Long) cbBranch.getValue());
			vendorBillHdr.setCompanyId(companyid);
			vendorBillHdr.setBillDate(dfbillDt.getValue());
			vendorBillHdr.setBillRemark(taRemark.getValue());
			vendorBillHdr.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			vendorBillHdr.setPackingPrcnt((new BigDecimal(tfpackingPer.getValue())));
			vendorBillHdr.setPackingValue(new BigDecimal(tfPaclingValue.getValue()));
			vendorBillHdr.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			vendorBillHdr.setVatPrcnt(((new BigDecimal(tfVatPer.getValue()))));
			vendorBillHdr.setVatValue(new BigDecimal(tfVatValue.getValue()));
			vendorBillHdr.setEdPrcnt((new BigDecimal(tfEDPer.getValue())));
			vendorBillHdr.setEdValue((new BigDecimal(tfEDValue.getValue())));
			vendorBillHdr.setHedValue(new BigDecimal(tfHEDValue.getValue()));
			vendorBillHdr.setHedPrcnt((new BigDecimal(tfHEDPer.getValue())));
			vendorBillHdr.setCessPrcnt(new BigDecimal(tfCessPer.getValue()));
			vendorBillHdr.setCessValue(new BigDecimal(tfCessValue.getValue()));
			vendorBillHdr.setCstPrcnt((new BigDecimal(tfCstPer.getValue())));
			vendorBillHdr.setCstValue(new BigDecimal(tfCstValue.getValue()));
			vendorBillHdr.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			vendorBillHdr.setFreightPrcnt(new BigDecimal(tfFreightPer.getValue()));
			vendorBillHdr.setFreightValue(new BigDecimal(tfFreightValue.getValue()));
			vendorBillHdr.setOtherPrcnt(new BigDecimal(tfOtherPer.getValue()));
			vendorBillHdr.setOtherValue(new BigDecimal(tfOtherValue.getValue()));
			vendorBillHdr.setGrantTotal(new BigDecimal(tfGrandtotal.getValue()));
			vendorBillHdr.setBillNo(tfbillNo.getValue());
			if (tfpaymetTerms.getValue().toString() != null) {
				vendorBillHdr.setPaymentTerms(tfpaymetTerms.getValue());
			}
			if (tfFreightTerms.getValue().toString() != null) {
				vendorBillHdr.setFrightTerms(tfFreightTerms.getValue());
			}
			if (tfWarrentyTerms.getValue().toString() != null) {
				vendorBillHdr.setWarrantyTerms(tfWarrentyTerms.getValue());
			}
			if (tfDelTerms.getValue().toString() != null) {
				vendorBillHdr.setDeliveryTerms(tfDelTerms.getValue());
			}
			if (ckDutyexm.getValue().equals(true)) {
				vendorBillHdr.setDutyExempted("Y");
			} else if (ckDutyexm.getValue().equals(false)) {
				vendorBillHdr.setDutyExempted("N");
			}
			if (ckCformRqu.getValue().equals(true)) {
				vendorBillHdr.setCformReqd("Y");
			} else if (ckCformRqu.getValue().equals(false)) {
				vendorBillHdr.setCformReqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				vendorBillHdr.setPdcReqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				vendorBillHdr.setPdcReqd("N");
			}
			if (cbpoNo.getValue() != null) {
				vendorBillHdr.setPoId(((POHdrDM) cbpoNo.getValue()).getPoId());
			}
			if (cbStatus.getValue() != null) {
				vendorBillHdr.setStatus(cbStatus.getValue().toString());
			}
			File file = new File(GERPConstants.DOCUMENT_PATH);
			byte fileContent[] = new byte[(int) file.length()];
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContent);
			fio.close();
			vendorBillHdr.setVendorRefdoc(fileContents);
			vendorBillHdr.setPreparedBy(employeeId);
			vendorBillHdr.setReviewedBy(null);
			vendorBillHdr.setActionedBy(null);
			vendorBillHdr.setLastUpdtDate(DateUtils.getcurrentdate());
			vendorBillHdr.setLastUpdatedBy(username);
			serviceVendorBillHdr.saveOrUpdateMmsVendorBillHdr(vendorBillHdr);
			@SuppressWarnings("unchecked")
			Collection<MmsVendorBillDtlDM> itemIds = (Collection<MmsVendorBillDtlDM>) tblVendorBillDtl
					.getVisibleItemIds();
			for (MmsVendorBillDtlDM save : (Collection<MmsVendorBillDtlDM>) itemIds) {
				save.setBillId(Long.valueOf(vendorBillHdr.getBillId().toString()));
				serviceVendorBillDtl.saveOrUpdatemmsvendrdtlDetails(save);
			}
			loadSrchRslt();
			resetFields();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void saveBillDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			int count = 0;
			for (MmsVendorBillDtlDM mmsVendorBillDtlDM : listVendorBillDtls) {
				if (mmsVendorBillDtlDM.getMaterialid() == ((MmsPoDtlDM) cbMaterial.getValue()).getMaterialid()) {
					count++;
					break;
				}
			}
			if (count == 0) {
				MmsVendorBillDtlDM vendorDtlDtlobj = new MmsVendorBillDtlDM();
				if (tblVendorBillDtl.getValue() != null) {
					vendorDtlDtlobj = beanVendorBillDtl.getItem(tblVendorBillDtl.getValue()).getBean();
					listVendorBillDtls.remove(vendorDtlDtlobj);
				}
				vendorDtlDtlobj.setMaterialid(((MmsPoDtlDM) cbMaterial.getValue()).getMaterialid());
				vendorDtlDtlobj.setMaterialName(((MmsPoDtlDM) cbMaterial.getValue()).getMaterialname());
				vendorDtlDtlobj.setReceiptQty((Long.valueOf(tfreceiptQnty.getValue())));
				vendorDtlDtlobj.setRejectQty((Long.valueOf(tfrejectQty.getValue())));
				vendorDtlDtlobj.setUnitRate((Long.valueOf(tfUnitRate.getValue())));
				vendorDtlDtlobj.setMaterialUom(cbMatUom.getValue().toString());
				vendorDtlDtlobj.setBasicValue((new BigDecimal(tfBasicValue.getValue())));
				vendorDtlDtlobj.setRejectReason(taRejectReason.getValue());
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
				listVendorBillDtls.add(vendorDtlDtlobj);
				btnAdd.setCaption("Add");
				loadMatDtl();
				getCalculatedValues();
			} else {
				cbMaterial.setComponentError(new UserError("Material Already Exist. ."));
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
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MMS_VENDOR_BILL_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", billId);
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
		tfCessPer.setValue("10");
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
		ckDutyexm.setValue(false);
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
		tfpaymetTerms.setValue("");
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
		cbMaterial.setValue(null);
		cbStatus.setValue(null);
		cbBranch.setValue(branchId);
		dfbillDt.setValue(new Date());
		taRemark.setValue("");
		cbBranch.setComponentError(null);
		ckPdcRqu.setValue(false);
		listVendorBillDtls = new ArrayList<MmsVendorBillDtlDM>();
		tblVendorBillDtl.removeAllItems();
		cbMaterial.setContainerDataSource(null);
		new UploadDocumentUI(hlPODoc);
		cbpoNo.setComponentError(null);
		tfbillNo.setNullRepresentation("");
	}
	
	protected void vendorBillResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMaterial.setValue(null);
		tfUnitRate.setReadOnly(false);
		tfUnitRate.setValue("0");
		tfUnitRate.setReadOnly(true);
		tfUnitRate.setComponentError(null);
		cbMatUom.setReadOnly(false);
		cbMatUom.setValue(null);
		cbMatUom.setComponentError(null);
		cbMatUom.setReadOnly(true);
		tfBasicValue.setReadOnly(false);
		tfBasicValue.setValue("0");
		tfBasicValue.setReadOnly(true);
		tfBasicValue.setComponentError(null);
		taRejectReason.setValue("");
		cbDtlStatus.setValue(null);
		cbMaterial.setComponentError(null);
		tfreceiptQnty.setReadOnly(false);
		tfreceiptQnty.setValue("0");
		tfreceiptQnty.setReadOnly(true);
		tfDebitValue.setValue("0");
		tfreceiptQnty.setComponentError(null);
		tfrejectQty.setValue("0");
		tfrejectQty.setComponentError(null);
		ckdebitNote.setValue(null);
		tfbillNo.setValue("");
		cbMaterial.setValue(null);
		new UploadDocumentUI(hldtlDoc);
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
	
	private void deleteDetails() {
		MmsVendorBillDtlDM save = new MmsVendorBillDtlDM();
		if (tblVendorBillDtl.getValue() != null) {
			save = beanVendorBillDtl.getItem(tblVendorBillDtl.getValue()).getBean();
			listVendorBillDtls.remove(save);
			vendorBillResetFields();
			loadMatDtl();
			btndelete.setEnabled(false);
		}
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
