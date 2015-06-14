package com.gnts.tools.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.base.domain.mst.CompanyDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.constants.DateFormateColumnGenerator;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateUtils;
import com.gnts.tools.domain.txn.MeetingDM;
import com.gnts.tools.domain.txn.MeetingTaskActionDM;
import com.gnts.tools.domain.txn.MeetingTaskDM;
import com.gnts.tools.service.txn.MeetingService;
import com.gnts.tools.service.txn.MeetingTaskActionService;
import com.gnts.tools.service.txn.MeetingTaskService;
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
import com.vaadin.shared.ui.MarginInfo;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

public class MeetingUI implements ClickListener {

	private MeetingService servbean = (MeetingService) SpringContextHelper
			.getBean("tToolMeeting");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private BeanItemContainer<EmployeeDM> beanEmployee = null;
	
	
	

	private MeetingTaskService serviceMeetingTaskbean = (MeetingTaskService) SpringContextHelper
			.getBean("tToolMeetingTask");
	
	private MeetingTaskActionService serviceMeetingTaskActionBean = (MeetingTaskActionService) SpringContextHelper
			.getBean("tToolMeetingTaskAction");
	
	
	private BeanItemContainer<MeetingTaskDM> beansToolMeetingTask = null;
	private BeanItemContainer<MeetingTaskActionDM> beansToolMeetingTaskAction = null;
	
	
	// Search Layout Components
	private ComboBox  cbSearchStatus;
	private PopupDateField dfSearchStartdt , dfSearchEnddt;
	private TextField tfSearchMeetingDesc;
	// form Layout components
	
	
	private	TabSheet toolMeetingTab;
	
	private ComboBox cbStatus,cbChairedBy ,cbRecordedBy;
	private TextField tfMeetingDesc,tfMeetingLocation,tfNoofTasks,tfOpenTasks;
	private TextArea tfMeetingAgenda,tfParticipants,tfAbsentees;
	private PopupDateField dfMeetingDt;
	
	private EmployeeDM selectChairedBy; 
	private EmployeeDM selectRecordedBy; 
	private String status="Active";
	private Long meetingHdrID;
	private Long taskId;
	
	// form Layout components for ttoolmeetingtask
	
	private ComboBox cbTaskStatus,cbOwner;
	private TextField tfTaskDesc,tfPriority;
	private PopupDateField dfTaskDt , dfTaskEndDt,dfRevisionDt;
	
	private EmployeeDM selectOwner; 

	// Button Declarations
		public Button btnSaveTask, btnCancelTask, btnEditTask,
				btnDownloadTask;
		HorizontalLayout hlSaveandCancelButtonLayoutTask;
		
		
	// form Layout components for ttoolmeetingtaskaction
	
	
	private TextField tfActionDesc,tfDependancyDesc,tfActionedby,tfProgressPercentage;
	
	private Table tblMeetingTaskAction;
	public Button btnSaveAction, btnCancelAction, btnEditAction,
	btnDownloadAction;
	
	// Button Declarations
	private Button btnSearch, btnReset, btnSave, btnCancel, btnAdd, btnEdit,
			btnDownload,btnBack;

	private VerticalLayout vlTableLayout;
	HorizontalLayout hlTableTitleandCaptionLayout;
	// Declaration for exporter
		private Window notifications;
		private ExcelExporter excelexporter = new ExcelExporter();
		private CSVExporter csvexporter = new CSVExporter();
		private PdfExporter pdfexporter = new PdfExporter();
		
	// label for titles
	private Label lblSearchTitle, lblFormTitle , lblFormTitle1,lblAddEdit;;

	private Label lblNotification, lblNotificationIcon;

	private Table tblToolMeeting;
	
	
	private Table tblToolMeetingTask;
	
	
	// Layouts
		VerticalLayout pnlTableTask = new VerticalLayout();
		VerticalLayout pnlFormTask = new VerticalLayout();

	// Layouts
	VerticalLayout pnlSearch = new VerticalLayout();
	VerticalLayout pnlTable = new VerticalLayout();
	VerticalLayout pnlForm = new VerticalLayout();

	VerticalLayout vlToolMeeting = new VerticalLayout();
	VerticalLayout vlToolMeetingTasks = new VerticalLayout();
	
	private VerticalLayout vlTableLayoutTask;
	HorizontalLayout hlTableTitleandCaptionLayoutTask;
	HorizontalLayout hlBreadCrumbs;
	
	private Logger logger = Logger.getLogger(MeetingUI.class);

	private HorizontalLayout hlSaveandCancelButtonLayout;
	private String strLoginUserName, strScreenName;
	private BeanItemContainer<StatusDM> beansStatus;
	private BeanItemContainer<MeetingDM> beansToolMeeting = null;

	// declare total= 0 for set the total size
	private int totalnoofrecords;
	private Long companyId;

	// Constructor received the parameters from Login UI class
	public MeetingUI() {
		strLoginUserName = UI.getCurrent().getSession()
				.getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		strScreenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		VerticalLayout clMainLayout = (VerticalLayout) UI.getCurrent().getSession()
				.getAttribute("clLayout");
		HorizontalLayout hlScreenNameLayout = (HorizontalLayout) UI
				.getCurrent().getSession().getAttribute("hlLayout");

		buildView(clMainLayout, hlScreenNameLayout);
	}

	private void buildView(VerticalLayout clMainLayout,
			HorizontalLayout hlScreenNameLayout) {

		logger.info("Tool Meeting App called");

		hlScreenNameLayout.removeAllComponents();

	
		
		
		
		lblNotification = new Label();
		lblNotification.setContentMode(ContentMode.HTML);
		lblNotificationIcon = new Label();
		// search field component

		// load lookup detail for search
		dfSearchStartdt = new PopupDateField("Start Date");
		//dfSearchStartdt.setInputPrompt("Select Date");
		dfSearchStartdt.setDateFormat("dd-MMM-yyyy");
		dfSearchStartdt.setWidth("120");
		
		dfSearchEnddt = new PopupDateField("End Date");
	//	dfSearchEnddt.setInputPrompt("Select Date");
		dfSearchEnddt.setDateFormat("dd-MMM-yyyy");
		dfSearchEnddt.setWidth("120");
		
		tfSearchMeetingDesc = new TextField("Meeting Description");
		tfSearchMeetingDesc.setWidth("120");
		 
		// status field for search
		cbSearchStatus = new ComboBox("Status");
		//cbSearchStatus.setInputPrompt(ApplicationConstants.selectDefault);
		cbSearchStatus.setItemCaptionPropertyId("desc");
		cbSearchStatus.setImmediate(true);
		cbSearchStatus.setNullSelectionAllowed(false);
		beansStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
		beansStatus.addAll(Common.listStatus);
		cbSearchStatus.setContainerDataSource(beansStatus);
		cbSearchStatus.setWidth("120");
		
		btnBack=new Button("Search",this);
		btnBack.setStyleName("link");

		
		btnSearch = new Button("Search", this);
		btnSearch.setStyleName("searchbt");
		//btnSearch.setDescription("Search Tool Meeting");

		btnReset = new Button("Reset", this);
		//btnReset.setDescription("Reset Search");
		btnReset.setStyleName("resetbt");

		lblSearchTitle = new Label(" ", ContentMode.HTML);
		lblSearchTitle
				.setValue("<font size=\"3\" color=\"#1E90FF\"><B>Search</B></font>");

		lblFormTitle = new Label();
		lblFormTitle.setContentMode(ContentMode.HTML);

		lblFormTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName
				+ "</b>&nbsp;::&nbsp;Search");

		
		
		lblFormTitle1=new Label();
		lblFormTitle1.setContentMode(ContentMode.HTML);

		lblFormTitle1.setValue("&nbsp;&nbsp;<b>" + strScreenName
				+ "</b>&nbsp;::&nbsp;");
		
		
		lblAddEdit=new Label();
		lblAddEdit.setContentMode(ContentMode.HTML);


		FormLayout flSearchLookupCode = new FormLayout();
		flSearchLookupCode.addComponent(tfSearchMeetingDesc);
		FormLayout flSearchStatus = new FormLayout();
		flSearchStatus.addComponent(cbSearchStatus);
		FormLayout flSearchStartdate = new FormLayout();
		flSearchStartdate.addComponent(dfSearchStartdt);
		FormLayout flSearchEndDate = new FormLayout();
		flSearchEndDate.addComponent(dfSearchEnddt);

		HorizontalLayout hlSearchComponentLayout = new HorizontalLayout();
		hlSearchComponentLayout.addComponent(flSearchLookupCode);
		hlSearchComponentLayout.addComponent(flSearchStatus);
		hlSearchComponentLayout.addComponent(flSearchStartdate);
		Label lblSpace = new Label();
		lblSpace.setWidth("5");
		
		hlSearchComponentLayout.addComponent(lblSpace);
		hlSearchComponentLayout.addComponent(flSearchEndDate);
		hlSearchComponentLayout.setSpacing(true);
		hlSearchComponentLayout.setMargin(true);

		VerticalLayout vlSearchandResetButtonLAyout = new VerticalLayout();
		vlSearchandResetButtonLAyout.setSpacing(true);
		vlSearchandResetButtonLAyout.addComponent(btnSearch);
		vlSearchandResetButtonLAyout.addComponent(btnReset);
		vlSearchandResetButtonLAyout.setWidth("32%");
		vlSearchandResetButtonLAyout.addStyleName("topbarthree");
		vlSearchandResetButtonLAyout.setMargin(true);

		HorizontalLayout hlSearchComponentandButtonLayout = new HorizontalLayout();
		hlSearchComponentandButtonLayout.setSizeFull();
		hlSearchComponentandButtonLayout.setSpacing(true);
		hlSearchComponentandButtonLayout.addComponent(hlSearchComponentLayout);
		hlSearchComponentandButtonLayout.setComponentAlignment(
				hlSearchComponentLayout, Alignment.MIDDLE_LEFT);
		hlSearchComponentandButtonLayout
				.addComponent(vlSearchandResetButtonLAyout);
		hlSearchComponentandButtonLayout.setComponentAlignment(
				vlSearchandResetButtonLAyout, Alignment.MIDDLE_RIGHT);
		hlSearchComponentandButtonLayout.setExpandRatio(vlSearchandResetButtonLAyout, 1);
		
		
		pnlSearch.addComponent(PanelGenerator
				.createPanel(hlSearchComponentandButtonLayout));
		pnlSearch.setMargin(true);
		

		btnDownload = new Button("Download Meeting", this);
		btnDownload.addStyleName("downloadbt");
		//btnDownload.setDescription("Download");
		btnDownload.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
        //  UI.getCurrent()..clearDashboardButtonBadge();
                event.getButton().removeStyleName("unread");
               if (notifications != null && notifications.getUI() != null)
                    notifications.close();
                else {
                    buildNotifications(event);
                UI.getCurrent().addWindow(notifications);
                    notifications.focus();
                    ((VerticalLayout) UI.getCurrent().getContent())
                            .addLayoutClickListener(new LayoutClickListener() {
                                @Override
                                public void layoutClick(LayoutClickEvent event) {
                                    notifications.close();
                                    ((VerticalLayout) UI.getCurrent().getContent())
                                            .removeLayoutClickListener(this);
                                }
                            });
                }

            }
        });

		
		
		btnAdd = new Button("Add", this);
		//btnAdd.setDescription("Add Tool Meeting");
		btnAdd.setStyleName("add");
		btnEdit = new Button("Edit", this);
		btnEdit.setEnabled(false);
		//btnEdit.setDescription("Edit Tool Meeting");
		btnEdit.setStyleName("editbt");
		
	

		HorizontalLayout hlFileDownloadLayout = new HorizontalLayout();
		hlFileDownloadLayout.setSpacing(true);
		hlFileDownloadLayout.addComponent(btnDownload);
		hlFileDownloadLayout.setComponentAlignment(btnDownload,
				Alignment.MIDDLE_CENTER);

		

		HorizontalLayout hlTableCaptionLayout = new HorizontalLayout();
		hlTableCaptionLayout.addComponent(btnAdd);
		hlTableCaptionLayout.addComponent(btnEdit);
		
		hlTableTitleandCaptionLayout = new HorizontalLayout();
		hlTableTitleandCaptionLayout.addStyleName("topbarthree");
		hlTableTitleandCaptionLayout.setWidth("100%");
		hlTableTitleandCaptionLayout.addComponent(hlTableCaptionLayout);
		hlTableTitleandCaptionLayout.addComponent(hlFileDownloadLayout);
		hlTableTitleandCaptionLayout.setComponentAlignment(
				hlFileDownloadLayout, Alignment.MIDDLE_RIGHT);
		hlTableTitleandCaptionLayout.setHeight("30px");
	
		tblToolMeeting = new Table();
		tblToolMeeting.setSizeFull();
		tblToolMeeting.setImmediate(true);
		tblToolMeeting.setSelectable(true);
		tblToolMeeting.setColumnCollapsingAllowed(true);
		tblToolMeeting.setPageLength(9);
		tblToolMeeting.setFooterVisible(true);

		vlTableLayout = new VerticalLayout();
		vlTableLayout.setSizeFull();
		//vlTableLayout.setMargin(true);
		vlTableLayout.setMargin(new MarginInfo(false, true, false, true));
		vlTableLayout.addComponent(hlTableTitleandCaptionLayout);
		vlTableLayout.addComponent(tblToolMeeting);
		vlTableLayout.setExpandRatio(tblToolMeeting, 1);
		pnlTable.addComponent(vlTableLayout);
		
		
		
		

		// Meeting Form Components Starts here
		
		dfMeetingDt = new PopupDateField("Meeting Date");
		//dfMeetingDt.setInputPrompt("Select Date");
		dfMeetingDt.setDateFormat("dd-MMM-yyyy");
		dfMeetingDt.setWidth("130");
		dfMeetingDt.setRequired(true);
		
		 tfMeetingDesc = new TextField("Meeting Desc.");
		// tfMeetingDesc.setInputPrompt("Enter Meeting Desc.");
		 tfMeetingDesc.setWidth("130");
		 tfMeetingDesc.setMaxLength(100);
		 tfMeetingDesc.setRequired(true);
		 
		 
		 tfMeetingAgenda = new TextArea("Agenda");
		// tfMeetingAgenda.setInputPrompt("Enter Meeting Agenda");
		 tfMeetingAgenda.setWidth("130");
		 tfMeetingAgenda.setHeight("100");
		 tfMeetingAgenda.setMaxLength(2000);
		 
		 tfMeetingLocation = new TextField("Meeting loc.");
		 //tfMeetingLocation.setInputPrompt("Enter Meeting Location");
		 tfMeetingLocation.setWidth("130");
		 tfMeetingLocation.setMaxLength(25);
		 
		 tfParticipants = new TextArea("Participants");
		// tfParticipants.setInputPrompt("Enter Participants");
		 tfParticipants.setWidth("130");
		 tfParticipants.setHeight("100");
		 tfParticipants.setMaxLength(2000);
		
		 tfAbsentees = new TextArea("Absentees");
		 //tfAbsentees.setInputPrompt("Enter Absentees");
		 tfAbsentees.setWidth("130");
		 tfAbsentees.setHeight("80");
		 tfAbsentees.setMaxLength(2000);
		 
		 tfNoofTasks = new TextField("No.of Tasks");
		 tfNoofTasks.setWidth("130");
		 tfNoofTasks.setValue("0");
		 
		 tfOpenTasks = new TextField("Open Tasks");
		 tfOpenTasks.setWidth("130");
		 tfOpenTasks.setValue("0");
		 
		 
		 cbChairedBy=new ComboBox("Chaired By");
		// cbChairedBy.setInputPrompt(ApplicationConstants.selectDefault);
		 cbChairedBy.setItemCaptionPropertyId("firstname");
		 loadChairedName();
		 cbChairedBy.addValueChangeListener(new Property.ValueChangeListener() {
				 private static final long serialVersionUID = 1L;

					public void valueChange(ValueChangeEvent event) {

						final Object itemId = event.getProperty().getValue();
						if (itemId != null) {
							final BeanItem<?> item = (BeanItem<?>) cbChairedBy.getItem(itemId);
							selectChairedBy = (EmployeeDM) item.getBean();
					
							}
					}
				}); 
		 cbChairedBy.setImmediate(true);
		 cbChairedBy.setNullSelectionAllowed(false);
		 cbChairedBy.setWidth("130");
		 cbChairedBy.setRequired(true);
		 
		 cbRecordedBy=new ComboBox("Recorded By");
		 //cbRecordedBy.setInputPrompt(ApplicationConstants.selectDefault);
		 cbRecordedBy.setItemCaptionPropertyId("firstname");
		 loadRecordedName();
		 cbRecordedBy.addValueChangeListener(new Property.ValueChangeListener() {
				 private static final long serialVersionUID = 1L;

					public void valueChange(ValueChangeEvent event) {

						final Object itemId = event.getProperty().getValue();
						if (itemId != null) {
							final BeanItem<?> item = (BeanItem<?>) cbRecordedBy.getItem(itemId);
							selectRecordedBy = (EmployeeDM) item.getBean();
					
							}
					}
				}); 
		 cbRecordedBy.setImmediate(true);
		 cbRecordedBy.setNullSelectionAllowed(false);
		 cbRecordedBy.setWidth("130");
		 cbRecordedBy.setRequired(true);
		 
		 
		cbStatus = new ComboBox("Status");
		//cbStatus.setInputPrompt(ApplicationConstants.selectDefault);
		cbStatus.setItemCaptionPropertyId("desc");
		cbStatus.setImmediate(true);
		cbStatus.setNullSelectionAllowed(false);
		beansStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
		beansStatus.addAll(Common.listStatus);
		cbStatus.setContainerDataSource(beansStatus);
		cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
		cbStatus.setWidth("130");

		

		// main panel (form) components ends here


		FormLayout fl1 = new FormLayout();
		FormLayout fl2 = new FormLayout();
		FormLayout fl3 = new FormLayout();
		FormLayout fl4 = new FormLayout();
		FormLayout fl5 = new FormLayout();
		FormLayout fl6 = new FormLayout();

		
		fl1.addComponent(dfMeetingDt);
		fl1.addComponent(tfMeetingDesc);
		fl1.addComponent(tfMeetingLocation);
		//fl1.addComponent(cbLookup);
		
		
		fl2.addComponent(tfMeetingAgenda);
		
		
		
		fl3.addComponent(cbChairedBy);
		fl3.addComponent(cbRecordedBy);
		fl3.addComponent(tfNoofTasks);
		fl3.addComponent(tfOpenTasks);
		
		fl4.addComponent(tfParticipants);
		
		
		fl5.addComponent(tfAbsentees);
		fl5.addComponent(cbStatus);
		
		
		
		
		fl1.setSpacing(true);
		fl2.setSpacing(true);
		fl3.setSpacing(true);
		fl4.setSpacing(true);
		fl5.setSpacing(true);
		fl6.setSpacing(true);

		Label lblSpace1 = new Label();
		lblSpace1.setWidth("5");
		
		HorizontalLayout hlForm = new HorizontalLayout();
		hlForm.setSpacing(true);
		hlForm.addComponent(fl1);
		hlForm.addComponent(lblSpace1);
		hlForm.addComponent(fl2);
		hlForm.addComponent(fl3);
		hlForm.addComponent(fl4);
		hlForm.addComponent(fl5);
		hlForm.addComponent(fl6);
		
		GridLayout glform = new GridLayout();
		glform.addComponent(hlForm);
		glform.setMargin(true);
		glform.setSpacing(true);
		glform.setSizeFull();
		vlToolMeeting.addComponent(PanelGenerator.createPanel(glform));
		vlToolMeeting.setMargin(true);
		
		
		
		
		
		
		
		
		
		
		
		
		

		// Form Components Starts here
		
		dfTaskDt = new PopupDateField("Task Date");
		//dfTaskDt.setInputPrompt("Select Date");
		dfTaskDt.setDateFormat("dd-MMM-yyyy");
		dfTaskDt.setWidth("130");
		
		dfTaskEndDt = new PopupDateField("Task End Date");
		//dfTaskEndDt.setInputPrompt("Select Date");
		dfTaskEndDt.setDateFormat("dd-MMM-yyyy");
		dfTaskEndDt.setWidth("130");
		
		dfRevisionDt = new PopupDateField("Revision Date");
		//dfRevisionDt.setInputPrompt("Select Date");
		dfRevisionDt.setDateFormat("dd-MMM-yyyy");
		dfRevisionDt.setWidth("130");
		
		
		 tfTaskDesc = new TextField("Task Description");
		// tfTaskDesc.setInputPrompt("Enter Task Desc.");
		 tfTaskDesc.setWidth("130");
		 tfTaskDesc.setMaxLength(500);
		 
		 	 
		 tfPriority = new TextField("Priority");
		// tfPriority.setInputPrompt("Enter Priority");
		 tfPriority.setWidth("130");
		 tfPriority.setMaxLength(10);
		 
		
		 
		 
		 cbOwner=new ComboBox("Owner");
		 //cbOwner.setInputPrompt(ApplicationConstants.selectDefault);
		 cbOwner.setItemCaptionPropertyId("firstname");
		 loadOwnerName();
		 cbOwner.addValueChangeListener(new Property.ValueChangeListener() {
				 private static final long serialVersionUID = 1L;

					public void valueChange(ValueChangeEvent event) {

						final Object itemId = event.getProperty().getValue();
						if (itemId != null) {
							final BeanItem<?> item = (BeanItem<?>) cbOwner.getItem(itemId);
							selectOwner = (EmployeeDM) item.getBean();
					
							}
					}
				}); 
		 cbOwner.setImmediate(true);
		 cbOwner.setNullSelectionAllowed(false);
		 cbOwner.setWidth("130");
		 cbOwner.setRequired(true);
		 
		
		 
		 
		 cbTaskStatus = new ComboBox("Status");
		// cbTaskStatus.setInputPrompt(ApplicationConstants.selectDefault);
		 cbTaskStatus.setItemCaptionPropertyId("desc");
		 cbTaskStatus.setImmediate(true);
		 cbTaskStatus.setNullSelectionAllowed(false);
		beansStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
		beansStatus.addAll(Common.listStatus);
		cbTaskStatus.setContainerDataSource(beansStatus);
		cbTaskStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
		cbTaskStatus.setWidth("130");

		

		FormLayout fl1Task = new FormLayout();
		FormLayout fl2Task = new FormLayout();
		FormLayout fl3Task = new FormLayout();
		FormLayout fl4Task = new FormLayout();
		
		
		fl1Task.addComponent(dfTaskDt);
		fl1Task.addComponent(tfTaskDesc);
				
		fl2Task.addComponent(dfTaskEndDt);
		fl2Task.addComponent(dfRevisionDt);
		
		
		fl3Task.addComponent(cbOwner);
		fl3Task.addComponent(tfPriority);
		
		
		fl4Task.addComponent(cbTaskStatus);
		
		
		fl1Task.setSpacing(true);
		fl2Task.setSpacing(true);
		fl3Task.setSpacing(true);
		fl4Task.setSpacing(true);

		Label lblSpaceTask = new Label();
		lblSpaceTask.setWidth("5");

		Label lblSpace2Task = new Label();
		lblSpace2Task.setWidth("5");
		
		
		HorizontalLayout hlFormTask = new HorizontalLayout();
		hlFormTask.setSpacing(true);
		hlFormTask.addComponent(fl1Task);
		hlFormTask.addComponent(lblSpaceTask);
		hlFormTask.addComponent(fl2Task);
		hlFormTask.addComponent(lblSpace2Task);
		hlFormTask.addComponent(fl3Task);
		hlFormTask.addComponent(fl4Task);
		
		GridLayout glformTask = new GridLayout();
		glformTask.addComponent(hlFormTask);
		glformTask.setMargin(true);
		glformTask.setSpacing(true);
		glformTask.setSizeFull();
		pnlFormTask.addComponent(PanelGenerator.createPanel(glformTask));
		pnlFormTask.setMargin(true);
		
		
		btnSaveTask = new Button("Save Meeting Task", this);
	//	btnSaveTask.setDescription("Save Tool Meeting Task");
		btnSaveTask.setStyleName("savebt");
		

		btnCancelTask = new Button("Reset Task", this);
		//btnCancelTask.setDescription("Return to Search");
		btnCancelTask.setStyleName("resetbt");

		// add save and cancel to to layout
		hlSaveandCancelButtonLayoutTask = new HorizontalLayout();
		hlSaveandCancelButtonLayoutTask.addComponent(btnSaveTask);
		hlSaveandCancelButtonLayoutTask.addComponent(btnCancelTask);
		hlSaveandCancelButtonLayoutTask.setVisible(false);


		btnEditTask = new Button("Edit Task", this);
		btnEditTask.setEnabled(false);
		//btnEditTask.setDescription("Edit Tool Meeting Tasks");
		btnEditTask.setStyleName("editbt");
		
		btnDownloadTask = new Button("Download Task", this);
		btnDownloadTask.addStyleName("downloadbt");
		//btnDownloadTask.setDescription("Download");
		btnDownloadTask.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
        //  UI.getCurrent()..clearDashboardButtonBadge();
                event.getButton().removeStyleName("unread");
               if (notifications != null && notifications.getUI() != null)
                    notifications.close();
                else {
                    buildNotifications(event);
                UI.getCurrent().addWindow(notifications);
                    notifications.focus();
                    ((VerticalLayout) UI.getCurrent().getContent())
                            .addLayoutClickListener(new LayoutClickListener() {
                                @Override
                                public void layoutClick(LayoutClickEvent event) {
                                    notifications.close();
                                    ((VerticalLayout) UI.getCurrent().getContent())
                                            .removeLayoutClickListener(this);
                                }
                            });
                }

            }
        });
		
		
		HorizontalLayout hlFileDownloadLayoutTask = new HorizontalLayout();
		hlFileDownloadLayoutTask.setSpacing(true);
		hlFileDownloadLayoutTask.addComponent(btnDownloadTask);
		hlFileDownloadLayoutTask.setComponentAlignment(btnDownloadTask,
				Alignment.MIDDLE_CENTER);

		

		HorizontalLayout hlTableCaptionLayoutTask = new HorizontalLayout();
		hlTableCaptionLayoutTask.addComponent(btnEditTask);
		

		hlTableTitleandCaptionLayoutTask = new HorizontalLayout();
		hlTableTitleandCaptionLayoutTask.addStyleName("topbarthree");
		hlTableTitleandCaptionLayoutTask.setWidth("100%");
		hlTableTitleandCaptionLayoutTask.addComponent(hlTableCaptionLayoutTask);
		hlTableTitleandCaptionLayoutTask.addComponent(hlFileDownloadLayoutTask);
		hlTableTitleandCaptionLayoutTask.setComponentAlignment(
				hlFileDownloadLayoutTask, Alignment.MIDDLE_RIGHT);
		hlTableTitleandCaptionLayoutTask.setHeight("30px");
		
		
		tblToolMeetingTask = new Table();
		tblToolMeetingTask.setSizeFull();
		tblToolMeetingTask.setImmediate(true);
		tblToolMeetingTask.setSelectable(true);
		tblToolMeetingTask.setPageLength(3);
		tblToolMeetingTask.setHeight("70");
		
		vlTableLayoutTask = new VerticalLayout();
		vlTableLayoutTask.setSizeFull();
		vlTableLayoutTask.setMargin(true);
		vlTableLayoutTask.addComponent(hlTableTitleandCaptionLayoutTask);
		vlTableLayoutTask.addComponent(tblToolMeetingTask);
		pnlTableTask.addComponent(vlTableLayoutTask);
		vlToolMeetingTasks.addComponent(pnlFormTask);
		vlToolMeetingTasks.addComponent(pnlTableTask);



		// form Layout components for ttoolmeetingtaskaction
		


		btnDownloadAction= new Button("Download Action", this);
		btnDownloadAction.addStyleName("downloadbt");
		//btnDownloadAction.setDescription("Download Action");
		btnDownloadAction.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
        //  UI.getCurrent()..clearDashboardButtonBadge();
                event.getButton().removeStyleName("unread");
               if (notifications != null && notifications.getUI() != null)
                    notifications.close();
                else {
                    buildNotifications(event);
                UI.getCurrent().addWindow(notifications);
                    notifications.focus();
                    ((VerticalLayout) UI.getCurrent().getContent())
                            .addLayoutClickListener(new LayoutClickListener() {
                                @Override
                                public void layoutClick(LayoutClickEvent event) {
                                    notifications.close();
                                    ((VerticalLayout) UI.getCurrent().getContent())
                                            .removeLayoutClickListener(this);
                                }
                            });
                }

            }
        });


		btnEditAction = new Button("Edit Action", this);
		btnEditAction.setEnabled(false);
		//btnEditAction.setDescription("Edit Tool Meeting Tasks");
		btnEditAction.setStyleName("editbt");

		
		HorizontalLayout hlFileDownloadLayout2 = new HorizontalLayout();
		hlFileDownloadLayout2.setSpacing(true);
		hlFileDownloadLayout2.addComponent(btnDownloadAction);
		hlFileDownloadLayout2.setComponentAlignment(btnDownloadAction,
				Alignment.MIDDLE_CENTER);

		

		HorizontalLayout hlTableCaptionLayout2 = new HorizontalLayout();
		hlTableCaptionLayout2.addComponent(btnEditAction);
		
		HorizontalLayout hlTableTitleandCaptionLayout2 = new HorizontalLayout();
		hlTableTitleandCaptionLayout2.addStyleName("topbarthree");
		hlTableTitleandCaptionLayout2.setWidth("100%");
		hlTableTitleandCaptionLayout2.addComponent(hlTableCaptionLayout2);
		hlTableTitleandCaptionLayout2.addComponent(hlFileDownloadLayout2);
		hlTableTitleandCaptionLayout2.setComponentAlignment(
				hlFileDownloadLayout2, Alignment.MIDDLE_RIGHT);
		hlTableTitleandCaptionLayout2.setHeight("30px");

		tblMeetingTaskAction = new Table();
		tblMeetingTaskAction.setSizeFull();
		tblMeetingTaskAction.setImmediate(true);
		tblMeetingTaskAction.setSelectable(true);
		tblMeetingTaskAction.setPageLength(3);
		tblMeetingTaskAction.setHeight("70");

		VerticalLayout vlTableLayout1 = new VerticalLayout();
		vlTableLayout1.setSizeFull();
		//vlTableLayout1.setMargin(true);
		vlTableLayout1.addComponent(hlTableTitleandCaptionLayout2);
		vlTableLayout1.addComponent(tblMeetingTaskAction);
		
		tfActionDesc = new TextField("Action Desc.");
		//tfActionDesc.setInputPrompt("Enter Action Desc.");
		tfActionDesc.setWidth("130");
		tfActionDesc.setMaxLength(500);
		
		tfDependancyDesc = new TextField("Dependancy Desc.");
		//tfDependancyDesc.setInputPrompt("Enter Dependancy Desc.");
		tfDependancyDesc.setWidth("130");
		tfDependancyDesc.setMaxLength(100);
		
		
		tfProgressPercentage = new TextField("Progress Percentage");
		tfProgressPercentage.setWidth("90");
		tfProgressPercentage.setValue("0");
		
		tfActionedby = new TextField("Actioned By");
		tfActionedby.setWidth("90");
		tfActionedby.setValue("0");
		
				
				
		btnSaveAction = new Button("Save Action", this);
		//btnSaveAction.setDescription("Save Meeting Task Action");
		btnSaveAction.setStyleName("savebt");
		btnSaveAction.setEnabled(false);
		
		
		btnCancelAction = new Button("Reset Action", this);
		//btnCancelAction.setDescription("Reset Action Fields");
		btnCancelAction.setStyleName("resetbt");

			
		HorizontalLayout hlSaveandCancelButtonLayout2 = new HorizontalLayout();
		hlSaveandCancelButtonLayout2.addComponent(btnSaveAction);
		hlSaveandCancelButtonLayout2.addComponent(btnCancelAction);
		//hlSaveandCancelButtonLayout2.setVisible(false);
		
		FormLayout fl12 = new FormLayout();
		FormLayout fl22 = new FormLayout();
		FormLayout fl32 = new FormLayout();
		FormLayout fl42 = new FormLayout();
		FormLayout fl52 = new FormLayout();
		FormLayout fl62 = new FormLayout();
		fl12.addComponent(tfActionDesc);
		fl22.addComponent(tfDependancyDesc);
		fl32.addComponent(tfProgressPercentage);
		fl42.addComponent(tfActionedby);
		fl52.addComponent(btnSaveAction);
		fl62.addComponent(btnCancelAction);
		
		
		fl12.setSpacing(true);
		fl22.setSpacing(true);
		fl32.setSpacing(true);
		fl42.setSpacing(true);
		fl52.setSpacing(true);
		fl62.setSpacing(true);
		
		HorizontalLayout hlForm2 = new HorizontalLayout();
		hlForm2.setSpacing(true);
		hlForm2.addComponent(fl12);
		hlForm2.addComponent(fl22);
		hlForm2.addComponent(fl32);
		hlForm2.addComponent(fl42);
		hlForm2.addComponent(fl52);
		hlForm2.addComponent(fl62);
		hlForm2.setMargin(true);
		
		GridLayout glform2 = new GridLayout();
	//	glform2.addComponent(hlForm2);
		glform2.addComponent(PanelGenerator.createPanel(hlForm2));
		glform2.addComponent(vlTableLayout1);
		glform2.setMargin(false);
		glform2.setSpacing(true);
		glform2.setSizeFull();
		
		vlToolMeetingTasks.addComponent(glform2);
	
	
		toolMeetingTab = new TabSheet();
		toolMeetingTab.addTab(vlToolMeeting, "Tool Meeting", null);
		toolMeetingTab.addTab(vlToolMeetingTasks, "Tool Meeting Tasks", null);
		toolMeetingTab.getTab(vlToolMeeting).setEnabled(true);
		toolMeetingTab.getTab(vlToolMeetingTasks).setEnabled(false);
		
		
		toolMeetingTab.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				// TODO Auto-generated method stub
				
				if(toolMeetingTab.getSelectedTab().equals(vlToolMeeting))
				{
					
					
					hlSaveandCancelButtonLayout.setVisible(true);
					hlSaveandCancelButtonLayoutTask.setVisible(false);
					
					/*hlSaveandCancelButtonLayout.setVisible(true);
					tToolMeetingTaskClassObj.hlSaveandCancelButtonLayout.setVisible(false);*/
					
					lblNotificationIcon.setVisible(false);
					lblNotification.setVisible(false);
				
					
				}
				
				else if(toolMeetingTab.getSelectedTab().equals(vlToolMeetingTasks))
				{
					hlSaveandCancelButtonLayout.setVisible(false);
					hlSaveandCancelButtonLayoutTask.setVisible(true);
				

					lblNotificationIcon.setVisible(false);
					lblNotification.setVisible(false);
				
				}
				
				
			}
		});
		
		

		btnSave = new Button("Save", this);
		//btnSave.setDescription("Save Tool Meeting");
		btnSave.setStyleName("savebt");

		btnCancel = new Button("Cancel", this);
		//btnCancel.setDescription("Return to Search");
		btnCancel.setStyleName("cancelbt");

		// add save and cancel to to layout
		hlSaveandCancelButtonLayout = new HorizontalLayout();
		hlSaveandCancelButtonLayout.addComponent(btnSave);
		hlSaveandCancelButtonLayout.addComponent(btnCancel);
		hlSaveandCancelButtonLayout.setVisible(false);
		
		pnlForm.addComponent(toolMeetingTab);
		pnlForm.setMargin(true);
		pnlForm.setVisible(false);

		
		
		
			
			
			
			
		clMainLayout.addComponent(pnlSearch);
		clMainLayout.addComponent(pnlForm);
		clMainLayout.addComponent(pnlTable);
		setTableProperties();
		populateAndConfigureTable(false);
		
		setTablePropertiesTask();
		setActionTableProperties();
		populateAndConfigureTableTask(meetingHdrID);
		populateAndConfigureActionTable(taskId);

		 excelexporter.setTableToBeExported(tblToolMeeting);
	        excelexporter.setCaption("Microsoft Excel (XLS)");
	        excelexporter.setStyleName("borderless");
	        
	    	csvexporter.setTableToBeExported(tblToolMeeting);
	    	csvexporter.setCaption("Comma Dilimited (CSV)");
			csvexporter.setStyleName("borderless");
			
			pdfexporter.setTableToBeExported(tblToolMeeting);
			pdfexporter.setCaption("Acrobat Document (PDF)");
			pdfexporter.setStyleName("borderless");
		
			
			hlBreadCrumbs=new HorizontalLayout();
			hlBreadCrumbs.addComponent(lblFormTitle1);
			hlBreadCrumbs.addComponent(btnBack);
			hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
			hlBreadCrumbs.addComponent(lblAddEdit);
			hlBreadCrumbs.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER);
			hlBreadCrumbs.setVisible(false);
			
			
		// add notification and title name to header layout

		HorizontalLayout hlNotification = new HorizontalLayout();
		hlNotification.addComponent(lblNotificationIcon);
		hlNotification.setComponentAlignment(lblNotificationIcon,
				Alignment.MIDDLE_CENTER);
		hlNotification.addComponent(lblNotification);
		hlNotification.setComponentAlignment(lblNotification,
				Alignment.MIDDLE_CENTER);
		hlScreenNameLayout.addComponent(lblFormTitle);
		hlScreenNameLayout.setComponentAlignment(lblFormTitle,
				Alignment.MIDDLE_LEFT);
		hlScreenNameLayout.addComponent(hlBreadCrumbs);
		hlScreenNameLayout.setComponentAlignment(hlBreadCrumbs,
				Alignment.MIDDLE_LEFT);
		
		hlScreenNameLayout.addComponent(hlNotification);
		hlScreenNameLayout.setComponentAlignment(hlNotification,
				Alignment.MIDDLE_CENTER);
		hlScreenNameLayout.addComponent(hlSaveandCancelButtonLayout);
		hlScreenNameLayout.setComponentAlignment(hlSaveandCancelButtonLayout,
				Alignment.MIDDLE_RIGHT);
		hlScreenNameLayout.addComponent(hlSaveandCancelButtonLayoutTask);
		hlScreenNameLayout.setComponentAlignment(hlSaveandCancelButtonLayoutTask,
				Alignment.MIDDLE_RIGHT);
	}

	private void buildNotifications(ClickEvent event) {
		notifications = new Window();
		VerticalLayout l = new VerticalLayout();
		l.setMargin(true);
		l.setSpacing(true);
		notifications.setWidth("178px");
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
		vlDownload.setSpacing(false);

		notifications.setContent(vlDownload);

	}
	
	
	/*
	 * loadEmployeeName(Long companyid)-->this function used to load the employeelist in  combo box
	 * 
	 * @param string status 
	 * 
	 * it loads employeenames names based on status in combo box 
	 */
	private void loadChairedName()
	{
		List<EmployeeDM> employeeList = servicebeanEmployee.getEmployeeList(null, null, null, status, null, null, null,null);
		beanEmployee = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployee.addAll(employeeList);
		cbChairedBy.setContainerDataSource(beanEmployee);
	}
	
	private void loadRecordedName()
	{
		List<EmployeeDM> employeeList = servicebeanEmployee.getEmployeeList(null, null, null, status, null, null, null,null);
		beanEmployee = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployee.addAll(employeeList);
		cbRecordedBy.setContainerDataSource(beanEmployee);
	}
	
	private void loadOwnerName()
	{
		List<EmployeeDM> employeeList = servicebeanEmployee.getEmployeeList(null, null, null, status, null, null, null,null);
		beanEmployee = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployee.addAll(employeeList);
		cbOwner.setContainerDataSource(beanEmployee);
	}
	

	// Method for show the details in grid table while search and normal mode
	private void populateAndConfigureTable(Boolean search) {

		// try{

		tblToolMeeting.removeAllItems();
		List<MeetingDM> usertable = new ArrayList<MeetingDM>();

		if (search) {
			
			Date dtStartdate = (Date)dfSearchStartdt.getValue();
			Date dtEnddate = (Date)dfSearchEnddt.getValue();
			String strMeetingDesc = tfSearchMeetingDesc.getValue();
			
			String strStatus = null;
			StatusDM st = (StatusDM) cbSearchStatus.getValue();
			try {
				strStatus = st.getCode();
			} catch (Exception e) {

			}
			if (strMeetingDesc != null ||dtStartdate!=null||dtEnddate!=null|| strStatus != null) {
				usertable = servbean.getToolMeetingList(companyId, dtStartdate, dtEnddate, strMeetingDesc);
				totalnoofrecords = usertable.size();
			}

			if (totalnoofrecords == 0) {

				lblNotification.setValue("No Records found");
			} else {
				lblNotificationIcon.setIcon(null);
				lblNotification.setValue("");
			}
		}

		else {

			usertable = servbean.getToolMeetingList(companyId, null, null,null);
			totalnoofrecords = usertable.size();
		}

		beansToolMeeting = new BeanItemContainer<MeetingDM>(
				MeetingDM.class);
		beansToolMeeting.addAll(usertable);
		
		tblToolMeeting.setContainerDataSource(beansToolMeeting);
		tblToolMeeting.setColumnFooter("lastupdatedby", "No.of Records:"
				+ totalnoofrecords);
		tblToolMeeting.setVisibleColumns(new Object[] { "meetingId",
				"meetingDesc", "meetingLocation", "meetingStatus", "lastupdateddt",
				"lastupdatedby" });
		tblToolMeeting.setColumnHeaders(new String[] { "Ref.Id",
				"Meeting Description", "Meeting Location", "Status",
				"Last Updated Date", "Last Updated By" });
		tblToolMeeting.setSelectable(true);
		tblToolMeeting.addItemClickListener(new ItemClickListener() {

			public void itemClick(ItemClickEvent event) {

				// TODO Auto-generated method stub
				if (tblToolMeeting.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
					btnAdd.setEnabled(true);

				} else {
					btnEdit.setEnabled(true);

					btnAdd.setEnabled(false);

				}
				resetFields();
				btnSave.setCaption("Save");

			}

		});
		/*
		 * }catch(Exception e){ logger.error(
		 * "error during populate values on the table, The Error is ----->"+e);
		 * }
		 */
	}

	// Show the expected value in Grid tblCompanyLookup.
	public void setTableProperties() {

		beansToolMeeting = new BeanItemContainer<MeetingDM>(
				MeetingDM.class);
		// beansCompanyLookup.addNestedContainerProperty("lookupid.lookupcode");

		/*
		 * tblCompanyLookup.addGeneratedColumn("lookupid.lookupcode", new
		 * NullCheckColumnGenerator());
		 */
		tblToolMeeting.addGeneratedColumn("lastupdateddt",
				new DateColumnGenerator());
		// tblCompanyLookup.addGeneratedColumn("lookupstatus", new
		// StatusColumnGenerator());
	}

	private void resetSearchFields() {

		tfSearchMeetingDesc.setValue("");
		cbSearchStatus.setValue(null);
		dfSearchStartdt.setValue(null);
		dfSearchEnddt.setValue(null);
		btnSearch.setComponentError(null);
		
	}

	void resetFields() {

		tfMeetingDesc.setComponentError(null);
		tfMeetingAgenda.setComponentError(null);
		tfMeetingLocation.setComponentError(null);
		tfParticipants.setComponentError(null);
		tfAbsentees.setComponentError(null);
		tfNoofTasks.setComponentError(null);
		tfOpenTasks.setComponentError(null);
		cbChairedBy.setComponentError(null);
		cbRecordedBy.setComponentError(null);
		dfMeetingDt.setComponentError(null);
		
		
		
		
		btnSave.setComponentError(null);
		
		
		tfMeetingDesc.setValue("");
		tfMeetingAgenda.setValue("");
		tfMeetingLocation.setValue("");
		tfParticipants.setValue("");
		tfAbsentees.setValue("");
		tfNoofTasks.setValue("0");
		tfOpenTasks.setValue("0");
		cbChairedBy.setValue(null);
		cbRecordedBy.setValue(null);
		dfMeetingDt.setValue(null);
		cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
		btnSave.setCaption("Save");

	
		//btnSave.setDescription("Save Tool Meeting");
		
	}

	// Display the details after click the edit button
	private void editToolMeeting() {

		pnlForm.setVisible(true);
		Item itselect = tblToolMeeting.getItem(tblToolMeeting.getValue());
		if (itselect != null) {

			MeetingDM editTToolMeeting = beansToolMeeting.getItem(
					tblToolMeeting.getValue()).getBean();
			
			meetingHdrID =  (Long)itselect.getItemProperty("meetingId").getValue();
			
			
			
			String stCode = itselect.getItemProperty("meetingStatus").getValue()
					.toString();
			cbStatus.setValue(Common.getStatus(stCode));

		
			if (editTToolMeeting.getAbsentees() != null
					&& !"null".equals((editTToolMeeting.getAbsentees()))) {
			tfAbsentees.setValue(itselect.getItemProperty("absentees")
					.getValue().toString());
			}
			
			if (editTToolMeeting.getMeetingAgenda() != null
					&& !"null".equals((editTToolMeeting.getMeetingAgenda()))) {
			tfMeetingAgenda.setValue(itselect.getItemProperty("meetingAgenda")
					.getValue().toString());
			}
			
			if (editTToolMeeting.getMeetingDesc()!= null
					&& !"null".equals((editTToolMeeting.getMeetingDesc()))) {
			
			tfMeetingDesc.setValue(itselect.getItemProperty("meetingDesc")
					.getValue().toString());
			}
			if (editTToolMeeting.getMeetingLocation()!= null
					&& !"null".equals((editTToolMeeting.getMeetingLocation()))) {
			tfMeetingLocation.setValue(itselect.getItemProperty("meetingLocation")
					.getValue().toString());
			}
			if (editTToolMeeting.getNooftasks()!= null
					&& !"null".equals((editTToolMeeting.getNooftasks()))) {
			tfNoofTasks.setValue(itselect.getItemProperty("nooftasks")
					.getValue().toString());
			}
			if (editTToolMeeting.getOpenTasks() != null
					&& !"null".equals((editTToolMeeting.getOpenTasks()))) {
			tfOpenTasks.setValue(itselect.getItemProperty("openTasks")
					.getValue().toString());
			}
			
			if (editTToolMeeting.getParticipants()!= null
					&& !"null".equals((editTToolMeeting.getParticipants()))) {
			tfParticipants.setValue(itselect.getItemProperty("participants")
					.getValue().toString());
			}
			if (editTToolMeeting.getMeetingDate()!= null
					&& !"null".equals((editTToolMeeting.getMeetingDate()))) {
			dfMeetingDt.setValue((Date)itselect.getItemProperty("meetingDate")
					.getValue());
			}
			EmployeeDM editChairedBy=editTToolMeeting.getChairedBy();
			Collection<?> collEmployee=cbChairedBy.getItemIds();
			for(Iterator iterator=collEmployee.iterator(); iterator.hasNext();) {
				Object itemid=(Object)iterator.next();
				BeanItem<?> item=(BeanItem<?>) cbChairedBy.getItem(itemid);
				EmployeeDM employeeId=(EmployeeDM) item.getBean();	
				if (editChairedBy != null && editChairedBy.getEmployeeid().equals(employeeId.getEmployeeid())) {
					cbChairedBy.setValue(itemid);
					break;
				} else {
					cbChairedBy.setValue(null);
				}
			}
			
			EmployeeDM editRecordedBy=editTToolMeeting.getRecordedBy();
			Collection<?> collEmployeeList=cbRecordedBy.getItemIds();
			for(Iterator iterator=collEmployeeList.iterator(); iterator.hasNext();) {
				Object itemid=(Object)iterator.next();
				BeanItem<?> item=(BeanItem<?>) cbRecordedBy.getItem(itemid);
				EmployeeDM employeeId=(EmployeeDM) item.getBean();	
				if (editRecordedBy != null && editRecordedBy.getEmployeeid().equals(employeeId.getEmployeeid())) {
					cbRecordedBy.setValue(itemid);
					break;
				} else {
					cbRecordedBy.setValue(null);
				}
			}
			
			populateAndConfigureTableTask(meetingHdrID);
			populateAndConfigureActionTable(taskId);

		}

	}

	// Method for save and update for Tool Meeting details
	private void saveorUpdateToolMeeting() {
		boolean valid = false;
		if (tblToolMeeting.getValue() != null) {
			MeetingDM update = beansToolMeeting.getItem(
					tblToolMeeting.getValue()).getBean();

			if (selectChairedBy != null) {
				update.setChairedBy(selectChairedBy);
			} else {
				cbChairedBy.setComponentError(new UserError("Please Select Staff "));
			}
			
			if (selectRecordedBy != null) {
				update.setRecordedBy(selectRecordedBy);
			} else {
				cbRecordedBy.setComponentError(new UserError("Please Select Staff "));
			}

			CompanyDM companyobj = new CompanyDM();
			companyobj.setCompanyid(companyId);
			update.setCompanyid(companyobj);

		
			update.setAbsentees(tfAbsentees.getValue());
			update.setMeetingAgenda(tfMeetingAgenda.getValue());

			update.setMeetingDesc(tfMeetingDesc.getValue());

			
			update.setMeetingLocation(tfMeetingLocation.getValue());

			update.setNooftasks(new Long(tfNoofTasks.getValue()));

			update.setOpenTasks(new Long(tfOpenTasks.getValue()));

			update.setParticipants(tfParticipants.getValue());
			update.setMeetingDate(dfMeetingDt.getValue());
			
			update.setLastupdatedby(strLoginUserName);
			update.setLastupdateddt(DateUtils.getcurrentdate());

			StatusDM st = (StatusDM) cbStatus.getValue();
			update.setMeetingStatus(st.getCode());

			if (tfMeetingDesc.isValid() && tfMeetingLocation.isValid()
					&& cbChairedBy.isValid()&&cbRecordedBy.isValid()) {
				servbean.saveAndUpdateDetails(update);
				
				meetingHdrID = update.getMeetingId();
				
				populateAndConfigureTableTask(meetingHdrID);
				populateAndConfigureActionTable(taskId);
				
				valid = true;
				
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblNotification.setValue(ApplicationConstants.updatedMsg);

				
			}
		}

		else {

			MeetingDM save = new MeetingDM();

			if (selectChairedBy != null) {
				save.setChairedBy(selectChairedBy);
			} else {
				cbChairedBy.setComponentError(new UserError("Please Select Staff "));
			}
			
			if (selectRecordedBy != null) {
				save.setRecordedBy(selectRecordedBy);
			} else {
				cbRecordedBy.setComponentError(new UserError("Please Select Staff "));
			}

			CompanyDM companyobj = new CompanyDM();
			companyobj.setCompanyid(companyId);
			save.setCompanyid(companyobj);

		
			save.setAbsentees(tfAbsentees.getValue());
			save.setMeetingAgenda(tfMeetingAgenda.getValue());

			save.setMeetingDesc(tfMeetingDesc.getValue());

			save.setMeetingDate(dfMeetingDt.getValue());
			
			save.setMeetingLocation(tfMeetingLocation.getValue());

			save.setNooftasks(new Long(tfNoofTasks.getValue()));

			save.setOpenTasks(new Long(tfOpenTasks.getValue()));

			save.setParticipants(tfParticipants.getValue());

			
			save.setLastupdatedby(strLoginUserName);
			save.setLastupdateddt(DateUtils.getcurrentdate());

			StatusDM st = (StatusDM) cbStatus.getValue();
			save.setMeetingStatus(st.getCode());

			if (tfMeetingDesc.isValid() && tfMeetingLocation.isValid()
					&& cbChairedBy.isValid()&&cbRecordedBy.isValid()) {
				servbean.saveAndUpdateDetails(save);
				meetingHdrID = save.getMeetingId();
				populateAndConfigureTableTask(meetingHdrID);
				populateAndConfigureActionTable(taskId);
				valid = true;
				

				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblNotification.setValue(ApplicationConstants.saveMsg);
				
			
			}

		}

		if (valid) {
			populateAndConfigureTable(false);
			btnSave.setComponentError(null);
			//resetFields();
			btnSave.setCaption("Save");
			pnlForm.setVisible(true);
			toolMeetingTab.getTab(vlToolMeeting).setEnabled(true);
			toolMeetingTab.getTab(vlToolMeetingTasks).setEnabled(true);
			
			
			
	
			
		} else {
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
			lblNotification
					.setValue("Save failed, please check the data and try again ");
		}
	}

	// Method for show the details in grid table while search and normal mode
	private void populateAndConfigureTableTask(Long metingHeaderId) {

		// try{

		tblToolMeetingTask.removeAllItems();
		List<MeetingTaskDM> usertable = new ArrayList<MeetingTaskDM>();

		
			usertable = serviceMeetingTaskbean.getTToolMeetingTaskList(metingHeaderId);


		beansToolMeetingTask = new BeanItemContainer<MeetingTaskDM>(
				MeetingTaskDM.class);
		beansToolMeetingTask.addAll(usertable);
		
		tblToolMeetingTask.setContainerDataSource(beansToolMeetingTask);
		tblToolMeetingTask.setVisibleColumns(new Object[] { "taskid",
				"taskDate", "taskDescription","employeeName", "taskStatus", "lastupdateddt",
				"lastupdatedby" });
		tblToolMeetingTask.setColumnHeaders(new String[] { "Ref.Id",
				"Task Date","Task Description", "Owner", "Status",
				"Last Updated Date", "Last Updated By" });
		tblToolMeetingTask.setSelectable(true);
		tblToolMeetingTask.addItemClickListener(new ItemClickListener() {

			public void itemClick(ItemClickEvent event) {

				// TODO Auto-generated method stub
				if (tblToolMeetingTask.isSelected(event.getItemId())) {
					btnEditTask.setEnabled(false);
					

				} else {
					btnEditTask.setEnabled(true);
				}
				resetFields();
				btnSaveTask.setCaption("Save Meeting Task");

			}

		});
		/*
		 * }catch(Exception e){ logger.error(
		 * "error during populate values on the table, The Error is ----->"+e);
		 * }
		 */
	}
	
	// Method for show the details in grid table while search and normal mode
		private void populateAndConfigureActionTable(Long taskId) {

			// try{

			tblMeetingTaskAction.removeAllItems();
			List<MeetingTaskActionDM> usertable = new ArrayList<MeetingTaskActionDM>();

			
				usertable = serviceMeetingTaskActionBean.getTToolMeetingTaskActionList(taskId);
		
			
				beansToolMeetingTaskAction = new BeanItemContainer<MeetingTaskActionDM>(
						MeetingTaskActionDM.class);
				beansToolMeetingTaskAction.addAll(usertable);
			
			tblMeetingTaskAction.setContainerDataSource(beansToolMeetingTaskAction);
	
			tblMeetingTaskAction.setVisibleColumns(new Object[] { "taskActionId",
					"actionDesc", "dependancyDesc","progressPercent", "actionedBy", "lastupdateddt",
					"lastupdatedby" });
			tblMeetingTaskAction.setColumnHeaders(new String[] { "Ref.Id",
					"Action Desc.","Dependancy Desc.", "Progress Percent", "Actioned By",
					"Last Updated Date", "Last Updated By" });
			tblMeetingTaskAction.setSelectable(true);
			tblMeetingTaskAction.addItemClickListener(new ItemClickListener() {

				public void itemClick(ItemClickEvent event) {

					// TODO Auto-generated method stub
					if (tblMeetingTaskAction.isSelected(event.getItemId())) {
						btnEditAction.setEnabled(false);
						

					} else {
						btnEditAction.setEnabled(true);
					}
					resetFieldsAction();
					btnSaveAction.setCaption("Save Task Action");

				}

			});
			/*
			 * }catch(Exception e){ logger.error(
			 * "error during populate values on the table, The Error is ----->"+e);
			 * }
			 */
		}

		// Show the expected value in Grid tblMeetingTaskAction.
		public void setActionTableProperties() {

			beansToolMeetingTaskAction = new BeanItemContainer<MeetingTaskActionDM>(
					MeetingTaskActionDM.class);
			
			tblMeetingTaskAction.addGeneratedColumn("lastupdateddt",
					new DateColumnGenerator());
		
		}
		
	// Show the expected value in Grid tblToolMeetingTask.
	public void setTablePropertiesTask() {

		beansToolMeetingTask = new BeanItemContainer<MeetingTaskDM>(
				MeetingTaskDM.class);
		// beansCompanyLookup.addNestedContainerProperty("lookupid.lookupcode");

		/*
		 * tblCompanyLookup.addGeneratedColumn("lookupid.lookupcode", new
		 * NullCheckColumnGenerator());
		 */
		tblToolMeetingTask.addGeneratedColumn("taskDate",
				new DateFormateColumnGenerator());
		tblToolMeetingTask.addGeneratedColumn("lastupdateddt",
				new DateColumnGenerator());
		// tblCompanyLookup.addGeneratedColumn("lookupstatus", new
		// StatusColumnGenerator());
	}

	void resetFieldsAction()
	{
		tfActionDesc.setComponentError(null);
		tfActionedby.setComponentError(null);
		tfDependancyDesc.setComponentError(null);
		tfProgressPercentage.setComponentError(null);
		
		tfActionDesc.setValue("");
		tfActionedby.setValue("");
		tfDependancyDesc.setValue("");
		tfProgressPercentage.setValue("");
	
		
		
	}
	void resetFieldsTask() {

		tfTaskDesc.setComponentError(null);
		tfPriority.setComponentError(null);
		dfTaskDt.setComponentError(null);
		dfTaskEndDt.setComponentError(null);
		dfRevisionDt.setComponentError(null);
		cbOwner.setComponentError(null);
		
		
		btnSave.setComponentError(null);
		
		
		tfTaskDesc.setValue("");
		tfPriority.setValue("");
		cbOwner.setValue(null);
		dfTaskDt.setValue(null);
		dfTaskEndDt.setValue(null);
		dfRevisionDt.setValue(null);
		cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
		btnSaveTask.setCaption("Save Meeting Task");


		//btnSaveTask.setDescription("Save Meeting Task");
		
	}

	// Display the details after click the edit button
	private void editToolMeetingTask() {

		pnlForm.setVisible(true);
		Item itselect = tblToolMeetingTask.getItem(tblToolMeetingTask.getValue());
		if (itselect != null) {

			MeetingTaskDM editMeetingTaskDM = beansToolMeetingTask.getItem(
					tblToolMeetingTask.getValue()).getBean();

			String stCode = itselect.getItemProperty("taskStatus").getValue()
					.toString();
			cbStatus.setValue(Common.getStatus(stCode));

		
			taskId = (Long)itselect.getItemProperty("taskid")
					.getValue();
			
			tfTaskDesc.setValue(itselect.getItemProperty("taskDescription")
					.getValue().toString());
			
			tfPriority.setValue(itselect.getItemProperty("priority")
					.getValue().toString());
			
			dfTaskDt.setValue((Date)itselect.getItemProperty("taskDate")
					.getValue());
			
			dfTaskEndDt.setValue((Date)itselect.getItemProperty("targetEndDate")
					.getValue());
			
			dfRevisionDt.setValue((Date)itselect.getItemProperty("revisionDate")
					.getValue());
			

			EmployeeDM editChairedBy=editMeetingTaskDM.getOwnerId();
			Collection<?> collEmployee=cbOwner.getItemIds();
			for(Iterator iterator=collEmployee.iterator(); iterator.hasNext();) {
				Object itemid=(Object)iterator.next();
				BeanItem<?> item=(BeanItem<?>) cbOwner.getItem(itemid);
				EmployeeDM employeeId=(EmployeeDM) item.getBean();	
				if (editChairedBy != null && editChairedBy.getEmployeeid().equals(employeeId.getEmployeeid())) {
					cbOwner.setValue(itemid);
					break;
				} else {
					cbOwner.setValue(null);
				}
			}
			
		
			populateAndConfigureActionTable(taskId);
			btnSaveAction.setEnabled(true);

		}

	}
	
	// Display the details after click the edit button
		private void editToolMeetingTaskAction() {

			
			Item itselect = tblMeetingTaskAction.getItem(tblMeetingTaskAction.getValue());
			if (itselect != null) {

			
				
				tfActionDesc.setValue(itselect.getItemProperty("actionDesc")
						.getValue().toString());
				
				tfActionedby.setValue(itselect.getItemProperty("actionedBy")
						.getValue().toString());
				
				tfDependancyDesc.setValue(itselect.getItemProperty("dependancyDesc")
						.getValue().toString());
				
				tfProgressPercentage.setValue(itselect.getItemProperty("progressPercent")
						.getValue().toString());
				
			

			

			}

		}

	// Method for save and update for Tool Meeting details
	private void saveorUpdateToolMeetingTask() {
		boolean valid = false;
		if (tblToolMeetingTask.getValue()!= null) {
			MeetingTaskDM update = beansToolMeetingTask.getItem(
					tblToolMeetingTask.getValue()).getBean();

			if (selectOwner != null) {
				update.setOwnerId(selectOwner);
			} else {
				cbOwner.setComponentError(new UserError("Please Select Staff "));
			}
			

			MeetingDM toolMeetingobj = new MeetingDM();
			toolMeetingobj.setMeetingId(meetingHdrID);
			update.setMeetingId(toolMeetingobj);

			update.setPriority(tfPriority.getValue());
			update.setTaskDescription(tfTaskDesc.getValue());
			update.setTaskDate(dfTaskDt.getValue());
			update.setTargetEndDate(dfTaskEndDt.getValue());
			update.setRevisionDate(dfRevisionDt.getValue());
			update.setLastupdatedby(strLoginUserName);
			update.setLastupdateddt(DateUtils.getcurrentdate());

			StatusDM st = (StatusDM) cbStatus.getValue();
			update.setTaskStatus(st.getCode());

			if (tfTaskDesc.isValid() && dfTaskDt.isValid()
					&& cbOwner.isValid()) {
				serviceMeetingTaskbean.saveAndUpdateDetails(update);
				taskId = update.getTaskid();
				valid = true;
				
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblNotification.setValue(ApplicationConstants.updatedMsg);

				

			}
		}

		else {

			MeetingTaskDM save = new MeetingTaskDM();
			if (selectOwner != null) {
				save.setOwnerId(selectOwner);
			} else {
				cbOwner.setComponentError(new UserError("Please Select Staff "));
			}
			MeetingDM toolMeetingobj = new MeetingDM();
			toolMeetingobj.setMeetingId(meetingHdrID);
			save.setMeetingId(toolMeetingobj);

			save.setPriority(tfPriority.getValue());
			save.setTaskDescription(tfTaskDesc.getValue());
			save.setTaskDate(dfTaskDt.getValue());
			save.setTargetEndDate(dfTaskEndDt.getValue());
			save.setRevisionDate(dfRevisionDt.getValue());
			save.setLastupdatedby(strLoginUserName);
			save.setLastupdateddt(DateUtils.getcurrentdate());

			StatusDM st = (StatusDM) cbStatus.getValue();
			save.setTaskStatus(st.getCode());

			if (tfTaskDesc.isValid() && dfTaskDt.isValid()
					&& cbOwner.isValid()) {
				serviceMeetingTaskbean.saveAndUpdateDetails(save);
				taskId = save.getTaskid();
				valid = true;
				

				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblNotification.setValue(ApplicationConstants.saveMsg);
			}

		}

		if (valid) {
			populateAndConfigureTableTask(meetingHdrID);
			populateAndConfigureActionTable(taskId);
			btnSave.setComponentError(null);
		
			btnSave.setCaption("Save Meeting Task");
			btnSaveAction.setEnabled(true);
		} else {
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
			lblNotification
					.setValue("Save failed, please check the data and try again ");
		}
	}

	// Method for save and update for Tool Meeting details
		private void saveorUpdateToolMeetingTaskAction() {
			boolean valid = false;
			if (tblMeetingTaskAction.getValue() != null) {
				MeetingTaskActionDM update = beansToolMeetingTaskAction.getItem(
						tblMeetingTaskAction.getValue()).getBean();

				
				MeetingTaskDM taskObj = new MeetingTaskDM();
				taskObj.setTaskid(taskId);
					update.setTaskId(taskObj);
				
				update.setActionDesc(tfActionDesc.getValue());
				update.setDependancyDesc(tfDependancyDesc.getValue());
				
				update.setActionedBy(new Long(tfActionedby.getValue()));
				update.setProgressPercent(new Long(tfProgressPercentage.getValue()));
				
				update.setLastupdatedby(strLoginUserName);
				update.setLastupdateddt(DateUtils.getcurrentdate());

				

				if (tfActionDesc.isValid() && tfDependancyDesc.isValid()
						) {
					serviceMeetingTaskActionBean.saveAndUpdateDetails(update);
					valid = true;
				
					lblNotificationIcon.setIcon(new ThemeResource(
							"img/success_small.png"));
					lblNotification.setValue(ApplicationConstants.updatedMsg);

				
				}
			}

			else {

				MeetingTaskActionDM save = new MeetingTaskActionDM();
				MeetingTaskDM taskObj = new MeetingTaskDM();
				taskObj.setTaskid(taskId);
				save.setTaskId(taskObj);
				
				save.setActionDesc(tfActionDesc.getValue());
				save.setDependancyDesc(tfDependancyDesc.getValue());
				
				save.setActionedBy(new Long(tfActionedby.getValue()));
				save.setProgressPercent(new Long(tfProgressPercentage.getValue()));
				
				save.setLastupdatedby(strLoginUserName);
				save.setLastupdateddt(DateUtils.getcurrentdate());

				

				if (tfActionDesc.isValid() && tfDependancyDesc.isValid()
						) {
					serviceMeetingTaskActionBean.saveAndUpdateDetails(save);
					valid = true;
				

					lblNotificationIcon.setIcon(new ThemeResource(
							"img/success_small.png"));
					lblNotification.setValue(ApplicationConstants.saveMsg);
				}

			}

			if (valid) {
				populateAndConfigureTableTask(meetingHdrID);
				populateAndConfigureActionTable(taskId);
				btnSaveAction.setComponentError(null);
				resetFieldsAction();
				btnSaveAction.setEnabled(true);
				btnSaveAction.setCaption("Save Task Action");
			} else {
				lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
				lblNotification
						.setValue("Save failed, please check the data and try again ");
			}
		}

	// Button Click event
	public void buttonClick(ClickEvent event) {

		if (btnAdd == event.getButton()) {
			pnlForm.setVisible(true);
			pnlSearch.setVisible(false);
			pnlTable.setVisible(false);
			resetFields();
			btnAdd.setEnabled(false);
			btnEdit.setEnabled(false);
			populateAndConfigureTable(false);
			hlSaveandCancelButtonLayout.setVisible(true);
			
			lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
			lblFormTitle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			hlTableTitleandCaptionLayout.setVisible(false);
			
			
			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");

			

		} else if (btnCancel == event.getButton()) {
			hlSaveandCancelButtonLayout.setVisible(true);
			pnlSearch.setVisible(true);
			pnlForm.setVisible(false);
			pnlTable.setVisible(true);
			tblToolMeeting.setValue(null);
			resetFields();
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			hlSaveandCancelButtonLayout.setVisible(false);

			lblFormTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName
					+ "</b>&nbsp;::&nbsp;Search");
			lblFormTitle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			populateAndConfigureTable(false);
			pnlTable.setVisible(true);

			

			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			
			
		} else if (btnSearch == event.getButton()) {
			populateAndConfigureTable(true);
			if (totalnoofrecords == 0) {
				lblNotification.setValue("No Records found");
			} else {
				lblNotificationIcon.setIcon(null);
				lblNotification.setValue("");
			}
		}

		else if (btnEdit == event.getButton()) {
			pnlForm.setVisible(true);
			pnlTable.setVisible(false);
			pnlSearch.setVisible(false);
			resetFields();
			hlSaveandCancelButtonLayout.setVisible(true);
			// try{
			editToolMeeting();
			/*
			 * } catch (Exception e) {
			 * logger.error("Error thorws in editTToolMeetinglookup() function--->" +
			 * e); }
			 */
			btnSave.setCaption("Update");
		//	btnSave.setDescription("Update Tool Meeting");

			hlTableTitleandCaptionLayout.setVisible(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Modify");
			lblFormTitle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			toolMeetingTab.getTab(vlToolMeeting).setEnabled(true);
			toolMeetingTab.getTab(vlToolMeetingTasks).setEnabled(true);
			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			
		}
		if (btnSave == event.getButton()) {
			// try{
			saveorUpdateToolMeeting();
			/*
			 * } catch (Exception e) {
			 * logger.info("Error on saveorUpdateCompanyLookup() function--->" +
			 * e); }
			 */
			pnlTable.setVisible(false);
		}

		if (btnReset == event.getButton()) {
			resetSearchFields();
			populateAndConfigureTable(false);
			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
		} 
		

		if (btnCancelTask == event.getButton()) {
			
			resetFieldsTask();
			btnEditTask.setEnabled(false);
			populateAndConfigureActionTable(null);
			btnSaveAction.setEnabled(false);
			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			//hlSaveandCancelButtonLayout.setVisible(false);

		}
		else if (btnEditTask == event.getButton()) {
			
			resetFieldsTask();
			// try{
			editToolMeetingTask();
			/*
			 * } catch (Exception e) {
			 * logger.error("Error thorws in editcompanylookup() function--->" +
			 * e); }
			 */
			btnSaveTask.setCaption("Update Task");
			//btnSaveTask.setDescription("Update Meeting Task");

				
			btnEditTask.setEnabled(false);
			
			

			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			
		}
		if (btnSaveTask == event.getButton()) {
			// try{
			saveorUpdateToolMeetingTask();
			/*
			 * } catch (Exception e) {
			 * logger.info("Error on saveorUpdateCompanyLookup() function--->" +
			 * e); }
			 */
		}
		
		if (btnSaveAction == event.getButton()) {
			// try{
			saveorUpdateToolMeetingTaskAction();
			/*
			 * } catch (Exception e) {
			 * logger.info("Error on saveorUpdateCompanyLookup() function--->" +
			 * e); }
			 */
		}
		
		if (btnEditAction == event.getButton()) {
			// try{
			editToolMeetingTaskAction();
			
			btnSaveAction.setCaption("Update Action");
			//btnSaveAction.setDescription("Update Task Action");
			/*
			 * } catch (Exception e) {
			 * logger.info("Error on saveorUpdateCompanyLookup() function--->" +
			 * e); }
			 */
			
			

			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
		}
		if (btnCancelAction == event.getButton()) {
			resetFieldsAction();
			

			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
		}
		else if(btnBack==event.getButton())
		{

			resetFields();
			pnlForm.setVisible(false);
			pnlSearch.setVisible(true);
			pnlTable.setVisible(true);
			btnEdit.setEnabled(false);
			
			hlSaveandCancelButtonLayout.setVisible(false);
			hlTableTitleandCaptionLayout.setVisible(true);
			lblFormTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName
					+ "</b>&nbsp;::&nbsp;Search");

			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			lblAddEdit.setValue("");
			lblFormTitle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			tblToolMeeting.setVisible(true);
			hlTableTitleandCaptionLayout.setVisible(true);

		
			
			
			//exportTableDate();
			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			
		}
		
	}
	
	
	
}
