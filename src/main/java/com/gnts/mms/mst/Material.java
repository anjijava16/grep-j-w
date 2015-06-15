/**
 * File Name	:	Material.java
 * Description	:	This Screen Purpose for Modify the Material Details.Add the Material Type details process should be directly added in DB.
 * Author		:	Mahaboob Subahan J
 * Date			:	Jul 15, 2014
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
 * 0.1          Jul 15, 2014   	Mahaboob Subahan J		Initial Version		
 * 
 */
package com.gnts.mms.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jboss.logging.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
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
import com.gnts.mms.domain.mst.MaterialConsumersDM;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.mst.MaterialOwnersDM;
import com.gnts.mms.domain.mst.MaterialSpecDM;
import com.gnts.mms.domain.mst.MaterialTypeDM;
import com.gnts.mms.service.mst.MaterialConsumersService;
import com.gnts.mms.service.mst.MaterialOwnersService;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.mst.MaterialSpecService;
import com.gnts.mms.service.mst.MaterialTypeService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Material extends BaseUI {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(Material.class);
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private DepartmentService serviceDepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	private MaterialTypeService serviceMaterialType = (MaterialTypeService) SpringContextHelper.getBean("materialType");
	private MaterialOwnersService serviceMaterialOwner = (MaterialOwnersService) SpringContextHelper
			.getBean("materialOwners");
	private MaterialConsumersService serviceMaterialConsumer = (MaterialConsumersService) SpringContextHelper
			.getBean("materialConsumer");
	private MaterialSpecService serviceMaterialSpec = (MaterialSpecService) SpringContextHelper.getBean("materialSpec");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private BeanItemContainer<MaterialDM> beanMaterial = null;
	private BeanItemContainer<MaterialOwnersDM> beanMaterialOwner = null;
	private BeanItemContainer<MaterialConsumersDM> beanMaterialConsumer = null;
	private BeanItemContainer<MaterialSpecDM> beanMaterialSpec = null;
	private BeanContainer<Long, BranchDM> beanBranch = null;
	private BeanContainer<Long, DepartmentDM> beanDepartment = null;
	private BeanContainer<Long, CompanyLookupDM> beanCompanyLookUp = null;
	private BeanContainer<Long, MaterialTypeDM> beanMaterialType = null;
	private List<MaterialOwnersDM> matOwnerList = new ArrayList<MaterialOwnersDM>();
	private List<MaterialConsumersDM> matConsList = new ArrayList<MaterialConsumersDM>();
	private List<MaterialSpecDM> matSpecList = new ArrayList<MaterialSpecDM>();
	// Material Component Declaration
	private TextField tfMaterialCode, tfMaterialName, tfPartCode, tfUnitRate, tfReorderLevel;
	private TextArea taVisualSpec, taRemark;
	private ComboBox cbMaterialType, cbMaterialGroup, cbBranch, cbDepartment, cbMaterialUOM;
	private ComboBox cbMaterialStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	// Material Consumer Components Declaration
	private ComboBox cbMatConsBranch, cbMatConsDepartment;
	private ComboBox cbMatConsStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private Table tblMatCons = new GERPTable();
	private Button btnaddMatCons = new GERPButton("Add", "addbt", this);
	private Button btndeletematcmr = new GERPButton("Delete", "delete", this);
	// Material Owner Components Declaration
	private ComboBox cbMatOwnerEmployee, cbMatOwnerBranch, cbMatOwnerDept;
	private ComboBox cbMatOwnerStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private Button btnaddMatOwner = new GERPButton("Add", "addbt", this);
	private Button btndeletematowner = new GERPButton("Delete", "delete", this);
	private Table tblMatOwner = new GERPTable();
	// Material Specification Components Declaration
	private TextField tfMatSpecName;
	private TextField taMatSpecDesc;
	private ComboBox cbMatSpecStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private Table tblMatSpec = new GERPTable();
	private Button btnaddMatSpec = new GERPButton("Add", "addbt", this);
	private Button btndeletespec = new GERPButton("Delete", "delete", this);
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private String userName;
	private Long companyId, moduleId, materialId, branchID;
	private int recordCnt = 0, recordCntMatOwner = 0, recordCntMatCons = 0, recordCntMatSpec = 0;
	
	// Constructor received the parameters from Login UI class
	public Material() {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = Long.valueOf(UI.getCurrent().getSession().getAttribute("moduleId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside Material() constructor");
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Painting Material UI");
		// Material Components Definition
		tfMaterialCode = new GERPTextField("Material Code");
		tfMaterialCode.setWidth("150");
		tfMaterialCode.setMaxLength(10);
		tfMaterialName = new GERPTextField("Material Name");
		tfMaterialName.setWidth("150");
		cbBranch = new GERPComboBox("Branch Name");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setWidth("150");
		cbBranch.setImmediate(true);
		cbBranch.setNullSelectionAllowed(false);
		tfPartCode = new GERPTextField("Part Code");
		tfPartCode.setWidth("150");
		cbMaterialUOM = new GERPComboBox("Material UOM");
		cbMaterialUOM.setItemCaptionPropertyId("lookupname");
		cbMaterialUOM.setWidth("150");
		tfUnitRate = new GERPTextField("Unit Rate");
		tfUnitRate.setWidth("150");
		tfReorderLevel = new GERPTextField("Reorder Level");
		tfReorderLevel.setWidth("150");
		taVisualSpec = new GERPTextArea("Visual Spec.");
		taVisualSpec.setWidth("150");
		taVisualSpec.setHeight("50");
		taRemark = new GERPTextArea("Remark");
		taRemark.setWidth("150");
		taRemark.setHeight("75");
		cbMaterialStatus.setWidth("150");
		cbMaterialType = new GERPComboBox("Material Type");
		cbMaterialType.setItemCaptionPropertyId("materialTypeName");
		cbMaterialType.setWidth("150");
		cbMaterialGroup = new GERPComboBox("Material Group");
		cbMaterialGroup.setItemCaptionPropertyId("lookupname");
		cbMaterialGroup.setWidth("150");
		cbDepartment = new GERPComboBox("Department");
		cbDepartment.setItemCaptionPropertyId("deptname");
		cbDepartment.setWidth("150");
		// Material Owner Components Definition
		cbMatOwnerEmployee = new GERPComboBox("Employee");
		cbMatOwnerEmployee.setItemCaptionPropertyId("firstname");
		cbMatOwnerEmployee.setWidth("150");
		cbMatOwnerBranch = new GERPComboBox("Branch");
		cbMatOwnerBranch.setItemCaptionPropertyId("branchName");
		cbMatOwnerBranch.setWidth("150");
		cbMatOwnerDept = new GERPComboBox("Department");
		cbMatOwnerDept.setItemCaptionPropertyId("deptname");
		cbMatOwnerDept.setWidth("150");
		btnaddMatOwner.setStyleName("add");
		btnaddMatOwner.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Material Owner
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validationmatowner()) {
					saveMatOwnerDetails();
				}
			}
		});
		btndeletematowner.setEnabled(false);
		tblMatOwner = new GERPTable();
		tblMatOwner.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMatOwner.isSelected(event.getItemId())) {
					tblMatOwner.setImmediate(true);
					btnaddMatOwner.setCaption("Add");
					btnaddMatOwner.setStyleName("savebt");
					btndeletematowner.setEnabled(false);
					matOwnerResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddMatOwner.setCaption("Update");
					btnaddMatOwner.setStyleName("savebt");
					btndeletematowner.setEnabled(true);
					editMaterialOwner();
				}
			}
		});
		btndeletematowner.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndeletematowner == event.getButton()) {
					deletematowner();
				}
			}
		});
		// ClickListener for Material Owner Tale
		// Material Consumer Components Definition
		cbMatConsBranch = new GERPComboBox("Branch");
		cbMatConsBranch.setItemCaptionPropertyId("branchName");
		cbMatConsBranch.setWidth("150");
		cbMatConsDepartment = new GERPComboBox("Department");
		cbMatConsDepartment.setItemCaptionPropertyId("deptname");
		cbMatConsDepartment.setWidth("150");
		btnaddMatCons.setStyleName("add");
		btnaddMatCons.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Material Consumer
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validationmatconsumer()) {
					saveMatConsDetails();
				}
			}
		});
		btndeletematcmr.setEnabled(false);
		tblMatCons = new GERPTable();
		// ClickListener for Material Consumer Tale
		tblMatCons.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMatCons.isSelected(event.getItemId())) {
					tblMatCons.setImmediate(true);
					btnaddMatCons.setCaption("Add");
					btnaddMatCons.setStyleName("savebt");
					btndeletematcmr.setEnabled(false);
					matConsResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddMatCons.setCaption("Update");
					btnaddMatCons.setStyleName("savebt");
					btndeletematcmr.setEnabled(true);
					editMaterialConsumer();
				}
			}
		});
		btndeletematcmr.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndeletematcmr == event.getButton()) {
					deletematconsumer();
				}
			}
		});
		// Material Specification Components Definition
		tfMatSpecName = new GERPTextField("Spec.Name");
		tfMatSpecName.setWidth("150");
		taMatSpecDesc = new TextField("Spec.Description");
		taMatSpecDesc.setWidth("275");
		btnaddMatSpec.setStyleName("add");
		btnaddMatSpec.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Material Specification
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validationmatspecification()) {
					saveMatSpecDetails();
				}
			}
		});
		btndeletespec.setEnabled(false);
		tblMatSpec = new GERPTable();
		// ClickListener for Material Owner Tale
		tblMatSpec.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMatSpec.isSelected(event.getItemId())) {
					tblMatSpec.setImmediate(true);
					btnaddMatSpec.setCaption("Add");
					btnaddMatSpec.setStyleName("savebt");
					btndeletespec.setEnabled(false);
					matSpecResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddMatSpec.setCaption("Update");
					btnaddMatSpec.setStyleName("savebt");
					btndeletespec.setEnabled(true);
					editMaterialSpec();
				}
			}
		});
		btndeletespec.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndeletespec == event.getButton()) {
					deletematspec();
				}
			}
		});
		// Material Combo Box Data List
		loadMaterialBranchList();
		loadMaterialDepartmentList();
		loadMaterialGroupList();
		loadMaterialUOMList();
		loadMaterialTypeList();
		loadEmployeeList();
		// Material Owner Combo Box Data List
		loadMatOwnerBranchList();
		loadMatOwnerDepartmentList();
		// Material Owner Combo Box Data List
		loadMatConsBranchList();
		loadMatConsDepartmentList();
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		matOwnerResetFields();
		matConsResetFields();
		matSpecResetFields();
		loadSrchRslt();
		loadSrchMatOwnerRslt(false);
		loadSrchMatConsRslt(false);
		loadSrchMatSpecRslt(false);
	}
	
	/*
	 * loadSrchRslt()-->this function is used for load the search result to table
	 */
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.removeAllItems();
		List<MaterialDM> materialList = new ArrayList<MaterialDM>();
		String materialCode = tfMaterialCode.getValue().toString();
		String materialName = tfMaterialName.getValue().toString();
		Long branchId = null;
		if (cbBranch.getValue() != null) {
			branchId = ((Long) cbBranch.getValue());
		}
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Search Parameters are "
				+ materialCode + "," + materialName + "," + branchId + "," + cbMaterialStatus.getValue() + ","
				+ companyId);
		materialList = serviceMaterial.getMaterialList(null, companyId, (Long) cbBranch.getValue(), null,
				(String) tfMaterialCode.getValue(), null, null, tfMaterialName.getValue(),
				(String) cbMaterialStatus.getValue(), "F");
		recordCnt = materialList.size();
		beanMaterial = new BeanItemContainer<MaterialDM>(MaterialDM.class);
		beanMaterial.addAll(materialList);
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Got the Material result set");
		tblMstScrSrchRslt.setContainerDataSource(beanMaterial);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "materialId", "branchName", "materialName", "materialCode",
				"materialStatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Branch Name", "Material Name", "Material Code",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("materialId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void deletematowner() {
		MaterialOwnersDM save = new MaterialOwnersDM();
		if (tblMatOwner.getValue() != null) {
			save = beanMaterialOwner.getItem(tblMatOwner.getValue()).getBean();
			matOwnerList.remove(save);
			matOwnerResetFields();
			loadSrchMatOwnerRslt(false);
			btndeletematowner.setEnabled(false);
		}
	}
	
	private void deletematconsumer() {
		MaterialConsumersDM save = new MaterialConsumersDM();
		if (tblMatCons.getValue() != null) {
			save = beanMaterialConsumer.getItem(tblMatCons.getValue()).getBean();
			matConsList.remove(save);
			matConsResetFields();
			loadSrchMatConsRslt(false);
			btndeletematcmr.setEnabled(false);
		}
	}
	
	private void deletematspec() {
		MaterialSpecDM save = new MaterialSpecDM();
		if (tblMatSpec.getValue() != null) {
			save = beanMaterialSpec.getItem(tblMatSpec.getValue()).getBean();
			matSpecList.remove(save);
			matSpecResetFields();
			loadSrchMatSpecRslt(false);
			btndeletespec.setEnabled(false);
		}
	}
	
	/*
	 * loadSrchMatOwnerRslt()-->this function is used for load the material owner search result to table
	 */
	private void loadSrchMatOwnerRslt(Boolean fromdb) {
		logger.info("Material Owner Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Loading Material Owner Search...");
		tblMatOwner.setPageLength(7);
		tblMatOwner.setWidth("100%");
		logger.info("Material Owner : Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Search Parameters are " + companyId + "," + materialId);
		if (fromdb) {
			matOwnerList = serviceMaterialOwner.getMaterialOwnerList(null, companyId, null, null, materialId, null,
					"Active", "F");
		}
		recordCntMatOwner = matOwnerList.size();
		beanMaterialOwner = new BeanItemContainer<MaterialOwnersDM>(MaterialOwnersDM.class);
		beanMaterialOwner.addAll(matOwnerList);
		tblMatOwner.setSelectable(true);
		tblMatOwner.setContainerDataSource(beanMaterialOwner);
		tblMatOwner.setVisibleColumns(new Object[] { "employeeName", "branchName", "deptName", "ownershipStatus",
				"lastupdateddt", "lastupdatedby" });
		tblMatOwner.setColumnHeaders(new String[] { "Employee", "Branch", "Department", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMatOwner.setColumnAlignment("materialOwnerId", Align.RIGHT);
		tblMatOwner.setColumnFooter("lastupdatedby", "No.of Records : " + recordCntMatOwner);
	}
	
	/*
	 * loadSrchMatConsRslt()-->this function is used for load the material owner search result to table
	 */
	private void loadSrchMatConsRslt(Boolean fromdb) {
		logger.info("Masterial Consumer Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Loading Material Consumer Search...");
		tblMatCons.setPageLength(7);
		tblMatCons.setWidth("100%");
		logger.info("" + "Material Consumer : Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Search Parameters are " + companyId + "," + materialId);
		if (fromdb) {
			matConsList.addAll(serviceMaterialConsumer.getMaterialConsumerList(null, companyId, null, null, materialId,
					"Active", "F"));
		}
		recordCntMatCons = matConsList.size();
		beanMaterialConsumer = new BeanItemContainer<MaterialConsumersDM>(MaterialConsumersDM.class);
		beanMaterialConsumer.addAll(matConsList);
		tblMatCons.setSelectable(true);
		tblMatCons.setContainerDataSource(beanMaterialConsumer);
		tblMatCons.setVisibleColumns(new Object[] { "branchName", "deptname", "matConsStatus", "lastupdateddt",
				"lastupdatedby" });
		tblMatCons.setColumnHeaders(new String[] { "Branch", "Department", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMatCons.setColumnAlignment("matConsumerId", Align.RIGHT);
		tblMatCons.setColumnFooter("lastupdatedby", "No.of Records : " + recordCntMatCons);
	}
	
	/*
	 * loadSrchMatSpecRslt()-->this function is used for load the material owner search result to table
	 */
	private void loadSrchMatSpecRslt(Boolean fromdb) {
		logger.info("Material Specification Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Loading Material Specification Search...");
		tblMatSpec.setPageLength(7);
		tblMatSpec.setWidth("100%");
		logger.info("" + "Material Specification : Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Search Parameters are " + companyId + "," + materialId);
		if (fromdb) {
			matSpecList.addAll(serviceMaterialSpec.getMaterialSpecList(null, companyId, materialId, null, null,
					"Active", "F"));
		}
		recordCntMatSpec = matSpecList.size();
		beanMaterialSpec = new BeanItemContainer<MaterialSpecDM>(MaterialSpecDM.class);
		beanMaterialSpec.addAll(matSpecList);
		tblMatSpec.setSelectable(true);
		tblMatSpec.setContainerDataSource(beanMaterialSpec);
		tblMatSpec.setVisibleColumns(new Object[] { "specName", "specDesc", "matSpecStatus", "lastupdateddt",
				"lastupdatedby" });
		tblMatSpec.setColumnHeaders(new String[] { "Spec.Name", "Spec.Description", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMatSpec.setColumnAlignment("materialSpecId", Align.RIGHT);
		tblMatSpec.setColumnFooter("lastupdatedby", "No.of Records : " + recordCntMatSpec);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// Remove all components in User Search Layout
		hlSearchLayout.removeAllComponents();
		tfMaterialCode.setRequired(false);
		tfMaterialName.setRequired(false);
		tfMaterialCode.setReadOnly(false);
		cbBranch.setRequired(false);
		cbBranch.setValue(0L);
		// Add components for Search Layout
		FormLayout flMaterialCode = new FormLayout();
		FormLayout flMaterialName = new FormLayout();
		FormLayout flBranch = new FormLayout();
		FormLayout flMaterialStatus = new FormLayout();
		flBranch.addComponent(cbBranch);
		flMaterialName.addComponent(tfMaterialName);
		flMaterialCode.addComponent(tfMaterialCode);
		flMaterialStatus.addComponent(cbMaterialStatus);
		hlSearchLayout.addComponent(flBranch);
		hlSearchLayout.addComponent(flMaterialName);
		hlSearchLayout.addComponent(flMaterialCode);
		hlSearchLayout.addComponent(flMaterialStatus);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling User Input layout");
		// Remove all components in User Input Layout
		hlUserInputLayout.removeAllComponents();
		tfMaterialCode.setRequired(true);
		tfMaterialName.setRequired(true);
		cbMaterialGroup.setRequired(true);
		cbMaterialType.setRequired(true);
		cbBranch.setRequired(true);
		cbDepartment.setRequired(true);
		cbMaterialUOM.setRequired(true);
		hlUserInputLayout.removeAllComponents();
		// Add Material Components for User Input Layout
		FormLayout flMaterial1 = new FormLayout();
		FormLayout flMaterial2 = new FormLayout();
		FormLayout flMaterial3 = new FormLayout();
		FormLayout flMaterial4 = new FormLayout();
		flMaterial1.addComponent(cbDepartment);
		flMaterial1.addComponent(cbBranch);
		flMaterial1.addComponent(tfMaterialName);
		flMaterial1.addComponent(tfMaterialCode);
		flMaterial2.addComponent(cbMaterialUOM);
		flMaterial2.addComponent(cbMaterialGroup);
		flMaterial2.addComponent(cbMaterialType);
		flMaterial2.addComponent(tfPartCode);
		flMaterial3.addComponent(tfUnitRate);
		flMaterial3.addComponent(taVisualSpec);
		flMaterial3.addComponent(tfReorderLevel);
		flMaterial4.addComponent(taRemark);
		flMaterial4.addComponent(cbMaterialStatus);
		// Setting for material component to layout
		HorizontalLayout hlMaterialComponent = new HorizontalLayout();
		hlMaterialComponent.addComponent(flMaterial1);
		hlMaterialComponent.addComponent(flMaterial2);
		hlMaterialComponent.addComponent(flMaterial3);
		hlMaterialComponent.addComponent(flMaterial4);
		hlMaterialComponent.setSpacing(true);
		hlMaterialComponent.setMargin(true);
		// Add Material Components for User Input Layout
		FormLayout flMaterialOwner1 = new FormLayout();
		FormLayout flMaterialOwner2 = new FormLayout();
		FormLayout flMaterialOwner3 = new FormLayout();
		FormLayout flMaterialOwner4 = new FormLayout();
		HorizontalLayout hlMatOwnerTable = new HorizontalLayout();
		hlMatOwnerTable.addComponent(tblMatOwner);
		hlMatOwnerTable.setWidth("100%");
		flMaterialOwner3.addComponent(cbMatOwnerEmployee);
		flMaterialOwner3.setSpacing(true);
		flMaterialOwner1.addComponent(cbMatOwnerBranch);
		flMaterialOwner1.setSpacing(true);
		flMaterialOwner2.addComponent(cbMatOwnerDept);
		flMaterialOwner2.setSpacing(true);
		flMaterialOwner4.addComponent(cbMatOwnerStatus);
		flMaterialOwner4.setSpacing(true);
		// Setting for material owner tab component to layout
		HorizontalLayout hlMaterialOwnerComponent = new HorizontalLayout();
		hlMaterialOwnerComponent.addComponent(flMaterialOwner1);
		hlMaterialOwnerComponent.addComponent(flMaterialOwner2);
		hlMaterialOwnerComponent.addComponent(flMaterialOwner3);
		hlMaterialOwnerComponent.addComponent(flMaterialOwner4);
		hlMaterialOwnerComponent.addComponent(btnaddMatOwner);
		hlMaterialOwnerComponent.addComponent(btndeletematowner);
		hlMaterialOwnerComponent.setComponentAlignment(btnaddMatOwner, Alignment.MIDDLE_LEFT);
		hlMaterialOwnerComponent.setComponentAlignment(btndeletematowner, Alignment.MIDDLE_LEFT);
		hlMaterialOwnerComponent.setSpacing(true);
		hlMaterialOwnerComponent.setMargin(true);
		hlMaterialOwnerComponent.setSizeUndefined();
		// To add component and table in material owner tab
		VerticalLayout vlMaterialOwnerTab = new VerticalLayout();
		vlMaterialOwnerTab.addComponent(hlMaterialOwnerComponent);
		vlMaterialOwnerTab.addComponent(hlMatOwnerTable);
		vlMaterialOwnerTab.setWidth("100%");
		// Add Material Consumer for User Input Layout
		FormLayout flMaterialConsumer1 = new FormLayout();
		FormLayout flMaterialConsumer2 = new FormLayout();
		FormLayout flMaterialConsumer3 = new FormLayout();
		HorizontalLayout hlMatConsTable = new HorizontalLayout();
		hlMatConsTable.addComponent(tblMatCons);
		hlMatConsTable.setWidth("100%");
		flMaterialConsumer1.addComponent(cbMatConsBranch);
		flMaterialConsumer1.setSpacing(true);
		flMaterialConsumer2.addComponent(cbMatConsDepartment);
		flMaterialConsumer2.setSpacing(true);
		flMaterialConsumer3.addComponent(cbMatConsStatus);
		cbMatConsBranch.setRequired(true);
		cbMatConsDepartment.setRequired(true);
		cbMatOwnerEmployee.setRequired(true);
		cbMatOwnerDept.setRequired(true);
		cbMatOwnerBranch.setRequired(true);
		tfMatSpecName.setRequired(true);
		flMaterialConsumer3.setSpacing(true);
		// Setting for material consumer tab component to layout
		HorizontalLayout hlMaterialConsumerComponent = new HorizontalLayout();
		hlMaterialConsumerComponent.addComponent(flMaterialConsumer1);
		hlMaterialConsumerComponent.addComponent(flMaterialConsumer2);
		hlMaterialConsumerComponent.addComponent(flMaterialConsumer3);
		hlMaterialConsumerComponent.addComponent(btnaddMatCons);
		hlMaterialConsumerComponent.addComponent(btndeletematcmr);
		hlMaterialConsumerComponent.setComponentAlignment(btnaddMatCons, Alignment.MIDDLE_LEFT);
		hlMaterialConsumerComponent.setComponentAlignment(btndeletematcmr, Alignment.MIDDLE_LEFT);
		hlMaterialConsumerComponent.setSpacing(true);
		hlMaterialConsumerComponent.setMargin(true);
		hlMaterialConsumerComponent.setSizeUndefined();
		// To add component and table in material consumer tab
		VerticalLayout vlMaterialConsTab = new VerticalLayout();
		vlMaterialConsTab.addComponent(hlMaterialConsumerComponent);
		vlMaterialConsTab.addComponent(hlMatConsTable);
		vlMaterialConsTab.setWidth("100%");
		// Add Material Specification for User Input Layout
		FormLayout flMaterialSpec1 = new FormLayout();
		FormLayout flMaterialSpec2 = new FormLayout();
		FormLayout flMaterialSpec3 = new FormLayout();
		HorizontalLayout hlMatSpecTable = new HorizontalLayout();
		hlMatSpecTable.addComponent(tblMatSpec);
		hlMatSpecTable.setWidth("100%");
		flMaterialSpec1.addComponent(tfMatSpecName);
		flMaterialSpec1.setSpacing(true);
		flMaterialSpec2.addComponent(taMatSpecDesc);
		flMaterialSpec2.setSpacing(true);
		flMaterialSpec3.addComponent(cbMatSpecStatus);
		flMaterialSpec3.setSpacing(true);
		// Setting for material specification tab component to layout
		HorizontalLayout hlMaterialSpecComponent = new HorizontalLayout();
		hlMaterialSpecComponent.addComponent(flMaterialSpec1);
		hlMaterialSpecComponent.addComponent(flMaterialSpec2);
		hlMaterialSpecComponent.addComponent(flMaterialSpec3);
		hlMaterialSpecComponent.addComponent(btnaddMatSpec);
		hlMaterialSpecComponent.addComponent(btndeletespec);
		hlMaterialSpecComponent.setComponentAlignment(btnaddMatSpec, Alignment.MIDDLE_LEFT);
		hlMaterialSpecComponent.setComponentAlignment(btndeletespec, Alignment.MIDDLE_LEFT);
		hlMaterialSpecComponent.setSpacing(true);
		hlMaterialSpecComponent.setMargin(true);
		hlMaterialSpecComponent.setSizeUndefined();
		// To add component and table in material specification tab
		VerticalLayout vlMaterialSpecTab = new VerticalLayout();
		vlMaterialSpecTab.addComponent(hlMaterialSpecComponent);
		vlMaterialSpecTab.addComponent(hlMatSpecTable);
		vlMaterialSpecTab.setWidth("100%");
		// Creating Tab Sheet
		TabSheet tabSheet = new TabSheet();
		tabSheet.setWidth("100%");
		tabSheet.setHeight("315");
		tabSheet.addTab(vlMaterialOwnerTab, "Material Owner", null);
		tabSheet.addTab(vlMaterialConsTab, "Material Consumer", null);
		tabSheet.addTab(vlMaterialSpecTab, "Material Specification", null);
		// Setting for all layout in vertical layout
		VerticalLayout vlAllComponent = new VerticalLayout();
		vlAllComponent.addComponent(GERPPanelGenerator.createPanel(hlMaterialComponent));
		vlAllComponent.addComponent(tabSheet);
		vlAllComponent.setSpacing(true);
		vlAllComponent.setWidth("100%");
		// add the form layouts into user input layout
		hlUserInputLayout.addComponent(vlAllComponent);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setWidth("100%");
	}
	
	/*
	 * loadMaterialTypeList()-->this function is used for load the material type
	 */
	public void loadMaterialTypeList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Loading Material Type Search...");
		List<MaterialTypeDM> materialTypeList = serviceMaterialType.getMaterialTypeList(null, null, "Active", "F");
		beanMaterialType = new BeanContainer<Long, MaterialTypeDM>(MaterialTypeDM.class);
		beanMaterialType.setBeanIdProperty("materialTypeId");
		beanMaterialType.addAll(materialTypeList);
		cbMaterialType.setContainerDataSource(beanMaterialType);
	}
	
	/*
	 * loadMaterialGroupList()-->this function is used for load the material group
	 */
	public void loadMaterialGroupList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Loading Material UOM Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyId, moduleId, "Active",
				"MM_MTRLGRP");
		beanCompanyLookUp = new BeanContainer<Long, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("cmplookupid");
		beanCompanyLookUp.addAll(lookUpList);
		cbMaterialGroup.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadMaterialUOMList()-->this function is used for load the material UOM type
	 */
	public void loadMaterialUOMList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Loading Material UOM Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyId, moduleId, "Active",
				"MM_UOM");
		beanCompanyLookUp = new BeanContainer<Long, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbMaterialUOM.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadMatOwnerBranchList()-->this function is used for load the branch list to material owner branch combo box
	 */
	private void loadMatOwnerBranchList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Branch Search...");
		List<BranchDM> branchlist = serviceBranch.getBranchList(null, null, null, "Active", companyId, "P");
		BeanItemContainer<BranchDM> beanownerbranch = new BeanItemContainer<BranchDM>(BranchDM.class);
		beanownerbranch.addAll(branchlist);
		cbMatOwnerBranch.setContainerDataSource(beanownerbranch);
	}
	
	/*
	 * loadMatConsBranchList()-->this function is used for load the branch list to material consumer branch combo box
	 */
	private void loadMatConsBranchList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Branch Search...");
		List<BranchDM> branchlist = serviceBranch.getBranchList(null, null, null, "Active", companyId, "P");
		BeanItemContainer<BranchDM> beanownerbranch = new BeanItemContainer<BranchDM>(BranchDM.class);
		beanownerbranch.addAll(branchlist);
		cbMatConsBranch.setContainerDataSource(beanownerbranch);
	}
	
	// // Load Client List
	// private void loadMatConsBranchList() {
	// logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading client Search...");
	// List<BranchDM> branchlist = serviceBranch.getBranchList(null, null, null, "Active", companyId, "P");
	// // beanBranch = new BeanItemContainer<BranchDM>(BranchDM.class);
	// // beanBranch.setBeanIdProperty("branchId");
	// beanBranch.addAll(branchlist);
	// cbMatConsBranch.setContainerDataSource(beanBranch);
	// }
	// private void loadMatConsDepartmentList() {
	// logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading client Search...");
	// List<DepartmentDM> departmentlist = serviceDepartmant.getDepartmentList(companyId, null, "Active", "P");
	// beanDepartment = new BeanItemContainer<DepartmentDM>(DepartmentDM.class);
	// // beanDepartment.setBeanIdProperty("deptid");
	// beanDepartment.addAll(departmentlist);
	// cbMatConsDepartment.setContainerDataSource(beanDepartment);
	// }
	//
	/*
	 * loadMaterialDepartmentList()--> this function is used for load the department list to material department combo
	 * box
	 */
	private void loadMaterialBranchList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Branch Search...");
		List<BranchDM> branchlist = serviceBranch.getBranchList(null, null, null, "Active", companyId, "P");
		branchlist.add(new BranchDM(0L, "All Branch"));
		beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranch.setBeanIdProperty("branchId");
		beanBranch.addAll(branchlist);
		cbBranch.setContainerDataSource(beanBranch);
	}
	
	private void loadMaterialDepartmentList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Department Search...");
		List<DepartmentDM> departmentlist = serviceDepartmant.getDepartmentList(companyId, null, "Active", "P");
		beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beanDepartment.setBeanIdProperty("deptid");
		beanDepartment.addAll(departmentlist);
		cbDepartment.setContainerDataSource(beanDepartment);
	}
	
	// //
	/*
	 * loadMatConsDepartmentList()--> this function is used for load the department list to material consumer department
	 * combo box //
	 */
	private void loadMatConsDepartmentList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Department Search...");
		List<DepartmentDM> departmentlist = serviceDepartmant.getDepartmentList(companyId, null, "Active", "P");
		BeanItemContainer<DepartmentDM> beanownerdepart = new BeanItemContainer<DepartmentDM>(DepartmentDM.class);
		beanownerdepart.addAll(departmentlist);
		cbMatConsDepartment.setContainerDataSource(beanownerdepart);
	}
	
	/*
	 * loadMatOwnerDepartmentList()--> this function is used for load the material department list to material owner
	 * department combo box
	 */
	private void loadMatOwnerDepartmentList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Department Search...");
		List<DepartmentDM> departmentlist = serviceDepartmant.getDepartmentList(companyId, null, "Active", "P");
		BeanItemContainer<DepartmentDM> beanownerdepart = new BeanItemContainer<DepartmentDM>(DepartmentDM.class);
		beanownerdepart.addAll(departmentlist);
		cbMatOwnerDept.setContainerDataSource(beanownerdepart);
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee list
	 */
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Loading Search...");
		List<EmployeeDM> employeelist = serviceEmployee.getEmployeeList(null, null, null, null, companyId, null, null,
				null, null, "F");
		BeanItemContainer<EmployeeDM> beanEmployee = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployee.addAll(employeelist);
		cbMatOwnerEmployee.setContainerDataSource(beanEmployee);
	}
	
	// Search Material Layout Details
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		tfMaterialCode.setValue("");
		tfMaterialCode.setReadOnly(false);
		tfMaterialName.setValue("");
		cbBranch.setValue(branchID);
		cbMaterialStatus.setValue(cbMaterialStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tblMstScrSrchRslt.setValue(null);
		hlCmdBtnLayout.setVisible(false);
		// remove the table
		tblMstScrSrchRslt.setVisible(false);
		// remove the default value from branch combo box
		cbBranch.removeItem(0L);
		cbMatOwnerBranch.removeItem(0L);
		cbMatConsBranch.removeItem(0L);
		assembleUserInputLayout();
		resetFields();
		loadSrchMatOwnerRslt(false);
		loadSrchMatConsRslt(false);
		loadSrchMatSpecRslt(false);
		tfMaterialCode.setReadOnly(false);
		tfMaterialCode.setValue("");
		try {
			SlnoGenDM slnoGenObj = serviceSlnogen.getSequenceNumber(companyId, branchID, moduleId, "MM_MTRLCD").get(0);
			if (slnoGenObj.getAutoGenYN().equals("Y")) {
				tfMaterialCode.setValue(slnoGenObj.getKeyDesc());
				tfMaterialCode.setReadOnly(true);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		cbMatOwnerBranch.setComponentError(null);
		cbMatOwnerDept.setComponentError(null);
		cbMatOwnerEmployee.setComponentError(null);
		cbMatConsBranch.setComponentError(null);
		cbMatConsDepartment.setComponentError(null);
		tfMatSpecName.setComponentError(null);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Updating existing record...");
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		// remove the table
		tblMstScrSrchRslt.setVisible(false);
		assembleUserInputLayout();
		// Add Blur Listener for Material Code
		resetFields();
		// remove the default value from branch combo box
		cbBranch.removeItem(0L);
		cbMatOwnerBranch.removeItem(0L);
		cbMatConsBranch.removeItem(0L);
		editMaterial();
		if (tfMaterialCode.getValue() == null || tfMaterialCode.getValue().trim().length() == 0) {
			tfMaterialCode.setReadOnly(false);
		}
	}
	
	// Reset the selected row's data into material input components
	private void editMaterial() {
		Item rowMaterialSelected = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (rowMaterialSelected != null) {
			MaterialDM editMaterialList = beanMaterial.getItem(tblMstScrSrchRslt.getValue()).getBean();
			materialId = editMaterialList.getMaterialId();
			if ((rowMaterialSelected.getItemProperty("materialCode").getValue() != null)) {
				tfMaterialCode.setReadOnly(false);
				tfMaterialCode.setValue(editMaterialList.getMaterialCode().toString());
				tfMaterialCode.setReadOnly(true);
			}
			if ((rowMaterialSelected.getItemProperty("materialName").getValue() != null)) {
				tfMaterialName.setValue(editMaterialList.getMaterialName().toString());
			}
			if ((rowMaterialSelected.getItemProperty("materialGroup").getValue() != null)) {
				cbMaterialGroup.setValue((String) editMaterialList.getMaterialGroup());
			}
			if ((rowMaterialSelected.getItemProperty("materialTypeName").getValue() != null)) {
				cbMaterialType.setValue(editMaterialList.getMaterialTypeId());
			}
			if ((rowMaterialSelected.getItemProperty("partCode").getValue() != null)) {
				tfPartCode.setValue(editMaterialList.getPartCode().toString());
			}
			if ((rowMaterialSelected.getItemProperty("materialUOM").getValue() != null)) {
				cbMaterialUOM.setValue(editMaterialList.getMaterialUOM().toString());
			}
			if ((rowMaterialSelected.getItemProperty("unitRate").getValue() != null)) {
				tfUnitRate.setValue(editMaterialList.getUnitRate().toString());
			}
			if ((rowMaterialSelected.getItemProperty("visualSpec").getValue() != null)) {
				taVisualSpec.setValue(editMaterialList.getVisualSpec().toString());
			}
			if ((rowMaterialSelected.getItemProperty("reorderLevel").getValue() != null)) {
				tfReorderLevel.setValue(editMaterialList.getReorderLevel().toString());
			}
			if ((rowMaterialSelected.getItemProperty("remarks").getValue() != null)) {
				taRemark.setValue(editMaterialList.getRemarks().toString());
			}
			if ((rowMaterialSelected.getItemProperty("branchName").getValue() != null)) {
				cbBranch.setValue(editMaterialList.getBranchId());
			}
			if ((rowMaterialSelected.getItemProperty("deptname").getValue() != null)) {
				cbDepartment.setValue(editMaterialList.getDeptId());
			}
			if (("materialStatus") != null) {
				String stCode = rowMaterialSelected.getItemProperty("materialStatus").getValue().toString();
				cbMaterialStatus.setValue(stCode);
			}
		}
		loadSrchMatOwnerRslt(true);
		loadSrchMatConsRslt(true);
		loadSrchMatSpecRslt(true);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		tfMaterialCode.setComponentError(null);
		tfMaterialName.setComponentError(null);
		cbMaterialGroup.setComponentError(null);
		cbMaterialType.setComponentError(null);
		cbMaterialUOM.setComponentError(null);
		cbBranch.setComponentError(null);
		cbDepartment.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((tfMaterialCode.getValue() == null) || tfMaterialCode.getValue().trim().length() == 0) {
			List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyId, branchID, moduleId, "MM_MTRLCD");
			for (SlnoGenDM slnoGenObj : slnoList) {
				if (slnoGenObj.getAutoGenYN().equals("N")) {
					tfMaterialCode.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_CODE));
					errorFlag = true;
				}
			}
		}
		if ((tfMaterialName.getValue() == "") || tfMaterialName.getValue().trim().length() == 0) {
			tfMaterialName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			errorFlag = true;
		}
		if (cbMaterialGroup.getValue() == null) {
			cbMaterialGroup.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_NAME));
			errorFlag = true;
		}
		if (cbMaterialType.getValue() == null) {
			cbMaterialType.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_TYPE));
			errorFlag = true;
		}
		if (cbMaterialUOM.getValue() == null) {
			cbMaterialUOM.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_UOM));
			errorFlag = true;
		}
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_BRANCH));
			errorFlag = true;
		}
		if (cbDepartment.getValue() == null) {
			cbDepartment.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_DEPARTMENT));
			errorFlag = true;
		}
		if (tblMatOwner.size() == 0) {
			cbMatOwnerEmployee.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			cbMatOwnerBranch.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_BRANCH));
			cbMatOwnerDept.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_DEPARTMENT));
			errorFlag = true;
		}
		if (tblMatCons.size() == 0) {
			cbMatConsBranch.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_BRANCH));
			cbMatConsDepartment.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_DEPARTMENT));
			errorFlag = true;
		}
		if (tblMatSpec.size() == 0) {
			tfMatSpecName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_SPEC_NAME));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Throwing ValidationException. User data is > " + tfMaterialCode.getValue() + ","
				+ tfMaterialName.getValue() + "," + cbMaterialGroup.getValue() + "," + cbMaterialType.getValue() + ","
				+ cbMaterialUOM.getValue() + "," + cbBranch.getValue() + "," + cbDepartment.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validationmatowner() {
		cbMatOwnerEmployee.setComponentError(null);
		cbMatOwnerBranch.setComponentError(null);
		cbMatOwnerDept.setComponentError(null);
		boolean isValid = true;
		if (cbMatOwnerEmployee.getValue() == null) {
			cbMatOwnerEmployee.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			isValid = false;
		}
		if (cbMatOwnerBranch.getValue() == null) {
			cbMatOwnerBranch.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_BRANCH));
			isValid = false;
		}
		if (cbMatOwnerDept.getValue() == null) {
			cbMatOwnerDept.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_DEPARTMENT));
			isValid = false;
		}
		return isValid;
	}
	
	private boolean validationmatconsumer() {
		cbMatConsBranch.setComponentError(null);
		cbMatConsDepartment.setComponentError(null);
		boolean isValid = true;
		if (cbMatConsBranch.getValue() == null) {
			cbMatConsBranch.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_BRANCH));
			isValid = false;
		}
		if (cbMatConsDepartment.getValue() == null) {
			cbMatConsDepartment.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_DEPARTMENT));
			isValid = false;
		}
		return isValid;
	}
	
	private boolean validationmatspecification() {
		tfMatSpecName.setComponentError(null);
		boolean isValid = true;
		if ((tfMatSpecName.getValue() == "") || tfMatSpecName.getValue().trim().length() == 0) {
			tfMatSpecName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERIAL_SPEC_NAME));
			isValid = false;
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
			MaterialDM materialObj = new MaterialDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				materialObj = beanMaterial.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			materialObj.setMaterialCode(tfMaterialCode.getValue());
			materialObj.setCompanyId(companyId);
			if (cbBranch.getValue() != null) {
				materialObj.setBranchId((Long) cbBranch.getValue());
			}
			if (cbDepartment.getValue() != null) {
				materialObj.setDeptId((Long) cbDepartment.getValue());
			}
			if (cbMaterialGroup.getValue() != null) {
				materialObj.setMaterialGroup((String) cbMaterialGroup.getValue());
			}
			if (cbMaterialType.getValue() != null) {
				materialObj.setMaterialTypeId((Long) cbMaterialType.getValue());
			}
			materialObj.setMaterialName(tfMaterialName.getValue().toString());
			if (tfPartCode.getValue() != null) {
				materialObj.setPartCode(tfPartCode.getValue().toString());
			}
			if (cbMaterialUOM.getValue() != null) {
				materialObj.setMaterialUOM((String) cbMaterialUOM.getValue());
			}
			if (tfUnitRate.getValue() != null && tfUnitRate.getValue() != "") {
				materialObj.setUnitRate(Long.valueOf(tfUnitRate.getValue()));
			}
			if (taVisualSpec.getValue() != null) {
				materialObj.setVisualSpec(taVisualSpec.getValue());
			}
			if (tfReorderLevel.getValue() != "" && tfReorderLevel.getValue() != "") {
				materialObj.setReorderLevel(Long.valueOf(tfReorderLevel.getValue()));
			}
			if (taRemark.getValue() != null) {
				materialObj.setRemarks(taRemark.getValue());
			}
			if (cbMaterialStatus.getValue() != null) {
				materialObj.setMaterialStatus((String) cbMaterialStatus.getValue());
			}
			materialObj.setLastupdateddt(DateUtils.getcurrentdate());
			materialObj.setLastupdatedby(userName);
			serviceMaterial.saveOrUpdateMaterial(materialObj);
			materialId = materialObj.getMaterialId();
			@SuppressWarnings("unchecked")
			Collection<MaterialOwnersDM> matOwnerItemIds = (Collection<MaterialOwnersDM>) tblMatOwner
					.getVisibleItemIds();
			for (MaterialOwnersDM saveMatOwner : (Collection<MaterialOwnersDM>) matOwnerItemIds) {
				saveMatOwner.setMaterialId(Long.valueOf(materialObj.getMaterialId()));
				serviceMaterialOwner.saveOrUpdateMaterialOwner(saveMatOwner);
			}
			@SuppressWarnings("unchecked")
			Collection<MaterialSpecDM> matSpecItemIds = (Collection<MaterialSpecDM>) tblMatSpec.getVisibleItemIds();
			for (MaterialSpecDM saveMatSpec : (Collection<MaterialSpecDM>) matSpecItemIds) {
				saveMatSpec.setMaterialId(Long.valueOf(materialObj.getMaterialId()));
				serviceMaterialSpec.saverOrUpdateMaterialSpec(saveMatSpec);
			}
			@SuppressWarnings("unchecked")
			Collection<MaterialConsumersDM> matConsItemIds = (Collection<MaterialConsumersDM>) tblMatCons
					.getVisibleItemIds();
			for (MaterialConsumersDM saveMatCons : (Collection<MaterialConsumersDM>) matConsItemIds) {
				saveMatCons.setMaterialId(Long.valueOf(materialObj.getMaterialId()));
				serviceMaterialConsumer.saveOrUpdateMaterialConsumer(saveMatCons);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyId, branchID, moduleId, "MM_MTRLCD")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyId, branchID, moduleId, "MM_MTRLCD");
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			resetFields();
			loadSrchRslt();
			loadSrchMatOwnerRslt(true);
			loadSrchMatConsRslt(true);
			loadSrchMatSpecRslt(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void showAuditDetails() {
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Canceling action ");
		// reset the input controls to default value
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		// Remove Blur Listener for Search Layout
		for (Object rbl : tfMaterialCode.getListeners(BlurEvent.class))
			tfMaterialCode.removeBlurListener((BlurListener) rbl);
		assembleSearchLayout();
		resetFields();
		matOwnerResetFields();
		matConsResetFields();
		matSpecResetFields();
	}
	
	/*
	 * matOwnerResetFields()-->this function is used for reset material owner fields
	 */
	protected void matOwnerResetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting the Material Owner UI controls");
		cbMatOwnerEmployee.setValue(null);
		cbMatOwnerBranch.setValue(null);
		cbMatOwnerDept.setValue(null);
		tblMatOwner.setValue(null);
		btnaddMatOwner.setCaption("Add");
		cbMatOwnerStatus.setValue(cbMatOwnerStatus.getItemIds().iterator().next());
	}
	
	/*
	 * matConsResetFields()-->this function is used for reset material consumer fields
	 */
	protected void matConsResetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting the Material Consumer UI controls");
		cbMatConsBranch.setValue(null);
		cbMatConsDepartment.setValue(null);
		tblMatCons.setValue(null);
		btnaddMatCons.setCaption("Add");
		cbMatConsStatus.setValue(cbMatConsStatus.getItemIds().iterator().next());
	}
	
	/*
	 * matSpecResetFields()-->this function is used for reset material specification fields
	 */
	protected void matSpecResetFields() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Resetting the Material Specification UI controls");
		tfMatSpecName.setValue("");
		taMatSpecDesc.setValue("");
		tblMatCons.setValue(null);
		btnaddMatSpec.setCaption("Add");
		cbMatSpecStatus.setValue(cbMatSpecStatus.getItemIds().iterator().next());
	}
	
	/*
	 * saveMatOwnerDetails()-->this function is used for save the material owner's details for temporary
	 */
	protected void saveMatOwnerDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Saving Material Owner Data... ");
		validationmatowner();
		MaterialOwnersDM matOwnerObj = new MaterialOwnersDM();
		if (tblMatOwner.getValue() != null) {
			matOwnerObj = beanMaterialOwner.getItem(tblMatOwner.getValue()).getBean();
		}
		matOwnerObj.setEmployeeId(((EmployeeDM) cbMatOwnerEmployee.getValue()).getEmployeeid());
		matOwnerObj.setEmployeeName(((EmployeeDM) cbMatOwnerEmployee.getValue()).getFullname());
		matOwnerObj.setBranchId(((BranchDM) cbMatOwnerBranch.getValue()).getBranchId());
		matOwnerObj.setBranchName(((BranchDM) cbMatOwnerBranch.getValue()).getBranchName());
		matOwnerObj.setMaterialName((String) tfMaterialName.getValue());
		matOwnerObj.setDeptId(((DepartmentDM) cbMatOwnerDept.getValue()).getDeptid());
		matOwnerObj.setDeptName(((DepartmentDM) cbMatOwnerDept.getValue()).getDeptname());
		matOwnerObj.setOwnershipStatus((String) cbMatOwnerStatus.getValue());
		matOwnerObj.setLastupdateddt(DateUtils.getcurrentdate());
		matOwnerObj.setLastupdatedby(userName);
		matOwnerObj.setCompanyId(companyId);
		matOwnerList.add(matOwnerObj);
		loadSrchMatOwnerRslt(false);
		btnaddMatOwner.setCaption("Add");
		matOwnerResetFields();
	}
	
	/*
	 * saveMatSpecDetails()-->this function is used for save the material specification's details for temporary
	 */
	protected void saveMatSpecDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Saving Material Specification Data... ");
		validationmatspecification();
		MaterialSpecDM matSpecObj = new MaterialSpecDM();
		if (tblMatSpec.getValue() != null) {
			matSpecObj = beanMaterialSpec.getItem(tblMatSpec.getValue()).getBean();
		}
		matSpecObj.setSpecName(tfMatSpecName.getValue().toString());
		matSpecObj.setSpecDesc(taMatSpecDesc.getValue().toString());
		matSpecObj.setMaterialName((String) tfMaterialName.getValue());
		matSpecObj.setMatSpecStatus(cbMatSpecStatus.getValue().toString());
		matSpecObj.setLastupdateddt(DateUtils.getcurrentdate());
		matSpecObj.setLastupdatedby(userName);
		matSpecObj.setCompanyId(companyId);
		matSpecList.add(matSpecObj);
		loadSrchMatSpecRslt(false);
		btnaddMatSpec.setCaption("Add");
		matSpecResetFields();
	}
	
	/*
	 * saveMatConsDetails()-->this function is used for save the material consumers's details for temporary
	 */
	protected void saveMatConsDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Saving Material Consumer Data... ");
		validationmatconsumer();
		MaterialConsumersDM materialConsObj = new MaterialConsumersDM();
		if (tblMatCons.getValue() != null) {
			materialConsObj = beanMaterialConsumer.getItem(tblMatCons.getValue()).getBean();
		}
		materialConsObj.setBranchId(((BranchDM) cbMatConsBranch.getValue()).getBranchId());
		materialConsObj.setBranchName(((BranchDM) cbMatConsBranch.getValue()).getBranchName());
		materialConsObj.setDeptId(((DepartmentDM) cbMatConsDepartment.getValue()).getDeptid());
		materialConsObj.setDeptname(((DepartmentDM) cbMatConsDepartment.getValue()).getDeptname());
		materialConsObj.setMaterialName(tfMaterialName.getValue().toString());
		materialConsObj.setMatConsStatus((String) cbMatConsStatus.getValue());
		materialConsObj.setLastupdateddt(DateUtils.getcurrentdate());
		materialConsObj.setLastupdatedby(userName);
		materialConsObj.setCompanyId(companyId);
		matConsList.add(materialConsObj);
		loadSrchMatConsRslt(false);
		btnaddMatCons.setCaption("Add");
		matConsResetFields();
	}
	
	/*
	 * editMaterialOwner()-->this function is used for restore the selected row's data to material owner components
	 */
	private void editMaterialOwner() {
		if (tblMatOwner.getValue() != null) {
			MaterialOwnersDM editmatowner = beanMaterialOwner.getItem(tblMatOwner.getValue()).getBean();
			Long empId = editmatowner.getEmployeeId();
			Collection<?> empColId = cbMatOwnerEmployee.getItemIds();
			for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMatOwnerEmployee.getItem(itemIdClient);
				// Get the actual bean and use the data
				EmployeeDM matObj = (EmployeeDM) itemclient.getBean();
				if (empId != null && empId.equals(matObj.getEmployeeid())) {
					cbMatOwnerEmployee.setValue(itemIdClient);
				}
			}
			Long branchId = editmatowner.getBranchId();
			Collection<?> branchcolId = cbMatOwnerBranch.getItemIds();
			for (Iterator<?> iteratorclient = branchcolId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMatOwnerBranch.getItem(itemIdClient);
				// Get the actual bean and use the data
				BranchDM matObj = (BranchDM) itemclient.getBean();
				if (branchId != null && branchId.equals(matObj.getBranchId())) {
					cbMatOwnerBranch.setValue(itemIdClient);
				}
			}
			Long deptId = editmatowner.getDeptId();
			Collection<?> deptcolId = cbMatOwnerDept.getItemIds();
			for (Iterator<?> iteratorclient = deptcolId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMatOwnerDept.getItem(itemIdClient);
				// Get the actual bean and use the data
				DepartmentDM matObj = (DepartmentDM) itemclient.getBean();
				if (deptId != null && deptId.equals(matObj.getDeptid())) {
					cbMatOwnerDept.setValue(itemIdClient);
				}
			}
			cbMatOwnerStatus.setValue(editmatowner.getOwnershipStatus());
		}
	}
	
	/*
	 * editMaterialConsumer()-->this function is used for restore the selected row's data to material consumer
	 * components
	 */
	private void editMaterialConsumer() {
		if (tblMatCons.getValue() != null) {
			MaterialConsumersDM editmatowner = beanMaterialConsumer.getItem(tblMatCons.getValue()).getBean();
			Long branchId = editmatowner.getBranchId();
			Collection<?> branchcolId = cbMatConsBranch.getItemIds();
			for (Iterator<?> iteratorclient = branchcolId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMatConsBranch.getItem(itemIdClient);
				// Get the actual bean and use the data
				BranchDM matObj = (BranchDM) itemclient.getBean();
				if (branchId != null && branchId.equals(matObj.getBranchId())) {
					cbMatConsBranch.setValue(itemIdClient);
				}
			}
			Long deptId = editmatowner.getDeptId();
			Collection<?> deptcolId = cbMatConsDepartment.getItemIds();
			for (Iterator<?> iteratorclient = deptcolId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMatConsDepartment.getItem(itemIdClient);
				// Get the actual bean and use the data
				DepartmentDM matObj = (DepartmentDM) itemclient.getBean();
				if (deptId != null && deptId.equals(matObj.getDeptid())) {
					cbMatConsDepartment.setValue(itemIdClient);
				}
			}
			cbMatConsStatus.setValue(editmatowner.getMatConsStatus());
		}
	}
	
	/*
	 * editMaterialSpec()-->this function is used for restore the selected row's data to material specification
	 * components
	 */
	private void editMaterialSpec() {
		Item matSpecSelected = tblMatSpec.getItem(tblMatSpec.getValue());
		if (matSpecSelected != null) {
			tfMatSpecName.setValue(matSpecSelected.getItemProperty("specName").getValue().toString());
			taMatSpecDesc.setValue(matSpecSelected.getItemProperty("specDesc").getValue().toString());
			String stCode = matSpecSelected.getItemProperty("matSpecStatus").getValue().toString();
			cbMatSpecStatus.setValue(stCode);
		}
	}
	
	@Override
	protected void resetFields() {
		tfMaterialCode.setComponentError(null);
		tfMaterialCode.setReadOnly(false);
		tfMaterialCode.setValue("");
		cbBranch.setValue(branchID);
		tfMaterialName.setComponentError(null);
		cbMaterialUOM.setComponentError(null);
		tfPartCode.setComponentError(null);
		tfReorderLevel.setComponentError(null);
		tfUnitRate.setComponentError(null);
		cbBranch.setComponentError(null);
		cbDepartment.setComponentError(null);
		cbDepartment.setValue(null);
		tfMaterialName.setValue("");
		cbMaterialUOM.setValue(null);
		cbMaterialGroup.setValue(null);
		tfPartCode.setValue("");
		tfUnitRate.setValue("");
		cbMaterialType.setValue(null);
		taVisualSpec.setValue("");
		tfReorderLevel.setValue("");
		taRemark.setValue("");
		cbMaterialStatus.setValue(null);
		cbMaterialGroup.setComponentError(null);
		cbMaterialType.setComponentError(null);
		taRemark.setComponentError(null);
		taVisualSpec.setComponentError(null);
		matOwnerList = new ArrayList<MaterialOwnersDM>();
		matConsList = new ArrayList<MaterialConsumersDM>();
		matSpecList = new ArrayList<MaterialSpecDM>();
	}
}