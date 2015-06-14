package com.gnts.erputil.components;

import java.util.List;
import com.gnts.base.service.mst.StaticCodesService;
import com.gnts.erputil.constants.GERPConstants;
import com.gnts.erputil.domain.StatusDM;
import com.gnts.erputil.helper.SpringContextHelper;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.ComboBox;

public class GERPComboBox extends ComboBox {
	private static final long serialVersionUID = 1L;
	
	public GERPComboBox(){
		setWidth("150px");	
		setInputPrompt(GERPConstants.selectDefault);
		setNullSelectionAllowed(false);
		setImmediate(true);
	}
	
	public GERPComboBox(String caption){
		setWidth("150px");	
		setCaption(caption);
		setInputPrompt(GERPConstants.selectDefault);
		setNullSelectionAllowed(false);
		setImmediate(true);
	}
	public GERPComboBox(String caption,String tablename,String columnname){
		setWidth("110px");	
		setCaption(caption);
		setInputPrompt(GERPConstants.selectDefault);
		setNullSelectionAllowed(false);
		setImmediate(true);
		StaticCodesService serviceStaticCodes=(StaticCodesService)SpringContextHelper.getBean("staticCodes");
		BeanContainer<String,StatusDM> beanStatus=new BeanContainer<String,StatusDM>(StatusDM.class);
		beanStatus.setBeanIdProperty("code");
		List<StatusDM> statuslist=serviceStaticCodes.getStaticCodesList(tablename, columnname, null, null, null);
		beanStatus.addAll(statuslist);
		setContainerDataSource(beanStatus);
		setItemCaptionPropertyId("desc");
		
	}

}
