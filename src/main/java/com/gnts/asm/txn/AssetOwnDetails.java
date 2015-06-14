package com.gnts.asm.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.txn.AssetOwnDetailsDM;
import com.gnts.asm.service.txn.AssetOwnDetailsService;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.crm.domain.txn.CommentsDM;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
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
	VerticalLayout vlAddEditPanel = new VerticalLayout();
	VerticalLayout vlTablePanel = new VerticalLayout();
	HorizontalLayout hlsavecancel = new HorizontalLayout();
	public HorizontalLayout hlHeader = new HorizontalLayout();
	// Table Declaration
	public Table tblAssetOwnDtls;
	private Button btnAdd;
	public Button btnSave,btndelete;
	public Button btnCancel;
	Button btnSearch, btnReset;
	private Label lblspace;
	List<AssetOwnDetailsDM> usertable = new ArrayList<AssetOwnDetailsDM>();
	// Declaration for Label
	private BeanItemContainer<AssetOwnDetailsDM> beanAssetOwner = null;
	private VerticalLayout vltable;
	HorizontalLayout hlTableTitleandCaptionLayout, hlSearchLayout;
	private FormLayout flColumn1, flColumn2;
	private String username;
	private Long companyid, assetOwnId;
	AssetOwnDetailsService serviceAssetOwnDetails = (AssetOwnDetailsService) SpringContextHelper
			.getBean("assetOwnDetails");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private static Logger logger = Logger.getLogger(AssetOwnDetails.class);
	private int total;
	
	public AssetOwnDetails(VerticalLayout vlOwnDetails, Long assetOwnId) {
		// this.assetId=assetId;
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
				System.out.println("validation()-->"+validation());
				if (validation()==false) {
					saveAssetOwnerDetail();
					//loadSrchRslt(false, null);
				}
			}
		});
		btndelete=new GERPButton("Delete", "delete",this);
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
		HorizontalLayout Input = new HorizontalLayout();
		Input.setMargin(true);
		// Input.setWidth("1100");
		Input.addComponent(flColumn1);
		Input.addComponent(flColumn2);
		// Input.addComponent(flColumn3);
		Input.setComponentAlignment(flColumn2, Alignment.MIDDLE_LEFT);
		// Input.setSpacing(true);
		hlTableTitleandCaptionLayout = new HorizontalLayout();
		vltable = new VerticalLayout();
		vltable.setSizeFull();
		vltable.setMargin(true);
		vltable.addComponent(/* GERPPanelGenerator.createPanel */(Input));
		// vltable.addComponent(hlTableTitleandCaptionLayout);
		// vlOwnDetails.addComponent(vlTablePanel);
		vlOwnDetails.addComponent(vltable);
		resetfields();
		loadSrchRslt(false, null);
	}
	
	// Load region list for pnladdedit's combo Box
	private void loadEmployee() {
		try {
			List<EmployeeDM> list = servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyid, null,
					null, null, null, "F");
			BeanContainer<Long, EmployeeDM> objAsserBrand = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			objAsserBrand.setBeanIdProperty("employeeid");
			objAsserBrand.addAll(list);
			cbUsedBy.setContainerDataSource(objAsserBrand);
		}
		catch (Exception e) {
			logger.info("fn_load Employee List->" + e);
		}
	}
	
	// Method for show the details in grid table while search and normal mode
	public void loadSrchRslt(boolean fromdb, Long assetId) {
		System.out.println("fromdb--->"+fromdb+"\nassetId-->"+assetId);
		if (fromdb) {
			usertable = serviceAssetOwnDetails.getAssetOwnDetailsList(assetId, null, "Active");
		}
		tblAssetOwnDtls.removeAllItems();
		total = usertable.size();
		System.out.println(" Asset own List Size" + total);
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
		Item itselect = tblAssetOwnDtls.getItem(tblAssetOwnDtls.getValue());
		if (itselect != null) {
			AssetOwnDetailsDM enqdtl = beanAssetOwner.getItem(tblAssetOwnDtls.getValue()).getBean();
			tfDesc.setValue(enqdtl.getOwnershpDesc());
			dtStartDate.setValue(enqdtl.getStartDate());
			dtEndDate.setValue(enqdtl.getEndDate());
			cbUsedBy.setValue(enqdtl.getUsedBy());
			cbStatus.setValue(cbStatus.getItemIds().iterator().next());
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
			delete=beanAssetOwner.getItem(tblAssetOwnDtls.getValue()).getBean();
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
		if(btndelete==event.getButton())
		{
			deletedetails();
		}
	}
	
	// Save Method for save and update the Asset Specification details
	private void saveAssetOwnerDetail() {
		//validation();
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
	
	public void Assetownersave(Long assetId) {
		System.out.println("saveid1-->>" + assetId);
		@SuppressWarnings("unchecked")
		Collection<AssetOwnDetailsDM> itemIds = (Collection<AssetOwnDetailsDM>) tblAssetOwnDtls.getVisibleItemIds();
		for (AssetOwnDetailsDM saveasset : (Collection<AssetOwnDetailsDM>) itemIds) {
			saveasset.setAssetId(assetId);
			System.out.println("saveid2-->>" + assetId);
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
