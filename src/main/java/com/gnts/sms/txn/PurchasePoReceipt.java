/**
 * File Name 		: PurchasePoReceipt.java 
 * Description 		: this class is used for add/edit PurchasePoReceipt details. 
 * Author 			: GANGA S 
 * Date 			: Sep 29, 2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	  Modified By               Remarks
 * 0.1          Sep 29, 2014        	GANGA S 		      Initial Version
 */
package com.gnts.sms.txn;

import java.io.File;
import java.io.FileInputStream;
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
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.sms.domain.txn.PurPoReceiptDtlDM;
import com.gnts.sms.domain.txn.PurPoReceiptsHdrDM;
import com.gnts.sms.domain.txn.PurchasePODtlDM;
import com.gnts.sms.domain.txn.PurchasePOHdrDM;
import com.gnts.sms.service.txn.PurPoReceiptDtlService;
import com.gnts.sms.service.txn.PurPoReceiptsHdrService;
import com.gnts.sms.service.txn.PurchasePODtlService;
import com.gnts.sms.service.txn.PurchasePOHdrService;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class PurchasePoReceipt extends BaseUI {
	// Bean Creation
	private PurPoReceiptsHdrService servicePurPoReceiptHdr = (PurPoReceiptsHdrService) SpringContextHelper
			.getBean("purporeceipthdr");
	private PurPoReceiptDtlService servicePurPoReceiptDtl = (PurPoReceiptDtlService) SpringContextHelper
			.getBean("purporeceiptDtl");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private PurchasePOHdrService servicepurchaePOHdr = (PurchasePOHdrService) SpringContextHelper
			.getBean("PurchasePOhdr");
	private PurchasePODtlService servicePurchasePODtl = (PurchasePODtlService) SpringContextHelper
			.getBean("PurchasePODtl");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private List<PurPoReceiptDtlDM> receiptDtlList = null;
	private VerticalLayout hlevddDoc = new VerticalLayout();
	private VerticalLayout hlrefDoc = new VerticalLayout();
	// form layout for input controls
	private FormLayout flHdr1, flHdr2, flHdr3, flHdr4, flDtl1, flDtl4, flDtl5, flDtl6, flDtl2, flDtl3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Purchase Enquire Details
	private Button btnadd = new GERPButton("Add", "addbt", this);
	// User Input Fields for Po Receipt Header
	private ComboBox cbBranch, cbHdrStatus, cbPoNo;
	private TextField tfLotNo, tfVenorDcNo, tfvenInvNo, tfDocType;
	private TextArea taReceiptRemark;
	private PopupDateField dfReceiptDt, dfDocDt, dfInvDt;
	private CheckBox ckBillRaised;
	// User Input Fields for Po Receipt Detail
	private ComboBox cbProduct, cbUom, cbDtlStatus;
	private TextField tfrecpQty, tfrejQty;
	private TextArea taRejectReason;
	private Table tblReceiptDtl = new GERPTable();
	// Bean Container
	private BeanItemContainer<PurPoReceiptsHdrDM> beanPurPoReceiptHdrDM = null;
	private BeanItemContainer<PurPoReceiptDtlDM> beanPurPoReceiptDtlDM = null;
	// local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	private Long receiptId;
	private Long branchId;
	private Long employeeId;
	private Long moduleId;
	private Long roleId, appScreenId;
	private String receiptid;
	private SmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	// Initialize logger
	private Logger logger = Logger.getLogger(PurchaseEnquiry.class);
	private String status;
	private static final long serialVersionUID = 1L;
	private Button btndelete = new GERPButton("Delete", "delete", this);
	
	public PurchasePoReceipt() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PurchasePoReceipt() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting PurchaseEnquiry UI");
		// Initialization for Purchase Order Receipts Details user input components
		cbPoNo = new GERPComboBox("PO No.");
		cbPoNo.setItemCaptionPropertyId("pono");
		loadPoNo();
		cbPoNo.addValueChangeListener(new Property.ValueChangeListener() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbPoNo.getItem(itemId);
				if (item != null) {
					loadProduct();
					savePODetails();
				}
			}
		});
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		dfReceiptDt = new GERPPopupDateField("Receipt Date");
		dfReceiptDt.setInputPrompt("Select Date");
		tfDocType = new TextField("Document Type");
		tfLotNo = new TextField("LotNo.");
		tfVenorDcNo = new TextField("Vendor Doc No.");
		dfDocDt = new GERPPopupDateField("Vendor Document Date");
		dfDocDt.setInputPrompt("Select Date");
		tfvenInvNo = new TextField("Vendor Invoice No.");
		dfInvDt = new GERPPopupDateField("Vendor Invoice Date");
		dfInvDt.setInputPrompt("Select Date");
		taReceiptRemark = new GERPTextArea("Remarks");
		taReceiptRemark.setHeight("30");
		taReceiptRemark.setWidth("150");
		ckBillRaised = new CheckBox("BillRaised");
		// Receipt Detail
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("productName");
		cbProduct.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbProduct.getValue() != null) {
					tfrecpQty.setReadOnly(false);
					tfrecpQty.setValue(((PurchasePODtlDM) cbProduct.getValue()).getPoQty() + "");
					tfrecpQty.setReadOnly(true);
					cbUom.setReadOnly(false);
					cbUom.setValue(((PurchasePODtlDM) cbProduct.getValue()).getMaterialUom() + "");
					cbUom.setReadOnly(true);
				}
			}
		});
		cbUom = new ComboBox();
		cbUom.setItemCaptionPropertyId("lookupname");
		cbUom.setWidth("77");
		cbUom.setHeight("23");
		loadUomList();
		tfrecpQty = new TextField();
		tfrecpQty.setValue("0");
		tfrecpQty.setValue("0");
		tfrecpQty.setWidth("78");
		tfrejQty = new TextField();
		tfrejQty.setValue("0");
		tfrejQty.setValue("0");
		tfrejQty.setWidth("90");
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_PO_RECEIPTS_DTL, BASEConstants.RP_STATUS);
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
		try {
			ApprovalSchemaDM obj = servicePurPoReceiptHdr.getReviewerId(companyid, appScreenId, branchId, roleId)
					.get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbHdrStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WO_PLAN_HDR, BASEConstants.REVIEWER_LVL);
			} else {
				cbHdrStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WO_PLAN_HDR, BASEConstants.APPROVE_LVL);
			}
		}
		catch (Exception e) {
		}
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WO_PLAN_HDR, BASEConstants.APPROVE_LVL);
		taRejectReason = new TextArea("Reject Reason");
		taRejectReason.setWidth("150");
		taRejectReason.setHeight("30");
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadReceiptDtl();
		btnadd.setStyleName("add");
		btnadd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (dtlValidation()) {
					saveReceiptDtl();
				}
			}
		});
		btndelete.setEnabled(false);
		// ClickListener for Receipt Detail Tale
		tblReceiptDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblReceiptDtl.isSelected(event.getItemId())) {
					tblReceiptDtl.setImmediate(true);
					btnadd.setCaption("Add");
					btnadd.setStyleName("savebt");
					btndelete.setEnabled(false);
					receiptResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnadd.setCaption("Update");
					btnadd.setStyleName("savebt");
					btndelete.setEnabled(true);
					editReceiptDetail();
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
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flHdr1 = new FormLayout();
		flHdr2 = new FormLayout();
		flHdr3 = new FormLayout();
		flHdr1.addComponent(cbBranch);
		flHdr2.addComponent(tfLotNo);
		flHdr3.addComponent(cbHdrStatus);
		hlSearchLayout.addComponent(flHdr1);
		hlSearchLayout.addComponent(flHdr2);
		hlSearchLayout.addComponent(flHdr3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlUserInputLayout.removeAllComponents();
		flHdr1 = new FormLayout();
		flHdr2 = new FormLayout();
		flHdr3 = new FormLayout();
		flHdr4 = new FormLayout();
		flHdr1.addComponent(cbBranch);
		flHdr1.addComponent(cbPoNo);
		flHdr1.addComponent(dfReceiptDt);
		flHdr1.addComponent(tfDocType);
		flHdr2.addComponent(tfLotNo);
		flHdr2.addComponent(tfVenorDcNo);
		flHdr2.addComponent(dfDocDt);
		flHdr2.addComponent(tfvenInvNo);
		flHdr3.addComponent(dfInvDt);
		flHdr3.addComponent(taReceiptRemark);
		flHdr3.addComponent(cbHdrStatus);
		flHdr3.addComponent(ckBillRaised);
		flHdr4.addComponent(hlrefDoc);
		HorizontalLayout hlHDR = new HorizontalLayout();
		hlHDR.addComponent(flHdr1);
		hlHDR.addComponent(flHdr2);
		hlHDR.addComponent(flHdr3);
		hlHDR.addComponent(flHdr4);
		hlHDR.setSpacing(true);
		hlHDR.setMargin(true);
		// Adding Purchase Order Receipt Dtl components
		// Add components for User Input Layout
		flDtl1 = new FormLayout();
		flDtl2 = new FormLayout();
		flDtl3 = new FormLayout();
		flDtl4 = new FormLayout();
		flDtl5 = new FormLayout();
		flDtl6 = new FormLayout();
		flDtl1.addComponent(cbProduct);
		HorizontalLayout hlRecQtyUom = new HorizontalLayout();
		hlRecQtyUom.addComponent(tfrecpQty);
		hlRecQtyUom.addComponent(cbUom);
		hlRecQtyUom.setCaption("Recepit Qty");
		flDtl1.addComponent(hlRecQtyUom);
		flDtl1.setComponentAlignment(hlRecQtyUom, Alignment.TOP_LEFT);
		HorizontalLayout hlRejQtyUom = new HorizontalLayout();
		hlRejQtyUom.addComponent(tfrejQty);
		hlRejQtyUom.setCaption("Reject Qty");
		flDtl2.addComponent(hlRejQtyUom);
		flDtl2.setComponentAlignment(hlRejQtyUom, Alignment.TOP_LEFT);
		flDtl3.addComponent(cbDtlStatus);
		flDtl3.addComponent(taRejectReason);
		flDtl3.addComponent(btnadd);
		flDtl3.addComponent(btndelete);
		flDtl4.addComponent(hlevddDoc);
		flDtl4.setHeight("20");
		HorizontalLayout hldTL = new HorizontalLayout();
		hldTL.addComponent(flDtl1);
		hldTL.addComponent(flDtl2);
		hldTL.addComponent(flDtl3);
		hldTL.addComponent(flDtl4);
		hldTL.addComponent(flDtl5);
		hldTL.addComponent(flDtl6);
		hldTL.setSpacing(true);
		hldTL.setMargin(true);
		VerticalLayout vlHDR = new VerticalLayout();
		vlHDR = new VerticalLayout();
		vlHDR.addComponent(hldTL);
		vlHDR.addComponent(tblReceiptDtl);
		vlHDR.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlHDR, "PO Receipt Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		VerticalLayout vlHdrdTL = new VerticalLayout();
		vlHdrdTL = new VerticalLayout();
		vlHdrdTL.addComponent(GERPPanelGenerator.createPanel(hlHDR));
		vlHdrdTL.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		vlHdrdTL.setSpacing(true);
		vlHdrdTL.setWidth("100%");
		hlUserInputLayout.addComponent(vlHdrdTL);
		hlUserInputLayout.setSizeFull();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	// Load Receipt Header
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<PurPoReceiptsHdrDM> receiptHdrList = new ArrayList<PurPoReceiptsHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbBranch.getValue() + ", " + cbPoNo.getValue());
		Long poNo = null;
		if (cbPoNo.getValue() != null) {
			poNo = (((PurchasePOHdrDM) cbPoNo.getValue()).getPoId());
		}
		receiptHdrList = servicePurPoReceiptHdr.getPurPoReceiptsHdrList(companyid, null, poNo,
				(Long) cbBranch.getValue(), (String) cbHdrStatus.getValue(), tfLotNo.getValue(), "f");
		recordCnt = receiptHdrList.size();
		beanPurPoReceiptHdrDM = new BeanItemContainer<PurPoReceiptsHdrDM>(PurPoReceiptsHdrDM.class);
		beanPurPoReceiptHdrDM.addAll(receiptHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the PurchaseEnquiry. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanPurPoReceiptHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "receiptId", "branchName", "lotNo", "projectStatus",
				"lastUpdtDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setPageLength(15);
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Lot No.", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("receiptId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Load Receipt Detail
	private void loadReceiptDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordCnt = receiptDtlList.size();
			beanPurPoReceiptDtlDM = new BeanItemContainer<PurPoReceiptDtlDM>(PurPoReceiptDtlDM.class);
			beanPurPoReceiptDtlDM.addAll(receiptDtlList);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the dPurDt. result set");
			tblReceiptDtl.setContainerDataSource(beanPurPoReceiptDtlDM);
			tblReceiptDtl.setVisibleColumns(new Object[] { "productName", "receiptQty", "rejectQty", "productUom",
					"lastupdateddt", "lastupdatedby" });
			tblReceiptDtl.setColumnHeaders(new String[] { "Product Name", "Receipt Qty", "Reject Qty", "Product Uom",
					"Last Updated Date", "Last Updated By" });
			tblReceiptDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
			tblReceiptDtl.setPageLength(6);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Branch List
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
	
	// Load Product List
	private void loadProduct() {
		try {
			BeanItemContainer<PurchasePODtlDM> beanPlnDtl = new BeanItemContainer<PurchasePODtlDM>(
					PurchasePODtlDM.class);
			beanPlnDtl.addAll(servicePurchasePODtl.getPurchaseOrdDtlList(null,
					((PurchasePOHdrDM) cbPoNo.getValue()).getPoId(), null, null));
			cbProduct.setContainerDataSource(beanPlnDtl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void savePODetails() {
		receiptDtlList = new ArrayList<PurPoReceiptDtlDM>();
		for (PurchasePODtlDM purchasePOrecDtlDM : servicePurchasePODtl.getPurchaseOrdDtlList(null,
				((PurchasePOHdrDM) cbPoNo.getValue()).getPoId(), null, null)) {
			PurPoReceiptDtlDM purchasePODtlDM = new PurPoReceiptDtlDM();
			purchasePODtlDM.setProductId(purchasePOrecDtlDM.getProductId());
			purchasePODtlDM.setProductName(purchasePOrecDtlDM.getProductName());
			purchasePODtlDM.setReceiptQty(purchasePOrecDtlDM.getPoQty());
			purchasePODtlDM.setProductUom(purchasePOrecDtlDM.getMaterialUom());
			purchasePODtlDM.setRecpDtlStatus("Active");
			purchasePODtlDM.setLastupdatedby(username);
			purchasePODtlDM.setLastupdateddt(DateUtils.getcurrentdate());
			receiptDtlList.add(purchasePODtlDM);
		}
		loadReceiptDtl();
	}
	
	private void loadPoNo() {
		BeanItemContainer<PurchasePOHdrDM> beanPurPoDM = new BeanItemContainer<PurchasePOHdrDM>(PurchasePOHdrDM.class);
		beanPurPoDM.addAll(servicepurchaePOHdr.getPurchaseOrdHdrList(companyid, branchId, null, null, null));
		cbPoNo.setContainerDataSource(beanPurPoDM);
	}
	
	// Method to edit the values from table into fields to update process
	private void editReceiptHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			PurPoReceiptsHdrDM editReceiptHdrlist = beanPurPoReceiptHdrDM.getItem(tblMstScrSrchRslt.getValue())
					.getBean();
			receiptId = editReceiptHdrlist.getReceiptId();
			cbBranch.setValue(editReceiptHdrlist.getBranchId());
			tfLotNo.setReadOnly(false);
			tfLotNo.setValue(editReceiptHdrlist.getLotNo());
			tfLotNo.setReadOnly(true);
			cbPoNo.setValue(editReceiptHdrlist.getPoId());
			if (editReceiptHdrlist.getReceiptDate() != null) {
				dfReceiptDt.setValue(editReceiptHdrlist.getReceiptDate());
			}
			if (editReceiptHdrlist.getVendordcNo() != null) {
				tfVenorDcNo.setValue(editReceiptHdrlist.getVendordcNo());
			}
			if (editReceiptHdrlist.getVendorDate() != null) {
				dfDocDt.setValue(editReceiptHdrlist.getVendorDate());
			}
			if (editReceiptHdrlist.getReceiptdocType() != null) {
				tfDocType.setValue(editReceiptHdrlist.getReceiptdocType());
			}
			if (editReceiptHdrlist.getVendorinvoiceNo() != null) {
				tfvenInvNo.setValue(editReceiptHdrlist.getVendorinvoiceNo());
			}
			if (editReceiptHdrlist.getVendorinvoiceDate() != null) {
				dfInvDt.setValue(editReceiptHdrlist.getVendorinvoiceDate());
			}
			if (editReceiptHdrlist.getReceiptRemark() != null) {
				taReceiptRemark.setValue(editReceiptHdrlist.getReceiptRemark());
			}
			if (editReceiptHdrlist.getBillraisedYN().equals("Y")) {
				ckBillRaised.setValue(true);
			} else {
				ckBillRaised.setValue(false);
			}
			Long uom = editReceiptHdrlist.getPoId();
			Collection<?> uomid = cbPoNo.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbPoNo.getItem(itemId);
				// Get the actual bean and use the data
				PurchasePOHdrDM st = (PurchasePOHdrDM) item.getBean();
				if (uom != null && uom.equals(st.getPoId())) {
					cbPoNo.setValue(itemId);
				}
			}
			cbHdrStatus.setValue(editReceiptHdrlist.getProjectStatus());
			if (editReceiptHdrlist.getVendorrefDoc() != null) {
				byte[] certificate = editReceiptHdrlist.getVendorrefDoc();
				UploadDocumentUI test = new UploadDocumentUI(hlrefDoc);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(hlrefDoc);
			}
			receiptDtlList = servicePurPoReceiptDtl.getsaveReceiptDtlList(null, null, null, receiptId);
		}
		loadReceiptDtl();
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, receiptId, null, null, null, null,
				null, status);
		comments.loadsrch(true, null, null, null, null, null, receiptId, null, null, null, null, null, null);
	}
	
	private void editReceiptDetail() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblReceiptDtl.getValue() != null) {
			PurPoReceiptDtlDM purPoReceiptDtlDM = beanPurPoReceiptDtlDM.getItem(tblReceiptDtl.getValue()).getBean();
			Long poid = purPoReceiptDtlDM.getProductId();
			Collection<?> poids = cbProduct.getItemIds();
			for (Iterator<?> iterator = poids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
				// Get the actual bean and use the data
				PurchasePODtlDM st = (PurchasePODtlDM) item.getBean();
				if (poid != null && poid.equals(st.getProductId())) {
					cbProduct.setValue(itemId);
				}
			}
			cbUom.setValue(purPoReceiptDtlDM.getProductUom());
			if (purPoReceiptDtlDM.getReceiptQty() != null) {
				tfrecpQty.setValue(purPoReceiptDtlDM.getReceiptQty().toString());
			}
			if (purPoReceiptDtlDM.getRejectQty() != null) {
				tfrejQty.setValue(purPoReceiptDtlDM.getRejectQty().toString());
			}
			if (purPoReceiptDtlDM.getReceiptEvd() != null) {
				byte[] certificate = purPoReceiptDtlDM.getReceiptEvd();
				UploadDocumentUI test = new UploadDocumentUI(hlevddDoc);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(hlevddDoc);
			}
			if (purPoReceiptDtlDM.getRejectReason() != null) {
				taRejectReason.setValue(purPoReceiptDtlDM.getRejectReason().toString());
			}
			cbDtlStatus.setValue(purPoReceiptDtlDM.getRecpDtlStatus());
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
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		tfLotNo.setValue("");
		cbPoNo.setValue(null);
		cbBranch.setValue(null);
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleInputUserLayout();
		// reset the input controls to default value
		new UploadDocumentUI(hlevddDoc);
		new UploadDocumentUI(hlrefDoc);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnadd.setCaption("Add");
		tblReceiptDtl.setVisible(true);
		cbBranch.setRequired(true);
		cbProduct.setRequired(true);
		cbPoNo.setRequired(true);
		// tfrejQty.setRequired(true);
		loadReceiptDtl();
		resetFields();
		tfLotNo.setReadOnly(true);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_LOTNO ").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfLotNo.setReadOnly(true);
			} else {
				tfLotNo.setReadOnly(false);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null);
	}
	
	@Override
	protected void editDetails() {
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleInputUserLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblReceiptDtl.setVisible(true);
		cbBranch.setRequired(true);
		cbProduct.setRequired(true);
		cbPoNo.setRequired(true);
		editReceiptDetail();
		editReceiptHdr();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbBranch.setComponentError(null);
		cbPoNo.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if (cbPoNo.getValue() == null) {
			cbPoNo.setComponentError(new UserError(GERPErrorCodes.PURCAHSE_ORD_NO));
			errorFlag = true;
		}
		if (taRejectReason.getValue() == null) {
			taRejectReason.setComponentError(new UserError(GERPErrorCodes.REGECT_REASON));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean dtlValidation() {
		boolean isValid = true;
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			cbProduct.setComponentError(null);
		}
		if (Long.valueOf(tfrecpQty.getValue()) < 0) {
			tfrecpQty.setComponentError(new UserError(GERPErrorCodes.RECEIPT_QTY));
			isValid = false;
		} else {
			tfrecpQty.setComponentError(null);
		}
		if (Long.valueOf(tfrejQty.getValue()) < 0) {
			tfrejQty.setComponentError(new UserError(GERPErrorCodes.REGECT_QTY));
			isValid = false;
		} else {
			tfrejQty.setComponentError(null);
		}
		if (taRejectReason.getValue() == null) {
			taRejectReason.setComponentError(new UserError(GERPErrorCodes.REGECT_REASON));
			isValid = false;
		} else {
			taRejectReason.setComponentError(null);
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			PurPoReceiptsHdrDM receiptobj = new PurPoReceiptsHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				receiptobj = beanPurPoReceiptHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				receiptobj.setLotNo(tfLotNo.getValue());
			} else {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_LOTNO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						receiptobj.setLotNo(slnoObj.getKeyDesc());
					}
				}
				catch (Exception e) {
				}
			}
			receiptobj.setCompanyId(companyid);
			if (cbPoNo.getValue() != null) {
				receiptobj.setPoId(((PurchasePOHdrDM) cbPoNo.getValue()).getPoId());
			}
			receiptobj.setBranchId((Long) cbBranch.getValue());
			receiptobj.setReceiptDate((Date) dfReceiptDt.getValue());
			if (tfDocType.getValue() != null) {
				receiptobj.setReceiptdocType(tfDocType.getValue().toString());
			}
			receiptobj.setVendordcNo((tfVenorDcNo.getValue()));
			receiptobj.setVendorDate((Date) dfDocDt.getValue());
			receiptobj.setVendorinvoiceNo(tfvenInvNo.getValue().toString());
			receiptobj.setVendorinvoiceDate((Date) dfInvDt.getValue());
			receiptobj.setReceiptRemark((taReceiptRemark.getValue()));
			if (ckBillRaised.getValue().equals(true)) {
				receiptobj.setBillraisedYN("Y");
			} else if (ckBillRaised.getValue().equals(false)) {
				receiptobj.setBillraisedYN("N");
			}
			receiptobj.setProjectStatus(cbHdrStatus.getValue().toString());
			receiptobj.setPreparedBy(employeeId);
			receiptobj.setReviewedBy(null);
			receiptobj.setActionedBy(null);
			receiptobj.setLastUpdtDate(DateUtils.getcurrentdate());
			receiptobj.setLastUpdatedBy(username);
			File file = new File(GERPConstants.DOCUMENT_PATH);
			byte fileContent[] = new byte[(int) file.length()];
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContent);
			fio.close();
			receiptobj.setVendorrefDoc(fileContents);
			servicePurPoReceiptHdr.savePurPoReceiptDetails(receiptobj);
			@SuppressWarnings("unchecked")
			Collection<PurPoReceiptDtlDM> itemIds = (Collection<PurPoReceiptDtlDM>) tblReceiptDtl.getVisibleItemIds();
			for (PurPoReceiptDtlDM save : (Collection<PurPoReceiptDtlDM>) itemIds) {
				save.setRecepId(Long.valueOf(receiptobj.getReceiptId().toString()));
				servicePurPoReceiptDtl.saveReceiptDtlDetails(save);
			}
			comments.saveReceipt(receiptobj.getReceiptId(), receiptobj.getProjectStatus());
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_LOTNO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_LOTNO");
					}
				}
				catch (Exception e) {
				}
			}
			receiptResetFields();
			loadSrchRslt();
			receiptId = 0L;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveReceiptDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			PurPoReceiptDtlDM purReceiptDtlObj = new PurPoReceiptDtlDM();
			if (tblReceiptDtl.getValue() != null) {
				purReceiptDtlObj = beanPurPoReceiptDtlDM.getItem(tblReceiptDtl.getValue()).getBean();
			}
			purReceiptDtlObj.setProductId(((PurchasePODtlDM) cbProduct.getValue()).getProductId());
			purReceiptDtlObj.setProductName(((PurchasePODtlDM) cbProduct.getValue()).getProductName());
			purReceiptDtlObj.setProductUom(cbUom.getValue().toString());
			if (tfrejQty.getValue() != null && tfrejQty.getValue().trim().length() > 0) {
				purReceiptDtlObj.setRejectQty(Long.valueOf(tfrejQty.getValue()));
			}
			purReceiptDtlObj.setRejectReason(taRejectReason.getValue().toString());
			if (tfrecpQty.getValue() != null && tfrecpQty.getValue().trim().length() > 0) {
				purReceiptDtlObj.setReceiptQty(Long.valueOf(tfrecpQty.getValue()));
			}
			if (cbDtlStatus.getValue() != null) {
				purReceiptDtlObj.setRecpDtlStatus((cbDtlStatus.getValue().toString()));
			}
			purReceiptDtlObj.setLastupdateddt(DateUtils.getcurrentdate());
			purReceiptDtlObj.setLastupdatedby(username);
			File file = new File(GERPConstants.DOCUMENT_PATH);
			byte fileContent[] = new byte[(int) file.length()];
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContent);
			fio.close();
			purReceiptDtlObj.setReceiptEvd(fileContents);
			receiptDtlList.add(purReceiptDtlObj);
			loadReceiptDtl();
			receiptResetFields();
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
		UI.getCurrent().getSession().setAttribute("audittablepk", receiptid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblReceiptDtl.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		tfLotNo.setReadOnly(false);
		cbBranch.setRequired(false);
		receiptResetFields();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		cbBranch.setValue(null);
		receiptDtlList = new ArrayList<PurPoReceiptDtlDM>();
		tblReceiptDtl.removeAllItems();
		cbBranch.setValue(branchId);
		cbBranch.setComponentError(null);
		tfDocType.setValue("");
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		cbPoNo.setValue(null);
		cbPoNo.setComponentError(null);
		tfLotNo.setValue("");
		tfvenInvNo.setValue("");
		tfVenorDcNo.setValue("");
		taReceiptRemark.setValue("");
		dfDocDt.setValue(null);
		dfInvDt.setValue(null);
		dfReceiptDt.setValue(new Date());
		new UploadDocumentUI(hlevddDoc);
		new UploadDocumentUI(hlrefDoc);
		cbProduct.setContainerDataSource(null);
	}
	
	private void deleteDetails() {
		PurPoReceiptDtlDM save = new PurPoReceiptDtlDM();
		if (tblReceiptDtl.getValue() != null) {
			save = beanPurPoReceiptDtlDM.getItem(tblReceiptDtl.getValue()).getBean();
			receiptDtlList.remove(save);
			receiptResetFields();
			loadReceiptDtl();
			btndelete.setEnabled(false);
		}
	}
	
	private void receiptResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbProduct.setValue(null);
		taRejectReason.setValue("");
		tfrecpQty.setReadOnly(false);
		tfrecpQty.setValue("0");
		tfrecpQty.setReadOnly(true);
		tfrecpQty.setComponentError(null);
		tfrejQty.setValue("0");
		tfrejQty.setComponentError(null);
		cbDtlStatus.setValue(null);
		cbUom.setReadOnly(false);
		cbUom.setValue(null);
		cbUom.setReadOnly(true);
		cbProduct.setComponentError(null);
		new UploadDocumentUI(hlevddDoc);
		btnadd.setCaption("Add");
	}
}
