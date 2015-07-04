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
import com.gnts.erputil.ui.BaseUI;
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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SignOff extends BaseUI {
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
	private TextField tfproductSlNo;
	private ComboBox cbInspectionNo, cbSignOffDtStatus;
	private Button btnAdd;
	private Table tblSignOffDtl;
	private SignOffDtlService serviceSignoffDtl = (SignOffDtlService) SpringContextHelper.getBean("SignoffDtl");
	private List<SignOffDtlDM> listSignOffDtlDM = null;
	private BeanItemContainer<SignOffDtlDM> beanSignoffDtl = null;
	private FormLayout flSignOffDtlCmp1, flSignOffDtlCmp2, flSignOffDtlCmp3;
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
	private Logger logger = Logger.getLogger(QCTestType.class);
	private int recordCnt;
	private Long signOffHdrId;
	private String userName;
	private Long companyId;
	private Long moduleId;
	private HorizontalLayout hlSearchLayout;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private Long branchID;
	private Comments comment;
	private Long commentby;
	private Long appScreenId;
	private Long roleId;
	private Button btnDetlIns = new GERPButton("Delete", "delete", this);
	
	public SignOff() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = Long.valueOf(UI.getCurrent().getSession().getAttribute("moduleId").toString());
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
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
				loadWorkOrderNoList();
				cbWorkOrderNo.setValue(cbWorkOrderNo.getItemIds().iterator().next());
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
				loadProductList();
				cbProduct.setValue(cbProduct.getItemIds().iterator().next());
			}
		});
		List<ApprovalSchemaDM> list = serviceSignoffHdr.getReviewerId(companyId, appScreenId, branchID, roleId);
		for (ApprovalSchemaDM obj : list) {
			if (obj.getApprLevel().equals("Reviewer")) {
				cbSignOffHdrStatus = new GERPComboBox("Status", BASEConstants.T_MFG_QA_SIGNOFF_HDR,
						BASEConstants.SGN_RVER);
			} else {
				cbSignOffHdrStatus = new GERPComboBox("Status", BASEConstants.T_MFG_QA_SIGNOFF_HDR,
						BASEConstants.SGN_APVER);
			}
		}
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
		pdBatchDate = new GERPPopupDateField("Batch Date");
		//
		tfproductSlNo = new GERPTextField("Product Sl.NO");
		cbInspectionNo = new GERPComboBox("Inspection No.");
		cbInspectionNo.setItemCaptionPropertyId("inspectionno");
		cbSignOffDtStatus = new GERPComboBox("Status", BASEConstants.T_MFG_QA_SIGNOFF_HDR, BASEConstants.SGN_APVER);
		loadInspectionNo();
		cbInspectionNo.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbInspectionNo.getValue() != null) {
					tfproductSlNo.setReadOnly(false);
					tfproductSlNo.setValue((serviceQATestHdr.getQaTestHdrDetails(
							((QATestHdrDM) cbInspectionNo.getValue()).getQatestHdrid(), companyId, null, null, null,
							"Active").get(0).getProdslno()));
					tfproductSlNo.setReadOnly(true);
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
		tfproductSlNo.setRequired(true);
		tfbatchTested.setRequired(true);
		cbSignOffDtStatus.setRequired(true);
		cbSignOffHdrStatus.setRequired(true);
		// QATest Header Components adding to the User input layout
		flSignOffCmp1 = new FormLayout();
		flSignOffCmp2 = new FormLayout();
		flSignOffCmp3 = new FormLayout();
		flSignOffCmp4 = new FormLayout();
		//
		flSignOffCmp1.addComponent(tfbatchNo);
		flSignOffCmp1.addComponent(pdBatchDate);
		flSignOffCmp1.addComponent(cbBranch);
		flSignOffCmp2.addComponent(cbClient);
		flSignOffCmp2.addComponent(cbWorkOrderNo);
		flSignOffCmp2.addComponent(cbProduct);
		flSignOffCmp3.addComponent(tfbatchQty);
		flSignOffCmp3.addComponent(tfbatchTested);
		flSignOffCmp3.addComponent(cbSignOffHdrStatus);
		flSignOffCmp4.addComponent(taRemaks);
		//
		hlSignOffHdr.addComponent(flSignOffCmp1);
		hlSignOffHdr.addComponent(flSignOffCmp2);
		hlSignOffHdr.addComponent(flSignOffCmp3);
		hlSignOffHdr.addComponent(flSignOffCmp4);
		hlSignOffHdr.setSpacing(true);
		hlSignOffHdr.setMargin(true);
		//
		flSignOffDtlCmp1 = new FormLayout();
		flSignOffDtlCmp2 = new FormLayout();
		flSignOffDtlCmp3 = new FormLayout();
		flSignOffDtlCmp1.addComponent(cbInspectionNo);
		flSignOffDtlCmp1.addComponent(tfproductSlNo);
		flSignOffDtlCmp2.addComponent(cbSignOffDtStatus);
		flSignOffDtlCmp2.addComponent(btnAdd);
		flSignOffDtlCmp3.addComponent(btnDetlIns);
		//
		hlSignOffDtl.addComponent(flSignOffDtlCmp1);
		hlSignOffDtl.addComponent(flSignOffDtlCmp2);
		hlSignOffDtl.addComponent(flSignOffDtlCmp3);
		hlSignOffDtl.setComponentAlignment(flSignOffDtlCmp3, Alignment.BOTTOM_CENTER);
		hlSignOffDtl.setSpacing(true);
		hlSignOffDtl.setMargin(true);
		//
		VerticalLayout vlSignOffHdr = new VerticalLayout();
		vlSignOffHdr.addComponent(hlSignOffHdr);
		vlSignOffHdr.addComponent(GERPPanelGenerator.createPanel(hlSignOffHdr));
		vlSignOffHdr.setSpacing(true);
		//
		VerticalLayout vlSignOffDtl = new VerticalLayout();
		vlSignOffDtl.addComponent(hlSignOffDtl);
		vlSignOffDtl.addComponent(tblSignOffDtl);
		//
		hlDtlNCmt.addComponent(vlSignOffDtl);
		hlDtlNCmt.addComponent(GERPPanelGenerator.createPanel(vlSignOffDtl));
		hlDtlNCmt.addComponent(vlTableForm);
		hlDtlNCmt.addComponent(GERPPanelGenerator.createPanel(vlTableForm));
		hlDtlNCmt.setWidth("100%");
		hlDtlNCmt.setSpacing(true);
		//
		VerticalLayout vlSignOff = new VerticalLayout();
		vlSignOff.addComponent(vlSignOffHdr);
		vlSignOff.addComponent(hlDtlNCmt);
		vlSignOff.setSpacing(true);
		//
		hlUserInputLayout.addComponent(vlSignOff);
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setSpacing(true);
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
		tblMstScrSrchRslt
				.setVisibleColumns(new Object[] { "qasignoffid", "batchno", "batchdate", "branchName", "clientName",
						"productName", "workOrdNo", "batchqty", "batchtested", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Batch No.", "Batch Dt.", "Branch Name",
				"Client Name", "Product Name", "Work Ord.No", "Batch Qty.", "Batch Tested", "Last Updated Dt.",
				"Last Updated By" });
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
				null, null, "F", null, null));
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
									"F", (Long) cbWorkOrderNo.getValue(), null).get(0).getWorkOrdrId()), "Approved",
							"F").get(0).getProdId()), null, null, "Active", null, null, "F"));
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
		List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyId, branchID, moduleId, "MF_BATCHNO");
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfbatchNo.setReadOnly(true);
			} else {
				tfbatchNo.setReadOnly(false);
			}
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
		List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyId, branchID, moduleId, "MF_QAINSNO");
		tfbatchNo.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfbatchNo.setReadOnly(true);
			} else {
				tfbatchNo.setReadOnly(false);
			}
		}
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tblSignOffDtl.setVisible(true);
		if (tfbatchNo.getValue() == null || tfbatchNo.getValue().trim().length() == 0) {
			tfbatchNo.setReadOnly(false);
		}
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
				tfbatchNo.setValue(signoffHdrDM.getBatchno());
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
			tfproductSlNo.setValue(signOffDtlDM.getProductSlNo());
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
			} else {
				List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyId, +branchID, moduleId, "MF_BATCHNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						signOffHdr.setBatchno(slnoObj.getKeyDesc());
					}
				}
			}
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
			@SuppressWarnings("unchecked")
			Collection<SignOffDtlDM> itemIds = (Collection<SignOffDtlDM>) tblSignOffDtl.getVisibleItemIds();
			for (SignOffDtlDM save : (Collection<SignOffDtlDM>) itemIds) {
				save.setQaSignHdrId(Long.valueOf(signOffHdr.getQasignoffid()));
				serviceSignoffDtl.saveSignoffDtl(save);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSLNo.getSequenceNumber(companyId, branchID, moduleId, "MF_BATCHNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSLNo.updateNextSequenceNumber(companyId, branchID, moduleId, "MF_BATCHNO");
					}
				}
			}
			resetFields();
			resetSignOffDtlFields();
			loadSrchRslt();
			loadSrchSignOffDtlList();
			comment.saveqaSignOffId(signOffHdr.getQasignoffid());
			comment.resetFields();
			comment.resettbl();
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
			signOffDtl.setProductSlNo(tfproductSlNo.getValue());
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
		if (tfproductSlNo.getValue() == "" || tfproductSlNo.getValue() == null
				|| tfproductSlNo.getValue().trim().length() == 0) {
			tfproductSlNo.setComponentError(new UserError(GERPErrorCodes.NULL_QATST_RESLT));
			errorFlag = false;
		} else {
			tfproductSlNo.setComponentError(null);
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
		tfproductSlNo.setComponentError(null);
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
		// comment.resetFields();
	}
	
	public void resetSignOffDtlFields() {
		cbInspectionNo.setValue(null);
		tfproductSlNo.setReadOnly(false);
		tfproductSlNo.setValue("");
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
}
