/**
 * File Name 		: ClientContacts.java 
 * Description 		: this class is used for add/edit Client Contacts details. 
 * Author 			: P Sekhar
 * Date 			: Mar 13, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
Version       Date           	Modified By               Remarks
 * 0.1          JUN 16 2014        	MOHAMED	        Initial Version
 * 0.2			18-Jun-2014			MOHAMED			Code re-factoring
 */
package com.gnts.crm.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.domain.txn.ClientsContactsDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.crm.service.txn.ClientContactsService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.constants.GERPErrorCodes;
import com.gnts.erputil.exceptions.ERPException;
import com.gnts.erputil.exceptions.ERPException.NoDataFoundException;
import com.gnts.erputil.exceptions.ERPException.ValidationException;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.BaseUI;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ClientContacts extends BaseUI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	private ClientContactsService serviceClntContact = (ClientContactsService) SpringContextHelper
			.getBean("clientContact");
	private CompanyLookupService serviceLookup = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private Long companyId;
	private VerticalLayout vlCommetTblLayout = new VerticalLayout();
	private VerticalLayout vlDocumentLayout = new VerticalLayout();
	private HorizontalLayout hlSearchLayout = new HorizontalLayout();
	private HorizontalLayout hlUserInputLayout = new HorizontalLayout();
	private HorizontalLayout hlImageLayout = new HorizontalLayout();
	private HorizontalLayout hlInput = new HorizontalLayout();
	private FormLayout FormLayout1, FormLayout2, FormLayout3;
	private String userName, strWidth = "160px";
	private HorizontalLayout hllayoutimage = new HorizontalLayout();
	/**
	 * for header layout
	 */
	/**
	 * UI Components
	 */
	private TextField tfContactName, tfDesignation, tftechperson, tfcommercialper, tfPhoneNo, tfMobileno, tfEmailId,
			tfCityname;
	private ComboBox cbClient, cbSearchstatus, cbClienSalut;
	private BeanContainer<Long, ClientDM> beanClients = null;
	// private BeanItemContainer<ClientsContactsDM> beanClientConatact = null;
	private BeanContainer<String, CompanyLookupDM> beanCompLookUp = null;
	private BeanItemContainer<ClientsContactsDM> beanclntcontact = null;
	private OptionGroup ogpersontype = new OptionGroup("");
	private Long clientId, moduleId, clntContactId, employeeid;
	private int recordCnt = 0;
	private Logger logger = Logger.getLogger(ClientContacts.class);
	Comments comment;
	Documents document;
	public static boolean filevalue = false;
	
	// Constructor
	public ClientContacts() {
		// Get the logged in user name and company id from the session
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Inside ClientContact() constructor");
		// Loading the UI
		buildview();
	}
	
	private void buildview() {
		ogpersontype.addItems("Technical Person", "Contact Person");
		ogpersontype.setValue("Technical Person");
		tfContactName = new GERPTextField("Contact person");
		tfContactName.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void blur(BlurEvent event) {
				if (tfContactName.getValue() != null) {
					tfContactName.setComponentError(null);
				}
			}
		});
		tfContactName.setMaxLength(100);
		tfContactName.setWidth(strWidth);
		tfDesignation = new GERPTextField("Designation");
		tfDesignation.setWidth(strWidth);
		tftechperson = new GERPTextField("Technical Person");
		tftechperson.setWidth(strWidth);
		tfcommercialper = new GERPTextField("Commercial Person");
		tfcommercialper.setWidth(strWidth);
		tfDesignation.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfDesignation.setComponentError(null);
				if (tfDesignation.getValue() != null) {
					tfDesignation.setComponentError(null);
				}
			}
		});
		tfEmailId = new GERPTextField("Email");
		tfEmailId.setMaxLength(30);
		tfEmailId.setWidth(strWidth);
		tfEmailId.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfEmailId.setComponentError(null);
			}
		});
		tfPhoneNo = new GERPTextField("Phone No.");
		tfPhoneNo.setWidth(strWidth);
		tfPhoneNo.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfPhoneNo.setComponentError(null);
			}
		});
		tfMobileno = new GERPTextField("Mobile No.");
		tfMobileno.setWidth(strWidth);
		tfMobileno.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfMobileno.setComponentError(null);
			}
		});
		cbClient = new GERPComboBox("Client");
		cbClient.setWidth(strWidth);
		cbClient.setItemCaptionPropertyId("clientfullname");
		cbClient.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (cbClient.getValue() != null) {
					tfCityname.setReadOnly(false);
					try {
						tfCityname.setValue(serviceClients
								.getClientDetails(null, (Long) cbClient.getValue(), null, null, null, null, null, null,
										null, "F").get(0).getCityName());
					}
					catch (Exception e) {
						e.printStackTrace();
						tfCityname.setReadOnly(false);
						tfCityname.setValue("");
						tfCityname.setReadOnly(true);
					}
					tfCityname.setReadOnly(true);
				} else {
					tfCityname.setReadOnly(false);
					tfCityname.setValue("");
					tfCityname.setReadOnly(true);
				}
			}
		});
		loadClientsDetails();
		tfCityname = new TextField("City");
		tfCityname.setWidth(strWidth);
		cbClienSalut = new GERPComboBox("Client Salut");
		cbClienSalut.setItemCaptionPropertyId("lookupname");
		cbClienSalut.setWidth(strWidth);
		cbClienSalut.setNullSelectionAllowed(false);
		loadLookUpList();
		cbSearchstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbSearchstatus.setItemCaptionPropertyId("desc");
		cbSearchstatus.setWidth(strWidth);
		hlSearchLayout = new GERPAddEditHLayout();
		hlSrchContainer.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		assembleSearchLayout();
		resetFields();
		loadSrchRslt();
	}
	
	private void loadClientsDetails() {
		try {
			List<ClientDM> clntList = serviceClients.getClientDetails(companyId, clientId, null, null, null, null,
					null, null, "Active", "P");
			beanClients = new BeanContainer<Long, ClientDM>(ClientDM.class);
			beanClients.setBeanIdProperty("clientId");
			beanClients.addAll(clntList);
			cbClient.setContainerDataSource(beanClients);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * load client details for search based on company id and status
	 */
	/**
	 * this method used to load the company look up list based on status
	 */
	private void loadLookUpList() {
		try {
			List<CompanyLookupDM> compLookUpList = serviceLookup.getCompanyLookUpByLookUp(companyId, moduleId,
					"Active", "BS_SALUTN");
			beanCompLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompLookUp.setBeanIdProperty("lookupname");
			beanCompLookUp.addAll(compLookUpList);
			cbClienSalut.setContainerDataSource(beanCompLookUp);
		}
		catch (Exception e) {
			logger.info("load company look up details" + e);
		}
	}
	
	private void assembleSearchLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		/*
		 * Adding user input layout to the search layout as all the fields in the user input are available in the search
		 * block. hence the same layout used as is
		 */
		hlSearchLayout.removeAllComponents();
		FormLayout1 = new FormLayout();
		FormLayout2 = new FormLayout();
		FormLayout3 = new FormLayout();
		/**
		 * add fields to form Layout
		 */
		FormLayout1.addComponent(tfContactName);
		FormLayout2.addComponent(cbClient);
		FormLayout3.addComponent(cbSearchstatus);
		hlSearchLayout.addComponent(FormLayout1);
		hlSearchLayout.addComponent(FormLayout2);
		hlSearchLayout.addComponent(FormLayout3);
		hlSearchLayout.setSizeUndefined();
		hlSearchLayout.setSpacing(true);
		hlSearchLayout.setMargin(true);
	}
	
	private void assembleUserInputLayout() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Assembling search layout");
		// add the form layouts into user input layout
		/**
		 * add fields to form Layout
		 */
		hlUserInputLayout.removeAllComponents();
		hlInput.removeAllComponents();
		FormLayout1 = new FormLayout();
		FormLayout2 = new FormLayout();
		FormLayout3 = new FormLayout();
		FormLayout1.addComponent(tfContactName);
		tfContactName.setRequired(true);
		FormLayout1.addComponent(tfDesignation);
		FormLayout1.addComponent(ogpersontype);
		FormLayout1.addComponent(cbClient);
		FormLayout1.addComponent(tfCityname);
		FormLayout2.addComponent(cbClienSalut);
		FormLayout2.addComponent(tfEmailId);
		FormLayout2.addComponent(tfMobileno);
		FormLayout2.addComponent(tfPhoneNo);
		tfPhoneNo.setRequired(true);
		FormLayout2.addComponent(cbSearchstatus);
		FormLayout3.addComponent(hlImageLayout);
		VerticalLayout hlUserInput = new VerticalLayout();
		hlInput.setWidth("1175");
		hlInput.addComponent(FormLayout1);
		hlInput.addComponent(FormLayout2);
		hlInput.addComponent(FormLayout3);
		hlInput.setComponentAlignment(FormLayout3, Alignment.BOTTOM_RIGHT);
		hlInput.setMargin(true);
		hlUserInput.addComponent(GERPPanelGenerator.createPanel(hlInput));
		TabSheet test3 = new TabSheet();
		test3.addTab(vlCommetTblLayout, "Comments");
		test3.addTab(vlDocumentLayout, "Documents");
		test3.setWidth("1195");
		hlUserInput.addComponent(test3);
		hlUserInputLayout.addComponent(hlUserInput);
		hlUserInputLayout.setSpacing(true);
		hlUserInputLayout.setSizeUndefined();
	}
	
	public void loadSrchRslt() {
		// tblMstScrSrchRslt.removeAllItems();
		List<ClientsContactsDM> clientContactList = new ArrayList<ClientsContactsDM>();
		clientContactList = serviceClntContact.getClientContactsDetails(companyId, null, (Long) cbClient.getValue(),
				(String) tfContactName.getValue(), (String) cbSearchstatus.getValue(), null);
		recordCnt = clientContactList.size();
		System.out.println("clientContactList---->" + recordCnt);
		beanclntcontact = new BeanItemContainer<ClientsContactsDM>(ClientsContactsDM.class);
		beanclntcontact.addAll(clientContactList);
		tblMstScrSrchRslt.setContainerDataSource(beanclntcontact);
		tblMstScrSrchRslt.setVisibleColumns(new Object[] { "contactId", "contactName", "clientName", "designation",
				"phoneNo", "contactStatus", "lastUpdatedDt", "lastUpdatedBy" });
		tblMstScrSrchRslt.setColumnHeaders(new String[] { "Ref.Id", "Contact person", "Client ", "Designation",
				"Phone No.", "Status", "Last Updated Date", "Last Updated By" });
		tblMstScrSrchRslt.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
	}
	
	private void editClientContactDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Editing the selected record");
		hlCmdBtnLayout.setVisible(false);
		hlUserInputLayout.setVisible(true);
		Item sltedRcd = tblMstScrSrchRslt.getItem(tblMstScrSrchRslt.getValue());
		clntContactId = (Long) sltedRcd.getItemProperty("contactId").getValue();
		if (sltedRcd != null) {
			ClientsContactsDM editContactlist = beanclntcontact.getItem(tblMstScrSrchRslt.getValue()).getBean();
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Selected Dept. Id -> "
					+ clientId);
			tfContactName.setValue(editContactlist.getContactName());
			tfDesignation.setValue(editContactlist.getDesignation());
			if (editContactlist.getTechPerson() != null) {
				ogpersontype.setValue(editContactlist.getTechPerson());
			}
			cbClient.setValue(editContactlist.getClientId());
			cbClienSalut.setValue(editContactlist.getContactSalut());
			tfEmailId.setValue(editContactlist.getEmailId());
			tfMobileno.setValue(editContactlist.getMobileNo());
			tfPhoneNo.setValue(editContactlist.getPhoneNo());
			cbSearchstatus.setValue(cbSearchstatus.getItemIds().iterator());
			if (editContactlist.getContactphoto() != null) {
				hlImageLayout.removeAllComponents();
				byte[] myimage = (byte[]) editContactlist.getContactphoto();
				UploadUI uploadObject = new UploadUI(hlImageLayout);
				uploadObject.dispayImage(myimage, editContactlist.getContactName());
			} else {
				try {
					new UploadUI(hlImageLayout);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		comment = new Comments(vlCommetTblLayout, employeeid, null, null, null, clntContactId, null, null);
		document = new Documents(vlDocumentLayout, null, null, null, clntContactId, null, null);
		comment.loadsrch(true, null, clntContactId, null, null, null, null);
		document.loadsrcrslt(true, null, clntContactId, null, null, null, null);
	}
	
	@Override
	protected void searchDetails() throws NoDataFoundException {
		// TODO Auto-generated method stub
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + " Invoking search");
		loadSrchRslt();
		if (recordCnt == 0) {
			logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "No data for the search. throwing ERPException.NoDataFoundException");
			throw new ERPException.NoDataFoundException();
		} else {
			lblNotification.setIcon(null);
			lblNotification.setCaption("");
		}
	}
	
	@Override
	protected void resetSearchDetails() {
		cbSearchstatus.setValue(cbSearchstatus.getItemIds().iterator().next());
		tfContactName.setValue("");
		cbClient.setValue(null);
		resetFields();
		loadSrchRslt();
	}
	
	@Override
	protected void addDetails() {
		hlUserIPContainer.removeAllComponents();
		hlInput.removeAllComponents();
		hlUserIPContainer.addComponent((hlUserInputLayout));
		assembleUserInputLayout();
		hlCmdBtnLayout.setVisible(false);
		tblMstScrSrchRslt.setVisible(false);
		hlImageLayout.removeAllComponents();
		resetFields();
		cbClient.setRequired(true);
		new UploadUI(hlImageLayout);
		comment = new Comments(vlCommetTblLayout, employeeid, null, null, null, clntContactId, null, null);
		document = new Documents(vlDocumentLayout, null, null, null, clntContactId, null, null);
	}
	
	@Override
	protected void editDetails() {
		// TODO Auto-generated method stub
		assembleUserInputLayout();
		hlUserIPContainer.removeAllComponents();
		hlUserIPContainer.addComponent(hlUserInputLayout);
		tblMstScrSrchRslt.setVisible(false);
		editClientContactDetails();
	}
	
	@Override
	protected void validateDetails() throws ValidationException {
		boolean errorflag = false;
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Validating Data ");
		if ((tfContactName.getValue() == null) || tfContactName.getValue().trim().length() == 0) {
			tfContactName.setComponentError(new UserError(GERPErrorCodes.NULL_CLNTCONTACT_NAME));
			errorflag = true;
		} else {
			tfContactName.setComponentError(null);
		}
		/*
		 * String emailSeq = tfEmailId.getValue().toString(); if (!emailSeq.contains("@") || !emailSeq.contains(".")) {
		 * tfEmailId.setComponentError(new UserError(GERPErrorCodes.EMAIL_VALIDATION)); errorflag = true; }
		 */
		/*
		 * if (tfPhoneNo.getValue().toString() == null) { tfPhoneNo.setComponentError(new
		 * UserError(GERPErrorCodes.NULL_PHONE_NUMBER)); } else if (tfPhoneNo.getValue() != null) { if
		 * (!tfPhoneNo.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) { tfPhoneNo.setComponentError(new
		 * UserError(GERPErrorCodes.PHONE_NUMBER_VALIDATION)); errorflag = true; } } if
		 * ((tfMobileno.getValue().toString() == null)) { tfMobileno.setComponentError(new
		 * UserError(GERPErrorCodes.NULL_PHONE_NUMBER)); } else if (tfMobileno.getValue() != null) { if
		 * (!tfMobileno.getValue().matches("^\\+?[0-9. ()-]{10,25}$")) tfMobileno.setComponentError(new UserError(
		 * GERPErrorCodes.NULL_PHONE_NUMBER)); errorflag = true; }
		 */
		/*
		 * if ((tfDesignation.getValue() == null) || tfDesignation.getValue().trim().length() == 0) {
		 * tfDesignation.setComponentError(new UserError(GERPErrorCodes.NULL_DESIGNATION)); } else {
		 * tfDesignation.setComponentError(null); }
		 */
		if ((cbClient.getValue() == null)) {
			cbClient.setComponentError(new UserError(GERPErrorCodes.NULL_CLIENT_NAME));
		} else {
			cbClient.setComponentError(null);
		}
		if (errorflag) {
			logger.warn("Company ID : " + companyId + " | User Name : " + userName + " > "
					+ "Throwing ValidationException. User data is > " + tfContactName.getValue() + tfEmailId.getValue());
			throw new ERPException.ValidationException();
		}
	}
	
	@Override
	protected void saveDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Saving Data... ");
		try {
			ClientsContactsDM Contactobj = new ClientsContactsDM();
			if (tblMstScrSrchRslt.getValue() != null) {
				Contactobj = beanclntcontact.getItem(tblMstScrSrchRslt.getValue()).getBean();
			}
			Contactobj.setContactName(tfContactName.getValue());
			Contactobj.setDesignation(tfDesignation.getValue());
			if (ogpersontype.getValue() != null) {
				Contactobj.setTechPerson(ogpersontype.getValue().toString());
			}
			if (cbClient.getValue() != null) {
				Contactobj.setClientId((Long) cbClient.getValue());
			}
			if (cbClienSalut.getValue() != null) {
				Contactobj.setContactSalut(cbClienSalut.getValue().toString());
			}
			if (tfEmailId.getValue() != "") {
				Contactobj.setEmailId(tfEmailId.getValue());
			}
			System.out.println("LeadIDloader" + cbClienSalut.getValue());
			Contactobj.setMobileNo(tfMobileno.getValue());
			Contactobj.setPhoneNo(tfPhoneNo.getValue());
			if ((Boolean) UI.getCurrent().getSession().getAttribute("isFileUploaded")) {
				try {
					Contactobj.setContactphoto((byte[]) UI.getCurrent().getSession().getAttribute("imagebyte"));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Contactobj.setContactphoto(null);
			}
			if (cbSearchstatus.getValue() != null) {
				Contactobj.setContactStatus(cbSearchstatus.getValue().toString());
			}
			Contactobj.setLastUpdatedDt(DateUtils.getcurrentdate());
			Contactobj.setLastUpdatedBy(userName);
			serviceClntContact.saveOrdUpdateClientContacts(Contactobj);
			System.out.println("saveOrdUpdateClientContacts" + Contactobj);
			resetFields();
			comment.savecontact(Contactobj.getContactId());
			System.out.println("contactsaved" + Contactobj.getContactId());
			comment.resetfields();
			document.savecontact(Contactobj.getContactId());
			document.ResetFields();
			loadSrchRslt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * catch (Exception e) { e.printStackTrace(); }
	 */
	@Override
	protected void showAuditDetails() {
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > "
				+ "Getting audit record for TestType. ID " + "");
		UI.getCurrent().getSession().setAttribute("audittable", BASEConstants.T_CRM_CAMPAIGN);
		UI.getCurrent().getSession().setAttribute("audittablepk", clntContactId);
	}
	
	@Override
	protected void cancelDetails() {
		assembleSearchLayout();
		tfContactName.setRequired(false);
		hlImageLayout.removeAllComponents();
		hlCmdBtnLayout.setVisible(true);
		tblMstScrSrchRslt.setVisible(true);
		resetFields();
	}
	
	@Override
	protected void resetFields() {
		// TODO Auto-generated method stub
		tfContactName.setValue("");
		tfContactName.setComponentError(null);
		tfDesignation.setValue("");
		tfDesignation.setComponentError(null);
		tftechperson.setValue("");
		tfcommercialper.setComponentError(null);
		cbClient.setValue(null);
		cbClient.setRequired(false);
		cbClienSalut.setValue(null);
		tfEmailId.setValue("");
		tfEmailId.setComponentError(null);
		tfMobileno.setValue("");
		tfMobileno.setComponentError(null);
		tfPhoneNo.setValue("");
		tfPhoneNo.setComponentError(null);
		hllayoutimage.removeAllComponents();
		filevalue = false;
		ogpersontype.setValue("Technical Person");
		cbSearchstatus.setValue(cbSearchstatus.getItemIds().iterator().next());
		UI.getCurrent().getSession().setAttribute("isFileUploaded", false);
	}
}
