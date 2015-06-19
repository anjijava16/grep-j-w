/**
 * File Name 		: IndentIssueReturn.java 
 * Description 		: this class is used for add/edit IndentIssueReturn  details. 
 * Author 			: Madhu T
 * Date 			: Oct-28-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1         Oct-28-2014     	Madhu T	       		 Initial Version
 **/
package com.gnts.mms.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.mst.Tax;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.IndentDtlDM;
import com.gnts.mms.domain.txn.IndentIssueHdrDM;
import com.gnts.mms.domain.txn.IndentIssueReturnDM;
import com.gnts.mms.domain.txn.MaterialLedgerDM;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.domain.txn.PoReceiptDtlDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.IndentDtlService;
import com.gnts.mms.service.txn.IndentIssueHdrService;
import com.gnts.mms.service.txn.IndentIssueReturnService;
import com.gnts.mms.service.txn.MaterialLedgerService;
import com.gnts.mms.domain.txn.IndentIssueReturnDM;
import com.gnts.mms.service.txn.MaterialStockService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class IndentIssueReturn extends BaseUI {
	// Bean Creation
	private IndentIssueReturnService serviceIndentHdr = (IndentIssueReturnService) SpringContextHelper
			.getBean("IndentIssueReturn");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private IndentDtlService serviceIndentDtlDM = (IndentDtlService) SpringContextHelper.getBean("IndentDtl");
	private IndentIssueHdrService serviceIndentIssueHdr = (IndentIssueHdrService) SpringContextHelper
			.getBean("IndentIssueHdr");
	private MaterialLedgerService serviceledger = (MaterialLedgerService) SpringContextHelper.getBean("materialledger");
	private MaterialStockService serviceMaterialStock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private MaterialService servicematerial = (MaterialService) SpringContextHelper.getBean("material");
	// form layout for input controls
	private FormLayout fltaxCol1, fltaxCol2, fltaxCol3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlTax = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Dtl Status ComboBox
	private ComboBox cbIndStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private TextField tfReturnQty, tfissueqty;
	private ComboBox cbMatName, cbIssueName, cbReturnReason, cbStockType;
	private TextArea taRemarks;
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	private BeanItemContainer<IndentIssueReturnDM> beanIndentIssueReturnDM = null;
	// local variables declaration
	private String taxSlapId;
	private Long companyid, moduleId, indqty, branchId;
	private Long indentId;
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	private Table tblIndentReturnDTl = new GERPTable();
	// Initialize logger
	private Logger logger = Logger.getLogger(Tax.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor received the parameters from Login UI class
	public IndentIssueReturn() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside IndentIssueReturn() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		// Initialization for work order Details user input components
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Indent UI");
		// Indent Type GERPComboBox
		cbIssueName = new GERPComboBox("Issue Id");
		cbIssueName.setItemCaptionPropertyId("issueId");
		cbIssueName.setWidth("200");
		loadIssueList();
		cbIssueName.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadMaterialList();
				}
			}
		});
		// Raised By GERPComboBox
		cbStockType = new GERPComboBox("Stock Type");
		cbStockType.addItem("New");
		cbStockType.addItem("Scrap");
		cbStockType.addItem("Refurbish");
		// Remarks TextArea
		taRemarks = new TextArea("Remarks");
		taRemarks.setWidth("200px");
		taRemarks.setHeight("50px");
		taRemarks.setNullRepresentation("");
		// Dtl Status ComboBox
		cbIndStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbIndStatus.setWidth("200");
		// Indent Detail
		// Material Name combobox
		cbMatName = new GERPComboBox("Material Name");
		cbMatName.setWidth("200");
		cbMatName.setItemCaptionPropertyId("materialName");
		loadMaterialNameList();
		cbMatName.setImmediate(true);
		cbMatName.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbMatName.getValue() != null) {
					tfissueqty.setValue(Long.valueOf(((IndentDtlDM) cbMatName.getValue()).getIndentQty())
							- Long.valueOf(((IndentDtlDM) cbMatName.getValue()).getBalenceQty()) + "");
				}
			}
		});
		// Indent Qty.GERPTextField
		cbReturnReason = new GERPComboBox("Return Reason");
		cbReturnReason.setItemCaptionPropertyId("lookupname");
		loadReturnReason();
		// Return Qty.GERPTextField
		tfissueqty = new GERPTextField("Balance Qty");
		tfReturnQty = new GERPTextField("Return Qty.");
		tfReturnQty.setValue("0");
		// Indent No text field
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		fltaxCol1 = new FormLayout();
		fltaxCol2 = new FormLayout();
		fltaxCol1.addComponent(cbMatName);
		fltaxCol2.addComponent(cbIndStatus);
		hlSearchLayout.addComponent(fltaxCol1);
		hlSearchLayout.addComponent(fltaxCol2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		fltaxCol1 = new FormLayout();
		fltaxCol2 = new FormLayout();
		fltaxCol3 = new FormLayout();
		flColumn4 = new FormLayout();
		fltaxCol1.addComponent(cbIssueName);
		fltaxCol1.addComponent(cbMatName);
		fltaxCol2.addComponent(tfissueqty);
		fltaxCol2.addComponent(cbReturnReason);
		fltaxCol3.addComponent(tfReturnQty);
		fltaxCol3.addComponent(cbStockType);
		flColumn4.addComponent(cbIndStatus);
		flColumn4.addComponent(taRemarks);
		hlTax = new HorizontalLayout();
		hlTax.addComponent(fltaxCol1);
		hlTax.addComponent(fltaxCol2);
		hlTax.addComponent(fltaxCol3);
		hlTax.addComponent(flColumn4);
		hlTax.setSpacing(true);
		hlTax.setMargin(true);
		hlUserInputLayout.addComponent(hlTax);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadMaterialNameList() {
		List<MaterialDM> materiallist = servicematerial.getMaterialList(null, companyid, null, null, null, null, null,
				null, "Active", "F");
		BeanContainer<Long, MaterialDM> beanCtgry = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
		beanCtgry.setBeanIdProperty("materialId");
		beanCtgry.addAll(materiallist);
		cbMatName.setContainerDataSource(beanCtgry);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<IndentIssueReturnDM> indentIssueRetnList = new ArrayList<IndentIssueReturnDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbReturnReason.getValue() + ", " + cbIndStatus.getValue());
		indentIssueRetnList = serviceIndentHdr.getIndentIssueReturnDMList(null, null, (Long) cbMatName.getValue(),
				null, (String) cbIndStatus.getValue(), "F");
		recordCnt = indentIssueRetnList.size();
		beanIndentIssueReturnDM = new BeanItemContainer<IndentIssueReturnDM>(IndentIssueReturnDM.class);
		beanIndentIssueReturnDM.addAll(indentIssueRetnList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Indent. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanIndentIssueReturnDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "issueRtnId", "materialName", "returnQty", "stockType",
				"status", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Material Name", "Return Qty.", "Stock Type",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("issueRtnId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbIssueName.setValue(null);
		cbStockType.setValue(null);
		taRemarks.setValue("");
		tfissueqty.setValue("");
		cbMatName.setValue(null);
		cbMatName.setComponentError(null);
		// cbMatName.setContainerDataSource(null);
		cbReturnReason.setValue(null);
		tfReturnQty.setValue("0");
		cbIndStatus.setValue(cbIndStatus.getItemIds().iterator().next());
	}
	
	// Method to edit the values from table into fields to update process
	private void editHdrIndentDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			IndentIssueReturnDM editHdrIndent = beanIndentIssueReturnDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			indentId = editHdrIndent.getIssueRtnId();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected Indent Return. Id -> " + indentId);
			Long indentId = editHdrIndent.getIssueeId();
			Collection<?> empColId1 = cbIssueName.getItemIds();
			for (Iterator<?> iteratorclient = empColId1.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbIssueName.getItem(itemIdClient);
				// Get the actual bean and use the data
				IndentIssueHdrDM matObj = (IndentIssueHdrDM) itemclient.getBean();
				if (indentId != null && indentId.equals(matObj.getIssueId())) {
					cbIssueName.setValue(itemIdClient);
				}
			}
			// cbMatName//cbIssueName.setValue(editHdrIndent.getIssueeId());
			Long matId = editHdrIndent.getMaterialId();
			Collection<?> empColId = cbMatName.getItemIds();
			for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMatName.getItem(itemIdClient);
				// Get the actual bean and use the data
				IndentDtlDM matObj = (IndentDtlDM) itemclient.getBean();
				if (matId != null && matId.equals(matObj.getMaterialId())) {
					cbMatName.setValue(itemIdClient);
				}
			}
			cbReturnReason.setValue(editHdrIndent.getReturnResn().toString());
			tfReturnQty.setValue(editHdrIndent.getReturnQty().toString());
			taRemarks.setValue(editHdrIndent.getRemarks());
			cbStockType.setValue(editHdrIndent.getStockType());
			cbIndStatus.setValue(editHdrIndent.getStatus());
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
		}
	}
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMatName.setValue(null);
		cbMatName.setComponentError(null);
		cbIndStatus.setValue(cbIndStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbMatName.setRequired(true);
		cbIssueName.setRequired(true);
		tfReturnQty.setRequired(true);
		cbReturnReason.setRequired(true);
		cbStockType.setRequired(true);
		cbReturnReason.setValue(null);
		tfReturnQty.setValue("0");
		// reset the input controls to default value
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
		cbMatName.setComponentError(null);
		cbIssueName.setComponentError(null);
		tfReturnQty.setComponentError(null);
		cbReturnReason.setComponentError(null);
		cbStockType.setComponentError(null);
		cbMatName.setRequired(false);
		cbReturnReason.setRequired(false);
		cbStockType.setRequired(false);
		cbIssueName.setRequired(false);
		tfReturnQty.setRequired(false);
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbMatName.setRequired(true);
		cbIssueName.setRequired(true);
		tfReturnQty.setRequired(true);
		cbReturnReason.setRequired(true);
		cbStockType.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleInputUserLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editHdrIndentDetails();
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
			cbMatName.setComponentError(null);
			cbIssueName.setComponentError(null);
			tfReturnQty.setComponentError(null);
			cbReturnReason.setComponentError(null);
			cbStockType.setComponentError(null);
			errorFlag = false;
			if (cbMatName.getValue() == null) {
				cbMatName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
				errorFlag = true;
			}
			if (cbIssueName.getValue() == null) {
				cbIssueName.setComponentError(new UserError(GERPErrorCodes.NULL_ISSUE_NAME));
				errorFlag = true;
			}
			if (cbReturnReason.getValue() == null) {
				cbReturnReason.setComponentError(new UserError(GERPErrorCodes.NULL_RETURN_REASON));
				errorFlag = true;
			}
			if (cbStockType.getValue() == null) {
				cbStockType.setComponentError(new UserError(GERPErrorCodes.NULL_STOCK_TYPE));
				errorFlag = true;
			}
			if (tfReturnQty.getValue() == "0") {
				tfReturnQty.setComponentError(new UserError(GERPErrorCodes.NULL_RETURN_QTY));
				errorFlag = true;
			} else {
				tfReturnQty.setComponentError(null);
			}
			Long achievedQty;
			try {
				achievedQty = Long.valueOf(tfReturnQty.getValue());
				if (achievedQty < 0) {
					tfReturnQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
					errorFlag = true;
				}
			}
			catch (Exception e) {
				tfReturnQty.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATION));
				errorFlag = true;
			}
			if (errorFlag) {
				throw new ERPException.ValidationException();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		IndentIssueReturnDM indentObj = new IndentIssueReturnDM();
		if (Long.valueOf(tfissueqty.getValue()) >= Long.valueOf(tfReturnQty.getValue())) {
			if (tblMstScrSrchRslt.getValue() != null) {
				indentObj = beanIndentIssueReturnDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (cbIssueName.getValue() != null) {
				indentObj.setIssueeId(((IndentIssueHdrDM) cbIssueName.getValue()).getIssueId());
				indentObj.setIssueeId(((IndentIssueHdrDM) cbIssueName.getValue()).getIssueId());
			}
			if (cbMatName.getValue() != null) {
				indentObj.setMaterialId(((IndentDtlDM) cbMatName.getValue()).getMaterialId());
				indentObj.setMaterialName(((IndentDtlDM) cbMatName.getValue()).getMaterialName());
			}
			indentObj.setReturnQty(Long.valueOf(tfReturnQty.getValue()));
			indentObj.setReturnResn(Long.valueOf(cbReturnReason.getValue().toString()));
			indentObj.setStockType(cbStockType.getValue().toString());
			indentObj.setRemarks((String) taRemarks.getValue());
			if (cbIndStatus.getValue() != null) {
				indentObj.setStatus((String) cbIndStatus.getValue());
			}
			System.out.println("inddddd" + Long.valueOf(tfReturnQty.getValue()));
			indqty = Long.valueOf(tfReturnQty.getValue()) + ((IndentDtlDM) cbMatName.getValue()).getBalenceQty();
			indentObj.setLastUpdatedDt(DateUtils.getcurrentdate());
			indentObj.setLastUpdatedBy(username);
			serviceIndentHdr.saveorUpdate(indentObj);
			serviceIndentDtlDM.updateissueqty(((IndentDtlDM) cbMatName.getValue()).getIndentHdrId(),
					indentObj.getMaterialId(), indqty);
		} else {
			tfReturnQty.setComponentError(new UserError("Enter Issue qty greater than Indent qty \n Balance Qty="
					+ Long.valueOf(tfissueqty.getValue())));
		}
		IndentIssueReturnDM save = new IndentIssueReturnDM();
		
		
		
		
		try {
			// for material stock
			MaterialStockDM materialStockDM = null;
			try {
				materialStockDM = serviceMaterialStock.getMaterialStockList(save.getMaterialId(), null, null, null,
						null, cbStockType.getValue().toString(), "F").get(0);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (materialStockDM == null) {
				materialStockDM = new MaterialStockDM();
				materialStockDM.setCompanyId(companyid);
				materialStockDM.setBranchId(branchId);
				materialStockDM.setMaterialId(save.getMaterialId());
				materialStockDM.setLotNo("----");
				materialStockDM.setStockType(cbStockType.getValue().toString());
				materialStockDM.setCurrentStock(Long.valueOf(tfReturnQty.getValue()));
				materialStockDM.setParkedStock(0L);
				materialStockDM.setEffectiveStock(Long.valueOf(tfReturnQty.getValue()));
				materialStockDM.setLastUpdatedby(username);
				materialStockDM.setLastUpdateddt(DateUtils.getcurrentdate());
				serviceMaterialStock.saveorupdatematerialstock(materialStockDM);
			} else {
				materialStockDM.setCurrentStock(materialStockDM.getCurrentStock()
						+ Long.valueOf(tfReturnQty.getValue()));
				materialStockDM.setEffectiveStock(materialStockDM.getEffectiveStock()
						+ Long.valueOf(tfReturnQty.getValue()));
				materialStockDM.setLastUpdatedby(username);
				materialStockDM.setLastUpdateddt(DateUtils.getcurrentdate());
				serviceMaterialStock.saveorupdatematerialstock(materialStockDM);
			}
		  }
		catch (Exception e) {
			e.printStackTrace();
		}
		try{
			MaterialLedgerDM materialledgerDM =null; 
			try {
				
				materialledgerDM = serviceledger.getMaterialLedgerList(save.getMaterialId(), null, null, null,
						cbStockType.getValue().toString(), null, "Y", "F").get(0);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (materialledgerDM == null) {
				MaterialLedgerDM ledgerDM = new MaterialLedgerDM();
				ledgerDM.setStockledgeDate(new Date());
				ledgerDM.setCompanyId(companyid);
				ledgerDM.setBranchId(branchId);
				ledgerDM.setMaterialId(save.getMaterialId());
				ledgerDM.setStockType("New");
				ledgerDM.setOpenQty(0L);
				ledgerDM.setInoutFlag("I");
				ledgerDM.setInoutFQty(save.getReturnQty());
				ledgerDM.setCloseQty(save.getReturnQty());
				ledgerDM.setReferenceNo("00");
				ledgerDM.setReferenceDate(DateUtils.getcurrentdate());
				ledgerDM.setIsLatest("Y");
				ledgerDM.setReferenceRemark(save.getRemarks());
				ledgerDM.setLastUpdatedby(username);
				ledgerDM.setLastUpdateddt(DateUtils.getcurrentdate());
				serviceledger.saveOrUpdateLedger(ledgerDM);
			} else {
				MaterialLedgerDM ledgerDM = new MaterialLedgerDM();
				ledgerDM.setStockledgeDate(new Date());
				ledgerDM.setCompanyId(companyid);
				ledgerDM.setBranchId(branchId);
				ledgerDM.setMaterialId(save.getMaterialId());
				ledgerDM.setStockType("New");
				ledgerDM.setOpenQty(materialledgerDM.getCloseQty());
				ledgerDM.setInoutFlag("I");
				ledgerDM.setInoutFQty(save.getReturnQty());
				ledgerDM.setCloseQty(materialledgerDM.getCloseQty() + save.getReturnQty());
				ledgerDM.setReferenceNo("00");
				ledgerDM.setReferenceDate(DateUtils.getcurrentdate());
				ledgerDM.setIsLatest("Y");
				ledgerDM.setReferenceRemark(save.getRemarks());
				ledgerDM.setLastUpdatedby(username);
				ledgerDM.setLastUpdateddt(DateUtils.getcurrentdate());
				serviceledger.saveOrUpdateLedger(ledgerDM);
			}
		}
		
		catch (Exception e) {
		}
		resetFields();
		loadSrchRslt();
	}
	
	/*
	 * loadMaterialList()-->this function is used for load the Material name
	 */
	private void loadMaterialList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
		List<IndentDtlDM> indentIssueList = serviceIndentDtlDM.getIndentDtlDMList(null,
				((IndentIssueHdrDM) cbIssueName.getValue()).getIndentId(), null, "Active", "F");
		BeanItemContainer<IndentDtlDM> beanIndentDtl = new BeanItemContainer<IndentDtlDM>(IndentDtlDM.class);
		beanIndentDtl.addAll(indentIssueList);
		cbMatName.setContainerDataSource(beanIndentDtl);
	}
	
	//
	/*
	 * loadModeOfTransList()-->this function is used for load the ModeOfTransaction name
	 */
	private void loadReturnReason() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
				"MM_ISURTN");
		beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("cmplookupid");
		beanCompanyLookUp.addAll(lookUpList);
		cbReturnReason.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadMaterialList()-->this function is used for load the Material name
	 */
	private void loadIssueList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
		List<IndentIssueHdrDM> indentIssueList = serviceIndentIssueHdr.getIndentIssueHdrList(null, companyid, null,
				null, null, null, "F");
		BeanItemContainer<IndentIssueHdrDM> beanIndentDtl = new BeanItemContainer<IndentIssueHdrDM>(
				IndentIssueHdrDM.class);
		beanIndentDtl.addAll(indentIssueList);
		cbIssueName.setContainerDataSource(beanIndentDtl);
	}
}
