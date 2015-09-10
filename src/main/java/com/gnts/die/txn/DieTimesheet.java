package com.gnts.die.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.die.domain.txn.DieTimeSheetDM;
import com.gnts.die.service.txn.DieTimeSheetService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class DieTimesheet extends BaseUI {
	private DieTimeSheetService serviceTimeSheet = (DieTimeSheetService) SpringContextHelper.getBean("dieTimesheet");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	// form layout for input controls
	private FormLayout flLayout1, flLayout2, flLayout3, flLayout4, flLayout5, flLayout6;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfWorkhrs, tfRemarks;
	private ComboBox cbEmployee, cbWork, cbStatus;
	private GERPPopupDateField dfRefDate;
	// Bean container
	private BeanItemContainer<DieTimeSheetDM> beanTimeSheet = null;
	// local variables declaration
	private Long companyId, deptId;
	private String userName, stateid;
	private int recordCnt = 0;
	// Initialize Logger
	private Logger logger = Logger.getLogger(DieTimesheet.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public DieTimesheet() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		deptId = (Long) UI.getCurrent().getSession().getAttribute("deptId");
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside DieTimesheet() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Painting DieTimesheet UI");
		// State Name text field
		tfWorkhrs = new GERPTextField("Work Hrs.");
		tfWorkhrs.setWidth("50");
		tfWorkhrs.setVisible(false);
		tfRemarks = new GERPTextField("Remarks");
		tfRemarks.setVisible(false);
		// Only Four Integer only accept it
		tfRemarks.setMaxLength(4);
		// Country Name Combo Box
		cbEmployee = new GERPComboBox("Employee");
		cbEmployee.setWidth("130");
		cbEmployee.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		cbWork = new GERPComboBox("Work Type");
		loadLoolupList();
		dfRefDate = new GERPPopupDateField("Date");
		dfRefDate.setWidth("100");
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("100");
		// create form layouts to hold the input items
		flLayout1 = new FormLayout();
		flLayout2 = new FormLayout();
		flLayout3 = new FormLayout();
		flLayout4 = new FormLayout();
		flLayout5 = new FormLayout();
		flLayout6 = new FormLayout();
		// add the user input items into appropriate form layout
		flLayout1.addComponent(dfRefDate);
		flLayout2.addComponent(cbEmployee);
		flLayout3.addComponent(cbWork);
		flLayout4.addComponent(tfWorkhrs);
		flLayout5.addComponent(tfRemarks);
		flLayout6.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flLayout1);
		hlUserInputLayout.addComponent(flLayout2);
		hlUserInputLayout.addComponent(flLayout3);
		hlUserInputLayout.addComponent(flLayout4);
		hlUserInputLayout.addComponent(flLayout5);
		hlUserInputLayout.addComponent(flLayout6);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<DieTimeSheetDM> list = new ArrayList<DieTimeSheetDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
					+ companyId + ", " + tfWorkhrs.getValue() + ", " + (String) cbStatus.getValue());
			list = serviceTimeSheet.getDieTimeSheetList(null, (Long) cbEmployee.getValue(), null, dfRefDate.getValue(),
					null, (String) cbWork.getValue(), "F");
			recordCnt = list.size();
			beanTimeSheet = new BeanItemContainer<DieTimeSheetDM>(DieTimeSheetDM.class);
			beanTimeSheet.addAll(list);
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Got the State result set");
			tblMstScrSrchRslt.setContainerDataSource(beanTimeSheet);
			tblMstScrSrchRslt.setColumnAlignment("timesheetId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No. of Records:" + recordCnt);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "timesheetId", "empName", "workDate", "workDone",
					"workHours", "remarks", "status", "lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee", "Ref Date", "Work", "Hours",
					"Remarks", "Status", "Updated Date", "Updated By" });
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editStateDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Editing the selected record");
		tfRemarks.setVisible(true);
		tfWorkhrs.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			DieTimeSheetDM timeSheetDM = beanTimeSheet.getItem(tblMstScrSrchRslt.getValue()).getBean();
			dfRefDate.setValue(timeSheetDM.getWorkDate1());
			cbEmployee.setValue(timeSheetDM.getEmpId());
			cbWork.setValue(timeSheetDM.getWorkDone());
			tfWorkhrs.setValue(timeSheetDM.getWorkHours());
			tfRemarks.setValue(timeSheetDM.getRemarks());
			cbStatus.setValue(timeSheetDM.getStatus());
		}
	}
	
	private void loadLoolupList() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyId, null, "Active",
					"DIE_WORK"));
			cbWork.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanInitiatedBy = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanInitiatedBy.setBeanIdProperty("employeeid");
			beanInitiatedBy.addAll(serviceEmployee.getEmployeeList(null, null, null, null, companyId, null, null, null,
					null, "P"));
			cbEmployee.setContainerDataSource(beanInitiatedBy);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	// Base class implementations
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Resetting the UI controls");
		tfWorkhrs.setValue("");
		tfRemarks.setValue("");
		tfWorkhrs.setComponentError(null);
		tfRemarks.setComponentError(null);
		cbEmployee.setComponentError(null);
		dfRefDate.setComponentError(null);
		cbEmployee.setValue(null);
		dfRefDate.setValue(null);
		cbWork.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// BaseUI searchDetails() implementation
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfWorkhrs.setComponentError(null);
		tfWorkhrs.setValue("");
		cbEmployee.setComponentError(null);
		cbEmployee.setValue(null);
		dfRefDate.setValue(null);
		cbWork.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		tfRemarks.setVisible(true);
		tfWorkhrs.setVisible(true);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the
		// same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfWorkhrs.setRequired(true);
		cbEmployee.setRequired(true);
		dfRefDate.setRequired(true);
		// reset the input controls to default value
		cbEmployee.removeItem(0L);
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for State ID " + stateid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_STATE);
		UI.getCurrent().getSession().setAttribute("audittablepk", stateid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		tfRemarks.setVisible(false);
		tfWorkhrs.setVisible(false);
		tfWorkhrs.setRequired(false);
		cbEmployee.setRequired(false);
		dfRefDate.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		tfRemarks.setVisible(true);
		tfWorkhrs.setVisible(true);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfWorkhrs.setRequired(true);
		cbEmployee.setRequired(true);
		dfRefDate.setRequired(true);
		editStateDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		tfWorkhrs.setComponentError(null);
		cbEmployee.setComponentError(null);
		dfRefDate.setComponentError(null);
		if ((tfWorkhrs.getValue() == null) || tfWorkhrs.getValue().trim().length() == 0) {
			tfWorkhrs.setComponentError(new UserError(GERPErrorCodes.NULL_STATE_NAME));
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + tfWorkhrs.getValue());
		}
		if (cbEmployee.getValue() == null || (Long) cbEmployee.getValue() == 0L) {
			cbEmployee.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_COUNTRY));
			errorFlag = true;
			logger.warn("Company ID : " + cbEmployee + " | User Name : " + cbEmployee + " > "
					+ "Throwing ValidationException. Holiday Name is > " + cbEmployee.getValue());
		}
		if (dfRefDate.getValue() == null) {
			dfRefDate.setComponentError(new UserError("Select Date"));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	/*
	 * saveDetails()-->this function is used for save/update the records
	 */
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
			DieTimeSheetDM timeSheetDM = new DieTimeSheetDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				timeSheetDM = beanTimeSheet.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			timeSheetDM.setWorkDate(dfRefDate.getValue());
			timeSheetDM.setDeptId(deptId);
			timeSheetDM.setEmpId((Long) cbEmployee.getValue());
			timeSheetDM.setWorkDone((String) cbWork.getValue());
			timeSheetDM.setWorkHours(tfWorkhrs.getValue());
			timeSheetDM.setRemarks(tfRemarks.getValue());
			timeSheetDM.setStatus((String) cbStatus.getValue());
			timeSheetDM.setLastUpdatedBy(userName);
			timeSheetDM.setLastUpdatedDate(DateUtils.getcurrentdate());
			serviceTimeSheet.saveOrUpdateDetails(timeSheetDM);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
