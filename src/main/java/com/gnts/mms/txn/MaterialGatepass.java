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
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.DcHdrDM;
import com.gnts.mms.domain.txn.GatepassDtlDM;
import com.gnts.mms.domain.txn.GatepassHdrDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.DcHdrService;
import com.gnts.mms.service.txn.GatepassDtlService;
import com.gnts.mms.service.txn.GatepassHdrService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
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
	private static final long serialVersionUID = 1L;
	private GatepassHdrService serviceGatepass = (GatepassHdrService) SpringContextHelper.getBean("gatepasshdr");
	private GatepassDtlService servicegatepassdtl = (GatepassDtlService) SpringContextHelper.getBean("gatepassdtl");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private DcHdrService servicedc = (DcHdrService) SpringContextHelper.getBean("DCHdr");
	private DcHdrService serviceDCHdr = (DcHdrService) SpringContextHelper.getBean("DCHdr");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private CompanyLookupService servicelookup = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private BeanItemContainer<GatepassHdrDM> beanGatePassHdr;
	private BeanItemContainer<GatepassDtlDM> beanGatePassDtl;
	private TextField tfVendorname, tfpersonname, tfGatePassNo, tfGatePassQty, tfReturnQty;
	private ComboBox cbGatepasstype, cbVendor, cbgatestatus, cbModeTransport, cbVendorDCNo, cbGoods, cbGoodsUom,
			cbGoodsType, cbMaterial, cbProduct, cbdtlstatus, cbDC;
	private PopupDateField dtlead, dfReturndate, gatepassdt;
	private TextArea taRemarks, taHdrremarks, tavendoraddres, tagoodsdesc;
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4, fldtl1, fldtl2, fldtl3, fldtl4;
	private HorizontalLayout hlsearch, hlInput, hluserinput;
	private HorizontalLayout hluserinputlayout = new HorizontalLayout();
	private VerticalLayout vlinput, vlinputlayout;
	private Table tblGatepassDetails = new Table();
	private Long gatePassId;
	private Button btnAddDtl;
	private List<GatepassDtlDM> listGatePassDtls = null;
	private String userName;
	private Long companyId, moduleId, branchID, vendorId;
	private MmsComments comments;
	private VerticalLayout vlTableForm = new VerticalLayout();
	private int recordCnt = 0;
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private Logger logger = Logger.getLogger(MaterialGatepass.class);
	
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
	
	private void buildview() {
		btndelete.setEnabled(false);
		tfpersonname = new GERPTextField("Person");
		tfVendorname = new GERPTextField("Through");
		tfGatePassQty = new TextField();
		tfGatePassQty.setValue("0");
		tfGatePassQty.setWidth("90");
		tfReturnQty = new GERPTextField("Return Quantity.");
		cbGoods = new GERPComboBox("Goods");
		cbGoods.setItemCaptionPropertyId("gatepassDt");
		loadgatepasslist();
		cbGoodsType = new GERPComboBox("Goods Type");
		cbGoodsType.addItem("Material");
		cbGoodsType.addItem("Product");
		cbGoodsType.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbGoodsType.getValue() != null) {
					if (cbGoodsType.getValue().equals("Material")) {
						cbProduct.setEnabled(false);
						cbMaterial.setEnabled(true);
						cbMaterial.setValue(null);
						cbProduct.setValue(null);
					} else if (cbGoodsType.getValue().equals("Product")) {
						cbMaterial.setEnabled(false);
						cbProduct.setEnabled(true);
						cbMaterial.setValue(null);
						cbProduct.setValue(null);
					}
				}
			}
		});
		cbGoodsUom = new ComboBox();
		cbGoodsUom.setItemCaptionPropertyId("lookupname");
		cbGoodsUom.setWidth("77");
		cbGoodsUom.setHeight("23");
		loadUomList();
		cbMaterial = new GERPComboBox("Material");
		cbMaterial.setItemCaptionPropertyId("materialName");
		loadMateriallist();
		cbMaterial.setImmediate(true);
		cbMaterial.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbMaterial.getValue() != null) {
					cbGoodsUom.setReadOnly(false);
					cbGoodsUom.setValue(((MaterialDM) cbMaterial.getValue()).getMaterialUOM());
					cbGoodsUom.setReadOnly(true);
				}
			}
		});
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		loadProductlist();
		cbProduct.setImmediate(true);
		cbProduct.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbProduct.getValue() != null) {
					cbGoodsUom.setReadOnly(false);
					cbGoodsUom.setValue(((ProductDM) cbProduct.getValue()).getUom());
					cbGoodsUom.setReadOnly(true);
				}
			}
		});
		cbDC = new GERPComboBox("DC Type");
		cbDC.setItemCaptionPropertyId("dcType");
		loadDCList();
		cbGatepasstype = new GERPComboBox("Gate Pass Type");
		cbGatepasstype.addItem("Returnable");
		cbGatepasstype.addItem("NonReturnable");
		cbGatepasstype.setImmediate(true);
		cbGatepasstype.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbGatepasstype.getValue() == ("Returnable")) {
					cbgatestatus.setValue("Pending");
				} else {
					if (cbGatepasstype.getValue() == ("NonReturnable")) {
						cbgatestatus.setValue("Delivered");
					}
				}
			}
		});
		cbVendor = new GERPComboBox("Vendor");
		cbVendor.setItemCaptionPropertyId("vendorName");
		loadvendordetails();
		cbVendor.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbVendor.getValue() != null) {
					VendorDM vendordm = serviceVendor.getVendorList(null,
							((VendorDM) cbVendor.getValue()).getVendorId(), null, null, null, null, null, null, null,
							null, "F").get(0);
					if ((vendordm.getVendorAddress().toString() != null)
							&& (vendordm.getcountryID().toString() != null)
							&& (vendordm.getStateId().toString() != null) && (vendordm.getCityId().toString() != null)) {
						tavendoraddres.setValue((vendordm.getVendorAddress().toString()) + "\n"
								+ (vendordm.getCityName().toString()) + "\n" + (vendordm.getStateName()).toString()
								+ "\n" + (vendordm.getCountryName().toString()) + "-"
								+ (vendordm.getVendorPostcode().toString()));
						tfVendorname.setValue(vendordm.getContactName());
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
		cbMaterial.setEnabled(false);
		cbProduct.setEnabled(false);
		cbModeTransport = new GERPComboBox("Mode of Transport");
		cbModeTransport.setItemCaptionPropertyId("lookupname");
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
		dfReturndate = new GERPPopupDateField("Return Date");
		taRemarks = new GERPTextArea("Remarks");
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
				// if (btnAddDtl == event.getButton()) {
				if (validategatepassdtl()) {
					saveGatepassDetails();
				}
			}
		});
		tblGatepassDetails = new GERPTable();
		tblGatepassDetails.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblGatepassDetails.isSelected(event.getItemId())) {
					tblGatepassDetails.setImmediate(true);
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
	
	private void assemblesearchlayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		hlsearch.removeAllComponents();
		hlsearch.setMargin(true);
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn4 = new FormLayout();
		flcolumn1.addComponent(gatepassdt);
		flcolumn2.addComponent(cbGatepasstype);
		flcolumn2.setSpacing(true);
		flcolumn2.setMargin(true);
		flcolumn3.addComponent(cbgatestatus);
		hlsearch.addComponent(flcolumn1);
		hlsearch.addComponent(flcolumn2);
		hlsearch.addComponent(flcolumn3);
		hlsearch.setSizeUndefined();
		hlsearch.setMargin(true);
	}
	
	private void assembleuserInputlayout() {
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn4 = new FormLayout();
		flcolumn1.addComponent(tfGatePassNo);
		flcolumn3.addComponent(cbVendorDCNo);
		flcolumn3.addComponent(cbDC);
		flcolumn1.addComponent(cbGatepasstype);
		flcolumn1.addComponent(gatepassdt);
		cbGatepasstype.setRequired(true);
		flcolumn1.addComponent(cbVendor);
		tfVendorname.setRequired(true);
		flcolumn1.addComponent(tfVendorname);
		flcolumn2.addComponent(tavendoraddres);
		tavendoraddres.setHeight("120");
		flcolumn3.addComponent(dtlead);
		flcolumn3.setSizeUndefined();
		flcolumn3.setSpacing(true);
		flcolumn3.addComponent(dfReturndate);
		flcolumn3.addComponent(cbModeTransport);
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
		hlInput.setSpacing(true);
		hlInput.setMargin(true);
		// Detail DetailGatepass
		fldtl1 = new FormLayout();
		fldtl2 = new FormLayout();
		fldtl3 = new FormLayout();
		fldtl4 = new FormLayout();
		fldtl1.addComponent(cbGoodsType);
		cbGoodsType.setRequired(true);
		fldtl1.addComponent(cbMaterial);
		cbMaterial.setRequired(true);
		fldtl1.addComponent(cbProduct);
		cbProduct.setRequired(true);
		fldtl2.addComponent(cbGoodsUom);
		fldtl2.addComponent(tfGatePassQty);
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfGatePassQty);
		hlQtyUom.addComponent(cbGoodsUom);
		hlQtyUom.setCaption("Quantity");
		// hlQtyUom.setWidth("30");
		tfGatePassQty.setWidth("90");
		tfGatePassQty.setHeight("18");
		cbGoodsUom.setWidth("65");
		cbGoodsUom.setHeight("18");
		fldtl2.addComponent(hlQtyUom);
		fldtl2.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		// cbgoodsuom.setRequired(true);
		tagoodsdesc.setHeight("50");
		fldtl2.addComponent(tagoodsdesc);
		taRemarks.setHeight("50");
		fldtl3.addComponent(taRemarks);
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
		vlinput.addComponent(tblGatepassDetails);
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
		loadDtlList();
	}
	
	private void loadSrchRslt() {
		tblMstScrSrchRslt.removeAllItems();
		List<GatepassHdrDM> gatepass = new ArrayList<GatepassHdrDM>();
		beanGatePassHdr = new BeanItemContainer<GatepassHdrDM>(GatepassHdrDM.class);
		gatepass = serviceGatepass.getGatepassHdrList(companyId, gatepassdt.getValue(),
				(String) cbGatepasstype.getValue(), null, null, vendorId, (String) cbgatestatus.getValue(),
				(String) tfGatePassNo.getValue(), "F");
		recordCnt = gatepass.size();
		beanGatePassHdr.addAll(gatepass);
		tblMstScrSrchRslt.setContainerDataSource(beanGatePassHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "gatepassId", "gatepassDt", "gatepassType",
				"gatepassStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.No", "Gate Pass Date ", "Gate Pass Type", "Status",
				"Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of.Records :" + recordCnt);
		tblMstScrSrchRslt.setPageLength(13);
	}
	
	private void loadDtlList() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			logger.info("Company ID : " + companyId + " | saveindentDtlListDetails User Name : " + userName + " > "
					+ "Search Parameters are " + companyId + ", " + tfpersonname.getValue() + ", "
					+ tfVendorname.getValue() + (String) cbdtlstatus.getValue());
			recordCnt = listGatePassDtls.size();
			beanGatePassDtl = new BeanItemContainer<GatepassDtlDM>(GatepassDtlDM.class);
			beanGatePassDtl.addAll(listGatePassDtls);
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Got the Taxslap. result set");
			tblGatepassDetails.setContainerDataSource(beanGatePassDtl);
			tblGatepassDetails.setVisibleColumns(new Object[] { "goodstype", "gatepassquanty", "returnqty", "status" });
			tblGatepassDetails.setColumnHeaders(new String[] { "Goods Type", "Quantity", "Return Quantity", "Status" });
			tblGatepassDetails.setColumnFooter("status", "No.of.Records :" + recordCnt);
			tblGatepassDetails.setPageLength(4);
			tblGatepassDetails.setSizeFull();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadvendordetails() {
		BeanItemContainer<VendorDM> beanvendor = new BeanItemContainer<VendorDM>(VendorDM.class);
		beanvendor.addAll(serviceVendor.getVendorList(null, null, companyId, null, null, null, null, null, null, null,
				"P"));
		cbVendor.setContainerDataSource(beanvendor);
	}
	
	private void loaddcdetails() {
		BeanContainer<String, DcHdrDM> beanHdrDC = new BeanContainer<String, DcHdrDM>(DcHdrDM.class);
		beanHdrDC.setBeanIdProperty("dcNo");
		beanHdrDC.addAll(serviceDCHdr.getMmsDcHdrList(null, null, companyId, null, null, null, null, null, null, "F"));
		cbVendorDCNo.setContainerDataSource(beanHdrDC);
	}
	
	private void loadlookupdetails() {
		BeanContainer<String, CompanyLookupDM> beanlookup = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanlookup.setBeanIdProperty("lookupname");
		beanlookup.addAll(servicelookup.getCompanyLookUpByLookUp(companyId, moduleId, "Active", "MM_TRNSPRT"));
		cbModeTransport.setContainerDataSource(beanlookup);
	}
	
	// Load Uom List
	private void loadUomList() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp
					.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyId, null, "Active", "MM_UOM"));
			cbGoodsUom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadMateriallist() {
		BeanItemContainer<MaterialDM> beanmaterial = new BeanItemContainer<MaterialDM>(MaterialDM.class);
		beanmaterial.addAll(serviceMaterial.getMaterialList(null, companyId, null, null, null, null, null, null, null,
				"P"));
		cbMaterial.setContainerDataSource(beanmaterial);
	}
	
	private void loadProductlist() {
		BeanItemContainer<ProductDM> beanproduct = new BeanItemContainer<ProductDM>(ProductDM.class);
		beanproduct.addAll(serviceProduct.getProductList(companyId, null, null, null, "Active", null, null, "P"));
		cbProduct.setContainerDataSource(beanproduct);
	}
	
	private void loadgatepasslist() {
		BeanItemContainer<GatepassHdrDM> beanGatePassHdrlist = new BeanItemContainer<GatepassHdrDM>(GatepassHdrDM.class);
		beanGatePassHdrlist.addAll(serviceGatepass.getGatepassHdrList(companyId, null, null, null, null, null, null,
				null, "F"));
		cbGoods.setContainerDataSource(beanGatePassHdrlist);
	}
	
	private void loadDCList() {
		BeanContainer<Long, DcHdrDM> beanDC = new BeanContainer<Long, DcHdrDM>(DcHdrDM.class);
		beanDC.setBeanIdProperty("dcId");
		beanDC.addAll(servicedc.getMmsDcHdrList(null, null, companyId, null, null, null, null, null, null, "F"));
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
		tfVendorname.setValue("");
		gatepassdt.setValue(null);
		cbGatepasstype.setValue(null);
		cbgatestatus.setValue(cbgatestatus.getItemIds().iterator().next());
		loadSrchRslt();
		// resetFields();
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
		tblGatepassDetails.setVisible(true);
		tblGatepassDetails.setVisible(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtl.setCaption("Add");
		tblGatepassDetails.setVisible(true);
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
		if (tblMstScrSrchRslt.getValue() != null) {
			GatepassHdrDM editHdrIndent = beanGatePassHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Selected Tax. Id -> ");
			gatePassId = editHdrIndent.getGatepassId();
			cbDC.setValue(editHdrIndent.getDcId());
			tfGatePassNo.setReadOnly(false);
			tfGatePassNo.setValue(editHdrIndent.getGatepassNo());
			if (gatepassdt.getValue() != null) {
				gatepassdt.setValue(editHdrIndent.getGatepassDtInt());
			}
			cbGatepasstype.setValue(editHdrIndent.getGatepassType());
			cbModeTransport.setValue(editHdrIndent.getModeOfTrans());
			String uom = editHdrIndent.getVendorName();
			Collection<?> uomid = cbVendor.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbVendor.getItem(itemId);
				// Get the actual bean and use the data
				VendorDM st = (VendorDM) item.getBean();
				if (uom != null && uom.equals(st.getVendorName())) {
					cbVendor.setValue(itemId);
				}
			}
			tfVendorname.setValue(editHdrIndent.getVendorName());
			tfpersonname.setValue(editHdrIndent.getPersonName());
			cbVendorDCNo.setValue(editHdrIndent.getVendorDcno());
			taHdrremarks.setValue(editHdrIndent.getRemarks());
			gatepassdt.setValue(editHdrIndent.getGatepassDtInt());
			tavendoraddres.setValue(editHdrIndent.getVendorAddress());
			dtlead.setValue(editHdrIndent.getLeadDate());
			dfReturndate.setValue(editHdrIndent.getReturnDate());
			cbgatestatus.setValue(editHdrIndent.getGatepassStatus());
			editHdrIndent.setLastUpdatedDt(DateUtils.getcurrentdate());
			editHdrIndent.setLastUpdatedBy(userName);
			listGatePassDtls.addAll(servicegatepassdtl.getGatepassDtlList(null, gatePassId, null, null, null, "Active",
					"F"));
		}
		loadDtlList();
		comments = new MmsComments(vlTableForm, null, companyId, null, null, null, null, null, null, gatePassId, null);
		comments.loadsrch(true, null, null, null, null, null, null, null, null, gatePassId);
	}
	
	private void editDtls() {
		// hluserinputlayout.setVisible(true);
		if (tblGatepassDetails.getValue() != null) {
			GatepassDtlDM gatepassDtlDM = beanGatePassDtl.getItem(tblGatepassDetails.getValue()).getBean();
			if (gatepassDtlDM.getGoodstype() != null) {
				cbGoodsType.setValue(gatepassDtlDM.getGoodstype());
			}
			Long matId = gatepassDtlDM.getMaterialId();
			Collection<?> empColId = cbMaterial.getItemIds();
			for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMaterial.getItem(itemIdClient);
				// Get the actual bean and use the data
				MaterialDM matObj = (MaterialDM) itemclient.getBean();
				if (matId != null && matId.equals(matObj.getMaterialId())) {
					cbMaterial.setValue(itemIdClient);
				}
			}
			Long prodId = gatepassDtlDM.getProductid();
			Collection<?> prodIdCol = cbProduct.getItemIds();
			for (Iterator<?> iteratorclient = prodIdCol.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbProduct.getItem(itemIdClient);
				// Get the actual bean and use the data
				ProductDM matObj = (ProductDM) itemclient.getBean();
				if (prodId != null && prodId.equals(matObj.getProdid())) {
					cbProduct.setValue(itemIdClient);
				}
			}
			if (gatepassDtlDM.getGoodsuom() != null) {
				cbGoodsUom.setReadOnly(false);
				cbGoodsUom.setValue(gatepassDtlDM.getGoodsuom());
				cbGoodsUom.setReadOnly(true);
			}
			if (gatepassDtlDM.getGatepassquanty() != null) {
				tfGatePassQty.setValue(gatepassDtlDM.getGatepassquanty().toString());
			}
			if (gatepassDtlDM.getReturnqty() != null) {
				tfReturnQty.setValue(gatepassDtlDM.getReturnqty().toString());
			}
			if (gatepassDtlDM.getGoodsDesc() != null) {
				tagoodsdesc.setValue(gatepassDtlDM.getGoodsDesc());
			}
			if (gatepassDtlDM.getReturnremarks() != null) {
				taRemarks.setValue(gatepassDtlDM.getReturnremarks());
			}
			cbdtlstatus.setValue(gatepassDtlDM.getStatus());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean errorflag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((cbGatepasstype.getValue() == null)) {
			cbGatepasstype.setComponentError(new UserError(GERPErrorCodes.NULL_MMS_gatepassType));
			errorflag = true;
		} else {
			cbGatepasstype.setComponentError(null);
		}
		if (cbVendor.getValue() == null) {
			cbVendor.setComponentError(new UserError(GERPErrorCodes.NULL_VENDOR_NAME));
			errorflag = true;
		} else {
			cbVendor.setComponentError(null);
		}
		if (tblGatepassDetails.size() == 0) {
			cbGoodsType.setComponentError(new UserError(GERPErrorCodes.NULL_POMATERIAL_UOM));
			cbGoodsUom.setComponentError(new UserError(GERPErrorCodes.NULL_POMATERIAL_UOM));
			errorflag = true;
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + cbGatepasstype.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validategatepassdtl() {
		boolean errorflag = true;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((cbGoodsType.getValue() == null)) {
			cbGoodsType.setComponentError(new UserError(GERPErrorCodes.NULL_POMATERIAL_UOM));
			errorflag = false;
		} else {
			cbGoodsType.setComponentError(null);
		}
		if (cbGoodsUom.getValue() == null) {
			cbGoodsUom.setComponentError(new UserError(GERPErrorCodes.NULL_POMATERIAL_UOM));
			errorflag = false;
		} else {
			cbGoodsUom.setComponentError(null);
		}
		return errorflag;
	}
	
	private void gatepassDtlResetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Resetting the UI controls");
		cbGoodsType.setValue(null);
		cbGoodsType.setComponentError(null);
		cbMaterial.setComponentError(null);
		cbProduct.setComponentError(null);
		cbGoodsUom.setComponentError(null);
		tfGatePassQty.setValue("");
		cbGoodsUom.setReadOnly(false);
		cbGoodsUom.setValue(null);
		cbGoodsUom.setReadOnly(true);
		cbMaterial.setValue(null);
		cbProduct.setValue(null);
		tagoodsdesc.setValue("");
		cbdtlstatus.setValue(cbdtlstatus.getItemIds().iterator().next());
		taRemarks.setValue("");
		tfReturnQty.setValue("");
		btnAddDtl.setComponentError(null);
	}
	
	@Override
	protected void saveDetails() {
		try {
			GatepassHdrDM gatepasshdr = new GatepassHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				gatepasshdr = beanGatePassHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			gatepasshdr.setGatepassNo(tfGatePassNo.getValue());
			gatepasshdr.setBranchId(branchID);
			gatepasshdr.setCompanyId(companyId);
			gatepasshdr.setDcId((Long) (cbDC.getValue()));
			gatepasshdr.setGatepassType((String) cbGatepasstype.getValue());
			gatepasshdr.setModeOfTrans((String) cbModeTransport.getValue());
			gatepasshdr.setVendorId(Long.valueOf(((VendorDM) cbVendor.getValue()).getVendorId().toString()));
			gatepasshdr.setVendorName(tfVendorname.getValue());
			gatepasshdr.setPersonName(tfpersonname.getValue());
			gatepasshdr.setVendorDcno((String) cbVendorDCNo.getValue());
			gatepasshdr.setVendorAddress(tavendoraddres.getValue().toString());
			gatepasshdr.setLeadDate(dtlead.getValue());
			gatepasshdr.setGatepassDt(gatepassdt.getValue());
			gatepasshdr.setReturnDate(dfReturndate.getValue());
			gatepasshdr.setRemarks(taHdrremarks.getValue());
			gatepasshdr.setGatepassStatus((String) cbgatestatus.getValue());
			gatepasshdr.setLastUpdatedDt(DateUtils.getcurrentdate());
			gatepasshdr.setLastUpdatedBy(userName);
			serviceGatepass.saveorUpdateGatepassHdrDetails(gatepasshdr);
			gatePassId = gatepasshdr.getGatepassId();
			@SuppressWarnings("unchecked")
			Collection<GatepassDtlDM> colPlanDtls = ((Collection<GatepassDtlDM>) tblGatepassDetails.getVisibleItemIds());
			for (GatepassDtlDM saveDtl : (Collection<GatepassDtlDM>) colPlanDtls) {
				saveDtl.setGatepassid(Long.valueOf(gatepasshdr.getGatepassId()));
				servicegatepassdtl.saveorupdateDtlDetails(saveDtl);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyId, branchID, moduleId, "MM_GPNO").get(
							0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyId, branchID, moduleId, "MM_GPNO");
					}
				}
				catch (Exception e) {
				}
			}
			comments.savegatepass(gatepasshdr.getGatepassId(), gatepasshdr.getGatepassStatus());
			comments.resetfields();
			gatepassDtlResetFields();
			// resetFields();
			loadSrchRslt();
			loadDtlList();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveGatepassDetails() {
		validategatepassdtl();
		GatepassDtlDM dtlpass = new GatepassDtlDM();
		if (tblGatepassDetails.getValue() != null) {
			dtlpass = beanGatePassDtl.getItem(tblGatepassDetails.getValue()).getBean();
		}
		dtlpass.setGoodstype((String) cbGoodsType.getValue());
		if (cbMaterial.getValue() != null) {
			dtlpass.setMaterialId(((MaterialDM) cbMaterial.getValue()).getMaterialId());
			dtlpass.setMaterialname(((MaterialDM) cbMaterial.getValue()).getMaterialName());
		}
		if (cbProduct.getValue() != null) {
			dtlpass.setProductid(((ProductDM) cbProduct.getValue()).getProdid());
			dtlpass.setProductname(((ProductDM) cbProduct.getValue()).getProdname());
		}
		dtlpass.setGoodsDesc(tagoodsdesc.getValue().toString());
		if (tfGatePassQty.getValue().toString().trim().length() > 0) {
			dtlpass.setGatepassquanty(Long.valueOf(tfGatePassQty.getValue()));
		}
		if (tfReturnQty.getValue().trim().length() > 0) {
			dtlpass.setReturnqty(Long.valueOf(tfReturnQty.getValue()));
		}
		if (cbGoodsUom.getValue() != null) {
			cbGoodsUom.setReadOnly(false);
			dtlpass.setGoodsuom(cbGoodsUom.getValue().toString());
			cbGoodsUom.setReadOnly(true);
		}
		dtlpass.setReturnremarks(taRemarks.getValue());
		dtlpass.setStatus((String) cbdtlstatus.getValue());
		dtlpass.setLastupdatedt(DateUtils.getcurrentdate());
		dtlpass.setLastupdatedBy(userName);
		listGatePassDtls.add(dtlpass);
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
		tblGatepassDetails.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		cbGatepasstype.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		tfGatePassNo.setReadOnly(false);
		tfGatePassNo.setValue("");
		gatepassdt.setValue(null);
		cbGatepasstype.setValue(null);
		cbDC.setValue(null);
		cbVendor.setValue(null);
		cbVendor.setComponentError(null);
		tfVendorname.setValue("");
		tavendoraddres.setValue("");
		cbVendorDCNo.setValue(null);
		dtlead.setValue(null);
		dfReturndate.setValue(null);
		cbModeTransport.setValue(null);
		tfpersonname.setValue("");
		taHdrremarks.setValue("");
		cbGatepasstype.setComponentError(null);
		cbgatestatus.setValue(cbgatestatus.getItemIds().iterator().next());
		listGatePassDtls = new ArrayList<GatepassDtlDM>();
		tblGatepassDetails.removeAllItems();
	}
	
	private void deleteDetails() {
		GatepassDtlDM save = new GatepassDtlDM();
		if (tblGatepassDetails.getValue() != null) {
			save = beanGatePassDtl.getItem(tblGatepassDetails.getValue()).getBean();
			listGatePassDtls.remove(save);
			gatepassDtlResetFields();
			loadDtlList();
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
			System.out.println("gatePassId-->" + gatePassId);
			parameterMap.put("GPASSID", gatePassId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/gatepass"); // productlist is the name of my jasper
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
