package com.gnts.base.dashboard;

import com.gnts.mfg.txn.WorkOrderPlan;
import com.gnts.mms.mst.ProductBomHdr;
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

public class DashboardPlanningView implements ClickListener {
	private static final long serialVersionUID = 1L;
	private Label lblDashboardTitle;
	private VerticalLayout clMainLayout;
	private HorizontalLayout hlHeader;
	private Button btnWOPlanCount = new Button("0 Nos.", this);
	private Button btnBOMCount = new Button("0 Nos.", this);
	private Button btnSerCallForm = new Button("0 Nos.", this);
	private Button btnDieReqCount = new Button("0 Nos.", this);
	private Button btnOthers = new Button("0 Nos.", this);
	
	public DashboardPlanningView() {
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		hlHeader.removeAllComponents();
		CustomLayout custom = new CustomLayout("planningdashboard");
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Planning Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		clMainLayout.addComponent(custom);
		btnWOPlanCount.setStyleName(Runo.BUTTON_LINK);
		btnBOMCount.setStyleName(Runo.BUTTON_LINK);
		btnSerCallForm.setStyleName(Runo.BUTTON_LINK);
		btnDieReqCount.setStyleName(Runo.BUTTON_LINK);
		btnOthers.setStyleName(Runo.BUTTON_LINK);
		custom.addComponent(btnWOPlanCount, "woplan");
		custom.addComponent(btnBOMCount, "bom");
		custom.addComponent(btnSerCallForm, "scf");
		custom.addComponent(btnDieReqCount, "dierequest");
		custom.addComponent(btnOthers, "others");
		custom.addComponent(new CalendarMonthly("PLAN_SCHEDULE"), "testschedule");
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnWOPlanCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Workorder Plan");
			new WorkOrderPlan();
		} else if (event.getButton() == btnBOMCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "QA Test");
			new ProductBomHdr();
		} else if (event.getButton() == btnSerCallForm) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
		} else if (event.getButton() == btnDieReqCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
		} else if (event.getButton() == btnOthers) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Others");
		}
	}
}
