package com.gnts.erputil.components;

import com.google.common.eventbus.Subscribe;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

public final class NotificationsButton extends Button {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String STYLE_UNREAD = "unread";
    public static final String ID = "dashboard-notifications";

    public NotificationsButton() {
        setIcon(FontAwesome.BELL);
        setId(ID);
        addStyleName("notifications");
        addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        //DashboardEventBus.register(this);
    }

    @Subscribe
    public void updateNotificationsCount() {
        setUnreadCount(2);
    }

    public void setUnreadCount(final int count) {
        setCaption(String.valueOf(count));

        String description = "Notifications";
        if (count > 0) {
            addStyleName(STYLE_UNREAD);
            description += " (" + count + " unread)";
        } else {
            removeStyleName(STYLE_UNREAD);
        }
        setDescription(description);
    }
}
