/**
 * File Name 		: RotoPlan.java 
 * Description 		: this class is used for add/edit RotoPlan details. 
 * Author 			: Arun Jeyaraj R
 * Date 			:  Oct 13 2014 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          Oct 13 2014        	Arun Jeyaraj R			        Initial Version
 * 
 */
package com.gnts.mfg.stt.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.txn.WorkOrderDtlDM;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.service.txn.WorkOrderDtlService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.stt.mfg.domain.txn.RotoPlanArmDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanHdrDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanShiftDM;
import com.gnts.stt.mfg.service.txn.RotoPlanArmService;
import com.gnts.stt.mfg.service.txn.RotoPlanDtlService;
import com.gnts.stt.mfg.service.txn.RotoPlanHdrService;
import com.gnts.stt.mfg.service.txn.RotoPlanShiftService;
import com.vaadin.data.Item;
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

public class RotoPlan extends BaseUI {
	private RotoPlanHdrService serviceRotoplanhdr = (RotoPlanHdrService) SpringContextHelper.getBean("rotoplanhdr");
	private RotoPlanDtlService serviceRotoplandtl = (RotoPlanDtlService) SpringContextHelper.getBean("rotoplandtl");
	private RotoPlanArmService serviceRotoplanarm = (RotoPlanArmService) SpringContextHelper.getBean("rotoplanarm");
	private RotoPlanShiftService serviceRotoplanshift = (RotoPlanShiftService) SpringContextHelper
			.getBean("rotoplanshift");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ClientService serviceClient = (ClientService) SpringContextHelper.getBean("clients");
	private WorkOrderHdrService serviceWorkOrderHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private WorkOrderDtlService serviceWorkOrderDtl = (WorkOrderDtlService) SpringContextHelper.getBean("workOrderDtl");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	List<RotoPlanDtlDM> RotoPlanDtlList = null;
	List<RotoPlanArmDM> RotoPlanArmList = null;
	List<RotoPlanShiftDM> RotoPlanShiftList = null;
	// form layout for input controls
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flArmCol1, flArmCol2, flArmCol3, flDtlCol1, flDtlCol2,
			flDtlCol3, flShiftCol1, flShiftCol2, flShiftCol3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlShift = new HorizontalLayout();
	private HorizontalLayout hlHdrslap = new HorizontalLayout();
	private HorizontalLayout hlArm = new HorizontalLayout();
	private VerticalLayout vl = new VerticalLayout();
	private VerticalLayout vlDtl = new VerticalLayout();
	private HorizontalLayout hlHdrAndShift = new HorizontalLayout();
	private VerticalLayout vlHrdAndDtlAndShift, vlShift;
	private HorizontalLayout hlarmAndDtl = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Work Order Details
	private Button btnAddDtls = new GERPButton("Add", "addbt", this);
	private Button btnAddShift = new GERPButton("Add", "addbt", this);
	private Button btnAddArm = new GERPButton("Add", "addbt", this);
	public Button btndelete = new GERPButton("Delete", "delete", this);
	public Button btnShiftdelete = new GERPButton("Delete", "delete", this);
	public Button btnArmDelete = new GERPButton("Delete", "delete", this);
	private ComboBox cbBranch, cbStatus, cbArmProd, cbDtlStatus, cbEmpName, cbWO, cbArmstatus, cbProd, cbClientId,
			cbarmwono;
	private ComboBox cbShftStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private TextField tfPlanRefNo, tfPlanHdrQty, tfShiftName, tfTargetQty, tfPlanDtlQty, tfarmno, tfnoofcycle;
	private DateField dfrotoPlanDt;
	private TextArea taRemark;
	private Table tblrotoplanDtl, tblShift, tblarm;
	private BeanContainer<Long, BranchDM> beanBranchDM = null;
	private BeanItemContainer<EmployeeDM> beanEmployeeDM = null;
	private BeanItemContainer<RotoPlanHdrDM> beanRotoPlanHdrDM = null;
	private BeanItemContainer<RotoPlanDtlDM> beanRotoPlandtlDM = null;
	private BeanItemContainer<RotoPlanArmDM> beanRotoPlanarmDM = null;
	private BeanItemContainer<RotoPlanShiftDM> beanRotoPlanShiftDM = null;
	// local variables declaration
	private Long companyid, moduleId, branchID;
	private String rotoplanId;
	private int recordCnt = 0;
	private int recordShiftCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(RotoPlan.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor received the parameters from Login UI class
	public RotoPlan() {
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
					saveRotoPlnDtlListDetails();
				}
			}
		});
		btnAddShift.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateShiftDetails()) {
					saveRotoPlnShiftListDetails();
				}
			}
		});
		btnAddArm.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validationArmDetails()) {
					saveRotoPlnArmListDetails();
				}
			}
		});
		tblrotoplanDtl = new GERPTable();
		tblrotoplanDtl.setWidth("588px");
		tblrotoplanDtl.setPageLength(5);
		tblrotoplanDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblrotoplanDtl.isSelected(event.getItemId())) {
					tblrotoplanDtl.setImmediate(true);
					btnAddDtls.setCaption("Add");
					btnAddDtls.setStyleName("savebt");
					RotoDtlResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtls.setCaption("Update");
					btnAddDtls.setStyleName("savebt");
					editRotoPlanDtls();
				}
			}
		});
		tblShift = new GERPTable();
		tblShift.setWidth("912px");
		tblShift.setPageLength(2);
		tblShift.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblShift.isSelected(event.getItemId())) {
					tblShift.setImmediate(true);
					btnAddShift.setCaption("Add");
					btnAddShift.setStyleName("savebt");
					RotoShiftResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddShift.setCaption("Update");
					btnAddShift.setStyleName("savebt");
					editRotoPlanShift();
				}
			}
		});
		tblarm = new GERPTable();
		tblarm.setWidth("588px");
		tblarm.setPageLength(5);
		tblarm.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblarm.isSelected(event.getItemId())) {
					tblarm.setImmediate(true);
					btnAddArm.setCaption("Add");
					btnAddArm.setStyleName("savebt");
					RotoArmResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddArm.setCaption("Update");
					btnAddArm.setStyleName("savebt");
					editRotoPlanArm();
				}
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
					btnAddDtls.setCaption("Add");
				}
			}
		});
		btnArmDelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnArmDelete == event.getButton()) {
					deleteArmDetails();
					btnAddArm.setCaption("Add");
				}
			}
		});
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting AssemblyPlan UI");
		// Plan Ref.No text field
		tfPlanRefNo = new GERPTextField("Plan Ref.No");
		tfPlanRefNo.setWidth("130px");
		tfarmno = new GERPTextField("Arm No");
		tfarmno.setWidth("120");
		tfarmno.setValue("0");
		tfnoofcycle = new GERPTextField("No Of Cycle");
		tfnoofcycle.setWidth("120");
		tfnoofcycle.setValue("0");
		// Branch Combo Box
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("130px");
		loadBranchList();
		// Assembly Plan Datefield
		dfrotoPlanDt = new PopupDateField("Plan Date");
		dfrotoPlanDt.setWidth("130px");
		// Plan Hdr Qty.Text field
		tfPlanHdrQty = new GERPTextField("Planned Qty");
		tfPlanHdrQty.setValue("0");
		tfPlanHdrQty.setWidth("130px");
		// Remarks TextArea
		taRemark = new TextArea("Remarks");
		taRemark.setHeight("75px");
		taRemark.setWidth("130px");
		// Status ComboBox
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Shift Name TextField
		tfShiftName = new GERPTextField("Shift Name");
		// Employee Name combobox
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setItemCaptionPropertyId("fullname");
		loadEmployeeList();
		// TargetQty TextField
		tfTargetQty = new GERPTextField("Target Qty");
		tfTargetQty.setValue("0");
		// Client Id ComboBox
		cbClientId = new GERPComboBox("Client Name");
		cbClientId.setItemCaptionPropertyId("clientName");
		cbClientId.setWidth("130");
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
		cbWO = new GERPComboBox("WO No.");
		cbWO.setItemCaptionPropertyId("workOrdrNo");
		cbWO.setWidth("130");
		cbWO.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbWO.getItem(itemId);
				if (item != null) {
					loadProductList();
				}
			}
		});
		// Product Name ComboBox
		cbProd = new GERPComboBox("Prod.Name");
		cbProd.setWidth("100px");
		cbProd.setItemCaptionPropertyId("prodName");
		// Arm Product Name ComboBox
		cbArmProd = new GERPComboBox("Prod.Name");
		cbArmProd.setWidth("130");
		cbArmProd.setItemCaptionPropertyId("prodName");
		loadProduct();
		// ARM WOID
		cbarmwono = new GERPComboBox("WO No.");
		cbarmwono.setItemCaptionPropertyId("workOrdrNo");
		cbarmwono.setWidth("130");
		loadarmWorkOrderNo();
		// Plan Qty. Textfield
		tfPlanDtlQty = new GERPTextField("Plan Qty.");
		tfPlanDtlQty.setValue("0");
		tfPlanDtlQty.setWidth("100px");
		// Status ComboBox
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setWidth("130px");
		cbArmstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadShiftRslt();
		btnAddArm.setStyleName("add");
		btnAddArm.setStyleName("add");
		loadRotoArmList();
		btnAddDtls.setStyleName("add");
		btnAddShift.setStyleName("add");
		loadRotoDtlList();
	}
	
	private void loadRotoDtlList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		recordCnt = RotoPlanDtlList.size();
		beanRotoPlandtlDM = new BeanItemContainer<RotoPlanDtlDM>(RotoPlanDtlDM.class);
		beanRotoPlandtlDM.addAll(RotoPlanDtlList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the AssemblyPlanslap. result set");
		tblrotoplanDtl.setContainerDataSource(beanRotoPlandtlDM);
		tblrotoplanDtl.setVisibleColumns(new Object[] { "clientname", "woNo", "productname", "plannedqty" });
		tblrotoplanDtl.setColumnHeaders(new String[] { "Client Name", "WO No.", "Product Name", "Planned Qty." });
		tblrotoplanDtl.setColumnAlignment("rotoplandtlId", Align.RIGHT);
		tblrotoplanDtl.setColumnFooter("plannedqty", "No.of Records : " + recordCnt);
	}
	
	private void loadRotoArmList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		recordCnt = RotoPlanArmList.size();
		beanRotoPlanarmDM = new BeanItemContainer<RotoPlanArmDM>(RotoPlanArmDM.class);
		beanRotoPlanarmDM.addAll(RotoPlanArmList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Rotoplan. result set");
		tblarm.setContainerDataSource(beanRotoPlanarmDM);
		tblarm.setVisibleColumns(new Object[] { "workOrdrNo", "prodname", "armNo", "noOfcycle" });
		tblarm.setColumnHeaders(new String[] { "WO No", "Product Name", "Arm No", "No Of Cycle" });
		tblarm.setColumnAlignment("rotoplanarmId", Align.RIGHT);
		tblarm.setColumnFooter("prodname", "No.of Records : " + recordCnt);
	}
	
	private void loadShiftRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(vlDtl));
		recordShiftCnt = RotoPlanShiftList.size();
		beanRotoPlanShiftDM = new BeanItemContainer<RotoPlanShiftDM>(RotoPlanShiftDM.class);
		beanRotoPlanShiftDM.addAll(RotoPlanShiftList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Rotoplan. result set");
		tblShift.setContainerDataSource(beanRotoPlanShiftDM);
		tblShift.setVisibleColumns(new Object[] { "shiftname", "empName", "targetqty" });
		tblShift.setColumnHeaders(new String[] { "Shift Name", "Employee Name", "Target Qty" });
		tblShift.setColumnAlignment("rotoplanshftId", Align.RIGHT);
		tblShift.setColumnFooter("targetqty", "No.of Records : " + recordShiftCnt);
		tblShift.setFooterVisible(true);
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
		Label lbl = new Label();
		flHdrCol1.addComponent(dfrotoPlanDt);
		flHdrCol2.addComponent(lbl);
		flHdrCol3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flHdrCol1);
		hlSearchLayout.addComponent(flHdrCol2);
		hlSearchLayout.addComponent(flHdrCol3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		vl.removeAllComponents();
		vlDtl.removeAllComponents();
		hlarmAndDtl.removeAllComponents();
		hlSearchLayout.removeAllComponents();
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol1.addComponent(cbBranch);
		flHdrCol1.addComponent(dfrotoPlanDt);
		flHdrCol1.addComponent(tfPlanRefNo);
		flHdrCol2.addComponent(tfPlanHdrQty);
		flHdrCol2.addComponent(taRemark);
		flHdrCol2.addComponent(cbStatus);
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdrCol1);
		hlHdr.addComponent(flHdrCol2);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding Arm Components
		flArmCol1 = new FormLayout();
		flArmCol2 = new FormLayout();
		flArmCol3 = new FormLayout();
		new FormLayout();
		new FormLayout();
		flArmCol1.addComponent(cbarmwono);
		flArmCol1.addComponent(cbArmProd);
		flArmCol1.addComponent(tfarmno);
		flArmCol2.addComponent(tfnoofcycle);
		flArmCol2.addComponent(cbArmstatus);
		flArmCol3.addComponent(btnAddArm);
		flArmCol3.addComponent(btnArmDelete);
		hlArm = new HorizontalLayout();
		hlArm.addComponent(flArmCol1);
		hlArm.addComponent(flArmCol1);
		hlArm.addComponent(flArmCol1);
		hlArm.addComponent(flArmCol2);
		hlArm.addComponent(flArmCol3);
		hlArm.setSpacing(true);
		hlArm.setMargin(true);
		// Adding Shift Components
		flShiftCol1 = new FormLayout();
		flShiftCol2 = new FormLayout();
		flShiftCol3 = new FormLayout();
		flShiftCol1.addComponent(tfShiftName);
		flShiftCol1.addComponent(cbEmpName);
		flShiftCol2.addComponent(tfTargetQty);
		flShiftCol2.addComponent(cbShftStatus);
		flShiftCol3.addComponent(btnAddShift);
		flShiftCol3.addComponent(btnShiftdelete);
		flShiftCol3.setComponentAlignment(btnAddShift, Alignment.BOTTOM_CENTER);
		vlShift = new VerticalLayout();
		hlShift = new HorizontalLayout();
		hlShift.addComponent(flShiftCol1);
		hlShift.addComponent(flShiftCol2);
		hlShift.addComponent(flShiftCol3);
		hlShift.setSpacing(true);
		vlShift.addComponent(hlShift);
		vlShift.addComponent(tblShift);
		vlShift.setMargin(true);
		// Add components for User Input Layout
		flDtlCol1 = new FormLayout();
		flDtlCol2 = new FormLayout();
		flDtlCol3 = new FormLayout();
		new FormLayout();
		new FormLayout();
		new FormLayout();
		flDtlCol1.addComponent(cbClientId);
		flDtlCol1.addComponent(cbWO);
		flDtlCol2.addComponent(cbProd);
		flDtlCol2.addComponent(tfPlanDtlQty);
		flDtlCol3.addComponent(cbDtlStatus);
		HorizontalLayout hlBtn = new HorizontalLayout();
		hlBtn.addComponent(btnAddDtls);
		hlBtn.addComponent(btndelete);
		flDtlCol3.addComponent(hlBtn);
		hlHdrslap = new HorizontalLayout();
		hlHdrslap.addComponent(flDtlCol1);
		hlHdrslap.addComponent(flDtlCol1);
		hlHdrslap.addComponent(flDtlCol2);
		hlHdrslap.addComponent(flDtlCol2);
		hlHdrslap.addComponent(flDtlCol3);
		hlHdrslap.addComponent(flDtlCol3);
		hlHdrslap.setSpacing(true);
		hlHdrslap.setMargin(true);
		vl.addComponent(hlArm);
		vl.addComponent(tblarm);
		vlDtl.addComponent(hlHdrslap);
		vlDtl.addComponent(tblrotoplanDtl);
		hlarmAndDtl.addComponent(GERPPanelGenerator.createPanel(vl));
		hlarmAndDtl.addComponent(GERPPanelGenerator.createPanel(vlShift));
		hlarmAndDtl.setSpacing(true);
		hlarmAndDtl.setHeight("100%");
		hlHdrAndShift = new HorizontalLayout();
		hlHdrAndShift.addComponent((hlHdr));
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(vlDtl));
		vlHrdAndDtlAndShift = new VerticalLayout();
		vlHrdAndDtlAndShift.addComponent(GERPPanelGenerator.createPanel(hlHdrAndShift));
		vlHrdAndDtlAndShift.addComponent(GERPPanelGenerator.createPanel(hlarmAndDtl));
		vlHrdAndDtlAndShift.setSpacing(true);
		vlHrdAndDtlAndShift.setWidth("100%");
		hlUserInputLayout.addComponent(vlHrdAndDtlAndShift);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<RotoPlanHdrDM> RtplnHdrList = new ArrayList<RotoPlanHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + dfrotoPlanDt.getValue());
		RtplnHdrList = serviceRotoplanhdr.getRotoPlanHdrDetails(null, companyid, dfrotoPlanDt.getValue(), cbStatus
				.getValue().toString());
		recordCnt = RtplnHdrList.size();
		beanRotoPlanHdrDM = new BeanItemContainer<RotoPlanHdrDM>(RotoPlanHdrDM.class);
		beanRotoPlanHdrDM.addAll(RtplnHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Roto plan. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanRotoPlanHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "rotoplanid", "rotoplanrefno", "rotoplandt",
				"rotoplanstatus", "lastupdateddate", "lastupdatedby", });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Plan Ref No", " Roto Plan Date", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("rotoplanid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		tblMstScrSrchRslt.setPageLength(13);
	}
	
	private void editRotoPlanHdrDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (sltedRcd != null) {
			RotoPlanHdrDM editRotoPlan = beanRotoPlanHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			rotoplanId = editRotoPlan.getRotoplanid();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected rotoplan. Id -> " + rotoplanId);
			cbBranch.setValue(editRotoPlan.getBranchid());
			tfPlanRefNo.setReadOnly(false);
			tfPlanRefNo.setValue((String) sltedRcd.getItemProperty("rotoplanrefno").getValue());
			tfPlanRefNo.setReadOnly(true);
			if (editRotoPlan.getRotoplandt() != null) {
				dfrotoPlanDt.setValue(editRotoPlan.getRotoplandt1());
			}
			tfPlanHdrQty.setValue(editRotoPlan.getPlannedqty().toString());
			if (editRotoPlan.getRemarks() != null) {
				taRemark.setValue(editRotoPlan.getRemarks());
			}
			cbStatus.setValue(editRotoPlan.getRotoplanstatus());
			RotoPlanDtlList.addAll(serviceRotoplandtl.getRotoPlanDtlList(null, Long.valueOf(rotoplanId), null, null,
					cbDtlStatus.getValue().toString()));
			RotoPlanShiftList.addAll(serviceRotoplanshift.getRotoPlanShiftList(null, Long.valueOf(rotoplanId), null,
					cbShftStatus.getValue().toString()));
			RotoPlanArmList.addAll(serviceRotoplanarm.getRotoPlanArmList(null, Long.valueOf(rotoplanId), null,
					cbArmstatus.getValue().toString()));
		}
		loadRotoDtlList();
		loadShiftRslt();
		loadRotoArmList();
	}
	
	private boolean validateDtlDetails() {
		tfPlanDtlQty.setComponentError(null);
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
		if ((cbWO.getValue() == null)) {
			cbWO.setComponentError(new UserError(GERPErrorCodes.NULL_WO_PLN_NO));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbClientId.getValue());
			isValid = false;
		} else {
			cbWO.setComponentError(null);
		}
		if ((cbProd.getValue() == null)) {
			cbProd.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbClientId.getValue());
			isValid = false;
		} else {
			cbProd.setComponentError(null);
		}
		Long plandtlQty;
		try {
			plandtlQty = Long.valueOf(tfPlanDtlQty.getValue());
			if (plandtlQty < 0) {
				tfPlanDtlQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfPlanDtlQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		return isValid;
	}
	
	private void RotoShiftResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEmpName.setValue(null);
		tfShiftName.setValue("");
		tfTargetQty.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfShiftName.setComponentError(null);
		cbEmpName.setComponentError(null);
	}
	
	private void editRotoPlanShift() {
		hlUserInputLayout.setVisible(true);
		Item itselect = tblShift.getItem(tblShift.getValue());
		if (itselect != null) {
			RotoPlanShiftDM editRtoDtlObj = new RotoPlanShiftDM();
			editRtoDtlObj = beanRotoPlanShiftDM.getItem(tblShift.getValue()).getBean();
			Long empId = editRtoDtlObj.getEmployeeid();
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
			if (itselect.getItemProperty("shiftname").getValue() != null) {
				tfShiftName.setValue(itselect.getItemProperty("shiftname").getValue().toString());
			}
			if (itselect.getItemProperty("targetqty").getValue() != null) {
				tfTargetQty.setValue(itselect.getItemProperty("targetqty").getValue().toString());
			}
			if (itselect.getItemProperty("shftstatus").getValue() != null) {
				cbStatus.setValue(itselect.getItemProperty("shftstatus").getValue().toString());
			}
		}
	}
	
	private void editRotoPlanArm() {
		hlUserInputLayout.setVisible(true);
		Item itselect = tblarm.getItem(tblarm.getValue());
		if (itselect != null) {
			RotoPlanArmDM editRotoArmObj = new RotoPlanArmDM();
			editRotoArmObj = beanRotoPlanarmDM.getItem(tblarm.getValue()).getBean();
			rotoplanId = String.valueOf(editRotoArmObj.getRotoplanId());
			Long uom = editRotoArmObj.getProductId();
			Collection<?> uomid = cbArmProd.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbArmProd.getItem(itemId);
				// Get the actual bean and use the data
				WorkOrderDtlDM st = (WorkOrderDtlDM) item.getBean();
				if (uom != null && uom.equals(st.getProdId())) {
					cbArmProd.setValue(itemId);
				}
			}
			Long woId = editRotoArmObj.getWoId();
			Collection<?> woIdCol = cbarmwono.getItemIds();
			for (Iterator<?> iteratorWO = woIdCol.iterator(); iteratorWO.hasNext();) {
				Object itemIdWOObj = (Object) iteratorWO.next();
				BeanItem<?> itemWoBean = (BeanItem<?>) cbarmwono.getItem(itemIdWOObj);
				// Get the actual bean and use the data
				WorkOrderHdrDM workOrderDM = (WorkOrderHdrDM) itemWoBean.getBean();
				if (woId != null && woId.equals(workOrderDM.getWorkOrdrId())) {
					cbarmwono.setValue(itemIdWOObj);
				}
			}
			/*
			 * if(itselect.getItemProperty("woId").getValue() !=null){
			 * cbarmwono.setValue(itselect.getItemProperty("woId").getValue().toString()); }
			 */
			if (itselect.getItemProperty("armNo").getValue() != null) {
				tfarmno.setValue(itselect.getItemProperty("armNo").getValue().toString());
			}
			if (itselect.getItemProperty("noOfcycle").getValue() != null) {
				tfnoofcycle.setValue(itselect.getItemProperty("noOfcycle").getValue().toString());
			}
			if (itselect.getItemProperty("rtarmStatus").getValue() != null) {
				cbArmstatus.setValue(itselect.getItemProperty("rtarmStatus").getValue().toString());
			}
		}
	}
	
	private void editRotoPlanDtls() {
		hlUserInputLayout.setVisible(true);
		Item itselect = tblrotoplanDtl.getItem(tblrotoplanDtl.getValue());
		if (itselect != null) {
			RotoPlanDtlDM editRotoDtlObj = new RotoPlanDtlDM();
			editRotoDtlObj = beanRotoPlandtlDM.getItem(tblrotoplanDtl.getValue()).getBean();
			Long clientId = editRotoDtlObj.getClientId();
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
			Long woId = editRotoDtlObj.getWoId();
			Collection<?> woIdCol = cbWO.getItemIds();
			for (Iterator<?> iteratorWO = woIdCol.iterator(); iteratorWO.hasNext();) {
				Object itemIdWOObj = (Object) iteratorWO.next();
				BeanItem<?> itemWoBean = (BeanItem<?>) cbWO.getItem(itemIdWOObj);
				// Get the actual bean and use the data
				WorkOrderHdrDM workOrderDM = (WorkOrderHdrDM) itemWoBean.getBean();
				if (woId != null && woId.equals(workOrderDM.getWorkOrdrId())) {
					cbWO.setValue(itemIdWOObj);
				}
			}
			Long prodId = editRotoDtlObj.getProductId();
			Collection<?> prodIdCol = cbProd.getItemIds();
			for (Iterator<?> iterator = prodIdCol.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProd.getItem(itemId);
				// Get the actual bean and use the data
				WorkOrderDtlDM st = (WorkOrderDtlDM) item.getBean();
				if (prodId != null && prodId.equals(st.getProdId())) {
					cbProd.setValue(itemId);
				}
			}
			if (itselect.getItemProperty("plannedqty").getValue() != null) {
				tfPlanDtlQty.setValue(itselect.getItemProperty("plannedqty").getValue().toString());
			}
			if (itselect.getItemProperty("rtoplndtlstatus").getValue() != null) {
				cbDtlStatus.setValue(itselect.getItemProperty("rtoplndtlstatus").getValue().toString());
			}
		}
	}
	
	private void RotoDtlResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbClientId.setValue(null);
		cbWO.setValue(null);
		cbProd.setValue(null);
		tfPlanDtlQty.setValue("0");
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		cbClientId.setComponentError(null);
		cbWO.setComponentError(null);
		cbProd.setComponentError(null);
	}
	
	private void RotoArmResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfarmno.setValue("0");
		tfnoofcycle.setValue("0");
		cbArmstatus.setValue(null);
		cbArmProd.setValue(null);
		cbarmwono.setValue(null);
		cbArmstatus.setValue(cbArmstatus.getItemIds().iterator().next());
	}
	
	private void saveRotoPlnShiftListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoPlanShiftDM RotoPlanShiftObj = new RotoPlanShiftDM();
			if (tblShift.getValue() != null) {
				RotoPlanShiftObj = beanRotoPlanShiftDM.getItem(tblShift.getValue()).getBean();
				RotoPlanShiftList.remove(RotoPlanShiftObj);
			}
			RotoPlanShiftObj.setShiftname(tfShiftName.getValue());
			if (cbEmpName.getValue() != null) {
				RotoPlanShiftObj.setEmployeeid(((EmployeeDM) cbEmpName.getValue()).getEmployeeid());
				RotoPlanShiftObj.setEmpName(((EmployeeDM) cbEmpName.getValue()).getFirstlastname());
			}
			RotoPlanShiftObj.setTargetqty(Long.valueOf(tfTargetQty.getValue()));
			if (cbStatus.getValue() != null) {
				RotoPlanShiftObj.setShftstatus((String) cbStatus.getValue());
			}
			RotoPlanShiftObj.setLastupdatedDt(DateUtils.getcurrentdate());
			RotoPlanShiftObj.setLastupdatedBy(username);
			RotoPlanShiftList.add(RotoPlanShiftObj);
			loadShiftRslt();
			btnAddShift.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		RotoShiftResetFields();
	}
	
	private void saveRotoPlnArmListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoPlanArmDM RotoPlanArmObj = new RotoPlanArmDM();
			if (tblarm.getValue() != null) {
				RotoPlanArmObj = beanRotoPlanarmDM.getItem(tblarm.getValue()).getBean();
				RotoPlanArmList.remove(RotoPlanArmObj);
			}
			RotoPlanArmObj.setArmNo(Long.valueOf(tfarmno.getValue()));
			RotoPlanArmObj.setNoOfcycle(Long.valueOf(tfnoofcycle.getValue()));
			/* RotoPlanArmObj.setWoId(Long.valueOf((cbarmwono.getValue().toString()))); */
			if (cbarmwono.getValue() != null) {
				RotoPlanArmObj.setWoId(((WorkOrderHdrDM) cbarmwono.getValue()).getWorkOrdrId());
				RotoPlanArmObj.setWorkOrdrNo(((WorkOrderHdrDM) cbarmwono.getValue()).getWorkOrdrNo());
			}
			RotoPlanArmObj.setProductId(((WorkOrderDtlDM) cbArmProd.getValue()).getProdId());
			RotoPlanArmObj.setProdname(((WorkOrderDtlDM) cbArmProd.getValue()).getProductName());
			if (cbArmstatus.getValue() != null) {
				RotoPlanArmObj.setRtarmStatus((String) cbArmstatus.getValue());
			}
			RotoPlanArmObj.setLastupdatedDt(DateUtils.getcurrentdate());
			RotoPlanArmObj.setLastupdatedBy(username);
			RotoPlanArmList.add(RotoPlanArmObj);
			loadRotoArmList();
			btnAddArm.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		RotoArmResetFields();
	}
	
	private void saveRotoPlnDtlListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoPlanDtlDM RotoPlanDtlObj = new RotoPlanDtlDM();
			if (tblrotoplanDtl.getValue() != null) {
				RotoPlanDtlObj = beanRotoPlandtlDM.getItem(tblrotoplanDtl.getValue()).getBean();
				RotoPlanDtlList.remove(RotoPlanDtlObj);
			}
			if (cbClientId.getValue() != null) {
				RotoPlanDtlObj.setClientId(((ClientDM) cbClientId.getValue()).getClientId());
				RotoPlanDtlObj.setClientname(((ClientDM) cbClientId.getValue()).getClientName());
			}
			if (cbWO.getValue() != null) {
				RotoPlanDtlObj.setWoId(((WorkOrderHdrDM) cbWO.getValue()).getWorkOrdrId());
				RotoPlanDtlObj.setWoNo(((WorkOrderHdrDM) cbWO.getValue()).getWorkOrdrNo());
			}
			RotoPlanDtlObj.setPlannedqty(Long.valueOf(tfPlanDtlQty.getValue()));
			if (cbProd.getValue() != null) {
				RotoPlanDtlObj.setProductId(((WorkOrderDtlDM) cbProd.getValue()).getProdId());
				RotoPlanDtlObj.setProductname(((WorkOrderDtlDM) cbProd.getValue()).getProdName());
			}
			if (cbDtlStatus.getValue() != null) {
				RotoPlanDtlObj.setRtoplndtlstatus((String) cbDtlStatus.getValue());
			}
			RotoPlanDtlObj.setLastupdatedDt(DateUtils.getcurrentdate());
			RotoPlanDtlObj.setLastupdatedBy(username);
			RotoPlanDtlList.add(RotoPlanDtlObj);
			loadRotoDtlList();
			btnAddDtls.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		RotoDtlResetFields();
	}
	
	private boolean validationArmDetails() {
		tfarmno.setComponentError(null);
		tfnoofcycle.setComponentError(null);
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbArmProd.getValue() == null)) {
			cbArmProd.setComponentError(new UserError(GERPErrorCodes.NULL_RTO_ARM));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbArmProd.getValue());
			isValid = false;
		} else {
			cbArmProd.setComponentError(null);
		}
		Long armNo;
		try {
			armNo = Long.valueOf(tfarmno.getValue());
			if (armNo < 0) {
				tfarmno.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfarmno.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		Long noCycle;
		try {
			noCycle = Long.valueOf(tfnoofcycle.getValue());
			if (noCycle < 0) {
				tfnoofcycle.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfnoofcycle.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		return isValid;
	}
	
	private boolean validateShiftDetails() {
		tfTargetQty.setComponentError(null);
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (tfShiftName.getValue() == "" || tfShiftName.getValue().trim().length() == 0) {
			tfShiftName.setComponentError(new UserError(GERPErrorCodes.NULL_SHIFT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfShiftName.getValue());
			isValid = false;
		} else {
			tfShiftName.setComponentError(null);
		}
		if ((cbEmpName.getValue() == null)) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbEmpName.getValue());
			isValid = false;
		} else {
			cbEmpName.setComponentError(null);
		}
		Long tagetQty;
		try {
			tagetQty = Long.valueOf(tfTargetQty.getValue());
			if (tagetQty < 0) {
				tfTargetQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfTargetQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		return isValid;
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		dfrotoPlanDt.setValue(null);
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		resetFields();
		cbBranch.setRequired(true);
		dfrotoPlanDt.setRequired(true);
		tfShiftName.setRequired(true);
		tfTargetQty.setRequired(true);
		cbEmpName.setRequired(true);
		cbClientId.setRequired(true);
		cbWO.setRequired(true);
		cbProd.setRequired(true);
		cbArmProd.setRequired(true);
		cbarmwono.setRequired(true);
		tfarmno.setRequired(true);
		tfnoofcycle.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtls.setCaption("Add");
		btnAddArm.setCaption("Add");
		tblrotoplanDtl.setVisible(true);
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTPLNNO");
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfPlanRefNo.setReadOnly(true);
			} else {
				tfPlanRefNo.setReadOnly(false);
			}
		}
		tblrotoplanDtl.setVisible(true);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		tblMstScrSrchRslt.setVisible(false);
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		cbBranch.setRequired(true);
		dfrotoPlanDt.setRequired(true);
		tfShiftName.setRequired(true);
		tfTargetQty.setRequired(true);
		cbEmpName.setRequired(true);
		cbWO.setRequired(true);
		cbProd.setRequired(true);
		cbClientId.setRequired(true);
		cbArmProd.setRequired(true);
		cbarmwono.setRequired(true);
		tfarmno.setRequired(true);
		tfnoofcycle.setRequired(true);
		// reset the input controls to default value
		assembleInputUserLayout();
		resetFields();
		editRotoPlanHdrDetails();
		editRotoPlanDtls();
		editRotoPlanShift();
		editRotoPlanArm();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		dfrotoPlanDt.setComponentError(null);
		tfShiftName.setComponentError(null);
		cbEmpName.setComponentError(null);
		cbClientId.setComponentError(null);
		cbWO.setComponentError(null);
		cbProd.setComponentError(null);
		cbArmProd.setComponentError(null);
		tfarmno.setComponentError(null);
		tfnoofcycle.setComponentError(null);
		errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbBranch.getValue());
			errorFlag = true;
		}
		if ((dfrotoPlanDt.getValue() == null)) {
			dfrotoPlanDt.setComponentError(new UserError(GERPErrorCodes.NULL_RTOPLN_HDR));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfrotoPlanDt.getValue());
			errorFlag = true;
		}
		Long prodQty;
		try {
			prodQty = Long.valueOf(tfPlanHdrQty.getValue());
			if (prodQty < 0) {
				tfPlanHdrQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorFlag = true;
			}
		}
		catch (Exception e) {
			tfPlanHdrQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoPlanHdrDM RotoPlanObj = new RotoPlanHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				RotoPlanObj = beanRotoPlanHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			} else {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId,
						"STT_MF_RTPLNNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						RotoPlanObj.setRotoplanrefno(slnoObj.getKeyDesc());
					}
				}
			}
			RotoPlanObj.setBranchid((Long.valueOf(cbBranch.getValue().toString())));
			RotoPlanObj.setRotoplandt(dfrotoPlanDt.getValue());
			RotoPlanObj.setPlannedqty(Long.valueOf(tfPlanHdrQty.getValue()));
			RotoPlanObj.setRemarks(taRemark.getValue());
			RotoPlanObj.setRotoplanstatus((String) cbStatus.getValue());
			RotoPlanObj.setCompanyid(companyid);
			RotoPlanObj.setLastupdateddate(DateUtils.getcurrentdate());
			RotoPlanObj.setLastupdatedby(username);
			serviceRotoplanhdr.saveRotoPlanHdr(RotoPlanObj);
			@SuppressWarnings("unchecked")
			Collection<RotoPlanDtlDM> colPlanDtls = ((Collection<RotoPlanDtlDM>) tblrotoplanDtl.getVisibleItemIds());
			for (RotoPlanDtlDM save : (Collection<RotoPlanDtlDM>) colPlanDtls) {
				save.setRotoplanId(Long.valueOf(RotoPlanObj.getRotoplanid()));
				serviceRotoplandtl.saveDetails(save);
			}
			@SuppressWarnings("unchecked")
			Collection<RotoPlanShiftDM> colAsmbShift = ((Collection<RotoPlanShiftDM>) tblShift.getVisibleItemIds());
			for (RotoPlanShiftDM saveShift : (Collection<RotoPlanShiftDM>) colAsmbShift) {
				saveShift.setRotoplanId(Long.valueOf(RotoPlanObj.getRotoplanid()));
				serviceRotoplanshift.saveDetails(saveShift);
			}
			@SuppressWarnings("unchecked")
			Collection<RotoPlanArmDM> colPlanArm = ((Collection<RotoPlanArmDM>) tblarm.getVisibleItemIds());
			for (RotoPlanArmDM save : (Collection<RotoPlanArmDM>) colPlanArm) {
				save.setRotoplanId(Long.valueOf(RotoPlanObj.getRotoplanid()));
				serviceRotoplanarm.saveDetails(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId,
						"STT_MF_RTPLNNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTPLNNO");
					}
				}
			}
			RotoDtlResetFields();
			RotoShiftResetFields();
			RotoArmResetFields();
			resetFields();
			loadSrchRslt();
			loadRotoArmList();
			loadRotoDtlList();
			loadShiftRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for AssemblyPlan. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_MFG_STT_ASMBLYPLAN);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(rotoplanId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		cbBranch.setComponentError(null);
		dfrotoPlanDt.setComponentError(null);
		tfPlanRefNo.setComponentError(null);
		tfShiftName.setComponentError(null);
		cbEmpName.setComponentError(null);
		cbClientId.setComponentError(null);
		cbWO.setComponentError(null);
		cbProd.setComponentError(null);
		cbArmProd.setComponentError(null);
		tfarmno.setComponentError(null);
		tfnoofcycle.setComponentError(null);
		cbBranch.setRequired(false);
		dfrotoPlanDt.setRequired(false);
		tfShiftName.setRequired(false);
		tfTargetQty.setRequired(false);
		cbEmpName.setRequired(false);
		cbClientId.setRequired(false);
		cbWO.setRequired(false);
		cbProd.setRequired(false);
		cbArmProd.setRequired(false);
		cbarmwono.setRequired(false);
		tfarmno.setRequired(false);
		tfnoofcycle.setRequired(false);
		RotoDtlResetFields();
		RotoShiftResetFields();
		RotoArmResetFields();
		hlCmdBtnLayout.setVisible(true);
		tblrotoplanDtl.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
		loadRotoArmList();
		loadShiftRslt();
		loadRotoDtlList();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		// Assembly Plan Hdr Resetfields
		cbBranch.setValue(branchID);
		tfPlanRefNo.setReadOnly(false);
		tfPlanRefNo.setValue("");
		tfPlanRefNo.setReadOnly(true);
		tfPlanRefNo.setComponentError(null);
		dfrotoPlanDt.setValue(null);
		tfPlanHdrQty.setValue("0");
		taRemark.setValue("");
		cbShftStatus.setValue(cbShftStatus.getItemIds().iterator().next());
		// Assembly Plan shift resetfields
		tfShiftName.setValue("");
		cbEmpName.setValue(null);
		tfTargetQty.setValue("0");
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		cbBranch.setComponentError(null);
		dfrotoPlanDt.setComponentError(null);
		tfShiftName.setComponentError(null);
		cbEmpName.setComponentError(null);
		// Assembly Plan Dtls resetfields
		cbClientId.setValue(null);
		cbWO.setValue(null);
		cbProd.setValue(null);
		tfPlanDtlQty.setValue("0");
		cbArmProd.setComponentError(null);
		tfarmno.setComponentError(null);
		tfnoofcycle.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbArmstatus.setValue(cbArmstatus.getItemIds().iterator().next());
		cbClientId.setComponentError(null);
		cbWO.setComponentError(null);
		cbProd.setComponentError(null);
		RotoPlanDtlList = new ArrayList<RotoPlanDtlDM>();
		RotoPlanShiftList = new ArrayList<RotoPlanShiftDM>();
		RotoPlanArmList = new ArrayList<RotoPlanArmDM>();
		tblrotoplanDtl.removeAllItems();
		tblShift.removeAllItems();
		tblarm.removeAllItems();
	}
	
	/*
	 * loadBranchList()-->this function is used for load the branch name
	 */
	public void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		List<BranchDM> lookUpList = serviceBranch.getBranchList(null, null, null, "Active", companyid, "P");
		beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranchDM.setBeanIdProperty("branchId");
		beanBranchDM.addAll(lookUpList);
		cbBranch.setContainerDataSource(beanBranchDM);
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee name
	 */
	public void loadEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Employee Search...");
		List<EmployeeDM> lookUpList = serviceEmployee.getEmployeeList(null, null, null, "Active", null, null, null,
				null, null, "P");
		beanEmployeeDM = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployeeDM.addAll(lookUpList);
		cbEmpName.setContainerDataSource(beanEmployeeDM);
	}
	
	/*
	 * loadClientList()-->this function is used for load the Client name
	 */
	private void loadClientList() {
		List<ClientDM> getClientList = new ArrayList<ClientDM>();
		getClientList.addAll(serviceClient.getClientDetails(companyid, null, null, null, null, null, null, null,
				"Active", "P"));
		BeanItemContainer<ClientDM> beanClient = new BeanItemContainer<ClientDM>(ClientDM.class);
		beanClient.addAll(getClientList);
		cbClientId.setContainerDataSource(beanClient);
	}
	
	/*
	 * loadProductList()-->this function is used for load the product Name
	 */
	private void loadProductList() {
		List<WorkOrderDtlDM> getworkOrderDtl = new ArrayList<WorkOrderDtlDM>();
		Long workOrdHdrId = ((WorkOrderHdrDM) cbWO.getValue()).getWorkOrdrId();
		getworkOrderDtl.addAll(serviceWorkOrderDtl.getWorkOrderDtlList(null, workOrdHdrId, null, "F"));
		BeanItemContainer<WorkOrderDtlDM> beanPlnDtl = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
		beanPlnDtl.addAll(getworkOrderDtl);
		cbProd.setContainerDataSource(beanPlnDtl);
	}
	
	// Load Product List
	public void loadProduct() {
		List<WorkOrderDtlDM> ProductList = serviceWorkOrderDtl.getWorkOrderDtlList(null, null, null, "F");
		BeanItemContainer<WorkOrderDtlDM> beanProduct = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
		beanProduct.addAll(ProductList);
		cbArmProd.setContainerDataSource(beanProduct);
	}
	
	/*
	 * loadWorkOrderNo()-->this function is used for load the workorderno
	 */
	private void loadWorkOrderNo() {
		List<WorkOrderHdrDM> getworkOrdHdr = new ArrayList<WorkOrderHdrDM>();
		Long clientId = (((ClientDM) cbClientId.getValue()).getClientId());
		System.out.println("clientId-->" + clientId);
		getworkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, clientId, null, null, null, "F",
				null, null));
		BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		beanWrkOrdHdr.addAll(getworkOrdHdr);
		cbWO.setContainerDataSource(beanWrkOrdHdr);
	}
	
	/*
	 * loadarmWorkOrderNo()-->this function is used for load the workorderno
	 */
	private void loadarmWorkOrderNo() {
		List<WorkOrderHdrDM> getworkOrdHdr = new ArrayList<WorkOrderHdrDM>();
		System.out.println("aaaaaaaaaa--->");
		getworkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, null, null, null, null, "F",
				null, null));
		BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		beanWrkOrdHdr.addAll(getworkOrdHdr);
		cbarmwono.setContainerDataSource(beanWrkOrdHdr);
	}
	
	private void deleteShiftDetails() {
		RotoPlanShiftDM removeShift = new RotoPlanShiftDM();
		if (tblShift.getValue() != null) {
			removeShift = beanRotoPlanShiftDM.getItem(tblShift.getValue()).getBean();
			RotoPlanShiftList.remove(removeShift);
			RotoShiftResetFields();
			loadShiftRslt();
		}
	}
	
	private void deleteArmDetails() {
		RotoPlanArmDM removeArm = new RotoPlanArmDM();
		if (tblarm.getValue() != null) {
			removeArm = beanRotoPlanarmDM.getItem(tblarm.getValue()).getBean();
			RotoPlanArmList.remove(removeArm);
			RotoArmResetFields();
			loadRotoArmList();
		}
	}
	
	private void deleteDetails() {
		RotoPlanDtlDM remove = new RotoPlanDtlDM();
		if (tblrotoplanDtl.getValue() != null) {
			remove = beanRotoPlandtlDM.getItem(tblrotoplanDtl.getValue()).getBean();
			RotoPlanDtlList.remove(remove);
			RotoDtlResetFields();
			loadRotoDtlList();
		}
	}
}
