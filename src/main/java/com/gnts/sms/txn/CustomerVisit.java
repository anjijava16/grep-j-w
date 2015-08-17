package com.gnts.sms.txn;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
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
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
import com.gnts.mfg.stt.txn.Roto;
import com.gnts.sms.domain.txn.CustomerVisitHdrDM;
import com.gnts.sms.domain.txn.CustomerVisitInfoDtlDM;
import com.gnts.sms.domain.txn.CustomerVisitNoDtlDM;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.service.txn.CustomerVisitHdrService;
import com.gnts.sms.service.txn.CustomerVisitInfoDtlService;
import com.gnts.sms.service.txn.CustomerVisitNoDtlService;
import com.gnts.sms.service.txn.SmsEnqHdrService;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class CustomerVisit extends BaseTransUI {
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private CustomerVisitNoDtlService serviceCustomerVisitNoDtl = (CustomerVisitNoDtlService) SpringContextHelper
			.getBean("customervisitnodtl");
	private CustomerVisitInfoDtlService serviceCustomerVisitInfoDtl = (CustomerVisitInfoDtlService) SpringContextHelper
			.getBean("customervisitinfodtl");
	private CustomerVisitHdrService serviceCustomerVisitHdr = (CustomerVisitHdrService) SpringContextHelper
			.getBean("customervisithdr");
	private WorkOrderHdrService serviceWorkOrderHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private SmsEnqHdrService serviceEnquiryHdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	// User Input Components for Work Order Details
	private BeanItemContainer<CustomerVisitHdrDM> beanCustomerVisitHdrDM = null;
	private BeanItemContainer<CustomerVisitNoDtlDM> beanCustomerVisitNoDtlDM = null;
	private BeanItemContainer<CustomerVisitInfoDtlDM> beanCustomerVisitInfoDtlDM = null;
	private List<CustomerVisitNoDtlDM> listCustVisitNo = new ArrayList<CustomerVisitNoDtlDM>();
	private List<CustomerVisitInfoDtlDM> listCustVisitInfo = new ArrayList<CustomerVisitInfoDtlDM>();
	private Table tblCusVisHdr, tblPersonNo, tblInfoPass;
	// Search Control Layout
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlSearchLayout, hlPerandInfo, hlHdrMain, hlPerson, hlInform, hlHdrslap;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlInform, vlPerson, vlHdrandDetail;
	// Data Fields
	private GERPPopupDateField dfVisitDt, dfFormDt;
	private GERPComboBox cbWO, cbEnqNo;
	private GERPTextField tfPjtName, tfClientName, tfPerName, tfPerPhone, tfClentCity, tfCusVisNo, tfPurposeVisit;
	private GERPNumberField tnNoPerson;
	private GERPTextArea taPurposeRe;
	private CheckBox checkBxHODPro, checkBxPlan, checkBxProd, checkBxQC, checkBxMain, checkBxHR, checkBxDie,
			checkBxDes, checkBxAcc;
	private ComboBox cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private FormLayout flHdrCol1, flHdrCol2, flHdrCol3, flHdrCol4;
	private ComboBox cbDtlStatus;
	private FormLayout flDtlCol1, flDtlCol2, flDtlCol3;
	private ComboBox cbSftStatus;
	private FormLayout flInformCol1, flInformCol2, flInformCol3, flInformCol4;
	private ComboBox cbPersonNoStatus;
	private FormLayout flPersonCol1, flPersonCol2;
	private Button btnAddDtls = new GERPButton("Add", "add", this);
	private Button btnAddPerson = new GERPButton("Add", "add", this);
	private Button btnDeletePerson = new GERPButton("Delete", "delete", this);
	private Button btndelete = new GERPButton("Delete", "delete", this);
	private Button btnAddInfo = new GERPButton("Add", "add", this);
	private Button btnDeleteInfo = new GERPButton("Delete", "delete", this);
	private String username;
	private Long companyid;
	private int recordCnt = 0;
	private int recordArmCnt = 0;
	// Initialize logger
	private Logger logger = Logger.getLogger(Roto.class);
	private static final long serialVersionUID = 1L;
	
	public CustomerVisit() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
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
				saveInfoDetails();
			}
		});
		btnSave.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				saveCustMainDetails();
			}
		});
		btnAddPerson.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				savePersonDetails();
				loadInfoPass(false);
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
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddDtls.setCaption("Update");
					btnAddDtls.setStyleName("savebt");
				}
			}
		});
		tblPersonNo = new Table();
		tblPersonNo.setSelectable(true);
		tblPersonNo.setPageLength(7);
		tblPersonNo.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblPersonNo.isSelected(event.getItemId())) {
					tblPersonNo.setImmediate(true);
					btnAddInfo.setCaption("Add");
					btnAddInfo.setStyleName("savebt");
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddInfo.setCaption("Update");
					btnAddInfo.setStyleName("savebt");
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
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddPerson.setCaption("Update");
					btnAddPerson.setStyleName("savebt");
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
					// Delete person details
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
		cbWO.setItemCaptionPropertyId("workOrdrNo");
		tfPurposeVisit = new GERPTextField("Purpose");
		tfPurposeVisit.setWidth("150");
		tfPurposeVisit.setHeight("70");
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
		tfCusVisNo = new GERPTextField("Visit No");
		tfCusVisNo.setWidth("150");
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
		taPurposeRe.setHeight("70");
		tfClentCity = new GERPTextField("Client City");
		tfClentCity.setWidth("150");
		tnNoPerson = new GERPNumberField("No.of Person");
		tnNoPerson.setWidth("150");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting FormPlan UI");
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbDtlStatus.setWidth("150px");
		// Status ComboBox
		cbSftStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbPersonNoStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbPersonNoStatus.setWidth("150px");
		cbSftStatus.setWidth("100px");
		cbHdrStatus.setWidth("150px");
		// Check Box Department.
		checkBxHODPro = new CheckBox("HOD-Prod.");
		checkBxPlan = new CheckBox("Planing");
		checkBxProd = new CheckBox("Production");
		checkBxQC = new CheckBox("QC");
		checkBxMain = new CheckBox("Maintenance");
		checkBxHR = new CheckBox("HR");
		checkBxDie = new CheckBox("Die");
		checkBxDes = new CheckBox("Design");
		checkBxAcc = new CheckBox("Accounts");
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
		// loadCustomerNo();
		btnAddDtls.setStyleName("add");
		btnAddInfo.setStyleName("add");
		btnAddPerson.setStyleName("add");
		loadCustVisitNo(false);
		loadInfoDetails(false);
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
		flHdrCol4 = new FormLayout();
		flHdrCol1.addComponent(tfCusVisNo);
		flHdrCol1.addComponent(dfFormDt);
		flHdrCol1.addComponent(tfPjtName);
		flHdrCol1.addComponent(cbEnqNo);
		flHdrCol2.addComponent(cbWO);
		flHdrCol2.addComponent(dfVisitDt);
		flHdrCol2.addComponent(tfClientName);
		flHdrCol2.addComponent(tfClentCity);
		flHdrCol3.addComponent(tnNoPerson);
		flHdrCol3.addComponent(tfPurposeVisit);
		flHdrCol4.addComponent(taPurposeRe);
		flHdrCol4.addComponent(cbHdrStatus);
		hlHdr = new HorizontalLayout();
		hlHdr.addComponent(flHdrCol1);
		hlHdr.addComponent(flHdrCol2);
		hlHdr.addComponent(flHdrCol3);
		hlHdr.addComponent(flHdrCol4);
		hlHdr.setSpacing(true);
		hlHdr.setMargin(true);
		// Adding Arm Components
		flPersonCol1 = new FormLayout();
		flPersonCol2 = new FormLayout();
		flPersonCol1.addComponent(tfPerName);
		flPersonCol1.addComponent(tfPerPhone);
		flPersonCol2.addComponent(cbPersonNoStatus);
		flPersonCol2.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(btnAddPerson);
				addComponent(btnDeletePerson);
			}
		});
		hlPerson = new HorizontalLayout();
		hlPerson.setSpacing(true);
		hlPerson.setWidth("600");
		hlPerson.addComponent(flPersonCol1);
		hlPerson.addComponent(flPersonCol2);
		hlPerson.setSpacing(true);
		hlPerson.setMargin(true);
		// Adding Shift Components
		flInformCol1 = new FormLayout();
		flInformCol2 = new FormLayout();
		flInformCol3 = new FormLayout();
		flInformCol4 = new FormLayout();
		// flshiftCol1.addComponent(tfshiftname);
		flInformCol1.addComponent(checkBxHODPro);
		flInformCol1.addComponent(checkBxPlan);
		flInformCol1.addComponent(checkBxProd);
		flInformCol2.addComponent(checkBxQC);
		flInformCol2.addComponent(checkBxDie);
		flInformCol2.addComponent(checkBxHR);
		flInformCol3.addComponent(checkBxDes);
		flInformCol3.addComponent(checkBxMain);
		flInformCol3.addComponent(checkBxAcc);
		flInformCol4.addComponent(cbSftStatus);
		flInformCol4.addComponent(btnAddInfo);
		flInformCol4.addComponent(btnDeleteInfo);
		flInformCol4.setComponentAlignment(btnAddInfo, Alignment.BOTTOM_CENTER);
		vlInform = new VerticalLayout();
		vlInform.setSpacing(true);
		hlInform = new HorizontalLayout();
		hlInform.setSpacing(true);
		hlInform.addComponent(flInformCol1);
		hlInform.addComponent(flInformCol2);
		hlInform.addComponent(flInformCol3);
		hlInform.addComponent(flInformCol4);
		vlInform.addComponent(hlInform);
		vlInform.addComponent(tblInfoPass);
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
		vlPerson = new VerticalLayout();
		vlPerson.addComponent(hlPerson);
		vlPerson.addComponent(tblPersonNo);
		hlPerandInfo = new HorizontalLayout();
		hlPerandInfo.addComponent(vlPerson);
		hlPerandInfo.addComponent(GERPPanelGenerator.createPanel(vlInform));
		hlPerandInfo.setSpacing(true);
		hlHdrMain = new HorizontalLayout();
		hlHdrMain.addComponent(hlHdr);
		vlHdrandDetail = new VerticalLayout();
		vlHdrandDetail.addComponent(GERPPanelGenerator.createPanel(hlHdrMain));
		vlHdrandDetail.addComponent(GERPPanelGenerator.createPanel(hlPerandInfo));
		vlHdrandDetail.setSpacing(true);
		vlHdrandDetail.setWidth("100%");
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.addComponent(vlHdrandDetail);
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		tblMstScrSrchRslt.setPageLength(14);
		List<CustomerVisitHdrDM> customervisitHdrList = new ArrayList<CustomerVisitHdrDM>();
		customervisitHdrList = serviceCustomerVisitHdr.getCustomerVisitHdrList(null, null, null, null, null, "F");
		recordCnt = customervisitHdrList.size();
		beanCustomerVisitHdrDM = new BeanItemContainer<CustomerVisitHdrDM>(CustomerVisitHdrDM.class);
		beanCustomerVisitHdrDM.addAll(customervisitHdrList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Got the Customer Visit result set");
		tblMstScrSrchRslt.setContainerDataSource(beanCustomerVisitHdrDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "cusVisId", "enquiryNo", "visitDt", "custName", "custCity",
				"custHdrStatus", "lastUpdateddt", "lastUpdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Visit Id", "Enquiry No", "Visit Date", "Customer", "City",
				"Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("cusVisId", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
	}
	
	private void loadInfoPass(Boolean fromdb) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		List<CustomerVisitInfoDtlDM> customervisitnodtlList = new ArrayList<CustomerVisitInfoDtlDM>();
		recordArmCnt = customervisitnodtlList.size();
		customervisitnodtlList = serviceCustomerVisitInfoDtl.getCustomerVisitInfoDtlList(null, null, null, null, null,
				null, null, null, null, null, null, null, "F");
		beanCustomerVisitInfoDtlDM = new BeanItemContainer<CustomerVisitInfoDtlDM>(CustomerVisitInfoDtlDM.class);
		beanCustomerVisitInfoDtlDM.addAll(customervisitnodtlList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the No. of person List");
		tblInfoPass.setContainerDataSource(beanCustomerVisitInfoDtlDM);
		tblInfoPass.setVisibleColumns(new Object[] { "hodPo", "plan", "prod", "qc", "die", "hr", "design",
				"maintenance", "accounts", "status" });
		tblInfoPass.setColumnHeaders(new String[] { "HOD Prod", "Plan", "Prod", "QC", "Die", "HR", "Design",
				"Maintenance", "Accounts", "Status" });
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
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assembleSearchLayout();
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
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
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
		loadWorkOrderNo();
		try {
			Long clientid = serviceEnquiryHdr
					.getSmsEnqHdrList(null, Long.valueOf(cbEnqNo.getValue().toString()), null, null, null, "F", null,
							null).get(0).getClientId();
			tfClientName.setValue(serviceClients
					.getClientDetails(companyid, clientid, null, null, null, null, null, null, "Active", "F").get(0)
					.getClientfullname().toString());
			tfClentCity.setValue(serviceClients
					.getClientDetails(companyid, clientid, null, null, null, null, null, null, "Active", "F").get(0)
					.getCityName().toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load Clients Details " + e);
		}
	}
	
	// Load Work Order List
	private void loadWorkOrderNo() {
		/*
		 * Long tenquiryId = serviceEnquiryHdr .getSmsEnqHdrList(null, Long.valueOf(cbEnqNo.getValue().toString()),
		 * null, null, null, "F", null, null) .get(0).getEnquiryId();
		 * System.out.println("===============+++================================>" + tenquiryId);
		 */
		BeanItemContainer<WorkOrderHdrDM> beanWrkOrdHdr = new BeanItemContainer<WorkOrderHdrDM>(WorkOrderHdrDM.class);
		beanWrkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, null, null, null, null, "F",
				null, null, null, null));
		cbWO.setContainerDataSource(beanWrkOrdHdr);
	}
	
	/*
	 * Save Meathods
	 */
	// Customer Visit Main Save
	private void saveCustMainDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			CustomerVisitHdrDM customervisitHdrObj = new CustomerVisitHdrDM();
			customervisitHdrObj.setCusVisNo(tfCusVisNo.getValue().toString());
			customervisitHdrObj.setFillDt(dfFormDt.getValue());
			customervisitHdrObj.setVisitDt(dfVisitDt.getValue());
			customervisitHdrObj.setProjectName(tfPjtName.getValue().toString());
			customervisitHdrObj.setEnquiryNo(cbEnqNo.getValue().toString());
			customervisitHdrObj.setWorkorderNo(cbWO.getValue().toString());
			customervisitHdrObj.setCustName(tfClientName.getValue().toString());
			customervisitHdrObj.setCustCity(tfClentCity.getValue().toString());
			customervisitHdrObj.setPurposeVisit(tfPurposeVisit.getValue().toString());
			customervisitHdrObj.setRemarks(taPurposeRe.getValue().toString());
			customervisitHdrObj.setPersonNo(tnNoPerson.getValue().toString());
			if (cbPersonNoStatus.getValue() != null) {
				customervisitHdrObj.setCustHdrStatus((String) cbHdrStatus.getValue());
			}
			customervisitHdrObj.setLastUpdatedby(username);
			customervisitHdrObj.setLastUpdateddt(DateUtils.getcurrentdate());
			try {
				@SuppressWarnings("unchecked")
				Collection<CustomerVisitNoDtlDM> customerDtls = ((Collection<CustomerVisitNoDtlDM>) tblPersonNo
						.getVisibleItemIds());
				for (CustomerVisitNoDtlDM visitNoDtlDM : (Collection<CustomerVisitNoDtlDM>) customerDtls) {
					visitNoDtlDM.setCusVisId(customervisitHdrObj.getCusVisId());
					serviceCustomerVisitNoDtl.saveorUpdateCustomerVisitNoDtlDetails(visitNoDtlDM);
				}
			}
			catch (Exception e) {
			}
			try {
				@SuppressWarnings("unchecked")
				Collection<CustomerVisitInfoDtlDM> itemids = ((Collection<CustomerVisitInfoDtlDM>) tblPersonNo
						.getVisibleItemIds());
				for (CustomerVisitInfoDtlDM visitInfoDtlDM : (Collection<CustomerVisitInfoDtlDM>) itemids) {
					visitInfoDtlDM.setCusVisId(customervisitHdrObj.getCusVisId());
					serviceCustomerVisitInfoDtl.saveorUpdateCustomerVisitInfoDtlDetails(visitInfoDtlDM);
				}
			}
			catch (Exception e) {
			}
			cusMainDetailsresetField();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Person Details Save
	private void savePersonDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			CustomerVisitNoDtlDM customervisitnoDtlObj = new CustomerVisitNoDtlDM();
			customervisitnoDtlObj.setPersonName(tfClientName.getValue());
			customervisitnoDtlObj.setContactNo(tfPerPhone.getValue());
			if (cbPersonNoStatus.getValue() != null) {
				customervisitnoDtlObj.setCustNoDtlStatus((String) cbPersonNoStatus.getValue());
			}
			customervisitnoDtlObj.setLastUpdatedby(username);
			customervisitnoDtlObj.setLastUpdateddt(DateUtils.getcurrentdate());
			listCustVisitNo.add(customervisitnoDtlObj);
			loadCustVisitNo(false);
			persondetailsresetField();
			btnAddPerson.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Information Pass Save
	private void saveInfoDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			CustomerVisitInfoDtlDM customervisitinfoDtlObj = new CustomerVisitInfoDtlDM();
			if (checkBxDes.getValue() != null) {
				customervisitinfoDtlObj.setDesign("Active");
			}
			if (checkBxDie.getValue() != null) {
				customervisitinfoDtlObj.setDie("Active");
			}
			if (checkBxHODPro.getValue() != null) {
				customervisitinfoDtlObj.setHodPo("Active");
			}
			if (checkBxHR.getValue() != null) {
				customervisitinfoDtlObj.setHr("Active");
			}
			if (checkBxMain.getValue() != null) {
				customervisitinfoDtlObj.setMaintenance("Active");
			}
			if (checkBxPlan.getValue() != null) {
				customervisitinfoDtlObj.setPlan("Active");
			}
			if (checkBxProd.getValue() != null) {
				customervisitinfoDtlObj.setProd("Active");
			}
			if (checkBxQC.getValue() != null) {
				customervisitinfoDtlObj.setQc("Active");
			}
			if (checkBxAcc.getValue() != null) {
				customervisitinfoDtlObj.setAccounts("Active");
			}
			if (cbPersonNoStatus.getValue() != null) {
				customervisitinfoDtlObj.setCustInfodTLStatus((String) cbPersonNoStatus.getValue());
			}
			customervisitinfoDtlObj.setLastUpdatedby(username);
			customervisitinfoDtlObj.setLastUpdateddt(DateUtils.getcurrentdate());
			listCustVisitInfo.add(customervisitinfoDtlObj);
			loadInfoDetails(false);
			infoDetailsresetField();
			btnAddInfo.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Load Tables
	 */
	// Load Person Details Table.
	private void loadCustVisitNo(Boolean fromdb) {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			beanCustomerVisitNoDtlDM = new BeanItemContainer<CustomerVisitNoDtlDM>(CustomerVisitNoDtlDM.class);
			beanCustomerVisitNoDtlDM.addAll(listCustVisitNo);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the IndentIssueslap. result set");
			if (fromdb) {
				listCustVisitNo = serviceCustomerVisitNoDtl.getCustomerVisitNoDtlList(null, null, null, "F");
			}
			beanCustomerVisitNoDtlDM.addAll(listCustVisitNo);
			tblPersonNo.setContainerDataSource(beanCustomerVisitNoDtlDM);
			tblPersonNo.setVisibleColumns(new Object[] { "personName", "contactNo", "custNoDtlStatus", "lastUpdateddt",
					"lastUpdatedby" });
			tblPersonNo
					.setColumnHeaders(new String[] { "Name", "Contact No.", "Status", "Updated Date", "Updated By" });
			tblPersonNo.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
			tblPersonNo.setPageLength(10);
			persondetailsresetField();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Contact Information Table
	private void loadInfoDetails(Boolean fromdb) {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			recordCnt = listCustVisitNo.size();
			beanCustomerVisitInfoDtlDM = new BeanItemContainer<CustomerVisitInfoDtlDM>(CustomerVisitInfoDtlDM.class);
			if (fromdb) {
				listCustVisitInfo = serviceCustomerVisitInfoDtl.getCustomerVisitInfoDtlList(null, null, null, null,
						null, null, null, null, null, null, null, null, "F");
			}
			beanCustomerVisitInfoDtlDM.addAll(listCustVisitInfo);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the IndentIssueslap. result set");
			tblInfoPass.setContainerDataSource(beanCustomerVisitInfoDtlDM);
			tblInfoPass.setVisibleColumns(new Object[] { "hodPo", "plan", "prod", "qc", "die", "hr", "accounts",
					"design", "maintenance", "custInfodTLStatus", "lastUpdateddt", "lastUpdatedby" });
			tblInfoPass.setColumnHeaders(new String[] { "HOD Prod", "Plan", "Prod", "QC", "Die", "HR", "Accounts",
					"Design", "Maintenance", "Status", "Updated Date", "Updated By" });
			tblInfoPass.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
			tblInfoPass.setPageLength(10);
			infoDetailsresetField();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Reset Fields
	 */
	// Customer Main Reset
	private void cusMainDetailsresetField() {
		tfCusVisNo.setValue(null);
		dfFormDt.setValue(DateUtils.getcurrentdate());
		dfVisitDt.setValue(null);
		cbEnqNo.setValue(null);
		cbWO.setValue(null);
		tfClientName.setReadOnly(false);
		tfClentCity.setReadOnly(false);
		tfClientName.setValue(null);
		tfClentCity.setValue(null);
		tfClientName.setReadOnly(true);
		tfClentCity.setReadOnly(true);
		tfPurposeVisit.setValue(null);
		taPurposeRe.setValue(null);
		tnNoPerson.setValue(null);
		cbHdrStatus.setValue(null);
	}
	
	// Reset Person Details People.
	private void persondetailsresetField() {
		tfPerName.setValue(" ");
		tfPerPhone.setValue(" ");
		cbPersonNoStatus.setValue(null);
	}
	
	// Reset Information Details People.
	private void infoDetailsresetField() {
		checkBxHODPro.setValue(null);
		checkBxPlan.setValue(null);
		checkBxProd.setValue(null);
		checkBxQC.setValue(null);
		checkBxMain.setValue(null);
		checkBxHR.setValue(null);
		checkBxDie.setValue(null);
		checkBxDes.setValue(null);
		checkBxAcc.setValue(null);
		cbPersonNoStatus.setValue(null);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
	}
}
