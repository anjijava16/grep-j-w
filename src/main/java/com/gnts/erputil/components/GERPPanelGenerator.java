package com.gnts.erputil.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

public class GERPPanelGenerator {
	public static CssLayout createPanel(Component content) {
        CssLayout panel = new CssLayout();
        panel.addStyleName("layout-panel");
       
        panel.setSizeFull();
        panel.addComponent(content);
        return panel;
    }
}
