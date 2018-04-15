package me.codetalk.auth.service;

import java.util.List;
import java.util.Map;

import me.codetalk.auth.entity.User;

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
	
	public User getUser(Long userId);
	
	public List<User> getUserList(List<Long> userIdList);
	
	public Map<Long, User> getUsersAsMap(Long... userIds);
	
	public Map<Long, User> getUsersAsMap(List<Long> userIdList);
	
}




