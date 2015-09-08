/**
 * File Name 		: EmployeeOnduty.java 
 * Description 		: this class is used for add/update EmployeeOnduty details. 
 * Author 			: Karthikeyan R
 * Date 			: Sep 10, 2014
 * Modification 	:
 * Modified By 		: Karthikeyan R 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Sep 10, 2014         Karthikeyan R	          Initial Version
 */
package com.gnts.hcm.txn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.EmployeeOndutyDM;
import com.gnts.hcm.domain.txn.EmployeeOndutyDatesDM;
import com.gnts.hcm.service.txn.EmployeeOndutyService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
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

public class EmployeeOnduty extends VerticalLayout implements ClickListener {
	private static final long serialVersionUID = 1L;
	// Declaration for add and edit panel components
	private ComboBox cbOndutyApprmgr;
	private PopupDateField dfOndutyDatefrom, dfOndutyDateto;
	private TextField tfOndutyNoOfDays, tfOndutyTothrs;
	private TextArea taOndutyrks;
	private ComboBox cbOndutyStatus;
	// for Search
	private Button btnSearch, btnReset;
	// Declaration for add and edit panel
	private VerticalLayout vlTablePanel = new VerticalLayout();
	private HorizontalLayout hlsavecancel = new HorizontalLayout();
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Table Declaration
	private Table tblMstScrSrchRslt;
	// private Button btnAdd, btnSave, btnCancel,;
	private Button btnadd;
	private Button btnSave;
	private Button btnCancel;
	private List<EmployeeOndutyDM> usertable = new ArrayList<EmployeeOndutyDM>();
	// Declaration for Label
	private BeanItemContainer<EmployeeOndutyDM> beans = null;
	private VerticalLayout vltable, vlTableForm, vlTableLayout;
	private HorizontalLayout hlTableTitleandCaptionLayout;
	private String username;
	private Long companyid;
	private EmployeeOndutyService serviceOnduty = (EmployeeOndutyService) SpringContextHelper.getBean("EmployeeOnduty");
	private EmployeeService servicEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private Logger logger = Logger.getLogger(EmployeeOnduty.class);
	private int total = 0;
	private Long ondutyid;
	private Long employeeid;
	private Date dtfrm;
	private Double check;
	
	// Build View
	public EmployeeOnduty(Long empid) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeOnduty() constructor");
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeid = empid;
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting EmployeeOnduty UI");
		// Initialization for dfOndutyDatefrom
		dfOndutyDatefrom = new GERPPopupDateField("Date From");
		dfOndutyDatefrom.setDateFormat("dd-MMM-yyyy");
		dfOndutyDatefrom.setRequired(true);
		dfOndutyDatefrom.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				dtfrm = dfOndutyDatefrom.getValue();
				try {
					if (dfOndutyDatefrom.getValue().equals(new Date())) {
						dfOndutyDatefrom.setComponentError(new UserError(GERPErrorCodes.DATE_FROM));
					} else {
						dfOndutyDatefrom.setComponentError(null);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// Initialization for dfOndutyDateto
		dfOndutyDateto = new GERPPopupDateField("Date To");
		dfOndutyDateto.setDateFormat("dd-MMM-yyyy");
		dfOndutyDateto.setRequired(true);
		dfOndutyDateto.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				try {
					if (dfOndutyDateto.getValue().before(dtfrm)) {
						dfOndutyDateto.setComponentError(new UserError(GERPErrorCodes.DATE_TO));
					} else {
						dfOndutyDateto.setComponentError(null);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		dfOndutyDateto.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				diffdays();
			}
		});
		// Initialization for tfOndutyNoOfDays
		tfOndutyNoOfDays = new GERPTextField("No of Days");
		tfOndutyNoOfDays.setRequired(true);
		tfOndutyNoOfDays.setWidth("100");
		// Initialization for tfOndutyTothrs
		tfOndutyTothrs = new GERPTextField("Total Hours");
		tfOndutyTothrs.setRequired(true);
		tfOndutyTothrs.setWidth("100");
		// Initialization for cbOndutyApprmgr
		cbOndutyApprmgr = new GERPComboBox("Approve Manager");
		cbOndutyApprmgr.setItemCaptionPropertyId("fullname");
		cbOndutyApprmgr.setRequired(true);
		loadAppMgrList();
		// Initialization for taOndutyOndutyrks
		taOndutyrks = new GERPTextArea("Remarks");
		taOndutyrks.setWidth("170");
		taOndutyrks.setHeight("55");
		// Initialization for cbOndutyStatus
		cbOndutyStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbOndutyStatus.setItemCaptionPropertyId("desc");
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
			// Click Listener for Add and Update for Employee Onduty
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDetails()) {
					saveOnduty();
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
		// ClickListener for Employee Onduty Tale
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
					editOnduty();
				}
			}
		});
		vlTableForm = new VerticalLayout();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(dfOndutyDatefrom);
		flColumn1.addComponent(dfOndutyDateto);
		flColumn2.addComponent(tfOndutyNoOfDays);
		flColumn2.addComponent(tfOndutyTothrs);
		flColumn3.addComponent(taOndutyrks);
		flColumn4.addComponent(cbOndutyApprmgr);
		flColumn4.addComponent(cbOndutyStatus);
		HorizontalLayout hlInput = new HorizontalLayout();
		hlInput.addComponent(flColumn1);
		hlInput.addComponent(flColumn2);
		hlInput.addComponent(flColumn3);
		hlInput.addComponent(flColumn4);
		hlInput.addComponent(btnadd);
		hlInput.setComponentAlignment(btnadd, Alignment.BOTTOM_LEFT);
		hlInput.setSpacing(true);
		hlInput.setMargin(true);
		hlInput.setWidth("100%");
		vlTableForm.addComponent(hlInput);
		vlTableForm.addComponent(tblMstScrSrchRslt);
		vlTableLayout = new VerticalLayout();
		vlTableLayout.addComponent(vlTableForm);
		addComponent(vlTableLayout);
		loadSrchRslt();
		btnadd.setStyleName("add");
		resetFields();
	}
	
	private void loadAppMgrList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "loading Approve Manager List...");
			BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(servicEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null, null,
					null, "P"));
			cbOndutyApprmgr.setContainerDataSource(beanEmployee);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "loading SearchResult Details...");
			tblMstScrSrchRslt.removeAllItems();
			total = 0;
			if (employeeid != null) {
				usertable = serviceOnduty.getempondutylist(ondutyid, employeeid, "Active", "F");
			}
			total = usertable.size();
			tblMstScrSrchRslt.setPageLength(10);
			beans = new BeanItemContainer<EmployeeOndutyDM>(EmployeeOndutyDM.class);
			beans.addAll(usertable);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Employee Onduty. result set");
			tblMstScrSrchRslt.setContainerDataSource(beans);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "datefrm", "dateto", "noofdays", "ondutyrks",
					"odstatus", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "From Date", "To Date", "No of Days", "Remarks",
					"Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("ondutyid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + total);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method used to display selected row's values in desired text box and combo box for edit the values
	private void editOnduty() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing Onduty.......");
			if (tblMstScrSrchRslt.getValue() != null) {
				EmployeeOndutyDM empOnduty = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
				dfOndutyDatefrom.setValue(empOnduty.getDatefrm1());
				dfOndutyDateto.setValue(empOnduty.getDatetoo());
				tfOndutyNoOfDays.setReadOnly(false);
				tfOndutyNoOfDays.setValue((empOnduty.getNoofdays()).toString());
				tfOndutyNoOfDays.setReadOnly(true);
				tfOndutyTothrs.setValue((empOnduty.getTothrs()).toString());
				cbOndutyApprmgr.setValue(empOnduty.getApprmgr());
				taOndutyrks.setValue(empOnduty.getOndutyrks());
				cbOndutyStatus.setValue(empOnduty.getOdstatus());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Save Method for save and update the Asset Specification details
	private void saveOnduty() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Onduty details......");
		try {
			EmployeeOndutyDM saveonduty = new EmployeeOndutyDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				saveonduty = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
				usertable.remove(saveonduty);
			}
			if (dfOndutyDatefrom.getValue() != null) {
				saveonduty.setDatefrm(dfOndutyDatefrom.getValue());
			}
			if (dfOndutyDateto.getValue() != null) {
				saveonduty.setDateto(dfOndutyDateto.getValue());
			}
			if (tfOndutyNoOfDays.getValue() != null) {
				saveonduty.setNoofdays((new BigDecimal(tfOndutyNoOfDays.getValue())));
			}
			if (tfOndutyTothrs.getValue() != null) {
				saveonduty.setTothrs((new BigDecimal(tfOndutyTothrs.getValue())));
			}
			if (cbOndutyApprmgr.getValue() != null) {
				saveonduty.setApprmgr((Long.valueOf(cbOndutyApprmgr.getValue().toString())));
			}
			if (taOndutyrks.getValue() != null) {
				saveonduty.setOndutyrks(taOndutyrks.getValue());
			}
			if (cbOndutyStatus.getValue() != null) {
				saveonduty.setOdstatus((String) cbOndutyStatus.getValue());
			}
			saveonduty.setEmployeeid(employeeid);
			saveonduty.setLastupdatedby(username);
			saveonduty.setLastupdateddt(DateUtils.getcurrentdate());
			serviceOnduty.saveAndUpdate(saveonduty);
			
			
			loadSrchRslt();
			btnadd.setCaption("Add");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		resetFields();
	}
	
	public void ondutysave(Long employeeid) {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "EmployeeOnduty Save details......");
			@SuppressWarnings("unchecked")
			Collection<EmployeeOndutyDM> itemIds = (Collection<EmployeeOndutyDM>) tblMstScrSrchRslt.getVisibleItemIds();
			for (EmployeeOndutyDM saveduty : (Collection<EmployeeOndutyDM>) itemIds) {
				saveduty.setEmployeeid(employeeid);
				serviceOnduty.saveAndUpdate(saveduty);
				}
			@SuppressWarnings("unchecked")
			Collection<EmployeeOndutyDatesDM> itemIds1 = (Collection<EmployeeOndutyDatesDM>) tblMstScrSrchRslt.getVisibleItemIds();
			for (EmployeeOndutyDatesDM savedutydate : (Collection<EmployeeOndutyDatesDM>) itemIds1) {
				savedutydate.setOndutyid(employeeid);				
			}
			loadSrchRslt();
			tblMstScrSrchRslt.removeAllItems();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private boolean validateDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Validating EmployeeOnduty Details.....");
		boolean errorFlag = true;
		dfOndutyDatefrom.setComponentError(null);
		dfOndutyDateto.setComponentError(null);
		tfOndutyNoOfDays.setComponentError(null);
		tfOndutyTothrs.setComponentError(null);
		cbOndutyApprmgr.setComponentError(null);
		if (dfOndutyDatefrom.getValue() == null) {
			dfOndutyDatefrom.setComponentError(new UserError(GERPErrorCodes.NULL_ONDUTY_DATEFRM));
			errorFlag = false;
		}
		if (dfOndutyDateto.getValue() == null) {
			dfOndutyDateto.setComponentError(new UserError(GERPErrorCodes.NULL_ONDUTY_DATETO));
			errorFlag = false;
		}
		if ((tfOndutyNoOfDays.getValue() == "") || tfOndutyNoOfDays.getValue().trim().length() == 0) {
			tfOndutyNoOfDays.setComponentError(new UserError(GERPErrorCodes.NULL_ONDUTY_NOOFDAYS));
			errorFlag = false;
		}
		if ((tfOndutyTothrs.getValue() == "") || tfOndutyTothrs.getValue().trim().length() == 0) {
			tfOndutyTothrs.setComponentError(new UserError(GERPErrorCodes.NULL_ONDUTY_TOTALHOURS));
			errorFlag = false;
		}
		if (cbOndutyApprmgr.getValue() == null) {
			cbOndutyApprmgr.setComponentError(new UserError(GERPErrorCodes.NULL_ONDUTY_MANAGER));
			errorFlag = false;
		}
		return errorFlag;
	}
	
	public void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Reseting Fields.....");
		dfOndutyDatefrom.setValue(null);
		dfOndutyDateto.setValue(null);
		tfOndutyNoOfDays.setReadOnly(false);
		tfOndutyNoOfDays.setValue("0");
		tfOndutyNoOfDays.setReadOnly(true);
		tfOndutyTothrs.setValue("0");
		cbOndutyApprmgr.setValue(null);
		taOndutyrks.setValue("");
		dfOndutyDatefrom.setComponentError(null);
		dfOndutyDateto.setComponentError(null);
		tfOndutyNoOfDays.setComponentError(null);
		tfOndutyTothrs.setComponentError(null);
		cbOndutyApprmgr.setComponentError(null);
		cbOndutyStatus.setValue(cbOndutyStatus.getItemIds().iterator().next());
	}
	
	private void diffdays() {
		try {
			Date startDate2 = (Date) dfOndutyDatefrom.getValue();
			Date endDate2 = (Date) dfOndutyDateto.getValue();
			long diff = endDate2.getTime() - startDate2.getTime();
			int diffDays = (int) (diff / (24 * 1000 * 60 * 60));
			check = 0d;
			check = (double) (diffDays + 1);
			tfOndutyNoOfDays.setReadOnly(false);
			tfOndutyNoOfDays.setValue(check.toString());
			tfOndutyNoOfDays.setReadOnly(true);
			dfOndutyDatefrom.setComponentError(null);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
	}
}