package com.gnts.pem.util.iterator;

import java.util.List;
import java.util.ArrayList;

import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.pem.domain.txn.common.TPemCmPropDimension;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.gnts.pem.util.list.DimensionList;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Runo;

public class ComponentIterDimensionofPlot extends HorizontalLayout implements ClickListener {

	/**
	 * 
	 */
	private CmBankConstantService beanBankCnst=(CmBankConstantService)SpringContextHelper.getBean("bankConstant");
	private static final long serialVersionUID = 1L;
	private HorizontalLayout mainLayout = new HorizontalLayout();
	private FormLayout formLayout1 = new FormLayout();
	private FormLayout formLayout2 = new FormLayout();
	private FormLayout formLayout3 = new FormLayout();
	private FormLayout formLayout4 = new FormLayout();

	private CheckBox chkSameasDeed = new CheckBox("Same as Deed?");

	private Label dummy = new Label();
	private CheckBox chkEditLables = new CheckBox("Edit Labels?");
	private TextField lblGrouphdr=new TextField("");
	private Button btnAdd = new Button("", this);
	private ComboBox lblDeed = new ComboBox();
	private ComboBox lblSite = new ComboBox();
	private ComboBox lblPlan = new ComboBox();

	private TextField lblNorthof = new TextField();
	private TextField tfNorthDeed = new TextField();
	private TextField tfNorthSite = new TextField();
	private TextField tfNorthPlan = new TextField();

	private TextField lblSouthof = new TextField();
	private TextField tfSouthDeed = new TextField();
	private TextField tfSouthSite = new TextField();
	private TextField tfSouthPlan = new TextField();

	private TextField lblEastof = new TextField();
	private TextField tfEastDeed = new TextField();
	private TextField tfEastSite = new TextField();
	private TextField tfEastPlan = new TextField();

	private TextField lblWestof = new TextField();
	private TextField tfWestDeed = new TextField();
	private TextField tfWestSite = new TextField();
	private TextField tfWestPlan = new TextField();

	private TextField lblDynamic1=new TextField();
	private TextField tfDynamicDeed1=new TextField();
	private TextField tfDynamicSite1=new TextField();
	private TextField tfDynamicPlan1=new TextField();
	
	private TextField lblDynamic2=new TextField();
	private TextField tfDynamicDeed2=new TextField();
	private TextField tfDynamicSite2=new TextField();
	private TextField tfDynamicPlan2=new TextField();
	
	private TextField lblDynamic3=new TextField();
	private TextField tfDynamicDeed3=new TextField();
	private TextField tfDynamicSite3=new TextField();
	private TextField tfDynamicPlan3=new TextField();
	
	private TextField lblDynamic4=new TextField();
	private TextField tfDynamicDeed4=new TextField();
	private TextField tfDynamicSite4=new TextField();
	private TextField tfDynamicPlan4=new TextField();
	
	private Label lblExtend = new Label("Extend");
	private TextField tfExtendDeed = new TextField();
	private TextField tfExtendSite = new TextField();
	private TextField tfExtendPlan = new TextField();
	
	private String lblHeight = "27px";
	private String strlblWidth = "200px";
	private String strleastvalue;
	private String strnorthsouth;
	Long selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
			.getAttribute("loginCompanyId").toString());

	void loadAsperDeed() {
		List<String> list = beanBankCnst.getBankConstantList("ASPER",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		lblDeed.setContainerDataSource(childAccounts);
		lblSite.setContainerDataSource(childAccounts);
		lblPlan.setContainerDataSource(childAccounts);
	}
	public List<TPemCmPropDimension> getDimensionPropertyList() {
		
		List<TPemCmPropDimension> dimenList=new ArrayList<TPemCmPropDimension>();
		
		TPemCmPropDimension obj1=new TPemCmPropDimension();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblNorthof.getValue());
		obj1.setAsPerDeed(tfNorthDeed.getValue());
		obj1.setAsPerPlan(tfNorthPlan.getValue());
		obj1.setAsPerSite(tfNorthSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		dimenList.add(obj1);
		
		obj1=new TPemCmPropDimension();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblSouthof.getValue());
		obj1.setAsPerDeed(tfSouthDeed.getValue());
		obj1.setAsPerPlan(tfSouthPlan.getValue());
		obj1.setAsPerSite(tfSouthSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		dimenList.add(obj1);
		
		obj1=new TPemCmPropDimension();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblEastof.getValue());
		obj1.setAsPerDeed(tfEastDeed.getValue());
		obj1.setAsPerPlan(tfEastPlan.getValue());
		obj1.setAsPerSite(tfEastSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		dimenList.add(obj1);
		
		obj1=new TPemCmPropDimension();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblWestof.getValue());
		obj1.setAsPerDeed(tfWestDeed.getValue());
		obj1.setAsPerPlan(tfWestPlan.getValue());
		obj1.setAsPerSite(tfWestSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		dimenList.add(obj1);
		
		//for dynamic
		obj1=new TPemCmPropDimension();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblDynamic1.getValue());
		obj1.setAsPerDeed(tfDynamicDeed1.getValue());
		obj1.setAsPerPlan(tfDynamicPlan1.getValue());
		obj1.setAsPerSite(tfDynamicSite1.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		dimenList.add(obj1);
		
		obj1=new TPemCmPropDimension();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblDynamic2.getValue());
		obj1.setAsPerDeed(tfDynamicDeed2.getValue());
		obj1.setAsPerPlan(tfDynamicPlan2.getValue());
		obj1.setAsPerSite(tfDynamicSite2.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		dimenList.add(obj1);
		
		obj1=new TPemCmPropDimension();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblDynamic3.getValue());
		obj1.setAsPerDeed(tfDynamicDeed3.getValue());
		obj1.setAsPerPlan(tfDynamicPlan3.getValue());
		obj1.setAsPerSite(tfDynamicSite3.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		dimenList.add(obj1);
		
		obj1=new TPemCmPropDimension();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblDynamic4.getValue());
		obj1.setAsPerDeed(tfDynamicDeed4.getValue());
		obj1.setAsPerPlan(tfDynamicPlan4.getValue());
		obj1.setAsPerSite(tfDynamicSite4.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		dimenList.add(obj1);
		
		obj1=new TPemCmPropDimension();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblExtend.getValue());
		obj1.setAsPerDeed(tfExtendDeed.getValue());
		obj1.setAsPerPlan(tfExtendPlan.getValue());
		obj1.setAsPerSite(tfExtendSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		dimenList.add(obj1);
		
		return dimenList;
	}

	@SuppressWarnings("deprecation")
	public ComponentIterDimensionofPlot(DimensionList obj,final boolean deed,final boolean site,final boolean plan) {

		setComponentStyle();
		loadAsperDeed();
		formLayout3.setVisible(false);
		formLayout4.setVisible(false);
		
		lblNorthof.setValue("North");
		lblSouthof.setValue("South");
		lblEastof.setValue("East");
		lblWestof.setValue("West");
		lblGrouphdr.setValue("Item No:");
		lblDeed.setInputPrompt(Common.SELECT_PROMPT);
		lblPlan.setInputPrompt(Common.SELECT_PROMPT);
		lblSite.setInputPrompt(Common.SELECT_PROMPT);
		
		if (obj != null) {
			loadDetails(obj);
		}
		
		chkEditLables.setHeight("20px");
		formLayout1.addComponent(chkEditLables);
		formLayout1.addComponent(lblNorthof);
		formLayout1.addComponent(lblSouthof);
		formLayout1.addComponent(lblEastof);
		formLayout1.addComponent(lblWestof);
		formLayout1.addComponent(lblDynamic1);
		formLayout1.addComponent(lblDynamic2);
		formLayout1.addComponent(lblDynamic3);
		formLayout1.addComponent(lblDynamic4);
		formLayout1.addComponent(lblExtend);
			
		if(deed){
		formLayout2.addComponent(lblDeed);
		formLayout2.addComponent(tfNorthDeed);
		formLayout2.addComponent(tfSouthDeed);
		formLayout2.addComponent(tfEastDeed);
		formLayout2.addComponent(tfWestDeed);
		formLayout2.addComponent(tfDynamicDeed1);
		formLayout2.addComponent(tfDynamicDeed2);
		formLayout2.addComponent(tfDynamicDeed3);
		formLayout2.addComponent(tfDynamicDeed4);
		formLayout2.addComponent(tfExtendDeed);
		}

	
		formLayout3.addComponent(lblSite);
		formLayout3.addComponent(tfNorthSite);
		formLayout3.addComponent(tfSouthSite);
		formLayout3.addComponent(tfEastSite);
		formLayout3.addComponent(tfWestSite);
		formLayout3.addComponent(tfDynamicSite1);
		formLayout3.addComponent(tfDynamicSite2);
		formLayout3.addComponent(tfDynamicSite3);
		formLayout3.addComponent(tfDynamicSite4);
		formLayout3.addComponent(tfExtendSite);

		
		formLayout4.addComponent(lblPlan);
		formLayout4.addComponent(tfNorthPlan);
		formLayout4.addComponent(tfSouthPlan);
		formLayout4.addComponent(tfEastPlan);
		formLayout4.addComponent(tfWestPlan);
		formLayout4.addComponent(tfDynamicPlan1);
		formLayout4.addComponent(tfDynamicPlan2);
		formLayout4.addComponent(tfDynamicPlan3);
		formLayout4.addComponent(tfDynamicPlan4);
		formLayout4.addComponent(tfExtendPlan);
	

		lblNorthof.setReadOnly(true);
		lblSouthof.setReadOnly(true);
		lblEastof.setReadOnly(true);
		lblWestof.setReadOnly(true);
		lblDynamic1.setReadOnly(true);
		lblDynamic2.setReadOnly(true);
		lblDynamic3.setReadOnly(true);
		lblDynamic4.setReadOnly(true);

		setSpacing(true);
		mainLayout.addComponent(formLayout1);
		mainLayout.addComponent(formLayout2);
		mainLayout.addComponent(formLayout3);
		mainLayout.addComponent(formLayout4);

		chkEditLables.setImmediate(true);
		chkEditLables.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				if (chkEditLables.getValue().equals(true)) {
					lblNorthof.setReadOnly(false);
					lblSouthof.setReadOnly(false);
					lblEastof.setReadOnly(false);
					lblWestof.setReadOnly(false);
					lblDynamic1.setReadOnly(false);
					lblDynamic2.setReadOnly(false);
					lblDynamic3.setReadOnly(false);
					lblDynamic4.setReadOnly(false);
				} else {
					lblNorthof.setReadOnly(true);
					lblSouthof.setReadOnly(true);
					lblEastof.setReadOnly(true);
					lblWestof.setReadOnly(true);
					lblDynamic1.setReadOnly(true);
					lblDynamic2.setReadOnly(true);
					lblDynamic3.setReadOnly(true);
					lblDynamic4.setReadOnly(true);
				}
			}
		});
		chkSameasDeed.setImmediate(true);
		chkSameasDeed.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				if (chkSameasDeed.getValue().equals(true)) {
					getDetailsSameAsDeed(deed,site,plan);
				} else {
					getDetailsDifferentAsDeed(deed,site,plan);
				}
			}
		});

		VerticalLayout testLayout = new VerticalLayout();
		HorizontalLayout test=new HorizontalLayout();
		test.addComponent(lblGrouphdr);
		test.addComponent(btnAdd);
		test.setSpacing(true);
		test.setComponentAlignment(btnAdd, Alignment.BOTTOM_RIGHT);
		testLayout.addComponent(test);
		testLayout.addComponent(chkSameasDeed);
		testLayout.setComponentAlignment(chkSameasDeed, Alignment.MIDDLE_LEFT);
		testLayout.addComponent(mainLayout);

		addComponent(testLayout);
		setSizeFull();
		setMargin(true);
	}

	void loadDetails(DimensionList obj) {
		
		lblGrouphdr.setValue(obj.getGrouphdrLabel());
		
		lblNorthof.setValue(obj.getDirectionNorthLabel());
		lblSouthof.setValue(obj.getDirectionSouthLabel());
		lblEastof.setValue(obj.getDirectionEastLabel());
		lblWestof.setValue(obj.getDirectionWestLabel());
		lblDynamic1.setValue(obj.getDirectionDynamic1());
		lblDynamic2.setValue(obj.getDirectionDynamic2());
		lblDynamic3.setValue(obj.getDirectionDynamic3());
		lblDynamic4.setValue(obj.getDirectionDynamic4());
		//lblExtend.setValue(obj.getExtentLabel());

		tfNorthDeed.setValue(obj.getNorthDeedValue());
		tfSouthDeed.setValue(obj.getSouthDeedValue());
		tfEastDeed.setValue(obj.getEastDeedValue());
		tfWestDeed.setValue(obj.getWestDeedValue());
		tfDynamicDeed1.setValue(obj.getDynamicdeedvalue1());
		tfDynamicDeed2.setValue(obj.getDynamicdeedvalue2());
		tfDynamicDeed3.setValue(obj.getDynamicdeedvalue3());
		tfDynamicDeed4.setValue(obj.getDynamicdeedvalue4());
		tfExtendDeed.setValue(obj.getExtentDeedValue());
		lblDeed.setValue(obj.getDeed());
		
		if(obj.getNorthSiteValue()!=null||obj.getSouthSiteValue()!=null){
			formLayout3.setVisible(true);
		}
		tfNorthSite.setValue(obj.getNorthSiteValue());
		tfSouthSite.setValue(obj.getSouthSiteValue());
		tfEastSite.setValue(obj.getEastSiteValue());
		tfWestSite.setValue(obj.getWestSiteValue());
		tfDynamicSite1.setValue(obj.getDynamicsitevalue1());
		tfDynamicSite2.setValue(obj.getDynamicsitevalue2());
		tfDynamicSite3.setValue(obj.getDynamicsitevalue3());
		tfDynamicSite4.setValue(obj.getDynamicsitevalue4());
		tfExtendSite.setValue(obj.getExtentSiteValue());
		lblSite.setValue(obj.getSite());

		if(obj.getNorthPlanValue()!=null||obj.getSouthPlanValue()!=null){
			formLayout4.setVisible(true);
		}
		tfNorthPlan.setValue(obj.getNorthPlanValue());
		tfSouthPlan.setValue(obj.getSouthPlanValue());
		tfEastPlan.setValue(obj.getEastPlanValue());
		tfWestPlan.setValue(obj.getWestPlanValue());
		tfDynamicPlan1.setValue(obj.getDynamicplanvalue1());
		tfDynamicPlan2.setValue(obj.getDynamicplanvalue2());
		tfDynamicPlan3.setValue(obj.getDynamicplanvalue3());
		tfDynamicPlan4.setValue(obj.getDynamicplanvalue4());
		tfExtendPlan.setValue(obj.getExtentPlanValue());
		lblPlan.setValue(obj.getPlan());
	}
	
	public List<String> getLeastValaue(){
		List<String> mylist=new ArrayList<String>();
		try{
		
		String deed ="";
		String site ="";
		String plan ="";
		System.out.println("Text Extent Value+++++"+tfExtendDeed.getValue());
		strleastvalue=tfExtendDeed.getValue();
		System.out.println("Extent Value====="+strleastvalue);
		strnorthsouth=tfNorthDeed.getValue()+"&"+tfSouthDeed.getValue();
		
		try{
			deed = tfExtendDeed.getValue().replaceAll("[^0-9]", "");
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			if(formLayout3.isVisible()){
		 site = tfExtendSite.getValue().replaceAll("[^0-9]", "");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			if(formLayout4.isVisible()){
		 plan = tfExtendPlan.getValue().replaceAll("[^0-9]", "");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Double deedvalue=99999999999999999999999.0,sitevalue=99999999999999999999999.0,planvalue=99999999999999999999999.0;
		if(deed.trim().length()>0&&deed!=null){
			deedvalue=Double.valueOf(deed);
		}
		if(site.trim().length()>0&&site!=null){
			sitevalue=Double.valueOf(site);
		}
		if(plan.trim().length()>0&&plan!=null){
			planvalue=Double.valueOf(plan);
		}
		
		
		if(formLayout3.isVisible()&&formLayout4.isVisible()){
		
		if(deedvalue<sitevalue&&deedvalue<planvalue)
		{
			strleastvalue=tfExtendDeed.getValue();
			strnorthsouth=tfNorthDeed.getValue()+"&"+tfSouthDeed.getValue();
		}
		else if(sitevalue<deedvalue&&sitevalue<planvalue)
		{
			strleastvalue=tfExtendSite.getValue();
			strnorthsouth=tfNorthSite.getValue()+"&"+tfSouthSite.getValue();
		}
		else
		{
			strleastvalue=tfExtendPlan.getValue();
			strnorthsouth=tfNorthPlan.getValue()+"&"+tfSouthPlan.getValue();
		}
		}
		else if(formLayout3.isVisible()){
			
			if(deedvalue<sitevalue){
				strleastvalue=tfExtendDeed.getValue();
				strnorthsouth=tfNorthDeed.getValue()+"&"+tfSouthDeed.getValue();
			}
			else{
				strleastvalue=tfExtendSite.getValue();
				strnorthsouth=tfNorthSite.getValue()+"&"+tfSouthSite.getValue();
			}
			
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		mylist.add(strleastvalue);
		mylist.add(strnorthsouth);
		return mylist;
	}
	
	private void setComponentStyle() {
		
		tfExtendDeed.setValue("0.0");
		tfExtendPlan.setValue("0.0");
		tfExtendSite.setValue("0.0");
		chkEditLables.setHeight(lblHeight);
		lblNorthof.setHeight(lblHeight);
		lblSouthof.setHeight(lblHeight);
		lblEastof.setHeight(lblHeight);
		lblWestof.setHeight(lblHeight);
		dummy.setHeight(lblHeight);

		lblGrouphdr.setWidth(strlblWidth);
		chkEditLables.setWidth(strlblWidth);
		lblNorthof.setWidth(strlblWidth);
		lblSouthof.setWidth(strlblWidth);
		lblEastof.setWidth(strlblWidth);
		lblWestof.setWidth(strlblWidth);
		lblExtend.setWidth(strlblWidth);

		lblDeed.setWidth(strlblWidth);
		tfNorthDeed.setWidth(strlblWidth);
		tfSouthDeed.setWidth(strlblWidth);
		tfEastDeed.setWidth(strlblWidth);
		tfWestDeed.setWidth(strlblWidth);
		tfExtendDeed.setWidth(strlblWidth);

		lblSite.setWidth(strlblWidth);
		tfNorthSite.setWidth(strlblWidth);
		tfSouthSite.setWidth(strlblWidth);
		tfEastSite.setWidth(strlblWidth);
		tfWestSite.setWidth(strlblWidth);
		tfExtendSite.setWidth(strlblWidth);

		lblPlan.setWidth(strlblWidth);
		tfNorthPlan.setWidth(strlblWidth);
		tfSouthPlan.setWidth(strlblWidth);
		tfEastPlan.setWidth(strlblWidth);
		tfWestPlan.setWidth(strlblWidth);
		tfExtendPlan.setWidth(strlblWidth);
		
		lblDynamic1.setWidth(strlblWidth);
		tfDynamicDeed1.setWidth(strlblWidth);
		tfDynamicSite1.setWidth(strlblWidth);
		tfDynamicPlan1.setWidth(strlblWidth);
		
		lblDynamic2.setWidth(strlblWidth);
		tfDynamicDeed2.setWidth(strlblWidth);
		tfDynamicSite2.setWidth(strlblWidth);
		tfDynamicPlan2.setWidth(strlblWidth);
		
		lblDynamic3.setWidth(strlblWidth);
		tfDynamicDeed3.setWidth(strlblWidth);
		tfDynamicSite3.setWidth(strlblWidth);
		tfDynamicPlan3.setWidth(strlblWidth);
		
		lblDynamic4.setWidth(strlblWidth);
		tfDynamicDeed4.setWidth(strlblWidth);
		tfDynamicSite4.setWidth(strlblWidth);
		tfDynamicPlan4.setWidth(strlblWidth);
		
		//for null
		lblEastof.setNullRepresentation("");
		lblWestof.setNullRepresentation("");
		lblNorthof.setNullRepresentation("");
		lblSouthof.setNullRepresentation("");
		lblDynamic1.setNullRepresentation("");
		tfDynamicDeed1.setNullRepresentation("");
		tfDynamicSite1.setNullRepresentation("");
		tfDynamicPlan1.setNullRepresentation("");
		
		lblDynamic2.setNullRepresentation("");
		tfDynamicDeed2.setNullRepresentation("");
		tfDynamicSite2.setNullRepresentation("");
		tfDynamicPlan2.setNullRepresentation("");
		
		lblDynamic3.setNullRepresentation("");
		tfDynamicDeed3.setNullRepresentation("");
		tfDynamicSite3.setNullRepresentation("");
		tfDynamicPlan3.setNullRepresentation("");
		
		lblDynamic4.setNullRepresentation("");
		tfDynamicDeed4.setNullRepresentation("");
		tfDynamicSite4.setNullRepresentation("");
		tfDynamicPlan4.setNullRepresentation("");
		
		tfNorthDeed.setNullRepresentation("");
		tfSouthDeed.setNullRepresentation("");
		tfEastDeed.setNullRepresentation("");
		tfWestDeed.setNullRepresentation("");
		tfExtendDeed.setNullRepresentation("");
		
		tfNorthSite.setNullRepresentation("");
		tfSouthSite.setNullRepresentation("");
		tfEastSite.setNullRepresentation("");
		tfWestSite.setNullRepresentation("");
		tfExtendSite.setNullRepresentation("");

		tfNorthPlan.setNullRepresentation("");
		tfSouthPlan.setNullRepresentation("");
		tfEastPlan.setNullRepresentation("");
		tfWestPlan.setNullRepresentation("");
		tfExtendPlan.setNullRepresentation("");
		
		lblDeed.setNullSelectionAllowed(false);
		lblSite.setNullSelectionAllowed(false);
		lblPlan.setNullSelectionAllowed(false);
		
		btnAdd.setIcon(new ThemeResource(Common.strAddIcon));
		btnAdd.setStyleName(Runo.BUTTON_LINK);
	}

	void getDetailsSameAsDeed(boolean deed,boolean site,boolean plan) {
		if(formLayout3.isVisible()){
		tfNorthSite.setValue(tfNorthDeed.getValue());
		tfSouthSite.setValue(tfSouthDeed.getValue());
		tfEastSite.setValue(tfEastDeed.getValue());
		tfWestSite.setValue(tfWestDeed.getValue());
		tfDynamicSite1.setValue(tfDynamicDeed1.getValue());
		tfDynamicSite2.setValue(tfDynamicDeed2.getValue());
		tfDynamicSite3.setValue(tfDynamicDeed3.getValue());
		tfDynamicSite4.setValue(tfDynamicDeed4.getValue());
		tfExtendSite.setValue(tfExtendDeed.getValue());
		}
		if(formLayout4.isVisible()){
		tfNorthPlan.setValue(tfNorthDeed.getValue());
		tfSouthPlan.setValue(tfSouthDeed.getValue());
		tfEastPlan.setValue(tfEastDeed.getValue());
		tfWestPlan.setValue(tfWestDeed.getValue());
		tfDynamicPlan1.setValue(tfDynamicDeed1.getValue());
		tfDynamicPlan2.setValue(tfDynamicDeed2.getValue());
		tfDynamicPlan3.setValue(tfDynamicDeed3.getValue());
		tfDynamicPlan4.setValue(tfDynamicDeed4.getValue());
		tfExtendPlan.setValue(tfExtendDeed.getValue());
		}
	}

	void getDetailsDifferentAsDeed(boolean deed,boolean site,boolean plan) {
		tfNorthSite.setValue("");
		tfSouthSite.setValue("");
		tfEastSite.setValue("");
		tfWestSite.setValue("");
		tfExtendSite.setValue("");
		tfDynamicSite1.setValue("");
		tfDynamicSite2.setValue("");
		tfDynamicSite3.setValue("");
		tfDynamicSite4.setValue("");
		

		tfNorthPlan.setValue("");
		tfSouthPlan.setValue("");
		tfEastPlan.setValue("");
		tfWestPlan.setValue("");
		tfExtendPlan.setValue("");
		tfDynamicPlan1.setValue("");
		tfDynamicPlan2.setValue("");
		tfDynamicPlan3.setValue("");
		tfDynamicPlan4.setValue("");

	}
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (btnAdd== event.getButton()) {
			if(formLayout3.isVisible()){
				formLayout4.setVisible(true);
				
			}else{
				formLayout4.setVisible(false);
			}
			if(formLayout2.isVisible()){
				formLayout3.setVisible(true);
				
			}else{
				formLayout3.setVisible(false);
			}
			
		}	
	}

}
