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
	private Long companyId, employeeId, assetMainId;
	// Main Field Components
	private ComboBox cbStatus, cbMaintType, cbServiceType, cbAssetName, cbAttenBy, cbPrepareBy, cbReviewBy, cbMaintSched,
			cbServiceby, cbCause;
	private TabSheet tabasset;
	private TextField tfAssetName;
	private PopupDateField dfCompleteDate, dfServicedate, dfMainSchedule;
	private TextArea taMaintDetails, taProblemDesc;
	private GERPTimeField tfMainTime, tfCompleteTime;
	// Initialization Logger
	private Logger logger = Logger.getLogger(AssetMaintDetail.class);
	private AssetMaintSchedService serviceMaintSched = (AssetMaintSchedService) SpringContextHelper
			.getBean("AssetMaintSchedul");
	private AssetDetailsService serviceAssetDetails = (AssetDetailsService) SpringContextHelper.getBean("assetDetails");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private AssetMaintDetailService serviceAssetMaintDetails = (AssetMaintDetailService) SpringContextHelper
			.getBean("assetMaintDetails");
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private CompanyLookupService serviceCompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	
	// Constructor
	public AssetMaintDetail() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside AssetMaintDetail() constructor");
		// Loading the UI
		buildView(true);
	}
	
	public AssetMaintDetail(Long assetMainId) {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside AssetMaintDetail() constructor");
		// Loading the UI
		buildView(false);
		this.assetMainId = assetMainId;
		loadSearchRslt();
		tblMstScrSrchRslt.setValue(tblMstScrSrchRslt.getItemIds().iterator().next());
		hlUserIPContainer.setVisible(true);
		hlUserIPContainer.setEnabled(true);
		hlSrchContainer.setVisible(false);
		btnPrint.setVisible(true);
		btnSave.setVisible(true);
		btnCancel.setVisible(true);
		btnSearch.setVisible(false);
		btnEdit.setEnabled(false);
		btnAdd.setEnabled(false);
		btnReset.setVisible(false);
		btnScreenName.setVisible(true);
		btnAuditRecords.setEnabled(true);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		hlUserIPContainer.removeAllComponents();
		// Dummy implementation, actual will be implemented in extended
		// class
		editDetails();
	}
	
	// Loading BuildView Components
	private void buildView(Boolean isLoadFullList) {
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
		cbServiceType = new GERPComboBox("Service Type", BASEConstants.T_AMS_ASST_MAINT_DTLS, BASEConstants.SERVICE_TYP);
		cbServiceType.setWidth("160");
		// comboBox for PreparedBy
		cbPrepareBy = new GERPComboBox("Prepared by");
		cbPrepareBy.setWidth("160");
		cbPrepareBy.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		// comboBox for AttendedBy
		cbAttenBy = new GERPComboBox("Attended by");
		cbAttenBy.setWidth("160");
		cbAttenBy.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		cbCause = new GERPComboBox("Caused by");
		cbCause.setWidth("160");
		cbCause.setItemCaptionPropertyId("lookupname");
		loadlookuplist();
		cbReviewBy = new GERPComboBox("Reviewed by");
		cbReviewBy.setWidth("160");
		cbReviewBy.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		cbServiceby = new GERPComboBox("Service by");
		cbServiceby.setWidth("160");
		cbServiceby.setNullSelectionAllowed(false);
		cbServiceby.setItemCaptionPropertyId("vendorName");
		loadVendorList();
		// comboBox for Maintenance Desription
		cbMaintSched = new GERPComboBox("Maintenance Desription ");
		cbMaintSched.setWidth("160");
		cbMaintSched.setItemCaptionPropertyId("maintaindescription");
		cbMaintSched.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbMaintSched.setComponentError(null);
				if (cbMaintSched.getValue() != null) {
					cbMaintSched.setComponentError(null);
				}
			}
		});
		loadMainSchedDesc();
		// PopUpDateField for CompleteDate
		dfCompleteDate = new GERPPopupDateField("Complete Date");
		dfCompleteDate.setWidth("140");
		// PopUpDateField for ServiceDate
		dfServicedate = new GERPPopupDateField("Service Date");
		dfServicedate.setWidth("140");
		// PopUpDateField for MainSchedule
		dfMainSchedule = new GERPPopupDateField("Maintain Schedule");
		dfMainSchedule.setWidth("140");
		cbStatus = new GERPComboBox("Status", BASEConstants.T_AMS_ASST_MAINT_DTLS, BASEConstants.MAINT_STS);
		cbStatus.setItemCaptionPropertyId("desc");
		cbStatus.setWidth("160");
		taMaintDetails = new GERPTextArea("Maintenance Detail");
		taMaintDetails.setWidth("900");
		taMaintDetails.setHeight("80");
		taProblemDesc = new GERPTextArea("Problem Desc.");
		taProblemDesc.setWidth("900");
		taProblemDesc.setHeight("80");
		tfMainTime = new GERPTimeField("MaintainTime");
		tfCompleteTime = new GERPTimeField("Completion Time");
		
		tabasset = new TabSheet();
		tabasset.setSizeFull();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		if (isLoadFullList) {
			loadSearchRslt();
		}
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
		cbMaintType.setRequired(true);
		flColumn1.addComponent(cbMaintType);
		flColumn1.addComponent(cbMaintSched);
		flColumn2.addComponent(dfMainSchedule);
		flColumn2.addComponent(tfMainTime);
		flColumn2.addComponent(tfCompleteTime);
		flColumn3.addComponent(dfCompleteDate);
		flColumn3.addComponent(dfServicedate);
		flColumn3.addComponent(cbServiceType);
		flColumn3.setMargin(true);
		flColumn4.addComponent(cbServiceby);
		flColumn4.addComponent(cbCause);
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
	private void loadSearchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			List<AssetMaintDetailDM> listAssetDetails = new ArrayList<AssetMaintDetailDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
					+ companyId + ", " + cbAssetName.getValue() + ", " + (String) cbStatus.getValue());
			listAssetDetails = serviceAssetMaintDetails.getAssetMaintDetailList(assetMainId,
					(Long) cbAssetName.getValue(), (String) cbMaintType.getValue(), null, (String) cbStatus.getValue());
			recordCnt = listAssetDetails.size();
			beanMaintDetail = new BeanItemContainer<AssetMaintDetailDM>(AssetMaintDetailDM.class);
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Got the Asset Maintain Detail. result set");
			beanMaintDetail.addAll(listAssetDetails);
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
			logger.info(e.getMessage());
		}
	}
	
	// this method use to Load AssetNamelist inside of ComboBox
	private void loadAssetName() {
		try {
			BeanContainer<Long, AssetDetailsDM> beanAssetDetails = new BeanContainer<Long, AssetDetailsDM>(
					AssetDetailsDM.class);
			beanAssetDetails.setBeanIdProperty("assetId");
			beanAssetDetails.addAll(serviceAssetDetails.getAssetDetailList(companyId, null, null, null, null, null,
					"Active"));
			cbAssetName.setContainerDataSource(beanAssetDetails);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// this method use to Load AssetNamelist inside of ComboBox
	private void loadMainSchedDesc() {
		try {
			BeanContainer<Long, AssetMaintSchedDM> beanMaintSched = new BeanContainer<Long, AssetMaintSchedDM>(
					AssetMaintSchedDM.class);
			beanMaintSched.setBeanIdProperty("maintId");
			beanMaintSched.addAll(serviceMaintSched.getMaintScheduleList(null, null, null, "Active", null, null));
			cbMaintSched.setContainerDataSource(beanMaintSched);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// this method use to Load Vendor Name list inside of ComboBox
	private void loadVendorList() {
		try {
			BeanContainer<String, VendorDM> beanvendor = new BeanContainer<String, VendorDM>(VendorDM.class);
			beanvendor.setBeanIdProperty("vendorId");
			beanvendor.addAll(serviceVendor.getVendorList(null, null, companyId, null, null, null, null, null,
					"Active", null, "P"));
			cbServiceby.setContainerDataSource(beanvendor);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// this method use to Load lookup Name list inside of ComboBox
	private void loadlookuplist() {
		try {
			BeanContainer<String, CompanyLookupDM> beanlook = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanlook.setBeanIdProperty("lookupname");
			beanlook.addAll(serviceCompany.getCompanyLookUpByLookUp(companyId, null, "Active", "AM_MNTCAUS"));
			cbCause.setContainerDataSource(beanlook);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// this method use to Load Employee Name list inside of ComboBox
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyId, employeeId,
					null, null, null, "P"));
			cbPrepareBy.setContainerDataSource(beanEmployee);
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
				cbServiceType.setValue(assetMaintDetailDM.getServiceType());
				cbMaintSched.setValue(assetMaintDetailDM.getAssetMaintSchdId());
				cbCause.setValue(assetMaintDetailDM.getCausedBy());
				if (assetMaintDetailDM.getMaintDetails() != null
						&& !"null".equals(assetMaintDetailDM.getMaintDetails())) {
					taMaintDetails.setValue(assetMaintDetailDM.getMaintDetails());
				}
				cbServiceby.setValue(assetMaintDetailDM.getServiceBy());
				dfServicedate.setValue(assetMaintDetailDM.getNextserviceDt());
				if (assetMaintDetailDM.getMaintenancetime() != null) {
					tfMainTime.setTime(assetMaintDetailDM.getMaintenancetime());
				}
				if (assetMaintDetailDM.getCompletedTime() != null) {
					tfCompleteTime.setTime(assetMaintDetailDM.getCompletedTime());
				}
				dfCompleteDate.setValue(assetMaintDetailDM.getCompleteddt());
				dfMainSchedule.setValue(assetMaintDetailDM.getMaintenanceDt());
				taProblemDesc.setValue(assetMaintDetailDM.getProblemDescription());
				cbStatus.setValue(assetMaintDetailDM.getMaintStatus());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		loadSearchRslt();
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
		loadSearchRslt();
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
		cbMaintType.setComponentError(null);
		cbAssetName.setComponentError(null);
		if ((cbAssetName.getValue() == null)) {
			cbAssetName.setComponentError(new UserError(GERPErrorCodes.NULL_ASSET_NAME));
			errorflag = true;
		}
		if ((cbMaintType.getValue() == null)) {
			cbMaintType.setComponentError(new UserError(GERPErrorCodes.NULL_ASSET_MAINTENANCE));
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
			if (tfMainTime.getValue() != null) {
				assetMaintDetailDM.setMaintenancetime(tfMainTime.getHorsMunites().toString());
			}
			if (dfServicedate.getValue() != null) {
				assetMaintDetailDM.setNextserviceDt(dfServicedate.getValue());
			}
			if (dfMainSchedule.getValue() != null) {
				assetMaintDetailDM.setMaintenanceDt(dfMainSchedule.getValue());
			}
			if (cbMaintSched.getValue() != null) {
				assetMaintDetailDM.setAssetMaintSchdId((Long) cbMaintSched.getValue());
			}
			assetMaintDetailDM.setMaintStatus((String) cbStatus.getValue());
			if (dfCompleteDate.getValue() != null) {
				assetMaintDetailDM.setCompleteddt(dfCompleteDate.getValue());
			}
			if (tfCompleteTime.getValue() != null) {
				assetMaintDetailDM.setCompletedTime(tfCompleteTime.getHorsMunites());
			}
			assetMaintDetailDM.setPreparedBy(employeeId);
			assetMaintDetailDM.setAttendedBy(null);
			assetMaintDetailDM.setReviewedBy(null);
			if (cbCause.getValue() != null) {
				assetMaintDetailDM.setCausedBy((String) cbCause.getValue());
			}
			if (cbServiceby.getValue() != null) {
				assetMaintDetailDM.setServiceBy(String.valueOf(cbServiceby.getValue()));
			}
			if (cbServiceType.getValue() != null) {
				assetMaintDetailDM.setServiceType(cbServiceType.getValue().toString());
			}
			assetMaintDetailDM.setProblemDescription(taProblemDesc.getValue());
			assetMaintDetailDM.setLastUpdatedDt(DateUtils.getcurrentdate());
			assetMaintDetailDM.setLastUpdatedBy(userName);
			serviceAssetMaintDetails.saveOrUpdateAssetMaintDetail(assetMaintDetailDM);
			resetFields();
			loadSearchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		cbMaintType.setRequired(false);
		resetFields();
		assetMainId = null;
		loadSearchRslt();
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		cbMaintType.setValue(null);
		cbServiceType.setValue(null);
		cbPrepareBy.setValue(null);
		cbReviewBy.setValue(null);
		cbServiceby.setValue(null);
		cbAttenBy.setValue(null);
		cbCause.setValue(null);
		cbAssetName.setValue(null);
		cbAssetName.setComponentError(null);
		cbMaintSched.setValue(null);
		cbMaintSched.setComponentError(null);
		taMaintDetails.setValue("");
		taProblemDesc.setValue("");
		tfMainTime.setValue(null);
		dfCompleteDate.setValue(null);
		dfServicedate.setValue(null);
		dfMainSchedule.setValue(null);
		tfCompleteTime.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbMaintType.setComponentError(null);
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
