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
import java.util.Date;
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
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
	private BranchService servicebeanBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private BeanContainer<Long, BranchDM> beanBranch = null;
	private BeanContainer<Long, SmsInvoiceHdrDM> beaninvoiece = null;
	// Product Return Hdr Declaration
	private ComboBox cbinvoiceid, cbbranch;
	private TextField tfreturndoc;
	private TextArea tfreturnremark;
	private PopupDateField dfretdocdate;
	private ComboBox cbprdstatus = new GERPComboBox("Status", BASEConstants.T_SMS_PRODUCT_RETURN_HDR,
			BASEConstants.INV_STATUS);
	private BeanItemContainer<ProductReturnDtlDM> beanprodctretdtls = null;
	// Product Return Dtl Declaration
	private ComboBox cbdtlstocktyp, cbdtlprodid;
	private TextField tfdtlreturnqty;
	private TextArea tfdtlreturnremark;
	private HorizontalLayout hlimage = new HorizontalLayout();
	private ComboBox cbprdrtnstatus = new GERPComboBox("Status", BASEConstants.T_SMS_PRODUCT_RETURN_HDR,
			BASEConstants.INV_STATUS);
	public Button btnaddreturndtl = new GERPButton("Add", "addbt", this);
	private Table tblrepdtls = new GERPTable();
	List<ProductReturnDtlDM> prodreturnlist = new ArrayList<ProductReturnDtlDM>();
	private BeanItemContainer<ProductReturnHdrDM> beanproductreturn = null;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private FormLayout fl1, fl2, fl3, fl4, fl11, fl12, fl13, fl14;
	private String username;
	private Long companyid;
	private Long EmployeeId;
	private Long productreturnId;
	private Long prodreturnid;
	private int recordcnt = 0, recordcntdtls = 0;
	public Button btndelete = new GERPButton("Delete", "delete", this);
	
	public ProductReturnHdr() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting product Return Hdr UI");
		cbbranch = new GERPComboBox("Branch");
		cbbranch.setItemCaptionPropertyId("branchName");
		cbbranch.setImmediate(true);
		cbbranch.setNullSelectionAllowed(false);
		cbbranch.setWidth("150");
		loadBranchList();
		cbinvoiceid = new GERPComboBox("Invoice No");
		cbinvoiceid.setItemCaptionPropertyId("invoiceNo");
		cbinvoiceid.setImmediate(true);
		// System.out.println("cbinvoiceid--->" + cbinvoiceid.getValue());
		loadinvoicelist();
		cbinvoiceid.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbinvoiceid.getItem(itemId);
				if (item != null) {
					loadProduct();
				}
			}
		});
		dfretdocdate = new PopupDateField("Return Date");
		dfretdocdate.setWidth("130");
		tfreturndoc = new GERPTextField("Return DOC ID");
		tfreturndoc.setWidth("150");
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
		cbdtlstocktyp = new GERPComboBox("Stock Type");
		cbdtlstocktyp.addItem("New");
		cbdtlstocktyp.addItem("Scrap");
		cbdtlstocktyp.addItem("Refurbish");
		cbdtlstocktyp.setRequired(true);
		cbdtlstocktyp.setNullSelectionAllowed(false);
		cbprdrtnstatus.setRequired(true);
		hlimage.setCaption("Evidence Image");
		cbdtlprodid = new GERPComboBox("Product Name");
		cbdtlprodid.setItemCaptionPropertyId("prodname");
		cbdtlprodid.setRequired(true);
		loadProduct();
		// cbdtlprodid.setNullSelectionAllowed(false);
		cbdtlprodid.setImmediate(true);
		tfdtlreturnqty = new GERPTextField("Return Quantity");
		tfdtlreturnqty.setRequired(true);
		tfdtlreturnremark = new TextArea("Return Remark");
		tfdtlreturnremark.setWidth("150");
		tfdtlreturnremark.setHeight("50");
		btnaddreturndtl.setStyleName("add");
		btnaddreturndtl.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validatereturndtls();
					saveprodretdtls();
					// prodretdtlsResetfields();
					// new GERPSaveNotification();
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
		tblrepdtls.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblrepdtls.isSelected(event.getItemId())) {
					tblrepdtls.setImmediate(true);
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
		fl1.addComponent(cbbranch);
		fl2.addComponent(cbinvoiceid);
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
		fl1.addComponent(cbbranch);
		fl1.addComponent(dfretdocdate);
		fl2.addComponent(tfreturndoc);
		fl2.addComponent(cbinvoiceid);
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
		fl11.addComponent(cbdtlprodid);
		fl11.addComponent(tfdtlreturnqty);
		fl12.addComponent(cbdtlstocktyp);
		fl12.addComponent(tfdtlreturnremark);
		fl13.addComponent(hlimage);
		fl14.addComponent(cbprdrtnstatus);
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
		hltable.addComponent(tblrepdtls);
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
	public void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		List<BranchDM> branchlist = servicebeanBranch.getBranchList(null, null, null, null, companyid, "P");
		beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranch.setBeanIdProperty("branchId");
		beanBranch.addAll(branchlist);
		cbbranch.setContainerDataSource(beanBranch);
	}
	
	// Load Product List
	public void loadProduct() {
		try {
			List<ProductDM> ProductList = serviceProduct.getProductList(companyid, null, null, null, "Active", null,
					null, "P");
			BeanItemContainer<ProductDM> beanProduct = new BeanItemContainer<ProductDM>(ProductDM.class);
			beanProduct.addAll(ProductList);
			cbdtlprodid.setContainerDataSource(beanProduct);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Invoice
	public void loadinvoicelist() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Invoice Search...");
		try {
			List<SmsInvoiceHdrDM> invlist = serviceSmsInvoiceHdr.getSmsInvoiceHeaderList(null, null, null, null, null,
					null, companyid, "F");
			beaninvoiece = new BeanContainer<Long, SmsInvoiceHdrDM>(SmsInvoiceHdrDM.class);
			beaninvoiece.setBeanIdProperty("invoiceId");
			beaninvoiece.addAll(invlist);
			cbinvoiceid.setContainerDataSource(beaninvoiece);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Product Return Hdr
	private void loadSrchRslt() {
		tblMstScrSrchRslt.removeAllItems();
		List<ProductReturnHdrDM> prodretu = new ArrayList<ProductReturnHdrDM>();
		prodretu = serviceProductReturnHdr.getProductReturnHdrList((Long) cbinvoiceid.getValue(), companyid, null,
				(Long) cbbranch.getValue(), (String) cbprdstatus.getValue(), "F");
		recordcnt = prodretu.size();
		beanproductreturn = new BeanItemContainer<ProductReturnHdrDM>(ProductReturnHdrDM.class);
		beanproductreturn.addAll(prodretu);
		tblMstScrSrchRslt.setContainerDataSource(beanproductreturn);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "productreturnId", "branchName", "invoiceAddress",
				"returnRemarks", "prdrtnStatus", "lastUpdateddt", "lastUpdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch", "Invoice Address", "Return Remark",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("productreturnId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordcnt);
	}
	
	// Load prodcut Return dtl
	private void loadreturndtls(boolean fromdb) {
		tblrepdtls.removeAllItems();
		tblrepdtls.setPageLength(6);
		if (fromdb) {
			prodreturnlist = new ArrayList<ProductReturnDtlDM>();
			prodreturnlist = serviceprodcutreturndtl.getprodReturndtls(null, prodreturnid, null, null, "F");
		}
		recordcntdtls = prodreturnlist.size();
		beanprodctretdtls = new BeanItemContainer<ProductReturnDtlDM>(ProductReturnDtlDM.class);
		beanprodctretdtls.addAll(prodreturnlist);
		tblrepdtls.setContainerDataSource(beanprodctretdtls);
		tblrepdtls.setVisibleColumns(new Object[] { "prodrtndtlsid", "prodname", "stocktype", "returnoty",
				"returnremarks", "prdrtnstatus", "lastupdateddt", "lastuupdatedby" });
		tblrepdtls.setColumnHeaders(new String[] { "Ref.Id", "Product Name", "Stock Type", "Return Quantity",
				"Return Remarks", "Status", "Last Updated Date", "Last Updated By" });
		tblrepdtls.setColumnAlignment("productreturnId", Align.RIGHT);
		tblrepdtls.setColumnFooter("lastuupdatedby", "No.of Records :" + recordcntdtls);
	}
	
	protected void saveprodretdtls() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "save prodloadPurDtluct Return dtsl");
			ProductReturnDtlDM repdtlsobj = new ProductReturnDtlDM();
			if (tblrepdtls.getValue() != null) {
				repdtlsobj = beanprodctretdtls.getItem(tblrepdtls.getValue()).getBean();
				prodreturnlist.remove(repdtlsobj);
			}
			repdtlsobj.setProdid(((ProductDM) cbdtlprodid.getValue()).getProdid());
			repdtlsobj.setProdname(((ProductDM) cbdtlprodid.getValue()).getProdname());
			repdtlsobj.setReturnoty((Long.valueOf(tfdtlreturnqty.getValue())));
			repdtlsobj.setStocktype((String) cbdtlstocktyp.getValue().toString());
			repdtlsobj.setReturnremarks((String) tfdtlreturnremark.getValue().toString());
			repdtlsobj.setPrdrtnstatus((String) cbprdrtnstatus.getValue().toString());
			File file = new File(GERPConstants.IMAGE_PATH);
			FileInputStream fin = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];
			fin.read(fileContent);
			fin.close();
			repdtlsobj.setEvidencedoc(fileContent);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContent);
			fio.close();
			repdtlsobj.setLastupdateddt(DateUtils.getcurrentdate());
			repdtlsobj.setLastuupdatedby(username);
			prodreturnlist.add(repdtlsobj);
			prodretdtlsResetfields();
			loadreturndtls(false);
		}
		catch (Exception e) {
			e.printStackTrace();
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
		cbbranch.setValue(null);
		cbinvoiceid.setValue(null);
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
		tblrepdtls.setVisible(true);
		hlUserInputLayout.setSpacing(true);
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		loadreturndtls(false);
		cbbranch.setRequired(true);
		cbinvoiceid.setRequired(true);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tblrepdtls.setVisible(true);
		hlUserInputLayout.setSpacing(true);
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		editproductreturn();
		loadreturndtls(false);
		cbbranch.setRequired(true);
		cbinvoiceid.setRequired(true);
	}
	
	private void editproductreturn() {
		try {
			Item rowselsect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			if (rowselsect != null) {
				ProductReturnHdrDM proreturn = beanproductreturn.getItem(tblMstScrSrchRslt.getValue()).getBean();
				productreturnId = proreturn.getProductreturnId();
				cbbranch.setValue(proreturn.getBranchId());
				cbinvoiceid.setValue(proreturn.getInvoiceId());
				tfreturndoc.setValue(rowselsect.getItemProperty("returnDocId").getValue().toString());
				dfretdocdate.setValue((Date) rowselsect.getItemProperty("returnDate").getValue());
				tfreturnremark.setValue(rowselsect.getItemProperty("returnRemarks").getValue().toString());
				cbprdstatus.setValue(rowselsect.getItemProperty("prdrtnStatus").getValue().toString());
				prodreturnlist = serviceprodcutreturndtl.getprodReturndtls(null, productreturnId, null, null, null);
				System.out.println("prodreturnid->" + prodreturnid);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void editproductreturndtl() {
		try {
			Item ifselect = tblrepdtls.getItem(tblrepdtls.getValue());
			if (ifselect != null) {
				ProductReturnDtlDM retdtls = beanprodctretdtls.getItem(tblrepdtls.getValue()).getBean();
				Long uom = retdtls.getProdid();
				Collection<?> uomid = cbdtlprodid.getItemIds();
				for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbdtlprodid.getItem(itemId);
					// Get the actual bean and use the data
					ProductDM st = (ProductDM) item.getBean();
					if (uom != null && uom.equals(st.getProdid())) {
						cbdtlprodid.setValue(itemId);
					}
				}
				tfdtlreturnqty.setValue(ifselect.getItemProperty("returnoty").getValue().toString());
				cbdtlstocktyp.setValue(ifselect.getItemProperty("stocktype").getValue().toString());
				tfdtlreturnremark.setValue(ifselect.getItemProperty("returnremarks").getValue().toString());
				cbprdrtnstatus.setValue(ifselect.getItemProperty("prdrtnstatus").getValue().toString());
				if (ifselect.getItemProperty("evidencedoc").getValue() != null) {
					byte[] myimage = (byte[]) ifselect.getItemProperty("evidencedoc").getValue();
					UploadUI test = new UploadUI(hlimage);
					// test.dispayImage(myimage);
				} else {
					new UploadUI(hlimage);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbbranch.setComponentError(null);
		cbinvoiceid.setComponentError(null);
		Boolean errorFlag = false;
		if (cbbranch.getValue() == null) {
			cbbranch.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_BRANCH));
			errorFlag = true;
		}
		if (cbinvoiceid.getValue() == null) {
			cbinvoiceid.setComponentError(new UserError(GERPErrorCodes.NULL_INVOICE_ADDRESS));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	protected void validatereturndtls() throws ValidationException {
		cbdtlprodid.setComponentError(null);
		cbdtlstocktyp.setComponentError(null);
		tfdtlreturnqty.setComponentError(null);
		cbprdrtnstatus.setComponentError(null);
		boolean errorFlag = false;
		if (cbdtlprodid.getValue() == null) {
			cbdtlprodid.setComponentError(new UserError(GERPErrorCodes.NULL_SMS_PRODNAME));
			errorFlag = true;
		}
		if (cbdtlstocktyp.getValue() == null) {
			cbdtlstocktyp.setComponentError(new UserError(GERPErrorCodes.NULL_SMS_STOCKTYPE));
			errorFlag = true;
		}
		if ((tfdtlreturnqty.getValue() == "") || tfdtlreturnqty.getValue().trim().length() == 0) {
			tfdtlreturnqty.setComponentError(new UserError(GERPErrorCodes.NULL_SMS_RETURNQUANTITY));
			errorFlag = true;
		}
		if (cbprdrtnstatus.getValue() == null) {
			cbprdrtnstatus.setComponentError(new UserError(GERPErrorCodes.NULL_STATUS));
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
				rethdrobj = beanproductreturn.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			rethdrobj.setCompanyId(companyid);
			rethdrobj.setBranchId((Long) cbbranch.getValue());
			rethdrobj.setInvoiceId((Long) cbinvoiceid.getValue());
			rethdrobj.setReturnDocId(tfreturndoc.getValue());
			rethdrobj.setReturnDate(dfretdocdate.getValue());
			rethdrobj.setReturnRemarks(tfreturnremark.getValue());
			rethdrobj.setPreparedby(EmployeeId);
			rethdrobj.setReviewedby(null);
			rethdrobj.setActionedby(null);
			if (cbprdstatus.getValue() != null) {
				rethdrobj.setPrdrtnStatus(cbprdstatus.getValue().toString());
			}
			rethdrobj.setLastUpdateddt(DateUtils.getcurrentdate());
			rethdrobj.setLastUpdatedby(username);
			serviceProductReturnHdr.saveorupdateproreturnhdr(rethdrobj);
			@SuppressWarnings("unchecked")
			Collection<ProductReturnDtlDM> returndtls = (Collection<ProductReturnDtlDM>) tblrepdtls.getVisibleItemIds();
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
			e.printStackTrace();
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
		cbbranch.setRequired(false);
		cbinvoiceid.setRequired(false);
		prodretdtlsResetfields();
		resetFields();
		loadSrchRslt();
		cbbranch.setRequired(false);
		cbinvoiceid.setRequired(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Bank UI controls");
		cbbranch.setValue(null);
		cbinvoiceid.setValue(null);
		cbprdstatus.setValue(null);
		tfreturndoc.setValue("");
		dfretdocdate.setValue(null);
		tfreturnremark.setValue("");
		cbbranch.setComponentError(null);
		cbinvoiceid.setComponentError(null);
		new UploadUI(hlimage);
		prodreturnlist = new ArrayList<ProductReturnDtlDM>();
		tblrepdtls.removeAllItems();
	}
	
	private void deleteDetails() {
		ProductReturnDtlDM save = new ProductReturnDtlDM();
		if (tblrepdtls.getValue() != null) {
			save = beanprodctretdtls.getItem(tblrepdtls.getValue()).getBean();
			prodreturnlist.remove(save);
			prodretdtlsResetfields();
			loadreturndtls(false);
			btndelete.setEnabled(false);
		}
	}
	
	protected void prodretdtlsResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Bank UI controls");
		cbdtlstocktyp.setValue(null);
		btnaddreturndtl.setCaption("add");
		cbdtlprodid.setValue(null);
		tfdtlreturnqty.setValue("");
		tfdtlreturnremark.setValue("");
		new UploadUI(hlimage);
		cbdtlprodid.setComponentError(null);
		cbdtlstocktyp.setComponentError(null);
		tfdtlreturnqty.setComponentError(null);
		cbprdrtnstatus.setComponentError(null);
		cbprdrtnstatus.setValue(null);
	}
}
