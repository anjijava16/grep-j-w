package com.gnts.pem.txn.sbi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.ui.UploadUI2;
import com.gnts.erputil.validations.DateValidation;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.txn.common.TPemCmAssetDetails;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.domain.txn.common.TPemCmLandValutnData;
import com.gnts.pem.domain.txn.common.TPemCmOwnerDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropAdjoinDtls;
import com.gnts.pem.domain.txn.common.TPemCmPropDescription;
import com.gnts.pem.domain.txn.common.TPemCmPropDimension;
import com.gnts.pem.domain.txn.common.TPemCmPropDocDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropGuidlnValue;
import com.gnts.pem.domain.txn.common.TPemCmPropImage;
import com.gnts.pem.domain.txn.common.TPemCmPropLegalDocs;
import com.gnts.pem.domain.txn.common.TPemCmPropOldPlanApprvl;
import com.gnts.pem.domain.txn.common.TPemCmPropValtnSummry;
import com.gnts.pem.domain.txn.sbi.TPemSbiPropChartrstic;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.txn.common.CmAssetDetailsService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;
import com.gnts.pem.service.txn.common.CmLandValutnDataService;
import com.gnts.pem.service.txn.common.CmOwnerDetailsService;
import com.gnts.pem.service.txn.common.CmPropAdjoinDtlsService;
import com.gnts.pem.service.txn.common.CmPropDescriptionService;
import com.gnts.pem.service.txn.common.CmPropDimensionService;
import com.gnts.pem.service.txn.common.CmPropDocDetailsService;
import com.gnts.pem.service.txn.common.CmPropGuidlnValueService;
import com.gnts.pem.service.txn.common.CmPropImageService;
import com.gnts.pem.service.txn.common.CmPropLegalDocsService;
import com.gnts.pem.service.txn.common.CmPropOldPlanApprvlService;
import com.gnts.pem.service.txn.common.CmPropValtnSummryService;
import com.gnts.pem.service.txn.sbi.SbiPropChartrsticService;
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
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.themes.Runo;

public class SBILand implements ClickListener {
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
	private CmPropDescriptionService beanPropDesc = (CmPropDescriptionService) SpringContextHelper
			.getBean("propDesc");
	private SbiPropChartrsticService beanCharcter = (SbiPropChartrsticService) SpringContextHelper
			.getBean("sbiPropCharcter");
	private CmLandValutnDataService beanlandvaluation = (CmLandValutnDataService) SpringContextHelper
			.getBean("landValtn");
	private CmPropOldPlanApprvlService beanPlanApprvl=(CmPropOldPlanApprvlService) SpringContextHelper
			.getBean("oldPlanApprvl");
	private CmPropGuidlnValueService beanguidelinevalue = (CmPropGuidlnValueService) SpringContextHelper
			.getBean("guidelineValue");
	private CmPropValtnSummryService beanPropertyvalue = (CmPropValtnSummryService) SpringContextHelper
			.getBean("propValtnSummary");
	private CmPropImageService beanPropImage = (CmPropImageService) SpringContextHelper
			.getBean("propImage");
	private CmBankService beanbank = (CmBankService) SpringContextHelper
			.getBean("bank");

	private Accordion accordion = new Accordion();
	private Table tblEvalDetails ;
	private BeanItemContainer<TPemCmEvalDetails> beans = null;

	// for common
	private VerticalLayout mainPanel = new VerticalLayout();
	private VerticalLayout searchPanel = new VerticalLayout();
	private VerticalLayout tablePanel = new VerticalLayout();
	private Long headerid;
	private String strEvaluationNo = "SBI_LAND_";
	private String strXslFile = "SbiLand.xsl";
	private String strComponentWidth = "200px";
	private String strSplComponentWidth = "230px";
	private Long selectedBankid;
	private String selectedFormName;
	private Long selectCompanyid,currencyId;
	private String loginusername;
	private String evalNumber;
	private String customer;
	private String propertyType;
	public static Boolean filevalue=false;
	public static Boolean filevalue1=false;
	int count =0;
	private FileDownloader filedownloader;
	// pagination
			private int total = 0;
	// for table panel
	private VerticalLayout layoutTable = new VerticalLayout();
	private HorizontalLayout hlAddEditLayout = new HorizontalLayout();
	private Button btnAdd = new Button("Add", this);
	private Button btnEdit = new Button("Edit", this);
	private Button btnView = new Button("View Document", this);
	
	// Added Button Back by Hohul
	
		private Button btnBack;
		
		
		// Added Bread Crumbs by Hohul
		private HorizontalLayout hlBreadCrumbs;
		
		
		// Label Declared Here by Hohul
		private Label lblTableTitle;
		private Label lblFormTittle, lblFormTitle1, lblAddEdit;
		private Label lblSaveNotification, lblNotificationIcon,lblNoofRecords;
		
		
		// Declared String Variable for Screen Name by Hohul
		
		private String screenName;
	// for search panel
	private HorizontalLayout layoutSearch = new HorizontalLayout();
	private TextField tfSearchEvalNumber = new TextField("Evaluation Number");
	private PopupDateField dfSearchEvalDate = new PopupDateField(
			"Evaluation Date");
	private ComboBox slSearchBankbranch = new ComboBox("Bank Branch");
	private TextField tfSearchCustomer = new TextField("Customer Name");
	private Button btnSearch = new Button("Search", this);
	private Button btnReset = new Button("Reset", this);

	private Button btnDownload;
	  HorizontalLayout hlFileDownloadLayout;
	//Declaration for Exporter
	private Window notifications=new Window();
	private ExcelExporter excelexporter = new ExcelExporter();
	private CSVExporter csvexporter = new CSVExporter();
	private PdfExporter pdfexporter = new PdfExporter();
	// for main panel
	private VerticalLayout layoutMainForm = new VerticalLayout();
	private HorizontalLayout layoutButton2 = new HorizontalLayout();
	private Button btnSave = new Button("Save", this);
	private Button btnCancel = new Button("Cancel", this);
	private Button btnSubmit =new Button("Submit",this);
	//private Button saveExcel = new Button("Report", this);
	private Label lblHeading = new Label();

	// for evaluation details
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
	private ComboBox slBankBranch = new ComboBox("Bank Branch");
	private TextField tfDynamicEvaluation1 = new TextField();
	private TextField tfDynamicEvaluation2 = new TextField();
	private Button btnDynamicEvaluation1 = new Button("", this);
	
	// Owner Details
		private VerticalLayout layoutOwnerDetails = new VerticalLayout();
		private GridLayout layoutOwnerDetails1 = new GridLayout();
		private Button btnAddOwner = new Button("", this);


	// for asset details
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

	
	// for Document Details
	private VerticalLayout layoutNormalLegal = new VerticalLayout();
	private VerticalLayout panelNormalDocumentDetails = new VerticalLayout();
	private VerticalLayout panelLegalDocumentDetails = new VerticalLayout();
	private Button btnAddNorDoc = new Button("", this);
	private Button btnAddLegalDoc = new Button("", this);

	// for description of the property
	private VerticalLayout layoutDescProperty = new VerticalLayout();
	private GridLayout layoutDescProperty1 = new GridLayout();
	private TextArea tfPostalAddress = new TextArea(
			"Postal Address of the property");
	private TextField tfSiteNumber = new TextField("Site No.");
	private TextField tfSFNumber = new TextField("S.F.No.");
	private TextField tfNewSFNumber = new TextField("New S.F.No.");
	private TextField tfVillage = new TextField("Village");
	private TextField tfTaluk = new TextField("Taluk");
	private TextField tfDistCorpPanc = new TextField("District/Municipality");
	private TextField tfLocationSketch = new TextField(
			"Location sketch of the property");
	private TextField tfProTaxReceipt = new TextField(
			"Property Tax Receipt Ref");
	private TextField tfElecServiceConnNo = new TextField(
			"Electricity Connection No.");
	private TextField tfElecConnecName = new TextField(
			"Electricity Connection Name");
	private ComboBox slHighMiddPoor = new ComboBox("High/Middle/Poor");
	private ComboBox slUrbanSemiRural = new ComboBox("Urban/Semi Urban/Rural");
	private ComboBox slResiIndustCommer = new ComboBox("Property Type");
	private ComboBox slProOccupiedBy = new ComboBox("Property Occupied by");
	private TextField tfMonthlyRent = new TextField("What is the monthly rent");
	private TextField tfCoverUnderStatCentral = new TextField(
			"Covered under any State/Central Govt.");
	private TextField tfAnyConversionLand = new TextField("Any Land Conversion");
	private TextField tfExtentSite = new TextField("Extent of the Site");
	private TextField tfYearAcquistion = new TextField(
			"Year of acquisition/purchase");
	private TextField tfPurchaseValue = new TextField("Value of purchase price");
	private TextField tfPropLandmark = new TextField(
			"Land Mark to the property");
	private TextField tfDynamicDescProp1 = new TextField();
	private TextField tfDynamicDescProp2 = new TextField();
	private Button btnDynamicDescProp = new Button("", this);
	// for charcteristiccs of the site
	private VerticalLayout layoutCharcterSite = new VerticalLayout();
	private GridLayout layoutCharcterSite1 = new GridLayout();
	private ComboBox slLocalClass = new ComboBox(
			"Classification of the Locality");
	private ComboBox slSurroundDevelop = new ComboBox(
			"Development of Surrounding areas");
	private TextField tfFlood = new TextField(
			"Possibility of Frequent Flooding");
	private ComboBox slFeasibility = new ComboBox(
			"Feasibility to the Civic amenities");
	private ComboBox slLandLevel = new ComboBox("Level of Land");
	private ComboBox slLandShape = new ComboBox("Shape of Land");
	private ComboBox slTypeUse = new ComboBox("Type of Use");
	private TextField tfUsageRestriction = new TextField(
			"Any Usage Restriction");
	private ComboBox slIsPlot = new ComboBox(
			
			
			"Is Plot in Town Planning approved");
	private TextField tfApprveNo = new TextField("If Yes Approval No.");
	private TextField tfNoReason = new TextField("If No Reasons there of ");
	private TextField tfSubdivide = new TextField("Is Subdivided");
	private ComboBox slDrawApproval = new ComboBox("Drawing Approval");
	private ComboBox slCornerInter = new ComboBox(
			"Corner plot or Intermittent plot");
	private ComboBox slRoadFacility = new ComboBox("Road Facilities");
	private ComboBox slTypeRoad = new ComboBox("Type of Road");
	private ComboBox slRoadWidth = new ComboBox("Width of Road");
	private ComboBox slLandLock = new ComboBox("Is Land Locked Land");
	private ComboBox slWaterPotential = new ComboBox("Water Potentiality");
	private ComboBox slUnderSewerage = new ComboBox(
			"Underground Water sewerage system");
	private ComboBox slPowerSupply = new ComboBox(
			"Power Supply is available in the site");
	private TextField tfAdvantageSite = new TextField("Advantage of the site");
	private TextField tfDisadvantageSite = new TextField(
			"Disadvantage of the site");
	private TextField tfGeneralRemarks = new TextField("General Remarks if any");
	private TextField tfDynamicCharacter1 = new TextField();
	private TextField tfDynamicCharacter2 = new TextField();
	private Button btnDynamicCharacter = new Button("", this);
	// for adjoin properties
	private VerticalLayout panelAdjoinProperties = new VerticalLayout();
	private Button btnAddAdjoinProperty = new Button("", this);

	// for dimension of plot
	private VerticalLayout panelDimension = new VerticalLayout();
	private Button btnAddDimension = new Button("", this);

	// valuation of land
	private VerticalLayout layoutValuationLand = new VerticalLayout();
	private GridLayout layoutValuationLand1 = new GridLayout();
	private TextField tfAreaofLand = new TextField("Size of the plot");
	private TextField tfNorthandSouth = new TextField("North and South");
	private TextField tfMarketRate = new TextField("Prevailing market rate");
	private TextField tfGuiderate=new TextField("Guideline rate");
	private TextField tfAdopetdMarketRate = new TextField(
			"Assd./Adped. rate of valuation");
	private TextField tfFairMarketRate = new TextField(
			"Estimated value of Land");
	private TextField tfDynamicValuation1 = new TextField();
	private TextField tfDynamicValuation2 = new TextField();
	private Button btnDynamicValuation = new Button("", this);
	

	// details of plan approval
	private VerticalLayout layoutPlanApproval = new VerticalLayout();
	private GridLayout layoutPlanApproval1 = new GridLayout();
	private TextField tfLandandBuilding = new TextField("Land");
	private TextField tfPlanApprovedBy = new TextField("Approved by");
	private TextField dfLicenseFrom = new TextField("License period");
	private ComboBox slIsLicenceForced = new ComboBox(
			"Is the license is in force");
	private ComboBox slAllApprovalRecved = new ComboBox(
			"Are all approvals required are received");
	private TextField tfDynamicPlanApproval1 = new TextField();
	private TextField tfDynamicPlanApproval2 = new TextField();
	private Button btnDynamicPlanApproval = new Button("", this);

	// guideline area
	private VerticalLayout layoutGuideline = new VerticalLayout();
	private Button btnAddGuideline = new Button("", this);

	// for property values
	private VerticalLayout layoutPropertyValue = new VerticalLayout();
	private GridLayout layoutPropertyValue1 = new GridLayout();
	private TextField tfGuidelineRate = new TextField("Guideline value of land");
	private TextField tfRealziableRate = new TextField(
			"Realizable value of the Land");
	private TextField tfDistressRate = new TextField(
			"Distress value of the Land");
	// commondata
	private Window mywindow = new Window("Enter Caption");
	private Button myButton = new Button("Ok", this);
	private TextField tfCaption = new TextField();
	private String strSelectedPanel;
	
	//for property image
	private VerticalLayout vlImageLayout=new VerticalLayout();
	private HorizontalLayout hlImage=new HorizontalLayout();
	private HorizontalLayout hlImageLayout1=new HorizontalLayout();
	private HorizontalLayout hlImageLayout2=new HorizontalLayout();
	private Button btnImgDownload = new Button("Download",this);
	private Button btnImg1Download = new Button("Download",this);
	
	// for report
	UIFlowData uiflowdata = new UIFlowData();
	private Logger logger = Logger.getLogger(SBILand.class);
	private String basepath;
	private String basepath1,basepath2;
	
	public SBILand() {	
		loginusername = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		selectedFormName = screenName;
		VerticalLayout clArgumentLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		String[] splitlist=screenName.split("-");
		for(String str:splitlist){
		List<MPemCmBank> list=beanbank.getBankDtlsList(selectCompanyid, null, str);
		
		for(MPemCmBank obj:list){
		selectedBankid = obj.getBankId();
		}
		break;
		}
		
		HorizontalLayout hlHeaderLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clArgumentLayout,hlHeaderLayout);
		basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		basepath1 = basepath+"/VAADIN/themes/gerp/img/Upload.jpg";
		basepath2= basepath+"/VAADIN/themes/gerp/img/Upload2.jpg";
		
	}

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

		// for evaluation details
		tfEvaluationPurpose.setValue("Collateral Security to the Bank");
		tfEvaluationNumber.setRequired(true);
		slBankBranch.setRequired(true);
		dfDateofValuation.setRequired(true);
		tfEvaluationPurpose.setRequired(true);
		layoutEvaluationDetails1.setColumns(4);

		layoutEvaluationDetails1.addComponent(tfEvaluationNumber);
		layoutEvaluationDetails1.addComponent(slBankBranch);
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
		// for Owner Details
		layoutOwnerDetails.addComponent(btnAddOwner);
		layoutOwnerDetails.setComponentAlignment(btnAddOwner,
						Alignment.TOP_RIGHT);
		layoutOwnerDetails1.setColumns(4);
		layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("", ""));
		layoutOwnerDetails.addComponent(layoutOwnerDetails1);
		layoutOwnerDetails1.setSpacing(true);
		layoutOwnerDetails1.setMargin(true);
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
		chkSameAddress.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {

				if (chkSameAddress.getValue().equals(true)) {
					tfPropertyAddress.setValue(tfCustomerAddr.getValue());

				} else {
					tfPropertyAddress.setValue("");
			}}});
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
		panelLegalDocumentDetails.addComponent(new ComponentIteratorLegalDoc
				(
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
		panelNormalDocumentDetails.setWidth("100%");
		panelLegalDocumentDetails.setWidth("100%");

		// for adjoin properties
		panelAdjoinProperties.addComponent(btnAddAdjoinProperty);
		panelAdjoinProperties.setComponentAlignment(btnAddAdjoinProperty,
				Alignment.BOTTOM_RIGHT);
		panelAdjoinProperties.addComponent(new ComponentIteratorAdjoinProperty(
				null, true, true, true));

		// for dimensions
		panelDimension.addComponent(btnAddDimension);
		panelDimension.setComponentAlignment(btnAddDimension,
				Alignment.BOTTOM_RIGHT);
		panelDimension.addComponent(new ComponentIterDimensionofPlot(null,
				true, true, true));

		// for description of the property
		layoutDescProperty.setMargin(true);
		layoutDescProperty1.setSpacing(true);
		layoutDescProperty1.setColumns(4);
		layoutDescProperty.addComponent(btnDynamicDescProp);
		layoutDescProperty.setComponentAlignment(btnDynamicDescProp,
				Alignment.TOP_RIGHT);
		layoutDescProperty1.addComponent(tfSiteNumber);
		layoutDescProperty1.addComponent(tfSFNumber);
		layoutDescProperty1.addComponent(tfNewSFNumber);
		layoutDescProperty1.addComponent(tfVillage);
		layoutDescProperty1.addComponent(tfTaluk);
		layoutDescProperty1.addComponent(tfDistCorpPanc);
		layoutDescProperty1.addComponent(tfLocationSketch);
		layoutDescProperty1.addComponent(tfProTaxReceipt);
		layoutDescProperty1.addComponent(tfElecServiceConnNo);
		layoutDescProperty1.addComponent(tfElecConnecName);
		layoutDescProperty1.addComponent(slHighMiddPoor);
		layoutDescProperty1.addComponent(slUrbanSemiRural);
		layoutDescProperty1.addComponent(slResiIndustCommer);
		layoutDescProperty1.addComponent(slProOccupiedBy);
		layoutDescProperty1.addComponent(tfMonthlyRent);
		layoutDescProperty1.addComponent(tfCoverUnderStatCentral);
		layoutDescProperty1.addComponent(tfAnyConversionLand);
		layoutDescProperty1.addComponent(tfExtentSite);
		layoutDescProperty1.addComponent(tfYearAcquistion);
		layoutDescProperty1.addComponent(tfPurchaseValue);
		layoutDescProperty1.addComponent(tfPropLandmark);
		layoutDescProperty1.addComponent(tfPostalAddress);
		layoutDescProperty1.addComponent(tfDynamicDescProp1);
		layoutDescProperty1.addComponent(tfDynamicDescProp2);
		layoutDescProperty.addComponent(layoutDescProperty1);
		tfDynamicDescProp1.setVisible(false);
		tfDynamicDescProp2.setVisible(false);
		// for charcteristiccs of the site
		layoutCharcterSite1.setSpacing(true);
		layoutCharcterSite1.setColumns(4);
		layoutCharcterSite1.setMargin(true);
		layoutCharcterSite.setMargin(true);
		layoutCharcterSite.setSpacing(true);
		layoutCharcterSite.addComponent(btnDynamicCharacter);
		layoutCharcterSite.setComponentAlignment(btnDynamicCharacter,
				Alignment.TOP_RIGHT);
		layoutCharcterSite1.addComponent(slLocalClass);
		layoutCharcterSite1.addComponent(slSurroundDevelop);
		layoutCharcterSite1.addComponent(tfFlood);
		layoutCharcterSite1.addComponent(slFeasibility);
		layoutCharcterSite1.addComponent(slLandLevel);
		layoutCharcterSite1.addComponent(slLandShape);
		layoutCharcterSite1.addComponent(slTypeUse);
		layoutCharcterSite1.addComponent(tfUsageRestriction);
		layoutCharcterSite1.addComponent(slIsPlot);
		layoutCharcterSite1.addComponent(tfApprveNo);
		layoutCharcterSite1.addComponent(tfNoReason);
		layoutCharcterSite1.addComponent(tfSubdivide);
		layoutCharcterSite1.addComponent(slDrawApproval);
		layoutCharcterSite1.addComponent(slCornerInter);
		layoutCharcterSite1.addComponent(slRoadFacility);
		layoutCharcterSite1.addComponent(slTypeRoad);
		layoutCharcterSite1.addComponent(slRoadWidth);
		layoutCharcterSite1.addComponent(slLandLock);
		layoutCharcterSite1.addComponent(slWaterPotential);
		layoutCharcterSite1.addComponent(slUnderSewerage);
		layoutCharcterSite1.addComponent(slPowerSupply);
		layoutCharcterSite1.addComponent(tfAdvantageSite);
		layoutCharcterSite1.addComponent(tfDisadvantageSite);
		layoutCharcterSite1.addComponent(tfGeneralRemarks);
		layoutCharcterSite1.addComponent(tfDynamicCharacter1);
		layoutCharcterSite1.addComponent(tfDynamicCharacter2);
		layoutCharcterSite.addComponent(layoutCharcterSite1);
		tfDynamicCharacter1.setVisible(false);
		tfDynamicCharacter2.setVisible(false);
		
		// for details of plan approval
		layoutPlanApproval1.setColumns(2);
		FormLayout lay1 = new FormLayout();
		FormLayout lay2 = new FormLayout();
		lay1.addComponent(tfLandandBuilding);
		lay1.addComponent(tfPlanApprovedBy);
		lay2.addComponent(tfDynamicPlanApproval1);
		lay2.addComponent(tfDynamicPlanApproval2);
		tfDynamicPlanApproval1.setVisible(false);
		tfDynamicPlanApproval2.setVisible(false);
		lay1.setSpacing(true);
		lay2.setSpacing(true);
		layoutPlanApproval1.addComponent(lay1);
		layoutPlanApproval1.addComponent(lay2);
		layoutPlanApproval1.setSpacing(true);
		layoutPlanApproval1.setMargin(true);
		layoutPlanApproval.addComponent(btnDynamicPlanApproval);
		layoutPlanApproval.setComponentAlignment(btnDynamicPlanApproval,
				Alignment.TOP_RIGHT);
		layoutPlanApproval.addComponent(layoutPlanApproval1);

		// valuation of land
		layoutValuationLand.setSpacing(true);
		layoutValuationLand1.setSpacing(true);
		layoutValuationLand1.setColumns(4);
		layoutValuationLand1.addComponent(tfAreaofLand);
		layoutValuationLand1.addComponent(tfNorthandSouth);
		layoutValuationLand1.addComponent(tfMarketRate);
		layoutValuationLand1.addComponent(tfGuiderate);
		layoutValuationLand1.addComponent(tfAdopetdMarketRate);
		layoutValuationLand1.addComponent(tfFairMarketRate);
		layoutValuationLand1.addComponent(tfDynamicValuation1);
		layoutValuationLand1.addComponent(tfDynamicValuation2);
		tfDynamicValuation1.setVisible(false);
		tfDynamicValuation2.setVisible(false);
		layoutValuationLand.addComponent(btnDynamicValuation);
		layoutValuationLand.setComponentAlignment(btnDynamicValuation,
				Alignment.TOP_RIGHT);
		layoutValuationLand.addComponent(layoutValuationLand1);
		layoutValuationLand.setMargin(true);
		tfLandMark.setImmediate(true);
		tfAdopetdMarketRate.setRequired(true);
		tfLandMark.addBlurListener(new BlurListener() {
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
			tfPropLandmark.setValue(tfLandMark.getValue());	
			}
		});
		tfPropertyAddress.setImmediate(true);
		tfPropertyAddress.addBlurListener(new BlurListener() {
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				tfPostalAddress.setValue(tfPropertyAddress.getValue());
				
			}
		});
	/*	tfEvaluationNumber.addValidator(new IntegerValidator("Enter numbers only"));
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

		// property value
		layoutPropertyValue.setSpacing(true);
		layoutPropertyValue.setMargin(true);
		layoutPropertyValue1.setColumns(4);
		layoutPropertyValue1.setSpacing(true);
		layoutPropertyValue1.addComponent(tfRealziableRate);
		layoutPropertyValue1.addComponent(tfDistressRate);
		layoutPropertyValue1.addComponent(tfGuidelineRate);
		layoutPropertyValue.addComponent(layoutPropertyValue1);
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
		
		// for Guideline area
		layoutGuideline.addComponent(btnAddGuideline);
		layoutGuideline.setComponentAlignment(btnAddGuideline,
				Alignment.TOP_RIGHT);
		layoutGuideline.setMargin(true);
		layoutGuideline.addComponent(new ComponentIterGuideline("Land", "", "",
				""));
		// add components in main panel
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

		layoutDescProperty.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutDescProperty),
				"Description of the property");
		layoutCharcterSite.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutCharcterSite),
				"Characteristics of the site");
		layoutValuationLand.setStyleName("bluebar");
		accordion.addTab(layoutValuationLand,
				"Valuation of Land");
		layoutPlanApproval.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutPlanApproval),
				"Details of Plan Approval");
		layoutGuideline.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutGuideline),
				"Guideline Details");
		layoutPropertyValue.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutPropertyValue),
				"Property Value Details");
		vlImageLayout.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(vlImageLayout),
				"Property Image Details");
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
							tfAreaofLand.setValue(siteArea.toString());
						} catch (Exception e) {
							 
							logger.info("Error-->" + e);
						}
					}
				}
				BigDecimal site=new BigDecimal(0.00);
				BigDecimal fair=new BigDecimal(1.00);
				BigDecimal salbale=new BigDecimal(435.60);
				try{
					site = new BigDecimal(tfAreaofLand.getValue().replaceAll("[^\\d.]", ""));
				}catch(Exception e){
					site = new BigDecimal("0.00");
					
				}
				try{
					site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString();
					fair.multiply(site.divide(salbale, 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(tfAdopetdMarketRate.getValue().replaceAll("[^\\d.]", "")));
					tfAreaofLand.setValue(site.toString()+" sft (or) "+site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString()+" cents");
					tfFairMarketRate.setValue(XMLUtil.IndianFormat(new BigDecimal(fair.multiply(site.divide(salbale, 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(tfAdopetdMarketRate.getValue().replaceAll("[^\\d.]", ""))).toString())));
				}catch(Exception e){
					 
				}
			}	
			
		}});
		layoutMainForm.addComponent(PanelGenerator.createPanel(accordion));

		mainPanel.addComponent(layoutMainForm);
		//mainPanel.addComponent(layoutButton2);
		//mainPanel.setComponentAlignment(layoutButton2, Alignment.BOTTOM_RIGHT);
		mainPanel.setVisible(false);

		layoutMainForm.setMargin(true);
		layoutMainForm.setSpacing(true);
		// for main panel
		layoutButton2.setSpacing(true);
		btnSave.setStyleName("savebt");
		btnCancel.setStyleName("cancelbt");
		btnSubmit.setStyleName("submitbt");
	//	saveExcel.addStyleName("downloadbt");
		
		
		btnSave.setVisible(false);
		btnSubmit.setVisible(false);
		btnCancel.setVisible(false);
		//saveExcel.setVisible(false);
		layoutButton2.addComponent(btnSave);
		layoutButton2.addComponent(btnSubmit);
	//	layoutButton2.addComponent(saveExcel);
		layoutButton2.addComponent(btnCancel);
		hlHeaderLayout.addComponent(layoutButton2);
		hlHeaderLayout.setComponentAlignment(layoutButton2, Alignment.BOTTOM_RIGHT);
		// Initaited the Label Function here by Hohul
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
				
				
				// Button Back declaration by Hohul
				btnBack = new Button("Home", this);
				btnBack.setStyleName("link");
				
				// Bread Scrumbs initiated here by Hohul
				
				hlBreadCrumbs = new HorizontalLayout();
				hlBreadCrumbs.addComponent(lblFormTitle1);
				hlBreadCrumbs.addComponent(btnBack);
				hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
				hlBreadCrumbs.addComponent(lblAddEdit);
				hlBreadCrumbs
						.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER);
				hlBreadCrumbs.setVisible(false);
				
		
		tfSearchEvalNumber.setImmediate(true);
		tfSearchEvalNumber.addTextChangeListener(new TextChangeListener() {
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
		slSearchBankbranch.setImmediate(true);
		slSearchBankbranch.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			Filter filter = null;

			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				
				try{
				String strBankBranch = slSearchBankbranch.getValue().toString();
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

		// for search panel
		// for search panel
        // Added by Hohul ----->  For Search Panel Layouts
			FormLayout flSearchEvalNumber = new FormLayout();
			flSearchEvalNumber.addComponent(tfSearchEvalNumber);
			
			
			FormLayout flSearchBankbranch = new FormLayout();
			flSearchBankbranch.addComponent(slSearchBankbranch);
			
			
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
		layoutSearch.addComponent(slSearchBankbranch);
		// layoutSearch.addComponent(dfSearchEvalDate);
		layoutSearch.addComponent(tfSearchCustomer);
		//layoutSearch.addComponent(btnSearch);
		layoutSearch.addComponent(btnReset);
		//layoutSearch.setComponentAlignment(btnSearch, Alignment.BOTTOM_LEFT);
		layoutSearch.setComponentAlignment(btnReset, Alignment.BOTTOM_LEFT);
		btnSearch.addStyleName("default");*/
		btnReset.addStyleName("resetbt");
		//tfSearchCustomer.setImmediate(true);
		tfSearchCustomer.addTextChangeListener(new TextChangeListener() {
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

		searchPanel.addComponent(PanelGenerator.createPanel(vlSearchComponentandButtonLayout));
		searchPanel.setMargin(true);
		// Add Layout table panel by Hohul

				HorizontalLayout flTableCaption = new HorizontalLayout();
				flTableCaption.addComponent(lblTableTitle);
				flTableCaption.setComponentAlignment(lblTableTitle,
						Alignment.MIDDLE_CENTER);
				flTableCaption.addStyleName("lightgray");
				flTableCaption.setHeight("25px");
				flTableCaption.setWidth("60px");
				lblNoofRecords = new Label(" ", ContentMode.HTML);
				lblNoofRecords.addStyleName("lblfooter");
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
				// Added Action Label to Table
				
				
				
				
				layoutTable.addComponent(hlAddEditLayout);
				layoutTable.setComponentAlignment(hlAddEditLayout, Alignment.TOP_LEFT);
				layoutTable.addComponent(tblEvalDetails);
				
				tablePanel.addComponent(layoutTable);;
				tablePanel.setWidth("100%");
				tablePanel.setMargin(true);
			
			
				layoutPage.addComponent(mainPanel);
				layoutPage.addComponent(searchPanel);
				layoutPage.addComponent(tablePanel);
				
				
				// Added labels and titles to respective Location by Hohul
				
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
		myButton.setStyleName("default");
		mywindow.setContent(subwindow);
		
    	mywindow.setResizable(false);
		mywindow.setContent(subwindow);
		UI.getCurrent().addWindow(mywindow);
		return null;
	}

	@SuppressWarnings("deprecation")
	void populateAndConfig(boolean search) {
		try {
		tblEvalDetails.removeAllItems();
		tblEvalDetails.setImmediate(true);
		List<TPemCmEvalDetails> evalList = null;
		 evalList = new ArrayList<TPemCmEvalDetails>();
		if (search) {
			String evalno = tfSearchEvalNumber.getValue();
			String customer = tfSearchCustomer.getValue();
			String bankbranch=(String)slSearchBankbranch.getValue();
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
	/*		tblEvalDetails.addListener(new ItemClickListener() {
				private static final long serialVersionUID = 1L;

				public void itemClick(ItemClickEvent event) {
					if (tblEvalDetails.isSelected(event.getItemId())) {
						btnEdit.setEnabled(false);
						btnAdd.setEnabled(true);

					} else {
						btnEdit.setEnabled(true);
						btnAdd.setEnabled(false);

					}

				}
			});
			*/
			tblEvalDetails
			.addValueChangeListener(new Property.ValueChangeListener() {
				/**
		 * 
		 */
				private static final long serialVersionUID = 3729824796823933688L;

				@Override
				public void valueChange(ValueChangeEvent event) {

					StreamResource sr =getPDFStream();

                    if (sr != null) {
                    
                       if (filedownloader == null) {
                    	   filedownloader = new FileDownloader(getPDFStream());
                    	   filedownloader.extend(btnView);
                      } else {
                    	  filedownloader.setFileDownloadResource(sr);
                     }
                    } else {
              
                   //   notif.show(Page.getCurrent());
                       if (filedownloader != null) {
                    	   filedownloader.setFileDownloadResource(null); // reset
                       }                       
                       
                    }
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
			logger.info("Error-->" + e);
		}
		getExportTableDetails();
	}

	/*void setTableProperties() {
		beans = new BeanItemContainer<TPemCmEvalDetails>(
				TPemCmEvalDetails.class);
		tblEvalDetails.addGeneratedColumn("docDate",
				new DateFormateColumnGenerator());
		tblEvalDetails.addGeneratedColumn("lastUpdateDt",
				new DateColumnGenerator());
	}
*/
	void loadComponentListValues() {
		loadPropertyDescList();
		loadTypeofProperty();
		loadHMPDetails();
		loadUSRDetails();
		loadOwnedorRented();
		loadDevelopedDetails();
		loadLevelDetails();
		loadCornerInterDetails();
		loadAvailableDetails();
		loadLandShapeDetails();
		loadRoadTypeDetails();
		loadRoadWidthDetails();
		loadFeasibiltyDetails();
		loadStructureDetails();
		slIsPlot.addItem(Common.YES_DESC);
		slIsPlot.addItem(Common.NO_DESC);
		slDrawApproval.addItem(Common.YES_DESC);
		slDrawApproval.addItem(Common.NO_DESC);
		slLandLock.addItem(Common.YES_DESC);
		slLandLock.addItem(Common.NO_DESC);
		slIsLicenceForced.addItem(Common.YES_DESC);
		slIsLicenceForced.addItem(Common.NO_DESC);
		slAllApprovalRecved.addItem(Common.YES_DESC);
		slAllApprovalRecved.addItem(Common.NO_DESC);

		loadBankBranchDetails();
	}
	void loadBankBranchDetails(){
		List<String> list = beanBankConst.getBankConstantList("BRANCH_CODE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slBankBranch.setContainerDataSource(childAccounts);
		slSearchBankbranch.setContainerDataSource(childAccounts);
	}
	void loadStructureDetails() {
		List<String> list = beanBankConst.getBankConstantList("STRUCTURE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
	}

	void loadFeasibiltyDetails() {
		List<String> list = beanBankConst.getBankConstantList("NMF",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slFeasibility.setContainerDataSource(childAccounts);
	}

	void loadRoadWidthDetails() {
		List<String> list = beanBankConst.getBankConstantList("ROAD WIDTH",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slRoadWidth.setContainerDataSource(childAccounts);
	}

	void loadRoadTypeDetails() {
		List<String> list = beanBankConst.getBankConstantList("ROAD",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slTypeRoad.setContainerDataSource(childAccounts);
	}

	void loadLandShapeDetails() {
		List<String> list = beanBankConst.getBankConstantList("ISR",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slLandShape.setContainerDataSource(childAccounts);
	}

	void loadAvailableDetails() {
		List<String> list = beanBankConst.getBankConstantList("AN",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slRoadFacility.setContainerDataSource(childAccounts);
		slUnderSewerage.setContainerDataSource(childAccounts);
		slPowerSupply.setContainerDataSource(childAccounts);
		slWaterPotential.setContainerDataSource(childAccounts);
	}

	void loadCornerInterDetails() {
		List<String> list = beanBankConst.getBankConstantList("CI",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slCornerInter.setContainerDataSource(childAccounts);
	}

	void loadLevelDetails() {
		List<String> list = beanBankConst.getBankConstantList("LS",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slLandLevel.setContainerDataSource(childAccounts);
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
		slTypeUse.setContainerDataSource(childAccounts);
		slResiIndustCommer.setContainerDataSource(childAccounts);
	}

	void loadHMPDetails() {
		List<String> list = beanBankConst.getBankConstantList("HML",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slLocalClass.setContainerDataSource(childAccounts);
		slHighMiddPoor.setContainerDataSource(childAccounts);
	}

	void loadDevelopedDetails() {
		List<String> list = beanBankConst.getBankConstantList("DYW",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slSurroundDevelop.setContainerDataSource(childAccounts);
	}

	void loadUSRDetails() {
		List<String> list = beanBankConst.getBankConstantList("USR",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slUrbanSemiRural.setContainerDataSource(childAccounts);
	}

	void loadOwnedorRented() {
		List<String> list = beanBankConst.getBankConstantList("OWNRENT",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slProOccupiedBy.setContainerDataSource(childAccounts);
	}
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
			evalobj.setBankBranch((String)slBankBranch.getValue());
			evalobj.setEvalPurpose(tfEvaluationPurpose.getValue());
			evalobj.setEvalDate(dfDateofValuation.getValue());
			evalobj.setCheckedBy(tfVerifiedBy.getValue());
			evalobj.setCheckedDt(dfVerifiedDate.getValue());
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
			
			String numberOnly = tfFairMarketRate.getValue().replaceAll("[^\\d.]", "");
			uiflowdata.setAmountInWords(beanEvaluation
					.getAmountInWords(numberOnly));
			if( numberOnly.trim().length()==0){
				numberOnly="0";	
			}
			evalobj.setPropertyValue(Double.valueOf(numberOnly));
			//beanEvaluation.saveorUpdateEvalDetails(evalobj);
			uiflowdata.setEvalDtls(evalobj);
			if(tfEvaluationNumber.isValid() && slBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid())
			{
				
				if(count ==0){
				beanEvaluation.saveorUpdateEvalDetails(evalobj);
				
				valid = true;
				}
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblSaveNotification.setValue("Successfully Submitted");
			}
			
			if(valid){
				/*populateAndConfig(false);
				resetAllFieldsFields();*/
				btnSubmit.setEnabled(false);
			}else{
				btnSubmit.setComponentError(new UserError("Form is invalid"));
			}
		}catch(Exception e){
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
		slBankBranch.setComponentError(null);
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
		if(slBankBranch.getValue()==null){
			slBankBranch.setComponentError(new UserError("Select Bank Branch"));
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
		// TODO Auto-generated method stub
		uiflowdata = new UIFlowData();
		// for save evaluation details
		boolean valid = false;
		try {
			TPemCmEvalDetails evalobj = new TPemCmEvalDetails();

			evalobj.setDocId(headerid);
			System.out.println("Headerid-->"+headerid);
			evalobj.setDocDate(dfDateofValuation.getValue());
			evalobj.setEvalNo(tfEvaluationNumber.getValue());
			/*if(tfEvaluationNumber.getValue()!=null || tfEvaluationNumber.getValue().trim().length()>0){
			tfEvaluationNumber.setComponentError(null);
			}else
			{
				tfEvaluationNumber.setComponentError(new UserError("Evaluation number is already Exist"));	
			}*/
			evalobj.setEvalDate(dfDateofValuation.getValue());
			evalobj.setValuationBy(tfValuatedBy.getValue());
			MPemCmBank bankid=new MPemCmBank();
			bankid.setBankId(selectedBankid);
			evalobj.setBankId(bankid);
			evalobj.setDoctype(screenName);
			evalobj.setCompanyId(selectCompanyid);
			evalobj.setBankBranch((String)slBankBranch.getValue());
			evalobj.setEvalPurpose(tfEvaluationPurpose.getValue());
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
			}

			try {
				saveDescriptionDetails();
			} catch (Exception e) {
			}

			try {
				saveCharacterDetails();
			} catch (Exception e) {
				
			}
			try {
				savePlanApprovalDetails();
			} catch (Exception e) {
			}
			try {
				saveGuidelineValue();
			} catch (Exception e) {
			}
			try {
				saveProductImageDetails();
			} catch (Exception e) {
			}
			try {
				uiflowdata.setValuationPurpose(tfEvaluationPurpose.getValue());
				uiflowdata.setPropertyAddress(tfPropertyAddress.getValue());
				uiflowdata.setBankBranch((String)slBankBranch.getValue());
				uiflowdata.setEvalnumber(tfEvaluationNumber.getValue());
				uiflowdata.setCustomername(tfCustomerName.getValue());
				if (dfDateofValuation.getValue() != null) {
					SimpleDateFormat dt1 = new SimpleDateFormat("dd-MMM-yyy");
					uiflowdata.setValuationDate(dt1.format(dfDateofValuation
							.getValue()));
				}
				if (dfVerifiedDate.getValue() != null) {
					SimpleDateFormat dt1 = new SimpleDateFormat("dd-MMM-yyy");
					uiflowdata.setInspectionDate(dt1.format(dfVerifiedDate
							.getValue()));
				}
				BigDecimal site=new BigDecimal(0.00);
				BigDecimal fair=new BigDecimal(1.00);
				BigDecimal salbale=new BigDecimal(435.60);
				try{
					site = new BigDecimal(tfAreaofLand.getValue().replaceAll("[^\\d.]", ""));
				}catch(Exception e){
					site = new BigDecimal("0.00");
					
				}
				try{
					site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString();
					fair.multiply(site.divide(salbale, 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(tfAdopetdMarketRate.getValue().replaceAll("[^\\d.]", "")));
					tfAreaofLand.setValue(site.toString()+" sft (or) "+site.divide(salbale, 2, RoundingMode.HALF_UP).toPlainString()+" cents");
					tfFairMarketRate.setValue(XMLUtil.IndianFormat(new BigDecimal(fair.multiply(site.divide(salbale, 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(tfAdopetdMarketRate.getValue().replaceAll("[^\\d.]", ""))).toString())));
				}catch(Exception e){
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
				//Bill
				//tfFairMarketRate.setValue(tfFairMarketRate.getValue().replaceAll("[^\\d.]", ""));
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
			}
			try {
				saveValuationofLandDetails();
			} catch (Exception e) {
			}
			try {
				savePropertyValueDetails();
			} catch (Exception e) {
			}
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="LAND";
			ByteArrayOutputStream recvstram = XMLUtil.doMarshall(uiflowdata);
			XMLUtil.getWordDocument(recvstram, evalNumber+"_"+customer+"_"+propertyType,
					strXslFile);
			uiflowdata.setEvalDtls(evalobj);
			if(tfEvaluationNumber.isValid() && slBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid())
			{
				if(count ==0){
				beanEvaluation.saveorUpdateEvalDetails(evalobj);
				
				valid = true;
				}
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblSaveNotification.setValue(ApplicationConstants.saveMsg);
			}
			
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

		}
		TPemCmPropImage propObj=new TPemCmPropImage();
		if(filevalue)
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
			 
		}
		}
		else
		{
			propObj.setPropimage1(null);
		}
		if(filevalue1)
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
			 
		}
		}
		else
		{
			propObj.setPropimage2(null);
		}
		    
		propObj.setDocId(headerid);
	    propObj.setLastupdateddt(new Date());
	    propObj.setLastupdatedby(loginusername);
		beanPropImage.saveorUpdatePropImage(propObj);
		uiflowdata.getPropImage().add(propObj);
	}

	private void saveOwnerDetails() {
		try {
			beanOwner.deleteExistingOwnerDetails(headerid);
		} catch (Exception e) {
		}

		//btnSave.setComponentError(null);
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
		obj.setFieldValue(tfCustomerName.getValue());
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
						tfAreaofLand.setValue(siteArea.toString());
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

	void saveDescriptionDetails() {
		try {
			beanPropDesc.deleteExistingPropDescription(headerid);
		} catch (Exception e) {

		}

		TPemCmPropDescription obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPostalAddress.getCaption());
		obj.setFieldValue(tfPostalAddress.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfSiteNumber.getCaption());
		obj.setFieldValue(tfSiteNumber.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription1().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfSFNumber.getCaption());
		obj.setFieldValue(tfSFNumber.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription1().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfNewSFNumber.getCaption());
		obj.setFieldValue(tfNewSFNumber.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription1().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfVillage.getCaption());
		obj.setFieldValue(tfVillage.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription1().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfTaluk.getCaption());
		obj.setFieldValue(tfTaluk.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription1().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDistCorpPanc.getCaption());
		obj.setFieldValue(tfDistCorpPanc.getValue());
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription1().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfLocationSketch.getCaption());
		obj.setFieldValue(tfLocationSketch.getValue());
		obj.setOrderNo(8L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription1().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfProTaxReceipt.getCaption());
		obj.setFieldValue(tfProTaxReceipt.getValue());
		obj.setOrderNo(9L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription2().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfElecServiceConnNo.getCaption());
		obj.setFieldValue(tfElecServiceConnNo.getValue());
		obj.setOrderNo(10L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription2().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfElecConnecName.getCaption());
		obj.setFieldValue(tfElecConnecName.getValue());
		obj.setOrderNo(11L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription2().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(slHighMiddPoor.getCaption());
		obj.setFieldValue((String) slHighMiddPoor.getValue());
		obj.setOrderNo(12L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription3().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(slUrbanSemiRural.getCaption());
		obj.setFieldValue((String) slUrbanSemiRural.getValue());
		obj.setOrderNo(13L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription3().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(slResiIndustCommer.getCaption());
		obj.setFieldValue((String) slResiIndustCommer.getValue());
		obj.setOrderNo(14L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription3().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(slProOccupiedBy.getCaption());
		obj.setFieldValue((String) slProOccupiedBy.getValue());
		obj.setOrderNo(15L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription4().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel("If tenanted what is the total monthly rent");
		obj.setFieldValue(tfMonthlyRent.getValue());
		obj.setOrderNo(16L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription4().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfCoverUnderStatCentral.getCaption());
		obj.setFieldValue(tfCoverUnderStatCentral.getValue());
		obj.setOrderNo(17L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription4().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfAnyConversionLand.getCaption());
		obj.setFieldValue(tfAnyConversionLand.getValue());
		obj.setOrderNo(18L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription4().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfExtentSite.getCaption());
		obj.setFieldValue(tfExtentSite.getValue());
		obj.setOrderNo(19L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription5().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfYearAcquistion.getCaption());
		obj.setFieldValue(tfYearAcquistion.getValue());
		obj.setOrderNo(20L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription5().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPurchaseValue.getCaption());
		obj.setFieldValue(tfPurchaseValue.getValue());
		obj.setOrderNo(21L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription5().add(obj);

		obj = new TPemCmPropDescription();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPropLandmark.getCaption());
		obj.setFieldValue(tfPropLandmark.getValue());
		obj.setOrderNo(22L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropDesc.saveorUpdatePropDescription(obj);
		uiflowdata.getPropertyDescription5().add(obj);
		if (tfDynamicDescProp1.getValue() != null
				&& tfDynamicDescProp1.getValue().trim().length() > 0) {
			obj = new TPemCmPropDescription();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicDescProp1.getCaption());
			obj.setFieldValue((String) tfDynamicDescProp1.getValue());
			obj.setOrderNo(23L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPropDesc.saveorUpdatePropDescription(obj);
			uiflowdata.getPropertyDescription5().add(obj);
		}

		if (tfDynamicDescProp2.getValue() != null
				&& tfDynamicDescProp2.getValue().trim().length() > 0) {
			obj = new TPemCmPropDescription();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicDescProp2.getCaption());
			obj.setFieldValue((String) tfDynamicDescProp2.getValue());
			obj.setOrderNo(24L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPropDesc.saveorUpdatePropDescription(obj);
			uiflowdata.getPropertyDescription5().add(obj);
		}
	}

	void saveCharacterDetails() {
		try {
			beanCharcter.deleteExistingSbiPropChartrstic(headerid);
		} catch (Exception e) {

		}

		TPemSbiPropChartrstic obj = new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slLocalClass.getCaption());
		obj.setFieldValue((String) slLocalClass.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slSurroundDevelop.getCaption());
		obj.setFieldValue((String) slSurroundDevelop.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFlood.getCaption());
		obj.setFieldValue(tfFlood.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slFeasibility.getCaption());
		obj.setFieldValue((String) slFeasibility.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slLandLevel.getCaption());
		obj.setFieldValue((String) slLandLevel.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slLandShape.getCaption());
		obj.setFieldValue((String) slLandShape.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slTypeUse.getCaption());
		obj.setFieldValue((String) slTypeUse.getValue());
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfUsageRestriction.getCaption());
		obj.setFieldValue(tfUsageRestriction.getValue());
		obj.setOrderNo(8L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slIsPlot.getCaption());
		obj.setFieldValue((String) slIsPlot.getValue());
		obj.setOrderNo(9L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfApprveNo.getCaption());
		obj.setFieldValue(tfApprveNo.getValue());
		obj.setOrderNo(10L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct1().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfNoReason.getCaption());
		obj.setFieldValue(tfNoReason.getValue());
		obj.setOrderNo(11L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct1().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfSubdivide.getCaption());
		obj.setFieldValue(tfSubdivide.getValue());
		obj.setOrderNo(12L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct1().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slDrawApproval.getCaption());
		obj.setFieldValue((String) slDrawApproval.getValue());
		obj.setOrderNo(13L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct2().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slCornerInter.getCaption());
		obj.setFieldValue((String) slCornerInter.getValue());
		obj.setOrderNo(14L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct2().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slRoadFacility.getCaption());
		obj.setFieldValue((String) slRoadFacility.getValue());
		obj.setOrderNo(15L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct2().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slTypeRoad.getCaption());
		obj.setFieldValue((String) slTypeRoad.getValue());
		obj.setOrderNo(16L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct3().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slRoadWidth.getCaption());
		obj.setFieldValue((String) slRoadWidth.getValue());
		obj.setOrderNo(17L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct3().add(obj);
		
		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slLandLock.getCaption());
		obj.setFieldValue((String) slLandLock.getValue());
		obj.setOrderNo(18L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct4().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slWaterPotential.getCaption());
		obj.setFieldValue((String) slWaterPotential.getValue());
		obj.setOrderNo(19L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct4().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slUnderSewerage.getCaption());
		obj.setFieldValue((String) slUnderSewerage.getValue());
		obj.setOrderNo(20L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct4().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(slPowerSupply.getCaption());
		obj.setFieldValue((String) slPowerSupply.getValue());
		obj.setOrderNo(21L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct4().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfAdvantageSite.getCaption());
		obj.setFieldValue(tfAdvantageSite.getValue());
		obj.setOrderNo(22L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct4().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDisadvantageSite.getCaption());
		obj.setFieldValue(tfDisadvantageSite.getValue());
		obj.setOrderNo(23L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct4().add(obj);

		obj =new TPemSbiPropChartrstic();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfGeneralRemarks.getCaption());
		obj.setFieldValue(tfGeneralRemarks.getValue());
		obj.setOrderNo(24L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCharcter.saveorUpdateSbiPropChartrstic(obj);
		uiflowdata.getPropertyConstruct4().add(obj);

		if (tfDynamicCharacter1.getValue() != null
				&& tfDynamicCharacter1.getValue().trim().length() > 0) {
			obj =new TPemSbiPropChartrstic();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicCharacter1.getCaption());
			obj.setFieldValue((String) tfDynamicCharacter1.getValue());
			obj.setOrderNo(25L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanCharcter.saveorUpdateSbiPropChartrstic(obj);
			uiflowdata.getPropertyConstruct4().add(obj);
		}

		if (tfDynamicCharacter2.getValue() != null
				&& tfDynamicCharacter2.getValue().trim().length() > 0) {
			obj =new TPemSbiPropChartrstic();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicCharacter2.getCaption());
			obj.setFieldValue((String) tfDynamicCharacter2.getValue());
			obj.setOrderNo(26L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanCharcter.saveorUpdateSbiPropChartrstic(obj);
			uiflowdata.getPropertyConstruct4().add(obj);
		}
	}

	private void savePlanApprovalDetails() {
		try {
			beanPlanApprvl.deleteExistingPropOldPlanApprvl(headerid);
		} catch (Exception e) {

		}

		TPemCmPropOldPlanApprvl obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfLandandBuilding.getCaption());
		obj.setFieldValue((String) tfLandandBuilding.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPlanApprovedBy.getCaption());
		obj.setFieldValue((String) tfPlanApprovedBy.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(dfLicenseFrom.getCaption());
		obj.setFieldValue((String) dfLicenseFrom.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
			beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slIsLicenceForced.getCaption());
		obj.setFieldValue((String) slIsLicenceForced.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
			beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slAllApprovalRecved.getCaption());
		obj.setFieldValue((String) slAllApprovalRecved.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
			beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		if (tfDynamicPlanApproval1.getValue() != null
				&& tfDynamicPlanApproval1.getValue().trim().length() > 0) {
			obj = new TPemCmPropOldPlanApprvl();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicPlanApproval1.getCaption());
			obj.setFieldValue((String) tfDynamicPlanApproval1.getValue());
			obj.setOrderNo(6L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
				beanPlanApprvl.savePropOldPlanApprvl(obj);
			uiflowdata.getPlanApproval().add(obj);
		}

		if (tfDynamicPlanApproval2.getValue() != null
				&& tfDynamicPlanApproval2.getValue().trim().length() > 0) {
			obj = new TPemCmPropOldPlanApprvl();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicPlanApproval2.getCaption());
			obj.setFieldValue((String) tfDynamicPlanApproval2.getValue());
			obj.setOrderNo(7L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
				beanPlanApprvl.savePropOldPlanApprvl(obj);
			uiflowdata.getPlanApproval().add(obj);
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
		beanlandvaluation.saveorUpdateLandValution(obj);;
		uiflowdata.getLandval().add(obj);

		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfMarketRate.getCaption());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfMarketRate.getValue()))+"/cent");
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);;
		uiflowdata.getLandval().add(obj);
		
		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel("Guideline rate obtained from the Registrar's Office");
		obj.setFieldValue(tfGuiderate.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);;
		uiflowdata.getLandval().add(obj);

		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfAdopetdMarketRate.getCaption());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfAdopetdMarketRate.getValue()))+"/cent");
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);;
		uiflowdata.getLandval().add(obj);

		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFairMarketRate.getCaption());
		obj.setFieldValue("Rs. "+tfFairMarketRate.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);;
		uiflowdata.getLandval().add(obj);
		if(tfDynamicValuation1.getValue()!=null&&tfDynamicValuation1.getValue().trim().length()>0)
		{
		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicValuation1.getCaption());
		obj.setFieldValue((String) tfDynamicValuation1.getValue());
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);;
		uiflowdata.getLandval().add(obj);
		}
		if(tfDynamicValuation2.getValue()!=null&&tfDynamicValuation2.getValue().trim().length()>0)
		{
		obj = new TPemCmLandValutnData();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicValuation2.getCaption());
		obj.setFieldValue((String) tfDynamicValuation2.getValue());
		obj.setOrderNo(8L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanlandvaluation.saveorUpdateLandValution(obj);;
		uiflowdata.getLandval().add(obj);
		}
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
				slBankBranch.setValue((String) itselect.getItemProperty(
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
			editPropDescDetails();
		} catch (Exception e) {
			
		}

		try {
			editCharacterDetails();
		} catch (Exception e) {
			
		}

		try {
			editPlanApprovalDetails();
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
			editPropertyImageDetails();
		} catch (Exception e) {
			
		}		
		try{
			editPropertyValueDetails();
		}catch(Exception e){
				
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
			layoutOwnerDetails1.addComponent(btnAddNorDoc);
			layoutOwnerDetails1.setComponentAlignment(btnAddNorDoc,Alignment.BOTTOM_RIGHT);
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
			tfCustomerName.setValue(obj1.getFieldValue());
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
		panelNormalDocumentDetails.setComponentAlignment(btnAddNorDoc,Alignment.BOTTOM_RIGHT);
				
		for (TPemCmPropDocDetails obj : doclist) {

			panelNormalDocumentDetails
					.addComponent(new ComponentIteratorNormlDoc(obj
							.getFieldLabel(), obj.getApprovalYN(), obj
							.getApproveAuth(), obj.getApproveRef()));
		}

	}

	void editLegalDocuments() {
		List<TPemCmPropLegalDocs> doclist =legalDoc.getPropLegalDocsList(headerid);
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

	void editPropDescDetails() {
		try {
			List<TPemCmPropDescription> descList = beanPropDesc.getPropApplcntEstmateList(headerid);

			TPemCmPropDescription obj1 = descList.get(0);
			tfPostalAddress.setValue(obj1.getFieldValue());
			tfPostalAddress.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(1);
			tfSiteNumber.setValue(obj1.getFieldValue());
			tfSiteNumber.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(2);
			tfSFNumber.setValue(obj1.getFieldValue());
			tfSFNumber.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(3);
			tfNewSFNumber.setValue(obj1.getFieldValue());
			tfNewSFNumber.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(4);
			tfVillage.setValue(obj1.getFieldValue());
			tfVillage.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(5);
			tfTaluk.setValue(obj1.getFieldValue());
			tfTaluk.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(6);
			tfDistCorpPanc.setValue(obj1.getFieldValue());
			tfDistCorpPanc.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(7);
			tfLocationSketch.setValue(obj1.getFieldValue());
			tfLocationSketch.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(8);
			tfProTaxReceipt.setValue(obj1.getFieldValue());
			tfProTaxReceipt.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(9);
			tfElecServiceConnNo.setValue(obj1.getFieldValue());
			tfElecServiceConnNo.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(10);
			tfElecConnecName.setValue(obj1.getFieldValue());
			tfElecConnecName.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(11);
			slHighMiddPoor.setValue(obj1.getFieldValue());
			slHighMiddPoor.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(12);
			slUrbanSemiRural.setValue(obj1.getFieldValue());
			slUrbanSemiRural.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(13);
			slResiIndustCommer.setValue(obj1.getFieldValue());
			slResiIndustCommer.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(14);
			slProOccupiedBy.setValue(obj1.getFieldValue());
			slProOccupiedBy.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(15);
			tfMonthlyRent.setValue(obj1.getFieldValue());
			//tfMonthlyRent.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(16);
			tfCoverUnderStatCentral.setValue(obj1.getFieldValue());
			tfCoverUnderStatCentral.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(17);
			tfAnyConversionLand.setValue(obj1.getFieldValue());
			tfAnyConversionLand.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(18);
			tfExtentSite.setValue(obj1.getFieldValue());
			tfExtentSite.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(19);
			tfYearAcquistion.setValue(obj1.getFieldValue());
			tfYearAcquistion.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(20);
			tfPurchaseValue.setValue(obj1.getFieldValue());
			tfPurchaseValue.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(21);
			tfPropLandmark.setValue(obj1.getFieldValue());
			tfPropLandmark.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(22);
			tfDynamicDescProp1.setValue(obj1.getFieldValue());
			tfDynamicDescProp1.setCaption(obj1.getFieldLabel());
			obj1 = descList.get(23);
			tfDynamicDescProp2.setValue(obj1.getFieldValue());
			tfDynamicDescProp2.setCaption(obj1.getFieldLabel());

		} catch (Exception e) {

		}
	}

	void editCharacterDetails() {
		try {
			List<TPemSbiPropChartrstic> propList = beanCharcter.getSbiPropChartrsticList(headerid);

			TPemSbiPropChartrstic obj1 = propList.get(0);
			slLocalClass.setValue(obj1.getFieldValue());
			slLocalClass.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(1);
			slSurroundDevelop.setValue(obj1.getFieldValue());
			slSurroundDevelop.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(2);
			tfFlood.setValue(obj1.getFieldValue());
			tfFlood.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(3);
			slFeasibility.setValue(obj1.getFieldValue());
			slFeasibility.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(4);
			slLandLevel.setValue(obj1.getFieldValue());
			slLandLevel.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(5);
			slLandShape.setValue(obj1.getFieldValue());
			slLandShape.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(6);
			slTypeUse.setValue(obj1.getFieldValue());
			slTypeUse.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(7);
			tfUsageRestriction.setValue(obj1.getFieldValue());
			tfUsageRestriction.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(8);
			slIsPlot.setValue(obj1.getFieldValue());
			slIsPlot.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(9);
			tfApprveNo.setValue(obj1.getFieldValue());
			tfApprveNo.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(10);
			tfNoReason.setValue(obj1.getFieldValue());
			tfNoReason.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(11);
			tfSubdivide.setValue(obj1.getFieldValue());
			tfSubdivide.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(12);
			slDrawApproval.setValue(obj1.getFieldValue());
			slDrawApproval.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(13);
			slCornerInter.setValue(obj1.getFieldValue());
			slCornerInter.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(14);
			slRoadFacility.setValue(obj1.getFieldValue());
			slRoadFacility.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(15);
			slTypeRoad.setValue(obj1.getFieldValue());
			slTypeRoad.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(16);
			slRoadWidth.setValue(obj1.getFieldValue());
			slRoadWidth.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(17);
			slLandLock.setValue(obj1.getFieldValue());
			slLandLock.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(18);
			slWaterPotential.setValue(obj1.getFieldValue());
			slWaterPotential.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(19);
			slUnderSewerage.setValue(obj1.getFieldValue());
			slUnderSewerage.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(20);
			slPowerSupply.setValue(obj1.getFieldValue());
			slPowerSupply.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(21);
			tfAdvantageSite.setValue(obj1.getFieldValue());
			tfAdvantageSite.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(22);
			tfDisadvantageSite.setValue(obj1.getFieldValue());
			tfDisadvantageSite.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(23);
			tfGeneralRemarks.setValue(obj1.getFieldValue());
			tfGeneralRemarks.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(24);
			tfDynamicCharacter1.setValue(obj1.getFieldValue());
			tfDynamicCharacter1.setCaption(obj1.getFieldLabel());
			obj1 = propList.get(25);
			tfDynamicCharacter2.setValue(obj1.getFieldValue());
			tfDynamicCharacter2.setCaption(obj1.getFieldLabel());

		} catch (Exception e) {

		}
	}

	private void editPlanApprovalDetails() {
		try {
			List<TPemCmPropOldPlanApprvl> list = beanPlanApprvl.getPropOldPlanApprvlList(headerid);
			TPemCmPropOldPlanApprvl obj1 = list.get(0);
			tfLandandBuilding.setValue(obj1.getFieldValue());
			tfLandandBuilding.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfPlanApprovedBy.setValue(obj1.getFieldValue());
			tfPlanApprovedBy.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			dfLicenseFrom.setValue(obj1.getFieldValue());
			dfLicenseFrom.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			slIsLicenceForced.setValue(obj1.getFieldValue());
			slIsLicenceForced.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			slAllApprovalRecved.setValue(obj1.getFieldValue());
			slAllApprovalRecved.setCaption(obj1.getFieldLabel());
			obj1 = list.get(5);
			tfDynamicPlanApproval1.setValue(obj1.getFieldValue());
			tfDynamicPlanApproval1.setCaption(obj1.getFieldLabel());
			tfDynamicPlanApproval1.setVisible(true);
			obj1 = list.get(6);
			tfDynamicPlanApproval2.setValue(obj1.getFieldValue());
			tfDynamicPlanApproval2.setCaption(obj1.getFieldLabel());
			tfDynamicPlanApproval2.setVisible(true);
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
			tfGuiderate.setValue(obj1.getFieldValue());
			//tfGuiderate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			tfAdopetdMarketRate.setValue(obj1.getFieldValue().replace("Rs. ","").replace("/cent","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			tfAdopetdMarketRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(5);
			tfFairMarketRate.setValue(obj1.getFieldValue().replace("Rs. ",""));
			tfFairMarketRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(6);
			tfDynamicValuation1.setValue(obj1.getFieldValue());
			tfDynamicValuation1.setCaption(obj1.getFieldLabel());
			tfDynamicValuation1.setVisible(true);
			obj1 = list.get(7);
			tfDynamicValuation2.setValue(obj1.getFieldValue());
			tfDynamicValuation2.setCaption(obj1.getFieldLabel());
			tfDynamicValuation2.setVisible(true);

		} catch (Exception e) {

			
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


	private void setComponentStyle() {
		
		slSearchBankbranch.setNullSelectionAllowed(false);
		slSearchBankbranch.setInputPrompt(Common.SELECT_PROMPT);
		tfSearchCustomer.setInputPrompt("Enter Customer");
		tfSearchEvalNumber.setInputPrompt("Enter Evaluation Number");
		// TODO Auto-generated method stub
		tfEvaluationNumber.setWidth(strComponentWidth);
		slBankBranch.setWidth(strComponentWidth);
		tfEvaluationPurpose.setWidth(strComponentWidth);
		dfDateofValuation.setWidth("150px");
		tfValuatedBy.setWidth(strComponentWidth);
		dfVerifiedDate.setWidth("150px");
		tfVerifiedBy.setWidth(strComponentWidth);
		tfDynamicEvaluation1.setWidth(strComponentWidth);
		tfDynamicEvaluation2.setWidth(strComponentWidth);

		dfDateofValuation.addValidator(new DateValidation("Invalid date entered"));
		dfDateofValuation.setImmediate(true);
		dfVerifiedDate.addValidator(new DateValidation("Invalid date entered"));
		dfVerifiedDate.setImmediate(true);
		
		dfDateofValuation.setDateFormat("dd-MMM-yyy");
		dfVerifiedDate.setDateFormat("dd-MMM-yyy");
		dfSearchEvalDate.setDateFormat("dd-MMM-yyy");

		// for dynamic
		btnDynamicEvaluation1.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicEvaluation1.setStyleName(Runo.BUTTON_LINK);
		btnDynamicPlanApproval.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicPlanApproval.setStyleName(Runo.BUTTON_LINK);
		btnDynamicDescProp
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicDescProp.setStyleName(Runo.BUTTON_LINK);
		btnDynamicCharacter
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicCharacter.setStyleName(Runo.BUTTON_LINK);

		btnDynamicValuation
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicValuation.setStyleName(Runo.BUTTON_LINK);

		// for default values
		tfGuiderate.setValue("Details enclosed");
		tfCoverUnderStatCentral.setValue(Common.strNA);
		tfAnyConversionLand.setValue(Common.strNA);
		tfMonthlyRent.setValue(Common.strNA);
		tfElecServiceConnNo.setValue(Common.strNA);
		tfProTaxReceipt.setValue(Common.strNA);
		tfFlood.setValue(Common.strNil);
		tfGeneralRemarks.setValue(Common.strNil);

		// for owner/asset
		tfCustomerName.setWidth(strComponentWidth);
		slPropertyDesc.setWidth(strComponentWidth);
	
		tfCustomerAddr.setWidth(strComponentWidth);
		tfCustomerAddr.setHeight("130px");
		slPropertyDesc.setNullSelectionAllowed(false);

		tfLandMark.setWidth(strComponentWidth);
		tfPropertyAddress.setWidth(strComponentWidth);
		tfPropertyAddress.setHeight("130px");
		tfDynamicAsset1.setWidth(strComponentWidth);
		tfDynamicAsset2.setWidth(strComponentWidth);
		btnDynamicAsset.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicAsset.setStyleName(Runo.BUTTON_LINK);
		btnAddOwner.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddOwner.setStyleName(Runo.BUTTON_LINK);

		// for document
		btnAddLegalDoc.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddNorDoc.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddNorDoc.setStyleName(Runo.BUTTON_LINK);
		btnAddLegalDoc.setStyleName(Runo.BUTTON_LINK);

		// /for adjoin property and dimension
		btnAddAdjoinProperty.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnAddAdjoinProperty.setStyleName(Runo.BUTTON_LINK);

		btnAddDimension.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddDimension.setStyleName(Runo.BUTTON_LINK);

		btnAddGuideline.setStyleName(Runo.BUTTON_LINK);
		btnAddGuideline.setIcon(new ThemeResource(Common.strAddIcon));

		// for description of the property
		tfPostalAddress.setWidth(strComponentWidth);
		tfSiteNumber.setWidth(strComponentWidth);
		tfSFNumber.setWidth(strComponentWidth);
		tfNewSFNumber.setWidth(strComponentWidth);
		tfVillage.setWidth(strComponentWidth);
		tfTaluk.setWidth(strComponentWidth);
		tfDistCorpPanc.setWidth(strComponentWidth);
		tfLocationSketch.setWidth(strComponentWidth);
		tfProTaxReceipt.setWidth(strComponentWidth);
		tfElecServiceConnNo.setWidth(strComponentWidth);
		tfElecConnecName.setWidth(strComponentWidth);
		slHighMiddPoor.setWidth(strComponentWidth);
		slUrbanSemiRural.setWidth(strComponentWidth);
		slResiIndustCommer.setWidth(strComponentWidth);
		slProOccupiedBy.setWidth(strComponentWidth);
		tfMonthlyRent.setWidth(strComponentWidth);
		tfCoverUnderStatCentral.setWidth(strComponentWidth);
		tfAnyConversionLand.setWidth(strComponentWidth);
		tfExtentSite.setWidth(strComponentWidth);
		tfYearAcquistion.setWidth(strComponentWidth);
		tfPurchaseValue.setWidth(strComponentWidth);
		tfPropLandmark.setWidth(strComponentWidth);
		tfDynamicDescProp1.setWidth(strComponentWidth);
		tfDynamicDescProp2.setWidth(strComponentWidth);
		// for Charcteristics of the site
		slLocalClass.setWidth(strSplComponentWidth);
		slSurroundDevelop.setWidth(strSplComponentWidth);
		tfFlood.setWidth(strSplComponentWidth);
		slFeasibility.setWidth(strSplComponentWidth);
		slLandLevel.setWidth(strSplComponentWidth);
		slLandShape.setWidth(strSplComponentWidth);
		slTypeUse.setWidth(strSplComponentWidth);
		tfUsageRestriction.setWidth(strSplComponentWidth);
		slIsPlot.setWidth(strSplComponentWidth);
		tfApprveNo.setWidth(strSplComponentWidth);
		tfNoReason.setWidth(strSplComponentWidth);
		tfSubdivide.setWidth(strSplComponentWidth);
		slDrawApproval.setWidth(strSplComponentWidth);
		slCornerInter.setWidth(strSplComponentWidth);
		slRoadFacility.setWidth(strSplComponentWidth);
		slTypeRoad.setWidth(strSplComponentWidth);
		slRoadWidth.setWidth(strSplComponentWidth);
		slLandLock.setWidth(strSplComponentWidth);
		slWaterPotential.setWidth(strSplComponentWidth);
		slUnderSewerage.setWidth(strSplComponentWidth);
		slPowerSupply.setWidth(strSplComponentWidth);
		tfAdvantageSite.setWidth(strSplComponentWidth);
		tfDisadvantageSite.setWidth(strSplComponentWidth);
		tfGeneralRemarks.setWidth(strSplComponentWidth);
		tfDynamicCharacter1.setWidth(strSplComponentWidth);
		tfDynamicCharacter2.setWidth(strSplComponentWidth);

		// for land valuation
		tfAreaofLand.setWidth(strComponentWidth);
		tfNorthandSouth.setWidth(strComponentWidth);
		tfMarketRate.setWidth(strComponentWidth);
		tfGuiderate.setWidth(strComponentWidth);
		tfAdopetdMarketRate.setWidth(strComponentWidth);
		tfFairMarketRate.setWidth(strComponentWidth);
		tfGuidelineRate.setWidth(strComponentWidth);
		tfRealziableRate.setWidth(strComponentWidth);
		tfDistressRate.setWidth(strComponentWidth);
		tfDynamicValuation1.setWidth(strComponentWidth);
		tfDynamicValuation2.setWidth(strComponentWidth);

		// for details of plan approval
		tfLandandBuilding.setWidth(strComponentWidth);
		tfPlanApprovedBy.setWidth(strComponentWidth);
		dfLicenseFrom.setWidth(strComponentWidth);
		tfDynamicPlanApproval1.setWidth(strComponentWidth);
		slIsLicenceForced.setWidth(strComponentWidth);
		slAllApprovalRecved.setWidth(strComponentWidth);
		tfDynamicPlanApproval2.setWidth(strComponentWidth);

		tfLandMark.setWidth(strComponentWidth);
		tfPropertyAddress.setWidth(strComponentWidth);
		tfPropertyAddress.setHeight("130px");
		tfDynamicAsset1.setWidth(strComponentWidth);
		tfDynamicAsset2.setWidth(strComponentWidth);
		
		//set Null representation

		// TODO Auto-generated method stub
		tfEvaluationNumber.setNullRepresentation("");
		slBankBranch.setNullSelectionAllowed(false);
		tfEvaluationPurpose.setNullRepresentation("");
		tfValuatedBy.setNullRepresentation("");
		tfVerifiedBy.setNullRepresentation("");
		tfDynamicEvaluation1.setNullRepresentation("");
		tfDynamicEvaluation2.setNullRepresentation("");
		slSearchBankbranch.setNullSelectionAllowed(false);
		// for owner/asset
		tfCustomerName.setNullRepresentation("");
		slPropertyDesc.setNullSelectionAllowed(false);
		tfCustomerAddr.setNullRepresentation("");
		slPropertyDesc.setNullSelectionAllowed(false);

		tfLandMark.setNullRepresentation("");
		tfPropertyAddress.setNullRepresentation("");
		tfDynamicAsset1.setNullRepresentation("");
		tfDynamicAsset2.setNullRepresentation("");
		// for description of the property
		tfPostalAddress.setNullRepresentation("");
		tfSiteNumber.setNullRepresentation("");
		tfSFNumber.setNullRepresentation("");
		tfNewSFNumber.setNullRepresentation("");
		tfVillage.setNullRepresentation("");
		tfTaluk.setNullRepresentation("");
		tfDistCorpPanc.setNullRepresentation("");
		tfLocationSketch.setNullRepresentation("");
		tfProTaxReceipt.setNullRepresentation("");
		tfElecServiceConnNo.setNullRepresentation("");
		tfElecConnecName.setNullRepresentation("");
		slHighMiddPoor.setNullSelectionAllowed(false);
		slUrbanSemiRural.setNullSelectionAllowed(false);
		slResiIndustCommer.setNullSelectionAllowed(false);
		slProOccupiedBy.setNullSelectionAllowed(false);
		tfMonthlyRent.setNullRepresentation("");
		tfCoverUnderStatCentral.setNullRepresentation("");
		tfAnyConversionLand.setNullRepresentation("");
		tfExtentSite.setNullRepresentation("");
		tfYearAcquistion.setNullRepresentation("");
		tfPurchaseValue.setNullRepresentation("");
		tfPropLandmark.setNullRepresentation("");
		tfDynamicDescProp1.setNullRepresentation("");
		tfDynamicDescProp2.setNullRepresentation("");
		// for Charcteristics of the site
		slLocalClass.setNullSelectionAllowed(false);
		slSurroundDevelop.setNullSelectionAllowed(false);
		tfFlood.setNullRepresentation("");
		slFeasibility.setNullSelectionAllowed(false);
		slLandLevel.setNullSelectionAllowed(false);
		slLandShape.setNullSelectionAllowed(false);
		slTypeUse.setNullSelectionAllowed(false);
		tfUsageRestriction.setNullRepresentation("");
		slIsPlot.setNullSelectionAllowed(false);
		tfApprveNo.setNullRepresentation("");
		tfNoReason.setNullRepresentation("");
		tfSubdivide.setNullRepresentation("");
		slDrawApproval.setNullSelectionAllowed(false);
		slCornerInter.setNullSelectionAllowed(false);
		slRoadFacility.setNullSelectionAllowed(false);
		slTypeRoad.setNullSelectionAllowed(false);
		slRoadWidth.setNullSelectionAllowed(false);
		slLandLock.setNullSelectionAllowed(false);
		slWaterPotential.setNullSelectionAllowed(false);
		slUnderSewerage.setNullSelectionAllowed(false);
		slPowerSupply.setNullSelectionAllowed(false);
		tfAdvantageSite.setNullRepresentation("");
		tfDisadvantageSite.setNullRepresentation("");
		tfGeneralRemarks.setNullRepresentation("");
		tfDynamicCharacter1.setNullRepresentation("");
		tfDynamicCharacter2.setNullRepresentation("");

		// for land valuation
		tfAreaofLand.setNullRepresentation("");
		tfNorthandSouth.setNullRepresentation("");
		tfMarketRate.setNullRepresentation("");
		tfGuiderate.setNullRepresentation("");
		tfAdopetdMarketRate.setNullRepresentation("");
		tfFairMarketRate.setNullRepresentation("");
		tfGuidelineRate.setNullRepresentation("");
		tfRealziableRate.setNullRepresentation("");
		tfDistressRate.setNullRepresentation("");
		tfDynamicValuation1.setNullRepresentation("");
		tfDynamicValuation2.setNullRepresentation("");

		// for details of plan approval
		tfLandandBuilding.setNullRepresentation("");
		tfPlanApprovedBy.setNullRepresentation("");
		dfLicenseFrom.setNullRepresentation("");
		tfDynamicPlanApproval1.setNullRepresentation("");
		slIsLicenceForced.setNullSelectionAllowed(false);
		slAllApprovalRecved.setNullSelectionAllowed(false);
		tfDynamicPlanApproval2.setNullRepresentation("");

		tfLandMark.setNullRepresentation("");
		tfPropertyAddress.setNullRepresentation("");
		tfDynamicAsset1.setNullRepresentation("");
		tfDynamicAsset2.setNullRepresentation("");
	}

	private void resetAllFieldsFields() {
		// for evaluation details
		tfEvaluationNumber.setComponentError(null);
		slBankBranch.setComponentError(null);
		dfDateofValuation.setComponentError(null);
		tfEvaluationPurpose.setComponentError(null);
		tfCustomerName.setComponentError(null);
		tfAdopetdMarketRate.setComponentError(null);
		tfEvaluationNumber.setReadOnly(false);
		tfEvaluationNumber.setValue("");
		slBankBranch.setValue(null);
		tfEvaluationPurpose.setValue("Collateral Security to the Bank");
		dfDateofValuation.setValue(null);
		tfValuatedBy.setValue("");
		dfVerifiedDate.setValue(null);
		tfVerifiedBy.setValue("");
		tfDynamicEvaluation1.setValue("");
		tfDynamicEvaluation2.setValue("");
		
		slBankBranch.setInputPrompt(Common.SELECT_PROMPT);
		tfDynamicEvaluation1.setVisible(false);
		tfDynamicEvaluation2.setVisible(false);

		// for customer/owner details
		tfCustomerName.setRequired(true);
		tfCustomerName.setValue("");
		slPropertyDesc.setValue(null);
		tfCustomerAddr.setValue("");
		tfPropertyAddress.setValue("");
		layoutOwnerDetails1.removeAllComponents();
		layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("", ""));
		
		slPropertyDesc.setInputPrompt(Common.SELECT_PROMPT);
		tfCustomerName.setValue("");
		tfCustomerAddr.setValue("");
		tfLandMark.setValue("");
		tfPropertyAddress.setValue("");
		tfDynamicAsset1.setValue("");
		tfDynamicAsset2.setValue("");
		tfDynamicAsset1.setVisible(false);
		tfDynamicAsset2.setVisible(false);

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
		panelDimension.addComponent(btnAddDimension);
		panelDimension.setComponentAlignment(btnAddDimension,
				Alignment.BOTTOM_RIGHT);
		panelDimension.addComponent(new ComponentIterDimensionofPlot(null,
				true, true, true));
		// for description of the property
		tfPostalAddress.setValue("");
		tfSiteNumber.setValue("");
		tfSFNumber.setValue("");
		tfNewSFNumber.setValue("");
		tfVillage.setValue("");
		tfTaluk.setValue("");
		tfDistCorpPanc.setValue("");
		tfLocationSketch.setValue("");
		tfProTaxReceipt.setValue("");
		tfElecServiceConnNo.setValue("");
		tfElecConnecName.setValue("");
		slHighMiddPoor.setValue(null);
		slUrbanSemiRural.setValue(null);
		slResiIndustCommer.setValue(null);
		slProOccupiedBy.setValue(null);
		tfMonthlyRent.setValue("");
		tfCoverUnderStatCentral.setValue("");
		tfAnyConversionLand.setValue("");
		tfExtentSite.setValue("");
		tfYearAcquistion.setValue("");
		tfPurchaseValue.setValue("");
		tfPropLandmark.setValue("");
		tfDynamicDescProp1.setValue("");
		tfDynamicDescProp2.setValue("");
		tfDynamicDescProp1.setVisible(false);
		tfDynamicDescProp2.setVisible(false);
		
		slHighMiddPoor.setInputPrompt(Common.SELECT_PROMPT);
		slUrbanSemiRural.setInputPrompt(Common.SELECT_PROMPT);
		slResiIndustCommer.setInputPrompt(Common.SELECT_PROMPT);
		slProOccupiedBy.setInputPrompt(Common.SELECT_PROMPT);
		// for Charcteristics of the site
		slLocalClass.setValue(null);
		slSurroundDevelop.setValue(null);
		tfFlood.setValue("");
		slFeasibility.setValue(null);
		slLandLevel.setValue(null);
		slLandShape.setValue(null);
		slTypeUse.setValue(null);
		tfUsageRestriction.setValue("");
		slIsPlot.setValue(null);
		tfApprveNo.setValue("");
		tfNoReason.setValue("");
		tfSubdivide.setValue("");
		slDrawApproval.setValue(null);
		slCornerInter.setValue(null);
		slRoadFacility.setValue(null);
		slTypeRoad.setValue(null);
		slRoadWidth.setValue(null);
		slLandLock.setValue(null);
		slWaterPotential.setValue(null);
		slUnderSewerage.setValue(null);
		slPowerSupply.setValue(null);
		tfAdvantageSite.setValue("");
		tfDisadvantageSite.setValue("");
		tfGeneralRemarks.setValue("");
		tfDynamicCharacter1.setValue("");
		tfDynamicCharacter2.setValue("");
		tfDynamicCharacter1.setVisible(false);
		tfDynamicCharacter2.setVisible(false);
		
		slLocalClass.setInputPrompt(Common.SELECT_PROMPT);
		slFeasibility.setInputPrompt(Common.SELECT_PROMPT);
		slLandLevel.setInputPrompt(Common.SELECT_PROMPT);
		slLandShape.setInputPrompt(Common.SELECT_PROMPT);
		slTypeUse.setInputPrompt(Common.SELECT_PROMPT);
		slIsPlot.setInputPrompt(Common.SELECT_PROMPT);
		slCornerInter.setInputPrompt(Common.SELECT_PROMPT);
		slRoadFacility.setInputPrompt(Common.SELECT_PROMPT);
		slTypeRoad.setInputPrompt(Common.SELECT_PROMPT);
		slRoadWidth.setInputPrompt(Common.SELECT_PROMPT);
		slLandLock.setInputPrompt(Common.SELECT_PROMPT);
		slWaterPotential.setInputPrompt(Common.SELECT_PROMPT);
		slUnderSewerage.setInputPrompt(Common.SELECT_PROMPT);
		slPowerSupply.setInputPrompt(Common.SELECT_PROMPT);

		// details of plan approval
		tfLandandBuilding.setValue("");
		tfPlanApprovedBy.setValue("");
		dfLicenseFrom.setValue("");
		tfDynamicPlanApproval1.setValue("");
		slIsLicenceForced.setValue(null);
		slAllApprovalRecved.setValue(null);
		tfDynamicPlanApproval2.setValue("");
		tfDynamicPlanApproval1.setVisible(false);
		tfDynamicPlanApproval2.setVisible(false);

		slIsLicenceForced.setInputPrompt(Common.SELECT_PROMPT);
		slAllApprovalRecved.setInputPrompt(Common.SELECT_PROMPT);
		// for Land Valuation
		tfAreaofLand.setValue("");
		tfNorthandSouth.setValue("");
		tfMarketRate.setValue("");
		tfGuiderate.setValue("");
		tfAdopetdMarketRate.setValue("");
		tfFairMarketRate.setValue("0");
		tfGuidelineRate.setValue("0");
		tfDistressRate.setValue("0");
		tfRealziableRate.setValue("0");
		tfDynamicValuation1.setValue("");
		tfDynamicValuation2.setValue("");
		tfDynamicValuation1.setVisible(false);
		tfDynamicValuation2.setVisible(false);

		// for Guideline area
		layoutGuideline.removeAllComponents();
		layoutGuideline.addComponent(btnAddGuideline);
		layoutGuideline.setComponentAlignment(btnAddGuideline,
				Alignment.BOTTOM_RIGHT);
		layoutGuideline.addComponent(new ComponentIterGuideline("Land", "", "",
				""));

		// for default values
		tfCoverUnderStatCentral.setValue(Common.strNA);
		tfAnyConversionLand.setValue(Common.strNA);
		tfMonthlyRent.setValue(Common.strNA);
		tfElecServiceConnNo.setValue(Common.strNA);
		tfProTaxReceipt.setValue(Common.strNA);
		tfFlood.setValue(Common.strNil);
		tfGeneralRemarks.setValue(Common.strNil);
		
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
	}
	/*
	 * Method for File Download
	 */
	private StreamResource getPDFStream() {
try{
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
	return null;
}
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

	@Override
	public void buttonClick(ClickEvent event) {
		notifications.close();
		if (btnAdd == event.getButton()) {
			btnSave.setVisible(true);
			btnCancel.setVisible(true);
			btnSubmit.setVisible(true);
			tablePanel.setVisible(false);
			searchPanel.setVisible(false);
			mainPanel.setVisible(true);
			headerid = beanEvaluation.getNextSequnceId("seq_pem_evaldtls_docid");
			lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
			lblAddEdit.setVisible(true);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);
			resetAllFieldsFields();

		} else if (btnEdit == event.getButton()) {
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
		if (btnSave == event.getButton()) {
			setComponentError();
			if(tfEvaluationNumber.isValid() && slBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid())
			{
			
			saveEvaluationDetails();
			}
			//btnSave.setVisible(false);
		}
		if(btnSubmit == event.getButton()){
			setComponentError();
			if(tfEvaluationNumber.isValid() && slBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid())
			{
			saveEvaluationDetails();
			updateEvaluationDetails();
			btnSubmit.setEnabled(false);
			btnSave.setEnabled(false);
			}
		//	saveExcel.setVisible(true);
		
		}
		if (btnCancel == event.getButton()) {
			btnSave.setVisible(false);
			btnCancel.setVisible(false);
			btnSubmit.setVisible(false);
			//saveExcel.setVisible(false);
			btnSave.setEnabled(true);
			btnSubmit.setEnabled(true);
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

			hlFileDownloadLayout.removeAllComponents();
			hlFileDownloadLayout.addComponent(btnDownload);
			getExportTableDetails();
			

		} else if (btnSearch == event.getButton()) {
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
		} else if (btnReset == event.getButton()) {
			tfSearchEvalNumber.setValue("");
			//dfSearchEvalDate.setValue(null);
			tfSearchCustomer.setValue("");
			slSearchBankbranch.setValue(null);
			populateAndConfig(false);
		}

		if (btnAddGuideline == event.getButton()) {

			layoutGuideline.addComponent(new ComponentIterGuideline("", "", "",
					""));
		}
		if (btnAddAdjoinProperty == event.getButton()) {

			panelAdjoinProperties
					.addComponent(new ComponentIteratorAdjoinProperty(null,
							true, true, false));
		}

		if (btnAddDimension == event.getButton()) {
			panelDimension.addComponent(new ComponentIterDimensionofPlot(null,
					true, true, false));
		}

		else if (btnAddNorDoc == event.getButton()) {

			panelNormalDocumentDetails
					.addComponent(new ComponentIteratorNormlDoc(null, null, "",
							""));
		}

		else if (btnAddLegalDoc == event.getButton()) {

			panelLegalDocumentDetails
					.addComponent(new ComponentIteratorLegalDoc("", "", null));
		}

		else if (btnAddOwner == event.getButton()) {

			layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("",
					""));
		} else if (btnDynamicEvaluation1 == event.getButton()) {

			strSelectedPanel = "1";
			showSubWindow();

		}
		if (btnDynamicAsset == event.getButton()) {
			strSelectedPanel = "3";
			showSubWindow();

		}

		if (btnDynamicPlanApproval == event.getButton()) {
			strSelectedPanel = "6";
			showSubWindow();

		}
		if (btnDynamicDescProp == event.getButton()) {
			strSelectedPanel = "8";
			showSubWindow();

		}
		if (btnDynamicCharacter == event.getButton()) {
			strSelectedPanel = "9";
			showSubWindow();

		}
		if (btnDynamicValuation == event.getButton()) {
			strSelectedPanel = "10";
			showSubWindow();

		}else if (btnBack == event.getButton()) {
			resetAllFieldsFields();
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
		/*else if (btnView == event.getButton()) {

			StreamResource sr = getPDFStream();
			FileDownloader fileDownloader = new FileDownloader(sr);
			fileDownloader.extend(btnView);

		}
*/
		else if (myButton == event.getButton()) {
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
				}

				else if (strSelectedPanel.equals("3")) {
					if (tfDynamicAsset1.isVisible()) {
						tfDynamicAsset2.setCaption(tfCaption.getValue());
						tfDynamicAsset2.setVisible(true);
					} else {
						tfDynamicAsset1.setCaption(tfCaption.getValue());
						tfDynamicAsset1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("6")) {
					if (tfDynamicPlanApproval1.isVisible()) {
						tfDynamicPlanApproval2.setCaption(tfCaption.getValue());
						tfDynamicPlanApproval2.setVisible(true);
					} else {
						tfDynamicPlanApproval1.setCaption(tfCaption.getValue());
						tfDynamicPlanApproval1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("8")) {
					if (tfDynamicDescProp1.isVisible()) {
						tfDynamicDescProp2.setCaption(tfCaption.getValue());
						tfDynamicDescProp2.setVisible(true);
					} else {
						tfDynamicDescProp1.setCaption(tfCaption.getValue());
						tfDynamicDescProp1.setVisible(true);
					}
				}

				else if (strSelectedPanel.equals("9")) {
					if (tfDynamicCharacter1.isVisible()) {
						tfDynamicCharacter2.setCaption(tfCaption.getValue());
						tfDynamicCharacter2.setVisible(true);
					} else {
						tfDynamicCharacter1.setCaption(tfCaption.getValue());
						tfDynamicCharacter1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("10")) {
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
	}
	
}
