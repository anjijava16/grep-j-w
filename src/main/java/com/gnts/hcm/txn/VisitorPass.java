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
import com.gnts.stt.dsn.domain.txn.VisitPassDM;
import com.gnts.stt.dsn.service.txn.VisitPassService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

public class VisitorPass extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private VisitPassService serviceVisitorPass = (VisitPassService) SpringContextHelper.getBean("visitPass");
	// Initialize the logger
	private Logger logger = Logger.getLogger(VisitorPass.class);
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// User Input Fields for EC Request
	private GERPPopupDateField dfPassDate;
	private GERPComboBox cbEmployee, cbMaterialFlw;
	private GERPTextField tfVisitorsName;
	private GERPTimeField tfTimeIn, tfTimeOut;
	private GERPTextField tfTotalTime;
	private GERPTextField tfVehicleNo, tfCompanyName, tfContactNumber;
	private TextArea taRemarks, taMaterialDesc;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<VisitPassDM> beanVisitpass = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control LayouttaRemarksa
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
	public VisitorPass() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside VisitorPass() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting VisitorPass UI");
		// EC Request Components Definition
		tfTotalTime = new GERPTextField("Total Time");
		tfTotalTime.setWidth("150");
		taMaterialDesc = new TextArea("Description");
		taMaterialDesc.setHeight("90px");
		cbMaterialFlw = new GERPComboBox("Materials");
		cbMaterialFlw.setRequired(true);
		cbMaterialFlw.setWidth("170");
		loadMaterialflow();
		cbMaterialFlw.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getmatedescription();
			}
		});
		tfVisitorsName = new GERPTextField("Visitor's Name");
		tfVisitorsName.setWidth("150");
		tfVisitorsName.setRequired(true);
		tfVehicleNo = new GERPTextField("Vehicle No.");
		tfVehicleNo.setWidth("150");
		tfCompanyName = new GERPTextField("Company Name");
		tfCompanyName.setWidth("150");
		tfContactNumber = new GERPTextField("Contact Number");
		tfContactNumber.setWidth("150");
		tfContactNumber.setRequired(true);
		tfTimeIn = new GERPTimeField("Time In");
		tfTimeIn.setRequired(true);
		tfTimeIn.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getTotalHours();
			}
		});
		tfTimeOut = new GERPTimeField("Time Out");
		tfTimeOut.setRequired(true);
		tfTimeOut.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getTotalHours();
			}
		});
		taRemarks = new TextArea("Purpose");
		taRemarks.setHeight("90px");
		cbEmployee = new GERPComboBox("Person met");
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
		cbStatus.setWidth("170");
		hlsearchlayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
	}
	
	/*
	 * Load material flow type.
	 */
	private void loadMaterialflow() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"HC_MATFLW"));
			cbMaterialFlw.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Condition for product description
	 */
	private void getmatedescription() {
		if (cbMaterialFlw != null) {
			if (cbMaterialFlw.getValue().toString().equals("Inward")
					|| cbMaterialFlw.getValue().toString().equals("Outward")) {
				taMaterialDesc.setRequired(true);
			} else {
				taMaterialDesc.setRequired(false);
			}
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlsearchlayout.removeAllComponents();
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol1.addComponent(tfVisitorsName);
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
		flcol1.addComponent(dfPassDate);
		flcol1.addComponent(tfVisitorsName);
		flcol1.addComponent(tfContactNumber);
		flcol1.addComponent(tfCompanyName);
		flcol1.addComponent(tfVehicleNo);
		flcol2.addComponent(cbEmployee);
		flcol2.addComponent(tfTimeIn);
		flcol2.addComponent(tfTimeOut);
		flcol2.addComponent(tfTotalTime);
		flcol3.addComponent(cbMaterialFlw);
		flcol3.addComponent(taMaterialDesc);
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
	
	/*
	 * Total time calculation.
	 */
	private void getTotalHours() {
		// TODO Auto-generated method stub
		if (tfTimeIn.getValue() != null && tfTimeOut.getValue() != null) {
			if (tfTimeIn.getHorsMunitesinBigDecimal().compareTo(tfTimeOut.getHorsMunitesinBigDecimal()) < 0) {
				tfTotalTime.setValue(tfTimeIn.getHorsMunitesinBigDecimal()
						.subtract(tfTimeOut.getHorsMunitesinBigDecimal()).abs().toString());
			} else {
				tfTotalTime.setValue("0.0");
			}
		}
	}
	
	// Load EC Request
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<VisitPassDM> list = new ArrayList<VisitPassDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + null + "," + tfVisitorsName.getValue() + ", " + (String) cbStatus.getValue());
		list = serviceVisitorPass.getVisitPasList(null, tfVisitorsName.getValue(), null, null,
				(String) cbStatus.getValue());
		recordCnt = list.size();
		beanVisitpass = new BeanItemContainer<VisitPassDM>(VisitPassDM.class);
		beanVisitpass.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the VisitorPass. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanVisitpass);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "visitorId", "visitDate", "visitorName", "companyName",
				"inTime", "outTime", "lastUpdatedDt", "lastUpdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Date", "Visitor Name", "Company Name", "In Time",
				"Out Time", "Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("visitorId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
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
			VisitPassDM visitPassDM = beanVisitpass.getItem(tblMstScrSrchRslt.getValue()).getBean();
			visitorid = visitPassDM.getVisitorId();
			dfPassDate.setValue(visitPassDM.getVisitDate());
			cbEmployee.setValue(visitPassDM.getEmployeeId());
			cbStatus.setValue(visitPassDM.getStatus());
			tfVisitorsName.setValue(visitPassDM.getVisitorName());
			tfTimeOut.setTime(visitPassDM.getOutTime());
			tfTimeIn.setTime(visitPassDM.getInTime());
			taRemarks.setValue(visitPassDM.getRemarks());
			tfVehicleNo.setValue(visitPassDM.getVehicleNo());
			tfContactNumber.setValue(visitPassDM.getContactNo());
			tfCompanyName.setValue(visitPassDM.getCompanyName());
			tfTotalTime.setValue(visitPassDM.getTotalTime().toString());
			cbMaterialFlw.setValue(visitPassDM.getMateFLow());
			taMaterialDesc.setValue(visitPassDM.getMateDesc());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		VisitPassDM visitPassDM = new VisitPassDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			visitPassDM = beanVisitpass.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		visitPassDM.setVisitDate(dfPassDate.getValue());
		visitPassDM.setEmployeeId((Long) cbEmployee.getValue());
		visitPassDM.setStatus((String) cbStatus.getValue());
		visitPassDM.setVisitorName(tfVisitorsName.getValue());
		visitPassDM.setOutTime(tfTimeOut.getHorsMunites());
		visitPassDM.setInTime(tfTimeIn.getHorsMunites());
		visitPassDM.setRemarks(taRemarks.getValue());
		visitPassDM.setVehicleNo(tfVehicleNo.getValue());
		visitPassDM.setContactNo(tfContactNumber.getValue());
		visitPassDM.setCompanyName(tfCompanyName.getValue());
		visitPassDM.setLastUpdatedby(username);
		visitPassDM.setTotalTime(tfTotalTime.getValue());
		visitPassDM.setMateFLow(cbMaterialFlw.getValue().toString());
		visitPassDM.setMateDesc(taMaterialDesc.getValue());
		visitPassDM.setLastUpdatedDt(DateUtils.getcurrentdate());
		serviceVisitorPass.saveOrUpdateVisitPass(visitPassDM);
		visitorid = visitPassDM.getVisitorId();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		tfVisitorsName.setValue("");
		tfVisitorsName.setReadOnly(false);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		tfVisitorsName.setReadOnly(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		tfVisitorsName.setReadOnly(false);
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
		if (tfVisitorsName.getValue() == null || tfVisitorsName.getValue().trim().length() == 0) {
			tfVisitorsName.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorFlag = true;
		}
		if (dfPassDate.getValue() == null) {
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
		tfVisitorsName.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		dfPassDate.setValue(new Date());
		tfVisitorsName.setValue("");
		tfVisitorsName.setComponentError(null);
		cbEmployee.setComponentError(null);
		cbEmployee.setValue(null);
		tfTimeIn.setValue(null);
		tfTimeOut.setValue(null);
		dfPassDate.setValue(null);
		taRemarks.setValue("");
		tfVehicleNo.setValue("");
		tfContactNumber.setValue("");
		tfCompanyName.setValue("");
		tfTotalTime.setValue("0");
		taMaterialDesc.setValue("");
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
			tfVisitorsName.setReadOnly(false);
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