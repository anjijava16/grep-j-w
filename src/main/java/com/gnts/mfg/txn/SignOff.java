package com.gnts.mfg.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.ProductService;
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
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.txn.QATestHdrDM;
import com.gnts.mfg.domain.txn.SignOffDtlDM;
import com.gnts.mfg.domain.txn.SignoffHdrDM;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.mst.QCTestType;
import com.gnts.mfg.service.txn.QATestHdrService;
import com.gnts.mfg.service.txn.SignOffDtlService;
import com.gnts.mfg.service.txn.SignoffHdrService;
import com.gnts.mfg.service.txn.WorkOrderDtlService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SignOff extends BaseTransUI {
	/**
	 * 
	 */
	// SignOffHdr Components Declaration
	private static final long serialVersionUID = 1L;
	private TextField tfbatchQty, tfbatchTested, tfbatchNo;
	private ComboBox cbBranch, cbClient, cbWorkOrderNo, cbProduct, cbSignOffHdrStatus;
	private TextArea taRemaks;
	private PopupDateField pdBatchDate;
	private SignoffHdrService serviceSignoffHdr = (SignoffHdrService) SpringContextHelper.getBean("Signoffhdr");
	private List<SignoffHdrDM> listSignOffHdr = null;
	private BeanItemContainer<SignoffHdrDM> beanSignoffHdr = null;
	private FormLayout flSignOffCmp1, flSignOffCmp2, flSignOffCmp3, flSignOffCmp4;
	private HorizontalLayout hlSignOffHdr = new HorizontalLayout();
	// SignOff Details Components Declaration
	private TextField tfProductSlNo;
	private ComboBox cbInspectionNo, cbSignOffDtStatus;
	private Button btnAdd;
	private Table tblSignOffDtl;
	private SignOffDtlService serviceSignoffDtl = (SignOffDtlService) SpringContextHelper.getBean("SignoffDtl");
	private List<SignOffDtlDM> listSignOffDtlDM = null;
	private BeanItemContainer<SignOffDtlDM> beanSignoffDtl = null;
	private HorizontalLayout hlSignOffDtl = new HorizontalLayout();
	private VerticalLayout vlTableForm = new VerticalLayout();
	private HorizontalLayout hlDtlNCmt = new HorizontalLayout();
	//
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private ClientService serviceClient = (ClientService) SpringContextHelper.getBean("clients");
	private WorkOrderHdrService serviceWorkOrderHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private WorkOrderDtlService serviceWorkOrderDtl = (WorkOrderDtlService) SpringContextHelper.getBean("workOrderDtl");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private SlnoGenService serviceSLNo = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private QATestHdrService serviceQATestHdr = (QATestHdrService) SpringContextHelper.getBean("qatesthdr");
	private FormLayout flTstHdr1, flTstHdr2, flTstHdr3, flTstHdr4;
	private FormLayout flTstDtl1, flTstDtl2, flTstDtl3, flTstDtl4, flTstDtl5;
	private VerticalLayout hlDocumentLayout = new VerticalLayout();
	private Logger logger = Logger.getLogger(QCTestType.class);
	private int recordCnt;
	private Long signOffHdrId;
	private String userName;
	private Long companyId;
	private Long moduleId;
	private HorizontalLayout hlSearchLayout;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private Long branchId;
	private Comments comment;
	private Long commentby;
	private Long appScreenId;
	private Long roleId;
	private Button btnDetlIns = new GERPButton("Delete", "delete", this);
	
	public SignOff() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = Long.valueOf(UI.getCurrent().getSession().getAttribute("moduleId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		commentby = ((Long) (UI.getCurrent().getSession().getAttribute("employeeid")));
		buildview();
	}
	
	private void buildview() {
		//
		tfbatchQty = new GERPTextField("Batch Quantity");
		tfbatchTested = new GERPTextField("Batch Tested");
		tfbatchNo = new GERPTextField("Batch Number");
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		cbClient = new GERPComboBox("Client Name");
		cbClient.setItemCaptionPropertyId("clientName");
		cbWorkOrderNo = new GERPComboBox("Work Order No.");
		loadClientList();
		cbWorkOrderNo.setItemCaptionPropertyId("workOrdrNo");
		cbClient.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					loadWorkOrderNoList();
					cbWorkOrderNo.setValue(cbWorkOrderNo.getItemIds().iterator().next());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		cbWorkOrderNo.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					if (cbWorkOrderNo.getValue() != null) {
						loadProductList();
						cbProduct.setValue(cbProduct.getItemIds().iterator().next());
					}
				}
				catch (Exception e) {
				}
			}
		});
		try {
			ApprovalSchemaDM obj = serviceSignoffHdr.getReviewerId(companyId, appScreenId, branchId, roleId).get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbSignOffHdrStatus = new GERPComboBox("Status", BASEConstants.T_MFG_QA_SIGNOFF_HDR,
						BASEConstants.SGN_RVER);
			} else {
				cbSignOffHdrStatus = new GERPComboBox("Status", BASEConstants.T_MFG_QA_SIGNOFF_HDR,
						BASEConstants.SGN_APVER);
			}
		}
		catch (Exception e) {
		}
		cbSignOffHdrStatus.setWidth("150");
		btnDetlIns.setEnabled(false);
		btnDetlIns.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteTstDefDetails();
			}
		});
		taRemaks = new GERPTextArea("Remarks");
		taRemaks.setHeight("70");
		pdBatchDate = new GERPPopupDateField("Batch Date");
		//
		tfProductSlNo = new GERPTextField("Product Sl.No");
		cbInspectionNo = new GERPComboBox("Inspection No.");
		cbInspectionNo.setItemCaptionPropertyId("inspectionno");
		cbSignOffDtStatus = new GERPComboBox("Status", BASEConstants.T_MFG_QA_SIGNOFF_HDR, BASEConstants.SGN_APVER);
		cbSignOffDtStatus.setWidth("150");
		loadInspectionNo();
		cbInspectionNo.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbInspectionNo.getValue() != null) {
					tfProductSlNo.setReadOnly(false);
					tfProductSlNo.setValue((serviceQATestHdr.getQaTestHdrDetails(
							((QATestHdrDM) cbInspectionNo.getValue()).getQatestHdrid(), companyId, null, null, null,
							"Active").get(0).getProdslno()));
					tfProductSlNo.setReadOnly(true);
				}
			}
		});
		btnAdd = new Button("Add");
		btnAdd.setStyleName("add");
		btnAdd.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateSignOffDtlDetails()) {
					saveSignOffDtlList();
				}
			}
		});
		tblSignOffDtl = new GERPTable();
		tblSignOffDtl.setPageLength(6);
		tblSignOffDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblSignOffDtl.isSelected(event.getItemId())) {
					tblSignOffDtl.setImmediate(true);
					btnAdd.setCaption("Add");
					resetSignOffDtlFields();
					btnDetlIns.setEnabled(true);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAdd.setCaption("Update");
					editSignOffDtlList();
					btnDetlIns.setEnabled(true);
				}
			}
		});
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		resetSignOffDtlFields();
		assembleSearchLayout();
		loadSrchRslt();
		hlPageHdrContainter.setVisible(true);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		// Initializing to form layouts for QCTest UI search layout
		tfbatchNo.setReadOnly(false);
		hlSearchLayout.removeAllComponents();
		flSignOffCmp1 = new GERPFormLayout();
		flSignOffCmp2 = new GERPFormLayout();
		flSignOffCmp3 = new GERPFormLayout();
		flSignOffCmp4 = new GERPFormLayout();
		flSignOffCmp1.addComponent(tfbatchNo);
		flSignOffCmp2.addComponent(cbBranch);
		flSignOffCmp3.addComponent(cbClient);
		flSignOffCmp4.addComponent(cbSignOffHdrStatus);
		hlSearchLayout.addComponent(flSignOffCmp1);
		hlSearchLayout.addComponent(flSignOffCmp2);
		hlSearchLayout.addComponent(flSignOffCmp3);
		hlSearchLayout.addComponent(flSignOffCmp4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ " assembleInputUserLayout layout");
		/*
		 * Adding user input layout to the input layout as all the fields in the user input are available in the edit
		 * block. hence the same layout used as is
		 */
		// Set required fields
		// Removing components from search layout and re-initializing form layouts
		// QATest Header Components adding to the User input layout
		// ========================================================================
		hlSearchLayout.removeAllComponents();
		hlUserInputLayout.removeAllComponents();
		hlDtlNCmt.removeAllComponents();
		hlSignOffHdr.removeAllComponents();
		hlSignOffDtl.removeAllComponents();
		tfbatchNo.setRequired(true);
		pdBatchDate.setRequired(true);
		cbClient.setRequired(true);
		cbBranch.setRequired(true);
		cbProduct.setRequired(true);
		cbWorkOrderNo.setRequired(true);
		tfbatchQty.setRequired(true);
		cbInspectionNo.setRequired(true);
		tfProductSlNo.setRequired(true);
		tfbatchTested.setRequired(true);
		cbSignOffDtStatus.setRequired(true);
		cbSignOffHdrStatus.setRequired(true);
		// QATest Header Components adding to the User input layout
		flTstHdr1 = new FormLayout();
		flTstHdr2 = new FormLayout();
		flTstHdr3 = new FormLayout();
		flTstHdr4 = new FormLayout();
		flTstHdr1.addComponent(tfbatchNo);
		flTstHdr1.addComponent(pdBatchDate);
		flTstHdr1.addComponent(cbBranch);
		flTstHdr2.addComponent(cbClient);
		flTstHdr2.addComponent(cbWorkOrderNo);
		flTstHdr2.addComponent(cbProduct);
		flTstHdr3.addComponent(tfbatchQty);
		flTstHdr3.addComponent(tfbatchTested);
		flTstHdr3.addComponent(cbSignOffHdrStatus);
		flTstHdr4.addComponent(taRemaks);
		HorizontalLayout hlTstHdr = new HorizontalLayout();
		hlTstHdr.addComponent(flTstHdr1);
		hlTstHdr.addComponent(flTstHdr2);
		hlTstHdr.addComponent(flTstHdr3);
		hlTstHdr.addComponent(flTstHdr4);
		hlTstHdr.setMargin(true);
		hlTstHdr.setSpacing(true);
		// QA Test Details Components adding to the user input layout
		flTstDtl1 = new FormLayout();
		flTstDtl2 = new FormLayout();
		flTstDtl3 = new FormLayout();
		flTstDtl4 = new FormLayout();
		flTstDtl5 = new FormLayout();
		flTstDtl1.addComponent(cbInspectionNo);
		flTstDtl2.addComponent(tfProductSlNo);
		flTstDtl3.addComponent(cbSignOffDtStatus);
		HorizontalLayout hlTstDtl = new HorizontalLayout();
		hlTstDtl.addComponent(flTstDtl1);
		hlTstDtl.addComponent(flTstDtl2);
		hlTstDtl.addComponent(flTstDtl3);
		hlTstDtl.addComponent(flTstDtl4);
		hlTstDtl.addComponent(flTstDtl5);
		hlTstDtl.setMargin(true);
		hlTstDtl.setSpacing(true);
		VerticalLayout vlTstDtl = new VerticalLayout();
		vlTstDtl.addComponent(hlTstDtl);
		vlTstDtl.addComponent(tblSignOffDtl);
		TabSheet tabSheet = new TabSheet();
		tabSheet.setWidth("100%");
		tabSheet.setHeight("320");
		tabSheet.addTab(vlTstDtl, "Inspection Detail", null);
		tabSheet.addTab(hlDocumentLayout, "Testing Documents");
		tabSheet.addTab(vlTableForm, "Comments", null);
		VerticalLayout vlAllComponent = new VerticalLayout();
		vlAllComponent.addComponent(hlTstHdr);
		vlAllComponent.addComponent(GERPPanelGenerator.createPanel(hlTstHdr));
		vlAllComponent.addComponent(tabSheet);
		vlAllComponent.setSpacing(true);
		vlAllComponent.setWidth("100%");
		// adding form layouts into user input layouts
		hlUserInputLayout.addComponent(vlAllComponent);
		hlUserInputLayout.setWidth("100%");
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		List<SignoffHdrDM> liSignoffHdr = new ArrayList<SignoffHdrDM>();
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyId);
		listSignOffHdr = serviceSignoffHdr.getSignoffHdrDetails(null, companyId, (Long) cbBranch.getValue(),
				(String) tfbatchNo.getValue(), (Long) cbClient.getValue(), (Long) cbProduct.getValue(),
				(String) cbSignOffHdrStatus.getValue());
		recordCnt = liSignoffHdr.size();
		beanSignoffHdr = new BeanItemContainer<SignoffHdrDM>(SignoffHdrDM.class);
		beanSignoffHdr.addAll(listSignOffHdr);
		tblMstScrSrchRslt.setContainerDataSource(beanSignoffHdr);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "qasignoffid", "batchno", "batchdate", "clientName",
				"productName", "workOrdNo", "batchqty", "batchtested", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Batch No.", "Batch Dt.", "Client Name",
				"Product Name", "Work Ord.No", "Batch Qty.", "Batch Tested", "Last Updated Dt.", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("qasignoffid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadSrchSignOffDtlList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ companyId);
		tblSignOffDtl.removeAllItems();
		recordCnt = listSignOffDtlDM.size();
		beanSignoffDtl = new BeanItemContainer<SignOffDtlDM>(SignOffDtlDM.class);
		beanSignoffDtl.addAll(listSignOffDtlDM);
		tblSignOffDtl.setContainerDataSource(beanSignoffDtl);
		tblSignOffDtl.setVisibleColumns(new Object[] { "qaSignOffDtlId", "inspectionNo", "productSlNo", "signStatus" });
		tblSignOffDtl.setColumnHeaders(new String[] { "Ref.Id", "Inspection No.", "Product Sl.No.", "Status", });
		tblSignOffDtl.setColumnAlignment("qaSignOffDtlId", Align.RIGHT);
		tblSignOffDtl.setColumnFooter("signStatus", "No.of Records : " + recordCnt);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
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
		tfbatchNo.setValue("");
		cbSignOffHdrStatus.setValue(null);
		loadSrchRslt();
	}
	
	private void loadClientList() {
		BeanContainer<Long, ClientDM> beanClient = new BeanContainer<Long, ClientDM>(ClientDM.class);
		beanClient.setBeanIdProperty("clientId");
		beanClient.addAll(serviceClient.getClientDetails(companyId, null, null, null, null, null, null, null, "Active",
				"P"));
		cbClient.setContainerDataSource(beanClient);
	}
	
	private void loadWorkOrderNoList() {
		BeanContainer<Long, WorkOrderHdrDM> beanWrkOrdHdr = new BeanContainer<Long, WorkOrderHdrDM>(
				WorkOrderHdrDM.class);
		beanWrkOrdHdr.setBeanIdProperty("workOrdrId");
		beanWrkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyId, null, (Long) cbClient.getValue(), null,
				null, null, "F", null, null, null, null));
		cbWorkOrderNo.setContainerDataSource(beanWrkOrdHdr);
	}
	
	private void loadProductList() {
		try {
			BeanContainer<Long, ProductDM> beanProd = new BeanContainer<Long, ProductDM>(ProductDM.class);
			beanProd.setBeanIdProperty("prodid");
			beanProd.addAll(serviceProduct.getProductList(
					companyId,
					(serviceWorkOrderDtl.getWorkOrderDtlList(
							null,
							(serviceWorkOrderHdr.getWorkOrderHDRList(companyId, null, null, null, null, "Approved",
									"F", (Long) cbWorkOrderNo.getValue(), null, null, null).get(0).getWorkOrdrId()),
							"Approved", "F").get(0).getProdId()), null, null, "Active", null, null, "F"));
			cbProduct.setContainerDataSource(beanProd);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadBranchList() {
		BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanbranch.setBeanIdProperty("branchId");
		beanbranch.addAll(serviceBranch.getBranchList(null, null, null, "Active", null, "F"));
		cbBranch.setContainerDataSource(beanbranch);
	}
	
	private void loadInspectionNo() {
		BeanItemContainer<QATestHdrDM> beanQATestHdr = new BeanItemContainer<QATestHdrDM>(QATestHdrDM.class);
		beanQATestHdr.addAll(serviceQATestHdr.getQaTestHdrDetails(null, companyId, null, null, null, "Active"));
		cbInspectionNo.setContainerDataSource(beanQATestHdr);
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		resetFields();
		try {
			tfbatchNo.setReadOnly(false);
			SlnoGenDM slnoObj = serviceSLNo.getSequenceNumber(companyId, branchId, moduleId, "MF_BATCHNO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfbatchNo.setValue(slnoObj.getKeyDesc());
				tfbatchNo.setReadOnly(true);
			}
		}
		catch (Exception e) {
		}
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblSignOffDtl.setVisible(true);
		resetSignOffDtlFields();
		loadSrchSignOffDtlList();
		pdBatchDate.setValue(new Date());
		comment = new Comments(vlTableForm, companyId, null, null, null, null, commentby);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		assembleInputUserLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblSignOffDtl.setVisible(true);
		resetFields();
		editSignOffHdrDetails();
		editSignOffDtlList();
		loadSrchSignOffDtlList();
	}
	
	private void editSignOffHdrDetails() {
		try {
			if (tblMstScrSrchRslt.getValue() != null) {
				SignoffHdrDM signoffHdrDM = beanSignoffHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
				signOffHdrId = signoffHdrDM.getQasignoffid();
				cbBranch.setValue(signoffHdrDM.getBranchid());
				cbProduct.setValue(signoffHdrDM.getProductid());
				cbWorkOrderNo.setValue(signoffHdrDM.getWoid().toString());
				cbClient.setValue(signoffHdrDM.getClientid());
				cbSignOffHdrStatus.setValue(signoffHdrDM.getSignstatus());
				tfbatchNo.setReadOnly(false);
				tfbatchNo.setValue(signoffHdrDM.getBatchno());
				tfbatchNo.setReadOnly(true);
				tfbatchTested.setValue(Long.valueOf(signoffHdrDM.getBatchtested()).toString());
				pdBatchDate.setValue(signoffHdrDM.getBatchdateIn());
				tfbatchQty.setValue(Long.valueOf(signoffHdrDM.getBatchqty()).toString());
				if (signoffHdrDM.getBatchremarks() != null) {
					taRemaks.setValue(signoffHdrDM.getBatchremarks());
				}
				listSignOffDtlDM = serviceSignoffDtl.getSignoffDetails(null, signOffHdrId, null,
						(String) cbSignOffDtStatus.getValue());
				comment = new Comments(vlTableForm, companyId, null, null, null, signOffHdrId, commentby);
				comment.loadsrch(true, null, companyId, null, null, null, signOffHdrId);
				new TestingDocuments(hlDocumentLayout, signOffHdrId.toString(), "SIGN_OFF");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void editSignOffDtlList() {
		if (tblSignOffDtl.getValue() != null) {
			SignOffDtlDM signOffDtlDM = new SignOffDtlDM();
			signOffDtlDM = beanSignoffDtl.getItem(tblSignOffDtl.getValue()).getBean();
			Long qaTestHdrId = signOffDtlDM.getQaTstId();
			Collection<?> tstSpecID = cbInspectionNo.getItemIds();
			for (Iterator<?> iterator = tstSpecID.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbInspectionNo.getItem(itemId);
				// Get the actual bean and use the data
				QATestHdrDM st = (QATestHdrDM) item.getBean();
				if (qaTestHdrId != null && qaTestHdrId.equals(st.getQatestHdrid())) {
					cbInspectionNo.setValue(itemId);
				}
			}
			tfProductSlNo.setValue(signOffDtlDM.getProductSlNo());
			cbSignOffDtStatus.setValue(signOffDtlDM.getSignStatus());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		Boolean errorFlag = false;
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_QC_BRANCH));
			errorFlag = true;
		} else {
			cbBranch.setComponentError(null);
		}
		if (cbClient.getValue() == null) {
			cbClient.setComponentError(new UserError(GERPErrorCodes.NULL_QC_MATERIAL));
			errorFlag = true;
		} else {
			cbClient.setComponentError(null);
		}
		if (pdBatchDate.getValue() == null) {
			pdBatchDate.setComponentError(new UserError(GERPErrorCodes.NULL_INSP_DATE));
			errorFlag = true;
		} else {
			pdBatchDate.setComponentError(null);
		}
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TSTTYP));
			errorFlag = true;
		} else {
			cbProduct.setComponentError(null);
		}
		if (cbWorkOrderNo.getValue() == null) {
			cbWorkOrderNo.setComponentError(new UserError(GERPErrorCodes.NULL_QC_PRDDRG));
			errorFlag = true;
		} else {
			cbWorkOrderNo.setComponentError(null);
		}
		if (tfbatchQty.getValue() == "" || tfbatchQty.getValue() == null || tfbatchQty.getValue().trim().length() == 0) {
			tfbatchQty.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TSTPRDSLNO));
			errorFlag = true;
		} else {
			tfbatchQty.setComponentError(null);
		}
		if (tfbatchTested.getValue() == "" || tfbatchTested.getValue() == null
				|| tfbatchTested.getValue().trim().length() == 0) {
			tfbatchTested.setComponentError(new UserError(GERPErrorCodes.NULL_QC_RESULT));
			errorFlag = true;
		} else {
			tfbatchTested.setComponentError(null);
		}
		if (cbSignOffHdrStatus.getValue() == null) {
			cbSignOffHdrStatus.setComponentError(new UserError(GERPErrorCodes.NULL_QC_RECEIPT));
			errorFlag = true;
		} else {
			cbSignOffHdrStatus.setComponentError(null);
		}
		try {
			Long.valueOf(tfbatchQty.getValue());
			tfbatchQty.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfbatchQty.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TSTSMPL));
			errorFlag = true;
		}
		try {
			Long.valueOf(tfbatchTested.getValue());
			tfbatchTested.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfbatchTested.setComponentError(new UserError(GERPErrorCodes.NULL_QC_FAILED));
			errorFlag = true;
		}
		if (Long.valueOf(tfbatchTested.getValue()) > Long.valueOf(tfbatchQty.getValue())) {
			tfbatchTested.setComponentError(new UserError(GERPErrorCodes.EXCEED_QTY));
			errorFlag = true;
		} else {
			tfbatchTested.setComponentError(null);
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
		try {
			SignoffHdrDM signOffHdr = new SignoffHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				signOffHdr = beanSignoffHdr.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			signOffHdr.setBatchno(tfbatchNo.getValue());
			signOffHdr.setCompanyid(companyId);
			signOffHdr.setBranchid((Long) cbBranch.getValue());
			signOffHdr.setBatchdate(pdBatchDate.getValue());
			signOffHdr.setBatchqty(Long.valueOf(tfbatchQty.getValue()));
			signOffHdr.setClientid((Long) cbClient.getValue());
			signOffHdr.setProductid(Long.valueOf(cbProduct.getValue().toString()));
			signOffHdr.setWoid(Long.valueOf(cbWorkOrderNo.getValue().toString()));
			signOffHdr.setBatchtested(Long.valueOf(tfbatchTested.getValue()));
			signOffHdr.setSignstatus((String) cbSignOffHdrStatus.getValue());
			signOffHdr.setReviewedby(null);
			signOffHdr.setActionedby(null);
			signOffHdr.setPreparedby(null);
			signOffHdr.setLastupdatedby(userName);
			signOffHdr.setLastupdateddt(DateUtils.getcurrentdate());
			serviceSignoffHdr.saveSignoffHdr(signOffHdr);
			signOffHdrId = signOffHdr.getQasignoffid();
			@SuppressWarnings("unchecked")
			Collection<SignOffDtlDM> itemIds = (Collection<SignOffDtlDM>) tblSignOffDtl.getVisibleItemIds();
			for (SignOffDtlDM save : (Collection<SignOffDtlDM>) itemIds) {
				save.setQaSignHdrId(Long.valueOf(signOffHdr.getQasignoffid()));
				serviceSignoffDtl.saveSignoffDtl(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSLNo.getSequenceNumber(companyId, branchId, moduleId, "MF_BATCHNO").get(
							0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSLNo.updateNextSequenceNumber(companyId, branchId, moduleId, "MF_BATCHNO");
					}
				}
				catch (Exception e) {
				}
			}
			resetFields();
			resetSignOffDtlFields();
			loadSrchRslt();
			loadSrchSignOffDtlList();
			comment.saveqaSignOffId(signOffHdr.getQasignoffid());
			comment.resetFields();
			comment.resettbl();
			new TestingDocuments(hlDocumentLayout, signOffHdrId.toString(), "SIGN_OFF");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveSignOffDtlList() {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
			SignOffDtlDM signOffDtl = new SignOffDtlDM();
			if (tblSignOffDtl.getValue() != null) {
				signOffDtl = beanSignoffDtl.getItem(tblSignOffDtl.getValue()).getBean();
				listSignOffDtlDM.remove(signOffDtl);
			}
			signOffDtl.setQaTstId(((QATestHdrDM) cbInspectionNo.getValue()).getQatestHdrid());
			signOffDtl.setInspectionNo(((QATestHdrDM) cbInspectionNo.getValue()).getInspectionno());
			signOffDtl.setProductSlNo(tfProductSlNo.getValue());
			signOffDtl.setSignStatus((String) cbSignOffDtStatus.getValue());
			signOffDtl.setLastUpdatedBy(userName);
			signOffDtl.setLastUpdatedDt(DateUtils.getcurrentdate());
			listSignOffDtlDM.add(signOffDtl);
			resetSignOffDtlFields();
			loadSrchSignOffDtlList();
			btnAdd.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Boolean validateSignOffDtlDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "validateTstDefDetails Data ");
		Boolean errorFlag = true;
		if (cbInspectionNo.getValue() == null) {
			cbInspectionNo.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_SPEC));
			errorFlag = false;
		} else {
			cbInspectionNo.setComponentError(null);
		}
		if (tfProductSlNo.getValue() == "" || tfProductSlNo.getValue() == null
				|| tfProductSlNo.getValue().trim().length() == 0) {
			tfProductSlNo.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_RESLT));
			errorFlag = false;
		} else {
			tfProductSlNo.setComponentError(null);
		}
		if (cbSignOffDtStatus.getValue() == null) {
			cbSignOffDtStatus.setComponentError(new UserError(GERPErrorCodes.NULL_QC_TEST_CYC));
			errorFlag = false;
		} else {
			cbSignOffDtStatus.setComponentError(null);
		}
		return errorFlag;
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for SignOff. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_QA_SIGNOFF_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", "");
	}
	
	@Override
	protected void cancelDetails() {
		resetFields();
		resetSignOffDtlFields();
		assembleSearchLayout();
		tfbatchNo.setRequired(false);
		cbClient.setRequired(false);
		cbBranch.setRequired(false);
		cbSignOffHdrStatus.setRequired(false);
		tfbatchNo.setComponentError(null);
		pdBatchDate.setComponentError(null);
		cbClient.setComponentError(null);
		cbBranch.setComponentError(null);
		cbProduct.setComponentError(null);
		cbWorkOrderNo.setComponentError(null);
		tfbatchTested.setComponentError(null);
		tfbatchQty.setComponentError(null);
		cbInspectionNo.setComponentError(null);
		tfProductSlNo.setComponentError(null);
		cbSignOffDtStatus.setComponentError(null);
		cbSignOffHdrStatus.setComponentError(null);
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		tfbatchQty.setValue("0");
		tfbatchTested.setValue("0");
		tfbatchNo.setReadOnly(false);
		tfbatchNo.setValue("");
		cbBranch.setValue(null);
		cbClient.setValue(null);
		cbWorkOrderNo.setValue(null);
		cbProduct.setValue(null);
		cbSignOffHdrStatus.setValue(null);
		taRemaks.setValue("");
		pdBatchDate.setValue(null);
		listSignOffDtlDM = new ArrayList<SignOffDtlDM>();
		tblSignOffDtl.removeAllItems();
	}
	
	private void resetSignOffDtlFields() {
		cbInspectionNo.setValue(null);
		tfProductSlNo.setReadOnly(false);
		tfProductSlNo.setValue("");
		cbSignOffDtStatus.setValue(null);
	}
	
	private void deleteTstDefDetails() {
		SignOffDtlDM signOffDtlDM = new SignOffDtlDM();
		if (tblSignOffDtl.getValue() != null) {
			signOffDtlDM = beanSignoffDtl.getItem(tblSignOffDtl.getValue()).getBean();
			listSignOffDtlDM.remove(signOffDtlDM);
			resetSignOffDtlFields();
			tblSignOffDtl.setValue("");
			loadSrchSignOffDtlList();
			btnDetlIns.setEnabled(false);
		}
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
