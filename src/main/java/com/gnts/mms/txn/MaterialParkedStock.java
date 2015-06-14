/**
 * File Name 		: MaterialParkedStock.java 
 * Description 		: this class is used for add/edit MaterialStock  details. 
 * Author 			: Karthikeyan R
 * Date 			: OCT 30 , 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1         OCT 30 , 2014    Karthikeyan R	        Initial Version
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
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.MaterialParkedStockDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.MaterialParkedStockService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class MaterialParkedStock extends BaseUI {
	private MaterialParkedStockService serviceparked = (MaterialParkedStockService) SpringContextHelper
			.getBean("materialparkedstock");
	private MaterialService servicematerial = (MaterialService) SpringContextHelper.getBean("material");
	private BranchService servicebranch = (BranchService) SpringContextHelper.getBean("mbranch");
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PopupDateField dtparkdate, dfrefDate;
	private ComboBox cbmaterial, cbbranch, cbstocktype;
	private TextField tfparkedQty, tfusedQty, tfbalanceQty, tfLotNO, tfrefNo;
	private TextArea taRefRemarks;
	private HorizontalLayout hlsearch;
	private BeanItemContainer<MaterialParkedStockDM> beanmaterialparkedstock;
	private BeanContainer<Long, MaterialDM> beanmaterial;
	private BeanContainer<Long, BranchDM> beanbranch;
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4;
	private Logger logger = Logger.getLogger(MaterialParkedStock.class);
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private String userName;
	private Long companyId, parkStockId, branchId;
	private Date parkedDate;
	int recordCnt = 0;
	
	// Constructor
	public MaterialParkedStock() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside ClientContact() constructor");
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	public void buildview() {
		dtparkdate = new GERPPopupDateField("Parked Date");
		dtparkdate.setWidth("150");
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
		tfparkedQty = new TextField("Parked Qty");
		tfparkedQty.setWidth("150");
		tfusedQty = new TextField("Used Qty");
		tfusedQty.setWidth("150");
		tfbalanceQty = new TextField("Balance Qty");
		tfbalanceQty.setWidth("150");
		tfLotNO = new TextField("LOT No");
		tfLotNO.setWidth("150");
		tfrefNo = new TextField("Reference No");
		tfrefNo.setWidth("150");
		dfrefDate = new GERPPopupDateField("Reference Date");
		dfrefDate.setWidth("150");
		taRefRemarks = new TextArea("Refernce Remarks");
		taRefRemarks.setWidth("150");
		taRefRemarks.setHeight("70");
		// build search layout
		hlsearch = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearch));
		assembleSearchlayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchlayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
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
		flcolumn3.addComponent(dtparkdate);
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
	
	private void assembleuserInputlayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling User Input layout");
		btnSave.setVisible(false);
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setMargin(true);
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn4 = new FormLayout();
		flcolumn1.addComponent(cbbranch);
		flcolumn1.addComponent(cbmaterial);
		flcolumn1.addComponent(cbstocktype);
		flcolumn2.addComponent(dtparkdate);
		flcolumn2.addComponent(tfparkedQty);
		flcolumn2.addComponent(tfusedQty);
		flcolumn3.addComponent(tfbalanceQty);
		flcolumn3.addComponent(tfLotNO);
		flcolumn3.addComponent(tfrefNo);
		flcolumn4.addComponent(taRefRemarks);
		hlUserInputLayout.addComponent(flcolumn1);
		hlUserInputLayout.addComponent(flcolumn2);
		hlUserInputLayout.addComponent(flcolumn3);
		hlUserInputLayout.addComponent(flcolumn4);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	/*
	 * private void editMaterialStock() { logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
	 * + "Editing the selected record"); Item slectedrecd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
	 * logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Selected Dept. Id -> " +
	 * parkStockId); if (slectedrecd != null) { MaterialParkedStockDM editmaterialstock =
	 * beanmaterialparkedstock.getItem(tblMstScrSrchRslt.getValue()) .getBean(); parkStockId =
	 * editmaterialstock.getParkStockId(); cbbranch.setValue(editmaterialstock.getBranchId());
	 * cbmaterial.setValue(editmaterialstock.getMaterialId()); cbstocktype.setValue(editmaterialstock.getStockType());
	 * editmaterialstock.setParkedDate(dtparkdate.getValue()); } readonlytrue(); }
	 */
	public void readonlytrue() {
		dtparkdate.setReadOnly(true);
		cbmaterial.setReadOnly(true);
		cbstocktype.setReadOnly(true);
		cbbranch.setReadOnly(true);
		tfparkedQty.setReadOnly(true);
		tfusedQty.setReadOnly(true);
		tfbalanceQty.setReadOnly(true);
		tfLotNO.setReadOnly(true);
		tfrefNo.setReadOnly(true);
		dfrefDate.setReadOnly(true);
		taRefRemarks.setReadOnly(true);
	}
	
	public void readonlyfalse() {
		dtparkdate.setReadOnly(false);
		cbmaterial.setReadOnly(false);
		cbstocktype.setReadOnly(false);
		cbbranch.setReadOnly(false);
		tfparkedQty.setReadOnly(false);
		tfusedQty.setReadOnly(false);
		tfbalanceQty.setReadOnly(false);
		tfLotNO.setReadOnly(false);
		tfrefNo.setReadOnly(false);
		dfrefDate.setReadOnly(false);
		taRefRemarks.setReadOnly(false);
	}
	
	private void viewLogger() {
		Item rowSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (rowSelected != null) {
			MaterialParkedStockDM view = beanmaterialparkedstock.getItem(tblMstScrSrchRslt.getValue()).getBean();
			readonlyfalse();
			cbmaterial.setValue(view.getMaterialId());
			cbbranch.setValue(view.getBranchId());
			dtparkdate.setValue(view.getParkedDate());
			cbstocktype.setValue(view.getStockType().toString());
			tfparkedQty.setValue(view.getParkedQty().toString());
			tfusedQty.setValue(view.getUsedQty().toString());
			tfbalanceQty.setValue(view.getBalanceQty().toString());
			tfLotNO.setValue(view.getLotNo());
			tfrefNo.setValue(view.getReferenceNo());
			dfrefDate.setValue(view.getReferenceDate());
			taRefRemarks.setValue(view.getReferenceRemark());
			readonlytrue();
		}
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			parkedDate = dtparkdate.getValue();
			List<MaterialParkedStockDM> materiallist = serviceparked.getMaterialParkedStockList(
					(Long) cbmaterial.getValue(), null, (String) cbstocktype.getValue(), parkedDate,
					(Long) cbbranch.getValue(), "F");
			recordCnt = materiallist.size();
			beanmaterialparkedstock = new BeanItemContainer<MaterialParkedStockDM>(MaterialParkedStockDM.class);
			beanmaterialparkedstock.addAll(materiallist);
			tblMstScrSrchRslt.setContainerDataSource(beanmaterialparkedstock);
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "parkStockId","branchName","materialName","parkedDate", 
					 "stockType", "lastUpdateddt", "lastUpdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id","Branch", "Material", "Parked Date", 
					"Stock Type","Updated Date", "Updated By" });
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of.Records :" + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("loadSrchRslt-->" + e);
		}
	}
	
	// Load Product List
	public void loadmateriallist() {
		try {
			List<MaterialDM> list = new ArrayList<MaterialDM>();
			list.add(new MaterialDM(0L, "All Materials"));
			list.addAll(servicematerial.getMaterialList(null, companyId, null, null, null, null, null, null, null, "P"));
			BeanContainer<Long, MaterialDM> beanmaterial = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
			beanmaterial.setBeanIdProperty("materialId");
			beanmaterial.addAll(list);
			cbmaterial.setContainerDataSource(beanmaterial);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadbranchlist() {
		List<BranchDM> branchlist = servicebranch.getBranchList(null, null, null, null, companyId, "P");
		beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanbranch.setBeanIdProperty("branchId");
		beanbranch.addAll(branchlist);
		cbbranch.setContainerDataSource(beanbranch);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		hlUserInputLayout.removeAllComponents();
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
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbmaterial.setValue(0L);
		cbbranch.setValue(branchId);
		cbstocktype.setValue(null);
		dtparkdate.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlsearch.removeAllComponents();
		assembleuserInputlayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		// reset the input controls to default value
		resetFields();
		assembleuserInputlayout();
		viewLogger();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void showAuditDetails() {
		/*
		 * logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " +
		 * "Getting audit record for client cat. ID " + parkStockId);
		 * UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MMS_MATERIAL_PARKED_STOCK);
		 * UI.getCurrent().getSession().setAttribute("audittablepk", parkStockId);
		 */
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchlayout();
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Resetting the UI controls");
		readonlyfalse();
		cbmaterial.setValue(0L);
		cbbranch.setValue(branchId);
		cbstocktype.setValue(null);
		dtparkdate.setValue(null);
		tfparkedQty.setValue("");
		tfusedQty.setValue("");
		tfbalanceQty.setValue("");
		tfLotNO.setValue("");
		tfrefNo.setValue("");
		dfrefDate.setValue(null);
		taRefRemarks.setValue("");
	}
}
