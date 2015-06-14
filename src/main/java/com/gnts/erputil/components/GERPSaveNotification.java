package com.gnts.erputil.components;

import com.gnts.erputil.constants.GERPConstants;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class GERPSaveNotification {
	public GERPSaveNotification() {
		Label lblNotification = (Label) UI.getCurrent().getSession().getAttribute("lblNotification");
		if (lblNotification != null) {
			lblNotification.setIcon(new ThemeResource("img/success_small.png"));
			lblNotification.setCaption(GERPConstants.saveMsg);
		}
	}
}
