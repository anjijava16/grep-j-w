/**
 * File Name 		: Login.java 
 * Description 		: this class is used for login screen 
 * Author 			: P Sekhar
 * Date 			: Feb 19, 2014
 * Modification 	:
 * Modified By 		: SOUNDAR C 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 *  Version         Date           Modified By             Remarks
 *  0.1          Mar 10 2014       P Sekhar          get employee detail in employee based on user id
 *  0.2          Mar 14 2014       P Sekhar          set the module id in session.
 *  0.3          Mar 15 2014       P Sekhar          set the employee id and department id in session 

 */
package com.gnts.base;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import javax.servlet.annotation.WebServlet;
import org.apache.log4j.Logger;
import com.gnts.asm.txn.AssetComplaintRegister;
import com.gnts.base.domain.mst.AppScreensDM;
import com.gnts.base.domain.mst.AppScreensMenuDM;
import com.gnts.base.domain.mst.CompanyDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.UserDM;
import com.gnts.base.domain.txn.UserLoginDM;
import com.gnts.base.mst.Dashboard;
import com.gnts.base.service.mst.AppScreensService;
import com.gnts.base.service.mst.CompanyService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.ParameterService;
import com.gnts.base.service.mst.UserRolesService;
import com.gnts.base.service.mst.UserService;
import com.gnts.base.service.rpt.AuditRecordsService;
import com.gnts.base.service.txn.UserLoginService;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.txn.Indent;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("gerp")
@Title("gERP")
@SuppressWarnings("serial")
@PreserveOnRefresh
public class Login extends UI implements ItemClickListener, MouseEvents.ClickListener {
	private HorizontalLayout hlLoginLayout = new HorizontalLayout();
	private VerticalLayout vlRoot = new VerticalLayout();
	private UserService serviceUser = null;
	private AppScreensService serviceAppScreen = null;
	private UserLoginService serviceUserLogin = null;
	private ParameterService serviceParam = null;
	private AuditRecordsService serviceAuditRec = null;
	private UserRolesService serviceUserRoles = null;
	private EmployeeService serviceEmployee = null;
	private CompanyService serviceCompany = null;
	private CssLayout clContent = new CssLayout();
	private VerticalLayout clArgumentLayout = new VerticalLayout();
	private CssLayout clHelpLayout = new CssLayout();
	private VerticalLayout vlAddSingIn;
	private VerticalLayout vlLocal;
	private String loginuserName = "", strSystemUser, userFullName = "";
	private Date passwordExDt = null;
	private VerticalLayout vlSingIn = new VerticalLayout();
	private Label lblExpand, lblCollapse, lblerror, lblPasswordError;
	private Label lblLastLogedin = new Label();
	private Label lblUserTimeZone = new Label();
	private Label lblCurrentScreenName = new Label("Dashboard");
	private VerticalLayout vlErrorpanel = new VerticalLayout();
	private HorizontalLayout hlFooterLayout = new HorizontalLayout();
	private Label lblCompanyName = new Label();
	private String sreenName, companyCode, strCompanyName;
	private boolean blIsEditMode = false;
	private VerticalLayout vlTreeLayout;
	private VerticalLayout vlPinLayout;
	private String sessionId, clientIP, adjustTime;
	private TextField tfUsername;
	private ComboBox cbSearchScreenCode = new ComboBox();
	private PasswordField pfPassword, pfPreviesePass, pfNewPassword, pfConformPassword;
	private List<AppScreensDM> appScreenList = null;
	private List<AppScreensMenuDM> menuList = null;
	private HorizontalLayout hlFooterMiddle, hlFooterRight, hlTitle;
	private HorizontalLayout hlCollapse, hlExpand, collandexpndhl, hlLine, hlScreenName, hlHeader, hlTreeMenu;
	private VerticalLayout vlLine, vlPasswordLayout;
	private Image imgExpand, imgCollapse, imgPin, imgUnpin, imgGlob, imgProfile, imgHelp, imgCustomer, imgSingOut,
			imgMonitor, imgFavorite, imgAssetIssue, imgIndent;
	private Tree treeMenu;
	private String versionVar = "BS_APPVER";
	private String copyrightVar = "BS_APPCPR";
	private String paramRefVersion;
	private String paramRefCopyright;
	private Long loginCompanyId, userId, userLoginId, roleId, branchId, appScreenId, moduleId, employeeid, deptId,
			quoteId, enquiryId, timezoneId, currenyId, countryid;
	private String currencysymbol;
	private String systemUser, dashboardview;
	private Logger logger = Logger.getLogger(Login.class.getName());
	private MenuBar mbFavarotise;
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = Login.class, widgetset = "com.gnts.base.AppWidgetSet")
	public static class Servlet extends VaadinServlet {}
	
	public CssLayout layout = new CssLayout();
	public CssLayout mainview = new CssLayout();
	
	@Override
	protected void init(VaadinRequest request) {
		try {
			new SpringContextHelper();
			serviceUser = (UserService) SpringContextHelper.getBean("user");
			serviceUserLogin = (UserLoginService) SpringContextHelper.getBean("userLogin");
			serviceParam = (ParameterService) SpringContextHelper.getBean("parameter");
			serviceAppScreen = (AppScreensService) SpringContextHelper.getBean("appScreens");
			serviceAuditRec = (AuditRecordsService) SpringContextHelper.getBean("auditRecords");
			serviceUserRoles = (UserRolesService) SpringContextHelper.getBean("userRoles");
			serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
			serviceCompany = (CompanyService) SpringContextHelper.getBean("companyBean");
			setLocale(Locale.US);
			setContent(vlRoot);
			vlRoot.setSizeFull();
			Label bg = new Label();
			bg.setSizeUndefined();
			bg.addStyleName("login-bg");
			buildLoginView(false);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Longin screen Init method" + e);
		}
	}
	
	private void buildLoginView(boolean exit) {
		if (exit) {
			vlRoot.removeAllComponents();
		}
		/*
		 * HelpOverlay w = helpManager .addOverlay("","",null ); w.center(); addWindow(w);
		 */
		logger.info("fn_buildLoginView() callloginCompanyIding");
		addStyleName("login");
		Random rand = new Random();
		int randomNum = rand.nextInt((20 - 1) + 1) + 1;
		vlAddSingIn = new VerticalLayout();
		vlAddSingIn.setSizeFull();
		vlAddSingIn.addStyleName("login-layout" + randomNum);
		vlRoot.addComponent(vlAddSingIn);
		// vlAddSingIn.addStyleName("backgroundimage");
		final VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.addStyleName("login-panel");
		loginPanel.setWidth("450px");
		HorizontalLayout labels = new HorizontalLayout();
		labels.setWidth("100%");
		labels.setMargin(true);
		labels.addStyleName("labels");
		loginPanel.addComponent(labels);
		Label welcome = new Label("Welcome");
		welcome.setSizeUndefined();
		welcome.addStyleName("h4");
		labels.addComponent(welcome);
		labels.setComponentAlignment(welcome, Alignment.MIDDLE_LEFT);
		Label title = new Label("gERP");
		title.setSizeUndefined();
		title.addStyleName("h2");
		title.addStyleName("light");
		labels.addComponent(title);
		labels.setComponentAlignment(title, Alignment.MIDDLE_RIGHT);
		HorizontalLayout fields = new HorizontalLayout();
		fields.setSpacing(true);
		fields.setMargin(true);
		fields.addStyleName("fields");
		tfUsername = new TextField("Username");
		tfUsername.focus();
		fields.addComponent(tfUsername);
		tfUsername.addStyleName("whitefont");
		tfUsername.setValue("superuser");
		pfPassword = new PasswordField("Password");
		pfPassword.addStyleName("whitefont");
		pfPassword.setValue("gnts");
		fields.addComponent(pfPassword);
		final Button signin = new Button("Sign In");
		signin.addStyleName("default");
		fields.addComponent(signin);
		fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);
		final ShortcutListener enter = new ShortcutListener("Sign In", KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				signin.click();
			}
		};
		signin.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				verfiyLoginUser();
			}
		});
		signin.addShortcutListener(enter);
		loginPanel.addComponent(fields);
		loginPanel.addComponent(vlErrorpanel);
		Label lblCompanyname = new Label(/**
		 * "
		 * <p align=\"right\">
		 * <font size=\"1\"color=\"white\"> Copyright © 2013 GNTS Technologies
		 * </p>
		 * &nbsp;&nbsp;", ContentMode.HTML
		 */
		);
		lblCompanyname.setWidth("100%");
		lblCompanyname.setHeight("25px");
		loginPanel.addComponent(lblCompanyname);
		loginPanel.setComponentAlignment(lblCompanyname, Alignment.BOTTOM_RIGHT);
		vlAddSingIn.addComponent(loginPanel);
		vlAddSingIn.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
		lblerror = new Label();
		lblerror.addStyleName("error");
		lblerror.setSizeUndefined();
		lblerror.addStyleName("light");
		// Add animation
		lblerror.addStyleName("v-animate-reveal");
		lblerror.setValue("");
	}
	
	private void buildMainView() {
		removeStyleName("login");
		vlRoot.removeAllComponents();
		vlRoot.setVisible(true);
		// add fields to header layout
		hlHeader = new HorizontalLayout();
		hlHeader.setWidth("100%");
		hlHeader.setHeight("36px");
		hlHeader.addStyleName("topbarone");
		imgGlob = new Image(null, new ThemeResource("img/globe.png"));
		imgGlob.setWidth("40px");
		imgGlob.setHeight("40px");
		imgGlob.setDescription("Home");
		imgGlob.addClickListener(this);
		lblCurrentScreenName.setValue("&nbsp;&nbsp;Dashboard");
		lblCurrentScreenName.setWidth("70%");
		HorizontalLayout logoHl = new HorizontalLayout();
		lblCompanyName.setContentMode(ContentMode.HTML);
		logoHl.addComponent(imgGlob);
		logoHl.setComponentAlignment(imgGlob, Alignment.MIDDLE_LEFT);
		logoHl.addComponent(lblCompanyName);
		logoHl.setComponentAlignment(lblCompanyName, Alignment.MIDDLE_LEFT);
		hlHeader.addComponent(logoHl);
		hlHeader.setComponentAlignment(logoHl, Alignment.MIDDLE_LEFT);
		HorizontalLayout userLayout = new HorizontalLayout();
		hlHeader.addComponent(userLayout);
		HorizontalLayout userLayout1 = new HorizontalLayout();
		userLayout1.setHeight("36px");
		imgProfile = new Image(null, new ThemeResource("img/setting.png"));
		imgProfile.setWidth("36px");
		imgProfile.setHeight("36px");
		imgProfile.setDescription("Settings");
		imgProfile.addClickListener(this);
		imgHelp = new Image(null, new ThemeResource("img/information.png"));
		imgHelp.setWidth("36px");
		imgHelp.setHeight("36px");
		imgHelp.setDescription("Help");
		imgHelp.addClickListener(this);
		imgCustomer = new Image(null, new ThemeResource("img/customerservice.png"));
		imgCustomer.setWidth("36px");
		imgCustomer.setHeight("36px");
		imgCustomer.setDescription("Customer Service");
		/**
		 * add menubar in HorizontalLayout
		 */
		mbFavarotise = new MenuBar();
		imgFavorite = new Image(null, new ThemeResource("img/favorite.png"));
		imgFavorite.setDescription("Favorites");
		imgFavorite.setWidth("22px");
		imgFavorite.setHeight("22px");
		imgFavorite.addClickListener(this);
		imgAssetIssue = new Image(null, new ThemeResource("img/customerservice.png"));
		imgAssetIssue.setDescription("Raise Ticket");
		imgAssetIssue.setWidth("36px");
		imgAssetIssue.setHeight("36px");
		imgAssetIssue.addClickListener(this);
		imgIndent = new Image(null, new ThemeResource("img/icon_orders.png"));
		imgIndent.setDescription("Raise Indent");
		imgIndent.setWidth("22px");
		imgIndent.setHeight("22px");
		imgIndent.addClickListener(this);
		Label userName = new Label(loginuserName);
		imgSingOut = new Image(null, new ThemeResource("img/signout.png"));
		imgSingOut.setDescription("Sign Out");
		imgSingOut.setWidth("36px");
		imgSingOut.setHeight("36px");
		imgSingOut.addClickListener(this);
		userName.setContentMode(ContentMode.HTML);
		userName.setValue("<font size=\"2\" color=\"white\">" + userFullName + "</font>");
		userName.setSizeUndefined();
		userLayout.addComponent(userName);
		userLayout.addComponent(imgProfile);
		userLayout.setComponentAlignment(userName, Alignment.MIDDLE_CENTER);
		userLayout.addComponent(imgHelp);
		userLayout.setComponentAlignment(imgHelp, Alignment.MIDDLE_CENTER);
		userLayout.addComponent(imgCustomer);
		userLayout.setComponentAlignment(imgCustomer, Alignment.MIDDLE_CENTER);
		userLayout.addComponent(imgFavorite);
		userLayout.setComponentAlignment(imgFavorite, Alignment.MIDDLE_CENTER);
		userLayout.addComponent(imgAssetIssue);
		userLayout.setComponentAlignment(imgAssetIssue, Alignment.MIDDLE_CENTER);
		userLayout.addComponent(imgIndent);
		userLayout.setComponentAlignment(imgIndent, Alignment.MIDDLE_CENTER);
		userLayout.addComponent(imgSingOut);
		userLayout.setComponentAlignment(imgSingOut, Alignment.MIDDLE_CENTER);
		hlHeader.setComponentAlignment(userLayout, Alignment.MIDDLE_RIGHT);
		lblCurrentScreenName.setStyleName("h7");
		/* add tree componends and application fields to middle in main layout */
		hlTreeMenu = new HorizontalLayout();
		hlTreeMenu.setSizeFull();
		hlCollapse = new HorizontalLayout();
		hlExpand = new HorizontalLayout();
		collandexpndhl = new HorizontalLayout();
		collandexpndhl.removeAllComponents();
		collandexpndhl.setWidth("145px");
		// collandexpndhl.setHeight("20px");
		collandexpndhl.setSpacing(true);
		hlLine = new HorizontalLayout();
		hlLine.removeAllComponents();
		hlLine.setHeight("2px");
		hlLine.setWidth("100%");
		hlLine.setStyleName("bluebar");
		hlCollapse.setSpacing(true);
		hlCollapse.setVisible(false);
		hlExpand.setSpacing(true);
		imgExpand = new Image(null, new ThemeResource("img/expandall.png"));
		imgExpand.setWidth("12px");
		imgExpand.setHeight("12px");
		imgExpand.addClickListener(this);
		imgCollapse = new Image(null, new ThemeResource("img/collapseall.png"));
		imgCollapse.setWidth("12px");
		imgCollapse.setHeight("12px");
		imgCollapse.addClickListener(this);
		lblExpand = new Label();
		lblExpand.setValue("Expand all");
		lblExpand.addStyleName("h7");
		lblCollapse = new Label();
		lblCollapse.setValue("Collapse all");
		lblCollapse.addStyleName("h7");
		imgPin = new Image(null, null);
		imgPin.setWidth("20px");
		imgPin.setHeight("20px");
		imgPin.addClickListener(this);
		imgPin.setDescription("Pin");
		imgPin.addStyleName("pin");
		imgUnpin = new Image(null, null);
		imgUnpin.setWidth("20px");
		imgUnpin.setHeight("20px");
		imgUnpin.addClickListener(this);
		imgUnpin.setDescription("Unpin");
		imgUnpin.addStyleName("unpin");
		hlExpand.addComponent(imgExpand);
		hlExpand.setComponentAlignment(imgExpand, Alignment.MIDDLE_LEFT);
		hlExpand.addComponent(lblExpand);
		hlExpand.setComponentAlignment(lblExpand, Alignment.MIDDLE_LEFT);
		hlCollapse.addComponent(imgCollapse);
		hlCollapse.setComponentAlignment(imgCollapse, Alignment.MIDDLE_RIGHT);
		hlCollapse.addComponent(lblCollapse);
		hlCollapse.setComponentAlignment(lblCollapse, Alignment.MIDDLE_LEFT);
		collandexpndhl.removeAllComponents();
		collandexpndhl.addComponent(hlExpand);
		collandexpndhl.setComponentAlignment(hlExpand, Alignment.TOP_CENTER);
		collandexpndhl.addComponent(hlCollapse);
		collandexpndhl.setComponentAlignment(hlCollapse, Alignment.TOP_CENTER);
		collandexpndhl.addComponent(imgUnpin);
		collandexpndhl.setComponentAlignment(imgUnpin, Alignment.BOTTOM_RIGHT);
		vlLine = new VerticalLayout();
		vlLine.setWidth("2px");
		vlLine.setHeight("100%");
		vlLine.addStyleName("sidebarone");
		vlTreeLayout = new VerticalLayout();
		vlTreeLayout.addStyleName("sidebar");
		vlTreeLayout.setWidth("145px");
		vlPinLayout = new VerticalLayout();
		vlPinLayout.addStyleName("sidebar");
		vlPinLayout.setWidth("15px");
		vlPinLayout.setVisible(false);
		vlPinLayout.removeAllComponents();
		vlPinLayout.addComponent(imgPin);
		vlPinLayout.setComponentAlignment(imgPin, Alignment.TOP_LEFT);
		// vlTreeLayout.addComponent(hlLine);
		vlTreeLayout.addComponent(treeMenu);
		vlTreeLayout.setExpandRatio(treeMenu, 1);
		hlScreenName = new HorizontalLayout();
		hlScreenName.setHeight("36px");
		hlScreenName.setWidth("100%");
		hlScreenName.addStyleName("topbar");
		hlScreenName.addComponent(lblCurrentScreenName);
		hlScreenName.setComponentAlignment(lblCurrentScreenName, Alignment.MIDDLE_LEFT);
		VerticalLayout vlLine1 = new VerticalLayout();
		vlLine1.setWidth("2px");
		vlLine1.setHeight("36px");
		vlLine1.addStyleName("sidebarone");
		hlTitle = new HorizontalLayout();
		hlTitle.setHeight("36px");
		hlTitle.setWidth("100%");
		hlTitle.addStyleName("topbar");
		hlTitle.addComponent(collandexpndhl);
		hlTitle.setComponentAlignment(collandexpndhl, Alignment.TOP_LEFT);
		hlTitle.addComponent(vlPinLayout);
		hlTitle.setComponentAlignment(vlPinLayout, Alignment.TOP_LEFT);
		hlTitle.addComponent(vlLine1);
		hlTitle.addComponent(hlScreenName);
		hlTitle.setComponentAlignment(hlScreenName, Alignment.MIDDLE_LEFT);
		hlTitle.setExpandRatio(hlScreenName, 1);
		vlLocal = new VerticalLayout();
		vlLocal.setStyleName("sidebar");
		vlLocal.addComponent(clContent);
		vlLocal.setExpandRatio(clContent, 1);
		hlTreeMenu.addComponent(vlTreeLayout);
		hlTreeMenu.addComponent(vlLine);
		hlTreeMenu.addComponent(vlLocal);
		hlTreeMenu.setExpandRatio(vlLocal, 1);
		// add fields to footer layout
		cbSearchScreenCode.setHeight("23px");
		imgMonitor = new Image(null, new ThemeResource("img/monitor.png"));
		imgMonitor.setWidth("22px");
		imgMonitor.setHeight("22px");
		imgMonitor.setDescription("Open screen");
		imgMonitor.addClickListener(this);
		hlFooterMiddle = new HorizontalLayout();
		hlFooterMiddle.setSpacing(true);
		hlFooterMiddle.addComponent(cbSearchScreenCode);
		hlFooterMiddle.addComponent(imgMonitor);
		hlFooterMiddle.setComponentAlignment(imgMonitor, Alignment.MIDDLE_CENTER);
		hlFooterLayout.setWidth("100%");
		hlFooterLayout.setHeight("25px");
		lblLastLogedin.setContentMode(ContentMode.HTML);
		lblUserTimeZone.setContentMode(ContentMode.HTML);
		final Label lblVersion = new Label();
		lblVersion.setContentMode(ContentMode.HTML);
		lblVersion.setValue("<font size=\"1\"color=\"white\">" + paramRefVersion + ", " + paramRefCopyright
				+ " &nbsp;&nbsp;");
		hlFooterLayout.setStyleName("topbartwo");
		hlFooterRight = new HorizontalLayout();
		hlFooterRight.setSpacing(true);
		hlFooterRight.addComponent(lblVersion);
		HorizontalLayout hlUserTime = new HorizontalLayout();
		hlUserTime.setSpacing(true);
		hlUserTime.addComponent(lblLastLogedin);
		hlUserTime.addComponent(lblUserTimeZone);
		hlFooterLayout.addComponent(hlUserTime);
		hlFooterLayout.setComponentAlignment(hlUserTime, Alignment.MIDDLE_LEFT);
		hlFooterLayout.addComponent(hlFooterMiddle);
		hlFooterLayout.setComponentAlignment(hlFooterMiddle, Alignment.MIDDLE_CENTER);
		hlFooterLayout.addComponent(hlFooterRight);
		hlFooterLayout.setComponentAlignment(hlFooterRight, Alignment.MIDDLE_RIGHT);
		// add all layouts in root layout
		vlRoot.addComponent(hlHeader);
		vlRoot.addComponent(hlTitle);
		vlRoot.addComponent(hlTreeMenu);
		vlRoot.setExpandRatio(hlTreeMenu, 1);
		vlRoot.addComponent(hlFooterLayout);
		vlRoot.setComponentAlignment(hlFooterLayout, Alignment.BOTTOM_CENTER);
	}
	
	private void verfiyLoginUser() {
		try {
			String loginId = tfUsername.getValue().toString();
			String password = pfPassword.getValue().toString();
			boolean valid = false;
			long diffInDays = 0;
			List<UserDM> userlist = serviceUser.getUserList(null, null, loginId, null, null, null, password, "F");
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			if (userlist.size() != 0) {
				for (UserDM mbaseuser : userlist) {
					loginCompanyId = mbaseuser.getCompanyid();
					if (mbaseuser.getLastlogindt() != null) {
						lblLastLogedin.setValue("<font size=\"1\"color=\"white\">Last logged in: "
								+ mbaseuser.getLastlogindt());
					}
					systemUser = mbaseuser.getSystemuseryn();
					dashboardview = mbaseuser.getDashboardView();
					userId = mbaseuser.getUserid();
					VaadinSession vSession = UI.getCurrent().getSession();
					WrappedSession wSession = vSession.getSession();
					sessionId = wSession.getId();
					clientIP = Page.getCurrent().getWebBrowser().getAddress();
					passwordExDt = mbaseuser.getPasswordexpiredtInDt();
					lblCompanyName
							.setValue("<font size=\"2\"color=\"white\"><B>" + mbaseuser.getCompanyName() + "</B>");
					strCompanyName = mbaseuser.getCompanyName();
					companyCode = mbaseuser.getCompanyCode();
					timezoneId = mbaseuser.getTimezoneid();
					loginuserName = tfUsername.getValue();
				}
				List<EmployeeDM> empList = serviceEmployee.getEmployeeList(null, null, null, "Active", loginCompanyId,
						null, userId, null, null, "F");
				if (empList.size() != 0) {
					for (EmployeeDM empObj : empList) {
						userFullName = empObj.getFirstname();
						if (empObj.getLastname() != null && empObj.getLastname().trim().length() > 0) {
							userFullName = userFullName + " " + empObj.getLastname();
						}
						branchId = empObj.getBranchid();
						employeeid = empObj.getEmployeeid();
						deptId = empObj.getDeptid();
						System.out.println("loginuserName" + loginuserName);
					}
				}
				List<CompanyDM> companylist = serviceCompany.getCompanyList(null, null, loginCompanyId);
				if (companylist.size() != 0) {
					for (CompanyDM compobj : companylist) {
						countryid = compobj.getCountryid();
						currencysymbol = compobj.getCurrencyName();
					}
				}
				roleId = serviceUserRoles.getRoleIdByUserId(userId, loginCompanyId);
				System.out.println("role id" + roleId);
				cal1.setTime(passwordExDt);
				cal2.setTime(DateUtils.getcurrentdate());
				diffInDays = daysBetween(cal2, cal1);
				if (empList.size() == 0) {
					lblerror.setValue("No employee for this name");
					vlErrorpanel.removeAllComponents();
					vlErrorpanel.addComponent(lblerror);
					tfUsername.focus();
					valid = false;
				} else if (diffInDays <= 5 && diffInDays >= 1) {
					lblerror.setValue("Warning pwd exp");
					vlErrorpanel.removeAllComponents();
					vlErrorpanel.addComponent(lblerror);
					tfUsername.focus();
					valid = true;
				} else if (diffInDays == 0) {
					lblerror.setValue("pwd exp");
					vlErrorpanel.removeAllComponents();
					vlErrorpanel.addComponent(lblerror);
					tfUsername.focus();
					valid = false;
					vlPasswordLayout.setVisible(true);
					vlSingIn.setVisible(false);
				} else {
					valid = true;
				}
				if (valid) {
					System.out.println(UI.getCurrent().getSession().getAttribute("loginUserName"));
					System.out.println(UI.getCurrent().getSession().getAttribute("loginCompanyId"));
					clArgumentLayout.removeAllComponents();
					clArgumentLayout.setMargin(new MarginInfo(true, true, false, true));
					hlFooterLayout.removeAllComponents();
					removeStyleName("login");
					lblerror.setValue("");
					// Create the tree nodes
					menuList = serviceAppScreen.getMBaseAppscreenList(roleId, loginCompanyId, branchId);
					treeMenu = new Tree();
					treeMenu.addStyleName("no-children");
					treeMenu.addItemClickListener(this);
					cbSearchScreenCode.setContainerDataSource(null);
					try {
						for (AppScreensMenuDM mBaseAppObj : menuList) {
							treeMenu.addItem(mBaseAppObj.getScreendesc());
							if (mBaseAppObj.getParentName() != null) {
								treeMenu.setParent(mBaseAppObj.getScreendesc(), mBaseAppObj.getParentName());
								if (mBaseAppObj.getTargetClass() == null) {
									treeMenu.setChildrenAllowed(mBaseAppObj.getScreendesc(), true);
								} else {
									treeMenu.setChildrenAllowed(mBaseAppObj.getScreendesc(), false);
									cbSearchScreenCode.addItem(mBaseAppObj.getScreendesc());
								}
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					paramRefVersion = serviceParam.getParameterValue(versionVar, loginCompanyId, null);
					paramRefCopyright = serviceParam.getParameterValue(copyrightVar, loginCompanyId, null);
					buildMainView();
					UserLoginDM userLoginList = new UserLoginDM();
					userLoginList.setCompanyId(loginCompanyId);
					userLoginList.setLoginDate(DateUtils.getcurrentdate());
					userLoginList.setSessionId(sessionId);
					userLoginList.setUserId(userId);
					userLoginList.setClientIp(clientIP);
					serviceUserLogin.saveUserLoginList(userLoginList);
					userLoginId = userLoginList.getLoginRefId();
					serviceUser.updateUserListByUserId(userId, sessionId);
					System.out.println("Log in User Name" + loginuserName);
					UI.getCurrent().getSession().setAttribute("loginUserName", loginuserName);
					UI.getCurrent().getSession().setAttribute("loginCompanyId", loginCompanyId);
					UI.getCurrent().getSession().setAttribute("userId", userId);
					UI.getCurrent().getSession().setAttribute("hlLayout", hlScreenName);
					UI.getCurrent().getSession().setAttribute("clLayout", clArgumentLayout);
					UI.getCurrent().getSession().setAttribute("companyCode", companyCode);
					UI.getCurrent().getSession().setAttribute("blIsEditMode", blIsEditMode);
					UI.getCurrent().getSession().setAttribute("companyName", strCompanyName);
					UI.getCurrent().getSession().setAttribute("systemUserYN", strSystemUser);
					UI.getCurrent().getSession().setAttribute("employeeId", employeeid);
					UI.getCurrent().getSession().setAttribute("deptId", deptId);
					UI.getCurrent().getSession().setAttribute("roleId", roleId);
					UI.getCurrent().getSession().setAttribute("timezoneId", timezoneId);
					UI.getCurrent().getSession().setAttribute("branchId", branchId);
					UI.getCurrent().getSession().setAttribute("clHelpLayout", clHelpLayout);
					UI.getCurrent().getSession().setAttribute("currenyId", currenyId);
					UI.getCurrent().getSession().setAttribute("countryid", countryid);
					UI.getCurrent().getSession().setAttribute("systemUser", systemUser);
					UI.getCurrent().getSession().setAttribute("currencysymbol", currencysymbol);
					UI.getCurrent().getSession().setAttribute("quoteid", quoteId);
					UI.getCurrent().getSession().setAttribute("enquiryid", enquiryId);
					UI.getCurrent().getSession().setAttribute("isFileUploaded", false);
					serviceAuditRec.executeStoredProcedure(loginCompanyId, userId, loginuserName, clientIP);
					/**
					 * add list to Menu
					 */
				
					appScreenList = serviceAppScreen.getMBaseAppScreenListByUserId(userId);
					MenuItem settingsMenu = mbFavarotise.addItem("", null);
					// settingsMenu.setStyleName("icon-cog");
					settingsMenu.setIcon(new ThemeResource("img/favorite.png"));
					for (AppScreensDM baseAppScreen : appScreenList) {
						settingsMenu.addItem(baseAppScreen.getScreendesc(), cmd);
					}
					clContent.removeAllComponents();
					getView(dashboardview);
					clContent.addComponent(clArgumentLayout);
					DateFormat gmtFormat = new SimpleDateFormat();
					TimeZone gmtTime = TimeZone.getTimeZone("GMT");
					gmtFormat.setTimeZone(gmtTime);
					System.out.println("Current Date" + DateUtils.getcurrentdate());
					System.out.println("Current DateTime in GMT : " + gmtFormat.format(DateUtils.getcurrentdate()));
					System.out.println("Adjust time" + adjustTime);
					if (roleId==2) {
						UI.getCurrent().getSession().setAttribute("IS_ENQ_WF", false);
						UI.getCurrent().getSession().setAttribute("IS_PROD_FRM", false);
						UI.getCurrent().getSession().setAttribute("IS_QC_FRM", false);
						UI.getCurrent().getSession().setAttribute("IS_MARK_FRM", true);
					} else if (roleId==3) {
						UI.getCurrent().getSession().setAttribute("IS_MARK_FRM", false);
						UI.getCurrent().getSession().setAttribute("IS_DIE_ENQ", false);
						UI.getCurrent().getSession().setAttribute("IS_ENQ_WF", true);
						UI.getCurrent().getSession().setAttribute("IS_DESIGN_DR", true);
					}
					// System.out.println("Adjuseted Date and time"
					// + gmtFormat.format(DateUtils.getcurrentdate().getTimezoneOffset()));
					// Date dstDate = new Date( gmtFormat.format(DateUtils.getcurrentdate()) +
					// TimeZone.getTimeZone(adjustTime));
				}
			} else {
				lblerror.setValue("Invalid user id and Password");
				vlErrorpanel.removeAllComponents();
				vlErrorpanel.addComponent(lblerror);
				tfUsername.focus();
				logger.info("Invalid user id and Password");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			// lblerror.setValue("Contact system admin");
			vlErrorpanel.removeAllComponents();
			vlErrorpanel.addComponent(lblerror);
			tfUsername.focus();
			logger.error("Verify loginusername and password" + e);
		}
	}
	
	@Override
	public void itemClick(ItemClickEvent event) {
		sreenName = event.getItemId().toString();
		loadScreenCode();
	}
	
	@Override
	public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
		// TODO Auto-generated method stub
		if (imgExpand == event.getComponent()) {
			for (Object itemId : treeMenu.getItemIds())
				treeMenu.expandItem(itemId);
			hlCollapse.setVisible(true);
			hlExpand.setVisible(false);
		}
		if (imgCollapse == event.getComponent()) {
			for (Object itemId : treeMenu.getItemIds()) {
				treeMenu.collapseItemsRecursively(itemId);
			}
			hlExpand.setVisible(true);
			hlCollapse.setVisible(false);
		}
		if (imgPin == event.getComponent()) {
			vlTreeLayout.setVisible(true);
			vlPinLayout.setVisible(false);
			collandexpndhl.setVisible(true);
		}
		if (imgUnpin == event.getComponent()) {
			vlTreeLayout.setVisible(false);
			vlPinLayout.setVisible(true);
			collandexpndhl.setVisible(false);
		}
		if (imgSingOut == event.getComponent()) {
			vlTreeLayout.removeAllComponents();
			hlTreeMenu.removeAllComponents();
			vlRoot.removeAllComponents();
			hlLoginLayout.removeAllComponents();
			clContent.removeAllComponents();
			buildLoginView(false);
			serviceUser.resetSessionId(userId);
			serviceUserLogin.updateLogoutDateByUserLogin(userLoginId);
			userId = null;
			sessionId = "";
			userLoginId = null;
			UI.getCurrent().getSession().setAttribute("loginUserName", null);
			UI.getCurrent().getSession().setAttribute("loginCompanyId", null);
			UI.getCurrent().getSession().setAttribute("userId", null);
			UI.getCurrent().getSession().setAttribute("hlLayout", null);
			UI.getCurrent().getSession().setAttribute("clLayout", null);
			UI.getCurrent().getSession().setAttribute("companyCode", null);
			UI.getCurrent().getSession().setAttribute("systemUserYN", null);
			UI.getCurrent().getSession().setAttribute("appScreenId", null);
			UI.getCurrent().getSession().setAttribute("moduleId", null);
			UI.getCurrent().getSession().setAttribute("employeeId", null);
			UI.getCurrent().getSession().setAttribute("deptId", null);
			UI.getCurrent().getSession().setAttribute("roleId", null);
			UI.getCurrent().getSession().setAttribute("timezoneId", null);
			UI.getCurrent().getSession().setAttribute("branchId", null);
			UI.getCurrent().getSession().setAttribute("clHelpLayout", null);
			UI.getCurrent().getSession().setAttribute("currenyId", null);
			UI.getCurrent().getSession().setAttribute("isFileUploaded", false);
			serviceAuditRec.closetheSessiondata();
			lblerror.setValue("");
		}
		if (imgMonitor == event.getComponent()) {
			if (cbSearchScreenCode.getValue() != null) {
				sreenName = cbSearchScreenCode.getValue().toString();
			}
			loadScreenCode();
		}
		if (imgProfile == event.getComponent()) {
			/*
			 * clContent.removeAllComponents(); clArgumentLayout.removeAllComponents(); new EmployeeSetting();
			 * clContent.addComponent(clArgumentLayout);
			 */
		}
		if (imgGlob == event.getComponent()) {
			clContent.removeAllComponents();
			new Dashboard();
			clContent.addComponent(clArgumentLayout);
		}
		if (imgAssetIssue == event.getComponent()) {
			UI.getCurrent().addWindow(new AssetComplaintRegister());
		}
		if (imgIndent == event.getComponent()) {
			clArgumentLayout.removeAllComponents();
			hlScreenName.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Indent");
			UI.getCurrent().getSession().setAttribute("moduleId", 9L);
			new Indent(0L);
		}
		if (imgHelp == event.getComponent()) {
			/*
			 * subwindow = new Window("Help"); subwindow.setWidth("580px"); subwindow.setHeight("800px");
			 * subwindow.setPositionX(785); new HelpUI(); subwindow.setContent(clHelpLayout);
			 * UI.getCurrent().addWindow(subwindow);
			 */
		}
	}
	
	private Date addDays(Date d, int days) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = sdf.format(d);
		Date parsedDate = null;
		try {
			parsedDate = sdf.parse(strDate);
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.warn("calculate days" + e);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, days);
		return now.getTime();
	}
	
	public static long daysBetween(Calendar startDate, Calendar endDate) {
		Calendar date = (Calendar) startDate.clone();
		long daysBetween = 0;
		while (date.before(endDate)) {
			date.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		return daysBetween;
	}
	
	@SuppressWarnings("unused")
	private void updateNewPassoword() {
		String loginName = tfUsername.getValue().toString();
		String loginPassword = pfPassword.getValue().toString();
		String newPassword = pfNewPassword.getValue().toString();
		String conforPassword = pfConformPassword.getValue().toString();
		String priviewPassword = pfPreviesePass.getValue().toString();
		String passwordExpireduration = serviceParam.getParameterValue("BS_PWDEXP", loginCompanyId, null);
		Date newPasswordExpireDt = null;
		/**
		 * calculate the new password Expire date by using addDays method
		 */
		newPasswordExpireDt = addDays(passwordExDt, Integer.valueOf(passwordExpireduration));
		if (pfNewPassword.isValid() && pfConformPassword.isValid() && pfPreviesePass.isValid()) {
			lblPasswordError.setValue("");
			if (!loginPassword.equals(priviewPassword)) {
				lblPasswordError.setValue("Your Previous Password is Incorrect!");
				lblPasswordError.setVisible(true);
			} else if (!newPassword.equals(conforPassword)) {
				lblPasswordError.setValue("New Password and Confirm Password should be Same!");
				lblPasswordError.setVisible(true);
			} else {
				serviceUser.getUpdatePasswordDetails(newPassword, DateUtils.datetostring(newPasswordExpireDt),
						loginPassword, loginName);
				vlPasswordLayout.setVisible(false);
				vlSingIn.setVisible(true);
				lblPasswordError.setVisible(false);
				lblPasswordError.setValue("");
				vlErrorpanel.removeAllComponents();
				pfConformPassword.setValue("");
				pfNewPassword.setValue("");
				pfPreviesePass.setValue("");
				tfUsername.setValue("");
				pfPassword.setValue("");
			}
		} else {
			lblPasswordError.setVisible(true);
			lblPasswordError.setValue("All fields are Mandatory");
		}
	}
	
	private void loadScreenCode() {
		lblCurrentScreenName.setVisible(false);
		clArgumentLayout.removeAllComponents();
		clArgumentLayout.removeStyleName("dashboard-view");
		clArgumentLayout.setStyleName("padding : 0");
		UI.getCurrent().getSession().setAttribute("screenName", sreenName);
		System.out.println("screen Name" + sreenName);
		String targetClass = null;
		try {
			for (AppScreensMenuDM mbaseAppsScreenList : menuList) {
				if (mbaseAppsScreenList.getScreendesc().equals(sreenName)) {
					targetClass = mbaseAppsScreenList.getTargetClass();
					appScreenId = mbaseAppsScreenList.getScreenId();
					moduleId = mbaseAppsScreenList.getModuleId();
				}
			}
			UI.getCurrent().getSession().setAttribute("moduleId", moduleId);
		}
		catch (Exception e) {
			logger.info("Target Class is null" + e);
			e.printStackTrace();
		}
		UI.getCurrent().getSession().setAttribute("appScreenId", appScreenId);
		if (targetClass == null) {
			if (sreenName.equalsIgnoreCase("Design")) {
				targetClass = "com.gnts.base.dashboard.DashbordDesignView";
			} else if (sreenName.equalsIgnoreCase("Finance Management")) {
				targetClass = "com.gnts.base.dashboard.DashboardFinanceView";
			} else if (sreenName.equalsIgnoreCase("Human Capital")) {
				targetClass = "com.gnts.base.dashboard.DashboardHCMView";
			} else if (sreenName.equalsIgnoreCase("Material Management")) {
				targetClass = "com.gnts.base.dashboard.DashboardMMSView";
			} else if (sreenName.equalsIgnoreCase("Inventory Management")) {
				targetClass = "com.gnts.base.dashboard.DashboardStoreView";
			} else if (sreenName.equalsIgnoreCase("Asset Management")) {
				targetClass = "com.gnts.base.dashboard.MaintenanceDashboardView";
			} else if (sreenName.equalsIgnoreCase("Die")) {
				targetClass = "com.gnts.base.dashboard.DieDashboardView";
			} else if (sreenName.equalsIgnoreCase("Production Management")
					|| sreenName.equalsIgnoreCase("Manufacturing")) {
				targetClass = "com.gnts.base.dashboard.DashboardProduction";
			} else if (sreenName.equalsIgnoreCase("Sales Management")) {
				targetClass = "com.gnts.base.dashboard.DashbordView";
			} else if (sreenName.equalsIgnoreCase("Testing")) {
				targetClass = "com.gnts.base.dashboard.DashboardTestingView";
			} else if (sreenName.equalsIgnoreCase("Planning")) {
				targetClass = "com.gnts.base.dashboard.DashboardPlanningView";
			} else {
				targetClass = "com.gnts.base.mst.Dashboard";
			}
		}
		System.out.println("test1" + targetClass);
		getView(targetClass);
		clContent.addComponent(clArgumentLayout);
		cbSearchScreenCode.setValue(null);
	}
	
	private void getView(String targetClass) {
		Class<?> c = null;
		Object b = null;
		try {
			c = Class.forName(targetClass);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("convert string to class type" + e);
		}
		try {
			b = c.newInstance();
			c.isInstance(b);// invoke(b);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("convert class type to object type " + e);
		}
	}
	
	Command cmd = new Command() {
		@Override
		public void menuSelected(MenuItem selectedItem) {
			sreenName = selectedItem.getText();
			loadScreenCode();
		}
	};
}
