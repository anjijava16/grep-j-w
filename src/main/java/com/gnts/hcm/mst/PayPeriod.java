/**
 * File Name 		: PayPeriod.java 
 * Description 		: this class is used for add/edit PayPeriod details. 
 * Author 			: MADHU T 
 * Date 			: 17-July-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         17-July-2014        	MADHU T		        Initial Version
 * 
 */
package com.gnts.hcm.mst;

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
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.PayPeriodDM;
import com.gnts.hcm.service.mst.PayPeriodService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class PayPeriod extends BaseUI {
	// Bean creation
	private PayPeriodService servicePayPeriod = (PayPeriodService) SpringContextHelper.getBean("PayPeriod");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flcolumn4, flcolumn5;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input control
	private TextField tfPayPeriodName, tfPayDays;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private ComboBox cbPayStDay, cbPayEdDay;
	private BeanItemContainer<PayPeriodDM> beanPayPeriodDM = null;
	// local variables declaration
	private Long companyid;
	private String payPeriodId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(PayPeriod.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public PayPeriod() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PayPeriod() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting PayPeriod UI");
		// PayPeriodName textfield
		tfPayPeriodName = new GERPTextField("Pay Period Name");
		tfPayPeriodName.setMaxLength(25);
		// Pay StartDay Combobox
		cbPayStDay = new GERPComboBox("Pay Start Day");
		cbPayStDay.setInputPrompt(null);
		for (int i = 1; i <= 31; i++) {
			cbPayStDay.addItem(i);
		}
		cbPayStDay.setRequired(true);
		cbPayStDay.setWidth("50");
		cbPayStDay.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				calculateDays();
			}
		});
		// Pay EndDay Combobox
		cbPayEdDay = new GERPComboBox("Pay End Day");
		cbPayEdDay.setInputPrompt(null);
		for (int i = 1; i <= 31; i++) {
			cbPayEdDay.addItem(i);
		}
		cbPayEdDay.setRequired(true);
		cbPayEdDay.setWidth("50");
		cbPayEdDay.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				calculateDays();
			}
		});
		// Total Working Hours
		tfPayDays = new GERPTextField("Pay Days");
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
		flColumn1.addComponent(tfPayPeriodName);
		flColumn2.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flcolumn4 = new FormLayout();
		flcolumn5 = new FormLayout();
		flColumn1.addComponent(tfPayPeriodName);
		flColumn2.addComponent(cbPayStDay);
		flColumn3.addComponent(cbPayEdDay);
		flcolumn4.addComponent(tfPayDays);
		flcolumn5.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flcolumn4);
		hlUserInputLayout.addComponent(flcolumn5);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<PayPeriodDM> listPayPeriod = new ArrayList<PayPeriodDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfPayPeriodName.getValue() + ", " + cbStatus.getValue());
			listPayPeriod = servicePayPeriod.getPayList(null, tfPayPeriodName.getValue(), null, null, companyid,
					(String) cbStatus.getValue(), "F");
			recordCnt = listPayPeriod.size();
			beanPayPeriodDM = new BeanItemContainer<PayPeriodDM>(PayPeriodDM.class);
			beanPayPeriodDM.addAll(listPayPeriod);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the PayPeriod Type. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanPayPeriodDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "payPeriodId", "periodName", "status",
					"lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Pay Period Name", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("payPeriodId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfPayPeriodName.setValue("");
		tfPayDays.setReadOnly(true);
		tfPayDays.setReadOnly(false);
		tfPayDays.setValue("0");
		tfPayDays.setReadOnly(true);
		cbPayEdDay.setValue(null);
		cbPayStDay.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editPayPeriod() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		try {
			hlUserInputLayout.setVisible(true);
			PayPeriodDM payPeriodDM = beanPayPeriodDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			payPeriodId = payPeriodDM.getPayPeriodId().toString();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected PayPeriod. Id -> " + payPeriodId);
			if (payPeriodDM.getPeriodName() != null) {
				tfPayPeriodName.setValue(payPeriodDM.getPeriodName());
			}
			tfPayDays.setReadOnly(false);
			if (payPeriodDM.getPayDays() != null) {
				tfPayDays.setValue(payPeriodDM.getPayDays().toString());
			}
			tfPayDays.setReadOnly(true);
			cbStatus.setValue(payPeriodDM.getStatus());
			int start = payPeriodDM.getPayStDay().intValue();
			cbPayStDay.select(start);
			int end = payPeriodDM.getPayEdDay().intValue();
			cbPayEdDay.select(end);
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
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfPayPeriodName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		assembleUserInputLayout();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfPayPeriodName.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for PayPeriod. ID " + payPeriodId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_PAYPERIOD);
		UI.getCurrent().getSession().setAttribute("audittablepk", payPeriodId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfPayPeriodName.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfPayPeriodName.setRequired(true);
		editPayPeriod();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfPayPeriodName.setComponentError(null);
		cbPayStDay.setComponentError(null);
		cbPayEdDay.setComponentError(null);
		Boolean errorFlag = false;
		if ((tfPayPeriodName.getValue() == null) || tfPayPeriodName.getValue().trim().length() == 0) {
			tfPayPeriodName.setComponentError(new UserError(GERPErrorCodes.NULL_PAY_PERIOD));
			errorFlag = true;
		}
		if (cbPayStDay.getValue() == null) {
			cbPayStDay.setComponentError(new UserError(GERPErrorCodes.NULL_PAYPERIOD_STDAY));
			errorFlag = true;
		}
		if (cbPayEdDay.getValue() == null) {
			cbPayEdDay.setComponentError(new UserError(GERPErrorCodes.NULL_PAYPERIOD_EDDAY));
			errorFlag = true;
		}
		if ((tfPayPeriodName.getValue() == null) || tfPayPeriodName.getValue().trim().length() == 0) {
			tfPayPeriodName.setComponentError(new UserError(GERPErrorCodes.NULL_PAY_PERIOD));
			errorFlag = true;
		} else if (tblMstScrSrchRslt.getValue() == null) {
			if (servicePayPeriod.getPayList(null, tfPayPeriodName.getValue(), null, null, companyid, "Active", "P")
					.size() > 0) {
				tfPayPeriodName.setComponentError(new UserError("Username Already Exist"));
				errorFlag = true;
			}
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			PayPeriodDM payPeriodDM = new PayPeriodDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				payPeriodDM = beanPayPeriodDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			payPeriodDM.setCmpId(companyid);
			payPeriodDM.setPeriodName(tfPayPeriodName.getValue());
			if (tfPayDays.getValue() != null) {
				payPeriodDM.setPayDays(Long.valueOf(tfPayDays.getValue()));
			}
			if (cbStatus.getValue() != null) {
				payPeriodDM.setStatus((String) cbStatus.getValue());
			}
			if (cbPayStDay.getValue() != null) {
				payPeriodDM.setPayStDay(Long.valueOf(cbPayStDay.getValue().toString()));
			}
			if (cbPayEdDay.getValue() != null) {
				payPeriodDM.setPayEdDay(Long.valueOf(cbPayEdDay.getValue().toString()));
			}
			payPeriodDM.setLastUpdatedDate(DateUtils.getcurrentdate());
			payPeriodDM.setLastUpdatedBy(username);
			servicePayPeriod.saveAndUpdate(payPeriodDM);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void calculateDays() {
		try {
			Long start = Long.valueOf(cbPayStDay.getValue().toString());
			Long end = Long.valueOf(cbPayEdDay.getValue().toString());
			Long tot = (end - start);
			tot++;
			if (start < end) {
				tfPayDays.setReadOnly(false);
				tfPayDays.setValue(tot.toString());
				tfPayDays.setReadOnly(true);
				cbPayEdDay.setComponentError(null);
			} else {
				cbPayEdDay.setComponentError(new UserError("End Day is Greater than the Start Day"));
				tfPayDays.setReadOnly(false);
				tfPayDays.setValue("");
				tfPayDays.setReadOnly(true);
			}
		}
		catch (NullPointerException e) {
			logger.info("Calculate Tatal Days " + e);
		}
	}
}
