/**
 * File Name	:	TrackLogUI.java
 * Description	:	Used for view T_TOOL_TRACK_LOGS details
 * Author		:	Prakash.s
 * Date			:	mar 12, 2014 
 * Modified By  :   prakash.s
 * Description	:
 *
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies pvt. ltd.
 * Version         Date           Modified By             Remarks
 * 1.0			   3-Dec-2014	  Gangaraj S				
 */
package com.gnts.tools.rpt;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTimeField;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.tools.domain.rpt.TrackLogDM;
import com.gnts.tools.service.rpt.TrackLogService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolyline;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class TrackLogUI implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TrackLogService beantooltracker = (TrackLogService) SpringContextHelper.getBean("tooltrackLog");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private Table tbltooltrackl;
	private Long companyId;
	private String screenName;
	private Long userId;
	// Buttons
	private Button btndelete, btnmapfinder;
	private Button btnCancel;
	private Button btnSearch, btnReset;
	private Button btnHome;
	// components
	private ComboBox cbemployeelist;
	private PopupDateField dfSearchLogDate;
	private GERPTimeField tfsttime = new GERPTimeField("Start Time");
	private GERPTimeField tfendtime = new GERPTimeField("End Time");
	private Window notifications;
	// pagination
	private int total = 0;
	private BeanItemContainer<TrackLogDM> beantooltrack = null;
	private List<TrackLogDM> trackList = new ArrayList<TrackLogDM>();
	private EmployeeDM employee;
	private Long empId;
	// for header layoute
	private Label lblFormTittle;
	private Label lblSaveNotification, lblNotificationIcon;
	private Button btnDownload = new Button("Download", this);
	private CheckBox cockeckall = new CheckBox();
	private HorizontalLayout hlButtonLayout1;
	// layout Components
	private VerticalLayout vlSearchLayout = new VerticalLayout();
	private VerticalLayout vlTableLayout = new VerticalLayout();
	private VerticalLayout vlTableForm = new VerticalLayout();
	private HorizontalLayout hlAddEditLayout = new HorizontalLayout();
	// for exporter
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	// googlemap Components
	private VerticalLayout vlmapFinder = new VerticalLayout();
	private GoogleMap googleMap = new GoogleMap(new LatLon(21.0000, 78.0000), 4.0, "");
	private Logger logger = Logger.getLogger(TrackLogUI.class);
	
	public TrackLogUI() {
		UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		VerticalLayout clArgumentLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		HorizontalLayout hlHeaderLayout = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		userId = (Long) UI.getCurrent().getSession().getAttribute("userId");
		buildview(clArgumentLayout, hlHeaderLayout);
	}
	
	@SuppressWarnings("serial")
	private void buildview(VerticalLayout clArgumentLayout, HorizontalLayout hlHeaderLayout) {
		hlHeaderLayout.removeAllComponents();
		lblSaveNotification = new Label();
		lblSaveNotification.setContentMode(ContentMode.HTML);
		lblNotificationIcon = new Label();
		lblFormTittle = new Label();
		lblFormTittle.setContentMode(ContentMode.HTML);
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName + "</b>&nbsp;::&nbsp;Search");
		btndelete = new Button("Delete", this);
		btnCancel = new Button("Cancel", this);
		btnSearch = new Button("Search", this);
		btnReset = new Button("Reset", this);
		btnmapfinder = new Button("Map", this);
		btnDownload = new Button("Download", this);
		btndelete.setEnabled(false);
		btnmapfinder.setStyleName("map");
		btnCancel.addStyleName("cancelbt");
		btnReset.addStyleName("resetbt");
		btnSearch.setStyleName("searchbt");
		btnDownload.addStyleName("downloadbt");
		btnHome = new Button("", this);
		btnHome.setStyleName("homebtn");
		btndelete.addStyleName("delete");
		btndelete.setDescription("Delete Email log Details");
		btnReset.setDescription("Search Email log Details");
		btnSearch.setDescription("Reset Search");
		btnDownload.setDescription("Download");
		btnmapfinder.setDescription("Map Finder");
		tbltooltrackl = new Table();
		tbltooltrackl.setStyleName(Runo.TABLE_SMALL);
		tbltooltrackl.setPageLength(12);
		tbltooltrackl.setSizeFull();
		tbltooltrackl.setFooterVisible(true);
		tbltooltrackl.setMultiSelect(true);
		/*-
		 * tfSearchemailid=new TextField("Email Id");
		 * tfSearchemailid.setInputPrompt("Enter Email Id");
		 */
		cbemployeelist = new ComboBox("Employee Name");
		cbemployeelist.setInputPrompt("Select-");
		cbemployeelist.setItemCaptionPropertyId("firstname");
		loadProductList();
		cbemployeelist.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				final Object itemId = event.getProperty().getValue();
				if (itemId != null) {
					final BeanItem<?> item = (BeanItem<?>) cbemployeelist.getItem(itemId);
					employee = (EmployeeDM) item.getBean();
					empId = employee.getEmployeeid();
				}
			}
		});
		cbemployeelist.setImmediate(true);
		cbemployeelist.setNullSelectionAllowed(true);
		dfSearchLogDate = new PopupDateField("Email.start");
		dfSearchLogDate.setDateFormat("dd-MMM-yyyy");
		dfSearchLogDate.setInputPrompt("Select Date");
		btnDownload.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// UI.getCurrent()..clearDashboardButtonBadge();
				event.getButton().removeStyleName("unread");
				// event.getButton().setDescription("Notifications");
				if (notifications != null && notifications.getUI() != null) notifications.close();
				else {
					buildNotifications(event);
					UI.getCurrent().addWindow(notifications);
					notifications.focus();
					((VerticalLayout) UI.getCurrent().getContent()).addLayoutClickListener(new LayoutClickListener() {
						@Override
						public void layoutClick(LayoutClickEvent event) {
							notifications.close();
							((VerticalLayout) UI.getCurrent().getContent()).removeLayoutClickListener(this);
						}
					});
				}
			}
		});
		// Searchpanel
		FormLayout flSearchForm1 = new FormLayout();
		flSearchForm1.addComponent(cbemployeelist);
		FormLayout flSearchForm2 = new FormLayout();
		flSearchForm2.addComponent(dfSearchLogDate);
		FormLayout flSearchForm3 = new FormLayout();
		flSearchForm3.addComponent(tfsttime);
		FormLayout flSearchForm4 = new FormLayout();
		flSearchForm4.addComponent(tfendtime);
		VerticalLayout hlSearchButtonLayout = new VerticalLayout();
		hlSearchButtonLayout.setSpacing(true);
		hlSearchButtonLayout.addComponent(btnSearch);
		hlSearchButtonLayout.addComponent(btnReset);
		hlSearchButtonLayout.setWidth("17%");
		hlSearchButtonLayout.addStyleName("topbarthree");
		hlSearchButtonLayout.setMargin(true);
		hlSearchButtonLayout.setSizeFull();
		HorizontalLayout hlSearch = new HorizontalLayout();
		hlSearch.addComponent(flSearchForm1);
		hlSearch.addComponent(flSearchForm2);
		hlSearch.addComponent(flSearchForm3);
		hlSearch.addComponent(flSearchForm4);
		hlSearch.setSpacing(true);
		hlSearch.setMargin(true);
		GridLayout glSearchPanel = new GridLayout();
		glSearchPanel.setSpacing(true);
		glSearchPanel.setColumns(2);
		glSearchPanel.addComponent(hlSearch);
		glSearchPanel.addComponent(hlSearchButtonLayout);
		glSearchPanel.setSizeFull();
		final VerticalLayout vlSearchPanel = new VerticalLayout();
		vlSearchPanel.setSpacing(true);
		vlSearchPanel.setSizeFull();
		vlSearchPanel.addComponent(glSearchPanel);
		vlSearchLayout = new VerticalLayout();
		vlSearchLayout.addComponent(GERPPanelGenerator.createPanel(vlSearchPanel));
		vlSearchLayout.setMargin(true);
		// table panel
		HorizontalLayout hlFileDownload = new HorizontalLayout();
		hlFileDownload.setSpacing(true);
		hlFileDownload.addComponent(btnDownload);
		hlFileDownload.setComponentAlignment(btnDownload, Alignment.MIDDLE_CENTER);
		HorizontalLayout hlTableTittleLayout = new HorizontalLayout();
		hlTableTittleLayout.addComponent(cockeckall);
		hlTableTittleLayout.addComponent(btnHome);
		hlTableTittleLayout.addComponent(btnmapfinder);
		hlTableTittleLayout.addComponent(btndelete);
		hlAddEditLayout.addStyleName("topbarthree");
		hlAddEditLayout.setWidth("100%");
		hlAddEditLayout.addComponent(hlTableTittleLayout);
		hlAddEditLayout.addComponent(hlFileDownload);
		hlAddEditLayout.setComponentAlignment(hlFileDownload, Alignment.MIDDLE_RIGHT);
		hlAddEditLayout.setHeight("30px");
		vlTableForm.setSizeFull();
		// vlTableForm.setMargin(true);
		vlTableForm.setMargin(new MarginInfo(false, true, false, true)); /* new */
		vlTableForm.addComponent(hlAddEditLayout);
		vlTableForm.addComponent(tbltooltrackl);
		vlTableForm.setExpandRatio(tbltooltrackl, 1); /* new */
		vlTableLayout.setStyleName(Runo.PANEL_LIGHT);
		vlTableLayout.addComponent(vlTableForm);
		clArgumentLayout.addComponent(vlSearchLayout);
		clArgumentLayout.addComponent(vlTableLayout);
		// add notification and title name to header layout
		hlButtonLayout1 = new HorizontalLayout();
		hlButtonLayout1.addComponent(btnCancel);
		hlButtonLayout1.setVisible(false);
		HorizontalLayout hlNotificationLayout = new HorizontalLayout();
		hlNotificationLayout.addComponent(lblNotificationIcon);
		hlNotificationLayout.setComponentAlignment(lblNotificationIcon, Alignment.MIDDLE_CENTER);
		hlNotificationLayout.addComponent(lblSaveNotification);
		hlNotificationLayout.setComponentAlignment(lblSaveNotification, Alignment.MIDDLE_CENTER);
		hlHeaderLayout.addComponent(lblFormTittle);
		hlHeaderLayout.setComponentAlignment(lblFormTittle, Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlNotificationLayout);
		hlHeaderLayout.setComponentAlignment(hlNotificationLayout, Alignment.MIDDLE_CENTER);
		hlHeaderLayout.addComponent(hlButtonLayout1);
		hlHeaderLayout.setComponentAlignment(hlButtonLayout1, Alignment.MIDDLE_RIGHT);
		loadgeneratedvalue();
		populatedAndConfig(false);
		// Excporter
		excelexporter.setTableToBeExported(tbltooltrackl);
		excelexporter.setDownloadFileName("tooltrack");
		csvexporter.setTableToBeExported(tbltooltrackl);
		pdfexporter.setTableToBeExported(tbltooltrackl);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		csvexporter.setDownloadFileName("tooltrack");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
		pdfexporter.setDownloadFileName("tooltrack");
		// map
		vlmapFinder.addComponent(googleMap);
		googleMap.setSizeFull();
		vlmapFinder.setSizeFull();
		vlmapFinder.setWidth("100%");
		vlmapFinder.setHeight("390");
		googleMap.setMinZoom(4.0);
		googleMap.setMaxZoom(16.0);
		// checkall
		cockeckall.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue().equals(true)) {
					selectAll(true);
					btndelete.setEnabled(true);
				} else {
					selectAll(false);
					btndelete.setEnabled(false);
				}
			}
		});
		cockeckall.setImmediate(true);
	}
	
	/*
	 * buildNotifications()-->this function is used for poppupview for Download components
	 */
	private void buildNotifications(ClickEvent event) {
		try {
			notifications = new Window();
			VerticalLayout l = new VerticalLayout();
			l.setMargin(true);
			l.setSpacing(true);
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
		catch (Exception e) {
			logger.error("fn_buildNotifications()----->" + e);
		}
	}
	
	/*
	 * populatedAndConfig()-->this function is used for populationg the records to Grid table
	 */
	public void populatedAndConfig(boolean search) {
		try {
			tbltooltrackl.removeAllItems();
			if (search) {
				String logstart = null;
				String logend = null;
				String date = null;
				if (dfSearchLogDate.getValue() != null) {
					date = DateUtils.datetostringsimple(dfSearchLogDate.getValue());
					String stTime = tfsttime.getHorsMunites();
					String endtime = tfendtime.getHorsMunites();
					logstart = date.concat(" ").concat(stTime);
					logend = date.concat(" ").concat(endtime);
				}
				if (logstart != null || logend != null || empId != null || companyId != null) {
					trackList = beantooltracker.getToolTrackList(companyId, userId, null, logstart, logend);
					total = trackList.size();
				}
			} else {
				trackList = beantooltracker.getToolTrackList(companyId, userId, null, null, null);
				total = trackList.size();
			}
			beantooltrack = new BeanItemContainer<TrackLogDM>(TrackLogDM.class);
			beantooltrack.addAll(trackList);
			tbltooltrackl.setSelectable(true);
			final String CHECKBOX_COLUMN_ID = "selected";
			tbltooltrackl.setContainerDataSource(beantooltrack);
			tbltooltrackl.setColumnWidth(CHECKBOX_COLUMN_ID, 30);
			tbltooltrackl.setColumnWidth("tracklogId", 60);
			tbltooltrackl.setColumnWidth("logTimstamp", 200);
			tbltooltrackl.setVisibleColumns(new Object[] { CHECKBOX_COLUMN_ID, "tracklogId", "logTimstamp", "latitude",
					"longitude" });
			tbltooltrackl.setColumnHeaders(new String[] { "", "Ref.Id", "Time Stamp", "Latitude", "Longitude" });
			tbltooltrackl.setColumnFooter("longitude", "No.of Records : " + total);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	private void selectAll(Boolean value) {
		try {
			if (value) {
				for (TrackLogDM log : trackList) {
					log.setSelected(true);
				}
			} else {
				for (TrackLogDM log : trackList) {
					log.setSelected(false);
				}
			}
			populatedAndConfig(false);
		}
		catch (Exception e) {
			logger.error("error during checkall/unckechall values on the table, The Error is ----->" + e);
		}
	}
	
	private void loadgeneratedvalue() {
		tbltooltrackl.addGeneratedColumn("selected", new ColumnGenerator() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Component generateCell(final Table source, final Object itemId, final Object columnId) {
				final TrackLogDM bean = (TrackLogDM) itemId;
				final CheckBox checkBox = new CheckBox();
				checkBox.setImmediate(true);
				checkBox.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = 1L;
					
					@Override
					public void valueChange(final ValueChangeEvent event) {
						bean.setSelected((Boolean) event.getProperty().getValue());
						if (event.getProperty().getValue().equals(true)) {
							btndelete.setEnabled(true);
						} else {
							btndelete.setEnabled(false);
						}
					}
				});
				if (bean.isSelected()) {
					checkBox.setValue(true);
				} else {
					checkBox.setValue(false);
				}
				return checkBox;
			}
		});
	}
	
	private void loadProductList() {
		try {
			List<EmployeeDM> list = servicebeanEmployee.getEmployeeList(null, null, null, null, companyId, null,
					userId, null, null, null);
			BeanItemContainer<EmployeeDM> empList = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
			empList.addAll(list);
			cbemployeelist.setContainerDataSource(empList);
		}
		catch (Exception e) {
			logger.error("fn_loadProductList_Exception Caught->" + e);
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (btndelete == event.getButton()) {
			try {
				if (trackList != null) {
					for (TrackLogDM obj : trackList) {
						if (obj.isSelected()) {
							beantooltracker.deleteTrackLogDetails(obj.getTracklogId());
						}
					}
				}
			}
			catch (Exception e) {
				logger.error("deleting trackLog error" + e);
			}
			populatedAndConfig(false);
			btndelete.setEnabled(false);
		}
		if (btnSearch == event.getButton()) {
			try {
				populatedAndConfig(true);
			}
			catch (Exception e) {
				logger.info("fn_populatedAndConfig()_caught while searc--->" + e);
			}
		}
		if (btnReset == event.getButton()) {
			populatedAndConfig(false);
			cbemployeelist.setValue(null);
			dfSearchLogDate.setValue(null);
			empId = null;
		} else if (btnHome == event.getButton()) {
			vlTableForm.removeAllComponents();
			vlTableForm.addComponent(hlAddEditLayout);
			vlTableForm.addComponent(tbltooltrackl);
			populatedAndConfig(false);
		}
		if (btnmapfinder == event.getButton()) {
			try {
				vlTableForm.removeAllComponents();
				vlTableForm.addComponent(hlAddEditLayout);
				vlTableForm.addComponent(vlmapFinder);
				ArrayList<LatLon> points = new ArrayList<LatLon>();
				for (TrackLogDM list : trackList) {
					try {
						googleMap.addMarker(list.getLogTimstamp(), new LatLon(new Double(list.getLatitude()),
								new Double(list.getLongitude())), true, "");
						points.add(new LatLon(new Double(list.getLatitude()), new Double(list.getLongitude())));
					}
					catch (Exception e) {
					}
				}
				GoogleMapPolyline overlay = new GoogleMapPolyline(points, "#d31717", 0.5, 5);
				googleMap.addPolyline(overlay);
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.info("fn_addGoogleMap--->" + e);
			}
		}
	}
}
