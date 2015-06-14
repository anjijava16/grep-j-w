package com.gnts.tools.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.tools.domain.txn.MeetingDM;
import com.gnts.tools.domain.txn.MeetingTaskActionDM;
import com.gnts.tools.domain.txn.MeetingTaskDM;
import com.gnts.tools.service.txn.MeetingService;
import com.gnts.tools.service.txn.MeetingTaskActionService;
import com.gnts.tools.service.txn.MeetingTaskService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class MeetingTask extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MeetingTaskService servicetask = (MeetingTaskService) SpringContextHelper.getBean("tToolMeetingTask");
	private MeetingTaskActionService actionServiceBean = (MeetingTaskActionService) SpringContextHelper
			.getBean("tToolMeetingTaskAction");
	private MeetingService servicemeeting = (MeetingService) SpringContextHelper.getBean("tToolMeeting");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private CompanyLookupService servicecompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private BeanItemContainer<EmployeeDM> beanEmployee = null;
	// form Layout components for ttoolmeetingtask
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private ComboBox cbStatus, cbOwner, cbPriority, cbmeeting;
	private TextField tfTaskDesc;
	private PopupDateField dfTaskDt, dfTaskEndDt, dfRevisionDt;
	private EmployeeDM selectOwner;
	private String status = "Active";
	// form Layout components for ttoolmeetingtaskaction
	private TextField tfDependancyDesc, tfActionedby, tfProgressPercentage;
	private GERPTextArea taActionDesc;
	private Table tblMeetingTaskAction;
	public Button btnSaveAction, btnCancelAction, btnEditAction, btnDownloadAction;
	// Button Declarations
	public Button btnSave, btnCancel, btnEdit, btnDownload;
	private VerticalLayout vlTableLayout;
	HorizontalLayout hlTableTitleandCaptionLayout;
	// Declaration for exporter
	// label for titles
	private Label lblFormTitle, lblTableTitle;
	public Label lblNotification, lblNotificationIcon;
	// Layouts
	VerticalLayout pnlTable = new VerticalLayout();
	VerticalLayout pnlForm = new VerticalLayout();
	private Logger logger = Logger.getLogger(MeetingTask.class);
	public HorizontalLayout hlSaveandCancelButtonLayout;
	private String strLoginUserName, strScreenName;
	private BeanItemContainer<StatusDM> beansStatus;
	private BeanItemContainer<MeetingTaskDM> beansToolMeetingTask = null;
	private BeanItemContainer<MeetingTaskActionDM> beansToolMeetingTaskAction = null;
	private Long meetingHeaderId, meetingId;
	private Long taskId, moduleId, companyid;
	private String username;
	HorizontalLayout hlScreenNameLayout, hlsearch;
	private int recordCnt = 0;
	private Boolean errorFlag = false;
	
	// Constructor received the parameters from Login UI class
	public MeetingTask() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Department() constructor");
		buildView();
	}
	
	private void buildView() {
		dfTaskDt = new PopupDateField("Task Date");
		dfTaskDt.setDateFormat("dd-MMM-yyyy");
		dfTaskEndDt = new PopupDateField("End Task");
		dfTaskEndDt.setDateFormat("dd-MMM-yyyy");
		dfRevisionDt = new PopupDateField("Revision Date");
		dfRevisionDt.setDateFormat("dd-MMM-yyyy");
		taActionDesc = new GERPTextArea("Task Description");
		cbOwner = new GERPComboBox("Owner");
		cbOwner.setItemCaptionPropertyId("firstname");
		cbOwner.setWidth("150");
		loadownerName();
		cbPriority = new GERPComboBox("Priority");
		cbPriority.setItemCaptionPropertyId("lookupname");
		cbPriority.setWidth("150");
		loadlookuplist();
		// cbStatus = new GERPComboBox("Task Status");
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setItemCaptionPropertyId("desc");
		cbStatus.setWidth("150");
		cbmeeting = new GERPComboBox("Meeting ");
		cbmeeting.setItemCaptionPropertyId("meetingAgenda");
		cbmeeting.setWidth("150");
		loadmeetinglist();
		hlsearch = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearch));
		assembleUserSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	public void assembleUserSearchLayout() {
		hlsearch.removeAllComponents();
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn1.addComponent(cbOwner);
		flcolumn2.addComponent(cbPriority);
		flcolumn3.addComponent(cbStatus);
		hlsearch.addComponent(flcolumn1);
		hlsearch.addComponent(flcolumn2);
		hlsearch.addComponent(flcolumn3);
		hlsearch.setSizeUndefined();
		hlsearch.setSpacing(true);
		hlsearch.setMargin(true);
		// hlUserInputLayout.removeAllComponents();
	}
	
	public void assembleUserInputLayout() {
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn4 = new FormLayout();
		hlUserInputLayout.removeAllComponents();
		flcolumn1.addComponent(cbOwner);
		flcolumn1.addComponent(cbPriority);
		flcolumn2.addComponent(dfTaskDt);
		flcolumn2.addComponent(dfTaskEndDt);
		flcolumn3.addComponent(cbmeeting);
		flcolumn3.addComponent(dfRevisionDt);
		flcolumn4.addComponent(taActionDesc);
		flcolumn4.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flcolumn1);
		hlUserInputLayout.addComponent(flcolumn2);
		hlUserInputLayout.addComponent(flcolumn3);
		hlUserInputLayout.addComponent(flcolumn4);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
		// hlsearch.removeAllComponents();
	}
	
	private void loadownerName() {
		/*
		 * List<EmployeeDM> employeeList = serviceEmployee.getEmployeeList(null, null, null, status, companyid, null,
		 * null, null, null, "F"); beanEmployee = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		 * beanEmployee.addAll(employeeList); beanEmployee.setBeanIdProperty("employeeid");
		 * cbOwner.setContainerDataSource(beanEmployee);
		 */
		List<EmployeeDM> employeelist = serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null,
				null, null, null, "F");
		BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmployee.setBeanIdProperty("employeeid");
		beanEmployee.addAll(employeelist);
		cbOwner.setContainerDataSource(beanEmployee);
	}
	
	// Method for show the details in grid table while search and normal mode
	private void loadSrchRslt() {
		tblMstScrSrchRslt.removeAllItems();
		List<MeetingTaskDM> usertable = new ArrayList<MeetingTaskDM>();
		usertable = servicetask.gettoolmeetingtask(null, null, null, null, "Active");
		recordCnt = usertable.size();
		beansToolMeetingTask = new BeanItemContainer<MeetingTaskDM>(MeetingTaskDM.class);
		beansToolMeetingTask.addAll(usertable);
		tblMstScrSrchRslt.setContainerDataSource(beansToolMeetingTask);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "taskid", "firstname", "priority", "taskStatus",
				"lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Owner", "Priority", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("taskid", Align.RIGHT);
		// tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records :" + recordCnt);
	}
	
	// Display the details after click the edit button
	private void editToolMeetingTask() {
		pnlForm.setVisible(true);
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			MeetingTaskDM editTToolMeetingTask = beansToolMeetingTask.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cbOwner.setValue(editTToolMeetingTask.getOwnerId());
			cbPriority.setValue(editTToolMeetingTask.getPriority());
			dfTaskDt.setValue(editTToolMeetingTask.getTaskDate());
			dfTaskEndDt.setValue(editTToolMeetingTask.getTargetEndDate());
			cbmeeting.setValue(editTToolMeetingTask.getMeetingId());
			dfRevisionDt.setValue(editTToolMeetingTask.getRevisionDate());
			taActionDesc.setValue(editTToolMeetingTask.getTaskDescription());
			cbStatus.setValue(cbStatus.getValue());
		}
	}
	
	public void loadlookuplist() {
		List<CompanyLookupDM> lookupname = servicecompany.getCompanyLookUpByLookUp(companyid, moduleId, null,
				"CM_MTNGPRY");
		BeanContainer<String, CompanyLookupDM> beanlookup = new BeanContainer<String, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanlookup.setBeanIdProperty("lookupname");
		beanlookup.addAll(lookupname);
		cbPriority.setContainerDataSource(beanlookup);
	}
	
	public void loadmeetinglist() {
		List<MeetingDM> meetinglist = servicemeeting.getToolMeetingList(companyid, null, null, null);
		BeanContainer<Long, MeetingDM> beanmeeting = new BeanContainer<Long, MeetingDM>(MeetingDM.class);
		beanmeeting.setBeanIdProperty("meetingId");
		beanmeeting.addAll(meetinglist);
		cbmeeting.setContainerDataSource(beanmeeting);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		loadSrchRslt();
		if (recordCnt == 0) {
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
			assembleUserSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbOwner.setRequired(false);
		cbPriority.setRequired(false);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbPriority.setValue(null);
		cbOwner.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		cbOwner.setRequired(true);
		cbPriority.setRequired(true);
		dfTaskDt.setRequired(true);
		dfTaskEndDt.setRequired(true);
		cbmeeting.setRequired(true);
		dfRevisionDt.setRequired(true);
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tblMstScrSrchRslt.setValue(null);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editToolMeetingTask();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbOwner.setComponentError(null);
		cbPriority.setComponentError(null);
		dfTaskDt.setComponentError(null);
		dfTaskEndDt.setComponentError(null);
		cbmeeting.setComponentError(null);
		dfRevisionDt.setComponentError(null);
		errorFlag = false;
		if (cbOwner.getValue() == null) {
			cbOwner.setComponentError(new UserError(GERPErrorCodes.NULL_GRD_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbOwner.getValue());
			errorFlag = true;
		}
		if (cbPriority.getValue() == null) {
			cbPriority.setComponentError(new UserError(GERPErrorCodes.NULL_PRIORITY));
			errorFlag = true;
		}
		if (dfTaskDt.getValue() == null) {
			dfTaskDt.setComponentError(new UserError(GERPErrorCodes.NULL_TASKDT));
			errorFlag = true;
		}
		if (dfTaskEndDt.getValue() == null) {
			dfTaskEndDt.setComponentError(new UserError(GERPErrorCodes.NULL_TASKEND));
			errorFlag = true;
		}
		if (cbmeeting.getValue() == null) {
			cbmeeting.setComponentError(new UserError(GERPErrorCodes.NULL_MEETING));
			errorFlag = true;
		}
		if (dfRevisionDt.getValue() == null) {
			dfRevisionDt.setComponentError(new UserError(GERPErrorCodes.NULL_REVISIONDT));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			MeetingTaskDM ToolMeetingObj = new MeetingTaskDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				ToolMeetingObj = beansToolMeetingTask.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			ToolMeetingObj.setOwnerId((Long) cbOwner.getValue());
			System.out.println("cbPriority====>" + cbPriority.getValue());
			ToolMeetingObj.setPriority(cbPriority.getValue().toString());
			ToolMeetingObj.setTaskDate(dfTaskDt.getValue());
			ToolMeetingObj.setTargetEndDate(dfTaskEndDt.getValue());
			ToolMeetingObj.setMeetingId((Long.valueOf(cbmeeting.getValue().toString())));
			ToolMeetingObj.setRevisionDate(dfRevisionDt.getValue());
			ToolMeetingObj.setTaskDescription(taActionDesc.getValue());
			ToolMeetingObj.setTaskStatus((String) cbStatus.getValue());
			ToolMeetingObj.setLastupdateddt(DateUtils.getcurrentdate());
			ToolMeetingObj.setLastupdatedby(username);
			logger.info(" saveOrUpdateBranch() > " + ToolMeetingObj);
			servicetask.saveAndUpdateDetails(ToolMeetingObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_BRANCH);
		UI.getCurrent().getSession().setAttribute("audittablepk", meetingId);
	}
	
	@Override
	protected void cancelDetails() {
		cbOwner.setComponentError(null);
		cbPriority.setComponentError(null);
		assembleUserSearchLayout();
		tblMstScrSrchRslt.setValue(null);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		cbOwner.setComponentError(null);
		cbPriority.setComponentError(null);
		dfTaskDt.setComponentError(null);
		dfTaskEndDt.setComponentError(null);
		cbmeeting.setComponentError(null);
		dfRevisionDt.setComponentError(null);
		cbOwner.setRequired(false);
		cbPriority.setRequired(false);
		cbOwner.setValue(null);
		cbPriority.setValue(null);
		dfTaskDt.setValue(null);
		dfTaskEndDt.setValue(null);
		cbmeeting.setValue(null);
		dfRevisionDt.setValue(null);
		taActionDesc.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
}
