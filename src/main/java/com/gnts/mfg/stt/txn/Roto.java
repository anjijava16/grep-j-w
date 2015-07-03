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
import com.gnts.erputil.ui.BaseUI;
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
import com.vaadin.data.Item;
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
import com.vaadin.ui.Component;
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

public class Roto extends BaseUI {
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
	private BeanItemContainer<EmployeeDM> beanEmployeeDM = null;
	List<RotoPlanShiftDM> RotoPlanShiftList = null;
	private Table tblDtl, tblShift, tblArm;
	// Search Control Layout
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlSearchLayout, hlDtlandArm, hlHdrAndShift, hlArm, hlShift, hlHdrslap;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlShift, vlArm, vlDtl, vlHdrshiftandDtlarm;
	// Roto Hdr Components
	private TextField tfRotorefno, tfRotoHdrqty, tfrotoplanedqty, tfPlnRef;
	private ComboBox cbbranch, Cbrotoplanid;
	private DateField dfrotodate;
	private TextArea tahdrremarks;
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4;
	// Roto Dtl Components
	private TextField tfRotoDtlqty;
	private ComboBox cbWO, cbProduct, cbClient, cbDtlStatus, cbarmWO;
	private FormLayout flDtlCol1, flDtlCol2, flDtlCol3;
	List<RotoPlanDtlDM> RotoplanDtlList = new ArrayList<RotoPlanDtlDM>();
	// Roto Shift Components
	private TextField tfshiftname, tfSfiftqty;
	private ComboBox cbEmpname, cbSftStatus;
	private FormLayout flshiftCol1, flshiftCol2, flshiftCol3;
	List<RotoShiftDM> ShiftList = new ArrayList<RotoShiftDM>();
	// Roto Arm Components
	private TextField tfArmno, tfCycleno, tfcyclecount, tfarmshiftname;
	private ComboBox cbArmproduct, cbArmstatus, cbarmempname;
	private FormLayout flArmCol1, flArmCol2, flArmCol3;
	private Button btnAddDtls = new GERPButton("Add", "Addbt", this);
	private Button btnAddShift = new GERPButton("Add", "Addbt", this);
	private Button btnAddArm = new GERPButton("Add", "Addbt", this);
	public Button btndelete = new GERPButton("Delete", "delete", this);
	public Button btnShiftdelete = new GERPButton("Delete", "delete", this);
	public Button btnArmDelete = new GERPButton("Delete", "delete", this);
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
	int count =1;
	Long qty =0L;
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
		/*
		 * btnAddDtls.addClickListener(new ClickListener() { // Click Listener for Add and Update private static final
		 * long serialVersionUID = 6551953728534136363L;
		 * @Override public void buttonClick(ClickEvent event) { if (validateDtlDetails()) {
		 * saveRotoplanDtlListDetails(); }cbarmWO } });
		 */
		btnAddShift.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateShiftDetails()) {
					saverotoShiftListDetails();
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
		tblDtl = new GERPTable();
		tblDtl.setWidth("588px");
		tblDtl.setPageLength(5);
		tfPlnRef = new TextField("Plan Ref.No");
		tblDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblDtl.isSelected(event.getItemId())) {
					tblDtl.setImmediate(true);
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
		tblShift = new GERPTable();
		tblShift.setWidth("912px");
		tblShift.setPageLength(2);
		tblShift.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblShift.isSelected(event.getItemId())) {
					tblShift.setImmediate(true);
					btnAddShift.setCaption("Add");
					btnAddShift.setStyleName("savebt");
					rotoShiftResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddShift.setCaption("Update");
					btnAddShift.setStyleName("savebt");
					editRotoShiftDetails();
				}
			}
		});
		tblArm = new GERPTable();
		tblArm.setWidth("588px");
		tblArm.setPageLength(5);
		tblArm.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblArm.isSelected(event.getItemId())) {
					tblArm.setImmediate(true);
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
				tfRotorefno.setReadOnly(false);
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
		/*
		 * btndelete.addClickListener(new ClickListener() { // Click Listener for Add and Update private static final
		 * long serialVersionUID = 6551953728534136363L;
		 * @Override public void buttonClick(ClickEvent event) { if (btndelete == event.getButton()) { deleteDetails();
		 * btnAddDtls.setCaption("Add"); } } });
		 */
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
		tfRotorefno = new GERPTextField("Roto Ref.No");
		// Branch Hdr Combo Box
		cbbranch = new GERPComboBox("Branch Name");
		cbbranch.setItemCaptionPropertyId("branchName");
		loadBranchList();
		// Roto Hdr Datefield
		dfrotodate = new PopupDateField("Roto Date");
		dfrotodate.setDateFormat("dd-MMM-yyyy");
		dfrotodate.setWidth("130px");
		// Roto Hdr Qty.Text field
		tfRotoHdrqty = new GERPTextField("production Quantity");
		// tfRotoHdrqty.setValue("0");
		// Roto planed qty
		tfrotoplanedqty = new GERPTextField("Planned Quantity");
		tfrotoplanedqty.setValue("0");
		Cbrotoplanid = new GERPComboBox("Plan Ref.No");
		Cbrotoplanid.setItemCaptionPropertyId("rotoplanrefno");
		loadrotohdrlist();
		Cbrotoplanid.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) Cbrotoplanid.getItem(itemId);
				if (item != null) {
					// tfvendorName.setReadOnly(false);
					dfrotodate.setValue(((RotoPlanHdrDM) Cbrotoplanid.getValue()).getRotoplandt1());
					rotoplanId = (((RotoPlanHdrDM) Cbrotoplanid.getValue()).getRotoplanidLong());
					System.out.println("rotoplanId--->" + rotoplanId);
					tfrotoplanedqty.setValue(((RotoPlanHdrDM) Cbrotoplanid.getValue()).getPlannedqty().toString());
/*					tfRotoHdrqty.setValue(((RotohdrDM) Cbrotoplanid.getValue()).getProdtntotqty().toString());*/
					RotoplanDtlList = serviceRotoplandtl.getRotoPlanDtlList(null, rotoplanId, null, null, "Active");
					loadPlanDtlRslt();
					RotoPlanShiftList = serviceRotoplanshift.getRotoPlanShiftList(null, rotoplanId, null, "Active");
					loadShiftRslt();
					// tfvendorName.setReadOnly(true);
				}
			}
		});
		// Roto Remarks TextArea
		tahdrremarks = new GERPTextArea("Remarks");
		tahdrremarks.setHeight("75px");
		tahdrremarks.setWidth("150px");
		// Status ComboBox
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbDtlStatus.setWidth("100px");
		// Status ComboBox
		cbSftStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Roto Shift
		// Shift Name TextField
		tfshiftname = new GERPTextField("Shift Name");
		// Employee Name combobox
		cbEmpname = new GERPComboBox("Employee Name");
		cbEmpname.setItemCaptionPropertyId("fullname");
		loadEmployeeList();
		// TargetQty TextField
		tfSfiftqty = new GERPTextField("Target Qty");
		tfSfiftqty.setValue("0");
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
		cbWO = new GERPComboBox("WO No.");
		cbWO.setItemCaptionPropertyId("workOrdrNo");
		cbWO.setWidth("100px");
		cbWO.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbWO.getItem(itemId);
				if (item != null) {
					loadProductList();
				}
			}
		});
		// Product Name ComboBox
		cbProduct = new GERPComboBox("Prod.Name");
		cbProduct.setWidth("100px");
		cbProduct.setItemCaptionPropertyId("prodName");
		// Plan Qty. Textfield
		tfRotoDtlqty = new GERPTextField("Plan Qty.");
		tfRotoDtlqty.setValue("0");
		tfRotoDtlqty.setWidth("100px");
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Roto Arm
		// Arm No. Textfield
		tfArmno = new GERPTextField("Arm No");
		tfArmno.setWidth("130px");
		// Arm Wo No
		cbarmWO = new GERPComboBox("WO No.");
		cbarmWO.setItemCaptionPropertyId("workOrdrNo");
		cbarmWO.setWidth("100px");
		loadarmwororderno();
		cbarmWO.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbarmWO.getItem(itemId);
				if (item != null) {
					loadProduct();
				}
			}
		});
		cbarmWO.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				// Get the selected item
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbarmWO.getItem(itemId);
				if (item != null) {
					// tfvendorName.setReadOnly(false);
					cbArmproduct.setValue(((RotoPlanArmDM) cbarmWO.getValue()).getProdname().toString());
					tfArmno.setValue(((RotoPlanArmDM) cbarmWO.getValue()).getArmNo().toString());
					tfCycleno.setValue(((RotoPlanArmDM) cbarmWO.getValue()).getNoOfcycle().toString());
				}
			}
		});
		// Arm Cycle no. Textfield
		tfCycleno = new TextField();
		tfCycleno.setWidth("50px");
		tfcyclecount = new TextField();
		tfcyclecount.setWidth("80px");
		tfcyclecount.setValue("1");
		// plan arm shift name
		tfarmshiftname = new TextField("Shift Name");
		tfarmshiftname.setWidth("120");
		//tfarmshiftname.setValue("0");
		// plan arm employee name
		cbarmempname = new GERPComboBox("Employee Name");
		cbarmempname.setItemCaptionPropertyId("fullname");
		loadARMEmployeeList();
		// Arm Product Combo Box
		cbArmproduct = new GERPComboBox("Product Name");
		cbArmproduct.setItemCaptionPropertyId("prodname");
		cbArmproduct.setWidth("130px");
		cbArmstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbArmstatus.setWidth("130px");
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
		tfRotorefno.setReadOnly(false);
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		Label lbl = new Label();
		flHdrCol4.addComponent(tfRotorefno);
		flHdrCol1.addComponent(dfrotodate);
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
		flHdrCol1.addComponent(Cbrotoplanid);
		flHdrCol1.addComponent(tfRotorefno);
		flHdrCol1.addComponent(dfrotodate);
		flHdrCol2.addComponent(tfrotoplanedqty);
		flHdrCol2.addComponent(tfRotoHdrqty);
		flHdrCol2.addComponent(tahdrremarks);
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
		flArmCol1.addComponent(cbarmWO);
		flArmCol1.addComponent(tfArmno);
		HorizontalLayout hlclycle = new HorizontalLayout();
		hlclycle.addComponent(tfCycleno);
		hlclycle.addComponent(tfcyclecount);
		hlclycle.setCaption("Cycle No");
		flArmCol1.addComponent(hlclycle);
		flArmCol2.addComponent(tfarmshiftname);
		flArmCol2.addComponent(cbarmempname);
		flArmCol2.addComponent(cbArmproduct);
		flArmCol2.addComponent(cbArmstatus);
		flArmCol3.addComponent(btnAddArm);
		flArmCol3.addComponent(btnArmDelete);
		hlArm = new HorizontalLayout();
		hlArm.addComponent(flArmCol1);
		hlArm.addComponent(flArmCol1);
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
		flshiftCol2.addComponent(tfSfiftqty);
		flshiftCol2.addComponent(cbSftStatus);
		flshiftCol3.addComponent(btnAddShift);
		flshiftCol3.addComponent(btnShiftdelete);
		flshiftCol3.setComponentAlignment(btnAddShift, Alignment.BOTTOM_CENTER);
		vlShift = new VerticalLayout();
		hlShift = new HorizontalLayout();
		hlShift.addComponent(flshiftCol1);
		hlShift.addComponent(flshiftCol2);
		hlShift.addComponent(flshiftCol3);
		vlShift.addComponent(hlShift);
		vlShift.addComponent(tblShift);
		// Adding Dtl Components
		flDtlCol1 = new FormLayout();
		flDtlCol2 = new FormLayout();
		flDtlCol3 = new FormLayout();
		flDtlCol1.addComponent(cbClient);
		flDtlCol1.addComponent(cbWO);
		flDtlCol2.addComponent(cbProduct);
		flDtlCol2.addComponent(tfRotoDtlqty);
		flDtlCol3.addComponent(cbDtlStatus);
		HorizontalLayout hlBtn = new HorizontalLayout();
		hlBtn.addComponent(btnAddDtls);
		hlBtn.addComponent(btndelete);
		// flDtlCol3.addComponent(btnAddDtls);
		flDtlCol3.addComponent(hlBtn);
		hlHdrslap = new HorizontalLayout();
		hlHdrslap.addComponent(flDtlCol1);
		hlHdrslap.addComponent(flDtlCol2);
		hlHdrslap.addComponent(flDtlCol3);
		hlHdrslap.setSpacing(true);
		hlHdrslap.setMargin(true);
		vlArm = new VerticalLayout();
		vlArm.addComponent(hlArm);
		vlArm.addComponent(tblArm);
		vlDtl = new VerticalLayout();
		// vlDtl.addComponent(hlHdrslap);
		vlDtl.addComponent(tblDtl);
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
				+ companyid + ", " + tfRotorefno.getValue() + ", " + cbHdrStatus.getValue());
		RotoList = serviceRotohdr.getRotohdrDetatils(null, (String) tfPlnRef.getValue(), companyid,
				dfrotodate.getValue(), cbHdrStatus.getValue().toString(), "F");
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
	
	/*
	 * private void loadPlanDtlRslt() { logger.info("Company ID : " + companyid + " | User Name : " + username + " > " +
	 * "Loading Search..."); recordDtl = RotoplanDtlList.size(); System.out.println("listsize------>" +
	 * RotoplanDtlList.size()); beanRotoplanDtlDM = new BeanItemContainer<RotoPlanDtlDM>(RotoPlanDtlDM.class);
	 * beanRotoplanDtlDM.addAll(RotoplanDtlList); logger.info("Company ID : " + companyid +
	 * " | saveasmblPlnDtlListDetails User Name : " + username + " > " + "Search Parameters are " + companyid + ", " +
	 * cbClient.getValue() + ", " + tfRotoDtlqty.getValue() + (String) cbDtlStatus.getValue() + ", " + rotodtlid);
	 * logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the PlanDtl. result set");
	 * tblDtl.setContainerDataSource(beanRotoplanDtlDM); tblDtl.setVisibleColumns(new Object[] { "clientname", "woNo",
	 * "productname", "plannedqty" }); tblDtl.setColumnHeaders(new String[] { "Client Name", "WO No.", "Product Name",
	 * "Planned Qty." }); tblDtl.setColumnAlignment("rotoplandtlId", Align.RIGHT); tblDtl.setColumnFooter("plannedqty",
	 * "No.of Records : " + recordDtl); }
	 */
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
			tblDtl.setContainerDataSource(beanrotodtldm);
			tblDtl.setVisibleColumns(new Object[] { "clientName", "woNo", "prodName", "plannedqty","prodtnqty" });
			tblDtl.setColumnHeaders(new String[] { "clientName", "WO No.", "Product Name", "Planned Qty.","Product Qty" });
			/*
			 * tblDtl.setColumnAlignment("rotoplandtlId", Align.RIGHT); tblDtl.setColumnFooter("plannedqty",
			 * "No.of Records : " + recordDtl);
			 */
		}
	}
	
	private void loadShiftRslt() {
		/*
		 * logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		 * recordShiftCnt = ShiftList.size(); beanRotoShiftDM = new BeanItemContainer<RotoShiftDM>(RotoShiftDM.class);
		 * beanRotoShiftDM.addAll(ShiftList); logger.info("Company ID : " + companyid + " | User Name : " + username +
		 * " > " + "Got the Roto. result set"); tblShift.setContainerDataSource(beanRotoShiftDM);
		 * tblShift.setVisibleColumns(new Object[] { "shiftname", "empName", "achivedqty" });
		 * tblShift.setColumnHeaders(new String[] { "Shift Name", "Emp.Name", "Target Qty" });
		 * tblShift.setColumnAlignment("rotosftid", Align.RIGHT); tblShift.setColumnFooter("achivedqty",
		 * "No.of Records : " + recordShiftCnt);
		 */
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
		recordShiftCnt = ShiftList.size();
		beanRotoShiftDM = new BeanItemContainer<RotoShiftDM>(RotoShiftDM.class);
		beanRotoShiftDM.addAll(rotoshiftdm);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Rotoplan. result set");
		tblShift.setContainerDataSource(beanRotoShiftDM);
		/*try {
			tblShift.removeGeneratedColumn("achieved");
		}
		catch (Exception e) {
		}*/
	/*	tblShift.addGeneratedColumn("achieved", new CheckBoxColumnGenerator());*/
		tblShift.setVisibleColumns(new Object[] { "shiftname", "empName", "targetqty", "achivedqty" });
		tblShift.setColumnHeaders(new String[] { "Shift Name", "Employee Name", "Target Qty", "Achived Qty" });
		tblShift.setColumnAlignment("rotosftid", Align.RIGHT);
		tblShift.setColumnFooter("achivedqty", "No.of Records : " + recordShiftCnt);
		tblShift.setFooterVisible(true);
	}
	
	private void loadArmRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<RotoArmDM> armList = new ArrayList<RotoArmDM>();
		recordArmCnt = armList.size();
		armList = serviceRotoArm.getRotoArmList(null, null, null, null, null, "F",null);
		System.out.println("armList-->" + armList.size());
		beanRotoArmDM = new BeanItemContainer<RotoArmDM>(RotoArmDM.class);
		beanRotoArmDM.addAll(armList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Roto. result set");
		tblArm.setContainerDataSource(beanRotoArmDM);
		tblArm.setVisibleColumns(new Object[] { "productslno", "prodname", "shiftname", "empName", "workOrdrNo",
				"cycleno", "armno" });
		tblArm.setColumnHeaders(new String[] { "productslno", "Product Name", "shiftname", "Employee name", "WO NO",
				"Cycle No", "Arm No" });
		tblArm.setColumnAlignment("rotoarmid", Align.RIGHT);
		tblArm.setColumnFooter("armno", "No.of Records : " + recordArmCnt);
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
		dfrotodate.setValue(null);
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
		dfrotodate.setRequired(true);
		tfshiftname.setRequired(true);
		cbEmpname.setRequired(true);
		cbWO.setRequired(true);
		cbProduct.setRequired(true);
		cbClient.setRequired(true);
		cbArmproduct.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		btnAddDtls.setCaption("Add");
		btnAddShift.setCaption("Add");
		btnAddArm.setCaption("Add");
		// tblDtl.setVisible(true);
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTONO");
		tfRotorefno.setReadOnly(true);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfRotorefno.setReadOnly(true);
			} else {
				tfRotorefno.setReadOnly(false);
			}
		}
		tblDtl.setVisible(true);
		tblShift.setVisible(true);
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
		dfrotodate.setRequired(true);
		tfshiftname.setRequired(true);
		cbEmpname.setRequired(true);
		cbWO.setRequired(true);
		cbProduct.setRequired(true);
		cbClient.setRequired(true);
		cbArmproduct.setRequired(true);
		// reset the input controls to default value comment
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTONO");
		tfRotorefno.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfRotorefno.setReadOnly(true);
			}
		}
		tblMstScrSrchRslt.setVisible(false);
		if (tfRotorefno.getValue() == null || tfRotorefno.getValue().trim().length() == 0) {
			tfRotorefno.setReadOnly(false);
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
		dfrotodate.setComponentError(null);
		tfRotorefno.setComponentError(null);
		tfshiftname.setComponentError(null);
		cbEmpname.setComponentError(null);
		cbClient.setComponentError(null);
		cbWO.setComponentError(null);
		cbProduct.setComponentError(null);
		cbArmproduct.setComponentError(null);
		cbbranch.setRequired(true);
		dfrotodate.setRequired(true);
		tfshiftname.setRequired(true);
		cbEmpname.setRequired(true);
		cbWO.setRequired(true);
		cbProduct.setRequired(true);
		cbClient.setRequired(true);
		cbArmproduct.setRequired(true);
		RotoDtlResetFields();
		rotoShiftResetFields();
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
		Cbrotoplanid.setValue(null);
		Cbrotoplanid.setComponentError(null);
		cbbranch.setValue(cbbranch.getItemIds().iterator().next());
		tfRotorefno.setReadOnly(false);
		tfRotorefno.setValue("");
		/* tfRotorefno.setReadOnly(true); */
		tfRotorefno.setComponentError(null);
		dfrotodate.setValue(null);
		// tfRotoHdrqty.setValue("0");
		tahdrremarks.setValue("");
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		// Roto shift Resetfields
		tfshiftname.setValue("");
		cbEmpname.setValue(null);
		tfSfiftqty.setValue("0");
		cbSftStatus.setValue(cbSftStatus.getItemIds().iterator().next());
		cbEmpname.setComponentError(null);
		dfrotodate.setComponentError(null);
		tfshiftname.setComponentError(null);
		cbbranch.setComponentError(null);
		// Roto Dtls ResetFields
		cbClient.setValue(null);
		cbWO.setValue(null);
		cbProduct.setValue(null);
		tfRotoDtlqty.setValue("0");
		cbSftStatus.setValue(cbSftStatus.getItemIds().iterator().next());
		cbWO.setComponentError(null);
		cbProduct.setComponentError(null);
		// Roto Arm ResetFields
		tfArmno.setValue("0");
		cbarmWO.setValue(null);
		//tfarmshiftname.setValue("1");
		cbarmempname.setValue(null);
		tfcyclecount.setValue("1");
		tfRotoHdrqty.setValue("0");
		cbArmproduct.setValue(null);
		cbArmproduct.setComponentError(null);
		ShiftList = new ArrayList<RotoShiftDM>();
		RotoPlanShiftList = new ArrayList<RotoPlanShiftDM>();
		// RotoplanDtlList = new ArrayList<RotoPlanDtlDM>();
		tblDtl.removeAllItems();
		tblShift.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editRotoHdrDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		Item HdrRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		if (HdrRcd != null) {
			RotohdrDM editRotohdr = beanRotohdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			rotoid = editRotohdr.getRotoid();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected AssemblyPlan. Id -> " + rotodtlid);
			cbbranch.setValue(editRotohdr.getBranchid());
			tfrotoplanedqty.setValue(editRotohdr.getProdtntotqty().toString());
			tfRotorefno.setReadOnly(false);
			tfRotorefno.setValue((String) HdrRcd.getItemProperty("rotorefno").getValue());
			tfRotorefno.setReadOnly(true);
			if (editRotohdr.getRotodate() != null) {
				dfrotodate.setValue(editRotohdr.getRotodate1());
			}
			/*
			 * if (editRotohdr.getRotoplanid() != null) { Cbrotoplanid.setValue(editRotohdr.getRotorefno().toString());
			 * }
			 */
			Long rotoplanid = editRotohdr.getRotoplanid();
			Collection<?> uomid = Cbrotoplanid.getItemIds();
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) Cbrotoplanid.getItem(itemId);
				// Get the actual bean and use the data
				RotoPlanHdrDM st = (RotoPlanHdrDM) item.getBean();
				if (rotoplanid != null && rotoplanid.equals(st.getRotoplanid())) {
					Cbrotoplanid.setValue(itemId);
				}
			}
			tfRotoHdrqty.setValue(editRotohdr.getProdtntotqty().toString());
			tahdrremarks.setValue(editRotohdr.getRemarks());
			cbHdrStatus.setValue(editRotohdr.getRotostatus());
			ShiftList.addAll(serviceRotoShift.getRotoShiftDtls(null, rotoid, null, null,
					(String) cbHdrStatus.getValue(), "F"));
			// ArmList.addAll(serviceRotoArm.getRotoArmList(null, rotoid, null, null, (String) cbHdrStatus.getValue(),
			// "F"));
		}
		loadPlanDtlRslt();
		loadShiftRslt();
		loadArmRslt();
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee name
	 */
	public void loadARMEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Employee Search...");
		List<EmployeeDM> lookUpList = serviceEmployee.getEmployeeList(null, null, null, "Active", null, null, null,
				null, null, "P");
		beanEmployeeDM = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployeeDM.addAll(lookUpList);
		cbarmempname.setContainerDataSource(beanEmployeeDM);
	}
	
	private void editRotoDtls() {
		Notification.show("editRotoDtls-->");
		hlUserInputLayout.setVisible(true);
		Item DtlRcd = tblDtl.getItem(tblDtl.getValue());
		if (DtlRcd != null) {
			RotoDtlDM editRotoDtl = new RotoDtlDM();
			editRotoDtl = beanrotodtldm.getItem(tblDtl.getValue()).getBean();
			/*
			 * Long clientId = editRotoDtl.getClientid(); Collection<?> clientIdCol = cbClient.getItemIds(); for
			 * (Iterator<?> iteratorclient = clientIdCol.iterator(); iteratorclient.hasNext();) { Object itemIdClient =
			 * (Object) iteratorclient.next(); BeanItem<?> itemclient = (BeanItem<?>) cbClient.getItem(itemIdClient); //
			 * Get the actual bean and use the data ClientDM clientObj = (ClientDM) itemclient.getBean(); if (clientId
			 * != null && clientId.equals(clientObj.getClientId())) { cbClient.setValue(itemIdClient); } }
			 */
			Long woId = editRotoDtl.getWoid();
			Collection<?> woIdCol = cbarmWO.getItemIds();
			for (Iterator<?> iteratorWO = woIdCol.iterator(); iteratorWO.hasNext();) {
				Object itemIdWOObj = (Object) iteratorWO.next();
				BeanItem<?> itemWoBean = (BeanItem<?>) cbarmWO.getItem(itemIdWOObj);
				// Get the actual bean and use the data
				RotoPlanArmDM workOrderDM = (RotoPlanArmDM) itemWoBean.getBean();
				if (woId != null && woId.equals(workOrderDM.getWoId())) {
					cbarmWO.setValue(itemIdWOObj);
				}
			}
			Long prodId = editRotoDtl.getProductid();
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
			System.out.println("editRotoDtl" + editRotoDtl.getWoid().toString());
			/*
			 * if(editRotoDtl.getWoId() !=null){ cbarmWO.setValue(Long.valueOf(editRotoDtl.getWoId())); }
			 */
			System.out.println("editRotoDtl--->" + editRotoDtl.getProdName());
			/*
			 * if(editRotoDtl.getProductname() !=null){ cbArmproduct.setValue(editRotoDtl.getProductname()); }
			 */
			/*
			 * if (DtlRcd.getItemProperty("rtodtlstatus").getValue() != null) {
			 * cbDtlStatus.setValue(DtlRcd.getItemProperty("rtodtlstatus").getValue().toString()); }
			 */
		}
	}
	
	private void editRotoShiftDetails() {
		hlUserInputLayout.setVisible(true);
		Item ShiftRcd = tblShift.getItem(tblShift.getValue());
		if (ShiftRcd != null) {
			RotoShiftDM editRotoShift = new RotoShiftDM();
			editRotoShift = beanRotoShiftDM.getItem(tblShift.getValue()).getBean();
			Long empId = editRotoShift.getEmployeeid();
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
			if (ShiftRcd.getItemProperty("shiftname").getValue() != null) {
				tfshiftname.setValue(ShiftRcd.getItemProperty("shiftname").getValue().toString());
			}
			if (ShiftRcd.getItemProperty("achivedqty").getValue() != null) {
				tfSfiftqty.setValue(ShiftRcd.getItemProperty("achivedqty").getValue().toString());
			}
			if (ShiftRcd.getItemProperty("shiftstatus").getValue() != null) {
				cbSftStatus.setValue(ShiftRcd.getItemProperty("shiftstatus").getValue().toString());
			}
		}
	}
	
	private void editArmDetails() {
		hlUserInputLayout.setVisible(true);
		Item ArmRcd = tblArm.getItem(tblArm.getValue());
		if (ArmRcd != null) {
			RotoArmDM editRotoArm = new RotoArmDM();
			editRotoArm = beanRotoArmDM.getItem(tblArm.getValue()).getBean();
			Long uom = editRotoArm.getProductid();
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
			if (editRotoArm.getShiftname() != null) {
				System.out.println("tfarmshiftname--------->" + tfarmshiftname);
				tfarmshiftname.setValue(ArmRcd.getItemProperty("shiftname").getValue().toString());
			}
			if (ArmRcd.getItemProperty("armno").getValue() != null) {
				tfArmno.setValue(ArmRcd.getItemProperty("armno").getValue().toString());
			}
			if (ArmRcd.getItemProperty("workOrdrNo").getValue() != null) {
				cbarmWO.setValue(ArmRcd.getItemProperty("workOrdrNo").getValue().toString());
			}
			/*
			 * if (ArmRcd.getItemProperty("empName").getValue() !=null) {
			 * cbarmempname.setValue(ArmRcd.getItemProperty("empName").getValue().toString()); }
			 */
			Long emp = editRotoArm.getEmployeeid();
			Collection<?> empid = cbarmempname.getItemIds();
			for (Iterator<?> iterator = empid.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbarmempname.getItem(itemId);
				// Get the actual bean and use the data
				EmployeeDM st = (EmployeeDM) item.getBean();
				if (emp != null && emp.equals(st.getEmployeeid())) {
					cbarmempname.setValue(itemId);
				}
			}
			if (ArmRcd.getItemProperty("cycleno").getValue() != null) {
				tfcyclecount.setValue(ArmRcd.getItemProperty("cycleno").getValue().toString());
			}
			if (ArmRcd.getItemProperty("rtarmstatus").getValue() != null) {
				cbArmstatus.setValue(ArmRcd.getItemProperty("rtarmstatus").getValue().toString());
			}
		}
	}
	
	private void RotoDtlResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbClient.setValue(null);
		cbWO.setValue(null);
		cbProduct.setValue(null);
		tfRotoDtlqty.setValue("0");
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		cbClient.setComponentError(null);
		cbWO.setComponentError(null);
		cbProduct.setComponentError(null);
	}
	
	private void rotoShiftResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEmpname.setValue(null);
		tfshiftname.setValue("");
		tfSfiftqty.setValue("0");
		cbSftStatus.setValue(cbSftStatus.getItemIds().iterator().next());
		tfshiftname.setComponentError(null);
		cbEmpname.setComponentError(null);
	}
	
	private void rotoArmResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbArmproduct.setValue(null);
		cbArmproduct.setValue(null);
		//tfarmshiftname.setValue("0");
		tfArmno.setValue("0");
		cbarmWO.setValue(null);
		// tfcyclecount.setValue("0");
		cbArmstatus.setValue(cbArmstatus.getItemIds().iterator().next());
	}
	
	private void rotoDtlResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbClient.setValue(null);
		cbarmempname.setValue(null);
		cbWO.setValue(null);
		cbProduct.setValue(null);
		tfRotoDtlqty.setValue("0");
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		cbClient.setComponentError(null);
		cbWO.setComponentError(null);
		cbProduct.setComponentError(null);
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbbranch.setComponentError(null);
		dfrotodate.setComponentError(null);
		tfshiftname.setComponentError(null);
		cbEmpname.setComponentError(null);
		cbClient.setComponentError(null);
		cbWO.setComponentError(null);
		cbProduct.setComponentError(null);
		errorFlag = false;
		if ((cbbranch.getValue() == null)) {
			cbbranch.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbbranch.getValue());
			errorFlag = true;
		}
		if ((dfrotodate.getValue() == null)) {
			dfrotodate.setComponentError(new UserError(GERPErrorCodes.NULL_ASMBL_PLAN_DT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfrotodate.getValue());
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
			Long.valueOf(tfSfiftqty.getValue());
			tfSfiftqty.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfSfiftqty.setComponentError(new UserError(GERPErrorCodes.PRDCT_QTY_LONG));
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
		if (Long.valueOf(tfcyclecount.getValue()) >= (Long.valueOf(tfCycleno.getValue()))) {
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
			RotohdrDM rotoHdrobj = new RotohdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				rotoHdrobj = beanRotohdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			} else {
				List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId,
						"STT_MF_RTONO");
				for (SlnoGenDM slnoObj : slnoList) {
					if (slnoObj.getAutoGenYN().equals("Y")) {
						System.out.println("slnoObj.getKeyDesc()-->" + slnoObj.getKeyDesc());
						rotoHdrobj.setRotorefno(slnoObj.getKeyDesc());
					}
				}
				// rotoHdrobj.setRotorefno(((RotoPlanHdrDM) (Cbrotoplanid.getValue())).getRotoplanid());
				rotoHdrobj.setBranchid((Long.valueOf(cbbranch.getValue().toString())));
				rotoHdrobj.setRotodate(dfrotodate.getValue());
				rotoHdrobj.setProdtntotqty(Long.valueOf(tfRotoHdrqty.getValue()));
				rotoHdrobj.setRemarks(tahdrremarks.getValue());
				rotoHdrobj.setRotostatus((String) cbHdrStatus.getValue());
				rotoHdrobj.setCompanyid(companyid);
				rotoHdrobj.setLastupdateddate(DateUtils.getcurrentdate());
				rotoHdrobj.setLastupdatedby(username);
				serviceRotohdr.saveRotohdr(rotoHdrobj);
				
				@SuppressWarnings("unchecked")
				Collection<RotoDtlDM> rotoDtls = ((Collection<RotoDtlDM>) tblDtl.getVisibleItemIds());
				for (RotoDtlDM Dtl : (Collection<RotoDtlDM>) rotoDtls) {
					Dtl.setRotoid(rotoHdrobj.getRotoid());
					id=rotoHdrobj.getRotoid();
					productid=Dtl.getProductid();
					serviceRotoDtl.saveRotoDtl(Dtl);
				}
				@SuppressWarnings("unchecked")
				Collection<RotoShiftDM> rotoShift = ((Collection<RotoShiftDM>) tblShift.getVisibleItemIds());
				for (RotoShiftDM Shift : (Collection<RotoShiftDM>) rotoShift) {
					Shift.setRotoid(rotoHdrobj.getRotoid());
					employeeid=Shift.getEmployeeid();
					serviceRotoShift.saveRotoShift(Shift);
				}
				/*
				 * @SuppressWarnings("unchecked") Collection<RotoArmDM> rotoArm = ((Collection<RotoArmDM>)
				 * tblArm.getVisibleItemIds()); for (RotoArmDM Arm : (Collection<RotoArmDM>) rotoArm) {
				 * Arm.setRotoid(rotoHdrobj.getRotoid()); serviceRotoArm.saveRotoArm(Arm); }
				 */
				if (tblMstScrSrchRslt.getValue() == null) {
					List<SlnoGenDM> slnolist = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId,
							"STT_MF_RTONO");
					for (SlnoGenDM slnoObj : slnolist) {
						if (slnoObj.getAutoGenYN().equals("Y")) {
							serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "STT_MF_RTONO");
						}
					}
				}
				RotoDtlResetFields();
				// saverotoArmListDetails();
				rotoShiftResetFields();
			//	resetFields();
				loadSrchRslt();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void saverotoShiftListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			RotoShiftDM rotoshiftobj = new RotoShiftDM();
			if (tblShift.getValue() != null) {
				rotoshiftobj = beanRotoShiftDM.getItem(tblShift.getValue()).getBean();
				ShiftList.remove(rotoshiftobj);
			}
			rotoshiftobj.setShiftname(tfshiftname.getValue());
			if (cbEmpname.getValue() != null) {
				rotoshiftobj.setEmployeeid(((EmployeeDM) cbEmpname.getValue()).getEmployeeid());
				rotoshiftobj.setEmpName(((EmployeeDM) cbEmpname.getValue()).getFirstname());
			}
			rotoshiftobj.setAchivedqty(Long.valueOf(tfSfiftqty.getValue()));
			if (cbSftStatus.getValue() != null) {
				rotoshiftobj.setShiftstatus((String) cbSftStatus.getValue());
			}
			rotoshiftobj.setLastupdateddt(DateUtils.getcurrentdate());
			rotoshiftobj.setLastupdatedby(username);
			ShiftList.add(rotoshiftobj);
			loadShiftRslt();
			btnAddShift.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		rotoShiftResetFields();
	}
	
	private void save(){
		saverotoArmListDetails();
		@SuppressWarnings("unchecked")
		Collection<RotoArmDM> colPlanDtls = ((Collection<RotoArmDM>) tblArm.getVisibleItemIds());
		for (RotoArmDM savecycle : (Collection<RotoArmDM>) colPlanDtls) {
			System.out.println("tfcyclecount.getValue()---->"+tfcyclecount.getValue());
			System.out.println("tblArm.size()"+tblArm.size());
			if ((savecycle.getCycleno()).equals(1L)&& ((tblArm.size()==1))) {
					Notification.show("save");
					saveDetails();
			
				}
		}
		}

	
	
	
	
	
	private void saverotoArmListDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		RotoArmDM rotoarmobj = new RotoArmDM();
		if (tblArm.getValue() != null) {
			rotoarmobj = beanRotoArmDM.getItem(tblArm.getValue()).getBean();
			// armList.remove(rotoarmobj);
		}
		rotoarmobj.setShiftname(tfarmshiftname.getValue());
		rotoarmobj.setArmno(Long.valueOf(tfArmno.getValue()));
		// rotoarmobj.setWoid(Long.valueOf(cbarmWO.getValue().toString()));
		if (cbarmWO.getValue() != null) {
			rotoarmobj.setWoid(((RotoPlanArmDM) cbarmWO.getValue()).getWoId());
			rotoarmobj.setWorkOrdrNo(((RotoPlanArmDM) cbarmWO.getValue()).getWorkOrdrNo());
		}		
		System.out.println("rotoHdrobj.getRotoid()---->"+(id));		
		rotoarmobj.setRotoid(id);
		if (cbarmempname.getValue() != null) {
			rotoarmobj.setEmployeeid(((EmployeeDM) cbarmempname.getValue()).getEmployeeid());
			rotoarmobj.setEmpName(((EmployeeDM) cbarmempname.getValue()).getFullname());
		}
		/*
		 * for (MaterialReturnDtlDM saveDtl : (Collection<MaterialReturnDtlDM>) colPlanDtls) {
		 * saveDtl.setReturn_id((indentObj.getReturn_id())); count++; if (saveDtl.getStatus().equals("Returned")) {
		 * inc++; } serviceMaterialReturnDtl.savereturnDetails(saveDtl);
		 */
		@SuppressWarnings("unchecked")
		Collection<RotoArmDM> colPlanDtls = ((Collection<RotoArmDM>) tblArm.getVisibleItemIds());
		count = 1;
		System.out.println("(RotoPlanArmDM) cbArmproduct.getValue()-->"
				+ ((RotoPlanArmDM) cbArmproduct.getValue()).getProductId());
		System.out.println("Long.valueOf(tfCycleno.getValue())--" + Long.valueOf(tfCycleno.getValue()));
		for (RotoArmDM savecycle : (Collection<RotoArmDM>) colPlanDtls) {
			if (savecycle.getProductid().equals(((RotoPlanArmDM) cbArmproduct.getValue()).getProductId())
					&& (savecycle.getCycleno() != (Long.valueOf(tfCycleno.getValue())))) {
				count++;
				tfcyclecount.setValue(count + "");
				if (Long.valueOf(tfCycleno.getValue()).equals(Long.valueOf(tfcyclecount.getValue()))) {
					qty++;
				//	RotohdrDM rotohdr = new RotohdrDM();
					tfRotoHdrqty.setValue(qty + "");
	/*		   		rotohdr.setProdtntotqty(Long.valueOf(tfRotoHdrqty.getValue()));
					serviceRotohdr.saveRotohdr(rotohdr);*/
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
			// ArmList.add(rotoarmobj);
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
		beanClient.addAll(serviceClient.getClientDetails(companyid, null, null, null, null, null, null, null,
				"Active", "P"));
		cbClient.setContainerDataSource(beanClient);
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee name
	 */
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Employee Search...");
		BeanItemContainer<EmployeeDM> beanEmployeeDM = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", null, null, null,
				null, null, "P"));
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
	
	private void loadrotohdrlist() {
		List<RotoPlanHdrDM> PlanList = serviceRotoplanhdr.getRotoPlanHdrDetails(null, companyid, null, "Active");
		BeanItemContainer<RotoPlanHdrDM> beanrotoplanhdr = new BeanItemContainer<RotoPlanHdrDM>(RotoPlanHdrDM.class);
		beanrotoplanhdr.addAll(PlanList);
		Cbrotoplanid.setContainerDataSource(beanrotoplanhdr);
	}
	
	/*
	 * loadProductList()-->this function is used for load the product Name
	 */
	private void loadProductList() {
		List<WorkOrderDtlDM> getworkOrderDtl = new ArrayList<WorkOrderDtlDM>();
		Long workOrdHdrId = ((WorkOrderHdrDM) cbWO.getValue()).getWorkOrdrId();
		getworkOrderDtl.addAll(serviceWorkOrderDtl.getWorkOrderDtlList(null, workOrdHdrId, null, "F"));
		BeanItemContainer<WorkOrderDtlDM> beanPlnDtl = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
		beanPlnDtl.addAll(getworkOrderDtl);
		cbProduct.setContainerDataSource(beanPlnDtl);
	}
	
	// Load Product List
	public void loadProduct() {
		if (cbarmWO.getValue() != null) {
			List<RotoPlanArmDM> getworkOrderDtl = new ArrayList<RotoPlanArmDM>();
			getworkOrderDtl.addAll(serviceRotoplanarm.getRotoPlanArmList(null, null, null, null));
			BeanItemContainer<RotoPlanArmDM> beanPlnDtl = new BeanItemContainer<RotoPlanArmDM>(RotoPlanArmDM.class);
			beanPlnDtl.addAll(getworkOrderDtl);
			cbArmproduct.setContainerDataSource(beanPlnDtl);
			System.out.println("cbArmproduct---->" + cbArmproduct.getValue());
		}
	}
	
	/*
	 * loadWorkOrderNo()-->this function is used for load the workorderno
	 */
	private void loadWorkOrderNo() {
		Long clientId = (((ClientDM) cbClient.getValue()).getClientId());
		BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		beanWrkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, clientId, null, null, null, "F",null,null));
		cbWO.setContainerDataSource(beanWrkOrdHdr);
	}
	
	private void loadarmwororderno() {
		BeanItemContainer<RotoPlanArmDM> beanrotoplanarm = new BeanItemContainer<RotoPlanArmDM>(RotoPlanArmDM.class);
		beanrotoplanarm.addAll(serviceRotoplanarm.getRotoPlanArmList(null, null, null, null));
		cbarmWO.setContainerDataSource(beanrotoplanarm);
	}
	
	private void deleteShiftDetails() {
		RotoShiftDM removeShift = new RotoShiftDM();
		if (tblShift.getValue() != null) {
			removeShift = beanRotoShiftDM.getItem(tblShift.getValue()).getBean();
			ShiftList.remove(removeShift);
			rotoShiftResetFields();
			loadShiftRslt();
		}
	}
	/*
	 * private void deleteArmDetails() { RotoArmDM removeArm = new RotoArmDM(); if (tblArm.getValue() != null) {
	 * removeArm = beanRotoArmDM.getItem(tblArm.getValue()).getBean(); ArmList.remove(removeArm); rotoArmResetFields();
	 * loadArmRslt(); } }
	 */
	/*
	 * private void deleteDetails() { RotoDtlDM removeArm = new RotoDtlDM(); if (tblDtl.getValue() != null) { removeArm
	 * = beanRotoplanDtlDM.getItem(tblDtl.getValue()).getBean(); RotoplanDtlList.remove(removeArm);
	 * rotoDtlResetFields(); loadPlanDtlRslt(); } }
	 */
}

class CheckBoxColumnGenerator implements Table.ColumnGenerator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component generateCell(Table source, Object itemId, Object columnId) {
		// instead of this the prop will be null
		return new TextField("tfachdqty");
	}
}