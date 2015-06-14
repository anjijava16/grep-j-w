/**
 * File Name	:	QATest.java
 * Description	:	This Screen Purpose for Modify the QATest Details.
 * 					Add the QATest details process should be directly added in DB.
 * Author		:	Nandhakumar.S
 * 
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1          01-Oct-2014		  Nandhakumar.S		Initial version 
 */
package com.gnts.mfg.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.mst.ProductDrawingDM;
import com.gnts.mfg.domain.mst.TestConditionDM;
import com.gnts.mfg.domain.mst.TestGroupDM;
import com.gnts.mfg.domain.mst.TestSpecificationDM;
import com.gnts.mfg.domain.mst.TestTypeDM;
import com.gnts.mfg.domain.txn.CommentDM;
import com.gnts.mfg.domain.txn.QATestCndtnResltDM;
import com.gnts.mfg.domain.txn.QATestDtlDM;
import com.gnts.mfg.domain.txn.QATestHdrDM;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.mst.QCTestType;
import com.gnts.mfg.service.mst.ProductDrawingService;
import com.gnts.mfg.service.mst.TestConditionService;
import com.gnts.mfg.service.mst.TestGroupService;
import com.gnts.mfg.service.mst.TestSpecificationService;
import com.gnts.mfg.service.mst.TestTypeService;
import com.gnts.mfg.service.txn.QATestCndRsltService;
import com.gnts.mfg.service.txn.QATestDtlService;
import com.gnts.mfg.service.txn.QATestHdrService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class QATest extends BaseUI {
	/**
	 * 
	 */
	// QA Test Header Components
	private static final long serialVersionUID = 1L;
	private TextField tfInspectnNo, tfPrdSlNo, tfTstReslt;
	private ComboBox cbClient, cbproduct, cbProdDrg, cbWorkOrderNo, cbTestGrp, cbTesttype, cbTestCondn, cbQThdrStatus;
	private PopupDateField pdInspectionDt;
	private TextArea taTstObservation;
	private FormLayout flTstHdr1, flTstHdr2, flTstHdr3, flTstHdr4;
	// QA Test Def.Details Components
	private ComboBox cbTstSpec, cbQDtlStatus;
	private TextArea taQcTstDtlRemarks;
	private Table tblQATstDtl;
	private TextField tfTstCycle, tfQADefntstReslt;
	private Button btnAddDtl;
	private FormLayout flTstDtl1, flTstDtl2, flTstDtl3, flTstDtl4, flTstDtl5;
	// QA Test Condition Result component
	private ComboBox cbTstCondtn, cbQATstCndnStatus;
	private TextArea taCndnObservation;
	private Table tblCndnRslt;
	private Button btnAddCndn;
	private FormLayout flTstCndn1, flTstCndn2, flTstCndn3, flTstCndn4;
	//
	private QATestDtlService serviceQATstDtl = (QATestDtlService) SpringContextHelper.getBean("qatestDetls");
	private QATestHdrService serviceQATstHdr = (QATestHdrService) SpringContextHelper.getBean("qatesthdr");
	private QATestCndRsltService serviceQATestCndRslt = (QATestCndRsltService) SpringContextHelper
			.getBean("qatestCndnRslt");
	BeanItemContainer<QATestHdrDM> beanQATstHdr = null;
	BeanItemContainer<QATestDtlDM> beanQATstDtl = null;
	BeanItemContainer<QATestCndtnResltDM> beanQATestCndtnReslt = null;
	List<QATestHdrDM> listQATestHdr = null;
	List<QATestDtlDM> listQATestDtl = null;
	List<QATestCndtnResltDM> listQATestCndtnReslt = null;
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private ProductDrawingService serviceProductDrawing = (ProductDrawingService) SpringContextHelper
			.getBean("productDrawings");
	private SlnoGenService serviceSLNo = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private WorkOrderHdrService serviceWorkOrderHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private ClientService serviceClient = (ClientService) SpringContextHelper.getBean("clients");
	private TestGroupService serviceTestGroup = (TestGroupService) SpringContextHelper.getBean("testGroup");
	private TestTypeService serviceTestType = (TestTypeService) SpringContextHelper.getBean("testType");
	private TestConditionService serviceTestCondition = (TestConditionService) SpringContextHelper
			.getBean("qaTestCondn");
	private TestSpecificationService seriviceTestSpecification = (TestSpecificationService) SpringContextHelper
			.getBean("testSpec");
	private String userName;
	private Long companyid;
	private Long EmployeeId;
	private Long moduleId;
	private Long branchID;
	private Long qaTstHdrId;
	private static Logger logger = Logger.getLogger(QCTestType.class);
	HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlSearchLayout;
	private TabSheet tabSheet;
	private int recordCnt;
	public Button btndelete = new GERPButton("Delete", "delete", this);
	public Button btnSpecdelete = new GERPButton("Delete", "delete", this);
	private Comments comment;
	VerticalLayout vlTableForm = new VerticalLayout();
	private Long commentby;
	
	public QATest() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		EmployeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Inside QATest() constructor");
		// Loading the QATest UI
		buildView();
	}
	
	private void buildView() {
		// QA Test Header Components Initialization
		tfInspectnNo = new GERPTextField("Inspection No.");
		tfPrdSlNo = new GERPTextField("Product Sl.No");
		tfTstReslt = new GERPTextField("Result");
		cbClient = new GERPComboBox("Client Name");
		cbClient.setItemCaptionPropertyId("clientName");
		loadClientList();
		cbproduct = new GERPComboBox("Product Name");
		cbproduct.setItemCaptionPropertyId("prodname");
		loadProductList();
		cbProdDrg = new GERPComboBox("Product Drawing No.");
		cbProdDrg.setItemCaptionPropertyId("drawingCode");
		loadProductDrgCodeList();
		cbWorkOrderNo = new GERPComboBox("Work Order No.");
		cbWorkOrderNo.setItemCaptionPropertyId("workOrdrNo");
		loadWorkOrdNo();
		cbTestGrp = new GERPComboBox("Test Group");
		cbTestGrp.setItemCaptionPropertyId("testGroup");
		loadTestGroup();
		cbTesttype = new GERPComboBox("Test Type");
		cbTesttype.setItemCaptionPropertyId("tstType");
		loadTesttype();
		cbTestCondn = new GERPComboBox("Test Condition");
		cbTestCondn.setItemCaptionPropertyId("testCondn");
		loadTstHdrConditions();
		cbQThdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		pdInspectionDt = new GERPPopupDateField("Inspection Date");
		taTstObservation = new GERPTextArea("Observation");
		taTstObservation.setWidth("215");
		taTstObservation.setHeight("97");
		tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
					btnAdd.setEnabled(true);
				} else {
					btnEdit.setEnabled(true);
					btnAdd.setEnabled(false);
				}
				resetFields();
				tfInspectnNo.setReadOnly(false);
			}
		});
		btndelete.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				deleteDetails();
			}
		});
		btnSpecdelete.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				deleteSpecDetails();
			}
		});
		// QA Test Details Components Initialization
		cbTstSpec = new GERPComboBox("Test Specification");
		cbTstSpec.setItemCaptionPropertyId("testSpec");
		loadTstSpecification();
		tfTstCycle = new GERPTextField("Test Cycle");
		tfTstCycle.setValue("0");
		tfTstCycle.setHeight("22");
		tfQADefntstReslt = new GERPTextField("Result");
		tfQADefntstReslt.setWidth("140");
		cbQDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbQDtlStatus.setHeight("22");
		taQcTstDtlRemarks = new GERPTextArea("Remarks");
		taQcTstDtlRemarks.setWidth("280");
		taQcTstDtlRemarks.setHeight("46");
		btnAddDtl = new GERPButton("Add", "add");
		btnAddDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateTstDefDetails()) {
					saveQaTstDtls();
				}
			}
		});
		tblQATstDtl = new GERPTable();
		tblQATstDtl.setPageLength(6);
		tblQATstDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblQATstDtl.isSelected(event.getItemId())) {
					tblQATstDtl.setImmediate(true);
					btnAddDtl.setCaption("Add");
					resetQATestDefDtl();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtl.setCaption("Update");
					editQATstDefnDetails();
				}
			}
		});
		// QA Test Condition Components Initialization
		cbTstCondtn = new GERPComboBox("Test Condition");
		cbTstCondtn.setItemCaptionPropertyId("testCondn");
		loadTstDefDtlConditions();
		cbQATstCndnStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbQATstCndnStatus.setHeight("22");
		taCndnObservation = new GERPTextArea("Observation");
		taCndnObservation.setWidth("240");
		taCndnObservation.setHeight("47");
		btnAddCndn = new GERPButton("Add", "add", this);
		btnAddCndn.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateTstCndnDetails()) {
					saveCndnReslt();
				}
			}
		});
		tblCndnRslt = new GERPTable();
		tblCndnRslt.setPageLength(6);
		tblCndnRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblCndnRslt.isSelected(event.getItemId())) {
					tblCndnRslt.setImmediate(true);
					btnAddCndn.setCaption("Add");
					resetQATstCndnRslt();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddCndn.setCaption("Update");
					editQATstCndnRslt();
				}
			}
		});
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		resetQATestDefDtl();
		resetQATstCndnRslt();
		assembleSearchLayout();
		loadSrchRslt();
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		List<QATestHdrDM> listQcTstHdr = new ArrayList<QATestHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyid);
		listQcTstHdr = serviceQATstHdr.getQaTestHdrDetails(null, companyid, (String) tfInspectnNo.getValue(),
				(Long) cbClient.getValue(), (Long) cbproduct.getValue(), (String) cbQThdrStatus.getValue());
		recordCnt = listQcTstHdr.size();
		beanQATstHdr = new BeanItemContainer<QATestHdrDM>(QATestHdrDM.class);
		beanQATstHdr.addAll(listQcTstHdr);
		tblMstScrSrchRslt.setContainerDataSource(beanQATstHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "qatestHdrid", "inspectionno", "inspectiondate",
				"clientName", "productName", "workOrdNo", "testresult", "teststatus", "lastupdateddate",
				"lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Inspection No.", "Inspection Date", "Client Name",
				"Product Name", "Work Ord.No", "QC. Result", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("qatestHdrid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadSrchQADtlList() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyid);
		tblQATstDtl.removeAllItems();
		recordCnt = listQATestDtl.size();
		beanQATstDtl = new BeanItemContainer<QATestDtlDM>(QATestDtlDM.class);
		beanQATstDtl.addAll(listQATestDtl);
		tblQATstDtl.setContainerDataSource(beanQATstDtl);
		tblQATstDtl.setVisibleColumns(new Object[] { "qatestDtlid", "tstSpec", "tstCycleNo", "tstSpecResult",
				"qaTstStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblQATstDtl.setColumnHeaders(new String[] { "Ref.Id", "Test Specification", "Test Cycles", "Result", "Status",
				"Last Updated Date", "Last Updated By" });
		tblQATstDtl.setColumnAlignment("qatestDtlid", Align.RIGHT);
		tblQATstDtl.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadSrchQACndnRsltList() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyid);
		tblCndnRslt.removeAllItems();
		recordCnt = listQATestCndtnReslt.size();
		beanQATestCndtnReslt = new BeanItemContainer<QATestCndtnResltDM>(QATestCndtnResltDM.class);
		beanQATestCndtnReslt.addAll(listQATestCndtnReslt);
		tblCndnRslt.setContainerDataSource(beanQATestCndtnReslt);
		tblCndnRslt.setVisibleColumns(new Object[] { "qaTstCndnRsltId", "tstCondition", "condnStatus", "lastUpdatedDt",
				"lastUpdatedBy" });
		tblCndnRslt.setColumnHeaders(new String[] { "Ref.Id", "Test Condition", "Status", "Last Updated Date",
				"Last Updated By" });
		tblCndnRslt.setColumnAlignment("qaTstCndnRsltId", Align.RIGHT);
		tblCndnRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for QATest UI search layout
		tfInspectnNo.setReadOnly(false);
		hlSearchLayout.removeAllComponents();
		flTstHdr1 = new FormLayout();
		flTstHdr2 = new FormLayout();
		flTstHdr3 = new FormLayout();
		flTstHdr4 = new FormLayout();
		flTstHdr1.addComponent(tfInspectnNo);
		flTstHdr2.addComponent(cbClient);
		flTstHdr3.addComponent(cbproduct);
		flTstHdr4.addComponent(cbQThdrStatus);
		hlSearchLayout.addComponent(flTstHdr1);
		hlSearchLayout.addComponent(flTstHdr2);
		hlSearchLayout.addComponent(flTstHdr3);
		hlSearchLayout.addComponent(flTstHdr4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ " assembleInputUserLayout layout");
		/*
		 * Adding user input layout to the input layout as all the fields in the user input are available in the edit
		 * block. hence the same layout used as is
		 */
		// Set required fields
		// Removing components from search layout and re-initializing form layouts
		hlSearchLayout.removeAllComponents();
		hlUserInputLayout.removeAllComponents();
		tfPrdSlNo.setRequired(true);
		tfTstReslt.setRequired(true);
		cbClient.setRequired(true);
		cbproduct.setRequired(true);
		cbProdDrg.setRequired(true);
		cbWorkOrderNo.setRequired(true);
		cbTestGrp.setRequired(true);
		cbTesttype.setRequired(true);
		cbTestCondn.setRequired(true);
		pdInspectionDt.setRequired(true);
		tfTstCycle.setRequired(true);
		cbTstSpec.setRequired(true);
		tfQADefntstReslt.setRequired(true);
		cbTstCondtn.setRequired(true);
		// QATest Header Components adding to the User input layout
		flTstHdr1 = new FormLayout();
		flTstHdr2 = new FormLayout();
		flTstHdr3 = new FormLayout();
		flTstHdr4 = new FormLayout();
		flTstHdr1.addComponent(tfInspectnNo);
		flTstHdr1.addComponent(pdInspectionDt);
		flTstHdr1.addComponent(cbClient);
		flTstHdr1.addComponent(cbproduct);
		flTstHdr2.addComponent(cbWorkOrderNo);
		flTstHdr2.addComponent(cbProdDrg);
		flTstHdr2.addComponent(tfPrdSlNo);
		flTstHdr2.addComponent(cbTestGrp);
		flTstHdr3.addComponent(cbTesttype);
		flTstHdr3.addComponent(cbTestCondn);
		flTstHdr3.addComponent(tfTstReslt);
		flTstHdr3.addComponent(cbQThdrStatus);
		flTstHdr4.addComponent(taTstObservation);
		HorizontalLayout hlTstHdr = new HorizontalLayout();
		hlTstHdr.addComponent(flTstHdr1);
		hlTstHdr.addComponent(flTstHdr2);
		hlTstHdr.addComponent(flTstHdr3);
		hlTstHdr.addComponent(flTstHdr4);
		hlTstHdr.setMargin(true);
		hlTstHdr.setSpacing(true);
		// QA Test Details Components adding to the user input layout
		flTstDtl1 = new FormLayout();
		flTstDtl2 = new FormLayout();
		flTstDtl3 = new FormLayout();
		flTstDtl4 = new FormLayout();
		flTstDtl5 = new FormLayout();
		flTstDtl1.addComponent(cbTstSpec);
		flTstDtl1.addComponent(tfTstCycle);
		flTstDtl2.addComponent(taQcTstDtlRemarks);
		flTstDtl3.addComponent(tfQADefntstReslt);
		flTstDtl3.addComponent(cbQDtlStatus);
		flTstDtl4.addComponent(btnAddDtl);
		flTstDtl5.addComponent(btnSpecdelete);
		HorizontalLayout hlTstDtl = new HorizontalLayout();
		hlTstDtl.addComponent(flTstDtl1);
		hlTstDtl.addComponent(flTstDtl2);
		hlTstDtl.addComponent(flTstDtl3);
		hlTstDtl.addComponent(flTstDtl4);
		hlTstDtl.addComponent(flTstDtl5);
		hlTstDtl.setMargin(true);
		hlTstDtl.setSpacing(true);
		VerticalLayout vlTstDtl = new VerticalLayout();
		vlTstDtl.addComponent(hlTstDtl);
		vlTstDtl.addComponent(tblQATstDtl);
		// QA Test Condition Components adding to user input layout
		flTstCndn1 = new FormLayout();
		flTstCndn2 = new FormLayout();
		flTstCndn3 = new FormLayout();
		flTstCndn4 = new FormLayout();
		flTstCndn1.addComponent(cbTstCondtn);
		flTstCndn1.addComponent(cbQATstCndnStatus);
		flTstCndn2.addComponent(taCndnObservation);
		flTstCndn3.addComponent(btnAddCndn);
		flTstCndn4.addComponent(btndelete);
		HorizontalLayout hlTstCndnRslt = new HorizontalLayout();
		hlTstCndnRslt.addComponent(flTstCndn1);
		hlTstCndnRslt.addComponent(flTstCndn2);
		hlTstCndnRslt.addComponent(flTstCndn3);
		hlTstCndnRslt.addComponent(flTstCndn4);
		hlTstCndnRslt.setSpacing(true);
		hlTstCndnRslt.setMargin(true);
		VerticalLayout vlTstCndn = new VerticalLayout();
		vlTstCndn.addComponent(hlTstCndnRslt);
		vlTstCndn.addComponent(tblCndnRslt);
		//
		tabSheet = new TabSheet();
		tabSheet.setWidth("100%");
		tabSheet.setHeight("315");
		tabSheet.addTab(vlTstDtl, "Test Definition", null);
		tabSheet.addTab(vlTstCndn, "Test Condition Result", null);
		tabSheet.addTab(vlTableForm, "Comments", null);
		VerticalLayout vlAllComponent = new VerticalLayout();
		vlAllComponent.addComponent(hlTstHdr);
		vlAllComponent.addComponent(GERPPanelGenerator.createPanel(hlTstHdr));
		vlAllComponent.addComponent(tabSheet);
		vlAllComponent.setSpacing(true);
		vlAllComponent.setWidth("100%");
		// adding form layouts into user input layouts
		hlUserInputLayout.addComponent(vlAllComponent);
		hlUserInputLayout.setWidth("100%");
	}
	
	private void resetQATestDefDtl() {
		cbTstSpec.setValue(null);
		cbTstSpec.setComponentError(null);
		tfTstCycle.setValue("0");
		tfTstCycle.setComponentError(null);
		tfQADefntstReslt.setComponentError(null);
		tfQADefntstReslt.setValue("");
		cbQDtlStatus.setValue(cbQDtlStatus.getItemIds().iterator().next());
		taQcTstDtlRemarks.setValue("");
	}
	
	@Override
	protected void resetFields() {
		tfInspectnNo.setReadOnly(false);
		tfInspectnNo.setValue("");
		tfInspectnNo.setReadOnly(true);
		tfPrdSlNo.setValue("");
		tfPrdSlNo.setComponentError(null);
		tfTstReslt.setValue("");
		tfTstReslt.setComponentError(null);
		cbClient.setValue(null);
		cbClient.setComponentError(null);
		cbproduct.setValue(null);
		cbproduct.setComponentError(null);
		cbProdDrg.setValue(null);
		cbProdDrg.setComponentError(null);
		cbWorkOrderNo.setValue(null);
		cbWorkOrderNo.setComponentError(null);
		cbTestGrp.setValue(null);
		cbTestGrp.setComponentError(null);
		cbTesttype.setValue(null);
		cbTesttype.setComponentError(null);
		cbTestCondn.setValue(null);
		cbTestCondn.setComponentError(null);
		pdInspectionDt.setValue(null);
		pdInspectionDt.setComponentError(null);
		cbQThdrStatus.setValue(cbQThdrStatus.getItemIds().iterator().next());
		taTstObservation.setValue("");
		listQATestDtl = new ArrayList<QATestDtlDM>();
		listQATestCndtnReslt = new ArrayList<QATestCndtnResltDM>();
		tblCndnRslt.removeAllItems();
		tblQATstDtl.removeAllItems();
	}
	
	public void resetQATstCndnRslt() {
		cbTstCondtn.setValue(null);
		taCndnObservation.setValue("");
		cbQATstCndnStatus.setValue(cbQATstCndnStatus.getItemIds().iterator().next());
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbClient.setValue(null);
		cbproduct.setValue(null);
		tfInspectnNo.setValue("");
		cbQThdrStatus.setValue(cbQThdrStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		resetFields();
		resetQATestDefDtl();
		List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyid, branchID, moduleId, "MF_QCINSNO");
		tfInspectnNo.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfInspectnNo.setReadOnly(true);
			} else {
				tfInspectnNo.setReadOnly(false);
			}
		}
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		tblQATstDtl.setVisible(true);
		tblCndnRslt.setVisible(true);
		loadSrchQACndnRsltList();
		loadSrchQADtlList();
		comment = new Comments(vlTableForm, companyid, null, null, null, null, commentby);
	}
	
	private void loadProductDrgCodeList() {
		List<ProductDrawingDM> getProdDrg = new ArrayList<ProductDrawingDM>();
		getProdDrg = serviceProductDrawing.getProductDrgDetails(companyid, null, null, null, "Active");
		BeanContainer<Long, ProductDrawingDM> beanProdDrg = new BeanContainer<Long, ProductDrawingDM>(
				ProductDrawingDM.class);
		beanProdDrg.setBeanIdProperty("productDrgId");
		beanProdDrg.addAll(getProdDrg);
		cbProdDrg.setContainerDataSource(beanProdDrg);
	}
	
	private void loadWorkOrdNo() {
		List<WorkOrderHdrDM> getworkOrdHdr = new ArrayList<WorkOrderHdrDM>();
		getworkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, null, null, null, "Active", "F",
				null,null));
		BeanContainer<Long, WorkOrderHdrDM> beanWrkOrdHdr = new BeanContainer<Long, WorkOrderHdrDM>(
				WorkOrderHdrDM.class);
		beanWrkOrdHdr.setBeanIdProperty("workOrdrId");
		beanWrkOrdHdr.addAll(getworkOrdHdr);
		cbWorkOrderNo.setContainerDataSource(beanWrkOrdHdr);
	}
	
	private void loadProductList() {
		List<ProductDM> getProdList = new ArrayList<ProductDM>();
		getProdList = serviceProduct.getProductList(companyid, null, null, null, "Active", null, null, "F");
		BeanContainer<Long, ProductDM> beanProd = new BeanContainer<Long, ProductDM>(ProductDM.class);
		beanProd.setBeanIdProperty("prodid");
		beanProd.addAll(getProdList);
		cbproduct.setContainerDataSource(beanProd);
	}
	
	private void loadClientList() {
		List<ClientDM> getClientList = new ArrayList<ClientDM>();
		getClientList.addAll(serviceClient.getClientDetails(companyid, null, null, null, null, null, null, null,
				"Active", "P"));
		BeanContainer<Long, ClientDM> beanClient = new BeanContainer<Long, ClientDM>(ClientDM.class);
		beanClient.setBeanIdProperty("clientId");
		beanClient.addAll(getClientList);
		cbClient.setContainerDataSource(beanClient);
	}
	
	private void loadTestGroup() {
		List<TestGroupDM> getTstGrp = new ArrayList<TestGroupDM>();
		getTstGrp.addAll(serviceTestGroup.getTestGpDetails(null, null, "Active", "F"));
		BeanContainer<Long, TestGroupDM> beanTstGrp = new BeanContainer<Long, TestGroupDM>(TestGroupDM.class);
		beanTstGrp.setBeanIdProperty("qaTestGpID");
		beanTstGrp.addAll(getTstGrp);
		cbTestGrp.setContainerDataSource(beanTstGrp);
	}
	
	private void loadTesttype() {
		List<TestTypeDM> getTstTyp = new ArrayList<TestTypeDM>();
		getTstTyp.addAll(serviceTestType.getTestTypeDetails(null, null, null, "Active"));
		BeanContainer<Long, TestTypeDM> beanTsttype = new BeanContainer<Long, TestTypeDM>(TestTypeDM.class);
		beanTsttype.setBeanIdProperty("qaTstTypId");
		beanTsttype.addAll(getTstTyp);
		cbTesttype.setContainerDataSource(beanTsttype);
	}
	
	private void loadTstHdrConditions() {
		List<TestConditionDM> getCondition = new ArrayList<TestConditionDM>();
		getCondition.addAll(serviceTestCondition.getTestCondnDetails(companyid, null, null, "Active"));
		BeanContainer<Long, TestConditionDM> beanTstCondn = new BeanContainer<Long, TestConditionDM>(
				TestConditionDM.class);
		beanTstCondn.setBeanIdProperty("testCondnId");
		beanTstCondn.addAll(getCondition);
		cbTestCondn.setContainerDataSource(beanTstCondn);
	}
	
	private void loadTstDefDtlConditions() {
		List<TestConditionDM> getCondition = new ArrayList<TestConditionDM>();
		getCondition.addAll(serviceTestCondition.getTestCondnDetails(companyid, null, null, "Active"));
		BeanItemContainer<TestConditionDM> beanTstCondn = new BeanItemContainer<TestConditionDM>(TestConditionDM.class);
		beanTstCondn.addAll(getCondition);
		cbTstCondtn.setContainerDataSource(beanTstCondn);
	}
	
	private void loadTstSpecification() {
		List<TestSpecificationDM> getClientList = new ArrayList<TestSpecificationDM>();
		getClientList.addAll(seriviceTestSpecification.getTestSpecDetails(companyid, null, "Active"));
		BeanItemContainer<TestSpecificationDM> beanTstSpec = new BeanItemContainer<TestSpecificationDM>(
				TestSpecificationDM.class);
		beanTstSpec.addAll(getClientList);
		cbTstSpec.setContainerDataSource(beanTstSpec);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyid, branchID, moduleId, "MF_QAINSNO");
		tfInspectnNo.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfInspectnNo.setReadOnly(true);
			} else {
				tfInspectnNo.setReadOnly(false);
			}
		}
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblQATstDtl.setVisible(true);
		if (tfInspectnNo.getValue() == null || tfInspectnNo.getValue().trim().length() == 0) {
			tfInspectnNo.setReadOnly(false);
		}
		resetQATestDefDtl();
		resetQATstCndnRslt();
		resetFields();
		editQAHdrDetails();
		loadSrchQADtlList();
		loadSrchQACndnRsltList();
	}
	
	private void editQAHdrDetails() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			QATestHdrDM editQaTestHdr = beanQATstHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			qaTstHdrId = editQaTestHdr.getQatestHdrid();
			tfInspectnNo.setReadOnly(false);
			tfInspectnNo.setValue((String) itselect.getItemProperty("inspectionno").getValue());
			tfInspectnNo.setReadOnly(true);
			tfPrdSlNo.setValue((String) itselect.getItemProperty("prodslno").getValue());
			tfTstReslt.setValue((String) itselect.getItemProperty("testresult").getValue());
			cbClient.setValue(editQaTestHdr.getClientid());
			cbproduct.setValue(editQaTestHdr.getProductid());
			cbWorkOrderNo.setValue(editQaTestHdr.getWoid().toString());
			System.out.println("cbWorkOrderNo.getValue() >>>>>>>>" + cbWorkOrderNo.getValue());
			logger.info("Work order no >>>>>>>>>>>>>>>" + editQaTestHdr.getWoid());
			cbProdDrg.setValue(editQaTestHdr.getProddwgid().toString());
			cbTesttype.setValue(editQaTestHdr.getQattesttypeid().toString());
			cbTestGrp.setValue(editQaTestHdr.getQatestgroupid());
			cbTesttype.setValue(Long.valueOf(editQaTestHdr.getQattesttypeid()));
			cbTestCondn.setValue(editQaTestHdr.getQatestcondid());
			cbQThdrStatus.setValue((String) itselect.getItemProperty("teststatus").getValue());
			pdInspectionDt.setValue(editQaTestHdr.getInspectiondateDt());
			if (editQaTestHdr.getObservation() != null) {
				taTstObservation.setValue((String) itselect.getItemProperty("observation").getValue());
			}
			cbWorkOrderNo.setValue(editQaTestHdr.getWoid());
			listQATestDtl = serviceQATstDtl.getQATestDtlDetails(null, qaTstHdrId, null, null, "Active");
			listQATestCndtnReslt = serviceQATestCndRslt.getQATestCndtnResltDetails(null, qaTstHdrId, "Active");
			comment = new Comments(vlTableForm, companyid, null, null, null, null, commentby);
			comment.loadsrch(true, null, companyid, null, null, null, qaTstHdrId);
		}
	}
	
	private void editQATstDefnDetails() {
		Item itselect = tblQATstDtl.getItem(tblQATstDtl.getValue());
		if (itselect != null) {
			QATestDtlDM editQaTestDtl = beanQATstDtl.getItem(tblQATstDtl.getValue()).getBean();
			Long speId = editQaTestDtl.getQatestSpecid();
			Collection<?> specID = cbTstSpec.getItemIds();
			for (Iterator<?> iterator = specID.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbTstSpec.getItem(itemId);
				// Get the actual bean and use the data
				TestSpecificationDM st = (TestSpecificationDM) item.getBean();
				if (speId != null && speId.equals(st.getTestSpecId())) {
					cbTstSpec.select(itemId);
				}
			}
			tfTstCycle.setValue(Long.valueOf(editQaTestDtl.getTstCycleNo()).toString());
			tfQADefntstReslt.setValue((String) itselect.getItemProperty("tstSpecResult").getValue());
			cbQDtlStatus.setValue((String) itselect.getItemProperty("qaTstStatus").getValue());
			if (editQaTestDtl.getQaTstRemarks() != null) {
				taQcTstDtlRemarks.setValue((String) itselect.getItemProperty("qaTstRemarks").getValue());
			}
		}
	}
	
	private void editQATstCndnRslt() {
		Item itselect = tblCndnRslt.getItem(tblCndnRslt.getValue());
		if (itselect != null) {
			QATestCndtnResltDM editQaTestCndnRslt = beanQATestCndtnReslt.getItem(tblCndnRslt.getValue()).getBean();
			Long cndnId = editQaTestCndnRslt.getQaTstCndnId();
			Collection<?> cndnID = cbTstCondtn.getItemIds();
			for (Iterator<?> iterator = cndnID.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbTstCondtn.getItem(itemId);
				// Get the actual bean and use the data
				TestConditionDM st = (TestConditionDM) item.getBean();
				if (cndnId != null && cndnId.equals(st.getTestCondnId())) {
					cbTstCondtn.select(itemId);
				}
			}
			cbQATstCndnStatus.setValue((String) itselect.getItemProperty("condnStatus").getValue());
			taCndnObservation.setValue((String) itselect.getItemProperty("cndnObserv").getValue());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if (cbClient.getValue() == null) {
			cbClient.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_CLIENT));
			errorFlag = true;
		} else {
			cbClient.setComponentError(null);
		}
		if (cbproduct.getValue() == null) {
			cbproduct.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_PRODUCT));
			errorFlag = true;
		} else {
			cbproduct.setComponentError(null);
		}
		if (cbProdDrg.getValue() == null) {
			cbProdDrg.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_PRDDRG));
			errorFlag = true;
		} else {
			cbProdDrg.setComponentError(null);
		}
		if (cbWorkOrderNo.getValue() == null) {
			cbWorkOrderNo.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_WORKORD_NO));
			errorFlag = true;
		} else {
			cbWorkOrderNo.setComponentError(null);
		}
		if (cbTestGrp.getValue() == null) {
			cbTestGrp.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_TSTGRP));
			errorFlag = true;
		} else {
			cbTestGrp.setComponentError(null);
		}
		if (cbTesttype.getValue() == null) {
			cbTesttype.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_TSTTYPE));
			errorFlag = true;
		} else {
			cbTesttype.setComponentError(null);
		}
		if (tfPrdSlNo.getValue() == "" || tfPrdSlNo.getValue().trim().length() == 0) {
			tfPrdSlNo.setComponentError(new UserError(GERPErrorCodes.NULL_QA_PRDSLNO));
			errorFlag = true;
		} else {
			tfPrdSlNo.setComponentError(null);
		}
		if (tfTstReslt.getValue() == "" || tfTstReslt.getValue().trim().length() == 0) {
			tfTstReslt.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_RESLT));
			errorFlag = true;
		} else {
			tfTstReslt.setComponentError(null);
		}
		if (cbTestCondn.getValue() == null) {
			cbTestCondn.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_TSTCNDN));
			errorFlag = true;
		} else {
			cbTestCondn.setComponentError(null);
		}
		if (pdInspectionDt.getValue() == null) {
			pdInspectionDt.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_INSPDT));
			errorFlag = true;
		} else {
			pdInspectionDt.setComponentError(null);
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Saving Data... ");
			QATestHdrDM saveQaTestHdr = new QATestHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				saveQaTestHdr = beanQATstHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			} else {
				List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyid, +branchID, moduleId, "MF_QAINSNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						saveQaTestHdr.setInspectionno(slnoObj.getKeyDesc());
					}
				}
			}
			saveQaTestHdr.setCompanyid(companyid);
			saveQaTestHdr.setProdslno(tfPrdSlNo.getValue());
			saveQaTestHdr.setTestresult(tfTstReslt.getValue());
			saveQaTestHdr.setClientid((Long) cbClient.getValue());
			saveQaTestHdr.setProductid((Long) cbproduct.getValue());
			saveQaTestHdr.setProddwgid(Long.valueOf(cbProdDrg.getValue().toString()));
			saveQaTestHdr.setWoid(Long.valueOf(cbWorkOrderNo.getValue().toString()));
			saveQaTestHdr.setQatestgroupid((Long) cbTestGrp.getValue());
			saveQaTestHdr.setQattesttypeid(Long.valueOf(cbTesttype.getValue().toString()));
			saveQaTestHdr.setQatestcondid(((Long) cbTestCondn.getValue()));
			saveQaTestHdr.setTeststatus((String) cbQThdrStatus.getValue());
			saveQaTestHdr.setInspectiondate(pdInspectionDt.getValue());
			saveQaTestHdr.setObservation(taTstObservation.getValue());
			saveQaTestHdr.setPreparedby(EmployeeId);
			saveQaTestHdr.setActionedby(null);
			saveQaTestHdr.setReviewedby(null);
			saveQaTestHdr.setLastupdateddate(DateUtils.getcurrentdate());
			saveQaTestHdr.setLastupdatedby(userName);
			serviceQATstHdr.saveQaTestHdr(saveQaTestHdr);
			@SuppressWarnings("unchecked")
			Collection<QATestDtlDM> itemIds = (Collection<QATestDtlDM>) tblQATstDtl.getVisibleItemIds();
			for (QATestDtlDM save : (Collection<QATestDtlDM>) itemIds) {
				save.setQaTstHdrId(Long.valueOf(saveQaTestHdr.getQatestHdrid()));
				serviceQATstDtl.saveQATestDtl(save);
			}
			@SuppressWarnings("unchecked")
			Collection<QATestCndtnResltDM> itemIdss = (Collection<QATestCndtnResltDM>) tblCndnRslt.getVisibleItemIds();
			for (QATestCndtnResltDM save : (Collection<QATestCndtnResltDM>) itemIdss) {
				save.setQaTstHdrtId(Long.valueOf(saveQaTestHdr.getQatestHdrid()));
				serviceQATestCndRslt.saveQATestCndtnReslt(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyid, branchID, moduleId, "MF_QAINSNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSLNo.updateNextSequenceNumber(companyid, branchID, moduleId, "MF_QAINSNO");
					}
				}
			}
			resetFields();
			resetQATestDefDtl();
			resetQATstCndnRslt();
			loadSrchRslt();
			loadSrchQADtlList();
			loadSrchQACndnRsltList();
			comment.saveqaTst(saveQaTestHdr.getQatestHdrid());
			comment.resetFields();
			comment.commentList = new ArrayList<CommentDM>();
			comment.resettbl();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveQaTstDtls() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Saving saveQaTstDtls  Data... ");
		QATestDtlDM qaTestDTl = new QATestDtlDM();
		if (tblQATstDtl.getValue() != null) {
			qaTestDTl = beanQATstDtl.getItem(tblQATstDtl.getValue()).getBean();
			listQATestDtl.remove(qaTestDTl);
		}
		qaTestDTl.setQatestSpecid(((TestSpecificationDM) cbTstSpec.getValue()).getTestSpecId());
		qaTestDTl.setTstSpec(((TestSpecificationDM) cbTstSpec.getValue()).getTestSpec());
		qaTestDTl.setQaTstRemarks(taQcTstDtlRemarks.getValue());
		qaTestDTl.setTstSpecResult(tfQADefntstReslt.getValue());
		try {
			Long.valueOf(tfTstCycle.getValue());
			tfTstCycle.setComponentError(null);
			qaTestDTl.setTstCycleNo(Long.valueOf(tfTstCycle.getValue()));
			qaTestDTl.setQaTstStatus((String) cbQDtlStatus.getValue());
			qaTestDTl.setLastUpdatedDt(DateUtils.getcurrentdate());
			qaTestDTl.setLastUpdatedBy(userName);
			listQATestDtl.add(qaTestDTl);
		}
		catch (NumberFormatException e) {
			tfTstCycle.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TSTSMPL));
		}
		resetQATestDefDtl();
		loadSrchQADtlList();
		btnAddDtl.setCaption("Add");
	}
	
	public Boolean validateTstDefDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "validateTstDefDetails Data ");
		Boolean errorFlag = true;
		if (cbTstSpec.getValue() == null) {
			cbTstSpec.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_SPEC));
			errorFlag = false;
		} else {
			cbTstSpec.setComponentError(null);
		}
		if (tfQADefntstReslt.getValue() == "" || tfQADefntstReslt.getValue() == null
				|| tfQADefntstReslt.getValue().trim().length() == 0) {
			tfQADefntstReslt.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_RESLT));
			errorFlag = false;
		} else {
			tfQADefntstReslt.setComponentError(null);
		}
		if (tfTstCycle.getValue() == "" || tfTstCycle.getValue().trim().length() == 0) {
			tfTstCycle.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TEST_CYC));
			errorFlag = false;
		} else {
			tfTstCycle.setComponentError(null);
		}
		return errorFlag;
	}
	
	public void saveCndnReslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Saving saveCndnReslt Data... ");
			QATestCndtnResltDM qaTestCndnRslt = new QATestCndtnResltDM();
			if (tblCndnRslt.getValue() != null) {
				qaTestCndnRslt = beanQATestCndtnReslt.getItem(tblCndnRslt.getValue()).getBean();
				listQATestCndtnReslt.remove(qaTestCndnRslt);
			}
			qaTestCndnRslt.setQaTstCndnId(((TestConditionDM) cbTstCondtn.getValue()).getTestCondnId());
			qaTestCndnRslt.setTstCondition(((TestConditionDM) cbTstCondtn.getValue()).getTestCondn());
			qaTestCndnRslt.setCndnObserv(taCndnObservation.getValue());
			qaTestCndnRslt.setCondnStatus((String) cbQATstCndnStatus.getValue());
			qaTestCndnRslt.setLastUpdatedBy(userName);
			qaTestCndnRslt.setLastUpdatedDt(DateUtils.getcurrentdate());
			listQATestCndtnReslt.add(qaTestCndnRslt);
			resetQATstCndnRslt();
			loadSrchQACndnRsltList();
			btnAddCndn.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Boolean validateTstCndnDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "validateTstDefDetails Data ");
		Boolean errorFlag = true;
		if (cbTstCondtn.getValue() == null) {
			cbTstCondtn.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_TSTCNDN));
			errorFlag = true;
		} else {
			cbTstCondtn.setComponentError(null);
		}
		return errorFlag;
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Getting audit record for QATest. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_WORKORDER_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", "");
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		cbClient.setRequired(false);
		cbproduct.setRequired(false);
		tfQADefntstReslt.setRequired(false);
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		hlCmdBtnLayout.setVisible(true);
		tblQATstDtl.removeAllItems();
		tblCndnRslt.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		resetQATestDefDtl();
		resetQATstCndnRslt();
		assembleSearchLayout();
		loadSrchRslt();
	}
	
	private void deleteDetails() {
		QATestCndtnResltDM qaTestCndnRslt = new QATestCndtnResltDM();
		if (tblCndnRslt.getValue() != null) {
			qaTestCndnRslt = beanQATestCndtnReslt.getItem(tblCndnRslt.getValue()).getBean();
			listQATestCndtnReslt.remove(qaTestCndnRslt);
			resetQATstCndnRslt();
			tblCndnRslt.setValue("");
			loadSrchQACndnRsltList();
		}
	}
	
	private void deleteSpecDetails() {
		QATestDtlDM aATestDtlDM = new QATestDtlDM();
		if (tblQATstDtl.getValue() != null) {
			aATestDtlDM = beanQATstDtl.getItem(tblQATstDtl.getValue()).getBean();
			listQATestDtl.remove(aATestDtlDM);
			resetQATestDefDtl();
			tblQATstDtl.setValue("");
			loadSrchQADtlList();
		}
	}
}
