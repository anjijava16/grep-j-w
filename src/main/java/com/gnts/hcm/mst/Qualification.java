/**
 * File Name 		: Qualification.java 
 * Description 		: this class is used for add/edit Qualification details. 
 * Author 			: MADHU T 
 * Date 			: 10-July-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         10-July-2014        	MADHU T		        Initial Version
 * 
 */
package com.gnts.hcm.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.QualificationDM;
import com.gnts.hcm.service.mst.QualificationService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Qualification extends BaseUI {
	// Bean Creation
	private QualificationService serviceQualification = (QualificationService) SpringContextHelper
			.getBean("Qualification");
	// form layout for input controls
	private FormLayout flQualificationName, flQualificationStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfQualificationName;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<QualificationDM> beanQualificationDM = null;
	// local variables declaration
	private Long companyid;
	private String qualId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(Qualification.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Qualification() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Qualification() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Qualification UI");
		tblMstScrSrchRslt.setVisible(true);
		// Qualification Name text field
		tfQualificationName = new GERPTextField("Qualification Name");
		tfQualificationName.setMaxLength(25);
		// create form layouts to hold the input items
		flQualificationName = new FormLayout();
		flQualificationStatus = new FormLayout();
		// add the user input items into appropriate form layout
		flQualificationName.addComponent(tfQualificationName);
		flQualificationStatus.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flQualificationName);
		hlUserInputLayout.addComponent(flQualificationStatus);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Add User Input Layout
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<QualificationDM> listQualification = new ArrayList<QualificationDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfQualificationName.getValue() + ", " + cbStatus.getValue());
		listQualification = serviceQualification.getQualificationList(null, null, null, null, "F");
		recordCnt = listQualification.size();
		beanQualificationDM = new BeanItemContainer<QualificationDM>(QualificationDM.class);
		beanQualificationDM.addAll(listQualification);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Qualification. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanQualificationDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "qualId", "qualName", "qualStatus", "lastUpdatedDate",
				"lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Qualification Name", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("qualId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfQualificationName.setValue("");
		tfQualificationName.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editQualification() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		qualId = sltedRcd.getItemProperty("qualId").getValue().toString();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Selected Qualification. Id -> " + qualId);
		if (sltedRcd != null) {
			tfQualificationName.setValue(sltedRcd.getItemProperty("qualName").getValue().toString());
			String stCode = sltedRcd.getItemProperty("qualStatus").getValue().toString();
			cbStatus.setValue(stCode);
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
		tfQualificationName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfQualificationName.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Qualification. ID " + qualId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_QUALIFICATION);
		UI.getCurrent().getSession().setAttribute("audittablepk", qualId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfQualificationName.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
		tblMstScrSrchRslt.setVisible(true);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfQualificationName.setRequired(true);
		editQualification();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfQualificationName.setComponentError(null);
		if ((tfQualificationName.getValue() == null) || tfQualificationName.getValue().trim().length() == 0) {
			tfQualificationName.setComponentError(new UserError(GERPErrorCodes.NULL_QUALIFICATION));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfQualificationName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		QualificationDM qualificationObj = new QualificationDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			qualificationObj = beanQualificationDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		qualificationObj.setCmpId(companyid);
		qualificationObj.setQualName(tfQualificationName.getValue().toString());
		if (cbStatus.getValue() != null) {
			qualificationObj.setQualStatus((String) cbStatus.getValue());
		}
		qualificationObj.setLastUpdatedDate(DateUtils.getcurrentdate());
		qualificationObj.setLastUpdatedBy(username);
		serviceQualification.saveAndUpdate(qualificationObj);
		resetFields();
		loadSrchRslt();
	}
}
