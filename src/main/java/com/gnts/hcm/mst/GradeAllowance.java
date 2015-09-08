/**
 * File Name 		: GradeAllowance.java 
 * Description 		: this class is used for add/edit GradeAllowance details. 
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
import com.gnts.hcm.domain.mst.AllowanceDM;
import com.gnts.hcm.domain.mst.GradeAllowanceDM;
import com.gnts.hcm.domain.mst.GradeDM;
import com.gnts.hcm.service.mst.AllowanceService;
import com.gnts.hcm.service.mst.GradeAllowanceService;
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

public class GradeAllowance extends BaseUI {
	// Bean creation
	private GradeAllowanceService serviceGradeAllowance = (GradeAllowanceService) SpringContextHelper
			.getBean("GradeAllowance");
	private GradeService serviceGrade = (GradeService) SpringContextHelper.getBean("Grade");
	private AllowanceService serviceAllowance = (AllowanceService) SpringContextHelper.getBean("Allowance");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3, flColumn5;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfMinValue, tfMaxValue, tfMinPer, tfMaxPer;
	private ComboBox cbStatus, cbGradeDesc, cbAlwncDesc, cbOnBasicGros, cbAlwncPercent, cbPayBasic;
	// BeanItemContainer
	private BeanItemContainer<GradeAllowanceDM> beanGradeEarningDM = null;
	// local variables declaration
	private Long companyid;
	private String pkGradeAllowanceId;
	private int recordCnt = 0;
	private int gradeByhiry = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(GradeEarning.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public GradeAllowance() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside GradeAllowance() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting GradeAllowance UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// On Basic Gross
		cbOnBasicGros = new GERPComboBox("On Basic/Gross", BASEConstants.M_HCM_GRADE_EARNING, BASEConstants.BASIC_GROSS);
		// On Basic Gross
		cbPayBasic = new GERPComboBox("Pay Basis", BASEConstants.M_HCM_GRADE_ALLOWANCE, BASEConstants.GRD_ALLOWANCE);
		// Is Flat percent checkBox
		cbAlwncPercent = new GERPComboBox("Flat/Percent", BASEConstants.M_HCM_GRADE_EARNING, BASEConstants.FLAT_PERCENT);
		cbAlwncPercent.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbAlwncPercent.getValue() != null) {
					if (cbAlwncPercent.getValue().equals("Flat")) {
						tfMaxPer.setComponentError(null);
						tfMinValue.setEnabled(true);
						tfMaxValue.setEnabled(true);
						cbOnBasicGros.setEnabled(false);
						cbOnBasicGros.setValue(null);
						tfMinValue.setValue(tfMinValue.getValue());
						tfMaxValue.setValue(tfMaxValue.getValue());
						tfMinPer.setEnabled(false);
						tfMaxPer.setEnabled(false);
					} else if (cbAlwncPercent.getValue().equals("Percent")) {
						tfMaxValue.setComponentError(null);
						tfMinValue.setEnabled(false);
						tfMaxValue.setEnabled(false);
						cbOnBasicGros.setEnabled(true);
						cbOnBasicGros.setValue(cbOnBasicGros.getValue());
						tfMinValue.setValue("0");
						tfMaxValue.setValue("0");
						tfMinPer.setEnabled(true);
						tfMaxPer.setEnabled(true);
						tfMinPer.setValue(tfMinPer.getValue());
						tfMaxPer.setValue(tfMaxPer.getValue());
					} else if (cbAlwncPercent.getValue() == null) {
						tfMinValue.setEnabled(true);
						tfMaxValue.setEnabled(true);
						tfMinPer.setEnabled(true);
						tfMaxPer.setEnabled(true);
						cbOnBasicGros.setEnabled(true);
					}
				}
			}
		});
		// Grand Name Combo Box
		cbGradeDesc = new GERPComboBox("Grand Name");
		cbGradeDesc.setRequired(true);
		cbGradeDesc.setItemCaptionPropertyId("gradeDESC");
		loadGradeList();
		// Earn Name ComboBox
		cbAlwncDesc = new GERPComboBox("Allowance Name");
		cbAlwncDesc.setRequired(true);
		cbAlwncDesc.setItemCaptionPropertyId("alowncDesc");
		cbAlwncDesc.setImmediate(true);
		cbAlwncDesc.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					if (tblMstScrSrchRslt.getValue() == null) {
						System.out.println("Grade hierarchy blur Event");
						gradeByhiry = serviceGradeAllowance.getAllowanceNameBaseOnGrade(
								Long.valueOf(cbGradeDesc.getValue().toString()),
								Long.valueOf(cbAlwncDesc.getValue().toString()));
						System.out.println("gradeByhiry-->" + gradeByhiry);
						if (gradeByhiry == 0) {
							cbAlwncDesc.setComponentError(null);
						} else {
							cbAlwncDesc.setComponentError(new UserError("Allowance Name already exists"));
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		cbAlwncDesc.setImmediate(true);
		loadAllowanceList();
		// Minimum value field
		tfMinValue = new GERPTextField("Min Value");
		tfMinValue.setValue("0");
		tfMinValue.setWidth("75");
		// Max value field
		tfMaxValue = new GERPTextField("Max Value");
		tfMaxValue.setValue("0");
		tfMaxValue.setWidth("75");
		// Minimum value field
		tfMinPer = new GERPTextField("Min Percent");
		tfMinPer.setValue("0");
		tfMinPer.setWidth("75");
		// Max value field
		tfMaxPer = new GERPTextField("Max Percent");
		tfMaxPer.setValue("0");
		tfMaxPer.setWidth("75");
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
		flColumn2.addComponent(cbAlwncDesc);
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
		flColumn1.addComponent(cbGradeDesc);
		flColumn1.addComponent(cbAlwncDesc);
		flColumn2.addComponent(cbAlwncPercent);
		flColumn2.addComponent(cbOnBasicGros);
		flColumn3.addComponent(tfMinValue);
		flColumn3.addComponent(tfMaxValue);
		flColumn4.addComponent(tfMinPer);
		flColumn4.addComponent(tfMaxPer);
		flColumn5.addComponent(cbPayBasic);
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
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			cbAlwncDesc.setRequired(false);
			cbGradeDesc.setRequired(false);
			List<GradeAllowanceDM> listGradeAllowance = new ArrayList<GradeAllowanceDM>();
			Long gradeId = null;
			if (cbGradeDesc.getValue() != null) {
				gradeId = ((Long.valueOf(cbGradeDesc.getValue().toString())));
			}
			Long alwncId = null;
			if (cbAlwncDesc.getValue() != null) {
				alwncId = ((Long.valueOf(cbAlwncDesc.getValue().toString())));
			}
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfMinValue.getValue() + ", " + tfMaxValue.getValue()
					+ (String) cbStatus.getValue() + ", " + gradeId + "," + alwncId);
			listGradeAllowance = serviceGradeAllowance.getGradeAllowanceList(null, gradeId, alwncId,
					(String) cbStatus.getValue(), "F");
			recordCnt = listGradeAllowance.size();
			beanGradeEarningDM = new BeanItemContainer<GradeAllowanceDM>(GradeAllowanceDM.class);
			beanGradeEarningDM.addAll(listGradeAllowance);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the GradeEarning. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanGradeEarningDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "grdAlwncId", "gradeDESC", "alowncDesc", "status",
					"lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Grand Name", "Allowance Name", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("grdAlwncId", Align.RIGHT);
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
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbAlwncDesc.setValue(null);
		cbGradeDesc.setValue(null);
		cbAlwncPercent.setValue(null);
		cbOnBasicGros.setValue(null);
		cbPayBasic.setValue(null);
		tfMinValue.setValue("0");
		tfMaxValue.setValue("0");
		tfMinPer.setValue("0");
		tfMaxPer.setValue("0");
		cbGradeDesc.setComponentError(null);
		cbAlwncDesc.setComponentError(null);
		tfMaxValue.setComponentError(null);
		tfMaxPer.setComponentError(null);
		cbAlwncPercent.setComponentError(null);
		cbOnBasicGros.setComponentError(null);
		cbPayBasic.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editGradeEarning() {
		try {
			cbAlwncDesc.setComponentError(null);
			if (tblMstScrSrchRslt.getValue() != null) {
				GradeAllowanceDM editGradeEarning = beanGradeEarningDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				pkGradeAllowanceId = editGradeEarning.getAlwnceId().toString();
				if (editGradeEarning.getMinVal() != null) {
					tfMinValue.setValue(editGradeEarning.getMinVal().toString());
				}
				if (editGradeEarning.getMaxVal() != null) {
					tfMaxValue.setValue(editGradeEarning.getMaxVal().toString());
				}
				if (editGradeEarning.getMaxPer() != null) {
					tfMaxPer.setValue(editGradeEarning.getMaxPer().toString());
				}
				if (editGradeEarning.getMinPer() != null) {
					tfMinPer.setValue(editGradeEarning.getMinPer().toString());
				}
				cbStatus.setValue(editGradeEarning.getStatus());
				cbGradeDesc.setValue(editGradeEarning.getGradeId());
				cbAlwncDesc.setValue(editGradeEarning.getAlwnceId());
				cbAlwncPercent.setValue(editGradeEarning.getIsFlatPer());
				cbOnBasicGros.setValue(editGradeEarning.getOnBasicGros());
				cbPayBasic.setValue(editGradeEarning.getPayBasis());
			}
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
		cbAlwncDesc.setValue(null);
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
		cbAlwncDesc.setRequired(true);
		cbGradeDesc.setRequired(true);
		cbAlwncPercent.setRequired(true);
		// cbOnBasicGros.setRequired(true);
		cbOnBasicGros.setEnabled(true);
		cbAlwncPercent.setEnabled(true);
		tfMinValue.setEnabled(true);
		tfMaxValue.setEnabled(true);
		tfMinPer.setEnabled(true);
		tfMaxPer.setEnabled(true);
		cbPayBasic.setRequired(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for grade Allowance. ID " + pkGradeAllowanceId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_GRADE_EARNING);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkGradeAllowanceId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbGradeDesc.setComponentError(null);
		cbAlwncDesc.setComponentError(null);
		cbAlwncDesc.setRequired(false);
		cbGradeDesc.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		cbAlwncDesc.setRequired(true);
		cbGradeDesc.setRequired(true);
		cbAlwncPercent.setRequired(true);
		cbPayBasic.setRequired(true);
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
		cbAlwncDesc.setComponentError(null);
		tfMaxValue.setComponentError(null);
		tfMaxPer.setComponentError(null);
		cbAlwncPercent.setComponentError(null);
		cbOnBasicGros.setComponentError(null);
		cbPayBasic.setComponentError(null);
		errorFlag = false;
		if (cbGradeDesc.getValue() == null) {
			cbGradeDesc.setComponentError(new UserError(GERPErrorCodes.NULL_GRD_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbGradeDesc.getValue());
			errorFlag = true;
		}
		if (cbAlwncDesc.getValue() == null) {
			cbAlwncDesc.setComponentError(new UserError(GERPErrorCodes.NULL_GRD_ALLOWANCE));
			errorFlag = true;
		}
		if (cbAlwncPercent.getValue() == null) {
			cbAlwncPercent.setComponentError(new UserError(GERPErrorCodes.NULL_FLAT_PERCENTAGE));
			errorFlag = true;
		}
		if (cbPayBasic.getValue() == null) {
			cbPayBasic.setComponentError(new UserError(GERPErrorCodes.NULL_PAYBASIC));
			errorFlag = true;
		}
		BigDecimal minsal = new BigDecimal(tfMinValue.getValue().toString());
		BigDecimal maxsal = new BigDecimal(tfMaxValue.getValue().toString());
		if (minsal.compareTo(maxsal) > 0) {
			tfMaxValue.setComponentError(new UserError("Maximum Value is greater than minimum Value"));
			errorFlag = true;
		}
		BigDecimal minPer = new BigDecimal(tfMinPer.getValue().toString());
		BigDecimal maxPer = new BigDecimal(tfMaxPer.getValue().toString());
		if (minPer.compareTo(maxPer) > 0) {
			tfMaxPer.setComponentError(new UserError("Maximum Percent is greater than minimum Percent"));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		GradeAllowanceDM gradeAllowance = new GradeAllowanceDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			gradeAllowance = beanGradeEarningDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if (tfMinValue.getValue() != null && tfMinValue.getValue().trim().length() > 0) {
			gradeAllowance.setMinVal(new BigDecimal(tfMinValue.getValue()));
		} else {
			gradeAllowance.setMinVal(new BigDecimal("0"));
		}
		if (tfMaxValue.getValue() != null && tfMaxValue.getValue().trim().length() > 0) {
			gradeAllowance.setMaxVal(new BigDecimal(tfMaxValue.getValue()));
		} else {
			gradeAllowance.setMaxVal(new BigDecimal("0"));
		}
		if (tfMinPer.getValue() != null && tfMinPer.getValue().trim().length() > 0) {
			gradeAllowance.setMinPer(new BigDecimal(tfMinPer.getValue()));
		} else {
			gradeAllowance.setMinPer(new BigDecimal("0"));
		}
		if (tfMaxPer.getValue() != null && tfMaxPer.getValue().trim().length() > 0) {
			gradeAllowance.setMaxPer(new BigDecimal(tfMaxPer.getValue()));
		} else {
			gradeAllowance.setMaxPer(new BigDecimal("0"));
		}
		if (cbStatus.getValue() != null) {
			gradeAllowance.setStatus((String) cbStatus.getValue());
		}
		if (cbAlwncPercent.getValue() != null) {
			gradeAllowance.setIsFlatPer((String) cbAlwncPercent.getValue());
		}
		if (cbOnBasicGros.getValue() != null) {
			gradeAllowance.setOnBasicGros((String) cbOnBasicGros.getValue());
		}
		if (cbPayBasic.getValue() != null) {
			gradeAllowance.setPayBasis((String) cbPayBasic.getValue());
		}
		if (cbGradeDesc.getValue() != null) {
			gradeAllowance.setGradeId((Long.valueOf(cbGradeDesc.getValue().toString())));
		}
		if (cbAlwncDesc.getValue() != null) {
			gradeAllowance.setAlwnceId((Long.valueOf(cbAlwncDesc.getValue().toString())));
		}
		gradeAllowance.setLastUpdatedDate(DateUtils.getcurrentdate());
		gradeAllowance.setLastUpdatedBy(username);
		serviceGradeAllowance.saveDetails(gradeAllowance);
		resetFields();
		loadSrchRslt();
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
			logger.info(e.getMessage());
		}
	}
	
	private void loadAllowanceList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
			BeanContainer<Long, AllowanceDM> beanAlwncDM = new BeanContainer<Long, AllowanceDM>(AllowanceDM.class);
			beanAlwncDM.setBeanIdProperty("alowncId");
			beanAlwncDM.addAll(serviceAllowance.getalowanceList(null, null, companyid, null, "Active", "F"));
			cbAlwncDesc.setContainerDataSource(beanAlwncDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
