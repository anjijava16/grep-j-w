/**
 * File Name 		: Campaign.java 
 * Description 		: this class is used for add/edit Client Campaign details. 
 * Author 			: P Sekhar
 * Date 			: Mar 18, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * 
 */
package com.gnts.crm.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.CompanyDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.CurrencyDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.CompanyService;
import com.gnts.base.service.mst.CurrencyService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.ProductService;
import com.gnts.crm.domain.txn.CampaignDM;
import com.gnts.crm.service.txn.CampaignService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.erputil.validations.DateValidation;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Campaign extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private ProductService serviceProduct = (ProductService) SpringContextHelper.getBean("Product");
	private CurrencyService servicebeanCurrency = (CurrencyService) SpringContextHelper.getBean("currency");
	private CompanyLookupService servicecompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private CampaignService serviceCampaign = (CampaignService) SpringContextHelper.getBean("clientCampaign");
	private CompanyService serviceCompany = (CompanyService) SpringContextHelper.getBean("companyBean");
	private Long companyId, campaingnId;
	private HorizontalLayout hlSearchLayout;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlCommetTblLayout = new VerticalLayout();
	private VerticalLayout vlDocumentLayout = new VerticalLayout();
	private HorizontalLayout hlInput = new HorizontalLayout();
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4, flColumn5;
	private String userName, strWidth = "130px";
	private Comments comment;
	private Documents document;
	/**
	 * UI Components
	 */
	private TextField tfProduct, tfcampaign, tfTagetAudieance, tfTargetSize, tfExptdBudget, tfExpRevenue,
			tfExpSalesCount, tfExpResepCount, tfExpRoi, tfActualBudget, tfAcualRev, tfActSalesCount,
			tfActResponseCount, tfActRoi;
	private ComboBox cbCampaignType, cbEmployee, cbCurrencyName, cbproduct, cbstatus, cbaction, cbreview;
	private TextArea taComments;
	private PopupDateField CampaignOpenDt, CampaignCloseDt, searchCampaignDt;
	private BeanItemContainer<CampaignDM> beanCampaign = null;
	private Long moduleId, clntCampaignId, employeeid;
	private WorkOrderHdrService serviceWrkOrdHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private int recordCnt = 0;
	private Logger logger = Logger.getLogger(Campaign.class);
	private String prodName;
	private Label lblspace;
	private Long branchID;
	private Long roleId;
	private Long appScreenId;
	private TextField tfExptBugt, tfActBugt, tfExptRvnu, tfActRvnu, tfExptROI, tfActROI;
	
	public Campaign() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = Long.valueOf(UI.getCurrent().getSession().getAttribute("moduleId").toString());
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		employeeid = ((Long) (UI.getCurrent().getSession().getAttribute("employeeId")));
		buildview();
	}
	
	/**
	 * buildMainview()-->for screen UI design
	 * 
	 * @param clArgumentLayout
	 * @param hlHeaderLayout
	 */
	private void buildview() {
		tfExptBugt = new TextField();
		tfActBugt = new TextField();
		tfExptRvnu = new TextField();
		tfActRvnu = new TextField();
		tfExptROI = new TextField();
		tfActROI = new TextField();
		tfExptBugt.setWidth("30");
		tfActBugt.setWidth("30");
		tfExptRvnu.setWidth("30");
		tfActRvnu.setWidth("30");
		tfExptROI.setWidth("30");
		tfActROI.setWidth("30");
		cbEmployee = new ComboBox("Owner");
		cbaction = new GERPComboBox("Actioned By");
		cbreview = new GERPComboBox("Reviewed By");
		cbEmployee.setItemCaptionPropertyId("firstname");
		cbEmployee.setWidth(strWidth);
		cbEmployee.setNullSelectionAllowed(false);
		loadEmployeeList();
		cbaction.setWidth(strWidth);
		cbaction.setItemCaptionPropertyId("firstname");
		cbreview.setWidth(strWidth);
		cbreview.setItemCaptionPropertyId("firstname");
		CampaignOpenDt = new GERPPopupDateField("Start Date");
		CampaignOpenDt.setRequired(true);
		CampaignOpenDt.setDateFormat("dd-MMM-yyyy");
		CampaignCloseDt = new GERPPopupDateField("End Date");
		CampaignCloseDt.setDateFormat("dd-MMM-yyyy");
		CampaignCloseDt.setRequired(true);
		cbCampaignType = new GERPComboBox("Campaign Type");
		cbCampaignType.setItemCaptionPropertyId("lookupname");
		cbCampaignType.setWidth(strWidth);
		cbCampaignType.setNullSelectionAllowed(false);
		cbCampaignType.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				cbCampaignType.setComponentError(null);
				if (cbCampaignType.getValue() != null) {
					cbCampaignType.setComponentError(null);
				}
			}
		});
		loadCampaignTypeByLookUpList();
		cbCurrencyName = new GERPComboBox("Currency Name");
		cbCurrencyName.setItemCaptionPropertyId("ccyname");
		cbCurrencyName.setNullSelectionAllowed(false);
		cbCurrencyName.setWidth(strWidth);
		cbCurrencyName.setImmediate(true);
		loadCurrencyList();
		tfcampaign = new GERPTextField("Campaign Name");
		tfcampaign.setMaxLength(45);
		tfcampaign.setWidth(strWidth);
		tfcampaign.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				tfcampaign.setComponentError(null);
				if (tfcampaign.getValue() != null) {
					tfcampaign.setComponentError(null);
				}
			}
		});
		cbproduct = new GERPComboBox("Product Name");
		cbproduct.setWidth(strWidth);
		cbproduct.setItemCaptionPropertyId("prodname");
		loadProductList();
		tfProduct = new GERPTextField("Product");
		tfProduct.setMaxLength(40);
		tfProduct.setWidth(strWidth);
		cbproduct.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbproduct.getValue() != null) {
					tfProduct.setValue(((ProductDM) cbproduct.getValue()).getProdname());
				}
			}
		});
		tfTagetAudieance = new GERPTextField("Target Audience");
		tfTagetAudieance.setMaxLength(100);
		tfTagetAudieance.setWidth(strWidth);
		tfTargetSize = new GERPTextField("Target Size");
		tfTargetSize.setWidth(strWidth);
		tfExptdBudget = new TextField();
		tfExptdBudget.setWidth("105");
		tfExpRevenue = new TextField();
		tfExpRevenue.setWidth("105");
		tfExpSalesCount = new GERPTextField("Expt. Sales Count");
		tfExpSalesCount.setWidth(strWidth);
		tfExpResepCount = new GERPTextField("Expt. Response Count");
		tfExpResepCount.setWidth(strWidth);
		tfExpRoi = new TextField();
		tfExpRoi.setWidth("105");
		tfActualBudget = new TextField();
		tfActualBudget.setWidth("105");
		tfAcualRev = new TextField();
		tfAcualRev.setWidth("105");
		tfActSalesCount = new GERPTextField("Actual Sales Count");
		tfActSalesCount.setWidth(strWidth);
		tfActResponseCount = new GERPTextField("Actual Responce Count");
		tfActResponseCount.setWidth(strWidth);
		tfActRoi = new TextField();
		tfActRoi.setWidth("105");
		taComments = new GERPTextArea("Comments");
		taComments.setWidth(strWidth);
		taComments.setHeight("50px");
		searchCampaignDt = new GERPPopupDateField("Campaign Date");
		searchCampaignDt.setDateFormat("dd-MMM-yyyy ");
		searchCampaignDt.addValidator(new DateValidation("Invalid date entered"));
		try {
			ApprovalSchemaDM obj = serviceWrkOrdHdr.getReviewerId(companyId, appScreenId, branchID, roleId).get(0);
			if (obj.getApprLevel().equals("Reviewer")) {
				cbstatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR, BASEConstants.WO_RV_STATUS);
			} else {
				cbstatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR, BASEConstants.WO_AP_STATUS);
			}
		}
		catch (Exception e) {
		}
		lblspace = new Label();
		hlSearchLayout = new HorizontalLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		hlSearchLayout.removeAllComponents();
		CampaignOpenDt.setRequired(false);
		CampaignCloseDt.setRequired(false);
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn5 = new FormLayout();
		flColumn1.addComponent(tfProduct);
		flColumn2.addComponent(tfcampaign);
		flColumn2.setSpacing(true);
		flColumn4.addComponent(cbCampaignType);
		flColumn5.addComponent(cbstatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(lblspace);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.addComponent(flColumn5);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSpacing(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// add the form layouts into user input layout
		CampaignCloseDt.setRequired(true);
		CampaignOpenDt.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		hlInput.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(tfcampaign);
		tfcampaign.setRequired(true);
		flColumn1.addComponent(CampaignOpenDt);
		flColumn1.addComponent(CampaignCloseDt);
		flColumn1.addComponent(cbEmployee);
		flColumn1.addComponent(cbCampaignType);
		cbCampaignType.setRequired(true);
		flColumn1.addComponent(tfTagetAudieance);
		flColumn1.addComponent(cbCurrencyName);
		flColumn2.addComponent(cbproduct);
		flColumn2.addComponent(tfProduct);
		flColumn2.addComponent(tfTargetSize);
		//
		HorizontalLayout hlExpctBudt = new HorizontalLayout();
		hlExpctBudt.addComponent(tfExptBugt);
		hlExpctBudt.addComponent(tfExptdBudget);
		hlExpctBudt.setCaption("Expected Budget");
		flColumn2.addComponent(hlExpctBudt);
		flColumn2.setComponentAlignment(hlExpctBudt, Alignment.TOP_LEFT);
		//
		HorizontalLayout hlRnv = new HorizontalLayout();
		hlRnv.addComponent(tfExptRvnu);
		hlRnv.addComponent(tfExpRevenue);
		hlRnv.setCaption("Expected Revenue");
		flColumn2.addComponent(hlRnv);
		flColumn2.setComponentAlignment(hlRnv, Alignment.TOP_LEFT);
		//
		flColumn2.addComponent(tfExpSalesCount);
		flColumn2.addComponent(tfExpResepCount);
		flColumn2.addComponent(tfActResponseCount);
		//
		HorizontalLayout hlExROI = new HorizontalLayout();
		hlExROI.addComponent(tfExptROI);
		hlExROI.addComponent(tfExpRoi);
		hlExROI.setCaption("Expected ROI");
		flColumn3.addComponent(hlExROI);
		flColumn3.setComponentAlignment(hlExROI, Alignment.TOP_LEFT);
		//
		HorizontalLayout hlActBudgt = new HorizontalLayout();
		hlActBudgt.addComponent(tfActBugt);
		hlActBudgt.addComponent(tfActualBudget);
		hlActBudgt.setCaption("Actual Budget");
		flColumn3.addComponent(hlActBudgt);
		flColumn3.setComponentAlignment(hlActBudgt, Alignment.TOP_LEFT);
		//
		HorizontalLayout hlARnv = new HorizontalLayout();
		hlARnv.addComponent(tfActRvnu);
		hlARnv.addComponent(tfAcualRev);
		hlARnv.setCaption("Actual Revenue");
		flColumn3.addComponent(hlARnv);
		flColumn3.setComponentAlignment(hlARnv, Alignment.TOP_LEFT);
		//
		flColumn3.addComponent(tfActSalesCount);
		flColumn3.addComponent(tfActResponseCount);
		flColumn3.addComponent(cbaction);
		flColumn3.addComponent(cbreview);
		//
		HorizontalLayout hlActROI = new HorizontalLayout();
		hlActROI.addComponent(tfActROI);
		hlActROI.addComponent(tfActRoi);
		hlActROI.setCaption("Actual ROI");
		flColumn4.addComponent(hlActROI);
		flColumn4.setComponentAlignment(hlActROI, Alignment.TOP_LEFT);
		//
		flColumn4.addComponent(taComments);
		flColumn4.addComponent(cbstatus);
		hlInput.setMargin(true);
		VerticalLayout hlUserInput = new VerticalLayout();
		hlInput.setWidth("1200");
		hlInput.addComponent(flColumn1);
		hlInput.addComponent(flColumn2);
		hlInput.addComponent(flColumn3);
		hlInput.setSpacing(true);
		hlInput.setMargin(true);
		hlInput.addComponent(flColumn4);
		hlInput.setSizeUndefined();
		hlUserInput.addComponent(GERPPanelGenerator.createPanel(hlInput));
		TabSheet test3 = new TabSheet();
		test3.addTab(vlCommetTblLayout, "Comments");
		test3.addTab(vlDocumentLayout, "Documents");
		test3.setWidth("1280");
		hlUserInput.addComponent(test3);
		hlUserInputLayout.addComponent(hlUserInput);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
	}
	
	public void hluserInputReadonlyTrue() {
		tfcampaign.setReadOnly(true);
		CampaignOpenDt.setReadOnly(true);
		CampaignOpenDt.setReadOnly(true);
		cbEmployee.setReadOnly(true);
		cbCampaignType.setReadOnly(true);
		tfTagetAudieance.setReadOnly(true);
		cbCurrencyName.setReadOnly(true);
		tfProduct.setReadOnly(true);
		tfTargetSize.setReadOnly(true);
		tfExptdBudget.setReadOnly(true);
		tfExpRevenue.setReadOnly(true);
		tfExpSalesCount.setReadOnly(true);
		tfExpResepCount.setReadOnly(true);
		tfExpRoi.setReadOnly(true);
		tfActualBudget.setReadOnly(true);
		tfAcualRev.setReadOnly(true);
		tfActRoi.setReadOnly(true);
		tfActSalesCount.setReadOnly(true);
		taComments.setReadOnly(true);
		tfActResponseCount.setReadOnly(true);
		cbproduct.setReadOnly(true);
		cbstatus.setReadOnly(true);
		cbaction.setReadOnly(true);
		cbreview.setReadOnly(true);
	}
	
	public void hluserInputReadonlyFalse() {
		tfcampaign.setReadOnly(false);
		CampaignOpenDt.setReadOnly(false);
		CampaignCloseDt.setReadOnly(false);
		cbEmployee.setReadOnly(false);
		cbCampaignType.setReadOnly(false);
		tfTagetAudieance.setReadOnly(false);
		cbCurrencyName.setReadOnly(false);
		cbproduct.setReadOnly(false);
		tfProduct.setReadOnly(false);
		tfTargetSize.setReadOnly(false);
		tfExptdBudget.setReadOnly(false);
		tfExpRevenue.setReadOnly(false);
		tfExpSalesCount.setReadOnly(false);
		tfExpResepCount.setReadOnly(false);
		tfExpRoi.setReadOnly(false);
		tfActualBudget.setReadOnly(false);
		tfAcualRev.setReadOnly(false);
		tfActRoi.setReadOnly(false);
		taComments.setReadOnly(false);
		tfActSalesCount.setReadOnly(false);
		tfActResponseCount.setReadOnly(false);
		cbproduct.setReadOnly(false);
		cbstatus.setReadOnly(false);
		cbaction.setReadOnly(false);
		cbreview.setReadOnly(false);
	}
	
	public void loadSrchRslt() {
		try {
			tblMstScrSrchRslt.removeAllItems();
			List<CampaignDM> compaingList = new ArrayList<CampaignDM>();
			compaingList = serviceCampaign.getCampaignDetailList(companyId, null, null,
					(String) cbCampaignType.getValue(), (String) tfProduct.getValue(), (String) tfcampaign.getValue(),
					null, ((String) cbstatus.getValue()), "F");
			recordCnt = compaingList.size();
			beanCampaign = new BeanItemContainer<CampaignDM>(CampaignDM.class);
			beanCampaign.addAll(compaingList);
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Got the Client. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanCampaign);
			tblMstScrSrchRslt
					.setVisibleColumns(new Object[] { "campaingnId", "productName", "campaignname",
							"campaignStartDate", "campaignEndDate", "campaignType", "status", "lastUpdatedDt",
							"lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Product", "Campaign Name",
					"Campaign Start Date", "Campaign End Date", "Campaign Type", "Status", "Last Updated Date",
					"Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("campaingnId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	/**
	 * this method used for edit the table values
	 */
	private void editCampaignDetails() {
		logger.info("EditCampaignDetails------>+e");
		if (tblMstScrSrchRslt.getValue() != null) {
			CampaignDM editCampaign = beanCampaign.getItem(tblMstScrSrchRslt.getValue()).getBean();
			campaingnId = editCampaign.getCampaingnId();
			if (editCampaign.getActulRespCount() != null && !"null".equals(editCampaign.getActulRespCount())) {
				tfActResponseCount.setValue(editCampaign.getActulRespCount().toString());
			}
			if (editCampaign.getActulRoi() != null && !"null".equals(editCampaign.getActulRoi())) {
				tfActRoi.setValue(editCampaign.getActulRoi().toString());
			}
			if (editCampaign.getActulSalesCount() != null && !"null".equals(editCampaign.getActulSalesCount())) {
				tfActSalesCount.setValue(editCampaign.getActulSalesCount().toString());
			}
			if (editCampaign.getActulBudget() != null && !"null".equals(editCampaign.getActulBudget())) {
				tfActualBudget.setValue(editCampaign.getActulBudget().toString());
			}
			if (editCampaign.getActulRevenue() != null && !"null".equals(editCampaign.getActulRevenue())) {
				tfAcualRev.setValue(editCampaign.getActulRevenue().toString());
			}
			if (editCampaign.getExptdRespCount() != null && !"null".equals(editCampaign.getExptdRespCount())) {
				tfExpResepCount.setValue(editCampaign.getExptdRespCount().toString());
			}
			if (editCampaign.getExptdRevenue() != null && !"null".equals(editCampaign.getExptdRevenue())) {
				tfExpRevenue.setValue(editCampaign.getExptdRevenue().toString());
			}
			if (editCampaign.getExptdRoi() != null && !"null".equals(editCampaign.getExptdRoi())) {
				tfExpRoi.setValue(editCampaign.getExptdRoi().toString());
			}
			if (editCampaign.getExptdSalesCount() != null && !"null".equals(editCampaign.getExptdSalesCount())) {
				tfExpSalesCount.setValue(editCampaign.getExptdSalesCount().toString());
			}
			if (editCampaign.getExptdBudget() != null && !"null".equals(editCampaign.getExptdBudget())) {
				tfExptdBudget.setValue(editCampaign.getExptdBudget().toString());
			}
			CampaignOpenDt.setValue(editCampaign.getCampaignStartDateInt());
			CampaignCloseDt.setValue(editCampaign.getCampaignEndDateInt());
			tfcampaign.setValue(editCampaign.getCampaignname());
			tfProduct.setValue(editCampaign.getProductId().toString());
			cbCurrencyName.setValue(editCampaign.getCcyid());
			cbCampaignType.setValue(editCampaign.getCampaignType().toString());
			cbproduct.setValue(editCampaign.getProductId());
			Long prodid = editCampaign.getProductId();
			Collection<?> ProductIdcol = cbproduct.getItemIds();
			for (Iterator<?> iteratorclient = ProductIdcol.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbproduct.getItem(itemIdClient);
				// Get the actual bean and use the data
				ProductDM prodObj = (ProductDM) itemclient.getBean();
				if (prodid != null && prodid.equals(prodObj.getProdid())) {
					cbproduct.setValue(itemIdClient);
				}
			}
			Collection<?> collEmp = cbreview.getItemIds();
			for (Iterator<?> iterator = collEmp.iterator(); iterator.hasNext();) {
				Object itemId4 = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbreview.getItem(itemId4);
				EmployeeDM editEmployee = (EmployeeDM) item.getBean();
				if (editEmployee != null && editEmployee.getEmployeeid().equals(editEmployee.getEmployeeid())) {
					cbreview.setValue(itemId4);
					break;
				} else {
					cbreview.setValue(null);
				}
			}
			cbEmployee.setValue(editCampaign.getCampaignOwner());
			Collection<?> Emp = cbaction.getItemIds();
			for (Iterator<?> iterator = Emp.iterator(); iterator.hasNext();) {
				Object itemId4 = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbaction.getItem(itemId4);
				EmployeeDM editEmployee = (EmployeeDM) item.getBean();
				if (editEmployee != null && editEmployee.getEmployeeid().equals(editEmployee.getEmployeeid())) {
					cbaction.setValue(itemId4);
					break;
				} else {
					cbaction.setValue(null);
				}
			}
			cbaction.setValue(editCampaign.getActionedby());
			cbreview.setValue(editCampaign.getReviewdby());
			if (editCampaign.getTargetAudience() != null && !"null".equals(editCampaign.getTargetAudience())) {
				tfTagetAudieance.setValue(editCampaign.getTargetAudience());
			}
			if (editCampaign.getTargetSize() != null && !"null".equals(editCampaign.getTargetSize())) {
				tfTargetSize.setValue(editCampaign.getTargetSize().toString());
			}
			if (editCampaign.getComments() != null && !"null".equals(editCampaign.getComments())) {
				taComments.setValue(editCampaign.getComments());
			}
			cbstatus.setValue(editCampaign.getStatus());
			if (cbstatus.getValue().equals("Approved") || cbstatus.getValue().equals("Closed")) {
				hluserInputReadonlyTrue();
			}
			comment = new Comments(vlCommetTblLayout, employeeid, null, null, null, null, campaingnId, null);
			document = new Documents(vlDocumentLayout, null, null, null, null, campaingnId, null);
			comment.loadsrch(true, null, null, campaingnId, null, null, null);
			document.loadsrcrslt(true, null, null, campaingnId, null, null, null);
		}
	}
	
	private void loadCurrencyList() {
		try {
			Long getCcyid = null;
			String currencysymbol = null;
			List<CompanyDM> companyList = serviceCompany.getCompanyList(null, "Active", companyId);
			for (CompanyDM company : companyList) {
				getCcyid = company.getCcyid();
			}
			List<CurrencyDM> getCurrencylist = servicebeanCurrency.getCurrencyList(getCcyid, null, null, "Active", "F");
			for (CurrencyDM currency : getCurrencylist) {
				currencysymbol = currency.getCcysymbol();
			}
			tfExptBugt.setReadOnly(false);
			tfExptBugt.setValue(currencysymbol);
			tfExptBugt.setReadOnly(true);
			tfActBugt.setReadOnly(false);
			tfActBugt.setValue(currencysymbol);
			tfActBugt.setReadOnly(true);
			tfExptRvnu.setReadOnly(false);
			tfExptRvnu.setValue(currencysymbol);
			tfExptRvnu.setReadOnly(true);
			tfActRvnu.setReadOnly(false);
			tfActRvnu.setValue(currencysymbol);
			tfActRvnu.setReadOnly(true);
			tfExptROI.setReadOnly(false);
			tfExptROI.setValue(currencysymbol);
			tfExptROI.setReadOnly(true);
			tfActROI.setReadOnly(false);
			tfActROI.setValue(currencysymbol);
			tfActROI.setReadOnly(true);
			BeanContainer<Long, CurrencyDM> beanCurrency = new BeanContainer<Long, CurrencyDM>(CurrencyDM.class);
			beanCurrency.setBeanIdProperty("ccyid");
			beanCurrency.addAll(getCurrencylist);
			cbCurrencyName.setContainerDataSource(beanCurrency);
		}
		catch (Exception e) {
			logger.info("Loading null values in loadCurrencyList() function------>>>>" + e);
		}
	}
	
	private void loadProductList() {
		try {
			BeanItemContainer<ProductDM> beanproduct = new BeanItemContainer<ProductDM>(ProductDM.class);
			beanproduct.addAll(serviceProduct
					.getProductList(companyId, null, null, prodName, "Active", null, null, "P"));
			cbproduct.setContainerDataSource(beanproduct);
		}
		catch (Exception e) {
			logger.info("Loading null values in loadCurrencyList() function------>>>>" + e);
		}
	}
	
	/**
	 * this method used to load the employee list based on company id and status
	 */
	private void loadEmployeeList() {
		List<EmployeeDM> empList = serviceEmployee.getEmployeeList(null, null, null, "Active", companyId, null, null,
				null, null, "P");
		BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanEmployee.setBeanIdProperty("employeeid");
		beanEmployee.addAll(empList);
		cbaction.setContainerDataSource(beanEmployee);
		cbEmployee.setContainerDataSource(beanEmployee);
		cbreview.setContainerDataSource(beanEmployee);
	}
	
	private void loadCampaignTypeByLookUpList() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompLookUp.setBeanIdProperty("lookupname");
			beanCompLookUp.addAll(servicecompanyLookup.getCompanyLookUpByLookUp(companyId, moduleId, "Active",
					"CM_CAMPTYE"));
			cbCampaignType.setContainerDataSource(beanCompLookUp);
		}
		catch (Exception e) {
			logger.info("load company look up details" + e);
		}
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
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		resetFields();
		cbstatus.setValue(null);
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		hlCmdBtnLayout.setVisible(false);
		hlInput.removeAllComponents();
		assembleUserInputLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		comment = new Comments(vlCommetTblLayout, employeeid, null, null, null, null, clntCampaignId, null);
		document = new Documents(vlDocumentLayout, null, null, null, null, clntCampaignId, null);
	}
	
	@Override
	protected void editDetails() {
		// TODO Auto-generated method stub
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent((hlUserInputLayout));
		editCampaignDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean errorflag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		tfProduct.setComponentError(null);
		if ((tfcampaign.getValue() == null) || tfcampaign.getValue().trim().length() == 0) {
			tfcampaign.setComponentError(new UserError(GERPErrorCodes.NULL_CAMPAIGN_NAME));
			errorflag = true;
		} else {
			tfcampaign.setComponentError(null);
		}
		if (CampaignOpenDt.getValue() == null) {
			CampaignOpenDt.setComponentError(new UserError(GERPErrorCodes.NULL_CAMPAIGN_DATE));
			errorflag = true;
		} else {
			CampaignOpenDt.setComponentError(null);
		}
		if ((CampaignCloseDt.getValue() != null) || (CampaignCloseDt.getValue() != null)) {
			if (CampaignOpenDt.getValue().after(CampaignCloseDt.getValue())) {
				CampaignCloseDt.setComponentError(new UserError(GERPErrorCodes.CRM_DATE_OUTOFRANGE));
				errorflag = true;
			}
		} else {
			CampaignCloseDt.setComponentError(null);
		}
		if (cbCampaignType.getValue() == null) {
			cbCampaignType.setComponentError(new UserError(GERPErrorCodes.NULL_CAMPAIGN_TYPE));
			errorflag = true;
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyId + " | User Name :  "
					+ "Throwing ValidationException. User data is > " + tfProduct.getValue() + tfcampaign.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("CampaignsaveDetails------>");
			CampaignDM campaignobj = new CampaignDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				campaignobj = beanCampaign.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (cbEmployee.getValue() != null) {
				campaignobj.setCampaignOwner((Long) cbEmployee.getValue());
			}
			campaignobj.setCampaignStartDate(CampaignOpenDt.getValue());
			campaignobj.setCampaignEndDate(CampaignCloseDt.getValue());
			if (cbCurrencyName.getValue() != null) {
				campaignobj.setCcyid((Long) cbCurrencyName.getValue());
			}
			if (tfTargetSize.getValue() != null && tfTargetSize.getValue().trim().length() > 0) {
				campaignobj.setTargetSize(Long.valueOf(tfTargetSize.getValue().toString()));
			}
			campaignobj.setComments(taComments.getValue());
			if (tfActualBudget.getValue() != null && tfActualBudget.getValue().trim().length() > 0) {
				campaignobj.setActulBudget(Long.valueOf(tfActualBudget.getValue()));
			}
			if (tfActResponseCount.getValue() != null && tfActResponseCount.getValue().trim().length() > 0) {
				campaignobj.setActulRespCount(Long.valueOf(tfActResponseCount.getValue()));
			}
			if (tfAcualRev.getValue() != null && tfAcualRev.getValue().trim().length() > 0) {
				campaignobj.setActulRevenue(Long.valueOf(tfAcualRev.getValue()));
			}
			if (tfActRoi.getValue() != null && tfActRoi.getValue().trim().length() > 0) {
				campaignobj.setActulRoi(Long.valueOf(tfActRoi.getValue().toString()));
			}
			if (tfActSalesCount.getValue() != null && tfActSalesCount.getValue().trim().length() > 0) {
				campaignobj.setActulSalesCount(Long.valueOf(tfActSalesCount.getValue()));
			}
			campaignobj.setCampaignType(cbCampaignType.getValue().toString());
			campaignobj.setComments(taComments.getValue());
			if (tfExptdBudget.getValue() != null && tfExptdBudget.getValue().trim().length() > 0) {
				campaignobj.setExptdBudget(Long.valueOf(tfExptdBudget.getValue()));
			}
			if (tfExpResepCount.getValue() != null && tfExpResepCount.getValue().trim().length() > 0) {
				campaignobj.setExptdRespCount(Long.valueOf(tfExpResepCount.getValue()));
			}
			if (tfExpRevenue.getValue() != null && tfExpRevenue.getValue().trim().length() > 0) {
				campaignobj.setExptdRevenue(Long.valueOf(tfExpRevenue.getValue()));
			}
			if (tfExpRoi.getValue() != null && tfExpRoi.getValue().trim().length() > 0) {
				campaignobj.setExptdRoi(Long.valueOf(tfExpRoi.getValue()));
			}
			if (tfExpSalesCount.getValue() != null && tfExpSalesCount.getValue().trim().length() > 0) {
				campaignobj.setExptdSalesCount(Long.valueOf(tfExpSalesCount.getValue()));
			}
			if (cbproduct.getValue() != null) {
				campaignobj.setProductId(((ProductDM) cbproduct.getValue()).getProdid());
			}
			campaignobj.setProductName(tfProduct.getValue());
			if (cbstatus.getValue() != null) {
				campaignobj.setStatus(cbstatus.getValue().toString());
			}
			campaignobj.setCampaignname(tfcampaign.getValue());
			campaignobj.setCompanyId(companyId);
			campaignobj.setLastUpdatedBy(userName);
			campaignobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			if (tfTagetAudieance.getValue().toString().trim().length() > 0) {
				campaignobj.setTargetAudience(tfTagetAudieance.getValue());
			}
			if (tfTargetSize.getValue().toString().trim().length() > 0) {
				campaignobj.setTargetSize(Long.valueOf(tfTargetSize.getValue()));
			}
			campaignobj.setPreparedby(employeeid);
			if (cbaction != null) {
				campaignobj.setActionedby((Long) cbaction.getValue());
			}
			if (cbreview.getValue() != null) {
				campaignobj.setReviewdby((Long) cbreview.getValue());
			}
			serviceCampaign.saveOrUpdateCampaignDetails(campaignobj);
			loadSrchRslt();
			comment.savecampaign(campaignobj.getCampaingnId());
			comment.resetfields();
			document.savecampaign(campaignobj.getCampaingnId());
			document.ResetFields();
		}
		catch (Exception e) {
			logger.info("saveOrUpdateCampaignDetails------>+e");
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for CompanyLookUp. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_CRM_CAMPAIGN);
		UI.getCurrent().getSession().setAttribute("audittablepk", campaingnId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		hlCmdBtnLayout.setVisible(true);
		tfcampaign.setRequired(false);
		hluserInputReadonlyFalse();
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		resetFields();
		tblMstScrSrchRslt.setVisible(true);
		tfProduct.setRequired(false);
		cbCampaignType.setRequired(false);
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		tfcampaign.setValue("");
		tfcampaign.setComponentError(null);
		tfActResponseCount.setValue("");
		tfActRoi.setValue("0");
		tfActSalesCount.setValue("");
		tfActualBudget.setValue("0");
		tfAcualRev.setValue("0");
		tfExpResepCount.setValue("");
		tfExpRevenue.setValue("0");
		tfExpRoi.setValue("0");
		tfExpSalesCount.setValue("");
		tfExptdBudget.setValue("0");
		taComments.setValue("");
		tfTagetAudieance.setValue("");
		tfTargetSize.setComponentError(null);
		tfTargetSize.setValue("");
		tfProduct.setValue("");
		tfProduct.setComponentError(null);
		cbreview.setValue(null);
		cbaction.setValue(null);
		cbEmployee.setValue(null);
		cbproduct.setValue(null);
		cbproduct.setComponentError(null);
		CampaignOpenDt.setValue(null);
		CampaignCloseDt.setValue(null);
		searchCampaignDt.setValue(null);
		cbCurrencyName.setValue(cbCurrencyName.getItemIds().iterator().next());
		cbCampaignType.setValue(null);
		cbCampaignType.setComponentError(null);
		cbstatus.setValue(null);
		tfTagetAudieance.setValue("");
		tfTargetSize.setValue("");
	}
}
