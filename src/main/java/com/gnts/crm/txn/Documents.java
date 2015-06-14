package com.gnts.crm.txn;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.CompanyLookupDM;
import com.gnts.base.service.mst.CompanyLookupService;
import com.gnts.crm.domain.txn.DocumentsDM;
import com.gnts.crm.service.txn.DocumentsService;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.UploadDocumentUI;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class Documents implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DocumentsService serviceDoc = (DocumentsService) SpringContextHelper.getBean("documents");
	private CompanyLookupService servicecompany = (CompanyLookupService) SpringContextHelper.getBean("companyLookUp");
	private Table tblDocuments = null;
	private Long companyId;
	private Button btnSave, btnDocument, btnEdit, btndelete;
	private VerticalLayout vlTableForm, vlMainLayout, vlTableLayout;
	private VerticalLayout vlDocuemntPanel;
	private VerticalLayout vlDocument;
	private HorizontalLayout hlButtonLayout1;
	private String basepath1, basepath;
	private String userName, strWidth = "170px";
	private CompanyLookupDM documentTypeLookup;
	private BeanContainer<String, CompanyLookupDM> beanCompLookUp = null;
	private int recordcount = 0;
	List<DocumentsDM> documentList = new ArrayList<DocumentsDM>();
	private Long leadId, clntContactId, clientId, campaignId, clntCaseId, clntOppertunityId, quoteId, enquiryId,
			moduleId;
	private TextField tfDocumentName;
	private ComboBox cbDocumentType;
	private TextArea taComments;
	private FormLayout flcolumn1, flcolumn2;
	public static boolean filevalue = false;
	private Window wndDocuments = new Window("Documents");
	private BeanItemContainer<DocumentsDM> beanDocuments = null;
	private Logger logger = Logger.getLogger(DocumentsDM.class);
	
	public Documents(VerticalLayout vlCommetTblLayout, Long oppertunityId, Long clntLeadId, Long clientid,
			Long contactId, Long clntCampaingId, Long caseId) {
		userName = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyId = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		moduleId = Long.valueOf(UI.getCurrent().getSession().getAttribute("moduleId").toString());
		quoteId = (Long) (UI.getCurrent().getSession().getAttribute("quoteid"));
		enquiryId = ((Long) UI.getCurrent().getSession().getAttribute("enquiryid"));
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
	@SuppressWarnings("deprecation")
	private void buildview(VerticalLayout vlCommetTblLayout) {
		// TODO Auto-generated method stub
		basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		basepath1 = basepath + "/VAADIN/themes/gerp/img/Document.pdf";
		vlCommetTblLayout.removeAllComponents();
		/**
		 * Initialise the button declarations
		 */
		btndelete = new GERPButton("Delete", "delete", this);
		btnSave = new GERPButton("Add", "add", this);
		btnDocument = new GERPButton("Add", "add", this);
		btnEdit = new Button("Edit", this);
		btnEdit.setEnabled(false);
		/**
		 * set the style for buttons
		 */
		btnSave.addStyleName("add");
		btnEdit.addStyleName("editbt");
		/**
		 * Declaration add/edit text field and combo box fields
		 */
		vlDocument = new VerticalLayout();
		vlDocuemntPanel = new VerticalLayout();
		vlDocuemntPanel.setMargin(true);
		vlDocument.removeAllComponents();
		tfDocumentName = new TextField("Document Name");
		tfDocumentName.setWidth(strWidth);
		tfDocumentName.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				tfDocumentName.setComponentError(null);
				if (tfDocumentName.getValue() != null) {
					tfDocumentName.setComponentError(null);
				}
			}
		});
		taComments = new TextArea("Comments");
		taComments.setWidth(strWidth);
		taComments.setHeight("60px");
		cbDocumentType = new ComboBox("Document Type");
		cbDocumentType.setItemCaptionPropertyId("lookupname");
		cbDocumentType.setNullSelectionAllowed(false);
		cbDocumentType.setWidth(strWidth);
		cbDocumentType.addBlurListener(new BlurListener() {
			private static final long serialVersionUID = 1L;
			
			public void blur(BlurEvent event) {
				cbDocumentType.setComponentError(null);
				if (cbDocumentType.getValue() != null) {
					cbDocumentType.setComponentError(null);
				}
			}
		});
		loadOppertunityTypeByLookUpList();
		cbDocumentType.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					BeanItem<?> item = (BeanItem<?>) cbDocumentType.getItem(itemid);
					documentTypeLookup = (CompanyLookupDM) item.getBean();
				}
			}
		});
		VerticalLayout img = new VerticalLayout();
		new UploadDocumentUI(vlDocument);
		img.addComponent(vlDocument);
		img.setSpacing(true);
		img.setMargin(true);
		img.setSizeFull();
		/**
		 * add fields to header layout
		 */
		hlButtonLayout1 = new HorizontalLayout();
		hlButtonLayout1.addComponent(btnDocument);
		hlButtonLayout1.addComponent(btndelete);
		HorizontalLayout hlForm = new HorizontalLayout();
		hlForm.setSpacing(true);
		// hlForm.addComponent(flMainform1);
		hlForm.addComponent(img);
		hlForm.setSpacing(true);
		final GridLayout glGridLayout1 = new GridLayout(1, 1);
		glGridLayout1.setSizeFull();
		glGridLayout1.setSpacing(true);
		glGridLayout1.setMargin(true);
		glGridLayout1.addComponent(hlForm);
		glGridLayout1.addComponent(hlButtonLayout1);
		glGridLayout1.setComponentAlignment(hlButtonLayout1, Alignment.MIDDLE_CENTER);
		vlMainLayout = new VerticalLayout();
		vlMainLayout.addComponent(glGridLayout1);
		/**
		 * declare the table and add in panel
		 */
		tblDocuments = new Table();
		tblDocuments.setStyleName(Runo.TABLE_SMALL);
		tblDocuments.setPageLength(4);
		tblDocuments.setWidth("600");
		tblDocuments.setSizeUndefined();
		tblDocuments.setSizeFull();
		tblDocuments.setInvalidAllowed(true);
		tblDocuments.setFooterVisible(true);
		tblDocuments.setColumnCollapsingAllowed(true);
		tblDocuments.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if (tblDocuments.isSelected(event.getItemId())) {
					tblDocuments.setImmediate(true);
					btnSave.setCaption("Add");
					btnSave.setStyleName("addbt");
					tfDocumentName.setValue("");
					taComments.setValue("");
				} else {
					((AbstractSelect) event.getSource()).select(event.getItemId());
					btnSave.setCaption("Update");
					btnSave.setStyleName("savebt");
					editDocumentDetails();
				}
			}
		});
		flcolumn1 = new FormLayout();
		flcolumn2 = new FormLayout();
		flcolumn1.addComponent(tfDocumentName);
		flcolumn1.addComponent(cbDocumentType);
		flcolumn1.addComponent(taComments);
		flcolumn1.addComponent(img);
		flcolumn2.addComponent(btnSave);
		flcolumn2.addComponent(btndelete);
		HorizontalLayout hlButton = new HorizontalLayout();
		hlButton.setSpacing(true);
		// hlButton.addComponent(btnDocument);
		// hlButton.addComponent(flcolumn2);
		vlTableForm = new VerticalLayout();
		vlTableForm.setSizeFull();
		vlTableForm.setMargin(true);
		vlTableForm.setSpacing(true);
		HorizontalLayout hluserInput = new HorizontalLayout();
		hluserInput.addComponent(flcolumn1);
		hluserInput.addComponent(flcolumn2);
		//hluserInput.setComponentAlignment(flcolumn2, Alignment.BOTTOM_CENTER);
		hluserInput.setSpacing(true);
		hluserInput.setMargin(true);
		hluserInput.addComponent(tblDocuments);
		vlTableLayout = new VerticalLayout();
		vlTableLayout.setSpacing(true);
		// vlTableLayout.addComponent(vlTableForm);
		vlCommetTblLayout.addComponent(hluserInput);
		setTableProperties();
		loadsrcrslt(false, null, null, null, null, null, null);
	}
	
	/**
	 * populatedAndConfig()-->this function used to load the list to the table if(search==true)--> it performs search
	 * operation else it loads all values
	 * 
	 * @param search
	 */
	public void loadsrcrslt(boolean from, Long clientId, Long clntContactId, Long campaingnId, Long leadId,
			Long oppurtunuityId, Long caseId) {
		if (from) {
			documentList = serviceDoc.getDocumentDetails(companyId, null, null, clientId, clntContactId, leadId,
					campaingnId, clntOppertunityId, clntCaseId);
			recordcount = documentList.size();
		}
		try {
			tblDocuments.removeAllItems();
			beanDocuments = new BeanItemContainer<DocumentsDM>(DocumentsDM.class);
			beanDocuments.addAll(documentList);
			recordcount = documentList.size();
			tblDocuments.setSelectable(true);
			tblDocuments.setContainerDataSource(beanDocuments);
			tblDocuments.setVisibleColumns(new Object[] { "documentId", "documentName", "documentType", "documentDate",
					"lastUpdatedDt", "lastUpdatedBy" });
			tblDocuments.setColumnHeaders(new String[] { "Ref.Id", "Document Name", "Document Type", "Document Date",
					"Last Updated Date", "Last Updated By" });
			tblDocuments.setColumnFooter("lastUpdatedBy", "No.of Records : " + recordcount);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("error during populate values on the table, The Error is ----->" + e);
		}
	}
	
	/**
	 * set the table properties
	 */
	private void setTableProperties() {
		tblDocuments.setColumnAlignment("documentId", Align.RIGHT);
		tblDocuments.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				if (tblDocuments.isSelected(event.getItemId())) {
					btnEdit.setEnabled(false);
					btnDocument.setEnabled(true);
				} else {
					btnEdit.setEnabled(true);
					btnDocument.setEnabled(false);
				}
			}
		});
	}
	
	/**
	 * resetFields()->this method is used for reset the add/edit UI components
	 */
	public void ResetFields() {
		tfDocumentName.setRequired(false);
		taComments.setValue("");
		taComments.setComponentError(null);
		btnSave.setComponentError(null);
		tfDocumentName.setValue("");
		cbDocumentType.setValue(null);
		cbDocumentType.setComponentError(null);
		cbDocumentType.setRequired(false);
		filevalue = false;
		new UploadDocumentUI(vlDocument);
		btnSave.setCaption("Add");
		tblDocuments.removeAllItems();
	}
	
	/**
	 * saveClientCommentsDetails()-->this method is used for save/update the records
	 */
	public void saveDocumentDetails() {
		boolean valid = true;
		validateAll();
		DocumentsDM saveDocument = new DocumentsDM();
		if (tblDocuments.getValue() != null) {
			saveDocument = beanDocuments.getItem(tblDocuments.getValue()).getBean();
			documentList.remove(saveDocument);
		}
		tblDocuments.removeAllItems();
		saveDocument.setCampaignId(campaignId);
		saveDocument.setClientCaseId(clntCaseId);
		saveDocument.setClientId(clientId);
		saveDocument.setCompanyId(companyId);
		saveDocument.setContactId(clntContactId);
		saveDocument.setQuoteid(quoteId);
		saveDocument.setEnquiryid(enquiryId);
		saveDocument.setDocumentDate(com.gnts.erputil.util.DateUtils.getcurrentdate());
		saveDocument.setComments(taComments.getValue());
		if (tfDocumentName.getValue().toString().trim().length() > 0) {
			saveDocument.setDocumentName(tfDocumentName.getValue());
		} else {
			tfDocumentName.setComponentError(new UserError("Enter document name"));
		}
		if (documentTypeLookup != null) {
			saveDocument.setDocumentType(documentTypeLookup.getLookupname());
		} else {
			cbDocumentType.setComponentError(new UserError("Select document type"));
		}
		saveDocument.setLastUpdatedBy(userName);
		saveDocument.setLastUpdatedDt(com.gnts.erputil.util.DateUtils.getcurrentdate());
		saveDocument.setLeadId(leadId);
		saveDocument.setOppertunityId(clntOppertunityId);
		if (filevalue) {
			try {
				File file = new File(basepath1);
				FileInputStream fin = new FileInputStream(file);
				byte fileContent[] = new byte[(int) file.length()];
				fin.read(fileContent);
				fin.close();
				saveDocument.setDocument(fileContent);
			}
			catch (Exception e) {
				logger.info("File upload exception" + e);
			}
		} else {
			saveDocument.setDocument(null);
		}
		if (tfDocumentName.isValid() && cbDocumentType.isValid()) {
			documentList.add(saveDocument);
			loadsrcrslt(false, null, null, null, null, null, null);
			wndDocuments.close();
		} else {
			btnSave.setComponentError(new UserError("Form not valid"));
		}
		ResetFields();
	}
	
	private void deletedetails() {
		DocumentsDM delete = new DocumentsDM();
		if (tblDocuments.getValue() != null) {
			delete = beanDocuments.getItem(tblDocuments.getValue()).getBean();
			documentList.remove(delete);
			tfDocumentName.setValue("");
			cbDocumentType.setValue(null);
			taComments.setValue("");
			btnSave.setCaption("Add");
			loadsrcrslt(false, null, null, null, null, null, null);
		}
	}
	
	public void documentsave(Long clientId) {
		@SuppressWarnings("unchecked")
		Collection<DocumentsDM> itemIds = (Collection<DocumentsDM>) tblDocuments.getVisibleItemIds();
		for (DocumentsDM savedocument : (Collection<DocumentsDM>) itemIds) {
			savedocument.setClientId(clientId);
			serviceDoc.saveOrUpdateDocumentsDetails(savedocument);
		}
	}
	
	public void savecontact(Long contactId) {
		System.out.println("saveid1-->>" + contactId);
		@SuppressWarnings("unchecked")
		Collection<DocumentsDM> itemIds = (Collection<DocumentsDM>) tblDocuments.getVisibleItemIds();
		for (DocumentsDM savecontacts : (Collection<DocumentsDM>) itemIds) {
			savecontacts.setContactId(contactId);
			System.out.println("saveid2-->>" + contactId);
			serviceDoc.saveOrUpdateDocumentsDetails(savecontacts);
		}
	}
	
	public void savecampaign(Long campaingnId) {
		System.out.println("saveid1-->>" + campaingnId);
		@SuppressWarnings("unchecked")
		Collection<DocumentsDM> itemIds = (Collection<DocumentsDM>) tblDocuments.getVisibleItemIds();
		for (DocumentsDM savecampaign : (Collection<DocumentsDM>) itemIds) {
			savecampaign.setCampaignId(campaingnId);
			System.out.println("saveid2-->>" + campaingnId);
			serviceDoc.saveOrUpdateDocumentsDetails(savecampaign);
		}
	}
	
	public void saveLeads(Long leadId) {
		System.out.println("saveid1-->>" + leadId);
		@SuppressWarnings("unchecked")
		Collection<DocumentsDM> itemIds = (Collection<DocumentsDM>) tblDocuments.getVisibleItemIds();
		for (DocumentsDM saveleads : (Collection<DocumentsDM>) itemIds) {
			saveleads.setLeadId(leadId);
			System.out.println("saveid2-->>" + leadId);
			serviceDoc.saveOrUpdateDocumentsDetails(saveleads);
		}
	}
	
	public void saveclientcases(Long caseId) {
		System.out.println("saveid1-->>" + caseId);
		@SuppressWarnings("unchecked")
		Collection<DocumentsDM> itemIds = (Collection<DocumentsDM>) tblDocuments.getVisibleItemIds();
		for (DocumentsDM savecases : (Collection<DocumentsDM>) itemIds) {
			savecases.setClientCaseId(caseId);
			System.out.println("saveid2-->>" + caseId);
			serviceDoc.saveOrUpdateDocumentsDetails(savecases);
		}
	}
	
	public void saveclientoppurtunuity(Long oppertunityId) {
		System.out.println("saveid1-->>" + oppertunityId);
		@SuppressWarnings("unchecked")
		Collection<DocumentsDM> itemIds = (Collection<DocumentsDM>) tblDocuments.getVisibleItemIds();
		for (DocumentsDM saveoppertunuity : (Collection<DocumentsDM>) itemIds) {
			saveoppertunuity.setOppertunityId(oppertunityId);
			System.out.println("saveid2-->>" + oppertunityId);
			serviceDoc.saveOrUpdateDocumentsDetails(saveoppertunuity);
		}
	}
	
	/**
	 * this method is used edit the document details
	 */
	private void editDocumentDetails() {
		if (tblDocuments.getValue() != null) {
			DocumentsDM editDocuments = beanDocuments.getItem(tblDocuments.getValue()).getBean();
			if (editDocuments.getComments() != null) {
				taComments.setValue(editDocuments.getComments());
			}
			String editType = editDocuments.getDocumentType();
			Collection<?> collType = cbDocumentType.getItemIds();
			for (Iterator iterator = collType.iterator(); iterator.hasNext();) {
				Object itemId8 = (Object) iterator.next();
				BeanItem<?> item = (BeanItem<?>) cbDocumentType.getItem(itemId8);
				CompanyLookupDM typeBean = (CompanyLookupDM) item.getBean();
				if (editType != null && editType.equals(typeBean.getLookupname())) {
					cbDocumentType.setValue(itemId8);
					break;
				} else {
					cbDocumentType.setValue(null);
				}
			}
			tfDocumentName.setValue(editDocuments.getDocumentName());
			try {
				byte[] bytes = (byte[]) editDocuments.getDocument();
				UploadDocumentUI test = new UploadDocumentUI(vlDocument);
				test.displaycertificate(bytes);
			}
			catch (Exception e) {
			}
		}
	}
	
	/**
	 * this method is used for validate user input
	 * 
	 */
	private void validateAll() {
		// TODO Auto-generated method stub
		try {
			tfDocumentName.setRequired(true);
			tfDocumentName.validate();
		}
		catch (Exception e) {
			logger.info("validaAll :document name is empty--->" + e);
		}
		try {
			cbDocumentType.setRequired(true);
			cbDocumentType.validate();
		}
		catch (Exception e) {
			logger.info("validaAll :document type is empty--->" + e);
		}
	}
	
	/**
	 * this method used to load the opportunity type in company look up list based on status ,company id module id and
	 * look up code
	 */
	private void loadOppertunityTypeByLookUpList() {
		try {
			List<CompanyLookupDM> statusLookUpList = servicecompany.getCompanyLookUpByLookUp(companyId, moduleId,
					"Active", "CM_DOCTYPE");
			beanCompLookUp = new BeanContainer<String, CompanyLookupDM>(CompanyLookupDM.class);
			beanCompLookUp.setBeanIdProperty("lookupname");
			beanCompLookUp.addAll(statusLookUpList);
			cbDocumentType.setContainerDataSource(beanCompLookUp);
		}
		catch (Exception e) {
			logger.info("load company look up details" + e);
		}
	}
	
	/**
	 * this method handles button click event
	 * 
	 * @param ClickEvent
	 *            event
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		if (btndelete == event.getButton()) {
			deletedetails();
		}
		if (btnSave == event.getButton()) {
			try {
				saveDocumentDetails();
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.info("Error on saveDocumentDetails() function--->" + e);
			}
		} else if (btnDocument == event.getButton()) {
			new UploadDocumentUI(vlDocument);
			UI.getCurrent().addWindow(wndDocuments);
		} else if (btnEdit == event.getButton()) {
			try {
				editDocumentDetails();
				btnSave.setCaption("Update");
				UI.getCurrent().addWindow(wndDocuments);
			}
			catch (Exception e) {
				logger.info("edit docuemnt details" + e);
			}
		}
	}
}
