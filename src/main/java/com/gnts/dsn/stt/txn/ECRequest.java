package com.gnts.dsn.stt.txn;

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
import com.gnts.erputil.components.GERPTextField;
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

public class ECRequest extends BaseTransUI {
	private static final long serialVersionUID = 1L;
	// Bean Creation
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	private SmsEnquiryDtlService serviceEnqDetails = (SmsEnquiryDtlService) SpringContextHelper
			.getBean("SmsEnquiryDtl");
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private ECRequestService serviceECRequest = (ECRequestService) SpringContextHelper.getBean("ecRequest");
	private DepartmentService serviceDepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	// Initialize the logger
	private Logger logger = Logger.getLogger(ECRequest.class);
	// User Input Fields for EC Request
	private TextField tfECRNumber;
	private PopupDateField dfECRDate;
	private ComboBox cbEnquiry;
	private ComboBox cbClient;
	private ComboBox cbProduct;
	private ComboBox cbFromDept, cbToDept;
	private TextField tfDrgNumber, tfPartNumber;
	private TextArea taChangeDetail, taChangeReason, taTestIfAny;
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
	private Long branchId;
	
	// Constructor received the parameters from Login UI class
	public ECRequest() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		branchId = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside ECRequest() constructor");
		buildview();
	}
	
	private void buildview() {
		logger.info("CompanyId" + companyid + "username" + username + "painting ECRequest UI");
		// EC Request Components Definition
		tfECRNumber = new GERPTextField("ECR Number");
		tfECRNumber.setWidth("150");
		tfECRNumber.setReadOnly(false);
		tfDrgNumber = new TextField("Drg. Number");
		tfDrgNumber.setWidth("150");
		tfPartNumber = new TextField("Part Number");
		tfPartNumber.setWidth("150");
		cbClient = new GERPComboBox("Client Name");
		cbClient.setItemCaptionPropertyId("clientName");
		cbClient.setRequired(true);
		cbFromDept = new GERPComboBox("From Dept.");
		cbFromDept.setItemCaptionPropertyId("deptname");
		loadFromDeptList();
		cbToDept = new GERPComboBox("To Dept.");
		cbToDept.setItemCaptionPropertyId("deptname");
		loadToDeptList();
		taChangeDetail = new TextArea("Change Detail");
		taChangeDetail.setWidth("984");
		taChangeReason = new TextArea("Reason of Change");
		taChangeReason.setWidth("984");
		taTestIfAny = new TextArea("Specify any Test to be Conducted");
		taTestIfAny.setWidth("984");
		cbEnquiry = new GERPComboBox("Enquiry No.");
		cbEnquiry.setItemCaptionPropertyId("enquiryNo");
		cbEnquiry.setImmediate(true);
		cbEnquiry.setNullSelectionAllowed(false);
		cbEnquiry.setWidth("150");
		cbEnquiry.setRequired(true);
		cbEnquiry.addValueChangeListener(new ValueChangeListener() {
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
		dfECRDate = new GERPPopupDateField("Date");
		dfECRDate.setDateFormat("dd-MMM-yyyy");
		dfECRDate.setInputPrompt("Select Date");
		dfECRDate.setWidth("130px");
		cbStatus.setWidth("130");
		cbProduct = new GERPComboBox("Product Name");
		cbProduct.setItemCaptionPropertyId("prodname");
		cbProduct.setWidth("150");
		cbProduct.setRequired(true);
		cbProduct.setImmediate(true);
		cbProduct.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				tfPartNumber.setValue("");
				tfDrgNumber.setValue("");
				try {
					if (((SmsEnquiryDtlDM) cbProduct.getValue()).getCustomField1() != null) {
						tfPartNumber.setValue(((SmsEnquiryDtlDM) cbProduct.getValue()).getCustomField1());
					}
					if (((SmsEnquiryDtlDM) cbProduct.getValue()).getCustomField2() != null) {
						tfDrgNumber.setValue(((SmsEnquiryDtlDM) cbProduct.getValue()).getCustomField2());
					}
				}
				catch (Exception e) {
					logger.info(e.getMessage());
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
		flcol2.addComponent(tfECRNumber);
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
		tfECRNumber.setReadOnly(true);
		flcol1 = new FormLayout();
		flcol2 = new FormLayout();
		flcol3 = new FormLayout();
		flcol4 = new FormLayout();
		flcol1.addComponent(tfECRNumber);
		flcol1.addComponent(dfECRDate);
		flcol1.addComponent(cbEnquiry);
		flcol2.addComponent(cbClient);
		flcol2.addComponent(cbProduct);
		flcol2.addComponent(tfDrgNumber);
		flcol3.addComponent(tfPartNumber);
		flcol3.addComponent(cbFromDept);
		flcol3.addComponent(cbToDept);
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
				vlHeader.addComponent(taChangeDetail);
				vlHeader.addComponent(taChangeReason);
				vlHeader.addComponent(taTestIfAny);
				addComponent(GERPPanelGenerator.createPanel(vlHeader));
			}
		});
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	// Load EC Request
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<ECRequestDM> listECReq = new ArrayList<ECRequestDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + null + "," + tfECRNumber.getValue() + ", " + (String) cbStatus.getValue());
			listECReq = serviceECRequest.getECRequestList(companyid, branchId, null, tfECRNumber.getValue(), null,
					(String) cbStatus.getValue());
			recordCnt = listECReq.size();
			beanECReq = new BeanItemContainer<ECRequestDM>(ECRequestDM.class);
			beanECReq.addAll(listECReq);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the ECReq. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanECReq);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "ecrid", "ecrNumber", "ecrDate", "drgNumber", "status",
					"lastUpdatedDate", "lastUpdatedBy" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "ECR Number", "Date", "Drg. Number", "Status",
					"Last Updated date", "Last Updated by" });
			tblMstScrSrchRslt.setColumnAlignment("ecrid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Enquiry List
	private void loadEnquiryList() {
		try {
			BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
			beansmsenqHdr.setBeanIdProperty("enquiryId");
			beansmsenqHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyid, null, null, null, null, "P", null, null));
			cbEnquiry.setContainerDataSource(beansmsenqHdr);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Product List
	private void loadProduct() {
		try {
			BeanItemContainer<SmsEnquiryDtlDM> beanEnqDtl = new BeanItemContainer<SmsEnquiryDtlDM>(
					SmsEnquiryDtlDM.class);
			beanEnqDtl.addAll(serviceEnqDetails.getsmsenquirydtllist(null, (Long) cbEnquiry.getValue(), null, null,
					null, null));
			cbProduct.setContainerDataSource(beanEnqDtl);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Load Client List
	private void loadSmsClientList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading client Search...");
			BeanContainer<Long, ClientDM> beanclientDM = new BeanContainer<Long, ClientDM>(ClientDM.class);
			beanclientDM.setBeanIdProperty("clientId");
			beanclientDM.addAll(serviceClients.getClientDetails(
					companyid,
					serviceEnqHeader
							.getSmsEnqHdrList(null, (Long) cbEnquiry.getValue(), null, null, null, "F", null, null)
							.get(0).getClientId(), null, null, null, null, null, null, "Active", "P"));
			cbClient.setContainerDataSource(beanclientDM);
			cbClient.setValue(cbClient.getItemIds().iterator().next());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadFromDeptList()-->this function is used for load the Department list
	 */
	private void loadFromDeptList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Department Search...");
			BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
			beanDepartment.setBeanIdProperty("deptid");
			beanDepartment.addAll(serviceDepartmant.getDepartmentList(companyid, null, "Active", "P"));
			cbFromDept.setContainerDataSource(beanDepartment);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadToDeptList()-->this function is used for load the Department list
	 */
	private void loadToDeptList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Loading Department Search...");
			BeanContainer<Long, DepartmentDM> beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
			beanDepartment.setBeanIdProperty("deptid");
			beanDepartment.addAll(serviceDepartmant.getDepartmentList(companyid, null, "Active", "P"));
			cbToDept.setContainerDataSource(beanDepartment);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method to edit the values from table into fields to update process for Sales Enquiry Header
	private void editECRequest() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hllayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			ECRequestDM ecRequestDM = beanECReq.getItem(tblMstScrSrchRslt.getValue()).getBean();
			ecrid = ecRequestDM.getEcrid();
			tfECRNumber.setReadOnly(false);
			tfECRNumber.setValue(ecRequestDM.getEcrNumber());
			tfECRNumber.setReadOnly(true);
			cbEnquiry.setValue(ecRequestDM.getEnquiryId());
			Long prodid = ecRequestDM.getProductId();
			Collection<?> prdids = cbProduct.getItemIds();
			for (Iterator<?> iterator = prdids.iterator(); iterator.hasNext();) {
				Object itemId = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbProduct.getItem(itemId);
				// Get the actual bean and use the data
				SmsEnquiryDtlDM st = (SmsEnquiryDtlDM) item.getBean();
				if (prodid != null && prodid.equals(st.getProductid())) {
					cbProduct.setValue(itemId);
				}
			}
			cbClient.setValue(ecRequestDM.getClientId());
			cbFromDept.setValue(ecRequestDM.getFromDeptId());
			cbToDept.setValue(ecRequestDM.getToDeptId());
			dfECRDate.setValue(ecRequestDM.getEcrDate());
			if (ecRequestDM.getChangeDetail() != null) {
				taChangeDetail.setValue(ecRequestDM.getChangeDetail());
			}
			if (ecRequestDM.getChangeReason() != null) {
				taChangeReason.setValue(ecRequestDM.getChangeReason());
			}
			if (ecRequestDM.getIfAnyTest() != null) {
				taTestIfAny.setValue(ecRequestDM.getIfAnyTest());
			}
			tfPartNumber.setValue(ecRequestDM.getPartNumber());
			tfDrgNumber.setValue(ecRequestDM.getDrgNumber());
			cbStatus.setValue(ecRequestDM.getStatus());
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, ecrid, null, null, null,
				status,null);
		comments.loadsrch(true, null, null, null, null, null, null, null, ecrid, null, null, null, null,null);
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... "); //
		ECRequestDM ecRequestDM = new ECRequestDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			ecRequestDM = beanECReq.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		ecRequestDM.setEcrNumber(tfECRNumber.getValue());
		ecRequestDM.setEnquiryId((Long) cbEnquiry.getValue());
		ecRequestDM.setDrgNumber(tfDrgNumber.getValue());
		ecRequestDM.setPartNumber(tfPartNumber.getValue());
		ecRequestDM.setFromDeptId((Long) cbFromDept.getValue());
		ecRequestDM.setToDeptId((Long) cbToDept.getValue());
		if (cbProduct.getValue() != null) {
			ecRequestDM.setProductId(((SmsEnquiryDtlDM) cbProduct.getValue()).getProductid());
		}
		ecRequestDM.setClientId((Long) cbClient.getValue());
		ecRequestDM.setEcrDate(dfECRDate.getValue());
		ecRequestDM.setChangeDetail(taChangeDetail.getValue());
		ecRequestDM.setChangeReason(taChangeReason.getValue());
		ecRequestDM.setIfAnyTest(taTestIfAny.getValue());
		ecRequestDM.setStatus((String) cbStatus.getValue());
		ecRequestDM.setLastUpdatedBy(username);
		ecRequestDM.setCompanyId(companyid);
		ecRequestDM.setBranchId(branchId);
		ecRequestDM.setLastUpdatedDate(DateUtils.getcurrentdate());
		serviceECRequest.saveOrUpdateECRequest(ecRequestDM);
		ecrid = ecRequestDM.getEcrid();
		try {
			SmsEnquiryDtlDM smsEnquiryDtlDM = (SmsEnquiryDtlDM) cbProduct.getValue();
			smsEnquiryDtlDM.setCustomField1(tfPartNumber.getValue());
			smsEnquiryDtlDM.setCustomField2(tfDrgNumber.getValue());
			System.out.println(smsEnquiryDtlDM);
			serviceEnqDetails.saveOrUpdatesmsenquirydtlDetails(smsEnquiryDtlDM);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
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
				logger.info(e.getMessage());
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
		tfECRNumber.setValue("");
		tfECRNumber.setReadOnly(false);
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// cbclient.setRequired(true);
		tfECRNumber.setReadOnly(true);
		hllayout.removeAllComponents();
		vlSrchRsltContainer.setVisible(true);
		assembleinputLayout();
		resetFields();
		tfECRNumber.setReadOnly(false);
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, branchId, moduleId, "STT_ECRNO").get(0);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfECRNumber.setValue(slnoObj.getKeyDesc());
				tfECRNumber.setReadOnly(true);
			} else {
				tfECRNumber.setReadOnly(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		comments = new SmsComments(vlTableForm, null, companyid, null, null, null, null, null, null, null, null, null,
				null,null);
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
		cbEnquiry.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if (cbEnquiry.getValue() == null) {
			cbEnquiry.setComponentError(new UserError(GERPErrorCodes.NULL_ENQUIRYNO));
			errorFlag = true;
		}
		if (cbClient.getValue() == null) {
			cbClient.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
			errorFlag = true;
		}
		if (cbProduct.getValue() == null) {
			cbProduct.setComponentError(new UserError(GERPErrorCodes.NULL_PRODUCT_NAME));
			errorFlag = true;
		}
		if ((dfECRDate.getValue() == null)) {
			dfECRDate.setComponentError(new UserError(GERPErrorCodes.SELECT_DATE));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + dfECRDate.getValue());
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + null + "," + cbEnquiry.getValue() + "," + "," + ","
				+ dfECRDate.getValue() + ",");
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
		tfECRNumber.setReadOnly(false);
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		tfECRNumber.setReadOnly(false);
		tfECRNumber.setValue("");
		tfECRNumber.setComponentError(null);
		cbEnquiry.setComponentError(null);
		cbEnquiry.setValue(null);
		cbProduct.setValue(null);
		cbClient.setValue(null);
		cbFromDept.setValue(null);
		cbToDept.setValue(null);
		dfECRDate.setValue(null);
		taChangeDetail.setValue("");
		taChangeReason.setValue("");
		taTestIfAny.setValue("");
		cbStatus.setValue(null);
		dfECRDate.setValue(new Date());
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
			tfECRNumber.setReadOnly(false);
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