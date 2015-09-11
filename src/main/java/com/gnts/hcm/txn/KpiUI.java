/**
 * File Name 		: Kpi.java 
 * Description 		: this class is used for KPI details. 
 * Author 			: Abdullah.H
 * Date 			: 20-Sep-2014
 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.

 * Version       Date           	 Modified By               Remarks
 * 0.2          20-Sep-2014    		 Abdullah.H		          Intial Version
 * 
 */
package com.gnts.hcm.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.DesignationDM;
import com.gnts.hcm.domain.txn.KpiDM;
import com.gnts.hcm.domain.txn.KpiGroupDM;
import com.gnts.hcm.service.mst.DesignationService;
import com.gnts.hcm.service.txn.KpiGroupService;
import com.gnts.hcm.service.txn.KpiService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class KpiUI extends BaseUI {
	private KpiService serviceKpi = (KpiService) SpringContextHelper.getBean("Kpi");
	private KpiGroupService serviceKpiGroup = (KpiGroupService) SpringContextHelper.getBean("KpiGroup");
	private DesignationService serviceDesignation = (DesignationService) SpringContextHelper.getBean("Designation");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5, flColumn6;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfKpiName, tfMinRating, tfMaxRating;
	private ComboBox cbKpiGrp, cbDesignnation, cbStatus;
	// Bean container
	private BeanItemContainer<KpiDM> beanKpiDM = null;
	private Long companyId;
	private String loginUserName;
	private int recordCnt = 0;
	private String primaryid;
	// Initialize Logger
	private Logger logger = Logger.getLogger(KpiUI.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public KpiUI() {
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Inside KpiUI() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Painting Keep Performance Indiator UI");
		// text fields
		tfKpiName = new GERPTextField("KPI Name");
		tfMinRating = new GERPTextField("Min Rating");
		tfMinRating.setWidth("50px");
		tfMaxRating = new GERPTextField("Max Rating");
		tfMaxRating.setWidth("50px");
		// Combo Boxes
		cbKpiGrp = new GERPComboBox("KPI Group");
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("70px");
		cbKpiGrp.setItemCaptionPropertyId("kpigroupname");
		loadKpiGroup();
		cbDesignnation = new GERPComboBox("Designation");
		cbDesignnation.setItemCaptionPropertyId("designationName");
		loadDesignation();
		// create form layouts to hold the input items
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		// add the user input items into appropriate form layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn1.addComponent(tfKpiName);
		flColumn2.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		hlUserInputLayout.removeAllComponents();
		// Remove all components in Search Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn5 = new FormLayout();
		flColumn6 = new FormLayout();
		flColumn1.addComponent(tfKpiName);
		flColumn2.addComponent(cbDesignnation);
		flColumn3.addComponent(tfMinRating);
		flColumn4.addComponent(tfMaxRating);
		flColumn5.addComponent(cbKpiGrp);
		flColumn6.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.addComponent(flColumn5);
		hlUserInputLayout.addComponent(flColumn6);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<KpiDM> list = new ArrayList<KpiDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Search Parameters are " + companyId + ", " + (Long) cbKpiGrp.getValue() + ", "
					+ (String) cbStatus.getValue());
			list = serviceKpi.getKpiList(null, (Long) cbKpiGrp.getValue(), (String) tfKpiName.getValue(), null,
					(String) cbStatus.getValue(), "F");
			recordCnt = list.size();
			beanKpiDM = new BeanItemContainer<KpiDM>(KpiDM.class);
			beanKpiDM.addAll(list);
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Got the KPI List result set");
			tblMstScrSrchRslt.setContainerDataSource(beanKpiDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "kpiId", "kpiName", "minRate", "maxRate", "status",
					"lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "KPI Name", "Minimum Rating", "Maximum Rating",
					"Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("kpigrpid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records:" + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadKpiGroup() {
		try {
			BeanContainer<Long, KpiGroupDM> bean = new BeanContainer<Long, KpiGroupDM>(KpiGroupDM.class);
			bean.setBeanIdProperty("kpigrpid");
			bean.addAll(serviceKpiGroup.getkpigrouplist(null, null, companyId, null, "Active", "F"));
			cbKpiGrp.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadDesignation() {
		try {
			BeanContainer<Long, DesignationDM> bean = new BeanContainer<Long, DesignationDM>(DesignationDM.class);
			bean.setBeanIdProperty("designationId");
			bean.addAll(serviceDesignation.getDesignationList(null, null, null, null, companyId, "Active", "F"));
			cbDesignnation.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbKpiGrp.setValue(null);
		tfKpiName.setValue("");
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		tfKpiName.setRequired(true);
		tfMaxRating.setRequired(true);
		tfMinRating.setRequired(true);
		cbKpiGrp.setRequired(true);
		cbDesignnation.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	private void editKpi() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Editing the selected record");
			if (tblMstScrSrchRslt.getValue() != null) {
				KpiDM editKpiObj = beanKpiDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				if (editKpiObj.getKpiName() != null) {
					tfKpiName.setValue(editKpiObj.getKpiName());
				}
				cbKpiGrp.setValue(editKpiObj.getKpiGrpId());
				if (editKpiObj.getMaxRate() != null) {
					tfMaxRating.setValue(Double.valueOf(editKpiObj.getMaxRate()).toString());
				}
				if (editKpiObj.getMinRate() != null) {
					tfMinRating.setValue(Double.valueOf(editKpiObj.getMinRate()).toString());
				}
				if (editKpiObj.getStatus() != null) {
					cbStatus.setValue(editKpiObj.getStatus());
				}
				cbDesignnation.setValue(editKpiObj.getDesigId());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		tfKpiName.setRequired(true);
		tfMaxRating.setRequired(true);
		tfMinRating.setRequired(true);
		cbKpiGrp.setRequired(true);
		cbDesignnation.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editKpi();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if ((tfKpiName.getValue() == null) || tfKpiName.getValue().trim().length() == 0) {
			tfKpiName.setComponentError(new UserError(GERPErrorCodes.NULL_KPI_NAME));
			errorFlag = true;
		} else {
			tfKpiName.setComponentError(null);
		}
		if ((cbKpiGrp.getValue() == null)) {
			cbKpiGrp.setComponentError(new UserError(GERPErrorCodes.NULL_KPIGROUPNAME));
			errorFlag = true;
		} else {
			cbKpiGrp.setComponentError(null);
		}
		if ((cbDesignnation.getValue() == null)) {
			cbDesignnation.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DTLS_Designation));
			errorFlag = true;
		} else {
			cbDesignnation.setComponentError(null);
		}
		try {
			Long.valueOf(tfMinRating.getValue());
			tfMinRating.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfMinRating.setComponentError(new UserError(GERPErrorCodes.NUMBER));
			errorFlag = true;
		}
		try {
			Long.valueOf(tfMaxRating.getValue());
			tfMaxRating.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfMaxRating.setComponentError(new UserError(GERPErrorCodes.NUMBER));
			errorFlag = true;
		}
		if ((Long.valueOf(tfMaxRating.getValue())) >= (Long.valueOf(tfMinRating.getValue()))) {
			try {
				Long.valueOf(tfMinRating.getValue());
				tfMinRating.setComponentError(null);
			}
			catch (NumberFormatException e) {
				tfMinRating.setComponentError(new UserError(GERPErrorCodes.NULL_KPI_GRP_WEIGHT));
				errorFlag = true;
			}
		} else {
			tfMinRating.setComponentError(new UserError(GERPErrorCodes.NULL_KPI_MIN_VALUE));
			errorFlag = true;
		}
		if ((Long.valueOf(tfMaxRating.getValue())) >= (Long.valueOf(tfMinRating.getValue()))) {
			try {
				Long.valueOf(tfMaxRating.getValue());
				tfMaxRating.setComponentError(null);
			}
			catch (NumberFormatException e) {
				tfMaxRating.setComponentError(new UserError(GERPErrorCodes.NULL_KPI_GRP_WEIGHT));
				errorFlag = true;
			}
		} else {
			tfMaxRating.setComponentError(new UserError(GERPErrorCodes.NULL_KPI_MAX_VALUE));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Saving Data... ");
		KpiDM kpiobj = new KpiDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			kpiobj = beanKpiDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		kpiobj.setKpiName(tfKpiName.getValue());
		kpiobj.setKpiGrpId((Long) cbKpiGrp.getValue());
		kpiobj.setDesigId((Long) cbDesignnation.getValue());
		kpiobj.setMaxRate(Double.valueOf(tfMaxRating.getValue()));
		kpiobj.setMinRate(Double.valueOf(tfMinRating.getValue()));
		if (cbStatus.getValue() != null) {
			kpiobj.setStatus((String) cbStatus.getValue());
		}
		kpiobj.setLastUpdatedDt(DateUtils.getcurrentdate());
		kpiobj.setLastUpdatedBy(loginUserName);
		serviceKpi.saveItInvest(kpiobj);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Getting audit record for Kpi Group ID " + primaryid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_KPI);
		UI.getCurrent().getSession().setAttribute("audittablepk", primaryid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Canceling action ");
		assembleSearchLayout();
		tfKpiName.setRequired(false);
		tfMaxRating.setRequired(false);
		tfMinRating.setRequired(false);
		cbKpiGrp.setRequired(false);
		cbDesignnation.setRequired(false);
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbKpiGrp.setValue(null);
		cbDesignnation.setValue(null);
		tfKpiName.setValue("");
		tfMaxRating.setValue("");
		tfMinRating.setValue("");
		tfKpiName.setComponentError(null);
		tfMaxRating.setComponentError(null);
		tfMinRating.setComponentError(null);
		cbKpiGrp.setComponentError(null);
		cbDesignnation.setComponentError(null);
	}
}
