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
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
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
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.util.DateUtils;
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
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
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

public class WorkOrderPlan extends BaseTransUI {
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
	private BranchService servicebeanBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	// form layout for input controls for Work Order Header
	private FormLayout flOdrHdrColumn1, flOdrHdrColumn2, flOdrHdrColumn3;
	// form layout for input controls for Work Order Details
	private FormLayout flOdrDtlColumn1, flOdrDtlColumn2, flOdrDtlColumn3;
	// User Input Components for WO.Plan Header
	private TextField tfPlanNo;
	private TextArea taPlanRemrks;
	private PopupDateField dfPlanDate;
	private ComboBox cbBranchName, cbPlanStatus, cbWorkOrderNo;
	// User Input Components for WO.Plan Detail
	private ComboBox cbProductName, cbPlanDtlStatus;
	private CheckBox chIsRequired;
	private TextField tfProductName, tfWrkOdrPlnQty;
	private Table tblWrkOrdPlnDtl;
	private Button btnsaveWrkOdrPlDtl;
	// User Input Components for WO.Plan Material
	private Table tblWOPlanMaterials;
	private TextField tfRequiredQty;
	// BeanItem container of
	private BeanItemContainer<WorkOrderPlanHdrDM> beanWrkOdrPlnHdr = null;
	private BeanItemContainer<WorkOrderPlanProdDtlDM> beanWrkOdrPlnPrdDtl = null;
	private BeanItemContainer<WorkOrderPlanMtrlDtlDM> beanWrkOdrPlnMtrlDtl = null;
	private List<WorkOrderPlanMtrlDtlDM> workOdrPlnMtrlDtlList = null;
	private List<WorkOrderPlanProdDtlDM> workOdrPlnPrdDtlList;
	// local variables declaration
	private String username;
	private Long companyid, employeeId, moduleId, branchId, appScreenId, roleId;
	private int recordCnt;
	private int flag = 0;
	private Long wrkOdrHdrId, woPlnHdr, productId;
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private HorizontalLayout hlOdrDtl, hlWrkOdrHdr;
	private HorizontalLayout hlWorkOdrHdl;
	// Initialize logger
	private Logger logger = Logger.getLogger(WorkOrder.class);
	private Button btnDtleWrkDtl = new GERPButton("Delete", "delete", this);
	private Comments comment;
	private VerticalLayout vlTableForm = new VerticalLayout();
	private Long commentby;
	
	// Constructor received the parameters from Login UI class
	public WorkOrderPlan() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
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
		dfPlanDate = new GERPPopupDateField("Plan Date");
		dfPlanDate.setWidth("120px");
		cbBranchName = new GERPComboBox("Branch Name");
		cbBranchName.setItemCaptionPropertyId("branchName");
		cbBranchName.setWidth("140");
		loadBranchlist();
		cbProductName = new GERPComboBox("Product Name");
		cbProductName.setWidth("130");
		cbProductName.setItemCaptionPropertyId("prodName");
		cbProductName.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					WorkOrderDtlDM workOrderDtlDM = (WorkOrderDtlDM) cbProductName.getValue();
					tfProductName.setValue(workOrderDtlDM.getProductName());
					tfWrkOdrPlnQty.setValue(workOrderDtlDM.getPlanQty().toString());
					loadSrchWrkOdrPlnMtrlDtlRslt(false);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		cbWorkOrderNo = new GERPComboBox("WO.No");
		cbWorkOrderNo.setItemCaptionPropertyId("workOrdrNo");
		cbWorkOrderNo.setWidth("140");
		loadWorkOrderNo();
		cbWorkOrderNo.addValueChangeListener(new Property.ValueChangeListener() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbWorkOrderNo.getItem(itemId);
				if (item != null) {
					loadProductList();
				}
			}
		});
		cbPlanDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		tfProductName = new GERPTextField("Cust.Product Name");
		tfProductName.setWidth("130");
		tfWrkOdrPlnQty = new GERPTextField("WO.Plan.Qty");
		tfWrkOdrPlnQty.setWidth("110");
		tblWrkOrdPlnDtl = new GERPTable();
		tblWrkOrdPlnDtl.setPageLength(5);
		tblWrkOrdPlnDtl.setWidth("100%");
		tblWOPlanMaterials = new GERPTable();
		tblWOPlanMaterials.setPageLength(15);
		tblWOPlanMaterials.setWidth("100%");
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
					editWorkOrderPlnDtlDetails();
					btnDtleWrkDtl.setEnabled(true);
				}
			}
		});
		try {
			ApprovalSchemaDM obj = serviceWorkOrderPlanHdr.getReviewerId(companyid, appScreenId, branchId, roleId).get(
					0);
			if (obj.getApprLevel().equals("Approver")) {
				cbPlanStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WO_PLAN_HDR, BASEConstants.APPROVE_LVL);
			} else {
				cbPlanStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WO_PLAN_HDR, BASEConstants.REVIEWER_LVL);
			}
		}
		catch (Exception e) {
		}
		cbPlanStatus.setWidth("150");
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
		dfPlanDate.setRequired(true);
		cbPlanStatus.setRequired(true);
		// adding components into first column in form layout1
		flOdrHdrColumn1.addComponent(tfPlanNo);
		flOdrHdrColumn1.addComponent(cbBranchName);
		// adding components into second column in form layout2
		flOdrHdrColumn1.addComponent(cbWorkOrderNo);
		flOdrHdrColumn1.addComponent(dfPlanDate);
		flOdrHdrColumn2.addComponent(taPlanRemrks);
		flOdrHdrColumn2.addComponent(cbPlanStatus);
		//
		flOdrDtlColumn1 = new FormLayout();
		flOdrDtlColumn2 = new FormLayout();
		flOdrDtlColumn3 = new FormLayout();
		// adding components into first column in form layout1
		// Initialization for work order Details user input components
		flOdrDtlColumn1.addComponent(cbProductName);
		// adding components into second column in form layout2
		flOdrDtlColumn1.addComponent(tfProductName);
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
		//
		VerticalLayout vlHdrNCmt = new VerticalLayout();
		vlHdrNCmt.addComponent(GERPPanelGenerator.createPanel(hlWrkOdrHdr));
		vlHdrNCmt.addComponent(GERPPanelGenerator.createPanel(hlOdrDtl));
		vlHdrNCmt.addComponent(tblWrkOrdPlnDtl);
		vlHdrNCmt.setWidth("100%");
		vlHdrNCmt.setSpacing(true);
		hlWorkOdrHdl = new HorizontalLayout();
		hlWorkOdrHdl.addComponent(vlHdrNCmt);
		hlWorkOdrHdl.addComponent(tblWOPlanMaterials);
		hlWorkOdrHdl.setWidth("100%");
		hlWorkOdrHdl.setSpacing(true);
		hlWorkOdrHdl.setMargin(false);
		hlUserInputLayout.addComponent(hlWorkOdrHdl);
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<WorkOrderPlanHdrDM> listWOPlan = new ArrayList<WorkOrderPlanHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", ");
		listWOPlan = serviceWorkOrderPlanHdr.getWorkOrderPlanHdr(null, (Long) cbBranchName.getValue(),
				tfPlanNo.getValue(), (String) cbPlanStatus.getValue());
		recordCnt = listWOPlan.size();
		beanWrkOdrPlnHdr = new BeanItemContainer<WorkOrderPlanHdrDM>(WorkOrderPlanHdrDM.class);
		beanWrkOdrPlnHdr.addAll(listWOPlan);
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
			if (cbProductName.getValue() != null) {
				productId = ((WorkOrderDtlDM) cbProductName.getValue()).getProdId();
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
			beanWrkOdrPlnMtrlDtl = new BeanItemContainer<WorkOrderPlanMtrlDtlDM>(WorkOrderPlanMtrlDtlDM.class);
			beanWrkOdrPlnMtrlDtl.addAll(workOrdMtrllList);
			tblWOPlanMaterials.setContainerDataSource(beanWrkOdrPlnMtrlDtl);
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
			tblWOPlanMaterials.setContainerDataSource(beanWrkOdrPlnMtrlDtl);
		}
		tblWOPlanMaterials.setVisibleColumns(new Object[] { "mtrlName", "requiredQty", "woMtrlStatus" });
		tblWOPlanMaterials.setColumnHeaders(new String[] { "Material Name", "Required Quantity", "IsRequired" });
		tblWOPlanMaterials.setColumnAlignment("mtrlName", Align.LEFT);
		tblWOPlanMaterials.setColumnFooter("woMtrlStatus", "No.of Records : " + recordCntmrl);
		tblWOPlanMaterials.setEditable(true);
		tblWOPlanMaterials.setTableFieldFactory(new TableFieldFactory() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
				if (propertyId.toString().equals("woMtrlStatus")) {
					chIsRequired = new CheckBox();
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
		cbProductName.setValue(null);
		tfProductName.setValue("");
		tfWrkOdrPlnQty.setReadOnly(false);
		tfWrkOdrPlnQty.setValue("0");
		cbPlanDtlStatus.setValue(cbPlanDtlStatus.getItemIds().iterator().next());
	}
	
	private void editWorkOrderPlnHdrDetails() {
		if (tblMstScrSrchRslt.getValue() != null) {
			WorkOrderPlanHdrDM workOrderPlanHdr = beanWrkOdrPlnHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cbBranchName.setValue(workOrderPlanHdr.getBranchId());
			woPlnHdr = Long.valueOf(workOrderPlanHdr.getWrkPlanId());
			tfPlanNo.setReadOnly(false);
			tfPlanNo.setValue(workOrderPlanHdr.getWrkPlanNo());
			tfPlanNo.setReadOnly(true);
			Long workordNo = workOrderPlanHdr.getWrkOrderNoID();
			Collection<?> woids = cbWorkOrderNo.getItemIds();
			for (Iterator<?> iterator = woids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbWorkOrderNo.getItem(itemId);
				// Get the actual bean and use the data
				WorkOrderHdrDM st = (WorkOrderHdrDM) item.getBean();
				if (workordNo != null && workordNo.equals(st.getWorkOrdrId())) {
					cbWorkOrderNo.setValue(itemId);
				}
			}
			dfPlanDate.setValue(workOrderPlanHdr.getPlanDtInt());
			if (workOrderPlanHdr.getPlanRemarks() != null) {
				taPlanRemrks.setValue(workOrderPlanHdr.getPlanRemarks());
			}
			cbPlanStatus.setValue(workOrderPlanHdr.getWorkStatus());
			workOdrPlnPrdDtlList = serviceWorkOrderPlanProdDtl.getWorkOrderPlanDtlList(null, woPlnHdr, "Active");
		}
		comment = new Comments(vlTableForm, companyid, null, null, null, null, commentby);
		comment.loadsrch(true, null, companyid, null, null, null, Long.valueOf(woPlnHdr));
		loadSrchWrkOdrPlnDtlRslt();
	}
	
	private void editWorkOrderPlnDtlDetails() {
		if (tblWrkOrdPlnDtl.getValue() != null) {
			WorkOrderPlanProdDtlDM workOrderPlanDtlDM = new WorkOrderPlanProdDtlDM();
			workOrderPlanDtlDM = beanWrkOdrPlnPrdDtl.getItem(tblWrkOrdPlnDtl.getValue()).getBean();
			Long prodid = workOrderPlanDtlDM.getProductId();
			Collection<?> prodids = cbProductName.getItemIds();
			for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProductName.getItem(itemId);
				// Get the actual bean and use the data
				WorkOrderDtlDM st = (WorkOrderDtlDM) item.getBean();
				if (prodid != null && prodid.equals(st.getProdId())) {
					cbProductName.setValue(itemId);
				}
			}
			tfProductName.setValue(workOrderPlanDtlDM.getProdName());
			tfWrkOdrPlnQty.setValue(workOrderPlanDtlDM.getWrkOrdPlnQty().toString());
			cbPlanDtlStatus.setValue(workOrderPlanDtlDM.getWrkOrdPlnStatus());
		}
	}
	
	private void loadWorkOrderNo() {
		BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		beanWrkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, null, null, null, null, "F",
				null, null,null,null,null));
		cbWorkOrderNo.setContainerDataSource(beanWrkOrdHdr);
	}
	
	private void loadProductList() {
		Long workOrdHdrId = ((WorkOrderHdrDM) cbWorkOrderNo.getValue()).getWorkOrdrId();
		BeanItemContainer<WorkOrderDtlDM> beanPlnDtl = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
		beanPlnDtl.addAll(serviceWorkOrderDtl.getWorkOrderDtlList(null, workOrdHdrId, null, "F"));
		cbProductName.setContainerDataSource(beanPlnDtl);
	}
	
	private void loadBranchlist() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, BranchDM> beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranch.setBeanIdProperty("branchId");
		beanBranch.addAll(servicebeanBranch.getBranchList(null, null, null, "Active", companyid, "P"));
		cbBranchName.setContainerDataSource(beanBranch);
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
		try {
			tfPlanNo.setReadOnly(false);
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MF_PLANNO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfPlanNo.setValue(slnoObj.getKeyDesc());
				tfPlanNo.setReadOnly(true);
			} else {
				tfPlanNo.setReadOnly(false);
			}
		}
		catch (Exception e) {
		}
		btnsaveWrkOdrPlDtl.setCaption("Add");
		tblWrkOrdPlnDtl.setVisible(true);
		tblWOPlanMaterials.setVisible(true);
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
		tfPlanNo.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfPlanNo.setComponentError(null);
				String workOrdNo = tfPlanNo.getValue().toString();
				if (workOrdNo == null || workOrdNo == "") {
					tfPlanNo.setComponentError(new UserError(GERPErrorCodes.NULL_WO_PLN_NO));
				} else {
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
		tblWOPlanMaterials.setVisible(true);
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
		if ((cbWorkOrderNo.getValue() == null)) {
			cbWorkOrderNo.setComponentError(new UserError(GERPErrorCodes.NULL_WORK_ORDER_NO));
			errorFlag = true;
		} else {
			cbWorkOrderNo.setComponentError(null);
		}
		if (dfPlanDate.getValue() == null) {
			dfPlanDate.setComponentError(new UserError(GERPErrorCodes.NULL_PLN_DT));
			errorFlag = true;
		} else {
			dfPlanDate.setComponentError(null);
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
			}
			logger.info("getSequenceNumber(companyid, branchID, moduleId, MF_PLANNO )  >....... " + companyid + ","
					+ branchId + "," + moduleId);
			wrkOrdPlnHdr.setWrkPlanNo(tfPlanNo.getValue());
			wrkOrdPlnHdr.setCompanyId(companyid);
			wrkOrdPlnHdr.setBranchId((Long) cbBranchName.getValue());
			wrkOrdPlnHdr.setPlanDt(dfPlanDate.getValue());
			wrkOrdPlnHdr.setWrkOrderNoID(((WorkOrderHdrDM) cbWorkOrderNo.getValue()).getWorkOrdrId());
			wrkOrdPlnHdr.setPlanRemarks(taPlanRemrks.getValue());
			wrkOrdPlnHdr.setPrepareBy(employeeId);
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
			Collection<WorkOrderPlanMtrlDtlDM> itemIds1 = (Collection<WorkOrderPlanMtrlDtlDM>) tblWOPlanMaterials
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
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "MF_PLANNO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "MF_PLANNO");
					}
				}
				catch (Exception e) {
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
		tfPlanNo.setReadOnly(false);
		tblMstScrSrchRslt.setVisible(true);
		tblWrkOrdPlnDtl.setVisible(false);
		tblWOPlanMaterials.setVisible(false);
		tblWOPlanMaterials.removeAllItems();
		wrkOdrPlnDtlresetFields();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		cbBranchName.setValue(branchId);
		cbBranchName.setRequired(false);
		cbBranchName.setComponentError(null);
		tfPlanNo.setReadOnly(false);
		tfPlanNo.setValue("");
		tfPlanNo.setRequired(false);
		tfPlanNo.setComponentError(null);
		cbWorkOrderNo.setComponentError(null);
		cbWorkOrderNo.setValue(null);
		cbProductName.setValue(null);
		tfProductName.setReadOnly(false);
		tfProductName.setValue("");
		dfPlanDate.setValue(null);
		dfPlanDate.setComponentError(null);
		taPlanRemrks.setValue("");
		cbPlanStatus.setValue(null);
		cbPlanStatus.setComponentError(null);
		cbPlanStatus.setRequired(false);
		workOdrPlnPrdDtlList = new ArrayList<WorkOrderPlanProdDtlDM>();
		workOdrPlnMtrlDtlList = new ArrayList<WorkOrderPlanMtrlDtlDM>();
		tblWOPlanMaterials.removeAllItems();
		tblWrkOrdPlnDtl.removeAllItems();
	}
	
	private boolean validateWOPrddtl() {
		boolean isValid = true;
		if (cbProductName.getValue() == null) {
			cbProductName.setComponentError(new UserError(GERPErrorCodes.NULL_QC_PRDNAME));
			isValid = false;
		} else {
			cbProductName.setComponentError(null);
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
		wrkOrdplnPrdDtl.setProductId(((WorkOrderDtlDM) cbProductName.getValue()).getProdId());
		wrkOrdplnPrdDtl.setProductName(tfProductName.getValue());
		wrkOrdplnPrdDtl.setProdName(((WorkOrderDtlDM) cbProductName.getValue()).getProdName());
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
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}