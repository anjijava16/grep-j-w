package com.gnts.base.mst; 

import java.io.File; 
import java.io.FileInputStream; 
import java.util.ArrayList; 
import java.util.Collection; 
import java.util.Iterator; 
import java.util.List; 

import org.apache.log4j.Logger; 
import org.springframework.context.ApplicationContext; 
import org.springframework.context.support.ClassPathXmlApplicationContext; 
import org.vaadin.haijian.CSVExporter; 
import org.vaadin.haijian.ExcelExporter; 
import org.vaadin.haijian.PdfExporter; 
import org.vaadin.tokenfield.TokenField; 

import com.gnts.base.domain.mst.CompanyDM; 
import com.gnts.base.domain.mst.CurrencyDM; 
import com.gnts.base.domain.mst.ProductCategoryDM; 
import com.gnts.base.domain.mst.ProductDM; 
import com.gnts.base.domain.mst.ProductSpecificationDM; 
import com.gnts.base.service.mst.CurrencyService; 
import com.gnts.base.service.mst.ProductCategoryService; 
import com.gnts.base.service.mst.ProductService; 
import com.gnts.base.service.mst.ProductSpecificationService; 
import com.gnts.erputil.Common; 
import com.gnts.erputil.helper.SpringContextHelper; 
import com.gnts.erputil.ui.AuditRecordsApp; 
import com.gnts.erputil.ui.PanelGenerator; 
import com.gnts.erputil.ui.UploadUI; 
import com.gnts.erputil.ui.UploadUI1; 
import com.gnts.erputil.validations.DateUtils; 
import com.gnts.erputil.constants.ApplicationConstants; 
import com.gnts.erputil.constants.ColorChangeColumnGenerator; 
import com.gnts.erputil.constants.DateColumnGenerator; 
import com.gnts.erputil.constants.GalleryChangeColumnGenerator; 
import com.gnts.erputil.domain.StatusDM; 
import com.gnts.erputil.domain.YesNoDM; 
import com.gnts.gcat.domain.mst.ProductColorDM; 
import com.gnts.gcat.domain.mst.ProductGalleryDM; 
import com.gnts.gcat.domain.txn.TagsDM; 
import com.gnts.gcat.service.mst.ProductColorService; 
import com.gnts.gcat.service.mst.ProductGalleryService; 
import com.gnts.gcat.service.txn.TagsService; 
import com.vaadin.data.Item; 
import com.vaadin.data.Property; 
import com.vaadin.data.Property.ValueChangeEvent; 
import com.vaadin.data.util.BeanItem; 
import com.vaadin.data.util.BeanItemContainer; 
import com.vaadin.event.ItemClickEvent; 
import com.vaadin.event.ItemClickEvent.ItemClickListener; 
import com.vaadin.event.LayoutEvents.LayoutClickEvent; 
import com.vaadin.event.LayoutEvents.LayoutClickListener; 
import com.vaadin.event.ShortcutAction.KeyCode; 
import com.vaadin.server.ThemeResource; 
import com.vaadin.server.UserError; 
import com.vaadin.server.VaadinService; 
import com.vaadin.shared.ui.MarginInfo; 
import com.vaadin.shared.ui.colorpicker.Color; 
import com.vaadin.shared.ui.label.ContentMode; 
import com.vaadin.ui.Alignment; 
import com.vaadin.ui.Button; 
import com.vaadin.ui.Button.ClickEvent; 
import com.vaadin.ui.ColorPickerArea; 
import com.vaadin.ui.ComboBox; 
import com.vaadin.ui.CssLayout; 
import com.vaadin.ui.FormLayout; 
import com.vaadin.ui.GridLayout; 
import com.vaadin.ui.HorizontalLayout; 
import com.vaadin.ui.Label; 
import com.vaadin.ui.Notification; 
import com.vaadin.ui.TabSheet; 
import com.vaadin.ui.Table; 
import com.vaadin.ui.TextField; 
import com.vaadin.ui.UI; 
import com.vaadin.ui.VerticalLayout; 
import com.vaadin.ui.Window; 
import com.vaadin.ui.Button.ClickListener; 
import com.vaadin.ui.components.colorpicker.ColorChangeEvent; 
import com.vaadin.ui.components.colorpicker.ColorChangeListener; 
import com.vaadin.ui.themes.Runo; 

public class Product implements ClickListener { 

	private ApplicationContext appContext = new ClassPathXmlApplicationContext( 
			"applicationContext-core.xml"); 
	private static final long serialVersionUID = 1L; 

	private ProductService servicebeanproduct = (ProductService) SpringContextHelper.getBean("Product"); 
	private ProductCategoryService servicebeanCategory = (ProductCategoryService) appContext.getBean("ProductCategory"); 
	private CurrencyService servicebeanCurrency = (CurrencyService) appContext.getBean("currency"); 
	private TagsService servicebeanTags = (TagsService) appContext.getBean("tags"); 
	private ProductSpecificationService servicebeanProdSpec = (ProductSpecificationService) appContext.getBean("prodspec"); 
	private ProductColorService servicebeanProdColor = (ProductColorService) appContext.getBean("ProductColor"); 
	private ProductGalleryService servicebeanProdGallery = (ProductGalleryService) appContext.getBean("prodgallery"); 
	private Button btnAdd; 
	private Button btnEdit; 
	private Button btnCancel; 
	private Button btnSearch; 
	private Button btnReset; 
	private Button btnSave; 
    private Button btnDownload,  btnAuditrRecords,btnHome,btnBack; 
	private Button btnaddSpec, btnEditspec, btnAuditrRecordSpec,btnHomeSpec, btnDownloadSpec; 
	private Button btnsaveColor, btndeleteColor; 
	private Button btnsaveGallery, btndeleteGallery; 
	private TextField tfprodname, tfspeccode, tfspecdesc; 
	private ComboBox cbcategoryS, cbstatusS, cbstatuspec; 
	private TextField tfaddprodname, tfproddesc, tfprice, tfuom, tfbrandname; 
	private ComboBox cbaddcategory, cbaddcurrency, cbaddvisual, cbaddview,cbaddstatus, cbaddparentprod; 
	private Table tblproduct, tblspec, tblcolor,tblgallery; 
	private TokenField totag; 
	private TabSheet tabsheet,tabsheet1; 
	private ColorPickerArea pickerarea; 

	private VerticalLayout vlTableLayout = new VerticalLayout(); 
	private FormLayout flSearchform1, flSearchform2, flSearchform3, 
			flMainform1, f2Mainform2, f3Mainform3, f4Mainform4; 
	private VerticalLayout vlMainLayout, vlSearchLayout, vlTableSpecLayout,vlTableForm, 
			vlAudit, vlAuditSpec, vlspeclayout,vlGallLayout,vlMainColorLayout,vlMainGallLayout,vlspec,hlmerg; 
	private HorizontalLayout hlimage = new HorizontalLayout(); 
	private VerticalLayout hlimage1 = new VerticalLayout(); 
	private HorizontalLayout hlGallery = new HorizontalLayout(); 
	private HorizontalLayout hlcolor1 = new HorizontalLayout(); 
	private HorizontalLayout hlcolor2 = new HorizontalLayout(); 
	private HorizontalLayout hlAddEditLayout, hlAddEditLayoutSpec, hlspec,hlFileDownload,hlBreadCrumbs; 

	private int total = 0; 
	private int total1 = 0; 
	private Label lblButton = new Label(); 
	private Label  lbl; 
	private Label lblFormTittle,lblFormTitle1,lblAddEdit; 
	private Label lblSaveNotification, lblNotificationIcon; 
	private Long productID; 
	private Long companyid; 
	private String username, basepath1, basepath, basepath2; 
	private String screenName; 
	private Long searchcateid; 
	private Long Longparentprodid; 
	private String pkValue,pkValue1; 
	public static boolean filevalue2 = false; 
	public static boolean filevalue3 = false; 
	private String colorcode = ""; 
	private Label test; 
	private Label lblcolor; 

	private ProductCategoryDM selectCategory; 
	private CompanyDM selectCompany; 
	private CurrencyDM selectCurrency; 
	private ProductDM selectProduct; 
	private TagsDM selectTags; 

	private BeanItemContainer<StatusDM> beanStatus = null; 
	private BeanItemContainer<YesNoDM> beanYesNo = null; 
	private BeanItemContainer<ProductDM> beanProduct = null; 
	private BeanItemContainer<ProductCategoryDM> beanCategory = null; 
	private BeanItemContainer<TagsDM> beanTag = null; 
	private BeanItemContainer<CurrencyDM> beanCurrency = null; 
	private BeanItemContainer<ProductSpecificationDM> beanSpecification = null; 
	private BeanItemContainer<ProductColorDM> beanColor = null; 
	private BeanItemContainer<ProductGalleryDM> beanGallery = null; 

	private ExcelExporter excelexporter = new ExcelExporter(); 
	private CSVExporter csvexporter = new CSVExporter(); 
	private PdfExporter pdfexporter = new PdfExporter(); 
	private ExcelExporter excelexporter1 = new ExcelExporter(); 
	private CSVExporter csvexporter1 = new CSVExporter(); 
	private PdfExporter pdfexporter1 = new PdfExporter(); 
	private Window notifications; 
	private Window notifications1; 

	private HorizontalLayout hlButtonLayout1; 

	private Logger logger = Logger.getLogger(Product.class); 

	public Product() { 
		username = UI.getCurrent().getSession().getAttribute("loginUserName") 
				.toString(); 
		companyid = Long.valueOf(UI.getCurrent().getSession() 
				.getAttribute("loginCompanyId").toString()); 
		screenName = UI.getCurrent().getSession().getAttribute("screenName") 
				.toString(); 
		VerticalLayout clArgumentLayout = (VerticalLayout) UI.getCurrent().getSession() 
				.getAttribute("clLayout"); 
		HorizontalLayout hlHeaderLayout = (HorizontalLayout) UI.getCurrent() 
				.getSession().getAttribute("hlLayout"); 

		buildview(clArgumentLayout, hlHeaderLayout); 

		//Product and Product Gallery Image 
		basepath = VaadinService.getCurrent().getBaseDirectory() 
				.getAbsolutePath(); 
		basepath1 = basepath + "/VAADIN/themes/gerp/img/Upload.jpg"; 

		//Product Document 
		basepath = VaadinService.getCurrent().getBaseDirectory() 
				.getAbsolutePath(); 
		basepath2 = basepath + "/VAADIN/themes/gerp/img/Document.pdf"; 
		 
	} 

	/* 
	 * buildMainview()-->for screen UI design 
	 * 
	 * @param CssLayout layoutPage, HorizontalLayout footerlayout 
	 */ 
	private void buildview(VerticalLayout clArgumentLayout, 
			HorizontalLayout hlHeaderLayout) { 
		hlHeaderLayout.removeAllComponents(); 

		lblSaveNotification = new Label(); 
		lblSaveNotification.setContentMode(ContentMode.HTML); 
		lblNotificationIcon = new Label(); 
		 
		lblFormTitle1=new Label(); 
		lblFormTitle1.setContentMode(ContentMode.HTML); 

		lblFormTitle1.setValue("&nbsp;&nbsp;<b>" + screenName 
				+ "</b>&nbsp;::&nbsp;"); 
		lblAddEdit=new Label(); 
		lblAddEdit.setContentMode(ContentMode.HTML); 
		 
		//Product Buttons 
		btnAdd = new Button("Add", this); 
		btnCancel = new Button("Cancel", this); 
		btnSearch = new Button("Search", this); 
		btnSave = new Button("Save", this); 
		btnSave.setStyleName("styles.css/buttonrefresh"); 
		btnEdit = new Button("Edit", this); 
		btnEdit.setEnabled(false); 
		btnReset = new Button("Reset", this); 
		btnDownload = new Button("Download", this); 
		btnBack=new Button("Search",this); 

		btnSave.setStyleName("savebt"); 
		btnAdd.addStyleName("add"); 
		btnCancel.addStyleName("cancelbt"); 
		btnEdit.addStyleName("editbt"); 
		btnReset.addStyleName("resetbt"); 
		btnSearch.setStyleName("searchbt"); 
		btnDownload.setStyleName("downloadbt"); 
		btnBack.setStyleName("link"); 
	 
		 
		btnAuditrRecords = new Button("Audit History", this); 
		btnAuditrRecords.setStyleName("hostorybtn"); 

		btnHome = new Button("Home", this); 
		btnHome.setStyleName("homebtn"); 
		btnHome.setEnabled(false); 
		 
		//Product Specification Buttons 
		btnaddSpec = new Button("Add", this); 
		btnEditspec = new Button("Edit", this); 
		btnEditspec.setEnabled(false); 
		btnDownloadSpec = new Button("Download", this); 
		 
		btnaddSpec.setStyleName("add"); 
		btnEditspec.setStyleName("editbt"); 
		btnDownloadSpec.setStyleName("downloadbt"); 
		 
		btnAuditrRecordSpec = new Button("Audit History", this); 
		btnAuditrRecordSpec.setStyleName("hostorybtn"); 

		btnHomeSpec = new Button("Home", this); 
		btnHomeSpec.setStyleName("homebtn"); 
		btnHomeSpec.setEnabled(false); 
		 
		//Product Color Buttons 
		btnsaveColor = new Button("Save", this); 
		btndeleteColor = new Button("Delete", this); 
		btndeleteColor.setEnabled(false); 
		 
		btnsaveColor.setStyleName("savebt"); 
		btndeleteColor.setStyleName("cancelbt"); 
		 
		 
		//Product Gallery Buttons 
		btnsaveGallery= new Button("Save", this); 
		btndeleteGallery= new Button("Delete", this); 
		btndeleteGallery.setEnabled(false); 
		 
		btnsaveGallery.setStyleName("savebt"); 
		btndeleteGallery.setStyleName("cancelbt"); 

		//Product Table 
		tblproduct = new Table(); 
		tblproduct.setImmediate(true); 
		tblproduct.setSelectable(true); 
		tblproduct.setStyleName(Runo.TABLE_SMALL); 
		tblproduct.setColumnCollapsingAllowed(true); 
		//tblproduct.setPageLength(8); 
		tblproduct.setSizeFull(); 
		tblproduct.setFooterVisible(true); 

		//Product Download 
		btnDownload.addClickListener(new ClickListener() { 
			private static final long serialVersionUID = 1L; 

			public void buttonClick(ClickEvent event) { 
				event.getButton().removeStyleName("unread"); 
				if (notifications != null && notifications.getUI() != null) 
					notifications.close(); 
				else { 
					buildNotifications(event); 
					UI.getCurrent().addWindow(notifications); 
					notifications.focus(); 
					((VerticalLayout) UI.getCurrent().getContent()) 
							.addLayoutClickListener(new LayoutClickListener() { 

								private static final long serialVersionUID = 1L; 

								@Override 
								public void layoutClick(LayoutClickEvent event) { 
									notifications.close(); 
									((VerticalLayout) UI.getCurrent() 
											.getContent()) 
											.removeLayoutClickListener(this); 
								} 
							}); 
				} 

			} 
		}); 

		//Product Specification Download 
		btnDownloadSpec.addClickListener(new ClickListener() { 
			private static final long serialVersionUID = 1L; 

			public void buttonClick(ClickEvent event) { 
				event.getButton().removeStyleName("unread"); 
				if (notifications1 != null && notifications1.getUI() != null) 
					notifications1.close(); 
				else { 
					buildNotifications1(event); 
					UI.getCurrent().addWindow(notifications1); 
					notifications1.focus(); 
					((VerticalLayout) UI.getCurrent().getContent()) 
							.addLayoutClickListener(new LayoutClickListener() { 

								private static final long serialVersionUID = 1L; 

								@Override 
								public void layoutClick(LayoutClickEvent event) { 
									notifications1.close(); 
									((VerticalLayout) UI.getCurrent() 
											.getContent()) 
											.removeLayoutClickListener(this); 
								} 
							}); 
				} 

			} 
		}); 

		// search panel components 

		tfprodname = new TextField("Product Name"); 
		tfprodname.setWidth("200"); 
		 

		cbcategoryS = new ComboBox("Product Category"); 
		cbcategoryS.setItemCaptionPropertyId("catename"); 
		loadCategoryList(); 
		cbcategoryS.addValueChangeListener(new Property.ValueChangeListener() { 

			public void valueChange(ValueChangeEvent event) { 

				final Object itemId = event.getProperty().getValue(); 
				if (itemId != null) { 
					final BeanItem<?> item = (BeanItem<?>) cbcategoryS 
							.getItem(itemId); 
					selectCategory = (ProductCategoryDM) item.getBean(); 
					searchcateid = selectCategory.getCateid(); 
				} 
			} 
		}); 
		cbcategoryS.setImmediate(true); 
		cbcategoryS.setNullSelectionAllowed(false); 
		cbcategoryS.setWidth("200"); 

		cbstatusS = new ComboBox("Status"); 
		cbstatusS.setItemCaptionPropertyId("desc"); 
		cbstatusS.setImmediate(true); 
		cbstatusS.setNullSelectionAllowed(false); 
		beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class); 
		beanStatus.addAll(Common.listStatus); 
		cbstatusS.setContainerDataSource(beanStatus); 
		cbstatusS.setWidth("100"); 

		flSearchform1 = new FormLayout(); 
		flSearchform1.addComponent(tfprodname); 
		flSearchform2 = new FormLayout(); 
		flSearchform2.addComponent(cbstatusS); 
		flSearchform3 = new FormLayout(); 
		flSearchform3.addComponent(cbcategoryS); 

		HorizontalLayout hlSearch = new HorizontalLayout(); 
		hlSearch.addComponent(flSearchform1); 
		hlSearch.addComponent(flSearchform3); 
		hlSearch.addComponent(flSearchform2); 
		hlSearch.setSpacing(true); 
		hlSearch.setMargin(true); 

		VerticalLayout hlSearchButtonLayout = new VerticalLayout(); 
		hlSearchButtonLayout.setSpacing(true); 
		hlSearchButtonLayout.addComponent(btnSearch); 
		hlSearchButtonLayout.addComponent(btnReset); 
		hlSearchButtonLayout.setWidth("100"); 
		hlSearchButtonLayout.addStyleName("topbarthree"); 
		hlSearchButtonLayout.setMargin(true); 

		HorizontalLayout hlSearchPanel = new HorizontalLayout(); 
		hlSearchPanel.setSizeFull(); 
		hlSearchPanel.setSpacing(true); 
		hlSearchPanel.addComponent(hlSearch); 
		hlSearchPanel.setComponentAlignment(hlSearch, Alignment.MIDDLE_LEFT); 
		hlSearchPanel.addComponent(hlSearchButtonLayout); 
		hlSearchPanel.setComponentAlignment(hlSearchButtonLayout, 
				Alignment.MIDDLE_RIGHT); 
		hlSearchPanel.setExpandRatio(hlSearchButtonLayout, 1); 

		final VerticalLayout vlSearchPanel = new VerticalLayout(); 
		vlSearchPanel.setSpacing(true); 
		vlSearchPanel.setSizeFull(); 
		vlSearchPanel.addComponent(hlSearchPanel); 

		vlSearchLayout = new VerticalLayout(); 
		vlSearchLayout.addComponent(PanelGenerator.createPanel(vlSearchPanel)); 
		vlSearchLayout.setMargin(true); 

		// Main panel components 

		tfaddprodname = new TextField("Product Name"); 
		tfaddprodname.setWidth("200"); 
		 

		tfproddesc = new TextField("Product Desc"); 
		tfproddesc.setWidth("200"); 
	 

		tfprice = new TextField("Price"); 
		tfprice.setWidth("120"); 
		tfprice.setHeight("24"); 

		tfuom = new TextField("UOM"); 
		tfuom.setWidth("200"); 
		 

		tfbrandname = new TextField("Brand Name"); 
		tfbrandname.setWidth("200"); 
		 

		cbaddcategory = new ComboBox("Product Category"); 
		cbaddcategory.setWidth("200"); 
		cbaddcategory.setItemCaptionPropertyId("catename"); 
		loadCategoryaddList(); 
		cbaddcategory 
				.addValueChangeListener(new Property.ValueChangeListener() { 

					public void valueChange(ValueChangeEvent event) { 

						final Object itemId = event.getProperty().getValue(); 
						if (itemId != null) { 
							final BeanItem<?> item = (BeanItem<?>) cbaddcategory 
									.getItem(itemId); 
							selectCategory = (ProductCategoryDM) item 
									.getBean(); 
						} 
					} 
				}); 
		cbaddcategory.setImmediate(true); 
		cbaddcategory.setNullSelectionAllowed(false); 

		cbaddcurrency = new ComboBox(""); 
		cbaddcurrency.setWidth("80"); 
		cbaddcurrency.setHeight(" "); 
		cbaddcurrency.setItemCaptionPropertyId("ccyname"); 
		loadCurrencyaddList(); 
		cbaddcurrency 
				.addValueChangeListener(new Property.ValueChangeListener() { 

					public void valueChange(ValueChangeEvent event) { 

						final Object itemId = event.getProperty().getValue(); 
						if (itemId != null) { 
							final BeanItem<?> item = (BeanItem<?>) cbaddcurrency 
									.getItem(itemId); 
							selectCurrency = (CurrencyDM) item.getBean(); 
						} 
					} 
				}); 
		cbaddcurrency.setImmediate(true); 
		cbaddcurrency.setNullSelectionAllowed(false); 

		cbaddparentprod = new ComboBox("Parent Product"); 
		cbaddparentprod.setWidth("200"); 
		cbaddparentprod.setItemCaptionPropertyId("prodname"); 
		loadParentProdList(); 
		cbaddparentprod 
				.addValueChangeListener(new Property.ValueChangeListener() { 

					public void valueChange(ValueChangeEvent event) { 

						final Object itemId = event.getProperty().getValue(); 
						if (itemId != null) { 
							final BeanItem<?> item = (BeanItem<?>) cbaddparentprod 
									.getItem(itemId); 
							selectProduct = (ProductDM) item.getBean(); 
							// Longparentprodid=selectProduct.getParentprodid(); 

							if (selectProduct.getCateid() != null) { 

								ProductCategoryDM uom1 = selectProduct 
										.getCateid(); 
								Collection<?> uomid1 = cbaddcategory 
										.getItemIds(); 
								for (Iterator<?> iterator = uomid1.iterator(); iterator 
										.hasNext();) { 
									Object itemId1 = (Object) iterator.next(); 

									BeanItem<?> item1 = (BeanItem<?>) cbaddcategory 
											.getItem(itemId1); 
									// Get the actual bean and use the data 

									ProductCategoryDM st = (ProductCategoryDM) item1 
											.getBean(); 
									System.out.println(uom1.getCateid() + "===" 
											+ st.getCateid()); 
									if (uom1 != null 
											&& uom1.getCateid().equals( 
													st.getCateid())) { 
										cbaddcategory.setReadOnly(false); 
										cbaddcategory.setValue(itemId1); 
										cbaddcategory.setReadOnly(true); 
									} 
								} 
 
							} 
						} 
					} 
				}); 
		cbaddparentprod.setImmediate(true); 
		cbaddparentprod.setNullSelectionAllowed(false); 

		cbaddvisual = new ComboBox("Visualizer"); 
		cbaddvisual.setItemCaptionPropertyId("desc"); 
		cbaddvisual.setImmediate(true); 
		cbaddvisual.setNullSelectionAllowed(false); 
		beanYesNo = new BeanItemContainer<YesNoDM>(YesNoDM.class); 
		beanYesNo.addAll(Common.listyesno); 
		cbaddvisual.setContainerDataSource(beanYesNo); 
		cbaddvisual.setWidth("200"); 

		cbaddview = new ComboBox("View 360"); 
		cbaddview.setItemCaptionPropertyId("desc"); 
		cbaddview.setImmediate(true); 
		cbaddview.setNullSelectionAllowed(false); 
		beanYesNo = new BeanItemContainer<YesNoDM>(YesNoDM.class); 
		beanYesNo.addAll(Common.listyesno); 
		cbaddview.setContainerDataSource(beanYesNo); 
		cbaddview.setWidth("200"); 

		cbaddstatus = new ComboBox("Status"); 
		cbaddstatus.setItemCaptionPropertyId("desc"); 
		cbaddstatus.setImmediate(true); 
		cbaddstatus.setNullSelectionAllowed(false); 
		beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class); 
		beanStatus.addAll(Common.listStatus); 
		cbaddstatus.setContainerDataSource(beanStatus); 
		cbaddstatus.setWidth("200"); 

		flMainform1 = new FormLayout(); 
		flMainform1.addComponent(tfaddprodname); 
		flMainform1.addComponent(cbaddparentprod); 
		flMainform1.addComponent(cbaddcategory); 
		flMainform1.addComponent(tfproddesc); 
		//flMainform1.setSpacing(true); 

		 
	 
	 
		 
	//	f2Mainform2 = new FormLayout(); 
		flMainform1.addComponent(tfbrandname); 
		flMainform1.addComponent(tfprice); 
		HorizontalLayout hlmerg = new HorizontalLayout(); 
			hlmerg.addComponent(tfprice); 
		    hlmerg.addComponent(cbaddcurrency); 
		   
		    
		    flMainform1.addComponent(hlmerg); 
		    flMainform1.addComponent(tfuom); 
		    flMainform1.setSpacing(true); 

	//	f3Mainform3 = new FormLayout(); 
		    flMainform1.addComponent(cbaddvisual); 
		    flMainform1.addComponent(cbaddview); 
		    flMainform1.addComponent(cbaddstatus); 
		    tabsheet1 = new TabSheet(); 
		    tabsheet1.addTab(hlspec, "Product Specification", null); 
		   
		 //   flMainform1.addComponent(totag); 
		    flMainform1.setSpacing(true); 
		    
		    f3Mainform3 = new FormLayout(); 
		    //f3Mainform3.addComponent(tabsheet1); 
		hlimage1.setCaption("Product Document"); 
		hlimage.setMargin(true); 
		 
		HorizontalLayout hlupload = new HorizontalLayout(); 
		hlupload.addComponent(hlimage); 
		hlimage.setCaption(null); 
		hlupload.addComponent(hlimage1); 
		hlupload.setComponentAlignment(hlimage1, Alignment.BOTTOM_CENTER); 
		hlimage1.setCaption(null); 
		hlupload.setSpacing(true); 

		HorizontalLayout hlMain = new HorizontalLayout(); 
		hlMain.addComponent(flMainform1); 
		//hlMain.addComponent(f2Mainform2); 
		hlMain.addComponent(f3Mainform3); 
		hlMain.setSpacing(true); 

		final GridLayout glGridLayout1 = new GridLayout(); 
		glGridLayout1.setSizeFull(); 
		glGridLayout1.addComponent(hlMain); 
		f3Mainform3.addComponent(PanelGenerator.createPanel(hlupload)); 

		VerticalLayout vlgrid = new VerticalLayout(); 
		vlgrid.addComponent(glGridLayout1); 

		// Tag components 

		lbl = new Label(); 
		lbl.setHeight("30px"); 

		totag = new TokenField("Keyword"); 
		totag.setWidth("200"); 
		totag.setTokenCaptionPropertyId("tagdesc"); 
		loadTagDescList(); 
 
		VerticalLayout vltag = new VerticalLayout(); 
		vltag.addComponent(lbl); 
		vltag.addComponent(totag); 
		vltag.setSpacing(true); 

		HorizontalLayout hltags = new HorizontalLayout(); 
		hltags.setCaption("Search Keywords"); 
		hltags.addComponent(vlgrid); 
		hltags.addComponent(vltag); 
		hltags.setSpacing(true); 
		hltags.setMargin(true); 

		vlMainLayout = new VerticalLayout(); 
		vlMainLayout.addComponent(PanelGenerator.createPanel(hltags)); 
		vlMainLayout.setMargin(true); 
		vlMainLayout.setVisible(false); 

           //	 Product Download and Tittle Components 

		hlFileDownload = new HorizontalLayout(); 
		hlFileDownload.setSpacing(true); 
		hlFileDownload.addComponent(btnDownload); 
		hlFileDownload.setComponentAlignment(btnDownload, 
				Alignment.MIDDLE_CENTER); 

		HorizontalLayout hlTableTittleLayout = new HorizontalLayout(); 
		hlTableTittleLayout.addComponent(btnHome); 
		hlTableTittleLayout.addComponent(btnAdd); 
		hlTableTittleLayout.addComponent(btnEdit); 
		hlTableTittleLayout.addComponent(btnAuditrRecords); 
	 

		hlAddEditLayout = new HorizontalLayout(); 
		hlAddEditLayout.addStyleName("topbarthree"); 
		hlAddEditLayout.setWidth("100%"); 
		hlAddEditLayout.addComponent(hlTableTittleLayout); 
		hlAddEditLayout.addComponent(hlFileDownload); 
		hlAddEditLayout.setComponentAlignment(hlFileDownload, 
				Alignment.MIDDLE_RIGHT); 
		hlAddEditLayout.setHeight("30px"); 

		vlAudit = new VerticalLayout(); 
		vlAudit.setVisible(false); 
		 
		vlTableForm = new VerticalLayout(); 
		vlTableForm.setSizeFull(); 
		vlTableForm.setMargin(new MarginInfo(false, true, false, true)); 
		vlTableForm.addComponent(hlAddEditLayout); 
		vlTableForm.addComponent(tblproduct); 
		vlTableForm.setExpandRatio(tblproduct, 1); 
		//vlTableForm.addComponent(vlAudit); 
 
		vlTableLayout.setStyleName(Runo.PANEL_LIGHT); 
		vlTableLayout.addComponent(vlTableForm); 

		// specification tab components 

		tfspeccode = new TextField("Code"); 
		tfspeccode.setWidth("170"); 
		tfspeccode.setInputPrompt("Code"); 
		tfspeccode.setRequired(true); 

		tfspecdesc = new TextField("Description"); 
		tfspecdesc.setWidth("170"); 
		tfspecdesc.setInputPrompt("Description"); 

		cbstatuspec = new ComboBox("Status"); 
		cbstatuspec.setInputPrompt(ApplicationConstants.selectDefault); 
		cbstatuspec.setItemCaptionPropertyId("desc"); 
		cbstatuspec.setImmediate(true); 
		cbstatuspec.setNullSelectionAllowed(false); 
		beanStatus = new BeanItemContainer<StatusDM>(StatusDM.class); 
		beanStatus.addAll(Common.listStatus); 
		cbstatuspec.setContainerDataSource(beanStatus); 
		cbstatuspec.setValue(Common.getStatus(Common.ACTIVE_CODE)); 
		cbstatuspec.setWidth("170"); 
		cbstatuspec.setRequired(true); 

		HorizontalLayout hlspecbutton = new HorizontalLayout(); 
		hlspecbutton.addComponent(btnaddSpec); 
		hlspecbutton.addComponent(btnEditspec); 
		hlspecbutton.setSpacing(true); 

	 
		 

		f4Mainform4 = new FormLayout(); 
		f4Mainform4.addComponent(tfspeccode); 
		f4Mainform4.addComponent(tfspecdesc); 
		f4Mainform4.addComponent(cbstatuspec); 
		f4Mainform4.addComponent(hlspecbutton); 
		f4Mainform4.setSpacing(true); 
		f4Mainform4.setMargin(true); 

		tblspec = new Table(); 
		tblspec.setImmediate(true); 
		tblspec.setSelectable(true); 
		tblspec.setStyleName(Runo.TABLE_SMALL); 
		tblspec.setColumnCollapsingAllowed(true); 
		tblspec.setSizeFull(); 
		//tblspec.setPageLength(4); 

		/*final VerticalLayout vlTableSpec = new VerticalLayout(); 
		vlTableSpec.setSizeFull(); 
		vlTableSpec.setMargin(true); 
		vlTableSpec.addComponent(tblspec);*/ 

		vlspec = new VerticalLayout(); 
		vlspec.addComponent(f4Mainform4); 
		vlspec.setSpacing(true); 
		vlspec.setSizeFull(); 

		HorizontalLayout hlFileDownloadSpec = new HorizontalLayout(); 
		hlFileDownloadSpec.setSpacing(true); 
		hlFileDownloadSpec.addComponent(btnDownloadSpec); 
		hlFileDownloadSpec.setComponentAlignment(btnDownloadSpec, 
				Alignment.MIDDLE_CENTER); 

		HorizontalLayout hlTableTittleLayoutSpec = new HorizontalLayout(); 
		hlTableTittleLayoutSpec.addComponent(btnHomeSpec); 
		hlTableTittleLayoutSpec.addComponent(btnAuditrRecordSpec); 

		hlAddEditLayoutSpec = new HorizontalLayout(); 
		hlAddEditLayoutSpec.addStyleName("topbarthree"); 
		hlAddEditLayoutSpec.setWidth("100%"); 
		hlAddEditLayoutSpec.addComponent(hlTableTittleLayoutSpec); 
		hlAddEditLayoutSpec.addComponent(hlFileDownloadSpec); 
		hlAddEditLayoutSpec.setComponentAlignment(hlFileDownloadSpec, 
				Alignment.MIDDLE_RIGHT); 
		hlAddEditLayoutSpec.setHeight("30px"); 

		vlAuditSpec = new VerticalLayout(); 
		 
		vlspeclayout = new VerticalLayout(); 
		vlspeclayout.addComponent(hlAddEditLayoutSpec); 
		//vlspeclayout.addComponent(hlspec); 
		//vlspeclayout.setSizeFull(); 
		 

		hlspec = new HorizontalLayout(); 
		//vlspeclayout.addComponent(hlspec); 
		//vlspeclayout.setSizeFull(); 
		 

		hlspec = new HorizontalLayout(); 
		//vlspeclayout.addComponent(hlspec); 
		//vlspeclayout.setSizeFull(); 
		 

		hlspec = new HorizontalLayout(); 
		vlspeclayout.addComponent(vlAuditSpec); 
		vlAuditSpec.setVisible(false); 
		vlspeclayout.addComponent(tblspec); 
		//vlspeclayout.addComponent(hlspec); 
		//vlspeclayout.setSizeFull(); 
		 

		hlspec = new HorizontalLayout(); 
		hlspec.addComponent(vlspeclayout); 
		hlspec.addComponent(vlspec); 
		hlspec.setSpacing(true); 
		hlspec.setSizeFull(); 
		hlspec.setMargin(true); 



		// Color Tab Components 

		lblcolor = new Label(); 
		lblcolor.setValue("<B>&nbsp;&nbsp;Select Colour:</B>"); 
		lblcolor.setContentMode(ContentMode.HTML); 

		tblcolor = new Table(); 
		tblcolor.setImmediate(true); 
		tblcolor.setSelectable(true); 
		tblcolor.setStyleName(Runo.TABLE_SMALL); 
		tblcolor.setColumnCollapsingAllowed(true); 
		//tblcolor.setPageLength(3); 

		final VerticalLayout vlTableColor = new VerticalLayout(); 
		vlTableColor.setSizeFull(); 
		vlTableColor.setMargin(true); 
		vlTableColor.addComponent(tblcolor); 

		HorizontalLayout hlsavebutton = new HorizontalLayout(); 
		hlsavebutton.addComponent(btnsaveColor); 
		hlsavebutton.addComponent(btndeleteColor); 
		hlsavebutton.setSpacing(true); 

		HorizontalLayout hlcolor3 = new HorizontalLayout(); 
		hlcolor3.addComponent(lblcolor); 
		hlcolor3.setComponentAlignment(lblcolor, Alignment.BOTTOM_CENTER); 
		hlcolor3.addStyleName("lightgray"); 

	    pickerarea = new ColorPickerArea(); 
		pickerarea.setWidth("100px"); 
		pickerarea.setHeight("25px"); 
		pickerarea.setColor(new Color(0, 100, 200)); 
		hlcolor1.addComponent(pickerarea); 

		pickerarea.addColorChangeListener(new ColorChangeListener() { 
			@Override 
			public void colorChanged(ColorChangeEvent event) { 
				// Do something with the color 
				test = new Label(); 
				test.setValue("<font size=\"150\" color=" 
						+ event.getColor().getCSS() + "><B>&#9830</B></font>"); 
				test.setContentMode(ContentMode.HTML); 
				btnsaveColor.setEnabled(true); 
				hlcolor2.addComponent(test); 
				hlcolor1.addComponent(hlcolor2); 
				colorcode += event.getColor().getCSS() + ","; 

			} 
		}); 

		VerticalLayout vlsavebutton = new VerticalLayout(); 
		vlsavebutton.addComponent(hlcolor3); 
		vlsavebutton.addComponent(hlcolor1); 
		vlsavebutton.addComponent(hlsavebutton); 
		vlsavebutton.setSpacing(true); 
		vlsavebutton.setSizeFull(); 

		HorizontalLayout hlcolorlayout = new HorizontalLayout(); 
		hlcolorlayout.addComponent(vlTableColor); 
		hlcolorlayout.addComponent(vlsavebutton); 
		hlcolorlayout.setSpacing(true); 
		hlcolorlayout.setSizeFull(); 
		 
		vlMainColorLayout = new VerticalLayout(); 
		vlMainColorLayout.addComponent(PanelGenerator.createPanel(hlcolorlayout)); 
		vlMainColorLayout.setMargin(true); 
		 
		// Gallery Tab components 
		 
		tblgallery = new Table(); 
		tblgallery.setImmediate(true); 
		tblgallery.setSelectable(true); 
		tblgallery.setStyleName(Runo.TABLE_SMALL); 
		tblgallery.setColumnCollapsingAllowed(true); 
	//	tblgallery.setPageLength(2); 
		 
		 
		final VerticalLayout vlTableGallery = new VerticalLayout(); 
		vlTableGallery.setWidth("60%"); 
		vlTableGallery.setMargin(true); 
		vlTableGallery.addComponent(tblgallery); 
		 
		 
		 
		HorizontalLayout hlgallbutton = new HorizontalLayout(); 
		hlgallbutton.addComponent(btnsaveGallery); 
		hlgallbutton.addComponent(btndeleteGallery); 
		hlgallbutton.setSpacing(true); 
		 
		 
		HorizontalLayout hlGalUpload=new HorizontalLayout(); 
		hlGalUpload.addComponent(hlGallery); 
		hlGallery.setCaption(null); 
		hlGalUpload.setSpacing(true); 
		 
		vlGallLayout=new VerticalLayout(); 
		vlGallLayout.addComponent(hlGalUpload); 
		vlGallLayout.addComponent(hlgallbutton); 
		vlGallLayout.setSpacing(true); 
		vlGallLayout.setSizeFull(); 
		 
		HorizontalLayout hlGallLayout=new HorizontalLayout(); 
		hlGallLayout.addComponent(vlTableGallery); 
		hlGallLayout.addComponent(vlGallLayout); 
		//hlGallLayout.setComponentAlignment(vlGallLayout, Alignment.BOTTOM_CENTER); 
		hlGallLayout.setSpacing(true); 
		hlGallLayout.setSizeFull(); 
		 

		vlMainGallLayout = new VerticalLayout(); 
		vlMainGallLayout.addComponent(PanelGenerator.createPanel(hlGallLayout)); 
		vlMainGallLayout.setMargin(true); 
		 
		tabsheet = new TabSheet(); 
		tabsheet1.setHeight("320"); 
		tabsheet1.setWidth("580"); 
		 
		tabsheet.addTab(vlMainLayout, "Product Details", null); 
		tabsheet.addTab(hlspec, "Product Specification", null); 
		tabsheet.addTab(vlMainColorLayout, "Product Colour", null); 
	    tabsheet.addTab(vlMainGallLayout, "Product Gallery", null); 

		vlTableSpecLayout = new VerticalLayout(); 
		vlTableSpecLayout.setStyleName(Runo.PANEL_LIGHT); 
		vlTableSpecLayout.addComponent(tabsheet); 
		vlTableSpecLayout.setVisible(false); 
		vlTableSpecLayout.setSizeFull(); 

		//clArgumentLayout.addComponent(vlMainLayout); 
		clArgumentLayout.addComponent(vlSearchLayout); 
		clArgumentLayout.addComponent(vlTableLayout); 
		clArgumentLayout.addComponent(vlTableSpecLayout); 
		setTableProperties(); 
		setcolorTableProperties(); 
		setgalleryTableProperties(); 
		populatedAndConfig(false); 
		exportTableDate(); 
		 
		hlButtonLayout1 = new HorizontalLayout(); 
		hlButtonLayout1.addComponent(btnSave); 
		hlButtonLayout1.addComponent(btnCancel); 
		hlButtonLayout1.setVisible(false); 

		lblFormTittle = new Label(); 
		lblFormTittle.setContentMode(ContentMode.HTML); 
		lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName 
				+ "</b>&nbsp;::&nbsp;Search"); 

		hlBreadCrumbs=new HorizontalLayout(); 
		hlBreadCrumbs.addComponent(lblFormTitle1); 
		hlBreadCrumbs.addComponent(btnBack); 
		hlBreadCrumbs.setComponentAlignment(btnBack, Alignment.TOP_CENTER); 
		hlBreadCrumbs.addComponent(lblAddEdit); 
		hlBreadCrumbs.setComponentAlignment(lblAddEdit, Alignment.MIDDLE_CENTER); 
		hlBreadCrumbs.setVisible(false); 
		 
		HorizontalLayout hlNotificationLayout = new HorizontalLayout(); 
		hlNotificationLayout.addComponent(lblNotificationIcon); 
		hlNotificationLayout.setComponentAlignment(lblNotificationIcon, 
				Alignment.MIDDLE_CENTER); 
		hlNotificationLayout.addComponent(lblSaveNotification); 
		hlNotificationLayout.setComponentAlignment(lblSaveNotification, 
				Alignment.MIDDLE_CENTER); 
		hlHeaderLayout.addComponent(lblFormTittle); 
		hlHeaderLayout.setComponentAlignment(lblFormTittle, 
				Alignment.MIDDLE_LEFT); 
		hlHeaderLayout.addComponent(hlBreadCrumbs); 
		hlHeaderLayout.setComponentAlignment(hlBreadCrumbs, 
				Alignment.MIDDLE_LEFT); 
		hlHeaderLayout.addComponent(hlNotificationLayout); 
		hlHeaderLayout.setComponentAlignment(hlNotificationLayout, 
				Alignment.MIDDLE_LEFT); 
		hlHeaderLayout.addComponent(hlButtonLayout1); 
		hlHeaderLayout.setComponentAlignment(hlButtonLayout1, 
				Alignment.MIDDLE_RIGHT); 

		cbstatusS.setValue(Common.getStatus(Common.ACTIVE_CODE)); 
		 
	} 

	// Product Download 

	private void buildNotifications(ClickEvent event) { 
		notifications = new Window(); 
		VerticalLayout l = new VerticalLayout(); 
		l.setMargin(true); 
		l.setSpacing(true); 
		notifications.setWidth("165px"); 
		notifications.addStyleName("notifications"); 
		notifications.setClosable(false); 
		notifications.setResizable(false); 
		notifications.setDraggable(false); 
		notifications.setPositionX(event.getClientX() - event.getRelativeX()); 
		notifications.setPositionY(event.getClientY() - event.getRelativeY()); 
		notifications.setCloseShortcut(KeyCode.ESCAPE, null); 

		VerticalLayout vlDownload = new VerticalLayout(); 
		vlDownload.addComponent(excelexporter); 
		vlDownload.addComponent(csvexporter); 
		vlDownload.addComponent(pdfexporter); 
		vlDownload.setSpacing(true); 

		notifications.setContent(vlDownload); 

	} 

	// Specification Download 

	private void buildNotifications1(ClickEvent event) { 
		notifications1 = new Window(); 
		VerticalLayout l1 = new VerticalLayout(); 
		l1.setMargin(true); 
		l1.setSpacing(true); 
		notifications1.setWidth("165px"); 
		notifications1.addStyleName("notifications1"); 
		notifications1.setClosable(false); 
		notifications1.setResizable(false); 
		notifications1.setDraggable(false); 
		notifications1.setPositionX(event.getClientX() - event.getRelativeX()); 
		notifications1.setPositionY(event.getClientY() - event.getRelativeY()); 
		notifications1.setCloseShortcut(KeyCode.ESCAPE, null); 

		VerticalLayout vlDownload1 = new VerticalLayout(); 
		vlDownload1.addComponent(excelexporter1); 
		vlDownload1.addComponent(csvexporter1); 
		vlDownload1.addComponent(pdfexporter1); 
		vlDownload1.setSpacing(true); 

		notifications1.setContent(vlDownload1); 

	} 

	 

	private void exportTableDate() 
	{ 
		// Product Export 

				excelexporter.setTableToBeExported(tblproduct); 
				csvexporter.setTableToBeExported(tblproduct); 
				pdfexporter.setTableToBeExported(tblproduct); 
				excelexporter.setCaption("Microsoft Excel (XLS)"); 
				excelexporter.setStyleName("borderless"); 
				csvexporter.setCaption("Comma Dilimited (CSV)"); 
				csvexporter.setStyleName("borderless"); 
				pdfexporter.setCaption("Acrobat Document (PDF)"); 
				pdfexporter.setStyleName("borderless"); 

	} 
	// Product Table 

	public void populatedAndConfig(boolean search) { 

		//try { 
			tblproduct.removeAllItems(); 

			List<ProductDM> prodList = null; 
			if (search) { 

				prodList = new ArrayList<ProductDM>(); 
				String prodName = tfprodname.getValue().toString(); 

				String Status = null; 
				StatusDM st = (StatusDM) cbstatusS.getValue(); 

				try { 
					Status = st.getCode(); 
				} catch (Exception e) { 
					logger.info("status is empty on search"); 
				} 

				if (prodName != null || searchcateid != null || Status != null) { 

					prodList = servicebeanproduct.getProductList(companyid, 
							null, prodName, Status, searchcateid); 
					total = prodList.size(); 
				} 
				if (total == 0) { 

					lblSaveNotification.setValue("No Records found"); 
				} 
			} else { 

				prodList = servicebeanproduct.getProductList(companyid, null, 
						null, null, null); 
				total = prodList.size(); 

			} 
			lblButton 
					.setValue("<font size=\"2\" color=\"black\">No.of Records: </font> <font size=\"2\" color=\"#1E90FF\"> " 
							+ total + "</font>"); 
			beanProduct.addAll(prodList); 
			//beanProduct.addNestedContainerProperty("cateid.catename"); 
			tblproduct.setSelectable(true); 
			tblproduct.setContainerDataSource(beanProduct); 
			tblproduct.setVisibleColumns(new Object[] { "prodid", "prodname", 
					"cateName", "brandname", "prodstatus", 
					"lastupdateddt", "lastupdatedby" }); 

			tblproduct.setColumnHeaders(new String[] { "Ref.Id", 
					"Product Name", "Category Name", "Brand Name", "Status", 
					"Last Updated Date", "Last Updated By" }); 
			tblproduct.setColumnFooter("lastupdatedby", "No.of Records : " 
					+ total); 
			tblproduct.addItemClickListener(new ItemClickListener() { 

				public void itemClick(ItemClickEvent event) { 
					if (tblproduct.isSelected(event.getItemId())) { 
						btnEdit.setEnabled(false); 
						btnAdd.setEnabled(true); 

					} else { 
						btnEdit.setEnabled(true); 
						btnAdd.setEnabled(false); 

					} 
					btnSave.setCaption("Save"); 

				} 

			}); 
		//} catch (Exception e) { 
		//	logger.error("error during populate values on the table Product, The Error is ----->" 
		//			+ e); 
		//} 
	} 

	//Product Specification Table 

	public void populatedAndConfigSpec(Long productHeaderId) { 

		//try { 
			tblspec.removeAllItems(); 

			List<ProductSpecificationDM> specList = new ArrayList<ProductSpecificationDM>(); 

			specList = servicebeanProdSpec.getSpecList(productHeaderId,null,null); 
			total1 = specList.size(); 
			beanSpecification = new BeanItemContainer<ProductSpecificationDM>( 
					ProductSpecificationDM.class); 

			beanSpecification.addAll(specList); 
			tblspec.setSelectable(true); 
			tblspec.setContainerDataSource(beanSpecification); 
			tblspec.setVisibleColumns(new Object[] { "speccode", "specdesc", 
					"specstatus" }); 

			tblspec.setColumnHeaders(new String[] { "Code", "Description", 
					"Status" }); 
			tblspec.addItemClickListener(new ItemClickListener() { 

				public void itemClick(ItemClickEvent event) { 
					if (tblspec.isSelected(event.getItemId())) { 
						btnEditspec.setEnabled(false); 
						 

					} else { 
						btnEditspec.setEnabled(true); 
						 

					} 
					 

				} 

			}); 
		//} catch (Exception e) { 
		//	logger.error("error during populate values on the table Product Specification, The Error is ----->" 
			//		+ e); 
		//} 
	} 

	//Product Colour Table 

	public void populatedAndConfigColor(Long productHeaderId) { 

		//try { 
			tblcolor.removeAllItems(); 

			List<ProductColorDM> colorList = new ArrayList<ProductColorDM>(); 

			colorList.removeAll(colorList); 
			colorList = servicebeanProdColor.getProdColorList(productHeaderId,null,null); 
			total1 = colorList.size(); 

			beanColor = new BeanItemContainer<ProductColorDM>( 
					ProductColorDM.class); 

			beanColor.addAll(colorList); 
			tblcolor.setSelectable(true); 
			tblcolor.setContainerDataSource(beanColor); 
			tblcolor.setVisibleColumns(new Object[] { "colorcode" }); 

			tblcolor.setColumnHeaders(new String[] { "Color" }); 
			tblcolor.addItemClickListener(new ItemClickListener() { 

				public void itemClick(ItemClickEvent event) { 
					if (tblcolor.isSelected(event.getItemId())) { 
						btndeleteColor.setEnabled(false); 
						btnsaveColor.setEnabled(true); 

					} else { 
						btndeleteColor.setEnabled(true); 
						btnsaveColor.setEnabled(false); 

					} 
				} 

			}); 
		//} catch (Exception e) { 
		//	logger.error("error during populate values on the table ProductColor, The Error is ----->" 
		//			+ e); 
		//} 
	} 

	// Product Gallery Table 
	 
	public void populatedAndConfigGallery(Long productHeaderId) { 

		//try { 
			tblgallery.removeAllItems(); 
			List<ProductGalleryDM> galleryList = new ArrayList<ProductGalleryDM>(); 
			galleryList.removeAll(galleryList); 
			galleryList = servicebeanProdGallery.getProdGalleryList(productHeaderId,null,null); 
			total1 = galleryList.size(); 
			beanGallery = new BeanItemContainer<ProductGalleryDM>(ProductGalleryDM.class); 
			beanGallery.addAll(galleryList); 
			tblgallery.setSelectable(true); 
			tblgallery.setContainerDataSource(beanGallery); 
			tblgallery.setVisibleColumns(new Object[] { "prodimage" }); 
			tblgallery.setColumnHeaders(new String[] { "Product image" }); 
			tblgallery.addItemClickListener(new ItemClickListener() { 

				public void itemClick(ItemClickEvent event) { 
					if (tblgallery.isSelected(event.getItemId())) { 
						btndeleteGallery.setEnabled(false); 
						btnsaveGallery.setEnabled(true); 

					} else { 
						btndeleteGallery.setEnabled(true); 
						btnsaveGallery.setEnabled(false); 

					} 
				} 

			}); 
		//} catch (Exception e) { 
		//	logger.error("error during populate values on the table ProductGalleryDM, The Error is ----->" 
		//			+ e); 
		//} 
	} 
	 
	// Show the expected value in Grid tblproduct. 
	public void setTableProperties() { 

		beanProduct = new BeanItemContainer<ProductDM>(ProductDM.class); 
		tblproduct.addGeneratedColumn("lastupdateddt", 
				new DateColumnGenerator()); 

	} 

	// Show the expected value in Grid tblcolor. 
	public void setcolorTableProperties() { 

		beanColor = new BeanItemContainer<ProductColorDM>( 
				ProductColorDM.class); 
		tblcolor.addGeneratedColumn("colorcode", 
				new ColorChangeColumnGenerator()); 

	} 
	 
	// Show the expected value in Grid tblcolor. 
	public void setgalleryTableProperties() { 

		beanGallery = new BeanItemContainer<ProductGalleryDM>( 
				ProductGalleryDM.class); 
		tblgallery.addGeneratedColumn("prodimage", 
				new GalleryChangeColumnGenerator()); 

	} 

	// load the Category details for search 
	public void loadCategoryList() { 
		List<ProductCategoryDM> categorylist = servicebeanCategory 
				.getProdCategoryList(null, null, null,null,"F"); 
		beanCategory = new BeanItemContainer<ProductCategoryDM>( 
				ProductCategoryDM.class); 
		beanCategory.addAll(categorylist); 
		cbcategoryS.setContainerDataSource(beanCategory); 
	} 

	// load the Category details for add 
	public void loadCategoryaddList() { 
		List<ProductCategoryDM> categorylist = servicebeanCategory 
				.getProdCategoryList(null, null, null,null,"F"); 
		beanCategory = new BeanItemContainer<ProductCategoryDM>( 
				ProductCategoryDM.class); 
		beanCategory.addAll(categorylist); 
		cbaddcategory.setContainerDataSource(beanCategory); 
	} 

	// load the Currency details for add 
	public void loadCurrencyaddList() { 
		List<CurrencyDM> currencylist = servicebeanCurrency.getCurrencyList( 
				null, null, null,null,"F"); 
		beanCurrency = new BeanItemContainer<CurrencyDM>(CurrencyDM.class); 
		beanCurrency.addAll(currencylist); 
		cbaddcurrency.setContainerDataSource(beanCurrency); 
	} 

	// load the ParentProduct details for add 
	public void loadParentProdList() { 
		List<ProductDM> parentprodlist = servicebeanproduct.getProductList( 
				null, null, null, null, null); 
		BeanItemContainer<ProductDM> beanProduct1 = new BeanItemContainer<ProductDM>( 
				ProductDM.class); 
		beanProduct1.addAll(parentprodlist); 
		cbaddparentprod.setContainerDataSource(beanProduct1); 
	} 

	// load the TagDesc details for add 
	public void loadTagDescList() { 
		List<TagsDM> tagDesclist = servicebeanTags.getTagDescList(companyid); 
		beanTag = new BeanItemContainer<TagsDM>(TagsDM.class); 
		beanTag.addAll(tagDesclist); 
		totag.setContainerDataSource(beanTag); 
	} 

	//Edit Product 
	private void editproduct() { 

		vlMainLayout.setVisible(true); 
		Item itselect = tblproduct.getItem(tblproduct.getValue()); 
		if (itselect != null) { 

			String stCode = itselect.getItemProperty("prodstatus").getValue() 
					.toString(); 
			cbaddstatus.setValue(Common.getStatus(stCode)); 

			String stCode1 = itselect.getItemProperty("visualizeryn") 
					.getValue().toString(); 
			cbaddvisual.setValue(Common.getYesNo(stCode1)); 

			String stCode2 = itselect.getItemProperty("view360yn").getValue() 
					.toString(); 
			cbaddview.setValue(Common.getYesNo(stCode2)); 

			if (itselect.getItemProperty("prodname").getValue() != null 
					&& !"null".equals(itselect.getItemProperty("prodname") 
							.getValue())) { 
				tfaddprodname.setValue(itselect.getItemProperty("prodname") 
						.getValue().toString()); 
			} 

			if (itselect.getItemProperty("proddesc").getValue() != null 
					&& !"null".equals(itselect.getItemProperty("proddesc") 
							.getValue())) { 
				tfproddesc.setValue(itselect.getItemProperty("proddesc") 
						.getValue().toString()); 
			} 

			if (itselect.getItemProperty("brandname").getValue() != null 
					&& !"null".equals(itselect.getItemProperty("brandname") 
							.getValue())) { 
				tfbrandname.setValue(itselect.getItemProperty("brandname") 
						.getValue().toString()); 
			} 

			if (itselect.getItemProperty("price").getValue() != null 
					&& !"null".equals(itselect.getItemProperty("price") 
							.getValue())) { 
				tfprice.setValue(itselect.getItemProperty("price").getValue() 
						.toString()); 
			} 

			if (itselect.getItemProperty("uom").getValue() != null 
					&& !"null".equals(itselect.getItemProperty("uom") 
							.getValue())) { 
				tfuom.setValue(itselect.getItemProperty("uom").getValue() 
						.toString()); 
			} 

			ProductDM uom = (ProductDM) itselect.getItemProperty( 
					"parentprodid").getValue(); 
			System.out.println("Parent Product" + uom); 
			Collection<?> uomid = cbaddparentprod.getItemIds(); 
			for (Iterator<?> iterator = uomid.iterator(); iterator.hasNext();) { 
				Object itemId = (Object) iterator.next(); 

				BeanItem<?> item = (BeanItem<?>) cbaddparentprod 
						.getItem(itemId); 
				// Get the actual bean and use the data 
				ProductDM st = (ProductDM) item.getBean(); 
				if (uom != null && uom.getProdid().equals(st.getProdid())) { 
					cbaddparentprod.setValue(itemId); 
				} 
			} 

			ProductCategoryDM uom1 = (ProductCategoryDM) itselect 
					.getItemProperty("cateid").getValue(); 
			Collection<?> uomid1 = cbaddcategory.getItemIds(); 
			for (Iterator<?> iterator = uomid1.iterator(); iterator.hasNext();) { 
				Object itemId = (Object) iterator.next(); 

				BeanItem<?> item = (BeanItem<?>) cbaddcategory.getItem(itemId); 
				// Get the actual bean and use the data 
				ProductCategoryDM st = (ProductCategoryDM) item.getBean(); 
				if (uom1 != null && uom1.getCateid().equals(st.getCateid())) { 
					cbaddcategory.setReadOnly(false); 
					cbaddcategory.setValue(itemId); 
				} 
			} 

			CurrencyDM uom2 = (CurrencyDM) itselect.getItemProperty( 
					"ccyid").getValue(); 
			Collection<?> uomid2 = cbaddcurrency.getItemIds(); 
			for (Iterator<?> iterator = uomid2.iterator(); iterator.hasNext();) { 
				Object itemId = (Object) iterator.next(); 

				BeanItem<?> item = (BeanItem<?>) cbaddcurrency.getItem(itemId); 
				// Get the actual bean and use the data 
				CurrencyDM st = (CurrencyDM) item.getBean(); 
				if (uom2 != null && uom2.getCcyid().equals(st.getCcyid())) { 
					cbaddcurrency.setValue(itemId); 
				} 
			} 

			if (itselect.getItemProperty("prodimg").getValue() != null) { 
				byte[] myimage = (byte[]) itselect.getItemProperty("prodimg") 
						.getValue(); 

				UploadUI test = new UploadUI(hlimage); 
				test.dispayImage(myimage); 
			} else { 

				try { 
					new UploadUI(hlimage); 
				} catch (Exception e) { 
					e.printStackTrace(); 

				}// 
				filevalue2 = false; 
			} 

			if (itselect.getItemProperty("proddoc").getValue() != null) { 
				byte[] certificate = (byte[]) itselect.getItemProperty( 
						"proddoc").getValue(); 

				UploadUI1 test = new UploadUI1(hlimage1); 
				test.displaycertificate(certificate); 
			} else { 
 
				try { 
					new UploadUI1(hlimage1); 
				} catch (Exception e) { 
					e.printStackTrace(); 

				} 
				filevalue3 = false; 
			} 

			pkValue=itselect.getItemProperty("prodid").getValue() 
		            .toString(); 
			 
			productID = (Long) itselect.getItemProperty("prodid").getValue(); 

			populatedAndConfigSpec(productID); 
			populatedAndConfigColor(productID); 
			populatedAndConfigGallery(productID); 
			 
			// Specification Export 
			excelexporter1.setTableToBeExported(tblspec); 
			csvexporter1.setTableToBeExported(tblspec); 
			pdfexporter1.setTableToBeExported(tblspec); 
			excelexporter1.setCaption("Microsoft Excel (XLS)"); 
			excelexporter1.setStyleName("borderless"); 
			csvexporter1.setCaption("Comma Dilimited (CSV)"); 
			csvexporter1.setStyleName("borderless"); 
			pdfexporter1.setCaption("Acrobat Document (PDF)"); 
			pdfexporter1.setStyleName("borderless"); 
			// End Specification Export 
			 
		} 
	} 

	//Save and Update Product Details 
	private void saveorUpdateProduct() { 
		boolean valid = false; 
		if (tblproduct.getValue() != null) { 

			ProductDM update = beanProduct.getItem(tblproduct.getValue()) 
					.getBean(); 

			update.setParentprodid(selectProduct); 
			update.setCateid(selectCategory); 

			update.setCcyid(selectCurrency); 

			CompanyDM companyobj = new CompanyDM(); 
			companyobj.setCompanyid(companyid); 
			update.setCompanyid(companyobj); 

			if (tfaddprodname.getValue().trim().length() == 0) { 
				tfaddprodname.setComponentError(new UserError( 
						"Product should not be empty")); 
			} 
			update.setProdname(tfaddprodname.getValue()); 

			update.setProddesc(tfproddesc.getValue()); 

			update.setBrandname(tfbrandname.getValue()); 

			update.setPrice(new Long(tfprice.getValue())); 

			update.setUom(tfuom.getValue()); 

			update.setLastupdatedby(username); 
			update.setLastupdateddt(DateUtils.getcurrentdate()); 

			YesNoDM st = (YesNoDM) cbaddview.getValue(); 
			update.setView360yn(st.getCode()); 

			YesNoDM st1 = (YesNoDM) cbaddvisual.getValue(); 
			update.setVisualizeryn(st1.getCode()); 

			StatusDM st2 = (StatusDM) cbaddstatus.getValue(); 
			update.setProdstatus(st2.getCode()); 

			if (filevalue2) { 
				try { 

					File file = new File(basepath1); 
					FileInputStream fin = new FileInputStream(file); 
					byte fileContent[] = new byte[(int) file.length()]; 
					fin.read(fileContent); 
					fin.close(); 
					update.setProdimg(fileContent); 

				} catch (Exception e) { 
					e.printStackTrace(); 
				} 
			} 

			if (filevalue3) { 
				try { 

					File file = new File(basepath2); 
					FileInputStream fin = new FileInputStream(file); 
					byte fileContent[] = new byte[(int) file.length()]; 
					fin.read(fileContent); 
					fin.close(); 
					update.setProddoc(fileContent); 

				} catch (Exception e) { 
					e.printStackTrace(); 
				} 
			} 
			if ( tfaddprodname.isValid()) { 
				servicebeanproduct.saveorUpdateProductDetails(update); 

				valid = true; 

				lblNotificationIcon.setIcon(new ThemeResource( 
						"img/success_small.png")); 
				lblSaveNotification.setValue(ApplicationConstants.updatedMsg); 

			} 
		} else { 

			ProductDM save = new ProductDM(); 

			save.setParentprodid(selectProduct); 
			save.setCateid(selectCategory); 

			save.setCcyid(selectCurrency); 

			CompanyDM companyobj = new CompanyDM(); 
			companyobj.setCompanyid(companyid); 
			save.setCompanyid(companyobj); 

			if (tfaddprodname.getValue().trim().length() == 0) { 
				tfaddprodname.setComponentError(new UserError( 
						"Product should not be empty")); 
			} 
			save.setProdname(tfaddprodname.getValue()); 

			save.setProddesc(tfproddesc.getValue()); 

			save.setBrandname(tfbrandname.getValue()); 

			save.setPrice(new Long(tfprice.getValue())); 

			save.setUom(tfuom.getValue()); 

			save.setLastupdatedby(username); 
			save.setLastupdateddt(DateUtils.getcurrentdate()); 

			YesNoDM st = (YesNoDM) cbaddview.getValue(); 
			save.setView360yn(st.getCode()); 

			YesNoDM st1 = (YesNoDM) cbaddvisual.getValue(); 
			save.setVisualizeryn(st1.getCode()); 

			StatusDM st2 = (StatusDM) cbaddstatus.getValue(); 
			save.setProdstatus(st2.getCode()); 

			if (filevalue2) { 
				try { 

					File file = new File(basepath1); 
					FileInputStream fin = new FileInputStream(file); 
					byte fileContent[] = new byte[(int) file.length()]; 
					fin.read(fileContent); 
					fin.close(); 
					save.setProdimg(fileContent); 

				} catch (Exception e) { 
					e.printStackTrace(); 
				} 
			} 
			if (filevalue3) { 
				try { 

					File file = new File(basepath2); 
					FileInputStream fin = new FileInputStream(file); 
					byte fileContent[] = new byte[(int) file.length()]; 
					fin.read(fileContent); 
					fin.close(); 
					save.setProddoc(fileContent); 

				} catch (Exception e) { 
					e.printStackTrace(); 
				} 
			} 
			if (tfaddprodname.isValid()) { 
				servicebeanproduct.saveorUpdateProductDetails(save); 

				productID = save.getProdid(); 
				valid = true; 
				 
				lblNotificationIcon.setIcon(new ThemeResource( 
						"img/success_small.png")); 
				lblSaveNotification.setValue(ApplicationConstants.saveMsg); 

			} 
		} 
		if (valid) { 
			populatedAndConfig(false); 
			populatedAndConfigSpec(productID); 
			populatedAndConfigColor(productID); 
			populatedAndConfigGallery(productID); 
			btnSave.setComponentError(null); 
			 
			btnSave.setCaption("Save"); 
			 
			hlBreadCrumbs.setVisible(true); 

			 
		} else { 
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png")); 
			lblSaveNotification 
					.setValue("Save failed, please check the data and try again "); 
		} 

	} 

	//Edit Product Specification 
	private void editSpec() { 

		Item itselect = tblspec.getItem(tblspec.getValue()); 
		if (itselect != null) { 

			String stCode = itselect.getItemProperty("specstatus").getValue() 
					.toString(); 
			cbstatuspec.setValue(Common.getStatus(stCode)); 

			if (itselect.getItemProperty("speccode").getValue() != null 
					&& !"null".equals(itselect.getItemProperty("speccode") 
							.getValue())) { 
				tfspeccode.setValue(itselect.getItemProperty("speccode") 
						.getValue().toString()); 
			} 

			if (itselect.getItemProperty("specdesc").getValue() != null 
					&& !"null".equals(itselect.getItemProperty("specdesc") 
							.getValue())) { 
				tfspecdesc.setValue(itselect.getItemProperty("specdesc") 
						.getValue().toString()); 
				 
				 
			} 
			 
			pkValue1=itselect.getItemProperty("specid").getValue() 
			            .toString(); 
		} 
	} 
	 
	//Save and Update Product Specification 
	private void saveSpec() { 
		boolean valid = false; 
		if (tblspec.getValue() != null) 
		//populatedAndConfig(true); 
		{ 
			 

			ProductSpecificationDM update = beanSpecification.getItem( 
					tblspec.getValue()).getBean(); 

			ProductDM productobj = new ProductDM(); 
			productobj.setProdid(productID); 
			update.setProdid(productobj); 
 
			if (tfspeccode.getValue().trim().length() == 0) { 
				tfspeccode.setComponentError(new UserError( 
						"Spec Code should not be empty")); 
			} 
			update.setSpeccode(tfspeccode.getValue()); 

			update.setSpecdesc(tfspecdesc.getValue()); 

			update.setLastupdatedby(username); 
			update.setLastupdateddt(DateUtils.getcurrentdate()); 

			StatusDM st = (StatusDM) cbstatuspec.getValue(); 
			update.setSpecstatus(st.getCode()); 

			if (tfspeccode.isValid()) { 
				servicebeanProdSpec.saveorUpdateProdSpecDetails(update); 

				valid = true; 

				lblNotificationIcon.setIcon(new ThemeResource( 
						"img/success_small.png")); 
				lblSaveNotification.setValue(ApplicationConstants.updatedMsg); 

			} 

		}  
		{ 
			ProductSpecificationDM save = new ProductSpecificationDM(); 

			ProductDM productobj = new ProductDM(); 
			productobj.setProdid(productID); 
			save.setProdid(productobj); 
			if (tfspeccode.getValue().trim().length() == 0) { 
				tfspeccode.setComponentError(new UserError( 
						"Spec Code should not be empty")); 
			} 
			save.setSpeccode(tfspeccode.getValue()); 

			save.setSpecdesc(tfspecdesc.getValue()); 

			save.setLastupdatedby(username); 
			save.setLastupdateddt(DateUtils.getcurrentdate()); 

			StatusDM st = (StatusDM) cbstatuspec.getValue(); 
			save.setSpecstatus(st.getCode()); 

			if (tfspeccode.isValid()) { 
				servicebeanProdSpec.saveorUpdateProdSpecDetails(save); 

				valid = true; 
			 
			} 
		} 

		if (valid) { 

			populatedAndConfigSpec(productID); 
			btnaddSpec.setComponentError(null); 
			btnaddSpec.setCaption("Add"); 
			 
			// Specification Export 
			excelexporter1.setTableToBeExported(tblspec); 
			csvexporter1.setTableToBeExported(tblspec); 
			pdfexporter1.setTableToBeExported(tblspec); 
			excelexporter1.setCaption("Microsoft Excel (XLS)"); 
			excelexporter1.setStyleName("borderless"); 
			csvexporter1.setCaption("Comma Dilimited (CSV)"); 
			csvexporter1.setStyleName("borderless"); 
			pdfexporter1.setCaption("Acrobat Document (PDF)"); 
			pdfexporter1.setStyleName("borderless"); 
			// End Specification Export 

		 
			lblNotificationIcon.setIcon(new ThemeResource( 
					"img/success_small.png")); 
			lblSaveNotification.setValue(ApplicationConstants.saveMsg); 
		} else { 
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png")); 
			lblSaveNotification 
					.setValue("Save failed, please check the data and try again "); 
		} 
		 
	} 

	/* 
	 * private void deleteSpec(){ boolean valid = false; Item select = 
	 * tblspec.getItem(tblspec.getValue()); if (select != null) { 
	 * ProductSpecificationDM deleteSpeclist = 
	 * beanSpecification.getItem(tblspec.getValue()).getBean(); 
	 * 
	 * Long specId=(Long)select.getItemProperty("specid").getValue(); 
	 * 
	 * servicebeanProdSpec.deleteProductSpecificationDM(specId); valid = true; 
	 * 
	 * lblNotificationIcon.setIcon(new ThemeResource( "img/success_small.png")); 
	 * lblSaveNotification.setValue("Colour Deleted"); } 
	 * 
	 * } 
	 */ 

	//Save and Update Product Color 
	private void saveColor() { 
		boolean valid = false; 
		if (tblcolor.getValue() != null) { 
            String[] split=colorcode.split(","); 
     		for(String obj:split){ 
     	 
            ProductColorDM update = beanColor.getItem(tblcolor.getValue()) 
						.getBean(); 
			//update.setProdid(productID); 
            ProductDM productobj = new ProductDM(); 
			productobj.setProdid(productID); 
			update.setProdid(productobj); 
			 
			update.setLastupdatedby(username); 
			update.setLastupdateddt(DateUtils.getcurrentdate()); 
		 
			update.setColorcode(obj); 
			servicebeanProdColor.saveorUpdateProdColorDetails(update); 
			 
			} 
             } 
             else 
		 { 
			String[] split = colorcode.split(","); 
			for (String obj : split) { 
				 
				System.out.println("\n\nColor Times --------------\n\n"); 
				ProductColorDM save = new ProductColorDM(); 

				//save.setProdid(productID); 
				ProductDM productobj = new ProductDM(); 
				productobj.setProdid(productID); 
				save.setProdid(productobj); 

				save.setLastupdatedby(username); 
				save.setLastupdateddt(DateUtils.getcurrentdate()); 

				save.setColorcode(obj); 
				servicebeanProdColor.saveorUpdateProdColorDetails(save); 
				 
				valid = true; 
				 
				lblNotificationIcon.setIcon(new ThemeResource( 
						"img/success_small.png")); 
				lblSaveNotification.setValue(ApplicationConstants.saveMsg); 


			} 
		} 
		if (valid) { 
			populatedAndConfigColor(productID); 
			btnsaveColor.setComponentError(null); 
			btnsaveColor.setCaption("Save"); 

			 
		} else { 
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png")); 
			lblSaveNotification 
					.setValue("Save failed, please check the data and try again "); 
		} 

	} 

	//Delete Product Color 
	private void deleteColor() { 

		Item select = tblcolor.getItem(tblcolor.getValue()); 
		if (select != null) { 
			ProductColorDM deleteColorlist = beanColor.getItem( 
					tblcolor.getValue()).getBean(); 

			Long colorId = (Long) select.getItemProperty("colorid").getValue(); 

			servicebeanProdColor.deleteProdColor(colorId); 
		 
		} 

	} 
	 
	    /*//Delete Product Color List 
		private void deleteColorList() { 
			 
			servicebeanProdColor.deleteProdColorList(productID); 
		}*/ 
		 
		//Save Product Gallery Details 
	 
		private void saveGallery() { 
		boolean valid = false; 
			{ 
			ProductGalleryDM save = new ProductGalleryDM(); 

			ProductDM productobj = new ProductDM(); 
			productobj.setProdid(productID); 
			save.setProdid(productobj); 
			save.setGallerystatus(Common.ACTIVE_DESC); 
			save.setLastupdatedby(username); 
			save.setLastupdateddt(DateUtils.getcurrentdate()); 

			if (filevalue2) { 
				try { 

					File file = new File(basepath1); 
					FileInputStream fin = new FileInputStream(file); 
					byte fileContent[] = new byte[(int) file.length()]; 
					fin.read(fileContent); 
					fin.close(); 
					save.setProdimage(fileContent); 

				} catch (Exception e) { 
					e.printStackTrace(); 
				} 
			} 
		servicebeanProdGallery.saveGallerydetails(save); 
		 
		valid = true;	 
		} 

		if (valid) { 
			 
			populatedAndConfigGallery(productID); 
			btnSave.setComponentError(null); 
			btnSave.setCaption("Save"); 

			lblNotificationIcon.setIcon(new ThemeResource( 
					"img/success_small.png")); 
			lblSaveNotification.setValue(ApplicationConstants.saveMsg); 
		} else { 
			lblNotificationIcon.setIcon(new ThemeResource("img/failure.png")); 
			lblSaveNotification 
					.setValue("Save failed, please check the data and try again "); 
		} 
	} 

	//Delete Product Gallery 
	private void deleteGallery() { 

	Item select = tblgallery.getItem(tblgallery.getValue()); 
	if (select != null) { 
	ProductGalleryDM deleteGallerylist = beanGallery.getItem( 
		tblgallery.getValue()).getBean(); 

	Long gallId = (Long) select.getItemProperty("gallid").getValue(); 

		servicebeanProdGallery.deleteProdGallery(gallId); 
			 
	   } 

	} 
	//Reset Product Specification Components 
	private void resetSpec() { 
		tfspeccode.setValue(""); 
		tfspecdesc.setValue(""); 
		cbstatuspec.setValue(Common.getStatus(Common.ACTIVE_CODE)); 
		btnaddSpec.setComponentError(null); 
		btnEditspec.setComponentError(null); 
		pkValue1=null; 
	} 
 
	//Reset Product MainPanel Components 
	private void resetProduct() { 
		tfaddprodname.setValue(""); 
		cbaddparentprod.setValue(null); 
		cbaddcategory.setReadOnly(false); 
		cbaddcategory.setValue(null); 
		tfproddesc.setValue(""); 
		tfbrandname.setValue(""); 
		tfprice.setValue(""); 
		cbaddcurrency.setValue(null); 
		tfuom.setValue(""); 
		cbaddvisual.setValue(null); 
		cbaddview.setValue(null); 
		cbaddstatus.setValue(null); 
		btnSave.setComponentError(null); 
		btnCancel.setComponentError(null); 
        btnSave.setDescription("Save Product"); 
      lblNotificationIcon.setIcon(null); 
      lblSaveNotification.setValue(""); 
        pkValue=null; 

	} 

	//Reset Product SearchPanel Components 
	private void resetSearch() { 
		tfprodname.setValue(""); 
		cbcategoryS.setValue(null); 
		btnSearch.setComponentError(null); 
		btnReset.setComponentError(null); 
		cbstatusS.setValue(Common.getStatus(Common.ACTIVE_CODE)); 
	} 

	@Override 
	public void buttonClick(ClickEvent event) { 
		// TODO Auto-generated method stub 

		if (btnAdd == event.getButton()) { 
			resetProduct(); 
			new UploadUI(hlimage); 
			new UploadUI1(hlimage1); 
			btnSave.setCaption("Save"); 
			btnAdd.setEnabled(false); 
			vlMainLayout.setVisible(true); 
			vlSearchLayout.setVisible(false); 
			vlTableLayout.setVisible(false); 
			populatedAndConfig(false); 
			populatedAndConfigSpec(null); 
			populatedAndConfigColor(null); 
			populatedAndConfigGallery(null); 
			hlButtonLayout1.setVisible(true); 
			vlAudit.setVisible(false); 
			lblAddEdit.setValue("&nbsp;>&nbsp;Add New"); 
			lblFormTittle.setVisible(false); 
			hlBreadCrumbs.setVisible(true); 
			vlTableSpecLayout.setVisible(true); 
			 

		} else if (btnEdit == event.getButton()) { 
			new UploadUI(hlimage); 
			new UploadUI1(hlimage1); 
			new UploadUI(hlGallery); 
			btnSave.setCaption("Update"); 
			vlMainLayout.setVisible(true); 
			vlSearchLayout.setVisible(false); 
			editproduct(); 
			vlTableSpecLayout.setVisible(true); 
			vlTableLayout.setVisible(false); 
			btnEdit.setEnabled(false); 
			hlButtonLayout1.setVisible(true); 
			lblFormTittle 
					.setValue("&nbsp;&nbsp;<b>" 
							+ screenName 
							+ "</b><font color=\"#1E90FF\">&nbsp;::&nbsp;Search</font> &nbsp;>&nbsp;Modify"); 
			vlAudit.setVisible(false); 
			btnAdd.setEnabled(true); 
			lblAddEdit.setValue("&nbsp;>&nbsp;Modify"); 
			lblFormTittle.setVisible(false); 
			hlBreadCrumbs.setVisible(true); 

		} else if (btnCancel == event.getButton()) { 
			resetProduct(); 
			vlMainLayout.setVisible(false); 
			vlSearchLayout.setVisible(true); 
			vlTableLayout.setVisible(true); 
			vlTableSpecLayout.setVisible(false); 
			btnAdd.setEnabled(true); 
			btnEdit.setEnabled(false); 
			hlButtonLayout1.setVisible(false); 
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName 
					+ "</b>&nbsp;::&nbsp;Search"); 
			populatedAndConfigColor(null); 
			colorcode=""; 
			hlFileDownload.removeAllComponents(); 
			hlFileDownload.addComponent(btnDownload); 
			exportTableDate(); 
			vlAudit.setVisible(false); 
			hlBreadCrumbs.setVisible(false); 
			lblNotificationIcon.setIcon(null); 
			lblSaveNotification.setValue(""); 
			btnAuditrRecords.setEnabled(true); 
			lblAddEdit.setValue(""); 
			lblFormTittle.setVisible(true); 
 
		} else if (btnSave == event.getButton()) { 
			new UploadUI(hlGallery); 
			vlTableSpecLayout.setVisible(true); 
			saveorUpdateProduct(); 
			 
			 

		}else if (btnSearch == event.getButton()) { 
			populatedAndConfig(true); 
			/*if (total == 0) { 
				lblSaveNotification.setValue("No Records found"); 
			}*/ 
			vlAudit.setVisible(false); 
			btnAuditrRecords.setEnabled(true); 
			tblproduct.setVisible(true);  
			hlFileDownload.removeAllComponents(); 
			hlFileDownload.addComponent(btnDownload); 
			exportTableDate(); 
			btnEdit.setEnabled(false); 
			btnAdd.setEnabled(true); 

		} else if (btnReset == event.getButton()) { 
			resetSearch(); 
			populatedAndConfig(false); 
			lblNotificationIcon.setIcon(null); 
		      lblSaveNotification.setValue(""); 
			 
			 
			 

		}else if (btnaddSpec == event.getButton()) { 
			saveSpec(); 
			resetSpec(); 

		}else if (btnEditspec == event.getButton()) { 
			btnaddSpec.setCaption("Update"); 
			btnEditspec.setEnabled(false); 
			 
			// try{ 
			editSpec(); 

			/* 
			 * } catch(Exception e) { e.printStackTrace(); } 
			 */ 
		} else if (btnsaveColor == event.getButton()) { 
			//deleteColorList(); 
			saveColor(); 
			hlcolor2.removeAllComponents(); 
			populatedAndConfigColor(productID); 
			colorcode=""; 

		} else if (btndeleteColor == event.getButton()) { 
			deleteColor(); 
			btndeleteColor.setEnabled(false); 
			populatedAndConfigColor(productID); 

			lblNotificationIcon.setIcon(new ThemeResource( 
					"img/success_small.png")); 
			lblSaveNotification.setValue("Deleted Successfully"); 
			 
			 
	   } else if (btnsaveGallery == event.getButton()) { 
			 
			saveGallery(); 
			populatedAndConfigGallery(productID); 
			 
	   } else if (btndeleteGallery == event.getButton()) { 
			deleteGallery(); 
			btndeleteGallery.setEnabled(false); 
			btnsaveGallery.setEnabled(true); 
			populatedAndConfigGallery(productID); 

			lblNotificationIcon.setIcon(new ThemeResource( 
					"img/success_small.png")); 
			lblSaveNotification.setValue("Deleted Successfully"); 
			 
		} else if (btnAuditrRecords == event.getButton()) { 
			vlAudit.setVisible(true); 
			vlAudit.removeAllComponents(); 
			AuditRecordsApp recordApp=new AuditRecordsApp(vlAudit, Common.M_GCAT_PRODUCT,pkValue); 
			hlFileDownload.removeAllComponents(); 
			hlFileDownload.addComponent(recordApp.btnDownload); 
			exportTableDate(); 
			/*vlTableForm.removeAllComponents(); 
			vlTableForm.addComponent(hlAddEditLayout); 
			vlTableForm.addComponent(vlAudit);*/ 
			 
			tblproduct.setVisible(false); 
			vlAudit.setVisible(true); 
			btnAuditrRecords.setEnabled(false); 
			btnEdit.setEnabled(false); 
			btnAdd.setEnabled(false); 
			btnHome.setEnabled(true); 
			 
		} else if (btnHome == event.getButton()) { 
			/*vlTableForm.removeAllComponents(); 
			vlTableForm.addComponent(hlAddEditLayout); 
			vlTableForm.addComponent(tblproduct);*/ 

			hlAddEditLayout.setVisible(true); 
			tblproduct.setVisible(true); 
			vlAudit.setVisible(false); 
			populatedAndConfig(false); 
			btnAuditrRecords.setEnabled(true); 
			hlFileDownload.removeAllComponents(); 
			hlFileDownload.addComponent(btnDownload); 
			exportTableDate(); 
			btnAdd.setEnabled(true); 
			btnHome.setEnabled(false); 
			 
		} else if (btnAuditrRecordSpec == event.getButton()) { 
			vlAuditSpec.setVisible(true); 
			vlAuditSpec.removeAllComponents(); 
			new AuditRecordsApp(vlAuditSpec, 
					Common.M_GCAT_PRODUCT_SPEC,pkValue1); 
			vlTableSpecLayout.setVisible(true); 
			//hlAddEditLayoutSpec.setVisible(true); 
			vlspeclayout.setVisible(true); 
			vlspec.setVisible(false); 
			vlAuditSpec.setVisible(true); 
			hlspec.setVisible(true); 
			tblspec.setVisible(false); 
			btnAuditrRecords.setEnabled(true); 
			btnHomeSpec.setEnabled(true); 
			 

		} else if (btnHomeSpec == event.getButton()) { 
			vlTableSpecLayout.setVisible(true); 
			//hlAddEditLayoutSpec.setVisible(true); 
			vlspeclayout.setVisible(true); 
			vlspec.setVisible(true); 
			vlAuditSpec.setVisible(false); 
			hlspec.setVisible(true); 
			tblspec.setVisible(true); 
			populatedAndConfigSpec(productID); 
			btnHomeSpec.setEnabled(false); 
		} 
		 
		else if(btnBack==event.getButton()) 
		{ 

			resetProduct(); 
			vlMainLayout.setVisible(false); 
			vlSearchLayout.setVisible(true); 

			vlTableLayout.setVisible(true); 
			btnAdd.setEnabled(true); 
			btnEdit.setEnabled(false); 
			hlButtonLayout1.setVisible(false); 
			lblFormTittle.setValue("&nbsp;&nbsp;<b>" + screenName 
					+ "</b>&nbsp;::&nbsp;Search"); 

			lblNotificationIcon.setIcon(null); 
			lblSaveNotification.setValue(""); 
			lblAddEdit.setValue(""); 
			lblFormTittle.setVisible(true); 
			hlBreadCrumbs.setVisible(false); 
			tblproduct.setVisible(true); 
			vlAudit.setVisible(false); 
			btnAuditrRecords.setEnabled(true); 
			populatedAndConfig(false); 
			vlTableSpecLayout.setVisible(false); 
			hlFileDownload.removeAllComponents(); 
			hlFileDownload.addComponent(btnDownload); 
			exportTableDate(); 
			 
		} 
	} 
} 