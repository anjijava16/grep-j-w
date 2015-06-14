/**
 * File Name	:	SyndicateLandApp.java
 * Description	:	This class is used for add/edit Syndicate bank Land details and generate report.
 * Author		:	Karthigadevi S
 * Date			:	March 17, 2014
 * Modification 
 * Modified By  :   
 * Description	:
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1 
 */
package com.gnts.pem.txn.synd;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;
import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.ui.UploadUI2;
import com.gnts.erputil.validations.DateValidation;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.txn.common.TPemCmAssetDetails;
import com.gnts.pem.domain.txn.common.TPemCmBldngStgofcnstructn;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.domain.txn.common.TPemCmLandValutnData;
import com.gnts.pem.domain.txn.common.TPemCmOwnerDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropAdjoinDtls;
import com.gnts.pem.domain.txn.common.TPemCmPropDimension;
import com.gnts.pem.domain.txn.common.TPemCmPropDocDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropGuidlnRefdata;
import com.gnts.pem.domain.txn.common.TPemCmPropGuidlnValue;
import com.gnts.pem.domain.txn.common.TPemCmPropImage;
import com.gnts.pem.domain.txn.common.TPemCmPropLegalDocs;
import com.gnts.pem.domain.txn.common.TPemCmPropValtnSummry;
import com.gnts.pem.domain.txn.synd.TPemSydPropMatchBoundry;
import com.gnts.pem.domain.txn.synd.TPemSynPropAreaDtls;
import com.gnts.pem.domain.txn.synd.TPemSynPropOccupancy;
import com.gnts.pem.domain.txn.synd.TPemSynPropViolation;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.rpt.CmBillDtlsService;
import com.gnts.pem.service.txn.common.CmAssetDetailsService;
import com.gnts.pem.service.txn.common.CmBldngStgofcnstructnService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;
import com.gnts.pem.service.txn.common.CmLandValutnDataService;
import com.gnts.pem.service.txn.common.CmOwnerDetailsService;
import com.gnts.pem.service.txn.common.CmPropAdjoinDtlsService;
import com.gnts.pem.service.txn.common.CmPropDimensionService;
import com.gnts.pem.service.txn.common.CmPropDocDetailsService;
import com.gnts.pem.service.txn.common.CmPropGuidlnRefdataService;
import com.gnts.pem.service.txn.common.CmPropGuidlnValueService;
import com.gnts.pem.service.txn.common.CmPropImageService;
import com.gnts.pem.service.txn.common.CmPropLegalDocsService;
import com.gnts.pem.service.txn.common.CmPropValtnSummryService;
import com.gnts.pem.service.txn.synd.SydPropMatchBoundryService;
import com.gnts.pem.service.txn.synd.SynPropAreaDtlsService;
import com.gnts.pem.service.txn.synd.SynPropOccupancyService;
import com.gnts.pem.service.txn.synd.SynPropViolationService;
import com.gnts.pem.util.UIFlowData;
import com.gnts.pem.util.XMLUtil;
import com.gnts.pem.util.iterator.ComponentIterDimensionofPlot;
import com.gnts.pem.util.iterator.ComponentIterGuideline;
import com.gnts.pem.util.iterator.ComponentIterOwnerDetails;
import com.gnts.pem.util.iterator.ComponentIteratorAdjoinProperty;
import com.gnts.pem.util.iterator.ComponentIteratorLegalDoc;
import com.gnts.pem.util.iterator.ComponentIteratorNormlDoc;
import com.gnts.pem.util.list.AdjoinPropertyList;
import com.gnts.pem.util.list.DimensionList;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class SyndLand implements ClickListener {
	private static final long serialVersionUID = 1L;
	private CmBankConstantService beanBankConst=(CmBankConstantService)SpringContextHelper.getBean("bankConstant");
	private CmEvalDetailsService beanEvaluation=(CmEvalDetailsService)SpringContextHelper.getBean("evalDtls");
	private CmOwnerDetailsService  beanOwner= (CmOwnerDetailsService) SpringContextHelper.getBean("ownerDtls");
	private CmAssetDetailsService  beanAsset= (CmAssetDetailsService) SpringContextHelper
			.getBean("assetDtls");
	private CmPropDocDetailsService  beanDocument= (CmPropDocDetailsService) SpringContextHelper
			.getBean("propDocument");
	private CmPropLegalDocsService legalDoc = (CmPropLegalDocsService) SpringContextHelper
			.getBean("legalDoc");
	private CmPropAdjoinDtlsService beanAdjoin = (CmPropAdjoinDtlsService) SpringContextHelper
			.getBean("adjoinDtls");
	private CmPropDimensionService beanDimension = (CmPropDimensionService) SpringContextHelper
			.getBean("propDimension");
	private SydPropMatchBoundryService beanmatchboundary = (SydPropMatchBoundryService) SpringContextHelper
			.getBean("synMatchBoundary");
	private SynPropOccupancyService beantenureOccupancy = (SynPropOccupancyService) SpringContextHelper
			.getBean("synPropOccupancy");
	private CmBldngStgofcnstructnService beanconstruction = (CmBldngStgofcnstructnService) SpringContextHelper
			.getBean("stageCnstn");
	private SynPropViolationService beanviolation = (SynPropViolationService) SpringContextHelper
			.getBean("synPropViolation");
	private SynPropAreaDtlsService beanareadetails = (SynPropAreaDtlsService) SpringContextHelper
			.getBean("synAreaDtls");
	private CmLandValutnDataService beanlandvaluation = (CmLandValutnDataService) SpringContextHelper
			.getBean("landValtn");
	private CmPropGuidlnValueService beanguidelinevalue = (CmPropGuidlnValueService) SpringContextHelper
			.getBean("guidelineValue");
	private CmPropGuidlnRefdataService beanguidelinereference = (CmPropGuidlnRefdataService) SpringContextHelper
			.getBean("guidelineRef");
	private CmPropValtnSummryService beanPropertyvalue = (CmPropValtnSummryService) SpringContextHelper
			.getBean("propValtnSummary");
	private CmBillDtlsService beanBill=(CmBillDtlsService) SpringContextHelper
			.getBean("billDtls");
	private CmBankService beanbank = (CmBankService) SpringContextHelper
			.getBean("bank");

	private CmPropImageService beanPropImage = (CmPropImageService) SpringContextHelper
			.getBean("propImage");

	private Table tblEvalDetails = new Table();
	private BeanItemContainer<TPemCmEvalDetails> beans = null;
	String documentStatus = "";

	private Accordion accordion = new Accordion();
	private VerticalLayout mainPanel = new VerticalLayout();
	private VerticalLayout searchPanel = new VerticalLayout();
	private VerticalLayout tablePanel = new VerticalLayout();
	private Long headerid;
	private String strEvaluationNo = "SYN_LAND_";
	private String strXslFile = "SynLand.xsl";
	private String strBillXsl = "Bill.xsl";
	private String evalNumber;
	private String customer;
	private String propertyType;
	private String screenName;
	int count =0;
	
	public static Boolean filevalue=false;
	public static Boolean filevalue1=false;
	private FileDownloader filedownloader;
	// pagination
		private int total = 0;
		// for header layoute
		private Label lblTableTitle;
		private Label lblFormTittle, lblFormTitle1, lblAddEdit;
		private Label lblSaveNotification, lblNotificationIcon,lblNoofRecords;
		private Button btnBack;
	//Declaration for table panel
	private VerticalLayout layoutTable = new VerticalLayout();
	private HorizontalLayout hlAddEditLayout ;
	private Button btnAdd = new Button("Add", this);
	private Button btnEdit = new Button("Edit", this);
	private Button btnView = new Button("View Document", this);
	private HorizontalLayout hlBreadCrumbs;

	//Declaration for search panel
	private HorizontalLayout layoutSearch = new HorizontalLayout();
	private TextField tfSearchEvalNumber = new TextField("Evaluation Number");
	private TextField tfSearchCustomer = new TextField("Customer Name");
	private PopupDateField dfSearchEvalDate = new PopupDateField(
			"Evaluation Date");
	
	private ComboBox tfSearchBankbranch = new ComboBox("Bank Branch");

	private Button btnSearch = new Button("Search", this);
	private Button btnReset = new Button("Reset", this);
	private Button btnDownload;
	  HorizontalLayout hlFileDownloadLayout;
	//Declaration for Exporter
	private Window notifications=new Window();
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	//Declaration for main panel
	private VerticalLayout layoutMainForm = new VerticalLayout();
	private HorizontalLayout layoutButton2 = new HorizontalLayout();
	private Button btnSave = new Button("Save", this);
	private Button btnCancel = new Button("Cancel", this);
	private Button btnSubmit=new Button("Submit",this);
//	private Button saveExcel = new Button("Report");
	private Label lblHeading = new Label();

	//Declaration for evaluation details
	private VerticalLayout layoutEvaluationDetails = new VerticalLayout();
	private GridLayout layoutEvaluationDetails1 = new GridLayout();
	private TextField tfEvaluationNumber = new TextField("Evaluation Number");
	private TextField tfEvaluationPurpose = new TextField("Evaluation Purpose");
	private PopupDateField dfDateofValuation = new PopupDateField(
			"Date of valuation made");
	private TextField tfValuatedBy = new TextField("Valuation By");
	private PopupDateField dfVerifiedDate = new PopupDateField(
			"Verification Date");
	private TextField tfVerifiedBy = new TextField("Verified By");
	private ComboBox tfBankBranch = new ComboBox("Bank Branch");
	private TextField tfDynamicEvaluation1 = new TextField();
	private TextField tfDynamicEvaluation2 = new TextField();
	private Button btnDynamicEvaluation1 = new Button("", this);

	//Declaration for asset details
	private VerticalLayout layoutAssetOwner = new VerticalLayout();
	private VerticalLayout layoutAssetDetails = new VerticalLayout();
	private GridLayout layoutAssetDetails1 = new GridLayout();
	private TextField tfCustomerName = new TextField("Customer Name");
	private TextArea tfCustomerAddr = new TextArea("Customer Address");
	private ComboBox slPropertyDesc = new ComboBox(
			"Description of the Property");
	private TextArea tfPropertyAddress = new TextArea("Property Address");
	private TextField tfLandMark = new TextField("Land Mark");
	private CheckBox chkSameAddress = new CheckBox("Same Address?");
	private TextField tfDynamicAsset1 = new TextField();
	private TextField tfDynamicAsset2 = new TextField();
	private Button btnDynamicAsset = new Button("", this);

	//Declaration Owner Details
	private VerticalLayout layoutOwnerDetails = new VerticalLayout();
	private GridLayout layoutOwnerDetails1 = new GridLayout();
	private Button btnAddOwner = new Button("", this);

	//Declaration for Document Details
	private VerticalLayout layoutNormalLegal = new VerticalLayout();
	private VerticalLayout panelNormalDocumentDetails = new VerticalLayout();
	private VerticalLayout panelLegalDocumentDetails = new VerticalLayout();
	private Button btnAddNorDoc = new Button("", this);
	private Button btnAddLegalDoc = new Button("", this);

	//Declaration for adjoin properties
	private VerticalLayout panelAdjoinProperties = new VerticalLayout();
	private Button btnAddAdjoinProperty = new Button("", this);

	//Declaration for dimension of plot
	private VerticalLayout panelDimension = new VerticalLayout();
	private Button btnAddDimension = new Button("", this);
	private Label lblDimension = new Label("");
	private int itemDimensionNumber = 1;

	//Declaration matching boundaries
	private VerticalLayout layoutmachingBoundary = new VerticalLayout();
	private FormLayout layoutmachingBoundary1 = new FormLayout();
	private VerticalLayout layoutmachingBoundary2 = new VerticalLayout();
	private ComboBox slMatchingBoundary = new ComboBox("Matching of Boundaries");
	private ComboBox slPlotDemarcated = new ComboBox("Plot Demarcated");
	private ComboBox slApproveLandUse = new ComboBox("Approved Land Use");
	private ComboBox slTypeofProperty = new ComboBox("Type of Property");
	private TextField tfDynamicmatching1 = new TextField();
	private TextField tfDynamicmatching2 = new TextField();
	private Button btnDynamicmatching = new Button("", this);

	//Declaration for tenure/occupancy details
	private GridLayout layoutTenureOccupay = new GridLayout();
	private FormLayout layoutTenureOccupay1 = new FormLayout();
	private VerticalLayout layoutTenureOccupay2 = new VerticalLayout();
	private TextField tfStatusofTenure = new TextField("Status of Tenure");
	private ComboBox slOwnedorRent = new ComboBox("Owned/Rented");
	private TextField tfNoOfYears = new TextField("No of Years Occupancy");
	private TextField tfRelationship = new TextField(
			"Relationship of tenant to the owner");
	private TextField tfDynamicTenure1 = new TextField();
	private TextField tfDynamicTenure2 = new TextField();
	private Button btnDynamicTenure = new Button("", this);

	//Declaration for stage of construction
	private VerticalLayout layoutConstruction = new VerticalLayout();
	private GridLayout layoutConstruction1 = new GridLayout();
	private TextField tfStageofConst = new TextField("Stage of Construction");
	private TextField tfDynamicConstruction1 = new TextField();
	private TextField tfDynamicConstruction2 = new TextField();
	private Button btnDynamicConstruction = new Button("", this);

	//Declaration for violation details
	private VerticalLayout layoutViolation = new VerticalLayout();
	private GridLayout layoutViolation1 = new GridLayout();
	private TextField tfAnyViolation = new TextField(
			"Violation if any observed");
	private TextField tfDynamicViolation1 = new TextField();
	private TextField tfDynamicViolation2 = new TextField();
	private Button btnDynamicViolation = new Button("", this);

	//Declaration for area details of the property
	private VerticalLayout layoutAreaDetails = new VerticalLayout();
	private GridLayout layoutAreaDetails1 = new GridLayout();
	private TextField tfSiteArea = new TextField("Site Area");
	private TextField tfPlinthArea = new TextField(
			"Plinth Area of the building");
	private TextField tfCarpetArea = new TextField("Carpet Area");
	private TextField tfSalableArea = new TextField("Salabale Area");
	private TextArea tfRemarks = new TextArea("Remarks");
	private TextField tfDynamicAreaDetail1 = new TextField();
	private TextField tfDynamicAreaDetail2 = new TextField();
	private Button btnDynamicAreaDetail = new Button("", this);

	//Declaration for valuation of land
	private VerticalLayout layoutValuationLand = new VerticalLayout();
	private GridLayout layoutValuationLand1 = new GridLayout();
	private TextField tfAreaofLand = new TextField("Area of the Land");
	private TextField tfNorthandSouth = new TextField("North and South");
	private TextField tfMarketRate = new TextField("Market Rate of land/cent");
	private TextField tfAdopetdMarketRate = new TextField(
			"Adopted Market rate of land/cent");
	private TextField tfFairMarketRate = new TextField(
			"Fair Market value of the Land");
	private TextField tfDynamicValuation1 = new TextField();
	private TextField tfDynamicValuation2 = new TextField();
	private Button btnDynamicValuation = new Button("", this);

	//Declaration for plinth area
	private VerticalLayout layoutGuideline = new VerticalLayout();
	private Button btnAddGuideline = new Button("", this);

	//Declaration for guideline reference details
	private VerticalLayout layoutGuidelineReference = new VerticalLayout();
	private GridLayout layoutGuidelineReference1 = new GridLayout();
	private TextField tfZone = new TextField("Zone");
	private TextField tfSRO = new TextField("SRO");
	private TextField tfVillage = new TextField("Village");
	private TextField tfRevnueDist = new TextField("Revenue Dist Name");
	private TextField tfTalukName = new TextField("Taluk Name");
	private VerticalLayout streetLayout=new VerticalLayout();
	private TextField tfStreetName = new TextField();
	private ComboBox slStreetSerNo=new ComboBox();
	private TextField tfGuidelineValue = new TextField("Guide Line Value(Sqft)");
	private TextField tfGuidelineValueMatric = new TextField(
			"Guideline Value(In Metric)");
	private TextField slClassification = new TextField("Classification");

	//Declaration for property values
	private VerticalLayout layoutPropertyValue = new VerticalLayout();
	private GridLayout layoutPropertyValue1 = new GridLayout();
	private TextField tfRealziableRate = new TextField(
			"Realizable value of the Land");
	private TextField tfDistressRate = new TextField(
			"Distress value of the Land");
	private TextField tfGuidelineRate = new TextField(
			"Guideline value of the Land");

	//Declaration for commondata
	private Window mywindow = new Window("Enter Caption");
	private Button myButton = new Button("Ok", this);
	private TextField tfCaption = new TextField();
	private String strSelectedPanel;

	private String strComponentWidth = "200px";
	private Long selectedBankid;
	private String selectedFormName;
	private Long selectCompanyid,currencyId;
	private String loginusername;

	
	//image 
	//for property image
		private VerticalLayout vlImageLayout=new VerticalLayout();
		private HorizontalLayout hlImage=new HorizontalLayout();
		private HorizontalLayout hlImageLayout1=new HorizontalLayout();
		private HorizontalLayout hlImageLayout2=new HorizontalLayout();
		private Button btnImgDownload = new Button("Download",this);
		private Button btnImg1Download = new Button("Download",this);
		
	
	// for report
	UIFlowData uiflowdata = new UIFlowData();

	private String basepath;
	private String basepath1,basepath2;

	private Logger logger = Logger.getLogger(SyndLand.class);
	public SyndLand() {
	
		loginusername = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		selectedFormName=screenName;
		VerticalLayout clArgumentLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		if(UI.getCurrent().getSession().getAttribute("currenyId")!=null)
		{
		currencyId=Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("currenyId").toString());
		}
		
//		currencyId = Long.valueOf(UI.getCurrent().getSession()
//				.getAttribute("currenyId").toString());
		String[] splitlist=screenName.split("-");
		for(String str:splitlist){
		List<MPemCmBank> list=beanbank.getBankDtlsList(selectCompanyid, null, str);
		
		for(MPemCmBank obj:list){
		selectedBankid = obj.getBankId();
		System.out.println("bankid"+obj.getBankId());
		}
		break;
		}
		HorizontalLayout hlHeaderLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clArgumentLayout,hlHeaderLayout);
		
		basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		basepath1 = basepath+"/VAADIN/themes/gerp/img/Upload.jpg";
		basepath2= basepath+"/VAADIN/themes/gerp/img/Upload2.jpg";
	}

	/**
	 * buildMainview()-->for screen UI design
	 * 
	 * @param clArgumentLayout
	 * @param hlHeaderLayout
	 */
	@SuppressWarnings("deprecation")
	void buildView(VerticalLayout layoutPage,HorizontalLayout hlHeaderLayout) {
		// for component width
		hlHeaderLayout.removeAllComponents();
		setComponentStyle();
		
		tblEvalDetails = new Table();
		tblEvalDetails.setStyleName(Runo.TABLE_SMALL);
		tblEvalDetails.setPageLength(14);
		tblEvalDetails.setSizeFull();
		tblEvalDetails.setFooterVisible(true);
		tblEvalDetails.setSelectable(true);
		tblEvalDetails.setImmediate(true);
		tblEvalDetails.setColumnCollapsingAllowed(true);
		//
		lblTableTitle = new Label();
		lblSaveNotification = new Label();
		lblSaveNotification.setContentMode(ContentMode.HTML);
		lblNotificationIcon = new Label();
		lblTableTitle.setValue("<B>&nbsp;&nbsp;Action:</B>");
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
		btnBack = new Button("Home", this);
		btnBack.setStyleName("link");
		// table panel

				HorizontalLayout flTableCaption = new HorizontalLayout();
				flTableCaption.addComponent(lblTableTitle);
				flTableCaption.setComponentAlignment(lblTableTitle,
						Alignment.MIDDLE_CENTER);
				flTableCaption.addStyleName("lightgray");
				flTableCaption.setHeight("25px");
				flTableCaption.setWidth("60px");
				lblNoofRecords = new Label(" ", ContentMode.HTML);
				lblNoofRecords.addStyleName("lblfooter");
				
				
		// for evaluation details
		tfEvaluationPurpose.setValue("Collateral Security to the Bank");
		slStreetSerNo.addItem("STREET NAME");
		slStreetSerNo.addItem("SURVEY NO");
		slStreetSerNo.setNullSelectionAllowed(false);
		
		tfEvaluationNumber.setRequired(true);
		tfBankBranch.setRequired(true);
		dfDateofValuation.setRequired(true);
		tfEvaluationPurpose.setRequired(true);
		tfCustomerName.setRequired(true);
		layoutEvaluationDetails1.setColumns(4);

		layoutEvaluationDetails1.addComponent(tfEvaluationNumber);
		layoutEvaluationDetails1.addComponent(tfBankBranch);
		layoutEvaluationDetails1.addComponent(tfEvaluationPurpose);
		layoutEvaluationDetails1.addComponent(tfValuatedBy);
		layoutEvaluationDetails1.addComponent(dfDateofValuation);
		layoutEvaluationDetails1.addComponent(dfVerifiedDate);
		layoutEvaluationDetails1.addComponent(tfVerifiedBy);
		layoutEvaluationDetails1.addComponent(tfDynamicEvaluation1);
		layoutEvaluationDetails1.addComponent(tfDynamicEvaluation2);
		tfDynamicEvaluation1.setVisible(false);
		tfDynamicEvaluation2.setVisible(false);
		layoutEvaluationDetails1.setSpacing(true);
		layoutEvaluationDetails1.setMargin(true);

		layoutEvaluationDetails.addComponent(btnDynamicEvaluation1);
		layoutEvaluationDetails.setComponentAlignment(btnDynamicEvaluation1,
				Alignment.TOP_RIGHT);
		layoutEvaluationDetails.addComponent(layoutEvaluationDetails1);

		// for asset details
		VerticalLayout formAsset1 = new VerticalLayout();
		VerticalLayout formAsset2 = new VerticalLayout();
		VerticalLayout formAsset3 = new VerticalLayout();
		VerticalLayout formAsset4 = new VerticalLayout();
		formAsset1.setSpacing(true);
		formAsset2.setSpacing(true);
		formAsset4.setSpacing(true);

		formAsset1.addComponent(tfCustomerName);
		formAsset1.addComponent(tfLandMark);
		formAsset1.addComponent(slPropertyDesc);
		formAsset2.addComponent(tfCustomerAddr);
		formAsset3.addComponent(tfPropertyAddress);
		formAsset3.addComponent(chkSameAddress);
		formAsset4.addComponent(tfDynamicAsset1);
		formAsset4.addComponent(tfDynamicAsset2);
		tfDynamicAsset1.setVisible(false);
		tfDynamicAsset2.setVisible(false);

		chkSameAddress.setImmediate(true);
		chkSameAddress.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {

				if (chkSameAddress.getValue().equals(true)) {
					tfPropertyAddress.setValue(tfCustomerAddr.getValue());

				} else {
					tfPropertyAddress.setValue("");
				}
			}
		});
		/*tfEvaluationNumber.addValidator(new IntegerValidator("Enter numbers only"));
		tfEvaluationNumber.addBlurListener(new SaarcValidate(tfEvaluationNumber));*/
		tfEvaluationNumber.setImmediate(true);
		
		tfEvaluationNumber.addBlurListener(new BlurListener(){

			
			private static final long serialVersionUID = 1L;
		
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				if(!tfEvaluationNumber.isReadOnly()){
				tfEvaluationNumber.setComponentError(null);
				try{
					tfEvaluationNumber.setComponentError(null);
				String evalno =tfEvaluationNumber.getValue().toString();
				count = beanEvaluation.getEvalNoCount(evalno);
				
				if(tfEvaluationNumber.getValue()!=null && tfEvaluationNumber.getValue().trim().length()>0 ){
					
					if(count == 0){
						
						tfEvaluationNumber.setComponentError(null);
						
					}else{
						
						tfEvaluationNumber.setComponentError(new UserError("Evaluation number is already Exist"));
						
					}
				}
				}
catch(Exception e){
					
				}
				}
				
			}
});
		layoutAssetDetails1.setSpacing(true);
		layoutAssetDetails1.setColumns(4);
		layoutAssetDetails1.addComponent(formAsset1);
		layoutAssetDetails1.addComponent(formAsset2);
		layoutAssetDetails1.addComponent(formAsset3);
		layoutAssetDetails1.addComponent(formAsset4);
		layoutAssetDetails1.setMargin(true);

		layoutAssetDetails.addComponent(btnDynamicAsset);
		layoutAssetDetails.setComponentAlignment(btnDynamicAsset,
				Alignment.TOP_RIGHT);
		layoutAssetDetails.addComponent(layoutAssetDetails1);

		lblHeading = new Label("Owner Details");
		layoutAssetOwner.addComponent(lblHeading);
		lblHeading.setStyleName("h4");
		layoutAssetOwner.addComponent(PanelGenerator
				.createPanel(layoutOwnerDetails));
		lblHeading = new Label("Asset Details");
		layoutAssetOwner.addComponent(lblHeading);
		lblHeading.setStyleName("h4");
		layoutAssetOwner.addComponent(PanelGenerator
				.createPanel(layoutAssetDetails));
		// for document details
		panelNormalDocumentDetails.addComponent(btnAddNorDoc);
		panelNormalDocumentDetails.setComponentAlignment(btnAddNorDoc,
				Alignment.TOP_RIGHT);
		panelNormalDocumentDetails.addComponent(new ComponentIteratorNormlDoc(
				null, null, "", ""));
		panelNormalDocumentDetails.setMargin(true);
		panelLegalDocumentDetails.addComponent(btnAddLegalDoc);
		panelLegalDocumentDetails.setComponentAlignment(btnAddLegalDoc,
				Alignment.TOP_RIGHT);
		panelLegalDocumentDetails.addComponent(new ComponentIteratorLegalDoc(
				"", "", null));
		panelLegalDocumentDetails.setMargin(true);
		layoutNormalLegal.addComponent(PanelGenerator
				.createPanel(panelNormalDocumentDetails));
		lblHeading = new Label("Legal Documents");
		layoutNormalLegal.addComponent(lblHeading);
		lblHeading.setStyleName("h4");
		layoutNormalLegal.addComponent(PanelGenerator
				.createPanel(panelLegalDocumentDetails));
		layoutNormalLegal.setMargin(true);
		// for adjoin properties
		panelAdjoinProperties.addComponent(btnAddAdjoinProperty);
		panelAdjoinProperties.setComponentAlignment(btnAddAdjoinProperty,
				Alignment.BOTTOM_RIGHT);
		panelAdjoinProperties.addComponent(new ComponentIteratorAdjoinProperty(
				null, true, true, true));
		// for Owner Details
		layoutOwnerDetails.addComponent(btnAddOwner);
		layoutOwnerDetails.setComponentAlignment(btnAddOwner,
				Alignment.TOP_RIGHT);
		layoutOwnerDetails1.setColumns(4);
		layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("", ""));
		layoutOwnerDetails.addComponent(layoutOwnerDetails1);
		layoutOwnerDetails1.setSpacing(true);
		layoutOwnerDetails1.setMargin(true);
		// for dimensions
		panelDimension.addComponent(btnAddDimension);
		panelDimension.addComponent(lblDimension);
		lblDimension.setValue("Item No :" + itemDimensionNumber);
		panelDimension.setComponentAlignment(btnAddDimension,
				Alignment.BOTTOM_RIGHT);
		panelDimension.addComponent(new ComponentIterDimensionofPlot(null,
				true, true, true));

		panelNormalDocumentDetails.setWidth("100%");
		panelLegalDocumentDetails.setWidth("100%");

		// for matching boundaries
		layoutmachingBoundary1.setSpacing(true);
		tfDynamicmatching1.setVisible(true);
		tfDynamicmatching2.setVisible(true);
		layoutmachingBoundary2.addComponent(btnDynamicmatching);
		layoutmachingBoundary2.setComponentAlignment(btnDynamicmatching,
				Alignment.TOP_RIGHT);
		layoutmachingBoundary1.setSpacing(true);
		layoutmachingBoundary1.addComponent(slMatchingBoundary);
		layoutmachingBoundary1.addComponent(slPlotDemarcated);
		layoutmachingBoundary1.addComponent(slApproveLandUse);
		layoutmachingBoundary1.addComponent(slTypeofProperty);
		layoutmachingBoundary1.addComponent(tfDynamicmatching1);
		layoutmachingBoundary1.addComponent(tfDynamicmatching2);
		layoutmachingBoundary1.setSpacing(true);
		layoutmachingBoundary1.setMargin(true);

		// for tenure/occupancy details
		layoutTenureOccupay.setSpacing(true);
		layoutTenureOccupay1.setSpacing(true);
		layoutTenureOccupay2.addComponent(btnDynamicTenure);
		layoutTenureOccupay2.setComponentAlignment(btnDynamicTenure,
				Alignment.TOP_RIGHT);
		layoutTenureOccupay.setColumns(2);
		layoutTenureOccupay1.addComponent(tfStatusofTenure);
		layoutTenureOccupay1.addComponent(slOwnedorRent);
		layoutTenureOccupay1.addComponent(tfNoOfYears);
		layoutTenureOccupay1.addComponent(tfRelationship);
		layoutTenureOccupay1.addComponent(tfDynamicTenure1);
		layoutTenureOccupay1.addComponent(tfDynamicTenure2);
		layoutmachingBoundary2.addComponent(layoutmachingBoundary1);
		layoutTenureOccupay2.addComponent(layoutTenureOccupay1);
		layoutTenureOccupay.addComponent(PanelGenerator
				.createPanel(layoutmachingBoundary2));
		layoutTenureOccupay.addComponent(PanelGenerator
				.createPanel(layoutTenureOccupay2));
		tfDynamicTenure1.setVisible(false);
		tfDynamicTenure2.setVisible(false);
		layoutmachingBoundary1.setMargin(true);
		layoutTenureOccupay1.setMargin(true);
		layoutmachingBoundary.addComponent(layoutTenureOccupay);
		layoutTenureOccupay1.setMargin(true);
		layoutmachingBoundary.setMargin(true);

		// construction
		layoutConstruction1.setSpacing(true);
		layoutConstruction1.setColumns(4);
		layoutConstruction1.addComponent(tfStageofConst);
		layoutConstruction1.addComponent(tfDynamicConstruction1);
		layoutConstruction1.addComponent(tfDynamicConstruction2);
		tfDynamicConstruction1.setVisible(false);
		tfDynamicConstruction2.setVisible(false);

		layoutConstruction.addComponent(btnDynamicConstruction);
		layoutConstruction.setComponentAlignment(btnDynamicConstruction,
				Alignment.TOP_RIGHT);
		layoutConstruction.addComponent(layoutConstruction1);
		layoutConstruction1.setMargin(true);

		// for violation
		layoutViolation1.setSpacing(true);
		layoutViolation1.setColumns(4);
		layoutViolation1.addComponent(tfAnyViolation);
		layoutViolation1.addComponent(tfDynamicViolation1);
		layoutViolation1.addComponent(tfDynamicViolation2);
		tfDynamicViolation1.setVisible(false);
		tfDynamicViolation2.setVisible(false);

		layoutViolation.addComponent(btnDynamicViolation);
		layoutViolation.setComponentAlignment(btnDynamicViolation,
				Alignment.TOP_RIGHT);
		layoutViolation.addComponent(layoutViolation1);
		layoutViolation1.setMargin(true);

		// area details of the property
		layoutAreaDetails1.setSpacing(true);
		layoutAreaDetails1.setColumns(4);
		layoutAreaDetails1.addComponent(tfSiteArea);
		layoutAreaDetails1.addComponent(tfPlinthArea);
		layoutAreaDetails1.addComponent(tfCarpetArea);
		layoutAreaDetails1.addComponent(tfSalableArea);
		layoutAreaDetails1.addComponent(tfRemarks);
		layoutAreaDetails1.addComponent(tfDynamicAreaDetail1);
		layoutAreaDetails1.addComponent(tfDynamicAreaDetail2);
		tfDynamicAreaDetail1.setVisible(false);
		tfDynamicAreaDetail2.setVisible(false);

		layoutAreaDetails.addComponent(btnDynamicAreaDetail);
		layoutAreaDetails.setComponentAlignment(btnDynamicAreaDetail,
				Alignment.TOP_RIGHT);
		layoutAreaDetails.addComponent(layoutAreaDetails1);
		layoutAreaDetails1.setMargin(true);

		// valuation of land
		layoutValuationLand1.setSpacing(true);
		layoutValuationLand1.setColumns(4);
		layoutValuationLand1.addComponent(tfAreaofLand);
		layoutValuationLand1.addComponent(tfNorthandSouth);
		layoutValuationLand1.addComponent(tfMarketRate);
		layoutValuationLand1.addComponent(tfAdopetdMarketRate);
		layoutValuationLand1.addComponent(tfFairMarketRate);
		layoutValuationLand1.addComponent(tfDynamicValuation1);
		layoutValuationLand1.addComponent(tfDynamicValuation2);
		tfDynamicValuation1.setVisible(false);
		tfDynamicValuation2.setVisible(false);
		tfAdopetdMarketRate.setRequired(true);
		layoutValuationLand.addComponent(btnDynamicValuation);
		layoutValuationLand.setComponentAlignment(btnDynamicValuation,
				Alignment.TOP_RIGHT);
		layoutValuationLand.addComponent(layoutValuationLand1);
		layoutValuationLand1.setMargin(true);

		// for Guideline area
		layoutGuideline.addComponent(btnAddGuideline);
		layoutGuideline.setComponentAlignment(btnAddGuideline,
				Alignment.TOP_RIGHT);
		layoutGuideline.setMargin(true);
		layoutGuideline.addComponent(new ComponentIterGuideline("Land", "", "",
				""));
		layoutGuideline.addComponent(new ComponentIterGuideline("Building", "",
				"", ""));
		// for guide line reference
		streetLayout.addComponent(slStreetSerNo);
		streetLayout.addComponent(tfStreetName);
		
		layoutGuidelineReference1.setColumns(4);
		layoutGuidelineReference1.setSpacing(true);
		layoutGuidelineReference1.addComponent(tfZone);
		layoutGuidelineReference1.addComponent(tfSRO);
		layoutGuidelineReference1.addComponent(tfVillage);
		layoutGuidelineReference1.addComponent(tfRevnueDist);
		layoutGuidelineReference1.addComponent(tfTalukName);
		layoutGuidelineReference1.addComponent(streetLayout);
		layoutGuidelineReference1.addComponent(tfGuidelineValue);
		layoutGuidelineReference1.addComponent(tfGuidelineValueMatric);
		layoutGuidelineReference1.addComponent(slClassification);
		layoutGuidelineReference1.setMargin(true);

		layoutGuidelineReference.setSpacing(true);
		layoutGuidelineReference.addComponent(layoutGuidelineReference1);
		
		
		//for property image
				hlImage.setSpacing(true);
				btnImgDownload.setStyleName("downloadbt");
				btnImg1Download.setStyleName("downloadbt");
				
				//layoutDescProperty.setComponentAlignment(btnImg1Download, Alignment.MIDDLE_CENTER);

				
				hlImage.addComponent(hlImageLayout1);
				hlImage.addComponent(hlImageLayout2);
				hlImageLayout1.setMargin(true);
				hlImageLayout2.setMargin(true);
				hlImageLayout1.setSpacing(true);
				hlImageLayout2.setSpacing(true);
				vlImageLayout.addComponent(hlImage);

		// property value
		layoutPropertyValue.setSpacing(true);
		layoutPropertyValue.setMargin(true);
		layoutPropertyValue1.setColumns(4);
		layoutPropertyValue1.setSpacing(true);
		layoutPropertyValue1.addComponent(tfRealziableRate);
		layoutPropertyValue1.addComponent(tfDistressRate);
		layoutPropertyValue1.addComponent(tfGuidelineRate);
		layoutPropertyValue.addComponent(layoutPropertyValue1);

		accordion.setWidth("100%");
		layoutEvaluationDetails.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutEvaluationDetails),
				"Evaluation Details");
		layoutOwnerDetails.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutAssetOwner),
				"Owner Details/Asset Details");
		layoutAssetDetails.setStyleName("bluebar");

		layoutNormalLegal.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutNormalLegal),
				"Document Details");

		panelAdjoinProperties.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(panelAdjoinProperties),
				"Adjoining Properties");
		panelDimension.setStyleName("bluebar");
		accordion.addTab(panelDimension,
				"Dimension");

		layoutmachingBoundary.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutmachingBoundary),
				"Matching of Boundaries And Tenure/Occupancy Details");

		layoutValuationLand.setStyleName("bluebar");
		accordion.addTab(layoutValuationLand,
				"Valuation of Land");
		
		layoutAreaDetails.setStyleName("bluebar");
		
		
		this.accordion.addListener(new SelectedTabChangeListener() {
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if(event.getTabSheet().getSelectedTab().equals(panelDimension)){
				Iterator<Component> myComps = panelDimension.getComponentIterator();
				BigDecimal siteArea=new BigDecimal(0.00);
				while (myComps.hasNext()) {
					final Component component = myComps.next();
					int i=1;
					
					if (component instanceof ComponentIterDimensionofPlot) {

						ComponentIterDimensionofPlot mycomponent = (ComponentIterDimensionofPlot) component;
						List<TPemCmPropDimension> getList = mycomponent
								.getDimensionPropertyList();

						try {
							List<String> mylist = mycomponent.getLeastValaue();
							siteArea=siteArea.add(new BigDecimal(mylist.get(0).replaceAll("[^\\d.]", "")));
							tfNorthandSouth.setValue(mylist.get(1));
							tfSiteArea.setValue(siteArea.toString());
						} catch (Exception e) {
							 
							logger.info("Error-->" + e);
						}
					}
				}
				BigDecimal site=new BigDecimal(0.00);
				BigDecimal fair=new BigDecimal(1.00);
				BigDecimal salbale=new BigDecimal(435.60);
				try{
					site = new BigDecimal(tfSiteArea.getValue().replaceAll("[^\\d.]", ""));
				}catch(Exception e){
					site = new BigDecimal("0.00");
					
				}
				try{
					site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString();
					fair.multiply(site.divide(salbale, 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(tfAdopetdMarketRate.getValue().replaceAll("[^\\d.]", "")));
					tfSiteArea.setValue(site.toString()+" sft (or) "+site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString()+" cents");
					tfSalableArea.setValue(site.toString()+" sft (or) "+site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString()+" cents");
					tfAreaofLand.setValue(site.toString()+" sft (or) "+site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString()+" cents");
					tfFairMarketRate.setValue(XMLUtil.IndianFormat(new BigDecimal(fair.multiply(site.divide(salbale, 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(tfAdopetdMarketRate.getValue().replaceAll("[^\\d.]", ""))).toString())));
				}catch(Exception e){
					 
				}
			}	if(event.getTabSheet().getSelectedTab().equals(layoutValuationLand)){
				
				BigDecimal realizable=new BigDecimal(0.00);
				BigDecimal distress=new BigDecimal(0.00);
				BigDecimal real=new BigDecimal("0.00");
				BigDecimal distre=new BigDecimal("0.00");
				
				try {
					real = new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
				} catch (Exception e) {
					real = new BigDecimal("0.00");
					 
				}
				try {
					distre = new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
				} catch (Exception e) {
					distre = new BigDecimal("0.00");
					 
				}
				try{
					realizable=real.multiply(new BigDecimal(95)).divide(new BigDecimal(100));
					realizable=realizable.subtract(realizable.remainder(new BigDecimal(1000)));
					tfRealziableRate.setValue(realizable.toString());
					uiflowdata.setRealizablevalue(XMLUtil.IndianFormat(new BigDecimal(realizable.toString())));
					
					distress=(distre.multiply(new BigDecimal(85))).divide(new BigDecimal(100));
					distress=distress.subtract(distress.remainder(new BigDecimal(1000)));
					tfDistressRate.setValue(distress.toString());
					uiflowdata.setDistressvalue(XMLUtil.IndianFormat(new BigDecimal(distress.toString())));
					}catch(Exception e){
						
					}
		
			
				}
			
		}});
				

		accordion.addTab(PanelGenerator.createPanel(layoutAreaDetails),
				"Area Details of the Property");
		layoutGuideline.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutGuideline),
				"Guideline Details");
		layoutGuidelineReference.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutGuidelineReference),
				"Guideline Reference Details");
		layoutMainForm.addComponent(PanelGenerator.createPanel(accordion));
		layoutPropertyValue.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutPropertyValue),
				"Property Value Details");
		layoutConstruction.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutConstruction),
				"Construction Details");
		layoutViolation.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutViolation),
				"Violation Details");
		accordion.addTab(PanelGenerator.createPanel(vlImageLayout),
				"Property Image Details");
		layoutMainForm.setMargin(true);
		layoutMainForm.setSpacing(true);
		// for main panel
				layoutButton2.setSpacing(true);
				btnSave.setStyleName("savebt");
				btnCancel.setStyleName("cancelbt");
				//saveExcel.addStyleName("downloadbt");
				btnSubmit.setStyleName("submitbt");
				btnSave.setVisible(false);
				btnCancel.setVisible(false);
				btnSubmit.setVisible(false);
				//saveExcel.setVisible(false);
				layoutButton2.addComponent(btnSave);
				layoutButton2.addComponent(btnSubmit);
				//layoutButton2.addComponent(saveExcel);
				layoutButton2.addComponent(btnCancel);
			
				 excelexporter.setTableToBeExported(tblEvalDetails);
			     excelexporter.setCaption("Microsoft Excel (XLS)");
			     excelexporter.setStyleName("borderless");
			        
			     csvexporter.setTableToBeExported(tblEvalDetails);
			     csvexporter.setCaption("Comma Dilimited (CSV)");
			     csvexporter.setStyleName("borderless");
					
			     pdfexporter.setTableToBeExported(tblEvalDetails);
			     pdfexporter.setCaption("Acrobat Document (PDF)");
			     pdfexporter.setStyleName("borderless");
				
				hlBreadCrumbs = new HorizontalLayout();
				hlBreadCrumbs.addComponent(lblFormTitle1);
				hlBreadCrumbs.addComponent(btnBack);
				hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
				hlBreadCrumbs.addComponent(lblAddEdit);
				hlBreadCrumbs
						.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER);
				hlBreadCrumbs.setVisible(false);
				
				HorizontalLayout hlNotificationLayout = new HorizontalLayout();
				hlNotificationLayout.addComponent(lblNotificationIcon);
				hlNotificationLayout.setComponentAlignment(lblNotificationIcon,
						Alignment.MIDDLE_LEFT);
				hlNotificationLayout.addComponent(lblSaveNotification);
				hlNotificationLayout.setComponentAlignment(lblSaveNotification,
						Alignment.MIDDLE_LEFT);
				hlHeaderLayout.addComponent(lblFormTittle);
				hlHeaderLayout.setComponentAlignment(lblFormTittle,
						Alignment.MIDDLE_LEFT);
				hlHeaderLayout.addComponent(hlBreadCrumbs);
				hlHeaderLayout.setComponentAlignment(hlBreadCrumbs,
						Alignment.MIDDLE_LEFT);
				hlHeaderLayout.addComponent(hlNotificationLayout);
				hlHeaderLayout.setComponentAlignment(hlNotificationLayout,
						Alignment.MIDDLE_LEFT);
				hlHeaderLayout.addComponent(layoutButton2);
				hlHeaderLayout.setComponentAlignment(layoutButton2,
						Alignment.MIDDLE_RIGHT);
				
				hlHeaderLayout.addComponent(layoutButton2);
				hlHeaderLayout.setComponentAlignment(layoutButton2, Alignment.BOTTOM_RIGHT);
		mainPanel.addComponent(layoutMainForm);
		mainPanel.setVisible(false);

		// for search panel
				// for search panel
		        // Added by Hohul ----->  For Search Panel Layouts
					FormLayout flSearchEvalNumber = new FormLayout();
					flSearchEvalNumber.addComponent(tfSearchEvalNumber);
					
					
					FormLayout flSearchBankbranch = new FormLayout();
					flSearchBankbranch.addComponent(tfSearchBankbranch);
					
					
					FormLayout flSearchCustomer = new FormLayout();
					flSearchCustomer.addComponent(tfSearchCustomer);
					
					HorizontalLayout hlSearchComponentLayout = new HorizontalLayout();
					hlSearchComponentLayout.addComponent(flSearchEvalNumber);
					hlSearchComponentLayout.addComponent(flSearchBankbranch);
					hlSearchComponentLayout.addComponent(flSearchCustomer);
					hlSearchComponentLayout.setSpacing(true);
					hlSearchComponentLayout.setMargin(true);

							
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
					
					VerticalLayout vlSearchandResetButtonLAyout = new VerticalLayout();
					vlSearchandResetButtonLAyout.setSpacing(true);
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

				/*layoutSearch.addComponent(tfSearchEvalNumber);
				layoutSearch.addComponent(tfSearchBankBranch);
				layoutSearch.addComponent(tfSearchCustomer);
				layoutSearch.addComponent(btnReset);
				layoutSearch.setComponentAlignment(btnReset, Alignment.BOTTOM_LEFT);*/

		btnReset.addStyleName("resetbt");

		tfSearchCustomer.setImmediate(true);
		tfSearchCustomer.addListener(new TextChangeListener() {
			private static final long serialVersionUID = 1L;
			SimpleStringFilter filter = null;

			public void textChange(TextChangeEvent event) {
				Filterable f = (Filterable) tblEvalDetails
						.getContainerDataSource();
				if (filter != null)
					f.removeContainerFilter(filter);

				filter = new SimpleStringFilter("custName", event.getText(),
						true, false);

				f.addContainerFilter(filter);
				total=f.size();
				tblEvalDetails.setColumnFooter("lastUpdateDt","No. of Records:"+total);
			}
		});
		tfSearchEvalNumber.setImmediate(true);
		tfSearchEvalNumber.addListener(new TextChangeListener() {
			private static final long serialVersionUID = 1L;
			SimpleStringFilter filter = null;

			public void textChange(TextChangeEvent event) {
				Filterable f = (Filterable) tblEvalDetails
						.getContainerDataSource();
				if (filter != null)
					f.removeContainerFilter(filter);

				filter = new SimpleStringFilter("evalNo", event.getText(),
						true, false);

				f.addContainerFilter(filter);
				total=f.size();
				tblEvalDetails.setColumnFooter("lastUpdateDt","No. of Records:"+total);
			}
		});

		tfSearchBankbranch.setImmediate(true);
		tfSearchBankbranch.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			Filter filter = null;

			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				
				try{
				String strBankBranch = tfSearchBankbranch.getValue().toString();
				Filterable f = (Filterable) tblEvalDetails
						.getContainerDataSource();
				if (filter != null)
					f.removeContainerFilter(filter);

				
				  filter = new Compare.Equal("bankBranch",strBankBranch );
	                f.addContainerFilter(filter);
				f.addContainerFilter(filter);
				total=f.size();
				tblEvalDetails.setColumnFooter("lastUpdateDt","No. of Records:"+total);
				}
				catch(Exception e){
				}
			}
				

		});

		layoutSearch.setSpacing(true);
		layoutSearch.setMargin(true);
	
		searchPanel.addComponent(PanelGenerator.createPanel(vlSearchComponentandButtonLayout));
		searchPanel.setMargin(true);
		searchPanel.setMargin(true);
		HorizontalLayout hlTableTittleLayout = new HorizontalLayout();
		hlTableTittleLayout.addComponent(flTableCaption);
		hlTableTittleLayout.addComponent(btnAdd);
		hlTableTittleLayout.addComponent(btnEdit);
		hlTableTittleLayout.addComponent(btnView);
		hlTableTittleLayout.setHeight("25px");
		hlTableTittleLayout.setSpacing(true);
		
	HorizontalLayout hlTableTitleandCaptionLayout = new HorizontalLayout();
		hlTableTitleandCaptionLayout.addStyleName("topbarthree");
		hlTableTitleandCaptionLayout.setWidth("100%");
		hlTableTitleandCaptionLayout.addComponent(hlTableTittleLayout);
		hlTableTitleandCaptionLayout.addComponent(hlFileDownloadLayout);
		hlTableTitleandCaptionLayout.setComponentAlignment(hlFileDownloadLayout,Alignment.MIDDLE_RIGHT);
		hlTableTitleandCaptionLayout.setHeight("28px");
		// for table panel
		btnAdd.addStyleName("add");
		btnEdit.addStyleName("editbt");
		btnView.addStyleName("view");
		btnView.setEnabled(false);
		
		hlAddEditLayout = new HorizontalLayout();
		hlAddEditLayout.addStyleName("topbarthree");
		hlAddEditLayout.setWidth("100%");
		hlAddEditLayout.addComponent(hlTableTitleandCaptionLayout);
		hlAddEditLayout.setHeight("28px");
		
		layoutTable.addComponent(hlAddEditLayout);
		layoutTable.addComponent(tblEvalDetails);
		tablePanel.addComponent(layoutTable);
		tablePanel.setStyleName(Runo.PANEL_LIGHT);
		tablePanel.setWidth("100%");
		tablePanel.setMargin(true);

		layoutPage.addComponent(mainPanel);
		layoutPage.addComponent(searchPanel);
		layoutPage.addComponent(tablePanel);

		// load Component list values
		loadComponentListValues();

		//setTableProperties();
		populateAndConfig(false);
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
	


	String showSubWindow() {
		tfCaption.setValue("");
		VerticalLayout subwindow = new VerticalLayout();
		mywindow.setHeight("130px");
		mywindow.setWidth("230px");
		tfCaption.setWidth("200px");
		mywindow.center();
		mywindow.setModal(true);
		tfCaption.focus();
		subwindow.addComponent(tfCaption);
		subwindow.addComponent(myButton);
		subwindow.setComponentAlignment(myButton, Alignment.MIDDLE_CENTER);
		subwindow.setMargin(true);
		subwindow.setSpacing(true);
		mywindow.setContent(subwindow);
		
    	mywindow.setResizable(false);
		myButton.setStyleName("default");
		mywindow.setContent(subwindow);
		UI.getCurrent().addWindow(mywindow);
		return null;
	}

	@SuppressWarnings("deprecation")
	private void populateAndConfig(boolean search) {
		try {
		tblEvalDetails.removeAllItems();
		tblEvalDetails.setImmediate(true);
		List<TPemCmEvalDetails> evalList = null;
		 evalList = new ArrayList<TPemCmEvalDetails>();
		if (search) {
			String evalno = tfSearchEvalNumber.getValue();
			String customer = tfSearchCustomer.getValue();
			String bankbranch=(String)tfSearchBankbranch.getValue();
			evalList = beanEvaluation.getSearchEvalDetailnList(null, evalno, null, customer,bankbranch,selectedBankid,selectCompanyid,null);
		} else {
			
			evalList = beanEvaluation.getSearchEvalDetailnList(selectedFormName,null, null,null,null,selectedBankid,selectCompanyid,null);
			total = evalList.size();
		}if (total == 0) {
			lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
			lblSaveNotification.setValue("No Records found");
		} else {
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
		}
		lblNoofRecords.setValue("<font size=\"2\" color=\"black\">No.of Records:</font> <font size=\"2\" color=\"#1E90FF\"> " + total +"</font>");
		beans = new BeanItemContainer<TPemCmEvalDetails>(TPemCmEvalDetails.class);
		beans.addAll(evalList);
		btnEdit.setEnabled(false);
		tblEvalDetails.setContainerDataSource(beans);
		tblEvalDetails.setSelectable(true);
		tblEvalDetails.setColumnFooter("lastUpdateDt","No. of Records:"+total);
		tblEvalDetails.setVisibleColumns(new Object[] { "evalNo",
				"docDate", "bankBranch", "custName", "docStatus",
				"lastUpdtedBy", "lastUpdateDt" });
		tblEvalDetails.setColumnHeaders(new String[] { "Evaluation Number",
				"Evaluation Date", "Bank Branch", "Customer Name",
				"Status", "Last Updated By", "Last Updated Date" });
			
		
		tblEvalDetails
		.addValueChangeListener(new Property.ValueChangeListener() {
			/**
	 * 
	 */
			private static final long serialVersionUID = 3729824796823933688L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				
				TPemCmEvalDetails	syncList= (TPemCmEvalDetails) event.getProperty().getValue();
					if (syncList!= null) {				
				
					if(syncList.getDocStatus().equals("Draft")||syncList.getDocStatus().equals("Rejected")){
						btnEdit.setEnabled(true);
						btnView.setEnabled(false);
					}else{
						btnEdit.setEnabled(false);
						btnView.setEnabled(true);
						
					}
					btnAdd.setEnabled(false);
				}
				else{
					btnEdit.setEnabled(false);
					btnAdd.setEnabled(true);
				}
				} 

			

		});
		tblEvalDetails.setImmediate(true);
		tblEvalDetails
		.addItemClickListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				if (tblEvalDetails.isSelected(event.getItemId())) {

					btnView.setEnabled(false);
				} else {

				btnView.setEnabled(true);
				}
			}
		});
	} catch (Exception e) {
		e.printStackTrace();
		logger.info("Error-->"+e);
	}
		getExportTableDetails();
}
	/*void setTableProperties() {
		beans = new BeanItemContainer<TPemCmEvalDetails>(
				TPemCmEvalDetails.class);
		tblEvalDetails.addGeneratedColumn("docDate",
				new DateFormateColumnGenerator());
		tblEvalDetails.addGeneratedColumn("lastUpdatedDt",
				new DateColumnGenerator());
	}*/
	private void updateEvaluationDetails(){
		try{
			 boolean valid=false;
			TPemCmEvalDetails evalobj = new TPemCmEvalDetails();
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="LAND";
			String basepath = VaadinService.getCurrent()
			          .getBaseDirectory().getAbsolutePath();
			File file = new File(basepath+"/WEB-INF/PEM-DOCS/"+evalNumber+"_"+customer+"_"+propertyType+".doc");
			FileInputStream fin = new FileInputStream(file);
		    byte fileContent[] = new byte[(int)file.length()];
		    fin.read(fileContent);
		    fin.close();
		    evalobj.setDocId(headerid);
		    evalobj.setEvalDoc(fileContent);
		    evalobj.setDocStatus(Common.DOC_PENDING);
			evalobj.setEvalNo(tfEvaluationNumber.getValue());
			evalobj.setValuationBy(tfValuatedBy.getValue());
			MPemCmBank bankid=new MPemCmBank();
			bankid.setBankId(selectedBankid);
			evalobj.setBankId(bankid);
			evalobj.setDoctype(screenName);
			evalobj.setCompanyId(selectCompanyid);
			evalobj.setBankBranch((String)tfBankBranch.getValue());
			evalobj.setEvalPurpose(tfEvaluationPurpose.getValue());
			evalobj.setEvalDate(dfDateofValuation.getValue());
			evalobj.setCheckedBy(tfVerifiedBy.getValue());
			evalobj.setCheckedDt(dfVerifiedDate.getValue());
			evalobj.setInspectionDt(dfDateofValuation.getValue());
			evalobj.setInspectionBy(tfValuatedBy.getValue());
			evalobj.setLastUpdateDt(new Date());
			evalobj.setLastUpdtedBy(loginusername);
			evalobj.setCustName(tfCustomerName.getValue());

			if (tfDynamicEvaluation1.getValue() != null
					&& tfDynamicEvaluation1.getValue().trim().length() > 0) {
				evalobj.setCustomLbl1(tfDynamicEvaluation1.getCaption());
				evalobj.setCustomValue1(tfDynamicEvaluation1.getValue());
			}
			if (tfDynamicEvaluation2.getValue() != null
					&& tfDynamicEvaluation2.getValue().trim().length() > 0) {
				evalobj.setCustomValue2(tfDynamicEvaluation2.getCaption());
				evalobj.setCustomValue2(tfDynamicEvaluation2.getValue());
			}
			String numberOnly = tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll(
					"[^0-9]", "");
			if( numberOnly.trim().length()==0){
				numberOnly="0";	
			}
			System.out.println("Number Only====="+numberOnly);
			uiflowdata.setAmountInWords(beanEvaluation
					.getAmountInWords(numberOnly));
			evalobj.setPropertyValue(Double.valueOf(numberOnly));
			uiflowdata.setEvalDtls(evalobj);
			
			if(tfEvaluationNumber.isValid() && tfBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid() &&tfCustomerName.isValid()&&tfAdopetdMarketRate.isValid())
			{
				
				if(count ==0){
				beanEvaluation.saveorUpdateEvalDetails(evalobj);
				
				valid = true;
				}
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblSaveNotification.setValue(ApplicationConstants.updatedMsg);
			}
			
			if(valid){
				/*populateAndConfig(false);
				resetAllFieldsFields();*/
				btnSubmit.setEnabled(false);
			}else{
				btnSubmit.setComponentError(new UserError("Form is invalid"));
			}
		}catch(Exception e){
			e.printStackTrace();
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
			lblSaveNotification
					.setValue("Submit failed, please check the data and try again ");
			logger.info("Error on SaveApproveReject Status function--->" + e);
		}
	}
void setComponentError(){
	tfEvaluationNumber.setComponentError(null);
	dfDateofValuation.setComponentError(null);
	tfEvaluationPurpose.setComponentError(null);
	tfBankBranch.setComponentError(null);
	tfCustomerName.setComponentError(null);
	tfAdopetdMarketRate.setComponentError(null);
	String Error="Enter";
	if(tfEvaluationNumber.getValue()==null || tfEvaluationNumber.getValue().trim().length()==0){
		tfEvaluationNumber.setComponentError(new UserError("Enter Evaluation Number"));
		Error=Error+" "+"Evaluation number";
	}
	if(dfDateofValuation.getValue()==null){
		dfDateofValuation.setComponentError(new UserError("Select Evaluation Date"));
		Error=Error+" "+"Evaluation Date";
	}
	if(tfEvaluationPurpose.getValue()==null || tfEvaluationPurpose.getValue().trim().length()==0){
		tfEvaluationPurpose.setComponentError(new UserError("Select Evaluation Date"));
		Error=Error+" "+"Evaluation Purpose";
	}
	if(tfBankBranch.getValue()==null){
		tfBankBranch.setComponentError(new UserError("Select Bank Branch"));
		Error=Error+" "+"Bank Branch";
	}
	if(tfCustomerName== null||tfCustomerName.getValue().trim().length()==0){
		tfCustomerName.setComponentError(new UserError("Enter Customer Name"));
		Error = Error+" "+"Customer Name";
	}
	if(tfAdopetdMarketRate == null || tfAdopetdMarketRate.getValue().trim().length() ==0){
		tfAdopetdMarketRate.setComponentError(new UserError("Enter Market Rate"));
		Error = Error+" "+"Market Rate";
	}
	lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
	lblSaveNotification.setValue(Error);
	
}
	private void saveEvaluationDetails() {
		uiflowdata = new UIFlowData();
		// for save evaluation details
		boolean valid = false;
		try {
			TPemCmEvalDetails evalobj = new TPemCmEvalDetails();

			evalobj.setDocId(headerid);
			evalobj.setEvalNo(tfEvaluationNumber.getValue());
			evalobj.setDocDate(dfDateofValuation.getValue());
			evalobj.setValuationBy(tfValuatedBy.getValue());
			MPemCmBank bankid=new MPemCmBank();
			bankid.setBankId(selectedBankid);
			evalobj.setBankId(bankid);
			evalobj.setDoctype(screenName);
			evalobj.setCompanyId(selectCompanyid);
			evalobj.setBankBranch((String)tfBankBranch.getValue());
			evalobj.setEvalPurpose(tfEvaluationPurpose.getValue());
			evalobj.setEvalDate(dfDateofValuation.getValue());
			evalobj.setCheckedBy(tfVerifiedBy.getValue());
			evalobj.setCheckedDt(dfVerifiedDate.getValue());
			evalobj.setInspectionDt(dfDateofValuation.getValue());
			evalobj.setInspectionBy(tfValuatedBy.getValue());
			evalobj.setDocStatus(Common.DOC_DRAFT);
			evalobj.setLastUpdateDt(new Date());
			evalobj.setLastUpdtedBy(loginusername);
			evalobj.setCustName(tfCustomerName.getValue());

			if (tfDynamicEvaluation1.getValue() != null
					&& tfDynamicEvaluation1.getValue().trim().length() > 0) {
				evalobj.setCustomLbl1(tfDynamicEvaluation1.getCaption());
				evalobj.setCustomValue1(tfDynamicEvaluation1.getValue());
			}
			if (tfDynamicEvaluation2.getValue() != null
					&& tfDynamicEvaluation2.getValue().trim().length() > 0) {
				evalobj.setCustomValue2(tfDynamicEvaluation2.getCaption());
				evalobj.setCustomValue2(tfDynamicEvaluation2.getValue());
			}
			beanEvaluation.saveorUpdateEvalDetails(evalobj);
			uiflowdata.setEvalDtls(evalobj);
			try {
				saveOwnerDetails();
			} catch (Exception e) {
			}

			try {
				saveAssetDetails();
			} catch (Exception e) {
			}

			try {
				saveNormalDocuments();
			} catch (Exception e) {
			}

			try {
				saveLegalDocuments();
			} catch (Exception e) {
			}

			try {
				saveAdjoinPropertyDetails();
			} catch (Exception e) {
			}

			try {
				saveDimensionValues();
			} catch (Exception e) {
				 
				logger.info("Error-->" + e);
			}

			try {
				saveMatchingBoundaries();
			} catch (Exception e) {
			}

			try {
				saveTenureOccupayDetails();
			} catch (Exception e) {
			}

			try {
				saveConstructionDetails();
			} catch (Exception e) {
			}

			try {
				saveViolationDetails();
			} catch (Exception e) {
			}
			try {
				saveGuidelineValue();
			} catch (Exception e) {
			}

			try {
				saveGuidelineReferenceDetails();
			} catch (Exception e) {
			}
			try {
				saveProductImageDetails();
			} catch (Exception e) {
				e.printStackTrace();

			}
			try {
				uiflowdata.setPropertyAddress(tfPropertyAddress.getValue());
				uiflowdata.setBankBranch((String)tfBankBranch.getValue());
				uiflowdata.setEvalnumber(tfEvaluationNumber.getValue());
				if (dfDateofValuation.getValue() != null) {
					SimpleDateFormat dt1 = new SimpleDateFormat("dd-MMM-yyyy");
					uiflowdata.setInspectionDate(dt1.format(dfDateofValuation
							.getValue()));
				}
				BigDecimal site=new BigDecimal(0.00);
				BigDecimal fair=new BigDecimal(1.00);
				BigDecimal salbale=new BigDecimal(435.60);
				try{
					site = new BigDecimal(tfSiteArea.getValue().replaceAll("[^\\d.]", ""));
				}catch(Exception e){
					site = new BigDecimal("0.00");
					
				}
				try{
					site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString();
					fair.multiply(site.divide(salbale, 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(tfAdopetdMarketRate.getValue().replaceAll("[^\\d.]", "")));
					tfSiteArea.setValue(site.toString()+" sft (or) "+site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString()+" cents");
					tfSalableArea.setValue(site.toString()+" sft (or) "+site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString()+" cents");
					tfAreaofLand.setValue(site.toString()+" sft (or) "+site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString()+" cents");
					tfFairMarketRate.setValue(XMLUtil.IndianFormat(new BigDecimal(fair.multiply(site.divide(salbale, 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(tfAdopetdMarketRate.getValue().replaceAll("[^\\d.]", ""))).toString())));
				}catch(Exception e){
					 
				}
				try {
					saveAreaDetailsofProperty();
				} catch (Exception e) {
				}
				uiflowdata.setPropDesc((String) slPropertyDesc.getValue());
				uiflowdata.setCustomername(tfCustomerName.getValue());
				uiflowdata.setMarketValue(XMLUtil.IndianFormat(new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""))));
				uiflowdata.setGuidelinevalue(XMLUtil
						.IndianFormat(new BigDecimal(tfGuidelineRate.getValue())));
				uiflowdata.setRealizablevalue(XMLUtil
						.IndianFormat(new BigDecimal(tfRealziableRate.getValue())));
				uiflowdata.setDistressvalue(XMLUtil
						.IndianFormat(new BigDecimal(tfDistressRate.getValue())));
				String numberOnly = tfFairMarketRate.getValue().replaceAll("[^\\d.]", "");
				uiflowdata.setAmountInWords(beanEvaluation
						.getAmountInWords(numberOnly));
				String numberOnly1 = tfGuidelineRate.getValue().replaceAll(
						"[^0-9]", "");
				uiflowdata.setAmountWordsGuideline(beanEvaluation.getAmountInWords(numberOnly1));
				/*List<CmCommonSetup> bill = BillGenerator.getEndValueDetails(numberOnly,headerid,loginusername,selectedBankid,selectCompanyid);
			//	bill.setBillNo(Long.toString(beanEvaluation.getNextSequnceId("seq_pem_evaldtls_docid")));
				uiflowdata.setBillDtls(bill);
				
				TPemCmBillDtls billDtls=BillGenerator.getBillDetails(numberOnly, headerid, loginusername, selectedBankid, selectCompanyid);
				uiflowdata.setBill(billDtls);
				*/
				BigDecimal realizable=new BigDecimal(0.00);
				BigDecimal distress=new BigDecimal(0.00);
				BigDecimal real=new BigDecimal("0.00");
				BigDecimal distre=new BigDecimal("0.00");
				
				try {
					real = new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
				} catch (Exception e) {
					real = new BigDecimal("0.00");
					 
				}
				try {
					distre = new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
				} catch (Exception e) {
					distre = new BigDecimal("0.00");
					 
				}
				try{
					realizable=real.multiply(new BigDecimal(95)).divide(new BigDecimal(100));
					realizable=realizable.subtract(realizable.remainder(new BigDecimal(1000)));
					tfRealziableRate.setValue(realizable.toString());
					uiflowdata.setRealizablevalue(XMLUtil.IndianFormat(new BigDecimal(realizable.toString())));
					
					distress=(distre.multiply(new BigDecimal(85))).divide(new BigDecimal(100));
					distress=distress.subtract(distress.remainder(new BigDecimal(1000)));
					tfDistressRate.setValue(distress.toString());
					uiflowdata.setDistressvalue(XMLUtil.IndianFormat(new BigDecimal(distress.toString())));
					}catch(Exception e){
						
					}
				
			} catch (Exception e) {
			e.printStackTrace();	 
			}

			try {
				saveValuationofLandDetails();
			} catch (Exception e) {
			}
			try {
				savePropertyValueDetails();
			} catch (Exception e) {
			}
			
		
			if(tfEvaluationNumber.isValid() && tfBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid()&&tfAdopetdMarketRate.isValid())
			{
				
				if(count ==0){
				beanEvaluation.saveorUpdateEvalDetails(evalobj);
				
				try {
					saveProductImageDetails();
				} catch (Exception e) {
					e.printStackTrace();

				}
				valid = true;
				}
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblSaveNotification.setValue(ApplicationConstants.saveMsg);
			}
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="LAND";
			uiflowdata.setEvalDtls(evalobj);
			ByteArrayOutputStream recvstram = XMLUtil.doMarshall(uiflowdata);
			XMLUtil.getWordDocument(recvstram, evalNumber+"_"+customer+"_"+propertyType,strXslFile);
			if(valid){
				/*populateAndConfig(false);
				resetAllFieldsFields();*/
				btnSave.setEnabled(false);
			}else{
				btnSave.setComponentError(new UserError("Form is invalid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
			lblSaveNotification
					.setValue("Saved failed, please check the data and try again ");
			logger.info("Error on SaveApproveReject Status function--->" + e);
			
		}
		
	}
	private void saveProductImageDetails(){
		try {
			beanPropImage.deleteExistingPropImage(headerid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TPemCmPropImage propObj=new TPemCmPropImage();
		
		System.out.println("filevalue--->"+filevalue);
		
		if(1==1)
		{
	try{
		File file = new File(basepath1);
			FileInputStream fin = new FileInputStream(file);
		    byte fileContent[] = new byte[(int)file.length()];
		    fin.read(fileContent);
		    fin.close();
		    propObj.setPropimage1(fileContent);
		    
		}catch(Exception e)
		{
			e.printStackTrace(); 
		}
		}
		else
		{
			propObj.setPropimage1(null);
		}
		System.out.println("filevalue1--->"+filevalue1);
		if(1==1)
		{
	try{
		File file2=new File(basepath2);
	    FileInputStream fin1 = new FileInputStream(file2);
	    byte fileContent1[] = new byte[(int)file2.length()];
	    fin1.read(fileContent1);
	    fin1.close();
	    propObj.setPropimage2(fileContent1);
		    
	    
	    
		}catch(Exception e)
		{
			e.printStackTrace(); 
		}
		}
		else
		{
			propObj.setPropimage2(null);
		}
		    
		propObj.setDocId(headerid);
	    propObj.setLastupdateddt(new Date());
	    propObj.setLastupdatedby(loginusername);
	    System.out.println("HeaderId--->"+headerid);
		beanPropImage.saveorUpdatePropImage(propObj);
		uiflowdata.getPropImage().add(propObj);
		
	}


	private void saveOwnerDetails() {
		try {
			beanOwner.deleteExistingOwnerDetails(headerid);
		} catch (Exception e) {
		}

		btnSave.setComponentError(null);
		Iterator<Component> myComps = layoutOwnerDetails1
				.getComponentIterator();
		int i = 1;
		while (myComps.hasNext()) {
			final Component component = myComps.next();

			if (component instanceof ComponentIterOwnerDetails) {

				ComponentIterOwnerDetails mycomponent = (ComponentIterOwnerDetails) component;
				TPemCmOwnerDetails obj1 = new TPemCmOwnerDetails();
				obj1.setDocId(headerid);
				obj1.setOrderNo(Long.valueOf(i));
				obj1.setFieldLabel(mycomponent.getOwnerName());
				obj1.setFieldValue(mycomponent.getOwnerAddr());
				obj1.setLastUpdatedBy(loginusername);
				obj1.setLastUpdatedDt(new Date());

				if (mycomponent.getOwnerName() != null &&mycomponent.getOwnerName()!= "Sri."
						&& mycomponent.getOwnerName().trim().length() > 0) {
					beanOwner.saveOwnerDetails(obj1);
					uiflowdata.getCustomer().add(obj1);
					i++;
				}
			}
		}

	}

	void saveAssetDetails() {
		try {
			beanAsset.deleteAssetDetails(headerid);
		} catch (Exception e) {

		}

		TPemCmAssetDetails obj = new TPemCmAssetDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPropertyAddress.getCaption());
		obj.setFieldValue(tfPropertyAddress.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanAsset.saveOrUpdateAssetDtls(obj);
		uiflowdata.getAssetDtls().add(obj);

		obj = new TPemCmAssetDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfLandMark.getCaption());
		obj.setFieldValue(tfLandMark.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanAsset.saveOrUpdateAssetDtls(obj);
		uiflowdata.getAssetDtls().add(obj);

		obj = new TPemCmAssetDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slPropertyDesc.getCaption());
		obj.setFieldValue((String) slPropertyDesc.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanAsset.saveOrUpdateAssetDtls(obj);
		uiflowdata.getAssetDtls().add(obj);

		obj = new TPemCmAssetDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfCustomerName.getCaption());
		obj.setFieldValue("Sri."+tfCustomerName.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanAsset.saveOrUpdateAssetDtls(obj);
		uiflowdata.getAssetDtls().add(obj);

		obj = new TPemCmAssetDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfCustomerAddr.getCaption());
		obj.setFieldValue(tfCustomerAddr.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanAsset.saveOrUpdateAssetDtls(obj);
		uiflowdata.getAssetDtls().add(obj);

		if (tfDynamicAsset1.getValue() != null
				&& tfDynamicAsset1.getValue().trim().length() > 0) {
			obj = new TPemCmAssetDetails();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicAsset1.getCaption());
			obj.setFieldValue(tfDynamicAsset1.getValue());
			obj.setOrderNo(6L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanAsset.saveOrUpdateAssetDtls(obj);
			uiflowdata.getAssetDtls().add(obj);
		}
		if (tfDynamicAsset2.getValue() != null
				&& tfDynamicAsset2.getValue().trim().length() > 0) {
			obj = new TPemCmAssetDetails();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicAsset2.getCaption());
			obj.setFieldValue(tfDynamicAsset2.getValue());
			obj.setOrderNo(7L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanAsset.saveOrUpdateAssetDtls(obj);
			uiflowdata.getAssetDtls().add(obj);
		}

	}

	void saveNormalDocuments() {

		try {

			try {
				beanDocument.deleteExistingPropDocDetails(headerid);
			} catch (Exception e) {
				logger.info("Error-->" + e);
			}

			btnSave.setComponentError(null);
			Iterator<Component> myComps = panelNormalDocumentDetails
					.getComponentIterator();
			int i = 1;
			while (myComps.hasNext()) {
				final Component component = myComps.next();

				if (component instanceof ComponentIteratorNormlDoc) {

					ComponentIteratorNormlDoc mycomponent = (ComponentIteratorNormlDoc) component;
					TPemCmPropDocDetails obj = new TPemCmPropDocDetails();
					obj.setDocid(headerid);
					obj.setOrderNo(Long.valueOf(i));
					obj.setFieldLabel(mycomponent.getNameofDocument());
					obj.setApprovalYN(mycomponent.getYesorNo());
					obj.setApproveAuth(mycomponent.getNameofAuthority());
					obj.setApproveRef(mycomponent.getApprovalNo());
					obj.setLastUpdatedBy(loginusername);
					obj.setLastUpdatedDt(new Date());

					if (mycomponent.getNameofDocument() != null) {
						beanDocument.saveorUpdatePropDocDetails(obj);
						uiflowdata.getDocument().add(obj);
						i++;
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error-->" + e);
		}

	}

	void saveLegalDocuments() {

		try {

			try {
				legalDoc.deleteExistingPropLegalDocs(headerid);
			} catch (Exception e) {
				logger.info("Error-->" + e);
			}

			btnSave.setComponentError(null);
			Iterator<Component> myComps = panelLegalDocumentDetails
					.getComponentIterator();
			int i = 1;
			while (myComps.hasNext()) {
				final Component component = myComps.next();

				if (component instanceof ComponentIteratorLegalDoc) {

					ComponentIteratorLegalDoc mycomponent = (ComponentIteratorLegalDoc) component;
					TPemCmPropLegalDocs obj = new TPemCmPropLegalDocs();
					obj.setDocId(headerid);
					obj.setOrderNo(Long.valueOf(i));
					obj.setFieldLabel(mycomponent.getNameofDocument());
					obj.setDocDated(mycomponent.getApprovalDate());
					obj.setDocNo(mycomponent.getReferenceNumber());
					obj.setLastUpdatedBy(loginusername);
					obj.setLastUpdatedDt(new Date());
					if (mycomponent.getNameofDocument() != null
							&& mycomponent.getNameofDocument().trim().length() > 0) {
						legalDoc.saveorUpdatePropLegalDocs(obj);
						uiflowdata.getLegalDoc().add(obj);
						i++;
					}
				}

			}
		} catch (Exception e) {
			logger.info("Error-->" + e);
		}

	}

	void saveAdjoinPropertyDetails() {

		try {

			try {
				beanAdjoin.deleteExistingPropAdjoinDtls(headerid);
			} catch (Exception e) {
				logger.info("Error-->" + e);
			}

			btnSave.setComponentError(null);
			Iterator<Component> myComps = panelAdjoinProperties.getComponentIterator();
			while (myComps.hasNext()) {
				final Component component = myComps.next();

				if (component instanceof ComponentIteratorAdjoinProperty) {

					ComponentIteratorAdjoinProperty mycomponent = (ComponentIteratorAdjoinProperty) component;

					List<TPemCmPropAdjoinDtls> getList = mycomponent
							.getAdjoinPropertyList();

					for (TPemCmPropAdjoinDtls oldobj : getList) {

						TPemCmPropAdjoinDtls obj = new TPemCmPropAdjoinDtls();
						obj.setDocId(headerid);
						obj.setGroupHdr(oldobj.getGroupHdr());
						obj.setFieldLabel(oldobj.getFieldLabel());
						obj.setAsPerDeed(oldobj.getAsPerDeed());
						obj.setAsPerPlan(oldobj.getAsPerPlan());
						obj.setAsAtSite(oldobj.getAsAtSite());
						obj.setDeedValue(oldobj.getDeedValue());
						obj.setSiteValue(oldobj.getSiteValue());
						obj.setPlanValue(oldobj.getPlanValue());
						obj.setLastUpdatedBy(loginusername);
						obj.setLastUpdatedDt(new Date());
						if(obj.getAsPerDeed()!=null){
							beanAdjoin.savePropAdjoinDtls(obj);
							uiflowdata.getAdjoinProperty().add(obj);
							}
					}
				}

			}
		} catch (Exception e) {
			logger.info("Error-->" + e);
		}

	}

	void saveDimensionValues() {

		try {

			try {
				beanDimension.deleteExistingPropDimension(headerid);
			} catch (Exception e) {
				logger.info("Error-->"+e);
			}

			btnSave.setComponentError(null);
			Iterator<Component> myComps = panelDimension.getComponentIterator();
			BigDecimal siteArea=new BigDecimal(0.00);
			while (myComps.hasNext()) {
				final Component component = myComps.next();
				int i=1;
				
				if (component instanceof ComponentIterDimensionofPlot) {

					ComponentIterDimensionofPlot mycomponent = (ComponentIterDimensionofPlot) component;
					List<TPemCmPropDimension> getList = mycomponent
							.getDimensionPropertyList();

					try {
						List<String> mylist = mycomponent.getLeastValaue();
						siteArea=siteArea.add(new BigDecimal(mylist.get(0).replaceAll("[^\\d.]", "")));
						tfNorthandSouth.setValue(mylist.get(1));
						tfSiteArea.setValue(siteArea.toString());
					} catch (Exception e) {
						 
						logger.info("Error-->" + e);
					}
					for (TPemCmPropDimension oldobj : getList) {
						
						
						TPemCmPropDimension obj = new TPemCmPropDimension();
						obj.setDocId(headerid);
						obj.setGroupHdr(oldobj.getGroupHdr());
						obj.setFieldLabel(oldobj.getFieldLabel());
						obj.setAsPerDeed(oldobj.getAsPerDeed());
						obj.setAsPerPlan(oldobj.getAsPerPlan());
						obj.setAsPerSite(oldobj.getAsPerSite());
						obj.setDeedValue(oldobj.getDeedValue());
						obj.setPlanValue(oldobj.getPlanValue());
						obj.setSiteValue(oldobj.getSiteValue());
						obj.setLastUpdatedBy(loginusername);
						obj.setLastUpdatedDt(new Date());
						if(obj.getDeedValue()!=null){
							beanDimension.saveorUpdatePropDimension(obj);
							uiflowdata.getDimension().add(obj);
						}
					}
				}

			}
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

	}

	void saveMatchingBoundaries() {
		try {
			beanmatchboundary.deleteExistingSydPropMatchBoundry(headerid);
		} catch (Exception e) {

		}

		TPemSydPropMatchBoundry obj = new TPemSydPropMatchBoundry();
		obj.setDocId(headerid);
		obj.setFieldLabel(slMatchingBoundary.getCaption());
		obj.setFieldValue((String) slMatchingBoundary.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanmatchboundary.saveorUpdateSydPropMatchBoundry(obj);
		uiflowdata.getBoundary().add(obj);

		obj = new TPemSydPropMatchBoundry();
		obj.setDocId(headerid);
		obj.setFieldLabel(slPlotDemarcated.getCaption());
		obj.setFieldValue((String) slPlotDemarcated.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanmatchboundary.saveorUpdateSydPropMatchBoundry(obj);
		uiflowdata.getBoundary().add(obj);

		obj = new TPemSydPropMatchBoundry();
		obj.setDocId(headerid);
		obj.setFieldLabel(slApproveLandUse.getCaption());
		obj.setFieldValue((String) slApproveLandUse.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanmatchboundary.saveorUpdateSydPropMatchBoundry(obj);
		uiflowdata.getBoundary().add(obj);

		obj = new TPemSydPropMatchBoundry();
		obj.setDocId(headerid);
		obj.setFieldLabel(slTypeofProperty.getCaption());
		obj.setFieldValue((String) slTypeofProperty.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanmatchboundary.saveorUpdateSydPropMatchBoundry(obj);
		uiflowdata.getBoundary().add(obj);

		if (tfDynamicmatching1.getValue() != null
				&& tfDynamicmatching1.getValue().trim().length() > 0) {
			obj = new TPemSydPropMatchBoundry();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicmatching1.getCaption());
			obj.setFieldValue((String) tfDynamicmatching1.getValue());
			obj.setOrderNo(5L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanmatchboundary.saveorUpdateSydPropMatchBoundry(obj);
			uiflowdata.getBoundary().add(obj);
		}

		if (tfDynamicmatching2.getValue() != null
				&& tfDynamicmatching2.getValue().trim().length() > 0) {
			obj = new TPemSydPropMatchBoundry();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicmatching2.getCaption());
			obj.setFieldValue((String) tfDynamicmatching2.getValue());
			obj.setOrderNo(6L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanmatchboundary.saveorUpdateSydPropMatchBoundry(obj);
			uiflowdata.getBoundary().add(obj);
		}

	}

	void saveTenureOccupayDetails() {
		try {
			beantenureOccupancy.deleteSynPropOccupancy(headerid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TPemSynPropOccupancy obj = new TPemSynPropOccupancy();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfStatusofTenure.getCaption());
		obj.setFieldValue((String) tfStatusofTenure.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beantenureOccupancy.saveorUpdateSynPropOccupancy(obj);
		uiflowdata.getPropertyOccupancy().add(obj);

		obj = new TPemSynPropOccupancy();
		obj.setDocId(headerid);
		obj.setFieldLabel(slOwnedorRent.getCaption());
		obj.setFieldValue((String) slOwnedorRent.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beantenureOccupancy.saveorUpdateSynPropOccupancy(obj);
		uiflowdata.getPropertyOccupancy().add(obj);

		obj =new TPemSynPropOccupancy();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfNoOfYears.getCaption());
		obj.setFieldValue((String) tfNoOfYears.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beantenureOccupancy.saveorUpdateSynPropOccupancy(obj);
		uiflowdata.getPropertyOccupancy().add(obj);

		obj =new TPemSynPropOccupancy();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfRelationship.getCaption());
		obj.setFieldValue((String) tfRelationship.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beantenureOccupancy.saveorUpdateSynPropOccupancy(obj);
		uiflowdata.getPropertyOccupancy().add(obj);

		if (tfDynamicTenure1.getValue() != null
				&& tfDynamicTenure1.getValue().trim().length() > 0) {
			obj =new TPemSynPropOccupancy();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicTenure1.getCaption());
			obj.setFieldValue((String) tfDynamicTenure1.getValue());
			obj.setOrderNo(5L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beantenureOccupancy.saveorUpdateSynPropOccupancy(obj);
			uiflowdata.getPropertyOccupancy().add(obj);
		}

		if (tfDynamicTenure2.getValue() != null
				&& tfDynamicTenure2.getValue().trim().length() > 0) {
			obj = new TPemSynPropOccupancy();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicTenure2.getCaption());
			obj.setFieldValue((String) tfDynamicTenure2.getValue());
			obj.setOrderNo(6L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beantenureOccupancy.saveorUpdateSynPropOccupancy(obj);
			uiflowdata.getPropertyOccupancy().add(obj);
		}
	}

	void saveConstructionDetails() {
		try {
			beanconstruction.deleteExistingBldngStgofcnstructn(headerid);
		} catch (Exception e) {

		}

		TPemCmBldngStgofcnstructn obj = new TPemCmBldngStgofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfStageofConst.getCaption());
		obj.setFieldValue((String) tfStageofConst.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanconstruction.saveBldngStgofcnstructn(obj);
		uiflowdata.getStgofConstn().add(obj);

		if (tfDynamicConstruction1.getValue() != null
				&& tfDynamicConstruction1.getValue().trim().length() > 0) {
			obj = new TPemCmBldngStgofcnstructn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicConstruction1.getCaption());
			obj.setFieldValue((String) tfDynamicConstruction1.getValue());
			obj.setOrderNo(2L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanconstruction.saveBldngStgofcnstructn(obj);
			uiflowdata.getStgofConstn().add(obj);
		}

		if (tfDynamicConstruction2.getValue() != null
				&& tfDynamicConstruction2.getValue().trim().length() > 0) {
			obj = new TPemCmBldngStgofcnstructn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicConstruction2.getCaption());
			obj.setFieldValue((String) tfDynamicConstruction2.getValue());
			obj.setOrderNo(3L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanconstruction.saveBldngStgofcnstructn(obj);
			uiflowdata.getStgofConstn().add(obj);
		}
	}

	void saveViolationDetails() {
		try {
			beanviolation.deleteSynPropViolation(headerid);
		} catch (Exception e) {

		}

		TPemSynPropViolation obj = new TPemSynPropViolation();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfAnyViolation.getCaption());
		obj.setFieldValue((String) tfAnyViolation.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanviolation.saveorUpdateSynPropViolation(obj);
		uiflowdata.getPropertyViolation().add(obj);

		if (tfDynamicViolation1.getValue() != null
				&& tfDynamicViolation1.getValue().trim().length() > 0) {
			obj = new TPemSynPropViolation();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicViolation1.getCaption());
			obj.setFieldValue((String) tfDynamicViolation1.getValue());
			obj.setOrderNo(2L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanviolation.saveorUpdateSynPropViolation(obj);
			uiflowdata.getPropertyViolation().add(obj);
		}

		if (tfDynamicViolation2.getValue() != null
				&& tfDynamicViolation2.getValue().trim().length() > 0) {
			obj = new TPemSynPropViolation();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicViolation2.getCaption());
			obj.setFieldValue((String) tfDynamicViolation2.getValue());
			obj.setOrderNo(3L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanviolation.saveorUpdateSynPropViolation(obj);
			uiflowdata.getPropertyViolation().add(obj);
		}
	}

	void saveAreaDetailsofProperty() {
		try {
			beanareadetails.deleteExistingSynPropAreaDtls(headerid);
		} catch (Exception e) {

		}

		TPemSynPropAreaDtls obj = new TPemSynPropAreaDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfSiteArea.getCaption());
		obj.setFieldValue((String) tfSiteArea.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanareadetails.saveorUpdateSynPropAreaDtls(obj);
		uiflowdata.getAreaDetails().add(obj);

		obj = new TPemSynPropAreaDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPlinthArea.getCaption());
		obj.setFieldValue((String) tfPlinthArea.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanareadetails.saveorUpdateSynPropAreaDtls(obj);
		uiflowdata.getAreaDetails().add(obj);

		obj = new TPemSynPropAreaDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfCarpetArea.getCaption());
		obj.setFieldValue((String) tfCarpetArea.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanareadetails.saveorUpdateSynPropAreaDtls(obj);
		uiflowdata.getAreaDetails().add(obj);

		obj = new TPemSynPropAreaDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfSalableArea.getCaption());
		obj.setFieldValue((String) tfSalableArea.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanareadetails.saveorUpdateSynPropAreaDtls(obj);
		uiflowdata.getAreaDetails().add(obj);

		obj = new TPemSynPropAreaDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfRemarks.getCaption());
		obj.setFieldValue((String) tfRemarks.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanareadetails.saveorUpdateSynPropAreaDtls(obj);
		uiflowdata.getAreaDetails().add(obj);

		if (tfDynamicAreaDetail1.getValue() != null
				&& tfDynamicAreaDetail1.getValue().trim().length() > 0) {
			obj = new TPemSynPropAreaDtls();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicAreaDetail1.getCaption());
			obj.setFieldValue((String) tfDynamicAreaDetail1.getValue());
			obj.setOrderNo(6L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanareadetails.saveorUpdateSynPropAreaDtls(obj);
			uiflowdata.getAreaDetails().add(obj);
		}

		if (tfDynamicAreaDetail2.getValue() != null
				&& tfDynamicAreaDetail2.getValue().trim().length() > 0) {
			obj = new TPemSynPropAreaDtls();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicAreaDetail2.getCaption());
			obj.setFieldValue((String) tfDynamicAreaDetail2.getValue());
			obj.setOrderNo(7L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanareadetails.saveorUpdateSynPropAreaDtls(obj);
			uiflowdata.getAreaDetails().add(obj);
		}
	}

	void saveValuationofLandDetails() {
		try {
			beanlandvaluation.deleteExistingLandValutnData(headerid);
		} catch (Exception e) {

		}

		TPemCmLandValutnData obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfAreaofLand.getCaption());
		obj.setFieldValue((String) tfAreaofLand.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);
		uiflowdata.getLandval().add(obj);

		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfNorthandSouth.getCaption());
		obj.setFieldValue((String) tfNorthandSouth.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);
		uiflowdata.getLandval().add(obj);

		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfMarketRate.getCaption());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfMarketRate.getValue()))+"/cent");
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);
		uiflowdata.getLandval().add(obj);

		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfAdopetdMarketRate.getCaption());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfAdopetdMarketRate.getValue()))+"/cent");
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);
		uiflowdata.getLandval().add(obj);

		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFairMarketRate.getCaption());
		obj.setFieldValue("Rs. "+tfFairMarketRate.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);
		uiflowdata.getLandval().add(obj);
		if(tfDynamicValuation1.getValue()!=null&&tfDynamicValuation1.getValue().trim().length()>0)
		{
		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicValuation1.getCaption());
		obj.setFieldValue((String) tfDynamicValuation1.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);
		uiflowdata.getLandval().add(obj);
		}
		if(tfDynamicValuation2.getValue()!=null&&tfDynamicValuation2.getValue().trim().length()>0)
		{
		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicValuation2.getCaption());
		obj.setFieldValue((String) tfDynamicValuation2.getValue());
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);
		uiflowdata.getLandval().add(obj);
		}
		uiflowdata.setMarketValue(tfFairMarketRate.getValue());
	}

	void saveGuidelineValue() {

		try {
			beanguidelinevalue.deleteExistingCmPropGuidlnValue(headerid);
		} catch (Exception e) {
			logger.info("Error-->" + e);
		}

		btnSave.setComponentError(null);
		Iterator<Component> myComps = layoutGuideline.getComponentIterator();
		int i = 1;
		BigDecimal guiderate = new BigDecimal(0.00);
		BigDecimal guiderate1=new BigDecimal(0.00);
		while (myComps.hasNext()) {
			final Component component = myComps.next();

			if (component instanceof ComponentIterGuideline) {
				
				ComponentIterGuideline mycomponent = (ComponentIterGuideline) component;
				TPemCmPropGuidlnValue obj = new TPemCmPropGuidlnValue();
				obj.setDocId(headerid);
				obj.setOrderNo(Long.valueOf(i));
				obj.setFieldLabel(mycomponent.getDescription());
				obj.setArea(mycomponent.getArea());
				obj.setRate(mycomponent.getRate());
				obj.setAmount(XMLUtil.IndianFormat(new BigDecimal(mycomponent.getAmount())));
				obj.setLastUpdatedBy(loginusername);
				obj.setLastUpdatedDt(new Date());
				guiderate = guiderate.add(new BigDecimal(mycomponent
						.getAmount()));
				if(i==1){
					guiderate1=guiderate1.add(new BigDecimal(mycomponent
						.getAmount()));
					uiflowdata.setGuideland(guiderate1.toString());
				}
				if (mycomponent.getDescription() != null) {
					beanguidelinevalue.saveorUpdatePropGuidlnValue(obj);
					uiflowdata.getGuideline().add(obj);
					i++;
				}

				tfGuidelineRate.setValue(guiderate.toString());
			}

		}
	}

	void saveGuidelineReferenceDetails() {
		try {
			beanguidelinereference.deleteExistingPropGuidlnRefdata(headerid);
		} catch (Exception e) {

		}

		TPemCmPropGuidlnRefdata obj = new TPemCmPropGuidlnRefdata();
		obj.setDocId(headerid);
		obj.setZone(tfZone.getValue());
		obj.setSro(tfSRO.getValue());
		obj.setVillage(tfVillage.getValue());
		obj.setDistrict(tfRevnueDist.getValue());
		obj.setTaluk(tfTalukName.getValue());
		obj.setStreetName(tfStreetName.getValue());
		obj.setGuidelineValue(tfGuidelineValue.getValue());
		obj.setGuidelineMatric(tfGuidelineValueMatric.getValue());
		obj.setClassification(slClassification.getValue());
		obj.setFieldLabel((String)slStreetSerNo.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanguidelinereference.savorUpdateePropGuidlnRefdata(obj);
		uiflowdata.getGuidelineref().add(obj);

	}

	void savePropertyValueDetails() {
		try {
			beanPropertyvalue.deleteExistingPropValtnSummry(headerid);
		} catch (Exception e) {

		}
		TPemCmPropValtnSummry obj = new TPemCmPropValtnSummry();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFairMarketRate.getCaption());
		obj.setFieldValue((String) tfFairMarketRate.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropertyvalue.saveorUpdatePropValtnSummry(obj);
		uiflowdata.getPropertyValue().add(obj);

		obj = new TPemCmPropValtnSummry();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfRealziableRate.getCaption());
		obj.setFieldValue((String) tfRealziableRate.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropertyvalue.saveorUpdatePropValtnSummry(obj);
		uiflowdata.getPropertyValue().add(obj);

		obj = new TPemCmPropValtnSummry();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDistressRate.getCaption());
		obj.setFieldValue(tfDistressRate.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropertyvalue.saveorUpdatePropValtnSummry(obj);
		uiflowdata.getPropertyValue().add(obj);

		obj = new TPemCmPropValtnSummry();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfGuidelineRate.getCaption());
		obj.setFieldValue(tfGuidelineRate.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropertyvalue.saveorUpdatePropValtnSummry(obj);
		uiflowdata.getPropertyValue().add(obj);
	}

	private void editDetails() {

		try {

			Item itselect = tblEvalDetails.getItem(tblEvalDetails.getValue());
			if (itselect != null) {
				TPemCmEvalDetails edit = beans.getItem(tblEvalDetails.getValue()).getBean();
				
				btnSave.setCaption("Update");
				// edit evaluation details
				beans.getItem(tblEvalDetails.getValue()).getBean();
				headerid = (Long) itselect.getItemProperty("docId").getValue();
				tfEvaluationNumber.setReadOnly(false);
				tfEvaluationNumber.setValue((String) itselect.getItemProperty(
						"evalNo").getValue());
				tfEvaluationNumber.setReadOnly(true);
				tfEvaluationPurpose.setValue((String) itselect.getItemProperty(
						"evalPurpose").getValue());
				if(edit.getEvalDate()!=null && edit.getEvalDate().trim().length()>0 )
				{
				dfDateofValuation.setValue(new Date(edit.getEvalDate()));
				}else{
					dfDateofValuation.setValue(null);
					
				}
					
				tfValuatedBy.setValue((String) itselect.getItemProperty(
						"valuationBy").getValue());
				if(edit.getCheckedDt()!=null && edit.getCheckedDt().trim().length()>0 )
				{
					dfVerifiedDate.setValue(new Date(edit.getCheckedDt()));
				}else{
					dfVerifiedDate.setValue(null);
					
				}
				tfVerifiedBy.setValue((String) itselect.getItemProperty(
						"checkedBy").getValue());
				tfBankBranch.setValue((String) itselect.getItemProperty(
						"bankBranch").getValue());
				if (itselect.getItemProperty("customValue1").getValue() != null) {
					tfDynamicEvaluation1.setValue((String) itselect
							.getItemProperty("customValue1").getValue());
					tfDynamicEvaluation1.setCaption((String) itselect
							.getItemProperty("customLbl1").getValue());
					tfDynamicEvaluation1.setVisible(true);
				}

				if (itselect.getItemProperty("customValue2").getValue() != null) {

					if (itselect.getItemProperty("customValue1").getValue() == null) {
						tfDynamicEvaluation1.setValue((String) itselect
								.getItemProperty("customValue2").getValue());
						tfDynamicEvaluation1.setCaption((String) itselect
								.getItemProperty("customLbl2").getValue());
						tfDynamicEvaluation1.setVisible(true);
					} else {
						tfDynamicEvaluation2.setValue((String) itselect
								.getItemProperty("customValue2").getValue());
						tfDynamicEvaluation2.setCaption((String) itselect
								.getItemProperty("customLbl2").getValue());
						tfDynamicEvaluation2.setVisible(true);
					}
				}

			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		try {
			// for customer details
			editOwnerDetails();
		} catch (Exception e) {
			
		}

		try {
			// for edit asset details
			editAssetDetails();
		} catch (Exception e) {
			
		}

		try {
			editDocumentsDetails();
		} catch (Exception e) {
			
		}

		try {
			editLegalDocuments();
		} catch (Exception e) {
			
		}

		try {
			editAdjoinProperties();
		} catch (Exception e) {
			
		}

		try {
			editDimensionDetails();
		} catch (Exception e) {
			
		}
		try {
			editMatchBoundaries();
		} catch (Exception e) {
			
		}

		try {
			editTenureOccupancyDetails();
		} catch (Exception e) {
			
		}

		try {
			editConstructionDetails();
		} catch (Exception e) {
			
		}

		try {
			editViolationDetails();
		} catch (Exception e) {
			
		}

		try {
			editAreaDetails();
		} catch (Exception e) {
			
		}

		try {
			editLandValuationDetails();
		} catch (Exception e) {
			
		}

		try {
			editGuidelinevalueDetails();
		} catch (Exception e) {
			
		}

		try {
			editGuidelineReferenceValues();
		} catch (Exception e) {
			
		}
		try {
			editPropertyValueDetails();
		} catch (Exception e) {
			
		}
		try {
			editPropertyImageDetails();
		} catch (Exception e) {
			
		}

	}

	void editPropertyImageDetails(){
		try{
			List<TPemCmPropImage> imageList=beanPropImage.getPropImageList(headerid);
			TPemCmPropImage propObj = imageList.get(0);
			if(propObj.getPropimage1()!=null){
				byte[] myimage=(byte[]) propObj.getPropimage1();
				
				
				UploadUI test=new UploadUI(hlImageLayout1);
				hlImageLayout1.addComponent(btnImgDownload);
				test.dispayImage(myimage);
				}else{
					
				try{
					new UploadUI(hlImageLayout1);
					}catch(Exception e){
						
					}
				}
			
			if(propObj.getPropimage2()!=null){
				byte[] myimage1=(byte[]) propObj.getPropimage2();
				
				
				UploadUI2 test=new UploadUI2(hlImageLayout2);
				hlImageLayout2.addComponent(btnImg1Download);
				test.dispayImage(myimage1);
				}else{
					
				try{
					new UploadUI2(hlImageLayout2);
					}catch(Exception e){
						
						
					}
				}

			StreamResource sr = getImgStream();
			FileDownloader fileDownloader = new FileDownloader(sr);
			fileDownloader.extend(btnImgDownload);
			
			StreamResource sr1 = getImgStream1();
			FileDownloader fileDownloader1 = new FileDownloader(sr1);
			fileDownloader1.extend(btnImg1Download);
		
		}catch(Exception e){
			 
		}
	}
/*	for Image Download*/
	private StreamResource getImgStream()  {
		List<TPemCmPropImage> list=beanPropImage.getPropImageList(headerid);
		for(final TPemCmPropImage obj:list){
		StreamResource.StreamSource source = new StreamResource.StreamSource() {
			private static final long serialVersionUID = 1L;

			public InputStream getStream() {
			
				if (obj.getPropimage1() != null) {

					return new ByteArrayInputStream(obj.getPropimage1());
				} else {
					return null;
				}}};
		StreamResource resource = new StreamResource(source, obj.getDocId()
				+ ".png");
		return resource;
		}
		return null;
	}

	private StreamResource getImgStream1() {
		System.out.println("File Name++++++++++++"+new File(basepath1).getName());
		List<TPemCmPropImage> list=beanPropImage.getPropImageList(headerid);
		for(final TPemCmPropImage obj:list){
		StreamResource.StreamSource source = new StreamResource.StreamSource() {
			private static final long serialVersionUID = 1L;

			public InputStream getStream() {
			
				if (obj.getPropimage2() != null) {

					return new ByteArrayInputStream(obj.getPropimage2());
				} else {
					return null;
				}}};
		StreamResource resource = new StreamResource(source, obj.getImgId()
				+ ".png");
		return resource;
		}
		return null;
	}
	
	void editOwnerDetails() {
		try {
			List<TPemCmOwnerDetails> custlist = beanOwner.getOwnerDtlsList(headerid);

			layoutOwnerDetails1.removeAllComponents();

			for (TPemCmOwnerDetails obj : custlist) {

				layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails(
						obj.getFieldLabel(), obj.getFieldValue()));
			}
		} catch (Exception e) {

		}

	}

	void editAssetDetails() {
		try {
			List<TPemCmAssetDetails> assetlist = beanAsset.getAssetDetailsList(headerid);
			TPemCmAssetDetails obj1 = assetlist.get(0);
			tfPropertyAddress.setValue(obj1.getFieldValue());
			tfPropertyAddress.setCaption(obj1.getFieldLabel());
			obj1 = assetlist.get(1);
			tfLandMark.setValue(obj1.getFieldValue());
			tfLandMark.setCaption(obj1.getFieldLabel());
			obj1 = assetlist.get(2);
			slPropertyDesc.setValue(obj1.getFieldValue());
			slPropertyDesc.setCaption(obj1.getFieldLabel());
			obj1 = assetlist.get(3);
			tfCustomerName.setValue(obj1.getFieldValue().replace("Sri.", ""));
			tfCustomerName.setCaption(obj1.getFieldLabel());
			obj1 = assetlist.get(4);
			tfCustomerAddr.setValue(obj1.getFieldValue());
			tfCustomerAddr.setCaption(obj1.getFieldLabel());
			obj1 = assetlist.get(5);
			tfDynamicAsset1.setValue(obj1.getFieldValue());
			tfDynamicAsset1.setCaption(obj1.getFieldLabel());
			tfDynamicAsset1.setVisible(true);
			obj1 = assetlist.get(6);
			tfDynamicAsset2.setValue(obj1.getFieldValue());
			tfDynamicAsset2.setCaption(obj1.getFieldLabel());
			tfDynamicAsset2.setVisible(true);

		} catch (Exception e) {

		}
	}

	void editDocumentsDetails() {
		List<TPemCmPropDocDetails> doclist = beanDocument.getPropDocDetailsList(headerid);
		panelNormalDocumentDetails.removeAllComponents();
		panelNormalDocumentDetails.addComponent(btnAddNorDoc);
		panelNormalDocumentDetails.setComponentAlignment(btnAddNorDoc,
				Alignment.BOTTOM_RIGHT);
		for (TPemCmPropDocDetails obj : doclist) {
			panelNormalDocumentDetails
					.addComponent(new ComponentIteratorNormlDoc(obj.getFieldLabel(), obj.getApprovalYN(), obj
							.getApproveAuth(), obj.getApproveRef()));
		}

	}

	void editLegalDocuments() {
		List<TPemCmPropLegalDocs> doclist = legalDoc.getPropLegalDocsList(headerid);
		panelLegalDocumentDetails.removeAllComponents();
		panelLegalDocumentDetails.addComponent(btnAddLegalDoc);
		panelLegalDocumentDetails.setComponentAlignment(btnAddLegalDoc,
				Alignment.BOTTOM_RIGHT);
		for (TPemCmPropLegalDocs obj : doclist) {
			panelLegalDocumentDetails
					.addComponent(new ComponentIteratorLegalDoc(obj
							.getFieldLabel(), obj.getDocNo(), obj.getDocDated()));
		}
	}

	void editAdjoinProperties() {
		List<TPemCmPropAdjoinDtls> adjoinList = beanAdjoin.getPropAdjoinDtlsList(headerid);
		List<AdjoinPropertyList> adjoininputList = new ArrayList<AdjoinPropertyList>();

		try {
			for (int i = 0; i < adjoinList.size(); i = i + 4) {
				AdjoinPropertyList adjoinListObj = new AdjoinPropertyList();

				TPemCmPropAdjoinDtls obj1 = adjoinList.get(i);
				adjoinListObj.setGroupLabel(obj1.getGroupHdr());
				adjoinListObj.setDirectionNorthLabel(obj1.getFieldLabel());
				adjoinListObj.setNorthDeedValue(obj1.getAsPerDeed());
				adjoinListObj.setNorthSiteValue(obj1.getAsAtSite());
				adjoinListObj.setNorthPlanValue(obj1.getAsPerPlan());
				adjoinListObj.setDeed(obj1.getDeedValue());
				adjoinListObj.setSite(obj1.getSiteValue());
				adjoinListObj.setPlan(obj1.getPlanValue());

				obj1 = adjoinList.get(i + 1);
				adjoinListObj.setGroupLabel(obj1.getGroupHdr());
				adjoinListObj.setDirectionSouthLabel(obj1.getFieldLabel());
				adjoinListObj.setSouthDeedValue(obj1.getAsPerDeed());
				adjoinListObj.setSouthSiteValue(obj1.getAsAtSite());
				adjoinListObj.setSouthPlanValue(obj1.getAsPerPlan());
				adjoinListObj.setDeed(obj1.getDeedValue());
				adjoinListObj.setSite(obj1.getSiteValue());
				adjoinListObj.setPlan(obj1.getPlanValue());

				obj1 = adjoinList.get(i + 2);
				adjoinListObj.setGroupLabel(obj1.getGroupHdr());
				adjoinListObj.setDirectionEastLabel(obj1.getFieldLabel());
				adjoinListObj.setEastDeedValue(obj1.getAsPerDeed());
				adjoinListObj.setEastSiteValue(obj1.getAsAtSite());
				adjoinListObj.setEastPlanValue(obj1.getAsPerPlan());
				adjoinListObj.setDeed(obj1.getDeedValue());
				adjoinListObj.setSite(obj1.getSiteValue());
				adjoinListObj.setPlan(obj1.getPlanValue());

				obj1 = adjoinList.get(i + 3);
				adjoinListObj.setGroupLabel(obj1.getGroupHdr());
				adjoinListObj.setDirectionWestLabel(obj1.getFieldLabel());
				adjoinListObj.setWestDeedValue(obj1.getAsPerDeed());
				adjoinListObj.setWestSiteValue(obj1.getAsAtSite());
				adjoinListObj.setWestPlanValue(obj1.getAsPerPlan());
				adjoinListObj.setDeed(obj1.getDeedValue());
				adjoinListObj.setSite(obj1.getSiteValue());
				adjoinListObj.setPlan(obj1.getPlanValue());

				adjoininputList.add(adjoinListObj);

			}
		} catch (Exception e) {
			
		}

		try {
			panelAdjoinProperties.removeAllComponents();
			panelAdjoinProperties.addComponent(btnAddAdjoinProperty);
			panelAdjoinProperties.setComponentAlignment(btnAddAdjoinProperty,
					Alignment.BOTTOM_RIGHT);

			for (AdjoinPropertyList inpobj : adjoininputList) {
				panelAdjoinProperties
						.addComponent(new ComponentIteratorAdjoinProperty(
								inpobj, true, true, true));

			}

		} catch (Exception e) {
			
		}
	}

	void editDimensionDetails() {
		List<TPemCmPropDimension> adjoinList = beanDimension.getPropDimensionList(headerid);
		List<DimensionList> dimeninputList = new ArrayList<DimensionList>();

		try {
			for (int i = 0; i < adjoinList.size(); i = i + 9) {
				DimensionList dimenListObj = new DimensionList();

				try{
				TPemCmPropDimension obj1 = adjoinList.get(i);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionNorthLabel(obj1.getFieldLabel());
				dimenListObj.setNorthDeedValue(obj1.getAsPerDeed());
				dimenListObj.setNorthSiteValue(obj1.getAsPerSite());
				dimenListObj.setNorthPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());

				obj1 = adjoinList.get(i + 1);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionSouthLabel(obj1.getFieldLabel());
				dimenListObj.setSouthDeedValue(obj1.getAsPerDeed());
				dimenListObj.setSouthSiteValue(obj1.getAsPerSite());
				dimenListObj.setSouthPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());

				obj1 = adjoinList.get(i + 2);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionEastLabel(obj1.getFieldLabel());
				dimenListObj.setEastDeedValue(obj1.getAsPerDeed());
				dimenListObj.setEastSiteValue(obj1.getAsPerSite());
				dimenListObj.setEastPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());

				obj1 = adjoinList.get(i + 3);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionWestLabel(obj1.getFieldLabel());
				dimenListObj.setWestDeedValue(obj1.getAsPerDeed());
				dimenListObj.setWestSiteValue(obj1.getAsPerSite());
				dimenListObj.setWestPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());

				
				obj1 = adjoinList.get(i + 4);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionDynamic1(obj1.getFieldLabel());
				dimenListObj.setDynamicdeedvalue1(obj1.getAsPerDeed());
				dimenListObj.setDynamicsitevalue1(obj1.getAsPerSite());
				dimenListObj.setDynamicplanvalue1(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());
				
				obj1 = adjoinList.get(i + 5);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionDynamic2(obj1.getFieldLabel());
				dimenListObj.setDynamicdeedvalue2(obj1.getAsPerDeed());
				dimenListObj.setDynamicsitevalue2(obj1.getAsPerSite());
				dimenListObj.setDynamicplanvalue2(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());
				
				obj1 = adjoinList.get(i + 6);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionDynamic3(obj1.getFieldLabel());
				dimenListObj.setDynamicdeedvalue3(obj1.getAsPerDeed());
				dimenListObj.setDynamicsitevalue3(obj1.getAsPerSite());
				dimenListObj.setDynamicplanvalue3(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());
				
				obj1 = adjoinList.get(i + 7);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionDynamic4(obj1.getFieldLabel());
				dimenListObj.setDynamicdeedvalue4(obj1.getAsPerDeed());
				dimenListObj.setDynamicsitevalue4(obj1.getAsPerSite());
				dimenListObj.setDynamicplanvalue4(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());
				
				obj1 = adjoinList.get(i + 8);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setExtentLabel(obj1.getFieldLabel());
				dimenListObj.setExtentDeedValue(obj1.getAsPerDeed());
				dimenListObj.setExtentSiteValue(obj1.getAsPerSite());
				dimenListObj.setExtentPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());
				}catch(Exception e){}
				
				dimeninputList.add(dimenListObj);

			}
		} catch (Exception e) {
			
		}

		try {
			panelDimension.removeAllComponents();
			panelDimension.addComponent(btnAddDimension);
			panelDimension.setComponentAlignment(btnAddDimension,
					Alignment.BOTTOM_RIGHT);

			for (DimensionList inpobj : dimeninputList) {

				panelDimension.addComponent(new ComponentIterDimensionofPlot(
						inpobj, true, true, true));
			}

		} catch (Exception e) {
			
		}
	}

	void editMatchBoundaries() {
		try {
			List<TPemSydPropMatchBoundry> list = beanmatchboundary.getSydPropMatchBoundryList(headerid);
			TPemSydPropMatchBoundry obj1 = list.get(0);
			slMatchingBoundary.setValue(obj1.getFieldValue());
			slMatchingBoundary.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			slPlotDemarcated.setValue(obj1.getFieldValue());
			slPlotDemarcated.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			slApproveLandUse.setValue(obj1.getFieldValue());
			slApproveLandUse.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			slTypeofProperty.setValue(obj1.getFieldValue());
			slTypeofProperty.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			tfDynamicmatching1.setValue(obj1.getFieldValue());
			tfDynamicmatching1.setCaption(obj1.getFieldLabel());
			tfDynamicmatching1.setVisible(true);
			obj1 = list.get(5);
			tfDynamicmatching2.setValue(obj1.getFieldValue());
			tfDynamicmatching2.setCaption(obj1.getFieldLabel());
			tfDynamicmatching2.setVisible(true);

		} catch (Exception e) {

			
		}
	}

	void editTenureOccupancyDetails() {
		try {
			List<TPemSynPropOccupancy> list = beantenureOccupancy.getSynPropOccupancyList(headerid);
			TPemSynPropOccupancy obj1 = list.get(0);
			tfStatusofTenure.setValue(obj1.getFieldValue());
			tfStatusofTenure.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			slOwnedorRent.setValue(obj1.getFieldValue());
			slOwnedorRent.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfNoOfYears.setValue(obj1.getFieldValue());
			tfNoOfYears.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfRelationship.setValue(obj1.getFieldValue());
			tfRelationship.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			tfDynamicTenure1.setValue(obj1.getFieldValue());
			tfDynamicTenure1.setCaption(obj1.getFieldLabel());
			tfDynamicTenure1.setVisible(true);
			obj1 = list.get(5);
			tfDynamicTenure2.setValue(obj1.getFieldValue());
			tfDynamicTenure2.setCaption(obj1.getFieldLabel());
			tfDynamicTenure2.setVisible(true);

		} catch (Exception e) {

			
		}
	}

	void editConstructionDetails() {
		try {
			List<TPemCmBldngStgofcnstructn> list = beanconstruction.getBldgStgofcnstList(headerid);
			TPemCmBldngStgofcnstructn obj1 = list.get(0);
			tfStageofConst.setValue(obj1.getFieldValue());
			tfStageofConst.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfDynamicConstruction1.setValue(obj1.getFieldValue());
			tfDynamicConstruction1.setCaption(obj1.getFieldLabel());
			tfDynamicConstruction1.setVisible(true);
			obj1 = list.get(2);
			tfDynamicConstruction2.setValue(obj1.getFieldValue());
			tfDynamicConstruction2.setCaption(obj1.getFieldLabel());
			tfDynamicConstruction2.setVisible(true);

		} catch (Exception e) {
			
		}
	}

	void editViolationDetails() {
		try {
			List<TPemSynPropViolation> list = beanviolation.getSynPropViolation(headerid);
			TPemSynPropViolation obj1 = list.get(0);
			tfAnyViolation.setValue(obj1.getFieldValue());
			tfAnyViolation.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfDynamicViolation1.setValue(obj1.getFieldValue());
			tfDynamicViolation1.setCaption(obj1.getFieldLabel());
			tfDynamicViolation1.setVisible(true);
			obj1 = list.get(2);
			tfDynamicViolation2.setValue(obj1.getFieldValue());
			tfDynamicViolation2.setCaption(obj1.getFieldLabel());
			tfDynamicViolation2.setVisible(true);
		} catch (Exception e) {
			
		}
	}

	void editAreaDetails() {
		try {
			List<TPemSynPropAreaDtls> list = beanareadetails.getSynPropAreaDtlsList(headerid);
			TPemSynPropAreaDtls obj1 = list.get(0);
			tfSiteArea.setValue(obj1.getFieldValue());
			tfSiteArea.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfPlinthArea.setValue(obj1.getFieldValue());
			tfPlinthArea.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfCarpetArea.setValue(obj1.getFieldValue());
			tfCarpetArea.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfSalableArea.setValue(obj1.getFieldValue());
			tfSalableArea.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			tfRemarks.setValue(obj1.getFieldValue());
			tfRemarks.setCaption(obj1.getFieldLabel());
			obj1 = list.get(5);
			tfDynamicAreaDetail1.setValue(obj1.getFieldValue());
			tfDynamicAreaDetail1.setCaption(obj1.getFieldLabel());
			tfDynamicAreaDetail1.setVisible(true);
			obj1 = list.get(6);
			tfDynamicAreaDetail2.setValue(obj1.getFieldValue());
			tfDynamicAreaDetail2.setCaption(obj1.getFieldLabel());
			tfDynamicAreaDetail2.setVisible(true);

		} catch (Exception e) {

			
		}
	}

	void editLandValuationDetails() {
		try {
			List<TPemCmLandValutnData> list = beanlandvaluation.getLandValutnDataList(headerid);
			TPemCmLandValutnData obj1 = list.get(0);
			tfAreaofLand.setValue(obj1.getFieldValue());
			tfAreaofLand.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfNorthandSouth.setValue(obj1.getFieldValue());
			tfNorthandSouth.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfMarketRate.setValue(obj1.getFieldValue().replace("Rs. ","").replace("/cent","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			tfMarketRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfAdopetdMarketRate.setValue(obj1.getFieldValue().replace("Rs. ","").replace("/cent","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			tfAdopetdMarketRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			tfFairMarketRate.setValue(obj1.getFieldValue().replace("Rs. ",""));
			tfFairMarketRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(5);
			tfDynamicValuation1.setValue(obj1.getFieldValue());
			tfDynamicValuation1.setCaption(obj1.getFieldLabel());
			tfDynamicValuation1.setVisible(true);
			obj1 = list.get(6);
			tfDynamicValuation2.setValue(obj1.getFieldValue());
			tfDynamicValuation2.setCaption(obj1.getFieldLabel());
			tfDynamicValuation2.setVisible(true);

		} catch (Exception e) {

			logger.info("Error-->"+e);
		}
	}

	void editGuidelinevalueDetails() {

		List<TPemCmPropGuidlnValue> guideList = beanguidelinevalue.getPropGuidlnRefdataList(headerid);

		layoutGuideline.removeAllComponents();
		layoutGuideline.addComponent(btnAddGuideline);
		layoutGuideline.setComponentAlignment(btnAddGuideline,
				Alignment.BOTTOM_RIGHT);
		for (TPemCmPropGuidlnValue obj : guideList) {

			layoutGuideline.addComponent(new ComponentIterGuideline(obj
					.getFieldLabel(), obj.getArea(), obj.getRate(), obj
					.getAmount().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", "")));
		}
	}

	void editGuidelineReferenceValues() {
		List<TPemCmPropGuidlnRefdata> list = beanguidelinereference.getPropGuidlnRefdataList(headerid);
		for (TPemCmPropGuidlnRefdata obj : list) {
			tfZone.setValue(obj.getZone());
			tfSRO.setValue(obj.getSro());
			tfVillage.setValue(obj.getVillage());
			tfRevnueDist.setValue(obj.getDistrict());
			tfTalukName.setValue(obj.getTaluk());
			tfStreetName.setValue(obj.getStreetName());
			tfGuidelineValue.setValue(obj.getGuidelineValue());
			tfGuidelineValueMatric.setValue(obj.getGuidelineMatric());
			slClassification.setValue(obj.getClassification());
			slStreetSerNo.setValue(obj.getFieldLabel());

		}
	}

	void editPropertyValueDetails() {

		try {
			List<TPemCmPropValtnSummry> list = beanPropertyvalue.getPropValtnSummryList(headerid);
			TPemCmPropValtnSummry obj1 = list.get(1);
			tfRealziableRate.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			tfRealziableRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfDistressRate.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			tfDistressRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfGuidelineRate.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			tfGuidelineRate.setCaption(obj1.getFieldLabel());

		} catch (Exception e) {

			
		}

	}

	void loadComponentListValues() {
		loadPropertyDescList();

		slMatchingBoundary.addItem(Common.YES_DESC);
		slMatchingBoundary.addItem(Common.NO_DESC);

		slPlotDemarcated.addItem(Common.YES_DESC);
		slPlotDemarcated.addItem(Common.NO_DESC);
		loadTypeofProperty();
		loadOwnedorRented();
		loadBankBranchDetails();
	}
	void loadBankBranchDetails(){
		List<String> list = beanBankConst.getBankConstantList("BRANCH_CODE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfBankBranch.setContainerDataSource(childAccounts);
		tfSearchBankbranch.setContainerDataSource(childAccounts);
	}
	void loadPropertyDescList() {
		List<String> list = beanBankConst.getBankConstantList("PROP_DESC",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slPropertyDesc.setContainerDataSource(childAccounts);
	}

	void loadTypeofProperty() {
		List<String> list = beanBankConst.getBankConstantList("RIC",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slApproveLandUse.setContainerDataSource(childAccounts);
		slTypeofProperty.setContainerDataSource(childAccounts);
	}

	void loadOwnedorRented() {
		List<String> list = beanBankConst.getBankConstantList("OWNRENT",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slOwnedorRent.setContainerDataSource(childAccounts);
	}
	private void resetFields() {

		lblNotificationIcon.setIcon(null);
		lblSaveNotification.setValue("");
		hlBreadCrumbs.setVisible(false);
		lblFormTittle.setVisible(true);
		
	}

	void resetAllFieldsFields() {
		btnSave.setCaption("Save");
		tfEvaluationNumber.setComponentError(null);
		tfBankBranch.setComponentError(null);
		dfDateofValuation.setComponentError(null);
		tfEvaluationPurpose.setComponentError(null);
		tfCustomerName.setComponentError(null);
		tfAdopetdMarketRate.setComponentError(null);
		tfEvaluationNumber.setReadOnly(false);
		tfEvaluationNumber.setValue("");
		tfBankBranch.setValue(null);
		tfEvaluationPurpose.setValue("Collateral Security to the Bank");
		dfDateofValuation.setValue(null);
		tfValuatedBy.setValue("");
		dfVerifiedDate.setValue(null);
		tfVerifiedBy.setValue("");
		tfDynamicEvaluation1.setValue("");
		tfDynamicEvaluation2.setValue("");
	//	tfBankBranch.setInputPrompt(Common.SELECT_PROMPT);
		lblNotificationIcon.setIcon(null);
		lblSaveNotification.setValue("");
		tfDynamicEvaluation1.setVisible(false);
		tfDynamicEvaluation2.setVisible(false);

		layoutOwnerDetails1.removeAllComponents();
		layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("", ""));

	//	tfCustomerName.setValue("Sri.");
		tfCustomerAddr.setValue("");
		tfLandMark.setValue("");
		tfPropertyAddress.setValue("");
		slPropertyDesc.setValue(null);
		tfDynamicAsset1.setValue("");
		tfDynamicAsset2.setValue("");
		tfDynamicAsset1.setVisible(false);
		tfDynamicAsset2.setVisible(false);
		
	//	slPropertyDesc.setInputPrompt(Common.SELECT_PROMPT);
		// for document details
		panelNormalDocumentDetails.removeAllComponents();
		panelNormalDocumentDetails.addComponent(btnAddNorDoc);
		panelNormalDocumentDetails.setComponentAlignment(btnAddNorDoc,
				Alignment.BOTTOM_RIGHT);
		panelNormalDocumentDetails.addComponent(new ComponentIteratorNormlDoc(
				null, null, "", ""));

		panelLegalDocumentDetails.removeAllComponents();
		panelLegalDocumentDetails.addComponent(btnAddLegalDoc);
		panelLegalDocumentDetails.setComponentAlignment(btnAddLegalDoc,
				Alignment.BOTTOM_RIGHT);
		panelLegalDocumentDetails.addComponent(new ComponentIteratorLegalDoc(
				"", "", null));

		// for adjoin properties
		panelAdjoinProperties.removeAllComponents();
		panelAdjoinProperties.addComponent(btnAddAdjoinProperty);
		panelAdjoinProperties.setComponentAlignment(btnAddAdjoinProperty,
				Alignment.BOTTOM_RIGHT);
		panelAdjoinProperties.addComponent(new ComponentIteratorAdjoinProperty(
				null, true, true, true));

		// for dimensions
		panelDimension.removeAllComponents();
		itemDimensionNumber = 1;
		panelDimension.addComponent(btnAddDimension);
		panelDimension.addComponent(lblDimension);
		lblDimension.setValue("Item No :" + itemDimensionNumber);
		panelDimension.setComponentAlignment(btnAddDimension,
				Alignment.BOTTOM_RIGHT);
		panelDimension.addComponent(new ComponentIterDimensionofPlot(null,
				true, true, true));
		
		slMatchingBoundary.setValue(null);
		slPlotDemarcated.setValue(null);
		slApproveLandUse.setValue(null);
		slTypeofProperty.setValue(null);
		tfDynamicmatching1.setValue("");
		tfDynamicmatching2.setValue("");
		tfDynamicmatching1.setVisible(false);
		tfDynamicmatching2.setVisible(false);

		tfStatusofTenure.setValue("");
		slOwnedorRent.setValue(null);
		tfNoOfYears.setValue("");
		tfRelationship.setValue("");
		tfDynamicTenure1.setValue("");
		tfDynamicTenure2.setValue("");
		tfDynamicTenure1.setVisible(false);
		tfDynamicTenure2.setVisible(false);
	//	slOwnedorRent.setInputPrompt(Common.SELECT_PROMPT);
		// for construction
		tfStageofConst.setValue("");
		tfDynamicConstruction1.setValue("");
		tfDynamicConstruction2.setValue("");
		tfDynamicConstruction1.setVisible(false);
		tfDynamicConstruction2.setVisible(false);

		// for violation
		tfAnyViolation.setValue("");
		tfDynamicViolation1.setValue("");
		tfDynamicViolation2.setValue("");
		tfDynamicViolation1.setVisible(false);
		tfDynamicViolation2.setVisible(false);

		tfSiteArea.setValue("0");
		tfPlinthArea.setValue("");
		tfCarpetArea.setValue("");
		tfSalableArea.setValue("0");
		tfRemarks.setValue("");
		tfDynamicAreaDetail1.setValue("");
		tfDynamicAreaDetail2.setValue("");
		tfDynamicAreaDetail1.setVisible(false);
		tfDynamicAreaDetail2.setVisible(false);

		tfAreaofLand.setValue("0");
		tfNorthandSouth.setValue("");
		tfMarketRate.setValue("");
		tfAdopetdMarketRate.setValue("");
		tfFairMarketRate.setValue("0");
		tfDistressRate.setValue("0");
		tfRealziableRate.setValue("0");
		tfGuidelineRate.setValue("0");
		tfDynamicValuation1.setValue("");
		tfDynamicValuation2.setValue("");
		tfDynamicValuation1.setVisible(false);
		tfDynamicValuation2.setVisible(false);

		// plinth Area
		layoutGuideline.removeAllComponents();
		layoutGuideline.addComponent(btnAddGuideline);
		layoutGuideline.setComponentAlignment(btnAddGuideline,
				Alignment.BOTTOM_RIGHT);
		layoutGuideline.addComponent(new ComponentIterGuideline("Land", "", "",
				""));
		layoutGuideline.addComponent(new ComponentIterGuideline("Building", "",
				"", ""));

		tfZone.setValue("");
		tfSRO.setValue("");
		tfVillage.setValue("");
		tfRevnueDist.setValue("");
		tfTalukName.setValue("");
		tfStreetName.setValue("");
		slStreetSerNo.setValue("Street Name");
		tfGuidelineValue.setValue("");
		tfGuidelineValueMatric.setValue("");
		slClassification.setValue("");

		// for default values
		tfStatusofTenure.setValue(Common.strNA);
		tfRelationship.setValue(Common.strNA);
		tfStageofConst.setValue(Common.strNA);
		tfPlinthArea.setValue(Common.strNil);
		tfCarpetArea.setValue(Common.strNil);
		tfRemarks.setValue(Common.strNil);
		

		try{
			new UploadUI(hlImageLayout1);
			}catch(Exception e){
				e.printStackTrace();

			}
			 filevalue=false;
			 
			 try{
					new UploadUI2(hlImageLayout2);
					}catch(Exception e){
						e.printStackTrace();

					}
					 filevalue1=false;
		
		accordion.setSelectedTab(0);
		
		slMatchingBoundary.setValue(Common.YES_DESC);
		slPlotDemarcated.setValue(Common.YES_DESC);

	}

	private StreamResource getPDFStream() {try{
		StreamResource.StreamSource source = new StreamResource.StreamSource() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public InputStream getStream() {
				TPemCmEvalDetails edit = beans.getItem(
						tblEvalDetails.getValue()).getBean();
				if (edit.getEvalDoc() != null) {

					return new ByteArrayInputStream(edit.getEvalDoc());
				} else {
					return null;
				}

			}
		};
		TPemCmEvalDetails edit = beans.getItem(tblEvalDetails.getValue())
				.getBean();
		StreamResource resource = new StreamResource(source, edit.getEvalNo()+"_"+edit.getCustName()+"_"+propertyType+
				 ".doc");
		return resource;
}
catch(Exception e){
	e.printStackTrace();

	return null;
}}
	

	
	 StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
		             @Override
		            public InputStream getStream() {
		 				TPemCmEvalDetails edit = beans.getItem(
								tblEvalDetails.getValue()).getBean();
						if (edit.getEvalDoc() != null) {

							return new ByteArrayInputStream(edit.getEvalDoc());
						} else {
							return null;
						}

					}
		        }, "demo.doc");
	
	
	@SuppressWarnings("deprecation")
	void setComponentStyle() {

		tfSearchBankbranch.setNullSelectionAllowed(false);
	//	tfSearchBankbranch.setInputPrompt(Common.SELECT_PROMPT);
	//	tfSearchCustomer.setInputPrompt("Enter Customer");
	//	tfSearchEvalNumber.setInputPrompt("Enter Evaluation Number");
		tfEvaluationNumber.setWidth(strComponentWidth);
		tfBankBranch.setWidth(strComponentWidth);
		tfEvaluationPurpose.setWidth(strComponentWidth);
		dfDateofValuation.setWidth("150px");
		dfDateofValuation.addValidator(new DateValidation("Invalid date entered"));
		dfDateofValuation.setImmediate(true);
		tfValuatedBy.setWidth(strComponentWidth);
		dfVerifiedDate.setWidth("150px");
		dfVerifiedDate.addValidator(new DateValidation("Invalid date entered"));
		dfVerifiedDate.setImmediate(true);
		tfVerifiedBy.setWidth(strComponentWidth);
		tfDynamicEvaluation1.setWidth(strComponentWidth);
		tfDynamicEvaluation2.setWidth(strComponentWidth);

		tfEvaluationNumber.setNullRepresentation("");
		tfBankBranch.setNullSelectionAllowed(false);
		tfEvaluationPurpose.setNullRepresentation("");
		tfValuatedBy.setNullRepresentation("");
		tfVerifiedBy.setNullRepresentation("");
		tfDynamicEvaluation1.setNullRepresentation("");
		tfDynamicEvaluation2.setNullRepresentation("");

		dfDateofValuation.setResolution(PopupDateField.RESOLUTION_DAY);
		dfDateofValuation.setDateFormat("dd-MMM-yyyy");
		dfVerifiedDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dfVerifiedDate.setDateFormat("dd-MMM-yyy");
		dfSearchEvalDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dfSearchEvalDate.setDateFormat("dd-MMM-yyy");

		tfCustomerName.setWidth(strComponentWidth);
		slPropertyDesc.setWidth(strComponentWidth);
		tfCustomerAddr.setWidth(strComponentWidth);
		tfCustomerAddr.setHeight("130px");
		slPropertyDesc.setNullSelectionAllowed(false);

		tfCustomerName.setNullRepresentation("");
		slPropertyDesc.setNullSelectionAllowed(false);
		tfCustomerAddr.setNullRepresentation("");

		// for matching boundary
		slMatchingBoundary.setWidth(strComponentWidth);
		slPlotDemarcated.setWidth(strComponentWidth);
		slApproveLandUse.setWidth(strComponentWidth);
		slTypeofProperty.setWidth(strComponentWidth);
		tfDynamicmatching1.setWidth(strComponentWidth);
		tfDynamicmatching2.setWidth(strComponentWidth);

		tfStatusofTenure.setWidth(strComponentWidth);
		slOwnedorRent.setWidth(strComponentWidth);
		tfNoOfYears.setWidth(strComponentWidth);
		tfRelationship.setWidth(strComponentWidth);
		tfDynamicTenure1.setWidth(strComponentWidth);
		tfDynamicTenure2.setWidth(strComponentWidth);

		slMatchingBoundary.setNullSelectionAllowed(false);
		slPlotDemarcated.setNullSelectionAllowed(false);
		slApproveLandUse.setNullSelectionAllowed(false);
		slTypeofProperty.setNullSelectionAllowed(false);
		tfDynamicmatching1.setNullRepresentation("");
		tfDynamicmatching2.setNullRepresentation("");

		tfStatusofTenure.setNullRepresentation("");
		slOwnedorRent.setNullSelectionAllowed(false);
		tfNoOfYears.setNullRepresentation("");
		tfRelationship.setNullRepresentation("");
		tfDynamicTenure1.setNullRepresentation("");
		tfDynamicTenure2.setNullRepresentation("");

		// for construction
		tfStageofConst.setWidth(strComponentWidth);
		tfDynamicConstruction1.setWidth(strComponentWidth);
		tfDynamicConstruction2.setWidth(strComponentWidth);

		tfStageofConst.setNullRepresentation("");
		tfDynamicConstruction1.setNullRepresentation("");
		tfDynamicConstruction2.setNullRepresentation("");

		// for violation
		tfAnyViolation.setWidth(strComponentWidth);
		tfDynamicViolation1.setWidth(strComponentWidth);
		tfDynamicViolation2.setWidth(strComponentWidth);

		tfAnyViolation.setNullRepresentation("");
		tfDynamicViolation1.setNullRepresentation("");
		tfDynamicViolation2.setNullRepresentation("");

		// for area details
		tfSiteArea.setWidth(strComponentWidth);
		tfPlinthArea.setWidth(strComponentWidth);
		tfCarpetArea.setWidth(strComponentWidth);
		tfSalableArea.setWidth(strComponentWidth);
		tfRemarks.setWidth(strComponentWidth);
		tfRemarks.setHeight("95px");
		tfDynamicAreaDetail1.setWidth(strComponentWidth);
		tfDynamicAreaDetail2.setWidth(strComponentWidth);

		tfSiteArea.setNullRepresentation("");
		tfPlinthArea.setNullRepresentation("");
		tfCarpetArea.setNullRepresentation("");
		tfSalableArea.setNullRepresentation("");
		tfRemarks.setNullRepresentation("");
		tfDynamicAreaDetail1.setNullRepresentation("");
		tfDynamicAreaDetail2.setNullRepresentation("");

		// for land valuation
		tfAreaofLand.setWidth(strComponentWidth);
		tfNorthandSouth.setWidth(strComponentWidth);
		tfMarketRate.setWidth(strComponentWidth);
		tfAdopetdMarketRate.setWidth(strComponentWidth);
		tfFairMarketRate.setWidth(strComponentWidth);
		tfRealziableRate.setWidth(strComponentWidth);
		tfDistressRate.setWidth(strComponentWidth);
		tfGuidelineRate.setWidth(strComponentWidth);
		tfDynamicValuation1.setWidth(strComponentWidth);
		tfDynamicValuation2.setWidth(strComponentWidth);

		tfLandMark.setWidth(strComponentWidth);
		tfPropertyAddress.setWidth(strComponentWidth);
		tfPropertyAddress.setHeight("130px");
		tfDynamicAsset1.setWidth(strComponentWidth);
		tfDynamicAsset2.setWidth(strComponentWidth);

		tfZone.setWidth(strComponentWidth);
		tfSRO.setWidth(strComponentWidth);
		tfVillage.setWidth(strComponentWidth);
		tfRevnueDist.setWidth(strComponentWidth);
		tfTalukName.setWidth(strComponentWidth);
		tfStreetName.setWidth(strComponentWidth);
		slStreetSerNo.setWidth(strComponentWidth);
		slStreetSerNo.setHeight("25");
		tfStreetName.setHeight("25");
		tfGuidelineValue.setWidth(strComponentWidth);
		tfGuidelineValueMatric.setWidth(strComponentWidth);
		slClassification.setWidth(strComponentWidth);

		tfAreaofLand.setNullRepresentation("");
		tfNorthandSouth.setNullRepresentation("");
		tfMarketRate.setNullRepresentation("");
		tfAdopetdMarketRate.setNullRepresentation("");
		tfFairMarketRate.setNullRepresentation("");
		tfRealziableRate.setNullRepresentation("");
		tfDistressRate.setNullRepresentation("");
		tfGuidelineRate.setNullRepresentation("");
		tfDynamicValuation1.setNullRepresentation("");
		tfDynamicValuation2.setNullRepresentation("");

		tfLandMark.setNullRepresentation("");
		tfPropertyAddress.setNullRepresentation("");
		tfPropertyAddress.setNullRepresentation("");
		;
		tfDynamicAsset1.setNullRepresentation("");
		tfDynamicAsset2.setNullRepresentation("");

		tfZone.setNullRepresentation("");
		tfSRO.setNullRepresentation("");
		tfVillage.setNullRepresentation("");
		tfRevnueDist.setNullRepresentation("");
		tfTalukName.setNullRepresentation("");
		tfStreetName.setNullRepresentation("");
		tfGuidelineValue.setNullRepresentation("");
		tfGuidelineValueMatric.setNullRepresentation("");
		slClassification.setNullRepresentation("");

		// for dynamic
		btnDynamicEvaluation1.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicEvaluation1.setStyleName(Runo.BUTTON_LINK);

		btnDynamicAsset.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicAsset.setStyleName(Runo.BUTTON_LINK);

		btnAddLegalDoc.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddNorDoc.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddNorDoc.setStyleName(Runo.BUTTON_LINK);
		btnAddLegalDoc.setStyleName(Runo.BUTTON_LINK);
		btnAddAdjoinProperty.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnAddAdjoinProperty.setStyleName(Runo.BUTTON_LINK);
		btnAddDimension.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddDimension.setStyleName(Runo.BUTTON_LINK);

		btnDynamicTenure.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicTenure.setStyleName(Runo.BUTTON_LINK);
		btnDynamicmatching
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicmatching.setStyleName(Runo.BUTTON_LINK);
		btnDynamicConstruction.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicConstruction.setStyleName(Runo.BUTTON_LINK);
		btnDynamicViolation
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicViolation.setStyleName(Runo.BUTTON_LINK);
		btnDynamicAreaDetail.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicAreaDetail.setStyleName(Runo.BUTTON_LINK);
		btnDynamicValuation
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicValuation.setStyleName(Runo.BUTTON_LINK);
		btnAddOwner.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddOwner.setStyleName(Runo.BUTTON_LINK);
		btnAddGuideline.setStyleName(Runo.BUTTON_LINK);
		btnAddGuideline.setIcon(new ThemeResource(Common.strAddIcon));

		tfStatusofTenure.setValue(Common.strNA);
		tfRelationship.setValue(Common.strNA);
		tfStageofConst.setValue(Common.strNA);
		tfPlinthArea.setValue(Common.strNil);
		tfCarpetArea.setValue(Common.strNil);
		tfRemarks.setValue(Common.strNil);
		
		tfValuatedBy.setNullRepresentation("");
		
	}
	private void getExportTableDetails()
	{
		excelexporter.setTableToBeExported(tblEvalDetails);
		csvexporter.setTableToBeExported(tblEvalDetails);
		pdfexporter.setTableToBeExported(tblEvalDetails);
		excelexporter.setCaption("Microsoft Excel (XLS)");
		excelexporter.setStyleName("borderless");
		csvexporter.setCaption("Comma Dilimited (CSV)");
		csvexporter.setStyleName("borderless");
		pdfexporter.setCaption("Acrobat Document (PDF)");
		pdfexporter.setStyleName("borderless");
		
	}

	public void buttonClick(ClickEvent event) {
		notifications.close();
		if (btnAddNorDoc == event.getButton()) {

			panelNormalDocumentDetails
					.addComponent(new ComponentIteratorNormlDoc(null, null, "",
							""));
		}
		if (btnAddGuideline == event.getButton()) {

			layoutGuideline.addComponent(new ComponentIterGuideline("", "", "",
					""));
		}
		if (btnAddLegalDoc == event.getButton()) {

			panelLegalDocumentDetails
					.addComponent(new ComponentIteratorLegalDoc("", "", null));
		}
		if (btnAddOwner == event.getButton()) {

			layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("",
					""));
		}

		if (btnAddAdjoinProperty == event.getButton()) {

			panelAdjoinProperties
					.addComponent(new ComponentIteratorAdjoinProperty(null,
							true, true, true));
		}

		if (btnAddDimension == event.getButton()) {
			panelDimension.addComponent(new ComponentIterDimensionofPlot(null,
					true, true, true));
		}

		if (btnAdd == event.getButton()) {
			btnSave.setVisible(true);
			btnSubmit.setVisible(true);
			btnCancel.setVisible(true);
			tablePanel.setVisible(false);
			searchPanel.setVisible(false);
			mainPanel.setVisible(true);
			headerid = beanEvaluation.getNextSequnceId("seq_pem_evaldtls_docid");
			lblAddEdit.setValue("&nbsp;>&nbsp;Add new");
			lblAddEdit.setVisible(true);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			resetAllFieldsFields();
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
		}

		if (btnEdit == event.getButton()) {
			
			
			btnSave.setVisible(true);
			btnCancel.setVisible(true);
			btnSubmit.setVisible(true);
			tablePanel.setVisible(false);
			searchPanel.setVisible(false);
			mainPanel.setVisible(true);
			resetAllFieldsFields();
			editDetails();
			lblAddEdit.setValue("&nbsp;>&nbsp;Modify");
			lblAddEdit.setVisible(true);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);

		}
		if (btnDynamicEvaluation1 == event.getButton()) {

			strSelectedPanel = "1";
			showSubWindow();

		}
		if (btnDynamicAsset == event.getButton()) {
			strSelectedPanel = "3";
			showSubWindow();

		}

		if (btnDynamicTenure == event.getButton()) {
			strSelectedPanel = "4";
			showSubWindow();

		}

		if (btnDynamicmatching == event.getButton()) {
			strSelectedPanel = "5";
			showSubWindow();

		}
		if (btnDynamicConstruction == event.getButton()) {
			strSelectedPanel = "6";
			showSubWindow();

		}
		if (btnDynamicViolation == event.getButton()) {
			strSelectedPanel = "7";
			showSubWindow();

		}
		if (btnDynamicAreaDetail == event.getButton()) {
			strSelectedPanel = "8";
			showSubWindow();

		}
		if (btnDynamicValuation == event.getButton()) {
			strSelectedPanel = "9";
			showSubWindow();

		}

		if (myButton == event.getButton()) {

			if (tfCaption.getValue() != null
					&& tfCaption.getValue().trim().length() > 0) {

				if (strSelectedPanel.equals("1")) {
					if (tfDynamicEvaluation1.isVisible()) {
						tfDynamicEvaluation2.setCaption(tfCaption.getValue());
						tfDynamicEvaluation2.setVisible(true);
					} else {
						tfDynamicEvaluation1.setCaption(tfCaption.getValue());
						tfDynamicEvaluation1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("3")) {
					if (tfDynamicAsset1.isVisible()) {
						tfDynamicAsset2.setCaption(tfCaption.getValue());
						tfDynamicAsset2.setVisible(true);
					} else {
						tfDynamicAsset1.setCaption(tfCaption.getValue());
						tfDynamicAsset1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("4")) {
					if (tfDynamicTenure1.isVisible()) {
						tfDynamicTenure2.setCaption(tfCaption.getValue());
						tfDynamicTenure2.setVisible(true);
					} else {
						tfDynamicTenure1.setCaption(tfCaption.getValue());
						tfDynamicTenure1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("5")) {
					if (tfDynamicmatching1.isVisible()) {
						tfDynamicmatching2.setCaption(tfCaption.getValue());
						tfDynamicmatching2.setVisible(true);
					} else {
						tfDynamicmatching1.setCaption(tfCaption.getValue());
						tfDynamicmatching1.setVisible(true);
					}
				}

				else if (strSelectedPanel.equals("6")) {
					if (tfDynamicConstruction1.isVisible()) {
						tfDynamicConstruction2.setCaption(tfCaption.getValue());
						tfDynamicConstruction2.setVisible(true);
					} else {
						tfDynamicConstruction1.setCaption(tfCaption.getValue());
						tfDynamicConstruction1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("7")) {
					if (tfDynamicViolation1.isVisible()) {
						tfDynamicViolation2.setCaption(tfCaption.getValue());
						tfDynamicViolation2.setVisible(true);
					} else {
						tfDynamicViolation1.setCaption(tfCaption.getValue());
						tfDynamicViolation1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("8")) {
					if (tfDynamicAreaDetail1.isVisible()) {
						tfDynamicAreaDetail2.setCaption(tfCaption.getValue());
						tfDynamicAreaDetail2.setVisible(true);
					} else {
						tfDynamicAreaDetail1.setCaption(tfCaption.getValue());
						tfDynamicAreaDetail1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("9")) {
					if (tfDynamicValuation1.isVisible()) {
						tfDynamicValuation2.setCaption(tfCaption.getValue());
						tfDynamicValuation2.setVisible(true);
					} else {
						tfDynamicValuation1.setCaption(tfCaption.getValue());
						tfDynamicValuation1.setVisible(true);
					}
				}
			}
			mywindow.close();
		}

		if (btnSave == event.getButton()) {
			setComponentError();
			if(tfEvaluationNumber.isValid() && tfBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid() && tfCustomerName.isValid())
			{
			
			saveEvaluationDetails();
			}
			//btnSave.setVisible(false);
		}
		if(btnSubmit == event.getButton()){
			setComponentError();
			if(tfEvaluationNumber.isValid() && tfBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid() &&tfCustomerName.isValid()&&tfAdopetdMarketRate.isValid())
			{

			saveEvaluationDetails();
			updateEvaluationDetails();
			btnSubmit.setEnabled(false);
			btnSave.setEnabled(false);
				
			}
		//	saveExcel.setVisible(true);
		
		}
		if (btnCancel == event.getButton()) {
			resetFields();
			btnSave.setVisible(false);
			btnCancel.setVisible(false);
			btnSubmit.setVisible(false);
			btnSave.setEnabled(true);
			btnSubmit.setEnabled(true);
			//saveExcel.setVisible(false);
			tablePanel.setVisible(true);
			searchPanel.setVisible(true);
			mainPanel.setVisible(false);
			btnEdit.setEnabled(false);
			btnAdd.setEnabled(true);
			populateAndConfig(false);
			hlFileDownloadLayout.removeAllComponents();
			hlFileDownloadLayout.addComponent(btnDownload);
			getExportTableDetails();
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");

		}
		if (btnSearch == event.getButton()) {
			populateAndConfig(true);
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
		if (btnReset == event.getButton()) {
			tfSearchEvalNumber.setValue("");
			tfSearchCustomer.setValue("");
			dfSearchEvalDate.setValue(null);
			tfSearchBankbranch.setValue(null);
			lblNotificationIcon.setIcon(null);
			lblSaveNotification.setValue("");
			populateAndConfig(false);
		}
		 else if (btnBack == event.getButton()) {
			 resetFields();
				btnSave.setVisible(false);
				btnCancel.setVisible(false);
				btnSubmit.setVisible(false);
				//saveExcel.setVisible(false);
				tablePanel.setVisible(true);
				searchPanel.setVisible(true);
				mainPanel.setVisible(false);
				btnEdit.setEnabled(false);
				btnAdd.setEnabled(true);
				populateAndConfig(false);
				lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName
						+ "</b>&nbsp;::&nbsp;Search");
				lblNotificationIcon.setIcon(null);
				lblSaveNotification.setValue("");
				lblFormTittle.setVisible(true);
				hlBreadCrumbs.setVisible(false);
				lblAddEdit.setVisible(false);
				btnView.setEnabled(false);
				
				
			}
		 else if (btnView == event.getButton()) {

				StreamResource sr =getPDFStream();

                        if (sr != null) {

                           if (filedownloader == null) {
                        	   filedownloader = new FileDownloader(getPDFStream());
                        	   filedownloader.extend(btnView);
                          } else {
                        	  filedownloader.setFileDownloadResource(sr);
                         }
                        } else {
                        	lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
                			lblSaveNotification.setValue("No document is there");
                       //   notif.show(Page.getCurrent());
                           if (filedownloader != null) {
                        	   filedownloader.setFileDownloadResource(null); // reset
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
