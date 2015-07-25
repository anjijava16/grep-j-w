package com.gnts.base.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import oracle.sql.DATE;
import org.omg.CORBA.Current;
import com.gnts.asm.domain.mst.AssetCategoryDM;
import com.gnts.base.mst.Employee;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.EmployeeLeaveDM;
import com.gnts.hcm.domain.txn.JobCandidateDM;
import com.gnts.hcm.rpt.Payslip;
import com.gnts.hcm.service.txn.EmployeeLeaveService;
import com.gnts.hcm.service.txn.JobCandidateService;
import com.gnts.hcm.serviceimpl.txn.EmployeeLeaveServiceImpl;
import com.gnts.hcm.txn.AttendenceProc;
import com.gnts.hcm.txn.Courier;
import com.gnts.hcm.txn.EmployeeAttendence;
import com.gnts.hcm.txn.EmployeeLeave;
import com.gnts.hcm.txn.JobCandidate;
import com.gnts.hcm.txn.JobVaccancy;
import com.gnts.hcm.txn.Outpass;
import com.gnts.hcm.txn.PhoneCallRegister;
import com.gnts.hcm.txn.VisitorPass;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.service.txn.MaterialStockService;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.txn.SmsEnquiry;
import com.gnts.stt.dsn.domain.txn.OutpassDM;
import com.gnts.stt.dsn.domain.txn.PhoneRegDM;
import com.gnts.stt.dsn.domain.txn.VisitPassDM;
import com.gnts.stt.dsn.service.txn.OutpassService;
import com.gnts.stt.dsn.service.txn.PhoneRegService;
import com.gnts.stt.dsn.service.txn.VisitPassService;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class DashboardHCMView implements ClickListener {
	/**
	 * 
	 */
	private OutpassService serviceoutpass = (OutpassService) SpringContextHelper.getBean("outpass");
	private VisitPassService servicevisitpass = (VisitPassService) SpringContextHelper.getBean("visitPass");
	private EmployeeLeaveService serviceemployeeleave = (EmployeeLeaveService) SpringContextHelper.getBean("EmployeeLeave");
	private PhoneRegService servicephonereg = (PhoneRegService) SpringContextHelper.getBean("phoneregister");
	static final long serialVersionUID = 1L;
	private Label lblDashboardTitle;
	VerticalLayout clMainLayout;
	HorizontalLayout hlHeader;
	private Button btnEmployeeCount = new Button("100", this);
	private Button btnEmpAtten = new Button("10", this);
	private Button btnAttenProcess = new Button("10", this);
	private Button btnPayslip = new Button("10", this);
	private Button btnJobVacancy = new Button("10", this);
	private Button btnEmpLeave = new Button("10", this);
	private Button btnOutpass = new Button("10", this);
	private Button btnVisitPass = new Button("10", this);
	private Button btnCourier = new Button("10", this);
	private Button btnPhoneReg = new Button("10", this);
	private Button btnNotify = new Button();
	private Table tblOutpass = new Table();
	private Table tblVisitpass = new Table();
	private Table tblEmplLeave = new Table();
	private Table tblPhoneReg = new Table();
	private Window notificationsWindow;
	private JobCandidateService jobcandidateService = (JobCandidateService) SpringContextHelper.getBean("JobCandidate");
	
	public DashboardHCMView() {
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		clMainLayout.setImmediate(true);
		clMainLayout.addLayoutClickListener(new LayoutClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void layoutClick(LayoutClickEvent event) {
				loadNotificWindow();
				// TODO Auto-generated method stub
			}
		});
		hlHeader.setImmediate(true);
		hlHeader.addLayoutClickListener(new LayoutClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void layoutClick(LayoutClickEvent event) {
				loadNotificWindow();
				// TODO Auto-generated method stub
			}
		});
		btnNotify.setHtmlContentAllowed(true);
		hlHeader.removeAllComponents();
		CustomLayout custom = new CustomLayout("dashhcm");
		btnEmployeeCount.setStyleName("borderless-colored");
		btnEmpAtten.setStyleName("borderless-colored");
		btnPayslip.setStyleName("borderless-colored");
		btnAttenProcess.setStyleName("borderless-colored");
		btnJobVacancy.setStyleName("borderless-colored");
		btnEmpLeave.setStyleName("borderless-colored");
		btnOutpass.setStyleName("borderless-colored");
		btnVisitPass.setStyleName("borderless-colored");
		btnCourier.setStyleName("borderless-colored");
		btnPhoneReg.setStyleName("borderless-colored");
		btnNotify.setIcon(new ThemeResource("img/download.png"));
		VerticalLayout root = new VerticalLayout();
		root.addComponent(buildHeader());
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Human Capital Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		hlHeader.addComponent(btnNotify);
		hlHeader.setComponentAlignment(btnNotify, Alignment.TOP_RIGHT);
		clMainLayout.addComponent(custom);
		custom.addComponent(btnEmployeeCount, "employeecount");
		custom.addComponent(btnEmpAtten, "empattencount");
		custom.addComponent(btnPayslip, "payslipcount");
		custom.addComponent(btnAttenProcess, "attenproccount");
		custom.addComponent(btnJobVacancy, "jobvacancy");
		custom.addComponent(btnEmpLeave, "empleave");
		custom.addComponent(btnOutpass, "outpass");
		custom.addComponent(btnVisitPass, "visitpass");
		custom.addComponent(btnCourier, "courier");
		custom.addComponent(btnPhoneReg, "phonereg");
		custom.addComponent(tblOutpass, "tableoutpass");
		custom.addComponent(tblVisitpass, "tablevisitorpass");
		custom.addComponent(tblEmplLeave, "tableempleave");
		custom.addComponent(tblPhoneReg, "tablephonereg");
		tblOutpass.setHeight("300px");
		tblVisitpass.setHeight("300px");
		tblEmplLeave.setHeight("300px");
		tblPhoneReg.setHeight("300px");
		loadOutpassTable();
		loadVisitTable();
		loadEmplLeave();
		loadPhoneReg();
		try{notificationsWindow.close();}catch(Exception e){}
	}
	
	private void loadEmplLeave() {
		try {
			// logger.info("Company ID : " + companyId + " | User Name : > " + "Loading Search...");
			tblEmplLeave.removeAllItems();
			BeanItemContainer<EmployeeLeaveDM> beanempleave = new BeanItemContainer<EmployeeLeaveDM>(EmployeeLeaveDM.class);
			beanempleave.addAll(serviceemployeeleave.getempleaveList(null, null, null, null, null));
			tblEmplLeave.setContainerDataSource(beanempleave);
			tblEmplLeave.setVisibleColumns(new Object[] { "employeeid", "leavetypeid", "noofdays", "leavereason", "appmgr",
					"empleavestatus" });
			tblEmplLeave.setColumnHeaders(new String[] { "Name", "Type", "Days", "Reason", "Approved BY", "Status" });
			tblEmplLeave.setColumnWidth("employeeid", 80);
			tblEmplLeave.setColumnWidth("leavetypeid", 75);
			tblEmplLeave.setColumnWidth("noofdays", 70);
			tblEmplLeave.setColumnWidth("leavereason", 60);
			tblEmplLeave.setColumnWidth("appmgr", 50);
			tblEmplLeave.setColumnWidth("empleavestatus", 50);
			tblEmplLeave.setHeightUndefined();
		}
		catch (Exception e) {
			e.printStackTrace();
			// logger.info("loadSrchRslt-->" + e);
		}
	}
	private void loadPhoneReg() {
		try {
			// logger.info("Company ID : " + companyId + " | User Name : > " + "Loading Search...");
			tblPhoneReg.removeAllItems();
			BeanItemContainer<PhoneRegDM> beanphonereg = new BeanItemContainer<PhoneRegDM>(PhoneRegDM.class);
			beanphonereg.addAll(servicephonereg.getPhoneRegList(null, null, null, null, null));
			tblPhoneReg.setContainerDataSource(beanphonereg);
			tblPhoneReg.setVisibleColumns(new Object[] { "callDate", "callType", "companyName", "employeeId", "phoneNumber",
					"interNo" });
			tblPhoneReg.setColumnHeaders(new String[] { "Date", "Type", "From", "To", "Number", "Intercom" });
			tblPhoneReg.setColumnWidth("callDate", 75);
			tblPhoneReg.setColumnWidth("callType", 50);
			tblPhoneReg.setColumnWidth("companyName", 100);
			tblPhoneReg.setColumnWidth("employeeId", 60);
			tblPhoneReg.setColumnWidth("phoneNumber", 80);
			tblPhoneReg.setColumnWidth("interNo", 40);
			tblPhoneReg.setHeightUndefined();
		}
		catch (Exception e) {
			e.printStackTrace();
			// logger.info("loadSrchRslt-->" + e);
		}
	}
	private void loadOutpassTable() {
		try {
			// logger.info("Company ID : " + companyId + " | User Name : > " + "Loading Search...");
			tblOutpass.removeAllItems();
			BeanItemContainer<OutpassDM> beanoutpass = new BeanItemContainer<OutpassDM>(OutpassDM.class);
			beanoutpass.addAll(serviceoutpass.getOutpassList(null, null, null, null, "Active"));
			tblOutpass.setContainerDataSource(beanoutpass);
			tblOutpass.setVisibleColumns(new Object[] { "passDate", "firstname", "place", "vehicle", "totalTime",
					"totalKM" });
			tblOutpass.setColumnHeaders(new String[] { "Date", "Name", "Place", "Vehicle", "Time", "KM" });
			tblOutpass.setColumnWidth("passDate", 75);
			tblOutpass.setColumnWidth("firstname", 150);
			tblOutpass.setColumnWidth("place", 70);
			tblOutpass.setColumnWidth("vehicle", 60);
			tblOutpass.setColumnWidth("totalTime", 45);
			tblOutpass.setColumnWidth("totalKM", 40);
			tblOutpass.setHeightUndefined();
		}
		catch (Exception e) {
			e.printStackTrace();
			// logger.info("loadSrchRslt-->" + e);
		}
	}
	
	private void loadVisitTable() {
		try {
			// logger.info("Company ID : " + companyId + " | User Name : > " + "Loading Search...");
			tblVisitpass.removeAllItems();
			BeanItemContainer<VisitPassDM> beanvisitpass = new BeanItemContainer<VisitPassDM>(VisitPassDM.class);
			beanvisitpass.addAll(servicevisitpass.getVisitPasList(null,null,null,null,null));
			tblVisitpass.setContainerDataSource(beanvisitpass);
			tblVisitpass.setVisibleColumns(new Object[] { "visitDate", "visitorName", "contactNo", "mateFLow",
					"inTime", "totalTime" });
			tblVisitpass
					.setColumnHeaders(new String[] { "Date", "Name", "Number", "Material", "Time In", "Total Time" });
			tblVisitpass.setColumnWidth("visitDate", 75);
			tblVisitpass.setColumnWidth("visitorName", 140);
			tblVisitpass.setColumnWidth("contactNo", 80);
			tblVisitpass.setColumnWidth("mateFLow", 60);
			tblVisitpass.setColumnWidth("inTime", 45);
			tblVisitpass.setColumnWidth("totalTime", 40);
			tblVisitpass.setHeightUndefined();
		}
		catch (Exception e) {
			e.printStackTrace();
			// logger.info("loadSrchRslt-->" + e);
		}
	}
	
	private Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.addStyleName("viewheader");
		header.setSpacing(true);
		btnNotify = buildNotificationsButton();
		HorizontalLayout tools = new HorizontalLayout(btnNotify);
		tools.setSpacing(true);
		tools.addStyleName("toolbar");
		return header;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnEmployeeCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Employee");
			new Employee();
		}
		if (event.getButton() == btnEmpAtten) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Employee Attendence");
			new EmployeeAttendence();
		}
		if (event.getButton() == btnPayslip) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Payslip");
			new Payslip();
		}
		if (event.getButton() == btnAttenProcess) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Attendenece Process");
			new AttendenceProc();
		}
		if (event.getButton() == btnJobVacancy) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Job Vaccancy");
			new JobVaccancy();
		}
		if (event.getButton() == btnEmpLeave) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Employee Leave");
			new EmployeeLeave();
		}
		if (event.getButton() == btnOutpass) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Outpass");
			new Outpass();
		}
		if (event.getButton() == btnVisitPass) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Visitor Pass");
			new VisitorPass();
		}
		if (event.getButton() == btnCourier) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Courier");
			new Courier();
		}
		if (event.getButton() == btnPhoneReg) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Phone Register");
			new PhoneCallRegister();
		}
	}
	
	/*
	 * btnJobVacancy btnEmpLeave btnOutpass btnVisitPass btnCourier btnPhoneReg
	 */
	// Notification
	private Button buildNotificationsButton() {
		btnNotify.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(final ClickEvent event) {
				openNotificationsPopup(event);
			}
		});
		return btnNotify;
	}
	
	private void openNotificationsPopup(final ClickEvent event) {
		VerticalLayout notificationsLayout = new VerticalLayout();
		notificationsLayout.setMargin(true);
		notificationsLayout.setSpacing(true);
		final Panel panel = new Panel("Notifications");
		notificationsLayout.addComponent(panel);
		List<JobCandidateDM> jobcandidateList = new ArrayList<JobCandidateDM>();
		JobCandidateDM jobcandidatedm = new JobCandidateDM();
		jobcandidateList.add(jobcandidatedm);
		jobcandidateList = jobcandidateService.getJobCandidateList(null, null, null, null, "Active");
		FormLayout fmlayout = new FormLayout();
		Date dttodaydt = new Date();
		VerticalLayout hrLayout = new VerticalLayout();
		for (JobCandidateDM n : jobcandidateList) {
			if (DateUtils.datetostring(dttodaydt).compareTo(DateUtils.datetostring(n.getDoa())) == 0) {
				hrLayout.addStyleName("notification-item");
				Label titleLabel = new Label("\n"
						+ "<table style=width:100%><tr><td><small>Status : </small><font color=blue><font size=4>"
						+ n.getStatus() + "</font></font color></td><td><small>Date : </small><font color=green>"
						+ DateUtils.datetostring(n.getDoa()) + "</font></td></tr></table>", ContentMode.HTML);
				// Label titleLabel = new Label(n.getEnquiryStatus());
				Label titleLabel1 = new Label("<small>Candidate Name </small><font color=green>" + n.getFirstName()
						+ " " + n.getLastName() + "</font>", ContentMode.HTML);
				Label titleLabel2 = new Label("<small>Concatc No. : </small><font color=green>" + n.getContactNo()
						+ "</font>", ContentMode.HTML);
				Label titleLabel3 = new Label("<small>Job Title : </small><font color=red>" + n.getJobtitle()
						+ "</font>", ContentMode.HTML);
				Label titleLabel5 = new Label("<small>Job Title : </small><font color=red>" + n.getVaccancyid()
						+ "</font>", ContentMode.HTML);
				Label titleLabel4 = new Label("<HR size=3 color=red>", ContentMode.HTML);
				titleLabel.addStyleName("notification-title");
				fmlayout.addComponents(titleLabel);
				fmlayout.addComponents(titleLabel1);
				fmlayout.addComponents(titleLabel2);
				fmlayout.addComponent(titleLabel3);
				fmlayout.addComponent(titleLabel5);
				fmlayout.addComponent(titleLabel4);
				hrLayout.addComponent(fmlayout);
			}
		}
		notificationsLayout.addComponent(hrLayout);
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth("100%");
		Button showAll = new Button("View All Notifications", new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(final ClickEvent event) {
				clMainLayout.removeAllComponents();
				hlHeader.removeAllComponents();
				new JobCandidate();
				notificationsWindow.close();
			}
		});
		showAll.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		showAll.addStyleName(ValoTheme.BUTTON_SMALL);
		footer.addComponent(showAll);
		footer.setComponentAlignment(showAll, Alignment.TOP_CENTER);
		notificationsLayout.addComponent(footer);
		if (notificationsWindow == null) {
			notificationsWindow = new Window();
			notificationsWindow.setWidthUndefined();
			notificationsWindow.addStyleName("notifications");
			notificationsWindow.setClosable(false);
			notificationsWindow.setResizable(false);
			notificationsWindow.setDraggable(true);
			notificationsWindow.setCloseShortcut(KeyCode.ESCAPE, null);
			notificationsWindow.setContent(notificationsLayout);
			notificationsWindow.setHeightUndefined();
		}
		if (!notificationsWindow.isAttached()) {
			notificationsWindow.setPositionX(event.getClientX() - 200);
			notificationsWindow.setPositionY(event.getClientY());
			notificationsWindow.setHeight("400");
			notificationsWindow.setWidth("300");
			UI.getCurrent().addWindow(notificationsWindow);
			notificationsWindow.focus();
		} else {
			notificationsWindow.close();
		}
	}
	
	private void loadNotificWindow() {
		// TODO Auto-generated method stub
		try {
			notificationsWindow.close();
		}
		catch (Exception e) {
		}
	}
}