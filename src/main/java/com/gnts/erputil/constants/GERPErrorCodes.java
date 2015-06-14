/**
 * File Name	:	GERPErrorCodes.java
 * Description	:	To Handle components error message.
 * Author		:	SOUNDER C
 * Date			:	16-Jun-2014	
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By            Remarks
 * 0.1			 16-Jun-2014	  Nandhkumar.S			 Base version
 * 0.2           17-jun-2014      Ganga.S                Code re-factoring&logging
 *
 */
package com.gnts.erputil.constants;

import com.vaadin.server.UserError;

public class GERPErrorCodes {
	// Common
	public static String DATE_OUTOFRANGE = "End Date should be greater than Start Date";
	public static String SMS_DATE_OUTOFRANGE = "Valid Date should be greater than Quote Date";
	public static String MMS_DATE_OUTOFRANGE = "Due Date should be greater than Enquiry Date";
	public static String CRM_DATE_OUTOFRANGE = "Due Date should be greater than Schedule Date";
	public static String EMAIL_VALIDATION = "Enter valid e-mail Id";
	public static String PHONE_NUMBER_VALIDATION = "Enter valid phone no.";
	public static String NULL_PHONE_NUMBER = "Enter phone number";
	public static String DATE_OF_BIRTH_VALIDATION = "Date of birth should be less than current date";
	public static String DATE_OF_JOIN_VALIDATION = "Date of join should be greater than Date of birth";
	public static String DATE_FROM = "Date from should be lesser than Date To";
	public static String DATE_TO = "Date to should be greater than Date from";
	public static String START_DATE = "Give Start Date";
	public static String END_DATE = "Give End Date";
	public static String CB_VALUES = "This field should not be null";
	public static String SELECT_DATE = "Please select date";
	
	// Org.News
	public static String NULL_ORG_NEWS = "please enter News";
	public static String DATE_VLIDTN = "Start date should be greater than end Date";
	public static String DATE_NOT_NULL = "Please enter start date";
	public static String DATE_NOT_NULL_END = "Please enter end date";
	// Org.News
	public static String NULL_SLNO_GEN = "please Serial number generation ";
	// Department
	public static String NULL_DEPT_NAME = "Please enter Dept. Name";
	// Asset Brand
	public static String NULL_ASST_BRAND_NAME = "Please enter Brand Name";
	// Asset Category
	public static String NULL_ASST_CATGRY_NAME = "Please enter Category Name";
	public static String NULL_ASSET_NAME = "Please Enter  Name";
	public static String NULL_ASSET_MAINTENANCE = "Select Maintenance Description";
	// Asset Details
	public static String NULL_ASSET_TYPE = "Enter Asset Type";
	public static String NULL_ASSET_LOCATION = "Enter Asset Location";
	// Asset Maintain Schedule
	public static String NULL_ASST_FREQUENCY = " Maintenance Frequency ";
	public static String FREQUENCY_PER_DAY = " Per Day Frequency ";
	// Asset Details
	public static String ASSET_OWNER = "Enter OwnerShip Description";
	// Client Category
	public static String NULL_CLNT_CATGRY_NAME = "Please enter Client Category Name";
	// CLient SubCategory
	public static String NULL_CLNT_SCATGRY_NAME = "Please enter Client Subcategory Name";
	// Client
	public static String NULL_CLNT_NAME = "Please enter Client  Name";
	public static String NULL_CLNT_CODE = "Please enter Client  Code";

	// Client
	public static String NULL_CASE_TITLE = "Please enter ClientCase Title";
	public static String NULL_CASE_Category = "select casecategory ";
	// Client Contact
	public static String NULL_CLNTCONTACT_NAME = "Enter ContactPerson Name";
	// Campaign
	public static String NULL_CAMPAIGN_NAME = "Please enter Campaign Name";
	public static String NULL_CAMPAIGN_TYPE = "Select Campaign Type";
	public static String NULL_CAMPAIGN_DATE = "Select Campaign Date";
	// Leads
	public static String NULL_LEAD_NAME = "Please enter Lead  Name";
	public static String EMPLOYEE_ADDRESS = "Enter employee address";
	// opportunity
	public static String NULL_CLNT_OPPORTUNITY_NAME = "please enter opportunity Name";
	public static String NULL_OPPORTUNITY_TYPE = "select oppertunity type";
	public static String NULL_OBJECTIVE = "Give some inputs";
	public static String NULL_CLOSE_DATE = "Please enter close date";
	// Client appointement
	public static String DUEDATE_OF_VALIDATION = "Due Date should be greater than Schedule date";
	// Role
	public static String NULL_ADMIN_SYS_ROLE_NAME = "Please enter Role Name";
	// Holiday
	public static String NULL_HOLIDAY_NAME = "Please enter Holiday Name";
	public static String NULL_HOLIDAY_DATE = "Please enter Holiday Date";
	// City
	public static String NULL_CITY_NAME = "Please enter City Name";
	// State
	public static String NULL_STATE_NAME = "Please enter State Name";
	public static String NULL_STATE_NAMECB = "Please select State Name";
	// Bank
	public static String NULL_BANK_NAME = "Please enter Bank Name";
	public static String NULL_BANK_IFSC_CODE = "Please enter IFSC Code";
	public static String NULL_BANK_MICR_CODE = "Please enter MICR Code";
	// PNC
	public static String NULL_PNC_CODE = "Please enter PNC Code";
	// Company Lookup
	public static String NULL_COMPANY_LOOKUP = "Please enter lookup Name";
	public static String NULL_COMPANY_LOOKUP_LOOKUP_CODE = "Please select lookup code";
	public static String NULL_COMPANY_LOOKUP_MODULE_CODE = "Please select module code";
	// Company
	public static String NULL_COMPANY_COUNTRY = "Please select Country Name";
	public static String NULL_COMPANY_STATE = "Please select State Name";
	public static String NULL_COMPANY_CITY = "Please select City Name";
	public static String NULL_COMPANY_CURRENCY = "Please select Currency Name";
	// Region
	public static String NULL_REGION_NAME = "Please enter Region Name";
	// Employee
	public static String NULL_EMPLOYEE_CODE = "Please enter Employee Code";
	public static String NULL_EMPLOYEE_FIRST_NAME = "Please enter Employee First Name";
	public static String NULL_EMPLOYEE_LAST_NAME = "Please enter Employee Last Name";
	public static String NULL_EMPLOYEE_DOB = "Please enter Date of Birth";
	public static String NULL_EMPLOYEE_DOJ = "Please enter Date of Join";
	public static String NULL_EMPLOYEE_BRANCH = "Please select branch";
	public static String NULL_EMPLOYEE_COUNTRY = "Please select country";
	public static String EMPLOYEE_CODE_VALIDATION = "Employee code already exist";
	// Product Category
	public static String NULL_PRODUCT_CATEGORY = "Please enter Product Category";
	// Product
	public static String NULL_PRODUCT_NAME = "Please select product name";
	public static String NULL_PRODUC_CODE = "Please enter Code";
	public static String NULL_PRODUC_CATEGORY = "Please select  Product Category";
	// Parameter
	public static String NULL_PARAMETER_VALUE = "Please enter Parameter Value ";
	// VendorType
	public static String NULL_VENDORTYPE_NAME = "Please enter VendorType Name ";
	public static String NULL_BRANCH_NAME = "Please select Branch Name ";
	public static String NULL_VENDOR_NAME = "Please enter Vendor Name ";
	// FMS
	// Account type
	public static String NULL_FMS_PROJECTNAME = "Please select project name";
	public static String NULL_FMS_INVOICENO = "Please enter invoice no";
	public static String NULL_VOUCHERNO = "Please enter voucherno";
	public static String NULL_FMS_ACTIONEDBY = "Please select actionedby";
	public static String NULL_FMS_ACCOUNT_TYPE = "Please enter account type";
	public static String NULL_FMS_INVOICEAMT = "Please enter Invoice amt";
	public static String NULL_FMS_PAIDAMT = "Please enter paid amount";
	public static String NULL_FMS_ACCOUNT_REF = "Please select account Reference";
	public static String NULL_FMS_TRANSAMT = "Please enter transaction amt";
	public static String NULL_FMS_TRANSACTIONS_TYPE = "Please select transaction type";
	public static String NULL_FPCATEGORY = "Please select fp category";
	public static String NULL_OWNERNAME = "Please select owner name";
	public static String NULL_FPDESCRIPTION = "Please select fp description";
	// Transaction Type
	public static String NULL_FMS_TRANS_TYPE = "Please enter type";
	public static String NULL_FMS_CREDIT_DEBIT = "Please choose credit or debit";
	// PNC Dept. Map
	public static String NULL_FMS_PNC_DEPT_MAP = "Please choose PNC Center Code ";
	// Accounts
	public static String NULL_FMS_ACCOUNTS_NUMBER = "Please Enter Account Number";
	public static String NULL_FMS_ACCOUNTS_NAME = "Please Enter Account Name";
	// HCM
	public static String NULL_BUSINESSVALUE = "Please Enter Number";
	public static String NULL_LEAVE_TYPE = "Please enter Leave Type";
	public static String NULL_PAY_PERIOD = "Please enter Pay Period Name";
	public static String NULL_LEAVE_TYPE_SYMBOL = "Please enter Symbol";
	public static String NULL_QUALIFICATION = "Please enter Qualification";
	public static String NULL_EMPLOYMENTTYPE = "Please enter Employment Type";
	public static String NULL_SHIFT = "Please enter Shift Type";
	public static String NULL_JOBCLASSIFICATION = "Please enter Job Classification Name";
	public static String NULL_GRADE = "Please enter Grade Desc";
	public static String NULL_DESIGNATION = "Please enter Designation";
	public static String NULL_TECH_PERSON = "Please enter Technical person Name";
	public static String NULL_COMMERCIAL_PERSON = "Please enter Commercial person Name";
	public static String NULL_PAYPERIOD_STDAY = "Please enter Start Day";
	public static String NULL_PAYPERIOD_EDDAY = "Please enter End Day";
	public static String NULL_EARNING_DESC = "Please enter EarnDescription";
	public static String NULL_GRD_NAME = "Please enter Grade Name";
	public static String NULL_EARN_NAME = "Please enter Earn Name";
	public static String NULL_DEDUCTION_NAME = "Please enter Deduction Name";
	public static String NULL_TAX = "Please enter Tax Name";
	public static String NULL_ALLOWANCE_NAME = "Please enter Allowance Name";
	public static String NULL_GRD_ALLOWANCE = "Please enter Grade Allowance Name";
	public static String NULL_FLAT_PERCENTAGE = "Choose Flat/Percent";
	public static String NULL_ONBASIC_GROSS = "Choose Basic/Gross";
	public static String NULL_PAYBASIC = "Choose PayBasic";
	public static String NULL_TAX_LIMIT = "Please enter Tax Limit Name";
	public static String NULL_SECTION_CODE = "Please enter Section code";
	public static String NULL_TAX_NAME = "Please enter Tax Name";
	public static String NULL_AMTFRM_AMTTO = "Earn amount from is smaller than Earn amount To";
	public static String NULL_EMP_STS_CODE = "Please enter Employment Status Code";
	public static String NULL_EMP_STS_DESC = "Please enter Employment Status Description";
	public static String NULL_JOB_VNCY_TITLE = "Please select Job Title";
	public static String NULL_JOB_VNCY_CLSFN = "Please select Classification Name";
	public static String NULL_TAX_SETION_LIMIT = "Please enter Section Limit as number only";
	public static String NULL_TAX_SECTION_TAX = "Please enter Tax Limit as number only";
	public static String DATE_OF_FROM_VALIDATION = "From date should be less than current date";
	public static String DATE_OF_UNTIL_VALIDATION = "Until Date should be greater than from date";
	public static String NULL_EMPLOYEE_DUNTIL = " enter Until Date";
	public static String NULL_EMPLOYEE_DFROM = " enter From Date";
	public static String NULL_JOB_INTRVW = "Please  select Candidate Name";
	public static String NULL_FIN_YEAR = "please enter fin year";
	public static String NULL_MEETING_TYPE = "Select Meeting Type";
	public static String NULL_MEETING_SCHEDULE_DT = "Please select Meeting schedule date";
	public static String NULL_MEETING_STATUS = "Select Meeting Status";
	public static String NULL_EMP_SHIFT = "Please enter EmployeeName";
	public static String NULL_JOB_CANDIDATE = "Please choose JobTitle";
	public static String NULL_PAYROLL_HDR = "Please enter remarks";
	public static String NULL_KPI_GROUP_NAME = "Please Enter the Group Name";
	public static String NULL_KPI_GRP_WEIGHT = "Please Enter weightage Number";
	public static String NULL_KPI_NAME = "Please Enter KPI Name";
	public static String NULL_KPI_MIN_VALUE = "Minimum Rating Should be lesser than Maximum Rating";
	public static String NULL_KPI_MAX_VALUE = " Maximum Rating Should be grater than Minimum Rating";
	public static String NUMBER = "Enter value should be a number";
	public static String NULL_EMPLOYEE_NAME = "Please choose Employee Name";
	public static String NULL_ONDUTY_NOOFDAYS = "Please Enter the total no.of days";
	public static String NULL_ONDUTY_TOTALHOURS = "Please Enter total hours";
	public static String NULL_ONDUTY_MANAGER = "Please Choose Approve manager";
	public static String NULL_ONDUTY_DATEFRM = "Please Choose Onduty Date From";
	public static String NULL_ONDUTY_DATETO = "Please Choose Onduty Date To";
	public static String NULL_PERMIS_PRMDATE = "Please Enter Date";
	public static String NULL_PERMIS_INTIM = "Set Intime";
	public static String NULL_PERMIS_TOTHRS = "Please Enter permission Hours";
	public static String NULL_PERMIS_APPMGR = "Please Choose Approve manager";
	public static String NULL_OTIME_OTDATE = "Please Enter Date";
	public static String NULL_OTIME_STRHOUR = "Please Enter start hours";
	public static String NULL_OTIME_EDHOUR = "Please Enter End hours";
	public static String NULL_OTIME_TOTHOUR = "Please Enter overtime hours";
	public static String NULL_OTIME_APPMGR = "Please Choose Approve manager";
	public static String NULL_ABST_ABDATE = "Please Enter Date";
	public static String NULL_ABST_STRHOUR = "Please Enter start hours";
	public static String NULL_ABST_EDHOUR = "Please Enter End hours";
	public static String NULL_ABST_TOTHOUR = "Please Enter Absent hours";
	public static String NULL_LATE_LTDATE = "Please Enter Date";
	public static String NULL_LATE_LTINTIME = "Set Intime";
	public static String NULL_LATE_LTHOUR = "Please Enter Late hours";
	public static String NULL_ABST_LTHOUR = "Please Enter Late hours";
	public static String NULL_LVE_EMPNAME = "Please Choose Employee Name";
	public static String NULL_LVE_DATEFRM = "Please Choose Leave Date From";
	public static String NULL_LVE_DATETO = "Please Choose Onduty Date To";
	public static String NULL_LVE_NOOFDAYS = "Please Enter the total no.of days";
	public static String NULL_LVE_MANAGER = "Please Choose Approve manager";
	public static String NULL_LVE_LVETYPE = "Please Choose Leavetype";
	public static String NULL_LEVEL_NAME = "Please enter level name";
	public static String NULL_APPRAISAL_LEVEL = "Please select appraisal level";
	public static String NULL_EMPLOYEE_DTLS_CANDIDATE_NAME = "Please select Candidate Name";
	public static String NULL_EMPLOYEE_DTLS_TYPE = "Please select Employee Type";
	public static String NULL_EMPLOYEE_DTLS_Grade = "Please select Employee Grade";
	public static String NULL_EMPLOYEE_DTLS_Designation = "Please select Employee Designation";
	public static String NULL_EMPLOYEE_DTLS_PayPeriod = "Please select Employee Pay Period";
	public static String NULL_EMPLOYEE_DTLS_MARTIAL = "Please select Martial Status";
	public static String NULL_EMPLOYEE_DTLS_BIRTHCOUNTRY = "Please select Birth Country";
	public static String NULL_EMPLOYEE_DTLS_Nationality = "Please select Employee Nationality";
	public static String NULL_EMPLOYEE_DTLS_CONFORMATIONDATE = "Conformation date should be less than Last Promotion Date";
	public static String NULL_EMPLOYEE_DTLS_LASTPROMOTIONDATE = "Last Promotion Date should be greater than Conformation date";
	public static String NULL_EMPLOYEE_DTLS_RELEAVEDDATE = "Releaved Date should be greater than Last Promotion date";
	public static String NULL_EMPLOYEE_DTLS_DEATHDATE = "Death Date should be greater than Releaved Date";
	public static String NULL_EMPLOYEE_SKill_Skill = "Please select Employee Skill";
	public static String NULL_EMPLOYEE_ADDRESS_ADDTYPE = "Please Select  Address Type";
	public static String NULL_EMPLOYEE_ADDRESS_LINE1 = "Please enter  address line1";
	public static String NULL_EMPLOYEE_ADDRESS_LINE2 = "Please enter  address line2";
	public static String NULL_EMPLOYEE_ADDRESS_CITY = "Please enter  City Name";
	public static String NULL_EMPLOYEE_ADDRESS_STATENAME = "Please enter State Name";
	public static String NULL_EMPLOYEE_ADDRESS_POSTCODE = "Please enter Postcode ";
	public static String NULL_EMPLOYEE_ADDRESS_COUNTRY = "Please Select Country Name ";
	public static String NULL_EMPLOYEE_CONTACTS_NAME = "Please enter Contact Name";
	public static String NULL_EMPLOYEE_CONTACTS_PHONENUMBER = "please enter a valid  phone number";
	public static String NULL_EMPLOYEE_IMMIGRATION_DOCTYPE = "Please Select DOC Type ";
	public static String NULL_EMPLOYEE_IMMIGRATION_DOCNO = "Please enter DOC No";
	public static String NULL_EMPLOYEE_IMMIGRATION_IMMGISSUDATE = "Issue Date should be less than Expiry date";
	public static String NULL_EMPLOYEE_IMMIGRATION_IMMGEXPDATE = "Expiry Date should be greater than Issue date";
	public static String NULL_EMPLOYEE_BANK_NAME = "Please enter Bank Name";
	public static String NULL_EMPLOYEE_BANK_Accno = "Please enter Account NO";
	public static String NULL_EMPLOYEE_BANK_BRANCHNAME = "Please enter Branch Name";
	public static String NULL_EMPLOYEE_BANK_ACCOUNTTYPE = "Please Select Account Type";
	public static String NULL_EMPLOYEE_BANK_ROUTINGCODE = "Please enter Routing Code";
	public static String NULL_EMPLOYEE_Education_Qualification = "Please select Qualification";
	public static String NULL_EMPLOYEE_Education_GRADE = "Please select Grade";
	public static String NULL_EMPLOYEE_Education_FROMDATE = "From Date should be less than Duration To date";
	public static String NULL_EMPLOYEE_Education_TODATE = "To Date should be greater than Duration From date";
	public static String NULL_EMPLOYEE_Education_ = "To Date should be greater than Duration From date";
	public static String NULL_EMPLOYEE_CONTACTS_MOBILENUMBER = "please enter a valid 10 digit mobile number";
	public static String NULL_ALLOWANCE_CLAIM_DATE = "Please select allowance claim date";
	public static String NULL_EMPLOYEE_Education_SCOREDMARK = "Please enter Scoredmark";
	public static String NULL_EMPLOYEE_DEPENDANT_NAME = "Please enter Dependant Name";
	public static String NULL_EMPLOYEE_DEPENDANT_RELATIONSHIP = "Please enter Relationship ";
	public static String NULL_EMPLOYEE_Identity = "Please  select ID Name";
	public static String NULL_EMPLOYEE_Refferenceid = "Please enter Refference ID";
	public static String NULL_INCOME_SALARY = "Please Enter the Income  Amount ";
	public static String NULL_APPROVED_AMOUNT = "Please Enter the Approved Amount ";
	public static String NULL_HRA_AMOUNT = "Please Enter the HRA Amount ";
	public static String NULL_INCOME_SALARY_DESC = "Please Enter the Income Salary Description";
	public static String NULL_EMP_EARNCOD = "Please choose Earn Code";
	public static String NULL_EMP_FLATPER = "Please choose Flat/Percent";
	public static String NULL_EMP_DEDCTNCOD = "Please choose Deduction Code";
	public static String NULL_EMPL_SHIFT = "Please choose Shift Type";
	public static String NULL_JOB_VANCY_TITLE = "Please select Job Title";
	public static String NULL_INCOME_AMOUNT_COMPARE = "Approved Amount should be greater then Income Amount";
	public static String NULL_APPROVED_AMOUNT_COMPARE = "Income Amount should be lesser then Approved Amount";
	public static String NULL_HRA_AMT = "HRA Amount should be lesser then Approved Amount";
	public static String NULL_HRA_APPROVED_AMOUNT = "Approved Amount should be greater then HRA Amount";
	public static String NULL_ORG_POLICIES_NAME = "Please enter policy name";
	public static String NULL_ORG_POLICIES_GROUP = "Please select policy group";
	public static String NULL_FLAT_PERCENT = "Choose Flat/Percent";
	public static String NULL_ALWNCE_NAME = "Please choose Allowance Name";
	public static String NULL_PAID_DATE = "Please choose Paid Date";
	public static String NULL_APPROVE_MANAGER = "Please Choose Approve Manager Name";
	public static String NULL_EFF_DATE = "Please Choose Effective Date";
	public static String NULL_EFF_DED_DATE = "Please Choose Deduction Start Date";
	public static String NULL_DEDCTION_NAME = "Please choose Deduction Name";
	public static String NULL_CLAIM_AMT = "Please enter claim amount";
	public static String NULL_Appraisal_Year = "Enter the year";
	public static String NULL_CLOSED = "Please choose closed Date";
	public static String NULL_SIGNOFF = "Please choose Sign Off Date";
	public static String NULL_Appraisal_Date = "Enter the date";
	public static String NULL_INCIDENT_TYPE = "Please Select Incident Type";
	public static String NULL_INCIDENT_SEVERITY = "Please Select Inident Severity";
	public static String NULL_BUSINESS_VALUE = "Please Enter Business Value";
	public static String NULL_EMP_AGREED = "Please Enter Employee Agreed";
	public static String NULL_EMP_RESPONSE = "Please Enter Employee Response";
	public static String NULL_INCIDENT_TITLE = "Please Enter Incident Title";
	public static String NULL_RECOREDBY = "Please Enter RecordedBy";
	public static String NULL_REVIEWEDBY = "Please Enter ReviewedBy";
	public static String NULL_APP_EMP_INCIDENT_STATUS = "Please Select Appraisal Employee Incident Status";
	public static String NULL_KPIGROUPNAME = "Please Select KPI GROUP NAME";
	public static String NULL_APPRAISAL_NAME = "Please Select APPRAISAL NAME ";
	public static String NULL_SKILL_NAME = "Please enter Skill Name ";
	public static String NULL_EARNING_CODE = "Please enter Earn Code";
	public static String NULL_BLOCK_PERIOD = "Please enter Block Period";
	public static String NULL_PROXIMITY_NO = "Please enter Proximity No";
	public static String NULL_INVEST_DECL = "Please enter Investment Decl.";
	public static String NULL_HIRING_MNGR = "Please choose Hiring Manager.";
	public static String NULL_REQUESTED_NAME = "Please choose Requested Name.";
	public static String NULL_APPROVED_STATUS = "Please choose Approved Status.";
	public static String NULL_APPROVED_NAME = "Please choose Approved Nmae.";
	public static String NULL_JOB_STATUS = "Please choose Job Status.";
	public static String JOB_CLASSIFICATOIN = "Please choose job classification";
	public static String NULL_DEDUCTION_CODE = "Please enter Deduction Code";
	// MFG
	// Test Group
	public static String NULL_TEST_GROUP_NAME = "Please enter test group";
	public static String EXIST_TEST_GROUP_NAME = "The test group name is already exist, pls enter new group";

	// QATest Type
	public static String NULL_TEST_TYPE = "Please enter test type";
	public static String NULL_TEST_GROUP = "Please select test group";
	public static String NULL_TEST_METHODOLOGY = "Please enter the methodology";
	public static String NULL_TEST_PRODUCT_NAME = "Please select the products";
	public static String NULL_CATGRY_NAME = "Please select the category name";
	public static String NULL_TEST_RECRMNT = "Please enter the test recruitment";
	public static String NULL_TEST_SPEC = "Please enter the test Specification";
	public static String NULL_TEST_CYC = "Please enter the test cycles";
	public static String NULL_TEST_CONDN = "Please enter the test condition";
	public static String NULL_TEST_CONDN_SPEC = "Please enter the test condition specification";
	public static String NULL_INTERVIEWER_ID = "Please choose Interviewer Name";
	public static String NULL_INTERVIEWER_LEVEL = "Please choose Interviewer Level";
	// QCTest Type
	public static String NULL_QC_TSTTYPE = "Please enter test type";
	public static String NULL_QC_TSTMETHDGLY = "Please enter test methodology ";
	public static String NULL_QC_MTRLTYPE = "Please select the materialType";
	public static String NULL_QC_MTRLNAME = "Please Select the Material";
	public static String NULL_QC_TEST_RECRMNT = "Please enter the test recruitment";
	public static String NULL_QC_TEST_SPEC = "Please enter the test Specification";
	public static String NULL_QC_TEST_CYC = "Please enter the test cycles ";
	public static String NULL_QC_TEST_CYC_NO = "Enter the test cycles by number only";
	public static String NULL_QC_PRDNAME = "Select product";
	public static String NULL_QC_WRKORDQTY = "Enter number only";
	// QC Test
	public static String NULL_QC_MATERIAL = "Select material";
	public static String NULL_QC_BRANCH = "Select branch";
	public static String NULL_INSP_DATE = "Enter inspection date";
	public static String NULL_QC_TSTBY = "Select Test by";
	public static String NULL_QC_TSTTYP = "Select Test Type";
	public static String NULL_QC_PRODUCT = "Select product";
	public static String NULL_QC_PRDDRG = "Select product drawing code";
	public static String NULL_QC_TSTPRDSLNO = "Enter product Serial No.";
	public static String NULL_QC_RESULT = "Enter the result";
	public static String NULL_QC_RECEIPT = "Select receipt";
	public static String NULL_QC_TSTSMPL = "Enter sample tested quantity in number only";
	public static String NULL_QC_FAILED = "Enter failed quantity in number only";
	public static String QC_INCORT_FALDQTY = "Failed quanity should not greater than Sample Tested";
	public static String EXCEED_QTY = "Test quantity should not more than Batch Qty";

	//
	// Work Order Header
	public static String NULL_WORK_ORDER_HDR_WO_ODR_NO = "Work Order number already exist";
	public static String WORK_ORDER_HDR_WRK_ODR_N0 = "Please enter work Order.No";
	public static String WORK_ORDER_HDR_BRNCH_NM = "Please select branch name";
	public static String WORK_ORDER_HDR_CLNT_NM = "Please select client name";
	public static String WORK_ORDER_HDR_PONO = "Please select Purchase Ord.No";
	public static String WORK_ORDER_HDR_DT = "Please enter work order date";
	public static String WORK_ORDER_HDR_TYPE = "Please select work order type";
	public static String WORK_ORDER_HDR_STATUS = "Please select work order status";
	public static String WORK_ORDER_HDR_RMKS = "Please enter work order remarks";
	public static String WORK_ORDER_DTL_PR_NAME = "Please select product name";
	public static String WORK_ORDER_DTL_CPR_NAME = "Please customer product should not null";
	public static String WORK_ORDER_DTL_QTY = "Enter work order quantity in number only";
	public static String WORK_ORDER_DTL_PL_QTY = "Enter work order quantity in number only";
	public static String WORK_ORDER_DTL_INCOR_PL_QTY = "Work order plan should not greater than work order";
	public static String WORK_ORDER_DTL_WRQTY_NOT_NEGT = "Work order plan should not be negative";
	public static String WORK_ORDER_DTL_DT = "Please enter work order dtl date";
	// Work Order Plan Header
	public static String NULL_WO_PLN_NO = "Please select work order number";
	public static String NULL_WORK_ORDER_NO = "Please select Work Order Number";
	public static String NULL_BRACH_NAME = "Please select branch";
	public static String NULL_PLN_DT = "Please enter WO_Plan date";
	public static String NULL_PLN_STATUS = "Please select WO_Status";
	public static String NULL_WO_PLN_NO_EX = "Work Order plan number already exist";
	public static String NULL_PLANNED_QTY = "Please select planned reference number";
	public static String PRDCT_QTY_LONG = "Please enter number only";
	// Product Drawings
	public static String PRD_DRG_BRNCH_NAME = "Please select branch name";
	public static String PRD_DRG_PRD_NAME = "Please select product name";
	public static String PRD_DRG_PRD_CODE = "Please enter product code";
	public static String PRD_DRG_VERS_NO = "Enter version number";
	// QA Test
	public static String NULL_QATST_CLIENT = "Select client name";
	public static String NULL_QATST_PRODUCT = "Select product name";
	public static String NULL_QATST_PRDDRG = "Select product drawing code";
	public static String NULL_QATST_WORKORD_NO = "Select work order number";
	public static String NULL_QATST_TSTGRP = "Select Test group";
	public static String NULL_QA_PRDSLNO = "Enter product serial number";
	public static String NULL_QATST_TSTTYPE = "Select test type";
	public static String NULL_QATST_RESLT = "Enter the test result";
	public static String NULL_QATST_TSTCNDN = "Select test condition";
	public static String NULL_QATST_INSPDT = "Enter inspection date";
	public static String NULL_QATST_SPEC = "Select test specification";
	// MMS
	// Material Type
	public static String NULL_MATERIAL_SPEC_NAME = "Please enter Material Specification Name";
	public static String NULL_MATERIAL_TYPE_NAME = "Please enter material type name";
	public static String NULL_MATERIAL_STOCK = "Please given material value";
	public static String NULL_STOCK_TYPE = "Choose stocktype ";
	public static String NULL_DC_DATE = "Please choose DC Date";
	public static String NULL_DC_QTY = "Please enter DC Qty.";
	public static String NULL_DC_TYPE = "Please choose DC Type.";
	public static String NULL_GOODS_TYPE = "Please choose Goods Type.";
	public static String NULL_RAISED_BY = "Please choose Raised By.";
	public static String NULL_PERSON_NAME = "Please choose Person Name";
	public static String NULL_MODE_OF_TRANSACTION = "Please choose Mode Of Transaction";
	public static String NULL_RETURN_QTY = "Please enter Return Qty less than or equal to Issueqty";
	// Material
	public static String NULL_MATERIAL_CODE = "Please enter material code";
	public static String MATERIAL_CODE_VALIDATION = "Material code already exist";
	public static String NULL_MATERIAL_NAME = "Please enter material name";
	public static String NULL_MATERIAL_GROUP = "Please enter material group";
	public static String NULL_MATERIAL_TYPE = "Please enter material type";
	public static String NULL_MATERIAL_UOM = "Please enter material UOM";
	public static String NULL_MATERIAL_BRANCH = "Please enter material branch";
	public static String NULL_MATERIAL_DEPARTMENT = "Please enter material department";
	public static String NULL_INDENT_NO = "Please enter Indent No";
	public static String NULL_INDENT_TYPE = "Please choose Indent Type ";
	public static String NULL_INDENT_DATE = "Please choose Indent Date";
	public static String NULL_INDENT_QTY = "Please enter Indent Qty Greater than zero";
	public static String NULL_MATERIAL_QTYZero = "Please enter Material Qty Greater than zero";
	public static String NULL_MATERIAL_QTY = "Please enter Material Quantity";
	public static String NULL_ISSUE_QTY = "Please enter IssueQty less than or equalto balanceQty";
	public static String NULL_ISSUE_DATE = "Please choose Issue Date";
	public static String NULL_ISSUE_NAME = "Please enter Issue Name";
	public static String NULL_POBRANCH_NAME = "Please choose Branch Name";
	public static String NULL_PURCAHSE_ORD_NO = "Please Choose Purchase Order No";
	public static String NULL_REGECT_REASON = "Please Enter Rejection Reason";
	public static String NUL_POMATERIAL_NAME = "Please Choose Material Name";
	public static String NULL_POMATERIAL_UOM = "Please Choose Material UOM";
	public static String NULL_RECEIPT_QTY = "Please Enter Recepit Qty";
	public static String NULL_REGECT_QTY = "Please Enter Reject Qty";
	public static String NULL_POREGECT_REASON = "Please Enter Rejection Reason";
	public static String NULL_MMS_HDRSTATUS = "Please Choose Status";
	public static String nULL_MMS_goodstype = "Please Choose GoodsType";
	public static String NULL_MMS_gatepassType = "Please Choose GatepassType";
	public static String NULL_BALANCE_QTY = "Please enter Balance Qty.";
	public static String NULL_Issue_To = "Please choose Issue To";
	public static String NULL_RETURN_REASON = "Please choose Return Reason";
	// BOM Setup
	public static String NULL_PRODUCT_BOM_HEADER_NAME = "Please Select Product";
	public static String NULL_PRODUCT_BOM_BRANCH = "Please Select Branch";
	// Gcat
	public static String NULL_SETTING_CODE = "Please select Setting Code";
	// SMS
	public static String NULL_TAX_CODE = "Please enter Tax Code";
	public static String BRANCH_NAME = "Please Select Branch Name";
	public static String ENQUIRY_NO = "Please Select Enquiry No";
	public static String PRODUCT_NAME = "Please Select Product Name";
	public static String PRODUCT_UOM = "Please Select PRODUCT uom";
	public static String UNIT_RATE = "Please Enter Unit Rate";
	public static String SMS_QTY = "Please Enter Quantity";
	public static String ORDER_TYPE = "Please Select Order";
	public static String QUOTE_NO = "Please Select Order";
	public static String BASIC_VALUE = "Please Enter Basic Values";
	public static String PURCAHSE_ORD_NO = "Please Select PO No.";
	public static String EXP_DAL_DATE = "Please Select Expected Delivery Date";

	public static String REGECT_REASON = "Please Enter Reject Reason";
	public static String REGECT_QTY = "Reject Qty should be greater than zero";
	public static String RECEIPT_QTY = "Receipt Qty value should be greater than zero";
	public static String NULL_SPEC_CODE = "Please Enter Specification Code";
	public static String NULL_SPEC_STATUS = "Please Select Spec Status";
	public static String NULL_SPEC_DESC = "Please Enter Specification Desc";
	public static String NULL_ENQUIRY_QTY = "Please Select Enquiry_Qty";
	public static String NULL_ENQUIRYNO = "Please Enter EnquiryNo";
	public static String NULL_PREPARED_BY = "Please Enter Preparedby";
	public static String NULL_CLIENT_NAME = "Please Select ClientName";
	public static String NULL_MODOF_ENQUIRY = "Please Select ModofEnquiry";
	public static String NULL_ENQUIRY_DATE = "Please Select EnquiryDate";
	public static String NULL_DUE_DATE = "Please Select DueDate";
	public static String NULL_SMS_STATUS = "Please Select SMS_Enquiry_Status";
	public static String NULL_SMS_QUOTE_STATUS = "Please Select SMS Quote Status";
	public static String NULL_SMS_CUST_PROD_CODE = "Please Enter Cust_Prod_Code";
	public static String NULL_PODTL_STATUS = "Please Select PO Detail Status";
	public static String NULL_POACCEPT_OPTION = "Please Select PO Accept Option";
	public static String NULL_POACCEPT_PARAM = "Please Select PO Accept Param";
	public static String NULL_INVOICE_ADDRESS = "Please Select Invoice Address";
	public static String NULL_SMS_PRODNAME = "Please Select Product Name";
	public static String NULL_SMS_STOCKTYPE = "Please Select Stock Type";
	public static String NULL_SMS_RETURNQUANTITY = "Please Enter Return Quantity";
	public static String NULL_GREATRETHANZERO = "Please Enter Value Greater Than Zero";
	public static String NULL_STATUS = "Please choose Product Status";
	// MFG STT
	public static String NULL_ASMBL_PLAN_DT = "Please choose assembly plan date";
	public static String NULL_ASMBL_DT = "Please choose Asm. Date";
	public static String NULL_ASMBL_PRDTQTY = "Please Enter Product Quantity";
	public static String NULL_ASMBL_ACIVDQTY = "Please Enter Achieved Quantity";
	public static String NULL_RTOPLN_HDR = "Please Enter Date";
	public static String NULL_RTO_ARM = "Please Enter Product Name";
	public static String NULL_ASMBL_SHIFT = "Please Enter Shift Name";
	public static String NULL_MACHINE_NAME = "Please Select Machine Name";
	public static String NULL_PULVIZDTL_DATE = "Please Select Date";
	public static String NULL_OP_MATERIAL_ID = "Please select O/P material ";
	public static String LESS_THEN_ZERO = "Please Enter value should be greater then zero ";
	public static String QUNATITY_CHAR_VALIDATION = "Please Enter Quantity in Number Only";
	public static String QUNATITY_CHAR_VALIDATIONHCM = "Please Enter Business Value in Number Only";
	public static String UNITRATE_CHAR_VALIDATION = "Please Enter Unit Rate in Number Only";
	public static String UNITRATE_NUMBER_VALIDATION = "Please Enter Unit Rate in Should Be Greater Then Zero";
	public static String NULL_HEATING_TIME = "Please select the heating time";
	public static String NULL_CHARGE_TIME = "Please select the charge start time";
	public static String NULL_CHARGE_END_TIME = "Please select the charge end time";
	public static String NULL_MATERAL_NAME = "Please select material name ";
	public static String NULL_STOCK_TYP = "Please select stock type";
	public static String NULL_LOT_NO = "Please select lot number";
	public static String NULL_MATEIAL_QTY = "Material Qty should not be Zero";
	public static String NULL_ZONE_NAME = "Enter Zone name";
	public static String NULL_TEMP = "Enter Temprature";
	public static String WRONG_DATE = "Date should be current date";
	// Tool
	public static String NULL_FEEDBACK_CAT = "Please Enter the feedback category";
	public static String NULL_PRIORITY = "Please Choose priority";
	public static String NULL_TASKDT = "Please Choose Taskdate";
	public static String NULL_TASKEND = "Please Choose Task End date";
	public static String NULL_MEETING = "Please Choose Meeting";
	public static String NULL_REVISIONDT = "Please Enter the weightage";
	public static String NULL_WEIGHTAGE = "Please Choose Revision date";
	// branch
	public static String NULL_BADDR = "Please Enter Branch Address";
	public static String NULL_PAR_PRDT = "Please Select Parent Product";
	public static String NULL_PRICE = "Please Enter Branch Address";
}
