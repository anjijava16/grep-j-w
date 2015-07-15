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
import com.gnts.asm.domain.txn.AssetDetailsDM;
import com.gnts.asm.domain.txn.XeroxDM;
import com.gnts.asm.service.txn.AssetDetailsService;
import com.gnts.asm.service.txn.XeroxService;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
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

public class Xerox extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	private XeroxService serviceXerox = (XeroxService) SpringContextHelper.getBean("xerox");
	private DepartmentService serviceDepartment = (DepartmentService) SpringContextHelper.getBean("department");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private AssetDetailsService serviceAssetDetail = (com.gnts.asm.service.txn.AssetDetailsService) SpringContextHelper
			.getBean("assetDetails");
	// Initialize the logger
	private Logger logger = Logger.getLogger(Xerox.class);
	// User Input Fields for EC Request
	private PopupDateField dfRefDate;
	private ComboBox cbAssetName;
	private ComboBox cbAssetAssignee;
	private ComboBox cbEmployeeName;
	private ComboBox cbDepartment;
	private TextField tfNoOfXerox, tfNoOfPrintouts, tfNoOfPapers;
	private TextArea taPurpose;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<XeroxDM> beanXerox = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// local variables declaration
	private Long xeroxRefId;
	private String username;
	private Long companyid;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public Xerox() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Xerox() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting Xerox UI");
		// EC Request Components Definition
		tfNoOfXerox = new TextField("No of Xerox");
		tfNoOfXerox.setWidth("150");
		tfNoOfPrintouts = new TextField("No of Printout");
		tfNoOfPrintouts.setWidth("150");
		tfNoOfPapers = new TextField("No of Papers");
		tfNoOfPapers.setWidth("150");
		tfNoOfPapers.setRequired(true);
		cbAssetAssignee = new GERPComboBox("Asset Assignee");
		cbAssetAssignee.setItemCaptionPropertyId("clientName");
		cbAssetAssignee.setRequired(true);
		cbDepartment = new GERPComboBox("Department");
		cbDepartment.setItemCaptionPropertyId("deptname");
		cbDepartment.setRequired(true);
		loadDepartmentList();
		taPurpose = new TextArea("Purpose");
		taPurpose.setWidth("90%");
		cbAssetName = new GERPComboBox("Xerox Machine");
		cbAssetName.setItemCaptionPropertyId("assetName");
		cbAssetName.setImmediate(true);
		cbAssetName.setNullSelectionAllowed(false);
		cbAssetName.setWidth("150");
		loadAssetList();
		dfRefDate = new GERPPopupDateField("Date");
		dfRefDate.setDateFormat("dd-MMM-yyyy");
		dfRefDate.setInputPrompt("Select Date");
		dfRefDate.setWidth("130px");
		cbStatus.setWidth("150px");
		cbEmployeeName = new GERPComboBox("Taken by");
		cbEmployeeName.setItemCaptionPropertyId("firstname");
		cbEmployeeName.setRequired(true);
		loadEmployeeName();
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
		flcol1.addComponent(cbAssetName);
		flcol2.addComponent(cbDepartment);
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
		flcol1.addComponent(cbAssetName);
		flcol2.addComponent(cbDepartment);
		flcol2.addComponent(cbEmployeeName);
		flcol3.addComponent(tfNoOfPrintouts);
		flcol3.addComponent(tfNoOfXerox);
		flcol4.addComponent(tfNoOfPapers);
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
				vlHeader.addComponent(taPurpose);
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
		List<XeroxDM> list = new ArrayList<XeroxDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + null + "," + null + ", " + (String) cbStatus.getValue());
		list = serviceXerox.getXeroxDetailList(null, (Long) cbAssetName.getValue(), (Long) cbDepartment.getValue(),
				null, null, null);
		recordCnt = list.size();
		beanXerox = new BeanItemContainer<XeroxDM>(XeroxDM.class);
		beanXerox.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Xerox. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanXerox);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "xeroxRefId", "copiesTakenDate", "assetName",
				"departmentName", "noOfXerox", "noOfPrintout", "noOfPapersUsed", "status", "lastupdateddt",
				"lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Date", "Asset Name", "Department", "No of Xerox",
				"No of Printout", "No of Papers", "Status", "Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("xeroxRefId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Load Enquiry List
	private void loadAssetList() {
		BeanContainer<Long, AssetDetailsDM> beanAssetDetails = new BeanContainer<Long, AssetDetailsDM>(
				AssetDetailsDM.class);
		beanAssetDetails.setBeanIdProperty("assetId");
		beanAssetDetails.addAll(serviceAssetDetail.getAssetDetailList(companyid, null, "PRINTER", null, null, null));
		cbAssetName.setContainerDataSource(beanAssetDetails);
	}
	
	// Load Product List
	private void loadEmployeeName() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null,
					null, null, "P"));
			cbEmployeeName.setContainerDataSource(beanEmployee);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * loadFromDeptList()-->this function is used for load the Department list
	 */
	private void loadDepartmentList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Department Search...");
		BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beanDepartment.setBeanIdProperty("deptid");
		beanDepartment.addAll(serviceDepartment.getDepartmentList(companyid, null, "Active", "P"));
		cbDepartment.setContainerDataSource(beanDepartment);
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editXerox() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected ecrid -> "
				+ xeroxRefId);
		if (tblMstScrSrchRslt.getValue() != null) {
			XeroxDM xeroxDM = beanXerox.getItem(tblMstScrSrchRslt.getValue()).getBean();
			xeroxRefId = xeroxDM.getXeroxRefId();
			cbAssetName.setValue(xeroxDM.getAssetId());
			cbEmployeeName.setValue(xeroxDM.getEmployeeId());
			cbAssetAssignee.setValue(xeroxDM.getAssetAssigneeRef());
			cbDepartment.setValue(xeroxDM.getDepartmentId());
			dfRefDate.setValue(xeroxDM.getCopiesTakenDate1());
			if (xeroxDM.getPurpose() != null) {
				taPurpose.setValue(xeroxDM.getPurpose());
			}
			if (xeroxDM.getNoOfPapersUsed() != null) {
				tfNoOfPrintouts.setValue(xeroxDM.getNoOfPapersUsed().toString());
			}
			if (xeroxDM.getNoOfXerox() != null) {
				tfNoOfXerox.setValue(xeroxDM.getNoOfXerox().toString());
			}
			if (xeroxDM.getNoOfPapersUsed() != null) {
				tfNoOfPapers.setValue(xeroxDM.getNoOfPapersUsed().toString());
			}
			cbStatus.setValue(xeroxDM.getStatus());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		XeroxDM xeroxDM = new XeroxDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			xeroxDM = beanXerox.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		xeroxDM.setAssetId((Long) cbAssetName.getValue());
		xeroxDM.setNoOfXerox(Long.valueOf(tfNoOfXerox.getValue()));
		xeroxDM.setNoOfPrintout(Long.valueOf(tfNoOfPrintouts.getValue()));
		xeroxDM.setNoOfPapersUsed(Long.valueOf(tfNoOfPapers.getValue()));
		xeroxDM.setDepartmentId((Long) cbDepartment.getValue());
		if (cbEmployeeName.getValue() != null) {
			xeroxDM.setEmployeeId((Long) cbEmployeeName.getValue());
		}
		xeroxDM.setAssetAssigneeRef((Long) cbAssetAssignee.getValue());
		xeroxDM.setCopiesTakenDate(dfRefDate.getValue());
		xeroxDM.setPurpose(taPurpose.getValue());
		xeroxDM.setStatus((String) cbStatus.getValue());
		xeroxDM.setLastupdatedby(username);
		xeroxDM.setLastupdateddt(DateUtils.getcurrentdate());
		serviceXerox.saveOrUpdateDetails(xeroxDM);
		xeroxRefId = xeroxDM.getXeroxRefId();
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		cbAssetName.setValue(null);
		cbDepartment.setValue(null);
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
		editXerox();
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
		if (cbDepartment.getValue() == null) {
			cbDepartment.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
			errorFlag = true;
		}
		if (cbEmployeeName.getValue() == null) {
			cbEmployeeName.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		if (tfNoOfPapers.getValue() == null || tfNoOfPapers.getValue().trim().length() == 0) {
			tfNoOfPapers.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
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
				+ "Getting audit record for enquiryId " + xeroxRefId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(xeroxRefId));
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
		cbAssetName.setComponentError(null);
		cbAssetName.setValue(null);
		cbEmployeeName.setValue(null);
		cbAssetAssignee.setValue(null);
		cbDepartment.setValue(null);
		dfRefDate.setValue(null);
		taPurpose.setValue("");
		cbStatus.setValue(null);
		dfRefDate.setValue(new Date());
		tfNoOfPapers.setValue("0");
		tfNoOfPrintouts.setValue("0");
		tfNoOfXerox.setValue("0");
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
			HashMap<String, String> parameterMap = new HashMap<String, String>();
			if (cbAssetName.getValue() != null) {
				parameterMap.put("assetid", cbAssetName.getValue().toString());
			}
			if (cbDepartment.getValue() != null) {
				parameterMap.put("deptid", cbDepartment.getValue().toString());
			}
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/xerox"); // xerox is the name of my jasper
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
