package com.gnts.erputil.ui;

import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.AppScreensDM;
import com.gnts.base.service.mst.AppScreensService;
import com.gnts.erputil.helper.SpringContextHelper;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class HelpUI extends UI implements ClickListener, ItemClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1558742455996764302L;
	private AppScreensService serviceAppScreen = (AppScreensService) SpringContextHelper.getBean("appScreens");;
	private Long branchId, companyId, roleId, moduleId;
	private Tree treeMenu;
	private String sreenName, systemUser;
	private HorizontalLayout hlTreeMenu;
	private VerticalLayout vlTreeLayout, vlLine;
	private VerticalLayout vlLocal;
	private List<AppScreensDM> appList = null;
	private Logger logger = Logger.getLogger(HelpUI.class);
	private final RichTextArea editor = new RichTextArea();
	private Label lblText;
	private Button btnSave;
	
	public HelpUI() {
		branchId = Long.valueOf(UI.getCurrent().getSession().getAttribute("branchId").toString());
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		systemUser = UI.getCurrent().getSession().getAttribute("systemUserYN").toString();
		roleId = Long.valueOf(UI.getCurrent().getSession().getAttribute("roleId").toString());
		CssLayout clMainLayout = (CssLayout) UI.getCurrent().getSession().getAttribute("clWindow");
		// HorizontalLayout hlHeader= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout);
	}
	
	private void buildView(CssLayout clMainLayout) {
		btnSave = new Button("Edit", this);
		btnSave.setStyleName("savebt");
		btnSave.setVisible(false);
		// hlHeader.removeAllComponents();
		clMainLayout.removeAllComponents();
		treeMenu = new Tree();
		treeMenu.addStyleName("no-children");
		treeMenu.addItemClickListener(this);
		hlTreeMenu = new HorizontalLayout();
		hlTreeMenu.setSizeFull();
		hlTreeMenu.setSpacing(true);
		vlTreeLayout = new VerticalLayout();
		vlTreeLayout.addStyleName("sidebar");
		vlTreeLayout.setWidth("145px");
		vlTreeLayout.addComponent(treeMenu);
		vlLine = new VerticalLayout();
		vlLine.setWidth("2px");
		vlLine.setHeight("100%");
		vlLine.addStyleName("sidebarone");
		lblText = new Label();
		lblText.setContentMode(ContentMode.HTML);
		editor.setWidth("310px");
		editor.setVisible(false);
		vlLocal = new VerticalLayout();
		vlLocal.setSpacing(true);
		vlLocal.setStyleName("sidebar");
		vlLocal.addComponent(lblText);
		vlLocal.addComponent(editor);
		vlLocal.addComponent(btnSave);
		vlLocal.setComponentAlignment(btnSave, Alignment.MIDDLE_CENTER);
		hlTreeMenu.addComponent(vlTreeLayout);
		hlTreeMenu.addComponent(vlLine);
		hlTreeMenu.addComponent(vlLocal);
		hlTreeMenu.setExpandRatio(vlLocal, 1);
		hlTreeMenu.setSizeFull();
		clMainLayout.addComponent(hlTreeMenu);
		clMainLayout.setSizeFull();
		loadTreeMenus();
	}
	
	private void loadTreeMenus() {
		// Create the tree nodes
		appList = serviceAppScreen.getMBaseAppscreenList(roleId, companyId, branchId);
		try {
			for (AppScreensDM mBaseAppObj : appList) {
				treeMenu.addItem(mBaseAppObj.getScreendesc());
				treeMenu.addItem(mBaseAppObj.getModuleId().getModuleName());
				if (mBaseAppObj.getModuleId().getModuleId() != null) {
					treeMenu.setParent(mBaseAppObj.getScreendesc(), mBaseAppObj.getModuleId().getModuleName());
				}
				if (mBaseAppObj.getParentId() != null) {
					treeMenu.setParent(mBaseAppObj.getScreendesc(), mBaseAppObj.getParentId().getScreendesc());
					treeMenu.setChildrenAllowed(mBaseAppObj.getScreendesc(), false);
				}
			}
		}
		catch (Exception e) {
			logger.info("Tree menu build" + e);
		}
	}
	
	private void loadDocuments() {
		try {
			lblText.setVisible(true);
			editor.setVisible(false);
			for (AppScreensDM mbaseAppsScreenList : appList) {
				if (mbaseAppsScreenList.getScreendesc().equals(sreenName)) {
					String doc = mbaseAppsScreenList.getHelpDco();
					lblText.setValue(doc);
					editor.setValue(mbaseAppsScreenList.getHelpDco());
					moduleId = mbaseAppsScreenList.getModuleId().getModuleId();
					break;
				}
			}
			if (systemUser.equals("Y")) {
				btnSave.setVisible(true);
			}
		}
		catch (Exception e) {
			logger.info("load documents" + e);
			e.printStackTrace();
		}
	}
	
	public void buttonClick(ClickEvent event) {
		if (btnSave == event.getButton()) {
			if (btnSave.getCaption().equals("Edit")) {
				lblText.setVisible(false);
				editor.setVisible(true);
				btnSave.setCaption("Save");
			} else if (btnSave.getCaption().equals("Save")) {
				serviceAppScreen.updateHelpDocumentDetails(editor.getValue(), sreenName, moduleId);
				lblText.setVisible(true);
				editor.setVisible(false);
				btnSave.setCaption("Edit");
				loadTreeMenus();
				loadDocuments();
			}
		}
	}
	
	@Override
	public void itemClick(ItemClickEvent event) {
		// TODO Auto-generated method stub
		sreenName = event.getItemId().toString();
		loadDocuments();
	}
	
	@Override
	protected void init(VaadinRequest request) {
		// TODO Auto-generated method stub
	}
}
