package com.gnts.sms.txn;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPNumberField;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.stt.txn.Roto;
import com.gnts.stt.mfg.domain.txn.RotoArmDM;
import com.gnts.stt.mfg.domain.txn.RotoDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanShiftDM;
import com.gnts.stt.mfg.domain.txn.RotoShiftDM;
import com.gnts.stt.mfg.domain.txn.RotohdrDM;
import com.gnts.stt.mfg.service.txn.RotoArmService;
import com.gnts.stt.mfg.service.txn.RotoPlanDtlService;
import com.gnts.stt.mfg.service.txn.RotoPlanShiftService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;

/*private GERPPopupDateField dfVisitDt;
 private GERPComboBox cbWO,cbPurposeVisit;
 private GERPTextField tfPjtName, tfClientName,tfPerName,tfPerPhone,tfTest,tfClentCity;
 private GERPNumberField tfNoPerson;
 private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
 BASEConstants.M_GENERIC_COLUMN);*/
public class CustomerVisit extends BaseTransUI {
	private RotoArmService serviceRotoArm = (RotoArmService) SpringContextHelper.getBean("rotoarm");
	private RotoPlanDtlService serviceRotoplandtl = (RotoPlanDtlService) SpringContextHelper.getBean("rotoplandtl");
	private RotoPlanShiftService serviceRotoplanshift = (RotoPlanShiftService) SpringContextHelper
			.getBean("rotoplanshift");
	// User Input Components for Work Order Details
	private BeanItemContainer<RotohdrDM> beanRotohdrDM = null;
	private BeanItemContainer<RotoDtlDM> beanrotodtldm = null;
	private BeanItemContainer<RotoShiftDM> beanRotoShiftDM = null;
	private BeanItemContainer<RotoArmDM> beanRotoArmDM = null;
	private Table tblRotoDetails, tblRotoShift, tblRotoArm;
	// Search Control Layout
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlSearchLayout, hlDtlandArm, hlHdrAndShift, hlArm, hlShift, hlHdrslap;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlShift, vlArm, vlDtl, vlHdrshiftandDtlarm;
	// Data Fields
	private GERPPopupDateField dfVisitDt, dfFormDt;
	private GERPComboBox cbWO, cbPurposeVisit;
	private GERPTextField tfPjtName, tfClientName, tfPerName, tfPerPhone, tfTest, tfClentCity;
	private GERPNumberField tnNoPerson;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4;
	private ComboBox cbDtlStatus;
	private FormLayout flDtlCol1, flDtlCol2, flDtlCol3;
	private ComboBox cbSftStatus;
	private FormLayout flshiftCol1, flshiftCol2, flshiftCol3;
	private List<RotoShiftDM> listRotoShift = new ArrayList<RotoShiftDM>();
	private ComboBox cbArmstatus;
	private FormLayout flArmCol1, flArmCol2, flArmCol3;
	private Button btnAddDtls = new GERPButton("Add", "Addbt", this);
	private Button btnAddShift = new GERPButton("Add", "Addbt", this);
	private Button btnAddArm = new GERPButton("Add", "Addbt", this);
	private Button btnArmDelete = new GERPButton("Delete", "delete", this);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private Button btnShiftdelete = new GERPButton("Delete", "delete", this);
	private Button btnAddMain = new GERPButton("Add", "Addbt", this);
	private Button btnDelMain = new GERPButton("Delete", "delete", this);
	private String username;
	private Long companyid, branchID, moduleId;
	private int recordCnt = 0;
	private int recordShiftCnt = 0;
	private int recordArmCnt = 0;
	private Boolean errorFlag = false;
	private Long rotoplanId;
	// Initialize logger
	private Logger logger = Logger.getLogger(Roto.class);
	private static final long serialVersionUID = 1L;
	
	public CustomerVisit() {
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
		btnAddShift.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateShiftDetails()) {
					saveRotoShiftDetails();
				}
			}
		});
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
					rotoDtlResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtls.setCaption("Update");
					btnAddDtls.setStyleName("savebt");
					editRotoDtls();
				}
			}
		});
		tblRotoShift = new Table();
		tblRotoShift.setSelectable(true);
		tblRotoShift.setWidth("912px");
		tblRotoShift.setPageLength(7);
		tblRotoShift.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblRotoShift.isSelected(event.getItemId())) {
					tblRotoShift.setImmediate(true);
					btnAddShift.setCaption("Add");
					btnAddShift.setStyleName("savebt");
					resetRotoShiftDetails();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddShift.setCaption("Update");
					btnAddShift.setStyleName("savebt");
					editRotoShiftDetails();
				}
			}
		});
		tblRotoArm = new GERPTable();
		tblRotoArm.setWidth("588px");
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
		btnShiftdelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnShiftdelete == event.getButton()) {
					deleteShiftDetails();
				}
				btnAddShift.setCaption("Add");
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
		// Main Customer Datas
		dfVisitDt = new GERPPopupDateField("Date of Visit");
		dfVisitDt.setRequired(true);
		dfVisitDt.setDateFormat("dd-MMM-yyyy");
		dfVisitDt.setInputPrompt("Select Date");
		dfVisitDt.setWidth("130px");
		dfFormDt = new GERPPopupDateField("Form Date");
		dfFormDt.setDateFormat("dd-MMM-yyyy");
		dfFormDt.setInputPrompt("Select Date");
		dfFormDt.setWidth("130px");
		cbWO = new GERPComboBox("Work Order");
		cbWO.setWidth("150");
		cbPurposeVisit = new GERPComboBox("Purpose");
		cbWO.setWidth("150");
		tfPjtName = new GERPTextField("Project Name");
		tfPjtName.setWidth("150");
		tfClientName = new GERPTextField("Cilent Name");
		tfClientName.setWidth("150");
		tfPerName = new GERPTextField("Person Name");
		tfPerName.setWidth("150");
		tfPerPhone = new GERPTextField("Contact");
		tfPerPhone.setWidth("150");
		tfTest = new GERPTextField("Test");
		tfTest.setWidth("150");
		tfClentCity = new GERPTextField("Client City");
		tfClentCity.setWidth("150");
		tnNoPerson = new GERPNumberField("No.of Person");
		tnNoPerson.setWidth("150");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting FormPlan UI");
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbDtlStatus.setWidth("150px");
		// Status ComboBox
		cbSftStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbArmstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbArmstatus.setWidth("150px");
		cbSftStatus.setWidth("150px");
		cbHdrStatus.setWidth("150px");
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadShiftRslt();
		loadArmRslt();
		loadPlanDtlRslt();
		btnAddDtls.setStyleName("add");
		btnAddShift.setStyleName("add");
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
		// flHdrCol4.addComponent(tfRotoRefno);
		// flHdrCol1.addComponent(dfRotoDate);
		flHdrCol2.addComponent(lbl);
		flHdrCol1.addComponent(cbWO);
		flHdrCol2.addComponent(dfVisitDt);
		flHdrCol3.addComponent(cbHdrStatus);
		hlSearchLayout.addComponent(flHdrCol4);
		hlSearchLayout.addComponent(flHdrCol1);
		hlSearchLayout.addComponent(flHdrCol2);
		hlSearchLayout.addComponent(flHdrCol3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Form planning search layout");
		// Remove all components in search layout
		/*
		 * vlArm.removeAllComponents(); vlDtl.removeAllComponents(); hlDtlandArm.removeAllComponents();
		 */
		hlSearchLayout.removeAllComponents();
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol1.addComponent(dfFormDt);
		flHdrCol1.addComponent(tfPjtName);
		flHdrCol1.addComponent(cbWO);
		flHdrCol1.addComponent(tfClientName);
		flHdrCol1.addComponent(tfClentCity);
		flHdrCol2.addComponent(dfVisitDt);
		flHdrCol2.addComponent(cbPurposeVisit);
		flHdrCol2.addComponent(tfTest);
		flHdrCol2.addComponent(cbHdrStatus);
		flHdrCol3.addComponent(btnAddMain);
		flHdrCol3.addComponent(btnDelMain);
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdrCol1);
		hlHdr.addComponent(flHdrCol2);
		hlHdr.addComponent(flHdrCol3);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding Arm Components
		flArmCol1 = new FormLayout();
		flArmCol2 = new FormLayout();
		flArmCol3 = new FormLayout();
		flArmCol1.addComponent(tnNoPerson);
		flArmCol1.addComponent(tfPerName);
		flArmCol2.addComponent(tfPerPhone);
		flArmCol2.addComponent(cbArmstatus);
		flArmCol3.addComponent(btnAddArm);
		flArmCol3.addComponent(btnArmDelete);
		hlArm = new HorizontalLayout();
		hlArm.setSpacing(true);
		hlArm.addComponent(flArmCol1);
		hlArm.addComponent(flArmCol2);
		hlArm.addComponent(flArmCol3);
		hlArm.setSpacing(true);
		hlArm.setMargin(true);
		// Adding Shift Components
		flshiftCol1 = new FormLayout();
		flshiftCol2 = new FormLayout();
		flshiftCol3 = new FormLayout();
		// flshiftCol1.addComponent(tfshiftname);
		flshiftCol2.addComponent(cbSftStatus);
		flshiftCol3.addComponent(btnAddShift);
		flshiftCol3.addComponent(btnShiftdelete);
		flshiftCol3.setComponentAlignment(btnAddShift, Alignment.BOTTOM_CENTER);
		vlShift = new VerticalLayout();
		vlShift.setSpacing(true);
		hlShift = new HorizontalLayout();
		hlShift.setSpacing(true);
		hlShift.addComponent(flshiftCol1);
		hlShift.addComponent(flshiftCol2);
		hlShift.addComponent(flshiftCol3);
		vlShift.addComponent(hlShift);
		vlShift.addComponent(tblRotoShift);
		// Adding Dtl Components
		flDtlCol1 = new FormLayout();
		flDtlCol2 = new FormLayout();
		flDtlCol3 = new FormLayout();
		/*
		 * flDtlCol1.addComponent(cbClient);
		 */
		flDtlCol3.addComponent(cbDtlStatus);
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
		hlDtlandArm = new HorizontalLayout();
		hlDtlandArm.addComponent(GERPPanelGenerator.createPanel(vlArm));
		hlDtlandArm.addComponent(GERPPanelGenerator.createPanel(vlShift));
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
		List<RotohdrDM> RotoList = new ArrayList<RotohdrDM>();
		recordCnt = RotoList.size();
		beanRotohdrDM = new BeanItemContainer<RotohdrDM>(RotohdrDM.class);
		beanRotohdrDM.addAll(RotoList);
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
	
	private void loadShiftRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		// hlHdrAndShift.setVisible(true);
		List<RotoPlanShiftDM> rotoplanshiftdm = new ArrayList<RotoPlanShiftDM>();
		rotoplanshiftdm = serviceRotoplanshift.getRotoPlanShiftList(null, rotoplanId, null, null);
		List<RotoShiftDM> rotoshiftdm = new ArrayList<RotoShiftDM>();
		for (RotoPlanShiftDM obj : rotoplanshiftdm) {
			RotoShiftDM list = new RotoShiftDM();
			list.setShiftname(obj.getShiftname());
			list.setEmployeeid(obj.getEmployeeid());
			list.setEmpName(obj.getEmpName());
			list.setTargetqty(obj.getTargetqty());
			list.setShiftstatus(obj.getShftstatus());
			list.setLastupdateddt(new Date());
			rotoshiftdm.add(list);
		}
		recordShiftCnt = listRotoShift.size();
		beanRotoShiftDM = new BeanItemContainer<RotoShiftDM>(RotoShiftDM.class);
		beanRotoShiftDM.addAll(rotoshiftdm);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Rotoplan. result set");
		tblRotoShift.setContainerDataSource(beanRotoShiftDM);
		tblRotoShift.setVisibleColumns(new Object[] { "shiftname", "empName", "targetqty", "achivedqty" });
		tblRotoShift.setColumnHeaders(new String[] { "Shift Name", "Employee Name", "Target Qty", "Achived Qty" });
		tblRotoShift.setColumnAlignment("rotosftid", Align.RIGHT);
		tblRotoShift.setColumnFooter("achivedqty", "No.of Records : " + recordShiftCnt);
		tblRotoShift.setFooterVisible(true);
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
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
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
		/*
		 * cbbranch.setRequired(true); dfRotoDate.setRequired(true); tfshiftname.setRequired(true);
		 * cbEmpname.setRequired(true); cbWorkorder.setRequired(true); cbProduct.setRequired(true);
		 * cbClient.setRequired(true); cbArmproduct.setRequired(true);
		 */
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtls.setCaption("Add");
		btnAddShift.setCaption("Add");
		btnAddArm.setCaption("Add");
		tblRotoDetails.setVisible(true);
		tblRotoShift.setVisible(true);
		loadArmRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		assembleInputUserLayout();
		resetFields();
		editRotoHdrDetails();
		editRotoDtls();
		editRotoShiftDetails();
		editArmDetails();
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		resetRotoDetails();
		resetRotoShiftDetails();
		hlCmdBtnLayout.setVisible(true);
		// tblDtl.removeAllItems();
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
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		listRotoShift = new ArrayList<RotoShiftDM>();
		tblRotoDetails.removeAllItems();
		tblRotoShift.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editRotoHdrDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			RotohdrDM editRotohdr = beanRotohdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			cbHdrStatus.setValue(editRotohdr.getRotostatus());
		}
		loadPlanDtlRslt();
		loadShiftRslt();
		loadArmRslt();
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee name
	 */
	private void editRotoDtls() {
		hlUserInputLayout.setVisible(true);
	}
	
	private void editRotoShiftDetails() {
		hlUserInputLayout.setVisible(true);
		if (tblRotoShift.getValue() != null) {
		}
	}
	
	private void editArmDetails() {
		hlUserInputLayout.setVisible(true);
	}
	
	private void resetRotoDetails() {
	}
	
	private void resetRotoShiftDetails() {
	}
	
	private void rotoArmResetFields() {
	}
	
	private void rotoDtlResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
	}
	
	private boolean validateShiftDetails() {
		return errorFlag;
	}
	
	private boolean validateArmDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		return isValid;
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
	}
	
	private void saveRotoShiftDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoShiftDM rotoShiftDM = new RotoShiftDM();
			if (tblRotoShift.getValue() != null) {
				rotoShiftDM = beanRotoShiftDM.getItem(tblRotoShift.getValue()).getBean();
				listRotoShift.remove(rotoShiftDM);
			}
			rotoShiftDM.setLastupdateddt(DateUtils.getcurrentdate());
			rotoShiftDM.setLastupdatedby(username);
			listRotoShift.add(rotoShiftDM);
			loadShiftRslt();
			btnAddShift.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		resetRotoShiftDetails();
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
	}
	
	private void deleteShiftDetails() {
		RotoShiftDM removeShift = new RotoShiftDM();
		if (tblRotoShift.getValue() != null) {
			removeShift = beanRotoShiftDM.getItem(tblRotoShift.getValue()).getBean();
			listRotoShift.remove(removeShift);
			resetRotoShiftDetails();
			loadShiftRslt();
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
