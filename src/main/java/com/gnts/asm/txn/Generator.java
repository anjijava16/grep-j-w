package com.gnts.asm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.txn.AssetDetailsDM;
import com.gnts.asm.domain.txn.GeneratorDM;
import com.gnts.asm.service.txn.AssetDetailsService;
import com.gnts.asm.service.txn.GeneratorService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.components.GERPTimeField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Generator extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private GeneratorService serviceGenerator = (GeneratorService) SpringContextHelper.getBean("generator");
	private AssetDetailsService serviceAssetDetail = (com.gnts.asm.service.txn.AssetDetailsService) SpringContextHelper
			.getBean("assetDetails");
	// Initialize the logger
	private Logger logger = Logger.getLogger(Generator.class);
	// User Input Fields for Generator
	private TextField tfDiselOpenBal, tfGenTotalTime, tfDiselConsBal, tfVolts, tfAmps, tfRpmHz, tfDiselCloseBal,
			tfDiselPurLtrs, tfOtherUseLtrs, tfLtrPerHours, tfMachineServRemain, tfOneLtrCost, tfTotalCost,
			tfSessionTime;
	private PopupDateField dfRefDate, dfRefEndDate;
	private GERPTimeField tfGenStartTime, tfGenStopTime;
	private ComboBox cbAssetName;
	private TextArea taRunningMachineDtl, taRemarks;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<GeneratorDM> beanGenerator = null;
	// form layout for input controls Generator
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// local variables declaration
	private Long generatorId;
	private String username;
	private Long companyid, branchId;
	private int recordCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public Generator() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Generator() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting Generator UI");
		// EC Request Components Definition
		tfDiselOpenBal = new GERPTextField("Disel Open Balance");
		tfGenTotalTime = new GERPTextField("Generator Total time");
		tfGenTotalTime.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getGenSetTime();
			}
		});
		tfDiselConsBal = new TextField("Disel Consuption Balance");
		tfDiselConsBal.setReadOnly(false);
		tfDiselConsBal.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getLiterHour();
				getDiselCloseBal();
				if (tfOneLtrCost.getValue() != null) {
					getTotLtrCost();
				}
			}
		});
		tfVolts = new GERPTextField("Volts");
		tfAmps = new GERPTextField("Amps");
		tfRpmHz = new GERPTextField("RPM HZ");
		tfDiselCloseBal = new TextField("Disel Close Balance");
		tfDiselCloseBal.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getDiselCloseBal();
			}
		});
		tfDiselPurLtrs = new GERPTextField("Disel Purchase(Ltrs)");
		tfDiselPurLtrs.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getDiselCloseBal();
			}
		});
		tfOtherUseLtrs = new GERPTextField("Other Use(Ltrs)");
		tfOtherUseLtrs.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getDiselCloseBal();
			}
		});
		tfLtrPerHours = new GERPTextField("Liter per Hour");
		tfMachineServRemain = new GERPTextField("Service Remainder");
		tfMachineServRemain.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getServiceTime();
			}
		});
		tfOneLtrCost = new GERPTextField("One Liter Cost");
		tfOneLtrCost.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getTotLtrCost();
			}
		});
		tfSessionTime = new GERPTextField("Session Time");
		tfMachineServRemain.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
			}
		});
		tfTotalCost = new GERPTextField("Total Cost");
		tfGenStartTime = new GERPTimeField("Start Time");
		tfGenStartTime.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getTotalHours();
			}
		});
		tfGenStopTime = new GERPTimeField("Stop Time");
		tfGenStopTime.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getTotalHours();
			}
		});
		taRunningMachineDtl = new TextArea("Running Machine Details");
		taRunningMachineDtl.setWidth("98%");
		taRemarks = new TextArea("Remarks");
		taRemarks.setWidth("98%");
		cbAssetName = new GERPComboBox("Asset Name");
		cbAssetName.setItemCaptionPropertyId("assetName");
		cbAssetName.setRequired(true);
		cbAssetName.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					loadAssetDetails();
					getGenSetTime();
				}
				catch (Exception e) {
					e.printStackTrace();
					logger.info(e.getMessage());
				}
			}
		});
		loadAssetList();
		dfRefDate = new GERPPopupDateField("Start Date");
		dfRefEndDate = new GERPPopupDateField("End Date");
		cbStatus.setWidth("150");
		hlsearchlayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
		btnPrint.setVisible(true);
	}
	
	private void getTotalHours() {
		try {
			// TODO Auto-generated method stub
			if (tfGenStartTime.getValue() != null && tfGenStopTime.getValue() != null) {
				if (tfGenStartTime.getHorsMunitesinBigDecimal().compareTo(tfGenStopTime.getHorsMunitesinBigDecimal()) < 0) {
					tfSessionTime.setValue(tfGenStartTime.getHorsMunitesinBigDecimal()
							.subtract(tfGenStopTime.getHorsMunitesinBigDecimal()).abs().toString());
				} else {
					tfSessionTime.setValue("0.0");
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void getTotLtrCost() {
		try {
			// TODO Auto-generated method stub
			if (tfOneLtrCost.getValue() != null && tfDiselConsBal.getValue() != null) {
				tfTotalCost.setReadOnly(false);
				tfDiselConsBal.setReadOnly(false);
				tfTotalCost.setValue((new BigDecimal(tfOneLtrCost.getValue())).multiply(
						new BigDecimal(tfDiselConsBal.getValue())).toString());
			} else {
				tfTotalCost.setValue("0");
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void getDiselCloseBal() {
		try {
			// TODO Auto-generated method stub
			if (tfDiselOpenBal.getValue() != null && tfDiselConsBal.getValue() != null) {
				tfDiselCloseBal.setReadOnly(false);
				tfDiselCloseBal.setValue((new BigDecimal(tfDiselOpenBal.getValue()))
						.add(new BigDecimal(tfDiselPurLtrs.getValue()))
						.subtract(new BigDecimal(tfDiselConsBal.getValue()))
						.subtract(new BigDecimal(tfOtherUseLtrs.getValue())).toString());
				tfDiselCloseBal.setReadOnly(true);
			} else {
				tfDiselConsBal.setValue("0");
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void getLiterHour() {
		try {
			// TODO Auto-generated method stub
			if (tfSessionTime.getValue() != null && tfDiselConsBal.getValue() != null) {
				tfLtrPerHours.setReadOnly(false);
				tfLtrPerHours.setValue((new BigDecimal(tfDiselConsBal.getValue())).divide(
						new BigDecimal(tfSessionTime.getValue())).toString());
				tfLtrPerHours.setReadOnly(true);
			} else {
				tfLtrPerHours.setValue("0");
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlsearchlayout.removeAllComponents();
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol1.addComponent(cbAssetName);
		flcol2.addComponent(dfRefDate);
		Label lbl = new Label();
		flcol3.addComponent(lbl);
		flcol4.addComponent(dfRefEndDate);
		hlsearchlayout.addComponent(flcol1);
		hlsearchlayout.addComponent(flcol2);
		hlsearchlayout.addComponent(flcol3);
		hlsearchlayout.addComponent(flcol4);
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setSizeUndefined();
	}
	
	private void assembleinputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol1.addComponent(dfRefDate);
		flcol1.addComponent(cbAssetName);
		flcol1.addComponent(tfGenStartTime);
		flcol1.addComponent(tfGenStopTime);
		flcol2.addComponent(tfSessionTime);
		flcol2.addComponent(tfGenTotalTime);
		flcol2.addComponent(tfVolts);
		flcol2.addComponent(tfAmps);
		flcol2.addComponent(tfRpmHz);
		flcol3.addComponent(tfDiselOpenBal);
		flcol3.addComponent(tfDiselConsBal);
		flcol3.addComponent(tfDiselPurLtrs);
		flcol3.addComponent(tfOtherUseLtrs);
		flcol3.addComponent(tfDiselCloseBal);
		flcol4.addComponent(tfLtrPerHours);
		flcol4.addComponent(tfMachineServRemain);
		flcol4.addComponent(tfOneLtrCost);
		flcol4.addComponent(tfTotalCost);
		flcol4.addComponent(cbStatus);
		hllayout.setMargin(true);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol4);
		hllayout.setMargin(true);
		hllayout.setSpacing(true);
		hlUserIPContainer.addComponent(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				VerticalLayout vlHeader = new VerticalLayout();
				vlHeader.setSpacing(true);
				vlHeader.setMargin(true);
				vlHeader.addComponent(hllayout);
				vlHeader.addComponent(taRunningMachineDtl);
				vlHeader.addComponent(taRemarks);
				addComponent(GERPPanelGenerator.createPanel(vlHeader));
			}
		});
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	// Load EC Request
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<GeneratorDM> list = new ArrayList<GeneratorDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + null + "," + tfDiselOpenBal.getValue() + ", " + (String) cbStatus.getValue());
			list = serviceGenerator.getGeneratorDetailList(companyid, branchId, null, (Long) cbAssetName.getValue(),
					dfRefDate.getValue(), (String) cbStatus.getValue(), null, null, dfRefEndDate.getValue());
			recordCnt = list.size();
			beanGenerator = new BeanItemContainer<GeneratorDM>(GeneratorDM.class);
			beanGenerator.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the ECReq. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanGenerator);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "gensetId", "assetName", "gensetDate", "genOnTime",
					"getOffTime", "sessionTime", "genTotalTime", "status", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Asset Name", "Date", "On Time", "Off Time",
					"Session Time", "Total Time", "Status", "Last Updated date", "Last Updated by" });
			tblMstScrSrchRslt.setColumnAlignment("gensetId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Enquiry List
	private void loadAssetList() {
		try {
			BeanContainer<Long, AssetDetailsDM> beanAssetDetails = new BeanContainer<Long, AssetDetailsDM>(
					AssetDetailsDM.class);
			beanAssetDetails.setBeanIdProperty("assetId");
			beanAssetDetails.addAll(serviceAssetDetail.getAssetDetailList(companyid, null, "GEN", null, null, null,
					null));
			cbAssetName.setContainerDataSource(beanAssetDetails);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editECRequest() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hllayout.setVisible(true);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected ecrid -> "
					+ generatorId);
			if (tblMstScrSrchRslt.getValue() != null) {
				GeneratorDM generatorDM = beanGenerator.getItem(tblMstScrSrchRslt.getValue()).getBean();
				generatorId = generatorDM.getGensetId();
				cbAssetName.setValue(generatorDM.getAssetId());
				dfRefDate.setValue(generatorDM.getGensetDate1());
				tfGenStartTime.setTime(generatorDM.getGenOnTime());
				tfGenStopTime.setTime(generatorDM.getGetOffTime());
				tfSessionTime.setValue(generatorDM.getSessionTime().toString());
				if (generatorDM.getDiselOpenBalance() != null) {
					tfDiselOpenBal.setValue(generatorDM.getDiselOpenBalance().toString());
				}
				if (generatorDM.getConsuptionBalance() != null) {
					tfDiselConsBal.setValue(generatorDM.getConsuptionBalance().toString());
				}
				tfGenTotalTime.setReadOnly(false);
				tfGenTotalTime.setValue(generatorDM.getGenTotalTime().toString());
				tfGenTotalTime.setReadOnly(true);
				if (generatorDM.getVolts() != null) {
					tfVolts.setValue(generatorDM.getVolts().toString());
				}
				if (generatorDM.getAmps() != null) {
					tfAmps.setValue(generatorDM.getAmps().toString());
				}
				if (generatorDM.getRpmHz() != null) {
					tfRpmHz.setValue(generatorDM.getRpmHz().toString());
				}
				if (generatorDM.getDiselCloseBalance() != null) {
					tfDiselCloseBal.setReadOnly(false);
					tfDiselCloseBal.setValue(generatorDM.getDiselCloseBalance().toString());
					tfDiselCloseBal.setReadOnly(true);
				}
				if (generatorDM.getDieselPurLiters() != null) {
					tfDiselPurLtrs.setValue(generatorDM.getDieselPurLiters().toString());
				}
				if (generatorDM.getOtherUseLiters() != null) {
					tfOtherUseLtrs.setValue(generatorDM.getOtherUseLiters().toString());
				}
				if (generatorDM.getLiterPerHour() != null) {
					tfLtrPerHours.setReadOnly(false);
					tfLtrPerHours.setValue(generatorDM.getLiterPerHour().toString());
					tfLtrPerHours.setReadOnly(true);
				}
				tfMachineServRemain.setReadOnly(false);
				if (generatorDM.getMachineServiceRemain() != null) {
					tfMachineServRemain.setValue(generatorDM.getMachineServiceRemain().toString());
				}
				tfMachineServRemain.setReadOnly(true);
				if (generatorDM.getOneLiterCost() != null) {
					tfOneLtrCost.setValue(generatorDM.getOneLiterCost().toString());
				}
				if (generatorDM.getTotalCost() != null) {
					tfTotalCost.setValue(generatorDM.getTotalCost().toString());
				}
				cbStatus.setValue(generatorDM.getStatus());
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		GeneratorDM generatorDM = new GeneratorDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			generatorDM = beanGenerator.getItem(tblMstScrSrchRslt.getValue()).getBean();
		} else {
			generatorDM.setIsLatest("Y");
		}
		generatorDM.setAssetId((Long) cbAssetName.getValue());
		generatorDM.setGensetDate(dfRefDate.getValue());
		generatorDM.setDiselOpenBalance(new BigDecimal(tfDiselOpenBal.getValue()));
		tfGenTotalTime.setReadOnly(false);
		generatorDM.setGenTotalTime(new BigDecimal(tfGenTotalTime.getValue()));
		tfGenTotalTime.setReadOnly(true);
		generatorDM.setConsuptionBalance(new BigDecimal(tfDiselConsBal.getValue()));
		generatorDM.setVolts(new BigDecimal(tfVolts.getValue()));
		generatorDM.setAmps(new BigDecimal(tfAmps.getValue()));
		generatorDM.setRpmHz(new BigDecimal(tfRpmHz.getValue()));
		tfDiselCloseBal.setReadOnly(false);
		generatorDM.setDiselCloseBalance(new BigDecimal(tfDiselCloseBal.getValue()));
		tfDiselCloseBal.setReadOnly(true);
		generatorDM.setDieselPurLiters(new BigDecimal(tfDiselPurLtrs.getValue()));
		generatorDM.setOtherUseLiters(new BigDecimal(tfOtherUseLtrs.getValue()));
		tfLtrPerHours.setReadOnly(false);
		generatorDM.setLiterPerHour(new BigDecimal(tfLtrPerHours.getValue()));
		tfLtrPerHours.setReadOnly(true);
		tfMachineServRemain.setReadOnly(false);
		generatorDM.setMachineServiceRemain(new BigDecimal(tfMachineServRemain.getValue()));
		tfMachineServRemain.setReadOnly(true);
		generatorDM.setOneLiterCost(new BigDecimal(tfOneLtrCost.getValue()));
		generatorDM.setTotalCost(new BigDecimal(tfTotalCost.getValue()));
		generatorDM.setGenOnTime(tfGenStartTime.getHorsMunites());
		generatorDM.setGetOffTime(tfGenStopTime.getHorsMunites());
		generatorDM.setSessionTime(new BigDecimal(tfSessionTime.getValue()));
		generatorDM.setRunningMachineDetails(taRunningMachineDtl.getValue());
		generatorDM.setRemarks(taRemarks.getValue());
		generatorDM.setIsLatest("Y");
		generatorDM.setStatus((String) cbStatus.getValue());
		generatorDM.setLastupdatedby(username);
		generatorDM.setCompanyId(companyid);
		generatorDM.setBranchId(branchId);
		generatorDM.setLastupdateddt(DateUtils.getcurrentdate());
		serviceGenerator.saveOrUpdateDetails(generatorDM);
		generatorId = generatorDM.getGensetId();
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		cbAssetName.setValue(null);
		dfRefDate.setValue(null);
		dfRefEndDate.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		resetFields();
		editECRequest();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbAssetName.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbAssetName.getValue() == null) {
			cbAssetName.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorFlag = true;
		}
		if ((dfRefDate.getValue() == null)) {
			dfRefDate.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfRefDate.getValue());
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbAssetName.getValue() + "," + ","
				+ "," + dfRefDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for enquiryId " + generatorId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(generatorId));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hllayout1.removeAllComponents();
		tblMstScrSrchRslt.setValue(null);
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbAssetName.setValue(null);
		dfRefDate.setValue(null);
		tfGenStartTime.setValue(null);
		tfGenStopTime.setValue(null);
		tfSessionTime.setValue("0");
		tfDiselOpenBal.setValue("0");
		tfGenTotalTime.setReadOnly(false);
		tfGenTotalTime.setValue("0");
		tfGenTotalTime.setReadOnly(true);
		tfDiselConsBal.setReadOnly(false);
		tfDiselConsBal.setValue("0");
		tfVolts.setValue("0");
		tfAmps.setValue("0");
		tfRpmHz.setValue("0");
		tfDiselCloseBal.setReadOnly(false);
		tfDiselCloseBal.setValue("0");
		tfDiselCloseBal.setReadOnly(true);
		tfDiselPurLtrs.setValue("0");
		tfOtherUseLtrs.setValue("0");
		tfLtrPerHours.setReadOnly(false);
		tfLtrPerHours.setValue("0");
		tfLtrPerHours.setReadOnly(true);
		tfMachineServRemain.setReadOnly(false);
		tfMachineServRemain.setValue("");
		tfMachineServRemain.setReadOnly(true);
		tfTotalCost.setValue("0");
		tfOneLtrCost.setReadOnly(false);
		tfOneLtrCost.setValue("0");
		taRunningMachineDtl.setValue("");
		taRemarks.setValue("");
		cbStatus.setValue(null);
		btnPrint.setVisible(true);
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
	protected void printDetails() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, String> parameterMap = new HashMap<String, String>();
			try {
				parameterMap.put("startdate", DateUtils.datetostring(dfRefDate.getValue()));
				parameterMap.put("enddate", DateUtils.datetostring(dfRefEndDate.getValue()));
			}
			catch (Exception e) {
			}
			try {
				parameterMap.put("assetid", cbAssetName.getValue().toString());
			}
			catch (Exception e) {
			}
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/generatormonth"); // generatormonth is the name of my jasper
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				statement.close();
				Database.close(connection);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadAssetDetails() {
		try {
			GeneratorDM generatorDM = serviceGenerator.getGeneratorDetailList(companyid, branchId, null,
					(Long) cbAssetName.getValue(), dfRefDate.getValue(), "Active", null, "Y", null).get(0);
			if (generatorDM.getDiselCloseBalance() != null) {
				tfDiselOpenBal.setValue(generatorDM.getDiselCloseBalance().toString());
			}
			if (generatorDM.getRpmHz() != null) {
				tfRpmHz.setValue(generatorDM.getRpmHz().toString());
			}
			if (generatorDM.getVolts() != null) {
				tfVolts.setValue(generatorDM.getVolts().toString());
			}
			if (generatorDM.getAmps() != null) {
				tfAmps.setValue(generatorDM.getAmps().toString());
			}
			if (generatorDM.getOneLiterCost() != null) {
				tfOneLtrCost.setValue(generatorDM.getOneLiterCost().toString());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getServiceTime() {
		try {
			if (Double.valueOf(tfMachineServRemain.getValue()) > 0) {
				tfMachineServRemain.setReadOnly(false);
				tfMachineServRemain.setValue(new BigDecimal(tfMachineServRemain.getValue()).subtract(
						new BigDecimal(tfSessionTime.getValue())).toString());
				tfMachineServRemain.setReadOnly(true);
			} else {
				tfMachineServRemain.setReadOnly(false);
				tfMachineServRemain.setRequired(true);
				tfMachineServRemain.setValue("");
				Notification.show("Enter the Next Service in Hours.", Type.WARNING_MESSAGE);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getGenSetTime() {
		try {
			GeneratorDM generatorDM = serviceGenerator.getGeneratorDetailList(companyid, branchId, null,
					(Long) cbAssetName.getValue(), dfRefDate.getValue(), "Active", "X", "Y", null).get(0);
			if (generatorDM.getGenTotalTime() != null) {
				tfGenTotalTime.setReadOnly(false);
				tfGenTotalTime.setValue((new BigDecimal(tfSessionTime.getValue())).add((generatorDM.getGenTotalTime()))
						.toString());
				tfGenTotalTime.setReadOnly(true);
			}
			tfMachineServRemain.setReadOnly(false);
			if (generatorDM.getMachineServiceRemain() != null) {
				tfMachineServRemain.setValue(generatorDM.getMachineServiceRemain().toString());
			}
			tfMachineServRemain.setReadOnly(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
