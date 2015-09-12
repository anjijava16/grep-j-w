package com.gnts.sms.txn;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.SlnoGenService;
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
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.tool.EmailTrigger;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.txn.WorkOrderHdrDM;
import com.gnts.mfg.service.txn.WorkOrderHdrService;
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
import com.vaadin.server.UserError;
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
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private CustomerVisitInfoDtlService serviceCustomerVisitInfoDtl = (CustomerVisitInfoDtlService) SpringContextHelper
			.getBean("customervisitinfodtl");
	private CustomerVisitHdrService serviceCustomerVisitHdr = (CustomerVisitHdrService) SpringContextHelper
			.getBean("customervisithdr");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private WorkOrderHdrService serviceWorkOrderHdr = (WorkOrderHdrService) SpringContextHelper.getBean("workOrderHdr");
	private SmsEnqHdrService serviceEnquiryHdr = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	// User Input Components for Work Order Details
	private BeanItemContainer<CustomerVisitHdrDM> beanCustomerVisitHdrDM = null;
	private BeanItemContainer<CustomerVisitNoDtlDM> beanCustomerVisitNoDtlDM = null;
	private BeanItemContainer<CustomerVisitInfoDtlDM> beanCustomerVisitInfoDtlDM = null;
	private List<CustomerVisitNoDtlDM> listCustVisitNo = new ArrayList<CustomerVisitNoDtlDM>();
	private List<CustomerVisitInfoDtlDM> listCustVisitInfo = new ArrayList<CustomerVisitInfoDtlDM>();
	private Table tblPersonNo, tblInfoPass;
	// Search Control Layout
	private HorizontalLayout hlHdr = new HorizontalLayout();
	private HorizontalLayout hlSearchLayout, hlPerandInfo, hlHdrMain, hlPerson, hlInform, hlHdrslap;
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private VerticalLayout vlInform, vlPerson, vlHdrandDetail;
	// Data Fields
	private GERPPopupDateField dfVisitDt, dfFormDt;
	private GERPComboBox cbWorkorder, cbEnqNo;
	private GERPTextField tfPjtName, tfClientName, tfPerName, tfPerPhone, tfClentCity, tfCusVisNo;
	private GERPNumberField tnNoPerson;
	private GERPTextArea taPurposeRe, taPurposeVisit;
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
	private Long companyid, visitHdrId, moduleId, branchId;
	private int recordCnt = 0;
	// Initialize logger
	private Logger logger = Logger.getLogger(CustomerVisit.class);
	private static final long serialVersionUID = 1L;
	
	public CustomerVisit() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside CustomerVisit() constructor");
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
		btnAddPerson.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				savePersonDetails();
				loadCustVisitNo(false);
			}
		});
		tblPersonNo = new Table();
		tblPersonNo.setSelectable(true);
		tblPersonNo.setPageLength(6);
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
					editPersonDetails();
				}
			}
		});
		tblInfoPass = new GERPTable();
		tblInfoPass.setPageLength(5);
		tblInfoPass.setFooterVisible(false);
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
					editVisitInfoDetails();
				}
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
		dfFormDt = new GERPPopupDateField("Date");
		dfFormDt.setRequired(true);
		dfFormDt.setDateFormat("dd-MMM-yyyy");
		dfFormDt.setInputPrompt("Select Date");
		cbWorkorder = new GERPComboBox("Work Order");
		cbWorkorder.setItemCaptionPropertyId("workOrdrNo");
		taPurposeVisit = new GERPTextArea("Purpose");
		taPurposeVisit.setHeight("70");
		cbEnqNo = new GERPComboBox("Enquiry No.");
		cbEnqNo.setItemCaptionPropertyId("enquiryNo");
		loadEnquiryNo();
		cbEnqNo.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemId = event.getProperty().getValue();
				BeanItem<?> item = (BeanItem<?>) cbEnqNo.getItem(itemId);
				if (item != null) {
					if (cbEnqNo.getValue() != null) {
						loadClientDetails();
						loadWorkOrderNo();
					}
				}
			}
		});
		tfCusVisNo = new GERPTextField("Visit No");
		tfCusVisNo.setRequired(true);
		tfPjtName = new GERPTextField("Project Name");
		tfClientName = new GERPTextField("Cilent Name");
		tfPerName = new GERPTextField("Person Name");
		tfPerPhone = new GERPTextField("Contact");
		taPurposeRe = new GERPTextArea("Remarks");
		taPurposeRe.setHeight("70");
		tfClentCity = new GERPTextField("Client City");
		tnNoPerson = new GERPNumberField("No.of Person");
		tnNoPerson.setRequired(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting FormPlan UI");
		cbDtlStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbDtlStatus.setWidth("150px");
		// Status ComboBox
		cbSftStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbPersonNoStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbHdrStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
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
		flHdrCol2.addComponent(cbWorkorder);
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
		hlSearchLayout.removeAllComponents();
		flHdrCol1 = new FormLayout();
		flHdrCol2 = new FormLayout();
		flHdrCol3 = new FormLayout();
		flHdrCol4 = new FormLayout();
		flHdrCol1.addComponent(tfCusVisNo);
		flHdrCol1.addComponent(dfFormDt);
		flHdrCol1.addComponent(tfPjtName);
		flHdrCol1.addComponent(cbEnqNo);
		flHdrCol2.addComponent(cbWorkorder);
		flHdrCol2.addComponent(dfVisitDt);
		flHdrCol2.addComponent(tfClientName);
		flHdrCol2.addComponent(tfClentCity);
		flHdrCol3.addComponent(tnNoPerson);
		flHdrCol3.addComponent(taPurposeVisit);
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
		hlPerson.setWidth("500");
		hlPerson.addComponent(flPersonCol1);
		hlPerson.addComponent(flPersonCol2);
		hlPerson.setSpacing(true);
		hlPerson.setMargin(true);
		// Adding Shift Components
		flInformCol1 = new FormLayout();
		flInformCol2 = new FormLayout();
		flInformCol3 = new FormLayout();
		flInformCol4 = new FormLayout();
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
		vlInform.setWidth("800");
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
		hlPerandInfo.addComponent(vlInform);
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
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			tblMstScrSrchRslt.setPageLength(14);
			List<CustomerVisitHdrDM> list = new ArrayList<CustomerVisitHdrDM>();
			list = serviceCustomerVisitHdr.getCustomerVisitHdrList(companyid, branchId, null, null, null, null, null,
					"F");
			recordCnt = list.size();
			beanCustomerVisitHdrDM = new BeanItemContainer<CustomerVisitHdrDM>(CustomerVisitHdrDM.class);
			beanCustomerVisitHdrDM.addAll(list);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the Customer Visit result set");
			tblMstScrSrchRslt.setContainerDataSource(beanCustomerVisitHdrDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "cusVisId", "cusVisNo", "visitDt", "custName",
					"custCity", "personNo", "purposeVisit", "custHdrStatus", "lastUpdateddt", "lastUpdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Visit Id", "Ref. No", "Visit Date", "Customer", "City",
					"No of Persons", "Purpose", "Status", "Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("cusVisId", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editCustVisitHdrDetails() {
		CustomerVisitHdrDM customerVisitHdrDM = beanCustomerVisitHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		visitHdrId = customerVisitHdrDM.getCusVisId();
		tfCusVisNo.setReadOnly(false);
		tfCusVisNo.setValue(customerVisitHdrDM.getCusVisNo());
		tfCusVisNo.setReadOnly(true);
		dfFormDt.setValue(customerVisitHdrDM.getFillDt());
		tfPjtName.setValue(customerVisitHdrDM.getProjectName());
		cbEnqNo.setValue(customerVisitHdrDM.getEnquiryId());
		cbWorkorder.setValue(customerVisitHdrDM.getWorkorderId());
		dfVisitDt.setValue(customerVisitHdrDM.getVisitDt1());
		tnNoPerson.setValue(customerVisitHdrDM.getPersonNo());
		taPurposeVisit.setValue(customerVisitHdrDM.getPurposeVisit());
		taPurposeRe.setValue(customerVisitHdrDM.getRemarks());
		cbHdrStatus.setValue(customerVisitHdrDM.getCustHdrStatus());
		loadCustVisitNo(true);
		loadInfoDetails(true);
	}
	
	private void editPersonDetails() {
		try {
			CustomerVisitNoDtlDM visitNoDtlDM = beanCustomerVisitNoDtlDM.getItem(tblPersonNo.getValue()).getBean();
			tfPerName.setValue(visitNoDtlDM.getPersonName());
			tfPerPhone.setValue(visitNoDtlDM.getContactNo());
			cbPersonNoStatus.setValue(visitNoDtlDM.getCustNoDtlStatus());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editVisitInfoDetails() {
		CustomerVisitInfoDtlDM visitInfoDtlDM = beanCustomerVisitInfoDtlDM.getItem(tblInfoPass.getValue()).getBean();
		if (visitInfoDtlDM.getDesign() != null && visitInfoDtlDM.getDesign().equalsIgnoreCase("Active")) {
			checkBxDes.setValue(true);
		} else {
			checkBxDes.setValue(false);
		}
		if (visitInfoDtlDM.getDie() != null && visitInfoDtlDM.getDie().equalsIgnoreCase("Active")) {
			checkBxDie.setValue(true);
		} else {
			checkBxDie.setValue(false);
		}
		if (visitInfoDtlDM.getHodPo() != null && visitInfoDtlDM.getHodPo().equalsIgnoreCase("Active")) {
			checkBxHODPro.setValue(true);
		} else {
			checkBxHODPro.setValue(false);
		}
		if (visitInfoDtlDM.getHr() != null && visitInfoDtlDM.getHr().equalsIgnoreCase("Active")) {
			checkBxHR.setValue(true);
		} else {
			checkBxHR.setValue(false);
		}
		if (visitInfoDtlDM.getMaintenance() != null && visitInfoDtlDM.getMaintenance().equalsIgnoreCase("Active")) {
			checkBxMain.setValue(true);
		} else {
			checkBxMain.setValue(false);
		}
		if (visitInfoDtlDM.getPlan() != null && visitInfoDtlDM.getPlan().equalsIgnoreCase("Active")) {
			checkBxPlan.setValue(true);
		} else {
			checkBxPlan.setValue(false);
		}
		if (visitInfoDtlDM.getProd() != null && visitInfoDtlDM.getProd().equalsIgnoreCase("Active")) {
			checkBxProd.setValue(true);
		} else {
			checkBxProd.setValue(false);
		}
		if (visitInfoDtlDM.getQc() != null && visitInfoDtlDM.getQc().equalsIgnoreCase("Active")) {
			checkBxQC.setValue(true);
		} else {
			checkBxQC.setValue(false);
		}
		if (visitInfoDtlDM.getAccounts() != null && visitInfoDtlDM.getAccounts().equalsIgnoreCase("Active")) {
			checkBxAcc.setValue(true);
		} else {
			checkBxAcc.setValue(false);
		}
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
		tblPersonNo.setVisible(true);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, moduleId, "SM_CUSVISIT").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfCusVisNo.setReadOnly(false);
				tfCusVisNo.setValue(slnoObj.getKeyDesc());
				tfCusVisNo.setReadOnly(true);
			} else {
				tfCusVisNo.setReadOnly(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
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
		editCustVisitHdrDetails();
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
			parameterMap.put("VISITID", visitHdrId);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/clientvisit"); // clientvisit is the name of my jasper
			rpt.callReport(basepath, "Preview");
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
		try {
			BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
			beansmsenqHdr.setBeanIdProperty("enquiryId");
			beansmsenqHdr
					.addAll(serviceEnquiryHdr.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
			cbEnqNo.setContainerDataSource(beansmsenqHdr);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Client Details
	private void loadClientDetails() {
		try {
			Long clientid = serviceEnquiryHdr
					.getSmsEnqHdrList(null, Long.valueOf(cbEnqNo.getValue().toString()), null, null, null, "P", null,
							null).get(0).getClientId();
			ClientDM clientDM = serviceClients.getClientDetails(companyid, clientid, null, null, null, null, null,
					null, "Active", "F").get(0);
			tfClientName.setValue(clientDM.getClientName());
			tfClentCity.setValue(clientDM.getCityName());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("load Clients Details " + e);
		}
	}
	
	// Load Work Order List
	private void loadWorkOrderNo() {
		try {
			BeanContainer<Long, WorkOrderHdrDM> beanWrkOrdHdr = new BeanContainer<Long, WorkOrderHdrDM>(
					WorkOrderHdrDM.class);
			beanWrkOrdHdr.setBeanIdProperty("workOrdrId");
			beanWrkOrdHdr.addAll(serviceWorkOrderHdr.getWorkOrderHDRList(companyid, null, null, null, null, null, "P",
					null, (Long) cbEnqNo.getValue(), null, null, null));
			cbWorkorder.setContainerDataSource(beanWrkOrdHdr);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * Save Meathods
	 */
	// Customer Visit Main Save
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			CustomerVisitHdrDM customerVisitHdrDM = new CustomerVisitHdrDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				customerVisitHdrDM = beanCustomerVisitHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			tfCusVisNo.setReadOnly(false);
			customerVisitHdrDM.setCusVisNo(tfCusVisNo.getValue());
			tfCusVisNo.setReadOnly(true);
			customerVisitHdrDM.setFillDt(dfFormDt.getValue());
			customerVisitHdrDM.setVisitDt(dfVisitDt.getValue());
			customerVisitHdrDM.setProjectName(tfPjtName.getValue());
			if (cbEnqNo.getValue() != null) {
				customerVisitHdrDM.setEnquiryId((Long) cbEnqNo.getValue());
			}
			if (cbWorkorder.getValue() != null) {
				customerVisitHdrDM.setWorkorderId((Long) cbWorkorder.getValue());
			}
			customerVisitHdrDM.setCustName(tfClientName.getValue());
			customerVisitHdrDM.setCustCity(tfClentCity.getValue());
			customerVisitHdrDM.setPurposeVisit(taPurposeVisit.getValue());
			customerVisitHdrDM.setRemarks(taPurposeRe.getValue());
			customerVisitHdrDM.setPersonNo(tnNoPerson.getValue());
			if (cbPersonNoStatus.getValue() != null) {
				customerVisitHdrDM.setCustHdrStatus((String) cbHdrStatus.getValue());
			}
			customerVisitHdrDM.setCompanyId(companyid);
			customerVisitHdrDM.setBranchId(branchId);
			customerVisitHdrDM.setLastUpdatedby(username);
			customerVisitHdrDM.setLastUpdateddt(DateUtils.getcurrentdate());
			serviceCustomerVisitHdr.saveorUpdateCustomerVisitHdrDetails(customerVisitHdrDM);
			try {
				@SuppressWarnings("unchecked")
				Collection<CustomerVisitNoDtlDM> customerDtls = ((Collection<CustomerVisitNoDtlDM>) tblPersonNo
						.getVisibleItemIds());
				for (CustomerVisitNoDtlDM visitNoDtlDM : (Collection<CustomerVisitNoDtlDM>) customerDtls) {
					visitNoDtlDM.setCusVisId(customerVisitHdrDM.getCusVisId());
					serviceCustomerVisitNoDtl.saveorUpdateCustomerVisitNoDtlDetails(visitNoDtlDM);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				@SuppressWarnings("unchecked")
				Collection<CustomerVisitInfoDtlDM> itemids = ((Collection<CustomerVisitInfoDtlDM>) tblInfoPass
						.getVisibleItemIds());
				for (CustomerVisitInfoDtlDM visitInfoDtlDM : (Collection<CustomerVisitInfoDtlDM>) itemids) {
					visitInfoDtlDM.setCusVisId(customerVisitHdrDM.getCusVisId());
					serviceCustomerVisitInfoDtl.saveorUpdateCustomerVisitInfoDtlDetails(visitInfoDtlDM);
					String messageBody = "Hi sir/Madam,\nTitle : Client visit\n Date : "
							+ customerVisitHdrDM.getVisitDt() + "\n Client Name : " + customerVisitHdrDM.getCustName()
							+ "\n Purpose : " + customerVisitHdrDM.getPurposeVisit();
					String messageHdr = "Reg : Client Visit on " + customerVisitHdrDM.getVisitDt();
					// To Do : Lookup the mail id s from Lookup table or properties file.
					if (visitInfoDtlDM.getDie() != null && visitInfoDtlDM.getDie().equalsIgnoreCase("Active")) {
						String dieMailId = serviceCompanyLookup
								.getCompanyLookUpByLookUp(companyid, null, "Active", "SMS_DIE").get(0).getLookupname();
						try {
							new EmailTrigger(dieMailId, messageBody, messageHdr);
						}
						catch (Exception e) {
						}
					}
					if (visitInfoDtlDM.getHodPo() != null && visitInfoDtlDM.getHodPo().equalsIgnoreCase("Active")) {
						String prodHodMailId = serviceCompanyLookup
								.getCompanyLookUpByLookUp(companyid, null, "Active", "SMS_PROD_HOD").get(0)
								.getLookupname();
						try {
							new EmailTrigger(prodHodMailId, messageBody, messageHdr);
						}
						catch (Exception e) {
						}
					}
					if (visitInfoDtlDM.getHr() != null && visitInfoDtlDM.getHr().equalsIgnoreCase("Active")) {
						String hrMailId = serviceCompanyLookup
								.getCompanyLookUpByLookUp(companyid, null, "Active", "SMS_HR").get(0).getLookupname();
						try {
							new EmailTrigger(hrMailId, messageBody, messageHdr);
						}
						catch (Exception e) {
						}
					}
					if (visitInfoDtlDM.getDesign() != null && visitInfoDtlDM.getDesign().equalsIgnoreCase("Active")) {
						String designMailId = serviceCompanyLookup
								.getCompanyLookUpByLookUp(companyid, null, "Active", "SMS_DESIGN").get(0)
								.getLookupname();
						try {
							new EmailTrigger(designMailId, messageBody, messageHdr);
						}
						catch (Exception e) {
						}
					}
					if (visitInfoDtlDM.getPlan() != null && visitInfoDtlDM.getPlan().equalsIgnoreCase("Active")) {
						String planningMailId = serviceCompanyLookup
								.getCompanyLookUpByLookUp(companyid, null, "Active", "SMS_PLANNING").get(0)
								.getLookupname();
						try {
							new EmailTrigger(planningMailId, messageBody, messageHdr);
						}
						catch (Exception e) {
						}
					}
					if (visitInfoDtlDM.getProd() != null && visitInfoDtlDM.getProd().equalsIgnoreCase("Active")) {
						String prodMailId = serviceCompanyLookup
								.getCompanyLookUpByLookUp(companyid, null, "Active", "SMS_PROD").get(0).getLookupname();
						try {
							new EmailTrigger(prodMailId, messageBody, messageHdr);
						}
						catch (Exception e) {
						}
					}
					if (visitInfoDtlDM.getQc() != null && visitInfoDtlDM.getQc().equalsIgnoreCase("Active")) {
						String qcMailId = serviceCompanyLookup
								.getCompanyLookUpByLookUp(companyid, null, "Active", "SMS_QC").get(0).getLookupname();
						try {
							new EmailTrigger(qcMailId, messageBody, messageHdr);
						}
						catch (Exception e) {
						}
					}
					if (visitInfoDtlDM.getMaintenance() != null
							&& visitInfoDtlDM.getMaintenance().equalsIgnoreCase("Active")) {
						String maintMailId = serviceCompanyLookup
								.getCompanyLookUpByLookUp(companyid, null, "Active", "SMS_MAINT").get(0)
								.getLookupname();
						try {
							new EmailTrigger(maintMailId, messageBody, messageHdr);
						}
						catch (Exception e) {
						}
					}
					if (visitInfoDtlDM.getAccounts() != null && visitInfoDtlDM.getAccounts().equalsIgnoreCase("Active")) {
						String accountsMailId = serviceCompanyLookup
								.getCompanyLookUpByLookUp(companyid, null, "Active", "SMS_ACCOUNTS").get(0)
								.getLookupname();
						try {
							new EmailTrigger(accountsMailId, messageBody, messageHdr);
						}
						catch (Exception e) {
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			resetFields();
			if (tblMstScrSrchRslt.getValue() == null) {
				try {
					SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_CUSVISIT")
							.get(0);
					if (slnoObj.getAutoGenYN().equals("Y")) {
						serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "SM_CUSVISIT");
					}
				}
				catch (Exception e) {
				}
			}
			tfCusVisNo.setReadOnly(false);
			tfCusVisNo.setValue(customerVisitHdrDM.getCusVisNo().toString());
			tfCusVisNo.setReadOnly(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Person Details Save
	private void savePersonDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		try {
			CustomerVisitNoDtlDM visitNoDtlDM = new CustomerVisitNoDtlDM();
			if (tblPersonNo.getValue() != null) {
				visitNoDtlDM = beanCustomerVisitNoDtlDM.getItem(tblPersonNo.getValue()).getBean();
				listCustVisitNo.remove(visitNoDtlDM);
			}
			visitNoDtlDM.setPersonName(tfPerName.getValue());
			visitNoDtlDM.setContactNo(tfPerPhone.getValue());
			if (cbPersonNoStatus.getValue() != null) {
				visitNoDtlDM.setCustNoDtlStatus((String) cbPersonNoStatus.getValue());
			}
			visitNoDtlDM.setLastUpdatedby(username);
			visitNoDtlDM.setLastUpdateddt(DateUtils.getcurrentdate());
			listCustVisitNo.add(visitNoDtlDM);
			loadCustVisitNo(false);
			resetPersonDetails();
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
			CustomerVisitInfoDtlDM visitInfoDtlDM = new CustomerVisitInfoDtlDM();
			if (tblInfoPass.getValue() != null) {
				visitInfoDtlDM = beanCustomerVisitInfoDtlDM.getItem(tblInfoPass.getValue()).getBean();
				listCustVisitInfo.remove(visitInfoDtlDM);
			}
			if (checkBxDes.getValue() != null) {
				visitInfoDtlDM.setDesign("Active");
			}
			if (checkBxDie.getValue() != null) {
				visitInfoDtlDM.setDie("Active");
			}
			if (checkBxHODPro.getValue() != null) {
				visitInfoDtlDM.setHodPo("Active");
			}
			if (checkBxHR.getValue() != null) {
				visitInfoDtlDM.setHr("Active");
			}
			if (checkBxMain.getValue() != null) {
				visitInfoDtlDM.setMaintenance("Active");
			}
			if (checkBxPlan.getValue() != null) {
				visitInfoDtlDM.setPlan("Active");
			}
			if (checkBxProd.getValue() != null) {
				visitInfoDtlDM.setProd("Active");
			}
			if (checkBxQC.getValue() != null) {
				visitInfoDtlDM.setQc("Active");
			}
			if (checkBxAcc.getValue() != null) {
				visitInfoDtlDM.setAccounts("Active");
			}
			if (cbPersonNoStatus.getValue() != null) {
				visitInfoDtlDM.setCustInfodTLStatus((String) cbPersonNoStatus.getValue());
			}
			visitInfoDtlDM.setLastUpdatedby(username);
			visitInfoDtlDM.setLastUpdateddt(DateUtils.getcurrentdate());
			listCustVisitInfo.add(visitInfoDtlDM);
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
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the IndentIssueslap. result set");
			if (fromdb) {
				listCustVisitNo = serviceCustomerVisitNoDtl.getCustomerVisitNoDtlList(null, visitHdrId, null, "F");
			}
			beanCustomerVisitNoDtlDM.addAll(listCustVisitNo);
			tblPersonNo.setContainerDataSource(beanCustomerVisitNoDtlDM);
			tblPersonNo.setVisibleColumns(new Object[] { "personName", "contactNo", "custNoDtlStatus" });
			tblPersonNo.setColumnHeaders(new String[] { "Name", "Contact No.", "Status" });
			tblPersonNo.setColumnFooter("lastUpdatedby", "No.of Records : " + recordCnt);
			tblPersonNo.setPageLength(10);
			tblPersonNo.setColumnWidth("personName", 200);
			tblPersonNo.setColumnWidth("contactNo", 150);
			tblPersonNo.setColumnWidth("custNoDtlStatus", 100);
			resetPersonDetails();
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
				listCustVisitInfo = serviceCustomerVisitInfoDtl.getCustomerVisitInfoDtlList(null, visitHdrId, null,
						null, null, null, null, null, null, null, null, null, "F");
			}
			beanCustomerVisitInfoDtlDM.addAll(listCustVisitInfo);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the IndentIssueslap. result set");
			tblInfoPass.setContainerDataSource(beanCustomerVisitInfoDtlDM);
			tblInfoPass.setVisibleColumns(new Object[] { "hodPo", "plan", "prod", "qc", "die", "hr", "accounts",
					"design", "maintenance" });
			tblInfoPass.setColumnHeaders(new String[] { "HOD", "Plan", "Prod", "QC", "Die", "HR", "A/C", "Dsn.",
					"Main." });
			infoDetailsresetField();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Reset Person Details People.
	private void resetPersonDetails() {
		tfPerName.setValue("");
		tfPerPhone.setValue("");
		cbPersonNoStatus.setValue("Active");
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
		tfCusVisNo.setReadOnly(false);
		tfCusVisNo.setComponentError(null);
		tfCusVisNo.setReadOnly(true);
		dfFormDt.setComponentError(null);
		tnNoPerson.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		tfCusVisNo.setReadOnly(false);
		if (tfCusVisNo.getValue() == null || tfCusVisNo.getValue().trim().length() == 0) {
			tfCusVisNo.setComponentError(new UserError(GERPErrorCodes.REQUIRED));
			errorFlag = true;
		}
		tfCusVisNo.setReadOnly(true);
		if (dfFormDt.getValue() == null) {
			dfFormDt.setComponentError(new UserError(GERPErrorCodes.REQUIRED));
			errorFlag = true;
		}
		if (tnNoPerson.getValue() == null || tnNoPerson.getValue().trim().length() == 0) {
			tnNoPerson.setComponentError(new UserError(GERPErrorCodes.REQUIRED));
			errorFlag = true;
		}
		tfCusVisNo.setReadOnly(false);
		if ((tfCusVisNo.getValue() == null) || tfCusVisNo.getValue().trim().length() == 0) {
			try {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "SM_CUSVISIT").get(
						0);
				if (slnoObj.getAutoGenYN().equals("N")) {
					tfCusVisNo.setComponentError(new UserError(""));
					errorFlag = true;
				}
			}
			catch (Exception e) {
			}
		} else {
			tfCusVisNo.setComponentError(null);
			errorFlag = false;
		}
		tfCusVisNo.setReadOnly(true);
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
		tfCusVisNo.setReadOnly(false);
		tfCusVisNo.setValue(null);
		tfCusVisNo.setReadOnly(true);
		dfFormDt.setValue(DateUtils.getcurrentdate());
		dfVisitDt.setValue(null);
		cbEnqNo.setValue(null);
		cbWorkorder.setValue(null);
		tfClientName.setValue(null);
		tfClentCity.setValue(null);
		taPurposeVisit.setValue("");
		taPurposeRe.setValue("");
		tnNoPerson.setValue("0");
		cbHdrStatus.setValue("Active");
		tfPjtName.setValue("");
		listCustVisitNo = new ArrayList<CustomerVisitNoDtlDM>();
		tblPersonNo.removeAllItems();
		listCustVisitInfo = new ArrayList<CustomerVisitInfoDtlDM>();
		tblInfoPass.removeAllItems();
		cbDtlStatus.setValue("Active");
	}
}
