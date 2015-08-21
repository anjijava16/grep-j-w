/**
 * File Name	:	WorkOrder.java
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

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
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
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.txn.WorkOrderDtlDM;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.service.txn.WorkOrderDtlService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsPODtlDM;
import com.gnts.sms.domain.txn.SmsPOHdrDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsPODtlService;
import com.gnts.sms.service.txn.SmsPOHdrService;
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
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class WorkOrder extends BaseTransUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WorkOrderDtlService serviceWrkOrdDtl = (WorkOrderDtlService) SpringContextHelper.getBean("workOrderDtl");
	private WorkOrderHdrService serviceWrkOrdHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private SmsEnqHdrService serviceEnquiryHdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ClientService serviceClient = (ClientService) SpringContextHelper.getBean("clients");
	private SmsPOHdrService servicePurchaseOrdHdr = (SmsPOHdrService) SpringContextHelper.getBean("smspohdr");
	private SmsPODtlService servicesmspodtl = (SmsPODtlService) SpringContextHelper.getBean("SmsPODtl");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private VerticalLayout vlCommendForm = new VerticalLayout();
	// form layout for input controls for Work Order Header
	private FormLayout flOdrHdrColumn1, flOdrHdrColumn2, flOdrHdrColumn3, flOdrHdrColumn4, flOdrHdrColumn5;
	// form layout for input controls for Work Order Details
	private FormLayout flOdrDtlColumn1, flOdrDtlColumn2, flOdrDtlColumn3, flOdrDtlColumn4;
	// // User Input Components for Work Order Details
	private ComboBox cbPrdName;
	private TextField tfPrdName, tfWrkOdrQty, tfPlnQty, tfBalQty;
	private TextArea taWrkOdrDtlRmks;
	private PopupDateField delvrySchdDt;
	private Table tblWrkOdrDtl;
	private Button btnsaveWrkOdrDtl;
	// User Input Components for Work Order Header
	private TextField tfPlanRefNo, tfEnquiryNumber;
	private TextArea taWrkOdrHdrRemarks;
	private PopupDateField wrkOdrDate;
	private OptionGroup workordtype = new OptionGroup("");
	private OptionGroup opPONumbers = new OptionGroup("");
	private ComboBox cbBranchName, cbClientName, cbwrkOdrType, cbPONumber, cbWorkderStatus, cbEnquiryNumber;
	// BeanItem container of TestGroupDM
	private BeanItemContainer<WorkOrderHdrDM> beanWrkOdrHdr = null;
	private BeanItemContainer<WorkOrderDtlDM> beanWrkOdrDtl = new BeanItemContainer<WorkOrderDtlDM>(
			WorkOrderDtlDM.class);
	private List<WorkOrderDtlDM> listWODetails;
	// local variables declaration
	private String username;
	private Long companyid, employeeId, moduleId, branchID, appScreenId, roleId;
	private int recordCnt, recordDtlCount;
	private Long wrkOdrHdrId;
	private OptionGroup ogPriority = new OptionGroup("Priority");
	private OptionGroup ogMaterialUsage = new OptionGroup("Material Usage");
	// Search control layout
	private GERPAddEditHLayout hlSearchLayout;
	// UserInput control layout
	private HorizontalLayout hlUserInputLayout = new GERPAddEditHLayout();
	private HorizontalLayout hlOdrDtl, hlWrkOdrHdr;
	private VerticalLayout vlWrkOdrComp, vlWorkOdrHdl;
	// Initialize logger
	private Logger logger = Logger.getLogger(WorkOrder.class);
	private Comments comment;
	private Long commentby;
	private String name = "";
	private Button btnDtlWrkOrd = new GERPButton("Delete", "delete", this);
	private GERPTextField tfCustomField1 = new GERPTextField("Part Number");
	private GERPTextField tfCustomField2 = new GERPTextField("Drawing Number");
	
	// Constructor received the parameters from Login UI class
	public WorkOrder() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = 13L;
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside WorkOrder() constructor");
		commentby = (Long) UI.getCurrent().getSession().getAttribute("employeeid");
		// Loading the WorkOrder UI
		buildView();
	}
	
	private void buildView() {
		workordtype.addItems("Self", "Enquiry");
		workordtype.setValue("Enquiry");
		workordtype.setImmediate(true);
		workordtype.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				changeworkorderstatus();
			}
		});
		cbwrkOdrType = new GERPComboBox("Work Order Type");
		cbwrkOdrType.addItem("Job Work Order");
		cbwrkOdrType.addItem("Sample Work Order");
		cbwrkOdrType.addItem("Mold Manufacturing");
		cbwrkOdrType.setWidth("150");
		cbwrkOdrType.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbwrkOdrType.getValue() != null) {
					loadworkordertypeAutogen();
				}
			}
		});
		opPONumbers.addItems("LOI", "PO");
		opPONumbers.setStyleName("displayblock");
		opPONumbers.setValue("PO");
		opPONumbers.setImmediate(true);
		opPONumbers.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				changeworkorderPOstatus();
			}
		});
		// Initialization for work order Details user input components
		btnsaveWrkOdrDtl = new GERPButton("Add", "add", this);
		btnsaveWrkOdrDtl.setVisible(true);
		btnsaveWrkOdrDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateWorkOrdDtls()) {
					saveWorkOdrDtlDetails();
				}
			}
		});
		btnDtlWrkOrd.setEnabled(false);
		btnDtlWrkOrd.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteTstDefDetails();
			}
		});
		tblWrkOdrDtl = new GERPTable();
		tblWrkOdrDtl.setPageLength(8);
		tblWrkOdrDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblWrkOdrDtl.isSelected(event.getItemId())) {
					tblWrkOdrDtl.setImmediate(true);
					btnsaveWrkOdrDtl.setCaption("Add");
					btnsaveWrkOdrDtl.setStyleName("savebt");
					resetWorkOrdDtl();
					btnDtlWrkOrd.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnsaveWrkOdrDtl.setCaption("Update");
					btnsaveWrkOdrDtl.setStyleName("savebt");
					editWorkOrderDtlDetails();
					btnDtlWrkOrd.setEnabled(true);
				}
			}
		});
		tfPrdName = new GERPTextField("Cust.Product Name");
		if (tfPrdName.getValue() != "") {
			tfPrdName.setReadOnly(true);
		} else {
			tfPrdName.setReadOnly(false);
		}
		tfBalQty = new GERPTextField("Work Order Balance Qty.");
		tfPlnQty = new GERPTextField("Work Order Plan Qty.");
		tfBalQty.setReadOnly(true);
		tfWrkOdrQty = new GERPTextField("Ordered Qty.");
		tfWrkOdrQty.setReadOnly(true);
		tfWrkOdrQty.setImmediate(true);
		tfWrkOdrQty.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				Long balv = (Long.valueOf(tfWrkOdrQty.getValue()) - Long.valueOf(tfPlnQty.getValue()));
				Long wrkQty = Long.valueOf(tfWrkOdrQty.getValue());
				Long plnQty = Long.valueOf(tfPlnQty.getValue());
				if (plnQty <= wrkQty) {
					tfBalQty.setReadOnly(false);
					tfBalQty.setValue(balv.toString());
					tfBalQty.setReadOnly(true);
				}
			}
		});
		tfPlnQty.setImmediate(true);
		tfPlnQty.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				Long balv = (Long.valueOf(tfWrkOdrQty.getValue()) - Long.valueOf(tfPlnQty.getValue()));
				Long wrkQty = Long.valueOf(tfWrkOdrQty.getValue());
				Long plnQty = Long.valueOf(tfPlnQty.getValue());
				if (Long.valueOf(tfWrkOdrQty.getValue()) < Long.valueOf(tfPlnQty.getValue())) {
					tfPlnQty.setComponentError(new UserError("Quantity should not greater than Work order"));
				} else {
					tfPlnQty.setComponentError(null);
				}
				if (plnQty <= wrkQty) {
					tfBalQty.setReadOnly(false);
					tfBalQty.setValue(balv.toString());
					tfBalQty.setReadOnly(true);
				}
			}
		});
		taWrkOdrDtlRmks = new GERPTextArea("Work Order Remarks");
		taWrkOdrDtlRmks.setWidth("150");
		taWrkOdrDtlRmks.setHeight("50");
		delvrySchdDt = new GERPPopupDateField("Delivery Schd.Date");
		delvrySchdDt.setWidth("133");
		delvrySchdDt.setImmediate(true);
		// Initialization for work order header user input components
		wrkOdrDate = new GERPPopupDateField("Work Order Date ");
		wrkOdrDate.setWidth("121");
		taWrkOdrHdrRemarks = new GERPTextArea("Remarks ");
		taWrkOdrHdrRemarks.setWidth("130");
		taWrkOdrHdrRemarks.setHeight("50");
		tfEnquiryNumber = new GERPTextField("Enquiry No");
		tfEnquiryNumber.setWidth("180");
		tfPlanRefNo = new GERPTextField("Work Order No.");
		tfPlanRefNo.setWidth("150");
		tfPlanRefNo.setMaxLength(10);
		try {
			ApprovalSchemaDM obj = serviceWrkOrdHdr.getReviewerId(companyid, appScreenId, branchID, roleId).get(0);
			name = obj.getApprLevel();
			if (name.equals("Reviewer")) {
				cbWorkderStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR,
						BASEConstants.WO_RV_STATUS);
			} else {
				cbWorkderStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR,
						BASEConstants.WO_AP_STATUS);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		/* TO DO : Check - Login user is approver or not */
		if (name.equals("Approver")) {
			// btnAdd.setVisible(false);
			btnAdd.setVisible(true);
		} else {
			btnAdd.setVisible(true);
		}
		cbBranchName = new GERPComboBox("Branch Name");
		cbBranchName.setWidth("150");
		cbBranchName.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbEnquiryNumber = new ComboBox("Enquiry No.");
		cbEnquiryNumber = new ComboBox("Enquiry No");
		cbEnquiryNumber.setItemCaptionPropertyId("enquiryNo");
		cbEnquiryNumber.setRequired(true);
		loadEnquiryNo();
		cbEnquiryNumber.setImmediate(true);
		cbEnquiryNumber.setWidth("150px");
		cbEnquiryNumber.setImmediate(true);
		cbEnquiryNumber.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnquiryNumber.getItem(itemId);
				if (item != null) {
					loadClientList();
					loadPurchaseOrdNo();
				}
			}
		});
		cbClientName = new GERPComboBox("Client Code");
		cbClientName.setItemCaptionPropertyId("clientCode");
		cbClientName.setWidth("140");
		cbPONumber = new GERPComboBox("Purchase Order No.");
		cbPONumber.setWidth("140");
		cbPONumber.setItemCaptionPropertyId("pono");
		cbPONumber.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadPurchseOrdDtlList();
					try {
						SmsPOHdrDM purchaseOrderHdrDM = servicePurchaseOrdHdr
								.getSmspohdrList(null, Long.valueOf(cbPONumber.getValue().toString()), null, null,
										null, null, null, "F", null).get(0);
						if (purchaseOrderHdrDM.getDutyexempted().equals("Y")) {
							ogMaterialUsage.setValue("Exempted");
						} else {
							ogMaterialUsage.setValue("Non Exempted");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		cbPrdName = new GERPComboBox("Product Name");
		cbPrdName.setItemCaptionPropertyId("prodname");
		cbPrdName.setWidth("150");
		cbPrdName.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					try {
						SmsPODtlDM obj = (SmsPODtlDM) cbPrdName.getValue();
						tfWrkOdrQty.setReadOnly(false);
						Long orderremqty = obj.getPoqty() - obj.getInvoicedqty();
						tfWrkOdrQty.setValue(orderremqty.toString());
						tfWrkOdrQty.setReadOnly(true);
						System.out.println("((SmsPODtlDM) cbPrdName.getValue()).getCustomField1()"
								+ ((SmsPODtlDM) cbPrdName.getValue()).getCustomField1());
						if (((SmsPODtlDM) cbPrdName.getValue()).getCustomField1() != null) {
							tfCustomField1.setValue(((SmsPODtlDM) cbPrdName.getValue()).getCustomField1());
						} else {
							tfCustomField1.setValue("");
						}
						System.out.println("((SmsPODtlDM) cbPrdName.getValue()).getCustomField2()"
								+ ((SmsPODtlDM) cbPrdName.getValue()).getCustomField2());
						if (((SmsPODtlDM) cbPrdName.getValue()).getCustomField2() != null) {
							tfCustomField2.setValue(((SmsPODtlDM) cbPrdName.getValue()).getCustomField2());
						} else {
							tfCustomField2.setValue("");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		ogPriority.addItems("Normal", "Urgent");
		ogPriority.setValue("Normal");
		ogPriority.setRequired(true);
		ogMaterialUsage.addItems("Exempted", "Non Exempted");
		ogMaterialUsage.setValue("Exempted");
		ogMaterialUsage.setRequired(true);
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
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		resetWorkOrdDtl();
		assembleSearchLayout();
		loadSrchRslt();
		loadSrchWrkOdrDtlRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for work order UI search layout
		tfPlanRefNo.setReadOnly(false);
		hlSearchLayout.removeAllComponents();
		flOdrHdrColumn1 = new GERPFormLayout();
		flOdrHdrColumn2 = new GERPFormLayout();
		flOdrHdrColumn3 = new GERPFormLayout();
		flOdrHdrColumn4 = new GERPFormLayout();
		flOdrHdrColumn5 = new GERPFormLayout();
		// Adding components into form layouts for WorkOrder UI search layout
		flOdrHdrColumn1.addComponent(tfPlanRefNo);
		flOdrHdrColumn2.addComponent(cbwrkOdrType);
		flOdrHdrColumn3.addComponent(cbBranchName);
		flOdrHdrColumn4.addComponent(cbClientName);
		flOdrHdrColumn5.addComponent(cbWorkderStatus);
		// Adding form layouts into search layout for WorkOrder UI search mode
		hlSearchLayout.addComponent(flOdrHdrColumn1);
		hlSearchLayout.addComponent(flOdrHdrColumn2);
		hlSearchLayout.addComponent(flOdrHdrColumn3);
		hlSearchLayout.addComponent(flOdrHdrColumn4);
		hlSearchLayout.addComponent(flOdrHdrColumn5);
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
		flOdrHdrColumn3 = new FormLayout();
		flOdrHdrColumn4 = new FormLayout();
		cbBranchName.setRequired(true);
		cbClientName.setRequired(true);
		cbPONumber.setRequired(true);
		wrkOdrDate.setRequired(true);
		cbwrkOdrType.setRequired(true);
		cbPrdName.setRequired(true);
		tfPrdName.setRequired(true);
		tfPlnQty.setRequired(true);
		tfBalQty.setRequired(true);
		delvrySchdDt.setRequired(true);
		// adding components into first column in form layout1
		flOdrHdrColumn1.addComponent(tfPlanRefNo);
		flOdrHdrColumn1.addComponent(cbwrkOdrType);
		flOdrHdrColumn1.addComponent(workordtype);
		flOdrHdrColumn1.addComponent(cbBranchName);
		flOdrHdrColumn1.addComponent(cbEnquiryNumber);
		flOdrHdrColumn2.addComponent(cbClientName);
		flOdrHdrColumn2.addComponent(opPONumbers);
		flOdrHdrColumn2.addComponent(cbPONumber);
		// adding components into second column in form layout2
		flOdrHdrColumn3.addComponent(wrkOdrDate);
		// adding components into third column in form layout3
		flOdrHdrColumn3.addComponent(taWrkOdrHdrRemarks);
		// adding components into third column in form layout4
		flOdrHdrColumn4.addComponent(ogPriority);
		flOdrHdrColumn4.addComponent(ogMaterialUsage);
		flOdrHdrColumn4.addComponent(cbWorkderStatus);
		// adding components into fourth column in form layout3
		flOdrDtlColumn1 = new FormLayout();
		flOdrDtlColumn2 = new FormLayout();
		flOdrDtlColumn3 = new FormLayout();
		flOdrDtlColumn4 = new FormLayout();
		// adding components into first column in form layout1
		// Initialization for work order Details user input components
		flOdrDtlColumn1.addComponent(cbPrdName);
		// adding components into second column in form layout2
		// flOdrDtlColumn1.addComponent(tfPrdName);
		flOdrDtlColumn1.addComponent(tfWrkOdrQty);
		flOdrDtlColumn1.addComponent(tfPlnQty);
		flOdrDtlColumn2.addComponent(tfBalQty);
		flOdrDtlColumn3.addComponent(tfCustomField2);
		flOdrDtlColumn3.addComponent(taWrkOdrDtlRmks);
		flOdrDtlColumn2.addComponent(delvrySchdDt);
		flOdrDtlColumn2.addComponent(tfCustomField1);
		flOdrDtlColumn4.addComponent(btnsaveWrkOdrDtl);
		flOdrDtlColumn4.addComponent(btnDtlWrkOrd);
		// adding form layouts into user input layouts
		hlWrkOdrHdr = new HorizontalLayout();
		hlWrkOdrHdr.addComponent(flOdrHdrColumn1);
		hlWrkOdrHdr.addComponent(flOdrHdrColumn2);
		hlWrkOdrHdr.addComponent(flOdrHdrColumn3);
		hlWrkOdrHdr.addComponent(flOdrHdrColumn4);
		hlWrkOdrHdr.setSpacing(true);
		hlWrkOdrHdr.setMargin(true);
		// adding form layouts into user input layouts
		hlOdrDtl = new HorizontalLayout();
		hlOdrDtl.addComponent(flOdrDtlColumn1);
		hlOdrDtl.addComponent(flOdrDtlColumn2);
		hlOdrDtl.addComponent(flOdrDtlColumn3);
		hlOdrDtl.addComponent(flOdrDtlColumn4);
		hlOdrDtl.setSpacing(true);
		hlOdrDtl.setMargin(true);
		vlWrkOdrComp = new VerticalLayout();
		vlWrkOdrComp.addComponent(hlOdrDtl);
		vlWrkOdrComp.addComponent(tblWrkOdrDtl);
		// Creating Tab Sheet
		TabSheet tabSheet = new TabSheet();
		tabSheet.setWidth("100%");
		tabSheet.setHeight("390");
		tabSheet.addTab(vlWrkOdrComp, "Work Order Detail", null);
		tabSheet.addTab(vlCommendForm, "Comment", null);
		vlWorkOdrHdl = new VerticalLayout();
		vlWorkOdrHdl.addComponent(GERPPanelGenerator.createPanel(hlWrkOdrHdr));
		vlWorkOdrHdl.addComponent(tabSheet);
		vlWorkOdrHdl.setSpacing(true);
		vlWorkOdrHdl.setWidth("100%");
		vlWorkOdrHdl.setSpacing(true);
		vlWorkOdrHdl.setMargin(false);
		hlUserInputLayout.addComponent(vlWorkOdrHdl);
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<WorkOrderHdrDM> listWOHeader = new ArrayList<WorkOrderHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid);
		Long client = (Long) cbClientName.getValue();
		Long branch = (Long) cbBranchName.getValue();
		if (name.equals("Reviewer")) {
			listWOHeader = serviceWrkOrdHdr.getWorkOrderHDRList(companyid, branch, client,
					(String) tfPlanRefNo.getValue(), (String) cbwrkOdrType.getValue(),
					(String) cbWorkderStatus.getValue(), "F", null, null, null, null, null);
		} else {
			listWOHeader = serviceWrkOrdHdr.getWorkOrderHDRList(companyid, branch, client,
					(String) tfPlanRefNo.getValue(), (String) cbwrkOdrType.getValue(),
					(String) cbWorkderStatus.getValue(), "F", null, null, null, null, null);
		}
		recordCnt = listWOHeader.size();
		beanWrkOdrHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		beanWrkOdrHdr.addAll(listWOHeader);
		tblMstScrSrchRslt.setContainerDataSource(beanWrkOdrHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "workOrdrId", "workOrdrNo", "pono", "workOrdrTyp",
				"branchName", "clientName", "workOrdrDt", "workOrdrSts", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Work Order No.", "PO No.", "Work Order Type",
				"Branch Name", "Client Name", "Work Order Date", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("workOrdrId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void loadSrchWrkOdrDtlRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblWrkOdrDtl.setFooterVisible(true);
		recordDtlCount = listWODetails.size();
		beanWrkOdrDtl = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
		beanWrkOdrDtl.addAll(listWODetails);
		tblWrkOdrDtl.setContainerDataSource(beanWrkOdrDtl);
		tblWrkOdrDtl.setVisibleColumns(new Object[] { "prodName", "workOrdQty", "customField1", "customField2",
				"planQty", "balQty", "lastUpdatedDt", "lastUpdatedBy" });
		tblWrkOdrDtl.setColumnHeaders(new String[] { "Product Name", "Work Order Qty.", "Part No.", "Drawing No.",
				"Plan Qty.", "Balance Qty.", "Last Updated Date", "Last Updated By" });
		tblWrkOdrDtl.setColumnAlignment("prodName", Align.RIGHT);
		tblWrkOdrDtl.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordDtlCount);
	}
	
	// Method to load branch List
	private void loadBranchList() {
		{
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(serviceBranch.getBranchList(branchID, null, null, "Active", companyid, "F"));
			cbBranchName.setContainerDataSource(beanbranch);
		}
	}
	
	private void loadPurchaseOrdNo() {
		BeanContainer<Long, SmsPOHdrDM> beanPurchaseOrdHdr = new BeanContainer<Long, SmsPOHdrDM>(SmsPOHdrDM.class);
		beanPurchaseOrdHdr.setBeanIdProperty("poid");
		beanPurchaseOrdHdr.addAll(servicePurchaseOrdHdr.getSmspohdrList(null, null, companyid, null, null, null, null,
				"F", (Long) cbEnquiryNumber.getValue()));
		cbPONumber.setContainerDataSource(beanPurchaseOrdHdr);
	}
	
	private void loadClientList() {
		try {
			Long clientid = serviceEnquiryHdr
					.getSmsEnqHdrList(null, Long.valueOf(cbEnquiryNumber.getValue().toString()), null, null, null, "F",
							null, null).get(0).getClientId();
			BeanContainer<Long, ClientDM> beanClient = new BeanContainer<Long, ClientDM>(ClientDM.class);
			beanClient.setBeanIdProperty("clientId");
			beanClient.addAll(serviceClient.getClientDetails(companyid, clientid, null, null, null, null, null, null,
					"Active", "P"));
			cbClientName.setContainerDataSource(beanClient);
			cbClientName.setValue(clientid);
		}
		catch (Exception e) {
		}
	}
	
	private void loadPurchseOrdDtlList() {
		BeanItemContainer<SmsPODtlDM> beanPurchaseOrdDtl = new BeanItemContainer<SmsPODtlDM>(SmsPODtlDM.class);
		beanPurchaseOrdDtl.addAll(serviceWrkOrdDtl.getPurchaseOrdDtlList((Long) cbPONumber.getValue()));
		cbPrdName.setContainerDataSource(beanPurchaseOrdDtl);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbBranchName.setReadOnly(false);
		cbBranchName.setValue(null);
		cbBranchName.setComponentError(null);
		cbClientName.setReadOnly(false);
		cbClientName.setValue(null);
		cbClientName.setComponentError(null);
		cbPONumber.setReadOnly(false);
		cbPONumber.setValue(null);
		cbPONumber.setComponentError(null);
		cbwrkOdrType.setReadOnly(false);
		cbwrkOdrType.setValue(null);
		cbwrkOdrType.setComponentError(null);
		cbWorkderStatus.setReadOnly(false);
		cbWorkderStatus.setValue(null);
		cbWorkderStatus.setComponentError(null);
		wrkOdrDate.setReadOnly(false);
		wrkOdrDate.setValue(new Date());
		wrkOdrDate.setComponentError(null);
		tfPlanRefNo.setReadOnly(false);
		tfPlanRefNo.setValue("");
		tfPlanRefNo.setReadOnly(true);
		tfPlanRefNo.setComponentError(null);
		tfEnquiryNumber.setReadOnly(false);
		tfEnquiryNumber.setValue("");
		tfEnquiryNumber.setReadOnly(true);
		taWrkOdrHdrRemarks.setValue("");
		taWrkOdrDtlRmks.setValue("");
		ogPriority.setValue("Normal");
		ogMaterialUsage.setValue("Exempted");
		cbClientName.setContainerDataSource(null);
		cbPONumber.setContainerDataSource(null);
		listWODetails = new ArrayList<WorkOrderDtlDM>();
		tblWrkOdrDtl.removeAllItems();
		cbEnquiryNumber.setReadOnly(false);
		cbEnquiryNumber.setValue(null);
	}
	
	// Method to edit the values from table into fields to update process
	private void editWorkOrderHdrDetails() {
		if (tblMstScrSrchRslt.getValue() != null) {
			WorkOrderHdrDM workOrderHdrDM = beanWrkOdrHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			wrkOdrHdrId = workOrderHdrDM.getWorkOrdrId();
			wrkOdrDate.setReadOnly(false);
			wrkOdrDate.setValue(workOrderHdrDM.getWorkOrdrDtF());
			wrkOdrDate.setReadOnly(true);
			tfPlanRefNo.setReadOnly(false);
			tfPlanRefNo.setValue(workOrderHdrDM.getWorkOrdrNo());
			tfPlanRefNo.setReadOnly(true);
			cbBranchName.setReadOnly(false);
			cbBranchName.setValue(workOrderHdrDM.getBranchId());
			cbBranchName.setReadOnly(true);
			cbEnquiryNumber.setReadOnly(false);
			cbEnquiryNumber.setValue(workOrderHdrDM.getEnquiryId());
			cbEnquiryNumber.setReadOnly(true);
			cbClientName.setReadOnly(false);
			cbClientName.setValue(workOrderHdrDM.getClientId());
			cbClientName.setReadOnly(true);
			cbPONumber.setReadOnly(false);
			cbPONumber.setValue(workOrderHdrDM.getPoId());
			cbPONumber.setReadOnly(true);
			cbwrkOdrType.setReadOnly(false);
			cbwrkOdrType.setValue(workOrderHdrDM.getWorkOrdrTyp());
			cbwrkOdrType.setReadOnly(true);
			if (workOrderHdrDM.getWorkOrdrRmrks() != null) {
				taWrkOdrHdrRemarks.setValue(workOrderHdrDM.getWorkOrdrRmrks());
			}
			ogPriority.setValue(workOrderHdrDM.getPriority());
			ogMaterialUsage.setValue(workOrderHdrDM.getMaterialUsage());
			workordtype.setValue(workOrderHdrDM.getIsSelf());
			opPONumbers.setValue(workOrderHdrDM.getIsLOI());
			if (workOrderHdrDM.getWorkOrdrSts().equals("Approved")) {
				cbWorkderStatus.setReadOnly(false);
				cbWorkderStatus.setValue(workOrderHdrDM.getWorkOrdrSts());
				cbWorkderStatus.setReadOnly(true);
			} else {
				cbWorkderStatus.setReadOnly(false);
				cbWorkderStatus.setValue(workOrderHdrDM.getWorkOrdrSts());
			}
			listWODetails = serviceWrkOrdDtl.getWorkOrderDtlList(null, Long.valueOf(wrkOdrHdrId), null, "F");
		}
		comment = new Comments(vlCommendForm, companyid, null, null, null, null, commentby);
		comment.loadsrch(true, null, null, wrkOdrHdrId, null, null, null);
		loadSrchWrkOdrDtlRslt();
	}
	
	private void editWorkOrderDtlDetails() {
		if (tblWrkOdrDtl.getValue() != null) {
			WorkOrderDtlDM workOrderDtlDM = new WorkOrderDtlDM();
			workOrderDtlDM = beanWrkOdrDtl.getItem(tblWrkOdrDtl.getValue()).getBean();
			Long prodid = workOrderDtlDM.getProdId();
			Collection<?> prodids = cbPrdName.getItemIds();
			for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbPrdName.getItem(itemId);
				// Get the actual bean and use the data
				SmsPODtlDM st = (SmsPODtlDM) item.getBean();
				if (prodid != null && prodid.equals(st.getProductid())) {
					cbPrdName.setValue(itemId);
				}
			}
			tfPrdName.setReadOnly(false);
			tfPrdName.setValue(workOrderDtlDM.getProductName());
			tfPrdName.setReadOnly(true);
			tfWrkOdrQty.setReadOnly(false);
			tfWrkOdrQty.setValue(workOrderDtlDM.getWorkOrdQty().toString());
			tfWrkOdrQty.setReadOnly(true);
			tfPlnQty.setValue(workOrderDtlDM.getPlanQty().toString());
			tfBalQty.setReadOnly(false);
			tfBalQty.setValue(workOrderDtlDM.getBalQty().toString());
			tfBalQty.setReadOnly(true);
			if (workOrderDtlDM.getWorkOrdDlRmrks() != null) {
				taWrkOdrDtlRmks.setValue(workOrderDtlDM.getWorkOrdDlRmrks());
			}
			if (workOrderDtlDM.getCustomField1() != null) {
				tfCustomField1.setValue(workOrderDtlDM.getCustomField1());
			}
			if (workOrderDtlDM.getCustomField2() != null) {
				tfCustomField2.setValue(workOrderDtlDM.getCustomField2());
			}
			delvrySchdDt.setValue(workOrderDtlDM.getDlvrySchdl());
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
		}
	}
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbBranchName.setValue(null);
		cbClientName.setValue(null);
		tfPlanRefNo.setValue("");
		cbwrkOdrType.setValue(null);
		cbWorkderStatus.setValue(null);
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		resetFields();
		resetWorkOrdDtl();
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		cbWorkderStatus.setRequired(true);
		cbwrkOdrType.setValue("Job Work Order");
		System.out.println("cbwrkOdrType.get" + cbwrkOdrType.getValue());
		if (cbwrkOdrType.getValue() != null) {
			loadworkordertypeAutogen();
		}
		tblWrkOdrDtl.setVisible(true);
		comment = new Comments(vlCommendForm, companyid, null, null, null, null, commentby);
		cbWorkderStatus.setValue(cbWorkderStatus.getItemIds().iterator().next());
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for TestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_WORKORDER_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", wrkOdrHdrId);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		cbBranchName.setRequired(false);
		cbClientName.setRequired(false);
		cbPONumber.setRequired(false);
		wrkOdrDate.setRequired(false);
		cbwrkOdrType.setRequired(false);
		cbPrdName.setRequired(false);
		tfPrdName.setRequired(false);
		tfPlnQty.setRequired(false);
		tfBalQty.setRequired(false);
		delvrySchdDt.setRequired(false);
		cbWorkderStatus.setRequired(false);
		resetFields();
		resetWorkOrdDtl();
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblWrkOdrDtl.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblWrkOdrDtl.setVisible(true);
		if (tfPlanRefNo.getValue() == null || tfPlanRefNo.getValue().trim().length() == 0) {
			tfPlanRefNo.setReadOnly(false);
		}
		resetFields();
		resetWorkOrdDtl();
		editWorkOrderHdrDetails();
		editWorkOrderDtlDetails();
		loadSrchWrkOdrDtlRslt();
	}
	
	private void resetWorkOrdDtl() {
		cbPrdName.setReadOnly(false);
		cbPrdName.setValue(null);
		cbPrdName.setComponentError(null);
		tfPrdName.setReadOnly(false);
		tfPrdName.setValue("");
		tfPrdName.setReadOnly(true);
		tfWrkOdrQty.setReadOnly(false);
		tfWrkOdrQty.setValue("0");
		tfWrkOdrQty.setReadOnly(true);
		tfPlnQty.setReadOnly(false);
		tfPlnQty.setValue("0");
		tfPlnQty.setComponentError(null);
		tfBalQty.setReadOnly(false);
		tfBalQty.setValue("0");
		tfBalQty.setReadOnly(true);
		tfBalQty.setComponentError(null);
		tfPrdName.setComponentError(null);
		delvrySchdDt.setValue(null);
		delvrySchdDt.setComponentError(null);
		taWrkOdrDtlRmks.setValue("");
		tfCustomField1.setValue("");
		tfCustomField2.setValue("");
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if ((cbBranchName.getValue() == null)) {
			cbBranchName.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_HDR_BRNCH_NM));
			errorFlag = true;
		} else {
			cbBranchName.setComponentError(null);
		}
		if (workordtype.getValue().toString().equalsIgnoreCase("Enquiry")) {
			if ((cbClientName.getValue() == null)) {
				cbClientName.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_HDR_CLNT_NM));
				errorFlag = true;
			} else {
				cbClientName.setComponentError(null);
			}
		}
		if (opPONumbers.getValue().toString().equalsIgnoreCase("PO")) {
			if (cbPONumber.getValue() == null) {
				cbPONumber.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_HDR_PONO));
				errorFlag = true;
			} else {
				cbPONumber.setComponentError(null);
			}
		}
		if (wrkOdrDate.getValue() == null) {
			wrkOdrDate.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_HDR_DT));
			errorFlag = true;
		} else {
			wrkOdrDate.setComponentError(null);
		}
		if (cbwrkOdrType.getValue() == null) {
			cbwrkOdrType.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_HDR_TYPE));
			errorFlag = true;
		} else {
			cbwrkOdrType.setComponentError(null);
		}
		if (cbWorkderStatus.getValue() == null) {
			cbWorkderStatus.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_HDR_STATUS));
			errorFlag = true;
		} else {
			cbWorkderStatus.setComponentError(null);
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			WorkOrderHdrDM wrkOdHdr = new WorkOrderHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				wrkOdHdr = beanWrkOdrHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			wrkOdHdr.setWorkOrdrNo(tfPlanRefNo.getValue());
			wrkOdHdr.setCompanyId(companyid);
			wrkOdHdr.setBranchId((Long) cbBranchName.getValue());
			wrkOdHdr.setWorkOrdrDt(wrkOdrDate.getValue());
			wrkOdHdr.setWorkOrdrTyp(cbwrkOdrType.getValue().toString());
			wrkOdHdr.setClientId((Long) cbClientName.getValue());
			wrkOdHdr.setPoId((Long) (cbPONumber.getValue()));
			wrkOdHdr.setWorkOrdrRmrks(taWrkOdrHdrRemarks.getValue());
			wrkOdHdr.setPreprdBy(employeeId);
			wrkOdHdr.setReviewBy(null);
			wrkOdHdr.setActionedBy(null);
			wrkOdHdr.setPriority((String) ogPriority.getValue());
			wrkOdHdr.setMaterialUsage((String) ogMaterialUsage.getValue());
			wrkOdHdr.setWorkOrdrSts((String) cbWorkderStatus.getValue());
			wrkOdHdr.setLastUpdatedDt(DateUtils.getcurrentdate());
			wrkOdHdr.setLastUpdatedBy(username);
			wrkOdHdr.setEnquiryId(Long.valueOf(cbEnquiryNumber.getValue().toString()));
			wrkOdHdr.setIsLOI(opPONumbers.getValue().toString());
			wrkOdHdr.setIsSelf(workordtype.getValue().toString());
			serviceWrkOrdHdr.saveOrUpdateWrkOdrHdrDetails(wrkOdHdr);
			wrkOdrHdrId = wrkOdHdr.getWorkOrdrId();
			@SuppressWarnings("unchecked")
			Collection<WorkOrderDtlDM> itemIds = (Collection<WorkOrderDtlDM>) tblWrkOdrDtl.getVisibleItemIds();
			for (WorkOrderDtlDM save : (Collection<WorkOrderDtlDM>) itemIds) {
				save.setWorkOrdHdrId(Long.valueOf(wrkOdHdr.getWorkOrdrId().toString()));
				Long ponumber = (Long) (cbPONumber.getValue());
				Long reduceOrdrqty = 0L;
				reduceOrdrqty = servicesmspodtl
						.getsmspodtllist(null, ponumber, save.getProdId(), null, null, null, "F").get(0)
						.getInvoicedqty();
				Long invoiceqty = 0L;
				if (save.getWorkOrdDlId() == null) {
					invoiceqty = reduceOrdrqty + save.getPlanQty();
					servicesmspodtl.updateInvoiceOrderQty(ponumber, save.getProdId(), invoiceqty);
				}
				serviceWrkOrdDtl.saveOrUpdateWrkOdrDtlDetails(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					if (cbwrkOdrType.getValue().equals("Job Work Order")) {
						SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "MF_PWONO")
								.get(0);
						if (slnoObj.getAutoGenYN().equals("Y")) {
							serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "MF_PWONO");
						}
					} else if (cbwrkOdrType.getValue().equals("Sample Work Order")) {
						SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "MF_SWONO")
								.get(0);
						if (slnoObj.getAutoGenYN().equals("Y")) {
							serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "MF_SWONO");
						}
					} else if (cbwrkOdrType.getValue().equals("Mold Manufacturing")) {
						SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "MF_IWONO")
								.get(0);
						if (slnoObj.getAutoGenYN().equals("Y")) {
							serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "MF_IWONO");
						}
					}
				}
				catch (Exception e) {
				}
			}
			resetWorkOrdDtl();
			resetFields();
			loadSrchRslt();
			loadPurchseOrdDtlList();
			comment.saveWoId(wrkOdrHdrId);
			comment.resetFields();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean validateWorkOrdDtls() {
		boolean isValid = true;
		if (cbPrdName.getValue() == null) {
			cbPrdName.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_PR_NAME));
			isValid = false;
		} else {
			cbPrdName.setComponentError(null);
		}
		if (Long.valueOf(tfWrkOdrQty.getValue()) < Long.valueOf(tfPlnQty.getValue())) {
			tfPlnQty.setComponentError(new UserError("Quantity should not greater than Work order"));
		}
		try {
			Long.valueOf(tfPlnQty.getValue());
			tfPlnQty.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfPlnQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_PL_QTY));
			isValid = false;
		}
		if (Long.valueOf(tfWrkOdrQty.getValue()) < Long.valueOf(tfPlnQty.getValue())) {
			tfPlnQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_INCOR_PL_QTY));
			isValid = false;
		}
		if (delvrySchdDt.getValue() == null) {
			delvrySchdDt.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_DT));
			isValid = false;
		} else {
			delvrySchdDt.setComponentError(null);
		}
		return isValid;
	}
	
	private void saveWorkOdrDtlDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			WorkOrderDtlDM workOrderDtlDM = new WorkOrderDtlDM();
			if (tblWrkOdrDtl.getValue() != null) {
				workOrderDtlDM = beanWrkOdrDtl.getItem(tblWrkOdrDtl.getValue()).getBean();
				listWODetails.remove(workOrderDtlDM);
			}
			workOrderDtlDM.setProdId(((SmsPODtlDM) cbPrdName.getValue()).getProductid());
			workOrderDtlDM.setProdName(((SmsPODtlDM) cbPrdName.getValue()).getProdname());
			workOrderDtlDM.setProductName(tfPrdName.getValue());
			tfWrkOdrQty.setReadOnly(false);
			workOrderDtlDM.setWorkOrdQty(Long.valueOf(tfWrkOdrQty.getValue()));
			tfWrkOdrQty.setReadOnly(true);
			workOrderDtlDM.setPlanQty(Long.valueOf(tfPlnQty.getValue()));
			workOrderDtlDM.setBalQty(Long.valueOf(tfBalQty.getValue().toString()));
			workOrderDtlDM.setDlvrySchdl(delvrySchdDt.getValue());
			workOrderDtlDM.setWorkOrdDlRmrks(taWrkOdrDtlRmks.getValue());
			workOrderDtlDM.setWdStatus((String) cbWorkderStatus.getValue());
			workOrderDtlDM.setLastUpdatedDt(DateUtils.getcurrentdate());
			workOrderDtlDM.setLastUpdatedBy(username);
			workOrderDtlDM.setCustomField1(tfCustomField1.getValue());
			workOrderDtlDM.setCustomField2(tfCustomField2.getValue());
			listWODetails.add(workOrderDtlDM);
			resetWorkOrdDtl();
			loadSrchWrkOdrDtlRslt();
			btnsaveWrkOdrDtl.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void deleteTstDefDetails() {
		WorkOrderDtlDM workOrderDtlDM = new WorkOrderDtlDM();
		if (tblWrkOdrDtl.getValue() != null) {
			workOrderDtlDM = beanWrkOdrDtl.getItem(tblWrkOdrDtl.getValue()).getBean();
			listWODetails.remove(workOrderDtlDM);
			resetWorkOrdDtl();
			tblWrkOdrDtl.setValue("");
			loadSrchWrkOdrDtlRslt();
			btnDtlWrkOrd.setEnabled(false);
		}
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, Long> parameterMap = new HashMap<String, Long>();
			parameterMap.put("WOID", wrkOdrHdrId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/workorder"); // workorder is the name of my jasper
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				statement.close();
				Database.close(connection);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Load EnquiryNo
	private void loadEnquiryNo() {
		BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("enquiryId");
		beansmsenqHdr.addAll(serviceEnquiryHdr.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
		cbEnquiryNumber.setContainerDataSource(beansmsenqHdr);
	}
	
	private void changeworkorderstatus() {
		if (workordtype.getValue() != null) {
			if (workordtype.getValue().toString().equalsIgnoreCase("Self")) {
				cbClientName.setRequired(false);
			} else if (workordtype.getValue().toString().equalsIgnoreCase("Enquiry")) {
				cbClientName.setRequired(true);
			}
		}
	}
	
	private void changeworkorderPOstatus() {
		if (opPONumbers.getValue() != null) {
			if (opPONumbers.getValue().toString().equalsIgnoreCase("LOI")) {
				cbPONumber.setRequired(false);
			} else if (opPONumbers.getValue().toString().equalsIgnoreCase("PO")) {
				cbPONumber.setRequired(true);
			}
		}
	}
	
	private void loadworkordertypeAutogen() {
		if (cbwrkOdrType.getValue().equals("Job Work Order")) {
			try {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, moduleId, "MF_PWONO").get(0);
				tfPlanRefNo.setReadOnly(false);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					tfPlanRefNo.setValue(slnoObj.getKeyDesc());
					tfPlanRefNo.setReadOnly(true);
				} else {
					tfPlanRefNo.setReadOnly(false);
				}
			}
			catch (Exception e) {
			}
		} else if (cbwrkOdrType.getValue().equals("Sample Work Order")) {
			try {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, moduleId, "MF_SWONO").get(0);
				tfPlanRefNo.setReadOnly(false);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					tfPlanRefNo.setValue(slnoObj.getKeyDesc());
					tfPlanRefNo.setReadOnly(true);
				} else {
					tfPlanRefNo.setReadOnly(false);
				}
			}
			catch (Exception e) {
			}
		} else if (cbwrkOdrType.getValue().equals("Mold Manufacturing")) {
			try {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, moduleId, "MF_IWONO").get(0);
				tfPlanRefNo.setReadOnly(false);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					tfPlanRefNo.setValue(slnoObj.getKeyDesc());
					tfPlanRefNo.setReadOnly(true);
				} else {
					tfPlanRefNo.setReadOnly(false);
				}
			}
			catch (Exception e) {
			}
		}
	}
}
