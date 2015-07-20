/**
 * File Name 		: AssetMaintDetail.java 
 * Description 		: this class is used for add/edit AssetMaintDetail  details. 
 * Author 			: P Priyanka
 * Date 			: Mar 12, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1          JUN 16 2014        	MOHAMED	        Initial Version
 * 0.2			18-Jun-2014			MOHAMED			Code re-factoring
 * 0.3			12-AUG-2014		MOHAMED			calling Constructor of document UI
 **/
package com.gnts.asm.txn;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.txn.AssetDetailsDM;
import com.gnts.asm.domain.txn.AssetMaintDetailDM;
import com.gnts.asm.domain.txn.AssetMaintSchedDM;
import com.gnts.asm.service.txn.AssetDetailsService;
import com.gnts.asm.service.txn.AssetMaintDetailService;
import com.gnts.asm.service.txn.AssetMaintSchedService;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.VendorService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTimeField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AssetMaintDetail extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	private BeanItemContainer<AssetMaintDetailDM> beanMaintDetail = null;
	// Buttons
	// Layouts
	private HorizontalLayout hlSearchLayout = new HorizontalLayout();
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// pagination
	private int recordCnt = 0;
	private String userName;
	private Long companyId, moduleId, employeeId;
	// Main Field Components
	private ComboBox cbStatus, cbMaintType, cbservicetype, cbAssetName, cbattenby, cbprepare, cbreviewby, cbMaint,
			cbserviceby, cbcause;
	private TabSheet tabasset;
	private TextField tfAssetName;
	private PopupDateField dfcompleteDate, dfservicedate, dfmainSchedule;
	private TextArea taMaintDetails, taProblemDesc;
	private GERPTimeField tfmaintime, tfcompletetime;
	// Initialization Logger
	private Logger logger = Logger.getLogger(AssetMaintDetail.class);
	private AssetMaintSchedService serviceMaintSched = (AssetMaintSchedService) SpringContextHelper
			.getBean("AssetMaintSchedul");
	private AssetDetailsService servicebeanAssetDetails = (AssetDetailsService) SpringContextHelper
			.getBean("assetDetails");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private AssetMaintDetailService serviceAssetMaintDetails = (AssetMaintDetailService) SpringContextHelper
			.getBean("assetMaintDetails");
	private VendorService servicevendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private CompanyLookupService servicecompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	
	// Constructor
	public AssetMaintDetail() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside AssetMaintDetail() constructor");
		// Loading the UI
		buildView();
	}
	
	// Loading BuildView Components
	private void buildView() {
		// comboBox for AssetName
		cbAssetName = new GERPComboBox("Asset Name");
		cbAssetName.setWidth("160");
		cbAssetName.setItemCaptionPropertyId("assetName");
		cbAssetName.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbAssetName.setComponentError(null);
				if (cbAssetName.getValue() != null) {
					cbAssetName.setComponentError(null);
				}
			}
		});
		loadAssetName();
		// TextField for Maintenance
		cbMaintType = new GERPComboBox("Maintenance Type", BASEConstants.T_AMS_ASST_MAINT_DTLS,
				BASEConstants.MAINT_TYPE);
		cbMaintType.setWidth("160");
		cbservicetype = new GERPComboBox("Service Type", BASEConstants.T_AMS_ASST_MAINT_DTLS, BASEConstants.SERVICE_TYP);
		cbservicetype.setWidth("160");
		// comboBox for PreparedBy
		cbprepare = new GERPComboBox("Prepared by");
		cbprepare.setWidth("160");
		cbprepare.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		// comboBox for AttendedBy
		cbattenby = new GERPComboBox("Attended by");
		cbattenby.setWidth("160");
		cbattenby.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		cbcause = new GERPComboBox("Caused by");
		cbcause.setWidth("160");
		cbcause.setItemCaptionPropertyId("lookupname");
		loadlookuplist();
		cbreviewby = new GERPComboBox("Reviewed by");
		cbreviewby.setWidth("160");
		cbreviewby.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		cbserviceby = new GERPComboBox("Service by");
		cbserviceby.setWidth("160");
		cbserviceby.setNullSelectionAllowed(false);
		cbserviceby.setItemCaptionPropertyId("vendorName");
		loadvendorlist();
		// comboBox for Maintenance Desription
		cbMaint = new GERPComboBox("Maintenance Desription ");
		cbMaint.setWidth("160");
		cbMaint.setItemCaptionPropertyId("maintaindescription");
		cbMaint.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbMaint.setComponentError(null);
				if (cbMaint.getValue() != null) {
					cbMaint.setComponentError(null);
				}
			}
		});
		loadmainDescriptionName();
		// PopUpDateField for CompleteDate
		dfcompleteDate = new GERPPopupDateField("Complete Date");
		dfcompleteDate.setDateFormat("dd-MMM-yyyy");
		dfcompleteDate.setWidth("140");
		// PopUpDateField for ServiceDate
		dfservicedate = new GERPPopupDateField("Service Date");
		dfservicedate.setDateFormat("dd-MMM-yyyy");
		dfservicedate.setWidth("140");
		// PopUpDateField for MainSchedule
		dfmainSchedule = new GERPPopupDateField("Maintain Schedule");
		dfmainSchedule.setWidth("140");
		cbStatus = new GERPComboBox("Status", BASEConstants.T_AMS_ASST_MAINT_DTLS, BASEConstants.MAINT_STS);
		cbStatus.setItemCaptionPropertyId("desc");
		cbStatus.setWidth("160");
		taMaintDetails = new GERPTextArea("Maintenance Detail");
		taMaintDetails.setWidth("900");
		taMaintDetails.setHeight("80");
		taProblemDesc = new GERPTextArea("Problem Desc.");
		taProblemDesc.setWidth("900");
		taProblemDesc.setHeight("80");
		tfmaintime = new GERPTimeField("MaintainTime");
		tfcompletetime = new GERPTimeField("Completion Time");
		tabasset = new TabSheet();
		tabasset.setSizeFull();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadsearchrslt();
		btnPrint.setVisible(true);
	}
	
	// Screen Search Fields
	private void assembleSearchLayout() {
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		// add the user input items into appropriate form layout
		flColumn1.addComponent(cbAssetName);
		flColumn2.addComponent(cbMaintType);
		flColumn3.addComponent(cbStatus);
		// add the user input items into appropriate Horizontal layout
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
	}
	
	// Screen Inserting DataComponents
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// add the form layouts into user input layout
		hlUserInputLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		// add the user input items into appropriate form layout
		flColumn1.addComponent(cbAssetName);
		cbAssetName.setRequired(true);
		flColumn1.addComponent(cbMaintType);
		flColumn1.addComponent(cbMaint);
		cbMaint.setRequired(true);
		flColumn1.addComponent(dfmainSchedule);
		flColumn2.addComponent(tfmaintime);
		flColumn2.addComponent(tfcompletetime);
		flColumn2.addComponent(dfcompleteDate);
		flColumn3.addComponent(dfservicedate);
		flColumn3.addComponent(cbservicetype);
		flColumn3.setMargin(true);
		flColumn3.addComponent(cbserviceby);
		flColumn4.addComponent(cbcause);
		flColumn4.addComponent(cbStatus);
		// add the user input items into appropriate Horizontal layout
		final HorizontalLayout hlUserInputLayout1 = new HorizontalLayout();
		hlUserInputLayout1.setSpacing(true);
		hlUserInputLayout1.addComponent(flColumn1);
		hlUserInputLayout1.addComponent(flColumn2);
		hlUserInputLayout1.addComponent(flColumn3);
		hlUserInputLayout1.addComponent(flColumn4);
		hlUserInputLayout.addComponent(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(hlUserInputLayout1);
				addComponent(taProblemDesc);
				addComponent(taMaintDetails);
			}
		});
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadsearchrslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			List<AssetMaintDetailDM> assetDetailList = new ArrayList<AssetMaintDetailDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
					+ companyId + ", " + cbAssetName.getValue() + ", " + (String) cbStatus.getValue());
			assetDetailList = serviceAssetMaintDetails.getAssetMaintDetailList(null, (Long) cbAssetName.getValue(),
					(String) cbMaintType.getValue(), null, (String) cbStatus.getValue());
			recordCnt = assetDetailList.size();
			beanMaintDetail = new BeanItemContainer<AssetMaintDetailDM>(AssetMaintDetailDM.class);
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Got the Asset Maintain Detail. result set");
			beanMaintDetail.addAll(assetDetailList);
			tblMstScrSrchRslt.setContainerDataSource(beanMaintDetail);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "maintId", "assetName", "maintenanceType",
					"maintenanceDt", "maintStatus", "lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Asset Name", "Maintenance Type",
					"Maintenance Date", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No. of Records:" + recordCnt);
			tblMstScrSrchRslt.setColumnAlignment("maintId", Align.RIGHT);
			logger.error("error during loadsearch on the table, The Error is ----->");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// this method use to Load AssetNamelist inside of ComboBox
	private void loadAssetName() {
		BeanContainer<Long, AssetDetailsDM> beanAssetDetails = new BeanContainer<Long, AssetDetailsDM>(
				AssetDetailsDM.class);
		beanAssetDetails.setBeanIdProperty("assetId");
		beanAssetDetails
				.addAll(servicebeanAssetDetails.getAssetDetailList(companyId, null, null, null, null, null, "Active"));
		cbAssetName.setContainerDataSource(beanAssetDetails);
	}
	
	// this method use to Load AssetNamelist inside of ComboBox
	private void loadmainDescriptionName() {
		BeanContainer<Long, AssetMaintSchedDM> beanMaintSched = new BeanContainer<Long, AssetMaintSchedDM>(
				AssetMaintSchedDM.class);
		beanMaintSched.setBeanIdProperty("maintId");
		beanMaintSched.addAll(serviceMaintSched.getMaintScheduleList(null, null,null, "Active", null, null));
		cbMaint.setContainerDataSource(beanMaintSched);
	}
	
	// this method use to Load Vendor Name list inside of ComboBox
	private void loadvendorlist() {
		BeanContainer<String, VendorDM> beanvendor = new BeanContainer<String, VendorDM>(VendorDM.class);
		beanvendor.setBeanIdProperty("vendorId");
		beanvendor.addAll(servicevendor.getVendorList(null, null, companyId, null, null, null, null, null, "Active",
				null, "P"));
		cbserviceby.setContainerDataSource(beanvendor);
	}
	
	// this method use to Load lookup Name list inside of ComboBox
	private void loadlookuplist() {
		BeanContainer<String, CompanyLookupDM> beanlook = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanlook.setBeanIdProperty("lookupname");
		beanlook.addAll(servicecompany.getCompanyLookUpByLookUp(companyId, moduleId, "Active", "AM_MNTCAUS"));
		cbcause.setContainerDataSource(beanlook);
	}
	
	// this method use to Load Employee Name list inside of ComboBox
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyId, employeeId,
					null, null, null, "P"));
			cbprepare.setContainerDataSource(beanEmployee);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editMaintDetail() {
		try {
			if (tblMstScrSrchRslt.getValue() != null) {
				AssetMaintDetailDM assetMaintDetailDM = beanMaintDetail.getItem(tblMstScrSrchRslt.getValue()).getBean();
				if (cbAssetName != null) {
					cbAssetName.setValue(assetMaintDetailDM.getAssetId());
				}
				cbMaintType.setValue(assetMaintDetailDM.getMaintenanceType());
				cbservicetype.setValue(assetMaintDetailDM.getServiceType());
				cbMaint.setValue(assetMaintDetailDM.getAssetMaintSchdId());
				cbcause.setValue(assetMaintDetailDM.getCausedBy());
				if (assetMaintDetailDM.getMaintDetails() != null && !"null".equals(assetMaintDetailDM.getMaintDetails())) {
					taMaintDetails.setValue(assetMaintDetailDM.getMaintDetails());
				}
				cbserviceby.setValue(assetMaintDetailDM.getServiceBy());
				dfservicedate.setValue(assetMaintDetailDM.getNextserviceDt());
				if (assetMaintDetailDM.getMaintenancetime() != null) {
					tfmaintime.setTime(assetMaintDetailDM.getMaintenancetime());
				}
				if (assetMaintDetailDM.getCompletedTime() != null) {
					tfcompletetime.setTime(assetMaintDetailDM.getCompletedTime());
				}
				dfcompleteDate.setValue(assetMaintDetailDM.getCompleteddt());
				dfmainSchedule.setValue(assetMaintDetailDM.getMaintenanceDt());
				taProblemDesc.setValue(assetMaintDetailDM.getProblemDescription());
				cbStatus.setValue(assetMaintDetailDM.getMaintStatus());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		loadsearchrslt();
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
		cbAssetName.setValue(null);
		resetFields();
		loadsearchrslt();
	}
	
	@Override
	protected void addDetails() {
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
	}
	
	@Override
	protected void editDetails() {
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editMaintDetail();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean errorflag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((cbAssetName.getValue() == null)) {
			cbAssetName.setComponentError(new UserError(GERPErrorCodes.NULL_ASSET_NAME));
			errorflag = true;
		}
		if ((cbMaint.getValue() == null)) {
			cbMaint.setComponentError(new UserError(GERPErrorCodes.NULL_ASSET_MAINTENANCE));
			errorflag = true;
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + tfAssetName.getValue() + tfAssetName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			AssetMaintDetailDM assetMaintDetailDM = new AssetMaintDetailDM();
			logger.info("Saving Data---->");
			if (tblMstScrSrchRslt.getValue() != null) {
				assetMaintDetailDM = beanMaintDetail.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (taMaintDetails.getValue() != null && taMaintDetails.getValue().trim().length() > 0) {
				assetMaintDetailDM.setMaintDetails(taMaintDetails.getValue().toString());
			}
			if (cbAssetName.getValue() != null) {
				assetMaintDetailDM.setAssetId((Long) cbAssetName.getValue());
			}
			if (cbMaintType.getValue() != null) {
				assetMaintDetailDM.setMaintenanceType(cbMaintType.getValue().toString());
			}
			if (tfmaintime.getValue() != null) {
				assetMaintDetailDM.setMaintenancetime(tfmaintime.getHorsMunites().toString());
			}
			if (dfservicedate.getValue() != null) {
				assetMaintDetailDM.setNextserviceDt(dfservicedate.getValue());
			}
			if (dfmainSchedule.getValue() != null) {
				assetMaintDetailDM.setMaintenanceDt(dfmainSchedule.getValue());
			}
			if (cbMaint.getValue() != null) {
				assetMaintDetailDM.setAssetMaintSchdId((Long) cbMaint.getValue());
			}
			assetMaintDetailDM.setMaintStatus((String) cbStatus.getValue());
			if (dfcompleteDate.getValue() != null) {
				assetMaintDetailDM.setCompleteddt(dfcompleteDate.getValue());
			}
			if (tfcompletetime.getValue() != null) {
				assetMaintDetailDM.setCompletedTime(tfcompletetime.getHorsMunites());
			}
			assetMaintDetailDM.setPreparedBy(employeeId);
			assetMaintDetailDM.setAttendedBy(null);
			assetMaintDetailDM.setReviewedBy(null);
			if (cbcause.getValue() != null) {
				assetMaintDetailDM.setCausedBy((String) cbcause.getValue());
			}
			if (cbserviceby.getValue() != null) {
				assetMaintDetailDM.setServiceBy(String.valueOf(cbserviceby.getValue()));
			}
			if (cbservicetype.getValue() != null) {
				assetMaintDetailDM.setServiceType(cbservicetype.getValue().toString());
			}
			assetMaintDetailDM.setProblemDescription(taProblemDesc.getValue());
			assetMaintDetailDM.setLastUpdatedDt(DateUtils.getcurrentdate());
			assetMaintDetailDM.setLastUpdatedBy(userName);
			serviceAssetMaintDetails.saveOrUpdateAssetMaintDetail(assetMaintDetailDM);
			resetFields();
			loadsearchrslt();
		}
		catch (Exception e) {
			logger.info("saveMaintDetail-->" + e);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for Client Case ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_AMS_ASST_MAINT_DTLS);
		UI.getCurrent().getSession().setAttribute("audittablepk", "");
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		cbAssetName.setRequired(false);
		resetFields();
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		cbMaintType.setValue(null);
		cbservicetype.setValue(null);
		cbprepare.setValue(null);
		cbreviewby.setValue(null);
		cbserviceby.setValue(null);
		cbattenby.setValue(null);
		cbcause.setValue(null);
		cbAssetName.setValue(null);
		cbAssetName.setComponentError(null);
		cbMaint.setValue(null);
		cbMaint.setComponentError(null);
		taMaintDetails.setValue("");
		taProblemDesc.setValue("");
		tfmaintime.setValue(null);
		dfcompleteDate.setValue(null);
		dfservicedate.setValue(null);
		dfmainSchedule.setValue(null);
		tfcompletetime.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
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
			try {
				parameterMap.put("startdate", null);
				parameterMap.put("enddate", null);
			}
			catch (Exception e) {
			}
			try {
				parameterMap.put("assetid", cbAssetName.getValue().toString());
			}
			catch (Exception e) {
			}
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/maintenancedtl"); // maintenancedtl is the name of my jasper
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
