/**
 * File Name	:	MessagesDM.java
 * Description	:	To Handle Messages Web page requests.
 * Author		:	Priyanga M
 * Date			:	March 43, 2014
 * Modified By  :   JOEL GLINDAN D
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1           26-Jun-2014      JOEL GLINDAN D         Code Re-factoring 
 **/
package com.gnts.base.rpt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.txn.MessagesDM;
import com.gnts.base.service.rpt.MessagesService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Messages extends BaseUI {
	private MessagesService servicemessage = (MessagesService) SpringContextHelper.getBean("messages");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfmsgsender, tfmsgsubject, tfmsgreceiver, tfmsgresponse, tfclientip, tfmessgeCc, tfstatus,
			tfmsgtype;
	private TextArea tamsgbody;
	private PopupDateField dfstartdate, dfenddate, dfmessagedate;
	private ComboBox cbmessagestatus, cbmsgtype;
	// Bean container
	private BeanItemContainer<MessagesDM> beanmessageDM = null;
	// local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(Messages.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Messages() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside MEssages() constructor");
		// Loading the UI
		buildview();
	}// Build the UI components
	
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Messages UI");
		// Message sender text field
		tfmsgsender = new GERPTextField("Message Sender");
		tfmsgsender.setMaxLength(25);
		// Message subject text field
		tfmsgsubject = new GERPTextField("Message Subject");
		tfmsgsubject.setMaxLength(25);
		// Message receiver text field
		tfmsgreceiver = new GERPTextField("Message Receiver");
		tfmsgreceiver.setMaxLength(25);
		// Message response text field
		tfmsgresponse = new GERPTextField("Message Response");
		// Message Client I.P creation text field
		tfclientip = new GERPTextField("Client I.P");
		// Message Cc text field
		tfmessgeCc = new GERPTextField("Message Cc");
		// Message type text field
		tfmsgtype = new GERPTextField("  Message Type ");
		// Message status text field
		tfstatus = new GERPTextField("Status");
		// Login Creation date
		dfmessagedate = new PopupDateField("Message Date");
		dfmessagedate.setDateFormat("dd-MMM-yyyy");
		dfmessagedate.setReadOnly(false);
		dfmessagedate.setValue(new Date());
		dfmessagedate.setImmediate(true);
		// Start date field
		dfstartdate = new PopupDateField("Start Date");
		dfstartdate.setImmediate(true);
		dfstartdate.setDateFormat("dd-MMM-yyyy");
		// End date field
		dfenddate = new PopupDateField("End Date");
		dfenddate.setDateFormat("dd-MMM-yyyy");
		dfenddate.setValue(new Date());
		dfenddate.setImmediate(true);
		dfstartdate.setValue(DateUtils.addDays(DateUtils.getcurrentdate(), -10));
		dfenddate.setValue(DateUtils.getcurrentdate());
		// Message status combo box
		cbmessagestatus = new GERPComboBox("Status", BASEConstants.T_BASE_MESSAGES, BASEConstants.MSG_STATUS);
		// Message type combo box
		cbmsgtype = new GERPComboBox("Message Type", BASEConstants.T_BASE_MESSAGES, BASEConstants.MSG_TYPE);
		//cbmsgtype.setImmediate(true);
		//beanmeassagetypeDM = new BeanItemContainer<MessageTypeDM>(MessageTypeDM.class);
		//cbmsgtype.setContainerDataSource(beanmeassagetypeDM);
		// Message body text area
		tamsgbody = new TextArea("Message Body");
		tamsgbody.setWidth("200");
		tamsgbody.setHeight("72");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		btnAdd.setVisible(false);
		btnAuditRecords.setVisible(false);
		btnEdit.setCaption("View");
		btnEdit.setStyleName("view");
		// Add components for Search Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn1.addComponent(tfmsgsender);
		flColumn1.addComponent(tfmsgsubject);
		flColumn2.addComponent(dfstartdate);
		flColumn2.addComponent(dfenddate);
		flColumn3.addComponent(cbmsgtype);
		flColumn3.addComponent(cbmessagestatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSizeUndefined();
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		btnSave.setVisible(false);
		hlUserInputLayout.removeAllComponents();
		hlUserInputLayout.setMargin(true);
		// Add components for User Input Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		flColumn1.addComponent(tfmsgsender);
		flColumn1.addComponent(tfmsgreceiver);
		flColumn1.addComponent(tfmsgtype);
		flColumn2.addComponent(tfmsgsubject);
		flColumn2.addComponent(tfmsgresponse);
		flColumn2.addComponent(dfmessagedate);
		flColumn2.setSpacing(true);
		flColumn3.addComponent(tfclientip);
		flColumn3.addComponent(tfmessgeCc);
		flColumn3.addComponent(tfstatus);
		flColumn4.addComponent(tamsgbody);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void viewLogger() {
		Item rowSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (rowSelected != null) {
			MessagesDM enqdtl = beanmessageDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			setReadOnlyFalseFields();
			tfmsgsender.setValue(enqdtl.getMsgsender());
			tfmsgreceiver.setValue(enqdtl.getMsgreceiver());
			tfmessgeCc.setValue(enqdtl.getMsgcc());
			tfmsgsubject.setValue(enqdtl.getMsgsubject());
			tamsgbody.setValue(enqdtl.getMsgbody());
			tfmsgresponse.setValue(enqdtl.getMsgresponse());
			tfmsgresponse.setReadOnly(true);
			tfclientip.setValue(enqdtl.getClientip());
			dfmessagedate.setValue(enqdtl.getMsgsentdateinDt());
			tfstatus.setValue(enqdtl.getMsgstatus());
			tfmsgtype.setValue(enqdtl.getMsgtype());
			setReadOnlyTrueFields();
		}
	}
	
	// get the search result from DB based on the search messages
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<MessagesDM> messagesList = new ArrayList<MessagesDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Messages are "
				+ companyid + ", " + tfmsgsender.getValue() + ", " + cbmessagestatus.getValue() + ","
				+ dfstartdate.getValue() + "," + dfenddate.getValue() + "," + tfmsgsubject.getValue() + ","
				+ tfmsgsubject.getValue());
		messagesList = servicemessage.getMessagesList(tfmsgsender.getValue(), null, null, null, dfstartdate.getValue(),dfenddate.getValue(), tfmsgsubject.getValue(), (String) cbmessagestatus.getValue(), null, companyid, null);
		
		
		
		
		recordCnt = messagesList.size();
		beanmessageDM = new BeanItemContainer<MessagesDM>(MessagesDM.class);
		beanmessageDM.addAll(messagesList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Messages. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanmessageDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "messageid", "msgtype", "msgsender", "msgreceiver",
				"msgsubject", "msgsentdate", "msgstatus", "msgbody" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Type", "Sender",
				"Receiver", "Subject", "Sent Date", "Status", "Message Body" });
		tblMstScrSrchRslt.setColumnAlignment("messageid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnCollapsingAllowed(true);
		tblMstScrSrchRslt.setColumnCollapsed("msgbody", true);
		tblMstScrSrchRslt.setColumnFooter("msgstatus", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		setReadOnlyFalseFields();
		dfmessagedate.setValue(null);
		dfstartdate.setValue(DateUtils.addDays(DateUtils.getcurrentdate(), -30));
		dfenddate.setValue(DateUtils.getcurrentdate());
		tfmsgsender.setValue("");
		tfmsgreceiver.setValue("");
		cbmsgtype.setValue("");
		tfmsgsubject.setValue("");
		tfmsgresponse.setValue("");
		tfclientip.setValue("");
		tfmessgeCc.setValue("");
		tamsgbody.setValue("");
		cbmessagestatus.setValue(cbmessagestatus.getItemIds().iterator().next());
		tfmsgtype.setValue("");
		tfstatus.setValue("");
	}
	
	public void setReadOnlyFalseFields() {
		dfmessagedate.setReadOnly(false);
		dfenddate.setReadOnly(false);
		dfstartdate.setReadOnly(false);
		tfmsgsender.setReadOnly(false);
		tfmsgreceiver.setReadOnly(false);
		cbmsgtype.setReadOnly(false);
		tfmsgsubject.setReadOnly(false);
		tfmsgresponse.setReadOnly(false);
		tfclientip.setReadOnly(false);
		tfmessgeCc.setReadOnly(false);
		tamsgbody.setReadOnly(false);
		cbmessagestatus.setReadOnly(false);
		tfmsgtype.setReadOnly(false);
		tfstatus.setReadOnly(false);
	}
	
	public void setReadOnlyTrueFields() {
		dfmessagedate.setReadOnly(true);
		tfmsgsender.setReadOnly(true);
		tfmsgreceiver.setReadOnly(true);
		tfmsgsender.setReadOnly(true);
		tfmsgsubject.setReadOnly(true);
		tfmsgresponse.setReadOnly(true);
		tfclientip.setReadOnly(true);
		tfmessgeCc.setReadOnly(true);
		tamsgbody.setReadOnly(true);
		tfmsgtype.setReadOnly(true);
		tfstatus.setReadOnly(true);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		hlUserInputLayout.removeAllComponents();
		setReadOnlyFalseFields();
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		hlUserInputLayout.removeAllComponents();
		// reset the field valued to default
		cbmessagestatus.setValue(cbmessagestatus.getItemIds().iterator().next());
		tfmsgsender.setValue("");
		tfmsgsubject.setValue("");
		cbmsgtype.setValue(null);
		dfstartdate.setValue(DateUtils.addDays(DateUtils.getcurrentdate(), -30));
		dfenddate.setValue(DateUtils.getcurrentdate());
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Viewing messages...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the
		// same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		assembleUserInputLayout();
		viewLogger();
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		setReadOnlyFalseFields();
		loadSrchRslt();
	}
}
