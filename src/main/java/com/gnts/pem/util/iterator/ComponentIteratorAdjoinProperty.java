package com.gnts.pem.util.iterator;

import java.util.ArrayList;
import java.util.List;

import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.pem.domain.txn.common.TPemCmPropAdjoinDtls;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.gnts.pem.util.list.AdjoinPropertyList;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.VerticalLayout;

public class ComponentIteratorAdjoinProperty extends HorizontalLayout implements ClickListener {
	private CmBankConstantService beanBankCnst=(CmBankConstantService)SpringContextHelper.getBean("bankConstant");

	private HorizontalLayout mainLayout = new HorizontalLayout();
	private VerticalLayout formLayout1 = new VerticalLayout();
	private VerticalLayout formLayout2 = new VerticalLayout();
	private VerticalLayout formLayout3 = new VerticalLayout();
	private VerticalLayout formLayout4 = new VerticalLayout();

	private CheckBox chkSameasDeed = new CheckBox("Same as Deed?");
	
	private TextField lblGrouphdr=new TextField("");
	private Button btnAdd = new Button("", this);
	private CheckBox chkOforBy = new CheckBox("By?");
	private ComboBox lblDeed = new ComboBox();
	private ComboBox lblSite = new ComboBox();
	private ComboBox lblPlan = new ComboBox();

	private Label lblNorthof = new Label("North of");
	private TextField tfNorthDeed = new TextField();
	private TextField tfNorthSite = new TextField();
	private TextField tfNorthPlan = new TextField();

	private Label lblSouthof = new Label("South of");
	private TextField tfSouthDeed = new TextField();
	private TextField tfSouthSite = new TextField();
	private TextField tfSouthPlan = new TextField();

	private Label lblEastof = new Label("East of");
	private TextField tfEastDeed = new TextField();
	private TextField tfEastSite = new TextField();
	private TextField tfEastPlan = new TextField();

	private Label lblWestof = new Label("West of");
	private TextField tfWestDeed = new TextField();
	private TextField tfWestSite = new TextField();
	private TextField tfWestPlan = new TextField();

	private String lblHeight = "20px";
	private String strlblWidth = "200px";
	void loadAsperDeed() {
		
		Long selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		
		List<String> list = beanBankCnst.getBankConstantList("ASPER",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		lblDeed.setContainerDataSource(childAccounts);
		lblSite.setContainerDataSource(childAccounts);
		lblPlan.setContainerDataSource(childAccounts);
	}

	public List<TPemCmPropAdjoinDtls> getAdjoinPropertyList() {
		
		List<TPemCmPropAdjoinDtls> mylist=new ArrayList<TPemCmPropAdjoinDtls>();
		TPemCmPropAdjoinDtls obj1=new TPemCmPropAdjoinDtls();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblNorthof.getValue());
		obj1.setAsPerDeed(tfNorthDeed.getValue());
		obj1.setAsPerPlan(tfNorthPlan.getValue());
		obj1.setAsAtSite(tfNorthSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		mylist.add(obj1);
		
		obj1=new TPemCmPropAdjoinDtls();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblSouthof.getValue());
		obj1.setAsPerDeed(tfSouthDeed.getValue());
		obj1.setAsPerPlan(tfSouthPlan.getValue());
		obj1.setAsAtSite(tfSouthSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		
		mylist.add(obj1);
		
		obj1=new TPemCmPropAdjoinDtls();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblEastof.getValue());
		obj1.setAsPerDeed(tfEastDeed.getValue());
		obj1.setAsPerPlan(tfEastPlan.getValue());
		obj1.setAsAtSite(tfEastSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		mylist.add(obj1);
		
		obj1=new TPemCmPropAdjoinDtls();
		obj1.setGroupHdr(lblGrouphdr.getValue());
		obj1.setFieldLabel(lblWestof.getValue());
		obj1.setAsPerDeed(tfWestDeed.getValue());
		obj1.setAsPerPlan(tfWestPlan.getValue());
		obj1.setAsAtSite(tfWestSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		mylist.add(obj1);
		
		return mylist;
	}

	@SuppressWarnings("deprecation")
	public ComponentIteratorAdjoinProperty(AdjoinPropertyList obj,boolean deed,boolean site,boolean plan) {

		setComponentStyle();
		loadAsperDeed();
		formLayout3.setVisible(false);
		formLayout4.setVisible(false);
		
		lblGrouphdr.setValue("Item No:");
		lblDeed.setInputPrompt(Common.SELECT_PROMPT);
		lblPlan.setInputPrompt(Common.SELECT_PROMPT);
		lblSite.setInputPrompt(Common.SELECT_PROMPT);
		chkOforBy.setHeight("15px");
	lblNorthof.setHeight(lblHeight);
		lblSouthof.setHeight(lblHeight);
		lblEastof.setHeight(lblHeight);
		lblWestof.setHeight(lblHeight);
		
		if (obj != null) {
			loadDetails(obj);
		}
		formLayout1.addComponent(chkOforBy);
		formLayout1.addComponent(lblNorthof);
		formLayout1.addComponent(lblSouthof);
		formLayout1.addComponent(lblEastof);
		formLayout1.addComponent(lblWestof);

		if(deed){
		formLayout2.addComponent(lblDeed);
		formLayout2.addComponent(tfNorthDeed);
		formLayout2.addComponent(tfSouthDeed);
		formLayout2.addComponent(tfEastDeed);
		formLayout2.addComponent(tfWestDeed);
		}

	
		formLayout3.addComponent(lblSite);
		formLayout3.addComponent(tfNorthSite);
		formLayout3.addComponent(tfSouthSite);
		formLayout3.addComponent(tfEastSite);
		formLayout3.addComponent(tfWestSite);

		formLayout4.addComponent(lblPlan);
		formLayout4.addComponent(tfNorthPlan);
		formLayout4.addComponent(tfSouthPlan);
		formLayout4.addComponent(tfEastPlan);
		formLayout4.addComponent(tfWestPlan);

		setSpacing(true);
		setMargin(true);
		mainLayout.setSpacing(true);
		formLayout1.setSpacing(true);
		formLayout2.setSpacing(true);
		formLayout3.setSpacing(true);
		formLayout4.setSpacing(true);
		
		mainLayout.addComponent(formLayout1);
		mainLayout.addComponent(formLayout2);
		mainLayout.addComponent(formLayout3);
		mainLayout.addComponent(formLayout4);

		chkOforBy.setImmediate(true);
		chkOforBy.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				if (chkOforBy.getValue().equals(true)) {
					lblNorthof.setValue("North by");
					lblSouthof.setValue("South by");
					lblEastof.setValue("East by");
					lblWestof.setValue("West by");
				} else {
					lblNorthof.setValue("North of");
					lblSouthof.setValue("South of");
					lblEastof.setValue("East of");
					lblWestof.setValue("West of");
				}
			}
		});
		chkSameasDeed.setImmediate(true);
		chkSameasDeed.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				if (chkSameasDeed.getValue().equals(true)) {
					getDetailsSameAsDeed();
				} else {
					getDetailsDifferentAsDeed();
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

	void loadDetails(AdjoinPropertyList obj) {
		lblGrouphdr.setValue(obj.getGroupLabel());
		lblNorthof.setValue(obj.getDirectionNorthLabel());
		lblSouthof.setValue(obj.getDirectionSouthLabel());
		lblEastof.setValue(obj.getDirectionEastLabel());
		lblWestof.setValue(obj.getDirectionWestLabel());

		tfNorthDeed.setValue(obj.getNorthDeedValue());
		tfSouthDeed.setValue(obj.getSouthDeedValue());
		tfEastDeed.setValue(obj.getEastDeedValue());
		tfWestDeed.setValue(obj.getWestDeedValue());
		lblDeed.setValue(obj.getDeed());

		if(obj.getNorthSiteValue()!=null||obj.getSouthSiteValue()!=null){
			formLayout3.setVisible(true);
		}
		tfNorthSite.setValue(obj.getNorthSiteValue());
		tfSouthSite.setValue(obj.getSouthSiteValue());
		tfEastSite.setValue(obj.getEastSiteValue());
		tfWestSite.setValue(obj.getWestSiteValue());
		lblSite.setValue(obj.getSite());

		if(obj.getNorthPlanValue()!=null||obj.getSouthPlanValue()!=null){
			formLayout4.setVisible(true);
		}
		tfNorthPlan.setValue(obj.getNorthPlanValue());
		tfSouthPlan.setValue(obj.getSouthPlanValue());
		tfEastPlan.setValue(obj.getEastPlanValue());
		tfWestPlan.setValue(obj.getWestPlanValue());
		lblPlan.setValue(obj.getPlan());
	}

	void setComponentStyle() {
		chkOforBy.setHeight("15px");
		lblNorthof.setHeight(lblHeight);
		lblSouthof.setHeight(lblHeight);
		lblEastof.setHeight(lblHeight);
		lblWestof.setHeight(lblHeight);

		lblGrouphdr.setWidth(strlblWidth);
		chkOforBy.setWidth(strlblWidth);
		lblNorthof.setWidth(strlblWidth);
		lblSouthof.setWidth(strlblWidth);
		lblEastof.setWidth(strlblWidth);
		lblWestof.setWidth(strlblWidth);

		lblDeed.setWidth(strlblWidth);
		tfNorthDeed.setWidth(strlblWidth);
		tfSouthDeed.setWidth(strlblWidth);
		tfEastDeed.setWidth(strlblWidth);
		tfWestDeed.setWidth(strlblWidth);

		lblSite.setWidth(strlblWidth);
		tfNorthSite.setWidth(strlblWidth);
		tfSouthSite.setWidth(strlblWidth);
		tfEastSite.setWidth(strlblWidth);
		tfWestSite.setWidth(strlblWidth);

		lblPlan.setWidth(strlblWidth);
		tfNorthPlan.setWidth(strlblWidth);
		tfSouthPlan.setWidth(strlblWidth);
		tfEastPlan.setWidth(strlblWidth);
		tfWestPlan.setWidth(strlblWidth);
		
		tfNorthDeed.setNullRepresentation("");
		tfSouthDeed.setNullRepresentation("");
		tfEastDeed.setNullRepresentation("");
		tfWestDeed.setNullRepresentation("");
		
		tfNorthSite.setNullRepresentation("");
		tfSouthSite.setNullRepresentation("");
		tfEastSite.setNullRepresentation("");
		tfWestSite.setNullRepresentation("");
		
		tfNorthPlan.setNullRepresentation("");
		tfSouthPlan.setNullRepresentation("");
		tfEastPlan.setNullRepresentation("");
		tfWestPlan.setNullRepresentation("");
	
		tfNorthDeed.setInputPrompt("North Deed");
		tfSouthDeed.setInputPrompt("South Deed");
		tfEastDeed.setInputPrompt("East Deed");
		tfWestDeed.setInputPrompt("West Deed");
		
		tfNorthSite.setInputPrompt("North Site");
		tfSouthSite.setInputPrompt("South site");
		tfEastSite.setInputPrompt("East Site");
		tfWestSite.setInputPrompt("West Site");
		
		tfNorthPlan.setInputPrompt("North Plan");
		tfSouthPlan.setInputPrompt("South Plan");
		tfEastPlan.setInputPrompt("East Plan");
		tfWestPlan.setInputPrompt("West Plan");
	
		
		lblDeed.setNullSelectionAllowed(false);
		lblPlan.setNullSelectionAllowed(false);
		lblSite.setNullSelectionAllowed(false);
		btnAdd.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnAdd.setStyleName(Runo.BUTTON_LINK);

	}

	void getDetailsSameAsDeed() {
		tfNorthSite.setValue(tfNorthDeed.getValue());
		tfSouthSite.setValue(tfSouthDeed.getValue());
		tfEastSite.setValue(tfEastDeed.getValue());
		tfWestSite.setValue(tfWestDeed.getValue());

		tfNorthPlan.setValue(tfNorthDeed.getValue());
		tfSouthPlan.setValue(tfSouthDeed.getValue());
		tfEastPlan.setValue(tfEastDeed.getValue());
		tfWestPlan.setValue(tfWestDeed.getValue());

	}

	void getDetailsDifferentAsDeed() {
		tfNorthSite.setValue("");
		tfSouthSite.setValue("");
		tfEastSite.setValue("");
		tfWestSite.setValue("");

		tfNorthPlan.setValue("");
		tfSouthPlan.setValue("");
		tfEastPlan.setValue("");
		tfWestPlan.setValue("");

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
