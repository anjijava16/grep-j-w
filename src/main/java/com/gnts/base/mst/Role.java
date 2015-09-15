/**
 * File Name 		: Role.java 
 * Description 		: this class is used for add/edit Role details. 
 * Author 			: Ram Sankar A 
 * Date 			: Mar 3, 2014
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 *  Version       Date           	Modified By              		 Remarks
 * 0.1           Mar 03 2014        Ram Sankar A		          Initial Version
 * 0.2			jun 20 2014			Abdullah H					  Code re-factoring
 * 
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.RoleDM;
import com.gnts.base.service.mst.RoleService;
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

public class Role extends BaseUI {
	private RoleService serviceRole = (RoleService) SpringContextHelper.getBean("role");
	// form layout for input controls
	private FormLayout flRoleName, flRoleStatus;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfRoleName = new GERPTextField("Role Name");
	private ComboBox cbRoleStatus;
	private BeanItemContainer<RoleDM> beanRoleDM = null;
	// local variables declaration
	private int recordCnt = 0;
	private String strLoginUserName;
	private Long companyId;
	private String roleid;
	// Initialize logger
	private Logger logger = Logger.getLogger(Role.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Role() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Inside Role() constructor");
		strLoginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ "Painting Role UI");
		// Role Name Combo Box
		cbRoleStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// create form layouts to hold the input items
		flRoleName = new FormLayout();
		flRoleStatus = new FormLayout();
		// create form layouts to hold the input items
		flRoleName.addComponent(tfRoleName);
		flRoleStatus.addComponent(cbRoleStatus);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flRoleName);
		hlUserInputLayout.addComponent(flRoleStatus);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfRoleName.getValue() + "," + (String) cbRoleStatus.getValue()
					+ ", " + companyId + ",F" + "Loading Search...");
			List<RoleDM> list = new ArrayList<RoleDM>();
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfRoleName.getValue() + "," + (String) cbRoleStatus.getValue()
					+ ", " + companyId + ",F");
			list = serviceRole.getRoleList(tfRoleName.getValue(), (String) cbRoleStatus.getValue(), companyId, "F");
			recordCnt = list.size();
			beanRoleDM = new BeanItemContainer<RoleDM>(RoleDM.class);
			beanRoleDM.addAll(list);
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
					+ "Got the Role result set");
			tblMstScrSrchRslt.setContainerDataSource(beanRoleDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "roleId", "roleName", "roleStatus", "lastUpdatedDt",
					"lastupdatedby" });
			tblMstScrSrchRslt
					.setColumnHeaders(new String[] { "Role ID", "Role", "Status", "Updated Date", "Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("roleId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editRole() {
		try {
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			if (sltedRcd != null) {
				roleid = sltedRcd.getItemProperty("roleId").getValue().toString();
				logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
						+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
						+ "Selected Role Id -> " + roleid);
				tfRoleName.setValue(sltedRcd.getItemProperty("roleName").getValue().toString());
				cbRoleStatus.setValue(sltedRcd.getItemProperty("roleStatus").getValue().toString());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbRoleStatus.setValue(cbRoleStatus.getItemIds().iterator().next());
		tfRoleName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfRoleName.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfRoleName.setRequired(true);
		editRole();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ "Validating Data ");
		tfRoleName.setComponentError(null);
		if ((tfRoleName.getValue() == null) || tfRoleName.getValue().trim().length() == 0) {
			tfRoleName.setComponentError(new UserError(GERPErrorCodes.NULL_ADMIN_SYS_ROLE_NAME));
			logger.warn("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
					+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
					+ "Throwing ValidationException.");
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ "Saving Data... ");
		RoleDM roleobj = new RoleDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			roleobj = beanRoleDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		roleobj.setCompanyid(companyId);
		roleobj.setRoleName(tfRoleName.getValue().toString());
		if (cbRoleStatus.getValue() != null) {
			roleobj.setRoleStatus((String) cbRoleStatus.getValue());
		}
		roleobj.setLastUpdatedDt(DateUtils.getcurrentdate());
		roleobj.setLastupdatedby(strLoginUserName);
		serviceRole.saveOrUpdateRole(roleobj);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ "Getting audit record for Role ID " + roleid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_ROLE);
		UI.getCurrent().getSession().setAttribute("audittablepk", roleid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ "Canceling action ");
		assembleSearchLayout();
		tfRoleName.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + tfRoleName.getValue() + ",  Active ," + companyId + ",F"
				+ "Resetting the UI controls");
		tfRoleName.setValue("");
		tfRoleName.setComponentError(null);
		cbRoleStatus.setValue(cbRoleStatus.getItemIds().iterator().next());
	}
}
