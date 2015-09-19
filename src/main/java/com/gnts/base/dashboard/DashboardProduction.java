package com.gnts.base.dashboard;

import com.gnts.die.txn.DieRequest;
import com.gnts.hcm.txn.ServiceCallForm;
import com.gnts.mfg.txn.ProductOverview;
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

public class DashboardProduction implements ClickListener {
	private static final long serialVersionUID = 1L;
	private Label lblDashboardTitle;
	private VerticalLayout clMainLayout;
	private HorizontalLayout hlHeader;
	private Button btnProductOverview = new Button("Overview", this);
	private Button btnDieSection = new Button("15 Nos.", this);
	private Button btnMoldTrialReq = new Button("13 Nos.", this);
	private Button btnDieCompletion = new Button("17 Nos.", this);
	private Button btnBOM = new Button("16 Nos.", this);
	private Button btnServiceCall = new Button("16 Nos.", this);

	public DashboardProduction() {
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		hlHeader.removeAllComponents();
		CustomLayout custom = new CustomLayout("productiondashboard");
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Production Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		clMainLayout.addComponent(custom);
		btnProductOverview.setStyleName(Runo.BUTTON_LINK);
		btnDieSection.setStyleName(Runo.BUTTON_LINK);
		btnMoldTrialReq.setStyleName(Runo.BUTTON_LINK);
		btnDieCompletion.setStyleName(Runo.BUTTON_LINK);
		btnBOM.setStyleName(Runo.BUTTON_LINK);
		btnServiceCall.setStyleName(Runo.BUTTON_LINK);

		custom.addComponent(btnProductOverview, "enquiry");
		custom.addComponent(btnDieSection, "quotation");
		custom.addComponent(btnMoldTrialReq, "purchaseorder");
		custom.addComponent(btnBOM, "receipts");
		custom.addComponent(btnDieCompletion, "vendorbills");
		custom.addComponent(btnServiceCall, "serviceform");

		custom.addComponent(new CalendarMonthly("DIE_SCHEDULE"), "maintaincedtls");
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnProductOverview) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Product Overview");
			UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new ProductOverview();
		}
		if (event.getButton() == btnDieSection) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Die Section");
			UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new DieRequest();
		}
		if (event.getButton() == btnMoldTrialReq) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Mold Trial Request");
			UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new DieRequest();
		}
		if (event.getButton() == btnDieCompletion) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Die Completion");
			UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new DieRequest();
		}
		if (event.getButton() == btnBOM) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Bill of Material");
			UI.getCurrent().getSession().setAttribute("moduleId", 17L);
			new DieRequest();
		}
		if (event.getButton() == btnServiceCall) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("IS_MARK_FRM", false);
			UI.getCurrent().getSession().setAttribute("IS_QC_FRM", false);

			UI.getCurrent().getSession().setAttribute("IS_PROD_FRM", true);

			UI.getCurrent().getSession().setAttribute("screenName", "Service Call Form");
			UI.getCurrent().getSession().setAttribute("moduleId", 13L);
			new ServiceCallForm();
		}
	}
}
