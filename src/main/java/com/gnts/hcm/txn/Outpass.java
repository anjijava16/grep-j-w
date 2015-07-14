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
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
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
import com.gnts.stt.dsn.domain.txn.OutpassDM;
import com.gnts.stt.dsn.service.txn.OutpassService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Outpass extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private OutpassService serviceOutpass = (OutpassService) SpringContextHelper.getBean("outpass");
	private DepartmentService servicebeandepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	// Initialize the logger
	private Logger logger = Logger.getLogger(Outpass.class);
	// User Input Fields for EC Request
	private GERPPopupDateField dfPassDate;
	private GERPComboBox cbEmployee;
	private GERPComboBox cbDepartment;
	private GERPTextField tfPlace, tfTimeIn, tfTimeOut;
	private GERPComboBox cbVehicle;
	private GERPTextField tfVehicleNo, tfKMIn, tfKMOut;
	private TextArea taPurpose;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<OutpassDM> beanOutpass = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4, flcol5;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// Parent layout for all the input controls Sms Comments
	VerticalLayout vlTableForm = new VerticalLayout();
	// local variables declaration
	private Long outpassid;
	private String username;
	private Long companyid;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public Outpass() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Outpass() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting Outpass UI");
		// EC Request Components Definition
		tfPlace = new GERPTextField("Place");
		tfPlace.setWidth("150");
		tfPlace.setReadOnly(false);
		tfVehicleNo = new GERPTextField("Vehicle No.");
		tfVehicleNo.setWidth("130");
		tfKMIn = new GERPTextField("KM Out");
		tfKMIn.setWidth("130");
		tfKMOut = new GERPTextField("KM In");
		tfKMOut.setWidth("130");
		cbVehicle = new GERPComboBox("Vehicle");
		cbVehicle.setWidth("130");
		cbVehicle.addItems("Company", "Personal");
		tfTimeOut = new GERPTextField("Time Out");
		tfTimeIn = new GERPTextField("Time In");
		cbDepartment = new GERPComboBox("Department");
		cbDepartment.setItemCaptionPropertyId("deptname");
		loadDepartmentList();
		taPurpose = new TextArea("Purpose");
		taPurpose.setHeight("70px");
		cbEmployee = new GERPComboBox("Employee");
		cbEmployee.setItemCaptionPropertyId("firstname");
		cbEmployee.setImmediate(true);
		cbEmployee.setNullSelectionAllowed(false);
		cbEmployee.setWidth("150");
		cbEmployee.setRequired(true);
		loadEmployeeList();
		dfPassDate = new GERPPopupDateField("Date");
		dfPassDate.setRequired(true);
		dfPassDate.setDateFormat("dd-MMM-yyyy");
		dfPassDate.setInputPrompt("Select Date");
		dfPassDate.setWidth("130px");
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
		flcol1.addComponent(dfPassDate);
		flcol1.addComponent(cbEmployee);
		flcol1.addComponent(cbDepartment);
		flcol2.addComponent(tfPlace);
		flcol2.addComponent(tfTimeIn);
		flcol2.addComponent(tfTimeOut);
		flcol3.addComponent(taPurpose);
		flcol4.addComponent(cbVehicle);
		flcol4.addComponent(tfVehicleNo);
		flcol4.addComponent(tfKMOut);
		flcol5.addComponent(tfKMIn);
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
		List<OutpassDM> list = new ArrayList<OutpassDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + null + "," + tfPlace.getValue() + ", " + (String) cbStatus.getValue());
		list = serviceOutpass.getOutpassList(null, (Long) cbEmployee.getValue(), null, null,
				(String) cbStatus.getValue());
		recordCnt = list.size();
		beanOutpass = new BeanItemContainer<OutpassDM>(OutpassDM.class);
		beanOutpass.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Outpass. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanOutpass);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "outpassId", "passDate", "place","vehicleNo", "outTime",
				"inTime", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Date", "Place", "Vehicle No","Out Time",
				"In Time", "Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("outpassId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Load Employee List
	private void loadEmployeeList() {
		BeanContainer<Long, EmployeeDM> beanInitiatedBy = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanInitiatedBy.setBeanIdProperty("employeeid");
		beanInitiatedBy.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null,
				null, null, "P"));
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
				+ outpassid);
		if (tblMstScrSrchRslt.getValue() != null) {
			OutpassDM outpassDM = beanOutpass.getItem(tblMstScrSrchRslt.getValue()).getBean();
			dfPassDate.setValue(outpassDM.getPassDate());
			cbEmployee.setValue(outpassDM.getEmployeeId());
			cbDepartment.setValue(outpassDM.getDeptId());
			cbStatus.setValue(outpassDM.getStatus());
			tfPlace.setValue(outpassDM.getPlace());
			tfTimeOut.setValue(outpassDM.getOutTime());
			tfTimeIn.setValue(outpassDM.getInTime());
			taPurpose.setValue(outpassDM.getPurpose());
			cbVehicle.setValue(outpassDM.getVehicle());
			tfVehicleNo.setValue(outpassDM.getVehicleNo());
			tfKMOut.setValue(outpassDM.getKmOut());
			tfKMIn.setValue(outpassDM.getKmIn());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		OutpassDM outpassDM = new OutpassDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			outpassDM = beanOutpass.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		outpassDM.setPassDate(dfPassDate.getValue());
		outpassDM.setEmployeeId((Long) cbEmployee.getValue());
		outpassDM.setDeptId((Long) cbDepartment.getValue());
		outpassDM.setStatus((String) cbStatus.getValue());
		outpassDM.setPlace(tfPlace.getValue());
		outpassDM.setOutTime(tfTimeOut.getValue());
		outpassDM.setInTime(tfTimeIn.getValue());
		outpassDM.setPurpose(taPurpose.getValue());
		outpassDM.setVehicle((String) cbVehicle.getValue());
		outpassDM.setVehicleNo(tfVehicleNo.getValue());
		outpassDM.setKmOut(tfKMOut.getValue());
		outpassDM.setKmIn(tfKMIn.getValue());
		outpassDM.setLastUpdatedBy(username);
		outpassDM.setLastUpdatedDt(DateUtils.getcurrentdate());
		serviceOutpass.saveOrUpdateOutpass(outpassDM);
		outpassid = outpassDM.getOutpassId();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		tfPlace.setValue("");
		tfPlace.setReadOnly(false);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		tfPlace.setReadOnly(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		tfPlace.setReadOnly(false);
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
		if ((dfPassDate.getValue() == null)) {
			dfPassDate.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfPassDate.getValue());
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbEmployee.getValue() + "," + ","
				+ "," + dfPassDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for outpassid " + outpassid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(outpassid));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hllayout1.removeAllComponents();
		tblMstScrSrchRslt.setValue(null);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		tfPlace.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		dfPassDate.setValue(new Date());
		tfPlace.setValue("");
		tfPlace.setComponentError(null);
		cbEmployee.setComponentError(null);
		cbEmployee.setValue(null);
		tfTimeIn.setValue(null);
		tfTimeOut.setValue(null);
		cbDepartment.setValue(null);
		dfPassDate.setValue(null);
		taPurpose.setValue("");
		tfVehicleNo.setValue("");
		tfKMOut.setValue("");
		tfKMIn.setValue("");
		cbVehicle.setValue(null);
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
			tfPlace.setReadOnly(false);
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
			parameterMap.put("ECRID", outpassid);
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