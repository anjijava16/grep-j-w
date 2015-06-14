/**
 * File Name 		: Product.java 
 * Description 		: this class is used for add/edit product category details. 
 * Author 			: Madhu
 * Date 			: March 03, 2014
 * Modification 	:
 * Modified By 		: Madhu 
 * Description 		:
 * 
 * Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 * 
 * This software is the confidential and proprietary information of GNTS
 * Technologies pvt. ltd.
 * Version       Date           	Modified By               Remarks
 * 0.1           Mar 04 2014         Madhu		          Intial Version
 * 0.2           3-Jun-2014         Ganga              Code Optimizing&code re-factoring
 */
package com.gnts.gcat.mst;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.ProductDM;
import com.gnts.erputil.components.GERPAddEditHLayout;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPPanelGenerator;
import com.gnts.erputil.constants.GERPColorChangeColGenerator;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.constants.GERPGalleryChngColGenerator;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.UploadUI;
import com.gnts.erputil.util.DateUtils;
import com.gnts.gcat.domain.mst.ProductColorDM;
import com.gnts.gcat.domain.mst.ProductGalleryDM;
import com.gnts.gcat.service.mst.ProductColorService;
import com.gnts.gcat.service.mst.ProductGalleryService;
import com.gnts.stt.mfg.domain.txn.ExtrudersMtrlDM;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ColorPickerArea;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.colorpicker.ColorChangeEvent;
import com.vaadin.ui.components.colorpicker.ColorChangeListener;
import com.vaadin.ui.themes.Runo;

public class ProductGallery implements ClickListener {
	private ProductColorService servicebeanProdColor = (ProductColorService) SpringContextHelper
			.getBean("ProductColor");
	private ProductGalleryService servicebeanProdGallery = (ProductGalleryService) SpringContextHelper
			.getBean("prodgallery");
	private static final long serialVersionUID = 1L;
	private HorizontalLayout hlSearchLayout;
	// form layout for input controls
	private Button btnsaveColor, btndeleteColor;
	private Button btnsaveGallery, btndeleteGallery;
	public Button btnSave = new GERPButton("Save", "savebt", this);
	public Button btnCancel = new GERPButton("Cancel", "cancelbt", this);
	private ColorPickerArea pickerarea;
	private HorizontalLayout hlcolor1 = new HorizontalLayout();
	private HorizontalLayout hlcolor2 = new HorizontalLayout();
	private HorizontalLayout hlimage = new HorizontalLayout();
	List<ProductGalleryDM> galleryList = new ArrayList<ProductGalleryDM>();
	List<ProductColorDM> colorList = new ArrayList<ProductColorDM>();
	private VerticalLayout vlSrchRsltContainer = new VerticalLayout();
	private Table tblgallery = new Table();
	private Table tblcolor = new Table();
	private String colorcode = "";
	private Label test;
	private Label lblcolor;
	// local variables declaration
	private Long companyid;
	private String username;
	private Long productID;
	// Bean container
	private BeanItemContainer<ProductColorDM> beanColor = null;
	private BeanItemContainer<ProductGalleryDM> beanGallery = null;
	// for initialize logger
	private Logger logger = Logger.getLogger(ProductGallery.class);
	private int records;
	
	// private AbstractComponent btnSave;
	// Constructor
	public ProductGallery(HorizontalLayout clrGlry, Long prodId) {
		// Get the logged in user name and company id from the session
		username = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		companyid = Long.valueOf(UI.getCurrent().getSession().getAttribute("loginCompanyId").toString());
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Inside Product() constructor");
		// Loading the UIu
		prodId = productID;
		buildview(clrGlry);
	}
	
	// Build the UI components
	private void buildview(HorizontalLayout clrGlry) {
		// Product Gallery Button
		btnsaveGallery = new Button("Save", this);
		btndeleteGallery = new Button("Delete", this);
		btndeleteGallery.setEnabled(false);
		btnsaveGallery.setStyleName("savebt");
		btndeleteGallery.setStyleName("cancelbt");
		tblgallery.setImmediate(true);
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
		// Product Color Buttons
		btnsaveColor = new Button("Save", this);
		btndeleteColor = new Button("Delete", this);
		btndeleteColor.setEnabled(false);
		btnsaveColor.setStyleName("savebt");
		btndeleteColor.setStyleName("cancelbt");
		tblcolor.setImmediate(true);
		tblcolor.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (tblcolor.isSelected(event.getItemId())) {
					btndeleteColor.setEnabled(false);
					btnsaveColor.setEnabled(true);
				} else {
					btndeleteColor.setEnabled(true);
					btnsaveColor.setEnabled(false);
					deleteColor();
				}
			}
		});
		btnsaveColor.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			public void buttonClick(ClickEvent event) {
				try {
					savecolor(null);
				}
				catch (Exception e) {
				}
				hlcolor2.removeAllComponents();
				loadSrchClrRslt(false, null);
				colorcode = "";
			}
		});
		btndeleteColor.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			public void buttonClick(ClickEvent event) {
				try {
					deleteColor();
				}
				catch (Exception e) {
				}
				btndeleteColor.setEnabled(false);
				loadSrchClrRslt(false, null);
			}
		});
		// Button Click Event for GAllery
		btnsaveGallery.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			public void buttonClick(ClickEvent event) {
				try {
					saveGallery(null);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				loadSrchGlryRslt(false, null);
			}
		});
		btndeleteGallery.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			
			public void buttonClick(ClickEvent event) {
				try {
					deleteGallery();
				}
				catch (Exception e) {
				}
				btndeleteGallery.setEnabled(false);
				btnsaveGallery.setEnabled(true);
				loadSrchGlryRslt(false, null);
			}
		});
		// Color Tab Comp
		tblcolor.setImmediate(true);
		tblcolor.setSelectable(true);
		tblcolor.setStyleName(Runo.TABLE_SMALL);
		tblcolor.setColumnCollapsingAllowed(true);
		// Gallery Tab components
		tblgallery.setImmediate(true);
		tblgallery.setSelectable(true);
		tblgallery.setStyleName(Runo.TABLE_SMALL);
		tblgallery.setColumnCollapsingAllowed(true);
		// Color Picker
		pickerarea = new ColorPickerArea();
		pickerarea.setWidth("200px");
		pickerarea.setHeight("25px");
		pickerarea.setColor(new Color(0, 100, 200));
		hlcolor1.addComponent(pickerarea);
		hlcolor1.setWidth("300");
		pickerarea.addColorChangeListener(new ColorChangeListener() {
			@Override
			public void colorChanged(ColorChangeEvent event) {
				// Do something with the color
				test = new Label();
				test.setValue("<font size=\"150\" color=" + event.getColor().getCSS() + "><B>&#9830</B></font>");
				test.setContentMode(ContentMode.HTML);
				btnsaveColor.setEnabled(true);
				hlcolor2.addComponent(test);
				hlcolor1.addComponent(hlcolor2);
				colorcode += event.getColor().getCSS() + ",";
			}
		});
		hlSearchLayout = new GERPAddEditHLayout();
		clrGlry.addComponent(GERPPanelGenerator.createPanel(hlSearchLayout));
		vlSrchRsltContainer.addComponent(tblcolor);
		assembleUserInputLayout(clrGlry);
		tblcolor.removeAllItems();
		UploadUI test = new UploadUI(hlimage);
		new UploadUI(hlimage);
		loadSrchClrRslt(false, null);
		// resetFields();
	}
	
	protected void assembleUserInputLayout(HorizontalLayout clrGlry) {
		logger.info("Company ID : " + companyid + " | User Name : " + username + " > " + "Assembling User Input layout");
		// Color Tab Components
		lblcolor = new Label();
		lblcolor.setValue("<B>&nbsp;&nbsp;Select Colour:</B>");
		lblcolor.setSizeUndefined();
		lblcolor.setContentMode(ContentMode.HTML);
		// tblcolor.setPageLength(3);
		VerticalLayout vlTableColor = new VerticalLayout();
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
		hlcolor3.setSpacing(false);
		hlcolor3.addStyleName("lightgray");
		VerticalLayout vlsavebutton = new VerticalLayout();
		vlsavebutton.addComponent(hlcolor3);
		vlsavebutton.addComponent(hlcolor1);
		vlsavebutton.addComponent(hlsavebutton);
		vlsavebutton.setSpacing(true);
		vlsavebutton.setSizeUndefined();
		vlsavebutton.setSizeFull();
		HorizontalLayout hlcolorlayout = new HorizontalLayout();
		hlcolorlayout.addComponent(vlTableColor);
		hlcolorlayout.addComponent(vlsavebutton);
		hlcolorlayout.setSpacing(true);
		hlcolorlayout.setSizeFull();
		VerticalLayout vlMainColorLayout = new VerticalLayout();
		vlMainColorLayout.addComponent(hlcolorlayout);
		vlMainColorLayout.setMargin(true);
		vlMainColorLayout.setWidth("550");
		vlMainColorLayout.setHeight("100%");
		VerticalLayout vlTableGallery = new VerticalLayout();
		vlTableGallery.setWidth("100%");
		vlTableGallery.setMargin(true);
		vlTableGallery.addComponent(tblgallery);
		HorizontalLayout hlgallbutton = new HorizontalLayout();
		hlgallbutton.addComponent(btnsaveGallery);
		hlgallbutton.addComponent(btndeleteGallery);
		hlgallbutton.setSpacing(true);
		VerticalLayout vlGalUpload = new VerticalLayout();
		vlGalUpload.addComponent(hlimage);
		vlGalUpload.addComponent(hlgallbutton);
		hlimage.setCaption("Image");
		vlGalUpload.setSpacing(true);
		HorizontalLayout hlGallLayout = new HorizontalLayout();
		hlGallLayout.addComponent(vlTableGallery);
		hlGallLayout.addComponent(vlGalUpload);
		hlGallLayout.setSpacing(true);
		hlGallLayout.setSizeFull();
		VerticalLayout vlMainGallLayout = new VerticalLayout();
		vlMainGallLayout.addComponent(hlGallLayout);
		vlMainGallLayout.setWidth("550");
		vlMainGallLayout.setHeight("430");
		// product color and label
		Label lblcolur = new Label("Product Color");
		lblcolur.setStyleName("h4");
		VerticalLayout vllblcolor = new VerticalLayout();
		vllblcolor.addComponent(lblcolur);
		vllblcolor.addComponent(GERPPanelGenerator.createPanel(vlMainColorLayout));
		clrGlry.addComponent(vllblcolor);
		clrGlry.setSpacing(true);
		Label lblGallery = new Label("Product Gallery");
		lblGallery.setStyleName("h4");
		VerticalLayout vllblGallery = new VerticalLayout();
		vllblGallery.addComponent(lblGallery);
		vllblGallery.addComponent(GERPPanelGenerator.createPanel(vlMainGallLayout));
		clrGlry.addComponent(vllblGallery);
		// vlSrchRsltContainer.addComponent(clrGlry);
		setgalleryTableProperties();
		setcolorTableProperties();
		loadSrchClrRslt(false, null);
		loadSrchGlryRslt(false, null);
	}
	
	// Product Colour Table
	public void loadSrchClrRslt(boolean fromdb, Long prodid) {
		if (fromdb) {
			colorList = servicebeanProdColor.getProdColorList(prodid, null, null);
		}
		records = colorList.size();
		beanColor = new BeanItemContainer<ProductColorDM>(ProductColorDM.class);
		beanColor.addAll(colorList);
		tblcolor.setSelectable(true);
		tblcolor.setContainerDataSource(beanColor);
		tblcolor.setVisibleColumns(new Object[] { "colorcode" });
		tblcolor.setColumnHeaders(new String[] { "Color" });
		// tblcolor.setPageLength(15);
	}
	
	// Product Gallery Table
	public void loadSrchGlryRslt(boolean fromdb, Long prodid) {
		if (fromdb) {
			galleryList = servicebeanProdGallery.getProdGalleryList(prodid, null, null);
		}
		records = galleryList.size();
		beanGallery = new BeanItemContainer<ProductGalleryDM>(ProductGalleryDM.class);
		beanGallery.addAll(galleryList);
		tblgallery.setSelectable(true);
		tblgallery.setContainerDataSource(beanGallery);
		tblgallery.setVisibleColumns(new Object[] { "prodimage" });
		tblgallery.setColumnHeaders(new String[] { "Product image" });
		// tblgallery.setPageLength(8);
	}
	
	public void saveDetails(Long prodid) {
		servicebeanProdColor.deleteProdColorList(prodid);
		@SuppressWarnings("unchecked")
		Collection<ProductColorDM> itemIds = (Collection<ProductColorDM>) tblcolor.getVisibleItemIds();
		for (ProductColorDM savecolor : (Collection<ProductColorDM>) itemIds) {
			savecolor.setProdid(prodid);
			System.out.println("save color===>");
			servicebeanProdColor.saveorUpdateProdColorDetails(savecolor);
		}
		
		
		servicebeanProdGallery.deleteProdGalleryList(prodid);
		@SuppressWarnings("unchecked")
		Collection<ProductGalleryDM> itemId = (Collection<ProductGalleryDM>) tblgallery.getVisibleItemIds();
		for (ProductGalleryDM savegallery : (Collection<ProductGalleryDM>) itemId) {
			savegallery.setProdid(prodid);
			System.out.println("save gallery===>");
			servicebeanProdGallery.saveGallerydetails(savegallery);
		}
	}
	
	public void savecolor(Long prodid) {
		String[] split = colorcode.split(",");
		for (String obj : split) {
			ProductColorDM save = new ProductColorDM();
			// save.setProdid(productID);
			ProductDM productobj = new ProductDM();
			productobj.setProdid(productID);
			save.setProdid(prodid);
			save.setColorstatus("Active");
			save.setLastupdatedby(username);
			save.setLastupdateddt(DateUtils.getcurrentdate());
			save.setColorcode(obj);
			colorList.add(save);
			loadSrchClrRslt(false, null);
			// servicebeanProdColor.saveorUpdateProdColorDetails(save);
		}
	}
	
	// Save Product Gallery Details
	public void saveGallery(Long prodid) throws IOException {
		try {
			ProductDM productobj = new ProductDM();
			productobj.setProdid(productID);
			ProductGalleryDM save = new ProductGalleryDM();
			save.setProdid(prodid);
			save.setGallerystatus("Active");
			// save.setGallerystatus((String) cbstatus.getValue());
			save.setLastupdatedby(username);
			save.setLastupdateddt(DateUtils.getcurrentdate());
			if ((Boolean) UI.getCurrent().getSession().getAttribute("isFileUploaded")) {
				try {
					save.setProdimage((byte[]) UI.getCurrent().getSession().getAttribute("imagebyte"));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				save.setProdimage(null);
			}
			/*File file = new File(GERPConstants.IMAGE_PATH);
			FileInputStream fin = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];
			fin.read(fileContent);
			fin.close();
			save.setProdimage(fileContent);*/
			// servicebeanProdGallery.saveGallerydetails(save);
			galleryList.add(save);
			loadSrchGlryRslt(false, null);
			btnSave.setComponentError(null);
			btnSave.setCaption("Save");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Show the expected value in Grid tblcolor.
	public void setcolorTableProperties() {
		beanColor = new BeanItemContainer<ProductColorDM>(ProductColorDM.class);
		tblcolor.addGeneratedColumn("colorcode", new GERPColorChangeColGenerator());
	}
	
	// Show the expected value in Grid tblcolor.
	public void setgalleryTableProperties() {
		beanGallery = new BeanItemContainer<ProductGalleryDM>(ProductGalleryDM.class);
		tblgallery.addGeneratedColumn("prodimage", new GERPGalleryChngColGenerator());
	}
	
	// Delete Product Color
	public void deleteColor() {
		Item select = tblcolor.getItem(tblcolor.getValue());
		if (select != null) {
			ProductColorDM deleteColorlist = beanColor.getItem(tblcolor.getValue()).getBean();
			colorList.remove(deleteColorlist);
			loadSrchClrRslt(false, null);
			// Long colorId = (Long) select.getItemProperty("colorid").getValue();
			// servicebeanProdColor.deleteProdColor(colorId);
		}
	}
	
	// Delete Product Gallery
	private void deleteGallery() {
		Item select = tblgallery.getItem(tblgallery.getValue());
		if (select != null) {
			ProductGalleryDM deleteGallerylist = beanGallery.getItem(tblgallery.getValue()).getBean();
			galleryList.remove(deleteGallerylist);
			loadSrchGlryRslt(false, null);
			// Long gallId = (Long) select.getItemProperty("gallid").getValue();
			// servicebeanProdGallery.deleteProdGallery(gallId);
		}
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
	}
}
