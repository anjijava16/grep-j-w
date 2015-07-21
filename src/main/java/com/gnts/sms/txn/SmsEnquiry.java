/**
 * File Name 	 :   SmsEnquiry.java 
 * Description 	 :   This Screen Purpose for Modify the SmsEnquiry Details.Add the SmsEnquiry details and Specification process should be directly added in DB.
 * Author 		 :   Sudhakar S 
 * Date 		 :   17-Sep-2014
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS Technologies pvt. ltd.
 * 
 * Version  Date           Modified By        Remarks
 * 0.1      17-Sep-2014    Sudhakar S     Initial Version
 */
package com.gnts.sms.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CityDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CityService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.base.service.mst.StateService;
import com.gnts.crm.domain.mst.ClientCategoryDM;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientCategoryService;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.stt.txn.EnquiryWorkflow;
import com.gnts.saarc.util.SerialNumberGenerator;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsEnquiryDtlDM;
import com.gnts.sms.domain.txn.SmsEnquirySpecDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsEnquiryDtlService;
import com.gnts.sms.service.txn.SmsEnquirySpecService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class SmsEnquiry extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private SmsEnqHdrService serviceenqhdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private SmsEnquiryDtlService serviceenqdtl = (SmsEnquiryDtlService) SpringContextHelper.getBean("SmsEnquiryDtl");
	private SmsEnquirySpecService serviceenqspec = (SmsEnquirySpecService) SpringContextHelper
			.getBean("SmsEnquirySpec");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private CityService serviceCity = (CityService) SpringContextHelper.getBean("city");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	private CountryService servicecountry = (CountryService) SpringContextHelper.getBean("country");
	private ClientCategoryService serviceClientCat = (ClientCategoryService) SpringContextHelper
			.getBean("clientCategory");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private List<SmsEnquiryDtlDM> enqdtlList = new ArrayList<SmsEnquiryDtlDM>();
	private List<SmsEnquirySpecDM> enqspecList = new ArrayList<SmsEnquirySpecDM>();
	// Initialize the logger
	private Logger logger = Logger.getLogger(SmsEnquiry.class);
	// User Input Fields for Sales Enquiry Header
	private TextField tfEnquiryNo, tfclientname;
	private TextArea taRemarks, tAclientAddrs;
	private PopupDateField dfEnquiryDate, dfDueDate;
	private ComboBox cbBranch, cbmodeofenquiry, cbClient;
	private Button btnNewClient = new Button("");
	private ComboBox cbEnquiryStatus = new GERPComboBox("Status", BASEConstants.T_SMS_ENQUIRY_HDR,
			BASEConstants.PE_STATUS);
	private BeanItemContainer<SmsEnqHdrDM> beanhdr = null;
	// User Input Fields for Sales Enquiry Detail
	private ComboBox cdProduct, cdenqdtlstatus;
	private TextField custprodcode, tfEnqQty;
	private TextArea tarequmentdesc, tacustproddesc;
	private GERPTextField tfCustomField1 = new GERPTextField("Part Number");
	private GERPTextField tfCustomField2 = new GERPTextField("Drawing Number");
	private Table tblEnqDetails = new GERPTable();
	private BeanItemContainer<SmsEnquiryDtlDM> beandtl = null;
	// commom data
	private Window mywindow = new Window("Add New Client");
	private Button saveClient = new Button("Save", this);
	private GERPTextField tfClntName = new GERPTextField("Client Name");
	private GERPTextField tfClientCode = new GERPTextField("Client Code");
	private GERPTextField tfEmail = new GERPTextField("Email");
	private GERPTextField tfPhoneNumber = new GERPTextField("Phone Number");
	private GERPComboBox cbClntCategory = new GERPComboBox("Client Category");
	private GERPComboBox cbCountry = new GERPComboBox("Country");
	private GERPComboBox cbState = new GERPComboBox("State");
	private GERPComboBox cbCity = new GERPComboBox("City");
	// User Input Fields for Sales Enquiry Specification
	private ComboBox cbspecstatus;
	private GERPComboBox tfspeccode;
	private TextArea taspecdesc;
	private Table tblspec = new GERPTable();
	private BeanItemContainer<SmsEnquirySpecDM> beanpec = null;
	// User Input Components for Sales Enquire Details
	private Button btndetaildelete = new GERPButton("Delete", "delete", this);
	private Button btndetailadd = new GERPButton("Add", "addbtn", this);
	// User Input Components for Sales Enquire Specification
	private Button btnaddspec = new GERPButton("Add", "addbtn", this);
	private Button btnspecdelete = new GERPButton("Delete", "delete", this);
	// form layout for input controls Sales Enquiry Header
	private FormLayout flcol1, flcol2, flcol3, flcol4, flcol5;
	// form layout for input controls Sales Enquiry Deatil
	private FormLayout fldtl1, fldtl2, fldtl3, fldtl4, fldtl5;
	// form layout for input controls Sales Enquiry Specification
	private FormLayout flspec1, flspec2, flspec3, flspec4, flspec5;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls Sales Enquiry Header
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// Parent layout for all the input controls Sales Enquiry Detail
	private HorizontalLayout hldtllayout = new HorizontalLayout();
	private VerticalLayout vldtl = new VerticalLayout();
	// Parent layout for all the input controls Sales Enquiry Specification
	private HorizontalLayout hlspecadd = new HorizontalLayout();
	private HorizontalLayout hlspecadd1 = new HorizontalLayout();
	private HorizontalLayout hlEnquiryWorkflow = new HorizontalLayout();
	private HorizontalLayout hlspec = new HorizontalLayout();
	private VerticalLayout vlspec = new VerticalLayout();
	// Parent layout for all the input controls Sms Comments
	private VerticalLayout vlTableForm = new VerticalLayout();
	// local variables declaration
	private Long enquiryId;
	private String username;
	private Long companyid, moduleId;
	private int recordCnt = 0;
	private SmsComments comments;
	private String status;
	private Long branchId, employeeId;
	private TabSheet dtlTab;
	
	// Constructor received the parameters from Login UI class
	public SmsEnquiry() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside SmsEnquiry() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting Sales Enquiry UI");
		// Sales Enquiry Header Components Definition
		tfClntName.setRequired(true);
		tfClientCode.setRequired(true);
		// tfEmail.setRequired(true);
		tfPhoneNumber.setRequired(true);
		cbClntCategory.setRequired(true);
		cbCountry.setRequired(true);
		cbState.setRequired(true);
		cbCity.setRequired(true);
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setImmediate(true);
		cbBranch.setNullSelectionAllowed(false);
		cbBranch.setWidth("130");
		loadBranchList();
		tfEnquiryNo = new GERPTextField("Enquiry No");
		tfEnquiryNo.setWidth("130");
		tfEnquiryNo.setReadOnly(false);
		cbClient = new GERPComboBox();
		cbClient.setItemCaptionPropertyId("clientCode");
		cbClient.setImmediate(true);
		cbClient.setNullSelectionAllowed(false);
		cbClient.setWidth("150");
		tfclientname = new GERPTextField("Client Name");
		tfclientname.setReadOnly(true);
		cbClient.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbClient.getValue() != null) {
					tfclientname.setReadOnly(false);
					try {
						tfclientname.setValue(serviceClients
								.getClientDetails(companyid, Long.valueOf(cbClient.getValue().toString()), null, null,
										null, null, null, null, "Active", "P").get(0).getClientName());
					}
					catch (Exception e) {
					}
					tfclientname.setReadOnly(true);
				}
			}
		});
		tfclientname.setImmediate(true);
		tfclientname.setWidth("150");
		loadSmsClientList();
		cbmodeofenquiry = new GERPComboBox("Mode of Enquiry");
		cbmodeofenquiry.setItemCaptionPropertyId("lookupname");
		cbmodeofenquiry.setImmediate(true);
		cbmodeofenquiry.setNullSelectionAllowed(false);
		cbmodeofenquiry.setWidth("120");
		loadmodofenquiryList();
		dfEnquiryDate = new GERPPopupDateField("Enquiry Date");
		dfEnquiryDate.setDateFormat("dd-MMM-yyyy");
		dfEnquiryDate.setInputPrompt("Select Date");
		dfEnquiryDate.setWidth("100px");
		dfDueDate = new GERPPopupDateField("Due Date");
		dfDueDate.setDateFormat("dd-MMM-yyyy");
		dfDueDate.setInputPrompt("Select Date");
		dfDueDate.setWidth("100px");
		taRemarks = new GERPTextArea("Remarks");
		taRemarks.setWidth("130");
		taRemarks.setHeight("50");
		tAclientAddrs = new GERPTextArea("Address");
		tAclientAddrs.setWidth("130");
		tAclientAddrs.setHeight("50");
		cbEnquiryStatus.setWidth("130");
		// Sales Enquiry Detail Components Definition
		cdProduct = new GERPComboBox("Product Name");
		cdProduct.setItemCaptionPropertyId("prodname");
		cdProduct.setWidth("150");
		cdProduct.setRequired(true);
		loadProduct();
		cdProduct.setImmediate(true);
		cdProduct.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cdProduct.getValue() != null) {
					tfEnqQty.setCaption("Enquiry Qty (" + ((ProductDM) cdProduct.getValue()).getUom() + ")");
					tfEnqQty.setValue("0");
					if (((ProductDM) cdProduct.getValue()).getProddesc() != null) {
						tacustproddesc.setValue(((ProductDM) cdProduct.getValue()).getProddesc());
					} else {
						tacustproddesc.setValue("");
					}
				}
			}
		});
		tfEnqQty = new GERPTextField("Enquiry Qty");
		tfEnqQty.setRequired(true);
		tfEnqQty.setWidth("150");
		custprodcode = new GERPTextField("Product Code");
		custprodcode.setWidth("130");
		tacustproddesc = new GERPTextArea("Product Description");
		tacustproddesc.setWidth("130");
		tacustproddesc.setHeight("93");
		tarequmentdesc = new GERPTextArea("Requirement Description");
		tarequmentdesc.setWidth("130");
		tarequmentdesc.setHeight("93");
		cdenqdtlstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cdenqdtlstatus.setWidth("130");
		loadEnquiryDetails(true);
		// Sales Enquiry Specification Components Definition
		tfspeccode = new GERPComboBox("Specification Code");
		tfspeccode.setReadOnly(false);
		tfspeccode.setRequired(true);
		tfspeccode.setWidth("170");
		loadspeccodeList();
		taspecdesc = new GERPTextArea("Specification Description");
		taspecdesc.setWidth("250");
		taspecdesc.setHeight("50");
		cbspecstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbspecstatus.setWidth("130");
		loadEnquirySpec(false, null);
		hlsearchlayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
		btndetailadd.setStyleName("add");
		btndetailadd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update Sales Detail
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validationForEnqDetails()) {
					saveEnqDtl();
				}
			}
		});
		btndetaildelete.setEnabled(false);
		// Click Listener for Enquire Detail Tale
		tblEnqDetails.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblEnqDetails.isSelected(event.getItemId())) {
					tblEnqDetails.setImmediate(true);
					btndetailadd.setCaption("Add");
					btndetailadd.setStyleName("savebt");
					btndetaildelete.setEnabled(false);
					enqDtlresetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btndetailadd.setCaption("Update");
					btndetailadd.setStyleName("savebt");
					btndetaildelete.setEnabled(true);
					editSmsDetail();
				}
			}
		});
		// specification
		btnaddspec.setStyleName("add");
		btnaddspec.addClickListener(new ClickListener() {
			// Click Listener for Add and Update Sales Specification
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validationForEnqSpecification()) {
					saveEnqSpec();
				}
			}
		});
		btnspecdelete.setEnabled(false);
		// ClickListener for Enquire Specification Table
		tblspec.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblspec.isSelected(event.getItemId())) {
					tblspec.setImmediate(true);
					btnaddspec.setCaption("Add");
					btnaddspec.setStyleName("savebt");
					btnspecdelete.setEnabled(false);
					enqSpecResetfields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddspec.setCaption("Update");
					btnaddspec.setStyleName("savebt");
					btnspecdelete.setEnabled(true);
					editSmsEnqspecification();
				}
			}
		});
		btndetaildelete.addClickListener(new ClickListener() {
			// Click Listener for delete Enquiry Detail
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndetaildelete == event.getButton()) {
					deleteDetail();
				}
			}
		});
		btnspecdelete.addClickListener(new ClickListener() {
			// Click Listener for delete Enquiry Specification
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnspecdelete == event.getButton()) {
					deleteEnquirySpec();
				}
			}
		});
		cbCountry.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadStateList();
				}
			}
		});
		cbState.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadCityList();
				}
			}
		});
		saveClient.setWidth("150px");
		saveClient.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				saveNewClient();
			}
		});
		try {
			btnAdd.setVisible(true);
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_ENQ_WF")) {
				btnAdd.setVisible(false);
			}
		}
		catch (Exception e) {
		}
	}
	
	private void deleteEnquirySpec() {
		SmsEnquirySpecDM save = new SmsEnquirySpecDM();
		if (tblspec.getValue() != null) {
			save = beanpec.getItem(tblspec.getValue()).getBean();
			enqspecList.remove(save);
			enqSpecResetfields();
			loadEnquirySpec(false, null);
			btnspecdelete.setEnabled(false);
		}
	}
	
	private void deleteDetail() {
		SmsEnquiryDtlDM save = new SmsEnquiryDtlDM();
		if (tblEnqDetails.getValue() != null) {
			save = beandtl.getItem(tblEnqDetails.getValue()).getBean();
			enqdtlList.remove(save);
			enqDtlresetFields();
			loadEnquiryDetails(false);
			btndetaildelete.setEnabled(false);
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlsearchlayout.removeAllComponents();
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol1.addComponent(cbBranch);
		flcol2.addComponent(tfEnquiryNo);
		flcol3.addComponent(cbEnquiryStatus);
		hlsearchlayout.addComponent(flcol1);
		hlsearchlayout.addComponent(flcol2);
		hlsearchlayout.addComponent(flcol3);
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setSizeUndefined();
	}
	
	private void assembleinputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol5 = new FormLayout();
		flcol1.addComponent(tfEnquiryNo);
		tfEnquiryNo.setReadOnly(true);
		flcol1.addComponent(cbBranch);
		final HorizontalLayout test = new HorizontalLayout();
		btnNewClient.setIcon(new ThemeResource("img/add_blue.png"));
		btnNewClient.setStyleName(Runo.BUTTON_LINK);
		test.addComponent(cbClient);
		test.addComponent(btnNewClient);
		test.setComponentAlignment(btnNewClient, Alignment.BOTTOM_LEFT);
		test.setCaption("Client Code");
		flcol2.addComponent(test);
		flcol2.addComponent(tfclientname);
		flcol3.addComponent(cbmodeofenquiry);
		flcol3.addComponent(dfEnquiryDate);
		flcol4.addComponent(dfDueDate);
		flcol5.addComponent(taRemarks);
		flcol4.addComponent(cbEnquiryStatus);
		hllayout.setMargin(true);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol4);
		hllayout.addComponent(flcol4);
		hllayout.addComponent(flcol5);
		hllayout.setMargin(true);
		hllayout.setSpacing(true);
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hllayout));
		// Adding Sales Enquiry Detail components
		// Add components for User Input Layout
		fldtl1 = new FormLayout();
		fldtl2 = new FormLayout();
		fldtl3 = new FormLayout();
		fldtl4 = new FormLayout();
		fldtl5 = new FormLayout();
		fldtl1.addComponent(cdProduct);
		fldtl1.addComponent(tfEnqQty);
		fldtl1.addComponent(tfCustomField1);
		fldtl1.addComponent(tfCustomField2);
		// fldtl3.addComponent(custprodcode);
		fldtl2.addComponent(tacustproddesc);
		fldtl3.addComponent(tarequmentdesc);
		fldtl4.addComponent(cdenqdtlstatus);
		fldtl5.addComponent(btndetailadd);
		fldtl5.addComponent(btndetaildelete);
		hldtllayout.setMargin(true);
		hldtllayout.setSpacing(true);
		hldtllayout.addComponent(fldtl1);
		hldtllayout.addComponent(fldtl2);
		hldtllayout.addComponent(fldtl3);
		hldtllayout.addComponent(fldtl4);
		hldtllayout.addComponent(fldtl5);
		vldtl.addComponent(GERPPanelGenerator.createPanel(hldtllayout));
		vldtl.addComponent(tblEnqDetails);
		tblEnqDetails.setWidth("1188px");
		tblEnqDetails.setPageLength(6);
		// tbldtl.setStyleName(Runo.TABLE_SMALL);
		loadEnquiryDetails(true);
		vldtl.setWidth("100%");
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		// Adding Sales Enquiry Specification components
		// Add components for User Input Layout
		flspec1 = new FormLayout();
		flspec2 = new FormLayout();
		flspec3 = new FormLayout();
		flspec4 = new FormLayout();
		flspec5 = new FormLayout();
		flspec1.addComponent(tfspeccode);
		flspec2.addComponent(taspecdesc);
		flspec3.addComponent(cbspecstatus);
		flspec4.addComponent(btnaddspec);
		flspec5.addComponent(btnspecdelete);
		hlspec.setMargin(true);
		hlspec.addComponent(flspec1);
		hlspec.addComponent(flspec2);
		hlspec.addComponent(flspec3);
		hlspec.addComponent(flspec4);
		hlspec.addComponent(flspec5);
		hlspec.setMargin(true);
		hlspec.setSpacing(true);
		vlspec.addComponent(GERPPanelGenerator.createPanel(hlspec));
		vlspec.addComponent(tblspec);
		tblspec.setPageLength(10);
		tblspec.setWidth("1188px");
		// tblspec.setStyleName(Runo.TABLE_BORDERLESS);
		loadEnquirySpec(false, null);
		// vlspec.setWidth("200%");
		hlspecadd = new HorizontalLayout();
		hlspecadd.addComponent(vldtl);
		hlspecadd.addComponent(GERPPanelGenerator.createPanel(vldtl));
		hlspecadd1.addComponent(vlspec);
		hlspecadd1.setSpacing(true);
		dtlTab = new TabSheet();
		dtlTab.addTab(hlspecadd, "Sales Enquiry Detail");
		dtlTab.addTab(hlspecadd1, "Sales Enquiry Specification");
		dtlTab.addTab(hlEnquiryWorkflow, "Enquiry Workflow");
		dtlTab.addTab(vlTableForm, "Comments");
		// new EnquiryWorkflow(hlEnquiryWorkflow, enquiryId, username);
		// dtlTab.setWidth("100%");
		vlSrchRsltContainer.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		btnNewClient.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				showSubWindow();
			}
		});
	}
	
	void showSubWindow() {
		try {
			tfClntName.setRequired(true);
			tfClntName.setValue("");
			tAclientAddrs.setRequired(true);
			tfClientCode.setValue("");
			tfEmail.setValue("");
			tfPhoneNumber.setValue("");
			cbCountry.setValue(null);
			cbState.setValue(null);
			cbCity.setValue(null);
			cbClntCategory.setValue(null);
			tfClntName.setComponentError(null);
			btnNewClient.setComponentError(null);
			cbCountry.setItemCaptionPropertyId("countryName");
			cbState.setItemCaptionPropertyId("stateName");
			cbCity.setItemCaptionPropertyId("cityname");
			cbClntCategory.setItemCaptionPropertyId("clientCatName");
			loadCountryList();
			loadClientCategoryList();
			FormLayout subwindow = new FormLayout();
			mywindow.setHeight("330px");
			mywindow.setWidth("330px");
			mywindow.center();
			mywindow.setModal(true);
			tfClntName.focus();
			subwindow.setMargin(true);
			subwindow.addComponent(tfClntName);
			subwindow.addComponent(cbClntCategory);
			subwindow.addComponent(tfClientCode);
			subwindow.addComponent(tfEmail);
			subwindow.addComponent(tfPhoneNumber);
			subwindow.addComponent(tAclientAddrs);
			subwindow.addComponent(cbCountry);
			subwindow.addComponent(cbState);
			subwindow.addComponent(cbCity);
			subwindow.addComponent(saveClient);
			subwindow.setComponentAlignment(saveClient, Alignment.BOTTOM_RIGHT);
			saveClient.setStyleName("default");
			mywindow.setContent(GERPPanelGenerator.createPanel(subwindow));
			UI.getCurrent().addWindow(mywindow);
		}
		catch (Exception e) {
		}
	}
	
	void saveNewClient() {
		try {
			tfClntName.setComponentError(null);
			if (tfClntName.getValue() != null && tfClntName.getValue().trim().length() > 0
					&& tfClientCode.getValue() != null && tfClientCode.getValue().trim().length() > 0
					&& cbClntCategory.getValue() != null && tfPhoneNumber.getValue() != null
					&& tfPhoneNumber.getValue().trim().length() > 0 && cbCountry.getValue() != null
					&& cbState.getValue() != null && cbCity.getValue() != null && tAclientAddrs.getValue() != ""
					&& tAclientAddrs.getValue().trim().length() > 0) {
				ClientDM obj = new ClientDM();
				obj.setClientName(tfClntName.getValue());
				obj.setClientCode(tfClientCode.getValue());
				obj.setClientCatId((Long) cbClntCategory.getValue());
				if (tfEmail.getValue() != "") {
					obj.setEmailId(tfEmail.getValue());
				}
				obj.setPhoneNo(tfPhoneNumber.getValue());
				obj.setCountryId((Long) cbCountry.getValue());
				obj.setStateId(Long.valueOf(cbState.getValue().toString()));
				obj.setCityId(Long.valueOf(cbCity.getValue().toString()));
				obj.setCompanyId(companyid);
				obj.setClientSttus("Active");
				obj.setClientAddress(tAclientAddrs.getValue());
				obj.setLastUpdatedBy(username);
				obj.setLastUpdatedDt(new Date());
				serviceClients.saveOrUpdateClientsDetails(obj);
				loadSmsClientList();
				cbClient.setValue(obj.getClientId());
				mywindow.close();
			} else {
				Notification.show("Enter All Details...");
			}
		}
		catch (Exception e) {
			Notification.show("Enter All Details...");
		}
	}
	
	// Load Sales Enquiry Header
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<SmsEnqHdrDM> hdrlist = new ArrayList<SmsEnqHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbBranch.getValue() + "," + tfEnquiryNo.getValue() + ", "
				+ (String) cbEnquiryStatus.getValue());
		hdrlist = serviceenqhdr.getSmsEnqHdrList(companyid, null, (Long) cbBranch.getValue(), null,
				(String) cbEnquiryStatus.getValue(), username, (String) tfEnquiryNo.getValue(), null);
		recordCnt = hdrlist.size();
		beanhdr = new BeanItemContainer<SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beanhdr.addAll(hdrlist);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the SMSENQUIRY. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanhdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "enquiryId", "enquiryNo", "clientName", "enquiryStatus",
				"enquiryid","custproddesc","lastUpdateddt", "lastUpdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Enquiry No", "Client Name", "Status",
				"Enquiry ID","Description","Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("enquiryId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
	}
	
	// Load Sales Enquiry Dateil
	private void loadEnquiryDetails(Boolean fromdb) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblEnqDetails.removeAllItems();
		logger.info("Company ID : "
				+ companyid
				+ " | User Name : "
				+ username
				+ " > "
				+ "Search Parameters are "
				+ (String.valueOf(cdProduct.getValue()) + ", " + (String) tfEnqQty.getValue() + "," + (String) cdenqdtlstatus
						.getValue()));
		Long prodid = null;
		if (cdProduct.getValue() != null) {
			prodid = ((ProductDM) cdProduct.getValue()).getProdid();
		}
		if (fromdb) {
			enqdtlList = serviceenqdtl.getsmsenquirydtllist(null, enquiryId, prodid, null,
					(String) cdenqdtlstatus.getValue(), "F");
		}
		System.out.println("list size---" + enqdtlList.size());
		recordCnt = enqdtlList.size();
		beandtl = new BeanItemContainer<SmsEnquiryDtlDM>(SmsEnquiryDtlDM.class);
		beandtl.addAll(enqdtlList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the SMSENQUIRYDTL. result set");
		tblEnqDetails.setContainerDataSource(beandtl);
		tblEnqDetails.setVisibleColumns(new Object[] { "prodname", "custproddesc", "enquiryqty", "uom", "customField1",
				"customField2", "enquitydtlstatus", "lastupdateddt", "lastupdatedby" });
		tblEnqDetails.setColumnHeaders(new String[] { "Product Name", "Description", "Enquiry Qty", "UOM", "Part No.",
				"Drg. No", "Status", "Last Updated Date", "Last Updated By" });
		tblEnqDetails.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Load Sales Enquiry Specification
	private void loadEnquirySpec(Boolean fromdb, Long enquiryId) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblspec.removeAllItems();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfspeccode.getValue() + ", " + (String) cbspecstatus.getValue());
		Long enquirydtlid = null;
		if (fromdb) {
			enqspecList = serviceenqspec.getsmsenquiryspecList(null, enquiryId, enquirydtlid,
					(String) cbspecstatus.getValue(), "F");
		}
		recordCnt = enqspecList.size();
		System.out.println("loadspec---->" + enqspecList.size());
		beanpec = new BeanItemContainer<SmsEnquirySpecDM>(SmsEnquirySpecDM.class);
		beanpec.addAll(enqspecList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the SMSENQUIRY. result set");
		tblspec.setContainerDataSource(beanpec);
		tblspec.setVisibleColumns(new Object[] { "speccode", "specdesc", "enqryspecstatus", "lastupdateddt",
				"lastupdatedby" });
		tblspec.setColumnHeaders(new String[] { "Specification Code", "Specification Description",
				"Enquiry Specification Status", "Last Updated Date", "Last Updated By" });
		tblspec.setColumnAlignment("enquiryspecid", Align.RIGHT);
		tblspec.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Load Branch List
	private void loadBranchList() {
		try {
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
			cbBranch.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Client List
	private void loadSmsClientList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading client Search...");
		BeanContainer<Long, ClientDM> beanclientDM = new BeanContainer<Long, ClientDM>(ClientDM.class);
		beanclientDM.setBeanIdProperty("clientId");
		beanclientDM.addAll(serviceClients.getClientDetails(companyid, null, null, null, null, null, null, null,
				"Active", "P"));
		cbClient.setContainerDataSource(beanclientDM);
	}
	
	// Load Mod_of_Enquiry List
	private void loadmodofenquiryList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_ENQMOD"));
			cbmodeofenquiry.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Enquiry Specification List
	private void loadspeccodeList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"SM_SALSPEC"));
			tfspeccode.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Product List
	private void loadProduct() {
		try {
			BeanItemContainer<ProductDM> beanProduct = new BeanItemContainer<ProductDM>(ProductDM.class);
			beanProduct.addAll(serviceProduct.getProductList(companyid, null, null, null, "Active", null, null, "P"));
			cdProduct.setContainerDataSource(beanProduct);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editSmsEnquiry() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			SmsEnqHdrDM enqhdrList1 = beanhdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			enquiryId = enqhdrList1.getEnquiryId();
			cbBranch.setValue(enqhdrList1.getBranchId());
			tfEnquiryNo.setReadOnly(false);
			tfEnquiryNo.setValue(enqhdrList1.getEnquiryNo());
			tfEnquiryNo.setReadOnly(true);
			cbClient.setValue(enqhdrList1.getClientId());
			dfEnquiryDate.setValue(enqhdrList1.getEnquiryDate());
			dfDueDate.setValue(enqhdrList1.getDueDate());
			cbEnquiryStatus.setValue(enqhdrList1.getEnquiryStatus());
			System.out.println("enqhdrList1.getModeofEnq()-->" + enqhdrList1.getModeofEnq());
			cbmodeofenquiry.setValue(enqhdrList1.getModeofEnq());
			if (enqhdrList1.getRemarks() != null) {
				taRemarks.setValue(enqhdrList1.getRemarks().toString());
			}
			enqdtlList = serviceenqdtl.getsmsenquirydtllist(null, enquiryId, null, null,
					(String) cbEnquiryStatus.getValue(), "F");
			enqspecList = serviceenqspec.getsmsenquiryspecList(null, enquiryId, null, (String) cbspecstatus.getValue(),
					"F");
		}
		loadEnquiryDetails(true);
		loadEnquirySpec(false, null);
		new EnquiryWorkflow(hlEnquiryWorkflow, enquiryId, username);
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, enquiryId, null, null,
				null, status);
		comments.loadsrch(true, null, null, null, null, null, null, null, enquiryId, null, null, null, null);
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Detail
	private void editSmsDetail() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hldtllayout.setVisible(true);
		if (tblEnqDetails.getValue() != null) {
			SmsEnquiryDtlDM enqdtllist1 = beandtl.getItem(tblEnqDetails.getValue()).getBean();
			Long uom = enqdtllist1.getProductid();
			Collection<?> uomid = cdProduct.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cdProduct.getItem(itemId);
				// Get the actual bean and use the data
				ProductDM st = (ProductDM) item.getBean();
				if (uom != null && uom.equals(st.getProdid())) {
					cdProduct.setValue(itemId);
				}
			}
			if (enqdtllist1.getEnquiryqty() != null) {
				tfEnqQty.setValue(enqdtllist1.getEnquiryqty().toString());
			}
			cdenqdtlstatus.setValue(cdenqdtlstatus.getItemIds().iterator().next());
			if (enqdtllist1.getCustprodcode() != null) {
				custprodcode.setValue(enqdtllist1.getCustprodcode().toString());
			}
			if (enqdtllist1.getRequirementdesc() != null) {
				tarequmentdesc.setValue(enqdtllist1.getRequirementdesc().toString());
			}
			if (enqdtllist1.getCustproddesc() != null) {
				tacustproddesc.setValue(enqdtllist1.getCustproddesc().toString());
			}
			if (enqdtllist1.getCustomField1() != null) {
				tfCustomField1.setValue(enqdtllist1.getCustomField1());
			}
			if (enqdtllist1.getCustomField2() != null) {
				tfCustomField2.setValue(enqdtllist1.getCustomField2());
			}
		}
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Specification
	private void editSmsEnqspecification() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hldtllayout.setVisible(true);
		if (tblspec.getValue() != null) {
			SmsEnquirySpecDM enqspecList = beanpec.getItem(tblspec.getValue()).getBean();
			cbspecstatus.setValue(enqspecList.getEnqryspecstatus());
			tfspeccode.setValue(enqspecList.getSpeccode());
			if (enqspecList.getSpecdesc() != null) {
				taspecdesc.setValue(enqspecList.getSpecdesc().toString());
			}
		}
	}
	
	// Reset Sales Enquiry Detail List
	private void enqDtlresetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cdProduct.setValue(null);
		custprodcode.setValue("0");
		tarequmentdesc.setValue("");
		tacustproddesc.setValue("");
		tfEnqQty.setValue("0");
		tfCustomField1.setValue("");
		tfCustomField2.setValue("");
		cdenqdtlstatus.setValue(cdenqdtlstatus.getItemIds().iterator().next());
		cdProduct.setComponentError(null);
		tfEnqQty.setComponentError(null);
		tfEnqQty.setCaption("Enquiry Qty");
		btndetailadd.setCaption("Add");
	}
	
	// Reset Sales Enquiry Specification List
	private void enqSpecResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbspecstatus.setValue(cbspecstatus.getItemIds().iterator().next());
		tfspeccode.setValue("");
		tfspeccode.setComponentError(null);
		taspecdesc.setValue("");
		taspecdesc.setComponentError(null);
		btnaddspec.setCaption("Add");
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		// try {
		validationForEnqDetails();
		SmsEnqHdrDM smsEnqHdrDM = new SmsEnqHdrDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			smsEnqHdrDM = beanhdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			smsEnqHdrDM.setEnquiryNo(tfEnquiryNo.getValue());
		} else {
			smsEnqHdrDM.setEnquiryNo(SerialNumberGenerator.generateEnquiryNumber(companyid, branchId, moduleId,
					"SM_ENQRYNO", (Long) cbClient.getValue()));
		}
		smsEnqHdrDM.setCompanyId(companyid);
		smsEnqHdrDM.setEnquiryDate(dfEnquiryDate.getValue());
		smsEnqHdrDM.setDueDate(dfDueDate.getValue());
		smsEnqHdrDM.setRemarks(taRemarks.getValue().toString());
		if (cbmodeofenquiry.getValue() != null) {
			smsEnqHdrDM.setModeofEnq(cbmodeofenquiry.getValue().toString());
		}
		smsEnqHdrDM.setBranchId((Long) cbBranch.getValue());
		smsEnqHdrDM.setClientId((Long) cbClient.getValue());
		smsEnqHdrDM.setEnquiryStatus(((String) cbEnquiryStatus.getValue()));
		smsEnqHdrDM.setPreparedBy(employeeId);
		smsEnqHdrDM.setLastUpdateddt(DateUtils.getcurrentdate());
		smsEnqHdrDM.setLastUpdatedby(username);
		serviceenqhdr.saveorUpdateSmsEnqHdrDetails(smsEnqHdrDM);
		@SuppressWarnings("unchecked")
		Collection<SmsEnquiryDtlDM> itemIds1 = (Collection<SmsEnquiryDtlDM>) tblEnqDetails.getVisibleItemIds();
		for (SmsEnquiryDtlDM save : (Collection<SmsEnquiryDtlDM>) itemIds1) {
			save.setEnquiryid(Long.valueOf(smsEnqHdrDM.getEnquiryId().toString()));
			serviceenqdtl.saveOrUpdatesmsenquirydtlDetails(save);
			@SuppressWarnings("unchecked")
			Collection<SmsEnquirySpecDM> itemIds = (Collection<SmsEnquirySpecDM>) tblspec.getVisibleItemIds();
			for (SmsEnquirySpecDM save1 : (Collection<SmsEnquirySpecDM>) itemIds) {
				save1.setEnquirydtlid((save.getEnquirydtlid()));
				save1.setEnquiryid(smsEnqHdrDM.getEnquiryId());
				System.out.println("EEEE=>" + save1.getEnquiryspecid());
				serviceenqspec.saveOrUpdateSmsEnqSpecDetails(save1);
			}
		}
		if (tblMstScrSrchRslt.getValue() == null) {
			System.out.println("inside1" + moduleId);
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_ENQRYNO").get(0);
			System.out.println("inside3");
			if (slnoObj.getAutoGenYN().equals("Y")) {
				System.out.println("inside2");
				serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_ENQRYNO");
			}
		}
		tfEnquiryNo.setReadOnly(false);
		tfEnquiryNo.setValue(smsEnqHdrDM.getEnquiryNo());
		tfEnquiryNo.setReadOnly(true);
		comments.saveSalesEnqId(smsEnqHdrDM.getEnquiryId(), null);
		enqDtlresetFields();
		enqSpecResetfields();
		loadSrchRslt();
	}
	
	// This function is used for save the Sales Enquiry details for temporary
	private void saveEnqDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			SmsEnquiryDtlDM smsEnquiryDtlDM = new SmsEnquiryDtlDM();
			if (tblEnqDetails.getValue() != null) {
				smsEnquiryDtlDM = beandtl.getItem(tblEnqDetails.getValue()).getBean();
				enqdtlList.remove(smsEnquiryDtlDM);
			}
			smsEnquiryDtlDM.setProductid(((ProductDM) cdProduct.getValue()).getProdid());
			smsEnquiryDtlDM.setProdname(((ProductDM) cdProduct.getValue()).getProdname());
			smsEnquiryDtlDM.setCustprodcode(((ProductDM) cdProduct.getValue()).getProductcode());
			smsEnquiryDtlDM.setUom(((ProductDM) cdProduct.getValue()).getUom());
			if (tfEnqQty.getValue() != null) {
				smsEnquiryDtlDM.setEnquiryqty(Long.valueOf(tfEnqQty.getValue()));
			}
			smsEnquiryDtlDM.setRequirementdesc(tarequmentdesc.getValue().toString());
			smsEnquiryDtlDM.setCustproddesc(tacustproddesc.getValue().toString());
			if (cdenqdtlstatus.getValue() != null) {
				smsEnquiryDtlDM.setEnquitydtlstatus((cdenqdtlstatus.getValue().toString()));
			}
			smsEnquiryDtlDM.setCustomField1(tfCustomField1.getValue());
			smsEnquiryDtlDM.setCustomField2(tfCustomField2.getValue());
			smsEnquiryDtlDM.setLastupdateddt(DateUtils.getcurrentdate());
			smsEnquiryDtlDM.setLastupdatedby(username);
			enqdtlList.add(smsEnquiryDtlDM);
			loadEnquiryDetails(false);
			enqDtlresetFields();
			btndetailadd.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// This function is used for save the Enquiry Specification's details for temporary
	private void saveEnqSpec() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		validationForEnqSpecification();
		SmsEnquirySpecDM smsEnquirySpecDM = new SmsEnquirySpecDM();
		if (tblspec.getValue() != null) {
			smsEnquirySpecDM = beanpec.getItem(tblspec.getValue()).getBean();
		}
		System.out.println("EID=>" + enquiryId);
		smsEnquirySpecDM.setSpeccode(tfspeccode.getValue().toString());
		smsEnquirySpecDM.setSpecdesc(taspecdesc.getValue().toString());
		smsEnquirySpecDM.setEnqryspecstatus(((String) cbspecstatus.getValue()));
		smsEnquirySpecDM.setLastupdateddt(DateUtils.getcurrentdate());
		smsEnquirySpecDM.setLastupdatedby(username);
		enqspecList.add(smsEnquirySpecDM);
		enqSpecResetfields();
		loadEnquirySpec(false, null);
		btnaddspec.setCaption("Add");
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbBranch.setValue(branchId);
		cbEnquiryStatus.setValue(null);
		tfEnquiryNo.setValue("");
		tfEnquiryNo.setReadOnly(false);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// cbclient.setRequired(true);
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		tfEnquiryNo.setReadOnly(true);
		System.out.println("branchId--->" + branchId);
		cbBranch.setValue(branchId);
		tfEnqQty.setValue("0");
		tfspeccode.setValue("0");
		custprodcode.setValue("0");
		hllayout.removeAllComponents();
		hldtllayout.removeAllComponents();
		hlspec.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		btndetailadd.setCaption("Add");
		btndetailadd.setVisible(true);
		btnaddspec.setCaption("Add");
		btnaddspec.setVisible(true);
		tblEnqDetails.setVisible(true);
		tfspeccode.setRequired(true);
		cbBranch.setRequired(true);
		enqdtlList = new ArrayList<SmsEnquiryDtlDM>();
		loadEnquiryDetails(false);
		loadEnquirySpec(false, null);
		enqDtlresetFields();
		enqSpecResetfields();
		resetFields();
		cbBranch.setValue(branchId);
		tfEnquiryNo.setReadOnly(true);
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, null, moduleId, "SM_ENQRYNO");
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfEnquiryNo.setReadOnly(true);
			} else {
				tfEnquiryNo.setReadOnly(false);
			}
			tblspec.setVisible(true);
			tblEnqDetails.setVisible(true);
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		cbBranch.setValue(branchId);
		hllayout.removeAllComponents();
		hldtllayout.removeAllComponents();
		hlspec.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		tblEnqDetails.setVisible(true);
		tblspec.setVisible(true);
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_ENQRYNO");
		tfEnquiryNo.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfEnquiryNo.setReadOnly(true);
			}
		}
		if (tfEnquiryNo.getValue() == null || tfEnquiryNo.getValue().trim().length() == 0) {
			tfEnquiryNo.setReadOnly(false);
		}
		assembleinputLayout();
		enqDtlresetFields();
		btndetailadd.setCaption("Add");
		btndetailadd.setVisible(true);
		btnaddspec.setCaption("Add");
		btnaddspec.setVisible(true);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		resetFields();
		editSmsEnquiry();
		editSmsDetail();
		editSmsEnqspecification();
		cbBranch.setRequired(true);
		// To Select Enquire workflow tab
		try {
			if ((Boolean) UI.getCurrent().getSession().getAttribute("IS_ENQ_WF")) {
				dtlTab.setSelectedTab(hlEnquiryWorkflow);
				hlUserIPContainer.setEnabled(false);
				hlspecadd.setEnabled(false);
				hlspecadd1.setEnabled(false);
			}
		}
		catch (Exception e) {
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbBranch.setComponentError(null);
		cbClient.setComponentError(null);
		taRemarks.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.BRANCH_NAME));
			errorFlag = true;
		}
		if (cbClient.getValue() == null) {
			cbClient.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
			errorFlag = true;
		}
		if ((dfEnquiryDate.getValue() != null) || (dfDueDate.getValue() != null)) {
			if (dfEnquiryDate.getValue().after(dfDueDate.getValue())) {
				dfDueDate.setComponentError(new UserError(GERPErrorCodes.SMS_DATE_OUTOFRANGE));
				logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Throwing ValidationException. User data is > " + dfEnquiryDate.getValue());
				errorFlag = true;
			}
		}
		if ((tfEnquiryNo.getValue() == null) || tfEnquiryNo.getValue().trim().length() == 0) {
			List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_ENQRYNO");
			for (SlnoGenDM slnoObj : slnoList) {
				if (slnoObj.getAutoGenYN().equals("N")) {
					tfEnquiryNo.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_CODE));
					errorFlag = true;
				}
			}
		} else {
			tfEnquiryNo.setComponentError(null);
			errorFlag = false;
		}
		// tfenqty.setComponentError(null);
		cdProduct.setComponentError(null);
		if (tblEnqDetails.size() == 0) {
			cdProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + cbBranch.getValue() + "," + cbClient.getValue()
				+ "," + "," + cbmodeofenquiry.getValue() + "," + dfEnquiryDate.getValue() + "," + dfDueDate.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Validation for Sales Enquiry Detail
	private boolean validationForEnqDetails() {
		boolean isValid = true;
		if (((tfEnqQty.getValue() == null) || tfEnqQty.getValue().trim().length() == 0)) {
			tfEnqQty.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRY_QTY));
			isValid = false;
		} else {
			tfEnqQty.setComponentError(null);
			isValid = true;
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfEnqQty.getValue());
			if (achievedQty < 0) {
				tfEnqQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfEnqQty.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATION));
			isValid = false;
		}
		if ((tfEnquiryNo.getValue() == null) || tfEnquiryNo.getValue().trim().length() == 0) {
			List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MM_ENQRYNO");
			for (SlnoGenDM slnoObj : slnoList) {
				if (slnoObj.getAutoGenYN().equals("N")) {
					tfEnquiryNo.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_CODE));
					isValid = false;
				}
			}
		}
		if (cdProduct.getValue() == null) {
			cdProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			cdProduct.setComponentError(null);
		}
		return isValid;
	}
	
	// Validation for Sales Enquiry Specification
	private boolean validationForEnqSpecification() {
		cbspecstatus.setComponentError(null);
		tfspeccode.setComponentError(null);
		taspecdesc.setComponentError(null);
		Boolean errorFlag = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((tfspeccode.getValue() == "") || ((String) tfspeccode.getValue()).trim().length() == 0) {
			tfspeccode.setComponentError(new UserError(GERPErrorCodes.NULL_SPEC_CODE));
			errorFlag = false;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + tfspeccode.getValue() + "," + taspecdesc.getValue()
				+ "," + cbspecstatus.getValue());
		return errorFlag;
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for enquiryId " + enquiryId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(enquiryId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hllayout1.removeAllComponents();
		tblMstScrSrchRslt.setValue(null);
		cbmodeofenquiry.setRequired(false);
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
		cbBranch.setRequired(false);
		tfEnquiryNo.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfEnquiryNo.setReadOnly(false);
		tfEnquiryNo.setValue("");
		tfclientname.setReadOnly(false);
		tfclientname.setValue("");
		tfclientname.setReadOnly(true);
		tfEnquiryNo.setComponentError(null);
		cbBranch.setValue(null);
		cbBranch.setComponentError(null);
		cbClient.setComponentError(null);
		cbClient.setValue(null);
		dfEnquiryDate.setValue(null);
		enqdtlList = new ArrayList<SmsEnquiryDtlDM>();
		tblEnqDetails.removeAllItems();
		enqspecList = new ArrayList<SmsEnquirySpecDM>();
		dfDueDate.setValue(null);
		cbmodeofenquiry.setValue(null);
		taRemarks.setValue("");
		taRemarks.setComponentError(null);
		cbEnquiryStatus.setValue(null);
		dfEnquiryDate.setValue(new Date());
		dfDueDate.setValue(addDays(new Date(), 7));
	}
	
	private Date addDays(Date d, int days) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = sdf.format(d);
		Date parsedDate = null;
		try {
			parsedDate = sdf.parse(strDate);
		}
		catch (ParseException e) {
			logger.warn("calculate days" + e);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, days);
		return now.getTime();
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
			tfEnquiryNo.setReadOnly(false);
		}
	}
	
	private void loadCountryList() {
		try {
			BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
			beanCountry.setBeanIdProperty("countryID");
			beanCountry.addAll(servicecountry.getCountryList(null, null, null, null, "Active", "F"));
			cbCountry.setContainerDataSource(beanCountry);
		}
		catch (Exception e) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	private void loadStateList() {
		try {
			BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
			beanState.setBeanIdProperty("stateId");
			beanState.addAll(serviceState.getStateList(null, "Active", (Long) cbCountry.getValue(), null, "F"));
			cbState.setContainerDataSource(beanState);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	private void loadCityList() {
		try {
			BeanContainer<Long, CityDM> beanCity = new BeanContainer<Long, CityDM>(CityDM.class);
			beanCity.setBeanIdProperty("cityid");
			beanCity.addAll(serviceCity.getCityList(null, null, Long.valueOf(cbState.getValue().toString()), "Active",
					companyid, "F"));
			cbCity.setContainerDataSource(beanCity);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " Country List is Null");
		}
	}
	
	private void loadClientCategoryList() {
		try {
			BeanContainer<Long, ClientCategoryDM> beanCat = new BeanContainer<Long, ClientCategoryDM>(
					ClientCategoryDM.class);
			beanCat.setBeanIdProperty("clientCategoryId");
			beanCat.addAll(serviceClientCat.getCrmClientCategoryList(companyid, null, "Active", "F"));
			cbClntCategory.setContainerDataSource(beanCat);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load client category List" + e);
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
			parameterMap.put("ENQID", enquiryId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/enquiryRpt"); // productlist is the name of my jasper
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
}