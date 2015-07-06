/**
 * File Name	:	ProductRPT.java
 * Description	:	entity class for SN_BASE_PRODREP table
 * Author		:	JOEL GLINDAN D
 * Date			:	JULY 11 , 2014
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of  GNTS Technologies pvt. ltd.
 * Version       Date           	Modified By              Remarks
 * 0.1           JULY 15 , 2014     JOEL GLINDAN D		     Initial Version
 */
package com.gnts.base.rpt;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.LookupDM;
import com.gnts.base.domain.mst.ProductCategoryListDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.ProductSpecificationDM;
import com.gnts.base.mst.Product;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.LookupService;
import com.gnts.base.service.mst.ProductCategoryService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ProductRPT extends BaseUI {
	private ProductService ServiceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private LookupService serviceLookup = (LookupService) SpringContextHelper.getBean("lookup");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ProductCategoryService ServiceProdCtgry = (ProductCategoryService) SpringContextHelper
			.getBean("ProductCategory");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfProdName;
	private ComboBox cbprntProdct, cbprodCtgry, cbstatus, cbbrand, cbbranchname;
	public Button btnaddSpec = new GERPButton("Add", "addbt", this);
	// Bean container
	private BeanItemContainer<ProductDM> beanProductDM = null;
	List<ProductSpecificationDM> specList = null;
	// local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	public static boolean filevalue1 = false;
	private Button btnPrint;
	// for initialize logger
	private Logger logger = Logger.getLogger(Product.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public ProductRPT() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Product() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Product UI");
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnDownload.setVisible(false);
		btnPrint = new Button("Print");
		btnPrint.addStyleName("print");
		btnPrint.setEnabled(true);
		btnPrint.setVisible(true);
		// product category Name text field
		tfProdName = new GERPTextField("Product Name");
		tfProdName.setMaxLength(25);
		// Product category combo box
		cbprodCtgry = new ComboBox("Product Category");
		cbprodCtgry.setItemCaptionPropertyId("catename");
		cbprodCtgry.setNullSelectionAllowed(false);
		loadCategoryaddList();
		cbprodCtgry.setWidth("150");
		// Parent category combo box
		cbprntProdct = new ComboBox("Parent Product");
		cbprntProdct.setItemCaptionPropertyId("prodname");
		cbprodCtgry.setNullSelectionAllowed(false);
		cbprntProdct.setWidth("150");
		loadParentProdList();
		// Product Status combo box
		cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Comobox for Brand Name
		cbbrand = new ComboBox("Brand Name");
		cbbrand.setWidth("150");
		cbbrand.setItemCaptionPropertyId("lookupdesc");
		cbbrand.setNullSelectionAllowed(false);
		loadBrandList();
		// Combobox for Branch
		cbbranchname = new ComboBox("Branch Name");
		cbbranchname.setItemCaptionPropertyId("branchName");
		cbbranchname.setNullSelectionAllowed(false);
		cbbranchname.setWidth("150");
		loadBranchList();
		hlSearchLayout = new GERPAddEditHLayout();
		hlCmdBtnLayout.setSpacing(true);
		hlCmdBtnLayout.addComponent(btnPrint);
		hlCmdBtnLayout.setComponentAlignment(btnPrint, Alignment.MIDDLE_RIGHT);
		hlCmdBtnLayout.setExpandRatio(btnPrint, 1);
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
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(cbprntProdct);
		flColumn1.addComponent(cbbranchname);
		flColumn2.addComponent(cbprodCtgry);
		flColumn2.addComponent(cbstatus);
		flColumn3.addComponent(cbbrand);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSizeUndefined();
	}
	
	public void loadSrchRslt() {
		logger.info("Product Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<ProductDM> productList = new ArrayList<ProductDM>();
		Long cateId = null;
		Long prodId = null;
		Long branchId = null;
		if (cbprntProdct.getValue() != null) {
			prodId = ((Long.valueOf(cbprntProdct.getValue().toString())));
		}
		if (cbprodCtgry.getValue() != null) {
			cateId = ((Long.valueOf(cbprodCtgry.getValue().toString())));
		}
		if (cbbrand.getValue() != null) {
		}
		if (cbbranchname.getValue() != null) {
			branchId = ((Long.valueOf(cbbranchname.getValue().toString())));
		}
		logger.info("" + "Product Category : Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + tfProdName.getValue() + ", " + tfProdName.getValue()
				+ (String) cbstatus.getValue());
		productList = ServiceProduct.getProductList(companyid, prodId, branchId, tfProdName.getValue().toString(),
				(String) cbstatus.getValue(), cateId,null, "F");
		
		
		recordCnt = productList.size();
		beanProductDM = new BeanItemContainer<ProductDM>(ProductDM.class);
		beanProductDM.addAll(productList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the ParentCategory. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanProductDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "prodid", "prodname", "cateName", "brandname", "prodstatus",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Product Name", "Category Name", "Brand Name",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("prodid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	public void loadBranchList() {
		try {
			List<BranchDM> list = new ArrayList<BranchDM>();
			list.add(new BranchDM(0L, "Alll Branches"));
			list.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(list);
			cbbranchname.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadBrandList() {
		try {
			List<LookupDM> list = new ArrayList<LookupDM>();
			list.add(new LookupDM(0L, "All Brand", "All Brand"));
			list.addAll(serviceLookup.getLookupList(null, "BS_PRDBRAND", null, "Active", "D"));
			BeanContainer<String, LookupDM> beanLookUp = new BeanContainer<String, LookupDM>(LookupDM.class);
			beanLookUp.setBeanIdProperty("lookupdesc");
			beanLookUp.addAll(list);
			cbbrand.setContainerDataSource(beanLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// load the ParentProduct details for add
	public void loadParentProdList() {
		try {
			List<ProductDM> list = new ArrayList<ProductDM>();
			list.add(new ProductDM(0L, "All Products"));
			list.addAll(ServiceProduct.getProductList(companyid, null, null, null, "Active", null,null, "P"));
			BeanContainer<Long, ProductDM> BeanProduct = new BeanContainer<Long, ProductDM>(ProductDM.class);
			BeanProduct.setBeanIdProperty("prodid");
			BeanProduct.addAll(list);
			cbprntProdct.setContainerDataSource(BeanProduct);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	// load the Category details for add
	public void loadCategoryaddList() {
		try {
			List<ProductCategoryListDM> list = new ArrayList<ProductCategoryListDM>();
			list.add(new ProductCategoryListDM(0L, "All Categories"));
			list.addAll(ServiceProdCtgry.getProdCategoryList(null,null, null, "Active", null, "P"));
			BeanContainer<Long, ProductCategoryListDM> beanCtgry = new BeanContainer<Long, ProductCategoryListDM>(
					ProductCategoryListDM.class);
			beanCtgry.setBeanIdProperty("cateid");
			beanCtgry.addAll(list);
			cbprodCtgry.setContainerDataSource(beanCtgry);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbprntProdct.setValue(0L);
		cbprodCtgry.setValue(0L);
		cbbrand.setValue(cbbrand.getItemIds().iterator().next());
		cbbranchname.setValue(0L);
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		hlUserInputLayout.removeAllComponents();
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		hlUserInputLayout.removeAllComponents();
		// reset the field valued to default
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		cbprntProdct.setValue(0L);
		cbbranchname.setValue(0L);
		cbprodCtgry.setValue(0L);
		cbbrand.setValue(cbbrand.getItemIds().iterator().next());
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
	}
	
	@Override
	protected void editDetails() {
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfProdName.setComponentError(null);
		if ((tfProdName.getValue() == null) || tfProdName.getValue().trim().length() == 0) {
			tfProdName.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfProdName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
	}
	
	@Override
	protected void cancelDetails() {
	}
	
	@Override
	protected void saveDetails() {
	}
	
	protected void savespecDetails() {
	}
}
