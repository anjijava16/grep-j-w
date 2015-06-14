/**
 * File Name 		: MmsComments.java 
 * Description 		:This Screen Purpose for Modify the PurchasePO Details.
 * 					 Add the PurchasePO details process should be directly added in DB.
 * Author 			: Karthikeyan R
 * Date 			: Oct 25, 2014

 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version      Date               Modified By            Remarks
 * 0.1          Oct 25, 2014       Karthikeyan R          Initial  Version
 */
package com.gnts.mms.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.rewrite.ITrackedNodePosition;
import com.gnts.asm.domain.txn.AssetDetailsDM;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mms.domain.txn.MmsCommentsDM;
import com.gnts.mms.service.txn.MmsCommentsService;
import com.gnts.sms.domain.txn.SmsCommentsDM;
import com.google.gwt.thirdparty.streamhtmlparser.util.EntityResolver.Status;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.themes.Runo;

public class MmsComments implements ClickListener {
	private static final long serialVersionUID = 1L;
	private MmsCommentsService serviceComments = (MmsCommentsService) SpringContextHelper.getBean("mmscomments");
	private EmployeeService serviceemployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private Table tblClntComments;
	private HorizontalLayout hluserInput = new HorizontalLayout();;
	private Long companyid;
	private Button btnComments, btnEdit;
	private FormLayout flMainform1, flMainform2;
	private String userName, strWidth = "160px";
	private int total = 0;
	List<MmsCommentsDM> commentList = new ArrayList<MmsCommentsDM>();
	VerticalLayout vlTableForm = new VerticalLayout();
	private TextArea taComments;
	private BeanItemContainer<MmsCommentsDM> beanComment = null;
	private Logger logger = Logger.getLogger(MmsCommentsDM.class);
	private Button btnSave = new GERPButton("Add", "addbt", this);
	private Long commentId, bomId, enquiryId, quoteId, poId, indentId, DcId, gatepassid, empid;
	private String status;
	
	public MmsComments(VerticalLayout vlTableForm, Long commentid, Long companyid, Long bomid, Long enquiryid,
			Long quoteid, Long poid, Long indentid, Long dcId, Long gatepassId, String status) {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		empid = ((Long) UI.getCurrent().getSession().getAttribute("employeeId"));
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		commentId = commentid;
		bomId = bomid;
		enquiryId = enquiryid;
		quoteId = quoteid;
		poId = poid;
		indentId = indentid;
		DcId = dcId;
		gatepassid = gatepassId;
		status = status;
		buildview(vlTableForm);
	}
	
	/**
	 * buildMainview()-->for screen UI design
	 * 
	 * @param clArgumentLayout
	 * @param hlHeaderLayout
	 */
	private void buildview(VerticalLayout vlTableForm) {
		vlTableForm.removeAllComponents();
		btnSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (validateComments()) {
					saveClientCommentsDetails();
				}
			}
		});
		btnSave.setStyleName("savebt");
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
		/**
		 * declare the table and add in panel
		 */
		tblClntComments = new Table();
		tblClntComments.setStyleName(Runo.TABLE_SMALL);
		tblClntComments.setWidth("1000");
		tblClntComments.setPageLength(6);
		tblClntComments.setSizeFull();
		tblClntComments.setFooterVisible(true);
		tblClntComments.setInvalidAllowed(true);
		tblClntComments.setColumnCollapsingAllowed(true);
		tblClntComments.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblClntComments.isSelected(event.getItemId())) {
					tblClntComments.setImmediate(true);
					btnSave.setCaption("Add");
					btnSave.setStyleName("savebt");
					resetfieldcm();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnSave.setCaption("Update");
					btnSave.setStyleName("savebt");
					editcommentDetails();
				}
			}
		});
		// setTableProperties();
		// HorizontalLayout hluserInput = new HorizontalLayout();
		hluserInput.addComponent(flMainform1);
		hluserInput.addComponent(flMainform2);
		hluserInput.setMargin(true);
		hluserInput.setSpacing(true);
		hluserInput.setMargin(true);
		vlTableForm.setSizeFull();
		vlTableForm.setMargin(true);
		vlTableForm.setSpacing(true);
		vlTableForm.addComponent(hluserInput);
		VerticalLayout vlcomment = new VerticalLayout();
		vlcomment.addComponent(hluserInput);
		vlcomment.addComponent(tblClntComments);
		vlTableForm.addComponent(vlcomment);
		tblClntComments.setVisible(true);
		loadsrch(false, null, null, null, null, null, null, null, null, null);
	}
	
	public void editcommentDetails() {
		resetfieldcm();
		System.out.println("Edit is workinghbhhhhhhhhhhhhh");
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Editing the selected record");
		Item CmtsRcd = tblClntComments.getItem(tblClntComments.getValue());
		logger.info("Company ID : " + companyid + " | User Name : " + userName + " > " + "Selected enquiry.Id -> "
				+ enquiryId);
		if (CmtsRcd != null) {
			MmsCommentsDM editcomment = beanComment.getItem(tblClntComments.getValue()).getBean();
			commentId = editcomment.getCommentid();
			if (editcomment.getComments() != null) {
				taComments.setValue(editcomment.getComments());
			}
		}
	}
	
	/**
	 * loadsrch()-->this function used to load the list to the table if(search==true)--> it performs search operation
	 * else it loads all values
	 * 
	 * @param search
	 */
	public void loadsrch(boolean fromdb, Long commentid, Long companyid, Long bomid, Long enquiryid, Long quoteid,
			Long poid, Long indentid, Long dcid, Long gatepassid) {
		if (fromdb) {
			Long empfirst = null;
			commentList = serviceComments.getmmscommentsList(commentid, empfirst, companyid, bomid, enquiryid, quoteid,
					poid, indentid, dcid, gatepassid);
			System.out.println("loadsrchoppertunity---->" + commentId);
		}
		try {
			tblClntComments.removeAllItems();
			total = commentList.size();
			System.out.println("LISTSIZE---->" + commentList.size());
			beanComment = new BeanItemContainer<MmsCommentsDM>(MmsCommentsDM.class);
			beanComment.addAll(commentList);
			tblClntComments.setSelectable(true);
			tblClntComments.setContainerDataSource(beanComment);
			tblClntComments
					.setVisibleColumns(new Object[] { "comments", "commentDate", "lastUpdtDate", "lastUpdatedBy" });
			tblClntComments.setColumnHeaders(new String[] { "Comment", "Comment Date", "Last Updated Date",
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
		tblClntComments.setColumnAlignment("commentid", Align.RIGHT);
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
		taComments.setComponentError(null);
		tblClntComments.removeAllItems();
		/*
		 * btnSave.setComponentError(null); tblClntComments.removeAllItems(); total = 0;
		 */
	}
	
	private void resetfieldcm() {
		taComments.setValue("");
		taComments.setComponentError(null);
		btnSave.setComponentError(null);
	}
	
	/**
	 * saveClientCommentsDetails()-->this method is used for save/update the records
	 */
	public void saveClientCommentsDetails() {
		validateComments();
		MmsCommentsDM saveComments = new MmsCommentsDM();
		if (tblClntComments.getValue() != null) {
			saveComments = beanComment.getItem(tblClntComments.getValue()).getBean();
			commentList.remove(saveComments);
		}
		if (taComments != null) {
			saveComments.setComments(taComments.getValue());
		}
		// saveComments.setCommentBy(((EmployeeDM) cbCommentBy.getValue()).getEmployeeid());
		saveComments.setQuoteid(quoteId);
		saveComments.setCompanyid(companyid);
		saveComments.setBomid(bomId);
		saveComments.setPoid(poId);
		saveComments.setIndentid(indentId);
		saveComments.setDcid(DcId);
		saveComments.setGatepassid(gatepassid);
		if (taComments != null) {
			saveComments.setComments(taComments.getValue());
		}
		saveComments.setCommentBy(empid);
		saveComments.setCommentDate(DateUtils.getcurrentdate());
		saveComments.setLastUpdatedBy(userName);
		saveComments.setLastUpdtDate(DateUtils.getcurrentdate());
		if (taComments.isValid()) {
			commentList.add(saveComments);
			resetfields();
			loadsrch(false, null, null, null, null, null, null, null, null, null);
		}
		btnSave.setCaption("Add");
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
	}
	
	public void saveEnquiry(Long enquiryId, String status) {
		System.out.println("saveid1-->>" + enquiryId);
		@SuppressWarnings("unchecked")
		Collection<MmsCommentsDM> itemIds = (Collection<MmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (MmsCommentsDM savecomments : (Collection<MmsCommentsDM>) itemIds) {
			savecomments.setEnquiryid(enquiryId);
			savecomments.setUseraction(status);
			System.out.println("saveid2-->>" + enquiryId);
			serviceComments.saveOrUpdateComments(savecomments);
		}
	}
	
	public void saveQuote(Long quoteId, String status) {
		System.out.println("saveid1-->>" + quoteId);
		@SuppressWarnings("unchecked")
		Collection<MmsCommentsDM> itemIds = (Collection<MmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (MmsCommentsDM saveQuote : (Collection<MmsCommentsDM>) itemIds) {
			saveQuote.setQuoteid(quoteId);
			saveQuote.setUseraction(status);
			System.out.println("saveid2-->>" + quoteId);
			serviceComments.saveOrUpdateComments(saveQuote);
		}
	}
	
	public void savePurchaseOrder(Long poId, String status) {
		System.out.println("saveid1-->>" + poId);
		@SuppressWarnings("unchecked")
		Collection<MmsCommentsDM> itemIds = (Collection<MmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (MmsCommentsDM savepo : (Collection<MmsCommentsDM>) itemIds) {
			savepo.setPoid(poId);
			savepo.setUseraction(status);
			System.out.println("saveid2-->>" + poId);
			serviceComments.saveOrUpdateComments(savepo);
		}
	}
	
	public void savedc(Long dcId, String status) {
		System.out.println("saveid1-->>" + dcId);
		@SuppressWarnings("unchecked")
		Collection<MmsCommentsDM> itemIds = (Collection<MmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (MmsCommentsDM savedc : (Collection<MmsCommentsDM>) itemIds) {
			savedc.setDcid(dcId);
			savedc.setUseraction(status);
			System.out.println("saveid2-->>" + dcId);
			serviceComments.saveOrUpdateComments(savedc);
		}
	}
	
	public void savegatepass(Long gatepassId, String status) {
		System.out.println("saveid1-->>" + gatepassId);
		@SuppressWarnings("unchecked")
		Collection<MmsCommentsDM> itemIds = (Collection<MmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (MmsCommentsDM savegatepass : (Collection<MmsCommentsDM>) itemIds) {
			savegatepass.setGatepassid(gatepassId);
			savegatepass.setUseraction(status);
			System.out.println("saveid2-->>" + gatepassId);
			serviceComments.saveOrUpdateComments(savegatepass);
		}
	}
	
	public void saveindent(Long indentId, String status) {
		System.out.println("saveid1-->>" + indentId);
		@SuppressWarnings("unchecked")
		Collection<MmsCommentsDM> itemIds = (Collection<MmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (MmsCommentsDM saveindent : (Collection<MmsCommentsDM>) itemIds) {
			saveindent.setIndentid(indentId);
			saveindent.setUseraction(status);
			System.out.println("saveid2-->>" + indentId);
			serviceComments.saveOrUpdateComments(saveindent);
		}
	}
	
	public void savebom(Long bomId, String status) {
		System.out.println("saveid1-->>" + bomId);
		@SuppressWarnings("unchecked")
		Collection<MmsCommentsDM> itemIds = (Collection<MmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (MmsCommentsDM savebom : (Collection<MmsCommentsDM>) itemIds) {
			savebom.setBomid(bomId);
			savebom.setUseraction(status);
			System.out.println("saveid2-->>" + bomId);
			serviceComments.saveOrUpdateComments(savebom);
		}
	}
	
	private boolean validateComments() {
		boolean valid = true;
		if ((taComments.getValue() == null) || taComments.getValue().trim().length() == 0) {
			taComments.setComponentError(new UserError("Enter Comments"));
			valid = false;
		} else {
			taComments.setComponentError(null);
		}
		return valid;
	}
}
