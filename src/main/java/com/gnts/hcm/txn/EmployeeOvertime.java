/**
 * File Name 		: EmployeeOvertime.java 
 * Description 		: this class is used for add/update EmployeeOvertime details. 
 * Author 			: Karthikeyan R
 * Date 			: Sep 12, 2014
 * Modification 	:
 * Modified By 		: Karthikeyan R 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Sep 12, 2014         Karthikeyan R	          Initial Version
 */
package com.gnts.hcm.txn;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import com.gnts.hcm.domain.txn.EmployeeOvertimeDM;
import com.gnts.hcm.domain.txn.EmployeePermissionDM;
import com.gnts.hcm.service.txn.EmployeeOvertimeService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class EmployeeOvertime extends VerticalLayout implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Declaration for add and edit panel components
	private ComboBox cbOvertimeapprovemgr;
	private PopupDateField dfovertimedt;
	private GERPTimeField tfstarthour, tfendhour;
	private TextArea taOvertimeremarks;
	private TextField tfOvertimetotalhours;
	private ComboBox cbOvertimestatus;
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
	private List<EmployeeOvertimeDM> usertable = new ArrayList<EmployeeOvertimeDM>();
	private BeanItemContainer<EmployeeOvertimeDM> beans = null;
	private VerticalLayout vltable, vlTableForm, vlTableLayout;
	private HorizontalLayout hlTableTitleandCaptionLayout;
	private String username;
	private Long companyid;
	private EmployeeOvertimeService serviceovertime = (EmployeeOvertimeService) SpringContextHelper
			.getBean("EmployeeOvertime");
	private EmployeeService serviceemployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private Logger logger = Logger.getLogger(EmployeePermissionDM.class);
	private int total = 0;
	private Long employeeid;
	
	public EmployeeOvertime(Long empid) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeOvertime() constructor");
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeid = empid;
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting EmployeeOvertime UI");
		// Initialization for dfovertimedate
		dfovertimedt = new GERPPopupDateField("Permission date");
		dfovertimedt.setDateFormat("dd-MMM-yyyy");
		dfovertimedt.setRequired(true);
		dfovertimedt.setWidth("95");
		// Initialization for tfstarthour
		tfstarthour = new GERPTimeField("Start Hour");
		tfstarthour.setRequired(true);
		// Initialization for tfendhour
		tfendhour = new GERPTimeField("End Hour");
		tfendhour.setImmediate(true);
		tfendhour.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				try {
					tfOvertimetotalhours.setReadOnly(false);
					tfOvertimetotalhours.setValue(timediff(tfstarthour.getHorsMunitesinLong(),
							tfendhour.getHorsMunitesinLong()));
					tfOvertimetotalhours.setReadOnly(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		tfendhour.setRequired(true);
		// Initialization for tfOvertimetotalhours
		tfOvertimetotalhours = new GERPTextField("Total Hours");
		tfOvertimetotalhours.setReadOnly(true);
		// tfOvertimetotalhours.setRequired(true);
		tfOvertimetotalhours.setWidth("95");
		// Initialization for taOvertimeremarks
		taOvertimeremarks = new GERPTextArea("Remarks");
		taOvertimeremarks.setWidth("170");
		taOvertimeremarks.setHeight("55");
		// Initialization for cbOvertimeapprovemgr
		cbOvertimeapprovemgr = new GERPComboBox("Approve Manager");
		cbOvertimeapprovemgr.setItemCaptionPropertyId("fullname");
		// cbOvertimeapprovemgr.setWidth("130");
		cbOvertimeapprovemgr.setRequired(true);
		loadAppMgrList();
		// Initialization for cbOvertimestatus
		cbOvertimestatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbOvertimestatus.setItemCaptionPropertyId("desc");
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
		// Initialization for btnHome
		btnadd = new GERPButton("Add", "add", this);
		btnadd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee Overtime
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDetails()) {
					saveOvertime();
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
		// ClickListener for Employee Overtime Tale
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
					editOvertime();
				}
			}
		});
		vlTableForm = new VerticalLayout();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(dfovertimedt);
		flColumn1.addComponent(tfOvertimetotalhours);
		flColumn2.addComponent(tfstarthour);
		flColumn2.addComponent(tfendhour);
		flColumn3.addComponent(taOvertimeremarks);
		flColumn4.addComponent(cbOvertimeapprovemgr);
		flColumn4.addComponent(cbOvertimestatus);
		HorizontalLayout Input = new HorizontalLayout();
		Input.addComponent(flColumn1);
		Input.addComponent(flColumn2);
		Input.addComponent(flColumn3);
		Input.addComponent(flColumn4);
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
	
	@SuppressWarnings("unused")
	private String timediff(Double timin, Double timout) {
		Double timindiff = timin / 100;
		Double tioutdiff = timout / 100;
		tioutdiff = (timindiff > tioutdiff) ? (tioutdiff + 24) : tioutdiff;
		Double min_1 = timin % 100;
		Double min_2 = timout % 100;
		Double diffmin, diffhr;
		if (min_2 >= min_1) {
			diffmin = min_2 - min_1;
		} else {
			diffmin = (min_2 + 60) - min_1;
			tioutdiff--;
		}
		diffhr = tioutdiff - timindiff;
		String numhr = diffhr < 10 ? "0" + diffhr : "" + diffhr;
		String nummin = diffmin < 10 ? "0" + diffmin : "" + diffmin;
		// txtTotWrkHrs.setReadOnly(false);
		DecimalFormat df = new DecimalFormat("#.##");
		return (df.format(Double.valueOf(nummin)));
		// txtTotWrkHrs.setReadOnly(true);
	}
	
	private void loadAppMgrList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "loading Approve Manager List...");
		List<EmployeeDM> employeelist = serviceemployee.getEmployeeList(null, null, null,/* department */
				"Active", companyid, null, null, null, null, "F");
		BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmployee.setBeanIdProperty("employeeid");
		beanEmployee.addAll(employeelist);
		cbOvertimeapprovemgr.setContainerDataSource(beanEmployee);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "loading SearchResult Details...");
		total = 0;
		if (employeeid != null) {
			usertable = serviceovertime.geempovrlist(null, employeeid, null, "Active", "F");
			total = usertable.size();
		}
		tblMstScrSrchRslt.setPageLength(10);
		beans = new BeanItemContainer<EmployeeOvertimeDM>(EmployeeOvertimeDM.class);
		beans.addAll(usertable);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Employee Overtime. result set");
		tblMstScrSrchRslt.setContainerDataSource(beans);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "overtimedt", "starthour", "endhour", "totalhours",
				"otremarks", "empotstatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "OverTime Date", "Start hour", "End Hour", "Total Hours",
				"Remarks", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("overtimeid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + total);
	}
	
	// Method used to display selected row's values in desired text box and combo box for edit the values
	private void editOvertime() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing Overtime.......");
		if (tblMstScrSrchRslt.getValue() != null) {
			EmployeeOvertimeDM employeeOvertimeDM = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
			dfovertimedt.setValue(employeeOvertimeDM.getOvertimedate());
			tfstarthour.setTime(employeeOvertimeDM.getStarthour());
			tfendhour.setTime(employeeOvertimeDM.getEndhour());
			tfOvertimetotalhours.setReadOnly(false);
			tfOvertimetotalhours.setValue(employeeOvertimeDM.getTotalhours().toString());
			tfOvertimetotalhours.setReadOnly(true);
			taOvertimeremarks.setValue(employeeOvertimeDM.getOtremarks());
			cbOvertimeapprovemgr.setValue(employeeOvertimeDM.getApprovemgr());
			cbOvertimestatus.setValue(employeeOvertimeDM.getEmpotstatus());
		}
	}
	
	private void saveOvertime() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Saving Overtime details......");
		try {
			EmployeeOvertimeDM saveOvertime = new EmployeeOvertimeDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				saveOvertime = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
				usertable.remove(saveOvertime);
			}
			if (dfovertimedt.getValue() != null) {
				saveOvertime.setOvertimedt(dfovertimedt.getValue());
			}
			if (tfstarthour.getValue() != null) {
				saveOvertime.setStarthour(tfstarthour.getHorsMunites());
			}
			if (tfendhour.getValue() != null) {
				saveOvertime.setEndhour(tfendhour.getHorsMunites());
			}
			if (tfOvertimetotalhours.getValue() != null) {
				saveOvertime.setTotalhours((new BigDecimal(tfOvertimetotalhours.getValue())));
			}
			if (taOvertimeremarks.getValue() != null) {
				saveOvertime.setOtremarks(taOvertimeremarks.getValue());
			}
			if (cbOvertimeapprovemgr.getValue() != null) {
				saveOvertime.setApprovemgr((Long.valueOf(cbOvertimeapprovemgr.getValue().toString())));
			}
			if (cbOvertimestatus.getValue() != null) {
				saveOvertime.setEmpotstatus((String) cbOvertimestatus.getValue());
			}
			saveOvertime.setEmployeeid(employeeid);
			saveOvertime.setLastupdatedby(username);
			saveOvertime.setLastupdateddt(DateUtils.getcurrentdate());
			serviceovertime.saveAndUpdate(saveOvertime);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void overtimesave(Long employeeid) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "EmployeeOvertime Save details......");
		@SuppressWarnings("unchecked")
		Collection<EmployeeOvertimeDM> itemIds = (Collection<EmployeeOvertimeDM>) tblMstScrSrchRslt.getVisibleItemIds();
		for (EmployeeOvertimeDM saveovertime : (Collection<EmployeeOvertimeDM>) itemIds) {
			saveovertime.setEmployeeid(employeeid);
			serviceovertime.saveAndUpdate(saveovertime);
		}
		loadSrchRslt();
		tblMstScrSrchRslt.removeAllItems();
	}
	
	public boolean validateDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Validating EmployeeOvertime Details.....");
		boolean errorFlag = true;
		dfovertimedt.setComponentError(null);
		tfstarthour.setComponentError(null);
		tfendhour.setComponentError(null);
		tfOvertimetotalhours.setComponentError(null);
		cbOvertimeapprovemgr.setComponentError(null);
		if (dfovertimedt.getValue() == null) {
			dfovertimedt.setComponentError(new UserError(GERPErrorCodes.NULL_OTIME_OTDATE));
			errorFlag = false;
		}
		if (tfstarthour.getValue() == null) {
			tfstarthour.setComponentError(new UserError(GERPErrorCodes.NULL_OTIME_STRHOUR));
			errorFlag = false;
		}
		if (tfendhour.getValue() == null) {
			tfendhour.setComponentError(new UserError(GERPErrorCodes.NULL_OTIME_EDHOUR));
			errorFlag = false;
		}
		if ((tfOvertimetotalhours.getValue() == null) || tfOvertimetotalhours.getValue().trim().length() == 0) {
			tfOvertimetotalhours.setComponentError(new UserError(GERPErrorCodes.NULL_OTIME_TOTHOUR));
			errorFlag = false;
		}
		if (cbOvertimeapprovemgr.getValue() == null) {
			cbOvertimeapprovemgr.setComponentError(new UserError(GERPErrorCodes.NULL_OTIME_APPMGR));
			errorFlag = false;
		}
		return errorFlag;
	}
	
	public void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Reseting Fields.....");
		dfovertimedt.setValue(null);
		tfstarthour.setValue(null);
		tfendhour.setValue(null);
		tfOvertimetotalhours.setReadOnly(false);
		tfOvertimetotalhours.setValue(null);
		tfOvertimetotalhours.setReadOnly(true);
		taOvertimeremarks.setValue("");
		cbOvertimeapprovemgr.setValue(null);
		btnadd.setCaption("Add");
		btnadd.setStyleName("add");
		dfovertimedt.setComponentError(null);
		tfstarthour.setComponentError(null);
		tfendhour.setComponentError(null);
		tfOvertimetotalhours.setComponentError(null);
		cbOvertimeapprovemgr.setComponentError(null);
		cbOvertimestatus.setValue(cbOvertimestatus.getItemIds().iterator().next());
		total = usertable.size();
		total = 0;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
	}
}