/**
 * File Name 		: BranchInfoDMApp.java 
 * Description 		: this class is used for add/edit Account details. 
 * Author 			: SOUNDAR C 
 * Date 			: Mar 05, 2014
 * Modification 	:
 * Modified By 		: SOUNDAR C 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 07 2014        SOUNDAR C		          Intial Version
 * 
 */
package com.gnts.hms.mst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.Common;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hms.domain.mst.BranchInfoDM;
import com.gnts.hms.service.mst.BranchInfoService;
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
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;

public class BranchInfo implements ClickListener {
	private static final long serialVersionUID = 1L;

	/*
	 * Service Declaration
	 */
	private BranchInfoService service = (BranchInfoService) SpringContextHelper
			.getBean("branchInfo");
	
	private BranchService branchbean=(BranchService)SpringContextHelper.getBean("mbranch");
	
	
	private Button btnAdd;
	private Button btnEdit;
	private Button btnCancel, btnSave, btnSearch, btnClear, btnDownload;
	private Table tblbranchinfo;
	
	private TextField tfSearchInfoId = new TextField("IFSC Code");
	
	private ComboBox cbStatus,cbSearchStatus;
	
	//private ComboBox cbbrnchid  = new ComboBox("BANK_BRANCH_ID");
	private ComboBox cbBranchid  = new ComboBox("Branch Id");
	 
	private TextField tfInfoId =new TextField("Information Id");
	private TextField tfInfoLabel=new TextField("Information Label");
	private TextField tfInfoDesc = new TextField("Information Description");
		
	private VerticalLayout vlMainPanel = new VerticalLayout();
	private VerticalLayout vlSearchPanel = new VerticalLayout();
	private VerticalLayout vlTablePanel = new VerticalLayout();
	private Button btnAuditRecords;
	private Button btnHome, btnBack;
	private VerticalLayout vlAudit;
	private VerticalLayout vlTable = new VerticalLayout();
	private HorizontalLayout hlAddEdit = new HorizontalLayout();
	private HorizontalLayout hlFileDownload;

	private BeanItemContainer<StatusDM> stausBeans = null;
	private String loginUserName, screenName;
	private Label lblFormTittle, lblFormTitle1, lblAddEdit;
	private Label lblNotification, lblNotificationIcon;

	private int total;
	private BeanItemContainer<BranchInfoDM> beans = null;
	private String strWidth = "160px";
	
	String primaryId;
	private Long infoId;
	String branchstatus;
	// for foreignkey values
	@SuppressWarnings("unused")
	private BranchDM selectedBranch;
	private Long companyId;
	Long branchid;
	private HorizontalLayout hlButtonLayout1, hlBreadCrumbs;
	private Window notifications;
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();

	private static Logger logger = Logger
			.getLogger(BranchInfo.class);

	public BranchInfo() {
		loginUserName = UI.getCurrent().getSession()
				.getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		VerticalLayout clMainLayout = (VerticalLayout) UI.getCurrent().getSession()
				.getAttribute("clLayout");
		HorizontalLayout hlHeaderLayout = (HorizontalLayout) UI.getCurrent()
				.getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlHeaderLayout);
	}

	/*
	 * buildMainview()-->for screen UI design
	 * 
	 * @param clArgumentLayout hlHeaderLayout
	 */
	private void buildView(VerticalLayout clMainLayout,
			HorizontalLayout hlHeaderLayout) {
		hlHeaderLayout.removeAllComponents();

		lblNotification = new Label();
		lblNotification.setContentMode(ContentMode.HTML);
		lblNotificationIcon = new Label();

		/**
		 * Initialise the buttons
		 */
		btnAdd = new Button("Add", this);
		btnCancel = new Button("Cancel", this);
		btnSave = new Button("Save", this);
		btnEdit = new Button("Edit", this);
		btnSearch = new Button("Search", this);
		btnDownload = new Button("Download", this);
		btnBack = new Button("Search", this);
		btnAuditRecords = new Button("Audit History", this);

		setComponentStyle();
		loadForeignKeyDetails();
		
		//tfInfoDesc.setInputPrompt("Enter Address1");
		//tfAddress2.setInputPrompt("Enter Address2");
		//tfemailid.setInputPrompt("Enter Email ID");
		//tfemailid.addValidator(new EmailValidator("Enter mail id"));
		//tfPhoneno.setInputPrompt("Enter phone number");
		//tfPhoneno.addValidator(new PhoneNumberValidation("Enter currect phone no."));
		
		//btnSearch.setDescription("Search Records");
		btnClear = new Button("Reset", this);
		//btnClear.setDescription("Reset Search");
		btnEdit.setEnabled(false);
		//btnAdd.setDescription("Add bankbranch detail");
		//btnSave.setDescription("Save bankbranch detail");
		
		//btnEdit.setDescription("Edit Bank branch");
		//btnCancel.setDescription("Return to Search");
		//btnDownload.setDescription("Download");
		btnAdd.addStyleName("add");
		btnCancel.addStyleName("cancelbt");
		btnDownload.addStyleName("downloadbt");
		btnSave.addStyleName("savebt");
		btnEdit.addStyleName("editbt");
		btnAuditRecords=new Button("Audit History",this);
		btnAuditRecords.setStyleName("hostorybtn");
		btnBack.setStyleName("link");
		
		btnSearch.addStyleName("searchbt");
		btnClear.addStyleName("resetbt");

		btnHome = new Button("Home", this);
		btnHome.setStyleName("homebtn");
		btnHome.setEnabled(false);
	
		//btnDownload.setDescription("Download");
						
		cbStatus = new ComboBox("Status");
		cbStatus.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbStatus.setItemCaptionPropertyId("desc");
		cbStatus.setImmediate(true);
		cbStatus.setNullSelectionAllowed(false);
		stausBeans = new BeanItemContainer<StatusDM>(StatusDM.class);
		stausBeans.addAll(Common.listStatus);
		cbStatus.setContainerDataSource(stausBeans);
		cbStatus.setWidth("120");
		
						
		/*cbSearchStatus = new ComboBox("Status");
		cbSearchStatus.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbSearchStatus.setInputPrompt(Common.SELECT_PROMPT);
		cbSearchStatus.setImmediate(true);
		cbSearchStatus.setNullSelectionAllowed(false);
		cbSearchStatus.setItemCaptionPropertyId("desc");
		cbSearchStatus.setWidth("120px");
		stausBeans = new BeanItemContainer<Status>(Status.class);
		stausBeans.addAll(Common.listStatus);
		cbSearchStatus.setContainerDataSource(stausBeans);*/

		// add fields to panel

			
		FormLayout flFormLayout1 = new FormLayout();
		flFormLayout1.setSpacing(true);
		//flFormLayout1.addComponent(cbbrnchid);
		
		flFormLayout1.addComponent(tfInfoId );
		flFormLayout1.addComponent(cbBranchid);
		flFormLayout1.addComponent(tfInfoLabel);
		//tfInfoId.setInputPrompt("IFSC Code");
		
		FormLayout flFormLayout2 = new FormLayout();
		flFormLayout2.setSpacing(true);
		flFormLayout2.addComponent(tfInfoDesc );
		flFormLayout2.addComponent(cbStatus );
		
			
		HorizontalLayout hlFormLayout = new HorizontalLayout();
		hlFormLayout.setSpacing(true);
		hlFormLayout.addComponent(flFormLayout1);
		hlFormLayout.addComponent(flFormLayout2);
	
		GridLayout vlMainPanelgrid = new GridLayout(1, 1);
		vlMainPanelgrid.setSpacing(true);
		vlMainPanelgrid.setMargin(true);
		vlMainPanelgrid.setSizeFull();

		vlMainPanelgrid.addComponent(hlFormLayout);

		lblFormTittle = new Label();
		lblFormTittle.setContentMode(ContentMode.HTML);

		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;Search");
		lblFormTitle1 = new Label();
		lblFormTitle1.setContentMode(ContentMode.HTML);

		lblFormTitle1.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;");
		lblAddEdit = new Label();
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
		flSearch1.addComponent(tfSearchInfoId);
		FormLayout flSearch3 = new FormLayout();
		flSearch3.addComponent(cbSearchStatus);

		HorizontalLayout hlSearchLayout = new HorizontalLayout();
		hlSearchLayout.addComponent(flSearch1);
		hlSearchLayout.addComponent(flSearch3);
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
		hlSearchComponent.setComponentAlignment(hlSearchLayout,
				Alignment.MIDDLE_LEFT);

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
	
		vlAudit = new VerticalLayout();
		vlAudit.removeAllComponents();
		
		hlAddEdit.addStyleName("topbarthree");
		hlAddEdit.setWidth("100%");
		hlAddEdit.addComponent(hlTableTittle);
		hlAddEdit.addComponent(hlFileDownload);
		hlAddEdit.setComponentAlignment(hlFileDownload, Alignment.MIDDLE_RIGHT);
		hlAddEdit.setHeight("30px");

		// table declaration

		tblbranchinfo = new Table();
		tblbranchinfo.setSizeFull();

		tblbranchinfo.setSelectable(true);
		tblbranchinfo.setColumnCollapsingAllowed(true);
		//tblbranchinfo.setPageLength(12);
		vlTablePanel = new VerticalLayout();
		tblbranchinfo.setImmediate(true);
		tblbranchinfo.setFooterVisible(true);

		vlTable.setSizeFull();
		vlTable.setMargin(new MarginInfo(false, true, false, true));
		vlTable.addComponent(hlAddEdit);
		vlTable.setSpacing(true);
		vlTable.addComponent(tblbranchinfo);
	//	vlTable.addComponent(vlAudit);
		vlTable.setExpandRatio(tblbranchinfo, 1);
		vlTablePanel.addComponent(vlTable);

		

		// add search,table and add fields layout to mainpanel
		clMainLayout.setSizeFull();
		clMainLayout.addComponent(vlSearchPanel);
		clMainLayout.addComponent(vlMainPanel);
		clMainLayout.addComponent(vlTablePanel);
		setTableProperties();
		populateAndConfig(false);

		hlBreadCrumbs = new HorizontalLayout();
		hlBreadCrumbs.addComponent(lblFormTitle1);
		hlBreadCrumbs.addComponent(btnBack);
		hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
		hlBreadCrumbs.addComponent(lblAddEdit);
		hlBreadCrumbs
				.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER);
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
		hlHeaderLayout.setComponentAlignment(hlNotification,
				Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlButtonLayout1);
		hlHeaderLayout.setComponentAlignment(hlButtonLayout1,
				Alignment.MIDDLE_RIGHT);

		excelexporter.setTableToBeExported(tblbranchinfo);
		csvexporter.setTableToBeExported(tblbranchinfo);
		pdfexporter.setTableToBeExported(tblbranchinfo);
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
	 * @param boolean search if(search==true)--> it performs search operation
	 * else it loads all values
	 */
	public void populateAndConfig(boolean search) {

		try {
			tblbranchinfo.removeAllItems();
			List<BranchInfoDM> list = new ArrayList<BranchInfoDM>();
			if (search) {

				String infoId = tfSearchInfoId.getValue();
				System.out.println("InfoId-->"+tfSearchInfoId.getValue());
				String status = null;
				StatusDM st = (StatusDM) cbSearchStatus.getValue();
				try {
					status= st.getCode();
					System.out.println("Status ===="+status);
				} catch (Exception e) {
					logger.info("status is empty on search");
				}

				if (infoId != null ||status != null) {

					
				
					list = service.getBranchlist(branchid, companyId, branchstatus);
					total = list.size();
					System.out.println("madhu list--------->"+list);
				}
				
			}
				else
				{
					list = service.getBranchlist(null, companyId, null);
					total = list.size();
				}

			beans = new BeanItemContainer<BranchInfoDM>(BranchInfoDM.class);
			beans.addAll(list);

			tblbranchinfo.setContainerDataSource(beans);

			tblbranchinfo.setVisibleColumns(new Object[] { "infoId", "infoStatus","lastupdateddt", "lastupdatedby" });

			tblbranchinfo.setColumnHeaders(new String[] { "Ref.Id","Status", "Last Updated Date", "Last Updated By" });

			tblbranchinfo.setColumnFooter("lastupdatedby", "No.of Records:"
					+ total);
	 
		}
		catch (Exception e) {
			logger.error("Error on populateAndConfig()---->" + e);
			e.printStackTrace();
		}
getExportTableDetails();
	}

	private void setTableProperties()

	{

		tblbranchinfo.setSelectable(true);
		tblbranchinfo.setColumnAlignment("bankbrnchid", Align.RIGHT);
		tblbranchinfo.addGeneratedColumn("lastupdateddt",
				new DateColumnGenerator());
		tblbranchinfo.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;

			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				if (tblbranchinfo.isSelected(event.getItemId())) {
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
		showComponentError();
		btnSave.setComponentError(null);
		//validateAll();
				
		 if (tblbranchinfo.getValue() != null) 
		 {
			BranchInfoDM updateob = beans.getItem(tblbranchinfo.getValue()).getBean();
			/*if (tfInfoId.isValid() && tfInfoLabel.isValid())
			{*/
			
			/*if (tfInfoId.getValue().trim().length() > 0) {
				updateob.setInfoId(tfInfoId.getValue().toString());
				tfInfoId.setComponentError(null);
			}

			else {
				tfInfoId.setComponentError(new UserError(
						"IFSC Code can not be null"));
			}*/
			if (tfInfoLabel.getValue().trim().length() > 0) {
				updateob.setInfoLabel(tfInfoLabel.getValue());
				tfInfoLabel.setComponentError(null);
			}

			else {
				tfInfoLabel.setComponentError(new UserError(
						"MICR Code can not be null"));
			}
			updateob.setInfoId(infoId);
			updateob.setBranchId(selectedBranch);
			updateob.setInfoDesc(tfInfoDesc.getValue());
			StatusDM stcode = (StatusDM) cbStatus.getValue();
			updateob.setInfoStatus(stcode.getCode());
			updateob.setLastupdatedby(loginUserName);
			updateob.setLastupdateddt(DateUtils.getcurrentdate());
			updateob.setCompanyid(companyId);
			
		if (tfInfoId.isValid() && cbStatus.isValid()) {
				service.saveDetails(updateob);
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
			
		} 
		else {
			
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
			lblNotification
					.setValue("Update failed, please check the data and try again ");
		 }
				 }
		 else
		 {

				BranchInfoDM saveobj = beans.getItem(tblbranchinfo.getValue()).getBean();
				/*if (tfInfoId.isValid() && tfInfoLabel.isValid())
				{*/
				
				/*if (tfInfoId.getValue().trim().length() > 0) {
					updateob.setInfoId(tfInfoId.getValue().toString());
					tfInfoId.setComponentError(null);
				}

				else {
					tfInfoId.setComponentError(new UserError(
							"IFSC Code can not be null"));
				}*/
				if (tfInfoLabel.getValue().trim().length() > 0) {
					saveobj.setInfoLabel(tfInfoLabel.getValue());
					tfInfoLabel.setComponentError(null);
				}

				else {
					tfInfoLabel.setComponentError(new UserError(
							"MICR Code can not be null"));
				}
				saveobj.setInfoId(infoId);
				saveobj.setBranchId(selectedBranch);
				saveobj.setInfoDesc(tfInfoDesc.getValue());
				StatusDM stcode = (StatusDM) cbStatus.getValue();
				saveobj.setInfoStatus(stcode.getCode());
				saveobj.setLastupdatedby(loginUserName);
				saveobj.setLastupdateddt(DateUtils.getcurrentdate());
				saveobj.setCompanyid(companyId);
				
			if (tfInfoId.isValid() && cbStatus.isValid()) {
					service.saveDetails(saveobj);
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
				
			} 
			else {
				
				lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
				lblNotification
						.setValue("Update failed, please check the data and try again ");
			 }
		 }
		}
		 
		catch(Exception e){
			logger.error("Error during save/update details --->"+e);
			e.printStackTrace();
		}
	}
	private void showComponentError() {
		tfInfoId.setComponentError(null);
		tfInfoLabel.setComponentError(null);

		if (tfInfoId.getValue() == null
				|| tfInfoId.getValue().trim().length() == 0) {
			tfInfoId.setComponentError(new UserError(
					"Please Enter IFSC Code"));
		}
		if (tfInfoLabel.getValue() == null
				|| tfInfoLabel.getValue().trim().length() == 0) {
			tfInfoLabel.setComponentError(new UserError(
					"Please enter MICR Code"));
		}

	}

	/*
	 * fn_editDetails
	 * 
	 * This fuction is used for edit Account details
	 */
	private void editDetails() {	
		try{
		Item itselect = tblbranchinfo.getItem(tblbranchinfo.getValue());
		if (itselect != null) {
			
			BranchInfoDM edit = beans.getItem(tblbranchinfo.getValue()).getBean();
			
			 primaryId = edit.getBranchId().toString();
			if(edit.getBranchId()!=null)
			{
				BranchDM mfb = edit.getBranchId();
				//Long mfb = edit.getBankid();
				//BranchInfoDMBranch editaccount = edit.getBankid().toString();
				Collection<?> mfbid = cbBranchid.getItemIds();
				for (Iterator<?> iterator = mfbid.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbBranchid.getItem(itemId);
					// Get the actual bean and use the data
					BranchDM st = (BranchDM) item.getBean();
					System.out.println("Branch id--------->"+itemId);
					if (mfb != null &&mfb.getBranchId().equals(st.getBranchId()))
					{
						System.out.println("Branch id--------->"+itemId);

						cbBranchid.setValue(itemId);
						

					}				}			}

			
			
		if (edit.getInfoId() != null)
					{
				tfInfoId.setValue(edit.getInfoId().toString());
			}
		
		if(edit.getInfoLabel()!=null){
			tfInfoLabel.setValue(edit.getInfoLabel());
		}
		
		if(edit.getInfoDesc()!=null)
		{
				tfInfoDesc.setValue(edit.getInfoDesc());
			}
			
				
						
			String st = (String) edit.getInfoStatus();
			cbStatus.setValue(Common.getStatus(st));
	
	}
		}
		catch(Exception e){
			logger.error("error during edit details-->"+e);
			e.printStackTrace();
		}
	}

	private void resetFields() {
		// TODO Auto-generated method stub
		//cbbrnchid.setValue("");
		cbBranchid.setValue(null);
		tfInfoId.setValue("");
		tfInfoLabel.setValue("");
		tfInfoDesc.setValue("");
		//cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
		tfInfoId.setComponentError(null);
		tfInfoLabel.setComponentError(null);
		btnSave.setComponentError(null);
		btnSave.setCaption("Save");
		tfInfoId.setComponentError(null);
		lblNotificationIcon.setIcon(null);
		lblNotification.setValue("");
		//lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName+ "</b>&nbsp;::&nbsp;Search");
		//lblNotificationIcon.setIcon(null);
		//lblNotification.setValue("");
		lblFormTittle.setVisible(true);
		hlBreadCrumbs.setVisible(false);
	}

	private void resetSearchFields() {
		cbSearchStatus.setValue(null);
		tfSearchInfoId.setValue("");
		lblNotification.setValue("");
		lblNotification.setValue("");
	}
	/*private void validateAll() {
		// TODO Auto-generated method stub
		try {
			tfInfoId.validate();
		} catch (Exception e) {
			logger.error("Bank type name text field value empty-->"+e);
		}
		
	}*/
	

	void setComponentStyle() {
		
		//cbbrnchid.setWidth(strWidth);
		cbBranchid.setWidth(strWidth);
		tfInfoId.setWidth(strWidth);
		tfInfoLabel.setWidth(strWidth);
		tfInfoDesc.setWidth(strWidth);
		//cbStatus.setWidth(strWidth);
		//cbludt.setWidth(strWidth);
		//cbluby.setWidth(strWidth);
		
	}

	void loadForeignKeyDetails() {

		tfInfoId.setRequired(true);
		//tfInfoId.setRequiredError("Please Enter IFSC code");
		//tfInfoId.setInputPrompt("ENTER IFSCCODE");
		tfInfoId.addValidator(new StringLengthValidator(
				" IFSC CODE must be 2 to 30 characters", 2, 30,
				true));
		tfInfoId.setMaxLength(30);
		
		
		tfInfoLabel.setRequired(true);
		//tfInfoLabel.setRequiredError("Please Enter MICR code");
		//tfInfoLabel.setInputPrompt("ENTER MICR");
		tfInfoLabel.addValidator(new StringLengthValidator(
				" MICR code must be 2 to 30 characters", 2, 30,
				true));
		tfInfoLabel.setMaxLength(30);
		
			
		// add search fields to panel
		//tfSearchInfoId.setInputPrompt("IFSC Code");
		//tfSearchMICRcode.setInputPrompt("MICR Code");
		
		//cbBranchid.setInputPrompt(Common.SELECT_PROMPT);
		cbBranchid.setImmediate(true);
		cbBranchid.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbBranchid.setNullSelectionAllowed(true);
		cbBranchid.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = 1L;

					public void valueChange(ValueChangeEvent event) {
						Object itemid = event.getProperty().getValue();
						if (itemid != null) {
							BeanItem<?> item = (BeanItem<?>) cbBranchid.getItem(itemid);
							selectedBranch = (BranchDM) item.getBean();
						}
					}
				});
	}		
				
	/*
	 * 
	 * this function handles get country list to country name component.
	 */

	
	
	private void loadBranchList() {
		try {
			List<BranchDM> getbranchlist = branchbean.getBranchList(null, Common.ACTIVE_DESC, null);
			BeanItemContainer<BranchDM > beanBranch = new BeanItemContainer<BranchDM>(BranchDM.class);
			beanBranch.addAll(getbranchlist);
			cbBranchid.setContainerDataSource(beanBranch);
		} catch (Exception e) {
			logger.warn("Loading null values in loadCountryList() functions----->>>>>"+ e);
		}
	}
	

	/**
	 * this used to export the data 
	 */
	private void getExportTableDetails()
	{
		excelexporter.setTableToBeExported(tblbranchinfo);
		csvexporter.setTableToBeExported(tblbranchinfo);
		pdfexporter.setTableToBeExported(tblbranchinfo);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
		
	}
	/*
	 * For Load Active Employee Details based on Company
	 */
		
		public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (btnAdd == event.getButton()) {
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			resetFields();
			btnAdd.setEnabled(false);
			hlButtonLayout1.setVisible(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			tblbranchinfo.setValue(null);
			vlAudit.setVisible(false);
			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			populateAndConfig(false);
		}

		else if (btnSave == event.getButton())
		{
			saveDetails();
		} 
		else if (btnCancel == event.getButton()) {
			vlMainPanel.setVisible(false);
			vlTablePanel.setVisible(true);
			vlSearchPanel.setVisible(true);
			populateAndConfig(false);
			resetFields();
			resetSearchFields();
			btnHome.setEnabled(false);
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			btnAuditRecords.setEnabled(true);
			vlAudit.setVisible(false);
			hlButtonLayout1.setVisible(false);

			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Search");

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
			btnHome.setEnabled(false);
			btnAdd.setEnabled(false);
			btnEdit.setEnabled(false);
			btnAuditRecords.setEnabled(true);
			vlAudit.setVisible(false);
			vlMainPanel.setVisible(true);
			vlSearchPanel.setVisible(false);
			hlButtonLayout1.setVisible(true);
			resetFields();
			editDetails();
			btnSave.setCaption("Update");
			//btnSave.setDescription("Update Bank Detail");
			btnEdit.setEnabled(false);
			//lblNotification.setValue("Successfully Updated");
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
				lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
				lblNotification.setValue("No Records found");
			
			}
			hlFileDownload.removeAllComponents();
			hlFileDownload.addComponent(btnDownload);
			getExportTableDetails();
			vlAudit.setVisible(false);
		} else if (btnAuditRecords == event.getButton()) {
			btnEdit.setEnabled(false);
			btnAuditRecords.setEnabled(false);
			btnHome.setEnabled(true);
			btnAdd.setEnabled(false);
			vlAudit.setVisible(true);
			vlAudit.removeAllComponents();
			AuditRecordsApp recordApp=new AuditRecordsApp(vlAudit,Common.M_FMS_BANK_BRANCH,primaryId);
		
			hlFileDownload.removeAllComponents();
			hlFileDownload.addComponent(recordApp.btnDownload);
				getExportTableDetails();
	    
				vlTable.removeAllComponents();
		vlTable.addComponent(hlAddEdit);
		vlTable.addComponent(vlAudit);
		lblFormTittle.setVisible(false);
		hlBreadCrumbs.setVisible(true);
		lblAddEdit.setValue("&nbsp;>&nbsp;Audit History");
		
		} else if (btnHome == event.getButton()) {
			vlMainPanel.setVisible(false);
			vlSearchPanel.setVisible(true);
			vlTablePanel.setVisible(true);
			hlButtonLayout1.setVisible(false);
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			btnHome.setEnabled(false);
			btnAuditRecords.setEnabled(true);
			vlAudit.setVisible(false);
			tblbranchinfo.setVisible(true);
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
			vlTable.addComponent(tblbranchinfo);
			populateAndConfig(false);
			
		} else if (btnClear == event.getButton()) {
			lblNotificationIcon.setVisible(false);
			resetSearchFields();
			populateAndConfig(false);
		}else if (btnDownload == event.getButton()) {

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
		else if (btnBack == event.getButton()) {
			vlMainPanel.setVisible(false);
			vlSearchPanel.setVisible(true);
			vlTablePanel.setVisible(true);
			hlButtonLayout1.setVisible(false);
			btnAdd.setEnabled(true);
			btnHome.setEnabled(false);
			btnAuditRecords.setEnabled(true);
			vlAudit.setVisible(false);
			tblbranchinfo.setVisible(true);
			hlFileDownload.removeAllComponents();
			hlFileDownload.addComponent(btnDownload);
			getExportTableDetails();
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Search");
			lblFormTitle1.setVisible(true);
			lblNotification.setVisible(false);
			lblNotificationIcon.setVisible(false);
			hlBreadCrumbs.setVisible(false);
			lblFormTittle.setVisible(true);
		 	vlTable.removeAllComponents();
			vlTable.addComponent(hlAddEdit);
			vlTable.addComponent(tblbranchinfo);
			populateAndConfig(false);
		}

	}

		}

