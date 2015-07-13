/**
 * File Name	:	ProductParkedStock.java
 * Description	:	Used for view ProductParkedStockDM details
 * Author		:	GANGA S
 * Date			:	Oct 8, 2014
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies pvt. ltd.
 * Version         Date           Modified By             Remarks
 * 0.1           Oct 8, 2014       GANGA S              Initial Version
 * */
package com.gnts.sms.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.sms.domain.txn.ProductParkedStockDM;
import com.gnts.sms.service.txn.ProductParkedStockService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ProductParked extends BaseUI {
	private ProductParkedStockService serviceProductParkedStock = (ProductParkedStockService) SpringContextHelper
			.getBean("productparkedstock");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfParkedQty, tfUsedQty, tfBalQty, tfRefNo;
	private ComboBox cbStockType;
	private ComboBox cbProductse, cbBranchse, cbStockTypese;
	private PopupDateField dfParkedDate, dfRefDate;
	private TextArea taRemarks;
	// Bean Container
	private BeanItemContainer<ProductParkedStockDM> beanProductParkedStockDM = null;
	// Local variables
	private Long companyid;
	private String username;
	private Long branchId;
	private int recordCnt = 0;
	// Initialize logger
	private Logger logger = Logger.getLogger(ProductParked.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public ProductParked() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmailLogger() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Email logger UI");
		btnAdd.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnEdit.setCaption("View");
		btnEdit.setStyleName("view");
		dfParkedDate = new GERPPopupDateField("Parked Date");
		dfParkedDate.setInputPrompt("Select Date");
		dfParkedDate.setReadOnly(false);
		cbProductse = new GERPComboBox("Product Name");
		cbProductse.setItemCaptionPropertyId("prodname");
		loadProdList();
		cbBranchse = new GERPComboBox("Branch Name");
		cbBranchse.setItemCaptionPropertyId("branchName");
		loadbranchlist();
		cbStockType = new GERPComboBox("Stock Type");
		cbStockType.addItem("new");
		cbStockType.addItem("scrap");
		cbStockType.addItem("Refurbish");
		cbStockTypese = new GERPComboBox("Stock Type");
		cbStockTypese.addItem("new");
		cbStockTypese.addItem("scrap");
		cbStockTypese.addItem("Refurbish");
		tfParkedQty = new TextField("Parked Qty");
		tfUsedQty = new TextField("Used Qty");
		tfBalQty = new TextField("Balance Qty");
		tfRefNo = new TextField("Reference No");
		dfRefDate = new GERPPopupDateField("Reference Date ");
		dfRefDate.setInputPrompt("Select Date");
		taRemarks = new TextArea("Reference Remark");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		// Add components for Search Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn1.addComponent(cbBranchse);
		flColumn2.addComponent(cbProductse);
		flColumn3.addComponent(cbStockType);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		btnSave.setVisible(false);
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setMargin(true);
		// Add components for User Input Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		flColumn1.addComponent(dfParkedDate);
		flColumn1.addComponent(cbBranchse);
		flColumn1.addComponent(cbProductse);
		flColumn2.addComponent(cbStockTypese);
		flColumn2.addComponent(tfParkedQty);
		flColumn2.addComponent(tfUsedQty);
		flColumn3.addComponent(tfBalQty);
		flColumn3.addComponent(tfRefNo);
		flColumn3.addComponent(dfRefDate);
		flColumn4.addComponent(taRemarks);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void viewLogger() {
		if (tblMstScrSrchRslt.getValue() != null) {
			ProductParkedStockDM productParkedStockDM = beanProductParkedStockDM.getItem(tblMstScrSrchRslt.getValue())
					.getBean();
			setReadOnlyFalseFields();
			cbProductse.setValue(productParkedStockDM.getProductId());
			cbBranchse.setValue(productParkedStockDM.getBranchId());
			dfParkedDate.setValue(productParkedStockDM.getParkedDate());
			cbStockTypese.setValue(productParkedStockDM.getStockType().toString());
			tfParkedQty.setValue(productParkedStockDM.getParkedQty().toString());
			tfUsedQty.setValue(productParkedStockDM.getUsedQty().toString());
			tfBalQty.setValue(productParkedStockDM.getBalanceQty().toString());
			tfRefNo.setValue(productParkedStockDM.getReferenceNo());
			dfRefDate.setValue(productParkedStockDM.getReferenceDate());
			taRemarks.setValue(productParkedStockDM.getReferenceRemark());
			setReadOnlyTrueFields();
		}
	}
	
	public void setReadOnlyFalseFields() {
		cbProductse.setReadOnly(false);
		cbBranchse.setReadOnly(false);
		dfParkedDate.setReadOnly(false);
		cbStockTypese.setReadOnly(false);
		tfParkedQty.setReadOnly(false);
		tfUsedQty.setReadOnly(false);
		tfBalQty.setReadOnly(false);
		tfRefNo.setReadOnly(false);
		dfRefDate.setReadOnly(false);
		taRemarks.setReadOnly(false);
	}
	
	public void setReadOnlyTrueFields() {
		cbProductse.setReadOnly(true);
		cbBranchse.setReadOnly(true);
		dfParkedDate.setReadOnly(true);
		cbStockTypese.setReadOnly(true);
		tfParkedQty.setReadOnly(true);
		tfUsedQty.setReadOnly(true);
		tfBalQty.setReadOnly(true);
		tfRefNo.setReadOnly(true);
		dfRefDate.setReadOnly(true);
		taRemarks.setReadOnly(true);
	}
	
	// get the search result from DB based on the search ProductParkedStockDM
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<ProductParkedStockDM> parkedList = new ArrayList<ProductParkedStockDM>();
		// String productStock = (String) cbStockType.getValue();
		parkedList = serviceProductParkedStock.getProductParkedStockList((Long) cbProductse.getValue(), null,
				(Long) cbBranchse.getValue(), (String) cbStockType.getValue(), "F");
		recordCnt = parkedList.size();
		beanProductParkedStockDM = new BeanItemContainer<ProductParkedStockDM>(ProductParkedStockDM.class);
		beanProductParkedStockDM.addAll(parkedList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Ratesetting. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanProductParkedStockDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "parkProductStockId", "branchName", "prodname", "stockType",
				"lastUpdateddt", "lastUpdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Product Name", "Stock Type",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("parkProductStockId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdateddt", "No.of Records : " + recordCnt);
	}
	
	// Load Product List
	private void loadProdList() {
		try {
			List<ProductDM> list = new ArrayList<ProductDM>();
			list.add(new ProductDM(0L, "All Products"));
			list.addAll(serviceProduct.getProductList(companyid, null, null, null, null, null, null, "P"));
			BeanContainer<Long, ProductDM> beanprod = new BeanContainer<Long, ProductDM>(ProductDM.class);
			beanprod.setBeanIdProperty("prodid");
			beanprod.addAll(list);
			cbProductse.setContainerDataSource(beanprod);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Loading Branch List
	private void loadbranchlist() {
		BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanbranch.setBeanIdProperty("branchId");
		beanbranch.addAll(serviceBranch.getBranchList(null, null, null, null, companyid, "P"));
		cbBranchse.setContainerDataSource(beanbranch);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		hlUserInputLayout.removeAllComponents();
		try {
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
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbBranchse.setValue(branchId);
		cbProductse.setValue(0L);
		cbStockType.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		// reset the input controls to default value
		resetFields();
		assembleUserInputLayout();
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
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		setReadOnlyFalseFields();
		dfParkedDate.setValue(null);
		dfRefDate.setValue(null);
		cbBranchse.setValue(branchId);
		cbProductse.setValue(0L);
		cbStockType.setValue(null);
		tfBalQty.setValue("");
		tfParkedQty.setValue("");
		tfRefNo.setValue("");
		tfUsedQty.setValue("");
		taRemarks.setValue("");
		cbStockTypese.setValue(null);
	}
}
