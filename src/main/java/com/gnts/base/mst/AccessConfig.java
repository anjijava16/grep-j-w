/**
 * File Name	:	AccessConfig.java
 * Description	:	This class is used for add/edit Access Config details.
 * Author		:	Hohulnath.V
 * Date			:	Mar 6, 2014
 * Modification :   1.Mar 6,  2014 --> Added the comment line for description. Mar 7,2014--> Changed the UI according to New Version
 * Modified By  :   Hohulnath.V
 *
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 *
 * Version      Date           		Modified By             Remarks
 * 0.1          Jul 02 2014        	SOUNDAR C		        Code re-factoring
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.AccessConfigDM;
import com.gnts.base.domain.mst.AppScreensDM;
import com.gnts.base.domain.mst.AppScreensUserDM;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.FieldAccessConfigDM;
import com.gnts.base.domain.mst.RoleDM;
import com.gnts.base.service.mst.AccessConfigService;
import com.gnts.base.service.mst.AppScreensService;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.FieldAccessConfigService;
import com.gnts.base.service.mst.RoleService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPOptionGroup;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AccessConfig extends BaseUI {
	private AppScreensService appsConfigBean = (AppScreensService) SpringContextHelper.getBean("appScreens");
	private AccessConfigService serviceAccessConfig = (AccessConfigService) SpringContextHelper.getBean("accessconfig");
	private FieldAccessConfigService serviceFieldAccess = (FieldAccessConfigService) SpringContextHelper
			.getBean("fieldAccessConfig");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private RoleService serviceRole = (RoleService) SpringContextHelper.getBean("role");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private ComboBox cbRole = new GERPComboBox("Role");
	private ComboBox cbBranch = new GERPComboBox("Branch");
	// User Input Control Components - screen access
	private CheckBox chkPublic = new CheckBox("Public");
	private CheckBox chkView = new CheckBox("View");
	private CheckBox chkCreate = new CheckBox("Create");
	private CheckBox chkReview = new CheckBox("Review");
	private CheckBox chkApprove = new CheckBox("Approve");
	private OptionGroup cbRecordLevel = new GERPOptionGroup(null, BASEConstants.M_BASE_SCRN_ACCESS_CONFIG,
			BASEConstants.RECORD_LVL);
	private List<AppScreensUserDM> appScreenList = new ArrayList<AppScreensUserDM>();
	private TreeTable tblScreenAccess = new TreeTable();
	private Table tblFieldAccess = new Table("Field Access Level");
	// local variables declaration
	private Long companyid;
	private String screenid;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(AccessConfig.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public AccessConfig() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside AccessConfig() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting AccessConfig UI");
		// invisible add button
		btnAdd.setVisible(true);
		btnSearch.setVisible(false);
		// set table properties
		tblScreenAccess.setFooterVisible(true);
		tblScreenAccess.setSizeFull();
		tblScreenAccess.setSelectable(true);
		tblFieldAccess.setPageLength(3);
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("150");
		loadBranchList();
		cbBranch.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbBranch.getItem(itemId);
				if (item != null) {
					loadSrchRslt();
				}
			}
		});
		cbRole.setItemCaptionPropertyId("roleName");
		cbRole.setWidth("150");
		loadRoleList();
		cbRole.addValueChangeListener(new Property.ValueChangeListener() {
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
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblScreenAccess);
		resetFields();
		loadSrchRslt();
		tblScreenAccess.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblScreenAccess.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
					btnAdd.setEnabled(true);
				} else {
					btnEdit.setEnabled(true);
					btnAdd.setEnabled(false);
				}
				resetFields();
			}
		});
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// create form layouts to hold the input items
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		// add the user input items into appropriate form layout
		flColumn1.addComponent(cbBranch);
		flColumn2.addComponent(cbRole);
		// add the form layouts into user input layout
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.setMargin(true);
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	private void assembleUserInputLayout() {
		// create form layouts to hold the input items
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		Label line1 = new Label("<hr/>", ContentMode.HTML);
		line1.setWidth("275");
		Label line2 = new Label("<hr/>", ContentMode.HTML);
		line2.setWidth("275");
		// add the user input items into appropriate form layout
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setSpacing(true);
		VerticalLayout hlRow1 = new VerticalLayout();
		HorizontalLayout hlRow2 = new HorizontalLayout();
		hlRow2.setCaption("Data Access Level");
		hlRow1.setCaption("Screen Access Level");
		hlRow1.addComponent(chkPublic);
		hlRow1.addComponent(chkView);
		hlRow1.addComponent(chkCreate);
		hlRow1.addComponent(chkReview);
		hlRow1.addComponent(chkApprove);
		hlRow2.addComponent(cbRecordLevel);
		hlUserInputLayout.addComponent(hlRow1);
		hlUserInputLayout.addComponent(line1);
		hlUserInputLayout.addComponent(hlRow2);
		hlUserInputLayout.addComponent(line2);
		hlUserInputLayout.addComponent(tblFieldAccess);
		hlUserInputLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		tblScreenAccess.removeAllItems();
		tblScreenAccess.addContainerProperty("screenname", String.class, "");
		tblScreenAccess.addContainerProperty("screenid", String.class, "");
		if (cbRole.getValue() != null && cbBranch.getValue() != null) {
			appScreenList = appsConfigBean.getMBaseAppscreenUserList(Long.valueOf(cbRole.getValue().toString()),
					companyid, Long.valueOf(cbBranch.getValue().toString()));
			recordCnt = appScreenList.size();
			if (recordCnt == 0) {
				for (AppScreensDM appScreensDM : appsConfigBean.getMBaseAppScreenListByUserId(null)) {
					AccessConfigDM accessConfigDM = new AccessConfigDM();
					accessConfigDM.setScrID(appScreensDM.getScreenId());
					accessConfigDM.setCompanyid(companyid);
					accessConfigDM.setBranchid(Long.valueOf(cbBranch.getValue().toString()));
					accessConfigDM.setRoleId(Long.valueOf(cbRole.getValue().toString()));
					accessConfigDM.setPublicYN("Y");
					accessConfigDM.setViewYN("Y");
					accessConfigDM.setCreateYN("Y");
					accessConfigDM.setReviewYN("Y");
					accessConfigDM.setApproYN("Y");
					accessConfigDM.setRecLVL("Company");
					accessConfigDM.setStatus("Active");
					serviceAccessConfig.saveAccessConfig(accessConfigDM);
				}
				appScreenList = appsConfigBean.getMBaseAppscreenUserList(Long.valueOf(cbRole.getValue().toString()),
						companyid, Long.valueOf(cbBranch.getValue().toString()));
			}
			for (AppScreensUserDM mBaseAppObj : appScreenList) {
				tblScreenAccess.addItem(new Object[] { mBaseAppObj.getScreendesc(),
						mBaseAppObj.getScreenId().toString() }, mBaseAppObj.getScreenId().intValue());
				if (mBaseAppObj.getParentId() != null) {
					tblScreenAccess.setParent(mBaseAppObj.getScreenId().intValue(), mBaseAppObj.getParentId()
							.intValue());
					int count = 0;
					for (AppScreensUserDM obj : appScreenList) {
						if (obj.getParentId() != null) {
							if (mBaseAppObj.getScreenId() == obj.getParentId()) {
								count++;
							}
						}
					}
					if (count != 0) {
						tblScreenAccess.setChildrenAllowed(mBaseAppObj.getScreenId().intValue(), true);
					} else {
						tblScreenAccess.setChildrenAllowed(mBaseAppObj.getScreenId().intValue(), false);
					}
				}
			}
		}
		tblScreenAccess.setVisibleColumns(new Object[] { "screenname", "screenid" });
		tblScreenAccess.setColumnHeaders(new String[] { "Screen", "Id" });
		tblScreenAccess.setColumnFooter("screenid", "No.of Records : " + recordCnt);
	}
	
	private void loadFieldAccessList() {
		tblFieldAccess.removeAllItems();
		List<FieldAccessConfigDM> auditConfigList = new ArrayList<FieldAccessConfigDM>();
		auditConfigList = serviceFieldAccess.getFieldAccessConfigList(Long.valueOf(screenid), null, companyid, null,
				null, "Active");
		recordCnt = auditConfigList.size();
		tblFieldAccess.setEditable(true);
		BeanItemContainer<FieldAccessConfigDM> auditConfigBean = new BeanItemContainer<FieldAccessConfigDM>(
				FieldAccessConfigDM.class);
		for (FieldAccessConfigDM fieldAccessConfigDM : auditConfigList) {
			if (fieldAccessConfigDM.getViewYN().equals("Y")) {
				fieldAccessConfigDM.setViewYN("true");
			} else {
				fieldAccessConfigDM.setViewYN("false");
			}
		}
		auditConfigBean.addAll(auditConfigList);
		tblFieldAccess.setTableFieldFactory(new TableFieldFactory() {
			private static final long serialVersionUID = 1L;
			
			public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
				if (propertyId.toString().equals("viewYN")) {
					CheckBox ckBox = new CheckBox();
					ckBox.setValue(true);
					return ckBox;
				}
				return null;
			}
		});
		tblFieldAccess.setContainerDataSource(auditConfigBean);
		tblFieldAccess.setColumnFooter("onOff", "No.of Records:" + recordCnt);
		tblFieldAccess.setVisibleColumns(new Object[] { "screeFieldDesc", "viewYN" });
		tblFieldAccess.setColumnHeaders(new String[] { "Field Name", "View?" });
		tblFieldAccess.setSelectable(true);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		chkApprove.setValue(false);
		chkCreate.setValue(false);
		chkPublic.setValue(false);
		chkReview.setValue(false);
		chkView.setValue(false);
		cbRecordLevel.setValue(cbRecordLevel.getItemIds().iterator().next());
		tblFieldAccess.removeAllItems();
		tblScreenAccess.setValue(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editAccessConfig() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		Item sltedRcd = tblScreenAccess.getItem(tblScreenAccess.getValue());
		if (sltedRcd != null) {
			screenid = sltedRcd.getItemProperty("screenid").getValue().toString();
			List<AccessConfigDM> list = serviceAccessConfig.getAccessConfigList(Long.valueOf(screenid), null,
					companyid, (Long) cbBranch.getValue(), (Long) cbRole.getValue(), "Active");
			for (AccessConfigDM obj : list) {
				if (obj.getPublicYN().equals("Y")) {
					chkPublic.setValue(true);
				} else {
					chkPublic.setValue(false);
				}
				if (obj.getViewYN().equals("Y")) {
					chkView.setValue(true);
				} else {
					chkView.setValue(false);
				}
				if (obj.getCreateYN().equals("Y")) {
					chkCreate.setValue(true);
				} else {
					chkCreate.setValue(false);
				}
				if (obj.getReviewYN() != null) {
					if (obj.getReviewYN().equals("Y")) {
						chkReview.setValue(true);
					} else {
						chkReview.setValue(false);
					}
				}
				if (obj.getApproYN() != null) {
					if (obj.getApproYN().equals("Y")) {
						chkApprove.setValue(true);
					} else {
						chkApprove.setValue(false);
					}
				}
				cbRecordLevel.setValue(obj.getRecLVL());
			}
			loadFieldAccessList();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected screenid -> "
					+ screenid);
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
		cbBranch.setValue(null);
		cbRole.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblScreenAccess);
		tblFieldAccess.removeAllItems();
		tblScreenAccess.removeAllItems();
		tblScreenAccess.setColumnFooter("screenid", "No.of Records : " + 0);
		btnSearch.setVisible(false);
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		resetFields();
		assembleUserInputLayout();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for screenid " + screenid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_SCRN_ACCESS_CONFIG);
		UI.getCurrent().getSession().setAttribute("audittablepk", screenid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblScreenAccess);
		btnSearch.setVisible(false);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editAccessConfig();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		AccessConfigDM accessobj = new AccessConfigDM();
		accessobj.setScrID(Long.valueOf(screenid));
		accessobj.setCompanyid(companyid);
		accessobj.setBranchid((Long) cbBranch.getValue());
		accessobj.setRoleId((Long) cbRole.getValue());
		if (chkPublic.getValue().equals(true)) {
			accessobj.setPublicYN("Y");
		} else {
			accessobj.setPublicYN("N");
		}
		if (chkView.getValue().equals(true)) {
			accessobj.setViewYN("Y");
		} else {
			accessobj.setViewYN("N");
		}
		if (chkCreate.getValue().equals(true)) {
			accessobj.setCreateYN("Y");
		} else {
			accessobj.setCreateYN("N");
		}
		if (chkReview.getValue().equals(true)) {
			accessobj.setReviewYN("Y");
		} else {
			accessobj.setReviewYN("N");
		}
		if (chkApprove.getValue().equals(true)) {
			accessobj.setApproYN("Y");
		} else {
			accessobj.setApproYN("N");
		}
		accessobj.setRecLVL((String) cbRecordLevel.getValue());
		accessobj.setStatus("Active");
		accessobj.setLastUpdatedBy(username);
		accessobj.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceAccessConfig.saveAccessConfigDetails(accessobj);
		saveFieldAccessDetails();
		resetFields();
	}
	
	private void saveFieldAccessDetails() {
		@SuppressWarnings("unchecked")
		Collection<FieldAccessConfigDM> itemIds = (Collection<FieldAccessConfigDM>) tblFieldAccess.getVisibleItemIds();
		for (FieldAccessConfigDM fieldaccess : (Collection<FieldAccessConfigDM>) itemIds) {
			if (fieldaccess.getViewYN().equals("true")) {
				fieldaccess.setViewYN("Y");
			} else {
				fieldaccess.setViewYN("N");
			}
			serviceFieldAccess.saveFieldAccessConfigDetails(fieldaccess);
		}
	}
	
	private void loadRoleList() {
		BeanContainer<Long, RoleDM> beanRole = new BeanContainer<Long, RoleDM>(RoleDM.class);
		beanRole.setBeanIdProperty("roleId");
		beanRole.addAll(serviceRole.getRoleList(null, "Active", companyid, "P"));
		cbRole.setContainerDataSource(beanRole);
	}
	
	private void loadBranchList() {
		BeanContainer<Long, BranchDM> beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranch.setBeanIdProperty("branchId");
		beanBranch.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
		cbBranch.setContainerDataSource(beanBranch);
	}
}
