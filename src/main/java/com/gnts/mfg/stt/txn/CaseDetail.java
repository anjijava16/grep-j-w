package com.gnts.mfg.stt.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.stt.mfg.domain.txn.CaseDetailDM;
import com.gnts.stt.mfg.service.txn.CaseDetailService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Table.Align;

public class CaseDetail extends BaseUI {
	// Bean creation
	private CaseDetailService serviceCaseDetail = (CaseDetailService) SpringContextHelper.getBean("caseDetail");
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Add User Input Controls
	private TextField tfCaseModel, tfPowWgTop, tfPowWgBot, tfPowWgTotal, tfBoxWgTop, tfBoxWBot, tfBoxWgTotal,
			tfInsertCase, tfWheelPBoltSz, tfFrokLift, tfLatchHook, tfRivertHinge, tfBoltHinge, tfRivert,
			tfBoltNutWasher, tfWireRopeRevert, tfGasket, tfGasketMtr, tfCaseDInner, tfCaseDOuter;
	private TextArea taRemarks;
	private ComboBox cbCaseStatus;
	private BeanItemContainer<CaseDetailDM> beanCaseDetailDM = null;
	// BeanItemContainer
	// local variables declaration
	private Long companyid;
	private String caseId;
	private int recordCnt = 0;
	private String username;
	private Boolean errorFlag = false;
	// Initialize logger
	private Logger logger = Logger.getLogger(CaseDetailDM.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public CaseDetail() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside CaseDetail() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Case Detail UI");
		// Status ComboBox
		cbCaseStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbCaseStatus.setWidth("150");
		tfCaseModel = new GERPTextField("Case Model");
		tfCaseModel.setWidth("150");
		tfPowWgTop = new GERPTextField("Powder Top");
		tfPowWgTop.setWidth("150");
		tfPowWgBot = new GERPTextField("Powder Bottom");
		tfPowWgBot.setWidth("150");
		tfPowWgTotal = new GERPTextField("Powder Total");
		tfPowWgTotal.setWidth("150");
		tfBoxWgTop = new GERPTextField("Box Wg.Top");
		tfBoxWgTop.setWidth("150");
		tfBoxWBot = new GERPTextField("Box Wg.Bottom");
		tfBoxWBot.setWidth("150");
		tfBoxWgTotal = new GERPTextField("Box Wg.Total");
		tfBoxWgTotal.setWidth("150");
		tfInsertCase = new GERPTextField("Insert");
		tfInsertCase.setWidth("150");
		tfWheelPBoltSz = new GERPTextField("wheel/Bolt Size");
		tfWheelPBoltSz.setWidth("150");
		tfFrokLift = new GERPTextField("Frok Lift");
		tfFrokLift.setWidth("150");
		tfLatchHook = new GERPTextField("Latch Hook");
		tfLatchHook.setWidth("150");
		tfRivertHinge = new GERPTextField("Revet Hinge");
		tfRivertHinge.setWidth("150");
		tfBoltHinge = new GERPTextField("Bolt Hinge");
		tfBoltHinge.setWidth("150");
		tfRivert = new GERPTextField("Rivert");
		tfRivert.setWidth("150");
		tfBoltNutWasher = new GERPTextField("Bolt Nut Washer");
		tfBoltNutWasher.setWidth("150");
		tfWireRopeRevert = new GERPTextField("Wire ROpe Revert ");
		tfWireRopeRevert.setWidth("150");
		tfGasket = new GERPTextField("Gasket");
		tfGasket.setWidth("150");
		tfGasketMtr = new GERPTextField("Gasket Meter");
		tfGasketMtr.setWidth("150");
		tfCaseDInner = new GERPTextField("Case Dim.Inner");
		tfCaseDInner.setWidth("150");
		tfCaseDOuter = new GERPTextField("Case Dim.Outer");
		tfCaseDOuter.setWidth("150");
		taRemarks = new GERPTextArea("Remarks");
		taRemarks.setWidth("150");
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlSearchLayout.removeAllComponents();
		flColumn1 = new FormLayout();
		flColumn2 = new FormLayout();
		flColumn3 = new FormLayout();
		flColumn1.addComponent(tfCaseModel);
		flColumn2.addComponent(cbCaseStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Assembling User Input layout");
			// Remove all components in Search Layout
			hlSearchLayout.removeAllComponents();
			// Add components for User Input Layout
			flColumn1 = new FormLayout();
			flColumn2 = new FormLayout();
			flColumn3 = new FormLayout();
			flColumn4 = new FormLayout();
			flColumn1.addComponent(tfCaseModel);
			flColumn1.addComponent(tfPowWgTop);
			flColumn1.addComponent(tfPowWgBot);
			flColumn1.addComponent(tfPowWgTotal);
			flColumn1.addComponent(tfBoxWgTop);
			flColumn1.addComponent(tfBoxWBot);
			flColumn2.addComponent(tfBoxWgTotal);
			flColumn2.addComponent(tfInsertCase);
			flColumn2.addComponent(tfWheelPBoltSz);
			flColumn2.addComponent(tfFrokLift);
			flColumn2.addComponent(tfLatchHook);
			flColumn2.addComponent(tfRivertHinge);
			flColumn3.addComponent(tfBoltHinge);
			flColumn3.addComponent(tfRivert);
			flColumn3.addComponent(tfBoltNutWasher);
			flColumn3.addComponent(tfWireRopeRevert);
			flColumn3.addComponent(tfGasket);
			flColumn3.addComponent(tfGasketMtr);
			flColumn4.addComponent(tfCaseDInner);
			flColumn4.addComponent(tfCaseDOuter);
			flColumn4.addComponent(taRemarks);
			flColumn4.addComponent(cbCaseStatus);
			hlUserInputLayout.addComponent(flColumn1);
			hlUserInputLayout.addComponent(flColumn2);
			hlUserInputLayout.addComponent(flColumn3);
			hlUserInputLayout.addComponent(flColumn4);
			hlUserInputLayout.setSizeUndefined();
			hlUserInputLayout.setMargin(true);
			hlUserInputLayout.setSpacing(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<CaseDetailDM> list = new ArrayList<CaseDetailDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + tfCaseModel.getValue() + ", " + (String) cbCaseStatus.getValue());
		list = serviceCaseDetail.getCaseDetail(null, companyid, null, (String) cbCaseStatus.getValue(), "F");
		recordCnt = list.size();
		beanCaseDetailDM = new BeanItemContainer<CaseDetailDM>(CaseDetailDM.class);
		beanCaseDetailDM.addAll(list);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Case Id. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanCaseDetailDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "caseId", "caseModel", "remarks", "caseStatus",
				"lastupdatedby", "lastupdateddate" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Case.Id", "Case Model", "Reamrks", "Status", "Updated By",
				"Updated Date" });
		tblMstScrSrchRslt.setColumnAlignment("caseId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbCaseStatus.setValue(cbCaseStatus.getItemIds().iterator().next());
		tfCaseModel.setValue("");
		tfCaseModel.setEnabled(false);
		tfCaseModel.setComponentError(null);
		tfPowWgTop.setValue("");
		tfPowWgBot.setValue("");
		tfPowWgTotal.setValue("");
		tfPowWgTotal.setEnabled(true);
		tfBoxWgTop.setValue("");
		tfBoxWBot.setValue("");
		tfBoxWgTotal.setValue("");
		tfBoxWgTotal.setEnabled(true);
		tfInsertCase.setValue("");
		tfWheelPBoltSz.setValue("");
		tfFrokLift.setValue("");
		tfLatchHook.setValue("");
		tfRivertHinge.setValue("");
		tfBoltHinge.setValue("");
		tfRivert.setValue("");
		tfBoltNutWasher.setValue("");
		tfWireRopeRevert.setValue("");
		tfGasket.setValue("");
		tfGasketMtr.setValue("");
		tfCaseDInner.setValue("");
		tfCaseDOuter.setValue("");
		taRemarks.setValue("");
		cbCaseStatus.setValue(null);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editCaseDetail() {
		CaseDetailDM editCaseDetail = beanCaseDetailDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		caseId = editCaseDetail.getCaseId().toString();
		if (editCaseDetail.getCaseModel() != null) {
			tfCaseModel.setValue(editCaseDetail.getCaseModel());
			tfCaseModel.setEnabled(true);
		}
		if (editCaseDetail.getPowWgTop() != null) {
			tfPowWgTop.setValue(editCaseDetail.getPowWgTop());
		}
		if (editCaseDetail.getPowWgBot() != null) {
			tfPowWgBot.setValue(editCaseDetail.getPowWgBot());
		}
		if (editCaseDetail.getPowWgTotal() != null) {
			tfPowWgTotal.setValue(editCaseDetail.getPowWgTotal());
		}
		if (editCaseDetail.getBoxWgTop() != null) {
			tfBoxWgTop.setValue(editCaseDetail.getBoxWgTop());
		}
		if (editCaseDetail.getBoxWgBot() != null) {
			tfBoxWBot.setValue(editCaseDetail.getBoxWgBot());
		}
		if (editCaseDetail.getBoxWgTotal() != null) {
			tfBoxWgTotal.setValue(editCaseDetail.getBoxWgTotal());
		}
		if (editCaseDetail.getInsertCase() != null) {
			tfInsertCase.setValue(editCaseDetail.getInsertCase());
		}
		if (editCaseDetail.getWheelPboltSz() != null) {
			tfWheelPBoltSz.setValue(editCaseDetail.getWheelPboltSz());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfFrokLift.setValue(editCaseDetail.getFrokLift());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfLatchHook.setValue(editCaseDetail.getLatchHook());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfRivertHinge.setValue(editCaseDetail.getRivertHinge());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfBoltHinge.setValue(editCaseDetail.getBoltHinge());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfRivert.setValue(editCaseDetail.getRivert());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfBoltNutWasher.setValue(editCaseDetail.getBoltNutWasher());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfWireRopeRevert.setValue(editCaseDetail.getWireRopeRevet());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfGasket.setValue(editCaseDetail.getGasket());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfGasketMtr.setValue(editCaseDetail.getGasketMtr());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfCaseDInner.setValue(editCaseDetail.getCaseDInner());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfCaseDOuter.setValue(editCaseDetail.getCaseDOuter());
		}
		if (editCaseDetail.getCaseModel() != null) {
			tfCaseModel.setValue(editCaseDetail.getCaseModel());
		}
		if (editCaseDetail.getCaseStatus() != null) {
			cbCaseStatus.setValue(editCaseDetail.getCaseStatus());
		}
		if (editCaseDetail.getRemarks() != null) {
			taRemarks.setValue(editCaseDetail.getRemarks());
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
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		tfCaseModel.setValue("");
		cbCaseStatus.setValue(cbCaseStatus.getItemIds().iterator().next());
		// reset the field valued to default
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tfCaseModel.setRequired(true);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Dept. ID " + caseId);
		UI.getCurrent().getSession().setAttribute("audittablepk", caseId);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfCaseModel.setRequired(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		resetFields();
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		tfCaseModel.setRequired(true);
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editCaseDetail();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		errorFlag = false;
		tfCaseModel.setComponentError(null);
		if ((tfCaseModel.getValue() == null) || tfCaseModel.getValue().trim().length() == 0) {
			tfCaseModel.setComponentError(new UserError("Enter Box Model Number"));
			errorFlag = true;
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + tfCaseModel.getValue());
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() throws SaveException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			CaseDetailDM CaseDetailObj = new CaseDetailDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				CaseDetailObj = beanCaseDetailDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			if (tfCaseModel.getValue() != null && tfCaseModel.getValue().trim().length() > 0) {
				CaseDetailObj.setCaseModel(tfCaseModel.getValue());
			}
			CaseDetailObj.setPowWgTop(tfPowWgTop.getValue());
			CaseDetailObj.setPowWgBot(tfPowWgBot.getValue());
			CaseDetailObj.setPowWgTotal(tfPowWgTotal.getValue());
			CaseDetailObj.setBoxWgTop(tfBoxWgTop.getValue());
			CaseDetailObj.setBoxWgBot(tfBoxWBot.getValue());
			CaseDetailObj.setBoxWgTotal(tfBoxWgTotal.getValue());
			CaseDetailObj.setInsertCase(tfInsertCase.getValue());
			CaseDetailObj.setWheelPboltSz(tfWheelPBoltSz.getValue());
			CaseDetailObj.setFrokLift(tfFrokLift.getValue());
			CaseDetailObj.setLatchHook(tfLatchHook.getValue());
			CaseDetailObj.setRivertHinge(tfRivertHinge.getValue());
			CaseDetailObj.setBoltHinge(tfBoltHinge.getValue());
			CaseDetailObj.setRivert(tfRivert.getValue());
			CaseDetailObj.setBoltNutWasher(tfBoltNutWasher.getValue());
			CaseDetailObj.setWireRopeRevet(tfWireRopeRevert.getValue());
			CaseDetailObj.setGasket(tfGasket.getValue());
			CaseDetailObj.setGasketMtr(tfGasketMtr.getValue());
			CaseDetailObj.setCaseDInner(tfCaseDInner.getValue());
			CaseDetailObj.setCaseDOuter(tfCaseDOuter.getValue());
			CaseDetailObj.setRemarks(taRemarks.getValue());
			CaseDetailObj.setCompanyId(companyid);
			if (cbCaseStatus.getValue() != null) {
				CaseDetailObj.setCaseStatus((String) cbCaseStatus.getValue());
			}
			CaseDetailObj.setLastupdateddate(DateUtils.getcurrentdate());
			CaseDetailObj.setLastupdatedby(username);
			serviceCaseDetail.saveCaseDetail(CaseDetailObj);
			resetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
