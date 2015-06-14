package com.gnts.base.dashboard;

import com.gnts.base.mst.Employee;
import com.gnts.hcm.rpt.Payslip;
import com.gnts.hcm.txn.AttendenceProc;
import com.gnts.hcm.txn.EmployeeAttendence;
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

public class DashboardHCMView implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Label lblDashboardTitle;
	VerticalLayout clMainLayout;
	HorizontalLayout hlHeader;
	private Button btnEmployeeCount = new Button("100", this);
	private Button btnEmpAtten = new Button("10", this);
	private Button btnAttenProcess = new Button("10", this);
	private Button btnPayslip = new Button("10", this);
	
	public DashboardHCMView() {
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		hlHeader.removeAllComponents();
		CustomLayout custom = new CustomLayout("dashhcm");
		btnEmployeeCount.setStyleName("borderless-colored");
		btnEmpAtten.setStyleName("borderless-colored");
		btnPayslip.setStyleName("borderless-colored");
		btnAttenProcess.setStyleName("borderless-colored");
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Human Capital Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		clMainLayout.addComponent(custom);
		custom.addComponent(btnEmployeeCount, "employeecount");
		custom.addComponent(btnEmpAtten, "empattencount");
		custom.addComponent(btnPayslip, "payslipcount");
		custom.addComponent(btnAttenProcess, "attenproccount");
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
	}
}
