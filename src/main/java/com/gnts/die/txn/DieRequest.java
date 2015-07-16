package com.gnts.die.txn;

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
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
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
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsEnquiryDtlService;
import com.gnts.stt.dsn.domain.txn.DieRequestDM;
import com.gnts.stt.dsn.service.txn.DieRequestService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author soundar
 * 
 */
public class DieRequest extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private DieRequestService serviceDieRequest = (DieRequestService) SpringContextHelper.getBean("dieRequest");
	private SmsEnquiryDtlService serviceEnqDetail = (SmsEnquiryDtlService) SpringContextHelper.getBean("SmsEnquiryDtl");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// Initialize the logger
	private Logger logger = Logger.getLogger(DieRequest.class);
	// User Input Fields for EC Request
	private PopupDateField dfRefDate, dfCompletionDate;
	private GERPComboBox cbEnquiry, cbProduct, cbNewDie;
	private GERPTextField tfNoofDie, tfCustomerCode, tfDrawingNumber, tfDieRefNumber;
	private GERPTextArea taChangeNote;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<DieRequestDM> beanDieRequest = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// local variables declaration
	private Long dieRequestId;
	private String username;
	private Long companyid, moduleId, branchId;
	private int recordCnt = 0;
	// for die section
	private GERPComboBox cbRegisterby, cbReceivedby;
	private GERPTextArea taTrailComments, taCmtsRectified;
	// for Mold trail request
	private GERPTextField tfMTRefNumber;
	private GERPPopupDateField dfMTRefDate;
	private GERPComboBox cbMTInput;
	private GERPTextField tfMTDescription;
	private GERPTable tblTrailRequest = new GERPTable();
	// Die completion report
	private GERPTextField tfDCRefNumber;
	private GERPComboBox cbFromDept, cbToDept;
	private GERPPopupDateField dfDCRefDate;
	private GERPComboBox cbDCDescription, cbDCResult;
	private GERPTextField taDCRemarks;
	private GERPTable tblDieCompletion = new GERPTable();
	
	// Constructor received the parameters from Login UI class
	public DieRequest() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside DieRequest() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting DieRequest UI");
		// EC Request Components Definition
		tfNoofDie = new GERPTextField("No of Die");
		tfDieRefNumber = new GERPTextField("Ref. Number");
		tfCustomerCode = new GERPTextField("Customer Code");
		tfDrawingNumber = new GERPTextField("Drawing No.");
		tfDrawingNumber.setEnabled(false);
		cbNewDie = new GERPComboBox("New Die ?");
		cbNewDie.addItems("Yes", "No");
		taChangeNote = new GERPTextArea("Change Note");
		taChangeNote.setWidth("984");
		cbEnquiry = new GERPComboBox("Enquiry No.");
		cbEnquiry.setItemCaptionPropertyId("enquiryNo");
		cbEnquiry.setRequired(true);
		loadEnquiryList();
		cbEnquiry.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				loadProductList();
			}
		});
		dfRefDate = new GERPPopupDateField("Req. Date");
		dfCompletionDate = new GERPPopupDateField("Completion Date");
		cbStatus.setWidth("150");
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		cbProduct.setRequired(true);
		cbProduct.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbProduct.getValue() != null) {
					SmsEnquiryDtlDM smsEnquiryDtlDM = (SmsEnquiryDtlDM) cbProduct.getValue();
					tfCustomerCode.setValue(smsEnquiryDtlDM.getCustprodcode());
					tfDrawingNumber.setValue(smsEnquiryDtlDM.getCustomField2());
				}
			}
		});
		// for die section
		cbRegisterby = new GERPComboBox("Registered by");
		cbReceivedby = new GERPComboBox("Received by");
		taTrailComments = new GERPTextArea("Trail Performance & Comments(by Roto)");
		taTrailComments.setWidth("984");
		taCmtsRectified = new GERPTextArea("Comments Rectified");
		taCmtsRectified.setWidth("984");
		// for mold section
		tfMTRefNumber = new GERPTextField("Ref. Number");
		dfMTRefDate = new GERPPopupDateField("Date");
		cbMTInput = new GERPComboBox("Detail of Input");
		loadInputTypes();
		cbMTInput.setWidth("250");
		tfMTDescription = new GERPTextField("Decription");
		tfMTDescription.setWidth("350");
		tblTrailRequest.setPageLength(12);
		// for die completion
		tfDCRefNumber = new GERPTextField("Report Number");
		dfDCRefDate = new GERPPopupDateField("Date");
		cbFromDept = new GERPComboBox("From ");
		cbToDept = new GERPComboBox("To ");
		cbDCDescription = new GERPComboBox("Description");
		loadDCDescriptions();
		cbDCResult = new GERPComboBox("Result");
		loadDCResults();
		taDCRemarks = new GERPTextField("Remarks");
		taDCRemarks.setWidth("300");
		tblDieCompletion.setPageLength(12);
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
		flcol1 = new FormLayout(cbEnquiry);
		flcol2 = new FormLayout(cbStatus);
		hlsearchlayout.addComponent(flcol1);
		hlsearchlayout.addComponent(flcol2);
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setSizeUndefined();
	}
	
	private void assembleinputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		flcol1 = new FormLayout(dfRefDate, tfDieRefNumber, cbEnquiry);
		flcol2 = new FormLayout(cbProduct, tfNoofDie, cbNewDie);
		flcol3 = new FormLayout(dfCompletionDate, tfCustomerCode, tfDrawingNumber);
		flcol4 = new FormLayout(cbStatus);
		hllayout.setMargin(true);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol4);
		hllayout.setMargin(true);
		hllayout.setSpacing(true);
		TabSheet tbDieRequest = new TabSheet();
		VerticalLayout vlHeader = new VerticalLayout(hllayout, taChangeNote);
		vlHeader.setSpacing(true);
		vlHeader.setMargin(true);
		tbDieRequest.addTab(GERPPanelGenerator.createPanel(vlHeader), "Die Request");
		// for die section
		VerticalLayout vlDieSection = new VerticalLayout();
		vlDieSection.setSpacing(true);
		vlDieSection.setMargin(true);
		vlDieSection.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(new FormLayout(cbRegisterby));
				addComponent(new FormLayout(cbReceivedby));
			}
		});
		vlDieSection.addComponent(taTrailComments);
		vlDieSection.addComponent(taCmtsRectified);
		tbDieRequest.addTab(vlDieSection, "Die Section");
		// for mold trial request
		VerticalLayout vlMoldTrialRequest = new VerticalLayout();
		vlMoldTrialRequest.addComponent(GERPPanelGenerator.createPanel(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				setMargin(true);
				addComponent(new FormLayout(tfMTRefNumber));
				addComponent(new FormLayout(dfMTRefDate));
			}
		}));
		vlMoldTrialRequest.addComponent(GERPPanelGenerator.createPanel(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				setMargin(true);
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(new FormLayout(cbMTInput));
						addComponent(new FormLayout(tfMTDescription));
					}
				});
				addComponent(tblTrailRequest);
			}
		}));
		tbDieRequest.addTab(vlMoldTrialRequest, "Mold Trial Request");
		VerticalLayout vlDieCompletion = new VerticalLayout();
		vlDieCompletion.addComponent(GERPPanelGenerator.createPanel(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				setMargin(true);
				addComponent(new FormLayout(tfDCRefNumber));
				addComponent(new FormLayout(dfDCRefDate));
				addComponent(new FormLayout(cbFromDept));
				addComponent(new FormLayout(cbToDept));
			}
		}));
		vlDieCompletion.addComponent(GERPPanelGenerator.createPanel(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				setMargin(true);
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(new FormLayout(cbDCDescription));
						addComponent(new FormLayout(cbDCResult));
						addComponent(new FormLayout(taDCRemarks));
					}
				});
				addComponent(tblDieCompletion);
			}
		}));
		tbDieRequest.addTab(vlDieCompletion, "Die Completion Report");
		tbDieRequest.addTab(new Label(), "Bill of Matrial");
		hlUserIPContainer.addComponent(tbDieRequest);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		// for disable tabs
		if (UI.getCurrent().getSession().getAttribute("IS_DIE_ENQ") == null
				|| (Boolean) UI.getCurrent().getSession().getAttribute("IS_DIE_ENQ")) {
			// vlDieSection.setEnabled(false);
			// vlMoldTrialRequest.setEnabled(false);
		}
	}
	
	// Load EC Request
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<DieRequestDM> list = new ArrayList<DieRequestDM>();
		list = serviceDieRequest.getDieRequestList(null, null, null, null, null);
		recordCnt = list.size();
		beanDieRequest = new BeanItemContainer<DieRequestDM>(DieRequestDM.class);
		beanDieRequest.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the DieReq. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanDieRequest);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "dieReqId", "enquiryNo", "refDate", "productName",
				"noOfDie", "status", "lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Enquiry", "Date", "Product", "No of Die",
				"Status", "Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("dieReqId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Load Enquiry List
	private void loadEnquiryList() {
		BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("enquiryId");
		beansmsenqHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
		cbEnquiry.setContainerDataSource(beansmsenqHdr);
	}
	
	private void loadInputTypes() {
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"DIE_MTR_INPUT");
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbMTInput.setContainerDataSource(beanCompanyLookUp);
	}
	
	private void loadDCDescriptions() {
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(serviceCompanyLookup
				.getCompanyLookUpByLookUp(companyid, null, "Active", "DIE_DC_DESC"));
		cbDCDescription.setContainerDataSource(beanCompanyLookUp);
	}
	
	private void loadDCResults() {
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"DIE_DC_RESULT"));
		cbDCResult.setContainerDataSource(beanCompanyLookUp);
	}
	
	private void loadProductList() {
		try {
			BeanItemContainer<SmsEnquiryDtlDM> beanPlnDtl = new BeanItemContainer<SmsEnquiryDtlDM>(
					SmsEnquiryDtlDM.class);
			beanPlnDtl.addAll(serviceEnqDetail.getsmsenquirydtllist(null, (Long) cbEnquiry.getValue(), null, null,
					null, null));
			cbProduct.setContainerDataSource(beanPlnDtl);
		}
		catch (Exception e) {
		}
	}
	
	// Method to edit the values from table into fields to update process for Die Request Header
	private void editDieRequest() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			DieRequestDM dieRequestDM = beanDieRequest.getItem(tblMstScrSrchRslt.getValue()).getBean();
			dieRequestId = dieRequestDM.getDieReqId();
			dfRefDate.setValue(dieRequestDM.getRefDate1());
			tfDieRefNumber.setReadOnly(false);
			tfDieRefNumber.setValue(dieRequestDM.getDieRefNumber());
			tfDieRefNumber.setReadOnly(true);
			cbNewDie.setValue(dieRequestDM.getNewDie());
			dfCompletionDate.setValue(dieRequestDM.getPlanCompleteDate());
			cbEnquiry.setValue(dieRequestDM.getEnquiryId());
			Long prodid = dieRequestDM.getProductId();
			Collection<?> prodids = cbProduct.getItemIds();
			for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
				// Get the actual bean and use the data
				SmsEnquiryDtlDM st = (SmsEnquiryDtlDM) item.getBean();
				if (prodid != null && prodid.equals(st.getProductid())) {
					cbProduct.setValue(itemId);
					break;
				} else {
					cbProduct.setValue(null);
				}
			}
			if (dieRequestDM.getNoOfDie() != null) {
				tfNoofDie.setValue(dieRequestDM.getNoOfDie().toString());
			}
			tfDrawingNumber.setValue(dieRequestDM.getDieRefNumber());
			taChangeNote.setValue(dieRequestDM.getChangeNote());
			cbStatus.setValue(dieRequestDM.getStatus());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		DieRequestDM dieRequestDM = new DieRequestDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			dieRequestDM = beanDieRequest.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		dieRequestDM.setRefDate(dfRefDate.getValue());
		dieRequestDM.setDieRefNumber(tfDieRefNumber.getValue());
		dieRequestDM.setNewDie((String) cbNewDie.getValue());
		dieRequestDM.setPlanCompleteDate(dfCompletionDate.getValue());
		dieRequestDM.setNoOfDie(Long.valueOf(tfNoofDie.getValue()));
		dieRequestDM.setEnquiryId((Long) cbEnquiry.getValue());
		dieRequestDM.setProductId(((SmsEnquiryDtlDM) cbProduct.getValue()).getProductid());
		dieRequestDM.setChangeNote(taChangeNote.getValue());
		dieRequestDM.setStatus((String) cbStatus.getValue());
		dieRequestDM.setLastUpdatedBy(username);
		dieRequestDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceDieRequest.saveOrUpdateDetails(dieRequestDM);
		dieRequestId = dieRequestDM.getDieReqId();
		if (tblMstScrSrchRslt.getValue() == null) {
			try {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "DIE_REQ_NO")
						.get(0);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "DIE_REQ_NO");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		tfNoofDie.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		tfDieRefNumber.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "DIE_REQ_NO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfDieRefNumber.setValue(slnoObj.getKeyDesc());
				tfDieRefNumber.setReadOnly(true);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
		editDieRequest();
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
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbEnquiry.getValue() + "," + "," + ","
				+ dfRefDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for enquiryId " + dieRequestId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(dieRequestId));
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
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfNoofDie.setReadOnly(false);
		tfNoofDie.setValue("");
		tfNoofDie.setComponentError(null);
		cbEnquiry.setComponentError(null);
		cbEnquiry.setValue(null);
		cbProduct.setValue(null);
		dfCompletionDate.setValue(null);
		tfCustomerCode.setValue("");
		tfDrawingNumber.setValue("");
		taChangeNote.setValue("");
		cbStatus.setValue(null);
		dfRefDate.setValue(new Date());
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
			tfNoofDie.setReadOnly(false);
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
			parameterMap.put("ECNID", dieRequestId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/ecn"); // sda is the name of my jasper
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