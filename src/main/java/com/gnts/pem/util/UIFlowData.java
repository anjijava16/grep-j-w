package com.gnts.pem.util;

import javax.xml.bind.annotation.XmlRootElement;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.pem.domain.mst.CommissionSetupDM;
import com.gnts.pem.domain.mst.MPemCmBank;
import com.gnts.pem.domain.rpt.TPemCmBillDtls;
import com.gnts.pem.domain.txn.common.TPemCmAssetDetails;
import com.gnts.pem.domain.txn.common.TPemCmBldngAditnlItms;
import com.gnts.pem.domain.txn.common.TPemCmBldngCostofcnstructn;
import com.gnts.pem.domain.txn.common.TPemCmBldngExtraItems;
import com.gnts.pem.domain.txn.common.TPemCmBldngMiscData;
import com.gnts.pem.domain.txn.common.TPemCmBldngNewPlinthArea;
import com.gnts.pem.domain.txn.common.TPemCmBldngNewSpec;
import com.gnts.pem.domain.txn.common.TPemCmBldngOldPlinthArea;
import com.gnts.pem.domain.txn.common.TPemCmBldngOldSpec;
import com.gnts.pem.domain.txn.common.TPemCmBldngRiskDtls;
import com.gnts.pem.domain.txn.common.TPemCmBldngRoofHght;
import com.gnts.pem.domain.txn.common.TPemCmBldngService;
import com.gnts.pem.domain.txn.common.TPemCmBldngStgofcnstructn;
import com.gnts.pem.domain.txn.common.TPemCmBldngTechDetails;
import com.gnts.pem.domain.txn.common.TPemCmBldngValutnSummry;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.domain.txn.common.TPemCmFlatUnderValutn;
import com.gnts.pem.domain.txn.common.TPemCmLandValutnData;
import com.gnts.pem.domain.txn.common.TPemCmOwnerDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropAdjoinDtls;
import com.gnts.pem.domain.txn.common.TPemCmPropApplcntEstmate;
import com.gnts.pem.domain.txn.common.TPemCmPropDescription;
import com.gnts.pem.domain.txn.common.TPemCmPropDimension;
import com.gnts.pem.domain.txn.common.TPemCmPropDocDetails;
import com.gnts.pem.domain.txn.common.TPemCmPropGuidlnRefdata;
import com.gnts.pem.domain.txn.common.TPemCmPropGuidlnValue;
import com.gnts.pem.domain.txn.common.TPemCmPropImage;
import com.gnts.pem.domain.txn.common.TPemCmPropLegalDocs;
import com.gnts.pem.domain.txn.common.TPemCmPropNewPlanApprvl;
import com.gnts.pem.domain.txn.common.TPemCmPropOldPlanApprvl;
import com.gnts.pem.domain.txn.common.TPemCmPropRsnbleEstmate;
import com.gnts.pem.domain.txn.common.TPemCmPropValtnSummry;
import com.gnts.pem.domain.txn.sbi.TPemSbiBldngCnstructnDtls;
import com.gnts.pem.domain.txn.sbi.TPemSbiBldngElctrcInstltn;
import com.gnts.pem.domain.txn.sbi.TPemSbiBldngPlumpInstltn;
import com.gnts.pem.domain.txn.sbi.TPemSbiPropChartrstic;
import com.gnts.pem.domain.txn.synd.TPemSydPropMatchBoundry;
import com.gnts.pem.domain.txn.synd.TPemSynBldngRoom;
import com.gnts.pem.domain.txn.synd.TPemSynPropAreaDtls;
import com.gnts.pem.domain.txn.synd.TPemSynPropFloor;
import com.gnts.pem.domain.txn.synd.TPemSynPropOccupancy;
import com.gnts.pem.domain.txn.synd.TPemSynPropViolation;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.vaadin.ui.UI;

import java.util.ArrayList;
import java.util.List;
@XmlRootElement
public class UIFlowData {
	
	
	private CmBankConstantService beanBankconst = (CmBankConstantService) SpringContextHelper.getBean("bankConstant");
	
	private TPemCmBillDtls bill=new TPemCmBillDtls();
	private TPemCmEvalDetails evalDtls;
	private List<TPemCmBldngStgofcnstructn> stgofConstn=new ArrayList<TPemCmBldngStgofcnstructn>();
	private List<TPemCmBldngStgofcnstructn> stgofConstn1=new ArrayList<TPemCmBldngStgofcnstructn>();
	private List<TPemCmOwnerDetails> customer=new ArrayList<TPemCmOwnerDetails>();
	private List<TPemCmAssetDetails> assetDtls=new ArrayList<TPemCmAssetDetails>();
	private List<TPemCmPropDocDetails> document=new ArrayList<TPemCmPropDocDetails>();
	private List<TPemCmPropLegalDocs> legalDoc=new ArrayList<TPemCmPropLegalDocs>();
	private List<TPemCmPropAdjoinDtls> adjoinProperty=new ArrayList<TPemCmPropAdjoinDtls>();
	private List<TPemCmPropDimension> dimension =new ArrayList<TPemCmPropDimension>();
	private List<TPemCmBldngValutnSummry> valuationDtls=new ArrayList<TPemCmBldngValutnSummry>();
	private List<TPemCmPropValtnSummry> propertyValue=new ArrayList<TPemCmPropValtnSummry>();
	private List<TPemCmPropValtnSummry> propertyValue1=new ArrayList<TPemCmPropValtnSummry>();
	private List<TPemCmPropValtnSummry> propertyValue2=new ArrayList<TPemCmPropValtnSummry>();
	private List<TPemCmAssetDetails> assetDtls1=new ArrayList<TPemCmAssetDetails>();
	private List<TPemCmAssetDetails> assetDtls2=new ArrayList<TPemCmAssetDetails>();
	private List<TPemCmBldngMiscData> miscell=new ArrayList<TPemCmBldngMiscData>();
	private List<TPemCmBldngService> service=new ArrayList<TPemCmBldngService>();
	private List<TPemCmPropImage> propImage=new ArrayList<TPemCmPropImage>();
	private List<TPemCmPropDescription> propertyDescription=new ArrayList<TPemCmPropDescription>();
	private List<TPemCmPropDescription> propertyDescription1=new ArrayList<TPemCmPropDescription>();
	private List<TPemCmPropDescription> propertyDescription2=new ArrayList<TPemCmPropDescription>();
	private List<TPemCmPropDescription> propertyDescription3=new ArrayList<TPemCmPropDescription>();
	private List<TPemCmPropDescription> propertyDescription4=new ArrayList<TPemCmPropDescription>();
	private List<TPemCmPropDescription> propertyDescription5=new ArrayList<TPemCmPropDescription>();
	private List<TPemCmPropLegalDocs> legalDoc1=new ArrayList<TPemCmPropLegalDocs>();
	private List<TPemCmPropLegalDocs> legalDoc2=new ArrayList<TPemCmPropLegalDocs>();
	private List<CommissionSetupDM> billDtls=new ArrayList<CommissionSetupDM>();
			
	
	//section	
	private List<TPemSynPropAreaDtls> areaDetails=new ArrayList<TPemSynPropAreaDtls>();
	private List<TPemSynPropAreaDtls> areaDetails1=new ArrayList<TPemSynPropAreaDtls>();
	
	private List<MPemCmBank> bank=new ArrayList<MPemCmBank>();
	private List<TPemCmBldngTechDetails> buildingDtls =new ArrayList<TPemCmBldngTechDetails>();
	private List<TPemCmBldngTechDetails> buildingDtls1=new ArrayList<TPemCmBldngTechDetails>();
	private List<TPemCmBldngOldSpec> buildSpec =new ArrayList<TPemCmBldngOldSpec>();
	private List<TPemCmBldngNewSpec> buildSpec2 =new ArrayList<TPemCmBldngNewSpec>();
	private List<TPemCmBldngValutnSummry> buildValuation=new ArrayList<TPemCmBldngValutnSummry>();
	private List<TPemCmBldngRiskDtls> earthQuake=new ArrayList<TPemCmBldngRiskDtls>();
	private List<TPemSynPropFloor> floor=new ArrayList<TPemSynPropFloor>();
	private List<TPemSynPropFloor> floor1=new ArrayList<TPemSynPropFloor>();
	private List<TPemCmBldngAditnlItms> addtionalItms=new ArrayList<TPemCmBldngAditnlItms>();
	private List<TPemCmLandValutnData> landval=new ArrayList<TPemCmLandValutnData>();
	private List<TPemSydPropMatchBoundry> boundary=new ArrayList<TPemSydPropMatchBoundry>();
	private List<TPemSydPropMatchBoundry> boundary1=new ArrayList<TPemSydPropMatchBoundry>();
	private List<TPemSbiPropChartrstic> propertyConstruct=new ArrayList<TPemSbiPropChartrstic>();
	private List<TPemSbiPropChartrstic> propertyConstruct1=new ArrayList<TPemSbiPropChartrstic>();
	private List<TPemSbiPropChartrstic> propertyConstruct2=new ArrayList<TPemSbiPropChartrstic>();
	private List<TPemSbiPropChartrstic> propertyConstruct3=new ArrayList<TPemSbiPropChartrstic>();
	private List<TPemSbiPropChartrstic> propertyConstruct4=new ArrayList<TPemSbiPropChartrstic>();
	private List<TPemSbiPropChartrstic> propertyConstruct5=new ArrayList<TPemSbiPropChartrstic>();
	private List<TPemSynPropOccupancy> propertyOccupancy=new ArrayList<TPemSynPropOccupancy>();
	private List<TPemSynPropOccupancy> propertyOccupancy1=new ArrayList<TPemSynPropOccupancy>();
	private List<TPemSynPropOccupancy> propertyOccupancy2=new ArrayList<TPemSynPropOccupancy>();
	private List<TPemSynPropOccupancy> propertyOccupancy3=new ArrayList<TPemSynPropOccupancy>();
	private List<TPemSynPropOccupancy> propertyOccupancy4=new ArrayList<TPemSynPropOccupancy>();
	private List<TPemSynPropViolation> propertyViolation=new ArrayList<TPemSynPropViolation>();
	private List<TPemSynBldngRoom> room=new ArrayList<TPemSynBldngRoom>();
	private List<TPemCmPropGuidlnRefdata> guidelineref=new ArrayList<TPemCmPropGuidlnRefdata>();
	private List<TPemSbiBldngCnstructnDtls> detailsBuilding=new ArrayList<TPemSbiBldngCnstructnDtls>();
	private List<TPemSbiBldngCnstructnDtls> detailsBuilding1=new ArrayList<TPemSbiBldngCnstructnDtls>();
	private List<TPemSbiBldngCnstructnDtls> detailsBuilding2=new ArrayList<TPemSbiBldngCnstructnDtls>();
	private List<TPemCmBldngOldPlinthArea> plinthArea=new ArrayList<TPemCmBldngOldPlinthArea>();
	private List<TPemCmBldngNewPlinthArea> plinthArea1=new ArrayList<TPemCmBldngNewPlinthArea>();
	private List<TPemCmFlatUnderValutn> flatValuation=new ArrayList<TPemCmFlatUnderValutn>();
	private List<TPemCmPropOldPlanApprvl> planApproval=new ArrayList<TPemCmPropOldPlanApprvl>();
	private List<TPemCmPropOldPlanApprvl> planApproval1=new ArrayList<TPemCmPropOldPlanApprvl>();
	private List<TPemCmPropNewPlanApprvl> planApproval2=new ArrayList<TPemCmPropNewPlanApprvl>();
	private List<TPemCmPropNewPlanApprvl> planApproval3=new ArrayList<TPemCmPropNewPlanApprvl>();
	
	private List<TPemCmBldngCostofcnstructn> constValuation=new ArrayList<TPemCmBldngCostofcnstructn>();
	private List<TPemCmBldngRoofHght> roofHeight=new ArrayList<TPemCmBldngRoofHght>();
	private List<TPemSbiBldngPlumpInstltn> plumInstln=new ArrayList<TPemSbiBldngPlumpInstltn>();
	private List<TPemSbiBldngElctrcInstltn> elecInstln=new ArrayList<TPemSbiBldngElctrcInstltn>();
	//calculation
	private List<TPemCmBldngExtraItems> extraItem=new ArrayList<TPemCmBldngExtraItems>();
	private List<TPemCmPropGuidlnValue> guideline=new ArrayList<TPemCmPropGuidlnValue>();
	private List<TPemCmPropGuidlnRefdata> guideRef=new ArrayList<TPemCmPropGuidlnRefdata>();
	private List<TPemCmPropApplcntEstmate> applicantEstimate=new ArrayList<TPemCmPropApplcntEstmate>();
	private List<TPemCmPropRsnbleEstmate> applicantReason=new ArrayList<TPemCmPropRsnbleEstmate>();
	
	private List<TPemCmPropGuidlnValue> guideline1=new ArrayList<TPemCmPropGuidlnValue>();
	private List<TPemCmPropGuidlnValue> guideline2=new ArrayList<TPemCmPropGuidlnValue>();
	
	//
	private String propertyAddress;
	private String customername;
	private String bankBranch;
	private String inspectionDate;
	private String billDate;
	private String valuationDate;
	private String valuationPurpose;
	private String propDesc;
	private String marketValue;
	private String guidelinevalue;
	private String realizablevalue;
	private String distressvalue;
	private String amountInWords;
	private String amountWordsGuideline;
	private String guideland;
	private String signature;
	private String totalFairMarket;
	private String totalRealizable;
	private String totalDistress;
	private String totalGuideline;
	private String totalValuation;
	private String evalnumber;
	
	
	//
	private String totalExtraItem;
	private String totalAdditional;
	private String totalMiscellaneous;
	private String totalServices;
	private String totalAbstractvalue;
	private String constructionValue;
	Long selectCompanyid = Long.valueOf(UI.getCurrent().getSession()
			.getAttribute("loginCompanyId").toString());
	
	public String getSignature() {
		return beanBankconst.getBankConstantList("SIGNATURE",selectCompanyid).get(0);
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public TPemCmBillDtls getBill() {
		return bill;
	}
	public void setBill(TPemCmBillDtls bill) {
		this.bill = bill;
	}
	public TPemCmEvalDetails getEvalDtls() {
		return evalDtls;
	}
	public void setEvalDtls(TPemCmEvalDetails evalDtls) {
		this.evalDtls = evalDtls;
	}
	public List<TPemCmOwnerDetails> getCustomer() {
		return customer;
	}
	public void setCustomer(List<TPemCmOwnerDetails> customer) {
		this.customer = customer;
	}
	public List<TPemCmAssetDetails> getAssetDtls() {
		return assetDtls;
	}
	public void setAssetDtls(List<TPemCmAssetDetails> assetDtls) {
		this.assetDtls = assetDtls;
	}
	public List<TPemCmPropDocDetails> getDocument() {
		return document;
	}
	public void setDocument(List<TPemCmPropDocDetails> document) {
		this.document = document;
	}
	public List<TPemCmPropLegalDocs> getLegalDoc() {
		return legalDoc;
	}
	public void setLegalDoc(List<TPemCmPropLegalDocs> legalDoc) {
		this.legalDoc = legalDoc;
	}
	public List<TPemCmPropAdjoinDtls> getAdjoinProperty() {
		return adjoinProperty;
	}
	public void setAdjoinProperty(List<TPemCmPropAdjoinDtls> adjoinProperty) {
		this.adjoinProperty = adjoinProperty;
	}
	public List<TPemCmPropDimension> getDimension() {
		return dimension;
	}
	public void setDimension(List<TPemCmPropDimension> dimension) {
		this.dimension = dimension;
	}
	public List<TPemCmBldngValutnSummry> getValuationDtls() {
		return valuationDtls;
	}
	public void setValuationDtls(List<TPemCmBldngValutnSummry> valuationDtls) {
		this.valuationDtls = valuationDtls;
	}
	public List<TPemCmPropValtnSummry> getPropertyValue() {
		return propertyValue;
	}
	public void setPropertyValue(List<TPemCmPropValtnSummry> propertyValue) {
		this.propertyValue = propertyValue;
	}
	public List<TPemCmPropValtnSummry> getPropertyValue1() {
		return propertyValue1;
	}
	public void setPropertyValue1(List<TPemCmPropValtnSummry> propertyValue1) {
		this.propertyValue1 = propertyValue1;
	}
	public List<TPemCmPropValtnSummry> getPropertyValue2() {
		return propertyValue2;
	}
	public void setPropertyValue2(List<TPemCmPropValtnSummry> propertyValue2) {
		this.propertyValue2 = propertyValue2;
	}
	public List<TPemCmAssetDetails> getAssetDtls1() {
		return assetDtls1;
	}
	public void setAssetDtls1(List<TPemCmAssetDetails> assetDtls1) {
		this.assetDtls1 = assetDtls1;
	}
	public List<TPemCmAssetDetails> getAssetDtls2() {
		return assetDtls2;
	}
	public void setAssetDtls2(List<TPemCmAssetDetails> assetDtls2) {
		this.assetDtls2 = assetDtls2;
	}
	public List<TPemCmBldngMiscData> getMiscell() {
		return miscell;
	}
	public void setMiscell(List<TPemCmBldngMiscData> miscell) {
		this.miscell = miscell;
	}
	public List<TPemCmBldngService> getService() {
		return service;
	}
	public void setService(List<TPemCmBldngService> service) {
		this.service = service;
	}
	public List<TPemCmPropImage> getPropImage() {
		return propImage;
	}
	public void setPropImage(List<TPemCmPropImage> propImage) {
		this.propImage = propImage;
	}
	public List<TPemCmPropLegalDocs> getLegalDoc1() {
		return legalDoc1;
	}
	public void setLegalDoc1(List<TPemCmPropLegalDocs> legalDoc1) {
		this.legalDoc1 = legalDoc1;
	}
	public List<TPemCmPropLegalDocs> getLegalDoc2() {
		return legalDoc2;
	}
	public void setLegalDoc2(List<TPemCmPropLegalDocs> legalDoc2) {
		this.legalDoc2 = legalDoc2;
	}
	public List<TPemSynPropAreaDtls> getAreaDetails() {
		return areaDetails;
	}
	public void setAreaDetails(List<TPemSynPropAreaDtls> areaDetails) {
		this.areaDetails = areaDetails;
	}
	public List<TPemSynPropAreaDtls> getAreaDetails1() {
		return areaDetails1;
	}
	public void setAreaDetails1(List<TPemSynPropAreaDtls> areaDetails1) {
		this.areaDetails1 = areaDetails1;
	}
	public List<MPemCmBank> getBank() {
		return bank;
	}
	public void setBank(List<MPemCmBank> bank) {
		this.bank = bank;
	}
	public List<TPemCmBldngTechDetails> getBuildingDtls() {
		return buildingDtls;
	}
	public void setBuildingDtls(List<TPemCmBldngTechDetails> buildingDtls) {
		this.buildingDtls = buildingDtls;
	}
	public List<TPemCmBldngTechDetails> getBuildingDtls1() {
		return buildingDtls1;
	}
	public void setBuildingDtls1(List<TPemCmBldngTechDetails> buildingDtls1) {
		this.buildingDtls1 = buildingDtls1;
	}
	public List<TPemCmBldngOldSpec> getBuildSpec() {
		return buildSpec;
	}
	public void setBuildSpec(List<TPemCmBldngOldSpec> buildSpec) {
		this.buildSpec = buildSpec;
	}
	public List<TPemCmBldngNewSpec> getBuildSpec2() {
		return buildSpec2;
	}
	public void setBuildSpec2(List<TPemCmBldngNewSpec> buildSpec2) {
		this.buildSpec2 = buildSpec2;
	}
	public List<TPemCmBldngValutnSummry> getBuildValuation() {
		return buildValuation;
	}
	public void setBuildValuation(List<TPemCmBldngValutnSummry> buildValuation) {
		this.buildValuation = buildValuation;
	}
	public List<TPemCmBldngRiskDtls> getEarthQuake() {
		return earthQuake;
	}
	public void setEarthQuake(List<TPemCmBldngRiskDtls> earthQuake) {
		this.earthQuake = earthQuake;
	}
	public List<TPemSynPropFloor> getFloor() {
		return floor;
	}
	public void setFloor(List<TPemSynPropFloor> floor) {
		this.floor = floor;
	}
	public List<TPemSynPropFloor> getFloor1() {
		return floor1;
	}
	public void setFloor1(List<TPemSynPropFloor> floor1) {
		this.floor1 = floor1;
	}
	
	public List<TPemCmBldngAditnlItms> getAddtionalItms() {
		return addtionalItms;
	}
	public void setAddtionalItms(List<TPemCmBldngAditnlItms> addtionalItms) {
		this.addtionalItms = addtionalItms;
	}
	public List<TPemCmLandValutnData> getLandval() {
		return landval;
	}
	public void setLandval(List<TPemCmLandValutnData> landval) {
		this.landval = landval;
	}
	public List<TPemSydPropMatchBoundry> getBoundary() {
		return boundary;
	}
	public void setBoundary(List<TPemSydPropMatchBoundry> boundary) {
		this.boundary = boundary;
	}
	public List<TPemSydPropMatchBoundry> getBoundary1() {
		return boundary1;
	}
	public void setBoundary1(List<TPemSydPropMatchBoundry> boundary1) {
		this.boundary1 = boundary1;
	}
	public List<TPemSbiPropChartrstic> getPropertyConstruct() {
		return propertyConstruct;
	}
	public void setPropertyConstruct(List<TPemSbiPropChartrstic> propertyConstruct) {
		this.propertyConstruct = propertyConstruct;
	}
	public List<TPemSbiPropChartrstic> getPropertyConstruct1() {
		return propertyConstruct1;
	}
	public void setPropertyConstruct1(List<TPemSbiPropChartrstic> propertyConstruct1) {
		this.propertyConstruct1 = propertyConstruct1;
	}
	public List<TPemSbiPropChartrstic> getPropertyConstruct2() {
		return propertyConstruct2;
	}
	public void setPropertyConstruct2(List<TPemSbiPropChartrstic> propertyConstruct2) {
		this.propertyConstruct2 = propertyConstruct2;
	}
	public List<TPemSbiPropChartrstic> getPropertyConstruct3() {
		return propertyConstruct3;
	}
	public void setPropertyConstruct3(List<TPemSbiPropChartrstic> propertyConstruct3) {
		this.propertyConstruct3 = propertyConstruct3;
	}
	public List<TPemSbiPropChartrstic> getPropertyConstruct4() {
		return propertyConstruct4;
	}
	public void setPropertyConstruct4(List<TPemSbiPropChartrstic> propertyConstruct4) {
		this.propertyConstruct4 = propertyConstruct4;
	}
	public List<TPemSbiPropChartrstic> getPropertyConstruct5() {
		return propertyConstruct5;
	}
	public void setPropertyConstruct5(List<TPemSbiPropChartrstic> propertyConstruct5) {
		this.propertyConstruct5 = propertyConstruct5;
	}
	public List<TPemSynPropOccupancy> getPropertyOccupancy() {
		return propertyOccupancy;
	}
	public void setPropertyOccupancy(List<TPemSynPropOccupancy> propertyOccupancy) {
		this.propertyOccupancy = propertyOccupancy;
	}
	public List<TPemSynPropOccupancy> getPropertyOccupancy1() {
		return propertyOccupancy1;
	}
	public void setPropertyOccupancy1(List<TPemSynPropOccupancy> propertyOccupancy1) {
		this.propertyOccupancy1 = propertyOccupancy1;
	}
	public List<TPemSynPropOccupancy> getPropertyOccupancy2() {
		return propertyOccupancy2;
	}
	public void setPropertyOccupancy2(List<TPemSynPropOccupancy> propertyOccupancy2) {
		this.propertyOccupancy2 = propertyOccupancy2;
	}
	public List<TPemSynPropOccupancy> getPropertyOccupancy3() {
		return propertyOccupancy3;
	}
	public void setPropertyOccupancy3(List<TPemSynPropOccupancy> propertyOccupancy3) {
		this.propertyOccupancy3 = propertyOccupancy3;
	}
	public List<TPemSynPropOccupancy> getPropertyOccupancy4() {
		return propertyOccupancy4;
	}
	public void setPropertyOccupancy4(List<TPemSynPropOccupancy> propertyOccupancy4) {
		this.propertyOccupancy4 = propertyOccupancy4;
	}
	public List<TPemSynPropViolation> getPropertyViolation() {
		return propertyViolation;
	}
	public void setPropertyViolation(List<TPemSynPropViolation> propertyViolation) {
		this.propertyViolation = propertyViolation;
	}
	public List<TPemSynBldngRoom> getRoom() {
		return room;
	}
	public void setRoom(List<TPemSynBldngRoom> room) {
		this.room = room;
	}
	public List<TPemCmPropGuidlnRefdata> getGuidelineref() {
		return guidelineref;
	}
	public void setGuidelineref(List<TPemCmPropGuidlnRefdata> guidelineref) {
		this.guidelineref = guidelineref;
	}
	public List<TPemSbiBldngCnstructnDtls> getDetailsBuilding() {
		return detailsBuilding;
	}
	public void setDetailsBuilding(List<TPemSbiBldngCnstructnDtls> detailsBuilding) {
		this.detailsBuilding = detailsBuilding;
	}
	public List<TPemSbiBldngCnstructnDtls> getDetailsBuilding1() {
		return detailsBuilding1;
	}
	public void setDetailsBuilding1(List<TPemSbiBldngCnstructnDtls> detailsBuilding1) {
		this.detailsBuilding1 = detailsBuilding1;
	}
	public List<TPemSbiBldngCnstructnDtls> getDetailsBuilding2() {
		return detailsBuilding2;
	}
	public void setDetailsBuilding2(List<TPemSbiBldngCnstructnDtls> detailsBuilding2) {
		this.detailsBuilding2 = detailsBuilding2;
	}
	public List<TPemCmBldngOldPlinthArea> getPlinthArea() {
		return plinthArea;
	}
	public void setPlinthArea(List<TPemCmBldngOldPlinthArea> plinthArea) {
		this.plinthArea = plinthArea;
	}
	public List<TPemCmBldngNewPlinthArea> getPlinthArea1() {
		return plinthArea1;
	}
	public void setPlinthArea1(List<TPemCmBldngNewPlinthArea> plinthArea1) {
		this.plinthArea1 = plinthArea1;
	}
	public List<TPemCmFlatUnderValutn> getFlatValuation() {
		return flatValuation;
	}
	public void setFlatValuation(List<TPemCmFlatUnderValutn> flatValuation) {
		this.flatValuation = flatValuation;
	}
	public List<TPemCmPropOldPlanApprvl> getPlanApproval() {
		return planApproval;
	}
	public void setPlanApproval(List<TPemCmPropOldPlanApprvl> planApproval) {
		this.planApproval = planApproval;
	}
	public List<TPemCmPropOldPlanApprvl> getPlanApproval1() {
		return planApproval1;
	}
	public void setPlanApproval1(List<TPemCmPropOldPlanApprvl> planApproval1) {
		this.planApproval1 = planApproval1;
	}
	public List<TPemCmPropNewPlanApprvl> getPlanApproval2() {
		return planApproval2;
	}
	public void setPlanApproval2(List<TPemCmPropNewPlanApprvl> planApproval2) {
		this.planApproval2 = planApproval2;
	}
	public List<TPemCmPropNewPlanApprvl> getPlanApproval3() {
		return planApproval3;
	}
	public void setPlanApproval3(List<TPemCmPropNewPlanApprvl> planApproval3) {
		this.planApproval3 = planApproval3;
	}
	public List<TPemCmBldngCostofcnstructn> getConstValuation() {
		return constValuation;
	}
	public void setConstValuation(List<TPemCmBldngCostofcnstructn> constValuation) {
		this.constValuation = constValuation;
	}
	public List<TPemCmBldngExtraItems> getExtraItem() {
		return extraItem;
	}
	public void setExtraItem(List<TPemCmBldngExtraItems> extraItem) {
		this.extraItem = extraItem;
	}
	public List<TPemCmPropGuidlnValue> getGuideline() {
		return guideline;
	}
	public void setGuideline(List<TPemCmPropGuidlnValue> guideline) {
		this.guideline = guideline;
	}
	public List<TPemCmPropGuidlnRefdata> getGuideRef() {
		return guideRef;
	}
	public void setGuideRef(List<TPemCmPropGuidlnRefdata> guideRef) {
		this.guideRef = guideRef;
	}
	public List<TPemCmPropApplcntEstmate> getApplicantEstimate() {
		return applicantEstimate;
	}
	public void setApplicantEstimate(
			List<TPemCmPropApplcntEstmate> applicantEstimate) {
		this.applicantEstimate = applicantEstimate;
	}
	public List<TPemCmPropRsnbleEstmate> getApplicantReason() {
		return applicantReason;
	}
	public void setApplicantReason(List<TPemCmPropRsnbleEstmate> applicantReason) {
		this.applicantReason = applicantReason;
	}
	public List<TPemCmPropGuidlnValue> getGuideline1() {
		return guideline1;
	}
	public void setGuideline1(List<TPemCmPropGuidlnValue> guideline1) {
		this.guideline1 = guideline1;
	}
	public List<TPemCmPropGuidlnValue> getGuideline2() {
		return guideline2;
	}
	public void setGuideline2(List<TPemCmPropGuidlnValue> guideline2) {
		this.guideline2 = guideline2;
	}
	public String getPropertyAddress() {
		return propertyAddress;
	}
	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}
	public String getCustomername() {
		return customername;
	}
	public void setCustomername(String customername) {
		this.customername = customername;
	}
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	public String getInspectionDate() {
		return inspectionDate;
	}
	public void setInspectionDate(String inspectionDate) {
		this.inspectionDate = inspectionDate;
	}
	public String getValuationDate() {
		return valuationDate;
	}
	public void setValuationDate(String valuationDate) {
		this.valuationDate = valuationDate;
	}
	public String getValuationPurpose() {
		return valuationPurpose;
	}
	public void setValuationPurpose(String valuationPurpose) {
		this.valuationPurpose = valuationPurpose;
	}
	public String getPropDesc() {
		return propDesc;
	}
	public void setPropDesc(String propDesc) {
		this.propDesc = propDesc;
	}
	public String getMarketValue() {
		return marketValue;
	}
	public void setMarketValue(String marketValue) {
		this.marketValue = marketValue;
	}
	public String getGuidelinevalue() {
		return guidelinevalue;
	}
	public void setGuidelinevalue(String guidelinevalue) {
		this.guidelinevalue = guidelinevalue;
	}
	public String getRealizablevalue() {
		return realizablevalue;
	}
	public void setRealizablevalue(String realizablevalue) {
		this.realizablevalue = realizablevalue;
	}
	public String getDistressvalue() {
		return distressvalue;
	}
	public void setDistressvalue(String distressvalue) {
		this.distressvalue = distressvalue;
	}
	public String getAmountInWords() {
		return amountInWords;
	}
	public void setAmountInWords(String amountInWords) {
		this.amountInWords = amountInWords;
	}
	public String getAmountWordsGuideline() {
		return amountWordsGuideline;
	}
	public void setAmountWordsGuideline(String amountWordsGuideline) {
		this.amountWordsGuideline = amountWordsGuideline;
	}
	public String getGuideland() {
		return guideland;
	}
	public void setGuideland(String guideland) {
		this.guideland = guideland;
	}
	public String getTotalFairMarket() {
		return totalFairMarket;
	}
	public void setTotalFairMarket(String totalFairMarket) {
		this.totalFairMarket = totalFairMarket;
	}
	public String getTotalRealizable() {
		return totalRealizable;
	}
	public void setTotalRealizable(String totalRealizable) {
		this.totalRealizable = totalRealizable;
	}
	public String getTotalDistress() {
		return totalDistress;
	}
	public void setTotalDistress(String totalDistress) {
		this.totalDistress = totalDistress;
	}
	public String getTotalGuideline() {
		return totalGuideline;
	}
	public void setTotalGuideline(String totalGuideline) {
		this.totalGuideline = totalGuideline;
	}
	public String getTotalValuation() {
		return totalValuation;
	}
	public void setTotalValuation(String totalValuation) {
		this.totalValuation = totalValuation;
	}
	public String getEvalnumber() {
		return evalnumber;
	}
	public void setEvalnumber(String evalnumber) {
		this.evalnumber = evalnumber;
	}
	public String getTotalExtraItem() {
		return totalExtraItem;
	}
	public void setTotalExtraItem(String totalExtraItem) {
		this.totalExtraItem = totalExtraItem;
	}
	public String getTotalAdditional() {
		return totalAdditional;
	}
	public void setTotalAdditional(String totalAdditional) {
		this.totalAdditional = totalAdditional;
	}
	public String getTotalMiscellaneous() {
		return totalMiscellaneous;
	}
	public void setTotalMiscellaneous(String totalMiscellaneous) {
		this.totalMiscellaneous = totalMiscellaneous;
	}
	public String getTotalServices() {
		return totalServices;
	}
	public void setTotalServices(String totalServices) {
		this.totalServices = totalServices;
	}
	public String getTotalAbstractvalue() {
		return totalAbstractvalue;
	}
	public void setTotalAbstractvalue(String totalAbstractvalue) {
		this.totalAbstractvalue = totalAbstractvalue;
	}
	public List<TPemCmBldngStgofcnstructn> getStgofConstn() {
		return stgofConstn;
	}
	public void setStgofConstn(List<TPemCmBldngStgofcnstructn> stgofConstn) {
		this.stgofConstn = stgofConstn;
	}
	public String getConstructionValue() {
		return constructionValue;
	}
	public void setConstructionValue(String constructionValue) {
		this.constructionValue = constructionValue;
	}
	public List<TPemCmBldngRoofHght> getRoofHeight() {
		return roofHeight;
	}
	public void setRoofHeight(List<TPemCmBldngRoofHght> roofHeight) {
		this.roofHeight = roofHeight;
	}
	public List<TPemCmPropDescription> getPropertyDescription() {
		return propertyDescription;
	}
	public void setPropertyDescription(
			List<TPemCmPropDescription> propertyDescription) {
		this.propertyDescription = propertyDescription;
	}
	public List<TPemCmPropDescription> getPropertyDescription1() {
		return propertyDescription1;
	}
	public void setPropertyDescription1(
			List<TPemCmPropDescription> propertyDescription1) {
		this.propertyDescription1 = propertyDescription1;
	}
	public List<TPemCmPropDescription> getPropertyDescription2() {
		return propertyDescription2;
	}
	public void setPropertyDescription2(
			List<TPemCmPropDescription> propertyDescription2) {
		this.propertyDescription2 = propertyDescription2;
	}
	public List<TPemCmPropDescription> getPropertyDescription3() {
		return propertyDescription3;
	}
	public void setPropertyDescription3(
			List<TPemCmPropDescription> propertyDescription3) {
		this.propertyDescription3 = propertyDescription3;
	}
	public List<TPemCmPropDescription> getPropertyDescription4() {
		return propertyDescription4;
	}
	public void setPropertyDescription4(
			List<TPemCmPropDescription> propertyDescription4) {
		this.propertyDescription4 = propertyDescription4;
	}
	public List<TPemCmPropDescription> getPropertyDescription5() {
		return propertyDescription5;
	}
	public void setPropertyDescription5(List<TPemCmPropDescription> propertyDescription5) {
		this.propertyDescription5 = propertyDescription5;
	}
	public List<TPemCmBldngStgofcnstructn> getStgofConstn1() {
		return stgofConstn1;
	}
	public void setStgofConstn1(List<TPemCmBldngStgofcnstructn> stgofConstn1) {
		this.stgofConstn1 = stgofConstn1;
	}
	public List<TPemSbiBldngPlumpInstltn> getPlumInstln() {
		return plumInstln;
	}
	public void setPlumInstln(List<TPemSbiBldngPlumpInstltn> plumInstln) {
		this.plumInstln = plumInstln;
	}
	public List<TPemSbiBldngElctrcInstltn> getElecInstln() {
		return elecInstln;
	}
	public void setElecInstln(List<TPemSbiBldngElctrcInstltn> elecInstln) {
		this.elecInstln = elecInstln;
	}
	public List<CommissionSetupDM> getBillDtls() {
		return billDtls;
	}
	public void setBillDtls(List<CommissionSetupDM> billDtls) {
		this.billDtls = billDtls;
	}
	public String getBillDate() {
		return billDate;
	}
	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}
	
	
	
}
