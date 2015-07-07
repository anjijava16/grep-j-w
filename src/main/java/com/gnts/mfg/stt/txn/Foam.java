package com.gnts.mfg.stt.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.txn.WorkOrderDtlDM;
import com.gnts.mfg.service.txn.WorkOrderDtlService;
import com.gnts.stt.mfg.domain.txn.FoamDtlDM;
import com.gnts.stt.mfg.domain.txn.FoamHdrDM;
import com.gnts.stt.mfg.domain.txn.FoamPlanHdrDM;
import com.gnts.stt.mfg.domain.txn.FoamShiftDM;
import com.gnts.stt.mfg.service.txn.FoamDtlService;
import com.gnts.stt.mfg.service.txn.FoamHdrService;
import com.gnts.stt.mfg.service.txn.FoamPlanHdrService;
import com.gnts.stt.mfg.service.txn.FoamShiftService;
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
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Foam extends BaseTransUI {
	// Bean Creation
	private FoamHdrService serviceAsmblyPlanHrd = (FoamHdrService) SpringContextHelper.getBean("foamHdr");
	private FoamDtlService serviceFoamDtl = (FoamDtlService) SpringContextHelper.getBean("foamDtl");
	private FoamShiftService serviceFoamShift = (FoamShiftService) SpringContextHelper.getBean("foamShift");
	private WorkOrderDtlService serviceWorkOrderDtl = (WorkOrderDtlService) SpringContextHelper.getBean("workOrderDtl");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private FoamPlanHdrService serviceFoamHdr = (FoamPlanHdrService) SpringContextHelper.getBean("foamplanhdr");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private List<FoamDtlDM> listFoamDetails = null;
	private List<FoamShiftDM> listFoamShift = null;
	// form layout for input controls
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4, flDtlCol1, flDtlCol2, flDtlCol3, flDtlCol4,
			flDtlCol5, flDtlCol6, flDtlCol7, flAsmShiftCol1, flAsmShiftCol2, flAsmShiftCol3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlHdrslap = new HorizontalLayout();
	private HorizontalLayout hlShift = new HorizontalLayout();
	private HorizontalLayout hlHdrAndShift = new HorizontalLayout();
	private VerticalLayout vlHdr, vlHrdAndDtlAndShift, vlShift;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Work Order Details
	private Button btnAddDtls = new GERPButton("Add", "add", this);
	private Button btnAddShift = new GERPButton("Add", "add", this);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private Button btnShiftdelete = new GERPButton("Delete", "delete", this);
	private ComboBox cbFoamPlanNo, cbStatus, cbDtlStatus, cbEmpName, cbProduct;
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private TextField tfFoamRefNo, tfProductnQty, tfShiftName, tfTargetQty, tfPlanDtlQty, tfPlanRefNo;
	private DateField dfFoanDt;
	private TextArea taRemark;
	private Table tblFoamDetail, tblFoamShift;
	private BeanItemContainer<FoamHdrDM> beanFoamHdrDM = null;
	private BeanItemContainer<FoamDtlDM> beanFoamDtlDM = null;
	private BeanItemContainer<FoamShiftDM> beanFoamShiftDM = null;
	// local variables declaration
	private Long companyid, moduleId, branchId;
	private Long asmbPlnHdrId;
	private int recordCnt = 0;
	private int recordShiftCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(Foam.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor received the parameters from Login UI class
	public Foam() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Foam() constructor");
		// Loading the UI
		buildView();
	}
	
	private void buildView() {
		// Initialization for work order Details user input components
		btnAddDtls.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDtlDetails()) {
					saveFoamDetails();
				}
			}
		});
		btnAddShift.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateShiftDetails()) {
					saveFoamShiftDetails();
				}
			}
		});
		tfPlanRefNo = new TextField("Plan Ref.No");
		tblFoamDetail = new Table();
		tblFoamDetail.setWidth("800px");
		tblFoamDetail.setPageLength(4);
		tblFoamDetail.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblFoamDetail.isSelected(event.getItemId())) {
					tblFoamDetail.setImmediate(true);
					btnAddDtls.setCaption("Add");
					btnAddDtls.setStyleName("savebt");
					asmblDtlResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtls.setCaption("Update");
					btnAddDtls.setStyleName("savebt");
					editAsmbPlanDtls();
				}
			}
		});
		tblFoamShift = new Table();
		tblFoamShift.setPageLength(4);
		tblFoamShift.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblFoamShift.isSelected(event.getItemId())) {
					tblFoamShift.setImmediate(true);
					btnAddShift.setCaption("Add");
					btnAddShift.setStyleName("savebt");
					asmblShiftResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddShift.setCaption("Update");
					btnAddShift.setStyleName("savebt");
					editAsmbPlanShift();
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
				tfFoamRefNo.setReadOnly(false);
			}
		});
		btnShiftdelete.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnShiftdelete == event.getButton()) {
					deleteShiftDetails();
					btnAddShift.setCaption("Add");
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
					btnAddDtls.setCaption("Add");
				}
			}
		});
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Foam UI");
		// Plan Ref.No text field
		tfFoamRefNo = new GERPTextField("Plan Ref.No");
		// Branch Combo Box
		cbFoamPlanNo = new GERPComboBox("Foam Plan No.");
		cbFoamPlanNo.setItemCaptionPropertyId("formplanreffno");
		loadFoamList();
		// Assembly Plan Datefield
		dfFoanDt = new PopupDateField("Foam Date");
		dfFoanDt.setWidth("130px");
		// Plan Hdr Qty.Text field
		tfProductnQty = new GERPTextField("Product Return Qty");
		tfProductnQty.setValue("0");
		// Remarks TextArea
		taRemark = new TextArea("Remarks");
		taRemark.setHeight("71px");
		taRemark.setWidth("150px");
		// Status ComboBox
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Shift Name TextField
		tfShiftName = new GERPTextField("Shift Name");
		// Employee Name combobox
		cbEmpName = new GERPComboBox("Employee Name");
		cbEmpName.setItemCaptionPropertyId("fullname");
		loadEmployeeList();
		// TargetQty TextField
		tfTargetQty = new GERPTextField("Achieved Qty");
		tfTargetQty.setWidth("120");
		tfTargetQty.setValue("0");
		// Client Id ComboBox
		// Product Name ComboBox
		cbProduct = new GERPComboBox("Prod.Name");
		cbProduct.setWidth("130");
		cbProduct.setItemCaptionPropertyId("prodName");
		loadProductList();
		// Plan Qty. Textfield
		tfPlanDtlQty = new GERPTextField("Plan Qty.");
		tfPlanDtlQty.setValue("0");
		tfPlanDtlQty.setWidth("100px");
		// Status ComboBox
		cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		loadAsmbDtlList();
		loadShiftRslt();
		btnAddDtls.setStyleName("add");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		tfFoamRefNo.setReadOnly(false);
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		Label lbl = new Label();
		flHdrCol1.addComponent(dfFoanDt);
		flHdrCol2.addComponent(lbl);
		flHdrCol3.addComponent(cbHdrStatus);
		flHdrCol4.addComponent(tfPlanRefNo);
		hlSearchLayout.addComponent(flHdrCol4);
		hlSearchLayout.addComponent(flHdrCol1);
		hlSearchLayout.addComponent(flHdrCol2);
		hlSearchLayout.addComponent(flHdrCol3);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol1.addComponent(cbFoamPlanNo);
		flHdrCol1.addComponent(tfFoamRefNo);
		flHdrCol1.addComponent(dfFoanDt);
		flHdrCol1.addComponent(tfProductnQty);
		flHdrCol1.addComponent(taRemark);
		flHdrCol1.addComponent(cbHdrStatus);
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdrCol1);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding Shift Components
		flAsmShiftCol1 = new FormLayout();
		flAsmShiftCol2 = new FormLayout();
		flAsmShiftCol3 = new FormLayout();
		flAsmShiftCol1.addComponent(tfShiftName);
		flAsmShiftCol1.addComponent(cbEmpName);
		flAsmShiftCol2.addComponent(tfTargetQty);
		flAsmShiftCol2.addComponent(cbStatus);
		flAsmShiftCol3.addComponent(btnAddShift);
		flAsmShiftCol3.addComponent(btnShiftdelete);
		flAsmShiftCol3.setComponentAlignment(btnAddShift, Alignment.BOTTOM_CENTER);
		vlShift = new VerticalLayout();
		hlShift = new HorizontalLayout();
		hlShift.addComponent(flAsmShiftCol1);
		hlShift.addComponent(flAsmShiftCol2);
		hlShift.addComponent(flAsmShiftCol3);
		hlShift.setSpacing(true);
		hlShift.setMargin(true);
		vlShift.addComponent(hlShift);
		vlShift.addComponent(tblFoamShift);
		vlShift.setWidth("915px");
		// Adding FoamSlap components
		// Add components for User Input Layout
		flDtlCol1 = new FormLayout();
		flDtlCol2 = new FormLayout();
		flDtlCol3 = new FormLayout();
		flDtlCol4 = new FormLayout();
		flDtlCol5 = new FormLayout();
		flDtlCol6 = new FormLayout();
		flDtlCol7 = new FormLayout();
		flDtlCol3.addComponent(cbProduct);
		flDtlCol4.addComponent(tfPlanDtlQty);
		flDtlCol5.addComponent(cbDtlStatus);
		flDtlCol6.addComponent(btnAddDtls);
		flDtlCol7.addComponent(btndelete);
		hlHdrslap = new HorizontalLayout();
		hlHdrslap.addComponent(flDtlCol1);
		hlHdrslap.addComponent(flDtlCol2);
		hlHdrslap.addComponent(flDtlCol3);
		hlHdrslap.addComponent(flDtlCol4);
		hlHdrslap.addComponent(flDtlCol5);
		hlHdrslap.addComponent(flDtlCol6);
		hlHdrslap.addComponent(flDtlCol7);
		hlHdrslap.setSpacing(true);
		hlHdrslap.setMargin(true);
		vlHdr = new VerticalLayout();
		vlHdr.addComponent(hlHdrslap);
		vlHdr.addComponent(tblFoamDetail);
		vlHdr.setSpacing(true);
		hlHdrAndShift = new HorizontalLayout();
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(hlHdr));
		hlHdrAndShift.addComponent(GERPPanelGenerator.createPanel(vlShift));
		vlHrdAndDtlAndShift = new VerticalLayout();
		vlHrdAndDtlAndShift.addComponent(hlHdrAndShift);
		vlHrdAndDtlAndShift.addComponent(GERPPanelGenerator.createPanel(vlHdr));
		vlHrdAndDtlAndShift.setSpacing(true);
		vlHrdAndDtlAndShift.setWidth("100%");
		hlUserInputLayout.addComponent(vlHrdAndDtlAndShift);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<FoamHdrDM> listFoam = new ArrayList<FoamHdrDM>();
		Long foamPlanId = null;
		if (cbFoamPlanNo.getValue() != null) {
			foamPlanId = ((Long.valueOf(cbFoamPlanNo.getValue().toString())));
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfFoamRefNo.getValue() + ", " + cbHdrStatus.getValue());
		listFoam = serviceAsmblyPlanHrd.getFormHdrDetails(null, foamPlanId, (String) tfPlanRefNo.getValue(),
				dfFoanDt.getValue(), (String) cbHdrStatus.getValue(), "F");
		recordCnt = listFoam.size();
		beanFoamHdrDM = new BeanItemContainer<FoamHdrDM>(FoamHdrDM.class);
		beanFoamHdrDM.addAll(listFoam);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Foam. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanFoamHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "foamid", "formrefno", "fomdate", "fomstatus",
				"lastupdateddate", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Plan Ref No", "Assembly Plan Date", "Status",
				"Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("foamid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadAsmbDtlList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		logger.info("Company ID : " + companyid + " | saveasmblPlnDtlListDetails User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + ", " + tfProductnQty.getValue()
				+ (String) cbStatus.getValue() + ", " + asmbPlnHdrId);
		recordCnt = listFoamDetails.size();
		beanFoamDtlDM = new BeanItemContainer<FoamDtlDM>(FoamDtlDM.class);
		beanFoamDtlDM.addAll(listFoamDetails);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Foamslap. result set");
		tblFoamDetail.setContainerDataSource(beanFoamDtlDM);
		tblFoamDetail.setVisibleColumns(new Object[] { "prodname", "productQty", "status", "lastupdateddate",
				"lastupdatedby" });
		tblFoamDetail.setColumnHeaders(new String[] { "Product Name", "Planned Qty.", "Status", "Last Updated Date",
				"Last Updated By" });
		tblFoamDetail.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadShiftRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		recordShiftCnt = listFoamShift.size();
		beanFoamShiftDM = new BeanItemContainer<FoamShiftDM>(FoamShiftDM.class);
		beanFoamShiftDM.addAll(listFoamShift);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Foam. result set");
		tblFoamShift.setContainerDataSource(beanFoamShiftDM);
		tblFoamShift.setVisibleColumns(new Object[] { "shiftName", "empName", "achivedQty", "status", "lastupdateddate",
				"lastupdatedby" });
		tblFoamShift.setColumnHeaders(new String[] { "Shift Name", "Emp.Name", "Achieved Qty.", "Status",
				"Last Updated Dt", "Last Updated By" });
		tblFoamShift.setColumnFooter("lastupdatedby", "No.of Records : " + recordShiftCnt);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		// Assembly Plan Hdr Resetfields
		tfPlanRefNo.setValue("");
		cbFoamPlanNo.setValue(null);
		tfFoamRefNo.setReadOnly(false);
		tfFoamRefNo.setValue("");
		tfFoamRefNo.setReadOnly(true);
		tfFoamRefNo.setComponentError(null);
		dfFoanDt.setValue(null);
		tfProductnQty.setValue("0");
		taRemark.setValue("");
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		// Assembly Plan shift resetfields
		tfShiftName.setValue("");
		cbEmpName.setValue(null);
		tfTargetQty.setValue("0");
		cbDtlStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		cbFoamPlanNo.setComponentError(null);
		tfProductnQty.setComponentError(null);
		dfFoanDt.setComponentError(null);
		tfShiftName.setComponentError(null);
		tfTargetQty.setComponentError(null);
		cbEmpName.setComponentError(null);
		// Assembly Plan Dtls resetfields
		cbProduct.setValue(null);
		tfPlanDtlQty.setValue("0");
		cbStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		cbProduct.setComponentError(null);
		tfPlanDtlQty.setComponentError(null);
		listFoamDetails = new ArrayList<FoamDtlDM>();
		listFoamShift = new ArrayList<FoamShiftDM>();
		tblFoamDetail.removeAllItems();
		tblFoamShift.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editFoamHdrDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			FoamHdrDM editFoam = beanFoamHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			asmbPlnHdrId = Long.valueOf(editFoam.getFoamid());
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected Foam. Id -> "
					+ asmbPlnHdrId);
			tfFoamRefNo.setReadOnly(false);
			tfFoamRefNo.setValue(editFoam.getFormrefno());
			tfFoamRefNo.setReadOnly(true);
			if (editFoam.getFomdate() != null) {
				dfFoanDt.setValue(editFoam.getFomdate1());
			}
			tfProductnQty.setValue(editFoam.getProdtntotqty().toString());
			if (editFoam.getRemarks() != null) {
				taRemark.setValue(editFoam.getRemarks());
			}
			cbHdrStatus.setValue(editFoam.getFomstatus());
			listFoamDetails.addAll(serviceFoamDtl.getFormDetails(null, asmbPlnHdrId, null,
					(String) cbStatus.getValue(), "F"));
			listFoamShift.addAll(serviceFoamShift.getFormShiftDetails(null, asmbPlnHdrId, null, null,
					(String) cbStatus.getValue(), "F"));
			cbFoamPlanNo.setValue(editFoam.getFoamplanid().toString());
		}
		loadAsmbDtlList();
		loadShiftRslt();
	}
	
	private void editAsmbPlanDtls() {
		hlUserInputLayout.setVisible(true);
		if (tblFoamDetail.getValue() != null) {
			FoamDtlDM foamDtlDM = new FoamDtlDM();
			foamDtlDM = beanFoamDtlDM.getItem(tblFoamDetail.getValue()).getBean();
			Long prodId = foamDtlDM.getProductId();
			Collection<?> prodIdCol = cbProduct.getItemIds();
			for (Iterator<?> iterator = prodIdCol.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
				// Get the actual bean and use the data
				WorkOrderDtlDM st = (WorkOrderDtlDM) item.getBean();
				if (prodId != null && prodId.equals(st.getProdId())) {
					cbProduct.setValue(itemId);
				}
			}
			if (foamDtlDM.getProductQty() != null) {
				tfPlanDtlQty.setValue(foamDtlDM.getProductQty().toString());
			}
			if (foamDtlDM.getStatus() != null) {
				cbDtlStatus.setValue(foamDtlDM.getStatus());
			}
		}
	}
	
	private void editAsmbPlanShift() {
		hlUserInputLayout.setVisible(true);
		if (tblFoamShift.getValue() != null) {
			FoamShiftDM foamShiftDM = new FoamShiftDM();
			foamShiftDM = beanFoamShiftDM.getItem(tblFoamShift.getValue()).getBean();
			Long empId = foamShiftDM.getEmpId();
			Collection<?> empColId = cbEmpName.getItemIds();
			for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbEmpName.getItem(itemIdClient);
				// Get the actual bean and use the data
				EmployeeDM empObj = (EmployeeDM) itemclient.getBean();
				if (empId != null && empId.equals(empObj.getEmployeeid())) {
					cbEmpName.setValue(itemIdClient);
				}
			}
			if (foamShiftDM.getShiftName() != null) {
				tfShiftName.setValue(foamShiftDM.getShiftName());
			}
			if (foamShiftDM.getAchivedQty() != null) {
				tfTargetQty.setValue(foamShiftDM.getAchivedQty().toString());
			}
			if (foamShiftDM.getStatus() != null) {
				cbStatus.setValue(foamShiftDM.getStatus());
			}
		}
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
		cbHdrStatus.setValue(cbStatus.getItemIds().iterator().next());
		dfFoanDt.setValue(null);
		tfPlanRefNo.setValue("");
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
		loadShiftRslt();
		loadAsmbDtlList();
		assembleInputUserLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		cbFoamPlanNo.setRequired(true);
		tfProductnQty.setRequired(true);
		dfFoanDt.setRequired(true);
		tfShiftName.setRequired(true);
		tfTargetQty.setRequired(true);
		cbEmpName.setRequired(true);
		cbProduct.setRequired(true);
		tfPlanDtlQty.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		resetFields();
		hlCmdBtnLayout.setVisible(false);
		btnAddDtls.setCaption("Add");
		tblFoamDetail.setVisible(true);
		try {
			tfFoamRefNo.setReadOnly(false);
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "STT_FOAMNO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfFoamRefNo.setValue(slnoObj.getKeyDesc());
				tfFoamRefNo.setReadOnly(true);
			} else {
				tfFoamRefNo.setReadOnly(false);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		tblFoamDetail.setVisible(true);
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Foam. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_MFG_STT_ASMBLYPLAN);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(asmbPlnHdrId));
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		cbFoamPlanNo.setComponentError(null);
		dfFoanDt.setComponentError(null);
		tfFoamRefNo.setComponentError(null);
		tfShiftName.setComponentError(null);
		tfTargetQty.setComponentError(null);
		cbEmpName.setComponentError(null);
		cbProduct.setComponentError(null);
		tfPlanDtlQty.setComponentError(null);
		tfProductnQty.setComponentError(null);
		cbFoamPlanNo.setRequired(false);
		tfProductnQty.setRequired(false);
		tfProductnQty.setRequired(false);
		dfFoanDt.setRequired(false);
		tfShiftName.setRequired(false);
		tfTargetQty.setRequired(false);
		cbEmpName.setRequired(false);
		cbProduct.setRequired(false);
		tfPlanDtlQty.setRequired(false);
		asmblDtlResetFields();
		asmblShiftResetFields();
		hlCmdBtnLayout.setVisible(true);
		tblFoamDetail.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		hlCmdBtnLayout.setVisible(false);
		cbFoamPlanNo.setRequired(true);
		tfProductnQty.setRequired(true);
		dfFoanDt.setRequired(true);
		tfShiftName.setRequired(true);
		tfTargetQty.setRequired(true);
		cbEmpName.setRequired(true);
		cbProduct.setRequired(true);
		tfPlanDtlQty.setRequired(true);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		if (tfFoamRefNo.getValue() == null || tfFoamRefNo.getValue().trim().length() == 0) {
			tfFoamRefNo.setReadOnly(false);
		}
		assembleInputUserLayout();
		resetFields();
		editFoamHdrDetails();
		editAsmbPlanDtls();
		editAsmbPlanShift();
	}
	
	private void asmblDtlResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbProduct.setValue(null);
		tfPlanDtlQty.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		cbProduct.setComponentError(null);
		tfPlanDtlQty.setComponentError(null);
	}
	
	private void asmblShiftResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEmpName.setValue(null);
		tfShiftName.setValue("");
		tfTargetQty.setValue("0");
		cbStatus.setValue(cbStatus.getItemIds().iterator().next());
		tfShiftName.setComponentError(null);
		tfTargetQty.setComponentError(null);
		cbEmpName.setComponentError(null);
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbFoamPlanNo.setComponentError(null);
		tfProductnQty.setComponentError(null);
		dfFoanDt.setComponentError(null);
		tfShiftName.setComponentError(null);
		tfTargetQty.setComponentError(null);
		cbEmpName.setComponentError(null);
		cbProduct.setComponentError(null);
		tfPlanDtlQty.setComponentError(null);
		errorFlag = false;
		if ((cbFoamPlanNo.getValue() == null)) {
			cbFoamPlanNo.setComponentError(new UserError(GERPErrorCodes.NULL_BRACH_NAME));
			errorFlag = true;
		}
		if ((dfFoanDt.getValue() == null)) {
			dfFoanDt.setComponentError(new UserError(GERPErrorCodes.NULL_ASMBL_PLAN_DT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfFoanDt.getValue());
			errorFlag = true;
		}
		Long prdctnQty;
		try {
			prdctnQty = Long.valueOf(tfProductnQty.getValue());
			if (prdctnQty < 0) {
				tfProductnQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				errorFlag = true;
			}
		}
		catch (Exception e) {
			tfProductnQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validateDtlDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbProduct.getValue() == null)) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbProduct.getValue());
			isValid = false;
		} else {
			cbProduct.setComponentError(null);
		}
		Long productQty;
		try {
			productQty = Long.valueOf(tfPlanDtlQty.getValue());
			if (productQty < 0) {
				tfPlanDtlQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfPlanDtlQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		return isValid;
	}
	
	private boolean validateShiftDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((tfShiftName.getValue() == "")) {
			tfShiftName.setComponentError(new UserError(GERPErrorCodes.NULL_SHIFT));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfShiftName.getValue());
			isValid = false;
		} else {
			tfShiftName.setComponentError(null);
		}
		if ((cbEmpName.getValue() == null)) {
			cbEmpName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbEmpName.getValue());
			isValid = false;
		} else {
			cbEmpName.setComponentError(null);
		}
		Long achievedQty;
		try {
			achievedQty = Long.valueOf(tfTargetQty.getValue());
			if (achievedQty < 0) {
				tfTargetQty.setComponentError(new UserError(GERPErrorCodes.LESS_THEN_ZERO));
				isValid = false;
			}
		}
		catch (Exception e) {
			tfTargetQty.setComponentError(new UserError(GERPErrorCodes.WORK_ORDER_DTL_QTY));
			isValid = false;
		}
		return isValid;
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			FoamHdrDM foamHdrDM = new FoamHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				foamHdrDM = beanFoamHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			foamHdrDM.setFormrefno(tfFoamRefNo.getValue());
			foamHdrDM.setFoamplanid((Long.valueOf(cbFoamPlanNo.getValue().toString())));
			foamHdrDM.setFomdate(dfFoanDt.getValue());
			foamHdrDM.setProdtntotqty(Long.valueOf(tfProductnQty.getValue()));
			foamHdrDM.setRemarks(taRemark.getValue());
			foamHdrDM.setFomstatus((String) cbStatus.getValue());
			foamHdrDM.setLastupdateddate(DateUtils.getcurrentdate());
			foamHdrDM.setLastupdatedby(username);
			serviceAsmblyPlanHrd.saveFormHdr(foamHdrDM);
			@SuppressWarnings("unchecked")
			Collection<FoamDtlDM> colPlanDtls = ((Collection<FoamDtlDM>) tblFoamDetail.getVisibleItemIds());
			for (FoamDtlDM save : (Collection<FoamDtlDM>) colPlanDtls) {
				save.setFoamId(Long.valueOf(foamHdrDM.getFoamid()));
				serviceFoamDtl.saveAndUpdate(save);
			}
			@SuppressWarnings("unchecked")
			Collection<FoamShiftDM> colAsmbShift = ((Collection<FoamShiftDM>) tblFoamShift.getVisibleItemIds());
			for (FoamShiftDM saveShift : (Collection<FoamShiftDM>) colAsmbShift) {
				saveShift.setFoamId(Long.valueOf(foamHdrDM.getFoamid()));
				serviceFoamShift.saveAndUpdate(saveShift);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "STT_FOAMNO")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "STT_FOAMNO");
					}
				}
				catch (Exception e) {
				}
			}
			asmblDtlResetFields();
			asmblShiftResetFields();
			resetFields();
			loadSrchRslt();
			asmbPlnHdrId = 0L;
			loadAsmbDtlList();
			loadShiftRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveFoamDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			FoamDtlDM foamDtlDM = new FoamDtlDM();
			if (tblFoamDetail.getValue() != null) {
				foamDtlDM = beanFoamDtlDM.getItem(tblFoamDetail.getValue()).getBean();
				listFoamDetails.remove(foamDtlDM);
			}
			foamDtlDM.setProductQty(Long.valueOf(tfPlanDtlQty.getValue()));
			if (cbProduct.getValue() != null) {
				foamDtlDM.setProductId(((WorkOrderDtlDM) cbProduct.getValue()).getProdId());
				foamDtlDM.setProdname(((WorkOrderDtlDM) cbProduct.getValue()).getProdName());
			}
			if (cbDtlStatus.getValue() != null) {
				foamDtlDM.setStatus((String) cbDtlStatus.getValue());
			}
			foamDtlDM.setLastupdateddate(DateUtils.getcurrentdate());
			foamDtlDM.setLastupdatedby(username);
			listFoamDetails.add(foamDtlDM);
			loadAsmbDtlList();
			btnAddDtls.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		asmblDtlResetFields();
	}
	
	private void saveFoamShiftDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			FoamShiftDM foamShiftDM = new FoamShiftDM();
			if (tblFoamShift.getValue() != null) {
				foamShiftDM = beanFoamShiftDM.getItem(tblFoamShift.getValue()).getBean();
				listFoamShift.remove(foamShiftDM);
			}
			foamShiftDM.setShiftName(tfShiftName.getValue());
			if (cbEmpName.getValue() != null) {
				foamShiftDM.setEmpId(((EmployeeDM) cbEmpName.getValue()).getEmployeeid());
				foamShiftDM.setEmpName(((EmployeeDM) cbEmpName.getValue()).getFirstname());
			}
			foamShiftDM.setAchivedQty(Long.valueOf(tfTargetQty.getValue()));
			if (cbStatus.getValue() != null) {
				foamShiftDM.setStatus((String) cbStatus.getValue());
			}
			foamShiftDM.setLastupdateddate(DateUtils.getcurrentdate());
			foamShiftDM.setLastupdatedby(username);
			listFoamShift.add(foamShiftDM);
			loadShiftRslt();
			asmbPlnHdrId = 0L;
			btnAddShift.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		asmblShiftResetFields();
	}
	
	/*
	 * loadFoamList()-->this function is used for load the branch name
	 */
	private void loadFoamList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, FoamPlanHdrDM> beanFoamPlanHdrDM = new BeanContainer<Long, FoamPlanHdrDM>(
				FoamPlanHdrDM.class);
		beanFoamPlanHdrDM.setBeanIdProperty("formplanid");
		beanFoamPlanHdrDM.addAll(serviceFoamHdr.getFormPlanHdrDetails(null, null, companyid, null, null));
		cbFoamPlanNo.setContainerDataSource(beanFoamPlanHdrDM);
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee name
	 */
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Employee Search...");
		BeanItemContainer<EmployeeDM> beanEmployeeDM = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployeeDM.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", null, null, null, null, null,
				"P"));
		cbEmpName.setContainerDataSource(beanEmployeeDM);
	}
	
	/*
	 * loadProductList()-->this function is used for load the product Name
	 */
	private void loadProductList() {
		BeanItemContainer<WorkOrderDtlDM> beanPlnDtl = new BeanItemContainer<WorkOrderDtlDM>(WorkOrderDtlDM.class);
		beanPlnDtl.addAll(serviceWorkOrderDtl.getWorkOrderDtlList(null, null, null, "F"));
		cbProduct.setContainerDataSource(beanPlnDtl);
	}
	
	private void deleteShiftDetails() {
		FoamShiftDM removeShift = new FoamShiftDM();
		if (tblFoamShift.getValue() != null) {
			removeShift = beanFoamShiftDM.getItem(tblFoamShift.getValue()).getBean();
			listFoamShift.remove(removeShift);
			asmblShiftResetFields();
			loadShiftRslt();
		}
	}
	
	private void deleteDetails() {
		FoamDtlDM remove = new FoamDtlDM();
		if (tblFoamDetail.getValue() != null) {
			remove = beanFoamDtlDM.getItem(tblFoamDetail.getValue()).getBean();
			listFoamDetails.remove(remove);
			asmblDtlResetFields();
			loadAsmbDtlList();
		}
	}
	
	@Override
	protected void printDetails() {
		// TODO Auto-generated method stub
	}
}
