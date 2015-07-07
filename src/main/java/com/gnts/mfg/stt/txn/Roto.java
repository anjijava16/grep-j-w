package com.gnts.mfg.stt.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.txn.WorkOrderDtlDM;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.service.txn.WorkOrderDtlService;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.stt.mfg.domain.txn.RotoArmDM;
import com.gnts.stt.mfg.domain.txn.RotoDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanArmDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanDtlDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanHdrDM;
import com.gnts.stt.mfg.domain.txn.RotoPlanShiftDM;
import com.gnts.stt.mfg.domain.txn.RotoShiftDM;
import com.gnts.stt.mfg.domain.txn.RotohdrDM;
import com.gnts.stt.mfg.service.txn.RotoArmService;
import com.gnts.stt.mfg.service.txn.RotoDtlService;
import com.gnts.stt.mfg.service.txn.RotoPlanArmService;
import com.gnts.stt.mfg.service.txn.RotoPlanDtlService;
import com.gnts.stt.mfg.service.txn.RotoPlanHdrService;
import com.gnts.stt.mfg.service.txn.RotoPlanShiftService;
import com.gnts.stt.mfg.service.txn.RotoShiftService;
import com.gnts.stt.mfg.service.txn.RotohdrService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Roto extends BaseTransUI {
	private RotohdrService serviceRotohdr = (RotohdrService) SpringContextHelper.getBean("rotohdr");
	private RotoDtlService serviceRotoDtl = (RotoDtlService) SpringContextHelper.getBean("rotodtl");
	private RotoArmService serviceRotoArm = (RotoArmService) SpringContextHelper.getBean("rotoarm");
	private RotoPlanDtlService serviceRotoplandtl = (RotoPlanDtlService) SpringContextHelper.getBean("rotoplandtl");
	private RotoPlanShiftService serviceRotoplanshift = (RotoPlanShiftService) SpringContextHelper
			.getBean("rotoplanshift");
	private RotoPlanArmService serviceRotoplanarm = (RotoPlanArmService) SpringContextHelper.getBean("rotoplanarm");
	private RotoShiftService serviceRotoShift = (RotoShiftService) SpringContextHelper.getBean("rotoshift");
	private ClientService serviceClient = (ClientService) SpringContextHelper.getBean("clients");
	private BranchService servicebeanBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private WorkOrderHdrService serviceWorkOrderHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private WorkOrderDtlService serviceWorkOrderDtl = (WorkOrderDtlService) SpringContextHelper.getBean("workOrderDtl");
	private RotoPlanHdrService serviceRotoplanhdr = (RotoPlanHdrService) SpringContextHelper.getBean("rotoplanhdr");
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
	// Roto Hdr Components
	private TextField tfRotoRefno, tfRotoHdrqty, tfrotoplanedqty, tfPlnRef;
	private ComboBox cbbranch, cbRotoPlan;
	private DateField dfRotoDate;
	private TextArea taHdrRemarks;
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4;
	// Roto Dtl Components
	private TextField tfRotoDtlqty;
	private ComboBox cbWorkorder, cbProduct, cbClient, cbDtlStatus, cbArmWorkorder;
	private FormLayout flDtlCol1, flDtlCol2, flDtlCol3;
	// Roto Shift Components
	private TextField tfshiftname, tfTargetQty;
	private ComboBox cbEmpname, cbSftStatus;
	private FormLayout flshiftCol1, flshiftCol2, flshiftCol3;
	private List<RotoShiftDM> listRotoShift = new ArrayList<RotoShiftDM>();
	// Roto Arm Components
	private TextField tfArmno, tfCycleNo, tfCycleCount, tfArmShiftName;
	private ComboBox cbArmproduct, cbArmstatus, cbArmEmployee;
	private FormLayout flArmCol1, flArmCol2, flArmCol3;
	private Button btnAddDtls = new GERPButton("Add", "Addbt", this);
	private Button btnAddShift = new GERPButton("Add", "Addbt", this);
	private Button btnAddArm = new GERPButton("Add", "Addbt", this);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private Button btnShiftdelete = new GERPButton("Delete", "delete", this);
	private Button btnArmDelete = new GERPButton("Delete", "delete", this);
	private String username;
	private Long companyid, branchID, moduleId;
	private Long rotodtlid;
	private Long rotoid;
	private Long productid;
	private Long employeeid;
	private int recordCnt = 0;
	private int recordShiftCnt = 0;
	private int recordArmCnt = 0;
	private Boolean errorFlag = false;
	private Long rotoplanId;
	private int count = 1;
	private Long qty = 0L;
	// Initialize logger
	private Logger logger = Logger.getLogger(Roto.class);
	private Long id;
	private static final long serialVersionUID = 1L;
	
	public Roto() {
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
		tblRotoDetails.setPageLength(3);
		tfPlnRef = new TextField("Plan Ref.No");
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
				tfRotoRefno.setReadOnly(false);
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting FormPlan UI");
		// Roto Hdr
		// plan Ref.No text field
		tfRotoRefno = new GERPTextField("Roto Ref.No");
		// Branch Hdr Combo Box
		cbbranch = new GERPComboBox("Branch Name");
		cbbranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		// Roto Hdr Datefield
		dfRotoDate = new PopupDateField("Roto Date");
		dfRotoDate.setDateFormat("dd-MMM-yyyy");
		dfRotoDate.setWidth("130px");
		// Roto Hdr Qty.Text field
		tfRotoHdrqty = new GERPTextField("production Quantity");
		// tfRotoHdrqty.setValue("0");
		tfrotoplanedqty = new GERPTextField("Planned Quantity");
		tfrotoplanedqty.setValue("0");
		cbRotoPlan = new GERPComboBox("Plan Ref.No");
		cbRotoPlan.setItemCaptionPropertyId("rotoplanrefno");
		loadRotoPlanList();
		cbRotoPlan.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbRotoPlan.getItem(itemId);
				if (item != null) {
					dfRotoDate.setValue(((RotoPlanHdrDM) cbRotoPlan.getValue()).getRotoplandt1());
					rotoplanId = (((RotoPlanHdrDM) cbRotoPlan.getValue()).getRotoplanidLong());
					System.out.println("rotoplanId--->" + rotoplanId);
					tfrotoplanedqty.setValue(((RotoPlanHdrDM) cbRotoPlan.getValue()).getPlannedqty().toString());
					loadPlanDtlRslt();
					loadShiftRslt();
				}
			}
		});
		// Roto Remarks TextArea
		taHdrRemarks = new GERPTextArea("Remarks");
		taHdrRemarks.setHeight("75px");
		taHdrRemarks.setWidth("150px");
		// Status ComboBox
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbDtlStatus.setWidth("100px");
		// Status ComboBox
		cbSftStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Roto Shift
		// Shift Name TextField
		tfshiftname = new GERPTextField("Shift Name");
		// Employee Name combobox
		cbEmpname = new GERPComboBox("Employee");
		cbEmpname.setItemCaptionPropertyId("fullname");
		loadEmployeeList();
		// TargetQty TextField
		tfTargetQty = new GERPTextField("Target Qty");
		tfTargetQty.setWidth("110");
		tfTargetQty.setValue("0");
		// Roto Dtl
		// Client Id ComboBox
		cbClient = new GERPComboBox("Client Name");
		cbClient.setItemCaptionPropertyId("clientName");
		cbClient.setWidth("100px");
		loadClientList();
		cbClient.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbClient.getItem(itemId);
				if (item != null) {
					loadWorkOrderNo();
				}
			}
		});
		// WOId ComboBox
		cbWorkorder = new GERPComboBox("WO No.");
		cbWorkorder.setItemCaptionPropertyId("workOrdrNo");
		cbWorkorder.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbWorkorder.getItem(itemId);
				if (item != null) {
					loadProductList();
				}
			}
		});
		// Product Name ComboBox
		cbProduct = new GERPComboBox("Prod.Name");
		cbProduct.setItemCaptionPropertyId("prodName");
		// Plan Qty. Textfield
		tfRotoDtlqty = new GERPTextField("Plan Qty.");
		tfRotoDtlqty.setValue("0");
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Roto Arm
		// Arm No. Textfield
		tfArmno = new GERPTextField("Arm No");
		// Arm Wo No
		cbArmWorkorder = new GERPComboBox("WO No.");
		cbArmWorkorder.setItemCaptionPropertyId("workOrdrNo");
		loadRotoPlanArmList();
		cbArmWorkorder.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbArmWorkorder.getItem(itemId);
				if (item != null) {
					loadProduct();
				}
			}
		});
		cbArmWorkorder.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbArmWorkorder.getItem(itemId);
				if (item != null) {
					// tfvendorName.setReadOnly(false);
					cbArmproduct.setValue(((RotoPlanArmDM) cbArmWorkorder.getValue()).getProdname().toString());
					tfArmno.setValue(((RotoPlanArmDM) cbArmWorkorder.getValue()).getArmNo().toString());
					tfCycleNo.setValue(((RotoPlanArmDM) cbArmWorkorder.getValue()).getNoOfcycle().toString());
				}
			}
		});
		// Arm Cycle no. Textfield
		tfCycleNo = new TextField();
		tfCycleNo.setWidth("50px");
		tfCycleCount = new TextField();
		tfCycleCount.setWidth("104px");
		tfCycleCount.setValue("1");
		// plan arm shift name
		tfArmShiftName = new GERPTextField("Shift");
		// plan arm employee name
		cbArmEmployee = new GERPComboBox("Employee");
		cbArmEmployee.setItemCaptionPropertyId("fullname");
		loadARMEmployeeList();
		// Arm Product Combo Box
		cbArmproduct = new GERPComboBox("Product Name");
		cbArmproduct.setItemCaptionPropertyId("prodname");
		cbArmstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbArmstatus.setWidth("150px");
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
		tfRotoRefno.setReadOnly(false);
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		Label lbl = new Label();
		flHdrCol4.addComponent(tfRotoRefno);
		flHdrCol1.addComponent(dfRotoDate);
		flHdrCol2.addComponent(lbl);
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
		flHdrCol1.addComponent(cbbranch);
		flHdrCol1.addComponent(cbRotoPlan);
		flHdrCol1.addComponent(tfRotoRefno);
		flHdrCol1.addComponent(dfRotoDate);
		flHdrCol1.addComponent(tfrotoplanedqty);
		flHdrCol2.addComponent(tfRotoHdrqty);
		flHdrCol2.addComponent(taHdrRemarks);
		flHdrCol2.addComponent(cbHdrStatus);
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdrCol1);
		hlHdr.addComponent(flHdrCol2);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding Arm Components
		flArmCol1 = new FormLayout();
		flArmCol2 = new FormLayout();
		flArmCol3 = new FormLayout();
		flArmCol1.addComponent(cbArmWorkorder);
		flArmCol1.addComponent(tfArmno);
		HorizontalLayout hlclycle = new HorizontalLayout();
		hlclycle.addComponent(tfCycleNo);
		hlclycle.addComponent(tfCycleCount);
		hlclycle.setCaption("Cycle No");
		flArmCol1.addComponent(hlclycle);
		flArmCol1.addComponent(tfArmShiftName);
		flArmCol2.addComponent(cbArmEmployee);
		flArmCol2.addComponent(cbArmproduct);
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
		flshiftCol1.addComponent(tfshiftname);
		flshiftCol1.addComponent(cbEmpname);
		flshiftCol2.addComponent(tfTargetQty);
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
		flDtlCol1.addComponent(cbClient);
		flDtlCol1.addComponent(cbWorkorder);
		flDtlCol2.addComponent(cbProduct);
		flDtlCol2.addComponent(tfRotoDtlqty);
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfRotoRefno.getValue() + ", " + cbHdrStatus.getValue());
		RotoList = serviceRotohdr.getRotohdrDetatils(null, (String) tfPlnRef.getValue(), companyid,
				dfRotoDate.getValue(), cbHdrStatus.getValue().toString(), "F");
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
		dfRotoDate.setValue(null);
		tfPlnRef.setValue("");
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
		cbbranch.setRequired(true);
		dfRotoDate.setRequired(true);
		tfshiftname.setRequired(true);
		cbEmpname.setRequired(true);
		cbWorkorder.setRequired(true);
		cbProduct.setRequired(true);
		cbClient.setRequired(true);
		cbArmproduct.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtls.setCaption("Add");
		btnAddShift.setCaption("Add");
		btnAddArm.setCaption("Add");
		tfRotoRefno.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTONO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfRotoRefno.setValue(slnoObj.getKeyDesc());
				tfRotoRefno.setReadOnly(true);
			} else {
				tfRotoRefno.setReadOnly(false);
			}
		}
		catch (Exception e) {
		}
		tblRotoDetails.setVisible(true);
		tblRotoShift.setVisible(true);
		loadArmRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// tblMstScrSrchRslt.setVisible(false);
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		cbbranch.setRequired(true);
		dfRotoDate.setRequired(true);
		tfshiftname.setRequired(true);
		cbEmpname.setRequired(true);
		cbWorkorder.setRequired(true);
		cbProduct.setRequired(true);
		cbClient.setRequired(true);
		cbArmproduct.setRequired(true);
		// reset the input controls to default value comment
		tblMstScrSrchRslt.setVisible(false);
		if (tfRotoRefno.getValue() == null || tfRotoRefno.getValue().trim().length() == 0) {
			tfRotoRefno.setReadOnly(false);
		}
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
		cbbranch.setComponentError(null);
		dfRotoDate.setComponentError(null);
		tfRotoRefno.setComponentError(null);
		tfshiftname.setComponentError(null);
		cbEmpname.setComponentError(null);
		cbClient.setComponentError(null);
		cbWorkorder.setComponentError(null);
		cbProduct.setComponentError(null);
		cbArmproduct.setComponentError(null);
		cbbranch.setRequired(true);
		dfRotoDate.setRequired(true);
		tfshiftname.setRequired(true);
		cbEmpname.setRequired(true);
		cbWorkorder.setRequired(true);
		cbProduct.setRequired(true);
		cbClient.setRequired(true);
		cbArmproduct.setRequired(true);
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
		UI.getCurrent().getSession().setAttribute("audittablepk", rotodtlid);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		// Roto Hdr Resetfields
		cbRotoPlan.setValue(null);
		cbRotoPlan.setComponentError(null);
		cbbranch.setValue(cbbranch.getItemIds().iterator().next());
		tfRotoRefno.setReadOnly(false);
		tfRotoRefno.setValue("");
		tfRotoRefno.setComponentError(null);
		dfRotoDate.setValue(null);
		taHdrRemarks.setValue("");
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		// Roto shift Resetfields
		tfshiftname.setValue("");
		cbEmpname.setValue(null);
		tfTargetQty.setValue("0");
		cbSftStatus.setValue(cbSftStatus.getItemIds().iterator().next());
		cbEmpname.setComponentError(null);
		dfRotoDate.setComponentError(null);
		tfshiftname.setComponentError(null);
		cbbranch.setComponentError(null);
		// Roto Dtls ResetFields
		cbClient.setValue(null);
		cbWorkorder.setValue(null);
		cbProduct.setValue(null);
		tfRotoDtlqty.setValue("0");
		cbSftStatus.setValue(cbSftStatus.getItemIds().iterator().next());
		cbWorkorder.setComponentError(null);
		cbProduct.setComponentError(null);
		// Roto Arm ResetFields
		tfArmno.setValue("0");
		cbArmWorkorder.setValue(null);
		cbArmEmployee.setValue(null);
		tfCycleCount.setValue("1");
		tfRotoHdrqty.setValue("0");
		cbArmproduct.setValue(null);
		cbArmproduct.setComponentError(null);
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
			rotoid = editRotohdr.getRotoid();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected AssemblyPlan. Id -> " + rotodtlid);
			cbbranch.setValue(editRotohdr.getBranchid());
			tfrotoplanedqty.setValue(editRotohdr.getProdtntotqty().toString());
			tfRotoRefno.setReadOnly(false);
			tfRotoRefno.setValue(editRotohdr.getRotorefno());
			tfRotoRefno.setReadOnly(true);
			if (editRotohdr.getRotodate() != null) {
				dfRotoDate.setValue(editRotohdr.getRotodate1());
			}
			Long rotoplanid = editRotohdr.getRotoplanid();
			Collection<?> uomid = cbRotoPlan.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbRotoPlan.getItem(itemId);
				// Get the actual bean and use the data
				RotoPlanHdrDM st = (RotoPlanHdrDM) item.getBean();
				if (rotoplanid != null && rotoplanid.equals(st.getRotoplanid())) {
					cbRotoPlan.setValue(itemId);
				}
			}
			tfRotoHdrqty.setValue(editRotohdr.getProdtntotqty().toString());
			taHdrRemarks.setValue(editRotohdr.getRemarks());
			cbHdrStatus.setValue(editRotohdr.getRotostatus());
			listRotoShift.addAll(serviceRotoShift.getRotoShiftDtls(null, rotoid, null, null,
					(String) cbHdrStatus.getValue(), "F"));
		}
		loadPlanDtlRslt();
		loadShiftRslt();
		loadArmRslt();
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee name
	 */
	private void loadARMEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Employee Search...");
		BeanItemContainer<EmployeeDM> beanEmployeeDM = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", null, null, null, null, null,
				"P"));
		cbArmEmployee.setContainerDataSource(beanEmployeeDM);
	}
	
	private void editRotoDtls() {
		hlUserInputLayout.setVisible(true);
		if (tblRotoDetails.getValue() != null) {
			RotoDtlDM rotoDtlDM = new RotoDtlDM();
			rotoDtlDM = beanrotodtldm.getItem(tblRotoDetails.getValue()).getBean();
			Long woId = rotoDtlDM.getWoid();
			Collection<?> woIdCol = cbArmWorkorder.getItemIds();
			for (Iterator<?> iteratorWO = woIdCol.iterator(); iteratorWO.hasNext();) {
				Object itemIdWOObj = (Object) iteratorWO.next();
				BeanItem<?> itemWoBean = (BeanItem<?>) cbArmWorkorder.getItem(itemIdWOObj);
				// Get the actual bean and use the data
				RotoPlanArmDM workOrderDM = (RotoPlanArmDM) itemWoBean.getBean();
				if (woId != null && woId.equals(workOrderDM.getWoId())) {
					cbArmWorkorder.setValue(itemIdWOObj);
				}
			}
			Long prodId = rotoDtlDM.getProductid();
			Collection<?> prodIdCol = cbArmproduct.getItemIds();
			for (Iterator<?> iterator = prodIdCol.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbArmproduct.getItem(itemId);
				// Get the actual bean and use the data
				RotoPlanArmDM st = (RotoPlanArmDM) item.getBean();
				if (prodId != null && prodId.equals(st.getProductId())) {
					cbArmproduct.setValue(itemId);
				}
			}
		}
	}
	
	private void editRotoShiftDetails() {
		hlUserInputLayout.setVisible(true);
		if (tblRotoShift.getValue() != null) {
			RotoShiftDM rotoShiftDM = new RotoShiftDM();
			rotoShiftDM = beanRotoShiftDM.getItem(tblRotoShift.getValue()).getBean();
			Long empId = rotoShiftDM.getEmployeeid();
			Collection<?> empColId = cbEmpname.getItemIds();
			for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbEmpname.getItem(itemIdClient);
				// Get the actual bean and use the data
				EmployeeDM empObj = (EmployeeDM) itemclient.getBean();
				if (empId != null && empId.equals(empObj.getEmployeeid())) {
					cbEmpname.setValue(itemIdClient);
				}
			}
			if (rotoShiftDM.getShiftname() != null) {
				tfshiftname.setValue(rotoShiftDM.getShiftname());
			}
			if (rotoShiftDM.getAchivedqty() != null) {
				tfTargetQty.setValue(rotoShiftDM.getAchivedqty().toString());
			}
			if (rotoShiftDM.getShiftstatus() != null) {
				cbSftStatus.setValue(rotoShiftDM.getShiftstatus());
			}
		}
	}
	
	private void editArmDetails() {
		hlUserInputLayout.setVisible(true);
		if (tblRotoArm.getValue() != null) {
			RotoArmDM rotoArmDM = new RotoArmDM();
			rotoArmDM = beanRotoArmDM.getItem(tblRotoArm.getValue()).getBean();
			Long uom = rotoArmDM.getProductid();
			Collection<?> uomid = cbArmproduct.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbArmproduct.getItem(itemId);
				// Get the actual bean and use the data
				RotoPlanArmDM st = (RotoPlanArmDM) item.getBean();
				if (uom != null && uom.equals(st.getProductId())) {
					cbArmproduct.setValue(itemId);
				}
			}
			if (rotoArmDM.getShiftname() != null) {
				tfArmShiftName.setValue(rotoArmDM.getShiftname());
			}
			if (rotoArmDM.getArmno() != null) {
				tfArmno.setValue(rotoArmDM.getArmno().toString());
			}
			if (rotoArmDM.getWorkOrdrNo() != null) {
				cbArmWorkorder.setValue(rotoArmDM.getWorkOrdrNo());
			}
			Long emp = rotoArmDM.getEmployeeid();
			Collection<?> empid = cbArmEmployee.getItemIds();
			for (Iterator<?> iterator = empid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbArmEmployee.getItem(itemId);
				// Get the actual bean and use the data
				EmployeeDM st = (EmployeeDM) item.getBean();
				if (emp != null && emp.equals(st.getEmployeeid())) {
					cbArmEmployee.setValue(itemId);
				}
			}
			if (rotoArmDM.getCycleno() != null) {
				tfCycleCount.setValue(rotoArmDM.getCycleno().toString());
			}
			if (rotoArmDM.getRtarmstatus() != null) {
				cbArmstatus.setValue(rotoArmDM.getRtarmstatus());
			}
		}
	}
	
	private void resetRotoDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbClient.setValue(null);
		cbWorkorder.setValue(null);
		cbProduct.setValue(null);
		tfRotoDtlqty.setValue("0");
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		cbClient.setComponentError(null);
		cbWorkorder.setComponentError(null);
		cbProduct.setComponentError(null);
	}
	
	private void resetRotoShiftDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEmpname.setValue(null);
		tfshiftname.setValue("");
		tfTargetQty.setValue("0");
		cbSftStatus.setValue(cbSftStatus.getItemIds().iterator().next());
		tfshiftname.setComponentError(null);
		cbEmpname.setComponentError(null);
	}
	
	private void rotoArmResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbArmproduct.setValue(null);
		cbArmproduct.setValue(null);
		tfArmno.setValue("0");
		cbArmWorkorder.setValue(null);
		cbArmstatus.setValue(cbArmstatus.getItemIds().iterator().next());
	}
	
	private void rotoDtlResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbClient.setValue(null);
		cbArmEmployee.setValue(null);
		cbWorkorder.setValue(null);
		cbProduct.setValue(null);
		tfRotoDtlqty.setValue("0");
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		cbClient.setComponentError(null);
		cbWorkorder.setComponentError(null);
		cbProduct.setComponentError(null);
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbbranch.setComponentError(null);
		dfRotoDate.setComponentError(null);
		tfshiftname.setComponentError(null);
		cbEmpname.setComponentError(null);
		cbClient.setComponentError(null);
		cbWorkorder.setComponentError(null);
		cbProduct.setComponentError(null);
		errorFlag = false;
		if ((cbbranch.getValue() == null)) {
			cbbranch.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbbranch.getValue());
			errorFlag = true;
		}
		if ((dfRotoDate.getValue() == null)) {
			dfRotoDate.setComponentError(new UserError(GERPErrorCodes.NULL_ASMBL_PLAN_DT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfRotoDate.getValue());
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validateShiftDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((tfshiftname.getValue() == "")) {
			tfshiftname.setComponentError(new UserError(GERPErrorCodes.NULL_SHIFT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfshiftname.getValue());
			isValid = false;
		} else {
			tfshiftname.setComponentError(null);
		}
		if ((cbEmpname.getValue() == null)) {
			cbEmpname.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbEmpname.getValue());
			isValid = false;
		} else {
			cbEmpname.setComponentError(null);
		}
		try {
			Long.valueOf(tfTargetQty.getValue());
			tfTargetQty.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfTargetQty.setComponentError(new UserError(GERPErrorCodes.PRDCT_QTY_LONG));
			isValid = false;
		}
		return isValid;
	}
	
	private boolean validateArmDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbArmproduct.getValue() == null)) {
			cbArmproduct.setComponentError(new UserError(GERPErrorCodes.NULL_RTO_ARM));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbProduct.getValue());
			isValid = false;
		} else {
			cbProduct.setComponentError(null);
		}
		try {
			Long.valueOf(tfRotoDtlqty.getValue());
			tfRotoDtlqty.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfRotoDtlqty.setComponentError(new UserError(GERPErrorCodes.PRDCT_QTY_LONG));
			isValid = false;
		}
		if (Long.valueOf(tfCycleCount.getValue()) >= (Long.valueOf(tfCycleNo.getValue()))) {
			Notification.show("The cycle was completed");
			isValid = false;
		}
		return isValid;
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotohdrDM rotohdrDM = new RotohdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				rotohdrDM = beanRotohdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			rotohdrDM.setRotorefno(tfRotoRefno.getValue());
			rotohdrDM.setBranchid((Long.valueOf(cbbranch.getValue().toString())));
			rotohdrDM.setRotodate(dfRotoDate.getValue());
			rotohdrDM.setProdtntotqty(Long.valueOf(tfRotoHdrqty.getValue()));
			rotohdrDM.setRemarks(taHdrRemarks.getValue());
			rotohdrDM.setRotostatus((String) cbHdrStatus.getValue());
			rotohdrDM.setCompanyid(companyid);
			rotohdrDM.setLastupdateddate(DateUtils.getcurrentdate());
			rotohdrDM.setLastupdatedby(username);
			serviceRotohdr.saveRotohdr(rotohdrDM);
			@SuppressWarnings("unchecked")
			Collection<RotoDtlDM> rotoDtls = ((Collection<RotoDtlDM>) tblRotoDetails.getVisibleItemIds());
			for (RotoDtlDM Dtl : (Collection<RotoDtlDM>) rotoDtls) {
				Dtl.setRotoid(rotohdrDM.getRotoid());
				id = rotohdrDM.getRotoid();
				productid = Dtl.getProductid();
				serviceRotoDtl.saveRotoDtl(Dtl);
			}
			@SuppressWarnings("unchecked")
			Collection<RotoShiftDM> rotoShift = ((Collection<RotoShiftDM>) tblRotoShift.getVisibleItemIds());
			for (RotoShiftDM Shift : (Collection<RotoShiftDM>) rotoShift) {
				Shift.setRotoid(rotohdrDM.getRotoid());
				employeeid = Shift.getEmployeeid();
				serviceRotoShift.saveRotoShift(Shift);
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
			resetRotoShiftDetails();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveRotoShiftDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoShiftDM rotoShiftDM = new RotoShiftDM();
			if (tblRotoShift.getValue() != null) {
				rotoShiftDM = beanRotoShiftDM.getItem(tblRotoShift.getValue()).getBean();
				listRotoShift.remove(rotoShiftDM);
			}
			rotoShiftDM.setShiftname(tfshiftname.getValue());
			if (cbEmpname.getValue() != null) {
				rotoShiftDM.setEmployeeid(((EmployeeDM) cbEmpname.getValue()).getEmployeeid());
				rotoShiftDM.setEmpName(((EmployeeDM) cbEmpname.getValue()).getFirstname());
			}
			rotoShiftDM.setAchivedqty(Long.valueOf(tfTargetQty.getValue()));
			if (cbSftStatus.getValue() != null) {
				rotoShiftDM.setShiftstatus((String) cbSftStatus.getValue());
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
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		RotoArmDM rotoarmobj = new RotoArmDM();
		if (tblRotoArm.getValue() != null) {
			rotoarmobj = beanRotoArmDM.getItem(tblRotoArm.getValue()).getBean();
		}
		rotoarmobj.setShiftname(tfArmShiftName.getValue());
		rotoarmobj.setArmno(Long.valueOf(tfArmno.getValue()));
		if (cbArmWorkorder.getValue() != null) {
			rotoarmobj.setWoid(((RotoPlanArmDM) cbArmWorkorder.getValue()).getWoId());
			rotoarmobj.setWorkOrdrNo(((RotoPlanArmDM) cbArmWorkorder.getValue()).getWorkOrdrNo());
		}
		rotoarmobj.setRotoid(id);
		if (cbArmEmployee.getValue() != null) {
			rotoarmobj.setEmployeeid(((EmployeeDM) cbArmEmployee.getValue()).getEmployeeid());
			rotoarmobj.setEmpName(((EmployeeDM) cbArmEmployee.getValue()).getFullname());
		}
		@SuppressWarnings("unchecked")
		Collection<RotoArmDM> colPlanDtls = ((Collection<RotoArmDM>) tblRotoArm.getVisibleItemIds());
		count = 1;
		for (RotoArmDM savecycle : (Collection<RotoArmDM>) colPlanDtls) {
			if (savecycle.getProductid().equals(((RotoPlanArmDM) cbArmproduct.getValue()).getProductId())
					&& (savecycle.getCycleno() != (Long.valueOf(tfCycleNo.getValue())))) {
				count++;
				tfCycleCount.setValue(count + "");
				if (Long.valueOf(tfCycleNo.getValue()).equals(Long.valueOf(tfCycleCount.getValue()))) {
					qty++;
					tfRotoHdrqty.setValue(qty + "");
					serviceRotohdr.updateissueqty(id, qty);
					serviceRotoDtl.updateissueqty(productid, qty);
					serviceRotoShift.updateissueqty(employeeid, qty);
				}
			} else {
				Notification.show("cycle complete");
			}
		}
		rotoarmobj.setCycleno((Long.valueOf(count++)));
		if (cbArmproduct.getValue() != null) {
			rotoarmobj.setProductid(((RotoPlanArmDM) cbArmproduct.getValue()).getProductId());
			rotoarmobj.setProdname(((RotoPlanArmDM) cbArmproduct.getValue()).getProdname());
		}
		if (cbArmstatus.getValue() != null) {
			rotoarmobj.setRtarmstatus((String) cbArmstatus.getValue());
			rotoarmobj.setLastupdateddt(DateUtils.getcurrentdate());
			rotoarmobj.setLastupdatedby(username);
			serviceRotoArm.saveRotoArm(rotoarmobj);
			loadArmRslt();
			btnAddArm.setCaption("Add");
		}
		rotoArmResetFields();
	}
	
	/*
	 * loadClientList()-->this function is used for load the Client name
	 */
	private void loadClientList() {
		BeanItemContainer<ClientDM> beanClient = new BeanItemContainer<ClientDM>(ClientDM.class);
		beanClient.addAll(serviceClient.getClientDetails(companyid, null, null, null, null, null, null, null, "Active",
				"P"));
		cbClient.setContainerDataSource(beanClient);
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee name
	 */
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Employee Search...");
		BeanItemContainer<EmployeeDM> beanEmployeeDM = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", null, null, null, null, null,
				"P"));
		cbEmpname.setContainerDataSource(beanEmployeeDM);
	}
	
	/*
	 * loadBranchList()-->this function is used for load the branch name
	 */
	private void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, BranchDM> beanBranchDM = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranchDM.setBeanIdProperty("branchId");
		beanBranchDM.addAll(servicebeanBranch.getBranchList(null, null, null, "Active", companyid, "P"));
		cbbranch.setContainerDataSource(beanBranchDM);
	}
	
	private void loadRotoPlanList() {
		List<RotoPlanHdrDM> PlanList = serviceRotoplanhdr.getRotoPlanHdrDetails(null, companyid, null, "Active");
		BeanItemContainer<RotoPlanHdrDM> beanrotoplanhdr = new BeanItemContainer<RotoPlanHdrDM>(RotoPlanHdrDM.class);
		beanrotoplanhdr.addAll(PlanList);
		cbRotoPlan.setContainerDataSource(beanrotoplanhdr);
	}
	
	/*
	 * loadProductList()-->this function is used for load the product Name
	 */
	private void loadProductList() {
		Long workOrdHdrId = ((WorkOrderHdrDM) cbWorkorder.getValue()).getWorkOrdrId();
		BeanItemContainer<WorkOrderDtlDM> beanPlnDtl = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
		beanPlnDtl.addAll(serviceWorkOrderDtl.getWorkOrderDtlList(null, workOrdHdrId, null, "F"));
		cbProduct.setContainerDataSource(beanPlnDtl);
	}
	
	// Load Product List
	private void loadProduct() {
		if (cbArmWorkorder.getValue() != null) {
			BeanItemContainer<RotoPlanArmDM> beanPlnDtl = new BeanItemContainer<RotoPlanArmDM>(RotoPlanArmDM.class);
			beanPlnDtl.addAll(serviceRotoplanarm.getRotoPlanArmList(null, null, null, null));
			cbArmproduct.setContainerDataSource(beanPlnDtl);
		}
	}
	
	/*
	 * loadWorkOrderNo()-->this function is used for load the workorderno
	 */
	private void loadWorkOrderNo() {
		Long clientId = (((ClientDM) cbClient.getValue()).getClientId());
		BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		beanWrkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, clientId, null, null, null, "F",
				null, null));
		cbWorkorder.setContainerDataSource(beanWrkOrdHdr);
	}
	
	private void loadRotoPlanArmList() {
		BeanItemContainer<RotoPlanArmDM> beanrotoplanarm = new BeanItemContainer<RotoPlanArmDM>(RotoPlanArmDM.class);
		beanrotoplanarm.addAll(serviceRotoplanarm.getRotoPlanArmList(null, null, null, null));
		cbArmWorkorder.setContainerDataSource(beanrotoplanarm);
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
	}
}
