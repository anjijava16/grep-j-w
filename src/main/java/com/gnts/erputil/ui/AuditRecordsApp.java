/**
 * File Name	:	AuditRecordsApp.java
 * Description	:	this class is used for declare ClientsDAO class methods
 * Author		:	Sekar
 * Date			:	Mar 07, 2014
 * Modification 
 * Modified By  :   
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of  GNTS Technologies pvt. ltd.
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1          JUN 16 2014        	MOHAMED	        Initial Version
 * 0.2			02-July-2014		MOHAMED			Code re-factoring
 */
package com.gnts.erputil.ui;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.txn.AuditConfigDM;
import com.gnts.base.domain.txn.AuditRecordsDM;
import com.gnts.base.service.rpt.AuditRecordsService;
import com.gnts.base.service.txn.AuditConfigService;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.helper.SpringContextHelper;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AuditRecordsApp implements ClickListener {
	private static final long serialVersionUID = 1L;
	private AuditRecordsService auditRecordsService = (AuditRecordsService) SpringContextHelper.getBean("auditRecords");
	private AuditConfigService auditConfigService = (AuditConfigService) SpringContextHelper.getBean("auditConfig");
	// Layout for command buttons
	public HorizontalLayout hlCmdBtnLayout = new HorizontalLayout();
	// form layout for input controls
	private FormLayout flColumn1, flColumn2;
	// Parent layout for all the input controls
	// Search Control Layout
	private HorizontalLayout hlSearchLayout = new HorizontalLayout();
	private Table tableAditRecords;
	private BeanItemContainer<AuditRecordsDM> beanAuditRecords = null;
	private BeanItemContainer<AuditConfigDM> beanAuditConfig = null;
	public Button btnAdd, btnEdit, btnAudit, btnDownload, btnSearch;
	private Logger logger = Logger.getLogger(AuditRecordsApp.class);
	// Declaration for add and edit panel
	private Long companyId;
	private String strTableName, columnName, oldValue;
	private String pkValue;
	private Window notifications;
	private TextField tfSearchValues;
	private ComboBox cbTableColumn;
	private AuditConfigDM AuditConfigDM;
	private Long companyid;
	private String username;
	
	public AuditRecordsApp(VerticalLayout vlMainLayout, String tableName, String primaryKeyValue) {
		strTableName = tableName;
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		pkValue = primaryKeyValue;
		buildView(vlMainLayout);
	}
	
	// Build the UI components
	private void buildView(VerticalLayout vlMainLayout) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting AuditRecord UI");
		btnSearch = new Button("Search", this);
		btnSearch.setStyleName("searchbt");
		btnAdd = new Button("Add");
		btnAdd.setEnabled(true);
		hlCmdBtnLayout.addComponent(btnAdd);
		hlCmdBtnLayout.setComponentAlignment(btnAdd, Alignment.MIDDLE_LEFT);
		btnEdit = new Button("Edit");
		btnEdit.setEnabled(true);
		hlCmdBtnLayout.addComponent(btnEdit);
		hlCmdBtnLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);
		btnAudit = new Button("Audit");
		btnAudit.setEnabled(true);
		hlCmdBtnLayout.addComponent(btnAudit);
		hlCmdBtnLayout.setComponentAlignment(btnAudit, Alignment.MIDDLE_LEFT);
		// old values Name text field
		tfSearchValues = new GERPTextField("Old values");
		tfSearchValues.setInputPrompt("filter");
		tfSearchValues.setWidth("200px");
		tableAditRecords = new Table();
		tableAditRecords.setSizeFull();
		tableAditRecords.setSelectable(true);
		tableAditRecords.setColumnCollapsingAllowed(true);
		tableAditRecords.setPageLength(9);
		tableAditRecords.setImmediate(true);
		tableAditRecords.setFooterVisible(true);
		// populate the Table combo box
		cbTableColumn = new ComboBox("Audit Fields");
		cbTableColumn.setWidth("148");
		cbTableColumn.setInputPrompt("Select");
		cbTableColumn.setImmediate(true);
		cbTableColumn.setNullSelectionAllowed(false);
		cbTableColumn.setItemCaptionPropertyId("scrFldName");
		loadColumnNames();
		cbTableColumn.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbTableColumn.getItem(itemId);
				if (item != null) {
					AuditConfigDM = (AuditConfigDM) item.getBean(); // Get the actual bean and use the data
				}
			}
		});
		assembleSearchLayout();
		setTableProperties();
		populatedAndConfig(false);
		vlMainLayout.setSpacing(true);
		vlMainLayout.addComponent(hlSearchLayout);
		vlMainLayout.addComponent(tableAditRecords);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * * Adding user input layout to the search layout as all the fields in the user input are available in the
		 * search block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn1.addComponent(cbTableColumn);
		flColumn2.addComponent(tfSearchValues);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(btnSearch);
		hlSearchLayout.setComponentAlignment(btnSearch, Alignment.MIDDLE_LEFT);
		hlSearchLayout.setSpacing(true);
	}
	
	public void populatedAndConfig(boolean search) {
		try {
			List<AuditRecordsDM> list = null;
			try {
				tableAditRecords.removeAllItems();
				list = new ArrayList<AuditRecordsDM>();
				columnName = AuditConfigDM.getColumnName().toString();
				oldValue = tfSearchValues.getValue();
			}
			catch (Exception e) {
				logger.info("papulate and config method " + e);
			}
			if (strTableName != null && companyId != null && columnName != null && oldValue != null || pkValue != null) {
				list = auditRecordsService.getAuditRecordsDMByTableName(null,strTableName, companyId, columnName, oldValue,
						pkValue);
			}
			beanAuditRecords = new BeanItemContainer<AuditRecordsDM>(AuditRecordsDM.class);
			beanAuditRecords.addAll(list);
			tableAditRecords.setContainerDataSource(beanAuditRecords);
			tableAditRecords.setVisibleColumns(new Object[] { "auditId", "oldCvalue", "newCvalue", "oldDtvalue",
					"newDtvalue", "oldNvalue", "newNvalue", "pkValue", "updatedDt", "updatedBy", });
			tableAditRecords.setColumnHeaders(new String[] { "History Id", "Old String", "New String", "Old Date",
					"New Date", "Old Number", "New Number", "PK Value", " Updated Date", " Updated By" });
			tableAditRecords.setColumnFooter("updatedBy", "No.of Records:" + list.size());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("papulate and config method  results" + e);
		}
	}
	
	private void setTableProperties() {
		tableAditRecords.setSelectable(true);
		tableAditRecords.setColumnWidth("auditId", 80);
		tableAditRecords.setColumnWidth("newCvalue", 150);
		tableAditRecords.setColumnWidth("oldCvalue", 150);
		tableAditRecords.setColumnAlignment("auditId", Align.RIGHT);
		tableAditRecords.setColumnAlignment("oldNvalue", Align.RIGHT);
		tableAditRecords.setColumnAlignment("newNvalue", Align.RIGHT);
		tableAditRecords.setColumnAlignment("pkValue", Align.RIGHT);
	}
	
	/**
	 * buildNotifications()-->this method is used for popup view for Download components
	 * 
	 * @param event
	 */
	private void buildNotifications(ClickEvent event) {
		notifications = new Window();
		VerticalLayout l = new VerticalLayout();
		l.setMargin(true);
		l.setSpacing(true);
		notifications.setWidth("178px");
		notifications.addStyleName("notifications");
		notifications.setClosable(false);
		notifications.setResizable(false);
		notifications.setDraggable(false);
		notifications.setPositionX(event.getClientX() - event.getRelativeX());
		notifications.setPositionY(event.getClientY() - event.getRelativeY());
		notifications.setCloseShortcut(KeyCode.ESCAPE, null);
		VerticalLayout vlDownload = new VerticalLayout();
		notifications.setContent(vlDownload);
	}
	
	private void loadColumnNames() {
		try {
			List<AuditConfigDM> configList = auditConfigService.getColumnNameByTableName(null,strTableName, companyId, null);
			beanAuditConfig = new BeanItemContainer<AuditConfigDM>(AuditConfigDM.class);
			beanAuditConfig.addAll(configList);
			cbTableColumn.setContainerDataSource(beanAuditConfig);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("load table column names" + e);
		}
	}
	
	public void buttonClick(ClickEvent event) {
		if (btnSearch == event.getButton()) {
			populatedAndConfig(false);
		} else if (btnDownload == event.getButton()) {
			event.getButton().removeStyleName("unread");
			if (notifications != null && notifications.getUI() != null) notifications.close();
			else {
				buildNotifications(event);
				UI.getCurrent().addWindow(notifications);
				notifications.focus();
				((VerticalLayout) UI.getCurrent().getContent()).addLayoutClickListener(new LayoutClickListener() {
					private static final long serialVersionUID = 1L;
					
					public void layoutClick(LayoutClickEvent event) {
						notifications.close();
						((VerticalLayout) UI.getCurrent().getContent()).removeLayoutClickListener(this);
					}
				});
			}
		}
	}
}
