package com.gnts.pem.mst;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.type.descriptor.sql.LongVarcharTypeDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;






//import com.gnts.base.domain.MBaseBranch;
//import com.gnts.base.domain.MBaseDepartment;
//import com.gnts.base.domain.MBaseEmployee;
//import com.gnts.base.domain.Status;
import com.gnts.erputil.helper.SpringContextHelper;
//import com.gnts.base.service.MBaseEmployeeService;
//import com.gnts.base.service.MBaseEmployeeService;
//import com.gnts.base.service.MBaseUSerService;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.CurrencyColumnGenerator;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.validations.DateUtils;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
//import com.gnts.pem.domain.CmCommonSetup;
//import com.gnts.pem.domain.MPemCmBank;
//import com.gnts.pem.service.CmBankService;
//import com.gnts.pem.service.CmCommonSetupService;
import com.gnts.erputil.Common;
import com.gnts.pem.domain.mst.CommissionSetupDM;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.mst.CommissionSetupService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
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
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CommissionSetup implements ClickListener {
	
	private ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext-core.xml");
	private static final long serialVersionUID = 1L;
	
	private BeanItemContainer<StatusDM> beanStatus = null;
	private BeanItemContainer<CommissionSetupDM> beanCommonSetup = null;
	private BeanItemContainer<MPemCmBank> beanBank = null;
	private CmBankService servicebeanBank = (CmBankService) SpringContextHelper.getBean("bank");
	
	
	private CommissionSetupService servicebeanCommonSetup = (CommissionSetupService) appContext.getBean("commonsetup");

	private ComboBox cbSearchBankName;
	private ComboBox cbSearchStatus;
	
	// Button Declarations
		private Button btnSave, btnCancel, btnAdd, btnEdit, btnSearch, btnReset,btnDownload,btnAuditrRecords,btnHome;
		
	private HorizontalLayout	hlFileDownloadLayout;
	// Table Panel Components
		
		private ComboBox cbFileFormat;
		private	HorizontalLayout hlSaveandCancelButtonLayout;
		
	// Mainpanel Components
	
	private TextField tfStartValue;
	private TextField tfEndValue;
	private TextField tfCommPercent;
	private ComboBox cbStatus;
	private ComboBox cbBankName;
	
	String orderby="lastUpdatedDt desc";
	private Label lblTableTitle,lblSearchTitle,lblFormTitle,lblNotification, lblNotificationIcon,lblNoofRecords;
	
	//Layouts
		private VerticalLayout vlSearchPanel = new VerticalLayout();
		private VerticalLayout vltablePanel = new VerticalLayout();
		private VerticalLayout vlmainpanel1 = new VerticalLayout();
		private HorizontalLayout hlimage = new HorizontalLayout();
		private VerticalLayout vlTableLayout ,vlAudit;
		private	HorizontalLayout hlAddEdit;

		private Table tblCommon;
		//
		
		//Window	
		private Window notifications=new Window();
		
		//Report	
		private ExcelExporter excelexporter = new ExcelExporter();
		private CSVExporter csvexporter = new CSVExporter();
		private PdfExporter pdfexporter = new PdfExporter();
		
		private String strLoginUserName , strScreenName;
		
		private Long companyId,searchBankId,  PrimaryId;
		
		private String cmSetupid;
		
		private MPemCmBank selectBank;
		
		private static Logger log = Logger.getLogger(CommissionSetup.class);
		
		List<CommissionSetupDM> commonList = null;
		
		private Long maxPrimaryId,editBankId,minPrimaryId;
		
		private Double endValue,nextStartValue;
		
//		private String status = "'Active'";
		
		private int total = 0;
		
		private	Double startValue,endingValue;
		
		public CommissionSetup()
		{
			
			strLoginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
			companyId=Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		//	currencyId=Long.valueOf(UI.getCurrent().getSession().getAttribute("currenyId").toString());
			strScreenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
			VerticalLayout clMainLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
			HorizontalLayout hlScreenNameLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");
			buildView(clMainLayout, hlScreenNameLayout);
		}
		
		private void buildView(VerticalLayout clMainLayout,HorizontalLayout hlScreenNameLayout)
		{
			clMainLayout.removeAllComponents();
			System.out.println("------------------------------------------");
			hlScreenNameLayout.removeAllComponents();
			
			/* Search Components starts */
			
			cbSearchBankName=new ComboBox("Bank Name");
			cbSearchBankName.setInputPrompt(ApplicationConstants.selectDefault);
			cbSearchBankName.setItemCaptionPropertyId("bankName");

			loadSearchBankName();
			
			 cbSearchBankName.addValueChangeListener(new Property.ValueChangeListener() {
				 private static final long serialVersionUID = 1L;

					public void valueChange(ValueChangeEvent event) {

						final Object itemId = event.getProperty().getValue();
						if (itemId != null) {
							final BeanItem<?> item = (BeanItem<?>) cbSearchBankName.getItem(itemId);
							selectBank = (MPemCmBank) item.getBean();
							searchBankId = selectBank.getBankId()	;
							
							
							}
					}
				});
			 cbSearchBankName.setImmediate(true);
			 cbSearchBankName.setNullSelectionAllowed(false);
			 cbSearchBankName.setWidth("200");
			
			cbSearchStatus=new ComboBox("Status");
			cbSearchStatus.setItemCaptionPropertyId("desc");
			cbSearchStatus.setInputPrompt(ApplicationConstants.selectDefault);
			
			beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
			beanStatus.addAll(Common.listStatus);
			cbSearchStatus.setContainerDataSource(beanStatus);
			cbSearchStatus.setImmediate(true);
			cbSearchStatus.setNullSelectionAllowed(false);
			cbSearchStatus.setWidth("75");
			
			FormLayout flSearchBankName = new FormLayout();
			flSearchBankName.addComponent(cbSearchBankName);
			
			FormLayout flSearchStatus = new FormLayout();
			flSearchStatus.addComponent(cbSearchStatus);
			
			HorizontalLayout hlSearchLayout = new HorizontalLayout();
			hlSearchLayout.addComponent(flSearchBankName);
			hlSearchLayout.addComponent(flSearchStatus);
			hlSearchLayout.setSpacing(true);
			
			hlSearchLayout.setMargin(true);
			
			btnSearch = new Button("Search", this);
			btnSearch.setStyleName("searchbt");

			btnReset = new Button("Reset", this);
			btnReset.addStyleName("resetbt");
	
			
			VerticalLayout vlbtnSearchLayout = new VerticalLayout();
			vlbtnSearchLayout.setSpacing(true);
			vlbtnSearchLayout.addComponent(btnSearch);
			vlbtnSearchLayout.addComponent(btnReset);
			vlbtnSearchLayout.setWidth("17%");
			vlbtnSearchLayout.addStyleName("topbarthree");
			vlbtnSearchLayout.setMargin(true);

			HorizontalLayout searchcomponetHl = new HorizontalLayout();
			searchcomponetHl.setSizeFull();
			searchcomponetHl.setSpacing(true);
			searchcomponetHl.addComponent(hlSearchLayout);
			searchcomponetHl.setComponentAlignment(hlSearchLayout, Alignment.MIDDLE_LEFT);

			searchcomponetHl.addComponent(vlbtnSearchLayout);
			searchcomponetHl.setComponentAlignment(vlbtnSearchLayout,Alignment.MIDDLE_RIGHT);

			final VerticalLayout vlsearchpanel = new VerticalLayout();
			
			vlsearchpanel.setSpacing(true);
			vlsearchpanel.setSizeFull();
			
			lblSearchTitle = new Label("", ContentMode.HTML);
			lblSearchTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName+ "</b>&nbsp;::&nbsp;Search");
			
			vlSearchPanel.addComponent(PanelGenerator.createPanel(searchcomponetHl));
			vlSearchPanel.setMargin(true);

			/* Search Components end */
			
			/*----------Table Components starts-------*/
			btnSave = new Button("Save", this);
			btnCancel = new Button("Cancel", this);
			
			btnSave.setStyleName("savebt");
			btnCancel.addStyleName("cancelbt");
			btnSave.setVisible(true);
			
			
			hlSaveandCancelButtonLayout = new HorizontalLayout();
			hlSaveandCancelButtonLayout.addComponent(btnSave);
			hlSaveandCancelButtonLayout.addComponent(btnCancel);
			hlSaveandCancelButtonLayout.setComponentAlignment(btnSave, Alignment.MIDDLE_RIGHT);
			hlSaveandCancelButtonLayout.setComponentAlignment(btnCancel, Alignment.MIDDLE_RIGHT);
			hlSaveandCancelButtonLayout.setSpacing(true);
			hlSaveandCancelButtonLayout.setVisible(false);
			
			lblNotification = new Label();
			lblNotification.setContentMode(ContentMode.HTML);
			
			lblNotificationIcon = new Label();
		
			lblTableTitle = new Label("", ContentMode.HTML);
			lblTableTitle.setValue("<B>&nbsp;&nbsp;Action:</B>");

			cbFileFormat = new ComboBox();
			cbFileFormat.addItem(Common.xlsx);
			cbFileFormat.addItem(Common.pdf);
			cbFileFormat.addItem(Common.csv);
			cbFileFormat.setNullSelectionAllowed(false);
			cbFileFormat.setValue(Common.xlsx);
			cbFileFormat.setHeight("23px");
			
			
			btnEdit = new Button("Edit", this);
			
			btnAdd = new Button("Add", this);
			

			btnDownload = new Button("Download", this);
			btnDownload.addStyleName("downloadbt");
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

			
			
			btnHome=new Button("Home",this);
			btnHome.setStyleName("homebtn");
			btnHome.setEnabled(false);
		
			btnEdit.addStyleName("editbt");
			btnAdd.addStyleName("add");
		
			
			btnAdd.setEnabled(true);
			btnEdit.setEnabled(false);
			
			btnAuditrRecords=new Button("Audit History",this);
			btnAuditrRecords.setStyleName("hostorybtn");
		
			
			
		 hlFileDownloadLayout = new HorizontalLayout();
			hlFileDownloadLayout.setSpacing(true);
			hlFileDownloadLayout.addComponent(btnDownload);
			hlFileDownloadLayout.setComponentAlignment(btnDownload,Alignment.MIDDLE_CENTER);
		
			HorizontalLayout hlTableTitle = new HorizontalLayout();
			hlTableTitle.addComponent(btnHome);
			hlTableTitle.addComponent(btnAdd);
			hlTableTitle.addComponent(btnEdit);
			hlTableTitle.addComponent(btnAuditrRecords);
			hlTableTitle.setHeight("25px");
			
			HorizontalLayout hlTableTitleandCaptionLayout = new HorizontalLayout();
			hlTableTitleandCaptionLayout.addStyleName("topbarthree");
			hlTableTitleandCaptionLayout.setWidth("100%");
			hlTableTitleandCaptionLayout.addComponent(hlTableTitle);
			hlTableTitleandCaptionLayout.addComponent(hlFileDownloadLayout);
			hlTableTitleandCaptionLayout.setComponentAlignment(hlFileDownloadLayout,Alignment.MIDDLE_RIGHT);
			hlTableTitleandCaptionLayout.setHeight("28px");

			hlAddEdit = new HorizontalLayout();
			hlAddEdit.addStyleName("topbarthree");
			hlAddEdit.setWidth("100%");
			hlAddEdit.addComponent(hlTableTitleandCaptionLayout);
			hlAddEdit.setHeight("28px");
			

			lblNoofRecords = new Label(" ", ContentMode.HTML);
			lblNoofRecords.addStyleName("lblfooter");
			
			
			tblCommon = new Table();
			tblCommon.setSizeFull();
			tblCommon.setStyleName(Runo.TABLE_SMALL);
			tblCommon.setPageLength(12);
			tblCommon.setImmediate(true);
			tblCommon.setFooterVisible(true);
			tblCommon.setSelectable(true);
			tblCommon.setColumnCollapsingAllowed(true);
			
			vlTableLayout = new VerticalLayout();
			
			vlTableLayout.setSizeFull();
			vlTableLayout.setMargin(true);
			vlTableLayout.addComponent(hlAddEdit);
			vlTableLayout.addComponent(tblCommon);
			//vlTableLayout.addComponent(vlAudit);
			vltablePanel.addComponent(vlTableLayout);
			
			/*----------Table Components Ends-------*/
			
			cbBankName=new ComboBox("Bank Name");
			cbBankName.setInputPrompt(ApplicationConstants.selectDefault);
			cbBankName.setItemCaptionPropertyId("bankName");

			loadBankName();
			
			 cbBankName.addValueChangeListener(new Property.ValueChangeListener() {
				 private static final long serialVersionUID = 1L;

					public void valueChange(ValueChangeEvent event) {

						final Object itemId = event.getProperty().getValue();
						if (itemId != null) {
							final BeanItem<?> item = (BeanItem<?>) cbBankName.getItem(itemId);
							selectBank = (MPemCmBank) item.getBean();
							searchBankId = selectBank.getBankId()	;
							
							/*maxPrimaryId = servicebeanCommonSetup.getMaxOfPrimaryId(searchBankId,null);
							System.out.println("primary id is--------------------->"+maxPrimaryId);
							commonList = servicebeanCommonSetup.getCommonSetupList(null, searchBankId, null, maxPrimaryId);
							System.out.println("commonList is--------------------->"+commonList);
							for(CmCommonSetup list:commonList)
							{
								endValue = list.getEndValue();
								System.out.println("endValue is--------------------->"+endValue);
							}*/
							
							
							}
					}
				});
			 cbBankName.setImmediate(true);
			 cbBankName.setNullSelectionAllowed(false);
			 cbBankName.setWidth("200");
			
			cbStatus=new ComboBox("Status");
			cbStatus.setItemCaptionPropertyId("desc");
			cbStatus.setInputPrompt(ApplicationConstants.selectDefault);
			
			beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class);
			beanStatus.addAll(Common.listStatus);
			cbStatus.setContainerDataSource(beanStatus);
			cbStatus.setImmediate(true);
			cbStatus.setNullSelectionAllowed(false);
			cbStatus.setWidth("75");
			
			/*-------------------------for start and end value validation-----------------------*/
			
			
			tfStartValue = new TextField("Start Value");
			tfStartValue.setInputPrompt("Enter Start Value");
			tfStartValue.setWidth("100");
			tfStartValue.setImmediate(true);
			tfStartValue.addValidator(new DoubleValidator("Enter number only"));
			tfStartValue.addValueChangeListener(new Property.ValueChangeListener() {

				@SuppressWarnings("deprecation")
				public void valueChange(ValueChangeEvent event) {
					try{
						tfStartValue.setComponentError(null);
					 startValue = new Double(tfStartValue.getValue());
					 System.out.println("start value is---------------------------------------------------->"+startValue);
					 
				if(PrimaryId == null)
				{
					getMaxPrimaryIdAndEndValueduringSave();
					conditionForSave();
				}else if(PrimaryId != null)
				{
					System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
					
					getMaxPrimaryIdAndEndValueduringEdit();
				//	getMinPrimaryIdAndStartValueDuringEdit();
					conditionForSave();
					
				}
				
					
					}catch(Exception e){
						
					}
					/* if(maxPrimaryId==null)
					 {
						 System.out.println("maxPrimaryId maxPrimaryId maxPrimaryId==============================");
						 tfStartValue.setValue(tfStartValue.getValue());
					 }else if(startValue > endValue)
					{
							 	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
								tfStartValue.setValue(tfStartValue.getValue());
								btnSave.setEnabled(true);
					}else
					{
								tfStartValue.setComponentError(new UserError("Start value should be greater than end value"));
								btnSave.setEnabled(false);
						 
					 }*/
				}
									
				}
			);
			
			tfEndValue = new TextField("End Value");
			tfEndValue.setInputPrompt("Enter End Value");
			tfEndValue.setWidth("100");
			tfEndValue.setImmediate(true);
			tfEndValue.addValidator(new DoubleValidator("Enter number only"));
			tfEndValue.addValueChangeListener(new Property.ValueChangeListener() {

				public void valueChange(ValueChangeEvent event) {
					
					try{
					
					 endingValue = new Double(tfEndValue.getValue());
					System.out.println("endingValue is------------------------------->"+endingValue);
					
					if(endingValue > startValue)
					{
						tfEndValue.setValue(tfEndValue.getValue());
						btnSave.setEnabled(true);
						if(PrimaryId != null)
						{
							getMinPrimaryIdAndStartValueDuringEdit();
							System.out.println("llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll");
							conditionForEdit();
						}
					}else
					{
						tfEndValue.setComponentError(new UserError("Start value should be less than end value"));
						btnSave.setEnabled(false);
					}
					}catch(NullPointerException e)
					{
						e.printStackTrace();
					}
					
					
				}});
			
			
			tfCommPercent = new TextField("Common Percent");
			tfCommPercent.setInputPrompt("Enter Percentage");
			tfCommPercent.setWidth("100");
			
			FormLayout fl1 = new FormLayout();
			FormLayout fl2 = new FormLayout();
			FormLayout fl3 = new FormLayout();
			FormLayout fl4 = new FormLayout();
			FormLayout fl5 = new FormLayout();
			
			 
			 fl1.addComponent(cbBankName);
			 fl2.addComponent(tfStartValue);
			 fl3.addComponent(tfEndValue);
			 fl4.addComponent(tfCommPercent);
			 fl5.addComponent(cbStatus);
			 
			 HorizontalLayout maincomponetHl = new HorizontalLayout();
			 maincomponetHl.setSizeFull();
			 maincomponetHl.setSpacing(true);
			 maincomponetHl.setMargin(true);
			 maincomponetHl.addComponent(fl1);
			 maincomponetHl.addComponent(fl2);
			 maincomponetHl.addComponent(fl3);
			 maincomponetHl.addComponent(fl4);
			 maincomponetHl.addComponent(fl5);
			/* 
			 HorizontalLayout glForm1 = new HorizontalLayout();
				glForm1.setSpacing(true);
				glForm1.setMargin(true);
				glForm1.addComponent(maincomponetHl);
				glForm1.setSizeFull();*/
			 
			 GridLayout addgrid = new GridLayout();
				addgrid.addComponent( maincomponetHl);
				addgrid.setMargin(true);
				
				lblFormTitle = new Label("", ContentMode.HTML);
				lblFormTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName
						+ "</b>&nbsp;::&nbsp;Search");
				
				vlmainpanel1.addComponent(PanelGenerator.createPanel(addgrid ));
				vlmainpanel1.setMargin(true);
			
			clMainLayout.addComponent(vlmainpanel1);
			clMainLayout.addComponent(vlSearchPanel);
			clMainLayout.addComponent(vltablePanel);
			
			vlmainpanel1.setVisible(false);
			vlmainpanel1.setEnabled(true);
			vlSearchPanel.setEnabled(true);
			HorizontalLayout hlNotification = new HorizontalLayout();
			hlNotification.addComponent(lblNotificationIcon);
			hlNotification.setComponentAlignment(lblNotificationIcon,
					Alignment.MIDDLE_CENTER);
			hlNotification.addComponent(lblNotification);
			hlNotification.setComponentAlignment(lblNotification,
					Alignment.MIDDLE_LEFT);
			hlScreenNameLayout.addComponent(lblFormTitle);
			hlScreenNameLayout.setComponentAlignment(lblFormTitle, Alignment.MIDDLE_LEFT);
			hlScreenNameLayout.addComponent(hlNotification);
			hlScreenNameLayout.setComponentAlignment(hlNotification, Alignment.MIDDLE_LEFT);
			hlScreenNameLayout.addComponent(hlSaveandCancelButtonLayout);
			hlScreenNameLayout.setComponentAlignment(hlSaveandCancelButtonLayout, Alignment.MIDDLE_RIGHT);

			populateAndConfig(false);
			
			excelexporter.setTableToBeExported(tblCommon);
			csvexporter.setTableToBeExported(tblCommon);
			pdfexporter.setTableToBeExported(tblCommon);
			excelexporter.setCaption("Microsoft Excel (XLS)");
			excelexporter.setStyleName("borderless");
			csvexporter.setCaption("Comma Dilimited (CSV)");
			csvexporter.setStyleName("borderless");
			pdfexporter.setCaption("Acrobat Document (PDF)");
			pdfexporter.setStyleName("borderless");
			
			setTableProperties();
			
		}
		
		private void buildNotifications(ClickEvent event) {
			notifications = new Window();
			VerticalLayout l = new VerticalLayout();
			l.setMargin(true);
			l.setSpacing(true);
			notifications.setWidth("175px");
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
			//vlDownload.setSpacing(false);

			notifications.setContent(vlDownload);
			
			
		}
		

		
		private void loadBankName()
		{
			List<MPemCmBank> bankList = servicebeanBank.getBankDtlsList(companyId,"Active",null);
			beanBank = new BeanItemContainer<MPemCmBank>(MPemCmBank.class);
			beanBank.addAll(bankList);
			cbBankName.setContainerDataSource(beanBank);
		}
		
		private void loadSearchBankName()
		{
			List<MPemCmBank> bankList = servicebeanBank.getBankDtlsList(companyId,"Active",null);
			beanBank = new BeanItemContainer<MPemCmBank>(MPemCmBank.class);
			beanBank.addAll(bankList);
			cbSearchBankName.setContainerDataSource(beanBank);
		}
		
		private void editCommonSetupDetails() {
			// TODO Auto-generated method stub
             btnSave.setCaption("Update");
             
		   	 Item select=tblCommon.getItem(tblCommon.getValue());
		   	 if (select != null)
		   	 {
		   		CommissionSetupDM editCommonlist=beanCommonSetup.getItem(tblCommon.getValue()).getBean();
		   		 
		   		 PrimaryId = editCommonlist.getCommonId();
		   		 
		   		cmSetupid	= select.getItemProperty("commonId").getValue().toString();
		   		
		   		tfCommPercent.setValue(select.getItemProperty("commPercent").getValue().toString());
		   		
		   		tfStartValue.setValue(select.getItemProperty("startValue").getValue().toString());
		   		
		   		tfEndValue.setValue(select.getItemProperty("endValue").getValue().toString());
		   		 
		   		String gtCode = select.getItemProperty("commStatus").getValue().toString();
		   		cbStatus.setValue(Common.getStatus(gtCode)); 
		   		
		   	
		   		
		   		
		   		MPemCmBank editGetBranch=editCommonlist.getBankId();
		   		
		   		 editBankId = editGetBranch.getBankId();
		   		System.out.println("/n/n/noooooooooooooooooooooooooooooooo"+editBankId);
					Collection<?> collBranch=cbBankName.getItemIds();
					for(Iterator iterator=collBranch.iterator(); iterator.hasNext();) {
						Object itemid=(Object)iterator.next();
						BeanItem<?> item=(BeanItem<?>) cbBankName.getItem(itemid);
						MPemCmBank editBranchBean=(MPemCmBank) item.getBean();	
						if (editGetBranch != null && editGetBranch.getBankId().equals(editBranchBean.getBankId())) {
							cbBankName.setReadOnly(false);
							cbBankName.setValue(itemid);
							cbBankName.setReadOnly(true);
							
							break;
						} else {
							cbBankName.setValue(null);
						}
						
						 maxPrimaryId = servicebeanCommonSetup.getMaxOfPrimaryId(editBankId, PrimaryId);
						 
					
			
		}}}

		private void resetFields() {
			// TODO Auto-generated method stub
			vlmainpanel1.setEnabled(true);
			vlSearchPanel.setEnabled(true);
			cbBankName.setReadOnly(false);
			cbBankName.setValue(null);
			cbStatus.setValue(Common.getStatus(Common.ACTIVE_CODE));
			tfCommPercent.setValue("0");
			tfEndValue.setValue("0.0");
			tfStartValue.setValue("0.0");
			
			lblNotification.setValue("");
			lblNotificationIcon.setIcon(null);
			
			cbBankName.setComponentError(null);
			tfStartValue.setComponentError(null);
			tfEndValue.setComponentError(null);
			cbStatus.setComponentError(null);
			
		}

		private void saveCommonSetupDetails() {
			// TODO Auto-generated method stub
			try{
			if(tblCommon.getValue()!=null)
			{
				CommissionSetupDM editCommonSetup = beanCommonSetup.getItem(tblCommon.getValue()).getBean();
				
				if(cbBankName.getValue()!=null)
				{
				editCommonSetup.setBankId(selectBank);
				}
				editCommonSetup.setCommPercent(new Double(tfCommPercent.getValue()));
				editCommonSetup.setStartValue(new Double(tfStartValue.getValue()));
				editCommonSetup.setEndValue(new Double(tfEndValue.getValue()));
				editCommonSetup.setCompanyId(companyId);
				StatusDM sts = (StatusDM) cbStatus.getValue();
				if(cbStatus.getValue()!=null)
				{
				editCommonSetup.setCommStatus(sts.getCode());
				}
				editCommonSetup.setLastUpdatedBy(strLoginUserName);
				editCommonSetup.setLastUpdatedDt(DateUtils.getcurrentdate());
				
				if((cbBankName.isValid()) && (cbStatus.isValid()) && (tfStartValue.isValid()) && (tfEndValue.isValid()))
				{
				servicebeanCommonSetup.saveOrUpdateCommonSetUp(editCommonSetup);
				populateAndConfig(false);
				 resetFields();
	   			 btnSave.setCaption("Save");
	   			 lblNotification.setValue("Successfully Updated");
	   			 lblNotificationIcon.setIcon(new ThemeResource("img/success_small.png"));
	   		 }
	   		 else {
	   			 btnSave.setComponentError(new UserError("Form is not valid"));
	   			 lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
	   			 lblNotification.setValue("Update failed, please check the data and try again ");
	   		 }
			}
			else
			{
			
				CommissionSetupDM saveCommonSetup = new CommissionSetupDM();
			
			if(cbBankName.getValue()!=null)
			{
			saveCommonSetup.setBankId(selectBank);
			}
			saveCommonSetup.setCommPercent(new Double(tfCommPercent.getValue()));
			saveCommonSetup.setStartValue(new Double(tfStartValue.getValue()));
			saveCommonSetup.setEndValue(new Double(tfEndValue.getValue()));
			saveCommonSetup.setCompanyId(companyId);
			
			StatusDM sts = (StatusDM) cbStatus.getValue();
			if(cbStatus.getValue()!=null)
			{
			saveCommonSetup.setCommStatus(sts.getCode());
			}
			saveCommonSetup.setLastUpdatedBy(strLoginUserName);
			saveCommonSetup.setLastUpdatedDt(DateUtils.getcurrentdate());
			
			if((cbBankName.isValid()) && (cbStatus.isValid()) && (tfStartValue.isValid()) && (tfEndValue.isValid()))
			{
			servicebeanCommonSetup.saveOrUpdateCommonSetUp(saveCommonSetup);
			populateAndConfig(false);
			 resetFields();
   			 btnSave.setCaption("Save");
   			 lblNotification.setValue("Successfully Saved");
   			 lblNotificationIcon.setIcon(new ThemeResource("img/success_small.png"));
   		 }
   		 else {
   			 btnSave.setComponentError(new UserError("Form is not valid"));
   			 lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
   			 lblNotification.setValue("Update failed, please check the data and try again ");
   		 }
			
		}
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		private void saveComponenterror() {
			// TODO Auto-generated method stub
			btnAdd.setComponentError(null);
			btnEdit.setComponentError(null);
			btnSave.setComponentError(null);
			btnCancel.setComponentError(null);
			
		}

		private void resetSearchFields() {
			// TODO Auto-generated method stub
			cbSearchBankName.setValue(null);
			cbSearchStatus.setValue(null);
			
		}
		private void setTableProperties() {
		

			tblCommon.addGeneratedColumn("startValue", new CurrencyColumnGenerator());
			tblCommon.addGeneratedColumn("endValue", new CurrencyColumnGenerator());
			tblCommon.setColumnAlignment("startValue", Align.RIGHT);
			tblCommon.setColumnAlignment("endValue", Align.RIGHT);
			tblCommon.setColumnAlignment("commPercent", Align.RIGHT);
		}

		private void populateAndConfig(boolean search) {
			// TODO Auto-generated method stub
			try{
		//	tblCommon.removeAllItems();
			List<CommissionSetupDM> list = null;
			if(search)
			{
				list = new ArrayList<CommissionSetupDM>();
				String strStatus = null;
				StatusDM searchStatus= (StatusDM) cbSearchStatus.getValue();
	    		try {
	    			strStatus = searchStatus.getCode();
				} catch (Exception e) {
				//	log.info("status is empty on search");
					e.printStackTrace();
				}
	    		if (searchBankId!= null || strStatus != null || companyId!=null ) {
	    			list = servicebeanCommonSetup.getCommonSetupList(strStatus, searchBankId, companyId,null,orderby);
	    			total = list.size();
				}
				if (total == 0) {
					lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
					lblNotification.setValue("No Records found");
				} else {
					lblNotificationIcon.setIcon(null);
					lblNotification.setValue("");
				}
				 
			}
			else
			{
			list = servicebeanCommonSetup.getCommonSetupList(null, null, companyId,null,orderby);
			total = list.size();
			}
			lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
			beanCommonSetup = new BeanItemContainer<CommissionSetupDM>(CommissionSetupDM.class);
			beanCommonSetup.addAll(list);
			tblCommon.setContainerDataSource(beanCommonSetup);
			tblCommon.setVisibleColumns("commonId","bankName","startValue","endValue","commPercent","commStatus","lastUpdatedDt","lastUpdatedBy");
			tblCommon.
			setColumnHeaders("Ref.Id","Bank Name","Start Value","End Value","Comm.Percent","Status","Last Updated Date","Last Updated By");
			tblCommon.setColumnFooter("lastUpdatedBy","No. of Records:"+total);
			tblCommon.addItemClickListener(new ItemClickListener() {
					private static final long serialVersionUID = 1L;
						public void itemClick(ItemClickEvent event) {
		    				// TODO Auto-generated method stub
		    				if (tblCommon.isSelected(event.getItemId())) {
		    					btnAdd.setEnabled(true);
		    					btnEdit.setEnabled(false);	
		    				} else {
		    					btnAdd.setEnabled(false);
		    					btnEdit.setEnabled(true);	
		    				}
		    			//	resetFields();			
		    			}});
			}catch(NullPointerException e)
			{
				e.printStackTrace();
			}
			getExportTableDetails();
		}
		
		private void conditionForSave()
		{
			 if(maxPrimaryId==null)
			 {
				 System.out.println("maxPrimaryId maxPrimaryId maxPrimaryId==============================");
				 tfStartValue.setValue(tfStartValue.getValue());
			 }else if(startValue > endValue)
			{
					 	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						tfStartValue.setValue(tfStartValue.getValue());
						btnSave.setEnabled(true);
			}else
			{
						tfStartValue.setComponentError(new UserError("Start value should be less than end value"));
						btnSave.setEnabled(false);
				 
			 }
		}
		
		private void conditionForEdit()
		{
			
			 if(endingValue < nextStartValue)
			{
					 	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						tfEndValue.setValue(tfEndValue.getValue());
						btnSave.setEnabled(true);
			}else
			{
						tfEndValue.setComponentError(new UserError("Start value should be less than end value"));
						btnSave.setEnabled(false);
				 
			 }
			
			
				
			
		}
		
		private void getMaxPrimaryIdAndEndValueduringSave()
		{
			maxPrimaryId = servicebeanCommonSetup.getMaxOfPrimaryId(searchBankId,null);
			System.out.println("primary id is--------------------->"+maxPrimaryId);
			commonList = servicebeanCommonSetup.getCommonSetupList(null, searchBankId, null, maxPrimaryId,orderby);
			System.out.println("commonList is--------------------->"+commonList);
			for(CommissionSetupDM list:commonList)
			{
				endValue = list.getEndValue();
				System.out.println("endValue is--------------------->"+endValue);
			}
		}
		
		private void getMaxPrimaryIdAndEndValueduringEdit()
		{
			maxPrimaryId = servicebeanCommonSetup.getMaxOfPrimaryId(editBankId,PrimaryId);
			System.out.println("primary id is--------------------->"+maxPrimaryId);
			commonList = servicebeanCommonSetup.getCommonSetupList(null, editBankId, null, maxPrimaryId,orderby);
			System.out.println("commonList is--------------------->"+commonList);
			for(CommissionSetupDM list:commonList)
			{
				endValue = list.getEndValue();
				System.out.println("endValue is--------------------->"+endValue);
			}
		}
		
		private void getMinPrimaryIdAndStartValueDuringEdit()
		{
			minPrimaryId = servicebeanCommonSetup.getMinOfPrimaryAfterEdit(editBankId, PrimaryId);
			commonList = servicebeanCommonSetup.getCommonSetupList(null, editBankId, null, minPrimaryId,orderby);
			System.out.println("commonList is--------------------->"+commonList);
			for(CommissionSetupDM list:commonList)
			{
				nextStartValue = list.getStartValue();
				System.out.println("endValue is--------------------->"+endValue);
			}
		}
		private void getExportTableDetails()
		{
			excelexporter.setTableToBeExported(tblCommon);
			csvexporter.setTableToBeExported(tblCommon);
			pdfexporter.setTableToBeExported(tblCommon);
			excelexporter.setCaption("Microsoft Excel (XLS)");
			excelexporter.setStyleName("borderless");
			csvexporter.setCaption("Comma Dilimited (CSV)");
			csvexporter.setStyleName("borderless");
			pdfexporter.setCaption("Acrobat Document (PDF)");
			pdfexporter.setStyleName("borderless");
			
		}

		@Override
		public void buttonClick(ClickEvent event) {
			notifications.close();
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
			if(btnAdd == event.getButton())
			{	
				btnHome.setEnabled(true);
				btnAdd.setEnabled(false);
				btnEdit.setEnabled(false);
				btnAuditrRecords.setEnabled(true);
				resetFields();
				
				vlSearchPanel.setVisible(false);
				vlmainpanel1.setVisible(true);
				hlSaveandCancelButtonLayout.setVisible(true);
				btnAdd.setEnabled(false);
				
				tblCommon.setPageLength(14);	
				btnSave.setVisible(true);
				btnCancel.setVisible(true);
				
				lblFormTitle.setValue("&nbsp;&nbsp;<b>"+ strScreenName+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Search</font> &nbsp;>&nbsp;Add New");
				
				
			}
			
			else if(btnEdit == event.getButton())
			{
				resetFields();
				lblFormTitle.setValue("&nbsp;&nbsp;<b>"+ strScreenName+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Search</font> &nbsp;>&nbsp;Modify");
				
				vlSearchPanel.setVisible(false);
				vlmainpanel1.setVisible(true);
				btnEdit.setEnabled(true);
				tblCommon.setPageLength(14);	
				hlSaveandCancelButtonLayout.setVisible(true);
				
				btnSave.setVisible(true);
				btnCancel.setVisible(true);
				lblFormTitle.setValue("&nbsp;&nbsp;<b>"+ strScreenName+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Search</font> &nbsp;>&nbsp;Modify");
				try {
					editCommonSetupDetails();
				} catch(Exception e) {
					log.error("Error thorws in editBranchDetails() function--->" + e);
					e.printStackTrace();
				}
				btnSave.setCaption("Update");
				
				
			}
			
			else if(btnCancel == event.getButton())
			{
				vlSearchPanel.setVisible(true);
				vlmainpanel1.setVisible(false);
				hlSaveandCancelButtonLayout.setVisible(false);
				btnSave.setVisible(false);
				btnCancel.setVisible(false);
				populateAndConfig(false);
				 resetFields();
				btnAdd.setEnabled(true);
				btnEdit.setEnabled(false);
				tblCommon.setPageLength(12);	
			btnSave.setCaption("Save");
			lblFormTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName+ "</b>&nbsp;::&nbsp;Search");
			getExportTableDetails();
			}
			
			else if(btnSave == event.getButton())
			{
				vlmainpanel1.setVisible(true);
				vlSearchPanel.setVisible(false);
				
				saveComponenterror();
				try {
					saveCommonSetupDetails();
				}catch(Exception e){
					e.printStackTrace();
					log.info("check  saveCommonSetupDetails() function. commonseetup datas doesn't saved properly--->" + e);
				}
				
				
				btnAdd.setEnabled(true);
				btnEdit.setEnabled(false);
			}
			
			else if(btnSearch==event.getButton()) {
				populateAndConfig(true);
				if (total == 0) {
					lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
					lblNotification.setValue("No Records found");
				} else {
					lblNotificationIcon.setIcon(null);
					lblNotification.setValue("");
				}
				 hlFileDownloadLayout.removeAllComponents();
					hlFileDownloadLayout.addComponent(btnDownload);
					getExportTableDetails();
				
			}
			else if(btnReset==event.getButton()) {		
				
				populateAndConfig(false);
				resetSearchFields();
				lblNotificationIcon.setIcon(null);
				lblNotification.setValue("");
			}
			else if(btnAuditrRecords==event.getButton())
			{
			//table.setVisible(false);
			vlAudit=new VerticalLayout();
			vlAudit.removeAllComponents();
			btnHome.setEnabled(true);
			btnAdd.setEnabled(false);
			btnEdit.setEnabled(false);
			btnSave.setEnabled(false);
			btnCancel.setEnabled(false);
			vlSearchPanel.setEnabled(false);
			vlmainpanel1.setEnabled(false);
			btnAuditrRecords.setEnabled(false);
			lblNotificationIcon.setIcon(null);
			lblNotification.setValue("");
			new AuditRecordsApp(vlAudit,Common.M_PEM_CM_COMM_SETUP,cmSetupid);
			vlTableLayout.removeAllComponents();
			vlTableLayout.setSizeFull();
			vlTableLayout.setMargin(true);
			vlTableLayout.addComponent(hlAddEdit);
			vlTableLayout.addComponent(vlAudit);
			lblFormTitle.setValue("&nbsp;&nbsp;<b>"+ strScreenName+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Search</font> &nbsp;>&nbsp;Audit History");
			}
			
			else if(btnHome==event.getButton())
			{
			//table.setVisible(false);
				vlmainpanel1.setVisible(false);
				vlSearchPanel.setVisible(true);
				vltablePanel.setVisible(true);
				vlmainpanel1.setEnabled(true);
				vlSearchPanel.setEnabled(true);
			vlTableLayout.removeAllComponents();
			vlTableLayout.addComponent(hlAddEdit);
			vlTableLayout.addComponent(tblCommon);
			btnAdd.setEnabled(true);
			btnHome.setEnabled(false);
			btnAuditrRecords.setEnabled(true);
			populateAndConfig(false);
			btnSave.setEnabled(true);
			btnCancel.setEnabled(true);
				
			}
			
		}

		

	
	
}
