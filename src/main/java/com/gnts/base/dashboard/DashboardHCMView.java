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
import com.gnts.hcm.domain.txn.JobCandidateDM;
import com.gnts.hcm.rpt.Payslip;
import com.gnts.hcm.service.txn.JobCandidateService;
import com.gnts.hcm.txn.AttendenceProc;
import com.gnts.hcm.txn.EmployeeAttendence;
import com.gnts.hcm.txn.JobCandidate;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.service.txn.MaterialStockService;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.txn.SmsEnquiry;
import com.gnts.stt.dsn.domain.txn.OutpassDM;
import com.gnts.stt.dsn.service.txn.OutpassService;
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
	private OutpassService serviceoutpass = (OutpassService) SpringContextHelper
			.getBean("outpass");
	private static final long serialVersionUID = 1L;
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
	private Table tblMstScrSrchRslt = new Table();
	
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
		custom.addComponent(tblMstScrSrchRslt, "stockDetails");
		
		tblMstScrSrchRslt.setHeight("300px");
		loadStockDetails();
		notificationsWindow.close();
	}
	
	private void loadStockDetails() {
		try {
		//logger.info("Company ID : " + companyId + " | User Name : > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			BeanItemContainer<OutpassDM> beanoutpass = new BeanItemContainer<OutpassDM>(
					OutpassDM.class);
			beanoutpass.addAll(serviceoutpass.getOutpassList(null, null, null, null, "Active"));
			tblMstScrSrchRslt.setContainerDataSource(beanoutpass);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "passDate", "employeeId",
					"place","vehicle", "totalTime","totalKM" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Date", "Name","Place","Vehicle",
					"Time","KM" });
			tblMstScrSrchRslt.setColumnWidth("passDate", 150);
			tblMstScrSrchRslt.setColumnWidth("employeeId", 75);
			tblMstScrSrchRslt.setColumnWidth("place", 70);
			tblMstScrSrchRslt.setColumnWidth("vehicle", 60);
			tblMstScrSrchRslt.setColumnWidth("totalTime", 45);
			tblMstScrSrchRslt.setColumnWidth("totalKM", 40);
			tblMstScrSrchRslt.setHeightUndefined();
			/*tblMstScrSrchRslt.addGeneratedColumn("materialName", new ColumnGenerator() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					@SuppressWarnings("unchecked")
					BeanItem<OutpassDM> item = (BeanItem<OutpassDM>) source.getItem(itemId);
					OutpassDM emp = (OutpassDM) item.getBean();
					MaterialDM material = serviceoutpass.getMaterialList(emp.getMaterialId(), null, null, null, null,
							null, null, null, null, "P").get(0);
					System.out.println("material.getReorderLevel()--->" + material.getReorderLevel());
					if (material.getReorderLevel() == null || material.getReorderLevel() == emp.getEffectiveStock()) {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#EC9E20;font-size:12px'>"
										+ emp.getMaterialName() + "</h1>", ContentMode.HTML);
					} else if (material.getReorderLevel() > emp.getEffectiveStock()) {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#E26666;font-size:12px'>"
										+ emp.getMaterialName() + "</h1>", ContentMode.HTML);
					} else if (material.getReorderLevel() < emp.getEffectiveStock()) {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#6CD4BD;font-size:12px'>"
										+ emp.getMaterialName() + "</h1>", ContentMode.HTML);
					} else {
						return new Label(
								"<h1 style='padding-left: 9px;padding-right: 9px;border-radius: 9px;background-color:#E26666;font-size:12px'>"
										+ emp.getMaterialName() + "</h1>", ContentMode.HTML);
					}
				}
			});*/
		}
		catch (Exception e) {
			e.printStackTrace();
			//logger.info("loadSrchRslt-->" + e);
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
			UI.getCurrent().getSession().setAttribute("screenName", "Attendenece Process");
			new AttendenceProc();
		}
		if (event.getButton() == btnEmpLeave) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Attendenece Process");
			new AttendenceProc();
		}
		if (event.getButton() == btnOutpass) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Attendenece Process");
			new AttendenceProc();
		}
		if (event.getButton() == btnVisitPass) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Attendenece Process");
			new AttendenceProc();
		}
		if (event.getButton() == btnCourier) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Attendenece Process");
			new AttendenceProc();
		}
		if (event.getButton() == btnPhoneReg) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Attendenece Process");
			new AttendenceProc();
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
		try{
		notificationsWindow.close();
		}catch(Exception e){}
		}
}