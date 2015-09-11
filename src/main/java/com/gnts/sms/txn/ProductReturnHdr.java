/**
 * File Name 		: ProductReturnHdr.java 
 * Description 		: this class is used for add/edit product Return details. 
 * Author 			: JOEL GLINDAN D
 * Date 			: OCT 17, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           OCT 17, 2014       JOEL GLINDAN D	          Intial Version
 * 
 */
package com.gnts.sms.txn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.sms.domain.txn.ProductReturnDtlDM;
import com.gnts.sms.domain.txn.ProductReturnHdrDM;
import com.gnts.sms.domain.txn.SmsInvoiceHdrDM;
import com.gnts.sms.service.txn.ProductReturnDtlService;
import com.gnts.sms.service.txn.ProductReturnHdrService;
import com.gnts.sms.service.txn.SmsInvoiceHdrService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ProductReturnHdr extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(ProductReturnHdr.class);
	private ProductReturnHdrService serviceProductReturnHdr = (ProductReturnHdrService) SpringContextHelper
			.getBean("productreturnhdr");
	private ProductReturnDtlService serviceprodcutreturndtl = (ProductReturnDtlService) SpringContextHelper
			.getBean("Productreportdtls");
	private SmsInvoiceHdrService serviceSmsInvoiceHdr = (SmsInvoiceHdrService) SpringContextHelper
			.getBean("smsInvoiceheader");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	// Product Return Hdr Declaration
	private ComboBox cbInvoice, cbBranch;
	private TextField tfreturndoc;
	private TextArea tfreturnremark;
	private PopupDateField dfretdocdate;
	private ComboBox cbprdstatus = new GERPComboBox("Status", BASEConstants.T_SMS_PRODUCT_RETURN_HDR,
			BASEConstants.INV_STATUS);
	private BeanItemContainer<ProductReturnDtlDM> beanprodctretdtls = null;
	// Product Return Dtl Declaration
	private ComboBox cbStockType, cbProduct;
	private TextField tfReturnQty;
	private TextArea taRtrnRemarks;
	private HorizontalLayout hlimage = new HorizontalLayout();
	private ComboBox cbDtlStatus = new GERPComboBox("Status", BASEConstants.T_SMS_PRODUCT_RETURN_HDR,
			BASEConstants.INV_STATUS);
	private Button btnaddreturndtl = new GERPButton("Add", "addbt", this);
	private Table tblProdRetDetails = new GERPTable();
	private List<ProductReturnDtlDM> listProdReturn = new ArrayList<ProductReturnDtlDM>();
	private BeanItemContainer<ProductReturnHdrDM> beanProdReturn = null;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private FormLayout fl1, fl2, fl3, fl4, fl11, fl12, fl13, fl14;
	private String username;
	private Long companyid;
	private Long employeeId;
	private Long productreturnId;
	private Long prodreturnid;
	private int recordcnt = 0, recordcntdtls = 0;
	private Button btndelete = new GERPButton("Delete", "delete", this);
	
	public ProductReturnHdr() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting product Return Hdr UI");
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setImmediate(true);
		cbBranch.setNullSelectionAllowed(false);
		cbBranch.setWidth("150");
		loadBranchList();
		cbInvoice = new GERPComboBox("Invoice No");
		cbInvoice.setItemCaptionPropertyId("invoiceNo");
		cbInvoice.setImmediate(true);
		loadInvoicelist();
		cbInvoice.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbInvoice.getItem(itemId);
				if (item != null) {
					loadProduct();
				}
			}
		});
		dfretdocdate = new PopupDateField("Return Date");
		dfretdocdate.setWidth("130");
		tfreturndoc = new GERPTextField("Return DOC ID");
		tfreturnremark = new TextArea("Return Remark");
		tfreturnremark.setMaxLength(40);
		tfreturnremark.setWidth("150");
		tfreturnremark.setHeight("50");
		cbprdstatus.setWidth("150");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		// product return dtls
		cbStockType = new GERPComboBox("Stock Type");
		cbStockType.addItem("New");
		cbStockType.addItem("Scrap");
		cbStockType.addItem("Refurbish");
		cbStockType.setRequired(true);
		cbStockType.setNullSelectionAllowed(false);
		cbDtlStatus.setRequired(true);
		hlimage.setCaption("Evidence Image");
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		cbProduct.setRequired(true);
		loadProduct();
		// cbdtlprodid.setNullSelectionAllowed(false);
		cbProduct.setImmediate(true);
		tfReturnQty = new GERPTextField("Return Quantity");
		tfReturnQty.setRequired(true);
		taRtrnRemarks = new TextArea("Return Remark");
		taRtrnRemarks.setWidth("150");
		taRtrnRemarks.setHeight("50");
		btnaddreturndtl.setStyleName("add");
		btnaddreturndtl.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validatereturndtls();
					saveprodretdtls();
				}
				catch (Exception e) {
				}
				logger.error("Company ID : " + UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
						+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
						+ " > " + "Exception ");
			}
		});
		btndelete.setEnabled(false);
		// ClickListener for Product Return dtls
		tblProdRetDetails.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblProdRetDetails.isSelected(event.getItemId())) {
					tblProdRetDetails.setImmediate(true);
					btnaddreturndtl.setCaption("Add");
					btnaddreturndtl.setStyleName("savebt");
					btndelete.setEnabled(false);
					prodretdtlsResetfields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddreturndtl.setCaption("Update");
					btnaddreturndtl.setStyleName("savebt");
					btndelete.setEnabled(true);
					editproductreturndtl();
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
		resetFields();
		loadSrchRslt();
		loadreturndtls(false);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		fl1 = new FormLayout();
		fl2 = new FormLayout();
		fl3 = new FormLayout();
		fl1.addComponent(cbBranch);
		fl2.addComponent(cbInvoice);
		fl3.addComponent(cbprdstatus);
		hlSearchLayout.addComponent(fl1);
		hlSearchLayout.addComponent(fl2);
		hlSearchLayout.addComponent(fl3);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Assembling User search layout");
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		fl1 = new FormLayout();
		fl2 = new FormLayout();
		fl3 = new FormLayout();
		fl4 = new FormLayout();
		fl1.addComponent(cbBranch);
		fl1.addComponent(dfretdocdate);
		fl2.addComponent(tfreturndoc);
		fl2.addComponent(cbInvoice);
		fl3.addComponent(tfreturnremark);
		fl4.addComponent(cbprdstatus);
		HorizontalLayout hl1 = new HorizontalLayout();
		VerticalLayout vlstatus = new VerticalLayout();
		Label labadd = new Label();
		Label labadd1 = new Label();
		vlstatus.addComponent(btnaddreturndtl);
		vlstatus.addComponent(btndelete);
		hl1.setSpacing(true);
		hl1.setMargin(true);
		hl1.addComponent(fl1);
		hl1.addComponent(fl2);
		hl1.addComponent(fl3);
		hl1.addComponent(fl4);
		// product Return dtls
		fl11 = new FormLayout();
		fl12 = new FormLayout();
		fl13 = new FormLayout();
		fl14 = new FormLayout();
		fl11.addComponent(cbProduct);
		fl11.addComponent(tfReturnQty);
		fl12.addComponent(cbStockType);
		fl12.addComponent(taRtrnRemarks);
		fl13.addComponent(hlimage);
		fl14.addComponent(cbDtlStatus);
		fl14.addComponent(labadd);
		fl14.addComponent(labadd1);
		fl14.addComponent(vlstatus);
		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.setMargin(true);
		hl2.setSpacing(true);
		hl2.addComponent(fl11);
		hl2.addComponent(fl12);
		hl2.addComponent(fl13);
		hl2.addComponent(fl14);
		VerticalLayout hltable = new VerticalLayout();
		hltable.addComponent(hl2);
		hltable.addComponent(tblProdRetDetails);
		hltable.setWidth("100%");
		hltable.setSizeFull();
		// Vertical layout
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.addComponent(GERPPanelGenerator.createPanel(hl1));
		vl.addComponent(GERPPanelGenerator.createPanel(hltable));
		hlUserInputLayout.addComponent(vl);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeFull();
	}
	
	// Load Branch List
	private void loadBranchList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
			BeanContainer<Long, BranchDM> beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanBranch.setBeanIdProperty("branchId");
			beanBranch.addAll(serviceBranch.getBranchList(null, null, null, null, companyid, "P"));
			cbBranch.setContainerDataSource(beanBranch);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Product List
	private void loadProduct() {
		try {
			BeanItemContainer<ProductDM> beanProduct = new BeanItemContainer<ProductDM>(ProductDM.class);
			beanProduct.addAll(serviceProduct.getProductList(companyid, null, null, null, "Active", null, null, "P"));
			cbProduct.setContainerDataSource(beanProduct);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Invoice
	private void loadInvoicelist() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Invoice Search...");
		try {
			BeanContainer<Long, SmsInvoiceHdrDM> beaninvoiece = new BeanContainer<Long, SmsInvoiceHdrDM>(
					SmsInvoiceHdrDM.class);
			beaninvoiece.setBeanIdProperty("invoiceId");
			beaninvoiece.addAll(serviceSmsInvoiceHdr.getSmsInvoiceHeaderList(null, null, null, null, null, null,
					companyid, "P"));
			cbInvoice.setContainerDataSource(beaninvoiece);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Product Return Hdr
	private void loadSrchRslt() {
		try {
			tblMstScrSrchRslt.removeAllItems();
			List<ProductReturnHdrDM> list = new ArrayList<ProductReturnHdrDM>();
			list = serviceProductReturnHdr.getProductReturnHdrList((Long) cbInvoice.getValue(), companyid, null,
					(Long) cbBranch.getValue(), (String) cbprdstatus.getValue(), "F");
			recordcnt = list.size();
			beanProdReturn = new BeanItemContainer<ProductReturnHdrDM>(ProductReturnHdrDM.class);
			beanProdReturn.addAll(list);
			tblMstScrSrchRslt.setContainerDataSource(beanProdReturn);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "productreturnId", "branchName", "invoiceAddress",
					"returnRemarks", "prdrtnStatus", "lastUpdateddt", "lastUpdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch", "Invoice Address", "Return Remark",
					"Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("productreturnId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordcnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load prodcut Return dtl
	private void loadreturndtls(boolean fromdb) {
		tblProdRetDetails.removeAllItems();
		tblProdRetDetails.setPageLength(6);
		if (fromdb) {
			listProdReturn = new ArrayList<ProductReturnDtlDM>();
			listProdReturn = serviceprodcutreturndtl.getprodReturndtls(null, prodreturnid, null, null, "F");
		}
		recordcntdtls = listProdReturn.size();
		beanprodctretdtls = new BeanItemContainer<ProductReturnDtlDM>(ProductReturnDtlDM.class);
		beanprodctretdtls.addAll(listProdReturn);
		tblProdRetDetails.setContainerDataSource(beanprodctretdtls);
		tblProdRetDetails.setVisibleColumns(new Object[] { "prodrtndtlsid", "prodname", "stocktype", "returnoty",
				"returnremarks", "prdrtnstatus", "lastupdateddt", "lastuupdatedby" });
		tblProdRetDetails.setColumnHeaders(new String[] { "Ref.Id", "Product Name", "Stock Type", "Return Quantity",
				"Return Remarks", "Status", "Last Updated Date", "Last Updated By" });
		tblProdRetDetails.setColumnAlignment("productreturnId", Align.RIGHT);
		tblProdRetDetails.setColumnFooter("lastuupdatedby", "No.of Records :" + recordcntdtls);
	}
	
	protected void saveprodretdtls() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "save prodloadPurDtluct Return dtsl");
			ProductReturnDtlDM returnDtlDM = new ProductReturnDtlDM();
			if (tblProdRetDetails.getValue() != null) {
				returnDtlDM = beanprodctretdtls.getItem(tblProdRetDetails.getValue()).getBean();
				listProdReturn.remove(returnDtlDM);
			}
			returnDtlDM.setProdid(((ProductDM) cbProduct.getValue()).getProdid());
			returnDtlDM.setProdname(((ProductDM) cbProduct.getValue()).getProdname());
			returnDtlDM.setReturnoty((Long.valueOf(tfReturnQty.getValue())));
			returnDtlDM.setStocktype((String) cbStockType.getValue().toString());
			returnDtlDM.setReturnremarks((String) taRtrnRemarks.getValue().toString());
			returnDtlDM.setPrdrtnstatus((String) cbDtlStatus.getValue().toString());
			File file = new File(GERPConstants.IMAGE_PATH);
			FileInputStream fin = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];
			fin.read(fileContent);
			fin.close();
			returnDtlDM.setEvidencedoc(fileContent);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			fio.read(fileContent);
			fio.close();
			returnDtlDM.setLastupdateddt(DateUtils.getcurrentdate());
			returnDtlDM.setLastuupdatedby(username);
			listProdReturn.add(returnDtlDM);
			prodretdtlsResetfields();
			loadreturndtls(false);
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
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbBranch.setValue(null);
		cbInvoice.setValue(null);
		cbprdstatus.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tblProdRetDetails.setVisible(true);
		hlUserInputLayout.setSpacing(true);
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		loadreturndtls(false);
		cbBranch.setRequired(true);
		cbInvoice.setRequired(true);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tblProdRetDetails.setVisible(true);
		hlUserInputLayout.setSpacing(true);
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		editProductReturn();
		loadreturndtls(false);
		cbBranch.setRequired(true);
		cbInvoice.setRequired(true);
	}
	
	private void editProductReturn() {
		try {
			if (tblMstScrSrchRslt.getValue() != null) {
				ProductReturnHdrDM productReturnHdrDM = beanProdReturn.getItem(tblMstScrSrchRslt.getValue()).getBean();
				productreturnId = productReturnHdrDM.getProductreturnId();
				cbBranch.setValue(productReturnHdrDM.getBranchId());
				cbInvoice.setValue(productReturnHdrDM.getInvoiceId());
				tfreturndoc.setValue(productReturnHdrDM.getReturnDocId());
				dfretdocdate.setValue(productReturnHdrDM.getReturnDate());
				tfreturnremark.setValue(productReturnHdrDM.getReturnRemarks());
				cbprdstatus.setValue(productReturnHdrDM.getPrdrtnStatus());
				listProdReturn = serviceprodcutreturndtl.getprodReturndtls(null, productreturnId, null, null, null);
				System.out.println("prodreturnid->" + prodreturnid);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editproductreturndtl() {
		try {
			if (tblProdRetDetails.getValue() != null) {
				ProductReturnDtlDM productReturnDtlDM = beanprodctretdtls.getItem(tblProdRetDetails.getValue())
						.getBean();
				Long prodid = productReturnDtlDM.getProdid();
				Collection<?> prodids = cbProduct.getItemIds();
				for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
					// Get the actual bean and use the data
					ProductDM st = (ProductDM) item.getBean();
					if (prodid != null && prodid.equals(st.getProdid())) {
						cbProduct.setValue(itemId);
					}
				}
				tfReturnQty.setValue(productReturnDtlDM.getReturnoty().toString());
				cbStockType.setValue(productReturnDtlDM.getStocktype());
				taRtrnRemarks.setValue(productReturnDtlDM.getReturnremarks());
				cbDtlStatus.setValue(productReturnDtlDM.getPrdrtnstatus());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbBranch.setComponentError(null);
		cbInvoice.setComponentError(null);
		Boolean errorFlag = false;
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_BRANCH));
			errorFlag = true;
		}
		if (cbInvoice.getValue() == null) {
			cbInvoice.setComponentError(new UserError(GERPErrorCodes.NULL_INVOICE_ADDRESS));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private void validatereturndtls() throws ValidationException {
		cbProduct.setComponentError(null);
		cbStockType.setComponentError(null);
		tfReturnQty.setComponentError(null);
		cbDtlStatus.setComponentError(null);
		boolean errorFlag = false;
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_SMS_PRODNAME));
			errorFlag = true;
		}
		if (cbStockType.getValue() == null) {
			cbStockType.setComponentError(new UserError(GERPErrorCodes.NULL_SMS_STOCKTYPE));
			errorFlag = true;
		}
		if ((tfReturnQty.getValue() == "") || tfReturnQty.getValue().trim().length() == 0) {
			tfReturnQty.setComponentError(new UserError(GERPErrorCodes.NULL_SMS_RETURNQUANTITY));
			errorFlag = true;
		}
		if (cbDtlStatus.getValue() == null) {
			cbDtlStatus.setComponentError(new UserError(GERPErrorCodes.NULL_STATUS));
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws IOException {
		try {
			ProductReturnHdrDM rethdrobj = new ProductReturnHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				rethdrobj = beanProdReturn.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			rethdrobj.setCompanyId(companyid);
			rethdrobj.setBranchId((Long) cbBranch.getValue());
			rethdrobj.setInvoiceId((Long) cbInvoice.getValue());
			rethdrobj.setReturnDocId(tfreturndoc.getValue());
			rethdrobj.setReturnDate(dfretdocdate.getValue());
			rethdrobj.setReturnRemarks(tfreturnremark.getValue());
			rethdrobj.setPreparedby(employeeId);
			rethdrobj.setReviewedby(null);
			rethdrobj.setActionedby(null);
			if (cbprdstatus.getValue() != null) {
				rethdrobj.setPrdrtnStatus(cbprdstatus.getValue().toString());
			}
			rethdrobj.setLastUpdateddt(DateUtils.getcurrentdate());
			rethdrobj.setLastUpdatedby(username);
			serviceProductReturnHdr.saveorupdateproreturnhdr(rethdrobj);
			@SuppressWarnings("unchecked")
			Collection<ProductReturnDtlDM> returndtls = (Collection<ProductReturnDtlDM>) tblProdRetDetails
					.getVisibleItemIds();
			for (ProductReturnDtlDM saveprodretdtls : (Collection<ProductReturnDtlDM>) returndtls) {
				saveprodretdtls.setProdreturnid(Long.valueOf(rethdrobj.getProductreturnId()));
				serviceprodcutreturndtl.saveorupdateProdRetdtls(saveprodretdtls);
			}
			loadSrchRslt();
			loadreturndtls(true);
			resetFields();
			prodretdtlsResetfields();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		hlSearchLayout.setVisible(true);
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		cbBranch.setRequired(false);
		cbInvoice.setRequired(false);
		prodretdtlsResetfields();
		resetFields();
		loadSrchRslt();
		cbBranch.setRequired(false);
		cbInvoice.setRequired(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Bank UI controls");
		cbBranch.setValue(null);
		cbInvoice.setValue(null);
		cbprdstatus.setValue(null);
		tfreturndoc.setValue("");
		dfretdocdate.setValue(null);
		tfreturnremark.setValue("");
		cbBranch.setComponentError(null);
		cbInvoice.setComponentError(null);
		new UploadUI(hlimage);
		listProdReturn = new ArrayList<ProductReturnDtlDM>();
		tblProdRetDetails.removeAllItems();
	}
	
	private void deleteDetails() {
		ProductReturnDtlDM save = new ProductReturnDtlDM();
		if (tblProdRetDetails.getValue() != null) {
			save = beanprodctretdtls.getItem(tblProdRetDetails.getValue()).getBean();
			listProdReturn.remove(save);
			prodretdtlsResetfields();
			loadreturndtls(false);
			btndelete.setEnabled(false);
		}
	}
	
	private void prodretdtlsResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Bank UI controls");
		cbStockType.setValue(null);
		btnaddreturndtl.setCaption("add");
		cbProduct.setValue(null);
		tfReturnQty.setValue("");
		taRtrnRemarks.setValue("");
		new UploadUI(hlimage);
		cbProduct.setComponentError(null);
		cbStockType.setComponentError(null);
		tfReturnQty.setComponentError(null);
		cbDtlStatus.setComponentError(null);
		cbDtlStatus.setValue(null);
	}
}
