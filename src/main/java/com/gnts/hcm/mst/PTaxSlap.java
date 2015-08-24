package com.gnts.hcm.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.StateDM;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.StateService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.PTaxSlapDM;
import com.gnts.hcm.service.mst.PTaxSlapService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class PTaxSlap extends BaseUI {
	// Bean Creation
	private PTaxSlapService servicePTaxSlap = (PTaxSlapService) SpringContextHelper.getBean("PTaxSlap");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private StateService serviceState = (StateService) SpringContextHelper.getBean("mstate");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private TextField tfAmtFrm, tfAmtTo, tfPTaxAmt;
	private ComboBox cbCountry, cbStateName;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<PTaxSlapDM> beanPTaxSlapDM = null;
	// local variables declaration
	private Long companyid, countryid;
	private int recordCnt = 0;
	private String username, taxrebateid;
	private Long earnAmtfrm = 0L;
	private Long earnAmtTo = 0L;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(TaxRebate.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public PTaxSlap() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside PTaxSlap() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Tax UI");
		cbCountry = new GERPComboBox("Country Name");
		cbCountry.setItemCaptionPropertyId("countryName");
		cbCountry.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				loadStateList();
			}
		});
		loadCountryList();
		cbStateName = new GERPComboBox("State Name");
		cbStateName.setItemCaptionPropertyId("stateName");
		tfAmtFrm = new TextField("Earn Amt From");
		tfAmtFrm.setValue("0");
		tfAmtTo = new TextField("Earn Amt To");
		tfAmtTo.setValue("0");
		tfPTaxAmt = new TextField("Tax Amt");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
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
		flColumn1.addComponent(cbCountry);
		flColumn2.addComponent(cbStateName);
		flColumn3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(cbCountry);
		flColumn1.addComponent(cbStateName);
		flColumn2.addComponent(tfAmtFrm);
		flColumn2.addComponent(tfAmtTo);
		flColumn3.addComponent(tfPTaxAmt);
		flColumn3.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<PTaxSlapDM> listTaxSlap = new ArrayList<PTaxSlapDM>();
		Long countryId = null;
		if (cbCountry.getValue() != null) {
			countryId = ((Long.valueOf(cbCountry.getValue().toString())));
		}
		Long stateId = null;
		if (cbStateName.getValue() != null) {
			stateId = ((Long.valueOf(cbStateName.getValue().toString())));
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + cbStateName.getValue() + ", " + cbStatus.getValue());
		listTaxSlap = servicePTaxSlap.getPTaxSlapList(null, companyid, countryId, stateId,
				(String) cbStatus.getValue(), "F");
		recordCnt = listTaxSlap.size();
		beanPTaxSlapDM = new BeanItemContainer<PTaxSlapDM>(PTaxSlapDM.class);
		beanPTaxSlapDM.addAll(listTaxSlap);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Tax Rebate. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanPTaxSlapDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "ptaxSlapId", "countryName", "stateName", "status",
				"lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Country Name", "State Name", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("ptaxSlapId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStateName.setValue(null);
		cbCountry.setValue(null);
		tfPTaxAmt.setValue("0");
		tfAmtFrm.setValue("0");
		tfAmtFrm.setComponentError(null);
		tfAmtTo.setValue("0");
		cbStateName.setComponentError(null);
		cbCountry.setComponentError(null);
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editPTaxSlap() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		PTaxSlapDM taxSlapDM = beanPTaxSlapDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		taxrebateid = taxSlapDM.getPtaxSlapId().toString();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Tax Rebate. Id -> "
				+ taxrebateid);
		if (tblMstScrSrchRslt.getValue() != null) {
			cbCountry.setValue(taxSlapDM.getCountryId());
			cbStatus.setValue(taxSlapDM.getStatus());
			if (taxSlapDM.getPtaxAmt() != null) {
				tfPTaxAmt.setValue(taxSlapDM.getPtaxAmt().toString());
			}
			if (taxSlapDM.getAmtFrm() != null) {
				tfAmtFrm.setValue(taxSlapDM.getAmtFrm().toString());
			}
			if (taxSlapDM.getAmtTo() != null) {
				tfAmtTo.setValue(taxSlapDM.getAmtTo().toString());
			}
			if (taxSlapDM.getStateId() != null) {
				cbStateName.setValue(taxSlapDM.getStateId().toString());
			}
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
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbStateName.setValue(null);
		cbCountry.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// reset the input controls to default value
		resetFields();
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		cbCountry.setRequired(true);
		cbStateName.setRequired(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Tax. ID " + taxrebateid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_TAX);
		UI.getCurrent().getSession().setAttribute("audittablepk", taxrebateid);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		cbCountry.setRequired(false);
		cbStateName.setRequired(false);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbStateName.setRequired(true);
		cbCountry.setRequired(true);
		// reset the input controls to default value
		resetFields();
		editPTaxSlap();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbStateName.setComponentError(null);
		cbCountry.setComponentError(null);
		tfAmtFrm.setComponentError(null);
		errorFlag = false;
		if (cbStateName.getValue() == null) {
			cbStateName.setComponentError(new UserError(GERPErrorCodes.NULL_STATE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbStateName.getValue());
			errorFlag = true;
		}
		if (cbCountry.getValue() == null) {
			cbCountry.setComponentError(new UserError(GERPErrorCodes.NULL_COMPANY_COUNTRY));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbCountry.getValue());
			errorFlag = true;
		}
		earnAmtTo = Long.valueOf(tfAmtTo.getValue().toString());
		earnAmtfrm = Long.valueOf(tfAmtFrm.getValue().toString());
		if (earnAmtTo < earnAmtfrm) {
			tfAmtFrm.setComponentError(new UserError(GERPErrorCodes.NULL_AMTFRM_AMTTO));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		PTaxSlapDM pTaxSlapDM = new PTaxSlapDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			pTaxSlapDM = beanPTaxSlapDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		pTaxSlapDM.setCmpId(companyid);
		if (cbCountry.getValue() != null) {
			pTaxSlapDM.setCountryId((Long) cbCountry.getValue());
		}
		if (cbStateName.getValue() != null) {
			pTaxSlapDM.setStateId((Long.valueOf(cbStateName.getValue().toString())));
		}
		if (tfPTaxAmt.getValue() != null) {
			pTaxSlapDM.setPtaxAmt((Long.valueOf(tfPTaxAmt.getValue().toString())));
		}
		if (tfAmtFrm.getValue() != null) {
			pTaxSlapDM.setAmtFrm(Long.valueOf(tfAmtFrm.getValue()));
		} else {
			pTaxSlapDM.setAmtFrm(new Long("0"));
		}
		if (tfAmtTo.getValue() != null) {
			pTaxSlapDM.setAmtTo(Long.valueOf(tfAmtTo.getValue()));
		} else {
			pTaxSlapDM.setAmtTo(new Long("0"));
		}
		if (cbStatus.getValue() != null) {
			pTaxSlapDM.setStatus((String) (cbStatus.getValue()));
		}
		pTaxSlapDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		pTaxSlapDM.setLastUpdatedBy(username);
		servicePTaxSlap.saveTaxDetails(pTaxSlapDM);
		resetFields();
		loadSrchRslt();
	}
	
	private void loadCountryList() {
		try {
			BeanContainer<Long, CountryDM> beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
			beanCountry.setBeanIdProperty("countryID");
			beanCountry.addAll(serviceCountry.getCountryList(countryid, null, null, null, "Active", "P"));
			cbCountry.setContainerDataSource(beanCountry);
		}
		catch (Exception e) {
		}
	}
	
	// load the State name list details for form
	private void loadStateList() {
		try {
			BeanContainer<Long, StateDM> beanState = new BeanContainer<Long, StateDM>(StateDM.class);
			beanState.setBeanIdProperty("stateId");
			beanState.addAll(serviceState.getStateList(null, "Active", (Long) cbCountry.getValue(), companyid, "P"));
			cbStateName.setContainerDataSource(beanState);
		}
		catch (Exception e) {
		}
	}
}
