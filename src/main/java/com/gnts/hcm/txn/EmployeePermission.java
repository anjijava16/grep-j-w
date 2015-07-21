/**
 * File Name 		: EmployeePermission.java 
 * Description 		: this class is used for add/update EmployeePermission details. 
 * Author 			: Karthikeyan R
 * Date 			: Sep 11, 2014
 * Modification 	:
 * Modified By 		: Karthikeyan R 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Sep 11, 2014         Karthikeyan R	          Initial Version
 */
package com.gnts.hcm.txn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.components.GERPTimeField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.EmployeePermissionDM;
import com.gnts.hcm.service.txn.EmployeePermissionService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class EmployeePermission extends VerticalLayout implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Declaration for add and edit panel components
	private ComboBox cbPermissionApprmgr;
	private PopupDateField dfPermissiondate;
	private TextArea taPermissionremarks;
	private GERPTimeField tfintime;
	private TextField tfPermissionprmhrs;
	private ComboBox cbPermissionStatus;
	// for Search
	private Button btnSearch, btnReset;
	// Declaration for add and edit panel
	private VerticalLayout vlTablePanel = new VerticalLayout();
	private HorizontalLayout hlsavecancel = new HorizontalLayout();
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Table Declaration
	public Table tblMstScrSrchRslt;
	public Button btnadd;
	public Button btnSave;
	public Button btnCancel;
	private List<EmployeePermissionDM> usertable = new ArrayList<EmployeePermissionDM>();
	private BeanItemContainer<EmployeePermissionDM> beans = null;
	private VerticalLayout vltable, vlTableForm, vlTableLayout;
	private HorizontalLayout hlTableTitleandCaptionLayout;
	private String username;
	private Long companyid;
	private EmployeePermissionService servicepermission = (EmployeePermissionService) SpringContextHelper
			.getBean("EmployeePermission");
	private EmployeeService serviceemployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private Logger logger = Logger.getLogger(EmployeePermissionDM.class);
	private int recordCnt = 0;
	private Long employeeid;
	
	public EmployeePermission(Long empid) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeePermission() constructor");
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeid = empid;
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting EmployeePermission UI");
		// Initialization for dfPermissiondate
		dfPermissiondate = new GERPPopupDateField("Permission Date");
		dfPermissiondate.setDateFormat("dd-MMM-yyyy");
		dfPermissiondate.setRequired(true);
		dfPermissiondate.setWidth("95");
		// Initialization for tfPermissionprmhrs
		tfPermissionprmhrs = new GERPTextField("Permission Hours");
		tfPermissionprmhrs.setWidth("100");
		tfPermissionprmhrs.setRequired(true);
		// Initialization for tfintime
		tfintime = new GERPTimeField("In Time");
		tfintime.setRequired(true);
		// Initialization for taPermissionremarks
		taPermissionremarks = new GERPTextArea("Remarks");
		taPermissionremarks.setWidth("170");
		taPermissionremarks.setHeight("55");
		// Initialization for cbPermissionApprmgr
		cbPermissionApprmgr = new GERPComboBox("Approve manager");
		cbPermissionApprmgr.setItemCaptionPropertyId("fullname");
		cbPermissionApprmgr.setWidth("100");
		cbPermissionApprmgr.setRequired(true);
		loadAppMgrList();
		// Initialization for cbPermissionStatus
		cbPermissionStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbPermissionStatus.setItemCaptionPropertyId("desc");
		btnSearch = new Button("Search", this);
		btnSearch.setStyleName("searchbt");
		btnReset = new Button("Reset", this);
		btnReset.setStyleName("resetbt");
		// Initialization for btnSave
		btnSave = new Button("Save", this);
		btnSave.setDescription("Save");
		btnSave.setStyleName("savebt");
		// Initialization for btnCancel
		btnCancel = new Button("Cancel", this);
		btnCancel.setDescription("Cancel");
		btnCancel.setStyleName("cancelbt");
		hlsavecancel = new HorizontalLayout();
		hlsavecancel.addComponent(btnSave);
		hlsavecancel.addComponent(btnCancel);
		hlsavecancel.setVisible(false);
		// label,add,edit and download panel
		btnadd = new GERPButton("Add", "add", this);
		btnadd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee Permission
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDetails()) {
					savePermission();
				}
			}
		});
		hlTableTitleandCaptionLayout = new HorizontalLayout();
		// Initialization for table panel components
		tblMstScrSrchRslt = new Table();
		tblMstScrSrchRslt.setSizeFull();
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.setColumnCollapsingAllowed(true);
		tblMstScrSrchRslt.setPageLength(10);
		tblMstScrSrchRslt.setStyleName(Runo.TABLE_SMALL);
		tblMstScrSrchRslt.setWidth("100%");
		tblMstScrSrchRslt.setImmediate(true);
		tblMstScrSrchRslt.setFooterVisible(true);
		vltable = new VerticalLayout();
		vltable.setSizeFull();
		vltable.setMargin(true);
		vltable.addComponent(hlTableTitleandCaptionLayout);
		vltable.addComponent(tblMstScrSrchRslt);
		vlTablePanel.addComponent(vltable);
		// ClickListener for Employee Permission Tale
		tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
					tblMstScrSrchRslt.setImmediate(true);
					btnadd.setCaption("Add");
					btnadd.setStyleName("savebt");
					resetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnadd.setCaption("Update");
					btnadd.setStyleName("savebt");
					editPermission();
				}
			}
		});
		vlTableForm = new VerticalLayout();
		vlTableForm.setSizeFull();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(dfPermissiondate);
		flColumn1.addComponent(tfintime);
		flColumn2.addComponent(tfPermissionprmhrs);
		flColumn2.addComponent(cbPermissionApprmgr);
		flColumn3.addComponent(taPermissionremarks);
		flColumn4.addComponent(cbPermissionStatus);
		HorizontalLayout Input = new HorizontalLayout();
		Input.addComponent(flColumn1);
		Input.addComponent(flColumn2);
		Input.addComponent(flColumn3);
		Input.addComponent(flColumn4);
		Input.addComponent(btnadd);
		Input.setComponentAlignment(btnadd, Alignment.BOTTOM_LEFT);
		Input.setSpacing(true);
		Input.setMargin(true);
		Input.setWidth("100%");
		vlTableForm.addComponent(Input);
		vlTableForm.addComponent(tblMstScrSrchRslt);
		vlTableLayout = new VerticalLayout();
		vlTableLayout.addComponent(vlTableForm);
		addComponent(vlTableLayout);
		loadSrchRslt();
		btnadd.setStyleName("add");
		resetFields();
	}
	
	private void loadAppMgrList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "loading Approve Manager List...");
		List<EmployeeDM> employeelist = serviceemployee.getEmployeeList(null, null, null,/* department */
				"Active", companyid, null, null, null, null, "P");
		BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmployee.setBeanIdProperty("employeeid");
		beanEmployee.addAll(employeelist);
		cbPermissionApprmgr.setContainerDataSource(beanEmployee);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "loading SearchResult Details...");
		tblMstScrSrchRslt.removeAllItems();
		if (employeeid != null) {
			usertable = servicepermission.getemppermissionList(null, employeeid, "Active", "F");
		}
		recordCnt = usertable.size();
		tblMstScrSrchRslt.setPageLength(10);
		beans = new BeanItemContainer<EmployeePermissionDM>(EmployeePermissionDM.class);
		beans.addAll(usertable);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Employee Permission. result set");
		tblMstScrSrchRslt.setContainerDataSource(beans);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "permissiondt", "intime", "permissionhrs", "remarks",
				"emppermstatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Permission Date", "In Time", "Permission Hours", "Remarks",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Method used to display selected row's values in desired text box and combo box for edit the values
	private void editPermission() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing Permission.......");
		if (tblMstScrSrchRslt.getValue() != null) {
			EmployeePermissionDM employeePermissionDM = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
			dfPermissiondate.setValue(employeePermissionDM.getPermissiondate());
			tfPermissionprmhrs.setValue((employeePermissionDM.getPermissionhrs().toString()));
			tfintime.setTime(employeePermissionDM.getIntime());
			taPermissionremarks.setValue(employeePermissionDM.getRemarks());
			cbPermissionApprmgr.setValue(employeePermissionDM.getApprovemgr());
			cbPermissionStatus.setValue(employeePermissionDM.getEmppermstatus());
		}
	}
	
	// Save Method for save and update the Asset Specification details
	private void savePermission() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Saving Permission details......");
		try {
			EmployeePermissionDM savePermission = new EmployeePermissionDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				savePermission = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
				usertable.remove(savePermission);
			}
			if (dfPermissiondate.getValue() != null) {
				savePermission.setPermissiondt(dfPermissiondate.getValue());
			}
			if (tfPermissionprmhrs.getValue() != null) {
				savePermission.setPermissionhrs((new BigDecimal(tfPermissionprmhrs.getValue())));
			}
			if (tfintime.getValue() != null) {
				savePermission.setIntime(tfintime.getHorsMunites());
			}
			if (taPermissionremarks.getValue() != null) {
				savePermission.setRemarks(taPermissionremarks.getValue());
			}
			if (cbPermissionApprmgr.getValue() != null) {
				savePermission.setApprovemgr((Long.valueOf(cbPermissionApprmgr.getValue().toString())));
			}
			if (cbPermissionStatus.getValue() != null) {
				savePermission.setEmppermstatus((String) cbPermissionStatus.getValue());
			}
			savePermission.setEmployeeid(employeeid);
			savePermission.setLastupdatedby(username);
			savePermission.setLastupdateddt(DateUtils.getcurrentdate());
			servicepermission.saveAndUpdate(savePermission);
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		resetFields();
		btnadd.setCaption("Add");
	}
	
	public void permissionsave(Long employeeid) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "EmployeeOPermission Save details......");
		@SuppressWarnings("unchecked")
		Collection<EmployeePermissionDM> itemIds = (Collection<EmployeePermissionDM>) tblMstScrSrchRslt
				.getVisibleItemIds();
		for (EmployeePermissionDM savepermission : (Collection<EmployeePermissionDM>) itemIds) {
			savepermission.setEmployeeid(employeeid);
			servicepermission.saveAndUpdate(savepermission);
		}
		loadSrchRslt();
		tblMstScrSrchRslt.removeAllItems();
	}
	
	public boolean validateDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Validating EmployeePermission Details.....");
		boolean errorFlag = true;
		dfPermissiondate.setComponentError(null);
		tfintime.setComponentError(null);
		tfPermissionprmhrs.setComponentError(null);
		cbPermissionApprmgr.setComponentError(null);
		if (dfPermissiondate.getValue() == null) {
			dfPermissiondate.setComponentError(new UserError(GERPErrorCodes.NULL_PERMIS_PRMDATE));
			errorFlag = false;
		}
		if (tfintime.getValue() == null) {
			tfintime.setComponentError(new UserError(GERPErrorCodes.NULL_PERMIS_INTIM));
			errorFlag = false;
		}
		if ((tfPermissionprmhrs.getValue() == null) || tfPermissionprmhrs.getValue().trim().length() == 0) {
			tfPermissionprmhrs.setComponentError(new UserError(GERPErrorCodes.NULL_PERMIS_TOTHRS));
			errorFlag = false;
		}
		if (cbPermissionApprmgr.getValue() == null) {
			cbPermissionApprmgr.setComponentError(new UserError(GERPErrorCodes.NULL_PERMIS_APPMGR));
			errorFlag = false;
		}
		return errorFlag;
	}
	
	public void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Reseting Fields.....");
		dfPermissiondate.setValue(null);
		dfPermissiondate.setComponentError(null);
		tfPermissionprmhrs.setValue("0");
		tfPermissionprmhrs.setComponentError(null);
		tfintime.setValue(null);
		tfintime.setComponentError(null);
		taPermissionremarks.setValue("");
		cbPermissionApprmgr.setValue(null);
		cbPermissionApprmgr.setComponentError(null);
		cbPermissionStatus.setValue(cbPermissionStatus.getItemIds().iterator().next());
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
	}
}