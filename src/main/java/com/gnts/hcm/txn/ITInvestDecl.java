/**
 * File Name	:	ITInvestDecl.java
 * Description	:	this class is used for add/edit ITInvestDecl details. 
 * Author		:	KAVITHA V M
 * Date			:	24-September-2014
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 *  Version         Date          		Modified By             Remarks
 *  0.1             24-September-2014   KAVITHA V M	  			Initial Version       
 * 
 */
package com.gnts.hcm.txn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.ITInvestDecDM;
import com.gnts.hcm.service.txn.ITInvestDecService;
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

public class ITInvestDecl extends BaseUI {
	private ITInvestDecService serviceITInvestDec = (ITInvestDecService) SpringContextHelper.getBean("ITInvest");
	private ParameterService serviceParameter = (ParameterService) SpringContextHelper.getBean("parameter");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private VerticalLayout vlappdoc = new VerticalLayout();
	// Add User Input Controls
	private ComboBox cbEmployeeName, cbStatus;
	private PopupDateField dfInvstedDt, dfVerifdDt;
	private TextField tfFinYear, tfSectnCode, tfInvstdAmt, tfApprvdAmt, tfVerifdBy;
	// BeanItemContainer
	private BeanItemContainer<ITInvestDecDM> beanITInvestDecDM = null;
	// local variables declaration
	private Long companyId, employeeId;
	private int recordCnt = 0;
	private String userName, itInvestDeclId;
	// Initialize logger
	private Logger logger = Logger.getLogger(ITInvestDecDM.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public ITInvestDecl() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside ITInvestDecl() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Printing ITInvestDecl UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("120");
		cbEmployeeName = new GERPComboBox("Employee Name");
		cbEmployeeName.setItemCaptionPropertyId("fullname");
		cbEmployeeName.setWidth("180");
		loadEmployeeList();
		tfFinYear = new GERPTextField("Finance Year");
		tfFinYear.setReadOnly(false);
		tfFinYear.setWidth("180");
		tfFinYear.setValue(serviceParameter.getParameterValue("FM_FINYEAR", companyId, null));
		tfFinYear.setReadOnly(true);
		tfSectnCode = new GERPTextField("Section Code");
		tfSectnCode.setWidth("180");
		dfInvstedDt = new GERPPopupDateField("Invested Date");
		tfInvstdAmt = new GERPTextField("Invested Amount");
		tfApprvdAmt = new GERPTextField("Approved Amount");
		tfVerifdBy = new GERPTextField("Verified By");
		dfVerifdDt = new GERPPopupDateField("Verified Date");
		// // build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		VerticalLayout img = new VerticalLayout();
		new UploadDocumentUI(vlappdoc);
		img.addComponent(vlappdoc);
		img.setSpacing(true);
		img.setMargin(true);
		img.setSizeFull();
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.setBeanIdProperty("employeeid");
			beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyId, employeeId,
					null, null, null, "P"));
			cbEmployeeName.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		// Add components for Search Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(cbEmployeeName);
		flColumn2.addComponent(tfSectnCode);
		flColumn3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbEmployeeName);
		flColumn1.addComponent(tfSectnCode);
		flColumn1.addComponent(tfFinYear);
		flColumn2.addComponent(tfInvstdAmt);
		flColumn2.addComponent(tfApprvdAmt);
		flColumn2.addComponent(tfVerifdBy);
		flColumn3.addComponent(dfInvstedDt);
		flColumn3.addComponent(dfVerifdDt);
		flColumn3.addComponent(cbStatus);
		flColumn3.setMargin(true);
		flColumn4.addComponent(vlappdoc);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		tblMstScrSrchRslt.setPageLength(13);
		List<ITInvestDecDM> listITInvestDec = new ArrayList<ITInvestDecDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyId + ", " + (Long) cbEmployeeName.getValue() + ", " + (String) tfSectnCode.getValue()
				+ (String) cbStatus.getValue());
		listITInvestDec = serviceITInvestDec.getITInvestList(null, (Long) cbEmployeeName.getValue(),
				(String) tfSectnCode.getValue(), (String) cbStatus.getValue(), "P");
		recordCnt = listITInvestDec.size();
		beanITInvestDecDM = new BeanItemContainer<ITInvestDecDM>(ITInvestDecDM.class);
		beanITInvestDecDM.addAll(listITInvestDec);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Got the ITInvestDec. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanITInvestDecDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "itDeclId", "fulName", "secCode", "investAmt", "appAmt",
				"verifiedBy", "status", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "Section Code", "Invested Amount",
				"Approved Amount", "Verified By", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("itDeclId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
			hlUserInputLayout.removeAllComponents();
			loadSrchRslt();
			if (recordCnt == 0) {
				logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
						+ "No data for the search. throwing ERPException.NoDataFoundException");
				throw new ERPException.NoDataFoundException();
			} else {
				lblNotification.setIcon(null);
				lblNotification.setCaption("");
				assembleSearchLayout();
			}
		}
		catch (Exception ex) {
			logger.info("search details" + ex);
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tfSectnCode.setValue("");
		cbEmployeeName.setValue(null);
		tfSectnCode.setValue(null);
		cbStatus.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlSearchLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		hlUserInputLayout.setSpacing(true);
		tblMstScrSrchRslt.setVisible(true);
		cbEmployeeName.setRequired(true);
		tfSectnCode.setRequired(true);
		tfInvstdAmt.setRequired(true);
		new UploadDocumentUI(vlappdoc);
		assembleUserInputLayout();
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		cbEmployeeName.setRequired(true);
		tfSectnCode.setRequired(true);
		tfInvstdAmt.setRequired(true);
		resetFields();
		editItInvestDeclDetails();
	}
	
	private void editItInvestDeclDetails() {
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			ITInvestDecDM itinvestDecDM = beanITInvestDecDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			if ((itinvestDecDM.getEmpId() != null)) {
				cbEmployeeName.setValue(itinvestDecDM.getEmpId());
			}
			if ((itinvestDecDM.getFinYear() != null)) {
				tfFinYear.setReadOnly(false);
				tfFinYear.setValue(itinvestDecDM.getFinYear());
			}
			if ((itinvestDecDM.getSecCode() != null)) {
				tfSectnCode.setValue(itinvestDecDM.getSecCode());
			}
			if (itinvestDecDM.getInvestDate() != null) {
				dfInvstedDt.setValue(itinvestDecDM.getInvestDate());
			}
			if ((itinvestDecDM.getInvestAmt() != null)) {
				tfInvstdAmt.setValue(itinvestDecDM.getInvestAmt().toString());
			}
			if ((itinvestDecDM.getAppAmt() != null)) {
				tfApprvdAmt.setValue(itinvestDecDM.getAppAmt().toString());
			}
			if ((itinvestDecDM.getVerifiedBy() != null)) {
				tfVerifdBy.setValue(itinvestDecDM.getVerifiedBy().toString());
			}
			if (itinvestDecDM.getVerifiedDt() != null) {
				dfVerifdDt.setValue(itinvestDecDM.getVerifiedDt());
			}
			if (itinvestDecDM.getStatus() != null) {
				cbStatus.setValue(itinvestDecDM.getStatus());
			}
			if (itinvestDecDM.getProofDoc() != null) {
				byte[] certificate = itinvestDecDM.getProofDoc();
				UploadDocumentUI test = new UploadDocumentUI(vlappdoc);
				test.displaycertificate(certificate);
			} else {
				new UploadDocumentUI(vlappdoc);
			}
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		boolean erroflag = false;
		tfSectnCode.setComponentError(null);
		cbEmployeeName.setComponentError(null);
		tfInvstdAmt.setComponentError(null);
		if (cbEmployeeName.getValue() == null) {
			cbEmployeeName.setComponentError(new UserError(GERPErrorCodes.NULL_EMP_SHIFT));
			erroflag = true;
		}
		if ((tfSectnCode.getValue() == null) || tfSectnCode.getValue().trim().length() == 0) {
			tfSectnCode.setComponentError(new UserError(GERPErrorCodes.NULL_SECTION_CODE));
			erroflag = true;
		}
		if ((tfInvstdAmt.getValue() == null) || tfInvstdAmt.getValue().trim().length() == 0) {
			tfInvstdAmt.setComponentError(new UserError(GERPErrorCodes.NULL_INVEST_DECL));
			erroflag = true;
		}
		if (erroflag) {
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + tfSectnCode.getValue() + tfInvstdAmt.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
			ITInvestDecDM itInvestobj = new ITInvestDecDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				itInvestobj = beanITInvestDecDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			itInvestobj.setEmpId((Long) cbEmployeeName.getValue());
			itInvestobj.setFinYear(tfFinYear.getValue().toString());
			if (tfSectnCode.getValue() != null) {
				itInvestobj.setSecCode(tfSectnCode.getValue().toString());
			}
			if (dfInvstedDt.getValue() != null) {
				itInvestobj.setInvestDate(dfInvstedDt.getValue());
			}
			if (tfInvstdAmt.getValue() != null) {
				itInvestobj.setInvestAmt(new BigDecimal(tfInvstdAmt.getValue()));
			}
			if (tfApprvdAmt.getValue() != null) {
				itInvestobj.setAppAmt(new BigDecimal(tfApprvdAmt.getValue()));
			}
			if (tfVerifdBy.getValue() != null) {
				itInvestobj.setVerifiedBy(tfVerifdBy.getValue().toString());
			}
			if (dfVerifdDt.getValue() != null) {
				itInvestobj.setVerifiedDt(dfVerifdDt.getValue());
			}
			if (cbStatus.getValue() != null) {
				itInvestobj.setStatus((String) cbStatus.getValue());
			}
			File file = new File(GERPConstants.DOCUMENT_PATH);
			FileInputStream fio = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];
			fio.read(fileContent);
			fio.close();
			itInvestobj.setProofDoc(fileContent);
			itInvestobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			itInvestobj.setLastUpdatedBy(userName);
			serviceITInvestDec.saveItInvest(itInvestobj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for ItInvestDecl. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_IT_INVESTMNT_DEC);
		UI.getCurrent().getSession().setAttribute("audittablepk", itInvestDeclId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		assembleSearchLayout();
		cbEmployeeName.setRequired(false);
		tfSectnCode.setRequired(false);
		tfInvstdAmt.setRequired(false);
		vlSrchRsltContainer.removeAllComponents();
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		cbEmployeeName.setValue(null);
		cbEmployeeName.setComponentError(null);
		tfSectnCode.setValue(null);
		tfSectnCode.setComponentError(null);
		dfInvstedDt.setValue(null);
		dfInvstedDt.setComponentError(null);
		tfInvstdAmt.setValue(null);
		tfInvstdAmt.setComponentError(null);
		tfApprvdAmt.setValue(null);
		tfApprvdAmt.setComponentError(null);
		tfVerifdBy.setValue(null);
		tfVerifdBy.setComponentError(null);
		dfVerifdDt.setValue(null);
		new UploadDocumentUI(vlappdoc);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
}
