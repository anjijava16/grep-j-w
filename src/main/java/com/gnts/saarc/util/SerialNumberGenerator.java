/**
 * File Name	:	SerialNumberGenerator.java
 * Description	:	SAARC - Serial Key Genertion
 * Author		:	SOUNDARC
 * Date			:	May 02,2015
 * Modification 
 * Modified By  :   
 * Description	:
 *
 *  Copyright (C) 2014 GNTS Technologies pvt. ltd. * All rights reserved.
 *
 * This software is the confidential and proprietary information of  GNTS Technologies pvt. ltd.
 * Version      Date           		Modified By               Remarks
 * 0.1          May 02,2015        	SOUNDARC	        	  Initial Version

 */
package com.gnts.saarc.util;

import org.apache.log4j.Logger;
import com.gnts.base.domain.mst.SlnoGenDM;
import com.gnts.base.service.mst.SlnoGenService;
import com.gnts.crm.domain.mst.ClientDM;
import com.gnts.crm.service.mst.ClientService;
import com.gnts.erputil.helper.SpringContextHelper;

public class SerialNumberGenerator {
	private static SlnoGenService serviceSlnogen = (SlnoGenService) SpringContextHelper.getBean("slnogen");
	private static ClientService serviceClients = (ClientService) SpringContextHelper.getBean("clients");
	// Initialize the logger
	private static Logger logger = Logger.getLogger(SerialNumberGenerator.class);
	
	public static String generateEnquiryNumber(Long companyid, Long branchid, Long moduleid, String refkey,
			Long clientid) {
		String serialnumber = "";
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "SM_ENQRYNO").get(0);
			System.out.println("slnoObj---->" + slnoObj);
			logger.info("Serial No Generation  Data...===>" + companyid + "," + branchid + "," + moduleid);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				ClientDM clientobj = serviceClients.getClientDetails(companyid, clientid, null, null, null, null, null,
						null, null, "P").get(0);
				serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + clientobj.getClientCode()
						+ slnoObj.getPrefixCncat() + slnoObj.getSuffixKey() + slnoObj.getSuffixCncat()
						+ slnoObj.getCurrSeqNo();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("serialnumber--->" + serialnumber);
		return serialnumber;
	}
	
/*	public static String generateVendorCode(Long companyid, Long branchid, String vendortype, String refkey,
			String vendorname) {
		String serialnumber = "";
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "BS_VNDRCD").get(0);
			logger.info("Serial No Generation  Data...===>" + companyid + "," + branchid + "," + vendortype);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + vendortype
						+ slnoObj.getPrefixCncat() + slnoObj.getSuffixKey() + slnoObj.getSuffixCncat()
						+ slnoObj.getCurrSeqNo();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("serialnumber--->" + serialnumber);
		return serialnumber;
	}*/
	
	public static String generateseralcallNumber(Long companyid, Long branchid, Long moduleid, String refkey,
			Long clientid) {
		String serialnumber = "";
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "SM_CALLFORM").get(0);
			logger.info("Serial No Generation  Data...===>" + companyid + "," + branchid + "," + moduleid);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				ClientDM clientobj = serviceClients.getClientDetails(companyid, clientid, null, null, null, null, null,
						null, null, "P").get(0);
				serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + clientobj.getClientCode()
						+ slnoObj.getPrefixCncat() + slnoObj.getSuffixKey() + slnoObj.getSuffixCncat()
						+ slnoObj.getCurrSeqNo();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("serialnumber--->" + serialnumber);
		return serialnumber;
	}
	
	public static String generateSNoMMSPO(Long companyid, Long branchid, Long moduleid, String refkey) {
		String serialnumber = "";
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "MM_NPONO").get(0);
			logger.info("Serial No Generation  Data...===>" + companyid + "," + branchid + "," + moduleid);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + "PO-" + slnoObj.getCurrSeqNo()
						+ slnoObj.getPrefixCncat() + slnoObj.getSuffixKey();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("serialnumber--->" + serialnumber);
		return serialnumber;
	}
	public static String generateSNoLOI(Long companyid, Long branchid, Long moduleid, String refkey) {
		String serialnumber = "";
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "MMS_LOI").get(0);
			logger.info("LOI Serial No Generation ===>" + companyid + "," + branchid + "," + moduleid);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + "LOI-" + slnoObj.getCurrSeqNo()
						+ slnoObj.getPrefixCncat() + slnoObj.getSuffixKey();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("serialnumber--->" + serialnumber);
		return serialnumber;
	}
	public static String generateSNoVEN(Long companyid, Long branchid, Long moduleid, String refkey,String venCode) {
		String serialnumber = "";
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "BS_VNDRCD").get(0);
			logger.info("LOI Serial No Generation ===>" + companyid + "," + branchid + "," + moduleid);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + venCode
						+ slnoObj.getPrefixCncat() + slnoObj.getSuffixKey();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("serialnumber--->" + serialnumber);
		return serialnumber;
	}
}
