package com.gnts.mfg.txn;

import java.util.ArrayList;
import java.util.List;
import org.vaadin.dialogs.ConfirmDialog;
import com.gnts.crm.domain.txn.DocumentsDM;
import com.gnts.crm.service.txn.DocumentsService;
import com.gnts.dsn.stt.txn.DesignDocumentUI;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPTable;
import com.gnts.erputil.components.GERPTextArea;
import com.gnts.erputil.components.GERPTextField;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.util.DateUtils;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class TestingDocuments implements ClickListener {
	private static final long serialVersionUID = 1L;
	private DocumentsService serviceDocuments = (DocumentsService) SpringContextHelper.getBean("documents");
	// Header container which holds, screen name, notification and page master
	// buttons
	private Long companyId;
	private String username;
	private GERPTextField tfDocumentName = new GERPTextField("Document Name");
	private GERPTextArea taComments = new GERPTextArea("Comments");
	private GERPButton btnSave = new GERPButton("Save", "add", this);
	private GERPButton btnDelete = new GERPButton("Delete", "cancelbt", this);
	private GERPTable tblDocuments = new GERPTable();
	private VerticalLayout vlDocument = new VerticalLayout();
	private BeanItemContainer<DocumentsDM> beanDocuments = null;
	private String docTypeId, docType;
	
	public TestingDocuments(VerticalLayout hlPageLayout, String testTypeId, String docType) {
		this.docTypeId = testTypeId;
		this.docType = docType;
		hlPageLayout.setWidth("1150");
		hlPageLayout.removeAllComponents();
		if (UI.getCurrent().getSession().getAttribute("loginCompanyId") != null) {
			companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		}
		UI.getCurrent().getSession().setAttribute("IS_DOC_UPLOAD", false);
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		tblDocuments.setPageLength(10);
		tfDocumentName.setRequired(true);
		// Build Page Header
		// Add screen name in the page title layout
		hlPageLayout.addComponent(new HorizontalLayout() {
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
								addComponent(tfDocumentName);
								addComponent(taComments);
								addComponent(new HorizontalLayout() {
									private static final long serialVersionUID = 1L;
									{
										setSpacing(true);
										addComponent(btnSave);
										addComponent(btnDelete);
									}
								});
							}
						});
						addComponent(tblDocuments);
					}
				});
				addComponent(vlDocument);
			}
		});
		new DesignDocumentUI(vlDocument);
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
		loadSearchResult(testTypeId);
	}
	
	private void loadSearchResult(String primaryId) {
		List<DocumentsDM> documentList = new ArrayList<DocumentsDM>();
		try {
			if (docType.equalsIgnoreCase("QC")) {
				documentList = serviceDocuments.getDocumentDetails(null, null, null, null, null, null, null, null,
						null, Long.valueOf(primaryId), null, null, "Active");
			} else if (docType.equalsIgnoreCase("QA")) {
				documentList = serviceDocuments.getDocumentDetails(null, null, null, null, null, null, null, null,
						null, null, Long.valueOf(primaryId), null, "Active");
			} else if (docType.equalsIgnoreCase("DR")) {
				documentList = serviceDocuments.getDocumentDetails(null, null, Long.valueOf(primaryId), null, null,
						null, null, null, null, null, null, null, "Active");
			} else if (docType.equalsIgnoreCase("SIGN_OFF")) {
				documentList = serviceDocuments.getDocumentDetails(null, null, null, null, null, null, null, null,
						null, null, null, Long.valueOf(primaryId), "Active");
			} else if (docType.equalsIgnoreCase("CLIENT")) {
				documentList = serviceDocuments.getDocumentDetails(null, null, null, Long.valueOf(primaryId), null,
						null, null, null, null, null, null, null, "Active");
			}
			int recordcount = documentList.size();
			beanDocuments = new BeanItemContainer<DocumentsDM>(DocumentsDM.class);
			beanDocuments.addAll(documentList);
			tblDocuments.setSelectable(true);
			tblDocuments.setContainerDataSource(beanDocuments);
			tblDocuments.setVisibleColumns(new Object[] { "documentName", "lastUpdatedBy","lastUpdatedDt" });
			tblDocuments.setColumnHeaders(new String[] { "Document Name", "Uploaded By","Uploaded Dt" });
			tblDocuments.setColumnFooter("lastUpdatedDt", "No.of Records : " + recordcount);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveDetails() {
		try {
			Boolean isDocUploaded = (Boolean) UI.getCurrent().getSession().getAttribute("IS_DOC_UPLOAD");
			if (isDocUploaded) {
				byte[] uploadedDoc = (byte[]) UI.getCurrent().getSession().getAttribute("UPLOAD_FILE_BYTE");
				String filename = (String) UI.getCurrent().getSession().getAttribute("UPLOAD_FILE_NAME");
				DocumentsDM documentsDM = new DocumentsDM();
				if (tblDocuments.getValue() != null) {
					documentsDM = beanDocuments.getItem(tblDocuments.getValue()).getBean();
				}
				documentsDM.setCompanyId(companyId);
				documentsDM.setDocument(uploadedDoc);
				documentsDM.setDocumentName(tfDocumentName.getValue());
				documentsDM.setComments(taComments.getValue());
				documentsDM.setFileName(filename);
				if (docType.equalsIgnoreCase("QC")) {
					documentsDM.setQcTestId(Long.valueOf(docTypeId));
				} else if (docType.equalsIgnoreCase("QA")) {
					documentsDM.setQaTestId(Long.valueOf(docTypeId));
				} else if (docType.equalsIgnoreCase("DR")) {
					documentsDM.setEnquiryid(Long.valueOf(docTypeId));
				} else if (docType.equalsIgnoreCase("SIGN_OFF")) {
					documentsDM.setSignOffId(Long.valueOf(docTypeId));
				} else if (docType.equalsIgnoreCase("CLIENT")) {
					documentsDM.setClientId(Long.valueOf(docTypeId));
				}
				documentsDM.setDocumentType("pdf");
				documentsDM.setLastUpdatedBy(username);
				documentsDM.setStatus("Active");
				documentsDM.setLastUpdatedDt(DateUtils.getcurrentdate());
				serviceDocuments.saveOrUpdateDocumentsDetails(documentsDM);
				resetDetails();
				loadSearchResult(docTypeId);
			} else {
				Notification.show("Plz Upload PDF Document");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void editDetails() {
		try {
			DocumentsDM documentsDM = beanDocuments.getItem(tblDocuments.getValue()).getBean();
			DesignDocumentUI designDocumentUI = new DesignDocumentUI(vlDocument);
			tfDocumentName.setValue(documentsDM.getDocumentName());
			if (documentsDM.getComments() != null) {
				taComments.setValue(documentsDM.getComments());
			}
			designDocumentUI.displayDocument(documentsDM.getDocument(), documentsDM.getFileName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void resetDetails() {
		tfDocumentName.setValue("");
		taComments.setValue("");
		new DesignDocumentUI(vlDocument);
		UI.getCurrent().getSession().setAttribute("IS_DOC_UPLOAD", false);
	}
	
	private void deleteDetails() {
		// TODO Auto-generated method stub
		try {
			// TODO Auto-generated method stub
			ConfirmDialog.show(UI.getCurrent(), "Please Confirm:", "Are you really sure?", "Delete File", "Not quite",
					new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;
						
						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								DocumentsDM documentsDM = beanDocuments.getItem(tblDocuments.getValue()).getBean();
								documentsDM.setStatus("Inactive");
								serviceDocuments.saveOrUpdateDocumentsDetails(documentsDM);
								resetDetails();
								loadSearchResult(docTypeId);
							}
						}
					});
		}
		catch (Exception e) {
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (event.getButton() == btnSave) {
			saveDetails();
		}
		if (event.getButton() == btnDelete) {
			deleteDetails();
		}
	}
}
