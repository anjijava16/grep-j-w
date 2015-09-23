/**
 * File Name 		: PurchaseEnquiry.java 
 * Description 		:This Screen Purpose for Modify the PurchaseEnquiry Details.
 * 					Add the PurchaseEnquiry details process should be directly added in DB.
 * Author 			: Ganga 
 * Date 			: Aug 10, 2014

 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Aug 10, 2014         GANGA                  Initial  Version
 */
package com.gnts.sms.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.base.service.mst.VendorService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.sms.domain.txn.EnquiryVendorDtlDM;
import com.gnts.sms.domain.txn.SmsPurEnqDtlDM;
import com.gnts.sms.domain.txn.SmsPurEnqHdrDM;
import com.gnts.sms.service.txn.EnquiryVendorDtlService;
import com.gnts.sms.service.txn.SmsPurEnqDtlService;
import com.gnts.sms.service.txn.SmsPurEnqHdrService;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class PurchaseEnquiry extends BaseUI {
	// Bean Creation
	private SmsPurEnqHdrService serviceSmsPurEnqHdr = (SmsPurEnqHdrService) SpringContextHelper.getBean("SmsPurEnqHdr");
	private SmsPurEnqDtlService serviceSmsPurEnqDtl = (SmsPurEnqDtlService) SpringContextHelper.getBean("SmsPurEnqDtl");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private EnquiryVendorDtlService serviceEnquiryVendorDtl = (EnquiryVendorDtlService) SpringContextHelper
			.getBean("purvendor");
	private List<SmsPurEnqDtlDM> listEnqDtls = new ArrayList<SmsPurEnqDtlDM>();
	// form layout for input controls
	private FormLayout flSmsPurHdr1, flSmsPurHdr2, flSmsPurHdr3, flSmsPurHdr4, flSmsPurDtl1, flSmsPurDtl4,
			flSmsPurDtl5, flSmsPurDtl6, flSmsPurDtl2, flSmsPurDtl3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Purchase Enquire Details
	private Button btnaddSpec = new GERPButton("Add", "addbt", this);
	// User Input Fields for Purchase Enquire Header
	private ComboBox cbBranch, cbEnqStatus;
	private ListSelect lsVendorName, lsProduct;
	private TextField tfEnqNo;
	private TextArea taEnqRem;
	private PopupDateField dfEnqDate, dfDueDate;
	private Label lbl;
	// User Input Fields for Purchase Enquire Detail
	private ComboBox cbEnqDtlStatus;
	private TextField tfEnqQty, cbUom;
	private TextArea taEnqDtlRem;
	private Table tblSmsEnqDtl = new GERPTable();
	private BeanItemContainer<SmsPurEnqHdrDM> beanPurEnqHdrDM = null;
	private BeanItemContainer<SmsPurEnqDtlDM> beanPurEnqDtlDM = null;
	// local variables declaration
	private Long companyid;
	private Long enquiryId;
	private int recordCnt = 0;
	private String username;
	private String enquiryid;
	// Initialize logger
	private Logger logger = Logger.getLogger(PurchaseEnquiry.class);
	private static final long serialVersionUID = 1L;
	private Long branchId;
	private Long employeeId;
	private Long moduleId;
	private Long roleId, appScreenId;
	private SmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private String status;
	
	// Constructor received the parameters from Login UI class
	public PurchaseEnquiry() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PurchaseEnquiry() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting PurchaseEnquiry UI");
		// Initialization for Purchase Enquire Details user input components
		tfEnqNo = new TextField("Enquiry No");
		tfEnqNo.setMaxLength(40);
		tfEnqQty = new TextField();
		tfEnqQty.setValue("0");
		tfEnqQty.setWidth("90");
		dfDueDate = new GERPPopupDateField("Due Date");
		dfDueDate.setInputPrompt("Select Date");
		dfEnqDate = new GERPPopupDateField("Enquiry Date");
		dfEnqDate.setInputPrompt("Select Date");
		taEnqDtlRem = new TextArea("Remarks");
		taEnqDtlRem.setMaxLength(40);
		taEnqDtlRem.setWidth("150");
		taEnqDtlRem.setHeight("50");
		taEnqRem = new TextArea("Remarks");
		taEnqRem.setHeight("50");
		taEnqDtlRem.setMaxLength(100);
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		try {
			ApprovalSchemaDM obj = serviceSmsPurEnqHdr.getReviewerId(companyid, appScreenId, branchId, roleId).get(0);
			if (obj.getApprLevel().equals("Approver")) {
				cbEnqStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_ENQUIRY_HDR, BASEConstants.RP_STATUS);
			} else {
				cbEnqStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_ENQUIRY_HDR, BASEConstants.PE_STATUS_RV);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		cbEnqStatus.setWidth("120");
		cbEnqDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		lsProduct = new ListSelect("Product Name");
		lsProduct.setItemCaptionPropertyId("prodname");
		lsProduct.setMultiSelect(true);
		loadProduct();
		lsProduct.setImmediate(true);
		lsProduct.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				String[] split = lsProduct.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
				for (String obj : split) {
					if (obj.trim().length() > 0) {
						cbUom.setReadOnly(false);
						cbUom.setValue(serviceProduct
								.getProductList(companyid, Long.valueOf(obj.trim()), null, null, null, null, null, "F")
								.get(0).getUom());
						cbUom.setReadOnly(true);
					}
				}
			}
		});
		cbUom = new TextField();
		cbUom.setWidth("77");
		cbUom.setHeight("23");
		lsVendorName = new ListSelect("Vendor Name ");
		lsVendorName.setMultiSelect(true);
		lsVendorName.setItemCaptionPropertyId("vendorName");
		loadVendorList();
		lsVendorName.setWidth("150");
		lsVendorName.setHeight("75");
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadPurDtl();
		btnaddSpec.setStyleName("add");
		btnaddSpec.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (dtlValidation()) {
					saveEnqDtl();
				}
			}
		});
		btndelete.setEnabled(false);
		// ClickListener for Enquire Detail Tale
		tblSmsEnqDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblSmsEnqDtl.isSelected(event.getItemId())) {
					tblSmsEnqDtl.setImmediate(true);
					btnaddSpec.setCaption("Add");
					btnaddSpec.setStyleName("savebt");
					btndelete.setEnabled(false);
					enqDtlresetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddSpec.setCaption("Update");
					btnaddSpec.setStyleName("savebt");
					btndelete.setEnabled(true);
					editSmsPurDetail();
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
		flSmsPurHdr1 = new FormLayout();
		flSmsPurHdr2 = new FormLayout();
		flSmsPurHdr3 = new FormLayout();
		flSmsPurHdr1.addComponent(cbBranch);
		flSmsPurHdr2.addComponent(tfEnqNo);
		flSmsPurHdr3.addComponent(cbEnqStatus);
		hlSearchLayout.addComponent(flSmsPurHdr1);
		hlSearchLayout.addComponent(flSmsPurHdr2);
		hlSearchLayout.addComponent(flSmsPurHdr3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlUserInputLayout.removeAllComponents();
		flSmsPurHdr1 = new FormLayout();
		flSmsPurHdr2 = new FormLayout();
		flSmsPurHdr3 = new FormLayout();
		flSmsPurHdr4 = new FormLayout();
		flSmsPurHdr1.addComponent(tfEnqNo);
		flSmsPurHdr1.addComponent(cbBranch);
		flSmsPurHdr2.addComponent(lsVendorName);
		cbEnqStatus.setWidth("130px");
		cbEnqStatus.setHeight("24px");
		flSmsPurHdr3.addComponent(dfEnqDate);
		flSmsPurHdr3.addComponent(dfDueDate);
		flSmsPurHdr3.addComponent(cbEnqStatus);
		flSmsPurHdr4.addComponent(taEnqRem);
		HorizontalLayout hlSmsEnqHDR = new HorizontalLayout();
		hlSmsEnqHDR.addComponent(flSmsPurHdr1);
		hlSmsEnqHDR.addComponent(flSmsPurHdr2);
		hlSmsEnqHDR.addComponent(flSmsPurHdr3);
		lbl = new Label(" ");
		hlSmsEnqHDR.addComponent(lbl);
		hlSmsEnqHDR.addComponent(flSmsPurHdr4);
		tfEnqNo.setReadOnly(true);
		hlSmsEnqHDR.setSpacing(true);
		hlSmsEnqHDR.setMargin(true);
		// Adding SmsEnqDtl components
		// Add components for User Input Layout
		flSmsPurDtl1 = new FormLayout();
		flSmsPurDtl2 = new FormLayout();
		flSmsPurDtl3 = new FormLayout();
		flSmsPurDtl4 = new FormLayout();
		flSmsPurDtl5 = new FormLayout();
		flSmsPurDtl6 = new FormLayout();
		flSmsPurDtl1.addComponent(lsProduct);
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfEnqQty);
		hlQtyUom.addComponent(cbUom);
		hlQtyUom.setCaption("Enquiry Qty");
		flSmsPurDtl2.addComponent(hlQtyUom);
		flSmsPurDtl2.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		flSmsPurDtl4.addComponent(taEnqDtlRem);
		flSmsPurDtl5.addComponent(cbEnqDtlStatus);
		VerticalLayout btn = new VerticalLayout();
		btn.addComponent(btnaddSpec);
		btn.addComponent(btndelete);
		flSmsPurDtl5.addComponent(btn);
		HorizontalLayout hlSmsEnqdTL = new HorizontalLayout();
		hlSmsEnqdTL.addComponent(flSmsPurDtl1);
		hlSmsEnqdTL.addComponent(flSmsPurDtl2);
		hlSmsEnqdTL.addComponent(flSmsPurDtl3);
		hlSmsEnqdTL.addComponent(flSmsPurDtl4);
		hlSmsEnqdTL.addComponent(flSmsPurDtl5);
		hlSmsEnqdTL.addComponent(flSmsPurDtl6);
		hlSmsEnqdTL.setSpacing(true);
		hlSmsEnqdTL.setMargin(true);
		VerticalLayout vlSmsEnqHDR = new VerticalLayout();
		vlSmsEnqHDR = new VerticalLayout();
		vlSmsEnqHDR.addComponent(hlSmsEnqdTL);
		vlSmsEnqHDR.addComponent(tblSmsEnqDtl);
		vlSmsEnqHDR.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlSmsEnqHDR, "Purchase Enquiry Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		VerticalLayout vlSmsEnqHdrdTL = new VerticalLayout();
		vlSmsEnqHdrdTL = new VerticalLayout();
		vlSmsEnqHdrdTL.addComponent(GERPPanelGenerator.createPanel(hlSmsEnqHDR));
		vlSmsEnqHdrdTL.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		vlSmsEnqHdrdTL.setSpacing(true);
		vlSmsEnqHdrdTL.setWidth("100%");
		hlUserInputLayout.addComponent(vlSmsEnqHdrdTL);
		hlUserInputLayout.setSizeFull();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	// Load Purchase Header
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<SmsPurEnqHdrDM> listPurEnq = new ArrayList<SmsPurEnqHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + cbBranch.getValue() + ", " + cbEnqStatus.getValue());
			listPurEnq = serviceSmsPurEnqHdr.getSmsPurEnqHdrList(companyid, null, tfEnqNo.getValue(),
					(Long) cbBranch.getValue(), (String) cbEnqStatus.getValue(), username);
			recordCnt = listPurEnq.size();
			beanPurEnqHdrDM = new BeanItemContainer<SmsPurEnqHdrDM>(SmsPurEnqHdrDM.class);
			beanPurEnqHdrDM.addAll(listPurEnq);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the PurchaseEnquiry. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanPurEnqHdrDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "enquiryId", "branchName", "enquiryNo", "enquiryStatus",
					"lastUpdateddt", "lastUpdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "EnquiryNo.", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("enquiryId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Purchase Detail
	private void loadPurDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordCnt = listEnqDtls.size();
			beanPurEnqDtlDM = new BeanItemContainer<SmsPurEnqDtlDM>(SmsPurEnqDtlDM.class);
			beanPurEnqDtlDM.addAll(listEnqDtls);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the dPurDt. result set");
			tblSmsEnqDtl.setContainerDataSource(beanPurEnqDtlDM);
			tblSmsEnqDtl.setVisibleColumns(new Object[] { "pordName", "productUom", "enquiryQty", "enqDtlStaus",
					"lastUpdateddt", "lastUpdatedby" });
			tblSmsEnqDtl.setColumnHeaders(new String[] { "Product Name", "UOM", "Enquiry Qty", "Status",
					"Last Updated Date", "Last Updated By" });
			tblSmsEnqDtl.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
			tblSmsEnqDtl.setPageLength(7);
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
	
	// Load Vendor List
	private void loadVendorList() {
		try {
			BeanContainer<Long, VendorDM> beanVendor = new BeanContainer<Long, VendorDM>(VendorDM.class);
			beanVendor.setBeanIdProperty("vendorId");
			beanVendor.addAll(serviceVendor.getVendorList(branchId, null, companyid, null, null, null, null, null,
					"Active", null, "P"));
			lsVendorName.setContainerDataSource(beanVendor);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadProduct() {
		try {
			BeanContainer<Long, ProductDM> beanVendor = new BeanContainer<Long, ProductDM>(ProductDM.class);
			beanVendor.setBeanIdProperty("prodid");
			beanVendor.addAll(serviceProduct.getProductList(companyid, null, null, null, "Active", null, null, "P"));
			lsProduct.setContainerDataSource(beanVendor);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfEnqNo.setReadOnly(false);
		tfEnqNo.setValue("");
		cbEnqStatus.setValue(null);
		listEnqDtls = new ArrayList<SmsPurEnqDtlDM>();
		tblSmsEnqDtl.removeAllItems();
		cbBranch.setValue(branchId);
		cbBranch.setComponentError(null);
		lsVendorName.setValue(null);
		lsVendorName.setComponentError(null);
		dfDueDate.setValue(null);
		dfEnqDate.setValue(null);
		taEnqRem.setValue("");
		taEnqDtlRem.setValue("");
		dfEnqDate.setValue(new Date());
		cbEnqStatus.setValue(cbEnqStatus.getItemIds().iterator().next());
		cbUom.setReadOnly(true);
		dfDueDate.setValue(addDays(new Date(), 7));
	}
	
	// Method to edit the values from table into fields to update process
	private void editPurHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		try {
			hlUserInputLayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				SmsPurEnqHdrDM enqHdrDM = beanPurEnqHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				enquiryId = enqHdrDM.getEnquiryId();
				logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Selected enquiryId. Id -> " + enquiryId);
				logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Selected enquiryId. Id -> " + enquiryId);
				cbBranch.setValue(enqHdrDM.getBranchId());
				tfEnqNo.setReadOnly(false);
				tfEnqNo.setValue(enqHdrDM.getEnquiryNo());
				tfEnqNo.setReadOnly(true);
				dfEnqDate.setValue(enqHdrDM.getEnquiryDate());
				dfDueDate.setValue(enqHdrDM.getDueDate());
				for (EnquiryVendorDtlDM enquiryVendorDtlDM : serviceEnquiryVendorDtl.getpurchasevdrdtl(null, enquiryId,
						null)) {
					lsVendorName.select(enquiryVendorDtlDM.getVendorid().toString());
				}
				if (enqHdrDM.getEnquiryStatus() != null) {
					cbEnqStatus.setValue(enqHdrDM.getEnquiryStatus());
				}
				if (enqHdrDM.getEnqRemark() != null) {
					taEnqRem.setValue(enqHdrDM.getEnqRemark().toString());
				}
				listEnqDtls = serviceSmsPurEnqDtl.getSmsPurEnqDtlList(null, enquiryId, null,
						(String) cbEnqDtlStatus.getValue());
			}
			loadPurDtl();
			comments = new SmsComments(vlTableForm, null, companyid, enquiryId, null, null, null, null, null, null,
					null, null, status,null);
			comments.loadsrch(true, null, null, enquiryId, null, null, null, null, null, null, null, null, null,null);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editSmsPurDetail() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblSmsEnqDtl.getValue() != null) {
			SmsPurEnqDtlDM purEnqDtlDM = beanPurEnqDtlDM.getItem(tblSmsEnqDtl.getValue()).getBean();
			lsProduct.setValue(null);
			Long prodid = purEnqDtlDM.getProductId();
			Collection<?> prodids = lsProduct.getItemIds();
			for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) lsProduct.getItem(itemId);
				// Get the actual bean and use the data
				ProductDM st = (ProductDM) item.getBean();
				if (prodid != null && prodid.equals(st.getProdid())) {
					lsProduct.select(itemId);
				}
			}
			if (cbUom.getValue() != null) {
				cbUom.setReadOnly(false);
				cbUom.setValue(purEnqDtlDM.getProductUom());
				cbUom.setReadOnly(true);
			}
			if (tfEnqQty.getValue() != null) {
				tfEnqQty.setValue(purEnqDtlDM.getEnquiryQty().toString());
			}
			if (purEnqDtlDM.getRemarks() != null) {
				taEnqDtlRem.setValue(purEnqDtlDM.getRemarks().toString());
			}
			if (purEnqDtlDM.getEnqDtlStaus() != null) {
				cbEnqDtlStatus.setValue(purEnqDtlDM.getEnqDtlStaus());
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
			tfEnqNo.setReadOnly(false);
			cbBranch.setValue(null);
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEnqStatus.setValue(null);
		tfEnqNo.setValue("");
		tfEnqNo.setReadOnly(false);
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
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnaddSpec.setCaption("Add");
		tblSmsEnqDtl.setVisible(true);
		cbBranch.setRequired(true);
		lsVendorName.setRequired(true);
		tfEnqNo.setReadOnly(true);
		lsProduct.setRequired(true);
		lsProduct.setComponentError(null);
		dfEnqDate.setComponentError(null);
		cbUom.setRequired(true);
		loadPurDtl();
		resetFields();
		tfEnqNo.setReadOnly(true);
		try {
			tfEnqNo.setReadOnly(false);
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_ENQRYNO ").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfEnqNo.setValue(slnoObj.getKeyDesc());
				tfEnqNo.setReadOnly(true);
			} else {
				tfEnqNo.setReadOnly(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null,null);
	}
	
	@Override
	protected void editDetails() {
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleInputUserLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblSmsEnqDtl.setVisible(true);
		if (tfEnqNo.getValue() == null || tfEnqNo.getValue().trim().length() == 0) {
			tfEnqNo.setReadOnly(false);
		}
		cbBranch.setRequired(true);
		lsVendorName.setRequired(true);
		resetFields();
		editPurHdr();
		editSmsPurDetail();
		lsProduct.setRequired(true);
		cbUom.setRequired(true);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			errorFlag = true;
		} else {
			cbBranch.setComponentError(null);
			errorFlag = false;
		}
		if (lsVendorName.getValue() == null || lsVendorName.getValue().toString() == "[]") {
			lsVendorName.setComponentError(new UserError(GERPErrorCodes.NULL_VENDOR_NAME));
			errorFlag = true;
		} else {
			lsVendorName.setComponentError(null);
			errorFlag = false;
		}
		if (tblSmsEnqDtl.size() == 0) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			tfEnqQty.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRY_QTY));
			lsProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		if ((dfEnqDate.getValue() != null) || (dfDueDate.getValue() != null)) {
			if (dfEnqDate.getValue().after(dfDueDate.getValue())) {
				dfDueDate.setComponentError(new UserError(GERPErrorCodes.MMS_DATE_OUTOFRANGE));
				logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Throwing ValidationException. User data is > " + dfEnqDate.getValue());
				errorFlag = true;
			}
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean dtlValidation() {
		boolean isValid = true;
		if (cbUom.getValue() == null) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			isValid = false;
		} else {
			cbUom.setComponentError(null);
		}
		if ((tfEnqQty.getValue() == "0")) {
			tfEnqQty.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRY_QTY));
			isValid = false;
		} else {
			tfEnqQty.setComponentError(null);
			isValid = true;
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfEnqQty.getValue());
			if (achievedQty < 0) {
				tfEnqQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfEnqQty.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATION));
			isValid = false;
		}
		if (lsProduct.getValue() == null) {
			lsProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			lsProduct.setComponentError(null);
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			SmsPurEnqHdrDM purEnqobj = new SmsPurEnqHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				purEnqobj = beanPurEnqHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				purEnqobj.setEnquiryNo(tfEnqNo.getValue());
			}
			purEnqobj.setEnquiryNo(tfEnqNo.getValue());
			purEnqobj.setCompanyId(companyid);
			purEnqobj.setEnqRemark(taEnqRem.getValue().toString());
			purEnqobj.setBranchId((Long) cbBranch.getValue());
			purEnqobj.setDueDate((Date) dfDueDate.getValue());
			purEnqobj.setEnquiryDate((Date) dfEnqDate.getValue());
			purEnqobj.setEnquiryStatus(((String) cbEnqStatus.getValue()));
			purEnqobj.setPreparedBy(employeeId);
			purEnqobj.setReviewedBy(null);
			purEnqobj.setActionedBy(null);
			purEnqobj.setLastUpdateddt(DateUtils.getcurrentdate());
			purEnqobj.setLastUpdatedby(username);
			serviceSmsPurEnqHdr.saveorUpdateSmsPurEnqHdrDetails(purEnqobj);
			enquiryId = purEnqobj.getEnquiryId();
			String[] split = lsVendorName.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
			for (String obj : split) {
				if (obj.trim().length() > 0) {
					EnquiryVendorDtlDM enqvendtl = new EnquiryVendorDtlDM();
					enqvendtl.setEnqid(purEnqobj.getEnquiryId());
					System.out.println("vendid" + Long.valueOf(obj.trim()));
					enqvendtl.setVendorid(Long.valueOf(obj.trim()));
					serviceEnquiryVendorDtl.save(enqvendtl);
				}
			}
			@SuppressWarnings("unchecked")
			Collection<SmsPurEnqDtlDM> itemIds = (Collection<SmsPurEnqDtlDM>) tblSmsEnqDtl.getVisibleItemIds();
			for (SmsPurEnqDtlDM save : (Collection<SmsPurEnqDtlDM>) itemIds) {
				save.setEnquiryId(Long.valueOf(purEnqobj.getEnquiryId().toString()));
				serviceSmsPurEnqDtl.saveorUpdateSmsPurEnqDtlDetails(save);
			}
			comments.resetfields();
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_ENQRYNO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_ENQRYNO");
					}
				}
				catch (Exception e) {
				}
			}
			tfEnqNo.setReadOnly(false);
			tfEnqNo.setValue(purEnqobj.getEnquiryNo());
			tfEnqNo.setReadOnly(true);
			comments.saveEnquiry(purEnqobj.getEnquiryId(), purEnqobj.getEnquiryStatus());
			loadSrchRslt();
			enqDtlresetFields();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void saveEnqDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			int count = 0;
			String[] split = lsProduct.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
			for (String obj : split) {
				if (obj.trim().length() > 0) {
					for (SmsPurEnqDtlDM smsPurEnqDtlDM : listEnqDtls) {
						if (smsPurEnqDtlDM.getProductId().equals(Long.valueOf(obj.trim()))) {
							count++;
							break;
						}
					}
					if (tblSmsEnqDtl.getValue() != null) {
						count = 0;
					}
					if (count == 0) {
						SmsPurEnqDtlDM enqDtlObj = new SmsPurEnqDtlDM();
						if (tblSmsEnqDtl.getValue() != null) {
							enqDtlObj = beanPurEnqDtlDM.getItem(tblSmsEnqDtl.getValue()).getBean();
							listEnqDtls.remove(enqDtlObj);
						}
						if (lsProduct.getValue() != null) {
							enqDtlObj.setProductId(Long.valueOf(obj.trim()));
							enqDtlObj.setPordName(serviceProduct
									.getProductList(null, Long.valueOf(obj.trim()), null, null, null, null, null, "P")
									.get(0).getProdname());
						}
						if (cbUom.getValue() != null) {
							cbUom.setReadOnly(false);
							enqDtlObj.setProductUom(cbUom.getValue().toString());
							cbUom.setReadOnly(true);
						}
						enqDtlObj.setRemarks(taEnqDtlRem.getValue().toString());
						if (tfEnqQty.getValue() != null && tfEnqQty.getValue().trim().length() > 0) {
							enqDtlObj.setEnquiryQty(Long.valueOf(tfEnqQty.getValue()));
						}
						if (cbEnqDtlStatus.getValue() != null) {
							enqDtlObj.setEnqDtlStaus((cbEnqDtlStatus.getValue().toString()));
						}
						enqDtlObj.setLastUpdateddt(DateUtils.getcurrentdate());
						enqDtlObj.setLastUpdatedby(username);
						listEnqDtls.add(enqDtlObj);
						loadPurDtl();
						btnaddSpec.setCaption("Add");
						count = 0;
					} else {
						lsProduct.setComponentError(new UserError("Product Already Exist.."));
					}
				}
			}
			enqDtlresetFields();
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
		UI.getCurrent().getSession().setAttribute("audittablepk", enquiryid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblSmsEnqDtl.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		tfEnqNo.setReadOnly(false);
		cbBranch.setRequired(false);
		lsVendorName.setRequired(false);
		lsVendorName.setComponentError(null);
		enqDtlresetFields();
		resetFields();
		loadSrchRslt();
	}
	
	private void enqDtlresetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		lsProduct.setValue(null);
		tfEnqQty.setValue("0");
		taEnqDtlRem.setValue("");
		cbUom.setReadOnly(false);
		cbUom.setValue("");
		cbUom.setReadOnly(true);
		cbEnqDtlStatus.setValue(cbEnqDtlStatus.getItemIds().iterator().next());
		cbUom.setComponentError(null);
		btnaddSpec.setCaption("Add");
		tfEnqQty.setComponentError(null);
	}
	
	private void deleteDetails() {
		SmsPurEnqDtlDM save = new SmsPurEnqDtlDM();
		if (tblSmsEnqDtl.getValue() != null) {
			save = beanPurEnqDtlDM.getItem(tblSmsEnqDtl.getValue()).getBean();
			listEnqDtls.remove(save);
			enqDtlresetFields();
			loadPurDtl();
			btndelete.setEnabled(false);
		}
	}
	
	private Date addDays(Date d, int days) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = sdf.format(d);
		Date parsedDate = null;
		try {
			parsedDate = sdf.parse(strDate);
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.warn("calculate days" + e);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, days);
		return now.getTime();
	}
}
