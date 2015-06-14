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
import com.gnts.pem.domain.txn.common.TPemCmBldngCostofcnstructn;
import com.gnts.pem.domain.txn.common.TPemCmBldngOldPlinthArea;
import com.gnts.pem.domain.txn.common.TPemCmBldngOldSpec;
import com.gnts.pem.domain.txn.common.TPemCmBldngRiskDtls;
import com.gnts.pem.domain.txn.common.TPemCmBldngStgofcnstructn;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.domain.txn.common.TPemCmFlatUnderValutn;
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
import com.gnts.pem.domain.txn.common.TPemCmPropOldPlanApprvl;
import com.gnts.pem.domain.txn.common.TPemCmPropRsnbleEstmate;
import com.gnts.pem.domain.txn.common.TPemCmPropValtnSummry;
import com.gnts.pem.domain.txn.sbi.TPemSbiPropChartrstic;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.txn.common.CmAssetDetailsService;
import com.gnts.pem.service.txn.common.CmBldngCostofcnstructnService;
import com.gnts.pem.service.txn.common.CmBldngOldPlinthAreaService;
import com.gnts.pem.service.txn.common.CmBldngOldSpecService;
import com.gnts.pem.service.txn.common.CmBldngRiskDtlsService;
import com.gnts.pem.service.txn.common.CmBldngStgofcnstructnService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;
import com.gnts.pem.service.txn.common.CmFlatUnderValutnService;
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
import com.gnts.pem.service.txn.common.CmPropOldPlanApprvlService;
import com.gnts.pem.service.txn.common.CmPropRsnbleEstmateService;
import com.gnts.pem.service.txn.common.CmPropValtnSummryService;
import com.gnts.pem.service.txn.sbi.SbiPropChartrsticService;
import com.gnts.pem.util.iterator.ComponentIterBuildingSpecfication;
import com.gnts.pem.util.iterator.ComponentIterDimensionofPlot;
import com.gnts.pem.util.iterator.ComponentIterGuideline;
import com.gnts.pem.util.iterator.ComponentIterOwnerDetails;
import com.gnts.pem.util.iterator.ComponentIterPlinthArea;
import com.gnts.pem.util.iterator.ComponentIteratorAdjoinProperty;
import com.gnts.pem.util.iterator.ComponentIteratorLegalDoc;
import com.gnts.pem.util.iterator.ComponentIteratorNormlDoc;
import com.gnts.pem.util.list.AdjoinPropertyList;
import com.gnts.pem.util.list.BuildSpecList;
import com.gnts.pem.util.list.DimensionList;
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

public class SBIConstruction implements ClickListener {
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
	private CmFlatUnderValutnService beanFlat = (CmFlatUnderValutnService) SpringContextHelper
			.getBean("flatValtn");
	private CmPropOldPlanApprvlService beanPlanApprvl=(CmPropOldPlanApprvlService) SpringContextHelper
			.getBean("oldPlanApprvl");
	private CmBldngOldSpecService beanSpecBuilding = (CmBldngOldSpecService) SpringContextHelper
			.getBean("oldSpec");
	private CmBldngOldPlinthAreaService beanPlinthArea=(CmBldngOldPlinthAreaService) SpringContextHelper
			.getBean("oldPlinth");
	private CmPropApplcntEstmateService beanEstimate=(CmPropApplcntEstmateService) SpringContextHelper
			.getBean("applnEstimate");
	private CmPropRsnbleEstmateService beanReasonable=(CmPropRsnbleEstmateService) SpringContextHelper
			.getBean("rsnbleEstimate");
	private CmBldngRiskDtlsService beanRiskDtls=(CmBldngRiskDtlsService) SpringContextHelper
					.getBean("bldingRiskDtls");
	private CmBldngCostofcnstructnService beanCostConstruction = (CmBldngCostofcnstructnService) SpringContextHelper
			.getBean("costOfConst");
	private CmPropGuidlnValueService beanguidelinevalue = (CmPropGuidlnValueService) SpringContextHelper
			.getBean("guidelineValue");
	private CmPropGuidlnRefdataService beanguidelinereference = (CmPropGuidlnRefdataService) SpringContextHelper
			.getBean("guidelineRef");
	private CmBldngStgofcnstructnService beanconstruction = (CmBldngStgofcnstructnService) SpringContextHelper
			.getBean("stageCnstn");
	private CmPropValtnSummryService beanPropertyvalue = (CmPropValtnSummryService) SpringContextHelper
			.getBean("propValtnSummary");
	private CmBankService beanbank = (CmBankService) SpringContextHelper
			.getBean("bank");
	
	private Accordion accordion = new Accordion();
	private Table tblEvalDetails = new Table();
	private BeanItemContainer<TPemCmEvalDetails> beans = null;

	// for common
	private VerticalLayout mainPanel = new VerticalLayout();
	private VerticalLayout searchPanel = new VerticalLayout();
	private VerticalLayout tablePanel = new VerticalLayout();
	private Long headerid;
	private String strEvaluationNo = "SBI_CONS_";
	private String strXslFile = "SbiConstruction.xsl";
	private String strComponentWidth = "200px";
	private String strEstimateWidth = "400px";
	private String strLabelWidth="350px";
	private Long selectedBankid;
	private Long selectCompanyid,currencyId;
	private String SelectedFormName;
	private String loginusername;
	private String evalNumber;
	private String customer;
	private String propertyType;
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
	private ComboBox slSearchBankbranch=new ComboBox("Bank Branch");
	private TextField tfSearchCustomer=new TextField("Customer Name");
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
	private Button btnSubmit = new Button("Submit",this);
	private Button btnCancel = new Button("Cancel", this);
//	private Button saveExcel = new Button("Report", this);
	private Label lblHeading = new Label();
int count=0;
private FileDownloader filedownloader;
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

	// Owner Details
	private VerticalLayout layoutOwnerDetails = new VerticalLayout();
	private GridLayout layoutOwnerDetails1 = new GridLayout();
	private Button btnAddOwner = new Button("", this);

	// for Document Details
	private VerticalLayout layoutNormalLegal = new VerticalLayout();
	private VerticalLayout panelNormalDocumentDetails = new VerticalLayout();
	private VerticalLayout panelLegalDocumentDetails = new VerticalLayout();
	private Button btnAddNorDoc = new Button("", this);
	private Button btnAddLegalDoc = new Button("", this);

	//for description of the property
	private VerticalLayout layoutDescProperty=new VerticalLayout();
	private GridLayout layoutDescProperty1=new GridLayout();
	private TextArea tfPostalAddress=new TextArea("Postal Address of the property");
	private TextField tfSiteNumber=new TextField("Flat No.");
	private TextField tfSFNumber=new TextField("S.F.No.");
	private TextField tfNewSFNumber=new TextField("New S.F.No.");
	private TextField tfVillage=new TextField("Village");
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
	private ComboBox slIsPlot=new ComboBox("Is Plot in Town Planning approved Layout");
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
	// for adjoin properties
	private VerticalLayout panelAdjoinProperties = new VerticalLayout();
	private Button btnAddAdjoinProperty = new Button("", this);

	// for dimension of plot
	private VerticalLayout panelDimension = new VerticalLayout();
	private Button btnAddDimension = new Button("", this);

		// valuation of land
		private VerticalLayout layoutValuationLand = new VerticalLayout();
		private GridLayout layoutValuationLand1 = new GridLayout();
		private TextField tfAreaofLand = new TextField("Area of the Land");
		private TextField tfNorthandSouth = new TextField("North and South");
		private TextField tfMarketRate = new TextField("Market Rate of land/cent");
		private TextField tfGuiderate=new TextField("Guideline rate");
		private TextField tfAdopetdMarketRate = new TextField(
				"Adopted Market rate of land/cent");
		private TextField tfFairMarketRate = new TextField(
				"Fair Market value of the Land");
		private TextField tfDynamicValuation1=new TextField();
		private TextField tfDynamicValuation2=new TextField();
		private Button btnDynamicValuation=new Button("",this);
		
		// details of plan approval
		private VerticalLayout layoutPlanApproval = new VerticalLayout();
		private GridLayout layoutPlanApproval1 = new GridLayout();
		private TextField tfLandandBuilding = new TextField("Land");
		private TextField tfBuilding=new TextField("Building");
		private TextField tfPlanApprovedBy = new TextField("Approved by");
		private TextField dfLicenseFrom = new TextField("License period");
		private ComboBox slIsLicenceForced = new ComboBox(
				"Is the license is in force");
		private ComboBox slAllApprovalRecved = new ComboBox(
				"Are all approvals required are received");
		private ComboBox slConstAsperAppPlan = new ComboBox(
				"Is the construction as per approved plan");
		private TextField tfQuality=new TextField("Quality of construction");
		private TextField tfReason=new TextField("Reason");
		private TextField tfDynamicPlanApproval1 = new TextField();
		private TextField tfDynamicPlanApproval2 = new TextField();
		private Button btnDynamicPlanApproval = new Button("", this);

		// BuildSpecfication
		private VerticalLayout panelBuildSpecfication = new VerticalLayout();
		private Button btnAddBuildSpec = new Button("", this);

		//plinth area
		private VerticalLayout layoutPlintharea=new VerticalLayout();
		private Button btnAddPlinth=new Button("", this);
		
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
			//guideline area
			private VerticalLayout layoutGuideline=new VerticalLayout();
			private Button btnAddGuideline=new Button("", this);
			
			// for guideline reference details
			private VerticalLayout layoutGuidelineReference = new VerticalLayout();
			private GridLayout layoutGuidelineReference1 = new GridLayout();
			private TextField tfZone = new TextField("Zone");
			private TextField tfSRO = new TextField("SRO");
			private TextField tfVillage1 = new TextField("Village");
			private TextField tfRevnueDist = new TextField("Revenue Dist Name");
			private TextField tfTalukName = new TextField("Taluk Name");
			private VerticalLayout streetLayout=new VerticalLayout();
			private TextField tfStreetName = new TextField();
			private ComboBox slStreetSerNo=new ComboBox();
			private TextField tfGuidelineValue = new TextField("Guide Line Value(Sqft)");
			private TextField tfGuidelineValueMatric = new TextField(
					"Guideline Value(In Metric)");
			private TextField slClassification = new TextField("Classification");
			//for property values
			private VerticalLayout layoutPropertyValue=new VerticalLayout();
			private GridLayout layoutPropertyValue1=new GridLayout();
			private TextField tfGuidelineRate=new TextField("Guideline value of land");
			private TextField tfRealziableRate = new TextField(
					"Realizable value of the Land");
			private TextField tfDistressRate = new TextField(
					"Distress value of the Land");
			
	//Estimate for construction
			private VerticalLayout layoutEstimateConstruction=new VerticalLayout();
			private FormLayout layoutEstimateConstruction1=new FormLayout();
			private TextField tfEstimateGiven=new TextField("Is the estimate given by the applicant confirms to the approved plan");
			private TextField tfAppReason=new TextField("Is the estimate given by the applicant reasonable");
			private TextField tfSalient=new TextField("If no for above discuss with Salient Features");
			private TextField tfBreakup=new TextField("Give Break up for Reasonable estimate");
			private TextField tfDynamicEstimateConst1=new TextField();
			private TextField tfDynamicEstimateConst2=new TextField();
			private Button btnDynamicEstimate=new Button("",this);
			
	// commondata
	private Window mywindow = new Window("Enter Caption");
	private Button myButton = new Button("Ok", this);
	private TextField tfCaption = new TextField();
	private String strSelectedPanel;

	// for report
	UIFlowData uiflowdata = new UIFlowData();
	private Logger logger = Logger.getLogger(SBIConstruction.class);
	public SBIConstruction() {
	
		loginusername = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		if(UI.getCurrent().getSession().getAttribute("currenyId")!=null)
		{
		currencyId=Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("currenyId").toString());
		}
		
		//currencyId=Long.valueOf(UI.getCurrent().getSession()
		//		.getAttribute("currenyId").toString());
		selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		SelectedFormName = screenName;
		String[] splitlist=screenName.split("-");
		for(String str:splitlist){
		List<MPemCmBank> list=beanbank.getBankDtlsList(selectCompanyid, null, str);
		
		for(MPemCmBank obj:list){
		selectedBankid = obj.getBankId();
		}
		break;
		}
		VerticalLayout clArgumentLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		HorizontalLayout hlHeaderLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");
		buildView(clArgumentLayout,hlHeaderLayout);

	}

	@SuppressWarnings("deprecation")
	void buildView(VerticalLayout layoutPage,HorizontalLayout hlHeaderLayout) {
		// for component width
		setComponentStyle();
		hlHeaderLayout.removeAllComponents();
		
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
		slStreetSerNo.addItem("Street Name");
		slStreetSerNo.addItem("Survey Number");
		slStreetSerNo.setNullSelectionAllowed(false);
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
		layoutAssetOwner.addComponent(PanelGenerator
				.createPanel(layoutOwnerDetails));
		lblHeading = new Label("Asset Details");
		layoutAssetOwner.addComponent(lblHeading);
		lblHeading.setStyleName("h4");
		layoutAssetOwner.addComponent(PanelGenerator
				.createPanel(layoutAssetDetails));

		// for Owner Details
		layoutOwnerDetails.addComponent(btnAddOwner);
		layoutOwnerDetails.setComponentAlignment(btnAddOwner,
				Alignment.TOP_RIGHT);
		layoutOwnerDetails1.setColumns(4);
		layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("", ""));
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
				null,true,true,true));
		
		// for dimensions
		panelDimension.addComponent(btnAddDimension);
		panelDimension.setComponentAlignment(btnAddDimension,
				Alignment.BOTTOM_RIGHT);
		panelDimension.addComponent(new ComponentIterDimensionofPlot(null,true,true,true));

		
		//for description of the property
		layoutDescProperty.setMargin(true);
		layoutDescProperty1.setSpacing(true);
		layoutDescProperty1.setColumns(4);
		layoutDescProperty.addComponent(btnDynamicDescProp);
		layoutDescProperty.setComponentAlignment(btnDynamicDescProp, Alignment.TOP_RIGHT);
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
				tfAdopetdMarketRate.setRequired(true);
				layoutValuationLand.addComponent(btnDynamicValuation);
				layoutValuationLand.setComponentAlignment(btnDynamicValuation, Alignment.TOP_RIGHT);
				layoutValuationLand.addComponent(layoutValuationLand1);
				layoutValuationLand.setMargin(true);
				tfFairMarketRate.setValue("0.00");
				tfFairMarketRate.setImmediate(true);
				tfFairMarketRate.addBlurListener(new BlurListener() {
				private static final long serialVersionUID = 1L;

					@Override
					public void blur(BlurEvent event) {
						try {
							 String numberOnly = tfFairMarketRate.getValue().replaceAll("[^0-9]", "");
							 BigDecimal fairmarket = new BigDecimal(numberOnly);
							
							BigDecimal realizable=(fairmarket.multiply(new BigDecimal(95))).divide(new BigDecimal(100));
							realizable=realizable.subtract(realizable.remainder(new BigDecimal(1000)));
							tfRealziableRate.setValue(realizable.toString());
							
							BigDecimal distress=(fairmarket.multiply(new BigDecimal(85))).divide(new BigDecimal(100));
							distress=distress.subtract(distress.remainder(new BigDecimal(1000)));
							tfDistressRate.setValue(distress.toString());
							
						} catch (Exception e) {
						}
					}
				});
				//tfEvaluationNumber.addValidator(new IntegerValidator("Enter numbers only"));
				//tfEvaluationNumber.addBlurListener(new SaarcValidate(tfEvaluationNumber));
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
				//property value
				layoutPropertyValue.setSpacing(true);
				layoutPropertyValue.setMargin(true);
				layoutPropertyValue1.setColumns(4);
				layoutPropertyValue1.setSpacing(true);
				layoutPropertyValue1.addComponent(tfRealziableRate);
				layoutPropertyValue1.addComponent(tfDistressRate);
				layoutPropertyValue1.addComponent(tfGuidelineRate);
				layoutPropertyValue.addComponent(layoutPropertyValue1);

				//for Estimate Construction
				layoutEstimateConstruction.setSpacing(true);
				layoutEstimateConstruction.setMargin(true);
				layoutEstimateConstruction.addComponent(btnDynamicEstimate);
				layoutEstimateConstruction.setComponentAlignment(btnDynamicEstimate, Alignment.TOP_RIGHT);
				layoutEstimateConstruction1.setSpacing(true);
				layoutEstimateConstruction1.addComponent(tfEstimateGiven);
				layoutEstimateConstruction1.addComponent(tfAppReason);
				layoutEstimateConstruction1.addComponent(tfSalient);
				layoutEstimateConstruction1.addComponent(tfBreakup);
				layoutEstimateConstruction1.addComponent(tfDynamicEstimateConst1);
				layoutEstimateConstruction1.addComponent(tfDynamicEstimateConst2);
				layoutEstimateConstruction.addComponent(layoutEstimateConstruction1);
				tfDynamicEstimateConst1.setVisible(false);
				tfDynamicEstimateConst2.setVisible(false);
				// for details of plan approval
				layoutPlanApproval1.setColumns(2);
				FormLayout lay1 = new FormLayout();
				FormLayout lay2 = new FormLayout();
				lay1.addComponent(tfLandandBuilding);
				lay1.addComponent(tfBuilding);
				lay1.addComponent(tfPlanApprovedBy);
				lay1.addComponent(dfLicenseFrom);
				lay1.addComponent(slIsLicenceForced);
				lay1.addComponent(tfDynamicPlanApproval2);
				
				lay2.addComponent(tfQuality);
				lay2.addComponent(slAllApprovalRecved);
				lay2.addComponent(slConstAsperAppPlan);
				lay2.addComponent(tfReason);
				lay2.addComponent(tfDynamicPlanApproval1);
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

				// for Build Specification
				panelBuildSpecfication.addComponent(btnAddBuildSpec);
				panelBuildSpecfication.setComponentAlignment(btnAddBuildSpec,
						Alignment.BOTTOM_RIGHT);
				panelBuildSpecfication
						.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));
				panelBuildSpecfication.setWidth("100%");
				// for plinth area
				layoutPlintharea.addComponent(btnAddPlinth);
				layoutPlintharea.setComponentAlignment(btnAddPlinth,
								Alignment.BOTTOM_RIGHT);
				layoutPlintharea.setMargin(true);
				layoutPlintharea.addComponent(new ComponentIterPlinthArea("Ground Floor","",""));
				layoutPlintharea.addComponent(new ComponentIterPlinthArea("Portico and Stair","",""));
				
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
				// construction
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

				// for Guideline area
				layoutGuideline.addComponent(btnAddGuideline);
				layoutGuideline.setComponentAlignment(btnAddGuideline,
										Alignment.TOP_RIGHT);
				layoutGuideline.setMargin(true);
				layoutGuideline.addComponent(new ComponentIterGuideline("Land","","",""));
				layoutGuideline.addComponent(new ComponentIterGuideline("Construction","","",""));
				
				// for guide line reference
				streetLayout.addComponent(slStreetSerNo);
				streetLayout.addComponent(tfStreetName);
				
				layoutGuidelineReference1.setColumns(4);
				layoutGuidelineReference1.setSpacing(true);
				layoutGuidelineReference1.addComponent(tfZone);
				layoutGuidelineReference1.addComponent(tfSRO);
				layoutGuidelineReference1.addComponent(tfVillage1);
				layoutGuidelineReference1.addComponent(tfRevnueDist);
				layoutGuidelineReference1.addComponent(tfTalukName);
				layoutGuidelineReference1.addComponent(streetLayout);
				layoutGuidelineReference1.addComponent(tfGuidelineValue);
				layoutGuidelineReference1.addComponent(tfGuidelineValueMatric);
				layoutGuidelineReference1.addComponent(slClassification);
				layoutGuidelineReference1.setMargin(true);
				
				layoutGuidelineReference.setSpacing(true);
				layoutGuidelineReference.addComponent(layoutGuidelineReference1);
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
		layoutPlanApproval.setStyleName("bluebar");
		accordion.addTab(PanelGenerator.createPanel(layoutEstimateConstruction),"Estimate for Construction");
		layoutEstimateConstruction.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutPlanApproval),"Details of Plan Approval");
		panelBuildSpecfication.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(panelBuildSpecfication),"Specification");
		layoutPlintharea.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutPlintharea),"Plinth Area Details");
		layoutApplicantEstimate.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutApplicantEstimate),"Applicant Estimate");
		
		layoutApplicantReasonable.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutApplicantReasonable),"Applicant Reasonable");

		layoutGuideline.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutGuideline),"Guideline Details");
		
		layoutGuidelineReference.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutGuidelineReference),"Guideline Reference Details");
		layoutPropertyValue.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutPropertyValue),"Property Value Details");
		layoutEarthquake.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutEarthquake),"Earth Quake Details");
		
		layoutCostConstruction.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutCostConstruction),"Cost of Construction");
		layoutConstruction.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutConstruction),"Stage of Construction");
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
		mainPanel.addComponent(layoutButton2);
		mainPanel.setComponentAlignment(layoutButton2, Alignment.BOTTOM_RIGHT);
		mainPanel.setVisible(false);

		layoutMainForm.setMargin(true);
		layoutMainForm.setSpacing(true);
		// for main panel
		layoutButton2.setSpacing(true);
		btnSave.setStyleName("savebt");
		btnCancel.setStyleName("cancelbt");
//		saveExcel.addStyleName("downloadbt");
		btnSubmit.setStyleName("submitbt");
		btnSave.setVisible(false);
		btnSubmit.setVisible(false);
		btnCancel.setVisible(false);

		layoutButton2.addComponent(btnSave);
		layoutButton2.addComponent(btnSubmit);
//		layoutButton2.addComponent(saveExcel);
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
		layoutSearch.addComponent(tfSearchCustomer);
		layoutSearch.addComponent(btnReset);
		layoutSearch.setComponentAlignment(btnReset, Alignment.BOTTOM_LEFT);
		btnSearch.addStyleName("default");*/
		btnReset.addStyleName("resetbt");
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
				
				tablePanel.addComponent(layoutTable);
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

	//	setTableProperties();
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
				total = evalList.size();
				if (total == 0) {
					lblNotificationIcon.setIcon(new ThemeResource("img/msg_info.png"));
					lblSaveNotification.setValue("No Records found");
				} else {
					lblNotificationIcon.setIcon(null);
					lblSaveNotification.setValue("");
				}
			}else {
				
				evalList = beanEvaluation.getSearchEvalDetailnList(SelectedFormName,null, null,null,null,selectedBankid,selectCompanyid,null);
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

/*	void setTableProperties() {
		beans = new BeanItemContainer<TPemCmEvalDetails>(
				TPemCmEvalDetails.class);
		tblEvalDetails.addGeneratedColumn("docdate",
				new DateFormateColumnGenerator());
		tblEvalDetails.addGeneratedColumn("LastUpdatedDt",
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
		
		slConstAsperAppPlan.addItem(Common.YES_DESC);
		slConstAsperAppPlan.addItem(Common.NO_DESC);
		
		slAppReasonable.addItem(Common.YES_DESC);
		slAppReasonable.addItem(Common.NO_DESC);
		
		slEarthQuake.addItem(Common.YES_DESC);
		slEarthQuake.addItem(Common.NO_DESC);
		
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
	void loadStructureDetails(){
		List<String> list = beanBankConst.getBankConstantList("STRUCTURE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
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
	void loadOwnedorRented() {
		List<String> list = beanBankConst.getBankConstantList("OWNRENT",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slProOccupiedBy.setContainerDataSource(childAccounts);
	}
	private void updateEvaluationDetails(){
		try{
			boolean valid =false;
			TPemCmEvalDetails evalobj = new TPemCmEvalDetails();
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="CONSTRUCTION";
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
			evalobj.setDocDate(dfDateofValuation.getValue());
			evalobj.setEvalNo(tfEvaluationNumber.getValue());
			evalobj.setEvalDate(dfDateofValuation.getValue());
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
			evalobj.setCheckedDt( dfVerifiedDate.getValue());
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
			
			BigDecimal construction = new BigDecimal(0.00);
			BigDecimal totalFair = new BigDecimal(0.00);
			BigDecimal fairMarket=new BigDecimal(0.00);
			BigDecimal totalRealizable = new BigDecimal(0.00);
			BigDecimal totalDistress = new BigDecimal(0.00);
			BigDecimal totalGuide = new BigDecimal(0.00);
			BigDecimal test = new BigDecimal("0.00");
			BigDecimal test1 = new BigDecimal("0.00");
			BigDecimal test2 = new BigDecimal("0.00");
			BigDecimal test3 = new BigDecimal("0.00");
			BigDecimal test4 = new BigDecimal("0.00");

			try {
				test = new BigDecimal(tfCostConstruction.getValue().replaceFirst("\\.0+$", "").replaceAll(
						"[^0-9]", ""));

			} catch (Exception e) {
				test = new BigDecimal("0.00");
				 
			}
			try {
				test1 = new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll(
						"[^0-9]", ""));

			} catch (Exception e) {
				 
				test1 = new BigDecimal("0.00");
			}
			
			try {
				test4 = new BigDecimal(tfGuidelineRate.getValue().replaceFirst("\\.0+$", "")
						.replaceAll("[^0-9]", ""));

			} catch (Exception e) {
				 
				test4 = new BigDecimal("0.00");
			}
			try {
				construction = construction.add(test);
				fairMarket=fairMarket.add(test1);
				totalFair = totalFair.add(test1).add(construction);
				uiflowdata.setTotalExtraItem(XMLUtil.IndianFormat(new BigDecimal(totalFair.toString())));
				
				totalGuide = totalGuide.add(test4);
				uiflowdata.setTotalServices(XMLUtil.IndianFormat(new BigDecimal(totalGuide.toString())));

				uiflowdata.setTotalAbstractvalue(XMLUtil.IndianFormat(new BigDecimal(totalFair.toString()))); 
				String numberOnly = totalFair.toString().replaceAll("[^\\d.]", "");
				if( numberOnly.trim().length()==0){
					numberOnly="0";	
				}
				uiflowdata.setAmountInWords(beanEvaluation.getAmountInWords(numberOnly));
				String numberOnly1 = tfGuidelineRate.getValue().replaceAll(
						"[^0-9]", "");
				uiflowdata.setAmountWordsGuideline(beanEvaluation.getAmountInWords(numberOnly1));
				evalobj.setPropertyValue(Double.valueOf(numberOnly));
				beanEvaluation.saveorUpdateEvalDetails(evalobj);
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
			} catch (Exception e) {
				 
				logger.info("Error-->" + e);
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
			evalobj.setEvalDate(dfDateofValuation.getValue());
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
			evalobj.setCheckedDt( dfVerifiedDate.getValue());
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
				saveBuildSpecDetails();
			} catch (Exception e) {
			}

			try {
				savePlinthAreaDetails();
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
			try {
				saveGuidelineValue();
			} catch (Exception e) {
			}

			try {
				saveGuidelineReferenceDetails();
			} catch (Exception e) {
			}
			try{
				saveConstructionDetails();
			}catch(Exception e){
				
			}
			try{
				saveEstimateConstruction();
			}catch(Exception e){
				
			}
			try {
				uiflowdata.setPropertyAddress(tfPropertyAddress.getValue());
				uiflowdata.setBankBranch((String)slBankBranch.getValue());
				uiflowdata.setCustomername(tfCustomerName.getValue());
				uiflowdata.setEvalnumber(tfEvaluationNumber.getValue());
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
					
					logger.info("Error-->" + e);
				}
				uiflowdata.setPropDesc((String) slPropertyDesc.getValue());
				uiflowdata.setMarketValue(XMLUtil.IndianFormat(new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""))));
				uiflowdata.setGuidelinevalue(XMLUtil.IndianFormat(new BigDecimal(tfGuidelineRate.getValue())));
				uiflowdata.setRealizablevalue(XMLUtil.IndianFormat(new BigDecimal(tfRealziableRate.getValue())));
				uiflowdata.setDistressvalue(XMLUtil.IndianFormat(new BigDecimal(tfDistressRate.getValue())));
				uiflowdata.setConstructionValue(XMLUtil.IndianFormat(new BigDecimal(tfCostConstruction.getValue())));

			} catch (Exception e) {
				logger.info("Error-->" + e);
			}
			try {
				savePropertyValueDetails();
			} catch (Exception e) {
			}
			BigDecimal construction = new BigDecimal(0.00);
			BigDecimal totalFair = new BigDecimal(0.00);
			BigDecimal fairMarket=new BigDecimal(0.00);
			BigDecimal totalRealizable = new BigDecimal(0.00);
			BigDecimal totalDistress = new BigDecimal(0.00);
			BigDecimal totalGuide = new BigDecimal(0.00);
			BigDecimal test = new BigDecimal("0.00");
			BigDecimal test1 = new BigDecimal("0.00");
			BigDecimal test2 = new BigDecimal("0.00");
			BigDecimal test3 = new BigDecimal("0.00");
			BigDecimal test4 = new BigDecimal("0.00");

			try {
				test = new BigDecimal(tfCostConstruction.getValue().replaceFirst("\\.0+$", "").replaceAll(
						"[^0-9]", ""));

			} catch (Exception e) {
				test = new BigDecimal("0.00");
				
			}
			try {
				test1 = new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll(
						"[^0-9]", ""));

			} catch (Exception e) {
				
				test1 = new BigDecimal("0.00");
			}
			
			try {
				test4 = new BigDecimal(tfGuidelineRate.getValue().replaceFirst("\\.0+$", "")
						.replaceAll("[^0-9]", ""));

			} catch (Exception e) {
				
				test4 = new BigDecimal("0.00");
			}
			try {
				construction = construction.add(test);
				fairMarket=fairMarket.add(test1);
				totalFair = totalFair.add(test1).add(construction);
				uiflowdata.setTotalExtraItem(XMLUtil.IndianFormat(new BigDecimal(totalFair.toString())));
				
				totalGuide = totalGuide.add(test4);
				uiflowdata.setTotalServices(XMLUtil.IndianFormat(new BigDecimal(totalGuide.toString())));

				uiflowdata.setTotalAbstractvalue(XMLUtil.IndianFormat(new BigDecimal(totalFair.toString()))); 
				String numberOnly = totalFair.toString().replaceAll("[^\\d.]", "");
				uiflowdata.setAmountInWords(beanEvaluation.getAmountInWords(numberOnly));
				String numberOnly1 = tfGuidelineRate.getValue().replaceAll(
						"[^0-9]", "");
				uiflowdata.setAmountWordsGuideline(beanEvaluation.getAmountInWords(numberOnly1));
			/*	List<CmCommonSetup> bill = BillGenerator.getEndValueDetails(numberOnly,headerid,loginusername,selectedBankid,selectCompanyid);
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
					real = new BigDecimal(fairMarket.toString().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
				} catch (Exception e) {
					real = new BigDecimal("0.00");
					
				}
				try {
					distre = new BigDecimal(fairMarket.toString().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
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
				
				logger.info("Error-->" + e);
			}
			try {
				test2 = new BigDecimal(uiflowdata.getRealizablevalue().replaceFirst("\\.0+$", "")
						.replaceAll("[^0-9]", ""));

			} catch (Exception e) {
				
				test2 = new BigDecimal("0.00");
			}
			try {
				test3 = new BigDecimal(uiflowdata.getDistressvalue().replaceFirst("\\.0+$", "")
						.replaceAll("[^0-9]", ""));
				

			} catch (Exception e) {
				
				test3 = new BigDecimal("0.00");
			}
			try{
				totalRealizable = totalRealizable.add(test2).add(construction);
				uiflowdata.setTotalAdditional(XMLUtil.IndianFormat(new BigDecimal(totalRealizable.toString())));
				totalDistress = totalDistress.add(test3).add(construction);
				uiflowdata.setTotalMiscellaneous(XMLUtil.IndianFormat(new BigDecimal(totalDistress.toString())));
			}catch(Exception e){
				
			}
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="CONSTRUCTION";
			ByteArrayOutputStream recvstram = XMLUtil.doMarshall(uiflowdata);
			XMLUtil.getWordDocument(recvstram, evalNumber+"_"+customer+"_"+propertyType,
					strXslFile);
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
		/*String basepath = VaadinService.getCurrent().getBaseDirectory()
				.getAbsolutePath();
		Resource res = new FileResource(new File(basepath
				+ "/WEB-INF/PEM-DOCS/"+evalNumber+"_"+customer+"_"+propertyType+".doc"));
		FileDownloader fd = new FileDownloader(res);
		fd.extend(saveExcel);*/
	
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
		
		obj =new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfBuilding.getCaption());
		obj.setFieldValue((String) tfBuilding.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj =new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPlanApprovedBy.getCaption());
		obj.setFieldValue((String) tfPlanApprovedBy.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj =new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(dfLicenseFrom.getCaption());
		obj.setFieldValue((String) dfLicenseFrom.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj =new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slIsLicenceForced.getCaption());
		obj.setFieldValue((String) slIsLicenceForced.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);
		
		obj =new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfQuality.getCaption());
		obj.setFieldValue(tfQuality.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval1().add(obj);
		
		obj =new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slAllApprovalRecved.getCaption());
		obj.setFieldValue((String) slAllApprovalRecved.getValue());
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval1().add(obj);

		obj =new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slConstAsperAppPlan.getCaption());
		obj.setFieldValue((String) slConstAsperAppPlan.getValue());
		obj.setOrderNo(8L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval1().add(obj);
		
		obj =new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfReason.getCaption());
		obj.setFieldValue(tfReason.getValue());
		obj.setOrderNo(9L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval1().add(obj);

		if (tfDynamicPlanApproval1.getValue() != null
				&& tfDynamicPlanApproval1.getValue().trim().length() > 0) {
			obj =new TPemCmPropOldPlanApprvl();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicPlanApproval1.getCaption());
			obj.setFieldValue((String) tfDynamicPlanApproval1.getValue());
			obj.setOrderNo(10L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPlanApprvl.savePropOldPlanApprvl(obj);
			uiflowdata.getPlanApproval1().add(obj);
		}

		if (tfDynamicPlanApproval2.getValue() != null
				&& tfDynamicPlanApproval2.getValue().trim().length() > 0) {
			obj = new TPemCmPropOldPlanApprvl();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicPlanApproval2.getCaption());
			obj.setFieldValue((String) tfDynamicPlanApproval2.getValue());
			obj.setOrderNo(11L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPlanApprvl.savePropOldPlanApprvl(obj);
			uiflowdata.getPlanApproval1().add(obj);
		}
	}

	
	void saveBuildSpecDetails() {

		try {

			try {
				beanSpecBuilding.deleteExistingOldBuildSpec(headerid);
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
						beanSpecBuilding.saveorUpdateOldBldgSpec(obj);
						uiflowdata.getBuildSpec().add(obj);
					}
				}

			}
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

	}

	void savePlinthAreaDetails() {

		try {

			try {
				beanPlinthArea.deleteExistingOldPlinthArea(headerid);
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
						beanPlinthArea.saveorUpdateOldPlinthArea(obj);
						uiflowdata.getPlinthArea().add(obj);
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
	void saveCostConstDetails(){
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
		obj.setFieldLabel(tfRealziableRate.getCaption());
		obj.setFieldValue((String) tfRealziableRate.getValue());
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
	void saveEstimateConstruction(){
		try {
			beanFlat.deleteExistingFlatUnderValutn(headerid);
		} catch (Exception e) {

		}
		TPemCmFlatUnderValutn obj = new TPemCmFlatUnderValutn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfEstimateGiven.getCaption());
		obj.setFieldValue(tfEstimateGiven.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFlat.saveorUpdateFlatUnderValutn(obj);
		uiflowdata.getFlatValuation().add(obj);
		
		obj = new TPemCmFlatUnderValutn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfAppReason.getCaption());
		obj.setFieldValue(tfAppReason.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFlat.saveorUpdateFlatUnderValutn(obj);
		uiflowdata.getFlatValuation().add(obj);
		
		obj = new TPemCmFlatUnderValutn();
		obj.setDocId(headerid);
		obj.setFieldLabel("If no for above discuss below with salient features of the applicants eatimate and /or reasonable estimate with rate per sft construction, etc");
		obj.setFieldValue(tfSalient.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFlat.saveorUpdateFlatUnderValutn(obj);
		uiflowdata.getFlatValuation().add(obj);
		
		obj = new TPemCmFlatUnderValutn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfBreakup.getCaption());
		obj.setFieldValue(tfBreakup.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFlat.saveorUpdateFlatUnderValutn(obj);
		uiflowdata.getFlatValuation().add(obj);
		
		if(tfDynamicEstimateConst1.getValue()!=null&&tfDynamicEstimateConst1.getValue().trim().length()>0)
		{
		obj = new TPemCmFlatUnderValutn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicEstimateConst1.getCaption());
		obj.setFieldValue(tfDynamicEstimateConst1.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFlat.saveorUpdateFlatUnderValutn(obj);
		uiflowdata.getFlatValuation().add(obj);
		}
		
		if(tfDynamicEstimateConst2.getValue()!=null&&tfDynamicEstimateConst2.getValue().trim().length()>0)
		{
		obj = new TPemCmFlatUnderValutn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicEstimateConst2.getCaption());
		obj.setFieldValue(tfDynamicEstimateConst2.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFlat.saveorUpdateFlatUnderValutn(obj);
		uiflowdata.getFlatValuation().add(obj);
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
			editLandValuationDetails();
		} catch (Exception e) {
			
		}
		try {
			editPlanApprovalDetails();
		} catch (Exception e) {
			
		}
		try {
			editBuildSpecDetails();
		} catch (Exception e) {
			
		}
		try {
			editPlinthAreaDetails();
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
		try {
			editGuidelinevalueDetails();
		} catch (Exception e) {
			
		}

		try {
			editGuidelineReferenceValues();
		} catch (Exception e) {
			
		}
		try{
			editConstructionDetails();
		}catch(Exception e){
			
		}
		try{
			editEstimateConstruction();
		}catch(Exception e){
			
		}
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
			//slDrawApproval.setCaption(obj1.getFieldLabel());
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
			tfBuilding.setValue(obj1.getFieldValue());
			tfBuilding.setCaption(obj1.getFieldLabel());
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
			tfQuality.setValue(obj1.getFieldValue());
			tfQuality.setCaption(obj1.getFieldLabel());
			obj1 = list.get(6);
			slAllApprovalRecved.setValue(obj1.getFieldValue());
			slAllApprovalRecved.setCaption(obj1.getFieldLabel());
			obj1 = list.get(7);
			slConstAsperAppPlan.setValue(obj1.getFieldValue());
			slConstAsperAppPlan.setCaption(obj1.getFieldLabel());
			obj1=list.get(8);
			tfReason.setValue(obj1.getFieldValue());
			tfReason.setCaption(obj1.getFieldLabel());
			obj1 = list.get(9);
			tfDynamicPlanApproval1.setValue(obj1.getFieldValue());
			tfDynamicPlanApproval1.setCaption(obj1.getFieldLabel());
			tfDynamicPlanApproval1.setVisible(true);
			obj1 = list.get(10);
			tfDynamicPlanApproval2.setValue(obj1.getFieldValue());
			tfDynamicPlanApproval2.setCaption(obj1.getFieldLabel());
			tfDynamicPlanApproval2.setVisible(true);
		}
		catch(Exception e){
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

	void editBuildSpecDetails() {
		List<TPemCmBldngOldSpec> specList = beanSpecBuilding.getOldBldgSpecList(headerid);

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

	
	void editPlinthAreaDetails(){
		
		List<TPemCmBldngOldPlinthArea> plinthList = beanPlinthArea.getOldPlinthAreaList(headerid);
				
		layoutPlintharea.removeAllComponents();
		layoutPlintharea.addComponent(btnAddPlinth);
		layoutPlintharea.setComponentAlignment(btnAddPlinth,
				Alignment.BOTTOM_RIGHT);
		for (TPemCmBldngOldPlinthArea obj : plinthList) {

			layoutPlintharea
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
	void editEstimateConstruction(){
		try {
			List<TPemCmFlatUnderValutn> list = beanFlat.getFlatUnderValutnList(headerid);
			TPemCmFlatUnderValutn obj1 = list.get(0);
			tfEstimateGiven.setValue(obj1.getFieldValue());
			tfEstimateGiven.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfAppReason.setValue(obj1.getFieldValue());
			tfAppReason.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfSalient.setValue(obj1.getFieldValue());
			//tfSalient.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfBreakup.setValue(obj1.getFieldValue());
			tfBreakup.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			tfDynamicEstimateConst1.setValue(obj1.getFieldValue());
			tfDynamicEstimateConst1.setValue(obj1.getFieldLabel());
			tfDynamicEstimateConst1.setVisible(true);
			obj1 = list.get(5);
			tfDynamicEstimateConst2.setValue(obj1.getFieldValue());
			tfDynamicEstimateConst2.setValue(obj1.getFieldLabel());
			tfDynamicEstimateConst2.setVisible(true);
			
		} catch (Exception e) {
			
		}
	}

	private void setComponentStyle() {
		// TODO Auto-generated method stub
		slSearchBankbranch.setNullSelectionAllowed(false);
		slSearchBankbranch.setInputPrompt(Common.SELECT_PROMPT);
		tfSearchCustomer.setInputPrompt("Enter Customer");
		tfSearchEvalNumber.setInputPrompt("Enter Evaluation Number");
		
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
		btnDynamicEstimate.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicEstimate.setStyleName(Runo.BUTTON_LINK);
		btnDynamicPlanApproval.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicPlanApproval.setStyleName(Runo.BUTTON_LINK);
		btnDynamicDescProp.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicDescProp.setStyleName(Runo.BUTTON_LINK);
		btnDynamicCharacter.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicCharacter.setStyleName(Runo.BUTTON_LINK);
		btnDynamicAppEstimate.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicAppEstimate.setStyleName(Runo.BUTTON_LINK);
		
		btnDynamicAppReason.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicAppReason.setStyleName(Runo.BUTTON_LINK);
		
		btnDynamicEarthQuake.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicEarthQuake.setStyleName(Runo.BUTTON_LINK);
		
		btnDynamicCostConst.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicCostConst.setStyleName(Runo.BUTTON_LINK);
		btnDynamicConstruction.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicConstruction.setStyleName(Runo.BUTTON_LINK);
		
		btnAddBuildSpec.setStyleName(Runo.BUTTON_LINK);
		btnAddBuildSpec.setIcon(new ThemeResource(Common.strAddIcon));
		
		btnAddPlinth.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddPlinth.setStyleName(Runo.BUTTON_LINK);
		
		btnAddGuideline.setStyleName(Runo.BUTTON_LINK);
		btnAddGuideline.setIcon(new ThemeResource(Common.strAddIcon));
		
		
		btnDynamicValuation.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicValuation.setStyleName(Runo.BUTTON_LINK);
		// for default values
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
		
	///for adjoin property and dimension
		btnAddAdjoinProperty.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnAddAdjoinProperty.setStyleName(Runo.BUTTON_LINK);
		btnAddDimension.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddDimension.setStyleName(Runo.BUTTON_LINK);
		
		//for description of the property
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
				//stage of construction
				tfSalablity.setWidth(strComponentWidth);
				tfRentalValues.setWidth(strComponentWidth);
				tfIncome.setWidth(strComponentWidth);
				tfStageofConst.setWidth(strComponentWidth);
				tfDynamicConstruction1.setWidth(strComponentWidth);
				tfDynamicConstruction2.setWidth(strComponentWidth);
				
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

				//for land valuation
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
				
				tfZone.setWidth(strComponentWidth);
				tfSRO.setWidth(strComponentWidth);
				tfVillage1.setWidth(strComponentWidth);
				tfRevnueDist.setWidth(strComponentWidth);
				tfTalukName.setWidth(strComponentWidth);
				tfStreetName.setWidth(strComponentWidth);
				slStreetSerNo.setWidth(strComponentWidth);
				tfGuidelineValue.setWidth(strComponentWidth);
				tfGuidelineValueMatric.setWidth(strComponentWidth);
				slClassification.setWidth(strComponentWidth);
				
				// for details of plan approval
				tfLandandBuilding.setWidth(strComponentWidth);
				tfBuilding.setWidth(strComponentWidth);
				tfPlanApprovedBy.setWidth(strComponentWidth);
				dfLicenseFrom.setWidth(strComponentWidth);
				tfQuality.setWidth(strComponentWidth);
				slIsLicenceForced.setWidth(strComponentWidth);
				slAllApprovalRecved.setWidth(strComponentWidth);
				slConstAsperAppPlan.setWidth(strComponentWidth);
				tfReason.setWidth(strComponentWidth);
				tfDynamicPlanApproval1.setWidth(strComponentWidth);
				tfDynamicPlanApproval2.setWidth(strComponentWidth);

				tfLandMark.setWidth(strComponentWidth);
				tfPropertyAddress.setWidth(strComponentWidth);
				tfPropertyAddress.setHeight("130px");
				tfDynamicAsset1.setWidth(strComponentWidth);
				tfDynamicAsset2.setWidth(strComponentWidth);
				
				tfEstimateGiven.setWidth(strComponentWidth);
				tfAppReason.setWidth(strComponentWidth);
				tfSalient.setWidth(strComponentWidth);
				tfBreakup.setWidth(strComponentWidth);
				tfDynamicEstimateConst1.setWidth(strComponentWidth);
				tfDynamicEstimateConst2.setWidth(strComponentWidth);	
				
				//set Null represenrtation
				tfEvaluationNumber.setNullRepresentation("");
				slBankBranch.setNullSelectionAllowed(false);
				tfEvaluationPurpose.setNullRepresentation("");
				dfDateofValuation.setWidth("150px");
				tfValuatedBy.setNullRepresentation("");
				dfVerifiedDate.setWidth("150px");
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
		//for description of the property
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
						tfDynamicAppReason3.setNullRepresentation("");
						tfDynamicAppReason4.setNullRepresentation("");
						
						//for earth quake
						slEarthQuake.setNullSelectionAllowed(false);
						tfDynamicEarthquake1.setNullRepresentation("");
						tfDynamicEarthquake2.setNullRepresentation("");
						//for construction
						tfStageofConst.setNullRepresentation("");
						tfSalablity.setNullRepresentation("");
						tfRentalValues.setNullRepresentation("");
						tfIncome.setNullRepresentation("");
						tfDynamicConstruction1.setNullRepresentation("");
						tfDynamicConstruction2.setNullRepresentation("");

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
						//for land valuation
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
						
						tfZone.setNullRepresentation("");
						tfSRO.setNullRepresentation("");
						tfVillage1.setNullRepresentation("");
						tfRevnueDist.setNullRepresentation("");
						tfTalukName.setNullRepresentation("");
						tfStreetName.setNullRepresentation("");
						slStreetSerNo.setNullSelectionAllowed(false);
						tfGuidelineValue.setNullRepresentation("");
						tfGuidelineValueMatric.setNullRepresentation("");
						slClassification.setNullRepresentation("");
						
						// for details of plan approval
						tfLandandBuilding.setNullRepresentation("");
						tfBuilding.setNullRepresentation("");
						tfPlanApprovedBy.setNullRepresentation("");
						dfLicenseFrom.setNullRepresentation("");
						tfQuality.setNullRepresentation("");
						tfDynamicPlanApproval1.setNullRepresentation("");
						slIsLicenceForced.setNullSelectionAllowed(false);
						slAllApprovalRecved.setNullSelectionAllowed(false);
						slConstAsperAppPlan.setNullSelectionAllowed(false);
						tfReason.setNullRepresentation("");
						tfDynamicPlanApproval2.setNullRepresentation("");

						tfLandMark.setNullRepresentation("");
						tfPropertyAddress.setNullRepresentation("");
						tfDynamicAsset1.setNullRepresentation("");
						tfDynamicAsset2.setNullRepresentation("");
						
						tfEstimateGiven.setNullRepresentation("");
						tfAppReason.setNullRepresentation("");
						tfSalient.setNullRepresentation("");
						tfBreakup.setNullRepresentation("");
						tfDynamicEstimateConst1.setNullRepresentation("");
						tfDynamicEstimateConst2.setNullRepresentation("");	
						tfDynamicEstimateConst2.setNullRepresentation("");
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
						null,true,true,true));

				// for dimensions
				panelDimension.removeAllComponents();
				panelDimension.addComponent(btnAddDimension);
				panelDimension.setComponentAlignment(btnAddDimension,
						Alignment.BOTTOM_RIGHT);
				panelDimension.addComponent(new ComponentIterDimensionofPlot(null,true,true,true));
				//for description of the property
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
			
				//valuation of land
				tfAreaofLand.setValue("0");
				tfNorthandSouth.setValue("");
				tfMarketRate.setValue("");
				tfGuiderate.setValue("Details Enclosed");
				tfAdopetdMarketRate.setValue("");
				tfFairMarketRate.setValue("0");
				tfRealziableRate.setValue("0");
				tfDistressRate.setValue("0");
				tfGuidelineRate.setValue("0");
				tfDynamicValuation1.setValue("");
				tfDynamicValuation2.setValue("");
				tfDynamicValuation1.setVisible(false);
				tfDynamicValuation2.setVisible(false);
				
				// details of plan approval
				tfLandandBuilding.setValue("");
				tfBuilding.setValue("");
				tfPlanApprovedBy.setValue("");
				dfLicenseFrom.setValue("");
				tfQuality.setValue("");
				tfDynamicPlanApproval1.setValue("");
				slIsLicenceForced.setValue(null);
				slAllApprovalRecved.setValue(null);
				slConstAsperAppPlan.setValue(null);
				tfReason.setValue("");
				tfDynamicPlanApproval2.setValue("");
				tfDynamicPlanApproval1.setVisible(false);
				tfDynamicPlanApproval2.setVisible(false);
				
				slIsLicenceForced.setInputPrompt(Common.SELECT_PROMPT);
				slAllApprovalRecved.setInputPrompt(Common.SELECT_PROMPT);
				slConstAsperAppPlan.setInputPrompt(Common.SELECT_PROMPT);
				
				//for construction
				tfStageofConst.setValue("");
				tfSalablity.setValue("");
				tfRentalValues.setValue("");
				tfIncome.setValue("");
				tfDynamicConstruction1.setValue("");
				tfDynamicConstruction2.setValue("");
				tfDynamicConstruction1.setVisible(false);
				tfDynamicConstruction2.setVisible(false);

				// for buildspecification

				panelBuildSpecfication.removeAllComponents();
				
				panelBuildSpecfication.addComponent(btnAddBuildSpec);
				panelBuildSpecfication.setComponentAlignment(btnAddBuildSpec,
						Alignment.BOTTOM_RIGHT);
				panelBuildSpecfication
						.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));

				//plinth Area
				layoutPlintharea.removeAllComponents();
				layoutPlintharea.addComponent(btnAddPlinth);
				layoutPlintharea.setComponentAlignment(btnAddPlinth,
						Alignment.TOP_RIGHT);
				layoutPlintharea.addComponent(new ComponentIterPlinthArea("Ground Floor","",""));
				layoutPlintharea.addComponent(new ComponentIterPlinthArea("Portico and Stair","",""));
			
				//earth quake
				slEarthQuake.setValue(null);
				slEarthQuake.setInputPrompt(Common.SELECT_PROMPT);
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
				
				//plinth Area
				layoutGuideline.removeAllComponents();
				layoutGuideline.addComponent(btnAddGuideline);
				layoutGuideline.setComponentAlignment(btnAddGuideline,
								Alignment.BOTTOM_RIGHT);
				layoutGuideline.addComponent(new ComponentIterGuideline("Land","","",""));
				layoutGuideline.addComponent(new ComponentIterGuideline("Building","","",""));
				
				tfZone.setValue("");
				tfSRO.setValue("");
				tfVillage1.setValue("");
				tfRevnueDist.setValue("");
				tfTalukName.setValue("");
				tfStreetName.setValue("");
				slStreetSerNo.setValue("");
				tfGuidelineValue.setValue("");
				tfGuidelineValueMatric.setValue("");
				slClassification.setValue("");
				
				//for estimate construction
				tfEstimateGiven.setValue("");
				tfAppReason.setValue("");
				tfSalient.setValue("");
				tfBreakup.setValue("");
				tfDynamicEstimateConst1.setValue("");
				tfDynamicEstimateConst2.setValue("");
				tfDynamicEstimateConst1.setValue("");
				tfDynamicEstimateConst1.setVisible(false);
				tfDynamicEstimateConst2.setVisible(false);
		
				// for default values
				tfCoverUnderStatCentral.setValue(Common.strNA);
				tfAnyConversionLand.setValue(Common.strNA);
				tfMonthlyRent.setValue(Common.strNA);
				tfElecServiceConnNo.setValue(Common.strNA);
				tfProTaxReceipt.setValue(Common.strNA);
				tfFlood.setValue(Common.strNil);
				tfGeneralRemarks.setValue(Common.strNil);
				
				tfEstimateGiven.setValue("Details Enclosed");
				
				
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
			
			resetAllFieldsFields();
			lblAddEdit.setValue("&nbsp;>&nbsp;Add New");
			lblAddEdit.setVisible(true);
			lblFormTittle.setVisible(false);
			hlBreadCrumbs.setVisible(true);

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

		}if (btnSave == event.getButton()) {
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
			btnSave.setEnabled(true);
			btnSubmit.setEnabled(true);
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
			
			hlFileDownloadLayout.removeAllComponents();
			hlFileDownloadLayout.addComponent(btnDownload);
			getExportTableDetails();


		}else if (btnBack == event.getButton()) {
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
		else if (btnSearch == event.getButton()) {
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
			dfSearchEvalDate.setValue(null);
			tfSearchCustomer.setValue("");
			slSearchBankbranch.setValue(null);
			populateAndConfig(false);
		}
		

		if (btnAddAdjoinProperty == event.getButton()) {
			
			panelAdjoinProperties
					.addComponent(new ComponentIteratorAdjoinProperty(null,true,true,false));
		}

		if (btnAddDimension == event.getButton()) {
			panelDimension.addComponent(new ComponentIterDimensionofPlot(null,true,true,false));
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
		}
		if (btnAddGuideline == event.getButton()) {

			layoutGuideline
					.addComponent(new ComponentIterGuideline("","", "",""));
		}
		else if (btnDynamicEvaluation1 == event.getButton()) {

			strSelectedPanel = "1";
			showSubWindow();

		}
		if (btnAddPlinth == event.getButton()) {

			layoutPlintharea.addComponent(new ComponentIterPlinthArea("","", ""));
		}
		if (btnDynamicEstimate == event.getButton()) {
			strSelectedPanel = "2";
			showSubWindow();

		}
		if (btnDynamicAsset == event.getButton()) {
			strSelectedPanel = "3";
			showSubWindow();

		}
		if (btnAddBuildSpec == event.getButton()) {
			panelBuildSpecfication
					.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));
		}
		
		if (btnDynamicPlanApproval == event.getButton()) {
			strSelectedPanel = "6";
			showSubWindow();

		}
		if (btnDynamicValuation == event.getButton()) {
			strSelectedPanel = "7";
			showSubWindow();

		}
		
		if (btnDynamicDescProp == event.getButton()) {
			strSelectedPanel = "8";
			showSubWindow();

		}
		if (btnDynamicConstruction == event.getButton()) {
			strSelectedPanel = "4";
			showSubWindow();

		}
		if (btnDynamicCharacter == event.getButton()) {
			strSelectedPanel = "9";
			showSubWindow();

		}if (btnDynamicAppEstimate == event.getButton()) {
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
			strSelectedPanel = "11";
			showSubWindow();

		}
		if (btnDynamicEarthQuake == event.getButton()) {
			strSelectedPanel = "12";
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
				
				 else if (strSelectedPanel.equals("6")) {
					if (tfDynamicPlanApproval1.isVisible()) {
						tfDynamicPlanApproval2.setCaption(tfCaption.getValue());
						tfDynamicPlanApproval2.setVisible(true);
					} else {
						tfDynamicPlanApproval1.setCaption(tfCaption.getValue());
						tfDynamicPlanApproval1.setVisible(true);
					}
				}
				else if (strSelectedPanel.equals("7")) {
					if (tfDynamicValuation1.isVisible()) {
						tfDynamicValuation2.setCaption(tfCaption.getValue());
						tfDynamicValuation2.setVisible(true);
					} else {
						tfDynamicValuation1.setCaption(tfCaption.getValue());
						tfDynamicValuation1.setVisible(true);
					}
			}
				else if (strSelectedPanel.equals("8")) {
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
				}
				else if (strSelectedPanel.equals("11")) {
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
					}else if (strSelectedPanel.equals("12")) {
					if (tfDynamicEarthquake1.isVisible()) {
						tfDynamicEarthquake2.setCaption(tfCaption.getValue());
						tfDynamicEarthquake2.setVisible(true);
					} else {
						tfDynamicEarthquake1.setCaption(tfCaption.getValue());
						tfDynamicEarthquake1.setVisible(true);
					}
					}
					else if (strSelectedPanel.equals("2")) {
						if (tfDynamicEstimateConst1.isVisible()) {
							tfDynamicEstimateConst2.setCaption(tfCaption.getValue());
							tfDynamicEstimateConst2.setVisible(true);
						} else {
							tfDynamicEstimateConst1.setCaption(tfCaption.getValue());
							tfDynamicEstimateConst1.setVisible(true);
						}
						}
				
			}
			mywindow.close();
		}
	}
}
