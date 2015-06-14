/**
 * File Name	:	LoggerUI.java
 * Description	:	To Handle Logger Web page requests.
 * Author		:	Priyanga M
 * Date			:	03,March,2014 
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 *  Version      Date             Modified By        Remarks
 * 	0.1          03,March,2014    Priyanga M	     Initial version
 *  0.2			 23,June, 2014    Karthikeyan R		 Code re-factoring 
 */
package com.gnts.base.rpt;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.txn.LoggerDM;
import com.gnts.base.service.rpt.LoggerService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class LoggerUI extends BaseUI {
	private static final long serialVersionUID = 1L;
	private TextField tfLogRef, tfErrcode, tfClientip;
	private RichTextArea rtaLogDescription;
	private ComboBox cbLogType;
	// Declaration for Table and View button
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private FormLayout flLogRef, flLogDesc, flErrorCode;
	private LoggerService loggerBean = (LoggerService) SpringContextHelper.getBean("logger");
	private BeanItemContainer<LoggerDM> beans = null;
	private static Logger logger = Logger.getLogger(LoggerUI.class);
	private int total;
	private String username;
	private Long companyid;
	private int recordCnt;
	
	// Constructor
	public LoggerUI() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildView();
		// Loading the UI
	}
	
	// Build the UI components
	private void buildView() {
		btnAdd.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnSave.setVisible(false);
		btnCancel.setVisible(false);
		btnEdit.setCaption("View");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting department UI");
		// Logger Name text field
		tfLogRef = new GERPTextField("Log Ref.");
		tfErrcode = new GERPTextField("Error Code");
		tfClientip = new GERPTextField("Client IP");
		rtaLogDescription = new RichTextArea("Logger Desc.");
		rtaLogDescription.setWidth("350");
		// Initialization for cbSearchLogType
		cbLogType = new GERPComboBox("Log Type", BASEConstants.T_BASE_LOGGER, BASEConstants.LOG_TYPE);
		cbLogType.setWidth("150px");
		// create form layouts to hold the input items
		flLogRef = new FormLayout();
		flLogDesc = new FormLayout();
		flErrorCode = new FormLayout();
		// add the user input items into appropriate form layout
		flLogRef.addComponent(tfLogRef);
		flLogRef.addComponent(cbLogType);
		flErrorCode.addComponent(tfErrcode);
		flErrorCode.addComponent(tfClientip);
		flLogDesc.addComponent(rtaLogDescription);
		// add the form layouts into user input layout
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.addComponent(flLogRef);
		hlUserInputLayout.addComponent(flErrorCode);
		hlUserInputLayout.addComponent(flLogDesc);
		hlUserInputLayout.setMargin(true);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	// Method for Reset ViewPanel values and component error
	protected void resetFields() {
		tfLogRef.setReadOnly(false);
		tfLogRef.setValue("");
		rtaLogDescription.setReadOnly(false);
		rtaLogDescription.setValue("");
		tfErrcode.setReadOnly(false);
		tfErrcode.setValue("");
		tfClientip.setReadOnly(false);
		tfClientip.setValue("");
		cbLogType.setReadOnly(false);
		cbLogType.setValue(null);
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		btnSave.setVisible(false);
		editLogger();
	}
	
	private void editLogger() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			LoggerDM enqdtl = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
			if (enqdtl.getLogref() != null) {
				tfLogRef.setReadOnly(false);
				tfLogRef.setValue(enqdtl.getLogref());
				tfLogRef.setReadOnly(true);
			}
			if (enqdtl.getErrcode() != null) {
				tfErrcode.setReadOnly(false);
				tfErrcode.setValue(enqdtl.getErrcode());
				tfErrcode.setReadOnly(true);
			}
			if (enqdtl.getClientip() != null) {
				tfClientip.setReadOnly(false);
				tfClientip.setValue(enqdtl.getClientip());
				tfClientip.setReadOnly(true);
			}
			if (enqdtl.getLogtype() != null) {
				cbLogType.setReadOnly(false);
				cbLogType.setValue(enqdtl.getLogtype());
				cbLogType.setReadOnly(true);
			}
			if (enqdtl.getLogdescription() != null) {
				rtaLogDescription.setReadOnly(false);
				rtaLogDescription.setValue(enqdtl.getLogdescription());
				rtaLogDescription.setReadOnly(true);
			}
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		// No functionality to implement
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		// No functionality to implement
	}
	
	@Override
	protected void showAuditDetails() {
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlSearchLayout.addComponent(hlUserInputLayout);
		hlSearchLayout.setComponentAlignment(hlUserInputLayout, Alignment.MIDDLE_LEFT);
	}
	
	private void loadSrchRslt() {
		tblMstScrSrchRslt.removeAllItems();
		List<LoggerDM> usertable = new ArrayList<LoggerDM>();
		usertable = loggerBean.getLoggerList(null, tfLogRef.getValue(), rtaLogDescription.getValue(),
				tfErrcode.getValue(), null, "F");
		total = usertable.size();
		tblMstScrSrchRslt.setPageLength(5);
		beans = new BeanItemContainer<LoggerDM>(LoggerDM.class);
		beans.addAll(usertable);
		tblMstScrSrchRslt.setContainerDataSource(beans);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "logid", "logref", "logtype", "logdescription", "clientip",
				"errcode", "lastupdateddate", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Log Ref.", "Type", "Description", "Client IP",
				"Error Code", "Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.Of Records:" + total);
	}
}
