/**
 * File Name	:	ApprovalSchema.java
 * Description	:	To Handle ApprovalSchema Web page requests.
 * Author		:	Nandhakumar.S
 * Date			:	02-Jul-2014

 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1          02-Jul-2014		  Nandhakumar.S		initial version
 */
package com.gnts.base.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.RoleDM;
import com.gnts.base.service.mst.ApprovalSchemaService;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.RoleService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPSaveNotification;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Runo;

public class ApprovalSchema extends BaseUI {
	private static final long serialVersionUID = 1L;
	private ApprovalSchemaService serviceApprSchema = (ApprovalSchemaService) SpringContextHelper
			.getBean("approvalSchema");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private RoleService serviceRole = (RoleService) SpringContextHelper.getBean("role");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2;
	// User Input Components
	private TextField tfApprovalmt;
	private ComboBox cbBranch, cbRole, cbApprovelvl, cbApproveStatus;
	private Button btnSaveApprSchm = new Button("Save");
	private String username;
	private String apprSchmId;
	private Long companyid;
	private Table tblApproSchm;
	private BeanItemContainer<ApprovalSchemaDM> beanApprovalSchema = null;
	private GERPAddEditHLayout hlSearchLayout;
	private int recordCnt;
	private Logger logger = Logger.getLogger(ApprovalSchema.class);
	
	// Constructor received the parameters from Login UI class
	public ApprovalSchema() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ApprovalSchema() constructor");
		// Loading the ApprovalSchema UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting ApprovalSchema UI");
		// Text field for set approval limit
		tfApprovalmt = new GERPTextField("Approval Limit");
		tfApprovalmt.setWidth("100");
		// cbApprovelvl is name of Combo box for set approval level
		cbApprovelvl = new GERPComboBox("Approval Level", BASEConstants.M_BASE_APPROVAL_SCHEMA,
				BASEConstants.APPROVE_LVL);
		// cbApproveStatus is name of Combo box for set approval status
		cbApproveStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// cbBranch is name of Combo box to select branch name
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setWidth("150");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		tblApproSchm = new Table();
		tblApproSchm.setVisible(true);
		tblApproSchm.setStyleName(Runo.TABLE_SMALL);
		tblApproSchm.setSizeFull();
		tblApproSchm.setFooterVisible(true);
		tblApproSchm.setSelectable(true);
		tblApproSchm.setImmediate(true);
		tblApproSchm.setColumnCollapsingAllowed(false);
		cbBranch.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadRoleList();
				}
			}
		});
		// cbRole is name of Combo box to select role name
		cbRole = new GERPComboBox("Role Name");
		cbRole.setWidth("150");
		cbRole.setItemCaptionPropertyId("roleName");
		cbRole.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbRole.getItem(itemId);
				if (item != null) {
					loadSrchRslt();
				}
			}
		});
		// Buttons were set to invisible which are not used
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnSearch.setVisible(false);
		btnReset.setVisible(false);
		// Buttons were set to be visible which are used
		btnSaveApprSchm.setVisible(true);
		btnSaveApprSchm.setStyleName("savebt");
		btnSaveApprSchm.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					saveAuthSchema();
				}
				catch (Exception e) {
					try {
						throw new ERPException.SaveException();
					}
					catch (SaveException e1) {
						logger.error("Company ID : "
								+ UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
								+ " | User Name : "
								+ UI.getCurrent().getSession().getAttribute("loginUserName").toString() + " > "
								+ "Exception " + e1.getMessage());
						e1.printStackTrace();
					}
				}
			}
		});
		hlPageHdrContainter.addComponent(btnSaveApprSchm);
		hlPageHdrContainter.setComponentAlignment(btnSaveApprSchm, Alignment.MIDDLE_RIGHT);
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing the form layouts
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		// add the user input items into appropriate form layout
		flColumn1.addComponent(cbBranch);
		flColumn2.addComponent(cbRole);
		// add the form layouts into user input layout
		HorizontalLayout vlComp = new HorizontalLayout();
		vlComp.addComponent(flColumn1);
		vlComp.addComponent(flColumn2);
		vlComp.setSpacing(true);
		tblMstScrSrchRslt.setVisible(false);
		vlSrchRsltContainer.addComponent(tblApproSchm);
		hlSearchLayout.addComponent(vlComp);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<ApprovalSchemaDM> apprlist = new ArrayList<ApprovalSchemaDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + (Long) cbBranch.getValue() + ", " + (Long) cbRole.getValue());
		apprlist = serviceApprSchema.getApprovalSchemaList(companyid, (Long) cbBranch.getValue(),
				(Long) cbRole.getValue(), null, null);
		recordCnt = apprlist.size();
		beanApprovalSchema = new BeanItemContainer<ApprovalSchemaDM>(ApprovalSchemaDM.class);
		beanApprovalSchema.addAll(apprlist);
		tblApproSchm.setContainerDataSource(beanApprovalSchema);
		tblApproSchm.setVisibleColumns(new Object[] { "apprSchmId", "screenName", "apprLevel", "apprLimit",
				"apprStatus", "lastUpdatedDate", "lastUpdatedBy" });
		tblApproSchm.setColumnHeaders(new String[] { "Ref.Id", "Screen", "Approval Level", "Limit", "Status",
				"Updated Date", "Updated By" });
		tblApproSchm.setColumnAlignment("apprSchmId", Align.RIGHT);
		tblApproSchm.setColumnFooter("apprStatus", "No.of Records : " + recordCnt);
		tblApproSchm.setEditable(true);
		tblApproSchm.setTableFieldFactory(new TableFieldFactory() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
				if (propertyId.toString().equals("apprStatus")) {
					cbApproveStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
							BASEConstants.M_GENERIC_COLUMN);
					return cbApproveStatus;
				}
				if (propertyId.toString().equals("apprLevel")) {
					cbApprovelvl = new GERPComboBox("Approval Level", BASEConstants.M_BASE_APPROVAL_SCHEMA,
							BASEConstants.APPROVE_LVL);
					return cbApprovelvl;
				}
				if (propertyId.toString().equals("apprLimit")) {
					tfApprovalmt = new GERPTextField("");
					tfApprovalmt.setWidth("100");
					return tfApprovalmt;
				}
				return null;
			}
		});
	}
	
	// loading BranchDM list
	private void loadBranchList() {
		BeanContainer<Long, BranchDM> beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranch.setBeanIdProperty("branchId");
		beanBranch.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
		cbBranch.setContainerDataSource(beanBranch);
	}
	
	// loading RoleDM list
	private void loadRoleList() {
		BeanContainer<Long, RoleDM> beanRole = new BeanContainer<Long, RoleDM>(RoleDM.class);
		beanRole.setBeanIdProperty("roleId");
		beanRole.addAll(serviceRole.getRoleList(null, "Active", companyid, "P"));
		cbRole.setContainerDataSource(beanRole);
	}
	
	private void saveAuthSchema() throws ERPException.SaveException, FileNotFoundException, IOException {
		logger.info(" Inside of saveAuthSchema( )>> ");
		@SuppressWarnings("unchecked")
		Collection<ApprovalSchemaDM> itemIds = (Collection<ApprovalSchemaDM>) tblMstScrSrchRslt.getVisibleItemIds();
		for (ApprovalSchemaDM save : (Collection<ApprovalSchemaDM>) itemIds) {
			save.setLastUpdatedDate(DateUtils.getcurrentdate());
			save.setLastUpdatedBy(username);
			serviceApprSchema.saveOrUpdate(save);
		}
		resetFields();
		loadSrchRslt();
		new GERPSaveNotification();
	}
	
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
		// No functionality to be implement
	}
	
	@Override
	protected void addDetails() {
		// No functionality to be implement
	}
	
	@Override
	protected void editDetails() {
		// No functionality to be implement
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		// No functionality to be implement
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		// Local method handles Save feature
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Company. ID " + "");
		if (tblMstScrSrchRslt.getValue() != null) {
			ApprovalSchemaDM editApprSchm = beanApprovalSchema.getItem(tblMstScrSrchRslt.getValue()).getBean();
			apprSchmId = editApprSchm.getApprSchmId().toString();
		}
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_APPROVAL_SCHEMA);
		UI.getCurrent().getSession().setAttribute("audittablepk", apprSchmId);
	}
	
	@Override
	protected void cancelDetails() {
		btnReset.setVisible(false);
		btnSearch.setVisible(false);
		btnSaveApprSchm.setVisible(true);
	}
	
	@Override
	protected void resetFields() {
		cbBranch.setValue(cbBranch.getItemIds().iterator().next());
		cbRole.setValue(cbRole.getItemIds().iterator().next());
		btnSearch.setVisible(false);
		btnReset.setVisible(false);
	}
}
