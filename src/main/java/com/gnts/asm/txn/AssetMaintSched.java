/**
 * File Name 		: AssetMaintSched.java 
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
 * 0.3			18-AUG-2014		MOHAMED			calling Constructor document UI
 **/
package com.gnts.asm.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.txn.AssetDetailsDM;
import com.gnts.asm.domain.txn.AssetMaintSchedDM;
import com.gnts.asm.service.txn.AssetDetailsService;
import com.gnts.asm.service.txn.AssetMaintSchedService;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
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
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class AssetMaintSched extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CompanyLookupService servicecompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private BeanItemContainer<AssetMaintSchedDM> beanMaintSched = null;
	// Layouts
	private HorizontalLayout hlSearchLayout;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	private Label lblspace = new Label();
	// pagination
	private int recordCnt = 0;
	private Long companyId, moduleId, maintId;
	// Main Field Components
	private ComboBox cbAssetStatus, cbMaintFreq, cbAssetName;
	private TextField tfFreqPerDay, tfmaindesc;
	private PopupDateField maintenanceDt;
	private TextArea taRemarks;
	// Labels
	private String username, schedassetName;
	private AssetMaintSchedService serviceMaintSched = (AssetMaintSchedService) SpringContextHelper
			.getBean("AssetMaintSchedul");
	private AssetDetailsService servicebeanAssetDetails = (AssetDetailsService) SpringContextHelper
			.getBean("assetDetails");
	private Logger logger = Logger.getLogger(AssetMaintSched.class);
	
	// Constructor
	public AssetMaintSched() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > "
				+ "Inside ClientCategory() constructor");
		// Loading BuildView Components
		buildView();
	}
	
	private void buildView() {
		// comboBox for Asset Name
		cbAssetName = new GERPComboBox("Asset Name");
		cbAssetName.setWidth("147");
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
		// popupDateField for Maintenance
		maintenanceDt = new GERPPopupDateField("Maintenance Date");
		maintenanceDt.setDateFormat("dd-MMM-yyyy");
		maintenanceDt.setInputPrompt("Date");
		maintenanceDt.setImmediate(true);
		// TextField for Maintenance Frequency
		cbMaintFreq = new GERPComboBox("Maintenance Freq.");
		cbMaintFreq.setInputPrompt(" Frequency");
		cbMaintFreq.setItemCaptionPropertyId("lookupname");
		loadlookuplist();
		cbMaintFreq.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbMaintFreq.setComponentError(null);
				if (cbMaintFreq.getValue() != null) {
					cbMaintFreq.setComponentError(null);
				}
			}
		});
		// TextField for Frequency per Day
		tfFreqPerDay = new GERPTextField("Freq. per Day");
		tfFreqPerDay.setInputPrompt("Frequency per Day");
		tfFreqPerDay.setValue("0");
		tfFreqPerDay.setMaxLength(10);
		tfFreqPerDay.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfFreqPerDay.setComponentError(null);
				if (tfFreqPerDay.getValue() != null) {
					tfFreqPerDay.setComponentError(null);
				}
			}
		});
		// TextField for Maintenance Description
		tfmaindesc = new GERPTextField("Maintain Description");
		tfmaindesc.setInputPrompt("Description");
		tfmaindesc.setRequired(true);
		tfmaindesc.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfmaindesc.setComponentError(null);
				if (tfmaindesc.getValue() != null) {
					tfmaindesc.setComponentError(null);
				}
			}
		});
		// comboBox for Status
		cbAssetStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbAssetStatus.setItemCaptionPropertyId("desc");
		cbAssetStatus.setWidth("150");
		// TextArea for Remarks
		taRemarks = new GERPTextArea("Remarks");
		taRemarks.setInputPrompt(" Remarks");
		taRemarks.setWidth("140");
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void loadlookuplist() {
		BeanContainer<String, CompanyLookupDM> beanlook = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanlook.setBeanIdProperty("lookupname");
		beanlook.addAll(servicecompany.getCompanyLookUpByLookUp(companyId, moduleId, "Active", "AM_MNTFREQ"));
		cbMaintFreq.setContainerDataSource(beanlook);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + schedassetName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		// add the user input items into appropriate form layout
		flColumn1.addComponent(cbAssetName);
		cbAssetName.setRequired(false);
		flColumn2.addComponent(cbMaintFreq);
		cbMaintFreq.setRequired(false);
		flColumn3.addComponent(maintenanceDt);
		flColumn3.setSpacing(true);
		flColumn3.setMargin(true);
		flColumn4.addComponent(cbAssetStatus);
		// add the form layouts into user input Horizontal layout
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(lblspace);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + schedassetName + " > "
				+ "Assembling search layout");
		// add the form layouts into user input layout
		hlUserInputLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		// add the user input items into appropriate form layout
		flColumn1.addComponent(cbAssetName);
		cbAssetName.setRequired(true);
		flColumn1.addComponent(maintenanceDt);
		flColumn1.addComponent(cbMaintFreq);
		cbMaintFreq.setRequired(true);
		flColumn2.addComponent(tfmaindesc);
		flColumn2.addComponent(tfFreqPerDay);
		flColumn2.setSizeUndefined();
		tfFreqPerDay.setRequired(true);
		flColumn2.addComponent(cbAssetStatus);
		flColumn3.addComponent(taRemarks);
		flColumn3.setSizeUndefined();
		// add the form layouts into user input Horizontal layout
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
		// build search layout
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		// TODO Auto-generated method stub
		try {
			tblMstScrSrchRslt.removeAllItems();
			List<AssetMaintSchedDM> schedList = null;
			schedList = new ArrayList<AssetMaintSchedDM>();
			schedList = serviceMaintSched.getMaintScheduleList((Long) cbAssetName.getValue(), maintenanceDt.getValue(),
					null, (String) cbAssetStatus.getValue(), (String) cbMaintFreq.getValue(), null);
			recordCnt = schedList.size();
			beanMaintSched = new BeanItemContainer<AssetMaintSchedDM>(AssetMaintSchedDM.class);
			beanMaintSched.addAll(schedList);
			tblMstScrSrchRslt.setContainerDataSource(beanMaintSched);
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No. of Records:" + recordCnt);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "maintId", "assetName", "maintenanceFreq", "maintainDt",
					"maintaindescription", "maintStatus", "lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Asset Name", "Maintenance Freq.",
					"Maintenance Date", "maintain Description", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	// this method use to Load AssetNamelist inside of ComboBox
	private void loadAssetName() {
		BeanContainer<Long, AssetDetailsDM> beanAssetDetails = new BeanContainer<Long, AssetDetailsDM>(
				AssetDetailsDM.class);
		beanAssetDetails.setBeanIdProperty("assetId");
		beanAssetDetails.addAll(servicebeanAssetDetails.getAssetDetailList(companyId, null, schedassetName, null, null,
				"Active"));
		cbAssetName.setContainerDataSource(beanAssetDetails);
	}
	
	private void editMaintSchedule() {
		hlUserInputLayout.setVisible(true);
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Selected Dept. Id -> "
				+ maintId);
		if (tblMstScrSrchRslt.getValue() != null) {
			AssetMaintSchedDM editSched = beanMaintSched.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cbAssetName.setValue(editSched.getAssetId());
			maintenanceDt.setValue(editSched.getMaintainDtt());
			cbMaintFreq.setValue(editSched.getMaintenanceFreq());
			if (tfFreqPerDay.getValue() != null) {
				tfFreqPerDay.setValue(editSched.getDauFreq());
			}
			if (editSched.getRemarks() != null && !"null".equals(editSched.getRemarks())) {
				taRemarks.setValue(editSched.getRemarks());
			}
			tfmaindesc.setValue(editSched.getMaintaindescription());
			cbAssetStatus.setValue(cbAssetStatus.getItemIds().iterator().hasNext());
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + username + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbAssetName.setValue(null);
		taRemarks.setValue("");
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		// TODO Auto-generated method stub
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
	}
	
	@Override
	protected void editDetails() {
		// TODO Auto-generated method stub
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editMaintSchedule();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean errorflag = false;
		if ((cbAssetName.getValue() == null)) {
			cbAssetName.setComponentError(new UserError(GERPErrorCodes.NULL_ASSET_NAME));
			errorflag = true;
		}
		if ((cbMaintFreq.getValue() == null)) {
			cbMaintFreq.setComponentError(new UserError(GERPErrorCodes.NULL_ASST_FREQUENCY));
			errorflag = true;
		}
		if ((tfFreqPerDay.getValue() == null) || tfFreqPerDay.getValue().trim().length() == 0) {
			tfFreqPerDay.setComponentError(new UserError(GERPErrorCodes.FREQUENCY_PER_DAY));
			errorflag = true;
		}
		if ((tfmaindesc.getValue() == null) || tfmaindesc.getValue().trim().length() == 0) {
			tfmaindesc.setComponentError(new UserError(GERPErrorCodes.NULL_ASSET_MAINTENANCE));
			errorflag = true;
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyId + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbAssetName.getValue() + cbMaintFreq.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	// Saving UI components to DB
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > " + "Saving Data... ");
		AssetMaintSchedDM mainsched = new AssetMaintSchedDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			mainsched = beanMaintSched.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		mainsched.setAssetId((Long) cbAssetName.getValue());
		mainsched.setMaintainDt(maintenanceDt.getValue());
		mainsched.setMaintenanceFreq(cbMaintFreq.getValue().toString());
		mainsched.setDauFreq(tfFreqPerDay.getValue());
		mainsched.setMaintStatus((String) cbAssetStatus.getValue());
		mainsched.setRemarks(taRemarks.getValue());
		mainsched.setMaintaindescription(tfmaindesc.getValue());
		mainsched.setLastUpdatedDt(DateUtils.getcurrentdate());
		mainsched.setLastUpdatedBy(username);
		serviceMaintSched.saveOrUpdateMaintSched(mainsched);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + username + " > "
				+ "Getting audit record for Asset MainSchedule ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_AMS_ASSET_SCHEDULE);
		UI.getCurrent().getSession().setAttribute("audittablepk", "maintId");
	}
	
	@Override
	protected void cancelDetails() {
		// TODO Auto-generated method stub
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	// Defaults reseting the Fields
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
		cbAssetName.setValue(null);
		cbAssetName.setComponentError(null);
		cbAssetStatus.setValue(cbAssetStatus.getItemIds().iterator().next());
		tfFreqPerDay.setValue("");
		tfFreqPerDay.setComponentError(null);
		cbMaintFreq.setValue(null);
		cbMaintFreq.setComponentError(null);
		maintenanceDt.setValue(null);
		taRemarks.setValue("");
		tfmaindesc.setValue("");
		tfmaindesc.setComponentError(null);
	}
}
