package com.gnts.mfg.stt.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.components.GERPTimeField;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.stt.mfg.domain.txn.RotoCheckHdrDM;
import com.gnts.stt.mfg.domain.txn.RotoDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanHdrDM;
import com.gnts.stt.mfg.service.txn.RotoCheckHdrService;
import com.gnts.stt.mfg.service.txn.RotoDtlService;
import com.gnts.stt.mfg.service.txn.RotoPlanDtlService;
import com.gnts.stt.mfg.service.txn.RotoPlanHdrService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;

public class RotoCheck extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private RotoCheckHdrService serviceRotoCheckhdr = (RotoCheckHdrService) SpringContextHelper.getBean("rotocheckhdr");
	private RotoDtlService serviceRotoDtl = (RotoDtlService) SpringContextHelper.getBean("rotodtl");
	private BranchService servicebeanBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private RotoPlanHdrService serviceRotoplanhdr = (RotoPlanHdrService) SpringContextHelper.getBean("rotoplanhdr");
	private RotoPlanDtlService serviceRotoplandtl = (RotoPlanDtlService) SpringContextHelper.getBean("rotoplandtl");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private BeanItemContainer<RotoCheckHdrDM> beanRotoCheckHdrDM = null;
	private BeanItemContainer<RotoDtlDM> beanrotodtldm = null;
	private Button btnAddDtls = new GERPButton("Add", "Addbt", this);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private FormLayout fldtl1, fldtl2, fldtl3, fldtl4, fldtl5;
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4;
	private VerticalLayout vlDtl, vlHdrshiftandDtlarm;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlHdrAndShift;
	private Table tblRotoDetails;
	// Initialize the logger
	private Logger logger = Logger.getLogger(RotoCheck.class);
	// User Input Fields for EC Request
	// Roto Header
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
	// private BeanItemContainer<RotohdrDM> beanRotohdr = null;
	// Search Control LayouttaRemarksa
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hlDtlandArm, hlArm, hlHdrslap;
	private HorizontalLayout hlSearchLayout = new HorizontalLayout();
	private VerticalLayout vlArm;
	// local variables declaration
	private String username;
	private Long companyid, branchID, moduleId;
	private int recordCnt = 0;
	private Long rotoplanId;
	private Long productid, id;
	
	// Constructor received the parameters from Login UI class
	public RotoCheck() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside VisitorPass() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting VisitorPass UI");
		hlsearchlayout = new GERPAddEditHLayout();
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
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setRequired(true);
		cbBranch.setWidth("150");
		loadBranchList();
		dfRotoDt = new GERPPopupDateField("Date");
		dfRotoDt.setRequired(true);
		dfRotoDt.setDateFormat("dd-MMM-yyyy");
		dfRotoDt.setInputPrompt("Select Date");
		dfRotoDt.setWidth("130px");
		cbStatus.setWidth("150");
		buildviewDtl();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		assembleSearchLayout();
		assembleinputLayout();
		resetFields();
		loadPlanDtlRslt();
	}
	
	// Roto Check Details
	private void buildviewDtl() {
		cbArmNo = new GERPComboBox("Arm No.");
		cbArmNo.addItem("1");
		cbArmNo.addItem("2");
		cbArmNo.addItem("3");
		cbArmNo.addItem("4");
		cbArmNo.setRequired(true);
		cbArmNo.setWidth("150");
		tmOvenOn = new GERPTimeField("Oven On");
		tmOvenOn.setWidth("150");
		tmOvenOff = new GERPTimeField("Oven Off");
		tmOvenOff.setWidth("150");
		tfOvenTotal = new GERPTextField("Oven Total");
		tfOvenTotal.setWidth("150");
		tmCharOn = new GERPTimeField("Charge On");
		tmCharOn.setWidth("150");
		tmCharOff = new GERPTimeField("Change Off");
		tmCharOff.setWidth("150");
		tfCharTot = new GERPTextField("Charge Total");
		tfCharTot.setWidth("150");
		tmCoolOn = new GERPTimeField("Cooling On");
		tmCoolOn.setWidth("150");
		tmCoolOff = new GERPTimeField("Cooling Off");
		tmCoolOff.setWidth("150");
		tfCoolTot = new GERPTextField("Cooling Total");
		tfCoolTot.setWidth("150");
		tftempZ1 = new GERPTextField("Z1");
		tftempZ1.setWidth("150");
		tftempZ2 = new GERPTextField("Z2");
		tftempZ2.setWidth("150");
		tftempZ3 = new GERPTextField("Z3");
		tftempZ3.setWidth("150");
		tfBoxModel = new GERPTextField("Box Model");
		tfBoxModel.setWidth("150");
		tfPwdTop = new GERPTextField("Powder Top");
		tfPwdTop.setWidth("150");
		tfPwdBot = new GERPTextField("Powder Bottom");
		tfPwdBot.setWidth("150");
		tfPowTotal = new GERPTextField("Powder Total");
		tfPowTotal.setWidth("150");
		tfBoxWgTop = new GERPTextField("Box.Wg Top");
		tfBoxWgTop.setWidth("150");
		tfBoxWgBot = new GERPTextField("Box.Wg Bottom");
		tfBoxWgBot.setWidth("150");
		tfBoxWgTotal = new GERPTextField("Box.Wg Total");
		tfBoxWgTotal.setWidth("150");
		tfCycles = new GERPTextField("Cycles");
		tfCycles.setWidth("150");
		tfKgCm = new GERPTextField("Kg/Cm3");
		tfKgCm.setWidth("150");
		tfEmpNo = new GERPTextField("No.of Emp.");
		tfEmpNo.setWidth("150");
		tfRemarksDtl = new GERPTextArea("Remarks");
		tfRemarksDtl.setWidth("150");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Roto planning search layout");
		tfRotoRef.setReadOnly(false);
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
	
	private void assembleinputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Form planning search layout");
		// Remove all components in search layout
		/*
		 * vlArm.removeAllComponents(); vlDtl.removeAllComponents(); hlDtlandArm.removeAllComponents();
		 */
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
		fldtl1 = new FormLayout();
		fldtl2 = new FormLayout();
		fldtl3 = new FormLayout();
		fldtl4 = new FormLayout();
		fldtl5 = new FormLayout();
		HorizontalLayout hlBtn = new HorizontalLayout();
		hlBtn.addComponent(btnAddDtls);
		hlBtn.addComponent(btndelete);
		fldtl1.addComponent(cbArmNo);
		fldtl1.addComponent(tmOvenOn);
		fldtl1.addComponent(cbArmNo);
		fldtl1.addComponent(tmOvenOn);
		fldtl1.addComponent(tmOvenOff);
		fldtl1.addComponent(tfOvenTotal);
		fldtl1.addComponent(tmCharOn);
		fldtl2.addComponent(tmCharOff);
		fldtl2.addComponent(tfCharTot);
		fldtl2.addComponent(tmCoolOn);
		fldtl2.addComponent(tmCoolOff);
		fldtl2.addComponent(tfCoolTot);
		fldtl3.addComponent(tftempZ1);
		fldtl3.addComponent(tftempZ2);
		fldtl3.addComponent(tftempZ3);
		fldtl3.addComponent(tfBoxModel);
		fldtl3.addComponent(tfPwdTop);
		fldtl4.addComponent(tfPwdBot);
		fldtl4.addComponent(tfPowTotal);
		fldtl4.addComponent(tfBoxWgTop);
		fldtl4.addComponent(tfBoxWgBot);
		fldtl4.addComponent(tfBoxWgTotal);
		fldtl5.addComponent(tfCycles);
		fldtl5.addComponent(tfKgCm);
		fldtl5.addComponent(tfEmpNo);
		fldtl5.addComponent(tfRemarksDtl);
		fldtl5.addComponent(hlBtn);
		hlArm = new HorizontalLayout();
		hlArm.setSpacing(true);
		hlArm.addComponent(fldtl1);
		hlArm.addComponent(fldtl2);
		hlArm.addComponent(fldtl3);
		hlArm.setSpacing(true);
		hlArm.setMargin(true);
		hlHdrslap = new HorizontalLayout();
		hlHdrslap.addComponent(fldtl1);
		hlHdrslap.addComponent(fldtl2);
		hlHdrslap.addComponent(fldtl3);
		hlHdrslap.addComponent(fldtl4);
		hlHdrslap.addComponent(fldtl5);
		hlHdrslap.setSpacing(true);
		hlHdrslap.setMargin(true);
		vlArm = new VerticalLayout();
		vlArm.addComponent(hlArm);
		vlDtl = new VerticalLayout();
		vlDtl.addComponent(tblRotoDetails);
		hlDtlandArm = new HorizontalLayout();
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
	
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoCheckHdrDM rotocheckhdrDM = new RotoCheckHdrDM();
			// RotoCheck Header.
			rotocheckhdrDM.setRotorefno(tfRotoRef.getValue());
			rotocheckhdrDM.setBranchid((Long.valueOf(cbBranch.getValue().toString())));
			rotocheckhdrDM.setRotodate(dfRotoDt.getValue());
			rotocheckhdrDM.setProdtntotqty(Long.valueOf(tfProdQty.getValue()));
			rotocheckhdrDM.setRemarks(tfRemarks.getValue());
			rotocheckhdrDM.setCompanyid(companyid);
			rotocheckhdrDM.setLastupdateddate(DateUtils.getcurrentdate());
			rotocheckhdrDM.setLastupdatedby(username);
			rotocheckhdrDM.setRotostatus((String) cbStatus.getValue());
			serviceRotoCheckhdr.saveRotoCheckHdr(rotocheckhdrDM);
			for (RotoDtlDM rotoDtl : (Collection<RotoDtlDM>) ((Collection<RotoDtlDM>) tblRotoDetails
					.getVisibleItemIds())) {
				rotoDtl.setRotoid(rotocheckhdrDM.getRotoid());
				id = rotocheckhdrDM.getRotoid();
				productid = rotoDtl.getProductid();
				serviceRotoDtl.saveRotoDtl(rotoDtl);
			}
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		// RotoCheck Header.
		cbPlanRef.setValue(null);
		cbPlanRef.setComponentError(null);
		cbBranch.setValue(cbBranch.getItemIds().iterator().next());
		tfRotoRef.setReadOnly(false);
		tfRotoRef.setValue("");
		tfRotoRef.setComponentError(null);
		dfRotoDt.setValue(null);
		tfRemarks.setValue("");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
	}
	
	/*
	 * loadBranchList()-->this function is used for load the branch name
	 */
	private void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, BranchDM> beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranchDM.setBeanIdProperty("branchId");
		beanBranchDM.addAll(servicebeanBranch.getBranchList(null, null, null, "Active", companyid, "P"));
		cbBranch.setContainerDataSource(beanBranchDM);
	}
	
	private void loadRotoPlanList() {
		List<RotoPlanHdrDM> PlanList = serviceRotoplanhdr.getRotoPlanHdrDetails(null, companyid, null, "Active");
		BeanItemContainer<RotoPlanHdrDM> beanrotoplanhdr = new BeanItemContainer<RotoPlanHdrDM>(RotoPlanHdrDM.class);
		beanrotoplanhdr.addAll(PlanList);
		cbPlanRef.setContainerDataSource(beanrotoplanhdr);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		tblMstScrSrchRslt.setPageLength(14);
		List<RotoCheckHdrDM> RotoCheckList = new ArrayList<RotoCheckHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfRotoRef.getValue() + ", " + cbStatus.getValue());
		RotoCheckList = serviceRotoCheckhdr.getRotoCheckHdrDetatils(null, (String) tfRotoRef.getValue(), companyid,
				dfRotoDt.getValue(), cbStatus.getValue().toString(), "F");
		recordCnt = RotoCheckList.size();
		beanRotoCheckHdrDM = new BeanItemContainer<RotoCheckHdrDM>(RotoCheckHdrDM.class);
		beanRotoCheckHdrDM.addAll(RotoCheckList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Roto. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanRotoCheckHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "rotoid", "rotorefno", "rotodate", "rotostatus",
				"lastupdateddate", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Roto Ref No", "Roto Date", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("rotoid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		vlSrchRsltContainer.setVisible(true);
		resetFields();
		assembleinputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
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
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		// tblMstScrSrchRslt.setVisible(false);
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		// reset the input controls to default value comment
		assembleinputLayout();
		loadPlanDtlRslt();
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
	
	@Override
	protected void validateDetails() throws ValidationException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void showAuditDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void cancelDetails() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}