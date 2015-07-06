package com.gnts.erputil.constants;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class GERPColorChangeColGenerator implements Table.ColumnGenerator
{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public Object generateCell(Table source, Object itemId, Object columnId) {
		 Property prop = source.getItem(itemId).getItemProperty(columnId);
		 try	        	
     	{
		 
		 if (prop.getType().equals(String.class)) {
	        	
	        
	        	if(prop.getValue() !=null  )
	        	{	      
	        		Object ob = prop.getValue();
	        		Label label = new Label();
//	        		label.setValue(
//	    		            "<font size=\"150\" color="+ob.toString()+"><B>&#9830</B></font>");
	        		label.setValue("<div style=\"width:40px;height:20px;background:"+ob.toString()+";border:1px solid "+ob.toString()+";\"> </div>");
	        		label.setHeight("20px");
	        		label.setContentMode(ContentMode.HTML);
	        		return label;
	        		
	        	}
	        	
	            
	        }
     	}catch(Exception e)
     	{
     		// Do nothing
     	}
		return null;
	}

}