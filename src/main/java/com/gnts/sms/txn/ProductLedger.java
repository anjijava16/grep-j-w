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
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.sms.domain.txn.ProductLedgerDM;
import com.gnts.sms.service.txn.ProductLedgerService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class ProductLedger extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProductLedgerService serviceProductLedger = (ProductLedgerService) SpringContextHelper
			.getBean("productledger");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	// form layout for input controls
	private FormLayout fl1, fl2, fl3, fl4;
	// User Input Fields for Product Ledger
	private ComboBox cbBranch, cbProduct, cbStockType;
	private TextField tfOpenqty, tfInOutFlag, tfInOutQty, tfCloseQty, tfRefno, tfIsLatest, tfRemarks;
	private PopupDateField dfProdLedgDt, dfRefdate;
	private BeanItemContainer<ProductLedgerDM> beanProdLedger = null;
	// Search control layout
	private GERPAddEditHLayout hlSearLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	// Local variables declaration
	private Long companyid;
	private String username;
	private Long branchId;
	private int recordcnt = 0;
	private Long productledgeId;
	// Initialize logger
	private Logger logger = Logger.getLogger(ProductStock.class);
	
	public ProductLedger() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ProductLedger() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("150");
		cbBranch.setNullSelectionAllowed(false);
		loadBranchlist();
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		cbProduct.setWidth("150");
		loadProduct();
		cbStockType = new GERPComboBox("Stock Type");
		cbStockType.addItem("new");
		cbStockType.addItem("scrap");
		cbStockType.addItem("Refurbish");
		cbStockType.setWidth("150");
		tfOpenqty = new GERPTextField("Open Quantity");
		tfOpenqty.setWidth("150");
		tfInOutFlag = new GERPTextField("Inout Falg");
		tfInOutFlag.setWidth("150");
		tfInOutQty = new GERPTextField("Inout Quantity");
		tfInOutQty.setWidth("150");
		tfCloseQty = new GERPTextField("Close Quantity");
		tfCloseQty.setWidth("150");
		tfRefno = new GERPTextField("Reference No");
		tfRefno.setWidth("150");
		dfProdLedgDt = new GERPPopupDateField("Product Ledger Date");
		dfProdLedgDt.setWidth("130");
		dfRefdate = new GERPPopupDateField("Reference Date");
		dfRefdate.setWidth("130");
		tfIsLatest = new GERPTextField("Latest");
		tfIsLatest.setWidth("150");
		tfRemarks = new GERPTextField("Reference Remark");
		tfRemarks.setWidth("150");
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
		btnEdit.setCaption("View");
		btnEdit.setStyleName("view");
		fl1 = new FormLayout();
		fl2 = new FormLayout();
		fl3 = new FormLayout();
		fl4 = new FormLayout();
		fl1.addComponent(cbBranch);
		fl2.addComponent(cbProduct);
		fl3.addComponent(dfProdLedgDt);
		fl3.setMargin(true);
		fl4.addComponent(cbStockType);
		hlSearLayout.addComponent(fl1);
		hlSearLayout.addComponent(fl2);
		hlSearLayout.addComponent(fl3);
		hlSearLayout.addComponent(fl4);
		hlSearLayout.setMargin(true);
		hlSearLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Assembling User search layout");
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		btnSave.setVisible(false);
		// Removing components from search layout and re-initializing form layouts
		hlSearLayout.removeAllComponents();
		fl1 = new FormLayout();
		fl2 = new FormLayout();
		fl3 = new FormLayout();
		fl4 = new FormLayout();
		fl1.addComponent(cbBranch);
		fl1.addComponent(cbProduct);
		fl1.addComponent(cbStockType);
		fl2.addComponent(dfProdLedgDt);
		fl2.addComponent(tfOpenqty);
		fl2.addComponent(tfInOutFlag);
		fl3.addComponent(tfInOutQty);
		fl3.addComponent(tfCloseQty);
		fl3.addComponent(tfRefno);
		fl4.addComponent(dfRefdate);
		fl4.addComponent(tfIsLatest);
		fl4.addComponent(tfRemarks);
		hlUserInputLayout.addComponent(fl1);
		hlUserInputLayout.addComponent(fl2);
		hlUserInputLayout.addComponent(fl3);
		hlUserInputLayout.addComponent(fl4);
		hlUserInputLayout.setSpacing(true);
		/* Readonlytrue(); */
	}
	
	// Loading Branch List
	private void loadBranchlist() {
		try {
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(serviceBranch.getBranchList(null, null, null, null, companyid, "P"));
			cbBranch.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
			cbProduct.setContainerDataSource(beanprod);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Product Ledger
	private void loadSrchRslt() {
		try {
			tblMstScrSrchRslt.removeAllItems();
			List<ProductLedgerDM> listProductLedger = new ArrayList<ProductLedgerDM>();
			listProductLedger = serviceProductLedger.getProductLedgerList((Long) cbProduct.getValue(),
					(String) cbStockType.getValue(), productledgeId, (Long) cbBranch.getValue(), "F");
			recordcnt = listProductLedger.size();
			beanProdLedger = new BeanItemContainer<ProductLedgerDM>(ProductLedgerDM.class);
			beanProdLedger.addAll(listProductLedger);
			tblMstScrSrchRslt.setContainerDataSource(beanProdLedger);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "productledgeId", "branchName", "prodname",
					"productledgeDate", "stockType", "uom", "lastUpdateddt", "lastUpdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Product Name",
					"Product Ledger Date", "Stock Type", "UOM", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("productledgeId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordcnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
		cbBranch.setValue(branchId);
		cbProduct.setValue(0L);
		dfProdLedgDt.setValue(null);
		cbStockType.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		tblMstScrSrchRslt.setPageLength(14);
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editproductledger();
	}
	
	private void editproductledger() {
		if (tblMstScrSrchRslt.getValue() != null) {
			ProductLedgerDM productLedgerDM = beanProdLedger.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cbBranch.setValue(productLedgerDM.getBranchId());
			cbProduct.setValue(productLedgerDM.getProductId());
			cbStockType.setValue(productLedgerDM.getStockType());
			tfOpenqty.setValue(productLedgerDM.getOpenQty().toString());
			tfInOutFlag.setValue(productLedgerDM.getInoutFlag());
			tfInOutQty.setValue(productLedgerDM.getInoutFQty().toString());
			tfCloseQty.setValue(productLedgerDM.getCloseQty().toString());
			tfRefno.setValue(productLedgerDM.getReferenceNo());
			dfProdLedgDt.setValue(productLedgerDM.getProductledgeDate());
			dfRefdate.setValue(productLedgerDM.getReferenceDate());
			tfIsLatest.setValue(productLedgerDM.getIsLatest());
			tfRemarks.setValue(productLedgerDM.getReferenceRemark());
		}
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
		hlSearLayout.setVisible(true);
		assembleSearchLayout();
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		cbBranch.setValue(branchId);
		cbProduct.setValue(0L);
		cbStockType.setValue(null);
		tfOpenqty.setValue("");
		tfInOutFlag.setValue("");
		tfInOutQty.setValue("");
		tfCloseQty.setValue("");
		tfRefno.setValue("");
		dfProdLedgDt.setValue(null);
		dfRefdate.setValue(null);
		tfIsLatest.setValue("");
		tfRemarks.setValue("");
	}
}
