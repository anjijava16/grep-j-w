/**
 * File Name	:	EmployeeDetail.java
 * Description	:	This Screen Purpose for Modify the Employee Details.Add the Employee  details process should be directly added in DB.
 * Author		:	JOEL GLINDAN D
 * Date			:	SEP 17, 2014
 * Modification :   
 * Modified By  :   
 * Description 	:
 *
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version      Date           	Modified By      		Remarks
 * 0.1          SEP 17, 2014   	JOEL GLINDAN D		Initial Version		
 * 
 */
package com.gnts.base.mst;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.BranchDM;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.domain.mst.CountryDM;
import com.gnts.base.domain.mst.DepartmentDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.ParameterDM;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.domain.mst.UserDM;
import com.gnts.base.service.mst.BranchService;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.base.service.mst.CountryService;
import com.gnts.base.service.mst.DepartmentService;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.base.service.mst.UserService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPSaveNotification;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.erputil.util.RandomNoGenerator;
import com.gnts.hcm.domain.mst.AllowanceDM;
import com.gnts.hcm.domain.mst.DeductionDM;
import com.gnts.hcm.domain.mst.DesignationDM;
import com.gnts.hcm.domain.mst.EarningsDM;
import com.gnts.hcm.domain.mst.EmployeeAddressDM;
import com.gnts.hcm.domain.mst.EmployeeBankDM;
import com.gnts.hcm.domain.mst.EmployeeContactsDM;
import com.gnts.hcm.domain.mst.EmployeeDependntsDM;
import com.gnts.hcm.domain.mst.EmployeeDtlsDM;
import com.gnts.hcm.domain.mst.EmployeeEducationDM;
import com.gnts.hcm.domain.mst.EmployeeIdentitiesDM;
import com.gnts.hcm.domain.mst.EmployeeImmgrtnDM;
import com.gnts.hcm.domain.mst.EmployeeSkillDM;
import com.gnts.hcm.domain.mst.EmploymentStatusDM;
import com.gnts.hcm.domain.mst.EmploymentTypeDM;
import com.gnts.hcm.domain.mst.GradeAllowanceDM;
import com.gnts.hcm.domain.mst.GradeDM;
import com.gnts.hcm.domain.mst.GradeDeductionDM;
import com.gnts.hcm.domain.mst.GradeEarningsDM;
import com.gnts.hcm.domain.mst.PayPeriodDM;
import com.gnts.hcm.domain.mst.QualificationDM;
import com.gnts.hcm.domain.mst.SkillsDM;
import com.gnts.hcm.domain.txn.EmployeeAllowanceDM;
import com.gnts.hcm.domain.txn.EmployeeDeductionDM;
import com.gnts.hcm.domain.txn.EmployeeEarningDM;
import com.gnts.hcm.domain.txn.JobCandidateDM;
import com.gnts.hcm.service.mst.DeductionService;
import com.gnts.hcm.service.mst.DesignationService;
import com.gnts.hcm.service.mst.EarningsService;
import com.gnts.hcm.service.mst.EmployeeAddressService;
import com.gnts.hcm.service.mst.EmployeeBankService;
import com.gnts.hcm.service.mst.EmployeeContactsService;
import com.gnts.hcm.service.mst.EmployeeDependntsService;
import com.gnts.hcm.service.mst.EmployeeDtlsService;
import com.gnts.hcm.service.mst.EmployeeEducationService;
import com.gnts.hcm.service.mst.EmployeeIdentitiedService;
import com.gnts.hcm.service.mst.EmployeeImmgrtnService;
import com.gnts.hcm.service.mst.EmployeeSkillService;
import com.gnts.hcm.service.mst.EmploymentStatusService;
import com.gnts.hcm.service.mst.EmploymentTypeService;
import com.gnts.hcm.service.mst.GradeAllowanceService;
import com.gnts.hcm.service.mst.GradeService;
import com.gnts.hcm.service.mst.PayPeriodService;
import com.gnts.hcm.service.mst.QualificationService;
import com.gnts.hcm.service.mst.SkillsService;
import com.gnts.hcm.service.txn.EmployeeAllowanceService;
import com.gnts.hcm.service.txn.EmployeeDeductionService;
import com.gnts.hcm.service.txn.EmployeeEarningService;
import com.gnts.hcm.service.txn.JobCandidateService;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
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
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class Employee extends BaseUI {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(Employee.class);
	private GradeAllowanceService serviceGradeAllowance = (GradeAllowanceService) SpringContextHelper
			.getBean("GradeAllowance");
	private DeductionService serviceDeduction = (DeductionService) SpringContextHelper.getBean("Deduction");
	private EmployeeService servicebeanEmployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private EmploymentTypeService serviceEmploymentType = (EmploymentTypeService) SpringContextHelper
			.getBean("EmploymentType");
	private GradeService serviceGrade = (GradeService) SpringContextHelper.getBean("Grade");
	private DesignationService serviceDesignation = (DesignationService) SpringContextHelper.getBean("Designation");
	private PayPeriodService servicePayPeriodService = (PayPeriodService) SpringContextHelper.getBean("PayPeriod");
	private JobCandidateService servicejobcandidate = (JobCandidateService) SpringContextHelper.getBean("JobCandidate");
	private EmploymentStatusService serviceEmploymentStatus = (EmploymentStatusService) SpringContextHelper
			.getBean("EmploymentStatus");
	private EmployeeSkillService serviceemployeeskill = (EmployeeSkillService) SpringContextHelper
			.getBean("EmployeeSkill");
	private EmployeeAddressService servempaddr = (EmployeeAddressService) SpringContextHelper
			.getBean("EmployeeAddress");
	private EmployeeBankService serviceempbnk = (EmployeeBankService) SpringContextHelper.getBean("EmployeeBank");
	private EmployeeEducationService serviceempedu = (EmployeeEducationService) SpringContextHelper
			.getBean("EmployeeEducation");
	private EmployeeDependntsService serviceempdeps = (EmployeeDependntsService) SpringContextHelper
			.getBean("EmployeeDepend");
	private EmployeeContactsService servempcontact = (EmployeeContactsService) SpringContextHelper.getBean("empcont");
	private EmployeeImmgrtnService servempimmgrtn = (EmployeeImmgrtnService) SpringContextHelper
			.getBean("EmployeeImmgrtn");
	private EmployeeIdentitiedService serviceempides = (EmployeeIdentitiedService) SpringContextHelper
			.getBean("EmployeeIdentities");
	private EmployeeDtlsService servempdtls = (EmployeeDtlsService) SpringContextHelper.getBean("Employeedtls");
	private DepartmentService servicebeandepartmant = (DepartmentService) SpringContextHelper.getBean("department");
	private SkillsService serviceSkills = (SkillsService) SpringContextHelper.getBean("Skills");
	private BranchService servicebeanBranch = (BranchService) SpringContextHelper.getBean("mbranch");
	private UserService servicebeanUser = (UserService) SpringContextHelper.getBean("user");
	private CountryService serviceCountry = (CountryService) SpringContextHelper.getBean("country");
	private CompanyLookupService serviceCompanyLookup = (CompanyLookupService) SpringContextHelper
			.getBean("companyLookUp");
	private QualificationService serviceQualification = (QualificationService) SpringContextHelper
			.getBean("Qualification");
	private SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private EarningsService serviceEarnings = (EarningsService) SpringContextHelper.getBean("Earnings");
	private BeanContainer<Long, DepartmentDM> beanDepartment = null;
	private BeanItemContainer<EmployeeDM> beanEmployee = null;
	private BeanItemContainer<EmployeeAddressDM> beanEmployeeAddress = new BeanItemContainer<EmployeeAddressDM>(
			EmployeeAddressDM.class);
	private BeanItemContainer<EmployeeSkillDM> beanEmployeeskill = new BeanItemContainer<EmployeeSkillDM>(
			EmployeeSkillDM.class);
	private BeanItemContainer<EmployeeDtlsDM> beanEmployeedtls = new BeanItemContainer<EmployeeDtlsDM>(
			EmployeeDtlsDM.class);
	private BeanItemContainer<EmployeeContactsDM> beanEmployeecontacts = new BeanItemContainer<EmployeeContactsDM>(
			EmployeeContactsDM.class);
	private BeanItemContainer<EmployeeImmgrtnDM> beanemployeeimmgrtn = new BeanItemContainer<EmployeeImmgrtnDM>(
			EmployeeImmgrtnDM.class);
	private BeanItemContainer<EmployeeBankDM> beanemployeebank = new BeanItemContainer<EmployeeBankDM>(
			EmployeeBankDM.class);
	private BeanItemContainer<EmployeeEducationDM> beanemployeeeducation = new BeanItemContainer<EmployeeEducationDM>(
			EmployeeEducationDM.class);
	private BeanItemContainer<EmployeeDependntsDM> beanemployeedependent = new BeanItemContainer<EmployeeDependntsDM>(
			EmployeeDependntsDM.class);
	private BeanItemContainer<EmployeeIdentitiesDM> beanemployeeidentities = new BeanItemContainer<EmployeeIdentitiesDM>(
			EmployeeIdentitiesDM.class);
	private EmployeeEarningService serviceEmployeeEarning = (EmployeeEarningService) SpringContextHelper
			.getBean("EmployeeEarning");
	private EmployeeDeductionService serviceEmployeeDeduction = (EmployeeDeductionService) SpringContextHelper
			.getBean("EmployeeDeduction");
	private EmployeeAllowanceService serviceEmpAllowance = (EmployeeAllowanceService) SpringContextHelper
			.getBean("EmployeeAllowance");
	private BeanContainer<Long, BranchDM> beanBranch = null;
	private BeanContainer<Long, CountryDM> beanCountry = null;
	private BeanContainer<String, CompanyLookupDM> beanCompanyLookUp = null;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	private int recordCnt = 0;
	private Long companyid, moduleId, branchID;
	private String username;
	private Date dtdob, dtfrmdate, immgissdf, dtlscondf, dtlspromotindf, dtlsreleavdf;
	private Long scoredmark = 0L;
	private long totalmark = 0L;
	private Long headerID = null;
	private BigDecimal yearGrossAmt;
	// form layout for input controls
	private TextField tfEmployeeCode, tfFirstName, tfLastName, tfPhonenumber, tfEmailid, tfISDCode;
	private PopupDateField dfDateofBirth, dfDateofJoin;
	private ComboBox cbEmpStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private ComboBox cbGender, cbBranch, cbManager, cbDepartment, cbCountry;
	private CheckBox cbCreateUser;
	private HorizontalLayout hlimage = new HorizontalLayout();
	private HorizontalLayout hlISDCodePhoneLayout;
	private List<EmployeeAddressDM> empaddresslist = new ArrayList<EmployeeAddressDM>();
	private List<EmployeeSkillDM> employeeSkillList = new ArrayList<EmployeeSkillDM>();
	private List<EmployeeDtlsDM> employeedtlsList = new ArrayList<EmployeeDtlsDM>();
	private List<EmployeeContactsDM> employeecontList = new ArrayList<EmployeeContactsDM>();
	private List<EmployeeImmgrtnDM> employeeimmglist = new ArrayList<EmployeeImmgrtnDM>();
	private List<EmployeeBankDM> employeebanklist = new ArrayList<EmployeeBankDM>();
	private List<EmployeeEducationDM> employeeedulist = new ArrayList<EmployeeEducationDM>();
	private List<EmployeeDependntsDM> employeedepnlist = new ArrayList<EmployeeDependntsDM>();
	private List<EmployeeIdentitiesDM> employeeidentitieslist = new ArrayList<EmployeeIdentitiesDM>();
	// Employee Address Declaration
	private ComboBox cbempadrscountryid, cbempadrstype;
	private TextField tfEmpaddressln1, tfEmpaddressln2, tfempadrspostcode, tfempaddresscityname, tfempaddressstatename;
	private ComboBox cbEmpaddressstatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER,
			BASEConstants.USER_STATUS);
	private Table tblempaddress = new GERPTable();
	private Button btnaddempadd = new GERPButton("Add", "addbt", this);
	// Employee Skill Declaration
	private ComboBox cbempskillid, cbempskilllevel;
	private CheckBox tfempskillisprimary;
	private ComboBox cbempskillstatus = new GERPComboBox("Status", BASEConstants.M_HCM_EMPLOYEE_SKILL,
			BASEConstants.SKILL_STATUS);
	private Table tblempskilltbl = new GERPTable();
	private Button btnaddempskill = new GERPButton("Add", "addbt", this);
	// Employee Dtls Declaration
	private ComboBox cbempdtlsbitrhcountry, cbempdtlsemptypeid, cbempdtlscandid, cbempdtlsgradid, cbempdtlspayperid,
			cbempdtlsstatusid, cbempdtlsnationalid, cbempdtlsdesignaton, cbempdtlsmaritalstatus;
	private TextField tfempdtlsPFno, tfempdtlsESIno, tfempdtlsPENSON, tfempdtlsbirthplace, tfempdtlsstatusreasion;
	private PopupDateField dfempdtlsconfdate, dfempdtlslasttpromotdate, dfempdtlsreleaveddate, dfempdtlsdeathdtate;
	private Table tblempdtls = new GERPTable();
	private Button btnaddempdtls = new GERPButton("Add", "addbt", this);
	// Employee contact Declaration
	private TextField tfempcontactname, tfempcontrelationship, tfempcontphno, tfempcontmono;
	private ComboBox cbempcontstatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private Table tblempcont = new GERPTable();
	private Button btnaddempcont = new GERPButton("Add", "addbt", this);
	// Employee Immigration Declaration
	private TextField tfempimmgdocno, tfempimmgissuedby, tfempimmgdocremark;
	private ComboBox cbempimmgdoctype;
	private ComboBox cbempimmgstatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private PopupDateField dfempimmgissuedf, dfempimmgexpirydf;
	private Table tblempimmg = new GERPTable();
	private Button btnaddempimmg = new GERPButton("Add", "addbt", this);
	// Employee Bank Declaration
	private TextField tfempbankname, tfempacctno, tfemproutingcode, tfempbranchname;
	private ComboBox cbempaccttype;
	private ComboBox cbempbankstatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private Table tblempbank = new GERPTable();
	private Button btnaddempbank = new GERPButton("Add", "addbt", this);
	// Employee Education Declaration
	private TextField tfempedusubject, tfempeduinstitnname, tfempeduunivname, tfempeduscoredmark, tfempedutotalmark;
	private ComboBox cbempedugradeached, cbempeduqualtnid;
	private PopupDateField dfempedudurtnfrm, dfempedudurtnto;
	private ComboBox cbempedustatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private Table tblempedu = new GERPTable();
	private Button btnaddempedu = new GERPButton("Add", "addbt", this);
	// Employee Dependnts Declaration
	private ComboBox cbempdepnreltionship, cbempdepngender;
	private TextField tfempdepndepname;
	private PopupDateField dfempdepndob;
	private ComboBox cbempdepnstatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private Table tblempdepn = new GERPTable();
	private Button btnaddempdepn = new GERPButton("Add", "addbt", this);
	// employee identities Declaration
	private ComboBox cbempidenname;
	private TextField tfempidenref, tfempidenissuedby;
	private PopupDateField dfempidendate;
	private ComboBox cbempidenstatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
	private Table tblempiden = new GERPTable();
	private Button btnaddempiden = new GERPButton("Add", "addbt", this);
	// private Date date;
	private String companyCode;
	private Long userId, employeeid, countryid, timezoneId;
	private String isdCode;
	private int flag = 0, recordCntempadd = 0, recordcntempskill = 0, recordcntempdtls = 0, recordcntempcont = 0,
			recordcntempimmt = 0, recordcntempbank = 0, recordcntempeducation = 0, recordcntempdependn = 0,
			recordcntempidentities = 0;
	
	// Constructor received the parameters from Login UI class
	public Employee() {
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		companyCode = UI.getCurrent().getSession().getAttribute("companyCode").toString();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside Employee() constructor");
		moduleId = (Long) UI.getCurrent().getSession().getAttribute("moduleId");
		branchID = (Long) UI.getCurrent().getSession().getAttribute("branchId");
		countryid = (Long) UI.getCurrent().getSession().getAttribute("countryid");
		timezoneId = (Long) UI.getCurrent().getSession().getAttribute("timezoneId");
		buildView();
	}
	
	private void buildView() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting employee UI");
		tfEmployeeCode = new GERPTextField("Employee Code");
		tfEmployeeCode.setWidth("150");
		tfEmployeeCode.setMaxLength(10);
		tfEmployeeCode.setReadOnly(true);
		tfFirstName = new GERPTextField("First Name");
		tfFirstName.setWidth("150");
		cbCountry = new GERPComboBox("Country");
		cbempdtlsbitrhcountry = new GERPComboBox("Birth Country");
		cbempdtlsnationalid = new GERPComboBox("Nationality");
		cbempadrscountryid = new GERPComboBox("Country");
		cbCountry.setItemCaptionPropertyId("countryName");
		cbCountry.setImmediate(true);
		cbCountry.setNullSelectionAllowed(false);
		cbCountry.setWidth("150");
		cbCountry.setRequired(true);
		loadCountryList();
		cbCountry.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					loadISDCode();
					loadBranchList();
				}
			}
		});
		cbDepartment = new GERPComboBox("Department");
		cbDepartment.setItemCaptionPropertyId("deptname");
		cbDepartment.setImmediate(true);
		cbDepartment.setNullSelectionAllowed(false);
		cbDepartment.setWidth("200");
		cbDepartment.setRequired(false);
		cbDepartment.setValue(0L);
		tfLastName = new GERPTextField("Last Name");
		tfLastName.setWidth("150");
		tfLastName.setMaxLength(50);
		tfLastName.setRequired(true);
		tfISDCode = new GERPTextField("");
		tfISDCode.setReadOnly(true);
		tfISDCode.setWidth("30");
		tfISDCode.setCaption(null);
		tfPhonenumber = new GERPTextField("");
		tfPhonenumber.setWidth("125");
		tfPhonenumber.setCaption(null);
		tfPhonenumber.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfPhonenumber.setComponentError(null);
				if (tfPhonenumber.getValue() != null) {
					if (!tfPhonenumber.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
						tfPhonenumber.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
					} else {
						tfPhonenumber.setComponentError(null);
					}
				}
			}
		});
		tfEmailid = new GERPTextField("E-Mail");
		tfEmailid.setWidth("150");
		tfEmailid.setMaxLength(40);
		dfDateofBirth = new GERPPopupDateField("Date Of Birth");
		dfDateofBirth.setRequired(true);
		dfDateofBirth.setImmediate(true);
		dfDateofBirth.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (dfDateofBirth.getValue() != null) {
					if (dfDateofBirth.getValue().after(new Date()) || dfDateofBirth.getValue().equals(new Date())) {
						dfDateofBirth.setComponentError(new UserError(GERPErrorCodes.DATE_OF_BIRTH_VALIDATION));
					} else {
						dtdob = dfDateofBirth.getValue();
						dfDateofBirth.setComponentError(null);
					}
				}
			}
		});
		cbGender = new GERPComboBox("Gender");
		cbGender.setItemCaptionPropertyId("lookupname");
		cbGender.setImmediate(true);
		cbGender.setNullSelectionAllowed(false);
		cbGender.setWidth("150");
		cbCreateUser = new CheckBox("Create User");
		cbBranch = new GERPComboBox("Branch");
		cbBranch.setItemCaptionPropertyId("branchName");
		cbBranch.setRequired(true);
		cbBranch.setImmediate(true);
		cbBranch.setNullSelectionAllowed(false);
		cbBranch.setWidth("150");
		loadBranchList();
		dfDateofJoin = new GERPPopupDateField("Date Of Join");
		dfDateofJoin.setRequired(true);
		dfDateofJoin.setImmediate(true);
		dfDateofJoin.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				try {
					if (dfDateofJoin.getValue().before(dtdob)) {
						dfDateofJoin.setComponentError(new UserError(GERPErrorCodes.DATE_OF_JOIN_VALIDATION));
					} else {
						dfDateofJoin.setComponentError(null);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		cbManager = new GERPComboBox("Reporting To");
		cbManager.setItemCaptionPropertyId("firstname");
		cbManager.setImmediate(true);
		cbManager.setNullSelectionAllowed(false);
		cbManager.setWidth("150");
		loadEmployeeList();
		// Employee Address Components Definition
		tfEmpaddressln1 = new GERPTextField("Address Line1");
		tfEmpaddressln1.setWidth("200");
		tfEmpaddressln1.setRequired(true);
		tfEmpaddressln2 = new GERPTextField("Address Line2");
		tfEmpaddressln2.setWidth("200");
		tfempaddresscityname = new GERPTextField("City");
		tfempaddresscityname.setWidth("200");
		tfempaddresscityname.setRequired(true);
		cbempadrstype = new GERPComboBox("Address Type");
		cbempadrstype.setItemCaptionPropertyId("lookupname");
		cbempadrstype.setWidth("200");
		cbempadrstype.setRequired(true);
		loadaddresstype();
		tfempaddressstatename = new GERPTextField("State");
		tfempaddressstatename.setWidth("200");
		tfempaddressstatename.setRequired(true);
		cbempadrscountryid.setItemCaptionPropertyId("countryName");
		cbempadrscountryid.setWidth("200");
		cbempadrscountryid.setRequired(true);
		loadadrsCountryList();
		tfempadrspostcode = new GERPTextField("Postcode");
		tfempadrspostcode.setWidth("200");
		tfempadrspostcode.setRequired(true);
		btnaddempadd.setStyleName("add");
		btnaddempadd.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee Address
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validationaddress();
					saveEmployeeAddress();
					empaddressResetFields();
					new GERPSaveNotification();
					// }
				}
				catch (Exception e) {
				}
				hlSrchContainer.setVisible(false);
				btnAuditRecords.setEnabled(false);
				logger.error("Company ID : " + UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
						+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
						+ " > " + "Exception ");
			}
		});
		// ClickListener for Employee Address Tbl
		tblempaddress.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblempaddress.isSelected(event.getItemId())) {
					tblempaddress.setImmediate(true);
					btnaddempadd.setCaption("Add");
					btnaddempadd.setStyleName("savebt");
					empaddressResetFields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddempadd.setCaption("Update");
					btnaddempadd.setStyleName("savebt");
					editempaddress();
				}
			}
		});
		// Employee Skill Definition
		cbempskillid = new GERPComboBox("Skill");
		cbempskillid.setItemCaptionPropertyId("skillName");
		cbempskillid.setWidth("200");
		cbempskillid.setRequired(true);
		loadempskilllsist();
		tfempskillisprimary = new CheckBox("Primary Skills");
		tfempskillisprimary.setWidth("200");
		cbempskilllevel = new GERPComboBox("Skill Level");
		cbempskilllevel.setItemCaptionPropertyId("lookupname");
		cbempskilllevel.setWidth("200");
		loadskilllavel();
		btnaddempskill.setStyleName("add");
		btnaddempskill.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee skill
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validationskill();
					saveEmpSKillDetails();
					new GERPSaveNotification();
				}
				catch (Exception e) {
				}
				hlSrchContainer.setVisible(false);
				btnAuditRecords.setEnabled(false);
				logger.error("Company ID : " + UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
						+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
						+ " > " + "Exception ");
			}
		});
		// ClickListener for employee skill Tbl
		tblempskilltbl.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblempskilltbl.isSelected(event.getItemId())) {
					tblempskilltbl.setImmediate(true);
					btnaddempskill.setCaption("Add");
					btnaddempskill.setStyleName("savebt");
					empskillResetfields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddempskill.setCaption("Update");
					btnaddempskill.setStyleName("savebt");
					editempskill();
				}
			}
		});
		// Employee Dtls Definition
		tfempdtlsbirthplace = new GERPTextField("Birth Place");
		tfempdtlsbirthplace.setWidth("200");
		tfempdtlsPENSON = new GERPTextField("Pension");
		tfempdtlsPENSON.setWidth("200");
		tfempdtlsESIno = new GERPTextField("ESI No");
		tfempdtlsESIno.setWidth("200");
		tfempdtlsPFno = new GERPTextField("PF No");
		tfempdtlsPFno.setWidth("200");
		cbempdtlsmaritalstatus = new GERPComboBox("Marital Status");
		cbempdtlsmaritalstatus.setItemCaptionPropertyId("lookupname");
		cbempdtlsmaritalstatus.setWidth("200");
		cbempdtlsmaritalstatus.setRequired(true);
		cbempdtlsmaritalstatus.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				if (cbempdtlsmaritalstatus.getValue() != null) {
					cbempdtlsmaritalstatus.setComponentError(null);
				}
			}
		});
		loadMaritalList();
		cbempdtlsbitrhcountry.setItemCaptionPropertyId("countryName");
		cbempdtlsbitrhcountry.setWidth("200");
		cbempdtlsbitrhcountry.setRequired(true);
		cbempdtlsbitrhcountry.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				if (cbempdtlsbitrhcountry.getValue() != null) {
					cbempdtlsbitrhcountry.setComponentError(null);
				}
			}
		});
		loadCountrydtlsbirthList();
		dfempdtlsconfdate = new GERPPopupDateField("Conformation Date");
		dfempdtlsconfdate.setImmediate(true);
		dfempdtlsconfdate.setWidth("180");
		dfempdtlsconfdate.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (dfempdtlsconfdate.getValue() != null) {
					if (dfempdtlsconfdate.getValue().after(new Date())
							|| dfempdtlsconfdate.getValue().equals(new Date())) {
						dfempdtlsconfdate.setComponentError(new UserError(
								GERPErrorCodes.NULL_EMPLOYEE_DTLS_CONFORMATIONDATE));
					} else {
						dtlscondf = dfempdtlsconfdate.getValue();
						dfempdtlsconfdate.setComponentError(null);
					}
				}
			}
		});
		dfempdtlslasttpromotdate = new GERPPopupDateField("Last Promotion Date");
		dfempdtlslasttpromotdate.setImmediate(true);
		dfempdtlslasttpromotdate.setWidth("180");
		dfempdtlslasttpromotdate.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				try {
					if (dfempdtlslasttpromotdate != null && dfempdtlslasttpromotdate.getValue().before(dtlscondf)) {
						dfempdtlslasttpromotdate.setComponentError(new UserError(
								GERPErrorCodes.NULL_EMPLOYEE_DTLS_LASTPROMOTIONDATE));
					} else {
						dtlspromotindf = dfempdtlslasttpromotdate.getValue();
						dfempdtlslasttpromotdate.setComponentError(null);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		dfempdtlsreleaveddate = new GERPPopupDateField("Releaved Date");
		dfempdtlsreleaveddate.setImmediate(true);
		dfempdtlsreleaveddate.setWidth("180");
		dfempdtlsreleaveddate.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				if (dfempdtlsreleaveddate != null) {
					try {
						if (dfempdtlsreleaveddate.getValue().before(dtlspromotindf)) {
							dfempdtlsreleaveddate.setComponentError(new UserError(
									GERPErrorCodes.NULL_EMPLOYEE_DTLS_RELEAVEDDATE));
						} else {
							dtlsreleavdf = dfempdtlsreleaveddate.getValue();
							dfempdtlsreleaveddate.setComponentError(null);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		dfempdtlsdeathdtate = new GERPPopupDateField("Death Date");
		dfempdtlsdeathdtate.setImmediate(true);
		dfempdtlsdeathdtate.setWidth("180");
		dfempdtlsdeathdtate.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				if (dfempdtlsdeathdtate != null) {
					try {
						if (dfempdtlsdeathdtate.getValue().before(dtlsreleavdf)) {
							dfempdtlsdeathdtate.setComponentError(new UserError(
									GERPErrorCodes.NULL_EMPLOYEE_DTLS_DEATHDATE));
						} else {
							dfempdtlsdeathdtate.setComponentError(null);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		cbempdtlsemptypeid = new GERPComboBox("Employee Type");
		cbempdtlsemptypeid.setItemCaptionPropertyId("empTypeName");
		cbempdtlsemptypeid.setWidth("200");
		cbempdtlsemptypeid.setRequired(true);
		cbempdtlsemptypeid.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				if (cbempdtlsemptypeid.getValue() != null) {
					cbempdtlsemptypeid.setComponentError(null);
				}
			}
		});
		loadempmenttypelist();
		cbempdtlscandid = new GERPComboBox("Candidate");
		cbempdtlscandid.setItemCaptionPropertyId("fulname");
		cbempdtlscandid.setWidth("200");
		cbempdtlscandid.setRequired(true);
		cbempdtlscandid.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbempdtlscandid.setComponentError(null);
				if (cbempdtlscandid.getValue() != null) {
					cbempdtlscandid.setComponentError(null);
				}
			}
		});
		loadjobcandidatelist();
		cbempdtlsgradid = new GERPComboBox("Grade Level");
		cbempdtlsgradid.setItemCaptionPropertyId("gradeDESC");
		cbempdtlsgradid.setWidth("200");
		cbempdtlsgradid.setRequired(true);
		cbempdtlsgradid.setRequired(true);
		loadempgradelist();
		cbempdtlsgradid.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbempdtlsgradid.getValue() != null) {
					cbempdtlsgradid.setComponentError(null);
				}
				loaddesignationlist();
			}
		});
		cbempdtlsdesignaton = new GERPComboBox("Designation");
		cbempdtlsdesignaton.setItemCaptionPropertyId("designationName");
		cbempdtlsdesignaton.setWidth("200");
		cbempdtlsdesignaton.setRequired(true);
		cbempdtlsdesignaton.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				if (cbempdtlsdesignaton.getValue() != null) {
					cbempdtlsdesignaton.setComponentError(null);
				}
			}
		});
		cbempdtlspayperid = new GERPComboBox("Pay Period");
		cbempdtlspayperid.setItemCaptionPropertyId("periodName");
		cbempdtlspayperid.setWidth("200");
		cbempdtlspayperid.setRequired(true);
		cbempdtlspayperid.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				if (cbempdtlspayperid.getValue() != null) {
					cbempdtlspayperid.setComponentError(null);
				}
			}
		});
		loadpayperiodlist();
		cbempdtlsstatusid = new GERPComboBox("Status");
		cbempdtlsstatusid.setItemCaptionPropertyId("empstatusdesc");
		cbempdtlsstatusid.setWidth("200");
		loademploymentstatuslist();
		cbempdtlsnationalid.setItemCaptionPropertyId("countryName");
		cbempdtlsnationalid.setWidth("200");
		cbempdtlsnationalid.setRequired(true);
		cbempdtlsnationalid.addBlurListener(new BlurListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				if (cbempdtlsnationalid.getValue() != null) {
					cbempdtlsnationalid.setComponentError(null);
				}
			}
		});
		loadCountrynationalList();
		tfempdtlsstatusreasion = new GERPTextField("Reason");
		tfempdtlsstatusreasion.setWidth("200");
		btnaddempdtls.setStyleName("add");
		btnaddempdtls.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for employee dtls
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validationdtls();
					saveEmpDtls();
					empdtlsResetfields();
					new GERPSaveNotification();
				}
				catch (Exception e) {
				}
				hlSrchContainer.setVisible(false);
				btnAuditRecords.setEnabled(false);
				/*
				 * try { throw new ERPException.SaveException(); }
				 */
				logger.error("Company ID : " + UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
						+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
						+ " > " + "Exception ");
			}
		});
		// ClickListener for Employee Dtls Tbl
		tblempdtls.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblempdtls.isSelected(event.getItemId())) {
					tblempdtls.setImmediate(true);
					btnaddempdtls.setCaption("Add");
					btnaddempdtls.setStyleName("savebt");
					empdtlsResetfields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddempdtls.setCaption("Update");
					btnaddempdtls.setStyleName("savebt");
					editempdtls();
				}
			}
		});
		// Employee Contact Definition
		tfempcontactname = new GERPTextField("Contact Name");
		tfempcontactname.setWidth("200");
		tfempcontactname.setRequired(true);
		tfempcontrelationship = new GERPTextField("Relationship");
		tfempcontrelationship.setWidth("200");
		tfempcontphno = new GERPTextField("Phone No");
		tfempcontphno.setWidth("200");
		tfempcontphno.setRequired(true);
		tfempcontphno.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				// tfempcontphno.setComponentError(null);
				if (!tfempcontphno.getValue().matches("^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$")) {
					tfempcontphno.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_CONTACTS_PHONENUMBER));
				} else {
					tfempcontphno.setComponentError(null);
				}
			}
		});
		tfempcontmono = new GERPTextField("Mobile No");
		tfempcontmono.setWidth("200");
		tfempcontmono.setRequired(true);
		tfempcontmono.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfempcontmono.setComponentError(null);
				if (tfempcontmono.getValue() != null) {
					if (!tfempcontmono.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
						tfempcontmono.setComponentError(new UserError(
								GERPErrorCodes.NULL_EMPLOYEE_CONTACTS_MOBILENUMBER));
					} else {
						tfempcontmono.setComponentError(null);
					}
				}
			}
		});
		btnaddempcont.setStyleName("add");
		btnaddempcont.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee Contact
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validationcontact();
					saveEmpContacts();
					empcontactsResetfields();
					new GERPSaveNotification();
				}
				catch (Exception e) {
				}
				logger.error("Company ID : " + UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
						+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
						+ " > " + "Exception ");
			}
		});
		// ClickListener for Employee Contact Tbl
		tblempcont.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblempcont.isSelected(event.getItemId())) {
					tblempcont.setImmediate(true);
					btnaddempcont.setCaption("Add");
					btnaddempcont.setStyleName("savebt");
					empcontactsResetfields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddempcont.setCaption("Update");
					btnaddempcont.setStyleName("savebt");
					editempcontacts();
				}
			}
		});
		// Employee Immigration Definition
		cbempimmgdoctype = new GERPComboBox("Document Type");
		cbempimmgdoctype.setItemCaptionPropertyId("lookupname");
		cbempimmgdoctype.setWidth("150");
		cbempimmgdoctype.setRequired(true);
		loadDOCType();
		tfempimmgdocno = new GERPTextField("Document No");
		tfempimmgdocno.setWidth("150");
		tfempimmgdocno.setRequired(true);
		dfempimmgissuedf = new GERPPopupDateField("Issue Date");
		dfempimmgissuedf.setWidth("150");
		dfempimmgissuedf.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (dfempimmgissuedf.getValue() != null) {
					if (dfempimmgissuedf.getValue().after(new Date()) || dfempimmgissuedf.getValue().equals(new Date())) {
						dfempimmgissuedf.setComponentError(new UserError(
								GERPErrorCodes.NULL_EMPLOYEE_IMMIGRATION_IMMGISSUDATE));
					} else {
						immgissdf = dfempimmgissuedf.getValue();
						dfempimmgissuedf.setComponentError(null);
					}
				}
			}
		});
		dfempimmgexpirydf = new GERPPopupDateField("Expiry Date");
		dfempimmgexpirydf.setWidth("150");
		dfempimmgexpirydf.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				if (dfempimmgexpirydf.getValue().before(immgissdf)) {
					dfempimmgexpirydf.setComponentError(new UserError(
							GERPErrorCodes.NULL_EMPLOYEE_IMMIGRATION_IMMGEXPDATE));
				} else {
					dfempimmgexpirydf.setComponentError(null);
				}
			}
		});
		tfempimmgissuedby = new GERPTextField("Issued By");
		tfempimmgissuedby.setWidth("150");
		tfempimmgdocremark = new GERPTextField("DOC Remark");
		tfempimmgdocremark.setWidth("150");
		btnaddempimmg.setStyleName("add");
		btnaddempimmg.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for employee immgrtn
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validationImmigration();
					saveempimmgrtn();
					empimmgrtnResetfields();
					new GERPSaveNotification();
				}
				catch (Exception e) {
				}
				// try {
				// throw new ERPException.SaveException();
				// }
				// catch (SaveException e1) {
				logger.error("Company ID : " + UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
						+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
						+ " > " + "Exception ");
			}
		});
		// ClickListener for employee Immigration Tale
		tblempimmg.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblempimmg.isSelected(event.getItemId())) {
					tblempimmg.setImmediate(true);
					btnaddempimmg.setCaption("Add");
					btnaddempimmg.setStyleName("savebt");
					empimmgrtnResetfields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddempimmg.setCaption("Update");
					btnaddempimmg.setStyleName("savebt");
					editempimmgrtn();
				}
			}
		});
		// Employee bank Definition
		tfempbankname = new GERPTextField("Bank Name");
		tfempbankname.setWidth("200");
		tfempbankname.setRequired(true);
		tfempacctno = new GERPTextField("Account No");
		tfempacctno.setWidth("200");
		tfempacctno.setRequired(true);
		cbempaccttype = new GERPComboBox("Account Type");
		cbempaccttype.setItemCaptionPropertyId("lookupname");
		cbempaccttype.setWidth("200");
		cbempaccttype.setRequired(true);
		loadaccttype();
		tfemproutingcode = new GERPTextField("Routing Code");
		tfemproutingcode.setWidth("200");
		tfemproutingcode.setRequired(true);
		tfempbranchname = new GERPTextField("Branch Name");
		tfempbranchname.setWidth("200");
		tfempbranchname.setRequired(true);
		btnaddempbank.setStyleName("add");
		btnaddempbank.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for employee bank
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validateionbank();
					saveempbank();
					empbankResetfields();
					new GERPSaveNotification();
				}
				catch (Exception e) {
				}
				logger.error("Company ID : " + UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
						+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
						+ " > " + "Exception ");
			}
		});
		// ClickListener for Employee Bank Tbl
		tblempbank.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblempbank.isSelected(event.getItemId())) {
					tblempbank.setImmediate(true);
					btnaddempbank.setCaption("Add");
					btnaddempbank.setStyleName("savebt");
					empbankResetfields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddempbank.setCaption("Update");
					btnaddempbank.setStyleName("savebt");
					editempbank();
				}
			}
		});
		// Employee Education Definition
		cbempeduqualtnid = new GERPComboBox("Qualification");
		cbempeduqualtnid.setItemCaptionPropertyId("qualName");
		cbempeduqualtnid.setWidth("200");
		cbempeduqualtnid.setRequired(true);
		loadempeduqualification();
		tfempedusubject = new GERPTextField("Subject");
		tfempedusubject.setWidth("200");
		dfempedudurtnfrm = new PopupDateField();
		dfempedudurtnfrm.setInputPrompt("From");
		dfempedudurtnfrm.setWidth("80");
		dfempedudurtnfrm.addValueChangeListener(new ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (dfempedudurtnfrm.getValue() != null) {
					if (dfempedudurtnfrm.getValue().after(new Date()) || dfempedudurtnfrm.getValue().equals(new Date())) {
						dfempedudurtnfrm.setComponentError(new UserError(
								GERPErrorCodes.NULL_EMPLOYEE_Education_FROMDATE));
					} else {
						dtfrmdate = dfempedudurtnfrm.getValue();
						dfempedudurtnfrm.setComponentError(null);
					}
				}
			}
		});
		dfempedudurtnto = new PopupDateField();
		dfempedudurtnto.setInputPrompt("To");
		dfempedudurtnto.setWidth("80");
		dfempedudurtnto.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				if (dfempedudurtnto.getValue().before(dtfrmdate)) {
					dfempedudurtnto.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_Education_TODATE));
				} else {
					dfempedudurtnto.setComponentError(null);
				}
			}
		});
		tfempeduinstitnname = new GERPTextField("Institution Name");
		tfempeduinstitnname.setWidth("200");
		tfempeduunivname = new GERPTextField("University Name");
		tfempeduunivname.setWidth("200");
		tfempeduscoredmark = new GERPTextField("Scored Mark");
		tfempeduscoredmark.setWidth("200");
		/*
		 * tfempeduscoredmark.addValueChangeListener(new ValueChangeListener() {
		 * @Override public void valueChange(ValueChangeEvent event) { if (tfempeduscoredmark.getValue() != null) { emp
		 * = Long.valueOf(tfempeduscoredmark.getValue ()); //emp1 = Long.valueOf(tfempedutotalmark.getValue()); if
		 * (emp>=emp1){ tfempeduscoredmark.setComponentError(new
		 * UserError(GERPErrorCodes.NULL_EMPLOYEE_Education_SCOREDMARK)); }else {
		 * tfempeduscoredmark.setComponentError(null); } } } });
		 */
		// tfempeduscoredmark.setRequired(true);
		tfempedutotalmark = new GERPTextField("Total Mark");
		tfempedutotalmark.setWidth("200");
		/*
		 * tfempedutotalmark.addValueChangeListener(new ValueChangeListener() {
		 * @Override public void valueChange(ValueChangeEvent event) { if (tfempedutotalmark.getValue() != null) { //emp
		 * = Long.valueOf(tfempeduscoredmark.getValue ()); emp1 = Long.valueOf(tfempedutotalmark.getValue()); if
		 * (emp1>=emp){ tfempedutotalmark.setComponentError(new
		 * UserError(GERPErrorCodes.NULL_EMPLOYEE_Education_TOTALMARK)); }else {
		 * tfempedutotalmark.setComponentError(null); } } } });
		 */
		cbempedugradeached = new GERPComboBox("Grade");
		cbempedugradeached.setItemCaptionPropertyId("lookupname");
		cbempedugradeached.setWidth("200");
		cbempedugradeached.setRequired(true);
		loadempeducation();
		btnaddempedu.setStyleName("add");
		btnaddempedu.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee Education
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validationeducation();
					saveempeducation();
					empeduResetfields();
					new GERPSaveNotification();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				logger.error("Company ID : " + UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
						+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
						+ " > " + "Exception ");
			}
		});
		// ClickListener for Employee Education Tbl
		tblempedu.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblempedu.isSelected(event.getItemId())) {
					tblempedu.setImmediate(true);
					btnaddempedu.setCaption("Add");
					btnaddempedu.setStyleName("savebt");
					empeduResetfields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddempedu.setCaption("Update");
					btnaddempedu.setStyleName("savebt");
					editempeducation();
				}
			}
		});
		// Employee Dependant Definition
		tfempdepndepname = new GERPTextField("Dependant Name");
		tfempdepndepname.setWidth("150");
		tfempdepndepname.setRequired(true);
		cbempdepnreltionship = new GERPComboBox("Relationship");
		cbempdepnreltionship.setItemCaptionPropertyId("lookupname");
		cbempdepnreltionship.setWidth("100");
		cbempdepnreltionship.setRequired(true);
		loadempdepnRetnship();
		dfempdepndob = new GERPPopupDateField("DOB");
		dfempdepndob.setWidth("100");
		cbempdepngender = new GERPComboBox("Gender");
		cbempdepngender.setItemCaptionPropertyId("lookupname");
		cbempdepngender.setWidth("100");
		// cbempdepngender.setRequired(true);
		loadGenderType();
		btnaddempdepn.setStyleName("add");
		btnaddempdepn.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee Dependant
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validationdependant();
					saveempdepnndnt();
					empdepnResetfields();
					new GERPSaveNotification();
				}
				catch (Exception e) {
				}
				logger.error("Company ID : " + UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
						+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
						+ " > " + "Exception ");
			}
		});
		// ClickListener for Employee Dependant Tbl
		tblempdepn.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblempdepn.isSelected(event.getItemId())) {
					tblempdepn.setImmediate(true);
					btnaddempdepn.setCaption("Add");
					btnaddempdepn.setStyleName("savebt");
					empdepnResetfields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddempdepn.setCaption("Update");
					btnaddempdepn.setStyleName("savebt");
					editempdepndent();
				}
			}
		});
		// Employee identities Definition
		cbempidenname = new GERPComboBox("ID Name");
		cbempidenname.setItemCaptionPropertyId("lookupname");
		cbempidenname.setWidth("150");
		cbempidenname.setRequired(true);
		loadempidentities();
		tfempidenref = new GERPTextField("Refference ID");
		tfempidenref.setWidth("150");
		tfempidenref.setRequired(true);
		dfempidendate = new GERPPopupDateField("Expiry Date");
		dfempidendate.setWidth("100");
		tfempidenissuedby = new GERPTextField("Issued By");
		tfempidenissuedby.setWidth("100");
		btnaddempiden.setStyleName("add");
		btnaddempiden.addClickListener(new ClickListener() {
			// Click Listener for Add and Update for Employee identities
			private static final long serialVersionUID = 6551953728534136363L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					validationiIdenti();
					saveempidentities();
					empidentitiesResetfields();
					new GERPSaveNotification();
				}
				catch (Exception e) {
				}
				logger.error("Company ID : " + UI.getCurrent().getSession().getAttribute("loginCompanyId").toString()
						+ " | User Name : " + UI.getCurrent().getSession().getAttribute("loginUserName").toString()
						+ " > " + "Exception ");
			}
		});
		// ClickListener for Employee Identities Tbl
		tblempiden.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblempiden.isSelected(event.getItemId())) {
					tblempiden.setImmediate(true);
					btnaddempiden.setCaption("Add");
					btnaddempiden.setStyleName("savebt");
					empidentitiesResetfields();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnaddempiden.setCaption("Update");
					btnaddempiden.setStyleName("savebt");
					editempidentities();
				}
			}
		});
		loadGenderType();
		loadDepartmentList();
		loadCountryList();
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		empdtlsResetfields();
		empskillResetfields();
		empaddressResetFields();
		empcontactsResetfields();
		empimmgrtnResetfields();
		empbankResetfields();
		empeduResetfields();
		empdepnResetfields();
		empidentitiesResetfields();
		loadSrchRslt();
		loadsrchEmpdtls(false);
		loadsrchEmpSkill(false);
		loadSrchEmpAddress(false);
		loadsrchempcont(false);
		loadsrchempimmgrtn(false);
		loadsrchempbank(false);
		loadsrcheudcation(false);
		loadsrcdepnndnts(false);
		loadsrcidentities(false);
	}
	
	/*
	 * loadSrchRslt()-->this function is used for load the search result to table
	 */
	private void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.setSelectable(true);
		tblMstScrSrchRslt.setPageLength(14);
		tblMstScrSrchRslt.removeAllItems();
		List<EmployeeDM> employeeList = new ArrayList<EmployeeDM>();
		employeeList = new ArrayList<EmployeeDM>();
		String employeeCode = tfEmployeeCode.getValue().toString();
		String firstName = tfFirstName.getValue().toString();
		Long deptid = null;
		if (cbDepartment.getValue() != null) {
			deptid = ((Long) cbDepartment.getValue());
		}
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
				+ firstName + "," + employeeCode + "," + deptid + "," + cbEmpStatus.getValue() + "," + companyid);
		employeeList = servicebeanEmployee.getEmployeeList(firstName, employeeCode, deptid,
				(String) cbEmpStatus.getValue(), companyid, null, null, null, null, "F");
		recordCnt = employeeList.size();
		beanEmployee = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
		beanEmployee.addAll(employeeList);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the Employee result set");
		tblMstScrSrchRslt.setContainerDataSource(beanEmployee);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "employeeid", "employeecode", "firstlastname", "branchName",
				"deptname", "empstatus", "lastupdateddt", "lastupdatedby" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Employee Code", "Employee", "Branch",
				"Department", "Status", "Updated Date", "Updated By" });
		tblMstScrSrchRslt.setColumnAlignment("employeeid", Align.RIGHT);
		tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
	}
	
	/*
	 * loadSrchEmpAddress()-->this function is used for load the search Employee Address to table
	 */
	private void loadSrchEmpAddress(boolean fromdb) {
		logger.info("Employee Address Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading employee Address Search...");
		tblempaddress.setPageLength(13);
		tblempaddress.removeAllItems();
		tblempaddress.setWidth("100%");
		logger.info("" + "employee Address : Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Search Parameters are " + companyid + "," + employeeid);
		if (fromdb) {
			empaddresslist = new ArrayList<EmployeeAddressDM>();
			empaddresslist.addAll(servempaddr.getempaddresslist(null, employeeid, null, "Active", "F"));
		}
		recordCntempadd = empaddresslist.size();
		beanEmployeeAddress = new BeanItemContainer<EmployeeAddressDM>(EmployeeAddressDM.class);
		beanEmployeeAddress.addAll(empaddresslist);
		tblempaddress.setSelectable(true);
		tblempaddress.setContainerDataSource(beanEmployeeAddress);
		tblempaddress.setVisibleColumns(new Object[] { "addresstype", "addressln1", "addressln2", "cityname",
				"addressstatus", "lastupdateddt", "lastupdatedby" });
		tblempaddress.setColumnHeaders(new String[] { "Address Type", "Address Line1", "Address Line2", "City",
				"Status", "Last Updated Date", "Last Updated by" });
		tblempaddress.setColumnAlignment("empaddressid", Align.RIGHT);
		tblempaddress.setColumnFooter("lastupdatedby", "No.of Records : " + recordCntempadd);
	}
	
	/*
	 * loadsrchEmpSkill()-->this function is used for load the search Employee Skill to table
	 */
	private void loadsrchEmpSkill(boolean fromdb) {
		logger.info("Employee Skill Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Employee Skill Search...");
		tblempskilltbl.removeAllItems();
		tblempskilltbl.setPageLength(15);
		tblempskilltbl.setWidth("100%");
		// employeeSkillList = new ArrayList<EmployeeSkillDM>();
		if (fromdb) {
			employeeSkillList = new ArrayList<EmployeeSkillDM>();
			employeeSkillList.addAll(serviceemployeeskill.gethcmemployeeskilllist(null, employeeid, null, null, null,
					"Pending", "F"));
			System.out.println("employeeid-- this---->" + employeeid);
		}
		recordcntempskill = employeeSkillList.size();
		beanEmployeeskill = new BeanItemContainer<EmployeeSkillDM>(EmployeeSkillDM.class);
		beanEmployeeskill.addAll(employeeSkillList);
		tblempskilltbl.setSelectable(true);
		tblempskilltbl.setContainerDataSource(beanEmployeeskill);
		tblempskilltbl.setVisibleColumns(new Object[] { "skillname", "skilllevel", "isprimary", "skillstatus",
				"lastupdateddt", "lastupdatedby" });
		tblempskilltbl.setColumnHeaders(new String[] { "Skill", "Skill Level", "Primary Skill", "Status",
				"Last Updated Date", "Last Updated By" });
		tblempskilltbl.setColumnAlignment("empskillId", Align.RIGHT);
		tblempskilltbl.setColumnFooter("lastupdatedby", "No.of Records : " + recordcntempskill);
	}
	
	/*
	 * loadsrchEmpdtls()-->this function is used for load the search Employee Details to table
	 */
	private void loadsrchEmpdtls(Boolean fromdb) {
		logger.info("Employee Details Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Employee Details Search...");
		tblempdtls.setPageLength(9);
		tblempdtls.removeAllItems();
		tblempdtls.setWidth("100%");
		if (fromdb) {
			employeedtlsList = new ArrayList<EmployeeDtlsDM>();
			employeedtlsList.addAll(servempdtls.getEmployeeDtls(null, employeeid, null, null, null, null, "F", null));
		}
		recordcntempdtls = employeedtlsList.size();
		beanEmployeedtls = new BeanItemContainer<EmployeeDtlsDM>(EmployeeDtlsDM.class);
		beanEmployeedtls.addAll(employeedtlsList);
		tblempdtls.setSelectable(true);
		tblempdtls.setContainerDataSource(beanEmployeedtls);
		tblempdtls.setVisibleColumns(new Object[] { "candidatename", "birthplace", "maritalstatus", "statusreason",
				"lastupdateddate", "lastupdatedby" });
		tblempdtls.setColumnHeaders(new String[] { "Candidate", "Birth Place", "Marital Status", "Reason",
				"Last Updated Date", "Last Updated By" });
		tblempdtls.setColumnAlignment("employeedtsid", Align.RIGHT);
		tblempdtls.setColumnFooter("lastupdatedby", "No.of Records : " + recordcntempdtls);
	}
	
	/*
	 * loadsrchempcont()-->this function is used for load the search Employee Contact to table
	 */
	private void loadsrchempcont(Boolean fromdb) {
		logger.info("Employee Contact Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Employee Contact Search...");
		tblempcont.setPageLength(14);
		tblempcont.removeAllItems();
		tblempcont.setWidth("100%");
		if (fromdb) {
			employeecontList = new ArrayList<EmployeeContactsDM>();
			employeecontList.addAll(servempcontact.getEmployeeContactsDetails(null, employeeid, null, "Active", "F"));
		}
		recordcntempcont = employeecontList.size();
		beanEmployeecontacts = new BeanItemContainer<EmployeeContactsDM>(EmployeeContactsDM.class);
		beanEmployeecontacts.addAll(employeecontList);
		tblempcont.setSelectable(true);
		tblempcont.setContainerDataSource(beanEmployeecontacts);
		tblempcont.setVisibleColumns(new Object[] { "contactname", "relationship", "phoneno", "mobileno",
				"contactstatus", "lastupdateddate", "lastupdatedby" });
		tblempcont.setColumnHeaders(new String[] { "Contact Name", "Relationship", "Phone No", "Mobile No", "Status",
				"Last Updated Date", "Last Updated By" });
		tblempcont.setColumnAlignment("employeecontid", Align.RIGHT);
		tblempcont.setColumnFooter("lastupdatedby", "No.of Records : " + recordcntempcont);
	}
	
	/*
	 * loadsrchempimmgrtn()-->this function is used for load the search Employee Immigration to table
	 */
	private void loadsrchempimmgrtn(Boolean fromdb) {
		logger.info("Employee Immigration Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Employee Immigration Search...");
		tblempimmg.setPageLength(13);
		tblempimmg.removeAllItems();
		tblempimmg.setWidth("100%");
		if (fromdb) {
			employeeimmglist = new ArrayList<EmployeeImmgrtnDM>();
			employeeimmglist.addAll(servempimmgrtn.getEmployeeImmgrtnDetail(null, employeeid, null, "Active", "F"));
		}
		recordcntempimmt = employeeimmglist.size();
		beanemployeeimmgrtn = new BeanItemContainer<EmployeeImmgrtnDM>(EmployeeImmgrtnDM.class);
		beanemployeeimmgrtn.addAll(employeeimmglist);
		tblempimmg.setSelectable(true);
		tblempimmg.setContainerDataSource(beanemployeeimmgrtn);
		tblempimmg.setVisibleColumns(new Object[] { "doctype", "docno", "issuedate", "expirydate", "immgrtnstatus",
				"lastupdateddate", "lastupdatedby" });
		tblempimmg.setColumnHeaders(new String[] { "Document Type", "Document No", "Issue Date", "Expiry date",
				"status", "Last Updated Date", "Last Updated By" });
		tblempimmg.setColumnAlignment("employeeimmgrtnid", Align.RIGHT);
		tblempimmg.setColumnFooter("lastupdatedby", "No.of Records : " + recordcntempimmt);
	}
	
	/*
	 * loadsrchempbank()-->this function is used for load the search Employee Bank to table
	 */
	private void loadsrchempbank(Boolean fromdb) {
		logger.info("Employee Bank Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Employee Bank Search...");
		try {
			logger.info("loadsrchempbank-->" + tblempbank);
			tblempbank.setPageLength(14);
			tblempbank.removeAllItems();
			tblempbank.setWidth("100%");
			if (fromdb) {
				employeebanklist = new ArrayList<EmployeeBankDM>();
				employeebanklist.addAll(serviceempbnk.geempbankList(null, employeeid, null, null, "Active", "F"));
			}
			recordcntempbank = employeebanklist.size();
			beanemployeebank = new BeanItemContainer<EmployeeBankDM>(EmployeeBankDM.class);
			beanemployeebank.addAll(employeebanklist);
			tblempbank.setSelectable(true);
			tblempbank.setContainerDataSource(beanemployeebank);
			tblempbank.setVisibleColumns(new Object[] { "bankname", "accntno", "accnttype", "bankstatus", "lastupdtdt",
					"lastupdtby" });
			tblempbank.setColumnHeaders(new String[] { "Bank Name", "Account No", "Account Type", "Status",
					"Last Updated Date", "Last Updated By" });
			tblempbank.setColumnAlignment("empbankid", Align.RIGHT);
			tblempbank.setColumnFooter("lastupdtby", "No.of Records :" + recordcntempbank);
			logger.info("loadsrchempbank2-->" + tblempbank);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * loadsrcheudcation()-->this function is used for load the search Employee Education to table
	 */
	private void loadsrcheudcation(Boolean fromdb) {
		logger.info("Employee Education Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Employee Education Search...");
		tblempedu.setPageLength(13);
		tblempedu.removeAllItems();
		tblempedu.setWidth("100%");
		if (fromdb) {
			employeeedulist = new ArrayList<EmployeeEducationDM>();
			employeeedulist
					.addAll(serviceempedu.getempeducationList(null, employeeid, null, null, null, "Active", "F"));
		}
		recordcntempeducation = employeeedulist.size();
		beanemployeeeducation = new BeanItemContainer<EmployeeEducationDM>(EmployeeEducationDM.class);
		beanemployeeeducation.addAll(employeeedulist);
		tblempedu.setSelectable(true);
		tblempedu.setContainerDataSource(beanemployeeeducation);
		tblempedu.setVisibleColumns(new Object[] { "qualname", "subject", "durfrm", "durto", "insname", "gradeachcd",
				"empednstatus", "lastupdatddt", "lastupdatdby" });
		tblempedu.setColumnHeaders(new String[] { "Qualification", "Subject", "Duration From", "Duration To",
				"Institution Name", "Grade", "Status", "Last Updated Date", "Last Updated By" });
		tblempedu.setColumnAlignment("empeduid", Align.RIGHT);
		tblempedu.setColumnFooter("lastupdatdby", "No.of Records :" + recordcntempeducation);
	}
	
	/*
	 * loadsrcdepnndnts()-->this function is used for load the search Employee Dependant to table
	 */
	private void loadsrcdepnndnts(Boolean fromdb) {
		logger.info("Employee Dependant Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Employee Dependant Search...");
		tblempdepn.setPageLength(15);
		tblempdepn.removeAllItems();
		tblempedu.setWidth("100%");
		if (fromdb) {
			employeedepnlist = new ArrayList<EmployeeDependntsDM>();
			employeedepnlist.addAll(serviceempdeps.getEmployeeDependntsDetails(null, employeeid, null, "Active", "F"));
		}
		recordcntempdependn = employeedepnlist.size();
		logger.info("loadsrcdepnndnts---->" + employeedepnlist);
		beanemployeedependent = new BeanItemContainer<EmployeeDependntsDM>(EmployeeDependntsDM.class);
		beanemployeedependent.addAll(employeedepnlist);
		tblempdepn.setSelectable(true);
		tblempdepn.setContainerDataSource(beanemployeedependent);
		tblempdepn.setVisibleColumns(new Object[] { "dependntname", "relationship", "dob", "gender", "dependntstatus",
				"lastupdateddate", "lastupdatedby" });
		tblempdepn.setColumnHeaders(new String[] { "Dependnt Name", "Relationship", "DOB", "Gender", "Status",
				"Last Updated Date", "Last Updated By" });
		tblempdepn.setColumnAlignment("employeedepndntid", Align.RIGHT);
		tblempdepn.setColumnFooter("lastupdatedby", "No.of Records :" + recordcntempdependn);
		logger.info("loadsrcdepnndnts1---->" + employeedepnlist);
	}
	
	/*
	 * loadsrcidentities()-->this function is used for load the search Employee Identities to table
	 */
	private void loadsrcidentities(Boolean fromdb) {
		logger.info("Employee Identities Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Employee Identities Search...");
		tblempiden.setPageLength(15);
		tblempiden.removeAllItems();
		tblempiden.setWidth("100%");
		if (fromdb) {
			employeeidentitieslist = new ArrayList<EmployeeIdentitiesDM>();
			employeeidentitieslist.addAll(serviceempides.getempittslist(null, employeeid, null, "Active", "F"));
		}
		recordcntempidentities = employeeidentitieslist.size();
		beanemployeeidentities = new BeanItemContainer<EmployeeIdentitiesDM>(EmployeeIdentitiesDM.class);
		beanemployeeidentities.addAll(employeeidentitieslist);
		tblempiden.setSelectable(true);
		tblempiden.setContainerDataSource(beanemployeeidentities);
		tblempiden.setVisibleColumns(new Object[] { "idntname", "idntref", "expdt", "issuesby", "empidnstatus",
				"lastupdtdt", "lastupdtby" });
		tblempiden.setColumnHeaders(new String[] { "ID Name", "Reference ID", "Expiry Date", "Issued BY", "Status",
				"Last Updated Date", "Last Updated By" });
		logger.info("loadsrcidentities--->" + employeeid);
		tblempiden.setColumnAlignment("empidntd", Align.RIGHT);
		tblempiden.setColumnFooter("lastupdtby", "No.of Records :" + recordcntempidentities);
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		tfEmployeeCode.setRequired(false);
		// tfEmployeeCode.setReadOnly(true);
		cbDepartment.setWidth("150");
		// Add components for Search Layout
		FormLayout flEmployeeCode = new FormLayout();
		FormLayout flFirstName = new FormLayout();
		FormLayout flDepartment = new FormLayout();
		FormLayout flStatus = new FormLayout();
		flEmployeeCode.addComponent(tfEmployeeCode);
		flFirstName.addComponent(tfFirstName);
		flDepartment.addComponent(cbDepartment);
		flStatus.addComponent(cbEmpStatus);
		hlSearchLayout.addComponent(flEmployeeCode);
		hlSearchLayout.addComponent(flFirstName);
		hlSearchLayout.addComponent(flDepartment);
		hlSearchLayout.addComponent(flStatus);
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
	}
	
	protected void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlUserInputLayout.removeAllComponents();
		tfFirstName.setRequired(true);
		cbDepartment.setRequired(true);
		cbDepartment.setWidth("150");
		// Add ISD Code and Phone Number in horizontal layout
		hlISDCodePhoneLayout = new HorizontalLayout();
		hlISDCodePhoneLayout.addComponent(tfISDCode);
		hlISDCodePhoneLayout.addComponent(tfPhonenumber);
		hlISDCodePhoneLayout.setCaption("Phone Number");
		// Add components for User Input Layout
		FormLayout fl1 = new FormLayout();
		FormLayout fl2 = new FormLayout();
		FormLayout fl3 = new FormLayout();
		FormLayout fl4 = new FormLayout();
		fl1.addComponent(tfEmployeeCode);
		fl1.addComponent(tfFirstName);
		fl1.addComponent(tfLastName);
		fl1.addComponent(cbGender);
		fl1.setSpacing(true);
		fl2.addComponent(dfDateofBirth);
		fl2.addComponent(dfDateofJoin);
		fl2.addComponent(tfEmailid);
		fl2.addComponent(cbCountry);
		fl2.setSpacing(true);
		fl3.addComponent(cbBranch);
		fl3.addComponent(hlISDCodePhoneLayout);
		fl3.setComponentAlignment(hlISDCodePhoneLayout, Alignment.BOTTOM_RIGHT);
		fl3.addComponent(cbDepartment);
		fl3.addComponent(cbManager);
		fl3.setSpacing(true);
		fl4.addComponent(cbEmpStatus);
		fl4.addComponent(cbCreateUser);
		fl4.addComponent(hlimage);
		fl4.setSpacing(true);
		// add the form layouts into user input layout
		HorizontalLayout hlemployee = new HorizontalLayout();
		hlemployee.setSpacing(true);
		hlemployee.addComponent(fl1);
		hlemployee.addComponent(fl2);
		hlemployee.addComponent(fl3);
		hlemployee.addComponent(fl4);
		hlemployee.setMargin(true);
		hlemployee.setSpacing(true);
		hlemployee.setWidth("100%");
		HorizontalLayout hlemptable = new HorizontalLayout();
		hlemptable.addComponent(tblMstScrSrchRslt);
		VerticalLayout vlemployee = new VerticalLayout();
		vlemployee.addComponent(hlemployee);
		vlemployee.addComponent(hlemptable);
		vlemployee.setSizeFull();
		// Add employee Address for User Input Layout
		FormLayout flempaddress1 = new FormLayout();
		FormLayout flempaddress2 = new FormLayout();
		FormLayout flempaddress3 = new FormLayout();
		FormLayout flempaddress4 = new FormLayout();
		HorizontalLayout hlempaddresstabl = new HorizontalLayout();
		hlempaddresstabl.addComponent(tblempaddress);
		hlempaddresstabl.setWidth("100%");
		flempaddress1.addComponent(cbempadrstype);
		flempaddress1.setSpacing(true);
		flempaddress1.addComponent(tfEmpaddressln1);
		flempaddress1.setSpacing(true);
		flempaddress1.addComponent(tfEmpaddressln2);
		flempaddress1.setSpacing(true);
		flempaddress2.addComponent(tfempaddresscityname);
		flempaddress2.setSpacing(true);
		flempaddress2.addComponent(tfempaddressstatename);
		flempaddress2.setSpacing(true);
		flempaddress2.addComponent(cbempadrscountryid);
		flempaddress2.setSpacing(true);
		flempaddress3.addComponent(tfempadrspostcode);
		flempaddress3.setSpacing(true);
		flempaddress3.addComponent(cbEmpaddressstatus);
		flempaddress3.setSpacing(true);
		// Setting for employee Address tab component to layout
		HorizontalLayout hlempaddress = new HorizontalLayout();
		hlempaddress.addComponent(flempaddress1);
		hlempaddress.addComponent(flempaddress2);
		hlempaddress.addComponent(flempaddress3);
		hlempaddress.addComponent(flempaddress4);
		hlempaddress.addComponent(btnaddempadd);
		hlempaddress.setComponentAlignment(btnaddempadd, Alignment.BOTTOM_RIGHT);
		hlempaddress.setSpacing(true);
		hlempaddress.setMargin(true);
		hlempaddress.setSizeUndefined();
		// To add employee and table in employee address tab
		VerticalLayout vlempaddresstab = new VerticalLayout();
		vlempaddresstab.addComponent(hlempaddress);
		vlempaddresstab.addComponent(hlempaddresstabl);
		vlempaddresstab.setWidth("100%");
		// Add Employee Skill for user Input Layout
		FormLayout flemployeeskill1 = new FormLayout();
		FormLayout flemployeeskill2 = new FormLayout();
		FormLayout flemployeeskill3 = new FormLayout();
		FormLayout flemployeeskill4 = new FormLayout();
		HorizontalLayout hlempskillTable = new HorizontalLayout();
		hlempskillTable.addComponent(tblempskilltbl);
		hlempskillTable.setWidth("100%");
		flemployeeskill1.addComponent(cbempskillid);
		flemployeeskill1.setSpacing(true);
		flemployeeskill2.addComponent(cbempskilllevel);
		flemployeeskill2.setSpacing(true);
		flemployeeskill3.addComponent(tfempskillisprimary);
		flemployeeskill3.setSpacing(true);
		// setting for employee skill tab component to layout
		HorizontalLayout hlempskillcomponent = new HorizontalLayout();
		hlempskillcomponent.addComponent(flemployeeskill1);
		hlempskillcomponent.addComponent(flemployeeskill2);
		hlempskillcomponent.addComponent(flemployeeskill3);
		hlempskillcomponent.addComponent(btnaddempskill);
		hlempskillcomponent.addComponent(flemployeeskill4);
		hlempskillcomponent.setComponentAlignment(btnaddempskill, Alignment.BOTTOM_RIGHT);
		hlempskillcomponent.setSpacing(true);
		hlempskillcomponent.setMargin(true);
		hlempskillcomponent.setSizeUndefined();
		// To add component and table in employee skill tab
		VerticalLayout vlemployeeskilltab = new VerticalLayout();
		vlemployeeskilltab.addComponent(hlempskillcomponent);
		vlemployeeskilltab.addComponent(hlempskillTable);
		vlemployeeskilltab.setWidth("100%");
		// Add Employee Dtls for user Input Layout
		FormLayout flemployeedtls1 = new FormLayout();
		FormLayout flemployeedtls2 = new FormLayout();
		FormLayout flemployeedtls3 = new FormLayout();
		HorizontalLayout hlempdtlstable = new HorizontalLayout();
		hlempdtlstable.addComponent(tblempdtls);
		hlempdtlstable.setWidth("100%");
		hlempdtlstable.setSizeFull();
		flemployeedtls1.addComponent(cbempdtlscandid);
		flemployeedtls1.setSpacing(true);
		flemployeedtls1.addComponent(cbempdtlsemptypeid);
		flemployeedtls1.setSpacing(true);
		flemployeedtls1.addComponent(cbempdtlsgradid);
		flemployeedtls1.setSpacing(true);
		flemployeedtls1.addComponent(cbempdtlsdesignaton);
		flemployeedtls1.setSpacing(true);
		flemployeedtls1.addComponent(cbempdtlspayperid);
		flemployeedtls1.setSpacing(true);
		flemployeedtls1.addComponent(cbempdtlsmaritalstatus);
		flemployeedtls1.setSpacing(true);
		flemployeedtls2.addComponent(tfempdtlsbirthplace);
		flemployeedtls2.setSpacing(true);
		flemployeedtls2.addComponent(cbempdtlsbitrhcountry);
		flemployeedtls2.setSpacing(true);
		flemployeedtls2.addComponent(dfempdtlsconfdate);
		flemployeedtls2.setSpacing(true);
		flemployeedtls2.addComponent(dfempdtlslasttpromotdate);
		flemployeedtls2.setSpacing(true);
		flemployeedtls2.addComponent(dfempdtlsreleaveddate);
		flemployeedtls2.setSpacing(true);
		flemployeedtls2.addComponent(dfempdtlsdeathdtate);
		flemployeedtls2.setSpacing(true);
		flemployeedtls3.addComponent(tfempdtlsPFno);
		flemployeedtls3.setSpacing(true);
		flemployeedtls3.addComponent(tfempdtlsESIno);
		flemployeedtls3.setSpacing(true);
		flemployeedtls3.addComponent(cbempdtlsnationalid);
		flemployeedtls3.setSpacing(true);
		flemployeedtls3.addComponent(tfempdtlsPENSON);
		flemployeedtls3.setSpacing(true);
		flemployeedtls3.addComponent(cbempdtlsstatusid);
		flemployeedtls3.setSpacing(true);
		flemployeedtls3.addComponent(tfempdtlsstatusreasion);
		flemployeedtls3.setSpacing(true);
		// setting for Employee Details tab component to layout
		HorizontalLayout hlempdtlscomponent = new HorizontalLayout();
		Label fllable = new Label();
		hlempdtlscomponent.addComponent(flemployeedtls1);
		hlempdtlscomponent.addComponent(flemployeedtls2);
		hlempdtlscomponent.addComponent(fllable);
		hlempdtlscomponent.addComponent(flemployeedtls3);
		hlempdtlscomponent.addComponent(btnaddempdtls);
		hlempdtlscomponent.setComponentAlignment(btnaddempdtls, Alignment.BOTTOM_RIGHT);
		hlempdtlscomponent.setSpacing(true);
		hlempdtlscomponent.setMargin(true);
		hlempdtlscomponent.setSizeUndefined();
		// To add component and table in Employee Details tab
		VerticalLayout vlempdtlstab = new VerticalLayout();
		vlempdtlstab.addComponent(hlempdtlscomponent);
		vlempdtlstab.addComponent(hlempdtlstable);
		vlempdtlstab.setSizeFull();
		// Add Employee contact for user Input Layout
		FormLayout flemployeecont1 = new FormLayout();
		FormLayout flemployeecont2 = new FormLayout();
		FormLayout flemployeecont3 = new FormLayout();
		HorizontalLayout hlempconttable = new HorizontalLayout();
		hlempconttable.addComponent(tblempcont);
		hlempconttable.setWidth("100%");
		hlempconttable.setSizeFull();
		flemployeecont1.addComponent(tfempcontactname);
		flemployeecont1.setSpacing(true);
		flemployeecont1.addComponent(tfempcontrelationship);
		flemployeecont1.setSpacing(true);
		flemployeecont2.addComponent(tfempcontphno);
		flemployeecont2.setSpacing(true);
		flemployeecont2.addComponent(tfempcontmono);
		flemployeecont2.setSpacing(true);
		flemployeecont3.addComponent(cbempcontstatus);
		flemployeecont3.setSpacing(true);
		// setting for Employee contact tab component to layout
		HorizontalLayout hlemployeecontComponent = new HorizontalLayout();
		hlemployeecontComponent.addComponent(flemployeecont1);
		hlemployeecontComponent.addComponent(flemployeecont2);
		hlemployeecontComponent.addComponent(flemployeecont3);
		hlemployeecontComponent.addComponent(btnaddempcont);
		hlemployeecontComponent.setComponentAlignment(btnaddempcont, Alignment.BOTTOM_RIGHT);
		hlemployeecontComponent.setSpacing(true);
		hlemployeecontComponent.setMargin(true);
		hlemployeecontComponent.setSizeUndefined();
		// To add component and table in Employee Contact tab
		VerticalLayout vlempcontTab = new VerticalLayout();
		vlempcontTab.addComponent(hlemployeecontComponent);
		vlempcontTab.addComponent(hlempconttable);
		vlempcontTab.setSizeFull();
		// Add Employee Immigration for user Input Layout
		FormLayout flemployeeimmg1 = new FormLayout();
		FormLayout flemployeeimmg2 = new FormLayout();
		FormLayout flemployeeimmg3 = new FormLayout();
		FormLayout flemployeeimmg4 = new FormLayout();
		HorizontalLayout hlempimmgtbl = new HorizontalLayout();
		hlempimmgtbl.addComponent(tblempimmg);
		hlempimmgtbl.setSizeFull();
		flemployeeimmg1.addComponent(cbempimmgdoctype);
		flemployeeimmg1.setVisible(true);
		flemployeeimmg1.addComponent(tfempimmgdocno);
		flemployeeimmg1.setVisible(true);
		flemployeeimmg2.addComponent(dfempimmgissuedf);
		flemployeeimmg2.setVisible(true);
		flemployeeimmg2.addComponent(dfempimmgexpirydf);
		flemployeeimmg2.setVisible(true);
		flemployeeimmg3.addComponent(tfempimmgissuedby);
		flemployeeimmg3.setVisible(true);
		flemployeeimmg3.addComponent(tfempimmgdocremark);
		flemployeeimmg3.setVisible(true);
		flemployeeimmg4.addComponent(cbempimmgstatus);
		flemployeeimmg4.setVisible(true);
		// setting for Employee Immigration tab component to layout
		HorizontalLayout hlempimmgcomponent = new HorizontalLayout();
		hlempimmgcomponent.addComponent(flemployeeimmg1);
		hlempimmgcomponent.addComponent(flemployeeimmg2);
		hlempimmgcomponent.addComponent(fllable);
		hlempimmgcomponent.addComponent(flemployeeimmg3);
		hlempimmgcomponent.addComponent(flemployeeimmg4);
		hlempimmgcomponent.addComponent(btnaddempimmg);
		hlempimmgcomponent.setComponentAlignment(btnaddempimmg, Alignment.BOTTOM_RIGHT);
		hlempimmgcomponent.setSpacing(true);
		hlempimmgcomponent.setMargin(true);
		hlempimmgcomponent.setSizeUndefined();
		// To add component and table in Employee Immigration tab
		VerticalLayout vlempimmgtab = new VerticalLayout();
		vlempimmgtab.addComponent(hlempimmgcomponent);
		vlempimmgtab.addComponent(hlempimmgtbl);
		vlempimmgtab.setSizeFull();
		// Add Employee Bank for user Input Layout
		FormLayout flempbank1 = new FormLayout();
		FormLayout flempbank2 = new FormLayout();
		FormLayout flempbank3 = new FormLayout();
		HorizontalLayout hlempbanktbl = new HorizontalLayout();
		hlempbanktbl.addComponent(tblempbank);
		hlempbanktbl.setSizeFull();
		flempbank1.addComponent(tfempbankname);
		flempbank1.setVisible(true);
		flempbank1.addComponent(tfempacctno);
		flempbank1.setVisible(true);
		flempbank2.addComponent(cbempaccttype);
		flempbank2.setVisible(true);
		flempbank2.addComponent(tfemproutingcode);
		flempbank2.setVisible(true);
		flempbank3.addComponent(tfempbranchname);
		flempbank3.setVisible(true);
		flempbank3.addComponent(cbempbankstatus);
		flempbank3.setVisible(true);
		// setting for Employee Bank tab component to layout
		HorizontalLayout hlempbankcomponent = new HorizontalLayout();
		hlempbankcomponent.addComponent(flempbank1);
		hlempbankcomponent.addComponent(flempbank2);
		hlempbankcomponent.addComponent(flempbank3);
		hlempbankcomponent.addComponent(btnaddempbank);
		hlempbankcomponent.setComponentAlignment(btnaddempbank, Alignment.BOTTOM_RIGHT);
		hlempbankcomponent.setSpacing(true);
		hlempbankcomponent.setMargin(true);
		hlempbankcomponent.setSizeUndefined();
		// To add component and table in Employee Bank tab
		VerticalLayout vlempbanktab = new VerticalLayout();
		vlempbanktab.addComponent(hlempbankcomponent);
		vlempbanktab.addComponent(hlempbanktbl);
		vlempbanktab.setSizeFull();
		// Add Employee Education for user Input Layout
		FormLayout flemployeeedu1 = new FormLayout();
		FormLayout flemployeeedu2 = new FormLayout();
		FormLayout flemployeeedu3 = new FormLayout();
		HorizontalLayout hlempedutable = new HorizontalLayout();
		HorizontalLayout FromTodate = new HorizontalLayout();
		Label frmtolab = new Label();
		FromTodate.addComponent(dfempedudurtnfrm);
		FromTodate.setCaption("Duration");
		FromTodate.setVisible(true);
		FromTodate.addComponent(frmtolab);
		FromTodate.setSpacing(true);
		FromTodate.addComponent(dfempedudurtnto);
		FromTodate.setVisible(true);
		hlempedutable.addComponent(tblempedu);
		hlempedutable.setSizeFull();
		flemployeeedu1.addComponent(cbempeduqualtnid);
		flemployeeedu1.setVisible(true);
		flemployeeedu1.addComponent(tfempedusubject);
		flemployeeedu1.setVisible(true);
		flemployeeedu1.addComponent(FromTodate);
		flemployeeedu1.setComponentAlignment(FromTodate, Alignment.TOP_LEFT);
		flemployeeedu1.setVisible(true);
		flemployeeedu2.addComponent(tfempeduinstitnname);
		flemployeeedu2.setVisible(true);
		flemployeeedu2.addComponent(tfempeduunivname);
		flemployeeedu2.setVisible(true);
		flemployeeedu2.addComponent(tfempeduscoredmark);
		flemployeeedu2.setVisible(true);
		flemployeeedu3.addComponent(tfempedutotalmark);
		flemployeeedu3.setVisible(true);
		flemployeeedu3.addComponent(cbempedugradeached);
		flemployeeedu3.setVisible(true);
		flemployeeedu3.addComponent(cbempedustatus);
		flemployeeedu3.setVisible(true);
		// setting for Employee Education tab component to layout
		HorizontalLayout hlempeducomponent = new HorizontalLayout();
		hlempeducomponent.addComponent(flemployeeedu1);
		hlempeducomponent.addComponent(flemployeeedu2);
		hlempeducomponent.addComponent(flemployeeedu3);
		hlempeducomponent.addComponent(btnaddempedu);
		hlempeducomponent.setComponentAlignment(btnaddempedu, Alignment.BOTTOM_RIGHT);
		hlempeducomponent.setSpacing(true);
		hlempeducomponent.setMargin(true);
		hlempeducomponent.setSizeUndefined();
		// To add component and table in Employee Education tab
		VerticalLayout vlempedutab = new VerticalLayout();
		vlempedutab.addComponent(hlempeducomponent);
		vlempedutab.addComponent(hlempedutable);
		vlempedutab.setSizeFull();
		// Add Employee Dependent for user Input Layout
		FormLayout flemployeedepn1 = new FormLayout();
		FormLayout flemployeedepn2 = new FormLayout();
		FormLayout flemployeedepn3 = new FormLayout();
		FormLayout flemployeedepn4 = new FormLayout();
		FormLayout flemployeedepn5 = new FormLayout();
		HorizontalLayout hlempdepntable = new HorizontalLayout();
		hlempdepntable.addComponent(tblempdepn);
		hlempdepntable.setSizeFull();
		flemployeedepn1.addComponent(tfempdepndepname);
		flemployeedepn1.setVisible(true);
		flemployeedepn2.addComponent(cbempdepnreltionship);
		flemployeedepn2.setVisible(true);
		flemployeedepn3.addComponent(dfempdepndob);
		flemployeedepn3.setVisible(true);
		flemployeedepn4.addComponent(cbempdepngender);
		flemployeedepn4.setVisible(true);
		flemployeedepn5.addComponent(cbempdepnstatus);
		flemployeedepn5.setVisible(true);
		// setting for Employee Dependent tab component to layout
		HorizontalLayout hlempdepncomponent = new HorizontalLayout();
		Label hllab = new Label();
		hlempdepncomponent.addComponent(flemployeedepn1);
		hlempdepncomponent.addComponent(flemployeedepn2);
		hlempdepncomponent.addComponent(flemployeedepn3);
		hlempdepncomponent.addComponent(hllab);
		hlempdepncomponent.addComponent(flemployeedepn4);
		hlempdepncomponent.addComponent(flemployeedepn5);
		hlempdepncomponent.addComponent(btnaddempdepn);
		hlempdepncomponent.setComponentAlignment(btnaddempdepn, Alignment.BOTTOM_RIGHT);
		hlempdepncomponent.setSpacing(true);
		hlempdepncomponent.setMargin(true);
		hlempdepncomponent.setSizeUndefined();
		// To add component and table in employee Dependent tab
		VerticalLayout vlempdepntab = new VerticalLayout();
		vlempdepntab.addComponent(hlempdepncomponent);
		vlempdepntab.addComponent(hlempdepntable);
		vlempdepntab.setSizeFull();
		// Add Employee Identities for user Input Layout
		FormLayout flemployeeiden1 = new FormLayout();
		FormLayout flemployeeiden2 = new FormLayout();
		FormLayout flemployeeiden3 = new FormLayout();
		FormLayout flemployeeiden4 = new FormLayout();
		FormLayout flemployeeiden5 = new FormLayout();
		HorizontalLayout hlempidentable = new HorizontalLayout();
		hlempidentable.addComponent(tblempiden);
		hlempidentable.setSizeFull();
		flemployeeiden1.addComponent(cbempidenname);
		flemployeeiden1.setVisible(true);
		flemployeeiden2.addComponent(tfempidenref);
		flemployeeiden2.setVisible(true);
		flemployeeiden3.addComponent(dfempidendate);
		flemployeeiden3.setVisible(true);
		flemployeeiden4.addComponent(tfempidenissuedby);
		flemployeeiden4.setVisible(true);
		flemployeeiden5.addComponent(cbempidenstatus);
		flemployeeiden5.setVisible(true);
		// setting for Employee Identities tab component to layout
		HorizontalLayout hlempidencomponent = new HorizontalLayout();
		Label hlidenlab = new Label();
		hlempidencomponent.addComponent(flemployeeiden1);
		hlempidencomponent.addComponent(flemployeeiden2);
		hlempidencomponent.addComponent(flemployeeiden3);
		hlempidencomponent.addComponent(hlidenlab);
		hlempidencomponent.addComponent(flemployeeiden4);
		hlempidencomponent.addComponent(flemployeeiden5);
		hlempidencomponent.addComponent(btnaddempiden);
		hlempidencomponent.setComponentAlignment(btnaddempiden, Alignment.BOTTOM_RIGHT);
		hlempidencomponent.setSpacing(true);
		hlempidencomponent.setMargin(true);
		hlempidencomponent.setSizeUndefined();
		// To add component and table in Employee Identities tab
		VerticalLayout vlempidentab = new VerticalLayout();
		vlempidentab.addComponent(hlempidencomponent);
		vlempidentab.addComponent(hlempidentable);
		vlempidentab.setSizeFull();
		// Creating Tab Sheet
		TabSheet tabSheet = new TabSheet();
		tabSheet.setWidth("1200");
		tabSheet.setHeight("100%");
		tabSheet.setSizeFull();
		tabSheet.addTab(vlemployee, "Employee Data ");
		tabSheet.addTab(vlempdtlstab, "Additional Details", null);
		tabSheet.addTab(vlemployeeskilltab, "Skill", null);
		tabSheet.addTab(vlempaddresstab, "Address", null);
		tabSheet.addTab(vlempcontTab, "Contacts", null);
		tabSheet.addTab(vlempimmgtab, "Immigration", null);
		tabSheet.addTab(vlempbanktab, "Bank", null);
		tabSheet.addTab(vlempedutab, "Education", null);
		tabSheet.addTab(vlempdepntab, "Dependant", null);
		tabSheet.addTab(vlempidentab, "Identity", null);
		tabSheet.setSizeFull();
		// Setting for all layout in vertical layout
		VerticalLayout vlAllComponent = new VerticalLayout();
		vlAllComponent.addComponent(tabSheet);
		vlAllComponent.setSpacing(true);
		vlAllComponent.setWidth("100%");
		// HorizontalLayout empname = new HorizontalLayout();
		// add the form layout into user input layout
		hlUserInputLayout.addComponent(vlAllComponent);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(false);
		hlUserInputLayout.setWidth("100%");
		hlUserInputLayout.setHeight("100%");
		vlSrchRsltContainer.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	/*
	 * loadGenderType()-->this function is used for load the gender type
	 */
	public void loadGenderType() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Gender Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"BS_GENDER");
		beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbGender.setContainerDataSource(beanCompanyLookUp);
		cbempdepngender.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadDOCType()-->this function is used for load the DOC type
	 */
	public void loadDOCType() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading DOC Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"HC_IMGDCTP");
		beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbempimmgdoctype.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadaccttype-->this function is used for load the Account type
	 */
	public void loadaccttype() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Account Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"HC_BNKACTP");
		beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbempaccttype.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadskilllavel()-->this function is used for load the skill level
	 */
	public void loadskilllavel() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading skill Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"HC_SKLLVL");
		beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbempskilllevel.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadaddresstype()-->this function is used for load the Address Type
	 */
	public void loadaddresstype() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Address Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"HC_ADRTYPE");
		beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbempadrstype.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadCountryList()-->this function is used for load the Country list
	 */
	public void loadCountrydtlsbirthList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Country Search...");
		List<CountryDM> countrylist = serviceCountry.getCountryList(null, null, null, null, "Active", "F");
		beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
		beanCountry.setBeanIdProperty("countryID");
		beanCountry.addAll(countrylist);
		cbCountry.setContainerDataSource(beanCountry);
		cbempdtlsbitrhcountry.setContainerDataSource(beanCountry);
	}
	
	/*
	 * loadCountryList()-->this function is used for load the Country list
	 */
	public void loadCountrynationalList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Country Search...");
		List<CountryDM> countrylist = serviceCountry.getCountryList(null, null, null, null, "Active", "F");
		beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
		beanCountry.setBeanIdProperty("countryID");
		beanCountry.addAll(countrylist);
		cbCountry.setContainerDataSource(beanCountry);
		cbempdtlsnationalid.setContainerDataSource(beanCountry);
	}
	
	/*
	 * loadCountryList()-->this function is used for load the Country list
	 */
	public void loadCountryList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Country Search...");
		List<CountryDM> countrylist = serviceCountry.getCountryList(null, null, null, null, "Active", "F");
		beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
		beanCountry.setBeanIdProperty("countryID");
		beanCountry.addAll(countrylist);
		cbCountry.setContainerDataSource(beanCountry);
	}
	
	/*
	 * loadCountryList()-->this function is used for load the Country list
	 */
	public void loadadrsCountryList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Country Search...");
		List<CountryDM> countrylist = serviceCountry.getCountryList(null, null, null, null, "Active", "F");
		beanCountry = new BeanContainer<Long, CountryDM>(CountryDM.class);
		beanCountry.setBeanIdProperty("countryID");
		beanCountry.addAll(countrylist);
		cbempadrscountryid.setContainerDataSource(beanCountry);
	}
	
	/*
	 * loadISDCode()-->this function is used for load ISD Code
	 */
	public void loadISDCode() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Load ISD Code..." + isdCode);
		isdCode = serviceCountry.getISDCodebyCountryId((Long) cbCountry.getValue());
		tfISDCode.setReadOnly(false);
		tfISDCode.setValue(isdCode);
		tfISDCode.setReadOnly(true);
	}
	
	/*
	 * loadBranchList()-->this function is used for load the Branch list
	 */
	public void loadBranchList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Branch Search...");
		List<BranchDM> branchlist = servicebeanBranch.getBranchList(null, null, (Long) cbCountry.getValue(), "Active",
				companyid, "P");
		beanBranch = new BeanContainer<Long, BranchDM>(BranchDM.class);
		beanBranch.setBeanIdProperty("branchId");
		beanBranch.addAll(branchlist);
		cbBranch.setContainerDataSource(beanBranch);
	}
	
	/*
	 * loadMaritalList()-->this function is used for load the Marital list
	 */
	public void loadMaritalList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Marital Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"HC_MRTLSTS");
		beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbempdtlsmaritalstatus.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadDepartmentList()-->this function is used for load the Department list
	 */
	public void loadDepartmentList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Department Search...");
		List<DepartmentDM> departmentlist = servicebeandepartmant.getDepartmentList(companyid, null, "Active", "P");
		departmentlist.add(new DepartmentDM(0L, "All Department"));
		beanDepartment = new BeanContainer<Long, DepartmentDM>(DepartmentDM.class);
		beanDepartment.setBeanIdProperty("deptid");
		beanDepartment.addAll(departmentlist);
		cbDepartment.setContainerDataSource(beanDepartment);
	}
	
	/*
	 * loadempskilllsist()-->this function is used for load the skill list
	 */
	public void loadempskilllsist() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Skill Search...");
			logger.info("skill------------>");
			List<SkillsDM> skillslist = serviceSkills.getSkillsList(null, null, null, companyid, "Active", "P");
			BeanItemContainer<SkillsDM> beanskills = new BeanItemContainer<SkillsDM>(SkillsDM.class);
			// beanskills.setBeanIdProperty("skillId");
			beanskills.addAll(skillslist);
			cbempskillid.setContainerDataSource(beanskills);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * loadEmployeeList()-->this function is used for load the employee list
	 */
	private void loadEmployeeList() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading  Employee Search...");
		List<EmployeeDM> employeelist = servicebeanEmployee.getEmployeeList(null, null, null, "Active", companyid,
				null, null, null, null, "P");
		BeanContainer<Long, EmployeeDM> beanLoadEmployee = new BeanContainer<Long, EmployeeDM>(EmployeeDM.class);
		beanLoadEmployee.setBeanIdProperty("employeeid");
		beanLoadEmployee.addAll(employeelist);
		cbManager.setContainerDataSource(beanLoadEmployee);
	}
	
	/*
	 * loadempmenttypelist()-->this function is used for load the employment type list
	 */
	private void loadempmenttypelist() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Employment Search...");
		List<EmploymentTypeDM> empmenttypelist = serviceEmploymentType.getEmpTypeList(null, null, companyid, "Active",
				"F");
		BeanContainer<Long, EmploymentTypeDM> benemptype = new BeanContainer<Long, EmploymentTypeDM>(
				EmploymentTypeDM.class);
		benemptype.setBeanIdProperty("empTypeId");
		benemptype.addAll(empmenttypelist);
		cbempdtlsemptypeid.setContainerDataSource(benemptype);
	}
	
	/*
	 * loadempgradelist()-->this function is used for load the grade list
	 */
	private void loadempgradelist() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Grade Search...");
		List<GradeDM> gradelist = serviceGrade.getGradeList(null, null, null, companyid, "Active", "F");
		BeanContainer<Long, GradeDM> beangrad = new BeanContainer<Long, GradeDM>(GradeDM.class);
		beangrad.setBeanIdProperty("gradeId");
		beangrad.addAll(gradelist);
		cbempdtlsgradid.setContainerDataSource(beangrad);
	}
	
	/*
	 * loadpayperiodlist()-->this function is used for load the payperiod list
	 */
	private void loadpayperiodlist() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Payperiod  Search...");
		List<PayPeriodDM> payperiodlist = servicePayPeriodService.getPayList(null, null, null, null, companyid,
				"Active", "F");
		BeanContainer<Long, PayPeriodDM> beanpayperiod = new BeanContainer<Long, PayPeriodDM>(PayPeriodDM.class);
		beanpayperiod.setBeanIdProperty("payPeriodId");
		beanpayperiod.addAll(payperiodlist);
		cbempdtlspayperid.setContainerDataSource(beanpayperiod);
	}
	
	/*
	 * loadjobcandidatelist()-->this function is used for load the jobcandidate list
	 */
	private void loadjobcandidatelist() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Jobcandidate Search...");
		List<JobCandidateDM> jobcandidatelist = servicejobcandidate.getJobCandidateList(null, null, null, null,
				"Active");
		BeanItemContainer<JobCandidateDM> beanjobcandidate = new BeanItemContainer<JobCandidateDM>(JobCandidateDM.class);
		// beanjobcandidate.setBeanIdProperty("candidateId");
		beanjobcandidate.addAll(jobcandidatelist);
		cbempdtlscandid.setContainerDataSource(beanjobcandidate);
	}
	
	/*
	 * loademploymentstatuslist()-->this function is used for load the Employment status list
	 */
	private void loademploymentstatuslist() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Employment status Search...");
		List<EmploymentStatusDM> employmentlist = serviceEmploymentStatus.getEmploymentStatusList(null, null,
				companyid, "Active");
		BeanContainer<Long, EmploymentStatusDM> beanemployment = new BeanContainer<Long, EmploymentStatusDM>(
				EmploymentStatusDM.class);
		beanemployment.setBeanIdProperty("empstatusid");
		beanemployment.addAll(employmentlist);
		cbempdtlsstatusid.setContainerDataSource(beanemployment);
	}
	
	/*
	 * loaddesignationlist()-->this function is used for load the Designation List
	 */
	private void loaddesignationlist() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Designation Search...");
		List<DesignationDM> designationlist = serviceDesignation.getDesignationList(null,
				(Long) cbempdtlsgradid.getValue(), null, null, companyid, "Active", "F");
		System.out.println("cbempdtlsgradid.getValue()---");
		BeanContainer<Long, DesignationDM> beandesignation = new BeanContainer<Long, DesignationDM>(DesignationDM.class);
		beandesignation.setBeanIdProperty("designationId");
		beandesignation.addAll(designationlist);
		cbempdtlsdesignaton.setContainerDataSource(beandesignation);
	}
	
	/*
	 * loadempeducation()-->this function is used for load the Education Grade
	 */
	private void loadempeducation() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Education Grade Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"HC_EDUGRD");
		beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbempedugradeached.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadempeduqualification()-->this function is used for load the Qualification
	 */
	private void loadempeduqualification() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Qualification Search...");
		logger.info("qualification--->");
		List<QualificationDM> qualificationlist = serviceQualification.getQualificationList(null, null, companyid,
				"Active", "F");
		BeanContainer<Long, QualificationDM> beaneducation = new BeanContainer<Long, QualificationDM>(
				QualificationDM.class);
		beaneducation.setBeanIdProperty("qualId");
		beaneducation.addAll(qualificationlist);
		cbempeduqualtnid.setContainerDataSource(beaneducation);
		logger.info("qualification1--->");
	}
	
	/*
	 * loadempdepnRetnship()-->this function is used for load the Relationship
	 */
	private void loadempdepnRetnship() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Relationship Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"HC_CONTRLN");
		beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbempdepnreltionship.setContainerDataSource(beanCompanyLookUp);
	}
	
	/*
	 * loadempidentities()-->this function is used for load the Identities Name
	 */
	private void loadempidentities() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Loading Identities Name Search...");
		List<CompanyLookupDM> lookUpList = serviceCompanyLookup.getCompanyLookUpByLookUp(companyid, null, "Active",
				"HC_IDNTYPE");
		beanCompanyLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
		beanCompanyLookUp.setBeanIdProperty("lookupname");
		beanCompanyLookUp.addAll(lookUpList);
		cbempidenname.setContainerDataSource(beanCompanyLookUp);
	}
	
	// Based on the selected record, the data would be populated into user input fields in the input form
	private void editEmployee() {
		flag = 1;
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		EmployeeDM editEmployeelist = beanEmployee.getItem(tblMstScrSrchRslt.getValue()).getBean();
		UserDM saveUsr = new UserDM();
		userId = saveUsr.getUserid();
		employeeid = editEmployeelist.getEmployeeid();
		if (editEmployeelist.getEmployeecode() != null || editEmployeelist.getFirstname() != null
				|| editEmployeelist.getLastname() != null || editEmployeelist.getPrimaryphone() != null
				|| editEmployeelist.getPrimaryemail() != null) {
			logger.info("editEmployee : " + "," + tfEmployeeCode.getValue() + "," + tfFirstName.getValue() + ","
					+ tfLastName.getValue() + "," + tfPhonenumber.getValue() + "," + tfEmailid.getValue() + ","
					+ cbEmpStatus.getValue() + "," + cbGender.getValue() + "," + cbDepartment.getValue() + ","
					+ cbBranch.getValue() + "," + cbManager.getValue());
			tfEmployeeCode.setValue((String) sltedRcd.getItemProperty("employeecode").getValue());
			tfEmployeeCode.setReadOnly(true);
			tfFirstName.setValue(sltedRcd.getItemProperty("firstname").getValue().toString());
			tfLastName.setValue(sltedRcd.getItemProperty("lastname").getValue().toString());
			tfPhonenumber.setValue((String) sltedRcd.getItemProperty("primaryphone").getValue());
			tfEmailid.setValue((String) sltedRcd.getItemProperty("primaryemail").getValue());
			String stCode = sltedRcd.getItemProperty("empstatus").getValue().toString();
			cbEmpStatus.setValue(stCode);
			if (sltedRcd.getItemProperty("gender").getValue() != null) {
				String genCode = sltedRcd.getItemProperty("gender").getValue().toString();
				cbGender.setValue(genCode);
			}
			cbDepartment.setValue((Long) editEmployeelist.getDeptid());
			cbManager.setValue((Long) editEmployeelist.getEmployeeid());
			cbGender.setValue((String) editEmployeelist.getGender());
		}
		if (editEmployeelist.getDob() != null) {
			dfDateofBirth.setValue(editEmployeelist.getDobinDt());
		}
		if (editEmployeelist.getDoj() != null) {
			dfDateofJoin.setValue(editEmployeelist.getDojInDt());
		}
		if (editEmployeelist.getCountryid() != null) {
			cbCountry.setValue(editEmployeelist.getCountryid());
		}
		cbBranch.setValue((Long) editEmployeelist.getBranchid());
		if (editEmployeelist.getLoginAccess() == null) {
			editEmployeelist.setLoginAccess("N");
		} else {
			if (editEmployeelist.getLoginAccess().equals("Y")) {
				cbCreateUser.setValue(true);
			} else if (editEmployeelist.getLoginAccess().equals("N")) {
				cbCreateUser.setValue(false);
			}
		}
		if (editEmployeelist.getEmpphoto() != null) {
			hlimage.removeAllComponents();
			byte[] myimage = (byte[]) editEmployeelist.getEmpphoto();
			UploadUI uploadObject = new UploadUI(hlimage);
			uploadObject.dispayImage(myimage, editEmployeelist.getFirstname());
		} else {
			new UploadUI(hlimage);
		}
	}
	
	/*
	 * editempaddress()-->this function is used for restore the selected row's data to Employee Address
	 */
	private void editempaddress() {
		Item empaddresselected = tblempaddress.getItem(tblempaddress.getValue());
		if (empaddresselected != null) {
			cbempadrstype.setValue(empaddresselected.getItemProperty("addresstype").getValue().toString());
			tfEmpaddressln1.setValue(empaddresselected.getItemProperty("addressln1").getValue().toString());
			if (empaddresselected.getItemProperty("addressln2").getValue() != null) {
				tfEmpaddressln2.setValue(empaddresselected.getItemProperty("addressln2").getValue().toString());
			}
			tfempaddresscityname.setValue(empaddresselected.getItemProperty("cityname").getValue().toString());
			tfempaddressstatename.setValue(empaddresselected.getItemProperty("statename").getValue().toString());
			cbempadrscountryid.setValue(empaddresselected.getItemProperty("countryid").getValue());
			tfempadrspostcode.setValue(empaddresselected.getItemProperty("postcode").getValue().toString());
			String stCode = empaddresselected.getItemProperty("addressstatus").getValue().toString();
			cbEmpaddressstatus.setValue(stCode);
		}
	}
	
	/*
	 * editempskill()-->this function is used for restore the selected row's data to Employee skill
	 */
	private void editempskill() {
		try {
			Item empskilllselected = tblempskilltbl.getItem(tblempskilltbl.getValue());
			if (empskilllselected != null) {
				Long skill = (Long) empskilllselected.getItemProperty("skillId").getValue();
				Collection<?> skillid = cbempskillid.getItemIds();
				for (java.util.Iterator<?> iterator = skillid.iterator(); iterator.hasNext();) {
					Object itemid = (Object) iterator.next();
					BeanItem<?> item = (BeanItem<?>) cbempskillid.getItem(itemid);
					SkillsDM st = (SkillsDM) item.getBean();
					if (skill != null && skill.equals(st.getSkillId())) {
						cbempskillid.setValue(itemid);
					}
				}
				if (empskilllselected.getItemProperty("isprimary").getValue().equals("Y")) {
					tfempskillisprimary.setValue(true);
				} else {
					tfempskillisprimary.setValue(false);
				}
				if (empskilllselected.getItemProperty("skilllevel").getValue() != null) {
					cbempskilllevel.setValue(empskilllselected.getItemProperty("skilllevel").getValue().toString());
				}
				String stCode = empskilllselected.getItemProperty("skillstatus").getValue().toString();
				cbempskillstatus.setValue(stCode);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * editempdtls()-->this function is used for restore the selected row's data to Employee Details
	 */
	private void editempdtls() {
		Item empdtlsselected = tblempdtls.getItem(tblempdtls.getValue());
		if (empdtlsselected != null) {
			Long dtls = (Long) empdtlsselected.getItemProperty("candidateid").getValue();
			Collection<?> dtlsid = cbempdtlscandid.getItemIds();
			for (java.util.Iterator<?> iterator = dtlsid.iterator(); iterator.hasNext();) {
				Object itemid = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbempdtlscandid.getItem(itemid);
				JobCandidateDM st = (JobCandidateDM) item.getBean();
				if (dtls != null && dtls.equals(st.getCandidateId())) {
					cbempdtlscandid.setValue(itemid);
				}
			}
			// cbempdtlscandid.setValue(empdtlsselected.getItemProperty("candidateid").getValue());
			// EmployeeDtlsDM dtls = beanEmployeedtls.getItem(tblempdtls.getValue()).getBean();
			if (empdtlsselected.getItemProperty("birthplace").getValue() != null) {
				tfempdtlsbirthplace.setValue(empdtlsselected.getItemProperty("birthplace").getValue().toString());
			}
			cbempdtlsbitrhcountry.setValue(empdtlsselected.getItemProperty("birthcountry").getValue());
			cbempdtlsmaritalstatus.setValue(empdtlsselected.getItemProperty("maritalstatus").getValue());
			dfempdtlsconfdate.setValue((Date) empdtlsselected.getItemProperty("confirmationndate").getValue());
			dfempdtlslasttpromotdate.setValue((Date) empdtlsselected.getItemProperty("lastpromottiondate").getValue());
			dfempdtlsreleaveddate.setValue((Date) empdtlsselected.getItemProperty("releaveddate").getValue());
			dfempdtlsdeathdtate.setValue((Date) empdtlsselected.getItemProperty("deathdate").getValue());
			if (empdtlsselected.getItemProperty("pfno").getValue() != null) {
				tfempdtlsPFno.setValue(empdtlsselected.getItemProperty("pfno").getValue().toString());
			}
			if (empdtlsselected.getItemProperty("esino").getValue() != null) {
				tfempdtlsESIno.setValue(empdtlsselected.getItemProperty("esino").getValue().toString());
			}
			if (empdtlsselected.getItemProperty("pensionno").getValue() != null) {
				tfempdtlsPENSON.setValue(empdtlsselected.getItemProperty("pensionno").getValue().toString());
			}
			cbempdtlsemptypeid.setValue(empdtlsselected.getItemProperty("employeetypeid").getValue());
			cbempdtlsgradid.setValue(empdtlsselected.getItemProperty("gradeid").getValue());
			// logger.info(" Designation id : " + dtls.getDesignationid());
			cbempdtlspayperid.setValue(empdtlsselected.getItemProperty("payperiodid").getValue());
			cbempdtlsnationalid.setValue(empdtlsselected.getItemProperty("nationalid").getValue());
			cbempdtlsstatusid.setValue(empdtlsselected.getItemProperty("employeestatusid").getValue());
			if (empdtlsselected.getItemProperty("statusreason").getValue() != null) {
				tfempdtlsstatusreasion.setValue(empdtlsselected.getItemProperty("statusreason").getValue().toString());
			}
			cbempdtlsdesignaton.setValue(empdtlsselected.getItemProperty("designationid").getValue());
		}
	}
	
	/*
	 * editempcontacts()-->this function is used for restore the selected row's data to Employee Contact
	 */
	private void editempcontacts() {
		Item empcontselected = tblempcont.getItem(tblempcont.getValue());
		if (empcontselected != null) {
			tfempcontactname.setValue(empcontselected.getItemProperty("contactname").getValue().toString());
			if (empcontselected.getItemProperty("relationship").getValue() != null) {
				tfempcontrelationship.setValue(empcontselected.getItemProperty("relationship").getValue().toString());
			}
			tfempcontphno.setValue(empcontselected.getItemProperty("phoneno").getValue().toString());
			if (empcontselected.getItemProperty("mobileno").getValue() != null) {
				tfempcontmono.setValue(empcontselected.getItemProperty("mobileno").getValue().toString());
			}
			String stcode = empcontselected.getItemProperty("contactstatus").getValue().toString();
			cbempcontstatus.setValue(stcode);
		}
	}
	
	/*
	 * editempimmgrtn()-->this function is used for restore the selected row's data to Employee Immigration
	 */
	@SuppressWarnings("deprecation")
	private void editempimmgrtn() {
		try {
			Item empimmgselected = tblempimmg.getItem(tblempimmg.getValue());
			if (empimmgselected != null) {
				// EmployeeImmgrtnDM empimmgobj = beanemployeeimmgrtn.getItem(tblempimmg.getValue()).getBean();
				logger.info("editempimmgrtn-->" + empimmgselected.getItemProperty("doctype").getValue());
				cbempimmgdoctype.setValue(empimmgselected.getItemProperty("doctype").getValue());
				tfempimmgdocno.setValue(empimmgselected.getItemProperty("docno").getValue().toString());
				if (empimmgselected.getItemProperty("issuedate").getValue() != null) {
					dfempimmgissuedf.setValue(new Date(empimmgselected.getItemProperty("issuedate").getValue()
							.toString()));
				}
				if (empimmgselected.getItemProperty("expirydate").getValue() != null) {
					dfempimmgexpirydf.setValue(new Date(empimmgselected.getItemProperty("expirydate").getValue()
							.toString()));
				}
				tfempimmgissuedby.setValue(empimmgselected.getItemProperty("issuedby").getValue().toString());
				tfempimmgdocremark.setValue(empimmgselected.getItemProperty("docremarks").getValue().toString());
				String stcode = empimmgselected.getItemProperty("immgrtnstatus").getValue().toString();
				cbempimmgstatus.setValue(stcode);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * editempbank()-->this function is used for restore the selected row's data to Employee Bank
	 */
	private void editempbank() {
		Item empbankselected = tblempbank.getItem(tblempbank.getValue());
		if (empbankselected != null) {
			tfempbankname.setValue(empbankselected.getItemProperty("bankname").getValue().toString());
			tfempacctno.setValue(empbankselected.getItemProperty("accntno").getValue().toString());
			cbempaccttype.setValue(empbankselected.getItemProperty("accnttype").getValue().toString());
			tfemproutingcode.setValue(empbankselected.getItemProperty("routingcode").getValue().toString());
			tfempbranchname.setValue(empbankselected.getItemProperty("branchname").getValue().toString());
			String stcode = empbankselected.getItemProperty("bankstatus").getValue().toString();
			cbempbankstatus.setValue(stcode);
		}
	}
	
	/*
	 * editempeducation()--> this function is used for restore the selected row's data to Employee Education
	 */
	@SuppressWarnings("deprecation")
	private void editempeducation() {
		Item empeduselected = tblempedu.getItem(tblempedu.getValue());
		if (empeduselected != null) {
			EmployeeEducationDM empeduobj = beanemployeeeducation.getItem(tblempedu.getValue()).getBean();
			// cbempeduqualtnid.setValue((empeduselected.getItemProperty("qufiid").getValue()));
			// if (empeduselected != null) {
			Long dtls = (Long) empeduselected.getItemProperty("qufiid").getValue();
			Collection<?> dtlsid = cbempeduqualtnid.getItemIds();
			for (java.util.Iterator<?> iterator = dtlsid.iterator(); iterator.hasNext();) {
				Object itemid = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbempeduqualtnid.getItem(itemid);
				QualificationDM st = (QualificationDM) item.getBean();
				if (dtls != null && dtls.equals(st.getQualId())) {
					cbempeduqualtnid.setValue(itemid);
				}
			}
			if (empeduselected.getItemProperty("subject").getValue() != null) {
				tfempedusubject.setValue(empeduselected.getItemProperty("subject").getValue().toString());
			}
			// if (empeduselected.getItemProperty("durfrm").getValue() != null) {
			// dfempedudurtnfrm.setValue(new Date(empeduselected.getItemProperty("durfrm").getValue().toString()));
			// }
			if (empeduobj.getDurfrm() != null)
			;
			{
				dfempedudurtnfrm.setValue(empeduobj.getDurfrom());
			}
			if (empeduselected.getItemProperty("durto").getValue() != null) {
				dfempedudurtnto.setValue(new Date(empeduselected.getItemProperty("durto").getValue().toString()));
			}
			if (empeduselected.getItemProperty("insname").getValue() != null) {
				tfempeduinstitnname.setValue(empeduselected.getItemProperty("insname").getValue().toString());
			}
			if (empeduselected.getItemProperty("usityname").getValue() != null) {
				tfempeduunivname.setValue(empeduselected.getItemProperty("usityname").getValue().toString());
			}
			tfempeduscoredmark.setValue(empeduselected.getItemProperty("srdmrks").getValue().toString());
			tfempedutotalmark.setValue(empeduselected.getItemProperty("totmrks").getValue().toString());
			cbempedugradeached.setValue(empeduselected.getItemProperty("gradeachcd").getValue().toString());
			String stcode = empeduselected.getItemProperty("empednstatus").getValue().toString();
			cbempedustatus.setValue(stcode);
		}
	}
	
	/*
	 * editempdepndent()-->this function is used for restore the selected row's data to Employee Dependent
	 */
	@SuppressWarnings("deprecation")
	private void editempdepndent() {
		Item empdepnselected = tblempdepn.getItem(tblempdepn.getValue());
		if (empdepnselected != null) {
			// EmployeeDependntsDM empdepobj;
			// empdepobj = beanemployeedependent.getItem(tblempdepn.getValue()).getBean();
			tfempdepndepname.setValue((empdepnselected.getItemProperty("dependntname").getValue().toString()));
			cbempdepnreltionship.setValue(empdepnselected.getItemProperty("relationship").getValue().toString());
			if (empdepnselected.getItemProperty("dob").getValue() != null) {
				dfempdepndob.setValue(new Date(empdepnselected.getItemProperty("dob").getValue().toString()));
			}
			cbempdepngender.setValue((empdepnselected.getItemProperty("gender").getValue()));
			String stcode = empdepnselected.getItemProperty("dependntstatus").getValue().toString();
			cbempdepnstatus.setValue(stcode);
		}
	}
	
	/*
	 * editempidentities()-->this function is used for restore the selected row's data to Employee Identities
	 */
	@SuppressWarnings("deprecation")
	private void editempidentities() {
		Item empidenselected = tblempiden.getItem(tblempiden.getValue());
		if (empidenselected != null) {
			cbempidenname.setValue((empidenselected.getItemProperty("idntname").getValue().toString()));
			tfempidenref.setValue((empidenselected.getItemProperty("idntref")).getValue().toString());
			// dfempidendate.setValue(editdtls.gete);
			if (empidenselected.getItemProperty("expdt").getValue() != null) {
				dfempidendate.setValue(new Date(empidenselected.getItemProperty("expdt").getValue().toString()));
			}
			if (empidenselected.getItemProperty("issuesby").getValue() != null) {
				tfempidenissuedby.setValue(empidenselected.getItemProperty("issuesby").getValue().toString());
			}
			String stcode = empidenselected.getItemProperty("empidnstatus").getValue().toString();
			cbempidenstatus.setValue(stcode);
		}
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
	protected void resetSearchDetails() {
		tfEmployeeCode.setValue("");
		tfFirstName.setValue("");
		cbEmpStatus.setValue(cbEmpStatus.getItemIds().iterator().next());
		cbDepartment.setValue(0L);
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Adding new record...");
		// remove the components in the search layout and input controls in the same container
		resetFields();
		// empdtlsResetfields();
		// empskillResetfields();
		// empaddressResetFields();
		// empcontactsResetfields();
		// empimmgrtnResetfields();
		// empbankResetfields();
		// empeduResetfields();
		// empdepnResetfields();
		// empidentitiesResetfields();
		// empdtlsResetfields();
		List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, null, null, "BS_EMPNO");
		tfEmployeeCode.setReadOnly(false);
		for (SlnoGenDM slnoObj : slnoList) {
			if (slnoObj.getAutoGenYN().equals("Y")) {
				tfEmployeeCode.setReadOnly(true);
			} else {
				tfEmployeeCode.setReadOnly(false);
				// Add Blur Listener for Add Layout
				tfEmployeeCode.addBlurListener(new BlurListener() {
					private static final long serialVersionUID = 1L;
					
					public void blur(BlurEvent event) {
						tfEmployeeCode.setComponentError(null);
						String employeeCodeSeq = tfEmployeeCode.getValue().toString();
						if (employeeCodeSeq == null || employeeCodeSeq == "") {
							tfEmployeeCode.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_CODE));
						} else {
							int count = servicebeanEmployee.getEmployeeList(null, employeeCodeSeq, null, "Active",
									companyid, null, null, null, null, "F").size();
							if (count == 0) {
								tfEmployeeCode.setComponentError(null);
							} else {
								if (flag == 0) {
									tfEmployeeCode.setComponentError(new UserError(
											GERPErrorCodes.EMPLOYEE_CODE_VALIDATION));
								}
								if (flag == 1) {
									tfEmployeeCode.setComponentError(null);
								}
							}
						}
					}
				});
			}
		}
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		tblMstScrSrchRslt.setValue(null);
		tblMstScrSrchRslt.setPageLength(8);
		tfEmployeeCode.setRequired(true);
		cbDepartment.setRequired(true);
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
		assembleUserInputLayout();
		loadSrchEmpAddress(false);
		loadsrchEmpSkill(false);
		loadsrchempcont(false);
		loadsrchempimmgrtn(false);
		loadsrchempbank(false);
		loadsrcheudcation(false);
		loadsrcdepnndnts(false);
		loadsrcidentities(false);
		loadsrchEmpdtls(false);
		// reset the input controls to default value
		// cbDepartment.removeItem(0L);
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Updating existing record...");
		// remove the components in the search layout and input controls in the same container
		resetFields();
		empdtlsResetfields();
		empskillResetfields();
		empaddressResetFields();
		empcontactsResetfields();
		empimmgrtnResetfields();
		empbankResetfields();
		empeduResetfields();
		empdepnResetfields();
		empidentitiesResetfields();
		tblMstScrSrchRslt.setPageLength(8);
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		assembleUserInputLayout();
		// Add Blur Listener for Edit Layout
		tfEmployeeCode.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfEmployeeCode.setComponentError(null);
				String employeeCodeSeq = tfEmployeeCode.getValue().toString();
				if (employeeCodeSeq == null || employeeCodeSeq == "") {
					tfEmployeeCode.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_CODE));
				} else {
					int count = servicebeanEmployee.getEmployeeList(null, employeeCodeSeq, null, "Active", companyid,
							null, null, null, null, "F").size();
					if (count == 0) {
						tfEmployeeCode.setComponentError(null);
					} else {
						if (flag == 0) {
							tfEmployeeCode.setComponentError(new UserError(GERPErrorCodes.EMPLOYEE_CODE_VALIDATION));
						}
						if (flag == 1) {
							tfEmployeeCode.setComponentError(null);
						}
					}
				}
			}
		});
		// resetFields();
		// cbDepartment.removeItem(0L);
		editEmployee();
		loadsrcidentities(true);
		loadsrcdepnndnts(true);
		loadSrchEmpAddress(true);
		loadsrchEmpSkill(true);
		loadsrchempcont(true);
		loadsrchempimmgrtn(true);
		loadsrchempbank(true);
		loadsrcheudcation(true);
		loadsrcidentities(true);
		loadsrchEmpdtls(true);
		if (tfEmployeeCode.getValue() == null || tfEmployeeCode.getValue().trim().length() == 0) {
			tfEmployeeCode.setReadOnly(false);
		}
		tblMstScrSrchRslt.setVisible(false);
		hlCmdBtnLayout.setVisible(false);
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		tfEmployeeCode.setComponentError(null);
		tfFirstName.setComponentError(null);
		cbDepartment.setComponentError(null);
		tfLastName.setComponentError(null);
		dfDateofBirth.setComponentError(null);
		dfDateofJoin.setComponentError(null);
		tfEmailid.setComponentError(null);
		cbBranch.setComponentError(null);
		cbCountry.setComponentError(null);
		tfPhonenumber.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((tfEmployeeCode.getValue() == null) || tfEmployeeCode.getValue().trim().length() == 0) {
			List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "BS_EMPNO");
			for (SlnoGenDM slnoObj : slnoList) {
				if (slnoObj.getAutoGenYN().equals("N")) {
					tfEmployeeCode.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_CODE));
					errorFlag = true;
				}
			}
		}
		if ((tfFirstName.getValue() == null) || tfFirstName.getValue().trim().length() == 0) {
			tfFirstName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_FIRST_NAME));
			errorFlag = true;
		}
		if ((tfLastName.getValue() == null) || tfLastName.getValue().trim().length() == 0) {
			tfLastName.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_LAST_NAME));
			errorFlag = true;
		}
		if (dfDateofJoin.getValue() == null) {
			dfDateofJoin.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DOJ));
			errorFlag = true;
		}
		if (cbBranch.getValue() == null) {
			cbBranch.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_BRANCH));
			errorFlag = true;
		}
		if ((Long) cbDepartment.getValue() == 0L) {
			cbDepartment.setComponentError(new UserError(GERPErrorCodes.NULL_DEPT_NAME));
			errorFlag = true;
		}
		if (cbCountry.getValue() == null) {
			cbCountry.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_COUNTRY));
			errorFlag = true;
		}
		if (tfPhonenumber.getValue().toString() == null) {
			tfPhonenumber.setComponentError(new UserError(GERPErrorCodes.NULL_PHONE_NUMBER));
			// errorFlag = true;
		} else if (tfPhonenumber.getValue() != null) {
			if (!tfPhonenumber.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
				tfPhonenumber.setComponentError(new UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION));
				errorFlag = true;
			}
		}
		if (dfDateofBirth.getValue() == null) {
			dfDateofBirth.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DOB));
			// errorFlag = true;
		} else if (dfDateofBirth.getValue().after(new Date()) || dfDateofBirth.getValue().equals(new Date())) {
			dfDateofBirth.setComponentError(new UserError(GERPErrorCodes.DATE_OF_BIRTH_VALIDATION));
			errorFlag = true;
		}
		if (dfDateofJoin.getValue() == null) {
			dfDateofJoin.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DOJ));
			// errorFlag = true;
		} else if (dfDateofJoin.getValue().before(dtdob)) {
			dfDateofJoin.setComponentError(new UserError(GERPErrorCodes.DATE_OF_JOIN_VALIDATION));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + tfEmployeeCode.getValue() + ","
				+ tfFirstName.getValue() + "," + tfLastName.getValue() + "," + cbGender.getValue() + ","
				+ tfPhonenumber + "," + dfDateofBirth.getValue() + "," + dfDateofJoin.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private void validationdtls() throws ValidationException {
		cbempdtlscandid.setComponentError(null);
		cbempdtlsemptypeid.setComponentError(null);
		cbempdtlsgradid.setComponentError(null);
		cbempdtlsdesignaton.setComponentError(null);
		cbempdtlspayperid.setComponentError(null);
		cbempdtlsmaritalstatus.setComponentError(null);
		cbempdtlsbitrhcountry.setComponentError(null);
		cbempdtlsnationalid.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbempdtlscandid.getValue() == null)) {
			cbempdtlscandid.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DTLS_CANDIDATE_NAME));
			errorFlag = true;
		}
		if ((cbempdtlsemptypeid.getValue() == null)) {
			cbempdtlsemptypeid.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DTLS_TYPE));
			errorFlag = true;
		}
		if ((cbempdtlsgradid.getValue() == null)) {
			cbempdtlsgradid.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DTLS_Grade));
			errorFlag = true;
		}
		if ((cbempdtlsdesignaton.getValue() == null)) {
			cbempdtlsdesignaton.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DTLS_Designation));
			errorFlag = true;
		}
		if ((cbempdtlspayperid.getValue() == null)) {
			cbempdtlspayperid.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DTLS_PayPeriod));
			errorFlag = true;
		}
		if ((cbempdtlsnationalid.getValue() == null)) {
			cbempdtlsnationalid.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DTLS_Nationality));
			errorFlag = true;
		}
		if ((cbempdtlsmaritalstatus.getValue() == null)) {
			cbempdtlsmaritalstatus.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DTLS_MARTIAL));
			errorFlag = true;
		}
		if ((cbempdtlsbitrhcountry.getValue() == null)) {
			cbempdtlsbitrhcountry.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DTLS_BIRTHCOUNTRY));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + cbempdtlscandid.getValue() + ","
				+ cbempdtlsemptypeid.getValue() + "," + cbempdtlsgradid.getValue() + ","
				+ cbempdtlsdesignaton.getValue() + "," + cbempdtlspayperid.getValue() + ","
				+ cbempdtlsmaritalstatus.getValue() + "," + cbempdtlsbitrhcountry.getValue() + ","
				+ cbempdtlsnationalid.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private void validationskill() throws ValidationException {
		cbempskillid.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbempskillid.getValue() == null)) {
			cbempskillid.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_SKill_Skill));
			errorFlag = true;
		} else
			logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Throwing ValidationException. User data is > " + cbempskillid.getValue() + ",");
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private void validationaddress() throws ValidationException {
		cbempadrstype.setComponentError(null);
		tfEmpaddressln1.setComponentError(null);
		tfempaddresscityname.setComponentError(null);
		tfempaddressstatename.setComponentError(null);
		tfempadrspostcode.setComponentError(null);
		cbempadrscountryid.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((tfEmployeeCode.getValue() == null) || tfEmployeeCode.getValue().trim().length() == 0) {
			List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "BS_EMPNO");
			for (SlnoGenDM slnoObj : slnoList) {
				if (slnoObj.getAutoGenYN().equals("N")) {
					tfEmployeeCode.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_CODE));
					errorFlag = true;
				}
			}
		}
		if ((cbempadrstype.getValue() == null)) {
			cbempadrstype.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_ADDRESS_ADDTYPE));
			errorFlag = true;
		}
		if ((tfEmpaddressln1.getValue() == "") || tfEmpaddressln1.getValue().trim().length() == 0) {
			tfEmpaddressln1.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_ADDRESS_LINE1));
			errorFlag = true;
		}
		if ((tfempaddresscityname.getValue() == "" || tfempaddresscityname.getValue().trim().length() == 0)) {
			tfempaddresscityname.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_ADDRESS_CITY));
			errorFlag = true;
		}
		if ((tfempaddressstatename.getValue() == "" || tfempaddressstatename.getValue().trim().length() == 0)) {
			tfempaddressstatename.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_ADDRESS_STATENAME));
			errorFlag = true;
		}
		if ((tfempadrspostcode.getValue() == "" || tfempadrspostcode.getValue().trim().length() == 0)) {
			tfempadrspostcode.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_ADDRESS_POSTCODE));
			errorFlag = true;
		}
		if ((cbempadrscountryid.getValue() == null)) {
			cbempadrscountryid.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_ADDRESS_COUNTRY));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + cbempadrstype.getValue() + ","
				+ tfEmpaddressln1.getValue() + "," + tfempaddresscityname.getValue() + ","
				+ tfempaddressstatename.getValue() + "," + tfempadrspostcode.getValue() + ","
				+ cbempadrscountryid.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private void validationcontact() throws ValidationException {
		tfempcontactname.setComponentError(null);
		tfempcontphno.setComponentError(null);
		tfempcontmono.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((tfempcontactname.getValue() == null || tfempcontactname.getValue().trim().length() == 0)) {
			tfempcontactname.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_CONTACTS_NAME));
			errorFlag = true;
		}
		if ((tfempcontmono.getValue() == null)) {
			tfempcontmono.setComponentError(new UserError(GERPErrorCodes.NULL_PHONE_NUMBER));
		} else if (tfempcontmono.getValue() != null) {
			if (!tfempcontmono.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) {
				tfempcontmono.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_CONTACTS_MOBILENUMBER));
				errorFlag = true;
			}
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + tfempcontactname.getValue() + ","
				+ tfempcontphno.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private void validationImmigration() throws ValidationException {
		cbempimmgdoctype.setComponentError(null);
		tfempimmgdocno.setComponentError(null);
		dfempimmgissuedf.setComponentError(null);
		dfempimmgexpirydf.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbempimmgdoctype.getValue() == null)) {
			cbempimmgdoctype.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_IMMIGRATION_DOCTYPE));
			errorFlag = true;
		}
		if ((tfempimmgdocno.getValue() == null || tfempimmgdocno.getValue().trim().length() == 0)) {
			tfempimmgdocno.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_IMMIGRATION_DOCNO));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + cbempimmgdoctype.getValue() + ","
				+ tfempimmgdocno.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private void validateionbank() throws ValidationException {
		tfempbankname.setComponentError(null);
		tfempacctno.setComponentError(null);
		tfempbranchname.setComponentError(null);
		cbempaccttype.setComponentError(null);
		tfemproutingcode.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((tfempbankname.getValue() == null || tfempbankname.getValue().trim().length() == 0)) {
			tfempbankname.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_BANK_NAME));
			errorFlag = true;
		}
		if ((tfempacctno.getValue() == null || tfempacctno.getValue().trim().length() == 0)) {
			tfempacctno.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_BANK_Accno));
			errorFlag = true;
		}
		if ((tfempbranchname.getValue() == null || tfempbranchname.getValue().trim().length() == 0)) {
			tfempbranchname.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_BANK_BRANCHNAME));
			errorFlag = true;
		}
		if ((cbempaccttype.getValue() == null)) {
			cbempaccttype.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_BANK_ACCOUNTTYPE));
			errorFlag = true;
		}
		if ((tfemproutingcode.getValue() == null || tfemproutingcode.getValue().trim().length() == 0)) {
			tfemproutingcode.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_BANK_ROUTINGCODE));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + tfempbankname.getValue() + ","
				+ tfempacctno.getValue() + "," + tfempbranchname.getValue() + "," + cbempaccttype.getValue() + ","
				+ tfemproutingcode.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private void validationeducation() throws ValidationException {
		cbempeduqualtnid.setComponentError(null);
		cbempedugradeached.setComponentError(null);
		dfempedudurtnfrm.setComponentError(null);
		dfempedudurtnto.setComponentError(null);
		tfempeduscoredmark.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbempeduqualtnid.getValue() == null)) {
			cbempeduqualtnid.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_Education_Qualification));
			errorFlag = true;
		}
		if ((cbempedugradeached.getValue() == null)) {
			cbempedugradeached.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_Education_GRADE));
			errorFlag = true;
		}
		// if (dfempedudurtnfrm.getValue() != null) {
		// if (dfempedudurtnfrm.getValue().after(new Date()) || dfempedudurtnfrm.getValue().equals(new Date())) {
		// dfempedudurtnfrm.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_Education_FROMDATE));
		// errorFlag = true;
		// } else {
		// dtfrmdate = dfempedudurtnfrm.getValue();
		// dfempedudurtnfrm.setComponentError(null);
		//
		// }
		// }
		//
		//
		// if (dfempedudurtnto.getValue().before(dtfrmdate)) {
		// dfempedudurtnto.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_Education_TODATE));
		// errorFlag = true;
		// } else {
		// dfempedudurtnto.setComponentError(null);
		//
		// }
		scoredmark = Long.valueOf(tfempeduscoredmark.getValue());
		totalmark = Long.valueOf(tfempedutotalmark.getValue());
		if (scoredmark > totalmark) {
			tfempeduscoredmark.setComponentError(new UserError("Scored Mark should be less than Totoal Mark"));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + cbempeduqualtnid.getValue() + ","
				+ tfempeduscoredmark.getValue() + cbempedugradeached.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private void validationdependant() throws ValidationException {
		tfempdepndepname.setComponentError(null);
		cbempdepnreltionship.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((tfempdepndepname.getValue() == null || tfempdepndepname.getValue().trim().length() == 0)) {
			tfempdepndepname.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DEPENDANT_NAME));
			errorFlag = true;
		}
		if ((cbempdepnreltionship.getValue() == null)) {
			cbempdepnreltionship.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_DEPENDANT_RELATIONSHIP));
			errorFlag = true;
		}
		logger.warn("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Throwing ValidationException. User data is > " + tfempdepndepname.getValue() + ","
				+ cbempdepnreltionship.getValue());
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	private void validationiIdenti() throws ValidationException {
		cbempidenname.setComponentError(null);
		tfempidenref.setComponentError(null);
		Boolean errorFlag = false;
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Validating Data ");
		if ((cbempidenname.getValue() == null)) {
			cbempidenname.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_Identity));
			errorFlag = true;
		}
		if ((tfempidenref.getValue() == null || tfempidenref.getValue().trim().length() == 0)) {
			tfempidenref.setComponentError(new UserError(GERPErrorCodes.NULL_EMPLOYEE_Refferenceid));
			errorFlag = true;
		}
		if (errorFlag) {
			throw new ERPException.ValidationException();
		}
	}
	
	/*
	 * saveEmployeeAddress()-->this function is used for save the Employee Address details for temporary
	 */
	protected void saveEmployeeAddress() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Saving Employee Address details Data... ");
		EmployeeAddressDM empaddrsobj = new EmployeeAddressDM();
		if (tblempaddress.getValue() != null) {
			empaddrsobj = beanEmployeeAddress.getItem(tblempaddress.getValue()).getBean();
			empaddresslist.remove(empaddrsobj);
		}
		empaddrsobj.setAddresstype((String) cbempadrstype.getValue().toString());
		empaddrsobj.setAddressln1((String) tfEmpaddressln1.getValue().toString());
		empaddrsobj.setAddressln2((String) tfEmpaddressln2.getValue().toString());
		empaddrsobj.setCityname((String) tfempaddresscityname.getValue().toString());
		empaddrsobj.setStatename((String) tfempaddressstatename.getValue().toString());
		empaddrsobj.setCountryid((Long) cbempadrscountryid.getValue());
		empaddrsobj.setPostcode((String) tfempadrspostcode.getValue().toString());
		empaddrsobj.setAddressstatus((String) cbEmpaddressstatus.getValue());
		empaddrsobj.setLastupdateddt(DateUtils.getcurrentdate());
		empaddrsobj.setLastupdatedby(username);
		empaddresslist.add(empaddrsobj);
		empaddressResetFields();
		loadSrchEmpAddress(false);
	}
	
	/*
	 * saveEmpSKillDetails()-->this function is used for save the Employee Skill details for temporary
	 */
	protected void saveEmpSKillDetails() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Saving Employee Skill details Data... ");
			EmployeeSkillDM empskillobj = new EmployeeSkillDM();
			if (tblempskilltbl.getValue() != null) {
				empskillobj = beanEmployeeskill.getItem(tblempskilltbl.getValue()).getBean();
				employeeSkillList.remove(empskillobj);
			}
			empskillobj.setSkillId(((SkillsDM) cbempskillid.getValue()).getSkillId());
			empskillobj.setSkillname(((SkillsDM) cbempskillid.getValue()).getSkillName());
			if (tfempskillisprimary.getValue() == null || tfempskillisprimary.getValue().equals(false)) {
				empskillobj.setIsprimary("N");
			} else {
				empskillobj.setIsprimary("Y");
			}
			if (cbempskilllevel.getValue() != null) {
				empskillobj.setSkilllevel((String) cbempskilllevel.getValue().toString());
			}
			empskillobj.setSkillstatus("Pending");
			empskillobj.setLastupdateddt(DateUtils.getcurrentdate());
			empskillobj.setLastupdatedby(username);
			employeeSkillList.add(empskillobj);
			empskillResetfields();
			loadsrchEmpSkill(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * saveEmpDtls()-->this function is used for save the Employee Details for temporary
	 */
	protected void saveEmpDtls() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
					+ "Saving Employee Details Data... ");
			EmployeeDtlsDM empdtlsobj = new EmployeeDtlsDM();
			if (tblempdtls.getValue() != null) {
				empdtlsobj = beanEmployeedtls.getItem(tblempdtls.getValue()).getBean();
				employeedtlsList.remove(empdtlsobj);
			}
			empdtlsobj.setCandidateid(((JobCandidateDM) cbempdtlscandid.getValue()).getCandidateId());
			empdtlsobj.setCandidatename(((JobCandidateDM) cbempdtlscandid.getValue()).getFirstName() + " "
					+ ((JobCandidateDM) cbempdtlscandid.getValue()).getLastName() + " " + "("
					+ ((JobCandidateDM) cbempdtlscandid.getValue()).getCandidateId() + ")");
			empdtlsobj.setBirthplace((String) tfempdtlsbirthplace.getValue().toString());
			empdtlsobj.setPfno((String) tfempdtlsPFno.getValue().toString());
			empdtlsobj.setEsino((String) tfempdtlsESIno.getValue().toString());
			empdtlsobj.setPensionno((String) tfempdtlsPENSON.getValue().toString());
			empdtlsobj.setMaritalstatus((String) cbempdtlsmaritalstatus.getValue().toString());
			empdtlsobj.setBirthcountry((Long) cbempdtlsbitrhcountry.getValue());
			empdtlsobj.setConfirmationndate((Date) dfempdtlsconfdate.getValue());
			empdtlsobj.setLastpromottiondate((Date) dfempdtlslasttpromotdate.getValue());
			empdtlsobj.setReleaveddate((Date) dfempdtlsreleaveddate.getValue());
			empdtlsobj.setDeathdate((Date) dfempdtlsdeathdtate.getValue());
			empdtlsobj.setEmployeetypeid((Long) cbempdtlsemptypeid.getValue());
			empdtlsobj.setGradeid((Long) cbempdtlsgradid.getValue());
			empdtlsobj.setDesignationid((Long) cbempdtlsdesignaton.getValue());
			empdtlsobj.setPayperiodid((Long) cbempdtlspayperid.getValue());
			empdtlsobj.setNationalid((Long) cbempdtlsnationalid.getValue());
			empdtlsobj.setEmployeestatusid((Long) cbempdtlsstatusid.getValue());
			empdtlsobj.setStatusreason((String) tfempdtlsstatusreasion.getValue());
			empdtlsobj.setLastupdateddate(DateUtils.getcurrentdate());
			empdtlsobj.setLastupdatedby(username);
			employeedtlsList.add(empdtlsobj);
			empdtlsResetfields();
			loadsrchEmpdtls(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * saveEmpContacts()-->this function is used for save the Employee Contact for temporary
	 */
	protected void saveEmpContacts() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Saving Employee Contact Data... ");
		try {
			EmployeeContactsDM empcontobj = new EmployeeContactsDM();
			if (tblempcont.getValue() != null) {
				empcontobj = beanEmployeecontacts.getItem(tblempcont.getValue()).getBean();
				employeecontList.remove(empcontobj);
			}
			if (tfempcontactname.getValue() != null) {
				empcontobj.setContactname((String) tfempcontactname.getValue());
			}
			if (tfempcontrelationship.getValue() != null) {
				empcontobj.setRelationship((String) tfempcontrelationship.getValue().toString());
			}
			if (tfempcontphno.getValue() != null) {
				empcontobj.setPhoneno((String) tfempcontphno.getValue().toString());
			}
			if (tfempcontmono.getValue() != null) {
				empcontobj.setMobileno((String) tfempcontmono.getValue().toString());
			}
			if (cbempcontstatus.getValue() != null) {
				empcontobj.setContactstatus((String) cbempcontstatus.getValue().toString());
			}
			empcontobj.setLastupdateddate(DateUtils.getcurrentdate());
			empcontobj.setLastupdatedby(username);
			employeecontList.add(empcontobj);
			empcontactsResetfields();
			loadsrchempcont(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * saveempimmgrtn()-->this function is used for save the Employee Immigration for temporary
	 */
	protected void saveempimmgrtn() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Saving Employee Immigration Data... ");
		EmployeeImmgrtnDM empimmgr = new EmployeeImmgrtnDM();
		if (tblempimmg.getValue() != null) {
			empimmgr = beanemployeeimmgrtn.getItem(tblempimmg.getValue()).getBean();
			employeeimmglist.remove(empimmgr);
		}
		// empimmgr.setEmployeeid(employeeid);
		empimmgr.setDoctype((String) cbempimmgdoctype.getValue());
		empimmgr.setDocno((String) tfempimmgdocno.getValue().toString());
		empimmgr.setIssuedate((Date) dfempimmgissuedf.getValue());
		empimmgr.setExpirydate((Date) dfempimmgexpirydf.getValue());
		empimmgr.setIssuedby((String) tfempimmgissuedby.getValue().toString());
		empimmgr.setDocremarks((String) tfempimmgdocremark.getValue().toString());
		empimmgr.setImmgrtnstatus((String) cbempimmgstatus.getValue());
		empimmgr.setLastupdateddate(DateUtils.getcurrentdate());
		empimmgr.setLastupdatedby(username);
		employeeimmglist.add(empimmgr);
		empimmgrtnResetfields();
		loadsrchempimmgrtn(false);
	}
	
	/*
	 * saveempbank()-->this function is used for save the Employee Bank for temporary
	 */
	protected void saveempbank() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Saving Employee Bank Data... ");
		EmployeeBankDM employeebankObj = new EmployeeBankDM();
		if (tblempbank.getValue() != null) {
			employeebankObj = beanemployeebank.getItem(tblempbank.getValue()).getBean();
			employeebanklist.remove(employeebankObj);
		}
		employeebankObj.setBankname((String) tfempbankname.getValue().toString());
		employeebankObj.setAccntno((String) tfempacctno.getValue().toString());
		employeebankObj.setAccnttype((String) cbempaccttype.getValue().toString());
		employeebankObj.setRoutingcode((String) tfemproutingcode.getValue().toString());
		employeebankObj.setBranchname((String) tfempbranchname.getValue().toString());
		employeebankObj.setBankstatus((String) cbempbankstatus.getValue());
		employeebankObj.setLastupdtdt((DateUtils.getcurrentdate()));
		employeebankObj.setLastupdtby(username);
		employeebanklist.add(employeebankObj);
		empbankResetfields();
		loadsrchempbank(false);
	}
	
	/*
	 * saveempbank()-->this function is used for save the Employee Education for temporary
	 */
	protected void saveempeducation() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Saving Employee Education Data... ");
		EmployeeEducationDM employeeeduobj = new EmployeeEducationDM();
		if (tblempedu.getValue() != null) {
			employeeeduobj = beanemployeeeducation.getItem(tblempedu.getValue()).getBean();
			employeeedulist.remove(employeeeduobj);
		}
		employeeeduobj.setQufiid((Long) cbempeduqualtnid.getValue());
		employeeeduobj.setSubject((String) tfempedusubject.getValue().toString());
		employeeeduobj.setDurfrm((Date) dfempedudurtnfrm.getValue());
		employeeeduobj.setDurto((Date) dfempedudurtnto.getValue());
		employeeeduobj.setInsname((String) tfempeduinstitnname.getValue().toString());
		employeeeduobj.setUsityname((String) tfempeduunivname.getValue().toString());
		if (tfempeduscoredmark.getValue() != null) {
			employeeeduobj.setSrdmrks(Long.valueOf(tfempeduscoredmark.getValue()));
		}
		if (tfempedutotalmark.getValue() != null) {
			employeeeduobj.setTotmrks(Long.valueOf(tfempedutotalmark.getValue()));
		}
		employeeeduobj.setGradeachcd((String) cbempedugradeached.getValue().toString());
		employeeeduobj.setEmpednstatus((String) cbempedustatus.getValue().toString());
		employeeeduobj.setLastupdatdby(username);
		employeeeduobj.setLastupdatddt((DateUtils.getcurrentdate()));
		employeeedulist.add(employeeeduobj);
		empeduResetfields();
		loadsrcheudcation(false);
	}
	
	/*
	 * saveempdepnndnt()-->this function is used for save the Employee Dependant for temporary
	 */
	protected void saveempdepnndnt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Saving Employee Dependant Data... ");
		EmployeeDependntsDM empdepnobj = new EmployeeDependntsDM();
		if (tblempdepn.getValue() != null) {
			empdepnobj = beanemployeedependent.getItem(tblempdepn.getValue()).getBean();
			employeedepnlist.remove(empdepnobj);
		}
		empdepnobj.setDependntname((String) tfempdepndepname.getValue().toString());
		empdepnobj.setRelationship((String) cbempdepnreltionship.getValue().toString());
		empdepnobj.setDob(dfempdepndob.getValue());
		if (cbempdepngender.getValue() != null) {
			empdepnobj.setGender((String) cbempdepngender.getValue().toString());
		}
		empdepnobj.setDependntstatus((String) cbempdepnstatus.getValue().toString());
		empdepnobj.setLastupdatedby(username);
		empdepnobj.setLastupdateddate(DateUtils.getcurrentdate());
		employeedepnlist.add(empdepnobj);
		empdepnResetfields();
		loadsrcdepnndnts(false);
	}
	
	/*
	 * saveempidentities()-->this function is used for save the Employee Identities for temporary
	 */
	protected void saveempidentities() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Saving Employee Identities Data... ");
		EmployeeIdentitiesDM empidenobj = new EmployeeIdentitiesDM();
		logger.info("saveempidentities--->" + tblempiden);
		if (tblempiden.getValue() != null) {
			empidenobj = beanemployeeidentities.getItem(tblempiden.getValue()).getBean();
			employeeidentitieslist.remove(empidenobj);
		}
		empidenobj.setIdntname((String) cbempidenname.getValue().toString());
		empidenobj.setIdntref((String) tfempidenref.getValue().toString());
		empidenobj.setExpdt((Date) dfempidendate.getValue());
		empidenobj.setIssuesby((String) tfempidenissuedby.getValue().toString());
		empidenobj.setEmpidnstatus((String) cbempidenstatus.getValue());
		empidenobj.setLastupdtby(username);
		empidenobj.setLastupdtdt(DateUtils.getcurrentdate());
		employeeidentitieslist.add(empidenobj);
		empidentitiesResetfields();
		loadsrcidentities(false);
	}
	
	/*
	 * saveUser()-->this function is used for save the user
	 */
	private void saveUser() {
		try {
			RandomNoGenerator randomGen = new RandomNoGenerator();
			UserDM saveUsr = new UserDM();
			saveUsr.setLoginid(tfFirstName.getValue() + "@" + companyCode.toLowerCase());
			saveUsr.setLoginpassword(randomGen.generateRandomString());
			saveUsr.setLogincount(0L);
			saveUsr.setSystemuseryn("N");
			saveUsr.setTimezoneid(timezoneId);
			saveUsr.setCreationdt(DateUtils.getcurrentdate());
			// dfreturnDt.setValue(addDays(dfissueDt.getValue(), 15));//
			saveUsr.setPasswordexpiredt(addDays(new Date(), 30));
			if (cbCreateUser.getValue().equals(true)) {
				saveUsr.setUserstatus("Active");
			}
			saveUsr.setCompanyid(companyid);
			servicebeanUser.saveorUpdateUserDetails(saveUsr);
			userId = saveUsr.getUserid();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * saveDetails()-->this function is used for save/update the records
	 */
	@Override
	protected void saveDetails() throws IOException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
		EmployeeDM empObj = new EmployeeDM();
		UserDM saveUsr = new UserDM();
		userId = saveUsr.getUserid();
		if (tblMstScrSrchRslt.getValue() != null) {
			empObj = beanEmployee.getItem(tblMstScrSrchRslt.getValue()).getBean();
			empObj.setEmployeecode(tfEmployeeCode.getValue());
		} else {
			System.out.println("companyid----->" + companyid);
			System.out.println("branchID----->" + branchID);
			System.out.println("moduleId----->" + moduleId);
			System.out.println("BS_EMPNO----->" + "BS_EMPNO");
			List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "BS_EMPNO");
			for (SlnoGenDM slnoObj : slnoList) {
				if (slnoObj.getAutoGenYN().equals("Y")) {
					empObj.setEmployeecode(slnoObj.getKeyDesc());
				}
			}
		}
		empObj.setCompanyid(companyid);
		// empObj.setEmployeecode(tfEmployeeCode.getValue().toString());
		empObj.setFirstname(tfFirstName.getValue().toString());
		empObj.setLastname(tfLastName.getValue().toString());
		empObj.setPrimaryphone(tfPhonenumber.getValue().toString());
		empObj.setPrimaryemail(tfEmailid.getValue().toString());
		if (cbGender.getValue() != null) {
			empObj.setGender(((String) cbGender.getValue()));
		}
		if (cbCountry.getValue() != null) {
			empObj.setCountryid((Long) cbCountry.getValue());
		}
		if (cbEmpStatus.getValue() != null) {
			empObj.setEmpstatus((String) cbEmpStatus.getValue());
		}
		if (dfDateofJoin.getValue() != null) {
			empObj.setDoj(dfDateofJoin.getValue());
		}
		if (dfDateofBirth.getValue() != null) {
			empObj.setDob(dfDateofBirth.getValue());
		}
		if (cbDepartment.getValue() != null) {
			empObj.setDeptid((Long) cbDepartment.getValue());
		}
		if (cbBranch.getValue() != null) {
			empObj.setBranchid((Long) cbBranch.getValue());
		}
		if (cbManager.getValue() != null) {
			empObj.setRmemployeeid((Long) cbManager.getValue());
		}
		if ((Boolean) UI.getCurrent().getSession().getAttribute("isFileUploaded")) {
			try {
				empObj.setEmpphoto((byte[]) UI.getCurrent().getSession().getAttribute("imagebyte"));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			empObj.setEmpphoto(null);
		}
		System.out.println("User-------------->" + userId);
		if (userId == null) {
			if (cbCreateUser.getValue().equals(true)) {
				saveUser();
				empObj.setUserId(userId);
				empObj.setLoginAccess("Y");
			} else if (cbCreateUser.getValue() == false) {
				empObj.setLoginAccess("N");
			}
		} else if (userId != null) {
			if (cbCreateUser.getValue().equals(true)) {
				servicebeanEmployee.updateUserStatus(userId);
				saveUsr.setUserstatus("Active");
				empObj.setLoginAccess("Y");
			} else if (cbCreateUser.getValue().equals(false)) {
				servicebeanEmployee.updateUserStatus(userId);
				saveUsr.setUserstatus("Inactive");
				empObj.setLoginAccess("N");
			}
		}
		empObj.setLastupdateddt(DateUtils.getcurrentdate());
		empObj.setLastupdatedby(username);
		servicebeanEmployee.saveOrUpdateEmploye(empObj);
		if (tblMstScrSrchRslt.getValue() == null) {
			List<SlnoGenDM> slnoList = serviceSlnogen.getSequenceNumber(companyid, branchID, moduleId, "BS_EMPNO");
			for (SlnoGenDM slnoObj : slnoList) {
				if (slnoObj.getAutoGenYN().equals("Y")) {
					serviceSlnogen.updateNextSequenceNumber(companyid, branchID, moduleId, "BS_EMPNO");
				}
			}
		}
		@SuppressWarnings("unchecked")
		Collection<EmployeeDtlsDM> empdtlsitemids = (Collection<EmployeeDtlsDM>) tblempdtls.getVisibleItemIds();
		for (EmployeeDtlsDM saveemployeedtls : (Collection<EmployeeDtlsDM>) empdtlsitemids) {
			saveemployeedtls.setEmployeeid(Long.valueOf(empObj.getEmployeeid()));
			servempdtls.saveorupdateEmployeeDtls(saveemployeedtls);
			// for employee earinngs
			try {
				insertEmployeeGradeEarning(empObj.getEmployeeid(), saveemployeedtls.getGradeid());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				insertEmployeeDeduction(empObj.getEmployeeid(), saveemployeedtls.getGradeid());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				insertGradeAllowance(empObj.getEmployeeid(), saveemployeedtls.getGradeid());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		@SuppressWarnings("unchecked")
		Collection<EmployeeSkillDM> empskillitem = (Collection<EmployeeSkillDM>) tblempskilltbl.getVisibleItemIds();
		for (EmployeeSkillDM saveempskill : (Collection<EmployeeSkillDM>) empskillitem) {
			saveempskill.setEmployeeId(Long.valueOf(empObj.getEmployeeid()));
			serviceemployeeskill.saveAndUpdate(saveempskill);
		}
		@SuppressWarnings("unchecked")
		Collection<EmployeeAddressDM> empaddressitem = (Collection<EmployeeAddressDM>) tblempaddress
				.getVisibleItemIds();
		for (EmployeeAddressDM saveempaddrs : (Collection<EmployeeAddressDM>) empaddressitem) {
			saveempaddrs.setEmployeeid(Long.valueOf(empObj.getEmployeeid()));
			servempaddr.saveAndUpdate(saveempaddrs);
		}
		@SuppressWarnings("unchecked")
		Collection<EmployeeContactsDM> empcontitem = (Collection<EmployeeContactsDM>) tblempcont.getVisibleItemIds();
		for (EmployeeContactsDM saveempcont : (Collection<EmployeeContactsDM>) empcontitem) {
			saveempcont.setEmployeeid(Long.valueOf(empObj.getEmployeeid()));
			servempcontact.saveorupdateEmployeeCont(saveempcont);
		}
		@SuppressWarnings("unchecked")
		Collection<EmployeeImmgrtnDM> empimmgitem = (Collection<EmployeeImmgrtnDM>) tblempimmg.getVisibleItemIds();
		for (EmployeeImmgrtnDM saveempimmgrtn : (Collection<EmployeeImmgrtnDM>) empimmgitem) {
			saveempimmgrtn.setEmployeeid(Long.valueOf(empObj.getEmployeeid()));
			servempimmgrtn.saveorupdateEmployeeImmgrtn(saveempimmgrtn);
		}
		@SuppressWarnings("unchecked")
		Collection<EmployeeBankDM> empbankitem = (Collection<EmployeeBankDM>) tblempbank.getVisibleItemIds();
		for (EmployeeBankDM saveempbank : (Collection<EmployeeBankDM>) empbankitem) {
			saveempbank.setEmpid(Long.valueOf(empObj.getEmployeeid()));
			serviceempbnk.saveAndUpdate(saveempbank);
		}
		@SuppressWarnings("unchecked")
		Collection<EmployeeEducationDM> empeduitem = (Collection<EmployeeEducationDM>) tblempedu.getVisibleItemIds();
		for (EmployeeEducationDM saveempedu : (Collection<EmployeeEducationDM>) empeduitem) {
			saveempedu.setEmpid(Long.valueOf(empObj.getEmployeeid()));
			serviceempedu.saveAndUpdate(saveempedu);
		}
		@SuppressWarnings("unchecked")
		Collection<EmployeeDependntsDM> empdepnitem = (Collection<EmployeeDependntsDM>) tblempdepn.getVisibleItemIds();
		for (EmployeeDependntsDM saveempdepn : (Collection<EmployeeDependntsDM>) empdepnitem) {
			saveempdepn.setEmployeeid(Long.valueOf(empObj.getEmployeeid()));
			serviceempdeps.saveorupdateEmployeeDependnts(saveempdepn);
		}
		@SuppressWarnings("unchecked")
		Collection<EmployeeIdentitiesDM> empidenitem = (Collection<EmployeeIdentitiesDM>) tblempiden
				.getVisibleItemIds();
		for (EmployeeIdentitiesDM saveempiden : (Collection<EmployeeIdentitiesDM>) empidenitem) {
			saveempiden.setEmpid(Long.valueOf(empObj.getEmployeeid()));
			serviceempides.saveAndUpdate(saveempiden);
		}
		flag = 0;
		employeeid = 0L;
		// cbDepartment.removeItem(0L);
		loadSrchRslt();
		loadSrchEmpAddress(true);
		loadsrchEmpSkill(true);
		loadsrchempcont(true);
		loadsrchEmpdtls(true);
		loadsrchempimmgrtn(true);
		loadsrchempbank(true);
		loadsrcheudcation(true);
		loadsrcdepnndnts(true);
		loadsrcidentities(true);
		resetFields();
		empdtlsResetfields();
		empskillResetfields();
		empaddressResetFields();
		empcontactsResetfields();
		empimmgrtnResetfields();
		empbankResetfields();
		empeduResetfields();
		empdepnResetfields();
		empidentitiesResetfields();
	}
	
	/*
	 * editMaterialConsumer()-->this function is used for restore the selected row's data to Employee Address
	 */
	protected void empaddressResetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Address UI controls");
		cbempadrstype.setValue(null);
		tfEmpaddressln1.setValue("");
		tfEmpaddressln2.setValue("");
		tfempaddresscityname.setValue("");
		tfempaddressstatename.setValue("");
		cbempadrscountryid.setValue(countryid);
		tfempadrspostcode.setValue("");
		tblempaddress.setValue("");
		btnaddempadd.setCaption("add");
		cbEmpaddressstatus.setValue(cbEmpaddressstatus.getItemIds().iterator().next());
		cbempadrstype.setComponentError(null);
		tfEmpaddressln1.setComponentError(null);
		tfempaddresscityname.setComponentError(null);
		tfempaddressstatename.setComponentError(null);
		tfempadrspostcode.setComponentError(null);
		cbempadrscountryid.setComponentError(null);
	}
	
	/*
	 * empskillResetfields()-->this function is used for restore the selected row's data to Employee skill
	 */
	protected void empskillResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee skill UI controls");
		cbempskillid.setValue(null);
		cbempskillid.setComponentError(null);
		tfempskillisprimary.setValue(null);
		cbempskilllevel.setValue(null);
		cbempskillid.setComponentError(null);
		cbempskillstatus.setValue(cbempskillstatus.getItemIds().iterator().next());
		tblempskilltbl.removeAllItems();
		btnaddempskill.setCaption("add");
	}
	
	/*
	 * empdtlsResetfields()-->this function is used for restore the selected row's data to Employee Details
	 */
	protected void empdtlsResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Details UI controls");
		tfempdtlsbirthplace.setValue("");
		cbempdtlsbitrhcountry.setValue(countryid);
		tfempdtlsPFno.setValue("");
		tfempdtlsESIno.setValue("");
		tfempdtlsPENSON.setValue("");
		cbempdtlsmaritalstatus.setValue(null);
		dfempdtlsconfdate.setValue(null);
		dfempdtlslasttpromotdate.setValue(null);
		dfempdtlsreleaveddate.setValue(null);
		dfempdtlsdeathdtate.setValue(null);
		cbempdtlsemptypeid.setValue(null);
		cbempdtlscandid.setValue(null);
		cbempdtlsgradid.setValue(null);
		cbempdtlsdesignaton.setValue(null);
		cbempdtlspayperid.setValue(null);
		cbempdtlsnationalid.setValue(countryid);
		cbempdtlsstatusid.setValue(null);
		tfempdtlsstatusreasion.setValue("");
		btnaddempdtls.setCaption("add");
		cbempdtlscandid.setComponentError(null);
		cbempdtlsemptypeid.setComponentError(null);
		cbempdtlsgradid.setComponentError(null);
		cbempdtlsdesignaton.setComponentError(null);
		cbempdtlspayperid.setComponentError(null);
		cbempdtlsmaritalstatus.setComponentError(null);
		cbempdtlsbitrhcountry.setComponentError(null);
		cbempdtlsnationalid.setComponentError(null);
		dfempdtlsconfdate.setComponentError(null);
		dfempdtlslasttpromotdate.setComponentError(null);
		dfempdtlsreleaveddate.setComponentError(null);
		dfempdtlsdeathdtate.setComponentError(null);
	}
	
	/*
	 * empcontactsResetfields()-->this function is used for restore the selected row's data to Employee Contact
	 */
	protected void empcontactsResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Contact UI controls");
		tfempcontactname.setValue("");
		tfempcontrelationship.setValue("");
		tfempcontphno.setValue("");
		tfempcontmono.setValue("");
		btnaddempcont.setCaption("add");
		cbempcontstatus.setValue(cbempcontstatus.getItemIds().iterator().next());
		tfempcontactname.setComponentError(null);
		tfempcontphno.setComponentError(null);
		tfempcontmono.setComponentError(null);
	}
	
	/*
	 * empimmgrtnResetfields()-->this function is used for restore the selected row's data to Employee Immigration
	 */
	protected void empimmgrtnResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Immigration UI controls");
		cbempimmgdoctype.setValue(null);
		tfempimmgdocno.setValue("");
		dfempimmgissuedf.setValue(null);
		dfempimmgexpirydf.setValue(null);
		tfempimmgissuedby.setValue("");
		tfempimmgdocremark.setValue("");
		btnaddempimmg.setCaption("add");
		cbempimmgstatus.setValue(cbempimmgstatus.getItemIds().iterator().next());
		cbempimmgdoctype.setComponentError(null);
		tfempimmgdocno.setComponentError(null);
		dfempimmgissuedf.setComponentError(null);
		dfempimmgexpirydf.setComponentError(null);
	}
	
	/*
	 * empbankResetfields()-->this function is used for restore the selected row's data to Employee Bank
	 */
	protected void empbankResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Bank UI controls");
		tfempbankname.setValue("");
		tfempacctno.setValue("");
		cbempaccttype.setValue(null);
		tfemproutingcode.setValue("");
		tfempbranchname.setValue("");
		btnaddempbank.setCaption("add");
		cbempbankstatus.setValue(cbempbankstatus.getItemIds().iterator().next());
		tfempbankname.setComponentError(null);
		tfempacctno.setComponentError(null);
		tfempbranchname.setComponentError(null);
		cbempaccttype.setComponentError(null);
		tfemproutingcode.setComponentError(null);
	}
	
	/*
	 * empeduResetfields()-->this function is used for restore the selected row's data to Employee Education
	 */
	protected void empeduResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Education UI controls");
		cbempeduqualtnid.setValue(null);
		tfempedusubject.setValue("");
		dfempedudurtnfrm.setValue(null);
		dfempedudurtnto.setValue(null);
		tfempeduinstitnname.setValue("");
		tfempeduunivname.setValue("");
		tfempeduscoredmark.setValue("0");
		tfempedutotalmark.setValue("0");
		cbempedugradeached.setValue(null);
		btnaddempedu.setCaption("add");
		cbempedustatus.setValue(cbempedustatus.getItemIds().iterator().next());
		cbempeduqualtnid.setComponentError(null);
		// tfempeduscoredmark.setComponentError(null);
		cbempedugradeached.setComponentError(null);
		dfempedudurtnfrm.setComponentError(null);
		dfempedudurtnto.setComponentError(null);
		tfempeduscoredmark.setComponentError(null);
	}
	
	/*
	 * empdepnResetfields()-->this function is used for restore the selected row's data to Employee Dependant
	 */
	protected void empdepnResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the Employee Dependant UI controls");
		tfempdepndepname.setValue("");
		cbempdepnreltionship.setValue(null);
		dfempdepndob.setValue(null);
		cbempdepngender.setValue(null);
		btnaddempdepn.setCaption("add");
		cbempdepnstatus.setValue(cbempdepnstatus.getItemIds().iterator().next());
		tfempdepndepname.setComponentError(null);
		cbempdepnreltionship.setComponentError(null);
	}
	
	/*
	 * empidentitiesResetfields()-->this function is used for restore the selected row's data to Employee Identities
	 */
	protected void empidentitiesResetfields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting the EEmployee Identities UI controls");
		cbempidenname.setValue(null);
		tfempidenissuedby.setValue("");
		dfempidendate.setValue(null);
		tfempidenref.setValue("");
		cbempidenstatus.setValue(cbempidenstatus.getItemIds().iterator().next());
		cbempidenname.setComponentError(null);
		tfempidenref.setComponentError(null);
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for Employee. ID " + employeeid);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_EMPLOYEE);
		UI.getCurrent().getSession().setAttribute("audittablepk", isdCode);
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		// Remove Blur Listener for Search Layout
		for (Object rbl : tfEmployeeCode.getListeners(BlurEvent.class))
			tfEmployeeCode.removeBlurListener((BlurListener) rbl);
		tblMstScrSrchRslt.setPageLength(15);
		tfFirstName.setRequired(false);
		cbDepartment.setRequired(false);
		cbDepartment.setValue(0L);
		tfPhonenumber.setRequired(false);
		tblMstScrSrchRslt.setVisible(true);
		hlCmdBtnLayout.setVisible(true);
		vlSrchRsltContainer.setVisible(true);
		assembleSearchLayout();
		resetFields();
		empdtlsResetfields();
		empskillResetfields();
		empaddressResetFields();
		empcontactsResetfields();
		empimmgrtnResetfields();
		empbankResetfields();
		empeduResetfields();
		empdepnResetfields();
		empidentitiesResetfields();
		loadSrchRslt();
	}
	
	@Override
	protected void resetFields() {
		tfEmployeeCode.setReadOnly(false);
		tfEmployeeCode.setValue("");
		tfFirstName.setValue("");
		tfLastName.setValue("");
		tfPhonenumber.setValue("");
		tfEmailid.setValue("");
		dfDateofBirth.setValue(null);
		dfDateofJoin.setValue(null);
		cbBranch.setValue(null);
		cbDepartment.setValue(0L);
		cbManager.setValue(null);
		cbCountry.setValue(null);
		cbGender.setValue(null);
		cbCreateUser.setValue(false);
		cbEmpStatus.setValue(cbEmpStatus.getItemIds().iterator().next());
		tfEmployeeCode.setComponentError(null);
		tfFirstName.setComponentError(null);
		cbDepartment.setComponentError(null);
		tfLastName.setComponentError(null);
		dfDateofBirth.setComponentError(null);
		dfDateofJoin.setComponentError(null);
		tfEmailid.setComponentError(null);
		cbBranch.setComponentError(null);
		cbCountry.setComponentError(null);
		tfPhonenumber.setComponentError(null);
		tfISDCode.setReadOnly(false);
		tfISDCode.setValue("");
		tfISDCode.setReadOnly(true);
		new UploadUI(hlimage);
		employeedtlsList = new ArrayList<EmployeeDtlsDM>();
		employeeSkillList = new ArrayList<EmployeeSkillDM>();
		empaddresslist = new ArrayList<EmployeeAddressDM>();
		employeecontList = new ArrayList<EmployeeContactsDM>();
		employeeimmglist = new ArrayList<EmployeeImmgrtnDM>();
		employeebanklist = new ArrayList<EmployeeBankDM>();
		employeeedulist = new ArrayList<EmployeeEducationDM>();
		employeedepnlist = new ArrayList<EmployeeDependntsDM>();
		employeeidentitieslist = new ArrayList<EmployeeIdentitiesDM>();
		tblempdtls.removeAllItems();
		tblempskilltbl.removeAllItems();
		tblempaddress.removeAllItems();
		tblempcont.removeAllItems();
		tblempimmg.removeAllItems();
		tblempbank.removeAllItems();
		tblempedu.removeAllItems();
		tblempdepn.removeAllItems();
		tblempiden.removeAllItems();
		UI.getCurrent().getSession().setAttribute("isFileUploaded", false);
	}
	
	private void insertEmployeeGradeEarning(Long employeeid, Long gradeid) {
		List<GradeEarningsDM> list = serviceEarnings.loadGradeEarningListByGradeId(gradeid);
		BigDecimal minValueByBasic = new BigDecimal(serviceEarnings.getMinValueByBasic(gradeid));
		BigDecimal earningAmount;
		BigDecimal minVal = new BigDecimal("0");
		BigDecimal totalGrossAmt = new BigDecimal(serviceEarnings.getMinValueByBasic(gradeid));
		BigDecimal tatolGrossearnAmount = new BigDecimal(serviceEarnings.getMinValueByBasic(gradeid));
		EarningsDM grossEran = null;
		String isFlat = null;
		String isRemaining = null;
		EarningsDM earningid = null;
		BigDecimal earnBasiPercent = new BigDecimal(serviceEarnings.getBasicEarnPercent(gradeid));
		BigDecimal basicAmount = minValueByBasic.multiply(earnBasiPercent).divide(new BigDecimal("100"));
		for (GradeEarningsDM pojo : list) {
			Long earnid = pojo.getEarnId();
			isFlat = pojo.getIsFlatPer();
			BigDecimal earnPercent = pojo.getEarnPercent();
			minVal = pojo.getMinVal();
			String onBasicGross = pojo.getOnBasicGros();
			List<EarningsDM> earnlist = serviceEarnings.getEarningByEarnID(earnid);
			EarningsDM earnPojo = null;
			for (EarningsDM ernpojo : earnlist) {
				earnPojo = ernpojo;
				if (ernpojo.getEarnCode().equalsIgnoreCase("ALLOW")) {
					grossEran = ernpojo;
				}
			}
			System.out.println("isFlat--->" + isFlat + "\nonBasicGross--->" + onBasicGross);
			EmployeeEarningDM staffearnPojo = new EmployeeEarningDM();
			if (isFlat.equals("Percent") && onBasicGross.equalsIgnoreCase("GROSS")) {
				earningAmount = new BigDecimal(calculateEarnAmount(earnPercent, minValueByBasic));
				totalGrossAmt = totalGrossAmt.subtract(earningAmount);
				tatolGrossearnAmount = tatolGrossearnAmount.add(earningAmount);
				staffearnPojo.setEmployeeid(employeeid);
				staffearnPojo.setEarnid(earnPojo.getEarnId());
				staffearnPojo.setIsflatpercent(isFlat);
				staffearnPojo.setEarnamt(earningAmount);// Get the calculated amt based on BASIC code in earning and
														// grade earnings
				staffearnPojo.setEffdt(new Date());
				staffearnPojo.setEarnpercent(earnPercent);
				staffearnPojo.setEmpearnstatus("Active");
				staffearnPojo.setLastpdateddt(DateUtils.getcurrentdate());
				staffearnPojo.setLastupdatedby(username);
				serviceEmployeeEarning.saveAndUpdate(staffearnPojo);
			}
			if (isFlat.equals("Percent") && onBasicGross.equalsIgnoreCase("BASIC")) {
				earningAmount = new BigDecimal(calculateEarnAmount(earnPercent, basicAmount));
				totalGrossAmt = totalGrossAmt.subtract(earningAmount);
				tatolGrossearnAmount = tatolGrossearnAmount.add(earningAmount);
				staffearnPojo.setEmployeeid(employeeid);
				staffearnPojo.setEarnid(earnPojo.getEarnId());
				staffearnPojo.setIsflatpercent(isFlat);
				staffearnPojo.setEarnamt(earningAmount);// Get the calculated amt based on BASIC code in earning and
														// grade earnings
				staffearnPojo.setEffdt(new Date());
				staffearnPojo.setEarnpercent(earnPercent);
				staffearnPojo.setEmpearnstatus("Active");
				staffearnPojo.setLastpdateddt(DateUtils.getcurrentdate());
				staffearnPojo.setLastupdatedby(username);
				serviceEmployeeEarning.saveAndUpdate(staffearnPojo);
			} else if (isFlat.equals("Flat")) {
				if (!earnPojo.getEarnCode().equalsIgnoreCase("GROSS")) {
					totalGrossAmt = totalGrossAmt.subtract(minVal);
					tatolGrossearnAmount = tatolGrossearnAmount.add(minVal);
					staffearnPojo.setEmployeeid(headerID);
					staffearnPojo.setEarnid(earnPojo.getEarnId());
					staffearnPojo.setIsflatpercent(isFlat);
					staffearnPojo.setEarnamt(minVal);// Get the minimum value if flat is selected in the
					staffearnPojo.setEmployeeid(employeeid); // grade_earnings
					staffearnPojo.setEffdt(new Date());
					staffearnPojo.setEmpearnstatus("Active");
					staffearnPojo.setLastpdateddt(DateUtils.getcurrentdate());
					staffearnPojo.setLastupdatedby(username);
					serviceEmployeeEarning.saveAndUpdate(staffearnPojo);
				} else {
					staffearnPojo.setEmployeeid(headerID);
					staffearnPojo.setEarnid(earnPojo.getEarnId());
					staffearnPojo.setIsflatpercent(isFlat);
					staffearnPojo.setEarnamt(minVal);// Get the minimum value if flat is selected in the
					staffearnPojo.setEmployeeid(employeeid); // grade_earnings
					staffearnPojo.setEffdt(new Date());
					staffearnPojo.setEmpearnstatus("Active");
					staffearnPojo.setLastpdateddt(DateUtils.getcurrentdate());
					staffearnPojo.setLastupdatedby(username);
					serviceEmployeeEarning.saveAndUpdate(staffearnPojo);
				}
			} else if (isFlat.equals("REM")) {
				isRemaining = pojo.getIsFlatPer();
				Long earnsid = pojo.getEarnId();
				BigDecimal gradeAmount = pojo.getMinVal();
				List<EarningsDM> earninglist = serviceEarnings.getEarningByEarnID(earnsid);
				for (EarningsDM earnObj : earninglist) {
					earningid = earnObj;
				}
				tatolGrossearnAmount = tatolGrossearnAmount.add(gradeAmount);
			}
		}
	}
	
	private void insertEmployeeDeduction(Long employeeid, Long gradeid) {
		List<GradeDeductionDM> list = serviceDeduction.getGradeDeductionByGradeId(gradeid);
		Long minValueByBasic = null;
		Long minValueByGross = null;
		Long earnpercent = serviceDeduction.getMinValueByGradeid(gradeid);
		minValueByGross = serviceDeduction.getMinValueByGradeidOnGross(gradeid);
		String dednCode = "";
		minValueByBasic = Long.valueOf(Math.round(minValueByGross * earnpercent) / 100);
		for (GradeDeductionDM pojo : list) {
			Long dednid = pojo.getDednId();
			String isFlat = pojo.getIsFlatPer();
			BigDecimal dednpercent = pojo.getDednPercent();
			BigDecimal minVal = pojo.getMinVal();
			String onBasic_Gross = pojo.getOnBasicGros();
			System.out.println("Deuction on basic gross" + onBasic_Gross);
			List<DeductionDM> dednlist = serviceDeduction.getDeductionListByDednId(dednid);
			DeductionDM deductionPojo = null;
			for (DeductionDM dednpojo : dednlist) {
				deductionPojo = dednpojo;
				dednCode = dednpojo.getDeductionCode();
			}
			// Save the T_Staff_Deduction
			EmployeeDeductionDM staffDednPojo = new EmployeeDeductionDM();
			System.out.println("Deuction on basic gross and is flat =" + onBasic_Gross + " and " + isFlat);
			System.out.println();
			if (isFlat.equalsIgnoreCase("Percent") && onBasic_Gross.equalsIgnoreCase("GROSS")) {
				BigDecimal deductionAmt = new BigDecimal(calculateDeductionAmount(dednpercent, minValueByGross));
				staffDednPojo.setEmployeeid(headerID);
				staffDednPojo.setDednid(deductionPojo.getDeductionId());
				staffDednPojo.setIsflatpt(isFlat);
				staffDednPojo.setDednpt(dednpercent);
				staffDednPojo.setDednamt(deductionAmt);// Get the percentage and calculate with gross value from
				staffDednPojo.setEmployeeid(employeeid); // grade earnings
				staffDednPojo.setEffdt(new Date());
				staffDednPojo.setEmpdednstatus("Active");
				staffDednPojo.setLastupdateddt(DateUtils.getcurrentdate());
				staffDednPojo.setLastupdatedby(username);
				serviceEmployeeDeduction.saveAndUpdate(staffDednPojo);
			} else if (isFlat.equalsIgnoreCase("Percent") && onBasic_Gross.equalsIgnoreCase("BASIC")) {
				BigDecimal deductionAmt = new BigDecimal(calculateDeductionAmount(dednpercent, minValueByBasic));
				staffDednPojo.setEmployeeid(headerID);
				staffDednPojo.setDednid(deductionPojo.getDeductionId());
				staffDednPojo.setIsflatpt(isFlat);
				staffDednPojo.setDednpt(dednpercent);
				staffDednPojo.setDednamt(deductionAmt);// Get the percentage and calculate with basic value from
				staffDednPojo.setEmployeeid(employeeid); // grade earnings
				staffDednPojo.setEffdt(new Date());
				staffDednPojo.setEmpdednstatus("Active");
				staffDednPojo.setLastupdateddt(DateUtils.getcurrentdate());
				staffDednPojo.setLastupdatedby(username);
				serviceEmployeeDeduction.saveAndUpdate(staffDednPojo);
			} else {
				staffDednPojo.setEmployeeid(headerID);
				staffDednPojo.setDednid(deductionPojo.getDeductionId());
				staffDednPojo.setIsflatpt(isFlat);
				staffDednPojo.setDednpt(minVal);// Get the minimum value from grade deduction
				staffDednPojo.setEffdt(new Date());
				staffDednPojo.setEmpdednstatus("Active");
				staffDednPojo.setLastupdateddt(DateUtils.getcurrentdate());
				staffDednPojo.setLastupdatedby(username);
				staffDednPojo.setEmployeeid(employeeid);
				serviceEmployeeDeduction.saveAndUpdate(staffDednPojo);
			}
		}
	}
	
	private void insertGradeAllowance(Long employeeid, Long gradeid) {
		String medicalAmount = "";
		List<ParameterDM> paramList = serviceEmployeeEarning.loadMedicalAllowanceDuration();
		for (ParameterDM paramObj : paramList) {
			medicalAmount = paramObj.getParamValue();
		}
		System.out.println("Medical Allowance Duration Amount" + medicalAmount);
		BigDecimal medicalDuration = new BigDecimal(medicalAmount.toString());
		List<GradeAllowanceDM> list = serviceGradeAllowance.getGradeAllowanceByGradeID(gradeid);
		BigDecimal minValueByBasic = null;
		BigDecimal minValueByGross = null;
		String allownceCode = "";
		minValueByGross = new BigDecimal(serviceDeduction.getMinValueByGradeidOnGross(gradeid));
		BigDecimal earnpercent = new BigDecimal(serviceDeduction.getMinValueByGradeid(gradeid));
		minValueByBasic = (earnpercent.multiply(minValueByGross)).divide(new BigDecimal("100"));
		System.out.println("BAsic Value for Calculate Based On Gross" + minValueByBasic);
		yearGrossAmt = minValueByGross.multiply(new BigDecimal("12"));
		for (GradeAllowanceDM pojo : list) {
			Long allowanceid = pojo.getAlwnceId();
			String isFlat = pojo.getIsFlatPer();
			String onBasicOrGross = pojo.getOnBasicGros();
			BigDecimal minPercent = null;
			if (pojo.getMinPer() != null) {
				minPercent = new BigDecimal(pojo.getMinPer().longValue());
			}
			BigDecimal minValue = null;
			if (pojo.getMinVal() != null) {
				minValue = new BigDecimal(pojo.getMinVal().toString());
			}
			List<AllowanceDM> allownList = serviceEmpAllowance.getAllowanceByAllowanceId(allowanceid);
			AllowanceDM allowanceObj = null;
			for (AllowanceDM alloPojo : allownList) {
				allowanceObj = alloPojo;
				allownceCode = alloPojo.getAlowncCode();
			}
			// Store the grade allowance to staff allowance table
			EmployeeAllowanceDM staffAllwnPojo = new EmployeeAllowanceDM();
			if (isFlat.equalsIgnoreCase("Percent") && onBasicOrGross.equalsIgnoreCase("GROSS")) {
				BigDecimal allownAmt = new BigDecimal(calculateEarnAmount(minPercent, minValueByGross));
				staffAllwnPojo.setAllowid(allowanceObj.getAlowncId());
				staffAllwnPojo.setIsflpt(isFlat);
				staffAllwnPojo.setAllowpt(minPercent);
				staffAllwnPojo.setAllowamt(allownAmt);// Get the calculated value based on 'BASIC' if percent is
				staffAllwnPojo.setEmpid(employeeid); // selected in the grade_allowance
				staffAllwnPojo.setAllowbal(allownAmt);
				staffAllwnPojo.setEffdt(new Date());
				staffAllwnPojo.setAutopay("N");
				staffAllwnPojo.setEmpawstatus("Active");
				staffAllwnPojo.setLastupdt(DateUtils.getcurrentdate());
				staffAllwnPojo.setLastupby(username);
				serviceEmpAllowance.saveAndUpdate(staffAllwnPojo);
			} else if (isFlat.equalsIgnoreCase("Percent") && onBasicOrGross.equalsIgnoreCase("BASIC")) {
				BigDecimal allownAmt = new BigDecimal(calculateEarnAmount(minPercent, minValueByBasic));
				staffAllwnPojo.setEmpid(employeeid);
				staffAllwnPojo.setAllowid(allowanceObj.getAlowncId());
				staffAllwnPojo.setIsflpt(isFlat);
				staffAllwnPojo.setAllowpt(minPercent);
				staffAllwnPojo.setAllowamt(allownAmt);// Get the calculated value based on 'BASIC' if percent is
														// selected in the grade_allowance
				staffAllwnPojo.setAllowbal(allownAmt);
				staffAllwnPojo.setEffdt(new Date());
				staffAllwnPojo.setAutopay("N");
				staffAllwnPojo.setEmpawstatus("Active");
				staffAllwnPojo.setLastupdt(DateUtils.getcurrentdate());
				staffAllwnPojo.setLastupby(username);
				serviceEmpAllowance.saveAndUpdate(staffAllwnPojo);
			} else {
				staffAllwnPojo.setEmpid(employeeid);
				staffAllwnPojo.setAllowid(allowanceObj.getAlowncId());
				staffAllwnPojo.setIsflpt(isFlat);
				staffAllwnPojo.setAllowamt(minValue);// Get the minimum value if flat is selected in the
														// grade_allowance
				staffAllwnPojo.setAllowbal(minValue);
				staffAllwnPojo.setEffdt(new Date());
				staffAllwnPojo.setEmpawstatus("Active");
				staffAllwnPojo.setAutopay("N");
				staffAllwnPojo.setLastupdt(DateUtils.getcurrentdate());
				staffAllwnPojo.setLastupby(username);
				if (allownceCode.equalsIgnoreCase("MEDA") && (yearGrossAmt.compareTo(medicalDuration)) == 1) {
					serviceEmpAllowance.saveAndUpdate(staffAllwnPojo);
				}
				if (!allownceCode.equalsIgnoreCase("MEDA")) {
					serviceEmpAllowance.saveAndUpdate(staffAllwnPojo);
				}
			}
		}
	}
	
	private Double calculateEarnAmount(BigDecimal earnPercent, BigDecimal minValuefrmBasic) {
		System.out.println("Earn Amount On Gross" + minValuefrmBasic);
		System.out.println("Earn Amount For Percent" + earnPercent);
		try {
			Double calEarnAmt = (double) ((minValuefrmBasic.doubleValue() * earnPercent.doubleValue()) / 100);
			return calEarnAmt;
		}
		catch (NullPointerException e) {
			return 0D;
		}
	}
	
	private Double calculateDeductionAmount(BigDecimal earnPercent, Long minValuefrmBasic) {
		System.out.println("Earn Amount On Gross" + minValuefrmBasic);
		System.out.println("Earn Amount For Percent" + earnPercent);
		try {
			Double calEarnAmt = (double) ((minValuefrmBasic * earnPercent.doubleValue()) / 100);
			return calEarnAmt;
		}
		catch (NullPointerException e) {
			return 0D;
		}
	}
	
	private Date addDays(Date returndate, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String strDate = sdf.format(returndate);
		Date parsedDate = null;
		try {
			parsedDate = sdf.parse(strDate);
		}
		catch (Exception e) {
			logger.warn("calculate days" + e);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, days);
		return now.getTime();
	}
	
	@SuppressWarnings("unused")
	private Double calculateEarnAmount(Long earnPercent, Long minValuefrmBasic) {
		System.out.println("Earn Amount On Gross" + minValuefrmBasic);
		System.out.println("Earn Amount For Percent" + earnPercent);
		try {
			Double calEarnAmt = (double) ((minValuefrmBasic * earnPercent) / 100);
			return calEarnAmt;
		}
		catch (NullPointerException e) {
			return 0D;
		}
	}
}
