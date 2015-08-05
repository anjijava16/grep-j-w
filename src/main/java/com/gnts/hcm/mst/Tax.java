/**
 * File Name 		: Tax.java 
 * Description 		: this class is used for add/edit Tax details. 
 * Author 			: MADHU T 
 * Date 			: 23-July-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         23-July-2014        	MADHU T		        Initial Version
 * 
 */
package com.gnts.hcm.mst;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.ParameterService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.TaxDM;
import com.gnts.hcm.domain.mst.TaxSlapDM;
import com.gnts.hcm.service.mst.TaxService;
import com.gnts.hcm.service.mst.TaxSlapService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class Tax extends BaseUI {
	// Bean Creation
	private TaxService serviceTax = (TaxService) SpringContextHelper.getBean("Tax");
	private TaxSlapService serviceTaxslap = (TaxSlapService) SpringContextHelper.getBean("TaxSlap");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private ParameterService serviceParameter = (ParameterService) SpringContextHelper.getBean("parameter");
	private List<TaxSlapDM> taxSlapList = new ArrayList<TaxSlapDM>();
	// form layout for input controls
	private FormLayout fltaxCol1, fltaxCol2, fltaxCol3, flColumn4, flTaxslapCol1, flTaxslapCol2, flTaxslapCol3,
			flTaxslapCol4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlTax = new HorizontalLayout();
	private HorizontalLayout hlTaxslap = new HorizontalLayout();
	private VerticalLayout vlTax, vlTaxAndTaxslap;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Work Order Details
	private Button btnAddTaxslap = new GERPButton("Add", "addbt", this);
	private TextField tffinanceYr = new GERPTextField("Finance Year");
	private ComboBox cbGender, cbStatus;
	private ComboBox cbTaxStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private TextField tfTaxName, tfEarnAmtTo, tfEarnAmtFrm, tfTaxAmt, tfTaxPer;
	private Table tblTaxslap;
	private BeanItemContainer<TaxDM> beanTaxDM = null;
	private BeanItemContainer<TaxSlapDM> beanTaxslapDM = null;
	// local variables declaration
	private String taxSlapId;
	private Long companyid;
	private Long taxId;
	private int recordCnt = 0;
	private String username;
	private Long earnAmtfrm = 0L;
	private Long earnAmtTo = 0L;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(Tax.class);
	private static final long serialVersionUID = 1L;
	private Button btnDelete = new GERPButton("Delete", "delete", this);
	
	// Constructor received the parameters from Login UI class
	public Tax() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Tax() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		// Initialization for work order Details user input components
		btnAddTaxslap.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				saveTaxslapListDetails();
			}
		});
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteDtls();
			}
		});
		tblTaxslap = new GERPTable();
		tblTaxslap.setPageLength(10);
		tblTaxslap.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblTaxslap.isSelected(event.getItemId())) {
					tblTaxslap.setImmediate(true);
					btnAddTaxslap.setCaption("Add");
					btnAddTaxslap.setStyleName("savebt");
					taxSlapResetField();
					btnDelete.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddTaxslap.setCaption("Update");
					btnAddTaxslap.setStyleName("savebt");
					editTaxslapDetails();
					btnDelete.setEnabled(true);
				}
			}
		});
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Tax Name text field
		tfTaxName = new GERPTextField("Tax Name");
		tfTaxName.setMaxLength(25);
		// Gender Combo Box
		cbGender = new GERPComboBox("Gender");
		cbGender.setItemCaptionPropertyId("lookupname");
		loadGenderType();
		// Finance Year TextField
		tffinanceYr.setValue(serviceParameter.getParameterValue("FM_FINYEAR", companyid, null));
		tffinanceYr.setReadOnly(true);
		// Earn amount From Text field
		tfEarnAmtFrm = new GERPTextField("Earn Amount From");
		tfEarnAmtFrm.setValue("0");
		// Earn amount To Text field
		tfEarnAmtTo = new GERPTextField("Earn Amount To");
		tfEarnAmtTo.setValue("0");
		// Tax Percent Text field
		tfTaxPer = new GERPTextField("Tax Percent");
		// Tax amount Text Field
		tfTaxAmt = new GERPTextField("Tax Amount");
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadTaxslapRslt();
		btnAddTaxslap.setStyleName("add");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		fltaxCol1 = new FormLayout();
		fltaxCol2 = new FormLayout();
		fltaxCol3 = new FormLayout();
		flColumn4 = new FormLayout();
		fltaxCol1.addComponent(tfTaxName);
		fltaxCol2.addComponent(cbTaxStatus);
		hlSearchLayout.addComponent(fltaxCol1);
		hlSearchLayout.addComponent(fltaxCol2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
		tfTaxName.setRequired(false);
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		fltaxCol1 = new FormLayout();
		fltaxCol2 = new FormLayout();
		fltaxCol3 = new FormLayout();
		flColumn4 = new FormLayout();
		fltaxCol1.addComponent(tfTaxName);
		fltaxCol2.addComponent(tffinanceYr);
		fltaxCol3.addComponent(cbGender);
		flColumn4.addComponent(cbTaxStatus);
		hlTax = new HorizontalLayout();
		hlTax.addComponent(fltaxCol1);
		hlTax.addComponent(fltaxCol2);
		hlTax.addComponent(fltaxCol3);
		hlTax.addComponent(flColumn4);
		hlTax.setSpacing(true);
		hlTax.setMargin(true);
		// Adding TaxSlap components
		// Add components for User Input Layout
		flTaxslapCol1 = new FormLayout();
		flTaxslapCol2 = new FormLayout();
		flTaxslapCol3 = new FormLayout();
		flTaxslapCol4 = new FormLayout();
		flTaxslapCol1.addComponent(tfEarnAmtFrm);
		flTaxslapCol1.addComponent(tfEarnAmtTo);
		flTaxslapCol2.addComponent(tfTaxPer);
		flTaxslapCol2.addComponent(tfTaxAmt);
		flTaxslapCol3.addComponent(cbStatus);
		flTaxslapCol4.addComponent(btnAddTaxslap);
		flTaxslapCol4.addComponent(btnDelete);
		hlTaxslap = new HorizontalLayout();
		hlTaxslap.addComponent(flTaxslapCol1);
		hlTaxslap.addComponent(flTaxslapCol2);
		hlTaxslap.addComponent(flTaxslapCol3);
		hlTaxslap.addComponent(flTaxslapCol4);
		hlTaxslap.setSpacing(true);
		hlTaxslap.setMargin(true);
		vlTax = new VerticalLayout();
		vlTax.addComponent(hlTaxslap);
		vlTax.addComponent(tblTaxslap);
		vlTax.setSpacing(true);
		vlTaxAndTaxslap = new VerticalLayout();
		vlTaxAndTaxslap.addComponent(GERPPanelGenerator.createPanel(hlTax));
		vlTaxAndTaxslap.addComponent(GERPPanelGenerator.createPanel(vlTax));
		vlTaxAndTaxslap.setSpacing(true);
		vlTaxAndTaxslap.setWidth("100%");
		hlUserInputLayout.addComponent(vlTaxAndTaxslap);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<TaxDM> taxList = new ArrayList<TaxDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfTaxName.getValue() + ", " + cbTaxStatus.getValue());
		taxList = serviceTax.getTaxList(companyid, null, tfTaxName.getValue(), (String) cbTaxStatus.getValue(), "F");
		recordCnt = taxList.size();
		beanTaxDM = new BeanItemContainer<TaxDM>(TaxDM.class);
		beanTaxDM.addAll(taxList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Tax. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanTaxDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "taxid", "taxname", "taxstatus", "lastupdateddt",
				"lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Tax Name", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("taxid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadTaxslapRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			logger.info("Company ID : " + companyid + " | saveTaxslapListDetails User Name : " + username + " > "
					+ "Search Parameters are " + companyid + ", " + tfEarnAmtTo.getValue() + ", "
					+ tfEarnAmtFrm.getValue() + (String) cbStatus.getValue() + ", " + taxId);
			recordCnt = taxSlapList.size();
			beanTaxslapDM = new BeanItemContainer<TaxSlapDM>(TaxSlapDM.class);
			beanTaxslapDM.addAll(taxSlapList);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxslap. result set");
			tblTaxslap.setContainerDataSource(beanTaxslapDM);
			tblTaxslap.setVisibleColumns(new Object[] { "taxSlabId", "earnAmtFrm", "earnAmtTo", "taxPer", "taxAmt",
					"status", "lastUpdatedBy", "lastUpdatedDate" });
			tblTaxslap.setColumnHeaders(new String[] { "Ref.Id", "Earn Amount From", "Earn Amount To", "Tax Percent",
					"Tax Amount", "Status", "Last Updated By", "Last Updated Date" });
			tblTaxslap.setColumnAlignment("taxSlabId", Align.RIGHT);
			tblTaxslap.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfTaxName.setValue("");
		tfTaxName.setComponentError(null);
		tfEarnAmtTo.setComponentError(null);
		tffinanceYr.setReadOnly(false);
		tffinanceYr.setValue(serviceParameter.getParameterValue("FM_FINYEAR", companyid, null));
		tffinanceYr.setReadOnly(true);
		cbTaxStatus.setValue(cbTaxStatus.getItemIds().iterator().next());
		taxSlapList = new ArrayList<TaxSlapDM>();
		tblTaxslap.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editTaxDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			TaxDM editTax = beanTaxDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			taxId = editTax.getTaxid();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Tax. Id -> "
					+ taxId);
			if (editTax.getTaxname() != null) {
				tfTaxName.setValue(editTax.getTaxname());
			}
			if (editTax.getFinyear() != null) {
				tffinanceYr.setReadOnly(false);
				tffinanceYr.setValue(editTax.getFinyear());
				tffinanceYr.setReadOnly(true);
			}
			cbTaxStatus.setValue(editTax.getTaxstatus());
			if (editTax.getGender() != null) {
				cbGender.setValue(editTax.getGender());
			}
			taxSlapList.addAll(serviceTaxslap.getTaxSlapList(null, taxId, (String) cbStatus.getValue(), "F"));
		}
		loadTaxslapRslt();
	}
	
	private void editTaxslapDetails() {
		hlUserInputLayout.setVisible(true);
		Item itselect = tblTaxslap.getItem(tblTaxslap.getValue());
		if (itselect != null) {
			if (itselect.getItemProperty("earnAmtTo").getValue() != null) {
				tfEarnAmtTo.setValue(itselect.getItemProperty("earnAmtTo").getValue().toString());
			}
			if (itselect.getItemProperty("earnAmtFrm").getValue() != null) {
				tfEarnAmtFrm.setValue(itselect.getItemProperty("earnAmtFrm").getValue().toString());
			}
			if (itselect.getItemProperty("taxAmt").getValue() != null) {
				tfTaxAmt.setValue(itselect.getItemProperty("taxAmt").getValue().toString());
			}
			if (itselect.getItemProperty("taxPer").getValue() != null) {
				tfTaxPer.setValue(itselect.getItemProperty("taxPer").getValue().toString());
			}
			cbStatus.setValue(itselect.getItemProperty("status").getValue());
		}
	}
	
	// Base class implementations
	// BaseUI searchDetails() implementation
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
		}
	}
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbTaxStatus.setValue(cbTaxStatus.getItemIds().iterator().next());
		tfTaxName.setValue("");
		loadSrchRslt();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		resetFields();
		loadTaxslapRslt();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfTaxName.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		hlCmdBtnLayout.setVisible(false);
		btnAddTaxslap.setCaption("Add");
		tblTaxslap.setVisible(true);
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Tax. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_WORKORDER_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", taxSlapId);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		taxSlapResetField();
		hlCmdBtnLayout.setVisible(true);
		tblTaxslap.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		resetFields();
		editTaxDetails();
		editTaxslapDetails();
	}
	
	private void taxSlapResetField() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfEarnAmtFrm.setValue("0");
		tfEarnAmtFrm.setComponentError(null);
		tfEarnAmtTo.setValue("0");
		tfTaxAmt.setValue("0");
		tfTaxPer.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfTaxName.setComponentError(null);
		tfEarnAmtFrm.setComponentError(null);
		tfEarnAmtTo.setComponentError(null);
		errorFlag = false;
		if ((tfTaxName.getValue() == null) || tfTaxName.getValue().trim().length() == 0) {
			tfTaxName.setComponentError(new UserError(GERPErrorCodes.NULL_TAX));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfTaxName.getValue());
			errorFlag = true;
		}
		earnAmtTo = Long.valueOf(tfEarnAmtFrm.getValue().toString());
		earnAmtfrm = Long.valueOf(tfEarnAmtTo.getValue().toString());
		if (earnAmtTo > earnAmtfrm) {
			tfEarnAmtTo.setComponentError(new UserError("Earn amount To is greater than to Earn amount From"));
			errorFlag = true;
		}
		if (tblTaxslap.size() == 0) {
			tfEarnAmtTo.setComponentError(new UserError("Enter To Earn Amount"));
			tfEarnAmtFrm.setComponentError(new UserError("Enter From Earn Amount "));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			TaxDM TaxObj = new TaxDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				TaxObj = beanTaxDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			TaxObj.setCompanyid(companyid);
			TaxObj.setTaxname(tfTaxName.getValue().toString());
			TaxObj.setFinyear(tffinanceYr.getValue().toString());
			if (cbTaxStatus.getValue() != null) {
				TaxObj.setTaxstatus((String) cbTaxStatus.getValue());
			}
			if (cbGender.getValue() != null) {
				TaxObj.setGender((String) cbGender.getValue());
			}
			TaxObj.setLastupdateddt(DateUtils.getcurrentdate());
			TaxObj.setLastupdatedby(username);
			serviceTax.saveTaxDetails(TaxObj);
			@SuppressWarnings("unchecked")
			Collection<TaxSlapDM> itemIds = (Collection<TaxSlapDM>) tblTaxslap.getVisibleItemIds();
			for (TaxSlapDM save : (Collection<TaxSlapDM>) itemIds) {
				save.setTaxId(Long.valueOf(TaxObj.getTaxid().toString()));
				serviceTaxslap.saveTaxDetails(save);
			}
			taxSlapResetField();
			resetFields();
			taxId = 0L;
			loadSrchRslt();
			loadTaxslapRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveTaxslapListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			TaxSlapDM taxSlapObj = new TaxSlapDM();
			if (tblTaxslap.getValue() != null) {
				taxSlapObj = beanTaxslapDM.getItem(tblTaxslap.getValue()).getBean();
				taxSlapList.remove(taxSlapObj);
			}
			if (tfEarnAmtTo.getValue() != null && tfEarnAmtTo.getValue().trim().length() > 0) {
				taxSlapObj.setEarnAmtTo(Long.valueOf(tfEarnAmtTo.getValue()));
			} else {
				taxSlapObj.setEarnAmtTo(new Long("0"));
			}
			if (tfEarnAmtFrm.getValue() != null && tfEarnAmtFrm.getValue().trim().length() > 0) {
				taxSlapObj.setEarnAmtFrm(Long.valueOf(tfEarnAmtFrm.getValue()));
			} else {
				taxSlapObj.setEarnAmtFrm(new Long("0"));
			}
			if (tfTaxAmt.getValue() != null && tfTaxAmt.getValue().trim().length() > 0) {
				taxSlapObj.setTaxAmt(Long.valueOf(tfTaxAmt.getValue()));
			} else {
				taxSlapObj.setTaxAmt(new Long("0"));
			}
			if (tfTaxPer.getValue() != null && tfTaxPer.getValue().trim().length() > 0) {
				taxSlapObj.setTaxPer(new BigDecimal(tfTaxPer.getValue()));
			} else {
				taxSlapObj.setTaxPer(new BigDecimal("0"));
			}
			if (cbStatus.getValue() != null) {
				taxSlapObj.setStatus((String) cbStatus.getValue());
			}
			taxSlapObj.setLastUpdatedDate(DateUtils.getcurrentdate());
			taxSlapObj.setLastUpdatedBy(username);
			taxSlapList.add(taxSlapObj);
			loadTaxslapRslt();
			btnAddTaxslap.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		taxSlapResetField();
	}
	
	/*
	 * loadGenderType()-->this function is used for load the gender type
	 */
	private void loadGenderType() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
		BeanContainer<Long, CompanyLookupDM> beanCompanyLookUp = new BeanContainer<Long, CompanyLookupDM>(
				CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("cmplookupid");
		beanCompanyLookUp.addAll(serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active", "BS_GENDER"));
		cbGender.setContainerDataSource(beanCompanyLookUp);
	}
	
	private void deleteDtls() {
		TaxSlapDM taxslapDtlObj = new TaxSlapDM();
		if (tblTaxslap.getValue() != null) {
			taxslapDtlObj = beanTaxslapDM.getItem(tblTaxslap.getValue()).getBean();
			taxSlapList.remove(taxslapDtlObj);
			taxSlapResetField();
			tblTaxslap.setValue("");
			loadTaxslapRslt();
			btnDelete.setEnabled(false);
		}
	}
}
