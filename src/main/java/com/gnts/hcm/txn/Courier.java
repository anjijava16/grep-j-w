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
import com.gnts.stt.dsn.domain.txn.CourierDM;
import com.gnts.stt.dsn.service.txn.CourierService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

public class Courier extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private CourierService serviceCourier = (CourierService) SpringContextHelper.getBean("courier");
	private DepartmentService serviceDepartment = (DepartmentService) SpringContextHelper.getBean("department");
	// Initialize the logger
	private Logger logger = Logger.getLogger(Courier.class);
	// User Input Fields for Courier
	private GERPPopupDateField dfRefDate;
	private GERPComboBox cbFromOrTo, cbCourierType, cbDepartment;
	private GERPTextField tfNoofDocuments, tfDocketNo, tfModeofPost, tfSentTo;
	private TextArea taRemarks, taAddress;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<CourierDM> beanCourier = null;
	// form layout for input controls Courier
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls Courier
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// local variables declaration
	private Long courierId;
	private String username;
	private Long companyid;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public Courier() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Courier() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting Courier UI");
		// EC Request Components Definition
		cbCourierType = new GERPComboBox("Type");
		cbCourierType.addItems("Inward", "Outward");
		cbCourierType.setWidth("150");
		cbCourierType.setRequired(true);
		tfNoofDocuments = new GERPTextField("No. of Documents");
		tfNoofDocuments.setWidth("150");
		tfSentTo = new GERPTextField("Sent To");
		tfSentTo.setWidth("150");
		tfSentTo.setRequired(true);
		tfModeofPost = new GERPTextField("Mode of Post");
		tfModeofPost.setWidth("180");
		tfDocketNo = new GERPTextField("Docket No");
		taRemarks = new GERPTextArea("Remarks");
		taRemarks.setWidth("180");
		taRemarks.setHeight("45px");
		taAddress = new GERPTextArea("Name & Address");
		taAddress.setRequired(true);
		taAddress.setWidth("180");
		taAddress.setHeight("45px");
		cbFromOrTo = new GERPComboBox("From/To");
		cbFromOrTo.setItemCaptionPropertyId("firstname");
		cbFromOrTo.setWidth("150");
		cbFromOrTo.setRequired(true);
		loadEmployeeList();
		cbDepartment = new GERPComboBox("Department");
		cbDepartment.setItemCaptionPropertyId("deptname");
		loadDeptList();
		cbDepartment.setWidth("150");
		dfRefDate = new GERPPopupDateField("Date");
		dfRefDate.setRequired(true);
		dfRefDate.setDateFormat("dd-MMM-yyyy");
		dfRefDate.setInputPrompt("Select Date");
		dfRefDate.setWidth("130px");
		cbStatus.setWidth("180");
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
		flcol1.addComponent(cbCourierType);
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
		flcol1.addComponent(cbCourierType);
		flcol1.addComponent(dfRefDate);
		flcol1.addComponent(tfNoofDocuments);
		flcol2.addComponent(taAddress);
		flcol2.addComponent(tfModeofPost);
		flcol3.addComponent(tfDocketNo);
		flcol3.addComponent(cbFromOrTo);
		flcol3.addComponent(cbDepartment);
		flcol4.addComponent(taRemarks);
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
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<CourierDM> list = new ArrayList<CourierDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + null + "," + dfRefDate.getValue() + ", " + (String) cbStatus.getValue());
			list = serviceCourier.getCourierList(null, null, (String) cbCourierType.getValue(), null, null,
					(String) cbStatus.getValue());
			recordCnt = list.size();
			beanCourier = new BeanItemContainer<CourierDM>(CourierDM.class);
			beanCourier.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Courier. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanCourier);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "courierId", "refDate", "courierType", "noOfDocuments",
					"modeofPost", "status", "lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Date", "Type", "No of Documents",
					"Mode of Post", "Status", "Last Updated date", "Last Updated by" });
			tblMstScrSrchRslt.setColumnAlignment("courierId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Employee List
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanInitiatedBy = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanInitiatedBy.setBeanIdProperty("employeeid");
			beanInitiatedBy.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null,
					null, null, "P"));
			cbFromOrTo.setContainerDataSource(beanInitiatedBy);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method to edit the values from table into fields to update process for VisitorPass
	private void ediVisitorpass() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hllayout.setVisible(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected ecrid -> "
					+ courierId);
			if (tblMstScrSrchRslt.getValue() != null) {
				CourierDM courierDM = beanCourier.getItem(tblMstScrSrchRslt.getValue()).getBean();
				courierId = courierDM.getCourierId();
				cbCourierType.setValue(courierDM.getCourierType());
				cbDepartment.setValue(courierDM.getDepartmentId());
				dfRefDate.setValue(courierDM.getRefDate());
				cbFromOrTo.setValue(courierDM.getFromOrTo());
				cbStatus.setValue(courierDM.getStatus());
				tfNoofDocuments.setValue(courierDM.getNoOfDocuments());
				tfModeofPost.setValue(courierDM.getModeofPost());
				tfDocketNo.setValue(courierDM.getDocketNumber());
				taRemarks.setValue(courierDM.getRemarks());
				tfSentTo.setValue(courierDM.getSendTo());
				taAddress.setValue(courierDM.getAddress());
			}
		}
		catch (Exception ex) {
			logger.info("load Earnings Details" + ex);
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		CourierDM courierDM = new CourierDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			courierDM = beanCourier.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		courierDM.setCourierType((String) cbCourierType.getValue());
		courierDM.setRefDate(dfRefDate.getValue());
		courierDM.setFromOrTo((Long) cbFromOrTo.getValue());
		courierDM.setStatus((String) cbStatus.getValue());
		courierDM.setNoOfDocuments(tfNoofDocuments.getValue());
		courierDM.setModeofPost(tfModeofPost.getValue());
		courierDM.setDocketNumber(tfDocketNo.getValue());
		courierDM.setRemarks(taRemarks.getValue());
		courierDM.setSendTo(tfSentTo.getValue());
		courierDM.setAddress(taAddress.getValue());
		courierDM.setDepartmentId((Long) cbDepartment.getValue());
		courierDM.setLastUpdatedBy(username);
		courierDM.setLastUpdatedDt(DateUtils.getcurrentdate());
		serviceCourier.saveOrUpdateDetails(courierDM);
		courierId = courierDM.getCourierId();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		cbCourierType.setValue(null);
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
		cbFromOrTo.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbCourierType.getValue() == null) {
			cbCourierType.setComponentError(new UserError(""));
			errorFlag = true;
		}
		if (dfRefDate.getValue() == null) {
			dfRefDate.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			errorFlag = true;
		}
		if (cbFromOrTo.getValue() == null) {
			cbFromOrTo.setComponentError(new UserError(""));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbFromOrTo.getValue() + "," + ","
				+ "," + dfRefDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for outpassid " + courierId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(courierId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hllayout1.removeAllComponents();
		tblMstScrSrchRslt.setValue(null);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		tfNoofDocuments.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		dfRefDate.setValue(new Date());
		tfNoofDocuments.setValue("");
		tfNoofDocuments.setComponentError(null);
		cbFromOrTo.setComponentError(null);
		cbFromOrTo.setValue(null);
		tfDocketNo.setValue(null);
		tfModeofPost.setValue(null);
		taRemarks.setValue("");
		tfSentTo.setValue("");
		cbDepartment.setValue(null);
		cbCourierType.setValue(null);
		taAddress.setValue("");
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
			tfNoofDocuments.setReadOnly(false);
		}
	}
	
	/*
	 * loadDeptList()-->this function is used for load the Department list
	 */
	private void loadDeptList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Department Search...");
			BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
			beanDepartment.setBeanIdProperty("deptid");
			beanDepartment.addAll(serviceDepartment.getDepartmentList(companyid, null, "Active", "P"));
			cbDepartment.setContainerDataSource(beanDepartment);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
			parameterMap.put("ECRID", courierId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/ecr"); // ecr is the name of my jasper
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