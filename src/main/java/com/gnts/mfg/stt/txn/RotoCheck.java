package com.gnts.mfg.stt.txn;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.components.GERPTimeField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.stt.mfg.domain.txn.RotoArmDM;
import com.gnts.stt.mfg.domain.txn.RotoDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanHdrDM;
import com.gnts.stt.mfg.domain.txn.RotohdrDM;
import com.gnts.stt.mfg.service.txn.RotoArmService;
import com.gnts.stt.mfg.service.txn.RotoDtlService;
import com.gnts.stt.mfg.service.txn.RotoPlanDtlService;
import com.gnts.stt.mfg.service.txn.RotoPlanHdrService;
import com.gnts.stt.mfg.service.txn.RotohdrService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class RotoCheck extends BaseTransUI {
	private RotohdrService serviceRotohdr = (RotohdrService) SpringContextHelper.getBean("rotohdr");
	private RotoDtlService serviceRotoDtl = (RotoDtlService) SpringContextHelper.getBean("rotodtl");
	private RotoArmService serviceRotoArm = (RotoArmService) SpringContextHelper.getBean("rotoarm");
	private RotoPlanDtlService serviceRotoplandtl = (RotoPlanDtlService) SpringContextHelper.getBean("rotoplandtl");
	private BranchService servicebeanBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private RotoPlanHdrService serviceRotoplanhdr = (RotoPlanHdrService) SpringContextHelper.getBean("rotoplanhdr");
	// User Input Components for Work Order Details
	private BeanItemContainer<RotohdrDM> beanRotohdrDM = null;
	private BeanItemContainer<RotoDtlDM> beanrotodtldm = null;
	private BeanItemContainer<RotoArmDM> beanRotoArmDM = null;
	private Table tblRotoDetails, tblRotoArm;
	// Search Control Layout
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlSearchLayout, hlHdrAndShift, hlArm, hlHdrslap;
	private VerticalLayout vlArm, vlDtl, vlHdrshiftandDtlarm, hlDtlandArm;
	private FormLayout flArmCol1, flArmCol2, flArmCol3, flArmCol4, flArmCol5;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Roto Hdr Components
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4;
	// Roto Dtl Components
	private FormLayout flDtlCol1, flDtlCol2, flDtlCol3;
	// Roto Arm Components
	private Button btnAddDtls = new GERPButton("Add", "Addbt", this);
	private Button btnAddArm = new GERPButton("Add", "Addbt", this);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private Button btnArmDelete = new GERPButton("Delete", "delete", this);
	private GERPPopupDateField dfRotoDt;
	private GERPTextField tfRotoRef, tfPlanedQty, tfProdQty;
	private GERPComboBox cbBranch, cbPlanRef;
	private TextArea tfRemarks;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	// Roto Details
	private GERPTextField tfOvenTotal, tfCharTot, tfCoolTot, tfBoxModel, tfPwdTop, tfPwdBot, tfPowTotal;
	private GERPTextField tftempZ1, tftempZ2, tftempZ3, tfBoxWgTop, tfBoxWgBot, tfBoxWgTotal, tfCycles, tfKgCm,
			tfEmpNo;
	private GERPTimeField tmOvenOn, tmOvenOff, tmCharOn, tmCharOff, tmCoolOn, tmCoolOff;
	private GERPComboBox cbArmNo;
	private TextArea tfRemarksDtl;
	private String username;
	private Long companyid, branchID, moduleId;
	private Long rotodtlid;
	private Long rotoid;
	private int recordCnt = 0;
	private int recordArmCnt = 0;
	private Boolean errorFlag = false;
	private Long rotoplanId;
	// Initialize logger
	private Logger logger = Logger.getLogger(RotoCheck.class);
	private static final long serialVersionUID = 1L;
	
	public RotoCheck() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside AssemblyPlan() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		// Initialization for work order Details user input components
		btnAddArm.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateArmDetails()) {
					save();
				}
			}
		});
		// RotoCheck Header.
		tblRotoDetails = new Table();
		tblRotoDetails.setSelectable(true);
		tblRotoDetails.setWidth("588px");
		tblRotoDetails.setPageLength(5);
		tblRotoDetails.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblRotoDetails.isSelected(event.getItemId())) {
					tblRotoDetails.setImmediate(true);
					btnAddDtls.setCaption("Add");
					btnAddDtls.setStyleName("savebt");
					// rotoDtlResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtls.setCaption("Update");
					btnAddDtls.setStyleName("savebt");
					// editRotoDtls();
				}
			}
		});
		tblRotoArm = new GERPTable();
		tblRotoArm.setWidth("1150px");
		tblRotoArm.setPageLength(5);
		tblRotoArm.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblRotoArm.isSelected(event.getItemId())) {
					tblRotoArm.setImmediate(true);
					btnAddArm.setCaption("Add");
					btnAddArm.setStyleName("savebt");
					rotoArmResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddArm.setCaption("Update");
					btnAddArm.setStyleName("savebt");
					editArmDetails();
				}
			}
		});
		tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMstScrSrchRslt.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
					btnAdd.setEnabled(true);
				} else {
					btnEdit.setEnabled(true);
					btnAdd.setEnabled(false);
				}
				resetFields();
			}
		});
		btnArmDelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnArmDelete == event.getButton()) {
					// deleteArmDetails();
					btnAddArm.setCaption("Add");
				}
			}
		});
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting FormPlan UI");
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setRequired(true);
		cbBranch.setWidth("150");
		loadBranchList();
		tfRotoRef = new GERPTextField("Roto RefNo.");
		tfRotoRef.setWidth("150");
		tfPlanedQty = new GERPTextField("Planed Qty.");
		tfPlanedQty.setWidth("150");
		tfProdQty = new GERPTextField("Prod. Qty");
		tfProdQty.setWidth("150");
		tfRemarks = new TextArea("Remarks");
		tfRemarks.setHeight("70px");
		tfRemarks.setWidth("150");
		cbPlanRef = new GERPComboBox("Plan RefNo.");
		cbPlanRef.setRequired(true);
		cbPlanRef.setWidth("150");
		loadRotoPlanList();
		cbPlanRef.setItemCaptionPropertyId("rotoplanrefno");
		cbPlanRef.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbPlanRef.getItem(itemId);
				if (item != null) {
					dfRotoDt.setValue(((RotoPlanHdrDM) cbPlanRef.getValue()).getRotoplandt1());
					rotoplanId = (((RotoPlanHdrDM) cbPlanRef.getValue()).getRotoplanidLong());
					System.out.println("rotoplanId--->" + rotoplanId);
					tfPlanedQty.setValue(((RotoPlanHdrDM) cbPlanRef.getValue()).getPlannedqty().toString());
					loadPlanDtlRslt();
				}
			}
		});
		dfRotoDt = new GERPPopupDateField("Date");
		dfRotoDt.setRequired(true);
		dfRotoDt.setDateFormat("dd-MMM-yyyy");
		dfRotoDt.setInputPrompt("Select Date");
		dfRotoDt.setWidth("130px");
		cbStatus.setWidth("150");
		cbArmNo = new GERPComboBox("Arm No.");
		cbArmNo.addItem("1");
		cbArmNo.addItem("2");
		cbArmNo.addItem("3");
		cbArmNo.addItem("4");
		cbArmNo.setRequired(true);
		cbArmNo.setWidth("130");
		tmOvenOn = new GERPTimeField("Oven On");
		tmOvenOn.setWidth("150");
		tmOvenOff = new GERPTimeField("Oven Off");
		tmOvenOff.setWidth("150");
		tfOvenTotal = new GERPTextField("Oven Total");
		tfOvenTotal.setWidth("130");
		tmCharOn = new GERPTimeField("Charge On");
		tmCharOn.setWidth("150");
		tmCharOff = new GERPTimeField("Change Off");
		tmCharOff.setWidth("150");
		tfCharTot = new GERPTextField("Charge Total");
		tfCharTot.setWidth("130");
		tmCoolOn = new GERPTimeField("Cooling On");
		tmCoolOn.setWidth("150");
		tmCoolOff = new GERPTimeField("Cooling Off");
		tmCoolOff.setWidth("150");
		tfCoolTot = new GERPTextField("Cooling Total");
		tfCoolTot.setWidth("130");
		tftempZ1 = new GERPTextField("Z1");
		tftempZ1.setWidth("100");
		tftempZ2 = new GERPTextField("Z2");
		tftempZ2.setWidth("100");
		tftempZ3 = new GERPTextField("Z3");
		tftempZ3.setWidth("100");
		tfBoxModel = new GERPTextField("Box Model");
		tfBoxModel.setWidth("100");
		tfPwdTop = new GERPTextField("Powder Top");
		tfPwdTop.setWidth("100");
		tfPwdBot = new GERPTextField("Powder Bottom");
		tfPwdBot.setWidth("100");
		tfPowTotal = new GERPTextField("Powder Total");
		tfPowTotal.setWidth("100");
		tfBoxWgTop = new GERPTextField("Box.Wg Top");
		tfBoxWgTop.setWidth("100");
		tfBoxWgBot = new GERPTextField("Box.Wg Bottom");
		tfBoxWgBot.setWidth("100");
		tfBoxWgTotal = new GERPTextField("Box.Wg Total");
		tfBoxWgTotal.setWidth("100");
		tfCycles = new GERPTextField("Cycles");
		tfCycles.setWidth("130");
		tfKgCm = new GERPTextField("Kg/Cm3");
		tfKgCm.setWidth("130");
		tfEmpNo = new GERPTextField("No.of Emp.");
		tfEmpNo.setWidth("130");
		tfRemarksDtl = new GERPTextArea("Remarks");
		tfRemarksDtl.setWidth("130");
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadArmRslt();
		loadPlanDtlRslt();
		btnAddDtls.setStyleName("add");
		btnAddArm.setStyleName("add");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Roto planning search layout");
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		Label lbl = new Label();
		flHdrCol4.addComponent(tfRotoRef);
		flHdrCol1.addComponent(dfRotoDt);
		flHdrCol2.addComponent(lbl);
		flHdrCol3.addComponent(cbStatus);
		hlSearchLayout.addComponent(flHdrCol4);
		hlSearchLayout.addComponent(flHdrCol1);
		hlSearchLayout.addComponent(flHdrCol2);
		hlSearchLayout.addComponent(flHdrCol3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Form planning search layout");
		hlSearchLayout.removeAllComponents();
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol1.addComponent(cbBranch);
		flHdrCol1.addComponent(cbPlanRef);
		flHdrCol1.addComponent(tfRotoRef);
		flHdrCol1.addComponent(dfRotoDt);
		flHdrCol1.addComponent(tfPlanedQty);
		flHdrCol2.addComponent(tfProdQty);
		flHdrCol2.addComponent(tfRemarks);
		flHdrCol2.addComponent(cbStatus);
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdrCol1);
		hlHdr.addComponent(flHdrCol2);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding Arm Components
		flArmCol1 = new FormLayout();
		flArmCol2 = new FormLayout();
		flArmCol3 = new FormLayout();
		flArmCol4 = new FormLayout();
		flArmCol5 = new FormLayout();
		flArmCol1.addComponent(cbArmNo);
		flArmCol1.addComponent(tmOvenOn);
		flArmCol1.addComponent(cbArmNo);
		flArmCol1.addComponent(tmOvenOn);
		flArmCol1.addComponent(tmOvenOff);
		flArmCol1.addComponent(tfOvenTotal);
		flArmCol1.addComponent(tmCharOn);
		flArmCol2.addComponent(tmCharOff);
		flArmCol2.addComponent(tfCharTot);
		flArmCol2.addComponent(tmCoolOn);
		flArmCol2.addComponent(tmCoolOff);
		flArmCol2.addComponent(tfCoolTot);
		flArmCol3.addComponent(tftempZ1);
		flArmCol3.addComponent(tftempZ2);
		flArmCol3.addComponent(tftempZ3);
		flArmCol3.addComponent(tfBoxModel);
		flArmCol3.addComponent(tfPwdTop);
		flArmCol4.addComponent(tfPwdBot);
		flArmCol4.addComponent(tfPowTotal);
		flArmCol4.addComponent(tfBoxWgTop);
		flArmCol4.addComponent(tfBoxWgBot);
		flArmCol4.addComponent(tfBoxWgTotal);
		flArmCol5.addComponent(tfCycles);
		flArmCol5.addComponent(tfKgCm);
		flArmCol5.addComponent(tfEmpNo);
		flArmCol5.addComponent(tfRemarksDtl);
		hlArm = new HorizontalLayout();
		hlArm.setSpacing(true);
		hlArm.addComponent(flArmCol1);
		hlArm.addComponent(flArmCol2);
		hlArm.addComponent(flArmCol3);
		hlArm.addComponent(flArmCol4);
		hlArm.addComponent(flArmCol5);
		hlArm.setSpacing(true);
		hlArm.setMargin(true);
		// Adding Dtl Components
		flDtlCol1 = new FormLayout();
		flDtlCol2 = new FormLayout();
		flDtlCol3 = new FormLayout();
		HorizontalLayout hlBtn = new HorizontalLayout();
		hlBtn.addComponent(btnAddDtls);
		hlBtn.addComponent(btndelete);
		flDtlCol3.addComponent(hlBtn);
		hlHdrslap = new HorizontalLayout();
		hlHdrslap.addComponent(flDtlCol1);
		hlHdrslap.addComponent(flDtlCol2);
		hlHdrslap.addComponent(flDtlCol3);
		hlHdrslap.setSpacing(true);
		hlHdrslap.setMargin(true);
		vlArm = new VerticalLayout();
		vlArm.addComponent(hlArm);
		vlArm.addComponent(tblRotoArm);
		vlDtl = new VerticalLayout();
		vlDtl.addComponent(tblRotoDetails);
		hlDtlandArm = new VerticalLayout();
		hlDtlandArm.addComponent(GERPPanelGenerator.createPanel(vlArm));
		hlDtlandArm.setSpacing(true);
		hlDtlandArm.setHeight("100%");
		hlHdrAndShift = new HorizontalLayout();
		hlHdrAndShift.addComponent(hlHdr);
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(vlDtl));
		vlHdrshiftandDtlarm = new VerticalLayout();
		vlHdrshiftandDtlarm.addComponent(GERPPanelGenerator.createPanel(hlHdrAndShift));
		vlHdrshiftandDtlarm.addComponent(GERPPanelGenerator.createPanel(hlDtlandArm));
		vlHdrshiftandDtlarm.setSpacing(true);
		vlHdrshiftandDtlarm.setWidth("100%");
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.addComponent(vlHdrshiftandDtlarm);
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		tblMstScrSrchRslt.setPageLength(14);
		List<RotohdrDM> listRotoHdr = new ArrayList<RotohdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfRotoRef.getValue() + ", " + cbStatus.getValue());
		listRotoHdr = serviceRotohdr.getRotohdrDetatils(null, (String) cbPlanRef.getValue(), companyid,
				dfRotoDt.getValue(), cbStatus.getValue().toString(), "F");
		recordCnt = listRotoHdr.size();
		beanRotohdrDM = new BeanItemContainer<RotohdrDM>(RotohdrDM.class);
		beanRotohdrDM.addAll(listRotoHdr);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Roto. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanRotohdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "rotoid", "rotorefno", "rotodate", "rotostatus",
				"lastupdateddate", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Roto Ref No", "Roto Date", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("rotoid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadPlanDtlRslt() {
		List<RotoPlanDtlDM> rotoplandm = new ArrayList<RotoPlanDtlDM>();
		rotoplandm = serviceRotoplandtl.getRotoPlanDtlList(null, rotoplanId, null, null, null);
		List<RotoDtlDM> rotodtldm = new ArrayList<RotoDtlDM>();
		for (RotoPlanDtlDM obj : rotoplandm) {
			RotoDtlDM list = new RotoDtlDM();
			list.setClientid(obj.getClientId());
			list.setClientName(obj.getClientname());
			list.setWoid(obj.getWoId());
			list.setWoNo(obj.getWoNo());
			list.setProdName(obj.getProductname());
			list.setProductid(obj.getProductId());
			list.setPlannedqty(obj.getPlannedqty());
			list.setRtodtlstatus(obj.getRtoplndtlstatus());
			list.setLastupdatedby(obj.getLastupdatedBy());
			list.setLastupdateddt(obj.getLastupdatedDt());
			rotodtldm.add(list);
			beanrotodtldm = new BeanItemContainer<RotoDtlDM>(RotoDtlDM.class);
			beanrotodtldm.addAll(rotodtldm);
			tblRotoDetails.setContainerDataSource(beanrotodtldm);
			tblRotoDetails
					.setVisibleColumns(new Object[] { "clientName", "woNo", "prodName", "plannedqty", "prodtnqty" });
			tblRotoDetails.setColumnHeaders(new String[] { "Client Name", "WO No.", "Product Name", "Planned Qty.",
					"Product Qty" });
		}
	}
	
	private void loadArmRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<RotoArmDM> armList = new ArrayList<RotoArmDM>();
		recordArmCnt = armList.size();
		armList = serviceRotoArm.getRotoArmList(null, null, null, null, null, "F", null);
		beanRotoArmDM = new BeanItemContainer<RotoArmDM>(RotoArmDM.class);
		beanRotoArmDM.addAll(armList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Roto. result set");
		tblRotoArm.setContainerDataSource(beanRotoArmDM);
		tblRotoArm.setVisibleColumns(new Object[] { "prodname", "empName", "workOrdrNo", "cycleno", "armno" });
		tblRotoArm.setColumnHeaders(new String[] { "Product Name", "Employee name", "WO No.", "Cycle No", "Arm No" });
		tblRotoArm.setColumnAlignment("cycleno", Align.RIGHT);
		tblRotoArm.setColumnFooter("armno", "No.of Records : " + recordArmCnt);
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
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		dfRotoDt.setValue(null);
		cbPlanRef.setValue("");
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
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		dfRotoDt.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtls.setCaption("Add");
		btnAddArm.setCaption("Add");
		tfRotoRef.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTONO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfRotoRef.setValue(slnoObj.getKeyDesc());
				tfRotoRef.setReadOnly(true);
			} else {
				tfRotoRef.setReadOnly(false);
			}
		}
		catch (Exception e) {
		}
		tblRotoDetails.setVisible(true);
		loadArmRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		cbBranch.setRequired(true);
		dfRotoDt.setRequired(true);
		// reset the input controls to default value comment
		tblMstScrSrchRslt.setVisible(false);
		if (tfRotoRef.getValue() == null || tfRotoRef.getValue().trim().length() == 0) {
			tfRotoRef.setReadOnly(false);
		}
		assembleInputUserLayout();
		resetFields();
		editRotoHdrDetails();
		editRotoDtls();
		editArmDetails();
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		cbBranch.setComponentError(null);
		dfRotoDt.setComponentError(null);
		tfRotoRef.setComponentError(null);
		cbBranch.setRequired(true);
		dfRotoDt.setRequired(true);
		resetRotoDetails();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for FoamPlan. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_MFG_WORKORDER_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", rotodtlid);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbBranch.setValue(cbBranch.getItemIds().iterator().next());
		tfRotoRef.setReadOnly(false);
		tfRotoRef.setValue("");
		tfRotoRef.setComponentError(null);
		dfRotoDt.setValue(null);
		tfRemarks.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		dfRotoDt.setComponentError(null);
		cbBranch.setComponentError(null);
		tblRotoDetails.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editRotoHdrDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			RotohdrDM editRotohdr = beanRotohdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			rotoid = editRotohdr.getRotoid();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected AssemblyPlan. Id -> " + rotodtlid);
			cbBranch.setValue(editRotohdr.getBranchid());
			tfRotoRef.setReadOnly(false);
			tfRotoRef.setValue(editRotohdr.getRotorefno());
			tfRotoRef.setReadOnly(true);
			if (editRotohdr.getRotodate() != null) {
				dfRotoDt.setValue(editRotohdr.getRotodate1());
			}
			tfRemarks.setValue(editRotohdr.getRemarks());
			cbStatus.setValue(editRotohdr.getRotostatus());
		}
		loadPlanDtlRslt();
		loadArmRslt();
	}
	
	private void editRotoDtls() {
		hlUserInputLayout.setVisible(true);
	}
	
	private void editArmDetails() {
	}
	
	private void resetRotoDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
	}
	
	private void rotoArmResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbBranch.setComponentError(null);
		dfRotoDt.setComponentError(null);
		errorFlag = false;
		if ((cbBranch.getValue() == null)) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbBranch.getValue());
			errorFlag = true;
		}
		if ((dfRotoDt.getValue() == null)) {
			dfRotoDt.setComponentError(new UserError(GERPErrorCodes.NULL_ASMBL_PLAN_DT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfRotoDt.getValue());
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validateArmDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		return isValid;
	}
	
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotohdrDM rotohdrDM = new RotohdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				rotohdrDM = beanRotohdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			rotohdrDM.setRotorefno(tfRotoRef.getValue());
			rotohdrDM.setBranchid((Long.valueOf(cbBranch.getValue().toString())));
			rotohdrDM.setRotodate(dfRotoDt.getValue());
			rotohdrDM.setRemarks(tfRemarks.getValue());
			rotohdrDM.setRotostatus((String) cbStatus.getValue());
			rotohdrDM.setCompanyid(companyid);
			rotohdrDM.setLastupdateddate(DateUtils.getcurrentdate());
			rotohdrDM.setLastupdatedby(username);
			serviceRotohdr.saveRotohdr(rotohdrDM);
			rotoid = rotohdrDM.getRotoid();
			@SuppressWarnings("unchecked")
			Collection<RotoDtlDM> rotoDtls = ((Collection<RotoDtlDM>) tblRotoDetails.getVisibleItemIds());
			for (RotoDtlDM rotoDtl : (Collection<RotoDtlDM>) rotoDtls) {
				rotoDtl.setRotoid(rotohdrDM.getRotoid());
				serviceRotoDtl.saveRotoDtl(rotoDtl);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTONO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTONO");
					}
				}
				catch (Exception e) {
				}
			}
			resetRotoDetails();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void save() {
		saverotoArmListDetails();
		@SuppressWarnings("unchecked")
		Collection<RotoArmDM> colPlanDtls = ((Collection<RotoArmDM>) tblRotoArm.getVisibleItemIds());
		for (RotoArmDM savecycle : (Collection<RotoArmDM>) colPlanDtls) {
			if ((savecycle.getCycleno()).equals(1L) && ((tblRotoArm.size() == 1))) {
				saveDetails();
			}
		}
	}
	
	private void saverotoArmListDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
	}
	
	/*
	 * loadClientList()-->this function is used for load the Client name
	 */
	/*
	 * Arm Employee List
	 */
	/*
	 * loadBranchList()-->this function is used for load the branch name
	 */
	private void loadBranchList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
			BeanContainer<Long, BranchDM> beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
			beanBranchDM.setBeanIdProperty("branchId");
			beanBranchDM.addAll(servicebeanBranch.getBranchList(null, null, null, "Active", companyid, "P"));
			cbBranch.setContainerDataSource(beanBranchDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadRotoPlanList() {
		try {
			BeanItemContainer<RotoPlanHdrDM> beanrotoplanhdr = new BeanItemContainer<RotoPlanHdrDM>(RotoPlanHdrDM.class);
			beanrotoplanhdr.addAll(serviceRotoplanhdr.getRotoPlanHdrDetails(null, companyid, null, "Active"));
			cbPlanRef.setContainerDataSource(beanrotoplanhdr);
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
			HashMap<String, Long> parameterMap = new HashMap<String, Long>();
			parameterMap.put("rotoid", rotoid);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/roto"); // pulverizer is the name of my jasper
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
}
