/**
 * File Name 		: AssemblyPlan.java 
 * Description 		: this class is used for add/edit AssemblyPlan  details. 
 * Author 			: Madhu T
 * Date 			: Oct-11-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version     	  Date           	Modified By               Remarks
 * 0.1          Oct-11-2014      	Madhu T	        Initial Version
 **/
package com.gnts.mfg.stt.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTable;
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
import com.gnts.mfg.service.txn.WorkOrderDtlService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.stt.mfg.domain.txn.AsmblyPlanDtlDM;
import com.gnts.stt.mfg.domain.txn.AsmblyPlanHdrDM;
import com.gnts.stt.mfg.domain.txn.AsmblyPlanShiftDM;
import com.gnts.stt.mfg.service.txn.AsmblyPlanDtlService;
import com.gnts.stt.mfg.service.txn.AsmblyPlanHdrService;
import com.gnts.stt.mfg.service.txn.AsmblyPlanShiftService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
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

public class AssemblyPlan extends BaseTransUI {
	// Bean Creation
	private AsmblyPlanHdrService serviceAsmblyPlanHrd = (AsmblyPlanHdrService) SpringContextHelper
			.getBean("AsmblyPlanHdr");
	private AsmblyPlanDtlService serviceAsmblyPlanDtl = (AsmblyPlanDtlService) SpringContextHelper
			.getBean("AsmblyPlanDtl");
	private AsmblyPlanShiftService serviceAsmblyPlanShift = (AsmblyPlanShiftService) SpringContextHelper
			.getBean("AsmblyPlanShift");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ClientService serviceClient = (ClientService) SpringContextHelper.getBean("clients");
	private WorkOrderHdrService serviceWorkOrderHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private WorkOrderDtlService serviceWorkOrderDtl = (WorkOrderDtlService) SpringContextHelper.getBean("workOrderDtl");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private List<AsmblyPlanDtlDM> asmblPlnDtlList = null;
	private List<AsmblyPlanShiftDM> asmblyPlnShitftList = null;
	// form layout for input controls
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4, flDtlCol1, flDtlCol2, flDtlCol3, flDtlCol4,
			flDtlCol5, flAsmShiftCol1, flAsmShiftCol2, flAsmShiftCol3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlHdrslap = new HorizontalLayout();
	private HorizontalLayout hlShift = new HorizontalLayout();
	private HorizontalLayout hlHdrAndShift = new HorizontalLayout();
	private VerticalLayout vlHdr, vlHrdAndDtlAndShift, vlShift;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Work Order Details
	private Button btnAddDtls = new GERPButton("Add", "add", this);
	private Button btnAddShift = new GERPButton("Add", "add", this);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private Button btnShiftdelete = new GERPButton("Delete", "delete", this);
	private ComboBox cbBranch, cbStatus, cbDtlStatus, cbEmpName, cbWorkOrder, cbProduct, cbClientId;
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private TextField tfPlanRefNo, tfPlanHdrQty, tfShiftName, tfTargetQty, tfPlanDtlQty, tfPlnRefNo;
	private DateField dfAsmPlanDt;
	private TextArea taRemark;
	private Table tblAsmbPlanDtl, tblShift;
	private BeanItemContainer<AsmblyPlanHdrDM> beanAsmblyPlanHdrDM = null;
	private BeanItemContainer<AsmblyPlanDtlDM> beanAsmblyPlanDtlDM = null;
	private BeanItemContainer<AsmblyPlanShiftDM> beanAsmblyPlanShiftDM = null;
	// local variables declaration
	private Long companyid, moduleId, branchID;
	private String asmbPlnHdrId;
	private int recordCnt = 0;
	private int recordShiftCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(AssemblyPlan.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor received the parameters from Login UI class
	public AssemblyPlan() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside AssemblyPlan() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		// Initialization for work order Details user input components
		btnAddDtls.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDtlDetails()) {
					saveasmblPlnDtlListDetails();
				}
			}
		});
		tfPlnRefNo = new TextField("Plan Ref.No");
		btnAddShift.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateShiftDetails()) {
					saveasmblPlnShiftListDetails();
				}
			}
		});
		tblAsmbPlanDtl = new GERPTable();
		tblAsmbPlanDtl.setPageLength(5);
		tblAsmbPlanDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblAsmbPlanDtl.isSelected(event.getItemId())) {
					tblAsmbPlanDtl.setImmediate(true);
					btnAddDtls.setCaption("Add");
					btnAddDtls.setStyleName("savebt");
					asmblDtlResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtls.setCaption("Update");
					btnAddDtls.setStyleName("savebt");
					editAsmbPlanDtls();
				}
			}
		});
		tblShift = new Table();
		tblShift.setPageLength(5);
		tblShift.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblShift.isSelected(event.getItemId())) {
					tblShift.setImmediate(true);
					btnAddShift.setCaption("Add");
					btnAddShift.setStyleName("savebt");
					asmblShiftResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddShift.setCaption("Update");
					btnAddShift.setStyleName("savebt");
					editAsmbPlanShift();
				}
			}
		});
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
				tfPlanRefNo.setReadOnly(false);
			}
		});
		btnShiftdelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnShiftdelete == event.getButton()) {
					deleteShiftDetails();
					btnAddShift.setCaption("Add");
				}
			}
		});
		btndelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndelete == event.getButton()) {
					deleteDetails();
					btnAdd.setCaption("Add");
				}
			}
		});
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting AssemblyPlan UI");
		// Plan Ref.No text field
		tfPlanRefNo = new GERPTextField("Plan Ref.No");
		cbHdrStatus.setWidth("150px");
		// Branch Combo Box
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		// Assembly Plan Datefield
		dfAsmPlanDt = new PopupDateField("Plan Date");
		dfAsmPlanDt.setWidth("130px");
		// Plan Hdr Qty.Text field
		tfPlanHdrQty = new GERPTextField("Planned Qty");
		tfPlanHdrQty.setValue("0");
		// Remarks TextArea
		taRemark = new TextArea("Remarks");
		taRemark.setHeight("80px");
		taRemark.setWidth("150px");
		// Status ComboBox
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbDtlStatus.setWidth("100");
		// Shift Name TextField
		tfShiftName = new GERPTextField("Shift Name");
		// Employee Name combobox
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setItemCaptionPropertyId("fullname");
		loadEmployeeList();
		// TargetQty TextField
		tfTargetQty = new GERPTextField("Target Qty");
		tfTargetQty.setWidth("110");
		tfTargetQty.setValue("0");
		// Client Id ComboBox
		cbWorkOrder = new GERPComboBox("WO No.");
		cbWorkOrder.setItemCaptionPropertyId("workOrdrNo");
		cbClientId = new GERPComboBox("Client Name");
		cbClientId.setItemCaptionPropertyId("clientName");
		loadClientList();
		cbClientId.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbClientId.getItem(itemId);
				if (item != null) {
					loadWorkOrderNo();
				}
			}
		});
		// WOId ComboBox
		cbWorkOrder.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbWorkOrder.getItem(itemId);
				if (item != null) {
					loadProductList();
				}
			}
		});
		// Product Name ComboBox
		cbProduct = new GERPComboBox("Prod.Name");
		cbProduct.setWidth("130");
		cbProduct.setItemCaptionPropertyId("prodName");
		// Plan Qty. Textfield
		tfPlanDtlQty = new GERPTextField("Plan Qty.");
		tfPlanDtlQty.setValue("0");
		tfPlanDtlQty.setWidth("75px");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadShiftRslt();
		loadAsmbDtlList();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		tfPlanRefNo.setReadOnly(false);
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		Label lbl = new Label();
		flHdrCol4.addComponent(tfPlnRefNo);
		flHdrCol1.addComponent(dfAsmPlanDt);
		flHdrCol2.addComponent(lbl);
		flHdrCol3.addComponent(cbHdrStatus);
		hlSearchLayout.addComponent(flHdrCol4);
		hlSearchLayout.addComponent(flHdrCol1);
		hlSearchLayout.addComponent(flHdrCol2);
		hlSearchLayout.addComponent(flHdrCol3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol1.addComponent(cbBranch);
		flHdrCol1.addComponent(tfPlanRefNo);
		flHdrCol1.addComponent(dfAsmPlanDt);
		flHdrCol1.addComponent(tfPlanHdrQty);
		flHdrCol1.addComponent(taRemark);
		flHdrCol1.addComponent(cbHdrStatus);
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdrCol1);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding Shift Components
		flAsmShiftCol1 = new FormLayout();
		flAsmShiftCol2 = new FormLayout();
		flAsmShiftCol3 = new FormLayout();
		flAsmShiftCol1.addComponent(tfShiftName);
		flAsmShiftCol1.addComponent(cbEmpName);
		flAsmShiftCol2.addComponent(tfTargetQty);
		flAsmShiftCol2.addComponent(cbStatus);
		flAsmShiftCol3.addComponent(btnAddShift);
		flAsmShiftCol3.addComponent(btnShiftdelete);
		flAsmShiftCol3.setComponentAlignment(btnAddShift, Alignment.BOTTOM_CENTER);
		vlShift = new VerticalLayout();
		hlShift = new HorizontalLayout();
		hlShift.addComponent(flAsmShiftCol1);
		hlShift.addComponent(flAsmShiftCol2);
		hlShift.addComponent(flAsmShiftCol3);
		hlShift.setMargin(true);
		hlShift.setSpacing(true);
		vlShift.addComponent(hlShift);
		vlShift.addComponent(tblShift);
		vlShift.setWidth("915px");
		// Adding AssemblyPlanSlap components
		// Add components for User Input Layout
		flDtlCol1 = new FormLayout();
		flDtlCol2 = new FormLayout();
		flDtlCol3 = new FormLayout();
		flDtlCol4 = new FormLayout();
		flDtlCol5 = new FormLayout();
		flDtlCol1.addComponent(cbClientId);
		flDtlCol2.addComponent(cbWorkOrder);
		flDtlCol3.addComponent(cbProduct);
		flDtlCol4.addComponent(tfPlanDtlQty);
		flDtlCol5.addComponent(cbDtlStatus);
		hlHdrslap = new HorizontalLayout();
		hlHdrslap.addComponent(flDtlCol1);
		hlHdrslap.addComponent(flDtlCol2);
		hlHdrslap.addComponent(flDtlCol3);
		hlHdrslap.addComponent(flDtlCol4);
		hlHdrslap.addComponent(flDtlCol5);
		hlHdrslap.addComponent(btnAddDtls);
		hlHdrslap.setComponentAlignment(btnAddDtls, Alignment.MIDDLE_LEFT);
		hlHdrslap.addComponent(btndelete);
		hlHdrslap.setComponentAlignment(btndelete, Alignment.MIDDLE_LEFT);
		hlHdrslap.setSpacing(true);
		hlHdrslap.setMargin(true);
		vlHdr = new VerticalLayout();
		vlHdr.addComponent(hlHdrslap);
		vlHdr.addComponent(tblAsmbPlanDtl);
		hlHdrAndShift = new HorizontalLayout();
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(hlHdr));
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(vlShift));
		vlHrdAndDtlAndShift = new VerticalLayout();
		vlHrdAndDtlAndShift.addComponent(hlHdrAndShift);
		vlHrdAndDtlAndShift.addComponent(GERPPanelGenerator.createPanel(vlHdr));
		vlHrdAndDtlAndShift.setSpacing(true);
		vlHrdAndDtlAndShift.setWidth("100%");
		hlUserInputLayout.addComponent(vlHrdAndDtlAndShift);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setHeight("65%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<AsmblyPlanHdrDM> assemblyPlanList = new ArrayList<AsmblyPlanHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfPlanRefNo.getValue() + ", " + cbHdrStatus.getValue());
		assemblyPlanList = serviceAsmblyPlanHrd.getAsmblyPlanHdrDetails(null, companyid, null,
				(String) tfPlnRefNo.getValue(), dfAsmPlanDt.getValue(), (String) cbHdrStatus.getValue(), "F");
		recordCnt = assemblyPlanList.size();
		beanAsmblyPlanHdrDM = new BeanItemContainer<AsmblyPlanHdrDM>(AsmblyPlanHdrDM.class);
		beanAsmblyPlanHdrDM.addAll(assemblyPlanList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the AssemblyPlan. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanAsmblyPlanHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "asmplnid", "asmplnreffno", "asmplndate", "asmplnstatus",
				"lastupdateddate", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Plan Ref.No", "Plan Date", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("asmplnid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadAsmbDtlList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		logger.info("Company ID : " + companyid + " | saveasmblPlnDtlListDetails User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + cbClientId.getValue() + ", " + tfPlanHdrQty.getValue()
				+ (String) cbStatus.getValue() + ", " + asmbPlnHdrId);
		tblAsmbPlanDtl.removeAllItems();
		recordCnt = asmblPlnDtlList.size();
		beanAsmblyPlanDtlDM = new BeanItemContainer<AsmblyPlanDtlDM>(AsmblyPlanDtlDM.class);
		beanAsmblyPlanDtlDM.addAll(asmblPlnDtlList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the AssemblyPlanslap. result set");
		tblAsmbPlanDtl.setContainerDataSource(beanAsmblyPlanDtlDM);
		tblAsmbPlanDtl.setVisibleColumns(new Object[] { "clientName", "woNo", "prodName", "plndQty", "status",
				"lastupdateddate", "lastupdatedby" });
		tblAsmbPlanDtl.setColumnHeaders(new String[] { "Client Name", "WO No.", "Prod.Name", "Plan Qty.", "Status",
				"Last Updated Date", "Last Updated By" });
		tblAsmbPlanDtl.setColumnAlignment("asmPlnDtlId", Align.RIGHT);
		tblAsmbPlanDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadShiftRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		recordShiftCnt = asmblyPlnShitftList.size();
		beanAsmblyPlanShiftDM = new BeanItemContainer<AsmblyPlanShiftDM>(AsmblyPlanShiftDM.class);
		beanAsmblyPlanShiftDM.addAll(asmblyPlnShitftList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the AssemblyPlan. result set");
		tblShift.setContainerDataSource(beanAsmblyPlanShiftDM);
		tblShift.setVisibleColumns(new Object[] { "shiftName", "empName", "targetQty", "status", "lastupdateddate",
				"lastupdatedby" });
		tblShift.setColumnHeaders(new String[] { "Shift Name", "Employee Name", "Target Qty", "Status",
				"Last Updated Dt", "Last Updated By" });
		tblShift.setColumnAlignment("asmPlnShiftId", Align.RIGHT);
		tblShift.setColumnFooter("lastupdatedby", "No.of Records : " + recordShiftCnt);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		// Assembly Plan Hdr Resetfields
		// cbBranch.setValue(null);
		cbBranch.setValue(cbBranch.getItemIds().iterator().next());
		tfPlanRefNo.setReadOnly(false);
		tfPlnRefNo.setValue("");
		tfPlanRefNo.setValue("");
		tfPlanRefNo.setReadOnly(true);
		tfPlanRefNo.setComponentError(null);
		dfAsmPlanDt.setValue(null);
		tfPlanHdrQty.setValue("0");
		taRemark.setValue("");
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		asmblPlnDtlList = new ArrayList<AsmblyPlanDtlDM>();
		asmblyPlnShitftList = new ArrayList<AsmblyPlanShiftDM>();
		tblAsmbPlanDtl.removeAllItems();
		tblShift.removeAllItems();
		recordShiftCnt = 0;
		recordCnt = 0;
	}
	
	// Method to edit the values from table into fields to update process
	private void editAssemblyPlanHdrDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			AsmblyPlanHdrDM editAssemblyPlan = beanAsmblyPlanHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			asmbPlnHdrId = editAssemblyPlan.getAsmplnid().toString();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected AssemblyPlan. Id -> " + asmbPlnHdrId);
			cbBranch.setValue(editAssemblyPlan.getBranchid());
			tfPlanRefNo.setReadOnly(false);
			tfPlanRefNo.setValue(editAssemblyPlan.getAsmplnreffno());
			tfPlanRefNo.setReadOnly(true);
			if (editAssemblyPlan.getAsmplndate() != null) {
				dfAsmPlanDt.setValue(editAssemblyPlan.getAsmplndate1());
			}
			tfPlanHdrQty.setValue(editAssemblyPlan.getPlannedqty().toString());
			if (editAssemblyPlan.getRemarks() != null) {
				taRemark.setValue(editAssemblyPlan.getRemarks());
			}
			cbHdrStatus.setValue(editAssemblyPlan.getAsmplnstatus());
			asmblPlnDtlList.addAll(serviceAsmblyPlanDtl.getAsmPlnDtlList(null, Long.valueOf(asmbPlnHdrId), null, null,
					null, (String) cbStatus.getValue(), "F"));
			asmblyPlnShitftList.addAll(serviceAsmblyPlanShift.getAsmblyPlanShiftDtls(null, Long.valueOf(asmbPlnHdrId),
					null, null, (String) cbStatus.getValue(), "F"));
			loadAsmbDtlList();
			loadShiftRslt();
		}
	}
	
	private void editAsmbPlanDtls() {
		hlUserInputLayout.setVisible(true);
		if (tblAsmbPlanDtl.getValue() != null) {
			AsmblyPlanDtlDM asmblyPlanDtlDM = new AsmblyPlanDtlDM();
			asmblyPlanDtlDM = beanAsmblyPlanDtlDM.getItem(tblAsmbPlanDtl.getValue()).getBean();
			Long clientId = asmblyPlanDtlDM.getClientId();
			Collection<?> clientIdCol = cbClientId.getItemIds();
			for (Iterator<?> iteratorclient = clientIdCol.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbClientId.getItem(itemIdClient);
				// Get the actual bean and use the data
				ClientDM clientObj = (ClientDM) itemclient.getBean();
				if (clientId != null && clientId.equals(clientObj.getClientId())) {
					cbClientId.setValue(itemIdClient);
				}
			}
			Long woId = asmblyPlanDtlDM.getWoId();
			Collection<?> woIdCol = cbWorkOrder.getItemIds();
			for (Iterator<?> iteratorWO = woIdCol.iterator(); iteratorWO.hasNext();) {
				Object itemIdWOObj = (Object) iteratorWO.next();
				BeanItem<?> itemWoBean = (BeanItem<?>) cbWorkOrder.getItem(itemIdWOObj);
				// Get the actual bean and use the data
				WorkOrderHdrDM workOrderDM = (WorkOrderHdrDM) itemWoBean.getBean();
				if (woId != null && woId.equals(workOrderDM.getWorkOrdrId())) {
					cbWorkOrder.setValue(itemIdWOObj);
				}
			}
			Long prodId = asmblyPlanDtlDM.getProductId();
			Collection<?> prodIdCol = cbProduct.getItemIds();
			for (Iterator<?> iterator = prodIdCol.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
				// Get the actual bean and use the data
				WorkOrderDtlDM st = (WorkOrderDtlDM) item.getBean();
				if (prodId != null && prodId.equals(st.getProdId())) {
					cbProduct.setValue(itemId);
				}
			}
			if (asmblyPlanDtlDM.getPlndQty() != null) {
				tfPlanDtlQty.setValue(asmblyPlanDtlDM.getPlndQty().toString());
			}
			if (asmblyPlanDtlDM.getStatus() != null) {
				cbDtlStatus.setValue(asmblyPlanDtlDM.getStatus());
			}
		}
	}
	
	private void editAsmbPlanShift() {
		hlUserInputLayout.setVisible(true);
		if (tblShift.getValue() != null) {
			AsmblyPlanShiftDM asmblyPlanShiftDM = new AsmblyPlanShiftDM();
			asmblyPlanShiftDM = beanAsmblyPlanShiftDM.getItem(tblShift.getValue()).getBean();
			Long empId = asmblyPlanShiftDM.getEmpId();
			Collection<?> empColId = cbEmpName.getItemIds();
			for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbEmpName.getItem(itemIdClient);
				// Get the actual bean and use the data
				EmployeeDM empObj = (EmployeeDM) itemclient.getBean();
				if (empId != null && empId.equals(empObj.getEmployeeid())) {
					cbEmpName.setValue(itemIdClient);
				}
			}
			if (asmblyPlanShiftDM.getShiftName() != null) {
				tfShiftName.setValue(asmblyPlanShiftDM.getShiftName());
			}
			if (asmblyPlanShiftDM.getTargetQty() != null) {
				tfTargetQty.setValue(asmblyPlanShiftDM.getTargetQty().toString());
			}
			if (asmblyPlanShiftDM.getStatus() != null) {
				cbStatus.setValue(asmblyPlanShiftDM.getStatus());
			}
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
			assembleSearchLayout();
		}
	}
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		dfAsmPlanDt.setValue(null);
		tfPlnRefNo.setValue("");
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		resetFields();
		loadShiftRslt();
		loadAsmbDtlList();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		cbBranch.setRequired(true);
		dfAsmPlanDt.setRequired(true);
		tfPlanHdrQty.setRequired(true);
		tfShiftName.setRequired(true);
		tfTargetQty.setRequired(true);
		cbEmpName.setRequired(true);
		cbWorkOrder.setRequired(true);
		cbProduct.setRequired(true);
		tfPlanDtlQty.setRequired(true);
		cbClientId.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblAsmbPlanDtl.setVisible(true);
		tfPlanRefNo.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STMF_APLNO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfPlanRefNo.setValue(slnoObj.getKeyDesc());
				tfPlanRefNo.setReadOnly(true);
			} else {
				tfPlanRefNo.setReadOnly(false);
			}
		}
		catch (Exception e) {
		}
		tblAsmbPlanDtl.setVisible(true);
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for AssemblyPlan. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_MFG_STT_ASMBLYPLAN);
		UI.getCurrent().getSession().setAttribute("audittablepk", asmbPlnHdrId);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		cbBranch.setComponentError(null);
		dfAsmPlanDt.setComponentError(null);
		tfPlanHdrQty.setComponentError(null);
		tfPlanRefNo.setComponentError(null);
		tfShiftName.setComponentError(null);
		tfTargetQty.setComponentError(null);
		cbEmpName.setComponentError(null);
		cbClientId.setComponentError(null);
		cbWorkOrder.setComponentError(null);
		cbProduct.setComponentError(null);
		tfPlanDtlQty.setComponentError(null);
		cbBranch.setRequired(false);
		dfAsmPlanDt.setRequired(false);
		tfPlanHdrQty.setRequired(false);
		tfShiftName.setRequired(false);
		cbEmpName.setRequired(false);
		cbClientId.setRequired(false);
		cbWorkOrder.setRequired(false);
		cbProduct.setRequired(false);
		tfPlanDtlQty.setRequired(false);
		asmblDtlResetFields();
		asmblShiftResetFields();
		hlCmdBtnLayout.setVisible(true);
		tblAsmbPlanDtl.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		cbBranch.setRequired(true);
		dfAsmPlanDt.setRequired(true);
		tfShiftName.setRequired(true);
		cbEmpName.setRequired(true);
		cbWorkOrder.setRequired(true);
		cbProduct.setRequired(true);
		tfPlanDtlQty.setRequired(true);
		cbClientId.setRequired(true);
		tblMstScrSrchRslt.setVisible(false);
		// reset the input controls to default value
		assembleInputUserLayout();
		resetFields();
		editAssemblyPlanHdrDetails();
		editAsmbPlanDtls();
		editAsmbPlanShift();
	}
	
	private void asmblDtlResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbClientId.setValue(null);
		cbWorkOrder.setValue(null);
		cbProduct.setValue(null);
		tfPlanDtlQty.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbClientId.setComponentError(null);
		cbWorkOrder.setComponentError(null);
		cbProduct.setComponentError(null);
		tfPlanDtlQty.setComponentError(null);
	}
	
	private void asmblShiftResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEmpName.setValue(null);
		tfShiftName.setValue("");
		tfTargetQty.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfShiftName.setComponentError(null);
		cbEmpName.setComponentError(null);
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		dfAsmPlanDt.setComponentError(null);
		tfPlanHdrQty.setComponentError(null);
		tfShiftName.setComponentError(null);
		cbEmpName.setComponentError(null);
		cbClientId.setComponentError(null);
		cbWorkOrder.setComponentError(null);
		cbProduct.setComponentError(null);
		tfPlanDtlQty.setComponentError(null);
		Boolean errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbBranch.getValue());
			errorFlag = true;
		}
		Long planhdrqty;
		try {
			planhdrqty = Long.valueOf(tfPlanHdrQty.getValue());
			if (planhdrqty < 0) {
				tfPlanHdrQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorFlag = true;
			}
		}
		catch (Exception e) {
			tfPlanHdrQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			errorFlag = true;
		}
		if ((dfAsmPlanDt.getValue() == null)) {
			dfAsmPlanDt.setComponentError(new UserError(GERPErrorCodes.NULL_ASMBL_PLAN_DT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfAsmPlanDt.getValue());
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validateDtlDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbClientId.getValue() == null)) {
			cbClientId.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbClientId.getValue());
			isValid = false;
		} else {
			cbClientId.setComponentError(null);
		}
		Long plandtlqty;
		try {
			plandtlqty = Long.valueOf(tfPlanDtlQty.getValue());
			if (plandtlqty < 0) {
				tfPlanDtlQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfPlanDtlQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		if ((cbWorkOrder.getValue() == null)) {
			cbWorkOrder.setComponentError(new UserError(GERPErrorCodes.NULL_WO_PLN_NO));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbClientId.getValue());
			isValid = false;
		} else {
			cbWorkOrder.setComponentError(null);
		}
		if ((cbProduct.getValue() == null)) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbClientId.getValue());
			isValid = false;
		} else {
			cbProduct.setComponentError(null);
		}
		return isValid;
	}
	
	private boolean validateShiftDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((tfShiftName.getValue() == "")) {
			tfShiftName.setComponentError(new UserError(GERPErrorCodes.NULL_SHIFT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfShiftName.getValue());
			isValid = false;
		} else {
			tfShiftName.setComponentError(null);
		}
		Long targetQty;
		try {
			targetQty = Long.valueOf(tfTargetQty.getValue());
			if (targetQty < 0) {
				tfTargetQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfTargetQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		if ((cbEmpName.getValue() == null)) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbEmpName.getValue());
			isValid = false;
		} else {
			cbEmpName.setComponentError(null);
		}
		return isValid;
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		tfPlanHdrQty.setComponentError(null);
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			AsmblyPlanHdrDM asmblyPlanHdrDM = new AsmblyPlanHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				asmblyPlanHdrDM = beanAsmblyPlanHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			asmblyPlanHdrDM.setAsmplnreffno(tfPlanRefNo.getValue());
			asmblyPlanHdrDM.setBranchid((Long.valueOf(cbBranch.getValue().toString())));
			asmblyPlanHdrDM.setAsmplndate(dfAsmPlanDt.getValue());
			asmblyPlanHdrDM.setPlannedqty(Long.valueOf(tfPlanHdrQty.getValue()));
			asmblyPlanHdrDM.setRemarks(taRemark.getValue());
			asmblyPlanHdrDM.setAsmplnstatus((String) cbStatus.getValue());
			asmblyPlanHdrDM.setCompanyid(companyid);
			asmblyPlanHdrDM.setLastupdateddate(DateUtils.getcurrentdate());
			asmblyPlanHdrDM.setLastupdatedby(username);
			serviceAsmblyPlanHrd.saveAsmblyPlanHdr(asmblyPlanHdrDM);
			@SuppressWarnings("unchecked")
			Collection<AsmblyPlanDtlDM> colPlanDtls = ((Collection<AsmblyPlanDtlDM>) tblAsmbPlanDtl.getVisibleItemIds());
			for (AsmblyPlanDtlDM save : (Collection<AsmblyPlanDtlDM>) colPlanDtls) {
				save.setAsmPlnId(Long.valueOf(asmblyPlanHdrDM.getAsmplnid()));
				serviceAsmblyPlanDtl.saveAsmPlnDtl(save);
			}
			@SuppressWarnings("unchecked")
			Collection<AsmblyPlanShiftDM> colAsmbShift = ((Collection<AsmblyPlanShiftDM>) tblShift.getVisibleItemIds());
			for (AsmblyPlanShiftDM saveShift : (Collection<AsmblyPlanShiftDM>) colAsmbShift) {
				saveShift.setAsmplnid(Long.valueOf(asmblyPlanHdrDM.getAsmplnid()));
				serviceAsmblyPlanShift.saveAsmblyPlanShift(saveShift);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STMF_APLNO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "STMF_APLNO");
					}
				}
				catch (Exception e) {
				}
			}
			asmblDtlResetFields();
			asmblShiftResetFields();
			resetFields();
			loadSrchRslt();
			asmbPlnHdrId = null;
			loadAsmbDtlList();
			loadShiftRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveasmblPlnDtlListDetails() {
		tfPlanDtlQty.setComponentError(null);
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			AsmblyPlanDtlDM assemblyPlanDtlObj = new AsmblyPlanDtlDM();
			if (tblAsmbPlanDtl.getValue() != null) {
				assemblyPlanDtlObj = beanAsmblyPlanDtlDM.getItem(tblAsmbPlanDtl.getValue()).getBean();
				asmblPlnDtlList.remove(assemblyPlanDtlObj);
			}
			if (cbClientId.getValue() != null) {
				assemblyPlanDtlObj.setClientId(((ClientDM) cbClientId.getValue()).getClientId());
				assemblyPlanDtlObj.setClientName(((ClientDM) cbClientId.getValue()).getClientName());
			}
			if (cbWorkOrder.getValue() != null) {
				assemblyPlanDtlObj.setWoId(((WorkOrderHdrDM) cbWorkOrder.getValue()).getWorkOrdrId());
				assemblyPlanDtlObj.setWoNo(((WorkOrderHdrDM) cbWorkOrder.getValue()).getWorkOrdrNo());
			}
			assemblyPlanDtlObj.setPlndQty(Long.valueOf(tfPlanDtlQty.getValue()));
			if (cbProduct.getValue() != null) {
				assemblyPlanDtlObj.setProductId(((WorkOrderDtlDM) cbProduct.getValue()).getProdId());
				assemblyPlanDtlObj.setProdName(((WorkOrderDtlDM) cbProduct.getValue()).getProdName());
			}
			if (cbDtlStatus.getValue() != null) {
				assemblyPlanDtlObj.setStatus((String) cbDtlStatus.getValue());
			}
			assemblyPlanDtlObj.setLastupdateddate(DateUtils.getcurrentdate());
			assemblyPlanDtlObj.setLastupdatedby(username);
			asmblPlnDtlList.add(assemblyPlanDtlObj);
			loadAsmbDtlList();
			btnAddDtls.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		asmblDtlResetFields();
	}
	
	private void saveasmblPlnShiftListDetails() {
		tfTargetQty.setComponentError(null);
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			AsmblyPlanShiftDM assemblyPlanShiftObj = new AsmblyPlanShiftDM();
			if (tblShift.getValue() != null) {
				assemblyPlanShiftObj = beanAsmblyPlanShiftDM.getItem(tblShift.getValue()).getBean();
				asmblyPlnShitftList.remove(assemblyPlanShiftObj);
			}
			assemblyPlanShiftObj.setShiftName(tfShiftName.getValue());
			if (cbEmpName.getValue() != null) {
				assemblyPlanShiftObj.setEmpId(((EmployeeDM) cbEmpName.getValue()).getEmployeeid());
				assemblyPlanShiftObj.setEmpName(((EmployeeDM) cbEmpName.getValue()).getFirstname());
			}
			assemblyPlanShiftObj.setTargetQty(Long.valueOf(tfTargetQty.getValue()));
			if (cbStatus.getValue() != null) {
				assemblyPlanShiftObj.setStatus((String) cbStatus.getValue());
			}
			assemblyPlanShiftObj.setLastupdateddate(DateUtils.getcurrentdate());
			assemblyPlanShiftObj.setLastupdatedby(username);
			asmblyPlnShitftList.add(assemblyPlanShiftObj);
			loadShiftRslt();
			asmbPlnHdrId = null;
			btnAddShift.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		asmblShiftResetFields();
	}
	
	/*
	 * loadBranchList()-->this function is used for load the branch name
	 */
	private void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, BranchDM> beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranchDM.setBeanIdProperty("branchId");
		beanBranchDM.addAll(serviceBranch.getBranchList(branchID, null, null, "Active", companyid, "P"));
		cbBranch.setContainerDataSource(beanBranchDM);
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee name
	 */
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Employee Search...");
		BeanItemContainer<EmployeeDM> beanEmployeeDM = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", null, null, null, null, null,
				"P"));
		cbEmpName.setContainerDataSource(beanEmployeeDM);
	}
	
	/*
	 * loadClientList()-->this function is used for load the Client name
	 */
	private void loadClientList() {
		BeanItemContainer<ClientDM> beanClient = new BeanItemContainer<ClientDM>(ClientDM.class);
		beanClient.addAll(serviceClient.getClientDetails(companyid, null, null, null, null, null, null, null, "Active",
				"P"));
		cbClientId.setContainerDataSource(beanClient);
	}
	
	/*
	 * loadProductList()-->this function is used for load the product Name
	 */
	private void loadProductList() {
		Long workOrdHdrId = ((WorkOrderHdrDM) cbWorkOrder.getValue()).getWorkOrdrId();
		BeanItemContainer<WorkOrderDtlDM> beanPlnDtl = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
		beanPlnDtl.addAll(serviceWorkOrderDtl.getWorkOrderDtlList(null, workOrdHdrId, null, "F"));
		cbProduct.setContainerDataSource(beanPlnDtl);
	}
	
	/*
	 * loadWorkOrderNo()-->this function is used for load the workorderno
	 */
	private void loadWorkOrderNo() {
		Long clientId = (((ClientDM) cbClientId.getValue()).getClientId());
		BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		beanWrkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, clientId, null, null, null, "F",
				null, null,null,null,null));
		cbWorkOrder.setContainerDataSource(beanWrkOrdHdr);
	}
	
	private void deleteShiftDetails() {
		AsmblyPlanShiftDM removeShift = new AsmblyPlanShiftDM();
		if (tblShift.getValue() != null) {
			removeShift = beanAsmblyPlanShiftDM.getItem(tblShift.getValue()).getBean();
			asmblyPlnShitftList.remove(removeShift);
			asmblShiftResetFields();
			loadShiftRslt();
		}
	}
	
	private void deleteDetails() {
		AsmblyPlanDtlDM removeShift = new AsmblyPlanDtlDM();
		if (tblAsmbPlanDtl.getValue() != null) {
			removeShift = beanAsmblyPlanDtlDM.getItem(tblAsmbPlanDtl.getValue()).getBean();
			asmblPlnDtlList.remove(removeShift);
			asmblDtlResetFields();
			loadAsmbDtlList();
		}
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
