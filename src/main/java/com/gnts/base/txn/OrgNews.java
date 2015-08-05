/**
 * File Name	:	OrgNews.java
 * Description	:	this class is used for declare ClientsDAO class methods
 * Author		:	Priyanka
 * Date			:	Mar 07, 2014
 * Modification 
 * Modified By  :   
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of  GNTS Technologies pvt. ltd.
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1          JUN 16 2014        	MOHAMED	        Initial Version
 * 0.2			23-Jun-2014			MOHAMED			Code re-factoring
 */
package com.gnts.base.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.txn.OrgNewsDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.txn.OrgNewsService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class OrgNews extends BaseUI {
	private OrgNewsService serviceNews = (OrgNewsService) SpringContextHelper.getBean("news");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private DepartmentService serviceDepartment = (DepartmentService) SpringContextHelper.getBean("department");
	private String username;
	private BeanItemContainer<OrgNewsDM> beanNews = null;
	// form layout for input controlsa
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private int recordCnt = 0;
	// Declaration for add and edit panel
	private TextField tfNewsTitle;
	private RichTextArea rtaNewsDesc;
	private ComboBox cbBranch, cbNewsStatus, cbDepartment;
	private PopupDateField dfValidFrom, dfValidTo;
	private Long companyid, branchId;
	private String newsId;
	// intialize the logger
	private Logger logger = Logger.getLogger(OrgNews.class);
	private static final long serialVersionUID = 1L;
	
	public OrgNews() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside OrgNews() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting OrgNews UI");
		// NewsDesc Name text field
		tfNewsTitle = new GERPTextField("News Title");
		tfNewsTitle.setMaxLength(25);
		dfValidFrom = new GERPPopupDateField("Valid From");
		dfValidFrom.setValue(DateUtils.getcurrentdate());
		dfValidFrom.setRequired(true);
		dfValidTo = new GERPPopupDateField("Valid Upto");
		dfValidTo.setValue(DateUtils.getcurrentdate());
		dfValidTo.setRequired(true);
		// NewsDescTitle text field
		rtaNewsDesc = new RichTextArea("News Description");
		rtaNewsDesc.setWidth("350");
		// Initialization and properties cbNewsStatus
		cbNewsStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// populate the branch combo box
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setNullSelectionAllowed(false);
		loadBranchDetails();
		// populate the Department combo box
		cbDepartment = new GERPComboBox("Department Name");
		cbDepartment.setItemCaptionPropertyId("deptname");
		cbDepartment.setNullSelectionAllowed(false);
		loadDepartment();
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(tfNewsTitle);
		flColumn2.addComponent(cbBranch);
		flColumn3.addComponent(cbDepartment);
		flColumn4.addComponent(cbNewsStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
			// add the form layouts into user input layout
			hlUserInputLayout.removeAllComponents();
			flColumn1 = new FormLayout();
			flColumn2 = new FormLayout();
			flColumn1.addComponent(tfNewsTitle);
			flColumn1.addComponent(cbBranch);
			flColumn1.addComponent(cbDepartment);
			flColumn1.addComponent(dfValidFrom);
			flColumn1.addComponent(dfValidTo);
			flColumn1.addComponent(cbNewsStatus);
			hlUserInputLayout.addComponent(flColumn1);
			hlUserInputLayout.addComponent(flColumn2);
			flColumn2.addComponent(rtaNewsDesc);
			hlUserInputLayout.addComponent(flColumn2);
			hlUserInputLayout.setSpacing(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Branch list for pnlmain's combo Box
	private void loadBranchDetails() {
		List<BranchDM> list = new ArrayList<BranchDM>();
		list.add(new BranchDM(0L, "All Branches"));
		list.addAll(serviceBranch.getBranchList(branchId, null, null, (String) cbNewsStatus.getValue(), companyid, "P"));
		BeanContainer<Long, BranchDM> beansbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beansbranch.setBeanIdProperty("branchId");
		beansbranch.addAll(list);
		cbBranch.setContainerDataSource(beansbranch);
	}
	
	// Load Department list for pnladdedit's combo Box
	private void loadDepartment() {
		List<DepartmentDM> list = new ArrayList<DepartmentDM>();
		list.add(new DepartmentDM(0L, "All Department"));
		list.addAll(serviceDepartment.getDepartmentList(companyid, null, "Active", "P"));
		BeanContainer<Long, DepartmentDM> beandept = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beandept.setBeanIdProperty("deptid");
		beandept.addAll(list);
		cbDepartment.setContainerDataSource(beandept);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<OrgNewsDM> orgList = new ArrayList<OrgNewsDM>();
		Long branchid = null, deptid = null;
		if (cbBranch.getValue() != null) {
			branchid = (Long) cbBranch.getValue();
		}
		if (cbDepartment.getValue() != null) {
			deptid = (Long) cbDepartment.getValue();
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfNewsTitle.getValue() + ", " + (String) cbNewsStatus.getValue());
		orgList = serviceNews.getNewsList(null, tfNewsTitle.getValue(), rtaNewsDesc.getValue(),
				(String) cbNewsStatus.getValue(), companyid, branchid, deptid);
		recordCnt = orgList.size();
		beanNews = new BeanItemContainer<OrgNewsDM>(OrgNewsDM.class);
		beanNews.addAll(orgList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the orgNews. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanNews);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "newsId", "newsTitle", "branchName", "departmentName",
				"validFrom", "validTo", "newsStatus", "lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id ", "News", "Branch", "Department", "Valid From",
				"Valid Upto", "Status", "Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("newsId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void editnews() {
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			OrgNewsDM orgNewsDM = beanNews.getItem(tblMstScrSrchRslt.getValue()).getBean();
			newsId = orgNewsDM.getnewsId().toString();
			rtaNewsDesc.setValue(orgNewsDM.getNewsDesc());
			tfNewsTitle.setValue(orgNewsDM.getNewsTitle());
			cbBranch.setValue(orgNewsDM.getBranchId());
			cbDepartment.setValue(orgNewsDM.getDeptId());
			dfValidFrom.setValue(orgNewsDM.getValidFromInDt());
			dfValidTo.setValue(orgNewsDM.getValidToInDt());
			cbNewsStatus.setValue(cbNewsStatus.getItemIds().iterator().next());
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
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbNewsStatus.setValue(cbNewsStatus.getItemIds().iterator().next());
		rtaNewsDesc.setValue("");
		hlCmdBtnLayout.setVisible(true);
		vlSrchRsltContainer.setVisible(true);
		cbBranch.setValue(0L);
		cbDepartment.setValue(0L);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfNewsTitle.setValue("");
		cbBranch.setValue(null);
		cbDepartment.setValue(null);
		tfNewsTitle.setRequired(true);
		dfValidFrom.setRequired(true);
		dfValidTo.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for client cat. ID " + newsId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_BASE_ORG_NEWS);
		UI.getCurrent().getSession().setAttribute("audittablepk", newsId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		vlSrchRsltContainer.setVisible(true);
		tfNewsTitle.setRequired(false);
		dfValidFrom.setRequired(false);
		dfValidTo.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		dfValidFrom.setComponentError(null);
		dfValidTo.setComponentError(null);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfNewsTitle.setValue("");
		rtaNewsDesc.setValue("");
		cbBranch.setValue(0L);
		cbDepartment.setValue(0L);
		dfValidFrom.setValue(null);
		dfValidTo.setValue(null);
		tfNewsTitle.setComponentError(null);
		cbNewsStatus.setValue(cbNewsStatus.getItemIds().iterator().next());
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfNewsTitle.setVisible(true);
		dfValidFrom.setVisible(true);
		dfValidTo.setVisible(true);
		cbBranch.setVisible(true);
		cbDepartment.setVisible(true);
		cbNewsStatus.setVisible(true);
		rtaNewsDesc.setVisible(true);
		tfNewsTitle.setRequired(true);
		dfValidFrom.setRequired(true);
		dfValidTo.setRequired(true);
		editnews();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		boolean errorflag = false;
		tfNewsTitle.setComponentError(null);
		dfValidFrom.setComponentError(null);
		dfValidTo.setComponentError(null);
		if ((tfNewsTitle.getValue() == null) || tfNewsTitle.getValue().trim().length() == 0) {
			tfNewsTitle.setComponentError(new UserError(GERPErrorCodes.NULL_ORG_NEWS));
			errorflag = true;
		} else {
			tfNewsTitle.setComponentError(null);
		}
		if (dfValidFrom.getValue() == null) {
			dfValidFrom.setComponentError(new UserError(GERPErrorCodes.DATE_NOT_NULL));
			errorflag = true;
		} else {
			dfValidFrom.setComponentError(null);
		}
		if (dfValidTo.getValue() == null) {
			dfValidTo.setComponentError(new UserError(GERPErrorCodes.DATE_NOT_NULL_END));
			errorflag = true;
		} else {
			dfValidTo.setComponentError(null);
		}
		if (dfValidFrom.getValue() != null || (dfValidTo.getValue() != null)) {
			if (dfValidFrom.getValue().after(dfValidTo.getValue())) {
				dfValidTo.setComponentError(new UserError(GERPErrorCodes.DATE_VLIDTN));
				logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Throwing ValidationException. User data is > " + dfValidFrom.getValue());
				errorflag = true;
			} else {
				dfValidTo.setComponentError(null);
			}
		}
		if (errorflag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			OrgNewsDM newsobj = new OrgNewsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				newsobj = beanNews.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			newsobj.setCompanyId(companyid);
			newsobj.setNewsTitle(tfNewsTitle.getValue());
			newsobj.setNewsDesc(rtaNewsDesc.getValue());
			newsobj.setBranchId((Long) cbBranch.getValue());
			newsobj.setDeptId((Long) cbDepartment.getValue());
			newsobj.setValidFrom(dfValidFrom.getValue());
			newsobj.setValidTo(dfValidTo.getValue());
			if (cbNewsStatus.getValue() != null) {
				newsobj.setNewsStatus((String) cbNewsStatus.getValue());
			}
			newsobj.setLastUpdatedDate(DateUtils.getcurrentdate());
			newsobj.setLastUpdatedBy(username);
			serviceNews.saveAndUpdateNews(newsobj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}