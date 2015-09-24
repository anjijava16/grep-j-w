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
	private CurrencyService serviceCurrency = (CurrencyService) SpringContextHelper.getBean("currency");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
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
	private TextField tfProduct, tfCampaignName, tfTagetAudieance, tfTargetSize, tfExptdBudget, tfExpRevenue,
			tfExpSalesCount, tfExpResepCount, tfExpRoi, tfActualBudget, tfAcualRev, tfActSalesCount,
			tfActResponseCount, tfActRoi;
	private ComboBox cbCampaignType, cbEmployee, cbCurrencyName, cbProduct, cbStatus, cbAction, cbReview;
	private TextArea taComments;
	private PopupDateField dfCampaignStart, dfCampaignEnd, searchCampaignDt;
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
		cbAction = new GERPComboBox("Actioned By");
		cbReview = new GERPComboBox("Reviewed By");
		cbEmployee.setItemCaptionPropertyId("firstname");
		cbEmployee.setWidth(strWidth);
		cbEmployee.setNullSelectionAllowed(false);
		loadEmployeeList();
		cbAction.setWidth(strWidth);
		cbAction.setItemCaptionPropertyId("firstname");
		cbReview.setWidth(strWidth);
		cbReview.setItemCaptionPropertyId("firstname");
		dfCampaignStart = new GERPPopupDateField("Start Date");
		dfCampaignStart.setRequired(true);
		dfCampaignStart.setDateFormat("dd-MMM-yyyy");
		dfCampaignEnd = new GERPPopupDateField("End Date");
		dfCampaignEnd.setDateFormat("dd-MMM-yyyy");
		dfCampaignEnd.setRequired(true);
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
		tfCampaignName = new GERPTextField("Campaign Name");
		tfCampaignName.setMaxLength(45);
		tfCampaignName.setWidth(strWidth);
		tfCampaignName.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				tfCampaignName.setComponentError(null);
				if (tfCampaignName.getValue() != null) {
					tfCampaignName.setComponentError(null);
				}
			}
		});
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setWidth(strWidth);
		cbProduct.setItemCaptionPropertyId("prodname");
		loadProductList();
		tfProduct = new GERPTextField("Product");
		tfProduct.setMaxLength(40);
		tfProduct.setWidth(strWidth);
		cbProduct.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbProduct.getValue() != null) {
					tfProduct.setValue(((ProductDM) cbProduct.getValue()).getProdname());
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
				cbStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR, BASEConstants.WO_RV_STATUS);
			} else {
				cbStatus = new GERPComboBox("Status", BASEConstants.T_MFG_WORKORDER_HDR, BASEConstants.WO_AP_STATUS);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		dfCampaignStart.setRequired(false);
		dfCampaignEnd.setRequired(false);
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn5 = new FormLayout();
		flColumn1.addComponent(tfProduct);
		flColumn2.addComponent(tfCampaignName);
		flColumn2.setSpacing(true);
		flColumn4.addComponent(cbCampaignType);
		flColumn5.addComponent(cbStatus);
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
		dfCampaignEnd.setRequired(true);
		dfCampaignStart.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		hlInput.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(tfCampaignName);
		tfCampaignName.setRequired(true);
		flColumn1.addComponent(dfCampaignStart);
		flColumn1.addComponent(dfCampaignEnd);
		flColumn1.addComponent(cbEmployee);
		flColumn1.addComponent(cbCampaignType);
		cbCampaignType.setRequired(true);
		flColumn1.addComponent(tfTagetAudieance);
		flColumn1.addComponent(cbCurrencyName);
		flColumn2.addComponent(cbProduct);
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
		flColumn3.addComponent(cbAction);
		flColumn3.addComponent(cbReview);
		//
		HorizontalLayout hlActROI = new HorizontalLayout();
		hlActROI.addComponent(tfActROI);
		hlActROI.addComponent(tfActRoi);
		hlActROI.setCaption("Actual ROI");
		flColumn4.addComponent(hlActROI);
		flColumn4.setComponentAlignment(hlActROI, Alignment.TOP_LEFT);
		//
		flColumn4.addComponent(taComments);
		flColumn4.addComponent(cbStatus);
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
	
	private void hluserInputReadonlyTrue() {
		tfCampaignName.setReadOnly(true);
		dfCampaignStart.setReadOnly(true);
		dfCampaignStart.setReadOnly(true);
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
		cbProduct.setReadOnly(true);
		cbStatus.setReadOnly(true);
		cbAction.setReadOnly(true);
		cbReview.setReadOnly(true);
	}
	
	private void hluserInputReadonlyFalse() {
		tfCampaignName.setReadOnly(false);
		dfCampaignStart.setReadOnly(false);
		dfCampaignEnd.setReadOnly(false);
		cbEmployee.setReadOnly(false);
		cbCampaignType.setReadOnly(false);
		tfTagetAudieance.setReadOnly(false);
		cbCurrencyName.setReadOnly(false);
		cbProduct.setReadOnly(false);
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
		cbProduct.setReadOnly(false);
		cbStatus.setReadOnly(false);
		cbAction.setReadOnly(false);
		cbReview.setReadOnly(false);
	}
	
	private void loadSrchRslt() {
		try {
			tblMstScrSrchRslt.removeAllItems();
			List<CampaignDM> listCampaign = new ArrayList<CampaignDM>();
			listCampaign = serviceCampaign.getCampaignDetailList(companyId, null, null,
					(String) cbCampaignType.getValue(), (String) tfProduct.getValue(), (String) tfCampaignName.getValue(),
					null, ((String) cbStatus.getValue()), "F");
			recordCnt = listCampaign.size();
			beanCampaign = new BeanItemContainer<CampaignDM>(CampaignDM.class);
			beanCampaign.addAll(listCampaign);
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
			logger.info(e.getMessage());
		}
	}
	
	/**
	 * this method used for edit the table values
	 */
	private void editCampaignDetails() {
		try {
			logger.info("EditCampaignDetails------>+e");
			if (tblMstScrSrchRslt.getValue() != null) {
				CampaignDM campaignDM = beanCampaign.getItem(tblMstScrSrchRslt.getValue()).getBean();
				campaingnId = campaignDM.getCampaingnId();
				if (campaignDM.getActulRespCount() != null && !"null".equals(campaignDM.getActulRespCount())) {
					tfActResponseCount.setValue(campaignDM.getActulRespCount().toString());
				}
				if (campaignDM.getActulRoi() != null && !"null".equals(campaignDM.getActulRoi())) {
					tfActRoi.setValue(campaignDM.getActulRoi().toString());
				}
				if (campaignDM.getActulSalesCount() != null && !"null".equals(campaignDM.getActulSalesCount())) {
					tfActSalesCount.setValue(campaignDM.getActulSalesCount().toString());
				}
				if (campaignDM.getActulBudget() != null && !"null".equals(campaignDM.getActulBudget())) {
					tfActualBudget.setValue(campaignDM.getActulBudget().toString());
				}
				if (campaignDM.getActulRevenue() != null && !"null".equals(campaignDM.getActulRevenue())) {
					tfAcualRev.setValue(campaignDM.getActulRevenue().toString());
				}
				if (campaignDM.getExptdRespCount() != null && !"null".equals(campaignDM.getExptdRespCount())) {
					tfExpResepCount.setValue(campaignDM.getExptdRespCount().toString());
				}
				if (campaignDM.getExptdRevenue() != null && !"null".equals(campaignDM.getExptdRevenue())) {
					tfExpRevenue.setValue(campaignDM.getExptdRevenue().toString());
				}
				if (campaignDM.getExptdRoi() != null && !"null".equals(campaignDM.getExptdRoi())) {
					tfExpRoi.setValue(campaignDM.getExptdRoi().toString());
				}
				if (campaignDM.getExptdSalesCount() != null && !"null".equals(campaignDM.getExptdSalesCount())) {
					tfExpSalesCount.setValue(campaignDM.getExptdSalesCount().toString());
				}
				if (campaignDM.getExptdBudget() != null && !"null".equals(campaignDM.getExptdBudget())) {
					tfExptdBudget.setValue(campaignDM.getExptdBudget().toString());
				}
				dfCampaignStart.setValue(campaignDM.getCampaignStartDateInt());
				dfCampaignEnd.setValue(campaignDM.getCampaignEndDateInt());
				tfCampaignName.setValue(campaignDM.getCampaignname());
				tfProduct.setValue(campaignDM.getProductId().toString());
				cbCurrencyName.setValue(campaignDM.getCcyid());
				cbCampaignType.setValue(campaignDM.getCampaignType().toString());
				cbProduct.setValue(campaignDM.getProductId());
				Long prodid = campaignDM.getProductId();
				Collection<?> prodids = cbProduct.getItemIds();
				for (Iterator<?> iteratorclient = prodids.iterator(); iteratorclient.hasNext();) {
					Object itemIdClient = (Object) iteratorclient.next();
					BeanItem<?> itemclient = (BeanItem<?>) cbProduct.getItem(itemIdClient);
					// Get the actual bean and use the data
					ProductDM prodObj = (ProductDM) itemclient.getBean();
					if (prodid != null && prodid.equals(prodObj.getProdid())) {
						cbProduct.setValue(itemIdClient);
					}
				}
				Collection<?> collEmp = cbReview.getItemIds();
				for (Iterator<?> iterator = collEmp.iterator(); iterator.hasNext();) {
					Object itemId4 = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbReview.getItem(itemId4);
					EmployeeDM editEmployee = (EmployeeDM) item.getBean();
					if (editEmployee != null && editEmployee.getEmployeeid().equals(editEmployee.getEmployeeid())) {
						cbReview.setValue(itemId4);
						break;
					} else {
						cbReview.setValue(null);
					}
				}
				cbEmployee.setValue(campaignDM.getCampaignOwner());
				Collection<?> actids = cbAction.getItemIds();
				for (Iterator<?> iterator = actids.iterator(); iterator.hasNext();) {
					Object itemId4 = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbAction.getItem(itemId4);
					EmployeeDM editEmployee = (EmployeeDM) item.getBean();
					if (editEmployee != null && editEmployee.getEmployeeid().equals(editEmployee.getEmployeeid())) {
						cbAction.setValue(itemId4);
						break;
					} else {
						cbAction.setValue(null);
					}
				}
				cbAction.setValue(campaignDM.getActionedby());
				cbReview.setValue(campaignDM.getReviewdby());
				if (campaignDM.getTargetAudience() != null && !"null".equals(campaignDM.getTargetAudience())) {
					tfTagetAudieance.setValue(campaignDM.getTargetAudience());
				}
				if (campaignDM.getTargetSize() != null && !"null".equals(campaignDM.getTargetSize())) {
					tfTargetSize.setValue(campaignDM.getTargetSize().toString());
				}
				if (campaignDM.getComments() != null && !"null".equals(campaignDM.getComments())) {
					taComments.setValue(campaignDM.getComments());
				}
				cbStatus.setValue(campaignDM.getStatus());
				if (cbStatus.getValue().equals("Approved") || cbStatus.getValue().equals("Closed")) {
					hluserInputReadonlyTrue();
				}
				comment = new Comments(vlCommetTblLayout, employeeid, null, null, null, null, campaingnId, null);
				document = new Documents(vlDocumentLayout, null, null, null, null, campaingnId, null);
				comment.loadsrch(true, null, null, campaingnId, null, null, null);
				document.loadsrcrslt(true, null, null, campaingnId, null, null, null);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
			List<CurrencyDM> getCurrencylist = serviceCurrency.getCurrencyList(getCcyid, null, null, "Active", "F");
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
			logger.info(e.getMessage());
		}
	}
	
	private void loadProductList() {
		try {
			BeanItemContainer<ProductDM> beanproduct = new BeanItemContainer<ProductDM>(ProductDM.class);
			beanproduct.addAll(serviceProduct
					.getProductList(companyId, null, null, prodName, "Active", null, null, "P"));
			cbProduct.setContainerDataSource(beanproduct);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/**
	 * this method used to load the employee list based on company id and status
	 */
	private void loadEmployeeList() {
		try {
			BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyId, null, null,
					null, null, "P"));
			cbAction.setContainerDataSource(beanEmployee);
			cbEmployee.setContainerDataSource(beanEmployee);
			cbReview.setContainerDataSource(beanEmployee);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadCampaignTypeByLookUpList() {
		try {
			BeanContainer<String, CompanyLookupDM> beanCompLookUp = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanCompLookUp.setBeanIdProperty("lookupname");
			beanCompLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyId, moduleId, "Active",
					"CM_CAMPTYE"));
			cbCampaignType.setContainerDataSource(beanCompLookUp);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		cbStatus.setValue(null);
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
		if ((tfCampaignName.getValue() == null) || tfCampaignName.getValue().trim().length() == 0) {
			tfCampaignName.setComponentError(new UserError(GERPErrorCodes.NULL_CAMPAIGN_NAME));
			errorflag = true;
		} else {
			tfCampaignName.setComponentError(null);
		}
		if (dfCampaignStart.getValue() == null) {
			dfCampaignStart.setComponentError(new UserError(GERPErrorCodes.NULL_CAMPAIGN_DATE));
			errorflag = true;
		} else {
			dfCampaignStart.setComponentError(null);
		}
		if ((dfCampaignEnd.getValue() != null) || (dfCampaignEnd.getValue() != null)) {
			if (dfCampaignStart.getValue().after(dfCampaignEnd.getValue())) {
				dfCampaignEnd.setComponentError(new UserError(GERPErrorCodes.CRM_DATE_OUTOFRANGE));
				errorflag = true;
			}
		} else {
			dfCampaignEnd.setComponentError(null);
		}
		if (cbCampaignType.getValue() == null) {
			cbCampaignType.setComponentError(new UserError(GERPErrorCodes.NULL_CAMPAIGN_TYPE));
			errorflag = true;
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyId + " | User Name :  "
					+ "Throwing ValidationException. User data is > " + tfProduct.getValue() + tfCampaignName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("CampaignsaveDetails------>");
			CampaignDM campaignDM = new CampaignDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				campaignDM = beanCampaign.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (cbEmployee.getValue() != null) {
				campaignDM.setCampaignOwner((Long) cbEmployee.getValue());
			}
			campaignDM.setCampaignStartDate(dfCampaignStart.getValue());
			campaignDM.setCampaignEndDate(dfCampaignEnd.getValue());
			if (cbCurrencyName.getValue() != null) {
				campaignDM.setCcyid((Long) cbCurrencyName.getValue());
			}
			if (tfTargetSize.getValue() != null && tfTargetSize.getValue().trim().length() > 0) {
				campaignDM.setTargetSize(Long.valueOf(tfTargetSize.getValue().toString()));
			}
			campaignDM.setComments(taComments.getValue());
			if (tfActualBudget.getValue() != null && tfActualBudget.getValue().trim().length() > 0) {
				campaignDM.setActulBudget(Long.valueOf(tfActualBudget.getValue()));
			}
			if (tfActResponseCount.getValue() != null && tfActResponseCount.getValue().trim().length() > 0) {
				campaignDM.setActulRespCount(Long.valueOf(tfActResponseCount.getValue()));
			}
			if (tfAcualRev.getValue() != null && tfAcualRev.getValue().trim().length() > 0) {
				campaignDM.setActulRevenue(Long.valueOf(tfAcualRev.getValue()));
			}
			if (tfActRoi.getValue() != null && tfActRoi.getValue().trim().length() > 0) {
				campaignDM.setActulRoi(Long.valueOf(tfActRoi.getValue().toString()));
			}
			if (tfActSalesCount.getValue() != null && tfActSalesCount.getValue().trim().length() > 0) {
				campaignDM.setActulSalesCount(Long.valueOf(tfActSalesCount.getValue()));
			}
			campaignDM.setCampaignType(cbCampaignType.getValue().toString());
			campaignDM.setComments(taComments.getValue());
			if (tfExptdBudget.getValue() != null && tfExptdBudget.getValue().trim().length() > 0) {
				campaignDM.setExptdBudget(Long.valueOf(tfExptdBudget.getValue()));
			}
			if (tfExpResepCount.getValue() != null && tfExpResepCount.getValue().trim().length() > 0) {
				campaignDM.setExptdRespCount(Long.valueOf(tfExpResepCount.getValue()));
			}
			if (tfExpRevenue.getValue() != null && tfExpRevenue.getValue().trim().length() > 0) {
				campaignDM.setExptdRevenue(Long.valueOf(tfExpRevenue.getValue()));
			}
			if (tfExpRoi.getValue() != null && tfExpRoi.getValue().trim().length() > 0) {
				campaignDM.setExptdRoi(Long.valueOf(tfExpRoi.getValue()));
			}
			if (tfExpSalesCount.getValue() != null && tfExpSalesCount.getValue().trim().length() > 0) {
				campaignDM.setExptdSalesCount(Long.valueOf(tfExpSalesCount.getValue()));
			}
			if (cbProduct.getValue() != null) {
				campaignDM.setProductId(((ProductDM) cbProduct.getValue()).getProdid());
			}
			campaignDM.setProductName(tfProduct.getValue());
			if (cbStatus.getValue() != null) {
				campaignDM.setStatus(cbStatus.getValue().toString());
			}
			campaignDM.setCampaignname(tfCampaignName.getValue());
			campaignDM.setCompanyId(companyId);
			campaignDM.setLastUpdatedBy(userName);
			campaignDM.setLastUpdatedDt(DateUtils.getcurrentdate());
			if (tfTagetAudieance.getValue().toString().trim().length() > 0) {
				campaignDM.setTargetAudience(tfTagetAudieance.getValue());
			}
			if (tfTargetSize.getValue().toString().trim().length() > 0) {
				campaignDM.setTargetSize(Long.valueOf(tfTargetSize.getValue()));
			}
			campaignDM.setPreparedby(employeeid);
			if (cbAction != null) {
				campaignDM.setActionedby((Long) cbAction.getValue());
			}
			if (cbReview.getValue() != null) {
				campaignDM.setReviewdby((Long) cbReview.getValue());
			}
			serviceCampaign.saveOrUpdateCampaignDetails(campaignDM);
			loadSrchRslt();
			comment.savecampaign(campaignDM.getCampaingnId());
			comment.resetfields();
			document.savecampaign(campaignDM.getCampaingnId());
			document.ResetFields();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		tfCampaignName.setRequired(false);
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
		tfCampaignName.setValue("");
		tfCampaignName.setComponentError(null);
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
		cbReview.setValue(null);
		cbAction.setValue(null);
		cbEmployee.setValue(null);
		cbProduct.setValue(null);
		cbProduct.setComponentError(null);
		dfCampaignStart.setValue(null);
		dfCampaignEnd.setValue(null);
		searchCampaignDt.setValue(null);
		cbCurrencyName.setValue(cbCurrencyName.getItemIds().iterator().next());
		cbCampaignType.setValue(null);
		cbCampaignType.setComponentError(null);
		cbStatus.setValue(null);
		tfTagetAudieance.setValue("");
		tfTargetSize.setValue("");
	}
}
