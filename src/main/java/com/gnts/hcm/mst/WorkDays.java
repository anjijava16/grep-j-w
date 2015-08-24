/**
 * File Name	:	WorkDays.java
 * Description	:	this class have UI design for Work Days 
 * Author		:	P Sekhar
 * Date			:	Feb 27, 2014
 * Modification 
 * Modified By  :   
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of  GNTS Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          Feb 27, 2014       	P Sekhar		        Initial Version
 * 0.2			5-Jul-2014			Abdullah.H				Code Optimization
 */
package com.gnts.hcm.mst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.StaticCodesService;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPSaveNotification;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.hcm.domain.mst.WorkDaysDM;
import com.gnts.hcm.service.mst.WorkDaysService;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.UI;

public class WorkDays extends BaseUI {
	private WorkDaysService serviceWorkDays = (WorkDaysService) SpringContextHelper.getBean("WorkDays");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private StaticCodesService serviceStatic = (StaticCodesService) SpringContextHelper.getBean("staticCodes");
	// form layout for input controls
	private FormLayout flBranchName;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private ComboBox cbBranch = new GERPComboBox("Branch Name");
	private BeanItemContainer<StatusDM> beanStaticCode = null;
	private Button btnSubmit = new GERPButton("Save", "savebt");
	// local variables declaration
	private int recordCnt = 0;
	private String strLoginUserName;
	private Long companyId, branchId;
	// Initialize logger
	private Logger logger = Logger.getLogger(WorkDays.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public WorkDays() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Inside WorkDays() constructor");
		strLoginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + cbBranch.getValue() + "Painting WorkDays UI");
		// Not Visible Buttons
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnSearch.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnReset.setVisible(false);
		btnSave.setVisible(false);
		btnDownload.setVisible(true);
		hlPageHdrContainter.addComponent(btnSubmit);
		hlPageHdrContainter.setComponentAlignment(btnSubmit, Alignment.MIDDLE_RIGHT);
		// To load Table field
		loadsearchworkDaysList();
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("200px");
		cbBranch.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbBranch.getItem(itemId);
				if (item != null) {
					// Get the actual bean and use the data
					loadSrchRslt();
					tblMstScrSrchRslt.setVisible(true);
				}
			}
		});
		// create form layouts to hold the input items
		flBranchName = new FormLayout();
		// create form layouts to hold the input items
		flBranchName.addComponent(cbBranch);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flBranchName);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		btnSubmit.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				saveWorkdays();
			}
		});
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + cbBranch.getValue() + ", Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
	}
	
	// load the lookup details for search
	private void loadsearchworkDaysList() {
		try {
			BeanContainer<String, BranchDM> auditConfigbean = new BeanContainer<String, BranchDM>(BranchDM.class);
			auditConfigbean.setBeanIdProperty("branchId");
			auditConfigbean.addAll(serviceBranch.getBranchList(null, null, null, null, companyId, "P"));
			cbBranch.setContainerDataSource(auditConfigbean);
		}
		catch (Exception e) {
		}
	}
	
	private void loadSrchRslt() {
		btnCancel.setVisible(false);
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + cbBranch.getValue() + "Loading Search...");
		try {
			tblMstScrSrchRslt.removeAllItems();
			tblMstScrSrchRslt.setPageLength(10);
			tblMstScrSrchRslt.setFooterVisible(false);
			List<WorkDaysDM> listWorkDays = new ArrayList<WorkDaysDM>();
			List<StatusDM> auditConfigList = new ArrayList<StatusDM>();
			List<StatusDM> auditConfigList2 = new ArrayList<StatusDM>();
			listWorkDays = serviceWorkDays.getWorkList(null, null, null, (Long) cbBranch.getValue(), companyId, "F");
			@SuppressWarnings("unused")
			Long branchId = null;
			if (cbBranch.getValue() != null) {
				branchId = ((Long.valueOf(cbBranch.getValue().toString())));
			}
			tblMstScrSrchRslt.setEditable(true);
			auditConfigList = serviceStatic.getStaticCodesList("M_HCM_WORK_DAYS", "WORK_DAYS", null, null, null);
			for (StatusDM obj : auditConfigList) {
				StatusDM obj2 = new StatusDM();
				obj2.setDesc(obj.getDesc());
				if (listWorkDays.size() != 0) {
					for (WorkDaysDM test : listWorkDays) {
						obj2.setCode("false");
						if (test.getWorkDay() == Long.valueOf(obj.getCode().toString()) && test.getWorkYN().equals("Y")) {
							obj2.setCode("true");
							break;
						}
					}
					auditConfigList2.add(obj2);
				} else {
					obj2.setCode("false");
					auditConfigList2.add(obj2);
				}
			}
			beanStaticCode = new BeanItemContainer<StatusDM>(StatusDM.class);
			beanStaticCode.addAll(auditConfigList2);
			tblMstScrSrchRslt.setContainerDataSource(beanStaticCode);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "desc", "code" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Week Days", "View?" });
			tblMstScrSrchRslt.setColumnFooter("workYN", "No.of Records:" + recordCnt);
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.setTableFieldFactory(new TableFieldFactory() {
				private static final long serialVersionUID = 1L;
				
				public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
					if (propertyId.toString().equals("code")) {
						CheckBox ckBox = new CheckBox();
						ckBox.setValue(true);
						return ckBox;
					}
					return null;
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error on loadSrchRslt(), The Error is ----->" + e);
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		tblMstScrSrchRslt.setVisible(true);
	}
	
	@Override
	protected void resetSearchDetails() {
		cbBranch.setValue(branchId);
	}
	
	@Override
	protected void addDetails() {
		// Do not have a code here...!
	}
	
	@Override
	protected void editDetails() {
		// Do not have a code here...!
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		// Do not have a code here...!
	}
	
	@Override
	protected void saveDetails() {
	}
	
	private void saveWorkdays() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName + " > "
				+ "Search Parameters are " + ":" + cbBranch.getValue() + "Save Method");
		try {
			serviceWorkDays.deleteWorkdayList((Long) cbBranch.getValue(), companyId);
			@SuppressWarnings("unchecked")
			Collection<StatusDM> itemIds = (Collection<StatusDM>) tblMstScrSrchRslt.getVisibleItemIds();
			int i = 1;
			for (StatusDM pojo1 : (Collection<StatusDM>) itemIds) {
				WorkDaysDM workObj = new WorkDaysDM();
				workObj.setWorkDay(Long.valueOf(i));
				workObj.setBranchId((Long) cbBranch.getValue());
				workObj.setCmpId(companyId);
				System.out.println("pojo1.getCode()-->" + pojo1.getCode());
				if (pojo1.getCode().equals("true")) {
					workObj.setWorkYN("Y");
				} else {
					workObj.setWorkYN("N");
				}
				serviceWorkDays.saveAndUpdate(workObj);
				i++;
				new GERPSaveNotification();
			}
			btnSubmit.setComponentError(null);
		}
		catch (Exception e) {
			try {
				throw new ERPException.SaveException();
			}
			catch (SaveException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	protected void showAuditDetails() {
		// Do not have a code here...!
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName);
		tblMstScrSrchRslt.setVisible(true);
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnSearch.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnReset.setVisible(false);
		btnSave.setVisible(true);
		btnCancel.setVisible(false);
		btnDownload.setVisible(true);
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID :" + companyId + " | Login User Name : " + strLoginUserName);
		cbBranch.setValue(branchId);
	}
}
