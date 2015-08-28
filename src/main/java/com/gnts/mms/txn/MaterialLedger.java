/**
 * File Name 		: MaterialLedger.java 
 * Description 		: this class is used for View MaterialLedger  details. 
 * Author 			: Karthikeyan R
 * Date 			: OCT 31 , 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1         OCT 31 , 2014    Karthikeyan R	        Initial Version
 **/
package com.gnts.mms.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.MaterialLedgerDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.MaterialLedgerService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class MaterialLedger extends BaseUI {
	private MaterialLedgerService serviceLedger = (MaterialLedgerService) SpringContextHelper.getBean("materialledger");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private static final long serialVersionUID = 1L;
	private HorizontalLayout hlsearch;
	private PopupDateField dfLedgerDate;
	private ComboBox cbMaterial, cbBranch, cbstocktype;
	private TextField tfopenqty, tfInoutflag, tfinoutqty, tfcloseqty, tfrefNo, tfIslatest, tfRemarks;
	private PopupDateField dfRefdate;
	// Bean Container
	private BeanItemContainer<MaterialLedgerDM> beanmatrlledger = null;
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4;
	// form layout for input controls
	private FormLayout fl1, fl2, fl3, fl4;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	// local variables declaration
	private Long companyId, branchId;
	private String userName;
	private int recordCnt = 0;
	// Initialize logger
	private Logger logger = Logger.getLogger(MaterialLedger.class);
	private Date stockledgeDate;
	
	// Constructor
	public MaterialLedger() {
		// Get the logged in user name and company id from the session
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside MaterialLedger() constructor");
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		dfLedgerDate = new GERPPopupDateField("Stock ledger Date");
		dfLedgerDate.setWidth("130");
		cbMaterial = new GERPComboBox("Material");
		cbMaterial.setItemCaptionPropertyId("materialName");
		cbMaterial.setWidth("150");
		loadMateriallist();
		cbstocktype = new GERPComboBox("Stock Type", BASEConstants.T_MMS_MATERIAL_STOCK, BASEConstants.STOCKTYPE);
		cbstocktype.setWidth("150");
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("150");
		loadbranchlist();
		tfopenqty = new GERPTextField("Open Quantity");
		tfopenqty.setWidth("150");
		tfInoutflag = new GERPTextField("Inout Flag");
		tfInoutflag.setWidth("150");
		tfinoutqty = new GERPTextField("Inout Quantity");
		tfinoutqty.setWidth("150");
		tfcloseqty = new GERPTextField("Close Quantity");
		tfcloseqty.setWidth("150");
		tfrefNo = new GERPTextField("Reference No");
		tfrefNo.setWidth("150");
		tfIslatest = new GERPTextField("Latest");
		tfIslatest.setWidth("150");
		tfRemarks = new GERPTextField("Reference Remarks");
		tfRemarks.setWidth("150");
		dfRefdate = new GERPPopupDateField("Reference Date");
		dfRefdate.setWidth("130");
		hlsearch = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearch));
		assembleSearchlayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchlayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlsearch.removeAllComponents();
		btnAdd.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnEdit.setCaption("View");
		btnEdit.setStyleName("view");
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn4 = new FormLayout();
		flcolumn1.addComponent(cbBranch);
		flcolumn2.addComponent(cbMaterial);
		flcolumn3.addComponent(dfLedgerDate);
		flcolumn3.setMargin(true);
		flcolumn4.addComponent(cbstocktype);
		hlsearch.addComponent(flcolumn1);
		hlsearch.addComponent(flcolumn2);
		hlsearch.addComponent(flcolumn3);
		hlsearch.addComponent(flcolumn4);
		hlsearch.setSpacing(true);
		hlsearch.setMargin(true);
		hlsearch.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Assembling User search layout");
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		btnSave.setVisible(false);
		// Removing components from search layout and re-initializing form layouts
		hlsearch.removeAllComponents();
		fl1 = new FormLayout();
		fl2 = new FormLayout();
		fl3 = new FormLayout();
		fl4 = new FormLayout();
		fl1.addComponent(cbBranch);
		fl1.addComponent(cbMaterial);
		fl1.addComponent(cbstocktype);
		fl2.addComponent(dfLedgerDate);
		fl2.addComponent(tfopenqty);
		fl2.addComponent(tfInoutflag);
		fl3.addComponent(tfinoutqty);
		fl3.addComponent(tfcloseqty);
		fl3.addComponent(tfrefNo);
		fl4.addComponent(dfRefdate);
		fl4.addComponent(tfIslatest);
		fl4.addComponent(tfRemarks);
		hlUserInputLayout.addComponent(fl1);
		hlUserInputLayout.addComponent(fl2);
		hlUserInputLayout.addComponent(fl3);
		hlUserInputLayout.addComponent(fl4);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			stockledgeDate = dfLedgerDate.getValue();
			List<MaterialLedgerDM> materiallist = serviceLedger.getMaterialLedgerList((Long) cbMaterial.getValue(),
					null, stockledgeDate, (Long) cbBranch.getValue(), (String) cbstocktype.getValue(), null, null, "F");
			recordCnt = materiallist.size();
			beanmatrlledger = new BeanItemContainer<MaterialLedgerDM>(MaterialLedgerDM.class);
			beanmatrlledger.addAll(materiallist);
			tblMstScrSrchRslt.setContainerDataSource(beanmatrlledger);
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "stockledgeId", "branchName", "materialName",
					"stockledgeDate", "stockType", "materialUOM", "lastUpdateddt", "lastUpdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch", "Material", "Stock ledger Date",
					"Stock Type", "UOM", "Updated Date", "Updated By" });
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of.Records :" + recordCnt);
		}
		catch (Exception e) {
			logger.info("loadSrchRslt-->" + e);
		}
	}
	
	// Loading Material List
	private void loadMateriallist() {
		try {
			List<MaterialDM> list = new ArrayList<MaterialDM>();
			list.add(new MaterialDM(0L, "All Materials"));
			list.addAll(serviceMaterial.getMaterialList(null, companyId, null, null, null, null, null, null, "Active",
					"F"));
			BeanContainer<Long, MaterialDM> beanmaterial = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
			beanmaterial.setBeanIdProperty("materialId");
			beanmaterial.addAll(list);
			cbMaterial.setContainerDataSource(beanmaterial);
		}
		catch (Exception e) {
			logger.info("loadSrchRslt-->" + e);
		}
	}
	
	private void setReadOnlyFalseFields() {
		cbMaterial.setReadOnly(false);
		cbBranch.setReadOnly(false);
		cbstocktype.setReadOnly(false);
		tfopenqty.setReadOnly(false);
		tfInoutflag.setReadOnly(false);
		tfinoutqty.setReadOnly(false);
		tfcloseqty.setReadOnly(false);
		tfrefNo.setReadOnly(false);
		tfIslatest.setReadOnly(false);
		tfRemarks.setReadOnly(false);
		dfRefdate.setReadOnly(false);
		dfLedgerDate.setReadOnly(false);
	}
	
	private void setReadOnlyTrueFields() {
		cbMaterial.setReadOnly(true);
		cbBranch.setReadOnly(true);
		cbstocktype.setReadOnly(true);
		tfopenqty.setReadOnly(true);
		tfInoutflag.setReadOnly(true);
		tfinoutqty.setReadOnly(true);
		tfcloseqty.setReadOnly(true);
		tfrefNo.setReadOnly(true);
		tfIslatest.setReadOnly(true);
		tfRemarks.setReadOnly(true);
		dfRefdate.setReadOnly(true);
		dfLedgerDate.setReadOnly(true);
	}
	
	// Loading Branch List
	private void loadbranchlist() {
		try {
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyId, "P"));
			cbBranch.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			logger.info("loadSrchRslt-->" + e);
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		try {
			loadSrchRslt();
			if (recordCnt == 0) {
				logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
						+ "No data for the search. throwing ERPException.NoDataFoundException");
				throw new ERPException.NoDataFoundException();
			} else {
				lblNotification.setIcon(null);
				lblNotification.setCaption("");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbBranch.setValue(branchId);
		cbMaterial.setValue(0L);
		dfLedgerDate.setValue(null);
		cbstocktype.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		loadSrchRslt();
	}
	
	// No functionality Implementation
	@Override
	protected void addDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		tblMstScrSrchRslt.setPageLength(14);
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editMaterialLedger();
	}
	
	private void editMaterialLedger() {
		try {
			if (tblMstScrSrchRslt.getValue() != null) {
				MaterialLedgerDM ledgerDM = beanmatrlledger.getItem(tblMstScrSrchRslt.getValue()).getBean();
				setReadOnlyFalseFields();
				cbBranch.setValue(ledgerDM.getBranchId());
				cbMaterial.setValue(ledgerDM.getMaterialId());
				cbstocktype.setValue(ledgerDM.getStockType());
				tfopenqty.setValue(ledgerDM.getOpenQty().toString());
				tfInoutflag.setValue(ledgerDM.getInoutFlag());
				tfinoutqty.setValue(ledgerDM.getInoutFQty().toString());
				tfcloseqty.setValue(ledgerDM.getCloseQty().toString());
				tfrefNo.setValue(ledgerDM.getReferenceNo());
				dfLedgerDate.setValue(ledgerDM.getStockledgeDate1());
				dfRefdate.setValue(ledgerDM.getReferenceDate());
				tfIslatest.setValue(ledgerDM.getIsLatest());
				tfRemarks.setValue(ledgerDM.getReferenceRemark());
				setReadOnlyTrueFields();
			}
		}
		catch (Exception e) {
			logger.info("editMaterialLedger-->" + e);
		}
	}
	
	// No functionality Implementation
	@Override
	protected void validateDetails() throws ValidationException {
		// TODO Auto-generated method stub
	}
	
	// No functionality Implementation
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
	}
	
	// No functionality Implementation
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	// No functionality Implementation
	@Override
	protected void cancelDetails() {
		hlsearch.setVisible(true);
		assembleSearchlayout();
		resetFields();
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		setReadOnlyFalseFields();
		cbMaterial.setValue(0L);
		cbBranch.setValue(branchId);
		cbstocktype.setValue(null);
		dfLedgerDate.setValue(null);
		tfopenqty.setValue("");
		tfInoutflag.setValue("");
		tfinoutqty.setValue("");
		tfcloseqty.setValue("");
		tfrefNo.setValue("");
		dfRefdate.setValue(null);
		tfIslatest.setValue("");
		tfRemarks.setValue("");
	}
}
