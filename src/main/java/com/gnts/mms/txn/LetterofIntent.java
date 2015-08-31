package com.gnts.mms.txn;

import java.math.BigDecimal;
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
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.base.service.mst.VendorService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
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
import com.gnts.mms.domain.txn.LOIDetailsDM;
import com.gnts.mms.domain.txn.LOIHeaderDM;
import com.gnts.mms.domain.txn.MmsQuoteDtlDM;
import com.gnts.mms.domain.txn.MmsQuoteHdrDM;
import com.gnts.mms.service.txn.LOIDetailService;
import com.gnts.mms.service.txn.LOIHeaderService;
import com.gnts.mms.service.txn.MmsQuoteDtlService;
import com.gnts.mms.service.txn.MmsQuoteHdrService;
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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class LetterofIntent extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private LOIHeaderService serviceLOIHeader = (LOIHeaderService) SpringContextHelper.getBean("loiheader");
	private LOIDetailService serviceLOIDetail = (LOIDetailService) SpringContextHelper.getBean("loidetail");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private MmsQuoteDtlService serviceMmsQuoteDtlService = (MmsQuoteDtlService) SpringContextHelper
			.getBean("mmsquotedtl");
	private MmsQuoteHdrService serviceMmsQuoteHdr = (MmsQuoteHdrService) SpringContextHelper.getBean("mmsquotehdr");
	private List<LOIDetailsDM> indentDtlList = null;
	// form layout for input controls
	private FormLayout flIndentCol1, flIndentCol2, flIndentCol3, flIndentCol4, flIndentDtlCol1, flIndentDtlCol2,
			flIndentDtlCol3, flIndentDtlCol4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout hlIndentDtl = new VerticalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Work Order Details
	private Button btnAddDtl = new GERPButton("Add", "addbt", this);
	private ComboBox cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	// Dtl Status ComboBox
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.T_SMS_INVOICE_HDR,
			BASEConstants.INVOICE_STATUS);
	private TextField tfLOINumber, tfIntQty, tfSubject, tfUnitprice;
	private PopupDateField dfReferenceDate;
	private ComboBox cbQuotation, cbVendor, cbMatName, cbUom;
	private TextArea taRemarks, tfDtlRemarks;
	private Table tblLOIDetail;
	private BeanItemContainer<LOIHeaderDM> beanIndentHdrDM = null;
	private BeanItemContainer<LOIDetailsDM> beanIndentDtlDM = null;
	// local variables declaration
	private String taxSlapId;
	private Long companyid;
	private Long loiHdrId, moduleId, branchId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(LetterofIntent.class);
	
	// private Button btnConvertToPO=new GERPButton("Convert To PO","hostorybtn");
	// Constructor received the parameters from Login UI class
	public LetterofIntent() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Indent() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		// Initialization for work order Details user input components
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Indent UI");
		btnAddDtl.setStyleName("add");
		btnAddDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDtlDetails()) {
					saveindentDtlListDetails();
				}
			}
		});
		btndelete.setEnabled(false);
		tblLOIDetail = new GERPTable();
		tblLOIDetail.setWidth("90%");
		tblLOIDetail.setPageLength(6);
		tblLOIDetail.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblLOIDetail.isSelected(event.getItemId())) {
					tblLOIDetail.setImmediate(true);
					btnAddDtl.setCaption("Add");
					btnAddDtl.setStyleName("savebt");
					btndelete.setEnabled(false);
					resetLOIDetails();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtl.setCaption("Update");
					btnAddDtl.setStyleName("savebt");
					btndelete.setEnabled(true);
					cbMatName.setComponentError(null);
					editLOIDetails();
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
		// Indent No. TextField
		tfLOINumber = new GERPTextField("Ref. No");
		tfLOINumber.setMaxLength(40);
		tfIntQty = new TextField();
		tfIntQty.setValue("0");
		tfIntQty.setWidth("130");
		cbUom = new ComboBox();
		cbUom.setItemCaptionPropertyId("lookupname");
		cbUom.setWidth("50");
		cbUom.setHeight("18");
		cbUom.setReadOnly(true);
		loadMaterialUOMList();
		// Indent Type GERPComboBox
		cbQuotation = new GERPComboBox("Quotation");
		cbQuotation.setItemCaptionPropertyId("quoteRef");
		cbQuotation.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbQuotation.getValue() != null) {
					loadMaterialList();
				}
			}
		});
		loadQuoteNoList();
		tfSubject = new GERPTextField("Subject");
		tfUnitprice = new GERPTextField("Unit Price");
		tfUnitprice.setWidth("110");
		tfDtlRemarks = new TextArea("Remarks");
		tfDtlRemarks.setWidth("150px");
		tfDtlRemarks.setHeight("50px");
		// Indent Date PopupDateField
		dfReferenceDate = new GERPPopupDateField("Date");
		dfReferenceDate.setInputPrompt("Select Date");
		// Branch Name GERPComboBox
		cbVendor = new GERPComboBox("Vendor");
		cbVendor.setItemCaptionPropertyId("vendorName");
		loadVendorNameList();
		// Expected Date field
		taRemarks = new TextArea("Remarks");
		taRemarks.setWidth("110px");
		taRemarks.setHeight("50px");
		taRemarks.setNullRepresentation("");
		// Dtl Status ComboBox
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbHdrStatus.setWidth("90px");
		// Indent Detail
		// Material Name combobox
		cbMatName = new ComboBox("Material Name");
		cbMatName.setItemCaptionPropertyId("materialname");
		cbMatName.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbMatName.getValue() != null) {
					cbUom.setReadOnly(false);
					cbUom.setValue(((MmsQuoteDtlDM) cbMatName.getValue()).getMatuom());
					cbUom.setReadOnly(true);
					tfIntQty.setValue(((MmsQuoteDtlDM) cbMatName.getValue()).getQuoteqty().toString());
					tfUnitprice.setValue(((MmsQuoteDtlDM) cbMatName.getValue()).getUnitrate().toString());
				}
			}
		});
		cbMatName.setImmediate(true);
		// Hdr Combobox
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.T_SMS_INVOICE_HDR, BASEConstants.INVOICE_STATUS);
		// Indent No text field
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadLOIDetails();
		btnAddDtl.setStyleName("add");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flIndentCol1 = new FormLayout();
		flIndentCol2 = new FormLayout();
		flIndentCol4 = new FormLayout();
		flIndentCol1.addComponent(tfLOINumber);
		flIndentCol2.addComponent(cbQuotation);
		flIndentCol4.addComponent(cbHdrStatus);
		hlSearchLayout.addComponent(flIndentCol1);
		hlSearchLayout.addComponent(flIndentCol2);
		hlSearchLayout.addComponent(flIndentCol4);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flIndentCol1 = new FormLayout();
		flIndentCol2 = new FormLayout();
		flIndentCol3 = new FormLayout();
		flIndentCol4 = new FormLayout();
		flIndentCol1.addComponent(tfLOINumber);
		flIndentCol1.addComponent(dfReferenceDate);
		flIndentCol2.addComponent(cbQuotation);
		flIndentCol2.addComponent(cbVendor);
		flIndentCol3.setSpacing(true);
		flIndentCol3.setMargin(true);
		flIndentCol3.addComponent(taRemarks);
		taRemarks.setWidth("140");
		flIndentCol4.addComponent(tfSubject);
		flIndentCol4.addComponent(cbHdrStatus);
		cbHdrStatus.setWidth("130");
		HorizontalLayout hlTax = new HorizontalLayout();
		hlTax.addComponent(flIndentCol1);
		hlTax.addComponent(flIndentCol2);
		hlTax.addComponent(flIndentCol3);
		hlTax.addComponent(flIndentCol4);
		hlTax.setSpacing(true);
		hlTax.setMargin(true);
		// Adding TaxSlap components
		// Add components for User Input Layout
		flIndentDtlCol1 = new FormLayout();
		flIndentDtlCol2 = new FormLayout();
		flIndentDtlCol3 = new FormLayout();
		flIndentDtlCol4 = new FormLayout();
		flIndentDtlCol1.addComponent(cbMatName);
		HorizontalLayout hlQtyUom = new HorizontalLayout();
		hlQtyUom.addComponent(tfIntQty);
		hlQtyUom.addComponent(cbUom);
		hlQtyUom.setCaption("Qty");
		flIndentDtlCol1.addComponent(hlQtyUom);
		// flIndentDtlCol1.setComponentAlignment(hlQtyUom, Alignment.TOP_LEFT);
		flIndentDtlCol2.addComponent(tfUnitprice);
		flIndentDtlCol2.addComponent(cbDtlStatus);
		flIndentDtlCol3.addComponent(tfDtlRemarks);
		flIndentDtlCol4.addComponent(btnAddDtl);
		flIndentDtlCol4.addComponent(btndelete);
		flIndentDtlCol2.setMargin(true);
		flIndentDtlCol2.setSpacing(true);
		hlIndentDtl.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				addComponent(flIndentDtlCol1);
				addComponent(flIndentDtlCol2);
				addComponent(flIndentDtlCol3);
				addComponent(flIndentDtlCol4);
			}
		});
		hlIndentDtl.addComponent(tblLOIDetail);
		hlIndentDtl.setSpacing(true);
		hlIndentDtl.setMargin(true);
		tfLOINumber.setReadOnly(false);
		VerticalLayout vlIndentHdrAndDtl = new VerticalLayout();
		vlIndentHdrAndDtl = new VerticalLayout();
		vlIndentHdrAndDtl.addComponent(GERPPanelGenerator.createPanel(hlTax));
		vlIndentHdrAndDtl.addComponent(GERPPanelGenerator.createPanel(hlIndentDtl));
		vlIndentHdrAndDtl.setSpacing(true);
		vlIndentHdrAndDtl.setWidth("100%");
		hlUserInputLayout.addComponent(vlIndentHdrAndDtl);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	// Load for Indent Search Hdr
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<LOIHeaderDM> indentHdrList = new ArrayList<LOIHeaderDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfLOINumber.getValue() + ", " + cbHdrStatus.getValue());
		indentHdrList = serviceLOIHeader.getLOIHeaderDMList(null, null, null, null, null, null);
		recordCnt = indentHdrList.size();
		beanIndentHdrDM = new BeanItemContainer<LOIHeaderDM>(LOIHeaderDM.class);
		beanIndentHdrDM.addAll(indentHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Indent. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanIndentHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "loiHdrId", "loiNumber", "quoteNumber", "vendorName",
				"status", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "LOI Number", "Quote", "Vendor Name", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("loiHdrId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Load for Indent Search Dtl
	private void loadLOIDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			logger.info("Company ID : " + companyid + " | saveindentDtlListDetails User Name : " + username + " > "
					+ "Search Parameters are " + companyid + ", " + tfIntQty.getValue() + ", "
					+ (String) cbDtlStatus.getValue());
			beanIndentDtlDM = new BeanItemContainer<LOIDetailsDM>(LOIDetailsDM.class);
			beanIndentDtlDM.addAll(indentDtlList);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxslap. result set");
			tblLOIDetail.setContainerDataSource(beanIndentDtlDM);
			tblLOIDetail.setVisibleColumns(new Object[] { "materialName", "qty", "unitRate", "remarks", "status" });
			tblLOIDetail.setColumnHeaders(new String[] { "Material Name", "Qty", "Unit Rate", "Remarks", "Status" });
			tblLOIDetail.setColumnFooter("status", "No.of Records : " + indentDtlList.size());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Uom List
	private void loadMaterialUOMList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Material UOM Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
					"MM_UOM"));
			cbUom.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbQuotation.setValue(null);
		dfReferenceDate.setValue(new Date());
		tfLOINumber.setReadOnly(false);
		tfLOINumber.setValue("");
		taRemarks.setValue(null);
		cbVendor.setValue(branchId);
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		cbQuotation.setComponentError(null);
		dfReferenceDate.setComponentError(null);
		cbVendor.setComponentError(null);
		cbMatName.setComponentError(null);
		tfIntQty.setComponentError(null);
		indentDtlList = new ArrayList<LOIDetailsDM>();
		tblLOIDetail.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editHeaderDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			LOIHeaderDM loiHeaderDM = beanIndentHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			loiHdrId = loiHeaderDM.getLoiHdrId();
			tfLOINumber.setValue(loiHeaderDM.getLoiNumber());
			dfReferenceDate.setValue(loiHeaderDM.getRefDate());
			cbVendor.setValue(loiHeaderDM.getVendorId());
			Long quote = loiHeaderDM.getQuoteid();
			Collection<?> quoteids = cbQuotation.getItemIds();
			for (Iterator<?> iterator = quoteids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbQuotation.getItem(itemId);
				// Get the actual bean and use the data
				MmsQuoteHdrDM st = (MmsQuoteHdrDM) item.getBean();
				if (quote != null && quote.equals(st.getQuoteId())) {
					cbQuotation.setValue(itemId);
					break;
				} else {
					cbQuotation.setValue(null);
				}
			}
			taRemarks.setValue(loiHeaderDM.getRemarks());
			indentDtlList.addAll(serviceLOIDetail.getLOIDetailList(null, loiHdrId, null, null));
		}
		loadLOIDetails();
	}
	
	// Method to edit the values from table into fields to update process
	private void editLOIDetails() {
		hlUserInputLayout.setVisible(true);
		if (tblLOIDetail.getValue() != null) {
			LOIDetailsDM editDtl = beanIndentDtlDM.getItem(tblLOIDetail.getValue()).getBean();
			Long uom = editDtl.getMaterialId();
			Collection<?> uomid = cbMatName.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbMatName.getItem(itemId);
				// Get the actual bean and use the data
				MmsQuoteDtlDM st = (MmsQuoteDtlDM) item.getBean();
				if (uom != null && uom.equals(st.getMaterialid())) {
					cbMatName.setValue(itemId);
				}
			}
			if (editDtl.getQty() != null) {
				tfIntQty.setValue(editDtl.getQty().toString());
			}
			if (editDtl.getUnitRate() != null) {
				tfUnitprice.setValue(editDtl.getUnitRate().toString());
			}
			if (editDtl.getStatus() != null) {
				cbDtlStatus.setValue(editDtl.getStatus());
			}
			if (editDtl.getRemarks() != null) {
				tfDtlRemarks.setValue(editDtl.getRemarks());
			}
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
			tfLOINumber.setReadOnly(false);
		}
	}
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfLOINumber.setValue("");
		tfLOINumber.setReadOnly(false);
		cbQuotation.setValue(null);
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tfLOINumber.setReadOnly(false);
		cbQuotation.setRequired(true);
		cbVendor.setRequired(true);
		cbVendor.setValue(branchId);
		cbMatName.setRequired(true);
		tfIntQty.setValue("0");
		resetFields();
		resetLOIDetails();
		loadLOIDetails();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtl.setCaption("Add");
		tblLOIDetail.setVisible(true);
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Tax. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_WORKORDER_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", taxSlapId);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		cbQuotation.setComponentError(null);
		dfReferenceDate.setComponentError(null);
		cbVendor.setComponentError(null);
		cbMatName.setComponentError(null);
		tfIntQty.setComponentError(null);
		cbQuotation.setRequired(false);
		cbVendor.setRequired(false);
		cbMatName.setRequired(false);
		tfLOINumber.setReadOnly(false);
		resetLOIDetails();
		hlCmdBtnLayout.setVisible(true);
		tblLOIDetail.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbQuotation.setRequired(true);
		cbVendor.setRequired(true);
		cbMatName.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		tfLOINumber.setReadOnly(false);
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		resetFields();
		editHeaderDetails();
		editLOIDetails();
	}
	
	// reset the input values to IndentDtl
	private void resetLOIDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMatName.setValue(null);
		tfIntQty.setValue("0");
		cbUom.setReadOnly(false);
		cbUom.setValue(null);
		cbUom.setReadOnly(true);
		cbUom.setComponentError(null);
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		btnAddDtl.setCaption("Add");
	}
	
	// validation for Detail table
	private boolean validateDtlDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbMatName.getValue() == null || cbMatName.getValue().toString() == "[]") {
			cbMatName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			isValid = false;
		} else {
			cbMatName.setComponentError(null);
		}
		if (cbUom.getValue() == null) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			isValid = false;
		} else {
			cbUom.setComponentError(null);
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfIntQty.getValue());
			if (achievedQty < 0) {
				tfIntQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfIntQty.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATION));
			isValid = false;
		}
		if (tfIntQty.getValue().equals("0")) {
			tfIntQty.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRY_QTY));
			isValid = false;
		} else {
			tfIntQty.setComponentError(null);
			isValid = true;
		}
		return isValid;
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if ((tfLOINumber.getValue() == null) || tfLOINumber.getValue().trim().length() == 0) {
			tfLOINumber.setComponentError(new UserError(GERPErrorCodes.NULL_INDENT_NO));
			errorFlag = true;
		}
		if (cbQuotation.getValue() == null) {
			cbQuotation.setComponentError(new UserError(GERPErrorCodes.NULL_INDENT_TYPE));
			errorFlag = true;
		}
		if (cbVendor.getValue() == null) {
			cbVendor.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			errorFlag = true;
		}
		if (tblLOIDetail.size() == 0) {
			cbUom.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
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
			LOIHeaderDM loiHeaderDM = new LOIHeaderDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				loiHeaderDM = beanIndentHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			loiHeaderDM.setLoiNumber(tfLOINumber.getValue());
			loiHeaderDM.setQuoteid(((MmsQuoteHdrDM) cbQuotation.getValue()).getQuoteId());
			loiHeaderDM.setVendorId((Long) cbVendor.getValue());
			loiHeaderDM.setRefDate(dfReferenceDate.getValue());
			loiHeaderDM.setRemarks(taRemarks.getValue());
			loiHeaderDM.setSubject(tfSubject.getValue());
			loiHeaderDM.setStatus((String) cbHdrStatus.getValue());
			loiHeaderDM.setLastUpdatedBy(username);
			loiHeaderDM.setLastUpdatedDt(DateUtils.getcurrentdate());
			serviceLOIHeader.saveOrUpdateLOIHdr(loiHeaderDM);
			loiHdrId = loiHeaderDM.getLoiHdrId();
			@SuppressWarnings("unchecked")
			Collection<LOIDetailsDM> loiDetails = ((Collection<LOIDetailsDM>) tblLOIDetail.getVisibleItemIds());
			for (LOIDetailsDM loiDetailobj : (Collection<LOIDetailsDM>) loiDetails) {
				loiDetailobj.setLoiHeaderId(loiHeaderDM.getLoiHdrId());
				serviceLOIDetail.saveOrUpdateLOIHdr(loiDetailobj);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MMS_LOI");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "MMS_LOI");
					}
				}
			}
			loadSrchRslt();
			loadLOIDetails();
			// resetFields();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Save method for Detail table in temporary
	private void saveindentDtlListDetails() {
		try {
			int count = 0;
			for (LOIDetailsDM indentDtlDM : indentDtlList) {
				if (indentDtlDM.getMaterialId() == ((MmsQuoteDtlDM) cbMatName.getValue()).getMaterialid()) {
					count++;
					break;
				}
			}
			if (tblLOIDetail.getValue() != null) {
				count = 0;
			}
			if (count == 0) {
				LOIDetailsDM indentDtlObj = new LOIDetailsDM();
				if (tblLOIDetail.getValue() != null) {
					indentDtlObj = beanIndentDtlDM.getItem(tblLOIDetail.getValue()).getBean();
					indentDtlList.remove(indentDtlObj);
				}
				indentDtlObj.setMaterialId(((MmsQuoteDtlDM) cbMatName.getValue()).getMaterialid());
				indentDtlObj.setMaterialName(((MmsQuoteDtlDM) cbMatName.getValue()).getMaterialname());
				indentDtlObj.setQty(Long.valueOf(tfIntQty.getValue()));
				indentDtlObj.setUnitRate(new BigDecimal(tfUnitprice.getValue()));
				indentDtlObj.setRemarks(tfDtlRemarks.getValue());
				indentDtlObj.setStatus((String) cbDtlStatus.getValue());
				indentDtlObj.setLastUpdatedDt(DateUtils.getcurrentdate());
				indentDtlObj.setLastUpdatedBy(username);
				indentDtlList.add(indentDtlObj);
				loadLOIDetails();
				btnAddDtl.setCaption("Add");
				count = 0;
			} else {
				cbMatName.setComponentError(new UserError("Material Already Exist.."));
			}
			resetLOIDetails();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * loadBranchList()-->this function is used for load the branch name
	 */
	private void loadVendorNameList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading VendorNameList");
			BeanContainer<Long, VendorDM> beanVendor = new BeanContainer<Long, VendorDM>(VendorDM.class);
			beanVendor.setBeanIdProperty("vendorId");
			beanVendor.addAll(serviceVendor.getVendorList(null, null, companyid, null, null, null, null, null,
					"Active", null, "P"));
			cbVendor.setContainerDataSource(beanVendor);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadMaterialList() {
		try {
			BeanItemContainer<MmsQuoteDtlDM> beanpodtl = new BeanItemContainer<MmsQuoteDtlDM>(MmsQuoteDtlDM.class);
			beanpodtl.addAll(serviceMmsQuoteDtlService.getmmsquotedtllist(null,
					((MmsQuoteHdrDM) cbQuotation.getValue()).getQuoteId(), null, null, null));
			cbMatName.setContainerDataSource(beanpodtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadQuoteNoList() {
		try {
			BeanItemContainer<MmsQuoteHdrDM> beanQuote = new BeanItemContainer<MmsQuoteHdrDM>(MmsQuoteHdrDM.class);
			beanQuote.addAll(serviceMmsQuoteHdr.getMmsQuoteHdrList(companyid, null, null, null, null, null, null, "F"));
			cbQuotation.setContainerDataSource(beanQuote);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// delete row in temporary table
	private void deleteDetails() {
		LOIDetailsDM loiDetailsDM = new LOIDetailsDM();
		if (tblLOIDetail.getValue() != null) {
			loiDetailsDM = beanIndentDtlDM.getItem(tblLOIDetail.getValue()).getBean();
			indentDtlList.remove(loiDetailsDM);
			resetLOIDetails();
			loadLOIDetails();
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
			parameterMap.put("LOIID", loiHdrId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/loi"); // productlist is the name of my jasper
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
