/**
 * File Name	:	SyndBuilding.java
 * Description	:	This class is used for add/edit Syndicate bank building details and generate report.
 * Author		:	Karthigadevi S
 * Date			:	March 20, 2014
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
import com.gnts.pem.domain.txn.common.TPemCmBldngExtraItems;
import com.gnts.pem.domain.txn.common.TPemCmBldngMiscData;
import com.gnts.pem.domain.txn.common.TPemCmBldngOldPlinthArea;
import com.gnts.pem.domain.txn.common.TPemCmBldngOldSpec;
import com.gnts.pem.domain.txn.common.TPemCmBldngRoofHght;
import com.gnts.pem.domain.txn.common.TPemCmBldngService;
import com.gnts.pem.domain.txn.common.TPemCmBldngStgofcnstructn;
import com.gnts.pem.domain.txn.common.TPemCmBldngTechDetails;
import com.gnts.pem.domain.txn.common.TPemCmBldngValutnSummry;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.domain.txn.common.TPemCmLandValutnData;
import com.gnts.pem.domain.txn.common.TPemCmOwnerDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropAdjoinDtls;
import com.gnts.pem.domain.txn.common.TPemCmPropDimension;
import com.gnts.pem.domain.txn.common.TPemCmPropDocDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropGuidlnRefdata;
import com.gnts.pem.domain.txn.common.TPemCmPropGuidlnValue;
import com.gnts.pem.domain.txn.common.TPemCmPropLegalDocs;
import com.gnts.pem.domain.txn.common.TPemCmPropOldPlanApprvl;
import com.gnts.pem.domain.txn.common.TPemCmPropValtnSummry;
import com.gnts.pem.domain.txn.synd.TPemSydPropMatchBoundry;
import com.gnts.pem.domain.txn.synd.TPemSynBldngRoom;
import com.gnts.pem.domain.txn.synd.TPemSynPropAreaDtls;
import com.gnts.pem.domain.txn.synd.TPemSynPropFloor;
import com.gnts.pem.domain.txn.synd.TPemSynPropOccupancy;
import com.gnts.pem.domain.txn.synd.TPemSynPropViolation;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.txn.common.CmAssetDetailsService;
import com.gnts.pem.service.txn.common.CmBldngAditnlItmsService;
import com.gnts.pem.service.txn.common.CmBldngExtraItemsService;
import com.gnts.pem.service.txn.common.CmBldngMiscDataService;
import com.gnts.pem.service.txn.common.CmBldngOldPlinthAreaService;
import com.gnts.pem.service.txn.common.CmBldngOldSpecService;
import com.gnts.pem.service.txn.common.CmBldngRoofHghtService;
import com.gnts.pem.service.txn.common.CmBldngServiceDtlsService;
import com.gnts.pem.service.txn.common.CmBldngStgofcnstructnService;
import com.gnts.pem.service.txn.common.CmBldngTechDetailsService;
import com.gnts.pem.service.txn.common.CmBldngValutnSummryService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;
import com.gnts.pem.service.txn.common.CmLandValutnDataService;
import com.gnts.pem.service.txn.common.CmOwnerDetailsService;
import com.gnts.pem.service.txn.common.CmPropAdjoinDtlsService;
import com.gnts.pem.service.txn.common.CmPropDimensionService;
import com.gnts.pem.service.txn.common.CmPropDocDetailsService;
import com.gnts.pem.service.txn.common.CmPropGuidlnRefdataService;
import com.gnts.pem.service.txn.common.CmPropGuidlnValueService;
import com.gnts.pem.service.txn.common.CmPropLegalDocsService;
import com.gnts.pem.service.txn.common.CmPropOldPlanApprvlService;
import com.gnts.pem.service.txn.common.CmPropValtnSummryService;
import com.gnts.pem.service.txn.synd.SydPropMatchBoundryService;
import com.gnts.pem.service.txn.synd.SynBldngRoomService;
import com.gnts.pem.service.txn.synd.SynPropAreaDtlsService;
import com.gnts.pem.service.txn.synd.SynPropFloorService;
import com.gnts.pem.service.txn.synd.SynPropOccupancyService;
import com.gnts.pem.service.txn.synd.SynPropViolationService;
import com.gnts.pem.util.UIFlowData;
import com.gnts.pem.util.XMLUtil;
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
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Accordion;
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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class SyndBuilding implements ClickListener {
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
private SynBldngRoomService beanRooms = (SynBldngRoomService) SpringContextHelper
		.getBean("synBldgRoom");
private SynPropFloorService beanFloor = (SynPropFloorService) SpringContextHelper
		.getBean("synPropFloor");
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
private CmPropOldPlanApprvlService beanPlanApprvl=(CmPropOldPlanApprvlService) SpringContextHelper
		.getBean("oldPlanApprvl");
private CmBldngTechDetailsService beanTechDtls=(CmBldngTechDetailsService) SpringContextHelper
.getBean("bldgTechDtls");
private CmBldngRoofHghtService beanRoofHt=(CmBldngRoofHghtService) SpringContextHelper
.getBean("bldngRoofHt");
private CmBldngOldSpecService beanSpecBuilding = (CmBldngOldSpecService) SpringContextHelper
		.getBean("oldSpec");
private CmBldngOldPlinthAreaService beanPlinthArea=(CmBldngOldPlinthAreaService) SpringContextHelper
.getBean("oldPlinth");
private CmBldngValutnSummryService beanValuationDtls=(CmBldngValutnSummryService) SpringContextHelper
			.getBean("bldngvaltnSummary");
	private CmBldngExtraItemsService beanExtra=(CmBldngExtraItemsService) SpringContextHelper
			.getBean("extraItms");
	private CmBldngAditnlItmsService beanAddtional=(CmBldngAditnlItmsService) SpringContextHelper
			.getBean("addtionalItms");
	private CmBldngMiscDataService beanMiscell=(CmBldngMiscDataService) SpringContextHelper
			.getBean("miscellItms");
	private CmBldngServiceDtlsService beanService=(CmBldngServiceDtlsService) SpringContextHelper
			.getBean("bldingServiceDtls");
	private CmPropGuidlnValueService beanguidelinevalue = (CmPropGuidlnValueService) SpringContextHelper
			.getBean("guidelineValue");
	private CmPropGuidlnRefdataService beanguidelinereference = (CmPropGuidlnRefdataService) SpringContextHelper
			.getBean("guidelineRef");
	private CmPropValtnSummryService beanPropertyvalue = (CmPropValtnSummryService) SpringContextHelper
			.getBean("propValtnSummary");
	private CmBankService beanbank = (CmBankService) SpringContextHelper
			.getBean("bank");
	
	private Table tblEvalDetails = new Table();
	private BeanItemContainer<TPemCmEvalDetails> beans = null;

	private VerticalLayout mainPanel = new VerticalLayout();
	private VerticalLayout searchPanel = new VerticalLayout();
	private VerticalLayout tablePanel = new VerticalLayout();
	private Long headerid;
	private String strEvaluationNo = "SYN_BUILD_";
	private String strXslFile="SyndBuiding.xsl";
	private String evalNumber;
	private String customer;
	private String propertyType;
	// pagination
			private int total = 0;
	// for table panel
	private VerticalLayout layoutTable = new VerticalLayout();
	private HorizontalLayout hlAddEditLayout = new HorizontalLayout();
	private Button btnAdd = new Button("Add", this);
	// Added Button Back by Hohul
	
	private Button btnBack;
	
	
	// Added Bread Crumbs by Hohul
	private HorizontalLayout hlBreadCrumbs;
	int count =0;
	private FileDownloader filedownloader;
	// Label Declared Here by Hohul
	private Label lblTableTitle;
	private Label lblFormTittle, lblFormTitle1, lblAddEdit;
	private Label lblSaveNotification, lblNotificationIcon,lblNoofRecords;
	
	
	// Declared String Variable for Screen Name by Hohul
	
	private String screenName;
	
	private Button btnEdit = new Button("Edit", this);
	private Button btnView = new Button("View Document", this);

	// for search panel
		private HorizontalLayout layoutSearch = new HorizontalLayout();
		private TextField tfSearchEvalNumber=new TextField("Evaluation Number");
		private PopupDateField dfSearchEvalDate=new PopupDateField("Evaluation Date"); 
		private TextField tfSearchCustomer=new TextField("Customer Name");
		private ComboBox tfSearchBankbranch=new ComboBox("Bank Branch");
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
	private Accordion accordion = new Accordion();
	private VerticalLayout layoutMainForm = new VerticalLayout();
	private HorizontalLayout layoutButton2 = new HorizontalLayout();
	private Button btnSave = new Button("Save", this);
	private Button btnCancel = new Button("Cancel", this);
	private Button btnSubmit = new Button("Submit",this);
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
	
	// matching boundaries
			private VerticalLayout layoutmachingBoundary = new VerticalLayout();
			private FormLayout layoutmachingBoundary1 = new FormLayout();
			private VerticalLayout layoutmachingBoundary2=new VerticalLayout();
			private ComboBox slMatchingBoundary = new ComboBox("Matching of Boundaries");
			private ComboBox slPlotDemarcated = new ComboBox("Plot Demarcated");
			private ComboBox slApproveLandUse = new ComboBox("Approved Land Use");
			private ComboBox slTypeofProperty = new ComboBox("Type of Property");
			private TextField tfDynamicmatching1=new TextField();
			private TextField tfDynamicmatching2=new TextField();
			private Button btnDynamicmatching=new Button("",this);

			// tenure/occupancy details
			private GridLayout layoutTenureOccupay = new GridLayout();
			private FormLayout layoutTenureOccupay1 = new FormLayout();
			private VerticalLayout layoutTenureOccupay2=new VerticalLayout();
			private TextField tfStatusofTenure = new TextField("Status of Tenure");
			private ComboBox slOwnedorRent = new ComboBox("Owned/Rented");
			private TextField tfNoOfYears = new TextField("No of Years Occupancy");
			private TextField tfRelationship = new TextField(
						"Relationship of tenant to the owner");
			private TextField tfDynamicTenure1=new TextField();
			private TextField tfDynamicTenure2=new TextField();
			private Button btnDynamicTenure=new Button("",this);


			// no of rooms
			private VerticalLayout layoutNoofRooms = new VerticalLayout();
			private VerticalLayout layoutNoofRooms2 = new VerticalLayout();
			private FormLayout layoutNoofRooms1 = new FormLayout();
			private TextField tfNoofRooms = new TextField("No. of Rooms");
			private TextField tfLivingDining = new TextField("Living/Dining");
			private TextField tfBedRooms = new TextField("Bed Rooms");
			private TextField tfKitchen = new TextField("Kitchen");
			private TextField tfToilets = new TextField("Toilets");
			private TextField tfDynamicRooms1 = new TextField();
			private TextField tfDynamicRooms2 = new TextField();
			private Button btnDynamicRooms = new Button("", this);

			// no of floors
			private GridLayout layoutNoofFloors = new GridLayout();
			private FormLayout layoutNoofFloors1 = new FormLayout();
			private VerticalLayout layoutNoofFloors2=new VerticalLayout();
			private TextField tfTotNoofFloors = new TextField("Total No. of Floors");
			private TextField tfPropertyLocated = new TextField(
					"Floor on which the property is located");
			private TextField tfApproxAgeofBuilding = new TextField(
					"Approx age of the building");
			private TextField tfResidualAgeofBuilding = new TextField(
					"Residual age of the building");
			private ComboBox slTypeofStructure = new ComboBox("Types of structure");
			private TextField tfDynamicFloors1 = new TextField();
			private TextField tfDynamicFloors2 = new TextField();
			private Button btnDynamicFloor = new Button("", this);

	// area details of the property
	private VerticalLayout layoutAreaDetails = new VerticalLayout();
	private GridLayout layoutAreaDetails1 = new GridLayout();
	private TextField tfSiteArea = new TextField("Site Area");
	private TextField tfPlinthArea = new TextField(
			"Plinth Area of the building");
	private TextField tfCarpetArea = new TextField("Carpet Area");
	private TextField tfSalableArea = new TextField("Salabale Area");
	private TextArea tfRemarks = new TextArea("Remarks");
	private TextField tfDynamicAreaDetail1=new TextField();
	private TextField tfDynamicAreaDetail2=new TextField();
	private Button btnDynamicAreaDetail=new Button("",this);
	

	// valuation of land
	private VerticalLayout layoutValuationLand = new VerticalLayout();
	private GridLayout layoutValuationLand1 = new GridLayout();
	private TextField tfAreaofLand = new TextField("Area of the Land");
	private TextField tfNorthandSouth = new TextField("North and South");
	private TextField tfMarketRate = new TextField("Market Rate of land/cent");
	private TextField tfAdopetdMarketRate = new TextField(
			"Adopted Market rate of land/cent");
	private TextField tfFairMarketRate = new TextField(
			"Fair Market value of the Land");
	private TextField tfDynamicValuation1=new TextField();
	private TextField tfDynamicValuation2=new TextField();
	private Button btnDynamicValuation=new Button("",this);
	
	//valuation of under construction
	private VerticalLayout layoutConstValuation=new VerticalLayout();
	private GridLayout layoutConstValuation1=new GridLayout();
	private FormLayout formConstValuation1=new FormLayout();
	private FormLayout formConstValuation2=new FormLayout();
	private VerticalLayout layoutRoofHeight=new VerticalLayout();
	private ComboBox slTypeProperty=new ComboBox("Type of Building");
	private ComboBox slTypeStructure=new ComboBox("Type of Construction");
	private TextField tfYearConstruction=new TextField("Year Of Construction");
	private TextField tfNoFloors=new TextField("Number of floors & height");
	private TextField tfDynamicConstval1=new TextField();
	private TextField tfDynamicConstval2=new TextField();
	private Button btnDynamicConstVal=new Button("",this);
	private Button btnAddRoofHt=new Button("",this);
	
	//Details of Building second off
	private ComboBox slExterior=new ComboBox("Exterior Condition");
	private ComboBox slInterior=new ComboBox("Interior Condition");
	private TextField tfLifeAge=new TextField("Life/Age of the building");
	private TextField tfFutureLife=new TextField("Future Life of building");
	private TextField tfDetailsPlan=new TextField("Details of plan approval");
	private ComboBox slDeviation=new ComboBox("Whether there are any deviations");
	private TextField tfDtlsDeviation=new TextField("Details of deviation");
	
	//plinth area
	private VerticalLayout layoutPlintharea=new VerticalLayout();
	private Button btnAddPlinth=new Button("", this);
	
	//BuildSpecfication
	private VerticalLayout panelBuildSpecfication=new VerticalLayout();
	private Button btnAddBuildSpec=new Button("", this);
	
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
	
	//for property values
	private VerticalLayout layoutPropertyValue=new VerticalLayout();
	private GridLayout layoutPropertyValue1=new GridLayout();
	private TextField tfRealziableRate = new TextField(
			"Realizable value of the Land");
	private TextField tfDistressRate = new TextField(
			"Distress value of the Land");
	private TextField tfGuidelineRate = new TextField(
			"Guideline value of the Land");
	
	//plinth area
	private VerticalLayout layoutGuideline=new VerticalLayout();
	private Button btnAddGuideline=new Button("", this);	

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
					"Is construction as per approved plan");
			private TextField tfDynamicPlanApproval1 = new TextField();
			private TextField tfDynamicPlanApproval2 = new TextField();
			private Button btnDynamicPlanApproval = new Button("", this);
	
	// construction
	private VerticalLayout layoutConstViolation=new VerticalLayout();
	private VerticalLayout layoutConstruction = new VerticalLayout();
	private GridLayout layoutConstruction1 = new GridLayout();
	private TextField tfStageofConst = new TextField("Stage of Construction");
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

	// commondata
	private Window mywindow = new Window("Enter Caption");
	private Button myButton = new Button("Ok", this);
	private TextField tfCaption = new TextField();
	private String strSelectedPanel;

	private String strComponentWidth = "200px";
	private String strWidth="100px";
	private String strLblWidth="75px";
	private Long selectedBankid;
	private Long selectCompanyid,currencyId;
	private String SelectedFormName;
	private String loginusername;

	// for report
	private UIFlowData uiflowdata = new UIFlowData();
	private Logger logger = Logger.getLogger(SyndBuilding.class);
	public SyndBuilding() {
		
		loginusername = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		if(UI.getCurrent().getSession().getAttribute("currenyId")!=null)
		{
		currencyId=Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("currenyId").toString());
		}
		//currencyId=Long.valueOf(UI.getCurrent().getSession().getAttribute("currenyId").toString());
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
		hlHeaderLayout.removeAllComponents();
		setComponentStyle();

		tblEvalDetails = new Table();
		tblEvalDetails.setStyleName(Runo.TABLE_SMALL);
		tblEvalDetails.setPageLength(12);
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

		// for
				layoutmachingBoundary1.setSpacing(true);
				tfDynamicmatching1.setVisible(true);
				tfDynamicmatching2.setVisible(true);
				layoutmachingBoundary2.addComponent(btnDynamicmatching);
				layoutmachingBoundary2.setComponentAlignment(btnDynamicmatching, Alignment.TOP_RIGHT);
				layoutmachingBoundary1.setSpacing(true);
				layoutmachingBoundary1.addComponent(slMatchingBoundary);
				layoutmachingBoundary1.addComponent(slPlotDemarcated);
				layoutmachingBoundary1.addComponent(slApproveLandUse);
				layoutmachingBoundary1.addComponent(slTypeofProperty);
				layoutmachingBoundary1.addComponent(tfDynamicmatching1);
				layoutmachingBoundary1.addComponent(tfDynamicmatching2);
				layoutmachingBoundary1.setSpacing(true);
				layoutmachingBoundary1.setMargin(true);

				// tenure/occupancy details
				layoutTenureOccupay.setSpacing(true);
				layoutTenureOccupay1.setSpacing(true);
				layoutTenureOccupay2.addComponent(btnDynamicTenure);
				layoutTenureOccupay2.setComponentAlignment(btnDynamicTenure, Alignment.TOP_RIGHT);
				layoutTenureOccupay.setColumns(2);
				layoutTenureOccupay1.addComponent(tfStatusofTenure);
				layoutTenureOccupay1.addComponent(slOwnedorRent);
				layoutTenureOccupay1.addComponent(tfNoOfYears);
				layoutTenureOccupay1.addComponent(tfRelationship);
				layoutTenureOccupay1.addComponent(tfDynamicTenure1);
				layoutTenureOccupay1.addComponent(tfDynamicTenure2);
				layoutmachingBoundary2.addComponent(layoutmachingBoundary1);
				layoutTenureOccupay2.addComponent(layoutTenureOccupay1);
				layoutTenureOccupay.addComponent(PanelGenerator.createPanel(layoutmachingBoundary2));
				layoutTenureOccupay.addComponent(PanelGenerator.createPanel(layoutTenureOccupay2));
				tfDynamicTenure1.setVisible(false);
				tfDynamicTenure2.setVisible(false);
				layoutmachingBoundary1.setMargin(true);
				layoutTenureOccupay1.setMargin(true);
				layoutmachingBoundary.addComponent(layoutTenureOccupay);
				layoutTenureOccupay1.setMargin(true);
				layoutmachingBoundary.setMargin(true);


				// for no of rooms
				layoutNoofRooms2.addComponent(btnDynamicRooms);
				layoutNoofRooms2.setComponentAlignment(btnDynamicRooms,
						Alignment.TOP_RIGHT);
				layoutNoofRooms2.setMargin(true);
				layoutNoofRooms1.addComponent(tfNoofRooms);
				layoutNoofRooms1.addComponent(tfLivingDining);
				layoutNoofRooms1.addComponent(tfBedRooms);
				layoutNoofRooms1.addComponent(tfKitchen);
				layoutNoofRooms1.addComponent(tfToilets);
				layoutNoofRooms1.addComponent(tfDynamicRooms1);
				layoutNoofRooms1.addComponent(tfDynamicRooms2);
				tfDynamicRooms1.setVisible(false);
				tfDynamicRooms2.setVisible(false);
				layoutNoofRooms1.setSpacing(true);	
				layoutNoofRooms2.addComponent(layoutNoofRooms1);
				// no of floors
				layoutNoofFloors.setColumns(2);
				layoutNoofFloors2.setMargin(true);
				layoutNoofFloors2.addComponent(btnDynamicFloor);
				layoutNoofFloors2.setComponentAlignment(btnDynamicFloor,
				Alignment.TOP_RIGHT);
				layoutNoofFloors1.addComponent(tfTotNoofFloors);
				layoutNoofFloors1.addComponent(tfPropertyLocated);
				layoutNoofFloors1.addComponent(tfApproxAgeofBuilding);
				layoutNoofFloors1.addComponent(tfResidualAgeofBuilding);
				layoutNoofFloors1.addComponent(slTypeofStructure);
				layoutNoofFloors1.addComponent(tfDynamicFloors1);
				layoutNoofFloors1.addComponent(tfDynamicFloors2);
				tfDynamicFloors1.setVisible(false);
				tfDynamicFloors2.setVisible(false);
				layoutNoofFloors1.setSpacing(true);
				layoutNoofFloors2.addComponent(layoutNoofFloors1);
				layoutNoofFloors.setSpacing(true);
				layoutNoofFloors.addComponent(PanelGenerator.createPanel(layoutNoofFloors2));
				layoutNoofFloors.addComponent(PanelGenerator.createPanel(layoutNoofRooms2));

				layoutNoofRooms.addComponent(layoutNoofFloors);
				layoutNoofRooms.setMargin(true);
	
	
				// construction
				//layoutConstruction.setCaption("Construction");
				layoutConstruction1.setSpacing(true);
				layoutConstruction1.setColumns(4);
				layoutConstruction1.addComponent(tfStageofConst);
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
				
				layoutConstViolation.addComponent(PanelGenerator.createPanel(layoutConstruction));
				layoutConstViolation.addComponent(PanelGenerator.createPanel(layoutViolation));
				layoutConstViolation.setSpacing(true);
				layoutConstViolation.setMargin(true);
		// area details of the property
		//layoutAreaDetails.setCaption("Area Details of the Property");
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
		layoutAreaDetails.setComponentAlignment(btnDynamicAreaDetail, Alignment.TOP_RIGHT);
		layoutAreaDetails.addComponent(layoutAreaDetails1);
		layoutAreaDetails.setMargin(true);

		// valuation of land
		//layoutValuationLand.setCaption("Valuation of Land");
		layoutValuationLand.setSpacing(true);
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
		layoutValuationLand.setComponentAlignment(btnDynamicValuation, Alignment.TOP_RIGHT);
		layoutValuationLand.addComponent(layoutValuationLand1);
		layoutValuationLand.setMargin(true);

		//property value
		layoutPropertyValue.setSpacing(true);
		layoutPropertyValue.setMargin(true);
		layoutPropertyValue1.setColumns(4);
		layoutPropertyValue1.setSpacing(true);
		layoutPropertyValue1.addComponent(tfRealziableRate);
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
		//valuation of Construction
		formConstValuation1.addComponent(slTypeProperty);
		formConstValuation1.addComponent(slTypeStructure);
		formConstValuation1.addComponent(tfYearConstruction);
		formConstValuation1.addComponent(slExterior);
		formConstValuation1.addComponent(slInterior);
		formConstValuation1.addComponent(tfLifeAge);
		formConstValuation1.addComponent(tfFutureLife);
		formConstValuation1.addComponent(tfDetailsPlan);
		formConstValuation1.addComponent(slDeviation);
		formConstValuation1.addComponent(tfDtlsDeviation);
		
		formConstValuation2.addComponent(btnAddRoofHt);
		formConstValuation2.addComponent(tfNoFloors);
		//formConstValuation2.addComponent(layoutRoofHeight);
		
		layoutConstValuation.setSpacing(true);
		layoutConstValuation1.setSpacing(true);
		layoutConstValuation1.setColumns(2);
		layoutConstValuation1.addComponent(formConstValuation1);
		layoutConstValuation1.addComponent(new VerticalLayout(){
		{
		addComponent(formConstValuation2);
		addComponent(layoutRoofHeight);	
		}	
				
		});
		
		formConstValuation1.addComponent(tfDynamicConstval1);
		formConstValuation1.addComponent(tfDynamicConstval2);
		layoutConstValuation.addComponent(btnDynamicConstVal);
		layoutConstValuation.setComponentAlignment(btnDynamicConstVal, Alignment.TOP_RIGHT);
		layoutConstValuation.addComponent(layoutConstValuation1);
		layoutConstValuation.setMargin(true);
		
		// for plinth area
			layoutPlintharea.addComponent(btnAddPlinth);
			layoutPlintharea.setComponentAlignment(btnAddPlinth,
						Alignment.TOP_RIGHT);
			layoutPlintharea.setMargin(true);
			layoutPlintharea.addComponent(new ComponentIterPlinthArea("Ground Floor","",""));
			layoutPlintharea.addComponent(new ComponentIterPlinthArea("Portico and Stair","",""));
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
			
		
		// for Build Specification
			panelBuildSpecfication.addComponent(btnAddBuildSpec);
			panelBuildSpecfication.setComponentAlignment(btnAddBuildSpec,
						Alignment.TOP_RIGHT);
			panelBuildSpecfication.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));
			panelBuildSpecfication.setWidth("100%");
		//for Calculation-extra Items
			layoutExtraItems.setCaption("Extra Items");
			layoutExtraItems.setWidth("420px");
			layoutExtraItems.addComponent(btnDynamicExtra);
			layoutExtraItems.setComponentAlignment(btnDynamicExtra, Alignment.TOP_RIGHT);
			layoutExtraItems.addComponent(new ComponentIterExtraItems(null,""));
			
			layoutAdditionItem.addComponent(btnDynamicAdditional);
			layoutAdditionItem.setComponentAlignment(btnDynamicAdditional, Alignment.TOP_RIGHT);
			layoutAdditionItem.addComponent(new ComponentIterAdditionalItems(null,""));
			
			layoutAdditionItem.setCaption("Additional Items");
			layoutAdditionItem.setWidth("400px");
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
		
		layoutMainForm.addComponent(PanelGenerator
				.createPanel(accordion));
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
				
		layoutmachingBoundary.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutmachingBoundary),"Matching of Boundaries And Tenure/Occupancy Details");
		
		layoutNoofRooms.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutNoofRooms),"No. of Rooms/No. of Floors");
		layoutAreaDetails.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutAreaDetails),"Area Details of the Property");
		layoutValuationLand.setStyleName("bluebar");
		accordion.addTab(layoutValuationLand,"Valuation of Land");
		layoutConstValuation.setStyleName("bluebar");
		layoutPlanApproval.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutPlanApproval),"Details of Plan Approval");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutConstValuation),"Valuation of Building");
		panelBuildSpecfication.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(panelBuildSpecfication),"Specification of the building");
				
		layoutValuationDetails.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutValuationDetails),"Details of Valuation");
				
		layoutExtraAddItems1.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutExtraAddItems1),"Extra/Additional Items");
		
		layoutMiscellService1.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutMiscellService1),"Miscellaneous/Services");
			
		layoutGuideline.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutGuideline),"Guideline Details");
		
		layoutGuidelineReference.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutGuidelineReference),"Guideline Reference Details");
		layoutPlintharea.setStyleName("bluebar");
		layoutPropertyValue.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutPropertyValue),"Property Value Details");
		layoutPlintharea.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutPlintharea),"Plinth Area Details");	
		layoutConstruction.setStyleName("bluebar");
		layoutViolation.setStyleName("bluebar");
		accordion.addTab(PanelGenerator 
				.createPanel(layoutConstViolation),"Construction/Violation Details");
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
			}
			
		}});
		layoutMainForm.setMargin(true);
		layoutMainForm.setSpacing(true);
		// for main panel
		layoutButton2.setSpacing(true);
		btnSave.setStyleName("savebt");
		btnCancel.setStyleName("cancelbt");
		//saveExcel.addStyleName("downloadbt");
		btnSubmit.setStyleName("submitbt");
		layoutButton2.addComponent(btnSave);
		layoutButton2.addComponent(btnSubmit);
		//layoutButton2.addComponent(saveExcel);
		layoutButton2.addComponent(btnCancel);
		
		btnSave.setVisible(false);
		btnCancel.setVisible(false);
		btnSubmit.setVisible(false);
		//saveExcel.setVisible(false);
		
		
		

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
	
		
		/*String basepath = VaadinService.getCurrent()
		            .getBaseDirectory().getAbsolutePath();
		Resource res = new FileResource(new File(basepath +
		                "/WEB-INF/view/channel.doc"));
		FileDownloader fd = new FileDownloader(res);
		fd.extend(saveExcel);
*/		
		mainPanel.addComponent(layoutMainForm);
		//mainPanel.addComponent(layoutButton2);
		//mainPanel.setComponentAlignment(layoutButton2, Alignment.BOTTOM_RIGHT);
		mainPanel.setVisible(false);


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






	/*	layoutSearch.addComponent(tfSearchEvalNumber);
		layoutSearch.addComponent(tfSearchBankbranch);
		layoutSearch.addComponent(tfSearchCustomer);
		//layoutSearch.addComponent(btnSearch);
		layoutSearch.addComponent(btnReset);
		//layoutSearch.setComponentAlignment(btnSearch, Alignment.BOTTOM_LEFT);
		layoutSearch.setComponentAlignment(btnReset, Alignment.BOTTOM_LEFT);*/
		//btnSearch.addStyleName("default");
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
		
		/*layoutSearch.setSpacing(true);
		layoutSearch.setMargin(true);*/
		/*lblHeading=new Label("Search");
		lblHeading.setStyleName("h4");
		searchPanel.addComponent(lblHeading);*/
		//searchPanel.addComponent(PanelGenerator.createPanel(layoutSearch));
		
		searchPanel.addComponent(PanelGenerator
				.createPanel(vlSearchComponentandButtonLayout));
		searchPanel.setMargin(true);
		
		
		// Search panel functions ends here by hohul

		
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
		subwindow.setSpacing(true);
		subwindow.setMargin(true);
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
			String bankbranch=(String)tfSearchBankbranch.getValue();
			evalList = beanEvaluation.getSearchEvalDetailnList(null, evalno, null, customer,bankbranch,selectedBankid,selectCompanyid,null);
		}else {
			
			evalList = beanEvaluation.getSearchEvalDetailnList(SelectedFormName,null, null,null,null,selectedBankid,selectCompanyid,null);
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

	/*void setTableProperties() {
		beans = new BeanItemContainer<TPemCmEvalDetails>(
				TPemCmEvalDetails.class);
		tblEvalDetails.addGeneratedColumn("docdate",
				new DateFormateColumnGenerator());
		tblEvalDetails.addGeneratedColumn("lastupdateddt",
				new DateColumnGenerator());
	}*/

	private void updateEvaluationDetails(){
		try{
			boolean valid=false;
			TPemCmEvalDetails evalobj = new TPemCmEvalDetails();
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="BUILDING";
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
					test1=new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
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
			evalobj.setPropertyValue(Double.valueOf(numberOnly));
			uiflowdata.setEvalDtls(evalobj);
			
			uiflowdata.setAmountInWords(beanEvaluation.getAmountInWords(numberOnly));
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
		// for save evaluation details
		boolean valid = false;
		try {
			TPemCmEvalDetails evalobj = new TPemCmEvalDetails();
			evalobj.setDocId(headerid);
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
				evalobj.setCustomLbl2(tfDynamicEvaluation2.getCaption());
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
				saveRoofHeightDetails();
			} catch (Exception e) {
			}
			try {
				saveMatchingBoundaries();
			} catch (Exception e) {
			}
			try {
				saveNoofRooms();
			} catch (Exception e) {
			}
			try {
				saveNoofFloors();
			} catch (Exception e) {
			}
			try {
				saveTenureOccupayDetails();
			} catch (Exception e) {
			}
			try {
				saveBuildTechDetails();
			} catch (Exception e) {
			}
			try {
				savePlinthAreaDetails();
			} catch (Exception e) {
			}
			try {
				saveBuildSpecDetails();
			} catch (Exception e) {
			}
			try{
				saveValuationDetails();
			}catch(Exception e){
				
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
				savePlanApprovalDetails();
			} catch (Exception e) {
			}
			try{
			uiflowdata.setPropertyAddress(tfPropertyAddress.getValue());
			uiflowdata.setBankBranch((String)tfBankBranch.getValue());
			uiflowdata.setEvalnumber(tfEvaluationNumber.getValue());
			uiflowdata.setCustomername(tfCustomerName.getValue());
			
			if(dfDateofValuation.getValue()!=null)
			{
			SimpleDateFormat dt1 = new SimpleDateFormat("dd-MMM-yyyy");
			uiflowdata.setInspectionDate(dt1.format(dfDateofValuation.getValue()));
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
			try {
				saveValuationofLandDetails();
			} catch (Exception e) {
				 
			}
			uiflowdata.setPropDesc((String)slPropertyDesc.getValue());
			uiflowdata.setMarketValue(XMLUtil.IndianFormat(new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""))));
			uiflowdata.setGuidelinevalue(XMLUtil.IndianFormat(new BigDecimal(tfGuidelineRate.getValue())));
			}catch(Exception e){
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
					test1=new BigDecimal(tfFairMarketRate.getValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
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
			//bill

			BigDecimal realizable=new BigDecimal(0.00);
			BigDecimal distress=new BigDecimal(0.00);
			BigDecimal real=new BigDecimal("0.00");
			BigDecimal distre=new BigDecimal("0.00");
			
			try {
				real = new BigDecimal(uiflowdata.getTotalAbstractvalue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
			} catch (Exception e) {
				real = new BigDecimal("0.00");
				 
			}
			try {
				distre = new BigDecimal(uiflowdata.getTotalAbstractvalue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", ""));
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
			}catch(Exception e){
				
			}
			try {
				savePropertyValueDetails();
			} catch (Exception e) {
			}
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue().replace("\\s+", " ").replaceAll("[-+/^:,]","");;
			propertyType="BUILDING";
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

		obj =new TPemSydPropMatchBoundry();
		obj.setDocId(headerid);
		obj.setFieldLabel(slPlotDemarcated.getCaption());
		obj.setFieldValue((String) slPlotDemarcated.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanmatchboundary.saveorUpdateSydPropMatchBoundry(obj);
		uiflowdata.getBoundary().add(obj);

		obj =new TPemSydPropMatchBoundry();
		obj.setDocId(headerid);
		obj.setFieldLabel(slApproveLandUse.getCaption());
		obj.setFieldValue((String) slApproveLandUse.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanmatchboundary.saveorUpdateSydPropMatchBoundry(obj);
		uiflowdata.getBoundary().add(obj);

		obj =new TPemSydPropMatchBoundry();
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
			obj =new TPemSydPropMatchBoundry();
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
			obj =new TPemSydPropMatchBoundry();
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

	private void saveNoofRooms() {
		try {
			beanRooms.deleteExistingSynBldngRoom(headerid);
		} catch (Exception e) {
		}
		TPemSynBldngRoom obj = new TPemSynBldngRoom();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfNoofRooms.getCaption());
		obj.setFieldValue((String) tfNoofRooms.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanRooms.saveorUpdateSynBldngRoom(obj);
		uiflowdata.getRoom().add(obj);

		obj = new TPemSynBldngRoom();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfLivingDining.getCaption());
		obj.setFieldValue((String) tfLivingDining.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanRooms.saveorUpdateSynBldngRoom(obj);
		uiflowdata.getRoom().add(obj);

		obj = new TPemSynBldngRoom();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfBedRooms.getCaption());
		obj.setFieldValue((String) tfBedRooms.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanRooms.saveorUpdateSynBldngRoom(obj);
		uiflowdata.getRoom().add(obj);

		obj = new TPemSynBldngRoom();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfKitchen.getCaption());
		obj.setFieldValue((String) tfKitchen.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanRooms.saveorUpdateSynBldngRoom(obj);
		uiflowdata.getRoom().add(obj);

		obj = new TPemSynBldngRoom();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfToilets.getCaption());
		obj.setFieldValue((String) tfToilets.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanRooms.saveorUpdateSynBldngRoom(obj);
		uiflowdata.getRoom().add(obj);

		if (tfDynamicRooms1.getValue() != null
				&& tfDynamicRooms1.getValue().trim().length() > 0) {
			obj = new TPemSynBldngRoom();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicRooms1.getCaption());
			obj.setFieldValue((String) tfDynamicRooms1.getValue());
			obj.setOrderNo(6L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanRooms.saveorUpdateSynBldngRoom(obj);
			uiflowdata.getRoom().add(obj);
		}

		if (tfDynamicRooms2.getValue() != null
				&& tfDynamicRooms2.getValue().trim().length() > 0) {
			obj = new TPemSynBldngRoom();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicRooms2.getCaption());
			obj.setFieldValue((String) tfDynamicRooms2.getValue());
			obj.setOrderNo(7L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanRooms.saveorUpdateSynBldngRoom(obj);
			uiflowdata.getRoom().add(obj);
		}

	}

	private void saveNoofFloors() {
		try {
			beanFloor.deleteExistingSynPropFloor(headerid);
		} catch (Exception e) {
		}
		TPemSynPropFloor obj = new TPemSynPropFloor();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfTotNoofFloors.getCaption());
		obj.setFieldValue((String) tfTotNoofFloors.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFloor.saveorUpdateSynPropFloor(obj);
		uiflowdata.getFloor().add(obj);

		obj = new TPemSynPropFloor();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPropertyLocated.getCaption());
		obj.setFieldValue((String) tfPropertyLocated.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFloor.saveorUpdateSynPropFloor(obj);
		uiflowdata.getFloor().add(obj);

		obj = new TPemSynPropFloor();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfApproxAgeofBuilding.getCaption());
		obj.setFieldValue((String) tfApproxAgeofBuilding.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFloor.saveorUpdateSynPropFloor(obj);
		uiflowdata.getFloor().add(obj);

		obj = new TPemSynPropFloor();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfResidualAgeofBuilding.getCaption());
		obj.setFieldValue((String) tfResidualAgeofBuilding.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFloor.saveorUpdateSynPropFloor(obj);
		uiflowdata.getFloor().add(obj);

		obj = new TPemSynPropFloor();
		obj.setDocId(headerid);
		obj.setFieldLabel(slTypeofStructure.getCaption());
		obj.setFieldValue((String) slTypeofStructure.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFloor.saveorUpdateSynPropFloor(obj);
		uiflowdata.getFloor().add(obj);

		if (tfDynamicFloors1.getValue() != null
				&& tfDynamicFloors1.getValue().trim().length() > 0) {
			obj = new TPemSynPropFloor();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicFloors1.getCaption());
			obj.setFieldValue((String) tfDynamicFloors1.getValue());
			obj.setOrderNo(6L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanFloor.saveorUpdateSynPropFloor(obj);
			uiflowdata.getFloor().add(obj);
		}

		if (tfDynamicFloors2.getValue() != null
				&& tfDynamicFloors2.getValue().trim().length() > 0) {
			obj = new TPemSynPropFloor();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicFloors2.getCaption());
			obj.setFieldValue((String) tfDynamicFloors2.getValue());
			obj.setOrderNo(7L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanFloor.saveorUpdateSynPropFloor(obj);
			uiflowdata.getFloor().add(obj);
		}

	}
	void saveTenureOccupayDetails() {
		try {
			beantenureOccupancy.deleteSynPropOccupancy(headerid);
		} catch (Exception e) {

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



	void saveBuildTechDetails(){
		
		try {
			beanTechDtls.deleteExistingBldngTechDtls(headerid);
		} catch (Exception e) {

		}

		TPemCmBldngTechDetails obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slTypeProperty.getCaption());
		obj.setFieldValue((String)slTypeProperty.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slTypeStructure.getCaption());
		obj.setFieldValue((String) slTypeStructure.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfYearConstruction.getCaption());
		obj.setFieldValue(tfYearConstruction.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);
		
		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel("Number of floors & height of each floor including basement if any");
		obj.setFieldValue(tfNoFloors.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slExterior.getCaption());
		obj.setFieldValue((String)slExterior.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);
		
		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slInterior.getCaption());
		obj.setFieldValue((String)slInterior.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);
		
		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfLifeAge.getCaption());
		obj.setFieldValue((String)tfLifeAge.getValue());
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);
		
		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFutureLife.getCaption());
		obj.setFieldValue((String)tfFutureLife.getValue());
		obj.setOrderNo(8L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);
		
		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel("Details of plan approval from the appropriate authority");
		obj.setFieldValue((String)tfDetailsPlan.getValue());
		obj.setOrderNo(9L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);

		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel("Whether there are any deviations vis a vis the plan approval and effect of the same on valuation");
		obj.setFieldValue((String)slDeviation.getValue());
		obj.setOrderNo(10L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);
		
		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDtlsDeviation.getCaption());
		obj.setFieldValue((String)tfDtlsDeviation.getValue());
		obj.setOrderNo(11L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls1().add(obj);
		
		if(tfDynamicConstval1.getValue()!=null&&tfDynamicConstval1.getValue().trim().length()>0)
		{
		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicConstval1.getCaption());
		obj.setFieldValue((String) tfDynamicConstval1.getValue());
		obj.setOrderNo(12L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);
		}
		if(tfDynamicConstval2.getValue()!=null&&tfDynamicConstval2.getValue().trim().length()>0)
		{
		obj =new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicConstval2.getCaption());
		obj.setFieldValue((String) tfDynamicConstval2.getValue());
		obj.setOrderNo(13L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanTechDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);
		}	
	}
	
	void savePlinthAreaDetails() {

		try {

			try {
				beanPlinthArea.deleteExistingOldPlinthArea(headerid);
			} catch (Exception e) {
				logger.info("Error-->" + e);
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
			logger.info("Error-->" + e);
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
						obj.setRateConstn(XMLUtil.IndianFormat(new BigDecimal(list.getRateLabel())));
						obj.setReplaceCost(XMLUtil.IndianFormat(new BigDecimal(list.getReplaceLabel())));
						obj.setDepreciation(XMLUtil.IndianFormat(new BigDecimal(list.getDepreciationLabel())));
						obj.setNetValue(XMLUtil.IndianFormat(new BigDecimal(list.getNetValueLabel())));
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
;							uiflowdata.getService().add(obj);
							i++;
						}
						uiflowdata.setTotalServices(XMLUtil.IndianFormat(new BigDecimal(service.toString())));
					}
					

				}
			} catch (Exception e) {
				logger.info("Error-->"+e);
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
	private void savePlanApprovalDetails() {
		try {
			beanPlanApprvl.deleteExistingPropOldPlanApprvl(headerid);
		} catch (Exception e) {

		}

		TPemCmPropOldPlanApprvl obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel((String)slLandandBuilding.getValue());
		obj.setFieldValue(tfLandandBuilding.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);
		
		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel((String)slBuilding.getValue());
		obj.setFieldValue(tfBuilding.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPlanApprovedBy.getCaption());
		obj.setFieldValue((String) tfPlanApprovedBy.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(dfLicenseFrom.getCaption());
		obj.setFieldValue((String) dfLicenseFrom.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slIsLicenceForced.getCaption());
		obj.setFieldValue((String) slIsLicenceForced.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slAllApprovalRecved.getCaption());
		obj.setFieldValue((String) slAllApprovalRecved.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPlanApprvl.savePropOldPlanApprvl(obj);
		uiflowdata.getPlanApproval().add(obj);

		obj = new TPemCmPropOldPlanApprvl();
		obj.setDocId(headerid);
		obj.setFieldLabel(slConstAsperAppPlan.getCaption());
		obj.setFieldValue((String) slConstAsperAppPlan.getValue());
		obj.setOrderNo(7L);
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
			obj.setOrderNo(8L);
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
			obj.setOrderNo(9L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanPlanApprvl.savePropOldPlanApprvl(obj);
			uiflowdata.getPlanApproval().add(obj);
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
			editRoofHeightDetails();
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
			editRoomDetails();
		} catch (Exception e) {
			
		}
		try {
			editFloorDetails();
		} catch (Exception e) {
			
		}
		try {
			editTenureOccupancyDetails();
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
			editConstValuationDetails();
		} catch (Exception e) {
			
		}
		try {
			editPlinthAreaDetails();
		} catch (Exception e) {
			
		}
		try {
			editBuildSpecDetails();
		} catch (Exception e) {
			
		}
		try {
			editValuationDetails();
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
		try{
			editPropertyValueDetails();
		}
		catch(Exception e){
			
		}
		try{
			editPlanApprovalDetails();
		}catch(Exception e){
			
		}
		try {
			editConstructionDetails();
		} catch (Exception e) {
			
		}

		try {
			editViolationDetails();
		} catch (Exception e) {
			
		}
	}

	void editOwnerDetails() {
		try {
			List<TPemCmOwnerDetails> ownerlist = beanOwner.getOwnerDtlsList(headerid);
			
		
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
				Alignment.TOP_RIGHT);
		for (TPemCmPropDocDetails obj : doclist) {

			panelNormalDocumentDetails
					.addComponent(new ComponentIteratorNormlDoc(obj
							.getFieldLabel(), obj.getApprovalYN(), obj
							.getApproveAuth(), obj.getApproveRef()));
		}

	}

	void editLegalDocuments() {
		List<TPemCmPropLegalDocs> doclist = legalDoc.getPropLegalDocsList(headerid);
		panelLegalDocumentDetails.removeAllComponents();
		panelLegalDocumentDetails.addComponent(btnAddLegalDoc);
		panelLegalDocumentDetails.setComponentAlignment(btnAddLegalDoc,
				Alignment.TOP_RIGHT);
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
				valObj.setRateLabel(obj1.getRateConstn().replaceAll("[^\\d.]", ""));
				valObj.setReplaceLabel(obj1.getReplaceCost().replaceAll("[^\\d.]", ""));
				valObj.setDepreciationLabel(obj1.getDepreciation().replaceAll("[^\\d.]", ""));
				valObj.setNetValueLabel(obj1.getNetValue().replaceAll("[^\\d.]", ""));
				
				list.add(valObj);
			}
			for(ValuationDetailsList test:list)
			{
				layoutValuationDetails.addComponent(new ComponenetIterValuationDetails(test));
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

	private void editRoomDetails() {
		try {
			List<TPemSynBldngRoom> list = beanRooms.getSynBldngRoomList(headerid);
			TPemSynBldngRoom obj1 = list.get(0);
			tfNoofRooms.setValue(obj1.getFieldValue());
			tfNoofRooms.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfLivingDining.setValue(obj1.getFieldValue());
			tfLivingDining.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfBedRooms.setValue(obj1.getFieldValue());
			tfBedRooms.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfKitchen.setValue(obj1.getFieldValue());
			tfKitchen.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			tfToilets.setValue(obj1.getFieldValue());
			tfToilets.setCaption(obj1.getFieldLabel());
			obj1 = list.get(5);
			tfDynamicRooms1.setValue(obj1.getFieldValue());
			tfDynamicRooms1.setCaption(obj1.getFieldLabel());
			tfDynamicRooms1.setVisible(true);
			obj1 = list.get(6);
			tfDynamicRooms2.setValue(obj1.getFieldValue());
			tfDynamicRooms2.setCaption(obj1.getFieldLabel());
			tfDynamicRooms2.setVisible(true);

		} catch (Exception e) {

			
		}
	}

	private void editFloorDetails() {
		try {
			List<TPemSynPropFloor> list = beanFloor.getSynPropFloorList(headerid);
			TPemSynPropFloor obj1 = list.get(0);
			tfTotNoofFloors.setValue(obj1.getFieldValue());
			tfTotNoofFloors.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfPropertyLocated.setValue(obj1.getFieldValue());
			tfPropertyLocated.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfApproxAgeofBuilding.setValue(obj1.getFieldValue());
			tfApproxAgeofBuilding.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfResidualAgeofBuilding.setValue(obj1.getFieldValue());
			tfResidualAgeofBuilding.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			slTypeofStructure.setValue(obj1.getFieldValue());
			slTypeofStructure.setCaption(obj1.getFieldLabel());
			obj1 = list.get(5);
			tfDynamicFloors1.setValue(obj1.getFieldValue());
			tfDynamicFloors1.setCaption(obj1.getFieldLabel());
			tfDynamicFloors1.setVisible(true);
			obj1 = list.get(6);
			tfDynamicFloors2.setValue(obj1.getFieldValue());
			tfDynamicFloors2.setCaption(obj1.getFieldLabel());
			tfDynamicFloors2.setVisible(true);

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
	void editConstValuationDetails(){
		try{
		List<TPemCmBldngTechDetails> list = beanTechDtls.getBldgTechDtlsList(headerid);
		TPemCmBldngTechDetails obj1 = list.get(0);
		slTypeProperty.setCaption(obj1.getFieldLabel());
		slTypeProperty.setValue(obj1.getFieldValue());
		obj1 = list.get(1);
		slTypeStructure.setValue(obj1.getFieldValue());
		slTypeStructure.setCaption(obj1.getFieldLabel());
		obj1 = list.get(2);
		tfYearConstruction.setValue(obj1.getFieldValue());
		tfYearConstruction.setCaption(obj1.getFieldLabel());
		obj1 = list.get(3);
		tfNoFloors.setValue(obj1.getFieldValue());
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
		slDeviation.setCaption("Whether there are any deviations");
		slDeviation.setValue(obj1.getFieldValue());
		obj1 = list.get(10);
		tfDtlsDeviation.setValue(obj1.getFieldValue());
		obj1 = list.get(11);
		tfDynamicConstval1.setValue(obj1.getFieldValue());
		tfDynamicConstval1.setCaption(obj1.getFieldLabel());
		tfDynamicConstval1.setVisible(true);
		obj1 = list.get(12);
		tfDynamicConstval2.setValue(obj1.getFieldValue());
		tfDynamicConstval2.setCaption(obj1.getFieldLabel());
		tfDynamicConstval2.setVisible(true);

	} catch(Exception e) {

		
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
						.addComponent(new ComponentIterPlinthArea(obj.getFieldLabel(),
								obj.getAsPerPlan(),obj.getAsPerSite()));
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
		
		List<TPemCmBldngAditnlItms> doclist = beanAddtional.getAditnlItmsList(headerid);
		layoutAdditionItem.removeAllComponents();
		layoutAdditionItem.addComponent(btnDynamicAdditional);
		layoutAdditionItem.setComponentAlignment(btnDynamicAdditional,
				Alignment.TOP_RIGHT);
		for (TPemCmBldngAditnlItms obj : doclist) {

			layoutAdditionItem.addComponent(new ComponentIterAdditionalItems(obj.getFieldLabel(), obj.getFieldValue().replaceFirst("\\.0+$", "").replaceAll("[^0-9]", "")));
		}

	}
	void editMiscellaneous(){
		
		List<TPemCmBldngMiscData> doclist = beanMiscell.getMiscDataList(headerid);
		layoutMiscellaneous.removeAllComponents();
		layoutMiscellaneous.addComponent(btnDynamicMiscell);
		layoutMiscellaneous.setComponentAlignment(btnDynamicMiscell,
				Alignment.TOP_RIGHT);
		for (TPemCmBldngMiscData obj : doclist) {

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
			tfRealziableRate.setValue(obj1.getFieldValue());
			tfRealziableRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfDistressRate.setValue(obj1.getFieldValue());
			tfDistressRate.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfGuidelineRate.setValue(obj1.getFieldValue());
			tfGuidelineRate.setCaption(obj1.getFieldLabel());

		} catch (Exception e) {

			
		}
	
	}
	private void editPlanApprovalDetails() {
		try {
			List<TPemCmPropOldPlanApprvl> list = beanPlanApprvl.getPropOldPlanApprvlList(headerid);
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
			slAllApprovalRecved.setCaption(obj1.getFieldLabel());
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

	void loadComponentListValues() {
		loadPropertyDescList();
		loadPropertyStructureList();
		slMatchingBoundary.addItem(Common.YES_DESC);
		slMatchingBoundary.addItem(Common.NO_DESC);

		slPlotDemarcated.addItem(Common.YES_DESC);
		slPlotDemarcated.addItem(Common.NO_DESC);
		
		slDeviation.addItem(Common.YES_DESC);
		slDeviation.addItem(Common.NO_DESC);
		
		slAllApprovalRecved.addItem(Common.YES_DESC);
		slAllApprovalRecved.addItem(Common.NO_DESC);
		
		slConstAsperAppPlan.addItem(Common.YES_DESC);
		slConstAsperAppPlan.addItem(Common.NO_DESC);
		
		slIsLicenceForced.addItem(Common.YES_DESC);
		slIsLicenceForced.addItem(Common.NO_DESC);
		
		loadTypeofProperty();
		loadOwnedorRented();
		loadCondtionList();
	loadBankBranchDetails();
	loadDetailPlan();
	}
	void loadDetailPlan(){
		List<String> list = beanBankConst.getBankConstantList("DETAILS_PLAN",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slLandandBuilding.setContainerDataSource(childAccounts);
		slBuilding.setContainerDataSource(childAccounts);
	}
void loadBankBranchDetails(){
	List<String> list = beanBankConst.getBankConstantList("BRANCH_CODE",selectCompanyid);
	BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
			String.class);
	childAccounts.addAll(list);
	tfBankBranch.setContainerDataSource(childAccounts);
	tfSearchBankbranch.setContainerDataSource(childAccounts);
}
	void loadCondtionList() {
		List<String> list =beanBankConst.getBankConstantList("EGNP",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slExterior.setContainerDataSource(childAccounts);
		slInterior.setContainerDataSource(childAccounts);
	}

	void loadPropertyDescList() {
		List<String> list =beanBankConst.getBankConstantList("PROP_DESC",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slPropertyDesc.setContainerDataSource(childAccounts);
	}
	void loadPropertyStructureList() {
		List<String> list =beanBankConst.getBankConstantList("STRUCTURE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slTypeStructure.setContainerDataSource(childAccounts);
		slTypeofStructure.setContainerDataSource(childAccounts);
	}
	void loadTypeofProperty() {
		List<String> list =beanBankConst.getBankConstantList("RIC",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slApproveLandUse.setContainerDataSource(childAccounts);
		slTypeofProperty.setContainerDataSource(childAccounts);
		slTypeProperty.setContainerDataSource(childAccounts);
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
		slPropertyDesc.setInputPrompt(Common.YES_DESC);

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
				Alignment.TOP_RIGHT);
		panelAdjoinProperties.addComponent(new ComponentIteratorAdjoinProperty(
				null,true,true,true));

		// for dimensions
		panelDimension.removeAllComponents();
		panelDimension.addComponent(btnAddDimension);
		panelDimension.setComponentAlignment(btnAddDimension,
				Alignment.TOP_RIGHT);
		panelDimension.addComponent(new ComponentIterDimensionofPlot(null,true,true,true));
		
		//for buildspecification
		
		panelBuildSpecfication.removeAllComponents();
		panelBuildSpecfication.addComponent(btnAddBuildSpec);
			panelBuildSpecfication.setComponentAlignment(btnAddBuildSpec,
						Alignment.TOP_RIGHT);
				panelBuildSpecfication.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));

		slMatchingBoundary.setValue(null);
		slPlotDemarcated.setValue(null);
		slApproveLandUse.setValue(null);
		slTypeofProperty.setValue(null);
		tfDynamicmatching1.setValue("");
		tfDynamicmatching2.setValue("");
		tfDynamicmatching1.setVisible(false);
		tfDynamicmatching2.setVisible(false);
		
		//for room
		tfNoofRooms.setValue("");
		tfLivingDining.setValue("");
		tfBedRooms.setValue("");
		tfKitchen.setValue("");
		tfToilets.setValue("");
		tfDynamicRooms1.setValue("");
		tfDynamicRooms2.setValue("");
		tfDynamicRooms1.setVisible(false);
		tfDynamicRooms2.setVisible(false);
	
		// no of floors
		tfTotNoofFloors.setValue("");
		tfPropertyLocated.setValue("");
		tfApproxAgeofBuilding.setValue("");
		tfResidualAgeofBuilding.setValue("");
		slTypeofStructure.setValue("");
		tfDynamicFloors1.setValue("");
		tfDynamicFloors2.setValue("");
		tfDynamicFloors1.setVisible(false);
		tfDynamicFloors2.setVisible(false);
		slTypeofStructure.setInputPrompt(Common.SELECT_PROMPT);
		//tenure
		tfStatusofTenure.setValue("");
		slOwnedorRent.setValue(null);
		tfNoOfYears.setValue("");
		tfRelationship.setValue("");
		tfDynamicTenure1.setValue("");
		tfDynamicTenure2.setValue("");
		tfDynamicTenure1.setVisible(false);
		tfDynamicTenure2.setVisible(false);
		slOwnedorRent.setInputPrompt(Common.SELECT_PROMPT);
		
		tfDynamicConstval1.setValue("");
		tfDynamicConstval2.setValue("");
		tfDynamicConstval1.setVisible(false);
		tfDynamicConstval1.setVisible(false);

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
		
		
		slTypeProperty.setValue("");
		slTypeStructure.setValue("");
		tfYearConstruction.setValue("");
		tfNoFloors.setValue("");
		slExterior.setValue("");
		slInterior.setValue("");
		tfLifeAge.setValue("");
		tfFutureLife.setValue("");
		tfDetailsPlan.setValue("");
		slDeviation.setValue("");
		tfDtlsDeviation.setValue("");
		tfDynamicConstval1.setValue("");
		tfDynamicConstval2.setValue("");
		tfDynamicConstval1.setVisible(false);
		tfDynamicConstval2.setVisible(false);
		
		slTypeProperty.setInputPrompt(Common.SELECT_PROMPT);
		slTypeStructure.setInputPrompt(Common.SELECT_PROMPT);
		slExterior.setInputPrompt(Common.SELECT_PROMPT);
		slInterior.setInputPrompt(Common.SELECT_PROMPT);
		//plinth Area
		layoutPlintharea.removeAllComponents();
		layoutPlintharea.addComponent(btnAddPlinth);
		layoutPlintharea.setComponentAlignment(btnAddPlinth,
				Alignment.BOTTOM_RIGHT);
		layoutPlintharea.addComponent(new ComponentIterPlinthArea("Ground Floor","",""));
		layoutPlintharea.addComponent(new ComponentIterPlinthArea("Portico and Stair","",""));
		
		//Roof ht
		layoutRoofHeight.removeAllComponents();
		layoutRoofHeight.addComponent(btnAddRoofHt);
		layoutRoofHeight.setComponentAlignment(btnAddRoofHt,
						Alignment.TOP_RIGHT);
		layoutRoofHeight.addComponent(new ComponentIterRoofHeight("Ground Floor",""));
		layoutRoofHeight.addComponent(new ComponentIterRoofHeight("First Floor",""));
		
		// details of plan approval
				tfLandandBuilding.setValue("");
				slLandandBuilding.setValue(null);
				slBuilding.setValue("");
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
				
				slLandandBuilding.setInputPrompt(Common.SELECT_PROMPT);
				slBuilding.setInputPrompt(Common.SELECT_PROMPT);
		
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
		//for extra item
		
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
		tfPlinthArea.setValue(Common.strNil);
		tfCarpetArea.setValue(Common.strNil);
		tfRemarks.setValue(Common.strNil);
		accordion.setSelectedTab(0);
		
		slMatchingBoundary.setValue(Common.YES_DESC);
		slPlotDemarcated.setValue(Common.YES_DESC);
		slDeviation.setValue(Common.YES_DESC);
		slAllApprovalRecved.setValue(Common.YES_DESC);
		slConstAsperAppPlan.setValue(Common.YES_DESC);
		slIsLicenceForced.setValue(Common.YES_DESC);
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
		tfCustomerAddr.setHeight("150px");
		slPropertyDesc.setNullSelectionAllowed(false);

		//for matching boundary
		slMatchingBoundary.setWidth(strComponentWidth);
		slPlotDemarcated.setWidth(strComponentWidth);
		slApproveLandUse.setWidth(strComponentWidth);
		slTypeofProperty.setWidth(strComponentWidth);
		tfDynamicmatching1.setWidth(strComponentWidth);
		tfDynamicmatching2.setWidth(strComponentWidth);
		
		//room
		tfNoofRooms.setWidth(strComponentWidth);
		tfLivingDining.setWidth(strComponentWidth);
		tfBedRooms.setWidth(strComponentWidth);
		tfKitchen.setWidth(strComponentWidth);
		tfToilets.setWidth(strComponentWidth);
		tfDynamicRooms1.setWidth(strComponentWidth);
		tfDynamicRooms2.setWidth(strComponentWidth);
		
		//floor
		tfTotNoofFloors.setWidth(strComponentWidth);
		tfPropertyLocated.setWidth(strComponentWidth);
		tfApproxAgeofBuilding.setWidth(strComponentWidth);
		tfResidualAgeofBuilding.setWidth(strComponentWidth);
		slTypeofStructure.setWidth(strComponentWidth);
		tfDynamicFloors1.setWidth(strComponentWidth);
		tfDynamicFloors2.setWidth(strComponentWidth);
		
		//tenure/occupancy
		tfStatusofTenure.setWidth(strComponentWidth);
		slOwnedorRent.setWidth(strComponentWidth);
		tfNoOfYears.setWidth(strComponentWidth);
		tfRelationship.setWidth(strComponentWidth);
		tfDynamicTenure1.setWidth(strComponentWidth);
		tfDynamicTenure2.setWidth(strComponentWidth);
		//for area details
		tfSiteArea.setWidth(strComponentWidth);
		tfPlinthArea.setWidth(strComponentWidth);
		tfCarpetArea.setWidth(strComponentWidth);
		tfSalableArea.setWidth(strComponentWidth);
		tfRemarks.setWidth(strComponentWidth);
		tfRemarks.setHeight("95px");
		tfDynamicAreaDetail1.setWidth(strComponentWidth);
		tfDynamicAreaDetail2.setWidth(strComponentWidth);
		
		//for construction details
		slTypeProperty.setWidth(strComponentWidth);
		slTypeStructure.setWidth(strComponentWidth);
		tfYearConstruction.setWidth(strComponentWidth);
		tfNoFloors.setWidth(strComponentWidth);
		slExterior.setWidth(strComponentWidth);
		slInterior.setWidth(strComponentWidth);
		tfLifeAge.setWidth(strComponentWidth);
		tfFutureLife.setWidth(strComponentWidth);
		tfDetailsPlan.setWidth(strComponentWidth);
		slDeviation.setWidth(strComponentWidth);
		tfDtlsDeviation.setWidth(strComponentWidth);
		tfDynamicConstval1.setWidth(strComponentWidth);
		tfDynamicConstval2.setWidth(strComponentWidth);
		
		//for land valuation
		tfAreaofLand.setWidth(strComponentWidth);
		tfNorthandSouth.setWidth(strComponentWidth);
		tfMarketRate.setWidth(strComponentWidth);
		tfAdopetdMarketRate.setWidth(strComponentWidth);
		tfFairMarketRate.setWidth(strComponentWidth);
		tfGuidelineRate.setWidth(strComponentWidth);
		tfRealziableRate.setWidth(strComponentWidth);
		tfDistressRate.setWidth(strComponentWidth);
		tfDynamicValuation1.setWidth(strComponentWidth);
		tfDynamicValuation2.setWidth(strComponentWidth);
		
		
		
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

		lblParticular.setWidth(strWidth);
		lblPlinthArea.setWidth(strLblWidth);
		lblRoofHt.setWidth(strLblWidth);
		lblBuildAge.setWidth(strWidth);
		lblRate.setWidth(strWidth);
		lblReplace.setWidth(strWidth);
		lblDepreciation.setWidth(strWidth);
		lblNetvalue.setWidth(strWidth);
	
		//for construction
		tfStageofConst.setWidth(strComponentWidth);
		tfDynamicConstruction1.setWidth(strComponentWidth);
		tfDynamicConstruction2.setWidth(strComponentWidth);
		
		//for violation
		tfAnyViolation.setWidth(strComponentWidth);
		tfDynamicViolation1.setWidth(strComponentWidth);
		tfDynamicViolation2.setWidth(strComponentWidth);
	
		//for construction
		tfStageofConst.setNullRepresentation("");
		tfDynamicConstruction1.setNullRepresentation("");
		tfDynamicConstruction2.setNullRepresentation("");
				
		//for violation
		tfAnyViolation.setNullRepresentation("");
		tfDynamicViolation1.setNullRepresentation("");
		tfDynamicViolation2.setNullRepresentation("");
		
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
		
				slLandandBuilding.setNullSelectionAllowed(false);
				tfLandandBuilding.setNullRepresentation("");
				tfBuilding.setNullRepresentation("");
				slBuilding.setNullSelectionAllowed(false);
				tfPlanApprovedBy.setNullRepresentation("");
				tfDynamicPlanApproval1.setNullRepresentation("");
				slIsLicenceForced.setNullSelectionAllowed(false);
				slAllApprovalRecved.setNullSelectionAllowed(false);
				slConstAsperAppPlan.setNullSelectionAllowed(false);
				tfDynamicPlanApproval2.setNullRepresentation("");
				dfLicenseFrom.setNullRepresentation("");

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

		//for matching boundary
		slMatchingBoundary.setNullSelectionAllowed(false);
		slPlotDemarcated.setNullSelectionAllowed(false);
		slApproveLandUse.setNullSelectionAllowed(false);
		slTypeofProperty.setNullSelectionAllowed(false);
		tfDynamicmatching1.setNullRepresentation("");
		tfDynamicmatching2.setNullRepresentation("");
		
		//room
		tfNoofRooms.setNullRepresentation("");
		tfLivingDining.setNullRepresentation("");
		tfBedRooms.setNullRepresentation("");
		tfKitchen.setNullRepresentation("");
		tfToilets.setNullRepresentation("");
		tfDynamicRooms1.setNullRepresentation("");
		tfDynamicRooms2.setNullRepresentation("");
		
		//floor
		tfTotNoofFloors.setNullRepresentation("");
		tfPropertyLocated.setNullRepresentation("");
		tfApproxAgeofBuilding.setNullRepresentation("");
		tfResidualAgeofBuilding.setNullRepresentation("");
		slTypeofStructure.setNullSelectionAllowed(false);
		tfDynamicFloors1.setNullRepresentation("");
		tfDynamicFloors2.setNullRepresentation("");
		
		//tenure/occupancy
		tfStatusofTenure.setNullRepresentation("");
		slOwnedorRent.setNullSelectionAllowed(false);
		tfNoOfYears.setNullRepresentation("");
		tfRelationship.setNullRepresentation("");
		tfDynamicTenure1.setNullRepresentation("");
		tfDynamicTenure2.setNullRepresentation("");
		//for area details
		tfSiteArea.setNullRepresentation("");
		tfPlinthArea.setNullRepresentation("");
		tfCarpetArea.setNullRepresentation("");
		tfSalableArea.setNullRepresentation("");
		tfRemarks.setNullRepresentation("");
		tfDynamicAreaDetail1.setNullRepresentation("");
		tfDynamicAreaDetail2.setNullRepresentation("");
		
		//for construction details
		slTypeProperty.setNullSelectionAllowed(false);
		slTypeStructure.setNullSelectionAllowed(false);
		tfYearConstruction.setNullRepresentation("");
		tfNoFloors.setNullRepresentation("");
		slExterior.setNullSelectionAllowed(false);
		slInterior.setNullSelectionAllowed(false);
		tfLifeAge.setNullRepresentation("");
		tfFutureLife.setNullRepresentation("");
		tfDetailsPlan.setNullRepresentation("");
		slDeviation.setNullSelectionAllowed(false);
		tfDtlsDeviation.setNullRepresentation("");
		tfDynamicConstval1.setNullRepresentation("");
		tfDynamicConstval2.setNullRepresentation("");
		
		//for land valuation
		tfAreaofLand.setNullRepresentation("");
		tfNorthandSouth.setNullRepresentation("");
		tfMarketRate.setNullRepresentation("");
		tfAdopetdMarketRate.setNullRepresentation("");
		tfFairMarketRate.setNullRepresentation("");
		tfGuidelineRate.setNullRepresentation("");
		tfRealziableRate.setNullRepresentation("");
		tfDistressRate.setNullRepresentation("");
		tfDynamicValuation1.setNullRepresentation("");
		tfDynamicValuation2.setNullRepresentation("");
		
		tfOwnerName.setNullRepresentation("");
		tfOwnerAddress.setNullRepresentation("");
		tfOwnerAddress.setHeight("150px");
		tfLandMark.setNullRepresentation("");
		tfPropertyAddress.setNullRepresentation("");
		tfPropertyAddress.setHeight("130px");
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
		btnDynamicPlanApproval.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicPlanApproval.setStyleName(Runo.BUTTON_LINK);
		
		btnDynamicCustomer
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicCustomer.setStyleName(Runo.BUTTON_LINK);
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
		
		btnAddPlinth.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddPlinth.setStyleName(Runo.BUTTON_LINK);
		
		btnAddRoofHt.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddRoofHt.setStyleName(Runo.BUTTON_LINK);
		
		
		btnDynamicTenure.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicTenure.setStyleName(Runo.BUTTON_LINK);
		btnDynamicmatching.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicmatching.setStyleName(Runo.BUTTON_LINK);
		btnDynamicRooms.setStyleName(Runo.BUTTON_LINK);
		btnDynamicRooms.setIcon(new ThemeResource(Common.strAddIcon));
		
		btnDynamicFloor.setStyleName(Runo.BUTTON_LINK);
		btnDynamicFloor.setIcon(new ThemeResource(Common.strAddIcon));
		
		btnDynamicConstruction.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicConstruction.setStyleName(Runo.BUTTON_LINK);
		btnDynamicViolation.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicViolation.setStyleName(Runo.BUTTON_LINK);
		btnDynamicAreaDetail.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicAreaDetail.setStyleName(Runo.BUTTON_LINK);
		btnDynamicConstVal.setStyleName(Runo.BUTTON_LINK);
		btnDynamicConstVal.setIcon(new 
				ThemeResource(Common.strAddIcon));
		btnAddOwner.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnAddOwner.setStyleName(Runo.BUTTON_LINK);
		btnAddBuildSpec.setStyleName(Runo.BUTTON_LINK);
		btnAddBuildSpec.setIcon(new ThemeResource(Common.strAddIcon));
		
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
		
		btnDynamicValuation.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicValuation.setStyleName(Runo.BUTTON_LINK);
		
		btnAddGuideline.setStyleName(Runo.BUTTON_LINK);
		btnAddGuideline.setIcon(new ThemeResource(Common.strAddIcon));
		
		tfStatusofTenure.setValue(Common.strNA);
		tfRelationship.setValue(Common.strNA);
		tfPlinthArea.setValue(Common.strNil);
		tfCarpetArea.setValue(Common.strNil);
		tfRemarks.setValue(Common.strNil);
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
		if (btnAddOwner == event.getButton()) {

			layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("", ""));
		}

		if (btnAddPlinth == event.getButton()) {

			layoutPlintharea
					.addComponent(new ComponentIterPlinthArea("","",""));
		}
		if (btnAddGuideline == event.getButton()) {

			layoutGuideline
					.addComponent(new ComponentIterGuideline("","", "",""));
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
		if (btnAddRoofHt == event.getButton()) {
			layoutRoofHeight.addComponent(new ComponentIterRoofHeight("",""));
		}

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

		}
		if (btnAddBuildSpec == event.getButton()) {
			
			panelBuildSpecfication.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));
		}
		if (btnAddValDtls == event.getButton()) {
			
		ValuationDetailsList obj=new ValuationDetailsList();
		layoutValuationDetails.addComponent(new ComponenetIterValuationDetails(obj));
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

		if (btnDynamicCustomer == event.getButton()) {
			strSelectedPanel = "2";
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
	
		if (btnDynamicAreaDetail == event.getButton()) {
			strSelectedPanel = "8";
			showSubWindow();

		}
		if (btnDynamicValuation == event.getButton()) {
			strSelectedPanel = "9";
			showSubWindow();

		}
		if (btnDynamicRooms == event.getButton()) {
			strSelectedPanel = "10";
			showSubWindow();

		}
		if (btnDynamicFloor == event.getButton()) {
			strSelectedPanel = "11";
			showSubWindow();

		}
		if (btnDynamicConstVal == event.getButton()) {
			strSelectedPanel = "12";
			showSubWindow();

		}
		if (btnDynamicPlanApproval == event.getButton()) {
			strSelectedPanel = "13";
			showSubWindow();

		}
		if (btnDynamicConstruction == event.getButton()) {
			strSelectedPanel = "14";
			showSubWindow();

		}
		if (btnDynamicViolation == event.getButton()) {
			strSelectedPanel = "15";
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
					if (tfDynamicTenure1.isVisible()) {
						tfDynamicTenure2.setCaption(tfCaption.getValue());
						tfDynamicTenure2.setVisible(true);
					} else {
						tfDynamicTenure1.setCaption(tfCaption.getValue());
						tfDynamicTenure1.setVisible(true);
					}
			}
				else if (strSelectedPanel.equals("5")) {
					if (tfDynamicmatching1.isVisible()) {
						tfDynamicmatching2.setCaption(tfCaption.getValue());
						tfDynamicmatching2.setVisible(true);
					} else {
						tfDynamicmatching1.setCaption(tfCaption.getValue());
						tfDynamicmatching1.setVisible(true);
					}
			}
				else if (strSelectedPanel.equals("8")) {
					if (tfDynamicAreaDetail1.isVisible()) {
						tfDynamicAreaDetail2.setCaption(tfCaption.getValue());
						tfDynamicAreaDetail2.setVisible(true);
					} else {
						tfDynamicAreaDetail1.setCaption(tfCaption.getValue());
						tfDynamicAreaDetail1.setVisible(true);
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
					if (tfDynamicRooms1.isVisible()) {
						tfDynamicRooms2.setCaption(tfCaption.getValue());
						tfDynamicRooms2.setVisible(true);
					} else {
						tfDynamicRooms1.setCaption(tfCaption.getValue());
						tfDynamicRooms1.setVisible(true);
					}
			}
				else if (strSelectedPanel.equals("11")) {
					if (tfDynamicFloors1.isVisible()) {
						tfDynamicFloors2.setCaption(tfCaption.getValue());
						tfDynamicFloors2.setVisible(true);
					} else {
						tfDynamicFloors1.setCaption(tfCaption.getValue());
						tfDynamicFloors1.setVisible(true);
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
				else if (strSelectedPanel.equals("13")) {
					if (tfDynamicPlanApproval1.isVisible()) {
						tfDynamicPlanApproval2.setCaption(tfCaption.getValue());
						tfDynamicPlanApproval2.setVisible(true);
					} else {
						tfDynamicPlanApproval1.setCaption(tfCaption.getValue());
						tfDynamicPlanApproval1.setVisible(true);
					}
				}
				else if (strSelectedPanel.equals("14")) {
					if (tfDynamicConstruction1.isVisible()) {
						tfDynamicConstruction2.setCaption(tfCaption.getValue());
						tfDynamicConstruction2.setVisible(true);
					} else {
						tfDynamicConstruction1.setCaption(tfCaption.getValue());
						tfDynamicConstruction1.setVisible(true);
					}
			}
				else if (strSelectedPanel.equals("15")) {
					if (tfDynamicViolation1.isVisible()) {
						tfDynamicViolation2.setCaption(tfCaption.getValue());
						tfDynamicViolation2.setVisible(true);
					} else {
						tfDynamicViolation1.setCaption(tfCaption.getValue());
						tfDynamicViolation1.setVisible(true);
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
			btnCancel.setVisible(false);
			//saveExcel.setVisible(false);
			
			btnSubmit.setVisible(false);
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

		} else if (btnBack == event.getButton()) {
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
	/*	else if (btnView == event.getButton()) {

			StreamResource sr = getPDFStream();
			FileDownloader fileDownloader = new FileDownloader(sr);
			fileDownloader.extend(btnView);

		}*/

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
			populateAndConfig(false);
		}
	}

}
