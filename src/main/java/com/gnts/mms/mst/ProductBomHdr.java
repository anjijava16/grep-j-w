/**
 * File Name	:	ProductBomHdr.java
 * Description	:	This Screen Purpose for Modify the ProductBomHdr Details.
 * 					Add the ProductBomHdr details process should be directly added in DB.
 * Author		:	Mahaboob Subahan J
 * Date			:	Jul 30, 2014
 * Modification :   
 * Modified By  :  Arun jeyaraj R 
 * Description 	:
 *
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version      Date           	Modified By      		Remarks
 * 0.1          Jul 30, 2014   	Mahaboob Subahan J		Initial Version	
 * 0.2     	   nov 272014   	Arun Jeyaraj R		modification		
	
 * 
 */
package com.gnts.mms.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.mst.ProductBomDtlDM;
import com.gnts.mms.domain.mst.ProductBomHdrDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.mst.ProductBomDtlService;
import com.gnts.mms.service.mst.ProductBomHdrService;
import com.gnts.mms.txn.MmsComments;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ProductBomHdr extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(ProductBomHdr.class);
	private ProductBomHdrService serviceProductBomHdr = (ProductBomHdrService) SpringContextHelper
			.getBean("productBomHdr");
	private ProductBomDtlService serviceProductBomDtl = (ProductBomDtlService) SpringContextHelper
			.getBean("productBomDtl");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private BeanItemContainer<ProductBomDtlDM> beanProductBomDtl = null;
	private BeanItemContainer<ProductBomHdrDM> beanProductBomHdr = null;
	private List<ProductBomDtlDM> productBomDtlList = new ArrayList<ProductBomDtlDM>();
	// Product Bom Hdr Component Declaration
	private ComboBox cbProduct, cbBranch, cbBomStatus;
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private TextField tfBomVersion;
	// Product Bom Dtl Component Declaration
	private ComboBox cbMaterialUOM;
	private ListSelect cbMaterialName;
	private ComboBox cbMaterialStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private TextField tfMaterialQty;
	private Button btnaddBomDtl = new GERPButton("Add", "addbt", this);
	private Table tblBomDtl = new GERPTable();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private Long companyId, moduleId, branchId, bomId;
	private String userName;
	private int recordCnt = 0, recordCntBomDtl = 0;
	private MmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	private String status;
	private Long employeeId;
	
	public ProductBomHdr() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = Long.valueOf(UI.getCurrent().getSession().getAttribute("moduleId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside Material() constructor");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Painting Product Bom Hdr UI");
		// Product Bom Hdr Component Definition
		btndelete.setEnabled(false);
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setWidth("200");
		cbProduct.setRequired(true);
		cbProduct.setItemCaptionPropertyId("prodname");
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setWidth("200");
		cbBranch.setRequired(true);
		cbBranch.setItemCaptionPropertyId("branchName");
		tfBomVersion = new GERPTextField("BOM Version");
		tfBomVersion.setWidth("120");
		cbBomStatus = new GERPComboBox("Status", BASEConstants.M_MMS_PRODUCT_BOM_HDR, BASEConstants.BOM_STATUS);
		cbBomStatus.setWidth("120");
		// Product Bom Dtl Component Definition
		cbMaterialName = new ListSelect("Material Name");
		cbMaterialName.setWidth("200");
		cbMaterialName.setHeight("80px");
		cbMaterialName.setItemCaptionPropertyId("materialName");
		cbMaterialName.setMultiSelect(true);
		loadMaterialList();
		tfMaterialQty = new TextField();
		tfMaterialQty.setValue("0");
		tfMaterialQty.setWidth("100");
		cbMaterialUOM = new ComboBox();
		cbMaterialUOM.setWidth("50");
		cbMaterialUOM.setHeight("18");
		cbMaterialUOM.setItemCaptionPropertyId("lookupname");
		loadMaterialUOMList();
		btnaddBomDtl.setStyleName("add");
		btnaddBomDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Product BOM Dtl Specification
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDtlDetails()) {
					saveProductBOMDetails();
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
		cbMaterialName.setImmediate(true);
		cbMaterialName.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbMaterialName.getValue() != null) {
					String[] split = cbMaterialName.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "")
							.split(",");
					for (String obj : split) {
						if (obj.trim().length() > 0) {
							cbMaterialUOM.setReadOnly(false);
							cbMaterialUOM.setValue(serviceMaterial
									.getMaterialList(Long.valueOf(obj.trim()), companyId, null, null, null, null, null,
											null, null, "F").get(0).getMaterialUOM());
							cbMaterialUOM.setReadOnly(true);
						}
					}
				}
			}
		});
		// ClickListener for Product BOM Dtl Tale
		tblBomDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblBomDtl.isSelected(event.getItemId())) {
					tblBomDtl.setImmediate(true);
					btnaddBomDtl.setCaption("Add");
					btnaddBomDtl.setStyleName("savebt");
					btndelete.setEnabled(false);
					resetBOMDetailFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddBomDtl.setCaption("Update");
					btnaddBomDtl.setStyleName("savebt");
					btndelete.setEnabled(true);
					editBomDtl();
				}
			}
		});
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		resetBOMDetailFields();
		loadBomHdrProductList();
		loadBomHdrBranchList();
		loadSrchRslt();
		cbBranch.setValue(branchId);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.removeAllItems();
		tblMstScrSrchRslt.setPageLength(13);
		List<ProductBomHdrDM> bomHdrList = new ArrayList<ProductBomHdrDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ cbProduct.getValue() + "," + cbBranch.getValue() + "," + cbBomStatus.getValue() + "," + companyId);
		bomHdrList = serviceProductBomHdr.getProductBomHdrList(null, companyId, (Long) cbBranch.getValue(),
				(Long) cbProduct.getValue(), null, null, null, null, null, (String) cbBomStatus.getValue(), "F");
		recordCnt = bomHdrList.size();
		beanProductBomHdr = new BeanItemContainer<ProductBomHdrDM>(ProductBomHdrDM.class);
		beanProductBomHdr.addAll(bomHdrList);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Got the Product Bom Hdr result set");
		tblMstScrSrchRslt.setContainerDataSource(beanProductBomHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "bomId", "branchName", "productName", "bomVersion",
				"bomStatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Product Name", "BOM Version",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("bomId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadProductBomDtlRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		tblBomDtl.setSelectable(true);
		tblBomDtl.removeAllItems();
		tblBomDtl.setPageLength(5);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are " + ","
				+ bomId + "," + companyId);
		recordCntBomDtl = productBomDtlList.size();
		beanProductBomDtl = new BeanItemContainer<ProductBomDtlDM>(ProductBomDtlDM.class);
		beanProductBomDtl.addAll(productBomDtlList);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Got the Product Bom Dtl result set");
		tblBomDtl.setContainerDataSource(beanProductBomDtl);
		tblBomDtl.setVisibleColumns(new Object[] { "bomDtlId", "materialName", "materialQty", "materialUom",
				"materialStatus", "lastupdateddt", "lastupdatedby" });
		tblBomDtl.setColumnHeaders(new String[] { "Ref.Id", "Material Name", "Quantity", "UOM", "Status",
				"Last Updated Date", "Last Updated By" });
		tblBomDtl.setColumnAlignment("bomDtlId", Align.RIGHT);
		tblBomDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCntBomDtl);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		cbProduct.setRequired(false);
		cbBranch.setRequired(false);
		// Remove all components in User Search Layout
		hlSearchLayout.removeAllComponents();
		// Add components for Search Layout
		FormLayout flProduct = new FormLayout();
		FormLayout flBranch = new FormLayout();
		FormLayout flBomStatus = new FormLayout();
		flProduct.addComponent(cbBranch);
		flBranch.addComponent(cbProduct);
		flBomStatus.addComponent(cbBomStatus);
		hlSearchLayout.addComponent(flProduct);
		hlSearchLayout.addComponent(flBranch);
		hlSearchLayout.addComponent(flBomStatus);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling User Input layout");
		cbProduct.setRequired(true);
		cbBranch.setRequired(true);
		// Remove all components in User Input Layout
		hlUserInputLayout.removeAllComponents();
		// Add Product BOM Hdr Components for User Input Layout
		FormLayout flBomHdr1 = new FormLayout();
		FormLayout flBomHdr2 = new FormLayout();
		FormLayout flBomHdr3 = new FormLayout();
		FormLayout flBomHdr4 = new FormLayout();
		flBomHdr1.addComponent(cbBranch);
		flBomHdr1.setSpacing(true);
		flBomHdr2.addComponent(cbProduct);
		flBomHdr2.setSpacing(true);
		flBomHdr3.addComponent(tfBomVersion);
		flBomHdr3.setSpacing(true);
		flBomHdr4.addComponent(cbBomStatus);
		flBomHdr4.setSpacing(true);
		HorizontalLayout hlBomHdr = new HorizontalLayout();
		hlBomHdr.addComponent(flBomHdr1);
		hlBomHdr.addComponent(flBomHdr2);
		hlBomHdr.addComponent(flBomHdr4);
		hlBomHdr.setSpacing(true);
		hlBomHdr.setMargin(true);
		hlBomHdr.setSizeUndefined();
		Label lblBomHdr = new Label("PRODUCT BILL OF MATERIAL HEADER");
		lblBomHdr.setStyleName("h4");
		// Add Product BOM Dtl Components for User Input Layout
		FormLayout flBomDtl1 = new FormLayout();
		FormLayout flBomDtl2 = new FormLayout();
		FormLayout flBomDtl3 = new FormLayout();
		flBomDtl1.addComponent(cbMaterialName);
		flBomDtl1.setSpacing(true);
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfMaterialQty);
		hlQtyUom.addComponent(cbMaterialUOM);
		hlQtyUom.setCaption("Material Qty");
		flBomDtl2.addComponent(hlQtyUom);
		flBomDtl2.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		flBomDtl2.addComponent(cbMaterialStatus);
		flBomDtl2.setSpacing(true);
		flBomDtl3.addComponent(btnaddBomDtl);
		flBomDtl3.addComponent(btndelete);
		flBomDtl3.setSpacing(true);
		HorizontalLayout hlBomDtl = new HorizontalLayout();
		hlBomDtl.addComponent(flBomDtl1);
		hlBomDtl.addComponent(flBomDtl2);
		hlBomDtl.addComponent(flBomDtl3);
		hlBomDtl.setSpacing(true);
		hlBomDtl.setMargin(true);
		hlBomDtl.setSizeUndefined();
		Label lblBomDtl = new Label("PRODUCT BILL OF MATERIAL DETAIL");
		lblBomDtl.setStyleName("h4");
		VerticalLayout vlBomDtlComponent = new VerticalLayout();
		vlBomDtlComponent.addComponent(hlBomDtl);
		vlBomDtlComponent.addComponent(tblBomDtl);
		vlBomDtlComponent.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlBomDtlComponent, "BOM Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		// Setting for all layout in vertical layout
		VerticalLayout vlAllComponent = new VerticalLayout();
		vlAllComponent.addComponent(lblBomHdr);
		vlAllComponent.addComponent(GERPPanelGenerator.createPanel(hlBomHdr));
		vlAllComponent.addComponent(lblBomDtl);
		vlAllComponent.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		vlAllComponent.setSpacing(true);
		vlAllComponent.setWidth("100%");
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(vlAllComponent);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setWidth("100%");
	}
	
	/*
	 * loadMaterialList()-->this function is used for load the material type
	 */
	private void loadMaterialList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Loading Material UOM Search...");
		BeanContainer<Long, MaterialDM> beanMaterial = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
		beanMaterial.setBeanIdProperty("materialId");
		beanMaterial.addAll(serviceMaterial.getMaterialList(null, companyId, null, null, null, null, null, null,
				"Active", "P"));
		cbMaterialName.setContainerDataSource(beanMaterial);
	}
	
	private void loadMaterialUOMList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Loading Material UOM Search...");
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp
				.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyId, moduleId, "Active", "MM_UOM"));
		cbMaterialUOM.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadBomHdrProductList()-->this function is used for load the product list
	 */
	private void loadBomHdrProductList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Product Search...");
		BeanContainer<Long, ProductDM> beanProduct = new BeanContainer<Long, ProductDM>(ProductDM.class);
		beanProduct.setBeanIdProperty("prodid");
		beanProduct.addAll(serviceProduct.getProductList(companyId, null, null, null, "Active", null, null, "P"));
		cbProduct.setContainerDataSource(beanProduct);
	}
	
	/*
	 * loadBomHdrBranchList()-->this function is used for load the branch list to Product BOM Hdr branch combo box
	 */
	private void loadBomHdrBranchList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Branch Search...");
		BeanContainer<Long, BranchDM> beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranch.setBeanIdProperty("branchId");
		beanBranch.addAll(serviceBranch.getBranchList(null, null, null, null, companyId, "P"));
		cbBranch.setContainerDataSource(beanBranch);
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbProduct.setValue(null);
		cbBranch.setValue(branchId);
		cbBomStatus.setValue("Draft");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tblMstScrSrchRslt.setValue(null);
		hlCmdBtnLayout.setVisible(false);
		// remove the table
		tblMstScrSrchRslt.setVisible(false);
		// remove the default value from branch combo box
		// cbBranch.removeItem(0L);
		assembleUserInputLayout();
		comments = new MmsComments(vlTableForm, null, companyId, null, null, null, null, null, null, null, status);
		bomId = 0L;
		resetFields();
		loadProductBomDtlRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Updating existing record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		// remove the table
		tblMstScrSrchRslt.setVisible(false);
		assembleUserInputLayout();
		resetFields();
		editProductBomHdr();
	}
	
	// Reset the selected row's data into Product BOM Hdr input components
	private void editProductBomHdr() {
		if (tblMstScrSrchRslt.getValue() != null) {
			ProductBomHdrDM editProductBomHdrList = beanProductBomHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			bomId = editProductBomHdrList.getBomId();
			if ((editProductBomHdrList.getProductId() != null)) {
				cbProduct.setValue(editProductBomHdrList.getProductId());
			}
			if ((editProductBomHdrList.getBranchId() != null)) {
				cbBranch.setValue(editProductBomHdrList.getBranchId());
			}
			if ((editProductBomHdrList.getBomVersion() != null)) {
				tfBomVersion.setValue(editProductBomHdrList.getBomVersion().toString());
			}
			if ((editProductBomHdrList.getBomStatus() != null)) {
				cbBomStatus.setValue(editProductBomHdrList.getBomStatus());
			}
			productBomDtlList.addAll(serviceProductBomDtl.getProductBomDtlList(null, bomId, null, null, "F"));
		}
		loadProductBomDtlRslt();
		comments = new MmsComments(vlTableForm, null, companyId, bomId, null, null, null, null, null, null, null);
		comments.loadsrch(true, null, null, bomId, null, null, null, null, null, null);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		Boolean errorFlag = false;
		cbProduct.setComponentError(null);
		cbBranch.setComponentError(null);
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_BOM_HEADER_NAME));
			errorFlag = true;
		}
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_BOM_BRANCH));
			errorFlag = true;
		}
		if (tblBomDtl.size() == 0) {
			cbMaterialUOM.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_QTY));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Throwing ValidationException. User data is > " + cbProduct.getValue() + "," + cbBranch.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
		ProductBomHdrDM productBOMObj = new ProductBomHdrDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			productBOMObj = beanProductBomHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		productBOMObj.setCompanyId(companyId);
		if (cbBranch.getValue() != null) {
			productBOMObj.setBranchId((Long) cbBranch.getValue());
		}
		if (cbProduct.getValue() != null) {
			productBOMObj.setProductId((Long) cbProduct.getValue());
		}
		if (cbBomStatus.getValue() != null) {
			productBOMObj.setBomStatus((String) cbBomStatus.getValue());
		}
		productBOMObj.setIsLatest("Y");
		productBOMObj.setPreparedBy(employeeId);
		productBOMObj.setBomVersion(Long.valueOf(serviceProductBomHdr.getProductBomHdrList(null, companyId, null,
				(Long) cbProduct.getValue(), null, null, null, null, null, null, "F").size() + 1));
		productBOMObj.setLastupdateddt(DateUtils.getcurrentdate());
		productBOMObj.setLastupdatedby(userName);
		serviceProductBomHdr.saveOrUpdateProductBomHdr(productBOMObj);
		@SuppressWarnings("unchecked")
		Collection<ProductBomDtlDM> prodBOMDtlItemIds = (Collection<ProductBomDtlDM>) tblBomDtl.getVisibleItemIds();
		for (ProductBomDtlDM saveProdBOMDtl : (Collection<ProductBomDtlDM>) prodBOMDtlItemIds) {
			saveProdBOMDtl.setBomId(Long.valueOf(productBOMObj.getBomId()));
			serviceProductBomDtl.saveOrUpdateProductBomDtl(saveProdBOMDtl);
		}
		comments.savebom(productBOMObj.getBomId(), productBOMObj.getBomStatus());
		loadSrchRslt();
		bomId = 0L;
		loadProductBomDtlRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		// reset the input controls to default value
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		assembleSearchLayout();
		tblBomDtl.removeAllItems();
		resetBOMDetailFields();
		resetFields();
		loadSrchRslt();
	}
	
	private boolean validateDtlDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if (cbMaterialName.getValue() == null || cbMaterialName.getValue().toString() == "[]") {
			cbMaterialName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			isValid = false;
		} else {
			cbMaterialName.setComponentError(null);
		}
		if (Long.valueOf(tfMaterialQty.getValue()) < 0) {
			cbMaterialUOM.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_QTYZero));
			isValid = false;
		} else {
			cbMaterialUOM.setComponentError(null);
		}
		if (tfMaterialQty.getValue().equals("0")) {
			cbMaterialUOM.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_QTYZero));
			isValid = false;
		} else {
			cbMaterialUOM.setComponentError(null);
		}
		return isValid;
	}
	
	/*
	 * saveProductBOMDetails()-->this function is used for save the product BOM details for temporary
	 */
	private void saveProductBOMDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Saving Product BOM Dtl Data... ");
		try {
			int count = 0;
			String[] split = cbMaterialName.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "")
					.split(",");
			for (String obj : split) {
				if (obj.trim().length() > 0) {
					for (ProductBomDtlDM productBomDtlDM : productBomDtlList) {
						if (productBomDtlDM.getMaterialId() == Long.valueOf(obj.trim())) {
							count++;
							break;
						}
					}
					System.out.println("count--->" + count);
					if (tblBomDtl.getValue() != null) {
						count = 0;
					}
					if (count == 0) {
						ProductBomDtlDM productBomDtlObj = new ProductBomDtlDM();
						if (tblBomDtl.getValue() != null) {
							productBomDtlObj = beanProductBomDtl.getItem(tblBomDtl.getValue()).getBean();
						}
						if (cbMaterialName.getValue() != null) {
							productBomDtlObj.setMaterialId(Long.valueOf(obj.trim()));
							productBomDtlObj.setMaterialName(serviceMaterial
									.getMaterialList(Long.valueOf(obj.trim()), null, null, null, null, null, null,
											null, null, "P").get(0).getMaterialName());
						}
						productBomDtlObj.setMaterialQty(Long.valueOf(tfMaterialQty.getValue()));
						cbMaterialUOM.setReadOnly(false);
						productBomDtlObj.setMaterialUom((String) cbMaterialUOM.getValue().toString());
						cbMaterialUOM.setReadOnly(true);
						productBomDtlObj.setMaterialStatus((String) cbMaterialStatus.getValue());
						productBomDtlObj.setLastupdateddt(DateUtils.getcurrentdate());
						productBomDtlObj.setLastupdatedby(userName);
						productBomDtlList.add(productBomDtlObj);
						loadProductBomDtlRslt();
						count = 0;
						btnaddBomDtl.setCaption("Add");
					} else {
						cbMaterialName.setComponentError(new UserError("Material name Already Exist.."));
					}
				}
			}
			resetBOMDetailFields();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * BomDtlResetFields()-->this function is used for reset product bom dtl fields
	 */
	private void resetBOMDetailFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting the Product BOM Dtl UI controls");
		cbMaterialName.setValue(null);
		tfMaterialQty.setValue("0");
		// tfMaterialQty.setComponentError(null);
		cbMaterialUOM.setReadOnly(false);
		cbMaterialUOM.setValue(null);
		cbMaterialUOM.setReadOnly(true);
		cbMaterialStatus.setValue(cbMaterialStatus.getItemIds().iterator().next());
		btnaddBomDtl.setCaption("Add");
	}
	
	/*
	 * editBomDtl()-->this function is used for restore the selected row's data to product bom dtl components
	 */
	private void editBomDtl() {
		if (tblBomDtl.getValue() != null) {
			ProductBomDtlDM productBomDtlDM = new ProductBomDtlDM();
			productBomDtlDM = beanProductBomDtl.getItem(tblBomDtl.getValue()).getBean();
			cbMaterialName.setValue(null);
			Long matId = productBomDtlDM.getMaterialId();
			Collection<?> empColId = cbMaterialName.getItemIds();
			for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMaterialName.getItem(itemIdClient);
				// Get the actual bean and use the data
				MaterialDM matObj = (MaterialDM) itemclient.getBean();
				if (matId != null && matId.equals(matObj.getMaterialId())) {
					cbMaterialName.select(itemIdClient);
				}
			}
			if (productBomDtlDM.getMaterialQty() != null) {
				tfMaterialQty.setValue(productBomDtlDM.getMaterialQty().toString());
			}
			cbMaterialStatus.setValue(productBomDtlDM.getMaterialStatus());
			cbMaterialUOM.setReadOnly(false);
			cbMaterialUOM.setValue(productBomDtlDM.getMaterialUom());
			cbMaterialUOM.setReadOnly(true);
		}
	}
	
	@Override
	protected void resetFields() {
		cbProduct.setValue(null);
		cbProduct.setComponentError(null);
		cbBranch.setValue(branchId);
		cbBranch.setComponentError(null);
		cbBomStatus.setValue("Draft");
		tfBomVersion.setValue("");
		tfBomVersion.setComponentError(null);
		cbMaterialName.setValue(null);
		cbMaterialName.setComponentError(null);
		cbMaterialUOM.setReadOnly(false);
		cbMaterialUOM.setValue(null);
		cbMaterialUOM.setComponentError(null);
		cbMaterialUOM.setReadOnly(true);
		tfMaterialQty.setValue("0");
		cbMaterialUOM.setComponentError(null);
		cbMaterialStatus.setValue(cbMaterialStatus.getItemIds().iterator().next());
		productBomDtlList = new ArrayList<ProductBomDtlDM>();
		tblBomDtl.removeAllItems();
	}
	
	private void deleteDetails() {
		ProductBomDtlDM save = new ProductBomDtlDM();
		if (tblBomDtl.getValue() != null) {
			save = beanProductBomDtl.getItem(tblBomDtl.getValue()).getBean();
			productBomDtlList.remove(save);
			resetBOMDetailFields();
			loadProductBomDtlRslt();
			btndelete.setEnabled(false);
		}
	}

	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
		
	}
}
