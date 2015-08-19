/**


 * File Name 		: MaterialStock.java 
 * Description 		: this class is used for View MaterialStock  details. 
 * Author 			: Karthikeyan R
 * Date 			: OCT 17 , 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1         OCT 17 , 2014    Karthikeyan R	        Initial Version
 **/
package com.gnts.mms.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.MaterialStockService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class MaterialStock extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextField tfLotno, tfCurrentStock, tfParkedStock, tfEffectiveStock;
	private ComboBox cbBranch, cbMaterial, cbStockType;
	private FormLayout f1column, f2column, f3column, f4column;
	private HorizontalLayout hlsearchlayout;
	private String userName;
	private Long companyId, stockId, branchId;
	private int recordCnt = 0;
	private MaterialStockService serviceMaterialStock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private BeanItemContainer<MaterialStockDM> beanMaterialStock;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private Logger logger = Logger.getLogger(MaterialStockDM.class);
	
	public MaterialStock() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside MaterialStock() constructor");
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		hlCmdBtnLayout.removeComponent(btnSave);
		tfLotno = new GERPTextField("Lot No");
		tfCurrentStock = new GERPTextField("Current Stock");
		tfParkedStock = new GERPTextField("Parked Stock");
		tfEffectiveStock = new GERPTextField("Effective Stock");
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchdetails();
		cbMaterial = new GERPComboBox("Material");
		cbMaterial.setItemCaptionPropertyId("materialName");
		loadMaterialdetails();
		cbStockType = new GERPComboBox("StockType", BASEConstants.T_MMS_MATERIAL_STOCK, BASEConstants.STOCKTYPE);
		hlsearchlayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		btnAdd.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnEdit.setCaption("View");
		btnEdit.setStyleName("view");
		hlsearchlayout.removeAllComponents();
		btnAdd.setVisible(false);
		hlsearchlayout.removeAllComponents();
		f1column = new FormLayout();
		f2column = new FormLayout();
		f3column = new FormLayout();
		f4column = new FormLayout();
		f1column.addComponent(cbBranch);
		f2column.addComponent(cbMaterial);
		f3column.addComponent(tfLotno);
		f4column.addComponent(cbStockType);
		hlsearchlayout.addComponent(f1column);
		hlsearchlayout.addComponent(f2column);
		hlsearchlayout.addComponent(f3column);
		hlsearchlayout.addComponent(f4column);
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		hlUserInputLayout.removeAllComponents();
		f1column = new FormLayout();
		f2column = new FormLayout();
		f3column = new FormLayout();
		f4column = new FormLayout();
		f1column.addComponent(cbBranch);
		f1column.addComponent(cbMaterial);
		f2column.addComponent(tfLotno);
		f2column.addComponent(tfCurrentStock);
		f3column.addComponent(tfParkedStock);
		f3column.addComponent(tfEffectiveStock);
		f4column.addComponent(cbStockType);
		hlUserInputLayout.addComponent(f1column);
		hlUserInputLayout.addComponent(f2column);
		hlUserInputLayout.addComponent(f3column);
		hlUserInputLayout.addComponent(f4column);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<MaterialStockDM> listMatStock = new ArrayList<MaterialStockDM>();
			listMatStock = serviceMaterialStock.getMaterialStockList((Long) cbMaterial.getValue(), companyId, null,
					(Long) cbBranch.getValue(), tfLotno.getValue(), (String) cbStockType.getValue(), "F");
			recordCnt = listMatStock.size();
			beanMaterialStock = new BeanItemContainer<MaterialStockDM>(MaterialStockDM.class);
			beanMaterialStock.addAll(listMatStock);
			tblMstScrSrchRslt.setContainerDataSource(beanMaterialStock);
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "stockid", "branchName", "materialName", "lotNo",
					"stockType", "materialUOM", "lastUpdateddt", "lastUpdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch", "Material", "Lot.No", "Stock Type",
					"Material UOM", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of.Records :" + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("loadSrchRslt-->" + e);
		}
	}
	
	private void editMaterialStock() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Editing the selected record");
		if (tblMstScrSrchRslt.getValue() != null) {
			MaterialStockDM materialStockDM = beanMaterialStock.getItem(tblMstScrSrchRslt.getValue()).getBean();
			stockId = materialStockDM.getStockid();
			cbBranch.setValue(materialStockDM.getBranchId());
			cbMaterial.setValue(materialStockDM.getMaterialId());
			cbStockType.setValue(materialStockDM.getStockType());
			tfLotno.setValue(materialStockDM.getLotNo());
			if (materialStockDM.getCurrentStock() != null) {
				tfCurrentStock.setValue(materialStockDM.getCurrentStock().toString());
			}
			if (materialStockDM.getParkedStock() != null) {
				tfParkedStock.setValue(materialStockDM.getParkedStock().toString());
			}
			if (materialStockDM.getEffectiveStock() != null) {
				tfEffectiveStock.setValue(materialStockDM.getEffectiveStock().toString());
			}
		}
		readonlytrue();
	}
	
	private void loadBranchdetails() {
		BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanbranch.setBeanIdProperty("branchId");
		beanbranch.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyId, "P"));
		cbBranch.setContainerDataSource(beanbranch);
	}
	
	// Loading Material List
	private void loadMaterialdetails() {
		try {
			List<MaterialDM> list = new ArrayList<MaterialDM>();
			list.add(new MaterialDM(0L, "All Materials"));
			list.addAll(serviceMaterial.getMaterialList(null, companyId, null, null, null, null, null, null, "Active",
					"P"));
			BeanContainer<Long, MaterialDM> beanmaterial = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
			beanmaterial.setBeanIdProperty("materialId");
			beanmaterial.addAll(list);
			cbMaterial.setContainerDataSource(beanmaterial);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
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
	
	@Override
	protected void resetSearchDetails() {
		cbBranch.setValue(branchId);
		cbMaterial.setValue(0L);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
	}
	
	private void readonlytrue() {
		cbBranch.setReadOnly(true);
		cbMaterial.setReadOnly(true);
		cbStockType.setReadOnly(true);
		tfCurrentStock.setReadOnly(true);
		tfEffectiveStock.setReadOnly(true);
		tfLotno.setReadOnly(true);
		tfParkedStock.setReadOnly(true);
	}
	
	private void readonlyfalse() {
		cbBranch.setReadOnly(false);
		cbMaterial.setReadOnly(false);
		cbStockType.setReadOnly(false);
		tfCurrentStock.setReadOnly(false);
		tfEffectiveStock.setReadOnly(false);
		tfLotno.setReadOnly(false);
		tfParkedStock.setReadOnly(false);
	}
	
	@Override
	protected void editDetails() {
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editMaterialStock();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean errorflag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((tfCurrentStock.getValue() == null) || tfCurrentStock.getValue().trim().length() == 0) {
			tfCurrentStock.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_STOCK));
			errorflag = true;
		}
		if (cbStockType.getValue() == null) {
			cbStockType.setComponentError(new UserError(GERPErrorCodes.NULL_STOCK_TYPE));
			errorflag = true;
		}
		if (tfLotno.getValue() == "" || tfLotno.getValue().trim().length() < 0) {
			tfLotno.setComponentError(new UserError("Given Integer values"));
			errorflag = true;
		} else {
			tfLotno.setComponentError(null);
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + tfCurrentStock.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		logger.info("saveDetails---------->");
		try {
			MaterialStockDM materialStockDM = new MaterialStockDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				materialStockDM = beanMaterialStock.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			materialStockDM.setCompanyId(companyId);
			materialStockDM.setBranchId((Long) cbBranch.getValue());
			materialStockDM.setMaterialId((Long) cbMaterial.getValue());
			materialStockDM.setLotNo(tfLotno.getValue());
			if (tfCurrentStock.getValue().trim().length() > 0) {
				materialStockDM.setCurrentStock(Long.valueOf(tfCurrentStock.getValue()));
			}
			if (tfParkedStock.getValue().trim().length() > 0) {
				materialStockDM.setParkedStock(Long.valueOf(tfParkedStock.getValue()));
			}
			if (tfEffectiveStock.getValue().trim().length() > 0) {
				materialStockDM.setEffectiveStock(Long.valueOf(tfEffectiveStock.getValue()));
			}
			materialStockDM.setStockType((String) cbStockType.getValue());
			materialStockDM.setLastUpdateddt(DateUtils.getcurrentdate());
			materialStockDM.setLastUpdatedby(userName);
			serviceMaterialStock.saveorupdatematerialstock(materialStockDM);
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for Material Stock. ID " + stockId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MMS_MATERIAL_STOCK);
		UI.getCurrent().getSession().setAttribute("audittablepk", stockId);
	}
	
	@Override
	protected void cancelDetails() {
		resetFields();
		assembleSearchLayout();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		readonlyfalse();
		cbBranch.setValue(branchId);
		cbMaterial.setValue(0L);
		tfCurrentStock.setValue("");
		tfParkedStock.setValue("");
		tfEffectiveStock.setValue("");
		tfLotno.setValue("");
		cbStockType.setValue(null);
	}
}
