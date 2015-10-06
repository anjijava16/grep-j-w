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
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
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

public class RotoPlan extends BaseTransUI {
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
	private List<RotoPlanDtlDM> listRotoPlanDetail = null;
	private List<RotoPlanArmDM> listRotoPlanArm = null;
	private List<RotoPlanShiftDM> listRotoPlanShift = null;
	// form layout for input controls
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flArmCol1, flArmCol2, flArmCol3, flDtlCol1, flDtlCol2,
			flDtlCol3, flShiftCol1, flShiftCol2, flShiftCol3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlShift = new HorizontalLayout();
	private HorizontalLayout hlHdrslap = new HorizontalLayout();
	private HorizontalLayout hlArm = new HorizontalLayout();
	private VerticalLayout vlArmDetails = new VerticalLayout();
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
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private Button btnShiftdelete = new GERPButton("Delete", "delete", this);
	private Button btnArmDelete = new GERPButton("Delete", "delete", this);
	private ComboBox cbBranch, cbStatus, cbArmProd, cbDtlStatus, cbEmpName, cbWO, cbArmstatus, cbProd, cbClientId,
			cbArmWONo;
	private ComboBox cbShftStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private TextField tfPlanRefNo, tfPlanHdrQty, tfShiftName, tfTargetQty, tfPlanDtlQty, tfArmno, tfNoofcycle;
	private DateField dfRotoPlanDt;
	private TextArea taRemark;
	private Table tblRotoPlanDtls, tblShift, tblArmDtls;
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
		tblRotoPlanDtls = new Table();
		tblRotoPlanDtls.setWidth("575");
		tblRotoPlanDtls.setPageLength(3);
		tblRotoPlanDtls.setFooterVisible(true);
		tblRotoPlanDtls.setSelectable(true);
		tblRotoPlanDtls.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblRotoPlanDtls.isSelected(event.getItemId())) {
					tblRotoPlanDtls.setImmediate(true);
					btnAddDtls.setCaption("Add");
					btnAddDtls.setStyleName("savebt");
					resetRotoDetailsFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtls.setCaption("Update");
					btnAddDtls.setStyleName("savebt");
					editRotoPlanDtls();
				}
			}
		});
		tblShift = new Table();
		tblShift.setWidth("550");
		tblShift.setPageLength(6);
		tblShift.setSelectable(true);
		tblShift.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblShift.isSelected(event.getItemId())) {
					tblShift.setImmediate(true);
					btnAddShift.setCaption("Add");
					btnAddShift.setStyleName("savebt");
					resetRotoShiftFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddShift.setCaption("Update");
					btnAddShift.setStyleName("savebt");
					editRotoPlanShift();
				}
			}
		});
		tblArmDtls = new Table();
		tblArmDtls.setWidth("550");
		tblArmDtls.setPageLength(5);
		tblArmDtls.setFooterVisible(true);
		tblArmDtls.setSelectable(true);
		tblArmDtls.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblArmDtls.isSelected(event.getItemId())) {
					tblArmDtls.setImmediate(true);
					btnAddArm.setCaption("Add");
					btnAddArm.setStyleName("savebt");
					resetRotoArmDetails();
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
		tfArmno = new GERPTextField("Arm No");
		tfArmno.setWidth("130");
		tfArmno.setValue("0");
		tfNoofcycle = new GERPTextField("No Of Cycle");
		tfNoofcycle.setWidth("110");
		tfNoofcycle.setValue("0");
		// Branch Combo Box
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("130px");
		loadBranchList();
		// Assembly Plan Datefield
		dfRotoPlanDt = new PopupDateField("Plan Date");
		dfRotoPlanDt.setWidth("130px");
		// Plan Hdr Qty.Text field
		tfPlanHdrQty = new GERPTextField("Planned Qty");
		tfPlanHdrQty.setValue("0");
		tfPlanHdrQty.setWidth("130px");
		// Remarks TextArea
		taRemark = new TextArea("Remarks");
		taRemark.setHeight("160px");
		// Status ComboBox
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Shift Name TextField
		tfShiftName = new GERPTextField("Shift Name");
		// Employee Name combobox
		cbEmpName = new GERPComboBox("Employee");
		cbEmpName.setItemCaptionPropertyId("fullname");
		loadEmployeeList();
		// TargetQty TextField
		tfTargetQty = new GERPTextField("Target Qty");
		tfTargetQty.setWidth("110");
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
		cbArmWONo = new GERPComboBox("WO No.");
		cbArmWONo.setItemCaptionPropertyId("workOrdrNo");
		cbArmWONo.setWidth("130");
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
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordCnt = listRotoPlanDetail.size();
			beanRotoPlandtlDM = new BeanItemContainer<RotoPlanDtlDM>(RotoPlanDtlDM.class);
			beanRotoPlandtlDM.addAll(listRotoPlanDetail);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the AssemblyPlanslap. result set");
			tblRotoPlanDtls.setContainerDataSource(beanRotoPlandtlDM);
			tblRotoPlanDtls.setVisibleColumns(new Object[] { "clientname", "woNo", "productname", "plannedqty" });
			tblRotoPlanDtls.setColumnHeaders(new String[] { "Client Name", "WO No.", "Product Name", "Planned Qty." });
			tblRotoPlanDtls.setColumnAlignment("rotoplandtlId", Align.RIGHT);
			tblRotoPlanDtls.setColumnFooter("plannedqty", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadRotoArmList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordCnt = listRotoPlanArm.size();
			beanRotoPlanarmDM = new BeanItemContainer<RotoPlanArmDM>(RotoPlanArmDM.class);
			beanRotoPlanarmDM.addAll(listRotoPlanArm);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Rotoplan. result set");
			tblArmDtls.setContainerDataSource(beanRotoPlanarmDM);
			tblArmDtls.setVisibleColumns(new Object[] { "workOrdrNo", "prodname", "armNo", "noOfcycle" });
			tblArmDtls.setColumnHeaders(new String[] { "WO No", "Product Name", "Arm No", "No Of Cycle" });
			tblArmDtls.setColumnAlignment("rotoplanarmId", Align.RIGHT);
			tblArmDtls.setColumnFooter("noOfcycle", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadShiftRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(vlDtl));
			recordShiftCnt = listRotoPlanShift.size();
			beanRotoPlanShiftDM = new BeanItemContainer<RotoPlanShiftDM>(RotoPlanShiftDM.class);
			beanRotoPlanShiftDM.addAll(listRotoPlanShift);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Rotoplan. result set");
			tblShift.setContainerDataSource(beanRotoPlanShiftDM);
			tblShift.setVisibleColumns(new Object[] { "shiftname", "empName", "targetqty" });
			tblShift.setColumnHeaders(new String[] { "Shift Name", "Employee Name", "Target Qty" });
			tblShift.setColumnAlignment("rotoplanshftId", Align.RIGHT);
			tblShift.setColumnFooter("targetqty", "No.of Records : " + recordShiftCnt);
			tblShift.setFooterVisible(true);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
		flHdrCol1.addComponent(dfRotoPlanDt);
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
		vlArmDetails.removeAllComponents();
		vlDtl.removeAllComponents();
		hlarmAndDtl.removeAllComponents();
		hlSearchLayout.removeAllComponents();
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol1.setHeight("190");
		flHdrCol1.addComponent(cbBranch);
		flHdrCol1.addComponent(dfRotoPlanDt);
		flHdrCol1.addComponent(tfPlanRefNo);
		flHdrCol1.addComponent(tfPlanHdrQty);
		flHdrCol1.addComponent(cbStatus);
		flHdrCol2.addComponent(taRemark);
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
		flArmCol1.addComponent(cbArmWONo);
		flArmCol1.addComponent(cbArmProd);
		flArmCol1.addComponent(tfArmno);
		flArmCol2.addComponent(tfNoofcycle);
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
		hlHdrslap.addComponent(flDtlCol2);
		hlHdrslap.addComponent(flDtlCol3);
		hlHdrslap.setSpacing(true);
		hlHdrslap.setMargin(true);
		vlArmDetails.addComponent(hlArm);
		vlArmDetails.addComponent(tblArmDtls);
		vlDtl.addComponent(hlHdrslap);
		vlDtl.addComponent(tblRotoPlanDtls);
		hlarmAndDtl.addComponent(GERPPanelGenerator.createPanel(vlArmDetails));
		hlarmAndDtl.addComponent(GERPPanelGenerator.createPanel(vlShift));
		hlarmAndDtl.setSpacing(true);
		hlarmAndDtl.setHeight("100%");
		hlHdrAndShift = new HorizontalLayout();
		hlHdrAndShift.setSpacing(true);
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(hlHdr));
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(vlDtl));
		vlHrdAndDtlAndShift = new VerticalLayout();
		vlHrdAndDtlAndShift.addComponent(hlHdrAndShift);
		vlHrdAndDtlAndShift.addComponent(hlarmAndDtl);
		hlUserInputLayout.addComponent(vlHrdAndDtlAndShift);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			List<RotoPlanHdrDM> listRotoPlanHdr = new ArrayList<RotoPlanHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + dfRotoPlanDt.getValue());
			listRotoPlanHdr = serviceRotoplanhdr.getRotoPlanHdrDetails(null, companyid, null,dfRotoPlanDt.getValue(),
					cbStatus.getValue().toString());
			recordCnt = listRotoPlanHdr.size();
			beanRotoPlanHdrDM = new BeanItemContainer<RotoPlanHdrDM>(RotoPlanHdrDM.class);
			beanRotoPlanHdrDM.addAll(listRotoPlanHdr);
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
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editRotoPlanHdrDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				RotoPlanHdrDM rotoPlanHdrDM = beanRotoPlanHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				rotoplanId = rotoPlanHdrDM.getRotoplanid();
				logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Selected rotoplan. Id -> " + rotoplanId);
				cbBranch.setValue(rotoPlanHdrDM.getBranchid());
				tfPlanRefNo.setReadOnly(false);
				tfPlanRefNo.setValue(rotoPlanHdrDM.getRotoplanrefno());
				tfPlanRefNo.setReadOnly(true);
				if (rotoPlanHdrDM.getRotoplandt() != null) {
					dfRotoPlanDt.setValue(rotoPlanHdrDM.getRotoplandt1());
				}
				tfPlanHdrQty.setValue(rotoPlanHdrDM.getPlannedqty().toString());
				if (rotoPlanHdrDM.getRemarks() != null) {
					taRemark.setValue(rotoPlanHdrDM.getRemarks());
				}
				cbStatus.setValue(rotoPlanHdrDM.getRotoplanstatus());
				listRotoPlanDetail.addAll(serviceRotoplandtl.getRotoPlanDtlList(null, Long.valueOf(rotoplanId), null,
						null, cbDtlStatus.getValue().toString()));
				listRotoPlanShift.addAll(serviceRotoplanshift.getRotoPlanShiftList(null, Long.valueOf(rotoplanId),
						null, cbShftStatus.getValue().toString()));
				listRotoPlanArm.addAll(serviceRotoplanarm.getRotoPlanArmList(null, Long.valueOf(rotoplanId), null,
						cbArmstatus.getValue().toString()));
			}
			loadRotoDtlList();
			loadShiftRslt();
			loadRotoArmList();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
	
	private void resetRotoShiftFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEmpName.setValue(null);
		tfShiftName.setValue("");
		tfTargetQty.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfShiftName.setComponentError(null);
		cbEmpName.setComponentError(null);
	}
	
	private void editRotoPlanShift() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblShift.getValue() != null) {
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
				if (editRtoDtlObj.getShiftname() != null) {
					tfShiftName.setValue(editRtoDtlObj.getShiftname());
				}
				if (editRtoDtlObj.getTargetqty() != null) {
					tfTargetQty.setValue(editRtoDtlObj.getTargetqty().toString());
				}
				if (editRtoDtlObj.getShftstatus() != null) {
					cbStatus.setValue(editRtoDtlObj.getShftstatus());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editRotoPlanArm() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblArmDtls.getValue() != null) {
				RotoPlanArmDM rotoPlanArmDM = new RotoPlanArmDM();
				rotoPlanArmDM = beanRotoPlanarmDM.getItem(tblArmDtls.getValue()).getBean();
				rotoplanId = String.valueOf(rotoPlanArmDM.getRotoplanId());
				Long uom = rotoPlanArmDM.getProductId();
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
				Long woId = rotoPlanArmDM.getWoId();
				Collection<?> woIdCol = cbArmWONo.getItemIds();
				for (Iterator<?> iteratorWO = woIdCol.iterator(); iteratorWO.hasNext();) {
					Object itemIdWOObj = (Object) iteratorWO.next();
					BeanItem<?> itemWoBean = (BeanItem<?>) cbArmWONo.getItem(itemIdWOObj);
					// Get the actual bean and use the data
					WorkOrderHdrDM workOrderDM = (WorkOrderHdrDM) itemWoBean.getBean();
					if (woId != null && woId.equals(workOrderDM.getWorkOrdrId())) {
						cbArmWONo.setValue(itemIdWOObj);
					}
				}
				if (rotoPlanArmDM.getArmNo() != null) {
					tfArmno.setValue(rotoPlanArmDM.getArmNo().toString());
				}
				if (rotoPlanArmDM.getNoOfcycle() != null) {
					tfNoofcycle.setValue(rotoPlanArmDM.getNoOfcycle().toString());
				}
				if (rotoPlanArmDM.getRtarmStatus() != null) {
					cbArmstatus.setValue(rotoPlanArmDM.getRtarmStatus());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editRotoPlanDtls() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblRotoPlanDtls.getValue() != null) {
				RotoPlanDtlDM rotoPlanDtlDM = new RotoPlanDtlDM();
				rotoPlanDtlDM = beanRotoPlandtlDM.getItem(tblRotoPlanDtls.getValue()).getBean();
				Long clientId = rotoPlanDtlDM.getClientId();
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
				Long woId = rotoPlanDtlDM.getWoId();
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
				Long prodId = rotoPlanDtlDM.getProductId();
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
				if (rotoPlanDtlDM.getPlannedqty() != null) {
					tfPlanDtlQty.setValue(rotoPlanDtlDM.getPlannedqty().toString());
				}
				if (rotoPlanDtlDM.getRtoplndtlstatus() != null) {
					cbDtlStatus.setValue(rotoPlanDtlDM.getRtoplndtlstatus());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void resetRotoDetailsFields() {
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
	
	private void resetRotoArmDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfArmno.setValue("0");
		tfNoofcycle.setValue("0");
		cbArmstatus.setValue(null);
		cbArmProd.setValue(null);
		cbArmWONo.setValue(null);
		cbArmstatus.setValue(cbArmstatus.getItemIds().iterator().next());
	}
	
	private void saveRotoPlnShiftListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoPlanShiftDM rotoPlanShiftObj = new RotoPlanShiftDM();
			if (tblShift.getValue() != null) {
				rotoPlanShiftObj = beanRotoPlanShiftDM.getItem(tblShift.getValue()).getBean();
				listRotoPlanShift.remove(rotoPlanShiftObj);
			}
			rotoPlanShiftObj.setShiftname(tfShiftName.getValue());
			if (cbEmpName.getValue() != null) {
				rotoPlanShiftObj.setEmployeeid(((EmployeeDM) cbEmpName.getValue()).getEmployeeid());
				rotoPlanShiftObj.setEmpName(((EmployeeDM) cbEmpName.getValue()).getFirstlastname());
			}
			rotoPlanShiftObj.setTargetqty(Long.valueOf(tfTargetQty.getValue()));
			if (cbStatus.getValue() != null) {
				rotoPlanShiftObj.setShftstatus((String) cbStatus.getValue());
			}
			rotoPlanShiftObj.setLastupdatedDt(DateUtils.getcurrentdate());
			rotoPlanShiftObj.setLastupdatedBy(username);
			listRotoPlanShift.add(rotoPlanShiftObj);
			loadShiftRslt();
			btnAddShift.setCaption("Add");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		resetRotoShiftFields();
	}
	
	private void saveRotoPlnArmListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoPlanArmDM rotoPlanArmObj = new RotoPlanArmDM();
			if (tblArmDtls.getValue() != null) {
				rotoPlanArmObj = beanRotoPlanarmDM.getItem(tblArmDtls.getValue()).getBean();
				listRotoPlanArm.remove(rotoPlanArmObj);
			}
			rotoPlanArmObj.setArmNo(Long.valueOf(tfArmno.getValue()));
			rotoPlanArmObj.setNoOfcycle(Long.valueOf(tfNoofcycle.getValue()));
			if (cbArmWONo.getValue() != null) {
				rotoPlanArmObj.setWoId(((WorkOrderHdrDM) cbArmWONo.getValue()).getWorkOrdrId());
				rotoPlanArmObj.setWorkOrdrNo(((WorkOrderHdrDM) cbArmWONo.getValue()).getWorkOrdrNo());
			}
			rotoPlanArmObj.setProductId(((WorkOrderDtlDM) cbArmProd.getValue()).getProdId());
			rotoPlanArmObj.setProdname(((WorkOrderDtlDM) cbArmProd.getValue()).getProductName());
			if (cbArmstatus.getValue() != null) {
				rotoPlanArmObj.setRtarmStatus((String) cbArmstatus.getValue());
			}
			rotoPlanArmObj.setLastupdatedDt(DateUtils.getcurrentdate());
			rotoPlanArmObj.setLastupdatedBy(username);
			listRotoPlanArm.add(rotoPlanArmObj);
			loadRotoArmList();
			btnAddArm.setCaption("Add");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		resetRotoArmDetails();
	}
	
	private void saveRotoPlnDtlListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoPlanDtlDM rotoPlanDtlObj = new RotoPlanDtlDM();
			if (tblRotoPlanDtls.getValue() != null) {
				rotoPlanDtlObj = beanRotoPlandtlDM.getItem(tblRotoPlanDtls.getValue()).getBean();
				listRotoPlanDetail.remove(rotoPlanDtlObj);
			}
			if (cbClientId.getValue() != null) {
				rotoPlanDtlObj.setClientId(((ClientDM) cbClientId.getValue()).getClientId());
				rotoPlanDtlObj.setClientname(((ClientDM) cbClientId.getValue()).getClientName());
			}
			if (cbWO.getValue() != null) {
				rotoPlanDtlObj.setWoId(((WorkOrderHdrDM) cbWO.getValue()).getWorkOrdrId());
				rotoPlanDtlObj.setWoNo(((WorkOrderHdrDM) cbWO.getValue()).getWorkOrdrNo());
			}
			rotoPlanDtlObj.setPlannedqty(Long.valueOf(tfPlanDtlQty.getValue()));
			if (cbProd.getValue() != null) {
				rotoPlanDtlObj.setProductId(((WorkOrderDtlDM) cbProd.getValue()).getProdId());
				rotoPlanDtlObj.setProductname(((WorkOrderDtlDM) cbProd.getValue()).getProdName());
			}
			if (cbDtlStatus.getValue() != null) {
				rotoPlanDtlObj.setRtoplndtlstatus((String) cbDtlStatus.getValue());
			}
			rotoPlanDtlObj.setLastupdatedDt(DateUtils.getcurrentdate());
			rotoPlanDtlObj.setLastupdatedBy(username);
			listRotoPlanDetail.add(rotoPlanDtlObj);
			loadRotoDtlList();
			btnAddDtls.setCaption("Add");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		resetRotoDetailsFields();
	}
	
	private boolean validationArmDetails() {
		tfArmno.setComponentError(null);
		tfNoofcycle.setComponentError(null);
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
			armNo = Long.valueOf(tfArmno.getValue());
			if (armNo < 0) {
				tfArmno.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfArmno.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		Long noCycle;
		try {
			noCycle = Long.valueOf(tfNoofcycle.getValue());
			if (noCycle < 0) {
				tfNoofcycle.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfNoofcycle.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
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
		dfRotoPlanDt.setValue(null);
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
		dfRotoPlanDt.setRequired(true);
		tfShiftName.setRequired(true);
		tfTargetQty.setRequired(true);
		cbEmpName.setRequired(true);
		cbClientId.setRequired(true);
		cbWO.setRequired(true);
		cbProd.setRequired(true);
		cbArmProd.setRequired(true);
		cbArmWONo.setRequired(true);
		tfArmno.setRequired(true);
		tfNoofcycle.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtls.setCaption("Add");
		btnAddArm.setCaption("Add");
		tblRotoPlanDtls.setVisible(true);
		try {
			tfPlanRefNo.setReadOnly(false);
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTPLNNO")
					.get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfPlanRefNo.setValue(slnoObj.getKeyDesc());
				tfPlanRefNo.setReadOnly(true);
			} else {
				tfPlanRefNo.setReadOnly(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		tblRotoPlanDtls.setVisible(true);
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
		dfRotoPlanDt.setRequired(true);
		tfShiftName.setRequired(true);
		tfTargetQty.setRequired(true);
		cbEmpName.setRequired(true);
		cbWO.setRequired(true);
		cbProd.setRequired(true);
		cbClientId.setRequired(true);
		cbArmProd.setRequired(true);
		cbArmWONo.setRequired(true);
		tfArmno.setRequired(true);
		tfNoofcycle.setRequired(true);
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
		dfRotoPlanDt.setComponentError(null);
		tfShiftName.setComponentError(null);
		cbEmpName.setComponentError(null);
		cbClientId.setComponentError(null);
		cbWO.setComponentError(null);
		cbProd.setComponentError(null);
		cbArmProd.setComponentError(null);
		tfArmno.setComponentError(null);
		tfNoofcycle.setComponentError(null);
		errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbBranch.getValue());
			errorFlag = true;
		}
		if ((dfRotoPlanDt.getValue() == null)) {
			dfRotoPlanDt.setComponentError(new UserError(GERPErrorCodes.NULL_RTOPLN_HDR));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfRotoPlanDt.getValue());
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
			RotoPlanHdrDM rotoPlanHdrDM = new RotoPlanHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				rotoPlanHdrDM = beanRotoPlanHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			rotoPlanHdrDM.setRotoplanrefno(tfPlanRefNo.getValue());
			rotoPlanHdrDM.setBranchid((Long.valueOf(cbBranch.getValue().toString())));
			rotoPlanHdrDM.setRotoplandt(dfRotoPlanDt.getValue());
			rotoPlanHdrDM.setPlannedqty(Long.valueOf(tfPlanHdrQty.getValue()));
			rotoPlanHdrDM.setRemarks(taRemark.getValue());
			rotoPlanHdrDM.setRotoplanstatus((String) cbStatus.getValue());
			rotoPlanHdrDM.setCompanyid(companyid);
			rotoPlanHdrDM.setLastupdateddate(DateUtils.getcurrentdate());
			rotoPlanHdrDM.setLastupdatedby(username);
			serviceRotoplanhdr.saveRotoPlanHdr(rotoPlanHdrDM);
			@SuppressWarnings("unchecked")
			Collection<RotoPlanDtlDM> colPlanDtls = ((Collection<RotoPlanDtlDM>) tblRotoPlanDtls.getVisibleItemIds());
			for (RotoPlanDtlDM save : (Collection<RotoPlanDtlDM>) colPlanDtls) {
				save.setRotoplanId(Long.valueOf(rotoPlanHdrDM.getRotoplanid()));
				serviceRotoplandtl.saveDetails(save);
			}
			@SuppressWarnings("unchecked")
			Collection<RotoPlanShiftDM> colAsmbShift = ((Collection<RotoPlanShiftDM>) tblShift.getVisibleItemIds());
			for (RotoPlanShiftDM saveShift : (Collection<RotoPlanShiftDM>) colAsmbShift) {
				saveShift.setRotoplanId(Long.valueOf(rotoPlanHdrDM.getRotoplanid()));
				serviceRotoplanshift.saveDetails(saveShift);
			}
			@SuppressWarnings("unchecked")
			Collection<RotoPlanArmDM> colPlanArm = ((Collection<RotoPlanArmDM>) tblArmDtls.getVisibleItemIds());
			for (RotoPlanArmDM save : (Collection<RotoPlanArmDM>) colPlanArm) {
				save.setRotoplanId(Long.valueOf(rotoPlanHdrDM.getRotoplanid()));
				serviceRotoplanarm.saveDetails(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId,
							"STT_MF_RTPLNNO").get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTPLNNO");
					}
				}
				catch (Exception e) {
				}
			}
			resetRotoDetailsFields();
			resetRotoShiftFields();
			resetRotoArmDetails();
			resetFields();
			loadSrchRslt();
			loadRotoArmList();
			loadRotoDtlList();
			loadShiftRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		dfRotoPlanDt.setComponentError(null);
		tfPlanRefNo.setComponentError(null);
		tfShiftName.setComponentError(null);
		cbEmpName.setComponentError(null);
		cbClientId.setComponentError(null);
		cbWO.setComponentError(null);
		cbProd.setComponentError(null);
		cbArmProd.setComponentError(null);
		tfArmno.setComponentError(null);
		tfNoofcycle.setComponentError(null);
		cbBranch.setRequired(false);
		dfRotoPlanDt.setRequired(false);
		tfShiftName.setRequired(false);
		tfTargetQty.setRequired(false);
		cbEmpName.setRequired(false);
		cbClientId.setRequired(false);
		cbWO.setRequired(false);
		cbProd.setRequired(false);
		cbArmProd.setRequired(false);
		cbArmWONo.setRequired(false);
		tfArmno.setRequired(false);
		tfNoofcycle.setRequired(false);
		resetRotoDetailsFields();
		resetRotoShiftFields();
		resetRotoArmDetails();
		hlCmdBtnLayout.setVisible(true);
		tblRotoPlanDtls.removeAllItems();
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
		dfRotoPlanDt.setValue(null);
		tfPlanHdrQty.setValue("0");
		taRemark.setValue("");
		cbShftStatus.setValue(cbShftStatus.getItemIds().iterator().next());
		// Assembly Plan shift resetfields
		tfShiftName.setValue("");
		cbEmpName.setValue(null);
		tfTargetQty.setValue("0");
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		cbBranch.setComponentError(null);
		dfRotoPlanDt.setComponentError(null);
		tfShiftName.setComponentError(null);
		cbEmpName.setComponentError(null);
		// Assembly Plan Dtls resetfields
		cbClientId.setValue(null);
		cbWO.setValue(null);
		cbProd.setValue(null);
		tfPlanDtlQty.setValue("0");
		cbArmProd.setComponentError(null);
		tfArmno.setComponentError(null);
		tfNoofcycle.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbArmstatus.setValue(cbArmstatus.getItemIds().iterator().next());
		cbClientId.setComponentError(null);
		cbWO.setComponentError(null);
		cbProd.setComponentError(null);
		listRotoPlanDetail = new ArrayList<RotoPlanDtlDM>();
		listRotoPlanShift = new ArrayList<RotoPlanShiftDM>();
		listRotoPlanArm = new ArrayList<RotoPlanArmDM>();
		tblRotoPlanDtls.removeAllItems();
		tblShift.removeAllItems();
		tblArmDtls.removeAllItems();
	}
	
	/*
	 * loadBranchList()-->this function is used for load the branch name
	 */
	private void loadBranchList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
			BeanContainer<Long, BranchDM> beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanBranchDM.setBeanIdProperty("branchId");
			beanBranchDM.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
			cbBranch.setContainerDataSource(beanBranchDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee name
	 */
	private void loadEmployeeList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Employee Search...");
			BeanItemContainer<EmployeeDM> beanEmployeeDM = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", null, null, null, null,
					null, "P"));
			cbEmpName.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadClientList()-->this function is used for load the Client name
	 */
	private void loadClientList() {
		try {
			BeanItemContainer<ClientDM> beanClient = new BeanItemContainer<ClientDM>(ClientDM.class);
			beanClient.addAll(serviceClient.getClientDetails(companyid, null, null, null, null, null, null, null,
					"Active", "P"));
			cbClientId.setContainerDataSource(beanClient);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadProductList()-->this function is used for load the product Name
	 */
	private void loadProductList() {
		try {
			Long workOrdHdrId = ((WorkOrderHdrDM) cbWO.getValue()).getWorkOrdrId();
			BeanItemContainer<WorkOrderDtlDM> beanPlnDtl = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
			beanPlnDtl.addAll(serviceWorkOrderDtl.getWorkOrderDtlList(null, workOrdHdrId, null, "F"));
			cbProd.setContainerDataSource(beanPlnDtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Product List
	private void loadProduct() {
		try {
			BeanItemContainer<WorkOrderDtlDM> beanProduct = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
			beanProduct.addAll(serviceWorkOrderDtl.getWorkOrderDtlList(null, null, null, "F"));
			cbArmProd.setContainerDataSource(beanProduct);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadWorkOrderNo()-->this function is used for load the workorderno
	 */
	private void loadWorkOrderNo() {
		try {
			Long clientId = (((ClientDM) cbClientId.getValue()).getClientId());
			BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(
					WorkOrderHdrDM.class);
			beanWrkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, clientId, null, null, null,
					"F", null, null, null, null, null));
			cbWO.setContainerDataSource(beanWrkOrdHdr);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadarmWorkOrderNo()-->this function is used for load the workorderno
	 */
	private void loadarmWorkOrderNo() {
		try {
			BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(
					WorkOrderHdrDM.class);
			beanWrkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, null, null, null, null, "F",
					null, null, null, null, null));
			cbArmWONo.setContainerDataSource(beanWrkOrdHdr);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deleteShiftDetails() {
		try {
			RotoPlanShiftDM removeShift = new RotoPlanShiftDM();
			if (tblShift.getValue() != null) {
				removeShift = beanRotoPlanShiftDM.getItem(tblShift.getValue()).getBean();
				listRotoPlanShift.remove(removeShift);
				resetRotoShiftFields();
				loadShiftRslt();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deleteArmDetails() {
		try {
			RotoPlanArmDM removeArm = new RotoPlanArmDM();
			if (tblArmDtls.getValue() != null) {
				removeArm = beanRotoPlanarmDM.getItem(tblArmDtls.getValue()).getBean();
				listRotoPlanArm.remove(removeArm);
				resetRotoArmDetails();
				loadRotoArmList();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deleteDetails() {
		try {
			RotoPlanDtlDM remove = new RotoPlanDtlDM();
			if (tblRotoPlanDtls.getValue() != null) {
				remove = beanRotoPlandtlDM.getItem(tblRotoPlanDtls.getValue()).getBean();
				listRotoPlanDetail.remove(remove);
				resetRotoDetailsFields();
				loadRotoDtlList();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
