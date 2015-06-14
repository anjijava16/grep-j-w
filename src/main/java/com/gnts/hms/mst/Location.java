package com.gnts.hms.mst;
/**
 * File Name 		: MMHMSLocations.java 
 * Description 		: this class is used for add/edit Location details. 
 * Author 			: M HASSAIN
 * Date 			: Mar 31, 2014
 * Modification 	:
 * Modified By 		:  M HASSAIN
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 31 2014         M HASSAIN		          Intial Version
 *  * 
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.base.domain.mst.CityDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.CityService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.StateService;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.Common;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hms.domain.mst.LocationDM;
import com.gnts.hms.service.mst.LocationService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
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

public class Location implements ClickListener {
	private static final long serialVersionUID = 1L;


	private static final String CountryId = null;
	

	private Button btnAdd;
	private Button btnEdit;
	private Button btnCancel, btnSave, btnSearch, btnClear, btnDownload;
	private Table tblLocation;
	private StateDM selectedBaseState;
	private CountryDM selectedCountry;
	private CityDM selectedBaseCity;
	
	private CountryService countryBean = (CountryService) SpringContextHelper.getBean("Country");
	private CityService citybean = (CityService) SpringContextHelper.getBean("city");
	private StateService statebean=(StateService)SpringContextHelper.getBean("mstate");
	private TextField tflocationname,tflocationId;
	private TextField tfSearchLocationname=new TextField("LocationName");
	private ComboBox cbStatus,cbSearchStatus,cbCountryid,cbStateid,cbCityid;
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
	private BeanItemContainer<LocationDM> beans = null;
	private String loginUserName, screenName;
	private Label lblFormTittle,lblFormTitle1,lblAddEdit;
	private Label lblNotification, lblNotificationIcon;
	private LocationService service = (LocationService) SpringContextHelper.getBean("location");

	String locationName;
	Long locationId;
	String locationStatus;
	private Long companyId;
	private int total;
	String primaryId;
	private HorizontalLayout hlButtonLayout1,hlBreadCrumbs;
	private Window notifications;
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	private Logger logger = Logger.getLogger(Location.class);
	
private String countryId;
	
	public Location() {
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
		/*btnAdd.setDescription("Add Location Detail");
		btnSave.setDescription("Save Location detail");*/

		/**
		 * set the style for buttons
		 */
		/*btnEdit.setDescription("Edit Location Detail");
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
		//tfSearchLocationname = new TextField("Location name");
		tfSearchLocationname.setWidth("160");
		tfSearchLocationname.setRequired(false);
		
		tflocationname=new TextField("locationname");
	
		tflocationId=new TextField("LocationId");

		
		
		cbStatus = new ComboBox("LocationStatus");
		cbStatus.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbStatus.setItemCaptionPropertyId("desc");
		cbStatus.setImmediate(true);
		cbStatus.setNullSelectionAllowed(false);
		stausBeans = new BeanItemContainer<StatusDM>(StatusDM.class);
		stausBeans.addAll(Common.listStatus);
		cbStatus.setContainerDataSource(stausBeans);
		cbStatus.setWidth("170");
		
		//tfSearchLocationname.setInputPrompt("Location name");
		//tfShortname.setInputPrompt("Short name");
		
		cbCountryid=new ComboBox("country");
		cbCountryid.setRequired(true);
		cbStateid=new ComboBox("state");
		cbStateid.setRequired(true);
		cbCityid=new ComboBox("city");
		cbCityid.setRequired(true);

		FormLayout flFormLayout1 = new FormLayout();
		flFormLayout1.setSpacing(true);
		
	//	flFormLayout1.addComponent(tflocationId);
		flFormLayout1.addComponent(tflocationname);
		flFormLayout1.setSpacing(true);
		FormLayout flFormLayout2 = new FormLayout();
		flFormLayout2.setSpacing(true);
	//	flFormLayout2.addComponent(tfSearchLocationname);
		flFormLayout2.addComponent(cbCountryid);
		flFormLayout2.addComponent(cbStateid);
		flFormLayout2.addComponent(cbCityid);

		FormLayout flFormLayout3 = new FormLayout();
		flFormLayout3.setSpacing(true);
		flFormLayout3.addComponent(cbStatus);
		
				
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
		hlButtonLayout1.setVisible(true);

		// add search fields to panel
		
		//tfSearchLname.setInputPrompt("Location name");
		

		cbSearchStatus = new ComboBox("Status");
		cbSearchStatus.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbSearchStatus.setInputPrompt(Common.SELECT_PROMPT);
		cbSearchStatus.setImmediate(true);
		cbSearchStatus.setNullSelectionAllowed(true);
		cbSearchStatus.setItemCaptionPropertyId("desc");
		cbSearchStatus.setWidth("120px");
		stausBeans = new BeanItemContainer<StatusDM>(StatusDM.class);
		stausBeans.addAll(Common.listStatus);
		cbSearchStatus.setContainerDataSource(stausBeans);



		FormLayout flSearch1 = new FormLayout();
		flSearch1.addComponent(tfSearchLocationname);
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

		tblLocation = new Table();
		tblLocation.setSizeFull();

		tblLocation.setSelectable(true);
		tblLocation.setColumnCollapsingAllowed(true);
	//	tblLocation.setPageLength(12);
		vlTablePanel = new VerticalLayout();
		tblLocation.setImmediate(true);
		tblLocation.setFooterVisible(true);

	
		vlTable.setSizeFull();
		vlTable.setMargin(new MarginInfo(false, true, false, true));
		vlTable.addComponent(hlAddEdit);
		vlTable.setSpacing(true);
		vlTable.addComponent(tblLocation);
	//	vlTable.addComponent(vlAudit);
		vlTable.setExpandRatio(tblLocation, 1);
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
		hlHeaderLayout.setComponentAlignment(hlNotification, Alignment.MIDDLE_RIGHT);
		hlHeaderLayout.addComponent(hlButtonLayout1);
		hlHeaderLayout.setComponentAlignment(hlButtonLayout1, Alignment.MIDDLE_RIGHT);

		excelexporter.setTableToBeExported(tblLocation);
		csvexporter.setTableToBeExported(tblLocation);
		pdfexporter.setTableToBeExported(tblLocation);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
		
		loadForeignKeyDetails();
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
		
		tblLocation.removeAllItems();

		List<LocationDM> list  = new ArrayList<LocationDM>();


		if(search)
		{
			String statusArg = null;
			StatusDM st = (StatusDM) cbSearchStatus.getValue();
			//String locationName=null;
			if(tfSearchLocationname.getValue() != null )
			{
				locationName=tfSearchLocationname.getValue();
			}
			try {
				statusArg = st.getCode();
				System.out.println("Status ===="+statusArg);
			} catch (Exception e) {
				logger.info("status is empty on search");
				e.printStackTrace();
			}
			if (tfSearchLocationname.getValue() != null || statusArg != null) {

				list = service.getLocation(locationId, companyId, locationStatus);
				total = list.size();
				logger.info("Bank Detail search list size-->"+list.size());
			}else{
				System.out.println("else");
				list = service.getLocation(null, companyId, null);
				total = list.size();
				logger.info("Account type list size-->"+list.size());
			}
			}
		else
		{
		list = service.getLocation(null, companyId, null);
		total = list.size();
		}


		beans = new BeanItemContainer<LocationDM>(LocationDM.class);
		beans.addAll(list);

		tblLocation.setContainerDataSource(beans);

		tblLocation.setVisibleColumns(new Object[] {"locationId","locationName",
				 "locationStatus","lastUpdatedDt",
				"lastUpdatedBy", });

		tblLocation.setColumnHeaders(new String[] {"Ref.Id", "Location Name",
				"Status", "Last Updated Date",
				"Last Updated By" });

		tblLocation.setColumnFooter("lastupdatedby", "No.of Records:" + total);
		}catch(Exception e){
			logger.error("Error on populateAndConfig()---->"+e);
			e.printStackTrace();
		}
getExportTableDetails();
	}

	
	private void setTableProperties()

	{

		tblLocation.setSelectable(true);
		tblLocation.setColumnAlignment("locationId", Align.RIGHT);
		tblLocation.addGeneratedColumn("lastupdateddt", new DateColumnGenerator());
		tblLocation.addItemClickListener(new ItemClickListener() {
		private static final long serialVersionUID = 1L;

			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				if (tblLocation.isSelected(event.getItemId())) {
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
	private void saveDetailss() {
		
		try{
		boolean valid = true;
	//validateAll();
		//resetFields();
		btnSave.setComponentError(null);
		if (tblLocation.getValue() != null) {
			LocationDM updatetransobj = beans.getItem(tblLocation.getValue()).getBean();

			if (tflocationname.getValue().trim().length() > 0) {
				updatetransobj.setLocationName(tflocationname.getValue());
				tflocationname.setComponentError(null);
			}

			else {
				tflocationname.setComponentError(new UserError(
						"Location name can not be null"));
			}
			//updatetransobj.setLocationId(locationId);
			updatetransobj.setLocationName(tflocationname.getValue());
			//updatetransobj.setCountryId(cbCountryid);
			updatetransobj.setCityId(selectedBaseCity);
			updatetransobj.setCountryId(selectedCountry);
			updatetransobj.setStateId(selectedBaseState);
			StatusDM stcode = (StatusDM) cbStatus.getValue();
		
			updatetransobj.setLocationStatus(stcode.getCode());
			updatetransobj.setLastUpdatedBy(loginUserName);
			updatetransobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			updatetransobj.setCompanyId(companyId);

			if (tflocationname.isValid() && cbStatus.isValid()) 
			{
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

			LocationDM savetransobj = new LocationDM();

			if (tflocationname.getValue().trim().length() > 0) {
				savetransobj.setLocationName(tflocationname.getValue());
				tflocationname.setComponentError(null);
			}

			else {
				tflocationname.setComponentError(new UserError(
						"Location name can not be null"));
			}
			savetransobj.setLocationId(locationId);
			//updatetransobj.setCountryId(countryId);(cbCountry.getValue());
			savetransobj.setCityId(selectedBaseCity);
			savetransobj.setCountryId(selectedCountry);
			savetransobj.setStateId(selectedBaseState);
			//savetransobj.setLocationName(cbCountry.getValue());
			StatusDM stcode1 = (StatusDM) cbStatus.getValue();
			savetransobj.setLocationStatus(stcode1.getCode());
			savetransobj.setLastUpdatedBy(loginUserName);
			savetransobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			savetransobj.setCompanyId(companyId);
			
			if (tflocationname.isValid() && cbStatus.isValid()) {
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
			Item itselect = tblLocation.getItem(tblLocation.getValue());
			if (itselect != null) {
				
				LocationDM edit = beans.getItem(tblLocation.getValue()).getBean();
				
				 primaryId = edit.getLocationId().toString();
			if(edit.getCountryId()!=null)
				{
					CountryDM mfb = edit.getCountryId();
					tflocationname.setValue(edit.getLocationName());
					//Long mfb = edit.getBankid();
					//MHMSLocation editaccount = edit.getBankid().toString();
					Collection<?> mfbid = cbCountryid.getItemIds();
					for (Iterator<?> iterator = mfbid.iterator(); iterator.hasNext();) {
						Object itemId = (Object) iterator.next();
						BeanItem<?> item = (BeanItem<?>) cbCountryid.getItem(itemId);
						// Get the actual bean and use the data
						CountryDM st = (CountryDM) item.getBean();
						System.out.println("Bank id--------->"+itemId);
						if (mfb != null &&mfb.getCountryID().equals(st.getCountryID()))
						{
							System.out.println("Bank id--------->"+itemId);

							cbCountryid.setValue(itemId);
							

						}				}			}

			
		/*	MBaseCountry editcountry=editClients.getCountryId();
 			Collection<?> collcountry=cbCountry.getItemIds();
 			for(Iterator iterator=collcountry.iterator(); iterator.hasNext(); )
 			{
 				Object itemId4=(Object)iterator.next();
 				BeanItem<?> item=(BeanItem<?>)cbCountry.getItem(itemId4);
 				MBaseCountry countryBean=(MBaseCountry)item.getBean();
 				if(editcountry!=null && editcountry.getCountryID().equals(countryBean.getCountryID()))
 				{
 					cbCountry.setValue(itemId4);
 					break;
 				}
 				else{
 					cbCountry.setValue(null);
 				}
 			}*/
			if(edit.getCityId()!=null)
			{
				CityDM mfb = edit.getCityId();
				//Long mfb = edit.getBankid();
				//MHMSLocation editaccount = edit.getBankid().toString();
				Collection<?> mfbid = cbCityid.getItemIds();
				for (Iterator<?> iterator = mfbid.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbCityid.getItem(itemId);
					// Get the actual bean and use the data
					CityDM st = (CityDM) item.getBean();
					System.out.println("Bank id--------->"+itemId);
					if (mfb != null &&mfb.getCityid().equals(st.getCityid()))
					{
						System.out.println("Bank id--------->"+itemId);

						cbCityid.setValue(itemId);
						

					}				}			}

				
		/*	if (edit.getLocationName() != null
						&& !"null".equals(edit.getLocationName())) {
				tflocationname.setValue(edit.getLocationName());
				}
			*/
			
			if(edit.getLocationId()!=null){
				tflocationId.setValue(edit.getLocationId().toString());
			}
		
			if(edit.getStateId()!=null)
			{
				StateDM uom = edit.getStateId();

				Collection<?> uomid = cbStateid.getItemIds();
				for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();

					BeanItem<?> item = (BeanItem<?>) cbStateid.getItem(itemId);
					// Get the actual bean and use the data
					StateDM st = (StateDM) item.getBean();
					if (uom != null && uom.getStateId().equals(st.getStateId()))
					{

						cbStateid.setValue(itemId);

					}

				}
				
			}
		

		}
		
		}
		catch(Exception e){
			logger.error("error during edit details-->"+e);
		}
	}

	private void resetFields() {
		// TODO Auto-generated method stub
		tflocationId.setValue("");
		tfSearchLocationname.setValue("");
		tflocationname.setValue("");
		cbCountryid.setValue(null);
		cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
		cbCountryid.setComponentError(null);
		cbCityid.setValue(null);
		cbCityid.setComponentError(null);
		cbStateid.setValue(null);
		cbStateid.setComponentError(null);
		btnSave.setComponentError(null);
		btnSave.setCaption("Save");
		tfSearchLocationname.setComponentError(null);
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;Search");
		lblNotificationIcon.setIcon(null);
		lblNotification.setValue("");
		lblFormTittle.setVisible(true);
		hlBreadCrumbs.setVisible(false);
	}

	private void resetSearchFields() {
		cbSearchStatus.setValue(null);
		tfSearchLocationname.setValue("");
		lblNotification.setValue("");
		
	}
	
	void loadForeignKeyDetails() {


		tfSearchLocationname.setRequired(true);
		//tfIFSCcode.setRequiredError("Please Enter IFSC code");
		//tfIFSCcode.setInputPrompt("ENTER IFSCCODE");
		tfSearchLocationname.addValidator(new StringLengthValidator(
				"Location name must be 2 to 30 characters", 2, 30,
				true));
		tfSearchLocationname.setMaxLength(30);
		
		
		
		
			
		// add search fields to panel
		//tfSearchIFSCcode.setInputPrompt("IFSC Code");
		//tfSearchMICRcode.setInputPrompt("MICR Code");
		
		//cbbankid.setInputPrompt(Common.SELECT_PROMPT);
		
		
		//cbcountryid.setInputPrompt(Common.SELECT_PROMPT);
		cbCountryid.setImmediate(true);
		cbCountryid.setNullSelectionAllowed(true);
		cbCountryid.setItemCaptionPropertyId("countryName");
		loadCountryList();
		cbCountryid.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = 1L;

					public void valueChange(ValueChangeEvent event) {
						Object itemid = event.getProperty().getValue();
						if (itemid != null) {
							BeanItem<?> item = (BeanItem<?>) cbCountryid.getItem(itemid);
							selectedCountry = (CountryDM) item.getBean();
						}
					}
				});
		
	//	cbcityid.setInputPrompt(Common.SELECT_PROMPT);
		cbCityid.setImmediate(true);
		cbCityid.setNullSelectionAllowed(true);
		cbCityid.setItemCaptionPropertyId("cityname");
		cbCityid.setValue(null);
		loadCityList();
		cbCityid.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = 1L;

					public void valueChange(ValueChangeEvent event) {
						Object itemid = event.getProperty().getValue();
						if (itemid != null) {
							BeanItem<?> item = (BeanItem<?>) cbCityid.getItem(itemid);
							selectedBaseCity = (CityDM) item.getBean();
						}
					}
				});
		
		//cbstateid.setInputPrompt(Common.SELECT_PROMPT);
		cbStateid.setImmediate(true);
		cbStateid.setNullSelectionAllowed(true);
		cbStateid.setItemCaptionPropertyId("stateName");
		loadStateList();
		cbStateid.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = 1L;

					public void valueChange(ValueChangeEvent event) {
						Object itemid = event.getProperty().getValue();
						if (itemid != null) {
							BeanItem<?> item = (BeanItem<?>) cbStateid.getItem(itemid);
							selectedBaseState = (StateDM) item.getBean();
						}
					}
				});
		
				
		
	}
	
	private void loadCityList() {
		try {
			List<CityDM> getCityList = citybean.getCityList(null, null,  Common.ACTIVE_DESC, null);		
			BeanItemContainer<CityDM > beancity = new BeanItemContainer<CityDM>(CityDM.class);
			beancity.addAll(getCityList);
			cbCityid.setContainerDataSource(beancity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("Loading null values in loadCityList() functions----->>>>>"+ e);
		}
	}
	
	private void loadCountryList(){
	
	}
	
/*	MBaseCountry editcountry=editClients.getCountryId();
		Collection<?> collcountry=cbCountry.getItemIds();
		for(Iterator iterator=collcountry.iterator(); iterator.hasNext(); )
		{
			Object itemId4=(Object)iterator.next();
			BeanItem<?> item=(BeanItem<?>)cbCountry.getItem(itemId4);
			MBaseCountry countryBean=(MBaseCountry)item.getBean();
			if(editcountry!=null && editcountry.getCountryID().equals(countryBean.getCountryID()))
			{
				cbCountry.setValue(itemId4);
				break;
			}
			else{
				cbCountry.setValue(null);
			}
		}
	*/
	private void loadStateList() {
		
	}
	
	
	private void validateAll() {
		// TODO Auto-generated method stub
		try {
			tfSearchLocationname.validate();
		} catch (Exception e) {
			logger.error("Account type name text field value empty-->"+e);
		}
		
	}
	private void getExportTableDetails()
	{
		excelexporter.setTableToBeExported(tblLocation);
		csvexporter.setTableToBeExported(tblLocation);
		pdfexporter.setTableToBeExported(tblLocation);
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
			vlMainPanel.setVisible(true);
			btnAuditRecords.setEnabled(true);
			vlSearchPanel.setVisible(false);
			resetFields();
			btnAdd.setEnabled(false);
			hlButtonLayout1.setVisible(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			tblLocation.setValue(null);
			hlButtonLayout1.setEnabled(true);
			populateAndConfig(false);
		}

		else if (btnSave == event.getButton()) {
			
			try {
				saveDetailss();
				
			} catch (Exception e) {
				
				logger.info("Error on saveDatails() function--->"+ e);
			}

		}
		 else if (btnCancel == event.getButton()) {
			vlMainPanel.setVisible(false);
			vlTablePanel.setVisible(true);
			vlSearchPanel.setVisible(true);
			populateAndConfig(false);
			resetFields();
			resetSearchFields();
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			

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
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			hlButtonLayout1.setVisible(true);
			resetFields();
			editDetails();
			btnSave.setCaption("Update");
			btnEdit.setEnabled(false);
			btnAuditRecords.setEnabled(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Modify");
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			
		}
		else if (btnSearch == event.getButton()) {
			populateAndConfig(true);
			hlFileDownload.removeAllComponents();
			hlFileDownload.addComponent(btnDownload);
			getExportTableDetails();
			if (total == 0) {
				//lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
				lblNotification.setCaption("No Records Found");
				lblNotification.setValue("No Records found");
				lblNotificationIcon.setVisible(true);
			}
			vlAudit.setVisible(true);

		} 
		else if(btnAuditRecords==event.getButton())
		{
			btnAuditRecords.setEnabled(true);
			btnHome.setEnabled(true);
			btnAdd.setEnabled(false);
			vlAudit.setVisible(true);
			vlAudit.removeAllComponents();
			
		
		AuditRecordsApp recordApp=new AuditRecordsApp(vlAudit,Common.M_HMS_LOCATION,primaryId);
		
		hlFileDownload.removeAllComponents();
		hlFileDownload.addComponent(recordApp.btnDownload);
	    getExportTableDetails();
	    
		vlTable.removeAllComponents();
		vlTable.addComponent(hlAddEdit);
		vlTable.addComponent(vlAudit);
		lblFormTittle.setVisible(false);
		hlBreadCrumbs.setVisible(true);
		lblAddEdit.setValue("&nbsp;>&nbsp;Audit History");
			
			
		}
		else if(btnHome==event.getButton())
		{   btnAdd.setEnabled(true);
			btnHome.setEnabled(false);
			btnAuditRecords.setEnabled(true);
			vlAudit.setVisible(false);
			tblLocation.setVisible(true);
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
			vlTable.addComponent(tblLocation);
			populateAndConfig(false);
			
			}

			
		else if (btnClear == event.getButton()) {
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
			btnHome.setEnabled(false);
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			btnAuditRecords.setEnabled(true);
			resetFields();
			vlMainPanel.setVisible(false);
			vlSearchPanel.setVisible(true);
			vlTablePanel.setVisible(true);
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);

			hlButtonLayout1.setVisible(false);

			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Search");

			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			lblAddEdit.setValue("");
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
		}

	}
	
	

}
	

