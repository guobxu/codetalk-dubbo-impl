package me.codetalk.apps.flow.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import me.codetalk.apps.flow.service.AbstractAppService;
import me.codetalk.apps.flow.service.AppService;
import me.codetalk.auth.aop.annotation.EnableThreadSession;

@Service("appService")
public class AppServiceImpl extends AbstractAppService {

	@Autowired
	private ApplicationContext applicationContext;
	
	@EnableThreadSession
	public String doService(String uri, Map<String, Object> params) {
		String service = getServiceName(uri);
		if(service == null) {
			return errorWithKey("sys_uri_notfound");
		}
		
		AppService appService = (AppService)applicationContext.getBean(service);
		
		return appService.doService(uri, params);
	}
	
}







