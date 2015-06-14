package com.gnts.erputil.constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.gnts.base.domain.mst.ModuleDM;
import com.gnts.base.service.mst.ModuleControlService;
import com.gnts.erputil.helper.SpringContextHelper;

public class SessionForModule {
	static Map<String, Long> map = new HashMap<String, Long>();
	private static ModuleControlService servicemodulectrl = (ModuleControlService) SpringContextHelper
			.getBean("modulecontrol");
	
	public static Long getModuleId(String key) {
		List<ModuleDM> list = servicemodulectrl.getModuleList();
		for (ModuleDM obj : list) {
			System.out.println("Map Values Code: " + obj.getModuleCode());
			map.put(obj.getModuleCode(), obj.getModuleId());
		}
		System.out.println("Map Values Before: " + map.get(key));
		return map.get(key);
	}
}
