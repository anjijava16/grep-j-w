/**
 * File Name	:	ProductOverview.java
 * Description	:	This Screen Purpose for view the Product Details.
 * Author		:	SOUNDARC
 * Date			:	JUL 25, 2015
 * Modification :   
 * Modified By  :   
 * Description 	:
 *
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version      Date           	Modified By      		Remarks
 * 0.1          JUL 25, 2015   	SOUNDARC				Initial Version		
 * 
 */
package com.gnts.mfg.txn;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.ProductService;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.mfg.domain.txn.QATestHdrDM;
import com.gnts.mfg.domain.txn.QcTestHdrDM;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.service.txn.QATestHdrService;
import com.gnts.mfg.service.txn.QcTestHdrService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsEnquiryDtlDM;
import com.gnts.sms.domain.txn.SmsEnquirySpecDM;
import com.gnts.sms.domain.txn.SmsInvoiceHdrDM;
import com.gnts.sms.domain.txn.SmsPOHdrDM;
import com.gnts.sms.domain.txn.SmsQuoteHdrDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsEnquiryDtlService;
import com.gnts.sms.service.txn.SmsEnquirySpecService;
import com.gnts.sms.service.txn.SmsInvoiceHdrService;
import com.gnts.sms.service.txn.SmsPOHdrService;
import com.gnts.sms.service.txn.SmsQuoteHdrService;
import com.gnts.stt.mfg.domain.txn.EnquiryWorkflowDM;
import com.gnts.stt.mfg.domain.txn.RotoCheckDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanDtlDM;
import com.gnts.stt.mfg.service.txn.EnquiryWorkflowService;
import com.gnts.stt.mfg.service.txn.RotoCheckDtlService;
import com.gnts.stt.mfg.service.txn.RotoPlanDtlService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class ProductOverview implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VerticalLayout hlPageRootContainter = (VerticalLayout) UI.getCurrent().getSession()
			.getAttribute("clLayout");
	private RotoCheckDtlService serviceRotoCheckDtl = (RotoCheckDtlService) SpringContextHelper.getBean("rotocheckdtl");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private SmsEnqHdrService serviceEnquiryHdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private SmsEnquiryDtlService serviceEnqDetail = (SmsEnquiryDtlService) SpringContextHelper.getBean("SmsEnquiryDtl");
	private SmsQuoteHdrService serviceQuoteHdr = (SmsQuoteHdrService) SpringContextHelper.getBean("smsquotehdr");
	private SmsPOHdrService servicesPOHdr = (SmsPOHdrService) SpringContextHelper.getBean("smspohdr");
	private SmsEnquirySpecService serviceEnqSpec = (SmsEnquirySpecService) SpringContextHelper
			.getBean("SmsEnquirySpec");
	private SmsInvoiceHdrService serviceInvoiceHdr = (SmsInvoiceHdrService) SpringContextHelper
			.getBean("smsInvoiceheader");
	private RotoPlanDtlService serviceRotoplandtl = (RotoPlanDtlService) SpringContextHelper.getBean("rotoplandtl");
	private WorkOrderHdrService serviceWrkOrdHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private EnquiryWorkflowService serviceWorkflow = (EnquiryWorkflowService) SpringContextHelper
			.getBean("enquiryWorkflow");
	private QcTestHdrService serviceQcTstHdr = (QcTestHdrService) SpringContextHelper.getBean("qcTestHdr");
	//private QCTestDtlService serviceQcTstDtl = (QCTestDtlService) SpringContextHelper.getBean("qcTestDtl");
	//private QATestDtlService serviceQATstDtl = (QATestDtlService) SpringContextHelper.getBean("qatestDetls");
	private QATestHdrService serviceQATstHdr = (QATestHdrService) SpringContextHelper.getBean("qatesthdr");
	// Header container which holds, screen name, notification and page master
	// buttons
	private HorizontalLayout hlPageHdrContainter = (HorizontalLayout) UI.getCurrent().getSession()
			.getAttribute("hlLayout");
	private String screenName = "";
	private Button btnScreenName;
	private GERPTextField tfSerialNumber = new GERPTextField("Serial Number");
	private Button btnSearch = new GERPButton("Search", "searchbt", this);
	// for basic information
	private Image imgProduct;
	private TextField tfProductCode;
	private TextField tfProductName;
	private TextArea taProductDesc;
	private TextArea taProductShortDesc;
	private TextField tfEnquiryRef;
	private TextField tfQuoteRef;
	private TextField tfPORef;
	private TextField tfInvoiceRef;
	private PopupDateField dfEnquiryDate;
	private PopupDateField dfQuoteDate;
	private PopupDateField dfPODate;
	private PopupDateField dfInvoiceDate;
	private TextField tfWorkorderRef;
	private PopupDateField dfWorkorderDate;
	private Table tblEnquirySpec = new Table();
	private Table tblEnquiryWorkflow = new Table();
	private Table tblDieRequest = new Table();
	private Table tblProductionDetails = new Table();
	private Table tblQATestHdr = new Table();
	private Table tblQCTestDetails = new Table();
	private Table tblMaterialDetails = new Table();
	// for enquiry details
	private TextField tfEnquiryNumber = new TextField("Enquiry No");
	private TextField tfBranchName = new TextField("Branch Name");
	private TextField tfClientCode = new TextField("Client Code");
	private TextField tfClientName = new TextField("Cleint Name");
	private TextField tfModeofEnquiry = new TextField("Mode of Enquiry");
	private PopupDateField dfEnquiryDate1 = new PopupDateField("Enquiry Date");
	private TextField tfDrawingNo = new TextField("Drawing No");
	private TextField tfPartNumber = new TextField("Part Number");
	private TextArea taEnqRemarks = new TextArea("Remarks");
	// for die request
	private PopupDateField dfDieReqDate = new PopupDateField("Req Date");
	private TextField tfDieReqNumber = new TextField("Ref. Number");
	private TextField tfNoofDie = new TextField("No of Die");
	private TextField tfIsNewDie = new TextField("Is New Die?");
	private PopupDateField dfDieComplDate = new PopupDateField("Completion Date");
	private TextField tfDieStatus = new TextField("Status");
	private TextArea taDieChangeNote = new TextArea("Change Note");
	// for die section
	private TextField tfDieModel = new TextField("Die Model");
	private TextField tfWorkNature = new TextField("Work Nature");
	private TextField tfDieRegBy = new TextField("Registered by");
	private TextField tfDieRecvBy = new TextField("Received by");
	private PopupDateField dfDieSecDate = new PopupDateField("Ref Date");
	private TextArea taTrailPerfomance = new TextArea("Trail Performance & Comments(by Roto)");
	private TextArea taCmntsRectified = new TextArea("Comments Rectified");
	// for production
	private TextField tfExtMachine = new TextField("Machine Name");
	private TextField tfExtRefNumber = new TextField("Ref. Number");
	private PopupDateField dfExtDate = new PopupDateField("Extuder Date");
	private TextField tfPulzRefNo = new TextField("Pulvrizer Ref.");
	private PopupDateField dfPulzRefDate = new PopupDateField("Pulvrizer Date");
	private TextField tfRotoRefNo = new TextField("Roto Ref No.");
	private PopupDateField dfRotoRefDate = new PopupDateField("Ref Date");
	private TextField tfArmNumber = new TextField("Arm Number");
	private TextField tfAssemblyRef = new TextField("Assembly Ref.");
	private PopupDateField dfAssemblyDate = new PopupDateField("Assembly DatTextFielde");
	private TextField tfAssembledBy = new TextField("Assembled by");
	private TextField tfFoamRefNumber = new TextField("Foam Ref No");
	private TextField tfFoamRefDate = new TextField("Ref Date");
	private TextField tfFoamBy = new TextField("Foam by");
	// for signoff details
	private TextField tfBatchNumber = new TextField("Batch Number");
	private TextField tfInspectionNo = new TextField("Inspection No");
	private PopupDateField dfSignoffDate = new PopupDateField("Signoff Date");
	private TextField tfSignOffBy = new TextField("Signoff by");
	// for Marketing Details
	private TextField tfQuoteNumber = new TextField();
	
	public ProductOverview() {
		// TODO Auto-generated constructor stub
		if (UI.getCurrent().getSession().getAttribute("screenName") != null) {
			screenName = (String) UI.getCurrent().getSession().getAttribute("screenName");
		}
		btnScreenName = new GERPButton(screenName, "link", this);
		hlPageHdrContainter.removeAllComponents();
		hlPageHdrContainter.addComponent(btnScreenName);
		hlPageHdrContainter.setComponentAlignment(btnScreenName, Alignment.MIDDLE_LEFT);
		buildView();
	}
	
	private void buildView() {
		// TODO Auto-generated method stub
		tfSerialNumber.setWidth("200px");
		btnSearch.setClickShortcut(KeyCode.ENTER);
		tblEnquirySpec.setPageLength(5);
		tblEnquiryWorkflow.setPageLength(5);
		tblDieRequest.setPageLength(5);
		tblProductionDetails.setPageLength(5);
		tblQATestHdr.setPageLength(5);
		tblQCTestDetails.setPageLength(5);
		tblMaterialDetails.setPageLength(5);
		tblEnquiryWorkflow.setFooterVisible(true);
		tblDieRequest.setFooterVisible(true);
		tblProductionDetails.setFooterVisible(true);
		tblQATestHdr.setFooterVisible(true);
		tblQCTestDetails.setFooterVisible(true);
		tblMaterialDetails.setFooterVisible(true);
		tblEnquirySpec.setWidth("1100px");
		tblMaterialDetails.setWidth("500px");
		tblEnquiryWorkflow.setWidth("1100px");
		tblDieRequest.setWidth("600px");
		tblProductionDetails.setWidth("500px");
		tblQATestHdr.setWidth("100%");
		tblQCTestDetails.setWidth("100%");
		hlPageRootContainter.setSpacing(true);
		hlPageRootContainter.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(new FormLayout(tfSerialNumber));
				addComponent(btnSearch);
				setComponentAlignment(btnSearch, Alignment.MIDDLE_LEFT);
			}
		});
		// Create the Accordion.
		Accordion accordion = new Accordion();
		// Have it take all space available in the layout.
		accordion.setSizeFull();
		// Some components to put in the Accordion.
		HorizontalLayout l3 = new HorizontalLayout();
		l3.addComponent(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(buildEnquiryDetails());
				addComponent(tblEnquirySpec);
			}
		});
		HorizontalLayout l2 = new HorizontalLayout();
		l2.addComponent(tblEnquiryWorkflow);
		// for die details
		VerticalLayout hlDieSection = new VerticalLayout();
		hlDieSection.setMargin(true);
		hlDieSection.addComponent(buildDieRequest());
		hlDieSection.addComponent(buildDieSection());
		// for production
		VerticalLayout vlProdLayout = new VerticalLayout();
		vlProdLayout.addComponent(buildProductionDetails());
		// for test layout
		VerticalLayout vlTestLayout = new VerticalLayout();
		vlTestLayout.setSpacing(true);
		vlTestLayout.addComponent(tblQATestHdr);
		vlTestLayout.addComponent(tblQCTestDetails);
		// for signoff details
		VerticalLayout vlSignoff = new VerticalLayout();
		vlSignoff.addComponent(buildSignoffDetails());
		// Add the components as tabs in the Accordion.
		accordion.addTab(buildBasicInformation(), "Basic Information", null);
		accordion.addTab(l3, "Marketing Information", null);
		accordion.addTab(l2, "Design Information", null);
		accordion.addTab(hlDieSection, "Die Information", null);
		accordion.addTab(vlProdLayout, "Production Information", null);
		accordion.addTab(new TextField(), "Testing Information", null);
		accordion.addTab(vlTestLayout, "Signoff Information", null);
		hlPageRootContainter.addComponent(accordion);
		loadEnquirySpec(null, null);
		getEnqWorkflowDetails(null);
		getQATestHeaderDetails(null, null);
		tfSerialNumber.focus();
	}
	
	private Component buildBasicInformation() {
		HorizontalLayout root = new HorizontalLayout();
		root.setWidth(100.0f, Unit.PERCENTAGE);
		root.setSpacing(true);
		root.setMargin(true);
		VerticalLayout pic = new VerticalLayout();
		pic.setSpacing(true);
		imgProduct = new Image(null, new ThemeResource("img/box.png"));
		imgProduct.setWidth(155.0f, Unit.PIXELS);
		imgProduct.setHeight(160.0f, Unit.PIXELS);
		pic.addComponent(imgProduct);
		root.addComponent(pic);
		FormLayout details1 = new FormLayout();
		details1.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		root.addComponent(details1);
		tfProductCode = new TextField("Product Code");
		tfProductCode.setWidth("150px");
		details1.addComponent(tfProductCode);
		tfProductName = new TextField("Product Name");
		tfProductName.setWidth("150px");
		details1.addComponent(tfProductName);
		taProductDesc = new TextArea("Description");
		taProductDesc.setWidth("150px");
		taProductDesc.setHeight("110px");
		details1.addComponent(taProductDesc);
		FormLayout details2 = new FormLayout();
		root.addComponent(details2);
		taProductShortDesc = new TextArea("Short Desc.");
		taProductShortDesc.setWidth("150px");
		taProductShortDesc.setNullRepresentation("");
		details2.addComponent(taProductShortDesc);
		tfEnquiryRef = new TextField("Enquiry Ref.");
		tfEnquiryRef.setWidth("150px");
		tfEnquiryRef.setNullRepresentation("");
		details2.addComponent(tfEnquiryRef);
		dfEnquiryDate = new PopupDateField("Enquiry Date");
		dfEnquiryDate.setWidth("150px");
		details2.addComponent(dfEnquiryDate);
		tfQuoteRef = new TextField("Quote Ref.");
		tfQuoteRef.setWidth("150px");
		details2.addComponent(tfQuoteRef);
		FormLayout details3 = new FormLayout();
		dfQuoteDate = new PopupDateField("Quote Date");
		dfQuoteDate.setWidth("150px");
		details3.addComponent(dfQuoteDate);
		tfPORef = new TextField("Order Ref.");
		tfPORef.setWidth("150px");
		details3.addComponent(tfPORef);
		dfPODate = new PopupDateField("Order Date");
		dfPODate.setWidth("150px");
		details3.addComponent(dfPODate);
		tfInvoiceRef = new TextField("Invoice Ref.");
		tfInvoiceRef.setWidth("150px");
		details3.addComponent(tfInvoiceRef);
		dfInvoiceDate = new PopupDateField("Invoice Date");
		dfInvoiceDate.setWidth("150px");
		details3.addComponent(dfInvoiceDate);
		tfWorkorderRef = new TextField("Workorder Ref.");
		tfWorkorderRef.setWidth("150px");
		details3.addComponent(tfWorkorderRef);
		dfWorkorderDate = new PopupDateField("Workorder Date");
		dfWorkorderDate.setWidth("150px");
		details3.addComponent(dfWorkorderDate);
		root.addComponent(details3);
		return root;
	}
	
	private Component buildEnquiryDetails() {
		HorizontalLayout root = new HorizontalLayout();
		root.setSpacing(true);
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfEnquiryNumber);
				addComponent(tfBranchName);
			}
		});
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfClientCode);
				addComponent(tfClientName);
			}
		});
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfModeofEnquiry);
				addComponent(dfEnquiryDate1);
			}
		});
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfDrawingNo);
				addComponent(tfPartNumber);
			}
		});
		VerticalLayout vlEnquiry = new VerticalLayout();
		vlEnquiry.addComponent(root);
		vlEnquiry.addComponent(taEnqRemarks);
		taEnqRemarks.setWidth("1054px");
		return vlEnquiry;
	}
	
	private Component buildDieRequest() {
		HorizontalLayout root = new HorizontalLayout();
		root.setSpacing(true);
		root.setMargin(true);
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(dfDieReqDate);
				addComponent(tfDieReqNumber);
			}
		});
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfNoofDie);
				addComponent(tfIsNewDie);
			}
		});
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(dfDieComplDate);
				addComponent(tfDieStatus);
			}
		});
		VerticalLayout vlDie = new VerticalLayout();
		vlDie.addComponent(root);
		vlDie.addComponent(taDieChangeNote);
		taDieChangeNote.setWidth("1054px");
		return vlDie;
	}
	
	private Component buildDieSection() {
		HorizontalLayout root = new HorizontalLayout();
		root.setSpacing(true);
		root.setMargin(true);
		root.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfDieModel);
				addComponent(tfWorkNature);
				addComponent(tfDieRegBy);
				addComponent(tfDieRecvBy);
				addComponent(dfDieSecDate);
			}
		});
		VerticalLayout vlDieSection = new VerticalLayout();
		vlDieSection.addComponent(root);
		vlDieSection.addComponent(taTrailPerfomance);
		taTrailPerfomance.setWidth("1054px");
		vlDieSection.addComponent(taCmntsRectified);
		taCmntsRectified.setWidth("1054px");
		return vlDieSection;
	}
	
	private Component buildProductionDetails() {
		HorizontalLayout root = new HorizontalLayout();
		root.setSpacing(true);
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfExtMachine);
				addComponent(tfExtRefNumber);
				addComponent(dfExtDate);
			}
		});
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfPulzRefNo);
				addComponent(dfPulzRefDate);
			}
		});
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfRotoRefNo);
				addComponent(dfRotoRefDate);
				addComponent(tfArmNumber);
			}
		});
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfAssemblyRef);
				addComponent(dfAssemblyDate);
				addComponent(tfAssembledBy);
			}
		});
		root.addComponent(new FormLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(tfFoamRefNumber);
				addComponent(tfFoamRefDate);
				addComponent(tfFoamBy);
			}
		});
		return root;
	}
	
	private Component buildSignoffDetails() {
		HorizontalLayout root = new HorizontalLayout();
		root.setSpacing(true);
		root.addComponent(new FormLayout(tfBatchNumber));
		root.addComponent(new FormLayout(tfInspectionNo));
		root.addComponent(new FormLayout(dfSignoffDate));
		root.addComponent(new FormLayout(tfSignOffBy));
		return root;
	}
	
	private String viewProdImage(byte[] myimage, String name) {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/VAADIN/themes/gerp/img/"
				+ name + ".png";
		if (myimage != null && !"null".equals(myimage)) {
			InputStream in = new ByteArrayInputStream(myimage);
			BufferedImage bImageFromConvert = null;
			try {
				bImageFromConvert = ImageIO.read(in);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				ImageIO.write(bImageFromConvert, "png", new File(basepath));
			}
			catch (Exception e) {
			}
			try {
				ImageIO.write(bImageFromConvert, "jpg", new File(basepath));
			}
			catch (Exception e) {
			}
			try {
				return basepath;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/VAADIN/themes/gerp/img/box.png";
		}
		return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/VAADIN/themes/gerp/img/box.png";
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (btnSearch == event.getButton()) {
			viewProductDetails();
		}
	}
	
	private void resetFields() {
		// for basic information
		tfProductCode.setValue("");
		tfProductName.setValue("");
		taProductDesc.setValue("");
		taProductShortDesc.setValue("");
		tfEnquiryRef.setValue("");
		dfEnquiryDate.setValue(null);
		tfQuoteRef.setValue("");
		dfQuoteDate.setValue(null);
		tfPORef.setValue("");
		dfPODate.setValue(null);
		tfInvoiceRef.setValue("");
		dfInvoiceDate.setValue(null);
		tfWorkorderRef.setValue("");
		dfWorkorderDate.setValue(null);
		// for enquiry information
		tfEnquiryNumber.setValue("");
		tfBranchName.setValue("");
		tfClientCode.setValue("");
		tfClientName.setValue("");
		tfModeofEnquiry.setValue("");
		dfEnquiryDate1.setValue(null);
		tfDrawingNo.setValue("");
		tfPartNumber.setValue("");
		taEnqRemarks.setValue("");
		// for die information
		dfDieReqDate.setValue(null);
		tfDieReqNumber.setValue("");
		tfNoofDie.setValue("");
		tfIsNewDie.setValue("");
		dfDieComplDate.setValue(null);
		tfDieStatus.setValue("");
		taDieChangeNote.setValue("");
		tfDieModel.setValue("");
		tfWorkNature.setValue("");
		tfDieRegBy.setValue("");
		tfDieRecvBy.setValue("");
		dfDieSecDate.setValue(null);
		taTrailPerfomance.setValue("");
		taCmntsRectified.setValue("");
		// for production information
		tfExtMachine.setValue("");
		tfExtRefNumber.setValue("");
		dfExtDate.setValue(null);
		tfPulzRefNo.setValue("");
		dfPulzRefDate.setValue(null);
		tfRotoRefNo.setValue("");
		dfRotoRefDate.setValue(null);
		tfArmNumber.setValue("");
		tfAssemblyRef.setValue("");
		dfAssemblyDate.setValue(null);
		tfAssembledBy.setValue("");
		tfFoamRefNumber.setValue("");
		tfFoamRefDate.setValue(null);
		tfFoamBy.setValue("");
	}
	
	private void setReadOnlyFalse() {
		// for basic information
		tfProductCode.setReadOnly(false);
		tfProductName.setReadOnly(false);
		taProductDesc.setReadOnly(false);
		taProductShortDesc.setReadOnly(false);
		tfEnquiryRef.setReadOnly(false);
		dfEnquiryDate.setReadOnly(false);
		tfQuoteRef.setReadOnly(false);
		dfQuoteDate.setReadOnly(false);
		tfPORef.setReadOnly(false);
		dfPODate.setReadOnly(false);
		tfInvoiceRef.setReadOnly(false);
		dfInvoiceDate.setReadOnly(false);
		tfWorkorderRef.setReadOnly(false);
		dfWorkorderDate.setReadOnly(false);
		// for enquiry information
		tfEnquiryNumber.setReadOnly(false);
		tfBranchName.setReadOnly(false);
		tfClientCode.setReadOnly(false);
		tfClientName.setReadOnly(false);
		tfModeofEnquiry.setReadOnly(false);
		dfEnquiryDate1.setReadOnly(false);
		tfDrawingNo.setReadOnly(false);
		tfPartNumber.setReadOnly(false);
		taEnqRemarks.setReadOnly(false);
		// for die information
		dfDieReqDate.setReadOnly(false);
		tfDieReqNumber.setReadOnly(false);
		tfNoofDie.setReadOnly(false);
		tfIsNewDie.setReadOnly(false);
		dfDieComplDate.setReadOnly(false);
		tfDieStatus.setReadOnly(false);
		taDieChangeNote.setReadOnly(false);
		tfDieModel.setReadOnly(false);
		tfWorkNature.setReadOnly(false);
		tfDieRegBy.setReadOnly(false);
		tfDieRecvBy.setReadOnly(false);
		dfDieSecDate.setReadOnly(false);
		taTrailPerfomance.setReadOnly(false);
		taCmntsRectified.setReadOnly(false);
		// for production information
		tfExtMachine.setReadOnly(false);
		tfExtRefNumber.setReadOnly(false);
		dfExtDate.setReadOnly(false);
		tfPulzRefNo.setReadOnly(false);
		dfPulzRefDate.setReadOnly(false);
		tfRotoRefNo.setReadOnly(false);
		dfRotoRefDate.setReadOnly(false);
		tfArmNumber.setReadOnly(false);
		tfAssemblyRef.setReadOnly(false);
		dfAssemblyDate.setReadOnly(false);
		tfAssembledBy.setReadOnly(false);
		tfFoamRefNumber.setReadOnly(false);
		tfFoamRefDate.setReadOnly(false);
		tfFoamBy.setReadOnly(false);
	}
	
	private void setReadOnlyTrue() {
		// for basic information
		tfProductCode.setReadOnly(true);
		tfProductName.setReadOnly(true);
		taProductDesc.setReadOnly(true);
		taProductShortDesc.setReadOnly(true);
		tfEnquiryRef.setReadOnly(true);
		dfEnquiryDate.setReadOnly(true);
		tfQuoteRef.setReadOnly(true);
		dfQuoteDate.setReadOnly(true);
		tfPORef.setReadOnly(true);
		dfPODate.setReadOnly(true);
		tfInvoiceRef.setReadOnly(true);
		dfInvoiceDate.setReadOnly(true);
		tfWorkorderRef.setReadOnly(true);
		dfWorkorderDate.setReadOnly(true);
		// for enquiry information
		tfEnquiryNumber.setReadOnly(true);
		tfBranchName.setReadOnly(true);
		tfClientCode.setReadOnly(true);
		tfClientName.setReadOnly(true);
		tfModeofEnquiry.setReadOnly(true);
		dfEnquiryDate1.setReadOnly(true);
		tfDrawingNo.setReadOnly(true);
		tfPartNumber.setReadOnly(true);
		taEnqRemarks.setReadOnly(true);
		// for die information
		dfDieReqDate.setReadOnly(true);
		tfDieReqNumber.setReadOnly(true);
		tfNoofDie.setReadOnly(true);
		tfIsNewDie.setReadOnly(true);
		dfDieComplDate.setReadOnly(true);
		tfDieStatus.setReadOnly(true);
		taDieChangeNote.setReadOnly(true);
		tfDieModel.setReadOnly(true);
		tfWorkNature.setReadOnly(true);
		tfDieRegBy.setReadOnly(true);
		tfDieRecvBy.setReadOnly(true);
		dfDieSecDate.setReadOnly(true);
		taTrailPerfomance.setReadOnly(true);
		taCmntsRectified.setReadOnly(true);
		// for production information
		tfExtMachine.setReadOnly(true);
		tfExtRefNumber.setReadOnly(true);
		dfExtDate.setReadOnly(true);
		tfPulzRefNo.setReadOnly(true);
		dfPulzRefDate.setReadOnly(true);
		tfRotoRefNo.setReadOnly(true);
		dfRotoRefDate.setReadOnly(true);
		tfArmNumber.setReadOnly(true);
		tfAssemblyRef.setReadOnly(true);
		dfAssemblyDate.setReadOnly(true);
		tfAssembledBy.setReadOnly(true);
		tfFoamRefNumber.setReadOnly(true);
		tfFoamRefDate.setReadOnly(true);
		tfFoamBy.setReadOnly(true);
	}
	
	private void viewProductDetails() {
		// TODO Auto-generated method stub
		setReadOnlyFalse();
		resetFields();
		RotoCheckDtlDM rotoCheckDtlDM = null;
		Long poid = null;
		try {
			rotoCheckDtlDM = serviceRotoCheckDtl.getRotoCheckDtlDetatils(null, null, tfSerialNumber.getValue(), null,
					"P").get(0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (rotoCheckDtlDM != null) {
			ProductDM productDM = null;
			try {
				productDM = serviceProduct.getProductList(rotoCheckDtlDM.getProductId(), null, null, null, null, null,
						null, "F").get(0);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (productDM != null) {
				if (productDM.getProdimg() != null) {
					imgProduct.setSource(new ThemeResource(viewProdImage(productDM.getProdimg(),
							productDM.getProductcode())));
				}
				tfProductCode.setValue(productDM.getProductcode());
				tfProductName.setValue(productDM.getProdname());
				taProductDesc.setValue(productDM.getProddesc());
				taProductShortDesc.setValue(productDM.getShortdesc());
				try {
					RotoPlanDtlDM rotoPlanDtlDM = serviceRotoplandtl.getRotoPlanDtlList(null,
							rotoCheckDtlDM.getRotoid(), null, rotoCheckDtlDM.getProductId(), null).get(0);
					try {
						WorkOrderHdrDM workOrderHdrDM = serviceWrkOrdHdr.getWorkOrderHDRList(null,
								null, null, null, null, null, "F", rotoPlanDtlDM.getWoId(), null, null, null,null).get(0);
						loadEnquiryDetails(workOrderHdrDM.getEnquiryId(), rotoCheckDtlDM.getProductId());
						getEnqWorkflowDetails(workOrderHdrDM.getEnquiryId());
						getQATestHeaderDetails(workOrderHdrDM.getWorkOrdrId(), rotoCheckDtlDM.getProductId());
						getQCTestDetails(workOrderHdrDM.getWorkOrdrId());
						try {
							getQuoteDetails(workOrderHdrDM.getEnquiryId());
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						try {
							poid = getPODetails(workOrderHdrDM.getEnquiryId());
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						try {
							getInvoiceDetails(poid);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			Notification.show("No data found", Type.ERROR_MESSAGE);
		}
		setReadOnlyTrue();
	}
	
	private void loadEnquiryDetails(Long enquiryId, Long productId) {
		SmsEnqHdrDM enqHdrDM = serviceEnquiryHdr.getSmsEnqHdrList(null, enquiryId, null, null, null, "F", null, null)
				.get(0);
		tfEnquiryNumber.setValue(enqHdrDM.getEnquiryNo());
		tfBranchName.setValue(enqHdrDM.getBranchName());
		tfClientCode.setValue(enqHdrDM.getClientName());
		tfClientName.setValue(enqHdrDM.getClientName());
		tfModeofEnquiry.setValue(enqHdrDM.getModeofEnq());
		dfEnquiryDate1.setValue(enqHdrDM.getEnquiryDate());
		taEnqRemarks.setValue(enqHdrDM.getRemarks());
		try {
			SmsEnquiryDtlDM enquiryDtlDM = serviceEnqDetail.getsmsenquirydtllist(null, enquiryId, productId, null,
					null, null).get(0);
			tfPartNumber.setValue(enquiryDtlDM.getCustomField1());
			tfDrawingNo.setValue(enquiryDtlDM.getCustomField2());
			loadEnquirySpec(enquiryId, enquiryDtlDM.getEnquirydtlid());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadEnquirySpec(Long enquiryId, Long enqDtlId) {
		try {
			// for enquiry specification
			tblEnquirySpec.removeAllItems();
			List<SmsEnquirySpecDM> listEnqSepec = new ArrayList<SmsEnquirySpecDM>();
			if (enquiryId != null && enqDtlId != null) {
				listEnqSepec = serviceEnqSpec.getsmsenquiryspecList(null, enquiryId, enqDtlId, null, null);
			}
			BeanItemContainer<SmsEnquirySpecDM> beanpec = new BeanItemContainer<SmsEnquirySpecDM>(
					SmsEnquirySpecDM.class);
			beanpec.addAll(listEnqSepec);
			tblEnquirySpec.setContainerDataSource(beanpec);
			tblEnquirySpec.setVisibleColumns(new Object[] { "speccode", "specdesc", "enqryspecstatus", "lastupdateddt",
					"lastupdatedby" });
			tblEnquirySpec.setColumnHeaders(new String[] { "Specification Code", "Specification Description",
					"Enquiry Specification Status", "Last Updated Date", "Last Updated By" });
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getEnqWorkflowDetails(Long enquiryId) {
		try {
			tblEnquiryWorkflow.removeAllItems();
			List<EnquiryWorkflowDM> listWorkflow = new ArrayList<EnquiryWorkflowDM>();
			if (enquiryId != null) {
				listWorkflow = serviceWorkflow.getEnqWorkflowList(null, enquiryId, null, null, null);
			}
			BeanItemContainer<EnquiryWorkflowDM> beanWorkflow = new BeanItemContainer<EnquiryWorkflowDM>(
					EnquiryWorkflowDM.class);
			beanWorkflow.addAll(listWorkflow);
			tblEnquiryWorkflow.setContainerDataSource(beanWorkflow);
			tblEnquiryWorkflow.setVisibleColumns(new Object[] { "enqWorkflowId", "reworkOn", "fromDeptName",
					"initiatorName", "toDeptName", "pendingName", "workflowRequest", "status", "lastUpdatedDate",
					"lastUpdatedBy" });
			tblEnquiryWorkflow.setColumnHeaders(new String[] { "Ref.Id", "Date", "From", "Initiated By", "To",
					"Pending With", "Request", "Status", "Last Updated date", "Last Updated by" });
			tblEnquiryWorkflow.setColumnAlignment("enqWorkflowId", Align.RIGHT);
			tblEnquiryWorkflow.setColumnFooter("lastUpdatedBy", "No.of Records : " + listWorkflow.size());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getQATestHeaderDetails(Long workOrdNumber, Long productId) {
		List<QATestHdrDM> listQcTstHdr = new ArrayList<QATestHdrDM>();
		if (productId != null && workOrdNumber != null) {
			listQcTstHdr = serviceQATstHdr.getQaTestHdrDetails(null, null, null, null, productId, null);
		}
		BeanItemContainer<QATestHdrDM> beanQATstHdr = new BeanItemContainer<QATestHdrDM>(QATestHdrDM.class);
		beanQATstHdr.addAll(listQcTstHdr);
		tblQATestHdr.setContainerDataSource(beanQATstHdr);
		tblQATestHdr.setVisibleColumns(new Object[] { "qatestHdrid", "inspectionno", "inspectiondate", "clientName",
				"productName", "workOrdNo", "testresult", "teststatus", "lastupdateddate", "lastupdatedby" });
		tblQATestHdr.setColumnHeaders(new String[] { "Ref.Id", "Inspection No.", "Inspection Date", "Client Name",
				"Product Name", "Work Ord.No", "QC. Result", "Status", "Last Updated Date", "Last Updated By" });
		tblQATestHdr.setColumnAlignment("qatestHdrid", Align.RIGHT);
	}
	
	private void getQCTestDetails(Long woOrdId) {
		List<QcTestHdrDM> listQcTstHdr = new ArrayList<QcTestHdrDM>();
		listQcTstHdr = serviceQcTstHdr.getQcTestHdrDetails(null, null, null, null, null, null);
		BeanItemContainer<QcTestHdrDM> beanQcTstHdr = new BeanItemContainer<QcTestHdrDM>(QcTestHdrDM.class);
		beanQcTstHdr.addAll(listQcTstHdr);
		tblQCTestDetails.setContainerDataSource(beanQcTstHdr);
		tblQCTestDetails.setVisibleColumns(new Object[] { "qctestid", "inspectionno", "inspectiondate", "branchName",
				"prodName", "qcresult", "qcteststatus", "lastupdateddate", "lastupdatedby" });
		tblQCTestDetails.setColumnHeaders(new String[] { "Ref.Id", "Inspection No.", "Inspection Date", "Branch Name",
				"Product Name", "QC Result", "Status", "Last Updated Dt.", "Last Updated By" });
		tblQCTestDetails.setColumnAlignment("qctestid", Align.RIGHT);
	}
	
	private void getQuoteDetails(Long enquiryId) {
		SmsQuoteHdrDM quoteHdrDM = serviceQuoteHdr.getSmsQuoteHdrList(null, null, null, null, null, null, "P",
				enquiryId).get(0);
		tfQuoteRef.setValue(quoteHdrDM.getQuoteRef());
		tfQuoteNumber.setValue(quoteHdrDM.getQuoteNumber());
		dfQuoteDate.setValue(quoteHdrDM.getQuoteDate());
	}
	
	private Long getPODetails(Long enquiryId) {
		SmsPOHdrDM poHdrDM = servicesPOHdr.getSmspohdrList(null, null, null, null, null, null, null, "P", enquiryId)
				.get(0);
		tfPORef.setValue(poHdrDM.getPono());
		dfPODate.setValue(poHdrDM.getPodate());
		return poHdrDM.getPoid();
	}
	
	private void getInvoiceDetails(Long poid) {
		SmsInvoiceHdrDM invoiceHdrDM = serviceInvoiceHdr.getSmsInvoiceHeaderList(null, null, null, null, poid, null,
				null, "P").get(0);
		tfInvoiceRef.setValue(invoiceHdrDM.getInvoiceNo());
		dfInvoiceDate.setValue(invoiceHdrDM.getInvoiceDate());
	}
}
