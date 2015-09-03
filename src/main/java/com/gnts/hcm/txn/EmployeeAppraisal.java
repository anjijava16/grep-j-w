/**
 * File Name 		: EmployeeAppraisal.java 
 * Description 		: this class is used for add/edit EmployeeAppraisal details. 
 * Author 			: SUNDAR 
 * Date 			: 14-October-2014	
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1       14-October-2014		 SUNDAR              Initial Version
 * 
 */
package com.gnts.hcm.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.hcm.domain.txn.AppraisalLevelsDM;
import com.gnts.hcm.domain.txn.EmpAppraisalDtlDM;
import com.gnts.hcm.domain.txn.EmpAppraisalHdrDM;
import com.gnts.hcm.domain.txn.KpiDM;
import com.gnts.hcm.domain.txn.KpiGroupDM;
import com.gnts.hcm.service.txn.AppraisalLevelsService;
import com.gnts.hcm.service.txn.EmpAppraisalDtlService;
import com.gnts.hcm.service.txn.EmpAppraisalHdrService;
import com.gnts.hcm.service.txn.KpiGroupService;
import com.gnts.hcm.service.txn.KpiService;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class EmployeeAppraisal extends BaseUI {
	// Bean Creation
	private EmpAppraisalHdrService serviceAppraisalhdr = (EmpAppraisalHdrService) SpringContextHelper
			.getBean("EmployeeAppraisalHdr");
	private EmpAppraisalDtlService serviceAppraisaldtl = (EmpAppraisalDtlService) SpringContextHelper
			.getBean("EmpAppraisalDtl");
	private EmployeeService serviceEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private KpiGroupService serviceKPIGroup = (KpiGroupService) SpringContextHelper.getBean("KpiGroup");
	private KpiService serviceKPI = (KpiService) SpringContextHelper.getBean("Kpi");
	private AppraisalLevelsService serviceAppraisalLevel = (AppraisalLevelsService) SpringContextHelper
			.getBean("AppraisalLevels");
	private List<EmpAppraisalDtlDM> listEmpApprDtls = new ArrayList<EmpAppraisalDtlDM>();
	// form layout for input controls
	private FormLayout fcol1, fcol2, fcol3, fcolt1, fcolt2, fcolt3, fcolt4, fcolt5, fcolDtl1, fcolDtl2, fcolDtl3,
			fcolDtl4;
	// Parent layout for all the input controls
	private HorizontalLayout hlsearchlayout = new HorizontalLayout();
	private HorizontalLayout h3search = new HorizontalLayout();
	private HorizontalLayout h2searchlayout = new HorizontalLayout();
	private HorizontalLayout hluserInputlayout = new HorizontalLayout();
	private Table tblEmpAprisalDtl;
	private BeanItemContainer<EmpAppraisalHdrDM> beanEmpAppraisalHdrDM = null;
	private BeanItemContainer<EmpAppraisalDtlDM> beanEmpAppraisalDtlDM = null;
	private VerticalLayout vlempAprisalDtl, vlEmpApraisalHdrDtl;
	private Button btnAddEmpAprisalDtl = new GERPButton("Add", "add", this);
	private ComboBox cbEmployee, cbAppYear, cbKPIGrpName, cbstatus, cbapprsename, cbkpiname, cvApprlevelname;
	private PopupDateField pdfappdate, pdfempsgndate, pdfclsddate;
	private CheckBox chkpromflag, chkempagd;
	private TextField tfoverrtng, tfkpiRating;
	private GERPTextArea taComments, taRemarks, comments;
	// local variables declaration
	private String aprisalId;
	private String username;
	private Boolean errorFlag = false;
	private Long companyid, employeeid;
	private int recordCnt = 0;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmployeeAppraisal.class);
	private static final long serialVersionUID = 1L;
	private Button btnDelete = new GERPButton("Delete", "delete", this);
	
	// Constructor received the parameters from Login UI class
	public EmployeeAppraisal() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmployeeAppraisal() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		// Initialization for EmpAprisal Details user input components
		btnAddEmpAprisalDtl = new GERPButton("Add", "add");
		btnAddEmpAprisalDtl.addClickListener(new ClickListener() {
			// Click Listener for Add and Update
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateDtl()) {
					saveempAppraisalDtlListDetails();
				}
			}
		});
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteDtls();
			}
		});
		tblEmpAprisalDtl = new GERPTable();
		tblEmpAprisalDtl.setPageLength(6);
		tblEmpAprisalDtl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblEmpAprisalDtl.isSelected(event.getItemId())) {
					tblEmpAprisalDtl.setImmediate(true);
					btnAddEmpAprisalDtl.setCaption("Add");
					resetSearchDetails();
					btnDelete.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnAddEmpAprisalDtl.setCaption("Update");
					editEmpAppraisalDtlDetails();
					btnDelete.setEnabled(true);
				}
			}
		});
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Painting EmployeeAppraisal UI");
		// Employee Name ComboBox
		cbEmployee = new GERPComboBox("Employee Name");
		cbEmployee.setWidth("100");
		cbEmployee.setItemCaptionPropertyId("firstlastname");
		loadEmployeeList();
		// Appraisal Year ComboBox
		cbAppYear = new ComboBox("Appraisal Year");
		cbAppYear.setWidth("100");
		cbAppYear.setRequired(true);
		loadAppraisalYearList();
		pdfappdate = new PopupDateField("Appraisal Date");
		pdfappdate.setDateFormat("dd-MMM-yyyy");
		pdfappdate.setWidth("100");
		pdfappdate.setRequired(true);
		// KPI Group Name ComboBox
		cbKPIGrpName = new ComboBox("KPI Group Name");
		cbKPIGrpName.setWidth("100");
		cbKPIGrpName.setItemCaptionPropertyId("kpigroupname");
		loadKpiGroupNameList();
		chkpromflag = new CheckBox("Promotion Flag");
		// Overall Rating text field
		tfoverrtng = new TextField("Overall Rating");
		tfoverrtng.setWidth("100");
		taComments = new GERPTextArea("Comments");
		taComments.setWidth("110%");
		taComments.setHeight("75px");
		chkempagd = new CheckBox("Employee Agreed");
		pdfempsgndate = new PopupDateField("SignOff Date");
		pdfempsgndate.setDateFormat("dd-MMM-yyyy");
		pdfempsgndate.setWidth("80");
		taRemarks = new GERPTextArea("Remarks");
		taRemarks.setWidth("110");
		taRemarks.setHeight("50px");
		pdfclsddate = new PopupDateField("Closed Date");
		pdfclsddate.setDateFormat("dd-MMM-yyyy");
		pdfclsddate.setWidth("80");
		// Status ComboBox
		cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbstatus.setWidth("110");
		// Appraisee Name ComboBox
		cbapprsename = new ComboBox("Appraisal Name");
		cbapprsename.setItemCaptionPropertyId("firstlastname");
		cbapprsename.setWidth("110");
		cbapprsename.setRequired(true);
		loadAppraiseeNameList();
		// KPI Name ComboBox
		cbkpiname = new GERPComboBox("KPI Name");
		cbkpiname.setWidth("110");
		cbkpiname.setRequired(true);
		cbkpiname.setItemCaptionPropertyId("kpiName");
		loadKpiNameList();
		// Apprlevel Name ComboBox
		cvApprlevelname = new ComboBox("Level");
		cvApprlevelname.setWidth("110");
		cvApprlevelname.setRequired(true);
		cvApprlevelname.setItemCaptionPropertyId("levelname");
		loadApprlevelNameList();
		// KPI Rating text field
		tfkpiRating = new TextField("KPI Rating");
		tfkpiRating.setValue("0");
		tfkpiRating.setWidth("110");
		comments = new GERPTextArea("Comments");
		comments.setWidth("110%");
		comments.setHeight("50px");
		hlsearchlayout = new GERPAddEditHLayout();
		assemblesearchlayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlsearchlayout));
		resetFields();
		loadSrchRslt();
		loaddtlRslt();
		btnAddEmpAprisalDtl.setStyleName("add");
	}
	
	private void assemblesearchlayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlsearchlayout.removeAllComponents();
		hlsearchlayout.setMargin(true);
		fcol1 = new GERPFormLayout();
		fcol2 = new GERPFormLayout();
		fcol3 = new GERPFormLayout();
		fcol1.addComponent(cbEmployee);
		fcol2.addComponent(cbKPIGrpName);
		fcol3.addComponent(cbstatus);
		hlsearchlayout.addComponent(fcol1);
		hlsearchlayout.addComponent(fcol2);
		hlsearchlayout.addComponent(fcol3);
		hlsearchlayout.setMargin(true);
		hlsearchlayout.setSizeUndefined();
	}
	
	private void assembleuserinputlayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in search layout
		hlsearchlayout.removeAllComponents();
		fcolt1 = new FormLayout();
		fcolt2 = new FormLayout();
		fcolt3 = new FormLayout();
		fcolt4 = new FormLayout();
		fcolt5 = new FormLayout();
		fcolt1.addComponent(cbEmployee);
		fcolt1.addComponent(cbAppYear);
		fcolt1.addComponent(pdfappdate);
		fcolt2.addComponent(cbKPIGrpName);
		fcolt2.addComponent(chkpromflag);
		fcolt2.addComponent(tfoverrtng);
		fcolt3.addComponent(chkempagd);
		fcolt3.addComponent(pdfclsddate);
		fcolt3.addComponent(pdfempsgndate);
		fcolt4.addComponent(taComments);
		fcolt5.addComponent(taRemarks);
		fcolt5.addComponent(cbstatus);
		h2searchlayout = new HorizontalLayout();
		h2searchlayout.addComponent(fcolt1);
		h2searchlayout.addComponent(fcolt2);
		h2searchlayout.addComponent(fcolt3);
		h2searchlayout.addComponent(fcolt4);
		h2searchlayout.addComponent(fcolt5);
		h2searchlayout.setSpacing(true);
		h2searchlayout.setMargin(true);
		// Adding EmpAprisalDtl components
		// Add components for User Input Layout
		fcolDtl1 = new FormLayout();
		fcolDtl2 = new FormLayout();
		fcolDtl3 = new FormLayout();
		fcolDtl4 = new FormLayout();
		fcolDtl1.addComponent(cbapprsename);
		fcolDtl1.addComponent(cbkpiname);
		fcolDtl2.addComponent(cvApprlevelname);
		fcolDtl2.addComponent(tfkpiRating);
		fcolDtl3.addComponent(comments);
		fcolDtl4.addComponent(btnAddEmpAprisalDtl);
		fcolDtl4.addComponent(btnDelete);
		h3search = new HorizontalLayout();
		h3search.addComponent(fcolDtl1);
		h3search.addComponent(fcolDtl2);
		h3search.addComponent(fcolDtl3);
		h3search.addComponent(fcolDtl4);
		btnAddEmpAprisalDtl.setVisible(true);
		vlempAprisalDtl = new VerticalLayout();
		vlempAprisalDtl.addComponent(h3search);
		vlempAprisalDtl.addComponent(tblEmpAprisalDtl);
		vlempAprisalDtl.setSpacing(true);
		vlEmpApraisalHdrDtl = new VerticalLayout();
		vlEmpApraisalHdrDtl.addComponent(GERPPanelGenerator.createPanel(h2searchlayout));
		vlEmpApraisalHdrDtl.addComponent(GERPPanelGenerator.createPanel(vlempAprisalDtl));
		vlEmpApraisalHdrDtl.setSpacing(true);
		vlEmpApraisalHdrDtl.setWidth("100%");
		hluserInputlayout.addComponent(vlEmpApraisalHdrDtl);
		hluserInputlayout.setSizeUndefined();
		hluserInputlayout.setWidth("100%");
		hluserInputlayout.setMargin(false);
		hluserInputlayout.setSpacing(true);
	}
	
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<EmpAppraisalHdrDM> listHdr = new ArrayList<EmpAppraisalHdrDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are ");
			listHdr = serviceAppraisalhdr.getempappraisalhdrlist(null, (Long) cbEmployee.getValue(),
					(Long) cbKPIGrpName.getValue(), null, (String) cbstatus.getValue(), "F");
			recordCnt = listHdr.size();
			beanEmpAppraisalHdrDM = new BeanItemContainer<EmpAppraisalHdrDM>(EmpAppraisalHdrDM.class);
			beanEmpAppraisalHdrDM.addAll(listHdr);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the EmpAppraisalHdr. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanEmpAppraisalHdrDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "appraisalid", "empfirstlastname", "kpigroupname",
					"apprstatus", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Name", "KPI Group Name", "Status",
					"Last Updated Date", "Last Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("appraisalid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loaddtlRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			logger.info("Company ID : " + companyid + " | saveempAppraisalDtlListDetails User Name : " + username
					+ " > " + "Search Parameters are ");
			recordCnt = listEmpApprDtls.size();
			tblEmpAprisalDtl.setPageLength(10);
			beanEmpAppraisalDtlDM = new BeanItemContainer<EmpAppraisalDtlDM>(EmpAppraisalDtlDM.class);
			beanEmpAppraisalDtlDM.addAll(listEmpApprDtls);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Got the EmpAppraisalDtl. result set");
			tblEmpAprisalDtl.setContainerDataSource(beanEmpAppraisalDtlDM);
			tblEmpAprisalDtl.setVisibleColumns(new Object[] { "firstlastname", "kpiName", "levelname", "kpirating",
					"comments", "lastupdatedby", "lastupdateddt" });
			tblEmpAprisalDtl.setColumnHeaders(new String[] { "Appraisal Name", "KPI Name", "Level ", "KPI Rating",
					"Comments", "Last Updated By", "Last Updated Date" });
			tblEmpAprisalDtl.setColumnAlignment("appraisaldtlid", Align.RIGHT);
			tblEmpAprisalDtl.setColumnFooter("lastupdateddt", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Method to reset the fields
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEmployee.setReadOnly(false);
		cbEmployee.setValue(null);
		cbEmployee.setComponentError(null);
		cbAppYear.setValue(null);
		cbAppYear.setComponentError(null);
		pdfappdate.setValue(null);
		pdfappdate.setComponentError(null);
		cbKPIGrpName.setReadOnly(false);
		cbKPIGrpName.setValue(null);
		cbKPIGrpName.setComponentError(null);
		chkpromflag.setValue(false);
		tfoverrtng.setValue("0");
		tfoverrtng.setNullRepresentation("");
		taComments.setValue("");
		taComments.setNullRepresentation("");
		chkempagd.setValue(false);
		pdfclsddate.setValue(null);
		pdfempsgndate.setValue(null);
		taRemarks.setValue("");
		taRemarks.setNullRepresentation("");
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		listEmpApprDtls = new ArrayList<EmpAppraisalDtlDM>();
		tblEmpAprisalDtl.removeAllItems();
		recordCnt = 0;
	}
	
	// Method to edit the values from table into fields to update process
	private void editEmpAppraisalHdrDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Editing the selected record");
			if (tblMstScrSrchRslt.getValue() != null) {
				EmpAppraisalHdrDM empAppraisalHdr = beanEmpAppraisalHdrDM.getItem(tblMstScrSrchRslt.getValue())
						.getBean();
				aprisalId = empAppraisalHdr.getAppraisalid().toString();
				cbEmployee.setReadOnly(false);
				cbEmployee.setValue(empAppraisalHdr.getEmployeeid());
				cbAppYear.setValue(empAppraisalHdr.getApryear());
				pdfappdate.setValue(empAppraisalHdr.getAprdate());
				cbKPIGrpName.setReadOnly(false);
				cbKPIGrpName.setValue(empAppraisalHdr.getKpigrpid());
				if (empAppraisalHdr.getPromotionflag().equals("Y")) {
					chkpromflag.setValue(true);
				} else {
					chkpromflag.setValue(false);
				}
				if (empAppraisalHdr.getEmpagreed().equals("Y")) {
					chkempagd.setValue(true);
				} else {
					chkempagd.setValue(false);
				}
				tfoverrtng.setValue(empAppraisalHdr.getOverallrating());
				taComments.setValue(empAppraisalHdr.getHrcomments());
				pdfempsgndate.setValue(empAppraisalHdr.getEmpsignoffdate());
				pdfclsddate.setValue(empAppraisalHdr.getCloseddate());
				taRemarks.setValue(empAppraisalHdr.getEmpremarks());
				cbstatus.setValue(empAppraisalHdr.getApprstatus());
				listEmpApprDtls.addAll(serviceAppraisaldtl.getEmpAppraisalDtl(null, (Long.valueOf(aprisalId)), null,
						null, null, "F"));
			}
			loaddtlRslt();
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void editEmpAppraisalDtlDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Selected appraisaldtl.Id ");
			if (tblEmpAprisalDtl.getValue() != null) {
				EmpAppraisalDtlDM empAppraisalDtlDM = new EmpAppraisalDtlDM();
				empAppraisalDtlDM = beanEmpAppraisalDtlDM.getItem(tblEmpAprisalDtl.getValue()).getBean();
				Long uom = empAppraisalDtlDM.getAppraiseeid();
				Collection<?> uomid = cbapprsename.getItemIds();
				for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbapprsename.getItem(itemId);
					// Get the actual bean and use the data
					EmployeeDM st = (EmployeeDM) item.getBean();
					if (uom != null && uom.equals(st.getEmployeeid())) {
						cbapprsename.setValue(itemId);
					}
				}
				Long som = empAppraisalDtlDM.getKpiid();
				Collection<?> somid = cbkpiname.getItemIds();
				for (Iterator<?> iterator = somid.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbkpiname.getItem(itemId);
					// Get the actual bean and use the data
					KpiDM kt = (KpiDM) item.getBean();
					if (som != null && som.equals(kt.getKpiId())) {
						cbkpiname.setValue(itemId);
					}
				}
				Long pom = empAppraisalDtlDM.getApprlevelid();
				Collection<?> pomid = cvApprlevelname.getItemIds();
				for (Iterator<?> iterator = pomid.iterator(); iterator.hasNext();) {
					Object itemId = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cvApprlevelname.getItem(itemId);
					// Get the actual bean and use the data
					AppraisalLevelsDM pt = (AppraisalLevelsDM) item.getBean();
					if (pom != null && pom.equals(pt.getApprlevelid())) {
						cvApprlevelname.setValue(itemId);
					}
				}
				tfkpiRating.setValue(empAppraisalDtlDM.getKpirating().toString());
				comments.setValue(empAppraisalDtlDM.getComments());
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
			assemblesearchlayout();
		}
	}
	
	private void resetsearch() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbEmployee.setReadOnly(false);
		cbEmployee.setValue(null);
		cbEmployee.setComponentError(null);
		cbKPIGrpName.setReadOnly(false);
		cbKPIGrpName.setValue(null);
		cbKPIGrpName.setComponentError(null);
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		loadSrchRslt();
	}
	
	// ResetSearchDetails the field values to default values
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbapprsename.setValue(null);
		cbapprsename.setComponentError(null);
		cbkpiname.setValue(null);
		cbkpiname.setComponentError(null);
		cvApprlevelname.setValue(null);
		cvApprlevelname.setComponentError(null);
		tfkpiRating.setValue("0");
		tfkpiRating.setNullRepresentation("");
		comments.setValue("");
		comments.setNullRepresentation("");
		resetsearch();
	}
	
	// Method to implement about add button functionality
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		hluserInputlayout.removeAllComponents();
		hlUserIPContainer.addComponent(hluserInputlayout);
		resetFields();
		empAppraisalDtlresetFields();
		assembleuserinputlayout();
		loaddtlRslt();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hluserInputlayout));
		hlCmdBtnLayout.setVisible(false);
		cbEmployee.setRequired(true);
		cbKPIGrpName.setRequired(true);
		tblMstScrSrchRslt.setVisible(false);
		btnAddEmpAprisalDtl.setCaption("Add");
		tblEmpAprisalDtl.setVisible(true);
	}
	
	// Method to get the audit history details
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Aprisal. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_HCM_Emp_Appraisal);
		UI.getCurrent().getSession().setAttribute("audittablepk", aprisalId);
	}
	
	// Method to cancel and get back to the home page from edit mode
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		hlUserIPContainer.removeAllComponents();
		assemblesearchlayout();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		cbEmployee.setRequired(false);
		cbKPIGrpName.setRequired(false);
		resetFields();
		loadSrchRslt();
		empAppraisalDtlresetFields();
		showAuditDetails();
	}
	
	// Method to implement about edit button functionality
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		hluserInputlayout.removeAllComponents();
		hlUserIPContainer.addComponent(hluserInputlayout);
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		cbEmployee.setRequired(true);
		cbKPIGrpName.setRequired(true);
		assembleuserinputlayout();
		resetFields();
		editEmpAppraisalHdrDetails();
		editEmpAppraisalDtlDetails();
	}
	
	private void empAppraisalDtlresetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbapprsename.setValue(null);
		cbapprsename.setComponentError(null);
		cbkpiname.setValue(null);
		cbkpiname.setComponentError(null);
		cvApprlevelname.setValue(null);
		cvApprlevelname.setComponentError(null);
		tfkpiRating.setValue("0");
		comments.setValue("");
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void validateDetails() throws ValidationException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		cbEmployee.setComponentError(null);
		cbAppYear.setComponentError(null);
		pdfappdate.setComponentError(null);
		cbKPIGrpName.setComponentError(null);
		errorFlag = false;
		if (cbEmployee.getValue() == null) {
			cbEmployee.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbEmployee.getValue());
			errorFlag = true;
		}
		if (cbAppYear.getValue() == null) {
			cbAppYear.setComponentError(new UserError(GERPErrorCodes.NULL_Appraisal_Year));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbAppYear.getValue());
			errorFlag = true;
		}
		if (pdfappdate.getValue() == null) {
			pdfappdate.setComponentError(new UserError(GERPErrorCodes.NULL_Appraisal_Date));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + pdfappdate.getValue());
			errorFlag = true;
		}
		if (cbKPIGrpName.getValue() == null) {
			cbKPIGrpName.setComponentError(new UserError(GERPErrorCodes.NULL_KPI_GROUP_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbKPIGrpName.getValue());
			errorFlag = true;
		}
		if (tblEmpAprisalDtl.size() == 0) {
			cbapprsename.setComponentError(new UserError(GERPErrorCodes.NULL_APPRAISAL_NAME));
			cbkpiname.setComponentError(new UserError(GERPErrorCodes.NULL_KPI_NAME));
			cvApprlevelname.setComponentError(new UserError(GERPErrorCodes.NULL_LEVEL_NAME));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private boolean validateDtl() {
		boolean isValid = true;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbapprsename.getValue() == null)) {
			cbapprsename.setComponentError(new UserError(GERPErrorCodes.NULL_APPRAISAL_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbapprsename.getValue());
			isValid = false;
		} else {
			cbapprsename.setComponentError(null);
		}
		if ((cbkpiname.getValue() == null)) {
			cbkpiname.setComponentError(new UserError(GERPErrorCodes.NULL_KPI_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbkpiname.getValue());
			isValid = false;
		} else {
			cbkpiname.setComponentError(null);
		}
		if ((cvApprlevelname.getValue() == null)) {
			cvApprlevelname.setComponentError(new UserError(GERPErrorCodes.NULL_LEVEL_NAME));
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cvApprlevelname.getValue());
			isValid = false;
		} else {
			cvApprlevelname.setComponentError(null);
		}
		return isValid;
	}
	
	// Method to implement about validations to the required input fields
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		EmpAppraisalHdrDM empAppHdrObj = new EmpAppraisalHdrDM();
		if (tblMstScrSrchRslt.getValue() != null) {
			empAppHdrObj = beanEmpAppraisalHdrDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
		}
		if (cbEmployee.getValue() != null) {
			empAppHdrObj.setEmployeeid(Long.valueOf(cbEmployee.getValue().toString()));
		}
		if (cbAppYear.getValue() != null) {
			empAppHdrObj.setApryear((String) cbAppYear.getValue());
		}
		empAppHdrObj.setAprdt((Date) pdfappdate.getValue());
		empAppHdrObj.setCloseddt((Date) pdfclsddate.getValue());
		empAppHdrObj.setEmpsignoffdt((Date) pdfempsgndate.getValue());
		if (tfoverrtng.getValue() != null) {
			empAppHdrObj.setOverallrating((String) tfoverrtng.getValue());
		}
		if (cbKPIGrpName.getValue() != null) {
			empAppHdrObj.setKpigrpid(Long.valueOf(cbKPIGrpName.getValue().toString()));
		}
		if (taComments.getValue() != null) {
			empAppHdrObj.setHrcomments((String) taComments.getValue());
		}
		if (taRemarks.getValue() != null) {
			empAppHdrObj.setEmpremarks((String) taRemarks.getValue());
		}
		if (cbstatus.getValue() != null) {
			empAppHdrObj.setApprstatus((String) cbstatus.getValue());
		}
		if (chkpromflag.getValue().equals(true)) {
			empAppHdrObj.setPromotionflag("Y");
		} else {
			empAppHdrObj.setPromotionflag("N");
		}
		if (chkempagd.getValue().equals(true)) {
			empAppHdrObj.setEmpagreed("Y");
		} else {
			empAppHdrObj.setEmpagreed("N");
		}
		empAppHdrObj.setLastupdateddt(DateUtils.getcurrentdate());
		empAppHdrObj.setLastupdatedby(username);
		serviceAppraisalhdr.saveAndUpdate(empAppHdrObj);
		@SuppressWarnings("unchecked")
		Collection<EmpAppraisalDtlDM> itemIds = (Collection<EmpAppraisalDtlDM>) tblEmpAprisalDtl.getVisibleItemIds();
		for (EmpAppraisalDtlDM save : (Collection<EmpAppraisalDtlDM>) itemIds) {
			save.setAppraisalid(Long.valueOf(empAppHdrObj.getAppraisalid().toString()));
			serviceAppraisaldtl.saveAndUpdate(save);
		}
		empAppraisalDtlresetFields();
		resetFields();
		loadSrchRslt();
		aprisalId = null;
		loaddtlRslt();
	}
	
	private void saveempAppraisalDtlListDetails() {
		try {
			int count = 0;
			for (EmpAppraisalDtlDM empAppraisalDtlDM : listEmpApprDtls) {
				if (empAppraisalDtlDM.getAppraiseeid() == ((EmployeeDM) cbapprsename.getValue()).getEmployeeid()) {
					count++;
					break;
				}
			}
			if (count == 0) {
				EmpAppraisalDtlDM empAppDtlObj = new EmpAppraisalDtlDM();
				if (tblEmpAprisalDtl.getValue() != null) {
					empAppDtlObj = beanEmpAppraisalDtlDM.getItem(tblEmpAprisalDtl.getValue()).getBean();
					listEmpApprDtls.remove(empAppDtlObj);
				}
				empAppDtlObj.setAppraiseeid(((EmployeeDM) cbapprsename.getValue()).getEmployeeid());
				empAppDtlObj.setFirstlastname(((EmployeeDM) cbapprsename.getValue()).getFirstlastname());
				empAppDtlObj.setKpiid(((KpiDM) cbkpiname.getValue()).getKpiId());
				empAppDtlObj.setKpiName(((KpiDM) cbkpiname.getValue()).getKpiName());
				empAppDtlObj.setApprlevelid(((AppraisalLevelsDM) cvApprlevelname.getValue()).getApprlevelid());
				empAppDtlObj.setLevelname(((AppraisalLevelsDM) cvApprlevelname.getValue()).getLevelname());
				empAppDtlObj.setKpirating(Long.valueOf(tfkpiRating.getValue()));
				empAppDtlObj.setComments(comments.getValue());
				empAppDtlObj.setLastupdateddt(DateUtils.getcurrentdate());
				empAppDtlObj.setLastupdatedby(username);
				listEmpApprDtls.add(empAppDtlObj);
				loaddtlRslt();
				empAppraisalDtlresetFields();
			} else {
				cbapprsename.setComponentError(new UserError("Appraisal Name Already Exist.."));
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee list
	 */
	private void loadEmployeeList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading EmployeeList");
			BeanContainer<Long, EmployeeDM> beanEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
			beanEmployee.setBeanIdProperty("employeeid");
			beanEmployee.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, employeeid,
					null, null, null, "P"));
			cbEmployee.setContainerDataSource(beanEmployee);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadAppraisalYearList()-->this function is used for load the appraisal year list
	 */
	private void loadAppraisalYearList() {
		int i;
		int year = 1990;
		for (i = 0; i <= 50; i++) {
			year = year + 1;
			cbAppYear.addItem(year + "");
			System.out.println("year is" + year);
		}
	}
	
	/*
	 * loadKpiGroupNameList()-->this function is used for load the kpi group name list
	 */
	private void loadKpiGroupNameList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading KpiGroupNameList");
			BeanContainer<Long, KpiGroupDM> beankpigroup = new BeanContainer<Long, KpiGroupDM>(KpiGroupDM.class);
			beankpigroup.setBeanIdProperty("kpigrpid");
			beankpigroup.addAll(serviceKPIGroup.getkpigrouplist(null, null, companyid, null, "Active", "P"));
			cbKPIGrpName.setContainerDataSource(beankpigroup);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadAppraiseeNameList()-->this function is used for load the appraisee name list
	 */
	private void loadAppraiseeNameList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "loading AppraiseeNameList");
			BeanItemContainer<EmployeeDM> beanappraiseename = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
			beanappraiseename.addAll(serviceEmployee.getEmployeeList(null, null, null, "Active", companyid, employeeid,
					null, null, null, "P"));
			cbapprsename.setContainerDataSource(beanappraiseename);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadKpiNameList()-->this function is used for load the kpi name list
	 */
	private void loadKpiNameList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "loading KpiNameList");
			BeanItemContainer<KpiDM> beankpi = new BeanItemContainer<KpiDM>(KpiDM.class);
			beankpi.addAll(serviceKPI.getKpiList(null, null, null, null, "Active", "F"));
			cbkpiname.setContainerDataSource(beankpi);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	 * loadApprlevelNameList()-->this function is used for load the apprlevel name list
	 */
	private void loadApprlevelNameList() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "loading ApprlevelNameList");
			BeanItemContainer<AppraisalLevelsDM> beanlevels = new BeanItemContainer<AppraisalLevelsDM>(
					AppraisalLevelsDM.class);
			beanlevels.addAll(serviceAppraisalLevel.getAppraisalLevelsList(null, null, null, "Active", "F"));
			cvApprlevelname.setContainerDataSource(beanlevels);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void deleteDtls() {
		try {
			EmpAppraisalDtlDM empAppraisalObj = new EmpAppraisalDtlDM();
			if (tblEmpAprisalDtl.getValue() != null) {
				empAppraisalObj = beanEmpAppraisalDtlDM.getItem(tblEmpAprisalDtl.getValue()).getBean();
				listEmpApprDtls.remove(empAppraisalObj);
				empAppraisalDtlresetFields();
				tblEmpAprisalDtl.setValue("");
				loaddtlRslt();
				btnDelete.setEnabled(false);
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
