/**
 * File Name	:	AssetDetails.java
 * Description	:	To Handle asset brand details for assets.
 * Author		:	Priyanga M
 * Date			:	March 06, 2014
 * Modification :   UI code optimization
 * Modified By  :   Nandhakumar.S
 * Description	:   Optimizing the code for asset brand UI 
 *
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1			16-Jun-2014		Nandhakumar.S			code refractment
 * 0.2          31-JULY-2014    MOHAMED					Code Modify
 *0.3			05-AUG-2014     MOHAMED                 Code Modified
 */
package com.gnts.asm.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.mst.AssetBrandDM;
import com.gnts.asm.domain.mst.AssetCategoryDM;
import com.gnts.asm.domain.txn.AssetDetailsDM;
import com.gnts.asm.service.mst.AssetBrandService;
import com.gnts.asm.service.mst.AssetCategoryService;
import com.gnts.asm.service.txn.AssetDetailsService;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
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
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AssetDetails extends BaseTransUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Declaration for search panel Components
	private Button btnSearch, btnReset;
	private TabSheet tabSheet;
	// Declaration for add and edit panel Components
	private TextField tfAssetName, tfCategoryId, tfSerialNo, tfMfgSerialNo, tfAssetLocation, tfPurchaseValue,
			tfservicerequire, tfInvoiceNo, tfSalvageValue, tfLifeInYears, tfLifeInMonths, tfLicenseInfo;
	private PopupDateField dtPurchaseDate, dtWarrentyDate, dtLastServiceDate, dtNextServiceDate;
	private ComboBox cbAssetStatus, cbAssetType, cbBrandId, cbDeptId, cbbranch, cbcategory, cbaction, cbreview;
	private Button btnSave, btnCancel, btnHome;
	private TextArea tfAssetDetailDesc, tfRemarks, tawarrentdesc;
	// Declaration for button
	private Button btnEdit;
	// VerticalLayout vlAssetDetails = new VerticalLayout();
	private HorizontalLayout hlsavecancel = new HorizontalLayout();
	private VerticalLayout vlAssetSpec = new VerticalLayout();
	private VerticalLayout vlOwnDetails = new VerticalLayout();
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	private String username;
	private BeanItemContainer<AssetDetailsDM> beanAssetdetail = null;
	private AssetDetailsService serviceAssetDetail = (com.gnts.asm.service.txn.AssetDetailsService) SpringContextHelper
			.getBean("assetDetails");
	private AssetBrandService serviceBrand = (AssetBrandService) SpringContextHelper.getBean("assetBrand");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private DepartmentService servicedepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	private AssetCategoryService serviceAsset = (AssetCategoryService) SpringContextHelper.getBean("assetCategory");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private int recordCnt;
	private Long companyid, employeeid;
	private HorizontalLayout hlSearchLayout;
	// parent HorizontalLayout for input control
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlInput = new HorizontalLayout();
	private Long assetId, assetOwnId;
	private Logger logger = Logger.getLogger(AssetDetails.class);
	private AssetSpec spec;
	private AssetOwnDetails owndetails;
	
	public AssetDetails() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		employeeid = (Long) (UI.getCurrent().getSession().getAttribute("employeeid"));
		buildView(hlInput);
	}
	
	private void buildView(HorizontalLayout hlInput) {
		// Initialization for tfSearchAssetDetailsDesc
		tfAssetDetailDesc = new GERPTextArea("Asset Details");
		tfservicerequire = new GERPTextField("ServiceRequired");
		// Initialization for cbSearchAssetStatus
		cbAssetStatus = new GERPComboBox("Status", BASEConstants.T_AMS_ASSET_DETAILS, BASEConstants.ASSETSTATUS);
		cbAssetStatus.setItemCaptionPropertyId("desc");
		cbAssetStatus.setWidth("140px");
		// category
		cbcategory = new GERPComboBox("Category");
		cbcategory.setItemCaptionPropertyId("catgryName");
		loadcategory();
		// Initialization for btnSearch
		btnSearch = new Button("Search", this);
		btnSearch.setStyleName("searchbt");
		// Initialization for btnReset
		btnReset = new Button("Reset", this);
		btnReset.setStyleName("resetbt");
		cbaction = new GERPComboBox("Reviewed By");
		cbreview = new GERPComboBox("Action By");
		cbaction.setItemCaptionPropertyId("firstname");
		cbreview.setItemCaptionPropertyId("firstname");
		loadEmployee();
		// Initialization for btnEdit
		btnEdit = new Button("Edit", this);
		btnEdit.setStyleName("editbt");
		btnEdit.setEnabled(false);
		// Initialization for btnHome
		btnHome = new Button("Home", this);
		btnHome.setStyleName("homebtn");
		btnHome.setEnabled(false);
		// Initialization for tfAssetName
		tfAssetName = new GERPTextField("Asset Name");
		tfAssetName.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfAssetName.setComponentError(null);
				if (tfAssetName.getValue() != null) {
					tfAssetName.setComponentError(null);
				}
			}
		});
		// Initialization for tfCategoryId
		tfCategoryId = new GERPTextField("Category Id");
		tfCategoryId.setValue("0");
		tfCategoryId.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfCategoryId.setComponentError(null);
				if (tfCategoryId.getValue() != null) {
					tfCategoryId.setComponentError(null);
				}
			}
		});
		// Initialization for tfAssetTypes
		cbAssetType = new GERPComboBox("Asset Type", BASEConstants.T_AMS_ASSET_DETAILS, BASEConstants.ASSET_TYPE);
		cbAssetType.setWidth("148");
		cbAssetType.setRequired(true);
		cbAssetType.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbAssetType.setComponentError(null);
				if (cbAssetType.getValue() != null) {
					cbAssetType.setComponentError(null);
				}
			}
		});
		// Used to Load Brand Name in Combo box
		cbBrandId = new GERPComboBox("Brand Name");
		cbBrandId.setWidth("148");
		cbBrandId.setNullSelectionAllowed(false);
		cbBrandId.setItemCaptionPropertyId("brandname");
		loadbrandDetails();
		cbBrandId.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbBrandId.setComponentError(null);
				if (cbBrandId.getValue() != null) {
					cbBrandId.setComponentError(null);
				}
			}
		});
		// Used to Load Region Name in Combo box
		cbDeptId = new GERPComboBox("Department Name");
		cbDeptId.setWidth("148");
		cbDeptId.setNullSelectionAllowed(false);
		cbDeptId.setItemCaptionPropertyId("deptname");
		loadDepartment();
		cbDeptId.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbDeptId.setComponentError(null);
				if (cbDeptId.getValue() != null) {
					cbDeptId.setComponentError(null);
				}
			}
		});
		//
		cbbranch = new GERPComboBox("Branch");
		cbbranch.setWidth("148");
		cbbranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		tfSerialNo = new GERPTextField("Serial No.");
		tfMfgSerialNo = new GERPTextField("MFG.Serial No.");
		tfAssetLocation = new GERPTextField("Asset Location");
		// Initialization for dtPurchaseDate
		dtPurchaseDate = new GERPPopupDateField("Purchase Date");
		dtPurchaseDate.setDateFormat("dd-MMM-yyyy");
		// Initialization for tfPurchaseValue
		tfPurchaseValue = new GERPTextField("Purchase Value");
		tfPurchaseValue.setValue("0");
		// Initialization for tfInvoiceNo
		tfInvoiceNo = new GERPTextField("Invoice No.");
		// Initialization for dtWarrentyDate
		dtWarrentyDate = new GERPPopupDateField("Warrenty End Date");
		dtWarrentyDate.setDateFormat("dd-MMM-yyyy");
		// Initialization for dtLastServiceDate
		dtLastServiceDate = new GERPPopupDateField("Last Service Date");
		dtLastServiceDate.setDateFormat("dd-MMM-yyyy");
		// Initialization for dtNextServiceDate
		dtNextServiceDate = new GERPPopupDateField("Next Service Date");
		dtNextServiceDate.setDateFormat("dd-MMM-yyyy");
		// Initialization for tfSalvageValue
		tfSalvageValue = new GERPTextField("Salvage Value");
		tfSalvageValue.setValue("0");
		// Initialization for tfLifeInYears
		tfLifeInYears = new GERPTextField("Life In Years");
		tfLifeInYears.setWidth("148");
		tfLifeInYears.setValue("0");
		// Initialization for tfLifeInMonths
		tfLifeInMonths = new GERPTextField("Life In Months");
		tfLifeInMonths.setWidth("148");
		tfLifeInMonths.setValue("0");
		// Initialization for tfLicenseInfo
		tfLicenseInfo = new GERPTextField("License Info. ");
		tfLicenseInfo.setWidth("148");
		// Initialization for tfRemarks
		tfRemarks = new GERPTextArea("Remarks");
		tfRemarks.setWidth("148");
		tawarrentdesc = new GERPTextArea("Warrenty Description");
		tawarrentdesc.setWidth("148");
		tabSheet = new TabSheet();
		vlAssetSpec = new VerticalLayout();
		vlOwnDetails = new VerticalLayout();
		// Initialization for btnSave
		btnSave = new Button("Save", this);
		btnSave.setStyleName("savebt");
		// Initialization for btnCancel
		btnCancel = new Button("Cancel", this);
		btnCancel.setStyleName("cancelbt");
		hlsavecancel = new HorizontalLayout();
		hlsavecancel.addComponent(btnSave);
		hlsavecancel.addComponent(btnCancel);
		hlsavecancel.setVisible(false);
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(tfAssetName);
		flColumn2.addComponent(cbBrandId);
		flColumn3.addComponent(cbDeptId);
		flColumn4.addComponent(cbAssetStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// add the form layouts into user input layout
		hlUserInputLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn4 = new FormLayout();
		flColumn1.addComponent(tfAssetName);
		tfAssetName.setRequired(true);
		flColumn1.addComponent(cbAssetType);
		cbAssetType.setRequired(true);
		flColumn1.addComponent(cbbranch);
		flColumn1.addComponent(tfAssetDetailDesc);
		flColumn1.addComponent(cbBrandId);
		cbBrandId.setRequired(true);
		flColumn1.addComponent(cbcategory);
		flColumn1.setSpacing(true);
		flColumn1.setMargin(true);
		cbcategory.setRequired(true);
		flColumn2.addComponent(cbDeptId);
		cbDeptId.setRequired(true);
		flColumn2.addComponent(tfAssetLocation);
		flColumn2.addComponent(tfSerialNo);
		flColumn2.addComponent(tfMfgSerialNo);
		flColumn2.addComponent(tfInvoiceNo);
		flColumn2.addComponent(dtPurchaseDate);
		flColumn2.addComponent(tfPurchaseValue);
		flColumn2.addComponent(tfSalvageValue);
		flColumn2.setSpacing(true);
		flColumn2.setMargin(true);
		flColumn3.addComponent(tfservicerequire);
		flColumn3.addComponent(dtLastServiceDate);
		flColumn3.addComponent(dtNextServiceDate);
		flColumn3.addComponent(dtWarrentyDate);
		flColumn3.addComponent(tawarrentdesc);
		flColumn3.addComponent(tfLicenseInfo);
		flColumn4.addComponent(tfLifeInYears);
		flColumn4.addComponent(tfLifeInMonths);
		flColumn4.addComponent(cbreview);
		flColumn4.addComponent(cbaction);
		flColumn4.addComponent(tfRemarks);
		flColumn4.addComponent(cbAssetStatus);
		HorizontalLayout hlInput = new HorizontalLayout();
		VerticalLayout vlUserInput = new VerticalLayout();
		// hlInput.setWidth("1150");
		hlInput.addComponent(flColumn1);
		hlInput.addComponent(flColumn2);
		hlInput.addComponent(flColumn3);
		hlInput.addComponent(flColumn4);
		hlInput.setSpacing(true);
		hlInput.setMargin(true);
		vlUserInput.addComponent(GERPPanelGenerator.createPanel(hlInput));
		tabSheet.addTab(vlAssetSpec, "Asset Spec");
		tabSheet.addTab(vlOwnDetails, "Asset Own Details");
		tabSheet.setSizeFull();
		vlUserInput.addComponent(tabSheet);
		vlUserInput.setWidth("1180");
		hlUserInputLayout.addComponent(vlUserInput);
		hlUserInputLayout.setSizeFull();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
		// build search layout
	}
	
	// Load Branch list for pnlmain's combo Box
	private void loadbrandDetails() {
		BeanContainer<Long, AssetBrandDM> beanbrand = new BeanContainer<Long, AssetBrandDM>(AssetBrandDM.class);
		beanbrand.setBeanIdProperty("brandid");
		beanbrand.addAll(serviceBrand.getAssetBrandList(companyid, null, "Active", "P"));
		cbBrandId.setContainerDataSource(beanbrand);
	}
	
	// Load Department list for pnladdedit's combo Box
	private void loadDepartment() {
		BeanContainer<Long, DepartmentDM> beandept = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beandept.setBeanIdProperty("deptid");
		beandept.addAll(servicedepartmant.getDepartmentList(companyid, null, "Active", "P"));
		cbDeptId.setContainerDataSource(beandept);
	}
	
	// Load Branch List
	private void loadBranchList() {
		try {
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(serviceBranch.getBranchList(null, null, null, "Active", companyid, "P"));
			cbbranch.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadcategory() {
		BeanContainer<Long, AssetCategoryDM> assetCategorydm = new BeanContainer<Long, AssetCategoryDM>(
				AssetCategoryDM.class);
		assetCategorydm.setBeanIdProperty("catgryId");
		assetCategorydm.addAll(serviceAsset.getAssetCategoryList(companyid, null, "Active", "P"));
		cbcategory.setContainerDataSource(assetCategorydm);
	}
	
	private void loadEmployee() {
		List<EmployeeDM> emplist = new ArrayList<EmployeeDM>();
		emplist.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null, null, null,
				"P"));
		BeanContainer<Long, EmployeeDM> beanemployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanemployee.setBeanIdProperty("employeeid");
		beanemployee.addAll(emplist);
		cbaction.setContainerDataSource(beanemployee);
		beanemployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanemployee.setBeanIdProperty("employeeid");
		beanemployee.addAll(emplist);
		cbreview.setContainerDataSource(beanemployee);
	}
	
	public void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<AssetDetailsDM> asstdetailList = new ArrayList<AssetDetailsDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfAssetName.getValue() + ",");
			asstdetailList = serviceAssetDetail.getAssetDetailList(companyid, null, tfAssetName.getValue(),
					(Long) cbBrandId.getValue(), (Long) cbDeptId.getValue(), null,((String) cbAssetStatus.getValue()));
			recordCnt = asstdetailList.size();
			beanAssetdetail = new BeanItemContainer<AssetDetailsDM>(AssetDetailsDM.class);
			beanAssetdetail.addAll(asstdetailList);
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.setContainerDataSource(beanAssetdetail);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "assetId", "assetName", "brandname", "deptname",
					"assetdetails", "assetstatus", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Asset Name", "Brand Name", "Department Name",
					"Asset Details", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editAssetDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			AssetDetailsDM assetDetailsDM = beanAssetdetail.getItem(tblMstScrSrchRslt.getValue()).getBean();
			assetId = assetDetailsDM.getAssetId();
			tfAssetName.setValue(assetDetailsDM.getAssetName());
			cbAssetType.setValue(assetDetailsDM.getAssetType());
			cbBrandId.setValue(assetDetailsDM.getBrandId());
			cbDeptId.setValue(assetDetailsDM.getDeptId());
			cbbranch.setValue(assetDetailsDM.getBranchId());
			if (assetDetailsDM.getAssetdetails() != null && !"null".equals(assetDetailsDM.getAssetdetails())) {
				tfAssetDetailDesc.setValue(assetDetailsDM.getAssetdetails());
			}
			tfSerialNo.setValue(assetDetailsDM.getSerialno());
			tfMfgSerialNo.setValue(assetDetailsDM.getMfgserialno());
			tfAssetLocation.setValue(assetDetailsDM.getAssetlocation());
			if (tfPurchaseValue.getValue() != null) {
				tfPurchaseValue.setValue(assetDetailsDM.getPurchasevalue().toString());
			}
			dtPurchaseDate.setValue(assetDetailsDM.getPurchasedate());
			dtLastServiceDate.setValue(assetDetailsDM.getLastservicedt());
			dtNextServiceDate.setValue(assetDetailsDM.getNextservicedt());
			dtWarrentyDate.setValue(assetDetailsDM.getWarrentyenddt());
			tfSalvageValue.setValue(assetDetailsDM.getSalvagevalue().toString());
			cbreview.setValue(assetDetailsDM.getActionedby());
			cbaction.setValue(assetDetailsDM.getReviewedby());
			tfLifeInYears.setValue(assetDetailsDM.getLifeinyears().toString());
			tfLifeInMonths.setValue(assetDetailsDM.getLifeinmonths().toString());
			tfLicenseInfo.setValue(assetDetailsDM.getLicenseinfo());
			tfInvoiceNo.setValue(assetDetailsDM.getInvoiceno());
			if (assetDetailsDM.getRemarks() != null && !"null".equals(assetDetailsDM.getRemarks())) {
				tfRemarks.setValue(assetDetailsDM.getRemarks());
			}
			if (assetDetailsDM.getWarrentydesc() != null && !"null".equals(assetDetailsDM.getWarrentydesc())) {
				tawarrentdesc.setValue(assetDetailsDM.getWarrentydesc());
			}
			cbcategory.setValue(assetDetailsDM.getCatgryId());
			tfservicerequire.setValue(assetDetailsDM.getServicereqd());
			cbAssetStatus.setValue(assetDetailsDM.getAssetstatus());
		}
		spec.loadSrchRslt(true, assetId);
		owndetails.loadSrchRslt(true, assetId);
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
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		// reload the search using the defaults
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		assembleUserInputLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		hlUserIPContainer.addComponent((hlUserInputLayout));
		tfAssetName.setRequired(true);
		spec = new AssetSpec(vlAssetSpec, null, assetId);
		owndetails = new AssetOwnDetails(vlOwnDetails, assetOwnId);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	@Override
	protected void editDetails() {
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		// reset the input controls to default value
		hlUserIPContainer.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		spec = new AssetSpec(vlAssetSpec, null, assetId);
		owndetails = new AssetOwnDetails(vlOwnDetails, assetOwnId);
		editAssetDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean errorflag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (tfAssetName.getValue() == null || tfAssetName.getValue().trim().length() == 0) {
			tfAssetName.setComponentError(new UserError(GERPErrorCodes.NULL_ASSET_NAME));
			errorflag = true;
		} else {
			tfAssetName.setComponentError(null);
		}
		if ((cbAssetType.getValue() == null)) {
			cbAssetType.setComponentError(new UserError(GERPErrorCodes.NULL_ASSET_TYPE));
			errorflag = true;
		} else {
			cbAssetType.setComponentError(null);
		}
		if ((tfAssetLocation.getValue() == null || tfAssetLocation.getValue().trim().length() == 0)) {
			tfAssetLocation.setComponentError(new UserError(GERPErrorCodes.NULL_ASSET_LOCATION));
			errorflag = true;
		} else {
			tfAssetLocation.setComponentError(null);
		}
		if ((cbBrandId.getValue() == null)) {
			cbBrandId.setComponentError(new UserError(GERPErrorCodes.NULL_ASST_BRAND_NAME));
			errorflag = true;
		} else {
			cbBrandId.setComponentError(null);
		}
		if ((cbcategory.getValue() == null)) {
			cbcategory.setComponentError(new UserError(GERPErrorCodes.NULL_CATGRY_NAME));
			errorflag = true;
		} else {
			cbcategory.setComponentError(null);
		}
		if ((cbDeptId.getValue() == null)) {
			cbDeptId.setComponentError(new UserError(GERPErrorCodes.NULL_DEPT_NAME));
			errorflag = true;
		} else {
			cbDeptId.setComponentError(null);
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfAssetName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			AssetDetailsDM assetDetailsDM = new AssetDetailsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				assetDetailsDM = beanAssetdetail.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			assetDetailsDM.setAssetName(tfAssetName.getValue());
			assetDetailsDM.setAssetdetails(tfAssetDetailDesc.getValue());
			assetDetailsDM.setAssetType(cbAssetType.getValue().toString());
			if (cbBrandId.getValue() != null) {
				assetDetailsDM.setBrandId(Long.valueOf(cbBrandId.getValue().toString()));
			}
			if (cbbranch.getValue() != null) {
				assetDetailsDM.setBranchId(Long.valueOf(cbbranch.getValue().toString()));
			}
			if (cbDeptId.getValue() != null) {
				assetDetailsDM.setDeptId(Long.valueOf(cbDeptId.getValue().toString()));
			}
			assetDetailsDM.setCatgryId((String)cbcategory.getValue());
			assetDetailsDM.setWarrentydesc(tawarrentdesc.getValue());
			assetDetailsDM.setServicereqd(tfservicerequire.getValue());
			assetDetailsDM.setSerialno(tfSerialNo.getValue());
			assetDetailsDM.setMfgserialno(tfMfgSerialNo.getValue());
			assetDetailsDM.setAssetlocation(tfAssetLocation.getValue());
			assetDetailsDM.setPurchasedate(dtPurchaseDate.getValue());
			assetDetailsDM.setPurchasevalue(Long.valueOf(tfPurchaseValue.getValue()));
			assetDetailsDM.setInvoiceno(tfInvoiceNo.getValue());
			assetDetailsDM.setWarrentyenddt(dtWarrentyDate.getValue());
			try {
				if (tfSalvageValue.getValue() != null && tfSalvageValue.getValue().trim().length() > 0) {
					assetDetailsDM.setSalvagevalue(new Long(tfSalvageValue.getValue()));
				}
				if (tfLifeInYears.getValue() != null && tfLifeInYears.getValue().trim().length() > 0) {
					assetDetailsDM.setLifeinyears(new Long(tfLifeInYears.getValue()));
				}
				if (tfLifeInMonths.getValue() != null && tfLifeInMonths.getValue().trim().length() > 0) {
					assetDetailsDM.setLifeinmonths(new Long(tfLifeInMonths.getValue()));
				}
				if (tfLicenseInfo.getValue() != null && tfLicenseInfo.getValue().trim().length() > 0) {
					assetDetailsDM.setLicenseinfo(tfLicenseInfo.getValue());
				}
				assetDetailsDM.setRemarks(tfRemarks.getValue());
				assetDetailsDM.setActionedby((Long) cbaction.getValue());
				assetDetailsDM.setPreparedby(employeeid);
				assetDetailsDM.setReviewedby((Long) cbreview.getValue());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			assetDetailsDM.setLastservicedt(dtLastServiceDate.getValue());
			assetDetailsDM.setNextservicedt(dtNextServiceDate.getValue());
			assetDetailsDM.setAssetstatus((String) cbAssetStatus.getValue());
			assetDetailsDM.setCompanyid(companyid);
			assetDetailsDM.setLastupdateddt(DateUtils.getcurrentdate());
			assetDetailsDM.setLastupdatedby(username);
			serviceAssetDetail.saveAndUpdateAssetDetails(assetDetailsDM);
			spec.saveAssetSpec(assetDetailsDM.getAssetId());
			spec.resetFields();
			owndetails.saveAssetOwners(assetDetailsDM.getAssetId());
			owndetails.resetfields();
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Client Case ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_AMS_ASSET_DETAILS);
		UI.getCurrent().getSession().setAttribute("audittablepk", "");
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tfAssetName.setRequired(false);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
		btnSave.setComponentError(null);
		tfAssetName.setValue("");
		tfAssetName.setComponentError(null);
		tfAssetDetailDesc.setValue("");
		tfSerialNo.setValue("");
		tfMfgSerialNo.setValue("");
		tfAssetLocation.setValue("");
		tfAssetLocation.setComponentError(null);
		dtPurchaseDate.setValue(null);
		tfPurchaseValue.setValue("0");
		tfInvoiceNo.setValue("");
		dtWarrentyDate.setValue(null);
		tfSalvageValue.setValue("0");
		tfLifeInYears.setValue("0");
		tfLifeInMonths.setValue("0");
		tfLicenseInfo.setValue("");
		tfRemarks.setValue("");
		cbaction.setValue(null);
		cbreview.setValue(null);
		tawarrentdesc.setValue("");
		cbAssetStatus.setValue(cbAssetStatus.getItemIds().iterator().next());
		cbAssetType.setValue(null);
		cbAssetType.setComponentError(null);
		cbBrandId.setValue(null);
		cbBrandId.setRequired(false);
		cbBrandId.setComponentError(null);
		// cbbranch.setValue(branchId);
		cbbranch.setValue(cbbranch.getItemIds().iterator().next());
		cbDeptId.setValue(null);
		cbDeptId.setRequired(false);
		cbDeptId.setComponentError(null);
		dtLastServiceDate.setValue(null);
		dtNextServiceDate.setValue(null);
		btnSave.setCaption("Save");
		cbcategory.setValue(null);
		cbcategory.setRequired(false);
		cbcategory.setComponentError(null);
		tfservicerequire.setValue("");
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
