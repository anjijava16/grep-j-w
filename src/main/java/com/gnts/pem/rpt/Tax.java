package com.gnts.pem.rpt;

import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.base.service.mst.ParameterService;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.tool.Database;
import com.gnts.erputil.tool.Report;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateUtils;
import com.gnts.erputil.validations.DateValidation;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.rpt.TPemCmBillDtls;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.rpt.CmBillDtlsService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.themes.Runo;
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

public class Tax implements ClickListener {
	
	private ParameterService paramServiceBean = (ParameterService) SpringContextHelper
			.getBean("parameter");
	
	private ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext-core.xml");
	
	private VerticalLayout vlSearchPanel = new VerticalLayout();
	private VerticalLayout vltablePanel = new VerticalLayout();
	
	VerticalLayout vlTableLayout;
	
	private HorizontalLayout hlAddEdit;
	
	//Search Components
	private ComboBox cbSearchBankName;
	private PopupDateField dfStartDate;
	private PopupDateField dfEndDate;
	
	//button Components
	private Button btnSearch, btnReset,btnReport;
	private Button btnDownload;
	  HorizontalLayout hlFileDownloadLayout;
	//Declaration for Exporter
	private Window notifications=new Window();
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	
	//Table Components
	private Table tblTaxCalc;
	
	private String strLoginUserName , strScreenName;
	
	private	Long companyId,searchBankId; 
	
	private MPemCmBank selectBank; 
	
	
	
	private int total = 0;
	
	private Label lblTableTitle,lblSearchTitle,lblFormTitle,lblNoofRecords,lblNotification,lblNotificationIcon;
	
	private BeanItemContainer<MPemCmBank> beanBank = null;
	private BeanItemContainer<TPemCmBillDtls> beanBillDtls = null;
	private CmBankService servicebeanBank = (CmBankService) SpringContextHelper.getBean("bank");
	
	private CmEvalDetailsService servicebeanEvalDetails = (CmEvalDetailsService) SpringContextHelper.getBean("evalDtls");
	private CmBillDtlsService servicebeanDetailsService = (CmBillDtlsService) SpringContextHelper.getBean("billDtls");
	
	public Tax()
	{

		System.out.println("mployee App called");
		strLoginUserName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		strScreenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		VerticalLayout clMainLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		HorizontalLayout hlScreenNameLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlScreenNameLayout);
		System.out.println("Company Id is------------------------>"+companyId);
	
	}

	private void buildView(VerticalLayout clMainLayout,HorizontalLayout hlScreenNameLayout) {
		// TODO Auto-generated method stub
		clMainLayout.removeAllComponents();
		System.out.println("------------------------------------------");
		hlScreenNameLayout.removeAllComponents();
		
		lblFormTitle = new Label("", ContentMode.HTML);
		lblFormTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName
				+ "</b>&nbsp;::&nbsp;Search");
		
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

		
		
		 dfStartDate = new PopupDateField("Start Date");
		 dfStartDate.setInputPrompt(ApplicationConstants.selectDefault);
		 dfStartDate.setInputPrompt("Select Date");
		 dfStartDate.setDateFormat("dd-MMM-yyyy");
		
		 dfStartDate.addValidator(new DateValidation("Invalid date entered"));
		 dfStartDate.setImmediate(true);
		 dfStartDate.addValueChangeListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 1L;

				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					//methodforreportbutton();
				}
			});
		
		 
		 dfEndDate = new PopupDateField("End Date");
		 dfEndDate.setInputPrompt(ApplicationConstants.selectDefault);
		 dfEndDate.setInputPrompt("Select Date");
		 dfEndDate.setDateFormat("dd-MMM-yyyy");
		 
		 dfEndDate.setValue(new Date());
		 dfEndDate.setImmediate(true);
		 dfEndDate.addValidator(new DateValidation("Invalid date entered"));
		 dfEndDate.setImmediate(true);
			int days = -30;
			addDays(dfEndDate.getValue(), days);
			dfEndDate.addValueChangeListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 1L;

				public void valueChange(ValueChangeEvent event) {
				
					System.out.println("\n\n================================ Search End Date===============");
					int days = -30;
					addDays(dfEndDate.getValue(), days);

				}
			});
		 
		 FormLayout flSearchBankName = new FormLayout();

			
			FormLayout flSearchStartDate = new FormLayout();
			flSearchStartDate.addComponent(dfStartDate);
			
			FormLayout flSearchEndDate = new FormLayout();
			flSearchEndDate.addComponent(dfEndDate);
			
			FormLayout flSearchBillNo = new FormLayout();
			flSearchBillNo.addComponent(cbSearchBankName);
			
			HorizontalLayout hlSearchLayout = new HorizontalLayout();
			
			hlSearchLayout.addComponent(flSearchBillNo);
			hlSearchLayout.addComponent(flSearchStartDate);
			hlSearchLayout.addComponent(flSearchEndDate);
			hlSearchLayout.setSpacing(true);
			
			hlSearchLayout.setMargin(true);
			
			btnSearch = new Button("Search", this);
			btnSearch.setStyleName("searchbt");

			btnReset = new Button("Reset", this);
			btnReset.addStyleName("resetbt");
			
			btnReport = new Button("Open Report", this);
			btnReport.addStyleName("reportbt");
			
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
			
			VerticalLayout vlbtnSearchLayout = new VerticalLayout();
			vlbtnSearchLayout.setSpacing(true);
			vlbtnSearchLayout.addComponent(btnSearch);
			vlbtnSearchLayout.addComponent(btnReset);
			vlbtnSearchLayout.setWidth("100");
			vlbtnSearchLayout.addStyleName("topbarthree");
			vlbtnSearchLayout.setMargin(true);
			
			HorizontalLayout searchcomponetHl = new HorizontalLayout();
			searchcomponetHl.setSizeFull();
			searchcomponetHl.setSpacing(true);
			searchcomponetHl.addComponent(hlSearchLayout);
			searchcomponetHl.setComponentAlignment(hlSearchLayout, Alignment.MIDDLE_LEFT);

			searchcomponetHl.addComponent(vlbtnSearchLayout);
			searchcomponetHl.setComponentAlignment(vlbtnSearchLayout,Alignment.MIDDLE_RIGHT);
			searchcomponetHl.setExpandRatio(vlbtnSearchLayout, 1);
				
			final VerticalLayout vlsearchpanel = new VerticalLayout();
			
			vlsearchpanel.setSpacing(true);
			vlsearchpanel.setSizeFull();
			
			lblSearchTitle = new Label("", ContentMode.HTML);
			lblSearchTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName+ "</b>&nbsp;::&nbsp;Search");
			
			vlSearchPanel.addComponent(PanelGenerator.createPanel(searchcomponetHl));
			vlSearchPanel.setMargin(true);

			/* Search Components end */
			
			/*----------Table Components starts-------*/
			
			lblNotification = new Label();
			lblNotification.setContentMode(ContentMode.HTML);
			
			lblNotificationIcon = new Label();
			
			lblTableTitle = new Label("", ContentMode.HTML);
			lblTableTitle.setValue("<B>&nbsp;&nbsp;Action:</B>");
			
			lblNoofRecords = new Label(" ", ContentMode.HTML);
			lblNoofRecords.addStyleName("lblfooter");
			
			HorizontalLayout hlTableCaption = new HorizontalLayout();
			
			hlTableCaption.addComponent(lblTableTitle);
			hlTableCaption.setComponentAlignment(lblTableTitle, Alignment.MIDDLE_CENTER);
			hlTableCaption.addStyleName("lightgray");
			hlTableCaption.setHeight("25px");
			hlTableCaption.setWidth("90px");
			
			HorizontalLayout hlTableTitle = new HorizontalLayout();

			hlTableTitle.addComponent(hlTableCaption);
			hlTableTitle.addComponent(btnReport);
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
			
			
			tblTaxCalc = new Table();
			tblTaxCalc.setSizeFull();
			tblTaxCalc.setStyleName(Runo.TABLE_SMALL);
			tblTaxCalc.setPageLength(14);
			tblTaxCalc.setImmediate(true);
			tblTaxCalc.setFooterVisible(true);
			tblTaxCalc.setSelectable(true);
			tblTaxCalc.setColumnCollapsingAllowed(true);
			
			 vlTableLayout = new VerticalLayout();
			
			vlTableLayout.setSizeFull();
			vlTableLayout.setMargin(true);
			vlTableLayout.addComponent(hlAddEdit);
			vlTableLayout.addComponent(tblTaxCalc);
			
			vltablePanel.addComponent(vlTableLayout);
			
			/*----------Table Components Ends-------*/
			
			clMainLayout.addComponent(vlSearchPanel);
			clMainLayout.addComponent(vltablePanel);
			
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
		
		
			populateAndConfig(false);
			setTableProperties();
			
		
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
		//vlDownload.setSpacing(true);

		notifications.setContent(vlDownload);

	}
	


	public void setTableProperties() {
	tblTaxCalc.setColumnAlignment("billdtlId", Align.RIGHT);
	tblTaxCalc.setColumnAlignment("billNo", Align.RIGHT);
	tblTaxCalc.setColumnAlignment("stPrcnt", Align.RIGHT);
	tblTaxCalc.setColumnAlignment("stValue", Align.RIGHT);
	tblTaxCalc.setColumnAlignment("scPrcnt", Align.RIGHT);
	tblTaxCalc.setColumnAlignment("scValue", Align.RIGHT);
	tblTaxCalc.setColumnAlignment("sehcPrcnt", Align.RIGHT);
	tblTaxCalc.setColumnAlignment("sehcValue", Align.RIGHT);
	tblTaxCalc.setColumnAlignment("billValue", Align.RIGHT);
	//tblTaxCalc.addGeneratedColumn("billDate", generatedColumn);
	}

	private void loadSearchBankName()
	{
		List<MPemCmBank> allList=new ArrayList<MPemCmBank>();
		MPemCmBank all=new MPemCmBank();
		all.setBankName("All Banks");
		all.setBankId(0L);
		allList.add(all);
		
		List<MPemCmBank> bankList = servicebeanBank.getBankDtlsList(companyId,"Active",null);
		for(MPemCmBank obj:bankList){
			allList.add(obj);
		}
		beanBank = new BeanItemContainer<MPemCmBank>(MPemCmBank.class);
		beanBank.addAll(allList);
		cbSearchBankName.setContainerDataSource(beanBank);
		 cbSearchBankName.setValue(all);
	}
	
	private void addDays(Date d, int days) {

	
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = sdf.format(d);
		Date parsedDate = null;
		try {
			parsedDate = sdf.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

		Calendar now = Calendar.getInstance();
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, days);
		dfStartDate.setValue(now.getTime());
		


	}
	
	private void resetSearchFields() {
		// TODO Auto-generated method stub
		dfEndDate.setValue(new Date());
		int days = -30;
		addDays(dfEndDate.getValue(), days);
		dfEndDate.setComponentError(null);
		dfStartDate.setComponentError(null);
		btnSearch.setComponentError(null);
		btnReset.setComponentError(null);
		cbSearchBankName.setValue(null);
	}

	private void getCmBillDetailsTaxReport()
	{
		
		Date startDate = dfStartDate.getValue(); 
		Date endDate =dfEndDate.getValue();
		
		String strStartDate = DateUtils.datetostring(startDate);
		String strEndDate = DateUtils.datetostring(endDate);
		if(searchBankId==null){
			searchBankId=0L;
		}
		/*String paramReference = "PEM_REPORT_PATH";
		String pem_report_path = paramServiceBean.getParameterValueByParamRef(paramReference, companyId);
		String stringLink = ""+pem_report_path+"/pem_tax_rpt&_repFormat=pdf&_dataSource=gerp" +
				"&_outFilename=Tax_Report&_repLocale=en_US&_repEncoding=UTF-8" +
				"&companyID="+companyId+"&stDate="+strStartDate+"&endDate="+strEndDate+"&bankID="+searchBankId;
		System.out.println("String Link==============="+stringLink);
	UI.getCurrent().getPage().open(stringLink, "_blank");*/

    Connection connection = null;
    Statement statement = null;
    String basepath=VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    try {
        connection = Database.getConnection();
        statement = connection.createStatement();
        HashMap parameterMap = new HashMap();
        parameterMap.put("stDate", strStartDate);//sending the report title as a parameter.
        parameterMap.put("endDate", strEndDate);
        parameterMap.put("bankID",searchBankId);
        Report rpt = new Report(parameterMap, connection);
        rpt.setReportName(basepath+"/WEB-INF/reports/pem_tax_rpt");
        rpt.callReport(basepath,"TaxReport");
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            statement.close();
            Database.close(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

		
	}
	private void populateAndConfig(boolean search) {
		// TODO Auto-generated method stub
		try{
		List<TPemCmBillDtls> list = null;
		if(search)
		{
		list = new ArrayList<TPemCmBillDtls>();
		Date startDate = dfStartDate.getValue();
		Date endDate = dfEndDate.getValue();
		if(searchBankId != null ||startDate!=null && endDate!=null)
		{
			list = servicebeanDetailsService.getgetBillDtlsListForTaxCalc(startDate, endDate,companyId,searchBankId);
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
			list = servicebeanDetailsService.getgetBillDtlsListForTaxCalc(null, null,companyId,null);
			System.out.println("calc----------------------------------->"+list);
			total = list.size();

		}
		
		beanBillDtls = new BeanItemContainer<TPemCmBillDtls>(TPemCmBillDtls.class);
		beanBillDtls.addAll(list);
		tblTaxCalc.setContainerDataSource(beanBillDtls);
		tblTaxCalc.setColumnFooter("billValue", "No.of Records:"
				+ total);
		tblTaxCalc.setVisibleColumns("billdtlId","bankname","billNo","billDate","stPrcnt","stValue","scPrcnt","scValue","sehcPrcnt","sehcValue","billValue");
		tblTaxCalc.setColumnHeaders("Ref.Id","Bank Name","Bill No.","Bill Date","St (%)","St Value","Sc (%)","Sc Value","Sehc (%)","Sehc Value","Bill Value");

		}
		catch(Exception e){
			
		}
		getExportTableDetails();
		}

	private void getExportTableDetails()
	{
		excelexporter.setTableToBeExported(tblTaxCalc);
		csvexporter.setTableToBeExported(tblTaxCalc);
		pdfexporter.setTableToBeExported(tblTaxCalc);
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
		if(btnSearch==event.getButton()) {
			populateAndConfig(true);
		//	 resetSearchFields();
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
		else if(btnReport==event.getButton())
		{
			Date startDate = dfStartDate.getValue(); 
			Date endDate =dfEndDate.getValue();
			
			String strStartDate = DateUtils.datetostring(startDate);
			String strEndDate = DateUtils.datetostring(endDate);
			if(searchBankId==null){
				searchBankId=0L;
			}
			/*String paramReference = "PEM_REPORT_PATH";
			String pem_report_path = paramServiceBean.getParameterValueByParamRef(paramReference, companyId);
			String stringLink = ""+pem_report_path+"/pem_tax_rpt&_repFormat=pdf&_dataSource=gerp" +
					"&_outFilename=Tax_Report&_repLocale=en_US&_repEncoding=UTF-8" +
					"&companyID="+companyId+"&stDate="+strStartDate+"&endDate="+strEndDate+"&bankID="+searchBankId;
			System.out.println("String Link==============="+stringLink);
		UI.getCurrent().getPage().open(stringLink, "_blank");*/

			System.out.println("strStartDate"+strStartDate);
			System.out.println("endDate++++++++++++++"+endDate);
			System.out.println("searchBankId=========="+searchBankId);
	    Connection connection = null;
	    Statement statement = null;
	    String basepath=VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	    try {
	        connection = Database.getConnection();
	        statement = connection.createStatement();
	        HashMap parameterMap = new HashMap();
	        parameterMap.put("companyID", companyId);
	        parameterMap.put("stDate", strStartDate);//sending the report title as a parameter.
	        parameterMap.put("endDate", strEndDate);
	        parameterMap.put("bankID",searchBankId);
	        Report rpt = new Report(parameterMap, connection);
	        rpt.setReportName(basepath+"/WEB-INF/reports/pem_tax_rpt");
	        rpt.callReport(basepath,"Tax - Report");
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            statement.close();
	            Database.close(connection);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

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
