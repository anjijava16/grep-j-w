package com.gnts.erputil.components;

import java.text.DecimalFormat;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public final class TopTenMoviesTable extends Table {
	@Override
	protected String formatPropertyValue(final Object rowId, final Object colId, final Property<?> property) {
		String result = super.formatPropertyValue(rowId, colId, property);
		if (colId.equals("revenue")) {
			if (property != null && property.getValue() != null) {
				Double r = (Double) property.getValue();
				String ret = new DecimalFormat("#.##").format(r);
				result = "$" + ret;
			} else {
				result = "";
			}
		}
		return result;
	}
	
	public TopTenMoviesTable() {
		setCaption("Top 10 Titles by Revenue");
		addStyleName(ValoTheme.TABLE_BORDERLESS);
		addStyleName(ValoTheme.TABLE_NO_STRIPES);
		addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
		addStyleName(ValoTheme.TABLE_SMALL);
		setSortEnabled(false);
		setColumnAlignment("revenue", Align.RIGHT);
		setRowHeaderMode(RowHeaderMode.INDEX);
		setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		setSizeFull();
		setSortAscending(false);
	}
}
