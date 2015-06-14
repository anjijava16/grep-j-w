/**
 * File Name 		: MRCMCustomerTypes.java 
 * Description 		: this class is used for add/edit Group details. 
 * Author 			: MADHU
 * Date 			: Apr 26, 2014
 * Modification 	:
 * Modified By 		: madhu
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Apr 26, 2014        madhu		          Intial Version
 * 
 */
package com.gnts.rcm.mst;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.erputil.Common;
import com.gnts.erputil.util.DateUtils;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.rcm.domain.mst.CustomerTypeDM;
import com.gnts.rcm.service.mst.CustomerTypeService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;

public class CustomerGroup implements ClickListener{
	private static final long serialVersionUID = 1L;
	private Button btnAdd;
	private Button btnEdit;
	private Button btnCancel, btnSave, btnSearch, btnClear, btnDownload;
	private Table tblRCM;
	private TextField tfGname, tfSearchGname;
	private ComboBox cbStatus, cbSearchStatus;
	private VerticalLayout vlMainPanel = new VerticalLayout();
	private VerticalLayout vlSearchPanel = new VerticalLayout();
	private VerticalLayout vlTablePanel = new VerticalLayout();
	private Button btnAuditRecords;
	private Button btnHome,btnBack;
	private VerticalLayout vlAudit;
	private VerticalLayout vlTable = new VerticalLayout();
	private HorizontalLayout hlAddEdit = new HorizontalLayout();
	private HorizontalLayout hlFileDownload;
	private BeanItemContainer<StatusDM> stausBeans = null;
	private String loginUserName, screenName;
	private Label lblFormTittle,lblFormTitle1,lblAddEdit;
	private Label lblNotification, lblNotificationIcon;
	private CustomerTypeService service = (CustomerTypeService) SpringContextHelper
			.getBean("rcmcustomer");
	String custGroupName;
	Long custGroupId;
	String grpStatus;
	private Long companyId;
	private int total;
	private BeanItemContainer<CustomerTypeDM> beans = null;
	String primaryId;
	private HorizontalLayout hlButtonLayout1,hlBreadCrumbs;
	private Window notifications;
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	private Logger logger = Logger.getLogger(CustomerGroup.class);
	
	public CustomerGroup() {
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		VerticalLayout clMainLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		HorizontalLayout hlHeaderLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeaderLayout);
	}
	
	/*
	 * buildMainview()-->for screen UI design
	 * 
	 * @param  clArgumentLayout hlHeaderLayout
	 */
	private void buildView(VerticalLayout clMainLayout, HorizontalLayout hlHeaderLayout) {
		hlHeaderLayout.removeAllComponents();

		lblNotification = new Label();
		lblNotification.setContentMode(ContentMode.HTML);
		lblNotificationIcon = new Label();

		// button declaration
		btnHome=new Button("Home",this);
		btnAdd = new Button("Add", this);
		btnCancel = new Button("Cancel", this);
		btnSave = new Button("Save", this);
		btnEdit = new Button("Edit", this);
		btnSearch = new Button("Search", this);
		btnDownload = new Button("Download", this);
		btnBack=new Button("Search",this);
		
		/**
		 * set the descriptions for buttons
		 */
		//btnSearch.setDescription("Search Records");
		btnClear = new Button("Reset", this);
		//btnClear.setDescription("Reset Search");
		btnEdit.setEnabled(false);
		/*btnAdd.setDescription("Add bank Detail");
		btnSave.setDescription("Save bank detail");*/

		/**
		 * set the style for buttons
		 */
		/*btnEdit.setDescription("Edit Bank Detail");
		btnCancel.setDescription("Return to Search");
		btnDownload.setDescription("Download");*/
		btnHome.addStyleName("homebtn");
		btnAdd.addStyleName("add");
		btnCancel.addStyleName("cancelbt");
		btnDownload.addStyleName("downloadbt");
		btnSave.addStyleName("savebt");
		btnEdit.addStyleName("editbt");
		btnAuditRecords=new Button("Audit History",this);
		btnAuditRecords.setStyleName("hostorybtn");
		//btnAuditRecords.setDescription("Audit history");
		btnBack.setStyleName("link");
		btnSearch.addStyleName("searchbt");
		btnClear.addStyleName("resetbt");
		btnHome=new Button("Home",this);
		//btnHome.setDescription("Home");
		btnHome.setStyleName("homebtn");
		btnHome.setEnabled(false);
		
		//btnHome.setVisible(false);
		//btnDownload.setDescription("Download");
		// add fields to panel
		tfGname = new TextField("Group name");
		tfGname.setWidth("160");
		tfGname.setRequired(true);

		cbStatus = new ComboBox("Status");
		cbStatus.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbStatus.setItemCaptionPropertyId("desc");
		cbStatus.setImmediate(true);
		cbStatus.setNullSelectionAllowed(false);
		stausBeans = new BeanItemContainer<StatusDM>(StatusDM.class);
		stausBeans.addAll(Common.listStatus);
		cbStatus.setContainerDataSource(stausBeans);
		cbStatus.setWidth("120");
		
		//tfGname.setInputPrompt("Bank name");
		

		FormLayout flFormLayout1 = new FormLayout();
		flFormLayout1.setSpacing(true);
		flFormLayout1.addComponent(tfGname);
		
		FormLayout flFormLayout3 = new FormLayout();
		flFormLayout3.setSpacing(true);
		flFormLayout3.addComponent(cbStatus);
						
		HorizontalLayout hlFormLayout = new HorizontalLayout();
		hlFormLayout.setSpacing(true);
		hlFormLayout.addComponent(flFormLayout1);
		hlFormLayout.addComponent(flFormLayout3);

		GridLayout vlMainPanelgrid = new GridLayout(1, 1);
		vlMainPanelgrid.setSpacing(true);
		vlMainPanelgrid.setMargin(true);
		vlMainPanelgrid.setSizeFull();

		vlMainPanelgrid.addComponent(hlFormLayout);

		lblFormTittle = new Label();
		lblFormTittle.setContentMode(ContentMode.HTML);

		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;Search");
		lblFormTitle1=new Label();
		lblFormTitle1.setContentMode(ContentMode.HTML);

		lblFormTitle1.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;");
		
		lblAddEdit=new Label();
		lblAddEdit.setContentMode(ContentMode.HTML);
		
		vlMainPanel = new VerticalLayout();
		vlMainPanel.addComponent(PanelGenerator.createPanel(vlMainPanelgrid));
		vlMainPanel.setMargin(true);
		vlMainPanel.setVisible(false);

		// add save and cancel to to layout
		hlButtonLayout1 = new HorizontalLayout();
		hlButtonLayout1.addComponent(btnSave);
		hlButtonLayout1.addComponent(btnCancel);
		hlButtonLayout1.setVisible(false);

		// add search fields to panel
		tfSearchGname = new TextField("Group name");
		//tfSearchGname.setInputPrompt("Bank name");
		

		cbSearchStatus = new ComboBox("Status");
		cbSearchStatus.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		//cbSearchStatus.setInputPrompt(Common.SELECT_PROMPT);
		cbSearchStatus.setImmediate(true);
		cbSearchStatus.setNullSelectionAllowed(true);
		cbSearchStatus.setItemCaptionPropertyId("desc");
		cbSearchStatus.setWidth("120px");
		stausBeans = new BeanItemContainer<StatusDM>(StatusDM.class);
		stausBeans.addAll(Common.listStatus);
		cbSearchStatus.setContainerDataSource(stausBeans);


		FormLayout flSearch1 = new FormLayout();
		flSearch1.addComponent(tfSearchGname);
		FormLayout flSearch2 = new FormLayout();
		flSearch2.addComponent(cbSearchStatus);

		HorizontalLayout hlSearchLayout = new HorizontalLayout();
		hlSearchLayout.addComponent(flSearch1);
		hlSearchLayout.addComponent(flSearch2);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);

		VerticalLayout vlSearchButtonLayout = new VerticalLayout();
		vlSearchButtonLayout.setSpacing(true);
		vlSearchButtonLayout.addComponent(btnSearch);
		vlSearchButtonLayout.addComponent(btnClear);
		vlSearchButtonLayout.setWidth("17%");
		vlSearchButtonLayout.addStyleName("topbarthree");
		vlSearchButtonLayout.setMargin(true);

		HorizontalLayout hlSearchComponent = new HorizontalLayout();
		hlSearchComponent.setSizeFull();
		hlSearchComponent.setSpacing(true);
		hlSearchComponent.addComponent(hlSearchLayout);
		hlSearchComponent.setComponentAlignment(hlSearchLayout, Alignment.MIDDLE_LEFT);

		hlSearchComponent.addComponent(vlSearchButtonLayout);
		hlSearchComponent.setComponentAlignment(vlSearchButtonLayout,
				Alignment.MIDDLE_RIGHT);

		final VerticalLayout vlSearch = new VerticalLayout();
		vlSearch.setSpacing(true);

		vlSearch.setSizeFull();

		vlSearch.addComponent(hlSearchComponent);
		vlSearchPanel = new VerticalLayout();

		vlSearchPanel.addComponent(PanelGenerator.createPanel(vlSearch));
		vlSearchPanel.setMargin(true);

		// add add,edit and download buttons to panel

		hlFileDownload = new HorizontalLayout();
		hlFileDownload.setSpacing(true);
		hlFileDownload.addComponent(btnDownload);
		hlFileDownload.setComponentAlignment(btnDownload,
				Alignment.MIDDLE_CENTER);
				
		HorizontalLayout hlTableTittle = new HorizontalLayout();
		hlTableTittle.addComponent(btnHome);
		hlTableTittle.addComponent(btnAdd);
		hlTableTittle.addComponent(btnEdit);
		hlTableTittle.addComponent(btnAuditRecords);
	
		vlAudit=new VerticalLayout();
		vlAudit.removeAllComponents();
		
		hlAddEdit.addStyleName("topbarthree");
		hlAddEdit.setWidth("100%");
		hlAddEdit.addComponent(hlTableTittle);
		hlAddEdit.addComponent(hlFileDownload);
		hlAddEdit.setComponentAlignment(hlFileDownload,
				Alignment.MIDDLE_RIGHT);
		hlAddEdit.setHeight("30px");

		// table declaration

		tblRCM = new Table();
		tblRCM.setSizeFull();

		tblRCM.setSelectable(true);
		tblRCM.setColumnCollapsingAllowed(true);
		//tblRCM.setPageLength(12);
		vlTablePanel = new VerticalLayout();
		tblRCM.setImmediate(true);
		tblRCM.setFooterVisible(true);

		vlTable.setSizeFull();
		vlTable.setMargin(new MarginInfo(false, true, false, true));
		vlTable.addComponent(hlAddEdit);
		vlTable.setSpacing(true);
		vlTable.addComponent(tblRCM);
	//	vlTable.addComponent(vlAudit);
		vlTable.setExpandRatio(tblRCM, 1);
		vlTablePanel.addComponent(vlTable);

		setTableProperties();
		populateAndConfig(false);

		// add search,table and add fields layout to mainpanel
		clMainLayout.setSizeFull();
		clMainLayout.addComponent(vlSearchPanel);
		clMainLayout.addComponent(vlMainPanel);
		clMainLayout.addComponent(vlTablePanel);


		hlBreadCrumbs=new HorizontalLayout();
		hlBreadCrumbs.addComponent(lblFormTitle1);
		hlBreadCrumbs.addComponent(btnBack);
		hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
		hlBreadCrumbs.addComponent(lblAddEdit);
		hlBreadCrumbs.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER);
		hlBreadCrumbs.setVisible(false);
		
		// add notification and title name to header layout
		HorizontalLayout hlNotification = new HorizontalLayout();
		hlNotification.addComponent(lblNotificationIcon);
		hlNotification.setComponentAlignment(lblNotificationIcon,
				Alignment.MIDDLE_LEFT);
		hlNotification.addComponent(lblNotification);
		hlNotification.setComponentAlignment(lblNotification,
				Alignment.MIDDLE_LEFT);
		
		hlHeaderLayout.addComponent(lblFormTittle);
		hlHeaderLayout.setComponentAlignment(lblFormTittle,
				Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlBreadCrumbs);
		hlHeaderLayout.setComponentAlignment(hlBreadCrumbs,
				Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlNotification);
		hlHeaderLayout.setComponentAlignment(hlNotification, Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlButtonLayout1);
		hlHeaderLayout.setComponentAlignment(hlButtonLayout1, Alignment.MIDDLE_RIGHT);

		excelexporter.setTableToBeExported(tblRCM);
		csvexporter.setTableToBeExported(tblRCM);
		pdfexporter.setTableToBeExported(tblRCM);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
	}
/*
 * buildNotifications()-->this function is used for poppupview for Download
 * components
 */
private void buildNotifications(ClickEvent event) {
	notifications = new Window();
	VerticalLayout l = new VerticalLayout();
	l.setMargin(true);
	l.setSpacing(true);
	notifications.setWidth("178px");
	notifications.addStyleName("notifications");
	notifications.setClosable(false);
	notifications.setResizable(false);
	notifications.setDraggable(false);
	notifications.setPositionX(event.getClientX() - event.getRelativeX());
	notifications.setPositionY(event.getClientY() - event.getRelativeY());
	notifications.setCloseShortcut(KeyCode.ESCAPE, null);
	VerticalLayout vlDownload = new VerticalLayout();
	vlDownload.addComponent(excelexporter);
	vlDownload.addComponent(csvexporter);
	vlDownload.addComponent(pdfexporter);
	vlDownload.setSpacing(false);
	notifications.setContent(vlDownload);

}

/*
 * populatedAndConfig()-->this function used to load the list to the table
 * 
 * @param boolean search 
 * if(search==true)--> it performs search operation
 * else it loads all values
 */
	public void populateAndConfig(boolean search) {

		try{
		
		tblRCM.removeAllItems();

		List<CustomerTypeDM> list  = new ArrayList<CustomerTypeDM>();

		if(search)
		{
			String status = null;
			StatusDM st = (StatusDM) cbSearchStatus.getValue();
			String customerName=null;
			if(tfSearchGname.getValue() != null )
			{
				customerName=tfSearchGname.getValue();
			}
			try {
				status = st.getCode();
				System.out.println("Status ===="+status);
			} catch (Exception e) {
				logger.info("status is empty on search");
				e.printStackTrace();
			}
			if (tfSearchGname.getValue() != null || status != null) {

				list = service.getCustomerTypeList(customerName, companyId, status);
				total = list.size();
				logger.info("Group Detail search list size-->"+list.size());
			}else{
				System.out.println("esle");
				list = service.getCustomerTypeList(null, companyId, null);
				total = list.size();
				logger.info("Group Detail search list size-->"+list.size());
			}
			
		}else{
			list = service.getCustomerTypeList(null, companyId, null);
		total = list.size();
		System.out.println("List size----------------------------------->>>"+list.size());
		}

		beans = new BeanItemContainer<CustomerTypeDM>(CustomerTypeDM.class);
		beans.addAll(list);
		tblRCM.setContainerDataSource(beans);
		tblRCM.setVisibleColumns(new Object[] { "custgroupid", "custgroupname","grpstatus", "lastupdateddt","lastupdatedby"});
		tblRCM.setColumnHeaders(new String[] {"Ref.Id", " Group Name","Status", "Last Updated Date","Last Updated By" });
		
		tblRCM.setColumnFooter("lastupdatedby", "No.of Records:" + total);
		}catch(Exception e){
			logger.error("Error on populateAndConfig()---->"+e);
			e.printStackTrace();
		}
		getExportTableDetails();
	}

	private void setTableProperties()

	{
		tblRCM.setSelectable(true);
		tblRCM.setColumnAlignment("custGroupId", Align.RIGHT);
		//tblRCM.addGeneratedColumn("lastupdateddt", new DateColumnGenerator());
		tblRCM.addItemClickListener(new ItemClickListener() {
		private static final long serialVersionUID = 1L;

			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				if (tblRCM.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
					btnAdd.setEnabled(true);

				} else {
					btnEdit.setEnabled(true);
					btnAdd.setEnabled(false);

				}
				resetFields();
			}

		});

	}

	/*
	 * saveDetails()-->this function is used for save/update the records
	 */
	private void saveDetails() {
		
		try{
		boolean valid = false;
		validateAll();
		btnSave.setComponentError(null);
		if (tblRCM.getValue() != null) {
			CustomerTypeDM updatetransobj = beans.getItem(tblRCM.getValue()).getBean();

			if (tfGname.getValue().trim().length() > 0) {
				updatetransobj.setCustgroupname(tfGname.getValue());
				tfGname.setComponentError(null);
			}

			else {
				tfGname.setComponentError(new UserError(
						"Group name can not be null"));
			}
			
			StatusDM stcode = (StatusDM) cbStatus.getValue();
			updatetransobj.setGrpstatus(stcode.getCode());
			updatetransobj.setLastupdatedby(loginUserName);
			updatetransobj.setLastupdateddt(DateUtils.getcurrentdate());
			updatetransobj.setCompanyid(companyId);

			if (tfGname.isValid() && cbStatus.isValid()) {
				service.saveDetails(updatetransobj);
				valid = true;
			}
			
			if (valid) {
				populateAndConfig(false);
				resetFields();
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblNotification.setValue(ApplicationConstants.updatedMsg);
			
				lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
				lblFormTittle.setVisible(false);
				hlBreadCrumbs.setVisible(true);
				//lblNotification.setValue("Successfully Updated");
			} 
			else {
				
				lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
				lblNotification
						.setValue("Update failed, please check the data and try again ");
			 }

		}

		else {

			CustomerTypeDM savetransobj = new CustomerTypeDM();

			if (tfGname.getValue().trim().length() > 0) {
				savetransobj.setCustgroupname(tfGname.getValue());
				tfGname.setComponentError(null);
			}

			else {
				tfGname.setComponentError(new UserError(
						"Group name can not be null"));
			}
			
			
			StatusDM stcode1 = (StatusDM) cbStatus.getValue();
			savetransobj.setGrpstatus(stcode1.getCode());
			savetransobj.setLastupdatedby(loginUserName);
			savetransobj.setLastupdateddt(DateUtils.getcurrentdate());
			savetransobj.setCompanyid(companyId);
			
			if (tfGname.isValid() && cbStatus.isValid()) {
				service.saveDetails(savetransobj);
				valid = true;
			}

			if (valid) {
				populateAndConfig(false);
				resetFields();
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblNotification.setValue(ApplicationConstants.saveMsg);
				lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
				lblFormTittle.setVisible(false);
				hlBreadCrumbs.setVisible(true);
				//lblNotification.setValue("Successfully Updated");
			} 
			else {
				
				lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
				lblNotification
						.setValue("Save failed, please check the data and try again ");
			 }
		}
		
		
		}
		catch(Exception e){
			logger.error("Error during save/update details --->"+e);
			e.printStackTrace();
		}
	}
/*
 * fn_editDetails
 * 
 * This fuction is used for  edit account type details
 * 
 */
	private void editDetails() {
		try{
		Item itselect = tblRCM.getItem(tblRCM.getValue());
		if (itselect != null) {

			CustomerTypeDM edit = beans.getItem(tblRCM.getValue()).getBean();
			 primaryId = edit.getCustgroupid().toString();
			if (edit.getCustgroupname() != null
					&& !"null".equals(edit.getCustgroupname())) {
				tfGname.setValue(edit.getCustgroupname());
			}

					
			String st = (String) edit.getGrpstatus();
			cbStatus.setValue(Common.getStatus(st));
		}
		}catch(Exception e){
			logger.error("error during edit details-->"+e);
		}
	}

	private void resetFields() {
		// TODO Auto-generated method stub

		tfGname.setValue("");
		cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
		
		btnSave.setComponentError(null);
		btnSave.setCaption("Save");
		tfGname.setComponentError(null);
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;Search");

		lblFormTittle.setVisible(true);
		hlBreadCrumbs.setVisible(false);
	}

	private void resetSearchFields() {
		cbSearchStatus.setValue(null);
		tfSearchGname.setValue("");
		lblNotification.setValue("");
		
	}

	private void validateAll() {
		// TODO Auto-generated method stub
		try {
			tfGname.validate();
		} catch (Exception e) {
			logger.error("Group name text field value empty-->"+e);
		}
		
	}
	private void getExportTableDetails()
	{
		excelexporter.setTableToBeExported(tblRCM);
		csvexporter.setTableToBeExported(tblRCM);
		pdfexporter.setTableToBeExported(tblRCM);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
		
	}
	/*
	 * 
	 * this function handles button click event
	 * 
	 * @param ClickEvent event
	 */
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (btnAdd == event.getButton()) {
			vlMainPanel.setEnabled(true);
			hlButtonLayout1.setEnabled(true);
			btnHome.setEnabled(true);
			vlMainPanel.setVisible(true);
			btnAuditRecords.setEnabled(true);
			vlSearchPanel.setVisible(false);
			resetFields();
			btnAdd.setEnabled(false);
			hlButtonLayout1.setVisible(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			tblRCM.setValue(null);
			vlAudit.setVisible(false);
			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			populateAndConfig(false);
		}

		else if (btnSave == event.getButton()) {
			saveDetails();
		} else if (btnCancel == event.getButton()) {
			vlMainPanel.setVisible(false);
			vlTablePanel.setVisible(true);
			vlSearchPanel.setVisible(true);
			populateAndConfig(false);
			resetFields();
			resetSearchFields();
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			btnHome.setEnabled(false);
			vlAudit.setVisible(false);
			hlButtonLayout1.setVisible(false);
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Search");

			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			lblAddEdit.setValue("");
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			hlFileDownload.removeAllComponents();
			hlFileDownload.addComponent(btnDownload);
			getExportTableDetails();
		}

		else if (btnEdit == event.getButton()) {
			vlMainPanel.setEnabled(true);
			hlButtonLayout1.setEnabled(true);
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			hlButtonLayout1.setVisible(true);
			resetFields();
			editDetails();
			btnSave.setCaption("Update");
			btnHome.setEnabled(true);
			btnEdit.setEnabled(false);
			btnAuditRecords.setEnabled(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Modify");
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			vlAudit.setVisible(false);
			
		} else if (btnSearch == event.getButton()) {
			populateAndConfig(true);
			hlFileDownload.removeAllComponents();
			hlFileDownload.addComponent(btnDownload);
			getExportTableDetails();
			resetFields();
			if (total == 0) {
				lblNotification.setVisible(true);
				lblNotificationIcon.setVisible(true);
				lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
				lblNotification.setValue("No Records found");
		 }
			vlAudit.setVisible(false);
		} 
		else if(btnAuditRecords==event.getButton())
		{
			vlMainPanel.setEnabled(false);
			vlSearchPanel.setEnabled(false);
			hlButtonLayout1.setEnabled(false);
			btnAuditRecords.setEnabled(false);
			btnHome.setEnabled(true);
			btnAdd.setEnabled(false);
			vlAudit.setVisible(true);
			vlAudit.removeAllComponents();
			AuditRecordsApp recordApp=new AuditRecordsApp(vlAudit,Common.M_RCM_CUSTOMER_TYPE,primaryId);
		
		hlFileDownload.removeAllComponents();
		hlFileDownload.addComponent(recordApp.btnDownload);
	    getExportTableDetails();
	    
		vlTable.removeAllComponents();
		vlTable.addComponent(hlAddEdit);
		vlTable.addComponent(vlAudit);
		lblFormTittle.setVisible(false);
		hlBreadCrumbs.setVisible(true);
		lblAddEdit.setValue("&nbsp;>&nbsp;Audit History");
		lblNotification.setVisible(false);
		lblNotificationIcon.setVisible(false);	
						
		}
		else if(btnHome==event.getButton())
		{   
			vlMainPanel.setVisible(false);
			vlSearchPanel.setVisible(true);
			vlSearchPanel.setEnabled(true);
			vlTablePanel.setVisible(true);
			hlButtonLayout1.setVisible(false);
			btnAdd.setEnabled(true);
			btnHome.setEnabled(false);
			btnAuditRecords.setEnabled(true);
			vlAudit.setVisible(false);
			tblRCM.setVisible(true);
			hlFileDownload.removeAllComponents();
			hlFileDownload.addComponent(btnDownload);
			getExportTableDetails();
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Search");
			lblFormTitle1.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			lblFormTittle.setVisible(true);
		 	vlTable.removeAllComponents();
			vlTable.addComponent(hlAddEdit);
			vlTable.addComponent(tblRCM);
			populateAndConfig(false);
			
			}

			
		else if (btnClear == event.getButton()) {
			lblNotification.setVisible(false);
			lblNotificationIcon.setVisible(false);
			resetSearchFields();
			populateAndConfig(false);
		} 
		else if (btnDownload == event.getButton()) {

			event.getButton().removeStyleName("unread");

			if (notifications != null && notifications.getUI() != null)
				notifications.close();
			else {
				buildNotifications(event);
				UI.getCurrent().addWindow(notifications);
				notifications.focus();
				((VerticalLayout) UI.getCurrent().getContent())
						.addLayoutClickListener(new LayoutClickListener() {
							@Override
							public void layoutClick(LayoutClickEvent event) {
								notifications.close();
								((VerticalLayout) UI.getCurrent().getContent())
										.removeLayoutClickListener(this);
							}
						});
			}
			
		}
		else if(btnBack==event.getButton())
		{
			vlMainPanel.setVisible(false);
			vlSearchPanel.setVisible(true);
			vlSearchPanel.setEnabled(true);
			vlTablePanel.setVisible(true);
			hlButtonLayout1.setVisible(false);
			btnAdd.setEnabled(true);
			btnHome.setEnabled(false);
			btnAuditRecords.setEnabled(true);
			vlAudit.setVisible(false);
			tblRCM.setVisible(true);
			hlFileDownload.removeAllComponents();
			hlFileDownload.addComponent(btnDownload);
			getExportTableDetails();
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Search");
			lblNotification.setVisible(false);
			lblNotificationIcon.setVisible(false);
			lblFormTitle1.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			lblFormTittle.setVisible(true);
		 	vlTable.removeAllComponents();
			vlTable.addComponent(hlAddEdit);
			vlTable.addComponent(tblRCM);
			populateAndConfig(false);
			lblNotification.setVisible(false);
			lblNotificationIcon.setVisible(false);
		
		}

	}
	
	

}
