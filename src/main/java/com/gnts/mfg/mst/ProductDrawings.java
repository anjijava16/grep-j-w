/**
 * File Name	:	ProductDrawings.java
 * Description	:	This Screen Purpose for Modify the ProductDrawings Details.
 * 					Add the ProductDrawings details process should be directly added in DB.
 * Author		:	Nandhakumar.S
 * 
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1         26-Jul-2014		  Nandhakumar.S		   Initial version
 * 
 */
package com.gnts.mfg.mst;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.mst.ProductDrawingDM;
import com.gnts.mfg.service.mst.ProductDrawingService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ProductDrawings extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProductDrawingService serviceProductDrawings = (ProductDrawingService) SpringContextHelper
			.getBean("productDrawings");
	private ProductService serviceProductService = (ProductService) SpringContextHelper.getBean("Product");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// User Input Components
	private TextField tfDrgCode, tfVersionNO;
	private CheckBox chIsLatest;
	private ComboBox cbBranchName, cbProductName, cbPrdDrgStatus;
	private TextArea taDrgCodeDesc;
	// BeanItem container of ProductDrawingDM
	private BeanItemContainer<ProductDrawingDM> beanProductDrawing = null;
	// local variables declaration
	private String userName;
	private Long companyId;
	private int recordCnt;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private Long productDrgId;
	// Initialize logger
	private static Logger logger = Logger.getLogger(ProductDrawings.class);
	
	// Constructor received the parameters from Login UI class
	public ProductDrawings() {
		// Get the logged in user name and company id from the session
		userName = (String) UI.getCurrent().getSession().getAttribute("loginUserName");
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside ProductDrawings() constructor");
		// Loading the ProductDrawings UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " ProductDrawings UI");
		tfDrgCode = new GERPTextField("Drawing Code");
		tfVersionNO = new GERPTextField("Version Number");
		chIsLatest = new CheckBox("Is Latest");
		cbBranchName = new GERPComboBox("Branch Name");
		cbBranchName.setWidth("140");
		cbBranchName.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbProductName = new GERPComboBox("Product Name");
		cbProductName.setItemCaptionPropertyId("prodname");
		cbProductName.setWidth("140");
		loadProductList();
		cbPrdDrgStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		taDrgCodeDesc = new GERPTextArea("Drawing Code Desc.");
		taDrgCodeDesc.setWidth("200");
		taDrgCodeDesc.setHeight("55");
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.setPageLength(13);
		List<ProductDrawingDM> productDrawingList = new ArrayList<ProductDrawingDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyId);
		Long branchId = (Long) cbBranchName.getValue();
		Long ProductId = (Long) cbProductName.getValue();
		productDrawingList = serviceProductDrawings.getProductDrgDetails(companyId, branchId, ProductId,
				(String) tfDrgCode.getValue(), (String) cbPrdDrgStatus.getValue());
		recordCnt = productDrawingList.size();
		beanProductDrawing = new BeanItemContainer<ProductDrawingDM>(ProductDrawingDM.class);
		beanProductDrawing.addAll(productDrawingList);
		tblMstScrSrchRslt.setContainerDataSource(beanProductDrawing);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "productDrgId", "branchName", "productName", "drawingCode",
				"versionNo", "isLatest", "drwStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Product Name", "Drawing Code",
				"Version Number", "Is Latest", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("productDrgId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		tblMstScrSrchRslt.setSelectable(true);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for ProductDrawings UI search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		// Adding components into form layouts for ProductDrawings UI search layout
		flColumn1.addComponent(cbBranchName);
		flColumn2.addComponent(cbProductName);
		flColumn3.addComponent(tfDrgCode);
		flColumn4.addComponent(cbPrdDrgStatus);
		// Adding form layouts into search layout for ProductDrawings UI search mode
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ " assembleInputUserLayout layout");
		/*
		 * Adding user input layout to the input layout as all the fields in the user input are available in the edit
		 * block. hence the same layout used as is
		 */
		// Set required fields
		cbBranchName.setRequired(true);
		cbProductName.setRequired(true);
		tfDrgCode.setRequired(true);
		tfVersionNO.setRequired(true);
		chIsLatest.setRequired(true);
		// Removing components from search layout and re-initializing form layouts
		hlSearchLayout.removeAllComponents();
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		// adding components into first column in form layout1
		flColumn1.addComponent(cbBranchName);
		flColumn1.addComponent(cbProductName);
		// adding components into second column in form layout2
		flColumn2.addComponent(tfDrgCode);
		flColumn2.addComponent(tfVersionNO);
		// adding components into third column in form layout3
		flColumn3.addComponent(taDrgCodeDesc);
		// add input fields into fourth column in form layout4
		flColumn4.addComponent(chIsLatest);
		flColumn4.addComponent(cbPrdDrgStatus);
		// adding form layouts into user input layouts
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSizeUndefined();
	}
	
	private void loadProductList() {
		List<ProductDM> getProductList = new ArrayList<ProductDM>();
		getProductList.addAll(serviceProductService.getProductList(companyId, null, null, null, "Active", null, null,
				"F"));
		BeanContainer<Long, ProductDM> beanProduct = new BeanContainer<Long, ProductDM>(ProductDM.class);
		beanProduct.setBeanIdProperty("prodid");
		beanProduct.addAll(getProductList);
		cbProductName.setContainerDataSource(beanProduct);
	}
	
	private void loadBranchList() {
		List<BranchDM> getBranchList = new ArrayList<BranchDM>();
		getBranchList.addAll(serviceBranch.getBranchList(null, null, null, "Active", null, "F"));
		BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanbranch.setBeanIdProperty("branchId");
		beanbranch.addAll(getBranchList);
		cbBranchName.setContainerDataSource(beanbranch);
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
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbBranchName.setValue(null);
		cbProductName.setValue(null);
		tfDrgCode.setValue("");
		cbPrdDrgStatus.setValue(cbPrdDrgStatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		resetFields();
		assembleInputUserLayout();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editProductDrawingDetails();
	}
	
	private void editProductDrawingDetails() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			ProductDrawingDM editPrdDrgList = beanProductDrawing.getItem(tblMstScrSrchRslt.getValue()).getBean();
			productDrgId = editPrdDrgList.getProductDrgIdLong();
			cbBranchName.setValue(editPrdDrgList.getBranchId());
			cbProductName.setValue(editPrdDrgList.getProductId());
			if (editPrdDrgList.getIsLatest().equals("Yes")) {
				chIsLatest.setValue(true);
			} else {
				chIsLatest.setValue(false);
			}
			if (editPrdDrgList.getDrawingDesc() != null) {
				taDrgCodeDesc.setValue((String) itselect.getItemProperty("drawingDesc").getValue());
			}
			tfDrgCode.setValue((String) itselect.getItemProperty("drawingCode").getValue());
			tfVersionNO.setValue(itselect.getItemProperty("versionNo").getValue().toString());
			cbPrdDrgStatus.setValue((String) itselect.getItemProperty("drwStatus").getValue());
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for ProductDrawings ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_MFG_PRODUCT_DWG);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(productDrgId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		cbBranchName.setRequired(false);
		cbProductName.setRequired(false);
		tfDrgCode.setRequired(false);
		tfVersionNO.setRequired(false);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbBranchName.setValue(null);
		cbBranchName.setComponentError(null);
		cbProductName.setValue(null);
		cbProductName.setComponentError(null);
		cbPrdDrgStatus.setValue(cbPrdDrgStatus.getItemIds().iterator().next());
		tfDrgCode.setValue("");
		tfDrgCode.setComponentError(null);
		tfVersionNO.setValue("");
		tfVersionNO.setComponentError(null);
		chIsLatest.setValue(null);
		taDrgCodeDesc.setValue("");
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if ((cbBranchName.getValue() == null)) {
			cbBranchName.setComponentError(new UserError(GERPErrorCodes.PRD_DRG_BRNCH_NAME));
			errorFlag = true;
		} else {
			cbBranchName.setComponentError(null);
		}
		if ((cbProductName.getValue() == null)) {
			cbProductName.setComponentError(new UserError(GERPErrorCodes.PRD_DRG_PRD_NAME));
			errorFlag = true;
		} else {
			cbProductName.setComponentError(null);
		}
		if (tfDrgCode.getValue() == "" || tfDrgCode.getValue() == null || tfDrgCode.getValue().trim().length() == 0) {
			tfDrgCode.setComponentError(new UserError(GERPErrorCodes.PRD_DRG_PRD_CODE));
			errorFlag = true;
		} else {
			tfDrgCode.setComponentError(null);
		}
		try {
			new BigDecimal(tfVersionNO.getValue());
			tfVersionNO.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfVersionNO.setComponentError(new UserError(GERPErrorCodes.PRD_DRG_VERS_NO));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		ProductDrawingDM productDgrObj = new ProductDrawingDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			productDgrObj = beanProductDrawing.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		productDgrObj.setCompanyId(companyId);
		productDgrObj.setBranchId((Long) cbBranchName.getValue());
		productDgrObj.setProductId((Long) cbProductName.getValue());
		productDgrObj.setDrawingCode(tfDrgCode.getValue());
		productDgrObj.setVersionNo(new BigDecimal(tfVersionNO.getValue()));
		productDgrObj.setDrawingDesc(taDrgCodeDesc.getValue());
		if (chIsLatest.getValue() == null || chIsLatest.getValue().equals(false)) {
			productDgrObj.setIsLatest("N");
		} else {
			productDgrObj.setIsLatest("Y");
		}
		productDgrObj.setDrwStatus((String) cbPrdDrgStatus.getValue());
		productDgrObj.setLastUpdatedDt(DateUtils.getcurrentdate());
		productDgrObj.setLastUpdatedBy(userName);
		serviceProductDrawings.saveorUpdateProductDrgDetails(productDgrObj);
		resetFields();
		loadSrchRslt();
	}
}
