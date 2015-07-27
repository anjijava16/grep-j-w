/**
 * File Name	:	ProductOverview.java
 * Description	:	This Screen Purpose for view the Product Details.
 * Author		:	SOUNDARC
 * Date			:	JUL 25, 2015
 * Modification :   
 * Modified By  :   
 * Description 	:
 *
 * Copyright (C) 2012 GNTS Technologies. * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of GNTS Technologies.
 * 
 * Version      Date           	Modified By      		Remarks
 * 0.1          JUL 25, 2015   	SOUNDARC				Initial Version		
 * 
 */
package com.gnts.mfg.txn;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import com.gnts.erputil.components.GERPButton;
import com.gnts.erputil.components.GERPTextField;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class ProductOverview implements ClickListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VerticalLayout hlPageRootContainter = (VerticalLayout) UI.getCurrent().getSession()
			.getAttribute("clLayout");
	// Header container which holds, screen name, notification and page master
	// buttons
	private HorizontalLayout hlPageHdrContainter = (HorizontalLayout) UI.getCurrent().getSession()
			.getAttribute("hlLayout");
	private String screenName = "";
	private Button btnScreenName;
	private GERPTextField tfSerialNumber = new GERPTextField("Serial Number");
	private Button btnSearch = new GERPButton("Search", "searchbt", this);
	// profile components
	private Image profilePic;
	private TextField tfProductCode;
	private TextField tfProductName;
	private TextField taProductDesc;
	private TextField taProductShortDesc;
	private TextField tfEnquiryRef;
	private TextField tfQuoteRef;
	private TextField tfPORef;
	private TextField tfInvoiceRef;
	private PopupDateField dfEnquiryDate;
	private PopupDateField dfQuoteDate;
	private PopupDateField dfOrderDate;
	private PopupDateField dfInvoiceDate;
	private TextField tfWorkorderRef;
	private PopupDateField dfWorkorderDate;
	private TextField cbPayperiod;
	private Table tblProductSpec = new Table();
	private Table tblEnquiryWorkflow = new Table();
	private Table tblDieRequest = new Table();
	private Table tblProductionDetails = new Table();
	private Table tblQATestDetails = new Table();
	private Table tblQCTestDetails = new Table();
	private Table tblMaterialDetails = new Table();
	
	public ProductOverview() {
		// TODO Auto-generated constructor stub
		if (UI.getCurrent().getSession().getAttribute("screenName") != null) {
			screenName = (String) UI.getCurrent().getSession().getAttribute("screenName");
		}
		btnScreenName = new GERPButton(screenName, "link", this);
		hlPageHdrContainter.removeAllComponents();
		hlPageHdrContainter.addComponent(btnScreenName);
		hlPageHdrContainter.setComponentAlignment(btnScreenName, Alignment.MIDDLE_LEFT);
		buildView();
	}
	
	private void buildView() {
		// TODO Auto-generated method stub
		tfSerialNumber.setWidth("200px");
		tfSerialNumber.focus();
		btnSearch.setClickShortcut(KeyCode.ENTER);
		tblProductSpec.setPageLength(5);
		tblEnquiryWorkflow.setPageLength(5);
		tblDieRequest.setPageLength(5);
		tblProductionDetails.setPageLength(5);
		tblQATestDetails.setPageLength(5);
		tblQCTestDetails.setPageLength(5);
		tblMaterialDetails.setPageLength(5);
		tblProductSpec.setFooterVisible(true);
		tblEnquiryWorkflow.setFooterVisible(true);
		tblDieRequest.setFooterVisible(true);
		tblProductionDetails.setFooterVisible(true);
		tblQATestDetails.setFooterVisible(true);
		tblQCTestDetails.setFooterVisible(true);
		tblMaterialDetails.setFooterVisible(true);
		tblProductSpec.setWidth("500px");
		tblMaterialDetails.setWidth("500px");
		tblEnquiryWorkflow.setWidth("600px");
		tblDieRequest.setWidth("600px");
		tblProductionDetails.setWidth("500px");
		tblQATestDetails.setWidth("100%");
		tblQCTestDetails.setWidth("100%");
		hlPageRootContainter.setSpacing(true);
		hlPageRootContainter.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;
			{
				setSpacing(true);
				addComponent(new FormLayout(tfSerialNumber));
				addComponent(btnSearch);
				setComponentAlignment(btnSearch, Alignment.MIDDLE_LEFT);
			}
		});
		// Create the Accordion.
		Accordion accordion = new Accordion();
		// Have it take all space available in the layout.
		accordion.setSizeFull();
		// Some components to put in the Accordion.
		HorizontalLayout l3 = new HorizontalLayout();
		l3.addComponent(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(tblMaterialDetails);
						addComponent(tblEnquiryWorkflow);
					}
				});
				addComponent(new HorizontalLayout() {
					private static final long serialVersionUID = 1L;
					{
						setSpacing(true);
						addComponent(tblProductionDetails);
						addComponent(tblDieRequest);
					}
				});
			}
		});
		HorizontalLayout l2 = new HorizontalLayout();
		l2.addComponent(tblProductSpec);
		// Add the components as tabs in the Accordion.
		accordion.addTab(buildBasicInformation(), "Basic Information", null);
		accordion.addTab(l3, "Marketing Information", null);
		accordion.addTab(l2, "Design/Die Information", null);
		accordion.addTab(tblQATestDetails, "Production Information", null);
		accordion.addTab(tblQCTestDetails, "Testing Information", null);
		hlPageRootContainter.addComponent(accordion);
	}
	
	private Component buildBasicInformation() {
		HorizontalLayout root = new HorizontalLayout();
		root.setWidth(100.0f, Unit.PERCENTAGE);
		root.setSpacing(true);
		root.setMargin(true);
		VerticalLayout pic = new VerticalLayout();
		pic.setSpacing(true);
		profilePic = new Image(null, new ThemeResource("img/box.png"));
		profilePic.setWidth(155.0f, Unit.PIXELS);
		profilePic.setHeight(160.0f, Unit.PIXELS);
		pic.addComponent(profilePic);
		root.addComponent(pic);
		FormLayout details1 = new FormLayout();
		details1.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		root.addComponent(details1);
		tfProductCode = new TextField("Product Code");
		tfProductCode.setWidth("150px");
		details1.addComponent(tfProductCode);
		tfProductName = new TextField("Product Name");
		tfProductName.setWidth("150px");
		details1.addComponent(tfProductName);
		taProductDesc = new TextField("Description");
		taProductDesc.setWidth("150px");
		details1.addComponent(taProductDesc);
		tfQuoteRef = new TextField("Quote Ref.");
		tfQuoteRef.setWidth("150px");
		details1.addComponent(tfQuoteRef);
		taProductShortDesc = new TextField("Short Desc.");
		taProductShortDesc.setWidth("150px");
		taProductShortDesc.setNullRepresentation("");
		details1.addComponent(taProductShortDesc);
		FormLayout details2 = new FormLayout();
		root.addComponent(details2);
		tfInvoiceRef = new TextField("Invoice Ref.");
		tfInvoiceRef.setWidth("150px");
		details2.addComponent(tfInvoiceRef);
		tfPORef = new TextField("Order Ref.");
		tfPORef.setWidth("150px");
		details2.addComponent(tfPORef);
		dfEnquiryDate = new PopupDateField("Marital Status");
		dfEnquiryDate.setWidth("150px");
		details2.addComponent(dfEnquiryDate);
		tfEnquiryRef = new TextField("Enquiry Ref.");
		tfEnquiryRef.setWidth("150px");
		tfEnquiryRef.setNullRepresentation("");
		details2.addComponent(tfEnquiryRef);
		dfQuoteDate = new PopupDateField("Quote Date");
		dfQuoteDate.setWidth("150px");
		details2.addComponent(dfQuoteDate);
		FormLayout details3 = new FormLayout();
		dfOrderDate = new PopupDateField("Order Date");
		dfOrderDate.setWidth("150px");
		details3.addComponent(dfOrderDate);
		dfInvoiceDate = new PopupDateField("Invoice Date");
		dfInvoiceDate.setWidth("150px");
		details3.addComponent(dfInvoiceDate);
		tfWorkorderRef = new TextField("Workorder Ref.");
		tfWorkorderRef.setWidth("150px");
		details3.addComponent(tfWorkorderRef);
		dfWorkorderDate = new PopupDateField("Workorder Date");
		dfWorkorderDate.setWidth("150px");
		details3.addComponent(dfWorkorderDate);
		cbPayperiod = new TextField("Pay period");
		cbPayperiod.setWidth("150px");
		details3.addComponent(cbPayperiod);
		root.addComponent(details3);
		return root;
	}
	
	public String viewImage(byte[] myimage, String name) {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/VAADIN/themes/gerp/img/"
				+ name + ".png";
		if (myimage != null && !"null".equals(myimage)) {
			InputStream in = new ByteArrayInputStream(myimage);
			BufferedImage bImageFromConvert = null;
			try {
				bImageFromConvert = ImageIO.read(in);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				ImageIO.write(bImageFromConvert, "png", new File(basepath));
			}
			catch (Exception e) {
			}
			try {
				ImageIO.write(bImageFromConvert, "jpg", new File(basepath));
			}
			catch (Exception e) {
			}
			try {
				return basepath;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/VAADIN/themes/gerp/img/box.png";
		}
		return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/VAADIN/themes/gerp/img/box.png";
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (btnSearch == event.getButton()) {
			viewProductDetails();
		}
	}

	private void viewProductDetails() {
		// TODO Auto-generated method stub
	}
}
