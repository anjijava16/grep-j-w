package com.gnts.tools.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.tools.domain.txn.MeetingDM;
import com.gnts.tools.domain.txn.MeetingTaskActionDM;
import com.gnts.tools.domain.txn.MeetingTaskDM;
import com.gnts.tools.service.txn.MeetingTaskActionService;
import com.gnts.tools.service.txn.MeetingTaskService;
import com.google.gwt.user.client.ui.TextArea;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class MeetingTaskUI extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MeetingTaskService servbean = (MeetingTaskService) SpringContextHelper.getBean("tToolMeetingTask");
	private MeetingTaskActionService actionServiceBean = (MeetingTaskActionService) SpringContextHelper
			.getBean("tToolMeetingTaskAction");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private BeanItemContainer<EmployeeDM> beanEmployee = null;
	// form Layout components for ttoolmeetingtask
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4;
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
	private Table tblToolMeetingTask;
	// Layouts
	VerticalLayout pnlTable = new VerticalLayout();
	VerticalLayout pnlForm = new VerticalLayout();
	private Logger logger = Logger.getLogger(MeetingTaskUI.class);
	public HorizontalLayout hlSaveandCancelButtonLayout;
	private String strLoginUserName, strScreenName;
	private BeanItemContainer<StatusDM> beansStatus;
	private BeanItemContainer<MeetingTaskDM> beansToolMeetingTask = null;
	private BeanItemContainer<MeetingTaskActionDM> beansToolMeetingTaskAction = null;
	private Long meetingHeaderId;
	private Long taskId;
	HorizontalLayout hlScreenNameLayout;
	
	// Constructor received the parameters from Login UI class
	public MeetingTaskUI() {
		strLoginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		strScreenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		VerticalLayout clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlScreenNameLayout = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView();
	}
	
	private void buildView() {
		dfTaskDt = new GERPPopupDateField("Task Date");
		dfTaskEndDt = new GERPPopupDateField("End Task");
		dfRevisionDt = new GERPPopupDateField("Revision Date");
		taActionDesc = new GERPTextArea("Task Description");
		cbOwner = new GERPComboBox("Owner");
		cbPriority = new GERPComboBox("Priority");
		cbStatus = new GERPComboBox("Task Status");
		cbmeeting = new GERPComboBox("Meeting ");
	}
	
	public void assembleUserSearchLayout() {
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn4 = new FormLayout();
		flcolumn1.addComponent(cbOwner);
		flcolumn2.addComponent(cbPriority);
		flcolumn3.addComponent(cbStatus);
		hlScreenNameLayout.addComponent(flcolumn1);
		hlScreenNameLayout.addComponent(flcolumn2);
		hlScreenNameLayout.addComponent(flcolumn3);
		hlScreenNameLayout.setSpacing(true);
		hlScreenNameLayout.setSizeUndefined();
	}
	
	public void assembleUserInputLayout() {
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn3 = new FormLayout();
		flcolumn1.addComponent(cbOwner);
		flcolumn1.addComponent(cbPriority);
		flcolumn2.addComponent(dfTaskDt);
		flcolumn2.addComponent(dfTaskEndDt);
		flcolumn2.addComponent(cbmeeting);
		flcolumn3.addComponent(dfRevisionDt);
		flcolumn3.addComponent(taActionDesc);
		flcolumn3.addComponent(cbStatus);
	}
	
	private void loadChairedName() {
		List<EmployeeDM> employeeList = servicebeanEmployee.getEmployeeList(null, null, null, "Active", null, null, null,
				null, null,"F");
		beanEmployee = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployee.addAll(employeeList);
		cbOwner.setContainerDataSource(beanEmployee);
	}
	
	// Method for show the details in grid table while search and normal mode
	private void loadSrchRslt() {
		// try{
		tblToolMeetingTask.removeAllItems();
		List<MeetingTaskDM> usertable = new ArrayList<MeetingTaskDM>();
		beansToolMeetingTask = new BeanItemContainer<MeetingTaskDM>(MeetingTaskDM.class);
		beansToolMeetingTask.addAll(usertable);
		tblToolMeetingTask.setContainerDataSource(beansToolMeetingTask);
		tblToolMeetingTask.setVisibleColumns(new Object[] { "taskid", "taskDate", "taskDescription", "employeeName",
				"taskStatus", "lastupdateddt", "lastupdatedby" });
		tblToolMeetingTask.setColumnHeaders(new String[] { "Ref.Id", "Task Date", "Task Description", "Owner",
				"Status", "Last Updated Date", "Last Updated By" });
		tblToolMeetingTask.setSelectable(true);
		tblToolMeetingTask.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				if (tblToolMeetingTask.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
				} else {
					btnEdit.setEnabled(true);
				}
				resetFields();
				btnSave.setCaption("Save Meeting Task");
			}
		});
		/*
		 * }catch(Exception e){ logger.error( "error during populate values on the table, The Error is ----->"+e); }
		 */
	}
	
	// Method for show the details in grid table while search and normal mode
	private void loadSrchrslt() {
		// try{
		tblMeetingTaskAction.removeAllItems();
		List<MeetingTaskActionDM> usertable = new ArrayList<MeetingTaskActionDM>();
		usertable = actionServiceBean.getTToolMeetingTaskActionList(null, null);
		beansToolMeetingTaskAction = new BeanItemContainer<MeetingTaskActionDM>(MeetingTaskActionDM.class);
		beansToolMeetingTaskAction.addAll(usertable);
		tblMeetingTaskAction.setContainerDataSource(beansToolMeetingTaskAction);
		tblMeetingTaskAction.setVisibleColumns(new Object[] { "taskActionId", "actionDesc", "dependancyDesc",
				"progressPercent", "actionedBy", "lastupdateddt", "lastupdatedby" });
		tblMeetingTaskAction.setColumnHeaders(new String[] { "Ref.Id", "Action Desc.", "Dependancy Desc.",
				"Progress Percent", "Actioned By", "Last Updated Date", "Last Updated By" });
		tblMeetingTaskAction.setSelectable(true);
	}
	
	// Display the details after click the edit button
	private void editToolMeetingTask() {
		pnlForm.setVisible(true);
		Item itselect = tblToolMeetingTask.getItem(tblToolMeetingTask.getValue());
		if (itselect != null) {
			MeetingTaskDM editTToolMeetingTask = beansToolMeetingTask.getItem(tblToolMeetingTask.getValue()).getBean();
		}
	}
	
	// Display the details after click the edit button
	private void editToolMeetingTaskAction() {
		Item itselect = tblMeetingTaskAction.getItem(tblMeetingTaskAction.getValue());
		if (itselect != null) {
			tfDependancyDesc.setValue(itselect.getItemProperty("dependancyDesc").getValue().toString());
			tfProgressPercentage.setValue(itselect.getItemProperty("progressPercent").getValue().toString());
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void resetSearchDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void addDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void editDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
	}
}
