/**
 * File Name	:	Vendor.java
 * Description	:	entity class for M_BASE_VENDOR table
 * Author		:	MADHU
 * Date			:	JULY 03 , 2014
 *
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of  GNTS Technologies pvt. ltd.
 * Version       Date           	Modified By              Remarks
 * 0.1           JULY 03 , 2014      MADHU		         Initial Version
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CityDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.domain.mst.VendorDM;
import com.gnts.base.domain.mst.VendorTypeDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CityService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.StateService;
import com.gnts.base.service.mst.VendorService;
import com.gnts.base.service.mst.VendorTypeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.erputil.validations.PhoneNumberValidation;
import com.gnts.mms.domain.mst.MaterialDM;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Table.Align;

public class Vendor extends BaseUI {
	// Bean creation
	private VendorService serviceVendor = (VendorService) SpringContextHelper.getBean("Vendor");
	private VendorTypeService serviceVendorType = (VendorTypeService) SpringContextHelper.getBean("vendorType");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private CityService serviceCity = (CityService) SpringContextHelper.getBean("city");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfVendorName, tfVendorCode, tfContactName, tfcontactno, tfDesignation, tfVendorRating, tfPinCode,
			tfEmail, tfRegNo, tfStNo, tfTanNo, tfPaymentTerm, tfED, tfPackingPercent, tfwarrantyType, tfHED, tfCST,
			tfDeliveryPeriod, tfCESS, tfVAT, tfFright, tfACNo, tfACType, tfBankName;
	private TextArea taAddress, taBankAddress;
	private CheckBox ckDutyExempt, ckCForm;
	private ComboBox cbStatus, cbVendorTypeName, cbBranch, cbCountry, cbState, cbCity;
	// BeanItemContainer
	private BeanItemContainer<VendorDM> beanVendorDM = null;
	private BeanContainer<Long, BranchDM> beanBranchDM = null;
	private BeanContainer<Long, VendorTypeDM> beanVendorTypeDM = null;
	private BeanContainer<Long, VendorTypeDM> beanvendrdm = null;
	// local variables declaration
	private Long companyid;
	private String departId, pkvendorId;
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(Vendor.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Vendor() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Vendor() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Vendor UI");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Vendor Name text field
		tfVendorName = new GERPTextField("Vendor Name");
		// Vendor Type text field
		cbVendorTypeName = new GERPComboBox("Vendor Type");
		cbVendorTypeName.setWidth("150");
		cbVendorTypeName.setItemCaptionPropertyId("vendortypename");
		loadVendorTypeList();
		// VendorCode text field
		tfVendorCode = new GERPTextField("Vendor Code");
		// VendorRating text field
		tfVendorRating = new GERPTextField("Vendor Rating");
		// ContactName text field
		tfContactName = new GERPTextField("Contact Name");
		// ContactName text field
		tfcontactno = new GERPTextField("Contact Number");
		tfcontactno.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfcontactno.setComponentError(null);
				if (tfcontactno.getValue() != null) {
					if (!tfcontactno.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
						tfcontactno.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
					} else {
						tfcontactno.setComponentError(null);
					}
				}
			}
		});
		// Designation text field
		tfDesignation = new GERPTextField("Designation");
		// Address text field
		taAddress = new TextArea("Address");
		taAddress.setWidth("150");
		taAddress.setHeight("100");
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		// Country text field
		cbCountry = new GERPComboBox("Country");
		cbCountry.setItemCaptionPropertyId("countryName");
		loadCountryList();
		cbCountry.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadStateList();
				}
			}
		});
		// State text field
		cbState = new GERPComboBox("State");
		cbState.setItemCaptionPropertyId("stateName");
		cbState.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadCityList();
				}
			}
		});
		// City text field
		cbCity = new GERPComboBox("City");
		cbCity.setItemCaptionPropertyId("cityname");
		// tfPinCode text field
		tfPinCode = new GERPTextField("Pin Code");
		// Email text field
		tfEmail = new GERPTextField("Email-ID");
		// RegNo text field
		tfRegNo = new GERPTextField("Reg No.");
		// ST No. text field
		tfStNo = new GERPTextField("ST No.");
		// Tin No. text field
		tfTanNo = new GERPTextField("TAN No.");
		// Payment Terms text field
		tfPaymentTerm = new GERPTextField("Payment Terms");
		// ED text field
		tfED = new GERPTextField("ED(%)");
		// Packing Percent text field
		tfPackingPercent = new GERPTextField("Packing(%)");
		// WarrantyType text field
		tfwarrantyType = new GERPTextField("Warranty Type");
		// HER text field
		tfHED = new GERPTextField("HED(%)");
		// CST text field
		tfCST = new GERPTextField("CST(%)");
		// Delivery Period text field
		tfDeliveryPeriod = new GERPTextField("Delivery Period");
		// CESS text field
		tfCESS = new GERPTextField("CESS(%)");
		// VAT text field
		tfVAT = new GERPTextField("VAT(%)");
		// C Form CheckBox
		ckCForm = new CheckBox("C Form");
		ckCForm.setCaption("C Form");
		// DutyExempt CheckBox
		ckDutyExempt = new CheckBox();
		ckDutyExempt.setCaption("Duty Exempt");
		// Fright text field
		tfFright = new GERPTextField("Fright");
		// Account Number TextField
		tfACNo = new GERPTextField("Account Number");
		// Account Type text field
		tfACType = new GERPTextField("Account Type");
		// Bank Name Textfield
		tfBankName = new GERPTextField("Bank Name");
		// Bank Address Textfield
		taBankAddress = new TextArea("Bank Address");
		taBankAddress.setWidth("150");
		taBankAddress.setHeight("100");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(tfVendorName);
		flColumn2.addComponent(cbBranch);
		flColumn3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Assembling User Input layout");
			// Remove all components in Search Layout
			hlSearchLayout.removeAllComponents();
			// Add components for User Input Layout
			flColumn1 = new FormLayout();
			flColumn2 = new FormLayout();
			flColumn3 = new FormLayout();
			flColumn4 = new FormLayout();
			flColumn1.addComponent(tfVendorName);
			flColumn1.addComponent(cbVendorTypeName);
			flColumn1.addComponent(tfVendorCode);
			flColumn1.addComponent(tfContactName);
			flColumn1.addComponent(tfcontactno);
			flColumn1.addComponent(tfDesignation);
			flColumn1.addComponent(tfVendorRating);
			flColumn1.addComponent(taAddress);
			flColumn2.addComponent(cbBranch);
			flColumn2.addComponent(cbCountry);
			flColumn2.addComponent(cbState);
			flColumn2.addComponent(cbCity);
			flColumn2.addComponent(tfPinCode);
			flColumn2.addComponent(tfEmail);
			flColumn2.addComponent(tfRegNo);
			flColumn2.addComponent(tfStNo);
			flColumn2.addComponent(tfTanNo);
			flColumn2.addComponent(tfPaymentTerm);
			flColumn2.addComponent(tfwarrantyType);
			flColumn3.addComponent(tfED);
			flColumn3.addComponent(tfPackingPercent);
			flColumn3.addComponent(tfHED);
			flColumn3.addComponent(tfCST);
			flColumn3.addComponent(tfDeliveryPeriod);
			flColumn3.addComponent(tfCESS);
			flColumn3.addComponent(tfVAT);
			flColumn3.addComponent(tfFright);
			flColumn3.addComponent(ckDutyExempt);
			flColumn3.addComponent(ckCForm);
			flColumn4.addComponent(tfACNo);
			flColumn4.addComponent(tfACType);
			flColumn4.addComponent(tfBankName);
			flColumn4.addComponent(taBankAddress);
			flColumn4.addComponent(cbStatus);
			hlUserInputLayout.addComponent(flColumn1);
			hlUserInputLayout.addComponent(flColumn2);
			hlUserInputLayout.addComponent(flColumn3);
			hlUserInputLayout.addComponent(flColumn4);
			hlUserInputLayout.setSizeUndefined();
			hlUserInputLayout.setMargin(true);
			hlUserInputLayout.setSpacing(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<VendorDM> vendorList = new ArrayList<VendorDM>();
		Long cityId = null;
		if (cbCity.getValue() != null) {
			cityId = ((Long.valueOf(cbCity.getValue().toString())));
		}
		Long branchId = null;
		if (cbBranch.getValue() != null) {
			branchId = ((Long.valueOf(cbBranch.getValue().toString())));
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfVendorName.getValue() + ", " + tfContactName.getValue()
				+ (String) cbStatus.getValue() + ", " + cityId);
		vendorList = serviceVendor.getVendorList(branchId, null, companyid, tfVendorName.getValue(), null, null, null,
				tfContactName.getValue(), (String) cbStatus.getValue(), cityId, "F");
		recordCnt = vendorList.size();
		beanVendorDM = new BeanItemContainer<VendorDM>(VendorDM.class);
		beanVendorDM.addAll(vendorList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Vendor. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanVendorDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "vendorId", "vendorName", "contactName", "branchName",
				"vendorstatus", "lastUpdatedBy", "lastUpdatedDt" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Vendor", "Contact", "Branch", "Status",
				"Updated By", "Updated Date" });
		tblMstScrSrchRslt.setColumnAlignment("vendorId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfVendorName.setValue("");
		tfVendorName.setComponentError(null);
		cbVendorTypeName.setComponentError(null);
		cbBranch.setComponentError(null);
		cbCountry.setComponentError(null);
		cbState.setComponentError(null);
		cbCity.setComponentError(null);
		tfVendorCode.setValue("");
		cbVendorTypeName.setValue(null);
		tfcontactno.setValue("");
		tfcontactno.setComponentError(null);
		tfContactName.setValue("");
		tfDesignation.setValue("");
		tfVendorRating.setValue("");
		taAddress.setValue("");
		cbBranch.setValue(null);
		cbCountry.setValue(null);
		cbState.setValue(null);
		cbCity.setValue(null);
		tfPinCode.setValue("");
		tfEmail.setValue("");
		tfRegNo.setValue("");
		tfStNo.setValue("");
		tfTanNo.setValue("");
		tfPaymentTerm.setValue("");
		tfED.setValue("");
		tfPackingPercent.setValue("");
		tfwarrantyType.setValue("");
		tfHED.setValue("");
		tfCST.setValue("");
		tfDeliveryPeriod.setValue("");
		tfCESS.setValue("");
		tfVAT.setValue("");
		tfFright.setValue("");
		ckDutyExempt.setValue(false);
		ckCForm.setValue(false);
		tfACNo.setValue("");
		tfACType.setValue("");
		tfBankName.setValue("");
		taBankAddress.setValue("");
		tfEmail.setComponentError(null);
		tfVendorName.setComponentError(null);
		tfcontactno.setComponentError(null);
		cbVendorTypeName.setComponentError(null);
		cbBranch.setComponentError(null);
		cbCountry.setComponentError(null);
		cbState.setComponentError(null);
		cbCity.setComponentError(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editVendor() {
		Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		VendorDM editVendor = beanVendorDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		pkvendorId = editVendor.getVendorId().toString();
		if (itselect.getItemProperty("vendorName") != null && !"null".equals(itselect.getItemProperty("vendorName"))) {
			tfVendorName.setValue(itselect.getItemProperty("vendorName").getValue().toString());
		}
		if (editVendor.getVendorCode() != null) {
			tfVendorCode.setValue(itselect.getItemProperty("vendorCode").getValue().toString());
		}
		if (editVendor.getVendorrating() != null) {
			tfVendorRating.setValue(itselect.getItemProperty("vendorrating").getValue().toString());
		}
		if (editVendor.getVendorAddress() != null) {
			taAddress.setValue(itselect.getItemProperty("vendorAddress").getValue().toString());
		}
		if (editVendor.getVendorPostcode() != null) {
			tfPinCode.setValue(itselect.getItemProperty("vendorPostcode").getValue().toString());
		}
		if (editVendor.getContactName() != null) {
			tfContactName.setValue(itselect.getItemProperty("contactName").getValue().toString());
		}
		if (editVendor.getContactNo() != null) {
			tfcontactno.setValue(itselect.getItemProperty("contactNo").getValue().toString());
		}
		if (editVendor.getDesidnation() != null) {
			tfDesignation.setValue(itselect.getItemProperty("desidnation").getValue().toString());
		}
		if (editVendor.getEmailId() != null) {
			tfEmail.setValue(itselect.getItemProperty("emailId").getValue().toString());
		}
		if (editVendor.getRegNo() != null) {
			tfRegNo.setValue(itselect.getItemProperty("regNo").getValue().toString());
		}
		if (editVendor.getTanNo() != null) {
			tfTanNo.setValue(itselect.getItemProperty("tanNo").getValue().toString());
		}
		if (editVendor.getStNo() != null) {
			tfStNo.setValue(itselect.getItemProperty("stNo").getValue().toString());
		}
		if (editVendor.getPaymentTerm() != null) {
			tfPaymentTerm.setValue(itselect.getItemProperty("paymentTerm").getValue().toString());
		}
		if (editVendor.getWarrentyType() != null) {
			tfwarrantyType.setValue(itselect.getItemProperty("warrentyType").getValue().toString());
		}
		if (editVendor.getDeliveryPeriod() != null) {
			tfDeliveryPeriod.setValue(itselect.getItemProperty("deliveryPeriod").getValue().toString());
		}
		if (editVendor.getFreightPrnct() != null) {
			tfFright.setValue(itselect.getItemProperty("freightPrnct").getValue().toString());
		}
		if (editVendor.getEdPrnct() != null) {
			tfED.setValue(itselect.getItemProperty("edPrnct").getValue().toString());
		}
		if (editVendor.getHedPrnct() != null) {
			tfHED.setValue(itselect.getItemProperty("hedPrnct").getValue().toString());
		}
		if (editVendor.getCessPrnct() != null) {
			tfCESS.setValue(itselect.getItemProperty("cessPrnct").getValue().toString());
		}
		if (editVendor.getCstPrnct() != null) {
			tfCST.setValue(itselect.getItemProperty("cstPrnct").getValue().toString());
		}
		if (editVendor.getVatPrnct() != null) {
			tfVAT.setValue(itselect.getItemProperty("vatPrnct").getValue().toString());
		}
		if (editVendor.getPackingPrnct() != null) {
			tfPackingPercent.setValue(itselect.getItemProperty("packingPrnct").getValue().toString());
		}
		if (editVendor.getBankName() != null) {
			tfBankName.setValue(itselect.getItemProperty("bankName").getValue().toString());
		}
		if (editVendor.getBankAddress() != null) {
			taBankAddress.setValue(itselect.getItemProperty("bankAddress").getValue().toString());
		}
		if (editVendor.getBankACNO() != null) {
			tfACNo.setValue(itselect.getItemProperty("bankACNO").getValue().toString());
		}
		if (editVendor.getBankACType() != null) {
			tfACType.setValue(itselect.getItemProperty("bankACType").getValue().toString());
		}
		if (ckCForm.getValue().equals(true)) {
			editVendor.setCformREQD("Y");
		} else if (ckCForm.getValue().equals(false)) {
			editVendor.setCformREQD("N");
		}
		if (ckDutyExempt.getValue() != null) {
			if (editVendor.getDutyExempt().equals("Y")) {
				ckDutyExempt.setValue(true);
			} else {
				ckDutyExempt.setValue(false);
			}
		}
		if (editVendor.getCformREQD().equals("Y")) {
			ckCForm.setValue(true);
		} else {
			ckCForm.setValue(false);
		}
		cbStatus.setValue(itselect.getItemProperty("vendorstatus").getValue());
		cbCountry.setValue(Long.valueOf(editVendor.getcountryID()));
		cbBranch.setValue(Long.valueOf(editVendor.getBranchId()));
		cbState.setValue(Long.valueOf(editVendor.getStateId()).toString());
		cbVendorTypeName.setValue(Long.valueOf(editVendor.getVendorTypeId()).toString());
		cbCity.setValue(Long.valueOf(editVendor.getCityId()).toString());
		System.out.println("vendor name"+cbVendorTypeName.getValue());
		/*Long matId = editVendor.getVendorTypeId();
		Collection<?> empColId = cbVendorTypeName.getItemIds();
		for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
			Object itemIdClient = (Object) iteratorclient.next();
			BeanItem<?> itemclient = (BeanItem<?>) cbVendorTypeName.getItem(itemIdClient);
			// Get the actual bean and use the data
			VendorTypeDM matObj = (VendorTypeDM) itemclient.getBean();
			if (matId != null && matId.equals(matObj.getVendorid())) {
				cbVendorTypeName.setValue(itemIdClient);
			}
		}
		*/
		tfcontactno.setComponentError(null);
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
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		tfVendorName.setValue("");
		cbBranch.setValue(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		// reset the field valued to default
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfVendorName.setRequired(true);
		cbVendorTypeName.setRequired(true);
		cbBranch.setRequired(true);
		cbCountry.setRequired(true);
		cbState.setRequired(true);
		cbCity.setRequired(true);
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Dept. ID " + departId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_VENDOR);
		UI.getCurrent().getSession().setAttribute("audittablepk", pkvendorId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfVendorName.setRequired(false);
		cbVendorTypeName.setRequired(false);
		cbBranch.setRequired(false);
		cbCountry.setRequired(false);
		cbState.setRequired(false);
		cbCity.setRequired(false);
		cbVendorTypeName.setComponentError(null);
		tfcontactno.setComponentError(null);
		cbBranch.setComponentError(null);
		cbCountry.setComponentError(null);
		cbState.setComponentError(null);
		cbCity.setComponentError(null);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		tfVendorName.setRequired(true);
		cbVendorTypeName.setRequired(true);
		cbBranch.setRequired(true);
		cbCountry.setRequired(true);
		cbState.setRequired(true);
		cbCity.setRequired(true);
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editVendor();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		//try{
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		errorFlag = false;
		tfVendorName.setComponentError(null);
		cbVendorTypeName.setComponentError(null);
		cbBranch.setComponentError(null);
		cbCountry.setComponentError(null);
		cbState.setComponentError(null);
		cbCity.setComponentError(null);
		tfcontactno.setComponentError(null);
		if (cbVendorTypeName.getValue() == null) {
			cbVendorTypeName.setComponentError(new UserError(GERPErrorCodes.NULL_VENDORTYPE_NAME));
			errorFlag = true;
		}
		if ((tfVendorName.getValue() == null) || tfVendorName.getValue().trim().length() == 0) {
			tfVendorName.setComponentError(new UserError(GERPErrorCodes.NULL_VENDOR_NAME));
			errorFlag = true;
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfVendorName.getValue());
		}
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_BRANCH_NAME));
			errorFlag = true;
		}
		if (cbCountry.getValue() == null) {
			cbCountry.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_COUNTRY));
			errorFlag = true;
		}
		if (cbState.getValue() == null) {
			cbState.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_STATE));
			errorFlag = true;
		}
		/*if (tfcontactno.getValue().toString() == null) {
			tfcontactno.setComponentError(new UserError(GERPErrorCodes.NULL_PHONE_NUMBER));
			// errorFlag = true;
		} else if (tfcontactno.getValue() != null) {
			if (!tfcontactno.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
				tfcontactno.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
				errorFlag = true;
			}
		}*/
		if (cbCity.getValue() == null) {
			cbCity.setComponentError(new UserError(GERPErrorCodes.NULL_CITY_NAME));
			errorFlag = true;
		}
		String emailseq = tfEmail.getValue().toString();
		if (emailseq.contains("@") && emailseq.contains(".") || emailseq.equals("")) {
			tfEmail.setComponentError(null);
		} else {
			tfEmail.setComponentError(new UserError(GERPErrorCodes.EMAIL_VALIDATION));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
		
		/*catch (Exception e) {
			e.printStackTrace();
		}
	*/
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		try{
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		
			VendorDM vendorObj = new VendorDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				vendorObj = beanVendorDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			vendorObj.setVendorName(tfVendorName.getValue());
			vendorObj.setVendorCode(tfVendorCode.getValue());
			vendorObj.setVendorrating(tfVendorRating.getValue());
			vendorObj.setVendorAddress(taAddress.getValue());
			if (cbBranch.getValue() != null) {
				vendorObj.setBranchId((Long) cbBranch.getValue());
			}
			if (cbVendorTypeName.getValue() != null) {
				vendorObj.setVendorTypeId(Long.valueOf(cbVendorTypeName.getValue().toString()));
				vendorObj.setVendorTypeName((String) cbVendorTypeName.getValue());
			}
			// vendorObj.setVendorTypeName(cbVendorTypeName.getValue().toString());
			
			if (tfPinCode.getValue() != null && tfPinCode.getValue().trim().length() > 0) {
				vendorObj.setVendorPostcode((Long.valueOf(tfPinCode.getValue())));
			}
			if (cbCountry.getValue() != null) {
				vendorObj.setcountryID((Long) cbCountry.getValue());
			}
			if (cbState.getValue() != null) {
				vendorObj.setStateId(Long.valueOf(cbState.getValue().toString()));
			}
			if (cbCity.getValue() != null) {
				vendorObj.setCityId((Long.valueOf(cbCity.getValue().toString())));
			}
			vendorObj.setContactName(tfContactName.getValue());
			vendorObj.setContactNo(tfcontactno.getValue());
			vendorObj.setDesidnation(tfDesignation.getValue());
			vendorObj.setBankName(tfBankName.getValue());
			vendorObj.setEmailId(tfEmail.getValue());
			vendorObj.setRegNo(tfRegNo.getValue());
			vendorObj.setTanNo(tfTanNo.getValue());
			vendorObj.setStNo(tfStNo.getValue());
			vendorObj.setPaymentTerm(tfPaymentTerm.getValue());
			vendorObj.setWarrentyType(tfwarrantyType.getValue());
			vendorObj.setDeliveryPeriod(tfDeliveryPeriod.getValue());
			if (tfFright.getValue() != null && tfFright.getValue().trim().length() > 0) {
				vendorObj.setFreightPrnct((Long.valueOf(tfFright.getValue())));
			}
			if (tfED.getValue() != null && tfED.getValue().trim().length() > 0) {
				vendorObj.setEdPrnct((Long.valueOf(tfED.getValue())));
			}
			if (tfHED.getValue() != null && tfHED.getValue().trim().length() > 0) {
				vendorObj.setHedPrnct((Long.valueOf(tfHED.getValue())));
			}
			if (tfCESS.getValue() != null && tfCESS.getValue().trim().length() > 0) {
				vendorObj.setCessPrnct((Long.valueOf(tfCESS.getValue())));
			}
			if (tfCST.getValue() != null && tfCST.getValue().trim().length() > 0) {
				vendorObj.setCstPrnct((Long.valueOf(tfCST.getValue())));
			}
			if (tfVAT.getValue() != null && tfVAT.getValue().trim().length() > 0) {
				vendorObj.setVatPrnct((Long.valueOf(tfVAT.getValue())));
			}
			if (tfPackingPercent.getValue() != null && tfPackingPercent.getValue().trim().length() > 0) {
				vendorObj.setPackingPrnct((Long.valueOf(tfPackingPercent.getValue())));
			}
			vendorObj.setBankAddress(taBankAddress.getValue());
			if (tfACNo.getValue() != null && tfACNo.getValue().trim().length() > 0) {
				vendorObj.setBankACNO((Long.valueOf(tfACNo.getValue())));
			}
			vendorObj.setBankACType(tfACType.getValue());
			if (ckDutyExempt.getValue().equals(true)) {
				vendorObj.setDutyExempt("Y");
			} else if (ckDutyExempt.getValue().equals(false)) {
				vendorObj.setDutyExempt("N");
			}
			if (ckCForm.getValue().equals(true)) {
				vendorObj.setCformREQD("Y");
			} else if (ckCForm.getValue().equals(false)) {
				vendorObj.setCformREQD("N");
			}
			vendorObj.setCompanyId(companyid);
			if (cbStatus.getValue() != null) {
				vendorObj.setVendorstatus((String) cbStatus.getValue());
			}
			vendorObj.setLastUpdatedDt(DateUtils.getcurrentdate());
			vendorObj.setLastUpdatedBy(username);
			serviceVendor.saveDetails(vendorObj);
			resetFields();
			loadSrchRslt();
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadCountryList() {
		List<CountryDM> countryList = new ArrayList<CountryDM>();
		countryList.addAll(serviceCountry.getCountryList(null, null, null, null, "Active", "P"));
		BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
		beanCountry.setBeanIdProperty("countryID");
		beanCountry.addAll(countryList);
		cbCountry.setContainerDataSource(beanCountry);
	}
	
	private void loadStateList() {
		List<StateDM> getStateList = new ArrayList<StateDM>();
		getStateList.addAll(serviceState.getStateList(null, "Active", (Long) cbCountry.getValue(), null, "P"));
		BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
		beanState.setBeanIdProperty("stateId");
		beanState.addAll(getStateList);
		cbState.setContainerDataSource(beanState);
	}
	
	private void loadCityList() {
		List<CityDM> getCityList = new ArrayList<CityDM>();
		getCityList.addAll(serviceCity.getCityList(null, null, ((Long.valueOf((String) cbState.getValue()))), "Active",
				null, "P"));
		BeanContainer<Long, CityDM> beanCity = new BeanContainer<Long, CityDM>(CityDM.class);
		beanCity.setBeanIdProperty("cityid");
		beanCity.addAll(getCityList);
		cbCity.setContainerDataSource(beanCity);
	}
	
	public void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<BranchDM> branchlist = serviceBranch.getBranchList(null, null, null, null, companyid, "P");
		beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranchDM.setBeanIdProperty("branchId");
		beanBranchDM.addAll(branchlist);
		cbBranch.setContainerDataSource(beanBranchDM);
	}
	
	/*
	 * public void loadVendorTypeList() { logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
	 * + "Loading Search..."); List<VendorTypeDM> vendorTypelist = serviceVendorType.getVendorTypeList(null, null, null,
	 * companyid); beanVendorTypeDM = new BeanContainer<Long, VendorTypeDM>(VendorTypeDM.class);
	 * beanVendorTypeDM.setBeanIdProperty("vendorid"); beanVendorTypeDM.addAll(vendorTypelist);
	 * cbVendorTypeName.setContainerDataSource(beanVendorTypeDM); }
	 */
	public void loadVendorTypeList() {
		List<VendorTypeDM> vendorTypelist = serviceVendorType.getVendorTypeList(null, null, null, companyid);
		beanvendrdm = new BeanContainer<Long, VendorTypeDM>(VendorTypeDM.class);
		beanvendrdm.setBeanIdProperty("vendorid");
		beanvendrdm.addAll(vendorTypelist);
		cbVendorTypeName.setContainerDataSource(beanvendrdm);
	}
}
