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
import com.gnts.asm.domain.txn.EbReadingDM;
import com.gnts.asm.service.txn.EbReadingService;
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
import com.vaadin.data.Item;
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

public class EBReading extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private EbReadingService serviceEBReading = (EbReadingService) SpringContextHelper.getBean("ebReading");
	// Initialize the logger
	private Logger logger = Logger.getLogger(EBReading.class);
	// User Input Fields for EC Request
	private TextField tfMainKwHr, tfC1, tfC2, tfC3, tfC4, tfC5, tfKvaHr, tfR1, tfR2, tfR3, tfR4, tfR5, tfRkvaHrCag,
			tfLead, tfPfc, tfPerDayUnit, tfPf, tfUnitCharge, tfAdjstCharge, tfHaltUnitCharge;
	private PopupDateField dfRefDate;
	private TextArea taMachineRunDetails, taRemarks;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<EbReadingDM> beanECReq = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// local variables declaration
	private Long ebReadingId;
	private String username;
	private Long companyid;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public EBReading() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ECRequest() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting ECRequest UI");
		// EC Request Components Definition
		tfMainKwHr = new GERPTextField("Main KW HR");
		tfC1 = new GERPTextField("C1");
		tfC2 = new GERPTextField("C2");
		tfC3 = new GERPTextField("C3");
		tfC4 = new GERPTextField("C4");
		tfC5 = new GERPTextField("C5");
		tfKvaHr = new GERPTextField("KVA HR");
		tfR1 = new GERPTextField("R1");
		tfR2 = new GERPTextField("R2");
		tfR3 = new GERPTextField("R3");
		tfR4 = new GERPTextField("R4");
		tfR5 = new GERPTextField("R5");
		tfRkvaHrCag = new GERPTextField("RKVA HR CAG");
		tfLead = new GERPTextField("Lead");
		tfPfc = new GERPTextField("PFC");
		tfPerDayUnit = new GERPTextField("Per Day Unit");
		tfPf = new GERPTextField("PF");
		tfUnitCharge = new GERPTextField("Unit Charge");
		tfAdjstCharge = new GERPTextField("Adjust. Charge");
		tfHaltUnitCharge = new GERPTextField("Half Unit Charge");
		taMachineRunDetails = new TextArea("Machine Run Details");
		taMachineRunDetails.setWidth("96%");
		taRemarks = new TextArea("Remarks");
		taRemarks.setWidth("96%");
		dfRefDate = new GERPPopupDateField("Date");
		dfRefDate.setDateFormat("dd-MMM-yyyy");
		dfRefDate.setWidth("130px");
		cbStatus.setWidth("150");
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
		flcol2.addComponent(dfRefDate);
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
		tfMainKwHr.setReadOnly(true);
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol1.addComponent(tfMainKwHr);
		flcol1.addComponent(dfRefDate);
		flcol1.addComponent(tfC1);
		flcol1.addComponent(tfC2);
		flcol1.addComponent(tfC3);
		flcol1.addComponent(tfC4);
		flcol1.addComponent(tfC5);
		flcol2.addComponent(tfKvaHr);
		flcol2.addComponent(tfR1);
		flcol2.addComponent(tfR2);
		flcol2.addComponent(tfR3);
		flcol2.addComponent(tfR4);
		flcol2.addComponent(tfR5);
		flcol3.addComponent(tfRkvaHrCag);
		flcol3.addComponent(tfLead);
		flcol3.addComponent(tfPfc);
		flcol3.addComponent(tfPerDayUnit);
		flcol3.addComponent(tfPf);
		flcol3.addComponent(tfUnitCharge);
		flcol4.addComponent(tfAdjstCharge);
		flcol4.addComponent(tfHaltUnitCharge);
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
				vlHeader.addComponent(taMachineRunDetails);
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
		List<EbReadingDM> list = new ArrayList<EbReadingDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + null + "," + tfMainKwHr.getValue() + ", " + (String) cbStatus.getValue());
		list = serviceEBReading.getEbReadingDetailList(null, null, null, null);
		recordCnt = list.size();
		beanECReq = new BeanItemContainer<EbReadingDM>(EbReadingDM.class);
		beanECReq.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the ECReq. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanECReq);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "ebReadingId", "readingDate", "mainKwHr", "kvaHr",
				"perDayUnit", "status", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Date", "Main KW HR", "KVA HR", "Per Day Unit",
				"Status", "Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("ebReadingId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editECRequest() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected ecrid -> " + ebReadingId);
		if (sltedRcd != null) {
			EbReadingDM ecRequestDM = beanECReq.getItem(tblMstScrSrchRslt.getValue()).getBean();
			ebReadingId = ecRequestDM.getEbReadingId();
			cbStatus.setValue(ecRequestDM.getStatus());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		EbReadingDM ecRequestDM = new EbReadingDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			ecRequestDM = beanECReq.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		ecRequestDM.setStatus((String) cbStatus.getValue());
		ecRequestDM.setLastupdatedby(username);
		ecRequestDM.setLastupdateddt(DateUtils.getcurrentdate());
		serviceEBReading.saveOrUpdateDetails(ecRequestDM);
		ebReadingId = ecRequestDM.getEbReadingId();
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		tfMainKwHr.setValue("");
		tfMainKwHr.setReadOnly(false);
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
		tfMainKwHr.setReadOnly(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		tfMainKwHr.setReadOnly(false);
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
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((dfRefDate.getValue() == null)) {
			dfRefDate.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfRefDate.getValue());
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + null + "," + "," + ","
				+ dfRefDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for enquiryId " + ebReadingId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(ebReadingId));
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
		tfMainKwHr.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfMainKwHr.setReadOnly(false);
		tfMainKwHr.setValue("");
		tfMainKwHr.setComponentError(null);
		dfRefDate.setValue(null);
		taMachineRunDetails.setValue("");
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
			tfMainKwHr.setReadOnly(false);
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
			parameterMap.put("ECRID", ebReadingId);
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
