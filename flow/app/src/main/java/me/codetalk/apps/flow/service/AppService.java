package me.codetalk.apps.flow.service;

import java.util.Map;

/**
 * 
 * @author guobxu
 * 
 */
public interface AppService {

	/************************ HTTP ************************/
	
	public String doService(String uri, Map<String, Object> params);
	
}




