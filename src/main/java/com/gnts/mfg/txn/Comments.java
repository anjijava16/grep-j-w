/**
 * File Name	:	Comments.java
 * Description	:	This Screen Purpose for Modify the Comments Details.
 * 					Add the Comments details process should be directly added in DB.
 * Author		:	Nandhakumar.S
 * 
 * Copyright (C) 2014 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version         Date           Modified By             Remarks
 * 0.1          09-Oct-2014		  Nandhakumar.S		Initial version 
 */
package com.gnts.mfg.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.mfg.domain.txn.CommentDM;
import com.gnts.mfg.domain.txn.WorkOrderDtlDM;
import com.gnts.mfg.service.txn.CommentService;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractSelect;
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
	private CommentService serviceComment = (CommentService) SpringContextHelper.getBean("mfgComments");
	private BeanItemContainer<CommentDM> beanMfgComments = null;
	private Logger logger = Logger.getLogger(CommentDM.class);
	private TextArea taComments;
	private FormLayout flMainform1, flMainform2;
	private Table tblComments;
	private String userName, strWidth = "160px";
	private Button btnComments, btnEdit;
	private int total = 0;
	List<CommentDM> commentList = new ArrayList<CommentDM>();
	private Long companyId, woOrdid, productDrgid, qaTstId, qcTstId, qaSignOffid, wrkOdrId;
	private Button btnSave = new GERPButton("Add", "add", this);
	VerticalLayout vlTableForm = new VerticalLayout();
	HorizontalLayout hluserInput = new HorizontalLayout();
	private Long empId;
	public Button btnDeleteCmt = new GERPButton("Delete", "delete", this);
	
	public Comments(VerticalLayout vlTableForm, Long companyId, Long woOrdId, Long productDrgId, Long qaTstTypId,
			Long qaSignOffId, Long commentby) {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		empId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		//empId = commentby;
		woOrdid = woOrdId;
		productDrgid = productDrgId;
		qaTstId = qaTstTypId;
		qcTstId = qcTstId;
		qaSignOffid = qaSignOffId;
		commentby = commentby;
		buildview(vlTableForm);
	}
	
	private void buildview(VerticalLayout vlTableForm) {
		vlTableForm.removeAllComponents();
		btnSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnSave == event.getButton()) {
					if (validateComments()) {
						saveMfgCommentsDetails();
					}
				}
			}
		});
		btnDeleteCmt.setEnabled(false);
		btnComments = new GERPButton("Add", "add");
		taComments = new TextArea("Comments");
		taComments.setWidth("250");
		taComments.setHeight("51");
		taComments.setRequired(false);
		flMainform1 = new FormLayout();
		flMainform2 = new FormLayout();
		flMainform1.setSpacing(true);
		flMainform1.addComponent(taComments);
		flMainform2.addComponent(btnSave);
		flMainform2.addComponent(btnDeleteCmt);
		tblComments = new Table();
		tblComments.setStyleName(Runo.TABLE_SMALL);
		tblComments.setWidth("1000");
		tblComments.setPageLength(6);
		tblComments.setSizeUndefined();
		tblComments.setSizeFull();
		tblComments.setInvalidAllowed(true);
		tblComments.setFooterVisible(true);
		tblComments.setColumnCollapsingAllowed(true);
		tblComments.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblComments.isSelected(event.getItemId())) {
					tblComments.setImmediate(true);
					btnSave.setCaption("Add");
					btnSave.setStyleName("savebt");
					resetFields();
					btnDeleteCmt.setEnabled(false);
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnSave.setCaption("Update");
					btnSave.setStyleName("savebt");
					editcommentDetails();
					btnDeleteCmt.setEnabled(true);
				}
			}
		});
		setTableProperties();
		hluserInput.addComponent(flMainform1);
		hluserInput.addComponent(flMainform2);
		hluserInput.setMargin(true);
		hluserInput.setSpacing(true);
		vlTableForm.setSizeFull();
		vlTableForm.addComponent(hluserInput);
		//
		VerticalLayout vlComments = new VerticalLayout();
		vlComments.addComponent(hluserInput);
		vlComments.addComponent(tblComments);
		vlTableForm.addComponent(vlComments);
		tblComments.setVisible(true);
		loadsrch(false, null, null, null, null, null, null);
	}
	
	public void loadsrch(boolean fromdb, Long commentId, Long companyId, Long woId, Long productDrgId, Long qaTstTypId,
			Long qaSignOffId) {
		if (fromdb) {
			commentList = serviceComment.getMFGCommentsDetails(commentId, companyId, woId, productDrgId, qaTstTypId,qaSignOffId);
			System.out.println("loadcommentId---->" + commentId);
		}
		try {
			tblComments.removeAllItems();
			System.out.println("companyId---->" + companyId);
			total = commentList.size();
			System.out.println("LISTSIZE---1->" + commentList.size());
			beanMfgComments = new BeanItemContainer<CommentDM>(CommentDM.class);
			beanMfgComments.addAll(commentList);
			tblComments.setSelectable(true);
			tblComments.setContainerDataSource(beanMfgComments);
			tblComments.setColumnAlignment("commnetId", Align.RIGHT);
			tblComments.addItem(taComments);
			tblComments.setVisibleColumns(new Object[] { "commentId", "comments", "commentDt", "lastUpdatedDt",
					"lastUpdatedBY" });
			tblComments.setColumnHeaders(new String[] { "Ref.Id", "Comments", "Comment Dt.", "Last Updated Dt.",
					"Last Updated By" });
			tblComments.setColumnAlignment("commentId", Align.RIGHT);
			tblComments.setColumnFooter("lastUpdatedBY", "No.of Records : " + total);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	public void editcommentDetails() {
		try {
			if (tblComments.getValue() != null) {
				CommentDM editcomment = beanMfgComments.getItem(tblComments.getValue()).getBean();
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
	
	public void saveMfgCommentsDetails() {
		logger.info(" Inside og Mfg Comments save details >>>>>>>>>>>");
		CommentDM saveMfgComments = new CommentDM();
		if (tblComments.getValue() != null) {
			saveMfgComments = beanMfgComments.getItem(tblComments.getValue()).getBean();
			commentList.remove(saveMfgComments);
		}
		saveMfgComments.setCompanyId(companyId);
		saveMfgComments.setQaTstId(qaTstId);
		saveMfgComments.setQcTstId(qcTstId);
		saveMfgComments.setWoId(woOrdid);
		saveMfgComments.setProductDrgId(productDrgid);
		saveMfgComments.setQaSignOffId(qaSignOffid);
		saveMfgComments.setUserAction(null);
		saveMfgComments.setComments(taComments.getValue());
		saveMfgComments.setCommentDt(DateUtils.getcurrentdate());
		saveMfgComments.setLastUpdatedBY(userName);
		saveMfgComments.setLastUpdatedDt(DateUtils.getcurrentdate());
		commentList.add(saveMfgComments);
		logger.info(" Inside og Mfg Comments save details 2 >>>>>>>>>>>");
		resetFields();
		loadsrch(false, null, null, null, null, null, null);
		btnSave.setCaption("Add");
	}
	
	public void saveWoId(Long woId) {
		System.out.println("woId-->>" + woId);
		@SuppressWarnings("unchecked")
		Collection<CommentDM> itemIds = (Collection<CommentDM>) tblComments.getVisibleItemIds();
		for (CommentDM savecomments : (Collection<CommentDM>) itemIds) {
			savecomments.setWoId(woId);
			System.out.println("saveid2-->>" + woId);
			serviceComment.saveOrUpdateMFGCommentsDetails(savecomments);
		}
	}
	
	public void saveProdDrgId(Long productDrgId) {
		System.out.println("productDrgId-->>" + productDrgId);
		@SuppressWarnings("unchecked")
		Collection<CommentDM> itemIds = (Collection<CommentDM>) tblComments.getVisibleItemIds();
		for (CommentDM savecomments : (Collection<CommentDM>) itemIds) {
			savecomments.setProductDrgId(productDrgId);
			System.out.println("saveid2-->>" + productDrgId);
			serviceComment.saveOrUpdateMFGCommentsDetails(savecomments);
		}
	}
	
	public void saveqaTst(Long qaTstId) {
		System.out.println("qaTstId-->>" + qaTstId);
		@SuppressWarnings("unchecked")
		Collection<CommentDM> itemIds = (Collection<CommentDM>) tblComments.getVisibleItemIds();
		for (CommentDM savecomments : (Collection<CommentDM>) itemIds) {
			savecomments.setQaTstId(qaTstId);
			System.out.println("qaTstId-->>" + qaTstId);
			serviceComment.saveOrUpdateMFGCommentsDetails(savecomments);
		}
		resetFields();
	}
	
	public void save(Long qcTstId) {
		System.out.println("save qcTstId->>" + qcTstId);
		@SuppressWarnings("unchecked")
		Collection<CommentDM> itemIds = (Collection<CommentDM>) tblComments.getVisibleItemIds();
		for (CommentDM savecomments : (Collection<CommentDM>) itemIds) {
			savecomments.setQcTstId(qcTstId);
			System.out.println("save qcTstId-->>" + qcTstId);
			serviceComment.saveOrUpdateMFGCommentsDetails(savecomments);
		}
	}
	
	public void saveqaSignOffId(Long qaSignOffId) {
		System.out.println("save qaSignOffId->>" + qaSignOffId);
		@SuppressWarnings("unchecked")
		Collection<CommentDM> itemIds = (Collection<CommentDM>) tblComments.getVisibleItemIds();
		for (CommentDM savecomments : (Collection<CommentDM>) itemIds) {
			savecomments.setQaSignOffId(qaSignOffId);
			System.out.println("save qaSignOffId-->>" + qaSignOffId);
			serviceComment.saveOrUpdateMFGCommentsDetails(savecomments);
		}
	}
	
	private boolean validateComments() {
		boolean valid = true;
		if (taComments.getValue() == "" || taComments.getValue().trim().length() == 0) {
			taComments.setComponentError(new UserError("Enter Comments"));
			valid = false;
			taComments.setRequired(true);
		} else {
			taComments.setComponentError(null);
		}
		return valid;
	}
	
	private void setTableProperties() {
		tblComments.setColumnAlignment("commentId", Align.RIGHT);
		tblComments.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblComments.isSelected(event.getItemId())) {
					/*
					 * btnEdit.setEnabled(false); } else { btnEdit.setEnabled(true); } if
					 * (tblComments.isSelected(event.getItemId())) {
					 */
					btnComments.setEnabled(true);
				} else {
					btnComments.setEnabled(false);
				}
			}
		});
	}
	
	public void resetFields() {
		taComments.setValue("");
		btnSave.setComponentError(null);
		total = 0;
		taComments.setRequired(false);
		loadsrch(false, null, companyId, null, null, null, null);
	}
	public void resettbl() {
		// commentList= new ArrayList<CommentDM>();
		commentList = new ArrayList<CommentDM>();
		tblComments.removeAllItems();
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		if (btnDeleteCmt == event.getButton()) {
			deleteTstDefDetails();
		}
	}
	
	private void deleteTstDefDetails() {
		CommentDM commentDM = new CommentDM();
		if (tblComments.getValue() != null) {
			commentDM = beanMfgComments.getItem(tblComments.getValue()).getBean();
			commentList.remove(commentDM);
			resetFields();
			tblComments.setValue("");
			loadsrch(false, null, null, null, null, null, null);
			btnDeleteCmt.setEnabled(false);
		}
	}
}
