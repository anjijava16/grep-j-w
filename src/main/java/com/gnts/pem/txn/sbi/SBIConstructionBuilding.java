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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.haijian.CSVExporter;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.Common;
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.constants.DateColumnGenerator;
import com.gnts.erputil.ui.AuditRecordsApp;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateValidation;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.txn.common.TPemCmAssetDetails;
import com.gnts.pem.domain.txn.common.TPemCmBldngAditnlItms;
import com.gnts.pem.domain.txn.common.TPemCmBldngCostofcnstructn;
import com.gnts.pem.domain.txn.common.TPemCmBldngExtraItems;
import com.gnts.pem.domain.txn.common.TPemCmBldngMiscData;
import com.gnts.pem.domain.txn.common.TPemCmBldngNewPlinthArea;
import com.gnts.pem.domain.txn.common.TPemCmBldngNewSpec;
import com.gnts.pem.domain.txn.common.TPemCmBldngOldPlinthArea;
import com.gnts.pem.domain.txn.common.TPemCmBldngOldSpec;
import com.gnts.pem.domain.txn.common.TPemCmBldngRiskDtls;
import com.gnts.pem.domain.txn.common.TPemCmBldngRoofHght;
import com.gnts.pem.domain.txn.common.TPemCmBldngService;
import com.gnts.pem.domain.txn.common.TPemCmBldngStgofcnstructn;
import com.gnts.pem.domain.txn.common.TPemCmBldngTechDetails;
import com.gnts.pem.domain.txn.common.TPemCmBldngValutnSummry;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.domain.txn.common.TPemCmLandValutnData;
import com.gnts.pem.domain.txn.common.TPemCmOwnerDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropAdjoinDtls;
import com.gnts.pem.domain.txn.common.TPemCmPropApplcntEstmate;
import com.gnts.pem.domain.txn.common.TPemCmPropDescription;
import com.gnts.pem.domain.txn.common.TPemCmPropDimension;
import com.gnts.pem.domain.txn.common.TPemCmPropDocDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropGuidlnRefdata;
import com.gnts.pem.domain.txn.common.TPemCmPropGuidlnValue;
import com.gnts.pem.domain.txn.common.TPemCmPropLegalDocs;
import com.gnts.pem.domain.txn.common.TPemCmPropNewPlanApprvl;
import com.gnts.pem.domain.txn.common.TPemCmPropOldPlanApprvl;
import com.gnts.pem.domain.txn.common.TPemCmPropRsnbleEstmate;
import com.gnts.pem.domain.txn.common.TPemCmPropValtnSummry;
import com.gnts.pem.domain.txn.sbi.TPemSbiBldngCnstructnDtls;
import com.gnts.pem.domain.txn.sbi.TPemSbiBldngElctrcInstltn;
import com.gnts.pem.domain.txn.sbi.TPemSbiBldngPlumpInstltn;
import com.gnts.pem.domain.txn.sbi.TPemSbiPropChartrstic;
import com.gnts.pem.domain.txn.synd.TPemSynPropViolation;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.txn.common.CmAssetDetailsService;
import com.gnts.pem.service.txn.common.CmBldngAditnlItmsService;
import com.gnts.pem.service.txn.common.CmBldngCostofcnstructnService;
import com.gnts.pem.service.txn.common.CmBldngExtraItemsService;
import com.gnts.pem.service.txn.common.CmBldngMiscDataService;
import com.gnts.pem.service.txn.common.CmBldngNewPlinthAreaService;
import com.gnts.pem.service.txn.common.CmBldngNewSpecService;
import com.gnts.pem.service.txn.common.CmBldngOldPlinthAreaService;
import com.gnts.pem.service.txn.common.CmBldngOldSpecService;
import com.gnts.pem.service.txn.common.CmBldngRiskDtlsService;
import com.gnts.pem.service.txn.common.CmBldngRoofHghtService;
import com.gnts.pem.service.txn.common.CmBldngServiceDtlsService;
import com.gnts.pem.service.txn.common.CmBldngStgofcnstructnService;
import com.gnts.pem.service.txn.common.CmBldngTechDetailsService;
import com.gnts.pem.service.txn.common.CmBldngValutnSummryService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;
import com.gnts.pem.service.txn.common.CmLandValutnDataService;
import com.gnts.pem.service.txn.common.CmOwnerDetailsService;
import com.gnts.pem.service.txn.common.CmPropAdjoinDtlsService;
import com.gnts.pem.service.txn.common.CmPropApplcntEstmateService;
import com.gnts.pem.service.txn.common.CmPropDescriptionService;
import com.gnts.pem.service.txn.common.CmPropDimensionService;
import com.gnts.pem.service.txn.common.CmPropDocDetailsService;
import com.gnts.pem.service.txn.common.CmPropGuidlnRefdataService;
import com.gnts.pem.service.txn.common.CmPropGuidlnValueService;
import com.gnts.pem.service.txn.common.CmPropLegalDocsService;
import com.gnts.pem.service.txn.common.CmPropNewPlanApprvlService;
import com.gnts.pem.service.txn.common.CmPropOldPlanApprvlService;
import com.gnts.pem.service.txn.common.CmPropRsnbleEstmateService;
import com.gnts.pem.service.txn.common.CmPropValtnSummryService;
import com.gnts.pem.service.txn.sbi.SbiBldngCnstructnDtlsService;
import com.gnts.pem.service.txn.sbi.SbiBldngElctrcInstltnService;
import com.gnts.pem.service.txn.sbi.SbiBldngPlumpInstltnService;
import com.gnts.pem.service.txn.sbi.SbiPropChartrsticService;
import com.gnts.pem.service.txn.synd.SynPropViolationService;
import com.gnts.pem.util.iterator.ComponenetIterValuationDetails;
import com.gnts.pem.util.iterator.ComponentIerServices;
import com.gnts.pem.util.iterator.ComponentIterAdditionalItems;
import com.gnts.pem.util.iterator.ComponentIterBuildingSpecfication;
import com.gnts.pem.util.iterator.ComponentIterDimensionofPlot;
import com.gnts.pem.util.iterator.ComponentIterExtraItems;
import com.gnts.pem.util.iterator.ComponentIterGuideline;
import com.gnts.pem.util.iterator.ComponentIterMiscellaneous;
import com.gnts.pem.util.iterator.ComponentIterOwnerDetails;
import com.gnts.pem.util.iterator.ComponentIterPlinthArea;
import com.gnts.pem.util.iterator.ComponentIterRoofHeight;
import com.gnts.pem.util.iterator.ComponentIteratorAdjoinProperty;
import com.gnts.pem.util.iterator.ComponentIteratorLegalDoc;
import com.gnts.pem.util.iterator.ComponentIteratorNormlDoc;
import com.gnts.pem.util.list.AdjoinPropertyList;
import com.gnts.pem.util.list.BuildSpecList;
import com.gnts.pem.util.list.DimensionList;
import com.gnts.pem.util.list.ValuationDetailsList;
import com.gnts.pem.util.UIFlowData;
import com.gnts.pem.util.XMLUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeEvent;
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

public class SBIConstructionBuilding implements ClickListener  {
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
private CmPropOldPlanApprvlService beanOldPlanApprvl=(CmPropOldPlanApprvlService) SpringContextHelper
.getBean("oldPlanApprvl");
private CmBldngTechDetailsService beanTechDtls=(CmBldngTechDetailsService) SpringContextHelper
.getBean("bldgTechDtls");
private CmBldngRoofHghtService beanRoofHt=(CmBldngRoofHghtService) SpringContextHelper
.getBean("bldngRoofHt");
private CmBldngOldSpecService beanOldSpecBuilding = (CmBldngOldSpecService) SpringContextHelper
		.getBean("oldSpec");
private CmBldngOldPlinthAreaService beanOldPlinthArea=(CmBldngOldPlinthAreaService) SpringContextHelper
.getBean("oldPlinth");
private CmBldngValutnSummryService beanValuationDtls=(CmBldngValutnSummryService) SpringContextHelper
.getBean("bldngvaltnSummary");
private SbiBldngElctrcInstltnService beanElectInstl=(SbiBldngElctrcInstltnService) SpringContextHelper
.getBean("sbiBldgElectrical");
private SbiBldngPlumpInstltnService beanPlumInstl=(SbiBldngPlumpInstltnService) SpringContextHelper
.getBean("sbiBldgPlumbing");
private CmBldngExtraItemsService beanExtra=(CmBldngExtraItemsService) SpringContextHelper
.getBean("extraItms");
private CmBldngAditnlItmsService beanAddtional=(CmBldngAditnlItmsService) SpringContextHelper
.getBean("addtionalItms");
private CmBldngMiscDataService beanMiscell=(CmBldngMiscDataService) SpringContextHelper
.getBean("miscellItms");
private CmBldngServiceDtlsService beanService=(CmBldngServiceDtlsService) SpringContextHelper
.getBean("bldingServiceDtls");
private CmPropNewPlanApprvlService beanNewPlanApprvl=(CmPropNewPlanApprvlService) SpringContextHelper
.getBean("newPlanApprvl");
private SbiBldngCnstructnDtlsService beanConstnDtls=(SbiBldngCnstructnDtlsService) SpringContextHelper
.getBean("sbiBldgCnstn");
private CmPropApplcntEstmateService beanEstimate=(CmPropApplcntEstmateService) SpringContextHelper
.getBean("applnEstimate");
private CmPropRsnbleEstmateService beanReasonable=(CmPropRsnbleEstmateService) SpringContextHelper
.getBean("rsnbleEstimate");
private CmBldngRiskDtlsService beanRiskDtls=(CmBldngRiskDtlsService) SpringContextHelper
.getBean("bldingRiskDtls");
private CmBldngStgofcnstructnService beanconstruction = (CmBldngStgofcnstructnService) SpringContextHelper
.getBean("stageCnstn");
private CmBldngNewSpecService beanNewSpecBuilding = (CmBldngNewSpecService) SpringContextHelper
.getBean("newSpec");
private CmBldngNewPlinthAreaService beanNewPlinthArea=(CmBldngNewPlinthAreaService) SpringContextHelper
.getBean("newPlinth");
	private CmPropGuidlnValueService beanguidelinevalue = (CmPropGuidlnValueService) SpringContextHelper
			.getBean("guidelineValue");
	private CmPropGuidlnRefdataService beanguidelinereference = (CmPropGuidlnRefdataService) SpringContextHelper
			.getBean("guidelineRef");
	private CmPropValtnSummryService beanPropertyvalue = (CmPropValtnSummryService) SpringContextHelper
			.getBean("propValtnSummary");
	private CmBldngCostofcnstructnService beanCostConstruction = (CmBldngCostofcnstructnService) SpringContextHelper
			.getBean("costOfConst");
	private SynPropViolationService beanviolation = (SynPropViolationService) SpringContextHelper
			.getBean("synPropViolation");
	private CmBankService beanbank = (CmBankService) SpringContextHelper
			.getBean("bank");
	
	private Table tblEvalDetails = new Table();
	private BeanItemContainer<TPemCmEvalDetails> beans = null;

	private Accordion accordion = new Accordion();
	private VerticalLayout mainPanel = new VerticalLayout();
	private VerticalLayout searchPanel = new VerticalLayout();
	private VerticalLayout tablePanel = new VerticalLayout();
	private Long headerid;
	private String strEvaluationNo = "SBI_CONSBUILD_",strXslFile="SbiConstBuilding.xsl", evalNumber, customer, propertyType;
	// pagination
			private int total = 0;
	// for table panel
			int count=0;
			private FileDownloader filedownloader;
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
	private TextField tfSearchEvalNumber=new TextField("Evaluation Number");
	private PopupDateField dfSearchEvalDate=new PopupDateField("Evaluation Date"); 
	private ComboBox tfSearchBankbranch=new ComboBox("Bank Branch");
	private TextField tfSearchCustomer=new TextField("Customer Name");
	private Button btnSearch=new Button("Search",this);
	private Button btnReset=new Button("Reset",this);

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
	private Button btnSubmit = new Button("Submit",this);
	private Button btnCancel = new Button("Cancel", this);
	//private Button saveExcel = new Button("Report");
	private Label lblHeading=new Label();

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
	private ComboBox tfBankBranch = new ComboBox("Bank Branch");
	private TextField tfDynamicEvaluation1 = new TextField();
	private TextField tfDynamicEvaluation2 = new TextField();
	private Button btnDynamicEvaluation1 = new Button("", this);
	

	// for customer details
	private VerticalLayout layoutAssetOwner = new VerticalLayout();
	private VerticalLayout layoutCustomerDetail = new VerticalLayout();
	private GridLayout layoutCustomerDetail1 = new GridLayout();
	private TextField tfCustomerName = new TextField("Customer Name");
	private TextArea tfCustomerAddr = new TextArea("Customer Address");
	private ComboBox slPropertyDesc = new ComboBox(
			"Description of the Property");
	private TextField tfDynamicCustomer1 = new TextField();
	private TextField tfDynamicCustomer2 = new TextField();
	private Button btnDynamicCustomer = new Button("", this);

	// for asset details
	private VerticalLayout layoutAssetDetails = new VerticalLayout();
	private GridLayout layoutAssetDetails1 = new GridLayout();

	//Owner Details
		private VerticalLayout layoutOwnerDetails=new VerticalLayout();
		private GridLayout layoutOwnerDetails1=new GridLayout();
		private Button btnAddOwner=new Button("",this);
		
	
	private TextField tfOwnerName = new TextField("Owner Name");
	private TextArea tfOwnerAddress = new TextArea("Owner Address");
	private TextArea tfPropertyAddress = new TextArea("Property Address");
	private TextField tfLandMark = new TextField("Land Mark");
	private CheckBox chkSameAddress = new CheckBox("Same Address?");
	private TextField tfDynamicAsset1 = new TextField();
	private TextField tfDynamicAsset2 = new TextField();
	private Button btnDynamicAsset = new Button("", this);

	// for Document Details
	private VerticalLayout layoutNormalLegal=new VerticalLayout();
	private VerticalLayout panelNormalDocumentDetails = new VerticalLayout();
	private VerticalLayout panelLegalDocumentDetails = new VerticalLayout();
	private Button btnAddNorDoc = new Button("", this);
	private Button btnAddLegalDoc = new Button("", this);

	// for adjoin properties
	private VerticalLayout panelAdjoinProperties = new VerticalLayout();
	private Button btnAddAdjoinProperty = new Button("", this);
	
	// for dimension of plot
	private VerticalLayout panelDimension = new VerticalLayout();
	private Button btnAddDimension = new Button("", this);
	//for description of the property
		private VerticalLayout layoutDescProperty=new VerticalLayout();
		private GridLayout layoutDescProperty1=new GridLayout();
		private TextArea tfPostalAddress=new TextArea("Postal Address of the property");
		private TextField tfSiteNumber=new TextField("Flat No.");
		private TextField tfSFNumber=new TextField("S.F.No.");
		private TextField tfNewSFNumber=new TextField("New S.F.No.");
		private TextField tfVillageLoc=new TextField("Village");
		private TextField tfTaluk=new TextField("Taluk");
		private TextField tfDistCorpPanc=new TextField("District/Municipality");
		private TextField tfLocationSketch=new TextField("Location sketch of the property");
		private TextField tfProTaxReceipt=new TextField("Property Tax Receipt Ref");
		private TextField tfElecServiceConnNo=new TextField("Electricity Connection No.");
		private TextField tfElecConnecName=new TextField("Electricity Connection Name");
		private ComboBox slHighMiddPoor=new ComboBox("High/Middle/Poor");
		private ComboBox slUrbanSemiRural=new ComboBox("Urban/Semi Urban/Rural");
		private ComboBox slResiIndustCommer=new ComboBox("Property Type");
		private ComboBox slProOccupiedBy=new ComboBox("Property Occupied by");
		private TextField tfMonthlyRent=new TextField("What is the monthly rent");
		private TextField tfCoverUnderStatCentral=new TextField("Covered under any State/Central Govt.");
		private TextField tfAnyConversionLand=new TextField("Any Land Conversion");
		private TextField tfExtentSite=new TextField("Extent of the Site");
		private TextField tfYearAcquistion=new TextField("Year of acquisition/purchase");
		private TextField tfPurchaseValue=new TextField("Value of purchase price");
		private TextField tfPropLandmark=new TextField("Land Mark to the property");
		private TextField tfDynamicDescProp1 = new TextField();
		private TextField tfDynamicDescProp2 = new TextField();
		private Button btnDynamicDescProp = new Button("", this);
		//for charcteristiccs of the site
		private VerticalLayout layoutCharcterSite=new VerticalLayout();
		private GridLayout layoutCharcterSite1=new GridLayout();
		private ComboBox slLocalClass=new ComboBox("Classification of the Locality");
		private ComboBox slSurroundDevelop=new ComboBox("Development of Surrounding areas");
		private TextField tfFlood=new TextField("Possibility of Frequent Flooding");
		private ComboBox slFeasibility=new ComboBox("Feasibility to the Civic amenities");
		private ComboBox slLandLevel=new ComboBox("Level of Land");
		private ComboBox slLandShape=new ComboBox("Shape of Land");
		private ComboBox slTypeUse=new ComboBox("Type of Use");
		private TextField tfUsageRestriction=new TextField("Any Usage Restriction");
		private ComboBox slIsPlot=new ComboBox("Town Planning approved Layout");
		private TextField tfApprveNo=new TextField("If Yes Approval No.");
		private TextField tfNoReason=new TextField("If No Reasons there of ");
		private TextField tfSubdivide=new TextField("Is Subdivided");
		private ComboBox slDrawApproval=new ComboBox("Drawing Approval");
		private ComboBox slCornerInter=new ComboBox("Corner plot or Intermittent plot");
		private ComboBox slRoadFacility=new ComboBox("Road Facilities");
		private ComboBox slTypeRoad=new ComboBox("Type of Road");
		private ComboBox slRoadWidth=new ComboBox("Width of Road");
		private ComboBox slLandLock=new ComboBox("Is Land Locked Land");
		private ComboBox slWaterPotential=new ComboBox("Water Potentiality");
		private ComboBox slUnderSewerage=new ComboBox("Underground Water sewerage system");
		private ComboBox slPowerSupply=new ComboBox("Power Supply is available in the site");
		private TextField tfAdvantageSite=new TextField("Advantage of the site");
		private TextField tfDisadvantageSite=new TextField("Disadvantage of the site");
		private TextField tfGeneralRemarks=new TextField("General Remarks if any");
		private TextField tfDynamicCharacter1 = new TextField();
		private TextField tfDynamicCharacter2 = new TextField();
		private Button btnDynamicCharacter= new Button("", this);

		// construction
		private VerticalLayout layoutConstruction = new VerticalLayout();
		private GridLayout layoutConstruction1 = new GridLayout();
		private TextField tfStageofConst = new TextField("Stage of Construction");
		private TextField tfSalablity=new TextField("Salability");
		private TextField tfRentalValues=new TextField("Likely rental values in future");
		private TextField tfIncome=new TextField("Any Likely income it may generate");
		private TextField tfDynamicConstruction1=new TextField();
		private TextField tfDynamicConstruction2=new TextField();
		private Button btnDynamicConstruction=new Button("",this);
	
	// violation
	private VerticalLayout layoutViolation = new VerticalLayout();
	private GridLayout layoutViolation1 = new GridLayout();
	private TextField tfAnyViolation = new TextField(
			"Violation if any observed");
	private TextField tfDynamicViolation1=new TextField();
	private TextField tfDynamicViolation2=new TextField();
	private Button btnDynamicViolation=new Button("",this);
	
	// valuation of land
	private VerticalLayout layoutValuationLand = new VerticalLayout();
	private GridLayout layoutValuationLand1 = new GridLayout();
	private TextField tfAreaofLand = new TextField("Size of the plot");
	private TextField tfNorthandSouth = new TextField("North and South");
	private TextField tfMarketRate = new TextField("Prevailing market rate");
	private TextField tfGuiderate=new TextField("Guideline rate");
	private TextField tfAdopetdMarketRate = new TextField(
			"Assessed/Adopted rate of valaution");
	private TextField tfFairMarketRate = new TextField(
			"Estimated value of land");
	private TextField tfDynamicValuation1=new TextField();
	private TextField tfDynamicValuation2=new TextField();
	private Button btnDynamicValuation=new Button("",this);
	
	//Electrical Installation
			private VerticalLayout layoutElectrical=new VerticalLayout();
			private GridLayout layoutElectrical1=new GridLayout();
			private TextField tfTypeofwiring=new TextField("Type of wiring");
			private TextField tfClassFit=new TextField("Class of fittings");
			private TextField tfNOofLight=new TextField("Number of Light Points");
			private TextField tfExhaustFan=new TextField("Exhaust Fan");
			private TextField tfFan=new TextField("Fan");
			private TextField tfSpareplug=new TextField("Spare plug points");
			private TextField tfDynamicElectrical1=new TextField();
			private TextField tfDynamicElectrical2=new TextField();
			
			private Button btnDynamicElectrical=new Button("",this);
			
			//Plumbing Installation
				private VerticalLayout layoutPlumbing=new VerticalLayout();
				private GridLayout layoutPlumbing1=new GridLayout();
				private TextField tfNoofClosets=new TextField("No.of water closets and their type");
				private TextField tfNoofWashbin=new TextField("No of wash basins");
				private TextField tfWatertaps=new TextField("Water meters. taps etc.");
				private TextField tfAnyFixtures=new TextField("Any other fixtures");
				private TextField tfDynamicPlum1=new TextField();
				private TextField tfDynamicPlum2=new TextField();
				private Button btnDynamicPlumbing=new Button("",this);	
			
	
	//valuation of under construction
	private VerticalLayout layoutConstValuation=new VerticalLayout();
	private FormLayout layoutConstValuation1=new FormLayout();
	private ComboBox slTypeProperty=new ComboBox("Type of Property");
	private ComboBox slTypeStructure=new ComboBox("Type of Structure");
	private TextField tfYearConstruction=new TextField("Year Of Construction");
	private TextField tfNoFloors=new TextField("No of Floors");
	private TextField tfConstQuality=new TextField("Quality of Construction");
	private ComboBox slAllapproval=new ComboBox("All approvals required are received");
	private ComboBox slIsConstruction=new ComboBox("Is construction as per approved plan");
	private TextField tfReason=new TextField("Reason");
	private TextField tfDynamicConstval1=new TextField();
	private TextField tfDynamicConstval2=new TextField();
	private Button btnDynamicConstVal=new Button("",this);
	
	//plinth area
	private VerticalLayout layoutPlintharea=new VerticalLayout();
	private Button btnAddPlinth=new Button("", this);
	//plinth area construction
	private VerticalLayout vlPlinthAreaNew=new VerticalLayout();
	private Button btnAddPlinthNew=new Button("",this);
	//BuildSpecfication
	private VerticalLayout panelBuildSpecfication=new VerticalLayout();
	private Button btnAddBuildSpec=new Button("", this);
	
	//BuildSpecfication
		private VerticalLayout panelBuildSpecficationNew=new VerticalLayout();
		private Button btnAddBuildSpecNew=new Button("", this);
	
	//valuation details
		private VerticalLayout layoutValuationDetails=new VerticalLayout();
		private VerticalLayout layoutValuationDetails2=new VerticalLayout();
		private GridLayout layoutValuationDetails1=new GridLayout();
		private Button btnAddValDtls=new Button("", this);
		private Label lblParticular=new Label("Particulars of Items");
		private Label lblPlinthArea=new Label("Plinth Area");
		private Label lblRoofHt=new Label("Roof Ht");
		private Label lblBuildAge=new Label("Age of Building");
		private Label lblRate=new Label("Estimated Replacement rate of construction");
		private Label lblReplace=new Label("Replacement Cost");
		private Label lblDepreciation=new Label("Depreciation");
		private Label lblNetvalue=new Label("Net value after depreciated");	
		
		//Extra Items
		private VerticalLayout layoutExtraAddItems1=new VerticalLayout();
		private GridLayout layoutExtraAddItems =new GridLayout();
		
		private VerticalLayout layoutExtraItems=new VerticalLayout();
		private Button btnDynamicExtra=new Button("",this);
		//Additional Items
		private VerticalLayout layoutAdditionItem=new VerticalLayout();
		private Button btnDynamicAdditional=new Button("",this);
		
		//Miscellaneous
		private VerticalLayout layoutMiscellService1=new VerticalLayout();
		private GridLayout layoutMiscellService =new GridLayout();
		
		private VerticalLayout layoutMiscellaneous=new VerticalLayout();
		private Button btnDynamicMiscell=new Button("",this);
		
		//Services
		private VerticalLayout layoutServices =new VerticalLayout();
		private Button btnDynamicServices=new Button("",this);
		// Applicant Estimate
		private VerticalLayout layoutApplicantEstimate = new VerticalLayout();
		private GridLayout layoutApplicantEstimate1 = new GridLayout();
		private GridLayout layoutApplicantEstimate2 = new GridLayout();
		private GridLayout layoutApplicantEstimate3 = new GridLayout();
		private GridLayout layoutApplicantEstimate4 = new GridLayout();
		private Label lblAppEstimate = new Label("Applicant Estimate");
		private TextField tfAppEstimate = new TextField();
		private Label lblDtlsAppEstimate = new Label(
				"Details of Applicant Estimate");
		private TextField tfDtlsAppEstimate = new TextField();
		private TextField tfDetails1 = new TextField();
		private TextField tfDetailVal1 = new TextField();
		private TextField tfDetails2 = new TextField();
		private TextField tfDetailVal2 = new TextField();
		private TextField tfDetails3 = new TextField();
		private TextField tfDetailVal3 = new TextField();
		private Label lblTotal = new Label("Total");
		private TextField tfTotalval = new TextField();
		private TextField tfDynamicAppEstimate1 = new TextField();
		private TextField tfDynamicAppEstimate2 = new TextField();
		private TextField tfDynamicAppEstimate3 = new TextField();
		private TextField tfDynamicAppEstimate4 = new TextField();
		private TextField tfDynamicAppEstimate5 = new TextField();
		private TextField tfDynamicAppEstimate6 = new TextField();
		private TextField tfDynamicAppEstimate7 = new TextField();
		private TextField tfDynamicAppEstimate8 = new TextField();
		

		private Button btnDynamicAppEstimate = new Button("", this);
	
	//Applicant Reasonable
		private VerticalLayout layoutApplicantReasonable=new VerticalLayout();
		private GridLayout layoutApplicantReasonable1=new GridLayout();
		private GridLayout layoutApplicantReasonable2=new GridLayout();
		private Label lblAppReasonable=new Label("Is Applicant Reasonable");
		private ComboBox slAppReasonable=new ComboBox();
		private Label lblReasonEstimate=new Label("Reasonable Estimate");
		private TextField tfReasonEstimateVal=new TextField();
		private Label lblDtlsAppReasonable=new Label("Details of Reasonable Estimate");
		private TextField tfDtlsAppReasonable=new TextField();
		private TextField tfDetailsReason1=new TextField();
		private TextField tfDetailReasonVal1=new TextField();
		private TextField tfDetailsReason2=new TextField();
		private TextField tfDetailReasonVal2=new TextField();
		private TextField tfDetailsReason3=new TextField();
		private TextField tfDetailReasonVal3=new TextField();
		private Label lblReasonTotal=new Label("Total");
		private TextField tfReasonTotalval=new TextField();
		private TextField tfDynamicAppReason1=new TextField();
		private TextField tfDynamicAppReason2=new TextField();
		private TextField tfDynamicAppReason3=new TextField();
		private TextField tfDynamicAppReason4=new TextField();
		
		
		private Button btnDynamicAppReason=new Button("",this);
		
	//EarthQuake
		
		private VerticalLayout layoutEarthquake = new VerticalLayout();
		private GridLayout layoutEarthquake1 = new GridLayout();
		private ComboBox slEarthQuake = new ComboBox("Is building designed for earth quake");
		private TextField tfDynamicEarthquake1=new TextField();
		private TextField tfDynamicEarthquake2=new TextField();
		private Button btnDynamicEarthQuake=new Button("",this);
		
		// cost of construction
		private VerticalLayout layoutCostConstruction = new VerticalLayout();
		private GridLayout layoutCost = new GridLayout();
		private GridLayout layoutCostConstruction1 = new GridLayout();
		private GridLayout layoutCostConstruction2 = new GridLayout();
		private Label lblCostConst = new Label("Cost of Construction");
		private TextField tfCostConstruction = new TextField();
		private TextField tfDynamicCostConst1 = new TextField();
		private TextField tfDynamicCostConst2 = new TextField();
		private TextField tfDynamicCostConst3 = new TextField();
		private TextField tfDynamicCostConst4 = new TextField();
		private TextField tfDynamicCostConst5 = new TextField();
		private TextField tfDynamicCostConst6 = new TextField();
		private TextField tfDynamicCostConst7 = new TextField();
		private TextField tfDynamicCostConst8 = new TextField();
		private TextField tfDynamicCostConst9 = new TextField();
		private TextField tfDynamicCostConst10 = new TextField();

		private Button btnDynamicCostConst = new Button("", this);
		//for property values
		private VerticalLayout layoutPropertyValue=new VerticalLayout();
		private GridLayout layoutPropertyValue1=new GridLayout();
		private TextField tfRealizabletRate = new TextField(
				"Realizable value of the Land");
		private TextField tfDistressRate = new TextField(
				"Distress value of the Land");
		private TextField tfGuidelineRate = new TextField(
				"Guideline value of the Land");
		// details of plan approval
					private VerticalLayout layoutPlanApproval = new VerticalLayout();
					private GridLayout layoutPlanApproval1 = new GridLayout();
					private VerticalLayout formLand=new VerticalLayout();
					private VerticalLayout formBuilding=new VerticalLayout();
					private ComboBox slLandandBuilding = new ComboBox();
					private TextField tfLandandBuilding=new TextField();
					private ComboBox slBuilding=new ComboBox();
					private TextField tfBuilding=new TextField();
					private TextField tfPlanApprovedBy = new TextField("Approved by");
					private TextField dfLicenseFrom = new TextField("License period");
					private ComboBox slIsLicenceForced = new ComboBox(
							"Is the license is in force");
					private ComboBox slAllApprovalRecved = new ComboBox(
							"Are all approvals required are received");
					private ComboBox slConstAsperAppPlan = new ComboBox(
							"Is the construction as per approved plan");
					private TextField tfDynamicPlanApproval1 = new TextField();
					private TextField tfDynamicPlanApproval2 = new TextField();
					private Button btnDynamicPlanApproval = new Button("", this);
		// details of plan approval for construction
					private VerticalLayout vlConstPlanApp= new VerticalLayout();
					private HorizontalLayout hlConstPlanApp1 = new HorizontalLayout();
					private HorizontalLayout hlConstPlanApp2 = new HorizontalLayout();
					private VerticalLayout vlLand=new VerticalLayout();
					private VerticalLayout vlBuilding=new VerticalLayout();
					private ComboBox slLandBuildConst = new ComboBox();
					private TextField tfLandBuildConst=new TextField();
					private ComboBox slBuildConst=new ComboBox();
					private TextField tfBuildConst=new TextField();
					private TextField tfPlanApproveConst = new TextField("Approved by");
					private ComboBox slAllApprovalRecvedConst = new ComboBox("Are all approvals required are received");
					private ComboBox slConstAsperAppPlanConst = new ComboBox("Is the construction as per approved plan");
					private TextField tfDynamicConstPlanApp1 = new TextField();
					private TextField tfDynamicConstPlanApp2 = new TextField();
					private Button btnDynamicConstPlan= new Button("", this);
		//plinth area
		private VerticalLayout layoutGuideline=new VerticalLayout();
		private Button btnAddGuideline=new Button("", this);

		//valuation of under construction
		private VerticalLayout layoutBuildtValuation=new VerticalLayout();
		private GridLayout layoutBuildtValuation1=new GridLayout();
		private FormLayout formConstValuation1=new FormLayout();
		private FormLayout formConstValuation2=new FormLayout();
		private VerticalLayout layoutRoofHeight=new VerticalLayout();
		private ComboBox slTypeBuilding=new ComboBox("Type of Building");
		private ComboBox slBuildStructure=new ComboBox("Type of Construction");
		private TextField tfYearConstructionBuild=new TextField("Year Of Construction");
		private TextField tfBuildNoFloors=new TextField("Number of floors & height");
		private TextField tfDynamicBuildval1=new TextField();
		private TextField tfDynamicBuildval2=new TextField();
		private Button btnDynamicBuildVal=new Button("",this);
		private Button btnAddRoofHt=new Button("",this);
		
		//Details of Building second off
		private ComboBox slExterior=new ComboBox("Exterior Condition");
		private ComboBox slInterior=new ComboBox("Interior Condition");
		private TextField tfLifeAge=new TextField("Life/Age of the building");
		private TextField tfFutureLife=new TextField("Future Life of builing");
		private TextField tfDetailsPlan=new TextField("Details of plan approval");
		private ComboBox slDeviation=new ComboBox("Whether there are any deviations");
		private TextField tfDtlsDeviation=new TextField("Details of deviation");
			

		
	// for guideline reference details
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

	// commondata
	private Window mywindow = new Window("Enter Caption");
	private Button myButton = new Button("Ok", this);
	private TextField tfCaption = new TextField();
	private String strSelectedPanel;

	private String strComponentWidth = "200px";
	private String strWidth="100px";
	private String strLblWidth="75px";
	private String strEstimateWidth="400px";
	private Long selectedBankid;
	private Long selectCompanyid,currencyId;
	private String selectedFormName;
	private String loginusername;

	// for report
	UIFlowData uiflowdata = new UIFlowData();
	private Logger logger = Logger.getLogger(SBIConstructionBuilding.class);
	public SBIConstructionBuilding() {
		
		loginusername = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		VerticalLayout clArgumentLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		selectedFormName=screenName;
		selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		if(UI.getCurrent().getSession().getAttribute("currenyId")!=null)
		{
		currencyId=Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("currenyId").toString());
		}
		
		//currencyId=Long.valueOf(UI.getCurrent().getSession().getAttribute("currenyId").toString());
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
		slStreetSerNo.addItem("STREET NAME");
		slStreetSerNo.addItem("SURVEY NO");
		slStreetSerNo.setNullSelectionAllowed(false);
		
		tfEvaluationNumber.setRequired(true);
		tfBankBranch.setRequired(true);
		dfDateofValuation.setRequired(true);
		tfEvaluationPurpose.setRequired(true);
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
		// for customer details
		tfDynamicCustomer1.setVisible(false);
		tfDynamicCustomer2.setVisible(false);
		layoutCustomerDetail1.setColumns(4);
		//layoutCustomerDetail.setCaption("Customer Details");
		layoutCustomerDetail1.addComponent(tfCustomerName);
		layoutCustomerDetail1.addComponent(slPropertyDesc);
		layoutCustomerDetail1.addComponent(tfDynamicCustomer1);
		layoutCustomerDetail1.addComponent(tfDynamicCustomer2);
		layoutCustomerDetail1.setSpacing(true);
		layoutCustomerDetail1.setMargin(true);

		layoutCustomerDetail.addComponent(btnDynamicCustomer);
		layoutCustomerDetail.setComponentAlignment(btnDynamicCustomer,
				Alignment.TOP_RIGHT);
		layoutCustomerDetail.addComponent(layoutCustomerDetail1);


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
				layoutAssetOwner.addComponent(PanelGenerator.createPanel(layoutOwnerDetails));
				lblHeading = new Label("Asset Details");
				layoutAssetOwner.addComponent(lblHeading);
				lblHeading.setStyleName("h4");
				layoutAssetOwner.addComponent(PanelGenerator.createPanel(layoutAssetDetails));
		
				//for Owner Details
				layoutOwnerDetails.addComponent(btnAddOwner);
				layoutOwnerDetails.setComponentAlignment(btnAddOwner, Alignment.TOP_RIGHT);
				layoutOwnerDetails1.setColumns(4);
				layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("",""));
				layoutOwnerDetails.addComponent(layoutOwnerDetails1);
				layoutOwnerDetails1.setSpacing(true);
				layoutOwnerDetails1.setMargin(true);
				
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
				layoutNormalLegal.addComponent(PanelGenerator.createPanel(panelNormalDocumentDetails));
				lblHeading = new Label("Legal Documents");
				layoutNormalLegal.addComponent(lblHeading);
				lblHeading.setStyleName("h4");
				layoutNormalLegal.addComponent(PanelGenerator.createPanel(panelLegalDocumentDetails));
				layoutNormalLegal.setMargin(true);

		// for adjoin properties
		//panelAdjoinProperties.setCaption("Adjoining Properties");
		panelAdjoinProperties.addComponent(btnAddAdjoinProperty);
		panelAdjoinProperties.setComponentAlignment(btnAddAdjoinProperty,
				Alignment.BOTTOM_RIGHT);
		panelAdjoinProperties.addComponent(new ComponentIteratorAdjoinProperty(
				null,true,true,true));

		// for dimensions
		//panelDimension.setCaption("Dimensions");
		panelDimension.addComponent(btnAddDimension);
		panelDimension.setComponentAlignment(btnAddDimension,
				Alignment.BOTTOM_RIGHT);
		panelDimension.addComponent(new ComponentIterDimensionofPlot(null,true,true,true));

		panelNormalDocumentDetails.setWidth("100%");
		panelLegalDocumentDetails.setWidth("100%");
		panelAdjoinProperties.setStyleName("width:100%;display:block;");

		
		//for description of the property
		layoutDescProperty.setMargin(true);
		layoutDescProperty1.setSpacing(true);
		layoutDescProperty1.setColumns(4);
		layoutDescProperty.addComponent(btnDynamicDescProp);
		layoutDescProperty.setComponentAlignment(btnDynamicDescProp, Alignment.TOP_RIGHT);
		layoutDescProperty1.addComponent(tfSiteNumber);
		layoutDescProperty1.addComponent(tfSFNumber);
		layoutDescProperty1.addComponent(tfNewSFNumber);
		layoutDescProperty1.addComponent(tfVillageLoc);
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
		//for charcteristiccs of the site
		layoutCharcterSite1.setSpacing(true);
		layoutCharcterSite1.setColumns(4);
		layoutCharcterSite.setMargin(true);
		layoutCharcterSite.addComponent(btnDynamicCharacter);
		layoutCharcterSite.setComponentAlignment(btnDynamicCharacter, Alignment.TOP_RIGHT);
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
		
		// construction
		//layoutConstruction.setCaption("Construction");
		//layoutConstruction.setCaption("Construction");
		layoutConstruction1.setSpacing(true);
		layoutConstruction1.setColumns(4);
		layoutConstruction1.addComponent(tfStageofConst);
		layoutConstruction1.addComponent(tfSalablity);
		layoutConstruction1.addComponent(tfRentalValues);
		layoutConstruction1.addComponent(tfIncome);
		layoutConstruction1.addComponent(tfDynamicConstruction1);
		layoutConstruction1.addComponent(tfDynamicConstruction2);
		tfDynamicConstruction1.setVisible(false);
		tfDynamicConstruction2.setVisible(false);
		
		layoutConstruction.addComponent(btnDynamicConstruction);
		layoutConstruction.setComponentAlignment(btnDynamicConstruction, Alignment.TOP_RIGHT);
		layoutConstruction.addComponent(layoutConstruction1);
		layoutConstruction.setMargin(true);
		
		
		//for violation
		//layoutViolation.setCaption("Violation Details");
		layoutViolation1.setSpacing(true);
		layoutViolation1.setColumns(4);
		layoutViolation1.addComponent(tfAnyViolation);
		layoutViolation1.addComponent(tfDynamicViolation1);
		layoutViolation1.addComponent(tfDynamicViolation2);
		tfDynamicViolation1.setVisible(false);
		tfDynamicViolation2.setVisible(false);
		
		layoutViolation.addComponent(btnDynamicViolation);
		layoutViolation.setComponentAlignment(btnDynamicViolation, Alignment.TOP_RIGHT);
		layoutViolation.addComponent(layoutViolation1);
		layoutViolation.setMargin(true);
		
		
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
		layoutValuationLand1.addComponent(tfRealizabletRate);
		layoutValuationLand1.addComponent(tfDistressRate);
		layoutValuationLand1.addComponent(tfGuidelineRate);
		layoutValuationLand1.addComponent(tfDynamicValuation1);
		layoutValuationLand1.addComponent(tfDynamicValuation2);
		tfDynamicValuation1.setVisible(false);
		tfDynamicValuation2.setVisible(false);
		tfAdopetdMarketRate.setRequired(true);
		layoutValuationLand.addComponent(btnDynamicValuation);
		layoutValuationLand.setComponentAlignment(btnDynamicValuation, Alignment.TOP_RIGHT);
		layoutValuationLand.addComponent(layoutValuationLand1);
		layoutValuationLand.setMargin(true);
		//
		
	//	tfEvaluationNumber.addValidator(new IntegerValidator("Enter numbers only"));
		//		tfEvaluationNumber.addBlurListener(new SaarcValidate(tfEvaluationNumber));
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
		tfLandMark.setImmediate(true);
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
		
		//for applicant estimate
		layoutElectrical.setSpacing(true);
		layoutElectrical1.setSpacing(true);
		layoutElectrical1.setColumns(4);
		layoutElectrical1.addComponent(tfTypeofwiring);
		layoutElectrical1.addComponent(tfClassFit);
		layoutElectrical1.addComponent(tfNOofLight);
		layoutElectrical1.addComponent(tfExhaustFan);
		layoutElectrical1.addComponent(tfSpareplug);
		layoutElectrical1.addComponent(tfDynamicElectrical1);
		layoutElectrical1.addComponent(tfDynamicElectrical2);
		tfDynamicElectrical1.setVisible(false);
		tfDynamicElectrical2.setVisible(false);
		layoutElectrical.addComponent(btnDynamicElectrical);
		layoutElectrical.setComponentAlignment(btnDynamicElectrical, Alignment.TOP_RIGHT);
		layoutElectrical.addComponent(layoutElectrical1);
		layoutElectrical.setMargin(true);
		
		//for applicant reasonable
		layoutPlumbing.setSpacing(true);
		layoutPlumbing1.setSpacing(true);
		layoutPlumbing1.setColumns(4);
		layoutPlumbing1.addComponent(tfNoofClosets);
		layoutPlumbing1.addComponent(tfNoofWashbin);
		layoutPlumbing1.addComponent(tfWatertaps);
		layoutPlumbing1.addComponent(tfAnyFixtures);
		layoutPlumbing1.addComponent(tfDynamicPlum1);
		layoutPlumbing1.addComponent(tfDynamicPlum2);
		
		layoutPlumbing.addComponent(btnDynamicPlumbing);
		layoutPlumbing.setComponentAlignment(btnDynamicPlumbing, Alignment.TOP_RIGHT);
		layoutPlumbing.addComponent(layoutPlumbing1);
		layoutPlumbing.setMargin(true);
		
		
		//property value
		layoutPropertyValue.setSpacing(true);
		layoutPropertyValue.setMargin(true);
		layoutPropertyValue1.setColumns(4);
		layoutPropertyValue1.setSpacing(true);
		layoutPropertyValue1.addComponent(tfRealizabletRate);
		layoutPropertyValue1.addComponent(tfDistressRate);
		layoutPropertyValue1.addComponent(tfGuidelineRate);
		layoutPropertyValue.addComponent(layoutPropertyValue1);
		
		
		// for details of plan approval
		layoutPlanApproval1.setColumns(4);
		formLand.addComponent(slLandandBuilding);
		formLand.addComponent(tfLandandBuilding);
		formBuilding.addComponent(slBuilding);
		formBuilding.addComponent(tfBuilding);
		
		layoutPlanApproval1.addComponent(formLand);
		layoutPlanApproval1.addComponent(formBuilding);
		layoutPlanApproval1.addComponent(tfPlanApprovedBy);
		layoutPlanApproval1.addComponent(dfLicenseFrom);
		layoutPlanApproval1.addComponent(slIsLicenceForced);
		layoutPlanApproval1.addComponent(slAllApprovalRecved);
		layoutPlanApproval1.addComponent(slConstAsperAppPlan);
		layoutPlanApproval1.addComponent(tfDynamicPlanApproval1);
		layoutPlanApproval1.addComponent(tfDynamicPlanApproval2);

		tfDynamicPlanApproval1.setVisible(false);
		tfDynamicPlanApproval2.setVisible(false);
		layoutPlanApproval1.setSpacing(true);
		layoutPlanApproval1.setMargin(true);
		layoutPlanApproval.addComponent(btnDynamicPlanApproval);
		layoutPlanApproval.setComponentAlignment(btnDynamicPlanApproval,
				Alignment.TOP_RIGHT);
		layoutPlanApproval.addComponent(layoutPlanApproval1);
		
		//details of the plan approval construction
		vlLand.addComponent(slLandBuildConst);
		vlLand.addComponent(tfLandBuildConst);
		vlBuilding.addComponent(slBuildConst);
		vlBuilding.addComponent(tfBuildConst);
		
		hlConstPlanApp1.addComponent(vlLand);
		hlConstPlanApp1.addComponent(vlBuilding);
		hlConstPlanApp1.addComponent(tfPlanApproveConst);
		hlConstPlanApp1.addComponent(slAllApprovalRecvedConst);
		hlConstPlanApp2.addComponent(slConstAsperAppPlanConst);
		hlConstPlanApp2.addComponent(tfDynamicConstPlanApp1);
		hlConstPlanApp2.addComponent(tfDynamicConstPlanApp2);
		
		
		tfDynamicConstPlanApp1.setVisible(false);
		tfDynamicConstPlanApp2.setVisible(false);
		hlConstPlanApp1.setSpacing(true);
		hlConstPlanApp1.setMargin(true);
		hlConstPlanApp2.setSpacing(true);
		hlConstPlanApp2.setMargin(true);
		vlConstPlanApp.addComponent(btnDynamicConstPlan);
		vlConstPlanApp.setComponentAlignment(btnDynamicConstPlan,
			Alignment.TOP_RIGHT);
		vlConstPlanApp.addComponent(hlConstPlanApp1);
		vlConstPlanApp.addComponent(hlConstPlanApp2);
		//valuation of Construction
		layoutConstValuation.setSpacing(true);
		layoutConstValuation1.setSpacing(true);
		layoutConstValuation1.addComponent(slTypeProperty);
		layoutConstValuation1.addComponent(slTypeStructure);
		layoutConstValuation1.addComponent(tfYearConstruction);
		layoutConstValuation1.addComponent(tfNoFloors);
		layoutConstValuation1.addComponent(tfConstQuality);
		layoutConstValuation1.addComponent(slAllapproval);
		layoutConstValuation1.addComponent(slIsConstruction);
		layoutConstValuation1.addComponent(tfReason);
		layoutConstValuation1.addComponent(tfDynamicConstval1);
		layoutConstValuation1.addComponent(tfDynamicConstval2);
		tfDynamicConstval1.setVisible(false);
		tfDynamicConstval2.setVisible(false);
		
		layoutConstValuation.addComponent(btnDynamicConstVal);
		layoutConstValuation.setComponentAlignment(btnDynamicConstVal, Alignment.TOP_RIGHT);
		layoutConstValuation.addComponent(layoutConstValuation1);
		layoutConstValuation.setMargin(true);
		
		//layoutLandConstValuation.addComponent(PanelGenerator.createPanel(layoutValuationLand));
		//layoutLandConstValuation.addComponent(PanelGenerator.createPanel(layoutConstValuation));
		//layoutLandConstValuation1.addComponent(layoutLandConstValuation);
		//layoutLandConstValuation1.setMargin(true);
		// for plinth area
		layoutPlintharea.addComponent(btnAddPlinth);
		layoutPlintharea.setComponentAlignment(btnAddPlinth,
					Alignment.TOP_RIGHT);
		layoutPlintharea.setMargin(true);
		layoutPlintharea.addComponent(new ComponentIterPlinthArea("Ground Floor","",""));
		layoutPlintharea.addComponent(new ComponentIterPlinthArea("Portico and Stair","",""));
		// for plinth area
		vlPlinthAreaNew.addComponent(btnAddPlinthNew);
		vlPlinthAreaNew.setComponentAlignment(btnAddPlinthNew,
							Alignment.TOP_RIGHT);
		vlPlinthAreaNew.setMargin(true);
		vlPlinthAreaNew.addComponent(new ComponentIterPlinthArea("Ground Floor","",""));
		vlPlinthAreaNew.addComponent(new ComponentIterPlinthArea("Portico and Stair","",""));
		
		//for Valuation Details
		layoutValuationDetails.addComponent(btnAddValDtls);
		layoutValuationDetails1.setColumns(8);
		layoutValuationDetails1.setSpacing(true);
		layoutValuationDetails.setComponentAlignment(btnAddValDtls, Alignment.TOP_RIGHT);
		layoutValuationDetails1.addComponent(lblParticular);
		layoutValuationDetails1.addComponent(lblPlinthArea);
		layoutValuationDetails1.addComponent(lblRoofHt);
		layoutValuationDetails1.addComponent(lblBuildAge);
		layoutValuationDetails1.addComponent(lblRate);
		layoutValuationDetails1.addComponent(lblReplace);
		layoutValuationDetails1.addComponent(lblDepreciation);
		layoutValuationDetails1.addComponent(lblNetvalue);
		layoutValuationDetails1.setComponentAlignment(lblParticular, Alignment.BOTTOM_LEFT);
		layoutValuationDetails1.setComponentAlignment(lblPlinthArea, Alignment.BOTTOM_LEFT);
		layoutValuationDetails1.setComponentAlignment(lblRoofHt, Alignment.BOTTOM_LEFT);
		layoutValuationDetails1.setComponentAlignment(lblBuildAge, Alignment.BOTTOM_LEFT);
		layoutValuationDetails1.setComponentAlignment(lblRate, Alignment.BOTTOM_LEFT);
		layoutValuationDetails1.setComponentAlignment(lblReplace, Alignment.BOTTOM_LEFT);
		layoutValuationDetails1.setComponentAlignment(lblDepreciation, Alignment.BOTTOM_LEFT);
		layoutValuationDetails1.setComponentAlignment(lblNetvalue, Alignment.BOTTOM_LEFT);
		layoutValuationDetails1.setWidth("650px");
		layoutValuationDetails.addComponent(layoutValuationDetails1);
		layoutValuationDetails.addComponent(layoutValuationDetails2);
		layoutValuationDetails.setWidth("100%");
		layoutValuationDetails.setMargin(true);
		ValuationDetailsList obj=new ValuationDetailsList();
		ValuationDetailsList obj1=new ValuationDetailsList();
		obj.setFloorDtlsLabel("Ground Floor Building");
		obj1.setFloorDtlsLabel("First Floor Building");
		layoutValuationDetails2.addComponent(new ComponenetIterValuationDetails(obj));
		layoutValuationDetails2.addComponent(new ComponenetIterValuationDetails(obj1));
		
	//for Calculation-extra Items
		layoutExtraItems.setCaption("Extra Items");
		layoutExtraItems.setWidth("420px");
		layoutExtraItems.addComponent(btnDynamicExtra);
		layoutExtraItems.setComponentAlignment(btnDynamicExtra, Alignment.TOP_RIGHT);
		layoutExtraItems.addComponent(new ComponentIterExtraItems(null,""));
		
		layoutAdditionItem.setCaption("Additional Items");
		layoutAdditionItem.setWidth("400px");
		layoutAdditionItem.addComponent(btnDynamicAdditional);
		layoutAdditionItem.setComponentAlignment(btnDynamicAdditional, Alignment.TOP_RIGHT);
		layoutAdditionItem.addComponent(new ComponentIterAdditionalItems(null,""));
		
		layoutExtraAddItems1.setSpacing(true);
		layoutExtraAddItems1.setMargin(true);
		layoutExtraAddItems.setColumns(2);
		layoutExtraAddItems.addComponent(layoutExtraItems);
		layoutExtraAddItems.addComponent(layoutAdditionItem);
		layoutExtraAddItems1.addComponent(layoutExtraAddItems);
		
	//for Miscellaneous
		layoutMiscellaneous.setCaption("Miscellaneous");
		layoutMiscellaneous.setWidth("420px");
		layoutMiscellaneous.addComponent(btnDynamicMiscell);
		layoutMiscellaneous.setComponentAlignment(btnDynamicMiscell, Alignment.TOP_RIGHT);
		layoutMiscellaneous.addComponent(new ComponentIterMiscellaneous(null,""));
		
		layoutServices.setCaption("Services");
		layoutServices.setWidth("400px");
		layoutServices.addComponent(btnDynamicServices);
		layoutServices.setComponentAlignment(btnDynamicServices, Alignment.TOP_RIGHT);
		layoutServices.addComponent(new ComponentIerServices(null,""));
		
		layoutMiscellService1.setSpacing(true);
		layoutMiscellService1.setMargin(true);
		layoutMiscellService.setColumns(2);
		layoutMiscellService.addComponent(layoutMiscellaneous);
		layoutMiscellService.addComponent(layoutServices);		
		layoutMiscellService1.addComponent(layoutMiscellService);
		// for Build Specification
			panelBuildSpecfication.addComponent(btnAddBuildSpec);
			panelBuildSpecfication.setComponentAlignment(btnAddBuildSpec,
						Alignment.BOTTOM_RIGHT);
			panelBuildSpecfication.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));
			panelBuildSpecfication.setWidth("100%");
		// for Build Specification
			panelBuildSpecficationNew.addComponent(btnAddBuildSpecNew);
			panelBuildSpecficationNew.setComponentAlignment(btnAddBuildSpecNew,
						Alignment.BOTTOM_RIGHT);
			panelBuildSpecficationNew.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));
			panelBuildSpecficationNew.setWidth("100%");
			
			// for applicant estimate
			layoutApplicantEstimate.setSpacing(true);
			layoutApplicantEstimate1.setSpacing(true);
			layoutApplicantEstimate1.setColumns(2);
			layoutApplicantEstimate2.setSpacing(true);
			layoutApplicantEstimate2.setColumns(2);
			layoutApplicantEstimate3.setSpacing(true);
			layoutApplicantEstimate3.setColumns(2);
			layoutApplicantEstimate4.setSpacing(true);
			layoutApplicantEstimate4.setColumns(2);
			layoutApplicantEstimate1.addComponent(lblAppEstimate);
			layoutApplicantEstimate1.addComponent(tfAppEstimate);
			layoutApplicantEstimate1.addComponent(lblDtlsAppEstimate);
			layoutApplicantEstimate1.addComponent(tfDtlsAppEstimate);
			layoutApplicantEstimate1.addComponent(tfDetails1);
			layoutApplicantEstimate1.addComponent(tfDetailVal1);
			layoutApplicantEstimate1.addComponent(tfDetails2);
			layoutApplicantEstimate1.addComponent(tfDetailVal2);
			layoutApplicantEstimate1.addComponent(tfDetails3);
			layoutApplicantEstimate1.addComponent(tfDetailVal3);
			layoutApplicantEstimate2.addComponent(tfDynamicAppEstimate1);
			layoutApplicantEstimate2.addComponent(tfDynamicAppEstimate2);
			layoutApplicantEstimate2.addComponent(tfDynamicAppEstimate3);
			layoutApplicantEstimate2.addComponent(tfDynamicAppEstimate4);
			layoutApplicantEstimate3.addComponent(tfDynamicAppEstimate5);
			layoutApplicantEstimate3.addComponent(tfDynamicAppEstimate6);
			layoutApplicantEstimate3.addComponent(tfDynamicAppEstimate7);
			layoutApplicantEstimate3.addComponent(tfDynamicAppEstimate8);
			layoutApplicantEstimate4.addComponent(lblTotal);
			layoutApplicantEstimate4.addComponent(tfTotalval);
			
			layoutApplicantEstimate.addComponent(btnDynamicAppEstimate);
			layoutApplicantEstimate.setComponentAlignment(btnDynamicAppEstimate,
					Alignment.TOP_RIGHT);
			layoutApplicantEstimate.addComponent(layoutApplicantEstimate1);
			layoutApplicantEstimate.addComponent(layoutApplicantEstimate2);
			layoutApplicantEstimate.addComponent(layoutApplicantEstimate3);
			layoutApplicantEstimate.addComponent(layoutApplicantEstimate4);
			layoutApplicantEstimate2.setVisible(true);
			layoutApplicantEstimate3.setVisible(true);
			layoutApplicantEstimate.setMargin(true);
			tfDynamicAppEstimate2.setValue("0.00");
			tfDetailVal2.setValue("0.00");
			tfDetailVal1.setValue("0.00");
			tfDynamicAppEstimate4.setValue("0.00");
			tfDynamicAppEstimate6.setValue("0.00");
			tfDynamicAppEstimate8.setValue("0.00");
			tfDetailVal3.setValue("0.00");
			tfTotalval.setImmediate(true);
			tfTotalval.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;

				@Override
				public void blur(BlurEvent event) {
					// TODO Auto-generated method stub
					BigDecimal appEstimate = new BigDecimal(0.00);

					BigDecimal test = new BigDecimal("0.00");
					BigDecimal test1 = new BigDecimal("0.00");
					BigDecimal test2 = new BigDecimal("0.00");
					BigDecimal test3 = new BigDecimal("0.00");
					BigDecimal test4 = new BigDecimal("0.00");
					BigDecimal test5= new BigDecimal("0.00");
					BigDecimal test6 = new BigDecimal("0.00");

					try {
						test = new BigDecimal(tfDetailVal1.getValue());

					} catch (Exception e) {
						test = new BigDecimal("0.00");
					}
					try {
						test1 = new BigDecimal(tfDetailVal2.getValue());
					} catch (Exception e) {
						test1 = new BigDecimal("0.00");
					}
					try {
						test2 = new BigDecimal(tfDetailVal3.getValue());
					} catch (Exception e) {
						test2 = new BigDecimal("0.00");
					}
					try {
						test3 = new BigDecimal(tfDynamicAppEstimate2.getValue());

					} catch (Exception e) {
						test3 = new BigDecimal("0.00");
					}
					try {
						test4 = new BigDecimal(tfDynamicAppEstimate4.getValue());
					} catch (Exception e) {
						test4 = new BigDecimal("0.00");
					}
					try {
						test5 = new BigDecimal(tfDynamicAppEstimate6.getValue());
					} catch (Exception e) {
						test4 = new BigDecimal("0.00");
					}
					try {
						test6 = new BigDecimal(tfDynamicAppEstimate8.getValue());
					} catch (Exception e) {
						test4 = new BigDecimal("0.00");
					}
					appEstimate = appEstimate.add(test).add(test1).add(test2)
							.add(test3).add(test4).add(test5).add(test6);
					tfTotalval.setValue(appEstimate.toString());
					tfAppEstimate.setValue(appEstimate.toString());
				}
			});

		//for applicant reasonable
		layoutApplicantReasonable.setSpacing(true);
		layoutApplicantReasonable1.setSpacing(true);
		layoutApplicantReasonable2.setSpacing(true);
		layoutApplicantReasonable1.setColumns(2);
		layoutApplicantReasonable2.setColumns(2);
		layoutApplicantReasonable1.setVisible(false);
		layoutApplicantReasonable2.addComponent(lblAppReasonable);
		layoutApplicantReasonable2.addComponent(slAppReasonable);
		layoutApplicantReasonable1.addComponent(lblReasonEstimate);
		layoutApplicantReasonable1.addComponent(tfReasonEstimateVal);
		layoutApplicantReasonable1.addComponent(lblDtlsAppReasonable);
		layoutApplicantReasonable1.addComponent(tfDtlsAppReasonable);
		layoutApplicantReasonable1.addComponent(tfDetailsReason1);
		layoutApplicantReasonable1.addComponent(tfDetailReasonVal1);
		layoutApplicantReasonable1.addComponent(tfDetailsReason2);
		layoutApplicantReasonable1.addComponent(tfDetailReasonVal2);
		layoutApplicantReasonable1.addComponent(tfDetailsReason3);
		layoutApplicantReasonable1.addComponent(tfDetailReasonVal3);
		layoutApplicantReasonable1.addComponent(tfDynamicAppReason1);
		layoutApplicantReasonable1.addComponent(tfDynamicAppReason2);
		layoutApplicantReasonable1.addComponent(tfDynamicAppReason3);
		layoutApplicantReasonable1.addComponent(tfDynamicAppReason4);
		layoutApplicantReasonable1.addComponent(lblReasonTotal);
		layoutApplicantReasonable1.addComponent(tfReasonTotalval);
		tfDynamicAppReason1.setVisible(false);
		tfDynamicAppReason2.setVisible(false);
		tfDynamicAppReason3.setVisible(false);
		tfDynamicAppReason4.setVisible(false);
		
		layoutApplicantReasonable.addComponent(btnDynamicAppReason);
		layoutApplicantReasonable.setComponentAlignment(btnDynamicAppReason, Alignment.TOP_RIGHT);
		layoutApplicantReasonable.addComponent(layoutApplicantReasonable2);
		layoutApplicantReasonable.addComponent(layoutApplicantReasonable1);
		layoutApplicantReasonable.setMargin(true);
		
		tfDetailReasonVal1.setValue("0.00");
		tfDetailReasonVal2.setValue("0.00");
		tfDetailReasonVal3.setValue("0.00");
		
		tfReasonTotalval.setImmediate(true);
		tfReasonTotalval.addBlurListener(new BlurListener() {
		private static final long serialVersionUID = 1L;

			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stubBigDecimal appEstimate =new BigDecimal(0.00);
				BigDecimal appReason =new BigDecimal(0.00);
				BigDecimal test=new BigDecimal("0.00");
				BigDecimal test1=new BigDecimal("0.00");
				BigDecimal test2=new BigDecimal("0.00");
				BigDecimal test3=new BigDecimal("0.00");
				BigDecimal test4=new BigDecimal("0.00");
				
				try{
				test=new BigDecimal(tfDetailReasonVal1.getValue());
				
				}catch(Exception e){
					test=new BigDecimal("0.00");
					}
				try{
					test1=new BigDecimal(tfDetailReasonVal2.getValue());
				}catch(Exception e){
					test1=new BigDecimal("0.00");
				}
				try{
					test2=new BigDecimal(tfDetailReasonVal3.getValue());
				}catch(Exception e){test2=new BigDecimal("0.00");
				}
				try{
					test3=new BigDecimal(tfDynamicAppReason2.getValue());
					
				}catch(Exception e){test3=new BigDecimal("0.00");
				}
				try{
					test4=new BigDecimal(tfDynamicAppReason2.getValue());
				}catch(Exception e){
					test4=new BigDecimal("0.00");
				}
				appReason=appReason.add(test)
				.add(test1)
				.add(test2)
				.add(test3)
				.add(test4);
				tfReasonTotalval.setValue(appReason.toString());
				tfReasonEstimateVal.setValue(appReason.toString());
			}
		});
		
		slAppReasonable.setImmediate(true);
		slAppReasonable.setNullSelectionAllowed(false);
		slAppReasonable.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			public void valueChange(ValueChangeEvent event) {
				if(slAppReasonable.getValue()!=null){
					
				if(slAppReasonable.getValue().equals(Common.NO_DESC)){
					
					System.out.println("serial application"+slAppReasonable.getValue());
					layoutApplicantReasonable1.setVisible(true);
					btnDynamicAppReason.setVisible(true);

				
				}else{
					layoutApplicantReasonable1.setVisible(false);
					btnDynamicAppReason.setVisible(false);
				}

				}
			}
			});
		
		//Earth quake
		
		layoutEarthquake1.setSpacing(true);
		layoutEarthquake1.setColumns(4);
		layoutEarthquake1.addComponent(slEarthQuake);
		layoutEarthquake1.addComponent(tfDynamicEarthquake1);
		layoutEarthquake1.addComponent(tfDynamicEarthquake2);
		tfDynamicEarthquake1.setVisible(false);
		tfDynamicEarthquake2.setVisible(false);
		
		layoutEarthquake.addComponent(btnDynamicEarthQuake);
		layoutEarthquake.setComponentAlignment(btnDynamicEarthQuake, Alignment.TOP_RIGHT);
		layoutEarthquake.addComponent(layoutEarthquake1);
		layoutEarthquake.setMargin(true);
		// cost of construction
				layoutCost.setColumns(2);
				layoutCostConstruction1.setColumns(2);
				layoutCostConstruction2.setColumns(2);
				layoutCostConstruction.setSpacing(true);
				layoutCost.setSpacing(true);
				layoutCostConstruction2.setSpacing(true);
				layoutCostConstruction1.setSpacing(true);
				layoutCostConstruction1.setSpacing(true);
				layoutCost.addComponent(lblCostConst);
				layoutCost.addComponent(tfCostConstruction);

				layoutCostConstruction1.addComponent(tfDynamicCostConst1);
				layoutCostConstruction1.addComponent(tfDynamicCostConst2);
				layoutCostConstruction1.addComponent(tfDynamicCostConst3);
				layoutCostConstruction1.addComponent(tfDynamicCostConst4);
				layoutCostConstruction1.addComponent(tfDynamicCostConst5);
				layoutCostConstruction1.addComponent(tfDynamicCostConst6);

				layoutCostConstruction2.addComponent(tfDynamicCostConst7);
				layoutCostConstruction2.addComponent(tfDynamicCostConst8);
				layoutCostConstruction2.addComponent(tfDynamicCostConst9);
				layoutCostConstruction2.addComponent(tfDynamicCostConst10);
				layoutCostConstruction1.setVisible(false);
				layoutCostConstruction2.setVisible(false);

				layoutCostConstruction.addComponent(btnDynamicCostConst);
				layoutCostConstruction.setComponentAlignment(btnDynamicCostConst,
						Alignment.TOP_RIGHT);
				layoutCostConstruction.addComponent(layoutCost);
				layoutCostConstruction.addComponent(layoutCostConstruction1);
				layoutCostConstruction.addComponent(layoutCostConstruction2);
				layoutCostConstruction.setMargin(true);

		
		//valuation of Building
		formConstValuation1.addComponent(slTypeBuilding);
		formConstValuation1.addComponent(slBuildStructure);
		formConstValuation1.addComponent(tfYearConstructionBuild);
		formConstValuation1.addComponent(slExterior);
		formConstValuation1.addComponent(slInterior);
		formConstValuation1.addComponent(tfLifeAge);
		formConstValuation1.addComponent(tfFutureLife);
		formConstValuation1.addComponent(tfDetailsPlan);
		formConstValuation1.addComponent(slDeviation);
		formConstValuation1.addComponent(tfDtlsDeviation);
		
		formConstValuation2.addComponent(btnAddRoofHt);
		formConstValuation2.addComponent(tfBuildNoFloors);
		
		layoutBuildtValuation.setSpacing(true);
		layoutBuildtValuation1.setSpacing(true);
		layoutBuildtValuation1.setColumns(2);
		layoutBuildtValuation1.addComponent(formConstValuation1);
		layoutBuildtValuation1.addComponent(new VerticalLayout(){
		{
		addComponent(formConstValuation2);
		addComponent(layoutRoofHeight);	
		}	
				
		});
		
		formConstValuation1.addComponent(tfDynamicBuildval1);
		formConstValuation1.addComponent(tfDynamicBuildval2);
		layoutBuildtValuation.addComponent(btnDynamicBuildVal);
		layoutBuildtValuation.setComponentAlignment(btnDynamicBuildVal, Alignment.TOP_RIGHT);
		layoutBuildtValuation.addComponent(layoutBuildtValuation1);
		layoutBuildtValuation.setMargin(true);
		
		
		// for Guideline area
		layoutGuideline.addComponent(btnAddGuideline);
		layoutGuideline.setComponentAlignment(btnAddGuideline,
								Alignment.TOP_RIGHT);
		layoutGuideline.setMargin(true);
		layoutGuideline.addComponent(new ComponentIterGuideline("Land","","",""));
		layoutGuideline.addComponent(new ComponentIterGuideline("Building","","",""));
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
	
		//add components in main panel
		accordion.setWidth("100%");
		
		layoutEvaluationDetails.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutEvaluationDetails),"Evaluation Details");
		layoutOwnerDetails.setStyleName("bluebar");
		layoutAssetDetails.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutAssetOwner),"Owner Details/Asset Details");
		
		layoutNormalLegal.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutNormalLegal),"Document Details");
		
		panelAdjoinProperties.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(panelAdjoinProperties),"Adjoining Properties");
		
		panelDimension.setStyleName("bluebar");
		accordion.addTab(panelDimension,"Dimension");
		layoutDescProperty.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutDescProperty),"Description of the property");
		layoutCharcterSite.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutCharcterSite),"Characteristics of the site");
		layoutValuationLand.setStyleName("bluebar");
		accordion.addTab(layoutValuationLand,"Valuation of Land");
		//for Building
		vlConstPlanApp.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(vlConstPlanApp),"Details of Existing Plan Approval");
		layoutBuildtValuation.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutBuildtValuation),"Details of Building");
		layoutPlintharea.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutPlintharea),"Plinth Area Details");
		panelBuildSpecfication.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(panelBuildSpecfication),"Specification of the building(Existing)");
		layoutValuationDetails.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutValuationDetails),"Details of Valuation");
		layoutExtraAddItems1.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutExtraAddItems1),"Extra/Additional Items");
		layoutMiscellService1.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutMiscellService1),"Miscellaneous/Services");
		layoutElectrical.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutElectrical),"Electrical Installation");
		
		layoutPlumbing.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutPlumbing),"Plumbing Installation");
//for construction
		layoutPlanApproval.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutPlanApproval),"Details of Plan Approval");
		layoutConstValuation.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutConstValuation),"Details of Construction");
		layoutApplicantEstimate.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutApplicantEstimate),"Applicant Estimate");
		
		layoutApplicantReasonable.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutApplicantReasonable),"Applicant Reasonable");
		
		vlPlinthAreaNew.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(vlPlinthAreaNew),"Plinth Area Details for Construction");
		panelBuildSpecficationNew.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(panelBuildSpecficationNew),"Specification of the building");
		layoutMainForm.addComponent(PanelGenerator
				.createPanel(accordion));
		layoutConstruction.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutConstruction),"Construction Details");
		layoutEarthquake.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutEarthquake),"Earth Quake Details");
		
		layoutCostConstruction.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutCostConstruction),"Cost of Construction");
		
		layoutGuideline.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutGuideline),"Guideline Details");
		
		layoutGuidelineReference.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutGuidelineReference),"Guideline Reference Details");
		layoutPropertyValue.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutPropertyValue),"Property Value Details");
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
		layoutMainForm.setMargin(true);
		layoutMainForm.setSpacing(true);
		// for main panel
		layoutButton2.setSpacing(true);
		btnSave.setStyleName("savebt");
		btnCancel.setStyleName("cancelbt");
		btnSubmit.setStyleName("submitbt");
	
		btnSave.setVisible(false);
		btnSubmit.setVisible(false);
		btnCancel.setVisible(false);
	//	saveExcel.setVisible(false);
		layoutButton2.addComponent(btnSave);
		layoutButton2.addComponent(btnSubmit);
	//	layoutButton2.addComponent(saveExcel);
		layoutButton2.addComponent(btnCancel);
		hlHeaderLayout.addComponent(layoutButton2);
		hlHeaderLayout.setComponentAlignment(layoutButton2, Alignment.BOTTOM_RIGHT);
		
		
		/*String basepath = VaadinService.getCurrent()
		            .getBaseDirectory().getAbsolutePath();
		Resource res = new FileResource(new File(basepath +
		                "/WEB-INF/view/channel.doc"));
		FileDownloader fd = new FileDownloader(res);
		fd.extend(saveExcel);*/
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
		layoutSearch.addComponent(tfSearchBankbranch);
		layoutSearch.addComponent(tfSearchCustomer);
		//layoutSearch.addComponent(btnSearch);
		layoutSearch.addComponent(btnReset);
		//layoutSearch.setComponentAlignment(btnSearch, Alignment.BOTTOM_LEFT);
		layoutSearch.setComponentAlignment(btnReset, Alignment.BOTTOM_LEFT);
		//btnSearch.addStyleName("default");
*/		btnReset.addStyleName("resetbt");
		tfSearchCustomer.setImmediate(true);
		tfSearchCustomer.addListener(new TextChangeListener() {
		private static final long serialVersionUID = 1L;
			SimpleStringFilter filter=null;
			public void textChange(TextChangeEvent event) {
							Filterable f=(Filterable)
			tblEvalDetails.getContainerDataSource();
			if(filter!=null)
								f.removeContainerFilter(filter);
							
							
							filter=new SimpleStringFilter("custName", event.getText(), true, false);
									
							f.addContainerFilter(filter);
							total=f.size();
							tblEvalDetails.setColumnFooter("lastUpdateDt","No. of Records:"+total);
						}
					});
		tfSearchEvalNumber.setImmediate(true);
		tfSearchEvalNumber.addListener(new TextChangeListener() {
		private static final long serialVersionUID = 1L;
			SimpleStringFilter filter=null;
			public void textChange(TextChangeEvent event) {
							Filterable f=(Filterable)
			tblEvalDetails.getContainerDataSource();
			if(filter!=null)
								f.removeContainerFilter(filter);
							
							
							filter=new SimpleStringFilter("evalNo", event.getText(), true, false);
									
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
		
		tfCaption.setWidth("200px");
		mywindow.center();

		mywindow.setModal(true);
		tfCaption.focus();
		mywindow.setHeight("120");
		mywindow.setWidth("220");
		
	
		
		subwindow.addComponent(tfCaption);
		subwindow.addComponent(myButton);
		subwindow.setComponentAlignment(myButton, Alignment.MIDDLE_CENTER);
		
		subwindow.setMargin(true);
		subwindow.setSpacing(true);
		
		myButton.setStyleName("default");
		
		mywindow.setContent(subwindow);
		
    	mywindow.setResizable(false);
    	//mywindow.setResizable(false);
//		subwindow.setWidth("200");
//		subwindow.setHeight("400");

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
				String bankbranch=(String)tfSearchBankbranch.getValue();
				evalList = beanEvaluation.getSearchEvalDetailnList(null, evalno, null, customer,bankbranch,selectedBankid,selectCompanyid,null);
				total = evalList.size();
				if (total == 0) {
					lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
					lblSaveNotification.setValue("No Records found");
				} else {
					lblNotificationIcon.setIcon(null);
					lblSaveNotification.setValue("");
				}
			} else {
				
				evalList = beanEvaluation.getSearchEvalDetailnList(selectedFormName,null, null,null,null,selectedBankid,selectCompanyid,null);
				total = evalList.size();
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
/*
	void setTableProperties() {
		beans = new BeanItemContainer<TPemCmEvalDetails>(
				TPemCmEvalDetails.class);
		tblEvalDetails.addGeneratedColumn("docdate",
				new DateFormateColumnGenerator());
		tblEvalDetails.addGeneratedColumn("lastupdateddt",
				new DateColumnGenerator());
	}
*/
	private void updateEvaluationDetails(){
		System.out.println("Inside Update Evaluation");
		try{
			boolean valid = false;
			TPemCmEvalDetails evalobj = new TPemCmEvalDetails();
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="UNDERCONSTRUTIONBUILDING";
			
		    evalobj.setDocId(headerid);
		    evalobj.setDocStatus(Common.DOC_PENDING);
			evalobj.setDocDate(dfDateofValuation.getValue());
			evalobj.setEvalNo(tfEvaluationNumber.getValue());
			evalobj.setEvalDate(dfDateofValuation.getValue());
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
			try{
				BigDecimal totalAbstract=new BigDecimal(0.00);
				BigDecimal test=new BigDecimal("0.00");
				BigDecimal test1=new BigDecimal("0.00");
				BigDecimal test2=new BigDecimal("0.00");
				BigDecimal test3=new BigDecimal("0.00");
				BigDecimal test4=new BigDecimal("0.00");
				BigDecimal test5=new BigDecimal("0.00");
				try{
					test=new BigDecimal(uiflowdata.getTotalExtraItem().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
					
					}catch(Exception e){
						test=new BigDecimal("0.00");
						}
					try{
						test1=new BigDecimal(uiflowdata.getMarketValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
					}catch(Exception e){
						test1=new BigDecimal("0.00");
					}
					try{
						test2=new BigDecimal(uiflowdata.getTotalAdditional().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
					}catch(Exception e){test2=new BigDecimal("0.00");
					}
					try{
						test3=new BigDecimal(uiflowdata.getTotalMiscellaneous().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
						
					}catch(Exception e){test3=new BigDecimal("0.00");
					}
					try{
						test4=new BigDecimal(uiflowdata.getTotalServices().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
					}catch(Exception e){
						test4=new BigDecimal("0.00");
					}
					try{
						test5=new BigDecimal(uiflowdata.getTotalValuation().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
					}catch(Exception e){
						test5=new BigDecimal("0.00");
					}
					totalAbstract=totalAbstract.add(test).add(test1).add(test2).add(test3).add(test4).add(test5);
				
				uiflowdata.setTotalAbstractvalue(XMLUtil.IndianFormat(new BigDecimal(totalAbstract.toString())));  
				String numberOnly = totalAbstract.toString().replaceAll("[^\\d.]", "");
				if( numberOnly.trim().length()==0){
					numberOnly="0";	
				}
				uiflowdata.setAmountInWords(beanEvaluation.getAmountInWords(numberOnly));
				String numberOnly1 = tfGuidelineRate.getValue().replaceAll(
						"[^0-9]", "");
				uiflowdata.setAmountWordsGuideline(beanEvaluation.getAmountInWords(numberOnly1));
				/*List<CmCommonSetup> bill = BillGenerator.getEndValueDetails(numberOnly,headerid,loginusername,selectedBankid,selectCompanyid);
				//	bill.setBillNo(Long.toString(beanEvaluation.getNextSequnceId("seq_pem_evaldtls_docid")));
					uiflowdata.setBillDtls(bill);
					
					TPemCmBillDtls billDtls=BillGenerator.getBillDetails(numberOnly, headerid, loginusername, selectedBankid, selectCompanyid,currencyId);
					uiflowdata.setBill(billDtls);*/
				evalobj.setPropertyValue(Double.valueOf(numberOnly));
				BigDecimal realizable=new BigDecimal(0.00);
				BigDecimal distress=new BigDecimal(0.00);
				BigDecimal real=new BigDecimal("0.00");
				BigDecimal distre=new BigDecimal("0.00");
				
				try {
					real = new BigDecimal(totalAbstract.toString().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
				} catch (Exception e) {
					real = new BigDecimal("0.00");
					  
				}
				try {
					distre = new BigDecimal(totalAbstract.toString().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
				} catch (Exception e) {
					distre = new BigDecimal("0.00");
					  
				}
				try{
					realizable=real.multiply(new BigDecimal(95)).divide(new BigDecimal(100));
					realizable=realizable.subtract(realizable.remainder(new BigDecimal(1000)));
					tfRealizabletRate.setValue(realizable.toString());
					uiflowdata.setRealizablevalue(XMLUtil.IndianFormat(new BigDecimal(realizable.toString())));
					
					distress=(distre.multiply(new BigDecimal(85))).divide(new BigDecimal(100));
					distress=distress.subtract(distress.remainder(new BigDecimal(1000)));
					tfDistressRate.setValue(distress.toString());
					uiflowdata.setDistressvalue(XMLUtil.IndianFormat(new BigDecimal(distress.toString())));
					}catch(Exception e){
						
					}
				
			}catch(Exception e){
					
				}
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="UNDERCONSTRUTIONBUILDING";
			ByteArrayOutputStream recvstram = XMLUtil.doMarshall(uiflowdata);
			XMLUtil.getWordDocument(recvstram, evalNumber+"_"+customer+"_"+propertyType,
					strXslFile);
			String basepath = VaadinService.getCurrent()
			          .getBaseDirectory().getAbsolutePath();
			System.out.println("Inside Update Evaluation basepath"+basepath+"/WEB-INF/PEM-DOCS/"+evalNumber+"_"+customer+"_"+propertyType+".doc");
			File file = new File(basepath+"/WEB-INF/PEM-DOCS/"+evalNumber+"_"+customer+"_"+propertyType+".doc");
			FileInputStream fin = new FileInputStream(file);
		    byte fileContent[] = new byte[(int)file.length()];
		    fin.read(fileContent);
		    fin.close();
		    evalobj.setEvalDoc(fileContent);
		//	beanEvaluation.saveorUpdateEvalDetails(evalobj);
			uiflowdata.setEvalDtls(evalobj);
			if(tfEvaluationNumber.isValid() && tfBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid())
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
		boolean valid = false;
		// for save evaluation details
		try {
			TPemCmEvalDetails evalobj = new TPemCmEvalDetails();

			evalobj.setDocId(headerid);
			System.out.println("Headerid-->"+headerid);
			evalobj.setDocDate(dfDateofValuation.getValue());
			evalobj.setEvalNo(tfEvaluationNumber.getValue());
			evalobj.setEvalDate(dfDateofValuation.getValue());
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
		//	beanEvaluation.saveorUpdateEvalDetails(evalobj);
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
			try{
				saveDescriptionDetails();
			}catch(Exception e){}
			try{
				saveCharacterDetails();
			}catch(Exception e){
				
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
				saveConstValuationDetails();
			} catch (Exception e) {
			}
			try {
				saveOldPlinthAreaDetails();
			} catch (Exception e) {
			}
			try {
				saveNewPlinthAreaDetails();
			} catch (Exception e) {
			}
			try {
				saveOldBuildSpecDetails();
			} catch (Exception e) {
			}
			try {
				saveNewBuildSpecDetails();
			} catch (Exception e) {
				  
			}
			try {
				saveEstimateDetails();
			} catch (Exception e) {
			}
			try {
				saveReasonableDetails();
			} catch (Exception e) {
			}
			try {
				saveEarthQuakeDetails();
			} catch (Exception e) {
			}
			try {
				saveCostConstDetails();
			} catch (Exception e) {
			}
			try{
				saveElectricalDetails();
			}catch(Exception e){}
			try{
				savePlumbingDetails();
			}catch(Exception e){}
			try {
				saveGuidelineValue();
			} catch (Exception e) {
			}

			try {
				saveGuidelineReferenceDetails();
			} catch (Exception e) {
			}
			try {
				savePropertyValueDetails();
			} catch (Exception e) {
			}
			try{
				saveOldPlanApprovalDetails();
			}catch(Exception e){
				logger.info("Error-->"+e);
			}
			try{
				saveNewPlanApprovalDetails();
			}catch(Exception e){
				logger.info("Error-->"+e);
			}try{
				saveBuildingTechDetails();
			}catch(Exception e){
				logger.info("Error-->"+e);
			}try {
				saveRoofHeightDetails();
			} catch (Exception e) {
			}try {
				saveValuationDetails();
			} catch (Exception e) {
			}
			try {
				saveExtraItemsDetails();
			} catch (Exception e) {
			}
			try {
				saveAdditionalDetails();
			} catch (Exception e) {
			}
			try {
				saveMiscellaneousDetails();
			} catch (Exception e) {
			}
			try {
				saveServiceDetails();
			} catch (Exception e) {
			}
			try{
			uiflowdata.setPropertyAddress(tfPropertyAddress.getValue());
			uiflowdata.setBankBranch((String)tfBankBranch.getValue());
			uiflowdata.setEvalnumber(tfEvaluationNumber.getValue());
			if(dfDateofValuation.getValue()!=null)
			{
			SimpleDateFormat dt1 = new SimpleDateFormat("dd-MMM-yyyy");
			uiflowdata.setInspectionDate(dt1.format(dfDateofValuation.getValue()));
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
			try {
				saveValuationofLandDetails();
			} catch (Exception e) {
			}
			uiflowdata.setPropDesc((String)slPropertyDesc.getValue());
			uiflowdata.setCustomername(tfCustomerName.getValue());
			uiflowdata.setMarketValue(XMLUtil.IndianFormat(new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""))));
			uiflowdata.setGuidelinevalue(XMLUtil.IndianFormat(new BigDecimal(tfGuidelineRate.getValue())));
			uiflowdata.setConstructionValue(tfCostConstruction.getValue());
			}catch(Exception e){
				logger.info("Error-->" + e);
			}
			try{
				BigDecimal totalAbstract=new BigDecimal(0.00);
				BigDecimal test=new BigDecimal("0.00");
				BigDecimal test1=new BigDecimal("0.00");
				BigDecimal test2=new BigDecimal("0.00");
				BigDecimal test3=new BigDecimal("0.00");
				BigDecimal test4=new BigDecimal("0.00");
				BigDecimal test5=new BigDecimal("0.00");
				try{
					test=new BigDecimal(uiflowdata.getTotalExtraItem().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
					
					}catch(Exception e){
						test=new BigDecimal("0.00");
						}
					try{
						test1=new BigDecimal(uiflowdata.getMarketValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
					}catch(Exception e){
						test1=new BigDecimal("0.00");
					}
					try{
						test2=new BigDecimal(uiflowdata.getTotalAdditional().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
					}catch(Exception e){test2=new BigDecimal("0.00");
					}
					try{
						test3=new BigDecimal(uiflowdata.getTotalMiscellaneous().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
						
					}catch(Exception e){test3=new BigDecimal("0.00");
					}
					try{
						test4=new BigDecimal(uiflowdata.getTotalServices().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
					}catch(Exception e){
						test4=new BigDecimal("0.00");
					}
					try{
						test5=new BigDecimal(uiflowdata.getTotalValuation().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
					}catch(Exception e){
						test5=new BigDecimal("0.00");
					}
					totalAbstract=totalAbstract.add(test).add(test1).add(test2).add(test3).add(test4).add(test5);
				
				uiflowdata.setTotalAbstractvalue(XMLUtil.IndianFormat(new BigDecimal(totalAbstract.toString())));  
				String numberOnly = totalAbstract.toString().replaceAll("[^\\d.]", "");
				uiflowdata.setAmountInWords(beanEvaluation.getAmountInWords(numberOnly));
				String numberOnly1 = tfGuidelineRate.getValue().replaceAll(
						"[^0-9]", "");
				uiflowdata.setAmountWordsGuideline(beanEvaluation.getAmountInWords(numberOnly1));
				//Bill
				BigDecimal realizable=new BigDecimal(0.00);
				BigDecimal distress=new BigDecimal(0.00);
				BigDecimal real=new BigDecimal("0.00");
				BigDecimal distre=new BigDecimal("0.00");
				
				try {
					real = new BigDecimal(totalAbstract.toString().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
				} catch (Exception e) {
					real = new BigDecimal("0.00");
					  
				}
				try {
					distre = new BigDecimal(totalAbstract.toString().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
				} catch (Exception e) {
					distre = new BigDecimal("0.00");
					  
				}
				try{
					realizable=real.multiply(new BigDecimal(95)).divide(new BigDecimal(100));
					realizable=realizable.subtract(realizable.remainder(new BigDecimal(1000)));
					tfRealizabletRate.setValue(realizable.toString());
					uiflowdata.setRealizablevalue(XMLUtil.IndianFormat(new BigDecimal(realizable.toString())));
					
					distress=(distre.multiply(new BigDecimal(85))).divide(new BigDecimal(100));
					distress=distress.subtract(distress.remainder(new BigDecimal(1000)));
					tfDistressRate.setValue(distress.toString());
					uiflowdata.setDistressvalue(XMLUtil.IndianFormat(new BigDecimal(distress.toString())));
					}catch(Exception e){
						
					}
				
			}catch(Exception e){
					
				}
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="UNDERCONSTRUTIONBUILDING";
			ByteArrayOutputStream recvstram = XMLUtil.doMarshall(uiflowdata);
			XMLUtil.getWordDocument(recvstram, evalNumber+"_"+customer+"_"+propertyType,
					strXslFile);
			if(tfEvaluationNumber.isValid() && tfBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid())
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
		}catch(Exception e){
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png"));
			lblSaveNotification
					.setValue("Save failed, please check the data and try again ");
			logger.info("Error on SaveApproveReject Status function--->" + e);
		}

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
		obj.setFieldLabel("Will there be any problem to get drawing approval at a latter date");
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
			uiflowdata.getPropertyConstruct5().add(obj);
		}
	}

	void saveOldPlinthAreaDetails() {

		try {

			try {
				beanOldPlinthArea.deleteExistingOldPlinthArea(headerid);
			} catch (Exception e) {
				logger.info("Error-->"+e);
			}

			btnSave.setComponentError(null);
			Iterator<Component> myComps = layoutPlintharea
					.getComponentIterator();
			int i = 1;
			while (myComps.hasNext()) {
				final Component component = myComps.next();

				if (component instanceof ComponentIterPlinthArea) {

					ComponentIterPlinthArea mycomponent = (ComponentIterPlinthArea) component;
					TPemCmBldngOldPlinthArea obj = new TPemCmBldngOldPlinthArea();
					obj.setDocId(headerid);
					obj.setOrderNo(Long.valueOf(i));
					obj.setFieldLabel(mycomponent.getGroundFloor());
					obj.setAsPerPlan(mycomponent.getAsPerPlan());
					obj.setAsPerSite(mycomponent.getAsatSite());
					obj.setLastUpdatedBy(loginusername);
					obj.setLastUpdatedDt(new Date());

					if (mycomponent.getGroundFloor() != null) {
						beanOldPlinthArea.saveorUpdateOldPlinthArea(obj);
						uiflowdata.getPlinthArea().add(obj);
						i++;
					}

				}

			}
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

	}

	void saveOldBuildSpecDetails() {

		try {

			try {
				beanOldSpecBuilding.deleteExistingOldBuildSpec(headerid);
			} catch (Exception e) {
				logger.info("Error-->"+e);
			}

			btnSave.setComponentError(null);
			Iterator<Component> myComps = panelBuildSpecfication.getComponentIterator();
			while (myComps.hasNext()) {
				final Component component = myComps.next();

				if (component instanceof ComponentIterBuildingSpecfication) {

					ComponentIterBuildingSpecfication mycomponent = (ComponentIterBuildingSpecfication) component;

					List<TPemCmBldngOldSpec> getList = mycomponent
							.getBuildingSpecificationList();
						for (TPemCmBldngOldSpec oldobj : getList) {
							TPemCmBldngOldSpec obj = new TPemCmBldngOldSpec();
						obj.setDocId(headerid);
						obj.setGroupHdr(oldobj.getGroupHdr());
						obj.setGroupHdrSite(oldobj.getGroupHdrSite());
						obj.setGroupHdrPlan(oldobj.getGroupHdrPlan());
						obj.setFieldLabel(oldobj.getFieldLabel());
						obj.setAsPerDeed(oldobj.getAsPerDeed());
						obj.setAsPerPlan(oldobj.getAsPerPlan());
						obj.setAsPerSite(oldobj.getAsPerSite());
						obj.setDeedValue(oldobj.getDeedValue());
						obj.setSiteValue(oldobj.getSiteValue());
						obj.setPlanValue(oldobj.getPlanValue());
						obj.setLastUpdatedBy(loginusername);
						obj.setLastUpdatedDt(new Date());
						beanOldSpecBuilding.saveorUpdateOldBldgSpec(obj);
						uiflowdata.getBuildSpec().add(obj);
					}
				}

			}
		} catch (Exception e) {
			logger.info("Error-->"+e);
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
		
		obj = new TPemCmBldngStgofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfSalablity.getCaption());
		obj.setFieldValue((String) tfSalablity.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
			beanconstruction.saveBldngStgofcnstructn(obj);
			uiflowdata.getStgofConstn1().add(obj);
			
		obj = new TPemCmBldngStgofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfRentalValues.getCaption());
		obj.setFieldValue((String) tfRentalValues.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
			beanconstruction.saveBldngStgofcnstructn(obj);
			uiflowdata.getStgofConstn1().add(obj);
		
		obj = new TPemCmBldngStgofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfIncome.getCaption());
		obj.setFieldValue((String) tfIncome.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
			beanconstruction.saveBldngStgofcnstructn(obj);
		uiflowdata.getStgofConstn1().add(obj);
		if(tfDynamicConstruction1.getValue()!=null&&tfDynamicConstruction1.getValue().trim().length()>0)
		{
		obj = new TPemCmBldngStgofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicConstruction1.getCaption());
		obj.setFieldValue((String) tfDynamicConstruction1.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
			beanconstruction.saveBldngStgofcnstructn(obj);
			uiflowdata.getStgofConstn().add(obj);
		}
		
		if(tfDynamicConstruction2.getValue()!=null&&tfDynamicConstruction2.getValue().trim().length()>0)
		{
		obj = new TPemCmBldngStgofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicConstruction2.getCaption());
		obj.setFieldValue((String) tfDynamicConstruction2.getValue());
		obj.setOrderNo(6L);
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

	void saveConstValuationDetails(){
		
		try {
			beanConstnDtls.deleteExistingBldngCnstructnDtls(headerid);
		} catch (Exception e) {

		}

		TPemSbiBldngCnstructnDtls obj = new TPemSbiBldngCnstructnDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(slTypeProperty.getCaption());
		obj.setFieldValue((String)slTypeProperty.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstnDtls.saveorUpdateBldngCnstructnDtls(obj);
		uiflowdata.getDetailsBuilding().add(obj);

		obj = new TPemSbiBldngCnstructnDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(slTypeStructure.getCaption());
		obj.setFieldValue((String) slTypeStructure.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstnDtls.saveorUpdateBldngCnstructnDtls(obj);
		uiflowdata.getDetailsBuilding().add(obj);

		obj = new TPemSbiBldngCnstructnDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfYearConstruction.getCaption());
		obj.setFieldValue(tfYearConstruction.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstnDtls.saveorUpdateBldngCnstructnDtls(obj);
		uiflowdata.getDetailsBuilding().add(obj);

		obj = new TPemSbiBldngCnstructnDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfNoFloors.getCaption());
		obj.setFieldValue(tfNoFloors.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstnDtls.saveorUpdateBldngCnstructnDtls(obj);
		uiflowdata.getDetailsBuilding().add(obj);

		obj = new TPemSbiBldngCnstructnDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfConstQuality.getCaption());
		obj.setFieldValue((String) tfConstQuality.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstnDtls.saveorUpdateBldngCnstructnDtls(obj);
		uiflowdata.getDetailsBuilding1().add(obj);
		
		obj = new TPemSbiBldngCnstructnDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel("Are all approvals required are received");
		obj.setFieldValue((String) slAllapproval.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstnDtls.saveorUpdateBldngCnstructnDtls(obj);
		uiflowdata.getDetailsBuilding1().add(obj);
		
		obj = new TPemSbiBldngCnstructnDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(slIsConstruction.getCaption());
		obj.setFieldValue((String) slIsConstruction.getValue());
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstnDtls.saveorUpdateBldngCnstructnDtls(obj);
		uiflowdata.getDetailsBuilding1().add(obj);
		
		obj = new TPemSbiBldngCnstructnDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfReason.getCaption());
		obj.setFieldValue((String) tfReason.getValue());
		obj.setOrderNo(8L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstnDtls.saveorUpdateBldngCnstructnDtls(obj);
		uiflowdata.getDetailsBuilding1().add(obj);
		
		if(tfDynamicConstval1.getValue()!=null&&tfDynamicConstval1.getValue().trim().length()>0)
		{
		obj = new TPemSbiBldngCnstructnDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicConstval1.getCaption());
		obj.setFieldValue((String) tfDynamicConstval1.getValue());
		obj.setOrderNo(9L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstnDtls.saveorUpdateBldngCnstructnDtls(obj);
		uiflowdata.getDetailsBuilding1().add(obj);
		}
		if(tfDynamicConstval2.getValue()!=null&&tfDynamicConstval2.getValue().trim().length()>0)
		{
		obj = new TPemSbiBldngCnstructnDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicConstval2.getCaption());
		obj.setFieldValue((String) tfDynamicConstval2.getValue());
		obj.setOrderNo(10L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstnDtls.saveorUpdateBldngCnstructnDtls(obj);
		uiflowdata.getDetailsBuilding1().add(obj);
		}
	
	}
	
	
	void saveNewBuildSpecDetails() {

		try {

			try {
				beanNewSpecBuilding.deleteExistingNewBuildSpec(headerid);
			} catch (Exception e) {
				logger.info("Error-->"+e);
			}

			btnSave.setComponentError(null);
			Iterator<Component> myComps = panelBuildSpecficationNew.getComponentIterator();
			while (myComps.hasNext()) {
				final Component component = myComps.next();

				if (component instanceof ComponentIterBuildingSpecfication) {

					ComponentIterBuildingSpecfication mycomponent = (ComponentIterBuildingSpecfication) component;

					List<TPemCmBldngNewSpec> getList = mycomponent.getTPemCmBldngNewSpecList();
						for (TPemCmBldngNewSpec oldobj : getList) {
							TPemCmBldngNewSpec obj = new TPemCmBldngNewSpec();
						obj.setDocId(headerid);
						obj.setGroupHdr(oldobj.getGroupHdr());
						obj.setGroupHdrSite(oldobj.getGroupHdrSite());
						obj.setGroupHdrPlan(oldobj.getGroupHdrPlan());
						obj.setFieldLabel(oldobj.getFieldLabel());
						obj.setAsPerDeed(oldobj.getAsPerDeed());
						obj.setAsPerPlan(oldobj.getAsPerPlan());
						obj.setAsPerSite(oldobj.getAsPerSite());
						obj.setDeedValue(oldobj.getDeedValue());
						obj.setSiteValue(oldobj.getSiteValue());
						obj.setPlanValue(oldobj.getPlanValue());
						obj.setLastUpdatedBy(loginusername);
						obj.setLastUpdatedDt(new Date());
						beanNewSpecBuilding.saveorUpdateNewBldgSpec(obj);
						uiflowdata.getBuildSpec2().add(obj);
					}
				}

			}
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

	}
	void saveNewPlinthAreaDetails() {

		try {

			try {
				beanNewPlinthArea.deleteExistingNewPlinthArea(headerid);
			} catch (Exception e) {
				logger.info("Error-->"+e);
			}

			btnSave.setComponentError(null);
			Iterator<Component> myComps = vlPlinthAreaNew
					.getComponentIterator();
			int i = 1;
			while (myComps.hasNext()) {
				final Component component = myComps.next();

				if (component instanceof ComponentIterPlinthArea) {

					ComponentIterPlinthArea mycomponent = (ComponentIterPlinthArea) component;
					TPemCmBldngNewPlinthArea obj = new TPemCmBldngNewPlinthArea();
					obj.setDocId(headerid);
					obj.setOrderNo(Long.valueOf(i));
					obj.setFieldLabel(mycomponent.getGroundFloor());
					obj.setAsPerPlan(mycomponent.getAsPerPlan());
					obj.setAsPerSite(mycomponent.getAsatSite());
					obj.setLastUpdatedBy(loginusername);
					obj.setLastUpdatedDt(new Date());

					if (mycomponent.getGroundFloor() != null) {
						beanNewPlinthArea.saveorUpdateNewPlinthArea(obj);
						uiflowdata.getPlinthArea1().add(obj);
						i++;
					}

				}

			}
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

	}
	void saveEstimateDetails() {
		try {
			beanEstimate.deleteExistingPropApplcntEstmate(headerid);
		} catch (Exception e) {
			logger.info("Error-->" + e);
		}

		TPemCmPropApplcntEstmate obj = new TPemCmPropApplcntEstmate();
		obj.setDocId(headerid);
		obj.setFieldLabel(lblAppEstimate.getValue());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfAppEstimate.getValue().replaceAll("[^\\d.]", ""))));
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanEstimate.saveorUpdatePropApplcntEstmate(obj);
		uiflowdata.getApplicantEstimate().add(obj);

		obj = new TPemCmPropApplcntEstmate();
		obj.setDocId(headerid);
		obj.setFieldLabel(lblDtlsAppEstimate.getValue());
		obj.setFieldValue(tfDtlsAppEstimate.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanEstimate.saveorUpdatePropApplcntEstmate(obj);
		uiflowdata.getApplicantEstimate().add(obj);

		obj = new TPemCmPropApplcntEstmate();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDetails1.getValue());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDetailVal1.getValue().replaceAll("[^\\d.]", ""))));
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanEstimate.saveorUpdatePropApplcntEstmate(obj);
		uiflowdata.getApplicantEstimate().add(obj);

		obj = new TPemCmPropApplcntEstmate();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDetails2.getValue());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDetailVal2.getValue().replaceAll("[^\\d.]", ""))));
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanEstimate.saveorUpdatePropApplcntEstmate(obj);
		uiflowdata.getApplicantEstimate().add(obj);

		obj = new TPemCmPropApplcntEstmate();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDetails3.getValue());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDetailVal2.getValue().replaceAll("[^\\d.]", ""))));
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanEstimate.saveorUpdatePropApplcntEstmate(obj);
		uiflowdata.getApplicantEstimate().add(obj);

		if (tfDynamicAppEstimate1.getValue() != null
				&& tfDynamicAppEstimate1.getValue().trim().length() > 0
				|| tfDynamicAppEstimate2.getValue() != null
				&& tfDynamicAppEstimate2.getValue().trim().length() > 0) {
			obj = new TPemCmPropApplcntEstmate();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicAppEstimate1.getValue());
			obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDynamicAppEstimate2.getValue().replaceAll("[^\\d.]", ""))));
			obj.setOrderNo(7L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanEstimate.saveorUpdatePropApplcntEstmate(obj);
			uiflowdata.getApplicantEstimate().add(obj);
		}
		if (tfDynamicAppEstimate3.getValue() != null
				&& tfDynamicAppEstimate3.getValue().trim().length() > 0
				|| tfDynamicAppEstimate4.getValue() != null
				&& tfDynamicAppEstimate4.getValue().trim().length() > 0) {
			obj = new TPemCmPropApplcntEstmate();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicAppEstimate3.getValue());
			obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDynamicAppEstimate4.getValue().replaceAll("[^\\d.]", ""))));
			obj.setOrderNo(8L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanEstimate.saveorUpdatePropApplcntEstmate(obj);
			uiflowdata.getApplicantEstimate().add(obj);
		}
		if (tfDynamicAppEstimate5.getValue() != null
				&& tfDynamicAppEstimate5.getValue().trim().length() > 0
				|| tfDynamicAppEstimate6.getValue() != null
				&& tfDynamicAppEstimate6.getValue().trim().length() > 0) {
			obj = new TPemCmPropApplcntEstmate();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicAppEstimate5.getValue());
			obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDynamicAppEstimate6.getValue().replaceAll("[^\\d.]", ""))));
			obj.setOrderNo(9L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanEstimate.saveorUpdatePropApplcntEstmate(obj);
			uiflowdata.getApplicantEstimate().add(obj);
		}
		if (tfDynamicAppEstimate7.getValue() != null
				&& tfDynamicAppEstimate7.getValue().trim().length() > 0
				|| tfDynamicAppEstimate8.getValue() != null
				&& tfDynamicAppEstimate8.getValue().trim().length() > 0) {
			obj = new TPemCmPropApplcntEstmate();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicAppEstimate7.getValue());
			obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDynamicAppEstimate8.getValue().replaceAll("[^\\d.]", ""))));
			obj.setOrderNo(10L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanEstimate.saveorUpdatePropApplcntEstmate(obj);
			uiflowdata.getApplicantEstimate().add(obj);
		}
		obj = new TPemCmPropApplcntEstmate();
		obj.setDocId(headerid);
		obj.setFieldLabel(lblTotal.getValue());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfTotalval.getValue().replaceAll("[^\\d.]", ""))));
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanEstimate.saveorUpdatePropApplcntEstmate(obj);
		uiflowdata.getApplicantEstimate().add(obj);
	}

	void saveReasonableDetails() {
		try {
			beanReasonable.deleteExistingPropRsnbleEstmate(headerid);
		} catch (Exception e) {
			logger.info("Error-->" + e);
		}

		TPemCmPropRsnbleEstmate obj = new TPemCmPropRsnbleEstmate();

		obj.setDocId(headerid);
		obj.setFieldLabel(lblAppReasonable.getValue());
		obj.setFieldValue((String) slAppReasonable.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanReasonable.saveorUpdatePropRsnbleEstmate(obj);
		uiflowdata.getApplicantReason().add(obj);

		if (slAppReasonable.getValue().equals(Common.NO_DESC)) {
			obj = new TPemCmPropRsnbleEstmate();
			obj.setDocId(headerid);
			obj.setFieldLabel(lblReasonEstimate.getValue());
			obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfReasonEstimateVal.getValue().replaceAll("[^\\d.]", ""))));
			obj.setOrderNo(2L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanReasonable.saveorUpdatePropRsnbleEstmate(obj);
			uiflowdata.getApplicantReason().add(obj);
			
			obj = new TPemCmPropRsnbleEstmate();
			obj.setDocId(headerid);
			obj.setFieldLabel(lblDtlsAppReasonable.getValue());
			obj.setFieldValue(tfDtlsAppReasonable.getValue());
			obj.setOrderNo(3L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanReasonable.saveorUpdatePropRsnbleEstmate(obj);
			uiflowdata.getApplicantReason().add(obj);

			obj = new TPemCmPropRsnbleEstmate();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDetailsReason1.getValue());
			obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDetailReasonVal1.getValue().replaceAll("[^\\d.]", ""))));
			obj.setOrderNo(4L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanReasonable.saveorUpdatePropRsnbleEstmate(obj);
			uiflowdata.getApplicantReason().add(obj);

			obj = new TPemCmPropRsnbleEstmate();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDetailsReason2.getValue());
			obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDetailReasonVal2.getValue().replaceAll("[^\\d.]", ""))));
			obj.setOrderNo(5L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanReasonable.saveorUpdatePropRsnbleEstmate(obj);
			uiflowdata.getApplicantReason().add(obj);

			obj = new TPemCmPropRsnbleEstmate();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDetailsReason3.getValue());
			obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDetailReasonVal3.getValue().replaceAll("[^\\d.]", ""))));
			obj.setOrderNo(6L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanReasonable.saveorUpdatePropRsnbleEstmate(obj);
			uiflowdata.getApplicantReason().add(obj);

			if (tfDynamicAppReason1.getValue() != null
					&& tfDynamicAppReason1.getValue().trim().length() > 0
					|| tfDynamicAppReason2.getValue() != null
					&& tfDynamicAppReason2.getValue().trim().length() > 0) {
				obj = new TPemCmPropRsnbleEstmate();
				obj.setDocId(headerid);
				obj.setFieldLabel(tfDynamicAppReason1.getValue());
				obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDynamicAppReason2.getValue().replaceAll("[^\\d.]", ""))));
				obj.setOrderNo(7L);
				obj.setLastUpdatedDt(new Date());
				obj.setLastUpdatedBy(loginusername);
				beanReasonable.saveorUpdatePropRsnbleEstmate(obj);
				uiflowdata.getApplicantReason().add(obj);
			}
			if (tfDynamicAppReason3.getValue() != null
					&& tfDynamicAppReason3.getValue().trim().length() > 0
					|| tfDynamicAppReason4.getValue() != null
					&& tfDynamicAppReason4.getValue().trim().length() > 0) {
				obj = new TPemCmPropRsnbleEstmate();
				obj.setDocId(headerid);
				obj.setFieldLabel(tfDynamicAppReason3.getValue());
				obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfDynamicAppReason4.getValue().replaceAll("[^\\d.]", ""))));
				obj.setOrderNo(8L);
				obj.setLastUpdatedDt(new Date());
				obj.setLastUpdatedBy(loginusername);
				beanReasonable.saveorUpdatePropRsnbleEstmate(obj);
				uiflowdata.getApplicantReason().add(obj);
			}
			obj = new TPemCmPropRsnbleEstmate();
			obj.setDocId(headerid);
			obj.setFieldLabel(lblReasonTotal.getValue());
			obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfReasonTotalval.getValue().replaceAll("[^\\d.]", ""))));
			obj.setOrderNo(9L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanReasonable.saveorUpdatePropRsnbleEstmate(obj);
			uiflowdata.getApplicantReason().add(obj);
		}

	}
	void saveEarthQuakeDetails(){
		try {
			beanRiskDtls.deleteExistingBldngRiskDtls(headerid);
		} catch (Exception e) {

		}

		TPemCmBldngRiskDtls obj = new TPemCmBldngRiskDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(slEarthQuake.getCaption());
		obj.setFieldValue((String) slEarthQuake.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanRiskDtls.saveorUpdateBldngRiskDtls(obj);
		uiflowdata.getEarthQuake().add(obj);

		if (tfDynamicEarthquake1.getValue() != null
				&& tfDynamicEarthquake1.getValue().trim().length() > 0) {
			obj = new TPemCmBldngRiskDtls();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicEarthquake1.getCaption());
			obj.setFieldValue((String) tfDynamicEarthquake1.getValue());
			obj.setOrderNo(2L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanRiskDtls.saveorUpdateBldngRiskDtls(obj);
			uiflowdata.getEarthQuake().add(obj);
		}

		if (tfDynamicEarthquake2.getValue() != null
				&& tfDynamicEarthquake2.getValue().trim().length() > 0) {
			obj = new TPemCmBldngRiskDtls();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicEarthquake2.getCaption());
			obj.setFieldValue((String) tfDynamicEarthquake2.getValue());
			obj.setOrderNo(3L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanRiskDtls.saveorUpdateBldngRiskDtls(obj);
			uiflowdata.getEarthQuake().add(obj);
		}

	}
	void saveCostConstDetails() {
		try {
			beanCostConstruction.deleteExistingCostofcnstructn(headerid);
		} catch (Exception e) {

		}

		TPemCmBldngCostofcnstructn obj = new TPemCmBldngCostofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfCostConstruction.getCaption());
		obj.setFieldValue(XMLUtil.IndianFormat(new BigDecimal(tfCostConstruction.getValue().replaceAll("[^\\d.]", ""))));
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanCostConstruction.saveorUpdateCostofcnstructnDetails(obj);
		uiflowdata.getConstValuation().add(obj);

		if (tfDynamicCostConst1.getValue() != null
				&& tfDynamicCostConst1.getValue().trim().length() > 0) {
			obj = new TPemCmBldngCostofcnstructn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicCostConst1.getValue());
			obj.setFieldValue((String)XMLUtil.IndianFormat(new BigDecimal( tfDynamicCostConst2.getValue().toString())));			obj.setOrderNo(2L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanCostConstruction.saveorUpdateCostofcnstructnDetails(obj);
			uiflowdata.getConstValuation().add(obj);
		}
		if (tfDynamicCostConst3.getValue() != null
				&& tfDynamicCostConst3.getValue().trim().length() > 0) {
			obj = new TPemCmBldngCostofcnstructn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicCostConst3.getValue());
			obj.setFieldValue((String)XMLUtil.IndianFormat(new BigDecimal( tfDynamicCostConst4.getValue().toString())));
			obj.setOrderNo(3L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanCostConstruction.saveorUpdateCostofcnstructnDetails(obj);
			uiflowdata.getConstValuation().add(obj);
		}
		if (tfDynamicCostConst5.getValue() != null
				&& tfDynamicCostConst5.getValue().trim().length() > 0) {
			obj = new TPemCmBldngCostofcnstructn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicCostConst5.getValue());
			obj.setFieldValue((String) XMLUtil.IndianFormat(new BigDecimal( tfDynamicCostConst6.getValue().toString())));
			obj.setOrderNo(4L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanCostConstruction.saveorUpdateCostofcnstructnDetails(obj);
			uiflowdata.getConstValuation().add(obj);
		}
		if (tfDynamicCostConst7.getValue() != null
				&& tfDynamicCostConst7.getValue().trim().length() > 0) {
			obj = new TPemCmBldngCostofcnstructn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicCostConst7.getValue());
			obj.setFieldValue((String) XMLUtil.IndianFormat(new BigDecimal( tfDynamicCostConst8.getValue().toString())));
			obj.setOrderNo(5L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanCostConstruction.saveorUpdateCostofcnstructnDetails(obj);
			uiflowdata.getConstValuation().add(obj);
		}
		if (tfDynamicCostConst9.getValue() != null
				&& tfDynamicCostConst9.getValue().trim().length() > 0) {
			obj = new TPemCmBldngCostofcnstructn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicCostConst9.getValue());
			obj.setFieldValue((String) XMLUtil.IndianFormat(new BigDecimal( tfDynamicCostConst10.getValue().toString())));
			obj.setOrderNo(6L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanCostConstruction.saveorUpdateCostofcnstructnDetails(obj);
			uiflowdata.getConstValuation().add(obj);
		}

	}
	
	void savePlumbingDetails(){
		try{
			beanPlumInstl.deleteExistingSbiBldngPlumpInstltn(headerid);
		}catch(Exception e){
			logger.info("Error-->"+e);
		}	

			TPemSbiBldngPlumpInstltn obj = new TPemSbiBldngPlumpInstltn();
			
			obj.setDocId(headerid);
			obj.setFieldLabel(tfNoofClosets.getCaption());
			obj.setFieldValue((String)tfNoofClosets.getValue());
			obj.setOrderNo(1L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPlumInstl.saveorUpdateSbiBldngPlumpInstltn(obj);
				uiflowdata.getPlumInstln().add(obj);
			
			obj=new TPemSbiBldngPlumpInstltn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfNoofWashbin.getCaption());
			obj.setFieldValue(tfNoofWashbin.getValue());
			obj.setOrderNo(2L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPlumInstl.saveorUpdateSbiBldngPlumpInstltn(obj);
				uiflowdata.getPlumInstln().add(obj);
			
			obj=new TPemSbiBldngPlumpInstltn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfWatertaps.getCaption());
			obj.setFieldValue(tfWatertaps.getValue());
			obj.setOrderNo(3L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPlumInstl.saveorUpdateSbiBldngPlumpInstltn(obj);
				uiflowdata.getPlumInstln().add(obj);
			
			obj=new TPemSbiBldngPlumpInstltn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfAnyFixtures.getCaption());
			obj.setFieldValue(tfAnyFixtures.getValue());
			obj.setOrderNo(4L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPlumInstl.saveorUpdateSbiBldngPlumpInstltn(obj);
				uiflowdata.getPlumInstln().add(obj);
			
			
			if(tfDynamicPlum1.getValue()!=null&&tfDynamicPlum1.getValue().trim().length()>0)
			{
			obj = new TPemSbiBldngPlumpInstltn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicPlum1.getCaption());
			obj.setFieldValue(tfDynamicPlum1.getValue());
			obj.setOrderNo(5L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPlumInstl.saveorUpdateSbiBldngPlumpInstltn(obj);
				uiflowdata.getPlumInstln().add(obj);
			}
			if(tfDynamicPlum2.getValue()!=null&&tfDynamicPlum2.getValue().trim().length()>0)
			{
			obj = new TPemSbiBldngPlumpInstltn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicPlum2.getCaption());
			obj.setFieldValue(tfDynamicPlum2.getValue());
			obj.setOrderNo(6L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPlumInstl.saveorUpdateSbiBldngPlumpInstltn(obj);
			uiflowdata.getPlumInstln().add(obj);
			}	}
	void saveElectricalDetails(){
		try {
			beanElectInstl.deleteExistingSbiBldngElctrcInstltn(headerid);
		} catch (Exception e) {
			logger.info("Error-->" + e);
		}

		TPemSbiBldngElctrcInstltn obj = new TPemSbiBldngElctrcInstltn();

		obj.setDocId(headerid);
		obj.setFieldLabel(tfTypeofwiring.getCaption());
		obj.setFieldValue(tfTypeofwiring.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanElectInstl.savorUpdateeSbiBldngElctrcInstltn(obj);
		uiflowdata.getElecInstln().add(obj);

		obj = new TPemSbiBldngElctrcInstltn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfClassFit.getCaption());
		obj.setFieldValue(tfClassFit.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanElectInstl.savorUpdateeSbiBldngElctrcInstltn(obj);
		uiflowdata.getElecInstln().add(obj);

		obj = new TPemSbiBldngElctrcInstltn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfNOofLight.getCaption());
		obj.setFieldValue(tfNOofLight.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanElectInstl.savorUpdateeSbiBldngElctrcInstltn(obj);
		uiflowdata.getElecInstln().add(obj);
		
		obj = new TPemSbiBldngElctrcInstltn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfExhaustFan.getCaption());
		obj.setFieldValue(tfExhaustFan.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanElectInstl.savorUpdateeSbiBldngElctrcInstltn(obj);
		uiflowdata.getElecInstln().add(obj);

		obj = new TPemSbiBldngElctrcInstltn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFan.getCaption());
		obj.setFieldValue(tfFan.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanElectInstl.savorUpdateeSbiBldngElctrcInstltn(obj);
		uiflowdata.getElecInstln().add(obj);
		
		obj = new TPemSbiBldngElctrcInstltn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfSpareplug.getCaption());
		obj.setFieldValue(tfSpareplug.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanElectInstl.savorUpdateeSbiBldngElctrcInstltn(obj);
		uiflowdata.getElecInstln().add(obj);

		if (tfDynamicElectrical1.getValue() != null
				&& tfDynamicElectrical1.getValue().trim().length() > 0) {
			obj = new TPemSbiBldngElctrcInstltn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicElectrical1.getCaption());
			obj.setFieldValue(tfDynamicElectrical1.getValue());
			obj.setOrderNo(7L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanElectInstl.savorUpdateeSbiBldngElctrcInstltn(obj);
			uiflowdata.getElecInstln().add(obj);
		}
		if (tfDynamicElectrical2.getValue() != null
				&& tfDynamicElectrical2.getValue().trim().length() > 0) {
			obj = new TPemSbiBldngElctrcInstltn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicElectrical2.getCaption());
			obj.setFieldValue(tfDynamicElectrical2.getValue());
			obj.setOrderNo(8L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanElectInstl.savorUpdateeSbiBldngElctrcInstltn(obj);
			uiflowdata.getElecInstln().add(obj);
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
	void savePropertyValueDetails(){
		try{
			beanPropertyvalue.deleteExistingPropValtnSummry(headerid);
		}catch(Exception e){
			
		}
		TPemCmPropValtnSummry obj=new TPemCmPropValtnSummry();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFairMarketRate.getCaption());
		obj.setFieldValue((String) tfFairMarketRate.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropertyvalue.saveorUpdatePropValtnSummry(obj);
		uiflowdata.getPropertyValue().add(obj);
		
		obj=new TPemCmPropValtnSummry();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfRealizabletRate.getCaption());
		obj.setFieldValue((String) tfRealizabletRate.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropertyvalue.saveorUpdatePropValtnSummry(obj);
		uiflowdata.getPropertyValue().add(obj);
		
		obj=new TPemCmPropValtnSummry();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDistressRate.getCaption());
		obj.setFieldValue(tfDistressRate.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropertyvalue.saveorUpdatePropValtnSummry(obj);
		uiflowdata.getPropertyValue().add(obj);
		
		obj=new TPemCmPropValtnSummry();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfGuidelineRate.getCaption());
		obj.setFieldValue(tfGuidelineRate.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropertyvalue.saveorUpdatePropValtnSummry(obj);
		uiflowdata.getPropertyValue().add(obj);
	}


	
	private void saveBuildingTechDetails(){
		
		try {
			beanTechDtls.deleteExistingBldngTechDtls(headerid);
		} catch (Exception e) {

		}

		TPemCmBldngTechDetails obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slTypeBuilding.getCaption());
		obj.setFieldValue((String)slTypeBuilding.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slBuildStructure.getCaption());
		obj.setFieldValue((String) slBuildStructure.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);


		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfYearConstructionBuild.getCaption());
		obj.setFieldValue(tfYearConstructionBuild.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		
		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel("Number of floors & height of each floor including basement if any");
		obj.setFieldValue(tfBuildNoFloors.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);


		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slExterior.getCaption());
		obj.setFieldValue((String)slExterior.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slInterior.getCaption());
		obj.setFieldValue((String)slInterior.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);

		
		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfLifeAge.getCaption());
		obj.setFieldValue((String)tfLifeAge.getValue());
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);
		
		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFutureLife.getCaption());
		obj.setFieldValue((String)tfFutureLife.getValue());
		obj.setOrderNo(8L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);

		
		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel("Details of plan approval from the appropriate authority");
		obj.setFieldValue((String)tfDetailsPlan.getValue());
		obj.setOrderNo(9L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);


		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel("Whether there are any deviations vis a vis the plan approval and effect of the same on valuation");
		obj.setFieldValue((String)slDeviation.getValue());
		obj.setOrderNo(10L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);

		
		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDtlsDeviation.getCaption());
		obj.setFieldValue((String)tfDtlsDeviation.getValue());
		obj.setOrderNo(11L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);
		
		if(tfDynamicBuildval1.getValue()!=null&&tfDynamicBuildval1.getValue().trim().length()>0)
		{
		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicBuildval1.getCaption());
		obj.setFieldValue((String) tfDynamicBuildval1.getValue());
		obj.setOrderNo(12L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);		}
		if(tfDynamicBuildval2.getValue()!=null&&tfDynamicBuildval2.getValue().trim().length()>0)
		{
		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicBuildval2.getCaption());
		obj.setFieldValue((String) tfDynamicBuildval2.getValue());
		obj.setOrderNo(13L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);

		}
	
	}
	void saveRoofHeightDetails() {

		try {

			try {
				beanRoofHt.deleteExistingRoofHtDetails(headerid);
			} catch (Exception e) {
				logger.info("Error-->"+e);
			}

			btnSave.setComponentError(null);
			Iterator<Component> myComps = layoutRoofHeight
					.getComponentIterator();
			int i = 1;
			while (myComps.hasNext()) {
				final Component component = myComps.next();

				if (component instanceof ComponentIterRoofHeight) {

					ComponentIterRoofHeight mycomponent = (ComponentIterRoofHeight) component;
					TPemCmBldngRoofHght obj = new TPemCmBldngRoofHght();
					obj.setDocId(headerid);
					obj.setOrderNo(Long.valueOf(i));
					obj.setFieldLabel(mycomponent.getGroundFloor());
					obj.setFieldValue(mycomponent.getRoofHeight());
					obj.setLastUpdatedBy(loginusername);
					obj.setLastUpdatedDt(new Date());

					if (mycomponent.getGroundFloor() != null) {
						beanRoofHt.saveOrUpdateRoofHghtDtls(obj);
						uiflowdata.getRoofHeight().add(obj);
						i++;
					}
			}
			}
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

	}
	
	private void saveOldPlanApprovalDetails() {
		try {
			beanOldPlanApprvl.deleteExistingPropOldPlanApprvl(headerid);
		} catch (Exception e) {

		}

		TPemCmPropOldPlanApprvl obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel((String)slLandandBuilding.getValue());
		obj.setFieldValue(tfLandandBuilding.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanOldPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);
		
		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel((String)slBuilding.getValue());
		obj.setFieldValue(tfBuilding.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanOldPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPlanApprovedBy.getCaption());
		obj.setFieldValue((String) tfPlanApprovedBy.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanOldPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(dfLicenseFrom.getCaption());
		obj.setFieldValue((String) dfLicenseFrom.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanOldPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slIsLicenceForced.getCaption());
		obj.setFieldValue((String) slIsLicenceForced.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanOldPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slAllApprovalRecved.getCaption());
		obj.setFieldValue((String) slAllApprovalRecved.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanOldPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slConstAsperAppPlan.getCaption());
		obj.setFieldValue((String) slConstAsperAppPlan.getValue());
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanOldPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		if (tfDynamicPlanApproval1.getValue() != null
				&& tfDynamicPlanApproval1.getValue().trim().length() > 0) {
			obj = new TPemCmPropOldPlanApprvl();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicPlanApproval1.getCaption());
			obj.setFieldValue((String) tfDynamicPlanApproval1.getValue());
			obj.setOrderNo(8L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanOldPlanApprvl.savePropOldPlanApprvl(obj);
			uiflowdata.getPlanApproval().add(obj);
		}

		if (tfDynamicPlanApproval2.getValue() != null
				&& tfDynamicPlanApproval2.getValue().trim().length() > 0) {
			obj = new TPemCmPropOldPlanApprvl();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicPlanApproval2.getCaption());
			obj.setFieldValue((String) tfDynamicPlanApproval2.getValue());
			obj.setOrderNo(9L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanOldPlanApprvl.savePropOldPlanApprvl(obj);
			uiflowdata.getPlanApproval().add(obj);
		}
	}
	private void saveNewPlanApprovalDetails() {
		try {
			beanNewPlanApprvl.deleteExistingPropNewPlanApprvl(headerid);
		} catch (Exception e) {

		}
		TPemCmPropNewPlanApprvl obj = new TPemCmPropNewPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel((String)slLandBuildConst.getValue());
		obj.setFieldValue(tfLandBuildConst.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanNewPlanApprvl.saveorUpdatePropNewPlanApprvl(obj);
		uiflowdata.getPlanApproval2().add(obj);
		
		obj = new TPemCmPropNewPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel((String)slBuildConst.getValue());
		obj.setFieldValue(tfBuildConst.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanNewPlanApprvl.saveorUpdatePropNewPlanApprvl(obj);
		uiflowdata.getPlanApproval2().add(obj);

		obj = new TPemCmPropNewPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPlanApproveConst.getCaption());
		obj.setFieldValue((String) tfPlanApproveConst.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanNewPlanApprvl.saveorUpdatePropNewPlanApprvl(obj);
		uiflowdata.getPlanApproval2().add(obj);

		obj = new TPemCmPropNewPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slAllApprovalRecvedConst.getCaption());
		obj.setFieldValue((String) slAllApprovalRecvedConst.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanNewPlanApprvl.saveorUpdatePropNewPlanApprvl(obj);
		uiflowdata.getPlanApproval3().add(obj);

		obj = new TPemCmPropNewPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slConstAsperAppPlanConst.getCaption());
		obj.setFieldValue((String) slConstAsperAppPlanConst.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanNewPlanApprvl.saveorUpdatePropNewPlanApprvl(obj);
		uiflowdata.getPlanApproval3().add(obj);

		if (tfDynamicConstPlanApp1.getValue() != null
				&& tfDynamicConstPlanApp1.getValue().trim().length() > 0) {
			obj = new TPemCmPropNewPlanApprvl();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicConstPlanApp1.getCaption());
			obj.setFieldValue((String) tfDynamicConstPlanApp1.getValue());
			obj.setOrderNo(8L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanNewPlanApprvl.saveorUpdatePropNewPlanApprvl(obj);
			uiflowdata.getPlanApproval3().add(obj);
		}

		if (tfDynamicConstPlanApp2.getValue() != null
				&& tfDynamicConstPlanApp2.getValue().trim().length() > 0) {
			obj = new TPemCmPropNewPlanApprvl();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicConstPlanApp1.getCaption());
			obj.setFieldValue((String) tfDynamicConstPlanApp1.getValue());
			obj.setOrderNo(9L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanNewPlanApprvl.saveorUpdatePropNewPlanApprvl(obj);
			uiflowdata.getPlanApproval3().add(obj);
		}
	}
void saveValuationDetails() {
	
	try {

		try {
			beanValuationDtls.deleteExistingBldngValutnSummry(headerid);
			
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

		btnSave.setComponentError(null);
		Iterator<Component> myComps =layoutValuationDetails.getComponentIterator();
			BigDecimal valuation=new BigDecimal(0.00);
			int i=1;
		while (myComps.hasNext()) {
			final Component component = myComps.next();
		
			if (component instanceof ComponenetIterValuationDetails) {
			
				ComponenetIterValuationDetails mycomponent = (ComponenetIterValuationDetails) component;

				ValuationDetailsList list=mycomponent.getValuationDtlsList();
				
				TPemCmBldngValutnSummry obj = new TPemCmBldngValutnSummry();
					obj.setDocId(headerid);
					obj.setOrderNo(Long.valueOf(i));
					obj.setPlinthArea(list.getPlinthAreaLabel());
					obj.setFieldLabel(list.getFloorDtlsLabel());
					obj.setRoofHt(list.getRoofHtLabel());
					obj.setAgeBuilding(list.getBuildAgeLabel());
					obj.setRateConstn(list.getRateLabel());
					obj.setReplaceCost(list.getReplaceLabel());
					obj.setDepreciation(list.getDepreciationLabel());
					obj.setNetValue(list.getNetValueLabel());
					obj.setLastUpdatedBy(loginusername);
					obj.setLastUpdatedDt(new Date());
					valuation=valuation.add(new BigDecimal(list.getNetValueLabel()));
				
					if (obj.getPlinthArea()!= null) {
						beanValuationDtls.saveorUpdateBldngValutnSummry(obj);
						
						uiflowdata.getValuationDtls().add(obj);
						i++;
					}
					uiflowdata.setTotalValuation(XMLUtil.IndianFormat(new BigDecimal(valuation.toString())));
			}

		}
	} catch (Exception e) {
		logger.info("Error-->"+e);
	}

}
void saveExtraItemsDetails(){

	try {

		try {
			beanExtra.deleteExistingXtraItms(headerid);
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

		btnSave.setComponentError(null);
		Iterator<Component> myComps = layoutExtraItems
				.getComponentIterator();
		int i = 1;
		BigDecimal extraItem=new BigDecimal(0.00);

		while (myComps.hasNext()) {
			final Component component = myComps.next();

			if (component instanceof ComponentIterExtraItems) {

				ComponentIterExtraItems mycomponent = (ComponentIterExtraItems) component;
				TPemCmBldngExtraItems obj = new TPemCmBldngExtraItems();
				obj.setDocId(headerid);
				obj.setOrderNo(Long.valueOf(i));
				obj.setFieldLabel(mycomponent.getExtraItem());
				obj.setFieldValue(XMLUtil.IndianFormat(new BigDecimal(mycomponent.getItemValue())));
				obj.setLastUpdatedBy(loginusername);
				obj.setLastUpdatedDt(new Date());
				extraItem=extraItem.add(new BigDecimal(mycomponent.getItemValue().replaceAll("[^\\d.]", "")));
				if (mycomponent.getExtraItem() != null) {
					beanExtra.saveorUpdateXtraItmsList(obj);
					uiflowdata.getExtraItem().add(obj);
					i++;
				}
				
				uiflowdata.setTotalExtraItem(XMLUtil.IndianFormat(new BigDecimal(extraItem.toString())));
			}

		}
	} catch (Exception e) {
		logger.info("Error-->"+e);
	}

}
void saveAdditionalDetails(){

	try {

		try {
			beanAddtional.deleteExistingAditnlItms(headerid);
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

		btnSave.setComponentError(null);
		Iterator<Component> myComps = layoutAdditionItem
				.getComponentIterator();
		int i = 1;
		BigDecimal addItem=new BigDecimal(0.00);

		while (myComps.hasNext()) {
			final Component component = myComps.next();

			if (component instanceof ComponentIterAdditionalItems) {

				ComponentIterAdditionalItems mycomponent = (ComponentIterAdditionalItems) component;
				TPemCmBldngAditnlItms obj = new TPemCmBldngAditnlItms();
				obj.setDocId(headerid);
				obj.setOrderNo(Long.valueOf(i));
				obj.setFieldLabel(mycomponent.getAdditionalItem());
				obj.setFieldValue(XMLUtil.IndianFormat(new BigDecimal(mycomponent.getItemValue())));
				obj.setLastUpdatedBy(loginusername);
				obj.setLastUpdatedDt(new Date());
				addItem=addItem.add(new BigDecimal(mycomponent.getItemValue().replaceAll("[^\\d.]", "")));
				if (mycomponent.getAdditionalItem() != null) {
					beanAddtional.saveorUpdateAditnlItms(obj);
					uiflowdata.getAddtionalItms().add(obj);
					i++;
				}
				uiflowdata.setTotalAdditional(XMLUtil.IndianFormat(new BigDecimal(addItem.toString())));
			}

		}
	} catch (Exception e) {
		logger.info("Error-->"+e);
	}

}
void saveMiscellaneousDetails(){

	try {

		try {
			beanMiscell.deleteExistingMiscData(headerid);
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

		btnSave.setComponentError(null);
		Iterator<Component> myComps = layoutMiscellaneous
				.getComponentIterator();
		int i = 1;
		BigDecimal miscell=new BigDecimal(0.00);
		while (myComps.hasNext()) {
			final Component component = myComps.next();

			if (component instanceof ComponentIterMiscellaneous) {

				ComponentIterMiscellaneous mycomponent = (ComponentIterMiscellaneous) component;
				TPemCmBldngMiscData obj = new TPemCmBldngMiscData();
				obj.setDocId(headerid);
				obj.setOrderNo(Long.valueOf(i));
				obj.setFieldLabel(mycomponent.getMiscellaneous());
				obj.setFieldValue(XMLUtil.IndianFormat(new BigDecimal(mycomponent.getItemValue())));
				obj.setLastUpdatedBy(loginusername);
				obj.setLastUpdatedDt(new Date());
				miscell=miscell.add(new BigDecimal(mycomponent.getItemValue().replaceAll("[^\\d.]", "")));
				if (mycomponent.getMiscellaneous() != null) {
					beanMiscell.saveorUpdateMiscellaneousData(obj);
					uiflowdata.getMiscell().add(obj);
					i++;
				}
				uiflowdata.setTotalMiscellaneous(XMLUtil.IndianFormat(new BigDecimal(miscell.toString())));
			}

		}
	} catch (Exception e) {
		logger.info("Error-->"+e);
	}

}
void saveServiceDetails(){

	try {

		try {
			beanService.deleteExistingBldgService(headerid);
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

		btnSave.setComponentError(null);
		Iterator<Component> myComps = layoutServices
				.getComponentIterator();
		int i = 1;
		BigDecimal service=new BigDecimal(0.00);
		while (myComps.hasNext()) {
			final Component component = myComps.next();

			if (component instanceof ComponentIerServices) {

				ComponentIerServices mycomponent = (ComponentIerServices) component;
				TPemCmBldngService obj = new TPemCmBldngService();
				obj.setDocId(headerid);
				obj.setOrderNo(Long.valueOf(i));
				obj.setFieldLabel(mycomponent.getService());
				obj.setFieldValue(XMLUtil.IndianFormat(new BigDecimal(mycomponent.getServiceValue())));
				obj.setLastUpdatedBy(loginusername);
				obj.setLastUpdatedDt(new Date());
				service=service.add(new BigDecimal(mycomponent.getServiceValue().replaceAll("[^\\d.]", "")));
				if (mycomponent.getService() != null) {
					beanService.saveBldgService(obj);
					uiflowdata.getService().add(obj);
					i++;
				}
				uiflowdata.setTotalServices(XMLUtil.IndianFormat(new BigDecimal(service.toString())));
			
			}
			

		}
	} catch (Exception e) {
		logger.info("Error-->"+e);
	}

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
			editConstructionDetails();
		} catch (Exception e) {
			
		}

		try {
			editViolationDetails();
		} catch (Exception e) {
			
		}
		try {
			editLandValuationDetails();
		} catch (Exception e) {
			
		}
		try {
			editConstValuationDetails();
		} catch (Exception e) {
			
		}
		try {
			editOldPlinthAreaDetails();
		} catch (Exception e) {
			
		}
		try {
			editNewPlinthAreaDetails();
		} catch (Exception e) {
			
		}
		try {
			editOldBuildSpecDetails();
		} catch (Exception e) {
			
		}try {
			editNewBuildSpecDetails();
		} catch (Exception e) {
			
		}
		try {
			editEstimateDetails();
		} catch (Exception e) {
			
		}
		try {
			editReasonableDetails();
		} catch (Exception e) {
			
		}try {
			editEarthquakeDetails();
		} catch (Exception e) {
			
		}
		try {
			editCostConstructDetails();
		} catch (Exception e) {
			
		}
		try{
			editPlumbingDetails();
		}catch(Exception e){
			
		}try{
			editElectricalDetails();
		}catch(Exception e){
			
		}
		try {
			editGuidelinevalueDetails();
		} catch (Exception e) {
			
		}

		try {
			editGuidelineReferenceValues();
		} catch (Exception e) {
			
		}
		try{
			editPropertyValueDetails();
		}catch(Exception e){
			
		}
		try{
			editOldPlanApprovalDetails();
		}catch(Exception e){
			
		}try{
			editPlanApprovalDetailsNew();
		}catch(Exception e){
			
		}
		try{
			editBuildTechDetails();
		}catch(Exception e){
			
		}try{
			editRoofHeightDetails();
		}catch(Exception e){
			
		}try{
			editValuationDetails();
		}catch(Exception e){
			
		}try {
			editExtraItems();
		} catch (Exception e) {
			
		}
		try {
			editAdditionlItems();
		} catch (Exception e) {
			
		}
		try {
			editMiscellaneous();
		} catch (Exception e) {
			
		}
		try {
			editServices();
		} catch (Exception e) {
			
		}
	}

	void editOwnerDetails() {
		try {
			List<TPemCmOwnerDetails> ownerlist = beanOwner.getOwnerDtlsList(headerid);
			//uiflowdata.setCustomer(custlist);
		layoutOwnerDetails1.removeAllComponents();
	
		for (TPemCmOwnerDetails obj : ownerlist) {

			layoutOwnerDetails1
					.addComponent(new ComponentIterOwnerDetails(obj.getFieldLabel(),obj.getFieldValue()));
		}}catch(Exception e){
			
		}
	}

	void editAssetDetails() {
		try {
			List<TPemCmAssetDetails> assetlist = beanAsset.getAssetDetailsList(headerid);
			uiflowdata.setAssetDtls(assetlist);
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
		//uiflowdata.setDocument(doclist);
		panelNormalDocumentDetails.removeAllComponents();
		panelNormalDocumentDetails.addComponent(btnAddNorDoc);
		panelNormalDocumentDetails.setComponentAlignment(btnAddNorDoc,
				Alignment.BOTTOM_RIGHT);
		for (TPemCmPropDocDetails obj : doclist) {

			panelNormalDocumentDetails
					.addComponent(new ComponentIteratorNormlDoc(obj
							.getFieldLabel(), obj.getApprovalYN(), obj
							.getApproveAuth(), obj.getApproveRef()));
		}

	}

	void editLegalDocuments() {
		List<TPemCmPropLegalDocs> doclist = legalDoc.getPropLegalDocsList(headerid);
		//uiflowdata.setLegalDoc(doclist);
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



	void editOldBuildSpecDetails() {
		List<TPemCmBldngOldSpec> specList = beanOldSpecBuilding.getOldBldgSpecList(headerid);

		List<BuildSpecList> specinputList = new ArrayList<BuildSpecList>();

		try {
			for (int i = 0; i < specList.size(); i = i + 8) {
				BuildSpecList specListObj = new BuildSpecList();

				TPemCmBldngOldSpec obj = specList.get(i);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setTypeStructureLabel(obj.getFieldLabel());
				specListObj.setTypeStructDeedValue(obj.getAsPerDeed());
				specListObj.setTypeStructSiteValue(obj.getAsPerSite());
				specListObj.setTypeStructPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());
				
				obj=new TPemCmBldngOldSpec();
				obj = specList.get(i + 1);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setFoundationLabel(obj.getFieldLabel());
				specListObj.setFoundationDeedValue(obj.getAsPerDeed());
				specListObj.setFoundationSiteValue(obj.getAsPerSite());
				specListObj.setFoundationPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());

				obj=new TPemCmBldngOldSpec();
				obj = specList.get(i + 2);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setBasementLabel(obj.getFieldLabel());
				specListObj.setBasementDeedValue(obj.getAsPerDeed());
				specListObj.setBasementSiteValue(obj.getAsPerSite());
				specListObj.setBasementPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());
		
				obj=new TPemCmBldngOldSpec();
				obj = specList.get(i + 3);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setSuperStructLabel(obj.getFieldLabel());
				specListObj.setSuperStructDeedValue(obj.getAsPerDeed());
				specListObj.setSuperStructSiteValue(obj.getAsPerSite());
				specListObj.setSuperStructPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());
				
				obj=new TPemCmBldngOldSpec();
				obj= specList.get(i + 4);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setRoofingLabel(obj.getFieldLabel());
				specListObj.setRoofingDeedValue(obj.getAsPerDeed());
				specListObj.setRoofingSiteValue(obj.getAsPerSite());
				specListObj.setRoofingPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());
				
				obj=new TPemCmBldngOldSpec();
				obj= specList.get(i + 5);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setFlooringLabel(obj.getFieldLabel());
				specListObj.setFlooringDeedValue(obj.getAsPerDeed());
				specListObj.setFlooringSiteValue(obj.getAsPerSite());
				specListObj.setFlooringPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());

				obj=new TPemCmBldngOldSpec();
				obj = specList.get(i + 6);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setJoineriesLabel(obj.getFieldLabel());
				specListObj.setJoineriesDeedValue(obj.getAsPerDeed());
				specListObj.setJoineriesSiteValue(obj.getAsPerSite());
				specListObj.setJoineriesPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());
				
				obj=new TPemCmBldngOldSpec();
				obj = specList.get(i + 7);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setFinishesLabel(obj.getFieldLabel());
				specListObj.setFinishesDeedValue(obj.getAsPerDeed());
				specListObj.setFinishesSiteValue(obj.getAsPerSite());
				specListObj.setFinishesPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());

				specinputList.add(specListObj);

			}
		} catch (Exception e) {
			
		}

		try {
			panelBuildSpecfication.removeAllComponents();
			panelBuildSpecfication.addComponent(btnAddBuildSpec);
			panelBuildSpecfication.setComponentAlignment(btnAddBuildSpec,
					Alignment.BOTTOM_RIGHT);
			for (BuildSpecList inpobj :specinputList) {
				panelBuildSpecfication.addComponent(new ComponentIterBuildingSpecfication(
						inpobj,true,true,true));
			}
			} catch (Exception e) {
			
		}
	}
	
	void editNewBuildSpecDetails() {
		List<TPemCmBldngNewSpec> specList = beanNewSpecBuilding.getNwBldgSpecList(headerid);
		List<BuildSpecList> specinputList = new ArrayList<BuildSpecList>();

		try {
			for (int i = 0; i < specList.size(); i = i + 8) {
				BuildSpecList specListObj = new BuildSpecList();

				TPemCmBldngNewSpec obj = specList.get(i);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setTypeStructureLabel(obj.getFieldLabel());
				specListObj.setTypeStructDeedValue(obj.getAsPerDeed());
				specListObj.setTypeStructSiteValue(obj.getAsPerSite());
				specListObj.setTypeStructPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());
				
				obj=new TPemCmBldngNewSpec();
				obj = specList.get(i + 1);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setFoundationLabel(obj.getFieldLabel());
				specListObj.setFoundationDeedValue(obj.getAsPerDeed());
				specListObj.setFoundationSiteValue(obj.getAsPerSite());
				specListObj.setFoundationPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());

				obj=new TPemCmBldngNewSpec();
				obj = specList.get(i + 2);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setBasementLabel(obj.getFieldLabel());
				specListObj.setBasementDeedValue(obj.getAsPerDeed());
				specListObj.setBasementSiteValue(obj.getAsPerSite());
				specListObj.setBasementPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());
		
				obj=new TPemCmBldngNewSpec();
				obj = specList.get(i + 3);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setSuperStructLabel(obj.getFieldLabel());
				specListObj.setSuperStructDeedValue(obj.getAsPerDeed());
				specListObj.setSuperStructSiteValue(obj.getAsPerSite());
				specListObj.setSuperStructPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());
				
				obj=new TPemCmBldngNewSpec();
				obj= specList.get(i + 4);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setRoofingLabel(obj.getFieldLabel());
				specListObj.setRoofingDeedValue(obj.getAsPerDeed());
				specListObj.setRoofingSiteValue(obj.getAsPerSite());
				specListObj.setRoofingPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());
				
				obj=new TPemCmBldngNewSpec();
				obj= specList.get(i + 5);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setFlooringLabel(obj.getFieldLabel());
				specListObj.setFlooringDeedValue(obj.getAsPerDeed());
				specListObj.setFlooringSiteValue(obj.getAsPerSite());
				specListObj.setFlooringPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());

				obj=new TPemCmBldngNewSpec();
				obj = specList.get(i + 6);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setJoineriesLabel(obj.getFieldLabel());
				specListObj.setJoineriesDeedValue(obj.getAsPerDeed());
				specListObj.setJoineriesSiteValue(obj.getAsPerSite());
				specListObj.setJoineriesPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());
				
				obj=new TPemCmBldngNewSpec();
				obj = specList.get(i + 7);
				specListObj.setGroupHdrLabel(obj.getGroupHdr());
				specListObj.setGrouphdrSite(obj.getGroupHdrSite());
				specListObj.setGrouphdrPlan(obj.getGroupHdrPlan());
				specListObj.setFinishesLabel(obj.getFieldLabel());
				specListObj.setFinishesDeedValue(obj.getAsPerDeed());
				specListObj.setFinishesSiteValue(obj.getAsPerSite());
				specListObj.setFinishesPlanValue(obj.getAsPerPlan());
				specListObj.setDeed(obj.getDeedValue());
				specListObj.setSite(obj.getSiteValue());
				specListObj.setPlan(obj.getPlanValue());

				specinputList.add(specListObj);

			}
		} catch (Exception e) {
			
		}
		try {
			panelBuildSpecficationNew.removeAllComponents();
			panelBuildSpecficationNew.addComponent(btnAddBuildSpecNew);
			panelBuildSpecficationNew.setComponentAlignment(btnAddBuildSpecNew,
					Alignment.BOTTOM_RIGHT);
			for (BuildSpecList inpobj :specinputList) {
				panelBuildSpecficationNew.addComponent(new ComponentIterBuildingSpecfication(
						inpobj,true,true,true));
			}
			} catch (Exception e) {
			
		}
	}
	void editPropDescDetails(){
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
	void editCharacterDetails(){
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
		//	slDrawApproval.setCaption(obj1.getFieldLabel());
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

	void editConstructionDetails() {
		try {
			List<TPemCmBldngStgofcnstructn> list = beanconstruction.getBldgStgofcnstList(headerid);
			TPemCmBldngStgofcnstructn obj1 = list.get(0);
			tfStageofConst.setValue(obj1.getFieldValue());
			tfStageofConst.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfSalablity.setValue(obj1.getFieldValue());
			tfSalablity.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfRentalValues.setValue(obj1.getFieldValue());
			tfRentalValues.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfIncome.setValue(obj1.getFieldValue());
			tfIncome.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			tfDynamicConstruction1.setValue(obj1.getFieldValue());
			tfDynamicConstruction1.setCaption(obj1.getFieldLabel());
			tfDynamicConstruction1.setVisible(true);
			obj1 = list.get(5);
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

	
	void editConstValuationDetails(){
		try{
		List<TPemSbiBldngCnstructnDtls> list = beanConstnDtls.getBldngCnstructnDtlsList(headerid);
				
		TPemSbiBldngCnstructnDtls obj1 = list.get(0);
		slTypeProperty.setValue(obj1.getFieldValue());
		slTypeProperty.setCaption(obj1.getFieldLabel());
		obj1 = list.get(1);
		slTypeStructure.setValue(obj1.getFieldValue());
		slTypeStructure.setCaption(obj1.getFieldLabel());
		obj1 = list.get(2);
		tfYearConstruction.setValue(obj1.getFieldValue());
		tfYearConstruction.setCaption(obj1.getFieldLabel());
		obj1 = list.get(3);
		tfNoFloors.setValue(obj1.getFieldValue());
		tfNoFloors.setCaption(obj1.getFieldLabel());
		obj1 = list.get(4);
		tfConstQuality.setValue(obj1.getFieldValue());
		tfConstQuality.setCaption(obj1.getFieldLabel());
		obj1 = list.get(5);
		slAllapproval.setValue(obj1.getFieldValue());
		slAllapproval.setCaption(obj1.getFieldLabel());
		obj1 = list.get(6);
		slIsConstruction.setValue(obj1.getFieldValue());
		slIsConstruction.setCaption(obj1.getFieldLabel());
		obj1 = list.get(7);
		tfReason.setValue(obj1.getFieldValue());
		tfReason.setCaption(obj1.getFieldLabel());
		obj1 = list.get(8);
		tfDynamicConstval1.setValue(obj1.getFieldValue());
		tfDynamicConstval1.setCaption(obj1.getFieldLabel());
		tfDynamicConstval1.setVisible(true);
		obj1 = list.get(9);
		tfDynamicConstval2.setValue(obj1.getFieldValue());
		tfDynamicConstval2.setCaption(obj1.getFieldLabel());
		tfDynamicConstval2.setVisible(true);

	} catch(Exception e) {

		
	}
		
}
	void editOldPlinthAreaDetails(){
		
		List<TPemCmBldngOldPlinthArea> plinthList = beanOldPlinthArea.getOldPlinthAreaList(headerid);
				
		layoutPlintharea.removeAllComponents();
		layoutPlintharea.addComponent(btnAddPlinth);
		layoutPlintharea.setComponentAlignment(btnAddPlinth,
				Alignment.BOTTOM_RIGHT);
		for (TPemCmBldngOldPlinthArea obj : plinthList) {

			layoutPlintharea
					.addComponent(new ComponentIterPlinthArea(obj.getFieldLabel(),obj.getAsPerPlan(),obj.getAsPerSite()));
		}
	}
void editNewPlinthAreaDetails(){
	
	List<TPemCmBldngNewPlinthArea> plinthList = beanNewPlinthArea.getNwPlinthAreaList(headerid);
			
	vlPlinthAreaNew.removeAllComponents();
	vlPlinthAreaNew.addComponent(btnAddPlinthNew);
	vlPlinthAreaNew.setComponentAlignment(btnAddPlinthNew,
			Alignment.BOTTOM_RIGHT);
	for (TPemCmBldngNewPlinthArea obj : plinthList) {

		vlPlinthAreaNew
				.addComponent(new ComponentIterPlinthArea(obj.getFieldLabel(),obj.getAsPerPlan(),obj.getAsPerSite()));
	}
}
	void editEstimateDetails(){
		try {
			List<TPemCmPropApplcntEstmate> estimateList = beanEstimate.getPropApplcntEstmateList(headerid);
			TPemCmPropApplcntEstmate obj1 = estimateList.get(0);;
			lblAppEstimate.setValue(obj1.getFieldLabel());
			tfAppEstimate.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));

			obj1 = estimateList.get(1);
			lblDtlsAppEstimate.setValue(obj1.getFieldLabel());
			tfDtlsAppEstimate.setValue(obj1.getFieldValue());

			obj1 = estimateList.get(2);
			tfDetails1.setValue(obj1.getFieldLabel());
			tfDetailVal1.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));

			obj1 = estimateList.get(3);
			tfDetails2.setValue(obj1.getFieldLabel());
			tfDetailVal2.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));

			obj1 = estimateList.get(4);
			tfDetails3.setValue(obj1.getFieldLabel());
			tfDetailVal3.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			
			obj1 = estimateList.get(5);
			lblTotal.setValue(obj1.getFieldLabel());
			tfTotalval.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));

			obj1 = estimateList.get(6);
			tfDynamicAppEstimate1.setValue(obj1.getFieldLabel());
			tfDynamicAppEstimate2.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			tfDynamicAppEstimate1.setVisible(true);
			tfDynamicAppEstimate2.setVisible(true);

			obj1 = estimateList.get(7);
			tfDynamicAppEstimate3.setValue(obj1.getFieldLabel());
			tfDynamicAppEstimate4.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			tfDynamicAppEstimate3.setVisible(true);
			tfDynamicAppEstimate4.setVisible(true);
			
			obj1 = estimateList.get(8);
			tfDynamicAppEstimate5.setValue(obj1.getFieldLabel());
			tfDynamicAppEstimate6.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			
			obj1 = estimateList.get(9);
			tfDynamicAppEstimate7.setValue(obj1.getFieldLabel());
			tfDynamicAppEstimate8.setValue(obj1.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
						
		} catch (Exception e) {
			
		}

	}
	void editReasonableDetails(){
		try {
			List<TPemCmPropRsnbleEstmate> reasonableList = beanReasonable.getPropRsnbleEstmateList(headerid);

			TPemCmPropRsnbleEstmate obj1 = reasonableList.get(0);
			lblAppReasonable.setValue(obj1.getFieldLabel());
			slAppReasonable.setValue(obj1.getFieldValue());

			obj1 = reasonableList.get(1);
			lblReasonEstimate.setValue(obj1.getFieldLabel());
			tfReasonEstimateVal.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));

			obj1 = reasonableList.get(2);
			lblDtlsAppReasonable.setValue(obj1.getFieldLabel());
			tfDtlsAppReasonable.setValue(obj1.getFieldValue());

			obj1 = reasonableList.get(3);
			tfDetailsReason1.setValue(obj1.getFieldLabel());
			tfDetailReasonVal1.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));

			obj1 = reasonableList.get(4);
			tfDetailsReason2.setValue(obj1.getFieldLabel());
			tfDetailReasonVal2.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));

			obj1 = reasonableList.get(5);
			tfDetailsReason3.setValue(obj1.getFieldLabel());
			tfDetailReasonVal3.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));

			obj1 = reasonableList.get(6);
			tfDynamicAppReason1.setValue(obj1.getFieldLabel());
			tfDynamicAppReason2.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			tfDynamicAppReason1.setVisible(true);
			tfDynamicAppReason2.setVisible(true);

			obj1 = reasonableList.get(7);
			tfDynamicAppReason3.setValue(obj1.getFieldLabel());
			tfDynamicAppReason4.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			tfDynamicAppReason3.setVisible(true);
			tfDynamicAppReason4.setVisible(true);

			obj1 = reasonableList.get(8);
			lblReasonTotal.setValue(obj1.getFieldLabel());
			tfReasonTotalval.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));

		} catch (Exception e) {
			
		}

	}

	void editEarthquakeDetails(){
		List<TPemCmBldngRiskDtls> list = beanRiskDtls.getBldngRiskDtlsList(headerid);
		TPemCmBldngRiskDtls obj1 = list.get(0);
		slEarthQuake.setValue(obj1.getFieldValue());
		slEarthQuake.setCaption(obj1.getFieldLabel());
		obj1 = list.get(1);
		tfDynamicEarthquake1.setValue(obj1.getFieldValue());
		tfDynamicEarthquake1.setCaption(obj1.getFieldLabel());
		tfDynamicEarthquake1.setVisible(true);
		obj1 = list.get(2);
		tfDynamicEarthquake2.setValue(obj1.getFieldValue());
		tfDynamicEarthquake2.setCaption(obj1.getFieldLabel());
		tfDynamicEarthquake2.setVisible(true);
	
		
	}
	void editCostConstructDetails(){
		List<TPemCmBldngCostofcnstructn> list = beanCostConstruction.getCostofcnstructnList(headerid);
		
		TPemCmBldngCostofcnstructn obj1 = list.get(0);
		tfCostConstruction.setValue(obj1.getFieldValue().replace("Rs. ","").replaceAll("[^\\d.]", ""));
		tfCostConstruction.setCaption(obj1.getFieldLabel());
		obj1 = list.get(1);
		tfDynamicCostConst1.setValue(obj1.getFieldLabel());
		tfDynamicCostConst2.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
		tfDynamicCostConst1.setVisible(true);
		tfDynamicCostConst2.setVisible(true);
		layoutCostConstruction1.setVisible(true);
		obj1 = list.get(2);
		tfDynamicCostConst3.setValue(obj1.getFieldLabel());
		tfDynamicCostConst4.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
		tfDynamicCostConst3.setVisible(true);
		tfDynamicCostConst4.setVisible(true);
		obj1 = list.get(3);
		tfDynamicCostConst5.setValue(obj1.getFieldLabel());
		tfDynamicCostConst6.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
		tfDynamicCostConst5.setVisible(true);
		tfDynamicCostConst6.setVisible(true);
		obj1 = list.get(4);
		tfDynamicCostConst7.setValue(obj1.getFieldLabel());
		tfDynamicCostConst8.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
		tfDynamicCostConst7.setVisible(true);
		tfDynamicCostConst8.setVisible(true);
		layoutCostConstruction2.setVisible(true);
		obj1 = list.get(5);
		tfDynamicCostConst9.setValue(obj1.getFieldLabel());
		tfDynamicCostConst10.setValue(obj1.getFieldValue().replace("Rs. ","").replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
		tfDynamicCostConst9.setVisible(true);
		tfDynamicCostConst10.setVisible(true);

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
				dimenListObj.setDirectionNorthLabel(obj1.getFieldLabel());
				dimenListObj.setNorthDeedValue(obj1.getAsPerDeed());
				dimenListObj.setNorthSiteValue(obj1.getAsPerSite());
				dimenListObj.setNorthPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());


				obj1 = adjoinList.get(i + 2);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionNorthLabel(obj1.getFieldLabel());
				dimenListObj.setNorthDeedValue(obj1.getAsPerDeed());
				dimenListObj.setNorthSiteValue(obj1.getAsPerSite());
				dimenListObj.setNorthPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());

				obj1 = adjoinList.get(i + 3);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionNorthLabel(obj1.getFieldLabel());
				dimenListObj.setNorthDeedValue(obj1.getAsPerDeed());
				dimenListObj.setNorthSiteValue(obj1.getAsPerSite());
				dimenListObj.setNorthPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());
				
				obj1 = adjoinList.get(i + 4);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionNorthLabel(obj1.getFieldLabel());
				dimenListObj.setNorthDeedValue(obj1.getAsPerDeed());
				dimenListObj.setNorthSiteValue(obj1.getAsPerSite());
				dimenListObj.setNorthPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());
				
				obj1 = adjoinList.get(i + 5);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionNorthLabel(obj1.getFieldLabel());
				dimenListObj.setNorthDeedValue(obj1.getAsPerDeed());
				dimenListObj.setNorthSiteValue(obj1.getAsPerSite());
				dimenListObj.setNorthPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());
				
				obj1 = adjoinList.get(i + 6);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionNorthLabel(obj1.getFieldLabel());
				dimenListObj.setNorthDeedValue(obj1.getAsPerDeed());
				dimenListObj.setNorthSiteValue(obj1.getAsPerSite());
				dimenListObj.setNorthPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());

				
				obj1 = adjoinList.get(i + 7);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionNorthLabel(obj1.getFieldLabel());
				dimenListObj.setNorthDeedValue(obj1.getAsPerDeed());
				dimenListObj.setNorthSiteValue(obj1.getAsPerSite());
				dimenListObj.setNorthPlanValue(obj1.getAsPerPlan());
				dimenListObj.setDeed(obj1.getDeedValue());
				dimenListObj.setSite(obj1.getSiteValue());
				dimenListObj.setPlan(obj1.getPlanValue());
				
				obj1 = adjoinList.get(i + 8);
				dimenListObj.setGrouphdrLabel(obj1.getGroupHdr());
				dimenListObj.setDirectionNorthLabel(obj1.getFieldLabel());
				dimenListObj.setNorthDeedValue(obj1.getAsPerDeed());
				dimenListObj.setNorthSiteValue(obj1.getAsPerSite());
				dimenListObj.setNorthPlanValue(obj1.getAsPerPlan());
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

			
		}
	}
	void editElectricalDetails(){
		try {
			List<TPemSbiBldngElctrcInstltn> estimateList = beanElectInstl.getSbiBldngElctrcInstltnList(headerid);
			TPemSbiBldngElctrcInstltn obj1 = estimateList.get(0);
			tfTypeofwiring.setValue(obj1.getFieldLabel());
			tfTypeofwiring.setValue(obj1.getFieldValue());

			obj1 = estimateList.get(1);
			tfClassFit.setValue(obj1.getFieldLabel());
			tfClassFit.setValue(obj1.getFieldValue());

			obj1 = estimateList.get(2);
			tfNOofLight.setValue(obj1.getFieldLabel());
			tfNOofLight.setValue(obj1.getFieldValue());

			obj1 = estimateList.get(3);
			tfExhaustFan.setValue(obj1.getFieldLabel());
			tfExhaustFan.setValue(obj1.getFieldValue());

			obj1 = estimateList.get(4);
			tfFan.setValue(obj1.getFieldLabel());
			tfFan.setValue(obj1.getFieldValue());
			
			obj1 = estimateList.get(5);
			tfSpareplug.setValue(obj1.getFieldLabel());
			tfSpareplug.setValue(obj1.getFieldValue());

			obj1 = estimateList.get(6);
			tfDynamicElectrical1.setValue(obj1.getFieldLabel());
			tfDynamicElectrical1.setValue(obj1.getFieldValue());
			tfDynamicElectrical1.setVisible(true);
			tfDynamicElectrical1.setVisible(true);

			obj1 = estimateList.get(7);
			tfDynamicElectrical2.setValue(obj1.getFieldLabel());
			tfDynamicElectrical2.setValue(obj1.getFieldValue());
			tfDynamicElectrical2.setVisible(true);
			tfDynamicElectrical2.setVisible(true);

			

		} catch (Exception e) {
			
		}

	}
	void editPlumbingDetails(){
		try{
		List<TPemSbiBldngPlumpInstltn> reasonableList=beanPlumInstl.getSbiBldngPlumpInstltnList(headerid);
		TPemSbiBldngPlumpInstltn obj1 = reasonableList.get(0);
		tfNoofClosets.setValue(obj1.getFieldLabel());
		tfNoofClosets.setValue(obj1.getFieldValue());
		
		obj1=reasonableList.get(1);
		tfNoofWashbin.setValue(obj1.getFieldLabel());
		tfNoofWashbin.setValue(obj1.getFieldValue());
		
		obj1=reasonableList.get(2);
		tfWatertaps.setValue(obj1.getFieldLabel());
		tfWatertaps.setValue(obj1.getFieldValue());
		
		obj1=reasonableList.get(3);
		tfAnyFixtures.setValue(obj1.getFieldLabel());
		tfAnyFixtures.setValue(obj1.getFieldValue());
		
		obj1=reasonableList.get(4);
		tfDynamicPlum1.setValue(obj1.getFieldLabel());
		tfDynamicPlum1.setValue(obj1.getFieldValue());
		tfDynamicPlum1.setVisible(true);
		
		obj1=reasonableList.get(5);
		tfDynamicPlum2.setValue(obj1.getFieldLabel());
		tfDynamicPlum2.setValue(obj1.getFieldValue());
		
		
		
	}
	catch(Exception e){
		
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
	void editValuationDetails(){
		List<TPemCmBldngValutnSummry> valList = beanValuationDtls.getBldngValutnSummryList(headerid);
		layoutValuationDetails.removeAllComponents();
		layoutValuationDetails.addComponent(btnAddValDtls);
		layoutValuationDetails.setComponentAlignment(btnAddValDtls, Alignment.TOP_RIGHT);
		layoutValuationDetails.addComponent(layoutValuationDetails1);
		List<ValuationDetailsList> list = new ArrayList<ValuationDetailsList>();
		try {
			for (int i = 0; i < valList.size(); i++) {
				ValuationDetailsList valObj=new ValuationDetailsList();
				
				TPemCmBldngValutnSummry obj1=valList.get(i);
				valObj.setFloorDtlsLabel(obj1.getFieldLabel());
				valObj.setPlinthAreaLabel(obj1.getPlinthArea());
				valObj.setRoofHtLabel(obj1.getRoofHt());
				valObj.setBuildAgeLabel(obj1.getAgeBuilding());
				valObj.setRateLabel(obj1.getRateConstn());
				valObj.setReplaceLabel(obj1.getReplaceCost());
				valObj.setDepreciationLabel(obj1.getDepreciation());
				valObj.setNetValueLabel(obj1.getNetValue());
				
				list.add(valObj);
			}
			for(ValuationDetailsList test:list)
			{
				layoutValuationDetails.addComponent(new ComponenetIterValuationDetails(test));
			}
	
		} catch (Exception e) {
			
		}
		

	}
void editExtraItems(){
		
		List<TPemCmBldngExtraItems> doclist = beanExtra.getXtraItmsList(headerid);
		layoutExtraItems.removeAllComponents();
		layoutExtraItems.addComponent(btnDynamicExtra);
		layoutExtraItems.setComponentAlignment(btnDynamicExtra,
				Alignment.TOP_RIGHT);
		for (TPemCmBldngExtraItems obj : doclist) {

			layoutExtraItems.addComponent(new ComponentIterExtraItems(obj.getFieldLabel(), obj.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", "")));
		}

	}
	void editAdditionlItems(){
		
		List<TPemCmBldngAditnlItms> addlist = beanAddtional.getAditnlItmsList(headerid);
		layoutAdditionItem.removeAllComponents();
		layoutAdditionItem.addComponent(btnDynamicAdditional);
		layoutAdditionItem.setComponentAlignment(btnDynamicAdditional,
				Alignment.TOP_RIGHT);
		for (TPemCmBldngAditnlItms obj : addlist) {

			layoutAdditionItem.addComponent(new ComponentIterAdditionalItems(obj.getFieldLabel(), obj.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", "")));
		}

	}
	void editMiscellaneous(){
		
		List<TPemCmBldngMiscData> miscellList = beanMiscell.getMiscDataList(headerid);
		layoutMiscellaneous.removeAllComponents();
		layoutMiscellaneous.addComponent(btnDynamicMiscell);
		layoutMiscellaneous.setComponentAlignment(btnDynamicMiscell,
				Alignment.TOP_RIGHT);
		for (TPemCmBldngMiscData obj : miscellList) {

			layoutMiscellaneous.addComponent(new ComponentIterMiscellaneous(obj.getFieldLabel(), obj.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", "")));
		}

	}
	void editServices(){
		
		List<TPemCmBldngService> doclist = beanService.getBldgServiceList(headerid);
		layoutServices.removeAllComponents();
		layoutServices.addComponent(btnDynamicServices);
		layoutServices.setComponentAlignment(btnDynamicServices,Alignment.TOP_RIGHT);
		for (TPemCmBldngService obj : doclist) {

			layoutServices.addComponent(new ComponentIerServices(obj.getFieldLabel(), obj.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", "")));
		}
		}
	void editPropertyValueDetails(){

		try {
			List<TPemCmPropValtnSummry> list = beanPropertyvalue.getPropValtnSummryList(headerid);
			TPemCmPropValtnSummry obj1 = list.get(1);
			tfRealizabletRate.setValue(obj1.getFieldValue());
			tfRealizabletRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfDistressRate.setValue(obj1.getFieldValue());
			tfDistressRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfGuidelineRate.setValue(obj1.getFieldValue());
			tfGuidelineRate.setCaption(obj1.getFieldLabel());

		} catch (Exception e) {

			
		}
	
	}
	private void editOldPlanApprovalDetails() {
		try {
			List<TPemCmPropOldPlanApprvl> list = beanOldPlanApprvl.getPropOldPlanApprvlList(headerid);
				
			TPemCmPropOldPlanApprvl obj1 = list.get(0);
			tfLandandBuilding.setValue(obj1.getFieldValue());
			slLandandBuilding.setValue(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfBuilding.setValue(obj1.getFieldValue());
			slBuilding.setValue(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfPlanApprovedBy.setValue(obj1.getFieldValue());
			tfPlanApprovedBy.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			dfLicenseFrom.setValue(obj1.getFieldValue());
			dfLicenseFrom.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			slIsLicenceForced.setValue(obj1.getFieldValue());
			slIsLicenceForced.setCaption(obj1.getFieldLabel());
			obj1 = list.get(5);
			slAllApprovalRecved.setValue(obj1.getFieldValue());
			//slAllApprovalRecved.setCaption(obj1.getFieldLabel());
			obj1 = list.get(6);
			slConstAsperAppPlan.setValue(obj1.getFieldValue());
			slConstAsperAppPlan.setCaption(obj1.getFieldLabel());
			obj1 = list.get(7);
			tfDynamicPlanApproval1.setValue(obj1.getFieldValue());
			tfDynamicPlanApproval1.setCaption(obj1.getFieldLabel());
			tfDynamicPlanApproval1.setVisible(true);
			obj1 = list.get(8);
			tfDynamicPlanApproval2.setValue(obj1.getFieldValue());
			tfDynamicPlanApproval2.setCaption(obj1.getFieldLabel());
			tfDynamicPlanApproval2.setVisible(true);
		} catch (Exception e) {
			
		}

	}
	private void editPlanApprovalDetailsNew() {
		try {
			List<TPemCmPropNewPlanApprvl> list = beanNewPlanApprvl.getPropNewPlanApprvlList(headerid);
			TPemCmPropNewPlanApprvl obj1 = list.get(0);
			tfLandBuildConst.setValue(obj1.getFieldValue());
			slLandBuildConst.setValue(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfBuildConst.setValue(obj1.getFieldValue());
			slBuildConst.setValue(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfPlanApproveConst.setValue(obj1.getFieldValue());
			tfPlanApproveConst.setCaption(obj1.getFieldLabel());
			obj1 = list.get(5);
			slAllApprovalRecvedConst.setValue(obj1.getFieldValue());
			slAllApprovalRecvedConst.setCaption(obj1.getFieldLabel());
			obj1 = list.get(6);
			slConstAsperAppPlanConst.setValue(obj1.getFieldValue());
			slConstAsperAppPlanConst.setCaption(obj1.getFieldLabel());
			obj1 = list.get(7);
			tfDynamicPlanApproval1.setValue(obj1.getFieldValue());
			tfDynamicPlanApproval1.setCaption(obj1.getFieldLabel());
			tfDynamicPlanApproval1.setVisible(true);
			obj1 = list.get(8);
			tfDynamicPlanApproval2.setValue(obj1.getFieldValue());
			tfDynamicPlanApproval2.setCaption(obj1.getFieldLabel());
			tfDynamicPlanApproval2.setVisible(true);
		} catch (Exception e) {
			
		}

	}
	void editBuildTechDetails(){
		try{
		List<TPemCmBldngTechDetails> list =beanTechDtls.getBldgTechDtlsList(headerid);
				
		TPemCmBldngTechDetails obj1 = list.get(0);
		slTypeBuilding.setCaption(obj1.getFieldLabel());
		slTypeBuilding.setValue(obj1.getFieldValue());
		obj1 = list.get(1);
		slBuildStructure.setValue(obj1.getFieldValue());
		slBuildStructure.setCaption(obj1.getFieldLabel());
		obj1 = list.get(2);
		tfYearConstructionBuild.setValue(obj1.getFieldValue());
		tfYearConstructionBuild.setCaption(obj1.getFieldLabel());
		obj1 = list.get(3);
		tfBuildNoFloors.setValue(obj1.getFieldValue());
		obj1 = list.get(4);
		slExterior.setValue(obj1.getFieldValue());
		slExterior.setCaption(obj1.getFieldLabel());
		obj1 = list.get(5);
		slInterior.setValue(obj1.getFieldValue());
		slInterior.setCaption(obj1.getFieldLabel());
		obj1 = list.get(6);
		tfLifeAge.setValue(obj1.getFieldValue());
		tfLifeAge.setCaption(obj1.getFieldLabel());
		obj1 = list.get(7);
		tfFutureLife.setValue(obj1.getFieldValue());
		tfFutureLife.setCaption(obj1.getFieldLabel());
		obj1 = list.get(8);
		tfDetailsPlan.setValue(obj1.getFieldValue());
		obj1 = list.get(9);
		slDeviation.setValue(obj1.getFieldValue());
		obj1 = list.get(10);
		tfDtlsDeviation.setValue(obj1.getFieldValue());
		obj1 = list.get(11);
		tfDynamicBuildval1.setValue(obj1.getFieldValue());
		tfDynamicBuildval1.setCaption(obj1.getFieldLabel());
		tfDynamicBuildval1.setVisible(true);
		obj1 = list.get(12);
		tfDynamicBuildval2.setValue(obj1.getFieldValue());
		tfDynamicBuildval2.setCaption(obj1.getFieldLabel());
		tfDynamicBuildval2.setVisible(true);

	} catch(Exception e) {

		
	}
		
}
void editRoofHeightDetails(){
	
	List<TPemCmBldngRoofHght> hghtList = beanRoofHt.getRoofHtDetailsList(headerid);
			
	layoutRoofHeight.removeAllComponents();
	layoutRoofHeight.addComponent(btnAddRoofHt);
	layoutRoofHeight.setComponentAlignment(btnAddRoofHt,
			Alignment.TOP_RIGHT);
	for (TPemCmBldngRoofHght obj : hghtList) {

		layoutRoofHeight
				.addComponent(new ComponentIterRoofHeight(obj.getFieldLabel(),obj.getFieldValue()));
	}
}
	
	void loadComponentListValues() {
		loadPropertyDescList();
		loadPropertyStructureList();
		loadTypeofProperty();
		loadCondtionList();
		loadBankBranchDetails();
		loadDetailPlan();
		loadOwnedorRented();
		loadHMPDetails();
		loadUSRDetails();
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
		
		slAppReasonable.addItem(Common.YES_DESC);
		slAppReasonable.addItem(Common.NO_DESC);
		
		slEarthQuake.addItem(Common.YES_DESC);
		slEarthQuake.addItem(Common.NO_DESC);
		
		slDeviation.addItem(Common.YES_DESC);
		slDeviation.addItem(Common.NO_DESC);
		
		slAllApprovalRecvedConst.addItem(Common.YES_DESC);
		slAllApprovalRecvedConst.addItem(Common.NO_DESC);
		
		slConstAsperAppPlanConst.addItem(Common.YES_DESC);
		slConstAsperAppPlanConst.addItem(Common.NO_DESC);
		
		slAllApprovalRecved.addItem(Common.YES_DESC);
		slAllApprovalRecved.addItem(Common.NO_DESC);
		
		slConstAsperAppPlan.addItem(Common.YES_DESC);
		slConstAsperAppPlan.addItem(Common.NO_DESC);
		
		slIsLicenceForced.addItem(Common.YES_DESC);
		slIsLicenceForced.addItem(Common.NO_DESC);
		
		slAllapproval.addItem(Common.YES_DESC);
		slAllapproval.addItem(Common.NO_DESC);
		
		slIsConstruction.addItem(Common.YES_DESC);
		slIsConstruction.addItem(Common.NO_DESC);
		
	}
	void loadOwnedorRented() {
		List<String> list = beanBankConst.getBankConstantList("OWNRENT",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slProOccupiedBy.setContainerDataSource(childAccounts);
	}
	void loadBankBranchDetails(){
		List<String> list = beanBankConst.getBankConstantList("BRANCH_CODE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfBankBranch.setContainerDataSource(childAccounts);
		tfSearchBankbranch.setContainerDataSource(childAccounts);
	}
	void loadStructureDetails(){
		List<String> list = beanBankConst.getBankConstantList("STRUCTURE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slTypeStructure.setContainerDataSource(childAccounts);
	}

	void loadFeasibiltyDetails(){
		List<String> list = beanBankConst.getBankConstantList("NMF",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slFeasibility.setContainerDataSource(childAccounts);
	}
	void loadRoadWidthDetails(){
		List<String> list = beanBankConst.getBankConstantList("ROAD WIDTH",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slRoadWidth.setContainerDataSource(childAccounts);
	}
	void loadRoadTypeDetails(){
		List<String> list = beanBankConst.getBankConstantList("ROAD",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slTypeRoad.setContainerDataSource(childAccounts);
	}
	void loadLandShapeDetails(){
		List<String> list = beanBankConst.getBankConstantList("ISR",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slLandShape.setContainerDataSource(childAccounts);
	}
	void loadAvailableDetails(){
		List<String> list = beanBankConst.getBankConstantList("AN",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slRoadFacility.setContainerDataSource(childAccounts);	
		slUnderSewerage.setContainerDataSource(childAccounts);	
		slPowerSupply.setContainerDataSource(childAccounts);
		slWaterPotential.setContainerDataSource(childAccounts);
	}
	void loadCornerInterDetails(){
		List<String> list = beanBankConst.getBankConstantList("CI",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slCornerInter.setContainerDataSource(childAccounts);
	}

	void loadLevelDetails(){
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
		slTypeProperty.setContainerDataSource(childAccounts);
		slTypeBuilding.setContainerDataSource(childAccounts);
	}

	void loadHMPDetails(){
		List<String> list = beanBankConst.getBankConstantList("HML",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slLocalClass.setContainerDataSource(childAccounts);
		slHighMiddPoor.setContainerDataSource(childAccounts);
	}
	void loadDevelopedDetails(){
		List<String> list = beanBankConst.getBankConstantList("DYW",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slSurroundDevelop.setContainerDataSource(childAccounts);
	}
	void loadUSRDetails(){
		List<String> list = beanBankConst.getBankConstantList("USR",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slUrbanSemiRural.setContainerDataSource(childAccounts);
	}
	

	void loadDetailPlan(){
		List<String> list = beanBankConst.getBankConstantList("DETAILS_PLAN",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slLandandBuilding.setContainerDataSource(childAccounts);
		slBuilding.setContainerDataSource(childAccounts);
		slLandBuildConst.setContainerDataSource(childAccounts);
		slBuildConst.setContainerDataSource(childAccounts);
	}
		
	void loadCondtionList() {
		List<String> list = beanBankConst.getBankConstantList("EGNP",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slExterior.setContainerDataSource(childAccounts);
		slInterior.setContainerDataSource(childAccounts);
	}

	void loadPropertyStructureList() {
		List<String> list = beanBankConst.getBankConstantList("STRUCTURE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slTypeStructure.setContainerDataSource(childAccounts);
		slBuildStructure.setContainerDataSource(childAccounts);
	}

	void resetAllFieldsFields() {
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

		tfDynamicEvaluation1.setVisible(false);
		tfDynamicEvaluation2.setVisible(false);

		tfDynamicCustomer1.setVisible(false);
		tfDynamicCustomer2.setVisible(false);
		tfCustomerName.setRequired(true);
	//	tfCustomerName.setValue("Sri.");
		slPropertyDesc.setValue(null);
		tfCustomerAddr.setValue("");
		tfPropertyAddress.setValue("");
		layoutOwnerDetails1.removeAllComponents();
		layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("", ""));
		slPropertyDesc.setInputPrompt(Common.SELECT_PROMPT);

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
				null,true,true,true));

		// for dimensions
		panelDimension.removeAllComponents();
		panelDimension.addComponent(btnAddDimension);
		panelDimension.setComponentAlignment(btnAddDimension,
				Alignment.BOTTOM_RIGHT);
		panelDimension.addComponent(new ComponentIterDimensionofPlot(null,true,true,true));
		
		//for buildspecification
		
		panelBuildSpecfication.removeAllComponents();
		panelBuildSpecfication.addComponent(btnAddBuildSpec);
		panelBuildSpecfication.setComponentAlignment(btnAddBuildSpec,
						Alignment.BOTTOM_RIGHT);
				panelBuildSpecfication.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));

	//for buildspecification new
			
				panelBuildSpecficationNew.removeAllComponents();
				panelBuildSpecficationNew.addComponent(btnAddBuildSpecNew);
				panelBuildSpecficationNew.setComponentAlignment(btnAddBuildSpecNew,
								Alignment.BOTTOM_RIGHT);
						panelBuildSpecficationNew.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));

						//for description of the property
						tfPostalAddress.setValue("");
						tfSiteNumber.setValue("");
						tfSFNumber.setValue("");
						tfNewSFNumber.setValue("");
						tfVillageLoc.setValue("");
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
						//for Charcteristics of the site
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
			
						tfTypeofwiring.setValue("");
						 tfClassFit.setValue("");
						 tfNOofLight.setValue("");
						 tfExhaustFan.setValue("");
						 tfFan.setValue("");
						 tfSpareplug.setValue("");
						 tfDynamicElectrical1.setValue("");
						 tfDynamicElectrical2.setValue("");
						 tfDynamicElectrical1.setVisible(false);
						 tfDynamicElectrical2.setVisible(false);

							
							 tfNoofClosets.setValue("");
							 tfNoofWashbin.setValue("");
							 tfWatertaps.setValue("");
							 tfAnyFixtures.setValue("");
							 tfDynamicPlum1.setValue("");
							 tfDynamicPlum2.setValue("");
							 tfDynamicPlum1.setVisible(false);
							 tfDynamicPlum2.setVisible(false);
		//valuation details
		layoutValuationDetails.removeAllComponents();
		layoutValuationDetails.addComponent(btnAddValDtls);
		layoutValuationDetails.setComponentAlignment(btnAddValDtls, Alignment.TOP_RIGHT);
		layoutValuationDetails.addComponent(layoutValuationDetails1);
		ValuationDetailsList obj=new ValuationDetailsList();
		ValuationDetailsList obj1=new ValuationDetailsList();
		obj.setFloorDtlsLabel("Ground Floor Building");
		obj1.setFloorDtlsLabel("First Floor Building");
		layoutValuationDetails2.addComponent(new ComponenetIterValuationDetails(obj));
		layoutValuationDetails2.addComponent(new ComponenetIterValuationDetails(obj1));
		// for ExtraItem
		layoutExtraItems.removeAllComponents();
		layoutExtraItems.addComponent(btnDynamicExtra);
		layoutExtraItems.setComponentAlignment(btnDynamicExtra,
				Alignment.TOP_RIGHT);
		layoutExtraItems.addComponent(new ComponentIterExtraItems(null,""));
// for AdditionalItem
		layoutAdditionItem.removeAllComponents();
		layoutAdditionItem.addComponent(btnDynamicAdditional);
		layoutAdditionItem.setComponentAlignment(btnDynamicAdditional,
				Alignment.TOP_RIGHT);
		layoutAdditionItem.addComponent(new ComponentIterAdditionalItems(null,""));
// for Miscellaneous
		layoutMiscellaneous.removeAllComponents();
		layoutMiscellaneous.addComponent(btnDynamicMiscell);
		layoutMiscellaneous.setComponentAlignment(btnDynamicMiscell,
				Alignment.TOP_RIGHT);
		layoutMiscellaneous.addComponent(new ComponentIterMiscellaneous(null,""));
// for Services
		layoutServices.removeAllComponents();
		layoutServices.addComponent(btnDynamicServices);
		layoutServices.setComponentAlignment(btnDynamicServices,
				Alignment.TOP_RIGHT);
		layoutServices.addComponent(new ComponentIerServices(null,""));
		//for construction
		tfStageofConst.setValue("");
		tfSalablity.setValue("");
		tfRentalValues.setValue("");
		tfIncome.setValue("");
		tfDynamicConstruction1.setValue("");
		tfDynamicConstruction2.setValue("");
		tfDynamicConstruction1.setVisible(false);
		tfDynamicConstruction2.setVisible(false);
		
		//for violation
		tfAnyViolation.setValue("");
		tfDynamicViolation1.setValue("");
		tfDynamicViolation2.setValue("");
		tfDynamicViolation1.setVisible(false);
		tfDynamicViolation2.setVisible(false);
		
		//earth quake
		slEarthQuake.setValue(null);
		tfDynamicEarthquake1.setValue("");
		tfDynamicEarthquake2.setValue("");
		tfDynamicEarthquake1.setVisible(false);
		tfDynamicEarthquake2.setVisible(false);
		
		// Cost of Construction
				tfCostConstruction.setValue("");
				tfDynamicCostConst1.setValue("");
				tfDynamicCostConst2.setValue("");
				tfDynamicCostConst3.setValue("");
				tfDynamicCostConst4.setValue("");
				tfDynamicCostConst5.setValue("");
				tfDynamicCostConst6.setValue("");
				tfDynamicCostConst7.setValue("");
				tfDynamicCostConst8.setValue("");
				tfDynamicCostConst9.setValue("");
				tfDynamicCostConst10.setValue("");
				layoutCostConstruction1.setVisible(false);
				layoutCostConstruction2.setVisible(false);

		tfAreaofLand.setValue("0");
		tfNorthandSouth.setValue("");
		tfMarketRate.setValue("");
		tfGuiderate.setValue("");
		tfAdopetdMarketRate.setValue("");
		tfFairMarketRate.setValue("0");
		tfRealizabletRate.setValue("0");
		tfDistressRate.setValue("0");
		tfGuidelineRate.setValue("0");
		tfDynamicValuation1.setValue("");
		tfDynamicValuation2.setValue("");
		tfDynamicValuation1.setVisible(false);
		tfDynamicValuation2.setVisible(false);
		
		slTypeProperty.setValue(null);
		slTypeStructure.setValue(null);
		tfYearConstruction.setValue("");
		tfNoFloors.setValue("");
		tfConstQuality.setValue("");
		slAllapproval.setValue("");
		slIsConstruction.setValue("");
		tfReason.setValue("");
		tfDynamicConstval1.setValue("");
		tfDynamicConstval2.setValue("");
		tfDynamicConstval1.setVisible(false);
		tfDynamicConstval2.setVisible(false);
		slTypeProperty.setInputPrompt(Common.SELECT_PROMPT);
		slTypeStructure.setInputPrompt(Common.SELECT_PROMPT);
		
		//plinth Area
		layoutPlintharea.removeAllComponents();
		layoutPlintharea.addComponent(btnAddPlinth);
		layoutPlintharea.setComponentAlignment(btnAddPlinth,
				Alignment.BOTTOM_RIGHT);
		layoutPlintharea.addComponent(new ComponentIterPlinthArea("Ground Floor","",""));
		layoutPlintharea.addComponent(new ComponentIterPlinthArea("Portico and Stair","",""));
		//plinth Area construction
		vlPlinthAreaNew.removeAllComponents();
		vlPlinthAreaNew.addComponent(btnAddPlinthNew);
		vlPlinthAreaNew.setComponentAlignment(btnAddPlinthNew,
				Alignment.BOTTOM_RIGHT);
		vlPlinthAreaNew.addComponent(new ComponentIterPlinthArea("Ground Floor","",""));
		vlPlinthAreaNew.addComponent(new ComponentIterPlinthArea("Portico and Stair","",""));
		
		//Roof ht
		layoutRoofHeight.removeAllComponents();
		layoutRoofHeight.addComponent(btnAddRoofHt);
		layoutRoofHeight.setComponentAlignment(btnAddRoofHt,
								Alignment.TOP_RIGHT);
		layoutRoofHeight.addComponent(new ComponentIterRoofHeight("Ground Floor",""));
		layoutRoofHeight.addComponent(new ComponentIterRoofHeight("First Floor",""));
		
		// Applicant Estimate
				tfAppEstimate.setValue("");
				tfDtlsAppEstimate.setValue("");
				tfDetails1.setValue("");
				tfDetails2.setValue("");
				tfDetails3.setValue("");
				tfDetailVal1.setValue("");
				tfDetailVal2.setValue("");
				tfDetailVal3.setValue("");
				tfDynamicAppEstimate1.setValue("");
				tfDynamicAppEstimate2.setValue("");
				tfDynamicAppEstimate3.setValue("");
				tfDynamicAppEstimate4.setValue("");
				tfDynamicAppEstimate5.setValue("");
				tfDynamicAppEstimate6.setValue("");
				tfDynamicAppEstimate7.setValue("");
				tfDynamicAppEstimate8.setValue("");
				layoutApplicantEstimate2.setVisible(false);
				layoutApplicantEstimate3.setVisible(false);
				tfTotalval.setValue("");
				tfDynamicAppEstimate2.setValue("0.00");
				tfDetailVal2.setValue("0.00");
				tfDetailVal1.setValue("0.00");
				tfDynamicAppEstimate4.setValue("0.00");
				tfDynamicAppEstimate6.setValue("0.00");
				tfDynamicAppEstimate8.setValue("0.00");
				tfDetailVal3.setValue("0.00");

		
		//ApplicantReasonable
		slAppReasonable.setValue(Common.YES_DESC);
		tfReasonEstimateVal.setValue("");
		tfDtlsAppReasonable.setValue("");
		tfDetailsReason1.setValue("");
		tfDetailsReason2.setValue("");
		tfDetailsReason3.setValue("");
		tfDetailReasonVal1.setValue("");
		tfDetailReasonVal2.setValue("");
		tfDetailReasonVal3.setValue("");
		tfDynamicAppReason1.setValue("");
		tfDynamicAppReason2.setValue("");
		tfDynamicAppReason3.setValue("");
		tfDynamicAppReason4.setValue("");
		tfReasonTotalval.setValue("");
		tfDynamicAppReason1.setVisible(false);
		tfDynamicAppReason2.setVisible(false);
		tfDynamicAppReason3.setVisible(false);
		tfDynamicAppReason4.setVisible(false);
		tfDetailReasonVal1.setValue("0.00");
		tfDetailReasonVal2.setValue("0.00");
		tfDetailReasonVal3.setValue("0.00");
		
		// details of plan approval
		slBuilding.setValue(null);
		slLandandBuilding.setValue(null);
		tfLandandBuilding.setValue("");
		tfBuilding.setValue("");
		tfPlanApprovedBy.setValue("");
		dfLicenseFrom.setValue("");
		tfDynamicPlanApproval1.setValue("");
		slIsLicenceForced.setValue(null);
		slAllApprovalRecved.setValue(null);
		slConstAsperAppPlan.setValue(null);
		tfDynamicPlanApproval2.setValue("");
		tfDynamicPlanApproval1.setVisible(false);
		tfDynamicPlanApproval2.setVisible(false);
		
		slBuilding.setInputPrompt(Common.SELECT_PROMPT);
		slLandandBuilding.setInputPrompt(Common.SELECT_PROMPT);
		//details of plan approval construction
		slLandBuildConst.setValue(null);
		tfLandBuildConst.setValue("");
		slBuildConst.setValue(null);
		tfBuildConst.setValue("");
		tfPlanApproveConst.setValue("");
		slAllApprovalRecvedConst.setValue(null);
		slConstAsperAppPlanConst.setValue(null);
		tfDynamicConstPlanApp1.setValue("");
		tfDynamicConstPlanApp2.setValue("");
		
		slBuildConst.setInputPrompt(Common.SELECT_PROMPT);
		slLandBuildConst.setInputPrompt(Common.SELECT_PROMPT);
		tfOwnerName.setValue("");
		tfOwnerAddress.setValue("");
		tfLandMark.setValue("");
		tfPropertyAddress.setValue("");
		tfDynamicAsset1.setValue("");
		tfDynamicAsset2.setValue("");
		tfDynamicAsset1.setVisible(false);
		tfDynamicAsset2.setVisible(false);

		//plinth Area
				layoutGuideline.removeAllComponents();
				layoutGuideline.addComponent(btnAddGuideline);
				layoutGuideline.setComponentAlignment(btnAddGuideline,
								Alignment.BOTTOM_RIGHT);
				layoutGuideline.addComponent(new ComponentIterGuideline("Land","","",""));
				layoutGuideline.addComponent(new ComponentIterGuideline("Building","","",""));
		
				//build Details
				slTypeBuilding.setValue("");
				slBuildStructure.setValue("");
				tfYearConstructionBuild.setValue("");
				tfBuildNoFloors.setValue("");
				slExterior.setValue("");
				slInterior.setValue("");
				tfLifeAge.setValue("");
				tfFutureLife.setValue("");
				tfDetailsPlan.setValue("");
				slDeviation.setValue("");
				tfDtlsDeviation.setValue("");
				tfDynamicBuildval1.setValue("");
				tfDynamicBuildval2.setValue("");
				tfDynamicBuildval1.setVisible(false);
				tfDynamicBuildval2.setVisible(false);
				slTypeBuilding.setInputPrompt(Common.SELECT_PROMPT);
				slBuildStructure.setInputPrompt(Common.SELECT_PROMPT);
				slExterior.setInputPrompt(Common.SELECT_PROMPT);
				slInterior.setInputPrompt(Common.SELECT_PROMPT);
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
		tfCoverUnderStatCentral.setValue(Common.strNA);
		tfAnyConversionLand.setValue(Common.strNA);
		tfMonthlyRent.setValue(Common.strNA);
		tfElecServiceConnNo.setValue(Common.strNA);
		tfProTaxReceipt.setValue(Common.strNA);
		tfFlood.setValue(Common.strNil);
		tfGeneralRemarks.setValue(Common.strNil);
		tfStageofConst.setValue(Common.strNA);
		accordion.setSelectedTab(0);
		
		slAppReasonable.setValue(Common.YES_DESC);
		slEarthQuake.setValue(Common.YES_DESC);;
		slDeviation.setValue(Common.YES_DESC);
		slAllApprovalRecved.setValue(Common.YES_DESC);
		slConstAsperAppPlan.setValue(Common.YES_DESC);
		slIsLicenceForced.setValue(Common.YES_DESC);
		slAllapproval.setValue(Common.YES_DESC);
		slIsConstruction.setValue(Common.YES_DESC);
		

	}

	@SuppressWarnings("deprecation")
	void setComponentStyle() {
		tfSearchBankbranch.setNullSelectionAllowed(false);
		tfSearchBankbranch.setInputPrompt(Common.SELECT_PROMPT);
		tfSearchCustomer.setInputPrompt("Enter Customer");
		tfSearchEvalNumber.setInputPrompt("Enter Evaluation Number");
		
		tfEvaluationNumber.setWidth(strComponentWidth);
		tfBankBranch.setWidth(strComponentWidth);
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
		
		dfDateofValuation.setResolution(PopupDateField.RESOLUTION_DAY);
		dfDateofValuation.setDateFormat("dd-MMM-yyyy");

		dfVerifiedDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dfVerifiedDate.setDateFormat("dd-MMM-yyyy");

		tfCustomerName.setWidth(strComponentWidth);
		slPropertyDesc.setWidth(strComponentWidth);
		tfCustomerAddr.setWidth(strComponentWidth);
		tfCustomerAddr.setHeight("130px");
		slPropertyDesc.setNullSelectionAllowed(false);

		//for description of the property
				tfPostalAddress.setWidth(strComponentWidth);
				tfSiteNumber.setWidth(strComponentWidth);
				tfSFNumber.setWidth(strComponentWidth);
				tfNewSFNumber.setWidth(strComponentWidth);
				tfVillageLoc.setWidth(strComponentWidth);
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
				//for Charcteristics of the site
				slLocalClass.setWidth(strComponentWidth);
				slSurroundDevelop.setWidth(strComponentWidth);
				tfFlood.setWidth(strComponentWidth);
				slFeasibility.setWidth(strComponentWidth);
				slLandLevel.setWidth(strComponentWidth);
				slLandShape.setWidth(strComponentWidth);
				slTypeUse.setWidth(strComponentWidth);
				tfUsageRestriction.setWidth(strComponentWidth);
				slIsPlot.setWidth(strComponentWidth);
				tfApprveNo.setWidth(strComponentWidth);
				tfNoReason.setWidth(strComponentWidth);
				tfSubdivide.setWidth(strComponentWidth);
				slDrawApproval.setWidth(strComponentWidth);
				slCornerInter.setWidth(strComponentWidth);
				slRoadFacility.setWidth(strComponentWidth);
				slTypeRoad.setWidth(strComponentWidth);
				slRoadWidth.setWidth(strComponentWidth);
				slLandLock.setWidth(strComponentWidth);
				slWaterPotential.setWidth(strComponentWidth);
				slUnderSewerage.setWidth(strComponentWidth);
				slPowerSupply.setWidth(strComponentWidth);
				tfAdvantageSite.setWidth(strComponentWidth);
				tfDisadvantageSite.setWidth(strComponentWidth);
				tfGeneralRemarks.setWidth(strComponentWidth);
				tfDynamicCharacter1.setWidth(strComponentWidth);
				tfDynamicCharacter2.setWidth(strComponentWidth);
				//for applicant Estimate
				 tfNoofClosets.setWidth(strComponentWidth);
				 tfNoofWashbin.setWidth(strComponentWidth);
				 tfWatertaps.setWidth(strComponentWidth);
				 tfAnyFixtures.setWidth(strComponentWidth);
				 tfDynamicPlum1.setWidth(strComponentWidth);
				 tfDynamicPlum2.setWidth(strComponentWidth);

						//for applicant Reasonable
						 tfTypeofwiring.setWidth(strComponentWidth);
						 tfClassFit.setWidth(strComponentWidth);
						 tfNOofLight.setWidth(strComponentWidth);
						 tfExhaustFan.setWidth(strComponentWidth);
						 tfFan.setWidth(strComponentWidth);
						 tfSpareplug.setWidth(strComponentWidth);
						 tfDynamicElectrical1.setWidth(strComponentWidth);
						 tfDynamicElectrical2.setWidth(strComponentWidth);

							//stage of construction
							tfSalablity.setWidth(strComponentWidth);
							tfRentalValues.setWidth(strComponentWidth);
							tfIncome.setWidth(strComponentWidth);
							tfStageofConst.setWidth(strComponentWidth);
							tfDynamicConstruction1.setWidth(strComponentWidth);
							tfDynamicConstruction2.setWidth(strComponentWidth);
		
		//for violation
		tfAnyViolation.setWidth(strComponentWidth);
		tfDynamicViolation1.setWidth(strComponentWidth);
		tfDynamicViolation2.setWidth(strComponentWidth);
		
		// for cost of construction
				lblCostConst.setWidth(strEstimateWidth);
				tfCostConstruction.setWidth(strComponentWidth);
				tfDynamicCostConst1.setWidth(strEstimateWidth);
				tfDynamicCostConst2.setWidth(strComponentWidth);
				tfDynamicCostConst3.setWidth(strEstimateWidth);
				tfDynamicCostConst4.setWidth(strComponentWidth);
				tfDynamicCostConst5.setWidth(strEstimateWidth);
				tfDynamicCostConst6.setWidth(strComponentWidth);
				tfDynamicCostConst7.setWidth(strEstimateWidth);
				tfDynamicCostConst8.setWidth(strComponentWidth);
				tfDynamicCostConst9.setWidth(strEstimateWidth);
				tfDynamicCostConst10.setWidth(strComponentWidth);
		
		//for construction details
		slTypeProperty.setWidth(strComponentWidth);
		slTypeStructure.setWidth(strComponentWidth);
		tfYearConstruction.setWidth(strComponentWidth);
		tfNoFloors.setWidth(strComponentWidth);
		tfConstQuality.setWidth(strComponentWidth);
		slAllapproval.setWidth(strComponentWidth);
		slIsConstruction.setWidth(strComponentWidth);
		tfReason.setWidth(strComponentWidth);
		tfDynamicConstval1.setWidth(strComponentWidth);
		tfDynamicConstval2.setWidth(strComponentWidth);
		
		//for land valuation
		tfAreaofLand.setWidth(strComponentWidth);
		tfNorthandSouth.setWidth(strComponentWidth);
		tfMarketRate.setWidth(strComponentWidth);
		tfAdopetdMarketRate.setWidth(strComponentWidth);
		tfGuiderate.setWidth(strComponentWidth);
		tfFairMarketRate.setWidth(strComponentWidth);
		tfRealizabletRate.setWidth(strComponentWidth);
		tfDistressRate.setWidth(strComponentWidth);
		tfGuidelineRate.setWidth(strComponentWidth);
		tfDynamicValuation1.setWidth(strComponentWidth);
		tfDynamicValuation2.setWidth(strComponentWidth);
		
		// for applicant Estimate
		lblAppEstimate.setWidth(strEstimateWidth);
		tfAppEstimate.setWidth(strComponentWidth);
		lblDtlsAppEstimate.setWidth(strEstimateWidth);
		tfDtlsAppEstimate.setWidth(strComponentWidth);
		tfDetails1.setWidth(strEstimateWidth);
		tfDetails2.setWidth(strEstimateWidth);
		tfDetails3.setWidth(strEstimateWidth);
		tfDetailVal1.setWidth(strComponentWidth);
		tfDetailVal2.setWidth(strComponentWidth);
		tfDetailVal3.setWidth(strComponentWidth);
		lblTotal.setWidth(strEstimateWidth);
		tfTotalval.setWidth(strComponentWidth);
		tfDynamicAppEstimate1.setWidth(strEstimateWidth);
		tfDynamicAppEstimate2.setWidth(strComponentWidth);
		tfDynamicAppEstimate3.setWidth(strEstimateWidth);
		tfDynamicAppEstimate4.setWidth(strComponentWidth);
		tfDynamicAppEstimate5.setWidth(strEstimateWidth);
		tfDynamicAppEstimate6.setWidth(strComponentWidth);
		tfDynamicAppEstimate7.setWidth(strEstimateWidth);
		tfDynamicAppEstimate8.setWidth(strComponentWidth);
		//for applicant Reasonable
		lblAppReasonable.setWidth(strEstimateWidth);
		slAppReasonable.setWidth(strComponentWidth);
		lblReasonEstimate.setWidth(strEstimateWidth);
		tfReasonEstimateVal.setWidth(strComponentWidth);
		lblDtlsAppReasonable.setWidth(strEstimateWidth);
		tfDtlsAppReasonable.setWidth(strComponentWidth);
		tfDetailsReason1.setWidth(strEstimateWidth);
		tfDetailsReason2.setWidth(strEstimateWidth);
		tfDetailsReason3.setWidth(strEstimateWidth);
		tfDetailReasonVal1.setWidth(strComponentWidth);
		tfDetailReasonVal2.setWidth(strComponentWidth);
		tfDetailReasonVal3.setWidth(strComponentWidth);
		lblReasonTotal.setWidth(strEstimateWidth);
		tfReasonTotalval.setWidth(strComponentWidth);
		tfDynamicAppReason1.setWidth(strEstimateWidth);
		tfDynamicAppReason2.setWidth(strComponentWidth);
		tfDynamicAppReason3.setWidth(strEstimateWidth);
		tfDynamicAppReason4.setWidth(strComponentWidth);
		
		//for earth quake
		slEarthQuake.setWidth(strComponentWidth);
		tfDynamicEarthquake1.setWidth(strComponentWidth);
		tfDynamicEarthquake2.setWidth(strComponentWidth);
		
		tfOwnerName.setWidth(strComponentWidth);
		tfOwnerAddress.setWidth(strComponentWidth);
		tfOwnerAddress.setHeight("150px");
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
		// for details of plan approval
		slLandandBuilding.setHeight("25");
		slBuilding.setHeight("25");
		tfBuilding.setHeight("25");
		tfLandandBuilding.setHeight("25");
		tfLandandBuilding.setWidth(strComponentWidth);
		tfBuilding.setWidth(strComponentWidth);
		slLandandBuilding.setWidth(strComponentWidth);
		slBuilding.setWidth(strComponentWidth);
		tfPlanApprovedBy.setWidth(strComponentWidth);
		dfLicenseFrom.setWidth(strComponentWidth);
		tfDynamicPlanApproval1.setWidth(strComponentWidth);
		slIsLicenceForced.setWidth(strComponentWidth);
		slAllApprovalRecved.setWidth(strComponentWidth);
		slConstAsperAppPlan.setWidth(strComponentWidth);
		tfDynamicPlanApproval2.setWidth(strComponentWidth);
		//for detsails of plan approval construction
		slLandBuildConst.setHeight("25");
		tfLandBuildConst.setHeight("25");
		slBuildConst.setHeight("25");
		tfBuildConst.setHeight("25");
		slLandBuildConst.setWidth(strComponentWidth);
		tfLandBuildConst.setWidth(strComponentWidth);
		slBuildConst.setWidth(strComponentWidth);
		tfBuildConst.setWidth(strComponentWidth);
		tfPlanApproveConst.setWidth(strComponentWidth);
		slAllApprovalRecvedConst.setWidth(strComponentWidth);
		slConstAsperAppPlanConst.setWidth(strComponentWidth);
		tfDynamicConstPlanApp1.setWidth(strComponentWidth);
		tfDynamicConstPlanApp2.setWidth(strComponentWidth);

		//for Build details
				slTypeBuilding.setWidth(strComponentWidth);
				slBuildStructure.setWidth(strComponentWidth);
				tfYearConstructionBuild.setWidth(strComponentWidth);
				tfBuildNoFloors.setWidth(strComponentWidth);
				slExterior.setWidth(strComponentWidth);
				slInterior.setWidth(strComponentWidth);
				tfLifeAge.setWidth(strComponentWidth);
				tfFutureLife.setWidth(strComponentWidth);
				tfDetailsPlan.setWidth(strComponentWidth);
				slDeviation.setWidth(strComponentWidth);
				tfDtlsDeviation.setWidth(strComponentWidth);
				tfDynamicBuildval1.setWidth(strComponentWidth);
				tfDynamicBuildval2.setWidth(strComponentWidth);
	//Details of valuation
				lblParticular.setWidth(strWidth);
				lblPlinthArea.setWidth(strLblWidth);
				lblRoofHt.setWidth(strLblWidth);
				lblBuildAge.setWidth(strWidth);
				lblRate.setWidth(strWidth);
				lblReplace.setWidth(strWidth);
				lblDepreciation.setWidth(strWidth);
				lblNetvalue.setWidth(strWidth);
		//set Null representation
		tfEvaluationNumber.setNullRepresentation("");
		tfBankBranch.setNullSelectionAllowed(false);
		tfEvaluationPurpose.setNullRepresentation("");
		tfValuatedBy.setNullRepresentation("");
		tfVerifiedBy.setNullRepresentation("");
		tfDynamicEvaluation1.setNullRepresentation("");
		tfDynamicEvaluation2.setNullRepresentation("");
		
		tfCustomerName.setNullRepresentation("");
		slPropertyDesc.setNullSelectionAllowed(false);
		tfCustomerAddr.setNullRepresentation("");
		tfCustomerAddr.setHeight("130px");
		slPropertyDesc.setNullSelectionAllowed(false);
		tfPostalAddress.setNullRepresentation("");
		tfSiteNumber.setNullRepresentation("");
		tfSFNumber.setNullRepresentation("");
		tfNewSFNumber.setNullRepresentation("");
		tfVillageLoc.setNullRepresentation("");
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
		//for Charcteristics of the site
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
		
		 tfTypeofwiring.setNullRepresentation("");
		 tfClassFit.setNullRepresentation("");
		 tfNOofLight.setNullRepresentation("");
		 tfExhaustFan.setNullRepresentation("");
		 tfFan.setNullRepresentation("");
		 tfSpareplug.setNullRepresentation("");
		 tfDynamicElectrical1.setNullRepresentation("");
		 tfDynamicElectrical2.setNullRepresentation("");

			
			 tfNoofClosets.setNullRepresentation("");
			 tfNoofWashbin.setNullRepresentation("");
			 tfWatertaps.setNullRepresentation("");
			 tfAnyFixtures.setNullRepresentation("");
			 tfDynamicPlum1.setNullRepresentation("");
			 tfDynamicPlum2.setNullRepresentation("");
				

		//for construction
			 tfStageofConst.setNullRepresentation("");
				tfSalablity.setNullRepresentation("");
				tfRentalValues.setNullRepresentation("");
				tfIncome.setNullRepresentation("");
				tfDynamicConstruction1.setNullRepresentation("");
				tfDynamicConstruction2.setNullRepresentation("");

		
		//for violation
		tfAnyViolation.setNullRepresentation("");
		tfDynamicViolation1.setNullRepresentation("");
		tfDynamicViolation2.setNullRepresentation("");
		
		// for cost of construction
				tfCostConstruction.setNullRepresentation("");
				tfDynamicCostConst1.setNullRepresentation("");
				tfDynamicCostConst2.setNullRepresentation("");
				tfDynamicCostConst3.setNullRepresentation("");
				tfDynamicCostConst4.setNullRepresentation("");
				tfDynamicCostConst5.setNullRepresentation("");
				tfDynamicCostConst6.setNullRepresentation("");
				tfDynamicCostConst7.setNullRepresentation("");
				tfDynamicCostConst8.setNullRepresentation("");
				tfDynamicCostConst9.setNullRepresentation("");
				tfDynamicCostConst10.setNullRepresentation("");

		
		//for construction details
		slTypeProperty.setNullSelectionAllowed(false);
		slTypeStructure.setNullSelectionAllowed(false);
		tfYearConstruction.setNullRepresentation("");
		tfNoFloors.setNullRepresentation("");
		tfConstQuality.setNullRepresentation("");
		slAllapproval.setNullSelectionAllowed(false);
		slIsConstruction.setNullSelectionAllowed(false);
		tfReason.setNullRepresentation("");
		tfDynamicConstval1.setNullRepresentation("");
		tfDynamicConstval2.setNullRepresentation("");
		
		//for land valuation
		tfAreaofLand.setNullRepresentation("");
		tfNorthandSouth.setNullRepresentation("");
		tfMarketRate.setNullRepresentation("");
		tfGuiderate.setNullRepresentation("");
		tfAdopetdMarketRate.setNullRepresentation("");
		tfFairMarketRate.setNullRepresentation("");
		tfRealizabletRate.setNullRepresentation("");
		tfDistressRate.setNullRepresentation("");
		tfGuidelineRate.setNullRepresentation("");
		tfDynamicValuation1.setNullRepresentation("");
		tfDynamicValuation2.setNullRepresentation("");
		
		// for applicant Estimate
		tfAppEstimate.setNullRepresentation("");
		tfDtlsAppEstimate.setNullRepresentation("");
		tfDetails1.setNullRepresentation("");
		tfDetails2.setNullRepresentation("");
		tfDetails3.setNullRepresentation("");
		tfDetailVal1.setNullRepresentation("");
		tfDetailVal2.setNullRepresentation("");
		tfDetailVal3.setNullRepresentation("");
		tfTotalval.setNullRepresentation("");
		tfDynamicAppEstimate1.setNullRepresentation("");
		tfDynamicAppEstimate2.setNullRepresentation("");
		tfDynamicAppEstimate3.setNullRepresentation("");
		tfDynamicAppEstimate4.setNullRepresentation("");
		tfDynamicAppEstimate5.setNullRepresentation("");
		tfDynamicAppEstimate6.setNullRepresentation("");
		tfDynamicAppEstimate7.setNullRepresentation("");
		tfDynamicAppEstimate8.setNullRepresentation("");
		
		//for applicant Reasonable
		lblAppReasonable.setWidth(strEstimateWidth);
		slAppReasonable.setNullSelectionAllowed(false);
		tfReasonEstimateVal.setNullRepresentation("");
		tfDtlsAppReasonable.setNullRepresentation("");
		tfDetailsReason1.setNullRepresentation("");
		tfDetailsReason2.setNullRepresentation("");
		tfDetailsReason3.setNullRepresentation("");
		tfDetailReasonVal1.setNullRepresentation("");
		tfDetailReasonVal2.setNullRepresentation("");
		tfDetailReasonVal3.setNullRepresentation("");
		tfReasonTotalval.setNullRepresentation("");
		tfDynamicAppReason1.setNullRepresentation("");
		tfDynamicAppReason2.setNullRepresentation("");
		tfDynamicAppReason3.setWidth(strEstimateWidth);
		tfDynamicAppReason4.setNullRepresentation("");
		
		//for earth quake
		slEarthQuake.setNullSelectionAllowed(false);
		tfDynamicEarthquake1.setNullRepresentation("");
		tfDynamicEarthquake2.setNullRepresentation("");
		
		tfOwnerName.setNullRepresentation("");
		tfOwnerAddress.setNullRepresentation("");
		tfLandMark.setNullRepresentation("");
		tfPropertyAddress.setNullRepresentation("");
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

		// for details of plan approval
		slLandandBuilding.setNullSelectionAllowed(false);
		slBuilding.setNullSelectionAllowed(false);
		tfLandandBuilding.setNullRepresentation("");
		tfBuilding.setNullRepresentation("");
		tfPlanApprovedBy.setNullRepresentation("");
		tfDynamicPlanApproval1.setNullRepresentation("");
		slIsLicenceForced.setNullSelectionAllowed(false);
		slAllApprovalRecved.setNullSelectionAllowed(false);
		slConstAsperAppPlan.setNullSelectionAllowed(false);
		tfDynamicPlanApproval2.setNullRepresentation("");
		dfLicenseFrom.setNullRepresentation("");
		//for details of plan approval construction
		slLandBuildConst.setNullSelectionAllowed(false);
		tfLandBuildConst.setNullRepresentation("");
		slBuildConst.setNullSelectionAllowed(false);
		tfBuildConst.setNullRepresentation("");
		tfPlanApproveConst.setNullRepresentation("");
		slAllApprovalRecvedConst.setNullSelectionAllowed(false);
		slConstAsperAppPlanConst.setNullSelectionAllowed(false);
		tfDynamicConstPlanApp1.setNullRepresentation("");
		tfDynamicConstPlanApp2.setNullRepresentation("");
		//for Build details
		slTypeBuilding.setNullSelectionAllowed(false);
		slBuildStructure.setNullSelectionAllowed(false);
		tfYearConstructionBuild.setNullRepresentation("");
		tfBuildNoFloors.setNullRepresentation("");
		slExterior.setNullSelectionAllowed(false);
		slInterior.setNullSelectionAllowed(false);
		tfLifeAge.setNullRepresentation("");
		tfFutureLife.setNullRepresentation("");
		tfDetailsPlan.setNullRepresentation("");
		slDeviation.setNullSelectionAllowed(false);
		tfDtlsDeviation.setNullRepresentation("");
		tfDynamicBuildval1.setNullRepresentation("");
		tfDynamicBuildval2.setNullRepresentation("");

		// for dynamic
		btnDynamicEvaluation1.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicEvaluation1.setStyleName(Runo.BUTTON_LINK);
		btnDynamicCustomer
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicCustomer.setStyleName(Runo.BUTTON_LINK);
		btnDynamicAsset.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicAsset.setStyleName(Runo.BUTTON_LINK);
		btnDynamicBuildVal.setStyleName(Runo.BUTTON_LINK);
		btnDynamicBuildVal.setIcon(new 
				ThemeResource(Common.strAddIcon));
		
		btnAddLegalDoc.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddNorDoc.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddNorDoc.setStyleName(Runo.BUTTON_LINK);
		btnAddLegalDoc.setStyleName(Runo.BUTTON_LINK);
		btnAddAdjoinProperty.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnAddAdjoinProperty.setStyleName(Runo.BUTTON_LINK);
		btnAddDimension.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddDimension.setStyleName(Runo.BUTTON_LINK);
		
		btnAddValDtls.setStyleName(Runo.BUTTON_LINK);
		btnAddValDtls.setIcon(new ThemeResource(Common.strAddIcon));
		
		btnDynamicExtra.setStyleName(Runo.BUTTON_LINK);
		btnDynamicExtra.setIcon(new ThemeResource(Common.strAddIcon));
		
		btnDynamicAdditional.setStyleName(Runo.BUTTON_LINK);
		btnDynamicAdditional.setIcon(new ThemeResource(Common.strAddIcon));
		
		btnDynamicMiscell.setStyleName(Runo.BUTTON_LINK);
		btnDynamicMiscell.setIcon(new ThemeResource(Common.strAddIcon));
		
		btnDynamicServices.setStyleName(Runo.BUTTON_LINK);
		btnDynamicServices.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddPlinth.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddPlinth.setStyleName(Runo.BUTTON_LINK);
		btnAddPlinthNew.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddPlinthNew.setStyleName(Runo.BUTTON_LINK);

		btnAddRoofHt.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddRoofHt.setStyleName(Runo.BUTTON_LINK);
		
		btnDynamicDescProp.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicDescProp.setStyleName(Runo.BUTTON_LINK);
		btnDynamicCharacter.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicCharacter.setStyleName(Runo.BUTTON_LINK);
		
		btnDynamicConstruction.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicConstruction.setStyleName(Runo.BUTTON_LINK);
		btnDynamicViolation.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicViolation.setStyleName(Runo.BUTTON_LINK);
		btnDynamicConstVal.setStyleName(Runo.BUTTON_LINK);
		btnDynamicConstVal.setIcon(new 
				ThemeResource(Common.strAddIcon));
		btnDynamicPlanApproval.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicPlanApproval.setStyleName(Runo.BUTTON_LINK);
		btnDynamicConstPlan.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicConstPlan.setStyleName(Runo.BUTTON_LINK);
		btnAddBuildSpec.setStyleName(Runo.BUTTON_LINK);
		btnAddBuildSpec.setIcon(new ThemeResource(Common.strAddIcon));
		
		btnAddBuildSpecNew.setStyleName(Runo.BUTTON_LINK);
		btnAddBuildSpecNew.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicAppEstimate.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicAppEstimate.setStyleName(Runo.BUTTON_LINK);
		
		btnDynamicAppReason.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicAppReason.setStyleName(Runo.BUTTON_LINK);
		
		btnDynamicEarthQuake.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicEarthQuake.setStyleName(Runo.BUTTON_LINK);
		
		btnDynamicCostConst.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicCostConst.setStyleName(Runo.BUTTON_LINK);
		
		btnAddOwner.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnAddOwner.setStyleName(Runo.BUTTON_LINK);
		
		btnDynamicValuation.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicValuation.setStyleName(Runo.BUTTON_LINK);
		
		btnAddGuideline.setStyleName(Runo.BUTTON_LINK);
		btnAddGuideline.setIcon(new ThemeResource(Common.strAddIcon));
		
		
		tfStageofConst.setValue(Common.strNA);
		
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

	public void buttonClick(ClickEvent event) {
		notifications.close();
		if (btnAddNorDoc == event.getButton()) {

			panelNormalDocumentDetails
					.addComponent(new ComponentIteratorNormlDoc(null, null, "",
							""));
		}
		if (btnAddOwner == event.getButton()) {

			layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("", ""));
		}
		if (btnAddPlinth == event.getButton()) {

			layoutPlintharea
					.addComponent(new ComponentIterPlinthArea("","", ""));
		}
		if (btnAddPlinthNew == event.getButton()) {

			vlPlinthAreaNew
					.addComponent(new ComponentIterPlinthArea("","", ""));
		}
		if (btnAddLegalDoc == event.getButton()) {

			panelLegalDocumentDetails
					.addComponent(new ComponentIteratorLegalDoc("", "", null));
		}

		if (btnAddAdjoinProperty == event.getButton()) {
			
			panelAdjoinProperties
					.addComponent(new ComponentIteratorAdjoinProperty(null,true,true,true));
		}

		if (btnAddDimension == event.getButton()) {
			panelDimension.addComponent(new ComponentIterDimensionofPlot(null,true,true,true));
		}
		if (btnAddGuideline == event.getButton()) {

			layoutGuideline
					.addComponent(new ComponentIterGuideline("","", "",""));
		}
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

		}
		if (btnAddRoofHt == event.getButton()) {
			layoutRoofHeight.addComponent(new ComponentIterRoofHeight("",""));
		}
		if (btnAddBuildSpec == event.getButton()) {
			
			panelBuildSpecfication.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));
		}
		if (btnAddBuildSpecNew == event.getButton()) {
			
			panelBuildSpecficationNew.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));
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
		if (btnAddValDtls == event.getButton()) {
			
			ValuationDetailsList obj=new ValuationDetailsList();
			layoutValuationDetails.addComponent(new ComponenetIterValuationDetails(obj));
			}
		if (btnDynamicExtra == event.getButton()) {
			layoutExtraItems.addComponent(new ComponentIterExtraItems(null,""));
}
if (btnDynamicAdditional == event.getButton()) {

	layoutAdditionItem
			.addComponent(new ComponentIterAdditionalItems( null,""));
}
if (btnDynamicMiscell == event.getButton()) {

	layoutMiscellaneous
			.addComponent(new ComponentIterMiscellaneous( null,""));
}
if (btnDynamicServices == event.getButton()) {

layoutServices.addComponent(new ComponentIerServices(null,""));
}
		if (btnDynamicEvaluation1 == event.getButton()) {

			strSelectedPanel = "1";
			showSubWindow();

		}

		if (btnDynamicCustomer == event.getButton()) {
			strSelectedPanel = "2";
			showSubWindow();

		}

		if (btnDynamicAsset == event.getButton()) {
			strSelectedPanel = "3";
			showSubWindow();

		}
		if (btnDynamicElectrical== event.getButton()) {
			strSelectedPanel = "4";
			showSubWindow();

		}
		if (btnDynamicPlumbing == event.getButton()) {
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
		
		if (btnDynamicValuation == event.getButton()) {
			strSelectedPanel = "9";
			showSubWindow();

		}
		if (btnDynamicDescProp == event.getButton()) {
			strSelectedPanel = "10";
			showSubWindow();

		}
		if (btnDynamicCharacter == event.getButton()) {
			strSelectedPanel = "11";
			showSubWindow();

		}
		if (btnDynamicConstVal == event.getButton()) {
			strSelectedPanel = "12";
			showSubWindow();

		}
		if (btnDynamicAppEstimate == event.getButton()) {
			if (layoutApplicantEstimate2.isVisible()) {
				layoutApplicantEstimate3.setVisible(true);

			} else {
				layoutApplicantEstimate3.setVisible(false);
			}
			if (layoutApplicantEstimate1.isVisible()) {
				layoutApplicantEstimate2.setVisible(true);

			} else {
				layoutApplicantEstimate2.setVisible(false);
			}
		}

		if (btnDynamicAppReason == event.getButton()) {
			strSelectedPanel = "14";
			showSubWindow();

		}
		if (btnDynamicEarthQuake == event.getButton()) {
			strSelectedPanel = "15";
			showSubWindow();

		}
		if (btnDynamicCostConst == event.getButton()) {
			if (layoutCostConstruction1.isVisible()) {
				layoutCostConstruction2.setVisible(true);
			} else {
				layoutCostConstruction2.setVisible(false);
			}
			if (layoutCost.isVisible()) {
				layoutCostConstruction1.setVisible(true);

			} else {
				layoutCostConstruction1.setVisible(false);
			}

		}
		if (btnDynamicPlanApproval == event.getButton()) {
			strSelectedPanel = "17";
			showSubWindow();

		}if (btnDynamicBuildVal == event.getButton()) {
			strSelectedPanel = "18";
			showSubWindow();

		}if (btnDynamicConstPlan == event.getButton()) {
			strSelectedPanel = "19";
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
				} else if (strSelectedPanel.equals("2")) {
					if (tfDynamicCustomer1.isVisible()) {
						tfDynamicCustomer2.setCaption(tfCaption.getValue());
						tfDynamicCustomer2.setVisible(true);
					} else {
						tfDynamicCustomer1.setCaption(tfCaption.getValue());
						tfDynamicCustomer1.setVisible(true);
					}

				} else if (strSelectedPanel.equals("3")) {
					if (tfDynamicAsset1.isVisible()) {
						tfDynamicAsset2.setCaption(tfCaption.getValue());
						tfDynamicAsset2.setVisible(true);
					} else {
						tfDynamicAsset1.setCaption(tfCaption.getValue());
						tfDynamicAsset1.setVisible(true);
					}
					}
				else if (strSelectedPanel.equals("4")) {
					if (tfDynamicElectrical1.isVisible()) {
						tfDynamicElectrical2.setCaption(tfCaption.getValue());
						tfDynamicElectrical2.setVisible(true);
					} else {
						tfDynamicElectrical1.setCaption(tfCaption.getValue());
						tfDynamicElectrical1.setVisible(true);
					}
				}
				else if (strSelectedPanel.equals("5")) {
					if (tfDynamicPlum1.isVisible()) {
						tfDynamicPlum2.setCaption(tfCaption.getValue());
						tfDynamicPlum2.setVisible(true);
					} else {
						tfDynamicPlum1.setCaption(tfCaption.getValue());
						tfDynamicPlum1.setVisible(true);
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
			}
				else if (strSelectedPanel.equals("7")) {
					if (tfDynamicViolation1.isVisible()) {
						tfDynamicViolation2.setCaption(tfCaption.getValue());
						tfDynamicViolation2.setVisible(true);
					} else {
						tfDynamicViolation1.setCaption(tfCaption.getValue());
						tfDynamicViolation1.setVisible(true);
					}
			}
				else if (strSelectedPanel.equals("9")) {
					if (tfDynamicValuation1.isVisible()) {
						tfDynamicValuation2.setCaption(tfCaption.getValue());
						tfDynamicValuation2.setVisible(true);
					} else {
						tfDynamicValuation1.setCaption(tfCaption.getValue());
						tfDynamicValuation1.setVisible(true);
					}
			}
				else if (strSelectedPanel.equals("10")) {
					if (tfDynamicDescProp1.isVisible()) {
						tfDynamicDescProp2.setCaption(tfCaption.getValue());
						tfDynamicDescProp2.setVisible(true);
					} else {
						tfDynamicDescProp1.setCaption(tfCaption.getValue());
						tfDynamicDescProp1.setVisible(true);
					}
				}
				
				else if (strSelectedPanel.equals("11")) {
					if (tfDynamicCharacter1.isVisible()) {
						tfDynamicCharacter2.setCaption(tfCaption.getValue());
						tfDynamicCharacter2.setVisible(true);
					} else {
						tfDynamicCharacter1.setCaption(tfCaption.getValue());
						tfDynamicCharacter1.setVisible(true);
					}
				}
				else if (strSelectedPanel.equals("12")) {
					if (tfDynamicConstval1.isVisible()) {
						tfDynamicConstval2.setCaption(tfCaption.getValue());
						tfDynamicConstval2.setVisible(true);
					} else {
						tfDynamicConstval1.setCaption(tfCaption.getValue());
						tfDynamicConstval1.setVisible(true);
					}
			}
				else if (strSelectedPanel.equals("14")) {
					if (tfDynamicAppReason1.isVisible()) {
						tfDynamicAppReason3.setValue(tfCaption.getValue());
						tfDynamicAppReason3.setVisible(true);
					} else {
						tfDynamicAppReason1.setValue(tfCaption.getValue());
						tfDynamicAppReason1.setVisible(true);
						
					}
					if (tfDynamicAppReason2.isVisible()) {
						tfDynamicAppReason4.setValue("");
						tfDynamicAppReason4.setVisible(true);
					} else {
						tfDynamicAppReason2.setValue("");
						tfDynamicAppReason2.setVisible(true);
					}
					}else if (strSelectedPanel.equals("15")) {
					if (tfDynamicEarthquake1.isVisible()) {
						tfDynamicEarthquake2.setCaption(tfCaption.getValue());
						tfDynamicEarthquake2.setVisible(true);
					} else {
						tfDynamicEarthquake1.setCaption(tfCaption.getValue());
						tfDynamicEarthquake1.setVisible(true);
					}
					}
					else if (strSelectedPanel.equals("17")) {
							if (tfDynamicPlanApproval1.isVisible()) {
								tfDynamicPlanApproval2.setCaption(tfCaption.getValue());
								tfDynamicPlanApproval2.setVisible(true);
							} else {
								tfDynamicPlanApproval1.setCaption(tfCaption.getValue());
								tfDynamicPlanApproval1.setVisible(true);
							}
						}else if (strSelectedPanel.equals("18")) {
							if (tfDynamicBuildval1.isVisible()) {
								tfDynamicBuildval2.setCaption(tfCaption.getValue());
								tfDynamicBuildval2.setVisible(true);
							} else {
								tfDynamicBuildval1.setCaption(tfCaption.getValue());
								tfDynamicBuildval1.setVisible(true);
							}
					}else if (strSelectedPanel.equals("19")) {
						if (tfDynamicConstPlanApp1.isVisible()) {
							tfDynamicConstPlanApp2.setCaption(tfCaption.getValue());
							tfDynamicConstPlanApp2.setVisible(true);
						} else {
							tfDynamicConstPlanApp1.setCaption(tfCaption.getValue());
							tfDynamicConstPlanApp1.setVisible(true);
						}
					}
				

			}
			mywindow.close();
		}

		if (btnSave == event.getButton()) {
			setComponentError();
			if(tfEvaluationNumber.isValid() && tfBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid())
			{
			
			saveEvaluationDetails();
			}
			//btnSave.setVisible(false);
		}
		if(btnSubmit == event.getButton()){
			setComponentError();
			if(tfEvaluationNumber.isValid() && tfBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid())
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
			btnSubmit.setVisible(false);
			btnCancel.setVisible(false);
			//.setVisible(false);
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

		}if (btnSearch == event.getButton()) {
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
			dfSearchEvalDate.setValue(null);
			tfSearchCustomer.setValue("");
			tfSearchBankbranch.setValue(null);
			populateAndConfig(false);
		}
		else if (btnBack == event.getButton()) {
			resetAllFieldsFields();
				btnSave.setVisible(false);
				btnCancel.setVisible(false);
				btnSubmit.setVisible(false);
			//	saveExcel.setVisible(false);
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
	
	}

}
