/**
 * File Name 		: TaxLimit.java 
 * Description 		: this class is used for add/edit TaxLimit details. 
 * Author 			: MADHU T 
 * Date 			: 30-July-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. 
 * All rights reserved.
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         30-July-2014        	MADHU T		        Initial Version
 * 
 */
package com.gnts.hcm.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
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
import com.gnts.hcm.domain.mst.TaxDM;
import com.gnts.hcm.domain.txn.TaxLimitDM;
import com.gnts.hcm.service.mst.TaxService;
import com.gnts.hcm.service.txn.TaxLimitService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class TaxLimit extends BaseUI {
	// Bean creation
	private TaxLimitService serviceTaxLimit = (TaxLimitService) SpringContextHelper.getBean("TaxLimit");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private TaxService serviceTax = (TaxService) SpringContextHelper.getBean("Tax");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfExemptLimit;
	private ComboBox cbStatus, cbSectnCode, cbTaxId;
	// BeanItemContainer
	private BeanItemContainer<TaxLimitDM> beanTaxLimitDM = null;
	// local variables declaration
	private Long companyid, moduleId;
	private int sectionCount = 0;
	private String pkTaxLimitId;
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(TaxLimit.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public TaxLimit() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside TaxLimit() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting TaxLimit UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// TaxLimit Description text field
		tfExemptLimit = new GERPTextField("Exempt Limit");
		// TaxLimit Level text field
		cbSectnCode = new GERPComboBox("Section Code");
		cbSectnCode.setRequired(true);
		cbSectnCode.setItemCaptionPropertyId("lookupname");
		cbSectnCode.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				if (cbSectnCode.getValue() != null) {
					sectionCount = serviceTaxLimit.getCountBySectionCode((String) cbSectnCode.getValue(),
							(Long) cbTaxId.getValue());
					if (sectionCount != 0) {
						cbSectnCode.setComponentError(new UserError("Section code already exists"));
						errorFlag = true;
					}
				}
			}
		});
		loadGRDLvl();
		// Tax Name text field
		cbTaxId = new GERPComboBox("Tax Name");
		cbTaxId.setReadOnly(false);
		cbTaxId.setItemCaptionPropertyId("taxname");
		cbTaxId.setWidth("160");
		loadTaxList();
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		cbTaxId.setReadOnly(false);
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(cbTaxId);
		flColumn2.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
		cbTaxId.setRequired(false);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlSearchLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbTaxId);
		flColumn2.addComponent(cbSectnCode);
		flColumn3.addComponent(tfExemptLimit);
		flColumn4.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		cbTaxId.setRequired(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<TaxLimitDM> list = new ArrayList<TaxLimitDM>();
			Long TaxId = null;
			if (cbTaxId.getValue() != null) {
				TaxId = ((Long.valueOf(cbTaxId.getValue().toString())));
			}
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfExemptLimit.getValue() + ", " + cbTaxId.getValue()
					+ (String) cbStatus.getValue() + ", " + TaxId);
			list = serviceTaxLimit.getTaxLimitList(null, TaxId, (String) cbSectnCode.getValue(),
					(String) cbStatus.getValue(), "F");
			recordCnt = list.size();
			beanTaxLimitDM = new BeanItemContainer<TaxLimitDM>(TaxLimitDM.class);
			beanTaxLimitDM.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the TaxLimit. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanTaxLimitDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "taxLimitId", "taxName", "sctnCode", "exemptLimit",
					"status", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Tax Name", "Section code", "Exempt Limit",
					"Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("taxLimitId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfExemptLimit.setValue("0");
		cbSectnCode.setComponentError(null);
		cbTaxId.setComponentError(null);
		cbTaxId.setReadOnly(false);
		cbTaxId.setValue(null);
		cbSectnCode.setReadOnly(false);
		cbSectnCode.setValue(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editTaxLimit() {
		try {
			TaxLimitDM taxLimitDM = beanTaxLimitDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			pkTaxLimitId = taxLimitDM.getTaxLimitId().toString();
			if (taxLimitDM.getExemptLimit() != null && !"null".equals(taxLimitDM.getExemptLimit())) {
				tfExemptLimit.setValue(taxLimitDM.getExemptLimit().toString());
			}
			cbTaxId.setReadOnly(false);
			cbTaxId.setValue(Long.valueOf(taxLimitDM.getTaxId()));
			cbTaxId.setReadOnly(true);
			cbStatus.setValue(taxLimitDM.getStatus());
			cbSectnCode.setReadOnly(false);
			cbSectnCode.setValue(taxLimitDM.getSctnCode());
			cbSectnCode.setReadOnly(true);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbTaxId.setValue(null);
		// reset the field valued to default
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		resetFields();
		cbSectnCode.setReadOnly(false);
		cbTaxId.setReadOnly(false);
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Tax Limit ID " + pkTaxLimitId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_TAX_LIMIT);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkTaxLimitId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbTaxId.setReadOnly(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		cbSectnCode.setReadOnly(true);
		cbTaxId.setReadOnly(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editTaxLimit();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbTaxId.setComponentError(null);
		cbSectnCode.setComponentError(null);
		errorFlag = false;
		if (cbSectnCode.getValue() == null) {
			cbSectnCode.setComponentError(new UserError(GERPErrorCodes.NULL_SECTION_CODE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbSectnCode.getValue());
			errorFlag = true;
		}
		if (cbTaxId.getValue() == null) {
			cbTaxId.setComponentError(new UserError(GERPErrorCodes.NULL_TAX_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbTaxId.getValue());
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
			TaxLimitDM taxLimitDM = new TaxLimitDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				taxLimitDM = beanTaxLimitDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (cbStatus.getValue() != null) {
				taxLimitDM.setStatus((String) cbStatus.getValue());
			}
			if (cbSectnCode.getValue() != null) {
				taxLimitDM.setSctnCode((String) cbSectnCode.getValue());
			}
			if (tfExemptLimit.getValue() != null && tfExemptLimit.getValue().trim().length() > 0) {
				taxLimitDM.setExemptLimit(Long.valueOf(tfExemptLimit.getValue()));
			} else {
				taxLimitDM.setExemptLimit(new Long("0"));
			}
			if (cbTaxId.getValue() != null) {
				taxLimitDM.setTaxId(Long.valueOf(cbTaxId.getValue().toString()));
			}
			taxLimitDM.setLastupdateddt(DateUtils.getcurrentdate());
			taxLimitDM.setLastupdatedby(username);
			serviceTaxLimit.saveTaxDetails(taxLimitDM);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadGRDLvl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
					"HC_TXSECCD"));
			cbSectnCode.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadTaxList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
			BeanContainer<String, TaxDM> beanTax = new BeanContainer<String, TaxDM>(TaxDM.class);
			beanTax.setBeanIdProperty("taxid");
			beanTax.addAll(serviceTax.getTaxList(companyid, null, null, null, "P"));
			cbTaxId.setContainerDataSource(beanTax);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
