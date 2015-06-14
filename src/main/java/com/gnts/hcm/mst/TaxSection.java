/**
 * File Name 		: TaxSection.java 
 * Description 		: this class is used for add/edit Tax  Section details. 
 * Author 			:  KAVITHA V M 
 * Date 			: 06-August-2014	
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. 
 * All rights reserved.
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 *
 * Version       Date           	    Modified By               Remarks
 * 0.1           06-August-2014	        KAVITHA V M	              Initial Version
 * 
 */
package com.gnts.hcm.mst;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
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
import com.gnts.hcm.domain.mst.TaxSectionDM;
import com.gnts.hcm.domain.mst.TaxSlapDM;
import com.gnts.hcm.domain.mst.TaxSubSectionDM;
import com.gnts.hcm.service.mst.TaxSectionService;
import com.gnts.hcm.service.mst.TaxSubSectionService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class TaxSection extends BaseUI {
	// Bean Creation
	private TaxSectionService serviceTaxSection = (TaxSectionService) SpringContextHelper.getBean("TaxSection");
	private TaxSubSectionService serviceTaxSubSection = (TaxSubSectionService) SpringContextHelper
			.getBean("TaxSubSection");
	// form layout for input controls
	private FormLayout fltaxsectionCol1, fltaxsectionCol2, fltaxsectionCol3, fltaxsectionCol4, flTaxsubsectionCol1,
			flTaxsubsectionCol2, flTaxsubsectionCol3, flTaxsubsectionCol4;
	// Parent layout for all the input controls
	int recordCnt = 0;
	private Button btnAddTaxsubsection = new GERPButton("Add", "addbt", this);
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private ComboBox cbSectionStatus, cbsubsecnstatus;
	private TextField tfSectionCode, tfSectionLimit, tftaxlimit;
	private TextArea taSectionDesc, tasubsectndesc;
	List<TaxSubSectionDM> taxSubsectionList = new ArrayList<TaxSubSectionDM>();
	private BeanItemContainer<TaxSectionDM> beanTaxSectionDM = null;
	private BeanItemContainer<TaxSubSectionDM> beanTaxSubSectionDM = null;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// local variables declaration
	private Long companyid;
	private Long sectionId;
	private String taxSectionId;
	private String username;
	private Table tblTaxsubsubsection;
	private Logger logger = Logger.getLogger(TaxSection.class);
	private static final long serialVersionUID = 1L;
	public Button btnDelete = new GERPButton("Delete", "delete", this);
	
	// Constructor received the parameters from Login UI class
	public TaxSection() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside TaxSection() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		btnAddTaxsubsection.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDtls()) {
					saveTaxsubsectionListDetails();
				}
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
		tblTaxsubsubsection = new GERPTable();
		tblTaxsubsubsection.setImmediate(true);
		tblTaxsubsubsection.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblTaxsubsubsection.isSelected(event.getItemId())) {
					btnAddTaxsubsection.setCaption("Add");
					btnAddTaxsubsection.setStyleName("savebt");
					taxsubsectionresetfields();
					btnDelete.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddTaxsubsection.setCaption("Update");
					btnAddTaxsubsection.setStyleName("savebt");
					editTaxSubSectionDetails();
					btnDelete.setEnabled(true);
				}
			}
		});
		// Initialization for work order Details user input components
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Printing TaxSection UI");
		// Tax Section Code text field
		tfSectionCode = new GERPTextField("Section Code");
		tfSectionCode.setMaxLength(25);
		tftaxlimit = new GERPTextField("Tax Limit");
		tblTaxsubsubsection.setPageLength(12);
		cbSectionStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Section Limit Text field
		tfSectionLimit = new GERPTextField("Section Limit");
		// Section Description Text Area
		taSectionDesc = new GERPTextArea("Section Description");
		taSectionDesc.setHeight("25");
		taSectionDesc.setWidth("150");
		tasubsectndesc = new GERPTextArea("Sub Section Description");
		tasubsectndesc.setHeight("25");
		tasubsectndesc.setWidth("410");
		cbsubsecnstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadTaxsubsectionRslt();
		btnAddTaxsubsection.setStyleName("add");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlUserInputLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		fltaxsectionCol1 = new FormLayout();
		fltaxsectionCol2 = new FormLayout();
		fltaxsectionCol1.addComponent(tfSectionCode);
		fltaxsectionCol2.addComponent(cbSectionStatus);
		hlSearchLayout.addComponent(fltaxsectionCol1);
		hlSearchLayout.addComponent(fltaxsectionCol2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlUserInputLayout.removeAllComponents();
		fltaxsectionCol1 = new FormLayout();
		fltaxsectionCol2 = new FormLayout();
		fltaxsectionCol3 = new FormLayout();
		fltaxsectionCol4 = new FormLayout();
		fltaxsectionCol1.addComponent(tfSectionCode);
		fltaxsectionCol3.addComponent(tfSectionLimit);
		fltaxsectionCol2.addComponent(taSectionDesc);
		fltaxsectionCol4.addComponent(cbSectionStatus);
		HorizontalLayout hlTaxsectn = new HorizontalLayout();
		hlTaxsectn.addComponent(fltaxsectionCol1);
		hlTaxsectn.addComponent(fltaxsectionCol2);
		hlTaxsectn.addComponent(fltaxsectionCol3);
		hlTaxsectn.addComponent(fltaxsectionCol4);
		hlTaxsectn.setSpacing(true);
		hlTaxsectn.setMargin(true);
		// Add sub section
		flTaxsubsectionCol1 = new FormLayout();
		flTaxsubsectionCol2 = new FormLayout();
		flTaxsubsectionCol3 = new FormLayout();
		flTaxsubsectionCol4 = new FormLayout();
		flTaxsubsectionCol1.addComponent(tftaxlimit);
		flTaxsubsectionCol2.addComponent(tasubsectndesc);
		flTaxsubsectionCol3.addComponent(cbsubsecnstatus);
		flTaxsubsectionCol4.addComponent(btnAddTaxsubsection);
		flTaxsubsectionCol4.addComponent(btnDelete);
		HorizontalLayout hlTaxsubsection = new HorizontalLayout();
		hlTaxsubsection.addComponent(flTaxsubsectionCol1);
		hlTaxsubsection.addComponent(flTaxsubsectionCol2);
		hlTaxsubsection.addComponent(flTaxsubsectionCol3);
		hlTaxsubsection.addComponent(flTaxsubsectionCol4);
		hlTaxsubsection.setSpacing(true);
		hlTaxsubsection.setMargin(true);
		VerticalLayout vl = new VerticalLayout();
		vl.addComponent(hlTaxsubsection);
		vl.addComponent(tblTaxsubsubsection);
		VerticalLayout vlTaxsectionAndTaxsubsection = new VerticalLayout();
		vlTaxsectionAndTaxsubsection.addComponent(GERPPanelGenerator.createPanel(hlTaxsectn));
		vlTaxsectionAndTaxsubsection.addComponent(GERPPanelGenerator.createPanel(vl));
		vlTaxsectionAndTaxsubsection.setSpacing(true);
		vlTaxsectionAndTaxsubsection.setWidth("100%");
		hlUserInputLayout.addComponent(vlTaxsectionAndTaxsubsection);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<TaxSectionDM> taxsectionList = new ArrayList<TaxSectionDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfSectionCode.getValue() + ", " + cbSectionStatus.getValue());
		taxsectionList = serviceTaxSection.getTaxSectionList(null, companyid, (String) tfSectionCode.getValue(), null,
				null, (String) cbSectionStatus.getValue(), "F");
		recordCnt = taxsectionList.size();
		beanTaxSectionDM = new BeanItemContainer<TaxSectionDM>(TaxSectionDM.class);
		beanTaxSectionDM.addAll(taxsectionList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Tax Section. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanTaxSectionDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "sectionid", "sectioncode", "sectiondesc", "sectionlimit",
				"sectionstatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Section Code", "Section Description",
				"Section Limit", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("sectionid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadTaxsubsectionRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			logger.info("Company ID : " + companyid + " |saveTaxsubsectionListDetails User Name : " + username + " > "
					+ "Search Parameters are " + companyid + ", " + tasubsectndesc.getValue() + ""
					+ (String) cbsubsecnstatus.getValue() + ", " + sectionId);
			recordCnt = taxSubsectionList.size();
			beanTaxSubSectionDM = new BeanItemContainer<TaxSubSectionDM>(TaxSubSectionDM.class);
			beanTaxSubSectionDM.addAll(taxSubsectionList);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Taxsubsection. result set");
			tblTaxsubsubsection.setContainerDataSource(beanTaxSubSectionDM);
			tblTaxsubsubsection.setVisibleColumns(new Object[] { "taxlimit", "subsectndesc", "subsecnstatus",
					"lastupdateddt", "lastupdatedby" });
			tblTaxsubsubsection.setColumnHeaders(new String[] { "Tax Limit", "Sub Section Description", "Status",
					"Last Updated Date", "Last Updated By" });
			tblTaxsubsubsection.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfSectionCode.setValue("");
		tfSectionCode.setComponentError(null);
		taSectionDesc.setValue("");
		tfSectionLimit.setValue("");
		tfSectionLimit.setComponentError(null);
		cbSectionStatus.setValue(cbSectionStatus.getItemIds().iterator().next());
		cbsubsecnstatus.setValue(cbsubsecnstatus.getItemIds().iterator().next());
		taxSubsectionList = new ArrayList<TaxSubSectionDM>();
		tblTaxsubsubsection.removeAllItems();
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
			assembleSearchLayout();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfSectionCode.setValue("");
		tfSectionCode.setComponentError(null);
		taSectionDesc.setValue("");
		tfSectionLimit.setValue("");
		tftaxlimit.setValue("");
		tftaxlimit.setComponentError(null);
		cbSectionStatus.setValue(cbSectionStatus.getItemIds().iterator().next());
		tblTaxsubsubsection.removeAllItems();
		loadSrchRslt();
	}
	
	private void taxsubsectionresetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tftaxlimit.setValue("");
		tftaxlimit.setComponentError(null);
		tasubsectndesc.setValue("");
		cbsubsecnstatus.setValue(cbsubsecnstatus.getItemIds().iterator().next());
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		resetFields();
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleInputUserLayout();
		loadTaxsubsectionRslt();
		tfSectionCode.setRequired(true);
		tftaxlimit.setRequired(true);
		tfSectionLimit.setRequired(true);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddTaxsubsection.setCaption("Add");
		tblTaxsubsubsection.setVisible(true);
		tblTaxsubsubsection.removeAllItems();
		tblTaxsubsubsection.setColumnFooter("lastupdatedby", "No.of Records : " + 0);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		Boolean errorFlag = false;
		tfSectionCode.setComponentError(null);
		tftaxlimit.setComponentError(null);
		if ((tfSectionCode.getValue() == null) || tfSectionCode.getValue().trim().length() == 0) {
			tfSectionCode.setComponentError(new UserError(GERPErrorCodes.NULL_SECTION_CODE));
			errorFlag = true;
		}
		try {
			Long.valueOf(tfSectionLimit.getValue());
			errorFlag = false;
			tfSectionLimit.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfSectionLimit.setComponentError(new UserError(GERPErrorCodes.NULL_TAX_SETION_LIMIT));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	public boolean validateDtls() {
		boolean isValid = true;
		// Boolean errorFlag = false;
		try {
			Long.valueOf(tftaxlimit.getValue());
			isValid = true;
			tftaxlimit.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tftaxlimit.setComponentError(new UserError(GERPErrorCodes.NULL_TAX_SECTION_TAX));
			isValid = false;
		}
		return isValid;
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		TaxSectionDM TaxsecObj = new TaxSectionDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			TaxsecObj = beanTaxSectionDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		TaxsecObj.setCompanyid(companyid);
		TaxsecObj.setSectioncode(tfSectionCode.getValue().toString());
		TaxsecObj.setSectiondesc(taSectionDesc.getValue().toString());
		if (cbSectionStatus.getValue() != null) {
			TaxsecObj.setSectionstatus((String) cbSectionStatus.getValue());
		}
		try {
			TaxsecObj.setSectionlimit(Long.valueOf(tfSectionLimit.getValue().toString()));
			TaxsecObj.setLastupdateddt(DateUtils.getcurrentdate());
			TaxsecObj.setLastupdatedby(username);
			serviceTaxSection.saveTaxSectionDetails(TaxsecObj);
			@SuppressWarnings("unchecked")
			Collection<TaxSubSectionDM> itemIds = (Collection<TaxSubSectionDM>) tblTaxsubsubsection.getVisibleItemIds();
			for (TaxSubSectionDM save : (Collection<TaxSubSectionDM>) itemIds) {
				save.setSectionid(Long.valueOf(TaxsecObj.getSectionid().toString()));
				serviceTaxSubSection.saveTaxSectionDetails(save);
			}
			taxsubsectionresetfields();
			resetFields();
			sectionId=0L;
			loadSrchRslt();
			loadTaxsubsectionRslt();
			tblTaxsubsubsection.setColumnFooter("lastupdatedby", "No.of Records : " + 0);
		}
		catch (Exception e) {
			tfSectionLimit.setComponentError(new UserError(GERPErrorCodes.NULL_TAX_SETION_LIMIT));
		}
	}
	
	private void saveTaxsubsectionListDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		TaxSubSectionDM taxsubsectionobj = new TaxSubSectionDM();
		if (tblTaxsubsubsection.getValue() != null) {
			taxsubsectionobj = beanTaxSubSectionDM.getItem(tblTaxsubsubsection.getValue()).getBean();
			taxSubsectionList.remove(taxsubsectionobj);
		}
		try {
			taxsubsectionobj.setTaxlimit(Long.valueOf(tftaxlimit.getValue()));
			taxsubsectionobj.setSubsecnstatus((String) cbsubsecnstatus.getValue());
			taxsubsectionobj.setSubsectndesc(tasubsectndesc.getValue());
			taxsubsectionobj.setLastupdateddt(DateUtils.getcurrentdate());
			taxsubsectionobj.setLastupdatedby(username);
			taxSubsectionList.add(taxsubsectionobj);
			beanTaxSubSectionDM.addAll(taxSubsectionList);
			tblTaxsubsubsection.removeAllItems();
			tblTaxsubsubsection.setColumnAlignment("taxlimit", Align.RIGHT);
			tblTaxsubsubsection.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
			taxsubsectionresetfields();
			loadTaxsubsectionRslt();
			btnAddTaxsubsection.setCaption("Add");
		}
		catch (NumberFormatException e) {
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for taxSection. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_TAX_SECTION);
		UI.getCurrent().getSession().setAttribute("audittablepk", taxSectionId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		tfSectionCode.setRequired(false);
		tftaxlimit.setRequired(false);
		tftaxlimit.setComponentError(null);
		tfSectionLimit.setComponentError(null);
		tfSectionLimit.setRequired(false);
		taxsubsectionresetfields();
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		tfSectionCode.setRequired(true);
		tftaxlimit.setRequired(true);
		tfSectionLimit.setRequired(true);
		assembleInputUserLayout();
		resetFields();
		editTaxSectionDetails();
		editTaxSubSectionDetails();
		loadTaxsubsectionRslt();
	}
	
	private void editTaxSectionDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (sltedRcd != null) {
			TaxSectionDM editTaxSection = beanTaxSectionDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			sectionId = editTaxSection.getSectionid();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected TaxSection. Id -> " + sectionId);
			if (sltedRcd.getItemProperty("sectioncode").getValue() != null) {
				tfSectionCode.setValue(sltedRcd.getItemProperty("sectioncode").getValue().toString());
			}
			if (sltedRcd.getItemProperty("sectiondesc").getValue() != null) {
				taSectionDesc.setValue(sltedRcd.getItemProperty("sectiondesc").getValue().toString());
			}
			if (sltedRcd.getItemProperty("sectionlimit").getValue() != null) {
				tfSectionLimit.setValue(sltedRcd.getItemProperty("sectionlimit").getValue().toString());
			}
			cbSectionStatus.setValue(sltedRcd.getItemProperty("sectionstatus").getValue().toString());
			taxSubsectionList.addAll(serviceTaxSubSection.getTaxSubSectionList(null, sectionId, null, "F"));
		}
		loadTaxsubsectionRslt();
	}
	
	private void editTaxSubSectionDetails() {
		hlUserInputLayout.setVisible(true);
		Item itselect = tblTaxsubsubsection.getItem(tblTaxsubsubsection.getValue());
		if (itselect != null) {
			if (itselect.getItemProperty("taxlimit").getValue() != null) {
				tftaxlimit.setValue(itselect.getItemProperty("taxlimit").getValue().toString());
			}
			if (itselect.getItemProperty("subsectndesc").getValue() != null) {
				tasubsectndesc.setValue(itselect.getItemProperty("subsectndesc").getValue().toString());
			}
			cbsubsecnstatus.setValue(itselect.getItemProperty("subsecnstatus").getValue());
		}
	}
	private void deleteDtls() {
		TaxSubSectionDM taxslapDtlObj = new TaxSubSectionDM();
		if (tblTaxsubsubsection.getValue() != null) {
			taxslapDtlObj = beanTaxSubSectionDM.getItem(tblTaxsubsubsection.getValue()).getBean();
			taxSubsectionList.remove(taxslapDtlObj);
			taxsubsectionresetfields();
			tblTaxsubsubsection.setValue("");
			loadTaxsubsectionRslt();
			btnDelete.setEnabled(false);
		}
	}
}
