/**
 * File Name 		: FoamPlan.java 
 * Description 		: this class is used for add/edit FoamPlan  details. 
 * Author 			: Karthikeyan R
 * Date 			: Oct-11-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version     	  Date           	Modified By               Remarks
 * 0.1          Oct-11-2014     	Karthikeyan R	        Initial Version
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
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.txn.WorkOrderDtlDM;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.service.txn.WorkOrderDtlService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.stt.mfg.domain.txn.FoamPlanDtlDM;
import com.gnts.stt.mfg.domain.txn.FoamPlanHdrDM;
import com.gnts.stt.mfg.domain.txn.FoamPlanShiftDM;
import com.gnts.stt.mfg.service.txn.FoamPlanDtlService;
import com.gnts.stt.mfg.service.txn.FoamPlanHdrService;
import com.gnts.stt.mfg.service.txn.FoamPlanShiftService;
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

public class FoamPlan extends BaseUI {
	// Bean Creation
	private FoamPlanHdrService serviceFormPlanHdr = (FoamPlanHdrService) SpringContextHelper.getBean("foamplanhdr");
	private FoamPlanDtlService serviceFormPlanDtl = (FoamPlanDtlService) SpringContextHelper.getBean("foamplandtl");
	private FoamPlanShiftService serviceFormPlanShift = (FoamPlanShiftService) SpringContextHelper
			.getBean("foamplanshift");
	private ClientService serviceClient = (ClientService) SpringContextHelper.getBean("clients");
	private BranchService servicebeanBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private WorkOrderHdrService serviceWorkOrderHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private WorkOrderDtlService serviceWorkOrderDtl = (WorkOrderDtlService) SpringContextHelper.getBean("workOrderDtl");
	// User Input Components for Work Order Details
	private BeanItemContainer<FoamPlanHdrDM> beanFormPlanHdrDM = null;
	private BeanItemContainer<FoamPlanDtlDM> beanFormPlanDtlDM = null;
	private BeanItemContainer<FoamPlanShiftDM> beanFormPlanShiftDM = null;
	private BeanContainer<Long, BranchDM> beanBranchDM = null;
	private BeanItemContainer<EmployeeDM> beanEmployeeDM = null;
	// Search Control Layout
	private HorizontalLayout hlShift, hlHdrslap, hlHdrAndShift;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private VerticalLayout vlShift, vlHdr, vlHrdAndDtlAndShift;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// FormPlan Hdr Components
	private TextField tfFormplanRefNo, tfplanHdrQty, tfplnRefNo;
	private ComboBox cbBranch;
	private DateField dfplanDt;
	private TextArea taRemarks;
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4;
	// FormPlan Dtl Components
	private TextField tfplanDtlQty;
	private ComboBox cbWO, cbProduct, cbDtlStatus, cbclientid;
	private FormLayout flDtlCol1, flDtlCol2, flDtlCol3, flDtlCol4, flDtlCol5, flDtlCol6;
	List<FoamPlanDtlDM> FormPlanDtlList = null;
	// FormPlan Shift Components
	private TextField tfshiftname, tfTargetQty;
	private ComboBox cbSftstatus, cbEmpName;
	private FormLayout flshiftCol1, flshiftCol2, flshiftCol3, flshiftCol4, flshiftCol5;
	private Button btnAddDtls = new GERPButton("Add", "add", this);
	private Button btnAddShift = new GERPButton("Add", "add", this);
	public Button btndelete = new GERPButton("Delete", "delete", this);
	public Button btnShiftdelete = new GERPButton("Delete", "delete", this);
	private Table tblFormplanDtl, tblShift;
	List<FoamPlanShiftDM> formShiftList = new ArrayList<FoamPlanShiftDM>();
	// local variables declaration
	private String username;
	private Long companyid, branchID, moduleId;
	private Long foamplndtlid;
	private String formplanid;
	private int recordCnt = 0;
	private int recordShiftCnt = 0;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(FoamPlan.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor received the parameters from Login UI class
	public FoamPlan() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside FoamPlan() constructor");
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
					savefoamplanDtllistDetails();
				}
			}
		});
		tfplnRefNo = new TextField("Plan Reference No.");
		btnAddShift.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateShiftDetails()) {
					savefoamplanShiftListDetails();
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
		tblFormplanDtl = new GERPTable();
		tblFormplanDtl.setPageLength(4);
		tblFormplanDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblFormplanDtl.isSelected(event.getItemId())) {
					tblFormplanDtl.setImmediate(true);
					btnAddDtls.setCaption("Add");
					tblFormplanDtl.setStyleName("savebt");
					foamplanDtlResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtls.setCaption("Update");
					btnAddDtls.setStyleName("savebt");
					editFoamplanDtls();
				}
			}
		});
		tblShift = new GERPTable();
		/*
		 * tblShift.setHeight("75px"); tblShift.setWidth("630px");
		 */
		tblShift.setPageLength(6);
		tblShift.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblShift.isSelected(event.getItemId())) {
					tblShift.setImmediate(true);
					btnAddShift.setCaption("Add");
					btnAddShift.setStyleName("savebt");
					foamplanShiftResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddShift.setCaption("Update");
					btnAddShift.setStyleName("savebt");
					editfoamplanShift();
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
				tfFormplanRefNo.setReadOnly(false);
			}
		});
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting FormPlan UI");
		// FormPlan Hdr
		// plan Ref.No text field
		tfFormplanRefNo = new GERPTextField("Plan Ref.No");
		// Branch Combo box
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		// Form Plan Date
		dfplanDt = new PopupDateField("Plan Date");
		dfplanDt.setDateFormat("dd-MMM-yyyy");
		dfplanDt.setWidth("130px");
		// Earn amount From Text field
		tfplanHdrQty = new GERPTextField("Planned Qty");
		tfplanHdrQty.setValue("0");
		// Remarks TextArea
		taRemarks = new TextArea("Remarks");
		taRemarks.setHeight("75px");
		taRemarks.setWidth("150px");
		taRemarks.setNullRepresentation("");
		// Status ComboBox
		cbSftstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbSftstatus.setWidth("70px");
		// Status ComboBox
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// cbDtlStatus.setWidth("140px");
		// Shift Name
		tfshiftname = new GERPTextField("Shift Name");
		tfshiftname.setWidth("100px");
		// Employee Name combobox
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setItemCaptionPropertyId("fullname");
		loadEmployeeList();
		// TargetQty TextField
		tfTargetQty = new GERPTextField("Target Qty");
		tfTargetQty.setValue("0");
		tfTargetQty.setWidth("70px");
		// Client Id Textfield
		cbclientid = new GERPComboBox("Client Name");
		cbclientid.setItemCaptionPropertyId("clientName");
		cbclientid.setWidth("110");
		loadClientList();
		cbclientid.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				cbWO.setImmediate(true);
				cbProduct.setImmediate(true);
				cbWO.removeAllItems();
				cbProduct.removeAllItems();
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbclientid.getItem(itemId);
				if (item != null) {
					loadWorkOrderNo();
				}
			}
		});
		// WOId ComboBox
		cbWO = new GERPComboBox("WO No.");
		cbWO.setItemCaptionPropertyId("workOrdrNo");
		cbWO.setWidth("110");
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
		// Product Id ComboBox
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodName");
		cbProduct.setWidth("110");
		// Plan Qty Textfield
		tfplanDtlQty = new GERPTextField("Plan Qty");
		tfplanDtlQty.setValue("0");
		tfplanDtlQty.setWidth("100px");
		// Status ComboBox
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadShiftRslt();
		loadPlanDtlRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Form planning search layout");
		// Remove all components in search layout
		tfFormplanRefNo.setReadOnly(false);
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		Label lbl = new Label();
		flHdrCol1.addComponent(dfplanDt);
		flHdrCol2.addComponent(lbl);
		flHdrCol3.addComponent(cbHdrStatus);
		flHdrCol4.addComponent(tfplnRefNo);
		hlSearchLayout.addComponent(flHdrCol4);
		hlSearchLayout.addComponent(flHdrCol1);
		hlSearchLayout.addComponent(flHdrCol2);
		hlSearchLayout.addComponent(flHdrCol3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Form planning search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flHdrCol1 = new FormLayout();
		// flHdrCol3 = new FormLayout();
		flHdrCol1.addComponent(cbBranch);
		flHdrCol1.addComponent(tfFormplanRefNo);
		flHdrCol1.addComponent(dfplanDt);
		flHdrCol1.addComponent(tfplanHdrQty);
		flHdrCol1.addComponent(taRemarks);
		flHdrCol1.addComponent(cbHdrStatus);
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdrCol1);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding Shift Components
		flshiftCol1 = new FormLayout();
		flshiftCol2 = new FormLayout();
		flshiftCol3 = new FormLayout();
		flshiftCol4 = new FormLayout();
		flshiftCol5 = new FormLayout();
		flshiftCol1.addComponent(tfshiftname);
		flshiftCol2.addComponent(cbEmpName);
		flshiftCol3.addComponent(tfTargetQty);
		flshiftCol4.addComponent(cbSftstatus);
		flshiftCol5.addComponent(btnAddShift);
		flshiftCol5.addComponent(btnShiftdelete);
		flshiftCol5.setComponentAlignment(btnAddShift, Alignment.BOTTOM_CENTER);
		vlShift = new VerticalLayout();
		hlShift = new HorizontalLayout();
		hlShift.addComponent(flshiftCol1);
		hlShift.addComponent(flshiftCol2);
		hlShift.addComponent(flshiftCol3);
		hlShift.addComponent(flshiftCol4);
		hlShift.addComponent(flshiftCol5);
		hlShift.setSpacing(true);
		hlShift.setMargin(true);
		vlShift.addComponent(hlShift);
		vlShift.addComponent(tblShift);
		vlShift.setWidth("915px");
		// Adding Dtl Components
		flDtlCol1 = new FormLayout();
		flDtlCol2 = new FormLayout();
		flDtlCol3 = new FormLayout();
		flDtlCol4 = new FormLayout();
		flDtlCol5 = new FormLayout();
		flDtlCol6 = new FormLayout();
		flDtlCol1.addComponent(cbclientid);
		flDtlCol2.addComponent(cbWO);
		flDtlCol3.addComponent(cbProduct);
		flDtlCol4.addComponent(tfplanDtlQty);
		flDtlCol5.addComponent(cbDtlStatus);
		flDtlCol6.addComponent(btnAddDtls);
		flDtlCol6.addComponent(btndelete);
		hlHdrslap = new HorizontalLayout();
		hlHdrslap.addComponent(flDtlCol1);
		hlHdrslap.addComponent(flDtlCol2);
		hlHdrslap.addComponent(flDtlCol3);
		hlHdrslap.addComponent(flDtlCol4);
		hlHdrslap.addComponent(flDtlCol5);
		hlHdrslap.addComponent(flDtlCol6);
		hlHdrslap.setSpacing(true);
		hlHdrslap.setMargin(true);
		vlHdr = new VerticalLayout();
		vlHdr.addComponent(hlHdrslap);
		vlHdr.addComponent(tblFormplanDtl);
		vlHdr.setSpacing(true);
		hlHdrAndShift = new HorizontalLayout();
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(hlHdr));
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(vlShift));
		vlHrdAndDtlAndShift = new VerticalLayout();
		vlHrdAndDtlAndShift.addComponent(GERPPanelGenerator.createPanel(hlHdrAndShift));
		vlHrdAndDtlAndShift.addComponent(GERPPanelGenerator.createPanel(vlHdr));
		vlHrdAndDtlAndShift.setSpacing(true);
		vlHrdAndDtlAndShift.setWidth("100%");
		hlUserInputLayout.addComponent(vlHrdAndDtlAndShift);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<FoamPlanHdrDM> FormPlanList = new ArrayList<FoamPlanHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfFormplanRefNo.getValue() + ", " + cbHdrStatus.getValue());
		FormPlanList = serviceFormPlanHdr.getFormPlanHdrDetails(null, dfplanDt.getValue(), companyid,
				(String) cbHdrStatus.getValue(), (String) tfplnRefNo.getValue());
		recordCnt = FormPlanList.size();
		beanFormPlanHdrDM = new BeanItemContainer<FoamPlanHdrDM>(FoamPlanHdrDM.class);
		beanFormPlanHdrDM.addAll(FormPlanList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the FormPlan. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanFormPlanHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "formplanid", "formplanreffno", "formplandate",
				"foamplnstatus", "lastupdateddate", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Plan Ref No", "Plan Date", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("formplanid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadPlanDtlRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		logger.info("Company ID : " + companyid + " | saveasmblPlnDtlListDetails User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + cbclientid.getValue() + ", " + tfplanDtlQty.getValue()
				+ (String) cbDtlStatus.getValue() + ", " + foamplndtlid);
		recordCnt = FormPlanDtlList.size();
		beanFormPlanDtlDM = new BeanItemContainer<FoamPlanDtlDM>(FoamPlanDtlDM.class);
		beanFormPlanDtlDM.addAll(FormPlanDtlList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the PlanDtl. result set");
		tblFormplanDtl.setContainerDataSource(beanFormPlanDtlDM);
		tblFormplanDtl.setVisibleColumns(new Object[] { "clientname", "woNo", "prodName", "plannedqty",
				"asmplndlstatus", "lastupdateddt", "lastupdatedby" });
		tblFormplanDtl.setColumnHeaders(new String[] { "Client Name", "WO No.", "Product Name", "Plan Qty", "Status",
				"Last Updated Date", "Last Updated By" });
		tblFormplanDtl.setColumnAlignment("foamplndtlid", Align.RIGHT);
		tblFormplanDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadShiftRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		recordShiftCnt = formShiftList.size();
		beanFormPlanShiftDM = new BeanItemContainer<FoamPlanShiftDM>(FoamPlanShiftDM.class);
		beanFormPlanShiftDM.addAll(formShiftList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the FormPlan. result set");
		tblShift.setContainerDataSource(beanFormPlanShiftDM);
		tblShift.setVisibleColumns(new Object[] { "shiftName", "empname", "targetqty", "shiftstatus", "lastupdateddt",
				"lastupdatedby" });
		tblShift.setColumnHeaders(new String[] { "Shift Name", "Employee Name", "Target Qty", "Status",
				"Last Updated Dt", "Last Updated By" });
		tblShift.setColumnAlignment("formplnshiftid", Align.RIGHT);
		tblShift.setColumnFooter("lastupdatedby", "No.of Records : " + recordShiftCnt);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		// Foam Plan Hdr Resetfields
		cbBranch.setValue(cbBranch.getItemIds().iterator().next());
		tfFormplanRefNo.setReadOnly(false);
		tfFormplanRefNo.setValue("");
		tfFormplanRefNo.setReadOnly(true);
		tfFormplanRefNo.setComponentError(null);
		dfplanDt.setValue(null);
		tfplnRefNo.setValue("");
		tfplanHdrQty.setValue("0");
		taRemarks.setValue("");
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		// Foam Plan shift Resetfields
		tfshiftname.setValue("");
		cbEmpName.setValue(null);
		tfTargetQty.setValue("0");
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		cbEmpName.setComponentError(null);
		dfplanDt.setComponentError(null);
		tfTargetQty.setComponentError(null);
		tfshiftname.setComponentError(null);
		cbBranch.setComponentError(null);
		// Foam Plan Dtls ResetFields
		cbclientid.setValue(null);
		cbWO.setValue(null);
		cbProduct.setValue(null);
		tfplanDtlQty.setValue("0");
		cbSftstatus.setValue(cbSftstatus.getItemIds().iterator().next());
		cbWO.setComponentError(null);
		cbProduct.setComponentError(null);
		FormPlanDtlList = new ArrayList<FoamPlanDtlDM>();
		formShiftList = new ArrayList<FoamPlanShiftDM>();
		tblFormplanDtl.removeAllItems();
		tblShift.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editFormPlanHdrDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item planRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (planRcd != null) {
			FoamPlanHdrDM editFormPlan = beanFormPlanHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			formplanid = editFormPlan.getFormplanid().toString();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected AssemblyPlan. Id -> " + foamplndtlid);
			cbBranch.setValue(editFormPlan.getBranchid());
			tfFormplanRefNo.setReadOnly(false);
			tfFormplanRefNo.setValue((String) planRcd.getItemProperty("formplanreffno").getValue());
			tfFormplanRefNo.setReadOnly(true);
			// dfplanDt.setValue(editFormPlan.getFormplandate());
			if (editFormPlan.getFormplandate() != null) {
				dfplanDt.setValue(editFormPlan.getFormplandate1());
			}
			tfplanHdrQty.setValue(editFormPlan.getPlannedqty().toString());
			taRemarks.setValue(editFormPlan.getRemark());
			cbHdrStatus.setValue(editFormPlan.getFoamplnstatus());
			FormPlanDtlList.addAll(serviceFormPlanDtl.getFormPlanDtl(null, Long.valueOf(formplanid), null, null,
					(String) cbHdrStatus.getValue()));
			formShiftList.addAll(serviceFormPlanShift.getFormPlanShift(null, Long.valueOf(formplanid), null,
					(String) cbHdrStatus.getValue()));
		}
		loadPlanDtlRslt();
		loadShiftRslt();
	}
	
	private void editFoamplanDtls() {
		hlUserInputLayout.setVisible(true);
		Item itselect = tblFormplanDtl.getItem(tblFormplanDtl.getValue());
		if (itselect != null) {
			FoamPlanDtlDM formplanobj = new FoamPlanDtlDM();
			formplanobj = beanFormPlanDtlDM.getItem(tblFormplanDtl.getValue()).getBean();
			Long clientId = formplanobj.getClientid();
			Collection<?> clientIdCol = cbclientid.getItemIds();
			for (Iterator<?> iteratorclient = clientIdCol.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbclientid.getItem(itemIdClient);
				// Get the actual bean and use the data
				ClientDM clientObj = (ClientDM) itemclient.getBean();
				if (clientId != null && clientId.equals(clientObj.getClientId())) {
					cbclientid.setValue(itemIdClient);
				}
			}
			Long woId = formplanobj.getWoid();
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
			Long prodId = formplanobj.getProductid();
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
			if (itselect.getItemProperty("plannedqty").getValue() != null) {
				tfplanDtlQty.setValue(itselect.getItemProperty("plannedqty").getValue().toString());
			}
			if (itselect.getItemProperty("asmplndlstatus").getValue() != null) {
				cbDtlStatus.setValue(itselect.getItemProperty("asmplndlstatus").getValue().toString());
			}
		}
	}
	
	private void editfoamplanShift() {
		hlUserInputLayout.setVisible(true);
		Item itselect = tblShift.getItem(tblShift.getValue());
		if (itselect != null) {
			FoamPlanShiftDM editfoamplanDtlobj = new FoamPlanShiftDM();
			editfoamplanDtlobj = beanFormPlanShiftDM.getItem(tblShift.getValue()).getBean();
			Long empId = editfoamplanDtlobj.getEmployeeid();
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
			if (itselect.getItemProperty("shiftName").getValue() != null) {
				tfshiftname.setValue(itselect.getItemProperty("shiftName").getValue().toString());
			}
			if (itselect.getItemProperty("targetqty").getValue() != null) {
				tfTargetQty.setValue(itselect.getItemProperty("targetqty").getValue().toString());
			}
			if (itselect.getItemProperty("shiftstatus").getValue() != null) {
				cbSftstatus.setValue(itselect.getItemProperty("shiftstatus").getValue().toString());
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
		dfplanDt.setValue(null);
		tfplnRefNo.setValue("");
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
		assembleInputUserLayout();
		loadShiftRslt();
		loadPlanDtlRslt();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbBranch.setRequired(true);
		dfplanDt.setRequired(true);
		tfshiftname.setRequired(true);
		cbEmpName.setRequired(true);
		cbWO.setRequired(true);
		cbProduct.setRequired(true);
		cbclientid.setRequired(true);
		tfTargetQty.setRequired(true);
		tfplanDtlQty.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		hlCmdBtnLayout.setVisible(false);
		btnAddDtls.setCaption("Add");
		btnAddShift.setCaption("Add");
		tblFormplanDtl.setVisible(true);
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STMF_APLNO");
		tfFormplanRefNo.setReadOnly(true);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfFormplanRefNo.setReadOnly(true);
			} else {
				tfFormplanRefNo.setReadOnly(false);
			}
		}
		tblFormplanDtl.setVisible(true);
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for FoamPlan. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_WORKORDER_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", foamplndtlid);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		cbBranch.setComponentError(null);
		dfplanDt.setComponentError(null);
		tfFormplanRefNo.setComponentError(null);
		tfshiftname.setComponentError(null);
		cbEmpName.setComponentError(null);
		tfTargetQty.setComponentError(null);
		cbclientid.setComponentError(null);
		tfplanDtlQty.setComponentError(null);
		cbWO.setComponentError(null);
		cbProduct.setComponentError(null);
		cbBranch.setRequired(false);
		dfplanDt.setRequired(false);
		tfshiftname.setRequired(false);
		cbEmpName.setRequired(false);
		tfTargetQty.setRequired(false);
		cbclientid.setRequired(false);
		tfplanDtlQty.setRequired(false);
		cbWO.setRequired(false);
		cbProduct.setRequired(false);
		foamplanDtlResetFields();
		foamplanShiftResetFields();
		hlCmdBtnLayout.setVisible(true);
		tblFormplanDtl.removeAllItems();
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
		dfplanDt.setRequired(true);
		tfshiftname.setRequired(true);
		cbEmpName.setRequired(true);
		tfTargetQty.setRequired(true);
		cbclientid.setRequired(true);
		tfplanDtlQty.setRequired(true);
		cbWO.setRequired(true);
		cbProduct.setRequired(true);
		/*
		 * // reset the input controls to default value comment List<SlnoGenDM> slnoList =
		 * serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STMF_APLNO");
		 * tfFormplanRefNo.setReadOnly(false); for (SlnoGenDM slnoObj : slnoList) { if
		 * (slnoObj.getAutoGenYN().equals("Y")) { tfFormplanRefNo.setReadOnly(true); } }
		 */
		tblMstScrSrchRslt.setVisible(false);
		/*
		 * if (tfFormplanRefNo.getValue() == null || tfFormplanRefNo.getValue().trim().length() == 0) {
		 * tfFormplanRefNo.setReadOnly(false); }
		 */
		assembleInputUserLayout();
		resetFields();
		editFormPlanHdrDetails();
		editFoamplanDtls();
		editfoamplanShift();
	}
	
	private void foamplanDtlResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbclientid.setValue(null);
		cbWO.setValue(null);
		cbProduct.setValue(null);
		tfplanDtlQty.setValue("0");
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		cbclientid.setComponentError(null);
		tfplanDtlQty.setComponentError(null);
		cbWO.setComponentError(null);
		cbProduct.setComponentError(null);
	}
	
	private void foamplanShiftResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEmpName.setValue(null);
		tfshiftname.setValue("");
		tfTargetQty.setValue("0");
		cbSftstatus.setValue(cbSftstatus.getItemIds().iterator().next());
		tfshiftname.setComponentError(null);
		cbEmpName.setComponentError(null);
		tfTargetQty.setComponentError(null);
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		dfplanDt.setComponentError(null);
		tfshiftname.setComponentError(null);
		cbEmpName.setComponentError(null);
		tfTargetQty.setComponentError(null);
		cbclientid.setComponentError(null);
		tfplanDtlQty.setComponentError(null);
		cbWO.setComponentError(null);
		cbProduct.setComponentError(null);
		errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbBranch.getValue());
			errorFlag = true;
		}
		if ((dfplanDt.getValue() == null)) {
			dfplanDt.setComponentError(new UserError(GERPErrorCodes.NULL_ASMBL_PLAN_DT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfplanDt.getValue());
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validateDtlDetails() {
		tfplanDtlQty.setComponentError(null);
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbclientid.getValue() == null)) {
			cbclientid.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbclientid.getValue());
			isValid = false;
		} else {
			cbclientid.setComponentError(null);
		}
		if ((cbWO.getValue() == null)) {
			cbWO.setComponentError(new UserError(GERPErrorCodes.NULL_WO_PLN_NO));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbWO.getValue());
			isValid = false;
		} else {
			cbWO.setComponentError(null);
		}
		if ((cbProduct.getValue() == null)) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUC_CODE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbProduct.getValue());
			isValid = false;
		} else {
			cbProduct.setComponentError(null);
		}
		Long plandtlQty;
		try {
			plandtlQty = Long.valueOf(tfplanDtlQty.getValue());
			if (plandtlQty < 0) {
				tfplanDtlQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfplanDtlQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		return isValid;
	}
	
	private boolean validateShiftDetails() {
		tfTargetQty.setComponentError(null);
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Long targetQty;
		try {
			targetQty = Long.valueOf(tfTargetQty.getValue());
			if (targetQty < 0) {
				tfTargetQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorFlag = true;
			}
		}
		catch (Exception e) {
			tfTargetQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			errorFlag = true;
		}
		if ((tfshiftname.getValue() == "")) {
			tfshiftname.setComponentError(new UserError(GERPErrorCodes.NULL_SHIFT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfshiftname.getValue());
			isValid = false;
		} else {
			tfshiftname.setComponentError(null);
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
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			FoamPlanHdrDM foamplanhdrobj = new FoamPlanHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				foamplanhdrobj = beanFormPlanHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			} else {
				List<SlnoGenDM> slnoList = serviceSlnogen
						.getSequenceNumber(companyid, branchID, moduleId, "STMF_APLNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						foamplanhdrobj.setFormplanreffno(slnoObj.getKeyDesc());
					}
				}
			}
			foamplanhdrobj.setBranchid((Long.valueOf(cbBranch.getValue().toString())));
			foamplanhdrobj.setFormplandate(dfplanDt.getValue());
			foamplanhdrobj.setPlannedqty(Long.valueOf(tfplanHdrQty.getValue()));
			foamplanhdrobj.setRemark(taRemarks.getValue());
			foamplanhdrobj.setFoamplnstatus((String) cbHdrStatus.getValue());
			foamplanhdrobj.setCompanyid(companyid);
			foamplanhdrobj.setLastupdateddate(DateUtils.getcurrentdate());
			foamplanhdrobj.setLastupdatedby(username);
			serviceFormPlanHdr.saveFormPlanHdr(foamplanhdrobj);
			@SuppressWarnings("unchecked")
			Collection<FoamPlanDtlDM> colPlanDtls = ((Collection<FoamPlanDtlDM>) tblFormplanDtl.getVisibleItemIds());
			for (FoamPlanDtlDM save : (Collection<FoamPlanDtlDM>) colPlanDtls) {
				save.setFoamplnid(Long.valueOf(foamplanhdrobj.getFormplanid()));
				serviceFormPlanDtl.saveFormPlanDtl(save);
			}
			@SuppressWarnings("unchecked")
			Collection<FoamPlanShiftDM> colfoamShift = ((Collection<FoamPlanShiftDM>) tblShift.getVisibleItemIds());
			for (FoamPlanShiftDM save : (Collection<FoamPlanShiftDM>) colfoamShift) {
				save.setFoamplnid(Long.valueOf(foamplanhdrobj.getFormplanid()));
				serviceFormPlanShift.saveFormPlanShift(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSlnogen
						.getSequenceNumber(companyid, branchID, moduleId, "STMF_APLNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "STMF_APLNO");
					}
				}
			}
			foamplanDtlResetFields();
			foamplanShiftResetFields();
			resetFields();
			loadSrchRslt();
			foamplndtlid=0L;
			loadPlanDtlRslt();
			loadShiftRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void savefoamplanDtllistDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			FoamPlanDtlDM formplandtlobj = new FoamPlanDtlDM();
			if (tblFormplanDtl.getValue() != null) {
				formplandtlobj = beanFormPlanDtlDM.getItem(tblFormplanDtl.getValue()).getBean();
				FormPlanDtlList.remove(formplandtlobj);
			}
			if (cbclientid.getValue() != null) {
				formplandtlobj.setClientid(((ClientDM) cbclientid.getValue()).getClientId());
				formplandtlobj.setClientname(((ClientDM) cbclientid.getValue()).getClientName());
			}
			if (cbWO.getValue() != null) {
				formplandtlobj.setWoid(((WorkOrderHdrDM) cbWO.getValue()).getWorkOrdrId());
				formplandtlobj.setWoNo(((WorkOrderHdrDM) cbWO.getValue()).getWorkOrdrNo());
			}
			formplandtlobj.setPlannedqty(Long.valueOf(tfplanDtlQty.getValue()));
			if (cbProduct.getValue() != null) {
				formplandtlobj.setProductid(((WorkOrderDtlDM) cbProduct.getValue()).getProdId());
				formplandtlobj.setProdName(((WorkOrderDtlDM) cbProduct.getValue()).getProdName());
			}
			if (cbDtlStatus.getValue() != null) {
				formplandtlobj.setAsmplndlstatus(((String) cbDtlStatus.getValue()));
			}
			formplandtlobj.setLastupdateddt(DateUtils.getcurrentdate());
			formplandtlobj.setLastupdatedby(username);
			FormPlanDtlList.add(formplandtlobj);
			loadPlanDtlRslt();
			btnAddDtls.setCaption("Add");
			btnAddDtls.setStyleName("savebt");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		foamplanDtlResetFields();
	}
	
	private void savefoamplanShiftListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			FoamPlanShiftDM formplanshiftobj = new FoamPlanShiftDM();
			if (tblShift.getValue() != null) {
				formplanshiftobj = beanFormPlanShiftDM.getItem(tblShift.getValue()).getBean();
				formShiftList.remove(formplanshiftobj);
			}
			formplanshiftobj.setShiftName(tfshiftname.getValue());
			if (cbEmpName.getValue() != null) {
				formplanshiftobj.setEmployeeid(((EmployeeDM) cbEmpName.getValue()).getEmployeeid());
				formplanshiftobj.setEmpname(((EmployeeDM) cbEmpName.getValue()).getFirstlastname());
			}
			formplanshiftobj.setTargetqty(Long.valueOf(tfTargetQty.getValue()));
			if (cbSftstatus.getValue() != null) {
				formplanshiftobj.setShiftstatus((String) cbSftstatus.getValue());
			}
			formplanshiftobj.setLastupdateddt(DateUtils.getcurrentdate());
			formplanshiftobj.setLastupdatedby(username);
			formShiftList.add(formplanshiftobj);
			loadShiftRslt();
			foamplndtlid=0L;
			btnAddShift.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		foamplanShiftResetFields();
	}
	
	/*
	 * loadBranchList()-->this function is used for load the branch name
	 */
	public void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		List<BranchDM> branchlist = servicebeanBranch.getBranchList(branchID, null, null, "Active", companyid, "F");
		beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranchDM.setBeanIdProperty("branchId");
		beanBranchDM.addAll(branchlist);
		cbBranch.setContainerDataSource(beanBranchDM);
	}
	
	/*
	 * loadClientList()-->this function is used for load the Client name
	 */
	private void loadClientList() {
		List<ClientDM> getClientList = new ArrayList<ClientDM>();
		getClientList.addAll(serviceClient.getClientDetails(companyid, null, null, null, null, null, null, null, "Active",
				"P"));
		BeanItemContainer<ClientDM> beanClient = new BeanItemContainer<ClientDM>(ClientDM.class);
		beanClient.addAll(getClientList);
		cbclientid.setContainerDataSource(beanClient);
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
	 * loadProductList()-->this function is used for load the product Name
	 */
	private void loadProductList() {
		List<WorkOrderDtlDM> getworkOrderDtl = new ArrayList<WorkOrderDtlDM>();
		Long workOrdHdrId = ((WorkOrderHdrDM) cbWO.getValue()).getWorkOrdrId();
		getworkOrderDtl.addAll(serviceWorkOrderDtl.getWorkOrderDtlList(null, workOrdHdrId, null, "F"));
		BeanItemContainer<WorkOrderDtlDM> beanPlnDtl = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
		beanPlnDtl.addAll(getworkOrderDtl);
		cbProduct.setContainerDataSource(beanPlnDtl);
	}
	
	/*
	 * loadWorkOrderNo()-->this function is used for load the workorderno
	 */
	private void loadWorkOrderNo() {
		List<WorkOrderHdrDM> getworkOrdHdr = new ArrayList<WorkOrderHdrDM>();
		Long clientId = (((ClientDM) cbclientid.getValue()).getClientId());
		getworkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, clientId, null, null, null, "F",null,null));
		BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		beanWrkOrdHdr.addAll(getworkOrdHdr);
		cbWO.setContainerDataSource(beanWrkOrdHdr);
	}
	
	private void deleteShiftDetails() {
		FoamPlanShiftDM removeShift = new FoamPlanShiftDM();
		if (tblShift.getValue() != null) {
			removeShift = beanFormPlanShiftDM.getItem(tblShift.getValue()).getBean();
			formShiftList.remove(removeShift);
			foamplanShiftResetFields();
			loadShiftRslt();
		}
	}
	
	private void deleteDetails() {
		FoamPlanDtlDM remove = new FoamPlanDtlDM();
		if (tblFormplanDtl.getValue() != null) {
			remove = beanFormPlanDtlDM.getItem(tblFormplanDtl.getValue()).getBean();
			FormPlanDtlList.remove(remove);
			foamplanDtlResetFields();
			loadPlanDtlRslt();
		}
	}
}
