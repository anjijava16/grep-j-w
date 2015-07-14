package com.gnts.die.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.SlnoGenService;
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
import com.gnts.stt.dsn.domain.txn.DieRequestDM;
import com.gnts.stt.dsn.service.txn.DieRequestService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class DieRequest extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private DieRequestService serviceECNote = (DieRequestService) SpringContextHelper.getBean("dieRequest");
	// Initialize the logger
	private Logger logger = Logger.getLogger(DieRequest.class);
	// User Input Fields for EC Request
	private PopupDateField tfReqDate, dfCompletionDate;
	private ComboBox cbEnquiry;
	private ComboBox cbProduct;
	private GERPTextField tfNoofDie, tfCustomerCode, tfDrawingNumber;
	private TextArea taRemarks;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<DieRequestDM> beanDieRequest = null;
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
	public DieRequest() {
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
		tfNoofDie = new GERPTextField("No of Die");
		tfNoofDie.setReadOnly(false);
		tfCustomerCode = new GERPTextField("Customer Code");
		tfDrawingNumber = new GERPTextField("Drawing No.");
		taRemarks = new TextArea("Remarks");
		taRemarks.setWidth("984");
		cbEnquiry = new GERPComboBox("Enquiry No.");
		cbEnquiry.setItemCaptionPropertyId("enquiryNo");
		cbEnquiry.setImmediate(true);
		cbEnquiry.setNullSelectionAllowed(false);
		cbEnquiry.setWidth("150");
		cbEnquiry.setRequired(true);
		loadEnquiryList();
		tfReqDate = new GERPPopupDateField("Req. Date");
		tfReqDate.setDateFormat("dd-MMM-yyyy");
		tfReqDate.setInputPrompt("Select Date");
		tfReqDate.setWidth("130px");
		dfCompletionDate = new GERPPopupDateField("Completion Date");
		dfCompletionDate.setDateFormat("dd-MMM-yyyy");
		dfCompletionDate.setInputPrompt("Select Date");
		dfCompletionDate.setWidth("130px");
		cbStatus.setWidth("150");
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
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
		flcol2.addComponent(tfNoofDie);
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
		tfNoofDie.setReadOnly(true);
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol1.addComponent(tfReqDate);
		flcol1.addComponent(tfNoofDie);
		flcol2.addComponent(dfCompletionDate);
		flcol2.addComponent(cbEnquiry);
		flcol3.addComponent(cbProduct);
		flcol3.addComponent(tfCustomerCode);
		flcol4.addComponent(tfDrawingNumber);
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
				vlHeader.addComponent(taRemarks);
				addComponent(GERPPanelGenerator.createPanel(vlHeader));
			}
		});
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	// Load EC Request
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<DieRequestDM> list = new ArrayList<DieRequestDM>();
		list = serviceECNote.getDieRequestList(null, null, null, null, null);
		recordCnt = list.size();
		beanDieRequest = new BeanItemContainer<DieRequestDM>(DieRequestDM.class);
		beanDieRequest.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the ECReq. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanDieRequest);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "dieReqId", "enquiryId", "planCompleteDate", "productId",
				"noOfDie", "status", "lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Enquiry", "Date", "Product", "No of Die",
				"Status", "Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("ecnid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Load Enquiry List
	private void loadEnquiryList() {
		BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("enquiryId");
		beansmsenqHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
		cbEnquiry.setContainerDataSource(beansmsenqHdr);
		cbEnquiry.setValue(cbEnquiry.getItemIds().iterator().next());
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editECNote() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			DieRequestDM ecNoteDM = beanDieRequest.getItem(tblMstScrSrchRslt.getValue()).getBean();
			ecnid = ecNoteDM.getDieReqId();
			cbStatus.setValue(ecNoteDM.getStatus());
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, ecnid, null, null, null,
				status);
		comments.loadsrch(true, null, null, null, null, null, null, null, ecnid, null, null, null, null);
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		DieRequestDM ecNoteDM = new DieRequestDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			ecNoteDM = beanDieRequest.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		ecNoteDM.setProductId((Long) cbProduct.getValue());
		ecNoteDM.setStatus((String) cbStatus.getValue());
		ecNoteDM.setLastUpdatedBy(username);
		ecNoteDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceECNote.saveOrUpdateDetails(ecNoteDM);
		ecnid = ecNoteDM.getDieReqId();
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
		tfNoofDie.setValue("");
		tfNoofDie.setReadOnly(false);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		tfNoofDie.setReadOnly(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		tfNoofDie.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "STT_ECNNO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfNoofDie.setValue(slnoObj.getKeyDesc());
				tfNoofDie.setReadOnly(true);
			} else {
				tfNoofDie.setReadOnly(false);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null);
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
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbEnquiry.getValue() + "," + "," + ","
				+ tfReqDate.getValue() + ",");
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
		tfNoofDie.setReadOnly(false);
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
		taRemarks.setValue("");
		cbStatus.setValue(null);
		tfReqDate.setValue(new Date());
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