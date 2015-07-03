/**
 * File Name	:	QATestType.java
 * Description	:	This Screen Purpose for Modify the QATestType Details.
 * 					Add the TestType details process should be directly added in DB.
 * Author		:	Nandhakumar.S
 * 
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1          2-Sep-2014		  Nandhakumar.S		Initial version 
 */
package com.gnts.mfg.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ProductCategoryListDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.ProductCategoryService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
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
import com.gnts.mfg.domain.mst.TestConditionDM;
import com.gnts.mfg.domain.mst.TestDefnDM;
import com.gnts.mfg.domain.mst.TestGroupDM;
import com.gnts.mfg.domain.mst.TestSpecificationDM;
import com.gnts.mfg.domain.mst.TestTypeDM;
import com.gnts.mfg.service.mst.TestConditionService;
import com.gnts.mfg.service.mst.TestDefnService;
import com.gnts.mfg.service.mst.TestGroupService;
import com.gnts.mfg.service.mst.TestSpecificationService;
import com.gnts.mfg.service.mst.TestTypeService;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class QATestType extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TestTypeService serviceTestType = (TestTypeService) SpringContextHelper.getBean("testType");
	private TestGroupService serviceTestGroup = (TestGroupService) SpringContextHelper.getBean("testGroup");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// User Input Components of TestType
	private TextField tfTestMethdlgy, tftstType;
	private TextArea taTypeDesc;
	private CheckBox chPreCdn;
	private ComboBox cbStatus, cbtstGrp;
	// Type Definition components
	private ComboBox cbCatgry, cbTstDefStatus;
	private ListSelect lsPrdList;
	private Button btnDefSave;
	private Table tblTstDef;
	//
	private FormLayout flCatgry, flCDefSts, flPrdList, flbtnDefSave, flbtnDefDelt;
	private HorizontalLayout hlTypDef;
	private VerticalLayout vlTypDef;
	// Type Specification Components
	private TextField tfTstRecr, tfTstSpec, tfTstCyc;
	private ComboBox cbTstSpecStatus;
	private Table tblTstSpec;
	private Button btnTstSpec;
	//
	private FormLayout flTstRecr, flTstSpec, flTstCyc, flTstSpecSts, flbtnTstSpec, flbtnSpecDelt;
	private HorizontalLayout hlTypSpec;
	private VerticalLayout vlTypSpec;
	// Type Condition Components
	private TextField tfTstCondn, tfTstCondnSpec;
	private ComboBox cbTstCondnStatus;
	private Table tblTstCondn;
	private Button btnTstCondn;
	//
	private TabSheet tabSheet;
	//
	private FormLayout flTstCondn, flTstCondSpec, flTstCondSts, flTstbtnTstCondn, flbtnContdnDelt;
	private HorizontalLayout hlTypCondn;
	private VerticalLayout vlTypCondn;
	private BeanItemContainer<TestTypeDM> beanTestType = null;
	private BeanItemContainer<TestDefnDM> beanTestDef = null;
	private BeanItemContainer<TestSpecificationDM> beanTestSpec = null;
	private BeanItemContainer<TestConditionDM> beanTestCondn = null;
	private List<TestDefnDM> listTestDefn = null;
	private List<TestSpecificationDM> listTestSpecification = null;
	private List<TestConditionDM> listTestCondition = null;
	private TestDefnService serviceTestDefn = (TestDefnService) SpringContextHelper.getBean("testDefn");
	private TestSpecificationService serviceTestSpecification = (TestSpecificationService) SpringContextHelper
			.getBean("testSpec");
	private TestConditionService serviceTestCondition = (TestConditionService) SpringContextHelper.getBean("testCondn");
	private ProductCategoryService serviceProductCategory = (ProductCategoryService) SpringContextHelper
			.getBean("ProductCategory");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	// local variables declaration
	private String userName;
	private Long companyid;
	private int recordCnt;
	private Long testSpecId;
	private String testTypeId;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private int recordTstCondn, recordTestdef, recordTstSpec;
	// Initialize logger
	private Logger logger = Logger.getLogger(QATestType.class);
	private Button btnDeltTstDef = new GERPButton("Delete", "delete", this);
	private Button btnDeltTstSpecf = new GERPButton("Delete", "delete", this);
	private Button btnDeltTstCondtn = new GERPButton("Delete", "delete", this);
	
	// Constructor received the parameters from Login UI class
	public QATestType() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Inside QATestType() constructor");
		// Loading the QATestType UI
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + " QATestType UI");
		//
		btnDeltTstDef.setEnabled(false);
		btnDeltTstSpecf.setEnabled(false);
		btnDeltTstCondtn.setEnabled(false);
		// Type Definition components
		cbCatgry = new ComboBox("Category");
		cbCatgry.setItemCaptionPropertyId("catename");
		cbCatgry.setImmediate(true);
		cbCatgry.setNullSelectionAllowed(false);
		cbCatgry.setWidth("120");
		loadCategoryList();
		cbCatgry.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbCatgry.getValue() != null) {
					loadProductList();
				}
			}
		});
		cbTstDefStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbTstDefStatus.setWidth("120");
		cbTstDefStatus.setNullSelectionAllowed(false);
		cbTstDefStatus.setValue(cbTstDefStatus.getItemIds().iterator().next());
		lsPrdList = new ListSelect("Products");
		lsPrdList.setItemCaptionPropertyId("prodname");
		lsPrdList.setNullSelectionAllowed(false);
		lsPrdList.setMultiSelect(true);
		lsPrdList.setWidth("160");
		lsPrdList.setHeight("65");
		btnDefSave = new Button("Add");
		btnDefSave.addStyleName("add");
		btnDefSave.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (tstDefValidation()) {
					saveTstDefDetails();
				}
			}
		});
		tblTstDef = new Table();
		tblTstDef.setPageLength(5);
		tblTstDef.setWidth("100%");
		tblTstDef.setHeight("167px");
		tblTstDef.setPageLength(5);
		tblTstDef.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblTstDef.isSelected(event.getItemId())) {
					tblTstDef.setImmediate(true);
					btnDefSave.setCaption("Add");
					btnDefSave.setStyleName("savebt");
					resetTstDefDetails();
					btnDeltTstDef.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnDefSave.setCaption("Update");
					btnDefSave.setStyleName("savebt");
					editTstDefDetails();
					btnDeltTstDef.setEnabled(true);
				}
			}
		});
		//
		btnDeltTstDef.setEnabled(false);
		btnDeltTstDef.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteTstDefDetails();
			}
		});
		btnDeltTstCondtn.setEnabled(false);
		btnDeltTstCondtn.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteTstCondtnDetails();
			}
		});
		btnDeltTstSpecf.setEnabled(false);
		btnDeltTstSpecf.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteTstSpecDetails();
			}
		});
		//
		flCatgry = new FormLayout();
		flCDefSts = new FormLayout();
		flPrdList = new FormLayout();
		flbtnDefSave = new FormLayout();
		flbtnDefDelt = new FormLayout();
		//
		flCatgry.addComponent(cbCatgry);
		flPrdList.addComponent(lsPrdList);
		flCDefSts.addComponent(cbTstDefStatus);
		flbtnDefSave.addComponent(btnDefSave);
		flbtnDefDelt.addComponent(btnDeltTstDef);
		//
		hlTypDef = new HorizontalLayout();
		hlTypDef.addComponent(flCatgry);
		hlTypDef.addComponent(flPrdList);
		hlTypDef.addComponent(flCDefSts);
		hlTypDef.addComponent(flbtnDefSave);
		hlTypDef.addComponent(flbtnDefDelt);
		hlTypDef.setSpacing(true);
		hlTypDef.setMargin(true);
		//
		vlTypDef = new VerticalLayout();
		vlTypDef.addComponent(hlTypDef);
		vlTypDef.addComponent(tblTstDef);
		// Type Specification Components
		tfTstRecr = new TextField("Test Requirement");
		tfTstRecr.setImmediate(true);
		tfTstRecr.setWidth("140");
		tfTstSpec = new TextField("Test Specification");
		tfTstSpec.setWidth("140");
		tfTstCyc = new TextField("Test Cycles");
		tfTstCyc.setWidth("120");
		cbTstSpecStatus = new ComboBox("Status");
		cbTstSpecStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbTstSpecStatus.setWidth("120");
		cbTstSpecStatus.setValue(cbTstSpecStatus.getItemIds().iterator().next());
		tblTstSpec = new Table();
		tblTstSpec.setPageLength(7);
		tblTstSpec.setWidth("100%");
		tblTstSpec.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblTstSpec.isSelected(event.getItemId())) {
					tblTstSpec.setImmediate(true);
					btnTstSpec.setCaption("Add");
					btnTstSpec.setStyleName("savebt");
					resetTstSpec();
					btnDeltTstSpecf.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnTstSpec.setCaption("Update");
					btnTstSpec.setStyleName("savebt");
					editTstSpecDetails();
					btnDeltTstSpecf.setEnabled(true);
				}
			}
		});
		btnTstSpec = new Button("Add");
		btnTstSpec.setStyleName("add");
		btnTstSpec.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (tstSpecValidation()) {
					saveTstSpecDetails();
				}
			}
		});
		//
		flTstRecr = new FormLayout();
		flTstSpec = new FormLayout();
		flTstCyc = new FormLayout();
		flbtnTstSpec = new FormLayout();
		flTstSpecSts = new FormLayout();
		flbtnSpecDelt = new FormLayout();
		//
		flTstRecr.addComponent(tfTstRecr);
		flTstSpec.addComponent(tfTstSpec);
		flTstCyc.addComponent(tfTstCyc);
		flTstSpecSts.addComponent(cbTstSpecStatus);
		flbtnTstSpec.addComponent(btnTstSpec);
		flbtnSpecDelt.addComponent(btnDeltTstSpecf);
		//
		hlTypSpec = new HorizontalLayout();
		hlTypSpec.addComponent(flTstRecr);
		hlTypSpec.addComponent(flTstSpec);
		hlTypSpec.addComponent(flTstCyc);
		hlTypSpec.addComponent(flTstSpecSts);
		hlTypSpec.addComponent(flbtnTstSpec);
		hlTypSpec.addComponent(flbtnSpecDelt);
		hlTypSpec.setSpacing(true);
		hlTypSpec.setMargin(true);
		//
		vlTypSpec = new VerticalLayout();
		vlTypSpec.addComponent(hlTypSpec);
		vlTypSpec.addComponent(tblTstSpec);
		// Type Condition Components
		tfTstCondn = new TextField("Condition");
		tfTstCondnSpec = new TextField("Test Specification");
		cbTstCondnStatus = new ComboBox("Status");
		cbTstCondnStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbTstCondnStatus.setValue(cbTstCondnStatus.getItemIds().iterator().next());
		tblTstCondn = new Table();
		tblTstCondn.setPageLength(7);
		tblTstCondn.setWidth("100%");
		tblTstCondn.setHeight("211px");
		tblTstCondn.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblTstCondn.isSelected(event.getItemId())) {
					tblTstCondn.setImmediate(true);
					btnTstCondn.setCaption("Add");
					btnTstCondn.setStyleName("savebt");
					resetTstCondn();
					btnDeltTstCondtn.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnTstCondn.setCaption("Update");
					btnTstCondn.setStyleName("savebt");
					editTstCondnDetails();
					btnDeltTstCondtn.setEnabled(true);
				}
			}
		});
		btnTstCondn = new Button("Add");
		btnTstCondn.setStyleName("add");
		btnTstCondn.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (tstCondnValidation()) {
					saveTstCondnDetails();
				}
			}
		});
		//
		flTstCondn = new FormLayout();
		flTstCondSpec = new FormLayout();
		flTstCondSts = new FormLayout();
		flTstbtnTstCondn = new FormLayout();
		flbtnContdnDelt = new FormLayout();
		//
		flTstCondn.addComponent(tfTstCondn);
		flTstCondSpec.addComponent(tfTstCondnSpec);
		flTstCondSts.addComponent(cbTstCondnStatus);
		flTstbtnTstCondn.addComponent(btnTstCondn);
		flbtnContdnDelt.addComponent(btnDeltTstCondtn);
		//
		hlTypCondn = new HorizontalLayout();
		hlTypCondn.addComponent(flTstCondn);
		hlTypCondn.addComponent(flTstCondSpec);
		hlTypCondn.addComponent(flTstCondSts);
		hlTypCondn.addComponent(flTstbtnTstCondn);
		hlTypCondn.addComponent(flbtnContdnDelt);
		hlTypCondn.setSpacing(true);
		hlTypCondn.setMargin(true);
		//
		vlTypCondn = new VerticalLayout();
		vlTypCondn.addComponent(hlTypCondn);
		vlTypCondn.addComponent(tblTstCondn);
		//
		// TestDefn Components
		cbtstGrp = new GERPComboBox("Test Group");
		cbtstGrp.setItemCaptionPropertyId("testGroup");
		cbtstGrp.setWidth("150");
		loadTestGrpList();
		tftstType = new GERPTextField("Type");
		tfTestMethdlgy = new GERPTextField("Test Methodology");
		taTypeDesc = new GERPTextArea("Description");
		taTypeDesc.setWidth("250");
		taTypeDesc.setHeight("55");
		chPreCdn = new CheckBox("Pre-Condition");
		chPreCdn.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (chPreCdn.getValue().equals(true)) {
					tabSheet.getTab(vlTypCondn).setEnabled(true);
				} else {
					tabSheet.getTab(vlTypCondn).setEnabled(false);
					resetTstCondn();
					tblTstCondn.removeAllItems();
					listTestCondition = new ArrayList<TestConditionDM>();
					loadSrchTstCondnRslt();
				}
			}
		});
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("120");
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		loadSrchTstDefRslt();
		loadSrchTstSpecRslt();
		loadSrchTstCondnRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for QATestType UI search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		// Adding components into form layouts for QATestType UI search layout
		flColumn1.addComponent(tftstType);
		flColumn2.addComponent(cbStatus);
		// Adding form layouts into search layout for QATestType UI search mode
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
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
		cbtstGrp.setRequired(true);
		tftstType.setRequired(true);
		tfTestMethdlgy.setRequired(true);
		cbCatgry.setRequired(true);
		lsPrdList.setRequired(true);
		tfTstCondn.setRequired(true);
		tfTstCondnSpec.setRequired(true);
		tfTstCyc.setRequired(true);
		tfTstRecr.setRequired(true);
		tfTstSpec.setRequired(true);
		// Removing components from search layout and re-initializing form layouts
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		// adding components into first column in form layout1
		flColumn1.addComponent(cbtstGrp);
		flColumn1.addComponent(tftstType);
		// adding components into second column in form layout2
		flColumn2.addComponent(tfTestMethdlgy);
		flColumn2.addComponent(chPreCdn);
		flColumn3.addComponent(taTypeDesc);
		// adding components into third column in form layout3
		flColumn4.addComponent(cbStatus);
		HorizontalLayout hlTstType = new HorizontalLayout();
		hlTstType.addComponent(flColumn1);
		hlTstType.addComponent(flColumn2);
		hlTstType.addComponent(flColumn3);
		hlTstType.addComponent(flColumn4);
		hlTstType.setSpacing(true);
		hlTstType.setMargin(true);
		//
		tabSheet = new TabSheet();
		tabSheet.setWidth("100%");
		tabSheet.setHeight("315");
		tabSheet.addTab(vlTypDef, "Test Definition", null);
		tabSheet.addTab(vlTypSpec, "Test Specification", null);
		tabSheet.addTab(vlTypCondn, "Test Condition", null);
		tabSheet.getTab(vlTypCondn).setEnabled(false);
		VerticalLayout vlAllComponent = new VerticalLayout();
		vlAllComponent.addComponent(hlTstType);
		vlAllComponent.addComponent(GERPPanelGenerator.createPanel(hlTstType));
		vlAllComponent.addComponent(tabSheet);
		vlAllComponent.setSpacing(true);
		vlAllComponent.setWidth("100%");
		// adding form layouts into user input layouts
		hlUserInputLayout.addComponent(vlAllComponent);
		hlUserInputLayout.setWidth("100%");
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		List<TestTypeDM> testTypeList = new ArrayList<TestTypeDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyid);
		testTypeList = serviceTestType.getTestTypeDetails(companyid, null, tftstType.getValue().toString(), cbStatus
				.getValue().toString());
		recordCnt = testTypeList.size();
		beanTestType = new BeanItemContainer<TestTypeDM>(TestTypeDM.class);
		beanTestType.addAll(testTypeList);
		tblMstScrSrchRslt.setContainerDataSource(beanTestType);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "qaTstTypId", "testGroup", "tstType", "tstMldlgy",
				"tstTypeDesc", "preCondtnYN", "tstTypStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Test Group", "Type", "Methodology", "Description",
				"Pre.Condition", "Status", "Last Updated Dt.", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("qaTstTypId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadSrchTstDefRslt() {
		logger.info("Test Defition Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Loading Test Defition Search...");
		recordTestdef = listTestDefn.size();
		beanTestDef = new BeanItemContainer<TestDefnDM>(TestDefnDM.class);
		beanTestDef.addAll(listTestDefn);
		tblTstDef.setSelectable(true);
		tblTstDef.setContainerDataSource(beanTestDef);
		tblTstDef.setVisibleColumns(new Object[] { "questdefid", "catgName", "productName", "tstdfnstatus",
				"lastupdateddate", "lastupdatedby" });
		tblTstDef.setColumnHeaders(new String[] { "Ref.ID", "Category Name", "Product Name", "Status",
				"Last Updated Date", "Last Updated By" });
		tblTstDef.setColumnAlignment("questdefid", Align.RIGHT);
		tblTstDef.setFooterVisible(true);
		tblTstDef.setColumnFooter("lastupdatedby", "No.of Records : " + recordTestdef);
	}
	
	private void loadSrchTstSpecRslt() {
		logger.info("Masterial Consumer Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Loading Material Consumer Search...");
		recordTstSpec = listTestSpecification.size();
		beanTestSpec = new BeanItemContainer<TestSpecificationDM>(TestSpecificationDM.class);
		beanTestSpec.addAll(listTestSpecification);
		tblTstSpec.setSelectable(true);
		tblTstSpec.setContainerDataSource(beanTestSpec);
		tblTstSpec.setVisibleColumns(new Object[] { "testSpecId", "testRqrmt", "testSpec", "testCycl", "tstPrmStatus",
				"lastUpdatedDt", "lastUpdatedBy" });
		tblTstSpec.setColumnHeaders(new String[] { "Ref.ID", "Test Requirement", "Test Specification", "Test Cycles",
				"Status", "Last Updated Date", "Last Updated By" });
		tblTstSpec.setColumnAlignment("testSpecId", Align.RIGHT);
		tblTstSpec.setFooterVisible(true);
		tblTstSpec.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordTstSpec);
	}
	
	private void loadSrchTstCondnRslt() {
		logger.info("Material Specification Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Loading Material Specification Search...");
		recordTstCondn = listTestCondition.size();
		beanTestCondn = new BeanItemContainer<TestConditionDM>(TestConditionDM.class);
		beanTestCondn.addAll(listTestCondition);
		tblTstCondn.setSelectable(true);
		tblTstCondn.setContainerDataSource(beanTestCondn);
		tblTstCondn.setVisibleColumns(new Object[] { "testCondnId", "testCondn", "testSpec", "testCondnStatus",
				"lastUpDatedDt", "lastUpDatedBy" });
		tblTstCondn.setColumnHeaders(new String[] { "Ref.ID", "Test Condition", "Test Specification", "Status",
				"Last Updated Date", "Last Updated By" });
		tblTstCondn.setColumnAlignment("testCondnId", Align.RIGHT);
		tblTstCondn.setFooterVisible(true);
		tblTstCondn.setColumnFooter("lastUpDatedBy", "No.of Records : " + recordTstCondn);
	}
	
	private void loadTestGrpList() {
		BeanContainer<Long, TestGroupDM> beanCity = new BeanContainer<Long, TestGroupDM>(TestGroupDM.class);
		beanCity.setBeanIdProperty("qaTestGpID");
		beanCity.addAll(serviceTestGroup.getTestGpDetails(companyid, null, "Active", "F"));
		cbtstGrp.setContainerDataSource(beanCity);
	}
	
	private void loadCategoryList() {
		BeanItemContainer<ProductCategoryListDM> beanProdCatgry = new BeanItemContainer<ProductCategoryListDM>(
				ProductCategoryListDM.class);
		beanProdCatgry.addAll(serviceProductCategory.getProdCategoryList(null, null, null, "Active", null, "F"));
		cbCatgry.setContainerDataSource(beanProdCatgry);
	}
	
	private void loadProductList() {
		BeanContainer<Long, ProductDM> beanProduct = new BeanContainer<Long, ProductDM>(ProductDM.class);
		beanProduct.setBeanIdProperty("prodid");
		beanProduct.addAll(serviceProduct.getProductList(companyid, null, null, null, "Active",
				((ProductCategoryListDM) cbCatgry.getValue()).getCateid(), null, "F"));
		lsPrdList.setContainerDataSource(beanProduct);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tftstType.setValue("");
		tftstType.setComponentError(null);
		cbtstGrp.setValue(null);
		cbtstGrp.setComponentError(null);
		tfTestMethdlgy.setValue("");
		tfTestMethdlgy.setComponentError(null);
		taTypeDesc.setValue("");
		tfTstCyc.setValue("0");
		chPreCdn.setValue(false);
		tfTstCyc.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		listTestDefn = new ArrayList<TestDefnDM>();
		listTestSpecification = new ArrayList<TestSpecificationDM>();
		listTestCondition = new ArrayList<TestConditionDM>();
		tblTstDef.removeAllItems();
		tblTstSpec.removeAllItems();
		tblTstCondn.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editTestTypeDetails() {
		if (tblMstScrSrchRslt.getValue() != null) {
			TestTypeDM testTypeDM = beanTestType.getItem(tblMstScrSrchRslt.getValue()).getBean();
			testTypeId = testTypeDM.getQaTstTypId().toString();
			tftstType.setValue(testTypeDM.getTstType());
			cbtstGrp.setValue(testTypeDM.getQaTstGpId().toString());
			tfTestMethdlgy.setValue(testTypeDM.getTstMldlgy());
			if (testTypeDM.getTstTypeDesc() != null) {
				taTypeDesc.setValue(testTypeDM.getTstTypeDesc());
			}
			cbStatus.setValue(testTypeDM.getTstTypStatus());
			if (testTypeDM.getPreCondtnYN().equals("Yes")) {
				chPreCdn.setValue(true);
			} else {
				chPreCdn.setValue(false);
			}
			listTestDefn = serviceTestDefn.getTestDefnDetails(null, Long.valueOf(testTypeId), companyid, null, null,
					"Active");
			listTestSpecification = serviceTestSpecification.getTestSpecDetails(companyid, Long.valueOf(testTypeId),
					"Active");
			listTestCondition = serviceTestCondition.getTestCondnDetails(companyid, null, Long.valueOf(testTypeId),
					"Active");
		}
	}
	
	private void editTstDefDetails() {
		if (tblTstDef.getValue() != null) {
			TestDefnDM editTstDefn = new TestDefnDM();
			editTstDefn = beanTestDef.getItem(tblTstDef.getValue()).getBean();
			Long catgryID = editTstDefn.getCategoryid();
			Collection<?> catID = cbCatgry.getItemIds();
			for (Iterator<?> iterator = catID.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbCatgry.getItem(itemId);
				// Get the actual bean and use the data
				ProductCategoryListDM st = (ProductCategoryListDM) item.getBean();
				if (catgryID != null && catgryID.equals(st.getCateid())) {
					cbCatgry.setValue(itemId);
				}
			}
			lsPrdList.setValue(null);
			Long productID = editTstDefn.getProductid();
			Collection<?> prdID = lsPrdList.getItemIds();
			for (Iterator<?> iterator = prdID.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) lsPrdList.getItem(itemId);
				// Get the actual bean and use the data
				ProductDM st = (ProductDM) item.getBean();
				if (productID != null && productID.equals(st.getProdid())) {
					lsPrdList.select(itemId);
				}
			}
			cbTstDefStatus.setValue(editTstDefn.getTstdfnstatus());
		}
	}
	
	private void editTstSpecDetails() {
		if (tblTstSpec.getValue() != null) {
			logger.info("testSpecId=editTstSpec.getTestSpecId()>>" + testSpecId);
			TestSpecificationDM testSpecificationDM = beanTestSpec.getItem(tblTstSpec.getValue()).getBean();
			testSpecId = testSpecificationDM.getTestSpecId();
			tfTstRecr.setValue(testSpecificationDM.getTestRqrmt());
			tfTstSpec.setValue(testSpecificationDM.getTestSpec());
			tfTstCyc.setValue(Long.valueOf(testSpecificationDM.getTestCycl()).toString());
			cbTstSpecStatus.setValue(testSpecificationDM.getTstPrmStatus());
		}
	}
	
	private void editTstCondnDetails() {
		Item itselect = tblTstCondn.getItem(tblTstCondn.getValue());
		if (itselect != null) {
			tfTstCondn.setValue((String) itselect.getItemProperty("testCondn").getValue());
			tfTstCondnSpec.setValue((String) itselect.getItemProperty("testSpec").getValue());
			cbTstDefStatus.setValue((String) itselect.getItemProperty("testCondnStatus").getValue());
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tftstType.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		resetFields();
		resetTstDefDetails();
		resetTstSpec();
		resetTstCondn();
		resetTstCondn();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Getting audit record for TestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_MFG_QA_TEST_TYPE);
		UI.getCurrent().getSession().setAttribute("audittablepk", testTypeId);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		tftstType.setComponentError(null);
		cbtstGrp.setComponentError(null);
		resetTstDefDetails();
		resetTstSpec();
		resetTstCondn();
		resetFields();
		cbtstGrp.setRequired(false);
		tftstType.setRequired(false);
		tfTestMethdlgy.setRequired(false);
		cbCatgry.setRequired(false);
		lsPrdList.setRequired(false);
		tfTstCondn.setRequired(false);
		tfTstCondnSpec.setRequired(false);
		tfTstCyc.setRequired(false);
		tfTstRecr.setRequired(false);
		tfTstSpec.setRequired(false);
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		editTestTypeDetails();
		editTstDefDetails();
		editTstSpecDetails();
		editTstCondnDetails();
		loadSrchTstDefRslt();
		loadSrchTstSpecRslt();
		loadSrchTstCondnRslt();
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Validating Data ");
		tftstType.setComponentError(null);
		Boolean errorFlag = false;
		if (cbtstGrp.getValue() == null) {
			cbtstGrp.setComponentError(new UserError(GERPErrorCodes.NULL_TEST_GROUP_NAME));
			errorFlag = true;
		} else {
			cbtstGrp.setComponentError(null);
		}
		if (tftstType.getValue() == null || tftstType.getValue().trim().length() == 0) {
			tftstType.setComponentError(new UserError(GERPErrorCodes.NULL_TEST_TYPE));
			errorFlag = true;
		} else {
			tftstType.setComponentError(null);
		}
		if (tfTestMethdlgy.getValue() == null || tfTestMethdlgy.getValue().trim().length() == 0) {
			tfTestMethdlgy.setComponentError(new UserError(GERPErrorCodes.NULL_TEST_METHODOLOGY));
			errorFlag = true;
		} else {
			tfTestMethdlgy.setComponentError(null);
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Saving Data... ");
		try {
			TestTypeDM tstType = new TestTypeDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				tstType = beanTestType.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			tstType.setCompanyId(companyid);
			tstType.setQaTstGpId(Long.valueOf(cbtstGrp.getValue().toString()));
			tstType.setTstType(tftstType.getValue());
			tstType.setTstMldlgy(tfTestMethdlgy.getValue());
			tstType.setTstTypeDesc(taTypeDesc.getValue());
			tstType.setTstTypStatus((String) cbStatus.getValue());
			if (chPreCdn.getValue().equals(true)) {
				tstType.setPreCondtnYN("Y");
			} else {
				tstType.setPreCondtnYN("N");
			}
			tstType.setLastUpdatedDt(DateUtils.getcurrentdate());
			tstType.setLastUpdatedBy(userName);
			serviceTestType.saveTestTypeDetails(tstType);
			// To save Test Definition
			@SuppressWarnings("unchecked")
			Collection<TestDefnDM> itemIds = (Collection<TestDefnDM>) tblTstDef.getVisibleItemIds();
			for (TestDefnDM save : (Collection<TestDefnDM>) itemIds) {
				save.setQatesttypeid(Long.valueOf(tstType.getQaTstTypId()));
				serviceTestDefn.saveTestDefn(save);
			}
			// To save Test Specification
			@SuppressWarnings("unchecked")
			Collection<TestSpecificationDM> itemIds2 = (Collection<TestSpecificationDM>) tblTstSpec.getVisibleItemIds();
			for (TestSpecificationDM save : (Collection<TestSpecificationDM>) itemIds2) {
				save.setQaTestTypeId(Long.valueOf(tstType.getQaTstTypId()));
				serviceTestSpecification.saveTestSpec(save);
			}
			// To save Test Condition
			@SuppressWarnings("unchecked")
			Collection<TestConditionDM> itemIds3 = (Collection<TestConditionDM>) tblTstCondn.getVisibleItemIds();
			for (TestConditionDM save : (Collection<TestConditionDM>) itemIds3) {
				save.setTestTypeId(Long.valueOf(tstType.getQaTstTypId()));
				serviceTestCondition.saveTestCondn(save);
			}
			resetFields();
			resetTstCondn();
			resetTstDefDetails();
			resetTstSpec();
			loadSrchRslt();
			loadSrchTstCondnRslt();
			loadSrchTstDefRslt();
			loadSrchTstSpecRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean tstDefValidation() {
		boolean isValid = true;
		if (cbCatgry.getValue() == null) {
			cbCatgry.setComponentError(new UserError(GERPErrorCodes.NULL_CATGRY_NAME));
			isValid = false;
		} else {
			cbCatgry.setComponentError(null);
		}
		if (lsPrdList.getValue().toString() == "[]") {
			lsPrdList.setComponentError(new UserError(GERPErrorCodes.NULL_TEST_PRODUCT_NAME));
			isValid = false;
		} else {
			lsPrdList.setComponentError(null);
		}
		return isValid;
	}
	
	private void saveTstDefDetails() {
		int count = 0;
		String[] split = lsPrdList.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
		for (String obj : split) {
			if (obj.trim().length() > 0) {
				for (TestDefnDM tstDefDM : listTestDefn) {
					// System.out.println("mat,getv" + tstDefDM.getProductId() + "," + Long.valueOf(obj.trim()));
					// System.out.println("cbmat" + lsPrdList.getValue());
					if (tstDefDM.getProductid().equals(Long.valueOf(obj.trim()))) {
						count++;
						break;
					}
				}
				System.out.println("count--->" + count);
				if (tblTstDef.getValue() != null) {
					count = 0;
				}
				if (count == 0) {
					/*
					 * @SuppressWarnings({ "rawtypes", "unchecked" }) List<ProductDM> prdList = new
					 * ArrayList<ProductDM>((Set) lsPrdList.getValue()); for (ProductDM obj : prdList) {
					 */TestDefnDM objTstDef = new TestDefnDM();
					if (tblTstDef.getValue() != null) {
						objTstDef = beanTestDef.getItem(tblTstDef.getValue()).getBean();
						listTestDefn.remove(objTstDef);
					}
					objTstDef.setCompanyid(companyid);
					if (cbCatgry.getValue() != null) {
						objTstDef.setCategoryid(((ProductCategoryListDM) cbCatgry.getValue()).getCateid());
						objTstDef.setCatgName(((ProductCategoryListDM) cbCatgry.getValue()).getCatename());
					}
					if (lsPrdList.getValue() != null) {
						objTstDef.setProductid(Long.valueOf(obj.trim()));
						objTstDef.setProductName(serviceProduct
								.getProductList(null, Long.valueOf(obj.trim()), null, null, null, null, null, "P")
								.get(0).getProdname());
					}
					objTstDef.setTstdfnstatus((String) cbTstDefStatus.getValue());
					objTstDef.setLastupdateddate(DateUtils.getcurrentdate());
					objTstDef.setLastupdatedby(userName);
					if (cbCatgry.getValue() != null && lsPrdList.getValue() != null) {
						listTestDefn.add(objTstDef);
					}
				}
			}
			loadSrchTstDefRslt();
			btnDefSave.setCaption("Add");
		}
		resetTstDefDetails();
	}
	
	public void resetTstDefDetails() {
		cbCatgry.setValue(null);
		cbCatgry.setComponentError(null);
		lsPrdList.removeAllItems();
		lsPrdList.setComponentError(null);
		cbTstDefStatus.setValue(cbTstDefStatus.getItemIds().iterator().next());
	}
	
	private boolean tstSpecValidation() {
		boolean isValid = true;
		if (tfTstRecr.getValue() == "" || tfTstRecr.getValue() == null || tfTstRecr.getValue().trim().length() == 0) {
			tfTstRecr.setComponentError(new UserError(GERPErrorCodes.NULL_TEST_RECRMNT));
			isValid = false;
		} else {
			tfTstRecr.setComponentError(null);
		}
		if (tfTstSpec.getValue() == "" || tfTstSpec.getValue() == null || tfTstSpec.getValue().trim().length() == 0) {
			tfTstSpec.setComponentError(new UserError(GERPErrorCodes.NULL_TEST_SPEC));
			isValid = false;
		} else {
			tfTstSpec.setComponentError(null);
		}
		try {
			Long.valueOf(tfTstCyc.getValue());
			tfTstCyc.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfTstCyc.setComponentError(new UserError(GERPErrorCodes.NULL_TEST_CYC));
			isValid = false;
		}
		return isValid;
	}
	
	private void saveTstSpecDetails() {
		TestSpecificationDM objTstSpec = new TestSpecificationDM();
		if (tblTstSpec.getValue() != null) {
			objTstSpec = beanTestSpec.getItem(tblTstSpec.getValue()).getBean();
			listTestSpecification.remove(objTstSpec);
			listTestSpecification.remove(objTstSpec);
		}
		objTstSpec.setCompanyId(companyid);
		objTstSpec.setTestRqrmt(tfTstRecr.getValue());
		objTstSpec.setTestSpec(tfTstSpec.getValue());
		objTstSpec.setTestCycl(Long.valueOf(tfTstCyc.getValue()));
		objTstSpec.setTstPrmStatus(cbTstSpecStatus.getValue().toString());
		objTstSpec.setLastUpdatedDt(DateUtils.getcurrentdate());
		objTstSpec.setLastUpdatedBy(userName);
		listTestSpecification.add(objTstSpec);
		resetTstSpec();
		loadSrchTstSpecRslt();
		btnTstSpec.setCaption("Add");
	}
	
	public void resetTstSpec() {
		tfTstRecr.setValue("");
		tfTstRecr.setComponentError(null);
		tfTstSpec.setValue("");
		tfTstSpec.setComponentError(null);
		tfTstCyc.setValue("");
		tfTstCyc.setComponentError(null);
		cbTstSpecStatus.setValue(cbTstSpecStatus.getItemIds().iterator().next());
	}
	
	private boolean tstCondnValidation() {
		boolean isValid = true;
		if (tfTstCondn.getValue() == "" || tfTstCondn.getValue() == null || tfTstCondn.getValue().trim().length() == 0) {
			tfTstCondn.setComponentError(new UserError(GERPErrorCodes.NULL_TEST_CONDN));
			isValid = false;
		} else {
			tfTstCondn.setComponentError(null);
		}
		if (tfTstCondnSpec.getValue() == "" || tfTstCondnSpec.getValue() == null
				|| tfTstCondnSpec.getValue().trim().length() == 0) {
			tfTstCondnSpec.setComponentError(new UserError(GERPErrorCodes.NULL_TEST_CONDN_SPEC));
			isValid = false;
		} else {
			tfTstCondnSpec.setComponentError(null);
		}
		return isValid;
	}
	
	private void saveTstCondnDetails() {
		try {
			TestConditionDM objTstCondn = new TestConditionDM();
			if (tblTstCondn.getValue() != null) {
				objTstCondn = beanTestCondn.getItem(tblTstCondn.getValue()).getBean();
				listTestCondition.remove(objTstCondn);
			}
			objTstCondn.setCompanyId(companyid);
			objTstCondn.setTestCondn(tfTstCondn.getValue());
			objTstCondn.setTestSpec(tfTstCondnSpec.getValue());
			objTstCondn.setTestCondnStatus((String) cbTstCondnStatus.getValue());
			objTstCondn.setLastUpDatedDt(DateUtils.getcurrentdate());
			objTstCondn.setLastUpDatedBy(userName);
			listTestCondition.add(objTstCondn);
			resetTstCondn();
			loadSrchTstCondnRslt();
			btnTstCondn.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void resetTstCondn() {
		tfTstCondn.setValue("");
		tfTstCondn.setComponentError(null);
		tfTstCondnSpec.setValue("");
		tfTstCondn.setComponentError(null);
		cbTstCondnStatus.setValue(cbTstCondnStatus.getItemIds().iterator().next());
	}
	
	private void deleteTstDefDetails() {
		TestDefnDM testDefnDM = new TestDefnDM();
		if (tblTstDef.getValue() != null) {
			testDefnDM = beanTestDef.getItem(tblTstDef.getValue()).getBean();
			listTestDefn.remove(testDefnDM);
			resetTstDefDetails();
			tblTstDef.setValue("");
			loadSrchTstDefRslt();
			btnDeltTstDef.setEnabled(false);
		}
	}
	
	private void deleteTstCondtnDetails() {
		TestConditionDM testConditionDM = new TestConditionDM();
		if (tblTstCondn.getValue() != null) {
			testConditionDM = beanTestCondn.getItem(tblTstCondn.getValue()).getBean();
			listTestCondition.remove(testConditionDM);
			resetTstCondn();
			tblTstCondn.setValue("");
			loadSrchTstCondnRslt();
			btnDeltTstCondtn.setEnabled(false);
		}
	}
	
	private void deleteTstSpecDetails() {
		TestSpecificationDM testSpecificationDM = new TestSpecificationDM();
		if (tblTstSpec.getValue() != null) {
			testSpecificationDM = beanTestSpec.getItem(tblTstSpec.getValue()).getBean();
			listTestSpecification.remove(testSpecificationDM);
			resetTstSpec();
			tblTstSpec.setValue("");
			loadSrchTstSpecRslt();
			btnDeltTstSpecf.setEnabled(false);
		}
	}
}
