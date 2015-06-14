package com.gnts.pem.mst;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;





import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.Common;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateUtils;
import com.gnts.pem.domain.mst.MPemCmBankConstant;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Runo;

public class Parameter implements ClickListener {
	private static final long serialVersionUID = 1L;

	private TextField tfsearch, tfvaluesearch, tfvalueadd, tfdescadd;
	private ComboBox cbsearch, cbadd,cbParamref;
	private Table table;
	private Button searchbtn, resetbtn, addbtn, editbtn, cancelbtn, savebtn,btnAuditrRecords,btnHome;
	private Button btnBack;
	private Button btnDownload;
	  HorizontalLayout hlFileDownloadLayout;
	//Declaration for Exporter
	private Window notifications;
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();

	private VerticalLayout searchpanel = new VerticalLayout();
	private VerticalLayout tablepanel = new VerticalLayout();
	private VerticalLayout mainpanel = new VerticalLayout();
	private VerticalLayout vlTableForm,vlSearchPanel;

	private VerticalLayout vlTableLayout ,vlAudit;
	private	HorizontalLayout hlAddEdit;
	private String username,screenName,parameterid;
	private Table tblCommon;
	
	// pagination
		private int total = 0;
		// for header layoute
		private Label lblTableTitle;
		private Label lblFormTittle, lblFormTitle1, lblAddEdit;
		private Label lblSaveNotification, lblNotificationIcon;
		
		private HorizontalLayout hlButtonLayout1;
		private HorizontalLayout hlAddEditLayout, hlBreadCrumbs;
		Long companyId;

	private CmBankConstantService beanBankConst = (CmBankConstantService) SpringContextHelper
			.getBean("bankConstant");
	private BeanItemContainer<StatusDM> stausBeans;
	private BeanItemContainer<MPemCmBankConstant> beans = null;

	private Logger logger = Logger.getLogger(Parameter.class);
	public Parameter() {

		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		VerticalLayout clArgumentLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		companyId=Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		HorizontalLayout hlHeaderLayout = (HorizontalLayout) UI.getCurrent()
				.getSession().getAttribute("hlLayout");
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		buildview(clArgumentLayout,hlHeaderLayout);
		
		
	
	}

	@SuppressWarnings("deprecation")
	private void buildview(VerticalLayout p,HorizontalLayout hlHeaderLayout) {
		
		hlHeaderLayout.removeAllComponents();
		
		lblTableTitle = new Label();
		lblSaveNotification = new Label();
		lblSaveNotification.setContentMode(ContentMode.HTML);
		lblNotificationIcon = new Label();
		lblTableTitle.setValue("<B>&nbsp;&nbsp;Home:</B>");
		lblTableTitle.setContentMode(ContentMode.HTML);
		
		lblFormTittle = new Label();
		lblFormTittle.setContentMode(ContentMode.HTML);
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;Home");
		
		lblFormTitle1 = new Label();
		lblFormTitle1.setContentMode(ContentMode.HTML);
		lblFormTitle1.setValue("&nbsp;&nbsp;<b>" + screenName
				+ "</b>&nbsp;::&nbsp;");
		
		lblAddEdit = new Label();
		lblAddEdit.setContentMode(ContentMode.HTML);
		

		// search
		tfsearch = new TextField("Parameter Reference");
		tfvaluesearch = new TextField("Parameter Description");
		cbsearch = new ComboBox("Status");
		searchbtn = new Button("Search", this);
		resetbtn = new Button("Reset", this);
		cbsearch.setInputPrompt("Select");
		searchbtn.setStyleName("searchbt");
		resetbtn.setStyleName("resetbt");
		
		tfsearch.setWidth("200");
		tfvaluesearch.setWidth("200");
		
		cbsearch.setItemCaptionPropertyId("desc");
		cbsearch.setImmediate(true);
		stausBeans = new BeanItemContainer<StatusDM>(StatusDM.class);
		stausBeans.addAll(Common.listStatus);
		cbsearch.setContainerDataSource(stausBeans);
		cbsearch.setWidth("150");
	/*	cbsearch.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			public void valueChange(ValueChangeEvent event) {
				try {
					SimpleStringFilter filter = null;
					Filterable f = (Filterable) table.getContainerDataSource();

					// Remove old filter
					if (filter != null)
						// Set new filter for the "Name" column
						if (cbsearch.getValue() != null) {
							Status st = (Status) cbsearch.getValue();
							filter = new SimpleStringFilter("cnstntStatus", st
									.getCode(), true, false);
							f.addContainerFilter(filter);
						} else {
							f.removeContainerFilter(filter);
						}
					if (f.size() == 0) {
						Notification.show("No Records found");
					}

				}

				catch (Exception e) {
				}
			}
		});
*/
		//
		cbParamref = new ComboBox("Parameter Ref");
		cbParamref.setNullSelectionAllowed(false);
		cbParamref.setInputPrompt(Common.SELECT_PROMPT);
		
		loadParameterRef();
	
		tfvalueadd = new TextField("Parameter Value");
		tfdescadd = new TextField("Parameter Desc");
		cbadd = new ComboBox("Status");
		tfvalueadd.setInputPrompt("Param Value");
		tfdescadd.setInputPrompt("Param Desc");
		cbadd.setInputPrompt("Select");
		savebtn = new Button("Save", this);
		cancelbtn = new Button("Cancel", this);
		savebtn.setStyleName("savebt");
		cancelbtn.setStyleName("cancelbt");
		
		tfvalueadd.setWidth("250");
		tfdescadd.setWidth("250");
		cbadd.setItemCaptionPropertyId("desc");
		cbadd.setImmediate(true);
		cbadd.setNullSelectionAllowed(false);
		stausBeans = new BeanItemContainer<StatusDM>(StatusDM.class);
		stausBeans.addAll(Common.listStatus);
		cbadd.setContainerDataSource(stausBeans);
		cbadd.setWidth("150");
/*
		tfsearch.addListener(new TextChangeListener() {
			private static final long serialVersionUID = 1L;
			SimpleStringFilter filter = null;

			public void textChange(TextChangeEvent event) {
				Filterable f = (Filterable) table.getContainerDataSource();
				// Remove old filter
				if (filter != null)
					f.removeContainerFilter(filter);

				// Set new filter for the "Name" column
				filter = new SimpleStringFilter("constCode", event.getText(),
						true, false);
				f.addContainerFilter(filter);

				if (f.size() == 0) {
					Notification.show("No Records found");
				}

			}
		});

		tfvaluesearch.addListener(new TextChangeListener() {
			private static final long serialVersionUID = 1L;
			SimpleStringFilter filter = null;

			public void textChange(TextChangeEvent event) {
				Filterable f = (Filterable) table.getContainerDataSource();

				// Remove old filter
				if (filter != null)
					f.removeContainerFilter(filter);

				// Set new filter for the "Name" column
				filter = new SimpleStringFilter("paramdesc", event.getText(),
						true, false);
				f.addContainerFilter(filter);

				if (f.size() == 0) {
					Notification.show("No Records found");
				}

			}
		});*/

		// search panel
		

		FormLayout flSearchForm1 = new FormLayout();
		flSearchForm1.addComponent(tfsearch);
		FormLayout flSearchForm2 = new FormLayout();
		flSearchForm2.addComponent(tfvaluesearch);
		
		

		HorizontalLayout hlSearch = new HorizontalLayout();
		hlSearch.addComponent(flSearchForm1);
		hlSearch.addComponent(flSearchForm2);
		
		hlSearch.setSpacing(true);
		hlSearch.setMargin(true);
		
		//Initialization and properties for btnDownload		
		btnDownload=new Button("Download");
		//btnDownload.setDescription("Download");
		btnDownload.addStyleName("downloadbt");
		btnDownload.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
        //  UI.getCurrent()..clearDashboardButtonBadge();
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
        });
		

	
		hlFileDownloadLayout = new HorizontalLayout();
		hlFileDownloadLayout.setSpacing(true);
		hlFileDownloadLayout.addComponent(btnDownload);
		hlFileDownloadLayout.setComponentAlignment(btnDownload,Alignment.MIDDLE_CENTER);

		VerticalLayout hlSearchButtonLayout = new VerticalLayout();
		hlSearchButtonLayout.setSpacing(true);
		hlSearchButtonLayout.addComponent(searchbtn);
		hlSearchButtonLayout.addComponent(resetbtn);
		hlSearchButtonLayout.setWidth("100");
		hlSearchButtonLayout.addStyleName("topbarthree");
		hlSearchButtonLayout.setMargin(true);
		

		HorizontalLayout hlSearchPanel = new HorizontalLayout();
		hlSearchPanel.setSpacing(true);
		hlSearchPanel.addComponent(hlSearch);
		hlSearchPanel.addComponent(hlSearchButtonLayout);
		hlSearchPanel.setComponentAlignment(hlSearchButtonLayout, Alignment.MIDDLE_RIGHT);
		hlSearchPanel.setExpandRatio(hlSearchButtonLayout, 1);
		hlSearchPanel.setSizeFull();

		vlSearchPanel = new VerticalLayout();
		vlSearchPanel.setSpacing(true);
		vlSearchPanel.setSizeFull();
		vlSearchPanel.addComponent(hlSearchPanel);
		


		btnHome=new Button("Home",this);
		btnHome.setStyleName("homebtn");
		btnHome.setEnabled(false);
		
		btnAuditrRecords=new Button("Audit History",this);
		btnAuditrRecords.setStyleName("hostorybtn");
	
	
		// table panel
		
		addbtn = new Button("Add", this);
		editbtn = new Button("Edit", this);
		addbtn.setStyleName("add");
		editbtn.setStyleName("editbt");
	
		editbtn.setEnabled(false);
		btnBack = new Button("Home", this);
		btnBack.setStyleName("link");

		

		table = new Table();
		table.setSizeFull();
		table.setStyleName(Runo.TABLE_SMALL);
		table.setImmediate(true);
		table.setSelectable(true);
		table.setColumnCollapsingAllowed(true);
		table.setPageLength(14);
		table.setFooterVisible(true);

		


		HorizontalLayout hlTableTittleLayout = new HorizontalLayout();
		hlTableTittleLayout.addComponent(btnHome);
		hlTableTittleLayout.addComponent(addbtn);
		hlTableTittleLayout.addComponent(editbtn);
		hlTableTittleLayout.addComponent(btnAuditrRecords);
		
		HorizontalLayout hlTableTitleandCaptionLayout = new HorizontalLayout();
		hlTableTitleandCaptionLayout.addStyleName("topbarthree");
		hlTableTitleandCaptionLayout.setWidth("100%");
		hlTableTitleandCaptionLayout.addComponent(hlTableTittleLayout);
		hlTableTitleandCaptionLayout.addComponent(hlFileDownloadLayout);
		hlTableTitleandCaptionLayout.setComponentAlignment(hlFileDownloadLayout,Alignment.MIDDLE_RIGHT);
		hlTableTitleandCaptionLayout.setHeight("28px");
		
		hlAddEditLayout = new HorizontalLayout();
		hlAddEditLayout.addStyleName("topbarthree");
		hlAddEditLayout.setWidth("100%");
		hlAddEditLayout.addComponent(hlTableTitleandCaptionLayout);
		hlAddEditLayout.setHeight("30px");

		vlTableForm=new VerticalLayout();
		vlTableForm.setSizeFull();
		vlTableForm.setMargin(true);
		vlTableForm.addComponent(hlAddEditLayout);
		vlTableForm.addComponent(table);
		
		tablepanel.setStyleName(Runo.PANEL_LIGHT);
		tablepanel.addComponent(vlTableForm);
		

		// MAIN PANEL

		HorizontalLayout addlayout = new HorizontalLayout();
		addlayout.addComponent(cbParamref);
		addlayout.addComponent(tfvalueadd);
		addlayout.addComponent(tfdescadd);
		addlayout.addComponent(cbadd);
		
		addlayout.setSpacing(true);

		GridLayout addgrid = new GridLayout();
		addgrid.addComponent(addlayout);
		addgrid.setMargin(true);

		
		searchpanel.addComponent(PanelGenerator.createPanel(vlSearchPanel));
		searchpanel.setMargin(true);

	
		mainpanel.addComponent(PanelGenerator.createPanel(addgrid));
		mainpanel.setMargin(true);

		mainpanel.setEnabled(true);
		searchpanel.setEnabled(true);

		p.addComponent(searchpanel);
		p.addComponent(mainpanel);
		p.addComponent(tablepanel);
		
		
		// HeaderPanel
				hlButtonLayout1 = new HorizontalLayout();
				hlButtonLayout1.addComponent(savebtn);
				hlButtonLayout1.addComponent(cancelbtn);
				hlButtonLayout1.setVisible(false);

				hlBreadCrumbs = new HorizontalLayout();
				hlBreadCrumbs.addComponent(lblFormTitle1);
				hlBreadCrumbs.addComponent(btnBack);
				hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
				hlBreadCrumbs.addComponent(lblAddEdit);
				hlBreadCrumbs
						.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER);
				hlBreadCrumbs.setVisible(false);
		
				HorizontalLayout hlNotification = new HorizontalLayout();
				hlNotification.addComponent(lblNotificationIcon);
				hlNotification.setComponentAlignment(lblNotificationIcon,
						Alignment.MIDDLE_LEFT);
				hlNotification.addComponent(lblSaveNotification);
				hlNotification.setComponentAlignment(lblSaveNotification,
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
		

		mainpanel.setVisible(false);
		
		populateAndConfigureTableNew(false);

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
		vlDownload.setSpacing(true);

		notifications.setContent(vlDownload);

	}
	



	//Load state list for pnlsearch's combo box
	private void loadParameterRef() {
	List<String> list = beanBankConst.getParameterRefList();
	BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
			String.class);
	childAccounts.addAll(list);
	cbParamref.setContainerDataSource(childAccounts);
}


	@SuppressWarnings("deprecation")
	private void populateAndConfigureTableNew(Boolean search) {
		// TODO Auto-generated method stub
		List<MPemCmBankConstant> parameterls = new ArrayList<MPemCmBankConstant>();
		table.removeAllItems();
		if (search) {
			String name = tfsearch.getValue().toString();
			String value = tfvaluesearch.getValue().toString();
			/*Status st = (Status) cbsearch.getValue();
			String status=null;
			try {
				status = st.getCode();
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("status is empty on search");
			}
			*/
			if (name != null || value != null) {
				parameterls = beanBankConst.getSearchBankConstantList(name, value, null, companyId);
				total = parameterls.size();
			}

		/*	if (total == 0) {

				Notification.show("No Records Found");
			}*/

		}

		else {
			parameterls =beanBankConst.getSearchBankConstantList(null, null, null, companyId);
			total = parameterls.size();

		}
		beans = new BeanItemContainer<MPemCmBankConstant>(MPemCmBankConstant.class);
		beans.addAll(parameterls);
		table.setContainerDataSource(beans);
		table.setVisibleColumns(new Object[] { "constCode", "constDesc",
				"constValue", "cnstntStatus", "lastUpdatedDt", "lastUpdatedBy" });
		table.setColumnHeaders(new String[] { "Parameter Reference",
				"Parameter Description", "Parameter Value", "Status",
				"Last Updated Date", "Last Updated By" });
		table.setColumnFooter("lastUpdatedBy", "No.of Records : "
				+ total);
		table.setSelectable(true);
		table.addListener(new ItemClickListener() {
		private static final long serialVersionUID = 1L;

			public void itemClick(ItemClickEvent event) {
				try {
					// TODO Auto-generated method stub
					if (table.isSelected(event.getItemId())) {
						editbtn.setEnabled(false);
						addbtn.setEnabled(true);

					} else {
						editbtn.setEnabled(true);
						addbtn.setEnabled(false);
					}
					resetFields();
					savebtn.setCaption("Save");
				} catch (Exception e) {

				}
			}
		});
getExportTableDetails();
	}

	public void saveParameterDetails() {

		try {
			boolean valid = false;

			if (table.getValue() != null) {
	           MPemCmBankConstant editBankConstant = beans.getItem(table.getValue()).getBean();
				editBankConstant.setConstCode(cbParamref.getValue().toString());
				editBankConstant.setConstValue(tfvalueadd.getValue().toString());
				editBankConstant.setConstDesc(tfdescadd.getValue().toString());
				StatusDM st = (StatusDM) cbadd.getValue();
				editBankConstant.setCnstntStatus(st.getCode());
				editBankConstant.setLastUpdatedDt(DateUtils.getcurrentdate());
				editBankConstant.setLastUpdatedBy(username);

				if (cbParamref.isValid() & tfvalueadd.isValid()
						& tfdescadd.isValid()) {
					beanBankConst.saveOrUpdateBankConstant(editBankConstant);
					
					lblNotificationIcon.setIcon(new ThemeResource(
							"img/success_small.png"));
					lblSaveNotification.setValue("Successfully Updated");
					valid = true;
				}

			} else {
				MPemCmBankConstant saveBankConstant = new MPemCmBankConstant();
				saveBankConstant.setConstCode(cbParamref.getValue().toString());
				saveBankConstant.setConstValue(tfvalueadd.getValue().toString());
				saveBankConstant.setConstDesc(tfdescadd.getValue().toString());
				StatusDM st = (StatusDM) cbadd.getValue();
				saveBankConstant.setCnstntStatus(st.getCode());
				saveBankConstant.setLastUpdatedDt(DateUtils.getcurrentdate());
				saveBankConstant.setLastUpdatedBy(username);


				if (cbParamref.isValid() & tfvalueadd.isValid()
						& tfdescadd.isValid()) {
					beanBankConst.saveOrUpdateBankConstant(saveBankConstant);
				
					lblNotificationIcon.setIcon(new ThemeResource(
							"img/success_small.png"));
					lblSaveNotification.setValue("Successfully Saved");
					valid = true;
				}
			}
			if (valid) {
				populateAndConfigureTableNew(false);
				savebtn.setCaption("Save");
				resetFields();
			} else {
				lblNotificationIcon
				.setIcon(new ThemeResource("img/failure.png"));
				lblSaveNotification
				.setValue("Saved failed, please check the data and try again ");

			}
		} catch (Exception e) {
			logger.info("Error-->" + e);

		}
	}

	private void editParameter() {
		savebtn.setComponentError(null);
		
		try {
			mainpanel.setVisible(true);
			Item itselect = table.getItem(table.getValue());
			if (itselect != null) {
				parameterid	= itselect.getItemProperty("constId")
						.getValue().toString();
				cbParamref.setReadOnly(false);
				cbParamref.setValue(itselect.getItemProperty("constCode").getValue()
						.toString());
				cbParamref.setReadOnly(true);
				tfvalueadd.setValue(itselect.getItemProperty("constValue")
						.getValue().toString());
				tfdescadd.setValue(itselect.getItemProperty("constDesc")
						.getValue().toString());
				String stCode = itselect.getItemProperty("cnstntStatus").getValue()
						.toString();
				cbadd.setValue(Common.getStatus(stCode));
			}
		} catch (Exception e) {
			logger.info("Error-->" + e);
		}

	}

	private void getExportTableDetails()
	{
		excelexporter.setTableToBeExported(table);
		csvexporter.setTableToBeExported(table);
		pdfexporter.setTableToBeExported(table);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
		
	}

	void resetFields() {
		mainpanel.setEnabled(true);
		searchpanel.setEnabled(true);
		cbParamref.setReadOnly(false);
		cbParamref.setValue(null);
		
		tfvalueadd.setValue("");
		tfdescadd.setValue("");
		cbParamref.setComponentError(null);
		tfvalueadd.setComponentError(null);
		tfdescadd.setComponentError(null);
		cbadd.setValue(Common.getStatus(Common.ACTIVE_CODE));
		savebtn.setCaption("save");
		savebtn.setComponentError(null);
	
	}
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (addbtn == event.getButton()) {
			
			btnHome.setEnabled(true);
			addbtn.setEnabled(false);
			editbtn.setEnabled(false);
			btnAuditrRecords.setEnabled(true);
			resetFields();
			searchpanel.setVisible(false);
			mainpanel.setVisible(true);
			hlButtonLayout1.setVisible(true);
			editbtn.setEnabled(false);
			addbtn.setEnabled(false);
			lblAddEdit.setValue("&nbsp;>&nbsp;Add new");
			lblAddEdit.setVisible(true);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
			

		} else if (cancelbtn == event.getButton()) {
			mainpanel.setVisible(false);
			searchpanel.setVisible(true);
			addbtn.setEnabled(true);
			hlButtonLayout1.setVisible(false);
			btnHome.setEnabled(false);
			resetFields();
			table.setValue(null);
			hlButtonLayout1.setVisible(false);
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Home");
			lblAddEdit.setVisible(false);
			lblFormTittle.setVisible(true);
			hlBreadCrumbs.setVisible(false);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
			hlBreadCrumbs.setVisible(false);
			lblFormTittle.setVisible(true);
			 hlFileDownloadLayout.removeAllComponents();
				hlFileDownloadLayout.addComponent(btnDownload);
				getExportTableDetails();
		
		}
		 else if (btnBack == event.getButton()) {
			 	mainpanel.setVisible(false);
				searchpanel.setVisible(true);
				addbtn.setEnabled(true);
				hlButtonLayout1.setVisible(false);
				btnHome.setEnabled(false);
				resetFields();
				table.setValue(null);
				hlButtonLayout1.setVisible(false);
				lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
						+ "</b>&nbsp;::&nbsp;Home");
				lblAddEdit.setVisible(false);
				lblFormTittle.setVisible(true);
				hlBreadCrumbs.setVisible(false);
				lblNotificationIcon.setIcon(null);
				lblSaveNotification.setValue("");
				hlBreadCrumbs.setVisible(false);
				lblFormTittle.setVisible(true);
		 }else
		if (savebtn == event.getButton()) {
			try {
				saveParameterDetails();
			} catch (Exception e) {
				logger.info("Error-->" + e);

			}
			addbtn.setEnabled(true);
			editbtn.setEnabled(false);
		}
		if (editbtn == event.getButton()) {
			searchpanel.setVisible(false);
			mainpanel.setVisible(true);
			editbtn.setEnabled(false);
			addbtn.setEnabled(false);
			btnHome.setEnabled(true);
			resetFields();
			hlButtonLayout1.setVisible(true);
			savebtn.setCaption("Update");
		
			editParameter();
			
			lblAddEdit.setValue("&nbsp;>&nbsp;Modify");
			lblAddEdit.setVisible(true);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			lblSaveNotification.setValue("");
			lblNotificationIcon.setIcon(null);
		}
		if (searchbtn == event.getButton()) {
			populateAndConfigureTableNew(true);
			if (total == 0) {
				lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
				lblSaveNotification.setValue("No Records found");
			} else {
				lblNotificationIcon.setIcon(null);
				lblSaveNotification.setValue("");
			}
			 hlFileDownloadLayout.removeAllComponents();
				hlFileDownloadLayout.addComponent(btnDownload);
				getExportTableDetails();
		
		}
		if (resetbtn == event.getButton()) {
			tfsearch.setValue("");
			tfvaluesearch.setValue("");
			cbsearch.setValue(null);
			populateAndConfigureTableNew(false);
			searchbtn.setComponentError(null);
			resetbtn.setComponentError(null);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");

		}
		else if(btnAuditrRecords==event.getButton())
		{
		//table.setVisible(false);
		vlAudit=new VerticalLayout();
		vlAudit.removeAllComponents();
		btnHome.setEnabled(true);
		addbtn.setEnabled(false);
		editbtn.setEnabled(false);
		savebtn.setEnabled(false);
		cancelbtn.setEnabled(false);
		mainpanel.setEnabled(false);
		searchpanel.setEnabled(false);
		btnAuditrRecords.setEnabled(false);
		lblNotificationIcon.setIcon(null);
		lblSaveNotification.setValue("");
		new AuditRecordsApp(vlAudit,Common.M_PEM_CM_BANK_CONSTANT,parameterid);
		
		vlTableForm.removeAllComponents();
		vlTableForm.addComponent(hlAddEditLayout);
		vlTableForm.addComponent(vlAudit);
		lblAddEdit.setValue("&nbsp;>&nbsp;Audit History");
		lblAddEdit.setVisible(true);
		lblFormTittle.setVisible(false);
		hlBreadCrumbs.setVisible(true);
		}
		
		else if(btnHome==event.getButton())
		{
		//table.setVisible(false);
			mainpanel.setVisible(false);
			searchpanel.setVisible(true);
			tablepanel.setVisible(true);
			mainpanel.setEnabled(true);
			searchpanel.setEnabled(true);
			vlTableForm.removeAllComponents();
			vlTableForm.addComponent(hlAddEditLayout);
			vlTableForm.addComponent(table);
			addbtn.setEnabled(true);
			btnHome.setEnabled(false);
			lblAddEdit.setVisible(false);
//			lblAddEdit.setValue(null);
//			lblAddEdit.setValue("&nbsp;&nbsp;<b>" + screenName
//					+ "</b>&nbsp;::&nbsp;Home");
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
					+ "</b>&nbsp;::&nbsp;Home");
			btnAuditrRecords.setEnabled(true);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
		//	lblAddEdit.setValue("&nbsp;>&nbsp;Modify");
		//	lblAddEdit.setVisible(true);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
		
			
		populateAndConfigureTableNew(false);
		savebtn.setEnabled(true);
		cancelbtn.setEnabled(true);
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
		

	}
}
