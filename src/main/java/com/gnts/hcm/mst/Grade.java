/**
 * File Name 		: Grade.java 
 * Description 		: this class is used for add/edit Grade details. 
 * Author 			: MADHU T 
 * Date 			: 14-July-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         14-July-2014        	MADHU T		        Initial Version
 * 
 */
package com.gnts.hcm.mst;

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
import com.gnts.hcm.domain.mst.GradeDM;
import com.gnts.hcm.service.mst.GradeService;
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

public class Grade extends BaseUI {
	// Bean creation
	private GradeService serviceGrade = (GradeService) SpringContextHelper.getBean("Grade");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfGRDDesc, tfMinSal, tfMaxSal, tfGRDHierachy, tfNoLates, tfPermission;
	private ComboBox cbStatus, cbGRDLvl;
	private int gradeByhiry = 0;
	// BeanItemContainer
	private BeanItemContainer<GradeDM> beanGradeDM = null;
	// local variables declaration
	private Long companyid, moduleId;
	private Long maxsal = 0L;
	private Long minsal = 0L;
	private String departId, pkGradeId;
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(Grade.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Grade() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Grade() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Grade UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Grade Description text field
		tfGRDDesc = new GERPTextField("Grade Desc.");
		// Grade Level text field
		cbGRDLvl = new GERPComboBox("Grade Level");
		cbGRDLvl.setItemCaptionPropertyId("lookupname");
		loadGRDLvl();
		// Minimum Salary field
		tfMinSal = new GERPTextField("Minimum Salary");
		tfMinSal.setValue("0");
		// Max Salary field
		tfMaxSal = new GERPTextField("Maximum Salary");
		tfMaxSal.setValue("0");
		// Grade Hierarchy text field
		tfGRDHierachy = new GERPTextField("Grade Hierarchy");
		tfGRDHierachy.setValue("0");
		tfGRDHierachy.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				try {
					System.out.println("Grade hierarchy blur Event");
					gradeByhiry = serviceGrade.getGradeListBasedOnHirarchy(
							Long.valueOf(tfGRDHierachy.getValue().toString()), companyid);
					if (gradeByhiry == 0) {
						tfGRDHierachy.setComponentError(null);
					} else {
						tfGRDHierachy.setComponentError(new UserError("Grade Hierarchy already exists"));
					}
				}
				catch (Exception e) {
				}
			}
		});
		tfGRDHierachy.setImmediate(true);
		// No.of Lates text field
		tfNoLates = new GERPTextField("No.of Lates");
		tfNoLates.setValue("0");
		// No.of Permissin text field
		tfPermission = new GERPTextField("No.of Permission");
		tfPermission.setValue("0");
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
		flColumn1.addComponent(tfGRDDesc);
		flColumn2.addComponent(cbGRDLvl);
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
		flColumn1.addComponent(tfGRDDesc);
		flColumn1.addComponent(cbGRDLvl);
		flColumn2.addComponent(tfMinSal);
		flColumn2.addComponent(tfMaxSal);
		flColumn3.addComponent(tfGRDHierachy);
		flColumn3.addComponent(tfNoLates);
		flColumn4.addComponent(tfPermission);
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
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<GradeDM> listGrade = new ArrayList<GradeDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfGRDDesc.getValue() + ", " + tfGRDDesc.getValue()
					+ (String) cbStatus.getValue());
			listGrade = serviceGrade.getGradeList(null, tfGRDDesc.getValue(), (String) cbGRDLvl.getValue(), companyid,
					(String) cbStatus.getValue(), "F");
			recordCnt = listGrade.size();
			beanGradeDM = new BeanItemContainer<GradeDM>(GradeDM.class);
			beanGradeDM.addAll(listGrade);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Grade. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanGradeDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "gradeId", "gradeDESC", "gradeLvl", "minSal", "maxSal",
					"status", "lastUpdatedBy", "lastUpdatedDate" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Grand Desc.", "Grade Level", "Min Salary",
					"Max Salary", "Status", "Last Updated By", "Last Updated Date" });
			tblMstScrSrchRslt.setColumnAlignment("gradeId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnAlignment("minSal", Align.RIGHT);
			tblMstScrSrchRslt.setColumnAlignment("maxSal", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedDate", "No.of Records : " + recordCnt);
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
		tfGRDDesc.setValue("");
		tfMinSal.setValue("0");
		cbGRDLvl.setValue(null);
		tfMaxSal.setValue("0");
		tfGRDHierachy.setValue("0");
		tfNoLates.setValue("0");
		tfPermission.setValue("0");
		tfGRDDesc.setComponentError(null);
		tfMaxSal.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editGrade() {
		GradeDM gradeDM = beanGradeDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		pkGradeId = gradeDM.getGradeId().toString();
		if (gradeDM.getGradeDESC() != null) {
			tfGRDDesc.setValue(gradeDM.getGradeDESC());
		}
		if (gradeDM.getMinSal() != null) {
			tfMinSal.setValue(gradeDM.getMinSal().toString());
		}
		if (gradeDM.getMaxSal() != null) {
			tfMaxSal.setValue(gradeDM.getMaxSal().toString());
		}
		if (gradeDM.getNoOfLates() != null) {
			tfNoLates.setValue(gradeDM.getNoOfLates().toString());
		}
		if (gradeDM.getGradeHirarchy() != null) {
			tfGRDHierachy.setValue(gradeDM.getGradeHirarchy().toString());
		}
		if (gradeDM.getNoOfPermission() != null) {
			tfPermission.setValue(gradeDM.getNoOfPermission().toString());
		}
		cbStatus.setValue(gradeDM.getStatus());
		cbGRDLvl.setValue(gradeDM.getGradeLvl());
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
		tfGRDDesc.setValue("");
		cbGRDLvl.setValue(null);
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
		tfGRDDesc.setRequired(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Dept. ID " + departId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_GRADE);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkGradeId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfGRDDesc.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		tfGRDDesc.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editGrade();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfGRDDesc.setComponentError(null);
		tfMaxSal.setComponentError(null);
		errorFlag = false;
		if ((tfGRDDesc.getValue() == null) || tfGRDDesc.getValue().trim().length() == 0) {
			tfGRDDesc.setComponentError(new UserError(GERPErrorCodes.NULL_GRADE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfGRDDesc.getValue());
		}
		minsal = Long.valueOf(tfMinSal.getValue().toString());
		maxsal = Long.valueOf(tfMaxSal.getValue().toString());
		if (minsal > maxsal) {
			tfMaxSal.setComponentError(new UserError("Maximum salary is greater than minimum salary"));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		GradeDM gradeDM = new GradeDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			gradeDM = beanGradeDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if (tfGRDDesc.getValue() != null) {
			gradeDM.setGradeDESC(tfGRDDesc.getValue().toString());
		}
		if (cbGRDLvl.getValue() != null) {
			gradeDM.setGradeLvl(cbGRDLvl.getValue().toString());
		}
		if (tfMinSal.getValue() != null && tfMinSal.getValue().trim().length() > 0) {
			gradeDM.setMinSal(Long.valueOf(tfMinSal.getValue()));
		} else {
			gradeDM.setMinSal(new Long("0"));
		}
		if (tfMaxSal.getValue() != null && tfMaxSal.getValue().trim().length() > 0) {
			gradeDM.setMaxSal(Long.valueOf(tfMaxSal.getValue()));
		} else {
			gradeDM.setMaxSal(new Long("0"));
		}
		if (tfNoLates.getValue() != null && tfNoLates.getValue().trim().length() > 0) {
			gradeDM.setNoOfLates((Long.valueOf(tfNoLates.getValue())));
		} else {
			gradeDM.setNoOfLates(new Long("0"));
		}
		if (tfGRDHierachy.getValue() != null && tfGRDHierachy.getValue().trim().length() > 0) {
			gradeDM.setGradeHirarchy((Long.valueOf(tfGRDHierachy.getValue())));
		} else {
			gradeDM.setGradeHirarchy(new Long("0"));
		}
		if (tfPermission.getValue() != null && tfPermission.getValue().trim().length() > 0) {
			gradeDM.setNoOfPermission((Long.valueOf(tfPermission.getValue())));
		} else {
			gradeDM.setNoOfPermission(new Long("0"));
		}
		gradeDM.setCmpId(companyid);
		if (cbStatus.getValue() != null) {
			gradeDM.setStatus((String) cbStatus.getValue());
		}
		gradeDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		gradeDM.setLastUpdatedBy(username);
		serviceGrade.saveAndUpdate(gradeDM);
		resetFields();
		loadSrchRslt();
	}
	
	private void loadGRDLvl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active",
					"HC_GRDLVL"));
			cbGRDLvl.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
