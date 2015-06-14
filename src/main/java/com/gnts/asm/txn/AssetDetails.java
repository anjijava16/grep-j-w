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
import com.gnts.asm.domain.txn.AssetDetailsDM;
import com.gnts.asm.service.mst.AssetBrandService;
import com.gnts.asm.service.txn.AssetDetailsService;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.crm.domain.mst.ClientCategoryDM;
import com.gnts.crm.service.mst.ClientCategoryService;
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
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AssetDetails extends BaseUI {
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
	// Declaration for table panel
	private Table table;
	// VerticalLayout vlAssetDetails = new VerticalLayout();
	HorizontalLayout hlsavecancel = new HorizontalLayout();
	HorizontalLayout hlImage = new HorizontalLayout();
	private VerticalLayout vlAssetSpec = new VerticalLayout();
	private VerticalLayout vlOwnDetails = new VerticalLayout();
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	private String username;
	private BeanItemContainer<AssetDetailsDM> beanAssetdetail = null;
	public static boolean filevalue2 = false;
	public static boolean filevalue3 = false;
	AssetDetailsService serviceAssetDetail = (com.gnts.asm.service.txn.AssetDetailsService) SpringContextHelper
			.getBean("assetDetails");
	CompanyService companybean = (CompanyService) SpringContextHelper.getBean("companyBean");
	AssetBrandService ServiceBrand = (AssetBrandService) SpringContextHelper.getBean("assetBrand");
	BranchService ServiceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	DepartmentService Servicedepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	ClientCategoryService ServiceCategory = (ClientCategoryService) SpringContextHelper.getBean("clientCategory");
	EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private int recordCnt;
	private Long companyid, branchId, employeeid;
	HorizontalLayout hlTableTitleandCaptionLayout, notificationHl, Hlheaderandbtn, hlSearchLayout;
	// parent HorizontalLayout for input control
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlInput = new HorizontalLayout();
	private Long assetId, assetOwnId;
	private static Logger logger = Logger.getLogger(AssetDetails.class);
	AssetSpec spec;
	AssetOwnDetails owndetails;
	
	public AssetDetails() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
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
		cbcategory.setItemCaptionPropertyId("clientCatName");
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
		//cbbranch.setNullSelectionAllowed(false);
		cbbranch.setItemCaptionPropertyId("branchName");
//		loadBranchDetails();
		loadBranchList();
		// Initialization for tfSerialNo
		tfSerialNo = new GERPTextField("Serial No.");
		// Initialization for tfMfgSerialNo
		tfMfgSerialNo = new GERPTextField("MFG.Serial No.");
		// Initialization for tfAssetLocation
		tfAssetLocation = new GERPTextField("Asset Location");
		tfAssetLocation.setRequired(true);
		tfAssetLocation.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfAssetLocation.setComponentError(null);
				if (tfAssetLocation.getValue() != null) {
					tfAssetLocation.setComponentError(null);
				}
			}
		});
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
		flColumn1.addComponent(tfAssetLocation);
		tfAssetLocation.setRequired(true);
		flColumn1.addComponent(tfAssetDetailDesc);
		flColumn1.addComponent(cbBrandId);
		cbBrandId.setRequired(true);
		flColumn1.addComponent(cbcategory);
		flColumn1.setSpacing(true);
		flColumn1.setMargin(true);
		cbcategory.setRequired(true);
		flColumn2.addComponent(cbDeptId);
		cbDeptId.setRequired(true);
		flColumn2.addComponent(cbbranch);
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
		// tabSheet.addTab(hlInput, "Asset Details");
		tabSheet.addTab(vlAssetSpec, "Asset Spec");
		System.out.println("ASSET------>" + vlAssetSpec);
		tabSheet.addTab(vlOwnDetails, "Asset Own Details");
		System.out.println("ASSETOWNER------>" + vlOwnDetails);
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
		List<AssetBrandDM> getbrandlist = ServiceBrand.getAssetBrandList(companyid, null, "Active", "P");
		BeanContainer<Long, AssetBrandDM> beanbrand = new BeanContainer<Long, AssetBrandDM>(AssetBrandDM.class);
		beanbrand.setBeanIdProperty("brandid");
		beanbrand.addAll(getbrandlist);
		cbBrandId.setContainerDataSource(beanbrand);
	}
	
	// Load Department list for pnladdedit's combo Box
	private void loadDepartment() {
		List<DepartmentDM> list = new ArrayList<DepartmentDM>();
		list.addAll(Servicedepartmant.getDepartmentList(companyid, null, "Active", "P"));
		BeanContainer<Long, DepartmentDM> beandept = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beandept.setBeanIdProperty("deptid");
		beandept.addAll(list);
		cbDeptId.setContainerDataSource(beandept);
	}
	
	
	
	// Load Branch List
	public void loadBranchList() {
		try {
			List<BranchDM> branchList = ServiceBranch.getBranchList(null, null, null, "Active", companyid, "P");
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(branchList);
			cbbranch.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
//	// Load Branch list for combo Box
//	private void loadBranchDetails() {
//		List<BranchDM> list = new ArrayList<BranchDM>();
//		list.addAll(ServiceBranch.getBranchList(branchId, null, null, "Active", companyid, "P"));
//		BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
//		beanbranch.setBeanIdProperty("branchId");
//		beanbranch.addAll(list);
//		cbbranch.setContainerDataSource(beanbranch);
//	}
	
	private void loadcategory() {
		List<ClientCategoryDM> clientlist = new ArrayList<ClientCategoryDM>();
		clientlist.addAll(ServiceCategory.getCrmClientCategoryList(companyid, null, "Active", "P"));
		BeanContainer<Long, ClientCategoryDM> beanclientcat = new BeanContainer<Long, ClientCategoryDM>(
				ClientCategoryDM.class);
		beanclientcat.setBeanIdProperty("clientCategoryId");
		beanclientcat.addAll(clientlist);
		cbcategory.setContainerDataSource(beanclientcat);
	}
	
	private void loadEmployee() {
		List<EmployeeDM> emplist = new ArrayList<EmployeeDM>();
		emplist.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null, null, null,
				"P"));
		BeanContainer<Long, EmployeeDM> beanemployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanemployee.setBeanIdProperty("employeeid");
		beanemployee.addAll(emplist);
		cbaction.setContainerDataSource(beanemployee);
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
					(Long) cbBrandId.getValue(), (Long) cbDeptId.getValue(), ((String) cbAssetStatus.getValue()));
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
			tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				
				public void itemClick(ItemClickEvent event) {
					// try {
					// TODO Auto-generated method stub
					if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
						btnEdit.setEnabled(false);
						btnAdd.setEnabled(true);
					} else {
						btnEdit.setEnabled(true);
						btnAdd.setEnabled(false);
					}
					resetFields();
					btnSave.setCaption("Save");
					/*
					 * } catch (Exception e) { logger.info("fn_populateAndConfig->" + e); }
					 */
				}
			});
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
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		assetId = (Long) sltedRcd.getItemProperty("assetId").getValue();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Dept. Id -> "
				+ assetId);
		if (sltedRcd != null) {
			AssetDetailsDM editassetlist = beanAssetdetail.getItem(tblMstScrSrchRslt.getValue()).getBean();
			tfAssetName.setValue(editassetlist.getAssetName());
			cbAssetType.setValue(editassetlist.getAssetType());
			cbBrandId.setValue(editassetlist.getBrandId());
			cbDeptId.setValue(editassetlist.getDeptId());
			cbbranch.setValue(editassetlist.getBranchId());
			if (editassetlist.getAssetdetails() != null && !"null".equals(editassetlist.getAssetdetails())) {
				tfAssetDetailDesc.setValue(editassetlist.getAssetdetails());
			}
			tfSerialNo.setValue(editassetlist.getSerialno());
			tfMfgSerialNo.setValue(editassetlist.getMfgserialno());
			tfAssetLocation.setValue(editassetlist.getAssetlocation());
			if (tfPurchaseValue.getValue() != null) {
				tfPurchaseValue.setValue(editassetlist.getPurchasevalue().toString());
			}
			dtPurchaseDate.setValue(editassetlist.getPurchasedate());
			dtLastServiceDate.setValue(editassetlist.getLastservicedt());
			dtNextServiceDate.setValue(editassetlist.getNextservicedt());
			dtWarrentyDate.setValue(editassetlist.getWarrentyenddt());
			tfSalvageValue.setValue(editassetlist.getSalvagevalue().toString());
			cbreview.setValue(editassetlist.getActionedby());
			cbaction.setValue(editassetlist.getReviewedby());
			tfLifeInYears.setValue(editassetlist.getLifeinyears().toString());
			tfLifeInMonths.setValue(editassetlist.getLifeinmonths().toString());
			tfLicenseInfo.setValue(editassetlist.getLicenseinfo());
			tfInvoiceNo.setValue(editassetlist.getInvoiceno());
			if (editassetlist.getRemarks() != null && !"null".equals(editassetlist.getRemarks())) {
				tfRemarks.setValue(editassetlist.getRemarks());
			}
			if (editassetlist.getWarrentydesc() != null && !"null".equals(editassetlist.getWarrentydesc())) {
				tawarrentdesc.setValue(editassetlist.getWarrentydesc());
			}
			cbcategory.setValue(editassetlist.getCatgryId());
			tfservicerequire.setValue(editassetlist.getServicereqd());
			cbAssetStatus.setValue(editassetlist.getAssetstatus());
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
		// hlInput.removeAllComponents();
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
		hlUserIPContainer.addComponent/* (GERPPanelGenerator.createPanel */(hlUserInputLayout);
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
			AssetDetailsDM assetdtl = new AssetDetailsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				assetdtl = beanAssetdetail.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			assetdtl.setAssetName(tfAssetName.getValue());
			assetdtl.setAssetdetails(tfAssetDetailDesc.getValue());
			assetdtl.setAssetType(cbAssetType.getValue().toString());
			if (cbBrandId.getValue() != null) {
				assetdtl.setBrandId(Long.valueOf(cbBrandId.getValue().toString()));
			}
			if (cbbranch.getValue() != null) {
				assetdtl.setBranchId(Long.valueOf(cbbranch.getValue().toString()));
			}
			if (cbDeptId.getValue() != null) {
				assetdtl.setDeptId(Long.valueOf(cbDeptId.getValue().toString()));
			}
			assetdtl.setCatgryId((Long) cbcategory.getValue());
			assetdtl.setWarrentydesc(tawarrentdesc.getValue());
			assetdtl.setServicereqd(tfservicerequire.getValue());
			assetdtl.setSerialno(tfSerialNo.getValue());
			assetdtl.setMfgserialno(tfMfgSerialNo.getValue());
			assetdtl.setAssetlocation(tfAssetLocation.getValue());
			assetdtl.setPurchasedate(dtPurchaseDate.getValue());
			assetdtl.setPurchasevalue(Long.valueOf(tfPurchaseValue.getValue()));
			assetdtl.setInvoiceno(tfInvoiceNo.getValue());
			assetdtl.setWarrentyenddt(dtWarrentyDate.getValue());
			try {
				if (tfSalvageValue.getValue() != null && tfSalvageValue.getValue().trim().length() > 0) {
					assetdtl.setSalvagevalue(new Long(tfSalvageValue.getValue()));
				}
				if (tfLifeInYears.getValue() != null && tfLifeInYears.getValue().trim().length() > 0) {
					assetdtl.setLifeinyears(new Long(tfLifeInYears.getValue()));
				}
				if (tfLifeInMonths.getValue() != null && tfLifeInMonths.getValue().trim().length() > 0) {
					assetdtl.setLifeinmonths(new Long(tfLifeInMonths.getValue()));
				}
				if (tfLicenseInfo.getValue() != null && tfLicenseInfo.getValue().trim().length() > 0) {
					assetdtl.setLicenseinfo(tfLicenseInfo.getValue());
				}
				assetdtl.setRemarks(tfRemarks.getValue());
				assetdtl.setActionedby((Long) cbaction.getValue());
				assetdtl.setPreparedby(employeeid);
				assetdtl.setReviewedby((Long) cbreview.getValue());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			assetdtl.setLastservicedt(dtLastServiceDate.getValue());
			assetdtl.setNextservicedt(dtNextServiceDate.getValue());
			assetdtl.setAssetstatus((String) cbAssetStatus.getValue());
			assetdtl.setCompanyid(companyid);
			assetdtl.setLastupdateddt(DateUtils.getcurrentdate());
			assetdtl.setLastupdatedby(username);
			serviceAssetDetail.saveAndUpdateAssetDetails(assetdtl);
			spec.Assetsave(assetdtl.getAssetId());
			spec.resetFields();
			owndetails.Assetownersave(assetdtl.getAssetId());
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
		//cbbranch.setValue(branchId);
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
}
