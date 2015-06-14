/*
 * @author="soundarc"
 */

package com.gnts.pem.txn.sbi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.erputil.ui.PanelGenerator;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.txn.common.TPemCmAssetDetails;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.domain.txn.common.TPemCmOwnerDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropGuidlnValue;
import com.gnts.pem.domain.txn.common.TPemCmPropLegalDocs;
import com.gnts.pem.domain.txn.common.TPemCmPropValtnSummry;
import com.gnts.pem.service.mst.CmBankService;
import com.gnts.pem.service.txn.common.CmAssetDetailsService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;
import com.gnts.pem.service.txn.common.CmOwnerDetailsService;
import com.gnts.pem.service.txn.common.CmPropDocDetailsService;
import com.gnts.pem.service.txn.common.CmPropGuidlnValueService;
import com.gnts.pem.service.txn.common.CmPropLegalDocsService;
import com.gnts.pem.service.txn.common.CmPropValtnSummryService;
import com.gnts.pem.util.UIFlowData;
import com.gnts.pem.util.XMLUtil;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class SBIMultiProperty implements ClickListener {
private static final long serialVersionUID = 1L;
private CmEvalDetailsService beanEvaluation=(CmEvalDetailsService)SpringContextHelper.getBean("evalDtls");
private CmOwnerDetailsService  beanCustomer= (CmOwnerDetailsService) SpringContextHelper.getBean("ownerDtls");
private CmAssetDetailsService  beanAsset= (CmAssetDetailsService) SpringContextHelper
		.getBean("assetDtls");
private CmPropDocDetailsService  beanDocument= (CmPropDocDetailsService) SpringContextHelper
		.getBean("propDocument");
private CmPropLegalDocsService beanleaglDocuments = (CmPropLegalDocsService) SpringContextHelper
		.getBean("legalDoc");
private CmPropValtnSummryService beanPropertyvalue = (CmPropValtnSummryService) SpringContextHelper
.getBean("propValtnSummary");
private CmPropGuidlnValueService beanguidelinevalue = (CmPropGuidlnValueService) SpringContextHelper
.getBean("guidelineValue");
private CmBankService beanbank = (CmBankService) SpringContextHelper
.getBean("bank");

	private Accordion accordion1 = new Accordion();
	private Accordion accordion2 = new Accordion();
	private Accordion accordion3 = new Accordion();
	private Accordion accordion4 = new Accordion();

	private TPemCmEvalDetails selectedEvaluation1;
	private TPemCmEvalDetails selectedEvaluation2;
	private TPemCmEvalDetails selectedEvaluation3;

	// for common
	private GridLayout mainPanel = new GridLayout();
	private HorizontalLayout searchPanel = new HorizontalLayout();
	private Button saveExcel = new Button("Report", this);

	private Long headerid;
	private String strEvaluationNo = "SBI_MUTIPLE_";
	private String strXslFile = "SbiMutiple.xsl";
	private String strComponentWidth1 = "100px";
	private String strComponentWidth2 = "150px";
	private Long selectedBankid,selectCompanyid,currencyId;
	private String SelectedFormName,screenName;
	private String loginusername;

	// search bar
	// for search property1
	private VerticalLayout searchLayout1 = new VerticalLayout();
	private VerticalLayout layoutSearch1 = new VerticalLayout();
	private PopupDateField dfSearchStartDate1 = new PopupDateField(
			"Evaluation Start Date");
	private PopupDateField dfSearchEndDate1 = new PopupDateField(
			"Evaluation End Date");
	private ComboBox slEvaluationNumber1 = new ComboBox("Evaluation Number");

	// for search property2
	private VerticalLayout searchLayout2 = new VerticalLayout();
	private VerticalLayout layoutSearch2 = new VerticalLayout();
	private PopupDateField dfSearchStartDate2 = new PopupDateField(
			"Evaluation Start Date");
	private PopupDateField dfSearchEndDate2 = new PopupDateField(
			"Evaluation End Date");
	private ComboBox slEvaluationNumber2 = new ComboBox("Evaluation Number");

	// for search property3
	private VerticalLayout searchLayout3 = new VerticalLayout();
	private VerticalLayout layoutSearch3 = new VerticalLayout();
	private PopupDateField dfSearchStartDate3 = new PopupDateField(
			"Evaluation Start Date");
	private PopupDateField dfSearchEndDate3 = new PopupDateField(
			"Evaluation End Date");
	private ComboBox slEvaluationNumber3 = new ComboBox("Evaluation Number");

	// for main panel property
	private FormLayout layoutFairmarket = new FormLayout();
	private TextField tfFairmarketValue1 = new TextField("Property - 1");
	private TextField tfFairmarketValue2 = new TextField("Property - 2");
	private TextField tfFairmarketValue3 = new TextField("Property - 3");
	private TextField tfFairmarketTotal = new TextField();

	// for realize value
	private FormLayout layoutRealizable = new FormLayout();
	private TextField tfRealizableValue1 = new TextField("Property - 1");
	private TextField tfRealizableValue2 = new TextField("Property - 2");
	private TextField tfRealizableValue3 = new TextField("Property - 3");
	private TextField tfRealizableTotal = new TextField();

	// for distress value
	private FormLayout layoutdistress = new FormLayout();
	private TextField tfDistressValue1 = new TextField("Property - 1");
	private TextField tfDistressValue2 = new TextField("Property - 2");
	private TextField tfDistressValue3 = new TextField("Property - 3");
	private TextField tfDistressTotal = new TextField();
	
	// for guideline value
	private FormLayout layoutGuideline = new FormLayout();
	private TextField tfGuidelineValue1 = new TextField("Property - 1");
	private TextField tfGuidelineValue2 = new TextField("Property - 2");
	private TextField tfGuidelineValue3 = new TextField("Property - 3");
	private TextField tfGuidelineTotal = new TextField();

	private Logger logger = Logger.getLogger(SBIMultiProperty.class);
	public SBIMultiProperty() {
	
		
		loginusername = UI.getCurrent().getSession().getAttribute("loginUserName").toString();
		selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("loginCompanyId").toString());
		currencyId=Long.valueOf(UI.getCurrent().getSession()
				.getAttribute("currenyId").toString());
		VerticalLayout clArgumentLayout=(VerticalLayout) UI.getCurrent().getSession().getAttribute("clLayout");
		//HorizontalLayout hlHeaderLayout= (HorizontalLayout)UI.getCurrent().getSession().getAttribute("hlLayout");
		screenName = UI.getCurrent().getSession().getAttribute("screenName")
				.toString();
		SelectedFormName = screenName;
		String[] splitlist=screenName.split("-");
		for(String str:splitlist){
		List<MPemCmBank> list=beanbank.getBankDtlsList(selectCompanyid, null, str);
		
		for(MPemCmBank obj:list){
		selectedBankid = obj.getBankId();
		}
		break;
		}
		buildView(clArgumentLayout);
}

	@SuppressWarnings("deprecation")
	void buildView(VerticalLayout layoutPage) {
		// for add component style
		setComponentStyle();

		slEvaluationNumber1.setImmediate(true);
		slEvaluationNumber1
				.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
		slEvaluationNumber1.setItemCaptionPropertyId("evalno");
		loadEvaluationDetails1();
		slEvaluationNumber1.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					BeanItem<?> item = (BeanItem<?>) slEvaluationNumber1
							.getItem(itemid);
					selectedEvaluation1 = (TPemCmEvalDetails) item.getBean();
					property1Deteials();
					getTotal();

				}

			}
		});

		slEvaluationNumber2.setImmediate(true);
		slEvaluationNumber2
				.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
		slEvaluationNumber2.setItemCaptionPropertyId("evalno");
		slEvaluationNumber2.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					BeanItem<?> item = (BeanItem<?>) slEvaluationNumber2
							.getItem(itemid);
					selectedEvaluation2 = (TPemCmEvalDetails) item.getBean();
					property2Deteials();
					getTotal();
				}

			}
		});

		slEvaluationNumber3.setImmediate(true);
		slEvaluationNumber3
				.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
		slEvaluationNumber3.setItemCaptionPropertyId("evalno");
		slEvaluationNumber3.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				Object itemid = event.getProperty().getValue();
				if (itemid != null) {
					BeanItem<?> item = (BeanItem<?>) slEvaluationNumber3
							.getItem(itemid);
					selectedEvaluation3 = (TPemCmEvalDetails) item.getBean();
					property3Deteials();
					getTotal();
				}

			}
		});

		// for property 1
		searchLayout1.addComponent(dfSearchStartDate1);
		searchLayout1.addComponent(dfSearchEndDate1);
		searchLayout1.setSpacing(true);

		layoutSearch1.setCaption("Property 1");
		// layoutSearch1.addComponent(searchLayout1);
		layoutSearch1.addComponent(slEvaluationNumber1);
		layoutSearch1.setSpacing(true);
		layoutSearch1.setMargin(true);

		// for property 2
		searchLayout2.addComponent(dfSearchStartDate2);
		searchLayout2.addComponent(dfSearchEndDate2);
		searchLayout2.setSpacing(true);

		layoutSearch2.setCaption("Property 2");
		// layoutSearch2.addComponent(searchLayout2);
		layoutSearch2.addComponent(slEvaluationNumber2);
		layoutSearch2.setSpacing(true);
		layoutSearch2.setMargin(true);

		// for property 3
		searchLayout3.addComponent(dfSearchStartDate3);
		searchLayout3.addComponent(dfSearchEndDate3);
		searchLayout3.setSpacing(true);

		layoutSearch3.setCaption("Property 3");
		// layoutSearch3.addComponent(searchLayout3);
		layoutSearch3.addComponent(slEvaluationNumber3);
		layoutSearch3.setSpacing(true);
		layoutSearch3.setMargin(true);

		// add components in search bar
		searchPanel.addComponent(PanelGenerator.createPanel(layoutSearch1));
		searchPanel.addComponent(PanelGenerator.createPanel(layoutSearch2));
		searchPanel.addComponent(PanelGenerator.createPanel(layoutSearch3));
		searchPanel.setMargin(true);
		searchPanel.setSpacing(true);

		// for fairmarket
		layoutFairmarket.addComponent(tfFairmarketValue1);
		layoutFairmarket.addComponent(tfFairmarketValue2);
		layoutFairmarket.addComponent(tfFairmarketValue3);
		layoutFairmarket.addComponent(tfFairmarketTotal);

		// for realizable value
		layoutRealizable.addComponent(tfRealizableValue1);
		layoutRealizable.addComponent(tfRealizableValue2);
		layoutRealizable.addComponent(tfRealizableValue3);
		layoutRealizable.addComponent(tfRealizableTotal);

		// for disttess value
		layoutdistress.addComponent(tfDistressValue1);
		layoutdistress.addComponent(tfDistressValue2);
		layoutdistress.addComponent(tfDistressValue3);
		layoutdistress.addComponent(tfDistressTotal);

		// for guideline value
		layoutGuideline.addComponent(tfGuidelineValue1);
		layoutGuideline.addComponent(tfGuidelineValue2);
		layoutGuideline.addComponent(tfGuidelineValue3);
		layoutGuideline.addComponent(tfGuidelineTotal);

		accordion1.addTab(layoutFairmarket, "Fairmarket value");
		accordion2.addTab(layoutRealizable, "Realizable value");
		accordion3.addTab(layoutdistress, "Distress value value");
		accordion4.addTab(layoutGuideline, "Guideline value value");

		mainPanel.setColumns(4);
		mainPanel.addComponent(PanelGenerator.createPanel(accordion1));
		mainPanel.addComponent(PanelGenerator.createPanel(accordion2));
		mainPanel.addComponent(PanelGenerator.createPanel(accordion3));
		mainPanel.addComponent(PanelGenerator.createPanel(accordion4));
		mainPanel.setSpacing(true);
		mainPanel.setMargin(true);

		mainPanel.setMargin(true);

		layoutPage.addComponent(new VerticalLayout() {
			private static final long serialVersionUID = 1L;
			{
				addComponent(PanelGenerator.createPanel(searchPanel));
				addComponent(PanelGenerator.createPanel(mainPanel));
				addComponent(saveExcel);
				setComponentAlignment(saveExcel, Alignment.TOP_RIGHT);
				setMargin(true);
				setSpacing(true);
			}
		});

		saveExcel.addStyleName("downloadbt");
		String basepath = VaadinService.getCurrent().getBaseDirectory()
				.getAbsolutePath();
		Resource res = new FileResource(new File(basepath
				+ "/WEB-INF/view/channel.doc"));
		FileDownloader fd = new FileDownloader(res);
		fd.extend(saveExcel);

		resetProperty1();
		resetProperty2();
		resetProperty3();
	}

	private void setComponentStyle() {

		// for property 1
		dfSearchStartDate1.setWidth(strComponentWidth1);
		dfSearchEndDate1.setWidth(strComponentWidth1);
		slEvaluationNumber1.setWidth(strComponentWidth2);
		dfSearchStartDate1.setDateFormat("dd-MMM-yyy");
		dfSearchEndDate1.setDateFormat("dd-MMM-yyy");

		// for property 2
		dfSearchStartDate2.setWidth(strComponentWidth1);
		dfSearchEndDate2.setWidth(strComponentWidth1);
		slEvaluationNumber2.setWidth(strComponentWidth2);
		dfSearchStartDate2.setDateFormat("dd-MMM-yyy");
		dfSearchEndDate2.setDateFormat("dd-MMM-yyy");

		// for property 3
		dfSearchStartDate3.setWidth(strComponentWidth1);
		dfSearchEndDate3.setWidth(strComponentWidth1);
		slEvaluationNumber3.setWidth(strComponentWidth2);
		dfSearchStartDate3.setDateFormat("dd-MMM-yyy");
		dfSearchEndDate3.setDateFormat("dd-MMM-yyy");

		tfFairmarketValue1.setStyleName("textright");
		tfFairmarketValue2.setStyleName("textright");
		tfFairmarketValue3.setStyleName("textright");
		tfFairmarketTotal.setStyleName("textright");

		tfRealizableValue1.setStyleName("textright");
		tfRealizableValue2.setStyleName("textright");
		tfRealizableValue3.setStyleName("textright");
		tfRealizableTotal.setStyleName("textright");

		tfDistressValue1.setStyleName("textright");
		tfDistressValue2.setStyleName("textright");
		tfDistressValue3.setStyleName("textright");
		tfDistressTotal.setStyleName("textright");

		tfGuidelineValue1.setStyleName("textright");
		tfGuidelineValue2.setStyleName("textright");
		tfGuidelineValue3.setStyleName("textright");
		tfGuidelineTotal.setStyleName("textright");
	}

	private void loadEvaluationDetails1() {
		try {
			List<TPemCmEvalDetails> list = beanEvaluation.getSearchEvalDetailnList(null,null,null, null,null, selectedBankid, null, null);
			BeanItemContainer<TPemCmEvalDetails> trnsType = new BeanItemContainer<TPemCmEvalDetails>(
					TPemCmEvalDetails.class);
			trnsType.addAll(list);
			slEvaluationNumber1.setContainerDataSource(trnsType);
			slEvaluationNumber2.setContainerDataSource(trnsType);
			slEvaluationNumber3.setContainerDataSource(trnsType);
		} catch (Exception e) {
			logger.info("fn_loadEvaluationDetails1()---->" + e);
		}
	}

	void property1Deteials() {
		try {
			List<TPemCmPropValtnSummry> list = beanPropertyvalue.getPropValtnSummryList(selectedEvaluation1.getDocId());
			TPemCmPropValtnSummry obj = list.get(0);
			tfFairmarketValue1.setValue(obj.getFieldValue());
			obj = list.get(1);
			tfRealizableValue1.setValue(obj.getFieldValue());
			obj = list.get(2);
			tfDistressValue1.setValue(obj.getFieldValue());
			obj = list.get(3);
			tfGuidelineValue1.setValue(obj.getFieldValue());
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}
	}

	void property2Deteials() {
		try {
			List<TPemCmPropValtnSummry> list = beanPropertyvalue
					.getPropValtnSummryList(selectedEvaluation2.getDocId());
			TPemCmPropValtnSummry obj = list.get(0);
			tfFairmarketValue2.setValue(obj.getFieldValue());
			obj = list.get(1);
			tfRealizableValue2.setValue(obj.getFieldValue());
			obj = list.get(2);
			tfDistressValue2.setValue(obj.getFieldValue());
			obj = list.get(3);
			tfGuidelineValue2.setValue(obj.getFieldValue());
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}
	}

	void property3Deteials() {
		try {
			List<TPemCmPropValtnSummry> list = beanPropertyvalue
					.getPropValtnSummryList(selectedEvaluation3.getDocId());
			TPemCmPropValtnSummry obj = list.get(0);
			tfFairmarketValue3.setValue(obj.getFieldValue());
			obj = list.get(1);
			tfRealizableValue3.setValue(obj.getFieldValue());
			obj = list.get(2);
			tfDistressValue3.setValue(obj.getFieldValue());
			obj = list.get(3);
			tfGuidelineValue3.setValue(obj.getFieldValue());
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}
	}

	private void getTotal() {

		try {
			// for fair market
			String fairmarket1 = tfFairmarketValue1.getValue().replaceAll(
					"[^0-9]", "");
			String fairmarket2 = tfFairmarketValue2.getValue().replaceAll(
					"[^0-9]", "");
			String fairmarket3 = tfFairmarketValue3.getValue().replaceAll(
					"[^0-9]", "");
			BigDecimal fairmarkettotal = new BigDecimal(fairmarket1).add(
					new BigDecimal(fairmarket2)).add(
					new BigDecimal(fairmarket3));
			tfFairmarketTotal.setValue(fairmarkettotal.toString());
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

		try {
			// for realizable value
			String realizable1 = tfRealizableValue1.getValue().replaceAll(
					"[^0-9]", "");
			String realizable2 = tfRealizableValue2.getValue().replaceAll(
					"[^0-9]", "");
			String realizable3 = tfRealizableValue3.getValue().replaceAll(
					"[^0-9]", "");
			BigDecimal realizabletotal = new BigDecimal(realizable1).add(
					new BigDecimal(realizable2)).add(
					new BigDecimal(realizable3));
			tfRealizableTotal.setValue(realizabletotal.toString());
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

		try {
			// for distress value
			String distress1 = tfDistressValue1.getValue().replaceAll("[^0-9]",
					"");
			String distress2 = tfDistressValue2.getValue().replaceAll("[^0-9]",
					"");
			String distress3 = tfDistressValue3.getValue().replaceAll("[^0-9]",
					"");
			BigDecimal distresstotal = new BigDecimal(distress1).add(
					new BigDecimal(distress2)).add(new BigDecimal(distress3));
			tfDistressTotal.setValue(distresstotal.toString());
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}

		try {
			// for guideline value
			String guideline1 = tfGuidelineValue1.getValue().replaceAll(
					"[^0-9]", "");
			String guideline2 = tfGuidelineValue2.getValue().replaceAll(
					"[^0-9]", "");
			String guideline3 = tfGuidelineValue3.getValue().replaceAll(
					"[^0-9]", "");
			BigDecimal guidelinetotal = new BigDecimal(guideline1).add(
					new BigDecimal(guideline2)).add(new BigDecimal(guideline3));
			tfGuidelineTotal.setValue(guidelinetotal.toString());
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}
	}

	void resetProperty1() {
		tfFairmarketValue1.setValue("0");
		tfRealizableValue1.setValue("0");
		tfDistressValue1.setValue("0");
		tfGuidelineValue1.setValue("0");
	}

	void resetProperty2() {
		tfFairmarketValue2.setValue("0");
		tfRealizableValue2.setValue("0");
		tfDistressValue2.setValue("0");
		tfGuidelineValue2.setValue("0");
	}

	void resetProperty3() {
		tfFairmarketValue3.setValue("0");
		tfRealizableValue3.setValue("0");
		tfDistressValue3.setValue("0");
		tfGuidelineValue3.setValue("0");
	}

	private void getUIflowDataList() {
		UIFlowData uiflowdata = new UIFlowData();

		try{
		if (selectedEvaluation1 != null) {
			// for evaluation details
			
			TPemCmEvalDetails eval = new TPemCmEvalDetails();
			eval.setDocId(selectedEvaluation1.getDocId());
			eval.setEvalDate(new Date(selectedEvaluation1.getEvalDate()));
			eval.setBankBranch(selectedEvaluation1.getBankBranch());
			uiflowdata.setEvalDtls(eval);
			// for customer
			List<TPemCmOwnerDetails> custlist = beanCustomer.getOwnerDtlsList(selectedEvaluation1.getDocId());
			for (int i = 0; i < custlist.size(); i = i + 2) {
				TPemCmOwnerDetails cust = custlist.get(i);
				uiflowdata.getCustomer().add(cust);
			}
			// for legalDocuments
			List<TPemCmPropLegalDocs> legallist = beanleaglDocuments.getPropLegalDocsList(selectedEvaluation1.getDocId());
			uiflowdata.setLegalDoc(legallist);
			//for property address
			List<TPemCmAssetDetails> assetlist=beanAsset.getAssetDetailsList(selectedEvaluation1.getDocId());
			TPemCmAssetDetails obj=assetlist.get(0);
			uiflowdata.getAssetDtls().add(obj);
			
			// for property value
			List<TPemCmPropValtnSummry> provalueList = beanPropertyvalue.getPropValtnSummryList(selectedEvaluation1.getDocId());
			uiflowdata.setPropertyValue(provalueList);
			
			//for guideline value
			List<TPemCmPropGuidlnValue> guidelist=beanguidelinevalue.getPropGuidlnRefdataList(selectedEvaluation1.getDocId());
			uiflowdata.setGuideline(guidelist);

		}
		}catch(Exception e){logger.info("Error-->"+e);}
		
		try{
		// for property 2
		if (selectedEvaluation2 != null) {
			// for evaluation details
			TPemCmEvalDetails eval = new TPemCmEvalDetails();
			eval.setDocId(selectedEvaluation2.getDocId());
			eval.setEvalDate(new Date(selectedEvaluation2.getEvalDate()));
			eval.setBankBranch(selectedEvaluation2.getBankBranch());
			uiflowdata.setEvalDtls(eval);
			// for customer
			List<TPemCmOwnerDetails> custlist = beanCustomer.getOwnerDtlsList(selectedEvaluation2.getDocId());
			for (int i = 0; i < custlist.size(); i = i + 2) {
				TPemCmOwnerDetails cust = custlist.get(i);
				uiflowdata.getCustomer().add(cust);
			}
			// for legalDocuments
			List<TPemCmPropLegalDocs> legallist = beanleaglDocuments.getPropLegalDocsList(selectedEvaluation2.getDocId());
			uiflowdata.setLegalDoc1(legallist);
			//for property address
			List<TPemCmAssetDetails> assetlist=beanAsset.getAssetDetailsList(selectedEvaluation2.getDocId());
			TPemCmAssetDetails obj=assetlist.get(0);
			uiflowdata.getAssetDtls1().add(obj);
			// for property value
			List<TPemCmPropValtnSummry> provalueList = beanPropertyvalue.getPropValtnSummryList(selectedEvaluation2.getDocId());
			uiflowdata.setPropertyValue1(provalueList);
			
			//for guideline value
			List<TPemCmPropGuidlnValue> guidelist=beanguidelinevalue.getPropGuidlnRefdataList(selectedEvaluation2.getDocId());
			uiflowdata.setGuideline1(guidelist);


		}
		}catch(Exception e){logger.info("Error-->"+e);}
		try{
		// for property 3
		if (selectedEvaluation3 != null) {
			// for evaluation details
			TPemCmEvalDetails eval = new TPemCmEvalDetails();
			eval.setDocId(selectedEvaluation3.getDocId());
			eval.setEvalDate(new Date(selectedEvaluation3.getEvalDate()));
			
			eval.setBankBranch(selectedEvaluation3.getBankBranch());
			uiflowdata.setEvalDtls(eval);
			// for customer
			List<TPemCmOwnerDetails> custlist = beanCustomer.getOwnerDtlsList(selectedEvaluation3.getDocId());
			for (int i = 0; i < custlist.size(); i = i + 2) {
				TPemCmOwnerDetails cust = custlist.get(i);
				uiflowdata.getCustomer().add(cust);
			}
			// for legalDocuments
			List<TPemCmPropLegalDocs> legallist = beanleaglDocuments.getPropLegalDocsList(selectedEvaluation3.getDocId());
			uiflowdata.setLegalDoc2(legallist);
			//for property address
			List<TPemCmAssetDetails> assetlist=beanAsset.getAssetDetailsList(selectedEvaluation3.getDocId());
			TPemCmAssetDetails obj=assetlist.get(0);
			uiflowdata.getAssetDtls2().add(obj);
			// for property value
			List<TPemCmPropValtnSummry> provalueList = beanPropertyvalue.getPropValtnSummryList(selectedEvaluation3.getDocId());
			uiflowdata.setPropertyValue2(provalueList);
			//for guideline value
			List<TPemCmPropGuidlnValue> guidelist=beanguidelinevalue.getPropGuidlnRefdataList(selectedEvaluation3.getDocId());
			uiflowdata.setGuideline2(guidelist);

		}
		}catch(Exception e){logger.info("Error-->"+e);}

		try{
			uiflowdata.setTotalFairMarket(tfFairmarketTotal.getValue());
			uiflowdata.setTotalRealizable(tfRealizableTotal.getValue());
			uiflowdata.setTotalDistress(tfDistressTotal.getValue());
			uiflowdata.setTotalGuideline(tfGuidelineTotal.getValue());
			String numberOnly = tfFairmarketTotal.getValue().replaceAll(
					"[^0-9]", "");
			uiflowdata.setAmountInWords(beanEvaluation.getAmountInWords(numberOnly));
	
			/*List<CmCommonSetup> bill = BillGenerator.getEndValueDetails(numberOnly,headerid,loginusername,selectedBankid,selectCompanyid);
			//	bill.setBillNo(Long.toString(beanEvaluation.getNextSequnceId("seq_pem_evaldtls_docid")));
				uiflowdata.setBillDtls(bill);
				
				TPemCmBillDtls billDtls=BillGenerator.getBillDetails(numberOnly, headerid, loginusername, selectedBankid, selectCompanyid,currencyId);
				uiflowdata.setBill(billDtls);*/
		}catch(Exception e){
			logger.info("Error-->"+e);
		}
			
			ByteArrayOutputStream recvstram = XMLUtil.doMarshall(uiflowdata);
			try {
				String propertyType="LAND";
				XMLUtil.getWordDocument(recvstram, strEvaluationNo + headerid,
						strXslFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("Error-->"+e);
			}
		
		
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (saveExcel == event.getButton()) {
			getUIflowDataList();
			}

	}

}
