package com.gnts.base.dashboard;

import com.gnts.fms.txn.Accounts;
import com.gnts.fms.txn.Transactions;
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

public class DashboardFinanceView implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Label lblDashboardTitle;
	VerticalLayout clMainLayout;
	HorizontalLayout hlHeader;
	private Button btnAccountsCount = new Button("100", this);
	private Button btnTransCount = new Button("10", this);
	
	public DashboardFinanceView() {
		clMainLayout = (VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		hlHeader = (HorizontalLayout) UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeader);
	}
	
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeader) {
		hlHeader.removeAllComponents();
		CustomLayout custom = new CustomLayout("dashfinance");
		btnAccountsCount.setStyleName("borderless-colored");
		btnTransCount.setStyleName("borderless-colored");
		clMainLayout.removeAllComponents();
		lblDashboardTitle = new Label();
		lblDashboardTitle.setContentMode(ContentMode.HTML);
		lblDashboardTitle.setValue("&nbsp;&nbsp;<b> Finance Dashboard</b>");
		hlHeader.addComponent(lblDashboardTitle);
		hlHeader.setComponentAlignment(lblDashboardTitle, Alignment.MIDDLE_LEFT);
		clMainLayout.addComponent(custom);
		custom.addComponent(btnAccountsCount, "accountscount");
		custom.addComponent(btnTransCount, "transcount");
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnAccountsCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Accounts Setup");
			new Accounts();
		}
		if (event.getButton() == btnTransCount) {
			clMainLayout.removeAllComponents();
			hlHeader.removeAllComponents();
			UI.getCurrent().getSession().setAttribute("screenName", "Transactions");
			new Transactions();
		}
	}
}
