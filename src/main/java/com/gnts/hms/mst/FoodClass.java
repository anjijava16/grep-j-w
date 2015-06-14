/**
 * File Name 		: MhmsFoodsClass.java 
 * Description 		: this class is used for add/edit Bank details. 
 * Author 			: GANGA
 * Date 			: Apr 28, 2014
 * Modification 	:
 * Modified By 		: GANGA
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Apr 28 2014        GANGA		          Intial Version
 * 
 */
package com.gnts.hms.mst;

import java.io.File;
import java.io.FileInputStream;
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
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.hms.domain.mst.FoodClassDM;
import com.gnts.hms.service.mst.FoodClassService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FoodClass implements ClickListener{
	private static final long serialVersionUID = 1L;
	private Button btnAdd;
	private Button btnEdit;
	private Button btnCancel, btnSave, btnSearch, btnClear, btnDownload;
	private Table tblFoodClass;
	private TextField tfCname, tfSearchCname;
	private ComboBox cbStatus, cbSearchStatus;
	
	private String pkValue;
	
	private VerticalLayout vlMainPanel = new VerticalLayout();
	private HorizontalLayout hlimage = new HorizontalLayout();
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
	private FoodClassService service = (FoodClassService) SpringContextHelper
			.getBean("FoodClass");
	//String className; 
	private String basepath1,basepath;
	Long fdclassid;
	String className;
	Long foodClassid;
	String foodclassstatus;
	private Long companyId;
	private int total;
	public static boolean filevalue=false;
	private BeanItemContainer<FoodClassDM> beans = null;
	String primaryId;
	private HorizontalLayout hlButtonLayout1,hlBreadCrumbs;
	private Window notifications;
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	private Logger logger = Logger.getLogger(FoodClass.class);
	public FoodClass() {
		loginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		VerticalLayout clMainLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		HorizontalLayout hlHeaderLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");
		//buildView(clMainLayout, hlHeaderLayout);
	//new UploadUI(hlimage);
		buildView(clMainLayout, hlHeaderLayout);
		//basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	  	basepath1 = basepath+"/VAADIN/themes/gerp/img/Upload.jpg";
		
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
	/*btnAdd.setDescription("Add food class Detail");
		btnSave.setDescription("Save food class detail");*/

		/**
		 * set the style for buttons
		 */
		/*btnEdit.setDescription("Edit FoodClass Detail");
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
		tfCname = new TextField("Food class name");
		tfCname.setWidth("160");
		tfCname.setRequired(true);


		cbStatus = new ComboBox("Status");
		cbStatus.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbStatus.setItemCaptionPropertyId("desc");
		cbStatus.setImmediate(true);
		cbStatus.setNullSelectionAllowed(false);
		stausBeans = new BeanItemContainer<StatusDM>(StatusDM.class);
		stausBeans.addAll(Common.listStatus);
		cbStatus.setContainerDataSource(stausBeans);
		cbStatus.setWidth("120");
		
		//tfCname.setInputPrompt("Bank name");
		//tfShortname.setInputPrompt("Short name");
		
		 FormLayout flFormLayout1 = new FormLayout();
		flFormLayout1.setSpacing(true);
		flFormLayout1.addComponent(tfCname);
				
		FormLayout flFormLayout2 = new FormLayout();
		flFormLayout2.setSpacing(true);
		flFormLayout2.addComponent(cbStatus);
		
		
		FormLayout flFormLayout3 = new FormLayout();
		 flFormLayout3.addComponent(hlimage);
		
				
		HorizontalLayout hlFormLayout = new HorizontalLayout();
		hlFormLayout.setSpacing(true);
		hlFormLayout.addComponent(flFormLayout1);
		hlFormLayout.addComponent(flFormLayout2);
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
		tfSearchCname = new TextField("Food class name");
		//tfSearchCname.setInputPrompt("Bank name");
		
		cbSearchStatus = new ComboBox("Status");
		cbSearchStatus.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		//cbSearchStatus.setInputPrompt(Common.SELECT_PROMPT);
		cbSearchStatus.setImmediate(true);
		cbSearchStatus.setNullSelectionAllowed(false);
		cbSearchStatus.setItemCaptionPropertyId("desc");
		cbSearchStatus.setWidth("120px");
		stausBeans = new BeanItemContainer<StatusDM>(StatusDM.class);
		stausBeans.addAll(Common.listStatus);
		cbSearchStatus.setContainerDataSource(stausBeans);
		cbSearchStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));

		FormLayout flSearch1 = new FormLayout();
		flSearch1.addComponent(tfSearchCname);
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
		vlSearchButtonLayout.setWidth("100");
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
		hlSearchComponent.setExpandRatio(vlSearchButtonLayout, 1);
		final VerticalLayout vlSearch = new VerticalLayout();
		vlSearch.setSpacing(true);

		
		vlSearch.setSizeFull();

		vlSearch.addComponent(hlSearchComponent);
		vlSearchPanel = new VerticalLayout();

		vlSearchPanel.addComponent(PanelGenerator.createPanel(vlSearch));
		vlSearchPanel.setMargin(true);

		//  add,edit and download buttons to panel

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

		tblFoodClass = new Table();
		tblFoodClass.setSizeFull();

		tblFoodClass.setSelectable(true);
		tblFoodClass.setColumnCollapsingAllowed(true);
		//tblFoodClass.setPageLength(12);
		vlTablePanel = new VerticalLayout();
		tblFoodClass.setImmediate(true);
		tblFoodClass.setFooterVisible(true);

		vlTable.setSizeFull();
		vlTable.setMargin(new MarginInfo(false, true, false, true));
		vlTable.addComponent(hlAddEdit);
		vlTable.setSpacing(true);
		vlTable.addComponent(tblFoodClass);
	//	vlTable.addComponent(vlAudit);
		vlTable.setExpandRatio(tblFoodClass, 1);
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
		
		excelexporter.setTableToBeExported(tblFoodClass);
		csvexporter.setTableToBeExported(tblFoodClass);
		pdfexporter.setTableToBeExported(tblFoodClass);
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
		
		tblFoodClass.removeAllItems();

		List<FoodClassDM> list  = new ArrayList<FoodClassDM>();

		if(search)
		{
			String statusArg = null;
			StatusDM st = (StatusDM) cbSearchStatus.getValue();
			String classname=null;
			if(tfSearchCname.getValue() != null )
			{
				classname=tfSearchCname.getValue();
			}
			try {
				statusArg = st.getCode();
				System.out.println("Status ===="+statusArg);
			} catch (Exception e) {
				logger.info("status is empty on search");
				e.printStackTrace();
			}
			if (tfSearchCname.getValue() != null || statusArg != null) {

				
				list = service.getFoodClassList(foodClassid, classname, companyId, statusArg);
				total = list.size();
				logger.info("Food class Detail search list size-->"+list.size());
			}else{
				System.out.println("else");
				list = service.getFoodClassList(null, null, companyId, Common.ACTIVE_DESC);
				total = list.size();
				logger.info("Food class list size-->"+list.size());
			}
			
		}else{
		list = service.getFoodClassList(null, null, null, foodclassstatus);
		total = list.size();
		}

		beans = new BeanItemContainer<FoodClassDM>(FoodClassDM.class);
		beans.addAll(list);

		tblFoodClass.setContainerDataSource(beans);

		tblFoodClass.setVisibleColumns(new Object[] {"fdclassid", "classname",
				 "foodclassstatus", "lastupdateddt",
				"lastupdatedby"});

		tblFoodClass.setColumnHeaders(new String[] {"Ref.Id", "Food Class name","Status", "Last Updated Date",
				"Last Updated By" });

		tblFoodClass.setColumnFooter("lastupdatedby", "No.of Records:" + total);
		}catch(Exception e){
			logger.error("Error on populateAndConfig()---->"+e);
			e.printStackTrace();
		}
        getExportTableDetails();
	}

	
	private void setTableProperties()

	{

		tblFoodClass.setSelectable(true);
		tblFoodClass.setColumnAlignment("fdclassid", Align.RIGHT);
		tblFoodClass.addGeneratedColumn("lastupdateddt", new DateColumnGenerator());
		tblFoodClass.addItemClickListener(new ItemClickListener() {
		private static final long serialVersionUID = 1L;

			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				if (tblFoodClass.isSelected(event.getItemId())) {
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
		if (tblFoodClass.getValue() != null) {
			FoodClassDM updatetransobj = beans.getItem(tblFoodClass.getValue()).getBean();

			if (tfCname.getValue().trim().length() > 0) {
				updatetransobj.setClassname(tfCname.getValue());
				tfCname.setComponentError(null);
			}

			else {
				tfCname.setComponentError(new UserError(
						"Food Class name can not be null"));
			}
			
			
			StatusDM stcode = (StatusDM) cbStatus.getValue();
			updatetransobj.setFoodclassstatus(stcode.getCode());
			updatetransobj.setLastupdatedby(loginUserName);
			updatetransobj.setLastupdateddt(DateUtils.getcurrentdate());
			updatetransobj.setCompanyid(companyId);
			

			if(1==1/*filevalue*/)
			{
	try{
			vlSearchPanel.setEnabled(true);	
			File file = new File(basepath1);
			FileInputStream fin = new FileInputStream(file);
			
			System.out.println("File stream...>"+fin);
			
		    byte fileContent[] = new byte[(int)file.length()];		
		    fin.read(fileContent);
		    fin.close();		 
		    updatetransobj.setClassicon(fileContent);

			
		}catch(Exception e)
		{			e.printStackTrace();
			}
			}
		
			if (tfCname.isValid() && cbStatus.isValid() ) {
				service.saveorUpdateFoodClassDetails(updatetransobj);
				valid = true;
			}
			
			if (valid) {
				populateAndConfig(false);
				resetFields();
				lblNotificationIcon.setIcon(new ThemeResource("img/success_small.png"));
				lblNotification.setValue(ApplicationConstants.updatedMsg);
			
				lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
				lblFormTittle.setVisible(true);
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

			FoodClassDM savetransobj = new FoodClassDM();

			if (tfCname.getValue().trim().length() > 0) {
				savetransobj.setClassname(tfCname.getValue());
				tfCname.setComponentError(null);
			}

			else {
				tfCname.setComponentError(new UserError(
						"Class name can not be null"));
			}
			
			
			StatusDM stcode1 = (StatusDM) cbStatus.getValue();
			savetransobj.setFoodclassstatus(stcode1.getCode());
			savetransobj.setLastupdatedby(loginUserName);
			savetransobj.setLastupdateddt(DateUtils.getcurrentdate());
			savetransobj.setCompanyid(companyId);
			
			if(1==1/*filevalue*/)
			{
		try{						
				File file = new File(basepath1);
				FileInputStream fin = new FileInputStream(file);
				System.out.println("File stream...>"+fin);
			    byte fileContent[] = new byte[(int)file.length()];
			    fin.read(fileContent);
			    fin.close();
			    savetransobj.setClassicon(fileContent);
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			}
		
						
			if (tfCname.isValid() && cbStatus.isValid()) {
				service.saveorUpdateFoodClassDetails(savetransobj);
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
				lblNotification.setValue("Successfully Updated");
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
 * This fuction is used for  edit food type details
 * 
 */
	private void editDetails() {
		try{
		Item itselect = tblFoodClass.getItem(tblFoodClass.getValue());
		if (itselect != null) {

			FoodClassDM edit = beans.getItem(tblFoodClass.getValue()).getBean();
			 primaryId = edit.getFdclassid().toString();
			if (edit.getClassname() != null
					&& !"null".equals(edit.getClassname())) {
				tfCname.setValue(edit.getClassname());
			}			
			
			String st = (String) edit.getFoodclassstatus();
			cbStatus.setValue(Common.getStatus(st));
			
			
			if(edit.getClassicon()!=null){
				byte[] myimage=(byte[]) edit.getClassicon();
				
				
				UploadUI test=new UploadUI(hlimage);
				test.dispayImage(myimage);
				}else{
					
				try{
					new UploadUI(hlimage);
					}catch(Exception e){
						e.printStackTrace();
						
					}
				}
	
				pkValue=edit.getClassicon().toString();

					
			
			
			
				}
				
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error("error during edit details-->"+e);
		}

	}
	
	
	

	

	private void resetFields() {
		// TODO Auto-generated method stub

		tfCname.setValue("");
		cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
		
		btnSave.setComponentError(null);
		btnSave.setCaption("Save");
		tfCname.setComponentError(null);
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;Search");
		lblFormTittle.setVisible(true);
		/*lblNotificationIcon.setIcon(null);
		lblNotification.setValue("");*/
	//	lblNotification.setVisible(false);
		lblNotificationIcon.setIcon(null);
		lblNotification.setValue("");
		hlBreadCrumbs.setVisible(false);
		try{
			new UploadUI(hlimage);
			}catch(Exception e){
				e.printStackTrace();
			}
			filevalue = false;
			
			pkValue=null;
	}
	

	private void resetSearchFields() {
		cbSearchStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
		tfSearchCname.setValue("");
		lblNotificationIcon.setIcon(null);
		lblNotification.setValue("");
		lblNotification.setVisible(false);
		lblNotification.setValue("");
	}

	private void validateAll() {
		// TODO Auto-generated method stub
		try {
			tfCname.validate();
		} catch (Exception e) {
			logger.error("Account type name text field value empty-->"+e);
		}
		
	}
	private void getExportTableDetails()
	{
		excelexporter.setTableToBeExported(tblFoodClass);
		csvexporter.setTableToBeExported(tblFoodClass);
		pdfexporter.setTableToBeExported(tblFoodClass);
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
			try{
				new UploadUI(hlimage);
			}catch(Exception e){
				
			}
			btnAdd.setEnabled(false);
			hlButtonLayout1.setVisible(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(true);
			tblFoodClass.setValue(null);
			vlAudit.setVisible(false);
			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			populateAndConfig(false);
		}

		else if (btnSave == event.getButton()) {
			try
			{
			saveDetails();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
			}
			
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
			btnHome.setEnabled(true);
			vlMainPanel.setEnabled(true);
			//hlButtonLayout1.setEnabled(false);
			vlMainPanel.setVisible(true);
			new UploadUI(hlimage);
			vlSearchPanel.setVisible(false);
			hlButtonLayout1.setEnabled(true);
            hlButtonLayout1.setVisible(true);
			resetFields();
			editDetails();
			btnSave.setCaption("Update");
			
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
			if (total ==0) {
				lblNotification.setVisible(true);
				lblNotificationIcon.setVisible(true);
				lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
				lblNotificationIcon.setWidth("30");
				lblNotification.setValue("No Records found");
	
		}
			 else
				{

					lblNotificationIcon.setIcon(null);
					lblNotification.setCaption("");

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
			//AuditRecordsApp recordApp=new AuditRecordsApp(vlAudit,Common.M_HMS_FOOD_CLASS ,pkValue);
			
			hlFileDownload.removeAllComponents();
			//hlFileDownload.addComponent(recordApp.btnDownload);
		    getExportTableDetails();
	    
		    vlTable.removeAllComponents();
			vlTable.addComponent(hlAddEdit);
			vlTable.addComponent(vlAudit);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Audit History");
			
			
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
			tblFoodClass.setVisible(true);
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
			vlTable.addComponent(tblFoodClass);
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
			tblFoodClass.setVisible(true);
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
			vlTable.addComponent(tblFoodClass);
			populateAndConfig(false);
		}

	}
	
	

}