package com.gnts.pem.util.iterator;

import java.util.List;
import java.util.ArrayList;

import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.pem.domain.txn.common.TPemCmBldngNewSpec;
import com.gnts.pem.domain.txn.common.TPemCmBldngOldSpec;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.gnts.pem.util.list.BuildSpecList;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Runo;

public class ComponentIterBuildingSpecfication extends HorizontalLayout implements ClickListener {

	/**
	 * 
	 */
	private CmBankConstantService beanBankCnst=(CmBankConstantService)SpringContextHelper.getBean("bankConstant");	private static final long serialVersionUID = 1L;
	private HorizontalLayout mainLayout = new HorizontalLayout();
	private FormLayout formLayout1 = new FormLayout();
	private FormLayout formLayout2 = new FormLayout();
	private FormLayout formLayout3 = new FormLayout();
	private FormLayout formLayout4 = new FormLayout();

	private CheckBox chkSameasDeed = new CheckBox("Same as Deed?");

	private Label dummy = new Label();
	private CheckBox chkOforBy = new CheckBox("Edit Labels?");
	private ComboBox lblGrouphdr=new ComboBox("");
	private ComboBox lblGrouphdr1=new ComboBox("");
	private ComboBox lblGrouphdr2=new ComboBox("");
	private Button btnAdd = new Button("", this);
	private ComboBox lblDeed = new ComboBox();
	private ComboBox lblSite = new ComboBox();
	private ComboBox lblPlan = new ComboBox();

	private TextField lblTypeStructure = new TextField();
	private ComboBox tfStructureDeed = new ComboBox();
	private ComboBox tfStructureSite = new ComboBox();
	private ComboBox tfStructurePlan = new ComboBox();

	private TextField lblFoundation = new TextField();
	private ComboBox tfFoundationDeed = new ComboBox();
	private ComboBox tfFoundationSite = new ComboBox();
	private ComboBox tfFoundationPlan = new ComboBox();

	private TextField lblBasement = new TextField();
	private ComboBox tfBasementDeed = new ComboBox();
	private ComboBox tfBasementSite = new ComboBox();
	private ComboBox tfBasementPlan = new ComboBox();

	private TextField lblSuperStruct= new TextField();
	private ComboBox tfSuperStructDeed = new ComboBox();
	private ComboBox tfSuperStructSite = new ComboBox();
	private ComboBox tfSuperStructPlan = new ComboBox();

	private TextField lblRoofing = new TextField();
	private ComboBox tfRoofingDeed = new ComboBox();
	private ComboBox tfRoofingSite = new ComboBox();
	private ComboBox tfRoofingPlan = new ComboBox();

	private TextField lblFlooring = new TextField();
	private ComboBox tfFlooringDeed = new ComboBox();
	private ComboBox tfFlooringSite = new ComboBox();
	private ComboBox tfFlooringPlan = new ComboBox();
	
	private TextField lblJoineries= new TextField();
	private ComboBox tfJoineriesDeed = new ComboBox();
	private ComboBox tfJoineriesSite = new ComboBox();
	private ComboBox tfJoineriesPlan = new ComboBox();
	
	private TextField lblFinishes = new TextField();
	private ComboBox tfFinishesDeed = new ComboBox();
	private ComboBox tfFinishesSite = new ComboBox();
	private ComboBox tfFinishesPlan = new ComboBox();

	private String lblHeight = "27px";
	private String strlblWidth = "200px";
	
	Long selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
			.getAttribute("loginCompanyId").toString());
	
	void loadComponentListValues() {
		
		loadTypeStructure();
		 loadFoundation() ;
		 loadBasement() ;
		 loadSuperStructure();
		 loadRoofing();
		 loadFlooring();
		 loadJoineries();
		 loadFinishes();
		 loadAsperDeed();
		 loadFloorDetails();
	}
	void loadFloorDetails(){
		List<String> list = beanBankCnst.getBankConstantList("FLOOR",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		lblGrouphdr.setContainerDataSource(childAccounts);
		lblGrouphdr1.setContainerDataSource(childAccounts);
		lblGrouphdr2.setContainerDataSource(childAccounts);
	}
	void loadAsperDeed() {
		List<String> list = beanBankCnst.getBankConstantList("ASPER",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		lblDeed.setContainerDataSource(childAccounts);
		lblSite.setContainerDataSource(childAccounts);
		lblPlan.setContainerDataSource(childAccounts);
	}
	void loadTypeStructure() {
		List<String> list = beanBankCnst.getBankConstantList("STRUCTURE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfStructureDeed.setContainerDataSource(childAccounts);
		tfStructurePlan.setContainerDataSource(childAccounts);
		tfStructureSite.setContainerDataSource(childAccounts);
		
	}
	void loadFoundation() {
		List<String> list = beanBankCnst.getBankConstantList("FOUNDATION",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfFoundationDeed.setContainerDataSource(childAccounts);
		tfFoundationPlan.setContainerDataSource(childAccounts);
		tfFoundationSite.setContainerDataSource(childAccounts);
	}
	void loadBasement() {
		List<String> list = beanBankCnst.getBankConstantList("BASEMENT",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfBasementDeed.setContainerDataSource(childAccounts);
		tfBasementPlan.setContainerDataSource(childAccounts);
		tfBasementSite.setContainerDataSource(childAccounts);
		
	}
	void loadSuperStructure() {
		List<String> list = beanBankCnst.getBankConstantList("SUPERSTRUCTURE",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfSuperStructDeed.setContainerDataSource(childAccounts);
		tfSuperStructSite.setContainerDataSource(childAccounts);
		tfSuperStructPlan.setContainerDataSource(childAccounts);
		
	}
	void loadRoofing() {
		List<String> list = beanBankCnst.getBankConstantList("ROOFING",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfRoofingDeed.setContainerDataSource(childAccounts);
		tfRoofingPlan.setContainerDataSource(childAccounts);
		tfRoofingSite.setContainerDataSource(childAccounts);
	}
	void loadFlooring() {
		List<String> list = beanBankCnst.getBankConstantList("FLOORING",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfFlooringDeed.setContainerDataSource(childAccounts);
		tfFlooringPlan.setContainerDataSource(childAccounts);
		tfFlooringSite.setContainerDataSource(childAccounts);
	}
	void loadJoineries() {
		List<String> list = beanBankCnst.getBankConstantList("JOINERIES",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfJoineriesDeed.setContainerDataSource(childAccounts);
		tfJoineriesPlan.setContainerDataSource(childAccounts);
		tfJoineriesSite.setContainerDataSource(childAccounts);
	}
	void loadFinishes() {
		List<String> list = beanBankCnst.getBankConstantList("FINISHES",selectCompanyid);
		BeanItemContainer<String> childAccounts = new BeanItemContainer<String>(
				String.class);
		childAccounts.addAll(list);
		tfFinishesDeed.setContainerDataSource(childAccounts);
		tfFinishesPlan.setContainerDataSource(childAccounts);
		tfFinishesSite.setContainerDataSource(childAccounts);
	}
	
	public List<TPemCmBldngOldSpec> getBuildingSpecificationList() {
		
		List<TPemCmBldngOldSpec> specList=new ArrayList<TPemCmBldngOldSpec>();
		
		TPemCmBldngOldSpec obj1=new TPemCmBldngOldSpec();
		obj1.setGroupHdr((String)lblGrouphdr.getValue());
		obj1.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj1.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj1.setFieldLabel(lblTypeStructure.getValue());
		obj1.setAsPerDeed((String)tfStructureDeed.getValue());
		obj1.setAsPerPlan((String)tfStructurePlan.getValue());
		obj1.setAsPerSite((String)tfStructureSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		specList.add(obj1);
		
		TPemCmBldngOldSpec obj2=new TPemCmBldngOldSpec();
		obj2.setGroupHdr((String)lblGrouphdr.getValue());
		obj2.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj2.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj2.setFieldLabel(lblFoundation.getValue());
		obj2.setAsPerDeed((String)tfFoundationDeed.getValue());
		obj2.setAsPerPlan((String)tfFoundationPlan.getValue());
		obj2.setAsPerSite((String)tfFoundationSite.getValue());
		obj2.setDeedValue((String)lblDeed.getValue());
		obj2.setSiteValue((String)lblSite.getValue());
		obj2.setPlanValue((String)lblPlan.getValue());
		specList.add(obj2);
		
		TPemCmBldngOldSpec obj3=new TPemCmBldngOldSpec();
		obj3.setGroupHdr((String)lblGrouphdr.getValue());
		obj3.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj3.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj3.setFieldLabel(lblBasement.getValue());
		obj3.setAsPerDeed((String)tfBasementDeed.getValue());
		obj3.setAsPerPlan((String)tfBasementPlan.getValue());
		obj3.setAsPerSite((String)tfBasementSite.getValue());
		obj3.setDeedValue((String)lblDeed.getValue());
		obj3.setSiteValue((String)lblSite.getValue());
		obj3.setPlanValue((String)lblPlan.getValue());
		specList.add(obj3);
		
		TPemCmBldngOldSpec obj4=new TPemCmBldngOldSpec();
		obj4.setGroupHdr((String)lblGrouphdr.getValue());
		obj4.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj4.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj4.setFieldLabel(lblSuperStruct.getValue());
		obj4.setAsPerDeed((String)tfSuperStructDeed.getValue());
		obj4.setAsPerPlan((String)tfSuperStructPlan.getValue());
		obj4.setAsPerSite((String)tfSuperStructSite.getValue());
		obj4.setDeedValue((String)lblDeed.getValue());
		obj4.setSiteValue((String)lblSite.getValue());
		obj4.setPlanValue((String)lblPlan.getValue());
		specList.add(obj4);
		
		TPemCmBldngOldSpec obj5=new TPemCmBldngOldSpec();
		obj5.setGroupHdr((String)lblGrouphdr.getValue());
		obj5.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj5.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj5.setFieldLabel(lblRoofing.getValue());
		obj5.setAsPerDeed((String)tfRoofingDeed.getValue());
		obj5.setAsPerPlan((String)tfRoofingPlan.getValue());
		obj5.setAsPerSite((String)tfRoofingSite.getValue());
		obj5.setDeedValue((String)lblDeed.getValue());
		obj5.setSiteValue((String)lblSite.getValue());
		obj5.setPlanValue((String)lblPlan.getValue());
		specList.add(obj5);
		
		TPemCmBldngOldSpec obj6=new TPemCmBldngOldSpec();
		obj6.setGroupHdr((String)lblGrouphdr.getValue());
		obj6.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj6.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj6.setFieldLabel(lblFlooring.getValue());
		obj6.setAsPerDeed((String)tfFlooringDeed.getValue());
		obj6.setAsPerPlan((String)tfFlooringPlan.getValue());
		obj6.setAsPerSite((String)tfFlooringSite.getValue());
		obj6.setDeedValue((String)lblDeed.getValue());
		obj6.setSiteValue((String)lblSite.getValue());
		obj6.setPlanValue((String)lblPlan.getValue());
		specList.add(obj6);
		
		TPemCmBldngOldSpec obj7=new TPemCmBldngOldSpec();
		obj7.setGroupHdr((String)lblGrouphdr.getValue());
		obj7.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj7.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj7.setFieldLabel(lblJoineries.getValue());
		obj7.setAsPerDeed((String)tfJoineriesDeed.getValue());
		obj7.setAsPerPlan((String)tfJoineriesPlan.getValue());
		obj7.setAsPerSite((String)tfJoineriesSite.getValue());
		obj7.setDeedValue((String)lblDeed.getValue());
		obj7.setSiteValue((String)lblSite.getValue());
		obj7.setPlanValue((String)lblPlan.getValue());
		specList.add(obj7);
		
		TPemCmBldngOldSpec obj8=new TPemCmBldngOldSpec();
		obj8.setGroupHdr((String)lblGrouphdr.getValue());
		obj8.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj8.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj8.setFieldLabel(lblFinishes.getValue());
		obj8.setAsPerDeed((String)tfFinishesDeed.getValue());
		obj8.setAsPerPlan((String)tfFinishesPlan.getValue());
		obj8.setAsPerSite((String)tfFinishesSite.getValue());
		obj8.setDeedValue((String)lblDeed.getValue());
		obj8.setSiteValue((String)lblSite.getValue());
		obj8.setPlanValue((String)lblPlan.getValue());
		specList.add(obj8);
		
		return specList;
	}
public List<TPemCmBldngNewSpec> getTPemCmBldngNewSpecList() {
		
		List<TPemCmBldngNewSpec> specList2=new ArrayList<TPemCmBldngNewSpec>();
		
		TPemCmBldngNewSpec obj1=new TPemCmBldngNewSpec();
		obj1.setGroupHdr((String)lblGrouphdr.getValue());
		obj1.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj1.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj1.setFieldLabel(lblTypeStructure.getValue());
		obj1.setAsPerDeed((String)tfStructureDeed.getValue());
		obj1.setAsPerPlan((String)tfStructurePlan.getValue());
		obj1.setAsPerSite((String)tfStructureSite.getValue());
		obj1.setDeedValue((String)lblDeed.getValue());
		obj1.setSiteValue((String)lblSite.getValue());
		obj1.setPlanValue((String)lblPlan.getValue());
		specList2.add(obj1);
		
		TPemCmBldngNewSpec obj2=new TPemCmBldngNewSpec();
		obj2.setGroupHdr((String)lblGrouphdr.getValue());
		obj2.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj2.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj2.setFieldLabel(lblFoundation.getValue());
		obj2.setAsPerDeed((String)tfFoundationDeed.getValue());
		obj2.setAsPerPlan((String)tfFoundationPlan.getValue());
		obj2.setAsPerSite((String)tfFoundationSite.getValue());
		obj2.setDeedValue((String)lblDeed.getValue());
		obj2.setSiteValue((String)lblSite.getValue());
		obj2.setPlanValue((String)lblPlan.getValue());
		specList2.add(obj2);
		
		TPemCmBldngNewSpec obj3=new TPemCmBldngNewSpec();
		obj3.setGroupHdr((String)lblGrouphdr.getValue());
		obj3.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj3.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj3.setFieldLabel(lblBasement.getValue());
		obj3.setAsPerDeed((String)tfBasementDeed.getValue());
		obj3.setAsPerPlan((String)tfBasementPlan.getValue());
		obj3.setAsPerSite((String)tfBasementSite.getValue());
		obj3.setDeedValue((String)lblDeed.getValue());
		obj3.setSiteValue((String)lblSite.getValue());
		obj3.setPlanValue((String)lblPlan.getValue());
		specList2.add(obj3);
		
		TPemCmBldngNewSpec obj4=new TPemCmBldngNewSpec();
		obj4.setGroupHdr((String)lblGrouphdr.getValue());
		obj4.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj4.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj4.setFieldLabel(lblSuperStruct.getValue());
		obj4.setAsPerDeed((String)tfSuperStructDeed.getValue());
		obj4.setAsPerPlan((String)tfSuperStructPlan.getValue());
		obj4.setAsPerSite((String)tfSuperStructSite.getValue());
		obj4.setDeedValue((String)lblDeed.getValue());
		obj4.setSiteValue((String)lblSite.getValue());
		obj4.setPlanValue((String)lblPlan.getValue());
		specList2.add(obj4);
		
		TPemCmBldngNewSpec obj5=new TPemCmBldngNewSpec();
		obj5.setGroupHdr((String)lblGrouphdr.getValue());
		obj5.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj5.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj5.setFieldLabel(lblRoofing.getValue());
		obj5.setAsPerDeed((String)tfRoofingDeed.getValue());
		obj5.setAsPerPlan((String)tfRoofingPlan.getValue());
		obj5.setAsPerSite((String)tfRoofingSite.getValue());
		obj5.setDeedValue((String)lblDeed.getValue());
		obj5.setSiteValue((String)lblSite.getValue());
		obj5.setPlanValue((String)lblPlan.getValue());
		specList2.add(obj5);
		
		TPemCmBldngNewSpec obj6=new TPemCmBldngNewSpec();
		obj6.setGroupHdr((String)lblGrouphdr.getValue());
		obj6.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj6.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj6.setFieldLabel(lblFlooring.getValue());
		obj6.setAsPerDeed((String)tfFlooringDeed.getValue());
		obj6.setAsPerPlan((String)tfFlooringPlan.getValue());
		obj6.setAsPerSite((String)tfFlooringSite.getValue());
		obj6.setDeedValue((String)lblDeed.getValue());
		obj6.setSiteValue((String)lblSite.getValue());
		obj6.setPlanValue((String)lblPlan.getValue());
		specList2.add(obj6);
		
		TPemCmBldngNewSpec obj7=new TPemCmBldngNewSpec();
		obj7.setGroupHdr((String)lblGrouphdr.getValue());
		obj7.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj7.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj7.setFieldLabel(lblJoineries.getValue());
		obj7.setAsPerDeed((String)tfJoineriesDeed.getValue());
		obj7.setAsPerPlan((String)tfJoineriesPlan.getValue());
		obj7.setAsPerSite((String)tfJoineriesSite.getValue());
		obj7.setDeedValue((String)lblDeed.getValue());
		obj7.setSiteValue((String)lblSite.getValue());
		obj7.setPlanValue((String)lblPlan.getValue());
		specList2.add(obj7);
		
		TPemCmBldngNewSpec obj8=new TPemCmBldngNewSpec();
		obj8.setGroupHdr((String)lblGrouphdr.getValue());
		obj8.setGroupHdrSite((String)lblGrouphdr1.getValue());
		obj8.setGroupHdrPlan((String)lblGrouphdr2.getValue());
		obj8.setFieldLabel(lblFinishes.getValue());
		obj8.setAsPerDeed((String)tfFinishesDeed.getValue());
		obj8.setAsPerPlan((String)tfFinishesPlan.getValue());
		obj8.setAsPerSite((String)tfFinishesSite.getValue());
		obj8.setDeedValue((String)lblDeed.getValue());
		obj8.setSiteValue((String)lblSite.getValue());
		obj8.setPlanValue((String)lblPlan.getValue());
		specList2.add(obj8);
		
		return specList2;
	}

	
	@SuppressWarnings("deprecation")
	public ComponentIterBuildingSpecfication(BuildSpecList obj,boolean deed,boolean site,boolean plan) {

		setComponentStyle();
		loadComponentListValues();
		formLayout3.setVisible(false);
		formLayout4.setVisible(false);
		lblTypeStructure.setValue("Type of Structure");
		lblFoundation.setValue("Foundation");
		lblBasement.setValue("Basement");
		lblSuperStruct.setValue("Super Structure");
		lblRoofing.setValue("Roofing");
		lblFlooring.setValue("Flooring");
		lblJoineries.setValue("Joineries");
		lblFinishes.setValue("Finishes");
		lblGrouphdr.setInputPrompt(Common.SELECT_PROMPT);
		lblGrouphdr1.setInputPrompt(Common.SELECT_PROMPT);
		lblGrouphdr2.setInputPrompt(Common.SELECT_PROMPT);
		if (obj != null) {
			loadDetails(obj);
		}
		chkOforBy.setHeight("20px");
		formLayout1.addComponent(btnAdd);
		formLayout1.setComponentAlignment(btnAdd, Alignment.TOP_LEFT);
		formLayout1.addComponent(chkOforBy);
		formLayout1.addComponent(lblTypeStructure);
		formLayout1.addComponent(lblFoundation);
		formLayout1.addComponent(lblBasement);
		formLayout1.addComponent(lblSuperStruct);
		formLayout1.addComponent(lblRoofing);
		formLayout1.addComponent(lblFlooring);
		formLayout1.addComponent(lblJoineries);
		formLayout1.addComponent(lblFinishes);
		if(deed){
		formLayout2.addComponent(lblGrouphdr);
		formLayout2.addComponent(lblDeed);
		formLayout2.addComponent(tfStructureDeed);
		formLayout2.addComponent(tfFoundationDeed);
		formLayout2.addComponent(tfBasementDeed);
		formLayout2.addComponent(tfSuperStructDeed);
		formLayout2.addComponent(tfRoofingDeed);
		formLayout2.addComponent(tfFlooringDeed);
		formLayout2.addComponent(tfJoineriesDeed);
		formLayout2.addComponent(tfFinishesDeed);
		}
		formLayout3.addComponent(lblGrouphdr1);
		formLayout3.addComponent(lblSite);
		formLayout3.addComponent(tfStructureSite);
		formLayout3.addComponent(tfFoundationSite);
		formLayout3.addComponent(tfBasementSite);
		formLayout3.addComponent(tfSuperStructSite);
		formLayout3.addComponent(tfRoofingSite);
		formLayout3.addComponent(tfFlooringSite);
		formLayout3.addComponent(tfJoineriesSite);
		formLayout3.addComponent(tfFinishesSite);
		
		formLayout4.addComponent(lblGrouphdr2);
		formLayout4.addComponent(lblPlan);
		formLayout4.addComponent(tfStructurePlan);
		formLayout4.addComponent(tfFoundationPlan);
		formLayout4.addComponent(tfBasementPlan);
		formLayout4.addComponent(tfSuperStructPlan);
		formLayout4.addComponent(tfRoofingPlan);
		formLayout4.addComponent(tfFlooringPlan);
		formLayout4.addComponent(tfJoineriesPlan);
		formLayout4.addComponent(tfFinishesPlan);
		
		
		lblTypeStructure.setReadOnly(true);
		lblFoundation.setReadOnly(true);
		lblBasement.setReadOnly(true);
		lblSuperStruct.setReadOnly(true);
		lblRoofing.setReadOnly(true);
		lblFlooring.setReadOnly(true);
		lblJoineries.setReadOnly(true);
		lblFinishes.setReadOnly(true);
		
		setSpacing(true);
		mainLayout.addComponent(formLayout1);
		mainLayout.addComponent(formLayout2);
		mainLayout.addComponent(formLayout3);
		mainLayout.addComponent(formLayout4);

		chkOforBy.setImmediate(true);
		chkOforBy.addListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {

				if (chkOforBy.getValue().equals(true)) {
					lblTypeStructure.setReadOnly(false);
					lblFoundation.setReadOnly(false);
					lblBasement.setReadOnly(false);
					lblSuperStruct.setReadOnly(false);
					lblRoofing.setReadOnly(false);
					lblFlooring.setReadOnly(false);
					lblJoineries.setReadOnly(false);
					lblFinishes.setReadOnly(false);
				} else {
					lblTypeStructure.setReadOnly(true);
					lblFoundation.setReadOnly(true);
					lblBasement.setReadOnly(true);
					lblSuperStruct.setReadOnly(true);
					lblRoofing.setReadOnly(true);
					lblFlooring.setReadOnly(true);
					lblJoineries.setReadOnly(true);
					lblFinishes.setReadOnly(true);
				}
			}
		});
		chkSameasDeed.setImmediate(true);
		chkSameasDeed.addListener(new Property.ValueChangeListener() {
		private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {

				if (chkSameasDeed.getValue().equals(true)) {
					getDetailsSameAsDeed();
				} else {
					getDetailsDifferentAsDeed();
				}
			}
		});

		VerticalLayout testLayout = new VerticalLayout();
		testLayout.addComponent(chkSameasDeed);
		testLayout.setComponentAlignment(chkSameasDeed, Alignment.MIDDLE_LEFT);
		testLayout.addComponent(mainLayout);

		addComponent(testLayout);
		setSizeFull();
		setMargin(true);
		lblDeed.setInputPrompt(Common.SELECT_PROMPT);
		lblPlan.setInputPrompt(Common.SELECT_PROMPT);
		lblSite.setInputPrompt(Common.SELECT_PROMPT);
	}

	void loadDetails(BuildSpecList obj) {
		
		
	
		lblTypeStructure.setValue(obj.getTypeStructureLabel());
		lblFoundation.setValue(obj.getFoundationLabel());
		lblBasement.setValue(obj.getBasementLabel());
		lblSuperStruct.setValue(obj.getSuperStructLabel());
		lblRoofing.setValue(obj.getRoofingLabel());
		lblFlooring.setValue(obj.getFlooringLabel());
		lblJoineries.setValue(obj.getJoineriesLabel());
		lblFinishes.setValue(obj.getFinishesLabel());
		
		lblGrouphdr.setValue(obj.getGroupHdrLabel());
		tfStructureDeed.setValue(obj.getTypeStructDeedValue());
		tfFoundationDeed.setValue(obj.getFoundationDeedValue());
		tfBasementDeed.setValue(obj.getBasementDeedValue());
		tfSuperStructDeed.setValue(obj.getSuperStructDeedValue());
		tfRoofingDeed.setValue(obj.getRoofingDeedValue());
		tfFlooringDeed.setValue(obj.getFlooringDeedValue());
		tfJoineriesDeed.setValue(obj.getJoineriesDeedValue());
		tfFinishesDeed.setValue(obj.getFinishesDeedValue());
		lblDeed.setValue(obj.getDeed());
		
		
		if(obj.getTypeStructSiteValue()!=null||obj.getFoundationSiteValue()!=null){
			formLayout3.setVisible(true);
		}
		lblGrouphdr1.setValue(obj.getGrouphdrSite());
		tfStructureSite.setValue(obj.getTypeStructSiteValue());
		tfFoundationSite.setValue(obj.getFoundationSiteValue());
		tfBasementSite.setValue(obj.getBasementSiteValue());
		tfSuperStructSite.setValue(obj.getSuperStructSiteValue());
		tfRoofingSite.setValue(obj.getRoofingSiteValue());
		tfFlooringSite.setValue(obj.getFlooringSiteValue());
		tfJoineriesSite.setValue(obj.getJoineriesSiteValue());
		tfFinishesSite.setValue(obj.getFinishesSiteValue());
		lblSite.setValue(obj.getSite());
		
		if(obj.getTypeStructPlanValue()!=null||obj.getFoundationPlanValue()!=null){
			formLayout4.setVisible(true);
		}
		lblGrouphdr2.setValue(obj.getGrouphdrPlan());
		tfStructurePlan.setValue(obj.getTypeStructPlanValue());
		tfFoundationPlan.setValue(obj.getFoundationPlanValue());
		tfBasementPlan.setValue(obj.getBasementPlanValue());
		tfSuperStructPlan.setValue(obj.getSuperStructPlanValue());
		tfRoofingPlan.setValue(obj.getRoofingPlanValue());
		tfFlooringPlan.setValue(obj.getFlooringPlanValue());
		tfJoineriesPlan.setValue(obj.getJoineriesPlanValue());
		tfFinishesPlan.setValue(obj.getFinishesPlanValue());
		lblPlan.setValue(obj.getPlan());
	}
	
	
	void setComponentStyle() {
		chkOforBy.setHeight(lblHeight);
		lblTypeStructure.setHeight(lblHeight);
		lblFoundation.setHeight(lblHeight);
		lblBasement.setHeight(lblHeight);
		lblSuperStruct.setHeight(lblHeight);
		lblRoofing.setHeight(lblHeight);
		lblFlooring.setHeight(lblHeight);
		lblJoineries.setHeight(lblHeight);
		lblFinishes.setHeight(lblHeight);
		dummy.setHeight(lblHeight);
		
		lblGrouphdr.setWidth(strlblWidth);
		lblGrouphdr1.setWidth(strlblWidth);
		lblGrouphdr2.setWidth(strlblWidth);
		chkOforBy.setWidth(strlblWidth);
		lblTypeStructure.setWidth(strlblWidth);
		lblFoundation.setWidth(strlblWidth);
		lblBasement.setWidth(strlblWidth);
		lblSuperStruct.setWidth(strlblWidth);
		lblRoofing.setWidth(strlblWidth);
		lblFlooring.setWidth(strlblWidth);
		lblJoineries.setWidth(strlblWidth);
		lblFinishes.setWidth(strlblWidth);

		lblDeed.setWidth(strlblWidth);
		tfStructureDeed.setWidth(strlblWidth);
		tfFoundationDeed.setWidth(strlblWidth);
		tfBasementDeed.setWidth(strlblWidth);
		tfSuperStructDeed.setWidth(strlblWidth);
		tfRoofingDeed.setWidth(strlblWidth);
		tfFlooringDeed.setWidth(strlblWidth);
		tfJoineriesDeed.setWidth(strlblWidth);
		tfFinishesDeed.setWidth(strlblWidth);

		lblSite.setWidth(strlblWidth);
		tfStructureSite.setWidth(strlblWidth);
		tfFoundationSite.setWidth(strlblWidth);
		tfBasementSite.setWidth(strlblWidth);
		tfSuperStructSite.setWidth(strlblWidth);
		tfRoofingSite.setWidth(strlblWidth);
		tfFlooringSite.setWidth(strlblWidth);
		tfJoineriesSite.setWidth(strlblWidth);
		tfFinishesSite.setWidth(strlblWidth);

		lblPlan.setWidth(strlblWidth);
		tfStructurePlan.setWidth(strlblWidth);
		tfFoundationPlan.setWidth(strlblWidth);
		tfBasementPlan.setWidth(strlblWidth);
		tfSuperStructPlan.setWidth(strlblWidth);
		tfRoofingPlan.setWidth(strlblWidth);
		tfFlooringPlan.setWidth(strlblWidth);
		tfJoineriesPlan.setWidth(strlblWidth);
		tfFinishesPlan.setWidth(strlblWidth);
		btnAdd.setIcon(new ThemeResource(
				Common.strAddIcon));
		btnAdd.setStyleName(Runo.BUTTON_LINK);
		
		
		lblDeed.setNullSelectionAllowed(false);
		tfStructureDeed.setNullSelectionAllowed(false);
		tfFoundationDeed.setNullSelectionAllowed(false);
		tfBasementDeed.setNullSelectionAllowed(false);
		tfSuperStructDeed.setNullSelectionAllowed(false);
		tfRoofingDeed.setNullSelectionAllowed(false);
		tfFlooringDeed.setNullSelectionAllowed(false);
		tfJoineriesDeed.setNullSelectionAllowed(false);
		tfFinishesDeed.setNullSelectionAllowed(false);

		lblSite.setNullSelectionAllowed(false);
		tfStructureSite.setNullSelectionAllowed(false);
		tfFoundationSite.setNullSelectionAllowed(false);
		tfBasementSite.setNullSelectionAllowed(false);
		tfSuperStructSite.setNullSelectionAllowed(false);
		tfRoofingSite.setNullSelectionAllowed(false);
		tfFlooringSite.setNullSelectionAllowed(false);
		tfJoineriesSite.setNullSelectionAllowed(false);
		tfFinishesSite.setNullSelectionAllowed(false);

		lblPlan.setNullSelectionAllowed(false);
		tfStructurePlan.setNullSelectionAllowed(false);
		tfFoundationPlan.setNullSelectionAllowed(false);
		tfBasementPlan.setNullSelectionAllowed(false);
		tfSuperStructPlan.setNullSelectionAllowed(false);
		tfRoofingPlan.setNullSelectionAllowed(false);
		tfFlooringPlan.setNullSelectionAllowed(false);
		tfJoineriesPlan.setNullSelectionAllowed(false);
		tfFinishesPlan.setNullSelectionAllowed(false);
		
		
		lblDeed.setInputPrompt(Common.SELECT_PROMPT);
		tfStructureDeed.setInputPrompt(Common.SELECT_PROMPT);
		tfFoundationDeed.setInputPrompt(Common.SELECT_PROMPT);
		tfBasementDeed.setInputPrompt(Common.SELECT_PROMPT);
		tfSuperStructDeed.setInputPrompt(Common.SELECT_PROMPT);
		tfRoofingDeed.setInputPrompt(Common.SELECT_PROMPT);
		tfFlooringDeed.setInputPrompt(Common.SELECT_PROMPT);
		tfJoineriesDeed.setInputPrompt(Common.SELECT_PROMPT);
		tfFinishesDeed.setInputPrompt(Common.SELECT_PROMPT);

		lblSite.setInputPrompt(Common.SELECT_PROMPT);
		tfStructureSite.setInputPrompt(Common.SELECT_PROMPT);
		tfFoundationSite.setInputPrompt(Common.SELECT_PROMPT);
		tfBasementSite.setInputPrompt(Common.SELECT_PROMPT);
		tfSuperStructSite.setInputPrompt(Common.SELECT_PROMPT);
		tfRoofingSite.setInputPrompt(Common.SELECT_PROMPT);
		tfFlooringSite.setInputPrompt(Common.SELECT_PROMPT);
		tfJoineriesSite.setInputPrompt(Common.SELECT_PROMPT);
		tfFinishesSite.setInputPrompt(Common.SELECT_PROMPT);

		lblPlan.setInputPrompt(Common.SELECT_PROMPT);
		tfStructurePlan.setInputPrompt(Common.SELECT_PROMPT);
		tfFoundationPlan.setInputPrompt(Common.SELECT_PROMPT);
		tfBasementPlan.setInputPrompt(Common.SELECT_PROMPT);
		tfSuperStructPlan.setInputPrompt(Common.SELECT_PROMPT);
		tfRoofingPlan.setInputPrompt(Common.SELECT_PROMPT);
		tfFlooringPlan.setInputPrompt(Common.SELECT_PROMPT);
		tfJoineriesPlan.setInputPrompt(Common.SELECT_PROMPT);
		tfFinishesPlan.setInputPrompt(Common.SELECT_PROMPT);
		
		lblGrouphdr.setNullSelectionAllowed(false);
		lblGrouphdr1.setNullSelectionAllowed(false);
		lblGrouphdr2.setNullSelectionAllowed(false);

	}

	void getDetailsSameAsDeed() {
		
		tfStructureSite.setValue(tfStructureDeed.getValue());
		tfFoundationSite.setValue(tfFoundationDeed.getValue());
		tfBasementSite.setValue(tfBasementDeed.getValue());
		tfSuperStructSite.setValue(tfSuperStructDeed.getValue());
		tfRoofingSite.setValue(tfRoofingDeed.getValue());
		tfFlooringSite.setValue(tfFlooringDeed.getValue());
		tfJoineriesSite.setValue(tfJoineriesDeed.getValue());
		tfFinishesSite.setValue(tfFinishesDeed.getValue());
		

		tfStructurePlan.setValue(tfStructureDeed.getValue());
		tfFoundationPlan.setValue(tfFoundationDeed.getValue());
		tfBasementPlan.setValue(tfBasementDeed.getValue());
		tfSuperStructPlan.setValue(tfSuperStructDeed.getValue());
		tfRoofingPlan.setValue(tfRoofingDeed.getValue());
		tfFlooringPlan.setValue(tfFlooringDeed.getValue());
		tfJoineriesPlan.setValue(tfJoineriesDeed.getValue());
		tfFinishesPlan.setValue(tfFinishesDeed.getValue());

	}

	void getDetailsDifferentAsDeed() {
		tfStructureSite.setValue("");
		tfFoundationSite.setValue("");
		tfBasementSite.setValue("");
		tfSuperStructSite.setValue("");
		tfRoofingSite.setValue("");
		tfFlooringSite.setValue("");
		tfJoineriesSite.setValue("");
		tfFinishesSite.setValue("");
		

		tfStructurePlan.setValue("");
		tfFoundationPlan.setValue("");
		tfBasementPlan.setValue("");
		tfSuperStructPlan.setValue("");
		tfRoofingPlan.setValue("");
		tfFlooringPlan.setValue("");
		tfJoineriesPlan.setValue("");
		tfFinishesPlan.setValue("");

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
