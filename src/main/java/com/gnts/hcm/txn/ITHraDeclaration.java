/**
 * File Name 		: ITHraDeclaration.java 
 * Description 		: this class is used for IT Hra Declaration  details. 
 * Author 			: Abdullah.H
 * Date 			: 24-Sep-2014
 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.

 * Version       Date           	 Modified By               Remarks
 * 0.1          24-Sep-2014    		 Abdullah.H		          Intial Version
 * 0.2			18-Oct-2014			 sudhakar				 Code Refactoring
 */
package com.gnts.hcm.txn;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.ParameterService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.ITHraDeclDM;
import com.gnts.hcm.service.txn.ITHraDeclService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ITHraDeclaration extends BaseUI {
	private ITHraDeclService serviceITHraDecl = (ITHraDeclService) SpringContextHelper.getBean("ITHRA");
	private ParameterService serviceParameter = (ParameterService) SpringContextHelper.getBean("parameter");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout hlithradDoc = new VerticalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfFinYear, tfHraAmt, tfApprovedAmt;
	private ComboBox cbEmpName, cbFinMonth, cbStatus;
	private PopupDateField dfVerifiedDt = new GERPPopupDateField("Verified Date");
	// Bean container
	private BeanItemContainer<ITHraDeclDM> beanITHraDeclDM = null;
	private Long companyId, EmployeeId;
	private String loginUserName;
	private int recordCnt = 0;
	private String primaryid;
	private Long hraamt = 0L;
	private Long approvedamount = 0L;
	private Boolean errorFlag = false;
	// Initialize Logger
	private Logger logger = Logger.getLogger(ITHraDeclaration.class);
	private File file;
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public ITHraDeclaration() {
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		EmployeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Inside ITHraDeclaration() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Painting ITHraDeclaration UI");
		// text fields
		tfFinYear = new GERPTextField("Finance Year");
		tfHraAmt = new GERPTextField("HRA Amount");
		tfApprovedAmt = new GERPTextField("Approved Amount");
		// Combo Boxes
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setWidth("150px");
		cbFinMonth = new GERPComboBox("Finance Month");
		cbFinMonth.addItem("Jan");
		cbFinMonth.addItem("Feb");
		cbFinMonth.addItem("Mar");
		cbFinMonth.addItem("Apr");
		cbFinMonth.addItem("May");
		cbFinMonth.addItem("Jun");
		cbFinMonth.addItem("Jul");
		cbFinMonth.addItem("Aug");
		cbFinMonth.addItem("Sep");
		cbFinMonth.addItem("Oct");
		cbFinMonth.addItem("Nov");
		cbFinMonth.addItem("Dec");
		tfFinYear.setReadOnly(false);
		tfFinYear.setValue(serviceParameter.getParameterValue("FM_FINYEAR", companyId, null));
		tfFinYear.setReadOnly(true);
		loadEmployee();
		cbEmpName.setItemCaptionPropertyId("fullname");
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("140px");
		// create form layouts to hold the input items
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		// add the user input items into appropriate form layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn1.addComponent(cbEmpName);
		flColumn2.addComponent(cbStatus);
		// add the form layouts into user input layout
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		hlUserInputLayout.removeAllComponents();
		// Remove all components in Search Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbEmpName);
		flColumn1.addComponent(tfFinYear);
		flColumn1.addComponent(cbFinMonth);
		flColumn2.addComponent(tfHraAmt);
		flColumn2.addComponent(tfApprovedAmt);
		flColumn3.addComponent(dfVerifiedDt);
		flColumn3.addComponent(cbStatus);
		flColumn4.addComponent(hlithradDoc);
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadEmployee() {
		BeanContainer<Long, EmployeeDM> bean = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		bean.setBeanIdProperty("employeeid");
		bean.addAll(servicebeanEmployee.getEmployeeList((String) cbEmpName.getValue(), null, null, null, null, null,
				null, null, null, "P"));
		cbEmpName.setContainerDataSource(bean);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<ITHraDeclDM> itOtherComeList = new ArrayList<ITHraDeclDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Search Parameters are "
				+ companyId + ", " + (Long) cbEmpName.getValue() + ", " + (String) cbStatus.getValue());
		itOtherComeList = serviceITHraDecl.getITHRAList(null, (Long) cbEmpName.getValue(), null,
				(String) cbStatus.getValue(), "F");
		recordCnt = itOtherComeList.size();
		beanITHraDeclDM = new BeanItemContainer<ITHraDeclDM>(ITHraDeclDM.class);
		beanITHraDeclDM.addAll(itOtherComeList);
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Got the IT Other HRA declaration List result set");
		tblMstScrSrchRslt.setContainerDataSource(beanITHraDeclDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "itHRAId", "empName", "hraAmt", "appAmt", "status",
				"lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "HRA Amount", "Approved Amount",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("itHRAId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records:" + recordCnt);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbEmpName.setValue(null);
		cbEmpName.setComponentError(null);
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID :" + companyId + " | User Name : " + loginUserName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		cbEmpName.setRequired(true);
		tfApprovedAmt.setRequired(true);
		tfHraAmt.setRequired(true);
		new UploadDocumentUI(hlithradDoc);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		resetFields();
	}
	
	private void editItHraDecl() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Editing the selected record");
		if (tblMstScrSrchRslt.getValue() != null) {
			ITHraDeclDM editHraDeclObj = beanITHraDeclDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			if (editHraDeclObj.getEmpId() != null) {
				cbEmpName.setValue(editHraDeclObj.getEmpId());
			}
			if (editHraDeclObj.getFinYear() != null) {
				tfFinYear.setReadOnly(false);
				tfFinYear.setValue(editHraDeclObj.getFinYear());
			}
			if (editHraDeclObj.getFinMonth() != null) {
				cbFinMonth.setValue(editHraDeclObj.getFinMonth().toString());
			}
			if (editHraDeclObj.getHraAmt() != null) {
				tfHraAmt.setValue(editHraDeclObj.getHraAmt().toString());
			}
			if (editHraDeclObj.getAppAmt() != null) {
				tfApprovedAmt.setValue(editHraDeclObj.getAppAmt().toString());
			}
			if (editHraDeclObj.getVerifiedDt() != null) {
				dfVerifiedDt.setValue(editHraDeclObj.getVerifiedDt());
			}
			if (editHraDeclObj.getStatus() != null) {
				cbStatus.setValue(editHraDeclObj.getStatus());
			}
			if (editHraDeclObj.getProofDoc() != null) {
				byte[] certificate = editHraDeclObj.getProofDoc();
				UploadDocumentUI test = new UploadDocumentUI(hlithradDoc);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(hlithradDoc);
			}
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Invoking Edit record ");
		cbEmpName.setRequired(true);
		tfHraAmt.setRequired(true);
		tfApprovedAmt.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editItHraDecl();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Validating Data ");
		tfHraAmt.setComponentError(null);
		tfApprovedAmt.setComponentError(null);
		cbEmpName.setComponentError(null);
		errorFlag = false;
		if (cbEmpName.getValue() == null) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			errorFlag = true;
		}
		if ((tfHraAmt.getValue() == null) || tfHraAmt.getValue().trim().length() == 0) {
			tfHraAmt.setComponentError(new UserError(GERPErrorCodes.NULL_HRA_AMOUNT));
			errorFlag = true;
		}
		if ((tfApprovedAmt.getValue() == null) || tfApprovedAmt.getValue().trim().length() == 0) {
			tfApprovedAmt.setComponentError(new UserError(GERPErrorCodes.NULL_APPROVED_AMOUNT));
			errorFlag = true;
		}
		approvedamount = Long.valueOf(tfApprovedAmt.getValue().toString());
		hraamt = Long.valueOf(tfHraAmt.getValue().toString());
		if (approvedamount > hraamt) {
			tfHraAmt.setComponentError(new UserError(GERPErrorCodes.NULL_HRA_AMT));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Saving Data... ");
			ITHraDeclDM itHraDeclObj = new ITHraDeclDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				itHraDeclObj = beanITHraDeclDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			itHraDeclObj.setEmpId((Long) cbEmpName.getValue());
			itHraDeclObj.setFinYear(tfFinYear.getValue());
			itHraDeclObj.setFinMonth((String) cbFinMonth.getValue());
			itHraDeclObj.setAppAmt(new BigDecimal(tfApprovedAmt.getValue()));
			itHraDeclObj.setHraAmt(new BigDecimal(tfHraAmt.getValue()));
			if (cbStatus.getValue() != null) {
				itHraDeclObj.setStatus((String) cbStatus.getValue());
			}
			itHraDeclObj.setVerifiedDt(dfVerifiedDt.getValue());
			itHraDeclObj.setVerifiedBy(EmployeeId.toString());
			itHraDeclObj.setLastUpdatedDt(DateUtils.getcurrentdate());
			itHraDeclObj.setLastUpdatedBy(loginUserName);
			file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContents[] = new byte[(int) file.length()];
			fio.read(fileContents);
			fio.close();
			itHraDeclObj.setProofDoc(fileContents);
			serviceITHraDecl.saveITHRA(itHraDeclObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info("saveDetails---------------_>");
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > "
				+ "Getting audit record for IT Other HRA deaclaration ID " + primaryid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_IT_HRA_DECL);
		UI.getCurrent().getSession().setAttribute("audittablepk", primaryid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + loginUserName + " > " + "Canceling action ");
		assembleSearchLayout();
		cbEmpName.setRequired(false);
		tfApprovedAmt.setRequired(false);
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		vlSrchRsltContainer.setExpandRatio(tblMstScrSrchRslt, 1);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		cbEmpName.setValue(null);
		tfApprovedAmt.setValue(null);
		tfHraAmt.setValue(null);
		dfVerifiedDt.setValue(null);
		cbFinMonth.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbEmpName.setComponentError(null);
		tfHraAmt.setComponentError(null);
		tfApprovedAmt.setComponentError(null);
		new UploadDocumentUI(hlithradDoc);
	}
}
