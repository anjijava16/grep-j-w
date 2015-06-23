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
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
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
	private TextField tflotno, tfcurrentstock, tfparkedstock, tfeffectivestock;
	private ComboBox cbbranch, cbmaterial, cbstocktype;
	private FormLayout f1column, f2column, f3column, f4column;
	private HorizontalLayout hlsearchlayout;
	private String userName;
	private Long companyId, stockId, branchId;
	private int recordCnt = 0;
	private MaterialStockService servicematerialstock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private BranchService servicebranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private MaterialService servicematerial = (MaterialService) SpringContextHelper.getBean("material");
	BeanItemContainer<MaterialStockDM> beanmaterialstock;
	BeanContainer<Long, BranchDM> beanbranch;
	BeanContainer<Long, MaterialDM> beanmaterial;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	Logger logger = Logger.getLogger(MaterialStockDM.class);
	Button btnScreenName = new Button();
	
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
		tflotno = new GERPTextField("Lot No");
		tfcurrentstock = new GERPTextField("Current Stock");
		tfparkedstock = new GERPTextField("Parked Stock");
		tfeffectivestock = new GERPTextField("Effective Stock");
		cbbranch = new GERPComboBox("Branch");
		cbbranch.setItemCaptionPropertyId("branchName");
		loadbranchdetails();
		cbmaterial = new GERPComboBox("Material");
		cbmaterial.setItemCaptionPropertyId("materialName");
		loadmaterialdetails();
		cbstocktype = new GERPComboBox("StockType", BASEConstants.T_MMS_MATERIAL_STOCK, BASEConstants.STOCKTYPE);
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
		f1column.addComponent(cbbranch);
		f2column.addComponent(cbmaterial);
		f3column.addComponent(tflotno);
		f4column.addComponent(cbstocktype);
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
		f1column.addComponent(cbbranch);
		f1column.addComponent(cbmaterial);
		f2column.addComponent(tflotno);
		f2column.addComponent(tfcurrentstock);
		f3column.addComponent(tfparkedstock);
		f3column.addComponent(tfeffectivestock);
		f4column.addComponent(cbstocktype);
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
			List<MaterialStockDM> materiallist = new ArrayList<MaterialStockDM>();
			materiallist = servicematerialstock.getMaterialStockList((Long) cbmaterial.getValue(), companyId, null,
					(Long) cbbranch.getValue(), tflotno.getValue(), (String) cbstocktype.getValue(), "F");
			recordCnt = materiallist.size();
			beanmaterialstock = new BeanItemContainer<MaterialStockDM>(MaterialStockDM.class);
			beanmaterialstock.addAll(materiallist);
			tblMstScrSrchRslt.setContainerDataSource(beanmaterialstock);
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
		Item slectedrecd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Selected Dept. Id -> "
				+ stockId);
		if (slectedrecd != null) {
			MaterialStockDM editmaterialstock = beanmaterialstock.getItem(tblMstScrSrchRslt.getValue()).getBean();
			stockId = editmaterialstock.getStockid();
			cbbranch.setValue(editmaterialstock.getBranchId());
			cbmaterial.setValue(editmaterialstock.getMaterialId());
			cbstocktype.setValue(editmaterialstock.getStockType());
			tflotno.setValue(editmaterialstock.getLotNo());
			if (editmaterialstock.getCurrentStock() != null) {
				tfcurrentstock.setValue(editmaterialstock.getCurrentStock().toString());
			}
			if (editmaterialstock.getParkedStock() != null) {
				tfparkedstock.setValue(editmaterialstock.getParkedStock().toString());
			}
			if (editmaterialstock.getEffectiveStock() != null) {
				tfeffectivestock.setValue(editmaterialstock.getEffectiveStock().toString());
			}
		}
		readonlytrue();
	}
	
	private void loadbranchdetails() {
		List<BranchDM> branchlist = servicebranch.getBranchList(null, null, null, "Active", companyId, "P");
		beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanbranch.setBeanIdProperty("branchId");
		beanbranch.addAll(branchlist);
		cbbranch.setContainerDataSource(beanbranch);
	}
	
	// Loading Material List
	public void loadmaterialdetails() {
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
		cbbranch.setValue(branchId);
		cbmaterial.setValue(0L);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
	}
	
	public void readonlytrue() {
		cbbranch.setReadOnly(true);
		cbmaterial.setReadOnly(true);
		cbstocktype.setReadOnly(true);
		tfcurrentstock.setReadOnly(true);
		tfeffectivestock.setReadOnly(true);
		tflotno.setReadOnly(true);
		tfparkedstock.setReadOnly(true);
	}
	
	public void readonlyfalse() {
		cbbranch.setReadOnly(false);
		cbmaterial.setReadOnly(false);
		cbstocktype.setReadOnly(false);
		tfcurrentstock.setReadOnly(false);
		tfeffectivestock.setReadOnly(false);
		tflotno.setReadOnly(false);
		tfparkedstock.setReadOnly(false);
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
		if ((tfcurrentstock.getValue() == null) || tfcurrentstock.getValue().trim().length() == 0) {
			tfcurrentstock.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_STOCK));
			errorflag = true;
		}
		if (cbstocktype.getValue() == null) {
			cbstocktype.setComponentError(new UserError(GERPErrorCodes.NULL_STOCK_TYPE));
			errorflag = true;
		}
		if (tflotno.getValue() == "" || tflotno.getValue().trim().length() < 0) {
			tflotno.setComponentError(new UserError("Given Integer values"));
			errorflag = true;
		} else {
			tflotno.setComponentError(null);
			// errorflag=false;
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + tfcurrentstock.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		logger.info("saveDetails---------->");
		try {
			MaterialStockDM savematerial = new MaterialStockDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				savematerial = beanmaterialstock.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			savematerial.setCompanyId(companyId);
			savematerial.setBranchId((Long) cbbranch.getValue());
			savematerial.setMaterialId((Long) cbmaterial.getValue());
			savematerial.setLotNo(tflotno.getValue());
			if (tfcurrentstock.getValue().trim().length() > 0) {
				savematerial.setCurrentStock(Long.valueOf(tfcurrentstock.getValue()));
			}
			if (tfparkedstock.getValue().trim().length() > 0) {
				savematerial.setParkedStock(Long.valueOf(tfparkedstock.getValue()));
			}
			if (tfeffectivestock.getValue().trim().length() > 0) {
				savematerial.setEffectiveStock(Long.valueOf(tfeffectivestock.getValue()));
			}
			savematerial.setStockType((String) cbstocktype.getValue());
			savematerial.setLastUpdateddt(DateUtils.getcurrentdate());
			savematerial.setLastUpdatedby(userName);
			servicematerialstock.saveorupdatematerialstock(savematerial);
//			resetFields();
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
		cbbranch.setValue(branchId);
		cbmaterial.setValue(0L);
		tfcurrentstock.setValue("");
		tfparkedstock.setValue("");
		tfeffectivestock.setValue("");
		tflotno.setValue("");
		cbstocktype.setValue(null);
	}
}
