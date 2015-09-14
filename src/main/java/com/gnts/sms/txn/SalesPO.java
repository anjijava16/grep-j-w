/**
 * File Name 		:	SalesPO.java 
 * Description 		:	This Screen Purpose for Modify the SalesPO Details.Add the SalesPO details process should be directly added in DB.
 * Author 			: 	sudhakar 
 * Date 			: 	Oct 10, 2014

 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version    Date           Modified By    Remarks
 * 0.1        Oct 10, 2014   sudhakar       Initial  Version
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
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsEnquiryDtlDM;
import com.gnts.sms.domain.txn.SmsPOAcceptanceDM;
import com.gnts.sms.domain.txn.SmsPODtlDM;
import com.gnts.sms.domain.txn.SmsPOHdrDM;
import com.gnts.sms.domain.txn.SmsQuoteDtlDM;
import com.gnts.sms.domain.txn.SmsQuoteHdrDM;
import com.gnts.sms.service.mst.SmsTaxesService;
import com.gnts.sms.service.txn.SmsCommentsService;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsEnquiryDtlService;
import com.gnts.sms.service.txn.SmsPOAcceptanceService;
import com.gnts.sms.service.txn.SmsPODtlService;
import com.gnts.sms.service.txn.SmsPOHdrService;
import com.gnts.sms.service.txn.SmsQuoteDtlService;
import com.gnts.sms.service.txn.SmsQuoteHdrService;
import com.vaadin.data.Container;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SalesPO extends BaseTransUI  {
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private SmsCommentsService serviceComment = (SmsCommentsService) SpringContextHelper.getBean("smsComments");
	private SmsPOHdrService servicesmsPOHdr = (SmsPOHdrService) SpringContextHelper.getBean("smspohdr");
	private SmsPODtlService servicesmspodtl = (SmsPODtlService) SpringContextHelper.getBean("SmsPODtl");
	private SmsEnqHdrService serviceEnquiryHdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private SmsQuoteDtlService serviceQuoteDtl = (SmsQuoteDtlService) SpringContextHelper.getBean("SmsQuoteDtl");
	private SmsPOAcceptanceService servicePOAcceptance = (SmsPOAcceptanceService) SpringContextHelper
			.getBean("SmsPOAcceptance");
	private SmsEnquiryDtlService serviceEnqdtl = (SmsEnquiryDtlService) SpringContextHelper.getBean("SmsEnquiryDtl");
	private SmsTaxesService serviceTaxes = (SmsTaxesService) SpringContextHelper.getBean("SmsTaxes");
	private SmsQuoteHdrService serviceQuoteHdr = (SmsQuoteHdrService) SpringContextHelper.getBean("smsquotehdr");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	// form layout for input controls for PO Header
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// form layout for input controls for PO Details
	private FormLayout flDtlColumn1, flDtlColumn2, flDtlColumn3, flDtlColumn4, flDtlColumn5;
	// // User Input Components for PO Details
	private ComboBox cbBranch, cbStatus, cbPOType, cbQuoteNo, cbEnquiryNumber;
	private ComboBox cbPaymetTerms, cbFreightTerms, cbWarrentyTerms, cbDelTerms, cbClient;
	private TextField tfVersionNo, tfBasictotal, tfPackingPer, tfPackingValue, tfPONumber;
	private TextField tfSubTotal, tfVatPer, tfVatValue, tfEDPer, tfEDValue, tfHEDPer;
	private TextField tfHEDValue, tfCessPer, tfCessValue, tfCstPer, tfCstValue, tfSubTaxTotal;
	private TextField tfFreightValue, tfOtherValue, tfGrandtotal, tfDocumentCharges, tfPDCCharges, tfDiscountPer,
			tfDiscountValue;
	private GERPTextField tfDisToatal = new GERPTextField("Total");
	private TextArea taRemark, taInvoiceAddr, taShipmentAddr, taLiquidatedDamage;
	private CheckBox chkDutyExe, ckPdcRqu, chkCformReq, ckCashPO;
	private PopupDateField dfPODt;
	private Button btnsavepurQuote = new GERPButton("Add", "addbt", this);
	private VerticalLayout hlPODoc = new VerticalLayout();
	// PODtl components
	private ComboBox cbProduct, cbProdUOM, cbPODtlStatus;
	private TextField tfQuoteQty, tfUnitRate, tfBasicValue, tfcustprodcode, tfOrderQty, tfdtlPDC;
	private TextArea taCustProddesc;
	private GERPTextField tfCustomField1 = new GERPTextField("Part Number");
	private GERPTextField tfCustomField2 = new GERPTextField("Drawing Number");
	private Button btnAccept = new GERPButton("Add", "addbt", this);
	private SmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	private static final long serialVersionUID = 1L;
	private Table tblSmsAccept = new GERPTable();
	// BeanItem container
	private BeanItemContainer<SmsPOHdrDM> beansmsPOHdr = null;
	private BeanItemContainer<SmsPODtlDM> beansmsPODtl = null;
	private BeanItemContainer<SmsPOAcceptanceDM> beanpoaccept = null;
	private List<SmsPODtlDM> listPODetails = new ArrayList<SmsPODtlDM>();
	private List<SmsPOAcceptanceDM> listPOAccept = new ArrayList<SmsPOAcceptanceDM>();
	// local variables declaration
	private String username;
	private Long companyid;
	private int recordCnt;
	private Long employeeId;
	private File file;
	private Long roleId;
	private Long branchId;
	private Long moduleId;
	private Long appScreenId;
	private Long branchID;
	private Long poid;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private GERPTable tblSmsPODtl;
	private String status;
	// Initialize logger
	private Logger logger = Logger.getLogger(SalesPO.class);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	
	// Constructor received the parameters from Login UI class
	public SalesPO() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PurchasePO() constructor");
		// Loading the PO UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax UI");
		// Initialization for PurchasePO Details user input components
		cbQuoteNo = new ComboBox("Quote No");
		cbQuoteNo.setItemCaptionPropertyId("quoteNumber");
		cbQuoteNo.setWidth("150");
		cbQuoteNo.setImmediate(true);
		cbEnquiryNumber = new ComboBox("Enquiry No.");
		cbEnquiryNumber = new ComboBox("Enquiry No");
		cbEnquiryNumber.setItemCaptionPropertyId("enquiryNo");
		cbEnquiryNumber.setWidth("150");
		cbEnquiryNumber.setRequired(true);
		cbClient = new GERPComboBox("Client Name");
		cbClient.setItemCaptionPropertyId("clientName");
		cbClient.setNullSelectionAllowed(true);
		cbClient.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					ClientDM clientDM = serviceClients.getClientDetails(null, (Long) cbClient.getValue(), null, null,
							null, null, null, null, null, "P").get(0);
					if (clientDM.getClientAddress() != null) {
						taInvoiceAddr.setValue(clientDM.getClientAddress());
						taShipmentAddr.setValue(clientDM.getClientAddress());
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// loadClientsDetails();
		loadEnquiryNo();
		cbEnquiryNumber.setImmediate(true);
		cbEnquiryNumber.setImmediate(true);
		cbEnquiryNumber.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnquiryNumber.getItem(itemId);
				if (item != null) {
					loadQuoteNoList();
					loadClientsDetails();
					if (cbQuoteNo.getValue() == null) {
						loadProductListenq();
					}
				}
			}
		});
		cbQuoteNo.setImmediate(true);
		cbQuoteNo.setImmediate(true);
		cbQuoteNo.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbQuoteNo.getItem(itemId);
				System.out.println("sddscds");
				if (cbQuoteNo.getValue() == null && cbEnquiryNumber.getValue() == null) {
					tblSmsPODtl.removeAllItems();
					System.out.println("inininini");
					loadPODetails();
					loadfullProduct();
				}
				if (item != null) {
					loadProductList();
					loadQuoteDetails();
					if (cbQuoteNo.getValue() == null) {
						tblSmsPODtl.removeAllItems();
						loadPODetails();
					}
				}
			}
		});
		taInvoiceAddr = new TextArea("Invoice Addr");
		taInvoiceAddr.setHeight("35");
		taInvoiceAddr.setWidth("150");
		taShipmentAddr = new TextArea("Shipping Addr");
		taShipmentAddr.setHeight("35");
		taShipmentAddr.setWidth("150");
		dfPODt = new GERPPopupDateField("Purchase Ord Date");
		dfPODt.setInputPrompt("Select Date");
		dfPODt.setWidth("150");
		tfPONumber = new TextField("PO. Number");
		tfPONumber.setWidth("150");
		tfPONumber.setRequired(true);
		taRemark = new TextArea("Remarks");
		taRemark.setHeight("50");
		taRemark.setWidth("150");
		taRemark.setNullRepresentation("");
		taLiquidatedDamage = new TextArea("Liquidated Damage");
		taLiquidatedDamage.setHeight("25");
		taLiquidatedDamage.setWidth("150");
		taLiquidatedDamage.setNullRepresentation("");
		tfVersionNo = new TextField(" Version No");
		tfVersionNo.setWidth("150");
		tfBasictotal = new GERPNumberField("Basic total");
		tfBasictotal.setWidth("150");
		tfPackingPer = new TextField();
		tfPackingPer.setWidth("30");
		tfPackingValue = new GERPNumberField();
		tfPackingValue.setWidth("124");
		tfSubTotal = new GERPNumberField("Sub Total");
		tfSubTotal.setWidth("150");
		tfVatPer = new TextField();
		tfVatPer.setWidth("30");
		tfVatValue = new GERPNumberField();
		tfVatValue.setWidth("124");
		tfEDPer = new TextField();
		tfEDPer.setWidth("30");
		tfEDValue = new GERPNumberField();
		tfEDValue.setWidth("124");
		tfHEDPer = new TextField();
		tfHEDPer.setWidth("30");
		tfHEDValue = new GERPNumberField();
		tfHEDValue.setWidth("124");
		tfCessPer = new TextField();
		tfCessPer.setWidth("30");
		tfCessValue = new GERPNumberField();
		tfCessValue.setWidth("124");
		tfCstPer = new TextField();
		tfCstPer.setWidth("30");
		tfCstValue = new GERPNumberField();
		tfCstValue.setWidth("124");
		tfSubTaxTotal = new GERPNumberField("Sub Tax Total");
		tfSubTaxTotal.setWidth("150");
		tfFreightValue = new GERPNumberField();
		tfFreightValue.setWidth("150");
		tfOtherValue = new GERPNumberField();
		tfOtherValue.setWidth("150");
		tfDiscountPer = new TextField();
		tfDiscountPer.setWidth("30");
		tfDiscountValue = new GERPNumberField();
		tfDiscountValue.setWidth("124");
		tfDiscountPer.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
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
		tfPackingPer.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfDocumentCharges = new GERPNumberField("Doc. Charges");
		tfDocumentCharges.setWidth("150");
		tfDocumentCharges.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfPDCCharges = new GERPNumberField("PDC Charges");
		tfPDCCharges.setWidth("150");
		tfPDCCharges.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getCalculatedValues();
			}
		});
		tfGrandtotal = new GERPNumberField("Grand Total");
		tfGrandtotal.setWidth("150");
		cbPaymetTerms = new ComboBox("Payment Terms");
		cbPaymetTerms.setWidth("150");
		cbPaymetTerms.setItemCaptionPropertyId("lookupname");
		loadPaymentTerms();
		cbFreightTerms = new ComboBox("Freight Terms");
		cbFreightTerms.setWidth("150");
		cbFreightTerms.setItemCaptionPropertyId("lookupname");
		loadFreightTerms();
		cbWarrentyTerms = new ComboBox("Warrenty Terms");
		cbWarrentyTerms.setItemCaptionPropertyId("lookupname");
		cbWarrentyTerms.setWidth("150");
		loadWarentyTerms();
		tfDisToatal.setWidth("150");
		cbDelTerms = new ComboBox("Delivery Terms");
		cbDelTerms.setItemCaptionPropertyId("lookupname");
		cbDelTerms.setWidth("150");
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
		ckCashPO = new CheckBox("Cash PO");
		cbBranch = new ComboBox("Branch Name");
		cbBranch.setWidth("150");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbPOType = new ComboBox("PO Type");
		cbPOType.setItemCaptionPropertyId("lookupname");
		cbPOType.setWidth("150");
		loadPOTypeOptions();
		try {
			ApprovalSchemaDM obj = servicesmsPOHdr.getReviewerId(companyid, appScreenId, branchID, roleId).get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR, BASEConstants.WO_RV_STATUS);
			} else {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR, BASEConstants.WO_AP_STATUS);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		cbStatus.setWidth("150");
		// PurchaseOrder Detail Comp
		taCustProddesc = new TextArea("Product Description");
		taCustProddesc.setHeight("30");
		taCustProddesc.setWidth("150");
		tfOrderQty = new TextField("Order Qty");
		tfOrderQty.setValue("0");
		tfOrderQty.setWidth("110");
		tfcustprodcode = new TextField("Product Code");
		tfcustprodcode.setValue("0");
		tfcustprodcode.setWidth("110");
		cbProduct = new ComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		cbProduct.setWidth("110");
		cbProduct.setImmediate(true);
		if (cbQuoteNo.getValue() == null) {
			System.out.println("loadfullProduct" + cbQuoteNo.getValue());
			loadfullProduct();
		}
		cbProduct.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbQuoteNo.getValue() != null) {
					if (cbProduct.getValue() != null) {
						tfQuoteQty.setReadOnly(false);
						if (((SmsQuoteDtlDM) cbProduct.getValue()).getQuoteqty() != null) {
							tfQuoteQty.setValue(((SmsQuoteDtlDM) cbProduct.getValue()).getQuoteqty() + "");
							tfQuoteQty.setReadOnly(true);
						} else {
							tfQuoteQty.setValue("0");
						}
						tfQuoteQty.setCaption("Quote Qty(" + ((SmsQuoteDtlDM) cbProduct.getValue()).getProduom() + ")");
						if (((SmsQuoteDtlDM) cbProduct.getValue()).getCustomField1() != null) {
							System.out.println("not null");
							tfCustomField1.setValue(((SmsQuoteDtlDM) cbProduct.getValue()).getCustomField1());
						} else {
							tfCustomField1.setValue("");
						}
						if (((SmsQuoteDtlDM) cbProduct.getValue()).getCustomField2() != null) {
							tfCustomField2.setValue(((SmsQuoteDtlDM) cbProduct.getValue()).getCustomField2());
						} else {
							tfCustomField2.setValue("");
						}
						if (((SmsQuoteDtlDM) cbProduct.getValue()).getUnitrate() != null) {
							tfUnitRate.setValue(((SmsQuoteDtlDM) cbProduct.getValue()).getUnitrate().toString());
						} else {
							tfUnitRate.setValue("");
						}
						if (((SmsQuoteDtlDM) cbProduct.getValue()).getBasicvalue() != null) {
							tfBasicValue.setValue(((SmsQuoteDtlDM) cbProduct.getValue()).getBasicvalue().toString());
						} else {
							tfBasicValue.setValue("");
						}
					}
				} else {
					tfQuoteQty.setEnabled(false);
				}
			}
		});
		taCustProddesc.setWidth("150");
		tfBasicValue = new TextField("Basic value");
		tfBasicValue.setWidth("110");
		tfBasicValue.setValue("0");
		tfCustomField1.setWidth("110");
		tfQuoteQty = new TextField("Quote Qty");
		tfQuoteQty.setValue("0");
		tfQuoteQty.setWidth("110");
		tfdtlPDC = new TextField("PDC Charge");
		tfdtlPDC.setValue("0");
		tfdtlPDC.setWidth("110");
		cbProdUOM = new ComboBox("Product Uom");
		cbProdUOM.setItemCaptionPropertyId("lookupname");
		cbProdUOM.setWidth("110");
		loadUomList();
		tfUnitRate = new TextField("Unit Rate");
		tfUnitRate.setWidth("110");
		tfUnitRate.setValue("0");
		tfUnitRate.setImmediate(true);
		tfUnitRate.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				getCalcBasicValue();
			}
		});
		tfOrderQty.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				getCalcBasicValue();
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
		cbPODtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbPODtlStatus.setWidth("110");
		btnsavepurQuote.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validatePODetails()) {
					savePurchaseQuoteDetails();
				}
			}
		});
		btndelete.setEnabled(false);
		tblSmsPODtl = new GERPTable();
		tblSmsPODtl.setPageLength(10);
		tblSmsPODtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblSmsPODtl.isSelected(event.getItemId())) {
					tblSmsPODtl.setImmediate(true);
					btnsavepurQuote.setCaption("Add");
					btnsavepurQuote.setStyleName("savebt");
					btndelete.setEnabled(false);
					poDetailsResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnsavepurQuote.setCaption("Update");
					btnsavepurQuote.setStyleName("savebt");
					btndelete.setEnabled(true);
					editPODtl();
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
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		loadPODetails();
		loadPOAcceptParamDetails(false);
		btnsavepurQuote.setStyleName("add");
		btnAccept.setStyleName("add");
		getEditableTable();
	}
	
	private void getCalcBasicValue() {
		// TODO Auto-generated method stub
		try {
			tfBasicValue.setReadOnly(false);
			tfBasicValue.setValue((new BigDecimal(tfUnitRate.getValue())).multiply(
					new BigDecimal(tfOrderQty.getValue())).toString());
			tfBasicValue.setReadOnly(true);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadProductList() {
		try {
			BeanItemContainer<SmsQuoteDtlDM> beanquoteDtl = new BeanItemContainer<SmsQuoteDtlDM>(SmsQuoteDtlDM.class);
			beanquoteDtl.addAll(serviceQuoteDtl.getsmsquotedtllist(null,
					((SmsQuoteHdrDM) cbQuoteNo.getValue()).getQuoteId(), null, null));
			cbProduct.setContainerDataSource(beanquoteDtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadProductListenq() {
		try {
			BeanContainer<Long, SmsEnquiryDtlDM> beanEnqDtl = new BeanContainer<Long, SmsEnquiryDtlDM>(
					SmsEnquiryDtlDM.class);
			beanEnqDtl.setBeanIdProperty("enquiryid");
			beanEnqDtl.addAll(serviceEnqdtl.getsmsenquirydtllist(null, (Long) cbEnquiryNumber.getValue(), null, null,
					"Active", "F"));
			cbProduct.setContainerDataSource(beanEnqDtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for TestType UI search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		// Adding components into form layouts for TestType UI search layout
		flColumn1.addComponent(cbBranch);
		// flColumn2.addComponent(tfPONo);
		flColumn2.addComponent(cbPOType);
		flColumn3.addComponent(cbStatus);
		// Adding form layouts into search layout for TestType UI search mode
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		// hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void loadClientsDetails() {
		try {
			Long clientid = serviceEnquiryHdr
					.getSmsEnqHdrList(null, Long.valueOf(cbEnquiryNumber.getValue().toString()), null, null, null, "F",
							null, null).get(0).getClientId();
			BeanContainer<Long, ClientDM> beanClients = new BeanContainer<Long, ClientDM>(ClientDM.class);
			beanClients.setBeanIdProperty("clientId");
			beanClients.addAll(serviceClients.getClientDetails(companyid, clientid, null, null, null, null, null, null,
					"Active", "P"));
			cbClient.setContainerDataSource(beanClients);
			cbClient.setValue(clientid);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
		flColumn1.addComponent(cbBranch);
		flColumn1.addComponent(cbEnquiryNumber);
		flColumn1.addComponent(cbQuoteNo);
		flColumn1.addComponent(cbClient);
		flColumn1.addComponent(cbPOType);
		// flColumn1.addComponent(ckcasePO);
		flColumn1.addComponent(tfPONumber);
		flColumn1.addComponent(dfPODt);
		flColumn1.addComponent(tfVersionNo);
		flColumn2.addComponent(taInvoiceAddr);
		flColumn2.addComponent(taShipmentAddr);
		flColumn2.addComponent(tfBasictotal);
		HorizontalLayout discount = new HorizontalLayout();
		discount.addComponent(tfDiscountPer);
		discount.addComponent(tfDiscountValue);
		discount.setCaption("Discount");
		flColumn2.addComponent(discount);
		flColumn2.setComponentAlignment(discount, Alignment.TOP_LEFT);
		flColumn2.addComponent(tfDisToatal);
		HorizontalLayout pv = new HorizontalLayout();
		pv.addComponent(tfPackingPer);
		pv.addComponent(tfPackingValue);
		pv.setCaption("Packing");
		flColumn2.addComponent(pv);
		flColumn2.setComponentAlignment(pv, Alignment.TOP_LEFT);
		flColumn2.addComponent(tfSubTotal);
		HorizontalLayout vp = new HorizontalLayout();
		vp.addComponent(tfVatPer);
		vp.addComponent(tfVatValue);
		vp.setCaption("VAT");
		flColumn2.addComponent(vp);
		flColumn2.setComponentAlignment(vp, Alignment.TOP_LEFT);
		HorizontalLayout cst = new HorizontalLayout();
		cst.addComponent(tfCstPer);
		cst.addComponent(tfCstValue);
		cst.setCaption("CST");
		flColumn3.addComponent(cst);
		flColumn3.setComponentAlignment(cst, Alignment.TOP_LEFT);
		HorizontalLayout ed = new HorizontalLayout();
		ed.addComponent(tfEDPer);
		ed.addComponent(tfEDValue);
		ed.setCaption("ED");
		flColumn3.addComponent(ed);
		flColumn3.setComponentAlignment(ed, Alignment.TOP_LEFT);
		flColumn3.addComponent(tfSubTaxTotal);
		HorizontalLayout frgt = new HorizontalLayout();
		frgt.addComponent(tfFreightValue);
		frgt.setCaption("Freight");
		flColumn3.addComponent(frgt);
		flColumn3.setComponentAlignment(frgt, Alignment.TOP_LEFT);
		HorizontalLayout other = new HorizontalLayout();
		other.addComponent(tfOtherValue);
		other.setCaption("Other");
		flColumn3.addComponent(other);
		flColumn3.setComponentAlignment(other, Alignment.TOP_LEFT);
		flColumn3.addComponent(tfPDCCharges);
		flColumn3.addComponent(tfDocumentCharges);
		flColumn3.addComponent(tfGrandtotal);
		flColumn3.addComponent(cbPaymetTerms);
		flColumn4.addComponent(cbFreightTerms);
		flColumn4.addComponent(cbWarrentyTerms);
		flColumn4.addComponent(cbDelTerms);
		// flColumn4.addComponent(taRemark);
		flColumn4.addComponent(taLiquidatedDamage);
		flColumn4.addComponent(cbStatus);
		flColumn4.addComponent(chkDutyExe);
		flColumn4.addComponent(chkCformReq);
		flColumn4.addComponent(ckPdcRqu);
		// flColumn4.addComponent(hlPODoc);
		HorizontalLayout hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flColumn1);
		hlHdr.addComponent(flColumn2);
		hlHdr.addComponent(flColumn3);
		hlHdr.addComponent(flColumn4);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding PODtl components
		// Add components for User Input Layout
		flDtlColumn1 = new FormLayout();
		flDtlColumn2 = new FormLayout();
		flDtlColumn3 = new FormLayout();
		flDtlColumn4 = new FormLayout();
		flDtlColumn5 = new FormLayout();
		flDtlColumn1.addComponent(cbProduct);
		flDtlColumn1.addComponent(tfQuoteQty);
		flDtlColumn1.addComponent(tfdtlPDC);
		flDtlColumn2.addComponent(tfOrderQty);
		flDtlColumn2.addComponent(tfUnitRate);
		flDtlColumn3.addComponent(tfBasicValue);
		flDtlColumn3.addComponent(tfCustomField1);
		flDtlColumn4.addComponent(tfCustomField2);
		flDtlColumn4.addComponent(taCustProddesc);
		flDtlColumn5.addComponent(btnsavepurQuote);
		flDtlColumn5.addComponent(btndelete);
		HorizontalLayout hlSmsQuotDtl = new HorizontalLayout();
		hlSmsQuotDtl.addComponent(flDtlColumn1);
		hlSmsQuotDtl.addComponent(flDtlColumn2);
		hlSmsQuotDtl.addComponent(flDtlColumn3);
		hlSmsQuotDtl.addComponent(flDtlColumn4);
		hlSmsQuotDtl.addComponent(flDtlColumn5);
		hlSmsQuotDtl.setSpacing(true);
		hlSmsQuotDtl.setMargin(true);
		HorizontalLayout hlsmsaccept = new HorizontalLayout();
		hlsmsaccept.setSpacing(true);
		hlsmsaccept.setMargin(true);
		VerticalLayout vlSmsQuoteHDR = new VerticalLayout();
		VerticalLayout vlSmsQuoteHDR1 = new VerticalLayout();
		vlSmsQuoteHDR = new VerticalLayout();
		vlSmsQuoteHDR.addComponent(hlSmsQuotDtl);
		vlSmsQuoteHDR.addComponent(tblSmsPODtl);
		vlSmsQuoteHDR.setWidth("100%");
		tblSmsPODtl.setWidth("100%");
		vlSmsQuoteHDR.setMargin(true);
		vlSmsQuoteHDR.setSpacing(true);
		vlSmsQuoteHDR1.addComponent(tblSmsAccept);
		vlSmsQuoteHDR1.setWidth("100%");
		tblSmsAccept.setWidth("100%");
		vlSmsQuoteHDR1.setSpacing(true);
		vlSmsQuoteHDR1.setMargin(true);
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(vlSmsQuoteHDR);
		HorizontalLayout ht = new HorizontalLayout();
		ht.addComponent(tblSmsAccept);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(hl, "Sales Purchase Order Detail");
		dtlTab.addTab(ht, "Sales Purchase Order Acceptance");
		dtlTab.addTab(vlTableForm, "Comments");
		VerticalLayout vlQuoteHdrDtl = new VerticalLayout();
		vlQuoteHdrDtl = new VerticalLayout();
		vlQuoteHdrDtl.addComponent(GERPPanelGenerator.createPanel(hlHdr));
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		vlQuoteHdrDtl.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		vlQuoteHdrDtl.setSpacing(true);
		vlQuoteHdrDtl.setWidth("100%");
		hlUserInputLayout.addComponent(vlQuoteHdrDtl);
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		btnsavepurQuote.setStyleName("add");
		btnAccept.setStyleName("add");
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<SmsPOHdrDM> list = new ArrayList<SmsPOHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + cbBranch.getValue() + ", " + cbStatus.getValue());
			list = servicesmsPOHdr.getSmspohdrList((String) cbPOType.getValue(), null, companyid,
					(Long) cbBranch.getValue(), null, null, (String) cbStatus.getValue(), "F", null);
			recordCnt = list.size();
			beansmsPOHdr = new BeanItemContainer<SmsPOHdrDM>(SmsPOHdrDM.class);
			beansmsPOHdr.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
			tblMstScrSrchRslt.setContainerDataSource(beansmsPOHdr);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "poid", "enqNo", "clientName", "clientCity", "pono",
					"lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Enquiry No.", "Client Name", "City", "PO No.",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("poId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadPODetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordCnt = listPODetails.size();
			tblSmsPODtl.setPageLength(5);
			beansmsPODtl = new BeanItemContainer<SmsPODtlDM>(SmsPODtlDM.class);
			beansmsPODtl.addAll(listPODetails);
			BigDecimal sum = new BigDecimal("0");
			BigDecimal sumPdc = new BigDecimal("0");

			for (SmsPODtlDM obj : listPODetails) {
				if (obj.getBasicvalue() != null) {
					sum = sum.add(obj.getBasicvalue());
				}
			}
			
			for (SmsPODtlDM obj : listPODetails) {
				if (obj.getPdcValue() != null) {
					sumPdc = sumPdc.add(obj.getPdcValue());
					System.out.println("=============================================>" + sumPdc);
				}
			}
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(sum.toString());
			tfBasictotal.setReadOnly(true);
			tfPDCCharges.setReadOnly(false);
			tfPDCCharges.setValue(sumPdc.toString());
			tfPDCCharges.setReadOnly(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxslap. result set");
			tblSmsPODtl.setContainerDataSource(beansmsPODtl);
			tblSmsPODtl.setVisibleColumns(new Object[] { "prodname", "poqty", "basicvalue", "unitrate", "custprodcode",
					"custproddesc", "lastupdateddt", "lastupdatedby" });
			tblSmsPODtl.setColumnHeaders(new String[] { "Product Name", "PO.Qty", "Basic Value", "Unit Rate",
					"Product Code", "Product Desc", "Last Updated Date", "Last Updated By" });
			tblSmsPODtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadPOAcceptParamDetails(Boolean fromdb) {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblSmsAccept.removeAllItems();
			tblSmsAccept.setPageLength(5);
			if (fromdb) {
				listPOAccept = servicePOAcceptance.getsmspoacceptancelist(null, poid, null, "F");
			} else {
				listPOAccept = new ArrayList<SmsPOAcceptanceDM>();
				for (CompanyLookupDM companyLookupDM : serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null,
						"Active", "SM_POACCPT")) {
					SmsPOAcceptanceDM smsPOAcceptanceDM = new SmsPOAcceptanceDM();
					smsPOAcceptanceDM.setAcceptparam(companyLookupDM.getLookupname());
					listPOAccept.add(smsPOAcceptanceDM);
				}
			}
			recordCnt = listPOAccept.size();
			beanpoaccept = new BeanItemContainer<SmsPOAcceptanceDM>(SmsPOAcceptanceDM.class);
			beanpoaccept.addAll(listPOAccept);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the SMSENQUIRY. result set");
			tblSmsAccept.setContainerDataSource(beanpoaccept);
			tblSmsAccept.setVisibleColumns(new Object[] { "acceptparam", "acceptoption", "acceptdesc", "lastupdateddt",
					"lastupdatedby" });
			tblSmsAccept.setColumnHeaders(new String[] { "Accept Param", "Accept Option", "Description",
					"Last Updated Date", "Last Updated By" });
			tblSmsAccept.setColumnFooter("pono", "No.of Records : " + recordCnt);
			getEditableTable();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadBranchList() {
		try {
			List<BranchDM> branchList = serviceBranch.getBranchList(null, null, null, "Active", companyid, "P");
			branchList.add(new BranchDM(0L, "All Branches"));
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(branchList);
			cbBranch.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Uom
	private void loadUomList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp
					.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active", "SM_UOM"));
			cbProdUOM.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadPOTypeOptions() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_POTYPE"));
			cbPOType.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadPaymentTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_PAYTRM"));
			cbPaymetTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadFreightTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_FRTTRM"));
			cbFreightTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadWarentyTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_WRNTRM"));
			cbWarrentyTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadDeliveryTerms() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_DELTRM"));
			cbDelTerms.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadQuoteNoList() {
		try {
			BeanItemContainer<SmsQuoteHdrDM> beanQuote = new BeanItemContainer<SmsQuoteHdrDM>(SmsQuoteHdrDM.class);
			beanQuote.addAll(serviceQuoteHdr.getSmsQuoteHdrList(companyid, null, null, null, null, null, "F",
					Long.valueOf(cbEnquiryNumber.getValue().toString())));
			cbQuoteNo.setContainerDataSource(beanQuote);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load EnquiryNo
	private void loadEnquiryNo() {
		try {
			BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
			beansmsenqHdr.setBeanIdProperty("enquiryId");
			beansmsenqHdr
					.addAll(serviceEnquiryHdr.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
			cbEnquiryNumber.setContainerDataSource(beansmsenqHdr);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editPOHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			SmsPOHdrDM poHdrDM = beansmsPOHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			poid = poHdrDM.getPoid();
			cbBranch.setValue(poHdrDM.getBranchid());
			tfPONumber.setReadOnly(false);
			tfPONumber.setValue(poHdrDM.getPono());
			dfPODt.setValue(poHdrDM.getPodate());
			taRemark.setValue(poHdrDM.getPoremark());
			tfVersionNo.setValue(poHdrDM.getVersionno().toString());
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(poHdrDM.getBasictotal().toString());
			tfBasictotal.setReadOnly(true);
			tfPackingPer.setValue(poHdrDM.getPackingprnct().toString());
			tfPackingValue.setReadOnly(false);
			tfPackingValue.setValue(poHdrDM.getPackingvalue().toString());
			tfPackingValue.setReadOnly(true);
			if (poHdrDM.getDiscountPercent() != null) {
				tfDiscountPer.setValue(poHdrDM.getDiscountPercent().toString());
			}
			if (poHdrDM.getDiscountValue() != null) {
				tfDiscountValue.setReadOnly(false);
				tfDiscountValue.setValue(poHdrDM.getDiscountValue().toString());
				tfDiscountValue.setReadOnly(true);
			}
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(poHdrDM.getSubtotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(poHdrDM.getVatprnct().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(poHdrDM.getVatvalue().toString());
			tfVatValue.setReadOnly(true);
			tfEDPer.setValue(poHdrDM.getEdprnct().toString());
			tfEDValue.setReadOnly(false);
			tfEDValue.setValue(poHdrDM.getEdvalue().toString());
			tfEDValue.setReadOnly(true);
			tfHEDPer.setValue(poHdrDM.getHedprnct().toString());
			tfHEDValue.setReadOnly(false);
			tfHEDValue.setValue(poHdrDM.getHedvalue().toString());
			tfHEDValue.setReadOnly(true);
			tfCessPer.setValue(poHdrDM.getCessprnct().toString());
			tfCessValue.setReadOnly(false);
			tfCessValue.setValue(poHdrDM.getCessvalue().toString());
			tfCessValue.setReadOnly(true);
			tfCstPer.setValue(poHdrDM.getCstprnct().toString());
			tfCstValue.setReadOnly(false);
			tfCstValue.setValue(poHdrDM.getCstvalue().toString());
			tfCstValue.setReadOnly(true);
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(poHdrDM.getSubtaxtotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(poHdrDM.getGrandtotal().toString());
			tfGrandtotal.setReadOnly(true);
			if (poHdrDM.getPaymentterms() != null) {
				cbPaymetTerms.setValue(poHdrDM.getPaymentterms().toString());
			}
			if (poHdrDM.getFreighttterms() != null) {
				cbFreightTerms.setValue(poHdrDM.getFreighttterms().toString());
			}
			if (poHdrDM.getWarrantyterms() != null) {
				cbWarrentyTerms.setValue(poHdrDM.getWarrantyterms().toString());
			}
			if ((poHdrDM.getDeliveryterms() != null)) {
				cbDelTerms.setValue(poHdrDM.getDeliveryterms().toString());
			}
			if (poHdrDM.getPotype() != null) {
				cbPOType.setValue(poHdrDM.getPotype());
			}
			if (poHdrDM.getEnquiryId() != null) {
				System.out.println("smspoHdrobj.getEnquiryId()" + poHdrDM.getEnquiryId());
				cbEnquiryNumber.setValue(poHdrDM.getEnquiryId());
			}
			Long quote = poHdrDM.getQuoteid();
			Collection<?> quoteids = cbQuoteNo.getItemIds();
			for (Iterator<?> iterator = quoteids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbQuoteNo.getItem(itemId);
				// Get the actual bean and use the data
				SmsQuoteHdrDM sh = (SmsQuoteHdrDM) item.getBean();
				if (quote != null && quote.equals(sh.getQuoteId())) {
					cbQuoteNo.setValue(itemId);
				}
			}
			if (poHdrDM.getDutyexempted().equals("Y")) {
				chkDutyExe.setValue(true);
			} else {
				chkDutyExe.setValue(false);
			}
			if (poHdrDM.getCformreqd().equals("Y")) {
				chkCformReq.setValue(true);
			} else {
				chkCformReq.setValue(false);
			}
			if (poHdrDM.getPdcreqd().equals("Y")) {
				ckPdcRqu.setValue(true);
			} else {
				ckPdcRqu.setValue(false);
			}
			if (poHdrDM.getLiquidatedDamage() != null) {
				taLiquidatedDamage.setValue(poHdrDM.getLiquidatedDamage());
			}
			if (poHdrDM.getClientid() != null) {
				cbClient.setValue(poHdrDM.getClientid());
			}
			if (poHdrDM.getShippingaddr() != null) {
				taShipmentAddr.setValue(poHdrDM.getShippingaddr());
			}
			if (poHdrDM.getInvoiceaddr() != null) {
				taInvoiceAddr.setValue(poHdrDM.getInvoiceaddr());
			}
			cbStatus.setValue(poHdrDM.getPostatus());
			tfFreightValue.setValue(poHdrDM.getFreightvalue().toString());
			tfOtherValue.setValue((poHdrDM.getOthersvalue().toString()));
		}
		listPODetails = servicesmspodtl.getsmspodtllist(null, poid, null, null, null, null, null);
		loadPODetails();
		loadPOAcceptParamDetails(true);
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, poid, null,
				status);
		comments.loadsrch(true, null, null, null, null, poid, null, null, null, null, null, null, null);
		comments.commentList = serviceComment.getSmsCommentsList(null, null, null, null, poid, null, null, null, null,
				null, null, null);
	}
	
	private void editPODtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			if (tblSmsPODtl.getValue() != null) {
				SmsPODtlDM poDtlDM = beansmsPODtl.getItem(tblSmsPODtl.getValue()).getBean();
				Long prodid = poDtlDM.getProductid();
				if (cbQuoteNo.getValue() != null) {
					Collection<?> prodids = cbProduct.getItemIds();
					for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
						Object itemId = (Object) iterator.next();
						BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
						// Get the actual bean and use the data
						SmsQuoteDtlDM st = (SmsQuoteDtlDM) item.getBean();
						if (prodid != null && prodid.equals(st.getProductid())) {
							cbProduct.setValue(itemId);
						}
					}
				} else {
					Collection<?> uomid = cbProduct.getItemIds();
					for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
						Object itemId = (Object) iterator.next();
						BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
						// Get the actual bean and use the data
						ProductDM st = (ProductDM) item.getBean();
						if (prodid != null && prodid.equals(st.getProdid())) {
							cbProduct.setValue(itemId);
						}
					}
				}
				tfUnitRate.setValue(poDtlDM.getUnitrate().toString());
				cbProdUOM.setReadOnly(false);
				cbProdUOM.setValue(poDtlDM.getProduom());
				cbProdUOM.setReadOnly(true);
				cbPODtlStatus.setValue(poDtlDM.getPodtlstatus());
				tfBasicValue.setReadOnly(false);
				tfBasicValue.setValue(poDtlDM.getBasicvalue().toString());
				if (poDtlDM.getCustprodcode() != null) {
					tfcustprodcode.setValue(poDtlDM.getCustprodcode());
				}
				if (poDtlDM.getInvoicedqty() != null) {
					tfOrderQty.setValue(poDtlDM.getPoqty().toString());
				}
				if (poDtlDM.getCustomField1() != null) {
					tfCustomField1.setValue(poDtlDM.getCustomField1());
				}
				if (poDtlDM.getCustomField2() != null) {
					tfCustomField2.setValue(poDtlDM.getCustomField2());
				}
				if (poDtlDM.getCustproddesc() != null) {
					taCustProddesc.setValue(poDtlDM.getCustproddesc());
				}
				if ((poDtlDM.getPdcValue() != null)) {
					tfdtlPDC.setValue(poDtlDM.getPdcValue().toString());
				}
				if(taCustProddesc.getValue()!=null){
					taCustProddesc.setValue(poDtlDM.getCustproddesc());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
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
		cbPOType.setValue(null);
		tfPONumber.setValue("");
		cbBranch.setValue(branchId);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		cbProduct.setRequired(true);
		cbPODtlStatus.setValue(cbPODtlStatus.getItemIds().iterator().next());
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		cbBranch.setRequired(true);
		tfBasicValue.setRequired(true);
		tfUnitRate.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		tfBasictotal.setReadOnly(true);
		tfSubTotal.setReadOnly(true);
		tfSubTaxTotal.setReadOnly(true);
		tfGrandtotal.setReadOnly(true);
		tfPackingValue.setReadOnly(true);
		tfVatValue.setReadOnly(true);
		tfEDValue.setReadOnly(true);
		tfHEDValue.setReadOnly(true);
		tfCessValue.setReadOnly(true);
		tfCstValue.setReadOnly(true);
		assembleInputUserLayout();
		new UploadDocumentUI(hlPODoc);
		tfPONumber.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_NPONO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfPONumber.setReadOnly(false);
			} else {
				tfPONumber.setReadOnly(false);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		resetFields();
		btnsavepurQuote.setCaption("Add");
		btnAccept.setCaption("Add");
		tblSmsPODtl.setVisible(true);
		tblSmsAccept.setVisible(true);
		cbBranch.setValue(branchId);
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null);
		loadPOAcceptParamDetails(false);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlCmdBtnLayout.setVisible(false);
		cbProduct.setRequired(true);
		cbProdUOM.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		cbBranch.setRequired(true);
		tfBasicValue.setRequired(true);
		tfUnitRate.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		resetFields();
		editPOHdr();
		getCalculatedValues();
		comments.loadsrch(true, null, null, null, null, null, null, null, null, null, poid, null, null);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		cbPOType.setComponentError(null);
		cbEnquiryNumber.setComponentError(null);
		tfPONumber.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if (tfPONumber.getValue() == null && tfPONumber.getValue().trim().length() > 0) {
			tfPONumber.setComponentError(new UserError(GERPErrorCodes.PURCAHSE_ORD_NO));
			errorFlag = true;
		}
		if ((cbEnquiryNumber.getValue() == null)) {
			cbEnquiryNumber.setComponentError(new UserError(GERPErrorCodes.ENQUIRY_NO));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validatePODetails() {
		boolean isValid = true;
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			cbProduct.setComponentError(null);
		}
		if (tfBasicValue.getValue() == null) {
			tfBasicValue.setComponentError(new UserError("Please Enter basic value"));
			isValid = false;
		} else {
			tfBasicValue.setComponentError(null);
		}
		if (tfUnitRate.getValue() == null) {
			tfUnitRate.setComponentError(new UserError("Please Enter Unit Rate"));
			isValid = false;
		} else {
			tfUnitRate.setComponentError(null);
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			SmsPOHdrDM smspoHdrobj = new SmsPOHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				smspoHdrobj = beansmsPOHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
				if (tfPONumber.getValue() != null) {
					smspoHdrobj.setPono(tfPONumber.getValue());
				} else {
					smspoHdrobj.setPono("");
				}
			}
			smspoHdrobj.setPono(tfPONumber.getValue());
			smspoHdrobj.setBranchid((Long) cbBranch.getValue());
			smspoHdrobj.setCompanyid(companyid);
			smspoHdrobj.setPodate(dfPODt.getValue());
			smspoHdrobj.setPoremark(taRemark.getValue());
			smspoHdrobj.setLiquidatedDamage(taLiquidatedDamage.getValue());
			if (cbPOType.getValue() != null) {
				smspoHdrobj.setPotype(cbPOType.getValue().toString());
			}
			if (tfVersionNo.getValue() != null) {
				smspoHdrobj.setVersionno((Long.valueOf(tfVersionNo.getValue())));
			}
			smspoHdrobj.setBasictotal(new BigDecimal(tfBasictotal.getValue()));
			smspoHdrobj.setPackingprnct(new BigDecimal(tfPackingPer.getValue()));
			smspoHdrobj.setPackingvalue(new BigDecimal(tfPackingValue.getValue()));
			smspoHdrobj.setDiscountPercent(new BigDecimal(tfDiscountPer.getValue()));
			smspoHdrobj.setDiscountValue(new BigDecimal(tfDiscountValue.getValue()));
			smspoHdrobj.setSubtotal(new BigDecimal(tfSubTotal.getValue()));
			smspoHdrobj.setVatprnct(new BigDecimal(tfVatPer.getValue()));
			smspoHdrobj.setVatvalue(new BigDecimal(tfVatValue.getValue()));
			smspoHdrobj.setEdprnct(new BigDecimal(tfEDPer.getValue()));
			smspoHdrobj.setEdvalue(new BigDecimal(tfEDValue.getValue()));
			smspoHdrobj.setHedvalue(new BigDecimal(tfHEDValue.getValue()));
			smspoHdrobj.setHedprnct(new BigDecimal(tfHEDPer.getValue()));
			smspoHdrobj.setCessprnct(new BigDecimal(tfCessPer.getValue()));
			smspoHdrobj.setCessvalue(new BigDecimal(tfCessValue.getValue()));
			smspoHdrobj.setCstprnct(new BigDecimal(tfCstPer.getValue()));
			smspoHdrobj.setCstvalue(new BigDecimal(tfCstValue.getValue()));
			smspoHdrobj.setSubtaxtotal(new BigDecimal(tfSubTaxTotal.getValue()));
			smspoHdrobj.setClientid((Long) (cbClient.getValue()));
			smspoHdrobj.setFreightvalue(new BigDecimal(tfFreightValue.getValue()));
			smspoHdrobj.setOthersvalue(new BigDecimal(tfOtherValue.getValue()));
			smspoHdrobj.setGrandtotal(new BigDecimal(tfGrandtotal.getValue()));
			if (tfDisToatal.getValue() != null) {
				smspoHdrobj.setDisTotal(new BigDecimal(tfDisToatal.getValue()));
			}
			if (tfPDCCharges.getValue() != null) {
				smspoHdrobj.setPdcCharges(new BigDecimal(tfPDCCharges.getValue()));
			}
			if (cbPaymetTerms.getValue() != null) {
				smspoHdrobj.setPaymentterms((cbPaymetTerms.getValue().toString()));
			}
			if (cbFreightTerms.getValue() != null) {
				smspoHdrobj.setFreighttterms(cbFreightTerms.getValue().toString());
			}
			if (cbWarrentyTerms.getValue() != null) {
				smspoHdrobj.setWarrantyterms((cbWarrentyTerms.getValue().toString()));
			}
			if (cbDelTerms.getValue() != null) {
				smspoHdrobj.setDeliveryterms(cbDelTerms.getValue().toString());
			}
			if (chkDutyExe.getValue().equals(true)) {
				smspoHdrobj.setDutyexempted("Y");
			} else if (chkDutyExe.getValue().equals(false)) {
				smspoHdrobj.setDutyexempted("N");
			}
			if (chkCformReq.getValue().equals(true)) {
				smspoHdrobj.setCformreqd("Y");
			} else if (chkCformReq.getValue().equals(false)) {
				smspoHdrobj.setCformreqd("N");
			}
			if (ckPdcRqu.getValue().equals(true)) {
				smspoHdrobj.setPdcreqd("Y");
			} else if (ckPdcRqu.getValue().equals(false)) {
				smspoHdrobj.setPdcreqd("N");
			}
			if (cbQuoteNo.getValue() != null) {
				smspoHdrobj.setQuoteid(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getQuoteId());
			}
			smspoHdrobj.setShippingaddr(taShipmentAddr.getValue());
			smspoHdrobj.setInvoiceaddr(taInvoiceAddr.getValue());
			smspoHdrobj.setLiquidatedDamage(taLiquidatedDamage.getValue());
			if (cbStatus.getValue() != null) {
				smspoHdrobj.setPostatus(cbStatus.getValue().toString());
			}
			smspoHdrobj.setPreparedby(employeeId);
			smspoHdrobj.setReviewedby(null);
			smspoHdrobj.setActionedby(null);
			smspoHdrobj.setLastupdateddt(DateUtils.getcurrentdate());
			smspoHdrobj.setLastupdatedby(username);
			smspoHdrobj.setEnquiryId(Long.valueOf(cbEnquiryNumber.getValue().toString()));
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			smspoHdrobj.setPodoc(fileContents);
			servicesmsPOHdr.saveorUpdateSmspohdrDetails(smspoHdrobj);
			poid = smspoHdrobj.getPoid();
			@SuppressWarnings("unchecked")
			Collection<SmsPODtlDM> itemIds = (Collection<SmsPODtlDM>) tblSmsPODtl.getVisibleItemIds();
			for (SmsPODtlDM save : (Collection<SmsPODtlDM>) itemIds) {
				save.setPoid(Long.valueOf(smspoHdrobj.getPoid().toString()));
				servicesmspodtl.saveOrUpdatesmspodtldtlDetails(save);
			}
			@SuppressWarnings("unchecked")
			Collection<SmsPOAcceptanceDM> itemIds1 = (Collection<SmsPOAcceptanceDM>) tblSmsAccept.getVisibleItemIds();
			for (SmsPOAcceptanceDM save : (Collection<SmsPOAcceptanceDM>) itemIds1) {
				save.setPoid(Long.valueOf(smspoHdrobj.getPoid().toString()));
				servicePOAcceptance.saveOrUpdatesmspoacceptDetails(save);
			}
			if (smspoHdrobj.getPostatus() != null) {
				comments.saveSalesPo(smspoHdrobj.getPoid(), smspoHdrobj.getPostatus().toString());
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_NPONO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_NPONO");
					}
				}
				catch (Exception e) {
				}
			}
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void savePurchaseQuoteDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			int count = 0;
			if (cbQuoteNo.getValue() != null) {
				for (SmsPODtlDM smsPODtlDM : listPODetails) {
					if (smsPODtlDM.getProductid() == ((SmsQuoteDtlDM) cbProduct.getValue()).getProductid()) {
						count++;
						break;
					}
				}
			} else {
				for (SmsPODtlDM smsPODtlDM : listPODetails) {
					if (smsPODtlDM.getProductid() == ((ProductDM) cbProduct.getValue()).getProdid()) {
						count++;
						break;
					}
				}
			}
			if (count == 0) {
				SmsPODtlDM salesOrderDtlobj = new SmsPODtlDM();
				if (tblSmsPODtl.getValue() != null) {
					salesOrderDtlobj = beansmsPODtl.getItem(tblSmsPODtl.getValue()).getBean();
					listPODetails.remove(salesOrderDtlobj);
				}
				if (cbQuoteNo.getValue() != null) {
					salesOrderDtlobj.setProductid(((SmsQuoteDtlDM) cbProduct.getValue()).getProductid());
					salesOrderDtlobj.setProdname(((SmsQuoteDtlDM) cbProduct.getValue()).getProdname());
					salesOrderDtlobj.setProduom(((SmsQuoteDtlDM) cbProduct.getValue()).getProduom());
				} else {
					salesOrderDtlobj.setProductid(((ProductDM) cbProduct.getValue()).getProdid());
					salesOrderDtlobj.setProdname(((ProductDM) cbProduct.getValue()).getProdname());
					salesOrderDtlobj.setProduom(((ProductDM) cbProduct.getValue()).getUom());
				}
				salesOrderDtlobj.setPoqty((Long.valueOf(tfOrderQty.getValue())));
				salesOrderDtlobj.setInvoicedqty(0L);
				salesOrderDtlobj.setUnitrate((new BigDecimal(tfUnitRate.getValue())));
				salesOrderDtlobj.setPodtlstatus("Active");
				salesOrderDtlobj.setBasicvalue((new BigDecimal(tfBasicValue.getValue())));
				salesOrderDtlobj.setCustprodcode(((tfcustprodcode.getValue())));
				salesOrderDtlobj.setCustproddesc(taCustProddesc.getValue());
				salesOrderDtlobj.setCustomField1(tfCustomField1.getValue());
				salesOrderDtlobj.setCustomField2(tfCustomField2.getValue());
				salesOrderDtlobj.setLastupdateddt(DateUtils.getcurrentdate());
				salesOrderDtlobj.setLastupdatedby(username);
				salesOrderDtlobj.setPdcValue(new BigDecimal(tfdtlPDC.getValue()));
				listPODetails.add(salesOrderDtlobj);
				loadPODetails();
				getCalculatedValues();
			} else {
				cbProduct.setComponentError(new UserError("Product Already Exist.."));
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		poDetailsResetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for TestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_P_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", poid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		cbProduct.setRequired(false);
		cbBranch.setRequired(false);
		cbPOType.setRequired(false);
		tfPONumber.setRequired(false);
		tfBasicValue.setRequired(false);
		tfUnitRate.setRequired(false);
		resetFields();
		poDetailsResetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEnquiryNumber.setValue(null);
		tfDisToatal.setValue("0");
		btnAccept.setCaption("Add");
		cbEnquiryNumber.setComponentError(null);
		cbQuoteNo.setContainerDataSource(null);
		cbClient.setContainerDataSource(null);
		tfPONumber.setReadOnly(false);
		tfPONumber.setValue("");
		tfBasictotal.setReadOnly(false);
		tfBasictotal.setValue("0");
		try {
			tfCessPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "CESS", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCessPer.setValue("0");
		}
		tfCessValue.setReadOnly(false);
		tfCessValue.setValue("0");
		chkCformReq.setValue(false);
		try {
			tfCstPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "CST", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfCstPer.setValue("0");
		}
		cbDelTerms.setValue(null);
		tfCstValue.setReadOnly(false);
		tfCstValue.setValue("0");
		chkDutyExe.setValue(false);
		try {
			tfVatPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "VAT", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfVatPer.setValue("0");
		}
		try {
			tfEDPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "ED", "Active", "F").get(0).getTaxprnct()
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
		tfSubTotal.setReadOnly(false);
		tfSubTotal.setValue("0");
		tfSubTaxTotal.setReadOnly(false);
		tfSubTaxTotal.setValue("0");
		tfVersionNo.setValue("0");
		cbQuoteNo.setValue(null);
		tfPackingValue.setReadOnly(false);
		tfPackingValue.setValue("0");
		try {
			tfPackingPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "PACKING", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfPackingPer.setValue("0");
		}
		tfOtherValue.setValue("0");
		tfHEDValue.setReadOnly(false);
		tfHEDValue.setValue("0");
		try {
			tfHEDPer.setValue(serviceTaxes.getTaxesSmsList(companyid, null, "HED", "Active", "F").get(0)
					.getTaxprnct().toString());
		}
		catch (Exception e) {
			tfHEDPer.setValue("0");
		}
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue("");
		tfFreightValue.setValue("0");
		tfPDCCharges.setReadOnly(false);
		tfPDCCharges.setValue("0.00");
		tfPDCCharges.setReadOnly(true);
		tfDocumentCharges.setValue("0.00");
		tfDiscountPer.setValue("0");
		tfDiscountValue.setReadOnly(false);
		tfDiscountValue.setValue("0");
		cbFreightTerms.setValue(null);
		cbProduct.setValue(null);
		cbStatus.setValue(null);
		cbClient.setValue(null);
		dfPODt.setValue(new Date());
		taRemark.setValue("");
		taLiquidatedDamage.setValue("");
		cbBranch.setComponentError(null);
		ckPdcRqu.setValue(false);
		listPODetails = new ArrayList<SmsPODtlDM>();
		tblSmsPODtl.removeAllItems();
		new UploadDocumentUI(hlPODoc);
		cbPOType.setValue(null);
		taInvoiceAddr.setValue("");
		taShipmentAddr.setValue("");
		ckCashPO.setValue(false);
		cbPOType.setComponentError(null);
		cbQuoteNo.setComponentError(null);
		cbClient.setValue("");
		listPOAccept = new ArrayList<SmsPOAcceptanceDM>();
		tblSmsAccept.removeAllItems();
	}
	
	private void poDetailsResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbProduct.setValue(null);
		tfQuoteQty.setReadOnly(false);
		tfQuoteQty.setValue("0");
		tfQuoteQty.setReadOnly(true);
		tfdtlPDC.setComponentError(null);
		tfdtlPDC.setValue("0");
		tfOrderQty.setValue("0");
		tfOrderQty.setComponentError(null);
		tfcustprodcode.setValue("0");
		tfcustprodcode.setComponentError(null);
		tfUnitRate.setValue("0");
		tfUnitRate.setComponentError(null);
		taCustProddesc.setValue("");
		taCustProddesc.setComponentError(null);
		tfCustomField1.setValue("");
		tfCustomField2.setValue("");
		cbProdUOM.setReadOnly(false);
		cbProdUOM.setValue(null);
		tfBasicValue.setReadOnly(false);
		tfBasicValue.setValue("0");
		tfBasicValue.setComponentError(null);
		cbPODtlStatus.setValue(cbPODtlStatus.getItemIds().iterator().next());
		cbPODtlStatus.setValue(null);
		cbProduct.setComponentError(null);
	}
	
	private void getCalculatedValues() {
		if (chkCformReq.getValue()) {
			tfVatPer.setValue("0");
			try {
				/*
				 * tfCstPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CST", "Active", "F").get(0)
				 * .getTaxprnct().toString());
				 */
			}
			catch (Exception e) {
				e.printStackTrace();
				tfCstPer.setValue("0");
			}
		} else {
			tfCstPer.setValue("0");
			try {
				/*
				 * tfVatPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "VAT", "Active", "F").get(0)
				 * .getTaxprnct().toString());
				 */
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
				/*
				 * tfHEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "HED", "Active", "F").get(0)
				 * .getTaxprnct().toString());
				 */
			}
			catch (Exception e) {
				tfHEDPer.setValue("0");
			}
			try {
				/*
				 * tfEDPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "ED", "Active", "F").get(0)
				 * .getTaxprnct().toString());
				 */
			}
			catch (Exception e) {
				tfEDPer.setValue("0");
			}
			try {
				/*
				 * tfCessPer.setValue(serviceTaxesSms.getTaxesSmsList(companyid, null, "CESS", "Active", "F").get(0)
				 * .getTaxprnct().toString());
				 */
			}
			catch (Exception e) {
				tfCessPer.setValue("0");
			}
		}
		BigDecimal basictotal = new BigDecimal(tfBasictotal.getValue());
		if (!tfDiscountPer.getValue().equals("0")) {
			BigDecimal discountamt = gerPercentageValue(new BigDecimal(tfDiscountPer.getValue()), basictotal);
			tfDiscountValue.setValue(discountamt.toString());
		} else if (!tfDiscountValue.getValue().equals("0")) {
			tfDiscountValue.setValue(tfDiscountValue.getValue());
		}
		BigDecimal discountTotal = basictotal.subtract(new BigDecimal(tfDiscountValue.getValue()));
		tfDisToatal.setValue(discountTotal.toString());
		BigDecimal packingvalue = gerPercentageValue(new BigDecimal(tfPackingPer.getValue()), discountTotal);
		tfPackingValue.setReadOnly(false);
		tfPackingValue.setValue(packingvalue.toString());
		tfPackingValue.setReadOnly(true);
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
		frgval = new BigDecimal(tfFreightValue.getValue());
		tfFreightValue.setValue(frgval.toString());
		otherval = new BigDecimal(tfOtherValue.getValue());
		tfOtherValue.setValue(otherval.toString());
		BigDecimal grand = frgval.add(otherval).add(cstval).add(csttotal);
		BigDecimal documentCharges = new BigDecimal(tfDocumentCharges.getValue());
		BigDecimal grandTotal = subtaxTotal.add(grand).add(documentCharges);
		tfGrandtotal.setReadOnly(false);
		tfGrandtotal.setValue(grandTotal.toString());
		tfGrandtotal.setReadOnly(true);
	}
	
	private void deleteDetails() {
		try {
			SmsPODtlDM save = new SmsPODtlDM();
			if (tblSmsPODtl.getValue() != null) {
				save = beansmsPODtl.getItem(tblSmsPODtl.getValue()).getBean();
				listPODetails.remove(save);
				poDetailsResetFields();
				loadPODetails();
				btndelete.setEnabled(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private BigDecimal gerPercentageValue(BigDecimal percent, BigDecimal value) {
		return (percent.multiply(value).divide(new BigDecimal("100"))).setScale(2, RoundingMode.CEILING);
	}
	
	private void getEditableTable() {
		tblSmsAccept.setEditable(true);
		tblSmsAccept.setTableFieldFactory(new TableFieldFactory() {
			private static final long serialVersionUID = 1L;
			
			public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
				if (propertyId.toString().equals("acceptoption")) {
					GERPComboBox acceptoption = new GERPComboBox(null, BASEConstants.T_SMS_PO_ACCEPTANCE,
							BASEConstants.ACCEPT_OPTION);
					acceptoption.setWidth("130");
					acceptoption.setNullSelectionAllowed(false);
					return acceptoption;
				}
				if (propertyId.toString().equals("acceptdesc")) {
					TextField tf = new TextField();
					tf.setInputPrompt("Enter Remarks");
					tf.setWidth("400");
					tf.setNullRepresentation("");
					return tf;
				}
				return null;
			}
		});
	}
	
	private void loadQuoteDetails() {
		try {
			tfBasictotal.setReadOnly(false);
			tfBasictotal.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getBasicTotal().toString());
			tfBasictotal.setReadOnly(true);
			tfVersionNo.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getQuoteVersion().toString());
			taRemark.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getRemarks());
			taLiquidatedDamage.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getLiqdatedDamage());
			tfPackingPer.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getPackingPrcnt().toString());
			tfPackingValue.setReadOnly(false);
			tfPackingValue.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getPackingValue().toString());
			tfPackingValue.setReadOnly(true);
			tfSubTotal.setReadOnly(false);
			tfSubTotal.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getSubTotal().toString());
			tfSubTotal.setReadOnly(true);
			tfVatPer.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getVatPrcnt().toString());
			tfVatValue.setReadOnly(false);
			tfVatValue.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getVatValue().toString());
			tfVatValue.setReadOnly(true);
			tfEDPer.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getEd_Prcnt().toString());
			tfEDValue.setReadOnly(false);
			tfEDValue.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getEdValue().toString());
			tfEDValue.setReadOnly(true);
			tfHEDPer.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getHedPrcnt().toString());
			tfHEDValue.setReadOnly(false);
			tfHEDValue.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getHedValue().toString());
			tfHEDValue.setReadOnly(true);
			tfCessPer.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getCessPrcnt().toString());
			tfCessValue.setReadOnly(false);
			tfCessValue.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getCessValue().toString());
			tfCessValue.setReadOnly(true);
			tfCstPer.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getCstPrcnt().toString());
			tfCstValue.setReadOnly(false);
			tfCstValue.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getCstValue().toString());
			tfCstValue.setReadOnly(true);
			tfSubTaxTotal.setReadOnly(false);
			tfSubTaxTotal.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getSubTaxTotal().toString());
			tfSubTaxTotal.setReadOnly(true);
			tfFreightValue.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getFreightValue().toString());
			tfOtherValue.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getOthersValue().toString());
			tfGrandtotal.setReadOnly(false);
			tfGrandtotal.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getGrandTotal().toString());
			tfGrandtotal.setReadOnly(true);
			System.out.println(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getPaymentTerms());
			if (((SmsQuoteHdrDM) cbQuoteNo.getValue()).getPaymentTerms() != null) {
				cbPaymetTerms.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getPaymentTerms().toString());
			}
			if (((SmsQuoteHdrDM) cbQuoteNo.getValue()).getFreightTerms() != null) {
				cbFreightTerms.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getFreightTerms().toString());
			}
			if (((SmsQuoteHdrDM) cbQuoteNo.getValue()).getWarrantyTerms() != null) {
				cbWarrentyTerms.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getWarrantyTerms().toString());
			}
			if (((SmsQuoteHdrDM) cbQuoteNo.getValue()).getDeliveryTerms() != null) {
				cbDelTerms.setValue(((SmsQuoteHdrDM) cbQuoteNo.getValue()).getDeliveryTerms().toString());
			}
			
			// load quote details
			listPODetails = new ArrayList<SmsPODtlDM>();
			for (SmsQuoteDtlDM quoteDtlDM : serviceQuoteDtl.getsmsquotedtllist(null,
					((SmsQuoteHdrDM) cbQuoteNo.getValue()).getQuoteId(), null, null)) {
				SmsPODtlDM smspoDtlDM = new SmsPODtlDM();
				smspoDtlDM.setProductid(quoteDtlDM.getProductid());
				smspoDtlDM.setProdname(quoteDtlDM.getProdname());
				smspoDtlDM.setUnitrate(quoteDtlDM.getUnitrate());
				smspoDtlDM.setPdcValue(quoteDtlDM.getPdcValue());
				smspoDtlDM.setPoqty(quoteDtlDM.getQuoteqty());
				smspoDtlDM.setProduom(quoteDtlDM.getProduom());
				smspoDtlDM.setBasicvalue(quoteDtlDM.getBasicvalue());
				smspoDtlDM.setPodtlstatus("Active");
				smspoDtlDM.setLastupdatedby(username);
				smspoDtlDM.setLastupdateddt(DateUtils.getcurrentdate());
				listPODetails.add(smspoDtlDM);
			}
			loadPODetails();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
			System.out.println("poid-->" + poid);
			parameterMap.put("poid", poid);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/poReport"); // productlist is the name of my jasper
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
	
	// Load Product List
	private void loadfullProduct() {
		try {
			BeanItemContainer<ProductDM> beanProduct = new BeanItemContainer<ProductDM>(ProductDM.class);
			beanProduct.addAll(serviceProduct.getProductList(companyid, null, null, null, "Active", null, null, "P"));
			cbProduct.setContainerDataSource(beanProduct);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
