/**
 * File Name 		: LTABlock.java 
 * Description 		: this class is used for add/edit LTABlock details. 
 * Author 			: MADHU T 
 * Date 			: 31-July-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. 
 * All rights reserved.
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         31-July-2014        	MADHU T		        Initial Version
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
import com.gnts.hcm.domain.mst.LTABlockDM;
import com.gnts.hcm.service.mst.LTABlockService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class LTABlock extends BaseUI {
	private LTABlockService serviceLTABlock = (LTABlockService) SpringContextHelper.getBean("LTABlock");
	// form layout for input controls
	private FormLayout flBlckPeriod, flStatus, flperiodFlag, flIscurrent;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfBlckPeriod, tfPeriodFlag;
	private ComboBox cbLTABlockStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private CheckBox ckIsCurrent;
	private BeanItemContainer<LTABlockDM> beanLTABlock = null;
	// local variables declaration
	private Long companyid;
	private String ltaBlockId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(LTABlock.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public LTABlock() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside LTABlock() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting LTABlock UI");
		// Block Period text field
		tfBlckPeriod = new GERPTextField("Block Period");
		// Period Flag Name text field
		tfPeriodFlag = new GERPTextField("Period Flag");
		tfPeriodFlag.setWidth("30");
		tfPeriodFlag.setMaxLength(1);
		// Iscurrent CheckBox
		ckIsCurrent = new CheckBox();
		ckIsCurrent.setCaption("Is current?");
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
		flBlckPeriod = new FormLayout();
		flperiodFlag = new FormLayout();
		flStatus = new FormLayout();
		flBlckPeriod.addComponent(tfBlckPeriod);
		flperiodFlag.addComponent(tfPeriodFlag);
		flStatus.addComponent(cbLTABlockStatus);
		hlSearchLayout.addComponent(flBlckPeriod);
		// hlSearchLayout.addComponent(flperiodFlag);
		hlSearchLayout.addComponent(flStatus);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flBlckPeriod = new FormLayout();
		flperiodFlag = new FormLayout();
		flIscurrent = new FormLayout();
		flStatus = new FormLayout();
		flBlckPeriod.addComponent(tfBlckPeriod);
		flperiodFlag.addComponent(tfPeriodFlag);
		flIscurrent.addComponent(ckIsCurrent);
		flStatus.addComponent(cbLTABlockStatus);
		hlUserInputLayout.addComponent(flBlckPeriod);
		hlUserInputLayout.addComponent(flIscurrent);
		hlUserInputLayout.addComponent(flperiodFlag);
		hlUserInputLayout.addComponent(flStatus);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<LTABlockDM> listLTABlock = new ArrayList<LTABlockDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfBlckPeriod.getValue() + ", " + cbLTABlockStatus.getValue());
			listLTABlock = serviceLTABlock.getLTABlock(null, companyid, (String) tfBlckPeriod.getValue(), null,
					(String) cbLTABlockStatus.getValue(), "F");
			recordCnt = listLTABlock.size();
			beanLTABlock = new BeanItemContainer<LTABlockDM>(LTABlockDM.class);
			beanLTABlock.addAll(listLTABlock);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the LTA Block result set");
			tblMstScrSrchRslt.setContainerDataSource(beanLTABlock);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "ltaBlockId", "blockPeriod", "periodFlag", "status",
					"lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Block Period", "Period Flag", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("ltaBlockId", Align.RIGHT);
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
		tfBlckPeriod.setValue("");
		tfPeriodFlag.setValue("");
		ckIsCurrent.setValue(false);
		tfBlckPeriod.setComponentError(null);
		cbLTABlockStatus.setValue(cbLTABlockStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editLTABlock() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			LTABlockDM editLTABlock = beanLTABlock.getItem(tblMstScrSrchRslt.getValue()).getBean();
			ltaBlockId = editLTABlock.getLtaBlockId().toString();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected LTABlock. Id -> " + ltaBlockId);
			if (editLTABlock.getBlockPeriod() != null) {
				tfBlckPeriod.setValue(editLTABlock.getBlockPeriod());
			}
			if (editLTABlock.getPeriodFlag() != null) {
				tfPeriodFlag.setValue(editLTABlock.getPeriodFlag());
			}
			if (editLTABlock.getIsCurrent().equals("Y")) {
				ckIsCurrent.setValue(true);
			} else {
				ckIsCurrent.setValue(false);
			}
			cbLTABlockStatus.setValue(editLTABlock.getStatus());
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
		cbLTABlockStatus.setValue(cbLTABlockStatus.getItemIds().iterator().next());
		tfBlckPeriod.setValue("");
		tfPeriodFlag.setValue("");
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
		tfBlckPeriod.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for LTABlock. ID " + ltaBlockId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_LEAVE_TYPE);
		UI.getCurrent().getSession().setAttribute("audittablepk", ltaBlockId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfBlckPeriod.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfBlckPeriod.setRequired(true);
		editLTABlock();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfBlckPeriod.setComponentError(null);
		Boolean errorFlag = false;
		if ((tfBlckPeriod.getValue() == null) || tfBlckPeriod.getValue().trim().length() == 0) {
			tfBlckPeriod.setComponentError(new UserError(GERPErrorCodes.NULL_BLOCK_PERIOD));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfBlckPeriod.getValue());
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		LTABlockDM ltaBlockObj = new LTABlockDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			ltaBlockObj = beanLTABlock.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		ltaBlockObj.setCmpId(companyid);
		if (tfBlckPeriod.getValue() != null && tfBlckPeriod.getValue().trim().length() > 0) {
			ltaBlockObj.setBlockPeriod(tfBlckPeriod.getValue());
		}
		if (tfPeriodFlag.getValue() != null) {
			ltaBlockObj.setPeriodFlag(tfPeriodFlag.getValue());
		}
		if (ckIsCurrent.getValue().equals(true)) {
			ltaBlockObj.setIsCurrent("Y");
		} else {
			ltaBlockObj.setIsCurrent("N");
		}
		if (cbLTABlockStatus.getValue() != null) {
			ltaBlockObj.setStatus((String) cbLTABlockStatus.getValue());
		}
		ltaBlockObj.setLastupdateddt(DateUtils.getcurrentdate());
		ltaBlockObj.setLastupdatedby(username);
		serviceLTABlock.saveLTABlock(ltaBlockObj);
		resetFields();
		loadSrchRslt();
	}
}
