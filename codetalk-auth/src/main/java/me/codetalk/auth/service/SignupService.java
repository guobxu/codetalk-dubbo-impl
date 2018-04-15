package me.codetalk.auth.service;

import me.codetalk.auth.entity.SignupSession;
import me.codetalk.auth.exception.AuthServiceException;

public interface SignupService {

	// 初始化注册会话
	public SignupSession initSession() throws AuthServiceException;
	
	public SignupSession findSessionById(String ssid);
	
	public void deleteSessionById(String ssid);
	
}
