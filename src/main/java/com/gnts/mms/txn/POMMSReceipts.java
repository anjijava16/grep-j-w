/**
 * File Name 		: POMMSReceipts.java 
 * Description 		: this class is used for add/edit POMMSReceipts details. 
 * Author 			: Karthikeyan R
 * Date 			: Oct 18, 2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	  Modified By               Remarks
 * 0.1          Oct 18, 2014          Karthikeyan R 		    Initial Version
 */
package com.gnts.mms.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.txn.MaterialLedgerDM;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.domain.txn.MmsPoDtlDM;
import com.gnts.mms.domain.txn.POHdrDM;
import com.gnts.mms.domain.txn.PoReceiptDtlDM;
import com.gnts.mms.domain.txn.PoReceiptHdrDM;
import com.gnts.mms.service.txn.MaterialLedgerService;
import com.gnts.mms.service.txn.MaterialStockService;
import com.gnts.mms.service.txn.MmsPoDtlService;
import com.gnts.mms.service.txn.POHdrService;
import com.gnts.mms.service.txn.PoReceiptDtlService;
import com.gnts.mms.service.txn.PoReceiptHdrService;
import com.gnts.sms.txn.PurchaseEnquiry;
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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class POMMSReceipts extends BaseTransUI {
	// Bean Creation
	private PoReceiptHdrService servicePoReceiptHdr = (PoReceiptHdrService) SpringContextHelper.getBean("poRecepitHdr");
	private PoReceiptDtlService servicePoReceiptDtl = (PoReceiptDtlService) SpringContextHelper.getBean("poRecepitDtl");
	private MaterialLedgerService serviceLedger = (MaterialLedgerService) SpringContextHelper.getBean("materialledger");
	private MaterialStockService serviceMaterialStock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private POHdrService serviceMMSPOHdr = (POHdrService) SpringContextHelper.getBean("pohdr");
	private MmsPoDtlService serviceMMSPODtls = (MmsPoDtlService) SpringContextHelper.getBean("mmspoDtl");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	// Parent layout for all the input controls
	private HorizontalLayout hlSearchLayout;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout hlevdDoc = new VerticalLayout();
	private VerticalLayout hlrefDoc = new VerticalLayout();
	// User Input Components for Purchase Enquire Details
	private Button btnadd = new GERPButton("Add", "addbt", this);
	// POHdr Recepits Components
	private GERPComboBox cbBranch, cbDocType, cbHdrStatus, cbIndentNo, cbPoNo;
	private GERPTextField tfLotNo, tfVendorDcNo, tfVenInvNo;
	private TextArea taRecepitRemark;
	private PopupDateField dfReceiptDate, dfVendorDocDt, dfVendorInvDt;
	private CheckBox chkBillRaised;
	private FormLayout flHdr1, flHdr2, flHdr3, flHdr4;
	// PODtl Recepits Components
	private ComboBox cbMaterial, cbDtlStatus;
	private TextField tfAcceptQty, tfPOQty, tfRejectqty, tfMaterialUOM;
	private TextArea taRejectReason;
	private Table tblReceiptDtl = new GERPTable();
	private List<PoReceiptDtlDM> listPOReceipts = null;
	private FormLayout flDtl1, flDtl2, flDtl3, flDtl4;
	// Bean Container
	private BeanItemContainer<PoReceiptHdrDM> beanPoReceiptHdrDM = null;
	private BeanItemContainer<PoReceiptDtlDM> beanPoReceiptDtlDM = null;
	private Button btndelete = new GERPButton("Delete", "delete", this);
	// local variables declaration
	private Long companyid, enquiryId, receiptId, employeeId, branchId;
	private int recordCnt = 0;
	private String username, lotNo;
	// Initialize logger
	private Logger logger = Logger.getLogger(PurchaseEnquiry.class);
	private static final long serialVersionUID = 1L;
	
	public POMMSReceipts() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PurchasePoReceipt() constructor");
		// Loading the UI
		buildView();
	}
	
	// Build View
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting POMMSReceipts UI");
		// Initialization for Purchase Order Receipts Hdr Details user input components
		cbIndentNo = new GERPComboBox("Indent No");
		cbIndentNo.setItemCaptionPropertyId("indentNo");
		cbIndentNo.setWidth("150");
		cbPoNo = new GERPComboBox("PO No");
		cbPoNo.setItemCaptionPropertyId("pono");
		cbPoNo.setImmediate(true);
		loadPoNo();
		cbPoNo.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					loadMaterial();
					cbIndentNo.setImmediate(true);
					cbIndentNo.setValue(serviceMMSPOHdr
							.getPOHdrList(null, (Long) cbPoNo.getValue(), null, null, null, null, null, "F").get(0)
							.getIndentId());
				}
				catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
		});
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		dfReceiptDate = new GERPPopupDateField("Recepit Date");
		dfReceiptDate.setInputPrompt("Select Date");
		dfReceiptDate.setDateFormat("dd-MMM-yyyy");
		dfReceiptDate.setWidth("130");
		cbDocType = new GERPComboBox("Document Type");
		cbDocType.addItem("DC");
		cbDocType.addItem("Invoice");
		tfLotNo = new GERPTextField("LotNo.");
		tfVendorDcNo = new GERPTextField("Vendor Doc No.");
		dfVendorDocDt = new GERPPopupDateField("Vendor Document Date");
		dfVendorDocDt.setInputPrompt("Select Date");
		dfVendorDocDt.setDateFormat("dd-MMM-yyyy");
		tfVenInvNo = new GERPTextField("Vendor Invoice No.");
		dfVendorInvDt = new GERPPopupDateField("Vendor Invoice Date");
		dfVendorInvDt.setInputPrompt("Select Date");
		dfVendorInvDt.setDateFormat("dd-MMM-yyyy");
		taRecepitRemark = new GERPTextArea("Remarks");
		taRecepitRemark.setHeight("50");
		chkBillRaised = new CheckBox("Bill Raised?");
		// Receipt Detail
		cbMaterial = new GERPComboBox("Material Name");
		cbMaterial.setItemCaptionPropertyId("materialname");
		cbMaterial.setWidth("150");
		cbMaterial.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbMaterial.getValue() != null) {
					MmsPoDtlDM materialDM = (MmsPoDtlDM) cbMaterial.getValue();
					tfMaterialUOM.setValue(materialDM.getMaterialuom());
					if (materialDM.getPoqty() != null) {
						tfPOQty.setReadOnly(false);
						tfPOQty.setValue(materialDM.getReqQty().toString());
						tfPOQty.setReadOnly(true);
						tfMaterialUOM.setValue(materialDM.getMaterialuom().toString());
					}
				}
			}
		});
		tfMaterialUOM = new TextField();
		tfMaterialUOM.setWidth("40");
		tfAcceptQty = new GERPTextField("Accepted Qty");
		tfAcceptQty.setValue("0");
		tfAcceptQty.setWidth("150");
		tfAcceptQty.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (tfPOQty.getValue() != null) {
					if (Long.valueOf(tfAcceptQty.getValue()) > (Long.valueOf(tfPOQty.getValue()))) {
						Notification.show("Value Exceeds PO Quantity",
								"Accept value is larger than PO Quantity.Kindly refer PO Qty", Type.WARNING_MESSAGE);
						tfAcceptQty.setValue("0");
						cbDtlStatus.setValue(null);
					}
				}
			}
		});
		tfPOQty = new TextField();
		tfPOQty.setValue("0");
		tfPOQty.setWidth("110");
		tfRejectqty = new GERPTextField("Reject Qty");
		tfRejectqty.setValue("0");
		tfRejectqty.setWidth("150");
		tfRejectqty.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (tfPOQty.getValue() != null) {
					if (Long.valueOf(tfRejectqty.getValue()) + Long.valueOf(tfAcceptQty.getValue()) > Long
							.valueOf(tfPOQty.getValue())) {
						Notification.show("Value Exceeds PO Quantity",
								"Reject value is larger than PO Quantity.Kindly refer PO Qty", Type.WARNING_MESSAGE);
						tfRejectqty.setValue("0");
						cbDtlStatus.setValue(null);
					}
				}
			}
		});
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.T_MMS_PO_RECEIPTS_DTL, BASEConstants.PORECDTL_STATUS);
		cbDtlStatus.setRequired(true);
		cbDtlStatus.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbDtlStatus.getValue() != null) {
					if (cbDtlStatus.getValue().equals("Rejected")) {
						taRejectReason.setEnabled(true);
						taRejectReason.setRequired(true);
					} else if (cbDtlStatus.getValue().equals("Approved")) {
						taRejectReason.setEnabled(false);
					}
				}
				if ((Long.valueOf(tfAcceptQty.getValue()) + (Long.valueOf((tfRejectqty.getValue())))) != (Long
						.valueOf((tfPOQty.getValue())))) {
					Notification.show("Quantity Mismatch", "Sum of Accept and Reject Qty is not equal to Receipt Qty",
							Type.WARNING_MESSAGE);
					tfAcceptQty.setValue("0");
					tfRejectqty.setValue("0");
					cbDtlStatus.setValue(null);
				}
			}
		});
		cbDtlStatus.setWidth("150");
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.T_MMS_PO_RECEIPTS_HDR, BASEConstants.PORECT_STATUS);
		cbHdrStatus.setWidth("150");
		taRejectReason = new TextArea("Reject Reason");
		taRejectReason.setWidth("150");
		taRejectReason.setHeight("50");
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
		btndelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndelete == event.getButton()) {
					btnadd.setCaption("Add");
					deleteDetails();
				}
			}
		});
		// ClickListener for Receipt Detail Tale
		tblReceiptDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblReceiptDtl.isSelected(event.getItemId())) {
					tblReceiptDtl.setImmediate(true);
					btnadd.setCaption("Add");
					btnadd.setStyleName("savebt");
					receiptResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnadd.setCaption("Update");
					btnadd.setStyleName("savebt");
					editReceiptDetail();
				}
			}
		});
	}
	
	// Assemble Search Layout
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
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
	
	// Assemble Input User Layout
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlUserInputLayout.removeAllComponents();
		flHdr1 = new FormLayout();
		flHdr2 = new FormLayout();
		flHdr3 = new FormLayout();
		flHdr4 = new FormLayout();
		flHdr1.addComponent(cbPoNo);
		flHdr1.addComponent(cbBranch);
		flHdr1.addComponent(dfReceiptDate);
		flHdr1.addComponent(cbDocType);
		flHdr1.addComponent(tfLotNo);
		flHdr2.addComponent(dfVendorDocDt);
		flHdr2.addComponent(tfVendorDcNo);
		flHdr2.addComponent(cbIndentNo);
		flHdr2.addComponent(dfVendorInvDt);
		flHdr2.addComponent(tfVenInvNo);
		flHdr3.addComponent(taRecepitRemark);
		flHdr3.addComponent(cbHdrStatus);
		flHdr3.addComponent(chkBillRaised);
		flHdr4.addComponent(hlrefDoc);
		HorizontalLayout hlHdr = new HorizontalLayout();
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdr1);
		hlHdr.addComponent(flHdr2);
		hlHdr.addComponent(flHdr3);
		hlHdr.addComponent(flHdr4);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		new HorizontalLayout();
		// Adding Purchase Order Receipt Dtl components
		// Add components for User Input Layout
		flDtl1 = new FormLayout();
		flDtl2 = new FormLayout();
		flDtl3 = new FormLayout();
		flDtl4 = new FormLayout();
		flDtl1.addComponent(cbMaterial);
		HorizontalLayout qtyUom = new HorizontalLayout();
		qtyUom.addComponent(tfPOQty);
		qtyUom.addComponent(tfMaterialUOM);
		qtyUom.setCaption("Quanity");
		flDtl1.addComponent(qtyUom);
		flDtl2.addComponent(tfAcceptQty);
		flDtl2.addComponent(tfRejectqty);
		flDtl3.addComponent(taRejectReason);
		flDtl4.addComponent(cbDtlStatus);
		HorizontalLayout addDelete = new HorizontalLayout();
		addDelete.addComponent(btnadd);
		addDelete.addComponent(btndelete);
		flDtl4.addComponent(addDelete);
		HorizontalLayout hldTL = new HorizontalLayout();
		hldTL.addComponent(flDtl1);
		hldTL.addComponent(flDtl2);
		hldTL.addComponent(flDtl3);
		hldTL.addComponent(flDtl4);
		hldTL.setSpacing(true);
		hldTL.setMargin(true);
		VerticalLayout vlHDR = new VerticalLayout();
		vlHDR = new VerticalLayout();
		vlHDR.addComponent(hldTL);
		vlHDR.addComponent(tblReceiptDtl);
		vlHDR.setSpacing(true);
		VerticalLayout vlHdrdTL = new VerticalLayout();
		vlHdrdTL = new VerticalLayout();
		vlHdrdTL.addComponent(GERPPanelGenerator.createPanel(hlHdr));
		vlHdrdTL.addComponent(GERPPanelGenerator.createPanel(vlHDR));
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
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<PoReceiptHdrDM> listReceiptHdr = new ArrayList<PoReceiptHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + cbBranch.getValue() + ", " + cbPoNo.getValue());
			Long poNo = null;
			if (cbPoNo.getValue() != null) {
				poNo = (((POHdrDM) cbPoNo.getValue()).getPoId());
			}
			listReceiptHdr = servicePoReceiptHdr.getPoReceiptsHdrList(companyid, null, poNo,
					(Long) cbBranch.getValue(), (String) tfLotNo.getValue(), (String) cbHdrStatus.getValue(), "F");
			recordCnt = listReceiptHdr.size();
			beanPoReceiptHdrDM = new BeanItemContainer<PoReceiptHdrDM>(PoReceiptHdrDM.class);
			beanPoReceiptHdrDM.addAll(listReceiptHdr);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the PO Recepit. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanPoReceiptHdrDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "receiptId", "branchName", "lotNo", "projectStatus",
					"lastUpdtDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setPageLength(15);
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Lot No.", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("receiptId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Receipt Detail
	private void loadReceiptDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordCnt = listPOReceipts.size();
			beanPoReceiptDtlDM = new BeanItemContainer<PoReceiptDtlDM>(PoReceiptDtlDM.class);
			beanPoReceiptDtlDM.addAll(listPOReceipts);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the DtlRecDt. result set");
			tblReceiptDtl.setContainerDataSource(beanPoReceiptDtlDM);
			tblReceiptDtl.setVisibleColumns(new Object[] { "materialName", "receiptqty", "rejectqty", "materialUOM",
					"lastupdateddt", "lastupdatedby" });
			tblReceiptDtl.setColumnHeaders(new String[] { "Material Name", "Receipt Qty", "Reject Qty", "Material Uom",
					"Last Updated Date", "Last Updated By" });
			tblReceiptDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
			tblReceiptDtl.setPageLength(6);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Delete POMMSReceiptDetail
	private void deleteDetails() {
		try {
			PoReceiptDtlDM save = new PoReceiptDtlDM();
			if (tblReceiptDtl.getValue() != null) {
				save = beanPoReceiptDtlDM.getItem(tblReceiptDtl.getValue()).getBean();
				listPOReceipts.remove(save);
				receiptResetFields();
				loadReceiptDtl();
				btndelete.setEnabled(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Purchase order No
	private void loadPoNo() {
		try {
			BeanItemContainer<POHdrDM> beanPurPoDM = new BeanItemContainer<POHdrDM>(POHdrDM.class);
			beanPurPoDM.addAll(serviceMMSPOHdr.getPOHdrList(null, null, null, null, null, null, null, "P"));
			cbPoNo.setContainerDataSource(beanPurPoDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Material List
	private void loadMaterial() {
		try {
			BeanItemContainer<MmsPoDtlDM> beanProduct = new BeanItemContainer<MmsPoDtlDM>(MmsPoDtlDM.class);
			beanProduct.addAll(serviceMMSPODtls.getpodtllist(((POHdrDM) cbPoNo.getValue()).getPoId(), null, null, null,
					null, "F"));
			cbMaterial.setContainerDataSource(beanProduct);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
			logger.info(e.getMessage());
		}
	}
	
	// Method to edit the values from table into fields to update process
	private void editReceiptHdr() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected receiptId. Id -> " + enquiryId);
			if (tblMstScrSrchRslt.getValue() != null) {
				PoReceiptHdrDM poReceiptHdrDM = beanPoReceiptHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				receiptId = poReceiptHdrDM.getReceiptId();
				cbBranch.setValue(poReceiptHdrDM.getBranchId());
				tfLotNo.setReadOnly(false);
				tfLotNo.setValue(poReceiptHdrDM.getLotNo());
				tfLotNo.setReadOnly(true);
				cbPoNo.setValue(poReceiptHdrDM.getPoId());
				if (poReceiptHdrDM.getReceiptDate() != null) {
					dfReceiptDate.setValue(poReceiptHdrDM.getReceiptDate());
				}
				if (poReceiptHdrDM.getVendordcNo() != null) {
					tfVendorDcNo.setValue(poReceiptHdrDM.getVendordcNo());
				}
				if (poReceiptHdrDM.getVendorDate() != null) {
					dfVendorDocDt.setValue(poReceiptHdrDM.getVendorDate());
				}
				if (poReceiptHdrDM.getReceiptdocType() != null) {
					cbDocType.setValue(poReceiptHdrDM.getReceiptdocType());
				}
				if (poReceiptHdrDM.getIndentId() != null) {
					cbIndentNo.setValue(poReceiptHdrDM.getIndentId());
				}
				if (poReceiptHdrDM.getVendorinvoiceNo() != null) {
					tfVenInvNo.setValue(poReceiptHdrDM.getVendorinvoiceNo());
				}
				if (poReceiptHdrDM.getVendorinvoiceDate() != null) {
					dfVendorInvDt.setValue(poReceiptHdrDM.getVendorinvoiceDate());
				}
				if (poReceiptHdrDM.getReceiptRemark() != null) {
					taRecepitRemark.setValue(poReceiptHdrDM.getReceiptRemark());
				}
				if (poReceiptHdrDM.getBillraisedYN().equals("Y")) {
					chkBillRaised.setValue(true);
				} else {
					chkBillRaised.setValue(false);
				}
				Long poid = poReceiptHdrDM.getPoId();
				Collection<?> poids = cbPoNo.getItemIds();
				for (Iterator<?> iterator = poids.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbPoNo.getItem(itemId);
					// Get the actual bean and use the data
					POHdrDM st = (POHdrDM) item.getBean();
					if (poid != null && poid.equals(st.getPoId())) {
						cbPoNo.setValue(itemId);
					}
				}
				cbHdrStatus.setValue(poReceiptHdrDM.getProjectStatus());
				listPOReceipts = servicePoReceiptDtl.getPoReceiptDtlList(null, receiptId, null, null, null);
				loadReceiptDtl();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Edit Recepit Details
	private void editReceiptDetail() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Receipt.Id -> "
					+ enquiryId);
			if (tblReceiptDtl.getValue() != null) {
				PoReceiptDtlDM receiptDtlDM = beanPoReceiptDtlDM.getItem(tblReceiptDtl.getValue()).getBean();
				Long matid = receiptDtlDM.getMaterialid();
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
				tfMaterialUOM.setValue(receiptDtlDM.getMaterialUOM());
				if (receiptDtlDM.getReceiptqty() != null) {
					tfAcceptQty.setValue(receiptDtlDM.getReceiptqty().toString());
				}
				if (receiptDtlDM.getRejectqty() != null) {
					tfRejectqty.setValue(receiptDtlDM.getRejectqty().toString());
				}
				if (receiptDtlDM.getRejectreason() != null) {
					taRejectReason.setValue(receiptDtlDM.getRejectreason().toString());
				}
				cbDtlStatus.setValue(receiptDtlDM.getReceiptstatus());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Search Details
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
	
	// Reset Search Details
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbHdrStatus.setValue(null);
		cbHdrStatus.setRequired(false);
		tfLotNo.setValue("");
		cbPoNo.setValue(null);
		cbBranch.setValue(branchId);
		loadSrchRslt();
	}
	
	// Add Details
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleInputUserLayout();
		// reset the input controls to default value
		new UploadDocumentUI(hlevdDoc);
		new UploadDocumentUI(hlrefDoc);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnadd.setCaption("Add");
		tblReceiptDtl.setVisible(true);
		cbBranch.setRequired(true);
		cbMaterial.setRequired(true);
		cbPoNo.setRequired(true);
		dfReceiptDate.setRequired(true);
		dfVendorDocDt.setRequired(true);
		dfVendorInvDt.setRequired(true);
		cbHdrStatus.setRequired(true);
		tfAcceptQty.setRequired(true);
		tfRejectqty.setRequired(true);
		resetFields();
		tfLotNo.setReadOnly(false);
	}
	
	// Edit Details
	@Override
	protected void editDetails() {
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleInputUserLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblReceiptDtl.setVisible(true);
		if (tfLotNo.getValue() == null || tfLotNo.getValue().trim().length() == 0) {
			tfLotNo.setReadOnly(false);
		}
		cbBranch.setRequired(true);
		cbMaterial.setRequired(true);
		tfMaterialUOM.setRequired(true);
		cbPoNo.setRequired(true);
		cbHdrStatus.setRequired(true);
		dfReceiptDate.setRequired(true);
		dfVendorDocDt.setRequired(true);
		dfVendorInvDt.setRequired(true);
		tfAcceptQty.setRequired(true);
		tfRejectqty.setRequired(true);
		editReceiptDetail();
		editReceiptHdr();
	}
	
	// Validation Details
	@Override
	protected void validateDetails() throws ValidationException {
		cbBranch.setComponentError(null);
		cbPoNo.setComponentError(null);
		dfReceiptDate.setComponentError(null);
		dfVendorDocDt.setComponentError(null);
		dfVendorInvDt.setComponentError(null);
		cbHdrStatus.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_POBRANCH_NAME));
			errorFlag = true;
		}
		if (cbPoNo.getValue() == null) {
			cbPoNo.setComponentError(new UserError(GERPErrorCodes.NULL_PURCAHSE_ORD_NO));
			errorFlag = true;
		}
		if (dfReceiptDate.getValue() == null) {
			dfReceiptDate.setComponentError(new UserError(GERPErrorCodes.NULL_REGECT_REASON));
			errorFlag = true;
		}
		if (dfVendorDocDt.getValue() == null) {
			dfVendorDocDt.setComponentError(new UserError(GERPErrorCodes.NULL_REGECT_REASON));
			errorFlag = true;
		}
		if (dfVendorInvDt.getValue() == null) {
			dfVendorInvDt.setComponentError(new UserError(GERPErrorCodes.NULL_REGECT_REASON));
			errorFlag = true;
		}
		if (cbHdrStatus.getValue() == null) {
			cbHdrStatus.setComponentError(new UserError(GERPErrorCodes.NULL_MMS_HDRSTATUS));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Detail Validation
	private boolean dtlValidation() {
		boolean isValid = true;
		if (cbMaterial.getValue() == null) {
			cbMaterial.setComponentError(new UserError(GERPErrorCodes.NUL_POMATERIAL_NAME));
			isValid = false;
		} else {
			cbMaterial.setComponentError(null);
		}
		if (tfMaterialUOM.getValue() == null) {
			tfMaterialUOM.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			isValid = false;
		} else {
			tfMaterialUOM.setComponentError(null);
		}
		if (taRejectReason.getValue() == null) {
			taRejectReason.setComponentError(new UserError(GERPErrorCodes.REGECT_REASON));
			isValid = false;
		} else {
			taRejectReason.setComponentError(null);
		}
		return isValid;
	}
	
	// Save Details
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			PoReceiptHdrDM recepithdrObj = new PoReceiptHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				recepithdrObj = beanPoReceiptHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			recepithdrObj.setLotNo(tfLotNo.getValue());
			recepithdrObj.setCompanyId(companyid);
			if (cbPoNo.getValue() != null) {
				recepithdrObj.setPoId(((POHdrDM) cbPoNo.getValue()).getPoId());
			}
			recepithdrObj.setBranchId((Long) cbBranch.getValue());
			if (cbDocType.getValue() != null) {
				recepithdrObj.setReceiptdocType(cbDocType.getValue().toString());
			}
			recepithdrObj.setReceiptDate((Date) dfReceiptDate.getValue());
			recepithdrObj.setVendorDate((Date) dfVendorDocDt.getValue());
			recepithdrObj.setVendordcNo(tfVendorDcNo.getValue());
			recepithdrObj.setIndentId((Long) cbIndentNo.getValue());
			recepithdrObj.setVendorinvoiceNo(tfVenInvNo.getValue().toString());
			recepithdrObj.setVendorinvoiceDate((Date) dfVendorInvDt.getValue());
			recepithdrObj.setReceiptRemark((taRecepitRemark.getValue()));
			if (chkBillRaised.getValue().equals(true)) {
				recepithdrObj.setBillraisedYN("Y");
			} else if (chkBillRaised.getValue().equals(false)) {
				recepithdrObj.setBillraisedYN("N");
			}
			recepithdrObj.setProjectStatus(cbHdrStatus.getValue().toString());
			recepithdrObj.setPreparedBy(employeeId);
			recepithdrObj.setReviewedBy(employeeId);
			recepithdrObj.setActionedBy(employeeId);
			recepithdrObj.setLastUpdtDate(DateUtils.getcurrentdate());
			recepithdrObj.setLastUpdatedBy(username);
			servicePoReceiptHdr.saveorUpdatePoReceiptHdrDetails(recepithdrObj);
			@SuppressWarnings("unchecked")
			Collection<PoReceiptDtlDM> itemIds = (Collection<PoReceiptDtlDM>) tblReceiptDtl.getVisibleItemIds();
			for (PoReceiptDtlDM save : (Collection<PoReceiptDtlDM>) itemIds) {
				save.setReceiptid(Long.valueOf(recepithdrObj.getReceiptId().toString()));
				servicePoReceiptDtl.savePoReceiptDtl(save);
				try {
					MaterialLedgerDM materialLedgerDM = null;
					try {
						materialLedgerDM = serviceLedger.getMaterialLedgerList(save.getMaterialid(), null, null, null,
								"New", null, "Y", "F").get(0);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					if (materialLedgerDM == null) {
						MaterialLedgerDM ledgerDM = new MaterialLedgerDM();
						ledgerDM.setStockledgeDate(new Date());
						ledgerDM.setCompanyId(companyid);
						ledgerDM.setBranchId(branchId);
						ledgerDM.setMaterialId(save.getMaterialid());
						ledgerDM.setStockType("New");
						ledgerDM.setOpenQty(0L);
						ledgerDM.setInoutFlag("I");
						ledgerDM.setInoutFQty(save.getReceiptqty());
						ledgerDM.setCloseQty(save.getReceiptqty());
						ledgerDM.setReferenceNo(recepithdrObj.getLotNo());
						ledgerDM.setReferenceDate(recepithdrObj.getReceiptDate());
						ledgerDM.setIsLatest("Y");
						ledgerDM.setReferenceRemark(save.getRejectreason());
						ledgerDM.setLastUpdatedby(username);
						ledgerDM.setLastUpdateddt(DateUtils.getcurrentdate());
						serviceLedger.saveOrUpdateLedger(ledgerDM);
					} else {
						MaterialLedgerDM ledgerDM = new MaterialLedgerDM();
						ledgerDM.setStockledgeDate(new Date());
						ledgerDM.setCompanyId(companyid);
						ledgerDM.setBranchId(branchId);
						ledgerDM.setMaterialId(save.getMaterialid());
						ledgerDM.setStockType("New");
						ledgerDM.setOpenQty(materialLedgerDM.getCloseQty());
						ledgerDM.setInoutFlag("I");
						ledgerDM.setInoutFQty(save.getReceiptqty());
						ledgerDM.setCloseQty(materialLedgerDM.getCloseQty() + save.getReceiptqty());
						ledgerDM.setReferenceNo(recepithdrObj.getLotNo());
						ledgerDM.setReferenceDate(recepithdrObj.getReceiptDate());
						ledgerDM.setIsLatest("Y");
						ledgerDM.setReferenceRemark(save.getRejectreason());
						ledgerDM.setLastUpdatedby(username);
						ledgerDM.setLastUpdateddt(DateUtils.getcurrentdate());
						serviceLedger.saveOrUpdateLedger(ledgerDM);
					}
					try {
						// for material stock
						MaterialStockDM materialStockDM = null;
						try {
							materialStockDM = serviceMaterialStock.getMaterialStockList(save.getMaterialid(), null,
									null, null, null, "New", "F").get(0);
						}
						catch (Exception e) {
							logger.info(e.getMessage());
						}
						if (materialStockDM == null) {
							materialStockDM = new MaterialStockDM();
							materialStockDM.setCompanyId(companyid);
							materialStockDM.setBranchId(branchId);
							materialStockDM.setMaterialId(save.getMaterialid());
							materialStockDM.setLotNo(recepithdrObj.getLotNo());
							materialStockDM.setStockType("New");
							materialStockDM.setCurrentStock(save.getReceiptqty());
							materialStockDM.setParkedStock(0L);
							materialStockDM.setEffectiveStock(save.getReceiptqty());
							materialStockDM.setLastUpdatedby(username);
							materialStockDM.setLastUpdateddt(DateUtils.getcurrentdate());
							serviceMaterialStock.saveorupdatematerialstock(materialStockDM);
						} else {
							materialStockDM.setCurrentStock(materialStockDM.getCurrentStock() + save.getReceiptqty());
							materialStockDM.setEffectiveStock(materialStockDM.getEffectiveStock()
									+ save.getReceiptqty());
							materialStockDM.setLastUpdatedby(username);
							materialStockDM.setLastUpdateddt(DateUtils.getcurrentdate());
							serviceMaterialStock.saveorupdatematerialstock(materialStockDM);
						}
					}
					catch (Exception e) {
						logger.info(e.getMessage());
					}
				}
				catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
			tfLotNo.setReadOnly(false);
			tfLotNo.setValue(recepithdrObj.getLotNo());
			tfLotNo.setReadOnly(true);
			receiptResetFields();
			loadSrchRslt();
			receiptId = 0L;
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Save Recepit Details
	private void saveReceiptDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			PoReceiptDtlDM receiptDtlObj = new PoReceiptDtlDM();
			if (tblReceiptDtl.getValue() != null) {
				receiptDtlObj = beanPoReceiptDtlDM.getItem(tblReceiptDtl.getValue()).getBean();
				listPOReceipts.remove(receiptDtlObj);
			}
			receiptDtlObj.setMaterialid(((MmsPoDtlDM) cbMaterial.getValue()).getMaterialid());
			receiptDtlObj.setMaterialName(((MmsPoDtlDM) cbMaterial.getValue()).getMaterialname());
			receiptDtlObj.setMaterialUOM(tfMaterialUOM.getValue().toString());
			if (tfRejectqty.getValue() != null && tfRejectqty.getValue().trim().length() > 0) {
				receiptDtlObj.setRejectqty(Long.valueOf(tfRejectqty.getValue()));
			}
			receiptDtlObj.setRejectreason(taRejectReason.getValue().toString());
			if (tfAcceptQty.getValue() != null && tfAcceptQty.getValue().trim().length() > 0) {
				receiptDtlObj.setReceiptqty(Long.valueOf(tfAcceptQty.getValue()));
			}
			if (cbDtlStatus.getValue() != null) {
				receiptDtlObj.setReceiptstatus((cbDtlStatus.getValue().toString()));
			}
			receiptDtlObj.setLastupdateddt(DateUtils.getcurrentdate());
			receiptDtlObj.setLastupdatedby(username);
			listPOReceipts.add(receiptDtlObj);
			loadReceiptDtl();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		receiptResetFields();
		btnadd.setCaption("Add");
	}
	
	// Audit Details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for TestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MMS_PO_RECEIPTS_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", lotNo);
	}
	
	// Cancel Details
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
	
	// Reset Fields
	@Override
	protected void resetFields() {
		listPOReceipts = new ArrayList<PoReceiptDtlDM>();
		tblReceiptDtl.removeAllItems();
		cbBranch.setValue(branchId);
		cbBranch.setComponentError(null);
		cbDocType.setValue(null);
		cbHdrStatus.setValue(null);
		cbHdrStatus.setComponentError(null);
		cbPoNo.setValue(null);
		cbPoNo.setComponentError(null);
		tfLotNo.setValue("");
		tfVenInvNo.setValue("");
		tfVendorDcNo.setValue("");
		cbIndentNo.setValue(null);
		taRecepitRemark.setValue("");
		dfVendorDocDt.setValue(null);
		dfReceiptDate.setValue(null);
		dfVendorInvDt.setValue(null);
		dfReceiptDate.setComponentError(null);
		dfVendorDocDt.setComponentError(null);
		dfVendorInvDt.setComponentError(null);
		new UploadDocumentUI(hlevdDoc);
		new UploadDocumentUI(hlrefDoc);
	}
	
	// Recepit Reset Fields
	private void receiptResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMaterial.setValue(null);
		taRejectReason.setValue("");
		tfAcceptQty.setValue("0");
		tfAcceptQty.setComponentError(null);
		tfAcceptQty.setValue("0");
		tfPOQty.setValue("0");
		tfRejectqty.setValue("0");
		tfRejectqty.setComponentError(null);
		tfRejectqty.setValue("0");
		cbDtlStatus.setValue(null);
		tfMaterialUOM.setValue(null);
		cbMaterial.setComponentError(null);
		tfMaterialUOM.setComponentError(null);
		new UploadDocumentUI(hlevdDoc);
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
