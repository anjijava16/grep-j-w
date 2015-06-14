package com.gnts.erputil.components;

import com.vaadin.ui.Table;
import com.vaadin.ui.themes.Runo;

public class GERPTable extends Table{

	public GERPTable(){
		setStyleName(Runo.TABLE_SMALL);
		setSizeFull();
		setFooterVisible(true);
		setSelectable(true);
		setImmediate(true);
		setColumnCollapsingAllowed(false);
	}
}
