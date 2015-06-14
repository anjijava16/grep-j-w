/**
 * File Name	:	SyndicateLandApp.java
 * Description	:	This class is used for add/edit Syndicate bank Flat details and generate report.
 * Author		:	Karthigadevi S
 * Date			:	March 18, 2014
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
import com.gnts.erputil.constants.ApplicationConstants;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.erputil.validations.DateValidation;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.txn.common.TPemCmAssetDetails;
import com.gnts.pem.domain.txn.common.TPemCmBldngCostofcnstructn;
import com.gnts.pem.domain.txn.common.TPemCmBldngOldSpec;
import com.gnts.pem.domain.txn.common.TPemCmBldngTechDetails;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.domain.txn.common.TPemCmFlatUnderValutn;
import com.gnts.pem.domain.txn.common.TPemCmOwnerDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropAdjoinDtls;
import com.gnts.pem.domain.txn.common.TPemCmPropDimension;
import com.gnts.pem.domain.txn.common.TPemCmPropDocDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropLegalDocs;
import com.gnts.pem.domain.txn.common.TPemCmPropOldPlanApprvl;
import com.gnts.pem.domain.txn.common.TPemCmPropValtnSummry;
import com.gnts.pem.domain.txn.synd.TPemSydPropMatchBoundry;
import com.gnts.pem.domain.txn.synd.TPemSynBldngRoom;
import com.gnts.pem.domain.txn.synd.TPemSynPropAreaDtls;
import com.gnts.pem.domain.txn.synd.TPemSynPropFloor;
import com.gnts.pem.domain.txn.synd.TPemSynPropOccupancy;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.txn.common.CmAssetDetailsService;
import com.gnts.pem.service.txn.common.CmBldngCostofcnstructnService;
import com.gnts.pem.service.txn.common.CmBldngOldSpecService;
import com.gnts.pem.service.txn.common.CmBldngTechDetailsService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;
import com.gnts.pem.service.txn.common.CmFlatUnderValutnService;
import com.gnts.pem.service.txn.common.CmOwnerDetailsService;
import com.gnts.pem.service.txn.common.CmPropAdjoinDtlsService;
import com.gnts.pem.service.txn.common.CmPropDimensionService;
import com.gnts.pem.service.txn.common.CmPropDocDetailsService;
import com.gnts.pem.service.txn.common.CmPropLegalDocsService;
import com.gnts.pem.service.txn.common.CmPropOldPlanApprvlService;
import com.gnts.pem.service.txn.common.CmPropValtnSummryService;
import com.gnts.pem.service.txn.synd.SydPropMatchBoundryService;
import com.gnts.pem.service.txn.synd.SynBldngRoomService;
import com.gnts.pem.service.txn.synd.SynPropAreaDtlsService;
import com.gnts.pem.service.txn.synd.SynPropFloorService;
import com.gnts.pem.service.txn.synd.SynPropOccupancyService;
import com.gnts.pem.util.UIFlowData;
import com.gnts.pem.util.XMLUtil;
import com.gnts.pem.util.iterator.ComponentIterBuildingSpecfication;
import com.gnts.pem.util.iterator.ComponentIterDimensionofPlot;
import com.gnts.pem.util.iterator.ComponentIterOwnerDetails;
import com.gnts.pem.util.iterator.ComponentIteratorAdjoinProperty;
import com.gnts.pem.util.iterator.ComponentIteratorLegalDoc;
import com.gnts.pem.util.iterator.ComponentIteratorNormlDoc;
import com.gnts.pem.util.list.AdjoinPropertyList;
import com.gnts.pem.util.list.BuildSpecList;
import com.gnts.pem.util.list.DimensionList;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
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
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
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
import com.vaadin.ui.themes.Runo;



public class SyndFlat implements ClickListener {
	
	
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
	private CmBldngTechDetailsService beanBuildDtls = (CmBldngTechDetailsService) SpringContextHelper
			.getBean("bldgTechDtls");
	private CmFlatUnderValutnService beanFlatValtn=(CmFlatUnderValutnService)SpringContextHelper
			.getBean("flatValtn");
	private CmBldngOldSpecService beanSpecBuilding = (CmBldngOldSpecService) SpringContextHelper
			.getBean("oldSpec");
	private CmBldngCostofcnstructnService beanConstValuation = (CmBldngCostofcnstructnService) SpringContextHelper
			.getBean("costOfConst");
	private SynPropAreaDtlsService beanareadetails = (SynPropAreaDtlsService) SpringContextHelper
			.getBean("synAreaDtls");
	private CmPropOldPlanApprvlService beanPlanApprvl=(CmPropOldPlanApprvlService) SpringContextHelper
			.getBean("oldPlanApprvl");
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
	private String strEvaluationNo = "SYN_FLAT_";
	private String strXslFile = "SyndFlat.xsl";
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
		
		private int count =0;
		private FileDownloader filedownloader;
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
	private ComboBox tfSearchBankBranch=new ComboBox("Bank Branch");
	private PopupDateField dfSearchEvalDate = new PopupDateField(
			"Evaluation Date");
	private TextField tfSearchCustomer=new TextField("Customer Name");
	private Button btnSearch = new Button("Search", this);
	private Button btnReset = new Button("Reset", this);
	private Button btnSubmit=new Button("Submit",this);
	
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
	//private Button saveExcel = new Button("Report");
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

		//Owner Details
		private VerticalLayout layoutOwnerDetails=new VerticalLayout();
		private GridLayout layoutOwnerDetails1=new GridLayout();
		private Button btnAddOwner=new Button("",this);

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
	private Label lblDimension = new Label("");
	private int itemDimensionNumber = 1;

	
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
	private ComboBox slTypeofStructures = new ComboBox("Type of structure");
	private TextField tfDynamicFloors1 = new TextField();
	private TextField tfDynamicFloors2 = new TextField();
	private Button btnDynamicFloor = new Button("", this);

	// area details of the property
	private VerticalLayout layoutAreaDetails = new VerticalLayout();
	private GridLayout layoutAreaDetails1 = new GridLayout();
	private TextField tfSiteArea = new TextField("Site Area");
	private TextField tfCarpetArea = new TextField("Carpet Area");
	private TextField tfSuperbuiltup = new TextField("Super built up area");
	private TextField tfSalableArea = new TextField("Salabale Area");
	private TextArea tfRemarks = new TextArea("Remarks");
	private TextField tfDynamicAreaDetail1 = new TextField();
	private TextField tfDynamicAreaDetail2 = new TextField();
	private Button btnDynamicAreaDetail = new Button("", this);

	// details of apartment building
	private VerticalLayout layoutApartmentBuilding = new VerticalLayout();
	private GridLayout layoutApartmentBuilding1 = new GridLayout();
	private VerticalLayout layoutAddress = new VerticalLayout();
	private ComboBox slNatureofApartment = new ComboBox(
			"Nature of the apartment");
	private TextField tfNameofApartment = new TextField("Name of the apartment");
	private TextArea tfPostalAddress = new TextArea("Postal Address");
	private CheckBox chkSamePostelAddress = new CheckBox(
			"Same as Property Address");
	private TextField tfApartmant = new TextField("Apartment");
	private TextField tfFlatNumber = new TextField("Flat No.");
	private TextField tfSFNumber = new TextField("S.F. No.");
	private TextField tfFloor = new TextField("Floor");
	private TextField tfVillageLoc = new TextField("Village");
	private TextField tfTalukLoc = new TextField("Taluk");
	private TextField tfDisrictLoc = new TextField("District");
	private ComboBox slDescriptionofLocality = new ComboBox(
			"Description of the locality");
	private TextField tfNoofFloors = new TextField("No. of Floors");
	private ComboBox slTypeofStructure = new ComboBox("Type of structure");
	private TextField tfNumberofFlats = new TextField("Number of flats");
	private TextField tfDynamicApartment1 = new TextField();
	private TextField tfDynamicApartment2 = new TextField();
	private Button btnDynamicApartment = new Button("", this);

	// flat under valuation
	private VerticalLayout layoutFlatValuation = new VerticalLayout();
	private GridLayout layoutFlatValuation1 = new GridLayout();
	private TextField tfFlatisSituated = new TextField(
			"The Floor in which the flat situated");
	private TextField tfDynamicFlatValuation1 = new TextField();
	private TextField tfDynamicFlatValuation2 = new TextField();
	private Button btnDynamicFlatValuation = new Button("", this);

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
	// BuildSpecfication
	private VerticalLayout panelBuildSpecfication = new VerticalLayout();
	private Button btnAddBuildSpec = new Button("", this);
	

	// valuation of under Construction
	private VerticalLayout layoutValuationConst = new VerticalLayout();
	private FormLayout layoutValuationConst1 = new FormLayout();
	private TextField tfUndividedShare = new TextField("Undivided share of land");
	private TextField tfSuperBuiltupArea = new TextField("Super bulit up area");
	private TextField tfCostofApartment = new TextField("Cost of apartment including undivided share of land covered car "
			+ "parking & statutory charges,as per agreement");
	private TextField tfRateofApartment = new TextField("Rate of apartment/sft");
	private ComboBox slIsRateReasonable = new ComboBox("Is the rate of apartment reasonable");
	private TextField tfStageOfConstruction = new TextField("Stage of construction as on date");
	private TextField tfCostOfConstAsAtSite = new TextField("Cost of construction including value of land as at site");
	private TextField tfCostOfConstAsPerAgree = new TextField("Cost of construction including the value of land as per the agreement");
	private TextField tfDynamicConstValuation1 = new TextField();
	private TextField tfDynamicConstValuation2 = new TextField();
	private Button btnDynamicConstValuation = new Button("", this);

	// commondata
	private Window mywindow = new Window("Enter Caption");
	private Button myButton = new Button("Ok", this);
	private TextField tfCaption = new TextField();
	private String strSelectedPanel;

	private String strComponentWidth = "200px";
	private Long selectedBankid;
	private String selectedFormName;
	private Long selectCompanyid;
	private String loginusername;

	// for report
	UIFlowData uiflowdata = new UIFlowData();
	private Logger logger = Logger.getLogger(SyndFlat.class);
	public SyndFlat() {
		
		loginusername = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		VerticalLayout clArgumentLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		selectedFormName=screenName;
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
tfCustomerName.setRequired(true);
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


		//for Owner Details
		layoutOwnerDetails.addComponent(btnAddOwner);
		layoutOwnerDetails.setComponentAlignment(btnAddOwner, Alignment.TOP_RIGHT);
		layoutOwnerDetails1.setColumns(4);
		layoutOwnerDetails1.addComponent(new ComponentIterOwnerDetails("",""));
		layoutOwnerDetails.addComponent(layoutOwnerDetails1);
		layoutOwnerDetails1.setSpacing(true);
		layoutOwnerDetails1.setMargin(true);
		// for adjoin properties
		panelAdjoinProperties.addComponent(btnAddAdjoinProperty);
		panelAdjoinProperties.setComponentAlignment(btnAddAdjoinProperty,
				Alignment.BOTTOM_RIGHT);
		panelAdjoinProperties.addComponent(new ComponentIteratorAdjoinProperty(
				null,true,true,true));

		// for dimensions
		panelDimension.addComponent(btnAddDimension);
		panelDimension.addComponent(lblDimension);
		lblDimension.setValue("Item No :" + itemDimensionNumber);
		panelDimension.setComponentAlignment(btnAddDimension,
				Alignment.BOTTOM_RIGHT);
		panelDimension.addComponent(new ComponentIterDimensionofPlot(null,true,true,true));

		panelNormalDocumentDetails.setWidth("100%");
		panelLegalDocumentDetails.setWidth("100%");

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
		layoutNoofFloors1.addComponent(slTypeofStructures);
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
		// area details of the property
		layoutAreaDetails1.setSpacing(true);
		layoutAreaDetails1.setColumns(4);
		layoutAreaDetails1.addComponent(tfSiteArea);
		layoutAreaDetails1.addComponent(tfCarpetArea);
		layoutAreaDetails1.addComponent(tfSuperbuiltup);
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

		// details of apartment building
		layoutApartmentBuilding1.setColumns(4);
		layoutApartmentBuilding1.addComponent(slNatureofApartment);
		layoutApartmentBuilding1.addComponent(tfNameofApartment);
		layoutApartmentBuilding1.addComponent(tfApartmant);
		layoutApartmentBuilding1.addComponent(tfFlatNumber);
		layoutApartmentBuilding1.addComponent(tfSFNumber);
		layoutApartmentBuilding1.addComponent(tfFloor);
		layoutApartmentBuilding1.addComponent(tfVillageLoc);
		layoutApartmentBuilding1.addComponent(tfTalukLoc);
		layoutApartmentBuilding1.addComponent(tfDisrictLoc);
		layoutApartmentBuilding1.addComponent(slDescriptionofLocality);
		layoutApartmentBuilding1.addComponent(tfNoofFloors);
		layoutApartmentBuilding1.addComponent(slTypeofStructure);
		layoutApartmentBuilding1.addComponent(tfNumberofFlats);
		layoutAddress.addComponent(tfPostalAddress);
		layoutAddress.addComponent(chkSamePostelAddress);
		layoutApartmentBuilding1.addComponent(layoutAddress);
		layoutApartmentBuilding1.addComponent(tfDynamicApartment1);
		layoutApartmentBuilding1.addComponent(tfDynamicApartment2);
		layoutApartmentBuilding1.setSpacing(true);
		tfDynamicApartment1.setVisible(false);
		tfDynamicApartment2.setVisible(false);
		layoutApartmentBuilding1.setMargin(true);
		chkSamePostelAddress.setImmediate(true);
		chkSamePostelAddress.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {

				if (chkSamePostelAddress.getValue().equals(true)) {
					tfPostalAddress.setValue(tfPropertyAddress.getValue());

				} else {
					tfPostalAddress.setValue("");
				}
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
		// flat under valuation
		layoutFlatValuation1.setColumns(4);
		layoutFlatValuation1.addComponent(tfFlatisSituated);
		layoutFlatValuation1.addComponent(tfDynamicFlatValuation1);
		layoutFlatValuation1.addComponent(tfDynamicFlatValuation2);
		tfDynamicFlatValuation1.setVisible(false);
		tfDynamicFlatValuation2.setVisible(false);
		layoutFlatValuation1.setSpacing(true);
		layoutFlatValuation1.setMargin(true);

		layoutFlatValuation.addComponent(btnDynamicFlatValuation);
		layoutFlatValuation.setComponentAlignment(btnDynamicFlatValuation,
				Alignment.TOP_RIGHT);
		layoutFlatValuation.addComponent(layoutFlatValuation1);

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


		// for Build Specification
		panelBuildSpecfication.addComponent(btnAddBuildSpec);
		panelBuildSpecfication.setComponentAlignment(btnAddBuildSpec,
				Alignment.BOTTOM_RIGHT);
		panelBuildSpecfication
				.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));
		panelBuildSpecfication.setWidth("100%");

		// for
		layoutApartmentBuilding.addComponent(btnDynamicApartment);
		layoutApartmentBuilding.setComponentAlignment(btnDynamicApartment,
				Alignment.TOP_RIGHT);
		layoutApartmentBuilding.addComponent(layoutApartmentBuilding1);

		// valuation of land
		layoutValuationConst.setSpacing(true);
		layoutValuationConst1.setSpacing(true);
		layoutValuationConst1.addComponent(tfUndividedShare);
		layoutValuationConst1.addComponent(tfSuperBuiltupArea);
		layoutValuationConst1.addComponent(tfCostofApartment);
		layoutValuationConst1.addComponent(tfRateofApartment);
		layoutValuationConst1.addComponent(slIsRateReasonable);
		layoutValuationConst1.addComponent(tfStageOfConstruction);
		layoutValuationConst1.addComponent(tfCostOfConstAsAtSite);
		layoutValuationConst1.addComponent(tfCostOfConstAsPerAgree);
					
		layoutValuationConst1.addComponent(tfDynamicConstValuation1);
		layoutValuationConst1.addComponent(tfDynamicConstValuation2);
		tfDynamicConstValuation1.setVisible(false);
		tfDynamicConstValuation2.setVisible(false);

		layoutValuationConst.addComponent(btnDynamicConstValuation);
		layoutValuationConst.setComponentAlignment(btnDynamicConstValuation,
				Alignment.TOP_RIGHT);
		layoutValuationConst.addComponent(layoutValuationConst1);
		layoutValuationConst1.setMargin(true);

		// add components in main panel
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
		accordion.addTab(PanelGenerator
				.createPanel(panelDimension),"Dimensions");
		layoutmachingBoundary.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutmachingBoundary),"Matching of Boundaries And Tenure/Occupancy Details");
		layoutNoofRooms.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutNoofRooms),"No. of Rooms/No. of Floors");
		layoutAreaDetails.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutAreaDetails),"Area Details of the Property");
		layoutApartmentBuilding.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutApartmentBuilding),"Details of Apartment Building-Under Construction");
		layoutFlatValuation.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutFlatValuation),"Flat Under Valuation");
		layoutPlanApproval.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutPlanApproval),"Details of Plan Approval");
		panelBuildSpecfication.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(panelBuildSpecfication),"Specification");
		layoutValuationConst.setStyleName("bluebar");
		accordion.addTab(PanelGenerator
				.createPanel(layoutValuationConst),"Valuation of under construction");
		
		layoutMainForm.addComponent(PanelGenerator
				.createPanel(accordion));

		layoutMainForm.setMargin(true);
		layoutMainForm.setSpacing(true);
		// for main panel
		layoutButton2.setSpacing(true);
		btnSave.setStyleName("savebt");
		btnCancel.setStyleName("cancelbt");
	//	saveExcel.addStyleName("downloadbt");
		btnSubmit.setStyleName("submitbt");
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

		
		btnSave.setVisible(false);
		btnCancel.setVisible(false);
		btnSubmit.setVisible(false);
		//saveExcel.setVisible(false);
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
				
		mainPanel.addComponent(layoutMainForm);
		mainPanel.setVisible(false);

		
		// for search panel
		// for search panel
        // Added by Hohul ----->  For Search Panel Layouts
			FormLayout flSearchEvalNumber = new FormLayout();
			flSearchEvalNumber.addComponent(tfSearchEvalNumber);
			
			
			FormLayout flSearchBankbranch = new FormLayout();
			flSearchBankbranch.addComponent(tfSearchBankBranch);
			
			
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
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
	                                /**
									 * 
									 */
									private static final long serialVersionUID = 1L;

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
		
		tfSearchBankBranch.setImmediate(true);
		tfSearchBankBranch.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			Filter filter = null;

			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				
				try{
				String strBankBranch = tfSearchBankBranch.getValue().toString();
				Filterable f = (Filterable) tblEvalDetails
						.getContainerDataSource();
				if (filter != null)
					f.removeContainerFilter(filter);

				
				  filter = new Compare.Equal("bankBranch",strBankBranch );
	                f.addContainerFilter(filter);
	                total=f.size();
					tblEvalDetails.setColumnFooter("lastUpdateDt","No. of Records:"+total);
				f.addContainerFilter(filter);
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
						Alignment.MIDDLE_CENTER);
				hlNotificationLayout.addComponent(lblSaveNotification);
				hlNotificationLayout.setComponentAlignment(lblSaveNotification,
						Alignment.MIDDLE_CENTER);
				hlHeaderLayout.addComponent(lblFormTittle);
				hlHeaderLayout.setComponentAlignment(lblFormTittle,
						Alignment.MIDDLE_LEFT);
				hlHeaderLayout.addComponent(hlBreadCrumbs);
				hlHeaderLayout.setComponentAlignment(hlBreadCrumbs,
						Alignment.MIDDLE_LEFT);
				hlHeaderLayout.addComponent(hlNotificationLayout);
				hlHeaderLayout.setComponentAlignment(hlNotificationLayout,
						Alignment.MIDDLE_CENTER);
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
				String bankbranch=(String)tfSearchBankBranch.getValue();
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
			boolean valid = false;
			TPemCmEvalDetails evalobj = new TPemCmEvalDetails();
			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="FLAT";
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
			uiflowdata.setEvalDtls(evalobj);
			String numberOnly = tfCostOfConstAsAtSite.getValue().replaceAll("[^\\d.]", "");
			uiflowdata.setAmountInWords(beanEvaluation
					.getAmountInWords(numberOnly));
			if( numberOnly.trim().length()==0){
				numberOnly="0";	
			}
			evalobj.setPropertyValue(Double.valueOf(numberOnly));
	
				
			if(tfEvaluationNumber.isValid() && slBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid())
			{
				
				if(count ==0){
				beanEvaluation.saveorUpdateEvalDetails(evalobj);
				
				valid = true;
				}
				lblNotificationIcon.setIcon(new ThemeResource(
						"img/success_small.png"));
				lblSaveNotification.setValue("Submitted Successfully");
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
				 
				logger.info("Error-->" + e);
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
				saveAreaDetailsofProperty();
			} catch (Exception e) {
			}
			try {
				saveDetailsofApartment();
			} catch (Exception e) {
			}
			try {
				saveFlatValuation();
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
				saveConstValuationDetails();
			} catch (Exception e) {
				 
			}
			try {
				savePropertyValueDetails();
			} catch (Exception e) {
			}
			try {
				
				uiflowdata.setPropDesc((String)slPropertyDesc.getValue());
				uiflowdata.setPropertyAddress(tfPropertyAddress.getValue());
				uiflowdata.setEvalnumber(tfEvaluationNumber.getValue());
				uiflowdata.setBankBranch((String)slBankBranch.getValue());
				uiflowdata.setCustomername(tfCustomerName.getValue());
				uiflowdata.setGuidelinevalue(XMLUtil.IndianFormat(new BigDecimal(tfCostOfConstAsAtSite.getValue())));
				uiflowdata.setMarketValue(XMLUtil.IndianFormat(new BigDecimal(tfCostOfConstAsPerAgree.getValue())));
				
				if (dfDateofValuation.getValue() != null) {
					SimpleDateFormat dt1 = new SimpleDateFormat("dd-MMM-yyyy");
					uiflowdata.setInspectionDate(dt1.format(dfDateofValuation
							.getValue()));
				}
				uiflowdata.setPropDesc((String) slPropertyDesc.getValue());
				String numberOnly = tfCostOfConstAsAtSite.getValue().replaceAll("[^\\d.]", "");
				uiflowdata.setAmountInWords(beanEvaluation
						.getAmountInWords(numberOnly));
				//Bill
			} catch (Exception e) {
			}

			evalNumber=tfEvaluationNumber.getValue();
			customer=tfCustomerName.getValue();
			propertyType="FLAT";
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
		obj.setFieldLabel(slTypeofStructures.getCaption());
		obj.setFieldValue((String) slTypeofStructures.getValue());
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
		obj.setFieldLabel(tfCarpetArea.getCaption());
		obj.setFieldValue((String) tfCarpetArea.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanareadetails.saveorUpdateSynPropAreaDtls(obj);
		uiflowdata.getAreaDetails().add(obj);

		obj = new TPemSynPropAreaDtls();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfSuperBuiltupArea.getCaption());
		obj.setFieldValue((String) tfSuperBuiltupArea.getValue());
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

	private void saveDetailsofApartment() {

		try {
			beanBuildDtls.deleteExistingBldngTechDtls(headerid);
		} catch (Exception e) {

		}

		TPemCmBldngTechDetails obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slNatureofApartment.getCaption());
		obj.setFieldValue((String) slNatureofApartment.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfNameofApartment.getCaption());
		obj.setFieldValue((String) tfNameofApartment.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfPostalAddress.getCaption());
		obj.setFieldValue((String) tfPostalAddress.getValue());
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfApartmant.getCaption());
		obj.setFieldValue((String) tfApartmant.getValue());
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFlatNumber.getCaption());
		obj.setFieldValue((String) tfFlatNumber.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfSFNumber.getCaption());
		obj.setFieldValue((String) tfSFNumber.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFloor.getCaption());
		obj.setFieldValue((String) tfFloor.getValue());
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfVillageLoc.getCaption());
		obj.setFieldValue((String) tfVillageLoc.getValue());
		obj.setOrderNo(8L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfTalukLoc.getCaption());
		obj.setFieldValue((String) tfTalukLoc.getValue());
		obj.setOrderNo(9L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDisrictLoc.getCaption());
		obj.setFieldValue((String) tfDisrictLoc.getValue());
		obj.setOrderNo(10L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slDescriptionofLocality.getCaption());
		obj.setFieldValue((String) slDescriptionofLocality.getValue());
		obj.setOrderNo(11L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfNoofFloors.getCaption());
		obj.setFieldValue((String) tfNoofFloors.getValue());
		obj.setOrderNo(12L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(slTypeofStructure.getCaption());
		obj.setFieldValue((String) slTypeofStructure.getValue());
		obj.setOrderNo(13L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		obj = new TPemCmBldngTechDetails();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfNumberofFlats.getCaption());
		obj.setFieldValue((String) tfNumberofFlats.getValue());
		obj.setOrderNo(14L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanBuildDtls.saveorUpdateBldngTechDetails(obj);
		uiflowdata.getBuildingDtls().add(obj);

		if (tfDynamicApartment1.getValue() != null
				&& tfDynamicApartment1.getValue().trim().length() > 0) {
			obj = new TPemCmBldngTechDetails();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicApartment1.getCaption());
			obj.setFieldValue((String) tfDynamicApartment1.getValue());
			obj.setOrderNo(15L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanBuildDtls.saveorUpdateBldngTechDetails(obj);
			uiflowdata.getBuildingDtls().add(obj);
		}
		if (tfDynamicApartment2.getValue() != null
				&& tfDynamicApartment2.getValue().trim().length() > 0) {
			obj = new TPemCmBldngTechDetails();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicApartment2.getCaption());
			obj.setFieldValue((String) tfDynamicApartment2.getValue());
			obj.setOrderNo(16L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanBuildDtls.saveorUpdateBldngTechDetails(obj);
			uiflowdata.getBuildingDtls().add(obj);
		}

	}

	private void saveFlatValuation() {
		try {
			beanFlatValtn.deleteExistingFlatUnderValutn(headerid);
		} catch (Exception e) {

		}

		TPemCmFlatUnderValutn obj = new TPemCmFlatUnderValutn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfFlatisSituated.getCaption());
		obj.setFieldValue((String) tfFlatisSituated.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanFlatValtn.saveorUpdateFlatUnderValutn(obj);
		uiflowdata.getFlatValuation().add(obj);

		if (tfDynamicFlatValuation1.getValue() != null
				&& tfDynamicFlatValuation1.getValue().trim().length() > 0) {
			obj = new TPemCmFlatUnderValutn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicFlatValuation1.getCaption());
			obj.setFieldValue((String) tfDynamicFlatValuation1.getValue());
			obj.setOrderNo(2L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanFlatValtn.saveorUpdateFlatUnderValutn(obj);
			uiflowdata.getFlatValuation().add(obj);
		}
		if (tfDynamicFlatValuation2.getValue() != null
				&& tfDynamicFlatValuation2.getValue().trim().length() > 0) {
			obj = new TPemCmFlatUnderValutn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicFlatValuation2.getCaption());
			obj.setFieldValue((String) tfDynamicFlatValuation2.getValue());
			obj.setOrderNo(3L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanFlatValtn.saveorUpdateFlatUnderValutn(obj);
			uiflowdata.getFlatValuation().add(obj);
		}

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
		obj.setFieldLabel("Are all approvals required are received");
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

	void saveConstValuationDetails() {
		try {
			beanConstValuation.deleteExistingCostofcnstructn(headerid);
		} catch (Exception e) {

		}

		TPemCmBldngCostofcnstructn obj = new TPemCmBldngCostofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfUndividedShare.getCaption());
		obj.setFieldValue((String)tfUndividedShare.getValue());
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstValuation.saveorUpdateCostofcnstructnDetails(obj);
		uiflowdata.getConstValuation().add(obj);

		obj = new TPemCmBldngCostofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfSuperBuiltupArea.getCaption());
		obj.setFieldValue((String)tfSuperBuiltupArea.getValue());
		obj.setOrderNo(2L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstValuation.saveorUpdateCostofcnstructnDetails(obj);
		uiflowdata.getConstValuation().add(obj);
		
		obj = new TPemCmBldngCostofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfCostofApartment.getCaption());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfCostofApartment.getValue())));
		obj.setOrderNo(3L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstValuation.saveorUpdateCostofcnstructnDetails(obj);
		
		obj = new  TPemCmBldngCostofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfRateofApartment.getCaption());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfRateofApartment.getValue())));
		obj.setOrderNo(4L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstValuation.saveorUpdateCostofcnstructnDetails(obj);
		uiflowdata.getConstValuation().add(obj);

		obj = new TPemCmBldngCostofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(slIsRateReasonable.getCaption());
		obj.setFieldValue((String)slIsRateReasonable.getValue());
		obj.setOrderNo(5L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstValuation.saveorUpdateCostofcnstructnDetails(obj);
		uiflowdata.getConstValuation().add(obj);
		
		obj = new TPemCmBldngCostofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfStageOfConstruction.getCaption());
		obj.setFieldValue((String)tfStageOfConstruction.getValue());
		obj.setOrderNo(6L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstValuation.saveorUpdateCostofcnstructnDetails(obj);
		uiflowdata.getConstValuation().add(obj);
		
		obj = new TPemCmBldngCostofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfCostOfConstAsAtSite.getCaption());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfCostOfConstAsAtSite.getValue())));
		obj.setOrderNo(7L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstValuation.saveorUpdateCostofcnstructnDetails(obj);
		uiflowdata.getConstValuation().add(obj);
		
		obj = new TPemCmBldngCostofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfCostOfConstAsPerAgree.getCaption());
		obj.setFieldValue("Rs. "+XMLUtil.IndianFormat(new BigDecimal(tfCostOfConstAsPerAgree.getValue())));
		obj.setOrderNo(8L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstValuation.saveorUpdateCostofcnstructnDetails(obj);
		uiflowdata.getConstValuation().add(obj);
		
		if(tfDynamicConstValuation1.getValue()!=null&&tfDynamicConstValuation1.getValue().trim().length()>0){
		obj = new TPemCmBldngCostofcnstructn();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfDynamicConstValuation1.getCaption());
		obj.setFieldValue((String)tfDynamicConstValuation1.getValue());
		obj.setOrderNo(9L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanConstValuation.saveorUpdateCostofcnstructnDetails(obj);
		uiflowdata.getConstValuation().add(obj);
		}

		if(tfDynamicConstValuation2.getValue()!=null&&tfDynamicConstValuation2.getValue().trim().length()>0){
			obj = new TPemCmBldngCostofcnstructn();
			obj.setDocId(headerid);
			obj.setFieldLabel(tfDynamicConstValuation2.getCaption());
			obj.setFieldValue((String)tfDynamicConstValuation2.getValue());
			obj.setOrderNo(10L);
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(loginusername);
			beanConstValuation.saveorUpdateCostofcnstructnDetails(obj);
			uiflowdata.getConstValuation().add(obj);
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
	void savePropertyValueDetails(){
		try{
			beanPropertyvalue.deleteExistingPropValtnSummry(headerid);
		}catch(Exception e){
			
		}
		TPemCmPropValtnSummry obj=new TPemCmPropValtnSummry();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfCostOfConstAsAtSite.getCaption());
		obj.setFieldValue(XMLUtil.IndianFormat(new BigDecimal(tfCostOfConstAsAtSite.getValue())));
		obj.setOrderNo(1L);
		obj.setLastUpdatedDt(new Date());
		obj.setLastUpdatedBy(loginusername);
		beanPropertyvalue.saveorUpdatePropValtnSummry(obj);
		uiflowdata.getPropertyValue().add(obj);
		
		obj=new TPemCmPropValtnSummry();
		obj.setDocId(headerid);
		obj.setFieldLabel(tfCostOfConstAsPerAgree.getCaption());
		obj.setFieldValue(XMLUtil.IndianFormat(new BigDecimal(tfCostOfConstAsPerAgree.getValue())));
		obj.setOrderNo(2L);
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
				
				tfEvaluationNumber.setValue((String) itselect.getItemProperty(
						"evalNo").getValue());
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
			logger.info("Error-->" + e);
		}

		try {
			// for customer details
			editCustomerDetails();
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
			editDetailsofApartment();
		} catch (Exception e) {
			
		}
		try {
			editFlatValuation();
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
			editConstValuationDetails();
		} catch (Exception e) {
			
		}

	}

	void editCustomerDetails() {
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
			logger.info("Error-->" + e);
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
			logger.info("Error-->" + e);
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
			logger.info("Error-->" + e);
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
			logger.info("Error-->" + e);
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

			logger.info("Error-->" + e);
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
			slTypeofStructures.setValue(obj1.getFieldValue());
			slTypeofStructures.setCaption(obj1.getFieldLabel());
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
			tfCarpetArea.setValue(obj1.getFieldValue());
			tfCarpetArea.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfSuperbuiltup.setValue(obj1.getFieldValue());
			tfSuperbuiltup.setCaption(obj1.getFieldLabel());
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

	private void editDetailsofApartment() {
		try {
			List<TPemCmBldngTechDetails> list = beanBuildDtls.getBldgTechDtlsList(headerid);
			TPemCmBldngTechDetails obj1 = list.get(0);
			slNatureofApartment.setValue(obj1.getFieldValue());
			slNatureofApartment.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfNameofApartment.setValue(obj1.getFieldValue());
			tfNameofApartment.setCaption(obj1.getFieldLabel());
			obj1 = list.get(2);
			tfPostalAddress.setValue(obj1.getFieldValue());
			tfPostalAddress.setCaption(obj1.getFieldLabel());
			obj1 = list.get(3);
			tfApartmant.setValue(obj1.getFieldValue());
			tfApartmant.setCaption(obj1.getFieldLabel());
			obj1 = list.get(4);
			tfFlatNumber.setValue(obj1.getFieldValue());
			tfFlatNumber.setCaption(obj1.getFieldLabel());
			obj1 = list.get(5);
			tfSFNumber.setValue(obj1.getFieldValue());
			tfSFNumber.setCaption(obj1.getFieldLabel());
			obj1 = list.get(6);
			tfFloor.setValue(obj1.getFieldValue());
			tfFloor.setCaption(obj1.getFieldLabel());
			obj1 = list.get(7);
			tfVillageLoc.setValue(obj1.getFieldValue());
			tfVillageLoc.setCaption(obj1.getFieldLabel());
			obj1 = list.get(8);
			tfTalukLoc.setValue(obj1.getFieldValue());
			tfTalukLoc.setCaption(obj1.getFieldLabel());
			obj1 = list.get(9);
			tfDisrictLoc.setValue(obj1.getFieldValue());
			tfDisrictLoc.setCaption(obj1.getFieldLabel());
			obj1 = list.get(10);
			slDescriptionofLocality.setValue(obj1.getFieldValue());
			slDescriptionofLocality.setCaption(obj1.getFieldLabel());
			obj1 = list.get(11);
			tfNoofFloors.setValue(obj1.getFieldValue());
			tfNoofFloors.setCaption(obj1.getFieldLabel());
			obj1 = list.get(12);
			slTypeofStructure.setValue(obj1.getFieldValue());
			slTypeofStructure.setCaption(obj1.getFieldLabel());
			obj1 = list.get(13);
			tfNumberofFlats.setValue(obj1.getFieldValue());
			tfNumberofFlats.setCaption(obj1.getFieldLabel());
			obj1 = list.get(14);
			tfDynamicApartment1.setValue(obj1.getFieldValue());
			tfDynamicApartment1.setCaption(obj1.getFieldLabel());
			tfDynamicApartment1.setVisible(true);
			obj1 = list.get(15);
			tfDynamicApartment2.setValue(obj1.getFieldValue());
			tfDynamicApartment2.setCaption(obj1.getFieldLabel());
			tfDynamicApartment2.setVisible(true);

		} catch (Exception e) {

			
		}
	}

	private void editFlatValuation() {
		try {
			List<TPemCmFlatUnderValutn> list = beanFlatValtn.getFlatUnderValutnList(headerid);
			TPemCmFlatUnderValutn obj1 = list.get(0);
			tfFlatisSituated.setValue(obj1.getFieldValue());
			tfFlatisSituated.setCaption(obj1.getFieldLabel());
			obj1 = list.get(1);
			tfDynamicFlatValuation1.setValue(obj1.getFieldValue());
			tfDynamicFlatValuation1.setCaption(obj1.getFieldLabel());
			tfDynamicFlatValuation1.setVisible(true);
			obj1 = list.get(2);
			tfDynamicFlatValuation2.setValue(obj1.getFieldValue());
			tfDynamicFlatValuation2.setCaption(obj1.getFieldLabel());
			tfDynamicFlatValuation2.setVisible(true);
		} catch (Exception e) {
			
		}

	}

	private void editPlanApprovalDetails() {
		try {
			List<TPemCmPropOldPlanApprvl> list= beanPlanApprvl.getPropOldPlanApprvlList(headerid);
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
	void editConstValuationDetails() {
		try {
			List<TPemCmBldngCostofcnstructn> list = beanConstValuation.getCostofcnstructnList(headerid);
			TPemCmBldngCostofcnstructn obj1 = list.get(0);
			 tfUndividedShare.setValue(obj1.getFieldValue());
			 tfUndividedShare.setCaption(obj1.getFieldLabel());
			 obj1 = list.get(1); 
			 tfSuperBuiltupArea.setValue(obj1.getFieldValue());
			 tfSuperBuiltupArea.setCaption(obj1.getFieldLabel()); 
			 obj1 = list.get(2); 
			 tfCostofApartment.setValue(obj1.getFieldValue().replace("Rs. ",""));
			 tfCostofApartment.setCaption(obj1.getFieldLabel()); 
			 obj1 = list.get(3); 
			 tfRateofApartment.setValue(obj1.getFieldValue().replace("Rs. ",""));
			 tfRateofApartment.setCaption(obj1.getFieldLabel()); 
			 obj1 = list.get(4); 
			 slIsRateReasonable.setValue(obj1.getFieldValue());
			 slIsRateReasonable.setCaption(obj1.getFieldLabel());
			 obj1 = list.get(5);
			tfStageOfConstruction.setValue(obj1.getFieldValue());
			tfStageOfConstruction.setCaption(obj1.getFieldLabel());
			obj1 = list.get(6);
			tfCostOfConstAsAtSite.setValue(obj1.getFieldValue().replace("Rs. ",""));
			tfCostOfConstAsAtSite.setCaption(obj1.getFieldLabel());
			obj1 = list.get(7);
			tfCostOfConstAsPerAgree.setValue(obj1.getFieldValue().replace("Rs. ",""));
			tfCostOfConstAsPerAgree.setCaption(obj1.getFieldLabel());
			obj1 = list.get(8);
			tfDynamicConstValuation1.setValue(obj1.getFieldValue());
			tfDynamicConstValuation1.setCaption(obj1.getFieldLabel());
			tfDynamicConstValuation1.setVisible(true);
			obj1 = list.get(9);
			tfDynamicConstValuation2.setValue(obj1.getFieldValue());
			tfDynamicConstValuation2.setCaption(obj1.getFieldLabel());
			tfDynamicConstValuation2.setVisible(true);

		} catch (Exception e) {

			
		}
	}

	void loadComponentListValues() {
		loadPropertyDescList();

		slMatchingBoundary.addItem(Common.YES_DESC);
		slMatchingBoundary.addItem(Common.NO_DESC);
		slPlotDemarcated.addItem(Common.YES_DESC);
		slPlotDemarcated.addItem(Common.NO_DESC);
		slIsLicenceForced.addItem(Common.YES_DESC);
		slIsLicenceForced.addItem(Common.NO_DESC);
		slAllApprovalRecved.addItem(Common.YES_DESC);
		slAllApprovalRecved.addItem(Common.NO_DESC);
		slConstAsperAppPlan.addItem(Common.YES_DESC);
		slConstAsperAppPlan.addItem(Common.NO_DESC);
		slIsRateReasonable.addItem(Common.YES_DESC);
		slIsRateReasonable.addItem(Common.NO_DESC);
		
		loadTypeofProperty();
		loadOwnedorRented();
		loadTypeofStructureList();
		loadBankBranchDetails();
	}
	void loadBankBranchDetails(){
		List<String> list = beanBankConst.getBankConstantList("BRANCH_CODE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slBankBranch.setContainerDataSource(childAccounts);
		tfSearchBankBranch.setContainerDataSource(childAccounts);
	}
	void loadPropertyDescList() {
		List<String> list = beanBankConst.getBankConstantList("PROP_DESC",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slPropertyDesc.setContainerDataSource(childAccounts);
	}

	void loadTypeofStructureList() {
		List<String> list = beanBankConst.getBankConstantList("STRUCTURE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slTypeofStructure.setContainerDataSource(childAccounts);
		slTypeofStructures.setContainerDataSource(childAccounts);
	}

	void loadTypeofProperty() {
		List<String> list =  beanBankConst.getBankConstantList("RIC",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		slApproveLandUse.setContainerDataSource(childAccounts);
		slTypeofProperty.setContainerDataSource(childAccounts);
		slNatureofApartment.setContainerDataSource(childAccounts);
		slDescriptionofLocality.setContainerDataSource(childAccounts);
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
		slBankBranch.setComponentError(null);
		dfDateofValuation.setComponentError(null);
		tfEvaluationPurpose.setComponentError(null);
		tfCustomerName.setComponentError(null);
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
		//slBankBranch.setInputPrompt(Common.SELECT_PROMPT);

		tfDynamicEvaluation1.setVisible(false);
		tfDynamicEvaluation2.setVisible(false);

		//tfCustomerName.setValue("Sri.");
		slPropertyDesc.setValue(null);
		tfCustomerAddr.setValue("");
		tfPropertyAddress.setValue("");
		tfLandMark.setValue("");
		tfDynamicAsset1.setValue("");
		tfDynamicAsset2.setValue("");
		tfDynamicAsset1.setVisible(false);
		tfDynamicAsset2.setVisible(false);

		//slPropertyDesc.setInputPrompt(Common.SELECT_PROMPT);
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
		itemDimensionNumber = 1;
		panelDimension.addComponent(btnAddDimension);
		panelDimension.addComponent(lblDimension);
		lblDimension.setValue("Item No :" + itemDimensionNumber);
		panelDimension.setComponentAlignment(btnAddDimension,
				Alignment.BOTTOM_RIGHT);
		panelDimension.addComponent(new ComponentIterDimensionofPlot(null,true,true,true));

		// matching
		slMatchingBoundary.setValue(null);
		slPlotDemarcated.setValue(null);
		slApproveLandUse.setValue(null);
		slTypeofProperty.setValue(null);
		tfDynamicmatching1.setValue("");
		tfDynamicmatching2.setValue("");
		tfDynamicmatching1.setVisible(false);
		tfDynamicmatching2.setVisible(false);

		// no of rooms
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
		slTypeofStructures.setValue("");
		tfDynamicFloors1.setValue("");
		tfDynamicFloors2.setValue("");
		tfDynamicFloors1.setVisible(false);
		tfDynamicFloors2.setVisible(false);
		
	//slTypeofStructures.setInputPrompt(Common.SELECT_PROMPT);

		// tenure and occupancy details
		tfStatusofTenure.setValue("");
		slOwnedorRent.setValue(null);
		tfNoOfYears.setValue("");
		tfRelationship.setValue("");
		tfDynamicTenure1.setValue("");
		tfDynamicTenure2.setValue("");
		tfDynamicTenure1.setVisible(false);
		tfDynamicTenure2.setVisible(false);
		slOwnedorRent.setInputPrompt(Common.SELECT_PROMPT);
		// area details
		tfSiteArea.setValue("");
		tfSuperbuiltup.setValue("");
		tfCarpetArea.setValue("");
		tfSalableArea.setValue("");
		tfRemarks.setValue("");
		tfDynamicAreaDetail1.setValue("");
		tfDynamicAreaDetail2.setValue("");
		tfDynamicAreaDetail1.setVisible(false);
		tfDynamicAreaDetail2.setVisible(false);

		// details of apartment
		slNatureofApartment.setValue(null);
		tfNameofApartment.setValue("");
		tfPostalAddress.setValue("");
		tfApartmant.setValue("");
		tfFlatNumber.setValue("");
		tfSFNumber.setValue("");
		tfFloor.setValue("");
		tfVillageLoc.setValue("");
		tfTalukLoc.setValue("");
		tfDisrictLoc.setValue("");
		slDescriptionofLocality.setValue(null);
		tfNoofFloors.setValue("");
		slTypeofStructure.setValue(null);
		tfNumberofFlats.setValue("");
		tfDynamicApartment1.setValue("");
		tfDynamicApartment2.setValue("");
		tfDynamicApartment1.setVisible(false);
		tfDynamicApartment2.setVisible(false);
	/*	slDescriptionofLocality.setInputPrompt(Common.SELECT_PROMPT);
		slTypeofStructure.setInputPrompt(Common.SELECT_PROMPT);
		slTypeofStructures.setInputPrompt(Common.SELECT_PROMPT);*/
		// for flat valuation
		tfFlatisSituated.setValue("");
		tfDynamicFlatValuation1.setValue("");
		tfDynamicFlatValuation2.setValue("");
		tfDynamicFlatValuation1.setVisible(false);
		tfDynamicFlatValuation2.setVisible(false);

		// details of plan approval
		tfLandandBuilding.setValue("");
		slLandandBuilding.setValue(null);
		slBuilding.setValue(null);
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
		
	/*	slLandandBuilding.setInputPrompt(Common.SELECT_PROMPT);
		slBuilding.setInputPrompt(Common.SELECT_PROMPT);*/

		// for buildspecification

		panelBuildSpecfication.removeAllComponents();
		
		panelBuildSpecfication.addComponent(btnAddBuildSpec);
		panelBuildSpecfication.setComponentAlignment(btnAddBuildSpec,
				Alignment.BOTTOM_RIGHT);
		panelBuildSpecfication
				.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));

		// for valuation of under construction
		tfUndividedShare.setValue("");
		tfSuperBuiltupArea.setValue("");
		tfCostofApartment.setValue("0");
		tfRateofApartment.setValue("0");
		slIsRateReasonable.setValue(null);
		tfStageOfConstruction.setValue("");
		tfCostOfConstAsAtSite.setValue("0");
		tfCostOfConstAsPerAgree.setValue("0");
		tfDynamicConstValuation1.setValue("");
		tfDynamicConstValuation2.setValue("");
		tfDynamicConstValuation1.setVisible(false);
		tfDynamicConstValuation2.setVisible(false);
		
		// for default values
		tfStatusofTenure.setValue(Common.strNA);
		tfRelationship.setValue(Common.strNA);
		tfSuperbuiltup.setValue("");
		tfCarpetArea.setValue(Common.strNil);
		tfRemarks.setValue(Common.strNil);
		accordion.setSelectedTab(0);
		//for default yes value
		slMatchingBoundary.setValue(Common.YES_DESC);
		slPlotDemarcated.setValue(Common.YES_DESC);
		slIsLicenceForced.setValue(Common.YES_DESC);
		slAllApprovalRecved.setValue(Common.YES_DESC);
		slConstAsperAppPlan.setValue(Common.YES_DESC);
		slIsRateReasonable.setValue(Common.YES_DESC);
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
	@SuppressWarnings("deprecation")
	void setComponentStyle() {
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

		dfDateofValuation.setResolution(PopupDateField.RESOLUTION_DAY);
		dfDateofValuation.setDateFormat("dd-MMM-yyyy");
		dfVerifiedDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dfVerifiedDate.setDateFormat("dd-MMM-yyyy");
		dfSearchEvalDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dfSearchEvalDate.setDateFormat("dd-MMM-yyyy");

		tfCustomerName.setWidth(strComponentWidth);
		slPropertyDesc.setWidth(strComponentWidth);
		tfCustomerAddr.setWidth(strComponentWidth);
		tfCustomerAddr.setHeight("150px");
		slPropertyDesc.setNullSelectionAllowed(false);

		// for matching boundary
		slMatchingBoundary.setWidth(strComponentWidth);
		slPlotDemarcated.setWidth(strComponentWidth);
		slApproveLandUse.setWidth(strComponentWidth);
		slTypeofProperty.setWidth(strComponentWidth);
		tfDynamicmatching1.setWidth(strComponentWidth);
		tfDynamicmatching2.setWidth(strComponentWidth);

		// no of rooms
		tfNoofRooms.setWidth(strComponentWidth);
		tfLivingDining.setWidth(strComponentWidth);
		tfBedRooms.setWidth(strComponentWidth);
		tfKitchen.setWidth(strComponentWidth);
		tfToilets.setWidth(strComponentWidth);
		tfDynamicRooms1.setWidth(strComponentWidth);
		tfDynamicRooms2.setWidth(strComponentWidth);

		// no of floors
		tfTotNoofFloors.setWidth(strComponentWidth);
		tfPropertyLocated.setWidth(strComponentWidth);
		tfApproxAgeofBuilding.setWidth(strComponentWidth);
		tfResidualAgeofBuilding.setWidth(strComponentWidth);
		slTypeofStructures.setWidth(strComponentWidth);
		tfDynamicFloors1.setWidth(strComponentWidth);
		tfDynamicFloors2.setWidth(strComponentWidth);

		// tenure / occupancy details
		tfStatusofTenure.setWidth(strComponentWidth);
		slOwnedorRent.setWidth(strComponentWidth);
		tfNoOfYears.setWidth(strComponentWidth);
		tfRelationship.setWidth(strComponentWidth);
		tfDynamicTenure1.setWidth(strComponentWidth);
		tfDynamicTenure2.setWidth(strComponentWidth);

		// for area details
		tfSiteArea.setWidth(strComponentWidth);
		tfSuperbuiltup.setWidth(strComponentWidth);
		tfCarpetArea.setWidth(strComponentWidth);
		tfSalableArea.setWidth(strComponentWidth);
		tfRemarks.setWidth(strComponentWidth);
		tfRemarks.setHeight("95px");
		tfDynamicAreaDetail1.setWidth(strComponentWidth);
		tfDynamicAreaDetail2.setWidth(strComponentWidth);

		// details of apartment
		slNatureofApartment.setWidth(strComponentWidth);
		tfNameofApartment.setWidth(strComponentWidth);
		tfPostalAddress.setWidth(strComponentWidth);
		tfApartmant.setWidth(strComponentWidth);
		tfFlatNumber.setWidth(strComponentWidth);
		tfSFNumber.setWidth(strComponentWidth);
		tfFloor.setWidth(strComponentWidth);
		tfVillageLoc.setWidth(strComponentWidth);
		tfTalukLoc.setWidth(strComponentWidth);
		tfDisrictLoc.setWidth(strComponentWidth);
		slDescriptionofLocality.setWidth(strComponentWidth);
		tfNoofFloors.setWidth(strComponentWidth);
		slTypeofStructure.setWidth(strComponentWidth);
		tfNumberofFlats.setWidth(strComponentWidth);
		tfDynamicApartment1.setWidth(strComponentWidth);
		tfDynamicApartment2.setWidth(strComponentWidth);

		// for flat valuation
		tfFlatisSituated.setWidth(strComponentWidth);
		tfDynamicFlatValuation1.setWidth(strComponentWidth);
		tfDynamicFlatValuation2.setWidth(strComponentWidth);

		// for details of plan approval
				tfLandandBuilding.setHeight("25");
				slLandandBuilding.setHeight("25");
				slBuilding.setHeight("25");
				tfBuilding.setHeight("25");
				tfLandandBuilding.setWidth(strComponentWidth);
				slLandandBuilding.setWidth(strComponentWidth);
				slBuilding.setWidth(strComponentWidth);
				tfBuilding.setWidth(strComponentWidth);
				tfPlanApprovedBy.setWidth(strComponentWidth);
				dfLicenseFrom.setWidth(strComponentWidth);
				tfDynamicPlanApproval1.setWidth(strComponentWidth);
				slIsLicenceForced.setWidth(strComponentWidth);
				slAllApprovalRecved.setWidth(strComponentWidth);
				slConstAsperAppPlan.setWidth(strComponentWidth);
				tfDynamicPlanApproval2.setWidth(strComponentWidth);

		// for land valuation
		tfUndividedShare.setWidth(strComponentWidth);
		tfSuperBuiltupArea.setWidth(strComponentWidth);
		tfCostofApartment.setWidth(strComponentWidth);
		tfRateofApartment.setWidth(strComponentWidth);
		slIsRateReasonable.setWidth(strComponentWidth);
		tfStageOfConstruction.setWidth(strComponentWidth);
		tfCostOfConstAsAtSite.setWidth(strComponentWidth);
		tfCostOfConstAsPerAgree.setWidth(strComponentWidth);
		tfDynamicConstValuation1.setWidth(strComponentWidth);
		tfDynamicConstValuation2.setWidth(strComponentWidth);

		
		tfLandMark.setWidth(strComponentWidth);
		tfPropertyAddress.setWidth(strComponentWidth);
		tfPropertyAddress.setHeight("130px");
		tfDynamicAsset1.setWidth(strComponentWidth);
		tfDynamicAsset2.setWidth(strComponentWidth);
		
		//for Null representation
		tfSearchBankBranch.setNullSelectionAllowed(false);
		//tfSearchBankBranch.setInputPrompt(Common.SELECT_PROMPT);
		tfSearchCustomer.setInputPrompt("Enter Customer");
		tfSearchEvalNumber.setInputPrompt("Enter Evaluation Number");
		
		tfEvaluationNumber.setNullRepresentation("");
		slBankBranch.setNullSelectionAllowed(false);
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

		// for matching boundary
		slMatchingBoundary.setNullSelectionAllowed(false);
		slPlotDemarcated.setNullSelectionAllowed(false);
		slApproveLandUse.setNullSelectionAllowed(false);
		slTypeofProperty.setNullSelectionAllowed(false);
		tfDynamicmatching1.setNullRepresentation("");
		tfDynamicmatching2.setNullRepresentation("");

		// no of rooms
		tfNoofRooms.setNullRepresentation("");
		tfLivingDining.setNullRepresentation("");
		tfBedRooms.setNullRepresentation("");
		tfKitchen.setNullRepresentation("");
		tfToilets.setNullRepresentation("");
		tfDynamicRooms1.setNullRepresentation("");
		tfDynamicRooms2.setNullRepresentation("");

		// no of floors
		tfTotNoofFloors.setNullRepresentation("");
		tfPropertyLocated.setNullRepresentation("");
		tfApproxAgeofBuilding.setNullRepresentation("");
		tfResidualAgeofBuilding.setNullRepresentation("");
		slTypeofStructures.setNullSelectionAllowed(false);
		tfDynamicFloors1.setNullRepresentation("");
		tfDynamicFloors2.setNullRepresentation("");

		// tenure / occupancy details
		tfStatusofTenure.setNullRepresentation("");
		slOwnedorRent.setNullSelectionAllowed(false);
		tfNoOfYears.setNullRepresentation("");
		tfRelationship.setNullRepresentation("");
		tfDynamicTenure1.setNullRepresentation("");
		tfDynamicTenure2.setNullRepresentation("");

		// for area details
		tfSiteArea.setNullRepresentation("");
		tfSuperbuiltup.setNullRepresentation("");
		tfCarpetArea.setNullRepresentation("");
		tfSalableArea.setNullRepresentation("");
		tfRemarks.setNullRepresentation("");
		tfDynamicAreaDetail1.setNullRepresentation("");
		tfDynamicAreaDetail2.setNullRepresentation("");

		// details of apartment
		slNatureofApartment.setNullSelectionAllowed(false);
		tfNameofApartment.setNullRepresentation("");
		tfPostalAddress.setNullRepresentation("");
		tfApartmant.setNullRepresentation("");
		tfFlatNumber.setNullRepresentation("");
		tfSFNumber.setNullRepresentation("");
		tfFloor.setNullRepresentation("");
		tfVillageLoc.setNullRepresentation("");
		tfTalukLoc.setNullRepresentation("");
		tfDisrictLoc.setNullRepresentation("");
		slDescriptionofLocality.setNullSelectionAllowed(false);
		tfNoofFloors.setNullRepresentation("");
		slTypeofStructure.setNullSelectionAllowed(false);
		tfNumberofFlats.setNullRepresentation("");
		tfDynamicApartment1.setNullRepresentation("");
		tfDynamicApartment2.setNullRepresentation("");

		// for flat valuation
		tfFlatisSituated.setNullRepresentation("");
		tfDynamicFlatValuation1.setNullRepresentation("");
		tfDynamicFlatValuation2.setNullRepresentation("");

		// for details of plan approval
		tfLandandBuilding.setNullRepresentation("");
		tfBuilding.setNullRepresentation("");
		slLandandBuilding.setNullSelectionAllowed(false);
		slBuilding.setNullSelectionAllowed(false);
		tfPlanApprovedBy.setNullRepresentation("");
		tfDynamicPlanApproval1.setNullRepresentation("");
		slIsLicenceForced.setNullSelectionAllowed(false);
		slAllApprovalRecved.setNullSelectionAllowed(false);
		slConstAsperAppPlan.setNullSelectionAllowed(false);
		tfDynamicPlanApproval2.setNullRepresentation("");
		dfLicenseFrom.setNullRepresentation("");

		// for land valuation
		tfUndividedShare.setNullRepresentation("");
		tfSuperBuiltupArea.setNullRepresentation("");
		tfCostofApartment.setNullRepresentation("");
		tfRateofApartment.setNullRepresentation("");
		slIsRateReasonable.setNullSelectionAllowed(false);
		tfStageOfConstruction.setNullRepresentation("");
		tfCostOfConstAsAtSite.setNullRepresentation("");
		tfCostOfConstAsPerAgree.setNullRepresentation("");
		tfDynamicConstValuation1.setNullRepresentation("");
		tfDynamicConstValuation2.setNullRepresentation("");

		
		tfLandMark.setNullRepresentation("");
		tfPropertyAddress.setNullRepresentation("");
		tfPropertyAddress.setHeight("130px");
		tfDynamicAsset1.setNullRepresentation("");
		tfDynamicAsset2.setNullRepresentation("");

		// for dynamic
		btnDynamicEvaluation1.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicEvaluation1.setStyleName(Runo.BUTTON_LINK);
		btnAddOwner
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnAddOwner.setStyleName(Runo.BUTTON_LINK);
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
		btnDynamicRooms.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicRooms.setStyleName(Runo.BUTTON_LINK);
		btnDynamicFloor.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicFloor.setStyleName(Runo.BUTTON_LINK);
		btnDynamicTenure.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicTenure.setStyleName(Runo.BUTTON_LINK);
		btnDynamicmatching
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicmatching.setStyleName(Runo.BUTTON_LINK);
		btnDynamicAreaDetail.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicAreaDetail.setStyleName(Runo.BUTTON_LINK);
		btnDynamicConstValuation.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicConstValuation.setStyleName(Runo.BUTTON_LINK);
		btnDynamicApartment
				.setIcon(new ThemeResource(Common.strAddIcon));
		btnDynamicApartment.setStyleName(Runo.BUTTON_LINK);

		btnDynamicFlatValuation.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicFlatValuation.setStyleName(Runo.BUTTON_LINK);
		btnDynamicPlanApproval.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnDynamicPlanApproval.setStyleName(Runo.BUTTON_LINK);
		btnAddBuildSpec.setStyleName(Runo.BUTTON_LINK);
		btnAddBuildSpec.setIcon(new ThemeResource(Common.strAddIcon));

		tfStatusofTenure.setValue(Common.strNA);
		tfRelationship.setValue(Common.strNA);
		tfSuperbuiltup.setValue(Common.strNil);
		tfCarpetArea.setValue(Common.strNil);
		tfRemarks.setValue(Common.strNil);

	}

	public void buttonClick(ClickEvent event) {
		notifications.close();
		if (btnAddNorDoc == event.getButton()) {

			panelNormalDocumentDetails
					.addComponent(new ComponentIteratorNormlDoc(null, null, "",
							""));
		}

		if (btnAddLegalDoc == event.getButton()) {

			panelLegalDocumentDetails
					.addComponent(new ComponentIteratorLegalDoc("", "", null));
		}
		if (btnAddOwner == event.getButton()) {

			layoutOwnerDetails1
					.addComponent(new ComponentIterOwnerDetails("", ""));
		}

		if (btnAddAdjoinProperty == event.getButton()) {
			panelAdjoinProperties
					.addComponent(new ComponentIteratorAdjoinProperty(null,true,true,true));
		}

		if (btnAddDimension == event.getButton()) {
			
			panelDimension.addComponent(new ComponentIterDimensionofPlot(null,true,true,true));
		}
		if (btnAddBuildSpec == event.getButton()) {
			panelBuildSpecfication
					.addComponent(new ComponentIterBuildingSpecfication(null,true,true,true));
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
			hlFileDownloadLayout.removeAllComponents();
			hlFileDownloadLayout.addComponent(btnDownload);
			getExportTableDetails();


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
		if (btnDynamicRooms == event.getButton()) {
			strSelectedPanel = "6";
			showSubWindow();

		}
		if (btnDynamicFloor == event.getButton()) {
			strSelectedPanel = "7";
			showSubWindow();

		}

		if (btnDynamicAreaDetail == event.getButton()) {
			strSelectedPanel = "8";
			showSubWindow();

		}
		if (btnDynamicApartment == event.getButton()) {
			strSelectedPanel = "9";
			showSubWindow();

		}
		if (btnDynamicFlatValuation == event.getButton()) {
			strSelectedPanel = "10";
			showSubWindow();

		}
		if (btnDynamicPlanApproval == event.getButton()) {
			strSelectedPanel = "11";
			showSubWindow();

		}
		if (btnDynamicConstValuation == event.getButton()) {
			strSelectedPanel = "12";
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
				}  else if (strSelectedPanel.equals("3")) {
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
				} else if (strSelectedPanel.equals("6")) {
					if (tfDynamicRooms1.isVisible()) {
						tfDynamicRooms2.setCaption(tfCaption.getValue());
						tfDynamicRooms2.setVisible(true);
					} else {
						tfDynamicRooms1.setCaption(tfCaption.getValue());
						tfDynamicRooms1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("7")) {
					if (tfDynamicFloors1.isVisible()) {
						tfDynamicFloors2.setCaption(tfCaption.getValue());
						tfDynamicFloors2.setVisible(true);
					} else {
						tfDynamicFloors1.setCaption(tfCaption.getValue());
						tfDynamicFloors1.setVisible(true);
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
					if (tfDynamicApartment1.isVisible()) {
						tfDynamicApartment2.setCaption(tfCaption.getValue());
						tfDynamicApartment2.setVisible(true);
					} else {
						tfDynamicApartment1.setCaption(tfCaption.getValue());
						tfDynamicApartment1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("10")) {
					if (tfDynamicFlatValuation1.isVisible()) {
						tfDynamicFlatValuation2
								.setCaption(tfCaption.getValue());
						tfDynamicFlatValuation2.setVisible(true);
					} else {
						tfDynamicFlatValuation1
								.setCaption(tfCaption.getValue());
						tfDynamicFlatValuation1.setVisible(true);
					}
				} else if (strSelectedPanel.equals("11")) {
					if (tfDynamicPlanApproval1.isVisible()) {
						tfDynamicPlanApproval2.setCaption(tfCaption.getValue());
						tfDynamicPlanApproval2.setVisible(true);
					} else {
						tfDynamicPlanApproval1.setCaption(tfCaption.getValue());
						tfDynamicPlanApproval1.setVisible(true);
					}
				}
				else if (strSelectedPanel.equals("12")) {
					if (tfDynamicConstValuation1.isVisible()) {
						tfDynamicConstValuation2.setCaption(tfCaption.getValue());
						tfDynamicConstValuation2.setVisible(true);
					} else {
						tfDynamicConstValuation1.setCaption(tfCaption.getValue());
						tfDynamicConstValuation1.setVisible(true);
					}
				}
				
			}
			mywindow.close();
		}

		if (btnSave == event.getButton()) {
			setComponentError();
			if(tfEvaluationNumber.isValid() &&slBankBranch.isValid() && tfEvaluationPurpose.isValid() && dfDateofValuation.isValid())
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
			tfSearchBankBranch.setValue(null);
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
		/* else if (btnView == event.getButton()) {

				StreamResource sr = getPDFStream();
				FileDownloader fileDownloader = new FileDownloader(sr);
				fileDownloader.extend(btnView);

			}*/
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
