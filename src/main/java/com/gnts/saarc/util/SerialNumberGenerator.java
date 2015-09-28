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
	
	public static String generateSNoEnqNo(Long companyid, Long branchid, Long moduleid, String refkey) {
		String serialnumber = "";
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "MM_ENQRYNO").get(0);
			logger.info("Serial No Generation  Data...===>" + companyid + "," + branchid + "," + moduleid);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + "EQ-" + slnoObj.getCurrSeqNo()
						+ slnoObj.getPrefixCncat() + slnoObj.getSuffixKey();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("serialnumber--->" + serialnumber);
		return serialnumber;
	}
	
	public static String generateSNoVEN(Long companyid, Long branchid, Long moduleid, String refkey, String venCode) {
		String serialnumber = "";
		try {
			SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "BS_VNDRCD").get(0);
			logger.info("LOI Serial No Generation ===>" + companyid + "," + branchid + "," + moduleid);
			if (slnoObj.getAutoGenYN().equals("Y")) {
				serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + venCode + slnoObj.getPrefixCncat()
						+ String.format("03d", slnoObj.getCurrSeqNo().intValue());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("serialnumber--->" + serialnumber);
		return serialnumber;
	}
	
	public static String generateIndNo(Long companyid, Long branchid, Long moduleid, String refkey, String indType) {
		String serialnumber = "";
		try {
			if (indType == "P") {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "MM_INDNOP").get(0);
				logger.info("LOI Serial No Generation ===>" + companyid + "," + branchid + "," + moduleid);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + indType
							+ slnoObj.getPrefixCncat() + slnoObj.getSuffixKey() + "/" + slnoObj.getCurrSeqNo();
				}
			} else {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "MM_INDNO").get(0);
				logger.info("LOI Serial No Generation ===>" + companyid + "," + branchid + "," + moduleid);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + indType
							+ slnoObj.getPrefixCncat() + slnoObj.getSuffixKey() + "/" + slnoObj.getCurrSeqNo();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("serialnumber--->" + serialnumber);
		return serialnumber;
	}
	
	public static String generateDCNo(Long companyid, Long branchid, Long moduleid, String refkey, String dcType,
			String dcTypeRNR) {
		String serialnumber = "";
		try {
			if (dcType.equals("M")) {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "MM_DCNO").get(0);
				logger.info("LOI Serial No Generation ===>" + companyid + "," + branchid + "," + moduleid);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + dcType + "/" + dcTypeRNR
							+ slnoObj.getSuffixCncat() + slnoObj.getCurrSeqNo();
				}
			} else if (dcType.equals("S")) {
				SlnoGenDM slnoObj = serviceSlnogen.getSequenceNumber(companyid, null, null, "MM_DCNOS").get(0);
				logger.info("LOI Serial No Generation ===>" + companyid + "," + branchid + "," + moduleid);
				if (slnoObj.getAutoGenYN().equals("Y")) {
					serialnumber = slnoObj.getPrefixKey() + slnoObj.getPrefixCncat() + dcType + "/" + dcTypeRNR
							+ slnoObj.getSuffixCncat() + slnoObj.getCurrSeqNo();
				}
			}
			dcType = "";
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("serialnumber--->" + serialnumber);
		return serialnumber;
	}
}
