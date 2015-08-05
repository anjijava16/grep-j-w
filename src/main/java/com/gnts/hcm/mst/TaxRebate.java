/**
 * File Name 		: TaxRebate.java 
 * Description 		: this class is used for add/edit Tax Rebate details. 
 * Author 			: KAVITHA V M
 * Date 			: 30-July-2014	
 *
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. 
 * All rights reserved.
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 *
 * Version       Date           	Modified By               Remarks
 * 0.1         30-July-2014        	 KAVITHA V M	        Initial Version
 * 
 */
package com.gnts.hcm.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.ParameterService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.TaxRebateDM;
import com.gnts.hcm.service.mst.TaxRebateservice;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class TaxRebate extends BaseUI {
	// Bean Creation
	private TaxRebateservice serviceTaxRebate = (TaxRebateservice) SpringContextHelper.getBean("TaxRebate");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private ParameterService serviceParameter = (ParameterService) SpringContextHelper.getBean("parameter");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfEarnAmtFrom, tfEarnAmtTo, tfRebateAmt;
	private TextField tfFinalYr = new GERPTextField("Finance Year");
	private ComboBox cbGender, cbSectionCode;
	private ComboBox cbTaxRebateStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<TaxRebateDM> beanTaxRebateDM = null;
	// local variables declaration
	private String taxrebateid;
	private Long companyid, moduleId;
	private int recordCnt = 0;
	private String username;
	private Long earnAmtfrm = 0L;
	private Long earnAmtTo = 0L;
	// Initialize logger
	private Logger logger = Logger.getLogger(TaxRebate.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public TaxRebate() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Tax Rebate() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax Rebate UI");
		// Tax Name text field
		cbSectionCode = new GERPComboBox("Section Code");
		cbSectionCode.setWidth("150");
		cbSectionCode.setItemCaptionPropertyId("lookupname");
		loadGRDLvl();
		// Gender Combo Box
		cbGender = new GERPComboBox("Gender");
		cbGender.setWidth("150");
		cbGender.setItemCaptionPropertyId("lookupname");
		loadGenderType();
		// Final Year TextField
		tfFinalYr.setValue(serviceParameter.getParameterValue("FM_FINYEAR", companyid, null));
		tfFinalYr.setReadOnly(true);
		tfEarnAmtFrom = new TextField("Earn Amt From");
		tfEarnAmtFrom.setValue("0");
		tfEarnAmtTo = new TextField("Earn Amt To");
		tfEarnAmtTo.setValue("0");
		tfRebateAmt = new TextField("Rebate Amt");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn1.addComponent(cbSectionCode);
		flColumn2.addComponent(cbTaxRebateStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbSectionCode);
		flColumn1.addComponent(tfFinalYr);
		flColumn2.addComponent(cbGender);
		flColumn2.addComponent(tfRebateAmt);
		flColumn3.addComponent(tfEarnAmtFrom);
		flColumn3.addComponent(tfEarnAmtTo);
		flColumn4.addComponent(cbTaxRebateStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<TaxRebateDM> taxrebateList = new ArrayList<TaxRebateDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbSectionCode.getValue() + ", " + cbTaxRebateStatus.getValue());
		taxrebateList = serviceTaxRebate.getTaxRebateList(null, (String) cbSectionCode.getValue(), companyid,
				(String) cbTaxRebateStatus.getValue(), "F");
		recordCnt = taxrebateList.size();
		beanTaxRebateDM = new BeanItemContainer<TaxRebateDM>(TaxRebateDM.class);
		beanTaxRebateDM.addAll(taxrebateList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Tax Rebate. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanTaxRebateDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "taxrebateid", "sectioncode", "rebateamount", "earnamtfrom",
				"earnamtto", "rebatestatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Section Code", "Rebate Amount", "Earn Amt From",
				"Earn Amt To", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("taxrebateid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfFinalYr.setReadOnly(false);
		tfFinalYr.setValue(serviceParameter.getParameterValue("FM_FINYEAR", companyid, null));
		tfFinalYr.setReadOnly(true);
		cbSectionCode.setValue(null);
		cbGender.setValue(null);
		tfRebateAmt.setValue("0");
		tfEarnAmtFrom.setValue("0");
		tfEarnAmtFrom.setComponentError(null);
		tfEarnAmtTo.setValue("0");
		cbSectionCode.setComponentError(null);
		cbTaxRebateStatus.setValue(cbTaxRebateStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editTaxRebate() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		TaxRebateDM editTaxRebateDM = beanTaxRebateDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		taxrebateid = editTaxRebateDM.getTaxrebateid().toString();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Tax Rebate. Id -> "
				+ taxrebateid);
		if (editTaxRebateDM != null) {
			// Object editTaxRebate;
			if (editTaxRebateDM.getSectioncode() != null) {
				cbSectionCode.setValue(editTaxRebateDM.getSectioncode());
			}
			if (editTaxRebateDM.getFinyear() != null) {
				tfFinalYr.setReadOnly(false);
				tfFinalYr.setValue(editTaxRebateDM.getFinyear());
				tfFinalYr.setReadOnly(true);
			}
			cbTaxRebateStatus.setValue(editTaxRebateDM.getRebatestatus());
			cbGender.setValue(editTaxRebateDM.getGender());
			tfRebateAmt.setValue(editTaxRebateDM.getRebateamount().toString());
			tfEarnAmtFrom.setValue(editTaxRebateDM.getEarnamtfrom().toString());
			tfEarnAmtTo.setValue(editTaxRebateDM.getEarnamtto().toString());
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbTaxRebateStatus.setValue(cbTaxRebateStatus.getItemIds().iterator().next());
		cbSectionCode.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		cbSectionCode.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Tax. ID " + taxrebateid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_TAX);
		UI.getCurrent().getSession().setAttribute("audittablepk", taxrebateid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbSectionCode.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbSectionCode.setRequired(true);
		// reset the input controls to default value
		resetFields();
		editTaxRebate();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbSectionCode.setComponentError(null);
		tfEarnAmtFrom.setComponentError(null);
		Boolean errorFlag = false;
		if (cbSectionCode.getValue() == null) {
			cbSectionCode.setComponentError(new UserError(GERPErrorCodes.NULL_TAX));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbSectionCode.getValue());
			errorFlag = true;
		}
		earnAmtTo = Long.valueOf(tfEarnAmtFrom.getValue().toString());
		earnAmtfrm = Long.valueOf(tfEarnAmtTo.getValue().toString());
		if (earnAmtTo > earnAmtfrm) {
			tfEarnAmtTo.setComponentError(new UserError("Earn amount To is greater than to Earn amount From"));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			TaxRebateDM taxRebateObj = new TaxRebateDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				taxRebateObj = beanTaxRebateDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			taxRebateObj.setCompanyid(companyid);
			taxRebateObj.setFinyear(tfFinalYr.getValue().toString());
			if (cbGender.getValue() != null) {
				taxRebateObj.setGender((String) cbGender.getValue());
			}
			if (cbSectionCode.getValue() != null) {
				taxRebateObj.setSectioncode((String) cbSectionCode.getValue());
			}
			if (tfRebateAmt.getValue() != null) {
				taxRebateObj.setRebateamount(Long.valueOf(tfRebateAmt.getValue()));
			}
			if (tfEarnAmtFrom.getValue() != null) {
				taxRebateObj.setEarnamtfrom(Long.valueOf(tfEarnAmtFrom.getValue()));
			} else {
				taxRebateObj.setEarnamtfrom(new Long("0"));
			}
			if (tfEarnAmtTo.getValue() != null) {
				taxRebateObj.setEarnamtto(Long.valueOf(tfEarnAmtTo.getValue()));
			} else {
				taxRebateObj.setEarnamtto(new Long("0"));
			}
			if (cbTaxRebateStatus.getValue() != null) {
				taxRebateObj.setRebatestatus((String) (cbTaxRebateStatus.getValue()));
			}
			taxRebateObj.setLastupdateddt(DateUtils.getcurrentdate());
			taxRebateObj.setLastupdatedby(username);
			serviceTaxRebate.saveTaxRebateDetails(taxRebateObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadGRDLvl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
				"HC_TXSECCD"));
		cbSectionCode.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadGenderType()-->this function is used for load the gender type
	 */
	private void loadGenderType() {
		BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("cmplookupid");
		beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active", "BS_GENDER"));
		cbGender.setContainerDataSource(beanCompanyLookUp);
	}
}
