/**
 * File Name	:	EmailLogger.java
 * Description	:	Used for view T_GCAT_EMAIL_LOGS details
 * Author		:	Prakash.s
 * Date			:	mar 12, 2014 
 * Modified By  :   prakash.s
 * Description	:
 *
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies pvt. ltd.
 * Version         Date           Modified By             Remarks
 * 0.1           3-Jun-2014         Ganga              Code Optimizing&code re-factoring
 * 
 */
package com.gnts.gcat.rpt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.ProductService;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPFormLayout;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPPopupDateField;
import com.gnts.erputil.components.GERPSaveNotification;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.SaveException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.gcat.domain.rpt.EmailLoggerDM;
import com.gnts.gcat.service.rpt.EmailLoggerService;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class EmailLogger extends BaseUI {
	private EmailLoggerService serviceemailLogger = (EmailLoggerService) SpringContextHelper.getBean("emailLogger");
	private ProductService servicebeanproduct = (ProductService) SpringContextHelper.getBean("Product");
	// form layout for input controls
	private FormLayout flColumn1, flColumn2, flColumn3, flColumn4;
	// Parent layout for all the input controls
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	// Search Control Layout
	private HorizontalLayout hlSearchLayout;
	// User Input Components
	private TextField tfemailid;
	private PopupDateField dfemailStartdate, dfemailEndDate;
	private GERPComboBox cbproduct;
	private CheckBox cockeckall = new CheckBox();
	private List<EmailLoggerDM> emailList = new ArrayList<EmailLoggerDM>();
	// Bean Container
	private BeanItemContainer<EmailLoggerDM> beanEmailLoggerDM = null;
	// Local variables
	private Long companyid;
	private String username;
	private Long userId;
	private int recordCnt;
	// Buttons
	private Button btndelete;
	// Initialize logger
	private Logger logger = Logger.getLogger(EmailLogger.class);
	private static final long serialVersionUID = 1L;
	
	// Constructor
	public EmailLogger() {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		userId = Long.valueOf(UI.getCurrent().getSession().getAttribute("userId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > "
				+ "Inside EmailLogger() constructor");
		// Loading the UI
		buildview();
	}
	
	// Build the UI components
	private void buildview() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Painting Email logger UI");
		btnAdd.setVisible(false);
		btnDownload.setVisible(false);
		btnEdit.setVisible(false);
		btnAuditRecords.setVisible(false);
		// Email-id text box
		tfemailid = new TextField("Email Id");
		// product combo box
		cbproduct = new GERPComboBox("Product Name"); 
		cbproduct.setItemCaptionPropertyId("prodname");
		loadProductList();
		// Email start date
		dfemailStartdate = new GERPPopupDateField("Log Start Dt");
		dfemailStartdate.setInputPrompt("Select Date");
		// Email End date
		dfemailEndDate = new GERPPopupDateField("Log End Dt");
		dfemailEndDate.setInputPrompt("Select Date");
		btndelete = new Button("Delete");
		btndelete.addStyleName("delete");
		btndelete.setEnabled(true);
		btndelete.setVisible(true);
		btndelete.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				btndelete.setEnabled(true);
				if (cockeckall.getValue().equals(true)) {
					for (EmailLoggerDM obj : emailList) {
						serviceemailLogger.delete(obj.getEmaillogId());
					}
					
				}else{
					delete();
					
				}
					
				loadSrchRslt();
			/*	btndelete.setEnabled(false);
				cockeckall.setValue(false);*/
			}
		});
		cockeckall = new CheckBox("Select All");
		cockeckall.addStyleName("delete");
		cockeckall.setEnabled(true);
		cockeckall.setVisible(true);
		setCheckBoxTable();
		cockeckall.setImmediate(true);
		cockeckall.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue().equals(true)) {
					loadSrchRslt();
					btndelete.setEnabled(true);
				} else {
					loadSrchRslt();
					btndelete.setEnabled(false);
				}
			}
		});
		hlCmdBtnLayout.addComponent(cockeckall);
		hlCmdBtnLayout.setSpacing(true);
		hlCmdBtnLayout.setComponentAlignment(cockeckall, Alignment.MIDDLE_RIGHT);
		hlCmdBtnLayout.addComponent(btndelete);
		hlCmdBtnLayout.setComponentAlignment(btndelete, Alignment.MIDDLE_RIGHT);
		hlCmdBtnLayout.setExpandRatio(btndelete, 1);
		// build search layout
		hlSearchLayout = new GERPAddEditHLayout();
		assembleSearchLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		resetFields();
		loadSrchRslt();
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling search layout");
		// Remove all components in User Input Layout
		hlSearchLayout.removeAllComponents();
		hlSearchLayout.setMargin(true);
		// Add components for Search Layout
		flColumn1 = new GERPFormLayout();
		flColumn2 = new GERPFormLayout();
		flColumn3 = new GERPFormLayout();
		flColumn4 = new GERPFormLayout();
		flColumn1.addComponent(cbproduct);
		flColumn2.addComponent(dfemailStartdate);
		flColumn2.setSpacing(true); 
		flColumn3.addComponent(dfemailEndDate);
		flColumn3.setMargin(true);
		flColumn3.setSpacing(true);
  		flColumn4.addComponent(tfemailid);
		hlSearchLayout.addComponent(flColumn1);
		hlSearchLayout.addComponent(flColumn2);
		hlSearchLayout.addComponent(flColumn3);
		hlSearchLayout.addComponent(flColumn4);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setMargin(true);
	}
	
	// get the search result from DB based on the search parameters
	public void loadSrchRslt() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Loading Search...");
		tblMstScrSrchRslt.removeAllItems();
		Long prodname = null;
		if (cbproduct.getValue() != null) {
			prodname = ((Long) cbproduct.getValue());
		}
		String stdate = null;
		if (dfemailStartdate.getValue() != null) {
			stdate = (DateUtils.datetostring(dfemailStartdate.getValue()));
		}
		String enddate = null;
		if (dfemailEndDate.getValue() != null) {
			enddate = (DateUtils.datetostring(dfemailEndDate.getValue()));
		}
		emailList = serviceemailLogger.getEmailLoggerList(companyid, userId, prodname, tfemailid.getValue(), stdate,
				enddate);
		recordCnt = emailList.size();
		if (cockeckall.getValue().equals(true)) {
			List<EmailLoggerDM> mylist = new ArrayList<EmailLoggerDM>();
			for (EmailLoggerDM obj : emailList) {
				obj.setSelected(true);
				mylist.add(obj);
			}
			beanEmailLoggerDM = new BeanItemContainer<EmailLoggerDM>(EmailLoggerDM.class);
			beanEmailLoggerDM.addAll(mylist);
		} else {
			List<EmailLoggerDM> mylist = new ArrayList<EmailLoggerDM>();   
			for (EmailLoggerDM obj : emailList) {
				obj.setSelected(false);
				mylist.add(obj);
			}
			beanEmailLoggerDM = new BeanItemContainer<EmailLoggerDM>(EmailLoggerDM.class);
			beanEmailLoggerDM.addAll(mylist);
		}
		tblMstScrSrchRslt.setContainerDataSource(beanEmailLoggerDM);
		tblMstScrSrchRslt.setSelectable(true);
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Got the UserFav result set");
		tblMstScrSrchRslt.setContainerDataSource(beanEmailLoggerDM);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "emaillogId", "productName", "clintnam",
				"emailid", "emailDate", "emailSubject", "quoteRef", });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Product Name", "Client Name", "Email Id",
				"Email Date", "Email Subject", "Quote Ref.No" });
		tblMstScrSrchRslt.setColumnAlignment("emailid", Align.LEFT);
		tblMstScrSrchRslt.setColumnFooter("quoteRef", "No.of Records : " + recordCnt);
	}
	
	private void setCheckBoxTable() {
		tblMstScrSrchRslt.addGeneratedColumn("selected", new ColumnGenerator() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Component generateCell(final Table source, final Object itemId, final Object columnId) {
				final EmailLoggerDM bean = (EmailLoggerDM) itemId;
				final CheckBox chbox = new CheckBox();
				chbox.setImmediate(true);
				chbox.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = 1L;
					
					@Override
					public void valueChange(final ValueChangeEvent event) {
						bean.setSelected((Boolean) event.getProperty().getValue());
						if (event.getProperty().getValue().equals(true)) {
							btndelete.setEnabled(true);
						} else {
							btndelete.setEnabled(false);
						}
					}
				});
				if (bean.isSelected()) {
					chbox.setValue(true);
				} else {
					chbox.setValue(false);
				}
				return chbox;
			}
		});
	}
	
	private void loadProductList() {
		try {
			List<ProductDM> productlist = servicebeanproduct.getProductList(companyid, null, null, null, null, null,null,
					"F");
			BeanContainer<Long, ProductDM> prodList = new BeanContainer<Long, ProductDM>(ProductDM.class);
			prodList.setBeanIdProperty("prodid");
			prodList.addAll(productlist);
			cbproduct.setContainerDataSource(prodList);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("fn_loadProductList_Exception Caught->" + e);
		}
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + " Invoking search");
		hlUserInputLayout.removeAllComponents();
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
		hlUserInputLayout.removeAllComponents();
		// reset the field valued to default
		cbproduct.setValue(null);
		cbproduct.setComponentError(null);
 		lblNotification.setIcon(null);
		dfemailStartdate.setValue(null);
		dfemailEndDate.setValue(null);
		lblNotification.setCaption("");
		tfemailid.setValue("");
		// reload the search using the defaults
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {

	}
	
	@Override
	protected void editDetails() {
		
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
	}
	
	@Override
	protected void saveDetails() throws SaveException, FileNotFoundException, IOException {

	}
	
	@Override
	protected void showAuditDetails() {

	}
	
	@Override
	protected void cancelDetails() {
	}
	
	@Override
	protected void resetFields() {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Resetting the UI controls");
		dfemailStartdate.setValue(null);
		tfemailid.setValue("");
		cbproduct.setValue(null);
		dfemailStartdate.setValue(null);
		
	}
	
	private void delete() {
		EmailLoggerDM emaillogDM = new EmailLoggerDM();
		tblMstScrSrchRslt.setSelectable(true);
	 	if (tblMstScrSrchRslt.getValue() != null) {
			emaillogDM = beanEmailLoggerDM.getItem(tblMstScrSrchRslt.getValue()).getBean();
			Long logid =emaillogDM.getEmaillogId();
			serviceemailLogger.delete(logid);
			resetFields();
			loadSrchRslt();
		}

	}
	
}
