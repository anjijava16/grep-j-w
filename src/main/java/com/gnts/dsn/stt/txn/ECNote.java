package com.gnts.dsn.stt.txn;

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
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
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
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.txn.SmsComments;
import com.gnts.stt.dsn.domain.txn.ECNoteDM;
import com.gnts.stt.dsn.domain.txn.ECRequestDM;
import com.gnts.stt.dsn.service.txn.ECNoteService;
import com.gnts.stt.dsn.service.txn.ECRequestService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ECNote extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private ECNoteService serviceECNote = (ECNoteService) SpringContextHelper.getBean("ecNote");
	private ECRequestService serviceECRequest = (ECRequestService) SpringContextHelper.getBean("ecRequest");
	// Initialize the logger
	private Logger logger = Logger.getLogger(ECNote.class);
	// User Input Fields for EC Request
	private TextField tfECNNumber;
	private PopupDateField dfECNDate, dfECRDate;
	private ComboBox cbEnquiry;
	private ComboBox cbClient;
	private ComboBox cbProduct, cbECRequest;
	private TextField tfDrgNumber, tfPartNumber, tfDocRefNumber;
	private TextArea taChangeDetail, taChangeReason, taRemarks;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<ECNoteDM> beanECNote = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// Parent layout for all the input controls Sms Comments
	private VerticalLayout vlTableForm = new VerticalLayout();
	// local variables declaration
	private Long ecnid;
	private String username;
	private Long companyid, moduleId;
	private int recordCnt = 0;
	private SmsComments comments;
	private String status;
	private Long branchId;
	
	// Constructor received the parameters from Login UI class
	public ECNote() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside ECNote() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting ECNote UI");
		// EC Request Components Definition
		tfECNNumber = new GERPTextField("ECN Number");
		tfECNNumber.setWidth("150");
		tfECNNumber.setReadOnly(false);
		tfDrgNumber = new TextField("Drg. Number");
		tfDrgNumber.setWidth("150");
		tfPartNumber = new TextField("Part Number");
		tfPartNumber.setWidth("150");
		cbClient = new GERPComboBox("Client Name");
		cbClient.setItemCaptionPropertyId("clientName");
		cbClient.setRequired(true);
		taChangeDetail = new TextArea("Change Detail");
		taChangeDetail.setWidth("984");
		taChangeReason = new TextArea("Reason of Change");
		taChangeReason.setWidth("984");
		taRemarks = new TextArea("Modified part/Sub assy Detail");
		taRemarks.setWidth("984");
		cbEnquiry = new GERPComboBox("Enquiry No.");
		cbEnquiry.setItemCaptionPropertyId("enquiryNo");
		cbEnquiry.setImmediate(true);
		cbEnquiry.setNullSelectionAllowed(false);
		cbEnquiry.setWidth("150");
		cbEnquiry.setRequired(true);
		cbECRequest = new GERPComboBox("ECR No.");
		cbECRequest.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbECRequest.getValue() != null) {
					ECRequestDM ecRequestDM = (ECRequestDM) cbECRequest.getValue();
					dfECRDate.setValue(ecRequestDM.getEcrDate());
					tfDrgNumber.setValue(ecRequestDM.getDrgNumber());
					tfPartNumber.setValue(ecRequestDM.getPartNumber());
					taChangeDetail.setValue(ecRequestDM.getChangeDetail());
					taChangeReason.setValue(ecRequestDM.getChangeReason());
					try {
						loadEnquiryList(ecRequestDM.getEnquiryId());
					}
					catch (Exception e) {
					}
					try {
						loadSmsClientList(ecRequestDM.getClientId());
					}
					catch (Exception e) {
					}
					try {
						loadProduct(ecRequestDM.getProductId());
					}
					catch (Exception e) {
					}
				}
			}
		});
		cbECRequest.setItemCaptionPropertyId("ecrNumber");
		loadECRequest();
		dfECNDate = new GERPPopupDateField("ECN Date");
		dfECNDate.setDateFormat("dd-MMM-yyyy");
		dfECNDate.setInputPrompt("Select Date");
		dfECNDate.setWidth("130px");
		dfECRDate = new GERPPopupDateField("ECR Date");
		dfECRDate.setDateFormat("dd-MMM-yyyy");
		dfECRDate.setInputPrompt("Select Date");
		dfECRDate.setWidth("130px");
		cbStatus.setWidth("130");
		tfDocRefNumber = new GERPTextField("Document Ref No.");
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		cbProduct.setWidth("150");
		cbProduct.setRequired(true);
		cbProduct.setImmediate(true);
		hlsearchlayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlsearchlayout.removeAllComponents();
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol2.addComponent(tfECNNumber);
		flcol3.addComponent(cbStatus);
		hlsearchlayout.addComponent(flcol1);
		hlsearchlayout.addComponent(flcol2);
		hlsearchlayout.addComponent(flcol3);
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setSizeUndefined();
	}
	
	private void assembleinputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		tfECNNumber.setReadOnly(true);
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol1.addComponent(tfECNNumber);
		flcol1.addComponent(dfECNDate);
		flcol1.addComponent(cbECRequest);
		flcol2.addComponent(dfECRDate);
		flcol2.addComponent(cbEnquiry);
		flcol2.addComponent(cbClient);
		flcol3.addComponent(cbProduct);
		flcol3.addComponent(tfDrgNumber);
		flcol3.addComponent(tfPartNumber);
		flcol4.addComponent(tfDocRefNumber);
		flcol4.addComponent(cbStatus);
		hllayout.setMargin(true);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol4);
		hllayout.setMargin(true);
		hllayout.setSpacing(true);
		hlUserIPContainer.addComponent(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				VerticalLayout vlHeader = new VerticalLayout();
				vlHeader.setSpacing(true);
				vlHeader.setMargin(true);
				vlHeader.addComponent(hllayout);
				vlHeader.addComponent(taChangeDetail);
				vlHeader.addComponent(taChangeReason);
				vlHeader.addComponent(taRemarks);
				addComponent(GERPPanelGenerator.createPanel(vlHeader));
			}
		});
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	// Load EC Request
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<ECNoteDM> listECNote = new ArrayList<ECNoteDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + null + "," + tfECNNumber.getValue() + ", " + (String) cbStatus.getValue());
			listECNote = serviceECNote.getECNoteList(companyid, branchId, null, tfECNNumber.getValue(), null, null,
					(String) cbStatus.getValue());
			recordCnt = listECNote.size();
			beanECNote = new BeanItemContainer<ECNoteDM>(ECNoteDM.class);
			beanECNote.addAll(listECNote);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the ECReq. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanECNote);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "ecnid", "ecnNumber", "ecnDate", "drgNumber", "status",
					"lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "ECN Number", "Date", "Drg. Number", "Status",
					"Last Updated date", "Last Updated by" });
			tblMstScrSrchRslt.setColumnAlignment("ecnid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Enquiry List
	private void loadEnquiryList(Long enquiryId) {
		try {
			BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
			beansmsenqHdr.setBeanIdProperty("enquiryId");
			beansmsenqHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, enquiryId, null, null, null, "P", null,
					null));
			cbEnquiry.setContainerDataSource(beansmsenqHdr);
			cbEnquiry.setValue(cbEnquiry.getItemIds().iterator().next());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Product List
	private void loadProduct(Long prodid) {
		try {
			BeanContainer<Long, ProductDM> beanProduct = new BeanContainer<Long, ProductDM>(ProductDM.class);
			beanProduct.setBeanIdProperty("prodid");
			beanProduct.addAll(serviceProduct.getProductList(companyid, prodid, null, null, "Active", null, null, "P"));
			cbProduct.setContainerDataSource(beanProduct);
			cbProduct.setValue(cbProduct.getItemIds().iterator().next());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Client List
	private void loadSmsClientList(Long clientid) {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading client Search...");
			BeanContainer<Long, ClientDM> beanclientDM = new BeanContainer<Long, ClientDM>(ClientDM.class);
			beanclientDM.setBeanIdProperty("clientId");
			beanclientDM.addAll(serviceClients.getClientDetails(companyid, clientid, null,null, null, null, null, null,
					null, "Active", "P"));
			cbClient.setContainerDataSource(beanclientDM);
			cbClient.setValue(cbClient.getItemIds().iterator().next());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadECRequest() {
		try {
			BeanItemContainer<ECRequestDM> beanECReq = new BeanItemContainer<ECRequestDM>(ECRequestDM.class);
			beanECReq.addAll(serviceECRequest.getECRequestList(companyid, branchId, null, null, null, null));
			cbECRequest.setContainerDataSource(beanECReq);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editECNote() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			ECNoteDM ecNoteDM = beanECNote.getItem(tblMstScrSrchRslt.getValue()).getBean();
			ecnid = ecNoteDM.getEcnid();
			tfECNNumber.setReadOnly(false);
			tfECNNumber.setValue(ecNoteDM.getEcnNumber());
			tfECNNumber.setReadOnly(true);
			Long ecrid = ecNoteDM.getEcrid();
			Collection<?> ecrids = cbECRequest.getItemIds();
			for (Iterator<?> iterator = ecrids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbECRequest.getItem(itemId);
				// Get the actual bean and use the data
				ECRequestDM st = (ECRequestDM) item.getBean();
				if (ecrid != null && ecrid.equals(st.getEcrid())) {
					cbECRequest.setValue(itemId);
				}
			}
			cbEnquiry.setValue(ecNoteDM.getEnquiryId());
			cbProduct.setValue(ecNoteDM.getProductId());
			cbClient.setValue(ecNoteDM.getClientId());
			dfECNDate.setValue(ecNoteDM.getEcnDate());
			if (ecNoteDM.getChangeDetail() != null) {
				taChangeDetail.setValue(ecNoteDM.getChangeDetail());
			}
			if (ecNoteDM.getChangeReason() != null) {
				taChangeReason.setValue(ecNoteDM.getChangeReason());
			}
			if (ecNoteDM.getRemarks() != null) {
				taRemarks.setValue(ecNoteDM.getRemarks());
			}
			tfDocRefNumber.setValue(ecNoteDM.getDocRefNumber());
			tfPartNumber.setValue(ecNoteDM.getPartNumber());
			tfDrgNumber.setValue(ecNoteDM.getDrgNumber());
			cbStatus.setValue(ecNoteDM.getStatus());
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, ecnid, null, null, null,
				status,null);
		comments.loadsrch(true, null, null, null, null, null, null, null, ecnid, null, null, null, null,null);
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		ECNoteDM ecNoteDM = new ECNoteDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			ecNoteDM = beanECNote.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if (cbECRequest.getValue() != null) {
			ecNoteDM.setEcrid(((ECRequestDM) cbECRequest.getValue()).getEcrid());
		}
		ecNoteDM.setEcnNumber(tfECNNumber.getValue());
		ecNoteDM.setEnquiryId((Long) cbEnquiry.getValue());
		ecNoteDM.setDrgNumber(tfDrgNumber.getValue());
		ecNoteDM.setPartNumber(tfPartNumber.getValue());
		ecNoteDM.setProductId((Long) cbProduct.getValue());
		ecNoteDM.setClientId((Long) cbClient.getValue());
		ecNoteDM.setEcnDate(dfECNDate.getValue());
		ecNoteDM.setChangeDetail(taChangeDetail.getValue());
		ecNoteDM.setChangeReason(taChangeReason.getValue());
		ecNoteDM.setRemarks(taRemarks.getValue());
		ecNoteDM.setDocRefNumber(tfDocRefNumber.getValue());
		ecNoteDM.setStatus((String) cbStatus.getValue());
		ecNoteDM.setLastUpdatedBy(username);
		ecNoteDM.setCompanyId(companyid);
		ecNoteDM.setBranchId(branchId);
		ecNoteDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceECNote.saveOrUpdateECNote(ecNoteDM);
		ecnid = ecNoteDM.getEcnid();
		if (tblMstScrSrchRslt.getValue() == null) {
			try {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "STT_ECNNO").get(0);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "STT_ECNNO");
					System.out.println("Serial no=>" + companyid + "," + moduleId + "," + branchId);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		comments.saveSalesEnqId(ecNoteDM.getEnquiryId(), null);
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		tfECNNumber.setValue("");
		tfECNNumber.setReadOnly(false);
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
		tfECNNumber.setReadOnly(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		tfECNNumber.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "STT_ECNNO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfECNNumber.setValue(slnoObj.getKeyDesc());
				tfECNNumber.setReadOnly(true);
			} else {
				tfECNNumber.setReadOnly(false);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null,null);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		resetFields();
		editECNote();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbEnquiry.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbEnquiry.getValue() == null) {
			cbEnquiry.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorFlag = true;
		}
		if (cbClient.getValue() == null) {
			cbClient.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
			errorFlag = true;
		}
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		if ((cbECRequest.getValue() == null)) {
			cbECRequest.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbEnquiry.getValue() + "," + "," + ","
				+ dfECNDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for enquiryId " + ecnid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(ecnid));
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
		tfECNNumber.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfECNNumber.setReadOnly(false);
		tfECNNumber.setValue("");
		tfECNNumber.setComponentError(null);
		cbEnquiry.setComponentError(null);
		cbECRequest.setValue(null);
		cbEnquiry.setValue(null);
		cbProduct.setValue(null);
		cbClient.setValue(null);
		dfECRDate.setValue(null);
		taChangeDetail.setValue("");
		taChangeReason.setValue("");
		tfDrgNumber.setValue("");
		tfPartNumber.setValue("");
		tfDocRefNumber.setValue("");
		taRemarks.setValue("");
		cbStatus.setValue(null);
		dfECNDate.setValue(new Date());
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
			tfECNNumber.setReadOnly(false);
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
			parameterMap.put("ECNID", ecnid);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/ecn"); // sda is the name of my jasper
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