/**
 * File Name 		: SmsComments.java 
 * Description 		:This Screen Purpose for Modify the PurchasePO Details.
 * 					 Add the PurchasePO details process should be directly added in DB.
 * Author 			: Ganga .S
 * Date 			: Oct 14, 2014

 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1         Oct 14, 2014       GANGA                  Initial  Version
 */
package com.gnts.sms.txn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.sms.domain.txn.SmsCommentsDM;
import com.gnts.sms.service.txn.SmsCommentsService;
import com.vaadin.data.Item;
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

public class SmsComments implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SmsCommentsService serviceComment = (SmsCommentsService) SpringContextHelper.getBean("smsComments");
	private Table tblClntComments;
	private Long companyId;
	private Button btnComments, btnEdit;
	private FormLayout flMainform1, flMainform2, flMainform4;
	private String userName, strWidth = "160px";
	private int total = 0;
	public List<SmsCommentsDM> commentList = new ArrayList<SmsCommentsDM>();
	public VerticalLayout vlTableForm = new VerticalLayout();
	private TextArea taComments;
	public Long commentBy;
	private BeanItemContainer<SmsCommentsDM> beanComment = null;
	private Logger logger = Logger.getLogger(SmsCommentsDM.class);
	private Button btnSave = new GERPButton("Add", "addbt", this);
	private Long comentsId, enquiryId, empId;
	public String status;
	
	public SmsComments(VerticalLayout vlTableForm, Long commentId, Long companyId, Long PurEnquiryId, Long purQuoteID,
			Long poId, Long receiptId, Long billId, Long SalesEnqId, Long salesQuoteId, Long salesPoId, Long InvoiceId,
			String status) {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		empId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		comentsId = commentId;
		enquiryId = PurEnquiryId;
		buildview(vlTableForm);
	}
	
	/**
	 * buildMainview()-->for screen UI design
	 * 
	 * @param clArgumentLayout
	 * @param hlHeaderLayout
	 */
	private void buildview(VerticalLayout vlTableForm) {
		// TODO Auto-generated method stub
		vlTableForm.removeAllComponents();
		btnSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if (btnSave == event.getButton()) {
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
		flMainform1 = new FormLayout();
		flMainform2 = new FormLayout();
		flMainform4 = new FormLayout();
		flMainform1.setSpacing(true);
		flMainform1.addComponent(taComments);
		flMainform4.addComponent(btnSave);
		/**
		 * declare the table and add in panel
		 */
		tblClntComments = new Table();
		tblClntComments.setStyleName(Runo.TABLE_SMALL);
		tblClntComments.setWidth("1000");
		tblClntComments.setPageLength(5);
		tblClntComments.setFooterVisible(true);
		tblClntComments.setColumnCollapsingAllowed(true);
		tblClntComments.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblClntComments.isSelected(event.getItemId())) {
					tblClntComments.setImmediate(true);
					btnSave.setCaption("Add");
					btnSave.setStyleName("savebt");
					resetfields();
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
		hluserInput.addComponent(flMainform4);
		hluserInput.setMargin(true);
		hluserInput.setSpacing(true);
		hluserInput.setMargin(true);
		vlTableForm.setSizeFull();
		vlTableForm.setMargin(true);
		vlTableForm.setSpacing(true);
		vlTableForm.addComponent(hluserInput);
		/**
		 * add search , table and add/edit panel in main layout /
		 */
		VerticalLayout vlComments = new VerticalLayout();
		vlComments.addComponent(hluserInput);
		vlComments.addComponent(tblClntComments);
		vlTableForm.addComponent(vlComments);
		tblClntComments.setVisible(true);
		loadsrch(false, null, null, null, null, null, null, null, null, null, null, null, null);
	}
	
	public void editcommentDetails() {
		System.out.println("Edit is nnot workinghbhhhhhhhhhhhhh");
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Editing the selected record");
		Item sltedRcd = tblClntComments.getItem(tblClntComments.getValue());
		logger.info("Company ID : " + companyId + " | User Name : " + userName + " > " + "Selected enquiry.Id -> "
				+ enquiryId);
		if (sltedRcd != null) {
			SmsCommentsDM editcomment = beanComment.getItem(tblClntComments.getValue()).getBean();
			comentsId = editcomment.getCommentId();
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
	public void loadsrch(boolean fromdb, Long commentId, Long companyId, Long PurEnquiryId, Long purQuoteID, Long poId,
			Long receiptId, Long billId, Long SalesEnqId, Long salesQuoteId, Long salesPoId, Long InvoiceId,
			Long commenedBy) {
		if (fromdb) {
			commentList = serviceComment.getSmsCommentsList(commentId, companyId, PurEnquiryId, purQuoteID, poId,
					receiptId, billId, SalesEnqId, salesQuoteId, salesPoId, InvoiceId, null);
		}
		try {
			tblClntComments.removeAllItems();
			total = commentList.size();
			beanComment = new BeanItemContainer<SmsCommentsDM>(SmsCommentsDM.class);
			beanComment.addAll(commentList);
			tblClntComments.setSelectable(true);
			tblClntComments.setContainerDataSource(beanComment);
			tblClntComments.setColumnAlignment("commnetId", Align.RIGHT);
			tblClntComments.setVisibleColumns(new Object[] { "comments", "userActiopn", "empName", "commentDate",
					"lastUpdtDate", "lastUpdatedBy" });
			tblClntComments.setColumnHeaders(new String[] { "Comment", "User Action", "Comment By", "Comment Date",
					"Last Updated Date", "Last Updated By" });
			tblClntComments.setColumnFooter("lastUpdatedBy", "No.of Records : " + total);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	public void resetfields() {
		taComments.setValue("");
	}
	
	/**
	 * saveClientCommentsDetails()-->this method is used for save/update the records
	 */
	public void saveClientCommentsDetails() {
		try {
			validateAll();
			SmsCommentsDM saveClntComments = new SmsCommentsDM();
			if (tblClntComments.getValue() != null) {
				saveClntComments = beanComment.getItem(tblClntComments.getValue()).getBean();
				commentList.remove(saveClntComments);
			}
			if (taComments != null) {
				saveClntComments.setComments(taComments.getValue());
			}
			saveClntComments.setCommentId(comentsId);
			saveClntComments.setCompanyId(companyId);
			saveClntComments.setComments(taComments.getValue());
			saveClntComments.setCommentBy(empId);
			saveClntComments.setUserActiopn(status);
			saveClntComments.setCommentDate(DateUtils.getcurrentdate());
			saveClntComments.setLastUpdatedBy(userName);
			saveClntComments.setLastUpdtDate(DateUtils.getcurrentdate());
			if (taComments.isValid()) {
				commentList.add(saveClntComments);
				resetfields();
				loadsrch(false, null, null, null, null, null, null, null, null, null, null, null, null);
			}
			btnSave.setCaption("Add");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveEnquiry(Long puchaseEnqId, String status) {
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM savecomments : (Collection<SmsCommentsDM>) itemIds) {
			savecomments.setPurEnquiryId(puchaseEnqId);
			savecomments.setUserActiopn(status);
			serviceComment.saveOrUpdateComments(savecomments);
		}
	}
	
	public void saveQuote(Long purchaseQuoteId, String status) {
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveQuote : (Collection<SmsCommentsDM>) itemIds) {
			saveQuote.setPurQuoteID(purchaseQuoteId);
			saveQuote.setUserActiopn(status);
			serviceComment.saveOrUpdateComments(saveQuote);
		}
	}
	
	public void savePurchaseOrder(Long poId, String status) {
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM savepo : (Collection<SmsCommentsDM>) itemIds) {
			savepo.setPoId(poId);
			savepo.setUserActiopn(status);
			serviceComment.saveOrUpdateComments(savepo);
		}
	}
	
	public void saveInvoice(Long invoiceID, String status) {
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveInvoice : (Collection<SmsCommentsDM>) itemIds) {
			saveInvoice.setInvoiceId(invoiceID);
			saveInvoice.setUserActiopn(status);
			serviceComment.saveOrUpdateComments(saveInvoice);
		}
	}
	
	public void saveSalesEnqId(Long salesEnqId, String status) {
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveSalesEnq : (Collection<SmsCommentsDM>) itemIds) {
			saveSalesEnq.setSalesEnqId(salesEnqId);
			saveSalesEnq.setUserActiopn(status);
			serviceComment.saveOrUpdateComments(saveSalesEnq);
		}
	}
	
	public void saveSaleQuote(Long salesQuoteid, String status) {
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveSalesQuote : (Collection<SmsCommentsDM>) itemIds) {
			saveSalesQuote.setSalesQuoteId(salesQuoteid);
			saveSalesQuote.setUserActiopn(status);
			serviceComment.saveOrUpdateComments(saveSalesQuote);
		}
	}
	
	public void saveSalesPo(Long salesPoid, String status) {
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveVendorBill : (Collection<SmsCommentsDM>) itemIds) {
			saveVendorBill.setSalesPoId(salesPoid);
			saveVendorBill.setUserActiopn(status);
			serviceComment.saveOrUpdateComments(saveVendorBill);
		}
	}
	
	public void saveReceipt(Long respId, String status) {
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveReceipt : (Collection<SmsCommentsDM>) itemIds) {
			saveReceipt.setReceiptId(respId);
			saveReceipt.setUserActiopn(status);
			serviceComment.saveOrUpdateComments(saveReceipt);
		}
	}
	
	public void saveVendorBill(Long billID, String status) {
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveVendorBill : (Collection<SmsCommentsDM>) itemIds) {
			saveVendorBill.setBillId(billID);
			saveVendorBill.setUserActiopn(status);
			serviceComment.saveOrUpdateComments(saveVendorBill);
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
	}
}
