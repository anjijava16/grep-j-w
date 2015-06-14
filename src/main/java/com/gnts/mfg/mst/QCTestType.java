/**
 * File Name 		: QCTestType.java 
 * Description 		: this class is used for add/edit QCTest Type details. 
 * Author 			: Nandhakumar S
 * Date 			: 05-Sep-2014
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * Version       Date           	Modified By               Remarks
 * 0.1          05-Sep-2014       	 Nandhakumar S	        Initial Version
 */
package com.gnts.mfg.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.mst.QCTestDefinitionDM;
import com.gnts.mfg.domain.mst.QCTestSpecificationDM;
import com.gnts.mfg.domain.mst.QCTestTypeDM;
import com.gnts.mfg.domain.mst.TestConditionDM;
import com.gnts.mfg.service.mst.QCTestDefService;
import com.gnts.mfg.service.mst.QCTestSpecService;
import com.gnts.mfg.service.mst.QCTestTypeService;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.mst.MaterialTypeDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.mst.MaterialTypeService;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class QCTestType extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private QCTestTypeService serviceTestType = (QCTestTypeService) SpringContextHelper.getBean("qcTestType");
	private QCTestDefService serviceTestDef = (QCTestDefService) SpringContextHelper.getBean("qcTestDef");
	private QCTestSpecService serviceTestSpec = (QCTestSpecService) SpringContextHelper.getBean("qcTestSpec");
	private MaterialTypeService serviceMaterialTyp = (MaterialTypeService) SpringContextHelper.getBean("materialType");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	List<QCTestDefinitionDM> listTstDef = null;
	List<QCTestSpecificationDM> listTstSpec = null;
	private BeanItemContainer<QCTestTypeDM> beanTestType = null;
	private BeanItemContainer<QCTestDefinitionDM> beanTestDef = null;
	private BeanItemContainer<QCTestSpecificationDM> beanTestSpec = null;
	//
	private TextField tftstTyp, tfTstMethdgly;
	private TextArea taTstDesc;
	private ComboBox cbTstTypeStatus;
	private FormLayout fltstTyp, fltstMethdgly, flTstDesc, flTstSts;
	//
	private ComboBox cbMtrlType, cbTstDefStatus;
	private ListSelect lsMtrlName;
	private Table tblTstdef;
	private Button btnTstDef;
	private FormLayout flMtrlTyp, flTstdefSts, flTstdefbtn;
	//
	private TextField tfTstRecrt, tfTstSpec, tfTstCycle;
	private ComboBox cbTstSpecStatus;
	private Table tblTstSpec;
	private Button btnTstSpec;
	private FormLayout flTstRecr, flTstCyc;
	private String userName;
	private Long companyid;
	private static Logger logger = Logger.getLogger(QCTestType.class);
	private GERPAddEditHLayout hlSearchLayout;
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	HorizontalLayout hlTstTyp, hlTstDef, hlTstSpec, hlTstDefNTstSpec;
	VerticalLayout vlTstdef, vlTstSpec;
	private int recordCnt, recordTestdef, recordTestSpec;
	private String testTypeId;
	public Button btnDeltSpec = new GERPButton("Delete", "delete", this);
	public Button btnDeltMtrl = new GERPButton("Delete", "delete", this);
	
	public QCTestType() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Inside QCTestType() constructor");
		// Loading the QCTestType UI
		buildView();
	}
	
	private void buildView() {
		tftstTyp = new TextField("Test Type");
		tfTstMethdgly = new TextField("Methodology");
		taTstDesc = new TextArea("Description");
		taTstDesc.setWidth("250");
		taTstDesc.setHeight("55");
		cbTstTypeStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbTstTypeStatus.setWidth("120");
		cbTstTypeStatus.setNullSelectionAllowed(false);
		cbTstTypeStatus.setValue(cbTstTypeStatus.getItemIds().iterator().next());
		//
		cbMtrlType = new ComboBox("Materia Type");
		cbMtrlType.setWidth("130");
		cbMtrlType.setItemCaptionPropertyId("materialTypeName");
		cbMtrlType.setNullSelectionAllowed(false);
		cbMtrlType.setImmediate(true);
		loadMaterialTypList();
		cbMtrlType.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbMtrlType.getValue() != null) {
					loadMaterialList();
				}
			}
		});
		lsMtrlName = new ListSelect("Material Name");
		lsMtrlName.setItemCaptionPropertyId("materialName");
		lsMtrlName.setNullSelectionAllowed(false);
		lsMtrlName.setMultiSelect(true);
		lsMtrlName.setWidth("147");
		lsMtrlName.setHeight("63");
		cbTstDefStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbTstDefStatus.setValue(cbTstDefStatus.getItemIds().iterator().next());
		cbTstDefStatus.setNullSelectionAllowed(false);
		cbTstDefStatus.setWidth("130");
		tblTstdef = new Table();
		tblTstdef.setWidth("100%");
		tblTstdef.setPageLength(7);
		tblTstdef.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblTstdef.isSelected(event.getItemId())) {
					tblTstdef.setImmediate(true);
					btnTstDef.setCaption("Add");
					btnTstDef.setStyleName("savebt");
					resetTstDefDetails();
					btnDeltMtrl.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnTstDef.setCaption("Update");
					btnTstDef.setStyleName("savebt");
					editTstDefDetails();
					btnDeltMtrl.setEnabled(true);
				}
			}
		});
		btnTstDef = new GERPButton("Add", "add", this);
		btnTstDef.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (tstDefValidation()) {
					saveTstDefDetails();
				}
			}
		});
		btnDeltMtrl.setEnabled(false);
		btnDeltMtrl.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteTstCondtnDetails();
			}
		});
		btnDeltSpec.setEnabled(false);
		btnDeltSpec.addClickListener(new ClickListener() {
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
		tfTstRecrt = new GERPTextField("Requirement");
		tfTstSpec = new GERPTextField("Test Specification");
		tfTstCycle = new GERPTextField("Test Cycles");
		tfTstCycle.setWidth("110");
		cbTstSpecStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbTstSpecStatus.setValue(cbTstSpecStatus.getItemIds().iterator().next());
		tblTstSpec = new Table();
		tblTstSpec.setWidth("100%");
		tblTstSpec.setPageLength(8);
		tblTstSpec.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblTstSpec.isSelected(event.getItemId())) {
					tblTstSpec.setImmediate(true);
					btnTstSpec.setCaption("Add");
					btnTstSpec.setStyleName("savebt");
					resetTstSpec();
					btnDeltSpec.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnTstSpec.setCaption("Update");
					btnTstSpec.setStyleName("savebt");
					editTstSpecDetails();
					btnDeltSpec.setEnabled(true);
				}
			}
		});
		btnTstSpec = new GERPButton("Add", "add", this);
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
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		loadSrchQCTstDefRslt();
		loadSrchQCTstSpecRslt();
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		List<QCTestTypeDM> testTypeList = new ArrayList<QCTestTypeDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyid);
		testTypeList = serviceTestType.getQCTestTypeDetails(companyid, null, (String) tftstTyp.getValue(),
				(String) cbTstTypeStatus.getValue());
		recordCnt = testTypeList.size();
		beanTestType = new BeanItemContainer<QCTestTypeDM>(QCTestTypeDM.class);
		beanTestType.addAll(testTypeList);
		tblMstScrSrchRslt.setContainerDataSource(beanTestType);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "qcTstTypId", "tstType", "tstMethdlgy", "tstTypStatus",
				"lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Test Type", "Methodology", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("qcTstTypId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadSrchQCTstDefRslt() {
		logger.info("Test Defition Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Loading Test Defition Search...");
		recordTestdef = listTstDef.size();
		beanTestDef = new BeanItemContainer<QCTestDefinitionDM>(QCTestDefinitionDM.class);
		beanTestDef.addAll(listTstDef);
		tblTstdef.setSelectable(true);
		tblTstdef.setContainerDataSource(beanTestDef);
		tblTstdef.setVisibleColumns(new Object[] { "qcTstDefId", "materialType", "materialName", "tstDefStatus" });
		tblTstdef.setColumnHeaders(new String[] { "Ref.Id", "Material Type", "Material Name", "Status" });
		tblTstdef.setColumnAlignment("qcTstDefId", Align.RIGHT);
		tblTstdef.setFooterVisible(true);
		tblTstdef.setColumnFooter("tstDefStatus", "No.of Records : " + recordTestdef);
	}
	
	private void loadSrchQCTstSpecRslt() {
		logger.info("Test Defition Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Loading Test Defition Search...");
		recordTestSpec = listTstSpec.size();
		beanTestSpec = new BeanItemContainer<QCTestSpecificationDM>(QCTestSpecificationDM.class);
		beanTestSpec.addAll(listTstSpec);
		tblTstSpec.setSelectable(true);
		tblTstSpec.setContainerDataSource(beanTestSpec);
		tblTstSpec.setVisibleColumns(new Object[] { "qcTstSpecId", "tstRecrmt", "tstSpec", "tstCycles", "tstStatus" });
		tblTstSpec
				.setColumnHeaders(new String[] { "Ref.Id", "Test Recuirment", "Test Spec.", "Test Cycles", "Status" });
		tblTstSpec.setColumnAlignment("qcTstSpecId", Align.RIGHT);
		tblTstSpec.setFooterVisible(true);
		tblTstSpec.setColumnFooter("tstStatus", "No.of Records : " + recordTestSpec);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for QCTestType UI search layout
		hlSearchLayout.removeAllComponents();
		fltstTyp = new FormLayout();
		fltstMethdgly = new FormLayout();
		// Adding components into form layouts for QCTestType UI search layout
		fltstTyp.addComponent(tftstTyp);
		fltstMethdgly.addComponent(cbTstTypeStatus);
		// Adding form layouts into search layout for QCTestType UI search mode
		hlSearchLayout.addComponent(fltstTyp);
		hlSearchLayout.addComponent(fltstMethdgly);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleInputUserLayout() {
		hlSearchLayout.removeAllComponents();
		fltstTyp = new FormLayout();
		fltstMethdgly = new FormLayout();
		flTstDesc = new FormLayout();
		flTstSts = new FormLayout();
		//
		tftstTyp.setRequired(true);
		tfTstMethdgly.setRequired(true);
		cbMtrlType.setRequired(true);
		lsMtrlName.setRequired(true);
		tfTstRecrt.setRequired(true);
		tfTstSpec.setRequired(true);
		tfTstCycle.setRequired(true);
		//
		fltstTyp.addComponent(tftstTyp);
		fltstMethdgly.addComponent(tfTstMethdgly);
		flTstDesc.addComponent(taTstDesc);
		flTstSts.addComponent(cbTstTypeStatus);
		//
		hlTstTyp = new HorizontalLayout();
		hlTstTyp.addComponent(fltstTyp);
		hlTstTyp.addComponent(fltstMethdgly);
		hlTstTyp.addComponent(flTstDesc);
		hlTstTyp.addComponent(flTstSts);
		hlTstTyp.setMargin(true);
		hlTstTyp.setSpacing(true);
		flMtrlTyp = new FormLayout();
		flTstdefSts = new FormLayout();
		flTstdefbtn = new FormLayout();
		flMtrlTyp.addComponent(cbMtrlType);
		flMtrlTyp.addComponent(cbTstDefStatus);
		flTstdefSts.addComponent(lsMtrlName);
		//
		VerticalLayout vlButn = new VerticalLayout();
		vlButn.addComponent(btnTstDef);
		vlButn.addComponent(btnDeltMtrl);
		//
		hlTstDef = new HorizontalLayout();
		hlTstDef.addComponent(flMtrlTyp);
		hlTstDef.addComponent(flTstdefSts);
		hlTstDef.addComponent(vlButn);
		hlTstDef.setComponentAlignment(vlButn, Alignment.MIDDLE_CENTER);
		hlTstDef.setSpacing(true);
		hlTstDef.setMargin(true);
		//
		vlTstdef = new VerticalLayout();
		vlTstdef.setWidth("100%");
		vlTstdef.addComponent(hlTstDef);
		vlTstdef.addComponent(tblTstdef);
		//
		flTstRecr = new FormLayout();
		flTstCyc = new FormLayout();
		//
		flTstRecr.addComponent(tfTstRecrt);
		flTstRecr.addComponent(tfTstSpec);
		flTstCyc.addComponent(tfTstCycle);
		flTstCyc.addComponent(cbTstSpecStatus);
		//
		hlTstSpec = new HorizontalLayout();
		hlTstSpec.addComponent(flTstRecr);
		hlTstSpec.addComponent(flTstCyc);
		hlTstSpec.addComponent(btnTstSpec);
		hlTstSpec.setComponentAlignment(btnTstSpec, Alignment.BOTTOM_RIGHT);
		hlTstSpec.setSpacing(true);
		hlTstSpec.setMargin(true);
		//
		vlTstSpec = new VerticalLayout();
		vlTstSpec.addComponent(hlTstSpec);
		vlTstSpec.addComponent(tblTstSpec);
		vlTstSpec.setWidth("100%");
		//
		hlTstDefNTstSpec = new HorizontalLayout();
		hlTstDefNTstSpec.addComponent(vlTstdef);
		hlTstDefNTstSpec.addComponent(GERPPanelGenerator.createPanel(vlTstdef));
		hlTstDefNTstSpec.addComponent(vlTstSpec);
		hlTstDefNTstSpec.addComponent(GERPPanelGenerator.createPanel(vlTstSpec));
		hlTstDefNTstSpec.setSpacing(true);
		hlTstDefNTstSpec.setWidth("100%");
		//
		VerticalLayout vlAllComponent = new VerticalLayout();
		vlAllComponent.addComponent(hlTstTyp);
		vlAllComponent.addComponent(GERPPanelGenerator.createPanel(hlTstTyp));
		vlAllComponent.addComponent(hlTstDefNTstSpec);
		vlAllComponent.setSpacing(true);
		vlAllComponent.setWidth("100%");
		hlUserInputLayout.addComponent(vlAllComponent);
		hlUserInputLayout.setWidth("100%");
	}
	
	private void loadMaterialTypList() {
		List<MaterialTypeDM> getMtrlTypList = new ArrayList<MaterialTypeDM>();
		getMtrlTypList = serviceMaterialTyp.getMaterialTypeList(null, null, "Active", "F");
		BeanItemContainer<MaterialTypeDM> beanMtrlTyp = new BeanItemContainer<MaterialTypeDM>(MaterialTypeDM.class);
		beanMtrlTyp.addAll(getMtrlTypList);
		cbMtrlType.setContainerDataSource(beanMtrlTyp);
	}
	
	private void loadMaterialList() {
		List<MaterialDM> getMtrlTypList = new ArrayList<MaterialDM>();
		getMtrlTypList = serviceMaterial.getMaterialList(null, companyid, null, null, null, null,
				((MaterialTypeDM) cbMtrlType.getValue()).getMaterialTypeId(), null, "Active", "F");
		BeanItemContainer<MaterialDM> beanMtrl = new BeanItemContainer<MaterialDM>(MaterialDM.class);
		beanMtrl.addAll(getMtrlTypList);
		lsMtrlName.setContainerDataSource(beanMtrl);
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
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		tftstTyp.setValue("");
		tfTstMethdgly.setValue("");
		cbTstTypeStatus.setValue(cbTstTypeStatus.getItemIds().iterator().next());
		// reload the search using the defaults
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
		resetTstDefDetails();
		resetTstSpec();
		tftstTyp.setRequired(true);
		tfTstMethdgly.setRequired(true);
		cbMtrlType.setRequired(true);
		lsMtrlName.setRequired(true);
		tfTstRecrt.setRequired(true);
		tfTstSpec.setRequired(true);
		tfTstCycle.setRequired(true);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	private void resetTstSpec() {
		tfTstRecrt.setValue("");
		tfTstRecrt.setComponentError(null);
		tfTstSpec.setValue("");
		tfTstSpec.setComponentError(null);
		tfTstCycle.setValue("");
		tfTstCycle.setComponentError(null);
		cbTstSpecStatus.setValue(cbTstSpecStatus.getItemIds().iterator().next());
	}
	
	private void resetTstDefDetails() {
		cbMtrlType.setValue(null);
		cbMtrlType.setComponentError(null);
		lsMtrlName.removeAllItems();
		lsMtrlName.setComponentError(null);
		cbTstDefStatus.setValue(cbTstDefStatus.getItemIds().iterator().next());
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleInputUserLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		editQCTestTypeDetails();
		editTstDefDetails();
		editTstSpecDetails();
		loadSrchQCTstDefRslt();
		loadSrchQCTstSpecRslt();
	}
	
	private void editQCTestTypeDetails() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			QCTestTypeDM editTestType = beanTestType.getItem(tblMstScrSrchRslt.getValue()).getBean();
			testTypeId = editTestType.getQcTstTypIdLong().toString();
			if (editTestType.getTstType() != null) {
				tftstTyp.setValue(itselect.getItemProperty("tstType").getValue().toString());
			}
			if (editTestType.getTstMethdlgy() != null) {
				tfTstMethdgly.setValue((String) itselect.getItemProperty("tstMethdlgy").getValue());
			}
			if (editTestType.getTstTypeDesc() != null) {
				taTstDesc.setValue(editTestType.getTstTypeDesc());
			}
			cbTstTypeStatus.setValue(itselect.getItemProperty("tstTypStatus").getValue().toString());
			listTstDef = serviceTestDef.getQCTestDefDetails(null, companyid, Long.valueOf(testTypeId), null, null,
					"Active");
			listTstSpec = serviceTestSpec.getQCTestSpecDetails(null, companyid, Long.valueOf(testTypeId), "Active");
		}
	}
	
	private void editTstDefDetails() {
		Item itselect = tblTstdef.getItem(tblTstdef.getValue());
		if (itselect != null) {
			QCTestDefinitionDM editTstDefn = new QCTestDefinitionDM();
			editTstDefn = beanTestDef.getItem(tblTstdef.getValue()).getBean();
			Long mtrlTypId = editTstDefn.getMartlTypId();
			Collection<?> mtrlTypID = cbMtrlType.getItemIds();
			for (Iterator<?> iterator = mtrlTypID.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbMtrlType.getItem(itemId);
				// Get the actual bean and use the data
				MaterialTypeDM st = (MaterialTypeDM) item.getBean();
				if (mtrlTypId != null && mtrlTypId.equals(st.getMaterialTypeId())) {
					cbMtrlType.setValue(itemId);
				}
			}
			lsMtrlName.setValue(null);
			Long mtrlID = editTstDefn.getMartlId();
			Collection<?> mtrlId = lsMtrlName.getItemIds();
			for (Iterator<?> iterator = mtrlId.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) lsMtrlName.getItem(itemId);
				// Get the actual bean and use the data
				MaterialDM st = (MaterialDM) item.getBean();
				if (mtrlID != null && mtrlID.equals(st.getMaterialId())) {
					lsMtrlName.select(itemId);
				}
			}
			cbTstDefStatus.setValue((String) itselect.getItemProperty("tstDefStatus").getValue());
		}
	}
	
	private void editTstSpecDetails() {
		Item itselect = tblTstSpec.getItem(tblTstSpec.getValue());
		if (itselect != null) {
			QCTestSpecificationDM editTstSpec = new QCTestSpecificationDM();
			editTstSpec = beanTestSpec.getItem(tblTstSpec.getValue()).getBean();
			tfTstRecrt.setValue((String) itselect.getItemProperty("tstRecrmt").getValue());
			tfTstSpec.setValue((String) itselect.getItemProperty("tstSpec").getValue());
			tfTstCycle.setValue(Long.valueOf(editTstSpec.getTstCycles()).toString());
			cbTstSpecStatus.setValue((String) itselect.getItemProperty("tstStatus").getValue());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if (tftstTyp.getValue() == null || tftstTyp.getValue().trim().length() == 0) {
			tftstTyp.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TSTTYPE));
			errorFlag = true;
		} else {
			tftstTyp.setComponentError(null);
		}
		if (tfTstMethdgly.getValue() == null || tfTstMethdgly.getValue().trim().length() == 0) {
			tfTstMethdgly.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TSTMETHDGLY));
			errorFlag = true;
		} else {
			tfTstMethdgly.setComponentError(null);
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Saving Data... ");
			QCTestTypeDM qcTstTyp = new QCTestTypeDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				qcTstTyp = beanTestType.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			qcTstTyp.setCompanyId(companyid);
			qcTstTyp.setTstType(tftstTyp.getValue());
			qcTstTyp.setTstMethdlgy(tfTstMethdgly.getValue());
			qcTstTyp.setTstTypeDesc(taTstDesc.getValue());
			qcTstTyp.setTstTypStatus((String) cbTstTypeStatus.getValue());
			qcTstTyp.setLastUpdatedDt(DateUtils.getcurrentdate());
			qcTstTyp.setLastUpdatedBy(userName);
			serviceTestType.saveQCTestType(qcTstTyp);
			@SuppressWarnings("unchecked")
			Collection<QCTestDefinitionDM> itemIds = (Collection<QCTestDefinitionDM>) tblTstdef.getVisibleItemIds();
			for (QCTestDefinitionDM save : (Collection<QCTestDefinitionDM>) itemIds) {
				save.setQcTstTypId(Long.valueOf(qcTstTyp.getQcTstTypId().toString()));
				serviceTestDef.saveQCTestDef(save);
			}
			@SuppressWarnings("unchecked")
			Collection<QCTestSpecificationDM> itemIds1 = (Collection<QCTestSpecificationDM>) tblTstSpec
					.getVisibleItemIds();
			for (QCTestSpecificationDM save : (Collection<QCTestSpecificationDM>) itemIds1) {
				save.setQcTstTypId(Long.valueOf(qcTstTyp.getQcTstTypId().toString()));
				serviceTestSpec.saveQCTestSpec(save);
			}
			resetFields();
			resetTstDefDetails();
			resetTstSpec();
			loadSrchRslt();
			loadSrchQCTstDefRslt();
			loadSrchQCTstSpecRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveTstDefDetails() {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<MaterialDM> mtrlList = new ArrayList<MaterialDM>((Set) lsMtrlName.getValue());
		logger.info("Material List> split" + mtrlList);
		for (MaterialDM objMtrl : mtrlList) {
			QCTestDefinitionDM objTstDef = new QCTestDefinitionDM();
			if (tblTstdef.getValue() != null) {
				objTstDef = beanTestDef.getItem(tblTstdef.getValue()).getBean();
				listTstDef.remove(objTstDef);
			}
			objTstDef.setCompanyId(companyid);
			objTstDef.setMartlTypId(((MaterialTypeDM) cbMtrlType.getValue()).getMaterialTypeId());
			objTstDef.setMartlId(Long.valueOf(objMtrl.getMaterialId()));
			objTstDef.setMaterialType(((MaterialTypeDM) cbMtrlType.getValue()).getMaterialTypeName());
			objTstDef.setMaterialName(objMtrl.getMaterialName());
			objTstDef.setTstDefStatus((String) cbTstDefStatus.getValue());
			objTstDef.setLastUpdatedDt(DateUtils.getcurrentdate());
			objTstDef.setLastUpdatedBy(userName);
			if (cbMtrlType.getValue() != null && lsMtrlName.getValue() != null) {
				listTstDef.add(objTstDef);
			}
		}
		resetTstDefDetails();
		loadSrchQCTstDefRslt();
		btnTstDef.setCaption("Add");
	}
	
	private boolean tstDefValidation() {
		boolean isValid = true;
		if (cbMtrlType.getValue() == null) {
			cbMtrlType.setComponentError(new UserError(GERPErrorCodes.NULL_QC_MTRLTYPE));
			isValid = false;
		} else {
			cbMtrlType.setComponentError(null);
		}
		if (lsMtrlName.getValue().toString() == "[]") {
			lsMtrlName.setComponentError(new UserError(GERPErrorCodes.NULL_QC_MTRLNAME));
			isValid = false;
		} else {
			lsMtrlName.setComponentError(null);
		}
		return isValid;
	}
	
	private void saveTstSpecDetails() {
		QCTestSpecificationDM qcTstSpec = new QCTestSpecificationDM();
		if (tblTstSpec.getValue() != null) {
			qcTstSpec = beanTestSpec.getItem(tblTstSpec.getValue()).getBean();
			listTstSpec.remove(qcTstSpec);
		}
		qcTstSpec.setCompanyId(companyid);
		qcTstSpec.setTstRecrmt(tfTstRecrt.getValue());
		qcTstSpec.setTstSpec(tfTstSpec.getValue());
		try {
			qcTstSpec.setTstCycles(Long.valueOf(tfTstCycle.getValue()));
			qcTstSpec.setTstStatus((String) cbTstSpecStatus.getValue());
			qcTstSpec.setLastUpdatedDt(DateUtils.getcurrentdate());
			qcTstSpec.setLastUpdatedBy(userName);
			if (tfTstRecrt.getValue() != "" && tfTstSpec.getValue() != "" && tfTstCycle.getValue() != "") {
				listTstSpec.add(qcTstSpec);
				resetTstSpec();
				loadSrchQCTstSpecRslt();
			}
			btnTstSpec.setCaption("Add");
		}
		catch (NumberFormatException e) {
			tfTstCycle.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TEST_CYC_NO));
		}
	}
	
	private boolean tstSpecValidation() {
		boolean isValid = true;
		if (tfTstRecrt.getValue() == "" || tfTstRecrt.getValue().trim().length() == 0) {
			tfTstRecrt.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TEST_RECRMNT));
			isValid = true;
		} else {
			tfTstRecrt.setComponentError(null);
		}
		if (tfTstSpec.getValue() == "" || tfTstSpec.getValue().trim().length() == 0) {
			tfTstSpec.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TEST_SPEC));
			isValid = true;
		} else {
			tfTstSpec.setComponentError(null);
		}
		if (tfTstCycle.getValue() == "" || tfTstCycle.getValue().trim().length() == 0) {
			tfTstCycle.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TEST_CYC));
			isValid = true;
		} else {
			tfTstCycle.setComponentError(null);
		}
		return isValid;
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Getting audit record for QCTestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_MFG_QC_TEST_TYPE);
		UI.getCurrent().getSession().setAttribute("audittablepk", testTypeId);
	}
	
	@Override
	protected void cancelDetails() {
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		resetSearchDetails();
		resetTstDefDetails();
		resetTstSpec();
		hlCmdBtnLayout.setVisible(true);
		tblTstdef.removeAllItems();
		tblTstSpec.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		tftstTyp.setRequired(false);
		tfTstMethdgly.setRequired(false);
		cbMtrlType.setRequired(false);
		lsMtrlName.setRequired(false);
		tfTstRecrt.setRequired(false);
		tfTstSpec.setRequired(false);
		tfTstCycle.setRequired(false);
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		tftstTyp.setValue("");
		tftstTyp.setComponentError(null);
		tfTstMethdgly.setValue("");
		tfTstMethdgly.setComponentError(null);
		taTstDesc.setValue("");
		cbTstTypeStatus.setValue(cbTstTypeStatus.getItemIds().iterator().next());
		listTstDef = new ArrayList<QCTestDefinitionDM>();
		listTstSpec = new ArrayList<QCTestSpecificationDM>();
		tblTstdef.removeAllItems();
		tblTstSpec.removeAllItems();
	}
	
	private void deleteTstCondtnDetails() {
		QCTestDefinitionDM qCTestDefinitionDM = new QCTestDefinitionDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			qCTestDefinitionDM = beanTestDef.getItem(tblMstScrSrchRslt.getValue()).getBean();
			listTstDef.remove(qCTestDefinitionDM);
			resetTstDefDetails();
			tblMstScrSrchRslt.setValue("");
			loadSrchQCTstDefRslt();
			btnDeltMtrl.setEnabled(false);
		}
	}
	
	private void deleteTstSpecDetails() {
		QCTestSpecificationDM qCTestSpecificationDM = new QCTestSpecificationDM();
		if (tblTstSpec.getValue() != null) {
			qCTestSpecificationDM = beanTestSpec.getItem(tblTstSpec.getValue()).getBean();
			listTstSpec.remove(qCTestSpecificationDM);
			resetTstSpec();
			tblTstSpec.setValue("");
			loadSrchQCTstSpecRslt();
			btnDeltSpec.setEnabled(false);
		}
	}
}
