/**
 * File Name	:	RateSettings.java
 * Description	:	Used for view T_GCAT_RATE_SETTING details
 * Author		:	Prakash.s
 * Date			:	mar 7, 2014
 * Modification 
 * Modified By  :   prakash.s
 * Description	:
 *
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies pvt. ltd.
 * Version         Date           Modified By             Remarks
 * 0.1           18-Jul-2014         Ganga              Code Optimizing&code re-factoring
 * */
package com.gnts.gcat.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.ProductService;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.gcat.domain.txn.RateSettingsDM;
import com.gnts.gcat.service.txn.RateSettingService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class RateSettings extends BaseUI {
	private RateSettingService serviceRateSettings = (RateSettingService) SpringContextHelper.getBean("rateSettings");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfRatechanging, tfClientname, tfClientnamesear;
	private ComboBox cbProduct, cbProductsear;
	private PopupDateField dfChangedate;
	// Bean Container
	private BeanItemContainer<RateSettingsDM> beanRateSettingsDM = null;
	// Local variables
	private Long companyid;
	private String username;
	private int recordCnt = 0;
	private Long userId;
	// Initialize logger
	private Logger logger = Logger.getLogger(RateSettings.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public RateSettings() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		userId = Long.valueOf(UI.getCurrent().getSession().getAttribute("userId").toString());
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
		tfClientname = new TextField("Client Name");
		tfClientname.setReadOnly(false);
		// RAte Changing text box
		tfRatechanging = new TextField("Rate Changing(%)");
		tfRatechanging.setReadOnly(false);
		// product combo box
		cbProduct = new ComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		loadProdList();
		cbProduct.setReadOnly(false);
		// productcombo box for search
		cbProductsear = new ComboBox("Product Name");
		cbProductsear.setItemCaptionPropertyId("prodname");
		loadProductList();
		cbProductsear.setReadOnly(false);
		// client TExt for Search
		tfClientnamesear = new TextField("Client Name");
		tfClientnamesear.setReadOnly(false);
		// Change start date
		dfChangedate = new GERPPopupDateField("Change Date");
		dfChangedate.setInputPrompt("Select Date");
		dfChangedate.setReadOnly(false);
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
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn1.addComponent(cbProductsear);
		flColumn2.addComponent(tfClientnamesear);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
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
		flColumn1.addComponent(cbProduct);
		flColumn2.addComponent(tfRatechanging);
		flColumn3.addComponent(dfChangedate);
		flColumn4.addComponent(tfClientname);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void viewLogger() {
		Item rowSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (rowSelected != null) {
			RateSettingsDM enqdtl = beanRateSettingsDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			setReadOnlyFalseFields();
			cbProduct.setReadOnly(false);
			cbProduct.setValue(enqdtl.getProductId());
			cbProduct.setReadOnly(true);
			tfRatechanging.setValue(enqdtl.getRateChng().toString());
			dfChangedate.setValue(enqdtl.getSetDate());
			tfClientname.setValue(enqdtl.getClintname());
			setReadOnlyTrueFields();
		}
	}
	
	public void setReadOnlyFalseFields() {
		cbProduct.setReadOnly(false);
		tfRatechanging.setReadOnly(false);
		dfChangedate.setReadOnly(false);
		tfClientname.setReadOnly(false);
	}
	
	public void setReadOnlyTrueFields() {
		cbProduct.setReadOnly(true);
		tfRatechanging.setReadOnly(true);
		dfChangedate.setReadOnly(true);
		tfClientname.setReadOnly(true);
	}
	
	// get the search result from DB based on the search Ratesettings
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		Long prodname = null;
		if (cbProductsear.getValue() != null) {
			prodname = (Long.valueOf(cbProductsear.getValue().toString()));
		}
		String clientname = tfClientnamesear.getValue().toString();
		List<RateSettingsDM> rateSettingList = new ArrayList<RateSettingsDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Ratesettings are "
				+ companyid + ", " + tfClientnamesear.getValue() + ", " + cbProduct.getValue());
		rateSettingList = serviceRateSettings.getRatingList(companyid, null, prodname,clientname );
		recordCnt = rateSettingList.size();
		beanRateSettingsDM = new BeanItemContainer<RateSettingsDM>(RateSettingsDM.class);
		beanRateSettingsDM.addAll(rateSettingList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Ratesetting. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanRateSettingsDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "rateSettingId", "prodName","clintname", "settingDate", "rateChng",
				"isCurrent"});
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Product Name", "Client Name","Setting Date", "Rate Changing(%)",
				"Is Current"});
		tblMstScrSrchRslt.setColumnAlignment("rateSettingId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("rateChng", "No.of Records : " + recordCnt);
	}
	
	private void loadProductList() {
		try {
			List<ProductDM> productlist = serviceProduct.getProductList(null, null, null, null, "Active", null,null, "p");
			BeanContainer<Long, ProductDM> prodList = new BeanContainer<Long, ProductDM>(ProductDM.class);
			prodList.setBeanIdProperty("prodid");
			prodList.addAll(productlist);
			cbProductsear.setContainerDataSource(prodList);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("fn_loadProductList_Exception Caught->" + e);
		}
	}
	
	private void loadProdList() {
		try {
			List<ProductDM> productlist = serviceProduct.getProductList(null, null, null, null, "Active", null,null, "p");
			BeanContainer<Long, ProductDM> prodList = new BeanContainer<Long, ProductDM>(ProductDM.class);
			prodList.setBeanIdProperty("prodid");
			prodList.addAll(productlist);
			cbProduct.setContainerDataSource(prodList);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("fn_loadProductList_Exception Caught->" + e);
		}
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
		// reset the field valued to default
		tfClientnamesear.setValue("");
		cbProductsear.setValue(null);
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
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
	}
	
	@Override
	protected void showAuditDetails() {
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
		tfClientname.setValue("");
		tfRatechanging.setValue("");
		dfChangedate.setValue(null);
		tfClientname.setValue("");
		cbProduct.setValue(null);
		cbProductsear.setValue(null);
		setReadOnlyTrueFields();
	}
}
