/**
 * File Name 		: Indent.java 
 * Description 		: this class is used for add/edit Indent  details. 
 * Author 			: Madhu T
 * Date 			: Oct-17-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1         Oct-17-2014     	Madhu T	        Initial Version
 * 0.2         Nov-10-2014     	Arun Jeyaraj R       Modifications
 **/
package com.gnts.mms.txn;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.hamcrest.core.IsSame;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.mst.Tax;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.IndentDtlDM;
import com.gnts.mms.domain.txn.IndentHdrDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.IndentDtlService;
import com.gnts.mms.service.txn.IndentHdrService;
import com.gnts.saarc.util.SerialNumberGenerator;
import com.vaadin.ui.CheckBox;
import com.vaadin.data.Property;
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
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Indent extends BaseTransUI {
	// Bean Creation
	private IndentHdrService serviceIndentHdr = (IndentHdrService) SpringContextHelper.getBean("IndentHdr");
	private IndentDtlService serviceIndentDtl = (IndentDtlService) SpringContextHelper.getBean("IndentDtl");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private DepartmentService serviceDepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private List<IndentDtlDM> listIndentDtls = null;
	// form layout for input controls
	private FormLayout flIndentCol1, flIndentCol2, flIndentCol3, flIndentCol4, flIndentCol5, flIndentDtlCol1,
			flIndentDtlCol2;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlIndentDtl = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Work Order Details
	private Button btnAddDtl = new GERPButton("Add", "addbt", this);
	private ComboBox cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	// Dtl Status ComboBox
	private ComboBox cbIndStatus = new GERPComboBox("Status", BASEConstants.T_SMS_INVOICE_HDR,
			BASEConstants.INVOICE_STATUS);
	private TextField tfIndNo, tfIndQty;
	private ComboBox cbIndType, cbBranchId, cbUom, cbDepartment;
	private ListSelect cbMatName;
	private PopupDateField dfIndDate, dfExpDt;
	private TextArea taRemarks;
	private Table tblIndentDtl;
	private BeanItemContainer<IndentHdrDM> beanIndentHdrDM = null;
	private BeanItemContainer<IndentDtlDM> beanIndentDtlDM = null;
	// local variables declaration
	private String taxSlapId;
	private Long companyid, employeeId;
	private Long indentHdrId, moduleId, branchId;
	private int recordCnt = 0;
	private MmsComments comments;
	private String username;
	private VerticalLayout vlTableForm = new VerticalLayout();
	// Initialize logger
	private Logger logger = Logger.getLogger(Tax.class);
	private String status, indType = null;
	private static final long serialVersionUID = 1L;
	
	// Constructor received the parameters from Login UI class
	public Indent() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = (Long) UI.getCurrent().getSession().getAttribute("employeeId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Indent() constructor");
		// Loading the UI
		buildView(true);
	}
	
	public Indent(Long indentHdrId) {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = (Long) UI.getCurrent().getSession().getAttribute("employeeId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Indent() constructor");
		// Loading the UI
		buildView(false);
		this.indentHdrId = indentHdrId;
		cbIndStatus.setValue(null);
		if (indentHdrId != 0L) {
			loadSrchRslt();
			tblMstScrSrchRslt.setValue(tblMstScrSrchRslt.getItemIds().iterator().next());
		}
		hlUserIPContainer.setVisible(true);
		hlUserIPContainer.setEnabled(true);
		hlSrchContainer.setVisible(false);
		btnPrint.setVisible(true);
		btnSave.setVisible(true);
		btnCancel.setVisible(true);
		btnSearch.setVisible(false);
		btnEdit.setEnabled(false);
		btnAdd.setEnabled(false);
		btnReset.setVisible(false);
		btnScreenName.setVisible(true);
		btnAuditRecords.setEnabled(true);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		hlUserIPContainer.removeAllComponents();
		// Dummy implementation, actual will be implemented in extended
		// class
		if (indentHdrId != 0L) {
			editDetails();
		} else {
			addDetails();
			cbIndType.setValue("Store Indent");
			cbIndType.setEnabled(true);
		}
	}
	
	private void buildView(Boolean isLoadFullList) {
		// Initialization for work order Details user input components
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Indent UI");
		btnAddDtl.setStyleName("add");
		btnAddDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDtlDetails()) {
					saveindentDtlListDetails();
				}
			}
		});
		btndelete.setEnabled(false);
		tblIndentDtl = new GERPTable();
		tblIndentDtl.setWidth("656px");
		tblIndentDtl.setPageLength(6);
		tblIndentDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblIndentDtl.isSelected(event.getItemId())) {
					tblIndentDtl.setImmediate(true);
					btnAddDtl.setCaption("Add");
					btnAddDtl.setStyleName("savebt");
					btndelete.setEnabled(false);
					indentDtlresetField();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtl.setCaption("Update");
					btnAddDtl.setStyleName("savebt");
					btndelete.setEnabled(true);
					cbMatName.setComponentError(null);
					editIntentDtls();
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
		// Indent No. TextField
		tfIndNo = new GERPTextField("Indent No.");
		tfIndNo.setWidth("130px");
		tfIndNo.setMaxLength(40);
		tfIndNo.setValue("");
		tfIndQty = new TextField();
		tfIndQty.setValue("0");
		tfIndQty.setWidth("90");
		cbUom = new ComboBox();
		cbUom.setItemCaptionPropertyId("lookupname");
		loadMaterialUOMList();
		cbUom.setWidth("77");
		cbUom.setHeight("18");
		cbUom.setReadOnly(true);
		// Indent Type GERPComboBox
		cbIndType = new GERPComboBox("Indent Type");
		cbIndType.addItem("Purchase Indent");
		cbIndType.addItem("Store Indent");
		cbIndType.setWidth("130px");
		cbIndType.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				loadSerialNo();
			}
		});
		cbDepartment = new GERPComboBox("Department");
		cbDepartment.setItemCaptionPropertyId("deptname");
		loadDepartmentList();
		cbDepartment.setWidth("130");
		// Indent Date PopupDateField
		dfIndDate = new GERPPopupDateField("Indent Date");
		dfIndDate.setInputPrompt("Select Date");
		// Branch Name GERPComboBox
		cbBranchId = new GERPComboBox("Branch Name");
		cbBranchId.setItemCaptionPropertyId("branchName");
		cbBranchId.setWidth("130px");
		loadBranchList();
		// Expected Date field
		/*
		 * ckdateValid = new CheckBox("Same Expected Date?"); ckdateValid.addValueChangeListener(new
		 * Property.ValueChangeListener() {
		 *//**
			 * 
			 */
		/*
		 * private static final long serialVersionUID = 1L; public void valueChange(ValueChangeEvent event) { if
		 * (dfIndDate.getValue() != null && dfExpDt.getValue() != null) { dfExpDt.setValue(dfIndDate.getValue());
		 * dfExpDt.setEnabled(false); } else { dfExpDt.setEnabled(true); ckdateValid.setValue(null); } } });
		 */
		dfExpDt = new GERPPopupDateField("Expected Date");
		dfExpDt.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					if ((dfIndDate.getValue().compareTo(dfExpDt.getValue())) < 0) {
					} else {
						if (dfIndDate.getValue() != null) {
							dfExpDt.setValue(null);
							Notification.show("Enter a valid date from Indent Date");
						}
					}
				}
				catch (Exception e) {
				}
			}
		});
		// Remarks TextArea
		taRemarks = new TextArea("Purpose");
		taRemarks.setWidth("110px");
		taRemarks.setHeight("50px");
		taRemarks.setNullRepresentation("");
		// Dtl Status ComboBox
		cbIndStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbIndStatus.setWidth("90px");
		// Indent Detail
		// Material Name combobox
		cbMatName = new ListSelect("Material Name");
		cbMatName.setItemCaptionPropertyId("materialName");
		cbMatName.setMultiSelect(true);
		loadMaterialList();
		cbMatName.setImmediate(true);
		cbMatName.setHeight("250");
		cbMatName.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbMatName.getValue() != null) {
					System.out.println("UOM--->" + cbMatName.getValue());
					String[] split = cbMatName.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "")
							.split(",");
					for (String obj : split) {
						if (obj.trim().length() > 0) {
							cbUom.setReadOnly(false);
							cbUom.setValue(serviceMaterial
									.getMaterialList(Long.valueOf(obj.trim()), companyid, null, null, null, null, null,
											null, null, "F").get(0).getMaterialUOM());
							cbUom.setReadOnly(true);
						}
					}
				}
			}
		});
		// Hdr Combobox
		cbIndStatus = new GERPComboBox("Status", BASEConstants.T_SMS_INVOICE_HDR, BASEConstants.INVOICE_STATUS);
		// Indent No text field
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		if (isLoadFullList) {
			loadSrchRslt();
		}
		loadIndentDtl();
		btnAddDtl.setStyleName("add");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flIndentCol1 = new FormLayout();
		flIndentCol2 = new FormLayout();
		flIndentCol4 = new FormLayout();
		flIndentCol1.addComponent(tfIndNo);
		flIndentCol2.addComponent(cbIndType);
		flIndentCol4.addComponent(cbIndStatus);
		hlSearchLayout.addComponent(flIndentCol1);
		hlSearchLayout.addComponent(flIndentCol2);
		hlSearchLayout.addComponent(flIndentCol4);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flIndentCol1 = new FormLayout();
		flIndentCol2 = new FormLayout();
		flIndentCol3 = new FormLayout();
		flIndentCol4 = new FormLayout();
		flIndentCol5 = new FormLayout();
		flIndentCol1.addComponent(cbBranchId);
		flIndentCol1.addComponent(cbIndType);
		cbIndType.setEnabled(true);
		flIndentCol2.addComponent(tfIndNo);
		flIndentCol2.addComponent(cbDepartment);
		flIndentCol3.addComponent(dfIndDate);
		// flIndentCol3.addComponent(ckdateValid);
		flIndentCol3.addComponent(dfExpDt);
		flIndentCol3.setSpacing(true);
		flIndentCol3.setMargin(true);
		flIndentCol4.addComponent(taRemarks);
		taRemarks.setWidth("140");
		flIndentCol5.addComponent(cbIndStatus);
		cbIndStatus.setWidth("130");
		HorizontalLayout hlTax = new HorizontalLayout();
		hlTax.addComponent(flIndentCol1);
		hlTax.addComponent(flIndentCol2);
		hlTax.addComponent(flIndentCol3);
		hlTax.addComponent(flIndentCol4);
		hlTax.addComponent(flIndentCol5);
		hlTax.setSpacing(true);
		hlTax.setMargin(true);
		// Adding TaxSlap components
		// Add components for User Input Layout
		flIndentDtlCol1 = new FormLayout();
		flIndentDtlCol2 = new FormLayout();
		flIndentDtlCol1.addComponent(cbMatName);
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfIndQty);
		hlQtyUom.addComponent(cbUom);
		hlQtyUom.setCaption("Indent Qty");
		cbUom.setWidth("60");
		flIndentDtlCol2.addComponent(hlQtyUom);
		flIndentDtlCol2.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		flIndentDtlCol2.addComponent(cbDtlStatus);
		cbDtlStatus.setWidth("145px");
		flIndentDtlCol2.addComponent(btnAddDtl);
		flIndentDtlCol2.addComponent(btndelete);
		flIndentDtlCol2.setMargin(true);
		flIndentDtlCol2.setSpacing(true);
		hlIndentDtl = new HorizontalLayout();
		hlIndentDtl.addComponent(flIndentDtlCol1);
		hlIndentDtl.addComponent(flIndentDtlCol2);
		hlIndentDtl.addComponent(tblIndentDtl);
		hlIndentDtl.setSpacing(true);
		hlIndentDtl.setMargin(true);
		tfIndNo.setReadOnly(true);
		VerticalLayout vlIndent = new VerticalLayout();
		vlIndent = new VerticalLayout();
		vlIndent.addComponent(hlIndentDtl);
		vlIndent.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlIndent, "Indent Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		VerticalLayout vlIndentHdrAndDtl = new VerticalLayout();
		vlIndentHdrAndDtl = new VerticalLayout();
		vlIndentHdrAndDtl.addComponent(GERPPanelGenerator.createPanel(hlTax));
		vlIndentHdrAndDtl.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		vlIndentHdrAndDtl.setSpacing(true);
		vlIndentHdrAndDtl.setWidth("100%");
		hlUserInputLayout.addComponent(vlIndentHdrAndDtl);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	// Load for Indent Search Hdr
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<IndentHdrDM> list = new ArrayList<IndentHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfIndNo.getValue() + ", " + cbIndStatus.getValue());
			list = serviceIndentHdr.getMmsIndentHdrList(tfIndNo.getValue(), (String) cbIndType.getValue(), null, null,
					indentHdrId, null, null, null, (String) cbIndStatus.getValue(), "F");
			recordCnt = list.size();
			beanIndentHdrDM = new BeanItemContainer<IndentHdrDM>(IndentHdrDM.class);
			beanIndentHdrDM.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Indent. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanIndentHdrDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "indentId", "indentNo", "indentType", "indentStatus",
					"last_updated_dt", "last_updated_by" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Indent No", "Indent Type", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("indentId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("last_updated_by", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load for Indent Search Dtl
	private void loadIndentDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			logger.info("Company ID : " + companyid + " | saveindentDtlListDetails User Name : " + username + " > "
					+ "Search Parameters are " + companyid + ", " + tfIndQty.getValue() + ", "
					+ (String) cbDtlStatus.getValue());
			recordCnt = listIndentDtls.size();
			beanIndentDtlDM = new BeanItemContainer<IndentDtlDM>(IndentDtlDM.class);
			beanIndentDtlDM.addAll(listIndentDtls);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxslap. result set");
			tblIndentDtl.setContainerDataSource(beanIndentDtlDM);
			tblIndentDtl.setVisibleColumns(new Object[] { "materialName", "materialUOM", "indentQty", "balenceQty",
					"status" });
			tblIndentDtl.setColumnHeaders(new String[] { "Material Name", "UOM", "Indent Qty", "Bal. Qty", "Status" });
			tblIndentDtl.setColumnFooter("status", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Uom List
	private void loadMaterialUOMList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Material UOM Search...");
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
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbIndType.setEnabled(false);
		cbIndType.setValue(null);
		dfIndDate.setValue(new Date());
		dfExpDt.setValue(DateUtils.addDays(new Date(), 7));
		dfExpDt.setEnabled(true);
		cbDepartment.setValue(null);
		tfIndNo.setReadOnly(false);
		tfIndNo.setValue("");
		taRemarks.setValue(null);
		cbBranchId.setValue(branchId);
		cbIndStatus.setValue(cbIndStatus.getItemIds().iterator().next());
		cbIndType.setComponentError(null);
		dfIndDate.setComponentError(null);
		cbBranchId.setComponentError(null);
		cbMatName.setComponentError(null);
		tfIndQty.setComponentError(null);
		listIndentDtls = new ArrayList<IndentDtlDM>();
		tblIndentDtl.removeAllItems();
		dfExpDt.setEnabled(true);
		try {
			if (UI.getCurrent().getSession().getAttribute("IS_IND_STORE") != null
					&& (Boolean) UI.getCurrent().getSession().getAttribute("IS_IND_STORE")) {
				dfExpDt.setValue(dfIndDate.getValue());
				dfExpDt.setEnabled(true);
				cbBranchId.setEnabled(true);
			} else {
				cbDepartment.setValue(null);
				cbBranchId.setEnabled(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method to edit the values from table into fields to update process
	private void editHdrIndentDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				IndentHdrDM editHdrIndent = beanIndentHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				indentHdrId = editHdrIndent.getIndentId();
				logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Tax. Id -> "
						+ indentHdrId);
				tfIndNo.setReadOnly(false);
				tfIndNo.setValue(editHdrIndent.getIndentNo());
				cbIndType.setValue(editHdrIndent.getIndentType());
				if (editHdrIndent.getIndentDate() != null) {
					dfIndDate.setValue(editHdrIndent.getIndentDate1());
				}
				cbBranchId.setValue(editHdrIndent.getBranchId());
				cbDepartment.setValue(editHdrIndent.getDeptid());
				dfExpDt.setValue(editHdrIndent.getExpectedDate());
				taRemarks.setValue(editHdrIndent.getIndentRemarks());
				cbIndStatus.setValue(editHdrIndent.getIndentStatus());
				listIndentDtls.addAll(serviceIndentDtl.getIndentDtlDMList(null, indentHdrId, null, null, "F"));
			}
			loadIndentDtl();
			comments = new MmsComments(vlTableForm, null, companyid, null, null, null, null, indentHdrId, null, null,
					status);
			comments.loadsrch(true, null, null, null, null, null, null, indentHdrId, null, null);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method to edit the values from table into fields to update process
	private void editIntentDtls() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblIndentDtl.getValue() != null) {
				IndentDtlDM indentDtlDM = new IndentDtlDM();
				indentDtlDM = beanIndentDtlDM.getItem(tblIndentDtl.getValue()).getBean();
				cbMatName.setValue(null);
				Long matId = indentDtlDM.getMaterialId();
				Collection<?> empColId = cbMatName.getItemIds();
				for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
					Object itemIdClient = (Object) iteratorclient.next();
					BeanItem<?> itemclient = (BeanItem<?>) cbMatName.getItem(itemIdClient);
					// Get the actual bean and use the data
					MaterialDM matObj = (MaterialDM) itemclient.getBean();
					if (matId != null && matId.equals(matObj.getMaterialId())) {
						cbMatName.select(itemIdClient);
					}
				}
				if (indentDtlDM.getIndentQty() != null) {
					tfIndQty.setValue(indentDtlDM.getIndentQty().toString());
				}
				if (indentDtlDM.getMaterialUOM() != null) {
					cbUom.setReadOnly(false);
					cbUom.setValue(indentDtlDM.getMaterialUOM());
					cbUom.setReadOnly(true);
				}
				if (indentDtlDM.getStatus() != null) {
					cbDtlStatus.setValue(indentDtlDM.getStatus());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
			tfIndNo.setReadOnly(false);
		}
	}
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfIndNo.setValue("");
		cbIndStatus.setValue(cbIndStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		cbIndType.setRequired(true);
		cbBranchId.setRequired(true);
		cbBranchId.setValue(branchId);
		cbMatName.setRequired(true);
		tfIndQty.setValue("0");
		resetFields();
		indentDtlresetField();
		loadIndentDtl();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtl.setCaption("Add");
		tblIndentDtl.setVisible(true);
		comments = new MmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null);
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Tax. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_WORKORDER_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", taxSlapId);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		cbIndType.setComponentError(null);
		dfIndDate.setComponentError(null);
		cbBranchId.setComponentError(null);
		cbMatName.setComponentError(null);
		tfIndQty.setComponentError(null);
		cbBranchId.setRequired(false);
		cbMatName.setRequired(false);
		tfIndNo.setReadOnly(false);
		indentDtlresetField();
		hlCmdBtnLayout.setVisible(true);
		tblIndentDtl.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		indentHdrId = null;
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbIndType.setRequired(true);
		cbBranchId.setRequired(true);
		cbMatName.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		resetFields();
		editHdrIndentDetails();
		editIntentDtls();
	}
	
	// reset the input values to IndentDtl
	private void indentDtlresetField() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMatName.setValue(null);
		tfIndQty.setValue("0");
		cbUom.setReadOnly(false);
		cbUom.setValue(null);
		cbUom.setReadOnly(true);
		cbUom.setComponentError(null);
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		btnAddDtl.setCaption("Add");
	}
	
	// validation for Detail table
	private boolean validateDtlDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbMatName.getValue() == null || cbMatName.getValue().toString() == "[]") {
			cbMatName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			isValid = false;
		} else {
			cbMatName.setComponentError(null);
		}
		if (cbUom.getValue() == null) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			isValid = false;
		} else {
			cbUom.setComponentError(null);
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfIndQty.getValue());
			if (achievedQty < 0) {
				cbUom.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATION));
			isValid = false;
		}
		if (tfIndQty.getValue().equals("0")) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRY_QTY));
			isValid = false;
		} else {
			cbUom.setComponentError(null);
			isValid = true;
		}
		return isValid;
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if (cbIndType.getValue() == null) {
			cbIndType.setComponentError(new UserError(GERPErrorCodes.NULL_INDENT_TYPE));
			errorFlag = true;
		}
		if (cbBranchId.getValue() == null) {
			cbBranchId.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			errorFlag = true;
		}
		if (tblIndentDtl.size() == 0) {
			cbMatName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			cbUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			tfIndQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			IndentHdrDM indentObj = new IndentHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				indentObj = beanIndentHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			indentObj.setIndentNo(tfIndNo.getValue());
			indentObj.setCompanyId(companyid);
			indentObj.setIndentType((String) cbIndType.getValue());
			indentObj.setIndentDate((Date) dfIndDate.getValue());
			indentObj.setExpectedDate((Date) dfExpDt.getValue());
			indentObj.setBranchId((Long.valueOf(cbBranchId.getValue().toString())));
			indentObj.setDeptid((Long.valueOf(cbDepartment.getValue().toString())));
			indentObj.setRaisedBy(employeeId);
			indentObj.setPreparedBy(employeeId);
			indentObj.setReviewedBy(employeeId);
			indentObj.setActionedBy(employeeId);
			indentObj.setIndentRemarks((String) taRemarks.getValue());
			if (cbIndStatus.getValue() != null) {
				indentObj.setIndentStatus((String) cbIndStatus.getValue());
			}
			indentObj.setLast_updated_dt(DateUtils.getcurrentdate());
			indentObj.setLast_updated_by(username);
			serviceIndentHdr.saveorUpdateMmsIndentHdrDetails(indentObj);
			indentHdrId = indentObj.getIndentId();
			@SuppressWarnings("unchecked")
			Collection<IndentDtlDM> colPlanDtls = ((Collection<IndentDtlDM>) tblIndentDtl.getVisibleItemIds());
			for (IndentDtlDM saveDtl : (Collection<IndentDtlDM>) colPlanDtls) {
				saveDtl.setIndentHdrId(Long.valueOf(indentObj.getIndentId()));
				serviceIndentDtl.saveorUpdate(saveDtl);
			}
			try {
				updateSerial();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			comments.saveindent(indentObj.getIndentId(), indentObj.getIndentStatus());
			loadSrchRslt();
			loadIndentDtl();
			resetFields();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Save method for Detail table in temporary
	private void saveindentDtlListDetails() {
		try {
			int count = 0;
			String[] split = cbMatName.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
			for (String obj : split) {
				if (obj.trim().length() > 0) {
					for (IndentDtlDM indentDtlDM : listIndentDtls) {
						if (indentDtlDM.getMaterialId() == Long.valueOf(obj.trim())) {
							count++;
							break;
						}
					}
					if (tblIndentDtl.getValue() != null) {
						count = 0;
					}
					if (count == 0) {
						IndentDtlDM indentDtlObj = new IndentDtlDM();
						if (tblIndentDtl.getValue() != null) {
							indentDtlObj = beanIndentDtlDM.getItem(tblIndentDtl.getValue()).getBean();
							listIndentDtls.remove(indentDtlObj);
						}
						UI.getCurrent().getSession().setAttribute("indqty", Long.valueOf(tfIndQty.getValue()));
						if (cbMatName.getValue() != null) {
							indentDtlObj.setMaterialId(Long.valueOf(obj.trim()));
							indentDtlObj.setMaterialName(serviceMaterial
									.getMaterialList(Long.valueOf(obj.trim()), null, null, null, null, null, null,
											null, null, "P").get(0).getMaterialName());
						}
						if (Long.valueOf(tfIndQty.getValue()) != null) {
							indentDtlObj.setIndentQty(Long.valueOf(tfIndQty.getValue()));
							indentDtlObj.setBalenceQty(Long.valueOf(tfIndQty.getValue()));
						}
						cbUom.setReadOnly(false);
						indentDtlObj.setMaterialUOM(cbUom.getValue().toString());
						cbUom.setReadOnly(true);
						if (cbDtlStatus.getValue() != null) {
							indentDtlObj.setStatus((String) cbDtlStatus.getValue());
						}
						indentDtlObj.setLastUpdatedDt(DateUtils.getcurrentdate());
						indentDtlObj.setLastUpdatedBy(username);
						listIndentDtls.add(indentDtlObj);
						loadIndentDtl();
						btnAddDtl.setCaption("Add");
						count = 0;
					} else {
						cbMatName.setComponentError(new UserError("Material Already Exist.."));
					}
				}
			}
			indentDtlresetField();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadBranchList()-->this function is used for load the branch name
	 */
	private void loadBranchList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
			BeanContainer<Long, BranchDM> beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanBranchDM.setBeanIdProperty("branchId");
			beanBranchDM.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
			cbBranchId.setContainerDataSource(beanBranchDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadMaterialList()-->this function is used for load the Material name
	 */
	private void loadMaterialList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Material UOM Search...");
			BeanContainer<Long, MaterialDM> beanMaterial = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
			beanMaterial.setBeanIdProperty("materialId");
			beanMaterial.addAll(serviceMaterial.getMaterialList(null, companyid, null, null, null, null, null, null,
					"Active", "P"));
			cbMatName.setContainerDataSource(beanMaterial);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadDepartmentList()-->this function is used for load the Department list
	 */
	private void loadDepartmentList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Department Search...");
			BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
			beanDepartment.setBeanIdProperty("deptid");
			beanDepartment.addAll(serviceDepartmant.getDepartmentList(companyid, null, "Active", "P"));
			cbDepartment.setContainerDataSource(beanDepartment);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// delete row in temporary table
	private void deleteDetails() {
		try {
			IndentDtlDM save = new IndentDtlDM();
			if (tblIndentDtl.getValue() != null) {
				save = beanIndentDtlDM.getItem(tblIndentDtl.getValue()).getBean();
				listIndentDtls.remove(save);
				indentDtlresetField();
				loadIndentDtl();
				btndelete.setEnabled(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
			parameterMap.put("INDID", indentHdrId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/indent"); // indent is the name of my jasper
			// file.
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
	
	private void loadSerialNo() {
		// TODO Auto-generated method stub
		try {
			indType = ((String) cbIndType.getValue()).substring(0, 1);
			tfIndNo.setReadOnly(false);
			tfIndNo.setValue("");
			if (indType.equals("S")) {
				tfIndNo.setValue(SerialNumberGenerator
						.generateIndNo(companyid, branchId, moduleId, "MM_INDNO", indType));
			} else if (indType.equals("P")) {
				cbDepartment.setValue("Store");
				tfIndNo.setValue(SerialNumberGenerator.generateIndNo(companyid, branchId, moduleId, "MM_INDNOP",
						indType));
			}
			tfIndNo.setReadOnly(true);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void updateSerial() {
		if (indType.equals("S")) {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_INDNO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "MM_INDNO");
			}
		} else if (indType.equals("P")) {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_INDNOP").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "MM_INDNOP");
			}
		}
	}
}
