package com.gnts.asm.txn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseTransUI;
import com.gnts.erputil.ui.Database;
import com.gnts.erputil.ui.Report;
import com.gnts.erputil.util.DateUtils;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.domain.txn.SmsEnquiryDtlDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.gnts.sms.service.txn.SmsEnquiryDtlService;
import com.gnts.sms.txn.SmsComments;
import com.gnts.stt.dsn.domain.txn.ECRequestDM;
import com.gnts.stt.dsn.service.txn.ECRequestService;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Xerox extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private SmsEnquiryDtlService serviceEnqDetails = (SmsEnquiryDtlService) SpringContextHelper
			.getBean("SmsEnquiryDtl");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private ECRequestService serviceECRequest = (ECRequestService) SpringContextHelper.getBean("ecRequest");
	private DepartmentService serviceDepartment = (DepartmentService) SpringContextHelper.getBean("department");
	// Initialize the logger
	private Logger logger = Logger.getLogger(Xerox.class);
	// User Input Fields for EC Request
	private PopupDateField dfRefDate;
	private ComboBox cbAssetName;
	private ComboBox cbAssetAssignee;
	private ComboBox cbEmployeeName;
	private ComboBox cbDepartment;
	private TextField tfNoOfXerox, tfNoOfPrintouts;
	private TextArea taPurpose;
	private ComboBox cbStatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE,
			BASEConstants.M_GENERIC_COLUMN);
	private BeanItemContainer<ECRequestDM> beanECReq = null;
	// form layout for input controls EC Request
	private FormLayout flcol1, flcol2, flcol3, flcol4;
	// Search Control Layout
	private HorizontalLayout hlsearchlayout;
	// Parent layout for all the input controls EC Request
	private HorizontalLayout hllayout = new HorizontalLayout();
	private HorizontalLayout hllayout1 = new HorizontalLayout();
	// Parent layout for all the input controls Sms Comments
	private VerticalLayout vlTableForm = new VerticalLayout();
	// local variables declaration
	private Long ecrid;
	private String username;
	private Long companyid, moduleId;
	private int recordCnt = 0;
	private SmsComments comments;
	private String status;
	@SuppressWarnings("unused")
	private Long branchId, employeeId, roleId, appScreenId;
	
	// Constructor received the parameters from Login UI class
	public Xerox() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		employeeId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		roleId = (Long) UI.getCurrent().getSession().getAttribute("roleId");
		appScreenId = (Long) UI.getCurrent().getSession().getAttribute("appScreenId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ECRequest() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting ECRequest UI");
		// EC Request Components Definition
		tfNoOfXerox = new TextField("No of Xerox");
		tfNoOfXerox.setWidth("150");
		tfNoOfPrintouts = new TextField("No of Printout");
		tfNoOfPrintouts.setWidth("150");
		cbAssetAssignee = new GERPComboBox("Asset Assignee");
		cbAssetAssignee.setItemCaptionPropertyId("clientName");
		cbAssetAssignee.setRequired(true);
		cbDepartment = new GERPComboBox("Department");
		cbDepartment.setItemCaptionPropertyId("deptname");
		loadFromDeptList();
		taPurpose = new TextArea("Purpose");
		taPurpose.setWidth("984");
		cbAssetName = new GERPComboBox("Xerox Machine");
		cbAssetName.setItemCaptionPropertyId("enquiryNo");
		cbAssetName.setImmediate(true);
		cbAssetName.setNullSelectionAllowed(false);
		cbAssetName.setWidth("150");
		cbAssetName.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					loadProduct();
				}
				catch (Exception e) {
				}
				try {
					loadSmsClientList();
				}
				catch (Exception e) {
				}
			}
		});
		loadEnquiryList();
		dfRefDate = new GERPPopupDateField("Date");
		dfRefDate.setDateFormat("dd-MMM-yyyy");
		dfRefDate.setInputPrompt("Select Date");
		dfRefDate.setWidth("130px");
		cbStatus.setWidth("150px");
		cbEmployeeName = new GERPComboBox("Taken by");
		cbEmployeeName.setItemCaptionPropertyId("prodname");
		cbEmployeeName.setWidth("150");
		cbEmployeeName.setRequired(true);
		cbEmployeeName.setImmediate(true);
		cbEmployeeName.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				tfNoOfPrintouts.setValue("");
				tfNoOfXerox.setValue("");
				try {
					if (((SmsEnquiryDtlDM) cbEmployeeName.getValue()).getCustomField1() != null) {
						tfNoOfPrintouts.setValue(((SmsEnquiryDtlDM) cbEmployeeName.getValue()).getCustomField1());
					}
					if (((SmsEnquiryDtlDM) cbEmployeeName.getValue()).getCustomField2() != null) {
						tfNoOfXerox.setValue(((SmsEnquiryDtlDM) cbEmployeeName.getValue()).getCustomField2());
					}
				}
				catch (Exception e) {
				}
			}
		});
		hlsearchlayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		hlsearchlayout.removeAllComponents();
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol1.addComponent(cbAssetName);
		flcol2.addComponent(cbDepartment);
		flcol3.addComponent(cbStatus);
		hlsearchlayout.addComponent(flcol1);
		hlsearchlayout.addComponent(flcol2);
		hlsearchlayout.addComponent(flcol3);
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setSizeUndefined();
	}
	
	private void assembleinputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol1.addComponent(dfRefDate);
		flcol1.addComponent(cbAssetName);
		flcol2.addComponent(cbAssetAssignee);
		flcol2.addComponent(cbEmployeeName);
		flcol3.addComponent(cbDepartment);
		flcol3.addComponent(tfNoOfPrintouts);
		flcol4.addComponent(tfNoOfXerox);
		flcol4.addComponent(cbStatus);
		hllayout.setMargin(true);
		hllayout.addComponent(flcol1);
		hllayout.addComponent(flcol2);
		hllayout.addComponent(flcol3);
		hllayout.addComponent(flcol4);
		hllayout.setMargin(true);
		hllayout.setSpacing(true);
		hlUserIPContainer.addComponent(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				VerticalLayout vlHeader = new VerticalLayout();
				vlHeader.setSpacing(true);
				vlHeader.setMargin(true);
				vlHeader.addComponent(hllayout);
				vlHeader.addComponent(taPurpose);
				addComponent(GERPPanelGenerator.createPanel(vlHeader));
			}
		});
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	// Load EC Request
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		List<ECRequestDM> listECReq = new ArrayList<ECRequestDM>();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ companyid + ", " + null + "," + null + ", " + (String) cbStatus.getValue());
		listECReq = serviceECRequest.getECRequestList(null, null, null, (String) cbStatus.getValue());
		recordCnt = listECReq.size();
		beanECReq = new BeanItemContainer<ECRequestDM>(ECRequestDM.class);
		beanECReq.addAll(listECReq);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the ECReq. result set");
		tblMstScrSrchRslt.setContainerDataSource(beanECReq);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "ecrid", "ecrNumber", "ecrDate", "drgNumber", "status",
				"lastUpdatedDate", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Asset Name", "Date", "No of Printouts", "Status",
				"Last Updated date", "Last Updated by" });
		tblMstScrSrchRslt.setColumnAlignment("ecrid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	// Load Enquiry List
	private void loadEnquiryList() {
		BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
		beansmsenqHdr.setBeanIdProperty("enquiryId");
		beansmsenqHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
		cbAssetName.setContainerDataSource(beansmsenqHdr);
	}
	
	// Load Product List
	private void loadProduct() {
		try {
			Long enquid = ((Long) cbAssetName.getValue());
			BeanItemContainer<SmsEnquiryDtlDM> beanEnqDtl = new BeanItemContainer<SmsEnquiryDtlDM>(
					SmsEnquiryDtlDM.class);
			beanEnqDtl.addAll(serviceEnqDetails.getsmsenquirydtllist(null, enquid, null, null, null, null));
			cbEmployeeName.setContainerDataSource(beanEnqDtl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Load Client List
	private void loadSmsClientList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading client Search...");
		List<ClientDM> clientlist = serviceClients.getClientDetails(companyid,
				serviceEnqHeader.getSmsEnqHdrList(null, (Long) cbAssetName.getValue(), null, null, null, "F", null, null)
						.get(0).getClientId(), null, null, null, null, null, null, "Active", "P");
		BeanContainer<Long, ClientDM> beanclientDM = new BeanContainer<Long, ClientDM>(ClientDM.class);
		beanclientDM.setBeanIdProperty("clientId");
		beanclientDM.addAll(clientlist);
		cbAssetAssignee.setContainerDataSource(beanclientDM);
		cbAssetAssignee.setValue(cbAssetAssignee.getItemIds().iterator().next());
	}
	
	/*
	 * loadFromDeptList()-->this function is used for load the Department list
	 */
	private void loadFromDeptList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Department Search...");
		List<DepartmentDM> departmentlist = serviceDepartment.getDepartmentList(companyid, null, "Active", "P");
		BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beanDepartment.setBeanIdProperty("deptid");
		beanDepartment.addAll(departmentlist);
		cbDepartment.setContainerDataSource(beanDepartment);
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editECRequest() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Selected ecrid -> " + ecrid);
		if (sltedRcd != null) {
			ECRequestDM ecRequestDM = beanECReq.getItem(tblMstScrSrchRslt.getValue()).getBean();
			ecrid = ecRequestDM.getEcrid();
			cbAssetName.setValue(ecRequestDM.getEnquiryId());
			Long prodid = ecRequestDM.getProductId();
			Collection<?> prdids = cbEmployeeName.getItemIds();
			for (Iterator<?> iterator = prdids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbEmployeeName.getItem(itemId);
				// Get the actual bean and use the data
				SmsEnquiryDtlDM st = (SmsEnquiryDtlDM) item.getBean();
				if (prodid != null && prodid.equals(st.getProductid())) {
					cbEmployeeName.setValue(itemId);
				}
			}
			cbAssetAssignee.setValue(ecRequestDM.getClientId());
			cbDepartment.setValue(ecRequestDM.getFromDeptId());
			dfRefDate.setValue(ecRequestDM.getEcrDate());
			if (ecRequestDM.getChangeDetail() != null) {
				taPurpose.setValue(ecRequestDM.getChangeDetail());
			}
			tfNoOfPrintouts.setValue(ecRequestDM.getPartNumber());
			tfNoOfXerox.setValue(ecRequestDM.getDrgNumber());
			cbStatus.setValue(ecRequestDM.getStatus());
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, ecrid, null, null, null,
				status);
		comments.loadsrch(true, null, null, null, null, null, null, null, ecrid, null, null, null, null);
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		ECRequestDM ecRequestDM = new ECRequestDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			ecRequestDM = beanECReq.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		ecRequestDM.setEnquiryId((Long) cbAssetName.getValue());
		ecRequestDM.setDrgNumber(tfNoOfXerox.getValue());
		ecRequestDM.setPartNumber(tfNoOfPrintouts.getValue());
		ecRequestDM.setFromDeptId((Long) cbDepartment.getValue());
		if (cbEmployeeName.getValue() != null) {
			ecRequestDM.setProductId(((SmsEnquiryDtlDM) cbEmployeeName.getValue()).getProductid());
		}
		ecRequestDM.setClientId((Long) cbAssetAssignee.getValue());
		ecRequestDM.setEcrDate(dfRefDate.getValue());
		ecRequestDM.setChangeDetail(taPurpose.getValue());
		ecRequestDM.setStatus((String) cbStatus.getValue());
		ecRequestDM.setLastUpdatedBy(username);
		ecRequestDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceECRequest.saveOrUpdateECRequest(ecRequestDM);
		ecrid = ecRequestDM.getEcrid();
		try {
			SmsEnquiryDtlDM smsEnquiryDtlDM = (SmsEnquiryDtlDM) cbEmployeeName.getValue();
			smsEnquiryDtlDM.setCustomField1(tfNoOfPrintouts.getValue());
			smsEnquiryDtlDM.setCustomField2(tfNoOfXerox.getValue());
			System.out.println(smsEnquiryDtlDM);
			serviceEnqDetails.saveOrUpdatesmsenquirydtlDetails(smsEnquiryDtlDM);
		}
		catch (Exception e) {
		}
		if (tblMstScrSrchRslt.getValue() == null) {
			try {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "STT_ECRNO").get(0);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					serviceSlnogen.updateNextSequenceNumber(companyid, branchId, moduleId, "STT_ECRNO");
					System.out.println("Serial no=>" + companyid + "," + moduleId + "," + branchId);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		comments.saveSalesEnqId(ecRequestDM.getEnquiryId(), null);
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbStatus.setValue(null);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// cbclient.setRequired(true);
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		resetFields();
		editECRequest();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		cbAssetName.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbAssetName.getValue() == null) {
			cbAssetName.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorFlag = true;
		}
		if (cbAssetAssignee.getValue() == null) {
			cbAssetAssignee.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
			errorFlag = true;
		}
		if (cbEmployeeName.getValue() == null) {
			cbEmployeeName.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		if ((dfRefDate.getValue() == null)) {
			dfRefDate.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfRefDate.getValue());
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbAssetName.getValue() + "," + "," + ","
				+ dfRefDate.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for enquiryId " + ecrid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_SMS_ENQUIRY_HDR);
		UI.getCurrent().getSession().setAttribute("audittablepk", String.valueOf(ecrid));
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hllayout1.removeAllComponents();
		tblMstScrSrchRslt.setValue(null);
		assembleSearchLayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbAssetName.setComponentError(null);
		cbAssetName.setValue(null);
		cbEmployeeName.setValue(null);
		cbAssetAssignee.setValue(null);
		cbDepartment.setValue(null);
		dfRefDate.setValue(null);
		taPurpose.setValue("");
		cbStatus.setValue(null);
		dfRefDate.setValue(new Date());
	}
	
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
	protected void printDetails() {
		// TODO Auto-generated method stub
		Connection connection = null;
		Statement statement = null;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			HashMap<String, Long> parameterMap = new HashMap<String, Long>();
			parameterMap.put("ECRID", ecrid);
			Report rpt = new Report(parameterMap, connection);
			rpt.setReportName(basepath + "/WEB-INF/reports/ecr"); // ecr is the name of my jasper
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
