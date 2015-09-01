package com.gnts.die.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.die.domain.txn.DieBOMDtlDM;
import com.gnts.die.domain.txn.DieBOMHdrDM;
import com.gnts.die.domain.txn.DieCompletionDtlDM;
import com.gnts.die.domain.txn.DieCompletionHdrDM;
import com.gnts.die.domain.txn.DieRequestDM;
import com.gnts.die.domain.txn.DieSectionDM;
import com.gnts.die.domain.txn.MoldTrailReqDtlDM;
import com.gnts.die.domain.txn.MoldTrailReqHdrDM;
import com.gnts.die.service.txn.DieBOMDtlService;
import com.gnts.die.service.txn.DieBOMHdrService;
import com.gnts.die.service.txn.DieCompletionDtlService;
import com.gnts.die.service.txn.DieCompletionHdrService;
import com.gnts.die.service.txn.DieRequestService;
import com.gnts.die.service.txn.DieSectionService;
import com.gnts.die.service.txn.MoldTrailReqDtlService;
import com.gnts.die.service.txn.MoldTrailReqHdrService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPNumberField;
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
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.txn.TestingDocuments;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsEnquiryDtlDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsEnquiryDtlService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author soundarc
 * 
 */
public class DieRequest extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private DieRequestService serviceDieRequest = (DieRequestService) SpringContextHelper.getBean("dieRequest");
	private DieSectionService serviceDieSection = (DieSectionService) SpringContextHelper.getBean("dieSection");
	private SmsEnquiryDtlService serviceEnqDetail = (SmsEnquiryDtlService) SpringContextHelper.getBean("SmsEnquiryDtl");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private DieCompletionHdrService serviceDieComplHdr = (DieCompletionHdrService) SpringContextHelper
			.getBean("dieComplHdr");
	private DieCompletionDtlService serviceDieComplDtl = (DieCompletionDtlService) SpringContextHelper
			.getBean("dieComplDtl");
	private MoldTrailReqHdrService serviceMoldTrialHdr = (MoldTrailReqHdrService) SpringContextHelper
			.getBean("moldTrialReqHdr");
	private MoldTrailReqDtlService serviceMoldTrialDtl = (MoldTrailReqDtlService) SpringContextHelper
			.getBean("moldTrialReqDtl");
	private DieBOMHdrService serviceDieBOMHdr = (DieBOMHdrService) SpringContextHelper.getBean("dieBOMHdr");
	private DieBOMDtlService serviceDieBOMDtl = (DieBOMDtlService) SpringContextHelper.getBean("dieBOMDtl");
	// Initialize the logger
	private Logger logger = Logger.getLogger(DieRequest.class);
	// User Input Fields for Die Request
	private PopupDateField dfRefDate, dfCompletionDate;
	private GERPComboBox cbEnquiry, cbProduct, cbNewDie;
	private GERPTextField tfNoofDie, tfCustomerCode, tfDrawingNumber, tfDieRefNumber;
	private GERPTextArea taChangeNote;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<DieRequestDM> beanDieRequest = null;
	private BeanItemContainer<MoldTrailReqDtlDM> beanMoldTrailReqDtl = null;
	private BeanItemContainer<DieCompletionDtlDM> beanDieCompletionDtl = null;
	private BeanItemContainer<DieBOMDtlDM> beanDieBOMDtl = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	private TabSheet tbDieRequest;
	private VerticalLayout vlDieSection;
	private VerticalLayout vlMoldTrialRequest;
	private VerticalLayout vlDieCompletion;
	private VerticalLayout vlBillofMaterial;
	// local variables declaration
	private Long dieRequestId, dieSectionId = null, moldRequestId = null, dieCompletionId = null, dieBOMId = null;
	private String username;
	private Long companyid, moduleId, branchId;
	private int recordCnt = 0;
	// for die section
	private GERPTextField tfDieModel, tfWorkNature;
	private GERPComboBox cbRegisterby, cbReceivedby;
	private GERPPopupDateField dfDieSecRefDate;
	private GERPTextArea taTrailComments, taCmtsRectified;
	// for Mold trail request
	private GERPTextField tfMTRefNumber;
	private GERPPopupDateField dfMTRefDate;
	private GERPComboBox cbMTInput, cbMTStatus;
	private GERPTextField tfMTDescription;
	private GERPTable tblTrailRequest = new GERPTable();
	private Button btnAddMoldTrial = new GERPButton("Add", "add");
	private List<MoldTrailReqDtlDM> listMoldTrailDetail = new ArrayList<MoldTrailReqDtlDM>();
	// Die completion report
	private GERPTextField tfDCRefNumber;
	private GERPPopupDateField dfDCRefDate;
	private GERPComboBox cbDCDescription, cbDCResult;
	private GERPTextField taDCRemarks;
	private GERPTable tblDieCompletion = new GERPTable();
	private Button btnAddDieCompl = new GERPButton("Add", "add");
	private List<DieCompletionDtlDM> listDieComplDetail = new ArrayList<DieCompletionDtlDM>();
	// Die Bill of material
	private GERPTextField tfBOMCustomerCode;
	private GERPTextField tfBOMIOM;
	private GERPTextField tfBOMPartNumber;
	private GERPComboBox cbBOMMaterial;
	private GERPTextField tfBOMLIDProfile;
	private GERPTextField tfBOMTopQty;
	private GERPTextField tfBOMBottomQty;
	private GERPTextArea taBOMNotes;
	private GERPTextField tfBOMDimensions;
	private GERPTextField tfBOMMatSize;
	private GERPNumberField tfBOMQty;
	private GERPTextField tfBOMRemarks;
	private GERPTable tblDieBillofMaterial = new GERPTable();
	private Button btnAddBOM = new GERPButton("Add", "add");
	private List<DieBOMDtlDM> listDieBOMDtl = new ArrayList<DieBOMDtlDM>();
	// for test documents
	private VerticalLayout hlDocumentLayout = new VerticalLayout();
	
	// Constructor received the parameters from Login UI class
	public DieRequest() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside DieRequest() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting DieRequest UI");
		// EC Request Components Definition
		tfNoofDie = new GERPTextField("No of Die");
		tfDieRefNumber = new GERPTextField("Ref. Number");
		tfCustomerCode = new GERPTextField("Customer Code");
		tfDrawingNumber = new GERPTextField("Drawing No.");
		tfDrawingNumber.setEnabled(false);
		cbNewDie = new GERPComboBox("New Die ?");
		cbNewDie.addItems("Yes", "No");
		cbNewDie.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				tfDieModel.setReadOnly(false);
				if (cbNewDie.getValue() != null && cbNewDie.getValue().equals("Yes")) {
					tfDieModel.setValue("NEW");
				} else {
					tfDieModel.setValue("OLD");
				}
				tfDieModel.setReadOnly(true);
			}
		});
		taChangeNote = new GERPTextArea("Change Note");
		taChangeNote.setWidth("984");
		cbEnquiry = new GERPComboBox("Enquiry No.");
		cbEnquiry.setItemCaptionPropertyId("enquiryNo");
		loadEnquiryList();
		cbEnquiry.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				loadProductList();
			}
		});
		dfRefDate = new GERPPopupDateField("Req. Date");
		dfCompletionDate = new GERPPopupDateField("Completion Date");
		cbStatus.setWidth("150");
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		cbProduct.setRequired(true);
		cbProduct.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbProduct.getValue() != null) {
					SmsEnquiryDtlDM smsEnquiryDtlDM = (SmsEnquiryDtlDM) cbProduct.getValue();
					tfCustomerCode.setValue(smsEnquiryDtlDM.getCustprodcode());
					tfDrawingNumber.setValue(smsEnquiryDtlDM.getCustomField2());
				}
			}
		});
		// for die section
		cbRegisterby = new GERPComboBox("Registered by");
		cbRegisterby.setItemCaptionPropertyId("firstname");
		cbReceivedby = new GERPComboBox("Received by");
		cbReceivedby.setItemCaptionPropertyId("firstname");
		loadEmployeeList();
		tfDieModel = new GERPTextField("Die Model");
		tfDieModel.setWidth("130");
		tfWorkNature = new GERPTextField("Work Nature");
		dfDieSecRefDate = new GERPPopupDateField("Date");
		dfDieSecRefDate.setWidth("110");
		taTrailComments = new GERPTextArea("Trail Performance & Comments(by Roto)");
		taTrailComments.setWidth("984");
		taTrailComments.setHeight("150px");
		taCmtsRectified = new GERPTextArea("Comments Rectified");
		taCmtsRectified.setWidth("984");
		taCmtsRectified.setHeight("150px");
		// for mold section
		tfMTRefNumber = new GERPTextField("Ref. Number");
		dfMTRefDate = new GERPPopupDateField("Date");
		cbMTInput = new GERPComboBox("Detail of Input");
		loadInputTypes();
		cbMTInput.setWidth("250");
		cbMTStatus = new GERPComboBox("Status", BASEConstants.T_SMS_ENQUIRY_HDR, BASEConstants.PE_STATUS);
		tfMTDescription = new GERPTextField("Decription");
		tfMTDescription.setWidth("350");
		tblTrailRequest.setPageLength(12);
		// for die completion
		tfDCRefNumber = new GERPTextField("Report Number");
		dfDCRefDate = new GERPPopupDateField("Date");
		cbDCDescription = new GERPComboBox("Description");
		loadDCDescriptions();
		cbDCResult = new GERPComboBox("Result");
		loadDCResults();
		taDCRemarks = new GERPTextField("Remarks");
		taDCRemarks.setWidth("300");
		tblDieCompletion.setPageLength(12);
		hlsearchlayout = new GERPAddEditHLayout();
		// for bill of material
		tfBOMCustomerCode = new GERPTextField("Customer Code");
		tfBOMIOM = new GERPTextField("IOM");
		tfBOMPartNumber = new GERPTextField("Saarc p/no");
		cbBOMMaterial = new GERPComboBox("Material");
		tfBOMLIDProfile = new GERPTextField("Lid profile with");
		tfBOMTopQty = new GERPTextField("Top Qty");
		tfBOMBottomQty = new GERPTextField("Bottom Qty");
		tfBOMDimensions = new GERPTextField("Dimensions");
		taBOMNotes = new GERPTextArea("Note :");
		taBOMNotes.setWidth("984");
		tfBOMMatSize = new GERPTextField("Raw material size");
		tfBOMQty = new GERPNumberField("Qty");
		tfBOMRemarks = new GERPTextField("Remarks");
		tfBOMRemarks.setWidth("200");
		tblDieBillofMaterial.setPageLength(5);
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
		loadTrailRequestDetails();
		loadDieCompletion();
		loadDieBillofMaterial();
		btnAddMoldTrial.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				saveMoldTrialDetails();
			}
		});
		btnAddDieCompl.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				saveDieCompletionDtl();
			}
		});
		tblTrailRequest.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblTrailRequest.isSelected(event.getItemId())) {
					resetMoldReqDetails();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					editMoldReqDetails();
				}
			}
		});
		tblDieCompletion.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblDieCompletion.isSelected(event.getItemId())) {
					resetDieCompleteDetails();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					editDieCompleteDetails();
				}
			}
		});
		tblDieBillofMaterial.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblDieBillofMaterial.isSelected(event.getItemId())) {
					resetDieBOMDetails();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					editDieBOMDetails();
				}
			}
		});
		btnAddBOM.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				saveBOMDetails();
			}
		});
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlsearchlayout.removeAllComponents();
		// Remove all components in search layout
		flcol1 = new FormLayout(cbEnquiry);
		flcol2 = new FormLayout(cbStatus);
		hlsearchlayout.addComponent(flcol1);
		hlsearchlayout.addComponent(flcol2);
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setSizeUndefined();
	}
	
	private void assembleinputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		flcol1 = new FormLayout(dfRefDate, tfDieRefNumber, cbEnquiry);
		flcol2 = new FormLayout(cbProduct, tfNoofDie, cbNewDie);
		flcol3 = new FormLayout(dfCompletionDate, tfCustomerCode, tfDrawingNumber);
		flcol4 = new FormLayout(cbStatus);
		hllayout.setMargin(true);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol4);
		hllayout.setMargin(true);
		hllayout.setSpacing(true);
		tbDieRequest = new TabSheet();
		VerticalLayout vlHeader = new VerticalLayout(hllayout, taChangeNote);
		vlHeader.setSpacing(true);
		vlHeader.setMargin(true);
		tbDieRequest.addTab(GERPPanelGenerator.createPanel(vlHeader), "Die Request");
		// for bill of material
		vlBillofMaterial = new VerticalLayout();
		vlBillofMaterial.setMargin(true);
		vlBillofMaterial.setSpacing(true);
		vlBillofMaterial.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(new FormLayout(tfBOMCustomerCode, tfBOMIOM));
				addComponent(new FormLayout(tfBOMPartNumber, cbBOMMaterial));
				addComponent(new FormLayout(tfBOMLIDProfile, tfBOMTopQty));
				addComponent(new FormLayout(tfBOMBottomQty));
			}
		});
		vlBillofMaterial.addComponent(taBOMNotes);
		vlBillofMaterial.addComponent(GERPPanelGenerator.createPanel(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setMargin(true);
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(new FormLayout(tfBOMDimensions));
						addComponent(new FormLayout(tfBOMMatSize));
						addComponent(new FormLayout(tfBOMQty));
						addComponent(new FormLayout(tfBOMRemarks));
						addComponent(btnAddBOM);
						setComponentAlignment(btnAddBOM, Alignment.MIDDLE_LEFT);
					}
				});
				addComponent(tblDieBillofMaterial);
			}
		}));
		tbDieRequest.addTab(vlBillofMaterial, "Bill of Matrial");
		// for die section
		vlDieSection = new VerticalLayout();
		vlDieSection.setSpacing(true);
		vlDieSection.setMargin(true);
		vlDieSection.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(new FormLayout(tfDieModel));
				addComponent(new FormLayout(tfWorkNature));
				addComponent(new FormLayout(cbRegisterby));
				addComponent(new FormLayout(cbReceivedby));
				addComponent(new FormLayout(dfDieSecRefDate));
			}
		});
		vlDieSection.addComponent(taTrailComments);
		vlDieSection.addComponent(taCmtsRectified);
		tbDieRequest.addTab(vlDieSection, "Die Section");
		// for mold trial request
		vlMoldTrialRequest = new VerticalLayout();
		vlMoldTrialRequest.addComponent(GERPPanelGenerator.createPanel(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				setMargin(true);
				addComponent(new FormLayout(tfMTRefNumber));
				addComponent(new FormLayout(dfMTRefDate));
				addComponent(new FormLayout(cbMTStatus));
			}
		}));
		vlMoldTrialRequest.addComponent(GERPPanelGenerator.createPanel(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				setMargin(true);
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(new FormLayout(cbMTInput));
						addComponent(new FormLayout(tfMTDescription));
						addComponent(btnAddMoldTrial);
						setComponentAlignment(btnAddMoldTrial, Alignment.MIDDLE_CENTER);
					}
				});
				addComponent(tblTrailRequest);
			}
		}));
		tbDieRequest.addTab(vlMoldTrialRequest, "Mold Trial Request");
		vlDieCompletion = new VerticalLayout();
		vlDieCompletion.addComponent(GERPPanelGenerator.createPanel(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				setMargin(true);
				addComponent(new FormLayout(tfDCRefNumber));
				addComponent(new FormLayout(dfDCRefDate));
			}
		}));
		vlDieCompletion.addComponent(GERPPanelGenerator.createPanel(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				setMargin(true);
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(new FormLayout(cbDCDescription));
						addComponent(new FormLayout(cbDCResult));
						addComponent(new FormLayout(taDCRemarks));
						addComponent(btnAddDieCompl);
						setComponentAlignment(btnAddDieCompl, Alignment.MIDDLE_CENTER);
					}
				});
				addComponent(tblDieCompletion);
			}
		}));
		tbDieRequest.addTab(vlDieCompletion, "Die Completion Report");
		hlUserIPContainer.addComponent(tbDieRequest);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		tbDieRequest.addTab(hlDocumentLayout, "Design Documents");
		// for disable tabs
		if (UI.getCurrent().getSession().getAttribute("IS_DIE_ENQ") == null
				|| (Boolean) UI.getCurrent().getSession().getAttribute("IS_DIE_ENQ")) {
			vlDieSection.setEnabled(false);
			vlMoldTrialRequest.setEnabled(false);
			vlDieCompletion.setEnabled(false);
			hlDocumentLayout.setEnabled(false);
		}
	}
	
	// Load EC Request
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<DieRequestDM> list = new ArrayList<DieRequestDM>();
			list = serviceDieRequest.getDieRequestList(null, null, null, null, null);
			recordCnt = list.size();
			beanDieRequest = new BeanItemContainer<DieRequestDM>(DieRequestDM.class);
			beanDieRequest.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the DieReq. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanDieRequest);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "dieReqId", "enquiryNo", "refDate", "productName",
					"noOfDie", "status", "lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Enquiry", "Date", "Product", "No of Die",
					"Status", "Last Updated date", "Last Updated by" });
			tblMstScrSrchRslt.setColumnAlignment("dieReqId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadTrailRequestDetails() {
		try {
			tblTrailRequest.removeAllItems();
			beanMoldTrailReqDtl = new BeanItemContainer<MoldTrailReqDtlDM>(MoldTrailReqDtlDM.class);
			beanMoldTrailReqDtl.addAll(listMoldTrailDetail);
			tblTrailRequest.setContainerDataSource(beanMoldTrailReqDtl);
			tblTrailRequest.setVisibleColumns(new Object[] { "inputDetail", "description" });
			tblTrailRequest.setColumnHeaders(new String[] { "Detail of Input", "Description" });
			tblTrailRequest.setColumnWidth("inputDetail", 350);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadDieCompletion() {
		try {
			tblDieCompletion.removeAllItems();
			beanDieCompletionDtl = new BeanItemContainer<DieCompletionDtlDM>(DieCompletionDtlDM.class);
			beanDieCompletionDtl.addAll(listDieComplDetail);
			tblDieCompletion.setContainerDataSource(beanDieCompletionDtl);
			tblDieCompletion.setVisibleColumns(new Object[] { "descType", "result", "remarks" });
			tblDieCompletion.setColumnHeaders(new String[] { "Description", "Results", "Remarks" });
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadDieBillofMaterial() {
		try {
			tblDieBillofMaterial.removeAllItems();
			beanDieBOMDtl = new BeanItemContainer<DieBOMDtlDM>(DieBOMDtlDM.class);
			beanDieBOMDtl.addAll(listDieBOMDtl);
			tblDieBillofMaterial.setContainerDataSource(beanDieBOMDtl);
			tblDieBillofMaterial.setVisibleColumns(new Object[] { "dimension", "size", "qty", "remarks" });
			tblDieBillofMaterial.setColumnHeaders(new String[] { "Dimensions", "Raw material size", "Qty", "Remarks" });
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Enquiry List
	private void loadEnquiryList() {
		try {
			BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
			beansmsenqHdr.setBeanIdProperty("enquiryId");
			beansmsenqHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
			cbEnquiry.setContainerDataSource(beansmsenqHdr);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadInputTypes() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"DIE_MTR_INPUT"));
			cbMTInput.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadDCDescriptions() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"DIE_DC_DESC"));
			cbDCDescription.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadDCResults() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompanyLookUp.setBeanIdProperty("lookupname");
			beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
					"DIE_DC_RESULT"));
			cbDCResult.setContainerDataSource(beanCompanyLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadProductList() {
		try {
			BeanItemContainer<SmsEnquiryDtlDM> beanPlnDtl = new BeanItemContainer<SmsEnquiryDtlDM>(
					SmsEnquiryDtlDM.class);
			beanPlnDtl.addAll(serviceEnqDetail.getsmsenquirydtllist(null, (Long) cbEnquiry.getValue(), null, null,
					null, null));
			cbProduct.setContainerDataSource(beanPlnDtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadEmployeeList() {
		try {
			List<EmployeeDM> empList = serviceEmployee.getEmployeeList(null, null, null, null, companyid, null, null,
					null, null, "P");
			BeanContainer<Long, EmployeeDM> beanInitiatedBy = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanInitiatedBy.setBeanIdProperty("employeeid");
			beanInitiatedBy.addAll(empList);
			cbReceivedby.setContainerDataSource(beanInitiatedBy);
			beanInitiatedBy = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanInitiatedBy.setBeanIdProperty("employeeid");
			beanInitiatedBy.addAll(empList);
			cbRegisterby.setContainerDataSource(beanInitiatedBy);
		}
		catch (Exception e) {
			logger.info("load Employee details" + e);
		}
	}
	
	// Method to edit the values from table into fields to update process for Die Request Header
	private void editDieRequest() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hllayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				DieRequestDM dieRequestDM = beanDieRequest.getItem(tblMstScrSrchRslt.getValue()).getBean();
				dieRequestId = dieRequestDM.getDieReqId();
				dfRefDate.setValue(dieRequestDM.getRefDate1());
				tfDieRefNumber.setReadOnly(false);
				tfDieRefNumber.setValue(dieRequestDM.getDieRefNumber());
				tfDieRefNumber.setReadOnly(true);
				cbNewDie.setValue(dieRequestDM.getNewDie());
				dfCompletionDate.setValue(dieRequestDM.getPlanCompleteDate());
				cbEnquiry.setValue(dieRequestDM.getEnquiryId());
				Long prodid = dieRequestDM.getProductId();
				Collection<?> prodids = cbProduct.getItemIds();
				for (Iterator<?> iterator = prodids.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
					// Get the actual bean and use the data
					SmsEnquiryDtlDM st = (SmsEnquiryDtlDM) item.getBean();
					if (prodid != null && prodid.equals(st.getProductid())) {
						cbProduct.setValue(itemId);
						break;
					} else {
						cbProduct.setValue(null);
					}
				}
				if (dieRequestDM.getNoOfDie() != null) {
					tfNoofDie.setValue(dieRequestDM.getNoOfDie().toString());
				}
				tfDrawingNumber.setValue(dieRequestDM.getDieRefNumber());
				taChangeNote.setValue(dieRequestDM.getChangeNote());
				cbStatus.setValue(dieRequestDM.getStatus());
				try {
					new TestingDocuments(hlDocumentLayout, dieRequestDM.getEnquiryId().toString(), "DR");
				}
				catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editMoldReqDetails() {
		try {
			// TODO Auto-generated method stub
			MoldTrailReqDtlDM moldTrailReqDtlDM = beanMoldTrailReqDtl.getItem(tblTrailRequest.getValue()).getBean();
			cbMTInput.setValue(moldTrailReqDtlDM.getInputDetail());
			tfMTDescription.setValue(moldTrailReqDtlDM.getDescription());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editDieCompleteDetails() {
		try {
			// TODO Auto-generated method stub
			DieCompletionDtlDM dieCompletionDtlDM = beanDieCompletionDtl.getItem(tblDieCompletion.getValue()).getBean();
			cbDCDescription.setValue(dieCompletionDtlDM.getDescType());
			cbDCResult.setValue(dieCompletionDtlDM.getResult());
			taDCRemarks.setValue(dieCompletionDtlDM.getRemarks());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editDieBOMDetails() {
		try {
			DieBOMDtlDM dieBOMDtlDM = beanDieBOMDtl.getItem(tblDieBillofMaterial.getValue()).getBean();
			tfBOMDimensions.setValue(dieBOMDtlDM.getDimension());
			tfBOMMatSize.setValue(dieBOMDtlDM.getSize());
			tfBOMQty.setValue(dieBOMDtlDM.getQty() + "");
			tfBOMRemarks.setValue(dieBOMDtlDM.getRemarks());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		saveDieRequest();
		try {
			if (validateDieSection()) {
				saveDieSection();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			if (validateMoldTrail()) {
				saveMoldTrailRequest();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			if (validateDieCompletion()) {
				saveDieCompletion();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			if (validateBillofMaterial()) {
				saveDieBillofMaterial();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void saveDieBillofMaterial() {
		// TODO Auto-generated method stub
		DieBOMHdrDM dieBOMHdrDM = new DieBOMHdrDM();
		dieBOMHdrDM.setDieBOMHdrId(dieBOMId);
		dieBOMHdrDM.setDieReqId(dieRequestId);
		dieBOMHdrDM.setClientCode(tfBOMCustomerCode.getValue());
		dieBOMHdrDM.setRefNumber(tfBOMIOM.getValue());
		dieBOMHdrDM.setPartNumber(tfBOMPartNumber.getValue());
		dieBOMHdrDM.setMaterialId((Long) cbBOMMaterial.getValue());
		dieBOMHdrDM.setLidProfile(tfBOMLIDProfile.getValue());
		dieBOMHdrDM.setQtyTop(Long.valueOf(tfBOMTopQty.getValue()));
		dieBOMHdrDM.setQtyBottom(Long.valueOf(tfBOMBottomQty.getValue()));
		dieBOMHdrDM.setRemarks(taBOMNotes.getValue());
		serviceDieBOMHdr.saveOrUpdateDetails(dieBOMHdrDM);
		dieBOMId = dieBOMHdrDM.getDieBOMHdrId();
		@SuppressWarnings("unchecked")
		Collection<DieBOMDtlDM> itemIds = (Collection<DieBOMDtlDM>) tblDieBillofMaterial.getVisibleItemIds();
		for (DieBOMDtlDM dieBOMDtlDM : (Collection<DieBOMDtlDM>) itemIds) {
			dieBOMDtlDM.setDieBOMHdrId(dieBOMId);
			serviceDieBOMDtl.saveOrUpdateDetails(dieBOMDtlDM);
		}
	}
	
	private boolean validateBillofMaterial() {
		tfBOMCustomerCode.setComponentError(null);
		tfBOMIOM.setComponentError(null);
		tfBOMPartNumber.setComponentError(null);
		cbBOMMaterial.setComponentError(null);
		Boolean isvalid = true;
		if (tfBOMCustomerCode.getValue() == null) {
			tfBOMCustomerCode.setComponentError(new UserError(""));
			isvalid = false;
		}
		if (tfBOMIOM.getValue() == null) {
			tfBOMIOM.setComponentError(new UserError(""));
			isvalid = false;
		}
		if (tfBOMPartNumber.getValue() == null) {
			tfBOMPartNumber.setComponentError(new UserError(""));
			isvalid = false;
		}
		if (cbBOMMaterial.getValue() == null) {
			cbBOMMaterial.setComponentError(new UserError(""));
			isvalid = false;
		}
		return isvalid;
	}
	
	private void saveDieCompletion() {
		// TODO Auto-generated method stub
		DieCompletionHdrDM dieCompletionHdrDM = new DieCompletionHdrDM();
		dieCompletionHdrDM.setDieReqId(dieRequestId);
		dieCompletionHdrDM.setDieComplHdrId(dieCompletionId);
		dieCompletionHdrDM.setRefNumber(tfDCRefNumber.getValue());
		dieCompletionHdrDM.setRefDate(dfDCRefDate.getValue());
		dieCompletionHdrDM.setDieModel(tfDieModel.getValue());
		serviceDieComplHdr.saveOrUpdateDetails(dieCompletionHdrDM);
		dieCompletionId = dieCompletionHdrDM.getDieComplHdrId();
		@SuppressWarnings("unchecked")
		Collection<DieCompletionDtlDM> itemIds = (Collection<DieCompletionDtlDM>) tblDieCompletion.getVisibleItemIds();
		for (DieCompletionDtlDM dieCompletionDtlDM : (Collection<DieCompletionDtlDM>) itemIds) {
			dieCompletionDtlDM.setDieComplHdrId(dieCompletionHdrDM.getDieComplHdrId());
			serviceDieComplDtl.saveOrUpdateDetails(dieCompletionDtlDM);
		}
	}
	
	private boolean validateDieCompletion() throws ValidationException {
		tfDCRefNumber.setComponentError(null);
		dfDCRefDate.setComponentError(null);
		Boolean isvalid = true;
		if (tfDCRefNumber.getValue() == null) {
			tfDCRefNumber.setComponentError(new UserError(""));
			isvalid = false;
		}
		if (dfDCRefDate.getValue() == null) {
			dfDCRefDate.setComponentError(new UserError(""));
			isvalid = false;
		}
		if (!isvalid) {
			throw new ERPException.ValidationException();
		}
		return isvalid;
	}
	
	private void saveMoldTrialDetails() {
		MoldTrailReqDtlDM moldTrailReqDtlDM = new MoldTrailReqDtlDM();
		if (tblTrailRequest.getValue() != null) {
			moldTrailReqDtlDM = beanMoldTrailReqDtl.getItem(tblTrailRequest.getValue()).getBean();
			listMoldTrailDetail.remove(moldTrailReqDtlDM);
		}
		moldTrailReqDtlDM.setInputDetail((String) cbMTInput.getValue());
		moldTrailReqDtlDM.setDescription((String) tfMTDescription.getValue());
		moldTrailReqDtlDM.setLastUpdatedBy(username);
		moldTrailReqDtlDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		listMoldTrailDetail.add(moldTrailReqDtlDM);
		loadTrailRequestDetails();
		resetMoldReqDetails();
	}
	
	private Boolean validateMoldTrail() throws ValidationException {
		Boolean isvalid = true;
		if (tfMTRefNumber.getValue() == null) {
			isvalid = false;
		}
		if (!isvalid) {
			throw new ERPException.ValidationException();
		}
		return isvalid;
	}
	
	private void saveMoldTrailRequest() {
		MoldTrailReqHdrDM moldTrailReqHdrDM = new MoldTrailReqHdrDM();
		moldTrailReqHdrDM.setDieReqId(dieRequestId);
		moldTrailReqHdrDM.setTrialReqHdrId(moldRequestId);
		moldTrailReqHdrDM.setRefNumber(tfMTRefNumber.getValue());
		moldTrailReqHdrDM.setRefDate(dfMTRefDate.getValue());
		moldTrailReqHdrDM.setStatus((String) cbMTStatus.getValue());
		moldTrailReqHdrDM.setLastUpdatedBy(username);
		moldTrailReqHdrDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceMoldTrialHdr.saveOrUpdateDetails(moldTrailReqHdrDM);
		moldRequestId = moldTrailReqHdrDM.getTrialReqHdrId();
		@SuppressWarnings("unchecked")
		Collection<MoldTrailReqDtlDM> itemIds = (Collection<MoldTrailReqDtlDM>) tblTrailRequest.getVisibleItemIds();
		for (MoldTrailReqDtlDM moldTrailReqDtlDM : (Collection<MoldTrailReqDtlDM>) itemIds) {
			moldTrailReqDtlDM.setTrialReqHdrId(moldTrailReqHdrDM.getTrialReqHdrId());
			serviceMoldTrialDtl.saveOrUpdateDetails(moldTrailReqDtlDM);
		}
	}
	
	private void saveDieCompletionDtl() {
		DieCompletionDtlDM dieCompletionDtlDM = new DieCompletionDtlDM();
		if (tblDieCompletion.getValue() != null) {
			dieCompletionDtlDM = beanDieCompletionDtl.getItem(tblDieCompletion.getValue()).getBean();
			listDieComplDetail.remove(dieCompletionDtlDM);
		}
		dieCompletionDtlDM.setDescType((String) cbDCDescription.getValue());
		dieCompletionDtlDM.setResult((String) cbDCResult.getValue());
		dieCompletionDtlDM.setRemarks(taDCRemarks.getValue());
		dieCompletionDtlDM.setLastUpdatedBy(username);
		dieCompletionDtlDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		listDieComplDetail.add(dieCompletionDtlDM);
		loadDieCompletion();
		resetDieCompleteDetails();
	}
	
	private void saveBOMDetails() {
		// TODO Auto-generated method stub
		DieBOMDtlDM dieBOMDtlDM = new DieBOMDtlDM();
		if (tblDieBillofMaterial.getValue() != null) {
			dieBOMDtlDM = beanDieBOMDtl.getItem(tblDieBillofMaterial.getValue()).getBean();
			listDieBOMDtl.remove(dieBOMDtlDM);
		}
		dieBOMDtlDM.setDimension(tfBOMDimensions.getValue());
		dieBOMDtlDM.setSize(tfBOMMatSize.getValue());
		dieBOMDtlDM.setQty(Long.valueOf(tfBOMQty.getValue()));
		dieBOMDtlDM.setRemarks(tfBOMRemarks.getValue());
		dieBOMDtlDM.setLastUpdatedBy(username);
		dieBOMDtlDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		listDieBOMDtl.add(dieBOMDtlDM);
		loadDieBillofMaterial();
		resetDieBOMDetails();
	}
	
	private void saveDieRequest() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		DieRequestDM dieRequestDM = new DieRequestDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			dieRequestDM = beanDieRequest.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		dieRequestDM.setRefDate(dfRefDate.getValue());
		dieRequestDM.setDieRefNumber(tfDieRefNumber.getValue());
		dieRequestDM.setNewDie((String) cbNewDie.getValue());
		dieRequestDM.setPlanCompleteDate(dfCompletionDate.getValue());
		dieRequestDM.setNoOfDie(Long.valueOf(tfNoofDie.getValue()));
		dieRequestDM.setEnquiryId((Long) cbEnquiry.getValue());
		dieRequestDM.setProductId(((SmsEnquiryDtlDM) cbProduct.getValue()).getProductid());
		dieRequestDM.setChangeNote(taChangeNote.getValue());
		dieRequestDM.setStatus((String) cbStatus.getValue());
		dieRequestDM.setLastUpdatedBy(username);
		dieRequestDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceDieRequest.saveOrUpdateDetails(dieRequestDM);
		dieRequestId = dieRequestDM.getDieReqId();
		if (tblMstScrSrchRslt.getValue() == null) {
			try {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "DIE_REQ_NO")
						.get(0);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "DIE_REQ_NO");
				}
			}
			catch (Exception e) {
				logger.info(e.getMessage());
			}
		}
	}
	
	private void saveDieSection() {
		DieSectionDM dieSectionDM = new DieSectionDM();
		dieSectionDM.setDieSecId(dieSectionId);
		dieSectionDM.setDieReqId(dieRequestId);
		dieSectionDM.setDieModel(tfDieModel.getValue());
		dieSectionDM.setWorkNature(tfWorkNature.getValue());
		dieSectionDM.setRegisterBy((Long) cbRegisterby.getValue());
		dieSectionDM.setReceiveBy((Long) cbReceivedby.getValue());
		dieSectionDM.setRefDate(dfDieSecRefDate.getValue());
		dieSectionDM.setTrailComments(taTrailComments.getValue());
		dieSectionDM.setCommentsRectified(taCmtsRectified.getValue());
		dieSectionDM.setStatus("Active");
		dieSectionDM.setLastUpdatedBy(username);
		dieSectionDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceDieSection.saveOrUpdateDetails(dieSectionDM);
		dieSectionId = dieSectionDM.getDieSecId();
	}
	
	private Boolean validateDieSection() throws ValidationException {
		Boolean isvalid = true;
		if (dieRequestId == null) {
			isvalid = false;
		}
		if (!isvalid) {
			throw new ERPException.ValidationException();
		}
		return isvalid;
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		tfNoofDie.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		dieRequestId = null;
		dieSectionId = null;
		moldRequestId = null;
		dieCompletionId = null;
		tfDieRefNumber.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "DIE_REQ_NO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfDieRefNumber.setValue(slnoObj.getKeyDesc());
				tfDieRefNumber.setReadOnly(true);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		resetFields();
		dieRequestId = null;
		dieSectionId = null;
		moldRequestId = null;
		dieCompletionId = null;
		editDieRequest();
		try {
			editDieSection();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			editMoldTrialRequest();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			editDieCompletion();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			editBillofMaterial();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editBillofMaterial() {
		// TODO Auto-generated method stub
		DieBOMHdrDM dieBOMHdrDM = serviceDieBOMHdr.getDieBOMHdrList(null, dieRequestId, null, null, null, null, null,
				null).get(0);
		dieBOMId = dieBOMHdrDM.getDieBOMHdrId();
		tfBOMCustomerCode.setValue(dieBOMHdrDM.getClientCode());
		tfBOMIOM.setValue(dieBOMHdrDM.getRefNumber());
		tfBOMPartNumber.setValue(dieBOMHdrDM.getPartNumber());
		cbBOMMaterial.setValue(dieBOMHdrDM.getMaterialId());
		tfBOMLIDProfile.setValue(dieBOMHdrDM.getLidProfile());
		tfBOMTopQty.setValue(dieBOMHdrDM.getQtyTop() + "");
		tfBOMBottomQty.setValue(dieBOMHdrDM.getQtyBottom() + "");
		listDieBOMDtl = serviceDieBOMDtl.getDieBOMDtlList(null, dieBOMId, null);
		loadDieBillofMaterial();
	}
	
	private void editDieCompletion() {
		// TODO Auto-generated method stub
		DieCompletionHdrDM dieCompletionHdrDM = serviceDieComplHdr.getDieCompletionHdrList(null, dieRequestId, null,
				null, null).get(0);
		dieCompletionId = dieCompletionHdrDM.getDieComplHdrId();
		tfDCRefNumber.setValue(dieCompletionHdrDM.getRefNumber());
		dfDCRefDate.setValue(dieCompletionHdrDM.getRefDate());
		tfDieModel.setValue(dieCompletionHdrDM.getDieModel());
		listDieComplDetail = serviceDieComplDtl.getDieCompletionDtlList(null, dieCompletionId, null);
		loadDieCompletion();
	}
	
	private void editMoldTrialRequest() {
		// TODO Auto-generated method stub
		MoldTrailReqHdrDM moldTrailReqHdrDM = serviceMoldTrialHdr.getMoldTrailReqHdrList(null, dieRequestId, null,
				null, null).get(0);
		moldRequestId = moldTrailReqHdrDM.getTrialReqHdrId();
		tfMTRefNumber.setValue(moldTrailReqHdrDM.getRefNumber());
		dfRefDate.setValue(moldTrailReqHdrDM.getRefDate());
		listMoldTrailDetail = serviceMoldTrialDtl.getMoldTrailReqDtlList(null, moldRequestId, null);
		loadTrailRequestDetails();
	}
	
	private void editDieSection() {
		// TODO Auto-generated method stub
		DieSectionDM dieSectionDM = serviceDieSection.getDieSectionList(null, dieRequestId, null, null, null).get(0);
		dieSectionId = dieSectionDM.getDieSecId();
		dfDieSecRefDate.setValue(dieSectionDM.getRefDate());
		tfDieModel.setValue(dieSectionDM.getDieModel());
		tfWorkNature.setValue(dieSectionDM.getWorkNature());
		cbRegisterby.setValue(dieSectionDM.getRegisterBy());
		cbReceivedby.setValue(dieSectionDM.getReceiveBy());
		taTrailComments.setValue(dieSectionDM.getTrailComments());
		taCmtsRectified.setValue(dieSectionDM.getCommentsRectified());
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbEnquiry.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbEnquiry.getValue() == null) {
			cbEnquiry.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorFlag = true;
		}
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbEnquiry.getValue() + "," + "," + ","
				+ dfRefDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for enquiryId " + dieRequestId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(dieRequestId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hllayout1.removeAllComponents();
		tblMstScrSrchRslt.setValue(null);
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	private void resetMoldReqDetails() {
		cbMTInput.setValue(null);
		tfMTDescription.setValue("");
		cbMTInput.setComponentError(null);
		tfMTDescription.setComponentError(null);
	}
	
	private void resetDieCompleteDetails() {
		// TODO Auto-generated method stub
		cbDCDescription.setValue(null);
		cbDCResult.setValue(null);
		taDCRemarks.setValue("");
		cbDCDescription.setComponentError(null);
		cbDCResult.setComponentError(null);
	}
	
	private void resetDieBOMDetails() {
		tfBOMDimensions.setValue("");
		tfBOMMatSize.setValue("");
		tfBOMQty.setValue("0");
		tfBOMRemarks.setValue("");
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfNoofDie.setReadOnly(false);
		tfNoofDie.setValue("");
		tfNoofDie.setComponentError(null);
		cbEnquiry.setComponentError(null);
		cbEnquiry.setValue(null);
		cbProduct.setValue(null);
		dfCompletionDate.setValue(null);
		tfCustomerCode.setValue("");
		tfDrawingNumber.setValue("");
		taChangeNote.setValue("");
		cbStatus.setValue(null);
		dfRefDate.setValue(new Date());
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
			tfNoofDie.setReadOnly(false);
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
			System.out.println("tbDieRequest.getSelectedTab().getCaption()--->" + tbDieRequest.getSelectedTab());
			if (tbDieRequest.getSelectedTab().equals(vlDieSection)) {
				HashMap<String, Long> parameterMap = new HashMap<String, Long>();
				parameterMap.put("diesecid", dieSectionId);
				Report rpt = new Report(parameterMap, connection);
				rpt.setReportName(basepath + "/WEB-INF/reports/diesection"); // diesection is the name of my jasper
				rpt.callReport(basepath, "Preview");
			} else if (tbDieRequest.getSelectedTab().equals(vlMoldTrialRequest)) {
				HashMap<String, Long> parameterMap = new HashMap<String, Long>();
				parameterMap.put("moldreqid", moldRequestId);
				Report rpt = new Report(parameterMap, connection);
				rpt.setReportName(basepath + "/WEB-INF/reports/moldtrialrequest"); // diesection is the name of my
																					// jasper
				rpt.callReport(basepath, "Preview");
			} else if (tbDieRequest.getSelectedTab().equals(vlDieCompletion)) {
				HashMap<String, Long> parameterMap = new HashMap<String, Long>();
				parameterMap.put("diecompid", dieCompletionId);
				Report rpt = new Report(parameterMap, connection);
				rpt.setReportName(basepath + "/WEB-INF/reports/diecompletionreport"); // diecompletionreport is the name
																						// of my
																						// jasper
				rpt.callReport(basepath, "Preview");
			} else if (tbDieRequest.getSelectedTab().equals(vlBillofMaterial)) {
				HashMap<String, Long> parameterMap = new HashMap<String, Long>();
				parameterMap.put("bomid", dieBOMId);
				Report rpt = new Report(parameterMap, connection);
				rpt.setReportName(basepath + "/WEB-INF/reports/moldlidbom"); // moldlidbom is the name of my
																				// jasper
				rpt.callReport(basepath, "Preview");
			} else {
				HashMap<String, Long> parameterMap = new HashMap<String, Long>();
				parameterMap.put("diereqid", dieRequestId);
				Report rpt = new Report(parameterMap, connection);
				rpt.setReportName(basepath + "/WEB-INF/reports/dierequest"); // dierequest is the name of my jasper
				rpt.callReport(basepath, "Preview");
			}
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
}