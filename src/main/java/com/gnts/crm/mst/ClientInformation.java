/**
 * File Name 		: ClientInformation.java 
 * Description 		: this class is used for add/edit Client Information  details. 
 * Author 			: MOHAMED
 * Date 			: Mar 12, 2014
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

 **/
package com.gnts.crm.mst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.crm.domain.mst.ClientInformationDM;
import com.gnts.crm.service.mst.ClientInformationService;
import com.gnts.erputil.BASEConstants;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ClientInformation implements ClickListener {
	/**
	 * 339
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ClientInformationService serviceinformation = (ClientInformationService) SpringContextHelper
			.getBean("clntinform");
	private Table tblinform = new Table();
	private ComboBox cbstatus;
	private TextField tfinfocode;
	private GERPTextArea tainfodesc;
	private FormLayout flcolumn1, flcolumn2;
	private HorizontalLayout hlinput = new HorizontalLayout();
	private VerticalLayout vlTableForm;
	private BeanItemContainer<ClientInformationDM> beanClntinform = null;
	private List<ClientInformationDM> informlist = new ArrayList<ClientInformationDM>();
	private int recordCnt = 0;
	private String userName;
	private Long clientid, companyId;
	private Button btnadd = new GERPButton("Add", "add", this);
	public Button btndelete = new GERPButton("Delete", "delete", this);
	private Logger logger = Logger.getLogger(ClientInformationDM.class);
	
	public ClientInformation(VerticalLayout vlinformTblLayout, Long clientId) {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		// clientid = (Long) (UI.getCurrent().getSession().getAttribute("clientId"));
		clientid = clientId;
		buildview(vlinformTblLayout);
	}
	
	private void buildview(VerticalLayout vlinformTblLayout) {
		vlinformTblLayout.removeAllComponents();
		hlinput.removeAllComponents();
		btnadd.addClickListener(new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnadd == event.getButton()) {
					saveClientinformDetails();
				} 
			}
		});
		cbstatus = new GERPComboBox("Status", BASEConstants.M_GENERIC_TABLE, BASEConstants.M_GENERIC_COLUMN);
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		cbstatus.setWidth("150"); 
		tfinfocode = new GERPTextField("Information Code");  
		tainfodesc = new GERPTextArea("Description");  
		tainfodesc.setWidth("150");  
		tainfodesc.setHeight("40");   
		flcolumn1 = new FormLayout(); 
		flcolumn2 = new FormLayout();
		flcolumn1.addComponent(tfinfocode);
		flcolumn1.addComponent(tainfodesc);
		flcolumn1.addComponent(cbstatus);
		flcolumn2.addComponent(btnadd);
		flcolumn2.addComponent(btndelete);
		hlinput.addComponent(flcolumn1);
		hlinput.addComponent(flcolumn2);
		vlTableForm = new VerticalLayout();
		vlTableForm.setSizeFull();
		vlTableForm.setMargin(true);
		vlTableForm.setSpacing(true);
		vlTableForm.addComponent(hlinput);
		HorizontalLayout hlButton = new HorizontalLayout();
		hlButton.addComponent(vlTableForm);
		hlButton.addComponent(tblinform);
		tblinform.setWidth("700");
		tblinform.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblinform.isSelected(event.getItemId())) {
					tblinform.setImmediate(true);
					btnadd.setCaption("Add");
					btnadd.setStyleName("addbt");
					tfinfocode.setValue("");
					cbstatus.setValue(null);
					tainfodesc.setValue("");
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnadd.setCaption("Update");
					btnadd.setStyleName("savebt");
					editinformationDetails();
					btnadd.setStyleName("savebt");
				}
			}
		});
		vlinformTblLayout.addComponent(hlButton);
		loadsrch(false, null);
	}
	
	public void loadsrch(boolean fromdb, Long clientId) {
		if (fromdb) {
			informlist = serviceinformation.getClientDetails(null, clientId, null, (String) cbstatus.getValue(), null);
		}
		try {
			tblinform.removeAllItems();
			recordCnt = informlist.size();
			System.out.println("LISTSIZE---1->" + informlist.size());
			beanClntinform = new BeanItemContainer<ClientInformationDM>(ClientInformationDM.class);
			beanClntinform.addAll(informlist);
			tblinform.setSelectable(true);
			tblinform.setPageLength(8);
			tblinform.setContainerDataSource(beanClntinform);
			tblinform.setColumnAlignment("commnetId", Align.RIGHT);
			// tblinform.addItem(taComments);
			tblinform.setVisibleColumns(new Object[] { "clientinfoid", "clntinfocode", "clntinfodesc",
					"lastupdateddt", "lastupdatedby" });
			tblinform.setColumnHeaders(new String[] { "Ref.Id", "Client Informationcode", "Description",
					"Last Updated Date", "Last Updated By" });
			tblinform.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordCnt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editinformationDetails() {
		try {
			if (tblinform.getValue() != null) {
				ClientInformationDM editinform = beanClntinform.getItem(tblinform.getValue()).getBean();
				tfinfocode.setValue(editinform.getClntinfocode());
				cbstatus.setValue(editinform.getClntinfostatus());
				tainfodesc.setValue(editinform.getClntinfodesc());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Edit is not working");
		}
	}
	
	private void validateAll() {
		try {
			tfinfocode.setRequired(true);
			tfinfocode.validate();
			
		}
		catch (Exception e) {
			logger.info("validaAll :information code name is empty--->" + e);
			tfinfocode.setComponentError(new UserError("Enter Comments"));
		}
		try {
			tainfodesc.setRequired(true);
			tainfodesc.validate();
			
		}
		catch (Exception e) {
			logger.info("validaAll :description name is empty--->" + e);
			tainfodesc.setComponentError(new UserError("Enter Description"));
		}
	
	}
	
	public void saveClientinformDetails() {
		validateAll();
		ClientInformationDM saveClntinform = new ClientInformationDM();
		if (tblinform.getValue() != null) {
			saveClntinform = beanClntinform.getItem(tblinform.getValue()).getBean();
			informlist.remove(saveClntinform);
		}
		saveClntinform.setClntinfodesc(tainfodesc.getValue());
		saveClntinform.setClntinfocode(tfinfocode.getValue());
		saveClntinform.setClntinfostatus((String) cbstatus.getValue());
		saveClntinform.setLastupdateddt(DateUtils.getcurrentdate());
		saveClntinform.setLastupdatedby(userName);
		if (tfinfocode.isValid() && tainfodesc.isValid() && cbstatus.isValid()) {
			informlist.add(saveClntinform);
			resetfields();
			loadsrch(false, null);
		}
		btnadd.setCaption("Add");
		
	}
	
	private void deleteDetails() {
		ClientInformationDM saveClntinform = new ClientInformationDM();
		if (tblinform.getValue() != null) {
			saveClntinform = beanClntinform.getItem(tblinform.getValue()).getBean();
			
			informlist.remove(saveClntinform);
			tfinfocode.setValue("");
			cbstatus.setValue(null);
			tainfodesc.setValue("");
			btnadd.setCaption("Add");
			loadsrch(false, null);
			
		}
	}
	
	public void saveinformation(Long clientId) {
		System.out.println("saveid1-->>" + clientid);
		@SuppressWarnings("unchecked")
		Collection<ClientInformationDM> itemIds = (Collection<ClientInformationDM>) tblinform.getVisibleItemIds();
		for (ClientInformationDM saveinform : (Collection<ClientInformationDM>) itemIds) {
			saveinform.setClientid(clientId);
			System.out.println("saveid2-->>" + clientid);
			serviceinformation.saveOrUpdateClientInformDetails(saveinform);
		}
	}
	public void resetfls() {
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
	}
	public void resetfields() {
		tfinfocode.setRequired(false);
        tainfodesc.setRequired(false);
 		tfinfocode.setValue("");
		tfinfocode.setComponentError(null);
		cbstatus.setValue(cbstatus.getItemIds().iterator().next());
		cbstatus.setComponentError(null);
		tainfodesc.setValue("");
		tainfodesc.setComponentError(null);
		tblinform.removeAllItems();
		recordCnt = 0;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (btndelete == event.getButton()) {
			deleteDetails();
			cbstatus.setValue((cbstatus.getItemIds().iterator().next())); 
		}
	}
}
