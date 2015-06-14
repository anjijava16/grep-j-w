package com.gnts.erputil.constants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.gnts.erputil.ui.DynamicImageResource;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;

public class GERPGalleryChngColGenerator implements Table.ColumnGenerator {
	public Object generateCell(final Table source, final Object itemId, Object columnId) {
		final Property prop = source.getItem(itemId).getItemProperty(columnId);
		StreamSource streamSource = new StreamSource() {
			public InputStream getStream() {
				final Item item = source.getItem(itemId);
				byte[] bas = (byte[]) prop.getValue();
				return (bas == null) ? null : new ByteArrayInputStream(bas);
			}
		};
		StreamResource resource = new DynamicImageResource(streamSource);
		System.out.println("Resource----->" + resource);
		Embedded embedded = new Embedded("", resource);
		embedded.setWidth("70px");
		embedded.setHeight("50px");
		return embedded;
	}
}