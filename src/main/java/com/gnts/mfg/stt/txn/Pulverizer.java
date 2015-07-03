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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.components.GERPTimeField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.stt.mfg.domain.txn.ExtrudersHdrDM;
import com.gnts.stt.mfg.domain.txn.PulvizDtlDM;
import com.gnts.stt.mfg.domain.txn.PulvizHdrDM;
import com.gnts.stt.mfg.service.txn.ExtrudersHdrService;
import com.gnts.stt.mfg.service.txn.PulvizDtlService;
import com.gnts.stt.mfg.service.txn.PulvizHdrService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class Pulverizer extends BaseUI {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(Pulverizer.class);
	private PulvizHdrService Pulvizhdrservice = (PulvizHdrService) SpringContextHelper.getBean("PulverizerHdr");
	private BranchService servicebranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private ExtrudersHdrService extrudservice = (ExtrudersHdrService) SpringContextHelper.getBean("extruderHdr");
	private AssetDetailsService serviceassetdetails = (AssetDetailsService) SpringContextHelper.getBean("assetDetails");
	private PulvizDtlService pulvizDtlservice = (PulvizDtlService) SpringContextHelper.getBean("PulverizerDtl");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private String username;
	private Long EmployeeId, extId;
	private Long companyid, branchid, assetId, moduleid, brnchid;
	private HorizontalLayout hlsearchlayout;
	private HorizontalLayout hluserInputlayoutHdr = new HorizontalLayout();
	private HorizontalLayout hluserInputlayoutDtl = new HorizontalLayout();
	private HorizontalLayout hluserInputlayout = new HorizontalLayout();
	private int recordCnt = 0, recdtl = 0;
	private String pulvizid;
	private BeanContainer<Long, BranchDM> beanbranch = null;
	private BeanContainer<Long, ExtrudersHdrDM> beanextrud = null;
	private BeanItemContainer<AssetDetailsDM> beanassetdetails = null;
	private BeanItemContainer<PulvizHdrDM> beanPulvizHdrDM = null;
	private BeanItemContainer<PulvizDtlDM> beanPulvizDtlDM = new BeanItemContainer<PulvizDtlDM>(PulvizDtlDM.class);
	private String branchName;
	private FormLayout flcolumn1, flcolumn2, flcolumn3, flcolumn4;
	private FormLayout flcolumn1dtl, flcolumn2dtl, flcolumn3dtl, flcolumn4dtl, flcolumn5dtl;
	private GERPTextArea tainstructhdr, taremarkdtl;
	private GERPTextField tfrefno, tflotno, tfoee, tfbalprcnt, tfip, tfop, tfbalqty, tfmaterialid;
	private GERPTimeField timestrt, timefend;
	private PopupDateField dfpulvizhdr, dfpulvizdtl;
	private GERPComboBox cbbrnchnamehdr, cbstatushdr, cbstatusdtl, cbmchnameldt, cbexredno;
	private VerticalLayout vlinputhdr, vlinputdtl;
	private Button btnAddPulvizDtl = new GERPButton("Add", "addbt", this);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private Table tblPulvizDtl;
	private List<PulvizDtlDM> pulvizdtllist = null;
	
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
	
	public void buidview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "building appraisallevel UI");
		cbbrnchnamehdr = new GERPComboBox("Branch Name");
		cbbrnchnamehdr.setItemCaptionPropertyId("branchName");
		loadbranchlist();
		cbexredno = new GERPComboBox("Extrud Ref.No");
		cbexredno.setItemCaptionPropertyId("extRefNo");
		loadextudersrefno();
		tflotno = new GERPTextField("LotNo");
		tfmaterialid = new GERPTextField("OP Material");
		cbstatushdr = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		dfpulvizhdr = new PopupDateField("Pulviz Date");
		dfpulvizhdr.setWidth("130");
		tainstructhdr = new GERPTextArea("Instruction");
		tainstructhdr.setHeight("75");
		tainstructhdr.setNullRepresentation("");
		tfrefno = new GERPTextField("Pulviz Ref.No");
		// pulviz Detail
		cbmchnameldt = new GERPComboBox("Machine Ref Name");
		cbmchnameldt.setItemCaptionPropertyId("assetName");
		cbmchnameldt.setRequired(true);
		loadmachinerefid();
		dfpulvizdtl = new PopupDateField("Date");
		dfpulvizdtl.setWidth("130");
		dfpulvizdtl.setRequired(true);
		tfip = new GERPTextField("Input");
		tfip.setWidth("100");
		tfop = new GERPTextField("Output");
		tfop.setWidth("100");
		tfbalqty = new GERPTextField("Bal Qty");
		tfbalqty.setWidth("100");
		tfoee = new GERPTextField("OEE");
		tfoee.setWidth("100");
		tfbalprcnt = new GERPTextField("Bal Prcnt");
		tfbalprcnt.setWidth("100");
		taremarkdtl = new GERPTextArea("Remark");
		taremarkdtl.setHeight("62");
		taremarkdtl.setWidth("110");
		cbstatusdtl = new GERPComboBox("Status", BASEConstants.T_STT_MFG_PULVIZ_DTL, BASEConstants.PULVIZ_STATUS);
		// Machine TimeIn TimeField
		timestrt = new GERPTimeField("Machine Time-In");
		// Machine TimeOut TimeField
		timefend = new GERPTimeField("Machine Time-Out");
		btnAddPulvizDtl.setStyleName("Add");
		btnAddPulvizDtl.setVisible(true);
		btnAddPulvizDtl.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validate_pulvizdtl()) {
					pulvizdtl_SaveDetails();
				}
			}
		});
		tblPulvizDtl = new GERPTable();
		tblPulvizDtl.setPageLength(10);
		tblPulvizDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblPulvizDtl.isSelected(event.getItemId())) {
					tblPulvizDtl.setImmediate(true);
					btnAddPulvizDtl.setCaption("Add");
					btnAddPulvizDtl.setStyleName("savebt");
					ResetpulvizDtl();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddPulvizDtl.setCaption("Update");
					btnAddPulvizDtl.setStyleName("savebt");
					editpulvizdetailDtl();
				}
			}
		});
		btndelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btndelete == event.getButton()) {
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
		loadpulvizdtl();
		btnAddPulvizDtl.setStyleName("add");
	}
	
	public void assemblsearch() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search");
		hlsearchlayout.removeAllComponents();
		flcolumn1 = new GERPFormLayout();
		flcolumn2 = new GERPFormLayout();
		flcolumn3 = new GERPFormLayout();
		flcolumn4 = new GERPFormLayout();
		flcolumn1.addComponent(cbbrnchnamehdr);
		flcolumn2.addComponent(tfrefno);
		flcolumn3.addComponent(dfpulvizhdr);
		flcolumn3.setSpacing(true);
		flcolumn3.setMargin(true);
		flcolumn4.addComponent(cbstatushdr);
		flcolumn4.setSpacing(true);
		cbstatushdr.setValue(cbstatushdr.getItemIds().iterator().next());
		hlsearchlayout.addComponent(flcolumn1);
		hlsearchlayout.addComponent(flcolumn2);
		hlsearchlayout.addComponent(flcolumn3);
		hlsearchlayout.addComponent(flcolumn4);
		hlsearchlayout.setSizeUndefined();
		hlsearchlayout.setSpacing(true);
		hlsearchlayout.setMargin(true);
	}
	
	public void assembleUserInputLayout() {
		logger.info("Company ID :" + companyid + " | User Name : " + username + ">" + "Assembliing User Input Layout");
		// pulviz hdr user input layout
		flcolumn1 = new GERPFormLayout();
		flcolumn2 = new GERPFormLayout();
		flcolumn3 = new GERPFormLayout();
		flcolumn4 = new GERPFormLayout();
		flcolumn1.addComponent(cbbrnchnamehdr);
		flcolumn1.addComponent(tfrefno);
		flcolumn1.addComponent(cbexredno);
		flcolumn2.addComponent(tflotno);
		flcolumn2.addComponent(dfpulvizhdr);
		flcolumn2.addComponent(tfmaterialid);
		flcolumn3.addComponent(tainstructhdr);
		flcolumn4.addComponent(cbstatushdr);
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
		flcolumn1dtl.addComponent(cbmchnameldt);
		flcolumn1dtl.addComponent(dfpulvizdtl);
		flcolumn2dtl.addComponent(tfip);
		flcolumn2dtl.addComponent(tfop);
		flcolumn2dtl.addComponent(tfbalqty);
		flcolumn3dtl.addComponent(tfoee);
		flcolumn3dtl.addComponent(tfbalprcnt);
		flcolumn4dtl.addComponent(timestrt);
		flcolumn4dtl.addComponent(timefend);
		flcolumn5dtl.addComponent(taremarkdtl);
		flcolumn5dtl.addComponent(cbstatusdtl);
		hluserInputlayoutDtl = new HorizontalLayout();
		hluserInputlayoutDtl.addComponent(flcolumn1dtl);
		hluserInputlayoutDtl.addComponent(flcolumn2dtl);
		hluserInputlayoutDtl.addComponent(flcolumn3dtl);
		hluserInputlayoutDtl.addComponent(flcolumn4dtl);
		hluserInputlayoutDtl.addComponent(flcolumn5dtl);
		VerticalLayout vl = new VerticalLayout();
		vl.addComponent(btnAddPulvizDtl);
		vl.addComponent(btndelete);
		hluserInputlayoutDtl.addComponent(vl);
		hluserInputlayoutDtl.setComponentAlignment(vl, Alignment.BOTTOM_RIGHT);
		/*
		 * btnAddPulvizDtl.setVisible(true); hluserInputlayoutDtl.addComponent(btnAddPulvizDtl);
		 * hluserInputlayoutDtl.setComponentAlignment(btnAddPulvizDtl, Alignment.MIDDLE_RIGHT);
		 * btndelete.setVisible(true); hluserInputlayoutDtl.addComponent(btndelete);
		 * hluserInputlayoutDtl.setComponentAlignment(btndelete, Alignment.MIDDLE_RIGHT);
		 */
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.removeAllItems();
		Long brnchid = null;
		if (cbbrnchnamehdr.getValue() != null) {
			brnchid = ((Long) cbbrnchnamehdr.getValue());
		}
		List<PulvizHdrDM> PulvizhdrList = new ArrayList<PulvizHdrDM>();
		PulvizhdrList = Pulvizhdrservice.getPulvizHdrDetails(null, brnchid, (String) tfrefno.getValue(),
				(Date) dfpulvizhdr.getValue(), (String) cbstatushdr.getValue(), "F");
		recordCnt = PulvizhdrList.size();
		beanPulvizHdrDM = new BeanItemContainer<PulvizHdrDM>(PulvizHdrDM.class);
		beanPulvizHdrDM.addAll(PulvizhdrList);
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
	
	private void loadpulvizdtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		logger.info("Company ID : " + companyid + " | savePulvizDetails User Name : " + username + " > "
				+ "Search Parameters are ");
		tblPulvizDtl.setPageLength(7);
		tblPulvizDtl.setFooterVisible(true);
		recdtl = pulvizdtllist.size();
		beanPulvizDtlDM = new BeanItemContainer<PulvizDtlDM>(PulvizDtlDM.class);
		beanPulvizDtlDM.addAll(pulvizdtllist);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the PulverizerDtl. result set");
		tblPulvizDtl.setContainerDataSource(beanPulvizDtlDM);
		tblPulvizDtl.setVisibleColumns(new Object[] { "machineName", "prodndate", "balanceqty", "plvzdtlstatus",
				"lastupdateddt", "lastupdatedby" });
		tblPulvizDtl.setColumnHeaders(new String[] { "Machine Ref Name", "Date", "Bal Qty", "Status",
				"Last Updated Date", "Last Updated By" });
		tblPulvizDtl.setColumnFooter("lastupdatedby", "No.of Records : " + recdtl);
	}
	
	private void pulvizdtl_SaveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Saving PulvizDtl Data... ");
			PulvizDtlDM savepulvizdtlobj = new PulvizDtlDM();
			if (tblPulvizDtl.getValue() != null) {
				savepulvizdtlobj = beanPulvizDtlDM.getItem(tblPulvizDtl.getValue()).getBean();
				pulvizdtllist.remove(savepulvizdtlobj);
			}
			savepulvizdtlobj.setMachid(((AssetDetailsDM) cbmchnameldt.getValue()).getAssetId());
			savepulvizdtlobj.setMachineName(((AssetDetailsDM) cbmchnameldt.getValue()).getAssetName());
			savepulvizdtlobj.setProdndate(dfpulvizdtl.getValue());
			if (Long.valueOf(tfip.getValue()) != null) {
				savepulvizdtlobj.setInputqty(Long.valueOf(tfip.getValue()));
			}
			if (Long.valueOf(tfop.getValue()) != null) {
				savepulvizdtlobj.setOutputqty(Long.valueOf(tfop.getValue()));
			}
			if (Long.valueOf(tfbalqty.getValue()) != null) {
				savepulvizdtlobj.setBalanceqty(Long.valueOf(tfbalqty.getValue()));
			}
			if (Long.valueOf(tfoee.getValue()) != null) {
				savepulvizdtlobj.setOeeprcnt(Long.valueOf(tfoee.getValue()));
			}
			if (Long.valueOf(tfbalprcnt.getValue()) != null) {
				savepulvizdtlobj.setBalprcnt(Long.valueOf(tfbalprcnt.getValue()));
			}
			if (timestrt.getValue() != null) {
				savepulvizdtlobj.setMchnstart(timestrt.getHorsMunites());
			}
			if (timefend.getValue() != null) {
				savepulvizdtlobj.setMchnend(timefend.getHorsMunites());
			}
			savepulvizdtlobj.setRemark(taremarkdtl.getValue());
			savepulvizdtlobj.setPreparedby(EmployeeId);
			savepulvizdtlobj.setReviewby(null);
			savepulvizdtlobj.setActionedby(null);
			if (cbstatusdtl.getValue() != null) {
				savepulvizdtlobj.setPlvzdtlstatus(cbstatusdtl.getValue().toString());
			}
			savepulvizdtlobj.setLastupdateddt(DateUtils.getcurrentdate());
			savepulvizdtlobj.setLastupdatedby(username);
			pulvizdtllist.add(savepulvizdtlobj);
			loadpulvizdtl();
			btnAddPulvizDtl.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		ResetpulvizDtl();
	}
	
	private void loadbranchlist() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading Branchlist");
		List<BranchDM> brnchlist = servicebranch.getBranchList(brnchid, branchName, null, "Active", companyid, "F");
		beanbranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanbranch.setBeanIdProperty("branchId");
		beanbranch.addAll(brnchlist);
		cbbrnchnamehdr.setContainerDataSource(beanbranch);
	}
	
	private void loadextudersrefno() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading Extrud refno");
		List<ExtrudersHdrDM> extrudref = extrudservice.getExtruderList(extId, companyid, brnchid, null, null, null,
				null, null, "Active", "F");
		beanextrud = new BeanContainer<Long, ExtrudersHdrDM>(ExtrudersHdrDM.class);
		beanextrud.setBeanIdProperty("extId");
		beanextrud.addAll(extrudref);
		cbexredno.setContainerDataSource(beanextrud);
	}
	
	public void loadmachinerefid() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading pulviz Reference No...");
		List<AssetDetailsDM> assetdtl = serviceassetdetails.getAssetDetailList(null, assetId, null, null, null,
				"Active");
		beanassetdetails = new BeanItemContainer<AssetDetailsDM>(AssetDetailsDM.class);
		beanassetdetails.addAll(assetdtl);
		cbmchnameldt.setContainerDataSource(beanassetdetails);
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
		tfrefno.setValue("");
		cbbrnchnamehdr.setValue(null);
		dfpulvizhdr.setValue(null);
		cbstatushdr.setValue(cbstatushdr.getItemIds().iterator().next());
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
		cbbrnchnamehdr.setRequired(true);
		resetFields();
		ResetpulvizDtl();
		assembleUserInputLayout();
		loadpulvizdtl();
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid, "STT_MF_PVZREFNO");
		tfrefno.setReadOnly(true);
		tflotno.setReadOnly(true);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfrefno.setReadOnly(true);
			} else {
				tfrefno.setReadOnly(false);
			}
			tblPulvizDtl.setVisible(true);
			lblNotification.setValue("");
			btnAddPulvizDtl.setCaption("Add");
		}
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing new record...");
		hlCmdBtnLayout.setVisible(false);
		hluserInputlayout.removeAllComponents();
		assembleUserInputLayout();
		cbbrnchnamehdr.setRequired(true);
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid, "STT_MF_PVZREFNO ");
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		tblMstScrSrchRslt.setVisible(false);
		if (tfrefno.getValue() == null || tfrefno.getValue().trim().length() == 0) {
			tfrefno.setReadOnly(false);
		}
		if (tflotno.getValue() == null || tfrefno.getValue().trim().length() == 0) {
			tflotno.setReadOnly(false);
		}
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfrefno.setReadOnly(true);
				tflotno.setReadOnly(true);
			}
		}
		resetFields();
		ResetpulvizDtl();
		editpulvizHdr();
		editpulvizdetailDtl();
	}
	
	public void editpulvizHdr() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hluserInputlayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (sltedRcd != null) {
			PulvizHdrDM editpulvizhdr = beanPulvizHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			pulvizid = editpulvizhdr.getPulvizid();
			cbbrnchnamehdr.setValue(editpulvizhdr.getBranchid());
			tfrefno.setReadOnly(false);
			tfrefno.setValue(editpulvizhdr.getPulvizreffno());
			tfrefno.setReadOnly(true);
			if (editpulvizhdr.getPulvizdate() != null) {
				dfpulvizhdr.setValue(editpulvizhdr.getPulvizdate1());
			}
			tfmaterialid.setValue(editpulvizhdr.getOpmaterialid().toString());
			tflotno.setValue(editpulvizhdr.getLotno());
			tflotno.setReadOnly(true);
			if (editpulvizhdr.getPulvizstatus() != null) {
				cbstatushdr.setValue(editpulvizhdr.getPulvizstatus());
			}
			if (editpulvizhdr.getExtrudid() != null) {
				cbexredno.setValue(editpulvizhdr.getExtrudid());
			}
			tainstructhdr.setValue(editpulvizhdr.getInstruction());
			/*
			 * if (editpulvizhdr.getOpmaterialid() != null){ tfmaterialid.setValue(editpulvizhdr.getOpmaterialid()); }
			 */
			tblPulvizDtl.removeAllItems();
			pulvizdtllist.addAll(pulvizDtlservice.getPulvizDtlDetails(null, (Long.valueOf(pulvizid)), null, null, "F"));
		}
		loadpulvizdtl();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbbrnchnamehdr.setComponentError(null);
		tfmaterialid.setComponentError(null);
		boolean errorflag = false;
		if ((cbbrnchnamehdr.getValue() == null)) {
			cbbrnchnamehdr.setComponentError(new UserError(GERPErrorCodes.NULL_BRANCH_NAME));
			errorflag = true;
		}
		Long prdctnQty;
		try {
			prdctnQty = Long.valueOf(tfmaterialid.getValue());
			if (prdctnQty < 0) {
				tfmaterialid.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorflag = true;
			}
		}
		catch (Exception e) {
			tfmaterialid.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			errorflag = true;
		}
		if (errorflag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			validate_pulvizdtl();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			PulvizHdrDM pulvizhdrobj = new PulvizHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				pulvizhdrobj = beanPulvizHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			} else {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid,
						"STT_MF_PVZREFNO");
				logger.info("Serial No Generation  Data...===> " + companyid + "," + branchid + "," + moduleid);
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						pulvizhdrobj.setPulvizreffno(slnoObj.getKeyDesc());
					}
				}
			}
			List<SlnoGenDM> slno1List = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid,
					"STT_MF_PVZLOTNO");
			logger.info("Serial No Generation  Data...===> " + companyid + "," + branchid + "," + moduleid);
			for (SlnoGenDM slnoObj : slno1List) {
				if (slnoObj.getAutoGenYN().equals("Y")) {
					pulvizhdrobj.setLotno(slnoObj.getKeyDesc());
				}
			}
			if (Long.valueOf(tfmaterialid.getValue()) != null) {
				pulvizhdrobj.setOpmaterialid(Long.valueOf(tfmaterialid.getValue()));
			}
			if (cbbrnchnamehdr.getValue() != null) {
				pulvizhdrobj.setBranchid((Long) cbbrnchnamehdr.getValue());
			}
			if (cbexredno.getValue() != null) {
				pulvizhdrobj.setExtrudid((Long) cbexredno.getValue());
			}
			pulvizhdrobj.setCompanyid(companyid);
			if (dfpulvizhdr.getValue() != null) {
				pulvizhdrobj.setPulvizdate(dfpulvizhdr.getValue());
			}
			pulvizhdrobj.setInstruction(tainstructhdr.getValue());
			if (cbstatushdr.getValue() != null) {
				pulvizhdrobj.setPulvizstatus(cbstatushdr.getValue().toString());
			}
			pulvizhdrobj.setLastupdateddate(DateUtils.getcurrentdate());
			pulvizhdrobj.setLastupdatedby(username);
			validate_pulvizdtl();
			Pulvizhdrservice.saveorupdatePulvizHdr(pulvizhdrobj);
			@SuppressWarnings("unchecked")
			Collection<PulvizDtlDM> itemids = (Collection<PulvizDtlDM>) tblPulvizDtl.getVisibleItemIds();
			for (PulvizDtlDM save : (Collection<PulvizDtlDM>) itemids) {
				save.setPulvizid(Long.valueOf(pulvizhdrobj.getPulvizid().toString()));
				pulvizDtlservice.saveOrUpdatepulvizDtl(save);
			}
			hluserInputlayout.setMargin(false);
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid,
						"STT_MF_PVZREFNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchid, moduleid, "STT_MF_PVZREFNO");
						System.out.println("Serial no=>" + companyid + "," + moduleid + "," + branchid);
					}
				}
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchid, moduleid,
						"STT_MF_PVZLOTNO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchid, moduleid, "STT_MF_PVZLOTNO");
						System.out.println("Serial no=>" + companyid + "," + moduleid + "," + branchid);
					}
				}
			}
			ResetpulvizDtl();
			resetFields();
			loadSrchRslt();
			pulvizid = null;
			loadpulvizdtl();
		}
		catch (Exception e) {
			e.printStackTrace();
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
		dfpulvizhdr.setRequired(false);
		cbbrnchnamehdr.setRequired(false);
		assemblsearch();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
		ResetpulvizDtl();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resitting the UI controls");
		tfrefno.setReadOnly(false);
		tflotno.setReadOnly(false);
		cbexredno.setValue(null);
		tfrefno.setValue("");
		tflotno.setValue("");
		cbexredno.setComponentError(null);
		tfmaterialid.setValue("0");
		tainstructhdr.setValue("");
		tainstructhdr.setComponentError(null);
		cbbrnchnamehdr.setValue(null);
		cbbrnchnamehdr.setComponentError(null);
		dfpulvizhdr.setValue(null);
		dfpulvizhdr.setComponentError(null);
		cbstatushdr.setValue(cbstatushdr.getItemIds().iterator().next());
		pulvizdtllist = new ArrayList<PulvizDtlDM>();
		tblPulvizDtl.removeAllItems();
		recordCnt = 0;
	}
	
	private void ResetpulvizDtl() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "reset_PulvizDtl...");
		cbmchnameldt.setValue(null);
		cbmchnameldt.setComponentError(null);
		dfpulvizdtl.setValue(null);
		dfpulvizdtl.setComponentError(null);
		tfoee.setValue("0");
		taremarkdtl.setValue("");
		tfbalprcnt.setValue("0");
		tfip.setValue("0");
		tfop.setValue("0");
		tfbalqty.setValue("0");
		recordCnt = 0;
		cbstatusdtl.setValue(cbstatusdtl.getItemIds().iterator().next());
		timestrt.setValue(null);
		timefend.setValue(null);
	}
	
	private void editpulvizdetailDtl() {
		Item sltedRcd = tblPulvizDtl.getItem(tblPulvizDtl.getValue());
		if (sltedRcd != null) {
			PulvizDtlDM editpulvizdtl = new PulvizDtlDM();
			editpulvizdtl = beanPulvizDtlDM.getItem(tblPulvizDtl.getValue()).getBean();
			Long mch = editpulvizdtl.getMachid();
			Collection<?> mchid = cbmchnameldt.getItemIds();
			for (Iterator<?> iterator = mchid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbmchnameldt.getItem(itemId);
				AssetDetailsDM add = (AssetDetailsDM) item.getBean();
				if (mch != null && mch.equals(add.getAssetId())) {
					cbmchnameldt.setValue(itemId);
				}
			}
			if (editpulvizdtl.getProdndate() != null) {
				dfpulvizdtl.setValue(editpulvizdtl.getProdndate1());
			}
			/*
			 * if (editpulvizdtl.getProdndate() != null) { dfpulvizdtl.setValue(editpulvizdtl.getProdndate1()); }
			 */
			tfip.setValue(editpulvizdtl.getInputqty().toString());
			tfop.setValue(editpulvizdtl.getOutputqty().toString());
			if (editpulvizdtl.getBalanceqty() != null) {
				tfbalqty.setValue(editpulvizdtl.getBalanceqty().toString());
			}
			if (editpulvizdtl.getOeeprcnt() != null) {
				tfoee.setValue(editpulvizdtl.getOeeprcnt().toString());
			}
			if (editpulvizdtl.getBalprcnt() != null) {
				tfbalprcnt.setValue(editpulvizdtl.getBalprcnt().toString());
			}
			if (editpulvizdtl.getPlvzdtlstatus() != null) {
				cbstatusdtl.setValue(sltedRcd.getItemProperty("plvzdtlstatus").getValue().toString());
			}
			timestrt.setTime(editpulvizdtl.getMchnstart());
			timefend.setTime(editpulvizdtl.getMchnend());
			if (editpulvizdtl.getRemark() != null) {
				taremarkdtl.setValue(sltedRcd.getItemProperty("remark").getValue().toString());
			}
		}
	}
	
	private boolean validate_pulvizdtl() {
		tfip.setComponentError(null);
		tfop.setComponentError(null);
		tfbalqty.setComponentError(null);
		tfoee.setComponentError(null);
		tfbalprcnt.setComponentError(null);
		boolean validpuldtl = true;
		if (cbmchnameldt.getValue() == null) {
			cbmchnameldt.setComponentError(new UserError(GERPErrorCodes.NULL_MACHINE_NAME));
			validpuldtl = false;
		} else {
			cbmchnameldt.setComponentError(null);
		}
		if (dfpulvizdtl.getValue() == null) {
			dfpulvizdtl.setComponentError(new UserError(GERPErrorCodes.NULL_PULVIZDTL_DATE));
			validpuldtl = false;
		} else {
			dfpulvizdtl.setComponentError(null);
		}
		Long input;
		try {
			input = Long.valueOf(tfip.getValue());
			if (input < 0) {
				tfip.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				validpuldtl = false;
			}
		}
		catch (Exception e) {
			tfip.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			validpuldtl = false;
		}
		Long output;
		try {
			output = Long.valueOf(tfop.getValue());
			if (output < 0) {
				tfop.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				validpuldtl = false;
			}
		}
		catch (Exception e) {
			tfop.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			validpuldtl = false;
		}
		Long balQty;
		try {
			balQty = Long.valueOf(tfbalqty.getValue());
			if (balQty < 0) {
				tfbalqty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				validpuldtl = false;
			}
		}
		catch (Exception e) {
			tfbalqty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			validpuldtl = false;
		}
		Long oee;
		try {
			oee = Long.valueOf(tfoee.getValue());
			if (oee < 0) {
				tfoee.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				validpuldtl = false;
			}
		}
		catch (Exception e) {
			tfoee.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			validpuldtl = false;
		}
		Long balper;
		try {
			balper = Long.valueOf(tfbalprcnt.getValue());
			if (balper < 0) {
				tfbalprcnt.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				validpuldtl = false;
			}
		}
		catch (Exception e) {
			tfbalprcnt.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			validpuldtl = false;
		}
		return validpuldtl;
	}
	
	private void deleteDetails() {
		PulvizDtlDM remove = new PulvizDtlDM();
		if (tblPulvizDtl.getValue() != null) {
			remove = beanPulvizDtlDM.getItem(tblPulvizDtl.getValue()).getBean();
			pulvizdtllist.remove(remove);
			ResetpulvizDtl();
			loadpulvizdtl();
		}
	}
}
