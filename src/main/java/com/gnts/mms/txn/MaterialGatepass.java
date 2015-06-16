/**
 * File Name	:	MaterialGatepass.java
 * Description	:	entity class for m_crm_clients_contacts
 * Author		:	MOHAMED 
 * Date			:	Mar 07, 2014
 * Modification 
 * Modified By  :   
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of  GNTS Technologies pvt. ltd.
 */
package com.gnts.mms.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.base.service.mst.VendorService;
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
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.DcHdrDM;
import com.gnts.mms.domain.txn.GatepassDtlDM;
import com.gnts.mms.domain.txn.GatepassHdrDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.DcHdrService;
import com.gnts.mms.service.txn.GatepassDtlService;
import com.gnts.mms.service.txn.GatepassHdrService;
import com.vaadin.data.Item;
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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class MaterialGatepass extends BaseTransUI {
	/**
	 * 
	 */
	private GatepassHdrService servicegatepass = (GatepassHdrService) SpringContextHelper.getBean("gatepasshdr");
	private GatepassDtlService servicegatepassdtl = (GatepassDtlService) SpringContextHelper.getBean("gatepassdtl");
	private VendorService servicevendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private DcHdrService servicedc = (DcHdrService) SpringContextHelper.getBean("DCHdr");
	private DcHdrService serviceDCHdr = (DcHdrService) SpringContextHelper.getBean("DCHdr");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private CompanyLookupService servicelookup = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private MaterialService servicematerial = (MaterialService) SpringContextHelper.getBean("material");
	private ProductService ServiceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private BeanItemContainer<GatepassHdrDM> beanGatePassHdr;
	private BeanItemContainer<GatepassDtlDM> beanGatePassDtl;
	private BeanItemContainer<VendorDM> beanvendor;
	@SuppressWarnings("unused")
	private BeanContainer<Long,VendorDM> beanvendordm;
	private BeanContainer<Long, DcHdrDM> beanDC;
	private BeanContainer<String, DcHdrDM> beanHdrDC;
	private BeanContainer<String, CompanyLookupDM> beanlookup;
	private BeanItemContainer<MaterialDM> beanmaterial;
	private BeanItemContainer<ProductDM> beanproduct;
	private static final long serialVersionUID = 1L;
	private TextField tfvendorname, tfpersonname, tfGatePassNo, tfGatePassQty, tfReturnQty;
	private ComboBox cbgatepasstype, cbvendor, cbgatestatus, cbmodetransport, cbVendorDCNo, cbgoods, cbgoodsuom,
			cbgoodstype, cbmaterial, cbproduct, cbdtlstatus, cbDC;
	private PopupDateField dtlead, dtreturndate, gatepassdt;
	private TextArea taremarks, taHdrremarks, tavendoraddres, tagoodsdesc;
	@SuppressWarnings("unused")
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4, fldtl1, fldtl2, fldtl3, fldtl4;
	private HorizontalLayout hlsearch, hlInput, hluserinput;
	private HorizontalLayout hluserinputlayout = new HorizontalLayout();
	private VerticalLayout vlinput, vlinputlayout;
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	private Table tbldtl = new Table();
	private Long gatePassId;
	private Button btnAddDtl;
	List<GatepassDtlDM> gatepassdtllist = null;
	String userName;
	Long companyId, moduleId, branchID, vendorId;
	private MmsComments comments;
	VerticalLayout vlTableForm = new VerticalLayout();
	private int recordCnt = 0;
	public Button btndelete = new GERPButton("Delete", "delete", this);
	private Logger logger = Logger.getLogger(MaterialGatepass.class);
	private String status;
	
	public MaterialGatepass() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside MaterialGatepass() constructor");
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		// Loading the UI
		buildview();
	}
	
	@SuppressWarnings("serial")
	public void buildview() {
		btndelete.setEnabled(false);
		tfpersonname = new GERPTextField("Person");
		tfvendorname = new GERPTextField("Vendor");
		tfGatePassQty = new TextField();
		tfGatePassQty.setValue("0");
		tfGatePassQty.setWidth("90");
		tfReturnQty = new GERPTextField("Return Quantity.");
		cbgoods = new GERPComboBox("Goods");
		cbgoods.setItemCaptionPropertyId("gatepassDt");
		loadgatepasslist();
		cbgoodstype = new GERPComboBox("Goods Type");
		cbgoodstype.addItem("Material");
		cbgoodstype.addItem("Product");
		cbgoodstype.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbgoodstype.getValue() != null) {
					if (cbgoodstype.getValue().equals("Material")) {
						cbproduct.setEnabled(false);
						cbmaterial.setEnabled(true);
						cbmaterial.setValue(cbmaterial.getValue());
					} else if (cbgoodstype.getValue().equals("Product")) {
						cbmaterial.setEnabled(false);
						cbproduct.setEnabled(true);
						cbproduct.setValue(cbproduct.getValue());
					}
				}
			}
		});
		cbgoodsuom = new ComboBox();
		cbgoodsuom.setItemCaptionPropertyId("lookupname");
		cbgoodsuom.setWidth("77");
		cbgoodsuom.setHeight("23");
		loadUomList();
		cbmaterial = new GERPComboBox("Material");
		cbmaterial.setItemCaptionPropertyId("materialName");
		loadmateriallist();
		cbmaterial.setImmediate(true);
		cbmaterial.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbmaterial.getValue() != null) {
					System.out.println("uom" + ((MaterialDM) cbmaterial.getValue()).getMaterialUOM());
					cbgoodsuom.setReadOnly(false);
					cbgoodsuom.setValue(((MaterialDM) cbmaterial.getValue()).getMaterialUOM());
					cbgoodsuom.setReadOnly(true);
				}
			}
		});
		cbproduct = new GERPComboBox("Product Name");
		cbproduct.setItemCaptionPropertyId("prodname");
		loadproductlist();
		cbproduct.setImmediate(true);
		cbproduct.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbproduct.getValue() != null) {
					cbgoodsuom.setReadOnly(false);
					cbgoodsuom.setValue(((ProductDM) cbproduct.getValue()).getUom());
					cbgoodsuom.setReadOnly(true);
				}
			}
		});
		cbDC = new GERPComboBox("DC Type");
		cbDC.setItemCaptionPropertyId("dcType");
		loadDCList();
		cbgatepasstype = new GERPComboBox("Gate Pass Type");
		cbgatepasstype.addItem("Returnable");
		cbgatepasstype.addItem("NonReturnable");
		
		cbgatepasstype.setImmediate(true);
		cbgatepasstype.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbgatepasstype.getValue()==("Returnable"))
				{
					cbgatestatus.setValue("Pending");
				}
				else
				{
					if (cbgatepasstype.getValue()==("NonReturnable"))
					{
						cbgatestatus.setValue("Delivered");
					
					}
				}
			}
		});
		
		cbvendor = new GERPComboBox("Vendor");
		cbvendor.setItemCaptionPropertyId("vendorName");
		loadvendordetails();
		cbvendor.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbvendor.getValue() != null) {
					tfvendorname.setValue(((VendorDM) cbvendor.getValue()).getVendorName());
//					tfvendorname.setValue(cbvendor.getValue().toString());
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
		cbmaterial.setEnabled(false);
		cbproduct.setEnabled(false);
		cbmodetransport = new GERPComboBox("Mode of Transport");
		cbmodetransport.setItemCaptionPropertyId("lookupname");
		loadlookupdetails();
		cbgatestatus = new GERPComboBox("Status", BASEConstants.T_MMS_GATEPASS_HDR, BASEConstants.GATE_STATUS);
		cbgatestatus.setWidth("150px");
		cbdtlstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_COLUMN, BASEConstants.M_GENERIC_TABLE);
		cbdtlstatus.setWidth("140px");
		cbVendorDCNo = new GERPComboBox("DC No");
		cbVendorDCNo.setItemCaptionPropertyId("dcNo");
		loaddcdetails();
		gatepassdt = new GERPPopupDateField("Gate Pass Date");
		gatepassdt.setDateFormat("dd-MMM-yyyy");
		dtlead = new GERPPopupDateField("Lead Date");
		dtreturndate = new GERPPopupDateField("Return Date");
		taremarks = new GERPTextArea("Remarks");
		taHdrremarks = new GERPTextArea("Remarks");
		tavendoraddres = new GERPTextArea("Vendor Address");
		tagoodsdesc = new GERPTextArea("Goods Description");
		tfGatePassNo = new GERPTextField("GatePass No");
		btnAddDtl = new GERPButton("Add", "add", this);
		btnAddDtl.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
//				if (btnAddDtl == event.getButton()) {
				if(validategatepassdtl())
				{
					savedtlDetails();
				}
			}
		});
		tbldtl = new GERPTable();
		tbldtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tbldtl.isSelected(event.getItemId())) {
					tbldtl.setImmediate(true);
					btnAddDtl.setCaption("Add");
					btndelete.setEnabled(false);
					btnAddDtl.setStyleName("savebt");
					gatepassDtlResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtl.setCaption("Update");
					btnAddDtl.setStyleName("savebt");
					btndelete.setEnabled(true);
					editDtls();
				}
			}
		});
		hlsearch = new GERPAddEditHLayout();
		assemblesearchlayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearch));
		resetFields();
		loadSrchRslt();
		loadDtlList();
	}
	
	public void assemblesearchlayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		hlsearch.removeAllComponents();
		hlsearch.setMargin(true);
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn4 = new FormLayout();
		// flcolumn1.addComponent(tfGatePassNo);
		flcolumn1.addComponent(gatepassdt);
		flcolumn2.addComponent(cbgatepasstype);
		flcolumn2.setSpacing(true);
		flcolumn2.setMargin(true);
		// flcolumn3.addComponent(cbvendor);
		flcolumn3.addComponent(cbgatestatus);
		hlsearch.addComponent(flcolumn1);
		hlsearch.addComponent(flcolumn2);
		hlsearch.addComponent(flcolumn3);
		// hlsearch.addComponent(flcolumn4);
		hlsearch.setSizeUndefined();
		hlsearch.setMargin(true);
	}
	
	public void assembleuserInputlayout() {
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn4 = new FormLayout();
		// tfGatePassNo.setReadOnly(true);
		flcolumn1.addComponent(tfGatePassNo);
		flcolumn3.addComponent(cbVendorDCNo);
		flcolumn3.addComponent(cbDC);
		flcolumn1.addComponent(cbgatepasstype);
		flcolumn1.addComponent(gatepassdt);
		cbgatepasstype.setRequired(true);
		flcolumn1.addComponent(cbvendor);
		tfvendorname.setRequired(true);
		flcolumn1.addComponent(tfvendorname);
		flcolumn2.addComponent(tavendoraddres);
		tavendoraddres.setHeight("120");
		flcolumn3.addComponent(dtlead);
		flcolumn3.setSizeUndefined();
		flcolumn3.setSpacing(true);
		flcolumn3.addComponent(dtreturndate);
		flcolumn3.addComponent(cbmodetransport);
		flcolumn4.addComponent(tfpersonname);
		flcolumn4.addComponent(taHdrremarks);
		flcolumn4.addComponent(cbgatestatus);
		taHdrremarks.setHeight("70");
		flcolumn4.setSizeUndefined();
		flcolumn4.setSpacing(true);
		hlInput = new HorizontalLayout();
		hlInput.addComponent(flcolumn1);
		hlInput.addComponent(flcolumn2);
		hlInput.addComponent(flcolumn3);
		hlInput.addComponent(flcolumn4);
		// hlInput.addComponent(flcolumn4);
		hlInput.setSpacing(true);
		hlInput.setMargin(true);
		// Detail DetailGatepass
		fldtl1 = new FormLayout();
		fldtl2 = new FormLayout();
		fldtl3 = new FormLayout();
		fldtl4 = new FormLayout();
		fldtl1.addComponent(cbgoodstype);
		cbgoodstype.setRequired(true);
		fldtl1.addComponent(cbmaterial);
		cbmaterial.setRequired(true);
		fldtl1.addComponent(cbproduct);
		cbproduct.setRequired(true);
		fldtl2.addComponent(cbgoodsuom);
		fldtl2.addComponent(tfGatePassQty);
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfGatePassQty);
		hlQtyUom.addComponent(cbgoodsuom);
		hlQtyUom.setCaption("Quantity");
		//hlQtyUom.setWidth("30");
		tfGatePassQty.setWidth("90");
		tfGatePassQty.setHeight("18");
		cbgoodsuom.setWidth("65");
		cbgoodsuom.setHeight("18");
		fldtl2.addComponent(hlQtyUom);
		fldtl2.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		// cbgoodsuom.setRequired(true);
		tagoodsdesc.setHeight("50");
		fldtl2.addComponent(tagoodsdesc);
		taremarks.setHeight("50");
		fldtl3.addComponent(taremarks);
		fldtl3.addComponent(tfReturnQty);
		fldtl4.addComponent(cbdtlstatus);
		cbdtlstatus.setWidth("150");
		fldtl4.addComponent(btnAddDtl);
		fldtl4.addComponent(btndelete);
		hluserinput = new HorizontalLayout();
		hluserinput.addComponent(fldtl1);
		hluserinput.addComponent(fldtl2);
		hluserinput.addComponent(fldtl3);
		hluserinput.addComponent(fldtl4);
		hluserinput.setSpacing(true);
		hluserinput.setMargin(true);
		vlinput = new VerticalLayout();
		vlinput.addComponent(hluserinput);
		vlinput.addComponent(tbldtl);
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(vlinput, "Material Gatepass Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		vlinputlayout = new VerticalLayout();
		vlinputlayout.addComponent(GERPPanelGenerator.createPanel(hlInput));
		vlinputlayout.addComponent(GERPPanelGenerator.createPanel(dtlTab));
		vlinputlayout.setSpacing(true);
		hluserinputlayout.addComponent(vlinputlayout);
		hluserinputlayout.setSizeUndefined();
		hluserinputlayout.setWidth("100%");
		hluserinputlayout.setMargin(true);
		hluserinputlayout.setSpacing(true);
		hlCmdBtnLayout.setVisible(true);
		loadDtlList();
	}
	
	public void loadSrchRslt() {
		tblMstScrSrchRslt.removeAllItems();
		List<GatepassHdrDM> gatepass = new ArrayList<GatepassHdrDM>();
		beanGatePassHdr = new BeanItemContainer<GatepassHdrDM>(GatepassHdrDM.class);
		/*
		 * if (cbvendor.getValue() != null) { vendorId = (Long.valueOf(cbvendor.getValue().toString())); }
		 */
		Date gatepassdtt = gatepassdt.getValue();
		gatepass = servicegatepass.getGatepassHdrList(companyId, gatepassdtt, (String) cbgatepasstype.getValue(), null,
				null, vendorId, (String)cbgatestatus.getValue(), (String) tfGatePassNo.getValue(), "F");
		recordCnt = gatepass.size();
		beanGatePassHdr.addAll(gatepass);
		tblMstScrSrchRslt.setContainerDataSource(beanGatePassHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "gatepassId", "gatepassDt", "gatepassType",
				"gatepassStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.No", "Gate Pass Date ", "Gate Pass Type",
				"Status", "Updated Date", "Updated BY" });
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of.Records :" + recordCnt);
		tblMstScrSrchRslt.setPageLength(13);
	}
	
	public void loadDtlList() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			logger.info("Company ID : " + companyId + " | saveindentDtlListDetails User Name : " + userName + " > "
					+ "Search Parameters are " + companyId + ", " + tfpersonname.getValue() + ", "
					+ tfvendorname.getValue() + (String) cbdtlstatus.getValue());
			recordCnt = gatepassdtllist.size();
			beanGatePassDtl = new BeanItemContainer<GatepassDtlDM>(GatepassDtlDM.class);
			beanGatePassDtl.addAll(gatepassdtllist);
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Got the Taxslap. result set");
			tbldtl.setContainerDataSource(beanGatePassDtl);
			tbldtl.setVisibleColumns(new Object[] { "goodstype", "gatepassquanty", "returnqty", "status" });
			tbldtl.setColumnHeaders(new String[] { "Goods Type", "Quantity", "Return Quantity", "Status" });
			tbldtl.setColumnFooter("status", "No.of.Records :" + recordCnt);
			tbldtl.setPageLength(4);
			tbldtl.setSizeFull();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadvendordetails() {
		List<VendorDM> vendorlist = servicevendor.getVendorList(null, null, companyId, null, null, null, null, null,
				null, null, "P");
		beanvendor = new BeanItemContainer<VendorDM>(VendorDM.class);
		// beanvendor.setBeanIdProperty("vendorId");
		beanvendor.addAll(vendorlist);
		cbvendor.setContainerDataSource(beanvendor);
	}
	
//	public void loadvendordetails() {
//		List<VendorDM> vendorlist = servicevendor.getVendorList(null, null, companyId, null, null, null, null, null,
//				null, null, "P");
//		beanvendordm = new BeanContainer<Long,VendorDM>(VendorDM.class);
//		beanvendordm.setBeanIdProperty("vendorId");
//		beanvendordm.addAll(vendorlist);
//		cbvendor.setContainerDataSource(beanvendordm);
//	}
//	
	public void loaddcdetails() {
		List<DcHdrDM> dcList = serviceDCHdr.getMmsDcHdrList(null, null, companyId, null, null, null, null, null, null,
				"F");
		beanHdrDC = new BeanContainer<String, DcHdrDM>(DcHdrDM.class);
		beanHdrDC.setBeanIdProperty("dcNo");
		beanHdrDC.addAll(dcList);
		cbVendorDCNo.setContainerDataSource(beanHdrDC);
	}
	
	public void loadlookupdetails() {
		List<CompanyLookupDM> lookuplist = servicelookup.getCompanyLookUpByLookUp(companyId, moduleId, "Active",
				"MM_TRNSPRT");
		beanlookup = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanlookup.setBeanIdProperty("lookupname");
		beanlookup.addAll(lookuplist);
		cbmodetransport.setContainerDataSource(beanlookup);
	}
	
	// Load Uom List
	public void loadUomList() {
		try {
			List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyId, null, "Active",
					"MM_UOM");
			beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(lookUpList);
			cbgoodsuom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadmateriallist() {
		List<MaterialDM> materialist = servicematerial.getMaterialList(null, companyId, null, null, null, null, null,
				null, null, "P");
		beanmaterial = new BeanItemContainer<MaterialDM>(MaterialDM.class);
		beanmaterial.addAll(materialist);
		cbmaterial.setContainerDataSource(beanmaterial);
	}
	
	public void loadproductlist() {
		List<ProductDM> productlist = ServiceProduct.getProductList(companyId, null, null, null, "Active", null, null,
				"P");
		beanproduct = new BeanItemContainer<ProductDM>(ProductDM.class);
		beanproduct.addAll(productlist);
		cbproduct.setContainerDataSource(beanproduct);
	}
	
	public void loadgatepasslist() {
		List<GatepassHdrDM> gatepasslist = servicegatepass.getGatepassHdrList(companyId, null, null, null, null, null,
				null, null, "F");
		BeanItemContainer<GatepassHdrDM> beanGatePassHdrlist = new BeanItemContainer<GatepassHdrDM>(GatepassHdrDM.class);
		beanGatePassHdrlist.addAll(gatepasslist);
		cbgoods.setContainerDataSource(beanGatePassHdrlist);
	}
	
	public void loadDCList() {
		List<DcHdrDM> dcList = servicedc
				.getMmsDcHdrList(null, null, companyId, null, null, null, null, null, null, "F");
		beanDC = new BeanContainer<Long, DcHdrDM>(DcHdrDM.class);
		beanDC.setBeanIdProperty("dcId");
		beanDC.addAll(dcList);
		cbDC.setContainerDataSource(beanDC);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			assemblesearchlayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		tfvendorname.setValue("");
		gatepassdt.setValue(null);
		cbgatepasstype.setValue(null);
		cbgatestatus.setValue(cbgatestatus.getItemIds().iterator().next());
		loadSrchRslt();
//		resetFields();
	}
	
	@Override
	protected void addDetails() {
		hluserinputlayout.removeAllComponents();
		hlUserIPContainer.addComponent(hluserinputlayout);
		resetFields();
		gatepassdt.setValue(new Date());
		gatepassDtlResetFields();
		assembleuserInputlayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserinputlayout));
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtl.setCaption("Add");
		tbldtl.setVisible(true);
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyId, branchID, moduleId, "MM_GPNO");
		// tfGatePassNo.setReadOnly(true);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfGatePassNo.setReadOnly(true);
			} else {
				tfGatePassNo.setReadOnly(false);
			}
		}
		tbldtl.setVisible(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtl.setCaption("Add");
		tbldtl.setVisible(true);
		comments = new MmsComments(vlTableForm, null, companyId, null, null, null, null, null, null, null, status);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		hluserinputlayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hluserinputlayout);
		hlCmdBtnLayout.setVisible(false);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		assembleuserInputlayout();
		resetFields();
		editHdrIndentDetails();
		editDtls();
	}
	
	// Method to edit the values from table into fields to update process
	private void editHdrIndentDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Editing the selected record");
		hluserinputlayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (sltedRcd != null) {
			GatepassHdrDM editHdrIndent = beanGatePassHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Selected Tax. Id -> ");
			gatePassId = editHdrIndent.getGatepassId();
			cbDC.setValue(editHdrIndent.getDcId());
			tfGatePassNo.setReadOnly(false);
			tfGatePassNo.setValue((String) sltedRcd.getItemProperty("gatepassNo").getValue());
			tfGatePassNo.setReadOnly(true);
			if (gatepassdt.getValue() != null) {
				gatepassdt.setValue(editHdrIndent.getGatepassDtInt());
			}
			cbgatepasstype.setValue(editHdrIndent.getGatepassType());
			cbmodetransport.setValue(editHdrIndent.getModeOfTrans());
//			Long vendorid = editHdrIndent.getVendorId();
//			Collection<?> vendorIdCol = cbvendor.getItemIds();
//			for (Iterator<?> iteratorclient = vendorIdCol.iterator(); iteratorclient.hasNext();) {
//				Object itemIdClient = (Object) iteratorclient.next();
//				BeanItem<?> itemclient = (BeanItem<?>) cbvendor.getItem(itemIdClient);
//				// Get the actual bean and use the data
//				VendorDM matObj = (VendorDM) itemclient.getBean();
//				if (vendorid != null && vendorid.equals(matObj.getVendorId())) {
//					cbvendor.setValue(itemIdClient);
//				}
//			}
			
			String uom = editHdrIndent.getVendorName();
			Collection<?> uomid = cbvendor.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbvendor.getItem(itemId);
				// Get the actual bean and use the data
				VendorDM st = (VendorDM) item.getBean();
				if (uom != null && uom.equals(st.getVendorName())) {
					cbvendor.setValue(itemId);
				}
			}
			
			tfvendorname.setValue(editHdrIndent.getVendorName());
			tfpersonname.setValue(editHdrIndent.getPersonName());
			cbVendorDCNo.setValue(editHdrIndent.getVendorDcno());
			taHdrremarks.setValue(editHdrIndent.getRemarks());
			gatepassdt.setValue(editHdrIndent.getGatepassDtInt());
			tavendoraddres.setValue(editHdrIndent.getVendorAddress());
			dtlead.setValue(editHdrIndent.getLeadDate());
			dtreturndate.setValue(editHdrIndent.getReturnDate());
			cbgatestatus.setValue(editHdrIndent.getGatepassStatus());
			editHdrIndent.setLastUpdatedDt(DateUtils.getcurrentdate());
			editHdrIndent.setLastUpdatedBy(userName);
			gatepassdtllist.addAll(servicegatepassdtl.getGatepassDtlList(null, gatePassId, null, null, null, "Active",
					"F"));
		}
		loadDtlList();
		comments = new MmsComments(vlTableForm, null, companyId, null, null, null, null, null, null, gatePassId, null);
		comments.loadsrch(true, null, null, null, null, null, null, null, null, gatePassId);
	}
	
	private void editDtls() {
		// hluserinputlayout.setVisible(true);
		Item itselect = tbldtl.getItem(tbldtl.getValue());
		if (itselect != null) {
			GatepassDtlDM editDtl = new GatepassDtlDM();
			editDtl = beanGatePassDtl.getItem(tbldtl.getValue()).getBean();
			if (itselect.getItemProperty("goodstype").getValue() != null) {
				cbgoodstype.setValue(itselect.getItemProperty("goodstype").getValue().toString());
			}
			Long matId = editDtl.getMaterialId();
			Collection<?> empColId = cbmaterial.getItemIds();
			for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbmaterial.getItem(itemIdClient);
				// Get the actual bean and use the data
				MaterialDM matObj = (MaterialDM) itemclient.getBean();
				if (matId != null && matId.equals(matObj.getMaterialId())) {
					cbmaterial.setValue(itemIdClient);
				}
			}
			Long prodId = editDtl.getProductid();
			Collection<?> prodIdCol = cbproduct.getItemIds();
			for (Iterator<?> iteratorclient = prodIdCol.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbproduct.getItem(itemIdClient);
				// Get the actual bean and use the data
				ProductDM matObj = (ProductDM) itemclient.getBean();
				if (prodId != null && prodId.equals(matObj.getProdid())) {
					cbproduct.setValue(itemIdClient);
				}
			}
			if (itselect.getItemProperty("goodsuom").getValue() != null) {
				cbgoodsuom.setReadOnly(false);
				cbgoodsuom.setValue(itselect.getItemProperty("goodsuom").getValue().toString());
				cbgoodsuom.setReadOnly(true);
			}
			/*
			 * if (itselect.getItemProperty("goodsuom").getValue() != null) {
			 * cbgoodsuom.setValue(itselect.getItemProperty("goodsuom").getValue().toString()); }
			 */
			if (itselect.getItemProperty("gatepassquanty").getValue() != null) {
				tfGatePassQty.setValue(itselect.getItemProperty("gatepassquanty").getValue().toString());
			}
			if (itselect.getItemProperty("returnqty").getValue() != null) {
				tfReturnQty.setValue(itselect.getItemProperty("returnqty").getValue().toString());
			}
			if (itselect.getItemProperty("goodsDesc").getValue() != null) {
				tagoodsdesc.setValue(itselect.getItemProperty("goodsDesc").getValue().toString());
			}
			if (itselect.getItemProperty("returnremarks").getValue() != null) {
				taremarks.setValue(itselect.getItemProperty("returnremarks").getValue().toString());
			}
			cbdtlstatus.setValue(itselect.getItemProperty("status").getValue());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean errorflag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((cbgatepasstype.getValue() == null)) {
			cbgatepasstype.setComponentError(new UserError(GERPErrorCodes.NULL_MMS_gatepassType));
			errorflag = true;
		} else {
			cbgatepasstype.setComponentError(null);
		}
		if (cbvendor.getValue() == null) {
			cbvendor.setComponentError(new UserError(GERPErrorCodes.NULL_VENDOR_NAME));
			errorflag = true;
		} else {
			cbvendor.setComponentError(null);
		}
		if (tbldtl.size() == 0) {
		cbgoodstype.setComponentError(new UserError(GERPErrorCodes.NULL_POMATERIAL_UOM));
//			cbmaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
//			cbproduct.setComponentError(new UserError(GERPErrorCodes.PRODUCT_NAME));
			cbgoodsuom.setComponentError(new UserError(GERPErrorCodes.NULL_POMATERIAL_UOM));
			errorflag = true;
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + cbgatepasstype.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validategatepassdtl() {
		boolean errorflag = true;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((cbgoodstype.getValue() == null)) {
			cbgoodstype.setComponentError(new UserError(GERPErrorCodes.NULL_POMATERIAL_UOM));
			errorflag = false;
		} else {
			cbgoodstype.setComponentError(null);
		}
		cbgoodstype.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbgoodstype.getValue()==("Material"))
				{
					cbmaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
					cbproduct.setComponentError(null);
				}
				else
				{
					if (cbgoodstype.getValue()==("Product"))
					{
						cbproduct.setComponentError(new UserError(GERPErrorCodes.PRODUCT_NAME));
						cbmaterial.setComponentError(null);
					
					}
				}
			}
		});
		
		
//		if (cbmaterial.getValue() == null) {
//			cbmaterial.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
//			errorflag = false;
//		} else {
//			cbmaterial.setComponentError(null);
//		}
//		if (cbproduct.getValue() == null) {
//			cbproduct.setComponentError(new UserError(GERPErrorCodes.PRODUCT_NAME));
//			errorflag = false;
//		} else {
//			cbproduct.setComponentError(null);
//		}
		if (cbgoodsuom.getValue() == null) {
			cbgoodsuom.setComponentError(new UserError(GERPErrorCodes.NULL_POMATERIAL_UOM));
			errorflag = false;
		} else {
			cbgoodsuom.setComponentError(null);
		}
		return errorflag;
	}
	
	private void gatepassDtlResetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Resetting the UI controls");
		cbgoodstype.setValue(null);
		cbgoodstype.setComponentError(null);
		cbmaterial.setComponentError(null);
		cbproduct.setComponentError(null);
		cbgoodsuom.setComponentError(null);
		tfGatePassQty.setValue("");
		cbgoodsuom.setReadOnly(false);
		cbgoodsuom.setValue(null);
		cbgoodsuom.setReadOnly(true);
		cbmaterial.setValue(null);
		cbproduct.setValue(null);
		tagoodsdesc.setValue("");
		cbdtlstatus.setValue(cbdtlstatus.getItemIds().iterator().next());
		taremarks.setValue("");
		tfReturnQty.setValue("");
		btnAddDtl.setComponentError(null);
	}
	
	@Override
	protected void saveDetails() {
		try {
			GatepassHdrDM gatepasshdr = new GatepassHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				gatepasshdr = beanGatePassHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			} else {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyId, branchID, moduleId, "MM_GPNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						gatepasshdr.setGatepassNo(slnoObj.getKeyDesc());
					}
				}
			}
			gatepasshdr.setBranchId(branchID);
			gatepasshdr.setCompanyId(companyId);
			gatepasshdr.setDcId((Long) (cbDC.getValue()));
			gatepasshdr.setGatepassType((String) cbgatepasstype.getValue());
			gatepasshdr.setModeOfTrans((String) cbmodetransport.getValue());
			gatepasshdr.setVendorId(Long.valueOf(((VendorDM) cbvendor.getValue()).getVendorId().toString()));
			gatepasshdr.setVendorName(tfvendorname.getValue());
			gatepasshdr.setPersonName(tfpersonname.getValue());
//			gatepasshdr.setRemarks(tfpersonname.getValue());
			gatepasshdr.setVendorDcno((String) cbVendorDCNo.getValue());
			gatepasshdr.setVendorAddress(tavendoraddres.getValue().toString());
			gatepasshdr.setLeadDate(dtlead.getValue());
			gatepasshdr.setGatepassDt(gatepassdt.getValue());
			gatepasshdr.setReturnDate(dtreturndate.getValue());
			gatepasshdr.setRemarks(taHdrremarks.getValue());
			gatepasshdr.setGatepassStatus((String) cbgatestatus.getValue());
			gatepasshdr.setLastUpdatedDt(DateUtils.getcurrentdate());
			gatepasshdr.setLastUpdatedBy(userName);
			servicegatepass.saveorUpdateGatepassHdrDetails(gatepasshdr);
			tfGatePassNo.setReadOnly(false);
			tfGatePassNo.setValue(gatepasshdr.getGatepassNo().toString());
			tfGatePassNo.setReadOnly(true);
			@SuppressWarnings("unchecked")
			Collection<GatepassDtlDM> colPlanDtls = ((Collection<GatepassDtlDM>) tbldtl.getVisibleItemIds());
			for (GatepassDtlDM saveDtl : (Collection<GatepassDtlDM>) colPlanDtls) {
				saveDtl.setGatepassid(Long.valueOf(gatepasshdr.getGatepassId()));
				servicegatepassdtl.saveorupdateDtlDetails(saveDtl);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyId, branchID, moduleId, "MM_GPNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyId, branchID, moduleId, "MM_GPNO");
					}
				}
			}
			comments.savegatepass(gatepasshdr.getGatepassId(), gatepasshdr.getGatepassStatus());
			comments.resetfields();
			gatepassDtlResetFields();
			//resetFields();
			loadSrchRslt();
			loadDtlList();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void savedtlDetails() {
		validategatepassdtl();
		GatepassDtlDM dtlpass = new GatepassDtlDM();
		if (tbldtl.getValue() != null) {
			dtlpass = beanGatePassDtl.getItem(tbldtl.getValue()).getBean();
		}
		dtlpass.setGoodstype((String) cbgoodstype.getValue());
		if (cbmaterial.getValue() != null) {
			dtlpass.setMaterialId(((MaterialDM) cbmaterial.getValue()).getMaterialId());
			dtlpass.setMaterialname(((MaterialDM) cbmaterial.getValue()).getMaterialName());
		}
		if (cbproduct.getValue() != null) {
			dtlpass.setProductid(((ProductDM) cbproduct.getValue()).getProdid());
			dtlpass.setProductname(((ProductDM) cbproduct.getValue()).getProdname());
		}
		dtlpass.setGoodsDesc(tagoodsdesc.getValue().toString());
		if (tfGatePassQty.getValue().toString().trim().length() > 0) {
			dtlpass.setGatepassquanty(Long.valueOf(tfGatePassQty.getValue()));
		}
		if (tfReturnQty.getValue().trim().length() > 0) {
			dtlpass.setReturnqty(Long.valueOf(tfReturnQty.getValue()));
		}
		if (cbgoodsuom.getValue() != null) {
			cbgoodsuom.setReadOnly(false);
			dtlpass.setGoodsuom(cbgoodsuom.getValue().toString());
			cbgoodsuom.setReadOnly(true);
		}
		dtlpass.setReturnremarks(taremarks.getValue());
		dtlpass.setStatus((String) cbdtlstatus.getValue());
		dtlpass.setLastupdatedt(DateUtils.getcurrentdate());
		dtlpass.setLastupdatedBy(userName);
		gatepassdtllist.add(dtlpass);
		gatepassDtlResetFields();
		loadDtlList();
	}
	
	/*
	 * catch (Exception e) { e.printStackTrace(); } gatepassDtlResetFields(); }
	 */
	@Override
	protected void showAuditDetails() {
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		// hlUserIPContainer.removeAllComponents();
		assemblesearchlayout();
		// gatepassDtlResetFields();
		hlCmdBtnLayout.setVisible(true);
		tbldtl.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		cbgatepasstype.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		tfGatePassNo.setReadOnly(false);
		tfGatePassNo.setValue("");
		tfGatePassNo.setReadOnly(true);
		gatepassdt.setValue(null);
		cbgatepasstype.setValue(null);
		cbDC.setValue(null);
		cbvendor.setValue(null);
		cbvendor.setComponentError(null);
		tfvendorname.setValue("");
		tavendoraddres.setValue("");
		cbVendorDCNo.setValue(null);
		dtlead.setValue(null);
		dtreturndate.setValue(null);
		cbmodetransport.setValue(null);
		tfpersonname.setValue("");
		taHdrremarks.setValue("");
		cbgatepasstype.setComponentError(null);
		cbgatestatus.setValue(cbgatestatus.getItemIds().iterator().next());
		gatepassdtllist = new ArrayList<GatepassDtlDM>();
		tbldtl.removeAllItems();
	}
	
	private void deleteDetails() {
		GatepassDtlDM save = new GatepassDtlDM();
		if (tbldtl.getValue() != null) {
			save = beanGatePassDtl.getItem(tbldtl.getValue()).getBean();
			gatepassdtllist.remove(save);
			gatepassDtlResetFields();
			loadDtlList();
			btndelete.setEnabled(false);
		}
	}

	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
		
	}
}
