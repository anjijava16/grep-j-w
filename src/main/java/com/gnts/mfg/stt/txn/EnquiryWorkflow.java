package com.gnts.mfg.stt.txn;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.crm.domain.txn.ClientsContactsDM;
import com.gnts.crm.service.txn.ClientContactsService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.tool.EmailTrigger;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.stt.mfg.domain.txn.EnquiryWorkflowDM;
import com.gnts.stt.mfg.service.txn.EnquiryWorkflowService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class EnquiryWorkflow implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HorizontalLayout hlEnquiryWorkflow = new HorizontalLayout();
	private Table tblEnquiryWorkflow = new Table();
	private GERPComboBox cbInitiatedBy = new GERPComboBox("Initiated By");
	private GERPComboBox cbwindTechPers = new GERPComboBox("Technical Person");
	private GERPComboBox cbwindcommPerson = new GERPComboBox("Commercial Person");
	private GERPComboBox cbPendingWith = new GERPComboBox("Allotted To");
	private GERPComboBox cbDesignAuthBy = new GERPComboBox("Design Authorised By");
	private GERPComboBox cbFromDept = new GERPComboBox("From");
	private GERPComboBox cbToDept = new GERPComboBox("To");
	private GERPTextArea taWorkflowRequest = new GERPTextArea("Workflow Request");
	private GERPTextField tfECRNumber = new GERPTextField("EDR Number");
	private GERPTextField tfECNNumber = new GERPTextField("ECN Number");
	private GERPTextField tfSIRNumber = new GERPTextField("SIR Number");
	private GERPComboBox cbExistingCase;
	private GERPTextField tfFusionedCase = new GERPTextField("Fusioned Case Model");
	private GERPTextField tfNewDieCase = new GERPTextField("New Die Case Model");
	private GERPTextField tfprojectName = new GERPTextField("Project Name");
	private GERPTextField tfBottomTopCase = new GERPTextField("Bottom/Top to be made");
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	// User Input Components for Sales Enquire workflow
	private Button btnAddWorkflow = new GERPButton("Add", "add", this);
	private Button btnDeleteWorkflow = new GERPButton("Delete", "delete", this);
	private Button btnPrint = new GERPButton("View Form", "downloadbt", this);
	private GERPPopupDateField dfReworkdate = new GERPPopupDateField("Allotted On");
	private GERPPopupDateField dfTargetDate = new GERPPopupDateField("Target Date");
	private GERPPopupDateField dfCompletedDate = new GERPPopupDateField("Completed On");
	private EnquiryWorkflowService serviceWorkflow = (EnquiryWorkflowService) SpringContextHelper
			.getBean("enquiryWorkflow");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private ClientContactsService serviceClntContact = (ClientContactsService) SpringContextHelper
			.getBean("clientContact");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private DepartmentService servicebeandepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	private VerticalLayout hlDocumentUpload = new VerticalLayout();
	private BeanItemContainer<EnquiryWorkflowDM> beanWorkflow;
	private String username;
	private Long enquiryId, companyid, workflowId, clientId;
	private Logger logger = Logger.getLogger(EnquiryWorkflow.class);
	
	public EnquiryWorkflow(HorizontalLayout hlEnquiryWorkflow, Long enquiryId, String username, Long clientId) {
		this.hlEnquiryWorkflow = hlEnquiryWorkflow;
		this.username = username;
		this.enquiryId = enquiryId;
		this.clientId = clientId;
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildView();
	}
	
	private void buildView() {
		new UploadDocumentUI(hlDocumentUpload);
		hlEnquiryWorkflow.removeAllComponents();
		btnDeleteWorkflow.setVisible(false);
		// TODO Auto-generated method stub
		tblEnquiryWorkflow.setWidth("1100");
		tblEnquiryWorkflow.setPageLength(8);
		tblEnquiryWorkflow.setSelectable(true);
		taWorkflowRequest.setHeight("50px");
		cbInitiatedBy.setRequired(true);
		cbPendingWith.setRequired(true);
		cbFromDept.setRequired(true);
		cbToDept.setRequired(true);
		// tfExistingCase.setNullRepresentation("---");
		cbExistingCase = new GERPComboBox("Existing Case Model");
		cbExistingCase.setReadOnly(false);
		cbExistingCase.setWidth("150");
		loadcasemodelList();
		tfFusionedCase.setNullRepresentation("---");
		tfNewDieCase.setNullRepresentation("---");
		tfBottomTopCase.setNullRepresentation("---");
		cbInitiatedBy.setItemCaptionPropertyId("firstname");
		cbPendingWith.setItemCaptionPropertyId("firstname");
		cbDesignAuthBy.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		cbFromDept.setItemCaptionPropertyId("deptname");
		cbToDept.setItemCaptionPropertyId("deptname");
		loadDepartmentList();
		hlEnquiryWorkflow.addComponent(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				setMargin(true);
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(new FormLayout() {
							private static final long serialVersionUID = 1L;
							{
								addComponent(cbFromDept);
								addComponent(cbInitiatedBy);
								addComponent(cbToDept);
								addComponent(cbPendingWith);
								addComponent(tfprojectName);
							}
						});
						addComponent(new FormLayout() {
							private static final long serialVersionUID = 1L;
							{
								addComponent(dfReworkdate);
								addComponent(dfTargetDate);
								addComponent(dfCompletedDate);
								addComponent(cbDesignAuthBy);
								addComponent(cbwindTechPers);
							}
						});
						addComponent(new FormLayout() {
							private static final long serialVersionUID = 1L;
							{
								addComponent(cbExistingCase);
								addComponent(tfFusionedCase);
								addComponent(tfNewDieCase);
								addComponent(tfBottomTopCase);
								addComponent(cbwindcommPerson);
							}
						});
						addComponent(new FormLayout() {
							private static final long serialVersionUID = 1L;
							{
								addComponent(taWorkflowRequest);
								addComponent(cbStatus);
								addComponent(new HorizontalLayout() {
									private static final long serialVersionUID = 1L;
									{
										setSpacing(true);
										addComponent(btnAddWorkflow);
										addComponent(btnPrint);
									}
								});
							}
						});
					}
				});
				addComponent(tblEnquiryWorkflow);
			}
		});
		getEnqWorkflowDetails();
		tblEnquiryWorkflow.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblEnquiryWorkflow.isSelected(event.getItemId())) {
					tblEnquiryWorkflow.setImmediate(true);
					btnDeleteWorkflow.setEnabled(false);
					resetWorkflowFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnDeleteWorkflow.setEnabled(true);
					editWorkflowDetails();
				}
			}
		});
		resetWorkflowFields();
		loadclienTecCont();
		loadclientCommCont();
	}
	
	private void getEnqWorkflowDetails() {
		try {
			logger.info("getEnqWorkflowDetails : Loading ...");
			tblEnquiryWorkflow.removeAllItems();
			List<EnquiryWorkflowDM> listWorkflow = new ArrayList<EnquiryWorkflowDM>();
			listWorkflow = serviceWorkflow.getEnqWorkflowList(null, enquiryId, null, null, null);
			beanWorkflow = new BeanItemContainer<EnquiryWorkflowDM>(EnquiryWorkflowDM.class);
			beanWorkflow.addAll(listWorkflow);
			tblEnquiryWorkflow.setContainerDataSource(beanWorkflow);
			tblEnquiryWorkflow.setVisibleColumns(new Object[] { "enqWorkflowId", "reworkOn", "fromDeptName",
					"initiatorName", "toDeptName", "pendingName", "workflowRequest", "status", "lastUpdatedDate",
					"lastUpdatedBy" });
			tblEnquiryWorkflow.setColumnHeaders(new String[] { "Ref.Id", "Date", "From", "Initiated By", "To",
					"Pending With", "Request", "Status", "Last Updated date", "Last Updated by" });
			tblEnquiryWorkflow.setColumnAlignment("enqWorkflowId", Align.RIGHT);
			tblEnquiryWorkflow.setColumnFooter("lastUpdatedBy", "No.of Records : " + listWorkflow.size());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void resetWorkflowFields() {
		cbInitiatedBy.setValue(null);
		cbPendingWith.setValue(null);
		cbFromDept.setValue(null);
		cbToDept.setValue(null);
		dfReworkdate.setValue(null);
		cbExistingCase.setValue(null);
		tfFusionedCase.setValue(null);
		tfNewDieCase.setValue(null);
		tfBottomTopCase.setValue(null);
		dfTargetDate.setValue(null);
		dfCompletedDate.setValue(null);
		cbDesignAuthBy.setValue(null);
		taWorkflowRequest.setValue("");
		tfECNNumber.setValue("");
		tfECRNumber.setValue("");
		tfSIRNumber.setValue("");
		cbStatus.setValue("Active");
		cbInitiatedBy.setComponentError(null);
		cbPendingWith.setComponentError(null);
		cbFromDept.setComponentError(null);
		cbToDept.setComponentError(null);
		cbwindcommPerson.setValue(null);
		cbwindTechPers.setValue(null);
		tfprojectName.setValue("");
		// new UploadDocumentUI(hlDocumentUpload);
	}
	
	private void saveWorkflowDetails() {
		try {
			EnquiryWorkflowDM enquiryWorkflowDM = new EnquiryWorkflowDM();
			if (tblEnquiryWorkflow.getValue() != null) {
				enquiryWorkflowDM = beanWorkflow.getItem(tblEnquiryWorkflow.getValue()).getBean();
			}
			enquiryWorkflowDM.setEnquiryId(enquiryId);
			enquiryWorkflowDM.setInitiatedBy((Long) cbInitiatedBy.getValue());
			enquiryWorkflowDM.setPendingWith((Long) cbPendingWith.getValue());
			enquiryWorkflowDM.setFromDept((Long) cbFromDept.getValue());
			enquiryWorkflowDM.setToDept((Long) cbToDept.getValue());
			enquiryWorkflowDM.setReworkOn(dfReworkdate.getValue());
			enquiryWorkflowDM.setExistingCaseModel((String) cbExistingCase.getValue());
			enquiryWorkflowDM.setFusionedCaseModel(tfFusionedCase.getValue());
			enquiryWorkflowDM.setNewDieCaseModel(tfNewDieCase.getValue());
			enquiryWorkflowDM.setBottomTopCaseModel(tfBottomTopCase.getValue());
			enquiryWorkflowDM.setTargetDate(dfTargetDate.getValue());
			enquiryWorkflowDM.setCompletedDate(dfCompletedDate.getValue());
			if (cbDesignAuthBy.getValue() != null) {
				enquiryWorkflowDM.setDesignAuthorisedBy((Long) cbDesignAuthBy.getValue());
			}
			enquiryWorkflowDM.setWorkflowRequest(taWorkflowRequest.getValue());
			if (tfECNNumber.getValue() != null && tfECNNumber.getValue().trim().length() > 0) {
				enquiryWorkflowDM.setEcnid(Long.valueOf(tfECNNumber.getValue()));
			}
			if (tfECRNumber.getValue() != null && tfECRNumber.getValue().trim().length() > 0) {
				enquiryWorkflowDM.setEcrid(Long.valueOf(tfECRNumber.getValue()));
			}
			enquiryWorkflowDM.setSirNumber(tfSIRNumber.getValue());
			enquiryWorkflowDM.setStatus((String) cbStatus.getValue());
			enquiryWorkflowDM.setDesignLocation((String) UI.getCurrent().getSession().getAttribute("uploadedFilePath"));
			enquiryWorkflowDM.setLastUpdatedDate(new Date());
			enquiryWorkflowDM.setLastUpdatedBy(username);
			enquiryWorkflowDM.setRefNo(serviceCompanyLookup
					.getCompanyLookUpByLookUp(companyid, 13L, "Active", "DES_REF_NO").get(0).getLookupname());
			enquiryWorkflowDM.setIssueNo(serviceCompanyLookup
					.getCompanyLookUpByLookUp(companyid, 13L, "Active", "ISSUE_NO").get(0).getLookupname());
			enquiryWorkflowDM.setPageRevNo(serviceCompanyLookup
					.getCompanyLookUpByLookUp(companyid, 13L, "Active", "PAGE_REV_NO").get(0).getLookupname());
			if (tfprojectName.getValue() != "") {
				enquiryWorkflowDM.setProjectName(tfprojectName.getValue());
			}
			if (cbwindcommPerson.getValue() != null) {
				enquiryWorkflowDM.setCommPerson(cbwindcommPerson.getValue().toString());
			}
			if (cbwindTechPers.getValue() != null) {
				enquiryWorkflowDM.setTechPerson(cbwindTechPers.getValue().toString());
			}
			serviceWorkflow.saveOrUpdateEnqWorkflow(enquiryWorkflowDM);
			workflowId = enquiryWorkflowDM.getEnqWorkflowId();
			String enno=enquiryWorkflowDM.getEnquiryRef();
			System.out.println("------------>"+enno);
			String messageHdr = "Reg : Enquiry WorkFlow Added";
			String messageBody = "Hi Sir/Madam ,\nEnquiry No : "
					+ enno + "\n WorkFlow Id  : " + workflowId;
			resetWorkflowFields();
			getEnqWorkflowDetails();
			try {
				new EmailTrigger(serviceCompanyLookup
						.getCompanyLookUpByLookUp(companyid, null, "Active", "SMS_MARK_MAIL").get(0).getLookupname(),
						messageBody, messageHdr);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private Boolean validateDetails() {
		cbInitiatedBy.setComponentError(null);
		cbPendingWith.setComponentError(null);
		cbFromDept.setComponentError(null);
		cbToDept.setComponentError(null);
		Boolean isvalid = true;
		if (cbInitiatedBy.getValue() == null) {
			cbInitiatedBy.setComponentError(new UserError(""));
			isvalid = false;
		}
		if (cbPendingWith.getValue() == null) {
			cbPendingWith.setComponentError(new UserError(""));
			isvalid = false;
		}
		if (cbFromDept.getValue() == null) {
			cbFromDept.setComponentError(new UserError(""));
			isvalid = false;
		}
		if (cbToDept.getValue() == null) {
			cbToDept.setComponentError(new UserError(""));
			isvalid = false;
		}
		return isvalid;
	}
	
	private void editWorkflowDetails() {
		try {
			EnquiryWorkflowDM enquiryWorkflowDM = beanWorkflow.getItem(tblEnquiryWorkflow.getValue()).getBean();
			workflowId = enquiryWorkflowDM.getEnqWorkflowId();
			cbInitiatedBy.setValue(enquiryWorkflowDM.getInitiatedBy());
			cbPendingWith.setValue(enquiryWorkflowDM.getPendingWith());
			cbFromDept.setValue(enquiryWorkflowDM.getFromDept());
			cbToDept.setValue(enquiryWorkflowDM.getToDept());
			dfReworkdate.setValue(enquiryWorkflowDM.getReworkOn());
			cbExistingCase.setValue(enquiryWorkflowDM.getExistingCaseModel());
			tfFusionedCase.setValue(enquiryWorkflowDM.getFusionedCaseModel());
			tfNewDieCase.setValue(enquiryWorkflowDM.getNewDieCaseModel());
			tfBottomTopCase.setValue(enquiryWorkflowDM.getBottomTopCaseModel());
			taWorkflowRequest.setValue(enquiryWorkflowDM.getWorkflowRequest());
			dfTargetDate.setValue(enquiryWorkflowDM.getTargetDate());
			dfCompletedDate.setValue(enquiryWorkflowDM.getCompletedDate());
			cbDesignAuthBy.setValue(enquiryWorkflowDM.getDesignAuthorisedBy());
			if (enquiryWorkflowDM.getEcnid() != null) {
				tfECNNumber.setValue(enquiryWorkflowDM.getEcnid().toString());
			}
			if (enquiryWorkflowDM.getEcrid() != null) {
				tfECRNumber.setValue(enquiryWorkflowDM.getEcrid().toString());
			}
			tfSIRNumber.setValue(enquiryWorkflowDM.getSirNumber());
			cbStatus.setValue(enquiryWorkflowDM.getStatus());
			if (enquiryWorkflowDM.getTechPerson() != null) {
				cbwindTechPers.setValue(enquiryWorkflowDM.getTechPerson().toString());
			}
			if (enquiryWorkflowDM.getCommPerson() != null) {
				cbwindcommPerson.setValue(enquiryWorkflowDM.getCommPerson().toString());
			}
			if (enquiryWorkflowDM.getProjectName() != null) {
				tfprojectName.setValue(enquiryWorkflowDM.getProjectName());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deleteWorkflowDetails() {
	}
	
	private void loadEmployeeList() {
		try {
			List<EmployeeDM> empList = serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null,
					null, null, null, "P");
			BeanContainer<Long, EmployeeDM> beanInitiatedBy = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanInitiatedBy.setBeanIdProperty("employeeid");
			beanInitiatedBy.addAll(empList);
			cbInitiatedBy.setContainerDataSource(beanInitiatedBy);
			BeanContainer<Long, EmployeeDM> beanPendingwith = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanPendingwith.setBeanIdProperty("employeeid");
			beanPendingwith.addAll(empList);
			cbPendingWith.setContainerDataSource(beanPendingwith);
			BeanContainer<Long, EmployeeDM> beanDesignAuth = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanDesignAuth.setBeanIdProperty("employeeid");
			beanDesignAuth.addAll(empList);
			cbDesignAuthBy.setContainerDataSource(beanDesignAuth);
		}
		catch (Exception e) {
			logger.info("load loadInitiatedByList details" + e);
		}
	}
	
	// Load Existing case models from Lookup
	private void loadcasemodelList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Uom Search...");
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"BS_CAMOD"));
			cbExistingCase.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadDepartmentList()-->this function is used for load the Department list
	 */
	private void loadDepartmentList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Department Search...");
			List<DepartmentDM> departmentlist = servicebeandepartmant.getDepartmentList(companyid, null, "Active", "P");
			BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
			beanDepartment.setBeanIdProperty("deptid");
			beanDepartment.addAll(departmentlist);
			cbFromDept.setContainerDataSource(beanDepartment);
			BeanContainer<Long, DepartmentDM> beanDepartment1 = new BeanContainer<Long, DepartmentDM>(
					DepartmentDM.class);
			beanDepartment1.setBeanIdProperty("deptid");
			beanDepartment1.addAll(departmentlist);
			cbToDept.setContainerDataSource(beanDepartment1);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (btnAddWorkflow == event.getButton()) {
			if (validateDetails()) {
				saveWorkflowDetails();
			}
		} else if (btnDeleteWorkflow == event.getButton()) {
			deleteWorkflowDetails();
		} else if (btnPrint == event.getButton()) {
			printDetails();
		}
	}
	
	private void printDetails() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, Long> parameterMap = new HashMap<String, Long>();
			parameterMap.put("WFID", workflowId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/pif"); // pif is the name of my jasper
			// file.
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				statement.close();
				Database.close(connection);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadclienTecCont() {
		try {
			Long clientId = serviceEnqHeader.getSmsEnqHdrList(companyid, enquiryId, null, null, null, "P", null, null)
					.get(0).getClientId();
			BeanContainer<Long, ClientsContactsDM> beanclientcontact = new BeanContainer<Long, ClientsContactsDM>(
					ClientsContactsDM.class);
			beanclientcontact.setBeanIdProperty("contactName");
			beanclientcontact.addAll(serviceClntContact.getClientContactsDetails(companyid, null, clientId, null,
					"Active", "Technical Person"));
			cbwindTechPers.setContainerDataSource(beanclientcontact);
			cbwindTechPers.setItemCaptionPropertyId("contactName");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadclientCommCont() {
		try {
			BeanContainer<Long, ClientsContactsDM> beanclientcontact = new BeanContainer<Long, ClientsContactsDM>(
					ClientsContactsDM.class);
			beanclientcontact.setBeanIdProperty("contactName");
			beanclientcontact.addAll(serviceClntContact.getClientContactsDetails(companyid, null, clientId, null,
					"Active", "Contact Person"));
			cbwindcommPerson.setContainerDataSource(beanclientcontact);
			cbwindcommPerson.setItemCaptionPropertyId("contactName");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
