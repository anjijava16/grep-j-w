package com.gnts.pms.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateUtils;
import com.gnts.pms.domain.mst.ProjectDM;
import com.gnts.pms.domain.txn.TimeSheetsDM;
import com.gnts.pms.service.mst.ProjectService;
import com.gnts.pms.service.txn.TimeSheetService;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class TimeSheetApproveUI implements ClickListener {
	private static final long serialVersionUID = 1L;
	private VerticalLayout vlSearchPanel=new VerticalLayout();
	private	VerticalLayout vlTablePanel=new VerticalLayout();
	private	VerticalLayout vlMainPanel=new VerticalLayout();
	private CheckBox chTaskVerfied,chApproveAll,chRejectAll;
	private PopupDateField dfStartdate,dfEndDate;
	private HorizontalLayout hlSaveBtn,hlAddEditBtn;
	private TextField tfActionReason;
	private ComboBox cbSearchEmployee,cbSearchStatus,cbProjectName;
	private	Button btnSearch,btnReset,btnSave,btnResetField,btnDownload;
	private	Table tblTimeSheet;
	private	Label lblNoofRecords,lblFormTitle,lblTableTitle,lblNotificationIcon,lblNotification;
	private int total = 0;
	private BeanItemContainer<TimeSheetsDM> beanTimesheet=null;
	private BeanItemContainer<EmployeeDM> beanEmployee=null;
	private BeanItemContainer<ProjectDM> beanProject=null;
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private TimeSheetService servTimeSheetBean=(TimeSheetService) SpringContextHelper.getBean("TPmsTimeSheet");
	private ProjectService servProjectsBean=(ProjectService) SpringContextHelper.getBean("MProjects");
	private ProjectDM selectedProject;
	private EmployeeDM selectedEmployee;
	private String userName,screenName;
	private VerticalLayout vlTableForm;
	private Long searchEmployeeId,loginEmployeeid,projectId,companyId;
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	private Window notifications;
	private Logger logger = Logger.getLogger(TimeSheetApproveUI.class);

	public TimeSheetApproveUI() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		companyId=Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		loginEmployeeid=Long.valueOf( UI.getCurrent().getSession().getAttribute("employeeId").toString());
		VerticalLayout clMainLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		HorizontalLayout hlScreenNameLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");

		buildMainview(clMainLayout,hlScreenNameLayout);
	}
	/*
	 * buildMainview()-->for screen UI design
	 * 
	 * @param clMainLayout, hlScreenNameLayout
	 */
    private	void buildMainview(VerticalLayout clMainLayout,HorizontalLayout hlScreenNameLayout)
	{
    	hlScreenNameLayout.removeAllComponents();
    	System.out.println("Login Employee Id-------->>>>>>>>"+loginEmployeeid);
    	int count = servicebeanEmployee.checkApprovedManager(loginEmployeeid);
    	System.out.println("login employee id is approved manager---------->>>>>>>>"+count);
    			
    	tblTimeSheet=new Table();
    	tblTimeSheet.setPageLength(8);
    	tblTimeSheet.setSizeFull();
    	tblTimeSheet.setImmediate(true);
    	tblTimeSheet.setFooterVisible(true);
		
		
		 btnSearch=new Button("Search",this);
		 btnReset=new Button("Reset",this);
		 btnSave=new Button("Save",this);
		 btnResetField=new Button("Reset",this);
		 btnDownload = new Button("Download",this);
		
		 btnSearch.setDescription("Search Time Sheet");
		btnReset.setDescription("Reset Time Sheet");
		btnSave.setDescription("Save Time Sheet");
		btnResetField.setDescription("Reset TimeSheet Field");
		btnDownload.setDescription("Download");
		btnDownload.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
		
			public void buttonClick(ClickEvent event) {
				event.getButton().removeStyleName("unread");
				if (notifications != null && notifications.getUI() != null)
					notifications.close();
				else {
					buildNotifications(event);
					UI.getCurrent().addWindow(notifications);
					notifications.focus();
					((VerticalLayout) UI.getCurrent().getContent())
							.addLayoutClickListener(new LayoutClickListener() {
								
								private static final long serialVersionUID = 1L;
								public void layoutClick(LayoutClickEvent event) {
									notifications.close();
									((VerticalLayout) UI.getCurrent().getContent())
											.removeLayoutClickListener(this);
								}
							});
				}     }
		});

		 btnSearch.addStyleName("searchbt");
		 btnReset.addStyleName("resetbt");
		 btnSave.addStyleName("savebt");
		 btnResetField.addStyleName("resetbt");
		 btnDownload.addStyleName("downloadbt");
		 
		 lblFormTitle = new Label();
		 lblFormTitle.setContentMode(ContentMode.HTML);
		 lblFormTitle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Search");
			lblTableTitle = new Label();
			lblTableTitle.setValue("<B>Action:</B>");
			lblTableTitle.setContentMode(ContentMode.HTML);
			lblNotificationIcon = new Label();
			lblNotification = new Label();
			lblNotification.setContentMode(ContentMode.HTML);
		
		 cbSearchStatus=new ComboBox("Status");
		 cbSearchStatus.setInputPrompt(Common.SELECT_PROMPT);
		 cbSearchStatus.setWidth("120");
		 cbSearchStatus.setNullSelectionAllowed(false);
		 cbSearchStatus.setImmediate(true);
		 cbSearchStatus.addItem(Common.PEND_DESC);
		 cbSearchStatus.addItem(Common.ACCEPTED_DESC);
		 cbSearchStatus.addItem(Common.REJECT_DESC);
	
	
	chTaskVerfied = new CheckBox("Task Verified");
	tfActionReason=new TextField("Reason");
	tfActionReason.setInputPrompt("Enter Reason");
	tfActionReason.setRequired(true);
	tfActionReason.setMaxLength(50);
	
	HorizontalLayout hlMainform = new HorizontalLayout();
	hlMainform.setSpacing(true);
	hlMainform.addComponent(tfActionReason);
	hlMainform.addComponent(chTaskVerfied);
	hlMainform.setComponentAlignment(chTaskVerfied, Alignment.BOTTOM_CENTER);
	
	hlSaveBtn = new HorizontalLayout();
	hlSaveBtn.addComponent(btnSave);
	hlSaveBtn.addComponent(btnResetField);
	 
	GridLayout glMainPanel = new GridLayout(1,1);
	glMainPanel.setSpacing(true);
	glMainPanel.setMargin(true);
	glMainPanel.setSizeFull();
	glMainPanel.addComponent(hlMainform);
	
	dfStartdate = new PopupDateField("Start Date");
	dfStartdate.setInputPrompt(Common.SELECT_PROMPT);
	dfStartdate.setWidth("150");
	dfEndDate  = new PopupDateField("End Date");
	dfEndDate.setInputPrompt(Common.SELECT_PROMPT);
	dfEndDate.setWidth("150");
	dfEndDate.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;
			public void blur(BlurEvent event) {
				dateValidator();
			}
	});		
	cbProjectName=new ComboBox("Project Name");
	cbProjectName.setInputPrompt(Common.SELECT_PROMPT);
	cbProjectName.setImmediate(true);
	cbProjectName.setWidth("120");
	cbProjectName.setNullSelectionAllowed(false);
	cbProjectName.setItemCaptionPropertyId("projectName");
	loadProjectList();
	cbProjectName.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				BeanItem<?> item = (BeanItem<?>) cbProjectName.getItem(itemid);
				selectedProject = (ProjectDM) item.getBean();
				projectId = selectedProject.getProjectId();
				
			}
		}
	});
	cbSearchEmployee = new ComboBox("Employee Name");
	cbSearchEmployee.setInputPrompt(Common.SELECT_PROMPT);
	cbSearchEmployee.setImmediate(true);
	cbSearchEmployee.setWidth("120");
	cbSearchEmployee.setNullSelectionAllowed(false);
	cbSearchEmployee.setItemCaptionPropertyId("firstname");
	loadSearchEmployeeList();
	cbSearchEmployee.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				BeanItem<?> item = (BeanItem<?>) cbSearchEmployee.getItem(itemid);
				selectedEmployee = (EmployeeDM) item.getBean();
			searchEmployeeId = selectedEmployee.getEmployeeid();
			}
		}
	});
	FormLayout flSearchProject=new FormLayout();
	flSearchProject.addComponent(cbProjectName);
	    FormLayout flSearchEmployee=new FormLayout();
	    flSearchEmployee.addComponent(cbSearchEmployee);
	    FormLayout flStartdate=new FormLayout();
	    flStartdate.addComponent(dfStartdate);
	    FormLayout flEnddate=new FormLayout();
	    flEnddate.addComponent(dfEndDate);
		FormLayout flSearchStatus=new FormLayout();
		flSearchStatus.addComponent(cbSearchStatus);
		
    HorizontalLayout hlSearchform=new HorizontalLayout(); 
    hlSearchform.setSpacing(true);
    hlSearchform.setMargin(true);
    hlSearchform.addComponent(flSearchProject);
    hlSearchform.addComponent(flSearchEmployee);
    hlSearchform.addComponent(flSearchStatus);
    hlSearchform.addComponent(flStartdate);
    hlSearchform.addComponent(flEnddate);
    
    VerticalLayout vlSearchBtn = new VerticalLayout();
    vlSearchBtn.setSpacing(true);
    vlSearchBtn.addComponent(btnSearch);
    vlSearchBtn.addComponent(btnReset);
    vlSearchBtn.setWidth("100");
    vlSearchBtn.addStyleName("topbarthree");
    vlSearchBtn.setMargin(true);
	
	HorizontalLayout hlSearchComponent = new HorizontalLayout();
	hlSearchComponent.setSizeFull();
	hlSearchComponent.setSpacing(true);
	hlSearchComponent.addComponent(hlSearchform);
	hlSearchComponent.setComponentAlignment(hlSearchform, Alignment.MIDDLE_LEFT);
	hlSearchComponent.addComponent(vlSearchBtn);
	hlSearchComponent.setComponentAlignment(vlSearchBtn,Alignment.MIDDLE_RIGHT);
	hlSearchComponent.setExpandRatio(vlSearchBtn, 1);
	vlSearchPanel.addComponent(PanelGenerator.createPanel(hlSearchComponent));
	vlSearchPanel.setMargin(true);
	
	chApproveAll = new CheckBox("Approve All");
	chRejectAll = new CheckBox("Reject All");
	
	HorizontalLayout hlTableTimeSheetApprove = new HorizontalLayout(); 
	hlTableTimeSheetApprove.addComponent(chApproveAll);
	hlTableTimeSheetApprove.addComponent(chRejectAll);
    
    HorizontalLayout hlFiledownload = new HorizontalLayout();
    hlFiledownload.setSpacing(true);
    hlFiledownload.addComponent(btnDownload);
    hlFiledownload.setComponentAlignment(btnDownload,Alignment.MIDDLE_CENTER);
	
    hlAddEditBtn = new HorizontalLayout();
	hlAddEditBtn.addStyleName("topbarthree");
	hlAddEditBtn.setWidth("100%");
	hlAddEditBtn.addComponent(hlTableTimeSheetApprove);
	hlAddEditBtn.addComponent(hlFiledownload);
	hlAddEditBtn.setComponentAlignment(hlFiledownload,Alignment.MIDDLE_RIGHT);
	hlAddEditBtn.setHeight("28px");
	
	 vlTableForm = new VerticalLayout();
	 vlTableForm.setSizeFull();
	 vlTableForm.setMargin(true);
	 vlTableForm.addComponent(hlAddEditBtn);
	 vlTableForm.addComponent(tblTimeSheet);
	
    lblNoofRecords=new Label(" ",ContentMode.HTML);
    lblNoofRecords.addStyleName("lblfooter");
	
	vlMainPanel.addComponent(PanelGenerator.createPanel(glMainPanel));
	vlMainPanel.setMargin(true);
	vlTablePanel.addComponent(vlTableForm);
	vlTablePanel.setMargin(false);
	
	clMainLayout.addComponent(vlSearchPanel);
	clMainLayout.addComponent(vlMainPanel);
	clMainLayout.addComponent(vlTablePanel);
	
	HorizontalLayout hlNotification = new HorizontalLayout();
	hlNotification.addComponent(lblNotificationIcon);
	hlNotification.setComponentAlignment(lblNotificationIcon,Alignment.MIDDLE_CENTER);
	hlNotification.addComponent(lblNotification);
	hlNotification.setComponentAlignment(lblNotification,Alignment.MIDDLE_CENTER);
	hlScreenNameLayout.addComponent(lblFormTitle);
	hlScreenNameLayout.setComponentAlignment(lblFormTitle, Alignment.MIDDLE_LEFT);
	hlScreenNameLayout.addComponent(hlNotification);
	hlScreenNameLayout.setComponentAlignment(hlNotification, Alignment.MIDDLE_CENTER);
	hlScreenNameLayout.addComponent(hlSaveBtn);
	hlScreenNameLayout.setComponentAlignment(hlSaveBtn, Alignment.MIDDLE_RIGHT);
	
	
    populateAndConfigureTable(false);	
    
    excelexporter.setTableToBeExported(tblTimeSheet);
	csvexporter.setTableToBeExported(tblTimeSheet);
	pdfexporter.setTableToBeExported(tblTimeSheet);
	excelexporter.setCaption("Microsoft Excel (XLS)");
	excelexporter.setStyleName("borderless");
	csvexporter.setCaption("Comma Dilimited (CSV)");
	csvexporter.setStyleName("borderless");
	pdfexporter.setCaption("Acrobat Document (PDF)");
	pdfexporter.setStyleName("borderless");
}
    /*
     * buildNotifications()-->this function is used for popupview for Download
     * components
     */
    private void buildNotifications(ClickEvent event) {
    	notifications = new Window();
    	VerticalLayout vlnotify = new VerticalLayout();
    	vlnotify.setMargin(true);
    	vlnotify.setSpacing(true);
    	notifications.setWidth("165px");
    	notifications.addStyleName("notifications");
    	notifications.setClosable(false);
    	notifications.setResizable(false);
    	notifications.setDraggable(false);
    	notifications.setPositionX(event.getClientX() - event.getRelativeX());
    	notifications.setPositionY(event.getClientY() - event.getRelativeY());
    	notifications.setCloseShortcut(KeyCode.ESCAPE, null);

    	VerticalLayout vlDownload = new VerticalLayout();
    	vlDownload.addComponent(excelexporter);
    	vlDownload.addComponent(csvexporter);
    	vlDownload.addComponent(pdfexporter);
    	vlDownload.setSpacing(true);

    	notifications.setContent(vlDownload);

    }   
    /*
     * populateAndConfigureTable()-->this function used to load the list to the table.
     * 
     * @param boolean search if(search==true)--> it performs search operation
     * else it loads all values
     */
private void populateAndConfigureTable(boolean search) {
	try {
		tblTimeSheet.removeAllItems();
    	 List<TimeSheetsDM> timesheetList=new ArrayList<TimeSheetsDM>();
    	if(search)
    	{
    		timesheetList=new ArrayList<TimeSheetsDM>();
    		String statusArg=null;
    		try {
    			statusArg = cbSearchStatus.getValue().toString();
			} catch (Exception e) {
				logger.info("status is empty on search");
			}
    		Date startdate = dfStartdate.getValue();
    		Date enddate = dfEndDate.getValue();
    		if (statusArg!=null || searchEmployeeId!=null || projectId!=null || startdate!=null || enddate!=null) {
    			timesheetList =servTimeSheetBean.getProjectTimeSheetList(null,projectId,searchEmployeeId, statusArg,startdate,enddate,companyId);
			total = timesheetList.size();
			}
			if (total == 0) {
				Notification.show("No Records Found");
			}
			 
		}
    	else {
    		timesheetList = servTimeSheetBean.getProjectTimeSheetList(null,null,null,Common.PEND_DESC,null,null,companyId);
			total = timesheetList.size();
		}
    	lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
    	 beanTimesheet=new BeanItemContainer<TimeSheetsDM>(TimeSheetsDM.class);
    	 beanTimesheet.addAll(timesheetList);
    	tblTimeSheet.setContainerDataSource(beanTimesheet);
    	tblTimeSheet.setSelectable(true);
    	tblTimeSheet.setColumnFooter("lastUpdatedBy","No. of Records:"+total);
    	tblTimeSheet.setVisibleColumns(new Object[] {"projectTimeSheetId","timeSheetDate","effort","status","lastUpdtDate","lastUpdatedBy"});
    	tblTimeSheet.setColumnHeaders(new String[] {"Ref.Id","Time Sheet Date","Effort","Status","Last Updated Date","Last Updated By"});	 
    	
    	tblTimeSheet.setEditable(true);
    	tblTimeSheet.setTableFieldFactory(new TableFieldFactory() {
			public Field createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				if (propertyId.toString().equals("status")) {
					ComboBox approvStatus = new ComboBox();
					approvStatus.addItem(Common.ACCEPTED_DESC);
					approvStatus.addItem(Common.PEND_DESC);
					approvStatus.addItem(Common.REJECT_DESC);
					approvStatus.setWidth("130");
					approvStatus.setNullSelectionAllowed(false);
					return approvStatus;
				}

				return null;
			}
		});
	} catch(Exception e) {
		logger.error("error during populate values on the table, The Error is ----->"+e);
	}
    }

/*
 * resetFields()->this function is used for reset the UI components
 */
     private void resetFields() {
    	 tfActionReason.setValue("");
    	 chTaskVerfied.setValue(false);
    	 chApproveAll.setValue(false);
    	 chRejectAll.setValue(false);
    	 btnSave.setComponentError(null);
    	 tfActionReason.setComponentError(null);
    	btnSave.setCaption("Save"); 
    	lblFormTitle.setValue("&nbsp;&nbsp;<b>"+ screenName+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Search</font> &nbsp;>&nbsp;Add New");
    	lblNotificationIcon.setIcon(null);
    	lblNotification.setValue("");
     }
     
    
    
      
      /*
    	 * 
    	 * this function handles load Employee list to Search Employee name component.
    	 * 
    	 */
        private void loadSearchEmployeeList() {
       	 try {
       	 List <EmployeeDM> getEmployeelist = servicebeanEmployee.getEmployeeList(null,null,null,Common.ACTIVE_DESC,null,null,null,null);
       	 beanEmployee = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
       	beanEmployee.addAll(getEmployeelist);
       	 cbSearchEmployee.setContainerDataSource(beanEmployee);
       	 } catch(Exception e) {
       		 logger.warn("Loaing null values in loadEmployeeList() function----->>>>>>>"+e);
       	 }
        }
        private void loadProjectList() {
       	 try {
       	 List <ProjectDM> getProjectlist = servProjectsBean.getProjectList(null,null,null,Common.ACTIVE_DESC);
       	  beanProject = new BeanItemContainer<ProjectDM>(ProjectDM.class);
       	  beanProject.addAll(getProjectlist);
       	  cbProjectName.setContainerDataSource(beanProject);
       	 } catch(Exception e) {
       		 logger.warn("Loading null values in loadProjectList() function------->>>>>>>"+e);
       	 }
        }
     /*
 	 * saveDetails()-->this function is used for save/update the records
 	 */	
 private  void saveTimeSheetApproveDetails() {
	 btnSave.setComponentError(null);
	 int countApprove = servicebeanEmployee.checkApprovedManager(loginEmployeeid);
	 @SuppressWarnings("unchecked")
	Collection<TimeSheetsDM> itemIds = (Collection<TimeSheetsDM>) tblTimeSheet.getVisibleItemIds();

		for (TimeSheetsDM updateTimeSheet : (Collection<TimeSheetsDM>) itemIds) {
			
			TimeSheetsDM updateEntry=new TimeSheetsDM();
    		 updateEntry.setProjectTimeSheetId(updateTimeSheet.getProjectTimeSheetId());
    		 updateEntry.setEmployeeid(updateTimeSheet.getEmployeeid());
    		 updateEntry.setEffort(updateTimeSheet.getEffort());
    		 updateEntry.setProjectId(updateTimeSheet.getProjectId());
    		 updateEntry.setProjectPhaseId(updateTimeSheet.getProjectPhaseId());
    		 updateEntry.setProjectTeamId(updateTimeSheet.getProjectTeamId());
    		 updateEntry.setApproveManagerId(updateTimeSheet.getApproveManagerId());
    		 updateEntry.setApprovedMGRId(loginEmployeeid);
    		 updateEntry.setTaskDesc(updateTimeSheet.getTaskDesc());
    		 updateEntry.setReason(tfActionReason.getValue().toString());
    		 updateEntry.setTimeSheetDate(updateTimeSheet.getTimeSheetDate());
    		 if(chTaskVerfied.getValue()!=null) {
    			 if(chTaskVerfied.getValue().equals(true)) {
    				 updateEntry.setTaskVerified("Y");
    			 }else {
    				 updateEntry.setTaskVerified("N");
    			 } }
    		 else {
    			 updateEntry.setTaskVerified("N");
    		 }
    		 updateEntry.setApproveDate(DateUtils.getcurrentdate());
    		 
    		   if(chApproveAll.getValue().equals(true)) {
    			 updateEntry.setStatus(Common.ACCEPTED_DESC);
    		    }
    		   else if(chRejectAll.getValue().equals(true)) {
    			 updateEntry.setStatus(Common.REJECT_DESC); 
    			 }
    		else {
    		 updateEntry.setStatus(updateTimeSheet.getStatus());
    		 }
    		 updateEntry.setLastUpdatedBy(userName);
    		 updateEntry.setLastUpdtDate(DateUtils.getcurrentdate());
    		 if(countApprove > 0 && tfActionReason.isValid() ) {
    			 System.out.println("count value of approve managers--------------->>>>>>>>>"+countApprove);
    			 servTimeSheetBean.saveOrUpdateProjectTimeSheet(updateEntry);
    			 populateAndConfigureTable(false);
    			 btnSave.setCaption("Save");
    			 
    		 }
		}
		if(countApprove > 0 && tfActionReason.isValid()) {
			System.out.println("valid valid valid valid------->>>>>>>");
			 populateAndConfigureTable(false);
			 resetFields();
			 lblNotification.setValue("Successfully Saved");
			 lblNotificationIcon.setIcon(new ThemeResource("img/success_small.png"));
		}
		else {
			System.out.println("Not valid Not valid Not valid------------>>>>>>>>>>>");
			 btnSave.setComponentError(new UserError("Form is not valid"));
			 lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
			 lblNotification.setValue("Time sheet did not Approve successfully ");
		}
     }
  private void setComponentError() {
	  tfActionReason.setComponentError(null);
		if( tfActionReason.getValue()==null || tfActionReason.getValue().toString().trim().length()==0)
		{
			tfActionReason.setComponentError(new UserError("Enter Action Reason "));
		}		
        }
  private void dateValidator()
  {
	   final Date startdt = (Date) dfStartdate.getValue();
		final Date enddt = (Date) dfEndDate.getValue();
		if(startdt.before(enddt) || startdt.equals(enddt))
		{
			dfEndDate.setComponentError(null);
			btnSearch.setEnabled(true);
		}
		else{
			dfEndDate.setComponentError(new UserError("Enter end date after the start date value"));
			btnSearch.setEnabled(false);
		}
  }
   
    /*
	 * 
	 * this function handles button click event
	 * 
	 * @param ClickEvent event
	 */
    @Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
		 if(btnSave==event.getButton()) {
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(true);
			vlTablePanel.setVisible(true);
			hlSaveBtn.setVisible(true);
			try {
				setComponentError();
				saveTimeSheetApproveDetails();
			} catch(Exception e) {
				e.printStackTrace();
				logger.info("check  saveTimeSheetEntryDetails() function. Time Sheet datas did not saved properly--->" + e);
			}
		
		}
		else if(btnResetField==event.getButton()) {
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(true);
			vlTablePanel.setVisible(true);
			populateAndConfigureTable(false);
			 resetFields();
			
		btnSave.setCaption("Save");
		lblFormTitle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Search");
		hlSaveBtn.setVisible(true);
		}
		else if(btnSearch==event.getButton()) {
			populateAndConfigureTable(true);
			 lblFormTitle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Search");
		}
		else if(btnReset==event.getButton()) {		
			cbSearchStatus.setValue(null);
			cbSearchEmployee.setValue(null);
			cbProjectName.setValue(null);
			dfStartdate.setValue(null);
			dfEndDate.setValue(null);
			searchEmployeeId = null;
			projectId = null;
			populateAndConfigureTable(false);
			 resetFields();
		
		}
		
		
	}
						

}
