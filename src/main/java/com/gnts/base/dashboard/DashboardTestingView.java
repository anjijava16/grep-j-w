package com.gnts.base.dashboard;

import com.gnts.die.txn.DieRequest;
import com.gnts.hcm.txn.ServiceCallForm;
import com.gnts.mfg.txn.QATest;
import com.gnts.mfg.txn.QCTest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class DashboardTestingView implements ClickListener {
	private static final long serialVersionUID = 1L;
	private Label lblDashboardTitle;
	private VerticalLayout clMainLayout;
	private HorizontalLayout hlHeader;
	private Button btnQCCount = new Button("11 Nos.", this);
	private Button btnQACount = new Button("15 Nos.", this);
	private Button btnSCFCount = new Button("13 Nos.", this);
	private Button btnDieReqCount = new Button("17 Nos.", this);
	private Button btnOthers = new Button("0 Nos.", this);
	
	public DashboardTestingView() {
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		hlHeader.removeAllComponents();
		CustomLayout custom = new CustomLayout("testingdashboard");
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Testing Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		clMainLayout.addComponent(custom);
		btnQCCount.setStyleName(Runo.BUTTON_LINK);
		btnQACount.setStyleName(Runo.BUTTON_LINK);
		btnSCFCount.setStyleName(Runo.BUTTON_LINK);
		btnDieReqCount.setStyleName(Runo.BUTTON_LINK);
		btnOthers.setStyleName(Runo.BUTTON_LINK);
		custom.addComponent(btnQCCount, "qc");
		custom.addComponent(btnQACount, "qa");
		custom.addComponent(btnSCFCount, "scf");
		custom.addComponent(btnDieReqCount, "dierequest");
		custom.addComponent(btnOthers, "others");
		custom.addComponent(new CalendarMonthly("TEST_QC_SCHEDULE"), "testschedule");
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnQCCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "QC Test");
			new QCTest();
		} else if (event.getButton() == btnQACount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "QA Test");
			new QATest();
		} else if (event.getButton() == btnSCFCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Service Call Form");
			new ServiceCallForm();
		} else if (event.getButton() == btnDieReqCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Die Request");
			new DieRequest();
		} else if (event.getButton() == btnOthers) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Others");
		}
	}
}
