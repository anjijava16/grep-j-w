package com.gnts.base.dashboard;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import com.gnts.asm.domain.txn.AssetMaintSchedDM;
import com.gnts.asm.service.txn.AssetMaintSchedService;
import com.gnts.die.domain.txn.DieRequestDM;
import com.gnts.die.service.txn.DieRequestService;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.helper.SpringContextHelper;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Calendar.TimeFormat;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;
import com.vaadin.ui.themes.Runo;

/**
 * @author soundar
 * 
 */
public class CalendarMonthly extends VerticalLayout implements CalendarEventProvider {
	private static final long serialVersionUID = -5436777475398410597L;
	private AssetMaintSchedService serviceMaintSched = (AssetMaintSchedService) SpringContextHelper
			.getBean("AssetMaintSchedul");
	private DieRequestService serviceDieRequest = (DieRequestService) SpringContextHelper.getBean("dieRequest");
	GregorianCalendar calendar = new GregorianCalendar();
	private Calendar calendarComponent;
	private Date currentMonthsFirstDate = null;
	private Label label = new Label("", ContentMode.HTML);
	private String type;
	
	public CalendarMonthly(String type) {
		this.type = type;
		// setTheme("calendartest");
		calendarComponent = new Calendar(this);
		calendarComponent.setTimeFormat(TimeFormat.Format24H);
		calendarComponent.setWidth("99%");
		Date today = new Date();
		calendar.setTime(today);
		calendar.get(GregorianCalendar.MONTH);
		System.out.println("Locale.US()-->" + Locale.US);
		DateFormatSymbols s = new DateFormatSymbols(Locale.US);
		String month = s.getShortMonths()[calendar.get(GregorianCalendar.MONTH)];
		label.setValue("<h1>" + month + " " + calendar.get(GregorianCalendar.YEAR) + "</h1>");
		int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
		calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
		currentMonthsFirstDate = calendar.getTime();
		calendarComponent.setStartDate(currentMonthsFirstDate);
		calendar.add(GregorianCalendar.MONTH, 1);
		calendar.add(GregorianCalendar.DATE, -1);
		calendarComponent.setEndDate(calendar.getTime());
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setMargin(true);
		addComponent(GERPPanelGenerator.createPanel(vl));
		setSizeFull();
		final Button next = new Button("next >>", new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			
			public void buttonClick(ClickEvent event) {
				calendar.setTime(currentMonthsFirstDate);
				calendar.add(GregorianCalendar.MONTH, 1);
				currentMonthsFirstDate = calendar.getTime();
				calendarComponent.setStartDate(currentMonthsFirstDate);
				DateFormatSymbols s = new DateFormatSymbols(getLocale());
				String month = s.getShortMonths()[calendar.get(GregorianCalendar.MONTH)];
				label.setValue("<h1>" + month + " " + calendar.get(GregorianCalendar.YEAR) + "</h1>");
				calendar.add(GregorianCalendar.MONTH, 1);
				calendar.add(GregorianCalendar.DATE, -1);
				calendarComponent.setEndDate(calendar.getTime());
			}
		});
		next.setStyleName(Runo.BUTTON_LINK);
		final Button prev = new Button("<< prev", new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			
			public void buttonClick(ClickEvent event) {
				calendar.setTime(currentMonthsFirstDate);
				calendar.add(GregorianCalendar.MONTH, -1);
				currentMonthsFirstDate = calendar.getTime();
				calendarComponent.setStartDate(currentMonthsFirstDate);
				DateFormatSymbols s = new DateFormatSymbols(getLocale());
				String month = s.getShortMonths()[calendar.get(GregorianCalendar.MONTH)];
				label.setValue("<h1>" + month + " " + calendar.get(GregorianCalendar.YEAR) + "</h1>");
				calendar.add(GregorianCalendar.MONTH, 1);
				calendar.add(GregorianCalendar.DATE, -1);
				calendarComponent.setEndDate(calendar.getTime());
			}
		});
		prev.setStyleName(Runo.BUTTON_LINK);
		vl.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setWidth("100%");
				addComponent(label);
				addComponent(new HorizontalLayout() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
					{
						setWidth("100%");
						setSpacing(true);
						addComponent(prev);
						setComponentAlignment(prev, Alignment.MIDDLE_RIGHT);
						addComponent(next);
						setComponentAlignment(next, Alignment.MIDDLE_RIGHT);
					}
				});
			}
		});
		vl.addComponent(calendarComponent);
		vl.setExpandRatio(calendarComponent, 1);
	}
	
	public List<CalendarEvent> getEvents(Date fromStartDate, Date toEndDate) {
		return getEventsOverlappingForMonthly(fromStartDate, toEndDate);
	}
	
	/**
	 * @param fromStartDate
	 * @param toEndDate
	 * @return
	 */
	private List<CalendarEvent> getEventsOverlappingForMonthly(Date fromStartDate, Date toEndDate) {
		List<CalendarEvent> e = new ArrayList<CalendarEvent>();
		if (type.equalsIgnoreCase("APPOINMENTS")) {
		} else if (type.equalsIgnoreCase("MAIN_SCHEDULE")) {
			for (AssetMaintSchedDM assetMaintSchedDM : serviceMaintSched.getMaintScheduleList(null, fromStartDate,
					toEndDate, null, null, null)) {
				calendar.setTime(assetMaintSchedDM.getMaintainDtt());
				calendar.add(GregorianCalendar.DATE, 2);
				CalendarTestEvent event = getNewEvent(
						assetMaintSchedDM.getAssetName() + " - \n" + assetMaintSchedDM.getMaintaindescription(),
						assetMaintSchedDM.getMaintainDtt(), calendar.getTime());
				if (calendar.getTime().after(new Date())) {
					event.setStyleName("color1");
				} else if (assetMaintSchedDM.getMaintainDtt().equals(new Date())) {
					event.setStyleName("color4");
				} else {
					event.setStyleName("color3");
				}
				event.setDescription(assetMaintSchedDM.getRemarks());
				e.add(event);
			}
		} else if (type.equalsIgnoreCase("DIE_SCHEDULE")) {
			for (DieRequestDM dieRequestDM : serviceDieRequest.getDieRequestList(null, null, null, null, null)) {
				calendar.setTime(dieRequestDM.getRefDate1());
				calendar.add(GregorianCalendar.DATE, 2);
				CalendarTestEvent event = getNewEvent("Ref. Number : " + dieRequestDM.getDieRefNumber()
						+ " - \n Enquiry Number : " + dieRequestDM.getEnquiryNo(), dieRequestDM.getRefDate1(),
						dieRequestDM.getPlanCompleteDate());
				if (dieRequestDM.getPlanCompleteDate().after(new Date())) {
					event.setStyleName("color1");
				} else if (dieRequestDM.getPlanCompleteDate().equals(new Date())) {
					event.setStyleName("color4");
				} else {
					event.setStyleName("color3");
				}
				event.setDescription("Ref. Number : " + dieRequestDM.getDieRefNumber() + " - \n Enquiry Number : "
						+ dieRequestDM.getEnquiryNo() +" Change Note : "+ dieRequestDM.getChangeNote());
				e.add(event);
			}
		}
		return e;
	}
	
	private CalendarTestEvent getNewEvent(String caption, Date start, Date end) {
		CalendarTestEvent event = new CalendarTestEvent();
		event.setCaption(caption);
		event.setStart(start);
		event.setEnd(end);
		return event;
	}
}