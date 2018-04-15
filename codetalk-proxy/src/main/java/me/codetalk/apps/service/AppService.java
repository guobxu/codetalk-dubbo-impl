package me.codetalk.apps.service;

import java.util.Map;

/**
 * Created by guobxu on 2017/4/1.
 *
 * 业务服务接口
 *
 */
public interface AppService {

	/************************ HTTP ************************/
	
	public String doService(String uri, Map<String, Object> params);
	
}
