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
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
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
import com.gnts.stt.dsn.domain.txn.PhoneRegDM;
import com.gnts.stt.dsn.service.txn.PhoneRegService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

public class PhoneCallRegister extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private PhoneRegService servicePhoneReg = (PhoneRegService) SpringContextHelper.getBean("phoneregister");
	private DepartmentService servicebeandepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	// Initialize the logger
	private Logger logger = Logger.getLogger(PhoneCallRegister.class);
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// User Input Fields for EC Request
	private GERPPopupDateField dfCallDate;
	private GERPComboBox cbEmployee, cbDepartment, cbCallType;
	private GERPTextField tfPhoneNumber, tfCompany, tfIntercom, tfTime;
	private TextArea taPurpose;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<PhoneRegDM> beanPhoneReg = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// local variables declaration
	private Long phoneRegid;
	private String username;
	private Long companyid;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public PhoneCallRegister() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PhoneCallRegister() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting PhoneCallRegister UI");
		// EC Request Components Definition
		cbCallType = new GERPComboBox("Call Type");
		cbCallType.setWidth("170");
		cbCallType.setRequired(true);
		loadCallType();
		tfPhoneNumber = new GERPTextField("Phone Number");
		tfPhoneNumber.setWidth("150");
		tfPhoneNumber.setReadOnly(false);
		tfPhoneNumber.setRequired(true);
		tfTime = new GERPTextField("Duration");
		tfTime.setWidth("130");
		tfIntercom = new GERPTextField("Intercom");
		tfIntercom.setRequired(true);
		tfCompany = new GERPTextField("Company/Customer");
		cbDepartment = new GERPComboBox("Department");
		cbDepartment.setItemCaptionPropertyId("deptname");
		loadDepartmentList();
		taPurpose = new TextArea("Purpose");
		taPurpose.setHeight("50px");
		cbEmployee = new GERPComboBox("Employee");
		cbEmployee.setItemCaptionPropertyId("firstname");
		cbEmployee.setImmediate(true);
		cbEmployee.setNullSelectionAllowed(false);
		cbEmployee.setWidth("150");
		cbEmployee.setRequired(true);
		loadEmployeeList();
		dfCallDate = new GERPPopupDateField("Date");
		dfCallDate.setRequired(true);
		dfCallDate.setDateFormat("dd-MMM-yyyy");
		dfCallDate.setInputPrompt("Select Date");
		dfCallDate.setWidth("130px");
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
		flcol1.addComponent(dfCallDate);
		flcol1.addComponent(cbEmployee);
		flcol1.addComponent(cbDepartment);
		flcol2.addComponent(tfPhoneNumber);
		flcol2.addComponent(tfIntercom);
		flcol2.addComponent(tfCompany);
		flcol3.addComponent(cbCallType);
		flcol3.addComponent(taPurpose);
		flcol4.addComponent(tfTime);
		flcol4.addComponent(cbStatus);
		hllayout.setMargin(true);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol4);
		hllayout.setMargin(true);
		hllayout.setSpacing(true);
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hllayout));
	}
	
	// Load EC Request
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<PhoneRegDM> list = new ArrayList<PhoneRegDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + null + "," + tfPhoneNumber.getValue() + ", " + (String) cbStatus.getValue());
		list = servicePhoneReg.getPhoneRegList(null, (Long) cbEmployee.getValue(), null, null,
				(String) cbStatus.getValue());
		recordCnt = list.size();
		beanPhoneReg = new BeanItemContainer<PhoneRegDM>(PhoneRegDM.class);
		beanPhoneReg.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Outpass. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanPhoneReg);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "phoneRegId", "callDate", "companyName", "interNo",
				"phoneTime", "status", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Date", "Company/Customer", "Intercom", "Duration",
				"Status", "Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("phoneRegId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	/*
	 * Load call type.
	 */
	private void loadCallType() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Call Type Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"HC_CALTYP"));
			cbCallType.setContainerDataSource(beanCompanyLookUp);
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
	
	/*
	 * loadDepartmentList()-->this function is used for load the Department list
	 */
	private void loadDepartmentList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Department Search...");
		BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beanDepartment.setBeanIdProperty("deptid");
		beanDepartment.addAll(servicebeandepartmant.getDepartmentList(companyid, null, "Active", "P"));
		cbDepartment.setContainerDataSource(beanDepartment);
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editOutpass() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected ecrid -> "
				+ phoneRegid);
		if (tblMstScrSrchRslt.getValue() != null) {
			PhoneRegDM phoneRegDM = beanPhoneReg.getItem(tblMstScrSrchRslt.getValue()).getBean();
			dfCallDate.setValue(phoneRegDM.getCallDate());
			cbEmployee.setValue(phoneRegDM.getEmployeeId());
			cbDepartment.setValue(phoneRegDM.getDeptId());
			cbStatus.setValue(phoneRegDM.getStatus());
			tfPhoneNumber.setValue(phoneRegDM.getPhoneNumber());
			tfIntercom.setValue(phoneRegDM.getInterNo());
			tfCompany.setValue(phoneRegDM.getCompanyName());
			taPurpose.setValue(phoneRegDM.getPurpose());
			tfTime.setValue(phoneRegDM.getPhoneTime());
			cbCallType.setValue(phoneRegDM.getCallType());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		PhoneRegDM phoneRegDM = new PhoneRegDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			phoneRegDM = beanPhoneReg.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		phoneRegDM.setCallDate(dfCallDate.getValue());
		phoneRegDM.setEmployeeId((Long) cbEmployee.getValue());
		phoneRegDM.setDeptId((Long) cbDepartment.getValue());
		phoneRegDM.setStatus((String) cbStatus.getValue());
		phoneRegDM.setPhoneNumber(tfPhoneNumber.getValue());
		phoneRegDM.setInterNo(tfIntercom.getValue());
		phoneRegDM.setCompanyName(tfCompany.getValue());
		phoneRegDM.setPurpose(taPurpose.getValue());
		phoneRegDM.setPhoneTime(tfTime.getValue());
		phoneRegDM.setLastUpdatedBy(username);
		phoneRegDM.setCallType(cbCallType.getValue().toString());
		phoneRegDM.setLastUpdatedDt(DateUtils.getcurrentdate());
		servicePhoneReg.saveOrUpdatePhoneReg(phoneRegDM);
		phoneRegid = phoneRegDM.getPhoneRegId();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		tfPhoneNumber.setValue("");
		tfPhoneNumber.setReadOnly(false);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		tfPhoneNumber.setReadOnly(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		tfPhoneNumber.setReadOnly(false);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		editOutpass();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbEmployee.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbEmployee.getValue() == null) {
			cbEmployee.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorFlag = true;
		}
		if ((dfCallDate.getValue() == null)) {
			dfCallDate.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfCallDate.getValue());
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbEmployee.getValue() + "," + ","
				+ "," + dfCallDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for outpassid " + phoneRegid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(phoneRegid));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hllayout1.removeAllComponents();
		tblMstScrSrchRslt.setValue(null);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		tfPhoneNumber.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		dfCallDate.setValue(new Date());
		tfPhoneNumber.setValue("");
		tfPhoneNumber.setComponentError(null);
		cbEmployee.setComponentError(null);
		cbEmployee.setValue(null);
		tfCompany.setValue(null);
		tfIntercom.setValue(null);
		cbDepartment.setValue(null);
		dfCallDate.setValue(null);
		taPurpose.setValue("");
		tfTime.setValue("");
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
			tfPhoneNumber.setReadOnly(false);
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
			parameterMap.put("ECRID", phoneRegid);
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