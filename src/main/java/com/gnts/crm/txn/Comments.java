/**
 * File Name 		: ClientCommentsApp.java 
 * Description 		: this class is used for add/edit Client  details. 
 * Author 			: P Sekhar
 * Date 			: Mar 28, 2014
 * Modification 	:
 * Modified By 		: 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * 
 * 
 */
package com.gnts.crm.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.crm.domain.txn.CommentsDM;
import com.gnts.crm.service.txn.CommentsService;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class Comments implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CommentsService serviceComment = (CommentsService) SpringContextHelper.getBean("clientComments");
	private Table tblClntComments = null;
	private Long companyId;
	private Button btnComments, btnEdit;
	private VerticalLayout vlTableForm;
	private FormLayout flMainform1, flMainform2;
	private String userName, strWidth = "160px";
	private int total = 0;
	List<CommentsDM> commentList = new ArrayList<CommentsDM>();
	private Long leadId, clntContactId, clientId, campaignId, clntCaseId, clntOppertunityId, empId;
	private TextArea taComments;
	private BeanItemContainer<CommentsDM> beanClntComments = null;
	private Logger logger = Logger.getLogger(CommentsDM.class);
	private Button btnSave = new GERPButton("Add", "add", this);
	private Button btndelete=new GERPButton("Delete","delete",this);
	
	public Comments(VerticalLayout vlCommetTblLayout, Long employeeid, Long oppertunityId, Long clntLeadId,
			Long clientid, Long contactId, Long clntCampaingId, Long caseId) {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		empId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		empId = employeeid;
		clntOppertunityId = oppertunityId;
		leadId = clntLeadId;
		clientId = clientid;
		clntContactId = contactId;
		campaignId = clntCampaingId;
		clntCaseId = caseId;
		buildview(vlCommetTblLayout);
	}
	
	/**
	 * buildMainview()-->for screen UI design
	 * 
	 * @param clArgumentLayout
	 * @param hlHeaderLayout
	 */
	private void buildview(VerticalLayout vlCommetTblLayout) {
		// TODO Auto-generated method stub
		vlCommetTblLayout.removeAllComponents();
		btnSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnSave == event.getButton()) {
					saveClientCommentsDetails();
				}
			}
		});
		btnSave.setStyleName("add");
		btnEdit = new Button("Edit", this);
		btnEdit.setEnabled(false);
		btnEdit.addStyleName("editbt");
		btnComments = new Button(" Add", this);
		btnComments.addStyleName("add");
		taComments = new TextArea("Comments");
		taComments.setRequired(true);
		taComments.setWidth(strWidth);
		taComments.setHeight("50px");
		/**
		 * add fields to header layout
		 */
		/**
		 * add fields to form Layout
		 */
		flMainform1 = new FormLayout();
		flMainform2 = new FormLayout();
		flMainform1.setSpacing(true);
		flMainform1.addComponent(taComments);
		flMainform2.addComponent(btnSave);
		flMainform2.addComponent(btndelete);
		/**
		 * declare the table and add in panel
		 */
		tblClntComments = new Table();
		tblClntComments.setStyleName(Runo.TABLE_SMALL);
		tblClntComments.setWidth("1000");
		tblClntComments.setPageLength(5);
		tblClntComments.setSizeUndefined();
		tblClntComments.setSizeFull();
		tblClntComments.setInvalidAllowed(true);
		tblClntComments.setFooterVisible(true);
		tblClntComments.setColumnCollapsingAllowed(true);
		tblClntComments.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblClntComments.isSelected(event.getItemId())) {
					tblClntComments.setImmediate(true);
					btnSave.setCaption("Add");
					btnSave.setStyleName("addbt");
					taComments.setValue("");
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnSave.setCaption("Update");
					btnSave.setStyleName("savebt");
					editcommentDetails();
				}
			}
		});
		HorizontalLayout hluserInput = new HorizontalLayout();
		hluserInput.addComponent(flMainform1);
		hluserInput.addComponent(flMainform2);
		hluserInput.setMargin(true);
		hluserInput.setSpacing(true);
		hluserInput.setMargin(true);
		hluserInput.setComponentAlignment(flMainform2, Alignment.MIDDLE_RIGHT);
		vlTableForm = new VerticalLayout();
		vlTableForm.setSizeFull();
		vlTableForm.setMargin(true);
		vlTableForm.setSpacing(true);
		vlTableForm.addComponent(hluserInput);
		/**
		 * add search , table and add/edit panel in main layout
		 */
		HorizontalLayout hlButton = new HorizontalLayout();
		hlButton.addComponent(vlTableForm);
		hlButton.addComponent(tblClntComments);
		hlButton.setSpacing(true);
		vlCommetTblLayout.addComponent(hlButton);
		setTableProperties();
		loadsrch(false, null, null, null, null, null, null);
	}
	
	public void editcommentDetails() {
		try {
			if (tblClntComments.getValue() != null) {
				CommentsDM editcomment = beanClntComments.getItem(tblClntComments.getValue()).getBean();
				if (editcomment.getComments() != null) {
					taComments.setValue(editcomment.getComments());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Edit is nnot workinghbhhhhhhhhhhhhh");
		}
	}
	
	/**
	 * loadsrch()-->this function used to load the list to the table if(search==true)--> it performs search operation
	 * else it loads all values
	 * 
	 * @param search
	 */
	public void loadsrch(boolean fromdb, Long clientId, Long clntContactId, Long campaingnId, Long leadId,
			Long oppertunuityID, Long caseId) {
		if (fromdb) {
			commentList = serviceComment.getClientCommentsDetails(companyId, clientId, clntContactId, leadId,
					campaingnId, oppertunuityID, caseId);
			System.out.println("loadsrchoppertunity---->" + oppertunuityID);
		}
		try {
			tblClntComments.removeAllItems();
			System.out.println("CONTACT---->" + clntContactId);
			total = commentList.size();
			System.out.println("LISTSIZE---1->" + commentList.size());
			beanClntComments = new BeanItemContainer<CommentsDM>(CommentsDM.class);
			beanClntComments.addAll(commentList);
			tblClntComments.setSelectable(true);
			tblClntComments.setContainerDataSource(beanClntComments);
			tblClntComments.setColumnAlignment("commnetId", Align.RIGHT);
			tblClntComments.addItem(taComments);
			tblClntComments.setVisibleColumns(new Object[] { "commnetId", "comments", "commentDt", "lastUpdatedDt",
					"lastUpdatedBy" });
			tblClntComments.setColumnHeaders(new String[] { "Ref.Id", "Comments", "Comment Date", "Last Updated Date",
					"Last Updated By" });
			tblClntComments.setColumnFooter("lastUpdatedBy", "No.of Records : " + total);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	/**
	 * resetFields()->this method is used for reset the add/edit UI components
	 */
	private void setTableProperties() {
		tblClntComments.setColumnAlignment("commentId", Align.RIGHT);
		tblClntComments.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblClntComments.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
				} else {
					btnEdit.setEnabled(true);
				}
				if (tblClntComments.isSelected(event.getItemId())) {
					btnComments.setEnabled(true);
				} else {
					btnComments.setEnabled(false);
				}
			}
		});
	}
	
	public void resetfields() {
		taComments.setValue("");
		btnSave.setComponentError(null);
		tblClntComments.removeAllItems();
		// /commentList=new ArrayList<CommentsDM>();
		total = 0;
	}
	
	private void deletedetails() {
		CommentsDM delete = new CommentsDM();
		if (tblClntComments.getValue() != null) {
			delete=beanClntComments.getItem(tblClntComments.getValue()).getBean();
			commentList.remove(delete);
			taComments.setValue("");
			loadsrch(false, null, null, null, null, null, null);
		}
	}
	
	/**
	 * saveClientCommentsDetails()-->this method is used for save/update the records
	 */
	public void saveClientCommentsDetails() {
		validateAll();
		CommentsDM saveClntComments = new CommentsDM();
		if (tblClntComments.getValue() != null) {
			saveClntComments = beanClntComments.getItem(tblClntComments.getValue()).getBean();
			commentList.remove(saveClntComments);
		}
		if (taComments != null) {
			saveClntComments.setComments(taComments.getValue());
		}
		saveClntComments.setClientId(clientId);
		saveClntComments.setCampaignId(campaignId);
		saveClntComments.setClientCaseId(clntCaseId);
		saveClntComments.setCommentedBy(empId);
		saveClntComments.setCompanyId(companyId);
		saveClntComments.setContactId(clntContactId);
		saveClntComments.setLeadId(leadId);
		saveClntComments.setOppertunityId(clntOppertunityId);
		saveClntComments.setLastUpdatedBy(userName);
		saveClntComments.setLastUpdatedDt(DateUtils.getcurrentdate());
		saveClntComments.setCommentDt(DateUtils.getcurrentdate());
		if (taComments.isValid()) {
			commentList.add(saveClntComments);
			resetfields();
			loadsrch(false, null, null, null, null, null, null);
		}
		btnSave.setCaption("Add");
	}
	
	public void save(Long clientId) {
		System.out.println("saveid1-->>" + clientId);
		@SuppressWarnings("unchecked")
		Collection<CommentsDM> itemIds = (Collection<CommentsDM>) tblClntComments.getVisibleItemIds();
		for (CommentsDM savecomments : (Collection<CommentsDM>) itemIds) {
			savecomments.setClientId(clientId);
			System.out.println("saveid2-->>" + clientId);
			serviceComment.saveOrUpdateClientCommentsDetails(savecomments);
		}
	}
	
	public void savecontact(Long contactId) {
		System.out.println("saveid1-->>" + contactId);
		@SuppressWarnings("unchecked")
		Collection<CommentsDM> itemIds = (Collection<CommentsDM>) tblClntComments.getVisibleItemIds();
		for (CommentsDM savecontacts : (Collection<CommentsDM>) itemIds) {
			savecontacts.setContactId(contactId);
			System.out.println("saveid2-->>" + contactId);
			serviceComment.saveOrUpdateClientCommentsDetails(savecontacts);
		}
	}
	
	public void savecampaign(Long campaingnId) {
		System.out.println("saveid1-->>" + campaingnId);
		@SuppressWarnings("unchecked")
		Collection<CommentsDM> itemIds = (Collection<CommentsDM>) tblClntComments.getVisibleItemIds();
		for (CommentsDM savecampaign : (Collection<CommentsDM>) itemIds) {
			savecampaign.setCampaignId(campaingnId);
			System.out.println("saveid2-->>" + campaingnId);
			serviceComment.saveOrUpdateClientCommentsDetails(savecampaign);
		}
	}
	
	public void saveLeads(Long leadId) {
		System.out.println("saveid1-->>" + leadId);
		@SuppressWarnings("unchecked")
		Collection<CommentsDM> itemIds = (Collection<CommentsDM>) tblClntComments.getVisibleItemIds();
		for (CommentsDM saveleads : (Collection<CommentsDM>) itemIds) {
			saveleads.setLeadId(leadId);
			System.out.println("saveid2-->>" + leadId);
			serviceComment.saveOrUpdateClientCommentsDetails(saveleads);
		}
	}
	
	public void saveclientcases(Long caseId) {
		System.out.println("saveid1-->>" + caseId);
		@SuppressWarnings("unchecked")
		Collection<CommentsDM> itemIds = (Collection<CommentsDM>) tblClntComments.getVisibleItemIds();
		for (CommentsDM savecases : (Collection<CommentsDM>) itemIds) {
			savecases.setClientCaseId(caseId);
			System.out.println("saveid2-->>" + caseId);
			serviceComment.saveOrUpdateClientCommentsDetails(savecases);
		}
	}
	
	public void saveclientoppertunuity(Long oppertunityId) {
		System.out.println("saveid1-->>" + oppertunityId);
		@SuppressWarnings("unchecked")
		Collection<CommentsDM> itemIds = (Collection<CommentsDM>) tblClntComments.getVisibleItemIds();
		for (CommentsDM saveoppurtunuity : (Collection<CommentsDM>) itemIds) {
			saveoppurtunuity.setOppertunityId(oppertunityId);
			System.out.println("saveid2-->>" + oppertunityId);
			serviceComment.saveOrUpdateClientCommentsDetails(saveoppurtunuity);
		}
	}
	
	private boolean validateAll() {
		boolean valid = true;
		try {
			taComments.validate();
			taComments.setComponentError(null);
		}
		catch (Exception e) {
			logger.info("validaAll :comments is empty--->" + e);
			taComments.setComponentError(new UserError("Enter Comments"));
			valid = false;
		}
		return valid;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if(btndelete==event.getButton())
		{
			deletedetails();
		}
	}
}
