/**
 * File Name 	 :   MaterialEnquiry.java 
 * Description 	 :   This Screen Purpose for Modify the MaterialEnquiry Details.Add the MaterialEnquiry details and Specification process should be directly added in DB.
 * Author 		 :   Arun Jeyaraj R 
 * Date 		 :   Oct17,2014
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS Technologies pvt. ltd.
 * 
 * Version  Date           Modified By    Remarks
 * 0.1      Oct17,2014    Arun Jeyaraj R        Initial Version
 */
package com.gnts.mms.txn;

import java.io.IOException;
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
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.IndentHdrDM;
import com.gnts.mms.domain.txn.MMSVendorDtlDM;
import com.gnts.mms.domain.txn.MmsEnqDtlDM;
import com.gnts.mms.domain.txn.MmsEnqHdrDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.IndentHdrService;
import com.gnts.mms.service.txn.MMSVendorDtlService;
import com.gnts.mms.service.txn.MmsEnqDtlService;
import com.gnts.mms.service.txn.MmsEnqHdrService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
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

public class MaterialEnquiry extends BaseTransUI {
	private MmsEnqHdrService serviceMmsEnqHdr = (MmsEnqHdrService) SpringContextHelper.getBean("MmsEnqHdr");
	private MmsEnqDtlService serviceMmsEnqDtl = (MmsEnqDtlService) SpringContextHelper.getBean("MmsEnqDtl");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private MMSVendorDtlService serviceMMSVendordtl = (MMSVendorDtlService) SpringContextHelper.getBean("mmsVendorDtl");
	private IndentHdrService serviceindent = (IndentHdrService) SpringContextHelper.getBean("IndentHdr");
	private MaterialService servicematerial = (MaterialService) SpringContextHelper.getBean("material");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private List<MmsEnqDtlDM> listEnqDetails = new ArrayList<MmsEnqDtlDM>();
	// form layout for input controls
	private FormLayout flMmsEnqHdr1, flMmsEnqHdr2, flMmsEnqHdr3, flMmsEnqHdr4, flMmsEnqDtl1, flMmsEnqDtl4,
			flMmsEnqDtl5, flMmsEnqDtl6, flMmsEnqDtl2, flMmsEnqDtl3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Purchase Enquire Details
	private Button btnaddDtl = new GERPButton("Add", "addbt", this);
	// User Input Fields for Purchase Enquire Header
	private ComboBox cbBranch, cbEnqStatus, cbindentno;
	private TextField tfEnqNo;
	private TextArea taEnqRem;
	private PopupDateField dfEnqDate, dfDueDate;
	private Label lbl;
	// User Input Fields for Purchase Enquire Detail
	private ComboBox cbUom, cbEnqDtlStatus;
	private ListSelect lsMaterial, lsVendorName;
	private TextField tfEnqQty;
	private TextArea taEnqDtlRem;
	private Table tblMmsEnqDtl = new GERPTable();
	private BeanItemContainer<MmsEnqHdrDM> beanMmsEnqHdrDM = null;
	private BeanItemContainer<MmsEnqDtlDM> beanMmsEnqDtlDM = null;
	// local variables declaration
	private String enquiryid;
	private Long companyid;
	private Long enquiryId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(MaterialEnquiry.class);
	private static final long serialVersionUID = 1L;
	private Long branchId;
	private Long employeeId;
	private Long moduleId;
	private Long roleId, appScreenId;
	private MmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private String status;
	
	// Constructor received the parameters from Login UI class
	public MaterialEnquiry() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside MaterialEnquiry() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting MaterialEnquiry UI");
		// Initialization for Material Enquire Details user input components
		tfEnqNo = new TextField("Enquiry No");
		tfEnqNo.setWidth("150px");
		tfEnqQty = new TextField();
		tfEnqQty.setValue("0");
		tfEnqQty.setWidth("100");
		dfDueDate = new GERPPopupDateField("Due Date");
		dfDueDate.setInputPrompt("Select Date");
		dfEnqDate = new GERPPopupDateField("Enquiry Date");
		dfEnqDate.setInputPrompt("Select Date");
		taEnqDtlRem = new TextArea("Remarks");
		taEnqDtlRem.setMaxLength(40);
		taEnqDtlRem.setWidth("163");
		taEnqDtlRem.setHeight("75");
		taEnqRem = new GERPTextArea("Remarks");
		taEnqRem.setHeight("35");
		taEnqDtlRem.setMaxLength(100);
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		try {
			ApprovalSchemaDM obj = serviceMmsEnqHdr.getReviewerId(companyid, appScreenId, branchId, roleId).get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbEnqStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_ENQUIRY_HDR, BASEConstants.RP_STATUS);
			} else {
				cbEnqStatus = new GERPComboBox("Status", BASEConstants.T_SMS_P_ENQUIRY_HDR, BASEConstants.PE_STATUS_RV);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		cbEnqStatus.setWidth("150");
		cbEnqStatus.setRequired(true);
		cbEnqDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		lsMaterial = new ListSelect("Material Name");
		lsMaterial.setItemCaptionPropertyId("materialName");
		lsMaterial.setMultiSelect(true);
		loadMatNameList();
		lsMaterial.setImmediate(true);
		lsMaterial.setHeight("100");
		lsMaterial.setWidth("150");
		lsMaterial.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				String[] split = lsMaterial.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "")
						.split(",");
				for (String obj : split) {
					if (obj.trim().length() > 0) {
						cbUom.setValue(servicematerial
								.getMaterialList(Long.valueOf(obj.trim()), companyid, null, null, null, null, null,
										null, null, "F").get(0).getMaterialUOM());
					}
				}
			}
		});
		cbUom = new ComboBox();
		cbUom.setItemCaptionPropertyId("lookupname");
		loadUomList();
		cbUom.setWidth("67");
		cbUom.setHeight("18");
		cbindentno = new ComboBox("Indent No");
		cbindentno.setItemCaptionPropertyId("indentNo");
		cbindentno.setWidth("150");
		loadindent();
		lsVendorName = new ListSelect("Vendor Name ");
		lsVendorName.setItemCaptionPropertyId("vendorName");
		lsVendorName.setMultiSelect(true);
		loadVendorNameList();
		lsVendorName.setWidth("150");
		lsVendorName.setHeight("75");
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadMatDtl();
		btnaddDtl.setStyleName("add");
		btnaddDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (valideteDetailFields()) {
					saveEnqDtl();
				}
			}
		});
		btndelete.setEnabled(false);
		// ClickListener for Enquire Detail Tale
		tblMmsEnqDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMmsEnqDtl.isSelected(event.getItemId())) {
					tblMmsEnqDtl.setImmediate(true);
					btnaddDtl.setCaption("Add");
					btnaddDtl.setStyleName("savebt");
					btndelete.setEnabled(false);
					enqDtlresetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddDtl.setCaption("Update");
					btnaddDtl.setStyleName("savebt");
					btndelete.setEnabled(true);
					editmmsPurDetail();
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
		flMmsEnqHdr1 = new FormLayout();
		flMmsEnqHdr2 = new FormLayout();
		flMmsEnqHdr3 = new FormLayout();
		flMmsEnqHdr1.addComponent(cbBranch);
		flMmsEnqHdr2.addComponent(tfEnqNo);
		flMmsEnqHdr3.addComponent(cbEnqStatus);
		hlSearchLayout.addComponent(flMmsEnqHdr1);
		hlSearchLayout.addComponent(flMmsEnqHdr2);
		hlSearchLayout.addComponent(flMmsEnqHdr3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlUserInputLayout.removeAllComponents();
		flMmsEnqHdr1 = new FormLayout();
		flMmsEnqHdr2 = new FormLayout();
		flMmsEnqHdr3 = new FormLayout();
		flMmsEnqHdr4 = new FormLayout();
		flMmsEnqHdr1.addComponent(tfEnqNo);
		flMmsEnqHdr1.addComponent(cbBranch);
		flMmsEnqHdr1.addComponent(cbindentno);
		flMmsEnqHdr2.addComponent(lsVendorName);
		flMmsEnqHdr3.addComponent(dfEnqDate);
		flMmsEnqHdr3.addComponent(dfDueDate);
		flMmsEnqHdr3.addComponent(taEnqRem);
		flMmsEnqHdr4.addComponent(cbEnqStatus);
		HorizontalLayout hlMmsEnqHDR = new HorizontalLayout();
		hlMmsEnqHDR.addComponent(flMmsEnqHdr1);
		hlMmsEnqHDR.addComponent(flMmsEnqHdr2);
		hlMmsEnqHDR.addComponent(flMmsEnqHdr3);
		lbl = new Label(" ");
		hlMmsEnqHDR.addComponent(lbl);
		hlMmsEnqHDR.addComponent(flMmsEnqHdr4);
		hlMmsEnqHDR.setSpacing(true);
		hlMmsEnqHDR.setMargin(true);
		// Adding MmsEnqDtl components
		// Add components for User Input Layout
		flMmsEnqDtl1 = new FormLayout();
		flMmsEnqDtl2 = new FormLayout();
		flMmsEnqDtl3 = new FormLayout();
		flMmsEnqDtl4 = new FormLayout();
		flMmsEnqDtl5 = new FormLayout();
		flMmsEnqDtl6 = new FormLayout();
		flMmsEnqDtl1.addComponent(lsMaterial);
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfEnqQty);
		hlQtyUom.addComponent(cbUom);
		hlQtyUom.setCaption("Enquiry Qty");
		flMmsEnqDtl2.addComponent(hlQtyUom);
		flMmsEnqDtl2.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		flMmsEnqDtl2.addComponent(taEnqDtlRem);
		flMmsEnqDtl5.addComponent(cbEnqDtlStatus);
		VerticalLayout btn = new VerticalLayout();
		btn.addComponent(btnaddDtl);
		btn.addComponent(btndelete);
		flMmsEnqDtl5.addComponent(btn);
		HorizontalLayout hlMmsEnqdTL = new HorizontalLayout();
		hlMmsEnqdTL.addComponent(flMmsEnqDtl1);
		hlMmsEnqdTL.addComponent(flMmsEnqDtl2);
		hlMmsEnqdTL.addComponent(flMmsEnqDtl3);
		hlMmsEnqdTL.addComponent(flMmsEnqDtl4);
		hlMmsEnqdTL.addComponent(flMmsEnqDtl5);
		hlMmsEnqdTL.addComponent(flMmsEnqDtl6);
		hlMmsEnqdTL.setSpacing(true);
		hlMmsEnqdTL.setMargin(true);
		VerticalLayout vlMmsEnqHDR = new VerticalLayout();
		vlMmsEnqHDR = new VerticalLayout();
		vlMmsEnqHDR.addComponent(hlMmsEnqdTL);
		vlMmsEnqHDR.addComponent(tblMmsEnqDtl);
		vlMmsEnqHDR.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlMmsEnqHDR, "Material Enquiry Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		VerticalLayout vlMmsEnqHdrdTL = new VerticalLayout();
		vlMmsEnqHdrdTL = new VerticalLayout();
		vlMmsEnqHdrdTL.addComponent(GERPPanelGenerator.createPanel(hlMmsEnqHDR));
		vlMmsEnqHdrdTL.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		vlMmsEnqHdrdTL.setSpacing(true);
		vlMmsEnqHdrdTL.setWidth("100%");
		hlUserInputLayout.addComponent(vlMmsEnqHdrdTL);
		hlUserInputLayout.setSizeFull();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// Load Purchase Header
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<MmsEnqHdrDM> list = new ArrayList<MmsEnqHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + cbBranch.getValue() + ", " + cbEnqStatus.getValue());
			list = serviceMmsEnqHdr.getMmsEnqHdrList(companyid, null, tfEnqNo.getValue(), (Long) cbBranch.getValue(),
					(String) cbEnqStatus.getValue(), username);
			recordCnt = list.size();
			beanMmsEnqHdrDM = new BeanItemContainer<MmsEnqHdrDM>(MmsEnqHdrDM.class);
			beanMmsEnqHdrDM.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the MaterialEnquiry. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanMmsEnqHdrDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "enquiryId", "branchName", "enquiryNo", "enquiryStatus",
					"lastUpdateddt", "lastUpdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Enquiry No", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("enquiryId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
			tblMstScrSrchRslt.setPageLength(13);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Purchase Detail
	private void loadMatDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordCnt = listEnqDetails.size();
			beanMmsEnqDtlDM = new BeanItemContainer<MmsEnqDtlDM>(MmsEnqDtlDM.class);
			beanMmsEnqDtlDM.addAll(listEnqDetails);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the dfmatDt. result set");
			tblMmsEnqDtl.setContainerDataSource(beanMmsEnqDtlDM);
			tblMmsEnqDtl.setVisibleColumns(new Object[] { "materialName", "enquiryQty", "matuom", "enqdtlsts",
					"lastUpdateddt", "lastUpdatedby" });
			tblMmsEnqDtl.setColumnHeaders(new String[] { "Material Name", "Enquiry Qty", "UOM", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMmsEnqDtl.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
			tblMmsEnqDtl.setPageLength(4);
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
	
	// Load Uom List
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
	
	private void loadVendorNameList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading VendorNameList");
			BeanContainer<Long, VendorDM> beanVendor = new BeanContainer<Long, VendorDM>(VendorDM.class);
			beanVendor.setBeanIdProperty("vendorId");
			beanVendor.addAll(serviceVendor.getVendorList(null, null, companyid, null, null, null, null, null,
					"Active", null, "P"));
			lsVendorName.setContainerDataSource(beanVendor);
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
		tfEnqQty.setValue("0");
		cbEnqStatus.setValue(null);
		cbindentno.setValue(null);
		listEnqDetails = new ArrayList<MmsEnqDtlDM>();
		tblMmsEnqDtl.removeAllItems();
		cbBranch.setValue(branchId);
		dfDueDate.setValue(null);
		dfEnqDate.setValue(null);
		dfDueDate.setComponentError(null);
		taEnqRem.setValue("");
		taEnqDtlRem.setValue("");
		dfEnqDate.setValue(new Date());
		dfDueDate.setValue(DateUtils.addDays(new Date(), 7));
	}
	
	// Method to edit the values from table into fields to update process
	private void editPurHdr() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				MmsEnqHdrDM enqHdrDM = beanMmsEnqHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				enquiryId = enqHdrDM.getEnquiryId();
				cbBranch.setValue(enqHdrDM.getBranchId());
				tfEnqNo.setReadOnly(false);
				tfEnqNo.setValue(enqHdrDM.getEnquiryNo());
				dfEnqDate.setValue(enqHdrDM.getEnquiryDate());
				dfDueDate.setValue(enqHdrDM.getDueDate());
				for (MMSVendorDtlDM enquiryVendorDtlDM : serviceMMSVendordtl.getmaterialvdrdtl(null, enquiryId, null)) {
					lsVendorName.select(enquiryVendorDtlDM.getVendorid());
				}
				cbindentno.setValue(enqHdrDM.getIndentId());
				if (enqHdrDM.getEnquiryStatus() != null) {
					cbEnqStatus.setValue(enqHdrDM.getEnquiryStatus());
				}
				if (enqHdrDM.getEnqRemark() != null) {
					taEnqRem.setValue(enqHdrDM.getEnqRemark().toString());
				}
				listEnqDetails = serviceMmsEnqDtl.getMmsEnqDtlList(null, enquiryId, null, null,
						(String) cbEnqDtlStatus.getValue());
			}
			loadMatDtl();
			comments = new MmsComments(vlTableForm, null, companyid, null, enquiryId, null, null, null, null, null,
					status);
			comments.loadsrch(true, null, null, null, enquiryId, null, null, null, null, null);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editmmsPurDetail() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			if (tblMmsEnqDtl.getValue() != null) {
				MmsEnqDtlDM enqDtlDM = beanMmsEnqDtlDM.getItem(tblMmsEnqDtl.getValue()).getBean();
				lsMaterial.setValue(null);
				Long matid = enqDtlDM.getMaterialid();
				Collection<?> matids = lsMaterial.getItemIds();
				for (Iterator<?> iterator = matids.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) lsMaterial.getItem(itemId);
					// Get the actual bean and use the data
					MaterialDM st = (MaterialDM) item.getBean();
					if (matid != null && matid.equals(st.getMaterialId())) {
						lsMaterial.select(itemId);
					}
				}
				if (enqDtlDM.getMatuom() != null) {
					cbUom.setValue(enqDtlDM.getMatuom());
				}
				if (enqDtlDM.getEnquiryQty() != null) {
					tfEnqQty.setValue(enqDtlDM.getEnquiryQty().toString());
				}
				if (enqDtlDM.getRemarks() != null) {
					taEnqDtlRem.setValue(enqDtlDM.getRemarks().toString());
				}
				if (enqDtlDM.getEnqdtlsts() != null) {
					cbEnqDtlStatus.setValue(enqDtlDM.getEnqdtlsts());
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
			tfEnqNo.setReadOnly(false);
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEnqStatus.setValue(null);
		tfEnqNo.setValue("");
		tfEnqNo.setReadOnly(false);
		cbBranch.setValue(branchId);
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
		btnaddDtl.setCaption("Add");
		tblMmsEnqDtl.setVisible(true);
		cbBranch.setRequired(true);
		lsVendorName.setRequired(true);
		lsMaterial.setRequired(true);
		resetFields();
		tfEnqNo.setReadOnly(false);
		comments = new MmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null);
	}
	
	@Override
	protected void editDetails() {
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleInputUserLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblMmsEnqDtl.setVisible(true);
		cbBranch.setRequired(true);
		lsVendorName.setRequired(true);
		resetFields();
		editPurHdr();
		editmmsPurDetail();
		lsMaterial.setRequired(true);
		// cbUom.setRequired(true);
		loadMatDtl();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Boolean errorFlag = false;
		cbBranch.setComponentError(null);
		lsVendorName.setComponentError(null);
		dfDueDate.setComponentError(null);
		cbUom.setComponentError(null);
		lsMaterial.setComponentError(null);
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			errorFlag = true;
		}
		if (lsVendorName.getValue() == null || lsVendorName.getValue().toString() == "[]") {
			lsVendorName.setComponentError(new UserError(GERPErrorCodes.NULL_VENDOR_NAME));
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
		if (tblMmsEnqDtl.size() == 0) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRY_QTY));
			lsMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean valideteDetailFields() {
		boolean isValid = true;
		if (cbUom.getValue() == null) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			isValid = false;
		} else {
			cbUom.setComponentError(null);
		}
		if (tfEnqQty.getValue().equals("0")) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRY_QTY));
			isValid = false;
		} else {
			cbUom.setComponentError(null);
			isValid = true;
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfEnqQty.getValue());
			if (achievedQty < 0) {
				cbUom.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATION));
			isValid = false;
		}
		if (lsMaterial.getValue() == null) {
			lsMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			isValid = false;
		} else {
			lsMaterial.setComponentError(null);
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() throws IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		// DtlValidation();
		MmsEnqHdrDM matEnqobj = new MmsEnqHdrDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			matEnqobj = beanMmsEnqHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		matEnqobj.setEnquiryNo(tfEnqNo.getValue());
		matEnqobj.setCompanyId(companyid);
		matEnqobj.setEnqRemark(taEnqRem.getValue().toString());
		matEnqobj.setBranchId((Long) cbBranch.getValue());
		matEnqobj.setDueDate((Date) dfDueDate.getValue());
		matEnqobj.setEnquiryDate((Date) dfEnqDate.getValue());
		matEnqobj.setIndentId((Long) cbindentno.getValue());
		matEnqobj.setEnquiryStatus(((String) cbEnqStatus.getValue()));
		matEnqobj.setPreparedBy(employeeId);
		matEnqobj.setLastUpdateddt(DateUtils.getcurrentdate());
		matEnqobj.setLastUpdatedby(username);
		serviceMmsEnqHdr.saveorUpdateMmsEnqHdrDetails(matEnqobj);
		enquiryId = matEnqobj.getEnquiryId();
		String[] split = lsVendorName.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
		for (String obj : split) {
			if (obj.trim().length() > 0) {
				MMSVendorDtlDM enqvendtl = new MMSVendorDtlDM();
				enqvendtl.setEnqid(matEnqobj.getEnquiryId());
				enqvendtl.setVendorid(Long.valueOf(obj.trim()));
				serviceMMSVendordtl.save(enqvendtl);
			}
		}
		@SuppressWarnings("unchecked")
		Collection<MmsEnqDtlDM> itemIds = (Collection<MmsEnqDtlDM>) tblMmsEnqDtl.getVisibleItemIds();
		for (MmsEnqDtlDM save : (Collection<MmsEnqDtlDM>) itemIds) {
			save.setEnquiryId(Long.valueOf(matEnqobj.getEnquiryId().toString()));
			serviceMmsEnqDtl.save(save);
		}
		comments.resetfields();
		if (tblMstScrSrchRslt.getValue() == null) {
			try {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_ENQRYNO")
						.get(0);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "MM_ENQRYNO");
				}
			}
			catch (Exception e) {
			}
		}
		tfEnqNo.setReadOnly(false);
		tfEnqNo.setValue(matEnqobj.getEnquiryNo());
		comments.saveEnquiry(matEnqobj.getEnquiryId(), matEnqobj.getEnquiryStatus());
		comments.resetfields();
		enqDtlresetFields();
		loadSrchRslt();
		resetFields();
	}
	
	private void saveEnqDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			int count = 0;
			String[] split = lsMaterial.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
			for (String obj : split) {
				if (obj.trim().length() > 0) {
					for (MmsEnqDtlDM mmsEnqDtlDM : listEnqDetails) {
						if (mmsEnqDtlDM.getMaterialid().equals(Long.valueOf(obj.trim()))) {
							count++;
							break;
						}
					}
					if (tblMmsEnqDtl.getValue() != null) {
						count = 0;
					}
					if (count == 0) {
						MmsEnqDtlDM enqDtlObj = new MmsEnqDtlDM();
						if (tblMmsEnqDtl.getValue() != null) {
							enqDtlObj = beanMmsEnqDtlDM.getItem(tblMmsEnqDtl.getValue()).getBean();
							listEnqDetails.remove(enqDtlObj);
						}
						if (lsMaterial.getValue() != null) {
							enqDtlObj.setMaterialid(Long.valueOf(obj.trim()));
							enqDtlObj.setMaterialName(servicematerial
									.getMaterialList(Long.valueOf(obj.trim()), null, null, null, null, null, null,
											null, null, "P").get(0).getMaterialName());
						}
						enqDtlObj.setMatuom(cbUom.getValue().toString());
						enqDtlObj.setRemarks(taEnqDtlRem.getValue().toString());
						if (tfEnqQty.getValue() != null && tfEnqQty.getValue().trim().length() > 0) {
							enqDtlObj.setEnquiryQty(Long.valueOf(tfEnqQty.getValue()));
						}
						if (cbEnqDtlStatus.getValue() != null) {
							enqDtlObj.setEnqdtlsts((cbEnqDtlStatus.getValue().toString()));
						}
						enqDtlObj.setLastUpdateddt(DateUtils.getcurrentdate());
						enqDtlObj.setLastUpdatedby(username);
						listEnqDetails.add(enqDtlObj);
						btnaddDtl.setCaption("Add");
						loadMatDtl();
						count = 0;
					} else {
						lsMaterial.setComponentError(new UserError("Material already Exist.."));
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
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", enquiryid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMmsEnqDtl.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		tfEnqNo.setReadOnly(false);
		cbBranch.setRequired(false);
		lsVendorName.setRequired(false);
		enqDtlresetFields();
		resetFields();
		loadSrchRslt();
	}
	
	private void enqDtlresetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		lsMaterial.setValue(null);
		taEnqDtlRem.setValue("");
		cbUom.setValue(null);
		tfEnqQty.setValue("0");
		cbEnqDtlStatus.setValue(cbEnqDtlStatus.getItemIds().iterator().next());
	}
	
	private void loadMatNameList() {
		try {
			BeanContainer<Long, MaterialDM> beanVendor = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
			beanVendor.setBeanIdProperty("materialId");
			beanVendor.addAll(servicematerial.getMaterialList(null, companyid, null, null, null, null, null, null,
					"Active", "P"));
			lsMaterial.setContainerDataSource(beanVendor);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Indent No
	private void loadindent() {
		try {
			BeanContainer<Long, IndentHdrDM> beanIndent = new BeanContainer<Long, IndentHdrDM>(IndentHdrDM.class);
			beanIndent.setBeanIdProperty("indentId");
			beanIndent.addAll(serviceindent.getMmsIndentHdrList(null, null, null, companyid, null, null, null, null,
					null, "F"));
			cbindentno.setContainerDataSource(beanIndent);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deleteDetails() {
		try {
			MmsEnqDtlDM save = new MmsEnqDtlDM();
			if (tblMmsEnqDtl.getValue() != null) {
				save = beanMmsEnqDtlDM.getItem(tblMmsEnqDtl.getValue()).getBean();
				listEnqDetails.remove(save);
				enqDtlresetFields();
				loadMatDtl();
				btndelete.setEnabled(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	protected void printDetails() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, Long> parameterMap = new HashMap<String, Long>();
			parameterMap.put("ENQID", enquiryId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/mmsenquiry"); // productlist is the name of my jasper
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
}
