/**
 * File Name 		: Tags.java 
 * Description 		: this class is used for add/edit Tags details. 
 * Author 			: Hema
 * Date 			: March 05, 2014
 * Modification 	:
 * Modified By 		: Hema 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 
 */
package com.gnts.gcat.txn;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Status;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.gcat.domain.txn.TagsDM;
import com.gnts.gcat.service.txn.TagsService;
import com.vaadin.client.ui.calendar.schedule.DateUtil;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class Tags implements ClickListener {
	
	private ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext-core.xml");
	private static final long serialVersionUID = 1L;
	
    private TagsService servicebeanTags = (TagsService) appContext.getBean("tags");
	
	private BeanItemContainer<TagsDM> beanTags = new BeanItemContainer<TagsDM>(TagsDM.class);
//	private BeanItemContainer<StatusDM> beanStatus = null;
	
	// Button Declarations
	private Button btnAdd;
	private Button btnEdit;
	private Button btnCancel;
	private Button btnSearch;
	private Button btnReset;
	private Button btnSave;
	private Button btnDownload,btnBack;
	
	private TextField tftagdescS,tftagdesc;
	private ComboBox cbstatusS,cbstatus;
	private Table tbltags;
	
	private FormLayout flSearchform1, f2Searchform2, flMainform1, f2Mainform2;
	private VerticalLayout vlMainLayout, vlSearchLayout,vlTableLayout;
	private HorizontalLayout hlAddEditLayout,hlButtonLayout1,hlBreadCrumbs,hlFileDownload;
	
	private Label lblButton = new Label();
	private Label lblFormTittle,lblFormTitle1,lblAddEdit;
	private Label lblSaveNotification, lblNotificationIcon;
	
	private int total = 0;
	private Long companyid;
	private String username;
	private String screenName;
	
	private Window notifications;
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	
	private Logger logger = Logger.getLogger(Tags.class);
	
	public Tags() {
		
	username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
	companyid=Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
	screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
	VerticalLayout clArgumentLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
	HorizontalLayout hlHeaderLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");
	
	buildview(clArgumentLayout, hlHeaderLayout);
	
	}
	private void buildview(VerticalLayout clArgumentLayout,
			HorizontalLayout hlHeaderLayout) {
		
		hlHeaderLayout.removeAllComponents();
		
		lblSaveNotification = new Label();
		lblSaveNotification.setContentMode(ContentMode.HTML);
		lblNotificationIcon = new Label();

		btnAdd=new Button("Add",this);
		btnCancel = new Button("Cancel",this);
		btnSearch = new Button("Search",this);
		btnSave = new Button("Save",this);
		btnSave.setStyleName("styles.css/buttonrefresh");
		btnEdit = new Button("Edit",this);
		btnEdit.setEnabled(false);
		btnReset = new Button("Reset",this);
		btnDownload=new Button("Download",this);
		
		btnSave.setStyleName("savebt");
		btnAdd.addStyleName("add");
		btnCancel.addStyleName("cancelbt");
		btnEdit.addStyleName("editbt");
		btnReset.addStyleName("resetbt");
		btnSearch.setStyleName("searchbt");
		btnDownload.setStyleName("downloadbt");
		
		btnBack=new Button("Search",this);
		btnBack.setStyleName("link");
		
		btnDownload.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
		
			public void buttonClick(ClickEvent event) {
				event.getButton().removeStyleName("unread");
				if (notifications != null && notifications.getUI() != null)
					notifications.close();
				else {
					buildNotifications(event);
					UI.getCurrent().addWindow(notifications);
					notifications.focus();
					((VerticalLayout) UI.getCurrent().getContent())
							.addLayoutClickListener(new LayoutClickListener() {
								
								private static final long serialVersionUID = 1L;

								@Override
								public void layoutClick(LayoutClickEvent event) {
									notifications.close();
									((VerticalLayout) UI.getCurrent()
											.getContent())
											.removeLayoutClickListener(this);
								}
							});
				}

			}
		});
		
		
		//Search panel components
		tftagdescS=new TextField("Tag Desc");
		tftagdescS.setWidth("180");
		
		
		cbstatusS = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		//cbstatusS.setItemCaptionPropertyId("desc");
		//cbstatusS.setImmediate(true);
		//cbstatusS.setNullSelectionAllowed(false);
//		beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
	//	beanStatus.addAll(Common.listStatus);
	//	cbstatusS.setContainerDataSource(beanStatus);
		//cbstatusS.setWidth("180");
		
		//Main panel components
		tftagdesc=new TextField("Tag Desc");
		tftagdesc.setWidth("180");
		tftagdesc.setRequired(true);
	
		
		cbstatus = new ComboBox("Status");
		cbstatus.setItemCaptionPropertyId("desc");
		cbstatus.setImmediate(true);
		cbstatus.setNullSelectionAllowed(false);
	//	beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
	//	beanStatus.addAll(Common.listStatus);
	//	cbstatus.setContainerDataSource(beanStatus);
		cbstatus.setWidth("180");
		
		hlButtonLayout1 = new HorizontalLayout();
		hlButtonLayout1.addComponent(btnSave);
		hlButtonLayout1.addComponent(btnCancel);
		hlButtonLayout1.setVisible(false);
		
		flSearchform1=new FormLayout();
		flSearchform1.addComponent(tftagdescS);
		
		f2Searchform2=new FormLayout();
		f2Searchform2.addComponent(cbstatusS);

		HorizontalLayout hlsearch=new HorizontalLayout();
		hlsearch.addComponent(flSearchform1);
		hlsearch.addComponent(f2Searchform2);
		hlsearch.setSpacing(true);
		hlsearch.setMargin(true);
		
		VerticalLayout hlSearchButtonLayout = new VerticalLayout();
		hlSearchButtonLayout.setSpacing(true);
		hlSearchButtonLayout.addComponent(btnSearch);
		hlSearchButtonLayout.addComponent(btnReset);
		hlSearchButtonLayout.setWidth("17%");
		hlSearchButtonLayout.addStyleName("topbarthree");
		hlSearchButtonLayout.setMargin(true);
		
		HorizontalLayout hlSearchPanel = new HorizontalLayout();
		hlSearchPanel.setSizeFull();
		hlSearchPanel.setSpacing(true);
		hlSearchPanel.addComponent(hlsearch);
		hlSearchPanel.setComponentAlignment(hlsearch, Alignment.MIDDLE_LEFT);
		hlSearchPanel.addComponent(hlSearchButtonLayout);
		hlSearchPanel.setComponentAlignment(hlSearchButtonLayout,
				Alignment.MIDDLE_RIGHT);
		
		
		final VerticalLayout vlSearchPanel = new VerticalLayout();
		vlSearchPanel.setSpacing(true);
		vlSearchPanel.setSizeFull();
		vlSearchPanel.addComponent(hlSearchPanel);
		
		vlSearchLayout = new VerticalLayout();
		vlSearchLayout.addComponent(GERPPanelGenerator.createPanel(vlSearchPanel));
		vlSearchLayout.setMargin(true);
		
		flMainform1=new FormLayout();
		flMainform1.addComponent(tftagdesc);
		flMainform1.setSpacing(true);
		
		f2Mainform2=new FormLayout();
		f2Mainform2.addComponent(cbstatus);
		f2Mainform2.setSpacing(true);
		
		HorizontalLayout hlMain = new HorizontalLayout();
		hlMain.addComponent(flMainform1);
		hlMain.addComponent(f2Mainform2);
		hlMain.setSpacing(true);
		hlMain.setMargin(true);
		
		final GridLayout glGridLayout1 = new GridLayout(1, 2);
		glGridLayout1.setSizeFull();
		glGridLayout1.setSpacing(true);
		glGridLayout1.setMargin(true);
		glGridLayout1.addComponent(hlMain);
		
		vlMainLayout = new VerticalLayout();
		vlMainLayout.addComponent(GERPPanelGenerator.createPanel(glGridLayout1));
		vlMainLayout.setMargin(true);
		vlMainLayout.setVisible(false);
		
		hlFileDownload = new HorizontalLayout();
		hlFileDownload.setSpacing(true);

		hlFileDownload.addComponent(btnDownload);
		hlFileDownload.setComponentAlignment(btnDownload,
				Alignment.MIDDLE_CENTER);
		
		HorizontalLayout hlTableTittleLayout = new HorizontalLayout();
		hlTableTittleLayout.addComponent(btnAdd);
		hlTableTittleLayout.addComponent(btnEdit);
		
		hlAddEditLayout = new HorizontalLayout();
		hlAddEditLayout.addStyleName("topbarthree");
		hlAddEditLayout.setWidth("100%");
		hlAddEditLayout.addComponent(hlTableTittleLayout);
		hlAddEditLayout.addComponent(hlFileDownload);
		hlAddEditLayout.setComponentAlignment(hlFileDownload,
				Alignment.MIDDLE_RIGHT);
		hlAddEditLayout.setHeight("28px");
	
		
		// Table Initialization
		tbltags=new Table();
		tbltags.setImmediate(true);
		tbltags.setSelectable(true);
		tbltags.setStyleName(Runo.TABLE_SMALL);
		tbltags.setColumnCollapsingAllowed(true);
		tbltags.setPageLength(12);
		tbltags.setSizeFull();
		tbltags.setFooterVisible(true);
		
		final VerticalLayout vlTableForm = new VerticalLayout();
		vlTableForm.setSizeFull();
		vlTableForm.setMargin(true);
		vlTableForm.addComponent(hlAddEditLayout);
		vlTableForm.addComponent(tbltags);
		
		vlTableLayout = new VerticalLayout();
		vlTableLayout.setStyleName(Runo.PANEL_LIGHT);
		vlTableLayout.addComponent(vlTableForm);
		
		clArgumentLayout.addComponent(vlMainLayout);
		clArgumentLayout.addComponent(vlSearchLayout);
		clArgumentLayout.addComponent(vlTableLayout);
		populatedAndConfig(false);
		setTableProperties();
		
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
		
		hlBreadCrumbs=new HorizontalLayout();
		hlBreadCrumbs.addComponent(lblFormTitle1);
		hlBreadCrumbs.addComponent(btnBack);
		hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
		hlBreadCrumbs.addComponent(lblAddEdit);
		hlBreadCrumbs.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER);
		hlBreadCrumbs.setVisible(false);
		
		
		HorizontalLayout hlNotificationLayout = new HorizontalLayout();
		hlNotificationLayout.addComponent(lblNotificationIcon);
		hlNotificationLayout.setComponentAlignment(lblNotificationIcon,Alignment.MIDDLE_CENTER);
		hlNotificationLayout.addComponent(lblSaveNotification);
		hlNotificationLayout.setComponentAlignment(lblSaveNotification,Alignment.MIDDLE_CENTER);
		hlHeaderLayout.addComponent(lblFormTittle);
		hlHeaderLayout.setComponentAlignment(lblFormTittle,Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlBreadCrumbs);
		hlHeaderLayout.setComponentAlignment(hlBreadCrumbs,Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlNotificationLayout);
		hlHeaderLayout.setComponentAlignment(hlNotificationLayout,Alignment.MIDDLE_LEFT);
		hlHeaderLayout.addComponent(hlButtonLayout1);
		hlHeaderLayout.setComponentAlignment(hlButtonLayout1,Alignment.MIDDLE_RIGHT);
		
		excelexporter.setTableToBeExported(tbltags);
		csvexporter.setTableToBeExported(tbltags);
		pdfexporter.setTableToBeExported(tbltags);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
		
		//cbstatusS.setValue(Common.getStatus(Common.ACTIVE_CODE));
	}
	
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
	// Method for show the details in grid table while search and normal mode
		public void populatedAndConfig(boolean search) {

			//try{
			tbltags.removeAllItems();

			List<TagsDM> tagList = null;
			if (search) {

				tagList = new ArrayList<TagsDM>();
				String tagDesc = tftagdescS.getValue().toString();
				
				try {
		//			Status = st.getCode();
				} catch (Exception e) {
					logger.info("status is empty on search");
				}

				if (tagDesc != null) {

					tagList = servicebeanTags.getTagsList(companyid,tagDesc,cbstatusS.getValue().toString());
					total = tagList.size();
				}

				/*if (total == 0) {

					Notification.show("No Records Found");
				}*/
			} else {
			
				tagList = servicebeanTags.getTagsList(companyid,null,null);
				total = tagList.size();

			}
			lblButton
					.setValue("<font size=\"2\" color=\"black\">No.of Records: </font> <font size=\"2\" color=\"#1E90FF\"> "
							+ total + "</font>");
			beanTags = new BeanItemContainer<TagsDM>(
					TagsDM.class);

			beanTags.addAll(tagList);
			tbltags.setSelectable(true);
			tbltags.setContainerDataSource(beanTags);
			tbltags.setVisibleColumns(new Object[] {"tagsid","tagdesc","tagstatus",
					 "lastupdateddt", "lastupdatedby" });

			tbltags.setColumnHeaders(new String[] {"Ref.Id","Tag Description", "Status", "Last Updated Date",
					                            "Last Updated By" });
			tbltags.setColumnFooter("lastupdatedby", "No.of Records : "
					+ total);
			tbltags.addItemClickListener(new ItemClickListener() {

				public void itemClick(ItemClickEvent event) {

					// TODO Auto-generated method stub
					if (tbltags.isSelected(event.getItemId())) {
						btnEdit.setEnabled(false);
						btnAdd.setEnabled(true);

					} else {
						btnEdit.setEnabled(true);

						btnAdd.setEnabled(false);

					}
					resetFields();
					btnSave.setCaption("Save");

				}

			});
			//}catch(Exception e){
			//	logger.error("error during populate values on the table, The Error is ----->"+e);
			//}
		}
		public void setTableProperties() {

			/*beanTags = new BeanItemContainer<Tags>(
					Tags.class);*/
			
	//		tbltags.addGeneratedColumn("lastupdateddt", new DateColumnGenerator());
		}

		
		private void editTags() {

			vlMainLayout.setVisible(true);
			Item itselect = tbltags.getItem(tbltags.getValue());
			if (itselect != null) {
				
				String stCode = itselect.getItemProperty("tagstatus").getValue()
						.toString();
		//		cbstatus.setValue(Common.getStatus(stCode));

				tftagdesc.setValue(itselect.getItemProperty("tagdesc")
						.getValue().toString());
			}

		}
		
		
		private void saveorUpdateTags() {
			boolean valid = false;
			if (tbltags.getValue() != null) {
				TagsDM update = beanTags.getItem(tbltags.getValue()).getBean();
				update.setCompanyid(companyid);
				
				if(tftagdesc.getValue().trim().length()==0) {
					tftagdesc.setComponentError(new UserError("Tag description should not be empty"));
				}
				update.setTagdesc(tftagdesc.getValue());

				update.setLastupdatedby(username);
			//	update.setLastupdateddt(DateUtils.getcurrentdate());

			//	StatusDM st = (StatusDM) cbstatus.getValue();
			//	update.setTagstatus(st.getCode());

				if (tftagdesc.isValid()) {
					servicebeanTags.saveorUpdateTagsDetails(update);

					valid = true;
					lblNotificationIcon.setIcon(new ThemeResource(
							"img/success_small.png"));
				//	lblSaveNotification.setValue(ApplicationConstants.updatedMsg);

				}
			}

			else {

				TagsDM save = new TagsDM();

				save.setCompanyid(companyid);
				
				if(tftagdesc.getValue().trim().length()==0) {
					tftagdesc.setComponentError(new UserError("Tag description should not be empty"));
				}
				save.setTagdesc(tftagdesc.getValue());

				save.setLastupdatedby(username);
				//save.setLastupdateddt(DateUtil.getcurrentdate());

		//		StatusDM st = (StatusDM) cbstatus.getValue();
		//		save.setTagstatus(st.getCode());

				if (tftagdesc.isValid()) {
					servicebeanTags.saveorUpdateTagsDetails(save);

					valid = true;
					

					lblNotificationIcon.setIcon(new ThemeResource(
							"img/success_small.png"));
				
				
				}

			}

			if (valid) {
				populatedAndConfig(false);
				btnSave.setComponentError(null);
				resetFields();
				btnSave.setCaption("Save");
				lblFormTittle.setVisible(true);
				lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
						+ "</b>&nbsp;::&nbsp;Search");
				hlBreadCrumbs.setVisible(false);
				
			} else {
				lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
				lblSaveNotification
						.setValue("Save failed, please check the data and try again ");
			}
		}

		private void resetSearchFields() {

			tftagdescS.setValue("");
	//		cbstatusS.setValue(Common.getStatus(Common.ACTIVE_CODE));
			btnSearch.setComponentError(null);
		
		}
		
		void resetFields() {
			
			tftagdesc.setComponentError(null);
			btnSave.setComponentError(null);
			tftagdesc.setValue("");
			btnSave.setCaption("save");
			cbstatus.setComponentError(null);
			cbstatus.setValue(null);
			
		}
		
	@Override
	public void buttonClick(ClickEvent event) {
		
		if (btnAdd == event.getButton()) {
			resetFields();
			btnSave.setCaption("Save");
			btnAdd.setEnabled(false);
			btnEdit.setEnabled(false);
			vlMainLayout.setVisible(true);
			vlSearchLayout.setVisible(false);
			vlTableLayout.setVisible(true);
			populatedAndConfig(false);
			hlButtonLayout1.setVisible(true);
			lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			btnDownload.setEnabled(false);
			
		} 
			else if (btnCancel == event.getButton()) {
			vlSearchLayout.setVisible(true);
			vlMainLayout.setVisible(false);
			tbltags.setValue(null);
			resetFields();
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			hlButtonLayout1.setVisible(false);

			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Search");
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
			btnDownload.setEnabled(true);
			

		} else if (btnSearch == event.getButton()) {
			populatedAndConfig(true);
			if (total == 0) {
				lblSaveNotification.setValue("No Records found");
			} 
			tbltags.setVisible(true);  
			btnEdit.setEnabled(false);
			btnAdd.setEnabled(true);
			btnDownload.setEnabled(true);
		}

		else if (btnEdit == event.getButton()) {
			vlMainLayout.setVisible(true);
			vlSearchLayout.setVisible(false);
			editTags();
			btnSave.setCaption("Update");
			lblAddEdit.setValue("&nbsp;>&nbsp;Modify");
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			lblFormTittle
			.setValue("&nbsp;&nbsp;<b>"
					+ screenName
					+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Search</font> &nbsp;>&nbsp;Modify");
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			hlButtonLayout1.setVisible(true);
			btnDownload.setEnabled(false);
			
		}
		if (btnSave == event.getButton()) {
			System.out.println("TEST");
			try{
				saveorUpdateTags();
		} catch (Exception e) {
			logger.info("Error on saveorUpdateTags() function--->" + e);
			e.printStackTrace();
		}
			lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
		}

		if (btnReset == event.getButton()) {
			resetSearchFields();
			populatedAndConfig(false);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
		}
		
		else if(btnBack==event.getButton())
		{

			resetFields();
			vlMainLayout.setVisible(false);
			vlSearchLayout.setVisible(true);
			vlTableLayout.setVisible(true);
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			
			hlButtonLayout1.setVisible(false);
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Search");

			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
			lblAddEdit.setValue("");
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			tbltags.setVisible(true);
			populatedAndConfig(false);
			btnDownload.setEnabled(true);
			
		   }
		
		
		
	    }
		
	}
	
