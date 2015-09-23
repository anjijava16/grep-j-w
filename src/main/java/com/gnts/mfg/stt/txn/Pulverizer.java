/**
 * File Name 		: Pulverizer.java 
 * Description 		: This UI screen purpose forModify the Pulverizer Details. 
 *                    Add the Pulverizer details should be directly added to DB.
 * Author 			: GOKUL M
 * Date 			: Oct 13, 2014
 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * Version       Date           	Modified By               Remarks
 *  0.1       Oct 13 2014             GOKUL M	           Initial Version
 **/
package com.gnts.mfg.stt.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.txn.AssetDetailsDM;
import com.gnts.asm.service.txn.AssetDetailsService;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextArea;
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
import com.gnts.stt.mfg.domain.txn.ExtrudersHdrDM;
import com.gnts.stt.mfg.domain.txn.PulvizDtlDM;
import com.gnts.stt.mfg.domain.txn.PulvizHdrDM;
import com.gnts.stt.mfg.service.txn.ExtrudersHdrService;
import com.gnts.stt.mfg.service.txn.PulvizDtlService;
import com.gnts.stt.mfg.service.txn.PulvizHdrService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Pulverizer extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(Pulverizer.class);
	private PulvizHdrService servicePulvizHdr = (PulvizHdrService) SpringContextHelper.getBean("PulverizerHdr");
	private BranchService serviceBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ExtrudersHdrService serviceExtruHdr = (ExtrudersHdrService) SpringContextHelper.getBean("extruderHdr");
	private AssetDetailsService serviceAssetDtls = (AssetDetailsService) SpringContextHelper.getBean("assetDetails");
	private PulvizDtlService servicePulvizDtls = (PulvizDtlService) SpringContextHelper.getBean("PulverizerDtl");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private String username;
	private Long employeeId, extId;
	private Long companyid, branchid, assetId, moduleid;
	private HorizontalLayout hlsearchlayout;
	private HorizontalLayout hluserInputlayoutHdr = new HorizontalLayout();
	private HorizontalLayout hluserInputlayoutDtl = new HorizontalLayout();
	private HorizontalLayout hluserInputlayout = new HorizontalLayout();
	private int recordCnt = 0, recdtl = 0;
	private String pulvizid;
	private BeanItemContainer<PulvizHdrDM> beanPulvizHdrDM = null;
	private BeanItemContainer<PulvizDtlDM> beanPulvizDtlDM = new BeanItemContainer<PulvizDtlDM>(PulvizDtlDM.class);
	private String branchName;
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4;
	private FormLayout flcolumn1dtl, flcolumn2dtl, flcolumn3dtl, flcolumn4dtl, flcolumn5dtl;
	private GERPTextArea taInstruction, taDtlRemarks;
	private GERPTextField tfPulRefNumber, tfLotNumber, tfOEE, tfBalancePercnt, tfInput, tfOutput, tfBalanceQty,
			tfOPMaterial;
	private GERPTimeField tfTimeIn, tfTimeOut;
	private PopupDateField dfPulvizDate, dfRefDate;
	private GERPComboBox cbBranchName, cbHdrStatus, cbDtlStatus, cbMachineName, cbExtrudRefNo;
	private VerticalLayout vlinputhdr, vlinputdtl;
	private Button btnAddPulvizDtl = new GERPButton("Add", "addbt", this);
	private Button btnDelete = new GERPButton("Delete", "delete", this);
	private Table tblPulvizDtl;
	private List<PulvizDtlDM> listPulverDetails = null;
	
	public Pulverizer() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleid = ((Long) UI.getCurrent().getSession().getAttribute("moduleId"));
		branchid = ((Long) UI.getCurrent().getSession().getAttribute("branchId"));
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Pulverizer constructor");
		// Loading the UI
		buidview();
	}
	
	private void buidview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "building Pulverizer UI");
		cbBranchName = new GERPComboBox("Branch Name");
		cbBranchName.setItemCaptionPropertyId("branchName");
		loadBranchlist();
		cbExtrudRefNo = new GERPComboBox("Extrud Ref.No");
		cbExtrudRefNo.setItemCaptionPropertyId("extRefNo");
		loadExtruHdrDetails();
		tfLotNumber = new GERPTextField("Lot No");
		tfOPMaterial = new GERPTextField("OP Material");
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		dfPulvizDate = new PopupDateField("Pulviz Date");
		dfPulvizDate.setWidth("130");
		taInstruction = new GERPTextArea("Instruction");
		taInstruction.setHeight("75");
		taInstruction.setNullRepresentation("");
		tfPulRefNumber = new GERPTextField("Pulviz Ref.No");
		// pulviz Detail
		cbMachineName = new GERPComboBox("Machine Ref Name");
		cbMachineName.setItemCaptionPropertyId("assetName");
		cbMachineName.setRequired(true);
		loadMachineDetails();
		dfRefDate = new PopupDateField("Date");
		dfRefDate.setWidth("130");
		dfRefDate.setRequired(true);
		tfInput = new GERPTextField("Input");
		tfOutput = new GERPTextField("Output");
		tfBalanceQty = new GERPTextField("Bal Qty");
		tfOEE = new GERPTextField("OEE");
		tfOEE.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				getOeePercentage();
			}
		});
		tfBalancePercnt = new GERPTextField("Bal Prcnt");
		taDtlRemarks = new GERPTextArea("Remark");
		taDtlRemarks.setHeight("45");
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.T_STT_MFG_PULVIZ_DTL, BASEConstants.PULVIZ_STATUS);
		// Machine TimeIn TimeField
		tfTimeIn = new GERPTimeField("Machine Time-In");
		// Machine TimeOut TimeField
		tfTimeOut = new GERPTimeField("Machine Time-Out");
		btnAddPulvizDtl.setStyleName("Add");
		btnAddPulvizDtl.setVisible(true);
		btnAddPulvizDtl.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validatePulverizerDetails()) {
					savePulverizerDetails();
				}
			}
		});
		tblPulvizDtl = new Table();
		tblPulvizDtl.setWidth("99%");
		tblPulvizDtl.setSelectable(true);
		tblPulvizDtl.setPageLength(7);
		tblPulvizDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblPulvizDtl.isSelected(event.getItemId())) {
					tblPulvizDtl.setImmediate(true);
					btnAddPulvizDtl.setCaption("Add");
					btnAddPulvizDtl.setStyleName("savebt");
					resetPulvizerDetails();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddPulvizDtl.setCaption("Update");
					btnAddPulvizDtl.setStyleName("savebt");
					editPulvizDetail();
				}
			}
		});
		btnDelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnDelete == event.getButton()) {
					deleteDetails();
					btnAddPulvizDtl.setCaption("Add");
				}
			}
		});
		hlsearchlayout = new GERPAddEditHLayout();
		assemblsearch();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
		loadPulverizerDetails();
		btnAddPulvizDtl.setStyleName("add");
	}
	
	private void assemblsearch() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search");
		hlsearchlayout.removeAllComponents();
		flcolumn1 = new GERPFormLayout();
		flcolumn2 = new GERPFormLayout();
		flcolumn3 = new GERPFormLayout();
		flcolumn4 = new GERPFormLayout();
		flcolumn1.addComponent(cbBranchName);
		flcolumn2.addComponent(tfPulRefNumber);
		flcolumn3.addComponent(dfPulvizDate);
		flcolumn3.setSpacing(true);
		flcolumn3.setMargin(true);
		flcolumn4.addComponent(cbHdrStatus);
		flcolumn4.setSpacing(true);
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		hlsearchlayout.addComponent(flcolumn1);
		hlsearchlayout.addComponent(flcolumn2);
		hlsearchlayout.addComponent(flcolumn3);
		hlsearchlayout.addComponent(flcolumn4);
		hlsearchlayout.setSizeUndefined();
		hlsearchlayout.setSpacing(true);
		hlsearchlayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID :" + companyid + " | User Name : " + username + ">" + "Assembliing User Input Layout");
		// pulviz hdr user input layout
		flcolumn1 = new GERPFormLayout();
		flcolumn2 = new GERPFormLayout();
		flcolumn3 = new GERPFormLayout();
		flcolumn4 = new GERPFormLayout();
		flcolumn1.addComponent(cbBranchName);
		flcolumn1.addComponent(tfPulRefNumber);
		flcolumn1.addComponent(cbExtrudRefNo);
		flcolumn2.addComponent(tfLotNumber);
		flcolumn2.addComponent(dfPulvizDate);
		flcolumn2.addComponent(tfOPMaterial);
		flcolumn3.addComponent(taInstruction);
		flcolumn4.addComponent(cbHdrStatus);
		hluserInputlayoutHdr = new HorizontalLayout();
		hluserInputlayoutHdr.addComponent(flcolumn1);
		hluserInputlayoutHdr.addComponent(flcolumn2);
		hluserInputlayoutHdr.addComponent(flcolumn3);
		hluserInputlayoutHdr.addComponent(flcolumn4);
		hluserInputlayoutHdr.setSpacing(true);
		hluserInputlayoutHdr.setMargin(true);
		// pulviz dtl user input layout
		flcolumn1dtl = new GERPFormLayout();
		flcolumn2dtl = new GERPFormLayout();
		flcolumn3dtl = new GERPFormLayout();
		flcolumn4dtl = new GERPFormLayout();
		flcolumn5dtl = new GERPFormLayout();
		flcolumn1dtl.addComponent(cbMachineName);
		flcolumn1dtl.addComponent(dfRefDate);
		flcolumn1dtl.addComponent(tfInput);
		flcolumn2dtl.addComponent(tfOutput);
		flcolumn2dtl.addComponent(tfBalanceQty);
		flcolumn2dtl.addComponent(tfOEE);
		flcolumn3dtl.addComponent(tfBalancePercnt);
		flcolumn3dtl.addComponent(taDtlRemarks);
		flcolumn4dtl.addComponent(tfTimeIn);
		flcolumn4dtl.addComponent(tfTimeOut);
		flcolumn4dtl.addComponent(cbDtlStatus);
		flcolumn5dtl.addComponent(btnAddPulvizDtl);
		flcolumn5dtl.addComponent(btnDelete);
		hluserInputlayoutDtl = new HorizontalLayout();
		hluserInputlayoutDtl.addComponent(flcolumn1dtl);
		hluserInputlayoutDtl.addComponent(flcolumn2dtl);
		hluserInputlayoutDtl.addComponent(flcolumn3dtl);
		hluserInputlayoutDtl.addComponent(flcolumn4dtl);
		hluserInputlayoutDtl.addComponent(flcolumn5dtl);
		hluserInputlayoutDtl.setSpacing(true);
		hluserInputlayoutDtl.setMargin(true);
		vlinputhdr = new VerticalLayout();
		vlinputhdr.addComponent(hluserInputlayoutDtl);
		vlinputhdr.addComponent(tblPulvizDtl);
		vlinputhdr.setSpacing(true);
		vlinputdtl = new VerticalLayout();
		vlinputdtl.setSpacing(true);
		vlinputdtl.setWidth("100%");
		vlinputdtl.addComponent(GERPPanelGenerator.createPanel(hluserInputlayoutHdr));
		vlinputdtl.addComponent(GERPPanelGenerator.createPanel(vlinputhdr));
		hluserInputlayout.addComponent(vlinputdtl);
		hluserInputlayout.setWidth("100%");
		hluserInputlayout.setMargin(false);
		hluserInputlayout.setSpacing(false);
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.setSelectable(true);
			tblMstScrSrchRslt.removeAllItems();
			Long brnchid = null;
			if (cbBranchName.getValue() != null) {
				brnchid = ((Long) cbBranchName.getValue());
			}
			List<PulvizHdrDM> list = new ArrayList<PulvizHdrDM>();
			list = servicePulvizHdr.getPulvizHdrDetails(null, brnchid, (String) tfPulRefNumber.getValue(),
					(Date) dfPulvizDate.getValue(), (String) cbHdrStatus.getValue(), "F");
			recordCnt = list.size();
			beanPulvizHdrDM = new BeanItemContainer<PulvizHdrDM>(PulvizHdrDM.class);
			beanPulvizHdrDM.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the PulvizerHdr result set");
			tblMstScrSrchRslt.setContainerDataSource(beanPulvizHdrDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "pulvizid", "branchname", "pulvizreffno", "lotno",
					"pulvizdate", "pulvizstatus", "lastupdateddate", "lastupdatedby" });
			logger.info("Company ID : " + companyid + " | User Name : " + username + " >>>>>>>>>>>>>>>>> "
					+ "Loading Search...");
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.No", "Branch Name", "Pulviz Ref.No", "Lot No",
					"Pulviz Date", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadPulverizerDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			logger.info("Company ID : " + companyid + " | savePulvizDetails User Name : " + username + " > "
					+ "Search Parameters are ");
			tblPulvizDtl.setFooterVisible(true);
			recdtl = listPulverDetails.size();
			beanPulvizDtlDM = new BeanItemContainer<PulvizDtlDM>(PulvizDtlDM.class);
			beanPulvizDtlDM.addAll(listPulverDetails);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the PulverizerDtl. result set");
			tblPulvizDtl.setContainerDataSource(beanPulvizDtlDM);
			tblPulvizDtl.setVisibleColumns(new Object[] { "machineName", "prodndate", "inputqty", "outputqty",
					"balanceqty", "plvzdtlstatus", "lastupdateddt", "lastupdatedby" });
			tblPulvizDtl.setColumnHeaders(new String[] { "Machine Ref Name", "Date", "Input Qty.", "Output Qty.",
					"Bal Qty.", "Status", "Last Updated Date", "Last Updated By" });
			tblPulvizDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recdtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void savePulverizerDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Saving PulvizDtl Data... ");
			PulvizDtlDM pulvizDtlDM = new PulvizDtlDM();
			if (tblPulvizDtl.getValue() != null) {
				pulvizDtlDM = beanPulvizDtlDM.getItem(tblPulvizDtl.getValue()).getBean();
				listPulverDetails.remove(pulvizDtlDM);
			}
			pulvizDtlDM.setMachid(((AssetDetailsDM) cbMachineName.getValue()).getAssetId());
			pulvizDtlDM.setMachineName(((AssetDetailsDM) cbMachineName.getValue()).getAssetName());
			pulvizDtlDM.setProdndate(dfRefDate.getValue());
			if (Long.valueOf(tfInput.getValue()) != null) {
				pulvizDtlDM.setInputqty(Long.valueOf(tfInput.getValue()));
			}
			if (Long.valueOf(tfOutput.getValue()) != null) {
				pulvizDtlDM.setOutputqty(Long.valueOf(tfOutput.getValue()));
			}
			if (Long.valueOf(tfBalanceQty.getValue()) != null) {
				pulvizDtlDM.setBalanceqty(Long.valueOf(tfBalanceQty.getValue()));
			}
			if (Long.valueOf(tfOEE.getValue()) != null) {
				pulvizDtlDM.setOeeprcnt(Long.valueOf(tfOEE.getValue()));
			}
			if (Long.valueOf(tfBalancePercnt.getValue()) != null) {
				pulvizDtlDM.setBalprcnt(Long.valueOf(tfBalancePercnt.getValue()));
			}
			if (tfTimeIn.getValue() != null) {
				pulvizDtlDM.setMchnstart(tfTimeIn.getHorsMunites());
			}
			if (tfTimeOut.getValue() != null) {
				pulvizDtlDM.setMchnend(tfTimeOut.getHorsMunites());
			}
			pulvizDtlDM.setRemark(taDtlRemarks.getValue());
			pulvizDtlDM.setPreparedby(employeeId);
			pulvizDtlDM.setReviewby(null);
			pulvizDtlDM.setActionedby(null);
			if (cbDtlStatus.getValue() != null) {
				pulvizDtlDM.setPlvzdtlstatus(cbDtlStatus.getValue().toString());
			}
			pulvizDtlDM.setLastupdateddt(DateUtils.getcurrentdate());
			pulvizDtlDM.setLastupdatedby(username);
			listPulverDetails.add(pulvizDtlDM);
			loadPulverizerDetails();
			btnAddPulvizDtl.setCaption("Add");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		resetPulvizerDetails();
	}
	
	private void loadBranchlist() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading Branchlist");
			BeanContainer<Long, BranchDM> beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanbranch.setBeanIdProperty("branchId");
			beanbranch.addAll(serviceBranch.getBranchList(branchid, branchName, null, "Active", companyid, "F"));
			cbBranchName.setContainerDataSource(beanbranch);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadExtruHdrDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading Extrud refno");
			BeanContainer<Long, ExtrudersHdrDM> beanextrud = new BeanContainer<Long, ExtrudersHdrDM>(
					ExtrudersHdrDM.class);
			beanextrud.setBeanIdProperty("extId");
			beanextrud.addAll(serviceExtruHdr.getExtruderList(extId, companyid, branchid, null, null, null, null, null,
					"Active", "F"));
			cbExtrudRefNo.setContainerDataSource(beanextrud);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadMachineDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading pulviz Reference No...");
			BeanItemContainer<AssetDetailsDM> beanassetdetails = new BeanItemContainer<AssetDetailsDM>(
					AssetDetailsDM.class);
			beanassetdetails.addAll(serviceAssetDtls.getAssetDetailList(null, assetId, "PUL", null, null, null, null));
			cbMachineName.setContainerDataSource(beanassetdetails);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
			assemblsearch();
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfPulRefNumber.setValue("");
		cbBranchName.setValue(null);
		dfPulvizDate.setValue(null);
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hluserInputlayout.removeAllComponents();
		hlUserIPContainer.addComponent(hluserInputlayout);
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		cbBranchName.setRequired(true);
		resetFields();
		resetPulvizerDetails();
		assembleUserInputLayout();
		loadPulverizerDetails();
		tfPulRefNumber.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid, "STT_MF_PVZREFNO").get(
					0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfPulRefNumber.setValue(slnoObj.getKeyDesc());
				tfPulRefNumber.setReadOnly(true);
			} else {
				tfPulRefNumber.setReadOnly(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		tblPulvizDtl.setVisible(true);
		lblNotification.setValue("");
		btnAddPulvizDtl.setCaption("Add");
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing new record...");
		hlCmdBtnLayout.setVisible(false);
		hluserInputlayout.removeAllComponents();
		assembleUserInputLayout();
		cbBranchName.setRequired(true);
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		resetPulvizerDetails();
		editPulvizHdr();
		editPulvizDetail();
	}
	
	private void editPulvizHdr() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			hluserInputlayout.setVisible(true);
			if (tblMstScrSrchRslt.getValue() != null) {
				PulvizHdrDM pulvizHdrDM = beanPulvizHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
				pulvizid = pulvizHdrDM.getPulvizid();
				cbBranchName.setValue(pulvizHdrDM.getBranchid());
				tfPulRefNumber.setReadOnly(false);
				tfPulRefNumber.setValue(pulvizHdrDM.getPulvizreffno());
				tfPulRefNumber.setReadOnly(true);
				if (pulvizHdrDM.getPulvizdate() != null) {
					dfPulvizDate.setValue(pulvizHdrDM.getPulvizdate1());
				}
				tfOPMaterial.setValue(pulvizHdrDM.getOpmaterialid().toString());
				tfLotNumber.setValue(pulvizHdrDM.getLotno());
				tfLotNumber.setReadOnly(true);
				if (pulvizHdrDM.getPulvizstatus() != null) {
					cbHdrStatus.setValue(pulvizHdrDM.getPulvizstatus());
				}
				if (pulvizHdrDM.getExtrudid() != null) {
					cbExtrudRefNo.setValue(pulvizHdrDM.getExtrudid());
				}
				taInstruction.setValue(pulvizHdrDM.getInstruction());
				tblPulvizDtl.removeAllItems();
				listPulverDetails.addAll(servicePulvizDtls.getPulvizDtlDetails(null, (Long.valueOf(pulvizid)), null,
						null, "F"));
			}
			loadPulverizerDetails();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranchName.setComponentError(null);
		tfOPMaterial.setComponentError(null);
		boolean errorflag = false;
		if ((cbBranchName.getValue() == null)) {
			cbBranchName.setComponentError(new UserError(GERPErrorCodes.NULL_BRANCH_NAME));
			errorflag = true;
		}
		Long prdctnQty;
		try {
			prdctnQty = Long.valueOf(tfOPMaterial.getValue());
			if (prdctnQty < 0) {
				tfOPMaterial.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorflag = true;
			}
		}
		catch (Exception e) {
			tfOPMaterial.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			errorflag = true;
		}
		if (errorflag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			validatePulverizerDetails();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			PulvizHdrDM pulvizHdrDM = new PulvizHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				pulvizHdrDM = beanPulvizHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			pulvizHdrDM.setPulvizreffno(tfPulRefNumber.getValue());
			pulvizHdrDM.setLotno(tfLotNumber.getValue());
			if (Long.valueOf(tfOPMaterial.getValue()) != null) {
				pulvizHdrDM.setOpmaterialid(Long.valueOf(tfOPMaterial.getValue()));
			}
			if (cbBranchName.getValue() != null) {
				pulvizHdrDM.setBranchid((Long) cbBranchName.getValue());
			}
			if (cbExtrudRefNo.getValue() != null) {
				pulvizHdrDM.setExtrudid((Long) cbExtrudRefNo.getValue());
			}
			pulvizHdrDM.setCompanyid(companyid);
			if (dfPulvizDate.getValue() != null) {
				pulvizHdrDM.setPulvizdate(dfPulvizDate.getValue());
			}
			pulvizHdrDM.setInstruction(taInstruction.getValue());
			if (cbHdrStatus.getValue() != null) {
				pulvizHdrDM.setPulvizstatus(cbHdrStatus.getValue().toString());
			}
			pulvizHdrDM.setLastupdateddate(DateUtils.getcurrentdate());
			pulvizHdrDM.setLastupdatedby(username);
			validatePulverizerDetails();
			servicePulvizHdr.saveorupdatePulvizHdr(pulvizHdrDM);
			pulvizid = pulvizHdrDM.getPulvizid();
			@SuppressWarnings("unchecked")
			Collection<PulvizDtlDM> itemids = (Collection<PulvizDtlDM>) tblPulvizDtl.getVisibleItemIds();
			for (PulvizDtlDM save : (Collection<PulvizDtlDM>) itemids) {
				save.setPulvizid(Long.valueOf(pulvizHdrDM.getPulvizid().toString()));
				servicePulvizDtls.saveOrUpdatepulvizDtl(save);
			}
			hluserInputlayout.setMargin(false);
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid,
							"STT_MF_PVZREFNO").get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchid, moduleid, "STT_MF_PVZREFNO");
					}
				}
				catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid,
							"STT_MF_PVZLOTNO").get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchid, moduleid, "STT_MF_PVZLOTNO");
					}
				}
				catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
			resetPulvizerDetails();
			resetFields();
			loadSrchRslt();
			pulvizid = null;
			loadPulverizerDetails();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		dfPulvizDate.setRequired(false);
		cbBranchName.setRequired(false);
		assemblsearch();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
		resetPulvizerDetails();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resitting the UI controls");
		tfPulRefNumber.setReadOnly(false);
		tfLotNumber.setReadOnly(false);
		cbExtrudRefNo.setValue(null);
		tfPulRefNumber.setValue("");
		tfLotNumber.setValue("");
		cbExtrudRefNo.setComponentError(null);
		tfOPMaterial.setValue("0");
		taInstruction.setValue("");
		taInstruction.setComponentError(null);
		cbBranchName.setValue(null);
		cbBranchName.setComponentError(null);
		dfPulvizDate.setValue(null);
		dfPulvizDate.setComponentError(null);
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		listPulverDetails = new ArrayList<PulvizDtlDM>();
		tblPulvizDtl.removeAllItems();
		recordCnt = 0;
	}
	
	private void resetPulvizerDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "reset_PulvizDtl...");
		cbMachineName.setValue(null);
		cbMachineName.setComponentError(null);
		dfRefDate.setValue(null);
		dfRefDate.setComponentError(null);
		tfOEE.setValue("0");
		taDtlRemarks.setValue("");
		tfBalancePercnt.setValue("0");
		tfInput.setValue("0");
		tfOutput.setValue("0");
		tfBalanceQty.setValue("0");
		recordCnt = 0;
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		tfTimeIn.setValue(null);
		tfTimeOut.setValue(null);
	}
	
	private void editPulvizDetail() {
		try {
			if (tblPulvizDtl.getValue() != null) {
				PulvizDtlDM pulvizDtlDM = new PulvizDtlDM();
				pulvizDtlDM = beanPulvizDtlDM.getItem(tblPulvizDtl.getValue()).getBean();
				Long mch = pulvizDtlDM.getMachid();
				Collection<?> mchid = cbMachineName.getItemIds();
				for (Iterator<?> iterator = mchid.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbMachineName.getItem(itemId);
					AssetDetailsDM add = (AssetDetailsDM) item.getBean();
					if (mch != null && mch.equals(add.getAssetId())) {
						cbMachineName.setValue(itemId);
					}
				}
				if (pulvizDtlDM.getProdndate() != null) {
					dfRefDate.setValue(pulvizDtlDM.getProdndate1());
				}
				tfInput.setValue(pulvizDtlDM.getInputqty().toString());
				tfOutput.setValue(pulvizDtlDM.getOutputqty().toString());
				if (pulvizDtlDM.getBalanceqty() != null) {
					tfBalanceQty.setValue(pulvizDtlDM.getBalanceqty().toString());
				}
				if (pulvizDtlDM.getOeeprcnt() != null) {
					tfOEE.setValue(pulvizDtlDM.getOeeprcnt().toString());
				}
				if (pulvizDtlDM.getBalprcnt() != null) {
					tfBalancePercnt.setValue(pulvizDtlDM.getBalprcnt().toString());
				}
				if (pulvizDtlDM.getPlvzdtlstatus() != null) {
					cbDtlStatus.setValue(pulvizDtlDM.getPlvzdtlstatus());
				}
				tfTimeIn.setTime(pulvizDtlDM.getMchnstart());
				tfTimeOut.setTime(pulvizDtlDM.getMchnend());
				if (pulvizDtlDM.getRemark() != null) {
					taDtlRemarks.setValue(pulvizDtlDM.getRemark());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private boolean validatePulverizerDetails() {
		tfInput.setComponentError(null);
		tfOutput.setComponentError(null);
		tfBalanceQty.setComponentError(null);
		tfOEE.setComponentError(null);
		tfBalancePercnt.setComponentError(null);
		boolean validpuldtl = true;
		if (cbMachineName.getValue() == null) {
			cbMachineName.setComponentError(new UserError(GERPErrorCodes.NULL_MACHINE_NAME));
			validpuldtl = false;
		} else {
			cbMachineName.setComponentError(null);
		}
		if (dfRefDate.getValue() == null) {
			dfRefDate.setComponentError(new UserError(GERPErrorCodes.NULL_PULVIZDTL_DATE));
			validpuldtl = false;
		} else {
			dfRefDate.setComponentError(null);
		}
		Long input;
		try {
			input = Long.valueOf(tfInput.getValue());
			if (input < 0) {
				tfInput.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				validpuldtl = false;
			}
		}
		catch (Exception e) {
			tfInput.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			validpuldtl = false;
		}
		Long output;
		try {
			output = Long.valueOf(tfOutput.getValue());
			if (output < 0) {
				tfOutput.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				validpuldtl = false;
			}
		}
		catch (Exception e) {
			tfOutput.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			validpuldtl = false;
		}
		Long balQty;
		try {
			balQty = Long.valueOf(tfBalanceQty.getValue());
			if (balQty < 0) {
				tfBalanceQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				validpuldtl = false;
			}
		}
		catch (Exception e) {
			tfBalanceQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			validpuldtl = false;
		}
		Long oee;
		try {
			oee = Long.valueOf(tfOEE.getValue());
			if (oee < 0) {
				tfOEE.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				validpuldtl = false;
			}
		}
		catch (Exception e) {
			tfOEE.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			validpuldtl = false;
		}
		Long balper;
		try {
			balper = Long.valueOf(tfBalancePercnt.getValue());
			if (balper < 0) {
				tfBalancePercnt.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				validpuldtl = false;
			}
		}
		catch (Exception e) {
			tfBalancePercnt.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			validpuldtl = false;
		}
		return validpuldtl;
	}
	
	private void deleteDetails() {
		try {
			PulvizDtlDM pulvizDtlDM = new PulvizDtlDM();
			if (tblPulvizDtl.getValue() != null) {
				pulvizDtlDM = beanPulvizDtlDM.getItem(tblPulvizDtl.getValue()).getBean();
				listPulverDetails.remove(pulvizDtlDM);
				resetPulvizerDetails();
				loadPulverizerDetails();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
			parameterMap.put("pulvizid", pulvizid);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/pulverizer"); // pulverizer is the name of my jasper
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
	private void getOeePercentage() {
		try {
			BigDecimal outPutOty = (new BigDecimal(tfOutput.getValue()).divide(new BigDecimal("800"), 2,
					RoundingMode.HALF_UP)).multiply(new BigDecimal("50"));
			BigDecimal time = tfTimeIn.getHorsMunitesinBigDecimal()
					.subtract(tfTimeOut.getHorsMunitesinBigDecimal())
					.divide(new BigDecimal("8.00"), 2, RoundingMode.HALF_UP);
			tfOEE.setValue(outPutOty.multiply(time).round(new MathContext(2)).toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
