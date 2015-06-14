package com.gnts.pem.rpt;

import java.math.BigDecimal;
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
import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;
import com.gnts.base.service.mst.CurrencyService;
import com.gnts.base.service.mst.ParameterService;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.CurrencyColumnGenerator;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.constants.RecordStatus;
import com.gnts.erputil.tool.Database;
import com.gnts.erputil.tool.Report;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateUtils;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.rpt.TPemCmBillDtls;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.rpt.CmBillDtlsService;
import com.ibm.icu.util.Currency;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;

public class BillDetailsUI implements ClickListener{

	
	private ParameterService paramServiceBean = (ParameterService) SpringContextHelper
			.getBean("parameter");
	
	private CmBillDtlsService servbean = (CmBillDtlsService) SpringContextHelper
			.getBean("cmBillDtls");
	private CurrencyService beanCurrency=(CurrencyService) SpringContextHelper
			.getBean("currency");
	private CmBankService servicebeanBank = (CmBankService) SpringContextHelper.getBean("bank");
	
	// Search Layout Components
	
	
	private MPemCmBank selectBank;
	private Long companyId, searchBankId,currencyId;
	private String ccySymbol;
	
	private ComboBox cbSearchBankName;
	private PopupDateField dfSearchStartdt , dfSearchEnddt;
	private ComboBox cbSearchStatus;
	private BeanItemContainer<com.gnts.pem.domain.txn.BillDetails> beansBillDetails = null;


	// Button Declarations
	private Button btnReset, btnCancel, btnReport, btnSearch;

	private Button btnDownload;
	  HorizontalLayout hlFileDownloadLayout, hlAddEditLayout;
	//Declaration for Exporter
	private Window notifications=new Window();
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();

	private Label lblSearchTitle, lblFormTitle, lblTableTitle;
	private Table tblCmBillDetails;

	// Layouts
	VerticalLayout vlSearch = new VerticalLayout();
	VerticalLayout vlTable = new VerticalLayout();
	
	private Logger logger = Logger.getLogger(BillDetailsUI.class);
	
	private String strScreenName;
	
	private BeanItemContainer<TPemCmBillDtls> beansCmBillDtls = null;
	private BeanItemContainer<MPemCmBank> beanBank = null;
	private int total;
	BigDecimal paymentSum=new BigDecimal(0.00);
	
	
	public BillDetailsUI() {

		strScreenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		companyId=Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
if(UI.getCurrent().getSession().getAttribute("currenyId")!=null)
{
		currencyId=Long.valueOf(UI.getCurrent().getSession().getAttribute("currenyId").toString());
}
VerticalLayout clMainLayout = (VerticalLayout) UI.getCurrent().getSession()
				.getAttribute("clLayout");
		HorizontalLayout hlScreenNameLayout = (HorizontalLayout) UI
				.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clMainLayout, hlScreenNameLayout);

	}

	private void buildView(VerticalLayout clMainLayout,
			HorizontalLayout hlScreenNameLayout) {

		logger.info("BillDetails Class called");

		hlScreenNameLayout.removeAllComponents();

		tblCmBillDetails = new Table();
		tblCmBillDetails.setSizeFull();
		tblCmBillDetails.setImmediate(true);
		tblCmBillDetails.setSelectable(true);
		tblCmBillDetails.setColumnCollapsingAllowed(true);
		tblCmBillDetails.setPageLength(14);
		tblCmBillDetails.setFooterVisible(true);
		

		lblTableTitle = new Label("", ContentMode.HTML);
		lblTableTitle.setValue("<B>&nbsp;&nbsp;Action:</B>");

		

		btnSearch = new Button("Search", this);
		btnSearch.setStyleName("searchbt");

		btnReset = new Button("Reset", this);
		btnReset.setStyleName("resetbt");

		btnReport = new Button("Open Report", this);
		btnReport.setEnabled(false);
		btnReport.setStyleName("reportbt");

		btnCancel = new Button("Cancel", this);
		btnCancel.setStyleName("cancelbt");

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
		

		HorizontalLayout hlTableTitleLayout = new HorizontalLayout();
		hlTableTitleLayout.addComponent(lblTableTitle);
		hlTableTitleLayout.setComponentAlignment(lblTableTitle,
				Alignment.MIDDLE_CENTER);
		hlTableTitleLayout.addStyleName("lightgray");
		hlTableTitleLayout.setHeight("25px");
		hlTableTitleLayout.setWidth("60px");

		HorizontalLayout hlTableCaptionLayout = new HorizontalLayout();
		hlTableCaptionLayout.addComponent(hlTableTitleLayout);
		hlTableCaptionLayout.addComponent(btnReport);
		hlTableCaptionLayout.setHeight("25px");	

		HorizontalLayout hlTableTitleandCaptionLayout = new HorizontalLayout();
		hlTableTitleandCaptionLayout.addStyleName("topbarthree");
		hlTableTitleandCaptionLayout.setWidth("100%");
		hlTableTitleandCaptionLayout.addComponent(hlTableCaptionLayout);
		hlTableTitleandCaptionLayout.addComponent(hlFileDownloadLayout);
		hlTableTitleandCaptionLayout.setComponentAlignment(hlFileDownloadLayout,Alignment.MIDDLE_RIGHT);
		hlTableTitleandCaptionLayout.setHeight("28px");
		
		hlAddEditLayout = new HorizontalLayout();
		hlAddEditLayout.addStyleName("topbarthree");
		hlAddEditLayout.setWidth("100%");
		hlAddEditLayout.addComponent(hlTableTitleandCaptionLayout);
		hlAddEditLayout.setHeight("28px");


		final VerticalLayout vlTableLayout = new VerticalLayout();
		vlTableLayout.setSizeFull();
		vlTableLayout.setMargin(true);
		vlTableLayout.addComponent(hlAddEditLayout);
		vlTableLayout.addComponent(tblCmBillDetails);
		vlTable.addComponent(vlTableLayout);

		/* Search Components starts */
		cbSearchStatus = new ComboBox("Status");
		cbSearchStatus.setInputPrompt(ApplicationConstants.selectDefault);
		cbSearchStatus.setItemCaptionPropertyId("desc");
		cbSearchStatus.setImmediate(true);
		cbSearchStatus.setNullSelectionAllowed(false);
		beansBillDetails = new BeanItemContainer<com.gnts.pem.domain.txn.BillDetails>(com.gnts.pem.domain.txn.BillDetails.class);
		beansBillDetails.addAll(RecordStatus.listBillDtls);
		cbSearchStatus.setContainerDataSource(beansBillDetails);
		cbSearchStatus.setWidth("150");
		cbSearchStatus.setValue(RecordStatus.getBillDetails(RecordStatus.BILL_DTLS_PEND_CODE));
		cbSearchStatus.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				methodforreportbutton();
			}
		});

		

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


			dfSearchStartdt = new PopupDateField("Start Date");
			dfSearchStartdt.setInputPrompt("Select Date");
			dfSearchStartdt.setDateFormat("dd-MMM-yyyy");
			dfSearchStartdt.setWidth("155");
			dfSearchStartdt.setImmediate(true);

			dfSearchStartdt.addValueChangeListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 1L;

				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					methodforreportbutton();
				}
			});
		
		dfSearchEnddt = new PopupDateField("End Date");
		dfSearchEnddt.setInputPrompt("Select Date");
		dfSearchEnddt.setDateFormat("dd-MMM-yyyy");
		dfSearchEnddt.setWidth("155");
		dfSearchEnddt.setValue(new Date());
		dfSearchEnddt.setImmediate(true);
		int days = -30;
		addDays(dfSearchEnddt.getValue(), days);
		dfSearchEnddt.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
			
				int days = -30;
				addDays(dfSearchEnddt.getValue(), days);

			}
		});
	/*	dfSearchEnddt.addBlurListener(new BlurListener() {
			
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				//methodforreportbutton();
				System.out.println("\n\n================================ Search End Date===============");
				int days = -30;
				addDays(dfSearchEnddt.getValue(), days);
			}
		});*/
		


		
		
		
		
		FormLayout flSearchBillNo = new FormLayout();
		flSearchBillNo.addComponent(cbSearchBankName);
		FormLayout flSearchStartdt = new FormLayout();
		flSearchStartdt.addComponent(dfSearchStartdt);
		FormLayout flSearchEnddt = new FormLayout();
		flSearchEnddt.addComponent(dfSearchEnddt);
		FormLayout flSearchStatus = new FormLayout();
		flSearchStatus.addComponent(cbSearchStatus);

		Label  lblSpace = new Label();
		lblSpace.setWidth("5");
		
		
		Label  lblSpace1 = new Label();
		lblSpace1.setWidth("5");
		
		
		
		HorizontalLayout hlSearchComponentLayout = new HorizontalLayout();
		hlSearchComponentLayout.addComponent(flSearchBillNo);
		hlSearchComponentLayout.addComponent(flSearchStartdt);
		
		hlSearchComponentLayout.addComponent(lblSpace);
		
		hlSearchComponentLayout.addComponent(flSearchEnddt);
		
		hlSearchComponentLayout.addComponent(lblSpace1);
		
		hlSearchComponentLayout.addComponent(flSearchStatus);
		hlSearchComponentLayout.setSpacing(true);
		hlSearchComponentLayout.setMargin(true);

		VerticalLayout vlSearchandResetButtonLAyout = new VerticalLayout();
		vlSearchandResetButtonLAyout.setSpacing(true);
		vlSearchandResetButtonLAyout.addComponent(btnSearch);
		vlSearchandResetButtonLAyout.addComponent(btnReset);
		vlSearchandResetButtonLAyout.setWidth("100");
		vlSearchandResetButtonLAyout.addStyleName("topbarthree");
		vlSearchandResetButtonLAyout.setMargin(true);

		HorizontalLayout hlSearchComponentandButtonLayout = new HorizontalLayout();
		hlSearchComponentandButtonLayout.setSizeFull();
		hlSearchComponentandButtonLayout.setSpacing(true);
		hlSearchComponentandButtonLayout.addComponent(hlSearchComponentLayout);
		hlSearchComponentandButtonLayout.setComponentAlignment(
				hlSearchComponentLayout, Alignment.MIDDLE_LEFT);

		hlSearchComponentandButtonLayout
				.addComponent(vlSearchandResetButtonLAyout);
		hlSearchComponentandButtonLayout.setComponentAlignment(
				vlSearchandResetButtonLAyout, Alignment.MIDDLE_RIGHT);
		hlSearchComponentandButtonLayout.setExpandRatio(vlSearchandResetButtonLAyout, 1);
		final VerticalLayout vlSearchComponentandButtonLayout = new VerticalLayout();
		vlSearchComponentandButtonLayout.setSpacing(true);
		vlSearchComponentandButtonLayout.setSizeFull();
		vlSearchComponentandButtonLayout
				.addComponent(hlSearchComponentandButtonLayout);

		lblSearchTitle = new Label("", ContentMode.HTML);
		lblSearchTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName
				+ "</b>&nbsp;::&nbsp;Search");

		vlSearch.addComponent(PanelGenerator
				.createPanel(vlSearchComponentandButtonLayout));
		vlSearch.setMargin(true);

		/* Search Components end */

	

		lblFormTitle = new Label("", ContentMode.HTML);
		lblFormTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName
				+ "</b>&nbsp;::&nbsp;Search");

		

		clMainLayout.addComponent(vlSearch);
		clMainLayout.addComponent(vlTable);



		setTableProperties();
		populateAndConfigureTableNew(true);

		hlScreenNameLayout.addComponent(lblFormTitle);
		hlScreenNameLayout.setComponentAlignment(lblFormTitle,
				Alignment.MIDDLE_LEFT);
		
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
	


	
	private void addDays(Date d, int days) {

				DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = sdf.format(d);
		Date parsedDate = null;
		try {
			parsedDate = sdf.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.warn("calculate days" + e);
		}

		Calendar now = Calendar.getInstance();
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, days);
		dfSearchStartdt.setValue(now.getTime());
		

					
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
	public void methodforreportbutton()

	{
		try
		{
			String billStatus = cbSearchStatus.getValue().toString();
		
			Date startDate = dfSearchStartdt.getValue(); 
			Date endDate =dfSearchEnddt.getValue();
			
			
			if(billStatus!=null && startDate!=null && endDate!=null)
			{
				btnReport.setEnabled(true);
			}
			else{
				btnReport.setEnabled(false);
			}
			
		}
		catch(Exception e)
		{
			
		}
	}
	// Method for show the details in grid table while search and normal mode

	private void populateAndConfigureTableNew(Boolean search) {
		try{

		tblCmBillDetails.removeAllItems();
		List<TPemCmBillDtls> usertable = new ArrayList<TPemCmBillDtls>();

		if (search) {
			String strStatus = null;
			com.gnts.pem.domain.txn.BillDetails status = (com.gnts.pem.domain.txn.BillDetails) cbSearchStatus.getValue();
			try {
				strStatus = status.getCode();
			} catch (Exception e) {

			}
			
			Date dtStartdate = (Date)dfSearchStartdt.getValue();
			Date dtEnddate = (Date)dfSearchEnddt.getValue();
			
	
			if (searchBankId != null || dtStartdate != null || dtEnddate != null ||strStatus != null) {
				
				usertable = servbean.getBillDtlsList(companyId,null, searchBankId, null, dtStartdate, dtEnddate, strStatus);
						
				total = usertable.size();
				try{
				paymentSum=servbean.getSumPymntAmount(null, searchBankId, null, dtStartdate, dtEnddate, strStatus,companyId);
				}catch(Exception e){
					e.printStackTrace();
				}
				}

		}

		else {

			usertable = servbean.getBillDtlsList(companyId,null, null, null,null,null,
					null);
			total = usertable.size();
			paymentSum=servbean.getSumPymntAmount(null, null, null, null, null, null,companyId);
		}

		beansCmBillDtls = new BeanItemContainer<TPemCmBillDtls>(
				TPemCmBillDtls.class);
		beansCmBillDtls.addAll(usertable);

		tblCmBillDetails.setContainerDataSource(beansCmBillDtls);

		tblCmBillDetails.setColumnFooter("lastUpdatedBy", "No.of Records:"
				+ total);
		tblCmBillDetails.setColumnFooter("paymentAmount", "Total Amount : "+paymentSum);
		tblCmBillDetails.setVisibleColumns(new Object[] { "billdtlId","bankname",
				"billNo", "billDate","paymentAmount", "paymentStatus",
				"lastUpdatedDt", "lastUpdatedBy" });
		tblCmBillDetails.setColumnHeaders(new String[] { "Ref.Id","Bank Name",
				"Bill No.", "Bill Date", "Payment Amount","Payment Status","Last Updated Date",
				"Last Updated By" });
		tblCmBillDetails.setSelectable(true);
		}catch(Exception e){
			
		}
		getExportTableDetails();
	}

	// Show the expected value in Grid table.
	public void setTableProperties() {

		tblCmBillDetails.addGeneratedColumn("paymentAmount", new CurrencyColumnGenerator());
		tblCmBillDetails.setColumnAlignment("paymentAmount", Align.RIGHT);
		tblCmBillDetails.setColumnAlignment("billNo", Align.RIGHT);
		tblCmBillDetails.setColumnAlignment("billdtlId", Align.RIGHT);
	//	tblCmBillDetails.addGeneratedColumn("bankname",new BankNameColumnGenerator());
		
		/*List<MBaseCurrency> list=beanCurrency.getCurrencyList(currencyId,null, null,null);
		for(MBaseCurrency obj:list){
			 ccySymbol = obj.getCcysymbol();
			}*/
		
			}


	private void resetsearchfield() {
		dfSearchEnddt.setValue(new Date());
		int days = -30;
		addDays(dfSearchEnddt.getValue(), days);
		
		cbSearchStatus.setValue(RecordStatus.getBillDetails(RecordStatus.BILL_DTLS_PEND_DESC));
		
		cbSearchBankName.setValue(null);

	}



	private void getCmBillDetailsTaxReport()
	{
		
		String strStatus = null;
		com.gnts.pem.domain.txn.BillDetails st = (com.gnts.pem.domain.txn.BillDetails) cbSearchStatus.getValue();
		try {
			strStatus = st.getCode();
		} catch (Exception e) {

		}
		
	
		Date startDate = dfSearchStartdt.getValue(); 
		Date endDate =dfSearchEnddt.getValue();
		 String basepath=VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		String strStartDate = DateUtils.datetostring(startDate);
		String strEndDate = DateUtils.datetostring(endDate);
		if(searchBankId==null){
			searchBankId=0L;
		}
		
		if(strStatus.equals("Pending")){
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
	   
	    try {
	        connection = Database.getConnection();
	        statement = connection.createStatement();
	        HashMap parameterMap = new HashMap();
	        parameterMap.put("companyID", companyId);
	        parameterMap.put("stDate", strStartDate);//sending the report title as a parameter.
	        parameterMap.put("endDate", strEndDate);
	        parameterMap.put("bankID",searchBankId);
	        Report rpt = new Report(parameterMap, connection);
	        rpt.setReportName(basepath+"/WEB-INF/reports/pem_pendpymt_rpt");
	        rpt.callReport(basepath,"Pending Payment - Report");
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
		else
		{
			/*String stringLink = "http://192.168.1.8:9090/JasperReportsIntegration/report?_repName=/gerp/pem/pem_pymtrect_rpt&_repFormat=pdf&_dataSource=gerp" +
					"&_outFilename=Payment_Paid_Report&_repLocale=en_US&_repEncoding=UTF-8" +
					"&companyID="+companyId+"&stDate="+strStartDate+"&endDate="+strEndDate+"&bankID="+searchBankId;
			*/
			
		/*	String paramReference = "PEM_REPORT_PATH";
			String pem_report_path = paramServiceBean.getParameterValueByParamRef(paramReference, companyId);
			String stringLink = ""+pem_report_path+"/pem_pymtrect_rpt&_repFormat=pdf&_dataSource=gerp" +
					"&_outFilename=Payment_Paid_Report&_repLocale=en_US&_repEncoding=UTF-8" +
					"&companyID="+companyId+"&stDate="+strStartDate+"&endDate="+strEndDate+"&bankID="+searchBankId;*/
			
			
			
			

			System.out.println("strStartDate"+strStartDate);
			System.out.println("endDate++++++++++++++"+strEndDate);
			System.out.println("searchBankId=========="+searchBankId);
	    Connection connection = null;
	    Statement statement = null;
	    try {
	        connection = Database.getConnection();
	        statement = connection.createStatement();
	        HashMap parameterMap = new HashMap();
	        parameterMap.put("companyID", companyId);
	        parameterMap.put("stDate", strStartDate);//sending the report title as a parameter.
	        parameterMap.put("endDate", strEndDate);
	        parameterMap.put("bankID",searchBankId);
	        Report rpt = new Report(parameterMap, connection);
	        rpt.setReportName(basepath+"/WEB-INF/reports/pem_pymtrect_rpt");
	        rpt.callReport(basepath,"Payment Receipt - Report");
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

		
	}
	private void getExportTableDetails()
	{
		excelexporter.setTableToBeExported(tblCmBillDetails);
		csvexporter.setTableToBeExported(tblCmBillDetails);
		pdfexporter.setTableToBeExported(tblCmBillDetails);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
		
	}

	// Button Click event
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		notifications.close();

		if (btnCancel == event.getButton()) {
			vlSearch.setVisible(true);
			
			tblCmBillDetails.setValue(null);
			
			lblFormTitle.setValue("&nbsp;&nbsp;<b>" + strScreenName
					+ "</b>&nbsp;::&nbsp;Search");
			hlFileDownloadLayout.removeAllComponents();
			hlFileDownloadLayout.addComponent(btnDownload);
			getExportTableDetails();

		} else if (btnSearch == event.getButton()) {
			populateAndConfigureTableNew(true);
			 hlFileDownloadLayout.removeAllComponents();
				hlFileDownloadLayout.addComponent(btnDownload);
				getExportTableDetails();
		}

		

		if (btnReset == event.getButton()) {
			resetsearchfield();
			populateAndConfigureTableNew(false);
		}

		if (btnReport == event.getButton()) {
			
			getCmBillDetailsTaxReport();
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
