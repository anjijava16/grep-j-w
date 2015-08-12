package com.gnts.sms.txn;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPNumberField;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
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
import com.gnts.sms.domain.txn.CustomerVisitHdrDM;
import com.gnts.sms.domain.txn.CustomerVisitNoDtlDM;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.service.txn.CustomerVisitHdrService;
import com.gnts.sms.service.txn.CustomerVisitInfoDtlService;
import com.gnts.sms.service.txn.CustomerVisitNoDtlService;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.stt.mfg.domain.txn.RotoArmDM;
import com.gnts.stt.mfg.domain.txn.RotoDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanShiftDM;
import com.gnts.stt.mfg.domain.txn.RotoShiftDM;
import com.gnts.stt.mfg.domain.txn.RotohdrDM;
import com.gnts.stt.mfg.service.txn.RotoArmService;
import com.gnts.stt.mfg.service.txn.RotoPlanDtlService;
import com.gnts.stt.mfg.service.txn.RotoPlanShiftService;
import com.vaadin.data.Container.Viewer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;

public class CustomerVisit extends BaseTransUI {
	private RotoPlanDtlService serviceRotoplandtl = (RotoPlanDtlService) SpringContextHelper.getBean("rotoplandtl");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private RotoPlanShiftService serviceRotoplanshift = (RotoPlanShiftService) SpringContextHelper
			.getBean("rotoplanshift");
	private CustomerVisitHdrService CustomerVisitHdr = (CustomerVisitHdrService) SpringContextHelper
			.getBean("customervisithdr");
	private CustomerVisitNoDtlService serviceCustomerVisitNoDtl = (CustomerVisitNoDtlService) SpringContextHelper
			.getBean("customervisitnodtl");
	private CustomerVisitInfoDtlService serviceCustomerVisitInfoDtl = (CustomerVisitInfoDtlService) SpringContextHelper
			.getBean("customervisitinfodtl");
	private SmsEnqHdrService serviceEnquiryHdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	// User Input Components for Work Order Details
	private BeanItemContainer<CustomerVisitHdrDM> beanCustomerVisitHdrDM = null;
	private BeanItemContainer<CustomerVisitNoDtlDM> beanCustomerVisitNoDtlDM = null;
	private BeanItemContainer<RotohdrDM> beanRotohdrDM = null;
	private BeanItemContainer<RotoDtlDM> beanrotodtldm = null;
	private BeanItemContainer<RotoShiftDM> beanRotoShiftDM = null;
	private Table tblCusVisHdr, tblPersonNo, tblInfoPass;
	// Search Control Layout
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlSearchLayout, hlDtlandArm, hlHdrAndShift, hlArm, hlShift, hlHdrslap;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlShift, vlArm, vlDtl, vlHdrshiftandDtlarm;
	// Data Fields
	private GERPPopupDateField dfVisitDt, dfFormDt;
	private GERPComboBox cbWO, cbPurposeVisit, cbEnqNo;
	private GERPTextField tfPjtName, tfClientName, tfPerName, tfPerPhone, tfClentCity;
	private GERPNumberField tnNoPerson;
	private GERPTextArea taPurposeRe;
	private CheckBox checkBx1, checkBx2, checkBx3, checkBx4, checkBx5, checkBx6, checkBx7;
	private GERPComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4;
	private ComboBox cbDtlStatus;
	private FormLayout flDtlCol1, flDtlCol2, flDtlCol3;
	private ComboBox cbSftStatus;
	private FormLayout flshiftCol1, flshiftCol2, flshiftCol3, flshiftCol4;
	private List<RotoShiftDM> listRotoShift = new ArrayList<RotoShiftDM>();
	private ComboBox cbArmstatus;
	private FormLayout flArmCol1, flArmCol2, flArmCol3;
	private Button btnAddDtls = new GERPButton("Add", "add", this);
	private Button btnAddPerson = new GERPButton("Add", "add", this);
	private Button btnDeletePerson = new GERPButton("Delete", "delete", this);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private Button btnAddInfo = new GERPButton("Add", "add", this);
	private Button btnDeleteInfo = new GERPButton("Delete", "delete", this);
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
		btnAddInfo.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateShiftDetails()) {
					saveRotoShiftDetails();
				}
			}
		});
		btnAddPerson.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateArmDetails()) {
					save();
				}
			}
		});
		tblCusVisHdr = new Table();
		tblCusVisHdr.setSelectable(true);
		tblCusVisHdr.setWidth("588px");
		tblCusVisHdr.setPageLength(5);
		tblCusVisHdr.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblCusVisHdr.isSelected(event.getItemId())) {
					tblCusVisHdr.setImmediate(true);
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
		tblPersonNo = new Table();
		tblPersonNo.setSelectable(true);
		tblPersonNo.setWidth("912px");
		tblPersonNo.setPageLength(7);
		tblPersonNo.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblPersonNo.isSelected(event.getItemId())) {
					tblPersonNo.setImmediate(true);
					btnAddInfo.setCaption("Add");
					btnAddInfo.setStyleName("savebt");
					resetRotoShiftDetails();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddInfo.setCaption("Update");
					btnAddInfo.setStyleName("savebt");
					editRotoShiftDetails();
				}
			}
		});
		tblInfoPass = new GERPTable();
		tblInfoPass.setWidth("588px");
		tblInfoPass.setPageLength(5);
		tblInfoPass.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblInfoPass.isSelected(event.getItemId())) {
					tblInfoPass.setImmediate(true);
					btnAddPerson.setCaption("Add");
					btnAddPerson.setStyleName("savebt");
					rotoArmResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddPerson.setCaption("Update");
					btnAddPerson.setStyleName("savebt");
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
		btnDeleteInfo.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnDeleteInfo == event.getButton()) {
					deleteShiftDetails();
				}
				btnAddInfo.setCaption("Add");
			}
		});
		btnDeletePerson.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnDeletePerson == event.getButton()) {
					// deleteArmDetails();
					btnAddPerson.setCaption("Add");
				}
			}
		});
		// Main Customer Datas
		dfVisitDt = new GERPPopupDateField("Visit Date");
		dfVisitDt.setRequired(true);
		dfVisitDt.setDateFormat("dd-MMM-yyyy");
		dfVisitDt.setInputPrompt("Select Date");
		dfVisitDt.setWidth("130px");
		dfFormDt = new GERPPopupDateField("Date");
		dfFormDt.setDateFormat("dd-MMM-yyyy");
		dfFormDt.setInputPrompt("Select Date");
		dfFormDt.setWidth("130px");
		cbWO = new GERPComboBox("Work Order");
		cbWO.setWidth("150");
		cbPurposeVisit = new GERPComboBox("Purpose");
		cbWO.setWidth("150");
		cbEnqNo = new GERPComboBox("Enquiry No.");
		cbEnqNo.setWidth("150");
		cbEnqNo.setItemCaptionPropertyId("enquiryNo");
		loadEnquiryNo();
		cbEnqNo.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				if (item != null) {
					loadClientDetails();
				}
			}
		});
		tfPjtName = new GERPTextField("Project Name");
		tfPjtName.setWidth("150");
		tfClientName = new GERPTextField("Cilent Name");
		tfClientName.setWidth("150");
		tfPerName = new GERPTextField("Person Name");
		tfPerName.setWidth("150");
		tfPerPhone = new GERPTextField("Contact");
		tfPerPhone.setWidth("150");
		taPurposeRe = new GERPTextArea("Remarks");
		taPurposeRe.setWidth("150");
		taPurposeRe.setHeight("50");
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
		cbSftStatus.setWidth("100px");
		cbHdrStatus.setWidth("150px");
		// Check Box Department.
		checkBx1 = new CheckBox("HOD-Prod.");
		checkBx2 = new CheckBox("Planing");
		checkBx3 = new CheckBox("Production");
		checkBx4 = new CheckBox("QC");
		checkBx5 = new CheckBox("Maintenance");
		checkBx6 = new CheckBox("HR");
		checkBx7 = new CheckBox("Die");
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadShiftRslt();
		loadArmRslt();
		loadPlanDtlRslt();
		btnAddDtls.setStyleName("add");
		btnAddInfo.setStyleName("add");
		btnAddPerson.setStyleName("add");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Roto planning search layout");
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		flHdrCol1.addComponent(cbEnqNo);
		flHdrCol2.addComponent(cbWO);
		flHdrCol3.addComponent(dfVisitDt);
		flHdrCol4.addComponent(cbHdrStatus);
		hlSearchLayout.addComponent(flHdrCol1);
		hlSearchLayout.addComponent(flHdrCol2);
		hlSearchLayout.addComponent(flHdrCol3);
		hlSearchLayout.addComponent(flHdrCol4);
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
		flHdrCol1.addComponent(cbEnqNo);
		flHdrCol1.addComponent(cbWO);
		flHdrCol1.addComponent(tfClientName);
		flHdrCol1.addComponent(tfClentCity);
		flHdrCol2.addComponent(dfVisitDt);
		flHdrCol2.addComponent(cbPurposeVisit);
		flHdrCol2.addComponent(taPurposeRe);
		flHdrCol2.addComponent(tnNoPerson);
		flHdrCol2.addComponent(cbHdrStatus);
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
		flArmCol1.addComponent(tfPerName);
		flArmCol1.addComponent(tfPerPhone);
		flArmCol2.addComponent(cbArmstatus);
		flArmCol3.addComponent(btnAddPerson);
		flArmCol3.addComponent(btnDeletePerson);
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
		flshiftCol4 = new FormLayout();
		// flshiftCol1.addComponent(tfshiftname);
		flshiftCol1.addComponent(checkBx1);
		flshiftCol1.addComponent(checkBx2);
		flshiftCol1.addComponent(checkBx3);
		flshiftCol2.addComponent(checkBx4);
		flshiftCol2.addComponent(checkBx7);
		flshiftCol2.addComponent(checkBx6);
		flshiftCol3.addComponent(checkBx5);
		flshiftCol4.addComponent(cbSftStatus);
		flshiftCol4.addComponent(btnAddInfo);
		flshiftCol4.addComponent(btnDeleteInfo);
		flshiftCol4.setComponentAlignment(btnAddInfo, Alignment.BOTTOM_CENTER);
		vlShift = new VerticalLayout();
		vlShift.setSpacing(true);
		hlShift = new HorizontalLayout();
		hlShift.setSpacing(true);
		hlShift.addComponent(flshiftCol1);
		hlShift.addComponent(flshiftCol2);
		hlShift.addComponent(flshiftCol3);
		hlShift.addComponent(flshiftCol4);
		vlShift.addComponent(hlShift);
		vlShift.addComponent(tblPersonNo);
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
		vlArm.addComponent(tblInfoPass);
		vlDtl = new VerticalLayout();
		vlDtl.addComponent(tblCusVisHdr);
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
		List<CustomerVisitHdrDM> customervisitHdrList = new ArrayList<CustomerVisitHdrDM>();
		recordCnt = customervisitHdrList.size();
		beanCustomerVisitHdrDM = new BeanItemContainer<CustomerVisitHdrDM>(CustomerVisitHdrDM.class);
		beanCustomerVisitHdrDM.addAll(customervisitHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Customer Visit result set");
		tblMstScrSrchRslt.setContainerDataSource(beanCustomerVisitHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "cusVisId", "enquiryNo", "visitDt", "custName", "custCity",
				"custHdrStatus", "lastUpdateddt", "lastUpdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Visit.Id", "Enquiry No", "Visit Date", "Customer", "City",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("cusVisId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
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
			tblCusVisHdr.setContainerDataSource(beanrotodtldm);
			tblCusVisHdr
					.setVisibleColumns(new Object[] { "clientName", "woNo", "prodName", "plannedqty", "prodtnqty" });
			tblCusVisHdr.setColumnHeaders(new String[] { "Client Name", "WO No.", "Product Name", "Planned Qty.",
					"Product Qty" });
		}
	}
	
	private void loadShiftRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
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
		tblPersonNo.setContainerDataSource(beanRotoShiftDM);
		tblPersonNo.setVisibleColumns(new Object[] { "shiftname", "empName", "targetqty", "achivedqty" });
		tblPersonNo.setColumnHeaders(new String[] { "Shift Name", "Employee Name", "Target Qty", "Achived Qty" });
		tblPersonNo.setColumnAlignment("rotosftid", Align.RIGHT);
		tblPersonNo.setColumnFooter("achivedqty", "No.of Records : " + recordShiftCnt);
		tblPersonNo.setFooterVisible(true);
	}
	
	private void loadArmRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<CustomerVisitNoDtlDM> customervisitnodtlList = new ArrayList<CustomerVisitNoDtlDM>();
		recordArmCnt = customervisitnodtlList.size();
		customervisitnodtlList = serviceCustomerVisitNoDtl.getCustomerVisitNoDtlList(null, null, null, null);
		beanCustomerVisitNoDtlDM = new BeanItemContainer<CustomerVisitNoDtlDM>(CustomerVisitNoDtlDM.class);
		beanCustomerVisitNoDtlDM.addAll(customervisitnodtlList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the No. of person List");
		tblInfoPass.setContainerDataSource(beanCustomerVisitNoDtlDM);
		tblInfoPass.setVisibleColumns(new Object[] { "personName", "contactNo" });
		tblInfoPass.setColumnHeaders(new String[] { "Person Name", "Contact No." });
		tblInfoPass.setColumnFooter("contactNo", "No.of Records : " + recordArmCnt);
	}
	
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
		hlUserInputLayout.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		resetFields();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtls.setCaption("Add");
		btnAddInfo.setCaption("Add");
		btnAddPerson.setCaption("Add");
		tblCusVisHdr.setVisible(true);
		tblPersonNo.setVisible(true);
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
		tblCusVisHdr.removeAllItems();
		tblPersonNo.removeAllItems();
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
		if (tblPersonNo.getValue() != null) {
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
			if (tblPersonNo.getValue() != null) {
				rotoShiftDM = beanRotoShiftDM.getItem(tblPersonNo.getValue()).getBean();
				listRotoShift.remove(rotoShiftDM);
			}
			rotoShiftDM.setLastupdateddt(DateUtils.getcurrentdate());
			rotoShiftDM.setLastupdatedby(username);
			listRotoShift.add(rotoShiftDM);
			loadShiftRslt();
			btnAddInfo.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		resetRotoShiftDetails();
	}
	
	private void save() {
		saverotoArmListDetails();
		@SuppressWarnings("unchecked")
		Collection<RotoArmDM> colPlanDtls = ((Collection<RotoArmDM>) tblInfoPass.getVisibleItemIds());
		for (RotoArmDM savecycle : (Collection<RotoArmDM>) colPlanDtls) {
			if ((savecycle.getCycleno()).equals(1L) && ((tblInfoPass.size() == 1))) {
				saveDetails();
			}
		}
	}
	
	private void saverotoArmListDetails() {
	}
	
	private void deleteShiftDetails() {
		RotoShiftDM removeShift = new RotoShiftDM();
		if (tblPersonNo.getValue() != null) {
			removeShift = beanRotoShiftDM.getItem(tblPersonNo.getValue()).getBean();
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
	
	/*
	 * Load Meathods
	 */
	// Load Enquiry Number
	private void loadEnquiryNo() {
		BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("enquiryId");
		beansmsenqHdr.addAll(serviceEnquiryHdr.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
		cbEnqNo.setContainerDataSource(beansmsenqHdr);
	}
	
	// Load Client Details
	private void loadClientDetails() {
		try {
			Long clientid = serviceEnquiryHdr
					.getSmsEnqHdrList(null, Long.valueOf(cbEnqNo.getValue().toString()), null, null, null, "F", null,
							null).get(0).getClientId();
			tfClientName.setValue(serviceClients
					.getClientDetails(companyid, clientid, null, null, null, null, null, null, "Active", "F").get(0)
					.getClientfullname().toString());
			tfClentCity.setValue(serviceClients
					.getClientDetails(companyid, clientid, null, null, null, null, null, null, "Active", "F").get(0).getCityName().toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load Clients Details " + e);
		}
	}
}
