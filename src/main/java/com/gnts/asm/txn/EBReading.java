package com.gnts.asm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.txn.EbReadingDM;
import com.gnts.asm.domain.txn.GeneratorDM;
import com.gnts.asm.service.txn.EbReadingService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPNumberField;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
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
			tfLead, tfPfc, tfPerDayUnit, tfPf, tfUnitCharge, tfAdjstCharge, tfHalfUnitCharge, tfCHours,tfKvaMdr;
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
		tfMainKwHr = new GERPNumberField("Main KW HR");
		tfC1 = new GERPNumberField("C1");
		tfC2 = new GERPNumberField("C2");
		tfC3 = new GERPNumberField("C3");
		tfC4 = new GERPNumberField("C4");
		tfC5 = new GERPNumberField("C5");
		tfRkvaHrCag = new GERPNumberField("RKVA HR CAG");
		tfR1 = new GERPNumberField("R1");
		tfR2 = new GERPNumberField("R2");
		tfR3 = new GERPNumberField("R3");
		tfR4 = new GERPNumberField("R4");
		tfR5 = new GERPNumberField("R5");
		tfKvaHr = new GERPNumberField("KVA HR");
		tfKvaMdr= new GERPNumberField("KVA MDR");
		tfLead = new GERPNumberField("Lead");
		tfPfc = new GERPNumberField("PFC");
		tfPerDayUnit = new GERPNumberField("Per Day Unit");
		tfPf = new GERPNumberField("PF");
		tfUnitCharge = new GERPNumberField("Unit Charge");
		tfAdjstCharge = new GERPNumberField("Adjust. Charge");
		tfHalfUnitCharge = new GERPNumberField("Peak Hours Unit");
		tfCHours = new GERPNumberField("Consumption Hours");
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
		btnPrint.setVisible(true);
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
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol1.addComponent(dfRefDate);
		flcol1.addComponent(tfC1);
		flcol1.addComponent(tfC2);
		flcol1.addComponent(tfC3);
		flcol1.addComponent(tfC4);
		flcol1.addComponent(tfC5);
		flcol2.addComponent(tfMainKwHr);
		flcol2.addComponent(tfR1);
		flcol2.addComponent(tfR2);
		flcol2.addComponent(tfR3);
		flcol2.addComponent(tfR4);
		flcol2.addComponent(tfR5);
		flcol3.addComponent(tfRkvaHrCag);
		flcol3.addComponent(tfLead);
		flcol3.addComponent(tfKvaHr);
		flcol3.addComponent(tfPfc);
		flcol3.addComponent(tfCHours);
		flcol3.addComponent(tfKvaMdr);
		flcol4.addComponent(tfPerDayUnit);
		flcol4.addComponent(tfPf);
		flcol4.addComponent(tfUnitCharge);
		flcol4.addComponent(tfAdjstCharge);
		flcol4.addComponent(tfHalfUnitCharge);
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
	private void editEbReading() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected ecrid -> "
				+ ebReadingId);
		if (tblMstScrSrchRslt.getValue() != null) {
			EbReadingDM ebReadingDM = beanECReq.getItem(tblMstScrSrchRslt.getValue()).getBean();
			ebReadingId = ebReadingDM.getEbReadingId();
			dfRefDate.setValue(ebReadingDM.getReadingDate1());
			if (ebReadingDM.getC1() != null) {
				tfC1.setValue(ebReadingDM.getC1().toString());
			}
			if (ebReadingDM.getC2() != null) {
				tfC2.setValue(ebReadingDM.getC2().toString());
			}
			if (ebReadingDM.getC3() != null) {
				tfC3.setValue(ebReadingDM.getC3().toString());
			}
			if (ebReadingDM.getC4() != null) {
				tfC4.setValue(ebReadingDM.getC4().toString());
			}
			if (ebReadingDM.getC5() != null) {
				tfC5.setValue(ebReadingDM.getC5().toString());
			}
			if (ebReadingDM.getKvaHr() != null) {
				tfKvaHr.setValue(ebReadingDM.getKvaHr().toString());
			}
			if (ebReadingDM.getR1() != null) {
				tfR1.setValue(ebReadingDM.getR1().toString());
			}
			if (ebReadingDM.getR2() != null) {
				tfR2.setValue(ebReadingDM.getR2().toString());
			}
			if (ebReadingDM.getR3() != null) {
				tfR3.setValue(ebReadingDM.getR3().toString());
			}
			if (ebReadingDM.getR4() != null) {
				tfR4.setValue(ebReadingDM.getR4().toString());
			}
			if (ebReadingDM.getR5() != null) {
				tfR5.setValue(ebReadingDM.getR5().toString());
			}
			if (ebReadingDM.getRkvaHrCag() != null) {
				tfRkvaHrCag.setValue(ebReadingDM.getRkvaHrCag().toString());
			}
			if (ebReadingDM.getLead() != null) {
				tfLead.setValue(ebReadingDM.getLead().toString());
			}
			if (ebReadingDM.getPfc() != null) {
				tfPfc.setValue(ebReadingDM.getPfc().toString());
			}
			if (ebReadingDM.getPerDayUnit() != null) {
				tfPerDayUnit.setValue(ebReadingDM.getPerDayUnit().toString());
			}
			if (ebReadingDM.getPf() != null) {
				tfPf.setValue(ebReadingDM.getPf().toString());
			}
			if (ebReadingDM.getUnitCharge() != null) {
				tfUnitCharge.setValue(ebReadingDM.getUnitCharge().toString());
			}
			if (ebReadingDM.getMainKwHr() != null) {
				tfMainKwHr.setValue(ebReadingDM.getMainKwHr().toString());
			}
			if (ebReadingDM.getAdjustmentCharge() != null) {
				tfAdjstCharge.setValue(ebReadingDM.getAdjustmentCharge().toString());
			}
			if (ebReadingDM.getHalfUnitCharge() != null) {
				tfHalfUnitCharge.setValue(ebReadingDM.getHalfUnitCharge().toString());
			}
			if (ebReadingDM.getMachineRunDetails() != null) {
				taMachineRunDetails.setValue(ebReadingDM.getMachineRunDetails());
			}
			if (ebReadingDM.getRemarks() != null) {
				taRemarks.setValue(ebReadingDM.getRemarks());
			}
			if (ebReadingDM.getConsHours() != null) {
				tfCHours.setValue(ebReadingDM.getConsHours().toString());
			}
			if (ebReadingDM.getKvaMdr() != null) {
				tfKvaMdr.setValue(ebReadingDM.getKvaMdr().toString());
			}
			cbStatus.setValue(ebReadingDM.getStatus());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		EbReadingDM ebReadingDM = new EbReadingDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			ebReadingDM = beanECReq.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		ebReadingDM.setReadingDate(dfRefDate.getValue());
		if (tfC1.getValue() != null) {
			ebReadingDM.setC1(new BigDecimal(tfC1.getValue()));
		}
		if (tfC2.getValue() != null) {
			ebReadingDM.setC2(new BigDecimal(tfC2.getValue()));
		}
		if (tfC3.getValue() != null) {
			ebReadingDM.setC3(new BigDecimal(tfC3.getValue()));
		}
		if (tfC4.getValue() != null) {
			ebReadingDM.setC4(new BigDecimal(tfC4.getValue()));
		}
		if (tfC5.getValue() != null) {
			ebReadingDM.setC5(new BigDecimal(tfC5.getValue()));
		}
		if (tfKvaHr.getValue() != null) {
			ebReadingDM.setKvaHr(new BigDecimal(tfKvaHr.getValue()));
		}
		if (tfR1.getValue() != null) {
			ebReadingDM.setR1(new BigDecimal(tfR1.getValue()));
		}
		if (tfR2.getValue() != null) {
			ebReadingDM.setR2(new BigDecimal(tfR2.getValue()));
		}
		if (tfR3.getValue() != null) {
			ebReadingDM.setR3(new BigDecimal(tfR3.getValue()));
		}
		if (tfR4.getValue() != null) {
			ebReadingDM.setR4(new BigDecimal(tfR4.getValue()));
		}
		if (tfR5.getValue() != null) {
			ebReadingDM.setR5(new BigDecimal(tfR5.getValue()));
		}
		if (tfRkvaHrCag.getValue() != null) {
			ebReadingDM.setRkvaHrCag(new BigDecimal(tfRkvaHrCag.getValue()));
		}
		if (tfLead.getValue() != null) {
			ebReadingDM.setLead(new BigDecimal(tfLead.getValue()));
		}
		if (tfPfc.getValue() != null) {
			ebReadingDM.setPfc(new BigDecimal(tfPfc.getValue()));
		}
		if (tfPerDayUnit.getValue() != null) {
			ebReadingDM.setPerDayUnit(new BigDecimal(tfPerDayUnit.getValue()));
		}
		if (tfPf.getValue() != null) {
			ebReadingDM.setPf(new BigDecimal(tfPf.getValue()));
		}
		if (tfUnitCharge.getValue() != null) {
			ebReadingDM.setUnitCharge(new BigDecimal(tfUnitCharge.getValue()));
		}
		if (tfMainKwHr.getValue() != null) {
			ebReadingDM.setMainKwHr(new BigDecimal(tfMainKwHr.getValue()));
		}
		if (tfAdjstCharge.getValue() != null) {
			ebReadingDM.setAdjustmentCharge(new BigDecimal(tfAdjstCharge.getValue()));
		}
		if (tfHalfUnitCharge.getValue() != null) {
			ebReadingDM.setHalfUnitCharge(new BigDecimal(tfHalfUnitCharge.getValue()));
		}
		if (tfCHours.getValue() != null) {
			ebReadingDM.setConsHours(new BigDecimal(tfCHours.getValue()));
		}
		if (tfKvaMdr.getValue() != null) {
			ebReadingDM.setKvaMdr(new BigDecimal(tfKvaMdr.getValue()));
		}
		ebReadingDM.setMachineRunDetails(taMachineRunDetails.getValue());
		ebReadingDM.setRemarks(taRemarks.getValue());
		ebReadingDM.setStatus((String) cbStatus.getValue());
		ebReadingDM.setLastupdatedby(username);
		ebReadingDM.setLastupdateddt(DateUtils.getcurrentdate());
		serviceEBReading.saveOrUpdateDetails(ebReadingDM);
		ebReadingId = ebReadingDM.getEbReadingId();
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		tfMainKwHr.setValue("");
		dfRefDate.setValue(null);
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
		dfRefDate.setValue(new Date());
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
		editEbReading();
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
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		dfRefDate.setValue(null);
		
		EbReadingDM ebReadingDM=serviceEBReading.getEbReadingDetailList(null, null, null, "Y").get(0);
		
		tfC1.setValue(ebReadingDM.getC1().toString());
		tfC2.setValue("0");
		tfC3.setValue("0");
		tfC4.setValue("0");
		tfC5.setValue("0");
		tfKvaHr.setValue("0");
		tfR1.setValue("0");
		tfR2.setValue("0");
		tfR3.setValue("0");
		tfR4.setValue("0");
		tfR5.setValue("0");
		tfKvaMdr.setValue("0");
		tfCHours.setValue("");
		tfRkvaHrCag.setValue("0");
		tfLead.setValue("0");
		tfPfc.setValue("0");
		tfPerDayUnit.setValue("0");
		tfPf.setValue("0");
		tfUnitCharge.setValue("0");
		tfMainKwHr.setValue("0");
		tfAdjstCharge.setValue("0");
		tfHalfUnitCharge.setValue("0");
		taMachineRunDetails.setValue("");
		taRemarks.setValue("");
		cbStatus.setValue(null);
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
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/ebreading"); // ebreading is the name of my jasper
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
