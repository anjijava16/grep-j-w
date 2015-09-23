package com.gnts.mfg.stt.txn;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.CompanyLookupService;
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
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
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
	private CompanyLookupService serviceCompLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private List<ExtrudersDtlDM> listExtrDtls = new ArrayList<ExtrudersDtlDM>();
	private List<ExtrudersMtrlDM> listExtrMaterial = null;
	private List<ExtrudersTempDM> listExtrTemp = null;
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
	private ComboBox cbMachineName, cbMatName, cbStockType, cbZoneName;
	private TextField tfExtRefNo, tfGradeNo, tfLotNo, tfExTPlnRef, tfIpQty, tfTotTime, tfOpQty, tfOeePerc, tfMtrlQty,
			tfIsoNo, tfTempValue;
	private PopupDateField dfExtDt, dfProdDt;
	private GERPTimeField tiHeatngTime, tiChrgStTm, tiChargEdTm;
	private TextArea taInstruct, taRemark;
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private Table tblExtrDtl, tblExtrMtrl, tblExtrTemp;
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
		tblExtrDtl = new GERPTable();
		tblExtrDtl.setPageLength(4);
		tblExtrDtl.setVisible(true);
		tblExtrDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblExtrDtl.isSelected(event.getItemId())) {
					tblExtrDtl.setImmediate(true);
					btnAddDtls.setCaption("Add");
					btnAddDtls.setStyleName("savebt");
					asmblDtlResetFields();
					SetRequiredfalse();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtls.setCaption("Update");
					btnAddDtls.setStyleName("savebt");
					editExtrDtls();
					SetRequiredtrue();
				}
			}
		});
		tblExtrMtrl = new GERPTable();
		tblExtrMtrl.setPageLength(4);
		tblExtrMtrl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblExtrMtrl.isSelected(event.getItemId())) {
					tblExtrMtrl.setImmediate(true);
					btnAddMtrl.setCaption("Add");
					btnAddMtrl.setStyleName("savebt");
					extMtrlResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddMtrl.setCaption("Update");
					btnAddMtrl.setStyleName("savebt");
					editExtrMtrl();
				}
			}
		});
		tblExtrTemp = new GERPTable();
		tblExtrTemp.setPageLength(4);
		tblExtrTemp.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblExtrTemp.isSelected(event.getItemId())) {
					tblExtrTemp.setImmediate(true);
					btnAddTemp.setCaption("Add");
					btnAddTemp.setStyleName("savebt");
					asmblTempResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddTemp.setCaption("Update");
					btnAddTemp.setStyleName("savebt");
					editExtrTemp();
				}
			}
		});
		tblMstScrSrchRslt.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
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
		tfIsoNo = new GERPTextField("ISO DocNo.");
		tfIsoNo.setValue("Test ISO No");
		tfIsoNo.setReadOnly(true);
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
		tfIpQty = new GERPTextField("Input Qty.");
		tfIpQty.setWidth("120");
		tfTotTime = new GERPTextField("Total Time");
		tfTotTime.setWidth("120");
		tfOpQty = new GERPTextField("OutPut Qty.");
		tfOpQty.setWidth("120");
		tiChrgStTm = new GERPTimeField("Charge St.Time");
		tiChrgStTm.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getTotalHours();
			}
		});
		tiChargEdTm = new GERPTimeField("Charge Ed.Time");
		tiChargEdTm.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				getTotalHours();
			}
		});
		tfOeePerc = new GERPTextField("OEE Percent");
		tfOeePerc.setWidth("120");
		tfOeePerc.setValue("0");
		tfOeePerc.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				getOeePercentage();
			}
		});
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
		cbZoneName = new ComboBox("Zone Name");
		cbZoneName.setWidth("150");
		loadlookuplist();
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
			logger.info(e.getMessage());
		}
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
		// flHdrCol1.addComponent(tfExtRefNo);
		flHdrCol1.addComponent(tfIsoNo);
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
		// flExtDtlCol1.addComponent(dfProdDt);
		flExtDtlCol1.addComponent(tfIpQty);
		flExtDtlCol1.addComponent(tfOpQty);
		flExtDtlCol1.addComponent(tiChrgStTm);
		flExtDtlCol1.addComponent(tiChargEdTm);
		flExtDtlCol1.addComponent(tfTotTime);
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
		vlDtl.addComponent(tblExtrDtl);
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
		vlMtrl.addComponent(tblExtrMtrl);
		// Adding Temp Components
		flTempCol1 = new FormLayout();
		flTempCol1.addComponent(cbZoneName);
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
		vlTemp.addComponent(tblExtrTemp);
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
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<ExtrudersHdrDM> list = new ArrayList<ExtrudersHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfExtRefNo.getValue() + ", " + cbHdrStatus.getValue());
			if (cbMachineName.getValue() != null) {
			}
			list = serviceExtruderHrd.getExtruderList(null, companyid, null, (Long) cbMachineName.getValue(),
					(String) tfExTPlnRef.getValue(), dfExtDt.getValue(), null, null, (String) cbHdrStatus.getValue(),
					"F");
			recordCnt = list.size();
			beanExtrudersHdrDM = new BeanItemContainer<ExtrudersHdrDM>(ExtrudersHdrDM.class);
			beanExtrudersHdrDM.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the AssemblyPlan. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanExtrudersHdrDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "extId", "machineName", "extRefNo", "extDate",
					"gradeNo", "lastUpdatedDt", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Machine Name", "Extruder Ref No",
					"Extruder Date", "Grade No", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("extId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records:" + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadAsmbDtlList() {
		try {
			tblExtrDtl.setSizeFull();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			logger.info("Company ID : " + companyid + " | saveExtDtl User Name : " + username + " > "
					+ "Search Parameters are " + companyid + ", " + (String) cbMtrlStatus.getValue() + ", " + extHdrId);
			recordDtlCnt = listExtrDtls.size();
			beanExtrudersDtlDM = new BeanItemContainer<ExtrudersDtlDM>(ExtrudersDtlDM.class);
			beanExtrudersDtlDM.addAll(listExtrDtls);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Extruderslap. result set");
			tblExtrDtl.setContainerDataSource(beanExtrudersDtlDM);
			tblExtrDtl.setVisibleColumns(new Object[] { "inputQty", "outQty", "oeePrnct", "chrgStTime", "chrgEdTime",
					"totalTime" });
			tblExtrDtl.setColumnWidth("extDtlId", 60);
			tblExtrDtl.setColumnWidth("", 85);
			tblExtrDtl.setColumnWidth("outQty", 60);
			tblExtrDtl.setColumnWidth("oeePrnct", 60);
			tblExtrDtl.setColumnWidth("chrgStTime", 70);
			tblExtrDtl.setColumnWidth("chrgEdTime", 80);
			tblExtrDtl.setColumnWidth("", 80);
			tblExtrDtl.setColumnHeaders(new String[] { "I/P Qty.", "O/P Qty.", "OEE %", "Start Time", "End Time",
					"Total Time" });
			tblExtrDtl.setColumnFooter("totalTime", "Records :" + recordDtlCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadMtrlRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordMtrlCnt = listExtrMaterial.size();
			beanExtrudersMtrlDM = new BeanItemContainer<ExtrudersMtrlDM>(ExtrudersMtrlDM.class);
			beanExtrudersMtrlDM.addAll(listExtrMaterial);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Extruder. result set");
			tblExtrMtrl.setContainerDataSource(beanExtrudersMtrlDM);
			tblExtrMtrl.setVisibleColumns(new Object[] { "extDtlId", "materialName", "stockType" });
			tblExtrMtrl.setColumnHeaders(new String[] { "Dtl.RefId", "Material Name", "Stock Type" });
			tblExtrMtrl.setColumnAlignment("extDtlId", Align.RIGHT);
			tblExtrMtrl.setColumnFooter("stockType", "Records :" + recordMtrlCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadTempRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordTempCnt = listExtrTemp.size();
			beanExtrudersTempDM = new BeanItemContainer<ExtrudersTempDM>(ExtrudersTempDM.class);
			beanExtrudersTempDM.addAll(listExtrTemp);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Extruder. result set");
			tblExtrTemp.setContainerDataSource(beanExtrudersTempDM);
			tblExtrTemp.setVisibleColumns(new Object[] { "extDtlId", "zoneName", "temprValue" });
			tblExtrTemp.setColumnHeaders(new String[] { "Dtl.RefId", "Zone Name", "Temp. Value" });
			tblExtrTemp.setColumnAlignment("extDtlId", Align.RIGHT);
			tblExtrTemp.setColumnFooter("temprValue", "Records :" + recordTempCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
		tfIpQty.setValue("");
		tfTotTime.setValue("");
		taRemark.setValue(null);
		cbDtlStatus.setValue(cbHdrStatus.getItemIds().iterator().next());
		// Extruders Mtrl Resetfields
		cbMatName.setValue(null);
		cbStockType.setValue(null);
		cbLotno.setValue(null);
		tfMtrlQty.setValue("");
		cbMtrlStatus.setValue(cbMtrlStatus.getItemIds().iterator().next());
		// Extruders Temperature Resetfields
		cbZoneName.setValue(null);
		tfTempValue.setValue(null);
		cbMachineName.setComponentError(null);
		dfExtDt.setComponentError(null);
		cbMaterial.setComponentError(null);
		cbTempStatus.setValue(cbTempStatus.getItemIds().iterator().next());
		// Initializing temporary List
		listExtrDtls = new ArrayList<ExtrudersDtlDM>();
		listExtrMaterial = new ArrayList<ExtrudersMtrlDM>();
		listExtrTemp = new ArrayList<ExtrudersTempDM>();
		tblExtrDtl.removeAllItems();
		tblExtrMtrl.removeAllItems();
		tblExtrTemp.removeAllItems();
	}
	
	// Method to edit the values from table into fields to update process
	private void editExtruderHdrDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
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
				listExtrDtls = serviceExtruderDtl.getExtrudersDtlList(null, null, extHdrId, null, null, null, null,
						null, null, (String) cbDtlStatus.getValue(), "F");
			}
			loadAsmbDtlList();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editExtrDtls() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblExtrDtl.getValue() != null) {
				ExtrudersDtlDM extrudersDtlDM = new ExtrudersDtlDM();
				extrudersDtlDM = beanExtrudersDtlDM.getItem(tblExtrDtl.getValue()).getBean();
				extrudersDtlDM.getExtDtlId();
				dfProdDt.setValue(extrudersDtlDM.getProdDateDT());
				if (extrudersDtlDM.getOutQty() != null) {
					tfOpQty.setValue(extrudersDtlDM.getOutQty().toString());
				}
				if (extrudersDtlDM.getInputQty() != null) {
					tfIpQty.setValue(extrudersDtlDM.getInputQty().toString());
				}
				if (extrudersDtlDM.getTotalTime() != null) {
					tfTotTime.setValue(extrudersDtlDM.getTotalTime().toString());
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
				listExtrMaterial = serviceExtruderMtrl.getExtMtrlList(null, extrudersDtlDM.getExtDtlId(), null, null,
						null, "Active", "F");
				listExtrTemp = serviceExtruderTemp.getExtTempDetails(null, extrudersDtlDM.getExtDtlId(), null, null,
						"Active", "F");
			}
			loadMtrlRslt();
			loadTempRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editExtrMtrl() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblExtrMtrl.getValue() != null) {
				ExtrudersMtrlDM extrudersMtrlDM = new ExtrudersMtrlDM();
				extrudersMtrlDM = beanExtrudersMtrlDM.getItem(tblExtrMtrl.getValue()).getBean();
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
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editExtrTemp() {
		try {
			hlUserInputLayout.setVisible(true);
			if (tblExtrTemp.getValue() != null) {
				ExtrudersTempDM extrudersTempDM = new ExtrudersTempDM();
				extrudersTempDM = beanExtrudersTempDM.getItem(tblExtrTemp.getValue()).getBean();
				extrudersTempDM.getExtTmprId();
				cbZoneName.setValue(extrudersTempDM.getZoneName());
				tfTempValue.setValue(extrudersTempDM.getTemprValue());
				if (extrudersTempDM.getStatus() != null) {
					cbTempStatus.setValue(extrudersTempDM.getStatus());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
			logger.info(e.getMessage());
		}
		tblExtrDtl.setVisible(true);
		tblExtrMtrl.setVisible(true);
		tblExtrTemp.setVisible(true);
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
		tblExtrDtl.removeAllItems();
		tblExtrMtrl.removeAllItems();
		tblExtrTemp.removeAllItems();
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
		editExtrDtls();
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
	
	// this method use to Load lookup Name list inside of ComboBox
	private void loadlookuplist() {
		try {
			BeanContainer<String, CompanyLookupDM> beanlook = new BeanContainer<String, CompanyLookupDM>(
					CompanyLookupDM.class);
			beanlook.setBeanIdProperty("lookupname");
			beanlook.addAll(serviceCompLookup.getCompanyLookUpByLookUp(companyid, moduleId, "Active", "MP_ZONNAME"));
			cbZoneName.setContainerDataSource(beanlook);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
		cbZoneName.setValue(null);
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
		if (cbZoneName.getValue() == null) {
			cbZoneName.setComponentError(new UserError(GERPErrorCodes.NULL_ZONE_NAME));
			isValid = false;
		} else {
			cbZoneName.setComponentError(null);
		}
		if (tfTempValue.getValue() == null || tfTempValue.getValue().trim().length() == 0) {
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
			if (tblExtrDtl.getValue() != null) {
				beanExtrudersDtlDM.getItem(tblExtrDtl.getValue()).getBean();
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
			Collection<ExtrudersDtlDM> colPlanDtls = ((Collection<ExtrudersDtlDM>) tblExtrDtl.getVisibleItemIds());
			for (ExtrudersDtlDM save : (Collection<ExtrudersDtlDM>) colPlanDtls) {
				save.setExtId(extruderObj.getExtId());
				serviceExtruderDtl.saveOrUpdate(save);
			}
			@SuppressWarnings("unchecked")
			Collection<ExtrudersMtrlDM> colMtrl = ((Collection<ExtrudersMtrlDM>) tblExtrMtrl.getVisibleItemIds());
			for (ExtrudersMtrlDM saveMtrl : (Collection<ExtrudersMtrlDM>) colMtrl) {
				saveMtrl.setExtId(extruderObj.getExtId());
				serviceExtruderMtrl.saveOrUpdate(saveMtrl);
			}
			@SuppressWarnings("unchecked")
			Collection<ExtrudersTempDM> colExtTemp = ((Collection<ExtrudersTempDM>) tblExtrTemp.getVisibleItemIds());
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
					logger.info(e.getMessage());
				}
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId,
							"STT_MF_EXTLOTNO").get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "STT_MF_EXTLOTNO");
					}
				}
				catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void saveExtDtl() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			ExtrudersDtlDM extruderDtlObj = new ExtrudersDtlDM();
			if (tblExtrDtl.getValue() != null) {
				extruderDtlObj = beanExtrudersDtlDM.getItem(tblExtrDtl.getValue()).getBean();
			} else {
				extruderDtlObj.setExtDtlId(Long.valueOf(serviceExtruderDtl.getSequence().toString()));
			}
			extruderDtlObj.setProdDate(dfProdDt.getValue());
			extruderDtlObj.setInputQty(tfIpQty.getValue().toString());
			extruderDtlObj.setTotalTime(tfTotTime.getValue().toString());
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
			listExtrDtls.add(extruderDtlObj);
			loadAsmbDtlList();
			btnAddDtls.setCaption("Add");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		asmblDtlResetFields();
	}
	
	private void saveExtMtrlDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			ExtrudersDtlDM extrudersDtlDM = new ExtrudersDtlDM();
			extrudersDtlDM = beanExtrudersDtlDM.getItem(tblExtrDtl.getValue()).getBean();
			ExtrudersMtrlDM extMtrlObj = new ExtrudersMtrlDM();
			if (tblExtrMtrl.getValue() != null) {
				extMtrlObj = beanExtrudersMtrlDM.getItem(tblExtrMtrl.getValue()).getBean();
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
			listExtrMaterial.add(extMtrlObj);
			loadMtrlRslt();
			btnAddMtrl.setCaption("Add");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		extMtrlResetFields();
	}
	
	private void saveasmblPlnTempListDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			ExtrudersDtlDM extrudersDtlDM = new ExtrudersDtlDM();
			if (tblExtrDtl.getValue() != null) {
				extrudersDtlDM = beanExtrudersDtlDM.getItem(tblExtrDtl.getValue()).getBean();
			}
			ExtrudersTempDM extruderTempObj = new ExtrudersTempDM();
			if (tblExtrTemp.getValue() != null) {
				extruderTempObj = beanExtrudersTempDM.getItem(tblExtrTemp.getValue()).getBean();
			}
			extruderTempObj.setExtDtlId(extrudersDtlDM.getExtDtlId());
			extruderTempObj.setZoneName((String) cbZoneName.getValue());
			extruderTempObj.setTemprValue(tfTempValue.getValue());
			extruderTempObj.setStatus("Active");
			extruderTempObj.setLastUpdatedDt(DateUtils.getcurrentdate());
			extruderTempObj.setLastUpdatedBy(username);
			listExtrTemp.add(extruderTempObj);
			loadTempRslt();
			btnAddTemp.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		asmblTempResetFields();
	}
	
	private void loadMachineList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
			BeanContainer<Long, AssetDetailsDM> beanAssetdetail = new BeanContainer<Long, AssetDetailsDM>(
					AssetDetailsDM.class);
			beanAssetdetail.setBeanIdProperty("assetId");
			beanAssetdetail.addAll(serviceAssetDetail.getAssetDetailList(companyid, null, null, null, null, "7050",
					"Active"));
			cbMachineName.setContainerDataSource(beanAssetdetail);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadMaterialList() {
		try {
			Long materialId = new Long("229");
			BeanItemContainer<MaterialDM> beanMaterial = new BeanItemContainer<MaterialDM>(MaterialDM.class);
			beanMaterial.addAll(serviceMaterial.getMaterialList(materialId, companyid, null, null, null, null, null,
					null, "Active", "P"));
			cbMatName.setContainerDataSource(beanMaterial);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadOPMaterialList() {
		try {
			BeanContainer<Long, MaterialDM> beanMaterials = new BeanContainer<Long, MaterialDM>(MaterialDM.class);
			beanMaterials.setBeanIdProperty("materialId");
			beanMaterials.addAll(serviceMaterial.getMaterialList(null, companyid, null, null, null, null, null, null,
					"Active", "P"));
			cbMaterial.setContainerDataSource(beanMaterials);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadLotNumber() {
		try {
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
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deleteDtlDetails() {
		try {
			if (tblExtrDtl.getValue() != null) {
				ExtrudersDtlDM extrudersDtlDM = beanExtrudersDtlDM.getItem(tblExtrDtl.getValue()).getBean();
				listExtrDtls.remove(extrudersDtlDM);
				asmblDtlResetFields();
				loadAsmbDtlList();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deleteMtrlDetails() {
		try {
			if (tblExtrMtrl.getValue() != null) {
				ExtrudersMtrlDM extrudersMtrlDM = beanExtrudersMtrlDM.getItem(tblExtrMtrl.getValue()).getBean();
				if (extrudersMtrlDM.getExtMtrlId() == null) {
					listExtrMaterial.remove(extrudersMtrlDM);
				} else {
					extrudersMtrlDM.setStatus("Inactive");
					serviceExtruderMtrl.saveOrUpdate(extrudersMtrlDM);
					listExtrMaterial.remove(extrudersMtrlDM);
				}
				extMtrlResetFields();
				loadMtrlRslt();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deleteZoneDetails() {
		try {
			ExtrudersTempDM extrudersTempDM = new ExtrudersTempDM();
			if (tblExtrTemp.getValue() != null) {
				extrudersTempDM = beanExtrudersTempDM.getItem(tblExtrTemp.getValue()).getBean();
				if (extrudersTempDM.getExtTmprId() == null) {
					listExtrTemp.remove(extrudersTempDM);
				} else {
					extrudersTempDM.setStatus("Inactive");
					serviceExtruderTemp.saveAndUpdate(extrudersTempDM);
					listExtrTemp.remove(extrudersTempDM);
				}
				asmblTempResetFields();
				loadTempRslt();
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void SetRequiredtrue() {
		cbMatName.setRequired(true);
		// cbLotno.setRequired(true);
		tfMtrlQty.setRequired(true);
		cbZoneName.setRequired(true);
		tfTempValue.setRequired(true);
	}
	
	private void SetRequiredfalse() {
		cbMatName.setRequired(false);
		cbLotno.setRequired(false);
		tfMtrlQty.setRequired(false);
		cbZoneName.setRequired(false);
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
	
	private void getOeePercentage() {
		try {
			BigDecimal outPutOty = (new BigDecimal(tfOpQty.getValue()).divide(new BigDecimal("650"), 2,
					RoundingMode.HALF_UP)).multiply(new BigDecimal("100"));
			BigDecimal time = tiChargEdTm.getHorsMunitesinBigDecimal()
					.subtract(tiChrgStTm.getHorsMunitesinBigDecimal())
					.divide(new BigDecimal("6.30"), 2, RoundingMode.HALF_UP);
			tfOeePerc.setValue(outPutOty.multiply(time).round(new MathContext(2)).toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getTotalHours() {
		try {
			// TODO Auto-generated method stub
			if (tiChrgStTm.getValue() != null && tiChargEdTm.getValue() != null) {
				if (tiChrgStTm.getHorsMunitesinBigDecimal().compareTo(tiChargEdTm.getHorsMunitesinBigDecimal()) < 0) {
					tfTotTime.setValue(tiChrgStTm.getHorsMunitesinBigDecimal()
							.subtract(tiChargEdTm.getHorsMunitesinBigDecimal()).abs().toString());
				} else {
					tfTotTime.setValue("0.0");
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
