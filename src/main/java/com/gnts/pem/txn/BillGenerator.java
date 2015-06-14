package com.gnts.pem.txn;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.gnts.erputil.Common;
import com.gnts.erputil.helper.SpringContextHelper;
import com.gnts.pem.domain.mst.CommissionSetupDM;
import com.gnts.pem.domain.rpt.TPemCmBillDtls;
import com.gnts.pem.domain.txn.common.TPemCmEvalDetails;
import com.gnts.pem.service.mst.CmBankConstantService;
import com.gnts.pem.service.mst.CommissionSetupService;
import com.gnts.pem.service.rpt.CmBillDtlsService;
import com.gnts.pem.service.txn.common.CmEvalDetailsService;

public class BillGenerator {
	private static CmBankConstantService beanBankConst = (CmBankConstantService) SpringContextHelper
			.getBean("bankConstant");
	private static CmEvalDetailsService beanEvaluation = (CmEvalDetailsService) SpringContextHelper
			.getBean("evalDtls");
	private static CmBillDtlsService beanBill=(CmBillDtlsService) SpringContextHelper
			.getBean("billDtls");
	private  static CommissionSetupService beanCmSetup=(CommissionSetupService) SpringContextHelper
			.getBean("commonsetup");
	private static String st1,sc1,sehc1;
	static Double doublesc,doublest,doublesehc;
	public static TPemCmBillDtls getBillDetails(Double recvAmount,Double discount,Long headerid,String username,Long bankid,Long companyid,Long currencyId) {
		System.out.println("Inside Bill=======================");
		List <CommissionSetupDM> endlist1=new ArrayList<CommissionSetupDM>();
		TPemCmEvalDetails docId=new TPemCmEvalDetails();
		docId.setDocId(headerid);
		TPemCmBillDtls obj=new TPemCmBillDtls();
		
		obj.setDocId(docId);
		//get st,sc,sehc value
		List<String> list = beanBankConst.getBankConstantList("ST_PERCENT",companyid);
		st1=list.get(0);
		doublest=Double.parseDouble(st1);
		List<String> list1 = beanBankConst.getBankConstantList("SC_PERCENT",companyid);
		sc1=list1.get(0);
		doublesc=Double.parseDouble(sc1);
		List<String> list2= beanBankConst.getBankConstantList("SEHC_PERCENT",companyid);
		sehc1=list2.get(0);
		doublesehc=Double.parseDouble(sehc1);
		obj.setStPrcnt(Long.parseLong(st1));
		obj.setScPrcnt(Long.parseLong(sc1));
		obj.setSehcPrcnt(Long.parseLong(sehc1));
		
		DecimalFormat df = new DecimalFormat("#.00"); 
		Date date=new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		obj.setBillDate(new Date());
		try{
		Double amount = recvAmount;
		obj.setPropertyValue(new BigDecimal(df.format(amount)));
		Double basicvalue=0.0, totals,round,discntVal,disntBillVal = 0.0;
		Double service, sc, sehc;
		Long roundOff;
	//	int i=0;
		Double amount1=amount;
		discntVal=discount;
		String orderby="endValue";
		//get Endvalue and percent from Common Setup
		List <CommissionSetupDM> endlist=beanCmSetup.getCommonSetupList(null, bankid, companyid,null,orderby);
		for(CommissionSetupDM comn:endlist){
			CommissionSetupDM setup=new CommissionSetupDM();
			if (amount1 > comn.getEndValue()) {
				amount1=amount1-comn.getEndValue();
				Double total = (double) (comn.getEndValue() / (comn.getCommPercent() * 100));  
				setup.setEndValue(comn.getEndValue());
				setup.setCommPercent(comn.getCommPercent());
				setup.setStartValue(total);
				basicvalue=basicvalue+setup.getStartValue();
			}
			else{
				amount1 = (amount1 / (comn.getCommPercent() * 100));  
				setup.setEndValue(comn.getEndValue());
				setup.setCommPercent(comn.getCommPercent());
				setup.setStartValue(amount1);
				endlist1.add(setup);
				basicvalue=basicvalue+setup.getStartValue();
				break;
			}
			obj.setBasicBillValue(new BigDecimal(df.format(basicvalue)));
			disntBillVal=basicvalue-discntVal;
			obj.setDiscntValue(new BigDecimal(df.format(discntVal)));
			obj.setDiscntBillValue(new BigDecimal(df.format(disntBillVal)));
			
			endlist1.add(setup);
			}
			
			service = (disntBillVal * doublest) / 100;
			obj.setStValue(new BigDecimal(df.format(service)));
			sc = (service * doublesc) / 100;
			obj.setScValue(new BigDecimal(df.format(sc)));
			sehc = (service * doublesehc) / 100;
			obj.setSehcValue(new BigDecimal(df.format(sehc)));
			totals = service + sc + sehc;
						round = disntBillVal + totals;

			roundOff = Math.round(round.doubleValue());
			obj.setPaymentAmount(new BigDecimal(df.format(round)));
			obj.setBalanceAmount(new BigDecimal(df.format(roundOff)));
			obj.setPaymentStatus(Common.DOC_PENDING);
			obj.setBillValue(new BigDecimal(df.format(roundOff)));
			obj.setLastUpdatedDt(new Date());
			obj.setLastUpdatedBy(username);
			obj.setCcyId(currencyId);
			//beanBill.saveBillDtls(obj);
			obj.setAmountinwords(beanEvaluation.getAmountInWords(String.valueOf(roundOff)));
		//	beanSlno.upadateSlnoGeneration(companyid, ref);
			System.out.println("Bill No"+obj.getBillNo());
			System.out.println("Bill Date"+obj.getBillDate());
			System.out.println("Balance Amount"+obj.getBalanceAmount());
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		return obj;
	}
	
	//For End value and Common percent
	public static List<CommissionSetupDM> getEndValueDetails(Double recvAmount,Long headerid,String username,Long bankid,Long companyid) {
		System.out.println("Inside Common Setup++++++++++++++++++++++++++");
		Double amount = recvAmount;
		String orderby="endValue";
		DecimalFormat df = new DecimalFormat("#.00");
		List <CommissionSetupDM> endlist=beanCmSetup.getCommonSetupList(null, bankid, companyid,null,orderby);
		List <CommissionSetupDM> endlist1=new ArrayList<CommissionSetupDM>();
		Double amount1=amount;
		Double  totals=0.0;
		for(CommissionSetupDM comn:endlist){
			CommissionSetupDM setup=new CommissionSetupDM();
			if (amount1 > comn.getEndValue()) {
				amount1=amount1-comn.getEndValue();
				Double total = (double) (comn.getEndValue() / (comn.getCommPercent() * 100));  
				setup.setEndValue(comn.getEndValue());
				setup.setCommPercent(comn.getCommPercent());
				setup.setStartValue(Double.valueOf(df.format(total)));
				totals=totals+setup.getStartValue();
			
			}
			else{
			
				amount1 = (amount1 / (comn.getCommPercent() * 100));  
				setup.setEndValue(comn.getEndValue());
				setup.setCommPercent(comn.getCommPercent());
				setup.setStartValue(Double.valueOf(df.format(amount1)));
				endlist1.add(setup);
				totals=totals+setup.getStartValue();
				break;
			}
			endlist1.add(setup);
			}
	
	
	return endlist1;
			}
	
	
}
