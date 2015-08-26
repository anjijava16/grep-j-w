package com.gnts.dsn.stt.txn;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.crm.domain.txn.DocumentsDM;
import com.gnts.crm.service.txn.DocumentsService;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPComboBox;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.gnts.sms.domain.txn.SmsEnqHdrDM;
import com.gnts.sms.service.txn.SmsEnqHdrService;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;

public class DesignDocuments implements ClickListener {
	private static final long serialVersionUID = 1L;
	private DocumentsService serviceDocuments = (DocumentsService) SpringContextHelper.getBean("documents");
	private VerticalLayout hlPageRootContainter = (VerticalLayout) UI.getCurrent().getSession()
			.getAttribute("clLayout");
	private SmsEnqHdrService serviceEnqHeader = (SmsEnqHdrService) SpringContextHelper.getBean("SmsEnqHdr");
	// Header container which holds, screen name, notification and page master
	// buttons
	private HorizontalLayout hlPageHdrContainter = (HorizontalLayout) UI.getCurrent().getSession()
			.getAttribute("hlLayout");
	private HorizontalLayout hlPageTitleLayout;
	private Button btnScreenName;
	private Long companyId;
	private String screenName = "", username;
	private GERPComboBox cbEnquiry = new GERPComboBox("Enquiry");
	private GERPTextField tfDocumentName = new GERPTextField("Document Name");
	private GERPTextArea taComments = new GERPTextArea("Comments");
	private GERPButton btnSave = new GERPButton("Save", "add", this);
	private GERPTable tblDocuments = new GERPTable();
	private VerticalLayout vlDocument = new VerticalLayout();
	private BeanItemContainer<DocumentsDM> beanDocuments = null;
	private Logger logger = Logger.getLogger(DesignDocuments.class);
	
	public DesignDocuments() {
		if (UI.getCurrent().getSession().getAttribute("screenName") != null) {
			screenName = UI.getCurrent().getSession().getAttribute("screenName").toString();
		}
		if (UI.getCurrent().getSession().getAttribute("loginCompanyId") != null) {
			companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		}
		UI.getCurrent().getSession().setAttribute("IS_DOC_UPLOAD", false);
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		btnScreenName = new GERPButton(screenName, "link");
		tblDocuments.setPageLength(12);
		cbEnquiry.setRequired(true);
		cbEnquiry.setItemCaptionPropertyId("enquiryNo");
		loadEnquiryList();
		tfDocumentName.setRequired(true);
		// Build Page Header
		// Instantiate page title layout
		hlPageHdrContainter.removeAllComponents();
		hlPageTitleLayout = new HorizontalLayout();
		hlPageTitleLayout.setMargin(new MarginInfo(false, false, false, true));
		// Add screen name in the page title layout
		hlPageTitleLayout.addComponent(btnScreenName);
		hlPageTitleLayout.setComponentAlignment(btnScreenName, Alignment.MIDDLE_CENTER);
		hlPageHdrContainter.addComponent(hlPageTitleLayout);
		hlPageHdrContainter.setComponentAlignment(hlPageTitleLayout, Alignment.MIDDLE_LEFT);
		hlPageRootContainter.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(new VerticalLayout() {
					private static final long serialVersionUID = 1L;
					{
						addComponent(new FormLayout() {
							private static final long serialVersionUID = 1L;
							{
								setSpacing(true);
								addComponent(cbEnquiry);
								addComponent(tfDocumentName);
								addComponent(taComments);
								addComponent(btnSave);
							}
						});
						addComponent(tblDocuments);
					}
				});
				addComponent(vlDocument);
			}
		});
		new DesignDocumentUI(vlDocument);
		cbEnquiry.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				resetDetails();
				loadSearchResult();
			}
		});
		tblDocuments.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblDocuments.isSelected(event.getItemId())) {
					tblDocuments.setImmediate(true);
					resetDetails();
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					editDetails();
				}
			}
		});
		loadSearchResult();
	}
	
	private void loadSearchResult() {
		try {
			List<DocumentsDM> documentList = new ArrayList<DocumentsDM>();
			if (cbEnquiry.getValue() != null) {
				System.out.println("cbEnquiry.getValue()--->" + cbEnquiry.getValue());
				documentList = serviceDocuments.getDocumentDetails(null, null, (Long) cbEnquiry.getValue(), null, null,
						null, null, null, null, null, null, null);
			}
			int recordcount = documentList.size();
			beanDocuments = new BeanItemContainer<DocumentsDM>(DocumentsDM.class);
			beanDocuments.addAll(documentList);
			tblDocuments.setSelectable(true);
			tblDocuments.setContainerDataSource(beanDocuments);
			tblDocuments.setVisibleColumns(new Object[] { "documentId", "documentName", "lastUpdatedBy" });
			tblDocuments.setColumnHeaders(new String[] { "Ref.Id", "Document Name", "Uploaded By" });
			tblDocuments.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordcount);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	// Load Enquiry List
	private void loadEnquiryList() {
		try {
			BeanContainer<Long, SmsEnqHdrDM> beansmsenqHdr = new BeanContainer<Long, SmsEnqHdrDM>(SmsEnqHdrDM.class);
			beansmsenqHdr.setBeanIdProperty("enquiryId");
			beansmsenqHdr.addAll(serviceEnqHeader.getSmsEnqHdrList(companyId, null, null, null, null, "P", null, null));
			cbEnquiry.setContainerDataSource(beansmsenqHdr);
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void saveDetails() {
		Boolean isDocUploaded = false;
		try {
			isDocUploaded = (Boolean) UI.getCurrent().getSession().getAttribute("IS_DOC_UPLOAD");
		}
		catch (Exception e) {
		}
		if (isDocUploaded) {
			byte[] uploadedDoc = (byte[]) UI.getCurrent().getSession().getAttribute("UPLOAD_FILE_BYTE");
			String filename = (String) UI.getCurrent().getSession().getAttribute("UPLOAD_FILE_NAME");
			DocumentsDM documentsDM = new DocumentsDM();
			if (tblDocuments.getValue() != null) {
				documentsDM = beanDocuments.getItem(tblDocuments.getValue()).getBean();
			}
			documentsDM.setCompanyId(companyId);
			documentsDM.setEnquiryid((Long) cbEnquiry.getValue());
			documentsDM.setDocument(uploadedDoc);
			documentsDM.setDocumentName(tfDocumentName.getValue());
			documentsDM.setComments(taComments.getValue());
			documentsDM.setFileName(filename);
			documentsDM.setDocumentType("pdf");
			documentsDM.setLastUpdatedBy(username);
			documentsDM.setLastUpdatedDt(DateUtils.getcurrentdate());
			serviceDocuments.saveOrUpdateDocumentsDetails(documentsDM);
			resetDetails();
			loadSearchResult();
		} else {
			Notification.show("Plz Upload PDF Document");
		}
	}
	
	private void editDetails() {
		try {
			DocumentsDM documentsDM = beanDocuments.getItem(tblDocuments.getValue()).getBean();
			DesignDocumentUI designDocumentUI = new DesignDocumentUI(vlDocument);
			tfDocumentName.setValue(documentsDM.getDocumentName());
			taComments.setValue(documentsDM.getComments());
			designDocumentUI.displayDocument(documentsDM.getDocument(), documentsDM.getFileName());
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private void resetDetails() {
		tfDocumentName.setValue("");
		taComments.setValue("");
		new DesignDocumentUI(vlDocument);
		UI.getCurrent().getSession().setAttribute("IS_DOC_UPLOAD", false);
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (event.getButton() == btnSave) {
			saveDetails();
		}
	}
}
