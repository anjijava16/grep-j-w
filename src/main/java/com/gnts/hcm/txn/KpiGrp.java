/**
 * File Name 		: KpiGrp.java 
 * Description 		: this class is used for KPI group details. 
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

import java.math.BigDecimal;
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
import com.gnts.hcm.domain.mst.JobClassificationDM;
import com.gnts.hcm.domain.txn.KpiGroupDM;
import com.gnts.hcm.service.mst.JobClassificationService;
import com.gnts.hcm.service.txn.KpiGroupService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class KpiGrp extends BaseUI {
	private KpiGroupService serviceKpiGroup = (KpiGroupService) SpringContextHelper.getBean("KpiGroup");
	private JobClassificationService serviceJobClassification = (JobClassificationService) SpringContextHelper
			.getBean("JobClassification");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfKpigrpName, tfWeightage;
	private ComboBox cbJobClasfn, cbStatus;
	// Bean container
	private BeanItemContainer<KpiGroupDM> beanKpiGroupDM = null;
	private Long companyId;
	private String loginUserName;
	private int recordCnt = 0;
	private String primaryid;
	// Initialize Logger
	private Logger logger = Logger.getLogger(KpiGrp.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public KpiGrp() {
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Inside KpiGrp() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Painting KpiGrp UI");
		// text fields
		tfKpigrpName = new GERPTextField("KPI Group Name");
		tfWeightage = new GERPTextField("Weightage");
		// Combo Boxes
		cbJobClasfn = new GERPComboBox("Job classification");
		cbJobClasfn.setItemCaptionPropertyId("clasficatnName");
		loadJobClassification();
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
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
		flColumn1.addComponent(tfKpigrpName);
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
		flColumn1.addComponent(tfKpigrpName);
		flColumn2.addComponent(tfWeightage);
		flColumn3.addComponent(cbJobClasfn);
		flColumn4.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadJobClassification() {
		try {
			BeanContainer<Long, JobClassificationDM> bean = new BeanContainer<Long, JobClassificationDM>(
					JobClassificationDM.class);
			bean.setBeanIdProperty("jobClasfnId");
			bean.addAll(serviceJobClassification.getJobClassificationList(null, null, companyId, "Active", "F"));
			cbJobClasfn.setContainerDataSource(bean);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<KpiGroupDM> list = new ArrayList<KpiGroupDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Search Parameters are " + companyId + ", " + (Long) cbJobClasfn.getValue() + ", "
					+ (String) cbStatus.getValue());
			list = serviceKpiGroup.getkpigrouplist(tfKpigrpName.getValue(), null, companyId, null,
					(String) cbStatus.getValue(), "F");
			recordCnt = list.size();
			beanKpiGroupDM = new BeanItemContainer<KpiGroupDM>(KpiGroupDM.class);
			beanKpiGroupDM.addAll(list);
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Got the KPI Group List result set");
			tblMstScrSrchRslt.setContainerDataSource(beanKpiGroupDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "kpigrpid", "kpigroupname", "weightage", "grpstatus",
					"lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "KPI Group Name", "Weightage", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("kpigrpid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records:" + recordCnt);
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
		tfKpigrpName.setValue("");
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		tfKpigrpName.setRequired(true);
		tfWeightage.setRequired(true);
		cbJobClasfn.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	private void editKpiGroup() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "Editing the selected record");
			if (tblMstScrSrchRslt.getValue() != null) {
				KpiGroupDM kpiGroupDM = beanKpiGroupDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				if (kpiGroupDM.getKpigroupname() != null) {
					tfKpigrpName.setValue(kpiGroupDM.getKpigroupname());
				}
				if (kpiGroupDM.getWeightage() != null) {
					tfWeightage.setValue((kpiGroupDM.getWeightage()).toString());
				}
				if (kpiGroupDM.getGrpstatus() != null) {
					cbStatus.setValue(kpiGroupDM.getGrpstatus());
				}
				cbJobClasfn.setValue(kpiGroupDM.getJobclasfnid());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		tfKpigrpName.setRequired(true);
		tfWeightage.setRequired(true);
		cbJobClasfn.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editKpiGroup();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if ((tfKpigrpName.getValue() == null) || tfKpigrpName.getValue().trim().length() == 0) {
			tfKpigrpName.setComponentError(new UserError(GERPErrorCodes.NULL_KPI_GROUP_NAME));
			errorFlag = true;
		} else {
			tfKpigrpName.setComponentError(null);
		}
		if (cbJobClasfn.getValue() == null) {
			cbJobClasfn.setComponentError(new UserError(GERPErrorCodes.JOB_CLASSIFICATOIN));
			errorFlag = true;
		} else {
			cbJobClasfn.setComponentError(null);
		}
		if (tfWeightage.getValue() != null) {
			try {
				new BigDecimal(tfWeightage.getValue());
				tfWeightage.setComponentError(null);
			}
			catch (NumberFormatException e) {
				tfWeightage.setComponentError(new UserError(GERPErrorCodes.NULL_KPI_GRP_WEIGHT));
				errorFlag = true;
			}
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Saving Data... ");
		KpiGroupDM kpiGrpobj = new KpiGroupDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			kpiGrpobj = beanKpiGroupDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		kpiGrpobj.setCompanyid(companyId);
		kpiGrpobj.setKpigroupname(tfKpigrpName.getValue());
		kpiGrpobj.setJobclasfnid((Long) cbJobClasfn.getValue());
		kpiGrpobj.setWeightage(new BigDecimal(tfWeightage.getValue()));
		if (cbStatus.getValue() != null) {
			kpiGrpobj.setGrpstatus((String) cbStatus.getValue());
		}
		kpiGrpobj.setLastupdateddt(DateUtils.getcurrentdate());
		kpiGrpobj.setLastupdatedby(loginUserName);
		serviceKpiGroup.saveAndUpdate(kpiGrpobj);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Getting audit record for Kpi Group ID " + primaryid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_KPI_GROUP);
		UI.getCurrent().getSession().setAttribute("audittablepk", primaryid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Canceling action ");
		assembleSearchLayout();
		tfKpigrpName.setRequired(false);
		tfWeightage.setRequired(false);
		cbJobClasfn.setRequired(false);
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		tfKpigrpName.setValue("");
		tfWeightage.setValue("");
		cbJobClasfn.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfKpigrpName.setComponentError(null);
		tfWeightage.setComponentError(null);
		cbJobClasfn.setComponentError(null);
	}
}
