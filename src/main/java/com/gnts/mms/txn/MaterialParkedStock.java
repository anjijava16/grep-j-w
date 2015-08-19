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
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.MaterialParkedStockDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.MaterialParkedStockService;
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
	private PopupDateField dtparkdate, dfRefDate;
	private ComboBox cbMaterial, cbBranch, cbStocktype;
	private TextField tfparkedQty, tfusedQty, tfbalanceQty, tfLotNO, tfrefNo;
	private TextArea taRefRemarks;
	private HorizontalLayout hlsearch;
	private BeanItemContainer<MaterialParkedStockDM> beanmaterialparkedstock;
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4;
	private Logger logger = Logger.getLogger(MaterialParkedStock.class);
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private String userName;
	private Long companyId, branchId;
	private Date parkedDate;
	private int recordCnt = 0;
	
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
	private void buildview() {
		dtparkdate = new GERPPopupDateField("Parked Date");
		dtparkdate.setWidth("150");
		cbMaterial = new GERPComboBox("Material");
		cbMaterial.setItemCaptionPropertyId("materialName");
		cbMaterial.setWidth("150");
		loadmateriallist();
		cbStocktype = new GERPComboBox("Stock Type", BASEConstants.T_MMS_MATERIAL_STOCK, BASEConstants.STOCKTYPE);
		cbStocktype.setWidth("150");
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("150");
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
		dfRefDate = new GERPPopupDateField("Reference Date");
		dfRefDate.setWidth("150");
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
		flcolumn1.addComponent(cbBranch);
		flcolumn2.addComponent(cbMaterial);
		flcolumn3.addComponent(dtparkdate);
		flcolumn3.setMargin(true);
		flcolumn4.addComponent(cbStocktype);
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
		flcolumn1.addComponent(cbBranch);
		flcolumn1.addComponent(cbMaterial);
		flcolumn1.addComponent(cbStocktype);
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
	
	private void readonlytrue() {
		dtparkdate.setReadOnly(true);
		cbMaterial.setReadOnly(true);
		cbStocktype.setReadOnly(true);
		cbBranch.setReadOnly(true);
		tfparkedQty.setReadOnly(true);
		tfusedQty.setReadOnly(true);
		tfbalanceQty.setReadOnly(true);
		tfLotNO.setReadOnly(true);
		tfrefNo.setReadOnly(true);
		dfRefDate.setReadOnly(true);
		taRefRemarks.setReadOnly(true);
	}
	
	private void readonlyfalse() {
		dtparkdate.setReadOnly(false);
		cbMaterial.setReadOnly(false);
		cbStocktype.setReadOnly(false);
		cbBranch.setReadOnly(false);
		tfparkedQty.setReadOnly(false);
		tfusedQty.setReadOnly(false);
		tfbalanceQty.setReadOnly(false);
		tfLotNO.setReadOnly(false);
		tfrefNo.setReadOnly(false);
		dfRefDate.setReadOnly(false);
		taRefRemarks.setReadOnly(false);
	}
	
	private void viewLogger() {
		if (tblMstScrSrchRslt.getValue() != null) {
			MaterialParkedStockDM parkedStockDM = beanmaterialparkedstock.getItem(tblMstScrSrchRslt.getValue()).getBean();
			readonlyfalse();
			cbMaterial.setValue(parkedStockDM.getMaterialId());
			cbBranch.setValue(parkedStockDM.getBranchId());
			dtparkdate.setValue(parkedStockDM.getParkedDate());
			cbStocktype.setValue(parkedStockDM.getStockType().toString());
			tfparkedQty.setValue(parkedStockDM.getParkedQty().toString());
			tfusedQty.setValue(parkedStockDM.getUsedQty().toString());
			tfbalanceQty.setValue(parkedStockDM.getBalanceQty().toString());
			tfLotNO.setValue(parkedStockDM.getLotNo());
			tfrefNo.setValue(parkedStockDM.getReferenceNo());
			dfRefDate.setValue(parkedStockDM.getReferenceDate());
			taRefRemarks.setValue(parkedStockDM.getReferenceRemark());
			readonlytrue();
		}
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			parkedDate = dtparkdate.getValue();
			List<MaterialParkedStockDM> materiallist = serviceparked.getMaterialParkedStockList(
					(Long) cbMaterial.getValue(), null, (String) cbStocktype.getValue(), parkedDate,
					(Long) cbBranch.getValue(), "F");
			recordCnt = materiallist.size();
			beanmaterialparkedstock = new BeanItemContainer<MaterialParkedStockDM>(MaterialParkedStockDM.class);
			beanmaterialparkedstock.addAll(materiallist);
			tblMstScrSrchRslt.setContainerDataSource(beanmaterialparkedstock);
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "parkStockId", "branchName", "materialName",
					"parkedDate", "stockType", "lastUpdateddt", "lastUpdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch", "Material", "Parked Date",
					"Stock Type", "Updated Date", "Updated By" });
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of.Records :" + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("loadSrchRslt-->" + e);
		}
	}
	
	// Load Product List
	private void loadmateriallist() {
		try {
			List<MaterialDM> list = new ArrayList<MaterialDM>();
			list.add(new MaterialDM(0L, "All Materials"));
			list.addAll(servicematerial.getMaterialList(null, companyId, null, null, null, null, null, null, null, "P"));
			BeanContainer<Long, MaterialDM> beanmaterial = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
			beanmaterial.setBeanIdProperty("materialId");
			beanmaterial.addAll(list);
			cbMaterial.setContainerDataSource(beanmaterial);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadbranchlist() {
		BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanbranch.setBeanIdProperty("branchId");
		beanbranch.addAll(servicebranch.getBranchList(null, null, null, null, companyId, "P"));
		cbBranch.setContainerDataSource(beanbranch);
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
		cbMaterial.setValue(0L);
		cbBranch.setValue(branchId);
		cbStocktype.setValue(null);
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
		cbMaterial.setValue(0L);
		cbBranch.setValue(branchId);
		cbStocktype.setValue(null);
		dtparkdate.setValue(null);
		tfparkedQty.setValue("");
		tfusedQty.setValue("");
		tfbalanceQty.setValue("");
		tfLotNO.setValue("");
		tfrefNo.setValue("");
		dfRefDate.setValue(null);
		taRefRemarks.setValue("");
	}
}
