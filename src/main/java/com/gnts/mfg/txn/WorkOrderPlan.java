/**
 * File Name	:	WorkOrderPlan.java
 * Description	:	This Screen Purpose for Modify the WorkOrder Details.
 * 					Add the WorkOrder details process should be directly added in DB.
 * Author		:	Nandhakumar.S
 * 
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1          14-Jul-2014		  Nandhakumar.S		Initial version 
 */
package com.gnts.mfg.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.SlnoGenDM;
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
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.mst.TestSpecificationDM;
import com.gnts.mfg.domain.txn.WorkOrderDtlDM;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.domain.txn.WorkOrderPlanHdrDM;
import com.gnts.mfg.domain.txn.WorkOrderPlanMtrlDtlDM;
import com.gnts.mfg.domain.txn.WorkOrderPlanProdDtlDM;
import com.gnts.mfg.service.txn.WorkOrderDtlService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.mfg.service.txn.WorkOrderPlanHdrService;
import com.gnts.mfg.service.txn.WorkOrderPlanMtrlDtlService;
import com.gnts.mfg.service.txn.WorkOrderPlanProdDtlService;
import com.gnts.mms.domain.mst.ProductBomDtlDM;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class WorkOrderPlan extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private WorkOrderPlanMtrlDtlService serviceWorkOrderPlanMtrlDtl = (WorkOrderPlanMtrlDtlService) SpringContextHelper
			.getBean("workOrderPlnMtrl");
	private WorkOrderPlanProdDtlService serviceWorkOrderPlanProdDtl = (WorkOrderPlanProdDtlService) SpringContextHelper
			.getBean("workOrderPlnPrd");
	private WorkOrderPlanHdrService serviceWorkOrderPlanHdr = (WorkOrderPlanHdrService) SpringContextHelper
			.getBean("workOrderPlnHdr");
	private WorkOrderHdrService serviceWorkOrderHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private WorkOrderDtlService serviceWorkOrderDtl = (WorkOrderDtlService) SpringContextHelper.getBean("workOrderDtl");
	// form layout for input controls for Work Order Header
	private FormLayout flOdrHdrColumn1, flOdrHdrColumn2, flOdrHdrColumn3, flOdrHdrColumn4;
	// form layout for input controls for Work Order Details
	private FormLayout flOdrDtlColumn1, flOdrDtlColumn2, flOdrDtlColumn3;
	// User Input Components for WO.Plan Header
	private TextField tfPlanNo;
	private TextArea taPlanRemrks;
	private PopupDateField planDt;
	private ComboBox cbBranchName, cbPlanStatus, cbWONo;
	// User Input Components for WO.Plan Detail
	private ComboBox cbPrdName, cbPlanDtlStatus;
	private CheckBox chIsRequired;
	private TextField tfPrdName, tfWrkOdrPlnQty;
	private Table tblWrkOrdPlnDtl;
	private Button btnsaveWrkOdrPlDtl;
	// User Input Components for WO.Plan Material
	private Table tblWrkOrdPlnMtrlDtl;
	private TextField tfRequiredQty;
	// BeanItem container of
	private BeanItemContainer<WorkOrderPlanHdrDM> beanWrkOdrPlnHdr = null;
	private BeanItemContainer<WorkOrderPlanProdDtlDM> beanWrkOdrPlnPrdDtl = null;
	private BeanItemContainer<WorkOrderPlanMtrlDtlDM> beanWrkOdrPlnMtrlDtl = null;
	List<WorkOrderPlanMtrlDtlDM> workOdrPlnMtrlDtlList = null;
	List<WorkOrderPlanProdDtlDM> workOdrPlnPrdDtlList;
	// local variables declaration
	private String username, roleName;
	private Long companyid, EmployeeId, moduleId, branchID, appScreenId, roleId;
	private int recordCnt;
	private int flag = 0;
	private Long wrkOdrHdrId, branchId, woPlnHdr, productId;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private HorizontalLayout hlOdrDtl, hlWrkOdrHdr;
	private HorizontalLayout hlWorkOdrHdl;
	// Initialize logger
	private static Logger logger = Logger.getLogger(WorkOrder.class);
	boolean flagtblPDtl = false;
	public Button btnDtleWrkDtl = new GERPButton("Delete", "delete", this);
	private Comments comment;
	VerticalLayout vlTableForm = new VerticalLayout();
	private Long commentby;
	
	// Constructor received the parameters from Login UI class
	public WorkOrderPlan() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		EmployeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Company() constructor");
		// Loading the WorkOrderPlan UI
		buildView();
	}
	
	private void buildView() {
		tfPlanNo = new GERPTextField("Plan.No");
		tfPlanNo.setWidth("140");
		tfPlanNo.setReadOnly(true);
		tfPlanNo.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfPlanNo.setComponentError(null);
				String workOrdNo = tfPlanNo.getValue().toString();
				if (workOrdNo == null || workOrdNo == "") {
					tfPlanNo.setComponentError(new UserError(GERPErrorCodes.NULL_WO_PLN_NO));
				} else {
					System.out.println("Work Order Plan Number Sequence : " + workOrdNo);
					int count = serviceWorkOrderPlanHdr.getWorkOrderPlanHdr(companyid, null, workOrdNo,
							(String) cbPlanStatus.getValue()).size();
					if (count == 0) {
						tfPlanNo.setComponentError(null);
					} else {
						if (flag == 0) {
							tfPlanNo.setComponentError(new UserError(GERPErrorCodes.NULL_WO_PLN_NO_EX));
						}
						if (flag == 1) {
							tfPlanNo.setComponentError(null);
						}
					}
				}
			}
		});
		btnDtleWrkDtl.setEnabled(false);
		btnDtleWrkDtl.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteTstCondtnDetails();
			}
		});
		taPlanRemrks = new GERPTextArea("Plan Remarks");
		taPlanRemrks.setWidth("150");
		taPlanRemrks.setHeight("50");
		tfRequiredQty = new GERPTextField("");
		planDt = new GERPPopupDateField("Plan Date");
		cbBranchName = new GERPComboBox("Branch Name");
		cbBranchName.setItemCaptionPropertyId("branchName");
		cbBranchName.setWidth("140");
		loadBranchlist();
		cbPrdName = new GERPComboBox("Product Name");
		cbPrdName.setWidth("130");
		cbPrdName.setItemCaptionPropertyId("prodName");
		cbPrdName.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					WorkOrderDtlDM obj = (WorkOrderDtlDM) cbPrdName.getValue();
					tfPrdName.setValue(obj.getProductName());
					tfWrkOdrPlnQty.setValue(obj.getPlanQty().toString());
					// if (flagtblPDtl) {
					loadSrchWrkOdrPlnMtrlDtlRslt(false);
					flagtblPDtl = true;
					// }
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		cbWONo = new GERPComboBox("WO.No");
		cbWONo.setItemCaptionPropertyId("workOrdrNo");
		cbWONo.setWidth("120");
		loadWorkOrderNo();
		cbWONo.addValueChangeListener(new Property.ValueChangeListener() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbWONo.getItem(itemId);
				if (item != null) {
					loadProductList();
				}
			}
		});
		cbPlanDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Plan Detail table
		/*
		 * cbPrdName = new GERPComboBox("Product Name"); cbPrdName.setWidth("130");
		 * cbPrdName.setItemCaptionPropertyId("prodName"); cbPrdName.addValueChangeListener(new
		 * Property.ValueChangeListener() { private static final long serialVersionUID = 1L; public void
		 * valueChange(ValueChangeEvent event) { // Get the selected item Object itemId =
		 * event.getProperty().getValue(); BeanItem<?> item = (BeanItem<?>) cbPrdName.getItem(itemId); if (item != null)
		 * { WorkOrderDtlDM obj = (WorkOrderDtlDM) cbPrdName.getValue(); tfPrdName.setValue(obj.getProductName());
		 * tfWrkOdrPlnQty.setValue(obj.getPlanQty().toString()); //if (flagtblPDtl) {
		 * loadSrchWrkOdrPlnMtrlDtlRslt(false); flagtblPDtl=true; //} } } });
		 */
		tfPrdName = new GERPTextField("Cust.Product Name");
		tfPrdName.setWidth("130");
		tfWrkOdrPlnQty = new GERPTextField("WO.Plan.Qty");
		tfWrkOdrPlnQty.setWidth("110");
		tblWrkOrdPlnDtl = new GERPTable();
		tblWrkOrdPlnDtl.setPageLength(5);
		tblWrkOrdPlnDtl.setWidth("100%");
		tblWrkOrdPlnMtrlDtl = new GERPTable();
		tblWrkOrdPlnMtrlDtl.setPageLength(5);
		tblWrkOrdPlnMtrlDtl.setWidth("100%");
		btnsaveWrkOdrPlDtl = new GERPButton("Add", "add", this);
		btnsaveWrkOdrPlDtl.setVisible(true);
		btnsaveWrkOdrPlDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateWOPrddtl()) {
					saveWorkOdrPlnDtlDetails();
				}
			}
		});
		tblWrkOrdPlnDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblWrkOrdPlnDtl.isSelected(event.getItemId())) {
					tblWrkOrdPlnDtl.setImmediate(true);
					btnsaveWrkOdrPlDtl.setCaption("Add");
					btnsaveWrkOdrPlDtl.setStyleName("savebt");
					wrkOdrPlnDtlresetFields();
					btnDtleWrkDtl.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnsaveWrkOdrPlDtl.setCaption("Update");
					btnsaveWrkOdrPlDtl.setStyleName("savebt");
					flagtblPDtl = false;
					editWorkOrderPlnDtlDetails();
					flagtblPDtl = true;
					btnDtleWrkDtl.setEnabled(true);
				}
			}
		});
		List<ApprovalSchemaDM> list = serviceWorkOrderPlanHdr.getReviewerId(companyid, appScreenId, branchID, roleId);
		System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + companyid + "," + appScreenId
				+ "," + branchID + "," + roleId);
		for (ApprovalSchemaDM obj : list) {
			roleName = obj.getApprLevel();
			System.out.println("roleName is >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + roleName);
			if (roleName.equals("Approver")) {
				cbPlanStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WO_PLAN_HDR, BASEConstants.APPROVE_LVL);
			} else {
				cbPlanStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WO_PLAN_HDR, BASEConstants.REVIEWER_LVL);
			}
		}
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		wrkOdrPlnDtlresetFields();
		loadSrchRslt();
		loadSrchWrkOdrPlnDtlRslt();
		loadSrchWrkOdrPlnMtrlDtlRslt(false);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for TestType UI search layout
		hlSearchLayout.removeAllComponents();
		flOdrHdrColumn1 = new GERPFormLayout();
		flOdrHdrColumn2 = new GERPFormLayout();
		flOdrHdrColumn3 = new GERPFormLayout();
		// Adding components into form layouts for TestType UI search layout
		flOdrHdrColumn1.addComponent(cbBranchName);
		flOdrHdrColumn2.addComponent(tfPlanNo);
		flOdrHdrColumn3.addComponent(cbPlanStatus);
		// Adding form layouts into search layout for TestType UI search mode
		hlSearchLayout.addComponent(flOdrHdrColumn1);
		hlSearchLayout.addComponent(flOdrHdrColumn2);
		hlSearchLayout.addComponent(flOdrHdrColumn3);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
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
		cbBranchName.setRequired(true);
		tfPlanNo.setRequired(true);
		planDt.setRequired(true);
		cbPlanStatus.setRequired(true);
		// adding components into first column in form layout1
		flOdrHdrColumn1.addComponent(tfPlanNo);
		flOdrHdrColumn1.addComponent(cbBranchName);
		// adding components into second column in form layout2
		flOdrHdrColumn1.addComponent(cbWONo);
		flOdrHdrColumn1.addComponent(planDt);
		flOdrHdrColumn2.addComponent(taPlanRemrks);
		flOdrHdrColumn2.addComponent(cbPlanStatus);
		//
		flOdrDtlColumn1 = new FormLayout();
		flOdrDtlColumn2 = new FormLayout();
		flOdrDtlColumn3 = new FormLayout();
		// adding components into first column in form layout1
		// Initialization for work order Details user input components
		flOdrDtlColumn1.addComponent(cbPrdName);
		// adding components into second column in form layout2
		flOdrDtlColumn1.addComponent(tfPrdName);
		flOdrDtlColumn2.addComponent(tfWrkOdrPlnQty);
		flOdrDtlColumn2.addComponent(cbPlanDtlStatus);
		flOdrDtlColumn3.addComponent(btnsaveWrkOdrPlDtl);
		flOdrDtlColumn3.addComponent(btnDtleWrkDtl);
		// adding form layouts into user input layouts
		hlWrkOdrHdr = new HorizontalLayout();
		hlWrkOdrHdr.addComponent(flOdrHdrColumn1);
		hlWrkOdrHdr.addComponent(flOdrHdrColumn2);
		hlWrkOdrHdr.setSpacing(true);
		hlWrkOdrHdr.setMargin(true);
		// adding form layouts into user input layouts
		hlOdrDtl = new HorizontalLayout();
		hlOdrDtl.addComponent(flOdrDtlColumn1);
		hlOdrDtl.addComponent(flOdrDtlColumn2);
		hlOdrDtl.addComponent(flOdrDtlColumn3);
		hlOdrDtl.setSpacing(true);
		hlOdrDtl.setMargin(true);
		VerticalLayout hdrAndDtl = new VerticalLayout();
		hdrAndDtl.addComponent(hlOdrDtl);
		hdrAndDtl.addComponent(GERPPanelGenerator.createPanel(hlOdrDtl));
		hdrAndDtl.addComponent(tblWrkOrdPlnDtl);
		hdrAndDtl.setWidth("100%");
		VerticalLayout vl = new VerticalLayout();
		vl.addComponent(hdrAndDtl);
		vl.addComponent(GERPPanelGenerator.createPanel(hdrAndDtl));
		vl.addComponent(tblWrkOrdPlnMtrlDtl);
		vl.setWidth("100%");
		//
		VerticalLayout vlHdrNCmt = new VerticalLayout();
		vlHdrNCmt.addComponent(GERPPanelGenerator.createPanel(hlWrkOdrHdr));
		vlHdrNCmt.addComponent(GERPPanelGenerator.createPanel(vlTableForm));
		vlHdrNCmt.setWidth("100%");
		vlHdrNCmt.setSpacing(true);
		hlWorkOdrHdl = new HorizontalLayout();
		hlWorkOdrHdl.addComponent(vlHdrNCmt);
		hlWorkOdrHdl.addComponent(vl);
		hlWorkOdrHdl.setWidth("100%");
		hlWorkOdrHdl.setSpacing(true);
		hlWorkOdrHdl.setMargin(false);
		hlUserInputLayout.addComponent(hlWorkOdrHdl);
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<WorkOrderPlanHdrDM> wrkOrdHdrList = new ArrayList<WorkOrderPlanHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", ");
		if (cbBranchName.getValue() != null) {
			branchId = ((WorkOrderHdrDM) cbBranchName.getValue()).getBranchId();
		}
		wrkOrdHdrList = serviceWorkOrderPlanHdr.getWorkOrderPlanHdr(null, branchId, tfPlanNo.getValue(),
				(String) cbPlanStatus.getValue());
		recordCnt = wrkOrdHdrList.size();
		beanWrkOdrPlnHdr = new BeanItemContainer<WorkOrderPlanHdrDM>(WorkOrderPlanHdrDM.class);
		beanWrkOdrPlnHdr.addAll(wrkOrdHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the WorkOrderPlanHdrDM. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanWrkOdrPlnHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "wrkPlanId", "branchName", "wrkPlanNo", "planDt",
				"workStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Brand Name", "Work Order Plan No.", "Plan Date",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("wrkPlanId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadSrchWrkOdrPlnDtlRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid);
		int recorddtlCnt = workOdrPlnPrdDtlList.size();
		beanWrkOdrPlnPrdDtl = new BeanItemContainer<WorkOrderPlanProdDtlDM>(WorkOrderPlanProdDtlDM.class);
		beanWrkOdrPlnPrdDtl.addAll(workOdrPlnPrdDtlList);
		tblWrkOrdPlnDtl.setContainerDataSource(beanWrkOdrPlnPrdDtl);
		tblWrkOrdPlnDtl
				.setVisibleColumns(new Object[] { "prodName", "productName", "wrkOrdPlnQty", "wrkOrdPlnStatus" });
		tblWrkOrdPlnDtl.setColumnHeaders(new String[] { "Product Name", "Customer Product Name", "WO.Plan Qty.",
				"Status" });
		tblWrkOrdPlnDtl.setColumnAlignment("productName", Align.LEFT);
		tblWrkOrdPlnDtl.setFooterVisible(true);
		tblWrkOrdPlnDtl.setColumnFooter("wrkOrdPlnStatus", "No.of Records : " + recorddtlCnt);
	}
	
	private void loadSrchWrkOdrPlnMtrlDtlRslt(boolean loadMtrlTble) {
		int recordCntmrl;
		if (!loadMtrlTble) {
			List<ProductBomDtlDM> wrkOrdPlnMtrlList = new ArrayList<ProductBomDtlDM>();
			logger.info("Inside loadSrchWrkOdrPlnMtrlDtlRslt >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			if (cbPrdName.getValue() != null) {
				System.out.println("productiddddddddddddd>>>>>>" + productId);
				productId = ((WorkOrderDtlDM) cbPrdName.getValue()).getProdId();
			}
			wrkOrdPlnMtrlList = serviceWorkOrderPlanMtrlDtl.getProductBomDtlDM(productId, "Y");
			List<WorkOrderPlanMtrlDtlDM> workOrdMtrllList = new ArrayList<WorkOrderPlanMtrlDtlDM>();
			for (ProductBomDtlDM obj : wrkOrdPlnMtrlList) {
				WorkOrderPlanMtrlDtlDM list = new WorkOrderPlanMtrlDtlDM();
				list.setMtrlId(obj.getMaterialId());
				list.setMtrlName(obj.getMaterialName());
				list.setRequiredQty(obj.getMaterialQty());
				workOrdMtrllList.add(list);
			}
			recordCntmrl = wrkOrdPlnMtrlList.size();
			logger.info("wrkOrdPlnMtrlList.size() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + recordCntmrl);
			beanWrkOdrPlnMtrlDtl = new BeanItemContainer<WorkOrderPlanMtrlDtlDM>(WorkOrderPlanMtrlDtlDM.class);
			beanWrkOdrPlnMtrlDtl.addAll(workOrdMtrllList);
			tblWrkOrdPlnMtrlDtl.setContainerDataSource(beanWrkOdrPlnMtrlDtl);
		} else {
			workOdrPlnMtrlDtlList = serviceWorkOrderPlanMtrlDtl.getWrkOrdPlnMtrlDtlDetails(null, null, woPlnHdr, null);
			recordCntmrl = workOdrPlnMtrlDtlList.size();
			beanWrkOdrPlnMtrlDtl = new BeanItemContainer<WorkOrderPlanMtrlDtlDM>(WorkOrderPlanMtrlDtlDM.class);
			beanWrkOdrPlnMtrlDtl.addAll(workOdrPlnMtrlDtlList);
			for (WorkOrderPlanMtrlDtlDM workOrderPlanMtrlDtlDM : workOdrPlnMtrlDtlList) {
				if (workOrderPlanMtrlDtlDM.getWoMtrlStatus().equals("Active")) {
					workOrderPlanMtrlDtlDM.setWoMtrlStatus("true");
				} else {
					workOrderPlanMtrlDtlDM.setWoMtrlStatus("false");
				}
			}
			tblWrkOrdPlnMtrlDtl.setContainerDataSource(beanWrkOdrPlnMtrlDtl);
		}
		tblWrkOrdPlnMtrlDtl.setVisibleColumns(new Object[] { "mtrlName", "requiredQty", "woMtrlStatus" });
		tblWrkOrdPlnMtrlDtl.setColumnHeaders(new String[] { "Material Name", "Required Quantity", "IsRequired" });
		tblWrkOrdPlnMtrlDtl.setColumnAlignment("mtrlName", Align.LEFT);
		tblWrkOrdPlnMtrlDtl.setColumnFooter("woMtrlStatus", "No.of Records : " + recordCntmrl);
		tblWrkOrdPlnMtrlDtl.setEditable(true);
		tblWrkOrdPlnMtrlDtl.setTableFieldFactory(new TableFieldFactory() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
				if (propertyId.toString().equals("woMtrlStatus")) {
					chIsRequired = new CheckBox();
					/*
					 * String type = (String) container.getItem(itemId).getItemProperty("woMtrlStatus").getValue();
					 * System.out.println("type---->" + type); if (type.equals("true")) { chIsRequired.setValue(true); }
					 * else { chIsRequired.setValue(false); }
					 */
					return chIsRequired;
				}
				if (propertyId.toString().equals("requiredQty")) {
					tfRequiredQty = new GERPTextField("");
					tfRequiredQty.setWidth("100");
					return tfRequiredQty;
				}
				return null;
			}
		});
	}
	
	private void wrkOdrPlnDtlresetFields() {
		cbPrdName.setValue(null);
		tfPrdName.setValue("");
		tfWrkOdrPlnQty.setReadOnly(false);
		tfWrkOdrPlnQty.setValue("0");
		cbPlanDtlStatus.setValue(cbPlanDtlStatus.getItemIds().iterator().next());
	}
	
	private void editWorkOrderPlnHdrDetails() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (itselect != null) {
			WorkOrderPlanHdrDM editWorkOrderPlnHdr = beanWrkOdrPlnHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			Long uom = editWorkOrderPlnHdr.getBranchId();
			Collection<?> uomid = cbBranchName.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbBranchName.getItem(itemId);
				// Get the actual bean and use the data
				WorkOrderHdrDM st = (WorkOrderHdrDM) item.getBean();
				if (uom != null && uom.equals(st.getBranchId())) {
					cbBranchName.setValue(itemId);
				}
			}
			woPlnHdr = Long.valueOf(editWorkOrderPlnHdr.getWrkPlanId());
			tfPlanNo.setReadOnly(false);
			tfPlanNo.setValue(itselect.getItemProperty("wrkPlanNo").getValue().toString());
			tfPlanNo.setReadOnly(true);
			Long workordNo = editWorkOrderPlnHdr.getWrkOrderNoID();
			Collection<?> branchID = cbWONo.getItemIds();
			for (Iterator<?> iterator = branchID.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbWONo.getItem(itemId);
				// Get the actual bean and use the data
				WorkOrderHdrDM st = (WorkOrderHdrDM) item.getBean();
				if (workordNo != null && workordNo.equals(st.getWorkOrdrId())) {
					cbWONo.setValue(itemId);
				}
			}
			planDt.setValue(editWorkOrderPlnHdr.getPlanDtInt());
			if (editWorkOrderPlnHdr.getPlanRemarks() != null) {
				taPlanRemrks.setValue((String) itselect.getItemProperty("planRemarks").getValue());
			}
			cbPlanStatus.setValue(itselect.getItemProperty("workStatus").getValue());
			workOdrPlnPrdDtlList = serviceWorkOrderPlanProdDtl.getWorkOrderPlanDtlList(null, woPlnHdr, "Active");
		}
		comment = new Comments(vlTableForm, companyid, null, null, null, null, commentby);
		comment.loadsrch(true, null, companyid, null, null, null, Long.valueOf(woPlnHdr));
		loadSrchWrkOdrPlnDtlRslt();
	}
	
	private void editWorkOrderPlnDtlDetails() {
		logger.info("Inside of wrkorderpln details >>>>>>>>>>>>>>>. ");
		Item itselect = tblWrkOrdPlnDtl.getItem(tblWrkOrdPlnDtl.getValue());
		if (itselect != null) {
			WorkOrderPlanProdDtlDM editWorkOrderPlanProdDtl = new WorkOrderPlanProdDtlDM();
			editWorkOrderPlanProdDtl = beanWrkOdrPlnPrdDtl.getItem(tblWrkOrdPlnDtl.getValue()).getBean();
			Long uom = editWorkOrderPlanProdDtl.getProductId();
			Collection<?> uomid = cbPrdName.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbPrdName.getItem(itemId);
				// Get the actual bean and use the data
				WorkOrderDtlDM st = (WorkOrderDtlDM) item.getBean();
				if (uom != null && uom.equals(st.getProdId())) {
					cbPrdName.setValue(itemId);
				}
			}
			tfPrdName.setValue((String) itselect.getItemProperty("prodName").getValue());
			tfWrkOdrPlnQty.setValue(itselect.getItemProperty("wrkOrdPlnQty").getValue().toString());
			cbPlanDtlStatus.setValue((String) itselect.getItemProperty("wrkOrdPlnStatus").getValue());
		}
	}
	
	private void loadWorkOrderNo() {
		List<WorkOrderHdrDM> getworkOrdHdr = new ArrayList<WorkOrderHdrDM>();
		getworkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, null, null, null, null, "F",
				null, null));
		BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		// beanWrkOrdHdr.setBeanIdProperty("workOrdrId");
		beanWrkOrdHdr.addAll(getworkOrdHdr);
		cbWONo.setContainerDataSource(beanWrkOrdHdr);
	}
	
	private void loadProductList() {
		List<WorkOrderDtlDM> getworkOrderDtl = new ArrayList<WorkOrderDtlDM>();
		Long workOrdHdrId = ((WorkOrderHdrDM) cbWONo.getValue()).getWorkOrdrId();
		getworkOrderDtl.addAll(serviceWorkOrderDtl.getWorkOrderDtlList(null, workOrdHdrId, null, "F"));
		BeanItemContainer<WorkOrderDtlDM> beanPlnDtl = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
		beanPlnDtl.addAll(getworkOrderDtl);
		cbPrdName.setContainerDataSource(beanPlnDtl);
	}
	
	private void loadBranchlist() {
		List<WorkOrderHdrDM> getWrkBranchList = new ArrayList<WorkOrderHdrDM>();
		getWrkBranchList.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, null, null, null, null, "F",
				null, null));
		System.out.println("Branch list is >>>>>>>>>>>>>>>>>>>>>>>>>>>> " + getWrkBranchList.size());
		BeanItemContainer<WorkOrderHdrDM> beanWorkOrderHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		beanWorkOrderHdr.addAll(getWrkBranchList);
		cbBranchName.setContainerDataSource(beanWorkOrderHdr);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbBranchName.setValue(null);
		tfPlanNo.setValue("");
		cbPlanStatus.setValue(null);
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		resetFields();
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		tfPlanNo.setReadOnly(true);
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "MF_PLANNO");
		tfPlanNo.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfPlanNo.setReadOnly(true);
			} else {
				tfPlanNo.setReadOnly(false);
			}
		}
		btnsaveWrkOdrPlDtl.setCaption("Add");
		tblWrkOrdPlnDtl.setVisible(true);
		tblWrkOrdPlnMtrlDtl.setVisible(true);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		loadSrchWrkOdrPlnMtrlDtlRslt(false);
		loadSrchWrkOdrPlnDtlRslt();
		comment = new Comments(vlTableForm, companyid, null, null, null, null, commentby);
	}
	
	// reset the input controls to default value
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "MF_WONO ");
		tfPlanNo.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfPlanNo.setReadOnly(true);
			}
		}
		tfPlanNo.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfPlanNo.setComponentError(null);
				String workOrdNo = tfPlanNo.getValue().toString();
				if (workOrdNo == null || workOrdNo == "") {
					tfPlanNo.setComponentError(new UserError(GERPErrorCodes.NULL_WO_PLN_NO));
				} else {
					System.out.println("Work Order Plan Number Sequence : " + workOrdNo);
					int count = serviceWorkOrderPlanHdr.getWorkOrderPlanHdr(companyid, null, workOrdNo,
							(String) cbPlanStatus.getValue()).size();
					if (count == 0) {
						tfPlanNo.setComponentError(null);
					} else {
						if (flag == 0) {
							tfPlanNo.setComponentError(new UserError(GERPErrorCodes.NULL_WO_PLN_NO_EX));
						}
						if (flag == 1) {
							tfPlanNo.setComponentError(null);
						}
					}
				}
			}
		});
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		tblWrkOrdPlnMtrlDtl.setVisible(true);
		tblWrkOrdPlnDtl.setVisible(true);
		resetFields();
		wrkOdrPlnDtlresetFields();
		editWorkOrderPlnHdrDetails();
		editWorkOrderPlnDtlDetails();
		loadSrchWrkOdrPlnDtlRslt();
		loadSrchWrkOdrPlnMtrlDtlRslt(true);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if ((cbBranchName.getValue() == null)) {
			cbBranchName.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			errorFlag = true;
		} else {
			cbBranchName.setComponentError(null);
		}
		if ((cbWONo.getValue() == null)) {
			cbWONo.setComponentError(new UserError(GERPErrorCodes.NULL_WORK_ORDER_NO));
			errorFlag = true;
		} else {
			cbWONo.setComponentError(null);
		}
		if (planDt.getValue() == null) {
			planDt.setComponentError(new UserError(GERPErrorCodes.NULL_PLN_DT));
			errorFlag = true;
		} else {
			planDt.setComponentError(null);
		}
		if (cbPlanStatus.getValue() == null) {
			cbPlanStatus.setComponentError(new UserError(GERPErrorCodes.NULL_PLN_STATUS));
			errorFlag = true;
		} else {
			cbPlanStatus.setComponentError(null);
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			WorkOrderPlanHdrDM wrkOrdPlnHdr = new WorkOrderPlanHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				wrkOrdPlnHdr = beanWrkOdrPlnHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			} else {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "MF_PLANNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						wrkOrdPlnHdr.setWrkPlanNo(slnoObj.getKeyDesc());
					}
				}
			}
			logger.info("getSequenceNumber(companyid, branchID, moduleId, MF_PLANNO )  >....... " + companyid + ","
					+ branchID + "," + moduleId);
			wrkOrdPlnHdr.setCompanyId(companyid);
			wrkOrdPlnHdr.setBranchId(((WorkOrderHdrDM) cbBranchName.getValue()).getBranchId());
			wrkOrdPlnHdr.setPlanDt(planDt.getValue());
			wrkOrdPlnHdr.setWrkOrderNoID(((WorkOrderHdrDM) cbWONo.getValue()).getWorkOrdrId());
			wrkOrdPlnHdr.setPlanRemarks(taPlanRemrks.getValue());
			wrkOrdPlnHdr.setPrepareBy(EmployeeId);
			wrkOrdPlnHdr.setReviewedBy(null);
			wrkOrdPlnHdr.setActionedBy(null);
			wrkOrdPlnHdr.setWorkStatus((String) cbPlanStatus.getValue());
			wrkOrdPlnHdr.setLastUpdatedDt(DateUtils.getcurrentdate());
			wrkOrdPlnHdr.setLastUpdatedBy(username);
			serviceWorkOrderPlanHdr.saveOrUpdate(wrkOrdPlnHdr);
			@SuppressWarnings("unchecked")
			Collection<WorkOrderPlanProdDtlDM> itemIds = (Collection<WorkOrderPlanProdDtlDM>) tblWrkOrdPlnDtl
					.getVisibleItemIds();
			for (WorkOrderPlanProdDtlDM save : (Collection<WorkOrderPlanProdDtlDM>) itemIds) {
				save.setWrkOrdPlnId(Long.valueOf(wrkOrdPlnHdr.getWrkPlanId()));
				serviceWorkOrderPlanProdDtl.saveOrUpdateWorkOrderPlanProdDtlDM(save);
			}
			@SuppressWarnings("unchecked")
			Collection<WorkOrderPlanMtrlDtlDM> itemIds1 = (Collection<WorkOrderPlanMtrlDtlDM>) tblWrkOrdPlnMtrlDtl
					.getVisibleItemIds();
			for (WorkOrderPlanMtrlDtlDM save : (Collection<WorkOrderPlanMtrlDtlDM>) itemIds1) {
				save.setWoPlnId(Long.valueOf(wrkOrdPlnHdr.getWrkPlanId()));
				if (chIsRequired.getValue() != null && chIsRequired.getValue().equals(true)) {
					save.setWoMtrlStatus("Active");
				} else {
					save.setWoMtrlStatus("Inactive");
				}
				save.setLastUpdatedDt(DateUtils.getcurrentdate());
				save.setLastUpdatedBy(username);
				serviceWorkOrderPlanMtrlDtl.saveOrUpdateWrkOrdPlnMtrlDtlDetails(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "MF_PLANNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "MF_PLANNO");
					}
				}
			}
			// comment.s
			wrkOdrPlnDtlresetFields();
			resetFields();
			loadSrchRslt();
			loadSrchWrkOdrPlnDtlRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for WorkOrderPlanHdr . ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_WO_PLAN_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", wrkOdrHdrId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		// cbBranchName.setReadOnly(false);
		tfPlanNo.setReadOnly(false);
		tblMstScrSrchRslt.setVisible(true);
		tblWrkOrdPlnDtl.setVisible(false);
		tblWrkOrdPlnMtrlDtl.setVisible(false);
		tblWrkOrdPlnMtrlDtl.removeAllItems();
		wrkOdrPlnDtlresetFields();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		cbBranchName.setValue(null);
		cbBranchName.setRequired(false);
		cbBranchName.setComponentError(null);
		tfPlanNo.setReadOnly(false);
		tfPlanNo.setValue("");
		tfPlanNo.setRequired(false);
		tfPlanNo.setComponentError(null);
		cbWONo.setComponentError(null);
		cbWONo.setValue(null);
		cbPrdName.setValue(null);
		tfPrdName.setReadOnly(false);
		tfPrdName.setValue("");
		planDt.setValue(null);
		planDt.setComponentError(null);
		taPlanRemrks.setValue("");
		cbPlanStatus.setValue(null);
		cbPlanStatus.setComponentError(null);
		cbPlanStatus.setRequired(false);
		workOdrPlnPrdDtlList = new ArrayList<WorkOrderPlanProdDtlDM>();
		workOdrPlnMtrlDtlList = new ArrayList<WorkOrderPlanMtrlDtlDM>();
		tblWrkOrdPlnMtrlDtl.removeAllItems();
		tblWrkOrdPlnDtl.removeAllItems();
	}
	
	private boolean validateWOPrddtl() {
		boolean isValid = true;
		if (cbPrdName.getValue() == null) {
			cbPrdName.setComponentError(new UserError(GERPErrorCodes.NULL_QC_PRDNAME));
			isValid = false;
		} else {
			cbPrdName.setComponentError(null);
		}
		try {
			Long.valueOf(tfWrkOdrPlnQty.getValue());
			tfWrkOdrPlnQty.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfWrkOdrPlnQty.setComponentError(new UserError(GERPErrorCodes.NULL_QC_WRKORDQTY));
			isValid = false;
		}
		return isValid;
	}
	
	private void saveWorkOdrPlnDtlDetails() {
		logger.info(" Inside of saveWorkOdrPlnDtlDetails Saving Data... ");
		WorkOrderPlanProdDtlDM wrkOrdplnPrdDtl = new WorkOrderPlanProdDtlDM();
		if (tblWrkOrdPlnDtl.getValue() != null) {
			wrkOrdplnPrdDtl = beanWrkOdrPlnPrdDtl.getItem(tblWrkOrdPlnDtl.getValue()).getBean();
			workOdrPlnPrdDtlList.remove(wrkOrdplnPrdDtl);
		}
		wrkOrdplnPrdDtl.setProductId(((WorkOrderDtlDM) cbPrdName.getValue()).getProdId());
		wrkOrdplnPrdDtl.setProductName(((WorkOrderDtlDM) cbPrdName.getValue()).getProductName());
		wrkOrdplnPrdDtl.setProdName(((WorkOrderDtlDM) cbPrdName.getValue()).getProdName());
		wrkOrdplnPrdDtl.setWrkOrdPlnQty(Long.valueOf(tfWrkOdrPlnQty.getValue()));
		wrkOrdplnPrdDtl.setWrkOrdPlnStatus((String) cbPlanDtlStatus.getValue());
		wrkOrdplnPrdDtl.setLastUpdatedDt(DateUtils.getcurrentdate());
		wrkOrdplnPrdDtl.setLastUpdatedBy(username);
		workOdrPlnPrdDtlList.add(wrkOrdplnPrdDtl);
		wrkOdrPlnDtlresetFields();
		loadSrchWrkOdrPlnDtlRslt();
		btnsaveWrkOdrPlDtl.setCaption("Add");
	}
	
	private void deleteTstCondtnDetails() {
		WorkOrderPlanProdDtlDM workOrderPlanProdDtlDM = new WorkOrderPlanProdDtlDM();
		if (tblWrkOrdPlnDtl.getValue() != null) {
			workOrderPlanProdDtlDM = beanWrkOdrPlnPrdDtl.getItem(tblWrkOrdPlnDtl.getValue()).getBean();
			workOdrPlnPrdDtlList.remove(workOrderPlanProdDtlDM);
			wrkOdrPlnDtlresetFields();
			tblWrkOrdPlnDtl.setValue("");
			loadSrchWrkOdrPlnDtlRslt();
			btnDtleWrkDtl.setEnabled(false);
		}
	}
}