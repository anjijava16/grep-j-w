/**
 * File Name	:	AssetApec.java
 * Description	:	To Handle asset brand details for assets.
 * Author		:	Priyanga M
 * Date			:	March 06, 2014
 * Modification :   UI code optimization
 * Modified By  :   MOHAMED 
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
 *0.3			07-AUG-2014     MOHAMED                 Code Modified
 */
package com.gnts.asm.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.txn.AssetSpecDM;
import com.gnts.asm.service.txn.AssetSpecService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class AssetSpec implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Declaration for add and edit panel components
	private TextField tfSpecName, tfSpecValue;
	private ComboBox cbSpecStatus;
	private Label lblspace;
	// for Search
	private Button btnSearch, btnReset;
	private HorizontalLayout hlSaveCancel = new HorizontalLayout();
	// form layout for input controls
	private FormLayout flColumn1, flColumn2;
	// Table Declaration
	private Table tblMstScrSrchRslt;
	private Button btnAdd, btnEdit, btnHome, downloadbtn, btnAuditrRecords;
	private Button btnadd;
	private Button btnSave = new Button("Save", this);
	private Button btnCancel = new Button("Cancel", this);
	private Button btndelete = new GERPButton(" Delete", "delete", this);
	private List<AssetSpecDM> usertable = new ArrayList<AssetSpecDM>();
	// Declaration for Label
	private BeanItemContainer<AssetSpecDM> beans = null;
	private VerticalLayout vlTableForm, vlTableLayout;
	private String username;
	private Long companyid;
	private AssetSpecService serviceSpec = (AssetSpecService) SpringContextHelper.getBean("assetSpec");
	private Logger logger = Logger.getLogger(AssetSpec.class);
	private int total = 0;
	private Long assetsId;
	
	public AssetSpec(VerticalLayout vlAssetSpec, VerticalLayout vlTab, Long assetId) {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildView(vlAssetSpec);
	}
	
	private void buildView(VerticalLayout vlAssetSpec) {
		vlAssetSpec.removeAllComponents();
		// Initialization for tfSpecName
		tfSpecName = new TextField("Specification Name");
		tfSpecName.setRequired(true);
		// Initialization for tfSpecValue
		tfSpecValue = new TextField("Specification Value");
		tfSpecValue.setRequired(true);
		// Initialization for cbSpecStatus
		cbSpecStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbSpecStatus.setItemCaptionPropertyId("desc");
		cbSpecStatus.setWidth("150");
		btnSearch = new Button("Search", this);
		btnSearch.setStyleName("searchbt");
		btnReset = new Button("Reset", this);
		btnReset.setStyleName("resetbt");
		// Initialization for btnSave
		btnSave.setDescription("Save");
		btnSave.setStyleName("savebt");
		// btnSave.setVisible(false);
		// Initialization for btnCancel
		btnCancel.setDescription("Cancel");
		btnCancel.setStyleName("cancelbt");
		// btnCancel.setVisible(false);
		hlSaveCancel = new HorizontalLayout();
		hlSaveCancel.addComponent(btnSave);
		hlSaveCancel.addComponent(btnCancel);
		hlSaveCancel.setVisible(false);
		// label,add,edit and download panel
		// Initialization for btnAdd
		btnAdd = new Button("Add", this);
		btnAdd.setDescription("Add Asset Specification");
		btnAdd.setStyleName("add");
		// Initialization for btnEdit
		btnEdit = new Button("Edit", this);
		btnEdit.setDescription("Edit Asset specification");
		btnEdit.setStyleName("editbt");
		btnEdit.setEnabled(false);
		// Initialization for btnHome
		btnHome = new Button("Home", this);
		btnHome.setStyleName("homebtn");
		btnHome.setEnabled(false);
		btnadd = new GERPButton("Add", "add", this);
		btnadd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Asset Spec
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnadd == event.getButton()) {
					if (validateAll()) {
						saveAssetSpec();
					}
				}
			}
		});
		// Initialization for btnAuditrRecords
		btnAuditrRecords = new Button("Audit History", this);
		btnAuditrRecords.setStyleName("hostorybtn");
		// Initialization for downloadbtn
		downloadbtn = new Button("Downloads");
		downloadbtn.setDescription("Download");
		downloadbtn.addStyleName("downloadbt");
		downloadbtn.setEnabled(true);
		lblspace = new Label();
		// Initialization for table panel components
		tblMstScrSrchRslt = new Table();
		tblMstScrSrchRslt.setSizeFull();
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.setColumnCollapsingAllowed(true);
		tblMstScrSrchRslt.setPageLength(7);
		tblMstScrSrchRslt.setStyleName(Runo.TABLE_SMALL);
		tblMstScrSrchRslt.setWidth("800");
		tblMstScrSrchRslt.setImmediate(true);
		tblMstScrSrchRslt.setFooterVisible(true);
		tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
					tblMstScrSrchRslt.setImmediate(true);
					btnadd.setCaption("Add");
					btnadd.setStyleName("addbt");
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnadd.setCaption("Update");
					btnadd.setStyleName("savebt");
					editSpecification();
				}
			}
		});
		// ClickListener for Asset Spec Tale
		vlTableForm = new VerticalLayout();
		vlTableForm.setSizeFull();
		vlTableForm.setMargin(true);
		vlTableForm.setSpacing(true);
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn1.addComponent(tfSpecName);
		flColumn1.addComponent(lblspace);
		flColumn1.addComponent(tfSpecValue);
		flColumn1.addComponent(lblspace);
		flColumn1.addComponent(cbSpecStatus);
		flColumn1.addComponent(lblspace);
		flColumn1.addComponent(btnadd);
		flColumn1.addComponent(btndelete);
		flColumn2.addComponent(tblMstScrSrchRslt);
		HorizontalLayout hlInput = new HorizontalLayout();
		hlInput.setMargin(true);
		// Input.setWidth("1100");
		hlInput.addComponent(flColumn1);
		hlInput.addComponent(flColumn2);
		hlInput.setComponentAlignment(flColumn2, Alignment.MIDDLE_LEFT);
		// vlTableForm.addComponent(Input);
		vlTableLayout = new VerticalLayout();
		vlTableLayout.addComponent(hlInput);
		vlAssetSpec.addComponent(vlTableLayout);
		setTableProperties();
		resetFields();
		loadSrchRslt(false, null);
	}
	
	private void setTableProperties() {
		tblMstScrSrchRslt.setColumnAlignment("assetSepcId", Align.RIGHT);
		tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
				} else {
					btnEdit.setEnabled(true);
				}
				if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}
			}
		});
	}
	
	// Method for show the details in grid table while search and normal mode
	public void loadSrchRslt(boolean fromdb, Long assetId) {
		try {
			if (fromdb) {
				usertable = serviceSpec.getAssetSpecList(assetId, null, "Active");
			}
			tblMstScrSrchRslt.removeAllItems();
			total = usertable.size();
			beans = new BeanItemContainer<AssetSpecDM>(AssetSpecDM.class);
			beans.addAll(usertable);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the assetspec. result set");
			tblMstScrSrchRslt.setContainerDataSource(beans);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.Of Records:" + total);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "assetSepcId", "specName", "specValue", "specStatus",
					"lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Specification Name", "Spec.Value",
					"Spec. Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("assetSepcId", Align.RIGHT);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method used to display selected row's values in desired text box and combo box for edit the values
	private void editSpecification() {
		try {
			if (tblMstScrSrchRslt.getValue() != null) {
				AssetSpecDM enqdtl = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
				tfSpecName.setValue(enqdtl.getSpecName());
				tfSpecValue.setValue(enqdtl.getSpecValue());
				cbSpecStatus.setValue(enqdtl.getSpecStatus());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private boolean validateAll() {
		boolean errorflag = true;
		if (tfSpecName.getValue() == "") {
			tfSpecName.setComponentError(new UserError("Enter Asset Specification"));
			errorflag = false;
		} else {
			tfSpecName.setComponentError(null);
		}
		if (tfSpecValue.getValue() == "") {
			tfSpecValue.setComponentError(new UserError("Given Spec value"));
			errorflag = false;
		} else {
			tfSpecValue.setComponentError(null);
		}
		return errorflag;
	}
	
	// Save Method for save and update the Asset Specification details
	private void saveAssetSpec() {
		try {
			AssetSpecDM savespec = new AssetSpecDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				savespec = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
				usertable.remove(savespec);
			}
			savespec.setSpecName(tfSpecName.getValue());
			savespec.setSpecValue(tfSpecValue.getValue());
			savespec.setSpecStatus((String) cbSpecStatus.getValue());
			savespec.setLastUpdatedBy(username);
			savespec.setLastUpdatedDate(DateUtils.getcurrentdate());
			usertable.add(savespec);
			resetFields();
			loadSrchRslt(false, assetsId);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deletedetails() {
		AssetSpecDM delete = new AssetSpecDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			delete = beans.getItem(tblMstScrSrchRslt.getValue()).getBean();
			usertable.remove(delete);
			tfSpecName.setValue("");
			tfSpecValue.setValue("");
			cbSpecStatus.setValue(cbSpecStatus.getItemIds().iterator().next());
			loadSrchRslt(false, null);
		}
	}
	
	public void saveAssetSpec(Long assetId) {
		@SuppressWarnings("unchecked")
		Collection<AssetSpecDM> itemIds = (Collection<AssetSpecDM>) tblMstScrSrchRslt.getVisibleItemIds();
		for (AssetSpecDM saveasset : (Collection<AssetSpecDM>) itemIds) {
			saveasset.setAssetId(assetId);
			serviceSpec.saveDetails(saveasset);
		}
	}
	
	// Method used to reset pnlmain's value
	void resetFields() {
		tfSpecName.setComponentError(null);
		// btnSave.setComponentError(null);
		tfSpecName.setValue("");
		tfSpecValue.setValue("");
		cbSpecStatus.setValue(cbSpecStatus.getItemIds().iterator().next());
		tblMstScrSrchRslt.removeAllItems();
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (btndelete == event.getButton()) {
			deletedetails();
		}
	}
}
