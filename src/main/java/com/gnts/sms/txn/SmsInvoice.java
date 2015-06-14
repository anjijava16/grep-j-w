/**
 * File Name 		: SmsInvoice.java 
 * Description 		: This Screen Purpose for Modify the PurchasePO Details.
 * 					  Add the PurchasePO details process should be directly added in DB.
 * Author 			: Ganga 
 * Date 			: Oct 10, 2014

 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1       Oct 10, 2014             GANGA               Initial  Version
 */
package com.gnts.sms.txn;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPNumberField;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.service.txn.WorkOrderDtlService;
import com.gnts.sms.domain.txn.PurchasePOHdrDM;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsInvoiceDtlDM;
import com.gnts.sms.domain.txn.SmsInvoiceHdrDM;
import com.gnts.sms.domain.txn.SmsPODtlDM;
import com.gnts.sms.domain.txn.SmsPOHdrDM;
import com.gnts.sms.service.mst.SmsTaxesService;
import com.gnts.sms.service.txn.PurchasePODtlService;
import com.gnts.sms.service.txn.PurchasePOHdrService;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsInvoiceDtlService;
import com.gnts.sms.service.txn.SmsInvoiceHdrService;
import com.gnts.sms.service.txn.SmsPODtlService;
import com.gnts.sms.service.txn.SmsPOHdrService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SmsInvoice extends BaseTransUI {
	private SmsInvoiceHdrService serviceInvoiceHdr = (SmsInvoiceHdrService) SpringContextHelper
			.getBean("smsInvoiceheader");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");

	private WorkOrderDtlService serviceWrkOrdDtl = (WorkOrderDtlService) SpringContextHelper.getBean("workOrderDtl");
	private SmsPODtlService servicesmspodtl = (SmsPODtlService) SpringContextHelper.getBean("SmsPODtl");

	private SmsPOHdrService servicePurchaseOrdHdr = (SmsPOHdrService) SpringContextHelper.getBean("smspohdr");
	private SmsTaxesService serviceTaxesSms = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	private SmsInvoiceDtlService serviceInvoiceDtl = (SmsInvoiceDtlService) SpringContextHelper
			.getBean("smsInvoiceDtl");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ClientService serviceClient = (ClientService) SpringContextHelper.getBean("clients");
	private PurchasePODtlService servicePurchasePODtl = (PurchasePODtlService) SpringContextHelper
			.getBean("PurchasePODtl");
	private PurchasePOHdrService servicePurchasePo = (PurchasePOHdrService) SpringContextHelper
			.getBean("PurchasePOhdr");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	// form layout for input controls for Invioce Header
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5;
	// form layout for input controls for invoice Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4, flDtlColumn5;
	// // User Input Components for invoiceHdr Details
	private ComboBox cbBranch, cbStatus, cbPONumber, cbClient, cbCarrier;
	private TextField tfInvNo, tfBasictotal, tfpackingPer, tfPaclingValue, tfvendorName, tfDelNOtNo;
	private TextField tfSubTotal, tfVatPer, tfVatValue, tfEDPer, tfEDValue, tfHEDPer;
	private TextField tfHEDValue, tfCessPer, tfCessValue, tfCstPer, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightPer, tfFreightValue, tfOtherPer, tfOtherValue, tfGrandtotal, tfLrNo, tfDocumentCharges,
			tfPDCCharges, tfDiscountPer, tfDiscountValue;
	private ComboBox cbpaymetTerms, cbFreightTerms, cbWarrentyTerms, cbDelTerms, cbDispatchBy, cbEnqNumber;
	private TextArea taInvoiceOrd, taShpnAddr;
	private PopupDateField dfInvoiceDt, dfShipmntDt, dfprpnDt, dfLRDate, dfDelNotDt,dfedd;
	private CheckBox chkDutyExe, ckPdcRqu, chkCformReq;
	private OptionGroup ogInvoiceType = new OptionGroup("Invoice Type");
	private Button btnAdd = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlquoteDoc = new VerticalLayout();
	// InvoiceDtl components
	private ComboBox cbproduct, cbUom, cbDtlStatus;
	private TextField tfcusProdCode, tfInvoiceQty, tfUnitRate, tfBasicValue;
	private TextArea taCustProdDessc;
	private GERPTextField tfCustomField1 = new GERPTextField("Part Number");
	private GERPTextField tfCustomField2 = new GERPTextField("Drawing Number");
	private GERPTextField tfDisToatal = new GERPTextField("Total");
	private static final long serialVersionUID = 1L;
	// BeanItem container
	private BeanItemContainer<SmsInvoiceHdrDM> beanInvoiceHdr = null;
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	private BeanItemContainer<SmsInvoiceDtlDM> beanInvoiceDtl = new BeanItemContainer<SmsInvoiceDtlDM>(
			SmsInvoiceDtlDM.class);
	List<SmsInvoiceDtlDM> invoiceDtllList = new ArrayList<SmsInvoiceDtlDM>();
	// local variables declaration
	private String username;
	private Long companyid;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private GERPTable tblInvoicDtl;
	private int recordCnt;
	private Long invoiceId;
	private Long EmployeeId;
	private File file;
	private Long roleId;
	private Long branchId;
	private Long screenId;
	private Long moduleId;
	private String invoiceaId,invoicetype;
	private SmsComments comments;
	private Long poId;
	VerticalLayout vlTableForm = new VerticalLayout();
	public Button btndelete = new GERPButton("Delete", "delete", this);
	private String status;
	public static boolean filevalue1 = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(PurchaseQuote.class);
	
	// Constructor received the parameters from Login UI class
	public SmsInvoice() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		EmployeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside SmsInvoice() constructor");
		// Loading the SmsInvoice UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax UI");
		// Initialization for SmsInvoice Details user input components
		cbClient = new GERPComboBox("Client Name");
		cbClient.setItemCaptionPropertyId("clientName");
		cbClient.setWidth("116");
		cbClient.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadPoNo();
				}
			}
		});
		btndelete.setEnabled(false);
		btndelete.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deletesmsinDetails();
			}
		});
		ogInvoiceType.addItems("Invoice", "Proforma Invoice");
		ogInvoiceType.setStyleName("displayblock");
		ogInvoiceType.setNullSelectionAllowed(false);
		tfInvNo = new TextField("Invoice No.");
		tfInvNo.setWidth("116");
		dfInvoiceDt = new GERPPopupDateField("Invoice Date");
		dfInvoiceDt.setInputPrompt("Select Date");
		dfInvoiceDt.setWidth("116");
		dfprpnDt = new PopupDateField("Prep. Time");
		dfprpnDt.setDateFormat("dd-MMM-yyyy HH:mm");
		dfprpnDt.setInputPrompt("Select Date");
		dfprpnDt.setWidth("116");
		dfShipmntDt = new PopupDateField("Removal Time");
		dfShipmntDt.setDateFormat("dd-MMM-yyyy HH:mm");
		dfShipmntDt.setInputPrompt("Select Date");
		dfShipmntDt.setWidth("116");
		dfLRDate = new GERPPopupDateField("LR Date");
		dfLRDate.setInputPrompt("Select Date");
		dfLRDate.setWidth("116");
		cbPONumber = new ComboBox("PoNo.");
		cbPONumber.setItemCaptionPropertyId("pono");
		cbPONumber.setWidth("116");
		cbEnqNumber = new ComboBox("Enquiry No");
		cbEnqNumber.setItemCaptionPropertyId("enquiryNo");
		cbEnqNumber.setWidth("116");
		loadEnquiryNo();
		cbEnqNumber.setRequired(true);
		cbEnqNumber.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnqNumber.getItem(itemId);
				if (item != null) {
					try {
						loadPoNo();
						loadClientList();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		cbPONumber.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbPONumber.getItem(itemId);
				if (item != null) {
					try {
						poId = (Long) cbPONumber.getValue();
						loadProduct();
					}
					catch (Exception e) {
					}
				}
			}
		});
		
		
		tfDocumentCharges = new GERPNumberField("Doc. Charges");
		tfDocumentCharges.setWidth("116");
		tfDocumentCharges.setImmediate(true);
		tfDocumentCharges.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfPDCCharges = new GERPNumberField("PDC Charges");
		tfPDCCharges.setWidth("116");
		tfPDCCharges.setImmediate(true);
		tfPDCCharges.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfBasictotal = new GERPNumberField("Basic total");
		tfBasictotal.setWidth("116");
		tfSubTotal = new GERPNumberField("Sub Total");
		tfSubTotal.setWidth("116");
		tfpackingPer = new TextField();
		tfpackingPer.setWidth("30");
		tfPaclingValue = new GERPNumberField();
		tfPaclingValue.setWidth("90");
		tfPaclingValue.setImmediate(true);
		tfVatPer = new TextField();
		tfVatPer.setWidth("30");
		tfVatValue = new GERPNumberField();
		tfVatValue.setWidth("90");
		tfEDPer = new TextField();
		tfEDPer.setWidth("30");
		tfEDValue = new GERPNumberField();
		tfEDValue.setWidth("90");
		tfHEDPer = new TextField();
		tfHEDPer.setWidth("30");
		tfHEDValue = new GERPNumberField();
		tfHEDValue.setWidth("90");
		tfCessPer = new TextField();
		tfCessPer.setWidth("30");
		tfCessValue = new GERPNumberField();
		tfCessValue.setWidth("90");
		tfSubTaxTotal = new GERPNumberField("Sub Tax Total");
		tfCstValue = new GERPNumberField();
		tfCstValue.setWidth("90");
		tfSubTaxTotal.setWidth("116");
		tfCstPer = new TextField();
		tfCstPer.setWidth("30");
		tfCstValue.setImmediate(true);
		tfGrandtotal = new GERPNumberField("Grand Total");
		tfGrandtotal.setWidth("116");
		tfFreightPer = new TextField();
		tfFreightPer.setWidth("30");
		tfFreightValue = new GERPNumberField();
		tfFreightValue.setWidth("90");
		tfOtherPer = new TextField();
		tfOtherPer.setWidth("30");
		tfOtherValue = new GERPNumberField();
		tfOtherValue.setWidth("90");
		cbpaymetTerms = new ComboBox("Payment Terms");
		cbpaymetTerms.setItemCaptionPropertyId("lookupname");
		cbpaymetTerms.setWidth("116");
		loadPaymentTerms();
		cbFreightTerms = new ComboBox("Freight Terms");
		cbFreightTerms.setItemCaptionPropertyId("lookupname");
		cbFreightTerms.setWidth("116");
		loadFreightTerms();
		cbWarrentyTerms = new ComboBox("Warrenty Terms");
		cbWarrentyTerms.setItemCaptionPropertyId("lookupname");
		cbWarrentyTerms.setWidth("116");
		loadWarentyTerms();
		cbDelTerms = new ComboBox("Delivery Terms");
		cbDelTerms.setItemCaptionPropertyId("lookupname");
		cbDelTerms.setWidth("116");
		loadDeliveryTerms();
		chkDutyExe = new CheckBox("Duty Exempted");
		chkDutyExe.setImmediate(true);
		chkDutyExe.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		chkCformReq = new CheckBox("Cform Req");
		chkCformReq.setImmediate(true);
		chkCformReq.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		ckPdcRqu = new CheckBox("PDC Req");
		ckPdcRqu.setImmediate(true);
		ckPdcRqu.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (ckPdcRqu.getValue()) {
					tfPDCCharges.setReadOnly(false);
				} else {
					tfPDCCharges.setReadOnly(false);
					tfPDCCharges.setValue("0");
					tfPDCCharges.setReadOnly(true);
					getCalculatedValues();
				}
			}
		});
		cbBranch = new ComboBox("Branch");
		cbBranch.setWidth("116");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		try {
			ApprovalSchemaDM obj = serviceInvoiceHdr.getReviewerId(companyid, screenId, branchId, roleId).get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_INVOICE_HDR, BASEConstants.INVOICE_STATUS_RV);
			} else {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_INVOICE_HDR, BASEConstants.INVOICE_STATUS);
			}
		}
		catch (Exception e) {
		}
		tfDiscountPer = new TextField();
		tfDiscountPer.setWidth("30");
		tfDiscountValue = new GERPNumberField();
		tfDiscountValue.setWidth("90");
		tfDiscountPer.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		cbStatus.setWidth("116");
		tfvendorName = new TextField("Vendor Name");
		tfvendorName.setWidth("116");
		taInvoiceOrd = new TextArea("Invoice Addr");
		taInvoiceOrd.setWidth("116");
		taInvoiceOrd.setHeight("30");
		taShpnAddr = new TextArea("Shipping Addr");
		taShpnAddr.setWidth("116");
		taShpnAddr.setHeight("30");
		cbCarrier = new GERPComboBox("Carrier");
		cbCarrier.setWidth("116");
		cbCarrier.setItemCaptionPropertyId("lookupname");
		loadGoodCarrier();
		tfLrNo = new TextField("LR No.");
		tfLrNo.setWidth("116");
		dfLRDate = new GERPPopupDateField("LR Date");
		dfLRDate.setWidth("116");
		dfLRDate.setInputPrompt("Select Date");
		tfDelNOtNo = new TextField("Delivery Note No.");
		tfDelNOtNo.setWidth("116");
		dfDelNotDt = new GERPPopupDateField("Delivery Note Dt.");
		dfDelNotDt.setWidth("116");
		dfDelNotDt.setInputPrompt("Select Date");
		dfedd = new GERPPopupDateField("Exp Del Dt.");
		dfedd.setWidth("116");
		dfedd.setInputPrompt("Select Date");
		cbDispatchBy = new GERPComboBox("Dispatch by");
		cbDispatchBy.addItems("By air", "By Road", "By hand");
		cbDispatchBy.setWidth("116");
		// Invoice Dtl Comp
		cbproduct = new ComboBox("Product Name");
		cbproduct.setItemCaptionPropertyId("prodname");
		cbproduct.setWidth("130");
		loadProduct();
		tfEDValue.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfCstPer.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfpackingPer.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		cbproduct.setImmediate(true);
		cbproduct.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbproduct.getValue() != null) {
					tfInvoiceQty.setReadOnly(false);
					if (((SmsPODtlDM) cbproduct.getValue()).getPoqty() != null) {
						tfInvoiceQty.setValue(((SmsPODtlDM) cbproduct.getValue()).getPoqty() + "");
						// tfInvoiceQty.setReadOnly(true);
					} else {
						tfInvoiceQty.setValue("0");
					}
					tfInvoiceQty.setCaption("Invoice Qty(" + ((SmsPODtlDM) cbproduct.getValue()).getProduom() + ")");
					tfUnitRate.setValue(((SmsPODtlDM) cbproduct.getValue()).getUnitrate().toString());
					tfBasicValue.setReadOnly(false);
					tfBasicValue.setValue(((SmsPODtlDM) cbproduct.getValue()).getBasicvalue().toString());
					tfBasicValue.setReadOnly(true);
					if (((SmsPODtlDM) cbproduct.getValue()).getCustomField1() != null) {
						tfCustomField1.setValue(((SmsPODtlDM) cbproduct.getValue()).getCustomField1());
					} else {
						tfCustomField1.setValue("");
					}
					if (((SmsPODtlDM) cbproduct.getValue()).getCustomField2() != null) {
						tfCustomField2.setValue(((SmsPODtlDM) cbproduct.getValue()).getCustomField2());
					} else {
						tfCustomField2.setValue("");
					}
				}
			}
		});
		tfcusProdCode = new TextField("Product Code");
		tfcusProdCode.setWidth("130");
		taCustProdDessc = new TextArea("Product Desc");
		taCustProdDessc.setHeight("30");
		taCustProdDessc.setWidth("130");
		tfCustomField1.setWidth("130");
		tfCustomField2.setWidth("130");
		tfDisToatal.setWidth("120");
		cbUom = new ComboBox("Product Uom");
		cbUom.setItemCaptionPropertyId("lookupname");
		cbUom.setWidth("130");
		loadUomList();
		tfInvoiceQty = new TextField("Invoice Qty");
		tfInvoiceQty.setWidth("130");
		tfUnitRate = new TextField("Unit Rate");
		tfUnitRate.setWidth("130");
		tfUnitRate.setValue("0");
		tfBasicValue = new TextField("Basic value");
		tfBasicValue.setWidth("130");
		tfBasicValue.setValue("0");
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbDtlStatus.setWidth("130");
		cbDtlStatus.setValue("Active");
		btnAdd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateInvoiceDetails()) {
					saveInvoiceDtl();
				}
			}
		});
		tblInvoicDtl = new GERPTable();
		tblInvoicDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblInvoicDtl.isSelected(event.getItemId())) {
					tblInvoicDtl.setImmediate(true);
					btnAdd.setCaption("Add");
					btnAdd.setStyleName("savebt");
					resetInvoiceDetails();
					btndelete.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAdd.setCaption("Update");
					btnAdd.setStyleName("savebt");
					editInvoiceDtl();
					btndelete.setEnabled(true);
				}
			}
		});
		tfUnitRate.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				getCalcBasicValue();
			}
		});
		tfInvoiceQty.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				getCalcBasicValue();
			}
		});
		tfDiscountValue.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfFreightValue.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				
				getCalculatedValues();
			}
		});
tfOtherValue.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
changeInvoiceType();

		ogInvoiceType.setImmediate(true);
		ogInvoiceType.setRequired(true);
		ogInvoiceType.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				changeInvoiceType();
			}
		});
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		loadInvoiceDtl();
		btnAdd.setStyleName("add");
		hlquoteDoc.setCaption("Document");
	}
	
	private void changeInvoiceType() {
		tfInvNo.setReadOnly(false);
		if (ogInvoiceType.getValue() != null) {
			if (ogInvoiceType.getValue().toString().equalsIgnoreCase("Proforma Invoice")) {
				poId = null;
				cbPONumber.setRequired(false);
				dfedd.setRequired(true);
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, moduleId, "SM_PINVNO").get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						tfInvNo.setValue(slnoObj.getKeyDesc());
						tfInvNo.setReadOnly(true);
					}
				}
				catch (Exception e) {
				}
				loadProductfull();			} else {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, moduleId, "SM_INVNO").get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						tfInvNo.setValue(slnoObj.getKeyDesc());
						tfInvNo.setReadOnly(true);
					}
				}
				catch (Exception e) {
				}
				cbPONumber.setRequired(true);
				dfedd.setRequired(false);

				cbproduct.setContainerDataSource(null);
			}
		}
	}
	// Load Product List
	public void loadProductfull() {
		try {
			List<ProductDM> ProductList = serviceProduct.getProductList(companyid, null, null, null, "Active", null,
					null, "P");
			BeanItemContainer<ProductDM> beanProduct = new BeanItemContainer<ProductDM>(ProductDM.class);
			beanProduct.addAll(ProductList);
			cbproduct.setContainerDataSource(beanProduct);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void getCalcBasicValue() {
		// TODO Auto-generated method stub
		try {
			tfBasicValue.setReadOnly(false);
			tfBasicValue.setValue((new BigDecimal(tfUnitRate.getValue())).multiply(
					new BigDecimal(tfInvoiceQty.getValue())).toString());
			tfBasicValue.setReadOnly(true);
		}
		catch (Exception e) {
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for SmsInvoice UI search layout
		tfvendorName.setRequired(false);
		hlSearchLayout.removeAllComponents();
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		flColumn5 = new GERPFormLayout();
		// Adding components into form layouts for TestType UI search layout
		flColumn1.addComponent(cbBranch);
		flColumn2.addComponent(cbClient);
		flColumn3.addComponent(tfInvNo);
		flColumn4.addComponent(cbStatus);
		// Adding form layouts into search layout for TestType UI search mode
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ " assembleInputUserLayout layout");
		/*
		 * Adding user input layout to the input layout as all the fields in the user input are available in the edit
		 * block. hence the same layout used as is
		 */
		// Set required fields
		// Removing components from search layout and re-initializing form layouts
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(ogInvoiceType);
		flColumn1.addComponent(cbBranch);
		flColumn1.addComponent(cbEnqNumber);
		flColumn1.addComponent(cbClient);
		flColumn1.addComponent(cbPONumber);
		flColumn1.addComponent(tfInvNo);
		flColumn1.addComponent(dfInvoiceDt);
		flColumn1.addComponent(dfprpnDt);
		flColumn2.addComponent(dfShipmntDt);
		// flColumn1.addComponent(taInvoiceOrd);
		// flColumn1.addComponent(taShpnAddr);
		flColumn2.addComponent(dfedd);
		flColumn2.addComponent(tfBasictotal);
		HorizontalLayout discount = new HorizontalLayout();
		discount.addComponent(tfDiscountPer);
		discount.addComponent(tfDiscountValue);
		discount.setCaption("Discount");
		flColumn2.addComponent(discount);
		flColumn2.setComponentAlignment(discount, Alignment.TOP_LEFT);
		flColumn2.addComponent(tfDisToatal);
		HorizontalLayout pp = new HorizontalLayout();
		pp.addComponent(tfpackingPer);
		pp.addComponent(tfPaclingValue);
		pp.setCaption("Packing(%)");
		flColumn2.addComponent(pp);
		flColumn2.setComponentAlignment(pp, Alignment.TOP_LEFT);
		flColumn2.addComponent(tfSubTotal);
	
		HorizontalLayout ed = new HorizontalLayout();
		ed.addComponent(tfEDPer);
		ed.addComponent(tfEDValue);
		ed.setCaption("ED");
		flColumn2.addComponent(ed);
		flColumn2.setComponentAlignment(ed, Alignment.TOP_LEFT);
		/*
		 * HorizontalLayout hed = new HorizontalLayout(); hed.addComponent(tfHEDPer); hed.addComponent(tfHEDValue);
		 * hed.setCaption("HED"); flColumn2.addComponent(hed); flColumn2.setComponentAlignment(hed, Alignment.TOP_LEFT);
		 * HorizontalLayout cess = new HorizontalLayout(); cess.addComponent(tfCessPer); cess.addComponent(tfCessValue);
		 * cess.setCaption("CESS"); flColumn3.addComponent(cess); flColumn3.setComponentAlignment(cess,
		 * Alignment.TOP_LEFT);
		 */
		flColumn2.addComponent(tfSubTaxTotal);

		HorizontalLayout vp = new HorizontalLayout();
		vp.addComponent(tfVatPer);
		vp.addComponent(tfVatValue);
		vp.setCaption("VAT");
		flColumn3.addComponent(vp);
		flColumn3.setComponentAlignment(vp, Alignment.TOP_LEFT);
		HorizontalLayout cst = new HorizontalLayout();
		cst.addComponent(tfCstPer);
		cst.addComponent(tfCstValue);
		cst.setCaption("CST");
		flColumn3.addComponent(cst);
		flColumn3.setComponentAlignment(cst, Alignment.TOP_LEFT);
		HorizontalLayout frght = new HorizontalLayout();
		frght.addComponent(tfFreightPer);
		frght.addComponent(tfFreightValue);
		frght.setCaption("Freight");
		flColumn3.addComponent(frght);
		flColumn3.setComponentAlignment(frght, Alignment.TOP_LEFT);
		HorizontalLayout other = new HorizontalLayout();
		other.addComponent(tfOtherPer);
		other.addComponent(tfOtherValue);
		other.setCaption("Other(%)");
		flColumn3.addComponent(other);
		flColumn3.setComponentAlignment(other, Alignment.TOP_LEFT);
		flColumn3.addComponent(tfPDCCharges);
		flColumn3.addComponent(tfDocumentCharges);
		flColumn3.addComponent(tfGrandtotal);
		flColumn3.addComponent(tfGrandtotal);
		/*
		 * flColumn3.addComponent(cbFreightTerms); flColumn4.addComponent(cbpaymetTerms);
		 * flColumn4.addComponent(cbWarrentyTerms); flColumn4.addComponent(cbDelTerms);
		 */
		flColumn3.addComponent(cbDispatchBy);
		flColumn3.addComponent(tfLrNo);
		flColumn4.addComponent(dfLRDate);
		flColumn4.addComponent(tfDelNOtNo);
		flColumn4.addComponent(dfDelNotDt);
		
		flColumn4.addComponent(cbCarrier);
		flColumn4.addComponent(chkDutyExe);
		flColumn4.addComponent(chkCformReq);
		flColumn4.addComponent(ckPdcRqu);
		flColumn4.addComponent(cbStatus);
		// flColumn5.addComponent(hlquoteDoc);
		HorizontalLayout hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flColumn1);
		hlHdr.addComponent(flColumn2);
		hlHdr.addComponent(flColumn3);
		hlHdr.addComponent(flColumn4);
		hlHdr.addComponent(flColumn5);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding SmsQuoteDtl components
		// Add components for User Input Layout
		flDtlColumn1 = new FormLayout();
		flDtlColumn2 = new FormLayout();
		flDtlColumn3 = new FormLayout();
		flDtlColumn4 = new FormLayout();
		flDtlColumn5 = new FormLayout();
		flDtlColumn1.addComponent(cbproduct);
		flDtlColumn1.addComponent(tfInvoiceQty);
		flDtlColumn2.addComponent(tfUnitRate);
		flDtlColumn2.addComponent(tfBasicValue);
		flDtlColumn3.addComponent(tfCustomField1);
		flDtlColumn3.addComponent(tfCustomField2);
		flDtlColumn4.addComponent(cbDtlStatus);
		flDtlColumn5.addComponent(btnAdd);
		flDtlColumn5.addComponent(btndelete);
		HorizontalLayout hlSmsQuotDtl = new HorizontalLayout();
		hlSmsQuotDtl.addComponent(flDtlColumn1);
		hlSmsQuotDtl.addComponent(flDtlColumn2);
		hlSmsQuotDtl.addComponent(flDtlColumn3);
		hlSmsQuotDtl.addComponent(flDtlColumn4);
		hlSmsQuotDtl.addComponent(flDtlColumn5);
		hlSmsQuotDtl.setSpacing(true);
		hlSmsQuotDtl.setMargin(true);
		VerticalLayout vlSmsQuoteHDR = new VerticalLayout();
		vlSmsQuoteHDR = new VerticalLayout();
		vlSmsQuoteHDR.addComponent(hlSmsQuotDtl);
		vlSmsQuoteHDR.addComponent(tblInvoicDtl);
		vlSmsQuoteHDR.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlSmsQuoteHDR, "Invoice Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		VerticalLayout vlQuoteHdrDtl = new VerticalLayout();
		vlQuoteHdrDtl = new VerticalLayout();
		vlQuoteHdrDtl.addComponent(GERPPanelGenerator.createPanel(hlHdr));
		vlQuoteHdrDtl.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		vlQuoteHdrDtl.setSpacing(true);
		vlQuoteHdrDtl.setWidth("100%");
		hlUserInputLayout.addComponent(vlQuoteHdrDtl);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		btnAdd.setStyleName("add");
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<SmsInvoiceHdrDM> inviceHdrList = new ArrayList<SmsInvoiceHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbBranch.getValue() + ", " + cbStatus.getValue());
		inviceHdrList = serviceInvoiceHdr.getSmsInvoiceHeaderList(null, (Long) cbClient.getValue(),
				(Long) cbBranch.getValue(), (String) cbStatus.getValue(), (Long) cbPONumber.getValue(),
				tfInvNo.getValue(), null, "F");
		recordCnt = inviceHdrList.size();
		beanInvoiceHdr = new BeanItemContainer<SmsInvoiceHdrDM>(SmsInvoiceHdrDM.class);
		beanInvoiceHdr.addAll(inviceHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanInvoiceHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "invoiceId", "branchName", "clientName", "invoiceNo",
				"status", "lastUpdtDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Client Name", "Invoice No",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("invoiceId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadInvoiceDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblInvoicDtl.setPageLength(3);
			recordCnt = invoiceDtllList.size();
			beanInvoiceDtl = new BeanItemContainer<SmsInvoiceDtlDM>(SmsInvoiceDtlDM.class);
			beanInvoiceDtl.addAll(invoiceDtllList);
			BigDecimal sum = new BigDecimal("0");
			for (SmsInvoiceDtlDM obj : invoiceDtllList) {
				if (obj.getBasicValue() != null) {
					sum = sum.add(obj.getBasicValue());
				}
			}
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(sum.toString());
			tfBasictotal.setReadOnly(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxslap. result set");
			tblInvoicDtl.setContainerDataSource(beanInvoiceDtl);
			tblInvoicDtl.setVisibleColumns(new Object[] { "productName", "productUom", "invoiceQty", "unitRate",
					"basicValue", "cusProdCode", "invoDtlStatus", "lastUpdtDate", "lastUpdatedBy" });
			tblInvoicDtl.setColumnHeaders(new String[] { "Product Name", "UOM", "Invoice qty", "UnitRate",
					"Basic Value", "Product Code", "Status", "Last Updated Date", "Last Updated By" });
			tblInvoicDtl.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadBranchList() {
		try {
			List<BranchDM> branchList = serviceBranch.getBranchList(null, null, null, "Active", companyid, "P");
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(branchList);
			cbBranch.setContainerDataSource(beanbranch);
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
					"SM_UOM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbUom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadProduct() {
		try {
			List<SmsPODtlDM> getPurchaseOrdDtl = new ArrayList<SmsPODtlDM>();
			getPurchaseOrdDtl.addAll(serviceWrkOrdDtl.getPurchaseOrdDtlList((Long) cbPONumber.getValue()));
			BeanItemContainer<SmsPODtlDM> beanPurchaseOrdDtl = new BeanItemContainer<SmsPODtlDM>(SmsPODtlDM.class);
			beanPurchaseOrdDtl.addAll(getPurchaseOrdDtl);
			cbproduct.setContainerDataSource(beanPurchaseOrdDtl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadClientList() {
		Long clientid = serviceEnqHeader
				.getSmsEnqHdrList(null, Long.valueOf(cbEnqNumber.getValue().toString()), null, null, null, "F", null,
						null).get(0).getClientId();
		List<ClientDM> getClientList = new ArrayList<ClientDM>();
		getClientList.addAll(serviceClient.getClientDetails(companyid, clientid, null, null, null, null, null, null,
				null, "P"));
		BeanContainer<Long, ClientDM> beanClient = new BeanContainer<Long, ClientDM>(ClientDM.class);
		beanClient.setBeanIdProperty("clientId");
		beanClient.addAll(getClientList);
		cbClient.setContainerDataSource(beanClient);
		cbClient.setValue(clientid);
	}
	
	private void loadPoNo() {
		List<SmsPOHdrDM> getPurchseList = new ArrayList<SmsPOHdrDM>();
		getPurchseList.addAll(servicePurchaseOrdHdr.getSmspohdrList(null, null, companyid, null, null, null, null, "F",
				Long.valueOf(cbEnqNumber.getValue().toString())));
		BeanContainer<Long, SmsPOHdrDM> beanPurchaseOrdHdr = new BeanContainer<Long, SmsPOHdrDM>(SmsPOHdrDM.class);
		beanPurchaseOrdHdr.setBeanIdProperty("poid");
		beanPurchaseOrdHdr.addAll(getPurchseList);
		cbPONumber.setContainerDataSource(beanPurchaseOrdHdr);
	}
	
	public void loadPaymentTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading PaymentTerms Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_PAYTRM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbpaymetTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadFreightTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading FreightTerms Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_FRTTRM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbFreightTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadWarentyTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading WarentyTerms Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_WRNTRM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbWarrentyTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadDeliveryTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading DeliveryTerms Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_DELTRM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbDelTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadGoodCarrier() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading GoodCarrier Search...");
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_CARRIER");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbCarrier.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void editInvoiceHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected invoiceId -> "
				+ invoiceId);
		if (sltedRcd != null) {
			SmsInvoiceHdrDM editSmsInvoice = beanInvoiceHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			invoiceId = editSmsInvoice.getInvoiceId();
			invoicetype=editSmsInvoice.getInvoiceType();
			if (editSmsInvoice.getInvoiceType() != null) {
				ogInvoiceType.setValue(editSmsInvoice.getInvoiceType());
			}
			cbBranch.setValue(editSmsInvoice.getBranchId());
			cbPONumber.setValue(editSmsInvoice.getPoId());
			if (editSmsInvoice.getInvoiceNo() != null) {
				tfInvNo.setReadOnly(false);
				tfInvNo.setValue(editSmsInvoice.getInvoiceNo());
				tfInvNo.setReadOnly(true);
			}
			if (editSmsInvoice.getEnquiryId() != null) {
				cbEnqNumber.setValue(editSmsInvoice.getEnquiryId());
			}
			if (editSmsInvoice.getClientId() != null) {
				cbClient.setValue(editSmsInvoice.getClientId());
			}
			if (editSmsInvoice.getPoId() != null) {
				cbPONumber.setValue(editSmsInvoice.getPoId());
			}
			dfInvoiceDt.setValue(editSmsInvoice.getInvoiceDate());
			dfprpnDt.setValue(editSmsInvoice.getPrepnDtTime());
			dfShipmntDt.setValue(editSmsInvoice.getShipmntDtTime());
			if (editSmsInvoice.getDespatchedBy() != null) {
				cbDispatchBy.setValue(editSmsInvoice.getDespatchedBy().toString());
			}
			if (editSmsInvoice.getCarrier() != null) {
				cbCarrier.setValue(editSmsInvoice.getCarrier().toString());
			}
			if (editSmsInvoice.getLrNo() != null) {
				tfLrNo.setValue(editSmsInvoice.getLrNo());
			}
			dfLRDate.setValue(editSmsInvoice.getLrDate());
			dfDelNotDt.setValue(editSmsInvoice.getDeliveryNoteDt());
			dfedd.setValue(editSmsInvoice.getEdd());

			if (editSmsInvoice.getDeliveryNoteNo() != null) {
				tfDelNOtNo.setValue(editSmsInvoice.getDeliveryNoteNo());
			}
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(editSmsInvoice.getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfpackingPer.setValue(editSmsInvoice.getPackingPrcnt().toString());
			tfPaclingValue.setReadOnly(false);
			tfPaclingValue.setValue(editSmsInvoice.getPackinValue().toString());
			tfPaclingValue.setReadOnly(true);
			if (editSmsInvoice.getDiscountPercent() != null) {
				tfDiscountPer.setValue(editSmsInvoice.getDiscountPercent().toString());
			}
			if (editSmsInvoice.getDiscountValue() != null) {
				tfDiscountValue.setValue(editSmsInvoice.getDiscountValue().toString());
			}
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(editSmsInvoice.getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(editSmsInvoice.getVatPrcnt().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(editSmsInvoice.getVatValue().toString());
			tfVatValue.setReadOnly(true);
			tfEDPer.setValue(editSmsInvoice.getEdPrcnt().toString());
			tfEDValue.setReadOnly(false);
			tfEDValue.setValue(editSmsInvoice.getEdValue().toString());
			tfEDValue.setReadOnly(true);
			tfHEDPer.setValue(editSmsInvoice.getHedPrcnt().toString());
			tfHEDValue.setReadOnly(false);
			tfHEDValue.setValue(editSmsInvoice.getHedValue().toString());
			tfHEDValue.setReadOnly(true);
			tfCessPer.setValue(editSmsInvoice.getCessPrcnt().toString());
			tfCessValue.setReadOnly(false);
			tfCessValue.setValue(editSmsInvoice.getCessValue().toString());
			tfCessValue.setReadOnly(true);
			tfCstPer.setValue(editSmsInvoice.getCstPrcnt().toString());
			tfCstValue.setReadOnly(false);
			tfCstValue.setValue(editSmsInvoice.getCstValue().toString());
			tfCstValue.setReadOnly(true);
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(editSmsInvoice.getSubTaxTotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			if(editSmsInvoice.getDisTotal()!=null){
			tfDisToatal.setReadOnly(false);
			tfDisToatal.setValue(editSmsInvoice.getDisTotal().toString());
			}
			if (editSmsInvoice.getPdcChharges() != null) {
				tfPDCCharges.setReadOnly(false);
				
				tfPDCCharges.setValue(editSmsInvoice.getPdcChharges().toString());
			} else {
				tfPDCCharges.setReadOnly(false);

				tfPDCCharges.setValue("0");
			}
			tfFreightPer.setValue(editSmsInvoice.getFreightPrcnt().toString());
			tfFreightValue.setValue(editSmsInvoice.getFreightValue().toString());
			tfOtherPer.setValue((editSmsInvoice.getOtherPrcnt().toString()));
			tfOtherValue.setValue((editSmsInvoice.getOtherValue().toString()));
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(editSmsInvoice.getGrandTotal().toString());
			tfGrandtotal.setReadOnly(true);
			if (editSmsInvoice.getPaymentTerms() != null) {
				cbpaymetTerms.setValue(editSmsInvoice.getPaymentTerms().toString());
			}
			if (editSmsInvoice.getFrightTerms() != null) {
				cbFreightTerms.setValue(editSmsInvoice.getFrightTerms());
			}
			if (editSmsInvoice.getWarrantyTerms() != null) {
				cbWarrentyTerms.setValue(editSmsInvoice.getWarrantyTerms());
			}
			if (editSmsInvoice.getDeliveryTerms() != null) {
				cbDelTerms.setValue(editSmsInvoice.getDeliveryTerms());
			}
			if (editSmsInvoice.getShippingAddress() != null) {
				taShpnAddr.setValue(editSmsInvoice.getShippingAddress());
			}
			if (editSmsInvoice.getInvoiceAddress() != null) {
				taInvoiceOrd.setValue(editSmsInvoice.getInvoiceAddress());
			}
			if (editSmsInvoice.getStatus() != null) {
				cbStatus.setValue(editSmsInvoice.getStatus().toString());
			}
			if (editSmsInvoice.getDutyExempted().equals("Y")) {
				chkDutyExe.setValue(true);
			} else {
				chkDutyExe.setValue(false);
			}
			if (editSmsInvoice.getCformReqd().equals("Y")) {
				chkCformReq.setValue(true);
			} else {
				chkCformReq.setValue(false);
			}
			if (editSmsInvoice.getPdcReqd().equals("Y")) {
				ckPdcRqu.setValue(true);
			} else {
				ckPdcRqu.setValue(false);
			}
			// if (sltedRcd.getItemProperty("quoteDoc").getValue() != null) {
			// byte[] certificate = (byte[]) sltedRcd.getItemProperty("quoteDoc").getValue();
			// UploadDocumentUI test = new UploadDocumentUI(hlquoteDoc);
			// test.displaycertificate(certificate);
			// } else {
			// new UploadDocumentUI(hlquoteDoc);
			// }
			invoiceDtllList = serviceInvoiceDtl.getSmsInvoiceDtlList(null, invoiceId, null);
		}
		loadInvoiceDtl();
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null,
				invoiceId, status);
		comments.loadsrch(true, null, null, null, null, null, null, null, null, null, null, invoiceId, null);
	}
	
	private void editInvoiceDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		Item sltedRcd = tblInvoicDtl.getItem(tblInvoicDtl.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected invoiceId -> "
				+ invoiceId);
		if (sltedRcd != null) {
			SmsInvoiceDtlDM editInvoiceDtlDtllist = beanInvoiceDtl.getItem(tblInvoicDtl.getValue()).getBean();
			Long prdid = editInvoiceDtlDtllist.getProductId();
			Collection<?> productids = cbproduct.getItemIds();
			for (Iterator<?> iterator = productids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbproduct.getItem(itemId);
				// Get the actual bean and use the data
				SmsPODtlDM st = (SmsPODtlDM) item.getBean();
				if (prdid != null && prdid.equals(st.getProductid())) {
					cbproduct.setValue(itemId);
				}
			}
			if (editInvoiceDtlDtllist.getCusProdCode() != null) {
				tfcusProdCode.setValue(editInvoiceDtlDtllist.getCusProdCode().toString());
			}
			if (editInvoiceDtlDtllist.getCusProdDesc() != null) {
				taCustProdDessc.setValue(editInvoiceDtlDtllist.getCusProdDesc());
			}
			if (editInvoiceDtlDtllist.getUnitRate() != null) {
				tfUnitRate.setValue(editInvoiceDtlDtllist.getUnitRate().toString());
			}
			if (editInvoiceDtlDtllist.getInvoiceQty() != null) {
				tfInvoiceQty.setValue(editInvoiceDtlDtllist.getInvoiceQty().toString());
			}
			if (editInvoiceDtlDtllist.getProductUom() != null) {
				cbUom.setValue(editInvoiceDtlDtllist.getProductUom().toString());
			}
			if (editInvoiceDtlDtllist.getBasicValue() != null) {
				tfBasicValue.setReadOnly(false);
				tfBasicValue.setValue(editInvoiceDtlDtllist.getBasicValue().toString());
				tfBasicValue.setReadOnly(true);
			}
			if (editInvoiceDtlDtllist.getInvoDtlStatus() != null) {
				cbDtlStatus.setValue(editInvoiceDtlDtllist.getInvoDtlStatus().toString());
			}
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(null);
		cbPONumber.setValue(null);
		tfInvNo.setValue("");
		cbBranch.setValue(null);
		cbClient.setValue(null);
		tfvendorName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		cbproduct.setRequired(true);
		cbUom.setRequired(true);
		tfvendorName.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		cbBranch.setRequired(true);
		tfvendorName.setRequired(true);
		cbPONumber.setRequired(true);
		tfBasicValue.setRequired(true);
		cbUom.setRequired(true);
		tfUnitRate.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		tfBasictotal.setReadOnly(true);
		tfSubTotal.setReadOnly(true);
		tfSubTaxTotal.setReadOnly(true);
		tfGrandtotal.setReadOnly(true);
		tfVatValue.setReadOnly(true);
		tfEDValue.setReadOnly(true);
		tfHEDValue.setReadOnly(true);
		tfCessValue.setReadOnly(true);
		tfCstValue.setReadOnly(true);
		cbClient.setRequired(true);
		loadInvoiceDtl();
		assembleInputUserLayout();
		new UploadDocumentUI(hlquoteDoc);
		btnAdd.setCaption("Add");
		tblInvoicDtl.setVisible(true);
		lblNotification.setValue("");
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		cbBranch.setRequired(true);
		cbPONumber.setRequired(true);
		tfvendorName.setRequired(true);
		tfBasicValue.setRequired(true);
		cbUom.setRequired(true);
		tfUnitRate.setRequired(true);
		cbClient.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_INVNO ");
		tfInvNo.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfInvNo.setReadOnly(true);
			}
		}
		cbproduct.setRequired(true);
		cbUom.setRequired(true);
		lblNotification.setValue("");
		assembleInputUserLayout();
		resetFields();
		editInvoiceDtl();
		editInvoiceHdr();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		cbPONumber.setComponentError(null);
		cbEnqNumber.setComponentError(null);
		cbClient.setComponentError(null);
		dfedd.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if ((cbEnqNumber.getValue() == null)) {
			cbEnqNumber.setComponentError(new UserError(GERPErrorCodes.ENQUIRY_NO));
			errorFlag = true;
		}
		if (ogInvoiceType.getValue().toString().equalsIgnoreCase("Invoice")) {
			if (cbPONumber.getValue() == null) {
				cbPONumber.setComponentError(new UserError(GERPErrorCodes.PURCAHSE_ORD_NO));
				errorFlag = true;
			}
					}
		if (ogInvoiceType.getValue().toString().equalsIgnoreCase("Proforma Invoice")) {
			if (dfedd.getValue() == null) {
				dfedd.setComponentError(new UserError(GERPErrorCodes.EXP_DAL_DATE));
				errorFlag = true;
			}
		}
		System.out.println("ogInvoiceType.getValue()"+ogInvoiceType.getValue());
if(!ogInvoiceType.getValue().equals("Invoice") && !ogInvoiceType.getValue().equals("Proforma Invoice")){
	ogInvoiceType.setComponentError(new UserError("Please select Invoice Type"));
	errorFlag = true;

}else{
	ogInvoiceType.setComponentError(null);
}
	if ((cbClient.getValue() == null)) {

			cbClient.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validateInvoiceDetails() {
		boolean isValid = true;
		if (cbproduct.getValue() == null) {
			cbproduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			cbproduct.setComponentError(null);
		}
		if (tfBasicValue.getValue() == "0") {
			tfBasicValue.setComponentError(new UserError(GERPErrorCodes.BASIC_VALUE));
			isValid = false;
		} else {
			tfBasicValue.setComponentError(null);
		}
		if (tfUnitRate.getValue() == "0") {
			tfUnitRate.setComponentError(new UserError(GERPErrorCodes.UNIT_RATE));
			isValid = false;
		} else {
			tfUnitRate.setComponentError(null);
		}
		/*
		 * if (cbUom.getValue() == null) { cbUom.setComponentError(new UserError(GERPErrorCodes.PRODUCT_UOM)); isValid =
		 * false; } else { cbUom.setComponentError(null); }
		 */
		return isValid;
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			SmsInvoiceHdrDM smsInvoiceHdrobj = new SmsInvoiceHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				smsInvoiceHdrobj = beanInvoiceHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (tfInvNo.getValue() != null) {
				smsInvoiceHdrobj.setInvoiceNo(tfInvNo.getValue());
			}
			invoicetype=(String) ogInvoiceType.getValue();
			smsInvoiceHdrobj.setInvoiceType((String) ogInvoiceType.getValue());
			smsInvoiceHdrobj.setBranchId((Long) cbBranch.getValue());
			smsInvoiceHdrobj.setClientId((Long) cbClient.getValue());
			smsInvoiceHdrobj.setInvoiceDate(dfInvoiceDt.getValue());
			smsInvoiceHdrobj.setPrepnDtTime(dfprpnDt.getValue());
			smsInvoiceHdrobj.setShipmntDtTime(dfShipmntDt.getValue());
			smsInvoiceHdrobj.setCompanyId(companyid);
			if (cbPONumber.getValue() != null) {
				smsInvoiceHdrobj.setPoId(((Long) cbPONumber.getValue()));
			}
			if (cbEnqNumber.getValue() != null) {
				smsInvoiceHdrobj.setEnquiryId(((Long) cbEnqNumber.getValue()));
			}
			if (tfBasictotal.getValue() != null && tfBasictotal.getValue().trim().length() > 0) {
				smsInvoiceHdrobj.setBasicTotal(new BigDecimal(tfBasictotal.getValue()));
			}
			smsInvoiceHdrobj.setPackingPrcnt((new BigDecimal(tfpackingPer.getValue())));
			if (tfPaclingValue.getValue() != null && tfPaclingValue.getValue().trim().length() > 0) {
				smsInvoiceHdrobj.setPackinValue(new BigDecimal(tfPaclingValue.getValue()));
			}
			smsInvoiceHdrobj.setDiscountPercent((new BigDecimal(tfDiscountPer.getValue())));
			if (tfDiscountValue.getValue() != null && tfDiscountValue.getValue().trim().length() > 0) {
				smsInvoiceHdrobj.setDiscountValue(new BigDecimal(tfDiscountValue.getValue()));
			}
			smsInvoiceHdrobj.setSubTotal(new BigDecimal(tfSubTotal.getValue()));
			smsInvoiceHdrobj.setVatPrcnt((new BigDecimal(tfVatPer.getValue())));
			if (tfVatValue.getValue() != null && tfVatValue.getValue().trim().length() > 0) {
				smsInvoiceHdrobj.setVatValue((new BigDecimal(tfVatValue.getValue())));
			}
			if (tfEDPer.getValue() != null && tfEDPer.getValue().trim().length() > 0) {
				smsInvoiceHdrobj.setEdPrcnt((new BigDecimal(tfEDPer.getValue())));
			}
			if (tfEDValue.getValue() != null && tfEDValue.getValue().trim().length() > 0) {
				smsInvoiceHdrobj.setEdValue(new BigDecimal(tfEDValue.getValue()));
			}
			smsInvoiceHdrobj.setHedValue(new BigDecimal(tfHEDValue.getValue()));
			smsInvoiceHdrobj.setHedPrcnt((new BigDecimal(tfHEDPer.getValue())));
			smsInvoiceHdrobj.setCessPrcnt((new BigDecimal(tfCessPer.getValue())));
			smsInvoiceHdrobj.setCessValue(new BigDecimal(tfCessValue.getValue()));
			smsInvoiceHdrobj.setCstPrcnt((new BigDecimal(tfCstPer.getValue())));
			if (tfCstValue.getValue() != null && tfCstValue.getValue().trim().length() > 0) {
				smsInvoiceHdrobj.setCstValue((new BigDecimal(tfCstValue.getValue())));
			}
			smsInvoiceHdrobj.setSubTaxTotal(new BigDecimal(tfSubTaxTotal.getValue()));
			smsInvoiceHdrobj.setFreightPrcnt(new BigDecimal(tfFreightPer.getValue()));
			smsInvoiceHdrobj.setFreightValue(new BigDecimal(tfFreightValue.getValue()));
			smsInvoiceHdrobj.setOtherPrcnt(new BigDecimal(tfOtherPer.getValue()));
			smsInvoiceHdrobj.setOtherValue(new BigDecimal(tfOtherValue.getValue()));
			smsInvoiceHdrobj.setPdcChharges(new BigDecimal(tfPDCCharges.getValue()));
			if(tfDisToatal.getValue()!=null){

			smsInvoiceHdrobj.setDisTotal(new BigDecimal(tfDisToatal.getValue()));}
			if(tfPDCCharges.getValue()!=null){

			smsInvoiceHdrobj.setPdcChharges(new BigDecimal(tfPDCCharges.getValue()));}
			smsInvoiceHdrobj.setGrandTotal(new BigDecimal(tfGrandtotal.getValue()));
			if (cbpaymetTerms.getValue() != null) {
				smsInvoiceHdrobj.setPaymentTerms((cbpaymetTerms.getValue().toString()));
			}
			if (cbFreightTerms.getValue() != null) {
				smsInvoiceHdrobj.setFrightTerms(cbFreightTerms.getValue().toString());
			}
			if (cbWarrentyTerms.getValue() != null) {
				smsInvoiceHdrobj.setWarrantyTerms((cbWarrentyTerms.getValue().toString()));
			}
			if (cbDelTerms.getValue() != null) {
				smsInvoiceHdrobj.setDeliveryTerms(cbDelTerms.getValue().toString());
			}
			smsInvoiceHdrobj.setInvoiceAddress(taInvoiceOrd.getValue());
			smsInvoiceHdrobj.setShippingAddress(taShpnAddr.getValue());
			if (chkDutyExe.getValue().equals(true)) {
				smsInvoiceHdrobj.setDutyExempted("Y");
			} else if (chkDutyExe.getValue().equals(false)) {
				smsInvoiceHdrobj.setDutyExempted("N");
			}
			if (chkCformReq.getValue().equals(true)) {
				smsInvoiceHdrobj.setCformReqd("Y");
			} else if (chkCformReq.getValue().equals(false)) {
				smsInvoiceHdrobj.setCformReqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				smsInvoiceHdrobj.setPdcReqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				smsInvoiceHdrobj.setPdcReqd("N");
			}
			if (cbStatus.getValue() != null) {
				smsInvoiceHdrobj.setStatus(cbStatus.getValue().toString());
			}
			smsInvoiceHdrobj.setDespatchedBy((String) cbDispatchBy.getValue());
			if (cbCarrier.getValue() != null) {
				smsInvoiceHdrobj.setCarrier(cbCarrier.getValue().toString());
			}
			smsInvoiceHdrobj.setLrNo(tfLrNo.getValue());
			smsInvoiceHdrobj.setLrDate(dfLRDate.getValue());
			smsInvoiceHdrobj.setDeliveryNoteNo(tfDelNOtNo.getValue());
			smsInvoiceHdrobj.setDeliveryNoteDt(dfDelNotDt.getValue());
			smsInvoiceHdrobj.setEdd(dfedd.getValue());

			smsInvoiceHdrobj.setPreparedBy(EmployeeId);
			smsInvoiceHdrobj.setReviewedBy(null);
			smsInvoiceHdrobj.setActionedBy(null);
			smsInvoiceHdrobj.setLastUpdtDate(DateUtils.getcurrentdate());
			smsInvoiceHdrobj.setLastUpdatedBy(username);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			smsInvoiceHdrobj.setInvoiceDocumet(fileContents);
			validateInvoiceDetails();
			serviceInvoiceHdr.saveOrUpdateSmsInvoiceHeader(smsInvoiceHdrobj);
			@SuppressWarnings("unchecked")
			Collection<SmsInvoiceDtlDM> itemIds = (Collection<SmsInvoiceDtlDM>) tblInvoicDtl.getVisibleItemIds();
			for (SmsInvoiceDtlDM save : (Collection<SmsInvoiceDtlDM>) itemIds) {
				save.setInvoiceId(Long.valueOf(smsInvoiceHdrobj.getInvoiceId().toString()));
				serviceInvoiceDtl.saveOrUpdateSmsInvoiceDtl(save);
				Long invoicedqty=save.getInvoiceQty();
				servicesmspodtl.updateInvoiceOrderQty(((Long) cbPONumber.getValue()), save.getProductId(), invoicedqty);
			}
			comments.saveInvoice(smsInvoiceHdrobj.getInvoiceId(), smsInvoiceHdrobj.getStatus());
			if (tblMstScrSrchRslt.getValue() == null) {
				if (ogInvoiceType.getValue().toString().equalsIgnoreCase("Invoice")) {
					try {
						SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_INVNO")
								.get(0);
						if (slnoObj.getAutoGenYN().equals("Y")) {
							serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_INVNO");
						}
					}
					catch (Exception e) {
					}
				} else {
					try {
						SlnoGenDM slnoObj = serviceSlnogen
								.getSequenceNumber(companyid, branchId, moduleId, "SM_PINVNO").get(0);
						if (slnoObj.getAutoGenYN().equals("Y")) {
							serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_PINVNO");
						}
					}
					catch (Exception e) {
					}
				}
			}
			resetInvoiceDetails();
			loadSrchRslt();
			invoiceId = 0L;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void saveInvoiceDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			SmsInvoiceDtlDM invoiceDtlobj = new SmsInvoiceDtlDM();
			if (tblInvoicDtl.getValue() != null) {
				invoiceDtlobj = beanInvoiceDtl.getItem(tblInvoicDtl.getValue()).getBean();
				invoiceDtllList.remove(invoiceDtlobj);
			}
			invoiceDtlobj.setProductId(((SmsPODtlDM) cbproduct.getValue()).getProductid());
			invoiceDtlobj.setProductName(((SmsPODtlDM) cbproduct.getValue()).getProdname());
			if (tfUnitRate.getValue() != null && tfUnitRate.getValue().trim().length() > 0) {
				invoiceDtlobj.setUnitRate((Long.valueOf(tfUnitRate.getValue())));
			}
			if (tfInvoiceQty.getValue() != null && tfInvoiceQty.getValue().trim().length() > 0) {
				invoiceDtlobj.setInvoiceQty((Long.valueOf(tfInvoiceQty.getValue())));
			}
			invoiceDtlobj.setProductUom(((SmsPODtlDM) cbproduct.getValue()).getProduom());
			if (tfBasicValue.getValue() != null && tfBasicValue.getValue().trim().length() > 0) {
				invoiceDtlobj.setBasicValue(new BigDecimal(tfBasicValue.getValue()));
			}
			if (cbDtlStatus.getValue() != null) {
				invoiceDtlobj.setInvoDtlStatus(cbDtlStatus.getValue().toString());
			}
			invoiceDtlobj.setCusProdCode(tfcusProdCode.getValue());
			invoiceDtlobj.setCusProdDesc(taCustProdDessc.getValue());
			invoiceDtlobj.setLastUpdtDate(DateUtils.getcurrentdate());
			invoiceDtlobj.setLastUpdatedBy(username);
			invoiceDtllList.add(invoiceDtlobj);
			loadInvoiceDtl();
			getCalculatedValues();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		resetInvoiceDetails();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for TestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_P_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", invoiceaId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		cbproduct.setRequired(false);
		cbBranch.setRequired(false);
		cbPONumber.setRequired(false);
		tfvendorName.setRequired(false);
		tfBasicValue.setRequired(false);
		cbUom.setRequired(false);
		tfUnitRate.setRequired(false);
		cbClient.setRequired(false);
		resetFields();
		resetInvoiceDetails();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEnqNumber.setValue(null);
		cbPONumber.setValue(null);
		ogInvoiceType.setValue(null);
		dfprpnDt.setValue(DateUtils.getcurrentdate());
		dfShipmntDt.setValue(null);
		tfLrNo.setValue("");
		dfLRDate.setValue(null);
		cbCarrier.setValue(null);
		tfBasictotal.setReadOnly(false);
		tfBasictotal.setValue("0");
		try {
			tfCessPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CESS", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCessPer.setValue("0");
		}
		chkCformReq.setValue(false);
		try {
			tfCstPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CST", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCstPer.setValue("0");
		}
		tfCessValue.setReadOnly(false);
		tfCessValue.setValue("0");
		tfCstPer.setValue("10");
		cbDelTerms.setValue(null);
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue("0");
		chkDutyExe.setValue(false);
		try {
			tfEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "ED", "Active", "F").get(0).getTaxprnct()
					.toString());
		}
		catch (Exception e) {
			tfEDPer.setValue("0");
		}
		tfEDValue.setReadOnly(false);
		tfEDValue.setValue("0");
		cbWarrentyTerms.setValue(null);
		tfVatValue.setReadOnly(false);
		tfVatValue.setValue("0");
		try {
			tfVatPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "VAT", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfVatPer.setValue("0");
		}
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue("0");
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue("0");
		tfInvNo.setReadOnly(false);
		tfInvNo.setValue("");
		ckPdcRqu.setValue(false);
		cbpaymetTerms.setValue(null);
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue("0");
		tfpackingPer.setValue("0");
		tfDiscountPer.setValue("0");
		tfDiscountValue.setValue("0");
		tfOtherValue.setValue("0");
		try {
			tfOtherPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "OTHER", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfOtherPer.setValue("0");
		}
		tfHEDValue.setReadOnly(false);
		tfHEDValue.setValue("0");
		try {
			tfHEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "HED", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfHEDPer.setValue("0");
		}
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue("0");
		tfFreightValue.setValue("0");
		try {
			tfFreightPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "FREIGHT", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfFreightPer.setValue("0");
		}
		tfPDCCharges.setReadOnly(false);
		tfPDCCharges.setValue("0");
		tfPDCCharges.setReadOnly(true);
		tfDocumentCharges.setValue("0");
		cbFreightTerms.setValue(null);
		cbStatus.setValue(null);
		cbBranch.setValue(null);
		dfInvoiceDt.setValue(null);
		cbBranch.setComponentError(null);
		cbPONumber.setComponentError(null);
		cbDelTerms.setValue(null);
		cbpaymetTerms.setValue(null);
		cbWarrentyTerms.setValue(null);
		cbFreightTerms.setValue(null);
		invoiceDtllList = new ArrayList<SmsInvoiceDtlDM>();
		tblInvoicDtl.removeAllItems();
		new UploadDocumentUI(hlquoteDoc);
		tfvendorName.setValue("");
		cbStatus.setValue(null);
		cbClient.setValue(null);
		cbClient.setComponentError(null);
		cbproduct.setContainerDataSource(null);
		tfDelNOtNo.setValue("");
		dfDelNotDt.setValue(null);
		dfedd.setValue(null);

		cbDispatchBy.setValue(null);
		tfDisToatal.setValue("0");
		loadProduct();
	}
	
	private void deletesmsinDetails() {
		SmsInvoiceDtlDM smsinvoicehdr = new SmsInvoiceDtlDM();
		if (tblInvoicDtl.getValue() != null) {
			smsinvoicehdr = beanInvoiceDtl.getItem(tblInvoicDtl.getValue()).getBean();
			invoiceDtllList.remove(smsinvoicehdr);
			resetInvoiceDetails();
			tblInvoicDtl.setValue("");
			loadInvoiceDtl();
			btndelete.setEnabled(false);
		}
	}
	
	protected void resetInvoiceDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbproduct.setValue(null);
		cbproduct.setComponentError(null);
		cbUom.setValue(null);
		cbUom.setComponentError(null);
		tfBasicValue.setReadOnly(false);
		tfBasicValue.setValue("0");
		tfBasicValue.setReadOnly(true);
		tfBasicValue.setComponentError(null);
		tfUnitRate.setValue("0");
		tfUnitRate.setComponentError(null);
		tfcusProdCode.setValue("");
		taCustProdDessc.setValue("");
		tfCustomField1.setValue("");
		tfCustomField2.setValue("");
		tfInvoiceQty.setValue("0");
		cbDtlStatus.setValue(null);
		cbDtlStatus.setComponentError(null);
		btnAdd.setCaption("Add");
	}
	
	private void getCalculatedValues() {
		if (chkCformReq.getValue()) {
			tfVatPer.setValue("0");
			try {
			/*	tfCstPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CST", "Active", "F").get(0)
						.getTaxprnct().toString());*/
			}
			catch (Exception e) {
				e.printStackTrace();
				tfCstPer.setValue("0");
			}
		} else {
			tfCstPer.setValue("0");
			try {
			/*	tfVatPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "VAT", "Active", "F").get(0)
						.getTaxprnct().toString());*/
			}
			catch (Exception e) {
				e.printStackTrace();
				tfVatPer.setValue("0");
			}
		}
		if (chkDutyExe.getValue()) {
			tfEDPer.setValue("0");
			tfHEDPer.setValue("0");
			tfCessPer.setValue("0");
		} else {
			try {
				/*tfHEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "HED", "Active", "F").get(0)
						.getTaxprnct().toString());*/
			}
			catch (Exception e) {
				tfHEDPer.setValue("0");
			}
			try {
			/*	tfEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "ED", "Active", "F").get(0)
						.getTaxprnct().toString());*/
			}
			catch (Exception e) {
				tfEDPer.setValue("0");
			}
			try {
				/*tfCessPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CESS", "Active", "F").get(0)
						.getTaxprnct().toString());*/
			}
			catch (Exception e) {
				tfCessPer.setValue("0");
			}
		}
		BigDecimal basictotal = new BigDecimal(tfBasictotal.getValue());
		if (!tfDiscountPer.getValue().equals("0")) {
			BigDecimal discountamt = gerPercentageValue(new BigDecimal(tfDiscountPer.getValue()), basictotal);
			tfDiscountValue.setValue(discountamt.toString());
		}else {
			tfDiscountValue.setValue("0");

		}
		BigDecimal discountTotal = basictotal.subtract(new BigDecimal(tfDiscountValue.getValue()));
		tfDisToatal.setValue(discountTotal.toString());
		BigDecimal packingvalue = gerPercentageValue(new BigDecimal(tfpackingPer.getValue()), discountTotal);
		tfPaclingValue.setReadOnly(false);
		tfPaclingValue.setValue(packingvalue.toString());
		tfPaclingValue.setReadOnly(true);
		BigDecimal pdcCharges = new BigDecimal("0");
		try {
			pdcCharges = new BigDecimal(tfPDCCharges.getValue());
		}
		catch (Exception e) {
		}
		BigDecimal subtotal = packingvalue.add(discountTotal).add(pdcCharges);
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue(subtotal.toString());
		tfSubTotal.setReadOnly(true);
		BigDecimal edValue = gerPercentageValue(new BigDecimal(tfEDPer.getValue()), subtotal);
		tfEDValue.setReadOnly(false);
		tfEDValue.setValue(edValue.toString());
		tfEDValue.setReadOnly(true);
	
		BigDecimal subtaxTotal = subtotal.add(new BigDecimal(tfEDValue.getValue()));
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue(subtaxTotal.toString());
		tfSubTaxTotal.setReadOnly(true);
		BigDecimal vatvalue = gerPercentageValue(new BigDecimal(tfVatPer.getValue()), subtaxTotal);
		tfVatValue.setReadOnly(false);
		tfVatValue.setValue(vatvalue.toString());
		tfVatValue.setReadOnly(true);
		
		BigDecimal cstval = gerPercentageValue(new BigDecimal(tfCstPer.getValue()), subtaxTotal);
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue(cstval.toString());
		tfCstValue.setReadOnly(true);
		BigDecimal csttotal = vatvalue;
		BigDecimal frgval = new BigDecimal(0);
		BigDecimal otherval = new BigDecimal(0);
		if (!tfFreightPer.getValue().equals("0")) {

		 frgval = gerPercentageValue(new BigDecimal(tfFreightPer.getValue()), subtaxTotal);
		tfFreightValue.setValue(frgval.toString());}else {
			tfFreightValue.setValue("0");
			frgval=new BigDecimal(tfFreightValue.getValue());
			
		}
		if (!tfOtherPer.getValue().equals("0")) {

		 otherval = gerPercentageValue(new BigDecimal(tfOtherPer.getValue()), subtaxTotal);
			tfOtherValue.setValue(otherval.toString());
}else {
	tfOtherValue.setValue("0");
otherval=new BigDecimal(tfOtherValue.getValue());
}
		BigDecimal grand = frgval.add(otherval).add(cstval).add(csttotal);
		BigDecimal documentCharges = new BigDecimal(tfDocumentCharges.getValue());
		BigDecimal grandTotal = subtaxTotal.add(grand).add(documentCharges);
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue(grandTotal.toString());
		tfGrandtotal.setReadOnly(true);
	}
	
	private BigDecimal gerPercentageValue(BigDecimal percent, BigDecimal value) {
		return (percent.multiply(value).divide(new BigDecimal("100"))).setScale(2, RoundingMode.CEILING);
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, Long> parameterMap = new HashMap<String, Long>();
			parameterMap.put("INVHDR", invoiceId);
			Report rpt = new Report(parameterMap, connection);
			System.out.println("invoicetype----------------->"+invoicetype);
			if(invoicetype.equals("Invoice")){
			rpt.setReportName(basepath + "/WEB-INF/reports/invoice_Report1"); // productlist is the name of my jasper
			}else if(invoicetype.equals("Proforma Invoice")){
				rpt.setReportName(basepath + "/WEB-INF/reports/performaInvoiceReport"); // productlist is the name of my jasper

			}
			// file.
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				statement.close();
				Database.close(connection);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Load EnquiryNo
	private void loadEnquiryNo() {
		List<SmsEnqHdrDM> getsmsEnqNoHdr = new ArrayList<SmsEnqHdrDM>();
		getsmsEnqNoHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
		BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("enquiryId");
		beansmsenqHdr.addAll(getsmsEnqNoHdr);
		cbEnqNumber.setContainerDataSource(beansmsenqHdr);
	}
}
