package com.gnts.sms.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.sms.domain.txn.ProductStockDM;
import com.gnts.sms.service.txn.ProductStockService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;

public class ProductStock extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private ProductStockService serviceproductstock = (ProductStockService) SpringContextHelper.getBean("productstock");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	// User Input Fields for Product Stock
	private ComboBox cbbranchid, cbproductid, cbstocktype;
	private BeanItemContainer<ProductStockDM> beanproductstock = null;
	private FormLayout fl1, fl2, fl3;
	private GERPAddEditHLayout hlSearLayout;
	// Local variables declaration
	private Long companyid;
	private String username;
	private Long branchId;
	private int recordcnt = 0;
	// public Label lblNotification;
	private Long productStockId;
	// Initialize logger
	private Logger logger = Logger.getLogger(ProductStock.class);
	
	public ProductStock() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ProductStock() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Product Stock UI");
		// Initialization for product stock Details
		cbbranchid = new GERPComboBox("Branch");
		cbbranchid.setItemCaptionPropertyId("branchName");
		cbbranchid.setNullSelectionAllowed(false);
		loadBranchList();
		cbproductid = new GERPComboBox("Product Name");
		cbproductid.setItemCaptionPropertyId("prodname");
		loadProduct();
		cbstocktype = new GERPComboBox("Stock Type");
		cbstocktype.addItem("new");
		cbstocktype.addItem("scrap");
		cbstocktype.addItem("Refurbish");
		hlSearLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearLayout.removeAllComponents();
		btnAdd.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnEdit.setVisible(false);
		fl1 = new FormLayout();
		fl2 = new FormLayout();
		fl3 = new FormLayout();
		fl1.addComponent(cbbranchid);
		fl2.addComponent(cbproductid);
		fl3.addComponent(cbstocktype);
		hlSearLayout.addComponent(fl1);
		hlSearLayout.addComponent(fl2);
		hlSearLayout.addComponent(fl3);
		hlSearLayout.setMargin(true);
		hlSearLayout.setSizeUndefined();
	}
	
	private void loadBranchList() {
		BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanbranch.setBeanIdProperty("branchId");
		beanbranch.addAll(serviceBranch.getBranchList(null, null, null, null, companyid, "P"));
		cbbranchid.setContainerDataSource(beanbranch);
	}
	
	// Load Product List
	private void loadProduct() {
		try {
			List<ProductDM> list = new ArrayList<ProductDM>();
			list.add(new ProductDM(0L, "All Products"));
			list.addAll(serviceProduct.getProductList(companyid, null, null, null, null, null, null, "P"));
			BeanContainer<Long, ProductDM> beanprod = new BeanContainer<Long, ProductDM>(ProductDM.class);
			beanprod.setBeanIdProperty("prodid");
			beanprod.addAll(list);
			cbproductid.setContainerDataSource(beanprod);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load product stock
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<ProductStockDM> listProductstock = new ArrayList<ProductStockDM>();
		listProductstock = serviceproductstock.getProductStockList((Long) cbproductid.getValue(),
				(String) cbstocktype.getValue(), productStockId, (Long) cbbranchid.getValue(), "F");
		recordcnt = listProductstock.size();
		beanproductstock = new BeanItemContainer<ProductStockDM>(ProductStockDM.class);
		beanproductstock.addAll(listProductstock);
		tblMstScrSrchRslt.setContainerDataSource(beanproductstock);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "productStockId", "branchName", "prodname", "stockType",
				"currentStock", "parkedStock", "effectiveStock", "uom", "lastUpdateddt", "lastUpdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch", "Product", "Stock Type", "Current Stock",
				"Parked Stock", "Effective stock", "UOM", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("productStockId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordcnt);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		try {
			loadSrchRslt();
			if (recordcnt == 0) {
				logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
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
		cbbranchid.setValue(branchId);
		cbproductid.setValue(0L);
		cbstocktype.setValue(null);
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
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
	}
	
	@Override
	protected void resetFields() {
		cbbranchid.setValue(branchId);
		cbproductid.setValue(0L);
		cbstocktype.setValue(null);
	}
}
