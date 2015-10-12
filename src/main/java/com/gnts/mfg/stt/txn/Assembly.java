/**
 * File Name 		: Assembly.java 
 * Description 		: this class is used for add/edit Assembly details. 
 * Author 			: KAVITHA
 * Date 			: 07-October-2014
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * Version       Date           	Modified By               Remarks
 * 0.1          07-October-2014     KAVITHA	V M        Initial Version
 */
package com.gnts.mfg.stt.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.base.service.mst.SlnoGenService;
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
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.ShiftDM;
import com.gnts.hcm.service.mst.ShiftService;
import com.gnts.stt.mfg.domain.txn.AsmblyDtlDM;
import com.gnts.stt.mfg.domain.txn.AsmblyHdrDM;
import com.gnts.stt.mfg.domain.txn.AsmblyPlanDtlDM;
import com.gnts.stt.mfg.domain.txn.AsmblyPlanHdrDM;
import com.gnts.stt.mfg.domain.txn.AsmblyShiftDM;
import com.gnts.stt.mfg.service.txn.AsmblyDtlService;
import com.gnts.stt.mfg.service.txn.AsmblyHdrService;
import com.gnts.stt.mfg.service.txn.AsmblyPlanDtlService;
import com.gnts.stt.mfg.service.txn.AsmblyPlanHdrService;
import com.gnts.stt.mfg.service.txn.AsmblyShiftService;
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

public class Assembly extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	private AsmblyHdrService serviceAsmblyHdr = (AsmblyHdrService) SpringContextHelper.getBean("asmblyHdr");
	private AsmblyDtlService serviceAsmblyDtl = (AsmblyDtlService) SpringContextHelper.getBean("asmblyDtl");
	private AsmblyShiftService serviceAsmblyShift = (AsmblyShiftService) SpringContextHelper.getBean("asmblyShift");
	private AsmblyPlanHdrService serviceAsmblyPlanHrd = (AsmblyPlanHdrService) SpringContextHelper
			.getBean("AsmblyPlanHdr");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private SlnoGenService serviceSLNo = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private ShiftService serviceShift = (ShiftService) SpringContextHelper.getBean("Shift");
	private AsmblyPlanDtlService serviceAsmblyPlanDtl = (AsmblyPlanDtlService) SpringContextHelper
			.getBean("AsmblyPlanDtl");
	private List<AsmblyDtlDM> listAsmDtl = null;
	private List<AsmblyShiftDM> listAsmShift = null;
	private Button btnDelete = new GERPButton("Delete", "delete", this);
	private Button btnShiftdelete = new GERPButton("Delete", "delete", this);
	private BeanItemContainer<AsmblyHdrDM> beanAsmblyHdr = null;
	private BeanItemContainer<AsmblyDtlDM> beanAsmblyDtl = null;
	private BeanItemContainer<AsmblyShiftDM> beanAsmblyShift = null;
	private TextField tfAsmRefNo, tfPrdctnTotlQty, tfProductQty, tfAchievedQty, tfPlanRefNo;
	private TextArea taRemarks;
	private ComboBox cbPlanRefNo, cbHdrStatus, cbProductName, cbDtlStatus, cbEmployeeName, cbShiftStatus, cbShiftName;
	private DateField dfAsmDt;
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
	//
	private Table tblAsmDtl;
	//
	private Table tblAsmShift;
	private String userName;
	private Long asmbPlanId;
	private Long companyid, employeeId;
	private Long moduleId;
	private Long branchID, asmblyHdrId;
	private Logger logger = Logger.getLogger(Assembly.class);
	private GERPAddEditHLayout hlSearchLayout;
	private int recordCnt = 0;
	private int recordShiftCnt = 0;
	private int recordCntDtl = 0;
	private Button btnAsmShift = new GERPButton("Add", "add", this);
	private Button btnAsmDtl = new GERPButton("Add", "add", this);
	private String productName;
	
	public Assembly() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Inside Assembly() constructor");
		buildView();
	}
	
	private void buildView() {
		cbPlanRefNo = new GERPComboBox("Plan Ref No");
		cbPlanRefNo.setWidth("150");
		cbPlanRefNo.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbPlanRefNo.getValue() != null) {
					tfPrdctnTotlQty.setValue(serviceAsmblyPlanHrd
							.getAsmblyPlanHdrDetails(null, companyid, null, cbPlanRefNo.getValue().toString(), null,
									"Active", "F").get(0).getPlannedqty().toString());
				}
				loadProductList();
			}
		});
		loadAsmPlan();
		tfAsmRefNo = new TextField("Asm. Ref. No");
		tfAsmRefNo.setWidth("150");
		dfAsmDt = new PopupDateField("Asm. Date");
		dfAsmDt.setWidth("130");
		tfPrdctnTotlQty = new TextField("Production Total Qty.");
		tfPrdctnTotlQty.setWidth("150");
		taRemarks = new TextArea("Remarks");
		taRemarks.setWidth("150");
		taRemarks.setHeight("52");
		tfPlanRefNo = new TextField("Asm. Ref. No");
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbHdrStatus.setWidth("150");
		cbHdrStatus.setNullSelectionAllowed(false);
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		cbProductName = new GERPComboBox("Product Name");
		cbProductName.setItemCaptionPropertyId("prodName");
		cbProductName.setNullSelectionAllowed(false);
		cbProductName.setWidth("150");
		tfProductQty = new GERPTextField("Production Quantity");
		tfProductQty.setWidth("150");
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbDtlStatus.setWidth("120");
		cbDtlStatus.setNullSelectionAllowed(false);
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		btnAsmShift.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateShiftDetails()) {
					saveAsmShiftListDetails();
				}
			}
		});
		tblAsmShift = new Table();
		tblAsmShift.setHeight("136px");
		tblAsmShift.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblAsmShift.isSelected(event.getItemId())) {
					tblAsmShift.setImmediate(true);
					btnAsmShift.setCaption("Add");
					btnShiftdelete.setEnabled(false);
					btnAsmShift.setStyleName("savebt");
					asmblShitResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAsmShift.setCaption("Update");
					btnShiftdelete.setEnabled(true);
					btnAsmShift.setStyleName("savebt");
					editAsmblyShift();
				}
			}
		});
		btnDelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnDelete == event.getButton()) {
					deleteDetails();
					btnAsmDtl.setCaption("Add");
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
					btnAsmShift.setCaption("Add");
				}
			}
		});
		btnAsmDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (validateDtlDetails()) {
						saveDtlDetails();
					}
				}
				catch (Exception e) {
				}
			}
		});
		tblAsmDtl = new GERPTable();
		tblAsmDtl.setPageLength(5);
		tblAsmDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblAsmDtl.isSelected(event.getItemId())) {
					tblAsmDtl.setImmediate(true);
					btnAsmDtl.setCaption("Add");
					btnAsmDtl.setStyleName("savebt");
					asmblDtlResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAsmDtl.setCaption("Update");
					btnAsmDtl.setStyleName("savebt");
					editAsmblyDtls();
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
				tfAsmRefNo.setReadOnly(false);
			}
		});
		cbShiftName = new GERPComboBox("Shift Name");
		cbShiftName.setItemCaptionPropertyId("shiftName");
		loadShiftList();
		cbEmployeeName = new GERPComboBox("Employee Name");
		cbEmployeeName.setItemCaptionPropertyId("fullname");
		cbEmployeeName.setWidth("150");
		loadEmployeeList();
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Resetting the UI controls");
		cbShiftStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbShiftStatus.setWidth("120");
		cbShiftStatus.setNullSelectionAllowed(false);
		cbShiftStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		tblAsmShift.removeAllItems();
		tfAchievedQty = new GERPTextField("Achieved Quantity");
		tfAchievedQty.setWidth("120");
		tblAsmDtl.setWidth("100%");
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
		loadShiftRslt();
		loadDtlRslt();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol1.addComponent(cbPlanRefNo);
		flHdrCol1.addComponent(tfAsmRefNo);
		flHdrCol1.addComponent(dfAsmDt);
		flHdrCol1.addComponent(tfPrdctnTotlQty);
		flHdrCol1.addComponent(taRemarks);
		flHdrCol1.addComponent(cbHdrStatus);
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdrCol1);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding Shift Components
		flAsmShiftCol1 = new FormLayout();
		flAsmShiftCol2 = new FormLayout();
		flAsmShiftCol3 = new FormLayout();
		flAsmShiftCol1.addComponent(cbShiftName);
		flAsmShiftCol1.addComponent(cbEmployeeName);
		flAsmShiftCol2.addComponent(tfAchievedQty);
		flAsmShiftCol2.addComponent(cbShiftStatus);
		flAsmShiftCol3.addComponent(btnAsmShift);
		flAsmShiftCol3.addComponent(btnShiftdelete);
		flAsmShiftCol1.setSpacing(true);
		flAsmShiftCol2.setSpacing(true);
		flAsmShiftCol3.setSpacing(true);
		vlShift = new VerticalLayout();
		hlShift = new HorizontalLayout();
		hlShift.setWidth("990px");
		hlShift.addComponent(flAsmShiftCol1);
		hlShift.addComponent(flAsmShiftCol2);
		hlShift.addComponent(flAsmShiftCol3);
		vlShift.addComponent(hlShift);
		vlShift.addComponent(tblAsmShift);
		vlShift.setWidth("915px");
		hlShift.setSpacing(true);
		hlShift.setMargin(true);
		flDtlCol1 = new FormLayout();
		flDtlCol2 = new FormLayout();
		flDtlCol3 = new FormLayout();
		flDtlCol4 = new FormLayout();
		flDtlCol5 = new FormLayout();
		flDtlCol1.addComponent(cbProductName);
		flDtlCol2.addComponent(tfProductQty);
		flDtlCol3.addComponent(cbDtlStatus);
		flDtlCol4.addComponent(btnAsmDtl);
		flDtlCol5.addComponent(btnDelete);
		hlHdrslap = new HorizontalLayout();
		hlHdrslap.addComponent(flDtlCol1);
		hlHdrslap.addComponent(flDtlCol2);
		hlHdrslap.addComponent(flDtlCol3);
		hlHdrslap.addComponent(flDtlCol4);
		hlHdrslap.addComponent(flDtlCol5);
		hlHdrslap.setSpacing(true);
		hlHdrslap.setMargin(true);
		vlHdr = new VerticalLayout();
		vlHdr.addComponent(hlHdrslap);
		vlHdr.addComponent(tblAsmDtl);
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
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Assembling search layout");
		tfAsmRefNo.setReadOnly(false);
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		Label lbl = new Label();
		flHdrCol4.addComponent(tfPlanRefNo);
		flHdrCol1.addComponent(dfAsmDt);
		flHdrCol2.addComponent(lbl);
		flHdrCol3.addComponent(cbHdrStatus);
		hlSearchLayout.addComponent(flHdrCol4);
		hlSearchLayout.addComponent(flHdrCol1);
		hlSearchLayout.addComponent(flHdrCol2);
		hlSearchLayout.addComponent(flHdrCol3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
		tfAsmRefNo.setRequired(false);
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<AsmblyHdrDM> list = new ArrayList<AsmblyHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Search Parameters are "
					+ companyid + ", " + cbPlanRefNo.getValue() + ", " + cbHdrStatus.getValue());
			list = serviceAsmblyHdr.getAsmblyHdrDMs(null, null, (String) tfPlanRefNo.getValue(), dfAsmDt.getValue(),
					null, (String) cbHdrStatus.getValue(), "F");
			recordCnt = list.size();
			beanAsmblyHdr = new BeanItemContainer<AsmblyHdrDM>(AsmblyHdrDM.class);
			beanAsmblyHdr.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Got the AssemblyHdr. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanAsmblyHdr);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "asmblyid", "asmrefno", "asmdate", "asmPlanRefno",
					"prodtntotqty", "asmstauts", "lastupdateddate", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Asm.Ref.No", "Asm. Date",
					"Plan Reference No.", "Production Total Qty.", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("asmblyid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadDtlRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Loading Search...");
			logger.info("Company ID : " + companyid + " | saveasmDtlListDetails User Name : " + userName + " > "
					+ "Search Parameters are " + companyid + ", " + tfProductQty.getValue() + ", "
					+ (String) cbDtlStatus.getValue());
			recordCntDtl = listAsmDtl.size();
			beanAsmblyDtl = new BeanItemContainer<AsmblyDtlDM>(AsmblyDtlDM.class);
			beanAsmblyDtl.addAll(listAsmDtl);
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Got the AssemblyDtls. result set");
			tblAsmDtl.setContainerDataSource(beanAsmblyDtl);
			tblAsmDtl.setVisibleColumns(new Object[] { "prodName", "prodtnQty", "asmdtlStatus", "lastUpdatedDate",
					"lastUpdatedBy" });
			tblAsmDtl.setColumnHeaders(new String[] { "Product Name", "Production Quantity", "Status",
					"Last Updated Date", "Last Updated By" });
			tblAsmDtl.setColumnAlignment("asmDtlId", Align.RIGHT);
			tblAsmDtl.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCntDtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadShiftRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + " > " + "Loading Search...");
			recordShiftCnt = listAsmShift.size();
			beanAsmblyShift = new BeanItemContainer<AsmblyShiftDM>(AsmblyShiftDM.class);
			beanAsmblyShift.addAll(listAsmShift);
			logger.info("Company ID : " + companyid + " | User Name : " + " > " + "Got the AssemblyPlan. result set");
			tblAsmShift.setContainerDataSource(beanAsmblyShift);
			tblAsmShift.setVisibleColumns(new Object[] { "shiftName", "empName", "achivedQty", "shiftStatus",
					"lastUpdatedDate", "lastUpdatedBy" });
			tblAsmShift.setColumnHeaders(new String[] { "Shift Name", "EmployeeName", "Achieved Quantity", "Status",
					"Last Updated Date", "Last Updated By" });
			tblAsmShift.setColumnAlignment("asmShftId", Align.RIGHT);
			tblAsmShift.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordShiftCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadEmployeeList() {
		try {
			BeanItemContainer<EmployeeDM> beanEmployeeDM = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
			beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, employeeId,
					null, null, null, "P"));
			cbEmployeeName.setContainerDataSource(beanEmployeeDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadShiftList() {
		try {
			BeanItemContainer<ShiftDM> beanShiftDM = new BeanItemContainer<ShiftDM>(ShiftDM.class);
			beanShiftDM.addAll(serviceShift.getShiftList(null, null, companyid, "Active", "F"));
			cbShiftName.setContainerDataSource(beanShiftDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadProductList() {
		try {
			String planId = serviceAsmblyPlanHrd
					.getAsmblyPlanHdrDetails(null, companyid, null, cbPlanRefNo.getValue().toString(), null, "Active",
							"F").get(0).getAsmplnid();
			BeanItemContainer<AsmblyPlanDtlDM> beanProductDM = new BeanItemContainer<AsmblyPlanDtlDM>(
					AsmblyPlanDtlDM.class);
			beanProductDM.addAll(serviceAsmblyPlanDtl.getAsmPlnDtlList(null, Long.valueOf(planId), null, null, null,
					null, "F"));
			cbProductName.setContainerDataSource(beanProductDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadAsmPlan() {
		try {
			BeanContainer<Long, AsmblyPlanHdrDM> beanAsmPlanHdr = new BeanContainer<Long, AsmblyPlanHdrDM>(
					AsmblyPlanHdrDM.class);
			beanAsmPlanHdr.setBeanIdProperty("asmplnreffno");
			beanAsmPlanHdr.addAll(serviceAsmblyPlanHrd.getAsmblyPlanHdrDetails(null, companyid, null, null, null, null,
					"F"));
			cbPlanRefNo.setContainerDataSource(beanAsmPlanHdr);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Resetting the UI controls");
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		dfAsmDt.setValue(null);
		dfAsmDt.setComponentError(null);
		tfPlanRefNo.setValue("");
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		resetFields();
		loadDtlRslt();
		loadShiftRslt();
		assembleInputUserLayout();
		dfAsmDt.setReadOnly(false);
		dfAsmDt.setValue(new Date());
		cbPlanRefNo.setRequired(true);
		tfPrdctnTotlQty.setRequired(true);
		dfAsmDt.setRequired(true);
		cbProductName.setRequired(true);
		tfProductQty.setRequired(true);
		cbShiftName.setRequired(true);
		cbEmployeeName.setRequired(true);
		tfAchievedQty.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAsmDtl.setCaption("Add");
		tblAsmDtl.setVisible(true);
		try {
			tfAsmRefNo.setReadOnly(false);
			SlnoGenDM slnoObj = serviceSLNo.getSequenceNumber(companyid, branchID, moduleId, "STT_ASMNO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfAsmRefNo.setValue(slnoObj.getKeyDesc());
				tfAsmRefNo.setReadOnly(true);
			} else {
				tfAsmRefNo.setReadOnly(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		tblAsmDtl.setVisible(true);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		cbPlanRefNo.setRequired(true);
		dfAsmDt.setRequired(true);
		cbProductName.setRequired(true);
		tfProductQty.setRequired(true);
		cbShiftName.setRequired(true);
		cbEmployeeName.setRequired(true);
		tfAchievedQty.setRequired(true);
		tblMstScrSrchRslt.setVisible(false);
		if (tfAsmRefNo.getValue() == null || tfAsmRefNo.getValue().trim().length() == 0) {
			tfAsmRefNo.setReadOnly(false);
		}
		assembleInputUserLayout();
		resetFields();
		editAsmblyDetails();
		editAsmblyDtls();
		editAsmblyShift();
	}
	
	protected void editAsmblyDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Editing the selected record");
			hlUserInputLayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				AsmblyHdrDM editAssembly = beanAsmblyHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
				asmblyHdrId = Long.valueOf(editAssembly.getAsmblyid());
				logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
						+ "Selected Assembly. Id -> " + asmblyHdrId);
				cbPlanRefNo.setValue(editAssembly.getAsmplnid().toString());
				tfAsmRefNo.setReadOnly(false);
				tfAsmRefNo.setValue(editAssembly.getAsmrefno());
				tfAsmRefNo.setReadOnly(true);
				if (editAssembly.getAsmdate() != null) {
					dfAsmDt.setValue(editAssembly.getAsmplndate1());
				}
				tfPrdctnTotlQty.setValue(editAssembly.getProdtntotqty().toString());
				taRemarks.setValue(editAssembly.getRemarks());
				cbHdrStatus.setValue(editAssembly.getAsmstauts());
				listAsmDtl.addAll(serviceAsmblyDtl.getAsmblyDtlList(null, asmblyHdrId, null, null,
						(String) cbDtlStatus.getValue(), "F"));
				listAsmShift.addAll(serviceAsmblyShift.getAsmblyShftList(null, asmblyHdrId, null, null,
						(String) cbShiftStatus.getValue(), "F"));
			}
			loadDtlRslt();
			loadShiftRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	protected void editAsmblyDtls() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblAsmDtl.getValue() != null) {
				AsmblyDtlDM asmblyDtlDM = new AsmblyDtlDM();
				asmblyDtlDM = beanAsmblyDtl.getItem(tblAsmDtl.getValue()).getBean();
				Long prodId = asmblyDtlDM.getProductId();
				Collection<?> prodIdCol = cbProductName.getItemIds();
				for (Iterator<?> iterator = prodIdCol.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbProductName.getItem(itemId);
					// Get the actual bean and use the data
					ProductDM st = (ProductDM) item.getBean();
					if (prodId != null && prodId.equals(st.getProdid())) {
						cbProductName.setValue(itemId);
					}
					if (asmblyDtlDM.getProdtnQty() != null) {
						tfProductQty.setValue(asmblyDtlDM.getProdtnQty().toString());
					}
					if (asmblyDtlDM.getAsmdtlStatus() != null) {
						cbDtlStatus.setValue(asmblyDtlDM.getAsmdtlStatus());
					}
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	protected void editAsmblyShift() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblAsmShift.getValue() != null) {
				AsmblyShiftDM asmblyShiftDM = new AsmblyShiftDM();
				asmblyShiftDM = beanAsmblyShift.getItem(tblAsmShift.getValue()).getBean();
				Long empId = asmblyShiftDM.getEmpId();
				Collection<?> empColId = cbEmployeeName.getItemIds();
				for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
					Object itemIdClient = (Object) iteratorclient.next();
					BeanItem<?> itemclient = (BeanItem<?>) cbEmployeeName.getItem(itemIdClient);
					// Get the actual bean and use the data
					EmployeeDM empObj = (EmployeeDM) itemclient.getBean();
					if (empId != null && empId.equals(empObj.getEmployeeid())) {
						cbEmployeeName.setValue(itemIdClient);
					}
				}
				if (asmblyShiftDM.getShiftName() != null) {
					cbShiftName.setValue(asmblyShiftDM.getShiftName());
				}
				if (asmblyShiftDM.getAchivedQty() != null) {
					tfAchievedQty.setValue(asmblyShiftDM.getAchivedQty().toString());
				}
				if (asmblyShiftDM.getShiftStatus() != null) {
					cbShiftStatus.setValue(asmblyShiftDM.getShiftStatus());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbPlanRefNo.setComponentError(null);
		tfPrdctnTotlQty.setComponentError(null);
		dfAsmDt.setComponentError(null);
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if (cbPlanRefNo.getValue() == null) {
			cbPlanRefNo.setComponentError(new UserError(GERPErrorCodes.NULL_PLANNED_QTY));
			errorFlag = true;
		}
		Long prdctnTotlQty;
		try {
			prdctnTotlQty = Long.valueOf(tfPrdctnTotlQty.getValue());
			if (prdctnTotlQty < 0) {
				tfPrdctnTotlQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorFlag = true;
			}
		}
		catch (Exception e) {
			tfPrdctnTotlQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			errorFlag = true;
		}
		if (dfAsmDt.getValue() == null) {
			dfAsmDt.setComponentError(new UserError(GERPErrorCodes.NULL_ASMBL_DT));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validateDtlDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Validating Data ");
		Long productQty;
		try {
			productQty = Long.valueOf(tfProductQty.getValue());
			if (productQty < 0) {
				tfProductQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfProductQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		if (cbProductName.getValue() == null) {
			cbProductName.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + cbProductName.getValue());
			isValid = false;
		} else {
			cbProductName.setComponentError(null);
		}
		return isValid;
	}
	
	private boolean validateShiftDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Validating Data ");
		if ((cbShiftName.getValue() == "")) {
			cbShiftName.setComponentError(new UserError(GERPErrorCodes.NULL_ASMBL_SHIFT));
			logger.warn("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + cbShiftName.getValue());
			isValid = false;
		} else {
			cbShiftName.setComponentError(null);
		}
		if ((cbEmployeeName.getValue() == null)) {
			cbEmployeeName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + cbEmployeeName.getValue());
			isValid = false;
		} else {
			cbEmployeeName.setComponentError(null);
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfAchievedQty.getValue());
			if (achievedQty < 0) {
				tfAchievedQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfAchievedQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			System.out.println("====================================>1:Am in save detais");
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Saving Data... ");
			AsmblyHdrDM asmblyHdr = new AsmblyHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				asmblyHdr = beanAsmblyHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			asmblyHdr.setAsmrefno(tfAsmRefNo.getValue());
			asmblyHdr.setAsmplnid(Long.valueOf(cbPlanRefNo.getValue().toString()));
			asmblyHdr.setAsmdate(dfAsmDt.getValue());
			asmblyHdr.setProdtntotqty(Long.valueOf(tfPrdctnTotlQty.getValue()));
			asmblyHdr.setRemarks(taRemarks.getValue().toString());
			asmblyHdr.setAsmstauts((String) cbHdrStatus.getValue());
			asmblyHdr.setLastupdateddate(DateUtils.getcurrentdate());
			asmblyHdr.setLastupdatedby(userName);
			serviceAsmblyHdr.saveAndupdateAsmblyHdr(asmblyHdr);
			@SuppressWarnings("unchecked")
			Collection<AsmblyShiftDM> itemIds = (Collection<AsmblyShiftDM>) tblAsmShift.getVisibleItemIds();
			for (AsmblyShiftDM save : (Collection<AsmblyShiftDM>) itemIds) {
				save.setAsmId(Long.valueOf(asmblyHdr.getAsmblyid().toString()));
				serviceAsmblyShift.saveAndUpdate(save);
			}
			@SuppressWarnings("unchecked")
			Collection<AsmblyDtlDM> itemIds1 = (Collection<AsmblyDtlDM>) tblAsmDtl.getVisibleItemIds();
			for (AsmblyDtlDM saveDtl : (Collection<AsmblyDtlDM>) itemIds1) {
				saveDtl.setAsmId(Long.valueOf(asmblyHdr.getAsmblyid().toString()));
				serviceAsmblyDtl.saveAndUpdate(saveDtl);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSLNo.getSequenceNumber(companyid, branchID, moduleId, "STT_ASMNO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSLNo.updateNextSequenceNumber(companyid, branchID, moduleId, "STT_ASMNO");
					}
				}
				catch (Exception e) {
				}
			}
			asmblDtlResetFields();
			asmblShitResetFields();
			resetFields();
			loadSrchRslt();
			asmbPlanId = 0L;
			loadDtlRslt();
			loadShiftRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void saveDtlDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Saving Data... ");
			AsmblyDtlDM assemblyDtlObj = new AsmblyDtlDM();
			ProductDM productDM = serviceProduct.getProductList(companyid, null, null, productName, "Active", null,
					null, "F").get(0);
			if (tblAsmDtl.getValue() != null) {
				assemblyDtlObj = beanAsmblyDtl.getItem(tblAsmDtl.getValue()).getBean();
				listAsmDtl.remove(assemblyDtlObj);
			}
			if (cbProductName.getValue() != null) {
				assemblyDtlObj.setProductId(productDM.getProdid());
				assemblyDtlObj.setProdName(productDM.getProdname());
			}
			if (tfProductQty.getValue() != null) {
				assemblyDtlObj.setProdtnQty((Long.valueOf(tfProductQty.getValue().trim().length())));
			}
			if (cbDtlStatus.getValue() != null) {
				assemblyDtlObj.setAsmdtlStatus((String) cbDtlStatus.getValue());
			}
			assemblyDtlObj.setLastUpdatedDate(DateUtils.getcurrentdate());
			assemblyDtlObj.setLastUpdatedBy(userName);
			listAsmDtl.add(assemblyDtlObj);
			loadDtlRslt();
			btnAsmDtl.setCaption("Add");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		asmblDtlResetFields();
	}
	
	private void saveAsmShiftListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Saving Data... ");
			AsmblyShiftDM assemblyShiftObj = new AsmblyShiftDM();
			if (tblAsmShift.getValue() != null) {
				assemblyShiftObj = beanAsmblyShift.getItem(tblAsmShift.getValue()).getBean();
				listAsmShift.remove(assemblyShiftObj);
			}
			assemblyShiftObj.setShiftName(cbShiftName.getValue().toString());
			if (cbEmployeeName.getValue() != null) {
				assemblyShiftObj.setEmpId(((EmployeeDM) cbEmployeeName.getValue()).getEmployeeid());
				assemblyShiftObj.setEmpName(((EmployeeDM) cbEmployeeName.getValue()).getFirstname());
			}
			if (tfAchievedQty.getValue() != null) {
				assemblyShiftObj.setAchivedQty(Long.valueOf(tfAchievedQty.getValue().toString()));
			}
			if (cbShiftStatus.getValue() != null) {
				assemblyShiftObj.setShiftStatus((String) cbShiftStatus.getValue());
			}
			assemblyShiftObj.setLastUpdatedDate(DateUtils.getcurrentdate());
			assemblyShiftObj.setLastUpdatedBy(userName);
			listAsmShift.add(assemblyShiftObj);
			loadShiftRslt();
			asmbPlanId = 0L;
			btnAsmShift.setCaption("Add");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		asmblShitResetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > "
				+ "Getting audit record for Assembly. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_WORKORDER_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", asmbPlanId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		cbPlanRefNo.setComponentError(null);
		dfAsmDt.setComponentError(null);
		tfAsmRefNo.setComponentError(null);
		tfPrdctnTotlQty.setComponentError(null);
		taRemarks.setComponentError(null);
		cbProductName.setComponentError(null);
		tfProductQty.setComponentError(null);
		cbShiftName.setComponentError(null);
		cbEmployeeName.setComponentError(null);
		tfAchievedQty.setComponentError(null);
		cbPlanRefNo.setRequired(false);
		tfPrdctnTotlQty.setRequired(false);
		dfAsmDt.setRequired(false);
		tfPrdctnTotlQty.setRequired(false);
		taRemarks.setRequired(false);
		cbProductName.setRequired(false);
		tfProductQty.setRequired(false);
		cbShiftName.setRequired(false);
		cbEmployeeName.setRequired(false);
		tfAchievedQty.setRequired(false);
		tblAsmDtl.removeAllItems();
		tblAsmShift.removeAllItems();
		asmblDtlResetFields();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		tfAchievedQty.setComponentError(null);
		cbPlanRefNo.setValue(null);
		cbPlanRefNo.setComponentError(null);
		tfPrdctnTotlQty.setComponentError(null);
		tfAsmRefNo.setReadOnly(false);
		tfAsmRefNo.setValue("");
		tfAsmRefNo.setReadOnly(true);
		tfAsmRefNo.setComponentError(null);
		dfAsmDt.setComponentError(null);
		dfAsmDt.setValue(null);
		tfPrdctnTotlQty.setValue("0");
		tfProductQty.setValue("0");
		tfAchievedQty.setValue("0");
		tfPlanRefNo.setValue("");
		taRemarks.setValue("");
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		listAsmDtl = new ArrayList<AsmblyDtlDM>();
		listAsmShift = new ArrayList<AsmblyShiftDM>();
		tblAsmDtl.removeAllItems();
		tblAsmShift.removeAllItems();
		recordCnt = 0;
		recordCntDtl = 0;
	}
	
	protected void asmblDtlResetFields() {
		cbProductName.setValue(null);
		cbProductName.setComponentError(null);
		tfProductQty.setValue("0");
		tfProductQty.setComponentError(null);
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
	}
	
	protected void asmblShitResetFields() {
		cbEmployeeName.setValue(null);
		cbEmployeeName.setComponentError(null);
		cbShiftName.setValue("");
		cbShiftName.setComponentError(null);
		tfAchievedQty.setValue("0");
		tfAchievedQty.setComponentError(null);
		cbShiftStatus.setValue(cbShiftStatus.getItemIds().iterator().next());
	}
	
	private void deleteDetails() {
		try {
			AsmblyDtlDM save = new AsmblyDtlDM();
			if (tblAsmDtl.getValue() != null) {
				save = beanAsmblyDtl.getItem(tblAsmDtl.getValue()).getBean();
				listAsmDtl.remove(save);
				asmblDtlResetFields();
				loadDtlRslt();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deleteShiftDetails() {
		try {
			AsmblyShiftDM removeShift = new AsmblyShiftDM();
			if (tblAsmShift.getValue() != null) {
				removeShift = beanAsmblyShift.getItem(tblAsmShift.getValue()).getBean();
				listAsmShift.remove(removeShift);
				asmblShitResetFields();
				loadShiftRslt();
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
