package com.gnts.asm.mst;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CustomComponent;

public class test extends CustomComponent {
	@AutoGenerated
	private AbsoluteLayout mainLayout;
	
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public test() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		// TODO add user code here
	}
	
	@AutoGenerated
	private void buildMainLayout() {
		// the main layout and components will be created here
		mainLayout = new AbsoluteLayout();
	}
}
