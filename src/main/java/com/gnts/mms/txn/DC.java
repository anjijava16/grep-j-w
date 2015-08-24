/**
 * File Name 		: DC.java 
 * Description 		: this class is used for add/edit DC  details. 
 * Author 			: Madhu T
 * Date 			: Oct-24-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1        Oct-24-2014     	Madhu T	       		 Initial Version
 * 0.2        nov-15-2014     	Arun jeyaraj R        modification
 **/
package com.gnts.mms.txn;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.base.service.mst.VendorService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.domain.txn.ClientsContactsDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.crm.service.txn.ClientContactsService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.DCDtlDM;
import com.gnts.mms.domain.txn.DcHdrDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.DCDtlService;
import com.gnts.mms.service.txn.DcHdrService;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class DC extends BaseTransUI {
	// Bean Creation
	private DcHdrService serviceDCHdr = (DcHdrService) SpringContextHelper.getBean("DCHdr");
	private DCDtlService serviceDCDtlHdr = (DCDtlService) SpringContextHelper.getBean("DCDtl");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private ClientContactsService serviceClntContact = (ClientContactsService) SpringContextHelper
			.getBean("clientContact");
	private List<DCDtlDM> DCDtlList = null;
	// form layout for input controls
	private FormLayout flDCCol1, flDCCol2, flDCCol3, flColumn4, flDCIssueDtlCol1, flDCIssueDtlCol2, flDCIssueDtlCol3,
			flDCIssueDtlCol4, flDCIssueDtlCol5;
	private Button btndelete = new GERPButton("Delete", "delete", this);
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlDC = new HorizontalLayout();
	private HorizontalLayout hlDCslap = new HorizontalLayout();
	private VerticalLayout vlDC, vlDCHdrAndDtl;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Work Order Details
	private Button btnAddDtl = new GERPButton("Add", "addbt", this);
	private ComboBox cbGoodsStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	// Dtl Status ComboBox
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_INVOICE_HDR,
			BASEConstants.INVOICE_STATUS);
	private TextField tfDcNo, tfDCQty, rfGoodsuom;
	private ComboBox cbVendor, cbClients, cbModeOfTrans, cbPersonName, cbEnquiry, cbGoodsType, cbMaterialId, cbProduct,
			cbDCType, cbwindTechPers, cbwindcommPerson, cbDCTypeRNR;
	private DateField dfDcDt;
	private TextArea taRemarks, taGoodsDesc;
	private Table tblDCDetails;
	private GERPTextArea taClientAddres = new GERPTextArea("Address");
	private BeanItemContainer<DcHdrDM> beanDcHdrDM = null;
	private BeanItemContainer<DCDtlDM> beanDcDtlDM = null;
	// local variables declaration
	private String pkDCId;
	private Long dcHdrId;
	private Long companyid, moduleId, branchID;
	private Long issueId, employeeId;
	private int recordCnt = 0;
	private int recordCntDtl = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private MmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	private Logger logger = Logger.getLogger(DC.class);
	private String status;
	private static final long serialVersionUID = 1L;
	
	// Constructor received the parameters from Login UI class
	public DC() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		employeeId = (Long) UI.getCurrent().getSession().getAttribute("employeeId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside DC() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		// Initialization for work order Details user input components
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting DC UI");
		btndelete.setEnabled(false);
		btnAddDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDtlDetails()) {
					saveDCDtlListDetails();
				}
			}
		});
		cbEnquiry = new GERPComboBox("Enquiry No.");
		cbEnquiry.setItemCaptionPropertyId("enquiryNo");
		cbEnquiry.setImmediate(true);
		cbEnquiry.setNullSelectionAllowed(false);
		cbEnquiry.setWidth("150");
		cbEnquiry.setRequired(true);
		cbEnquiry.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				loadCustomerList();
				loadClientContacts();
				loadclienTecCont();
			}
		});
		loadEnquiryList();
		tblDCDetails = new GERPTable();
		tblDCDetails.setPageLength(7);
		tblDCDetails.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblDCDetails.isSelected(event.getItemId())) {
					tblDCDetails.setImmediate(true);
					btnAddDtl.setCaption("Add");
					btnAddDtl.setStyleName("savebt");
					btndelete.setEnabled(false);
					resetDCDetails();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtl.setCaption("Update");
					btnAddDtl.setStyleName("savebt");
					btndelete.setEnabled(true);
					editDtls();
				}
			}
		});
		cbwindTechPers = new GERPComboBox("Tech.Person");
		cbwindTechPers.setWidth("130");
		cbwindcommPerson = new GERPComboBox("Commer.Person");
		cbwindcommPerson.setWidth("130");
		taClientAddres.setWidth("150px");
		taClientAddres.setHeight("50px");
		// DC NO Textfield
		tfDcNo = new GERPTextField("DC No");
		tfDcNo.setReadOnly(false);
		// DC Type combobox
		cbDCTypeRNR = new GERPComboBox("DC Type");
		cbDCTypeRNR.addItem("Returnable");
		cbDCTypeRNR.addItem("Non Returnable");
		cbDCType = new GERPComboBox("Department");
		cbDCType.addItem("Marketing");
		cbDCType.addItem("Store");
		cbDCType.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbDCType.getValue() != null) {
					if (cbDCType.getValue().equals("Marketing")) {
						cbClients.setEnabled(true);
						cbVendor.setEnabled(false);
						cbwindcommPerson.setEnabled(true);
						cbwindTechPers.setEnabled(true);
						cbClients.setValue(cbClients.getValue());
						if (cbEnquiry.getValue() != null) {
							Long clientid = serviceEnqHeader
									.getSmsEnqHdrList(companyid, (Long) cbEnquiry.getValue(), null, null, null, "F",
											null, null).get(0).getClientId();
							if (serviceClients
									.getClientDetails(companyid, clientid, null, null, null, null, null, null,
											"Active", "F").get(0).getClientAddress() != null) {
								taClientAddres.setValue(serviceClients
										.getClientDetails(companyid, clientid, null, null, null, null, null, null,
												"Active", "F").get(0).getClientAddress());
							}
						}
					} else if (cbDCType.getValue().equals("Store")) {
						cbVendor.setValue(cbVendor.getValue());
						cbClients.setEnabled(false);
						cbVendor.setEnabled(true);
						taClientAddres.setValue("");
						btndelete.setEnabled(true);
						cbwindcommPerson.setEnabled(false);
						cbwindTechPers.setEnabled(false);
					}
				}
			}
		});
		btndelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndelete == event.getButton()) {
					btnAddDtl.setCaption("Add");
					deleteDetails();
				}
			}
		});
		// Vendor Name Combobox
		cbVendor = new GERPComboBox("Vendor");
		cbVendor.setItemCaptionPropertyId("vendorName");
		loadVendorList();
		cbVendor.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (serviceVendor
						.getVendorList(null, (Long) cbVendor.getValue(), companyid, null, null, null, null, null,
								"Active", null, "F").get(0).getVendorAddress() != null) {
					String vendoraddress = serviceVendor
							.getVendorList(null, (Long) cbVendor.getValue(), companyid, null, null, null, null, null,
									"Active", null, "F").get(0).getVendorAddress();
					taClientAddres.setValue(vendoraddress);
				}
				cbwindcommPerson.setEnabled(false);
				cbwindTechPers.setEnabled(false);
			}
		});
		cbVendor.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				cbwindcommPerson.setEnabled(false);
				cbwindTechPers.setEnabled(false);
				if (serviceVendor
						.getVendorList(null, (Long) cbVendor.getValue(), companyid, null, null, null, null, null,
								"Active", null, "F").get(0).getVendorAddress() != null) {
					String vendoraddress = serviceVendor
							.getVendorList(null, (Long) cbVendor.getValue(), companyid, null, null, null, null, null,
									"Active", null, "F").get(0).getVendorAddress();
					taClientAddres.setValue(vendoraddress);
				}
			}
		}); // Customer Name Combobox
		cbClients = new GERPComboBox("Client");
		cbClients.setItemCaptionPropertyId("clientCode");
		cbVendor.setEnabled(false);
		cbClients.setEnabled(false);
		// Mode of transaction GERPComboBox
		cbModeOfTrans = new GERPComboBox("Mode Of Dispatch");
		cbModeOfTrans.setItemCaptionPropertyId("lookupname");
		loadModeOfTransList();
		// Person Name GERPComboBox
		cbPersonName = new GERPComboBox("Person Name");
		cbPersonName.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		// DC Date PopupDateField
		dfDcDt = new PopupDateField("DC Date");
		// Remarks TextArea
		taRemarks = new TextArea("Remarks");
		taRemarks.setWidth("150px");
		taRemarks.setHeight("50px");
		taRemarks.setNullRepresentation("");
		// Material Name GERPComboBox
		cbMaterialId = new GERPComboBox("Material");
		cbMaterialId.setItemCaptionPropertyId("materialName");
		loadMaterialList();
		cbMaterialId.setImmediate(true);
		cbMaterialId.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbMaterialId.getValue() != null) {
					rfGoodsuom.setReadOnly(false);
					rfGoodsuom.setValue(((MaterialDM) cbMaterialId.getValue()).getMaterialUOM());
					rfGoodsuom.setReadOnly(true);
				}
			}
		});
		// Product Name GERPComboBox
		cbProduct = new GERPComboBox("Product");
		cbProduct.setItemCaptionPropertyId("prodname");
		loadProductList();
		cbProduct.setImmediate(true);
		cbProduct.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbProduct.getValue() != null) {
					rfGoodsuom.setReadOnly(false);
					rfGoodsuom.setValue(((ProductDM) cbProduct.getValue()).getUom());
					rfGoodsuom.setReadOnly(true);
				}
			}
		});
		cbMaterialId.setEnabled(false);
		cbProduct.setEnabled(false);
		// Detail Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.T_SMS_INVOICE_HDR, BASEConstants.INVOICE_STATUS);
		// DC Details
		// Goods Type
		cbGoodsType = new GERPComboBox("Goods Type");
		cbGoodsType.addItem("Material");
		cbGoodsType.addItem("Product");
		cbGoodsType.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbGoodsType.getValue() != null) {
					if (cbGoodsType.getValue().equals("Material")) {
						cbProduct.setEnabled(false);
						cbMaterialId.setEnabled(true);
						cbMaterialId.setValue(cbMaterialId.getValue());
					} else if (cbGoodsType.getValue().equals("Product")) {
						cbMaterialId.setEnabled(false);
						cbProduct.setEnabled(true);
						cbProduct.setValue(cbProduct.getValue());
					}
				}
			}
		});
		// Goods Desc TextArea
		taGoodsDesc = new GERPTextArea("Goods Desc");
		taGoodsDesc.setHeight("25px");
		// DC Qty TextField
		tfDCQty = new GERPTextField("DC Qty");
		// Goods UOM combobox
		rfGoodsuom = new GERPTextField("Goods UOM");
		// Goods Dtl Status Combobox
		cbGoodsStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadDCDtl();
		btnAddDtl.setStyleName("add");
		dfDcDt.setValue(null);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flDCCol1 = new FormLayout();
		flDCCol2 = new FormLayout();
		flDCCol3 = new FormLayout();
		flColumn4 = new FormLayout();
		flDCCol1.addComponent(tfDcNo);
		flDCCol2.addComponent(cbDCType);
		flDCCol3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flDCCol1);
		hlSearchLayout.addComponent(flDCCol2);
		hlSearchLayout.addComponent(flDCCol3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flDCCol1 = new FormLayout();
		flDCCol2 = new FormLayout();
		flDCCol3 = new FormLayout();
		flColumn4 = new FormLayout();
		flDCCol1.addComponent(tfDcNo);
		flDCCol1.addComponent(cbEnquiry);
		flDCCol1.addComponent(cbDCType);
		flDCCol1.addComponent(cbDCTypeRNR);
		dfDcDt.setWidth("130");
		flDCCol1.addComponent(dfDcDt);
		flDCCol2.addComponent(cbVendor);
		flDCCol2.addComponent(cbClients);
		cbwindcommPerson.setWidth("150");
		flDCCol2.addComponent(cbwindcommPerson);
		cbwindTechPers.setWidth("150");
		flDCCol2.addComponent(cbwindTechPers);
		flDCCol3.addComponent(cbModeOfTrans);
		flDCCol3.addComponent(cbPersonName);
		flDCCol3.addComponent(taClientAddres);
		taRemarks.setHeight("80");
		flColumn4.addComponent(taRemarks);
		cbStatus.setWidth("150");
		flColumn4.addComponent(cbStatus);
		hlDC = new HorizontalLayout();
		hlDC.addComponent(flDCCol1);
		hlDC.addComponent(flDCCol2);
		hlDC.addComponent(flDCCol3);
		hlDC.addComponent(flColumn4);
		hlDC.setSpacing(true);
		hlDC.setMargin(true);
		// Adding DCDtl components
		// Add components for User Input Layout
		flDCIssueDtlCol1 = new FormLayout();
		flDCIssueDtlCol2 = new FormLayout();
		flDCIssueDtlCol3 = new FormLayout();
		flDCIssueDtlCol4 = new FormLayout();
		flDCIssueDtlCol5 = new FormLayout();
		flDCIssueDtlCol1.addComponent(cbGoodsType);
		flDCIssueDtlCol1.addComponent(cbMaterialId);
		flDCIssueDtlCol2.addComponent(cbProduct);
		flDCIssueDtlCol2.addComponent(taGoodsDesc);
		flDCIssueDtlCol3.addComponent(tfDCQty);
		flDCIssueDtlCol3.addComponent(rfGoodsuom);
		flDCIssueDtlCol4.addComponent(cbGoodsStatus);
		flDCIssueDtlCol5.addComponent(btnAddDtl);
		flDCIssueDtlCol5.addComponent(btndelete);
		hlDCslap = new HorizontalLayout();
		hlDCslap.addComponent(flDCIssueDtlCol1);
		hlDCslap.addComponent(flDCIssueDtlCol2);
		hlDCslap.addComponent(flDCIssueDtlCol3);
		hlDCslap.addComponent(flDCIssueDtlCol4);
		hlDCslap.addComponent(flDCIssueDtlCol5);
		hlDCslap.setSpacing(true);
		hlDCslap.setMargin(true);
		vlDC = new VerticalLayout();
		vlDC.addComponent(hlDCslap);
		vlDC.addComponent(tblDCDetails);
		vlDC.setSpacing(true);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlDC, "DC Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		vlDCHdrAndDtl = new VerticalLayout();
		vlDCHdrAndDtl.addComponent(GERPPanelGenerator.createPanel(hlDC));
		vlDCHdrAndDtl.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		vlDCHdrAndDtl.setSpacing(true);
		vlDCHdrAndDtl.setWidth("100%");
		hlUserInputLayout.addComponent(vlDCHdrAndDtl);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<DcHdrDM> dcHdrList = new ArrayList<DcHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + ", " + cbStatus.getValue());
		dcHdrList = serviceDCHdr.getMmsDcHdrList(null, tfDcNo.getValue(), null, null, null,
				(String) cbDCType.getValue(), null, null, (String) cbStatus.getValue(), "F");
		recordCnt = dcHdrList.size();
		beanDcHdrDM = new BeanItemContainer<DcHdrDM>(DcHdrDM.class);
		beanDcHdrDM.addAll(dcHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the DC. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanDcHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "enqNo", "dcNo", "dcType", "techPerson", "commPerson",
				"lastUpdateddt", "lastUpdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Enquiry No", "DC No", "DC Type", "Technical Person",
				"Commercial Person", "Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("dcId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadDCDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			logger.info("Company ID : " + companyid + " | saveDCDtlListDetails User Name : " + username + " > "
					+ "Search Parameters are " + companyid + ", " + tfDCQty.getValue() + ", "
					+ (String) cbGoodsStatus.getValue() + ", " + issueId);
			recordCntDtl = DCDtlList.size();
			beanDcDtlDM = new BeanItemContainer<DCDtlDM>(DCDtlDM.class);
			beanDcDtlDM.addAll(DCDtlList);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the DCslap. result set");
			tblDCDetails.setContainerDataSource(beanDcDtlDM);
			tblDCDetails.setVisibleColumns(new Object[] { "goodsType", "goodsDesc", "dcQty", "status", "lastUpdateddt",
					"lastUpdatedby" });
			tblDCDetails.setColumnHeaders(new String[] { "Goods Type", "Description", "DC Qty", "Status",
					"Updated Date", "Updated By" });
			tblDCDetails.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCntDtl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		// tfDcNo.setReadOnly(false);
		tfDcNo.setValue("");
		// tfDcNo.setReadOnly(true);
		dfDcDt.setValue(new Date());
		cbDCType.setValue(null);
		cbVendor.setValue(null);
		cbClients.setValue(null);
		cbModeOfTrans.setValue(null);
		cbPersonName.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		taRemarks.setValue("");
		cbClients.setComponentError(null);
		cbVendor.setComponentError(null);
		cbDCType.setComponentError(null);
		cbModeOfTrans.setComponentError(null);
		cbGoodsType.setComponentError(null);
		cbMaterialId.setComponentError(null);
		cbProduct.setComponentError(null);
		dfDcDt.setComponentError(null);
		tfDCQty.setComponentError(null);
		cbPersonName.setComponentError(null);
		cbwindcommPerson.setValue(null);
		cbwindTechPers.setValue(null);
		taClientAddres.setValue("");
		cbwindcommPerson.setContainerDataSource(null);
		cbwindTechPers.setContainerDataSource(null);
		cbEnquiry.setComponentError(null);
		cbEnquiry.setValue(null);
		cbEnquiry.setRequired(true);
		DCDtlList = new ArrayList<DCDtlDM>();
		tblDCDetails.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editHdrDCDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			DcHdrDM editHdrDC = beanDcHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			dcHdrId = editHdrDC.getDcId();
			if (editHdrDC.getDcDate() != null) {
				dfDcDt.setValue(editHdrDC.getDcDate1());
			}
			cbEnquiry.setValue(editHdrDC.getEnquiryId());
			if (editHdrDC.getCommPerson() != null) {
				cbwindcommPerson.setValue(editHdrDC.getCommPerson());
			}
			if (editHdrDC.getTechPerson() != null) {
				cbwindTechPers.setValue(editHdrDC.getTechPerson());
			}
			if (editHdrDC.getClientAddress() != null) {
				taClientAddres.setValue(editHdrDC.getClientAddress());
			}
			tfDcNo.setReadOnly(false);
			tfDcNo.setValue(editHdrDC.getDcNo());
			cbDCType.setValue(editHdrDC.getDcType());
			cbVendor.setValue(editHdrDC.getVendorId());
			cbClients.setValue(editHdrDC.getCustomerId());
			cbModeOfTrans.setValue(editHdrDC.getModeofTrans());
			cbPersonName.setValue(editHdrDC.getPersonName());
			taRemarks.setValue(editHdrDC.getRemarks());
			cbStatus.setValue(editHdrDC.getDcStatus());
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected DC. Id -> "
					+ issueId);
			DCDtlList.addAll(serviceDCDtlHdr.getDCCtlList(null, dcHdrId, null, null, null, null, null, "F"));
		}
		loadDCDtl();
		comments = new MmsComments(vlTableForm, null, companyid, null, null, null, null, null, dcHdrId, null, status);
		comments.loadsrch(true, null, null, null, null, null, null, null, dcHdrId, null);
	}
	
	private void editDtls() {
		hlUserInputLayout.setVisible(true);
		if (tblDCDetails.getValue() != null) {
			DCDtlDM dcDtlDM = beanDcDtlDM.getItem(tblDCDetails.getValue()).getBean();
			cbGoodsType.setValue(dcDtlDM.getGoodsType());
			Long matId = dcDtlDM.getMaterialId();
			Collection<?> empColId = cbMaterialId.getItemIds();
			for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMaterialId.getItem(itemIdClient);
				// Get the actual bean and use the data
				MaterialDM matObj = (MaterialDM) itemclient.getBean();
				if (matId != null && matId.equals(matObj.getMaterialId())) {
					cbMaterialId.setValue(itemIdClient);
				}
			}
			Long prodId = dcDtlDM.getProductId();
			Collection<?> empProdId = cbProduct.getItemIds();
			for (Iterator<?> iteratorclient = empProdId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbProduct.getItem(itemIdClient);
				// Get the actual bean and use the data
				ProductDM prodObj = (ProductDM) itemclient.getBean();
				if (prodId != null && prodId.equals(prodObj.getProdid())) {
					cbProduct.setValue(itemIdClient);
				}
			}
			if (dcDtlDM.getGoodsDesc() != null) {
				taGoodsDesc.setValue(dcDtlDM.getGoodsDesc());
			}
			if (dcDtlDM.getDcQty() != null) {
				tfDCQty.setValue(dcDtlDM.getDcQty().toString());
			}
			if (dcDtlDM.getGoodsUOM() != null) {
				rfGoodsuom.setReadOnly(false);
				rfGoodsuom.setValue(dcDtlDM.getGoodsUOM());
				rfGoodsuom.setReadOnly(true);
			}
			cbGoodsStatus.setValue(dcDtlDM.getStatus());
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		dfDcDt.setValue(null);
		cbDCType.setValue(null);
		tfDcNo.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		dfDcDt.setRequired(true);
		cbDCType.setRequired(true);
		cbModeOfTrans.setRequired(true);
		cbPersonName.setRequired(true);
		cbGoodsType.setRequired(true);
		cbMaterialId.setRequired(true);
		cbProduct.setRequired(true);
		tfDCQty.setRequired(true);
		tfDCQty.setValue("0");
		resetFields();
		loadDCDtl();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tblDCDetails.setVisible(true);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "MM_DCNO").get(0);
			tfDcNo.setReadOnly(false);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfDcNo.setValue(slnoObj.getKeyDesc());
				tfDcNo.setReadOnly(true);
			} else {
				tfDcNo.setReadOnly(false);
			}
		}
		catch (Exception e) {
		}
		tblDCDetails.setVisible(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtl.setCaption("Add");
		tblDCDetails.setVisible(true);
		comments = new MmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null);
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for DC. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MMS_DC_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkDCId);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		cbClients.setComponentError(null);
		cbVendor.setComponentError(null);
		cbDCType.setComponentError(null);
		cbModeOfTrans.setComponentError(null);
		cbPersonName.setComponentError(null);
		cbGoodsType.setComponentError(null);
		cbMaterialId.setComponentError(null);
		cbProduct.setComponentError(null);
		dfDcDt.setComponentError(null);
		tfDCQty.setComponentError(null);
		dfDcDt.setRequired(false);
		tfDCQty.setRequired(false);
		cbDCType.setRequired(false);
		tfDcNo.setReadOnly(false);
		cbModeOfTrans.setRequired(false);
		cbPersonName.setRequired(false);
		hlCmdBtnLayout.setVisible(true);
		tblDCDetails.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		resetDCDetails();
		loadSrchRslt();
		dfDcDt.setValue(null);
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbStatus.setVisible(true);
		dfDcDt.setRequired(true);
		cbDCType.setRequired(true);
		cbModeOfTrans.setRequired(true);
		cbPersonName.setRequired(true);
		cbGoodsType.setRequired(true);
		cbMaterialId.setRequired(true);
		cbProduct.setRequired(true);
		tfDCQty.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		if (tfDcNo.getValue() == null || tfDcNo.getValue().trim().length() == 0) {
			tfDcNo.setReadOnly(false);
		}
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		resetFields();
		editHdrDCDetails();
		editDtls();
	}
	
	private void resetDCDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbGoodsType.setValue(null);
		cbMaterialId.setValue(null);
		cbProduct.setValue(null);
		taGoodsDesc.setValue("");
		tfDCQty.setValue("0");
		rfGoodsuom.setReadOnly(false);
		rfGoodsuom.setValue("");
		rfGoodsuom.setReadOnly(true);
		cbProduct.setComponentError(null);
		cbMaterialId.setComponentError(null);
		cbGoodsType.setComponentError(null);
		tfDCQty.setComponentError(null);
		cbGoodsStatus.setValue(cbGoodsStatus.getItemIds().iterator().next());
	}
	
	private boolean validateDtlDetails() {
		boolean errorflag = true;
		cbProduct.setComponentError(null);
		cbMaterialId.setComponentError(null);
		cbGoodsType.setComponentError(null);
		tfDCQty.setComponentError(null);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbGoodsType.getValue() == null) {
			cbGoodsType.setComponentError(new UserError(GERPErrorCodes.NULL_GOODS_TYPE));
			errorflag = false;
		} else {
			cbGoodsType.setComponentError(null);
		}
		cbGoodsType.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbGoodsType.getValue() == ("Material")) {
					cbMaterialId.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
					cbProduct.setComponentError(null);
				} else {
					if (cbGoodsType.getValue() == ("Product")) {
						cbProduct.setComponentError(new UserError(GERPErrorCodes.PRODUCT_NAME));
						cbMaterialId.setComponentError(null);
					}
				}
			}
		});
		if ((tfDCQty.getValue() == null) || tfDCQty.getValue().trim().length() == 0) {
			tfDCQty.setComponentError(new UserError(GERPErrorCodes.NULL_DC_QTY));
			errorflag = false;
		} else {
			tfDCQty.setComponentError(null);
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfDCQty.getValue());
			if (achievedQty < 0) {
				tfDCQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorflag = false;
			}
		}
		catch (Exception e) {
			tfDCQty.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATION));
			errorflag = false;
		}
		return errorflag;
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		dfDcDt.setComponentError(null);
		cbDCType.setComponentError(null);
		cbModeOfTrans.setComponentError(null);
		cbPersonName.setComponentError(null);
		cbEnquiry.setComponentError(null);
		cbGoodsType.setComponentError(null);
		tfDCQty.setComponentError(null);
		cbProduct.setComponentError(null);
		cbMaterialId.setComponentError(null);
		errorFlag = false;
		if (dfDcDt.getValue() == null) {
			dfDcDt.setComponentError(new UserError(GERPErrorCodes.NULL_DC_DATE));
			errorFlag = true;
		}
		if (cbDCType.getValue() == null) {
			cbDCType.setComponentError(new UserError(GERPErrorCodes.NULL_DC_TYPE));
			errorFlag = true;
		}
		if (cbEnquiry.getValue() == null) {
			cbEnquiry.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorFlag = true;
		} else {
			cbEnquiry.setComponentError(null);
		}
		if (cbPersonName.getValue() == null) {
			cbPersonName.setComponentError(new UserError(GERPErrorCodes.NULL_PERSON_NAME));
			errorFlag = true;
		}
		if (cbModeOfTrans.getValue() == null) {
			cbModeOfTrans.setComponentError(new UserError(GERPErrorCodes.NULL_MODE_OF_TRANSACTION));
			errorFlag = true;
		}
		if (tblDCDetails.size() == 0) {
			cbGoodsType.setComponentError(new UserError(GERPErrorCodes.NULL_GOODS_TYPE));
			tfDCQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			DcHdrDM dcHdrDM = new DcHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				dcHdrDM = beanDcHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			dcHdrDM.setDcNo(tfDcNo.getValue());
			dcHdrDM.setCompanyId(companyid);
			dcHdrDM.setBranchId(branchID);
			if (cbwindTechPers.getValue() != null) {
				dcHdrDM.setTechPerson(cbwindTechPers.getValue().toString());
			}
			if (cbwindcommPerson.getValue() != null) {
				dcHdrDM.setCommPerson(cbwindcommPerson.getValue().toString());
			}
			dcHdrDM.setEnquiryId((Long) cbEnquiry.getValue());
			dcHdrDM.setClientAddress(taClientAddres.getValue());
			dcHdrDM.setDcDate((Date) dfDcDt.getValue());
			dcHdrDM.setDcType(cbDCType.getValue().toString());
			if (cbVendor.getValue() != null) {
				dcHdrDM.setVendorId((Long.valueOf(cbVendor.getValue().toString())));
			}
			if (cbClients.getValue() != null) {
				dcHdrDM.setCustomerId(Long.valueOf(cbClients.getValue().toString()));
			}
			dcHdrDM.setModeofTrans(cbModeOfTrans.getValue().toString());
			dcHdrDM.setPersonName(cbPersonName.getValue().toString());
			if (taRemarks.getValue() != null) {
				dcHdrDM.setRemarks(taRemarks.getValue().toString());
			}
			dcHdrDM.setDcStatus(cbStatus.getValue().toString());
			dcHdrDM.setPreparedBy(employeeId);
			dcHdrDM.setReviewedBy(employeeId);
			dcHdrDM.setActionedBy(employeeId);
			dcHdrDM.setLastUpdateddt(DateUtils.getcurrentdate());
			dcHdrDM.setLastUpdatedby(username);
			serviceDCHdr.saveOrUpdate(dcHdrDM);
			dcHdrId = dcHdrDM.getDcId();
			@SuppressWarnings("unchecked")
			Collection<DCDtlDM> colPlanDtls = ((Collection<DCDtlDM>) tblDCDetails.getVisibleItemIds());
			for (DCDtlDM saveDtl : (Collection<DCDtlDM>) colPlanDtls) {
				saveDtl.setDcId(dcHdrDM.getDcId());
				serviceDCDtlHdr.saveOrUpdate(saveDtl);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "MM_DCNO").get(
							0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "MM_DCNO");
					}
				}
				catch (Exception e) {
				}
			}
			comments.savedc(dcHdrDM.getDcId(), dcHdrDM.getDcStatus());
			comments.resetfields();
			resetDCDetails();
			loadSrchRslt();
			loadDCDtl();
			tfDcNo.setReadOnly(false);
			tfDcNo.setValue(dcHdrDM.getDcNo());
			tfDcNo.setReadOnly(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		resetFields();
	}
	
	private void saveDCDtlListDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		DCDtlDM dcDtlDM = new DCDtlDM();
		if (tblDCDetails.getValue() != null) {
			dcDtlDM = beanDcDtlDM.getItem(tblDCDetails.getValue()).getBean();
			DCDtlList.remove(dcDtlDM);
		}
		dcDtlDM.setGoodsType((String) cbGoodsType.getValue());
		if (cbMaterialId.getValue() != null) {
			dcDtlDM.setMaterialId(((MaterialDM) cbMaterialId.getValue()).getMaterialId());
			dcDtlDM.setMaterialName(((MaterialDM) cbMaterialId.getValue()).getMaterialName());
		}
		if (cbProduct.getValue() != null) {
			dcDtlDM.setProductId(((ProductDM) cbProduct.getValue()).getProdid());
			dcDtlDM.setProductName(((ProductDM) cbProduct.getValue()).getProdname());
		}
		dcDtlDM.setGoodsDesc(taGoodsDesc.getValue().toString());
		dcDtlDM.setDcQty(Long.valueOf(tfDCQty.getValue().toString()));
		if (rfGoodsuom.getValue() != null) {
			rfGoodsuom.setReadOnly(false);
			dcDtlDM.setGoodsUOM(rfGoodsuom.getValue().toString());
			rfGoodsuom.setReadOnly(true);
		}
		if (cbGoodsStatus.getValue() != null) {
			dcDtlDM.setStatus((String) cbGoodsStatus.getValue());
		}
		dcDtlDM.setLastUpdateddt(DateUtils.getcurrentdate());
		dcDtlDM.setLastUpdatedby(username);
		DCDtlList.add(dcDtlDM);
		loadDCDtl();
		resetDCDetails();
		btnAddDtl.setCaption("Add");
	}
	
	/*
	 * loadMaterialList()-->this function is used for load the Material name
	 */
	private void loadMaterialList() {
		BeanItemContainer<MaterialDM> beanMaterial = new BeanItemContainer<MaterialDM>(MaterialDM.class);
		beanMaterial.addAll(serviceMaterial.getMaterialList(null, null, null, null, null, null, null, null, "Active",
				"F"));
		cbMaterialId.setContainerDataSource(beanMaterial);
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the Employee name
	 */
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<String, EmployeeDM> beanPersonEmployeeDM = new BeanContainer<String, EmployeeDM>(EmployeeDM.class);
		beanPersonEmployeeDM.setBeanIdProperty("firstname");
		beanPersonEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null,
				null, null, "P"));
		cbPersonName.setContainerDataSource(beanPersonEmployeeDM);
	}
	
	/*
	 * loadVendorList()-->this function is used for load the vendor name
	 */
	private void loadVendorList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, VendorDM> beanVendorDM = new BeanContainer<Long, VendorDM>(VendorDM.class);
		beanVendorDM.setBeanIdProperty("vendorId");
		beanVendorDM.addAll(serviceVendor.getVendorList(null, null, null, null, null, null, null, null, "Active", null,
				"P"));
		cbVendor.setContainerDataSource(beanVendorDM);
	}
	
	/*
	 * loadCustomerList()-->this function is used for load the customer name
	 */
	private void loadCustomerList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading client Search...");
		Long clientid = serviceEnqHeader
				.getSmsEnqHdrList(companyid, (Long) cbEnquiry.getValue(), null, null, null, "P", null, null).get(0)
				.getClientId();
		BeanContainer<Long, ClientDM> beanclientDM = new BeanContainer<Long, ClientDM>(ClientDM.class);
		beanclientDM.setBeanIdProperty("clientId");
		beanclientDM.addAll(serviceClients.getClientDetails(companyid, clientid, null, null, null, null, null, null,
				"Active", "P"));
		cbClients.setContainerDataSource(beanclientDM);
		cbClients.setValue(clientid);
	}
	
	/*
	 * loadModeOfTransList()-->this function is used for load the ModeOfTransaction name
	 */
	private void loadModeOfTransList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
				"MM_TRNSPRT"));
		cbModeOfTrans.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadProductList()-->this function is used for load the Product name
	 */
	private void loadProductList() {
		BeanItemContainer<ProductDM> beanProduct = new BeanItemContainer<ProductDM>(ProductDM.class);
		beanProduct.addAll(serviceProduct.getProductList(null, null, null, null, "Active", null, null, "F"));
		cbProduct.setContainerDataSource(beanProduct);
	}
	
	private void deleteDetails() {
		DCDtlDM save = new DCDtlDM();
		if (tblDCDetails.getValue() != null) {
			save = beanDcDtlDM.getItem(tblDCDetails.getValue()).getBean();
			DCDtlList.remove(save);
			resetDCDetails();
			loadDCDtl();
			btndelete.setEnabled(false);
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
			parameterMap.put("DCID", dcHdrId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/dc"); // productlist is the name of my jasper
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
	
	private void loadclienTecCont() {
		try {
			Long enquid = ((Long) cbEnquiry.getValue());
			Long clientId = serviceEnqHeader.getSmsEnqHdrList(companyid, enquid, null, null, null, "F", null, null)
					.get(0).getClientId();
			BeanContainer<Long, ClientsContactsDM> beanclientcontact = new BeanContainer<Long, ClientsContactsDM>(
					ClientsContactsDM.class);
			beanclientcontact.setBeanIdProperty("contactName");
			beanclientcontact.addAll(serviceClntContact.getClientContactsDetails(companyid, null, clientId, null,
					"Active", "Technical Person"));
			cbwindTechPers.setContainerDataSource(beanclientcontact);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadClientContacts() {
		try {
			Long clientId = serviceEnqHeader
					.getSmsEnqHdrList(companyid, (Long) cbEnquiry.getValue(), null, null, null, "F", null, null).get(0)
					.getClientId();
			BeanContainer<Long, ClientsContactsDM> beanclientcontact = new BeanContainer<Long, ClientsContactsDM>(
					ClientsContactsDM.class);
			beanclientcontact.setBeanIdProperty("contactName");
			beanclientcontact.addAll(serviceClntContact.getClientContactsDetails(companyid, null, clientId, null,
					"Active", "Contact Person"));
			cbwindcommPerson.setContainerDataSource(beanclientcontact);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadEnquiryList() {
		BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("enquiryId");
		beansmsenqHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
		cbEnquiry.setContainerDataSource(beansmsenqHdr);
	}
}
