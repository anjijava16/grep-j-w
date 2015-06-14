package com.gnts.pem.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import com.vaadin.server.VaadinService;


public class XMLUtil{

	static String basepath = VaadinService.getCurrent()
          .getBaseDirectory().getAbsolutePath();
	public static ByteArrayOutputStream doMarshall(UIFlowData uiFlowData){
		ByteArrayOutputStream bout =  null;
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(UIFlowData.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			bout = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(uiFlowData, bout);
			jaxbMarshaller.marshal(uiFlowData, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bout;
	}
	
	public static  void getWordDocument(ByteArrayOutputStream recvstram,String filename,String xslfilename)
			throws TransformerConfigurationException, TransformerException,
			TransformerFactoryConfigurationError {
		System.out.println("Receive Stream-"+recvstram);
		System.out.println("basepath inputtext"+basepath +"/WEB-INF/PEM-XSL/input.txt");
		try {
			byte[] myBytes = recvstram.toByteArray();
			FileOutputStream out = new FileOutputStream(basepath +"/WEB-INF/PEM-XSL/input.txt");
			try {
				out.write(myBytes);
			} finally {
				out.close();
			}
			(TransformerFactory.newInstance().newTransformer(new StreamSource(new File(basepath +"/WEB-INF/PEM-XSL/"+xslfilename)))).
			transform(new StreamSource(new File(basepath + "/WEB-INF/PEM-XSL/input.txt")),
			new StreamResult(new File(basepath+"/WEB-INF/PEM-DOCS/"+filename+".doc")));
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static  void getWordDocument1(ByteArrayOutputStream recvstram,String filename,String xslfilename)
			throws TransformerConfigurationException, TransformerException,
			TransformerFactoryConfigurationError {

		try {
	    	  
	    	  byte[] myBytes = recvstram.toByteArray();
	     	 FileOutputStream out = new FileOutputStream("g:/input.txt"); 
	     	 try {  
	 		     out.write(myBytes);  
	 		 } finally {  
	 		     out.close();  
	 		 } 
	 		 (TransformerFactory.newInstance().newTransformer(new StreamSource(new File("g:/"+xslfilename)))).
	 		 transform(new StreamSource(new File("g:/input.txt"))
	 		 ,new StreamResult(new File("g:/channel.doc")));
	 		 

		} catch (Exception e) {
			
		}

	}
	
	  public static String IndianFormat(BigDecimal n) {
		    DecimalFormat formatter = new DecimalFormat("#,###.00");
		    //we never reach double digit grouping so return
		    if (n.doubleValue() < 100000) {
		        return formatter.format(n.setScale(2, 1).doubleValue());
		    }
		    StringBuffer returnValue = new StringBuffer();
		    //Spliting integer part and decimal part
		    String value = n.setScale(2, 1).toString();
		    String intpart = value.substring(0, value.indexOf("."));
		    String decimalpart = value.substring(value.indexOf("."), value.length());
		    //switch to double digit grouping
		    formatter.applyPattern("#,##");
		    returnValue.append(formatter.format(new BigDecimal(intpart).doubleValue() / 1000)).append(",");
		    //appending last 3 digits and decimal part
		    returnValue.append(intpart.substring(intpart.length() - 3, intpart.length())).append(decimalpart);
		    //returning complete string
		    if(returnValue.toString().equals(".00")){
		    	return "0.00";
		    }
		    
		    return returnValue.toString();
		    
		    
		   
		}
}
