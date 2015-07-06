package com.gnts.erputil.components;

import com.vaadin.ui.Table;
import com.vaadin.ui.themes.Runo;

public class GERPTable extends Table {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GERPTable() {
		setStyleName(Runo.TABLE_SMALL);
		setSizeFull();
		setFooterVisible(true);
		setSelectable(true);
		setImmediate(true);
		setColumnCollapsingAllowed(false);
	}
}
