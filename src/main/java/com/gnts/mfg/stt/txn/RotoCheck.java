package com.gnts.mfg.stt.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.BranchService;
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
import com.gnts.stt.mfg.domain.txn.RotoPlanHdrDM;
import com.gnts.stt.mfg.domain.txn.RotohdrDM;
import com.gnts.stt.mfg.service.txn.RotoPlanHdrService;
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

public  class RotoCheck extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private VisitPassService serviceVisitorPass = (VisitPassService) SpringContextHelper.getBean("visitPass");
	private BranchService servicebeanBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private RotoPlanHdrService serviceRotoplanhdr = (RotoPlanHdrService) SpringContextHelper.getBean("rotoplanhdr");
	// Initialize the logger
	private Logger logger = Logger.getLogger(RotoCheck.class);
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// User Input Fields for EC Request
	private GERPPopupDateField dfRotoDt;
	private GERPTextField tfRotoRef, tfPlanedQty, tfProdQty;
	private GERPComboBox cbBranch, cbPlanRef;
	private TextArea tfRemarks;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<RotohdrDM> beanRotohdr = null;
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
	public RotoCheck() {
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
		tfRotoRef = new GERPTextField("Total Time");
		tfRotoRef.setWidth("150");
		tfPlanedQty = new GERPTextField("Total Time");
		tfPlanedQty.setWidth("150");
		tfProdQty = new GERPTextField("Total Time");
		tfProdQty.setWidth("150");
		tfRemarks = new TextArea("Description");
		tfRemarks.setHeight("90px");
		cbPlanRef = new GERPComboBox("Materials");
		cbPlanRef.setRequired(true);
		cbPlanRef.setWidth("150");
		loadRotoPlanList();
		cbBranch = new GERPComboBox("Materials");
		cbBranch.setRequired(true);
		cbBranch.setWidth("150");
		loadBranchList();
		dfRotoDt = new GERPPopupDateField("Date");
		dfRotoDt.setRequired(true);
		dfRotoDt.setDateFormat("dd-MMM-yyyy");
		dfRotoDt.setInputPrompt("Select Date");
		dfRotoDt.setWidth("130px");
		cbStatus.setWidth("170");
		hlsearchlayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		//loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlsearchlayout.removeAllComponents();
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol1.addComponent(cbBranch);
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
		flcol1.addComponent(cbBranch);
		flcol1.addComponent(cbPlanRef);
		flcol1.addComponent(tfRotoRef);
		flcol1.addComponent(dfRotoDt);
		flcol2.addComponent(tfPlanedQty);
		flcol2.addComponent(tfProdQty);
		flcol2.addComponent(tfRemarks);
		flcol2.addComponent(cbStatus);
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
/*	private void loadSrchRslt() {
	}
	// Method to edit the values from table into fields to update process for VisitorPass
	private void ediVisitorpass() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected ecrid -> "
				+ visitorid);
		
		if (tblMstScrSrchRslt.getValue() != null) {
			RotohdrDM RotohdrDM = beanRotohdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			tfRotoRef = RotohdrDM.get
			tfPlanedQty.setValue(RotohdrDM.get
			tfProdQty.setValue((String)RotohdrDM.getProdtntotqty());
			cbStatus.setValue(RotohdrDM.getStatus());
			cbBranch.setValue(RotohdrDM.getVisitorName());
			cbPlanRef.setTime(RotohdrDM.getOutTime());
			tfRemarks.setTime(RotohdrDM.getInTime());
	
		}
	}
	*/
/*	@Override
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
	}*/
	
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
	
	/*
	 * loadBranchList()-->this function is used for load the branch name
	 */
	private void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, BranchDM> beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranchDM.setBeanIdProperty("branchId");
		beanBranchDM.addAll(servicebeanBranch.getBranchList(null, null, null, "Active", companyid, "P"));
		cbBranch.setContainerDataSource(beanBranchDM);
	}
	
	private void loadRotoPlanList() {
		List<RotoPlanHdrDM> PlanList = serviceRotoplanhdr.getRotoPlanHdrDetails(null, companyid, null, "Active");
		BeanItemContainer<RotoPlanHdrDM> beanrotoplanhdr = new BeanItemContainer<RotoPlanHdrDM>(RotoPlanHdrDM.class);
		beanrotoplanhdr.addAll(PlanList);
		cbPlanRef.setContainerDataSource(beanrotoplanhdr);
	}

	@Override
	protected void searchDetails() throws NoDataFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void resetSearchDetails() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addDetails() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void editDetails() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void validateDetails() throws ValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cancelDetails() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
		
	}
}