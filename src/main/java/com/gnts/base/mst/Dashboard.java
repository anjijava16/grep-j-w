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
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.txn.HolidaysDM;
import com.gnts.base.domain.txn.OrgNewsDM;
import com.gnts.base.rpt.PayrollChart;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.txn.HolidayService;
import com.gnts.base.service.txn.OrgNewsService;
import com.gnts.erputil.components.DummyDataGenerator;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.SparklineChart;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.sms.domain.txn.CustomerVisitHdrDM;
import com.gnts.sms.service.txn.CustomerVisitHdrService;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Dashboard {
	private Table tblHoliday, tblBirthday, tblNews, tblClientVisit;
	private HolidayService serviceHoliday = (HolidayService) SpringContextHelper.getBean("holidays");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private OrgNewsService serviceNews = (OrgNewsService) SpringContextHelper.getBean("news");
	private CustomerVisitHdrService serviceCustomerVisitHdr = (CustomerVisitHdrService) SpringContextHelper
			.getBean("customervisithdr");
	private Logger logger = Logger.getLogger(Dashboard.class);
	private BeanItemContainer<HolidaysDM> beans = null;
	private VerticalLayout vltable;
	private HorizontalLayout vlMainLayout;
	private VerticalLayout vlnew, vlEval, vlCustVisit;
	private Long companyId, branchId;
	private Accordion accordionHoli, accordionNews, accordionEval, accordionCustVisit;
	private Label lblFormTittle;
	
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
		tblBirthday = new Table();
		tblBirthday.setPageLength(3);
		tblBirthday.setSizeFull();
		tblNews = new Table();
		tblNews.setPageLength(7);
		tblNews.setSizeFull();
		tblClientVisit = new Table();
		tblClientVisit.setPageLength(7);
		tblClientVisit.setSizeFull();
		lblFormTittle = new Label();
		lblFormTittle.setContentMode(ContentMode.HTML);
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + "Dashboard");
		vlnew = new VerticalLayout();
		vlnew.setWidth("300px");
		vlnew.setHeight("240px");
		vlnew.addComponent(tblNews);
		vlEval = new VerticalLayout();
		vlEval.setWidth("250px");
		vlEval.setHeight("200px");
		vlEval.addComponent(tblBirthday);
		vltable = new VerticalLayout();
		vltable.addComponent(tblHoliday);
		vlCustVisit = new VerticalLayout();
		vlCustVisit.addComponent(tblClientVisit);
		accordionHoli = new Accordion();
		accordionNews = new Accordion();
		accordionEval = new Accordion();
		accordionCustVisit = new Accordion();
		accordionHoli.setHeight("360px");
		accordionHoli.setWidth("280px");
		accordionCustVisit.setWidth("300px");
		accordionEval.addTab(vlEval, "Birthday Wishes");
		accordionHoli.addTab(vltable, "Holidays");
		accordionNews.addTab(vlnew, "News");
		accordionHoli.setSelectedTab(vltable);
		accordionCustVisit.addTab(vlCustVisit, "Client Visit");
		VerticalLayout vlchart = new VerticalLayout();
		new PayrollChart(vlchart, null);
		vlMainLayout = new HorizontalLayout();
		vlMainLayout.setWidth("100%");
		vlMainLayout.setSpacing(true);
		vlMainLayout.setMargin(true);
		clMainLayout.addComponent(buildSparklines());
		clMainLayout.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				addComponent(accordionHoli);
				addComponent(accordionNews);
				addComponent(accordionEval);
				addComponent(accordionCustVisit);
				setSpacing(true);
			}
		});
		clMainLayout.addComponent(vlMainLayout);
		clMainLayout.setSpacing(true);
		hlHeader.addComponent(lblFormTittle);
		hlHeader.setComponentAlignment(lblFormTittle, Alignment.MIDDLE_LEFT);
		hlHeader.setExpandRatio(lblFormTittle, 1);
		populateAndConfigureTableNew();
		loadBirthDayDetails();
		loadNewsDetails();
		loadClientVistDetails();
	}
	
	// Method for show the details in grid table
	private void populateAndConfigureTableNew() {
		try {
			tblHoliday.removeAllItems();
			List<HolidaysDM> usertable = new ArrayList<HolidaysDM>();
			Date endDate = addDays(DateUtils.getcurrentdate(), 30);
			if (branchId != null && endDate != null) usertable = serviceHoliday.getHolidaysList(null, null, null,
					"Active", companyId, null, "F");
			beans = new BeanItemContainer<HolidaysDM>(HolidaysDM.class);
			beans.addAll(usertable);
			tblHoliday.setContainerDataSource(beans);
			tblHoliday.setVisibleColumns(new Object[] { "holidayDate", "holidayName" });
			tblHoliday.setColumnHeaders(new String[] { " Date", "Holiday " });
			tblHoliday.setColumnWidth("holidayDate", 100);
			tblHoliday.setColumnWidth("holidayName", 150);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadBirthDayDetails() {
		try {
			List<EmployeeDM> list = serviceEmployee.getEmployeeList(null, null, null, null, null, null, null, null,
					null, "P");
			if (list != null) {
				tblBirthday.removeAllItems();
				BeanItemContainer<EmployeeDM> beansNews = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
				for (EmployeeDM employeeDM : list) {
					try {
						if (DateUtils.getMonthAndYear(employeeDM.getDobinDt()).endsWith(
								DateUtils.getMonthAndYear(new Date()))
								|| DateUtils.getMonthAndYear(employeeDM.getDobinDt()).endsWith(
										DateUtils.getMonthAndYear(addDays(new Date(), 1)))
								|| DateUtils.getMonthAndYear(employeeDM.getDobinDt()).endsWith(
										DateUtils.getMonthAndYear(addDays(new Date(), 2)))
								|| DateUtils.getMonthAndYear(employeeDM.getDobinDt()).endsWith(
										DateUtils.getMonthAndYear(addDays(new Date(), 3)))) {
							beansNews.addBean(employeeDM);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				tblBirthday.setContainerDataSource(beansNews);
				tblBirthday.setVisibleColumns(new Object[] { "dob", "firstlastname" });
				tblBirthday.setColumnHeaders(new String[] { " Date", "Employee Name " });
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadNewsDetails() {
		try {
			List<OrgNewsDM> newsList = serviceNews.getNewsList(null, null, null, "Active", companyId, null, null);
			if (newsList != null) {
				tblNews.removeAllItems();
				BeanItemContainer<OrgNewsDM> beansNews = new BeanItemContainer<OrgNewsDM>(OrgNewsDM.class);
				beansNews.addAll(newsList);
				tblNews.setContainerDataSource(beansNews);
				tblNews.setVisibleColumns(new Object[] { "newsTitle", "newsDesc" });
				tblNews.setColumnHeaders(new String[] { " Title", "Description " });
				tblNews.addGeneratedColumn("newsDesc", new ColumnGenerator() {
					private static final long serialVersionUID = 1L;
					
					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						@SuppressWarnings("unchecked")
						BeanItem<OrgNewsDM> item = (BeanItem<OrgNewsDM>) source.getItem(itemId);
						return new Label((String) item.getItemProperty("newsDesc").getValue(), ContentMode.HTML);
					}
				});
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
	}
	
	private void loadClientVistDetails() {
		try {
			List<CustomerVisitHdrDM> newsList = serviceCustomerVisitHdr.getCustomerVisitHdrList(null, null, null, null,
					null, "F");
			if (newsList != null) {
				tblClientVisit.removeAllItems();
				BeanItemContainer<CustomerVisitHdrDM> beansNews = new BeanItemContainer<CustomerVisitHdrDM>(
						CustomerVisitHdrDM.class);
				beansNews.addAll(newsList);
				tblClientVisit.setContainerDataSource(beansNews);
				tblClientVisit.setVisibleColumns(new Object[] { "visitDt", "custName" });
				tblClientVisit.setColumnHeaders(new String[] { " Date", "Client Name" });
				tblClientVisit.addGeneratedColumn("custName", new ColumnGenerator() {
					private static final long serialVersionUID = 1L;
					
					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						@SuppressWarnings("unchecked")
						BeanItem<CustomerVisitHdrDM> item = (BeanItem<CustomerVisitHdrDM>) source.getItem(itemId);
						return new Label((String) item.getItemProperty("custName").getValue(), ContentMode.HTML);
					}
				});
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
			logger.warn("calculate days" + e);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, days);
		return now.getTime();
	}
}
