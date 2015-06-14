package com.gnts.pms.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.ParameterService;
import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateUtils;
import com.gnts.pms.domain.mst.ProjectDM;
import com.gnts.pms.domain.mst.ProjectPhaseDM;
import com.gnts.pms.domain.mst.ProjectTeamDM;
import com.gnts.pms.domain.txn.TimeSheetsDM;
import com.gnts.pms.service.mst.ProjectPhaseService;
import com.gnts.pms.service.mst.ProjectService;
import com.gnts.pms.service.mst.ProjectTeamService;
import com.gnts.pms.service.txn.TimeSheetService;
import com.gnts.pms.serviceimpl.mst.ProjectsServiceImpl;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class TimeSheetEntryUI implements ClickListener {

	private static final long serialVersionUID = 1L;
	private VerticalLayout vlSearchPanel=new VerticalLayout();
	private	VerticalLayout vlTablePanel=new VerticalLayout();
	private	VerticalLayout vlMainPanel=new VerticalLayout();
	
	private FormLayout flForm1,flForm2,flForm3;
	private PopupDateField dfTimesheet;
	private HorizontalLayout hlSaveBtn,hlAddEditBtn;
	private TextArea taTaskDesc;
	private TextField tfEffort;
	private ComboBox cbProjectName,cbProjectTeam,cbProjectPhaseName;
	private	Button btnSave,btnReset,btnDownload;
	private	Table tblTimeSheet;
	private	Label lblNoofRecords,lblFormTitle,lblTableTitle,lblNotificationIcon,lblNotification;
	private int total = 0;
	private Long defaultvalue;
	private BeanItemContainer<TimeSheetsDM> beanTimesheet=null;
	private BeanItemContainer<ProjectDM> beanProject=null;
	private BeanItemContainer<ProjectPhaseDM> beanProjectPhase=null;
	private BeanItemContainer<ProjectTeamDM> beanProjectTeam=null;
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private ProjectPhaseService servProjPhaseBean=(ProjectPhaseService) SpringContextHelper.getBean("MProjectPhase");
	private TimeSheetService servTimeSheetBean=(TimeSheetService) SpringContextHelper.getBean("TPmsTimeSheet");
	private ProjectTeamService servProjTeamBean = (ProjectTeamService) SpringContextHelper.getBean("MProjectTeam");
	private ProjectService servProjectsBean=(ProjectService) SpringContextHelper.getBean("MProjects");
	private ParameterService servParameterbean = (ParameterService) SpringContextHelper.getBean("parameter");
	private ProjectDM selectedProject;
	private ProjectPhaseDM selectedProjPhase;
	private ProjectTeamDM selectedProjTeam;
	private String userName,screenName;
	private VerticalLayout vlTableForm;
	private Long projectId,projectPhaseId,projectTeamId,loginEmployeeid,approveManagerId,companyId;
	private String timeSheetId;
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	private Window notifications;
	private Logger logger = Logger.getLogger(TimeSheetEntryUI.class);

	public TimeSheetEntryUI() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		loginEmployeeid=Long.valueOf( UI.getCurrent().getSession().getAttribute("employeeId").toString());
		companyId=Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
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
    	

    	tblTimeSheet=new Table();
    	tblTimeSheet.setPageLength(10);
    	tblTimeSheet.setSizeFull();
    	tblTimeSheet.setImmediate(true);
    	tblTimeSheet.setFooterVisible(true);
	
		 btnSave=new Button("Save",this);
		 btnReset=new Button("Reset",this);
		 btnDownload = new Button("Download",this);
		
		btnSave.setDescription("Save TimeSheet Entry");
		btnReset.setDescription("Reset TimeSheet Entry");
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

		 btnSave.addStyleName("savebt");
		 btnReset.addStyleName("resetbt");
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
		
		
	
	tfEffort = new TextField("Effort");
	tfEffort.setValue("0");
	dfTimesheet = new PopupDateField("TimeSheet Date");
	dfTimesheet.setInputPrompt(Common.SELECT_PROMPT);
	dfTimesheet.setRequired(true);
	dfTimesheet.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;

			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				dateValidate();	
			} 
    });
	taTaskDesc  = new TextArea("Task Description");
	taTaskDesc.setInputPrompt("Enter Task Description");
	taTaskDesc.setRequired(true);
	taTaskDesc.setMaxLength(100);
	taTaskDesc.setHeight("80");
	taTaskDesc.setWidth("200");
	
	
	
	cbProjectName=new ComboBox("Project Name");
	cbProjectName.setInputPrompt(Common.SELECT_PROMPT);
	cbProjectName.setRequired(true);
	cbProjectName.setImmediate(true);
	cbProjectName.setWidth("150");
	cbProjectName.setNullSelectionAllowed(false);
	cbProjectName.setItemCaptionPropertyId("projectName");
	loadProjectList();
	cbProjectName.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				projectPhaseId = null;
				projectTeamId = null;
				BeanItem<?> item = (BeanItem<?>) cbProjectName.getItem(itemid);
				selectedProject = (ProjectDM) item.getBean();
				projectId = selectedProject.getProjectId();
				
				loadProjectPhaseList(projectId);
			}
		}
	});
	
	
	 
	cbProjectPhaseName=new ComboBox("Project Phase");
	cbProjectPhaseName.setInputPrompt(Common.SELECT_PROMPT);
	cbProjectPhaseName.setImmediate(true);
	cbProjectPhaseName.setWidth("150");
	cbProjectPhaseName.setNullSelectionAllowed(false);
	cbProjectPhaseName.setItemCaptionPropertyId("projectPhase");
	cbProjectPhaseName.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			// TODO Auto-generated method stub
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				projectTeamId = null;
				BeanItem<?> item = (BeanItem<?>) cbProjectPhaseName.getItem(itemid);
				selectedProjPhase = (ProjectPhaseDM) item.getBean();
				projectPhaseId = selectedProjPhase.getProjectPhaseId();
				
				loadProjectTeamList(projectPhaseId);
			}
		}
	});
	cbProjectTeam=new ComboBox("Project Role");
	cbProjectTeam.setInputPrompt(Common.SELECT_PROMPT);
	cbProjectTeam.setImmediate(true);
	cbProjectTeam.setNullSelectionAllowed(false);
	cbProjectTeam.setWidth("150");
	cbProjectTeam.setItemCaptionPropertyId("roleDesc");
	cbProjectTeam.addValueChangeListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;
		public void valueChange(ValueChangeEvent event) {
			// TODO Auto-generated method stub
			Object itemid = event.getProperty().getValue();
			if (itemid != null) {
				BeanItem<?> item = (BeanItem<?>) cbProjectTeam.getItem(itemid);
				selectedProjTeam= (ProjectTeamDM) item.getBean();
				projectTeamId = selectedProjTeam.getProjectTeamId();
			}
		}
	});
	
	defaultvalue = servParameterbean.getListBasedParameter(loginEmployeeid+"", companyId);
	
	if(defaultvalue != 0) {
		
		List<TimeSheetsDM> tsList = servTimeSheetBean.getProjectTimeSheetList(defaultvalue,null,null,null,null, null,companyId);
		for(TimeSheetsDM getTS : tsList) {
			
			dfTimesheet.setValue(getTS.getTimeSheetDate());
			tfEffort.setValue(getTS.getEffort().toString());
			taTaskDesc.setValue(getTS.getTaskDesc());
			
			ProjectDM editGetProject = getTS.getProjectId();
			Collection<?> coll = cbProjectName.getItemIds();
			for (Iterator<?> iterator = coll.iterator(); iterator.hasNext();) {
				Object itemid = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProjectName.getItem(itemid);
				ProjectDM editProjectBean = (ProjectDM) item.getBean();
				if (editGetProject != null && editGetProject.getProjectId().equals(editProjectBean.getProjectId())) {
					cbProjectName.setValue(itemid);
					break;
				} else {
					cbProjectName.setValue(null);
				}
			}
			
			ProjectPhaseDM editGetProjectPhase = getTS.getProjectPhaseId();
			Collection<?> collPhase = cbProjectPhaseName.getItemIds();
			for (Iterator<?> iterator = collPhase.iterator(); iterator.hasNext();) {
				Object itemid = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProjectPhaseName.getItem(itemid);
				ProjectPhaseDM editProjectPhaseBean = (ProjectPhaseDM) item.getBean();
				if (editGetProjectPhase != null && editGetProjectPhase.getProjectPhaseId().equals(editProjectPhaseBean.getProjectPhaseId())) {
					cbProjectPhaseName.setValue(itemid);
					break;
				} else {
					cbProjectPhaseName.setValue(null);
				}
			}
			
			ProjectTeamDM editGetProjectTeam = getTS.getProjectTeamId();
			Collection<?> collTeam = cbProjectTeam.getItemIds();
			for (Iterator<?> iterator = collTeam.iterator(); iterator.hasNext();) {
				Object itemid = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProjectTeam.getItem(itemid);
				ProjectTeamDM editProjectTeamBean = (ProjectTeamDM) item.getBean();
				if (editGetProjectTeam != null && editGetProjectTeam.getProjectTeamId().equals(editProjectTeamBean.getProjectTeamId())) {
					cbProjectTeam.setValue(itemid);
					break;
				} else {
					cbProjectTeam.setValue(null);
				}
			}
		}
	}
	
	flForm1=new FormLayout();
	flForm1.setSpacing(true);
	flForm1.addComponent(cbProjectName);
	flForm1.addComponent(cbProjectPhaseName);
	flForm1.addComponent(cbProjectTeam);
	flForm2=new FormLayout();
	flForm2.setSpacing(true);
	flForm2.addComponent(dfTimesheet);
	flForm2.addComponent(tfEffort);
	flForm3=new FormLayout();
	flForm3.setSpacing(true);
	flForm3.addComponent(taTaskDesc);
	

	
	
	HorizontalLayout hlMainform = new HorizontalLayout();
	hlMainform.setSpacing(true);
	hlMainform.addComponent(flForm1);
	hlMainform.addComponent(flForm2);
	hlMainform.addComponent(flForm3);
	
	hlSaveBtn = new HorizontalLayout();
	hlSaveBtn.addComponent(btnSave);
	hlSaveBtn.addComponent(btnReset);
	 
	GridLayout glMainPanel = new GridLayout(1,1);
	glMainPanel.setSpacing(true);
	glMainPanel.setMargin(true);
	glMainPanel.setSizeFull();
	glMainPanel.addComponent(hlMainform);
    
    HorizontalLayout hlFiledownload = new HorizontalLayout();
    hlFiledownload.setSpacing(true);
    hlFiledownload.addComponent(btnDownload);
    hlFiledownload.setComponentAlignment(btnDownload,Alignment.MIDDLE_CENTER);
	
    hlAddEditBtn = new HorizontalLayout();
	hlAddEditBtn.addStyleName("topbarthree");
	hlAddEditBtn.setWidth("100%");
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
	
    populateAndConfigureTable();	
    
    System.out.println("Login Employee Id-------->>>>>>>>"+loginEmployeeid);
	approveManagerId = servicebeanEmployee.getApproveManager(loginEmployeeid);
	System.out.println("Approve Manager Id of Login Employee-------->>>>>>>>"+approveManagerId);
    
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
private void populateAndConfigureTable() {
	try {
		tblTimeSheet.removeAllItems();
    	 List<TimeSheetsDM> timesheetList=new ArrayList<TimeSheetsDM>();
    	
    		timesheetList = servTimeSheetBean.getProjectTimeSheetList(null,null,loginEmployeeid,null,null,null,companyId);
			total = timesheetList.size();
	
    	lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
    	 beanTimesheet=new BeanItemContainer<TimeSheetsDM>(TimeSheetsDM.class);
    	 beanTimesheet.addAll(timesheetList);
    	tblTimeSheet.setContainerDataSource(beanTimesheet);
    	tblTimeSheet.setSelectable(false);
    	tblTimeSheet.setColumnFooter("lastUpdatedBy","No. of Records:"+total);
    	tblTimeSheet.setVisibleColumns(new Object[] {"projectTimeSheetId","timeSheetDate","effort","status","lastUpdtDate","lastUpdatedBy"});
    	tblTimeSheet.setColumnHeaders(new String[] {"Ref.Id","Time Sheet Date","Effort","Status","Last Updated Date","Last Updated By"});	 
    	
	} catch(Exception e) {
		logger.error("error during populate values on the table, The Error is ----->"+e);
	}
    }
private void dateValidate()
{
	Date timeshetDate = dfTimesheet.getValue();
	
	int count = servTimeSheetBean.getTimesheetDateValidate(timeshetDate,loginEmployeeid,companyId);
		
		if(count!=0)
		{
			dfTimesheet.setComponentError(new UserError("This Date Already Exists"));
		}
		else{
			dfTimesheet.setComponentError(null);
		}
		
}
/*
 * resetFields()->this function is used for reset the UI components
 */
     private void resetFields() {
    	 tfEffort.setValue("0"); 
    	 dfTimesheet.setValue(null);
    	 dfTimesheet.setComponentError(null);
    	 cbProjectPhaseName.setValue(null);
    	 cbProjectTeam.setValue(null);
    	 taTaskDesc.setValue("");
    	 taTaskDesc.setComponentError(null);
    	 cbProjectName.setValue(null);
    	 cbProjectName.setComponentError(null);
    	 dfTimesheet.setComponentError(null);
    	 btnSave.setComponentError(null);
    	btnSave.setCaption("Save"); 
    	lblFormTitle.setValue("&nbsp;&nbsp;<b>"+ screenName+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Search</font> &nbsp;>&nbsp;Add New");
    	lblNotificationIcon.setIcon(null);
    	lblNotification.setValue("");
     }
    
     /*
 	 * this function handles load country list to country name component.
 	 */
     private void loadProjectList() {
    	 try {
    	 List <ProjectDM> getProjectlist = servProjectsBean.getProjectList(null,null,loginEmployeeid,Common.ACTIVE_DESC);
    	  beanProject = new BeanItemContainer<ProjectDM>(ProjectDM.class);
    	  beanProject.addAll(getProjectlist);
    	  cbProjectName.setContainerDataSource(beanProject);
    	 } catch(Exception e) {
    		 e.printStackTrace();
    		 logger.warn("Loading null values in loadProjectList() function------->>>>>>>"+e);
    	 }
     }
     /*
 	 * 
 	 * this function handles load state list to state name component.
 	 * 
 	 */
     private void loadProjectPhaseList(Long projecId) {
    	 try {
    	 List <ProjectPhaseDM> getProjectPhaselist=servProjPhaseBean.getProjectPhaseList(null,Common.ACTIVE_DESC,projecId);
    	 beanProjectPhase = new  BeanItemContainer<ProjectPhaseDM>(ProjectPhaseDM.class);
    	 beanProjectPhase.addAll(getProjectPhaselist);
    	 cbProjectPhaseName.setContainerDataSource(beanProjectPhase);
    	 } catch(Exception e) {
    		 logger.warn("Loading null values in loadStateList() function -------->>>>>>"+e);
    	 }
     }
     /*
 	 * 
 	 * this function handles load city list to city name component.
 	 * 
 	 */
     private void loadProjectTeamList(Long projecctphaseid) {
    	 try {
    	 List <ProjectTeamDM> getProjectTeamlist = servProjTeamBean.getProjectTeamList(null,projecctphaseid);
    	 beanProjectTeam = new BeanItemContainer<ProjectTeamDM>(ProjectTeamDM.class);
    	 beanProjectTeam.addAll(getProjectTeamlist);
    	 cbProjectTeam.setContainerDataSource(beanProjectTeam);
    	 } catch(Exception e) {
    		 logger.warn("Loaing null values in loadCityList() function----->>>>>>>"+e);
    	 }
     }
    
      
      /*
    	 * 
    	 * this function handles load Employee list to Search Employee name component.
    	 * 
    	 */
        
     /*
 	 * saveDetails()-->this function is used for save/update the records
 	 */	
 private  void saveTimeSheetEntryDetails() {
	 btnSave.setComponentError(null);
	 Date tsDate = dfTimesheet.getValue();
	 int count = 1;
	 try {
    	 count = servTimeSheetBean.getTimesheetDateValidate(tsDate,loginEmployeeid,companyId);
	 } catch(Exception e) {
		 logger.info("Select Time Sheet Date------->>>>>>>>"+e);
	 }
    		 TimeSheetsDM saveEntry=new TimeSheetsDM();
    		 saveEntry.setTaskDesc(taTaskDesc.getValue().toString());
    		 saveEntry.setTimeSheetDate(dfTimesheet.getValue());
    		 saveEntry.setEffort(new Long(tfEffort.getValue().toString()));
    		 saveEntry.setTaskVerified("N");
    		 saveEntry.setStatus(Common.PEND_DESC);
    		 saveEntry.setEmployeeid(loginEmployeeid);
    		 saveEntry.setApproveManagerId(approveManagerId);
    		 saveEntry.setProjectId(selectedProject);
    		 saveEntry.setProjectPhaseId(selectedProjPhase);
    		 saveEntry.setProjectTeamId(selectedProjTeam);
    		 saveEntry.setLastUpdatedBy(userName);
    		 saveEntry.setLastUpdtDate(DateUtils.getcurrentdate());
    		 if(count == 0 && approveManagerId!=0 && tfEffort.isValid() && cbProjectName.isValid() && dfTimesheet.isValid() && taTaskDesc.isValid()) {
    			 servTimeSheetBean.saveOrUpdateProjectTimeSheet(saveEntry);
    			 timeSheetId = saveEntry.getProjectTimeSheetId().toString();
    			 System.out.println("employee id "+loginEmployeeid+" time sheet id "+timeSheetId+" companyid"+companyId);
    			 servParameterbean.updateParameterBasedonTimeSheetList(loginEmployeeid+"",timeSheetId,companyId);
    			 populateAndConfigureTable();
    			 btnSave.setCaption("Save");
    			 lblNotification.setValue("Successfully Saved");
    			 lblNotificationIcon.setIcon(new ThemeResource("img/success_small.png"));
    		 }
    		 else {
    			 btnSave.setComponentError(new UserError("Form is not valid"));
    			 lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
    			 lblNotification.setValue("Save failed, please check the data and try again ");
    		 }
     }
    private void saveComponentError()
    {
     cbProjectName.setComponentError(null);
   	 dfTimesheet.setComponentError(null);
   	 taTaskDesc.setComponentError(null);
   		if( cbProjectName.getValue()==null || cbProjectName.getValue().toString().trim().length()==0)
   		{
   			cbProjectName.setComponentError(new UserError("Select Project Name "));
   		}
   		if( dfTimesheet.getValue()==null || dfTimesheet.getValue().toString().trim().length()==0)
   		{
   			dfTimesheet.setComponentError(new UserError("Select TimeSheet Date "));
   		}		
   		if( taTaskDesc.getValue()==null || taTaskDesc.getValue().toString().trim().length()==0)
   		{
   			taTaskDesc.setComponentError(new UserError("Enter Task Description "));
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
			vlSearchPanel.setVisible(false);
			vlTablePanel.setVisible(true);
			saveComponentError();
			try {
				saveTimeSheetEntryDetails();
			} catch(Exception e) {
				e.printStackTrace();
				logger.info("check  saveTimeSheetEntryDetails() function. Time Sheet datas did not saved properly--->" + e);
			}
		}
		else if(btnReset==event.getButton()) {
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			vlTablePanel.setVisible(true);
			populateAndConfigureTable();
			 resetFields();
			
		btnSave.setCaption("Save");
		lblFormTitle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Search");
		hlSaveBtn.setVisible(true);
		}
		
	}
						

}
