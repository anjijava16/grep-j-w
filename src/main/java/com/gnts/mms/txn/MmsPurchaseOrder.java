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
package com.gnts.mms.txn;

import java.io.File;
import java.io.FileInputStream;
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
import com.gnts.base.service.mst.CompanyService;
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
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.txn.MmsEnqDtlDM;
import com.gnts.mms.domain.txn.MmsPoDtlDM;
import com.gnts.mms.domain.txn.MmsQuoteDtlDM;
import com.gnts.mms.domain.txn.MmsQuoteHdrDM;
import com.gnts.mms.domain.txn.POHdrDM;
import com.gnts.mms.service.txn.MmsCommentsService;
import com.gnts.mms.service.txn.MmsPoDtlService;
import com.gnts.mms.service.txn.MmsQuoteDtlService;
import com.gnts.mms.service.txn.MmsQuoteHdrService;
import com.gnts.mms.service.txn.POHdrService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
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

public class MmsPurchaseOrder extends BaseTransUI {
	// private static final long serialVersionUID = 1L;
	private MmsPoDtlService servicepodtl = (MmsPoDtlService) SpringContextHelper.getBean("mmspoDtl");
	private POHdrService servicepohdr = (POHdrService) SpringContextHelper.getBean("pohdr");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private MmsCommentsService serviceComments = (MmsCommentsService) SpringContextHelper.getBean("mmscomments");
	private MmsQuoteDtlService serviceMmsQuoteDtlService = (MmsQuoteDtlService) SpringContextHelper
			.getBean("mmsquotedtl");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private BeanItemContainer<MmsPoDtlDM> beanpodtl = null;
	private MmsQuoteHdrService serviceMmsQuoteHdr = (MmsQuoteHdrService) SpringContextHelper.getBean("mmsquotehdr");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private CompanyService serviceCompany = (CompanyService) SpringContextHelper.getBean("companyBean");
	// private BeanItemContainer<MmsPoDtlDM> beanpodtldm = null;
	private GERPTable tblPODetails;
	// form layout for input controls for PO Header
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// form layout for input controls for PO Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4, flDtlColumn5;
	// // User Input Components for PO Details
	private ComboBox cbBranch, cbStatus, cbVendor, cbpoType;
	private GERPComboBox cbQuoteRef;
	private TextField tfversionNo, tfBasictotal, tfPaclingValue, tfPONo, tfPaymentTerms, tfFreightTerms,
			tfWarrentyTerms, tfDelTerms;
	private TextField tfSubTotal;
	private TextField tfquoteNum;
	private TextField tfCstValue, tfSubTaxTotal, tfEDValue, tfHEDValue, tfCessValue, tfVatValue;
	private TextField tfFreightValue, tfOtherValue, tfGrandtotal, tfvendorCode, tfpackingPer, tfVatPer, tfEDPer,
			tfHEDPer, tfCessPer, tfCstPer, tfFreightPer, tfOtherPer;
	private TextArea taRemark, taInvoiceOrd, taShpnAddr;
	private CheckBox ckdutyexm, ckPdcRqu, ckCformRqu, ckcasePO;
	private PopupDateField dfPODt, dfExpDt;
	private Button btnsavepurQuote = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlPODoc = new VerticalLayout();
	// PODtl components
	private ComboBox cbMaterial, cbMatUom, cbPODtlStatus;
	private TextField tfPOQnty, tfUnitRate, tfBasicValue;
	private TextArea taPODtlRemark;
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private List<MmsPoDtlDM> listPODetails = new ArrayList<MmsPoDtlDM>();
	private BeanItemContainer<POHdrDM> beanpohdr = null;
	// local variables declaration
	private String username;
	private Long companyid;
	private int recordCnt;
	private Long employeeId;
	private File file;
	private Long roleId;
	private Long branchId;
	private Long stateId;
	private Long moduleId;
	private Long appScreenId;
	private Long branchID;
	private Long poId;
	private String poid;
	private MmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	// Initialize logger
	private Logger logger = Logger.getLogger(MmsPurchaseOrder.class);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private int recordcnt = 0;
	
	// Constructor received the parameters from Login UI class
	public MmsPurchaseOrder() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		// StateId = Long.valueOf(UI.getCurrent().getSession().getAttribute("stateId").toString());
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
		tfquoteNum = new TextField("Quote No");
		tfquoteNum.setWidth("150");
		tfquoteNum.setValue("");
		cbQuoteRef = new GERPComboBox("Quote Ref No.");
		cbQuoteRef.setWidth("150");
		cbQuoteRef.setRequired(true);
		cbQuoteRef.setItemCaptionPropertyId("quoteRef");
		loadQuoteRefNumber();
		cbQuoteRef.addValueChangeListener(new Property.ValueChangeListener() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbQuoteRef.getItem(itemId);
				if (item != null) {
					loadmaterial();
					loadQuoteDetails();
				}
			}
		});
		taInvoiceOrd = new TextArea("Invoice Addr");
		taInvoiceOrd.setHeight("35");
		taInvoiceOrd.setWidth("150");
		taShpnAddr = new TextArea("Shipping Addr");
		taShpnAddr.setHeight("35");
		taShpnAddr.setWidth("150");
		dfPODt = new GERPPopupDateField("PO Date");
		dfPODt.setInputPrompt("Select Date");
		dfPODt.setWidth("130");
		tfPONo = new TextField("PO No.");
		tfPONo.setWidth("150");
		taRemark = new TextArea("Remarks");
		taRemark.setHeight("50");
		taRemark.setWidth("150");
		tfversionNo = new GERPNumberField("Version No");
		tfversionNo.setWidth("150");
		tfBasictotal = new GERPNumberField("Basic Total");
		tfBasictotal.setWidth("150");
		tfpackingPer = new GERPNumberField();
		tfpackingPer.setWidth("30");
		tfPaclingValue = new GERPNumberField();
		tfPaclingValue.setWidth("124");
		tfEDPer = new GERPNumberField();
		tfEDPer.setWidth("30");
		tfEDValue = new GERPNumberField();
		tfEDValue.setWidth("124");
		tfHEDPer = new GERPNumberField();
		tfHEDPer.setWidth("30");
		tfHEDValue = new GERPNumberField();
		tfHEDValue.setWidth("124");
		tfCessPer = new GERPNumberField();
		tfCessPer.setWidth("30");
		tfCessValue = new GERPNumberField();
		tfCessValue.setWidth("124");
		tfCstPer = new GERPNumberField();
		tfCstPer.setWidth("30");
		tfCstValue = new GERPNumberField();
		tfCstValue.setWidth("124");
		tfFreightPer = new GERPNumberField();
		tfFreightPer.setWidth("30");
		tfOtherPer = new GERPNumberField();
		tfOtherPer.setWidth("30");
		tfSubTotal = new GERPNumberField("Sub Total");
		tfSubTotal.setWidth("150");
		tfVatValue = new GERPNumberField();
		tfVatValue.setWidth("124");
		tfVatPer = new GERPNumberField();
		tfVatPer.setWidth("30");
		tfCstValue = new GERPNumberField();
		tfCstValue.setWidth("124");
		tfSubTaxTotal = new GERPNumberField("Sub Tax Total");
		tfSubTaxTotal.setWidth("150");
		tfFreightValue = new GERPNumberField();
		tfFreightValue.setWidth("124");
		tfOtherValue = new GERPNumberField();
		tfOtherValue.setWidth("124");
		tfGrandtotal = new GERPNumberField("Grand Total");
		tfGrandtotal.setWidth("150");
		tfPaymentTerms = new TextField("Payment Terms");
		tfPaymentTerms.setWidth("150");
		tfFreightTerms = new TextField("Freight Terms");
		tfFreightTerms.setWidth("150");
		tfWarrentyTerms = new TextField("Warrenty Terms");
		tfWarrentyTerms.setWidth("150");
		tfDelTerms = new TextField("Delivery Terms");
		tfDelTerms.setWidth("150");
		ckdutyexm = new CheckBox("Duty Exempted");
		ckCformRqu = new CheckBox("Cfrom Req");
		ckCformRqu.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				loadCForm();
			}
		});
		ckPdcRqu = new CheckBox("PDC Req");
		ckcasePO = new CheckBox("Case PO");
		cbBranch = new ComboBox("Branch Name");
		cbBranch.setWidth("150");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbVendor = new ComboBox("Vendor Name");
		cbVendor.setWidth("150");
		cbVendor.setItemCaptionPropertyId("vendorName");
		cbVendor.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbVendor.getValue() != null) {
					tfvendorCode.setReadOnly(false);
					tfvendorCode.setValue(serviceVendor
							.getVendorList(null, (Long) cbVendor.getValue(), companyid, null, null, null, stateId,
									null, null, null, "P").get(0).getVendorCode());
					tfvendorCode.setReadOnly(true);
					Long VendorstateId = serviceVendor
							.getVendorList(null, (Long) cbVendor.getValue(), null, null, null, null, stateId, null,
									null, null, "P").get(0).getStateId();
					Long LoginstateId = serviceCompany.getCompanyList(null, null, companyid).get(0).getStateid();
					if (LoginstateId == VendorstateId) {
						tfCstValue.setReadOnly(false);
						tfCstValue.setValue("00");
						tfCstValue.setReadOnly(true);
						tfCstPer.setReadOnly(false);
						tfCstPer.setValue("00");
						tfCstPer.setReadOnly(true);
					}
				}
			}
		});
		loadVendor();
		tfvendorCode = new TextField("Vendor Code");
		tfvendorCode.setWidth("150");
		tfvendorCode.setReadOnly(true);
		cbpoType = new ComboBox("Order Type");
		cbpoType.setItemCaptionPropertyId("lookupname");
		cbpoType.setWidth("150");
		loadPOTypet();
		ApprovalSchemaDM obj = servicepohdr.getReviewerId(companyid, appScreenId, branchID, roleId).get(0);
		if (obj.getApprLevel().equals("Reviewer")) {
			cbStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR, BASEConstants.WO_RV_STATUS);
		} else {
			cbStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR, BASEConstants.WO_AP_STATUS);
		}
		cbStatus.setWidth("150");
		// PurchaseOrder Detail Comp
		cbMaterial = new ComboBox("Material Name");
		cbMaterial.setItemCaptionPropertyId("materialname");
		cbMaterial.setWidth("150");
		loadmaterial();
		cbMaterial.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbMaterial.getValue() != null) {
					tfPOQnty.setReadOnly(false);
					tfPOQnty.setValue(((MmsQuoteDtlDM) cbMaterial.getValue()).getQuoteqty() + "");
					tfPOQnty.setReadOnly(true);
					cbMatUom.setReadOnly(false);
					cbMatUom.setValue(((MmsQuoteDtlDM) cbMaterial.getValue()).getMatuom() + "");
					cbMatUom.setReadOnly(true);
				}
			}
		});
		tfPOQnty = new TextField();
		tfPOQnty.setValue("0");
		tfPOQnty.setWidth("80");
		cbMatUom = new ComboBox();
		cbMatUom.setItemCaptionPropertyId("lookupname");
		cbMatUom.setWidth("60");
		cbMatUom.setHeight("20");
		loadUomList();
		tfUnitRate = new TextField("Unit Rate");
		tfUnitRate.setWidth("150");
		tfUnitRate.setValue("0");
		tfUnitRate.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				calculateBasicvalue();
			}
		});
		tfBasicValue = new TextField("Basic value");
		tfBasicValue.setWidth("150");
		tfBasicValue.setValue("0");
		taPODtlRemark = new TextArea("Remark");
		taPODtlRemark.setWidth("150");
		taPODtlRemark.setHeight("40");
		dfExpDt = new GERPPopupDateField("Expected Delivery");
		dfExpDt.setInputPrompt("Select Date");
		dfExpDt.setWidth("130");
		cbPODtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbPODtlStatus.setWidth("150");
		btnsavepurQuote.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validatePODetails()) {
					savePurchaseQuoteDetails();
				}
			}
		});
		tblPODetails = new GERPTable();
		tblPODetails.setPageLength(10);
		btndelete.setEnabled(false);
		tblPODetails.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblPODetails.isSelected(event.getItemId())) {
					tblPODetails.setImmediate(true);
					btnsavepurQuote.setCaption("Add");
					btnsavepurQuote.setStyleName("savebt");
					btndelete.setEnabled(false);
					resetDetailsFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnsavepurQuote.setCaption("Update");
					btnsavepurQuote.setStyleName("savebt");
					btndelete.setEnabled(true);
					editPODtl();
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
		loadPODetails();
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
		flColumn1.addComponent(cbQuoteRef);
		flColumn1.addComponent(tfquoteNum);
		flColumn1.addComponent(cbVendor);
		flColumn1.addComponent(tfvendorCode);
		flColumn1.addComponent(cbpoType);
		flColumn1.addComponent(ckcasePO);
		flColumn1.addComponent(tfPONo);
		flColumn1.addComponent(dfPODt);
		flColumn2.addComponent(tfversionNo);
		flColumn2.addComponent(taInvoiceOrd);
		flColumn2.addComponent(taShpnAddr);
		flColumn2.addComponent(taRemark);
		flColumn2.addComponent(tfBasictotal);
		// // flColumn2.addComponent(tfPaclingValue);
		// HorizontalLayout pv = new HorizontalLayout();
		// pv.addComponent(tfpackingPer);
		// pv.addComponent(tfPaclingValue);
		// pv.setCaption("Packing");
		// flColumn2.addComponent(pv);
		// flColumn2.setComponentAlignment(pv, Alignment.TOP_LEFT);
		//
		// flColumn2.addComponent(tfSubTotal);
		// flColumn2.addComponent(tfSubTotal);
		// flColumn3.addComponent(tfVatValue);
		// flColumn3.addComponent(tfCstValue);
		// flColumn3.addComponent(tfSubTaxTotal);
		// flColumn3.addComponent(tfFreightValue);
		// flColumn3.addComponent(tfOtherValue);
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
		flColumn3.addComponent(vp);
		flColumn3.setComponentAlignment(vp, Alignment.TOP_LEFT);
		// HorizontalLayout ed = new HorizontalLayout();
		// ed.addComponent(tfEDPer);
		// ed.addComponent(tfEDValue);
		// ed.setCaption("ED");
		// flColumn2.addComponent(ed);
		// flColumn2.setComponentAlignment(ed, Alignment.TOP_LEFT);
		// HorizontalLayout hed = new HorizontalLayout();
		// hed.addComponent(tfHEDPer);
		// hed.addComponent(tfHEDValue);
		// hed.setCaption("HED");
		// flColumn2.addComponent(hed);
		// flColumn2.setComponentAlignment(hed, Alignment.TOP_LEFT);
		// flColumn2.addComponent(hed);
		// HorizontalLayout cess = new HorizontalLayout();
		// cess.addComponent(tfCessPer);
		// cess.addComponent(tfCessValue);
		// cess.setCaption("CESS");
		// flColumn3.addComponent(cess);
		// flColumn3.setComponentAlignment(cess, Alignment.TOP_LEFT);
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
		flColumn3.addComponent(tfPaymentTerms);
		flColumn3.addComponent(tfFreightTerms);
		flColumn3.addComponent(tfWarrentyTerms);
		flColumn4.addComponent(tfDelTerms);
		flColumn4.addComponent(dfExpDt);
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
		flDtlColumn1.addComponent(cbMaterial);
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfPOQnty);
		hlQtyUom.addComponent(cbMatUom);
		hlQtyUom.setCaption("PO Qty");
		flDtlColumn2.addComponent(hlQtyUom);
		flDtlColumn2.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		flDtlColumn2.addComponent(tfUnitRate);
		flDtlColumn3.addComponent(tfBasicValue);
		flDtlColumn3.addComponent(cbPODtlStatus);
		flDtlColumn4.addComponent(taPODtlRemark);
		// flDtlColumn5.addComponent(btnsavepurQuote);
		VerticalLayout btn = new VerticalLayout();
		btn.addComponent(btnsavepurQuote);
		btn.addComponent(btndelete);
		flDtlColumn5.addComponent(btn);
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
		vlSmsQuoteHDR.addComponent(tblPODetails);
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
		try {
			// TODO Auto-generated method stub
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfversionNo.setReadOnly(false);
			tfversionNo.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getQuoteVersion().toString());
			tfversionNo.setReadOnly(true);
			taRemark.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getRemarks());
			tfpackingPer.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getPackingPrcnt().toString());
			tfPaclingValue.setReadOnly(false);
			tfPaclingValue.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getPackingValue().toString());
			tfPaclingValue.setReadOnly(true);
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getVatPrcnt().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getVatValue().toString());
			tfVatValue.setReadOnly(true);
			tfCstPer.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getCstPrcnt().toString());
			tfCstValue.setReadOnly(false);
			tfCstValue.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getCstValue().toString());
			tfCstValue.setReadOnly(true);
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getSubTaxTotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			tfFreightPer.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getFreightPrcnt().toString());
			tfFreightValue.setReadOnly(false);
			tfFreightValue.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getFreightValue().toString());
			tfFreightValue.setReadOnly(true);
			tfOtherPer.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getOthersPrcnt().toString());
			tfOtherValue.setReadOnly(false);
			tfOtherValue.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getOthersValue().toString());
			tfOtherValue.setReadOnly(true);
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getGrandTotal().toString());
			tfGrandtotal.setReadOnly(true);
			try {
				tfPaymentTerms.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getPaymentTerms().toString());
			}
			catch (Exception e) {
			}
			try {
				tfFreightTerms.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getFreightTerms().toString());
			}
			catch (Exception e) {
			}
			try {
				tfquoteNum.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getQuoteNumber().toString());
			}
			catch (Exception e) {
			}
			try {
				tfWarrentyTerms.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getWarrantyTerms().toString());
			}
			catch (Exception e) {
			}
			try {
				tfDelTerms.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getDeliveryTerms().toString());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			// load quote details
			listPODetails = new ArrayList<MmsPoDtlDM>();
			MmsQuoteDtlDM quoteDtlDMobj = serviceMmsQuoteDtlService.getmmsquotedtllist(null,
					((MmsQuoteHdrDM) cbQuoteRef.getValue()).getQuoteId(), null, null, null).get(0);
			MmsPoDtlDM mmspodtl = new MmsPoDtlDM();
			mmspodtl.setMaterialid(quoteDtlDMobj.getMaterialid());
			mmspodtl.setMaterialname(quoteDtlDMobj.getMaterialname());
			mmspodtl.setUnitrate(Long.valueOf(quoteDtlDMobj.getUnitrate().toString()));
			mmspodtl.setPoqty(Long.valueOf(quoteDtlDMobj.getQuoteqty().toString()));
			mmspodtl.setMaterialuom(quoteDtlDMobj.getMatuom());
			mmspodtl.setBasicvalue(quoteDtlDMobj.getBasicvalue());
			mmspodtl.setPodtlstatus("Active");
			mmspodtl.setLastupdatedby(username);
			mmspodtl.setLastupdatedt(DateUtils.getcurrentdate());
			listPODetails.add(mmspodtl);
			loadPODetails();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<POHdrDM> pohdrlist = new ArrayList<POHdrDM>();
		String poType = null;
		if (cbpoType.getValue() != null) {
			poType = cbpoType.getValue().toString();
		}
		pohdrlist = servicepohdr.getPOHdrList(companyid, null, (Long) cbBranch.getValue(), null,
				(String) cbStatus.getValue(), (String) cbpoType.getValue(), tfPONo.getValue(), "F");
		recordcnt = pohdrlist.size();
		beanpohdr = new BeanItemContainer<POHdrDM>(POHdrDM.class);
		beanpohdr.addAll(pohdrlist);
		tblMstScrSrchRslt.setContainerDataSource(beanpohdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "poId", "branchName", "pono", "pOType", "pOStatus",
				"lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch", "PO.No", "PO Type", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("poId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordcnt);
	}
	
	private void loadPODetails() {
		recordcnt = listPODetails.size();
		tblPODetails.setPageLength(3);
		tblPODetails.setWidth("1000px");
		beanpodtl = new BeanItemContainer<MmsPoDtlDM>(MmsPoDtlDM.class);
		beanpodtl.addAll(listPODetails);
		BigDecimal sum = new BigDecimal("0");
		for (MmsPoDtlDM obj : listPODetails) {
			if (obj.getBasicvalue() != null) {
				sum = sum.add(obj.getBasicvalue());
			}
		}
		tfBasicValue.setReadOnly(false);
		tfBasicValue.setValue(sum.toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Taxslap. result set");
		tblPODetails.setContainerDataSource(beanpodtl);
		tblPODetails.setVisibleColumns(new Object[] { "materialname", "materialuom", "poqty", "unitrate", "basicvalue",
				"podtlstatus", "lastupdatedt", "lastupdatedby" });
		tblPODetails.setColumnHeaders(new String[] { "Material Name", "Material Uom", "Qty", "Unit Rate",
				"Basic Value", "Status", "Last Updated Date", "Last Updated By" });
	}
	
	private void loadCForm() {
		try {
			if (ckCformRqu.getValue() == true) {
				tfCstValue.setReadOnly(false);
				tfCstValue.setValue("2");
				tfCstValue.setReadOnly(true);
			} else {
				tfCstValue.setReadOnly(false);
				tfCstValue.setValue(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getCstValue().toString());
				tfCstValue.setReadOnly(true);
			}
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
			e.printStackTrace();
		}
	}
	
	private void loadmaterial() {
		try {
			BeanItemContainer<MmsQuoteDtlDM> beanpodtl = new BeanItemContainer<MmsQuoteDtlDM>(MmsQuoteDtlDM.class);
			beanpodtl.addAll(serviceMmsQuoteDtlService.getmmsquotedtllist(null,
					((MmsQuoteHdrDM) cbQuoteRef.getValue()).getQuoteId(), null, null, null));
			cbMaterial.setContainerDataSource(beanpodtl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadVendor() {
		try {
			BeanContainer<Long, VendorDM> beanVendor = new BeanContainer<Long, VendorDM>(VendorDM.class);
			beanVendor.setBeanIdProperty("vendorId");
			beanVendor.addAll(serviceVendor.getVendorList(null, null, companyid, null, null, null, stateId, null, null,
					null, "P"));
			cbVendor.setContainerDataSource(beanVendor);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadPOTypet() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"MM_POTYPE"));
			cbpoType.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadQuoteRefNumber() {
		BeanItemContainer<MmsQuoteHdrDM> beanQuote = new BeanItemContainer<MmsQuoteHdrDM>(MmsQuoteHdrDM.class);
		beanQuote.addAll(serviceMmsQuoteHdr.getMmsQuoteHdrList(companyid, null, null, null, null, null, null, "F"));
		cbQuoteRef.setContainerDataSource(beanQuote);
	}
	
	private void editPOHdr() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlCmdBtnLayout.setVisible(false);
			hlUserInputLayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				POHdrDM poHdrDM = beanpohdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
				poId = poHdrDM.getPoId();
				cbBranch.setValue(poHdrDM.getBranchId());
				tfPONo.setReadOnly(false);
				tfPONo.setValue(poHdrDM.getPono());
				tfPONo.setReadOnly(true);
				dfPODt.setValue(poHdrDM.getPurchaseDate());
				dfExpDt.setValue(poHdrDM.getExpDate1());
				taRemark.setValue(poHdrDM.getPoRemark());
				tfversionNo.setValue(poHdrDM.getVersionNo().toString());
				tfBasictotal.setReadOnly(false);
				tfBasictotal.setValue(poHdrDM.getBasicTotal().toString());
				tfBasictotal.setReadOnly(true);
				tfpackingPer.setValue(poHdrDM.getPackingPrcnt().toString());
				tfPaclingValue.setReadOnly(false);
				tfPaclingValue.setValue(poHdrDM.getPackingVL().toString());
				tfPaclingValue.setReadOnly(true);
				tfSubTotal.setReadOnly(false);
				tfSubTotal.setValue(poHdrDM.getSubTotal().toString());
				tfSubTotal.setReadOnly(true);
				tfVatPer.setValue(poHdrDM.getVatPrcnt().toString());
				tfVatValue.setReadOnly(false);
				tfVatValue.setValue(poHdrDM.getVatValue().toString());
				tfVatValue.setReadOnly(true);
				tfCstPer.setValue(poHdrDM.getCstPrcnt().toString());
				tfCstValue.setReadOnly(false);
				tfCstValue.setValue(poHdrDM.getCstValue().toString());
				tfCstValue.setReadOnly(true);
				tfSubTaxTotal.setReadOnly(false);
				tfSubTaxTotal.setValue(poHdrDM.getSubTaxTotal().toString());
				tfSubTaxTotal.setReadOnly(true);
				tfFreightPer.setValue(poHdrDM.getFrgtPrcnt().toString());
				tfFreightValue.setReadOnly(false);
				tfFreightValue.setValue(poHdrDM.getFrgtValue().toString());
				tfFreightValue.setReadOnly(true);
				tfOtherPer.setValue((poHdrDM.getOthersPrcnt().toString()));
				tfOtherValue.setReadOnly(false);
				tfOtherValue.setValue((poHdrDM.getOthersValue().toString()));
				tfOtherValue.setReadOnly(true);
				tfGrandtotal.setReadOnly(false);
				tfGrandtotal.setValue(poHdrDM.getGrandTotal().toString());
				tfGrandtotal.setReadOnly(true);
				if (poHdrDM.getPaymentTerms() != null) {
					tfPaymentTerms.setValue(poHdrDM.getPaymentTerms().toString());
				}
				if (poHdrDM.getFrnghtTerms() != null) {
					tfFreightTerms.setValue(poHdrDM.getFrnghtTerms());
				}
				if (poHdrDM.getExpDate() != null) {
					dfExpDt.setValue(poHdrDM.getExpDate1());
				}
				if (poHdrDM.getWrntyTerms() != null) {
					tfWarrentyTerms.setValue(poHdrDM.getWrntyTerms());
				}
				if ((poHdrDM.getDlvryTerms() != null)) {
					tfDelTerms.setValue(poHdrDM.getDlvryTerms());
				}
				if (poHdrDM.getpOType() != null) {
					cbpoType.setValue(poHdrDM.getpOType());
				}
				if (poHdrDM.getShippingAddr() != null) {
					taShpnAddr.setValue(poHdrDM.getShippingAddr());
				}
				if (poHdrDM.getInvoiceAddress() != null) {
					taInvoiceOrd.setValue(poHdrDM.getInvoiceAddress());
				}
				if (poHdrDM.getVendorId() != null) {
					cbVendor.setValue(poHdrDM.getVendorId());
				}
				Long quoteid = poHdrDM.getQuoteId();
				Collection<?> quoteids = cbQuoteRef.getItemIds();
				for (Iterator<?> iterator = quoteids.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbQuoteRef.getItem(itemId);
					// Get the actual bean and use the data
					MmsQuoteHdrDM st = (MmsQuoteHdrDM) item.getBean();
					if (quoteid != null && quoteid.equals(st.getQuoteId())) {
						cbQuoteRef.setReadOnly(false);
						cbQuoteRef.setValue(itemId);
						cbQuoteRef.setReadOnly(true);
					}
				}
				if (poHdrDM.getDutyExempt().equals("Y")) {
					ckdutyexm.setValue(true);
				} else {
					ckdutyexm.setValue(false);
				}
				if (poHdrDM.getCformReqd().equals("Y")) {
					ckCformRqu.setValue(true);
				} else {
					ckCformRqu.setValue(false);
				}
				if (poHdrDM.getpDCReqd().equals("Y")) {
					ckPdcRqu.setValue(true);
				} else {
					ckPdcRqu.setValue(false);
				}
				if (poHdrDM.getCasePoYn().equals("Y")) {
					ckcasePO.setValue(true);
				} else {
					ckcasePO.setValue(false);
				}
				cbStatus.setValue(poHdrDM.getpOStatus());
				listPODetails = servicepodtl.getpodtllist(poId, null, null, null, null, "F");
			}
			loadPODetails();
			comments = new MmsComments(vlTableForm, null, companyid, null, null, null, poId, null, null, null, null);
			comments.loadsrch(true, null, companyid, null, null, null, poId, null, null, null);
			comments.commentList = serviceComments.getmmscommentsList(null, null, null, null, null, null, poId, null,
					null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setDfPODt(PopupDateField dfPODt) {
		this.dfPODt = dfPODt;
	}
	
	private void editPODtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		if (tblPODetails.getValue() != null) {
			MmsPoDtlDM editmmspodtllist = beanpodtl.getItem(tblPODetails.getValue()).getBean();
			Long matid = editmmspodtllist.getMaterialid();
			Collection<?> matids = cbMaterial.getItemIds();
			for (Iterator<?> iterator = matids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbMaterial.getItem(itemId);
				// Get the actual bean and use the data
				MmsQuoteDtlDM st = (MmsQuoteDtlDM) item.getBean();
				if (matid != null && matid.equals(st.getMaterialid())) {
					cbMaterial.setValue(itemId);
				}
			}
			tfPOQnty.setReadOnly(false);
			tfPOQnty.setValue(editmmspodtllist.getPoqty().toString());
			tfPOQnty.setReadOnly(true);
			if (editmmspodtllist.getUnitrate().toString() != null) {
				tfUnitRate.setValue(editmmspodtllist.getUnitrate() + "");
			}
			cbMatUom.setReadOnly(false);
			cbMatUom.setValue(editmmspodtllist.getMaterialuom());
			cbMatUom.setReadOnly(true);
			tfBasicValue.setReadOnly(false);
			tfBasicValue.setValue(editmmspodtllist.getBasicvalue().toString());
			tfBasicValue.setReadOnly(true);
			if (editmmspodtllist.getRemarks() != null) {
				taPODtlRemark.setValue(editmmspodtllist.getRemarks() + "");
			}
			cbPODtlStatus.setValue(editmmspodtllist.getPodtlstatus());
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
		cbpoType.setRequired(true);
		tfBasicValue.setRequired(true);
		tfUnitRate.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		tfBasictotal.setReadOnly(true);
		tfSubTotal.setReadOnly(true);
		tfSubTaxTotal.setReadOnly(true);
		tfGrandtotal.setReadOnly(true);
		tfPaclingValue.setReadOnly(true);
		tfVatValue.setReadOnly(true);
		tfCstValue.setReadOnly(true);
		tfFreightValue.setReadOnly(true);
		tfOtherValue.setReadOnly(true);
		loadPODetails();
		assembleInputUserLayout();
		new UploadDocumentUI(hlPODoc);
		try {
			resetFields();
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_NPONO").get(0);
			tfPONo.setReadOnly(false);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfPONo.setValue(slnoObj.getKeyDesc());
				tfPONo.setReadOnly(true);
			} else {
				tfPONo.setReadOnly(false);
			}
		}
		catch (Exception e) {
		}
		btnsavepurQuote.setCaption("Add");
		tblPODetails.setVisible(true);
		comments = new MmsComments(vlTableForm, null, companyid, null, null, null, poId, null, null, null, null);
	}
	
	@Override
	protected void editDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
			hlCmdBtnLayout.setVisible(false);
			cbMaterial.setRequired(true);
			hlUserInputLayout.removeAllComponents();
			// remove the components in the search layout and input controls in the same container
			hlUserIPContainer.removeAllComponents();
			hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
			hlUserInputLayout.setSpacing(true);
			hlUserInputLayout.setSizeUndefined();
			cbBranch.setRequired(true);
			tfPONo.setRequired(true);
			tfBasicValue.setRequired(true);
			tfUnitRate.setRequired(true);
			// reset the input controls to default value
			tblMstScrSrchRslt.setVisible(false);
			assembleInputUserLayout();
			resetFields();
			editPODtl();
			editPOHdr();
			comments.loadsrch(true, null, companyid, null, null, null, poId, null, null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		cbpoType.setComponentError(null);
		tfquoteNum.setComponentError(null);
		cbQuoteRef.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if ((cbQuoteRef.getValue() == null)) {
			cbQuoteRef.setComponentError(new UserError(GERPErrorCodes.QUOTE_REF_NO));
			errorFlag = true;
		}
		if (cbpoType.getValue() == null) {
			cbpoType.setComponentError(new UserError(GERPErrorCodes.ORDER_TYPE));
			errorFlag = true;
		}
		if ((tfquoteNum.getValue() == null)) {
			tfquoteNum.setComponentError(new UserError(GERPErrorCodes.QUOTE_NO));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validatePODetails() {
		boolean isValid = true;
		if (cbMatUom.getValue() == null) {
			cbMatUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			isValid = false;
		} else {
			cbMatUom.setComponentError(null);
		}
		if (tfPOQnty.getValue() == "0") {
			tfPOQnty.setComponentError(new UserError(GERPErrorCodes.SMS_QTY));
			isValid = false;
		} else {
			tfPOQnty.setComponentError(null);
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
			POHdrDM poHdrDM = new POHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				poHdrDM = beanpohdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			poHdrDM.setPono(tfPONo.getValue());
			poHdrDM.setBranchId((Long) cbBranch.getValue());
			poHdrDM.setCompanyId(companyid);
			poHdrDM.setPurchaseDate(dfPODt.getValue());
			poHdrDM.setExpDate(dfExpDt.getValue());
			poHdrDM.setPoRemark(taRemark.getValue());
			poHdrDM.setpOType((String) cbpoType.getValue());
			poHdrDM.setVendorId((Long) cbVendor.getValue());
			poHdrDM.setVersionNo((Long.valueOf(tfversionNo.getValue())));
			poHdrDM.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			poHdrDM.setPackingPrcnt((Long.valueOf(tfpackingPer.getValue())));
			poHdrDM.setPackingVL(new BigDecimal(tfPaclingValue.getValue()));
			poHdrDM.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			poHdrDM.setVatPrcnt(new BigDecimal(tfVatPer.getValue()));
			poHdrDM.setVatValue(new BigDecimal(tfVatValue.getValue()));
			poHdrDM.setCstPrcnt((new BigDecimal(tfCstPer.getValue())));
			poHdrDM.setCstValue(new BigDecimal(tfCstValue.getValue()));
			poHdrDM.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			poHdrDM.setFrgtValue(new BigDecimal(tfFreightPer.getValue()));
			poHdrDM.setFrgtPrcnt(new BigDecimal(tfFreightPer.getValue()));
			poHdrDM.setOthersPrcnt(new BigDecimal(tfOtherPer.getValue()));
			poHdrDM.setOthersValue(new BigDecimal(tfOtherValue.getValue()));
			poHdrDM.setGrandTotal(new BigDecimal(tfGrandtotal.getValue()));
			if (tfPaymentTerms.getValue() != null) {
				poHdrDM.setPaymentTerms((tfPaymentTerms.getValue().toString()));
			}
			if (tfFreightTerms.getValue() != null) {
				poHdrDM.setFrnghtTerms(tfFreightTerms.getValue().toString());
			}
			if (tfWarrentyTerms.getValue() != null) {
				poHdrDM.setWrntyTerms((tfWarrentyTerms.getValue().toString()));
			}
			if (tfDelTerms.getValue() != null) {
				poHdrDM.setDlvryTerms(tfDelTerms.getValue().toString());
			}
			if (ckdutyexm.getValue().equals(true)) {
				poHdrDM.setDutyExempt("Y");
			} else if (ckdutyexm.getValue().equals(false)) {
				poHdrDM.setDutyExempt("N");
			}
			if (ckCformRqu.getValue().equals(true)) {
				poHdrDM.setCformReqd("Y");
			} else if (ckCformRqu.getValue().equals(false)) {
				poHdrDM.setCformReqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				poHdrDM.setpDCReqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				poHdrDM.setpDCReqd("N");
			}
			if (ckcasePO.getValue().equals(true)) {
				poHdrDM.setCasePoYn("Y");
			} else if (ckcasePO.getValue().equals(false)) {
				poHdrDM.setCasePoYn("N");
			}
			if (tfquoteNum.getValue() != null) {
				poHdrDM.setQuoteId(((MmsQuoteHdrDM) cbQuoteRef.getValue()).getQuoteId());
			}
			poHdrDM.setShippingAddr(taShpnAddr.getValue());
			poHdrDM.setInvoiceAddress(taInvoiceOrd.getValue());
			if (cbStatus.getValue() != null) {
				poHdrDM.setpOStatus(cbStatus.getValue().toString());
			}
			poHdrDM.setPreparedBy(employeeId);
			poHdrDM.setReviewedBy(null);
			poHdrDM.setActionedBY(null);
			poHdrDM.setLastUpdatedDt(DateUtils.getcurrentdate());
			poHdrDM.setLastUpdatedBy(username);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			poHdrDM.setPoDoc(fileContents);
			servicepohdr.saveorUpdatePOHdrDetails(poHdrDM);
			poId = poHdrDM.getPoId();
			@SuppressWarnings("unchecked")
			Collection<MmsPoDtlDM> itemIds = (Collection<MmsPoDtlDM>) tblPODetails.getVisibleItemIds();
			for (MmsPoDtlDM save : (Collection<MmsPoDtlDM>) itemIds) {
				save.setPoid(Long.valueOf(poHdrDM.getPoId().toString()));
				servicepodtl.saveorupdatepodtl(save);
			}
			comments.savePurchaseOrder(poHdrDM.getPoId(), poHdrDM.getpOStatus());
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_NPONO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_NPONO");
					}
				}
				catch (Exception e) {
				}
			}
			resetDetailsFields();
			resetFields();
			loadSrchRslt();
			poId = 0L;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void savePurchaseQuoteDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			int count = 0;
			for (MmsPoDtlDM MmsPoDtlDM : listPODetails) {
				if (MmsPoDtlDM.getMaterialid() == ((MmsQuoteDtlDM) cbMaterial.getValue()).getMaterialid()) {
					count++;
					break;
				}
			}
			if (count == 0) {
				MmsPoDtlDM poDtlDM = new MmsPoDtlDM();
				if (tblPODetails.getValue() != null) {
					try {
						poDtlDM = beanpodtl.getItem(tblPODetails.getValue()).getBean();
						listPODetails.remove(poDtlDM);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				poDtlDM.setMaterialid(((MmsQuoteDtlDM) cbMaterial.getValue()).getMaterialid());
				poDtlDM.setMaterialname(((MmsQuoteDtlDM) cbMaterial.getValue()).getMaterialname());
				tfPOQnty.setReadOnly(false);
				poDtlDM.setPoqty((Long.valueOf(tfPOQnty.getValue())));
				tfPOQnty.setReadOnly(true);
				poDtlDM.setUnitrate((Long.valueOf(tfUnitRate.getValue())));
				cbMatUom.setReadOnly(false);
				poDtlDM.setMaterialuom(cbMatUom.getValue().toString());
				cbMatUom.setReadOnly(true);
				tfBasicValue.setReadOnly(false);
				poDtlDM.setBasicvalue((new BigDecimal(tfBasicValue.getValue())));
				tfBasicValue.setReadOnly(true);
				poDtlDM.setRemarks(taPODtlRemark.getValue());
				poDtlDM.setPodtlstatus(cbPODtlStatus.getValue().toString());
				poDtlDM.setLastupdatedt(DateUtils.getcurrentdate());
				poDtlDM.setLastupdatedby(username);
				listPODetails.add(poDtlDM);
				loadPODetails();
				getCalculatedValues();
			} else {
				cbMaterial.setComponentError(new UserError("Product Already Exist.."));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		resetDetailsFields();
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
		cbMaterial.setRequired(false);
		cbBranch.setRequired(false);
		cbpoType.setRequired(false);
		tfPONo.setRequired(false);
		tfBasicValue.setRequired(false);
		tfUnitRate.setRequired(false);
		resetFields();
		resetDetailsFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfPONo.setReadOnly(false);
		tfPONo.setValue("");
		cbQuoteRef.setReadOnly(false);
		cbQuoteRef.setValue(null);
		tfBasictotal.setReadOnly(false);
		tfBasictotal.setValue("0");
		ckCformRqu.setValue(false);
		tfDelTerms.setValue("");
		// tfCstPer.setValue("0");
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue("0");
		ckdutyexm.setValue(false);
		tfWarrentyTerms.setValue("");
		// tfVatPer.setValue("0");
		tfVatValue.setReadOnly(false);
		tfVatValue.setValue("0");
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue("0");
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue("0");
		tfversionNo.setReadOnly(false);
		tfversionNo.setValue("0");
		tfquoteNum.setValue("");
		tfPaymentTerms.setValue("");
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue("0");
		tfOtherValue.setReadOnly(false);
		tfOtherValue.setValue("0");
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue("");
		// tfFreightPer.setValue("0");
		tfFreightValue.setReadOnly(false);
		tfFreightValue.setValue("0");
		tfFreightTerms.setValue("");
		// tfOtherPer.setValue("0");
		// tfpackingPer.setValue("0");
		cbMaterial.setValue(null);
		cbStatus.setValue(null);
		dfPODt.setValue(new Date());
		dfExpDt.setValue(new Date());
		taRemark.setValue("");
		cbBranch.setComponentError(null);
		ckPdcRqu.setValue(false);
		listPODetails = new ArrayList<MmsPoDtlDM>();
		tblPODetails.removeAllItems();
		new UploadDocumentUI(hlPODoc);
		cbpoType.setValue(null);
		cbVendor.setValue(null);
		taInvoiceOrd.setValue("");
		taShpnAddr.setValue("");
		ckcasePO.setValue(false);
		cbpoType.setComponentError(null);
		tfquoteNum.setComponentError(null);
		cbMaterial.setContainerDataSource(null);
		cbBranch.setValue(branchId);
	}
	
	private void resetDetailsFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMaterial.setValue(null);
		cbMaterial.setComponentError(null);
		tfPOQnty.setReadOnly(false);
		tfPOQnty.setValue("0");
		tfPOQnty.setReadOnly(true);
		tfPOQnty.setComponentError(null);
		tfUnitRate.setValue("0");
		tfUnitRate.setComponentError(null);
		cbMatUom.setReadOnly(false);
		cbMatUom.setValue(null);
		cbMatUom.setReadOnly(true);
		tfBasicValue.setReadOnly(false);
		tfBasicValue.setValue("0");
		tfBasicValue.setReadOnly(true);
		tfBasicValue.setComponentError(null);
		cbMatUom.setComponentError(null);
		taPODtlRemark.setValue("");
		cbPODtlStatus.setValue(cbPODtlStatus.getItemIds().iterator().next());
		btnsavepurQuote.setCaption("Add");
	}
	
	private BigDecimal gerPercentageValue(BigDecimal percent, BigDecimal value) {
		return (percent.multiply(value).divide(new BigDecimal("100"))).setScale(2, RoundingMode.CEILING);
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
		BigDecimal cstval = gerPercentageValue(new BigDecimal(tfCstPer.getValue()), subtotal);
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue(cstval.toString());
		tfCstValue.setReadOnly(true);
		BigDecimal csttotal = vatvalue.add(cstval);
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
			System.out.println("poid-->" + poid);
			parameterMap.put("ENQID", poId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/mmspo"); // productlist is the name of my jasper
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
	
	private void deleteDetails() {
		MmsPoDtlDM save = new MmsPoDtlDM();
		if (tblPODetails.getValue() != null) {
			save = beanpodtl.getItem(tblPODetails.getValue()).getBean();
			listPODetails.remove(save);
			resetDetailsFields();
			loadPODetails();
			btndelete.setEnabled(false);
		}
	}
	
	private void calculateBasicvalue() {
		// TODO Auto-generated method stub
		try {
			tfBasicValue.setReadOnly(false);
			tfBasicValue.setValue((new BigDecimal(tfPOQnty.getValue())).multiply(new BigDecimal(tfUnitRate.getValue()))
					.toString());
			tfBasicValue.setReadOnly(true);
		}
		catch (Exception e) {
		}
	}
}