/**
 * File Name 		: Deduction.java 
 * Description 		: this class is used for add/edit Deduction details. 
 * Author 			: MADHU T 
 * Date 			: 21-July-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         21-July-2014        	MADHU T		        Initial Version
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
import com.gnts.hcm.domain.mst.DeductionDM;
import com.gnts.hcm.domain.mst.EarningsDM;
import com.gnts.hcm.service.mst.DeductionService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Table.Align;

public class Deduction extends BaseUI {
	// Bean creation
	private DeductionService serviceDeduction = (DeductionService) SpringContextHelper.getBean("Deduction");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3, flColumn5;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfDeduDesc, tfDeduPercent, tfDeduCode;
	private ComboBox cbStatus;
	private CheckBox chkAppAllGRD;
	private Boolean errorFlag = false;
	// BeanItemContainer
	private BeanItemContainer<DeductionDM> beanDeductionDM = null;
	// local variables declaration
	private Long companyid;
	private String pkDeductionId;
	private int recordCnt = 0;
	private int dedncodeCount = 0;
	private Long dednper;
	private String username, getdeductionCode;
	// Initialize logger
	private Logger logger = Logger.getLogger(Deduction.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Deduction() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Deduction() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	@SuppressWarnings("deprecation")
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Deduction UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Deduction Description text field
		tfDeduDesc = new GERPTextField("Deduction Description");
		// Deduction Percent Textfield
		tfDeduPercent = new GERPTextField("Deduction Percent");
		tfDeduPercent.setWidth("70");
		tfDeduPercent.setValue("0");
		tfDeduPercent.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				percentValidation();
			}
		});
		// Deduction Code text field
		tfDeduCode = new GERPTextField("Deduction Code");
		tfDeduCode.setWidth("50");
		tfDeduCode.addListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getdeductionCode = tfDeduCode.getValue().toString();
				dedncodeCount = serviceDeduction.countDeductionCode(getdeductionCode, companyid);
				if (dedncodeCount == 0) {
					tfDeduCode.setComponentError(null);
				} else {
					tfDeduCode.setComponentError(new UserError("Deduction code already Exist"));
				}
			}
		});
		// ApplyAllGrade CheckBox
		chkAppAllGRD = new CheckBox();
		chkAppAllGRD.setCaption("Apply all grade?");
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
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(tfDeduCode);
		flColumn2.addComponent(tfDeduDesc);
		flColumn3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
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
		flColumn5 = new FormLayout();
		flColumn1.addComponent(tfDeduDesc);
		flColumn2.addComponent(tfDeduCode);
		flColumn3.addComponent(tfDeduPercent);
		flColumn4.addComponent(chkAppAllGRD);
		flColumn5.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.addComponent(flColumn5);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<DeductionDM> deductionList = new ArrayList<DeductionDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfDeduDesc.getValue() + ", " + tfDeduCode.getValue()
				+ (String) cbStatus.getValue());
		deductionList = serviceDeduction.getDuctionList(null, tfDeduCode.getValue(), companyid, tfDeduDesc.getValue(),
				(String) cbStatus.getValue(), "F");
		recordCnt = deductionList.size();
		beanDeductionDM = new BeanItemContainer<DeductionDM>(DeductionDM.class);
		beanDeductionDM.addAll(deductionList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Deduction. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanDeductionDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "deductionId", "deductionCode", "deducnDesc", "status",
				"lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Deduction Code", "Deduction Desc", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("deductionId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfDeduDesc.setValue("");
		tfDeduCode.setValue("");
		tfDeduPercent.setValue("0");
		chkAppAllGRD.setValue(false);
		tfDeduDesc.setComponentError(null);
		tfDeduCode.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editDeduction() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		DeductionDM editDeduction = beanDeductionDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		pkDeductionId = editDeduction.getDeductionId().toString();
		if (editDeduction.getDeducnDesc() != null) {
			tfDeduDesc.setValue(itselect.getItemProperty("deducnDesc").getValue().toString());
		}
		if (editDeduction.getDeductionCode() != null) {
			tfDeduCode.setValue(itselect.getItemProperty("deductionCode").getValue().toString());
		}
		if (editDeduction.getDedcnPercent() != null) {
			tfDeduPercent.setValue(itselect.getItemProperty("dedcnPercent").getValue().toString());
		}
		if (editDeduction.getAppAllGRD().equals("Y")) {
			chkAppAllGRD.setValue(true);
		} else {
			chkAppAllGRD.setValue(false);
		}
		cbStatus.setValue(itselect.getItemProperty("status").getValue());
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
		tfDeduDesc.setValue("");
		tfDeduCode.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
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
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfDeduDesc.setRequired(true);
		tfDeduCode.setRequired(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for deduction. ID " + pkDeductionId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_DEDUCTION);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkDeductionId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfDeduDesc.setRequired(false);
		tfDeduCode.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		tfDeduDesc.setRequired(true);
		tfDeduCode.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editDeduction();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfDeduDesc.setComponentError(null);
		tfDeduCode.setComponentError(null);
		errorFlag = false;
		if ((tfDeduDesc.getValue() == null) || tfDeduDesc.getValue().trim().length() == 0) {
			tfDeduDesc.setComponentError(new UserError(GERPErrorCodes.NULL_DEDUCTION_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfDeduDesc.getValue());
			errorFlag = true;
		}
		if ((tfDeduCode.getValue() == null) || tfDeduCode.getValue().trim().length() == 0) {
			tfDeduCode.setComponentError(new UserError(GERPErrorCodes.NULL_DEDUCTION_CODE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfDeduCode.getValue());
			errorFlag = true;
		}
		DeductionDM deductionObj = new DeductionDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			deductionObj = beanDeductionDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if ((tfDeduCode.getValue() != null) && deductionObj.getDeductionId() == null) {
			if (serviceDeduction.getDuctionList(null, tfDeduCode.getValue(), companyid, null, "Active", "P").size() > 0) {
				tfDeduCode.setComponentError(new UserError("Deduction Code already Exist"));
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
		DeductionDM deductionObj = new DeductionDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			deductionObj = beanDeductionDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if (tfDeduDesc.getValue() != null) {
			deductionObj.setDeducnDesc(tfDeduDesc.getValue().toString());
		}
		if (tfDeduCode.getValue() != null) {
			deductionObj.setDeductionCode(tfDeduCode.getValue().toString());
		}
		if (tfDeduPercent.getValue() != null && tfDeduPercent.getValue().trim().length() > 0) {
			deductionObj.setDedcnPercent(Long.valueOf(tfDeduPercent.getValue()));
		} else {
			deductionObj.setDedcnPercent(new Long("0"));
		}
		if (chkAppAllGRD.getValue().equals(true)) {
			deductionObj.setAppAllGRD("Y");
		} else if (chkAppAllGRD.getValue().equals(false)) {
			deductionObj.setAppAllGRD("N");
		}
		deductionObj.setCmpId(companyid);
		if (cbStatus.getValue() != null) {
			deductionObj.setStatus((String) cbStatus.getValue());
		}
		deductionObj.setLastUpdatedDate(DateUtils.getcurrentdate());
		deductionObj.setLastUpdatedBy(username);
		serviceDeduction.saveAndUpdate(deductionObj);
		resetFields();
		loadSrchRslt();
	}
	
	private void percentValidation() {
		try {
			dednper = Long.valueOf(tfDeduPercent.getValue().toString());
			if (dednper < 0) {
				tfDeduPercent.setComponentError(new UserError("Deduction Percent should not Less than zero"));
			} else if (dednper > 100) {
				tfDeduPercent.setComponentError(new UserError("Deduction Percent should not Greater than 100"));
			} else {
				tfDeduPercent.setComponentError(null);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
