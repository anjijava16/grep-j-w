package com.gnts.mfg.stt.txn;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.asm.domain.txn.AssetDetailsDM;
import com.gnts.asm.service.txn.AssetDetailsService;
import com.gnts.base.domain.mst.ApprovalSchemaDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPTable;
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
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.mms.domain.mst.MaterialDM;
import com.gnts.mms.domain.txn.MaterialStockDM;
import com.gnts.mms.service.mst.MaterialService;
import com.gnts.mms.service.txn.MaterialStockService;
import com.gnts.stt.mfg.domain.txn.ExtrudersDtlDM;
import com.gnts.stt.mfg.domain.txn.ExtrudersHdrDM;
import com.gnts.stt.mfg.domain.txn.ExtrudersMtrlDM;
import com.gnts.stt.mfg.domain.txn.ExtrudersTempDM;
import com.gnts.stt.mfg.service.txn.ExtrudersDtlService;
import com.gnts.stt.mfg.service.txn.ExtrudersHdrService;
import com.gnts.stt.mfg.service.txn.ExtrudersMtrlService;
import com.gnts.stt.mfg.service.txn.ExtrudersTempService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
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

public class Extruder extends BaseTransUI {
	// Bean Creation
	private ExtrudersHdrService serviceExtruderHrd = (ExtrudersHdrService) SpringContextHelper.getBean("extruderHdr");
	private ExtrudersDtlService serviceExtruderDtl = (ExtrudersDtlService) SpringContextHelper.getBean("extruderDtl");
	private ExtrudersMtrlService serviceExtruderMtrl = (ExtrudersMtrlService) SpringContextHelper
			.getBean("extruderMtrl");
	private ExtrudersTempService serviceExtruderTemp = (ExtrudersTempService) SpringContextHelper
			.getBean("extruderTemp");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private AssetDetailsService serviceAssetDetail = (com.gnts.asm.service.txn.AssetDetailsService) SpringContextHelper
			.getBean("assetDetails");
	private MaterialService serviceMaterial = (MaterialService) SpringContextHelper.getBean("material");
	private MaterialStockService serviceMaterialStock = (MaterialStockService) SpringContextHelper
			.getBean("materialstock");
	private List<ExtrudersDtlDM> extrudersDtlList = new ArrayList<ExtrudersDtlDM>();
	private List<ExtrudersMtrlDM> extrudersMtrlList = null;
	private List<ExtrudersTempDM> extrudersTempList = null;
	// form layout for input controls
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4, flHdrCol6, flMtrlCol1, flTempCol1, flExtDtlCol1,
			flExtDtlCol2;
	private WorkOrderHdrService serviceWrkOrdHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlExtDtl = new HorizontalLayout();
	private HorizontalLayout hlMtrl = new HorizontalLayout();
	private HorizontalLayout hlDtlAdMtrlAdTemp = new HorizontalLayout();
	private VerticalLayout vlHdrToTemp, vlMtrl, vlDtl, vlTemp;
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components for Work Order Details
	private Button btnAddDtls = new GERPButton("Add", "addbt", this);
	private Button btnAddMtrl = new GERPButton("Add", "addbt", this);
	private Button btnAddTemp = new GERPButton("Add", "addbt", this);
	private ComboBox cbMtrlStatus, cbDtlStatus, cbTempStatus, cbMaterial, cbLotno;
	private ComboBox cbMachineName, cbMatName, cbStockType;
	private TextField tfExtRefNo, tfGradeNo, tfLotNo, tfExTPlnRef, tfOpQty, tfOeePerc, tfMtrlQty, tfZoneName,
			tfTempValue;
	private PopupDateField dfExtDt, dfProdDt;
	private GERPTimeField tiHeatngTime, tiChrgStTm, tiChargEdTm;
	private TextArea taInstruct, taRemark;
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private Table tblDtl, tblMtrl, tblTemp;
	private BeanItemContainer<ExtrudersHdrDM> beanExtrudersHdrDM = null;
	private BeanItemContainer<ExtrudersDtlDM> beanExtrudersDtlDM = null;
	private BeanItemContainer<ExtrudersMtrlDM> beanExtrudersMtrlDM = null;
	private BeanItemContainer<ExtrudersTempDM> beanExtrudersTempDM = null;
	// local variables declaration
	private Long companyid, moduleId, branchID, employeeId;
	private Long extHdrId;
	private int recordDtlCnt = 0;
	private int recordCnt = 0;
	private int recordMtrlCnt = 0;
	private int recordTempCnt = 0;
	private String username;
	// Initialize logger
	private Logger logger = Logger.getLogger(Extruder.class);
	private Long appScreenId;
	private Long roleId;
	private String name;
	private static final long serialVersionUID = 1L;
	private Button btnDeletedtl = new GERPButton("Delete", "delete", this);
	private Button btnDeleteMtrl = new GERPButton("Delete", "delete", this);
	private Button btnDeleteZone = new GERPButton("Delete", "delete", this);
	
	// Constructor received the parameters from Login UI class
	public Extruder() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		employeeId = (Long) UI.getCurrent().getSession().getAttribute("employeeId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Extruder() constructor");
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
					saveExtDtl();
				}
			}
		});
		tfExTPlnRef = new TextField("Extruder Ref.No.");
		btnAddMtrl.setEnabled(false);
		btnAddMtrl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateMtrlDetails()) {
					saveExtMtrlDetails();
				}
			}
		});
		btnAddTemp.setEnabled(false);
		btnAddTemp.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateTempDetails()) {
					saveasmblPlnTempListDetails();
				}
			}
		});
		tblDtl = new GERPTable();
		tblDtl.setPageLength(4);
		tblDtl.setVisible(true);
		tblDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblDtl.isSelected(event.getItemId())) {
					tblDtl.setImmediate(true);
					btnAddDtls.setCaption("Add");
					btnAddDtls.setStyleName("savebt");
					asmblDtlResetFields();
					btnAddMtrl.setEnabled(false);
					btnAddTemp.setEnabled(false);
					btnDeletedtl.setEnabled(false);
					SetRequiredfalse();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtls.setCaption("Update");
					btnAddDtls.setStyleName("savebt");
					editDtls();
					btnAddMtrl.setEnabled(true);
					btnAddTemp.setEnabled(true);
					btnDeletedtl.setEnabled(true);
					SetRequiredtrue();
				}
			}
		});
		tblMtrl = new GERPTable();
		tblMtrl.setPageLength(4);
		tblMtrl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblMtrl.isSelected(event.getItemId())) {
					tblMtrl.setImmediate(true);
					btnAddMtrl.setCaption("Add");
					btnAddMtrl.setStyleName("savebt");
					extMtrlResetFields();
					btnDeleteMtrl.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddMtrl.setCaption("Update");
					btnAddMtrl.setStyleName("savebt");
					editMtrl();
					btnDeleteMtrl.setEnabled(true);
				}
			}
		});
		tblTemp = new GERPTable();
		tblTemp.setPageLength(4);
		tblTemp.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblTemp.isSelected(event.getItemId())) {
					tblTemp.setImmediate(true);
					btnAddTemp.setCaption("Add");
					btnAddTemp.setStyleName("savebt");
					asmblTempResetFields();
					btnDeleteZone.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddTemp.setCaption("Update");
					btnAddTemp.setStyleName("savebt");
					editTemp();
					btnDeleteZone.setEnabled(true);
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
		// Initializing Hdr Detail
		cbMachineName = new GERPComboBox("Machine Name");
		cbMachineName.setItemCaptionPropertyId("assetName");
		loadMachineList();
		cbMaterial = new GERPComboBox("O/P Material Name");
		cbMaterial.setItemCaptionPropertyId("materialName");
		cbMaterial.setWidth("140");
		loadOPMaterialList();
		tfExtRefNo = new GERPTextField("Extruder Ref.No");
		dfExtDt = new GERPPopupDateField("Extruder Date");
		dfExtDt.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				dfProdDt.setValue(dfExtDt.getValue());
			}
		});
		tfGradeNo = new GERPTextField("Grade No.");
		tfGradeNo.setWidth("140");
		tfLotNo = new GERPTextField("Lot No.");
		tfLotNo.setWidth("140");
		tiHeatngTime = new GERPTimeField("Heating Time");
		taInstruct = new TextArea("Instruction");
		taInstruct.setHeight("78");
		taInstruct.setNullRepresentation("");
		// taInstruct.setHeight("103px");
		// Initializing Dtl
		dfProdDt = new GERPPopupDateField("Production Date");
		tfOpQty = new GERPTextField("OutPut Qty.");
		tfOpQty.setWidth("130");
		tiChrgStTm = new GERPTimeField("Charge St.Time");
		tiChargEdTm = new GERPTimeField("Charge Ed.Time");
		tfOeePerc = new GERPTextField("OEE Percent");
		tfOeePerc.setWidth("150");
		tfOeePerc.setValue("0");
		taRemark = new TextArea("Instruction");
		taRemark.setWidth("155px");
		taRemark.setHeight("");
		taRemark.setNullRepresentation("");
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.T_FMS_ACCOUNT_TXNS, BASEConstants.TXN_STATUS);
		// Initializing Material Details
		cbMatName = new GERPComboBox("Material Name");
		cbLotno = new GERPComboBox("Lot No.");
		cbLotno.setItemCaptionPropertyId("lotNo");
		cbLotno.setWidth("140");
		cbMatName.setWidth("140");
		cbMatName.setItemCaptionPropertyId("materialName");
		loadMaterialList();
		cbMatName.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbMatName.getValue() != null) {
					loadLotNumber();
				}
			}
		});
		cbStockType = new GERPComboBox("Stock Type");
		cbStockType.setWidth("140");
		cbStockType.addItem("New");
		cbStockType.addItem("Scrap");
		cbStockType.addItem("Refurbish");
		tfMtrlQty = new GERPTextField("Material Qty");
		tfMtrlQty.setWidth("140");
		cbMtrlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		// Initializing Temperature Details
		tfZoneName = new GERPTextField("Zone Name");
		tfZoneName.setWidth("150");
		tfTempValue = new GERPTextField("Temprature Value");
		tfTempValue.setWidth("150");
		cbTempStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbTempStatus.setWidth("135");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Extruder UI");
		try {
			ApprovalSchemaDM obj = serviceWrkOrdHdr.getReviewerId(companyid, appScreenId, branchID, roleId).get(0);
			name = obj.getApprLevel();
			if (name.equals("Reviewer")) {
				cbDtlStatus = new GERPComboBox("Status", BASEConstants.T_STT_MFG_EXTRUD_DTL,
						BASEConstants.EXTRUD_DTL_STS);
			} else {
				cbDtlStatus = new GERPComboBox("Status", BASEConstants.T_STT_MFG_EXTRUD_DTL,
						BASEConstants.EXTRUD_DTL_AP_STS);
			}
		}
		catch (Exception e) {
		}
		btnDeletedtl.setEnabled(false);
		btnDeletedtl.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteDtlDetails();
			}
		});
		btnDeleteMtrl.setEnabled(false);
		btnDeleteMtrl.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteMtrlDetails();
			}
		});
		btnDeleteZone.setEnabled(false);
		btnDeleteZone.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteZoneDetails();
			}
		});
		cbDtlStatus.setValue(cbDtlStatus.getItemIds().iterator().next());
		tiHeatngTime.setRequired(true);
		tiChrgStTm.setRequired(true);
		tiChargEdTm.setRequired(true);
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		btnAddDtls.setStyleName("add");
		btnAddMtrl.setStyleName("add");
		btnAddTemp.setStyleName("add");
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		flHdrCol6 = new FormLayout();
		Label lbl = new Label();
		flHdrCol6.addComponent(tfExTPlnRef);
		flHdrCol1.addComponent(cbMachineName);
		flHdrCol2.addComponent(dfExtDt);
		flHdrCol3.addComponent(lbl);
		flHdrCol4.addComponent(cbHdrStatus);
		hlSearchLayout.addComponent(flHdrCol6);
		hlSearchLayout.addComponent(flHdrCol1);
		hlSearchLayout.addComponent(flHdrCol2);
		hlSearchLayout.addComponent(flHdrCol3);
		hlSearchLayout.addComponent(flHdrCol4);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleInputUserLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		// Adding Hdr Components
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		new FormLayout();
		flHdrCol1.addComponent(cbMachineName);
		flHdrCol1.addComponent(tfExtRefNo);
		flHdrCol1.addComponent(dfExtDt);
		flHdrCol2.addComponent(tfGradeNo);
		flHdrCol2.addComponent(tfLotNo);
		flHdrCol2.addComponent(tiHeatngTime);
		flHdrCol3.addComponent(taInstruct);
		flHdrCol4.addComponent(cbMaterial);
		flHdrCol4.addComponent(cbHdrStatus);
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdrCol1);
		hlHdr.addComponent(flHdrCol2);
		hlHdr.addComponent(flHdrCol3);
		hlHdr.addComponent(flHdrCol4);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding Dtl Components
		flExtDtlCol1 = new FormLayout();
		flExtDtlCol2 = new FormLayout();
		//flExtDtlCol1.addComponent(dfProdDt);
		flExtDtlCol1.addComponent(tfOpQty);
		flExtDtlCol1.addComponent(tiChrgStTm);
		flExtDtlCol1.addComponent(tiChargEdTm);
		flExtDtlCol1.addComponent(tfOeePerc);
		flExtDtlCol2.addComponent(taRemark);
		flExtDtlCol2.addComponent(cbDtlStatus);
		HorizontalLayout hlDtl = new HorizontalLayout();
		hlDtl.addComponent(btnAddDtls);
		hlDtl.addComponent(btnDeletedtl);
		flExtDtlCol2.addComponent(hlDtl);
		flExtDtlCol2.setComponentAlignment(hlDtl, Alignment.BOTTOM_CENTER);
		vlDtl = new VerticalLayout();
		hlExtDtl = new HorizontalLayout();
		hlExtDtl.addComponent(flExtDtlCol1);
		hlExtDtl.addComponent(flExtDtlCol2);
		hlExtDtl.setSpacing(true);
		vlDtl.addComponent(hlExtDtl);
		vlDtl.addComponent(tblDtl);
		vlDtl.setMargin(true);
		// Adding Material Components
		flMtrlCol1 = new FormLayout();
		new FormLayout();
		Label lblSpc1 = new Label();
		Label lblSpc2 = new Label();
		lblSpc1.setHeight("17");
		lblSpc2.setHeight("20");
		flMtrlCol1.addComponent(cbMatName);
		flMtrlCol1.addComponent(cbStockType);
		flMtrlCol1.addComponent(cbLotno);
		flMtrlCol1.addComponent(tfMtrlQty);
		HorizontalLayout hlMtrlbtn = new HorizontalLayout();
		hlMtrlbtn.addComponent(btnAddMtrl);
		hlMtrlbtn.addComponent(btnDeleteMtrl);
		//
		hlMtrl = new HorizontalLayout();
		hlMtrl.addComponent(flMtrlCol1);
		hlMtrl.setMargin(new MarginInfo(false, false, false, true));
		Label lblSpc = new Label();
		lblSpc.setHeight("54");
		//
		vlMtrl = new VerticalLayout();
		vlMtrl.addComponent(hlMtrl);
		vlMtrl.addComponent(lblSpc1);
		vlMtrl.addComponent(hlMtrlbtn);
		vlMtrl.setComponentAlignment(hlMtrlbtn, Alignment.TOP_RIGHT);
		vlMtrl.addComponent(lblSpc2);
		vlMtrl.addComponent(tblMtrl);
		// Adding Temp Components
		flTempCol1 = new FormLayout();
		flTempCol1.addComponent(tfZoneName);
		flTempCol1.addComponent(tfTempValue);
		HorizontalLayout hlTemp = new HorizontalLayout();
		hlTemp.addComponent(btnAddTemp);
		hlTemp.addComponent(btnDeleteZone);
		flTempCol1.addComponent(lblSpc);
		flTempCol1.addComponent(hlTemp);
		flTempCol1.setComponentAlignment(hlTemp, Alignment.MIDDLE_RIGHT);
		//
		vlTemp = new VerticalLayout();
		hlTemp = new HorizontalLayout();
		hlTemp.addComponent(flTempCol1);
		hlTemp.setMargin(true);
		vlTemp.addComponent(hlTemp);
		vlTemp.addComponent(tblTemp);
		hlDtlAdMtrlAdTemp = new HorizontalLayout();
		hlDtlAdMtrlAdTemp.addComponent(GERPPanelGenerator.createPanel(vlDtl));
		hlDtlAdMtrlAdTemp.addComponent(GERPPanelGenerator.createPanel(vlMtrl));
		hlDtlAdMtrlAdTemp.addComponent(GERPPanelGenerator.createPanel(vlTemp));
		hlDtlAdMtrlAdTemp.setSpacing(true);
		// hlDtlAdMtrlAdTemp.setSpacing(true);
		vlHdrToTemp = new VerticalLayout();
		vlHdrToTemp.addComponent(GERPPanelGenerator.createPanel(hlHdr));
		vlHdrToTemp.addComponent(hlDtlAdMtrlAdTemp);
		vlHdrToTemp.setSpacing(true);
		hlUserInputLayout.addComponent(vlHdrToTemp);
		hlUserInputLayout.setWidth("100%");
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<ExtrudersHdrDM> extHdrList = new ArrayList<ExtrudersHdrDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfExtRefNo.getValue() + ", " + cbHdrStatus.getValue());
		if (cbMachineName.getValue() != null) {
		}
		extHdrList = serviceExtruderHrd.getExtruderList(null, companyid, null, (Long) cbMachineName.getValue(),
				(String) tfExTPlnRef.getValue(), dfExtDt.getValue(), null, null, (String) cbHdrStatus.getValue(), "F");
		recordCnt = extHdrList.size();
		beanExtrudersHdrDM = new BeanItemContainer<ExtrudersHdrDM>(ExtrudersHdrDM.class);
		beanExtrudersHdrDM.addAll(extHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the AssemblyPlan. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanExtrudersHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "extId", "machineName", "extRefNo", "extDate", "gradeNo",
				"lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Machine Name", "Extruder Ref No", "Extruder Date",
				"Grade No", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("extId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records:" + recordCnt);
	}
	
	private void loadAsmbDtlList() {
		tblDtl.setSizeFull();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		logger.info("Company ID : " + companyid + " | saveExtDtl User Name : " + username + " > "
				+ "Search Parameters are " + companyid + ", " + (String) cbMtrlStatus.getValue() + ", " + extHdrId);
		recordDtlCnt = extrudersDtlList.size();
		beanExtrudersDtlDM = new BeanItemContainer<ExtrudersDtlDM>(ExtrudersDtlDM.class);
		beanExtrudersDtlDM.addAll(extrudersDtlList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Extruderslap. result set");
		tblDtl.setContainerDataSource(beanExtrudersDtlDM);
		tblDtl.setVisibleColumns(new Object[] { "extDtlId", "prodDate", "outQty", "oeePrnct", "chrgStTime",
				"chrgEdTime" });
		tblDtl.setColumnWidth("extDtlId", 60);
		tblDtl.setColumnWidth("prodDate", 85);
		tblDtl.setColumnWidth("outQty", 60);
		tblDtl.setColumnWidth("oeePrnct", 60);
		tblDtl.setColumnWidth("chrgStTime", 70);
		tblDtl.setColumnWidth("chrgEdTime", 80);
		tblDtl.setColumnHeaders(new String[] { "Ref.Id", "Prodtn. Date", "O/P Qty.", "OEE %", "Start Time", "End Time" });
		tblDtl.setColumnFooter("chrgEdTime", "Records :" + recordDtlCnt);
	}
	
	private void loadMtrlRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		recordMtrlCnt = extrudersMtrlList.size();
		beanExtrudersMtrlDM = new BeanItemContainer<ExtrudersMtrlDM>(ExtrudersMtrlDM.class);
		beanExtrudersMtrlDM.addAll(extrudersMtrlList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Extruder. result set");
		tblMtrl.setContainerDataSource(beanExtrudersMtrlDM);
		tblMtrl.setVisibleColumns(new Object[] { "extDtlId", "materialName", "stockType" });
		tblMtrl.setColumnHeaders(new String[] { "Dtl.RefId", "Material Name", "Stock Type" });
		tblMtrl.setColumnAlignment("extDtlId", Align.RIGHT);
		tblMtrl.setColumnFooter("stockType", "Records :" + recordMtrlCnt);
	}
	
	private void loadTempRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		recordTempCnt = extrudersTempList.size();
		beanExtrudersTempDM = new BeanItemContainer<ExtrudersTempDM>(ExtrudersTempDM.class);
		beanExtrudersTempDM.addAll(extrudersTempList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Extruder. result set");
		tblTemp.setContainerDataSource(beanExtrudersTempDM);
		tblTemp.setVisibleColumns(new Object[] { "extDtlId", "zoneName", "temprValue" });
		tblTemp.setColumnHeaders(new String[] { "Dtl.RefId", "Zone Name", "Temp. Value" });
		tblTemp.setColumnAlignment("extDtlId", Align.RIGHT);
		tblTemp.setColumnFooter("temprValue", "Records :" + recordTempCnt);
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		// Extruders Hdr Resetfields
		cbMachineName.setValue(null);
		dfExtDt.setValue(null);
		tfExtRefNo.setReadOnly(false);
		tfExtRefNo.setValue("");
		tfExtRefNo.setReadOnly(true);
		tfExTPlnRef.setValue("");
		tfGradeNo.setValue("");
		tfLotNo.setReadOnly(false);
		tfLotNo.setValue("");
		tiHeatngTime.setValue(null);
		taInstruct.setValue(null);
		cbMaterial.setValue(null);
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		// Extruders Dtl Resetfields
		dfProdDt.setValue(null);
		tfOpQty.setValue("");
		tiChrgStTm.setValue(null);
		tiChargEdTm.setValue(null);
		tfOeePerc.setValue("");
		taRemark.setValue(null);
		cbDtlStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		// Extruders Mtrl Resetfields
		cbMatName.setValue(null);
		cbStockType.setValue(null);
		cbLotno.setValue(null);
		tfMtrlQty.setValue("");
		cbMtrlStatus.setValue(cbMtrlStatus.getItemIds().iterator().next());
		// Extruders Temperature Resetfields
		tfZoneName.setValue(null);
		tfTempValue.setValue(null);
		cbMachineName.setComponentError(null);
		dfExtDt.setComponentError(null);
		cbMaterial.setComponentError(null);
		cbTempStatus.setValue(cbTempStatus.getItemIds().iterator().next());
		// Initializing temporary List
		extrudersDtlList = new ArrayList<ExtrudersDtlDM>();
		extrudersMtrlList = new ArrayList<ExtrudersMtrlDM>();
		extrudersTempList = new ArrayList<ExtrudersTempDM>();
		tblDtl.removeAllItems();
		tblMtrl.removeAllItems();
		tblTemp.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editExtruderHdrDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			ExtrudersHdrDM extrudersHdrDM = beanExtrudersHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			extHdrId = Long.valueOf(extrudersHdrDM.getExtId());
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected Extruder. Id -> " + extHdrId);
			cbMachineName.setValue(extrudersHdrDM.getMachineId());
			tfExtRefNo.setReadOnly(false);
			tfExtRefNo.setValue(extrudersHdrDM.getExtRefNo());
			tfExtRefNo.setReadOnly(true);
			tfLotNo.setReadOnly(false);
			tfLotNo.setValue(extrudersHdrDM.getLotNo());
			tfLotNo.setReadOnly(true);
			dfExtDt.setValue(extrudersHdrDM.getExtDate1());
			tfGradeNo.setValue(extrudersHdrDM.getGradeNo());
			tiHeatngTime.setTime(extrudersHdrDM.getHeatingTime());
			taInstruct.setValue(extrudersHdrDM.getInstruction());
			cbMaterial.setValue(extrudersHdrDM.getOpMaterialId());
			cbHdrStatus.setValue(extrudersHdrDM.getStatus());
			extrudersDtlList = serviceExtruderDtl.getExtrudersDtlList(null, null, extHdrId, null, null, null, null,
					null, null, (String) cbDtlStatus.getValue(), "F");
		}
		loadAsmbDtlList();
	}
	
	private void editDtls() {
		hlUserInputLayout.setVisible(true);
		if (tblDtl.getValue() != null) {
			ExtrudersDtlDM extrudersDtlDM = new ExtrudersDtlDM();
			extrudersDtlDM = beanExtrudersDtlDM.getItem(tblDtl.getValue()).getBean();
			extrudersDtlDM.getExtDtlId();
			dfProdDt.setValue(extrudersDtlDM.getProdDateDT());
			if (extrudersDtlDM.getOutQty() != null) {
				tfOpQty.setValue(extrudersDtlDM.getOutQty().toString());
			}
			if (extrudersDtlDM.getChrgStTime() != null) {
				tiChrgStTm.setTime(extrudersDtlDM.getChrgStTime());
			}
			if (extrudersDtlDM.getChrgEdTime() != null) {
				tiChargEdTm.setTime(extrudersDtlDM.getChrgEdTime());
			}
			if (extrudersDtlDM.getOeePrnct() != null) {
				tfOeePerc.setValue(extrudersDtlDM.getOeePrnct().toString());
			}
			if (extrudersDtlDM.getRemarks() != null) {
				taRemark.setValue(extrudersDtlDM.getRemarks());
			}
			if (extrudersDtlDM.getStatus() != null) {
				cbDtlStatus.setValue(extrudersDtlDM.getStatus());
			}
			extrudersMtrlList = serviceExtruderMtrl.getExtMtrlList(null, extrudersDtlDM.getExtDtlId(), null, null,
					null, "Active", "F");
			extrudersTempList = serviceExtruderTemp.getExtTempDetails(null, extrudersDtlDM.getExtDtlId(), null, null,
					"Active", "F");
		}
		loadMtrlRslt();
		loadTempRslt();
	}
	
	private void editMtrl() {
		hlUserInputLayout.setVisible(true);
		if (tblMtrl.getValue() != null) {
			ExtrudersMtrlDM extrudersMtrlDM = new ExtrudersMtrlDM();
			extrudersMtrlDM = beanExtrudersMtrlDM.getItem(tblMtrl.getValue()).getBean();
			extrudersMtrlDM.getExtMtrlId();
			Long matId = extrudersMtrlDM.getMaterialId();
			Collection<?> empColId = cbMatName.getItemIds();
			for (Iterator<?> iteratorclient = empColId.iterator(); iteratorclient.hasNext();) {
				Object itemIdClient = (Object) iteratorclient.next();
				BeanItem<?> itemclient = (BeanItem<?>) cbMatName.getItem(itemIdClient);
				// Get the actual bean and use the data
				MaterialDM matObj = (MaterialDM) itemclient.getBean();
				if (matId != null && matId.equals(matObj.getMaterialId())) {
					cbMatName.setValue(itemIdClient);
				}
			}
			cbStockType.setValue(extrudersMtrlDM.getStockType());
			cbLotno.setValue(extrudersMtrlDM.getLotNo());
			tfMtrlQty.setValue(extrudersMtrlDM.getMaterialQty().toString());
			if (extrudersMtrlDM.getStatus() != null) {
				cbMtrlStatus.setValue(extrudersMtrlDM.getStatus());
			}
		}
	}
	
	private void editTemp() {
		hlUserInputLayout.setVisible(true);
		if (tblTemp.getValue() != null) {
			ExtrudersTempDM extrudersTempDM = new ExtrudersTempDM();
			extrudersTempDM = beanExtrudersTempDM.getItem(tblTemp.getValue()).getBean();
			extrudersTempDM.getExtTmprId();
			tfZoneName.setValue(extrudersTempDM.getZoneName());
			tfTempValue.setValue(extrudersTempDM.getTemprValue());
			if (extrudersTempDM.getStatus() != null) {
				cbTempStatus.setValue(extrudersTempDM.getStatus());
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
		cbHdrStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		cbMachineName.setValue(null);
		dfExtDt.setValue(null);
		tfExTPlnRef.setValue("");
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
		extMtrlResetFields();
		asmblDtlResetFields();
		asmblTempResetFields();
		assembleInputUserLayout();
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		cbMachineName.setRequired(true);
		dfExtDt.setRequired(true);
		cbMaterial.setRequired(true);
		tfOpQty.setRequired(true);
		tfOeePerc.setRequired(true);
		btnAddDtls.setCaption("Add");
		btnAddMtrl.setCaption("Add");
		btnAddTemp.setCaption("Add");
		dfProdDt.setValue(new Date());
		dfExtDt.setValue(new Date());
		try {
			tfExtRefNo.setReadOnly(false);
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "STT_MF_EXTREFNO").get(
					0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfExtRefNo.setValue(slnoObj.getKeyDesc());
				tfExtRefNo.setReadOnly(true);
			} else {
				tfExtRefNo.setReadOnly(false);
			}
		}
		catch (Exception e) {
		}

		tblDtl.setVisible(true);
		tblMtrl.setVisible(true);
		tblTemp.setVisible(true);
		btnAddMtrl.setEnabled(false);
		btnAddTemp.setEnabled(false);
		loadAsmbDtlList();
		loadMtrlRslt();
		loadTempRslt();
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Extruder. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_STT_MFG_EXTRUD_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", extHdrId);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
		asmblDtlResetFields();
		extMtrlResetFields();
		asmblTempResetFields();
		cbMachineName.setRequired(false);
		dfExtDt.setRequired(false);
		cbMaterial.setRequired(false);
		tfOpQty.setRequired(false);
		tfOeePerc.setRequired(false);
		hlCmdBtnLayout.setVisible(true);
		tblDtl.removeAllItems();
		tblMtrl.removeAllItems();
		tblTemp.removeAllItems();
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		asmblDtlResetFields();
		extMtrlResetFields();
		asmblTempResetFields();
		loadSrchRslt();
		SetRequiredfalse();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hlUserInputLayout.removeAllComponents();
		// remove the components in the search layout and input controls in the same container
		hlUserIPContainer.addComponent(hlUserInputLayout);
		cbMachineName.setRequired(true);
		dfExtDt.setRequired(true);
		cbMaterial.setRequired(true);
		tfOpQty.setRequired(true);
		tfOeePerc.setRequired(true);
		hlCmdBtnLayout.setVisible(false);
		// reset the input controls to default value
		tblMstScrSrchRslt.setVisible(false);
		if (tfExtRefNo.getValue() == null || tfExtRefNo.getValue().trim().length() == 0) {
			tfExtRefNo.setReadOnly(false);
		}
		tblMstScrSrchRslt.setVisible(false);
		if (tfLotNo.getValue() == null || tfLotNo.getValue().trim().length() == 0) {
			tfLotNo.setReadOnly(false);
		}
		// reset the input controls to default value
		assembleInputUserLayout();
		resetFields();
		editExtruderHdrDetails();
		editDtls();
	}
	
	private void asmblDtlResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		dfProdDt.setValue(null);
		tfOpQty.setValue("");
		tiChrgStTm.setValue(null);
		tiChargEdTm.setValue(null);
		tfOeePerc.setValue("");
		taRemark.setValue(null);
		tfOpQty.setComponentError(null);
		tfOeePerc.setComponentError(null);
		cbDtlStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
	}
	
	private void extMtrlResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbMatName.setValue(null);
		cbLotno.setValue(null);
		tfMtrlQty.setValue("0");
		cbStockType.setValue(cbStockType.getItemIds().iterator().next());
		cbMtrlStatus.setValue(cbMtrlStatus.getItemIds().iterator().next());
	}
	
	private void asmblTempResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfZoneName.setValue(null);
		tfTempValue.setValue(null);
		cbTempStatus.setValue(cbTempStatus.getItemIds().iterator().next());
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbMachineName.setComponentError(null);
		dfExtDt.setComponentError(null);
		cbMaterial.setComponentError(null);
		boolean errorflag = false;
		if ((cbMachineName.getValue() == null)) {
			cbMachineName.setComponentError(new UserError(GERPErrorCodes.NULL_MACHINE_NAME));
			errorflag = true;
		} else {
			cbMachineName.setComponentError(null);
		}
		if (dfExtDt.getValue() == null) {
			dfExtDt.setComponentError(new UserError(GERPErrorCodes.NULL_PULVIZDTL_DATE));
			errorflag = true;
		} else {
			dfExtDt.setComponentError(null);
		}
		SimpleDateFormat simpleDate = new SimpleDateFormat("dd/M/yy");
		simpleDate.format(new Date());
		String date = simpleDate.format(new Date());
		String dateField = simpleDate.format(dfExtDt.getValue());
		if (!dateField.equals(date)) {
			dfExtDt.setComponentError(new UserError(GERPErrorCodes.WRONG_DATE));
			errorflag = true;
		} else {
			dfExtDt.setComponentError(null);
		}
		if ((cbMaterial.getValue() == null)) {
			cbMaterial.setComponentError(new UserError(GERPErrorCodes.NULL_OP_MATERIAL_ID));
			errorflag = true;
		} else {
			cbMaterial.setComponentError(null);
		}
		if (tiHeatngTime.getValue() == null) {
			tiHeatngTime.setComponentError(new UserError(GERPErrorCodes.NULL_HEATING_TIME));
			errorflag = true;
		} else {
			tiHeatngTime.setComponentError(null);
		}
		if (errorflag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validateDtlDetails() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (dfProdDt.getValue() == null) {
			dfProdDt.setComponentError(new UserError(GERPErrorCodes.PRDCT_QTY_LONG));
			isValid = false;
		} else {
			dfProdDt.setComponentError(null);
		}
		try {
			Long.valueOf(tfOpQty.getValue());
			tfOpQty.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfOpQty.setComponentError(new UserError(GERPErrorCodes.PRDCT_QTY_LONG));
			isValid = false;
		}
		try {
			Long.valueOf(tfOeePerc.getValue());
			tfOeePerc.setComponentError(null);
		}
		catch (NumberFormatException e) {
			tfOeePerc.setComponentError(new UserError(GERPErrorCodes.PRDCT_QTY_LONG));
			isValid = false;
		}
		if (tiChrgStTm.getValue() == null) {
			tiChrgStTm.setComponentError(new UserError(GERPErrorCodes.NULL_CHARGE_TIME));
			isValid = false;
		} else {
			tiChrgStTm.setComponentError(null);
		}
		if (tiChargEdTm.getValue() == null) {
			tiChargEdTm.setComponentError(new UserError(GERPErrorCodes.NULL_CHARGE_END_TIME));
			isValid = false;
		} else {
			tiChargEdTm.setComponentError(null);
		}
		SimpleDateFormat simpleDate = new SimpleDateFormat("dd/M/yy");
		simpleDate.format(new Date());
		String date = simpleDate.format(new Date());
		String dateField = simpleDate.format(dfProdDt.getValue());
		if (!dateField.equals(date)) {
			dfProdDt.setComponentError(new UserError(GERPErrorCodes.WRONG_DATE));
			isValid = false;
		} else {
			dfProdDt.setComponentError(null);
		}
		return isValid;
	}
	
	private boolean validateMtrlDetails() {
		boolean isValid = true;
		if (cbMatName.getValue() == null) {
			cbMatName.setComponentError(new UserError(GERPErrorCodes.NULL_MATERAL_NAME));
			isValid = false;
		} else {
			cbMatName.setComponentError(null);
		}
		try {
			Long.valueOf(tfMtrlQty.getValue());
			if (Long.valueOf(tfMtrlQty.getValue()) <= 0) {
				tfMtrlQty.setComponentError(new UserError(GERPErrorCodes.NULL_MATEIAL_QTY));
				isValid = false;
			} else {
				tfMtrlQty.setComponentError(null);
			}
			tfMtrlQty.setComponentError(null);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			tfMtrlQty.setComponentError(new UserError(GERPErrorCodes.PRDCT_QTY_LONG));
			isValid = false;
		}
		return isValid;
	}
	
	private boolean validateTempDetails() {
		boolean isValid = true;
		if (tfZoneName.getValue() == null && tfZoneName.getValue().trim().length() == 0) {
			tfZoneName.setComponentError(new UserError(GERPErrorCodes.NULL_ZONE_NAME));
			isValid = false;
		} else {
			tfZoneName.setComponentError(null);
		}
		if (tfTempValue.getValue() == null && tfZoneName.getValue().trim().length() == 0) {
			tfTempValue.setComponentError(new UserError(GERPErrorCodes.NULL_TEMP));
			isValid = false;
		} else {
			tfTempValue.setComponentError(null);
		}
		return isValid;
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			ExtrudersHdrDM extruderObj = new ExtrudersHdrDM();
			new ExtrudersDtlDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				extruderObj = beanExtrudersHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (tblDtl.getValue() != null) {
				beanExtrudersDtlDM.getItem(tblDtl.getValue()).getBean();
			}
			extruderObj.setExtRefNo(tfExtRefNo.getValue());
			extruderObj.setLotNo(tfLotNo.getValue());
			extruderObj.setCmpId(companyid);
			extruderObj.setBranchId(branchID);
			extruderObj.setMachineId((Long) cbMachineName.getValue());
			extruderObj.setExtDate(dfExtDt.getValue());
			extruderObj.setGradeNo(tfGradeNo.getValue());
			if (tiHeatngTime.getValue() != null) {
				extruderObj.setHeatingTime(tiHeatngTime.getHorsMunites());
			}
			extruderObj.setInstruction(taInstruct.getValue());
			extruderObj.setOpMaterialId((Long) (cbMaterial.getValue()));
			extruderObj.setStatus(((String) cbHdrStatus.getValue()));
			extruderObj.setLastUpdatedDt((DateUtils.getcurrentdate()));
			extruderObj.setLastUpdatedBy(username);
			serviceExtruderHrd.saveAndUpdate(extruderObj);
			extHdrId = extruderObj.getExtId();
			@SuppressWarnings("unchecked")
			Collection<ExtrudersDtlDM> colPlanDtls = ((Collection<ExtrudersDtlDM>) tblDtl.getVisibleItemIds());
			for (ExtrudersDtlDM save : (Collection<ExtrudersDtlDM>) colPlanDtls) {
				save.setExtId(extruderObj.getExtId());
				serviceExtruderDtl.saveOrUpdate(save);
			}
			@SuppressWarnings("unchecked")
			Collection<ExtrudersMtrlDM> colMtrl = ((Collection<ExtrudersMtrlDM>) tblMtrl.getVisibleItemIds());
			for (ExtrudersMtrlDM saveMtrl : (Collection<ExtrudersMtrlDM>) colMtrl) {
				saveMtrl.setExtId(extruderObj.getExtId());
				serviceExtruderMtrl.saveOrUpdate(saveMtrl);
			}
			@SuppressWarnings("unchecked")
			Collection<ExtrudersTempDM> colExtTemp = ((Collection<ExtrudersTempDM>) tblTemp.getVisibleItemIds());
			for (ExtrudersTempDM saveTemp : (Collection<ExtrudersTempDM>) colExtTemp) {
				serviceExtruderTemp.saveAndUpdate(saveTemp);
			}
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId,
							"STT_MF_EXTREFNO").get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "STT_MF_EXTREFNO");
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId,
							"STT_MF_EXTLOTNO").get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "STT_MF_EXTLOTNO");
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveExtDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			ExtrudersDtlDM extruderDtlObj = new ExtrudersDtlDM();
			if (tblDtl.getValue() != null) {
				extruderDtlObj = beanExtrudersDtlDM.getItem(tblDtl.getValue()).getBean();
			} else {
				extruderDtlObj.setExtDtlId(Long.valueOf(serviceExtruderDtl.getSequence().toString()));
			}
			extruderDtlObj.setProdDate(dfProdDt.getValue());
			extruderDtlObj.setOutQty(Long.valueOf(tfOpQty.getValue()));
			if (tiChrgStTm.getValue() != null) {
				extruderDtlObj.setChrgStTime(tiChrgStTm.getHorsMunites());
			}
			if (tiChargEdTm.getValue() != null) {
				extruderDtlObj.setChrgEdTime(tiChargEdTm.getHorsMunites());
			}
			extruderDtlObj.setOeePrnct(Long.valueOf(tfOeePerc.getValue()));
			extruderDtlObj.setRemarks(taRemark.getValue());
			extruderDtlObj.setPreparedBy(employeeId);
			extruderDtlObj.setReviewedBy(employeeId);
			extruderDtlObj.setActionedBy(employeeId);
			extruderDtlObj.setStatus(((String) cbDtlStatus.getValue()));
			extruderDtlObj.setLastUpdatedDt((DateUtils.getcurrentdate()));
			extruderDtlObj.setLastUpdatedBy(username);
			extrudersDtlList.add(extruderDtlObj);
			loadAsmbDtlList();
			btnAddDtls.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		asmblDtlResetFields();
	}
	
	private void saveExtMtrlDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			ExtrudersDtlDM extrudersDtlDM = new ExtrudersDtlDM();
			extrudersDtlDM = beanExtrudersDtlDM.getItem(tblDtl.getValue()).getBean();
			ExtrudersMtrlDM extMtrlObj = new ExtrudersMtrlDM();
			if (tblMtrl.getValue() != null) {
				extMtrlObj = beanExtrudersMtrlDM.getItem(tblMtrl.getValue()).getBean();
			}
			if (cbMatName.getValue() != null) {
				extMtrlObj.setMaterialId(((MaterialDM) cbMatName.getValue()).getMaterialId());
				extMtrlObj.setMaterialName(((MaterialDM) cbMatName.getValue()).getMaterialName());
			}
			extMtrlObj.setExtDtlId(extrudersDtlDM.getExtDtlId());
			extMtrlObj.setStockType((String) cbStockType.getValue());
			if (cbLotno.getValue() != null) {
				extMtrlObj.setLotNo(cbLotno.getValue().toString());
			}
			extMtrlObj.setMaterialQty(Long.valueOf(tfMtrlQty.getValue().toString()));
			extMtrlObj.setStatus("Active");
			extMtrlObj.setLastUpdatedDt(DateUtils.getcurrentdate());
			extMtrlObj.setLastUpdatedBy(username);
			extrudersMtrlList.add(extMtrlObj);
			loadMtrlRslt();
			btnAddMtrl.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		extMtrlResetFields();
	}
	
	private void saveasmblPlnTempListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			ExtrudersDtlDM extrudersDtlDM = new ExtrudersDtlDM();
			extrudersDtlDM = beanExtrudersDtlDM.getItem(tblDtl.getValue()).getBean();
			ExtrudersTempDM extruderTempObj = new ExtrudersTempDM();
			if (tblTemp.getValue() != null) {
				extruderTempObj = beanExtrudersTempDM.getItem(tblTemp.getValue()).getBean();
			}
			extruderTempObj.setExtDtlId(extrudersDtlDM.getExtDtlId());
			extruderTempObj.setZoneName(tfZoneName.getValue());
			extruderTempObj.setTemprValue(tfTempValue.getValue());
			extruderTempObj.setStatus("Active");
			extruderTempObj.setLastUpdatedDt(DateUtils.getcurrentdate());
			extruderTempObj.setLastUpdatedBy(username);
			extrudersTempList.add(extruderTempObj);
			loadTempRslt();
			btnAddTemp.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		asmblTempResetFields();
	}
	
	private void loadMachineList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		BeanContainer<Long, AssetDetailsDM> beanAssetdetail = new BeanContainer<Long, AssetDetailsDM>(
				AssetDetailsDM.class);
		beanAssetdetail.setBeanIdProperty("assetId");
		beanAssetdetail.addAll(serviceAssetDetail.getAssetDetailList(companyid, null, null, (long) 1305, (long) 201,
				"7050", "Active"));
		cbMachineName.setContainerDataSource(beanAssetdetail);
	}
	
	private void loadMaterialList() {
		BeanItemContainer<MaterialDM> beanMaterial = new BeanItemContainer<MaterialDM>(MaterialDM.class);
		beanMaterial.addAll(serviceMaterial.getMaterialList(null, companyid, null, null, null, null, null, null,
				"Active", "P"));
		cbMatName.setContainerDataSource(beanMaterial);
	}
	
	private void loadOPMaterialList() {
		BeanContainer<Long, MaterialDM> beanMaterials = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
		beanMaterials.setBeanIdProperty("materialId");
		beanMaterials.addAll(serviceMaterial.getMaterialList(null, companyid, null, null, null, null, null, null,
				"Active", "P"));
		cbMaterial.setContainerDataSource(beanMaterials);
	}
	
	private void loadLotNumber() {
		BeanContainer<String, MaterialStockDM> beanMaterialStockDM = new BeanContainer<String, MaterialStockDM>(
				MaterialStockDM.class);
		for (MaterialStockDM materialStockDM : serviceMaterialStock.getMaterialStockList(
				(((MaterialDM) cbMatName.getValue()).getMaterialId()), companyid, null, null, null, null, "F")) {
			if (materialStockDM.getCurrentStock() > 0) {
				beanMaterialStockDM.setBeanIdProperty("lotNo");
				beanMaterialStockDM.addBean(materialStockDM);
				cbLotno.setContainerDataSource(beanMaterialStockDM);
			}
		}
	}
	
	private void deleteDtlDetails() {
		if (tblDtl.getValue() != null) {
			ExtrudersDtlDM extrudersDtlDM = beanExtrudersDtlDM.getItem(tblDtl.getValue()).getBean();
			extrudersDtlList.remove(extrudersDtlDM);
			asmblDtlResetFields();
			loadAsmbDtlList();
			btnDeletedtl.setEnabled(false);
		}
	}
	
	private void deleteMtrlDetails() {
		if (tblMtrl.getValue() != null) {
			ExtrudersMtrlDM extrudersMtrlDM = beanExtrudersMtrlDM.getItem(tblMtrl.getValue()).getBean();
			if (extrudersMtrlDM.getExtMtrlId() == null) {
				extrudersMtrlList.remove(extrudersMtrlDM);
			} else {
				extrudersMtrlDM.setStatus("Inactive");
				serviceExtruderMtrl.saveOrUpdate(extrudersMtrlDM);
				extrudersMtrlList.remove(extrudersMtrlDM);
			}
			extMtrlResetFields();
			loadMtrlRslt();
			btnDeleteMtrl.setEnabled(false);
		}
	}
	
	private void deleteZoneDetails() {
		ExtrudersTempDM extrudersTempDM = new ExtrudersTempDM();
		if (tblTemp.getValue() != null) {
			extrudersTempDM = beanExtrudersTempDM.getItem(tblTemp.getValue()).getBean();
			if (extrudersTempDM.getExtTmprId() == null) {
				extrudersTempList.remove(extrudersTempDM);
			} else {
				extrudersTempDM.setStatus("Inactive");
				serviceExtruderTemp.saveAndUpdate(extrudersTempDM);
				extrudersTempList.remove(extrudersTempDM);
			}
			asmblTempResetFields();
			loadTempRslt();
			btnDeletedtl.setEnabled(false);
		}
	}
	
	private void SetRequiredtrue() {
		cbMatName.setRequired(true);
		// cbLotno.setRequired(true);
		tfMtrlQty.setRequired(true);
		tfZoneName.setRequired(true);
		tfTempValue.setRequired(true);
	}
	
	private void SetRequiredfalse() {
		cbMatName.setRequired(false);
		cbLotno.setRequired(false);
		tfMtrlQty.setRequired(false);
		tfZoneName.setRequired(false);
		tfTempValue.setRequired(false);
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
			parameterMap.put("headerid", extHdrId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/extruder"); // extruder is the name of my jasper
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
