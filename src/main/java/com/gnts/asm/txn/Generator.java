package com.gnts.asm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.components.GERPTimeField;
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
import com.gnts.stt.dsn.domain.txn.ECRequestDM;
import com.gnts.stt.dsn.service.txn.ECRequestService;
import com.vaadin.data.Item;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Generator extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private ECRequestService serviceECRequest = (ECRequestService) SpringContextHelper.getBean("ecRequest");
	// Initialize the logger
	private Logger logger = Logger.getLogger(Generator.class);
	// User Input Fields for EC Request
	private TextField tfDiselOpenBal, tfGenTotalTime, tfDiselConsBal, tfVolts, tfAmps, tfRpmHz, tfDiselCloseBal,
			tfDiselPurLtrs, tfOtherUseLtrs, tfLtrPerHours, tfMachineServRemain, tfOneLtrCost, tfTotalCost, tfTotalTime;
	private PopupDateField dfRefDate;
	private GERPTimeField tfGenStartTime, tfGenStopTime;
	private ComboBox cbAssetName;
	private TextArea taRunningMachineDtl, taRemarks;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<ECRequestDM> beanECReq = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// local variables declaration
	private Long ecrid;
	private String username;
	private Long companyid;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public Generator() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Generator() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting Generator UI");
		// EC Request Components Definition
		tfDiselOpenBal = new GERPTextField("Disel Open Balance");
		tfDiselOpenBal.setWidth("150");
		tfDiselOpenBal.setReadOnly(false);
		tfGenTotalTime = new GERPTextField("Generator Total time");
		tfDiselConsBal = new GERPTextField("Disel Consuption Balance");
		tfVolts = new GERPTextField("Volts");
		tfAmps = new GERPTextField("Amps");
		tfRpmHz = new GERPTextField("RPM HZ");
		tfDiselCloseBal = new GERPTextField("Disel Close Balance");
		tfDiselPurLtrs = new GERPTextField("Disel Purchase(Ltrs)");
		tfOtherUseLtrs = new GERPTextField("Other Use(Ltrs)");
		tfLtrPerHours = new GERPTextField("Liter per Hour");
		tfMachineServRemain = new GERPTextField("Machine Service Remainder");
		tfOneLtrCost = new GERPTextField("One Liter Cost");
		tfTotalTime = new GERPTextField("Total Time");
		tfTotalCost = new GERPTextField("Total Cost");
		tfGenStartTime = new GERPTimeField("Start Time");
		tfGenStopTime = new GERPTimeField("Stop Time");
		taRunningMachineDtl = new TextArea("Running Machine Details");
		taRunningMachineDtl.setWidth("90%");
		taRemarks = new TextArea("Remarks");
		taRemarks.setWidth("90%");
		cbAssetName = new GERPComboBox("Enquiry No.");
		cbAssetName.setItemCaptionPropertyId("enquiryNo");
		cbAssetName.setImmediate(true);
		cbAssetName.setNullSelectionAllowed(false);
		cbAssetName.setWidth("150");
		cbAssetName.setRequired(true);
		loadEnquiryList();
		dfRefDate = new GERPPopupDateField("Date");
		dfRefDate.setDateFormat("dd-MMM-yyyy");
		dfRefDate.setInputPrompt("Select Date");
		dfRefDate.setWidth("130px");
		cbStatus.setWidth("130");
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
		flcol2.addComponent(tfDiselOpenBal);
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
		tfDiselOpenBal.setReadOnly(true);
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol1.addComponent(cbAssetName);
		flcol1.addComponent(dfRefDate);
		flcol1.addComponent(tfDiselOpenBal);
		flcol1.addComponent(tfGenTotalTime);
		flcol1.addComponent(tfDiselConsBal);
		flcol1.addComponent(tfVolts);
		flcol2.addComponent(tfAmps);
		flcol2.addComponent(tfRpmHz);
		flcol2.addComponent(tfDiselCloseBal);
		flcol2.addComponent(tfDiselPurLtrs);
		flcol2.addComponent(tfOtherUseLtrs);
		flcol2.addComponent(tfLtrPerHours);
		flcol3.addComponent(tfMachineServRemain);
		flcol3.addComponent(tfOneLtrCost);
		flcol3.addComponent(tfTotalCost);
		flcol3.addComponent(tfGenStartTime);
		flcol3.addComponent(tfGenStopTime);
		flcol3.addComponent(tfTotalTime);
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
				vlHeader.addComponent(taRunningMachineDtl);
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
		List<ECRequestDM> listECReq = new ArrayList<ECRequestDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + null + "," + tfDiselOpenBal.getValue() + ", " + (String) cbStatus.getValue());
		listECReq = serviceECRequest.getECRequestList(null, tfDiselOpenBal.getValue(), null,
				(String) cbStatus.getValue());
		recordCnt = listECReq.size();
		beanECReq = new BeanItemContainer<ECRequestDM>(ECRequestDM.class);
		beanECReq.addAll(listECReq);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the ECReq. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanECReq);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "ecrid", "ecrNumber", "ecrDate", "drgNumber", "status",
				"lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "ECR Number", "Date", "Drg. Number", "Status",
				"Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("ecrid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Load Enquiry List
	private void loadEnquiryList() {
		List<SmsEnqHdrDM> getsmsEnqNoHdr = new ArrayList<SmsEnqHdrDM>();
		getsmsEnqNoHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
		BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("enquiryId");
		beansmsenqHdr.addAll(getsmsEnqNoHdr);
		cbAssetName.setContainerDataSource(beansmsenqHdr);
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editECRequest() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected ecrid -> " + ecrid);
		if (sltedRcd != null) {
			ECRequestDM ecRequestDM = beanECReq.getItem(tblMstScrSrchRslt.getValue()).getBean();
			ecrid = ecRequestDM.getEcrid();
			tfDiselOpenBal.setReadOnly(false);
			tfDiselOpenBal.setValue(ecRequestDM.getEcrNumber());
			tfDiselOpenBal.setReadOnly(true);
			cbAssetName.setValue(ecRequestDM.getEnquiryId());
			dfRefDate.setValue(ecRequestDM.getEcrDate());
			if (ecRequestDM.getChangeDetail() != null) {
				taRunningMachineDtl.setValue(ecRequestDM.getChangeDetail());
			}
			if (ecRequestDM.getChangeReason() != null) {
				taRemarks.setValue(ecRequestDM.getChangeReason());
			}
			cbStatus.setValue(ecRequestDM.getStatus());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		ECRequestDM ecRequestDM = new ECRequestDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			ecRequestDM = beanECReq.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		ecRequestDM.setEcrNumber(tfDiselOpenBal.getValue());
		ecRequestDM.setEnquiryId((Long) cbAssetName.getValue());
		ecRequestDM.setEcrDate(dfRefDate.getValue());
		ecRequestDM.setChangeDetail(taRunningMachineDtl.getValue());
		ecRequestDM.setChangeReason(taRemarks.getValue());
		ecRequestDM.setStatus((String) cbStatus.getValue());
		ecRequestDM.setLastUpdatedBy(username);
		ecRequestDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceECRequest.saveOrUpdateECRequest(ecRequestDM);
		ecrid = ecRequestDM.getEcrid();
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		tfDiselOpenBal.setValue("");
		tfDiselOpenBal.setReadOnly(false);
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
		tfDiselOpenBal.setReadOnly(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		tfDiselOpenBal.setReadOnly(false);
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
		editECRequest();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbAssetName.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbAssetName.getValue() == null) {
			cbAssetName.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorFlag = true;
		}
		if ((dfRefDate.getValue() == null)) {
			dfRefDate.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfRefDate.getValue());
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbAssetName.getValue() + "," + ","
				+ "," + dfRefDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for enquiryId " + ecrid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(ecrid));
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
		tfDiselOpenBal.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfDiselOpenBal.setReadOnly(false);
		tfDiselOpenBal.setValue("");
		tfDiselOpenBal.setComponentError(null);
		cbAssetName.setComponentError(null);
		cbAssetName.setValue(null);
		dfRefDate.setValue(null);
		taRunningMachineDtl.setValue("");
		taRemarks.setValue("");
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
			tfDiselOpenBal.setReadOnly(false);
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
			parameterMap.put("ECRID", ecrid);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/ecr"); // ecr is the name of my jasper
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
