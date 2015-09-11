/**
 * File Name 		: EmploymentStatus .java 
 * Description 		: this class is used for add/edit Employment Status details. 
 * Author 			:  KAVITHA V M 
 * Date 			: 06-Auguest-2014	
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. 
 * All rights reserved.
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 *
 * Version       Date           	Modified By               Remarks
 * 0.1          06-Auguest-2014	        KAVITHA V M	        Initial Version
 * 
 */
package com.gnts.hcm.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.EmploymentStatusDM;
import com.gnts.hcm.service.mst.EmploymentStatusService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class EmploymentStatus extends BaseUI {
	// Bean creation
	private EmploymentStatusService serviceEmploymentStatus = (EmploymentStatusService) SpringContextHelper
			.getBean("EmploymentStatus");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfStatusCode, tfStatusDesc;
	private CheckBox ckProcSalary;
	private ComboBox cbStatus;
	private Boolean errorFlag = false;
	// BeanItemContainer
	private BeanItemContainer<EmploymentStatusDM> beanEmploymentStatusDM = null;
	// local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmploymentStatus.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmploymentStatus() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmploymentStatus() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Printing Employment Status UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Employment Status Description text field
		tfStatusCode = new GERPTextField("Emp. Status Code");
		// Employment Status Description Area
		tfStatusDesc = new GERPTextField("Emp. Status Desc");
		tfStatusDesc.setWidth("250");
		// Salary field
		ckProcSalary = new CheckBox("");
		ckProcSalary.setCaption("Process Salary");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn1.addComponent(tfStatusCode);
		flColumn2.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlSearchLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(tfStatusCode);
		flColumn2.addComponent(tfStatusDesc);
		flColumn3.addComponent(ckProcSalary);
		flColumn4.addComponent(cbStatus);
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
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<EmploymentStatusDM> listEmpStatus = new ArrayList<EmploymentStatusDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfStatusCode.getValue() + ", " + tfStatusCode.getValue()
					+ (String) cbStatus.getValue());
			listEmpStatus = serviceEmploymentStatus.getEmploymentStatusList(null, tfStatusCode.getValue(), companyid,
					(String) cbStatus.getValue());
			recordCnt = listEmpStatus.size();
			beanEmploymentStatusDM = new BeanItemContainer<EmploymentStatusDM>(EmploymentStatusDM.class);
			beanEmploymentStatusDM.addAll(listEmpStatus);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the EmploymentStatusList. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanEmploymentStatusDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "empstatusid", "empstatuscode", "empstatusdesc",
					"status", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Emp. Status Code", "Emp. Status Desc",
					"Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("empstatusid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		tfStatusCode.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		resetFields();
		tfStatusCode.setRequired(true);
		tfStatusDesc.setRequired(true);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfStatusCode.setRequired(true);
		tfStatusDesc.setRequired(true);
		editEmp();
	}
	
	private void editEmp() {
		try {
			if (tblMstScrSrchRslt.getValue() != null) {
				EmploymentStatusDM empsts = beanEmploymentStatusDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				if (empsts.getEmpstatusdesc() != null) {
					tfStatusDesc.setValue(empsts.getEmpstatusdesc());
				}
				if (empsts.getEmpstatuscode() != null) {
					tfStatusCode.setValue(empsts.getEmpstatuscode());
				}
				if (empsts.getProcesssalary().equals("Y")) {
					ckProcSalary.setValue(true);
				} else {
					ckProcSalary.setValue(false);
				}
				cbStatus.setValue(empsts.getStatus());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfStatusCode.setComponentError(null);
		tfStatusDesc.setComponentError(null);
		errorFlag = false;
		if ((tfStatusCode.getValue() == null) || tfStatusCode.getValue().trim().length() == 0) {
			tfStatusCode.setComponentError(new UserError(GERPErrorCodes.NULL_EMP_STS_CODE));
			errorFlag = true;
		}
		if ((tfStatusDesc.getValue() == null) || tfStatusDesc.getValue().trim().length() == 0) {
			tfStatusDesc.setComponentError(new UserError(GERPErrorCodes.NULL_EMP_STS_DESC));
			errorFlag = true;
		}
		EmploymentStatusDM employmentstatusobj = new EmploymentStatusDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			employmentstatusobj = beanEmploymentStatusDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if ((tfStatusCode.getValue() != null) && employmentstatusobj.getEmpstatusid() == null) {
			if (serviceEmploymentStatus.getEmploymentStatusList(null, tfStatusCode.getValue(), companyid, "Active")
					.size() > 0) {
				tfStatusCode.setComponentError(new UserError("Earning Code already Exist"));
				errorFlag = true;
			}
		}
		if (errorFlag) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfStatusCode.getValue());
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			EmploymentStatusDM employmentStatus = new EmploymentStatusDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				employmentStatus = beanEmploymentStatusDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (tfStatusCode.getValue() != null) {
				employmentStatus.setEmpstatuscode(tfStatusCode.getValue().toString());
			}
			if (tfStatusDesc.getValue() != null) {
				employmentStatus.setEmpstatusdesc(tfStatusDesc.getValue().toString());
			}
			if (ckProcSalary.getValue().equals(true)) {
				employmentStatus.setProcesssalary("Y");
			} else {
				employmentStatus.setProcesssalary("N");
			}
			employmentStatus.setCompanyid(companyid);
			if (cbStatus.getValue() != null) {
				employmentStatus.setStatus((String) cbStatus.getValue());
			}
			employmentStatus.setLastupdateddt(DateUtils.getcurrentdate());
			employmentStatus.setLastupdatedby(username);
			serviceEmploymentStatus.saveAndUpdateEmploymentStsDetails(employmentStatus);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfStatusCode.setRequired(false);
		tfStatusDesc.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		tfStatusCode.setValue("");
		tfStatusDesc.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		ckProcSalary.setValue(false);
		tfStatusCode.setComponentError(null);
		tfStatusDesc.setComponentError(null);
	}
}
