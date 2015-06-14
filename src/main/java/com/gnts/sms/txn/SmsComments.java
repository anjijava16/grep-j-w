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
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.EmployeeDM;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.base.service.mst.EmployeeService;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.sms.domain.txn.PurchasePODtlDM;
import com.gnts.sms.domain.txn.SmsCommentsDM;
import com.gnts.sms.domain.txn.VendorBillDtlDM;
import com.gnts.sms.service.txn.SmsCommentsService;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class SmsComments implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SmsCommentsService serviceComment = (SmsCommentsService) SpringContextHelper.getBean("smsComments");
	private EmployeeService serviceemployee = (EmployeeService) SpringContextHelper.getBean("employee");
	private Table tblClntComments;
	private Long companyId;
	private Button btnComments, btnEdit;
	private FormLayout flMainform1, flMainform2, flMainform3, flMainform4;
	private String userName, strWidth = "160px";
	private int total = 0;
	List<SmsCommentsDM> commentList = new ArrayList<SmsCommentsDM>();
	VerticalLayout vlTableForm = new VerticalLayout();
	private TextArea taComments;
	private TextField taUserAction;
	private ComboBox cbCommenyBy;
	Long commentBy;
	private BeanItemContainer<SmsCommentsDM> beanComment = null;
	private Logger logger = Logger.getLogger(SmsCommentsDM.class);
	private Button btnSave = new GERPButton("Add", "addbt", this);
	private Long comentsId, enquiryId, PurchaseQuoteId, PoId, invoiceID, salesEnqId, salesQuoteid, billID, salesPoid,
			respId, empId;
	String status;
	
	public SmsComments(VerticalLayout vlTableForm, Long commentId, Long companyId, Long PurEnquiryId, Long purQuoteID,
			Long poId, Long receiptId, Long billId, Long SalesEnqId, Long salesQuoteId, Long salesPoId, Long InvoiceId,
			String status) {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		empId = Long.valueOf(UI.getCurrent().getSession().getAttribute("employeeId").toString());
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		comentsId = commentId;
		enquiryId = PurEnquiryId;
		PurchaseQuoteId = purQuoteID;
		PoId = poId;
		respId = receiptId;
		billID = billId;
		salesEnqId = SalesEnqId;
		salesQuoteid = salesQuoteId;
		salesPoid = salesPoId;
		invoiceID = InvoiceId;
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
		// taUserAction = new GERPTextField("User Action");
		// cbCommenyBy = new GERPComboBox("Commend By");
		// cbCommenyBy.setRequired(true);
		// cbCommenyBy.setItemCaptionPropertyId("firstname");
		// loadEmpList();
		/**
		 * add fields to header layout
		 */
		/**
		 * add fields to form Layout
		 */
		flMainform1 = new FormLayout();
		flMainform2 = new FormLayout();
		flMainform3 = new FormLayout();
		flMainform4 = new FormLayout();
		flMainform1.setSpacing(true);
		flMainform1.addComponent(taComments);
		// flMainform2.addComponent(taUserAction);
		// flMainform3.addComponent(cbCommenyBy);
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
		// hluserInput.addComponent(flMainform3);
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
			/*
			 * if (editcomment.getUserActiopn() != null) { taUserAction.setValue(editcomment.getUserActiopn()); }
			 */
			// Long uom = editcomment.getCommentBy();
			// Collection<?> uomid = cbCommenyBy.getItemIds();
			// for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) {
			// Object itemId = (Object) iterator.next();
			// BeanItem<?> item = (BeanItem<?>) cbCommenyBy.getItem(itemId);
			// // Get the actual bean and use the data
			// EmployeeDM st = (EmployeeDM) item.getBean();
			// if (uom != null && uom.equals(st.getEmployeeid())) {
			// cbCommenyBy.setValue(itemId);
			// }
			// }
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
			// Long empfirst = null;
			// if (cbCommenyBy.getValue() != null) {
			// empfirst = ((Long) cbCommenyBy.getValue());
			// }
			commentList = serviceComment.getSmsCommentsList(commentId, companyId, PurEnquiryId, purQuoteID, poId,
					receiptId, billId, SalesEnqId, salesQuoteId, salesPoId, InvoiceId, null);
			System.out.println("loadsrchoppertunity---->" + commentId);
		}
		try {
			tblClntComments.removeAllItems();
			total = commentList.size();
			System.out.println("LISTSIZE---1->" + commentList.size());
			beanComment = new BeanItemContainer<SmsCommentsDM>(SmsCommentsDM.class);
			beanComment.addAll(commentList);
			tblClntComments.setSelectable(true);
			tblClntComments.setContainerDataSource(beanComment);
			tblClntComments.setColumnAlignment("commnetId", Align.RIGHT);
			/*
			 * tblClntComments.addItem(taComments); tblClntComments.addItem(taUserAction);
			 * tblClntComments.addItem(cbCommenyBy);
			 */tblClntComments.setVisibleColumns(new Object[] { "comments", "userActiopn", "empName", "commentDate",
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
		// cbCommenyBy.setValue(null);
		// taUserAction.setValue("");
		// commentList = new ArrayList<SmsCommentsDM>();
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
	
	// load employee names
	// public void loadEmpList() {
	// try {
	// List<EmployeeDM> empList = serviceemployee.getEmployeeList(null, null, null, "Active", companyId, null, null,
	// null, null, "F");
	// BeanItemContainer<EmployeeDM> beanProduct = new BeanItemContainer<EmployeeDM>(EmployeeDM.class);
	// beanProduct.addAll(empList);
	// cbCommenyBy.setContainerDataSource(beanProduct);
	// }
	// catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	public void saveEnquiry(Long PuchaseEnqId, String status) {
		System.out.println("saveid1-->>" + PuchaseEnqId);
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM savecomments : (Collection<SmsCommentsDM>) itemIds) {
			savecomments.setPurEnquiryId(PuchaseEnqId);
			savecomments.setUserActiopn(status);
			System.out.println("saveid2-->>" + PuchaseEnqId);
			serviceComment.saveOrUpdateComments(savecomments);
		}
	}
	
	public void saveQuote(Long PurchaseQuoteId, String status) {
		System.out.println("saveid1-->>" + PurchaseQuoteId);
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveQuote : (Collection<SmsCommentsDM>) itemIds) {
			saveQuote.setPurQuoteID(PurchaseQuoteId);
			saveQuote.setUserActiopn(status);
			System.out.println("saveid2-->>" + PurchaseQuoteId);
			serviceComment.saveOrUpdateComments(saveQuote);
		}
	}
	
	public void savePurchaseOrder(Long PoId, String status) {
		System.out.println("saveid1-->>" + PoId);
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM savepo : (Collection<SmsCommentsDM>) itemIds) {
			savepo.setPoId(PoId);
			savepo.setUserActiopn(status);
			System.out.println("saveid2-->>" + PoId);
			serviceComment.saveOrUpdateComments(savepo);
		}
	}
	
	public void saveInvoice(Long invoiceID, String status) {
		System.out.println("saveid1-->>" + invoiceID);
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveInvoice : (Collection<SmsCommentsDM>) itemIds) {
			saveInvoice.setInvoiceId(invoiceID);
			saveInvoice.setUserActiopn(status);
			System.out.println("saveid2-->>" + invoiceID);
			serviceComment.saveOrUpdateComments(saveInvoice);
		}
	}
	
	public void saveSalesEnqId(Long salesEnqId, String status) {
		System.out.println("saveid1-->>" + salesEnqId);
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveSalesEnq : (Collection<SmsCommentsDM>) itemIds) {
			saveSalesEnq.setSalesEnqId(salesEnqId);
			saveSalesEnq.setUserActiopn(status);
			System.out.println("saveid2-->>" + salesEnqId);
			serviceComment.saveOrUpdateComments(saveSalesEnq);
		}
	}
	
	public void saveSaleQuote(Long salesQuoteid, String status) {
		System.out.println("saveid1-->>" + salesQuoteid);
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveSalesQuote : (Collection<SmsCommentsDM>) itemIds) {
			saveSalesQuote.setSalesQuoteId(salesQuoteid);
			saveSalesQuote.setUserActiopn(status);
			System.out.println("saveid2-->>" + salesQuoteid);
			serviceComment.saveOrUpdateComments(saveSalesQuote);
		}
	}
	
	public void saveSalesPo(Long salesPoid, String status) {
		System.out.println("saveid1-->>" + billID);
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveVendorBill : (Collection<SmsCommentsDM>) itemIds) {
			saveVendorBill.setSalesPoId(salesPoid);
			saveVendorBill.setUserActiopn(status);
			System.out.println("saveid2-->>" + billID);
			serviceComment.saveOrUpdateComments(saveVendorBill);
		}
	}
	
	public void saveReceipt(Long respId, String status) {
		System.out.println("saveid1-->>" + respId);
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveReceipt : (Collection<SmsCommentsDM>) itemIds) {
			saveReceipt.setReceiptId(respId);
			saveReceipt.setUserActiopn(status);
			System.out.println("saveid2-->>" + respId);
			serviceComment.saveOrUpdateComments(saveReceipt);
		}
	}
	
	public void saveVendorBill(Long billID, String status) {
		System.out.println("saveid1-->>" + billID);
		@SuppressWarnings("unchecked")
		Collection<SmsCommentsDM> itemIds = (Collection<SmsCommentsDM>) tblClntComments.getVisibleItemIds();
		for (SmsCommentsDM saveVendorBill : (Collection<SmsCommentsDM>) itemIds) {
			saveVendorBill.setBillId(billID);
			saveVendorBill.setUserActiopn(status);
			System.out.println("saveid2-->>" + billID);
			serviceComment.saveOrUpdateComments(saveVendorBill);
		}
	}
	
	private boolean validateAll() {
		boolean valid = true;
		try {
			taComments.validate();
			taComments.setComponentError(null);
			/*
			 * cbCommenyBy.validate(); cbCommenyBy.setComponentError(null);
			 */
		}
		catch (Exception e) {
			logger.info("validaAll :comments is empty--->" + e);
			taComments.setComponentError(new UserError("Enter Comments"));
			// cbCommenyBy.setComponentError(new UserError("Select CommendBy"));
			valid = false;
		}
		return valid;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
	}
}
