/**
 * File Name 		: IndentIssue.java 
 * Description 		: this class is used for add/edit IndentIssue  details. 
 * Author 			: Madhu T
 * Date 			: Oct-18-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 Version       Date           	Modified By               Remarks
 * 0.1         Oct-18-2014     	Madhu T	        Initial Version
 **/
package com.gnts.mms.txn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
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
import com.gnts.mms.domain.txn.IndentDtlDM;
import com.gnts.mms.domain.txn.IndentHdrDM;
import com.gnts.mms.domain.txn.IndentIssueDtlDM;
import com.gnts.mms.domain.txn.IndentIssueHdrDM;
import com.gnts.mms.domain.txn.MaterialLedgerDM;
import com.gnts.mms.service.txn.IndentDtlService;
import com.gnts.mms.service.txn.IndentHdrService;
import com.gnts.mms.service.txn.IndentIssueDtlService;
import com.gnts.mms.service.txn.IndentIssueHdrService;
import com.gnts.mms.service.txn.MaterialLedgerService;
import com.gnts.mms.service.txn.MaterialStockService;
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
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class IndentIssue extends BaseTransUI {
	// Bean Creation
	private IndentIssueHdrService serviceIndentHdr = (IndentIssueHdrService) SpringContextHelper
			.getBean("IndentIssueHdr");
	private IndentIssueDtlService serviceIndentDtl = (IndentIssueDtlService) SpringContextHelper
			.getBean("IndentIssueDtl");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private IndentHdrService serviceIndHdr = (IndentHdrService) SpringContextHelper.getBean("IndentHdr");
	private IndentDtlService serviceIndentDtlDM = (IndentDtlService) SpringContextHelper.getBean("IndentDtl");
	private MaterialStockService serviceMaterialStock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private List<IndentIssueDtlDM> listIssueDetails = null;
	// form layout for input controls
	private FormLayout flIndentIssueCol1, flIndentIssueCol2, flIndentIssueCol3, flColumn4, flColumn5,
			flIndentIssueDtlCol1, flIndentIssueDtlCol2, flIndentIssueDtlCol3, flIndentIssueDtlCol4,
			flIndentIssueDtlCol5;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlIndent = new HorizontalLayout();
	private HorizontalLayout hlIndentslap = new HorizontalLayout();
	private VerticalLayout vlIndent, vlIndentHdrAndDtl;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Work Order Details
	private Button btnAddDtl = new GERPButton("Add", "addbt", this);
	private ComboBox cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	// Dtl Status ComboBox
	private ComboBox cbIndStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private TextField tfIssueQty, tfBalanceQty, tfStockQty;
	private ComboBox cbIssuedTo, cbMatName, cbIntNo;
	private DateField dfIssueDt;
	private TextField taRemarks;
	private Table tblIndtIssueDtl;
	private BeanItemContainer<IndentIssueHdrDM> beanIndentIssueHdrDM = null;
	private BeanItemContainer<IndentIssueDtlDM> beanIndentIssueDtlDM = null;
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private MaterialLedgerService serviceledger = (MaterialLedgerService) SpringContextHelper.getBean("materialledger");
	// local variables declaration
	private String pkIndentId;
	private Long companyid, branchId;
	private Long issueId, indentId, indqty;
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(IndentIssue.class);
	private Object balqty;
	private static final long serialVersionUID = 1L;
	
	// Constructor received the parameters from Login UI class
	public IndentIssue() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside IndentIssue() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		// Initialization for work order Details user input components
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting IndentIssue UI");
		btnAddDtl.setStyleName("add");
		btnAddDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDtlDetails()) {
					saveindentDtlListDetails();
				}
			}
		});
		btndelete.setEnabled(false);
		tblIndtIssueDtl = new GERPTable();
		tblIndtIssueDtl.setPageLength(10);
		tblIndtIssueDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblIndtIssueDtl.isSelected(event.getItemId())) {
					tblIndtIssueDtl.setImmediate(true);
					btnAddDtl.setCaption("Add");
					btnAddDtl.setStyleName("savebt");
					btndelete.setEnabled(false);
					IndentDtlresetField();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtl.setCaption("Update");
					btnAddDtl.setStyleName("savebt");
					btndelete.setEnabled(true);
					editDtls();
				}
			}
		});
		btndelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndelete == event.getButton()) {
					btnAddDtl.setCaption("Add");
					deleteDetails();
				}
			}
		});
		// Indent No. TextField
		cbIntNo = new GERPComboBox("Indent No");
		cbIntNo.setItemCaptionPropertyId("indentNo");
		loadIndentList();
		cbIntNo.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				loadMaterialList();
			}
		});
		// Issue Date PopupDateField
		dfIssueDt = new PopupDateField("Issue Date");
		dfIssueDt.setDateFormat("dd-MMM-yyyy");
		// Issued To GERPComboBox
		cbIssuedTo = new GERPComboBox("Issued To");
		cbIssuedTo.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		// Remarks TextArea
		taRemarks = new TextField("Remarks");
		taRemarks.setNullRepresentation("");
		// Hdr Status ComboBox
		cbIndStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Indent Detail
		// Material Name combobox
		cbMatName = new GERPComboBox("Material");
		cbMatName.setItemCaptionPropertyId("materialName");
		cbMatName.setImmediate(true);
		cbMatName.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbMatName.getValue() != null) {
					tfBalanceQty.setValue(((IndentDtlDM) cbMatName.getValue()).getBalenceQty() + "");
					loadMaterial();
				}
			}
		});
		// Indent Qty.GERPTextField
		tfIssueQty = new GERPTextField("Issue Qty.");
		tfIssueQty.setValue("0");
		tfIssueQty.setWidth("70");
		tfIssueQty.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (tfIssueQty.getValue() != null) {
					loadCalc();
				}
			}
		});
		tfBalanceQty = new GERPTextField("Balance Qty");
		tfBalanceQty.setValue("0");
		tfBalanceQty.setWidth("70");
		tfStockQty = new GERPTextField("Stock Qty");
		tfStockQty.setValue("0");
		tfStockQty.setWidth("70");
		tfStockQty.setReadOnly(true);
		// Hdr Combobox
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Indent No text field
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadIndentDtl();
		btnAddDtl.setStyleName("add");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flIndentIssueCol1 = new FormLayout();
		flIndentIssueCol2 = new FormLayout();
		flIndentIssueCol3 = new FormLayout();
		flColumn4 = new FormLayout();
		flIndentIssueCol1.addComponent(cbIntNo);
		flIndentIssueCol3.addComponent(dfIssueDt);
		flColumn4.addComponent(cbIndStatus);
		hlSearchLayout.addComponent(flIndentIssueCol1);
		hlSearchLayout.addComponent(flIndentIssueCol2);
		hlSearchLayout.addComponent(flIndentIssueCol3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flIndentIssueCol1 = new FormLayout();
		flIndentIssueCol2 = new FormLayout();
		flIndentIssueCol3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn5 = new FormLayout();
		flIndentIssueCol1.addComponent(cbIntNo);
		flIndentIssueCol2.addComponent(dfIssueDt);
		flIndentIssueCol3.addComponent(cbIssuedTo);
		flColumn4.addComponent(taRemarks);
		flColumn5.addComponent(cbIndStatus);
		hlIndent = new HorizontalLayout();
		hlIndent.addComponent(flIndentIssueCol1);
		hlIndent.addComponent(flIndentIssueCol2);
		hlIndent.addComponent(flIndentIssueCol3);
		hlIndent.addComponent(flColumn4);
		hlIndent.addComponent(flColumn5);
		hlIndent.setSpacing(true);
		hlIndent.setMargin(true);
		// Adding IndentIssueSlap components
		// Add components for User Input Layout
		flIndentIssueDtlCol1 = new FormLayout();
		flIndentIssueDtlCol2 = new FormLayout();
		flIndentIssueDtlCol3 = new FormLayout();
		flIndentIssueDtlCol4 = new FormLayout();
		flIndentIssueDtlCol5 = new FormLayout();
		flIndentIssueDtlCol1.addComponent(cbMatName);
		flIndentIssueDtlCol2.addComponent(tfBalanceQty);
		flIndentIssueDtlCol3.addComponent(tfStockQty);
		flIndentIssueDtlCol4.addComponent(tfIssueQty);
		flIndentIssueDtlCol5.addComponent(cbDtlStatus);
		hlIndentslap = new HorizontalLayout();
		hlIndentslap.addComponent(flIndentIssueDtlCol1);
		hlIndentslap.addComponent(flIndentIssueDtlCol2);
		hlIndentslap.addComponent(flIndentIssueDtlCol3);
		hlIndentslap.addComponent(flIndentIssueDtlCol4);
		hlIndentslap.addComponent(flIndentIssueDtlCol5);
		hlIndentslap.addComponent(btnAddDtl);
		hlIndentslap.setComponentAlignment(btnAddDtl, Alignment.MIDDLE_LEFT);
		hlIndentslap.addComponent(btndelete);
		hlIndentslap.setComponentAlignment(btndelete, Alignment.MIDDLE_LEFT);
		hlIndentslap.setSpacing(true);
		hlIndentslap.setMargin(true);
		vlIndent = new VerticalLayout();
		vlIndent.addComponent(hlIndentslap);
		vlIndent.addComponent(tblIndtIssueDtl);
		vlIndent.setSpacing(true);
		vlIndentHdrAndDtl = new VerticalLayout();
		vlIndentHdrAndDtl.addComponent(GERPPanelGenerator.createPanel(hlIndent));
		vlIndentHdrAndDtl.addComponent(GERPPanelGenerator.createPanel(vlIndent));
		vlIndentHdrAndDtl.setSpacing(true);
		vlIndentHdrAndDtl.setWidth("100%");
		hlUserInputLayout.addComponent(vlIndentHdrAndDtl);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<IndentIssueHdrDM> indentIssueList = new ArrayList<IndentIssueHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbIntNo.getValue() + ", " + cbIndStatus.getValue());
		indentIssueList = serviceIndentHdr.getIndentIssueHdrList(null, companyid, (Long) cbIntNo.getValue(),
				dfIssueDt.getValue(), null, (String) cbIndStatus.getValue(), "F");
		recordCnt = indentIssueList.size();
		beanIndentIssueHdrDM = new BeanItemContainer<IndentIssueHdrDM>(IndentIssueHdrDM.class);
		beanIndentIssueHdrDM.addAll(indentIssueList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Indent. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanIndentIssueHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "issueId", "indentNo", "issueDate", "issueStatus",
				"last_updated_dt", "last_updated_by" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Indent No", "Issue Date", "Status",
				"Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("issueId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("last_updated_by", "No.of Records : " + recordCnt);
	}
	
	private void loadIndentDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			logger.info("Company ID : " + companyid + " | saveindentDtlListDetails User Name : " + username + " > "
					+ "Search Parameters are " + companyid + ", " + tfIssueQty.getValue() + ", "
					+ (String) cbDtlStatus.getValue() + ", " + issueId);
			recordCnt = listIssueDetails.size();
			beanIndentIssueDtlDM = new BeanItemContainer<IndentIssueDtlDM>(IndentIssueDtlDM.class);
			beanIndentIssueDtlDM.addAll(listIssueDetails);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the IndentIssueslap. result set");
			tblIndtIssueDtl.setContainerDataSource(beanIndentIssueDtlDM);
			tblIndtIssueDtl.setVisibleColumns(new Object[] { "materialName", "issueQty", "status", "lastUpdatedDt",
					"lastUpdatedBy" });
			tblIndtIssueDtl.setColumnHeaders(new String[] { "Material", "Issue Qty", "Status", "Updated Date",
					"Updated By" });
			tblIndtIssueDtl.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
			tblIndtIssueDtl.setPageLength(10);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbIntNo.setValue(null);
		dfIssueDt.setValue(null);
		cbIssuedTo.setValue(null);
		tfBalanceQty.setValue(null);
		tfIssueQty.setValue(null);
		taRemarks.setValue(null);
		cbMatName.setValue(null);
		cbIndStatus.setValue(cbIndStatus.getItemIds().iterator().next());
		cbIntNo.setComponentError(null);
		dfIssueDt.setComponentError(null);
		cbIssuedTo.setComponentError(null);
		cbMatName.setComponentError(null);
		cbMatName.setContainerDataSource(null);
		tfIssueQty.setComponentError(null);
		listIssueDetails = new ArrayList<IndentIssueDtlDM>();
		tblIndtIssueDtl.removeAllItems();
		tfStockQty.setReadOnly(false);
		tfStockQty.setValue(null);
		tfStockQty.setReadOnly(false);
	}
	
	// Method to edit the values from table into fields to update process
	private void editHdrIndentDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			IndentIssueHdrDM editHdrIndent = beanIndentIssueHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			issueId = editHdrIndent.getIssueId();
			System.out.println("issueId-->" + issueId);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected IndentIssue. Id -> " + issueId);
			cbIntNo.setValue(editHdrIndent.getIndentId());
			if (editHdrIndent.getIssueDate() != null) {
				dfIssueDt.setValue(editHdrIndent.getIssueDate1());
			}
			taRemarks.setValue(editHdrIndent.getIssueRemarks());
			cbIssuedTo.setValue(editHdrIndent.getIssuedTo());
			cbIndStatus.setValue(editHdrIndent.getIssueStatus());
			listIssueDetails.addAll(serviceIndentDtl.getIndentIssueDtlDMList(null, issueId, null, null, "Active", "F"));
		}
		loadIndentDtl();
	}
	
	private void editDtls() {
		hlUserInputLayout.setVisible(true);
		if (tblIndtIssueDtl.getValue() != null) {
			IndentIssueDtlDM indentIssueDtlDM = beanIndentIssueDtlDM.getItem(tblIndtIssueDtl.getValue()).getBean();
			Long matId = indentIssueDtlDM.getMaterialId();
			Collection<?> matids = cbMatName.getItemIds();
			for (Iterator<?> iteratorclient = matids.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMatName.getItem(itemIdClient);
				// Get the actual bean and use the data
				IndentDtlDM matObj = (IndentDtlDM) itemclient.getBean();
				if (matId != null && matId.equals(matObj.getMaterialId())) {
					cbMatName.setValue(itemIdClient);
				}
			}
			if (indentIssueDtlDM.getIssueQty() != null) {
				tfIssueQty.setValue(indentIssueDtlDM.getIssueQty().toString());
			}
			if (indentIssueDtlDM.getStockQty() != null) {
				tfStockQty.setReadOnly(false);
				tfStockQty.setValue(indentIssueDtlDM.getStockQty().toString());
				tfStockQty.setReadOnly(true);
			}
			cbDtlStatus.setValue(indentIssueDtlDM.getStatus());
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
		cbIntNo.setValue(null);
		dfIssueDt.setValue(null);
		cbIndStatus.setValue(cbIndStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		cbIntNo.setRequired(true);
		dfIssueDt.setRequired(false);
		dfIssueDt.setValue(new Date());
		dfIssueDt.setRequired(true);
		cbIssuedTo.setRequired(true);
		cbMatName.setRequired(true);
		tfIssueQty.setRequired(true);
		resetFields();
		dfIssueDt.setRequired(false);
		dfIssueDt.setValue(new Date());
		dfIssueDt.setRequired(true);
		assembleInputUserLayout();
		loadIndentDtl();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtl.setCaption("Add");
		tblIndtIssueDtl.setVisible(true);
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for IndentIssue. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MMS_INDENT_ISSUE_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkIndentId);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		cbIntNo.setComponentError(null);
		dfIssueDt.setComponentError(null);
		cbIssuedTo.setComponentError(null);
		cbMatName.setComponentError(null);
		tfIssueQty.setComponentError(null);
		tfStockQty.setReadOnly(false);
		tfStockQty.setComponentError(null);
		tfStockQty.setReadOnly(true);
		cbIntNo.setRequired(false);
		dfIssueDt.setRequired(false);
		cbIssuedTo.setRequired(false);
		cbMatName.setRequired(false);
		tfIssueQty.setRequired(false);
		IndentDtlresetField();
		hlCmdBtnLayout.setVisible(true);
		tblIndtIssueDtl.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		cbIntNo.setRequired(true);
		dfIssueDt.setRequired(true);
		cbIssuedTo.setRequired(true);
		cbMatName.setRequired(true);
		tfIssueQty.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		resetFields();
		editHdrIndentDetails();
		editDtls();
	}
	
	private void IndentDtlresetField() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMatName.setValue(null);
		tfBalanceQty.setValue(null);
		tfIssueQty.setReadOnly(false);
		tfIssueQty.setValue(null);
		tfStockQty.setReadOnly(false);
		tfStockQty.setValue(null);
		tfStockQty.setReadOnly(true);
		// cbMatName.setContainerDataSource(null);
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
	}
	
	private boolean validateDtlDetails() {
		boolean isValid = true;
		tfIssueQty.setComponentError(null);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbMatName.getValue() == null) {
			cbMatName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			isValid = false;
		} else {
			cbMatName.setComponentError(null);
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfIssueQty.getValue());
			if (achievedQty < 0) {
				tfIssueQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfIssueQty.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATION));
			isValid = false;
		}
		return isValid;
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbIntNo.setComponentError(null);
		dfIssueDt.setComponentError(null);
		cbIssuedTo.setComponentError(null);
		cbMatName.setComponentError(null);
		tfIssueQty.setComponentError(null);
		errorFlag = false;
		if (cbIntNo.getValue() == null) {
			cbIntNo.setComponentError(new UserError(GERPErrorCodes.NULL_INDENT_NO));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbIntNo.getValue());
			errorFlag = true;
		}
		if (dfIssueDt.getValue() == null) {
			dfIssueDt.setComponentError(new UserError(GERPErrorCodes.NULL_ISSUE_DATE));
			errorFlag = true;
		}
		if (cbIssuedTo.getValue() == null) {
			cbIssuedTo.setComponentError(new UserError(GERPErrorCodes.NULL_Issue_To));
			errorFlag = true;
		}
		if (tblIndtIssueDtl.size() == 0) {
			cbMatName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			tfIssueQty.setComponentError(new UserError(GERPErrorCodes.QUNATITY_CHAR_VALIDATION));
			errorFlag = true;
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
			IndentIssueHdrDM indentObj = new IndentIssueHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				indentObj = beanIndentIssueHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			indentObj.setCompanyId(companyid);
			indentObj.setIndentId(Long.valueOf(cbIntNo.getValue().toString()));
			indentObj.setIssueDate((Date) dfIssueDt.getValue());
			indentObj.setIssuedTo((Long.valueOf(cbIssuedTo.getValue().toString())));
			indentObj.setIssueRemarks((String) taRemarks.getValue());
			if (cbIndStatus.getValue() != null) {
				indentObj.setIssueStatus((String) cbIndStatus.getValue());
			}
			indentObj.setLast_updated_dt(DateUtils.getcurrentdate());
			indentObj.setLast_updated_by(username);
			serviceIndentHdr.saveorUpdateIndentIssueHdrDetails(indentObj);
			@SuppressWarnings("unchecked")
			Collection<IndentIssueDtlDM> colPlanDtls = ((Collection<IndentIssueDtlDM>) tblIndtIssueDtl
					.getVisibleItemIds());
			for (IndentIssueDtlDM saveDtl : (Collection<IndentIssueDtlDM>) colPlanDtls) {
				saveDtl.setIssueId(indentObj.getIssueId());
				saveDtl.setIndentId(indentObj.getIndentId());
				balqty = 0L;
				try {
					balqty = serviceIndentDtlDM.getIndentDtlDMList(null, indentId, saveDtl.getMaterialId(), null, "F")
							.get(0).getBalenceQty();
				}
				catch (Exception e) {
				}
				indqty = (Long) balqty - Long.valueOf(saveDtl.getIssueQty());
				serviceIndentDtl.saveorUpdate(saveDtl);
				serviceIndentDtlDM.updateissueqty(indentObj.getIndentId(), saveDtl.getMaterialId(), indqty);
				IndentIssueHdrDM indentissuehdrObj = new IndentIssueHdrDM();
				try {
					MaterialLedgerDM materialLedgerDM = null;
					try {
						materialLedgerDM = serviceledger.getMaterialLedgerList(saveDtl.getMaterialId(), null, null,
								null, "New", null, "Y", "F").get(0);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					if (materialLedgerDM == null) {
						MaterialLedgerDM ledgerDM = new MaterialLedgerDM();
						ledgerDM.setStockledgeDate(new Date());
						ledgerDM.setCompanyId(companyid);
						ledgerDM.setBranchId(branchId);
						ledgerDM.setMaterialId(saveDtl.getMaterialId());
						ledgerDM.setStockType("New");
						ledgerDM.setOpenQty(0L);
						ledgerDM.setInoutFlag("O");
						ledgerDM.setInoutFQty(saveDtl.getIssueQty());
						ledgerDM.setCloseQty(ledgerDM.getOpenQty() - saveDtl.getIssueQty());
						ledgerDM.setReferenceNo(indentissuehdrObj.getIndentNo());
						ledgerDM.setReferenceDate(indentissuehdrObj.getIssueDate1());
						ledgerDM.setIsLatest("Y");
						ledgerDM.setReferenceRemark(indentissuehdrObj.getIssueRemarks());
						ledgerDM.setLastUpdatedby(username);
						ledgerDM.setLastUpdateddt(DateUtils.getcurrentdate());
						serviceledger.saveOrUpdateLedger(ledgerDM);
					} else {
						MaterialLedgerDM ledgerDM = new MaterialLedgerDM();
						ledgerDM.setStockledgeDate(new Date());
						ledgerDM.setCompanyId(companyid);
						ledgerDM.setBranchId(branchId);
						ledgerDM.setMaterialId(saveDtl.getMaterialId());
						ledgerDM.setStockType("New");
						ledgerDM.setOpenQty(materialLedgerDM.getCloseQty());
						ledgerDM.setInoutFlag("O");
						ledgerDM.setInoutFQty(saveDtl.getIssueQty());
						ledgerDM.setCloseQty(ledgerDM.getOpenQty() - saveDtl.getIssueQty());
						ledgerDM.setReferenceNo(indentissuehdrObj.getIndentNo());
						ledgerDM.setReferenceDate(indentissuehdrObj.getIssueDate1());
						ledgerDM.setIsLatest("Y");
						ledgerDM.setReferenceRemark(indentissuehdrObj.getIssueRemarks());
						ledgerDM.setLastUpdatedby(username);
						ledgerDM.setLastUpdateddt(DateUtils.getcurrentdate());
						serviceledger.saveOrUpdateLedger(ledgerDM);
						materialLedgerDM.setIsLatest("N");
						serviceledger.saveOrUpdateLedger(materialLedgerDM);
					}
					if (materialLedgerDM == null) {
						MaterialLedgerDM ledgerDM = new MaterialLedgerDM();
						ledgerDM.setStockledgeDate(new Date());
						ledgerDM.setCompanyId(companyid);
						ledgerDM.setBranchId(branchId);
						ledgerDM.setMaterialId(saveDtl.getMaterialId());
						ledgerDM.setStockType("New");
						ledgerDM.setOpenQty(0L);
						ledgerDM.setInoutFlag("I");
						ledgerDM.setInoutFQty(saveDtl.getIssueQty());
						ledgerDM.setCloseQty(ledgerDM.getOpenQty() - saveDtl.getIssueQty());
						ledgerDM.setReferenceNo(indentissuehdrObj.getIndentNo());
						ledgerDM.setReferenceDate(indentissuehdrObj.getIssueDate1());
						ledgerDM.setIsLatest("Y");
						ledgerDM.setReferenceRemark(indentissuehdrObj.getIssueRemarks());
						ledgerDM.setLastUpdatedby(username);
						ledgerDM.setLastUpdateddt(DateUtils.getcurrentdate());
						serviceledger.saveOrUpdateLedger(ledgerDM);
					} else {
						MaterialLedgerDM ledgerDM = new MaterialLedgerDM();
						ledgerDM.setStockledgeDate(new Date());
						ledgerDM.setCompanyId(companyid);
						ledgerDM.setBranchId(branchId);
						ledgerDM.setMaterialId(saveDtl.getMaterialId());
						ledgerDM.setStockType("New");
						ledgerDM.setOpenQty(materialLedgerDM.getCloseQty());
						ledgerDM.setInoutFlag("I");
						ledgerDM.setInoutFQty(saveDtl.getIssueQty());
						ledgerDM.setCloseQty(ledgerDM.getOpenQty() - saveDtl.getIssueQty());
						ledgerDM.setReferenceNo(indentissuehdrObj.getIndentNo());
						ledgerDM.setReferenceDate(indentissuehdrObj.getIssueDate1());
						ledgerDM.setIsLatest("Y");
						ledgerDM.setReferenceRemark(indentissuehdrObj.getIssueRemarks());
						ledgerDM.setLastUpdatedby(username);
						ledgerDM.setLastUpdateddt(DateUtils.getcurrentdate());
						serviceledger.saveOrUpdateLedger(ledgerDM);
						materialLedgerDM.setIsLatest("N");
						serviceledger.saveOrUpdateLedger(materialLedgerDM);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveindentDtlListDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			int count = 0;
			for (IndentIssueDtlDM indentIssueDtlDM : listIssueDetails) {
				if (indentIssueDtlDM.getMaterialId() == ((IndentDtlDM) cbMatName.getValue()).getMaterialId()) {
					count++;
					break;
				}
			}
			if (count == 0) {
				if (((IndentDtlDM) cbMatName.getValue()).getBalenceQty() >= Long.valueOf(tfIssueQty.getValue())) {
					IndentIssueDtlDM indentDtlObj = new IndentIssueDtlDM();
					if (tblIndtIssueDtl.getValue() != null) {
						indentDtlObj = beanIndentIssueDtlDM.getItem(tblIndtIssueDtl.getValue()).getBean();
						listIssueDetails.remove(indentDtlObj);
					}
					if (cbMatName.getValue() != null) {
						indentDtlObj.setMaterialId(((IndentDtlDM) cbMatName.getValue()).getMaterialId());
						indentDtlObj.setMaterialName(((IndentDtlDM) cbMatName.getValue()).getMaterialName());
					}
					indentDtlObj.setIssueQty(Long.valueOf(tfIssueQty.getValue()));
					if (cbDtlStatus.getValue() != null) {
						indentDtlObj.setStatus((String) cbDtlStatus.getValue());
					}
					tfIssueQty.setReadOnly(false);
					if (tfIssueQty.getValue() != null) {
						indentDtlObj.setStockQty(Long.parseLong(tfStockQty.getValue()));
					}
					tfIssueQty.setReadOnly(true);
					indentDtlObj.setLastUpdatedDt(DateUtils.getcurrentdate());
					indentDtlObj.setLastUpdatedBy(username);
					listIssueDetails.add(indentDtlObj);
					loadIndentDtl();
					IndentDtlresetField();
					btnAddDtl.setCaption("Add");
				} else {
					tfIssueQty.setComponentError(new UserError(
							"Enter Issue qty greater than Indent qty \n Balance Qty="
									+ ((IndentDtlDM) cbMatName.getValue()).getBalenceQty()));
				}
			} else {
				cbMatName.setComponentError(new UserError("Material Already Exist.."));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * loadMaterialList()-->this function is used for load the Material name
	 */
	private void loadMaterialList() {
		tfStockQty.setReadOnly(false);
		tfStockQty.setValue(null);
		tfStockQty.setReadOnly(true);
		BeanItemContainer<IndentDtlDM> beanIndentDtl = new BeanItemContainer<IndentDtlDM>(IndentDtlDM.class);
		beanIndentDtl
				.addAll(serviceIndentDtlDM.getIndentDtlDMList(null, (Long) cbIntNo.getValue(), null, "Active", "F"));
		cbMatName.setContainerDataSource(beanIndentDtl);
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the Employee name
	 */
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, EmployeeDM> beanEmployeeDM = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmployeeDM.setBeanIdProperty("employeeid");
		beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null, null,
				null, "P"));
		cbIssuedTo.setContainerDataSource(beanEmployeeDM);
	}
	
	/*
	 * loadIndentList()-->this function is used for load the IndentNo
	 */
	private void loadIndentList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, IndentHdrDM> beanIndentHdrDM = new BeanContainer<Long, IndentHdrDM>(IndentHdrDM.class);
		beanIndentHdrDM.setBeanIdProperty("indentId");
		beanIndentHdrDM.addAll(serviceIndHdr.getMmsIndentHdrList(null, null, null, companyid, null, null, null, null,
				null, "F"));
		cbIntNo.setContainerDataSource(beanIndentHdrDM);
	}
	
	// Load Stock of Selected Material.
	private void loadMaterial() {
		tfStockQty.setReadOnly(false);
		tfStockQty.setValue(serviceMaterialStock
				.getMaterialStockList(((IndentDtlDM) cbMatName.getValue()).getMaterialId(), null, null, null, null,
						null, "F").get(0).getCurrentStock().toString());
		tfStockQty.setReadOnly(true);
	}
	
	private void loadCalc() {
		try {
			tfStockQty.setReadOnly(false);
			if (cbMatName != null && tfIssueQty != null) {
				Long ltotalOty = new BigDecimal(tfStockQty.getValue()).subtract(new BigDecimal(tfIssueQty.getValue()))
						.longValue();
				tfStockQty.setValue(ltotalOty.toString());
			}
			tfStockQty.setReadOnly(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void deleteDetails() {
		IndentIssueDtlDM save = new IndentIssueDtlDM();
		if (tblIndtIssueDtl.getValue() != null) {
			save = beanIndentIssueDtlDM.getItem(tblIndtIssueDtl.getValue()).getBean();
			listIssueDetails.remove(save);
			IndentDtlresetField();
			loadIndentDtl();
			btndelete.setEnabled(false);
		}
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
