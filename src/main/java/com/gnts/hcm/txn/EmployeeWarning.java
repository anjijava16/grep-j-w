package com.gnts.hcm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
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
import com.gnts.stt.dsn.domain.txn.EmployeeWarningDM;
import com.gnts.stt.dsn.service.txn.EmployeeWarningService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

public class EmployeeWarning extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private EmployeeWarningService serviceEmplyeeWarning = (EmployeeWarningService) SpringContextHelper
			.getBean("employeeWarning");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// Initialize the logger
	private Logger logger = Logger.getLogger(EmployeeWarning.class);
	// User Input Fields for EC Request
	private GERPPopupDateField dfRefDate, dfDateTo;
	private GERPComboBox cbEmployee, cbWarLevel;
	private GERPTextField tfFrom, tfTo, tfFineAmount;
	private GERPTextField tfDeductMonth;
	private TextArea taRemarks;
	private GERPTimeField tfTimeOut;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<EmployeeWarningDM> beanEmpWarning = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4, flcol5;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// local variables declaration
	private Long visitorid;
	private String username;
	private Long companyid;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public EmployeeWarning() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeWarning() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting EmployeeWarning UI");
		// EC Request Components Definition
		tfTimeOut = new GERPTimeField("Time In");
		tfFrom = new GERPTextField("From");
		tfFrom.setWidth("150");
		tfDeductMonth = new GERPTextField("Deduct month");
		tfDeductMonth.setWidth("150");
		tfFineAmount = new GERPTextField("Fine Amount");
		tfTo = new GERPTextField("To");
		taRemarks = new TextArea("Reason");
		taRemarks.setHeight("40");
		taRemarks.setWidth("150");
		cbEmployee = new GERPComboBox("Employee");
		cbEmployee.setItemCaptionPropertyId("firstname");
		cbEmployee.setImmediate(true);
		cbEmployee.setNullSelectionAllowed(false);
		cbEmployee.setWidth("150");
		cbEmployee.setRequired(true);
		loadEmployeeList();
		cbWarLevel = new GERPComboBox("Level");
		cbWarLevel.setWidth("150");
		loadWarLevel();
		dfDateTo = new GERPPopupDateField("To Date");
		dfDateTo.setDateFormat("dd-MMM-yyyy");
		dfDateTo.setInputPrompt("Select Date");
		dfDateTo.setWidth("110px");
		dfRefDate = new GERPPopupDateField("Date");
		dfRefDate.setRequired(true);
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
		flcol1.addComponent(cbEmployee);
		flcol2.addComponent(cbStatus);
		hlsearchlayout.addComponent(flcol1);
		hlsearchlayout.addComponent(flcol2);
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
		flcol1.addComponent(dfRefDate);
		flcol1.addComponent(cbEmployee);
		flcol2.addComponent(tfFrom);
		flcol2.addComponent(tfTo);
		flcol3.addComponent(tfTimeOut);
		flcol3.addComponent(tfDeductMonth);
		flcol3.addComponent(tfFineAmount);
		flcol4.addComponent(cbWarLevel);
		flcol4.addComponent(taRemarks);
		flcol5.addComponent(dfDateTo);
		flcol5.addComponent(cbStatus);
		hllayout.setMargin(true);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol4);
		hllayout.addComponent(flcol5);
		hllayout.setMargin(true);
		hllayout.setSpacing(true);
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hllayout));
	}
	
	// Load EC Request
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<EmployeeWarningDM> list = new ArrayList<EmployeeWarningDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + null + "," + tfFrom.getValue() + ", " + (String) cbStatus.getValue());
		list = serviceEmplyeeWarning.getEmployeeWarningList(null, null, null, (String) cbStatus.getValue());
		recordCnt = list.size();
		beanEmpWarning = new BeanItemContainer<EmployeeWarningDM>(EmployeeWarningDM.class);
		beanEmpWarning.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the VisitorPass. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanEmpWarning);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "empwarningId", "refDate", "fromName", "toName",
				"deductAmt", "status", "lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Date", "From Name", "To Name", "Amount", "Status",
				"Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("empwarningId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	/*
	 * Employe waring level
	 */
	private void loadWarLevel() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Warning Search Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"HC_EMPWAR"));
			cbWarLevel.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Employee List
	private void loadEmployeeList() {
		BeanContainer<Long, EmployeeDM> beanInitiatedBy = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanInitiatedBy.setBeanIdProperty("employeeid");
		beanInitiatedBy.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null, null,
				null, "P"));
		cbEmployee.setContainerDataSource(beanInitiatedBy);
	}
	
	// Method to edit the values from table into fields to update process for VisitorPass
	private void ediVisitorpass() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected ecrid -> "
				+ visitorid);
		if (tblMstScrSrchRslt.getValue() != null) {
			EmployeeWarningDM employeewarningDM = beanEmpWarning.getItem(tblMstScrSrchRslt.getValue()).getBean();
			visitorid = employeewarningDM.getEmpwarningId();
			dfRefDate.setValue(employeewarningDM.getRefDate());
			cbEmployee.setValue(employeewarningDM.getEmployeeID());
			cbStatus.setValue(employeewarningDM.getStatus());
			tfFrom.setValue(employeewarningDM.getFromName());
			tfFineAmount.setValue(employeewarningDM.getDeductAmt());
			tfTo.setValue(employeewarningDM.getToName());
			taRemarks.setValue(employeewarningDM.getReason());
			tfDeductMonth.setValue(employeewarningDM.getDeductFrom());
			cbWarLevel.setValue(employeewarningDM.getWarLevel());
			dfDateTo.setValue(employeewarningDM.getDateTo());
			tfTimeOut.setTime(employeewarningDM.getTimeOut());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		EmployeeWarningDM employeewarningDM = new EmployeeWarningDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			employeewarningDM = beanEmpWarning.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		employeewarningDM.setRefDate(dfRefDate.getValue());
		employeewarningDM.setEmployeeID((Long) cbEmployee.getValue());
		employeewarningDM.setStatus((String) cbStatus.getValue());
		employeewarningDM.setFromName(tfFrom.getValue());
		employeewarningDM.setDeductAmt(tfFineAmount.getValue());
		employeewarningDM.setToName(tfTo.getValue());
		employeewarningDM.setReason(taRemarks.getValue());
		employeewarningDM.setDeductFrom(tfDeductMonth.getValue());
		employeewarningDM.setLastUpdatedBy(username);
		employeewarningDM.setWarLevel(cbWarLevel.getValue().toString());
		employeewarningDM.setDateTo(dfDateTo.getValue());
		employeewarningDM.setTimeOut(tfTimeOut.getHorsMunites().toString());
		employeewarningDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceEmplyeeWarning.saveOrUpdateEmployeeWarning(employeewarningDM);
		visitorid = employeewarningDM.getEmpwarningId();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		cbEmployee.setValue(null);
		tfFrom.setValue("");
		tfFrom.setReadOnly(false);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		ediVisitorpass();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbEmployee.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbEmployee.getValue() == null) {
			tfFrom.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorFlag = true;
		}
		if ((dfRefDate.getValue() == null)) {
			dfRefDate.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfRefDate.getValue());
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbEmployee.getValue() + "," + ","
				+ "," + dfRefDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for outpassid " + visitorid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(visitorid));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hllayout1.removeAllComponents();
		tblMstScrSrchRslt.setValue(null);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		tfFrom.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		dfRefDate.setValue(new Date());
		tfFrom.setValue("");
		tfFrom.setComponentError(null);
		cbEmployee.setComponentError(null);
		cbEmployee.setValue(null);
		tfTo.setValue(null);
		tfFineAmount.setValue(null);
		dfRefDate.setValue(null);
		taRemarks.setValue("");
		tfDeductMonth.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
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
			tfFrom.setReadOnly(false);
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
			parameterMap.put("ECRID", visitorid);
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