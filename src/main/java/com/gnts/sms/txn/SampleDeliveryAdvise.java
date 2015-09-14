package com.gnts.sms.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.txn.ClientsContactsDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.crm.service.txn.ClientContactsService;
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
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsEnquiryDtlDM;
import com.gnts.sms.domain.txn.SmsSDADtlDM;
import com.gnts.sms.domain.txn.SmsSDAHdrDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsEnquiryDtlService;
import com.gnts.sms.service.txn.SmsSDADtlService;
import com.gnts.sms.service.txn.SmsSDAHdrService;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SampleDeliveryAdvise extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private ClientContactsService serviceClntContact = (ClientContactsService) SpringContextHelper
			.getBean("clientContact");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private SmsEnquiryDtlService serviceEnqDetails = (SmsEnquiryDtlService) SpringContextHelper
			.getBean("SmsEnquiryDtl");
	private SmsSDAHdrService serviceSDAHeader = (SmsSDAHdrService) SpringContextHelper.getBean("smsSDRHdr");
	private SmsSDADtlService serviceSDADetail = (SmsSDADtlService) SpringContextHelper.getBean("smsSDRDtl");
	private List<SmsSDADtlDM> listSDADetails = new ArrayList<SmsSDADtlDM>();
	// Initialize the logger
	private Logger logger = Logger.getLogger(SmsEnquiry.class);
	// User Input Fields for Sales Enquiry Header
	private TextField tfSDANumber;
	private PopupDateField dfSDADate;
	private ComboBox cbEnquiry, cbGatepassType;
	private ComboBox cbSDAStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<SmsSDAHdrDM> beanSDAHdr = null;
	// User Input Fields for Sales Enquiry Detail
	private ComboBox cbProduct, cbSDADtlStatus, cbWindTechPers, cbWindCommPerson;
	private TextField tfSDAQty;
	private GERPTextArea taClientAddres = new GERPTextArea("Client Address");
	private GERPTextField tfCustomField1 = new GERPTextField("Part Number");
	private GERPTextField tfCustomField2 = new GERPTextField("Drawing Number");
	private Table tblSDADetails = new GERPTable();
	private BeanItemContainer<SmsSDADtlDM> beanSDADetails = null;
	// User Input Components for Sales Enquire Details
	private Button btnDetailDelete = new GERPButton("Delete", "delete", this);
	private Button btnDetailAdd = new GERPButton("Add", "addbtn", this);
	// form layout for input controls Sales Enquiry Header
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private FormLayout flcol1, flcol2, flcol3, flcol4, flcol5;
	// form layout for input controls Sales Enquiry Deatil
	private FormLayout fldtl1, fldtl2, fldtl3, fldtl4, fldtl5;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls Sales Enquiry Header
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// Parent layout for all the input controls Sales Enquiry Detail
	private HorizontalLayout hlDtlCompnts = new HorizontalLayout();
	private VerticalLayout vlSDADetails = new VerticalLayout();
	// Parent layout for all the input controls Sales Enquiry Specification
	private HorizontalLayout hlSDADetails = new HorizontalLayout();
	// Parent layout for all the input controls Sms Comments
	private VerticalLayout vlTableForm = new VerticalLayout();
	// local variables declaration
	private Long sdaHeaderId;
	private String username;
	private Long companyid, moduleId;
	private int recordCnt = 0;
	private SmsComments comments;
	private String status;
	private Long branchId;
	
	// Constructor received the parameters from Login UI class
	public SampleDeliveryAdvise() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside SampleDeliveryAdvise() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting Sales SampleDeliveryAdvise UI");
		// Sales Enquiry Header Components Definition
		tfSDANumber = new GERPTextField("SDA Number");
		tfSDANumber.setWidth("150");
		tfSDANumber.setMaxLength(10);
		tfSDANumber.setReadOnly(false);
		cbEnquiry = new GERPComboBox("Enquiry No.");
		cbEnquiry.setItemCaptionPropertyId("enquiryNo");
		cbEnquiry.setRequired(true);
		cbEnquiry.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				loadProduct();
				loadclientCommCont();
				loadclienTecCont();
				if (cbEnquiry.getValue() != null) {
					try {
						Long clientid = serviceEnqHeader
								.getSmsEnqHdrList(companyid, (Long) cbEnquiry.getValue(), null, null, null, "F", null,
										null).get(0).getClientId();
						taClientAddres.setValue(serviceClients
								.getClientDetails(null, clientid, null, null, null, null, null, null, "Active", "F")
								.get(0).getClientAddress());
					}
					catch (Exception e) {
					}
				}
			}
		});
		loadEnquiryList();
		dfSDADate = new GERPPopupDateField("SDA Date");
		dfSDADate.setDateFormat("dd-MMM-yyyy");
		dfSDADate.setInputPrompt("Select Date");
		dfSDADate.setWidth("100px");
		cbSDAStatus.setWidth("130");
		// Returned or Not field defenition
		cbGatepassType = new GERPComboBox("Stock Type");
		cbGatepassType.addItem("Returnable");
		cbGatepassType.addItem("NonReturnable");
		cbGatepassType.setImmediate(true);
		// Sales Enquiry Detail Components Definition
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		cbProduct.setRequired(true);
		cbProduct.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					tfSDAQty.setValue("1");
					tfCustomField1.setValue(((SmsEnquiryDtlDM) cbProduct.getValue()).getCustomField1());
					tfCustomField2.setValue(((SmsEnquiryDtlDM) cbProduct.getValue()).getCustomField2());
				}
				catch (Exception e) {
				}
			}
		});
		tfSDAQty = new GERPTextField("Qty");
		tfSDAQty.setRequired(true);
		cbSDADtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbSDADtlStatus.setWidth("130");
		cbWindTechPers = new GERPComboBox("Tech.Person");
		cbWindTechPers.setWidth("130");
		cbWindCommPerson = new GERPComboBox("Commer.Person");
		cbWindCommPerson.setWidth("130");
		loadSDADetails(true);
		hlsearchlayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
		btnDetailAdd.setStyleName("add");
		btnDetailAdd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update Sales Detail
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validationForEnqDetails()) {
					saveSDADetails();
				}
			}
		});
		btnDetailDelete.setEnabled(false);
		// Click Listener for Enquire Detail Tale
		tblSDADetails.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblSDADetails.isSelected(event.getItemId())) {
					tblSDADetails.setImmediate(true);
					btnDetailAdd.setCaption("Add");
					btnDetailAdd.setStyleName("savebt");
					btnDetailDelete.setEnabled(false);
					resetSDADetails();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnDetailAdd.setCaption("Update");
					btnDetailAdd.setStyleName("savebt");
					btnDetailDelete.setEnabled(true);
					editSDADetail();
				}
			}
		});
		btnDetailDelete.addClickListener(new ClickListener() {
			// Click Listener for delete Enquiry Detail
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnDetailDelete == event.getButton()) {
					deleteSDADetail();
				}
			}
		});
	}
	
	private void deleteSDADetail() {
		SmsSDADtlDM save = new SmsSDADtlDM();
		if (tblSDADetails.getValue() != null) {
			save = beanSDADetails.getItem(tblSDADetails.getValue()).getBean();
			listSDADetails.remove(save);
			resetSDADetails();
			loadSDADetails(false);
			btnDetailDelete.setEnabled(false);
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlsearchlayout.removeAllComponents();
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol2.addComponent(tfSDANumber);
		flcol3.addComponent(cbSDAStatus);
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
		flcol1.addComponent(tfSDANumber);
		tfSDANumber.setReadOnly(true);
		flcol1.addComponent(cbEnquiry);
		flcol2.addComponent(cbWindTechPers);
		flcol2.addComponent(cbWindCommPerson);
		flcol3.addComponent(dfSDADate);
		flcol3.addComponent(cbSDAStatus);
		taClientAddres.setHeight("50");
		flcol4.addComponent(taClientAddres);
		hllayout.setMargin(true);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol3);
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
		fldtl1.addComponent(cbProduct);
		fldtl1.addComponent(tfSDAQty);
		fldtl2.addComponent(tfCustomField1);
		fldtl2.addComponent(tfCustomField2);
		fldtl3.addComponent(cbGatepassType);
		fldtl3.addComponent(cbSDADtlStatus);
		fldtl4.addComponent(btnDetailAdd);
		fldtl4.addComponent(btnDetailDelete);
		hlDtlCompnts.setMargin(true);
		hlDtlCompnts.setSpacing(true);
		hlDtlCompnts.addComponent(fldtl1);
		hlDtlCompnts.addComponent(fldtl2);
		hlDtlCompnts.addComponent(fldtl3);
		hlDtlCompnts.addComponent(fldtl4);
		hlDtlCompnts.addComponent(fldtl5);
		vlSDADetails.addComponent(GERPPanelGenerator.createPanel(hlDtlCompnts));
		vlSDADetails.addComponent(tblSDADetails);
		tblSDADetails.setWidth("1188px");
		tblSDADetails.setPageLength(6);
		loadSDADetails(true);
		vlSDADetails.setWidth("100%");
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		// Adding Sales Enquiry Specification components
		hlSDADetails = new HorizontalLayout();
		hlSDADetails.addComponent(vlSDADetails);
		hlSDADetails.addComponent(GERPPanelGenerator.createPanel(vlSDADetails));
		TabSheet dtlTab = new TabSheet();
		dtlTab.addTab(hlSDADetails, "Sample Delivery Advise Detail");
		dtlTab.addTab(vlTableForm, "Comments");
		vlSrchRsltContainer.addComponent(GERPPanelGenerator.createPanel(dtlTab));
	}
	
	// Load Sales Enquiry Header
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<SmsSDAHdrDM> list = new ArrayList<SmsSDAHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + null + "," + tfSDANumber.getValue() + ", " + (String) cbSDAStatus.getValue());
			list = serviceSDAHeader.getsmsSDRdetails(companyid, branchId, null, tfSDANumber.getValue(), null,
					(String) cbSDAStatus.getValue());
			recordCnt = list.size();
			beanSDAHdr = new BeanItemContainer<SmsSDAHdrDM>(SmsSDAHdrDM.class);
			beanSDAHdr.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the SMSENQUIRY. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanSDAHdr);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "sdaHdrId", "sdaNo", "enqNo", "clientName", "cpmPerson",
					"tecPerson", "sdaDate", "status", "lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "SDA Number", "Enquiry No.", "Client Name",
					"Commer. Person", "Tech. Person", "Date", "Status", "Last Updated date", "Last Updated by" });
			tblMstScrSrchRslt.setColumnAlignment("sdaHdrId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnWidth("cpmPerson", 90);
			tblMstScrSrchRslt.setColumnWidth("tecPerson", 90);
			tblMstScrSrchRslt.setColumnWidth("clientName", 120);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Sales Enquiry Dateil
	private void loadSDADetails(Boolean fromdb) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblSDADetails.removeAllItems();
		logger.info("Company ID : "
				+ companyid
				+ " | User Name : "
				+ username
				+ " > "
				+ "Search Parameters are "
				+ (String.valueOf(cbProduct.getValue()) + ", " + (String) tfSDAQty.getValue() + "," + (String) cbSDADtlStatus
						.getValue()));
		if (fromdb) {
			listSDADetails = serviceSDADetail.getsmsSDRdtldetails(null, null, sdaHeaderId, null);
		}
		int numRecordCount = listSDADetails.size();
		beanSDADetails = new BeanItemContainer<SmsSDADtlDM>(SmsSDADtlDM.class);
		beanSDADetails.addAll(listSDADetails);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the SMSENQUIRYDTL. result set");
		tblSDADetails.setContainerDataSource(beanSDADetails);
		tblSDADetails.setVisibleColumns(new Object[] { "prodname", "proddesc", "qty", "custFld1", "custFld2", "status",
				"lastUpdatedDate", "lastUpdatedBy" });
		tblSDADetails.setColumnHeaders(new String[] { "Product Name", "Description", "Qty", "Part Number",
				"Drawing Number", "Status", "Last Updated Date", "Last Updated By" });
		tblSDADetails.setColumnFooter("lastUpdatedBy", "No.of Records : " + numRecordCount);
	}
	
	// Load Client List
	private void loadEnquiryList() {
		try {
			BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
			beansmsenqHdr.setBeanIdProperty("enquiryId");
			beansmsenqHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
			cbEnquiry.setContainerDataSource(beansmsenqHdr);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Product List
	private void loadProduct() {
		try {
			BeanItemContainer<SmsEnquiryDtlDM> beanEnqDtl = new BeanItemContainer<SmsEnquiryDtlDM>(
					SmsEnquiryDtlDM.class);
			beanEnqDtl.addAll(serviceEnqDetails.getsmsenquirydtllist(null, (Long) cbEnquiry.getValue(), null, null,
					null, null));
			cbProduct.setContainerDataSource(beanEnqDtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editSDAHeader() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			SmsSDAHdrDM smsSDAHdrDM = beanSDAHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			sdaHeaderId = smsSDAHdrDM.getSdaHdrId();
			tfSDANumber.setReadOnly(false);
			tfSDANumber.setValue(smsSDAHdrDM.getSdaNo());
			tfSDANumber.setReadOnly(true);
			cbEnquiry.setValue(smsSDAHdrDM.getEnquiryId());
			dfSDADate.setValue(smsSDAHdrDM.getSdaDate1());
			cbSDAStatus.setValue(smsSDAHdrDM.getStatus());
			if (smsSDAHdrDM.getCpmPerson() != null) {
				cbWindCommPerson.setValue(smsSDAHdrDM.getCpmPerson());
			}
			if (smsSDAHdrDM.getTecPerson() != null) {
				cbWindTechPers.setValue(smsSDAHdrDM.getTecPerson());
			}
			if (smsSDAHdrDM.getClientAddress() != null) {
				taClientAddres.setValue(smsSDAHdrDM.getClientAddress());
			}
		}
		loadSDADetails(true);
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, sdaHeaderId, null, null,
				null, status);
		comments.loadsrch(true, null, null, null, null, null, null, null, sdaHeaderId, null, null, null, null);
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Detail
	private void editSDADetail() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlDtlCompnts.setVisible(true);
		if (tblSDADetails.getValue() != null) {
			SmsSDADtlDM smsSDADtlDM = beanSDADetails.getItem(tblSDADetails.getValue()).getBean();
			Long prodid = smsSDADtlDM.getProductId();
			Collection<?> prodids = cbProduct.getItemIds();
			for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
				// Get the actual bean and use the data
				SmsEnquiryDtlDM st = (SmsEnquiryDtlDM) item.getBean();
				if (prodid != null && prodid.equals(st.getProductid())) {
					cbProduct.setValue(itemId);
				}
			}
			if (smsSDADtlDM.getQty() != null) {
				tfSDAQty.setValue(smsSDADtlDM.getQty().toString());
			}
			tfCustomField1.setValue(smsSDADtlDM.getCustFld1());
			tfCustomField2.setValue(smsSDADtlDM.getCustFld2());
			cbSDADtlStatus.setValue(smsSDADtlDM.getStatus());
		}
	}
	
	// Reset Sales Enquiry Detail List
	private void resetSDADetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbProduct.setValue(null);
		tfSDAQty.setValue("0");
		tfCustomField1.setValue("");
		tfCustomField2.setValue("");
		cbSDADtlStatus.setValue(cbSDADtlStatus.getItemIds().iterator().next());
		cbProduct.setComponentError(null);
		tfSDAQty.setComponentError(null);
		tfSDAQty.setCaption("Enquiry Qty");
		btnDetailAdd.setCaption("Add");
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		try {
			validationForEnqDetails();
			SmsSDAHdrDM sdaHdrDM = new SmsSDAHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				sdaHdrDM = beanSDAHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			sdaHdrDM.setSdaNo(tfSDANumber.getValue());
			sdaHdrDM.setEnquiryId((Long) cbEnquiry.getValue());
			sdaHdrDM.setSdaDate(dfSDADate.getValue());
			sdaHdrDM.setStatus((String) cbSDAStatus.getValue());
			sdaHdrDM.setLastUpdatedBy(username);
			sdaHdrDM.setCompanyId(companyid);
			sdaHdrDM.setBranchId(branchId);
			sdaHdrDM.setLastUpdatedDate(DateUtils.getcurrentdate());
			if (taClientAddres.getValue() != null) {
				sdaHdrDM.setClientAddress(taClientAddres.getValue());
			}
			if (cbWindTechPers.getValue() != null) {
				sdaHdrDM.setTecPerson(cbWindTechPers.getValue().toString());
			}
			if (cbWindCommPerson.getValue() != null) {
				sdaHdrDM.setCpmPerson(cbWindCommPerson.getValue().toString());
			}
			serviceSDAHeader.saveorupdateSDR(sdaHdrDM);
			@SuppressWarnings("unchecked")
			Collection<SmsSDADtlDM> itemIds = (Collection<SmsSDADtlDM>) tblSDADetails.getVisibleItemIds();
			for (SmsSDADtlDM smsSDADtlDM : (Collection<SmsSDADtlDM>) itemIds) {
				smsSDADtlDM.setSdaHdrId(sdaHdrDM.getSdaHdrId());
				sdaHeaderId = sdaHdrDM.getSdaHdrId();
				serviceSDADetail.saveorupdateSDRDtl(smsSDADtlDM);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_SDANO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_SDANO");
						System.out.println("Serial no=>" + companyid + "," + moduleId + "," + branchId);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			comments.saveSalesEnqId(sdaHdrDM.getEnquiryId(), null);
			resetSDADetails();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// This function is used for save the Sales Enquiry details for temporary
	private void saveSDADetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			SmsSDADtlDM sdaDtlDM = new SmsSDADtlDM();
			if (tblSDADetails.getValue() != null) {
				sdaDtlDM = beanSDADetails.getItem(tblSDADetails.getValue()).getBean();
				listSDADetails.remove(sdaDtlDM);
			}
			sdaDtlDM.setProductId(((SmsEnquiryDtlDM) cbProduct.getValue()).getProductid());
			sdaDtlDM.setProdname(((SmsEnquiryDtlDM) cbProduct.getValue()).getProdname());
			sdaDtlDM.setProddesc(((SmsEnquiryDtlDM) cbProduct.getValue()).getCustproddesc());
			sdaDtlDM.setQty(Long.valueOf(tfSDAQty.getValue()));
			sdaDtlDM.setCustFld1(tfCustomField1.getValue());
			sdaDtlDM.setCustFld2(tfCustomField2.getValue());
			sdaDtlDM.setStatus((String) cbSDADtlStatus.getValue());
			sdaDtlDM.setLastUpdatedDate(DateUtils.getcurrentdate());
			sdaDtlDM.setLastUpdatedBy(username);
			listSDADetails.add(sdaDtlDM);
			loadSDADetails(false);
			resetSDADetails();
			btnDetailAdd.setCaption("Add");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbSDAStatus.setValue(null);
		tfSDANumber.setValue("");
		tfSDANumber.setReadOnly(false);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		tfSDANumber.setReadOnly(true);
		tfSDAQty.setValue("0");
		hllayout.removeAllComponents();
		hlDtlCompnts.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		btnDetailAdd.setCaption("Add");
		btnDetailAdd.setVisible(true);
		tblSDADetails.setVisible(true);
		listSDADetails = new ArrayList<SmsSDADtlDM>();
		loadSDADetails(false);
		resetSDADetails();
		resetFields();
		tfSDANumber.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_SDANO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfSDANumber.setValue(slnoObj.getKeyDesc());
				tfSDANumber.setReadOnly(true);
			} else {
				tfSDANumber.setReadOnly(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		tblSDADetails.setVisible(true);
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hllayout.removeAllComponents();
		hlDtlCompnts.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		tblSDADetails.setVisible(true);
		if (tfSDANumber.getValue() == null || tfSDANumber.getValue().trim().length() == 0) {
			tfSDANumber.setReadOnly(false);
		}
		assembleinputLayout();
		resetSDADetails();
		btnDetailAdd.setCaption("Add");
		btnDetailAdd.setVisible(true);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		resetFields();
		editSDAHeader();
		editSDADetail();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbEnquiry.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbEnquiry.getValue() == null) {
			cbEnquiry.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
			errorFlag = true;
		}
		if (dfSDADate.getValue() == null) {
			dfSDADate.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfSDADate.getValue());
			errorFlag = true;
		}
		cbProduct.setComponentError(null);
		if (tblSDADetails.size() == 0) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbEnquiry.getValue() + "," + "," + ","
				+ dfSDADate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validationForEnqDetails() {
		boolean isValid = true;
		if (tfSDAQty.getValue() == null || tfSDAQty.getValue().trim().length() == 0) {
			tfSDAQty.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRY_QTY));
			isValid = false;
		} else {
			tfSDAQty.setComponentError(null);
			isValid = true;
		}
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			isValid = false;
		} else {
			cbProduct.setComponentError(null);
		}
		return isValid;
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for enquiryId " + sdaHeaderId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(sdaHeaderId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hllayout1.removeAllComponents();
		tblMstScrSrchRslt.setValue(null);
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
		tfSDANumber.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfSDANumber.setReadOnly(false);
		tfSDANumber.setValue("");
		tfSDANumber.setComponentError(null);
		cbEnquiry.setComponentError(null);
		cbEnquiry.setValue(null);
		dfSDADate.setValue(null);
		listSDADetails = new ArrayList<SmsSDADtlDM>();
		tblSDADetails.removeAllItems();
		cbSDAStatus.setValue("Active");
		dfSDADate.setValue(new Date());
		cbWindCommPerson.setValue(null);
		cbWindTechPers.setValue(null);
		taClientAddres.setValue("");
		cbWindCommPerson.setContainerDataSource(null);
		cbWindTechPers.setContainerDataSource(null);
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
			tfSDANumber.setReadOnly(false);
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
			parameterMap.put("SDAID", sdaHeaderId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/sda"); // productlist is the name of my jasper
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
			Long clientId = serviceEnqHeader
					.getSmsEnqHdrList(companyid, (Long) cbEnquiry.getValue(), null, null, null, "P", null, null).get(0)
					.getClientId();
			BeanContainer<Long, ClientsContactsDM> beanclientcontact = new BeanContainer<Long, ClientsContactsDM>(
					ClientsContactsDM.class);
			beanclientcontact.setBeanIdProperty("contactName");
			beanclientcontact.addAll(serviceClntContact.getClientContactsDetails(companyid, null, clientId, null,
					"Active", "Technical Person"));
			cbWindTechPers.setContainerDataSource(beanclientcontact);
			cbWindTechPers.setItemCaptionPropertyId("contactName");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadclientCommCont() {
		try {
			Long clientId = serviceEnqHeader
					.getSmsEnqHdrList(companyid, (Long) cbEnquiry.getValue(), null, null, null, "P", null, null).get(0)
					.getClientId();
			BeanContainer<Long, ClientsContactsDM> beanclientcontact = new BeanContainer<Long, ClientsContactsDM>(
					ClientsContactsDM.class);
			beanclientcontact.setBeanIdProperty("contactName");
			beanclientcontact.addAll(serviceClntContact.getClientContactsDetails(companyid, null, clientId, null,
					"Active", "Contact Person"));
			cbWindCommPerson.setContainerDataSource(beanclientcontact);
			cbWindCommPerson.setItemCaptionPropertyId("contactName");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}