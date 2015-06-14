/**
 * File Name 		: ProductCategory.java 
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
 * 0.2           20-Jun-2014         Ganga              Code Optimizing&code re-factoring
 */
package com.gnts.base.mst;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.ProductCategoryDM;
import com.gnts.base.domain.mst.ProductCategoryListDM;
import com.gnts.base.service.mst.ProductCategoryService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ProductCategory extends BaseUI {
	private ProductCategoryService ServiceProductCategory = (ProductCategoryService) SpringContextHelper
			.getBean("ProductCategory");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlimage = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfProdCtgryName;
	private TextField tfPrntCtgryName, tfShortDesc;
	private ComboBox cbProdCtgryStatus;
	private ComboBox cbPrntCtgry;
	private TextArea taProdCtgryDesc;
	// Bean container
	private BeanItemContainer<ProductCategoryListDM> beanProdCtgryListDM = null;
	// local variables declaration
	private Long companyid;
	private String cateid;
	private int recordCnt = 0;
	private int flag = 0;
	private String username;
	public static boolean filevalue1 = false;
	// for initialize logger
	private Logger logger = Logger.getLogger(ProductCategory.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public ProductCategory() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ProductCategory() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Product Category UI");
		// product category Name text field
		tfProdCtgryName = new GERPTextField("Product Category");
		tfProdCtgryName.setMaxLength(25);
		tfProdCtgryName.setId("tfProdCtgryName");
		tfProdCtgryName.setVisible(true);
		// parent category Name text field
		tfPrntCtgryName = new GERPTextField("Parent Category");
		tfPrntCtgryName.setRequired(false);
		tfPrntCtgryName.setMaxLength(25);
		tfPrntCtgryName.setId("tfPrntCtgryName");
		// Product CATEGORY Short Desc
		tfShortDesc = new GERPTextField("Short Desc");
		tfPrntCtgryName.setMaxLength(20);
		// Product category status combo box
		cbProdCtgryStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Parent category status combo box
		cbPrntCtgry = new ComboBox("Parent Category");
		cbPrntCtgry.setItemCaptionPropertyId("catename");
		loadCategoryList();
		cbPrntCtgry.setWidth("150");
		// Product category description text area
		taProdCtgryDesc = new TextArea("Category Desc");
		taProdCtgryDesc.setWidth("400");
		taProdCtgryDesc.setHeight("123");
		hlimage.setCaption("Category Image");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
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
		flColumn1.addComponent(tfProdCtgryName);
		flColumn2.addComponent(tfPrntCtgryName);
		flColumn3.addComponent(cbProdCtgryStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSizeUndefined();
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(tfProdCtgryName);
		flColumn1.addComponent(cbPrntCtgry);
		flColumn1.addComponent(tfShortDesc);
		flColumn1.addComponent(cbProdCtgryStatus);
		flColumn2.addComponent(taProdCtgryDesc);
		flColumn3.addComponent(hlimage);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Productcat Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<ProductCategoryListDM> productList = new ArrayList<ProductCategoryListDM>();
		logger.info("" + "Product Category : Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + tfProdCtgryName.getValue() + ", "
				+ tfPrntCtgryName.getValue() + (String) cbProdCtgryStatus.getValue());
		productList = ServiceProductCategory.getProdCategoryList(null, null,tfProdCtgryName.getValue(),
				(String) cbProdCtgryStatus.getValue(), tfPrntCtgryName.getValue(), "F");
		recordCnt = productList.size();
		beanProdCtgryListDM = new BeanItemContainer<ProductCategoryListDM>(ProductCategoryListDM.class);
		beanProdCtgryListDM.addAll(productList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the ParentCategory. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanProdCtgryListDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "cateid", "catename", "parentCategoryname", "catestatus",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Category", "Parent Category", "Status",
				"Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("cateid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfProdCtgryName.setValue("");
		tfProdCtgryName.setComponentError(null);
		new UploadUI(hlimage);
		tfShortDesc.setValue("");
		tfPrntCtgryName.setValue("");
		taProdCtgryDesc.setValue("");
		tfPrntCtgryName.setValue("");
		cbPrntCtgry.setValue(null);
		tfProdCtgryName.setComponentError(null);
		cbProdCtgryStatus.setValue(cbProdCtgryStatus.getItemIds().iterator().next());
		//new UploadUI(hlimage);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editProductCategory() {
		flag=1;
		hlUserInputLayout.setVisible(true);
		Item rowSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		ProductCategoryListDM prodCtgryDM = beanProdCtgryListDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		if (rowSelected != null) {
			//ProductCategoryListDM prodCtgryDM = beanProdCtgryListDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			tfProdCtgryName.setValue(rowSelected.getItemProperty("catename").getValue().toString());
			if ((rowSelected.getItemProperty("parentcateid").getValue() != null)) {
				cbPrntCtgry.setValue((Long) prodCtgryDM.getParentcateid());
			}
			String stCode = rowSelected.getItemProperty("catestatus").getValue().toString();
			cbProdCtgryStatus.setValue(stCode);
			if ((rowSelected.getItemProperty("shortdesc").getValue() != null)) {
				tfShortDesc.setValue(rowSelected.getItemProperty("shortdesc").getValue().toString());
			}
			if ((rowSelected.getItemProperty("catedesc").getValue() != null)) {
				taProdCtgryDesc.setValue(rowSelected.getItemProperty("catedesc").getValue().toString());
			}
		}
		if (prodCtgryDM.getCateimage() != null) {
			hlimage.removeAllComponents();				
			byte[] myimage = (byte[]) prodCtgryDM.getCateimage();
			UploadUI uploadObject = new UploadUI(hlimage);
			uploadObject.dispayImage(myimage,prodCtgryDM.getCatename());			
			
		} else {
			new UploadUI(hlimage);
		}
		
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
		cbProdCtgryStatus.setValue(cbProdCtgryStatus.getItemIds().iterator().next());
		tfProdCtgryName.setValue("");
		tfPrntCtgryName.setValue("");
		cbPrntCtgry.setValue(null);
		taProdCtgryDesc.setValue("");
		tfShortDesc.setValue("");
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
		tfProdCtgryName.setRequired(true);
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
		tfProdCtgryName.setRequired(true);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		// reset the input controls to default value
		resetFields();
		assembleUserInputLayout();
		editProductCategory();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfProdCtgryName.setComponentError(null);
		if ((tfProdCtgryName.getValue() == null) || tfProdCtgryName.getValue().trim().length() == 0) {
			tfProdCtgryName.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_CATEGORY));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfProdCtgryName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Producat. ID " + cateid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_PRODUCT_CATEGORY);
		UI.getCurrent().getSession().setAttribute("audittablepk", cateid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfProdCtgryName.setRequired(false);
		resetFields();
	}
	
	public void loadCategoryList() {
		List<ProductCategoryListDM> categorylist = ServiceProductCategory.getProdCategoryList(null,null, null, null, null,
				"P");
		BeanContainer<Long, ProductCategoryListDM> beanCtgry = new BeanContainer<Long, ProductCategoryListDM>(
				ProductCategoryListDM.class);
		beanCtgry.setBeanIdProperty("cateid");
		beanCtgry.addAll(categorylist);
		cbPrntCtgry.setContainerDataSource(beanCtgry);
	}
	
	@Override
	protected void saveDetails() throws SaveException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		ProductCategoryDM productcategoryobj = new ProductCategoryDM();
		//productcategoryobj = beanProdCtgryListDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		if (tblMstScrSrchRslt.getValue() != null) {
			Item rowSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			productcategoryobj.setCateid((Long) rowSelected.getItemProperty("cateid").getValue());
		}
		productcategoryobj.setCompanyid(companyid);
		productcategoryobj.setCatename(tfProdCtgryName.getValue().toString());
		productcategoryobj.setShortdesc(tfShortDesc.getValue().toString());
		productcategoryobj.setCatedesc(taProdCtgryDesc.getValue().toString());
		productcategoryobj.setParentcateid((Long) cbPrntCtgry.getValue());
		if (cbProdCtgryStatus.getValue() != null) {
			productcategoryobj.setCatestatus((String) cbProdCtgryStatus.getValue());
		}
		if ((Boolean) UI.getCurrent().getSession().getAttribute("isFileUploaded")) {
			try {
				productcategoryobj.setCateimage((byte[]) UI.getCurrent().getSession().getAttribute("imagebyte"));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			productcategoryobj.setCateimage(null);
		}
		/*File file = new File(GERPConstants.IMAGE_PATH);
		FileInputStream fin = new FileInputStream(file);
		byte fileContent[] = new byte[(int) file.length()];
		fin.read(fileContent);
		fin.close();*/
		//productcategoryobj.setCateimage(fileContent);
		productcategoryobj.setLastupdateddt(DateUtils.getcurrentdate());
		productcategoryobj.setLastupdatedby(username);
		ServiceProductCategory.saveorUpdateCategoryDetails(productcategoryobj);
		resetFields();
		loadSrchRslt();
	}
}