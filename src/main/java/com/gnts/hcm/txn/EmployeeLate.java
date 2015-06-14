/**
 * File Name 		: EmployeeLate.java 
 * Description 		: this class is used for add/update EmployeeLate details. 
 * Author 			: Karthikeyan R
 * Date 			: Sep 14, 2014
 * Modification 	:
 * Modified By 		: Karthikeyan R 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Sep 14, 2014         Karthikeyan R	          Initial Version
 */
package com.gnts.hcm.txn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
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
import com.gnts.hcm.domain.txn.EmployeeLateDetailDM;
import com.gnts.hcm.service.txn.EmployeeLateDetailService;
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
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class EmployeeLate extends VerticalLayout implements ClickListener {
	// Declaration for add and edit panel components
	private ComboBox cbLatestatus;
	private PopupDateField dfLatedate;
	private GERPTimeField tfLateintime;
	private TextField tfLatehrs;
	private TextArea taLateRemarks;
	// for Search
	Button btnSearch, btnReset;
	// Declaration for add and edit panel
	VerticalLayout vlAddEditPanel = new VerticalLayout();
	VerticalLayout vlTablePanel = new VerticalLayout();
	HorizontalLayout hlsavecancel = new HorizontalLayout();
	HorizontalLayout hlFileDownloadLayout;
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3;
	// Table Declaration
	public Table tblMstScrSrchRslt;
	// private Button btnAdd, btnSave,btnCancel;
	public Button btnadd;
	public Button btnSave = new Button("Save", this);
	public Button btnCancel = new Button("Cancel", this);
	List<EmployeeLateDetailDM> usertable = new ArrayList<EmployeeLateDetailDM>();
	private BeanItemContainer<EmployeeLateDetailDM> beans = null;
	private VerticalLayout vltable, vlTableForm, vlTableLayout;
	HorizontalLayout hlTableTitleandCaptionLayout;
	private String username;
	private Long companyid;
	private Long employeeid;
	EmployeeLateDetailService serviceLate = (EmployeeLateDetailService) SpringContextHelper
			.getBean("EmployeeLateDetail");
	EmployeeService serviceemployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private static Logger logger = Logger.getLogger(EmployeeLateDetailDM.class);
	private int total = 0;
	public HorizontalLayout hlHeader = new HorizontalLayout();
	
	public EmployeeLate(Long empid) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeLate() constructor");
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeid = empid;
		buildView();
	}
	
	@SuppressWarnings("unused")
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting EmployeeLate UI");
		// Initialization for dfLatedate
		dfLatedate = new GERPPopupDateField("Late Date");
		dfLatedate.setDateFormat("dd-MMM-yyyy");
		dfLatedate.setRequired(true);
		// Initialization for tfLateintime
		tfLateintime = new GERPTimeField("In Time");
		tfLateintime.setRequired(true);
		// Initialization for tfLatehrs
		tfLatehrs = new GERPTextField("Late Hours");
		tfLatehrs.setRequired(true);
		tfLatehrs.setWidth("110");
		// Initialization for taLateRemarks
		taLateRemarks = new GERPTextArea("Remarks");
		taLateRemarks.setWidth("170");
		taLateRemarks.setHeight("55");
		// Initialization for cbLatestatus
		cbLatestatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbLatestatus.setItemCaptionPropertyId("desc");
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
		btnadd = new GERPButton("Add", "add", this);
		btnadd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee Permission
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDetails()) {
					saveLate();
				}
			}
		});
		HorizontalLayout hlTableCaptionLayout = new HorizontalLayout();
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
		// ClickListener for Employee Late Tale
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
					editLate();
				}
			}
		});
		vlTableForm = new VerticalLayout();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(dfLatedate);
		flColumn1.addComponent(tfLateintime);
		flColumn3.addComponent(tfLatehrs);
		flColumn3.addComponent(cbLatestatus);
		flColumn2.addComponent(taLateRemarks);
		HorizontalLayout Input = new HorizontalLayout();
		Input.setMargin(true);
		Input.setWidth("100%");
		Input.addComponent(flColumn1);
		Input.addComponent(flColumn2);
		Input.addComponent(flColumn3);
		Input.setSpacing(true);
		Input.setMargin(true);
		Input.setWidth("100%");
		Input.addComponent(btnadd);
		Input.setComponentAlignment(btnadd, Alignment.BOTTOM_LEFT);
		vlTableForm.addComponent(Input);
		vlTableForm.addComponent(tblMstScrSrchRslt);
		vlTableLayout = new VerticalLayout();
		vlTableLayout.addComponent(vlTableForm);
		addComponent(vlTableLayout);
		loadSrchRslt();
		btnadd.setStyleName("add");
		resetFields();
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "loading SearchResult Details...");
		total = 0;
		if (employeeid != null) {
			usertable = serviceLate.getemplatelist(null, employeeid, null, "Active", "F");
			total = usertable.size();
		}
		tblMstScrSrchRslt.setPageLength(10);
		beans = new BeanItemContainer<EmployeeLateDetailDM>(EmployeeLateDetailDM.class);
		beans.addAll(usertable);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Employee Late. result set");
		tblMstScrSrchRslt.setContainerDataSource(beans);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + total);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "latedate", "intime", "latehrs", "laterks", "latestatus",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Late Date", "In Time", "Late Hours", "Remarks", "Status",
				"Last Updated Date", "Last Updates By" });
		tblMstScrSrchRslt.setColumnAlignment("lateid", Align.RIGHT);
	}
	
	// Method used to display selected row's values in desired text box and combo box for edit the values
	private void editLate() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing Late.......");
		if (tblMstScrSrchRslt.getValue() != null) {
			EmployeeLateDetailDM lateDM = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
			dfLatedate.setValue(lateDM.getLatedat());
			tfLateintime.setTime(lateDM.getIntime());
			tfLatehrs.setValue(lateDM.getLatehrs().toString());
			taLateRemarks.setValue(lateDM.getLaterks());
			cbLatestatus.setValue(lateDM.getLatestatus());
		}
	}
	
	private void saveLate() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Late details......");
		try {
			validateDetails();
			EmployeeLateDetailDM saveLate = new EmployeeLateDetailDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				saveLate = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
				usertable.remove(saveLate);
			}
			if (dfLatedate.getValue() != null) {
				saveLate.setLatedate(dfLatedate.getValue());
			}
			if (tfLateintime.getValue() != null) {
				saveLate.setIntime(tfLateintime.getHorsMunites());
			}
			if (tfLatehrs.getValue() != null) {
				saveLate.setLatehrs((new BigDecimal(tfLatehrs.getValue())));
			}
			if (taLateRemarks.getValue() != null) {
				saveLate.setLaterks(taLateRemarks.getValue());
			}
			if (cbLatestatus.getValue() != null) {
				saveLate.setLatestatus((String) cbLatestatus.getValue());
			}
			saveLate.setEmployeeid(employeeid);
			saveLate.setLastupdatedby(username);
			saveLate.setLastupdateddt(DateUtils.getcurrentdate());
			serviceLate.saveAndUpdate(saveLate);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void latesave(Long employeeid) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "EmployeeLate Save details......");
		@SuppressWarnings("unchecked")
		Collection<EmployeeLateDetailDM> itemIds = (Collection<EmployeeLateDetailDM>) tblMstScrSrchRslt
				.getVisibleItemIds();
		for (EmployeeLateDetailDM saveLate : (Collection<EmployeeLateDetailDM>) itemIds) {
			saveLate.setEmployeeid(employeeid);
			serviceLate.saveAndUpdate(saveLate);
		}
		loadSrchRslt();
		tblMstScrSrchRslt.removeAllItems();
	}
	
	public boolean validateDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Validating EmployeeLate Details.....");
		boolean errorFlag = true;
		dfLatedate.setComponentError(null);
		tfLatehrs.setComponentError(null);
		tfLateintime.setComponentError(null);
		if (dfLatedate.getValue() == null) {
			dfLatedate.setComponentError(new UserError(GERPErrorCodes.NULL_LATE_LTDATE));
			errorFlag = false;
		}
		if (tfLateintime.getValue() == null) {
			tfLateintime.setComponentError(new UserError(GERPErrorCodes.NULL_LATE_LTINTIME));
			errorFlag = false;
		}
		if ((tfLatehrs.getValue() == null) || tfLatehrs.getValue().trim().length() == 0) {
			tfLatehrs.setComponentError(new UserError(GERPErrorCodes.NULL_LATE_LTHOUR));
			errorFlag = false;
		}
		return errorFlag;
	}
	
	public void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Reseting Fields.....");
		dfLatedate.setValue(null);
		tfLateintime.setValue(null);
		tfLatehrs.setValue("0");
		taLateRemarks.setValue("");
		cbLatestatus.setValue(cbLatestatus.getItemIds().iterator().next());
		btnadd.setCaption("Add");
		btnadd.setStyleName("add");
		dfLatedate.setComponentError(null);
		tfLatehrs.setComponentError(null);
		tfLateintime.setComponentError(null);
		total = usertable.size();
		total = 0;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
	}
}