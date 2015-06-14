/**
 * File Name 		: Dashboard.java 
 * Description 		: this class is used for add/edit Employee  details. 
 * Author 			: P Sekhar
 * Date 			: Apr 13, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * 
 */
package com.gnts.base.mst;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.txn.HolidaysDM;
import com.gnts.base.domain.txn.OrgNewsDM;
import com.gnts.base.rpt.PayrollChart;
import com.gnts.base.service.txn.HolidayService;
import com.gnts.base.service.txn.OrgNewsService;
import com.gnts.erputil.components.DummyDataGenerator;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.NotificationsButton;
import com.gnts.erputil.components.SparklineChart;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class Dashboard {
	private Table tblHoliday, tblEvaluation;
	private HolidayService serviceHoliday = (HolidayService) SpringContextHelper.getBean("holidays");
	private OrgNewsService serviceNews = (OrgNewsService) SpringContextHelper.getBean("news");
	private Logger log = Logger.getLogger(Dashboard.class);
	private BeanItemContainer<HolidaysDM> beans = null;
	private VerticalLayout vltable, vlEvalTable;
	private HorizontalLayout vlMainLayout;
	private VerticalLayout vlnew, vlEval;
	private Long companyId, branchId;
	private Accordion accordion;
	private Label lblNews, lblFormTittle;
	private Label titleLabel;
	private NotificationsButton notificationsButton;
	public static final String EDIT_ID = "dashboard-edit";
	public static final String TITLE_ID = "dashboard-title";
	
	public Dashboard() {
		branchId = Long.valueOf(UI.getCurrent().getSession().getAttribute("branchId").toString());
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		VerticalLayout clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		HorizontalLayout hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		hlHeader.removeAllComponents();
		clMainLayout.removeAllComponents();
		tblHoliday = new Table();
		tblHoliday.setPageLength(7);
		tblHoliday.setSizeFull();
		tblEvaluation = new Table();
		tblEvaluation.setPageLength(3);
		tblEvaluation.setSizeFull();
		lblFormTittle = new Label();
		lblFormTittle.setContentMode(ContentMode.HTML);
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + "Dashboard");
		vlnew = new VerticalLayout();
		vlnew.setWidth("300px");
		vlnew.setHeight("240px");
		vlEval = new VerticalLayout();
		vlEval.setWidth("250px");
		vlEval.setHeight("200px");
		vltable = new VerticalLayout();
		vltable.addComponent(tblHoliday);
		vlEvalTable = new VerticalLayout();
		vlEvalTable.addComponent(tblEvaluation);
		accordion = new Accordion();
		accordion.setHeight("360px");
		accordion.setWidth("350px");
		accordion.addTab(vlEval, "Evaluation Details");
		accordion.addTab(vltable, "Holidays");
		accordion.addTab(vlnew, "News");
		accordion.setSelectedTab(vltable);
		VerticalLayout vlchart = new VerticalLayout();
		new PayrollChart(vlchart, null);
		vlMainLayout = new HorizontalLayout();
		vlMainLayout.setWidth("100%");
		vlMainLayout.setSpacing(true);
		vlMainLayout.setMargin(true);
		// vlMainLayout.addComponent(GERPPanelGenerator.createPanel(tblHoliday));
		// vlMainLayout.addComponent(accordion);
		// vlMainLayout.setComponentAlignment(accordion, Alignment.MIDDLE_LEFT);
		// vlMainLayout.setComponentAlignment(accordion, Alignment.MIDDLE_RIGHT);
		// vlMainLayout.setExpandRatio(vlchart, 1);
		// clMainLayout.addComponent(buildHeader());
		clMainLayout.addComponent(buildSparklines());
		clMainLayout.addComponent(vlMainLayout);
		hlHeader.addComponent(lblFormTittle);
		hlHeader.setComponentAlignment(lblFormTittle, Alignment.MIDDLE_LEFT);
		//hlHeader.addComponent(buildNotificationsButton());
		hlHeader.setExpandRatio(lblFormTittle, 1);
		populateAndConfigureTableNew();
		populateAndConfigEval();
		loadNewsDetails();
	}
	
	// Method for show the details in grid table
	private void populateAndConfigureTableNew() {
		tblHoliday.removeAllItems();
		List<HolidaysDM> usertable = new ArrayList<HolidaysDM>();
		Date endDate = addDays(DateUtils.getcurrentdate(), 30);
		if (branchId != null && endDate != null) usertable = serviceHoliday.getHolidaysList(null, null, null, "Active",
				companyId, null, "F");
		beans = new BeanItemContainer<HolidaysDM>(HolidaysDM.class);
		beans.addAll(usertable);
		tblHoliday.setContainerDataSource(beans);
		tblHoliday.setVisibleColumns(new Object[] { "holidayDate", "holidayName" });
		tblHoliday.setColumnHeaders(new String[] { " Date", "Holiday " });
		tblHoliday.setColumnWidth("holidayDate", 100);
	}
	
	private void populateAndConfigEval() {
	}
	
	private void loadNewsDetails() {
		List<OrgNewsDM> newsList = serviceNews.getNewsList(null, null, null, "Active", companyId, branchId, null);
		if (newsList != null) {
			lblNews = new Label();
			lblNews.setContentMode(ContentMode.HTML);
			for (OrgNewsDM newObject : newsList) {
				lblNews.setValue("<marquee scrollamount=\"4\" direction=\"up\">" + newObject.getNewsDesc()
						+ "<br> </marquee>");
				vlnew.addComponent(lblNews);
			}
		}
	}
	
	private Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.addStyleName("viewheader");
		header.setSpacing(true);
		titleLabel = new Label("Dashboard");
		titleLabel.setId(TITLE_ID);
		titleLabel.setSizeUndefined();
		titleLabel.addStyleName(ValoTheme.LABEL_H1);
		titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		header.addComponent(titleLabel);
		notificationsButton = buildNotificationsButton();
		Component edit = buildEditButton();
		HorizontalLayout tools = new HorizontalLayout(notificationsButton, edit);
		tools.setSpacing(true);
		tools.addStyleName("toolbar");
		header.addComponent(tools);
		return header;
	}
	
	private NotificationsButton buildNotificationsButton() {
		NotificationsButton result = new NotificationsButton();
		result.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				// openNotificationsPopup(event);
			}
		});
		return result;
	}
	
	private Component buildEditButton() {
		Button result = new Button();
		result.setId(EDIT_ID);
		result.setIcon(FontAwesome.EDIT);
		result.addStyleName("icon-edit");
		result.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		result.setDescription("Edit Dashboard");
		result.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				/*
				 * getUI().addWindow( new DashboardEdit(DashboardView.this, titleLabel .getValue()));
				 */
			}
		});
		return result;
	}
	
	private Component buildSparklines() {
		CssLayout sparks = new CssLayout();
		sparks.addStyleName("sparks");
		sparks.setWidth("100%");
		Responsive.makeResponsive(sparks);
		SparklineChart s = new SparklineChart("Traffic", "K", "", DummyDataGenerator.chartColors[0], 22, 20, 80);
		sparks.addComponent(s);
		s = new SparklineChart("Revenue / Day", "M", "$", DummyDataGenerator.chartColors[2], 8, 89, 150);
		sparks.addComponent(s);
		s = new SparklineChart("Production Time", "s", "", DummyDataGenerator.chartColors[3], 10, 30, 120);
		sparks.addComponent(s);
		s = new SparklineChart("Sales Rate", "%", "", DummyDataGenerator.chartColors[5], 50, 34, 100);
		sparks.addComponent(s);
		return GERPPanelGenerator.createPanel(sparks);
	}
	
	private Component createContentWrapper(final Component content) {
		final CssLayout slot = new CssLayout();
		slot.setWidth("100%");
		slot.addStyleName("dashboard-panel-slot");
		CssLayout card = new CssLayout();
		card.setWidth("100%");
		card.addStyleName(ValoTheme.LAYOUT_CARD);
		HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.addStyleName("dashboard-panel-toolbar");
		toolbar.setWidth("100%");
		Label caption = new Label("Holidays");
		caption.addStyleName(ValoTheme.LABEL_H4);
		caption.addStyleName(ValoTheme.LABEL_COLORED);
		caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		content.setCaption(null);
		MenuBar tools = new MenuBar();
		tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
		MenuItem max = tools.addItem("", FontAwesome.EXPAND, new Command() {
			@Override
			public void menuSelected(final MenuItem selectedItem) {
				if (!slot.getStyleName().contains("max")) {
					selectedItem.setIcon(FontAwesome.COMPRESS);
					// toggleMaximized(slot, true);
				} else {
					slot.removeStyleName("max");
					selectedItem.setIcon(FontAwesome.EXPAND);
					// toggleMaximized(slot, false);
				}
			}
		});
		max.setStyleName("icon-only");
		MenuItem root = tools.addItem("", FontAwesome.COG, null);
		root.addItem("Configure", new Command() {
			@Override
			public void menuSelected(final MenuItem selectedItem) {
				Notification.show("Not implemented in this demo");
			}
		});
		root.addSeparator();
		root.addItem("Close", new Command() {
			@Override
			public void menuSelected(final MenuItem selectedItem) {
				Notification.show("Not implemented in this demo");
			}
		});
		toolbar.addComponents(caption, tools);
		toolbar.setExpandRatio(caption, 1);
		toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);
		card.addComponents(toolbar, content);
		slot.addComponent(card);
		return slot;
	}
	
	/**
	 * calculate the date
	 * 
	 * @param d
	 * @param days
	 * @return
	 */
	private Date addDays(Date d, int days) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = sdf.format(d);
		Date parsedDate = null;
		try {
			parsedDate = sdf.parse(strDate);
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			log.warn("calculate days" + e);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, days);
		return now.getTime();
	}
}
