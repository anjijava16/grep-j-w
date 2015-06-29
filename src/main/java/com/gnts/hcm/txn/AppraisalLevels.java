/**
 * File Name 		: Appraisal Level.java 
 * Description 		: This UI screen  is used for Add AppraisalLevels Details. 
 * Author 			: GOKUL M
 * Date 			: Sep 20, 2014
 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * Version       Date           	Modified By               Remarks
 *   0.1       Sep 20 2014           GOKUL M	           Initial Version
 **/
package com.gnts.hcm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
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
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.AppraisalLevelsDM;
import com.gnts.hcm.service.txn.AppraisalLevelsService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class AppraisalLevels extends BaseUI {
	private static final long serialVersionUID = 1L;
	// Initialize Logger
	private Logger logger = Logger.getLogger(AppraisalLevels.class);
	private AppraisalLevelsService appraisallevelservice = (AppraisalLevelsService) SpringContextHelper
			.getBean("AppraisalLevels");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// Bean container
	private BeanItemContainer<AppraisalLevelsDM> beanAppraisalLevelsDM = null;
	private TextField txlevelname;
	private PopupDateField pfstrtdate;
	private PopupDateField pfenddate;
	private ComboBox cblvlstatus, cbapprasallvl, cbapprsalyr;
	private GERPTextArea taapprdetl;
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4;
	private HorizontalLayout hlsearchlayout;
	private HorizontalLayout hluserInputlayout = new HorizontalLayout();
	private String username;
	private Long companyid, moduleId;
	private int recordCnt;
	
	// Constructor
	public AppraisalLevels() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside AppraisalLevels() constructor");
		// Loading the UI
		buidview();
	}
	
	public void buidview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "building appraisallevel UI");
		txlevelname = new GERPTextField("Level Name");
		cbapprasallvl = new GERPComboBox("Appraisal Level");
		cbapprasallvl.setItemCaptionPropertyId("lookupname");
		loadappraisallvl();
		cbapprsalyr = new GERPComboBox("Appraisal Year");
		cbapprsalyr.setInputPrompt("Select Year");
		loadapprlist();
		pfstrtdate = new GERPPopupDateField("Start Date");
		pfstrtdate.setInputPrompt("Select Date");
		pfenddate = new GERPPopupDateField("End Date");
		pfenddate.setInputPrompt("Select Date");
		cblvlstatus = new GERPComboBox("Level Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		taapprdetl = new GERPTextArea("Comment");
		taapprdetl.setHeight("50");
		hlsearchlayout = new GERPAddEditHLayout();
		assemblsearch();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
	}
	
	public void assemblsearch() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search");
		hlsearchlayout.removeAllComponents();
		flcolumn1 = new GERPFormLayout();
		flcolumn2 = new GERPFormLayout();
		flcolumn3 = new GERPFormLayout();
		flcolumn4 = new GERPFormLayout();
		flcolumn1.addComponent(txlevelname);
		flcolumn2.addComponent(cbapprasallvl);
		flcolumn3.addComponent(cblvlstatus);
		hlsearchlayout.addComponent(flcolumn1);
		hlsearchlayout.addComponent(flcolumn2);
		hlsearchlayout.addComponent(flcolumn3);
		hlsearchlayout.setSizeUndefined();
		hlsearchlayout.setSpacing(true);
		hlsearchlayout.setMargin(true);
	}
	
	public void assemblUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		hluserInputlayout.removeAllComponents();
		flcolumn1 = new GERPFormLayout();
		flcolumn2 = new GERPFormLayout();
		flcolumn3 = new GERPFormLayout();
		flcolumn4 = new GERPFormLayout();
		flcolumn1.addComponent(txlevelname);
		txlevelname.setRequired(true);
		flcolumn1.addComponent(cbapprasallvl);
		cbapprasallvl.setRequired(true);
		flcolumn2.addComponent(cbapprsalyr);
		flcolumn2.addComponent(pfstrtdate);
		pfstrtdate.setWidth("130");
		flcolumn3.addComponent(pfenddate);
		pfenddate.setWidth("90");
		flcolumn3.addComponent(cblvlstatus);
		flcolumn4.addComponent(taapprdetl);
		taapprdetl.setValue("");
		hluserInputlayout.addComponent(flcolumn1);
		hluserInputlayout.addComponent(flcolumn2);
		hluserInputlayout.addComponent(flcolumn3);
		hluserInputlayout.setSpacing(true);
		hluserInputlayout.addComponent(flcolumn4);
		hluserInputlayout.setMargin(true);
	}
	
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.removeAllItems();
		List<AppraisalLevelsDM> AppraisalList = new ArrayList<AppraisalLevelsDM>();
		String levelname = txlevelname.getValue().toString();
		AppraisalList = appraisallevelservice.getAppraisalLevelsList(null, (String) cbapprasallvl.getValue(),
				levelname, (String) cblvlstatus.getValue(), "F");
		recordCnt = AppraisalList.size();
		beanAppraisalLevelsDM = new BeanItemContainer<AppraisalLevelsDM>(AppraisalLevelsDM.class);
		beanAppraisalLevelsDM.addAll(AppraisalList);
		tblMstScrSrchRslt.setContainerDataSource(beanAppraisalLevelsDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "apprlevelid", "levelname", "appraisallevel",
				"appraisaldetails", "levelstatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Level Name", "Appraisal Level", "Comment",
				"Level Status", "LastUpDated Date", "LastUpDated By" });
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Searching detail...");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			assemblsearch();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		txlevelname.setValue("");
		cbapprasallvl.setValue(null);
		cblvlstatus.setValue(cblvlstatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		assemblUserInputLayout();
		btnCancel.setVisible(true);
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Updating existing record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		assemblUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		editAppraisalLevel();
	}
	
	// Based on the selected record, the data would be included into user input fields in the input form
	private void editAppraisalLevel() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		tblMstScrSrchRslt.setVisible(true);
		hlCmdBtnLayout.setVisible(true);
		hluserInputlayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			AppraisalLevelsDM editapprlvllist = beanAppraisalLevelsDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cblvlstatus.setValue(editapprlvllist.getLevelstatus());
			txlevelname.setValue(editapprlvllist.getLevelname());
			cbapprasallvl.setValue(editapprlvllist.getAppraisallevel());
			cbapprsalyr.setValue(editapprlvllist.getAppraisalyear());
			if (editapprlvllist.getStartdate() != null) {
				pfstrtdate.setValue(editapprlvllist.getStartdate());
			}
			if (editapprlvllist.getEnddate() != null) {
				pfenddate.setValue(editapprlvllist.getEnddate());
			}
			if (editapprlvllist.getAppraisaldetails() != null) {
				taapprdetl.setValue(editapprlvllist.getAppraisaldetails());
			}
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		txlevelname.setComponentError(null);
		cbapprasallvl.setComponentError(null);
		boolean errorflag = false;
		if ((txlevelname.getValue() == null) || txlevelname.getValue().trim().length() == 0) {
			txlevelname.setComponentError(new UserError(GERPErrorCodes.NULL_LEVEL_NAME));
			errorflag = true;
		}
		if ((cbapprasallvl.getValue() == null)) {
			cbapprasallvl.setComponentError(new UserError(GERPErrorCodes.NULL_APPRAISAL_LEVEL));
			errorflag = true;
		}
		if ((pfstrtdate.getValue() != null) || (pfenddate.getValue() != null)) {
			if (pfstrtdate.getValue().after(pfenddate.getValue())) {
				pfenddate.setComponentError(new UserError(GERPErrorCodes.DATE_OUTOFRANGE));
				logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Throwing ValidationException. User data is > " + txlevelname.getValue() + ","
						+ cbapprsalyr.getValue());
				errorflag = true;
			}
		}
		if (errorflag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			AppraisalLevelsDM apprlvlsobj = new AppraisalLevelsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				apprlvlsobj = beanAppraisalLevelsDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			apprlvlsobj.setCompanyid(companyid);
			apprlvlsobj.setLevelname(txlevelname.getValue());
			if (cbapprsalyr.getValue() != null) {
				apprlvlsobj.setAppraisalyear(cbapprsalyr.getValue().toString());
			}
			if (cbapprasallvl.getValue() != null) {
				apprlvlsobj.setAppraisallevel(cbapprasallvl.getValue().toString());
			}
			if (cblvlstatus.getValue() != null) {
				apprlvlsobj.setLevelstatus(cblvlstatus.getValue().toString());
			}
			apprlvlsobj.setStartdate(pfstrtdate.getValue());
			apprlvlsobj.setEnddate(pfenddate.getValue());
			apprlvlsobj.setLastupdateddt(DateUtils.getcurrentdate());
			apprlvlsobj.setLastupdatedby(username);
			apprlvlsobj.setAppraisaldetails(taapprdetl.getValue());
			appraisallevelservice.saveOrUpdateAppraisalLevels(apprlvlsobj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		txlevelname.setRequired(false);
		cbapprasallvl.setRequired(false);
		assemblsearch();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "resetfields...");
		txlevelname.setValue("");
		txlevelname.setComponentError(null);
		cbapprasallvl.setValue(null);
		cbapprasallvl.setComponentError(null);
		cbapprsalyr.setValue(null);
		cbapprsalyr.setComponentError(null);
		pfstrtdate.setValue(null);
		pfstrtdate.setComponentError(null);
		pfenddate.setValue(null);
		pfenddate.setComponentError(null);
		taapprdetl.setValue("");
		cblvlstatus.setValue(cblvlstatus.getItemIds().iterator().next());
	}
	
	private void loadapprlist() {
		int i;
		int year = 1990;
		for (i = 0; i < 50; i++) {
			year = year + 1;
			cbapprsalyr.addItem(year + "");
			System.out.println("year is " + year);
		}
	}
	
	private void loadappraisallvl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading appraisal levels...");
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
				"HC_GRDLVL"));
		cbapprasallvl.setContainerDataSource(beanCompanyLookUp);
	}
}