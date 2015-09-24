/**
 * File Name 		: EmployeeAbsent.java 
 * Description 		: this class is used for add/update EmployeeAbsent details. 
 * Author 			: Karthikeyan R
 * Date 			: Sep 13, 2014
 * Modification 	:
 * Modified By 		: Karthikeyan R 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Sep 13, 2014         Karthikeyan R	          Initial Version
 */
package com.gnts.hcm.txn;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
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
import com.gnts.hcm.domain.txn.EmployeeAbsentDM;
import com.gnts.hcm.service.txn.EmployeeAbsentService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
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

public class EmployeeAbsent extends VerticalLayout implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Declaration for add and edit panel components
	private CheckBox cbAbsentlwpmark;
	private PopupDateField dfAbsentdate;
	private GERPTimeField tfAbsentStartHours, tfAbsentEndHours;
	private TextField tfAbsentTotalHours;
	private TextArea taAbsentRemarks;
	private ComboBox cbAbsentStatus;
	// for Search
	private Button btnSearch, btnReset;
	// Declaration for add and edit panel
	private VerticalLayout vlTablePanel = new VerticalLayout();
	private HorizontalLayout hlsavecancel = new HorizontalLayout();
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Table Declaration
	private Table tblMstScrSrchRslt;
	// private Button btnAdd,btnSave,btnCancel ;
	private Button btnadd;
	private Button btnSave = new Button("Save", this);
	private Button btnCancel = new Button("Cancel", this);
	private List<EmployeeAbsentDM> listEmpAbsent = new ArrayList<EmployeeAbsentDM>();
	private BeanItemContainer<EmployeeAbsentDM> beans = null;
	private VerticalLayout vltable, vlTableForm, vlTableLayout;
	private HorizontalLayout hlTableTitleandCaptionLayout;
	private String username;
	private Long companyid;
	private Long employeeid;
	private EmployeeAbsentService serviceEmpAbsent = (EmployeeAbsentService) SpringContextHelper
			.getBean("EmployeeAbsent");
	private Logger logger = Logger.getLogger(EmployeeAbsentDM.class);
	private int total = 0;
	
	public EmployeeAbsent(Long empid) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeAbsent() constructor");
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeid = empid;
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting EmployeeAbsent UI");
		// Initialization for dfAbsentdate
		dfAbsentdate = new GERPPopupDateField("Absent Date");
		dfAbsentdate.setDateFormat("dd-MMM-yyyy");
		dfAbsentdate.setRequired(true);
		// Initialization for tfAbsentstarthours
		tfAbsentStartHours = new GERPTimeField("Start Hours");
		tfAbsentStartHours.setRequired(true);
		// Initialization for tfAbsentendhours
		tfAbsentEndHours = new GERPTimeField("End Hours");
		tfAbsentEndHours.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				try {
					tfAbsentTotalHours.setReadOnly(false);
					tfAbsentTotalHours.setValue(timediff(tfAbsentStartHours.getHorsMunitesinLong(),
							tfAbsentEndHours.getHorsMunitesinLong()));
					tfAbsentTotalHours.setReadOnly(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		tfAbsentEndHours.setRequired(true);
		// Initialization for tfAbsenttotalhours
		tfAbsentTotalHours = new GERPTextField("Total Hours");
		tfAbsentTotalHours.setWidth("120");
		tfAbsentTotalHours.setRequired(true);
		// Initialization for cbAbsentlwpmark
		cbAbsentlwpmark = new CheckBox("LWP");
		// Initialization for taAbsentremarks
		taAbsentRemarks = new GERPTextArea("Remarks");
		taAbsentRemarks.setWidth("170");
		taAbsentRemarks.setHeight("55");
		// Initialization for cbabsentstatus
		cbAbsentStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbAbsentStatus.setItemCaptionPropertyId("desc");
		btnSearch = new Button("Search", this);
		btnSearch.setStyleName("searchbt");
		btnReset = new Button("Reset", this);
		btnReset.setStyleName("resetbt");
		// Initialization for btnSave
		btnSave.setDescription("Save");
		btnSave.setStyleName("savebt");
		// Initialization for btnCancel
		btnCancel.setDescription("Cancel");
		btnCancel.setStyleName("cancelbt");
		hlsavecancel = new HorizontalLayout();
		hlsavecancel.addComponent(btnSave);
		hlsavecancel.addComponent(btnCancel);
		hlsavecancel.setVisible(false);
		btnadd = new GERPButton("Add", "add", this);
		btnadd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee Absent
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDetails()) {
					saveAbsent();
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
		// ClickListener for Employee Absent Tale
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
					editAbsent();
				}
			}
		});
		vlTableForm = new VerticalLayout();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(dfAbsentdate);
		flColumn1.addComponent(tfAbsentTotalHours);
		flColumn2.addComponent(tfAbsentStartHours);
		flColumn2.addComponent(tfAbsentEndHours);
		flColumn3.addComponent(taAbsentRemarks);
		flColumn4.addComponent(cbAbsentlwpmark);
		flColumn4.addComponent(cbAbsentStatus);
		HorizontalLayout hlInput = new HorizontalLayout();
		hlInput.setSpacing(true);
		hlInput.setMargin(true);
		hlInput.setWidth("100%");
		hlInput.addComponent(flColumn1);
		hlInput.addComponent(flColumn2);
		hlInput.addComponent(flColumn3);
		hlInput.addComponent(flColumn4);
		hlInput.addComponent(btnadd);
		hlInput.setComponentAlignment(btnadd, Alignment.BOTTOM_LEFT);
		vlTableForm.addComponent(hlInput);
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
		DecimalFormat df = new DecimalFormat("#.##");
		return (df.format(Double.valueOf(nummin)));
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "loading SearchResult Details...");
			total = 0;
			if (employeeid != null) {
				listEmpAbsent = serviceEmpAbsent.getempabslist(null, employeeid, "Active", "F");
				total = listEmpAbsent.size();
			}
			tblMstScrSrchRslt.setPageLength(10);
			beans = new BeanItemContainer<EmployeeAbsentDM>(EmployeeAbsentDM.class);
			beans.addAll(listEmpAbsent);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Employee Absent. result set");
			tblMstScrSrchRslt.setContainerDataSource(beans);
			tblMstScrSrchRslt.setVisibleColumns("absentdate", "starthours", "endhours", "totalhours", "absentremarks",
					"absentstatus", "lastupdatedtd", "lastupdatedby");
			tblMstScrSrchRslt.setColumnHeaders("Absent Date", "Start Hour", "End Hour", "Total Hours", "Remarks",
					"Status", "Last Updated Date", "Last Updated By");
			tblMstScrSrchRslt.setColumnAlignment("absentid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + total);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method used to display selected row's values in desired text box and combo box for edit the values
	private void editAbsent() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing Absent.......");
			if (tblMstScrSrchRslt.getValue() != null) {
				EmployeeAbsentDM absent = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
				dfAbsentdate.setValue(absent.getAbsentdte());
				tfAbsentStartHours.setTime(absent.getStarthours());
				tfAbsentEndHours.setTime(absent.getEndhours());
				tfAbsentTotalHours.setValue(absent.getTotalhours().toString());
				if (absent.getLwpmark().equals("Y")) {
					cbAbsentlwpmark.setValue(true);
				} else {
					cbAbsentlwpmark.setValue(false);
				}
				taAbsentRemarks.setValue(absent.getAbsentremarks());
				cbAbsentStatus.setValue(absent.getAbsentstatus());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void saveAbsent() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Absent details......");
		try {
			EmployeeAbsentDM saveAbsent = new EmployeeAbsentDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				saveAbsent = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
				listEmpAbsent.remove(saveAbsent);
			}
			if (dfAbsentdate.getValue() != null) {
				saveAbsent.setAbsentdate(dfAbsentdate.getValue());
			}
			if (tfAbsentStartHours.getValue() != null) {
				saveAbsent.setStarthours(tfAbsentStartHours.getHorsMunites());
			}
			if (tfAbsentEndHours.getValue() != null) {
				saveAbsent.setEndhours(tfAbsentEndHours.getHorsMunites());
			}
			if (tfAbsentTotalHours.getValue() != null) {
				saveAbsent.setTotalhours((new BigDecimal(tfAbsentTotalHours.getValue())));
			}
			if (cbAbsentlwpmark.getValue() == null || cbAbsentlwpmark.getValue().equals(false)) {
				saveAbsent.setLwpmark("N");
			} else {
				saveAbsent.setLwpmark("Y");
			}
			if (taAbsentRemarks.getValue() != null) {
				saveAbsent.setAbsentremarks(taAbsentRemarks.getValue());
			}
			if (cbAbsentStatus.getValue() != null) {
				saveAbsent.setAbsentstatus(cbAbsentStatus.getValue().toString());
			}
			saveAbsent.setEmployeeid(employeeid);
			saveAbsent.setLastupdatedby(username);
			saveAbsent.setLastupdatedtd(DateUtils.getcurrentdate());
			serviceEmpAbsent.saveORUpdate(saveAbsent);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	public void saveAbsentDetails(Long employeeid) {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "EmployeeAbsent Save details......");
			@SuppressWarnings("unchecked")
			Collection<EmployeeAbsentDM> itemIds = (Collection<EmployeeAbsentDM>) tblMstScrSrchRslt.getVisibleItemIds();
			for (EmployeeAbsentDM saveAbsent : (Collection<EmployeeAbsentDM>) itemIds) {
				saveAbsent.setEmployeeid(employeeid);
				serviceEmpAbsent.saveORUpdate(saveAbsent);
			}
			loadSrchRslt();
			tblMstScrSrchRslt.removeAllItems();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	public boolean validateDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Validating EmployeeAbsent Details.....");
		boolean errorFlag = true;
		dfAbsentdate.setComponentError(null);
		tfAbsentStartHours.setComponentError(null);
		tfAbsentEndHours.setComponentError(null);
		tfAbsentTotalHours.setComponentError(null);
		if (dfAbsentdate.getValue() == null) {
			dfAbsentdate.setComponentError(new UserError(GERPErrorCodes.NULL_ABST_ABDATE));
			errorFlag = false;
		}
		if (tfAbsentStartHours.getValue() == null) {
			tfAbsentStartHours.setComponentError(new UserError(GERPErrorCodes.NULL_ABST_STRHOUR));
			errorFlag = false;
		}
		if (tfAbsentEndHours.getValue() == null) {
			tfAbsentEndHours.setComponentError(new UserError(GERPErrorCodes.NULL_ABST_EDHOUR));
			errorFlag = false;
		}
		if ((tfAbsentTotalHours.getValue() == null) || tfAbsentTotalHours.getValue().trim().length() == 0) {
			tfAbsentTotalHours.setComponentError(new UserError(GERPErrorCodes.NULL_ABST_TOTHOUR));
			errorFlag = false;
		}
		return errorFlag;
	}
	
	public void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Reseting Fields.....");
		dfAbsentdate.setValue(null);
		tfAbsentStartHours.setValue(null);
		tfAbsentEndHours.setValue(null);
		tfAbsentTotalHours.setReadOnly(false);
		tfAbsentTotalHours.setValue(null);
		tfAbsentTotalHours.setReadOnly(true);
		cbAbsentlwpmark.setValue(null);
		taAbsentRemarks.setValue("");
		btnadd.setCaption("Add");
		btnadd.setStyleName("add");
		dfAbsentdate.setComponentError(null);
		tfAbsentStartHours.setComponentError(null);
		tfAbsentEndHours.setComponentError(null);
		tfAbsentTotalHours.setComponentError(null);
		cbAbsentStatus.setValue(cbAbsentStatus.getItemIds().iterator().next());
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
	}
}