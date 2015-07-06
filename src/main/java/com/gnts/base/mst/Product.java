/**
 * File Name 		: Product.java 
 * Description 		: this class is used for add/edit product category details. 
 * Author 			: Hema
 * Date 			: March 03, 2014
 * Modification 	:
 * Modified By 		: Hema 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 04 2014         Hema		          Intial Version
 * 0.2           3-Jun-2014         Ganga              Code Optimizing&code re-factoring
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.CurrencyDM;
import com.gnts.base.domain.mst.ProductCategoryListDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.ProductSpecificationDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.CurrencyService;
import com.gnts.base.service.mst.ProductCategoryService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.ProductSpecificationService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.components.GERPTokenField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.gcat.domain.mst.ProductColorDM;
import com.gnts.gcat.domain.mst.ProductGalleryDM;
import com.gnts.gcat.domain.txn.TagsDM;
import com.gnts.gcat.mst.ProductGallery;
import com.gnts.gcat.service.txn.TagsService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class Product extends BaseUI {
	private ProductService ServiceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private CurrencyService ServiceCurrency = (CurrencyService) SpringContextHelper.getBean("currency");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private ProductCategoryService ServiceProdCtgry = (ProductCategoryService) SpringContextHelper
			.getBean("ProductCategory");
	private TagsService ServiceTag = (TagsService) SpringContextHelper.getBean("tags");
	private ProductSpecificationService ServiceProdSpec = (ProductSpecificationService) SpringContextHelper
			.getBean("prodspec");
	ProductGallery productcolorGlry;
	List<ProductGalleryDM> galleryList;
	List<ProductColorDM> colorList;
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlProdCtgryImg = new HorizontalLayout();
	private VerticalLayout hlprodDoc = new VerticalLayout();
	// private HorizontalLayout hl
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private HorizontalLayout clrGlry = new HorizontalLayout();
	// User Input Components
	private TextField tfProdName, tfprice, tfcode, tfprodcode;
	private ComboBox cbprntProdct, cbprodCtgry, cbstatus, cbcurrency, cbbrand, cbbranchname, cbuom;
	private TextArea taprodDesc, tadescription, tasrtDesc;
	private CheckBox cbView, cbVisualizer;
	public Button btnaddSpec = new GERPButton("Add", "addbt", this);
	private Table tblspec = new Table();
	private GERPTokenField totag;
	// Bean container
	private BeanItemContainer<ProductDM> beanProductDM = null;
	private BeanItemContainer<ProductSpecificationDM> beanProdSpecDM = null;
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	List<ProductSpecificationDM> specList = null;
	// local variables declaration
	private Long prodId;
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	public static boolean filevalue1 = false;
	// for initialize logger
	private Logger logger = Logger.getLogger(Product.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Product() {
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
		// product category Name text field
		tfProdName = new GERPTextField("Product Name");
		tfProdName.setMaxLength(25);
		// Product category combo box
		cbprodCtgry = new ComboBox("Product Category");
		cbprodCtgry.setItemCaptionPropertyId("catename");
		loadCategoryaddList();
		cbprodCtgry.setWidth("150");
		// Parent category combo box
		cbprntProdct = new ComboBox("Parent Product");
		cbprntProdct.setItemCaptionPropertyId("prodname");
		loadParentProdList();
		cbprntProdct.setWidth("150");
		// Product Status combo box
		cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// TextArea Product Description
		taprodDesc = new GERPTextArea("Product Desc");
		// Text area product specification
		tadescription = new TextArea("Description");
		tadescription.setHeight("25");
		tadescription.setWidth("165");
		// Price text field
		tfprice = new TextField();
		tfprice.setWidth("75");
		// Currency Combo box
		cbcurrency = new ComboBox();
		cbcurrency.setItemCaptionPropertyId("ccyname");
		loadCurrencyaddList();
		cbcurrency.setWidth("77");
		cbcurrency.setHeight("18px");
		// text field unit of measurement
		cbuom = new ComboBox("UOM");
		loadUomList();
		cbuom.setWidth("150");
		cbuom.setItemCaptionPropertyId("lookupname");
		// Code text Field
		tfcode = new GERPTextField("Code");
		tfcode.setWidth("100");
		// View Check Box
		cbView = new CheckBox("View360");
		cbVisualizer = new CheckBox("Visualizer");
		// Comobox for Brand Name
		cbbrand = new ComboBox("Brand Name");
		loadBrandList();
		cbbrand.setWidth("150");
		cbbrand.setItemCaptionPropertyId("lookupname");
		// text field for Product Code
		tfprodcode = new GERPTextField("Product Code");
		// Combobox for Branch
		cbbranchname = new ComboBox("Branch Name");
		cbbranchname.setWidth("150");
		loadBranchList();
		cbbranchname.setItemCaptionPropertyId("branchName");
		// Text area for Short Desc
		tasrtDesc = new TextArea("Short Desc");
		tasrtDesc.setHeight("30");
		tasrtDesc.setWidth("150");
		hlprodDoc.setCaption("");
		hlprodDoc.setMargin(true);
		btnaddSpec.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (validateDtl()) {
						savespecDetails();
						// new GERPSaveNotification();
					}
				}
				catch (Exception e) {
					try {
						throw new ERPException.SaveException();
					}
					catch (SaveException e1) {
						logger.error("Company ID : "
								+ UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
								+ " | User Name : "
								+ UI.getCurrent().getSession().getAttribute("loginUserName").toString() + " > "
								+ "Exception " + e1.getMessage());
						e1.printStackTrace();
					}
				}
			}
		});
		// ClickListener for ProdSpec Tale
		tblspec.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblspec.isSelected(event.getItemId())) {
					tblspec.setImmediate(true);
					btnaddSpec.setCaption("Add");
					btnaddSpec.setStyleName("savebt");
					specResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddSpec.setCaption("Update");
					btnaddSpec.setStyleName("savebt");
					editProductspec();
				}
			}
		});
		// Tag components
		Label lbl = new Label();
		lbl.setHeight("60px");
		totag = new GERPTokenField("");
		totag.setWidth("300");
		totag.setHeight("140");
		totag.setTokenCaptionPropertyId("tagdesc");
		loadTagDescList();
		VerticalLayout vltag = new VerticalLayout();
		vltag.addComponent(lbl);
		vltag.addComponent(totag);
		vltag.setSpacing(true);
		HorizontalLayout hltags = new HorizontalLayout();
		hltags.addComponent(vltag);
		hltags.setSpacing(true);
		hltags.setMargin(true);
		hlUserInputLayout.addComponent(GERPPanelGenerator.createPanel(hltags));
		VerticalLayout img = new VerticalLayout();
		new UploadDocumentUI(hlprodDoc);
		img.addComponent(hlprodDoc);
		img.setSpacing(true);
		img.setMargin(true);
		img.setSizeFull();
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadSrchspecRslt();
		btnaddSpec.setStyleName("add");
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
		flColumn1.addComponent(tfProdName);
		// flColumn2.addComponent(cbprodCtgry);
		flColumn3.addComponent(cbstatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSizeUndefined();
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Add Price and Currency in horizontal layout
		HorizontalLayout hlpricecurency = new HorizontalLayout();
		hlpricecurency.addComponent(tfprice);
		hlpricecurency.addComponent(cbcurrency);
		hlpricecurency.setCaption("Price");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		// Formlayout1 components
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn1.addComponent(tfprodcode);
		flColumn1.addComponent(tfProdName);
		// flColumn1.addComponent(cbprodCtgry);
		// flColumn1.addComponent(cbprntProdct);
		flColumn1.addComponent(taprodDesc);
		flColumn1.addComponent(tasrtDesc);
		// flColumn1.addComponent(cbbrand);
		// flColumn1.addComponent(cbbranchname);
		flColumn1.addComponent(hlpricecurency);
		flColumn1.setComponentAlignment(hlpricecurency, Alignment.TOP_LEFT);
		flColumn1.addComponent(cbuom);
		flColumn1.addComponent(cbstatus);
		HorizontalLayout hlviewviz = new HorizontalLayout();
		hlviewviz.addComponent(cbView);
		hlviewviz.addComponent(cbVisualizer);
		hlviewviz.setCaption("Other Attributes");
		flColumn1.addComponent(hlviewviz);
		flColumn1.setComponentAlignment(hlviewviz, Alignment.TOP_LEFT);
		// Formlayout 2 components
		HorizontalLayout hlcodeDesc = new HorizontalLayout();
		hlcodeDesc.addComponent(tfcode);
		hlcodeDesc.addComponent(tadescription);
		VerticalLayout vl = new VerticalLayout();
		vl.addComponent(cbstatus);
		vl.setSpacing(true);
		vl.addComponent(btnaddSpec);
		hlcodeDesc.addComponent(vl);
		hlcodeDesc.setSpacing(true);
		flColumn2.addComponent(hlcodeDesc);
		flColumn2.addComponent(tblspec);
		// Formlayout3 components
		HorizontalLayout hlimgprod = new HorizontalLayout();
		hlimgprod.addComponent(hlProdCtgryImg);
		hlimgprod.addComponent(hlprodDoc);
		hlimgprod.setComponentAlignment(hlprodDoc, Alignment.BOTTOM_RIGHT);
		Label lblimage = new Label("Product Image and Document ");
		lblimage.setStyleName("h4");
		VerticalLayout vlimageDoc = new VerticalLayout();
		vlimageDoc.addComponent(lblimage);
		vlimageDoc.addComponent(GERPPanelGenerator.createPanel(hlimgprod));
		flColumn3.addComponent(vlimageDoc);
		Label lblkey = new Label("Keyword");
		lblkey.setStyleName("h4");
		VerticalLayout vlkey = new VerticalLayout();
		vlkey.addComponent(lblkey);
		vlkey.addComponent(GERPPanelGenerator.createPanel(totag));
		vlkey.setSpacing(true);
		flColumn3.addComponent(vlkey);
		HorizontalLayout hlproductDetailTab = new HorizontalLayout();
		VerticalLayout vlColumn1 = new VerticalLayout();
		Label lblpro = new Label("Product Details");
		lblpro.setStyleName("h4");
		vlColumn1.addComponent(lblpro);
		vlColumn1.addComponent(GERPPanelGenerator.createPanel(flColumn1));
		hlproductDetailTab.addComponent(vlColumn1);
		Label lblspec = new Label("Product Specification");
		lblspec.setStyleName("h4");
		VerticalLayout vlColumn2 = new VerticalLayout();
		vlColumn2.addComponent(lblspec);
		vlColumn2.addComponent(GERPPanelGenerator.createPanel(flColumn2));
		hlproductDetailTab.addComponent(vlColumn2);
		hlproductDetailTab.addComponent(flColumn3);
		hlproductDetailTab.setSpacing(true);
		hlproductDetailTab.setMargin(true);
		flColumn1.setMargin(true);
		clrGlry.removeAllComponents();
		productcolorGlry = new ProductGallery(clrGlry, prodId);
		TabSheet tabsheet = new TabSheet();
		tabsheet.setHeight("495");
		tabsheet.setWidth("1200");
		tabsheet.setWidth("100%");
		tabsheet.addTab(hlproductDetailTab, "Product Details");
		tabsheet.addTab(clrGlry, "Product Gallery");
		tabsheet.setSizeFull();
		hlUserInputLayout.addComponent(tabsheet);
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setHeight("495%");
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeFull();
		hlUserInputLayout.setMargin(false);
	}
	
	public void loadSrchRslt() {
		logger.info("Product Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<ProductDM> productList = new ArrayList<ProductDM>();
		logger.info("" + "Product Category : Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + tfProdName.getValue() + ", " + tfProdName.getValue()
				+ (String) cbstatus.getValue());
		productList = ServiceProduct.getProductList(companyid, null, null, tfProdName.getValue().toString(),
				(String) cbstatus.getValue(), null, null, "F");
		recordCnt = productList.size();
		beanProductDM = new BeanItemContainer<ProductDM>(ProductDM.class);
		beanProductDM.addAll(productList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the ParentCategory. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanProductDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "prodid", "prodname", "cateName", "brandname", "prodstatus",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Product", "Category", "Brand", "Status",
				"Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("prodid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	public void loadSrchspecRslt() {
		logger.info("Product Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblspec.removeAllItems();
		tblspec.setWidth("400");
		tblspec.setPageLength((int) 10.5);
		tblspec.setStyleName(Runo.TABLE_SMALL);
		logger.info("" + "Product Category : Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + (String) cbstatus.getValue());
		// specList.addAll(ServiceProdSpec.getSpecList(prodId, null, "Active"));
		recordCnt = specList.size();
		beanProdSpecDM = new BeanItemContainer<ProductSpecificationDM>(ProductSpecificationDM.class);
		beanProdSpecDM.addAll(specList);
		tblspec.setPageLength(5);
		tblspec.setFooterVisible(true);
		tblspec.setContainerDataSource(beanProdSpecDM);
		tblspec.setVisibleColumns(new Object[] { "speccode", "specdesc", "specstatus" });
		tblspec.setColumnHeaders(new String[] { "Code", "Description", "Status" });
		tblspec.setColumnFooter("specstatus", "No.of Records : " + recordCnt);
	}
	
	// load the BranchList
	public void loadBranchList() {
		try {
			List<BranchDM> branchList = serviceBranch.getBranchList(null, null, null, "Active", companyid, "P");
			branchList.add(new BranchDM(0L, "All Branches"));
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(branchList);
			cbbranchname.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// load the BrandList
	public void loadBrandList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Brand Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"BS_PRDBRAND");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbbrand.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Uom
	public void loadUomList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"BS_PRODUOM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbuom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// load the Currency details for add
	public void loadCurrencyaddList() {
		try {
			List<CurrencyDM> getCurrencylist = ServiceCurrency.getCurrencyList(null, null, null, "Active", "P");
			getCurrencylist.add(new CurrencyDM(0L, "All Currency", null));
			BeanContainer<Long, CurrencyDM> BeanCurrency = new BeanContainer<Long, CurrencyDM>(CurrencyDM.class);
			BeanCurrency.setBeanIdProperty("ccyid");
			BeanCurrency.addAll(getCurrencylist);
			cbcurrency.setContainerDataSource(BeanCurrency);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	// load the ParentProduct details for add
	public void loadParentProdList() {
		try {
			List<ProductDM> getPrntProdlist = ServiceProduct.getProductList(null, null, null, null, "Active", null,
					null, "P");
			BeanContainer<Long, ProductDM> BeanProduct = new BeanContainer<Long, ProductDM>(ProductDM.class);
			BeanProduct.setBeanIdProperty("prodid");
			BeanProduct.addAll(getPrntProdlist);
			cbprntProdct.setContainerDataSource(BeanProduct);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	// load the Category details for add
	public void loadCategoryaddList() {
		try {
			List<ProductCategoryListDM> getProdCtgrylist = ServiceProdCtgry.getProdCategoryList(null, null, null,
					"Active", null, "P");
			BeanContainer<Long, ProductCategoryListDM> beanCtgry = new BeanContainer<Long, ProductCategoryListDM>(
					ProductCategoryListDM.class);
			beanCtgry.setBeanIdProperty("cateid");
			beanCtgry.addAll(getProdCtgrylist);
			cbprodCtgry.setContainerDataSource(beanCtgry);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	// load the TagDesc details for add
	public void loadTagDescList() {
		try {
			List<TagsDM> getProdCtgrylist = ServiceTag.getTagsList(null, null, "Active");
			getProdCtgrylist.add(new TagsDM());
			BeanContainer<Long, TagsDM> TagsDM = new BeanContainer<Long, TagsDM>(TagsDM.class);
			TagsDM.setBeanIdProperty("tagsid");
			TagsDM.addAll(getProdCtgrylist);
			totag.setContainerDataSource(TagsDM);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editProduct() {
		hlUserInputLayout.setVisible(true);
		Item rowSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (rowSelected != null) {
			ProductDM editproductList = beanProductDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			prodId = editproductList.getProdid();
			if ((rowSelected.getItemProperty("prodname").getValue() != null)) {
				tfProdName.setValue(editproductList.getProdname().toString());
			}
			/*
			 * if ((rowSelected.getItemProperty("parentprodid").getValue() != null)) {
			 * cbprntProdct.setValue(editproductList.getParentprodid()); }
			 */
			if ((rowSelected.getItemProperty("productcode").getValue() != null)) {
				tfprodcode.setValue(editproductList.getProductcode().toString());
			}
			/*
			 * if ((rowSelected.getItemProperty("cateName").getValue() != null)) {
			 * cbprodCtgry.setValue(editproductList.getCateid()); }
			 */
			if ((rowSelected.getItemProperty("proddesc").getValue() != null)) {
				taprodDesc.setValue(editproductList.getProddesc());
			}
			if ((rowSelected.getItemProperty("price").getValue() != null)) {
				tfprice.setValue(editproductList.getPrice().toString());
			}
			if ((rowSelected.getItemProperty("shortdesc").getValue() != null)) {
				tasrtDesc.setValue(editproductList.getShortdesc().toString());
			}
			if ((rowSelected.getItemProperty("uom").getValue() != null)) {
				cbuom.setValue(editproductList.getUom().toString());
			}
			/*
			 * if ((rowSelected.getItemProperty("brandname").getValue() != null)) {
			 * cbbrand.setValue(editproductList.getBrandname()); }
			 */
			/*
			 * if ((rowSelected.getItemProperty("branchid").getValue() != null)) {
			 * cbbranchname.setValue(editproductList.getBranchid()); }
			 */
			if ((rowSelected.getItemProperty("ccyName").getValue() != null)) {
				cbcurrency.setValue(editproductList.getCcyid());
			}
			/*
			 * if ((rowSelected.getItemProperty("brandname").getValue() != null)) {
			 * cbbrand.setValue(editproductList.getBrandname()); }
			 */
			if (("prodstatus") != null) {
				String stCode = rowSelected.getItemProperty("prodstatus").getValue().toString();
				cbstatus.setValue(stCode);
			}
			if ((rowSelected.getItemProperty("proddesc").getValue() != null)) {
				taprodDesc.setValue(rowSelected.getItemProperty("proddesc").getValue().toString());
			}
			if (editproductList.getView360yn().equals("Y")) {
				cbView.setValue(true);
			} else {
				cbView.setValue(false);
			}
			if (editproductList.getVisualizeryn().equals("Y")) {
				cbVisualizer.setValue(true);
			} else {
				cbVisualizer.setValue(false);
			}
			if (editproductList.getProdimg() != null) {
				hlProdCtgryImg.removeAllComponents();
				byte[] myimage = (byte[]) editproductList.getProdimg();
				UploadUI uploadObject = new UploadUI(hlProdCtgryImg);
				uploadObject.dispayImage(myimage, editproductList.getProdname());
			} else {
				new UploadUI(hlProdCtgryImg);
			}
			if (editproductList.getProddoc() != null) {
				byte[] certificate = (byte[]) editproductList.getProddoc();
				UploadDocumentUI test = new UploadDocumentUI(hlprodDoc);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(hlprodDoc);
			}
			productcolorGlry.loadSrchClrRslt(true, prodId);
			productcolorGlry.loadSrchGlryRslt(true, prodId);
			specList.addAll(ServiceProdSpec.getSpecList(prodId, null, "Active"));
		}
		loadSrchspecRslt();
	}
	
	private void editProductspec() {
		hlUserInputLayout.setVisible(true);
		Item specrowSelected = tblspec.getItem(tblspec.getValue());
		if (specrowSelected != null) {
			// ProductSpecificationDM editspec = beanProdSpecDM.getItem(tblspec.getValue()).getBean();
			tfcode.setValue((String) specrowSelected.getItemProperty("speccode").getValue());
			if ((specrowSelected.getItemProperty("specdesc").getValue() != null)) {
				tadescription.setValue(specrowSelected.getItemProperty("specdesc").getValue().toString());
			}
			String stCode = specrowSelected.getItemProperty("specstatus").getValue().toString();
			cbstatus.setValue(stCode);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfProdName.setValue("");
		cbbrand.setValue(null);
		cbbranchname.setValue(null);
		tfprodcode.setValue("");
		tasrtDesc.setValue("");
		taprodDesc.setValue("");
		tfprice.setValue("0");
		cbcurrency.setValue(null);
		cbuom.setValue(null);
		cbView.setValue(false);
		cbVisualizer.setValue(false);
		tadescription.setValue("");
		tfProdName.setComponentError(null);
		tfcode.setComponentError(null);
		cbbrand.setComponentError(null);
		cbbranchname.setComponentError(null);
		cbcurrency.setComponentError(null);
		cbuom.setComponentError(null);
		tfprice.setComponentError(null);
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		new UploadUI(hlProdCtgryImg);
		new UploadDocumentUI(hlprodDoc);
		specList = new ArrayList<ProductSpecificationDM>();
		tblspec.removeAllItems();
		tfcode.setValue("");
		totag.setValue(null);
		UI.getCurrent().getSession().setAttribute("isFileUploaded", false);
	}
	
	protected void specResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfcode.setValue("");
		tfcode.setComponentError(null);
		tadescription.setValue("");
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		loadSrchspecRslt();
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		hlUserInputLayout.removeAllComponents();
		// reset the field valued to default
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		tfProdName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		// new ProductGallery(clrGlry,prodId);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfProdName.setRequired(true);
		cbprodCtgry.setRequired(true);
		tfcode.setRequired(true);
		cbbrand.setRequired(true);
		cbbranchname.setRequired(true);
		cbprntProdct.setRequired(true);
		hlUserInputLayout.setSpacing(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		new UploadDocumentUI(hlprodDoc);
		new UploadUI(hlProdCtgryImg);
		assembleUserInputLayout();
		loadSrchspecRslt();
		specResetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfProdName.setRequired(true);
		cbprodCtgry.setRequired(true);
		tfcode.setRequired(true);
		cbbrand.setRequired(true);
		cbbranchname.setRequired(true);
		cbprntProdct.setRequired(true);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		assembleUserInputLayout();
		resetFields();
		editProduct();
		editProductspec();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfProdName.setComponentError(null);
		cbprodCtgry.setComponentError(null);
		tfcode.setComponentError(null);
		cbbrand.setComponentError(null);
		cbbranchname.setComponentError(null);
		cbprntProdct.setComponentError(null);
		Boolean errorFlag = false;
		if ((tfProdName.getValue() == null) || tfProdName.getValue().trim().length() == 0) {
			tfProdName.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfProdName.getValue());
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validateDtl() {
		boolean validDtl = true;
		if ((tfcode.getValue() == null) || tfcode.getValue().trim().length() == 0) {
			tfcode.setComponentError(new UserError("Enter Code"));
			validDtl = false;
		}
		return validDtl;
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfProdName.setRequired(false);
		cbprodCtgry.setRequired(false);
		tfcode.setRequired(false);
		cbbrand.setRequired(false);
		cbbranchname.setRequired(false);
		cbprntProdct.setRequired(false);
		cbcurrency.setRequired(false);
		resetFields();
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfProdName.setRequired(false);
		cbprodCtgry.setRequired(false);
		tfcode.setRequired(false);
		cbbrand.setRequired(false);
		cbbranchname.setRequired(false);
		cbprntProdct.setRequired(false);
		cbcurrency.setRequired(false);
		tblMstScrSrchRslt.setVisible(true);
		hlCmdBtnLayout.setVisible(true);
		resetFields();
		loadSrchRslt();
		specResetFields();
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			ProductDM productobj = new ProductDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				productobj = beanProductDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			productobj.setCompanyid(companyid);
			productobj.setProdname(tfProdName.getValue().toString());
			if (tfprice.getValue() != null) {
				productobj.setPrice(Long.valueOf(tfprice.getValue().toString()));
			}
			if (cbcurrency.getValue() != null) {
				productobj.setCcyid((Long) cbcurrency.getValue());
			}
			if (cbuom.getValue() != null) {
				productobj.setUom(cbuom.getValue().toString());
			}
			if (tfprodcode.getValue() != "") {
				productobj.setProductcode(tfprodcode.getValue().toString());
			}
			if (taprodDesc.getValue() != "") {
				productobj.setProddesc(taprodDesc.getValue().toString());
			}
			if (tasrtDesc.getValue() != "") {
				productobj.setShortdesc(tasrtDesc.getValue().toString());
			}
			if (cbstatus.getValue() != null) {
				productobj.setProdstatus((String) cbstatus.getValue());
			}
			if (cbView.getValue().equals(true)) {
				productobj.setView360yn("Y");
			} else if (cbView.getValue().equals(false)) {
				productobj.setView360yn("N");
			}
			if (cbVisualizer.getValue().equals(true)) {
				productobj.setVisualizeryn("Y");
			} else if (cbVisualizer.getValue().equals(false)) {
				productobj.setVisualizeryn("N");
			}
			if ((Boolean) UI.getCurrent().getSession().getAttribute("isFileUploaded")) {
				try {
					productobj.setProdimg((byte[]) UI.getCurrent().getSession().getAttribute("imagebyte"));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				productobj.setProdimg(null);
			}
			if ((Boolean) UI.getCurrent().getSession().getAttribute("isFileUploaded")) {
				try {
					productobj.setProddoc((byte[]) UI.getCurrent().getSession().getAttribute("docbyte"));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				productobj.setProddoc(null);
			}
			productobj.setLastupdateddt(DateUtils.getcurrentdate());
			productobj.setLastupdatedby(username);
			ServiceProduct.saveorUpdateProductDetails(productobj);
			@SuppressWarnings("unchecked")
			Collection<ProductSpecificationDM> itemIds = (Collection<ProductSpecificationDM>) tblspec
					.getVisibleItemIds();
			for (ProductSpecificationDM save : (Collection<ProductSpecificationDM>) itemIds) {
				save.setProdid(productobj);
				ServiceProdSpec.saveorUpdateProdSpecDetails(save);
			}
			productcolorGlry.saveDetails(productobj.getProdid());
			specResetFields();
			resetFields();
			loadSrchRslt();
			prodId = 0L;
			loadSrchspecRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void savespecDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			ProductSpecificationDM productspecobj = new ProductSpecificationDM();
			if (tblspec.getValue() != null) {
				productspecobj = beanProdSpecDM.getItem(tblspec.getValue()).getBean();
			}
			productspecobj.setSpeccode(tfcode.getValue());
			productspecobj.setSpecdesc(tadescription.getValue().toString());
			if (cbstatus.getValue() != null) {
				productspecobj.setSpecstatus((String) cbstatus.getValue());
			}
			productspecobj.setLastupdateddt(DateUtils.getcurrentdate());
			productspecobj.setLastupdatedby(username);
			specList.add(productspecobj);
			loadSrchspecRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		specResetFields();
	}
}