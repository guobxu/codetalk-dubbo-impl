package me.codetalk.auth.service;

import java.util.Map;

/**
 * 
 * @author guobxu
 * 
 */
public interface AuthService {

	/************************ HTTP ************************/
	
	public String doService(String uri, Map<String, Object> params);
	
	/************************ RPC  ************************/
	
	public boolean isLoggedIn(Long userId, String accessToken, String authStr);
	
}
