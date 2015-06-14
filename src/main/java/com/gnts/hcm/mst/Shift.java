/**
 * File Name 		: Shift.java 
 * Description 		: this class is used for add/edit Shift details. 
 * Author 			: MADHU T 
 * Date 			: 11-July-2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1			11-July-2014			Madhu T						Initial Version
 */
package com.gnts.hcm.mst;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.components.GERPTimeField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.mst.ShiftDM;
import com.gnts.hcm.service.mst.ShiftService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Table.Align;

public class Shift extends BaseUI {
	private ShiftService serviceShift = (ShiftService) SpringContextHelper.getBean("Shift");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private GERPTimeField tfShiftIn, tfShiftOut, tfteaBrk1st, tfteaBrk1Ed, tfteaBrk2st, tfteaBrk2Ed, tfMealSt,
			tfMealEd;
	private TextField txtShiftName, txtPeriod, txtTotWrkHrs;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<ShiftDM> beanShift = null;
	// local variables declaration
	private Long companyid;
	private String shiftId;
	private int recordCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(Shift.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public Shift() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Shift() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Shift UI");
		// ShiftName text field
		txtShiftName = new GERPTextField("Shift Name");
		txtShiftName.setMaxLength(25);
		// Shift TimeIn TimeField
		tfShiftIn = new GERPTimeField("Shift Time-In");
		tfShiftIn.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				try {
					txtTotWrkHrs.setReadOnly(false);
					txtTotWrkHrs.setValue(timediff(tfShiftIn.getHorsMunitesinLong(), tfShiftOut.getHorsMunitesinLong()));
					txtTotWrkHrs.setReadOnly(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// Shift TimeOUT TimeField
		tfShiftOut = new GERPTimeField("Shift Time-Out");
		tfShiftOut.setImmediate(true);
		tfShiftOut.addValueChangeListener(new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				try {
					txtTotWrkHrs.setReadOnly(false);
					txtTotWrkHrs.setValue(timediff(tfShiftIn.getHorsMunitesinLong(), tfShiftOut.getHorsMunitesinLong()));
					txtTotWrkHrs.setReadOnly(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// Total Working Hours
		txtTotWrkHrs = new GERPTextField("Total Hours");
		txtTotWrkHrs.setReadOnly(true);
		// Tea Break1 Start TimeField
		tfteaBrk1st = new GERPTimeField("Tea Break One Start");
		// Tea Break1 End TimeField
		tfteaBrk1Ed = new GERPTimeField("Tea Break One End");
		// Tea Break1 Start TimeField
		tfteaBrk2st = new GERPTimeField("Tea Break Two Start");
		// Tea Break2 End TimeField
		tfteaBrk2Ed = new GERPTimeField("Tea Break Two End");
		// Meal Break Start TimeField
		tfMealSt = new GERPTimeField("Meal Break Start");
		// Meal Break End TimeField
		tfMealEd = new GERPTimeField("Meal Break End");
		// Meal Break Start Text Field
		txtPeriod = new GERPTextField("Grace Period(Minutes)");
		txtPeriod.setValue("0");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn1.addComponent(txtShiftName);
		flColumn2.addComponent(cbStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		// Add components for User Input Layout
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(txtShiftName);
		flColumn1.addComponent(tfShiftIn);
		flColumn1.addComponent(tfShiftOut);
		flColumn1.addComponent(txtTotWrkHrs);
		flColumn2.addComponent(tfteaBrk1st);
		flColumn2.addComponent(tfteaBrk1Ed);
		flColumn2.addComponent(tfteaBrk2st);
		flColumn2.addComponent(tfteaBrk2Ed);
		flColumn3.addComponent(tfMealSt);
		flColumn3.addComponent(tfMealEd);
		flColumn3.addComponent(txtPeriod);
		flColumn3.addComponent(cbStatus);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setMargin(true);
		hlUserInputLayout.setSpacing(true);
	}
	
	private String timediff(Double timin, Double timout) {
		// TODO Auto-generated method stub
		Double timindiff = timin / 100;
		Double tioutdiff = timout / 100;
		tioutdiff = (timindiff > tioutdiff) ? (tioutdiff + 24) : tioutdiff;
		Double min_1 = timin % 100;
		Double min_2 = timout % 100;
		Double diffmin, diffhr;
		if (min_2 >= min_1) {
			diffmin = min_2 - min_1;
		} else {
			diffmin = (min_2 + 60) - min_1;
			tioutdiff--;
		}
		diffhr = tioutdiff - timindiff;
		String numhr = diffhr < 10 ? "0" + diffhr : "" + diffhr;
		String nummin = diffmin < 10 ? "0" + diffmin : "" + diffmin;
		// txtTotWrkHrs.setReadOnly(false);
		DecimalFormat df = new DecimalFormat("#.##");
		return (df.format(Double.valueOf(nummin)));
		// txtTotWrkHrs.setReadOnly(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<ShiftDM> shiftList = new ArrayList<ShiftDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + txtShiftName.getValue() + ", " + cbStatus.getValue());
		shiftList = serviceShift.getShiftList(null, txtShiftName.getValue(), companyid, (String) cbStatus.getValue(),
				"F");
		recordCnt = shiftList.size();
		beanShift = new BeanItemContainer<ShiftDM>(ShiftDM.class);
		beanShift.addAll(shiftList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Shift Type. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanShift);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "shiftId", "shiftName", "status", "lastUpdatedDate",
				"lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Shift Name", "Status", "Last Updated Date",
				"Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("shiftId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		txtShiftName.setValue("");
		txtShiftName.setComponentError(null);
		tfShiftIn.setValue(null);
		tfShiftOut.setValue(null);
		txtTotWrkHrs.setReadOnly(false);
		txtTotWrkHrs.setValue("0");
		txtTotWrkHrs.setReadOnly(true);
		tfteaBrk1Ed.setValue(null);
		tfteaBrk1st.setValue(null);
		tfteaBrk2Ed.setValue(null);
		tfteaBrk2st.setValue(null);
		tfMealEd.setValue(null);
		tfMealSt.setValue(null);
		txtPeriod.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editShift() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		try {
			hlUserInputLayout.setVisible(true);
			Item itselect = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			ShiftDM editShift = beanShift.getItem(tblMstScrSrchRslt.getValue()).getBean();
			shiftId = editShift.getShiftId().toString();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Shift. Id -> "
					+ shiftId);
			if (editShift.getShiftName() != null) {
				txtShiftName.setValue(itselect.getItemProperty("shiftName").getValue().toString());
			}
			tfShiftIn.setTime(editShift.getShiftTimeIn());
			tfShiftOut.setTime(editShift.getShiftTimeOut());
			tfteaBrk1Ed.setTime(editShift.getTeaBrk1Ed());
			tfteaBrk1st.setTime(editShift.getTeaBrk1St());
			tfteaBrk2Ed.setTime(editShift.getTeaBrk2Ed());
			tfteaBrk2st.setTime(editShift.getTeaBrk2St());
			tfMealEd.setTime(editShift.getMealBrkEd());
			tfMealSt.setTime(editShift.getMealBrkSt());
			txtTotWrkHrs.setReadOnly(false);
			if (editShift.getTotWorkHrs() != null) {
				txtTotWrkHrs.setValue(itselect.getItemProperty("totWorkHrs").getValue().toString());
			}
			txtTotWrkHrs.setReadOnly(true);
			if (editShift.getGracePeriod() != null) {
				txtPeriod.setValue(itselect.getItemProperty("gracePeriod").getValue().toString());
			}
			cbStatus.setValue(itselect.getItemProperty("status").getValue());
		}
		catch (Exception e) {
			e.printStackTrace();
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
		txtShiftName.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		assembleUserInputLayout();
		resetFields();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		txtShiftName.setRequired(true);
		// reset the input controls to default value
		resetFields();
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Shift. ID " + shiftId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_HCM_SHIFT);
		UI.getCurrent().getSession().setAttribute("audittablepk", shiftId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		txtShiftName.setComponentError(null);
		txtShiftName.setRequired(false);
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		txtShiftName.setRequired(true);
		editShift();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		txtShiftName.setComponentError(null);
		if ((txtShiftName.getValue() == null) || txtShiftName.getValue().trim().length() == 0) {
			txtShiftName.setComponentError(new UserError(GERPErrorCodes.NULL_SHIFT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + txtShiftName.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			ShiftDM saveShiftObj = new ShiftDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				saveShiftObj = beanShift.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			saveShiftObj.setCmpId(companyid);
			saveShiftObj.setShiftName(txtShiftName.getValue().toString());
			if (tfShiftIn.getValue() != null) {
				saveShiftObj.setShiftTimeIn(tfShiftIn.getHorsMunites());
			}
			if (tfShiftOut.getValue() != null) {
				saveShiftObj.setShiftTimeOut(tfShiftOut.getHorsMunites());
			}
			if (txtTotWrkHrs.getValue() != null) {
				saveShiftObj.setTotWorkHrs(Double.valueOf(txtTotWrkHrs.getValue()));
			} else {
				saveShiftObj.setTotWorkHrs(new Double("0"));
			}
			if (tfteaBrk1st.getValue() != null) {
				saveShiftObj.setTeaBrk1St(tfteaBrk1st.getHorsMunites());
			}
			if (tfteaBrk1Ed.getValue() != null) {
				saveShiftObj.setTeaBrk1Ed(tfteaBrk1Ed.getHorsMunites());
			}
			if (tfteaBrk2st.getValue() != null) {
				saveShiftObj.setTeaBrk2St(tfteaBrk2st.getHorsMunites());
			}
			if (tfteaBrk2Ed.getValue() != null) {
				saveShiftObj.setTeaBrk2Ed(tfteaBrk2Ed.getHorsMunites());
			}
			if (tfMealSt.getValue() != null) {
				saveShiftObj.setMealBrkSt(tfMealSt.getHorsMunites());
			}
			if (tfMealEd.getValue() != null) {
				saveShiftObj.setMealBrkEd(tfMealEd.getHorsMunites());
			}
			if (txtPeriod.getValue() != null) {
				saveShiftObj.setGracePeriod(Long.valueOf(txtPeriod.getValue()));
			} else {
				saveShiftObj.setGracePeriod(new Long("0"));
			}
			if (cbStatus.getValue() != null) {
				saveShiftObj.setStatus((String) cbStatus.getValue());
			}
			saveShiftObj.setLastUpdatedDate(DateUtils.getcurrentdate());
			saveShiftObj.setLastUpdatedBy(username);
			serviceShift.saveAndUpdate(saveShiftObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}