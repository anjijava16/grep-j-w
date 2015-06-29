package com.gnts.asm.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.txn.AssetOwnDetailsDM;
import com.gnts.asm.service.txn.AssetOwnDetailsService;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanContainer;
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
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class AssetOwnDetails implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Declaration for add and edit panel components
	private TextField tfDesc;
	private ComboBox cbStatus, cbUsedBy;
	private PopupDateField dtStartDate, dtEndDate;
	// Declaration for add and edit panel
	// Table Declaration
	private Table tblAssetOwnDtls;
	private Button btnAdd;
	private Button btndelete;
	private Label lblspace;
	private List<AssetOwnDetailsDM> usertable = new ArrayList<AssetOwnDetailsDM>();
	// Declaration for Label
	private BeanItemContainer<AssetOwnDetailsDM> beanAssetOwner = null;
	private VerticalLayout vltable;
	private FormLayout flColumn1, flColumn2;
	private String username;
	private Long companyid, assetOwnId;
	private AssetOwnDetailsService serviceAssetOwnDetails = (AssetOwnDetailsService) SpringContextHelper
			.getBean("assetOwnDetails");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private Logger logger = Logger.getLogger(AssetOwnDetails.class);
	private int total;
	
	public AssetOwnDetails(VerticalLayout vlOwnDetails, Long assetOwnId) {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		buildView(vlOwnDetails);
	}
	
	private void buildView(VerticalLayout vlOwnDetails) {
		vlOwnDetails.removeAllComponents();
		// Initialization for tfSpecName
		tfDesc = new GERPTextField("OwnerShip Desc.");
		// Initialization for dtSearchMsgSentDateFrom
		dtStartDate = new GERPPopupDateField("Start Date");
		dtStartDate.setDateFormat("dd-MMM-yyyy");
		dtStartDate.setRequired(true);
		dtStartDate.setWidth("155");
		// Initialization for dtSearchMsgSentDateFrom
		dtEndDate = new GERPPopupDateField("End Date");
		dtEndDate.setDateFormat("dd-MMM-yyyy");
		dtEndDate.setRequired(true);
		dtEndDate.setWidth("155");
		// Used to Load Region Name in Combo box
		cbUsedBy = new GERPComboBox("Used By");
		cbUsedBy.setNullSelectionAllowed(false);
		cbUsedBy.setRequired(true);
		cbUsedBy.setItemCaptionPropertyId("firstname");
		loadEmployee();
		// Initialization for cbSearchBrandsStatus
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbStatus.setItemCaptionPropertyId("desc");
		cbStatus.setWidth("155");
		btnAdd = new GERPButton("Add", "add", this);
		btnAdd.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validation() == false) {
					saveAssetOwnerDetail();
				}
			}
		});
		btndelete = new GERPButton("Delete", "delete", this);
		// Initialize label for empty space
		lblspace = new Label();
		// Initialization for table panel components
		tblAssetOwnDtls = new Table();
		tblAssetOwnDtls.setSizeFull();
		tblAssetOwnDtls.setSelectable(true);
		tblAssetOwnDtls.setColumnCollapsingAllowed(true);
		tblAssetOwnDtls.setPageLength(6);
		tblAssetOwnDtls.setWidth("800");
		tblAssetOwnDtls.setStyleName(Runo.TABLE_SMALL);
		tblAssetOwnDtls.setImmediate(true);
		tblAssetOwnDtls.setFooterVisible(true);
		tblAssetOwnDtls.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblAssetOwnDtls.isSelected(event.getItemId())) {
					tblAssetOwnDtls.setImmediate(true);
					btnAdd.setCaption("Save");
					btnAdd.setStyleName("savebt");
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAdd.setCaption("Update");
					btnAdd.setStyleName("savebt");
					editAssetOwnDetails();
				}
			}
		});
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn1.addComponent(tfDesc);
		flColumn1.addComponent(dtStartDate);
		flColumn1.addComponent(dtEndDate);
		flColumn1.setSpacing(true);
		flColumn1.setMargin(true);
		flColumn1.addComponent(cbUsedBy);
		flColumn1.addComponent(cbStatus);
		flColumn1.addComponent(lblspace);
		flColumn1.addComponent(btnAdd);
		flColumn1.addComponent(btndelete);
		flColumn2.addComponent(tblAssetOwnDtls);
		HorizontalLayout hlUserInput = new HorizontalLayout();
		hlUserInput.setMargin(true);
		hlUserInput.addComponent(flColumn1);
		hlUserInput.addComponent(flColumn2);
		hlUserInput.setComponentAlignment(flColumn2, Alignment.MIDDLE_LEFT);
		vltable = new VerticalLayout();
		vltable.setSizeFull();
		vltable.setMargin(true);
		vltable.addComponent(hlUserInput);
		vlOwnDetails.addComponent(vltable);
		resetfields();
		loadSrchRslt(false, null);
	}
	
	// Load region list for pnladdedit's combo Box
	private void loadEmployee() {
		try {
			BeanContainer<Long, EmployeeDM> objAsserBrand = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			objAsserBrand.setBeanIdProperty("employeeid");
			objAsserBrand.addAll(servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyid, null, null,
					null, null, "P"));
			cbUsedBy.setContainerDataSource(objAsserBrand);
		}
		catch (Exception e) {
			logger.info("fn_load Employee List->" + e);
		}
	}
	
	// Method for show the details in grid table while search and normal mode
	public void loadSrchRslt(boolean fromdb, Long assetId) {
		if (fromdb) {
			usertable = serviceAssetOwnDetails.getAssetOwnDetailsList(assetId, null, "Active");
		}
		tblAssetOwnDtls.removeAllItems();
		total = usertable.size();
		beanAssetOwner = new BeanItemContainer<AssetOwnDetailsDM>(AssetOwnDetailsDM.class);
		beanAssetOwner.addAll(usertable);
		tblAssetOwnDtls.setContainerDataSource(beanAssetOwner);
		tblAssetOwnDtls.setColumnFooter("lastUpdatedBy", "No.Of Records:" + total);
		tblAssetOwnDtls.setVisibleColumns(new Object[] { "assetOwnId", "usedBy", "startDate", "endDate",
				"ownershpStatus", "lastUpdatedDate", "lastUpdatedBy" });
		tblAssetOwnDtls.setColumnHeaders(new String[] { "Ref.Id", "Used By", "Start Date", "End Date", "Status",
				"Last Updated Date", "Last Updated By" });
		tblAssetOwnDtls.setColumnAlignment("assetOwnId", Align.RIGHT);
	}
	
	private void editAssetOwnDetails() {
		if (tblAssetOwnDtls.getValue() != null) {
			AssetOwnDetailsDM enqdtl = beanAssetOwner.getItem(tblAssetOwnDtls.getValue()).getBean();
			tfDesc.setValue(enqdtl.getOwnershpDesc());
			dtStartDate.setValue(enqdtl.getStartDate());
			dtEndDate.setValue(enqdtl.getEndDate());
			cbUsedBy.setValue(enqdtl.getUsedBy());
			cbStatus.setValue(enqdtl.getOwnershpStatus());
		}
	}
	
	private boolean validation() {
		boolean errorflag = false;
		if (cbUsedBy.getValue() == null) {
			cbUsedBy.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_FIRST_NAME));
			errorflag = true;
		} else {
			cbUsedBy.setComponentError(null);
		}
		if (dtStartDate.getValue() == null) {
			dtStartDate.setComponentError(new UserError(GERPErrorCodes.START_DATE));
		} else {
			dtStartDate.setComponentError(null);
		}
		if ((dtStartDate.getValue() != null) && (dtEndDate.getValue() != null)) {
			if (dtStartDate.getValue().after(dtEndDate.getValue())) {
				dtEndDate.setComponentError(new UserError(GERPErrorCodes.DATE_OUTOFRANGE));
				logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
						+ "Throwing ValidationException. User data is > ");
				errorflag = true;
			}
		} else {
			dtEndDate.setComponentError(null);
		}
		return errorflag;
	}
	
	private void deletedetails() {
		AssetOwnDetailsDM delete = new AssetOwnDetailsDM();
		if (tblAssetOwnDtls.getValue() != null) {
			delete = beanAssetOwner.getItem(tblAssetOwnDtls.getValue()).getBean();
			usertable.remove(delete);
			tfDesc.setValue("");
			dtStartDate.setValue(null);
			dtEndDate.setValue(null);
			cbUsedBy.setValue(null);
			loadSrchRslt(false, null);
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (btndelete == event.getButton()) {
			deletedetails();
		}
	}
	
	// Save Method for save and update the Asset Specification details
	private void saveAssetOwnerDetail() {
		AssetOwnDetailsDM asstowner = new AssetOwnDetailsDM();
		if (tblAssetOwnDtls.getValue() != null) {
			asstowner = beanAssetOwner.getItem(tblAssetOwnDtls.getValue()).getBean();
		}
		if (tfDesc.getValue().toString().trim().length() > 0) {
			asstowner.setOwnershpDesc(tfDesc.getValue());
		}
		asstowner.setStartDate(dtStartDate.getValue());
		asstowner.setEndDate(dtEndDate.getValue());
		if (cbUsedBy.getValue() != null) {
			asstowner.setUsedBy((Long) cbUsedBy.getValue());
		}
		if (cbStatus.getValue() != null) {
			asstowner.setOwnershpStatus((String) cbStatus.getValue());
		}
		asstowner.setLastUpdatedBy(username);
		asstowner.setLastUpdatedDate(DateUtils.getcurrentdate());
		usertable.add(asstowner);
		loadSrchRslt(false, assetOwnId);
	}
	
	public void saveAssetOwners(Long assetId) {
		@SuppressWarnings("unchecked")
		Collection<AssetOwnDetailsDM> itemIds = (Collection<AssetOwnDetailsDM>) tblAssetOwnDtls.getVisibleItemIds();
		for (AssetOwnDetailsDM saveasset : (Collection<AssetOwnDetailsDM>) itemIds) {
			saveasset.setAssetId(assetId);
			serviceAssetOwnDetails.saveAndUpdateAssetOwnDetails(saveasset);
		}
		loadSrchRslt(false, null);
	}
	
	public void resetfields() {
		tfDesc.setValue("");
		dtStartDate.setValue(null);
		dtEndDate.setValue(null);
		cbUsedBy.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tblAssetOwnDtls.removeAllItems();
	}
}
