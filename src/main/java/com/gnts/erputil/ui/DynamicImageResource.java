package com.gnts.erputil.ui;

import com.vaadin.server.StreamResource;

public class DynamicImageResource extends StreamResource {
	
	public DynamicImageResource(StreamSource streamSource)
	  {
	   super(streamSource, null);
	    //SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	    String filename = "myfilename-" + ".png";
	    setFilename(filename);
	    setCacheTime(0l);
	 }

}