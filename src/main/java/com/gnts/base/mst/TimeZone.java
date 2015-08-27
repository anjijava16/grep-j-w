/**
 * File Name	:	TimeZone.java
 * Description	:	To Handle TimeZone Web page requests.
 * Author		:	Priyanga M
 * Date			:	Feb 28, 2014
 * Modification 
 * Modified By  :   Mahaboob SUbahan J
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version      Date           	Modified By         Remarks
 * 0.1			Feb 28 2014		Priyanga M			Initial Version
 * 0.2 			Jun 19 2014		Mahaboob SUbahan J	Code re-factoring 
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.TimeZoneDM;
import com.gnts.base.service.mst.TimeZoneService;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class TimeZone extends BaseUI {
	private static final long serialVersionUID = 1L;
	/*
	 * Search panel Components Declaration for Search Panel
	 */
	private TextField tfTimeZoneCode, tfTimeZoneDesc;
	private FormLayout flTimeZoneCode, flTimeZoneDesc;
	private TimeZoneService timezoneBean = (TimeZoneService) SpringContextHelper.getBean("timezone");
	private BeanItemContainer<TimeZoneDM> beans = null;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(TimeZone.class);
	
	// Time Zone Constructor
	public TimeZone() {
		UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Time Zone() constructor");
		// Loading the TimeZone UI
		buildView();
	}
	
	// Build View
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Time Zone UI");
		// Country text fields
		tfTimeZoneCode = new GERPTextField("Time Zone Code");
		tfTimeZoneDesc = new GERPTextField("Time Zone Description");
		// get the list of statuses for Time Zone field
		flTimeZoneCode = new FormLayout();
		flTimeZoneDesc = new FormLayout();
		// Base UI add, edit, Audit Records buttons to be invisible
		btnAdd.setVisible(false);
		btnEdit.setVisible(false);
		btnAuditRecords.setVisible(false);
		// add the form layouts into user input layout
		flTimeZoneCode.addComponent(tfTimeZoneCode);
		flTimeZoneDesc.addComponent(tfTimeZoneDesc);
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(flTimeZoneCode);
		hlUserInputLayout.addComponent(flTimeZoneDesc);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
		resetSearchDetails();
		loadSrchRslt();
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<TimeZoneDM> list = new ArrayList<TimeZoneDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + "," + tfTimeZoneCode.getValue() + "," + tfTimeZoneDesc.getValue());
		list = timezoneBean.getTimeZoneList(tfTimeZoneCode.getValue(), tfTimeZoneDesc.getValue(),"F");
		recordCnt = list.size();
		beans = new BeanItemContainer<TimeZoneDM>(TimeZoneDM.class);
		beans.addAll(list);
		tblMstScrSrchRslt.setContainerDataSource(beans);
		tblMstScrSrchRslt
				.setVisibleColumns(new Object[] { "timezoneid", "timezonecode", "timezonedesc", "clockadjust" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Time Zone Code", "Time Zone Desc.", "OffSet" });
		tblMstScrSrchRslt.setColumnAlignment("timezoneid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("clockadjust", "No.of Records : " + recordCnt);
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
	
	// Reset Search Component
	@Override
	protected void resetSearchDetails() {
		tfTimeZoneCode.setValue("");
		tfTimeZoneDesc.setValue("");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
	}
	
	@Override
	protected void editDetails() {
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
	}
	
	@Override
	protected void saveDetails() throws SaveException {
	}
	
	@Override
	protected void showAuditDetails() {
	}
	
	@Override
	protected void cancelDetails() {
	}
	
	@Override
	protected void resetFields() {
	}
}
