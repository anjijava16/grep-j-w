package com.gnts.asm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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
	// User Input Fields for EC Request
	private TextField tfDiselOpenBal, tfGenTotalTime, tfDiselConsBal, tfVolts, tfAmps, tfRpmHz, tfDiselCloseBal,
			tfDiselPurLtrs, tfOtherUseLtrs, tfLtrPerHours, tfMachineServRemain, tfOneLtrCost, tfTotalCost, tfTotalTime;
	private PopupDateField dfRefDate, dfRefEndDate;
	private GERPTimeField tfGenStartTime, tfGenStopTime;
	private ComboBox cbAssetName;
	private TextArea taRunningMachineDtl, taRemarks;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<GeneratorDM> beanGenerator = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// local variables declaration
	private Long generatorId;
	private String username;
	private Long companyid;
	private int recordCnt = 0;
	private int timeCnt = 0;
	
	// Constructor received the parameters from Login UI class
	public Generator() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Generator() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting Generator UI");
		// EC Request Components Definition
		getunitvalues();
		tfDiselOpenBal = new GERPTextField("Disel Open Balance");
		tfGenTotalTime = new GERPTextField("Generator Total time");
		tfGenTotalTime.setValue("0");
		tfDiselConsBal = new TextField("Disel Consuption Balance");
		tfDiselConsBal.setReadOnly(false);
		tfVolts = new GERPTextField("Volts");
		tfAmps = new GERPTextField("Amps");
		tfRpmHz = new GERPTextField("RPM HZ");
		tfDiselCloseBal = new TextField("Disel Close Balance");
		tfDiselCloseBal.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getDiselConsBal();
			}
		});
		tfDiselPurLtrs = new GERPTextField("Disel Purchase(Ltrs)");
		tfDiselPurLtrs.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getDiselPurBal();
			}
		});
		tfOtherUseLtrs = new GERPTextField("Other Use(Ltrs)");
		tfOtherUseLtrs.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getOtherUseLiter();
			}
		});
		tfLtrPerHours = new GERPTextField("Liter per Hour");
		tfMachineServRemain = new GERPTextField("Service Remainder");
		tfOneLtrCost = new GERPTextField("One Liter Cost");
		tfOneLtrCost.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getTotLtrCost();
			}
		});
		tfTotalTime = new GERPTextField("Session Run Time");
		tfTotalTime.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				getunitvalues();
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
				getTotalTime();
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
					GeneratorDM generatorDM = serviceGenerator.getGeneratorDetailList(null,
							(Long) cbAssetName.getValue(), null, null, "Y", null).get(0);
					if (generatorDM.getDiselCloseBalance() != null) {
						tfDiselOpenBal.setValue(generatorDM.getDiselCloseBalance().toString());
					}
					if (generatorDM.getRpmHz() != null) {
						tfRpmHz.setValue(generatorDM.getRpmHz().toString());
					}
				}
				catch (Exception e) {
					tfDiselOpenBal.setValue("0");
					tfRpmHz.setValue("0");
					e.printStackTrace();
				}
			}
		});
		loadAssetList();
		dfRefDate = new GERPPopupDateField("Date");
		dfRefEndDate = new GERPPopupDateField("End Date");
		cbStatus.setWidth("150");
		hlsearchlayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
		btnPrint.setVisible(true);
	}
	
	private void getTotalTime() {
		// TODO Auto-generated method stub
		if (tfTotalTime.getValue() != null) {
			if (timeCnt == 0) {
				tfGenTotalTime.setValue((new BigDecimal(tfGenTotalTime.getValue())).add(
						new BigDecimal(tfTotalTime.getValue())).toString());
				timeCnt++;
			} else {
				tfGenTotalTime.setValue("0.0");
				tfGenTotalTime.setValue((new BigDecimal(tfGenTotalTime.getValue())).add(
						new BigDecimal(tfTotalTime.getValue())).toString());
				timeCnt++;
			}
		} else {
			tfGenTotalTime.setValue("0.0");
		}
	}
	
	private void getTotalHours() {
		// TODO Auto-generated method stub
		if (tfGenStartTime.getValue() != null && tfGenStopTime.getValue() != null) {
			if (tfGenStartTime.getHorsMunitesinBigDecimal().compareTo(tfGenStopTime.getHorsMunitesinBigDecimal()) < 0) {
				tfTotalTime.setValue(tfGenStartTime.getHorsMunitesinBigDecimal()
						.subtract(tfGenStopTime.getHorsMunitesinBigDecimal()).abs().toString());
			} else {
				tfTotalTime.setValue("0.0");
			}
		}
	}
	
	private void getOtherUseLiter() {
		// TODO Auto-generated method stub
		if (tfDiselOpenBal.getValue() != null && tfOtherUseLtrs.getValue() != null) {
			tfDiselOpenBal.setReadOnly(false);
			tfDiselOpenBal.setValue((new BigDecimal(tfDiselOpenBal.getValue())).subtract(
					new BigDecimal(tfOtherUseLtrs.getValue())).toString());
		} else {
			tfDiselPurLtrs.setValue("0");
		}
	}
	
	private void getTotLtrCost() {
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
	
	private void getDiselConsBal() {
		// TODO Auto-generated method stub
		if (tfDiselOpenBal.getValue() != null && tfDiselCloseBal.getValue() != null) {
			tfLtrPerHours.setReadOnly(false);
			tfDiselConsBal.setReadOnly(false);
			tfDiselConsBal.setValue((new BigDecimal(tfDiselOpenBal.getValue())).subtract(
					new BigDecimal(tfDiselCloseBal.getValue())).toString());
			// tfLtrPerHours.setValue((new BigDecimal(tfDiselConsBal.getValue())).divide(new
			// BigDecimal(tfTotalTime.getValue().trim().length())).toString());
		} else {
			tfDiselConsBal.setValue("0");
			tfLtrPerHours.setValue("0");
		}
	}
	
	private void getDiselPurBal() {
		// TODO Auto-generated method stub
		if (tfDiselOpenBal.getValue() != null && tfDiselPurLtrs.getValue() != null) {
			tfDiselOpenBal.setReadOnly(false);
			tfDiselOpenBal.setValue((new BigDecimal(tfDiselOpenBal.getValue())).add(
					new BigDecimal(tfDiselPurLtrs.getValue())).toString());
		} else {
			tfDiselPurLtrs.setValue("0");
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlsearchlayout.removeAllComponents();
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol1.addComponent(cbAssetName);
		flcol2.addComponent(dfRefDate);
		flcol3.addComponent(dfRefEndDate);
		hlsearchlayout.addComponent(flcol1);
		hlsearchlayout.addComponent(flcol2);
		hlsearchlayout.addComponent(flcol3);
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
		flcol1.addComponent(cbAssetName);
		flcol1.addComponent(dfRefDate);
		flcol1.addComponent(tfGenStartTime);
		flcol1.addComponent(tfGenStopTime);
		flcol2.addComponent(tfTotalTime);
		flcol2.addComponent(tfDiselOpenBal);
		flcol2.addComponent(tfGenTotalTime);
		flcol2.addComponent(tfDiselConsBal);
		flcol2.addComponent(tfVolts);
		flcol3.addComponent(tfAmps);
		flcol3.addComponent(tfRpmHz);
		flcol3.addComponent(tfDiselCloseBal);
		flcol3.addComponent(tfDiselPurLtrs);
		flcol3.addComponent(tfOtherUseLtrs);
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<GeneratorDM> list = new ArrayList<GeneratorDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + null + "," + tfDiselOpenBal.getValue() + ", " + (String) cbStatus.getValue());
		list = serviceGenerator.getGeneratorDetailList(null, (Long) cbAssetName.getValue(), dfRefDate.getValue(),
				(String) cbStatus.getValue(), null, dfRefEndDate.getValue());
		recordCnt = list.size();
		beanGenerator = new BeanItemContainer<GeneratorDM>(GeneratorDM.class);
		beanGenerator.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the ECReq. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanGenerator);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "gensetId", "assetName", "gensetDate", "genOnTime",
				"getOffTime", "totalTime", "status", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Asset Name", "Date", "On Time", "Off Time",
				"Total Time", "Status", "Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("gensetId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	// Load Enquiry List
	private void loadAssetList() {
		BeanContainer<Long, AssetDetailsDM> beanAssetDetails = new BeanContainer<Long, AssetDetailsDM>(
				AssetDetailsDM.class);
		beanAssetDetails.setBeanIdProperty("assetId");
		beanAssetDetails.addAll(serviceAssetDetail.getAssetDetailList(companyid, null, "GEN", null, null, null, null));
		cbAssetName.setContainerDataSource(beanAssetDetails);
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editECRequest() {
		timeCnt = 0;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
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
			tfTotalTime.setValue(generatorDM.getTotalTime());
			if (generatorDM.getDiselOpenBalance() != null) {
				tfDiselOpenBal.setValue(generatorDM.getDiselOpenBalance().toString());
			}
			if (generatorDM.getConsuptionBalance() != null) {
				tfDiselConsBal.setValue(generatorDM.getConsuptionBalance().toString());
			}
			tfGenTotalTime.setValue(generatorDM.getTotalTime());
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
				tfDiselCloseBal.setValue(generatorDM.getDiselCloseBalance().toString());
			}
			if (generatorDM.getDieselPurLiters() != null) {
				tfDiselPurLtrs.setValue(generatorDM.getDieselPurLiters().toString());
			}
			if (generatorDM.getOtherUseLiters() != null) {
				tfOtherUseLtrs.setValue(generatorDM.getOtherUseLiters().toString());
			}
			if (generatorDM.getLiterPerHour() != null) {
				tfLtrPerHours.setValue(generatorDM.getLiterPerHour().toString());
			}
			if (generatorDM.getMachineServiceRemain() != null) {
				tfMachineServRemain.setValue(generatorDM.getMachineServiceRemain().toString());
			}
			if (generatorDM.getOneLiterCost() != null) {
				tfOneLtrCost.setValue(generatorDM.getOneLiterCost().toString());
			}
			if (generatorDM.getTotalCost() != null) {
				tfTotalCost.setValue(generatorDM.getTotalCost().toString());
			}
			cbStatus.setValue(generatorDM.getStatus());
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		timeCnt = 0;
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
		generatorDM.setGenTotalTime(new BigDecimal(tfGenTotalTime.getValue()));
		generatorDM.setConsuptionBalance(new BigDecimal(tfDiselConsBal.getValue()));
		generatorDM.setVolts(new BigDecimal(tfVolts.getValue()));
		generatorDM.setAmps(new BigDecimal(tfAmps.getValue()));
		generatorDM.setRpmHz(new BigDecimal(tfRpmHz.getValue()));
		generatorDM.setDiselCloseBalance(new BigDecimal(tfDiselCloseBal.getValue()));
		generatorDM.setDieselPurLiters(new BigDecimal(tfDiselPurLtrs.getValue()));
		generatorDM.setOtherUseLiters(new BigDecimal(tfOtherUseLtrs.getValue()));
		generatorDM.setLiterPerHour(new BigDecimal(tfLtrPerHours.getValue()));
		generatorDM.setMachineServiceRemain(new BigDecimal(tfMachineServRemain.getValue()));
		generatorDM.setOneLiterCost(new BigDecimal(tfOneLtrCost.getValue()));
		generatorDM.setTotalCost(new BigDecimal(tfTotalCost.getValue()));
		generatorDM.setGenOnTime(tfGenStartTime.getHorsMunites());
		generatorDM.setGetOffTime(tfGenStopTime.getHorsMunites());
		generatorDM.setTotalTime(tfTotalTime.getValue());
		generatorDM.setRunningMachineDetails(taRunningMachineDtl.getValue());
		generatorDM.setRemarks(taRemarks.getValue());
		generatorDM.setStatus((String) cbStatus.getValue());
		generatorDM.setLastupdatedby(username);
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
		timeCnt = 0;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		dfRefDate.setValue(new Date());
	}
	
	@Override
	protected void editDetails() {
		timeCnt = 0;
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
		tfTotalTime.setValue("0");
		tfDiselOpenBal.setValue("0");
		tfGenTotalTime.setValue("0");
		tfDiselConsBal.setValue("0");
		tfDiselConsBal.setWidth("150");
		tfVolts.setValue("0");
		tfAmps.setValue("0");
		tfRpmHz.setValue("0");
		tfDiselCloseBal.setValue("0");
		tfDiselPurLtrs.setValue("0");
		tfOtherUseLtrs.setValue("0");
		tfLtrPerHours.setValue("0");
		tfMachineServRemain.setValue("");
		tfOneLtrCost.setValue("0");
		tfTotalCost.setValue("0");
		taRunningMachineDtl.setValue("");
		taRemarks.setValue("");
		cbStatus.setValue(null);
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
			// file.
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
	// Load Last Unit Values.
	private void getunitvalues() {
		GeneratorDM generatorDM = null;
		try {
			generatorDM = serviceGenerator.getGeneratorDetailList(null, null,dfRefDate.getValue(), null, "Y",null).get(0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			tfGenTotalTime.setValue(new BigDecimal(tfGenTotalTime.getValue()).add(generatorDM.getGenTotalTime()).toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

