/**
 * File Name	:	QCTest.java
 * Description	:	This Screen Purpose for Modify the QCTest Details.
 * 					Add the QCTest details process should be directly added in DB.
 * Author		:	Nandhakumar.S
 * 
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1          18-Sep-2014		  Nandhakumar.S		Initial version 
 */
package com.gnts.mfg.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.mst.ProductDrawingDM;
import com.gnts.mfg.domain.mst.QCTestSpecificationDM;
import com.gnts.mfg.domain.mst.QCTestTypeDM;
import com.gnts.mfg.domain.txn.QcTestDtlDM;
import com.gnts.mfg.domain.txn.QcTestHdrDM;
import com.gnts.mfg.mst.QCTestType;
import com.gnts.mfg.service.mst.ProductDrawingService;
import com.gnts.mfg.service.mst.QCTestSpecService;
import com.gnts.mfg.service.mst.QCTestTypeService;
import com.gnts.mfg.service.txn.QCTestDtlService;
import com.gnts.mfg.service.txn.QcTestHdrService;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.PoReceiptHdrDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.PoReceiptHdrService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class QCTest extends BaseUI {
	/**
	 
	 */
	private static final long serialVersionUID = 1L;
	private QcTestHdrService serviceQcTstHdr = (QcTestHdrService) SpringContextHelper.getBean("qcTestHdr");
	private QCTestDtlService serviceQcTstDtl = (QCTestDtlService) SpringContextHelper.getBean("qcTestDtl");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private ProductDrawingService serviceProductDrawing = (ProductDrawingService) SpringContextHelper
			.getBean("productDrawings");
	private QCTestTypeService serviceQCTestType = (QCTestTypeService) SpringContextHelper.getBean("qcTestType");
	private QCTestSpecService serviceQCTestSpec = (QCTestSpecService) SpringContextHelper.getBean("qcTestSpec");
	private SlnoGenService serviceSLNo = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private PoReceiptHdrService servicePoReceipt = (PoReceiptHdrService) SpringContextHelper.getBean("poreceipthdr");
	List<QcTestDtlDM> listQcTstDtl = null;
	BeanItemContainer<QcTestHdrDM> beanQcTstHdr = null;
	BeanItemContainer<QcTestDtlDM> beanQcTstDtl = null;
	private String userName;
	private Long companyid;
	private static Logger logger = Logger.getLogger(QCTestType.class);
	private FormLayout flOdrHdrColumn1, flOdrHdrColumn2, flOdrHdrColumn3, flOdrHdrColumn4;
	private TextField tfInSpecNo, tfPrdSlNo, tfSamplTst, tfQtyFailed, tfQcReslt;
	private ComboBox cbBranch, cbReceipt, cbMaterial, cbTsttyp, cbTstBy, cbProduct, cbProductDrg, cbQcHdrStatus;
	private PopupDateField pdInspectionDt;
	private CheckBox chVisl, chFitmnt;
	private TextArea taTstRemarks, taObserv;
	//
	private Button btnSaveTst;
	private Table tblQcDtlTbl;
	private ComboBox cbTstSpec, cbQcTstDtlStatus;
	private TextField tfTstSpecReslt;
	private TextArea taQcRemarks;
	private GERPAddEditHLayout hlSearchLayout;
	private FormLayout flOdrDtlColumn1, flOdrDtlColumn2, flOdrDtlColumn3;
	private HorizontalLayout hlQCHdr, hlOdrDtl;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlTstDtlNtbl, vlQcHdrNDtl;
	private int recordCnt;
	private Long EmployeeId;
	private Long moduleId;
	private Long branchID;;
	private String testTypeId;
	private Button btnDtete = new GERPButton("Delete", "delete", this);
	private Comments comment;
	private VerticalLayout vlTableForm = new VerticalLayout();
	private Long commentby;
	
	public QCTest() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		EmployeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Inside QCTest() constructor");
		// Loading the TestType UI
		buildView();
	}
	
	private void buildView() {
		// Initialization for work order Details user input components
		btnSaveTst = new Button("Add");
		btnSaveTst.setStyleName("savebt");
		btnSaveTst.setVisible(true);
		btnSaveTst.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (qcTstDtlValidation()) {
					saveQCTstDetails();
				}
			}
		});
		btnDtete.setEnabled(false);
		btnDtete.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteTstDefDetails();
			}
		});
		tblQcDtlTbl = new GERPTable();
		tblQcDtlTbl.setPageLength(5);
		tblQcDtlTbl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblQcDtlTbl.isSelected(event.getItemId())) {
					tblQcDtlTbl.setImmediate(true);
					btnSaveTst.setCaption("Add");
					resetQcTestDtl();
					btnDtete.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnSaveTst.setCaption("Update");
					editQCDetails();
					btnDtete.setEnabled(true);
				}
			}
		});
		tfInSpecNo = new TextField("Inspection No.");
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
				tfInSpecNo.setReadOnly(false);
			}
		});
		tfPrdSlNo = new TextField("Product SL.No");
		tfSamplTst = new TextField("Samples Tested");
		tfQtyFailed = new TextField("Failed Qty");
		tfQcReslt = new TextField("QC Result");
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("154");
		loadBranchList();
		cbReceipt = new GERPComboBox("LotNo & Date");
		cbReceipt.setItemCaptionPropertyId("lotNdate");
		cbReceipt.setWidth("154");
		loadReceiptlist();
		cbMaterial = new GERPComboBox("Material Name");
		cbMaterial.setItemCaptionPropertyId("materialName");
		cbMaterial.setWidth("154");
		loadMaterialList();
		cbTsttyp = new GERPComboBox("Test Type");
		cbTsttyp.setItemCaptionPropertyId("tstType");
		cbTsttyp.setWidth("154");
		loadTestTypeList();
		cbTstBy = new GERPComboBox("Test By");
		cbTstBy.setItemCaptionPropertyId("firstlastname");
		cbTstBy.setWidth("154");
		loadTestByList();
		cbProduct = new GERPComboBox("Product");
		cbProduct.setItemCaptionPropertyId("prodname");
		cbProduct.setWidth("154");
		loadProductList();
		cbProductDrg = new GERPComboBox("Product Drg.Code");
		cbProductDrg.setItemCaptionPropertyId("drawingCode");
		cbProductDrg.setWidth("154");
		loadProductDrgCodeList();
		cbQcHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbQcHdrStatus.setValue(cbQcHdrStatus.getItemIds().iterator().next());
		chVisl = new CheckBox("Visual OK");
		chFitmnt = new CheckBox("Fitment OK");
		taQcRemarks = new GERPTextArea("Remarks");
		taQcRemarks.setWidth("190");
		taQcRemarks.setHeight("50");
		taObserv = new GERPTextArea("Observation");
		taObserv.setWidth("190");
		taObserv.setHeight("50");
		//
		cbTstSpec = new GERPComboBox("Specification");
		cbTstSpec.setItemCaptionPropertyId("tstSpec");
		cbTstSpec.setWidth("145");
		loadTestSpecList();
		cbQcTstDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbQcTstDtlStatus.setValue(cbQcTstDtlStatus.getItemIds().iterator().next());
		cbBranch.setWidth("140");
		tfTstSpecReslt = new TextField("Result");
		tfTstSpecReslt.setWidth("140");
		taTstRemarks = new GERPTextArea("Remarks");
		taTstRemarks.setWidth("150");
		taTstRemarks.setHeight("75");
		//
		pdInspectionDt = new GERPPopupDateField("Inspection Date");
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		resetQcTestDtl();
		assembleSearchLayout();
		loadSrchRslt();
		loadSrchQCDtlList();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for QCTest UI search layout
		tfInSpecNo.setReadOnly(false);
		hlSearchLayout.removeAllComponents();
		flOdrHdrColumn1 = new GERPFormLayout();
		flOdrHdrColumn2 = new GERPFormLayout();
		flOdrHdrColumn3 = new GERPFormLayout();
		flOdrHdrColumn4 = new GERPFormLayout();
		// Adding components into form layouts for QCTest UI search layout
		flOdrHdrColumn1.addComponent(tfInSpecNo);
		flOdrHdrColumn2.addComponent(cbBranch);
		flOdrHdrColumn3.addComponent(cbProduct);
		flOdrHdrColumn4.addComponent(cbQcHdrStatus);
		// Adding form layouts into search layout for QCTest UI search mode
		hlSearchLayout.addComponent(flOdrHdrColumn1);
		hlSearchLayout.addComponent(flOdrHdrColumn2);
		hlSearchLayout.addComponent(flOdrHdrColumn3);
		hlSearchLayout.addComponent(flOdrHdrColumn4);
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
		flOdrHdrColumn1 = new FormLayout();
		flOdrHdrColumn2 = new FormLayout();
		flOdrHdrColumn3 = new FormLayout();
		flOdrHdrColumn4 = new FormLayout();
		cbBranch.setRequired(true);
		cbMaterial.setRequired(true);
		cbProduct.setRequired(true);
		cbProductDrg.setRequired(true);
		cbReceipt.setRequired(true);
		cbTstBy.setRequired(true);
		cbTsttyp.setRequired(true);
		pdInspectionDt.setRequired(true);
		cbTstSpec.setRequired(true);
		tfSamplTst.setRequired(true);
		tfQtyFailed.setRequired(true);
		tfQcReslt.setRequired(true);
		tfPrdSlNo.setRequired(true);
		tfTstSpecReslt.setRequired(true);
		// adding components into first column in form layout1
		flOdrHdrColumn1.addComponent(tfInSpecNo);
		flOdrHdrColumn1.addComponent(pdInspectionDt);
		flOdrHdrColumn1.addComponent(cbBranch);
		flOdrHdrColumn1.addComponent(cbReceipt);
		flOdrHdrColumn1.addComponent(cbMaterial);
		// adding components into second column in form layout2
		flOdrHdrColumn2.addComponent(cbTstBy);
		flOdrHdrColumn2.addComponent(cbTsttyp);
		flOdrHdrColumn2.addComponent(cbProduct);
		flOdrHdrColumn2.addComponent(cbProductDrg);
		flOdrHdrColumn2.addComponent(tfPrdSlNo);
		//
		flOdrHdrColumn3.addComponent(tfSamplTst);
		flOdrHdrColumn3.addComponent(tfQtyFailed);
		flOdrHdrColumn3.addComponent(chVisl);
		flOdrHdrColumn3.addComponent(chFitmnt);
		flOdrHdrColumn3.addComponent(tfQcReslt);
		// adding components into third column in form layout
		//
		flOdrHdrColumn4.addComponent(taObserv);
		flOdrHdrColumn4.addComponent(taQcRemarks);
		flOdrHdrColumn4.addComponent(cbQcHdrStatus);
		// adding components into fourth column in form layout3
		flOdrDtlColumn1 = new FormLayout();
		flOdrDtlColumn2 = new FormLayout();
		flOdrDtlColumn3 = new FormLayout();
		// adding components into first column in form layout1
		// Initialization for work order Details user input components
		flOdrDtlColumn1.addComponent(cbTstSpec);
		flOdrDtlColumn1.addComponent(tfTstSpecReslt);
		flOdrDtlColumn1.addComponent(cbQcTstDtlStatus);
		flOdrDtlColumn2.addComponent(taTstRemarks);
		flOdrDtlColumn3.addComponent(btnSaveTst);
		flOdrDtlColumn3.addComponent(btnDtete);
		// adding form layouts into user input layouts
		hlQCHdr = new HorizontalLayout();
		hlQCHdr.addComponent(flOdrHdrColumn1);
		hlQCHdr.addComponent(flOdrHdrColumn2);
		hlQCHdr.addComponent(flOdrHdrColumn3);
		hlQCHdr.addComponent(flOdrHdrColumn4);
		hlQCHdr.setSpacing(true);
		hlQCHdr.setMargin(true);
		// adding form layouts into user input layouts
		hlOdrDtl = new HorizontalLayout();
		hlOdrDtl.addComponent(flOdrDtlColumn1);
		hlOdrDtl.addComponent(flOdrDtlColumn2);
		hlOdrDtl.addComponent(flOdrDtlColumn3);
		hlOdrDtl.setComponentAlignment(flOdrDtlColumn3, Alignment.MIDDLE_LEFT);
		hlOdrDtl.setSpacing(true);
		hlOdrDtl.setMargin(true);
		vlTstDtlNtbl = new VerticalLayout();
		vlTstDtlNtbl.addComponent(hlOdrDtl);
		vlTstDtlNtbl.addComponent(tblQcDtlTbl);
		vlTstDtlNtbl.setWidth("600");
		VerticalLayout vl = new VerticalLayout();
		vl.addComponent(vlTstDtlNtbl);
		vl.addComponent(GERPPanelGenerator.createPanel(vlTstDtlNtbl));
		HorizontalLayout hlDtlNCmt = new HorizontalLayout();
		hlDtlNCmt.addComponent(vl);
		hlDtlNCmt.addComponent(vlTableForm);
		hlDtlNCmt.addComponent(GERPPanelGenerator.createPanel(vlTableForm));
		hlDtlNCmt.setSpacing(true);
		vlQcHdrNDtl = new VerticalLayout();
		Label lblQCDtl = new Label("Test Details");
		lblQCDtl.setStyleName("h4");
		vlQcHdrNDtl.addComponent(GERPPanelGenerator.createPanel(hlQCHdr));
		vlQcHdrNDtl.addComponent(hlDtlNCmt);
		vlQcHdrNDtl.setSpacing(true);
		hlUserInputLayout.addComponent(vlQcHdrNDtl);
		// hlUserInputLayout.setWidth("100%");
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		List<QcTestHdrDM> listQcTstHdr = new ArrayList<QcTestHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyid);
		listQcTstHdr = serviceQcTstHdr.getQcTestHdrDetails(null, companyid, (Long) cbBranch.getValue(),
				(Long) cbProduct.getValue(), (String) tfInSpecNo.getValue(), (String) cbQcHdrStatus.getValue());
		recordCnt = listQcTstHdr.size();
		beanQcTstHdr = new BeanItemContainer<QcTestHdrDM>(QcTestHdrDM.class);
		beanQcTstHdr.addAll(listQcTstHdr);
		tblMstScrSrchRslt.setContainerDataSource(beanQcTstHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "qctestid", "inspectionno", "inspectiondate", "branchName",
				"prodName", "qcresult", "qcteststatus", "lastupdateddate", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Inspection No.", "Inspection Date", "Branch Name",
				"Product Name", "QC Result", "Status", "Last Updated Dt.", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("workOrdrId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadSrchQCDtlList() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyid);
		tblQcDtlTbl.removeAllItems();
		recordCnt = listQcTstDtl.size();
		beanQcTstDtl = new BeanItemContainer<QcTestDtlDM>(QcTestDtlDM.class);
		beanQcTstDtl.addAll(listQcTstDtl);
		tblQcDtlTbl.setContainerDataSource(beanQcTstDtl);
		tblQcDtlTbl.setVisibleColumns(new Object[] { "qcTestDtlID", "tstSpec", "testSpecRslt", "qcTestStatus",
				"lastUpdatedDT", "lastUpdatedBy" });
		tblQcDtlTbl.setColumnHeaders(new String[] { "Ref.Id", "Specification", "Result", "Status", "Last Updated Dt.",
				"Last Updated By" });
		tblQcDtlTbl.setColumnAlignment("qcTestDtlId", Align.RIGHT);
		tblQcDtlTbl.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadBranchList() {
		{
			List<BranchDM> getBranchList = new ArrayList<BranchDM>();
			getBranchList = serviceBranch.getBranchList(null, null, null, "Active", null, "F");
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(getBranchList);
			cbBranch.setContainerDataSource(beanbranch);
		}
	}
	
	private void loadProductDrgCodeList() {
		List<ProductDrawingDM> getProdDrg = new ArrayList<ProductDrawingDM>();
		getProdDrg = serviceProductDrawing.getProductDrgDetails(companyid, null, null, null, "Active");
		BeanContainer<Long, ProductDrawingDM> beanProdDrg = new BeanContainer<Long, ProductDrawingDM>(
				ProductDrawingDM.class);
		for (ProductDrawingDM po : getProdDrg) {
			if (po.getDrawingCode() != null) {
				beanProdDrg.setBeanIdProperty("productDrgId");
				beanProdDrg.addAll(getProdDrg);
			}
		}
		cbProductDrg.setContainerDataSource(beanProdDrg);
	}
	
	private void loadProductList() {
		List<ProductDM> getProdList = new ArrayList<ProductDM>();
		getProdList = serviceProduct.getProductList(companyid, null, null, null, "Active", null, null, "F");
		BeanContainer<Long, ProductDM> beanProd = new BeanContainer<Long, ProductDM>(ProductDM.class);
		beanProd.setBeanIdProperty("prodid");
		beanProd.addAll(getProdList);
		cbProduct.setContainerDataSource(beanProd);
	}
	
	private void loadReceiptlist() {
		List<PoReceiptHdrDM> getProdList = new ArrayList<PoReceiptHdrDM>();
		getProdList = servicePoReceipt.getPoReceiptsHdrList(companyid, null, null, null, null, "Active", "F");
		BeanContainer<Long, PoReceiptHdrDM> beanReceipt = new BeanContainer<Long, PoReceiptHdrDM>(PoReceiptHdrDM.class);
		beanReceipt.setBeanIdProperty("receiptId");
		beanReceipt.addAll(getProdList);
		cbReceipt.setContainerDataSource(beanReceipt);
	}
	
	private void loadTestTypeList() {
		List<QCTestTypeDM> getTstTypList = new ArrayList<QCTestTypeDM>();
		getTstTypList = serviceQCTestType.getQCTestTypeDetails(companyid, null, null, "Active");
		BeanContainer<Long, QCTestTypeDM> beanTestTyp = new BeanContainer<Long, QCTestTypeDM>(QCTestTypeDM.class);
		beanTestTyp.setBeanIdProperty("qcTstTypId");
		beanTestTyp.addAll(getTstTypList);
		cbTsttyp.setContainerDataSource(beanTestTyp);
	}
	
	private void loadTestByList() {
		List<EmployeeDM> getTstBy = new ArrayList<EmployeeDM>();
		getTstBy = serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null, null, null, "F");
		BeanContainer<Long, EmployeeDM> beanbranch = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanbranch.setBeanIdProperty("employeeid");
		beanbranch.addAll(getTstBy);
		cbTstBy.setContainerDataSource(beanbranch);
	}
	
	private void loadMaterialList() {
		List<MaterialDM> getMaterialList = new ArrayList<MaterialDM>();
		getMaterialList = serviceMaterial
				.getMaterialList(null, null, null, null, null, null, null, null, "Active", "F");
		BeanContainer<Long, MaterialDM> beanMaterial = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
		beanMaterial.setBeanIdProperty("materialId");
		beanMaterial.addAll(getMaterialList);
		cbMaterial.setContainerDataSource(beanMaterial);
	}
	
	private void loadTestSpecList() {
		List<QCTestSpecificationDM> getTstSpecList = new ArrayList<QCTestSpecificationDM>();
		getTstSpecList = serviceQCTestSpec.getQCTestSpecDetails(null, companyid, null, "Active");
		BeanItemContainer<QCTestSpecificationDM> beanTstSpec = new BeanItemContainer<QCTestSpecificationDM>(
				QCTestSpecificationDM.class);
		beanTstSpec.addAll(getTstSpecList);
		cbTstSpec.setContainerDataSource(beanTstSpec);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbBranch.setComponentError(null);
		cbBranch.setValue(null);
		cbMaterial.setComponentError(null);
		cbMaterial.setValue(null);
		cbProduct.setComponentError(null);
		cbProduct.setValue(null);
		cbProductDrg.setComponentError(null);
		cbProductDrg.setValue(null);
		chFitmnt.setValue(false);
		cbTstBy.setComponentError(null);
		cbTstBy.setValue(null);
		cbTsttyp.setComponentError(null);
		cbTsttyp.setValue(null);
		pdInspectionDt.setComponentError(null);
		pdInspectionDt.setValue(null);
		chVisl.setValue(false);
		cbReceipt.setComponentError(null);
		cbReceipt.setValue(null);
		tfPrdSlNo.setComponentError(null);
		tfPrdSlNo.setValue("");
		tfQcReslt.setComponentError(null);
		tfQcReslt.setValue("");
		tfQtyFailed.setComponentError(null);
		tfQtyFailed.setValue("");
		taObserv.setValue("");
		tfInSpecNo.setReadOnly(false);
		tfInSpecNo.setValue("");
		tfInSpecNo.setReadOnly(true);
		taQcRemarks.setValue("");
		tfSamplTst.setComponentError(null);
		tfSamplTst.setValue("");
		cbQcHdrStatus.setValue(cbQcHdrStatus.getItemIds().iterator().next());
		tblQcDtlTbl.removeAllItems();
		listQcTstDtl = new ArrayList<QcTestDtlDM>();
	}
	
	private void editQCHdrDetails() {
		try {
			Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			if (itselect != null) {
				QcTestHdrDM editTestHdr = beanQcTstHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
				testTypeId = editTestHdr.getQctestid().toString();
				cbBranch.setValue(editTestHdr.getBranchid());
				cbMaterial.setValue(editTestHdr.getMaterialid());
				pdInspectionDt.setValue(editTestHdr.getInspectiondatedt());
				cbTstBy.setValue(editTestHdr.getTestedby());
				tfInSpecNo.setReadOnly(false);
				tfInSpecNo.setValue((String) itselect.getItemProperty("inspectionno").getValue());
				tfInSpecNo.setReadOnly(true);
				cbReceipt.setValue(editTestHdr.getReceiptid());
				cbProduct.setValue(editTestHdr.getProductid());
				cbProductDrg.setValue(editTestHdr.getProddrawgid());
				cbTsttyp.setValue(editTestHdr.getQctesttypeid());
				if (editTestHdr.getProdserlno() != null) {
					tfPrdSlNo.setValue((String) itselect.getItemProperty("prodserlno").getValue());
				}
				if (editTestHdr.getSamplestested() != null) {
					tfSamplTst.setValue(Long.valueOf(editTestHdr.getSamplestested()).toString());
				}
				if (editTestHdr.getQtyfailed() != null) {
					tfQtyFailed.setValue(Long.valueOf(editTestHdr.getQtyfailed()).toString());
				}
				if (editTestHdr.getQcresult() != null) {
					tfQcReslt.setValue((String) itselect.getItemProperty("qcresult").getValue());
				}
				if (editTestHdr.getFitmentokyn().equals("Yes")) {
					chFitmnt.setValue(true);
				} else {
					chFitmnt.setValue(false);
				}
				if (editTestHdr.getVisualokyn().equals("Yes")) {
					chVisl.setValue(true);
				} else {
					chVisl.setValue(false);
				}
				if (editTestHdr.getOthertstremark() != null) {
					taQcRemarks.setValue((String) itselect.getItemProperty("othertstremark").getValue());
				}
				if (editTestHdr.getQcobervations() != null) {
					taObserv.setValue((String) itselect.getItemProperty("qcobervations").getValue());
				}
				cbQcHdrStatus.setValue((String) itselect.getItemProperty("qcteststatus").getValue());
				listQcTstDtl = serviceQcTstDtl
						.getQcTestDtlDetails(null, Long.valueOf(testTypeId), null, null, "Active");
				comment = new Comments(vlTableForm, companyid, null, null, null, null, commentby);
				comment.loadsrch(true, null, companyid, null, null, null, Long.valueOf(testTypeId));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void editQCDetails() {
		Item itselect = tblQcDtlTbl.getItem(tblQcDtlTbl.getValue());
		if (itselect != null) {
			QcTestDtlDM editQCTstDtl = new QcTestDtlDM();
			editQCTstDtl = beanQcTstDtl.getItem(tblQcDtlTbl.getValue()).getBean();
			Long tstSpecId = editQCTstDtl.getQcTestSpecId();
			Collection<?> tstSpecID = cbTstSpec.getItemIds();
			for (Iterator<?> iterator = tstSpecID.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbTstSpec.getItem(itemId);
				// Get the actual bean and use the data
				QCTestSpecificationDM st = (QCTestSpecificationDM) item.getBean();
				if (tstSpecId != null && tstSpecId.equals(st.getQcTstSpecId())) {
					cbTstSpec.setValue(itemId);
				}
			}
			tfTstSpecReslt.setValue((String) itselect.getItemProperty("testSpecRslt").getValue());
			if (editQCTstDtl.getQcRemarks() != null) {
				taTstRemarks.setValue((String) itselect.getItemProperty("qcRemarks").getValue());
			}
			cbQcTstDtlStatus.setValue((String) itselect.getItemProperty("qcTestStatus").getValue());
		}
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
		resetQcTestDtl();
		List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyid, branchID, moduleId, "MF_QCINSNO");
		tfInSpecNo.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfInSpecNo.setReadOnly(true);
			} else {
				tfInSpecNo.setReadOnly(false);
			}
			hlCmdBtnLayout.setVisible(false);
			tblMstScrSrchRslt.setVisible(false);
			tblQcDtlTbl.setVisible(true);
			comment = new Comments(vlTableForm, companyid, null, null, null, null, commentby);
		}
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Getting audit record for QATest. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_WORKORDER_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", "");
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		cbBranch.setRequired(false);
		cbMaterial.setRequired(false);
		cbProduct.setRequired(false);
		cbProductDrg.setRequired(false);
		cbReceipt.setRequired(false);
		cbTstBy.setRequired(false);
		cbTsttyp.setRequired(false);
		pdInspectionDt.setRequired(false);
		cbTstSpec.setRequired(false);
		tfQcReslt.setRequired(false);
		tfTstSpecReslt.setRequired(false);
		tfSamplTst.setRequired(false);
		tfQtyFailed.setRequired(false);
		tfQcReslt.setRequired(false);
		tfPrdSlNo.setRequired(false);
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		hlCmdBtnLayout.setVisible(true);
		tblQcDtlTbl.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		resetQcTestDtl();
		assembleSearchLayout();
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyid, branchID, moduleId, "MF_QCINSNO");
		tfInSpecNo.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfInSpecNo.setReadOnly(true);
			}
		}
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblQcDtlTbl.setVisible(true);
		if (tfInSpecNo.getValue() == null || tfInSpecNo.getValue().trim().length() == 0) {
			tfInSpecNo.setReadOnly(false);
		}
		resetQcTestDtl();
		resetFields();
		editQCHdrDetails();
		editQCDetails();
		loadSrchQCDtlList();
	}
	
	private void resetQcTestDtl() {
		cbTstSpec.setComponentError(null);
		cbTstSpec.setValue(null);
		tfTstSpecReslt.setComponentError(null);
		tfTstSpecReslt.setValue("");
		taTstRemarks.setValue("");
		cbQcTstDtlStatus.setValue(cbQcTstDtlStatus.getItemIds().iterator().next());
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_QC_BRANCH));
			errorFlag = true;
		} else {
			cbBranch.setComponentError(null);
		}
		if (cbMaterial.getValue() == null) {
			cbMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_QC_MATERIAL));
			errorFlag = true;
		} else {
			cbMaterial.setComponentError(null);
		}
		if (pdInspectionDt.getValue() == null) {
			pdInspectionDt.setComponentError(new UserError(GERPErrorCodes.NULL_INSP_DATE));
			errorFlag = true;
		} else {
			pdInspectionDt.setComponentError(null);
		}
		if (cbTstBy.getValue() == null) {
			cbTstBy.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TSTBY));
			errorFlag = true;
		} else {
			cbTstBy.setComponentError(null);
		}
		if (cbTsttyp.getValue() == null) {
			cbTsttyp.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TSTTYP));
			errorFlag = true;
		} else {
			cbTsttyp.setComponentError(null);
		}
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_QC_PRODUCT));
			errorFlag = true;
		} else {
			cbProduct.setComponentError(null);
		}
		if (cbProductDrg.getValue() == null) {
			cbProductDrg.setComponentError(new UserError(GERPErrorCodes.NULL_QC_PRDDRG));
			errorFlag = true;
		} else {
			cbProductDrg.setComponentError(null);
		}
		if (tfPrdSlNo.getValue() == "" || tfPrdSlNo.getValue() == null || tfPrdSlNo.getValue().trim().length() == 0) {
			tfPrdSlNo.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TSTPRDSLNO));
			errorFlag = true;
		} else {
			tfPrdSlNo.setComponentError(null);
		}
		if (tfQcReslt.getValue() == "" || tfQcReslt.getValue() == null || tfQcReslt.getValue().trim().length() == 0) {
			tfQcReslt.setComponentError(new UserError(GERPErrorCodes.NULL_QC_RESULT));
			errorFlag = true;
		} else {
			tfQcReslt.setComponentError(null);
		}
		if (cbReceipt.getValue() == null) {
			cbReceipt.setComponentError(new UserError(GERPErrorCodes.NULL_QC_RECEIPT));
			errorFlag = true;
		} else {
			cbReceipt.setComponentError(null);
		}
		try {
			Long.valueOf(tfSamplTst.getValue());
			tfSamplTst.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfSamplTst.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TSTSMPL));
			errorFlag = true;
		}
		try {
			Long.valueOf(tfQtyFailed.getValue());
			tfQtyFailed.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfQtyFailed.setComponentError(new UserError(GERPErrorCodes.NULL_QC_FAILED));
			errorFlag = true;
		}
		if (Long.valueOf(tfSamplTst.getValue()) < Long.valueOf(tfQtyFailed.getValue())) {
			tfQtyFailed.setComponentError(new UserError(GERPErrorCodes.QC_INCORT_FALDQTY));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Saving Data... ");
		try {
			QcTestHdrDM qcTestHdr = new QcTestHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				qcTestHdr = beanQcTstHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			} else {
				List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyid, +branchID, moduleId, "MF_QCINSNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						qcTestHdr.setInspectionno(slnoObj.getKeyDesc());
					}
				}
			}
			qcTestHdr.setCompanyid(companyid);
			qcTestHdr.setBranchid((Long) cbBranch.getValue());
			qcTestHdr.setInspectiondate(pdInspectionDt.getValue());
			qcTestHdr.setMaterialid((Long) cbMaterial.getValue());
			qcTestHdr.setTestedby((Long) cbTstBy.getValue());
			qcTestHdr.setQctesttypeid((Long) cbTsttyp.getValue());
			qcTestHdr.setProductid(Long.valueOf(cbProduct.getValue().toString()));
			qcTestHdr.setProddrawgid((Long) cbProductDrg.getValue());
			qcTestHdr.setProdserlno((String) tfPrdSlNo.getValue());
			qcTestHdr.setSamplestested(Long.valueOf(tfSamplTst.getValue()));
			qcTestHdr.setQtyfailed(Long.valueOf(tfQtyFailed.getValue()));
			if (chFitmnt.getValue().equals(true)) {
				qcTestHdr.setFitmentokyn("Y");
			} else {
				qcTestHdr.setFitmentokyn("N");
			}
			if (chVisl.getValue().equals(true)) {
				qcTestHdr.setVisualokyn("Y");
			} else {
				qcTestHdr.setVisualokyn("N");
			}
			qcTestHdr.setQcresult(tfQcReslt.getValue());
			qcTestHdr.setReceiptid((Long) cbReceipt.getValue());
			qcTestHdr.setQcobervations((String) taObserv.getValue());
			qcTestHdr.setOthertstremark(taQcRemarks.getValue());
			qcTestHdr.setQcteststatus((String) cbQcHdrStatus.getValue());
			qcTestHdr.setPreparedby(EmployeeId);
			qcTestHdr.setActionedby(null);
			qcTestHdr.setReviewedby(null);
			qcTestHdr.setLastupdateddate(DateUtils.getcurrentdate());
			qcTestHdr.setLastupdatedby(userName);
			serviceQcTstHdr.saveQcTestHdr(qcTestHdr);
			@SuppressWarnings("unchecked")
			Collection<QcTestDtlDM> itemIds = (Collection<QcTestDtlDM>) tblQcDtlTbl.getVisibleItemIds();
			for (QcTestDtlDM save : (Collection<QcTestDtlDM>) itemIds) {
				save.setQcTestID(Long.valueOf(qcTestHdr.getQctestid()));
				serviceQcTstDtl.saveQcTestDtl(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyid, branchID, moduleId, "MF_QCINSNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSLNo.updateNextSequenceNumber(companyid, branchID, moduleId, "MF_QCINSNO");
					}
				}
			}
			resetQcTestDtl();
			resetFields();
			loadSrchRslt();
			loadSrchQCDtlList();
			comment.saveqaSignOffId(qcTestHdr.getQctesttypeid());
			comment.resetFields();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveQCTstDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Saving Data... ");
		QcTestDtlDM qcTestDTl = new QcTestDtlDM();
		if (tblQcDtlTbl.getValue() != null) {
			qcTestDTl = beanQcTstDtl.getItem(tblQcDtlTbl.getValue()).getBean();
			listQcTstDtl.remove(qcTestDTl);
		}
		qcTestDTl.setQcTestSpecId(Long.valueOf(((QCTestSpecificationDM) cbTstSpec.getValue()).getQcTstSpecId()));
		qcTestDTl.setTstSpec(((QCTestSpecificationDM) cbTstSpec.getValue()).getTstSpec());
		qcTestDTl.setTestSpecRslt(tfTstSpecReslt.getValue());
		qcTestDTl.setQcRemarks(taTstRemarks.getValue());
		qcTestDTl.setQcTestStatus((String) cbQcTstDtlStatus.getValue());
		qcTestDTl.setLastUpdatedDT(DateUtils.getcurrentdate());
		qcTestDTl.setLastUpdatedBy(userName);
		listQcTstDtl.add(qcTestDTl);
		resetQcTestDtl();
		loadSrchQCDtlList();
		btnSaveTst.setCaption("Add");
	}
	
	private boolean qcTstDtlValidation() {
		boolean isValid = true;
		if (cbTstSpec.getValue() == null) {
			cbTstSpec.setComponentError(new UserError(GERPErrorCodes.NULL_QC_MTRLTYPE));
			isValid = false;
		} else {
			cbTstSpec.setComponentError(null);
		}
		if (tfTstSpecReslt.getValue() == "" || tfTstSpecReslt.getValue().trim().length() == 0
				|| tfTstSpecReslt.getValue() == null) {
			tfTstSpecReslt.setComponentError(new UserError(GERPErrorCodes.NULL_QC_MTRLNAME));
			isValid = false;
		} else {
			tfTstSpecReslt.setComponentError(null);
		}
		return isValid;
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
		cbBranch.setValue(null);
		cbProduct.setValue(null);
		tfInSpecNo.setValue("");
		cbQcHdrStatus.setValue(cbQcHdrStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	private void deleteTstDefDetails() {
		QcTestDtlDM qcTestDtlDM = new QcTestDtlDM();
		if (tblQcDtlTbl.getValue() != null) {
			qcTestDtlDM = beanQcTstDtl.getItem(tblQcDtlTbl.getValue()).getBean();
			listQcTstDtl.remove(qcTestDtlDM);
			resetQcTestDtl();
			tblQcDtlTbl.setValue("");
			loadSrchQCDtlList();
			btnDtete.setEnabled(false);
		}
	}
}
