/**
 * File Name 		: User.java 
 * Description 		: this class is used for add/edit User details. 
 * Author 			: SOUNDAR C 
 * Date 			: Feb 25, 2014
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1          Mar 03 2014        	SOUNDAR C		        Initial Version
 * 0.2			25-Jun-2014			Madhu					Code re-factoring
 */
package com.gnts.base.mst;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.RoleDM;
import com.gnts.base.domain.mst.UserDM;
import com.gnts.base.domain.mst.UserRolesDM;
import com.gnts.base.domain.txn.UserLoginDM;
import com.gnts.base.service.mst.RoleService;
import com.gnts.base.service.mst.UserRolesService;
import com.gnts.base.service.mst.UserService;
import com.gnts.base.service.txn.UserLoginService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class User extends BaseUI {
	// Bean creation
	private UserService serviceUser = (UserService) SpringContextHelper.getBean("user");
	private UserLoginService serviceUserLogin = (UserLoginService) SpringContextHelper.getBean("userLogin");
	private RoleService serviceRole = (RoleService) SpringContextHelper.getBean("role");
	private UserRolesService serviceUserRole = (UserRolesService) SpringContextHelper.getBean("userRoles");
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// Form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn4, flColumn3;
	// Add User Input Controls
	private TextField tfLoginId, tfloginCount, tfCompanyName, tfTimeZone, tfSystemUser;
	private TextField tfPswdExpDt, tfPswdChangeDt, tfLastLoginDt, tfLoginCreationDt;
	private PasswordField pfPassWord;
	private ListSelect lsUserRole;
	private ComboBox cbUserStatus;
	private Button btnLogs = new Button("User Logs");
	// BeanItemContainer
	private BeanItemContainer<UserDM> beanUserDM = null;
	private BeanItemContainer<UserLoginDM> beanUserLoginDM = null;
	// Local variables declaration
	private Long companyid;
	private int recordCnt = 0;
	private String username;
	private String cmpNameFormat;
	private String cmpCode;
	private Long userId;
	// Initialize logger
	private Logger logger = Logger.getLogger(User.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public User() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		cmpCode = UI.getCurrent().getSession().getAttribute("companyCode").toString();
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside User() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting User UI");
		btnAdd.setVisible(false);
		// User Logs button
		btnLogs.addStyleName("add");
		// To set User logs button to Layout
		hlCmdBtnLayout.addComponent(btnLogs);
		hlCmdBtnLayout.setComponentAlignment(btnLogs, Alignment.MIDDLE_LEFT);
		tblMstScrSrchRslt.setVisible(true);
		btnLogs.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				btnEdit.setEnabled(false);
				vlSrchRsltContainer.removeAllComponents();
				vlSrchRsltContainer.addComponent(tblMstScrSrchRslt);
				tblMstScrSrchRslt.setVisible(true);
				UserLogs(userId);
			}
		});
		// Login Id TextField
		tfLoginId = new TextField();
		tfLoginId.setWidth("110");
		// User status combo Box
		cbUserStatus = new GERPComboBox("Status", BASEConstants.M_BASE_USER, BASEConstants.USER_STATUS);
		// PasswordExpireDate PopupDateField
		tfPswdExpDt = new GERPTextField("Password Expire Date");
		tfPswdExpDt.setReadOnly(true);
		// Password Change Date PopupDateField
		tfPswdChangeDt = new GERPTextField("Password Change Date");
		tfPswdChangeDt.setReadOnly(true);
		// Last Login Date PopupDateField
		tfLastLoginDt = new GERPTextField("Last Login Date");
		tfLastLoginDt.setReadOnly(true);
		// Login creation date
		tfLoginCreationDt = new GERPTextField("Login Creation Date");
		tfLoginCreationDt.setReadOnly(true);
		// concatenate company code
		cmpNameFormat = "@" + cmpCode.toLowerCase();
		// Company name text field
		tfCompanyName = new GERPTextField("");
		tfCompanyName.setValue(cmpNameFormat);
		tfCompanyName.setReadOnly(true);
		tfCompanyName.setWidth("45");
		tfCompanyName.setCaption(null);
		// user Password Field
		pfPassWord = new PasswordField("Password");
		pfPassWord.setWidth("150");
		// Timezone Textfield
		tfTimeZone = new GERPTextField("Time Zone");
		tfTimeZone.setReadOnly(true);
		// User Role TextfieldR
		lsUserRole = new ListSelect("User Role");
		lsUserRole.setItemCaptionPropertyId("roleName");
		lsUserRole.setWidth("200");
		lsUserRole.setHeight("110");
		lsUserRole.setMultiSelect(true);
		loadUserRole();
		// SystemUser Textfield
		tfSystemUser = new GERPTextField("System User");
		tfSystemUser.setReadOnly(true);
		// Login count Textfield
		tfloginCount = new GERPTextField("Login Count");
		tfloginCount.setValue("0");
		tfloginCount.setWidth("110");
		tfloginCount.setReadOnly(true);
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
		tfLoginId.setCaption("Login Id");
		// Add loginId in horizondal layout
		HorizontalLayout hlLoginIdplusCompanyName = new HorizontalLayout();
		hlLoginIdplusCompanyName.addComponent(tfLoginId);
		hlLoginIdplusCompanyName.addComponent(tfCompanyName);
		hlLoginIdplusCompanyName.setCaption("Login Id");
		// Add components for Search Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn1.addComponent(tfLoginId);
		flColumn2.addComponent(cbUserStatus);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.setMargin(true);
		hlSearchLayout.setSizeUndefined();
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Remove all components in Search Layout
		hlSearchLayout.removeAllComponents();
		tfLoginId.setCaption(null);
		// Add loginId in horizondal layout
		HorizontalLayout hlLoginIdplusCompanyName = new HorizontalLayout();
		hlLoginIdplusCompanyName.addComponent(tfLoginId);
		hlLoginIdplusCompanyName.addComponent(tfCompanyName);
		hlLoginIdplusCompanyName.setCaption("Login Id");
		// Add components for User Input Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		flColumn1.addComponent(hlLoginIdplusCompanyName);
		flColumn1.setComponentAlignment(hlLoginIdplusCompanyName, Alignment.TOP_LEFT);
		flColumn1.addComponent(pfPassWord);
		flColumn1.addComponent(tfTimeZone);
		flColumn1.addComponent(tfSystemUser);
		flColumn2.addComponent(tfLoginCreationDt);
		flColumn2.addComponent(tfLastLoginDt);
		flColumn2.addComponent(tfPswdExpDt);
		flColumn2.addComponent(tfPswdChangeDt);
		flColumn3.addComponent(tfloginCount);
		flColumn3.addComponent(cbUserStatus);
		flColumn4.addComponent(lsUserRole);
		hlUserInputLayout.addComponent(flColumn1);
		hlUserInputLayout.addComponent(flColumn2);
		hlUserInputLayout.addComponent(flColumn3);
		hlUserInputLayout.addComponent(flColumn4);
		hlUserInputLayout.setSizeUndefined();
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	private void loadSrchRslt() {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
			tblMstScrSrchRslt.removeAllItems();
			List<UserDM> userList = new ArrayList<UserDM>();
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Search Parameters are "
					+ companyid + ", " + tfLoginId.getValue() + ", " + (String) cbUserStatus.getValue());
			userList = serviceUser.getUserList(null, null, tfLoginId.getValue(), (String) cbUserStatus.getValue()
					.toString(), null, companyid, null, "F");
			recordCnt = userList.size();
			beanUserDM = new BeanItemContainer<UserDM>(UserDM.class);
			beanUserDM.addAll(userList);
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the User. result set");
			tblMstScrSrchRslt.setContainerDataSource(beanUserDM);
			tblMstScrSrchRslt.setVisibleColumns(new Object[] { "userid", "loginid", "creationdt", "passwordexpiredt",
					"lastlogindt", "userstatus", "lastupdateddt", "lastupdatedby" });
			tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Login Id", "Creation Date", "Pswd Exp.Date",
					"Last Login Date", "Status", "Updated Date", "Updated By" });
			tblMstScrSrchRslt.setColumnAlignment("userid", Align.RIGHT);
			tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	// Reset the field values to default values
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		cbUserStatus.setValue(cbUserStatus.getItemIds().iterator().next());
	}
	
	// Based on the selected record, the data would be populated into user input
	// fields in the input form
	private void editUser() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Editing the selected record");
		hlUserInputLayout.setVisible(true);
		if (tblMstScrSrchRslt.getValue() != null) {
			UserDM userDM = beanUserDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			userId = userDM.getUserid();
			if (userDM.getLoginid() != null) {
				tfLoginId.setReadOnly(false);
				tfLoginId.setValue(userDM.getLoginid());
				tfLoginId.setReadOnly(true);
			}
			if (userDM.getCreationdt() != null) {
				tfLoginCreationDt.setReadOnly(false);
				tfLoginCreationDt.setValue(userDM.getCreationdt());
				tfLoginCreationDt.setReadOnly(true);
			}
			if (userDM.getPasswordchangedt() != null) {
				tfPswdChangeDt.setReadOnly(false);
				tfPswdChangeDt.setValue(userDM.getPasswordchangedt());
				tfPswdChangeDt.setReadOnly(true);
			}
			if (userDM.getLastlogindt() != null) {
				tfLastLoginDt.setReadOnly(false);
				tfLastLoginDt.setValue(userDM.getLastlogindt());
				tfLastLoginDt.setReadOnly(true);
			}
			if (userDM.getPasswordexpiredtInDt() != null) {
				tfPswdExpDt.setReadOnly(false);
				tfPswdExpDt.setValue(userDM.getPasswordexpiredt());
				tfPswdExpDt.setReadOnly(true);
			}
			if (userDM.getLoginpassword() != null) {
				pfPassWord.setValue(userDM.getLoginpassword());
			}
			if (userDM.getLogincount() != null) {
				tfloginCount.setReadOnly(false);
				tfloginCount.setValue(userDM.getLogincount().toString());
				tfloginCount.setReadOnly(true);
			}
			if (userDM.getTimezoneid() != null) {
				tfTimeZone.setReadOnly(false);
				tfTimeZone.setValue(userDM.getTimezonedesc());
				tfTimeZone.setReadOnly(true);
			}
			if (userDM.getSystemuseryn() != null) {
				tfSystemUser.setReadOnly(false);
				tfSystemUser.setValue(userDM.getSystemuseryn());
				tfSystemUser.setReadOnly(true);
			}
			cbUserStatus.setValue(userDM.getUserstatus());
			// select roles
			lsUserRole.setValue(null);
			List<UserRolesDM> listUserRole = serviceUserRole.getRoleList(null, userId, cbUserStatus.getValue()
					.toString(), companyid, null, "F");
			for (UserRolesDM userrole : listUserRole) {
				lsUserRole.select(userrole.getRoleId());
			}
		}
	}
	
	private void UserLogs(Long userId) {
		try {
			List<UserLoginDM> userLoginList = null;
			Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
			userId = ((Long) sltedRcd.getItemProperty("userid").getValue());
			if (userId != null) {
				tblMstScrSrchRslt.removeAllItems();
				userLoginList = new ArrayList<UserLoginDM>();
				userLoginList = serviceUserLogin.getUserLoginList(userId, null);
				recordCnt = userLoginList.size();
				beanUserLoginDM = new BeanItemContainer<UserLoginDM>(UserLoginDM.class);
				beanUserLoginDM.addAll(userLoginList);
				tblMstScrSrchRslt.setContainerDataSource(beanUserLoginDM);
				tblMstScrSrchRslt.setSelectable(true);
				tblMstScrSrchRslt.setVisibleColumns(new Object[] { "loginRefId", "loginDate", "logoutDate", "clientIp",
						"sessionId" });
				tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Login Date", "Logout Date", "clientIp",
						"SessionId" });
				tblMstScrSrchRslt.setColumnFooter("lastupdatedby", "No.of Records:" + recordCnt);
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
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Resetting search fields and reloading the result");
		// reset the field valued to default
		cbUserStatus.setValue(cbUserStatus.getItemIds().iterator().next());
		tfLoginId.setReadOnly(false);
		tfLoginId.setValue("");
		lblNotification.setIcon(null);
		lblNotification.setCaption("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	// No addDetails() cannot be implemented
	@Override
	protected void addDetails() {
	}
	
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Getting audit record for User. ID " + userId);
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.M_BASE_USER);
		UI.getCurrent().getSession().setAttribute("audittablepk", userId.toString());
	}
	
	@Override
	protected void cancelDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Canceling action ");
		assembleSearchLayout();
		tfLoginId.setReadOnly(false);
		tblMstScrSrchRslt.setValue(null);
		resetFields();
		resetSearchDetails();
		loadSrchRslt();
	}
	
	@Override
	protected void editDetails() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Invoking Edit record ");
		hlUserInputLayout.removeAllComponents();
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(GERPPanelGenerator.createPanel(hlUserInputLayout));
		editUser();
	}
	
	// No validateDetails() cannot be implemented
	@Override
	protected void validateDetails() throws ValidationException {
	}
	
	@Override
	protected void saveDetails() throws ERPException.SaveException {
		try {
			logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Saving Data... ");
			UserDM userDM = new UserDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				userDM = beanUserDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			userDM.setLoginpassword(pfPassWord.getValue());
			userDM.setLastupdatedby(username);
			userDM.setLastupdateddt(DateUtils.getcurrentdate());
			if (cbUserStatus.getValue() != null) {
				userDM.setUserstatus((String) cbUserStatus.getValue());
			}
			serviceUser.saveorUpdateUserDetails(userDM);
			resetFields();
			loadSrchRslt();
			// for save UserRole details
			String[] split = lsUserRole.getValue().toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
			logger.info("User Role> split" + split);
			serviceUserRole.deleteUserRole(userId);
			for (String obj : split) {
				if (obj.trim().length() > 0) {
					UserRolesDM userrole = new UserRolesDM();
					userrole.setRoleId(Long.valueOf(obj.trim()));
					userrole.setUserId(userId);
					userrole.setCompanyId(companyid);
					userrole.setStatus("Active");
					userrole.setLastUpdatedBy(username);
					userrole.setLastUpdatedDt(DateUtils.getcurrentdate());
					serviceUserRole.saveOrUpdateUserRole(userrole);
				}
			}
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void loadUserRole() {
		try {
			BeanContainer<Long, RoleDM> beanUser = new BeanContainer<Long, RoleDM>(RoleDM.class);
			beanUser.setBeanIdProperty("roleId");
			beanUser.addAll(serviceRole.getRoleList(null, "Active", null, "F"));
			lsUserRole.setContainerDataSource(beanUser);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
}
