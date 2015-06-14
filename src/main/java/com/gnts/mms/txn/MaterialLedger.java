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
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class MaterialLedger extends BaseUI {
	private MaterialLedgerService serviceledger = (MaterialLedgerService) SpringContextHelper.getBean("materialledger");
	private MaterialService servicematerial = (MaterialService) SpringContextHelper.getBean("material");
	private BranchService servicebranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private static final long serialVersionUID = 1L;
	private HorizontalLayout hlsearch;
	private PopupDateField dtstockletdate;
	private ComboBox cbmaterial, cbbranch, cbstocktype;
	private TextField tfopenqty, tfInoutflag, tfinoutqty, tfcloseqty, tfrefNo, tfislatest, tfremarks;
	private PopupDateField dfrefdate;
	// Bean Container
	private BeanItemContainer<MaterialLedgerDM> beanmatrlledger = null;
	private BeanContainer<Long, MaterialDM> beanmaterial;
	private BeanContainer<Long, BranchDM> beanbranch;
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4;
	// form layout for input controls
	private FormLayout fl1, fl2, fl3, fl4;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	// local variables declaration
	private Long companyId, stockledgeId, branchId;
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
	public void buildview() {
		dtstockletdate = new GERPPopupDateField("Stock ledger Date");
		dtstockletdate.setWidth("130");
		cbmaterial = new GERPComboBox("Material");
		cbmaterial.setItemCaptionPropertyId("materialName");
		cbmaterial.setWidth("150");
		loadmateriallist();
		cbstocktype = new GERPComboBox("Stock Type", BASEConstants.T_MMS_MATERIAL_STOCK, BASEConstants.STOCKTYPE);
		cbstocktype.setWidth("150");
		cbbranch = new GERPComboBox("Branch");
		cbbranch.setItemCaptionPropertyId("branchName");
		cbbranch.setWidth("150");
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
		tfislatest = new GERPTextField("Latest");
		tfislatest.setWidth("150");
		tfremarks = new GERPTextField("Reference Remarks");
		tfremarks.setWidth("150");
		dfrefdate = new GERPPopupDateField("Reference Date");
		dfrefdate.setWidth("130");
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
		flcolumn1.addComponent(cbbranch);
		flcolumn2.addComponent(cbmaterial);
		flcolumn3.addComponent(dtstockletdate);
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
		fl1.addComponent(cbbranch);
		fl1.addComponent(cbmaterial);
		fl1.addComponent(cbstocktype);
		fl2.addComponent(dtstockletdate);
		fl2.addComponent(tfopenqty);
		fl2.addComponent(tfInoutflag);
		fl3.addComponent(tfinoutqty);
		fl3.addComponent(tfcloseqty);
		fl3.addComponent(tfrefNo);
		fl4.addComponent(dfrefdate);
		fl4.addComponent(tfislatest);
		fl4.addComponent(tfremarks);
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
			stockledgeDate = dtstockletdate.getValue();
			List<MaterialLedgerDM> materiallist = serviceledger.getMaterialLedgerList((Long) cbmaterial.getValue(),
					null, stockledgeDate, (Long) cbbranch.getValue(), (String) cbstocktype.getValue(), "F");
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
			e.printStackTrace();
			logger.info("loadSrchRslt-->" + e);
		}
	}
	
	// Loading Material List
	public void loadmateriallist() {
		try {
			List<MaterialDM> list = new ArrayList<MaterialDM>();
			list.add(new MaterialDM(0L, "All Materials"));
			list.addAll(servicematerial.getMaterialList(null, companyId, null, null, null, null, null, null, "Active",
					"F"));
			BeanContainer<Long, MaterialDM> beanmaterial = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
			beanmaterial.setBeanIdProperty("materialId");
			beanmaterial.addAll(list);
			cbmaterial.setContainerDataSource(beanmaterial);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setReadOnlyFalseFields() {
		cbmaterial.setReadOnly(false);
		cbbranch.setReadOnly(false);
		cbstocktype.setReadOnly(false);
		tfopenqty.setReadOnly(false);
		tfInoutflag.setReadOnly(false);
		tfinoutqty.setReadOnly(false);
		tfcloseqty.setReadOnly(false);
		tfrefNo.setReadOnly(false);
		tfislatest.setReadOnly(false);
		tfremarks.setReadOnly(false);
		dfrefdate.setReadOnly(false);
		dtstockletdate.setReadOnly(false);
	}
	
	public void setReadOnlyTrueFields() {
		cbmaterial.setReadOnly(true);
		cbbranch.setReadOnly(true);
		cbstocktype.setReadOnly(true);
		tfopenqty.setReadOnly(true);
		tfInoutflag.setReadOnly(true);
		tfinoutqty.setReadOnly(true);
		tfcloseqty.setReadOnly(true);
		tfrefNo.setReadOnly(true);
		tfislatest.setReadOnly(true);
		tfremarks.setReadOnly(true);
		dfrefdate.setReadOnly(true);
		dtstockletdate.setReadOnly(true);
	}
	
	// Loading Branch List
	public void loadbranchlist() {
		List<BranchDM> branchlist = servicebranch.getBranchList(null, null, null, "Active", companyId, "P");
		beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanbranch.setBeanIdProperty("branchId");
		beanbranch.addAll(branchlist);
		cbbranch.setContainerDataSource(beanbranch);
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
		cbbranch.setValue(branchId);
		cbmaterial.setValue(0L);
		dtstockletdate.setValue(null);
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
		editmaterialLedger();
	}
	
	private void editmaterialLedger() {
		Item iteselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (iteselect != null) {
			MaterialLedgerDM editledgerlist = beanmatrlledger.getItem(tblMstScrSrchRslt.getValue()).getBean();
			setReadOnlyFalseFields();
			cbbranch.setValue(editledgerlist.getBranchId());
			cbmaterial.setValue(editledgerlist.getMaterialId());
			cbstocktype.setValue(editledgerlist.getStockType());
			tfopenqty.setValue(iteselect.getItemProperty("openQty").getValue().toString());
			tfInoutflag.setValue(iteselect.getItemProperty("inoutFlag").getValue().toString());
			tfinoutqty.setValue(iteselect.getItemProperty("inoutFQty").getValue().toString());
			tfcloseqty.setValue(iteselect.getItemProperty("closeQty").getValue().toString());
			tfrefNo.setValue(iteselect.getItemProperty("referenceNo").getValue().toString());
			dtstockletdate.setValue((Date) iteselect.getItemProperty("stockledgeDate").getValue());
			dfrefdate.setValue((Date) iteselect.getItemProperty("referenceDate").getValue());
			tfislatest.setValue(iteselect.getItemProperty("isLatest").getValue().toString());
			tfremarks.setValue(iteselect.getItemProperty("referenceRemark").getValue().toString());
			setReadOnlyTrueFields();
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
		cbmaterial.setValue(0L);
		cbbranch.setValue(branchId);
		cbstocktype.setValue(null);
		dtstockletdate.setValue(null);
		tfopenqty.setValue("");
		tfInoutflag.setValue("");
		tfinoutqty.setValue("");
		tfcloseqty.setValue("");
		tfrefNo.setValue("");
		dfrefdate.setValue(null);
		tfislatest.setValue("");
		tfremarks.setValue("");
	}
}
