/**
 * File Name 		: GradeEarning.java 
 * Description 		: this class is used for add/edit GradeEarning details. 
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
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.EarningsDM;
import com.gnts.hcm.domain.mst.GradeDM;
import com.gnts.hcm.domain.mst.GradeEarningsDM;
import com.gnts.hcm.service.mst.EarningsService;
import com.gnts.hcm.service.mst.GradeEarningService;
import com.gnts.hcm.service.mst.GradeService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class GradeEarning extends BaseUI {
	// Bean creation
	private GradeEarningService serviceGradeEarning = (GradeEarningService) SpringContextHelper
			.getBean("GradeEarnings");
	private GradeService serviceGrade = (GradeService) SpringContextHelper.getBean("Grade");
	private EarningsService serviceEarnings = (EarningsService) SpringContextHelper.getBean("Earnings");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfMinValue, tfMaxValue, tfEarnPercent;
	private ComboBox cbStatus, cbGradeDesc, cbEarnDesc, cbOnBasicGros, cbFlatPercent;
	// BeanItemContainer
	private BeanItemContainer<GradeEarningsDM> beanGradeEarningDM = null;
	// local variables declaration
	private Long companyid;
	private String departId, pkGradeEarningId;
	private int recordCnt = 0;
	private int gradeByhiry = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(GradeEarning.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public GradeEarning() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside GradeEarning() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting GradeEarning UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// On Basic Gross
		cbOnBasicGros = new GERPComboBox("On Basic/Gross", BASEConstants.M_HCM_GRADE_EARNING, BASEConstants.BASIC_GROSS);
		// Is Flat percent checkBox
		cbFlatPercent = new GERPComboBox("Flat/Percent", BASEConstants.M_HCM_GRADE_EARNING, BASEConstants.FLAT_PERCENT);
		cbFlatPercent.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbFlatPercent.getValue() != null) {
					if (cbFlatPercent.getValue().equals("Flat")) {
						tfMinValue.setEnabled(true);
						tfMaxValue.setEnabled(true);
						cbOnBasicGros.setEnabled(false);
						tfEarnPercent.setEnabled(false);
						cbOnBasicGros.setValue(null);
						tfEarnPercent.setValue("0");
						tfMinValue.setValue(tfMinValue.getValue());
						tfMaxValue.setValue(tfMaxValue.getValue());
					} else if (cbFlatPercent.getValue().equals("Percent")) {
						tfMaxValue.setComponentError(null);
						tfMinValue.setEnabled(false);
						tfMaxValue.setEnabled(false);
						cbOnBasicGros.setEnabled(true);
						tfEarnPercent.setEnabled(true);
						cbOnBasicGros.setValue(cbOnBasicGros.getValue());
						tfEarnPercent.setValue(tfEarnPercent.getValue());
						tfMinValue.setValue("0");
						tfMaxValue.setValue("0");
					} else if (cbFlatPercent.getValue() == null) {
						tfMinValue.setEnabled(true);
						tfMaxValue.setEnabled(true);
						cbOnBasicGros.setEnabled(true);
						tfEarnPercent.setEnabled(true);
					}
				}
			}
		});
		// Grand Name Combo Box
		cbGradeDesc = new GERPComboBox("Grand Name");
		cbGradeDesc.setRequired(false);
		cbGradeDesc.setItemCaptionPropertyId("gradeDESC");
		loadGradeList();
		// Earn Name ComboBox
		cbEarnDesc = new GERPComboBox("Earn Name");
		cbEarnDesc.setRequired(false);
		cbEarnDesc.setItemCaptionPropertyId("earnDESCR");
		cbEarnDesc.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					if (tblMstScrSrchRslt.getValue() == null) {
						System.out.println("Grade hierarchy blur Event");
						gradeByhiry = serviceGradeEarning.getEarnNameBaseOnGrade(
								Long.valueOf(cbGradeDesc.getValue().toString()),
								Long.valueOf(cbEarnDesc.getValue().toString()));
						if (gradeByhiry == 0) {
							cbEarnDesc.setComponentError(null);
						} else {
							cbEarnDesc.setComponentError(new UserError("Earn Desc. already exists"));
						}
					}
				}
				catch (Exception e) {
				}
			}
		});
		cbEarnDesc.setImmediate(true);
		loadEarnList();
		// Earn Percent textField
		tfEarnPercent = new TextField("Earn Percent");
		// Minimum value field
		tfMinValue = new GERPTextField("Min Value");
		tfMinValue.setValue("0");
		// Max value field
		tfMaxValue = new GERPTextField("Max Value");
		tfMaxValue.setValue("0");
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
		flColumn1.addComponent(cbGradeDesc);
		flColumn2.addComponent(cbEarnDesc);
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
		flColumn1.addComponent(cbGradeDesc);
		flColumn1.addComponent(cbEarnDesc);
		flColumn2.addComponent(cbFlatPercent);
		flColumn2.addComponent(cbOnBasicGros);
		flColumn3.addComponent(tfMinValue);
		flColumn3.addComponent(tfMaxValue);
		flColumn4.addComponent(tfEarnPercent);
		flColumn4.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		cbEarnDesc.setRequired(false);
		cbGradeDesc.setRequired(false);
		List<GradeEarningsDM> listGradeEarning = new ArrayList<GradeEarningsDM>();
		Long gradeId = null;
		if (cbGradeDesc.getValue() != null) {
			gradeId = ((Long.valueOf(cbGradeDesc.getValue().toString())));
		}
		Long earnId = null;
		if (cbEarnDesc.getValue() != null) {
			earnId = ((Long.valueOf(cbEarnDesc.getValue().toString())));
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfEarnPercent.getValue() + ", " + tfMaxValue.getValue()
				+ (String) cbStatus.getValue() + ", " + gradeId + "," + earnId);
		listGradeEarning = serviceGradeEarning.getGradeEarnList(null, gradeId, earnId, (String) cbStatus.getValue(),
				"F");
		recordCnt = listGradeEarning.size();
		beanGradeEarningDM = new BeanItemContainer<GradeEarningsDM>(GradeEarningsDM.class);
		beanGradeEarningDM.addAll(listGradeEarning);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the GradeEarning. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanGradeEarningDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "grdEarnId", "gradeDESC", "earnDesc", "status",
				"lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Grand Name", "Earn Name", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("grdEarnId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbEarnDesc.setValue(null);
		cbGradeDesc.setValue(null);
		cbFlatPercent.setValue(null);
		cbOnBasicGros.setValue(null);
		tfEarnPercent.setValue("");
		tfMinValue.setValue("0");
		tfMaxValue.setValue("0");
		cbGradeDesc.setComponentError(null);
		cbEarnDesc.setComponentError(null);
		tfMaxValue.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editGradeEarning() {
		if (tblMstScrSrchRslt.getValue() != null) {
			GradeEarningsDM editGradeEarning = beanGradeEarningDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			pkGradeEarningId = editGradeEarning.getGrdEarnId().toString();
			if (editGradeEarning.getMinVal() != null) {
				tfMinValue.setValue(editGradeEarning.getMinVal().toString());
			}
			if (editGradeEarning.getMaxVal() != null) {
				tfMaxValue.setValue(editGradeEarning.getMaxVal().toString());
			}
			if (editGradeEarning.getEarnPercent() != null) {
				tfEarnPercent.setValue(editGradeEarning.getEarnPercent().toString());
			}
			cbStatus.setValue(editGradeEarning.getStatus());
			cbGradeDesc.setValue(editGradeEarning.getGradeId());
			cbEarnDesc.setValue(editGradeEarning.getEarnId());
			cbFlatPercent.setValue(editGradeEarning.getIsFlatPer());
			cbOnBasicGros.setValue(editGradeEarning.getOnBasicGros());
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
		tfEarnPercent.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbEarnDesc.setValue(null);
		cbGradeDesc.setValue(null);
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
		cbEarnDesc.setRequired(true);
		cbGradeDesc.setRequired(true);
		cbOnBasicGros.setEnabled(true);
		cbFlatPercent.setEnabled(true);
		tfMinValue.setEnabled(true);
		tfMaxValue.setEnabled(true);
		tfEarnPercent.setEnabled(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Dept. ID " + departId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_GRADE_EARNING);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkGradeEarningId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
		cbGradeDesc.setComponentError(null);
		cbEarnDesc.setComponentError(null);
		cbEarnDesc.setRequired(false);
		cbGradeDesc.setRequired(false);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		cbEarnDesc.setRequired(true);
		cbGradeDesc.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editGradeEarning();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbGradeDesc.setComponentError(null);
		cbEarnDesc.setComponentError(null);
		tfMaxValue.setComponentError(null);
		errorFlag = false;
		if (cbGradeDesc.getValue() == null) {
			cbGradeDesc.setComponentError(new UserError(GERPErrorCodes.NULL_GRD_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbGradeDesc.getValue());
			errorFlag = true;
		}
		if (cbEarnDesc.getValue() == null) {
			cbEarnDesc.setComponentError(new UserError(GERPErrorCodes.NULL_EARN_NAME));
			errorFlag = true;
		}
		BigDecimal minsal = new BigDecimal(tfMinValue.getValue().toString());
		BigDecimal maxsal = new BigDecimal(tfMaxValue.getValue().toString());
		if (minsal.compareTo(maxsal) > 0) {
			tfMaxValue.setComponentError(new UserError("Maximum Value is greater than minimum Value"));
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
			GradeEarningsDM gradeEarningObj = new GradeEarningsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				gradeEarningObj = beanGradeEarningDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (tfMinValue.getValue() != null && tfMinValue.getValue().trim().length() > 0) {
				gradeEarningObj.setMinVal(new BigDecimal(tfMinValue.getValue()));
			} else {
				gradeEarningObj.setMinVal(new BigDecimal("0"));
			}
			if (tfMaxValue.getValue() != null && tfMaxValue.getValue().trim().length() > 0) {
				gradeEarningObj.setMaxVal(new BigDecimal(tfMaxValue.getValue()));
			} else {
				gradeEarningObj.setMaxVal(new BigDecimal("0"));
			}
			if (tfEarnPercent.getValue() != null && tfEarnPercent.getValue().trim().length() > 0) {
				gradeEarningObj.setEarnPercent(new BigDecimal(tfEarnPercent.getValue()));
			} else {
				gradeEarningObj.setEarnPercent(new BigDecimal("0"));
			}
			if (cbStatus.getValue() != null) {
				gradeEarningObj.setStatus((String) cbStatus.getValue());
			}
			if (cbFlatPercent.getValue() != null) {
				gradeEarningObj.setIsFlatPer((String) cbFlatPercent.getValue());
			}
			if (cbOnBasicGros.getValue() != null) {
				gradeEarningObj.setOnBasicGros((String) cbOnBasicGros.getValue());
			}
			if (cbGradeDesc.getValue() != null) {
				gradeEarningObj.setGradeId((Long.valueOf(cbGradeDesc.getValue().toString())));
			}
			if (cbEarnDesc.getValue() != null) {
				gradeEarningObj.setEarnId((Long.valueOf(cbEarnDesc.getValue().toString())));
			}
			gradeEarningObj.setLastUpdatedDate(DateUtils.getcurrentdate());
			gradeEarningObj.setLastUpdatedBy(username);
			serviceGradeEarning.saveAndUpdate(gradeEarningObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadGradeList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
			BeanContainer<Long, GradeDM> beanGradeDM = new BeanContainer<Long, GradeDM>(GradeDM.class);
			beanGradeDM.setBeanIdProperty("gradeId");
			beanGradeDM.addAll(serviceGrade.getGradeList(null, null, null, companyid, "Active", "P"));
			cbGradeDesc.setContainerDataSource(beanGradeDM);
		}
		catch (Exception e) {
		}
	}
	
	private void loadEarnList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
			BeanContainer<Long, EarningsDM> beanEarningsDM = new BeanContainer<Long, EarningsDM>(EarningsDM.class);
			beanEarningsDM.setBeanIdProperty("earnId");
			beanEarningsDM.addAll(serviceEarnings.getEarningList(null, null, null, companyid, "Active", "F"));
			cbEarnDesc.setContainerDataSource(beanEarningsDM);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
