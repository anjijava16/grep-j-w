/**
 * File Name 		: ClientCases.java 
 * Description 		: this class is used for add/edit ClientCases  details. 
 * Author 			: P Sekhar
 * Date 			: Mar 26, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
Version       Date           	Modified By               Remarks
 * 0.1          JUN 06 2014        	MOHAMED	        Initial Version
 * 0.2			12-Jun-2014			MOHAMED			Code re-factoring
 *  0.3			30-JULY-2014			MOHAMED			Code re-factoring
 */
package com.gnts.crm.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.domain.txn.ClientCasesDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.crm.service.txn.ClientCasesService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.erputil.validations.StringValidation;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.service.txn.WorkOrderDtlService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsPODtlDM;
import com.gnts.sms.domain.txn.SmsPOHdrDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsPOHdrService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ClientCases extends BaseTransUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private WorkOrderDtlService serviceWrkOrdDtl = (WorkOrderDtlService) SpringContextHelper.getBean("workOrderDtl");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private CompanyLookupService serviceCompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private SmsEnqHdrService serviceEnquiryHdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private WorkOrderHdrService serviceWrkOrdHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private SmsPOHdrService servicePurchaseOrdHdr = (SmsPOHdrService) SpringContextHelper.getBean("smspohdr");
	private ClientCasesService serviceCase = (ClientCasesService) SpringContextHelper.getBean("clientCase");
	private Long companyId;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	private HorizontalLayout hlSearchLayout;
	private String userName, strWidth = "160px";
	private VerticalLayout vlCommetTblLayout = new VerticalLayout();
	private VerticalLayout vlDocumentLayout = new VerticalLayout();
	/**
	 * UI Components
	 */
	private TextField tfCaseTitle, tfCaseResult, tfEffortDays, tfSearchTitle, tfpartNo, tfDrgNo;
	private ComboBox cbClient, cbEmployee, cbCaseCategory, cbPriority, cbSevrity, cbClntCaseStatus, cbEnquiryNo,
			cbwoNo, cbPONo, cbprdname;
	private TextArea taCaseDesc;
	private BeanItemContainer<ClientCasesDM> beanClntCases = null;
	private Long moduleId, employeeId, deptId, clientId;
	private int recordCnt = 0;
	private Logger logger = Logger.getLogger(ClientCasesDM.class);
	private Long clientCaseId;
	private Comments comment;
	private Documents document;
	
	public ClientCases() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = Long.valueOf(UI.getCurrent().getSession().getAttribute("moduleId").toString());
		deptId = Long.valueOf(UI.getCurrent().getSession().getAttribute("deptId").toString());
		buildview();
	}
	
	/**
	 * buildMainview()-->for screen UI design
	 * 
	 * @param clArgumentLayout
	 * @param hlHeaderLayout
	 */
	private void buildview() {
		/**
		 * Declaration add/edit text field and combo box fields
		 */
		cbEnquiryNo = new GERPComboBox("Enquiry No.");
		cbEnquiryNo.setItemCaptionPropertyId("enquiryNo");
		cbEnquiryNo.setNullSelectionAllowed(false);
		cbEnquiryNo.setWidth(strWidth);
		loadEnquiryNo();
		cbPONo = new GERPComboBox("Purchase Order No.");
		cbPONo.setItemCaptionPropertyId("pono");
		cbPONo.setNullSelectionAllowed(false);
		cbPONo.setWidth(strWidth);
		cbprdname = new GERPComboBox("Product");
		cbprdname.setItemCaptionPropertyId("prodname");
		cbprdname.setNullSelectionAllowed(false);
		cbprdname.setWidth(strWidth);
		cbwoNo = new GERPComboBox("Work Order No.");
		cbwoNo.setItemCaptionPropertyId("workOrdrNo");
		cbwoNo.setNullSelectionAllowed(false);
		cbwoNo.setWidth(strWidth);
		cbClient = new GERPComboBox("Client Name");
		cbClient.setItemCaptionPropertyId("clientName");
		cbClient.setNullSelectionAllowed(false);
		cbClient.setWidth(strWidth);
		cbprdname.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (cbprdname.getValue() != null) {
					try {
						if (((SmsPODtlDM) cbprdname.getValue()).getCustomField1() != null) {
							tfpartNo.setReadOnly(false);
							tfpartNo.setValue(((SmsPODtlDM) cbprdname.getValue()).getCustomField1());
							tfpartNo.setReadOnly(true);
						} else {
							tfpartNo.setReadOnly(false);
							tfpartNo.setValue("");
							tfpartNo.setReadOnly(true);
						}
						if (((SmsPODtlDM) cbprdname.getValue()).getCustomField2() != null) {
							tfDrgNo.setReadOnly(false);
							tfDrgNo.setValue(((SmsPODtlDM) cbprdname.getValue()).getCustomField2());
							tfDrgNo.setReadOnly(true);
						} else {
							tfDrgNo.setReadOnly(false);
							tfDrgNo.setValue("");
							tfDrgNo.setReadOnly(true);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		cbEnquiryNo.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbEnquiryNo.getValue() != null) {
					loadClientsDetails();
					loadworkorder();
					loadPurchaseOrdNo();
				}
			}
		});
		tfCaseTitle = new GERPTextField("Complaint Title");
		tfCaseTitle.setMaxLength(50);
		tfCaseTitle.setWidth(strWidth);
		tfCaseTitle.addValidator(new StringValidation("Enter alpanumeric with space"));
		tfCaseTitle.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfCaseTitle.setComponentError(null);
				if (tfCaseTitle.getValue() != null) {
					tfCaseTitle.setComponentError(null);
				}
			}
		});
		cbEmployee = new GERPComboBox("Assigned To");
		cbEmployee.setItemCaptionPropertyId("firstname");
		cbEmployee.setWidth(strWidth);
		cbEmployee.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbEmployee.setComponentError(null);
				if (cbEmployee.getValue() != null) {
					cbEmployee.setComponentError(null);
				}
			}
		});
		loadEmployeeList();
		cbCaseCategory = new GERPComboBox("Category");
		cbCaseCategory.setItemCaptionPropertyId("lookupname");
		cbCaseCategory.setNullSelectionAllowed(false);
		cbCaseCategory.setWidth(strWidth);
		cbCaseCategory.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbCaseCategory.setComponentError(null);
				if (cbCaseCategory.getValue() != null) {
					cbCaseCategory.setComponentError(null);
				}
			}
		});
		loadCaseCategotyByLookUpList();
		taCaseDesc = new GERPTextArea("Problem Reports");
		taCaseDesc.setWidth(strWidth);
		taCaseDesc.setHeight("50px");
		cbPriority = new GERPComboBox("Priority");
		cbPriority.setItemCaptionPropertyId("lookupname");
		cbPriority.setNullSelectionAllowed(false);
		cbPriority.setWidth(strWidth);
		loadCasePriorityByLookUpList();
		cbPONo.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbPONo.getValue() != null) {
					loadPurchseOrdDtlList();
				}
			}
		});
		cbSevrity = new GERPComboBox("Severity");
		cbSevrity.setItemCaptionPropertyId("lookupname");
		cbSevrity.setWidth(strWidth);
		loadCaseSevrityByLookUpList();
		tfCaseResult = new TextField("Result");
		tfCaseResult.setMaxLength(100);
		tfCaseResult.setWidth(strWidth);
		tfEffortDays = new GERPTextField("Effort Days");
		tfEffortDays.setWidth("110");
		cbClntCaseStatus = new GERPComboBox("Status", BASEConstants.T_CRM_CLIENT_CASES,
				BASEConstants.T_CRM_CLIENT_CASES_STATUS);
		cbClntCaseStatus.setItemCaptionPropertyId("desc");
		tfSearchTitle = new GERPTextField("Case Title");
		tfSearchTitle.setWidth(strWidth);
		tfpartNo = new GERPTextField("Part No.");
		tfpartNo.setWidth(strWidth);
		tfDrgNo = new GERPTextField("Drg No.");
		tfDrgNo.setWidth(strWidth);
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbClient);
		flColumn2.addComponent(tfSearchTitle);
		flColumn3.addComponent(cbEmployee);
		flColumn4.addComponent(cbClntCaseStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// add the form layouts into user input layout
		hlUserInputLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(cbEnquiryNo);
		flColumn1.addComponent(cbClient);
		flColumn1.addComponent(cbwoNo);
		flColumn2.addComponent(cbPONo);
		flColumn2.addComponent(cbprdname);
		flColumn2.addComponent(tfpartNo);
		flColumn2.addComponent(tfDrgNo);
		tfDrgNo.setReadOnly(true);
		tfpartNo.setReadOnly(true);
		cbClient.setRequired(true);
		flColumn1.addComponent(tfCaseTitle);
		tfCaseTitle.setRequired(true);
		flColumn1.addComponent(cbEmployee);
		cbEmployee.setRequired(true);
		flColumn2.addComponent(cbCaseCategory);
		flColumn3.addComponent(taCaseDesc);
		flColumn3.addComponent(cbPriority);
		flColumn3.addComponent(cbSevrity);
		flColumn3.addComponent(tfCaseResult);
		flColumn4.addComponent(tfEffortDays);
		flColumn4.addComponent(cbClntCaseStatus);
		HorizontalLayout hlInput = new HorizontalLayout();
		hlInput.setMargin(true);
		VerticalLayout hlUserInput = new VerticalLayout();
		hlUserInput.setSpacing(true);
		hlInput.addComponent(flColumn1);
		hlInput.addComponent(flColumn2);
		hlInput.addComponent(flColumn3);
		hlInput.addComponent(flColumn4);
		hlUserInput.addComponent(GERPPanelGenerator.createPanel(hlInput));
		TabSheet test3 = new TabSheet();
		test3.addTab(vlCommetTblLayout, "Comments");
		test3.addTab(vlDocumentLayout, "Documents");
		test3.setWidth("1100");
		hlUserInput.addComponent(test3);
		hlUserInputLayout.addComponent(hlUserInput);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	/**
	 * populatedAndConfig()-->this function used to load the list to the table if(search==true)--> it performs search
	 * operation else it loads all values
	 * 
	 * @param search
	 */
	public void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<ClientCasesDM> caseList = new ArrayList<ClientCasesDM>();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
					+ companyId + ", " + tfCaseResult.getValue() + ",");
			caseList = serviceCase.getClientCaseDetails(companyId, null, (Long) cbClient.getValue(), null,
					tfCaseTitle.getValue(), (String) cbClntCaseStatus.getValue(), "F");
			recordCnt = caseList.size();
			beanClntCases = new BeanItemContainer<ClientCasesDM>(ClientCasesDM.class);
			beanClntCases.addAll(caseList);
			tblMstScrSrchRslt.setContainerDataSource(beanClntCases);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "clientCaseId", "clientName", "caseTitle",
					"employeename", "caseCategory", "casePriority", "caseStatus", "lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Client Name", "Case Title", "Assigned To",
					"Case Category", "Case Priority", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	private void editClientCaseDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Selected Dept. Id -> "
				+ clientCaseId);
		if (tblMstScrSrchRslt.getValue() != null) {
			ClientCasesDM editCaselist = beanClntCases.getItem(tblMstScrSrchRslt.getValue()).getBean();
			clientCaseId = editCaselist.getClientCaseId();
			tfCaseResult.setValue(editCaselist.getCaseresltn());
			cbEnquiryNo.setValue(editCaselist.getEnquiryId());
			cbClient.setValue(editCaselist.getClientId());
			cbPONo.setValue(editCaselist.getPoid());
			cbwoNo.setValue(editCaselist.getWoId());
			tfpartNo.setReadOnly(false);
			tfDrgNo.setReadOnly(false);
			tfpartNo.setValue(editCaselist.getPartno());
			tfDrgNo.setValue(editCaselist.getDrgno());
			tfpartNo.setReadOnly(true);
			tfDrgNo.setReadOnly(true);
			Long prodid = editCaselist.getProdid();
			Collection<?> prodids = cbprdname.getItemIds();
			for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbprdname.getItem(itemId);
				// Get the actual bean and use the data
				SmsPODtlDM st = (SmsPODtlDM) item.getBean();
				if (prodid != null && prodid.equals(st.getProductid())) {
					cbprdname.setValue(itemId);
				}
			}
			tfCaseTitle.setValue(editCaselist.getCaseTitle());
			tfEffortDays.setValue(editCaselist.getEffortDays().toString());
			if (editCaselist.getCaseDescription() != null) {
				taCaseDesc.setValue(editCaselist.getCaseDescription().toString());
			}
			logger.info("cbClient---->" + cbClient);
			cbEmployee.setValue(editCaselist.getAssignedTo());
			cbPriority.setValue(editCaselist.getCasePriority());
			cbSevrity.setValue(editCaselist.getCaseSevrity());
			cbCaseCategory.setValue(editCaselist.getCaseCategory());
			cbClntCaseStatus.setValue(editCaselist.getCaseStatus());
		}
		comment.loadsrch(true, null, null, null, null, null, clientCaseId);
		document.loadsrcrslt(true, null, null, null, null, null, clientCaseId);
	}
	
	private void loadClientsDetails() {
		try {
			clientId = serviceEnquiryHdr
					.getSmsEnqHdrList(null, (Long) cbEnquiryNo.getValue(), null, null, null, "P", null, null).get(0)
					.getClientId();
			BeanContainer<Long, ClientDM> beanClients = new BeanContainer<Long, ClientDM>(ClientDM.class);
			beanClients.setBeanIdProperty("clientId");
			beanClients.addAll(serviceClients.getClientDetails(companyId, clientId, null, null, null, null, null, null,
					"Active", "P"));
			cbClient.setContainerDataSource(beanClients);
			cbClient.setValue(clientId);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load Clients Details " + e);
		}
	}
	
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(serviceEmployee.getEmployeeList(null, null, deptId, "Active", companyId, employeeId,
					null, null, null, "P"));
			cbEmployee.setContainerDataSource(beanEmployee);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	private void loadCaseCategotyByLookUpList() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompLookUp.setBeanIdProperty("lookupname");
			beanCompLookUp.addAll(serviceCompany.getCompanyLookUpByLookUp(companyId, moduleId, "Active", "CM_CASECTG"));
			cbCaseCategory.setContainerDataSource(beanCompLookUp);
		}
		catch (Exception e) {
			logger.info("load company look up details" + e);
		}
	}
	
	private void loadCasePriorityByLookUpList() {
		BeanContainer<String, CompanyLookupDM> beanCompLookUp = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanCompLookUp.setBeanIdProperty("lookupname");
		beanCompLookUp.addAll(serviceCompany.getCompanyLookUpByLookUp(companyId, moduleId, "Active", "CM_CASEPRY"));
		cbPriority.setContainerDataSource(beanCompLookUp);
	}
	
	/*
	 * catch (Exception e) { logger.info("load company look up details" + e); }
	 */
	private void loadCaseSevrityByLookUpList() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompLookUp.setBeanIdProperty("lookupname");
			beanCompLookUp.addAll(serviceCompany.getCompanyLookUpByLookUp(companyId, moduleId, "Active", "CM_CASESEV"));
			cbSevrity.setContainerDataSource(beanCompLookUp);
		}
		catch (Exception e) {
			logger.info("load company look up details" + e);
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		tfSearchTitle.setValue("");
		cbClient.setValue(null);
		cbClntCaseStatus.setValue(cbClntCaseStatus.getItemIds().iterator().next());
		cbEmployee.setValue(null);
		hlCmdBtnLayout.setVisible(true);
		// loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		// TODO Auto-generated method stub
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent((hlUserInputLayout));
		comment = new Comments(vlCommetTblLayout, null, null, null, null, null, null, clientCaseId);
		document = new Documents(vlDocumentLayout, null, null, null, null, null, clientCaseId);
	}
	
	@Override
	protected void editDetails() {
		// TODO Auto-generated method stub
		tblMstScrSrchRslt.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		comment = new Comments(vlCommetTblLayout, null, null, null, null, null, null, clientCaseId);
		document = new Documents(vlDocumentLayout, null, null, null, null, null, clientCaseId);
		editClientCaseDetails();
	}
	
	@Override
	protected void validateDetails() throws ERPException.ValidationException {
		boolean errorflag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((tfCaseTitle.getValue() == null) || tfCaseTitle.getValue().trim().length() == 0) {
			tfCaseTitle.setComponentError(new UserError(GERPErrorCodes.NULL_CASE_TITLE));
			errorflag = true;
		}
		cbClient.setComponentError(null);
		cbEnquiryNo.setComponentError(null);
		cbwoNo.setComponentError(null);
		cbPONo.setComponentError(null);
		cbprdname.setComponentError(null);
		if ((cbClient.getValue() == null)) {
			cbClient.setComponentError(new UserError(GERPErrorCodes.NULL_CLNT_NAME));
			errorflag = true;
		}
		if ((cbEnquiryNo.getValue() == null)) {
			cbEnquiryNo.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorflag = true;
		}
		if ((cbwoNo.getValue() == null)) {
			cbwoNo.setComponentError(new UserError(GERPErrorCodes.NULL_WORK_ORDER_NO));
			errorflag = true;
		}
		if ((cbPONo.getValue() == null)) {
			cbPONo.setComponentError(new UserError(GERPErrorCodes.NULL_PURCAHSE_ORD_NO));
			errorflag = true;
		}
		if ((cbprdname.getValue() == null)) {
			cbprdname.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorflag = true;
		}
		if ((cbCaseCategory.getValue() == null)) {
			cbCaseCategory.setComponentError(new UserError(GERPErrorCodes.NULL_CASE_Category));
			errorflag = true;
		}
		if (cbEmployee.getValue() == null) {
			cbEmployee.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_FIRST_NAME));
			errorflag = true;
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + tfCaseTitle.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		try {
			ClientCasesDM ClntCaseobj = new ClientCasesDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				ClntCaseobj = beanClntCases.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			ClntCaseobj.setCaseDescription(taCaseDesc.getValue());
			if (cbEmployee.getValue() != null) {
				ClntCaseobj.setAssignedTo((Long) cbEmployee.getValue());
			} else {
				cbEmployee.setComponentError(new UserError("Select assigned to"));
			}
			if (cbCaseCategory.getValue() != null) {
				ClntCaseobj.setCaseCategory(cbCaseCategory.getValue().toString());
			}
			ClntCaseobj.setCaseDescription(taCaseDesc.getValue());
			if (cbPriority.getValue() != null) {
				ClntCaseobj.setCasePriority((String) cbPriority.getValue());
			}
			if (cbSevrity.getValue() != null) {
				ClntCaseobj.setCaseSevrity(cbSevrity.getValue().toString());
			}
			ClntCaseobj.setCaseresltn(tfCaseResult.getValue());
			if (cbClntCaseStatus.getValue() != null) {
				ClntCaseobj.setCaseStatus(cbClntCaseStatus.getValue().toString());
			}
			if (tfCaseTitle.getValue().toString().trim().length() > 0) {
				ClntCaseobj.setCaseTitle(tfCaseTitle.getValue());
			}
			if (cbClient.getValue() != null) {
				ClntCaseobj.setClientId(Long.valueOf(cbClient.getValue().toString()));
			}
			tfCaseResult.setValue(ClntCaseobj.getCaseresltn());
			if (tfEffortDays.getValue() != null && tfEffortDays.getValue().trim().length() > 0) {
				ClntCaseobj.setEffortDays(Long.valueOf(tfEffortDays.getValue()));
			}
			ClntCaseobj.setEnquiryId((Long) cbEnquiryNo.getValue());
			ClntCaseobj.setPoid((Long) cbPONo.getValue());
			ClntCaseobj.setLastUpdatedBy(userName);
			ClntCaseobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			ClntCaseobj.setProdid(((SmsPODtlDM) cbprdname.getValue()).getProductid());
			tfpartNo.setReadOnly(false);
			tfDrgNo.setReadOnly(false);
			ClntCaseobj.setPartno(tfpartNo.getValue());
			ClntCaseobj.setDrgno(tfDrgNo.getValue());
			tfpartNo.setReadOnly(true);
			tfDrgNo.setReadOnly(true);
			ClntCaseobj.setWoId((Long) cbwoNo.getValue());
			serviceCase.saveClientCasesDetails(ClntCaseobj);
			resetFields();
			System.out.println("CLIENTCASES->>" + ClntCaseobj.getClientCaseId());
			comment.saveclientcases(ClntCaseobj.getClientCaseId());
			comment.resetfields();
			document.saveclientcases(ClntCaseobj.getClientCaseId());
			document.ResetFields();
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			logger.info("saveDetails----->" + e);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for Client Case ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_CRM_CLIENT_CASES);
		UI.getCurrent().getSession().setAttribute("audittablepk", "");
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		cbClient.setRequired(false);
		cbEmployee.setRequired(false);
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
		cbEnquiryNo.setValue(null);
		cbEnquiryNo.setRequired(true);
		cbwoNo.setRequired(true);
		cbPONo.setRequired(true);
		cbprdname.setRequired(true);
		cbCaseCategory.setRequired(true);
		cbClient.setValue(null);
		cbPONo.setValue(null);
		cbEnquiryNo.setComponentError(null);
		cbwoNo.setComponentError(null);
		cbPONo.setComponentError(null);
		cbprdname.setComponentError(null);
		cbprdname.setValue(null);
		cbprdname.setContainerDataSource(null);
		cbwoNo.setValue(null);
		tfpartNo.setReadOnly(false);
		tfDrgNo.setReadOnly(false);
		tfpartNo.setValue("");
		tfDrgNo.setValue("");
		tfpartNo.setReadOnly(true);
		tfDrgNo.setReadOnly(true);
		cbClient.setContainerDataSource(null);
		cbPONo.setContainerDataSource(null);
		cbwoNo.setContainerDataSource(null);
		tfCaseResult.setValue("");
		tfCaseTitle.setValue("");
		tfCaseTitle.setComponentError(null);
		cbCaseCategory.setValue(null);
		cbCaseCategory.setComponentError(null);
		cbEmployee.setValue(null);
		cbEmployee.setComponentError(null);
		cbClient.setValue(null);
		cbClient.setComponentError(null);
		cbPriority.setValue(cbPriority.getItemIds().iterator().next());
		cbSevrity.setValue(cbSevrity.getItemIds().iterator().next());
		taCaseDesc.setValue("");
		tfEffortDays.setValue("0");
		cbClntCaseStatus.setValue(cbClntCaseStatus.getItemIds().iterator().next());
	}
	
	// Load EnquiryNo
	private void loadEnquiryNo() {
		List<SmsEnqHdrDM> getsmsEnqNoHdr = new ArrayList<SmsEnqHdrDM>();
		getsmsEnqNoHdr.addAll(serviceEnquiryHdr.getSmsEnqHdrList(companyId, null, null, null, null, "P", null, null));
		BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("enquiryId");
		beansmsenqHdr.addAll(getsmsEnqNoHdr);
		cbEnquiryNo.setContainerDataSource(beansmsenqHdr);
	}
	
	// load Workorderid
	private void loadworkorder() {
		List<WorkOrderHdrDM> getsmsEnqNoHdr = new ArrayList<WorkOrderHdrDM>();
		getsmsEnqNoHdr.addAll(serviceWrkOrdHdr.getWorkOrderHDRList(companyId, null, null, null, null, null, "F", null,
				(Long) cbEnquiryNo.getValue(),null,null));
		BeanContainer<Long, WorkOrderHdrDM> beansmsenqHdr = new BeanContainer<Long, WorkOrderHdrDM>(
				WorkOrderHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("workOrdrId");
		beansmsenqHdr.addAll(getsmsEnqNoHdr);
		cbwoNo.setContainerDataSource(beansmsenqHdr);
	}
	
	private void loadPurchaseOrdNo() {
		List<SmsPOHdrDM> getPurchseList = new ArrayList<SmsPOHdrDM>();
		getPurchseList.addAll(servicePurchaseOrdHdr.getSmspohdrList(null, null, companyId, null, null, null, null, "F",
				(Long) cbEnquiryNo.getValue()));
		BeanContainer<Long, SmsPOHdrDM> beanPurchaseOrdHdr = new BeanContainer<Long, SmsPOHdrDM>(SmsPOHdrDM.class);
		beanPurchaseOrdHdr.setBeanIdProperty("poid");
		beanPurchaseOrdHdr.addAll(getPurchseList);
		cbPONo.setContainerDataSource(beanPurchaseOrdHdr);
	}
	
	private void loadPurchseOrdDtlList() {
		List<SmsPODtlDM> getPurchaseOrdDtl = new ArrayList<SmsPODtlDM>();
		getPurchaseOrdDtl.addAll(serviceWrkOrdDtl.getPurchaseOrdDtlList((Long) cbPONo.getValue()));
		BeanItemContainer<SmsPODtlDM> beanPurchaseOrdDtl = new BeanItemContainer<SmsPODtlDM>(SmsPODtlDM.class);
		beanPurchaseOrdDtl.addAll(getPurchaseOrdDtl);
		cbprdname.setContainerDataSource(beanPurchaseOrdDtl);
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}